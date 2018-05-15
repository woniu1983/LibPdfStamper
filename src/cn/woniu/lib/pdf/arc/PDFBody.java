/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.arc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import cn.woniu.lib.pdf.PDFWriter;
import cn.woniu.lib.pdf.encode.ByteBuffer;
import cn.woniu.lib.pdf.model.PDFIndirectObject;
import cn.woniu.lib.pdf.model.PDFIndirectReference;
import cn.woniu.lib.pdf.model.PDFObj;
import cn.woniu.lib.pdf.util.PDFConstant;
import cn.woniu.lib.pdf.util.StringUtils;


/** 
 * @ClassName: PDFBody <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月14日 下午7:17:13 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFBody {

	private static final int OBJSINSTREAM = 200;

	protected final PDFWriter writer;

	/** array containing the cross-reference table of the normal objects. */
	protected final TreeSet<PDFCrossReference> xrefs;

	protected int refnum;

	/** the current byte position in the body. */
	protected long position;

	protected int currentObjNum;

	protected int numObj = 0;

	protected ByteBuffer index;

	protected ByteBuffer streamObjects;

	public PDFBody(final PDFWriter writer) {
		xrefs = new TreeSet<PDFCrossReference>();
		xrefs.add(new PDFCrossReference(0, 0, PDFConstant.MAX_GEN));
		position = writer.getOs().getCounter();
		refnum = 1;
		this.writer = writer;
	}


	public void setRefnum(final int refnum) {
		this.refnum = refnum;
	}
	
	public void setOffset(final long offset) {
		this.position = offset;
	}

	public long offset() {
		return position;
	}

	public int size() {
		return Math.max(xrefs.last().getRefnum() + 1, refnum);
	}


	/**
	 * Adds a <CODE>PDFObj</CODE> to the body.
	 * <P>
	 * This methods creates a <CODE>PDFIndirectObject</CODE> with a
	 * certain number, containing the given <CODE>PDFObj</CODE>.
	 * It also adds a <CODE>PDFCrossReference</CODE> for this object
	 * to an <CODE>ArrayList</CODE> that will be used to build the
	 * Cross-reference Table.
	 *
	 * @param		object			a <CODE>PDFObj</CODE>
	 * @return		a <CODE>PDFIndirectObject</CODE>
	 * @throws IOException
	 */

	public PDFIndirectObject add(final PDFObj object) throws IOException {
		return add(object, getIndirectReferenceNumber());
	}

	public PDFIndirectObject add(final PDFObj object, final boolean inObjStm) throws IOException {
		return add(object, getIndirectReferenceNumber(), 0, inObjStm);
	}

	/**
	 * Gets a PDFIndirectReference for an object that will be created in the future.
	 * @return a PDFIndirectReference
	 */

	public PDFIndirectReference getPdfIndirectReference() {
		return new PDFIndirectReference(0, getIndirectReferenceNumber());
	}

	public int getIndirectReferenceNumber() {
		int n = refnum++;
		xrefs.add(new PDFCrossReference(n, 0, PDFConstant.MAX_GEN));
		return n;
	}

	/**
	 * Adds a <CODE>PDFObj</CODE> to the body given an already existing
	 * PDFIndirectReference.
	 * <P>
	 * This methods creates a <CODE>PDFIndirectObject</CODE> with the number given by
	 * <CODE>ref</CODE>, containing the given <CODE>PDFObj</CODE>.
	 * It also adds a <CODE>PDFCrossReference</CODE> for this object
	 * to an <CODE>ArrayList</CODE> that will be used to build the
	 * Cross-reference Table.
	 *
	 * @param		object			a <CODE>PDFObj</CODE>
	 * @param		ref		        a <CODE>PDFIndirectReference</CODE>
	 * @return		a <CODE>PDFIndirectObject</CODE>
	 * @throws IOException
	 */

	public PDFIndirectObject add(final PDFObj object, final PDFIndirectReference ref) throws IOException {
		return add(object, ref, true);
	}

	public PDFIndirectObject add(final PDFObj object, final PDFIndirectReference ref, final boolean inObjStm) throws IOException {
		return add(object, ref.getNumber(), ref.getGeneration(), inObjStm);
	}

	public PDFIndirectObject add(final PDFObj object, final int refNumber) throws IOException {
		return add(object, refNumber, 0, true); // to false
	}

	public PDFIndirectObject add(final PDFObj object, final int refNumber, final int generation, final boolean inObjStm) throws IOException {
		//        if (inObjStm && object.canBeInObjStm() && writer.isFullCompression()) {
		//            PDFCrossReference pxref = addToObjStm(object, refNumber);
		//            PDFIndirectObject indirect = new PDFIndirectObject(refNumber, object, writer);
		//            if (!xrefs.add(pxref)) {
		//                xrefs.remove(pxref);
		//                xrefs.add(pxref);
		//            }
		//            return indirect;
		//        }
		//        else {
		//            PDFIndirectObject indirect;
		//            if (writer.isFullCompression()) {
		//            	System.out.println("[PDFWriter] add-----------------------Write for writer.isFullCompression");
		//            	indirect = new PDFIndirectObject(refNumber, object, writer);
		//            	write(indirect, refNumber);
		//            }
		//            else {
		//            	System.out.println("[PDFWriter] add #######################Write for Not writer.isFullCompression");
		//            	indirect = new PDFIndirectObject(refNumber, generation, object, writer);
		//            	write(indirect, refNumber, generation);
		//            }
		//            return indirect;
		//        }
		System.out.println("[PDFWriter] add #######################Write for Not writer.isFullCompression");
		PDFIndirectObject indirect = new PDFIndirectObject(refNumber, generation, object, writer);
		write(indirect, refNumber, generation);
		return indirect;
	}


	//	protected PDFCrossReference addToObjStm(final PDFObj obj, final int nObj) throws IOException {
	//		if (numObj >= OBJSINSTREAM) {
	//			flushObjStm();
	//		}
	//		
	//		if (index == null) {
	//			index = new ByteBuffer();
	//			streamObjects = new ByteBuffer();
	//			currentObjNum = getIndirectReferenceNumber();
	//			numObj = 0;
	//		}
	//		int p = streamObjects.size();
	//		int idx = numObj++;
	//		PdfEncryption enc = writer.crypto;
	//		writer.crypto = null;
	//		obj.write(streamObjects);
	//		writer.crypto = enc;
	//		streamObjects.append(' ');
	//		index.append(nObj).append(' ').append(p).append(' ');
	//		return new PDFCrossReference(2, nObj, currentObjNum, idx);
	//	}

	//	public void flushObjStm() throws IOException {
	//		if (numObj == 0)
	//			return;
	//		int first = index.size();
	//		index.append(streamObjects);
	//		PDFStream stream = new PDFStream(index.toByteArray());
	//		stream.flateCompress(writer.getCompressionLevel());
	//		stream.put(PDFName.TYPE, PDFName.OBJSTM);
	//		stream.put(PDFName.N, new PDFNumeric(numObj));
	//		stream.put(PDFName.FIRST, new PDFNumeric(first));
	//		add(stream, currentObjNum);
	//		index = null;
	//		streamObjects = null;
	//		numObj = 0;
	//	}

	protected void write(final PDFIndirectObject indirect, final int refNumber) throws IOException {
		PDFCrossReference pxref = new PDFCrossReference(refNumber, position);
		System.out.println("PDFCrossReference=" + pxref.type + " " + pxref.refnum + " "  + pxref.generation + " "  + pxref.offset);//TODO
		if (!xrefs.add(pxref)) {
			xrefs.remove(pxref);
			xrefs.add(pxref);
		}
		indirect.writeTo(writer.getOs());
		position = writer.getOs().getCounter();
	}

	protected void write(final PDFIndirectObject indirect, final int refNumber, final int generation) throws IOException {
		PDFCrossReference pxref = new PDFCrossReference(refNumber, position, generation);
		System.out.println("[PDFWriter] write >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
				+ "XPDFCrossReference=" + pxref.type + " " + pxref.refnum + " "  + pxref.generation + " "  + pxref.offset);//TODO
		if (!xrefs.add(pxref)) {
			xrefs.remove(pxref);
			xrefs.add(pxref);
		}

		System.out.println("[PDFWriter] writeTo OutStream Begin>>>>>>>>>>>>>>>" + indirect);//TODO
		indirect.writeTo(writer.getOs());
		System.out.println("[PDFWriter] writeTo OutStream End<<<<<<<<<<<<<<<<<<<<");//TODO
		position = writer.getOs().getCounter();
	}


	public void writeCrossReferenceTable(final OutputStream os, final PDFIndirectReference root, final PDFIndirectReference info, final PDFIndirectReference encryption, final PDFObj fileID, final long prevxref) throws IOException {
		int refNumber = 0;
		PDFCrossReference entry = xrefs.first();
		int first = entry.getRefnum();
		int len = 0;
		ArrayList<Integer> sections = new ArrayList<Integer>();
		for (PDFCrossReference PDFCrossReference : xrefs) {
			entry = PDFCrossReference;
			if (first + len == entry.getRefnum())
				++len;
			else {
				sections.add(Integer.valueOf(first));
				sections.add(Integer.valueOf(len));
				first = entry.getRefnum();
				len = 1;
			}
		}
		sections.add(Integer.valueOf(first));
		sections.add(Integer.valueOf(len));
		System.out.print("xref\n");//TODO
		os.write(StringUtils.getISOBytes("xref\n"));
		Iterator<PDFCrossReference> i = xrefs.iterator();
		for (int k = 0; k < sections.size(); k += 2) {
			first = sections.get(k).intValue();
			len = sections.get(k + 1).intValue();
			os.write(StringUtils.getISOBytes(String.valueOf(first)));
			os.write(StringUtils.getISOBytes(" "));
			os.write(StringUtils.getISOBytes(String.valueOf(len)));
			os.write('\n');
			System.out.print(String.valueOf(first) + " " + String.valueOf(len) + "\n");//TODO
			while (len-- > 0) {
				entry = i.next();
				entry.write(os);
			}
		}
	}


}
