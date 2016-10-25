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

package com.anyframe.core.vo.charset;

import java.math.BigDecimal;
import java.nio.charset.CodingErrorAction;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public abstract class AbstractEncoder extends AbstractCharsetCoder implements ICharsetEncoder {

	protected byte replacement = ' ';
	
	protected AbstractEncoder(String charsetName) {
		super(charsetName);
	}
	
	public void setReplaceChar(byte ch) {
		this.replacement = ch;
	}
	
	abstract public int getByteLength(char[] chars, int offset, int length);
	
	abstract public void encodeChar(char[] chars, int charOffset, int charLength, 
									byte[] bytes, int byteOffset, int byteLength,
									Padding pad, Align align);
	public void encodeChar(char[] chars, int charOffset, int charLength, 
            byte[] bytes, int byteOffset, int byteLength,
            Padding pad){
		encodeChar(chars, charOffset, charLength, bytes, byteOffset, byteLength, pad, null);
	}
	
	public byte[] encodeChar(char[] chars, int offset, int length) {
		int byteLength = getByteLength(chars, offset, length);
		byte[] bytes = new byte[byteLength];
		
		encodeChar(chars, offset, length, bytes, 0, bytes.length, null, null);
		
		return bytes;
	}
	
	public void encodeNumber(char[] chars, int charOffset, int charLength, 
            byte[] bytes, int byteOffset, int byteLength,
            Padding pad, Align align) {
		
		boolean bNagative = chars[0] == '-';
			
		byte[] numberBytes = encodeChar(chars, 0, chars.length);
		formatNumber(numberBytes, bNagative, pad, align, bytes, byteOffset, byteLength);
	}
	
	public void encodeInteger(int intVal, Padding pad, Align align, 
			byte[] bytes, int offset, int byteLen) {

		char[] chars = toCharArray(intVal);

		formatNumber(chars, pad, align, bytes, offset, byteLen);
	}

	
	public void encodeBigDecimal(BigDecimal decimal, int scale,
			boolean bScale, Padding pad, Align align, byte[] bytes,
			int offset, int byteLen) {

		// in order not to modify original value
		BigDecimal value = bScale ? decimal.setScale(scale) : decimal;
		
		char[] chars = value.toPlainString().toCharArray();

		formatNumber(chars, pad, align, bytes, offset, byteLen);
	}
	
	protected void formatNumber(byte[] intput, boolean bNegative, Padding pad, Align align,
			byte[] bytes, int offset, int byteLen) {
		
		int digitCnt = intput.length;

		// when exceed buffer. slice tail or throw exception if REPORT.
		if (digitCnt > byteLen) {
			for (int i = 0; i < byteLen; i++)
				bytes[i + offset] = (byte) intput[i];

			if (onOverFlow == CodingErrorAction.REPORT)
				throw BufferOverFlowException.newInstance(intput, 0, intput.length, byteLen);

			return;
		}

		int padCnt = byteLen - digitCnt;
		int index1 = offset;
		int index2 = 0;
		
		byte padByte = getEncodedChar(pad.toChar());

		if (align == Align.RIGHT) {
			// if zero padding and minus value, add sign first
			if (pad == Padding.ZERO && bNegative) {
				bytes[index1++] = (byte) intput[index2++];
				digitCnt--;
			}

			// add left padding first
			for (int i = 0; i < padCnt; i++)
				bytes[index1++] = padByte;
			// copy number digit
			for (int i = 0; i < digitCnt; i++)
				bytes[index1++] = (byte) intput[index2++];

		} else {
			if (pad == Padding.ZERO)
				throw new RuntimeException("zero(0) can not be padded on the right side of the number");

			// copy number digit first
			for (int i = 0; i < digitCnt; i++)
				bytes[index1++] = (byte) intput[i];
			// add right padding
			for (int i = 0; i < padCnt; i++)
				bytes[index1++] = (byte) padByte;
		}

		assert index1 == byteLen + offset;
	}
	
	private void formatNumber(char[] chars, Padding pad, Align align,
			  byte[] bytes, int offset, int byteLen) {
		int digitCnt = chars.length;
		
		// when exceed buffer. slice tail and throw exception if REPORT.
		if(digitCnt > byteLen) {
			for(int i=0; i<byteLen; i++)
				bytes[i+offset] = (byte) chars[i];
		
			if(onOverFlow == CodingErrorAction.REPORT)
				throw BufferOverFlowException.newInstance(chars, 0, chars.length, byteLen);
		
			return;
		}
		
		int padCnt = byteLen - digitCnt;
		int index1 = offset;
		int index2 = 0;
		byte padByte = getEncodedChar(pad.toChar());
		
		if(align == Align.RIGHT) {
			// if zero padding and minus value, add sign first
			if(pad == Padding.ZERO && chars[0] == '-') {
				bytes[index1++] = (byte) chars[index2++];
				digitCnt--;
			}
		
			// add left padding first
			for(int i=0; i<padCnt; i++)
				bytes[index1++] = padByte;
			// copy number digit
			for(int i=0; i<digitCnt; i++)
				bytes[index1++] = (byte) chars[index2++];
		
		} else {
			if(pad == Padding.ZERO)
				throw new RuntimeException("zero(0) can not be added on the right side of the number");
		
			// copy number digit first
			for(int i=0; i<digitCnt; i++)
				bytes[index1++] = (byte) chars[i];
			// add right padding
			for(int i=0; i<padCnt; i++)
				bytes[index1++] = (byte) padByte; 
		}
		
		assert index1 == byteLen+offset;
	}
	
	protected char[] toCharArray(int intVal) {
        if (intVal == Integer.MIN_VALUE)
            return new char[]{'-','2','1','4','7','4','8','3','6','4','8'};
        
        int size = (intVal < 0) ? stringSize(-intVal) + 1 : stringSize(intVal);
        
        char[] buf = new char[size];
        fillCharBuffer(intVal, size, buf);
        
        return buf;
    }
	
	protected byte getEncodedChar(char ch) {
	
		char[] padChars = new char[]{ch};
		byte[] bytes = encodeChar(padChars, 0, padChars.length);
		
		if(bytes.length != 1) {
			throw new EncodingException("multi-byte character set");
		}
		
		return bytes[0];
	}

	
	/**
	 * {@code bytes}의{@code offset}부터 {@code length} 만큼  padding 한다.
	 */
	protected void doPadding(byte[] bytes, int offset, int length, Padding pad) {

		byte padByte = getEncodedChar(pad.toChar());
		
		for(int i=0; i<length; i++)
			bytes[offset+i] = padByte;
	}
	
	// size is 1024
	final static byte[] PADDING_SPACE = new byte[] {
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',
		' ', ' ', ' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' ',' ', ' '
	};
	
	
	/**
	 * java.lang.Integer 참조
	 * int type 의 정수를 char 배열로 변환하기 위한 정수 길이 배열
	 */
	final static int [] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
        99999999, 999999999, Integer.MAX_VALUE };
	
	/**
	 * java.lang.Integer 참조
	 */
	final static char[] digits = {
		'0' , '1' , '2' , '3' , '4' , '5' ,
		'6' , '7' , '8' , '9' , 'a' , 'b' ,
		'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
		'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
		'o' , 'p' , 'q' , 'r' , 's' , 't' ,
		'u' , 'v' , 'w' , 'x' , 'y' , 'z'
	    };
	
	/**
	 * java.lang.Integer 참조
	 */
	final static char [] DigitTens = {
		'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
		'1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
		'2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
		'3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
		'4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
		'5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
		'6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
		'7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
		'8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
		'9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
		} ; 

	/**
	 * java.lang.Integer 참조
	 */
	final static char [] DigitOnes = { 
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		} ;
	
	
	// Requires positive x
	/**
	 * java.lang.Integer 참조
	 * @param x
	 * @return
	 */
	int stringSize(int x) {
		for (int i=0; ; i++)
			if (x <= sizeTable[i])
				return i+1;
	}
	
	/**
	 * java.lang.Integer 참조
	 * @param i
	 * @param index
	 * @param buf
	 */
	void fillCharBuffer(int i, int index, char[] buf) {
        int q, r;
        int charPos = index;
        char sign = 0;

        if (i < 0) { 
            sign = '-';
            i = -i;
        }

        // Generate two digits per iteration
        while (i >= 65536) {
            q = i / 100;
        // really: r = i - (q * 100);
            r = i - ((q << 6) + (q << 5) + (q << 2));
            i = q;
            buf [--charPos] = DigitOnes[r];
            buf [--charPos] = DigitTens[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i <= 65536, i);
        for (;;) { 
            q = (i * 52429) >>> (16+3);
            r = i - ((q << 3) + (q << 1));  // r = i-(q*10) ...
            buf [--charPos] = digits [r];
            i = q;
            if (i == 0) break;
        }
        if (sign != 0) {
            buf [--charPos] = sign;
        }
    }
}
