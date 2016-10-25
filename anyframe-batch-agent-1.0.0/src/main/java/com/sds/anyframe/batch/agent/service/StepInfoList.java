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

import com.sds.anyframe.batch.agent.util.XMLUtil;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class StepInfoList extends Page {

	private final String BATCH_STEP_NODE = "/*/*/step";

	@Override
	public PageRequest getPage(PageRequest request) throws Exception {
		String xmlFileName = (String) request.get("xmlFileName");
		XMLUtil xml = new XMLUtil();
		request.setResult(xml.getStepList(xmlFileName, BATCH_STEP_NODE));
		
		return request;
	}

}
