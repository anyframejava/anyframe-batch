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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.sds.anyframe.batch.agent.PropertyProvider;
import com.sds.anyframe.batch.agent.model.FileInfoVO;
import com.sds.anyframe.batch.agent.util.AgentUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class LogFileList implements FileList {
	
	private final static String BASE_LOG_DIR;
	
	static {
		String getenv = PropertyProvider.runtimeLogPath;
		if (getenv == null)
			BASE_LOG_DIR = "logs/";
		else
			BASE_LOG_DIR = getenv.endsWith("/") ? getenv : getenv + "/";
	}

	public String getBaseDir() {
		return BASE_LOG_DIR;
	}

	public List<FileInfoVO> getList(String jobPath, final String start,
			final String end) {
		String tmpPath = StringUtils.substringBetween(jobPath,
				PropertyProvider.buildPath + AgentUtils.SEPARATOR, ".xml");
		final String nameIndex = StringUtils.substringAfterLast(tmpPath, "/");
		final String path = StringUtils.substringBeforeLast(tmpPath, "/");
		final long startDate = Long.valueOf(start);
		final long endDate = Long.valueOf(end);
		File folder = new File(BASE_LOG_DIR + path);
		File[] searchFiles = getSearchFiles(nameIndex, startDate, endDate,
				folder);
		return listFiles(path, searchFiles);
	}

	private File[] getSearchFiles(final String nameIndex, final long startDate,
			final long endDate, File folder) {
		File[] searchFiles = folder.listFiles(new FileFilter() {
			public boolean accept(File file) {
				if (file.isDirectory())
					return false;
				long modifiedDate = Long.parseLong(new SimpleDateFormat(
						"yyyyMMdd").format(new Date(file.lastModified())));
				if (StringUtils.contains(file.getName(), nameIndex)
						&& modifiedDate >= startDate && modifiedDate <= endDate) {
					return true;
				} else {
					return false;
				}
			}
		});
		return searchFiles;
	}

	public List<FileInfoVO> getSearchList(String jobPath, String fileName,
			final String start, final String end) throws FileNotFoundException {
		File dir = new File(BASE_LOG_DIR + jobPath);
		if (!dir.isDirectory()) {
			throw new FileNotFoundException("This directory is not exist. : "
					+ BASE_LOG_DIR + jobPath);
		}
		final long startDate = Long.valueOf(start);
		final long endDate = Long.valueOf(end);
		File[] files = getSearchFiles(fileName, startDate, endDate, dir);

		String path = StringUtils.substringAfter(dir.getPath(), BASE_LOG_DIR);
		return listFiles(path, files);
	}

	private List<FileInfoVO> listFiles(final String path, File[] searchFiles) {
		List<FileInfoVO> list = new ArrayList<FileInfoVO>();
		if (searchFiles != null && searchFiles.length != 0) {
			Arrays.sort(searchFiles);
			Collections.reverse(Arrays.asList(searchFiles));
			for (int i = 0; i < searchFiles.length; i++) {
				FileInfoVO vo = new FileInfoVO();
				vo.setPath(path);
				vo.setName(searchFiles[i].getName());
				vo.setSize(searchFiles[i].length());
				vo.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date(searchFiles[i].lastModified())));
				list.add(vo);
			}
		}
		return list;
	}

	@Override
	public List<FileInfoVO> getList2(List<String> files) {
		// TODO Auto-generated method stub
		return null;
	}

}
