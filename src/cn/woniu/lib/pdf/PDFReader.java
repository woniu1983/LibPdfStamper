/** 
 * Copyright (c) 2018, woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/** 
 * @ClassName: PDFReader <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月10日 下午3:20:04 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFReader {
	
	private String srcPdfPath;
	private String srcPdfPassword;
	
	/**
	 * 
	 * <p>Title: </p>  
	 * <p>Description: 读取PDF文件并解析结构， 目前只支持无加密和签名的PDF文件</p>  
	 * @param path
	 * @throws IOException
	 */
	public PDFReader(final String path) throws IOException{
		this.srcPdfPath = path;
		readPdf();
	}

	/**
	 * 
	 * @Title: readPdf  
	 * @Description: TODO  
	 *
	 * @throws IOException
	 */
	protected void readPdf() throws IOException {
		File file = new File(this.srcPdfPath);
		if (!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException(this.srcPdfPath + " can't open.");
		}
		
		
			
	}
}
