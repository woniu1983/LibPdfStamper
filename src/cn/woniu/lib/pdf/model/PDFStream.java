/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import cn.woniu.lib.pdf.PDFReader;
import cn.woniu.lib.pdf.PDFWriter;
import cn.woniu.lib.pdf.encode.OutputStreamCounter;
import cn.woniu.lib.pdf.exception.ExceptionConverter;


/** 
 * @ClassName: PDFStream <br/> 
 * 
 * @since JDK 1.6 <BR>
 * @Description   
 * A stream, like a string, is a sequence of characters. However, an application can
 * read a small portion of a stream at a time, while a string must be read in its entirety.
 * For this reason, objects with potentially large amounts of data, such as images and
 * page descriptions, are represented as streams.<BR>
 * A stream consists of a dictionary that describes a sequence of characters, followed by
 * the keyword <B>stream</B>, followed by zero or more lines of characters, followed by
 * the keyword <B>endstream</B>.<BR><BR>
 * 
 * All streams must be <CODE>PdfIndirectObject</CODE>s. The stream dictionary must be a direct
 * object. The keyword <B>stream</B> that follows the stream dictionary should be followed by
 * a carriage return and linefeed or just a linefeed.<BR>
 * Remark: In this version only the FLATEDECODE-filter is supported.<BR>
 * 
 * EXAMPLE： <BR>
 * 		 		<I>dictionary</I><BR>
 * 				<B>stream</B><BR>
 * 				...Zero or more bytes<BR>
 * 				<B>endstream</B><BR>
 */
public class PDFStream extends PDFDictionary {

	private static final long serialVersionUID = 8438500001574814678L;

	/* compression levels */
	public static final int DEFAULT_COMPRESSION = -1;
	public static final int NO_COMPRESSION = 0;
	public static final int BEST_SPEED = 1;
	public static final int BEST_COMPRESSION = 9;


	protected boolean compressed = false;
	protected int compressionLevel = NO_COMPRESSION;

	protected ByteArrayOutputStream streamBytes = null;
	protected InputStream inputStream;
	protected PDFIndirectReference ref;
	protected int inputStreamLength = -1;
	protected PDFWriter writer;
	protected int rawLength;

	static final byte STARTSTREAM[] = getISOBytes("stream\n");
	static final byte ENDSTREAM[] = getISOBytes("\nendstream");
	static final int SIZESTREAM = STARTSTREAM.length + ENDSTREAM.length;

	public PDFStream(byte[] bytes) {
		super();
		type = STREAM;
		this.bytes = bytes;
		rawLength = bytes.length;
		put(PDFName.LENGTH, new PDFNumeric(bytes.length));
	}

	public PDFStream(InputStream inputStream, PDFWriter writer) {
		super();
		type = STREAM;
		this.inputStream = inputStream;
		this.writer = writer;
		//		ref = writer.getPdfIndirectReference();//TODO
		put(PDFName.LENGTH, ref);
	}

	/**
	 * Constructs a <CODE>PdfStream</CODE>-object.
	 */

	protected PDFStream() {
		super();
		type = STREAM;
	}

	//	public void writeLength() throws IOException {
	//        if (inputStream == null)
	//            throw new UnsupportedOperationException("writelength.can.only.be.called.in.a.contructed.pdfstream.inputstream.PDFWriter");
	//        if (inputStreamLength == -1)
	//            throw new IOException("writelength.can.only.be.called.after.output.of.the.stream.body");
	//        writer.addToBody(new PDFNumeric(inputStreamLength), ref, false);
	//    }

	/**
	 * Gets the raw length of the stream.
	 * @return the raw length of the stream
	 */
	public int getRawLength() {
		return rawLength;
	}

	/**
	 * Compresses the stream.
	 */
	public void flateCompress() {
		flateCompress(DEFAULT_COMPRESSION);
	}

