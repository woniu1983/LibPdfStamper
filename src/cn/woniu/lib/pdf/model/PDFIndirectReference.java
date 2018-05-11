/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model;

import java.io.IOException;
import java.io.OutputStream;

import cn.woniu.lib.pdf.PDFWriter;

/** 
 * @ClassName: PDFIndirect <br/> 
 * 
 * @since JDK 1.6 
 * @Description: TODO  <br/> 
 * 
 * <CODE>PDFIndirectReference</CODE> contains a reference to a <CODE>PdfIndirectObject</CODE>.
 * <P>
 * Any object used as an element of an array or as a value in a dictionary may be specified
 * by either a direct object of an indirect reference. An <I>indirect reference</I> is a reference
 * to an indirect object, and consists of the indirect object's object number, generation number
 * and the <B>R</B> keyword.<BR>
 *
 * @see		PdfObject
 * @see		PdfIndirectObject
 */
public class PDFIndirectReference extends PDFObj {

	// membervariables

	/** the object number */
	protected int number;

	/** the generation number */
	protected int generation = 0;

	// constructors

	protected PDFIndirectReference() {
		super(0);
	}

	/**
	 * Constructs a <CODE>PdfIndirectReference</CODE>.
	 *
	 * @param		type			the type of the <CODE>PdfObject</CODE> that is referenced to
	 * @param		number			the object number.
	 * @param		generation		the generation number.
	 */

	PDFIndirectReference(int type, int number, int generation) {
		super(0, new StringBuffer().append(number).append(" ").append(generation).append(" R").toString());
		this.number = number;
		this.generation = generation;
	}

	/**
	 * Constructs a <CODE>PdfIndirectReference</CODE>.
	 *
	 * @param		type			the type of the <CODE>PdfObject</CODE> that is referenced to
	 * @param		number			the object number.
	 */

	protected PDFIndirectReference(int type, int number) {
		this(type, number, 0);
	}

	// methods

	/**
	 * Returns the number of the object.
	 *
	 * @return		a number.
	 */

	public int getNumber() {
		return number;
	}

	/**
	 * Returns the generation of the object.
	 *
	 * @return		a number.
	 */

	public int getGeneration() {
		return generation;
	}

	public String toString() {
		return new StringBuffer().append(number).append(" ").append(generation).append(" R").toString();
	}

	@Override
	public void toPdf(PDFWriter writer, OutputStream os) throws IOException {
		os.write(PDFEncodings.convertToBytes(toString(), null));
	}
	
}
