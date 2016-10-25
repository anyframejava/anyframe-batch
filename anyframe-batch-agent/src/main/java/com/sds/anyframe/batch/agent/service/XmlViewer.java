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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class XmlViewer extends Page {
	private static final Logger logger = Logger.getLogger(XmlViewer.class);

	@Override
	public PageRequest getPage(PageRequest request) throws Exception {

		File file = new File((String) request.getParameter());
		if (!file.exists()) {
			logger.error(request.getParameter() + " should exist ");
			throw new Exception(request.getParameter() + " should exist ");
		}

		long length = file.length();

		InputStream inputStream = new FileInputStream(file);

		byte[] buffer = new byte[(int) length];

		try {
			int read = inputStream.read(buffer);

			if (read > 0)
				request.setResult(buffer);
			
			return request;
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}
}
