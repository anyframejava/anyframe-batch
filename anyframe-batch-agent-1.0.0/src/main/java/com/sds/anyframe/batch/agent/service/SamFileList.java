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

import com.sds.anyframe.batch.agent.model.FileInfoVO;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class SamFileList implements FileList {

	public SamFileList() {

	}

	private File[] getSearchFiles(final String fileName, final long startDate,
			final long endDate, File folder) {
		File[] searchFiles = folder.listFiles(new FileFilter() {
			public boolean accept(File file) {
				long modifiedDate = Long.parseLong(new SimpleDateFormat(
						"yyyyMMdd").format(new Date(file.lastModified())));
				if (StringUtils.contains(file.getName(), fileName)
						&& modifiedDate >= startDate && modifiedDate <= endDate) {
					return true;
				} else {
					return false;
				}
			}
		});
		return searchFiles;
	}

	private List<FileInfoVO> listFiles(String path, File[] searchFiles) {
		List<FileInfoVO> list = new ArrayList<FileInfoVO>();
		if (searchFiles != null && searchFiles.length != 0) {
			Arrays.sort(searchFiles);
			Collections.reverse(Arrays.asList(searchFiles));
			for (int i = 0; i < searchFiles.length; i++) {
				FileInfoVO vo = getFileInfo(path, searchFiles[i]);
				list.add(vo);
			}
		}
		return list;
	}

	private FileInfoVO getFileInfo(String path, File searchFiles) {
		
		FileInfoVO vo = new FileInfoVO();
		vo.setPath(path);
		vo.setName(searchFiles.getName());
		vo.setFullPathName(searchFiles.getAbsolutePath());
		if (searchFiles.exists()) {
			vo.setSize(searchFiles.length());
			vo.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date(searchFiles.lastModified())));
			return vo;
		}
		return vo;
	}

	
	public List<FileInfoVO> getSearchList(String jobPath, String fileName,
			final String start, final String end) throws FileNotFoundException {
		File dir = new File(jobPath);
		if (!dir.isDirectory()) {
			throw new FileNotFoundException("This directory is not exist. : "
					+ jobPath);
		}
		final long startDate = Long.valueOf(start);
		final long endDate = Long.valueOf(end);
		File[] files = getSearchFiles(fileName, startDate, endDate, dir);

		String path = dir.getAbsolutePath();
		return listFiles(path, files);
	}

	
	public List<FileInfoVO> getList(String jobPath, final String start,
			final String end) {
		final String fileName = StringUtils.substringAfterLast(jobPath, "/");
		final String path = StringUtils.substringBeforeLast(jobPath, "/");
		final long startDate = Long.valueOf(start);
		final long endDate = Long.valueOf(end);
		File folder = new File(path);
		File[] searchFiles = getSearchFiles(fileName, startDate, endDate,
				folder);
		return listFiles(path, searchFiles);
	}

	
	public List<FileInfoVO> getList2(List<String> files) {
		List<FileInfoVO> list = new ArrayList<FileInfoVO>();

		for (String strFile : files) {

			File file = new File(strFile);

			FileInfoVO fileInfo = getFileInfo(file.getParent(), file);
			if(fileInfo == null)
				continue;
			list.add(fileInfo);
		}

		return list;
	}

	
	public String getBaseDir() {
		return null;
	}
}
