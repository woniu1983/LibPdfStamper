/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.io;

import java.io.IOException;
import java.io.OutputStream;

/** 
 * @ClassName: CounterOutputStream <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月14日 下午2:30:34 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class CounterOutputStream extends OutputStream {


	protected OutputStream out;

	protected long counter = 0L;

	public CounterOutputStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void write(byte[] bytes) throws IOException {
        this.out.write(bytes);
        this.counter += bytes.length;
	}
	
	@Override
	public void write(int b) throws IOException {
        ++counter;
        this.out.write(b);
    }
	
	@Override
    public void write(byte[] bytes, int off, int len) throws IOException {
		this.counter += len;
        this.out.write(bytes, off, len);
    }

	@Override
	public void close() throws IOException {
		this.out.close();
	}

	@Override
	public void flush() throws IOException {
		this.out.flush();
	}
    
    public long getCounter() {
        return this.counter;
    }
    
    public void resetCounter() {
    	this.counter = 0;
    }

}
