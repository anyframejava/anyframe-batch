/*
 * Copyright 2010-2012 Samsung SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sds.anyframe.batch.agent.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.JobFilter;
import com.sds.anyframe.batch.agent.model.JobStatus;
import com.sds.anyframe.batch.agent.model.Performance;
import com.sds.anyframe.batch.agent.model.Resource;
import com.sds.anyframe.batch.agent.model.ResourceIoType;
import com.sds.anyframe.batch.agent.model.ResourceStatus;
import com.sds.anyframe.batch.agent.model.ResourceType;
import com.sds.anyframe.batch.agent.model.StepStatus;
import com.sds.anyframe.batch.agent.service.PageRequest;
import com.sds.anyframe.batch.util.AddableLong;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobMonitorDaoImpl implements IJobMonitorDao {

	private final Logger log = Logger.getLogger(getClass());

	private JdbcTemplate jdbcTemplate;
	
	private static final String SELECT_JOB_SQL = "SELECT * FROM JOB_EXEC_HISTORY WHERE JOB_SEQ = ?";

	private static final String GET_STEPS_IN_JOB_SQL = " SELECT   STEP_EXEC_HISTORY.STEP_ID      , "
			+ "         STEP_EXEC_HISTORY.STATUS STEP_STATUS , "
			+ "         STEP_EXEC_HISTORY.JOB_SEQ , "
			+ "         STEP_EXEC_HISTORY.CREATE_TIME  , "
			+ "         STEP_EXEC_HISTORY.LAST_UPDATED , "
			+ "         STEP_EXEC_HISTORY.LOG_FILES ," 
			+ "         STEP_EXEC_HISTORY.ACTIVE_THREAD_COUNT        , "
			+ "         STEP_EXEC_HISTORY.CURRENT_CPU_USAGE        , "
			+ "         STEP_EXEC_HISTORY.TOTAL_CPU_USAGE        , "
			+ "         STEP_EXEC_HISTORY.FREE_MEMORY        , "
			+ "         STEP_EXEC_HISTORY.TOTAL_MEMORY       , " 
			+ "         RESOURCE_MANAGEMENT.RESOURCE_NAME, "
			+ "         RESOURCE_MANAGEMENT.STATUS RESOURCE_STATUS, "
			+ "         RESOURCE_MANAGEMENT.TYPE RESOURCE_TYPE, "
			+ "         RESOURCE_MANAGEMENT.IO_TYPE RESOURCE_IO_TYPE, "
			+ "         RESOURCE_MANAGEMENT.UPDATE_TIME, "
			+ "         RESOURCE_MANAGEMENT.TRANSACTED_COUNT "
			+ " FROM     "
			+ " 	    STEP_EXEC_HISTORY LEFT OUTER JOIN "
			+ " 		RESOURCE_MANAGEMENT ON STEP_EXEC_HISTORY.JOB_SEQ = RESOURCE_MANAGEMENT.JOB_SEQ "
			+ " AND		STEP_EXEC_HISTORY.STEP_ID = RESOURCE_MANAGEMENT.STEP_ID "
			+ " WHERE    STEP_EXEC_HISTORY.JOB_SEQ  = ? "
			+ " ORDER BY STEP_EXEC_HISTORY.CREATE_TIME DESC, STEP_EXEC_HISTORY.STEP_ID";

	private static final String LIST_JOB = "\n SELECT IP              , "
			+ "\n        PID           , " 
			+ "\n        JOB_ID           , "
			+ "\n        JOB_SEQ          , " 
			+ "\n        STATUS       , "
			+ "\n        CREATE_TIME      , " 
			+ "\n        LAST_UPDATED     , "
			+ "\n        EXIT_MESSAGE     , " 
			+ "\n        LOG_FILES        , "
			+ "\n        ACTIVE_THREAD_COUNT        , "
			+ "\n        CURRENT_CPU_USAGE        , "
			+ "\n        TOTAL_CPU_USAGE        , "
			+ "\n        FREE_MEMORY        , "
			+ "\n        TOTAL_MEMORY        " 
			+ "\n FROM  JOB_EXEC_HISTORY"
			+ "\n WHERE ";

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	
	public List<Job> listJob(JobFilter filter, PageRequest pageRequest) {
		String selectedStatus = filter.getStatus() == null ? "%" : "%"
				+ filter.getStatus().name() + "%";
		String selectedJobId = filter.getJobId() == null
				|| filter.getJobId().equals("") ? "%" : "%" + filter.getJobId()
				+ "%";
		final int pageSize = pageRequest.getPageSize();
		final int pageIndex = pageRequest.getPageNo();
		final List<Job> jobs = new ArrayList<Job>(pageSize);
		
		long time = filter.getEndDate().getTime() + (23*60*60*1000) + (59*60*1000) + (59*1000);
		
		Date endDate = new Date(time);
		
		jdbcTemplate
				.query(
						LIST_JOB
								+ "\n STATUS  LIKE ? "
								+ "\n AND    JOB_ID  LIKE ? "
								+ "\n AND    CREATE_TIME BETWEEN ? AND ? ORDER BY JOB_SEQ DESC",
						new Object[] { selectedStatus, selectedJobId,
								filter.getStartDate(), endDate },
						new ResultSetExtractor() {
							
							public Object extractData(ResultSet rs)
									throws SQLException, DataAccessException {
								extractJob(rs, jobs, pageSize, pageIndex);
								return null;
							}
						});

		return jobs;
	}

	private void extractJob(ResultSet rs, List<Job> list, int pageIndex,
			int pageSize) throws SQLException {

		Job job = null;
		boolean newJob = true;
		List<Resource> resources = null;
		int startRow = (pageIndex - 1) * pageSize + 1;
		int rowNum = 1;
		int count = 0;
		long jobSeq = -1;

		while (rs.next()) {

			if (jobSeq > 0 && jobSeq != rs.getLong("JOB_SEQ"))
				rowNum++;

			jobSeq = rs.getLong("JOB_SEQ");

			if (rowNum < startRow)
				continue;

			if (job != null && job.getJobSeq() != rs.getLong("JOB_SEQ"))
				newJob = true;

			if (newJob) {
				if (count < pageSize) {
					job = getJobFromResultSet(rs);

					list.add(job);

					resources = new ArrayList<Resource>();
					job.setResources(resources);
					count++;
					newJob = false;
				} else
					return;
			}

		}
	}

	private Job getJobFromResultSet(ResultSet rs) throws SQLException {
		Job job;
		job = new Job();
		job.setIp(rs.getString("IP"));
		job.setPid(rs.getLong("PID"));
		job.setJobId(rs.getString("JOB_ID"));
		job.setJobSeq(rs.getLong("JOB_SEQ"));
		job.setJobStatus(JobStatus.valueOf(rs.getString("STATUS")));
		job.setCreatedDate(rs.getTimestamp("CREATE_TIME"));
		job.setLastUpdated(rs.getTimestamp("LAST_UPDATED"));
		job.setExitMessage(rs.getString("EXIT_MESSAGE"));
		job.setLogFiles(rs.getString("LOG_FILES"));

		Performance performance = new Performance();

		performance.setActiveThreadCount(rs.getInt("ACTIVE_THREAD_COUNT"));
		performance.setCurrentCpuUsage(rs.getDouble("CURRENT_CPU_USAGE"));
		performance.setTotalCpuUsage(rs.getDouble("TOTAL_CPU_USAGE"));
		performance.setFreeMemory(rs.getLong("FREE_MEMORY"));
		performance.setTotalMemory(rs.getLong("TOTAL_MEMORY"));

		job.setPerformance(performance);

		return job;
	}

	private void fillResourceMapFromResultSet(
			List<Resource> resources, ResultSet rs)
			throws SQLException {
		Resource resource = new Resource();
		
		resource.setIoType(ResourceIoType.valueOf(rs
				.getString("RESOURCE_IO_TYPE")));
		resource.setResourceName(rs.getString("RESOURCE_NAME"));
		resource.setStatus(ResourceStatus.getStatus(rs.getString("RESOURCE_STATUS")));
		String type = rs.getString("RESOURCE_TYPE");
		if(type == null)
			type = ResourceType.FILE.name();
		resource.setType(ResourceType.valueOf(type));
		resource.setTransactedCount(new AddableLong(rs.getLong("TRANSACTED_COUNT")));
		resource.setUpdateTime(rs
				.getTimestamp("UPDATE_TIME"));
		
		resources.add(resource);
		
	}

	public List<Job> getSteps(final Job job) {
		final List<Job> jobs = new ArrayList<Job>();

		jdbcTemplate.query(GET_STEPS_IN_JOB_SQL,
				new Object[] { job.getJobSeq() },
				// , job.getCurrentStepId()
				new ResultSetExtractor() {
					
					public Object extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						extractJobForGetSteps(rs, jobs, job);
						return null;
					}
				});
		return jobs;
	}

	private void extractJobForGetSteps(ResultSet rs, List<Job> list, Job parent)
			throws SQLException {
		Job job = null;
		boolean newJob = true;
		List<Resource> resources = null;

		while (rs.next()) {
			if (job != null
					&& !job.getCurrentStepId().equals(rs.getString("STEP_ID")))
				newJob = true;

			if (newJob) {
				job = new Job();
				job.setParent(parent);
				job.setJobSeq(rs.getLong("JOB_SEQ"));
				job.setCreatedDate(rs.getTimestamp("CREATE_TIME"));
				job.setLastUpdated(rs.getTimestamp("LAST_UPDATED"));
				job.setCurrentStepId(rs.getString("STEP_ID"));
				job.setLogFiles(rs.getString("LOG_FILES"));
				
				Performance performance = new Performance();
				
				performance.setActiveThreadCount(rs
						.getInt("ACTIVE_THREAD_COUNT"));
				performance.setCurrentCpuUsage(rs
						.getDouble("CURRENT_CPU_USAGE"));
				performance.setTotalCpuUsage(rs
						.getDouble("TOTAL_CPU_USAGE"));
				performance
						.setFreeMemory(rs.getLong("FREE_MEMORY"));
				performance.setTotalMemory(rs
						.getLong("TOTAL_MEMORY"));

				job.setPerformance(performance);
				
				String stepStatus = rs.getString("STEP_STATUS");
				if (stepStatus != null && !stepStatus.equals(""))
					job.setStepStatus(StepStatus.valueOf(stepStatus));

				list.add(job);

				resources = new ArrayList<Resource>();
				job.setResources(resources);

				newJob = false;
			}
			if (rs.getString("RESOURCE_IO_TYPE") != null
					&& !rs.getString("RESOURCE_IO_TYPE").equals(""))
				fillResourceMapFromResultSet(resources, rs);
		}
	}

	private Job getJob(Job job) {
		Job result = (Job) jdbcTemplate.query(SELECT_JOB_SQL,
				new Object[] { job.getJobSeq() }, new ResultSetExtractor() {
					
					public Object extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						if (rs.next()) {
							Job job = new Job();
							Performance performance = new Performance();
							job.setIp(rs.getString("IP"));
							job.setPid(rs.getLong("PID"));
							job.setJobId(rs.getString("JOB_ID"));
							job.setJobSeq(rs.getLong("JOB_SEQ"));
							job.setJobStatus(JobStatus.valueOf(rs
									.getString("STATUS")));
							job.setCreatedDate(rs.getTimestamp("CREATE_TIME"));
							job.setLastUpdated(rs.getTimestamp("LAST_UPDATED"));
							job.setExitMessage(rs.getString("EXIT_MESSAGE"));
							job.setLogFiles(rs.getString("LOG_FILES"));

							performance.setActiveThreadCount(rs
									.getInt("ACTIVE_THREAD_COUNT"));
							performance.setCurrentCpuUsage(rs
									.getDouble("CURRENT_CPU_USAGE"));
							performance.setTotalCpuUsage(rs
									.getDouble("TOTAL_CPU_USAGE"));
							performance
									.setFreeMemory(rs.getLong("FREE_MEMORY"));
							performance.setTotalMemory(rs
									.getLong("TOTAL_MEMORY"));

							job.setPerformance(performance);

							return job;
						}
						return null;
					}
				});
		log.debug("get job : " + result);
		return result;
	}

	
	public Job getDetailJob(Job job) {

		job = getJob(job);

		job.setChildren(getSteps(job));

		return job;
	}
}
