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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class JobEditHandler implements EditorSupport{
	
	public void saveAsTemp(String filePath, List<String> editedStringList) throws Exception{
		File tempFile = new File(filePath);
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(tempFile)));
		try {
			for (String str : editedStringList) {
				out.println(str);
			}
			out.flush();

		} finally {
			out.close();
		}
	}
	
	
	public boolean isExist(String filePath){
		return new File(filePath).exists();
	}
}
