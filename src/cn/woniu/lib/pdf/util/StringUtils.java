/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.util;

import cn.woniu.lib.pdf.encode.ByteBuffer;

/** 
 * @ClassName: StringUtils <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月10日 下午5:05:39 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class StringUtils {

	final static private byte[] r = ByteBuffer.getISOBytes("\\r");
	final static private byte[] n = ByteBuffer.getISOBytes("\\n");
	final static private byte[] t = ByteBuffer.getISOBytes("\\t");
	final static private byte[] b = ByteBuffer.getISOBytes("\\b");
	final static private byte[] f = ByteBuffer.getISOBytes("\\f");

	private StringUtils() {

	}

	/**
	 * Escapes a <CODE>byte</CODE> array according to the PDF conventions.
	 *
	 * @param bytes the <CODE>byte</CODE> array to escape
	 * @return an escaped <CODE>byte</CODE> array
	 */
	public static byte[] escapeString(final byte bytes[]) {
		ByteBuffer content = new ByteBuffer();
		escapeString(bytes, content);
		return content.toByteArray();
	}

	/**
	 * Escapes a <CODE>byte</CODE> array according to the PDF conventions.
	 *
	 * @param bytes the <CODE>byte</CODE> array to escape
	 * @param content the content
	 */
	public static void escapeString(final byte bytes[], final ByteBuffer content) {
		content.append_i('(');
		for (int k = 0; k < bytes.length; ++k) {
			byte c = bytes[k];
			switch (c) {
			case '\r':
				content.append(r);
				break;
			case '\n':
				content.append(n);
				break;
			case '\t':
				content.append(t);
				break;
			case '\b':
				content.append(b);
				break;
			case '\f':
				content.append(f);
				break;
			case '(':
			case ')':
			case '\\':
				content.append_i('\\').append_i(c);
				break;
			default:
				content.append_i(c);
			}
		}
		content.append_i(')');
	}


	/**
	 * Converts an array of unsigned 16bit numbers to an array of bytes.
	 * The input values are presented as chars for convenience.
	 * 
	 * @param chars the array of 16bit numbers that should be converted
	 * @return the resulting byte array, twice as large as the input
	 */
	public static byte[] convertCharsToBytes(char[] chars) {
		byte[] result = new byte[chars.length*2];
		for (int i=0; i<chars.length;i++) {
			result[2*i] = (byte) (chars[i] / 256);
			result[2*i+1] = (byte) (chars[i] % 256);
		}
		return result;
	}



	/** Converts a <CODE>String</CODE> into a <CODE>Byte</CODE> array
	 * according to the ISO-8859-1 codepage.
	 * @param text the text to be converted
	 * @return the conversion result
	 */
	public static final byte[] getISOBytes(String text) {
		if (text == null)
			return null;
		int len = text.length();
		byte b[] = new byte[len];
		for (int k = 0; k < len; ++k)
			b[k] = (byte)text.charAt(k);
		return b;
	}
}
