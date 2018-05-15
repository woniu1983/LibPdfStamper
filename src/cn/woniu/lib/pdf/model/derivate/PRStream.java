/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model.derivate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import cn.woniu.lib.pdf.PDFReader;
import cn.woniu.lib.pdf.model.PDFDictionary;
import cn.woniu.lib.pdf.model.PDFName;
import cn.woniu.lib.pdf.model.PDFNumeric;
import cn.woniu.lib.pdf.model.PDFStream;
import cn.woniu.lib.pdf.util.PDFConstant;


/** 
 * @ClassName: PRStream <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月13日 下午4:26:50 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PRStream extends PDFStream {
    
    protected PDFReader reader;
    protected long offset;
    protected int length;
    
    //added by ujihara for decryption
    protected int objNum = 0;
    protected int objGen = 0;
    
    public PRStream(PRStream stream, PDFDictionary newDic) {
        reader = stream.reader;
        offset = stream.offset;
        length = stream.length;
        compressed = stream.compressed;
        compressionLevel = stream.compressionLevel;
        streamBytes = stream.streamBytes;
        bytes = stream.bytes;
        objNum = stream.objNum;
        objGen = stream.objGen;
        if (newDic != null)
            putAll(newDic);
        else
            hashMap.putAll(stream.hashMap);
    }

    public PRStream(PRStream stream, PDFDictionary newDic, PDFReader reader) {
        this(stream, newDic);
        this.reader = reader;
    }

    public PRStream(PDFReader reader, long offset) {
        this.reader = reader;
        this.offset = offset;
    }

    public PRStream(PDFReader reader, byte conts[]) {
    	this(reader, conts, DEFAULT_COMPRESSION);
    }

    /**
     * Creates a new PDF stream object that will replace a stream
     * in a existing PDF file.
     * @param	reader	the reader that holds the existing PDF
     * @param	conts	the new content
     * @param	compressionLevel	the compression level for the content
     * @since	2.1.3 (replacing the existing constructor without param compressionLevel)
     */
    public PRStream(PDFReader reader, byte[] conts, int compressionLevel) {
        this.reader = reader;
        this.offset = -1;
        if (PDFConstant.compress) {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Deflater deflater = new Deflater(compressionLevel);
                DeflaterOutputStream zip = new DeflaterOutputStream(stream, deflater);
                zip.write(conts);
                zip.close();
                deflater.end();
                bytes = stream.toByteArray();
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            put(PDFName.FILTER, PDFName.FLATEDECODE);
        }
        else
            bytes = conts;
        setLength(bytes.length);
    }
    
    /**
     * Sets the data associated with the stream, either compressed or
     * uncompressed. Note that the data will never be compressed if
     * Document.compress is set to false.
     * 
     * @param data raw data, decrypted and uncompressed.
     * @param compress true if you want the stream to be compressed.
     * @since	iText 2.1.1
     */
    public void setData(byte[] data, boolean compress) {
    	setData(data, compress, DEFAULT_COMPRESSION);
    }
    
    /**
     * Sets the data associated with the stream, either compressed or
     * uncompressed. Note that the data will never be compressed if
     * Document.compress is set to false.
     * 
     * @param data raw data, decrypted and uncompressed.
     * @param compress true if you want the stream to be compressed.
     * @param compressionLevel	a value between -1 and 9 (ignored if compress == false)
     * @since	iText 2.1.3
     */
    public void setData(byte[] data, boolean compress, int compressionLevel) {
        remove(PDFName.FILTER);
        this.offset = -1;
        if (PDFConstant.compress && compress) {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Deflater deflater = new Deflater(compressionLevel);
                DeflaterOutputStream zip = new DeflaterOutputStream(stream, deflater);
                zip.write(data);
                zip.close();
                deflater.end();
                bytes = stream.toByteArray();
                this.compressionLevel = compressionLevel;
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            put(PDFName.FILTER, PDFName.FLATEDECODE);
        }
        else
            bytes = data;
        setLength(bytes.length);
    }
    
    /**
     * Sets the data associated with the stream, as-is.  This method will not
     * remove or change any existing filter: the data has to match an existing
     * filter or an appropriate filter has to be set.
     * 
     * @param data data, possibly encrypted and/or compressed
     * @since 5.5.0
     */
    public void setDataRaw(byte[] data) {
        this.offset = -1;
        bytes = data;
        setLength(bytes.length);
    }
    
    /**Sets the data associated with the stream
     * @param data raw data, decrypted and uncompressed.
     */
    public void setData(byte[] data) {
        setData(data, true);
    }

    public void setLength(int length) {
        this.length = length;
        put(PDFName.LENGTH, new PDFNumeric(length));
    }
    
    public long getOffset() {
        return offset;
    }
    
    public int getLength() {
        return length;
    }
    
    public PDFReader getReader() {
        return reader;
    }
    
    public byte[] getBytes() {
        return bytes;
    }
    
    public void setObjNum(int objNum, int objGen) {
        this.objNum = objNum;
        this.objGen = objGen;
    }
    
    int getObjNum() {
        return objNum;
    }
    
    int getObjGen() {
        return objGen;
    }
    
    @Override
    public void write(OutputStream os) throws IOException {
    	//TODO 不支持
//        byte[] b = PDFReader.getStreamBytesRaw(this);
//        PDFObj objLen = get(PDFName.LENGTH);
//        int nn = b.length;
//        put(PDFName.LENGTH, new PDFNumeric(nn));
//        superToPdf(os);
//        put(PDFName.LENGTH, objLen);
//        os.write(STARTSTREAM);
//        
//        if (length > 0) {
//            if (crypto != null && !crypto.isEmbeddedFilesOnly())
//                b = crypto.encryptByteArray(b);
//            os.write(b);
//        }
//        os.write(ENDSTREAM);
    }
}
