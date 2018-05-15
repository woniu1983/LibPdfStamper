/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model.derivate;

import java.io.IOException;
import java.io.OutputStream;

import cn.woniu.lib.pdf.PDFReader;
import cn.woniu.lib.pdf.PDFWriter;
import cn.woniu.lib.pdf.encode.PdfEncodings;
import cn.woniu.lib.pdf.model.PDFIndirectReference;

/** 
 * @ClassName: PRIndirectReference <br/> 
 * @Description: TODO  <br/> 
 * 
 * @since JDK 1.6 
 */
public class PRIndirectReference extends PDFIndirectReference {

	protected PDFReader reader;

	/**
	 * Constructs a <CODE>PDFIndirectReference</CODE>.
	 *
	 * @param		reader			a <CODE>PDFReader</CODE>
	 * @param		number			the object number.
	 * @param		generation		the generation number.
	 */
	public PRIndirectReference(PDFReader reader, int number, int generation) {
		type = INDIRECT;
		this.number = number;
		this.generation = generation;
		this.reader = reader;
	}

	/**
	 * Constructs a <CODE>PDFIndirectReference</CODE>.
	 *
	 * @param		reader			a <CODE>PDFReader</CODE>
	 * @param		number			the object number.
	 */
	public PRIndirectReference(PDFReader reader, int number) {
		this(reader, number, 0);
	}

	// methods
	public void write(PDFWriter writer, OutputStream os) throws IOException {
		if (writer != null) {
			int n = writer.getNewObjectNumber(reader, number, generation);
			String value = new StringBuffer().append(n)
											.append(" ")
											.append(reader.isAppendable() ? generation : 0)
											.append(" R")
											.toString();
			byte[] encodeValue = PdfEncodings.convertToBytes(value, null);
			os.write(encodeValue);
		} else {
			this.write(os);
		}
	}
	
	@Override
	public void write(OutputStream os) throws IOException {
		super.write(os);
	}

	public PDFReader getReader() {
		return reader;
	}

	public void setNumber(int number, int generation) {
		this.number = number;
		this.generation = generation;
	}
}
