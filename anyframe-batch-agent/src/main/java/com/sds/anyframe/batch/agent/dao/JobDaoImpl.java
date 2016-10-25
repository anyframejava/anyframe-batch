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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.lob.LobHandler;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.JobStatus;
import com.sds.anyframe.batch.agent.model.Performance;
import com.sds.anyframe.batch.agent.properties.AgentConfigurations;
import com.sds.anyframe.batch.agent.properties.SqlProperties;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobDaoImpl implements IJobDao {
	private static final int _DAYTOMILSECONDS = 24 * 60 * 60 * 1000;

	private final Logger log = Logger.getLogger(getClass());

	private static String GET_JOB_SEQ = "SELECT JOB_EXEC_SEQ.NEXTVAL JOB_SEQ FROM DUAL";

	private static final String JOB_WAITING = "INSERT INTO JOB_EXEC_HISTORY(IP, PID, JOB_ID, JOB_SEQ, STATUS, CREATE_TIME, LAST_UPDATED, LOG_FILES) "
			+ "VALUES (?, ?, ?, ?, 'WAITING', ?, ?, ? )";

	private static final String JOB_BLOCKING = "INSERT INTO JOB_EXEC_HISTORY(IP, PID, JOB_ID, JOB_SEQ, STATUS, CREATE_TIME, LAST_UPDATED, LOG_FILES) "
		+ "VALUES (?, ?, ?, ?, 'BLOCKING', ?, ?, ? )";

	private static final String RUNNING_COUNT_SQL = "SELECT COUNT(0) READY_ORDER FROM JOB_EXEC_HISTORY WHERE IP = ? AND STATUS IN ('RUNNING', 'READY') "
			+ "AND (CREATE_TIME > ? AND CREATE_TIME < ?) ";

	private static final String UPDATE_JOB_AS_WAITING = "UPDATE JOB_EXEC_HISTORY SET STATUS = 'WAITING', LAST_UPDATED = ? "
		+ "WHERE JOB_SEQ = ? ";

	private static final String UPDATE_JOB_AS_RUNNING = "UPDATE JOB_EXEC_HISTORY SET STATUS = 'RUNNING', LAST_UPDATED = ? "
			+ "WHERE JOB_SEQ = ? ";

	private static final String UPDATE_JOB_AS_STOP = "UPDATE JOB_EXEC_HISTORY SET STATUS = 'STOPPED', LAST_UPDATED = ? "
			+ "WHERE JOB_SEQ = ? ";

	private static final String UPDATE_JOB_AS_FAIL = "UPDATE JOB_EXEC_HISTORY SET STATUS = 'FAILED', LAST_UPDATED = ?, EXIT_MESSAGE = ? "
			+ "WHERE JOB_SEQ = ? ";

	private static final String UPDATE_JOB_AS_COMPLETE = "UPDATE JOB_EXEC_HISTORY SET STATUS = 'COMPLETED', LAST_UPDATED = ? "
			+ "WHERE JOB_SEQ = ? ";

	private static final String UPDATE_JOB_AS_READY = "UPDATE JOB_EXEC_HISTORY SET STATUS = 'READY', LAST_UPDATED = ? "
			+ "WHERE JOB_SEQ = ? ";

	private static final String GET_JOB = "SELECT * FROM JOB_EXEC_HISTORY WHERE JOB_SEQ = ?";

	private static final String GET_SAME_JOB = "SELECT * FROM JOB_EXEC_HISTORY WHERE JOB_ID = ? AND STATUS IN ('BLOCKING', 'WAITING', 'READY', 'RUNNING')";

	private static final String GET_JOB_STATUS = "SELECT STATUS FROM JOB_EXEC_HISTORY WHERE JOB_SEQ = ?";

	
	private JdbcTemplate jdbcTemplate;

	private LobHandler lobHandler;

	public JobDaoImpl() {
		if(SqlProperties.getJobSequenceSql() != null) {
			GET_JOB_SEQ = SqlProperties.getJobSequenceSql();
		}
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setLobHandler(LobHandler lobHandler) {
		this.lobHandler = lobHandler;
	}


	public int getRunningJobCount(Job job) {
		Date createTime = job.getCreatedDate();

		long before = createTime.getTime() - (AgentConfigurations.getConfigurations().getRunningMaxJobBefore() * _DAYTOMILSECONDS);

		Date _7daysBefore = new Date(before);

		Object result = jdbcTemplate.query(RUNNING_COUNT_SQL, new Object[] {
				job.getIp(), _7daysBefore, createTime}, new ResultSetExtractor() {

			public Object extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				boolean next = rs.next();
				if (next)
					return rs.getInt("READY_ORDER");
				return -1;
			}
		});
		log.debug("order of this job: " + result);
		return (Integer) result;
	}

	public Job insertJob(Job job, boolean blocking) {

		if(job.getJobSeq() > 0) {
			Job existJob = getJob(job.getJobSeq());
			if (existJob != null)
				return job;
		}

		job.setJobSeq((Long) jdbcTemplate.query(GET_JOB_SEQ,
				new ResultSetExtractor() {
					public Object extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						if (rs.next())
							return rs.getLong("JOB_SEQ");
						return 0;
					}
				}));

		String sql = blocking ? JOB_BLOCKING : JOB_WAITING;

		if(job.getJobId() == null)
			job.setJobId("");

		int insert = jdbcTemplate.update(sql, new Object[] {
				job.getIp(), job.getPid(), job.getJobId(), job.getJobSeq(),
				job.getCreatedDate(), job.getLastUpdated(), job.getLogFiles()});
		log.debug("insert count : " + insert);
		return job;
	}


	public Job getJob(long jobSeq) {
		Job result = (Job) jdbcTemplate.query(GET_JOB,
				new Object[] { jobSeq }, new ResultSetExtractor() {

					public Object extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						if (rs.next()) {
							Job job = new Job();
							job.setIp(rs.getString("IP"));
							job.setPid(rs.getLong("PID"));
							job.setJobId(rs.getString("JOB_ID"));
							job.setJobSeq(rs.getLong("JOB_SEQ"));
							job.setJobStatus(JobStatus.valueOf(rs
									.getString("STATUS")));
							job.setCreatedDate(rs.getTimestamp("CREATE_TIME"));
							job.setLastUpdated(rs.getTimestamp("LAST_UPDATED"));
							job.setExitMessage(rs.getString("EXIT_MESSAGE"));
							return job;
						}
						return null;
					}
				});
		log.debug("select job : " + result);
		return result;
	}


	public void updatePerformance(Job job) {
		if (job == null)
			return;

		Object[] args = new Object[7];

		StringBuilder sql = new StringBuilder();

		sql.append("UPDATE JOB_EXEC_HISTORY SET LAST_UPDATED = ? , ACTIVE_THREAD_COUNT = ?, CURRENT_CPU_USAGE = ?, TOTAL_CPU_USAGE  = ?, FREE_MEMORY  = ?, TOTAL_MEMORY = ? ");
		sql.append("WHERE JOB_SEQ = ?");

		Performance performance = job.getPerformance();

		args[0] = new Date();
		args[1] = performance.getActiveThreadCount();
		args[2] = performance.getCurrentCpuUsage();
		args[3] = performance.getTotalCpuUsage();
		args[4] = performance.getFreeMemory();
		args[5] = performance.getTotalMemory();
		args[6] = job.getJobSeq();

		jdbcTemplate.update(sql.toString(), args);
	}


	public void updateJobAsWaiting(long jobSeq) {
		updateJob(UPDATE_JOB_AS_WAITING, jobSeq);
	}


	public void updateJobAsReady(long jobSeq) {
		updateJob(UPDATE_JOB_AS_READY, jobSeq);
	}


	public void updateJobAsRunning(long jobSeq) {
		updateJob(UPDATE_JOB_AS_RUNNING, jobSeq);
	}


	public void updateJobAsCompleted(long jobSeq) {
		updateJob(UPDATE_JOB_AS_COMPLETE, jobSeq);
	}


	public void updateJobAsStopped(long jobSeq) {
		updateJob(UPDATE_JOB_AS_STOP, jobSeq);
	}


	public void updateJobAsFailed(final long jobSeq, final String error) {
		jdbcTemplate.update(UPDATE_JOB_AS_FAIL, new PreparedStatementSetter() {

			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setTimestamp(index++,  new java.sql.Timestamp(System.currentTimeMillis()));
				lobHandler.getLobCreator().setClobAsString(ps,
						index++, error);
				ps.setLong(index++, jobSeq);
			}
		});

	}

	private void updateJob(String sql, final long jobSeq) {
		jdbcTemplate.update(sql, new PreparedStatementSetter() {

			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setTimestamp(index++,  new java.sql.Timestamp(System.currentTimeMillis()));
				ps.setLong(index++, jobSeq);
			}
		});
	}


		@SuppressWarnings("unchecked")
		public List<Job> getSameJob(String jobId) {
		List<Job> jobs = (List<Job>) jdbcTemplate.query(GET_SAME_JOB,
				new Object[] {jobId}, new ResultSetExtractor() {

					public Object extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						List<Job> jobs = new ArrayList<Job>();

						while(rs.next()) {
							Job job = new Job();
							job.setIp(rs.getString("IP"));
							job.setPid(rs.getLong("PID"));
							job.setJobId(rs.getString("JOB_ID"));
							job.setJobSeq(rs.getLong("JOB_SEQ"));
							job.setJobStatus(JobStatus.valueOf(rs
									.getString("STATUS")));
							job.setCreatedDate(rs.getTimestamp("CREATE_TIME"));
							job.setLastUpdated(rs.getTimestamp("LAST_UPDATED"));
							job.setExitMessage(rs.getString("EXIT_MESSAGE"));
							jobs.add(job);
						}
						return jobs;
					}
				});
		log.debug("The same jobs: " + jobs);
		return jobs;
	}


	public void unBlockJob(long jobSeq) {
		updateJobAsWaiting(jobSeq);
	}

	public JobStatus getLastJobStatus(long jobSeq) {
		JobStatus result = (JobStatus) jdbcTemplate.query(GET_JOB_STATUS, new Object[] {
				jobSeq}, new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				if (rs.next()) {
					return JobStatus.valueOf(rs
							.getString("STATUS"));
				}
				return null;
			}
		});
		return result;
	}
}
