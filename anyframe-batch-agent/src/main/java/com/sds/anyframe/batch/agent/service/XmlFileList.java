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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sds.anyframe.batch.agent.PropertyProvider;
import com.sds.anyframe.batch.agent.model.JobInfo;
import com.sds.anyframe.batch.agent.util.AgentUtils;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class XmlFileList extends Page {
	private static Logger log = Logger.getLogger(XmlFileList.class);

	private static final int FULL_PACKAGE_START_LOC = 1;

	private static final String[] JOB_CONFIG_FILE = new String[] { "CFG.xml",
			"CFG.tmp.xml" };

	private class BatchRuntimeDirectoryWalker extends DirectoryWalker {
		String baseDirectory = null;
		List<JobInfo> fileList = new ArrayList<JobInfo>();

		protected BatchRuntimeDirectoryWalker(IOFileFilter dirFilter,
				IOFileFilter fileFilter) {
			super(dirFilter, fileFilter, -1);
		}

		protected BatchRuntimeDirectoryWalker(IOFileFilter dirFilter,
				IOFileFilter fileFilter, int depth) {
			super(dirFilter, fileFilter, depth);
		}

		protected void setBaseDirectory(String baseDirectory) {
			this.baseDirectory = baseDirectory;
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected void handleFile(File file, int i, Collection collection) {
			try {
				JobInfo job = getJobInfo(file.getAbsolutePath(), file
						.isDirectory());
				if (job != null) {
					fileList.add(job);
				}
			} catch (Exception e) {
				log.error(e.toString(), e);
				throw new RuntimeException("This file is somewhat weird... : "
						+ file.getAbsolutePath(), e);
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected boolean handleDirectory(File file, int i,
				Collection collection) {
			try {
				JobInfo job = getJobInfo(file.getAbsolutePath(), file
						.isDirectory());
				if (job != null) {
					fileList.add(job);
				}
			} catch (Exception e) {
				log.error(e.toString(), e);
				throw new RuntimeException("This file is somewhat weird... : "
						+ file.getAbsolutePath(), e);
			}
			return true;
		}

		protected List<JobInfo> find() {
			File file = new File(baseDirectory);
			try {
				walk(file, null);
			} catch (IOException ex) {
				log.error(ex.toString(), ex);
				throw new RuntimeException("This file is somewhat weird... : "
						+ file.getAbsolutePath(), ex);
			}
			return fileList;
		}

		private JobInfo getJobInfo(String path, boolean isDir) throws Exception {

			if(isDir) {
				boolean containJobs = isAvailableDirectory(new File(path));
				if(!containJobs)
					return null;
			}
			
			String[] urlArray = StringUtils
					.split(path.substring(path
							.indexOf(PropertyProvider.buildPath)),
							AgentUtils.SEPARATOR);
			
			String jobPackageName = "";
			JobInfo jobInfo = null;
			if (!isDir) {
				for (int idx = FULL_PACKAGE_START_LOC; idx < urlArray.length - 1; idx++) {
					jobPackageName += urlArray[idx];
					if (idx != urlArray.length - 2) {
						jobPackageName += ".";
					}
				}
				String jobFileName = urlArray[urlArray.length - 1].substring(0,
						urlArray[urlArray.length - 1].lastIndexOf("."));

				jobInfo = JobInfo.newJob(path, "", jobPackageName,
						jobFileName, jobFileName);
			} else {
				for (int idx = FULL_PACKAGE_START_LOC; idx < urlArray.length; idx++) {
					jobPackageName += urlArray[idx];
					if (idx != urlArray.length - 1) {
						jobPackageName += ".";
					}
				}
				jobInfo = JobInfo.newPackage(path, "", jobPackageName);
			}
			return jobInfo;
		}

	};

	@Override
	public PageRequest getPage(PageRequest request) throws Exception {

		String searchValue = (String) request.get("searchValue");
		final boolean searchDirOnly = request.get("searchDirOnly") == null ? false
				: (Boolean) request.get("searchDirOnly");
		String searchBaseDir = (String) request.get("searchBaseDir");
		int searchDepth = -1;

		if (searchBaseDir != null && searchBaseDir.length() > 0) {
			searchDepth = 1;
		}

		String baseDirectory = PropertyProvider.runtimeBasePath;

		BatchRuntimeDirectoryWalker walker = null;

		if (searchDirOnly) {
			walker = new BatchRuntimeDirectoryWalker(
			// 1. Dir filter
					FileFilterUtils.andFileFilter(
					// 1-1. Dir filter : dir name contains build/" and not
							// ".svn" and not "**vo"
							FileFilterUtils.asFileFilter(new FileFilter() {
								public boolean accept(File pathname) {
									if (isValidDir(pathname)) {
										return true;
									}
									return false;
								}

								private boolean isValidDir(File pathname) {
									String absolutePath = pathname
											.getAbsolutePath();
									boolean existInBuildDir = StringUtils
											.contains(
													absolutePath,
													PropertyProvider.buildPath
															+ AgentUtils.SEPARATOR);
									boolean isSvnDir = StringUtils.contains(
											absolutePath, ".svn");
									boolean isVODir = absolutePath
											.endsWith("vo");
									
									return existInBuildDir && !isSvnDir
											&& !isVODir;
								}
								
							}),
							// 1-2. Dir filter : dir must be not hidden & not
							// empty
							FileFilterUtils.andFileFilter(
									HiddenFileFilter.VISIBLE,
									EmptyFileFilter.NOT_EMPTY)),
					// 2. File filter : all files excluded
					FalseFileFilter.FALSE);
		} else {
			walker = new BatchRuntimeDirectoryWalker(HiddenFileFilter.VISIBLE,
					FileFilterUtils.asFileFilter(new FileFilter() {
						public boolean accept(File pathname) {
							return isAvailableFile(pathname);
						}

					
					}), searchDepth);
		}

		if (searchBaseDir != null && searchBaseDir.length() > 0) {
			walker.setBaseDirectory(searchBaseDir);
		} else {
			walker.setBaseDirectory(baseDirectory + AgentUtils.SEPARATOR
					+ PropertyProvider.buildPath);
		}
		List<JobInfo> pageResultList = new ArrayList<JobInfo>();
		for (JobInfo item : walker.find()) {
			if (isValidForShowPackages(searchDirOnly, item)) {
				pageResultList.add(item);
			} else if (isValidForShowJobs(searchValue,
					searchDirOnly, item)) {
				pageResultList.add(item);
			}
		}

		request.setResult(pageResultList);
		return request;
	}

	private boolean isAvailableFile(File pathname) {
		for (String str : JOB_CONFIG_FILE) {
			if (StringUtils.contains(pathname.getName(),
					str)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check to show job packages rules : 'work-name-location' package does not
	 * include any file.
	 */
	private boolean isValidForShowPackages(boolean searchDirsOnly, JobInfo item) {

		if (!searchDirsOnly)
			return false; // must be checked before all other conditions
		
		boolean isPackage = item.getType().equalsIgnoreCase(
				JobInfo.TYPE_PACKAGE);
		
		return isPackage;
	}

	/**
	 * Check to show job file items
	 * 
	 * @param searchCondition
	 * @param searchValue
	 * @param searchDirsOnly
	 * @param item
	 * @return
	 * @throws Exception
	 */
	private boolean isValidForShowJobs(String searchValue,
			boolean searchDirsOnly, JobInfo item) throws Exception {

		if (searchDirsOnly)
			return false; // must be checked before all other conditions

		boolean isJob = item.getType().equalsIgnoreCase(JobInfo.TYPE_JOB);
		boolean isSearchItem = isJob
				&& isSearchItem(searchValue, item.getJobId());
		return isSearchItem;
	}

	private boolean isSearchItem(String searchValue, String jobId)
			throws Exception {

		searchValue = searchValue.toUpperCase();

		if (jobId.toUpperCase().indexOf(searchValue) >= 0)
			return true;

		return false;
	}

	private boolean isAvailableDirectory(
			File absolutePath) {
		File[] items = absolutePath.listFiles();
		
		for(File item: items) {
			if(item.isFile()) {
				boolean availableFile = isAvailableFile(item);
				if(availableFile)
					return availableFile;
			}
		}
		return false;
	}
}
