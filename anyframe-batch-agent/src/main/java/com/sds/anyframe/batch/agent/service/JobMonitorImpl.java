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
package com.sds.anyframe.batch.agent.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.dao.IJobMonitorDao;
import com.sds.anyframe.batch.agent.dao.IResourceDao;
import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.JobFilter;
import com.sds.anyframe.batch.agent.model.JobStatus;
import com.sds.anyframe.batch.agent.model.Resource;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobMonitorImpl implements IJobMonitor {
	private final Logger log = Logger.getLogger(getClass());

	private IJobMonitorDao jobMonitorDao;
	
	private IResourceDao resourceDao;
	
	public JobMonitorImpl() {
	}
	
	
	public List<Job> listJob(String ip, Date startDate, Date endDate,
			JobStatus status, String jobId, PageRequest pageRequest)
			throws Exception {
		try {
			JobFilter jobFilter = new JobFilter(ip, startDate, endDate, status,
					jobId);

			List<Job> jobs = jobMonitorDao.listJob(jobFilter, pageRequest);

			return jobs;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	
	public List<Job> getStepsInSelectedJob(Job job) {
		return jobMonitorDao.getSteps(job);
	}

	
	public Job getDetailJob(Job job) {
		return jobMonitorDao.getDetailJob(job);
	}

	
	public Resource getOwnerOfLockedResource(String resourceName) {
		return resourceDao.getOwnerOfLockedResource(resourceName);
	}

	public void setResourceDao(IResourceDao resourceDao) {
		this.resourceDao = resourceDao;
	}

	public void setJobMonitorDao(IJobMonitorDao jobMonitorDao) {
		this.jobMonitorDao = jobMonitorDao;
	}
}
