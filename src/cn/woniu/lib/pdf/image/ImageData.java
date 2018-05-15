/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.image;

import java.io.IOException;
import java.net.URL;


/** 
 * @ClassName: ImageData <br/> 
 * @Description: Raw Image data that has to be inserted into the document  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月14日 上午8:57:12 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class ImageData extends PDFImage {


	/** 
	 * Creates an Image in raw mode.
	 *
	 * @param width the exact width of the image
	 * @param height the exact height of the image
	 * @param components 1=GrayScale, 3=RGB ,  4=CMYK
	 * @param bpc bits per component. Must be 1,2,4 or 8
	 * @param data the image data
	 * @throws BadElementException on error
	 */
	public ImageData(int width, int height, int components, int bpc, byte[] data) throws IOException{
		super((URL)null);
		
		type = IMGRAW;
		scaledHeight = height;
		setTop(scaledHeight);
		scaledWidth = width;
		setRight(scaledWidth);
		
		if (components != 1 && components != 3 && components != 4) {
			throw new IOException("components must be 1, 3 or4");
		}
		if (bpc != 1 && bpc != 2 && bpc != 4 && bpc != 8) {
			throw new IOException("bits per component must be 1, 2, 4 or8");
		}
		
		this.colorspace = components;
		this.bpc = bpc;
		this.rawData = data;
		this.plainWidth = getWidth();
		this.plainHeight = getHeight();
	}
}
