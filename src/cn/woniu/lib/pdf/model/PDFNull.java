/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model;

/** 
 * @ClassName: PDFNull <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月10日 下午7:15:36 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFNull extends PDFObj {

	private static final long serialVersionUID = -3124461514381132020L;

	public static final PDFNull	PDFNULL = new PDFNull();

	private static final String CONTENT = "null";

	public PDFNull() {
		super(NULL, CONTENT);
	}

	public String toString() {
		return CONTENT;
	}
	
}
