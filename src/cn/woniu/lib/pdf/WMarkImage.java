/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf;

import cn.woniu.lib.pdf.PDFWatermark.PageMode;
import cn.woniu.lib.pdf.PDFWatermark.PositionMode;
import cn.woniu.lib.pdf.image.PDFImage;

/** 
 * @ClassName: WMarkImage <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年10月17日 下午6:52:32 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class WMarkImage {
	public PDFImage image;
	public PageMode pageMode = PageMode.ALL; 
	public PositionMode posMode = PositionMode.MID;
	public int rotateDegree = 0;
}
