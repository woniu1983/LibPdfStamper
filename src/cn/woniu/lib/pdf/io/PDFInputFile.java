/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.io;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

/** 
 * @ClassName: PDFInputFile <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月11日 下午7:18:23 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFInputFile extends BufferedRandomAccessFile {



	private static final String DEFAULT_MODE = "r";

	public PDFInputFile(String s) throws IOException {
		super(s, DEFAULT_MODE);
	}
	public PDFInputFile(File file) throws IOException {
		super(file, DEFAULT_MODE);
	}

	/**
	 * 
	 * @Title: unread  
	 * @Description: 回退n个字节,n=bytes[].length
	 *
	 * @param bytes[]
	 * @throws IOException
	 */
	public void unread(byte bytes[]) throws IOException {
		if (bytes == null) {
			throw new NullPointerException("bytes[] can't be null.");
		} else {
			long l = getFilePointer();
			seek(l - (long)bytes.length);
			return;
		}
	}

	/**
	 * 
	 * @Title: read  
	 * @Description: 从文件中读取j个字节(char)，然后复制到ac[](从ac的offset位置开始存放)
	 *
	 * @param ac
	 * @param i
	 * @param j
	 * @return
	 * @throws IOException
	 */
	public int read(char ac[], int offset, int j) throws IOException {
		if (ac == null)
			throw new NullPointerException("ch can't be null.");
		for (int k = 0; k < j; k++) {
			try {
				char c = readChar();
				ac[offset + k] = c;
			} catch (EOFException e) {
				return -1;
			}
		}

		return j;
	}

	/**
	 * 
	 * @Title: getRevChar  
	 * @Description: 读取前一个字节，并返回这个字节的int数值，文件指针返回到前一个字节  
	 *
	 * @return
	 * @throws IOException
	 */
	public int getRevChar() throws IOException {
		long l = getFilePointer();
		seek(l - 1L);
		int i = read();
		seek(l - 1L);
		return i;
	}

	/**
	 * 
	 * @Title: skip  
	 * @Description: 跳过 n个字节,并返回跳过的字节数
	 *
	 * @param skipNum
	 * @return
	 * @throws IOException
	 */
	public long skip(long skipNum) throws IOException {
		if (skipNum <= 0L) {
			return 0L;
		} else {
			long l1 = getFilePointer();
			seek(l1 + skipNum);
			return skipNum;
		}
	}

}
