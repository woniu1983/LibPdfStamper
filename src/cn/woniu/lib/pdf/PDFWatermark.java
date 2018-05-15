/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.woniu.lib.pdf.arc.PDFBody;
import cn.woniu.lib.pdf.arc.PDFTrailer;
import cn.woniu.lib.pdf.arc.PageStamp;
import cn.woniu.lib.pdf.encode.ByteBuffer;
import cn.woniu.lib.pdf.encode.IntHashtable;
import cn.woniu.lib.pdf.encode.PDFEncryption;
import cn.woniu.lib.pdf.image.PDFImage;
import cn.woniu.lib.pdf.image.PNGImage;
import cn.woniu.lib.pdf.image.Rectangle;
import cn.woniu.lib.pdf.io.BufferedRandomAccessFile;
import cn.woniu.lib.pdf.model.PDFArray;
import cn.woniu.lib.pdf.model.PDFDictionary;
import cn.woniu.lib.pdf.model.PDFIndirectReference;
import cn.woniu.lib.pdf.model.PDFName;
import cn.woniu.lib.pdf.model.PDFObj;
import cn.woniu.lib.pdf.model.PDFStream;
import cn.woniu.lib.pdf.model.PDFString;
import cn.woniu.lib.pdf.model.derivate.PDFContents;
import cn.woniu.lib.pdf.model.derivate.PRIndirectReference;
import cn.woniu.lib.pdf.util.PDFConstant;
import cn.woniu.lib.pdf.util.StringUtils;


