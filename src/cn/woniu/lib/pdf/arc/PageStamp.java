/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.arc;

import cn.woniu.lib.pdf.encode.ByteBuffer;
import cn.woniu.lib.pdf.model.PDFDictionary;
import cn.woniu.lib.pdf.model.PDFIndirectReference;
import cn.woniu.lib.pdf.model.PDFName;
import cn.woniu.lib.pdf.model.derivate.PDFResources;

/** 
 * @ClassName: PageStamp <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月15日 下午2:46:53 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PageStamp {


	public int replacePoint = 0;
	public PDFDictionary pageN;
	public PDFDictionary originalResources;
	public PDFDictionary xObjectDictionary = new PDFDictionary();

	/** 
	 * 用于插入图片时，后面附加的一些内容 <BR>
	 * 例如：  <B>q 74.07 119.98 -121.68 75.12 151.68 50 cm /Xi5 Do Q </B>
	 */
	public ByteBuffer content = new ByteBuffer(); 

	public PageStamp(PDFDictionary pageN) {
		this.pageN = pageN;
		this.originalResources = pageN.getAsDict(PDFName.RESOURCES);
	}

	public PDFName addXObject(PDFName name, PDFIndirectReference reference) {
		this.xObjectDictionary.put(name, reference);
		return name;
	}

	public ByteBuffer getAdditionalContent() {
		return this.content;
	}

	public PDFDictionary getResources() {
		PDFResources resources = new PDFResources();
		if (originalResources != null)
			resources.putAll(originalResources);
		//         resources.add(PdfName.FONT, fontDictionary);
		resources.add(PDFName.XOBJECT, xObjectDictionary); //TODO {/Xi2=14 0 R, /Xi3=15 0 R}
		//         resources.add(PdfName.COLORSPACE, colorDictionary);
		//         resources.add(PdfName.PATTERN, patternDictionary);
		//         resources.add(PdfName.SHADING, shadingDictionary);
		//         resources.add(PdfName.EXTGSTATE, extGStateDictionary);
		//         resources.add(PdfName.PROPERTIES, propertyDictionary);
		return resources;
	}



}
