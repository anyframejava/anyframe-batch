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
package com.sds.anyframe.batch.agent.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public class ByteBufferHelper {
	private static final Logger logger = Logger.getLogger(ByteBufferHelper.class);

	public static byte[] readByteBuffer2(String fileName, int size,
			long position) throws Exception {

		File file = new File(fileName);
		if (!file.exists()) {
			String msg = "The requested file does not exist[" + fileName + "]";
			logger.error(msg);
			throw new RuntimeException(msg);
		}

		FileInputStream fis = new FileInputStream(file);
		FileChannel fc = fis.getChannel();
		byte[] result = null;

		try {
			ByteBuffer buffer = ByteBuffer.allocate(size);
			fc.position(position);

			int read = fc.read(buffer);
			if (read == -1)
				return null;
			buffer.flip();

			byte[] array = buffer.array();
			buffer.clear();
			if (array.length != read) {
				result = new byte[read];
				System.arraycopy(array, 0, result, 0, read);
			} else
				result = array;
		} finally {
			if (fc != null)
				fc.close();

			if (fis != null)
				fis.close();
		}
		return result;
	}

	public static byte[] readByteBuffer(String fileName, int size, long position)
			throws Exception {

		File file = new File(fileName);
		if (!file.exists()) {
			String msg = "The requested file does not exist[" + fileName + "]";
			logger.error(msg);
			throw new RuntimeException(msg);
		}
		if(file.length() == 0)
			throw new Exception("The requested file is empty[" + fileName + "]");

		RandomAccessFile raf = new RandomAccessFile(fileName, "r");

		byte[] result = null;

		try {
			byte[] buffer = new byte[size];
			raf.seek(position);

			int read = raf.read(buffer, 0, size);
			if (buffer.length != read) {
				result = new byte[read];
				System.arraycopy(buffer, 0, result, 0, read);
			} else
				result = buffer;
		} finally {
			if (raf != null)
				raf.close();
		}
		return result;
	}

}
