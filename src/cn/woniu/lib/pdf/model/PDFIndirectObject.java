/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model;

import java.io.IOException;
import java.io.OutputStream;

import cn.woniu.lib.pdf.PDFWriter;
import cn.woniu.lib.pdf.util.StringUtils;

/** 
 * @ClassName: PDFIndirectObject <br/> 
 * 
 * @since JDK 1.6 
 * @Description: TODO  <br/> 
 * 
 * <CODE>PDFIndirectObject</CODE> is the Pdf indirect object.
 * <P>
 * An <I>indirect object</I> is an object that has been labeled so that it can be referenced by
 * other objects. Any type of <CODE>PDFObject</CODE> may be labeled as an indirect object.<BR>
 * An indirect object consists of an object identifier, a direct object, and the <B>endobj</B>
 * keyword. The <I>object identifier</I> consists of an integer <I>object number</I>, an integer
 * <I>generation number</I>, and the <B>obj</B> keyword.<BR>
 *
 * @see		PDFObj
 * @see		PDFIndirectReference
 */
public class PDFIndirectObject {

	/** The object number */
	protected int number;

	/** the generation number */
	protected int generation = 0;

	static final byte STARTOBJ[] 		= StringUtils.getISOBytes(" obj\n");
	static final byte ENDOBJ[] 			= StringUtils.getISOBytes("\nendobj\n");
	static final int SIZEOBJ 			= STARTOBJ.length + ENDOBJ.length;
	protected PDFObj object;
	protected PDFWriter writer;

	protected PDFIndirectObject(int number, PDFObj object, PDFWriter writer) {
		this(number, 0, object, writer);
	}

	PDFIndirectObject(PDFIndirectReference ref, PDFObj object, PDFWriter writer) {
		this(ref.getNumber(),ref.getGeneration(),object,writer);
	}

	PDFIndirectObject(int number, int generation, PDFObj object, PDFWriter writer) {
		this.writer = writer;
		this.number = number;
		this.generation = generation;
		this.object = object;
		PdfEncryption crypto = null;
		if (writer != null) {
			crypto = writer.getEncryption();
		}			
		if (crypto != null) {
			crypto.setHashKey(number, generation);
		}
	}

	public PDFIndirectReference getIndirectReference() {
		return new PDFIndirectReference(object.type(), number, generation);
	}

	/**
	 * Writes efficiently to a stream
	 *
	 * @param os the stream to write to
	 * @throws IOException on write error
	 */
	protected void writeTo(OutputStream os) throws IOException
	{
		os.write(StringUtils.getISOBytes(String.valueOf(number)));
		os.write(' ');
		os.write(StringUtils.getISOBytes(String.valueOf(generation)));
		os.write(STARTOBJ);
		object.toPdf(writer, os);
		os.write(ENDOBJ);
	}

	@Override
	public String toString() {
		return new StringBuffer().append(number).append(' ').append(generation).append(" R: ").append(object != null ? object.toString(): "null").toString();
	}

}
