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

package com.sds.anyframe.batch.charset;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class EncoderDefault extends AbstractEncoder {

	private CharsetEncoder encoder = null;

	public EncoderDefault() {
		this(System.getProperty("file.encoding"));
	}

	public EncoderDefault(String charsetName) {
		super(charsetName);
		this.encoder = Charset.forName(charsetName).newEncoder();

		encoder.onMalformedInput(this.onMalformed);
		encoder.onUnmappableCharacter(this.onUnmappable);
		setReplaceChar(this.replacement);
	}

	@Override
	public byte[] encodeChar(char[] chars, int offset, int length) {

		CharBuffer in = CharBuffer.wrap(chars, offset, length);
		ByteBuffer out;
		try {
			out = encoder.encode(in);
		} catch (CharacterCodingException e) {
			throw new RuntimeException(e);
		}

		byte[] bytes = new byte[out.remaining()];
		out.get(bytes);

		return bytes;
	}

	@Override
	public void encodeChar(char[] chars, int charOffset, int charLength, byte[] bytes, int byteOffset, int byteLength,
			Padding pad, Align align) {

		CharBuffer in = CharBuffer.wrap(chars, charOffset, charLength);
		ByteBuffer out = ByteBuffer.wrap(bytes, byteOffset, byteLength);

		encoder.reset();
		CoderResult result = encoder.encode(in, out, true);

		if (result.isOverflow() && this.onOverFlow == CodingErrorAction.REPORT)
			throw BufferOverFlowException.newInstance(chars, charOffset, charLength, byteLength);

		if (result.isMalformed())
			throw new MalformedInputException();

		if (result.isUnmappable())
			throw new MalformedInputException();

		// padding (right padding only)
		int endPos = out.position();

		if (byteOffset + byteLength - endPos > 0)
			doPadding(bytes, endPos, byteOffset + byteLength - endPos, pad);
	}

	@Override
	public int getByteLength(char[] chars, int offset, int length) {
		CharBuffer in = CharBuffer.wrap(chars, offset, offset + length);
		ByteBuffer out;
		try {
			out = encoder.encode(in);
		} catch (CharacterCodingException e) {
			throw new EncodingException("", e);
		}

		return out.limit();
	}

	public void encodeInteger(int intVal, byte[] bytes, int offset, int byteLen, Padding pad, Align align) {

		boolean bNegative = (intVal < 0);

		char[] chars = toCharArray(intVal);
		byte[] encodeChar = encodeChar(chars, 0, chars.length);

		formatNumber(encodeChar, bNegative, pad, align, bytes, offset, byteLen);
	}

	public void encodeBigDecimal(BigDecimal decimal, int scale, byte[] bytes, int offset, int byteLen, Padding pad,
			Align align, boolean bScale) {

		decimal = bScale ? decimal.setScale(scale) : decimal;
		boolean bNegative = (decimal.signum() == -1);

		char[] chars = decimal.toPlainString().toCharArray();
		byte[] encodeChar = encodeChar(chars, 0, chars.length);

		formatNumber(encodeChar, bNegative, pad, align, bytes, offset, byteLen);
	}

	@Override
	public void onMalformedInput(CodingErrorAction action) {
		super.onMalformedInput(action);
		encoder.onMalformedInput(action);
	}

	@Override
	public void onUnmappableCharacter(CodingErrorAction action) {
		super.onUnmappableCharacter(action);
		encoder.onUnmappableCharacter(action);
	}

	@Override
	public void setReplaceChar(byte ch) {
		super.setReplaceChar(ch);

		char[] chars = new char[] { (char) ch };
		CharBuffer in = CharBuffer.wrap(chars);
		try {
			ByteBuffer out = encoder.encode(in);
			byte[] bytes = new byte[out.limit()];
			out.get(bytes);

			int skip = checkUnicodeBOM(bytes);

			byte[] replace = new byte[bytes.length - skip];
			System.arraycopy(bytes, skip, replace, 0, bytes.length - skip);

			encoder.replaceWith(replace);
		} catch (CharacterCodingException ex) {
			throw new EncodingException("fail to set replaceChar", ex);
		}
	}

	protected int checkUnicodeBOM(byte[] bytes) {

		if (bytes.length > 2) {
			if (bytes[0] == (byte) 0xFE && bytes[1] == (byte) 0xFF) // UTF-16BE BOM
				return 2;

			if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE) // UTF-16LE BOM
				return 2;
		}

		if (bytes.length > 3) {
			if (bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) // UTF-8 BOM
				return 3;
		}

		if (bytes.length > 4) {
			if (bytes[0] == (byte) 0x00 && bytes[1] == (byte) 0x00 && bytes[2] == (byte) 0xFE
					&& bytes[3] == (byte) 0xFF) // UTF-32BE BOM
				return 4;

			if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xFE && bytes[2] == (byte) 0x00
					&& bytes[3] == (byte) 0x00) // UTF-32LE BOM
				return 4;
		}

		return 0;
	}

}