	/**
	 * Compresses the stream.
	 * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
	 */
	public void flateCompress(int compressionLevel) {
		// check if the flateCompress-method has already been
		if (compressed) {
			return;
		}
		this.compressionLevel = compressionLevel;
		if (inputStream != null) {
			compressed = true;
			return;
		}
		// check if a filter already exists
		PDFObj filter = PDFReader.getPdfObject(get(PDFName.FILTER));
		if (filter != null) {
			if (filter.isName()) {
				if (PDFName.FLATEDECODE.equals(filter))
					return;
			}
			else if (filter.isArray()) {
				if (((PDFArray) filter).contains(PDFName.FLATEDECODE))
					return;
			}
			else {
				throw new RuntimeException("stream.could.not.be.compressed.filter.is.not.a.name.or.array");
			}
		}
		try {
			// compress
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Deflater deflater = new Deflater(compressionLevel);
			DeflaterOutputStream zip = new DeflaterOutputStream(stream, deflater);
			if (streamBytes != null)
				streamBytes.writeTo(zip);
			else
				zip.write(bytes);
			zip.close();
			deflater.end();
			// update the object
			streamBytes = stream;
			bytes = null;
			put(PDFName.LENGTH, new PDFNumeric(streamBytes.size()));
			if (filter == null) {
				put(PDFName.FILTER, PDFName.FLATEDECODE);
			}
			else {
				PDFArray filters = new PDFArray(filter);
				filters.add(0, PDFName.FLATEDECODE);
				put(PDFName.FILTER, filters);
			}
			compressed = true;
		}
		catch(IOException ioe) {
			throw new ExceptionConverter(ioe);
		}
	}
	
	protected void superToPdf(OutputStream os) throws IOException {
		super.write(os);
    }


	@Override
	public void write(OutputStream os) throws IOException {
		if (inputStream != null && compressed) {
			put(PDFName.FILTER, PDFName.FLATEDECODE);
		}

		superToPdf(os);
		os.write(STARTSTREAM);
		
		if (inputStream != null) {
			rawLength = 0;
			DeflaterOutputStream def = null;
			OutputStreamCounter osc = new OutputStreamCounter(os);
			OutputStream fout = osc;
			Deflater deflater = null;
			if (compressed) {
				deflater = new Deflater(compressionLevel);
				fout = def = new DeflaterOutputStream(fout, deflater, 0x8000);
			}

			byte buf[] = new byte[4192];
			while (true) {
				int n = inputStream.read(buf);
				if (n <= 0)
					break;
				fout.write(buf, 0, n);
				rawLength += n;
			}
			if (def != null) {
				def.finish();
				deflater.end();
			}
			inputStreamLength = (int)osc.getCounter();

		} else {
			if (streamBytes != null) {
				streamBytes.writeTo(os);
			} else {
				os.write(bytes);
			}
		}
		os.write(ENDSTREAM);
	}

	/**
	 * Writes the data content to an <CODE>OutputStream</CODE>.
	 * @param os the destination to write to
	 * @throws IOException on error
	 */    
	public void writeContent(OutputStream os) throws IOException {
		if (streamBytes != null)
			streamBytes.writeTo(os);
		else if (bytes != null)
			os.write(bytes);
	}

	/**
	 * @see com.itextpdf.text.pdf.PDFObj#toString()
	 */
	public String toString() {
		if (get(PDFName.TYPE) == null) return "Stream";
		return "Stream of type: " + get(PDFName.TYPE);
	}


	/** Converts a <CODE>String</CODE> into a <CODE>Byte</CODE> array
	 * according to the ISO-8859-1 codepage.
	 * @param text the text to be converted
	 * @return the conversion result
	 */
	public static final byte[] getISOBytes(String text) {
		if (text == null)
			return null;
		int len = text.length();
		byte b[] = new byte[len];
		for (int k = 0; k < len; ++k)
			b[k] = (byte)text.charAt(k);
		return b;
	}

}
