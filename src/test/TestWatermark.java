/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package test;

import java.io.File;
import java.io.IOException;

import cn.woniu.lib.pdf.PDFReader;
import cn.woniu.lib.pdf.PDFWatermark;
import cn.woniu.lib.pdf.image.PDFImage;
import cn.woniu.lib.pdf.image.PNGImage;

/** 
 * @ClassName: TestWatermark <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月18日 上午8:57:13 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class TestWatermark {

	/**  
	 * @Title: main  
	 * @Description: TODO  
	 *
	 * @param args 
	*/


	public static void main(String[] args) {
		String srcPath = "resource\\sourceoffset.pdf";
		String outPath = "resource\\result.pdf";
		PDFReader reader = null;
		PDFWatermark watermarker = null;
		try {
			reader = new PDFReader(srcPath);

			PDFImage image = PNGImage.getImage("resource\\p.png");
			image.setAbsolutePosition(100, 150);
			//			image.setRotationDegrees(45);
			
			watermarker = new PDFWatermark(reader, new File(outPath), image);
			watermarker.appendWatermark();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (watermarker != null) {
				watermarker.close();
			}
		}
	}

}
