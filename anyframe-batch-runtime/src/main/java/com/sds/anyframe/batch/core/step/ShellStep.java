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

package com.sds.anyframe.batch.core.step;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.repeat.ExitStatus;

import com.sds.anyframe.batch.common.util.ParamUtil;
import com.sds.anyframe.batch.common.util.ParameterReplacer;
import com.sds.anyframe.batch.common.util.QuoteStringTokenizer;
import com.sds.anyframe.batch.config.BatchResource;
import com.sds.anyframe.batch.config.BatchResource.Mode;
import com.sds.anyframe.batch.config.BatchResource.Type;
import com.sds.anyframe.batch.core.bean.BeanFactory;
import com.sds.anyframe.batch.core.step.resolver.ScriptResolver;
import com.sds.anyframe.batch.core.step.support.StreamConsumerThread;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.util.StringHolder;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ShellStep extends AnyframeAbstractStep {

	private static final String FILENAME_PREFIX = "BATCH_SHELL";
	private static final Log LOGGER = LogFactory.getLog(ShellStep.class);
	private static final Log SCRIPT_LOGGER = LogFactory.getLog("ShellScriptLogger");
	private static final Pattern MERGE_PATTERN = Pattern.compile("\\\\$");

	private String script = null;
	
	public void setScriptHolder(StringHolder scriptHolder) {
		this.script = mergeMultiLines(scriptHolder.getString());
	}
	
	@SuppressWarnings("unchecked")
	public void beforeExeute(StepExecution stepExecution) {
		
		super.beforeExeute(stepExecution);
		
		this.script = ParameterReplacer.replaceParameters(script);
		
		if(!BatchDefine.IS_SHELL_AUTHORIZED)
			return;
		
		List<ScriptResolver> resolvers = null;
		try {
			resolvers = (List<ScriptResolver>) BeanFactory.getBean("shellScriptResolver");
		} catch (Exception e) {
			throw new BatchRuntimeException("shell script resolver is not defined", e);
		}

		// 전체 shell script를 명령어 라인 단위로 자른다. (quotation 지원함)
		QuoteStringTokenizer qst = new QuoteStringTokenizer(script, "\r\n", false, true);

		boolean bUnique = false;
		while (qst.hasMoreTokens()) {
			String commandLine = qst.nextToken().trim();

			// empty line이거나 주석 라인은 skip
			if (StringUtils.isEmpty(commandLine) || commandLine.startsWith(BatchDefine.SHELL_COMMENT))
				continue;

			ScriptResolver targetResolver = null;
			for (ScriptResolver resolver : resolvers) {
				resolver.setScript(commandLine);

				if (resolver.isValid()) {
					targetResolver = resolver;
					break;
				}
			}

			if (targetResolver == null)
				throw new BatchRuntimeException("Unauthorized shell command : " + commandLine);

			for (String filepath : targetResolver.getReadFiles()) {
				BatchResource resource = new BatchResource();
				resource.setMode(Mode.READ);
				resource.setType(Type.FILE);
				resource.setUrl(filepath);
				resource.setData(bUnique);
				
				this.addResource(resource);
			}
			
			for (String filepath : targetResolver.getRewriteFiles()) {
				BatchResource resource = new BatchResource();
				resource.setMode(Mode.UPDATE);
				resource.setType(Type.FILE);
				resource.setUrl(filepath);
				resource.setData(bUnique);
				
				this.addResource(resource);
			}
			
			for (String filepath : targetResolver.getWriteFiles()) {
				BatchResource resource = new BatchResource();
				resource.setMode(Mode.WRITE);
				resource.setType(Type.FILE);
				resource.setUrl(filepath);
				resource.setData(bUnique);
				
				this.addResource(resource);
			}
		}
	}
	
	@Override
	protected ExitStatus doExecute(StepExecution stepExecution) throws Exception {

		beforeStep(null);
		
		ExitStatus exitStatus = ExitStatus.CONTINUABLE;

		replaceFileURL();
		
		LOGGER.info("running shell script");
		LOGGER.info("######################################################################################\n" + script);
		LOGGER.info("######################################################################################");
		LOGGER.info("resources in script");
		
		for (BatchResource resource : getResources()) {
			LOGGER.info(String.format("%10s: %s(%s)", 
					resource.getMode(),
					resource.getUrl(), 
					resource.getUrlOrg()));
		}
		LOGGER.info("######################################################################################");
		
		// script를 실행할 shell file 생성
		String tmpScriptFilePath = getScriptFileName(getName());
		File tempFile = new File(tmpScriptFilePath);
		FileWriter writer = new FileWriter(tempFile);
		writer.write("#! /bin/sh\n");
		writer.write(script);

		writer.flush();
		writer.close();

		// Shell Process 가 실행 중 Batch Process 가 강제 종료된 경우에도 Shell File 을 삭제하기 위하여
		tempFile.deleteOnExit();
		
		// script 실행 - process invoke
		ProcessBuilder pb = new ProcessBuilder("/bin/sh", tmpScriptFilePath);
		//ProcessBuilder pb = new ProcessBuilder("/bin/sh", shellScript);
		Map<String, String> env = pb.environment();
		
		// 현재 process 의 환경변수에 ParamUtil에 등록된 변수를 환경변수를 추가 
		Map<String, String> params = ParamUtil.getAll();
		
		for( Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			
			if(key != null && value != null)
				env.put(key, value);
		}
		
		Process process = pb.start();

		StreamConsumerThread outStream = new StreamConsumerThread(process
				.getInputStream(), "stdout", false);
		StreamConsumerThread errStream = new StreamConsumerThread(process
				.getErrorStream(), "stderr", true);

		errStream.start();
		outStream.start();

		int shellCode = process.waitFor();

		errStream.join();
		outStream.join();

		String execInfo;
		if (shellCode == 0) {
			execInfo = "Return code[" + shellCode + "] Shell execution Success.";
		} else {
			execInfo = "Return code[" + shellCode + "] Shell execution Failed.";
			exitStatus = ExitStatus.FAILED;
		}

		// script logger를 통해 결과값 출력 (step appender로 출력됨)
		SCRIPT_LOGGER.info(execInfo);

		if (shellCode != 0) {
			throw new BatchRuntimeException(
					"Shell execution failed. Return code [" + shellCode
							+ "] error message\n" + errStream.getMessage());
		}
		
		afterStep(null);
		return exitStatus;
	}

	/**
	 * Sehll 실행하기 위하여 script를 임시 저장할 file path.
	 * <p>SLI에서는 BASE_LOG_DIR 환경변수로 저장된 log 파일 디렉토리 (/batch_sam/SHARE/log)
	 * 에 임시 파일을 생성한다.
	 */
	private String getScriptFileName(String stepId) {
		String basePath = BatchDefine.BASE_LOG_DIR;
		String jobID = ParamUtil.getParameter("JOBNAME");
		String fileName = FILENAME_PREFIX + "_" + jobID + "_" + stepId + "_" + System.nanoTime();
		String fullPath = FilenameUtils.concat(basePath, fileName);
		
		return fullPath;
	}

	String mergeMultiLines(String input) {
		
		StringBuilder lineBuffer = new StringBuilder();
		String[] splitScript = StringUtils.split(input, "\r\n");
		
		for(int index = 0; index<splitScript.length; index++) {
			String line = splitScript[index].trim();
			Matcher matcher = MERGE_PATTERN.matcher(line);
			if(matcher.find()) {
				line = line.substring(0, line.lastIndexOf("\\")).trim();
				lineBuffer.append(line);
				lineBuffer.append(" ");
			} else {
				if(index != splitScript.length-1)
					lineBuffer.append(line+"\n");
				else
					lineBuffer.append(line);
			}
		}

		return lineBuffer.toString(); 
	}
	
	void replaceFileURL() {
		
		List<BatchResource> resources = this.getResources();
		
		for(BatchResource resource : resources) {
			String url = resource.getUrl();
			String urlOrg = resource.getUrlOrg();
			
			Boolean bUnique = (Boolean)resource.getData();
			
			script = replace(script, urlOrg, url, bUnique);
		}
	}
	
	/**
	 * script 내에 포함된 filepath에 해당하는 token을 찾아 replace 시키기 위한 메써드.
	 * </br>단순 String.replace()인 경우 같은 character sequence를 갖는 filepath 를 replace 하는 오류가 발생함
	 * <pre> text = "/test/sample.out /test/sample.out.2 </pre> 
	 * 위 문자열에서 "/test/sample.out"을 "/test/sampl.repl"로 변경하는 경우
	 * "/test/sample.out.repl /test/sample.repl.2" 가 되는 현상이 발생함
	 * 이 문제를 방지하기 위하여 완전한 token 단위로만 치환함 
	 * 
	 * @param text
	 * @param search
	 * @param replacement
	 * @return
	 */
	String replace(String text, String search, String replacement, boolean bUnique) {
		
		String literal = Pattern.quote(search);
		
		// 토큰 단위로 match 하기 위하여 search 문자열의 이후에 
		// 공백, 개행문자, 콤마, quotation, 중괄호가  있거나 text 의 끝이어야 함
		String patternStr = literal + "$|" + literal + "(?=[\\s,'\"\\)])";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(text);
		
		int mark = 0;
		int start, end;
		StringBuilder sb = new StringBuilder();
		
		while(matcher.find()) {
			start = matcher.start();
			end   = matcher.end();
			
			sb.append(text.substring(mark, start));
			sb.append(replacement);
			mark = end;
			
			if(bUnique)
				break;
		}
		
		sb.append(text.substring(mark));
		
		
		return sb.toString();
	}

	public void beforeStep(StepExecution stepExecution) {
		
		List<BatchResource> resources = this.getResources();
		
		for(BatchResource resource : resources) {
			Mode mode = resource.getMode();
			Type type = resource.getType();
			
			if(type == Type.FILE && mode == Mode.WRITE) {
				String url = resource.getUrl();
				
				File file = new File(url);
				if(file.exists()) {
					if(BatchDefine.WRITER_FILE_ERROR_ON_EXIST)
						throw new BatchRuntimeException("The file to write alreay exists [" + url + "]");
					else {
						LOGGER.info("The file to write alreay exists and will be overritten[" + url + "]");
					}
				}
				
				if(!url.endsWith(BatchDefine.WRITER_FILE_TMP_SUFFIX)) {
					url = url + BatchDefine.WRITER_FILE_TMP_SUFFIX;
					resource.setUrl(url);
				}
			}
		}
	}

	public ExitStatus afterStep(StepExecution stepExecution) {
		List<BatchResource> resources = this.getResources();
		
		for(BatchResource resource : resources) {
			Mode mode = resource.getMode();
			
			if(mode == Mode.WRITE ) {
				String url = resource.getUrl();
				File tmpFile = new File(url);
				
				if(tmpFile.exists()) {
					if(url.endsWith(BatchDefine.WRITER_FILE_TMP_SUFFIX)) {
						String realName = url.substring(0, url.length() - BatchDefine.WRITER_FILE_TMP_SUFFIX.length());
						File realFile = new File(realName);
						
						if(realFile.exists()) {
							if(BatchDefine.WRITER_FILE_ERROR_ON_EXIST)
								throw new BatchRuntimeException("Complete file already exists: " + realName);
							
							else {
								if(!realFile.delete()) {
									throw new BatchRuntimeException("Fail to delete previous file: " + realName);
								}
							}
						}
						
						if(tmpFile.renameTo(realFile)) {
							LOGGER.info("Succeed to change complete file name from " + url + " to " + realName);
						}else {
							throw new BatchRuntimeException("Fail to change complete file name from " + url + " to " + realName);
						}
					}
				} else {
					throw new BatchRuntimeException("complete temp file does not exist: " + url);
				}
			}
		}
		
		return null;
	}
}
