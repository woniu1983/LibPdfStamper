/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/** 
 * @ClassName: OffsetRandomAccessFile <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月13日 下午1:07:21 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class OffsetRandomAccessFile extends RandomAccessFile {
	
	private long offset;
	
//	/**
//	 * totalLen - offset
//	 */
//	private long length;

	public OffsetRandomAccessFile(final File file) throws IOException {
		this(file, "r", 0L);
	}

	public OffsetRandomAccessFile(final File file, long offset) throws IOException {
		this(file, "r", offset);
	}

	public OffsetRandomAccessFile(final File file, String mode, long offset) throws IOException {
		super(file, mode);
		
		long fileLen = super.length();
		this.offset = offset >= (fileLen-1) ? (fileLen-1) : offset; 
	
//		this.length = fileLen - offset;
	}
	
	@Override
	public void seek(long pos) throws IOException {
		super.seek(pos + this.offset);
	}
	
//	@Override
//	public long getFilePointer() throws IOException {  
//        return super.getFilePointer() + this.offset;  
//    }
//	
//	@Override
//	public long length() {
//		return this.length;
//	}

}
