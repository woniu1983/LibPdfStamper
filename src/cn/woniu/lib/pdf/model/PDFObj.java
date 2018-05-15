/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import cn.woniu.lib.pdf.encode.PdfEncodings;
import cn.woniu.lib.pdf.model.derivate.PRIndirectReference;

/** 
 * @ClassName: PDFObj <br/> 
 * 
 * @since JDK 1.6 
 * @Description: PDF对象，子类参考PDF32000_2008.pdf中7.3章节的定义 ,8种基本类型和1个Null类型 <br/>
 * 
 * @see PDFBoolean
 * @see PDFNumeric
 * @see PDFString
 * @see PDFName
 * @see PDFArray
 * @see PDFDictionary
 * @see PDFStream
 * @see PDFIndirectReference
 * @see PDFNull
 * @see PDFIndirectObject (没有继承PDFObj)
 * 
 * 
 */
public abstract class PDFObj implements Serializable {
    /** 
	 * serialVersionUID:TODO  
	 */ 
	private static final long serialVersionUID = -1425792666409336304L;

	/* PDFObject Type */

	/** Means <CODE>PDFBoolean</CODE> type */
    public static final int BOOLEAN = 1;

    /** Means <CODE>PDFNumeric</CODE> type */
    public static final int NUMBER = 2;

    /** Means <CODE>PDFString</CODE> type */
    public static final int STRING = 3;

    /** Means <CODE>PDFName</CODE> type */
    public static final int NAME = 4;

    /** Means <CODE>PDFArray</CODE> type */
    public static final int ARRAY = 5;

    /** Means <CODE>PDFDictionary</CODE> type */
    public static final int DICTIONARY = 6;

    /** Means <CODE>PDFStream</CODE> type */
    public static final int STREAM = 7;

    /** Means <CODE>PDFNull</CODE> type */
    public static final int NULL = 8;

    /** Means <CODE>PDFIndirect</CODE> type */
    public static final int INDIRECT = 10;

    /** An empty string used for the <CODE>PDFNull</CODE>-object and for an empty <CODE>PDFString</CODE>-object. */
    public static final String NOTHING = "";

    /**
     * This is the default encoding to be used for converting Strings into
     * bytes and vice versa. The default encoding is PdfDocEncoding.
     */
    public static final String TEXT_PDFDOCENCODING = "PDF";

    /** This is the encoding to be used to output text in Unicode. */
    public static final String TEXT_UNICODE = "UnicodeBig";
    
    


    /** The type of this <CODE>PDFObj</CODE> */
    protected int type;

    /** The content of this <CODE>PDFObj</CODE> */
    protected byte[] bytes;

    /** Holds the indirect reference. */
    protected PRIndirectReference indRef;
    
    /**
     * 
     * <p>Title: </p>  
     * <p>Description: </p>  
     * @param type
     */
    protected PDFObj(int type) {
        this.type = type;
    }

    /**
     * 
     * <p>Title: </p>  
     * <p>Description: </p>  
     * @param type
     * @param content
     */
    protected PDFObj(int type, String content) {
        this.type = type;
        bytes = PdfEncodings.convertToBytes(content, null);
    }
    
    protected PDFObj(int type, byte[] bytes) {
        this.bytes = bytes;
        this.type = type;
    }
    
    protected void setContent(String content) {
        bytes = PdfEncodings.convertToBytes(content, null);
    }

    // methods dealing with the type of this object

    /**
     * Returns the type of this <CODE>PDFObject</CODE>.
     * 
     * May be either of:<BR>
     * - <VAR>BOOLEAN=1</VAR>:  	<CODE>PDFBoolean</CODE><BR>
     * - <VAR>NUMBER=2</VAR>:  	<CODE>PDFNumeric</CODE><BR>
     * - <VAR>STRING=3</VAR>:  	<CODE>PDFString</CODE><BR>
     * - <VAR>NAME=4</VAR>:  	<CODE>PDFName</CODE><BR>
     * - <VAR>ARRAY=5</VAR>:  	<CODE>PDFArray</CODE><BR>
     * - <VAR>DICTIONARY=6</VAR>:  <CODE>PDFDictionary</CODE><BR>
     * - <VAR>STREAM=7</VAR>:  	<CODE>PDFStream</CODE><BR>
     * - <VAR>NULL=8</VAR>:  	<CODE>PDFNull</CODE><BR>
     * - <VAR>INDIRECT=10</VAR>: <CODE>PDFIndirectObject</CODE><BR>
     *
     * @return The type
     */
    public int type() {
        return type;
    }

    /**
     * Checks if this <CODE>PDFObject</CODE> is of the type
     * <CODE>PdfNull</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isNull() {
        return (type == NULL);
    }

    /**
     * Checks if this <CODE>PDFObject</CODE> is of the type
     * <CODE>PdfBoolean</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isBoolean() {
        return (type == BOOLEAN);
    }

    /**
     * Checks if this <CODE>PDFObject</CODE> is of the type
     * <CODE>PDFNumber</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isNumber() {
        return (type == NUMBER);
    }

    /**
     * Checks if this <CODE>PDFObject</CODE> is of the type
     * <CODE>PDFString</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isString() {
        return (type == STRING);
    }

    /**
     * Checks if this <CODE>PDFObject</CODE> is of the type
     * <CODE>PdfName</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isName() {
        return (type == NAME);
    }

    /**
     * Checks if this <CODE>PDFObject</CODE> is of the type
     * <CODE>PDFArray</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isArray() {
        return (type == ARRAY);
    }

    /**
     * Checks if this <CODE>PDFObject</CODE> is of the type
     * <CODE>PDFDictionary</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isDictionary() {
        return (type == DICTIONARY);
    }

    /**
     * Checks if this <CODE>PDFObject</CODE> is of the type
     * <CODE>PDFStream</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isStream() {
        return (type == STREAM);
    }

    /**
     * Checks if this <CODE>PDFObject</CODE> is of the type
     * <CODE>PRIndirectReference</CODE>.
     * 
     * @return <CODE>true</CODE> if this is an indirect object,
     *   otherwise <CODE>false</CODE>
     */
    public boolean isIndirect() {
        return (type == INDIRECT);
    }

    /**
     * Get the indirect reference
     * 
     * @return A <CODE>PDFIndirectReference</CODE>
     */
    public PRIndirectReference getIndRef() {
        return indRef;
    }

    /**
     * Set the indirect reference
     * 
     * @param indRef New value as a <CODE>PdfIndirectReference</CODE>
     */
    public void setIndRef(PRIndirectReference indRef) {
        this.indRef = indRef;
    }

    /**
     * Gets the presentation of this object in a byte array
     * 
     * @return a byte array
     */
    public byte[] getBytes() {
        return bytes;
    }
    
    public void write(OutputStream os) throws IOException {
        if (bytes != null) {
            os.write(bytes);
        }
    }

    public String toString() {
        if (bytes == null)
            return super.toString();
        return PdfEncodings.convertToString(bytes, null);
    }
}
