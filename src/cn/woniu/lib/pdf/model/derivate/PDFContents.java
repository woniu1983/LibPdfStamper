/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model.derivate;

import cn.woniu.lib.pdf.model.PDFStream;
import cn.woniu.lib.pdf.util.StringUtils;


/** 
 * @ClassName: PDFContents <br/> 
 * @Description: /Contents 这一行内容所在结构体， 作为一个PDFStream  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月14日 下午8:31:24 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFContents extends PDFStream {

	public static final byte SAVESTATE[] 		= StringUtils.getISOBytes("q\n");
	public static final byte RESTORESTATE[] 	= StringUtils.getISOBytes("Q\n");
	public static final byte ROTATE90[] 		= StringUtils.getISOBytes("0 1 -1 0 ");
	public static final byte ROTATE180[] 		= StringUtils.getISOBytes("-1 0 0 -1 ");
	public static final byte ROTATE270[] 		= StringUtils.getISOBytes("0 -1 1 0 ");
	public static final byte ROTATEFINAL[] 	= StringUtils.getISOBytes(" cm\n");
	
	
}
