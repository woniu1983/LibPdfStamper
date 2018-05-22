/** 
 * Copyright (c) 2018, woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import cn.woniu.lib.pdf.encode.IntHashtable;
import cn.woniu.lib.pdf.encode.PdfEncodings;
import cn.woniu.lib.pdf.image.Rectangle;
import cn.woniu.lib.pdf.io.BufferedRandomAccessFile;
import cn.woniu.lib.pdf.io.PDFToken;
import cn.woniu.lib.pdf.io.PDFToken.TokenType;
import cn.woniu.lib.pdf.model.PDFArray;
import cn.woniu.lib.pdf.model.PDFBoolean;
import cn.woniu.lib.pdf.model.PDFDictionary;
import cn.woniu.lib.pdf.model.PDFName;
import cn.woniu.lib.pdf.model.PDFNull;
import cn.woniu.lib.pdf.model.PDFNumeric;
import cn.woniu.lib.pdf.model.PDFObj;
import cn.woniu.lib.pdf.model.PDFString;
import cn.woniu.lib.pdf.model.derivate.PDFLiteral;
import cn.woniu.lib.pdf.model.derivate.PDFPageTree;
import cn.woniu.lib.pdf.model.derivate.PRIndirectReference;
import cn.woniu.lib.pdf.model.derivate.PRStream;
import cn.woniu.lib.pdf.util.Logger;
import cn.woniu.lib.pdf.util.PDFConstant;
import cn.woniu.lib.pdf.util.StringUtils;

/** 
 * @ClassName: PDFReader <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月10日 下午3:20:04 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFReader {


	/**
	 * 默认不支持加密PDF
	 */
	protected boolean encrypted = false;

	static final byte endstream[] = PdfEncodings.convertToBytes("endstream", null);
	static final byte endobj[] = PdfEncodings.convertToBytes("endobj", null);

	private String srcPdfPath;
	private String srcPdfPassword;

	/**
	 * Default=true
	 * Only support PDF Incremental Update 
	 * Holds value of property appendable.
	 */
	private static boolean appendable = true;

	private PDFToken token;

	/** PDF 头部起始位置 (%PDF- 的偏移量) */
	private long headerOffset; 

	/** PDF版本号： 1.4, 1.5, 1.6.....*/
	private String version;

	protected long lastXref;
	private int lastXrefPartial = -1;
	protected long eofPos;
	protected boolean newXrefType;
	private boolean hybridXref;

	/**
	 * 存放了 xref下的Obj的 index and generation 
	 */
	public long xref[];
	public HashMap<Integer, IntHashtable> objStmMark;
	public PDFDictionary trailer;
	public PDFDictionary catalog;
	public ArrayList<PDFObj> xrefObjs;
	public PDFDictionary rootPages;
	protected PDFPageTree pageTree;

	public static void main(String[] args) {
		PDFReader reader = null;
		try {
			reader = new PDFReader("E:\\Projects\\Github\\source.pdf");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * <p>Title: </p>  
	 * <p>Description: 读取PDF文件并解析结构， 目前只支持无加密和签名的PDF文件</p>  
	 * @param path
	 * @throws IOException
	 */
	public PDFReader(final String path) throws IOException{
		this.srcPdfPath = path;

		readPdf();
	}

	public String getSrcPDFPath(){
		return this.srcPdfPath;
	}

	public long getHeaderOffset() {
		return this.headerOffset;
	}

    public long getLastXref() {
        return this.lastXref;
    }

    public int getXrefSize() {
        return this.xrefObjs.size();
    }

    public PDFDictionary getTrailer() {
        return this.trailer;
    }
    
    public int getPageCount() {
        return this.pageTree.size();
    }

    public PDFDictionary getPageN(final int pageNum) {
        PDFDictionary dic = this.pageTree.getPageDic(pageNum);
        if (dic == null)
            return null;
        
        if (appendable) {
            dic.setIndRef(this.pageTree.getPageOrigRef(pageNum));
        }
        return dic;
    }
    
    public PRIndirectReference getPageOrigRef(final int pageNum) {
        return this.pageTree.getPageOrigRef(pageNum);
    }
    
	/**
	 * 
	 * @Title: readPdf  
	 * @Description: TODO  
	 *
	 * @throws IOException
	 */
	protected void readPdf() throws IOException {
		File file = new File(this.srcPdfPath);
		if (!file.exists() || file.isDirectory()) {
			throw new FileNotFoundException(this.srcPdfPath + " can't open.");
		}

		// 1. Parse Pdf Version
		parsePdfHeader();
		checkPdfVersion();

		// 2. RandomAccessFile
		//		RandomAccessFile rasFile = new RandomAccessFile(file, "r");
		BufferedRandomAccessFile rasFile = new BufferedRandomAccessFile(file, "r");
		this.token = new PDFToken(rasFile, this.headerOffset);

		// 3. Parse Xref  Trailer
		parseXrefTrailer();

		// 4. Parse PDF Objects
		parsePDFObjs();

		// 5. Parse PDF Pages
		strings.clear();
		parsePDFPages();
	}

	private void parsePdfHeader() throws IOException{
		final BufferedRandomAccessFile buffRasFile = new BufferedRandomAccessFile(this.srcPdfPath, "r", 2048);

		// Search %PDF-
		byte[] buff = new byte[1024];
		buffRasFile.seek(0);
		int len = buffRasFile.read(buff);
		int index = StringUtils.search(buff, 0, len, PDFConstant.PDF_HEADER.getBytes());
		if (index < 0){
			throw new IOException("pdf header not found");
		}
		this.headerOffset = index;
		Logger.Info("headerOffset=" + index);

		// Search pdf version
		buffRasFile.seek(index + PDFConstant.PDF_HEADER.length());
		byte[] verBuff = new byte[3];
		len = buffRasFile.read(verBuff);
		String version = new String(verBuff);
		try {
			double value = Double.valueOf(version);	
			Logger.Debug("version=" + value);
			this.version = version;
		} catch (Exception e) {
			throw new IOException("pdf format invalid, version not found.");
		}
	}

	private void checkPdfVersion() throws IOException {
		if (this.version.compareTo("1.4") < 0) {
			throw new IOException("input pdf file version is : [" + this.version + "], should > 1.4.");
		}

	}

	private void parseXrefTrailer() throws IOException {
		// 1. "startxref" location
		long index = this.token.getStartxref();
		this.token.seek(index);
		this.token.nextToken();
		if (!this.token.getStringValue().equals("startxref"))
			throw new IOException("startxref.not.found");

		// 2. value after "startxref", Must a number, means "xref" location
		this.token.nextToken();
		if (this.token.getTokenType() != TokenType.NUMBER)
			throw new IOException("startxref.is.not.followed.by.a.number");
		long startxref = this.token.longValue();
		Logger.Debug("startxref value=" + startxref);//TODO
		this.lastXref = startxref;
		eofPos = this.token.getFilePointer(); // Number之后一行是%%EOF        

		// 3. xref parse 
		try {
			if (readXRefStream(startxref)) {
				newXrefType = true;
				return;
			}
		} catch (Exception e) {}

		xref = null;
		this.token.seekOffset(startxref);
		trailer = readXrefSection();

		PDFDictionary trailer2 = trailer;
		while (true) {
			PDFNumeric prev = (PDFNumeric)trailer2.get(PDFName.PREV);
			if (prev == null) {
				break;
			}
			if (prev.longValue() == startxref) {
				throw new IOException("Trailer's Prev points to own reference");
			}
			long prevstartxref = prev.longValue();
			this.token.seekOffset(prevstartxref);
			trailer2 = readXrefSection();
		}
	}

	private void parsePDFObjs() throws IOException {
		ArrayList<PRStream> streams = new ArrayList<PRStream>();
		// xref.length / 2 == obj total number
		xrefObjs = new ArrayList<PDFObj>(xref.length / 2);
		xrefObjs.addAll(Collections.<PDFObj>nCopies(xref.length / 2, null));
		for (int k = 2; k < xref.length; k += 2) {
			long pos = xref[k];
			if (pos <= 0 || xref[k + 1] > 0)
				continue;
			this.token.seekOffset(pos);
			this.token.nextValidToken();
			if (this.token.getTokenType() != TokenType.NUMBER) {
				throw new IOException("Object Number invliad");
			}
			objNum = this.token.intValue();
			this.token.nextValidToken();
			if (this.token.getTokenType() != TokenType.NUMBER) {
				throw new IOException("Object generation invalid");
			}
			objGen = this.token.intValue();
			this.token.nextValidToken();
			if (!this.token.getStringValue().equals("obj"))
				throw new IOException("Not Obj");
			PDFObj obj;
			try {
				obj = readPRObject();
				if (obj.isStream()) {
					streams.add((PRStream)obj);
				}
			}
			catch (IOException e) {
				throw e;
			}
			xrefObjs.set(k / 2, obj);
		}
		for (int k = 0; k < streams.size(); ++k) {
			checkPRStreamLength(streams.get(k));
		}

		// 以下不需要
		//		readDecryptedDocObj();
		//		if (objStmMark != null) {
		//			for (Map.Entry<Integer, IntHashtable>entry: objStmMark.entrySet()) {
		//				int n = entry.getKey().intValue();
		//				IntHashtable h = entry.getValue();
		//				readObjStm((PRStream)xrefObjs.get(n), h);
		//				xrefObjs.set(n, null);
		//			}
		//			objStmMark = null;
		//		}
		xref = null;
	}

	protected void parsePDFPages() throws IOException {
		this.catalog = this.trailer.getAsDict(PDFName.ROOT);
		if (this.catalog == null) {
			throw new IOException("Not found catalog");
		}
		rootPages = catalog.getAsDict(PDFName.PAGES);
		if (rootPages == null || 
				(!PDFName.PAGES.equals(rootPages.get(PDFName.TYPE)) && !PDFName.PAGES.equals(rootPages.get(new PDFName("Types"))))) {
			throw new IOException("Not found page root");
		}
		//TODO
		pageTree = new PDFPageTree(this);
	}

	protected boolean readXRefStream(final long position) throws IOException {
		return false;
		//		this.token.seekOffset(position);
		//		int thisStream = 0;
		//		if (!this.token.nextToken())
		//			return false;
		//		if (this.token.getTokenType() != TokenType.NUMBER)
		//			return false;
		//		thisStream = this.token.intValue();
		//		if (!this.token.nextToken() || this.token.getTokenType() != TokenType.NUMBER)
		//			return false;
		//		if (!this.token.nextToken() || !this.token.getStringValue().equals("obj"))
		//			return false;
		//
		//		return false;//TODO
		//		PDFObj object = readPRObject();
		//		PRStream stm = null;
		//		if (object.isStream()) {
		//			stm = (PRStream)object;
		//			if (!PDFName.XREF.equals(stm.get(PDFName.TYPE)))
		//				return false;
		//		}
		//		else
		//			return false;
		//		if (trailer == null) {
		//			trailer = new PDFDictionary();
		//			trailer.putAll(stm);
		//		}
		//		stm.setLength(((PDFNumeric)stm.get(PDFName.LENGTH)).intValue());
		//		int size = ((PDFNumeric)stm.get(PDFName.SIZE)).intValue();
		//		PDFArray index;
		//		PDFObj obj = stm.get(PDFName.INDEX);
		//		if (obj == null) {
		//			index = new PDFArray();
		//			index.add(new int[]{0, size});
		//		} else {
		//			index = (PDFArray)obj;
		//		}
		//		PDFArray w = (PDFArray)stm.get(PDFName.W);
		//		long prev = -1;
		//		obj = stm.get(PDFName.PREV);
		//		if (obj != null)
		//			prev = ((PDFNumeric)obj).longValue();
		//		// Each xref pair is a position
		//		// type 0 -> -1, 0
		//		// type 1 -> offset, 0
		//		// type 2 -> index, obj num
		//		ensureXrefSize(size * 2);
		//		if (objStmMark == null) {
		//			objStmMark = new HashMap<Integer, IntHashtable>();
		//		}            
		//		byte b[] = getStreamBytes(stm, this.token.getFile());
		//		int bptr = 0;
		//		int wc[] = new int[3];
		//		for (int k = 0; k < 3; ++k)
		//			wc[k] = w.getAsNumber(k).intValue();
		//		for (int idx = 0; idx < index.size(); idx += 2) {
		//			int start = index.getAsNumber(idx).intValue();
		//			int length = index.getAsNumber(idx + 1).intValue();
		//			ensureXrefSize((start + length) * 2);
		//			while (length-- > 0) {
		//				int type = 1;
		//				if (wc[0] > 0) {
		//					type = 0;
		//					for (int k = 0; k < wc[0]; ++k)
		//						type = (type << 8) + (b[bptr++] & 0xff);
		//				}
		//				long field2 = 0;
		//				for (int k = 0; k < wc[1]; ++k)
		//					field2 = (field2 << 8) + (b[bptr++] & 0xff);
		//				int field3 = 0;
		//				for (int k = 0; k < wc[2]; ++k)
		//					field3 = (field3 << 8) + (b[bptr++] & 0xff);
		//				int base = start * 2;
		//				if (xref[base] == 0 && xref[base + 1] == 0) {
		//					switch (type) {
		//					case 0:
		//						xref[base] = -1;
		//						break;
		//					case 1:
		//						xref[base] = field2;
		//						break;
		//					case 2:
		//						xref[base] = field3;
		//						xref[base + 1] = field2;
		//						Integer on = Integer.valueOf((int)field2);
		//						IntHashtable seq = objStmMark.get(on);
		//						if (seq == null) {
		//							seq = new IntHashtable();
		//							seq.put(field3, 1);
		//							objStmMark.put(on, seq);
		//						}
		//						else
		//							seq.put(field3, 1);
		//						break;
		//					}
		//				}
		//				++start;
		//			}
		//		}
		//		thisStream *= 2;
		//		if (thisStream + 1 < xref.length && xref[thisStream] == 0 && xref[thisStream + 1] == 0)
		//			xref[thisStream] = -1;
		//
		//		if (prev == -1)
		//			return true;
		//		return readXRefStream(prev);
	}

	protected PDFDictionary readXrefSection() throws IOException {
		this.token.nextValidToken();
		if (!PDFConstant.PDF_XREF.equals(this.token.getStringValue())) {
			throw new IOException("not found xref childs");
		}

		int start = 0;
		int end = 0;
		long pos = 0;
		int gen = 0;
		while (true) {
			this.token.nextValidToken();
			if (this.token.getStringValue().equals("trailer"))
				break;
			if (this.token.getTokenType() != TokenType.NUMBER)
				throw new IOException("not found the obj number in the 1st obj of xref childs");
			start = this.token.intValue();
			this.token.nextValidToken();
			if (this.token.getTokenType() != TokenType.NUMBER)
				throw new IOException("not found number in xref childs");
			end = this.token.intValue() + start;
			if (start == 1) { // fix incorrect start number
				long back = this.token.getFilePointer();
				this.token.nextValidToken();
				pos = this.token.longValue();
				this.token.nextValidToken();
				gen = this.token.intValue();
				if (pos == 0 && gen == PDFConstant.MAX_GEN) {
					--start;
					--end;
				}
				this.token.seek(back);
			}
			ensureXrefSize(end * 2);
			for (int k = start; k < end; ++k) {
				this.token.nextValidToken();
				pos = this.token.longValue();
				this.token.nextValidToken();
				gen = this.token.intValue();
				this.token.nextValidToken();
				int p = k * 2;
				if (this.token.getStringValue().equals("n")) {
					if (xref[p] == 0 && xref[p + 1] == 0) {
						xref[p] = pos;
					}
				}
				else if (this.token.getStringValue().equals("f")) {
					if (xref[p] == 0 && xref[p + 1] == 0)
						xref[p] = -1;
				}
				else
					throw new IOException("invalid.cross.reference.entry.in.this.xref.subsection");
			}
		}
		PDFDictionary trailer = (PDFDictionary)readPRObject();
		PDFNumeric xrefSize = (PDFNumeric)trailer.get(PDFName.SIZE);
		ensureXrefSize(xrefSize.intValue() * 2);
		PDFObj xrs = trailer.get(PDFName.XREFSTM);
		if (xrs != null && xrs.isNumber()) {
			int loc = ((PDFNumeric)xrs).intValue();
			try {
				readXRefStream(loc);
				newXrefType = true;
				hybridXref = true;
			}
			catch (IOException e) {
				xref = null;
				throw e;
			}
		}
		return trailer;
	}

	private void checkPRStreamLength(final PRStream stream) throws IOException {
		long fileLength = this.token.length();
		long start = stream.getOffset();
		boolean calc = false;
		long streamLength = 0;
		PDFObj obj = getPdfObjectRelease(stream.get(PDFName.LENGTH));
		if (obj != null && obj.type() == PDFObj.NUMBER) {
			streamLength = ((PDFNumeric)obj).intValue();
			if (streamLength + start > fileLength - 20) {
				calc = true;
			} else {
				this.token.seekOffset(start + streamLength);
				String line = this.token.readString(20);
				if (!line.startsWith("\nendstream") &&
						!line.startsWith("\r\nendstream") &&
						!line.startsWith("\rendstream") &&
						!line.startsWith("endstream")) {
					calc = true;
				}
			}
		} else {
			calc = true;
		}
		if (calc) {
			byte tline[] = new byte[16];
			this.token.seek(start);
			long pos;
			while (true) {
				pos = this.token.getFilePointer();
				if (!this.token.readLineSegment(tline, false)) // added boolean because of mailing list issue (17 Feb. 2014)
					break;
				if (StringUtils.isEqualBytes(tline, endstream)) {
					streamLength = pos - start;
					break;
				}
				if (StringUtils.isEqualBytes(tline, endobj)) {
					this.token.seek(pos - 16);
					String s = this.token.readString(16);
					int index = s.indexOf("endstream");
					if (index >= 0)
						pos = pos - 16 + index;
					streamLength = pos - start;
					break;
				}
			}
			this.token.seek(pos - 2);
			if (this.token.read() == 13)
				streamLength--;
			this.token.seek(pos - 1);
			if (this.token.read() == 10)
				streamLength--;

			if ( streamLength < 0 ) {
				streamLength = 0;
			}
		}
		stream.setLength((int)streamLength);
	}



	/**
	 * Reads a <CODE>PDFObj</CODE> resolving an indirect reference
	 * if needed.
	 * @param obj the <CODE>PDFObj</CODE> to read
	 * @return the resolved <CODE>PDFObj</CODE>
	 */
	public static PDFObj getPdfObject(PDFObj obj) {
		if (obj == null)
			return null;
		if (!obj.isIndirect())
			return obj;
		try {
			PRIndirectReference ref = (PRIndirectReference)obj;
			int idx = ref.getNumber();
			boolean appendable = ref.getReader().appendable;
			obj = ref.getReader().getPdfObject(idx);
			if (obj == null) {
				return null;
			}
			else {
				if (appendable) {
					switch (obj.type()) {
					case PDFObj.NULL:
						obj = new PDFNull();
						break;
					case PDFObj.BOOLEAN:
						obj = new PDFBoolean(((PDFBoolean)obj).booleanValue());
						break;
					case PDFObj.NAME:
						obj = new PDFName(obj.getBytes());
						break;
					}
					obj.setIndRef(ref);
				}
				return obj;
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}



	/**
	 * @param obj
	 * @return a PDFObj
	 */
	public static PDFObj getPdfObjectRelease(final PDFObj obj) {
		PDFObj obj2 = getPdfObject(obj);
		//		releaseLastXrefPartial(obj); // partial = true时调用
		return obj2;
	}

	//	/**
	//	 * Reads a <CODE>PDFObj</CODE> resolving an indirect reference
	//	 * if needed. If the reader was opened in partial mode the object will be released
	//	 * to save memory.
	//	 * @param obj the <CODE>PDFObj</CODE> to read
	//	 * @param parent
	//	 * @return a PDFObj
	//	 */
	//	public static PDFObj getPdfObjectRelease(final PDFObj obj, final PDFObj parent) {
	//		PDFObj obj2 = getPdfObject(obj, parent);
	////		releaseLastXrefPartial(obj);
	//		return obj2;
	//	}

		/**
		 * @param idx
		 * @return a PDFObj
		 */
		public PDFObj getPdfObjectRelease(final int idx) {
			PDFObj obj = getPdfObject(idx);
	//		releaseLastXrefPartial();
			return obj;
		}

	/**
	 * @param obj
	 * @param parent
	 * @return a PDFObj
	 */
	public static PDFObj getPdfObject(PDFObj obj, final PDFObj parent) {
		if (obj == null)
			return null;
		if (!obj.isIndirect()) {
			PRIndirectReference ref = null;
			if (parent != null && (ref = parent.getIndRef()) != null && appendable) {
				switch (obj.type()) {
				case PDFObj.NULL:
					obj = new PDFNull();
					break;
				case PDFObj.BOOLEAN:
					obj = new PDFBoolean(((PDFBoolean)obj).booleanValue());
					break;
				case PDFObj.NAME:
					obj = new PDFName(obj.getBytes());
					break;
				}
				obj.setIndRef(ref);
			}
			return obj;
		}
		return getPdfObject(obj);
	}

	/**
	 * @param idx
	 * @return aPdfObject
	 */
	public PDFObj getPdfObject(final int idx) {
		try {
			lastXrefPartial = -1;
			if (idx < 0 || idx >= xrefObjs.size())
				return null;
			PDFObj obj = xrefObjs.get(idx);
			return obj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

    /**
     * Gets the page rotation. This value can be 0, 90, 180 or 270.
     * @param index the page number. The first page is 1
     * @return the page rotation
     */
    public int getPageRotation(final int index) {
        return getPageRotation(this.pageTree.getPageDic(index));
    }

    int getPageRotation(final PDFDictionary page) {
        PDFNumeric rotate = page.getAsNumber(PDFName.ROTATE);
        if (rotate == null)
            return 0;
        else {
            int n = rotate.intValue();
            n %= 360;
            return n < 0 ? n + 360 : n;
        }
    }
    /** Gets the page size, taking rotation into account. This
     * is a <CODE>Rectangle</CODE> with the value of the /MediaBox and the /Rotate key.
     * @param index the page number. The first page is 1
     * @return a <CODE>Rectangle</CODE>
     */
    public Rectangle getPageSizeWithRotation(final int index) {
        return getPageSizeWithRotation(this.pageTree.getPageDic(index));
    }

    /**
     * Gets the rotated page from a page dictionary.
     * @param page the page dictionary
     * @return the rotated page
     */
    public Rectangle getPageSizeWithRotation(final PDFDictionary page) {
        Rectangle rect = getPageSize(page);
        int rotation = getPageRotation(page);
        while (rotation > 0) {
            rect = rect.rotate();
            rotation -= 90;
        }
        return rect;
    }

    /** Gets the page size without taking rotation into account. This
     * is the value of the /MediaBox key.
     * @param index the page number. The first page is 1
     * @return the page size
     */
    public Rectangle getPageSize(final int index) {
        return getPageSize(this.pageTree.getPageDic(index));
    }

    /**
     * Gets the page from a page dictionary
     * @param page the page dictionary
     * @return the page
     */
    public Rectangle getPageSize(final PDFDictionary page) {
        PDFArray mediaBox = page.getAsArray(PDFName.MEDIABOX);
        return getNormalizedRectangle(mediaBox);
    }

    /** Normalizes a <CODE>Rectangle</CODE> so that llx and lly are smaller than urx and ury.
     * @param box the original rectangle
     * @return a normalized <CODE>Rectangle</CODE>
     */
    public static Rectangle getNormalizedRectangle(final PDFArray box) {
        float llx = ((PDFNumeric)getPdfObjectRelease(box.getPDFObj(0))).floatValue();
        float lly = ((PDFNumeric)getPdfObjectRelease(box.getPDFObj(1))).floatValue();
        float urx = ((PDFNumeric)getPdfObjectRelease(box.getPDFObj(2))).floatValue();
        float ury = ((PDFNumeric)getPdfObjectRelease(box.getPDFObj(3))).floatValue();
        
        return new Rectangle(Math.min(llx, urx), Math.min(lly, ury),
        Math.max(llx, urx), Math.max(lly, ury));
    }

	private void readDecryptedDocObj() throws IOException {
		// 当前不支持加密
		if (encrypted) {
			return;
		}            
	}

	protected ArrayList<PDFString> strings = new ArrayList<PDFString>();
	private int readDepth = 0;
	private int objNum;
	private int objGen;    

	protected PDFObj readPRObject() throws IOException {
		this.token.nextValidToken();
		TokenType type = this.token.getTokenType();
		switch (type) {
		case START_DIC: {
			++readDepth;
			PDFDictionary dic = readDictionary();
			--readDepth;
			long pos = this.token.getFilePointer();
			// be careful in the trailer. May not be a "next" token.
			boolean hasNext;
			do {
				hasNext = this.token.nextToken();
			} while (hasNext && this.token.getTokenType() == TokenType.COMMENT);

			if (hasNext && this.token.getStringValue().equals("stream")) {
				//skip whitespaces
				int ch;
				do {
					ch = this.token.read();
				} while (ch == 32 || ch == 9 || ch == 0 || ch == 12);
				if (ch != '\n')
					ch = this.token.read();
				if (ch != '\n')
					this.token.backOnePosition(ch);
				PRStream stream = new PRStream(this, this.token.getFilePointer());
				stream.putAll(dic);
				// crypto handling
				stream.setObjNum(objNum, objGen);

				return stream;
			}
			else {
				this.token.seek(pos);
				return dic;
			}
		}
		case START_ARRAY: {
			++readDepth;
			PDFArray arr = readArray();
			--readDepth;
			return arr;
		}
		case NUMBER:
			return new PDFNumeric(this.token.getStringValue());
		case STRING:
			PDFString str = new PDFString(this.token.getStringValue(), null).setHexWriting(this.token.isHexString());
			// crypto handling
			str.setObjNum(objNum, objGen);
			if (strings != null)
				strings.add(str);

			return str;
		case NAME: {
			PDFName cachedName = PDFName.staticNames.get( this.token.getStringValue() );
			if (readDepth > 0 && cachedName != null) {
				return cachedName;
			} else {
				// an indirect name (how odd...), or a non-standard one
				return new PDFName(this.token.getStringValue(), false);
			}
		}
		case REF:
			int num = this.token.getReference();
			PRIndirectReference ref = new PRIndirectReference(this, num, this.token.getGeneration());
			return ref;
		case ENDOFFILE:
			throw new IOException("unexpected.end.of.file");
		default:
			String sv = this.token.getStringValue();
			if ("null".equals(sv)) {
				if (readDepth == 0) {
					return new PDFNull();
				} //else
				return PDFNull.PDFNULL;
			}
			else if ("true".equals(sv)) {
				if (readDepth == 0) {
					return new PDFBoolean( true );
				} //else
				return PDFBoolean.PDFTRUE;
			}
			else if ("false".equals(sv)) {
				if (readDepth == 0) {
					return new PDFBoolean( false );
				} //else
				return PDFBoolean.PDFFALSE;
			}
			return new PDFLiteral(-type.ordinal(), this.token.getStringValue());
		}
	}

	protected PDFDictionary readDictionary() throws IOException {
		PDFDictionary dic = new PDFDictionary();
		while (true) {
			this.token.nextValidToken();
			if (this.token.getTokenType() == TokenType.END_DIC)
				break;
			if (this.token.getTokenType() != TokenType.NAME)
				throw new IOException("Dict key is not Name:" + this.token.getStringValue());
			PDFName name = new PDFName(this.token.getStringValue(), false);
			PDFObj obj = readPRObject();
			int type = obj.type();
			if (-type == TokenType.END_DIC.ordinal())
				throw new IOException(("unexpected end of Dict"));
			if (-type == TokenType.END_ARRAY.ordinal())
				throw new IOException(("unexpected end of Array"));
			dic.put(name, obj);
		}
		return dic;
	}

	protected PDFArray readArray() throws IOException {
		PDFArray array = new PDFArray();
		while (true) {
			PDFObj obj = readPRObject();
			int type = obj.type();
			if (-type == TokenType.END_ARRAY.ordinal())
				break;
			if (-type == TokenType.END_DIC.ordinal())
				throw new IOException("unexpected end of Dict");
			array.add(obj);
		}
		return array;
	}

	/**
	 * 
	 * @Title: ensureXrefSize  
	 * @Description: 根据读取的值来确定xref的长度  
	 *
	 * @param size
	 */
	private void ensureXrefSize(final int size) {
		if (size == 0)
			return;
		if (xref == null)
			xref = new long[size];
		else {
			if (xref.length < size) {
				long xref2[] = new long[size];
				System.arraycopy(xref, 0, xref2, 0, xref.length);
				xref = xref2;
			}
		}
	}

	public boolean isAppendable() {
		return this.appendable;
	}

	public void close() throws IOException {
		if (this.token != null) {
			this.token.close();
		}
	}

}
