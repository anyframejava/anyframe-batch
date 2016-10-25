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

import java.util.List;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.agent.model.JobStatus;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public interface IJobDao {

	Job insertJob(Job job, boolean blocked);

	int getRunningJobCount(Job job);

	void updatePerformance(Job job);

	Job getJob(long jobSeq);

	void updateJobAsWaiting(long jobSeq);
	
	void updateJobAsReady(long jobSeq);

	void updateJobAsRunning(long jobSeq);

	void updateJobAsCompleted(long jobSeq);

	void updateJobAsStopped(long jobSeq);

	void updateJobAsFailed(long jobSeq, String error);

	List<Job> getSameJob(String jobId);

	void unBlockJob(long jobSeq);

	JobStatus getLastJobStatus(long jobSeq);
}
