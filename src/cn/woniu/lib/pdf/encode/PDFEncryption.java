/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.encode;

import java.io.IOException;
import java.security.MessageDigest;

import cn.woniu.lib.pdf.model.PDFObj;
import cn.woniu.lib.pdf.model.derivate.PDFLiteral;

/** 
 * @ClassName: PDFEncryption <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月11日 上午11:17:37 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFEncryption {

	private static long seq = System.currentTimeMillis();
	
	public static PDFObj createInfoId(byte id[], boolean modified) throws IOException {
		ByteBuffer buf = new ByteBuffer(90);
		if (id.length == 0)
			id = createDocumentId();
		buf.append('[').append('<');
		for (int k = 0; k < id.length; ++k)
			buf.appendHex(id[k]);
		buf.append('>').append('<');
		if (modified)
			id = createDocumentId();
		for (int k = 0; k < id.length; ++k)
			buf.appendHex(id[k]);
		buf.append('>').append(']');
		buf.close();
		return new PDFLiteral(buf.toByteArray());
	}

	public static byte[] createDocumentId() {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		long time = System.currentTimeMillis();
		long mem = Runtime.getRuntime().freeMemory();
		String s = time + "+" + mem + "+" + (seq++);
		return md5.digest(s.getBytes());
	}

}
