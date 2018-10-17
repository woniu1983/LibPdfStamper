/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package test;

import java.io.File;
import java.io.IOException;

import cn.woniu.lib.pdf.PDFMultiWatermark;
import cn.woniu.lib.pdf.PDFReader;
import cn.woniu.lib.pdf.PDFWatermark;
import cn.woniu.lib.pdf.PDFWatermark.PageMode;
import cn.woniu.lib.pdf.PDFWatermark.PositionMode;
import cn.woniu.lib.pdf.WMarkImage;
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
		addMultiWM();
	}
	
	private static void addSingleWM() {
		long t1 = System.currentTimeMillis();
		String srcPath = "resource\\copy-0-180.pdf";
		String outPath = "resource\\result.pdf";
		PDFReader reader = null;
		PDFWatermark watermarker = null;
		try {
			reader = new PDFReader(srcPath);

//			Rectangle rect = reader.getPageSize(1);
//			System.out.println("--------Width=" + rect.getWidth() + " Height=" + rect.getHeight());

			PDFImage image = PNGImage.getImage("resource\\wm_txt.png");

			watermarker = new PDFWatermark(reader, new File(outPath), image, PageMode.ALL, PositionMode.MID_TOP, 300);
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
	
	private static void addMultiWM() {
		long t1 = System.currentTimeMillis();
		String srcPath = "resource\\source.pdf";
		String outPath = "resource\\resultm.pdf";
		PDFReader reader = null;
		PDFMultiWatermark watermarker = null;
		try {
			reader = new PDFReader(srcPath);

//			Rectangle rect = reader.getPageSize(1);
//			System.out.println("--------Width=" + rect.getWidth() + " Height=" + rect.getHeight());

			PDFImage image = PNGImage.getImage("resource\\p.png");
			WMarkImage wm1 = new WMarkImage();
			wm1.image = image;
			wm1.pageMode = PageMode.ALL;
			wm1.posMode = PositionMode.MID;
			wm1.rotateDegree = 300;
			
			PDFImage image2 = PNGImage.getImage("resource\\q.png");
			WMarkImage wm2 = new WMarkImage();
			wm2.image = image2;
			wm2.pageMode = PageMode.FIRST;
			wm2.posMode = PositionMode.MID;
			wm2.rotateDegree = 45;

			watermarker = new PDFMultiWatermark(reader, new File(outPath), wm1, wm2);
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
