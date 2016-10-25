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

package com.sds.anyframe.batch.manager.utils;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.sds.anyframe.batch.agent.model.Job;
import com.sds.anyframe.batch.manager.core.MessageUtil;

/**                               
 * 								
 *                                
 * @author Byungho Park         
 */								

public class ExcelWriter {

	private static final int _HEADER = 0;
	private String fileName;
	private static final int _DATA = 1;

	enum CELL {
		_NO("No"),
		_JOBNAME("Job ID"),
		_PID("PID"),
		_JOBSEQ("Job seq"),
		_JOBSTATUS("Job status"),
		_CREATED("Created time"),
		_LASTED("Last updated time"),
		_ELAPSEDTIME("Elapsed time"),
		_LOGFILE("Log file"),
		_SERVERIP("Server IP");
		
		final String title;
		
		CELL(String title) {
			this.title = title;
		}
		public String getCell() {
			return title;
		}
	}
	public ExcelWriter(String fileToWrite) {
		this.fileName = fileToWrite;
	}

	public void write(List<Job> jobs) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Jobs");

		createHeader(sheet, wb);
		writeData(jobs, sheet);
		
		try {
			FileOutputStream file = new FileOutputStream(fileName);
			wb.write(file);
			file.close();
		} catch (Exception e) {
			MessageUtil.showErrorMessage("Batch Manager", "Can not create a excel file", e);
		}
	}

	private void writeData(List<Job> jobs, HSSFSheet sheet) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		for(int i =0;i<jobs.size();i++) {
			Job job = jobs.get(i);
			
			HSSFRow row = sheet.createRow(i+_DATA);
			
			short column = 0;
			
			HSSFCell cell = row.createCell(column);
			cell.setCellValue(i+1);
			sheet.autoSizeColumn(column);
			
			cell = row.createCell(++column);
			cell.setCellValue(new HSSFRichTextString(job.getJobId()));
			sheet.autoSizeColumn(column);
			
			cell = row.createCell(++column);
			cell.setCellValue(job.getPid());
			sheet.autoSizeColumn(column);
			
			cell = row.createCell(++column);
			cell.setCellValue(job.getJobSeq());
			sheet.autoSizeColumn(column);
			
			cell = row.createCell(++column);
			cell.setCellValue(new HSSFRichTextString(job.getJobStatus().toString()));
			sheet.autoSizeColumn(column);
			
			cell = row.createCell(++column);
			cell.setCellValue(new HSSFRichTextString(dateFormat.format(job.getCreatedDate())));
			sheet.autoSizeColumn(column);
			
			cell = row.createCell(++column);
			cell.setCellValue(new HSSFRichTextString(dateFormat.format(job.getLastUpdated())));
			sheet.autoSizeColumn(column);
			
			cell = row.createCell(++column);
			cell.setCellValue(new HSSFRichTextString(BatchUtil.getElapsedTime(job.getCreatedDate(), job
					.getLastUpdated())));
			sheet.autoSizeColumn(column);
			
			
			cell = row.createCell(++column);
			cell.setCellValue(new HSSFRichTextString(job.getLogFiles()));
			sheet.autoSizeColumn(column);
			
			cell = row.createCell(++column);
			cell.setCellValue(new HSSFRichTextString(job.getIp()));
			sheet.autoSizeColumn(column);
		}
	}

	private void createHeader(HSSFSheet sheet, HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
        
        HSSFFont font = wb.createFont();
        font.setFontName(HSSFFont.FONT_ARIAL);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        style.setFont(font);
        
		HSSFRow header = sheet.createRow(_HEADER);
		short column = 0;
		
		for(CELL eCell: CELL.values()) {
			HSSFCell cell = header.createCell((short) eCell.ordinal());
			cell.setCellValue(new HSSFRichTextString(eCell.getCell()));
			cell.setCellStyle(style);
			sheet.autoSizeColumn(column);
			column++;
		}
	}
}
