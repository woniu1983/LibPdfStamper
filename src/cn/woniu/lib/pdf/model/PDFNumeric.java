/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model;

import cn.woniu.lib.pdf.encode.ByteBuffer;

/** 
 * @ClassName: PDFNumeric <br/> 
 * @Description: <CODE>PDFNumeric</CODE> provides two types of numbers, integer and real.
 * <P>
 * Integers may be specified by signed or unsigned constants.
 * Reals may only be in decimal format.<BR>
 * 
 * @since JDK 1.6 
 */
public class PDFNumeric extends PDFObj {

	private static final long serialVersionUID = -3707997111719889093L;
	

    private double value;

	protected PDFNumeric(int value) {
        super(NUMBER);
        this.value = value;
        setContent(String.valueOf(value));
	}
	
    public PDFNumeric(long value) {
        super(NUMBER);
        this.value = value;
        setContent(String.valueOf(value));
    }
    
    public PDFNumeric(String content) {
        super(NUMBER);
        try {
            value = Double.parseDouble(content.trim());
            setContent(content);
        }
        catch (NumberFormatException nfe){
            throw new RuntimeException(content + " is invalid numeric.");
        }
    }
    
    public PDFNumeric(double value) {
        super(NUMBER);
        this.value = value;
        setContent(ByteBuffer.formatDouble(value));
    }
    
    public PDFNumeric(float value) {
        this((double)value);
    }
    
    // methods returning the value of this object
    
    /**
     * Returns the primitive <CODE>int</CODE> value of this object.
     *
     * @return The value as <CODE>int</CODE>
     */
    public int intValue() {
        return (int) value;
    }
    
    public long longValue() {
        return (long) value;
    }
    
    public double doubleValue() {
        return value;
    }
    
    public float floatValue() {
        return (float)value;
    }
    
    public void increment() {
        value += 1.0;
        setContent(ByteBuffer.formatDouble(value));
    }

}
