/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.image;

import java.io.ByteArrayOutputStream;

/** 
 * @ClassName: SimpleByteArrayOutputStream <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月13日 下午9:33:56 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class SimpleByteArrayOutputStream extends ByteArrayOutputStream {
	
    public byte[] getBuf() {
        return this.buf;
    }

}
