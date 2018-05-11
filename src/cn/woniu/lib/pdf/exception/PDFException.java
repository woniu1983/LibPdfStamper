package cn.woniu.lib.pdf.exception;

public class PDFException extends Exception {

	/** 
	 * serialVersionUID:TODO  
	 */ 
	private static final long serialVersionUID = -4481169434722589325L;

	public PDFException(Exception ex) {
		super(ex);
	}

	public PDFException() {
		super();
	}

	public PDFException(String message) {
		super(message);
	}

}
