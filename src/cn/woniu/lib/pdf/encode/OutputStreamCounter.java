/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.encode;

import java.io.IOException;
import java.io.OutputStream;

/** 
 * @ClassName: OutputStreamCounter <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月10日 下午7:41:50 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class OutputStreamCounter extends OutputStream {
    
    protected OutputStream out;
    
    /**
     * Record written bytes[] length.
     */
    protected long counter = 0;
    
    /** Creates a new instance of OutputStreamCounter */
    public OutputStreamCounter(OutputStream out) {
        this.out = out;
    }
    
    public void close() throws IOException {
        out.close();
    }
    
    public void flush() throws IOException {
        out.flush();
    }
    
    /** Writes <code>b.length</code> bytes from the specified byte array
     * to this output stream. The general contract for <code>write(b)</code>
     * is that it should have exactly the same effect as the call
     * <code>write(b, 0, b.length)</code>.
     *
     * @param      b   the data.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.OutputStream#write(byte[], int, int)
     *
     */
    public void write(byte[] b) throws IOException {
        counter += b.length;
        out.write(b);
    }
    
    /** Writes the specified byte to this output stream. The general
     * contract for <code>write</code> is that one byte is written
     * to the output stream. The byte to be written is the eight
     * low-order bits of the argument <code>b</code>. The 24
     * high-order bits of <code>b</code> are ignored.
     * <p>
     * Subclasses of <code>OutputStream</code> must provide an
     * implementation for this method.
     *
     * @param      b   the <code>byte</code>.
     * @exception  IOException  if an I/O error occurs. In particular,
     *             an <code>IOException</code> may be thrown if the
     *             output stream has been closed.
     *
     */
    public void write(int b) throws IOException {
        ++counter;
        out.write(b);
    }
    
    /** Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to this output stream.
     * The general contract for <code>write(b, off, len)</code> is that
     * some of the bytes in the array <code>b</code> are written to the
     * output stream in order; element <code>b[off]</code> is the first
     * byte written and <code>b[off+len-1]</code> is the last byte written
     * by this operation.
     * <p>
     * The <code>write</code> method of <code>OutputStream</code> calls
     * the write method of one argument on each of the bytes to be
     * written out. Subclasses are encouraged to override this method and
     * provide a more efficient implementation.
     * <p>
     * If <code>b</code> is <code>null</code>, a
     * <code>NullPointerException</code> is thrown.
     * <p>
     * If <code>off</code> is negative, or <code>len</code> is negative, or
     * <code>off+len</code> is greater than the length of the array
     * <code>b</code>, then an <tt>IndexOutOfBoundsException</tt> is thrown.
     *
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs. In particular,
     *             an <code>IOException</code> is thrown if the output
     *             stream is closed.
     *
     */
    public void write(byte[] b, int off, int len) throws IOException {
        counter += len;
        out.write(b, off, len);
    }
    
    public long getCounter() {
        return counter;
    }
    
    public void resetCounter() {
        counter = 0;
    }}
