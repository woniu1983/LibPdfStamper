/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.util;

/** 
 * @ClassName: PDFConstant <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月13日 上午9:54:35 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFConstant {
	
    public static boolean compress = true;
	
	public static final String PDF_HEADER = "%PDF-";
	
	public static final String FDF_HEADER = "%FDF-";
	
	public static final String PDF_EOF = "%%EOF";
	
	public static final String PDF_STARTXREF = "startxref";
	
	public static final String PDF_XREF = "xref";
	
	public static final int SEPARATOR_LINE = '\n';
	
	/**
	 * 65535 Generation 最大值
	 */
	public static final int MAX_GEN = 65535;

}