/** 
 * @ClassName: PDFWatermark <br/> 
 * @Description: Use PDF Incremental Updates to insert image to PDF <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月14日 下午6:09:42 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFWatermark extends PDFWriter {


	private static final boolean append = true;

	private PDFReader reader;
	protected IntHashtable marked;
	protected int initialXrefSize;
	private PDFImage image;
	protected long prevxref = 0;

	IntHashtable myXref = new IntHashtable();
	HashMap<PDFReader, IntHashtable> readers2intrefs = new HashMap<PDFReader, IntHashtable>();

	/**
	 * PDF Page 上的一些内容，用于写文件时，添加到PDF中
	 */
	private HashMap<PDFDictionary, PageStamp> stampMap = new HashMap<PDFDictionary, PageStamp>(); 

	protected PDFWatermark(final PDFReader reader, final File saveFile, final PDFImage image) throws IOException {
		super(saveFile);
		this.reader = reader;
		this.image = image;
		this.prevxref = this.reader.getLastXref();
		this.body = new PDFBody(this);
		if (append) {
			this.body.setRefnum(this.reader.getXrefSize());
			marked = new IntHashtable();
		}
	}


	public void appendWatermark() throws IOException {
		String srcPath = this.reader.getSrcPDFPath();
		long offset = this.reader.getHeaderOffset();

		// 1. Read and save orginal PDF content, filter Header Offset
		writeOrigPDF(srcPath, offset);
		// 在文件最后添加换行
		this.os.write(StringUtils.getISOBytes("\n"));

		// 2. Append Image for pages
		writeImages();

		// 3. 
		writeOtherContents();
	}

	private void writeImages() throws IOException {

		int pages = this.reader.getPageCount();
		if (pages <= 0) {
			throw new IOException("Not found any PDF Page.");
		}

		for(int i = 1; i <= pages; i++) {
			PageStamp stamp = getPageStamp(i);

			float matrix[] = this.image.matrix();
			matrix[PDFImage.CX] = this.image.getAbsoluteX() - matrix[PDFImage.CX];
			matrix[PDFImage.CY] = this.image.getAbsoluteY() - matrix[PDFImage.CY];
			addImage(image, stamp, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
		}

	}

	private PageStamp getPageStamp(int pageNum) {
		PDFDictionary pageN = reader.getPageN(pageNum);
		PageStamp ps = stampMap.get(pageN);
		if (ps == null) {
			ps = new PageStamp(pageN);
			stampMap.put(pageN, ps);
		}
		ps.pageN.setIndRef(reader.getPageOrigRef(pageNum));
		return ps;

	}

	private void addImage(final PDFImage image, final PageStamp stamp, final double a, final double b, final double c, final double d, final double e, final double f) throws IOException {

		// 1. q cm..
		stamp.content.append("q ");
		stamp.content.append(a).append(' ');
		stamp.content.append(b).append(' ');
		stamp.content.append(c).append(' ');
		stamp.content.append(d).append(' ');
		stamp.content.append(e).append(' ');
		stamp.content.append(f).append(" cm");

		// 2. Image
		PDFName name;
		//        PageResources prs = getPageResources();
		PDFImage maskImage = image.getImageMask();
		if (maskImage != null) {
			System.out.println("[PdfContentByte]*******************************************Add Mask Image Obj*******************************************");
			name = addDirectImage(maskImage);
			stamp.addXObject(name, getImageReference(name));
		}
		System.out.println("[PdfContentByte]*******************************************Add Image Obj*******************************************");
		name = addDirectImage(image);
		name = stamp.addXObject(name, getImageReference(name));

		stamp.content.append(' ').append(name.getBytes()).append(" Do Q").append_i(PDFConstant.SEPARATOR_LINE);
		System.out.println("[PdfContentByte]!!!!!!!!!content==" + stamp.content);
		System.out.println("[PdfContentByte]*******************************************Add Image End*******************************************");
	}

	private void writeOtherContents() throws IOException {

		initialXrefSize = reader.getXrefSize();

		// metadata
		int skipInfo = -1;
		PDFIndirectReference iInfo = reader.getTrailer().getAsIndirectObject(PDFName.INFO);
		if (iInfo != null) {
			skipInfo = iInfo.getNumber();
		}
		PDFDictionary oldInfo = reader.getTrailer().getAsDict(PDFName.INFO);
		String producer = null;
		if (oldInfo != null && oldInfo.get(PDFName.PRODUCER) != null) {
			producer = oldInfo.getAsString(PDFName.PRODUCER).toUnicodeString();
		}
		if (producer == null) {
			producer = "Woniu";
		}
		PDFIndirectReference info = null;
		PDFDictionary newInfo = new PDFDictionary();
		if (oldInfo != null) {
			for (Object element : oldInfo.getKeys()) {
				PDFName key = (PDFName) element;
				PDFObj value = PDFReader.getPdfObject(oldInfo.get(key));
				newInfo.put(key, value);
			}
		}
		//        PdfDate date = new PdfDate();
		//        newInfo.put(PDFName.MODDATE, date);
		newInfo.put(PDFName.PRODUCER, new PDFString(producer, PDFObj.TEXT_UNICODE));
		System.out.println("[PdfStamperImpl] PDFName.PRODUCER--------------------------");
		if (append) {
			System.out.println("[PdfStamperImpl] addToBody--------------------------" + iInfo);
			if (iInfo == null) {
				info = addToBody(newInfo, false).getIndirectReference();
			} else {
				info = addToBody(newInfo, iInfo.getNumber(), false).getIndirectReference();
			}
		} 

		writeEnd(info, skipInfo);
	}


	private void writeEnd(PDFIndirectReference info, int skipInfo) throws IOException {
		alterContents();
		int rootN = ((PRIndirectReference) reader.trailer.get(PDFName.ROOT)).getNumber();
		if (append) {
			int keys[] = marked.getKeys();
			for (int k = 0; k < keys.length; ++k) {
				int j = keys[k];
				PDFObj obj = reader.getPdfObjectRelease(j);
				if (obj != null && skipInfo != j && j < initialXrefSize) {
					addToBody(obj, obj.getIndRef(), j != rootN);
				}
			}
			for (int k = initialXrefSize; k < reader.getXrefSize(); ++k) {
				PDFObj obj = reader.getPdfObject(k);
				if (obj != null) {
					addToBody(obj, getNewObjectNumber(reader, k, 0));
				}
			}
		}

		PDFIndirectReference encryption = null;
		PDFObj fileID = null;
		PDFArray IDs = reader.trailer.getAsArray(PDFName.ID);
		if (IDs != null && IDs.getAsString(0) != null) {
			fileID = PDFEncryption.createInfoId(IDs.getAsString(0).getBytes(), true);
		} else {
			fileID = PDFEncryption.createInfoId(PDFEncryption.createDocumentId(), true);
		}
		PRIndirectReference iRoot = (PRIndirectReference) reader.trailer.get(PDFName.ROOT);
		PDFIndirectReference root = new PDFIndirectReference(0, getNewObjectNumber(reader, iRoot.getNumber(), 0));
		// write the cross-reference table of the body
		System.out.println("[PdfStamperImp] *******************************************Add xref*******************************************");

		body.writeCrossReferenceTable(os, root, info, encryption, fileID, prevxref);
		System.out.println("[PdfStamperImp] *******************************************Add trailer*******************************************");
		PDFTrailer trailer = new PDFTrailer(body.size(),
				body.offset(),
				root,
				info,
				encryption,
				fileID, prevxref);
		trailer.write(os);
		os.flush();
		//		getCounter().written(os.getCounter()); //TODO
	}

	void applyRotation(PDFDictionary pageN, ByteBuffer out) {
		Rectangle page = reader.getPageSizeWithRotation(pageN);
		int rotation = page.getRotation();
		switch (rotation) {
		case 90:
			out.append(PDFContents.ROTATE90);
			out.append(page.getTop());
			out.append(' ').append('0').append(PDFContents.ROTATEFINAL);
			break;
		case 180:
			out.append(PDFContents.ROTATE180);
			out.append(page.getRight());
			out.append(' ');
			out.append(page.getTop());
			out.append(PDFContents.ROTATEFINAL);
			break;
		case 270:
			out.append(PDFContents.ROTATE270);
			out.append('0').append(' ');
			out.append(page.getRight());
			out.append(PDFContents.ROTATEFINAL);
			break;
		}
	}

	protected void alterContents() throws IOException {
		for (Object element : stampMap.values()) {
			PageStamp ps = (PageStamp) element;
			PDFDictionary pageN = ps.pageN;
			markUsed(pageN);
			PDFArray ar = null;
			PDFObj content = PDFReader.getPdfObject(pageN.get(PDFName.CONTENTS), pageN);
			if (content == null) {
				ar = new PDFArray();
				pageN.put(PDFName.CONTENTS, ar);
			} else if (content.isArray()) {
				ar = new PDFArray((PDFArray) content);
				pageN.put(PDFName.CONTENTS, ar);
			} else if (content.isStream()) {
				ar = new PDFArray();
				ar.add(pageN.get(PDFName.CONTENTS));
				pageN.put(PDFName.CONTENTS, ar);
			} else {
				ar = new PDFArray();
				pageN.put(PDFName.CONTENTS, ar);
			}
			ByteBuffer out = new ByteBuffer();
			if (ps.content != null) {
				out.append(PDFContents.SAVESTATE);
			}
			PDFStream stream = new PDFStream(out.toByteArray());
			stream.flateCompress(compressionLevel);
			ar.addFirst(addToBody(stream).getIndirectReference());
			out.reset();
			if (ps.content != null) {
				out.append(' ');
				out.append(PDFContents.RESTORESTATE);
				ByteBuffer buf = ps.content;
				out.append(buf.getBuffer(), 0, ps.replacePoint);
				out.append(PDFContents.SAVESTATE);
				applyRotation(pageN, out);
				out.append(buf.getBuffer(), ps.replacePoint, buf.size() - ps.replacePoint);
				out.append(PDFContents.RESTORESTATE);
				stream = new PDFStream(out.toByteArray());
				stream.flateCompress(compressionLevel);
				ar.add(addToBody(stream).getIndirectReference());
			}
			alterResources(ps);
		}
	}

	protected void markUsed(PDFObj obj) {
		if (append && obj != null) {
			PRIndirectReference ref = null;
			if (obj.type() == PDFObj.INDIRECT) {
				ref = (PRIndirectReference) obj;
			} else {
				ref = obj.getIndRef();
			}
			if (ref != null) {
				this.marked.put(ref.getNumber(), 1);
			}
		}
	}

	protected void markUsed(int num) {
		if (append)
			marked.put(num, 1);
	}

	void alterResources(PageStamp ps) {
		ps.pageN.put(PDFName.RESOURCES, ps.getResources());
	}


	private void writeOrigPDF(String srcPath, long offset) throws IOException {
		BufferedRandomAccessFile raf = null;
		try {
			raf = new BufferedRandomAccessFile(srcPath, "r");
			raf.seek(offset);

			byte[] buff = new byte[4096];
			int read = 0;
			while((read = raf.read(buff)) != -1) {
				this.os.write(buff, 0, read);
			}
			this.os.flush();
			System.out.println("Save File len=" + this.os.getCounter());

		} catch(IOException e) {
			e.printStackTrace();
			throw e;

		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void close(){
		if (this.os != null) {
			try {
				this.os.close();
			} catch (IOException e) {
			}
		} 

		if (this.reader != null) {
			try {
				this.reader.close();
			} catch (IOException e) {
			}
		} 
	}



	@Override
	public int getNewObjectNumber(PDFReader reader, int number, int generation) {
		if (append && number < initialXrefSize)
			return number;
		int n = myXref.get(number);
		if (n == 0) {
			n = getIndirectReferenceNumber();
			myXref.put(number, n);
		}
		return n;
	}


	public static void main(String[] args) {
		String srcPath = "E:\\Projects\\Github\\source.pdf";
		String outPath = "E:\\Projects\\Github\\save.pdf";
		PDFReader reader = null;
		PDFWatermark watermarker = null;
		try {
			reader = new PDFReader(srcPath);

			PDFImage image = PNGImage.getImage("E:\\Projects\\Github\\p.png");
			image.setAbsolutePosition(100, 150);
			//			image.setRotationDegrees(45);
			
			watermarker = new PDFWatermark(reader, new File(outPath), image);
			watermarker.appendWatermark();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (watermarker != null) {
				watermarker.close();
			}
		}
	}

}
