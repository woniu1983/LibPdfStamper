/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.image;

import java.io.IOException;
import java.io.InputStream;

/** 
 * @ClassName: PngUtil <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月13日 下午9:31:42 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PngUtil {

	/**
	 * Gets an <CODE>int</CODE> from an <CODE>InputStream</CODE>.
	 *
	 * @param		is      an <CODE>InputStream</CODE>
	 * @return		the value of an <CODE>int</CODE>
	 */

	public static final int getInt(InputStream is) throws IOException {
		return (is.read() << 24) + (is.read() << 16) + (is.read() << 8) + is.read();
	}

	/**
	 * Gets a <CODE>word</CODE> from an <CODE>InputStream</CODE>.
	 *
	 * @param		is      an <CODE>InputStream</CODE>
	 * @return		the value of an <CODE>int</CODE>
	 */

	public static final int getWord(InputStream is) throws IOException {
		return (is.read() << 8) + is.read();
	}

	/**
	 * Gets a <CODE>String</CODE> from an <CODE>InputStream</CODE>.
	 *
	 * @param		is      an <CODE>InputStream</CODE>
	 * @return		the value of an <CODE>int</CODE>
	 */

	public static final String getString(InputStream is) throws IOException {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			buf.append((char)is.read());
		}
		return buf.toString();
	}

    
	public static final boolean checkMarker(String s) {
        if (s.length() != 4)
            return false;
        for (int k = 0; k < 4; ++k) {
            char c = s.charAt(k);
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z'))
                return false;
        }
        return true;
    }

	/**
	 * This method is an alternative for the <CODE>InputStream.skip()</CODE>
	 * -method that doesn't seem to work properly for big values of <CODE>size
	 * </CODE>.
	 *
	 * @param is
	 *            the <CODE>InputStream</CODE>
	 * @param size
	 *            the number of bytes to skip
	 * @throws IOException
	 */
	public static void skip(final InputStream is, int size) throws IOException {
	    long n;
		while (size > 0) {
	        n = is.skip(size);
	        if (n <= 0)
	            break;
			size -= n;
		}
	}
}
