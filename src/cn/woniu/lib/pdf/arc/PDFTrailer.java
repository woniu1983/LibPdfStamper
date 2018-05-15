/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.arc;

import java.io.IOException;
import java.io.OutputStream;

import cn.woniu.lib.pdf.PDFWriter;
import cn.woniu.lib.pdf.model.PDFDictionary;
import cn.woniu.lib.pdf.model.PDFIndirectReference;
import cn.woniu.lib.pdf.model.PDFName;
import cn.woniu.lib.pdf.model.PDFNumeric;
import cn.woniu.lib.pdf.model.PDFObj;
import cn.woniu.lib.pdf.util.StringUtils;

/** 
 * @ClassName: PDFTrailer <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月14日 下午7:35:55 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFTrailer extends PDFDictionary {

	long offset;

	/**
	 * Constructs a PDF-Trailer.
	 *
	 * @param		size		the number of entries in the <CODE>PdfCrossReferenceTable</CODE>
	 * @param		offset		offset of the <CODE>PdfCrossReferenceTable</CODE>
	 * @param		root		an indirect reference to the root of the PDF document
	 * @param		info		an indirect reference to the info object of the PDF document
	 * @param encryption
	 * @param fileID
	 * @param prevxref
	 */

	public PDFTrailer(final int size, final long offset, final PDFIndirectReference root, final PDFIndirectReference info, final PDFIndirectReference encryption, final PDFObj fileID, final long prevxref) {
		this.offset = offset;
		put(PDFName.SIZE, new PDFNumeric(size));
		put(PDFName.ROOT, root);
		if (info != null) {
			put(PDFName.INFO, info);
		}
		if (encryption != null) {
			put(PDFName.ENCRYPT, encryption);
		}
		if (fileID != null)
			put(PDFName.ID, fileID);
		if (prevxref > 0) {
			put(PDFName.PREV, new PDFNumeric(prevxref));
		}
	}

	/**
	 * Returns the PDF representation of this <CODE>PDFObj</CODE>.
	 * @param writer
	 * @param os
	 * @throws IOException
	 */
	@Override
	public void write(final OutputStream os) throws IOException {
		os.write(StringUtils.getISOBytes("trailer\n"));
		super.write(os);
		os.write('\n');
		PDFWriter.writeKeyInfo(os);
		os.write(StringUtils.getISOBytes("startxref\n"));
		os.write(StringUtils.getISOBytes(String.valueOf(offset)));
		os.write(StringUtils.getISOBytes("\n%%EOF\n"));

		System.out.print("startxref\n" + String.valueOf(offset) + "\n%%EOF\n");//TODO
	}
}
