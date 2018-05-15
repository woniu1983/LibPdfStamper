/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.arc;

import java.io.IOException;
import java.io.OutputStream;

import cn.woniu.lib.pdf.util.PDFConstant;
import cn.woniu.lib.pdf.util.StringUtils;

/** 
 * @ClassName: PDFCrossReference <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月14日 下午7:17:46 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFCrossReference implements Comparable<PDFCrossReference> {

	protected final int type;

	/**	Byte offset in the PDF file. */
	protected final long offset;

	protected final int refnum;

	/**	generation of the object. */
	protected final int generation;

	public PDFCrossReference(final int refnum, final long offset, final int generation) {
		type = 0;
		this.offset = offset;
		this.refnum = refnum;
		this.generation = generation;
	}

	/**
	 * Constructs a cross-reference element for a PdfIndirectObject.
	 * @param refnum
	 * @param	offset		byte offset of the object
	 */

	public PDFCrossReference(final int refnum, final long offset) {
		type = 1;
		this.offset = offset;
		this.refnum = refnum;
		this.generation = 0;
	}

	public PDFCrossReference(final int type, final int refnum, final long offset, final int generation) {
		this.type = type;
		this.offset = offset;
		this.refnum = refnum;
		this.generation = generation;
	}

    public int getRefnum() {
        return refnum;
    }

	public int compareTo(final PDFCrossReference other) {
		return refnum < other.refnum ? -1 : refnum==other.refnum ? 0 : 1;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof PDFCrossReference) {
			PDFCrossReference other = (PDFCrossReference)obj;
			return refnum == other.refnum;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return refnum;
	}

    /**
     * Returns the PDF representation of this <CODE>PdfObject</CODE>.
     * @param os
     * @throws IOException
     */
    public void write(final OutputStream os) throws IOException {
        StringBuffer off = new StringBuffer("0000000000").append(offset);
        off.delete(0, off.length() - 10);
        StringBuffer gen = new StringBuffer("00000").append(generation);
        gen.delete(0, gen.length() - 5);

        off.append(' ').append(gen).append(generation == PDFConstant.MAX_GEN ? " f \n" : " n \n");
        os.write(StringUtils.getISOBytes(off.toString()));
        
        System.out.print(off.toString());//TODO
    }

    /**
     * Writes PDF syntax to the OutputStream
     * @param midSize
     * @param os
     * @throws IOException
     */
    public void write(int midSize, final OutputStream os) throws IOException {
        os.write((byte)type);
        while (--midSize >= 0) {
            os.write((byte)(offset >>> 8 * midSize & 0xff));
        }
        os.write((byte)(generation >>> 8 & 0xff));
        os.write((byte)(generation & 0xff));
    }

}
