/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model;

import cn.woniu.lib.pdf.exception.PDFException;

/** 
 * @ClassName: PDFBoolean <br/> 
 * @Description: <CODE>PdfBoolean</CODE> is the boolean object represented by the keywords <VAR>true</VAR> or <VAR>false</VAR>.  <br/> 
 * 
 * @version  
 * @since JDK 1.6 
 */
public class PDFBoolean extends PDFObj {

	/** 
	 * serialVersionUID:TODO  
	 */ 
	private static final long serialVersionUID = 4518599742782231153L;
	
	public static final String TRUE = "true";

	public static final String FALSE = "false";
	
    private boolean value;

	public PDFBoolean(boolean value) {
		super(BOOLEAN);
		if (value) {
			setContent(TRUE);
		}
		else {
			setContent(FALSE);
		}
		this.value = value;
	}
	
	public PDFBoolean(String value) throws PDFException {
        super(BOOLEAN, value);
        if (value.equals(TRUE)) {
            this.value = true;
        }
        else if (value.equals(FALSE)) {
            this.value = false;
        }
        else {
            throw new PDFException("PDFBoolean only support true or false, instead of: " + value);
        }
    }
	
	public boolean booleanValue() {
        return value;
    }
    
    public String toString() {
    	return value ? TRUE : FALSE;
    }
}
