/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package test;

import java.io.File;
import java.io.IOException;

import cn.woniu.lib.pdf.PDFReader;
import cn.woniu.lib.pdf.PDFWatermark;
import cn.woniu.lib.pdf.PDFWatermark.PageMode;
import cn.woniu.lib.pdf.PDFWatermark.PositionMode;
import cn.woniu.lib.pdf.image.PDFImage;
import cn.woniu.lib.pdf.image.PNGImage;
import cn.woniu.lib.pdf.image.Rectangle;

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
		long t1 = System.currentTimeMillis();
		String srcPath = "resource\\A4_Landscape.pdf";
		String outPath = "resource\\result.pdf";
		PDFReader reader = null;
		PDFWatermark watermarker = null;
		try {
			reader = new PDFReader(srcPath);

			Rectangle rect = reader.getPageSize(1);
			System.out.println("Width=" + rect.getWidth() + " Height=" + rect.getHeight());

			PDFImage image = PNGImage.getImage("resource\\p.png");
//			image.setRotationDegrees(180);
//			System.out.println("Image imgW = " + image.getWidth() + "  imgH=" + image.getHeight());
//			System.out.println("Image imgW = " + image.getScaledWidth() + "  imgH=" + image.getScaledHeight());

			watermarker = new PDFWatermark(reader, new File(outPath), image, PageMode.ALL, PositionMode.RIGHT_TOP, 45);
			watermarker.appendWatermark();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (watermarker != null) {
				watermarker.close();
			}

			long t2 = System.currentTimeMillis();
			System.out.println("Time =" + ((t2 - t1) / 1000f));
		}
	}

}
