/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model.derivate;

import java.io.IOException;
import java.io.OutputStream;

import cn.woniu.lib.pdf.encode.OutputStreamCounter;
import cn.woniu.lib.pdf.model.PDFObj;

/** 
 * @ClassName: PDFLiteral <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月13日 下午4:33:35 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFLiteral extends PDFObj {
    
    /**
     * Holds value of property position.
     */
    private long position;
        
    public PDFLiteral(String text) {
        super(0, text);
    }
    
    public PDFLiteral(byte b[]) {
        super(0, b);
    }

    public PDFLiteral(int size) {
        super(0, (byte[])null);
        bytes = new byte[size];
        java.util.Arrays.fill(bytes, (byte)32);
    }

    public PDFLiteral(int type, String text) {
        super(type, text);
    }
    
    public PDFLiteral(int type, byte b[]) {
        super(type, b);
    }
    
    /**
     * Getter for property position.
     * @return Value of property position.
     */
    public long getPosition() {
        return this.position;
    }
    
    /**
     * Getter for property posLength.
     * @return Value of property posLength.
     */
    public int getPosLength() {
        if (bytes != null)
            return bytes.length;
        else
            return 0;
    }
    
    @Override
    public void write(OutputStream os) throws IOException {
        if (os instanceof OutputStreamCounter) {
            position = ((OutputStreamCounter)os).getCounter();
        }
        super.write(os);
    }
    
}
