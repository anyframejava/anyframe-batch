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

import com.sds.anyframe.batch.agent.model.Performance;
import com.sds.anyframe.batch.agent.model.Step;
import com.sds.anyframe.batch.agent.model.StepStatus;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class StepDaoImpl implements IStepDao {
	private final Logger log = Logger.getLogger(getClass());

	private JdbcTemplate jdbcTemplate;

	private LobHandler lobHandler;

	private IResourceDao resourceDao;

	public StepDaoImpl() {
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setLobHandler(LobHandler lobHandler) {
		this.lobHandler = lobHandler;
	}


	private static final String INSERT_STEP = "INSERT INTO STEP_EXEC_HISTORY(IP, PID, JOB_ID, JOB_SEQ, STEP_ID, STATUS, CREATE_TIME, LAST_UPDATED, LOG_FILES) "
			+ "VALUES(?, ?, ?, ?, ?, 'READY', ?, ?, ?)";

	private static final String UPDATE_STEP_AS_RUNNING = "UPDATE STEP_EXEC_HISTORY SET STATUS = 'RUNNING', LAST_UPDATED = ? "
			+ "WHERE JOB_SEQ = ? AND STEP_ID = ?";

	private static final String UPDATE_STEP_AS_COMPLETED = "UPDATE STEP_EXEC_HISTORY SET STATUS = 'COMPLETED', LAST_UPDATED = ? "
			+ "WHERE JOB_SEQ = ? AND STEP_ID = ?";

	private static final String UPDATE_STEP_AS_STOPPED = "UPDATE STEP_EXEC_HISTORY SET STATUS = 'STOPPED', LAST_UPDATED = ? "
			+ "WHERE JOB_SEQ = ? AND STEP_ID = ?";

	private static final String UPDATE_STEP_AS_FAILED = "UPDATE STEP_EXEC_HISTORY SET STATUS = 'FAILED', EXIT_MESSAGE = ?, LAST_UPDATED = ? "
			+ "WHERE JOB_SEQ = ? AND STEP_ID = ?";

	private static final String GET_STEP = "SELECT IP, PID, JOB_ID, JOB_SEQ, STEP_ID, STATUS, CREATE_TIME, LAST_UPDATED FROM STEP_EXEC_HISTORY "
			+ "WHERE JOB_SEQ = ? AND STEP_ID = ?";

	private static final String GET_EXECUTE_STEPS = "SELECT * FROM STEP_EXEC_HISTORY WHERE JOB_SEQ = ? AND STATUS IN ('READY', 'RUNNING') ORDER BY CREATE_TIME DESC";


	public void insertStep(Step step) {
		if (step == null)
			return;

		// Check if previous transaction has already done in the case of agent
		// down.
		Step alreayInserted = getStep(step.getJobSeq(), step.getStepId());
		if (alreayInserted != null)
			return;

		jdbcTemplate.update(INSERT_STEP, new Object[] {
				step.getIp(), step.getPid(), step.getJobId(), step.getJobSeq(),
				step.getStepId(), step.getCreatedDate(), step.getLastUpdated(), step.getLogFiles()});
	}


	public Step getStep(long jobSeq, String stepId) {
		Step result = (Step) jdbcTemplate.query(GET_STEP, new Object[] {
				jobSeq, stepId }, new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				if (rs.next()) {
					Step step = new Step();
					step.setPid(rs.getLong("PID"));
					step.setJobId(rs.getString("JOB_ID"));
					step.setJobSeq(rs.getLong("JOB_SEQ"));
					step.setStepId(rs.getString("STEP_ID"));
					step.setStepStatus(StepStatus.valueOf(rs
							.getString("STATUS")));
					step.setResources(resourceDao.getResources(step));
					step.setCreatedDate(rs.getTimestamp("CREATE_TIME"));
					step.setLastUpdated(rs.getTimestamp("LAST_UPDATED"));
					return step;
				}
				return null;
			}
		});
		log.debug("current step : " + result);
		return result;
	}


	public List<Step> getExecuteSteps(long jobSeq) {
		@SuppressWarnings("unchecked")
		List<Step> result = (List<Step>) jdbcTemplate.query(GET_EXECUTE_STEPS,
				new Object[] { jobSeq }, new ResultSetExtractor() {
					public Object extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						List<Step> stepList = new ArrayList<Step>();
						while(rs.next()) {
							Step step = new Step();
							step.setPid(rs.getLong("PID"));
							step.setJobId(rs.getString("JOB_ID"));
							step.setJobSeq(rs.getLong("JOB_SEQ"));
							step.setStepId(rs.getString("STEP_ID"));
							step.setStepStatus(StepStatus.valueOf(rs
									.getString("STATUS")));
							step.setResources(resourceDao.getResources(step));
							step.setCreatedDate(rs.getTimestamp("CREATE_TIME"));
							step
									.setLastUpdated(rs
											.getTimestamp("LAST_UPDATED"));
							step.setExitMessage(rs.getString("EXIT_MESSAGE"));

							stepList.add(step);
						}
						return stepList;
					}
				});
		log.debug("current step : " + result);
		return result;
	}


	public void updateStepAsCompleted(final long jobSeq, final String stepId) {
		updateStep(UPDATE_STEP_AS_COMPLETED, jobSeq, stepId);
	}


	public void updateStepAsFailed(final long jobSeq, final String stepId,
			final String errorMessage) {
		int update = jdbcTemplate.update(UPDATE_STEP_AS_FAILED,
				new PreparedStatementSetter() {

					public void setValues(PreparedStatement ps)
							throws SQLException {
						int index = 1;

						lobHandler.getLobCreator().setClobAsString(ps, index++,
								errorMessage);
						ps.setTimestamp(index++,  new java.sql.Timestamp(System.currentTimeMillis()));
						ps.setLong(index++, jobSeq);
						ps.setString(index++, stepId);
					}
				});
		log.debug("Step updated" + update);
	}


	public void updateStepAsRunning(final long jobSeq, final String stepId) {
		updateStep(UPDATE_STEP_AS_RUNNING, jobSeq, stepId);
	}


	public void updateStepAsStopped(final long jobSeq, final String stepId) {
		updateStep(UPDATE_STEP_AS_STOPPED, jobSeq, stepId);
	}

	private void updateStep(String sql, final long jobSeq, final String stepId) {
		jdbcTemplate.update(sql, new PreparedStatementSetter() {

			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setTimestamp(index++, new java.sql.Timestamp(System.currentTimeMillis()));
				ps.setLong(index++, jobSeq);
				ps.setString(index++, stepId);
			}
		});
	}


	public void updatePerformanceAndTransactedCount(Performance performance,
			Step currentStep) {
		if(performance != null && currentStep != null)
			updatePerformance(performance, currentStep.getJobSeq(), currentStep.getStepId());

		if(currentStep != null)
			resourceDao.updateTransactedCount(currentStep);
	}

	private void updatePerformance(Performance performance, long jobSeq, String stepId) {

		Object[] args = new Object[8];

		StringBuilder sql = new StringBuilder();

		sql
				.append("UPDATE STEP_EXEC_HISTORY SET LAST_UPDATED = ? , ACTIVE_THREAD_COUNT = ?, CURRENT_CPU_USAGE = ?, TOTAL_CPU_USAGE  = ?, FREE_MEMORY  = ?, TOTAL_MEMORY = ? ");
		sql.append("WHERE JOB_SEQ = ? AND STEP_ID = ?");

		args[0] = new Date();
		args[1] = performance.getActiveThreadCount();
		args[2] = performance.getCurrentCpuUsage();
		args[3] = performance.getTotalCpuUsage();
		args[4] = performance.getFreeMemory();
		args[5] = performance.getTotalMemory();
		args[6] = jobSeq;
		args[7] = stepId;

		jdbcTemplate.update(sql.toString(), args);
	}

	public void setResourceDao(IResourceDao resourceDao) {
		this.resourceDao = resourceDao;
	}

	private static final String GET_STEP_STATUS = "SELECT STATUS FROM STEP_EXEC_HISTORY "
		+ "WHERE JOB_SEQ = ? AND STEP_ID = ?";


	public StepStatus getLastStepStatus(long jobSeq, String stepId) {
		StepStatus result = (StepStatus) jdbcTemplate.query(GET_STEP_STATUS, new Object[] {
				jobSeq, stepId }, new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				if (rs.next()) {
					return StepStatus.valueOf(rs
							.getString("STATUS"));
				}
				return null;
			}
		});
		return result;
	}
}
