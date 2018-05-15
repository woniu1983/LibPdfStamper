/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model;

import java.io.IOException;
import java.io.OutputStream;

import cn.woniu.lib.pdf.encode.ByteBuffer;
import cn.woniu.lib.pdf.encode.PdfEncodings;
import cn.woniu.lib.pdf.util.StringUtils;

/** 
 * @ClassName: PDFString <br/> 
 * @Description <br/> 
 * 
 * A <CODE>PDFString</CODE>-class is the PDF-equivalent of a
 * JAVA-<CODE>String</CODE>-object.
 * <P>
 * A string object consists of a series of bytes
 * - unsigned integer values in the range of 0 to 255 
 * - delimited by parenthesis * 
 * String objects are not integer objects, but are stored in a more compact format.
 * String objects can be written in two ways.
 * - Literal String:    (helloworld)
 * - Hex String    :    <901FA3>
 * If a string is too long to be conveniently placed on a single line, it may
 * be split across multiple lines by using the backslash character (\) at the
 * end of a line to indicate that the string continues on the following line.
 * Within a string, the backslash character is used as an escape to specify
 * unbalanced parenthesis, non-printing ASCII characters, and the backslash
 * character itself. Use of the \<I>ddd</I> escape sequence is the preferred
 * way to represent characters outside the printable ASCII character set.<BR>
 * 
 * @version  
 * @since JDK 1.6 
 */
public class PDFString extends PDFObj {

	private static final long serialVersionUID = -7104936244693143451L;
    
    /** The value of this object. */
    protected String value = NOTHING;
    
    protected String originalValue = null;
    
    protected String encoding = TEXT_PDFDOCENCODING;
    
    protected int objNum = 0;
    
    protected int objGen = 0;
    
    protected boolean hexWriting = false;

    /**
     * Constructs an empty <CODE>PDFString</CODE>-object.
     */
    public PDFString() {
        super(STRING);
    }
    
    /**
     * Constructs a <CODE>PDFString</CODE>-object containing a string in the
     * standard encoding <CODE>TEXT_PDFDOCENCODING</CODE>.
     *
     * @param value    the content of the string
     */
    public PDFString(String value) {
        super(STRING);
        this.value = value;
    }
    
    /**
     * Constructs a <CODE>PDFString</CODE>-object containing a string in the
     * specified encoding.
     *
     * @param value    the content of the string
     * @param encoding an encoding
     */
    public PDFString(String value, String encoding) {
        super(STRING);
        this.value = value;
        this.encoding = encoding;
    }
    
    /**
     * Constructs a <CODE>PDFString</CODE>-object.
     *
     * @param bytes    an array of <CODE>byte</CODE>
     */
    public PDFString(byte[] bytes) {
        super(STRING);
        value = PdfEncodings.convertToString(bytes, null);
        encoding = NOTHING;
    }
    
    /**
     * Returns the <CODE>String</CODE> value of this <CODE>PdfString</CODE>-object.
     *
     * @return A <CODE>String</CODE>
     */
    public String toString() {
        return value;
    }
    
    public byte[] getBytes() {
        if (bytes == null) {
            if (encoding != null && encoding.equals(TEXT_UNICODE) && PdfEncodings.isPdfDocEncoding(value))
                bytes = PdfEncodings.convertToBytes(value, TEXT_PDFDOCENCODING);
            else
                bytes = PdfEncodings.convertToBytes(value, encoding);
        }
        return bytes;
    }
    
    // other methods
    
    /**
     * Returns the Unicode <CODE>String</CODE> value of this
     * <CODE>PdfString</CODE>-object.
     *
     * @return A <CODE>String</CODE>
     */
    public String toUnicodeString() {
        if (encoding != null && encoding.length() != 0)
            return value;
        getBytes();
        if (bytes.length >= 2 && bytes[0] == (byte)254 && bytes[1] == (byte)255) {
            return PdfEncodings.convertToString(bytes, PDFObj.TEXT_UNICODE);
        } else {
            return PdfEncodings.convertToString(bytes, PDFObj.TEXT_PDFDOCENCODING);
        }
    }
    
    /**
     * Gets the encoding of this string.
     *
     * @return a <CODE>String</CODE>
     */
    public String getEncoding() {
        return encoding;
    }
    
    public void setObjNum(int objNum, int objGen) {
        this.objNum = objNum;
        this.objGen = objGen;
    }
   
    public byte[] getOriginalBytes() {
        if (originalValue == null)
            return getBytes();
        return PdfEncodings.convertToBytes(originalValue, null);
    }
    
    public PDFString setHexWriting(boolean hexWriting) {
        this.hexWriting = hexWriting;
        return this;
    }
    
    public boolean isHexWriting() {
        return hexWriting;
    }
    
    /**
     * Writes the PDF representation of this <CODE>PdfString</CODE> as an array
     * of <CODE>byte</CODE> to the specified <CODE>OutputStream</CODE>.
     * 
     * @param writer for backwards compatibility
     * @param os The <CODE>OutputStream</CODE> to write the bytes to.
     */
    @Override
    public void write(OutputStream os) throws IOException {
        byte b[] = getBytes();
        if (hexWriting) {
            ByteBuffer buf = new ByteBuffer();
            buf.append('<');
            int len = b.length;
            for (int k = 0; k < len; ++k)
                buf.appendHex(b[k]);
            buf.append('>');
            os.write(buf.toByteArray());
        } else {
        	os.write(StringUtils.escapeString(b));
        }
            
    }
    
//    /**
//     * Decrypt an encrypted <CODE>PdfString</CODE>
//     */
//    void decrypt(PdfReader reader) {
//        PdfEncryption decrypt = reader.getDecrypt();
//        if (decrypt != null) {
//            originalValue = value;
//            decrypt.setHashKey(objNum, objGen);
//            bytes = PdfEncodings.convertToBytes(value, null);
//            bytes = decrypt.decryptByteArray(bytes);
//            value = PdfEncodings.convertToString(bytes, null);
//        }
//    }

}
