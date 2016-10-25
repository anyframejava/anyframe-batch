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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.sds.anyframe.batch.agent.model.Resource;
import com.sds.anyframe.batch.agent.model.ResourceIoType;
import com.sds.anyframe.batch.agent.model.ResourceStatus;
import com.sds.anyframe.batch.agent.model.ResourceType;
import com.sds.anyframe.batch.agent.model.Step;
import com.sds.anyframe.batch.agent.util.AgentUtils;
import com.sds.anyframe.batch.util.AddableLong;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class ResourceDaoImpl implements IResourceDao {
	private static Logger log = Logger.getLogger(ResourceDaoImpl.class);
	
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private static final String GET_PRELOCKED_RESOURCES = "SELECT * FROM RESOURCE_MANAGEMENT WHERE JOB_SEQ <> ? AND RESOURCE_NAME = ? AND STATUS = ? ";
	
	
	public List<Resource> getPreLockedResources(String resourceName, long jobSeq) {
		List<Resource> result = simpleJdbcTemplate.query(GET_PRELOCKED_RESOURCES,
				new ParameterizedRowMapper<Resource>() {
					
					public Resource mapRow(ResultSet rs, int rowNum) throws SQLException {
						Resource resource = new Resource();
						resource.setResourceName(rs.getString("RESOURCE_NAME"));
						resource.setJobId(rs.getString("JOB_ID"));
						resource.setJobSeq(rs.getLong("JOB_SEQ"));
						resource.setStepId(rs.getString("STEP_ID"));
						String type = rs.getString("TYPE");
						if(type == null)
							type = ResourceType.FILE.name();
						resource.setType(ResourceType.valueOf(type));
						resource.setIoType(ResourceIoType.valueOf(rs.getString("IO_TYPE")));
						
						resource.setStatus(ResourceStatus.getStatus(rs.getString("STATUS")));
						resource.setCreateTime(rs.getTimestamp("CREATE_TIME"));
						resource.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));
						return resource;
					}
				}, new Object[] { jobSeq, resourceName, ResourceStatus.LOCKED.name()});
		
		result = result == null ? new ArrayList<Resource>() : result;
		return result;
	}

	private static final String INSERT_RESOURCE = "INSERT INTO RESOURCE_MANAGEMENT(RESOURCE_NAME, JOB_ID, JOB_SEQ, STEP_ID, IO_TYPE, STATUS, CREATE_TIME, UPDATE_TIME, TYPE) "
		+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	
	public int[] insertResources(final Step step) {
		List<Resource> resources = step.getResources();
		if(resources == null || resources.size() == 0)
			return null;
		
		List<Object[]> batchArgs = new ArrayList<Object[]>();

		for (Resource resource : resources) {
			ResourceIoType resourceIoType = resource.getIoType();
			String resourceName = resource.getResourceName();
			log.debug("insert - resource name : " + resourceName
					+ " job execution id : " + step.getJobExecutionId()
					+ " step id : " + step.getStepId());

			Resource alreadyInserted = getResource(step.getJobSeq(), step
					.getStepId(), resourceIoType.name(), resourceName);
			if (alreadyInserted != null)
				continue;

			Object[] args = new Object[] { resourceName, step.getJobId(),
					step.getJobSeq(), step.getStepId(), resourceIoType.name(),
					ResourceStatus.WAITING.name(), step.getCreatedDate(),
					step.getLastUpdated(), resource.getType().toString() };
			batchArgs.add(args);
		}
		
		if(batchArgs.size() == 0)
			return null;
		
		int[] values = simpleJdbcTemplate.batchUpdate(INSERT_RESOURCE, batchArgs);
		if(values != null)
			log.debug("insertResources() : " + values.length + ", batch updated values : " + Arrays.toString(values));
		
		return values;
	}

	
	public int[] updateResourcesAsLocked(Step step) {
		int[] values = updateResources(ResourceStatus.LOCKED, step);
		if(values != null)
			log.info("Resources locked : " + AgentUtils.toStepString(step) + ", batch updated values : " + values.length);
		return values;
	}
	
	
	public int[] updateResourcesAsReleased(Step step) {
		int[] values = updateResources(ResourceStatus.RELEASED, step);
		if(values != null)
			log.info("Resources released : "  + AgentUtils.toStepString(step) + ", batch updated values : " +values.length);
		return values;
	}
	
	private static final String UPDATE_RESOURCE = "UPDATE RESOURCE_MANAGEMENT SET STATUS = ?, UPDATE_TIME = ? "
		+ "WHERE JOB_SEQ = ? AND STEP_ID = ? AND IO_TYPE = ? AND RESOURCE_NAME = ? ";
	
	private int[] updateResources(ResourceStatus status, Step step) {
		List<Resource> resources = step.getResources();
		if(resources == null || resources.size() == 0)
			return null;
		
		List<Object[]> batchArgs = new ArrayList<Object[]>();
		
		for (Resource resource : resources) {
			batchArgs.add(getArguments(status, step, resource));
		}
		
		if(batchArgs.size() == 0)
			return null;
		
		return simpleJdbcTemplate.batchUpdate(UPDATE_RESOURCE, batchArgs);
	}
	
	private Object[] getArguments(ResourceStatus status, Step step, Resource resource) {
		
			ResourceIoType resourceIoType = resource.getIoType();

			String resourceName = resource.getResourceName();

			log.debug("updateResources() - resource name : " + resourceName
					+ " job execution id : " + step.getJobExecutionId()
					+ " step id : " + step.getStepId() + " resource status : "
					+ status.name());

			return new Object[] { status.name(), new Date(),
					step.getJobSeq(), step.getStepId(), resourceIoType.name(),
					resourceName };

	}
	
	private static final String GET_RESOURCE = "SELECT * FROM RESOURCE_MANAGEMENT "
											  + "WHERE JOB_SEQ = ? AND STEP_ID = ? AND IO_TYPE = ? AND RESOURCE_NAME = ?";
	
	
	private Resource getResource(long jobSeq, String stepId,
			String resourceType, String resourceName) {
		try {
			Resource resource = simpleJdbcTemplate.queryForObject(GET_RESOURCE,
					new ParameterizedRowMapper<Resource>() {
						public Resource mapRow(ResultSet rs, int rowNum)
								throws SQLException {
								Resource resource = new Resource();
								resource.setResourceName(rs
										.getString("RESOURCE_NAME"));
								resource.setJobId(rs.getString("JOB_ID"));
								resource.setJobSeq(rs.getLong("JOB_SEQ"));
								resource.setStepId(rs.getString("STEP_ID"));
								resource.setIoType(ResourceIoType.valueOf(rs
										.getString("IO_TYPE")));
								String type = rs.getString("TYPE");
								if(type == null)
									type = ResourceType.FILE.name();
								resource.setType(ResourceType.valueOf(type));
								resource.setStatus(ResourceStatus.getStatus(rs.getString("STATUS")));
								resource.setCreateTime(rs
										.getTimestamp("CREATE_TIME"));
								resource.setUpdateTime(rs
										.getTimestamp("UPDATE_TIME"));
								return resource;
						}
					}, new Object[] { jobSeq, stepId, resourceType,
							resourceName });
			return resource;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final String GET_OWNER_OF_LOCKED_RESOURCE = "SELECT * FROM RESOURCE_MANAGEMENT "
		  + "WHERE STATUS = '" + ResourceStatus.LOCKED.name() + "' AND RESOURCE_NAME = ?";
	
	
	public Resource getOwnerOfLockedResource(String resourceName) {
		return (Resource) jdbcTemplate.query(
				GET_OWNER_OF_LOCKED_RESOURCE, new Object[] { resourceName },
				new ResultSetExtractor() {
					
					public Object extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						if (rs.next()) {
							Resource resource = new Resource();
							resource.setResourceName(rs
									.getString("RESOURCE_NAME"));
							resource.setJobId(rs.getString("JOB_ID"));
							resource.setJobSeq(rs.getLong("JOB_SEQ"));
							resource.setStepId(rs.getString("STEP_ID"));
							resource.setIoType(ResourceIoType.valueOf(rs
									.getString("IO_TYPE")));
							resource.setStatus(ResourceStatus.getStatus(rs.getString("STATUS")));
							resource.setCreateTime(rs
									.getTimestamp("CREATE_TIME"));
							resource.setUpdateTime(rs
									.getTimestamp("UPDATE_TIME"));
							return resource;
						}
						return null;
					}
				});
	}
	
	private static final String SELECT_STEP_RESOURCES = "SELECT * FROM RESOURCE_MANAGEMENT WHERE JOB_SEQ = ? AND STEP_ID = ?";

	
	public List<Resource> getResources(Step step) {

		final List<Resource> resources = new ArrayList<Resource>();

		jdbcTemplate.query(SELECT_STEP_RESOURCES, new Object[] {
				step.getJobSeq(), step.getStepId() }, new RowMapper() {

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Resource resource = new Resource();
				resource.setResourceName(rs.getString("RESOURCE_NAME"));
				resource.setIoType(ResourceIoType.valueOf(rs.getString("IO_TYPE")));
				String type = rs.getString("TYPE");
				if(type == null)
					type = ResourceType.FILE.name();
				resource.setType(ResourceType.valueOf(type));
				resource.setTransactedCount(new AddableLong(rs.getLong("TRANSACTED_COUNT")));
				resource.setStatus(ResourceStatus.getStatus(rs.getString("STATUS")));
				resource.setCreateTime(rs.getTimestamp("CREATE_TIME"));
				resource.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));
				resources.add(resource);
				return null;
			}
		});

		return resources;
	}
	
	private static final String UPDATE_TRANSACTION = "UPDATE RESOURCE_MANAGEMENT SET UPDATE_TIME = ?, TRANSACTED_COUNT = ? "
		+ "WHERE JOB_SEQ = ? AND STEP_ID = ? AND IO_TYPE = ? AND RESOURCE_NAME = ? ";
	
	
	public void updateTransactedCount(Step currentStep) {
		List<Resource> resources = currentStep.getResources();
		
		List<Object[]> values = new ArrayList<Object[]> ();
		
		for(Resource resource: resources) {
			Object[] args = new Object[6];

			int i = 0;
			args[i++] = new Date();
			args[i++] = resource.getTransactedCount().get();
			args[i++] = currentStep.getJobSeq();
			args[i++] = currentStep.getStepId();
			args[i++] = resource.getIoType().name();
			args[i++] = resource.getResourceName();
			
			values.add(args);
		}
		
		if(values.size() == 0)
			return;
		
		int[] batchUpdate = simpleJdbcTemplate.batchUpdate(UPDATE_TRANSACTION, values);
		
		if(batchUpdate != null)
			log.debug("updateTransactedCount() : " + batchUpdate.length + ", resource updated values : " + Arrays.toString(batchUpdate));
		
	}
}
