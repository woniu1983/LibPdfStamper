/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import cn.woniu.lib.pdf.arc.PDFBody;
import cn.woniu.lib.pdf.image.PDFImage;
import cn.woniu.lib.pdf.io.CounterOutputStream;
import cn.woniu.lib.pdf.model.PDFDictionary;
import cn.woniu.lib.pdf.model.PDFIndirectObject;
import cn.woniu.lib.pdf.model.PDFIndirectReference;
import cn.woniu.lib.pdf.model.PDFName;
import cn.woniu.lib.pdf.model.PDFObj;
import cn.woniu.lib.pdf.model.PDFStream;
import cn.woniu.lib.pdf.model.derivate.ImageStream;
import cn.woniu.lib.pdf.util.StringUtils;

/** 
 * @ClassName: PDFWriter <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月10日 下午7:35:51 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFWriter {


	protected CounterOutputStream os; 

	private File saveFile;

	/** body of the PDF document */
	protected PDFBody body;

	public PDFWriter(final File saveFile) throws IOException {
		this.saveFile = saveFile;
		this.os =  new CounterOutputStream(new FileOutputStream(saveFile));
	}


	public CounterOutputStream getOs() {
		return os;
	}


    protected int compressionLevel = PDFStream.DEFAULT_COMPRESSION;
    
	/** This is the list with all the images in the document. */
	private final HashMap<Long, PDFName> images = new HashMap<Long, PDFName>();


	/** Dictionary, containing all the images of the PDF document */
	protected PDFDictionary imageDictionary = new PDFDictionary();

	public PDFIndirectReference getImageReference(final PDFName name) {
		return (PDFIndirectReference) imageDictionary.get(name);
	}

	public PDFName addDirectImage(final PDFImage image) throws IOException {
		PDFName name;
		// if the images is already added, just retrievethe name
		System.out.println("MySerialId=" + image.getImgId());//TODO
		if (images.containsKey(image.getImgId())) {
			System.out.println("images already contain this image");//TODO
			name = images.get(image.getImgId());
			System.out.println("images already contain this image: " + name);//TODO

		} else {

			PDFIndirectReference dref = image.getDirectReference();
			System.out.println("[PdfWriter] PDFIndirectReference===>" + dref);//TODO 新增文件==NULL
			if (dref != null) {
				PDFName rname = new PDFName("img" + images.size());
				images.put(image.getImgId(), rname);
				imageDictionary.put(rname, dref);
				return rname;
			}

			PDFImage maskImage = image.getImageMask();
			PDFIndirectReference maskRef = null;
			if (maskImage != null) {
				PDFName mname = images.get(maskImage.getImgId());
				maskRef = getImageReference(mname);
				System.out.println("[PdfWriter] PdfImage Get MaskImage===>" + maskRef.toString());//TODO
			}

			ImageStream stream = new ImageStream(image, "img" + images.size(), maskRef);
			System.out.println("[PdfWriter] PdfImage===>" + stream.getName());//TODO

			add(stream);
			name = stream.getName();
		}
		images.put(image.getImgId(), name);

		return name;
	}

	private PDFIndirectReference add(final ImageStream pdfImage) throws IOException {
		if (! imageDictionary.contains(pdfImage.getName())) {
			//	            PdfWriter.checkPdfIsoConformance(this, PdfIsoKeys.PDFISOKEY_IMAGE, pdfImage);
			PDFIndirectReference fixedRef = null;
			try {
				System.out.println("[PdfWriter] ---------------------fixedRef=null");
				fixedRef = addToBody(pdfImage).getIndirectReference();// 追加Image的同时，会先追加Obj头 n 0 obj /Type.....
				System.out.println("[PdfWriter] +++++++++++++++++++++fixedRef7777777After addToBody");
			}
			catch(IOException ioe) {
				throw ioe;
			}
			imageDictionary.put(pdfImage.getName(), fixedRef);
			System.out.println("[PdfWriter] ---------------------pdfImage.name=" + pdfImage.getName() + " " + fixedRef);
			return fixedRef;
		}
		return (PDFIndirectReference) imageDictionary.get(pdfImage.getName());
	}

	public PDFIndirectObject addToBody(final PDFObj object) throws IOException {
		System.out.println("[PdfWriter] ---------------------addToBody");
		PDFIndirectObject iobj = this.body.add(object);
		System.out.println("[PdfWriter] ---------------------body.add");
		cacheObject(iobj);
		System.out.println("[PdfWriter] ---------------------cacheObject");
		return iobj;
	}

	public PDFIndirectObject addToBody(final PDFObj object, final boolean inObjStm) throws IOException {
		PDFIndirectObject iobj = body.add(object, inObjStm);
		cacheObject(iobj);
		return iobj;
	}

	public PDFIndirectObject addToBody(final PDFObj object, final PDFIndirectReference ref) throws IOException {
		PDFIndirectObject iobj = body.add(object, ref);
		cacheObject(iobj);
		return iobj;
	}

	public PDFIndirectObject addToBody(final PDFObj object, final int refNumber, final boolean inObjStm) throws IOException {
		PDFIndirectObject iobj = body.add(object, refNumber, 0, inObjStm);
		cacheObject(iobj);
		return iobj;
	}

    /**
     * Use this method to add a PDF object to the PDF body.
     * Use this method only if you know what you're doing!
     * @param object
     * @param ref
     * @param inObjStm
     * @return a PdfIndirectObject
     * @throws IOException
     */
    public PDFIndirectObject addToBody(final PDFObj object, final PDFIndirectReference ref, final boolean inObjStm) throws IOException {
    	PDFIndirectObject iobj = body.add(object, ref, inObjStm);
        cacheObject(iobj);
        return iobj;
    }

    /**
     * Use this method to add a PDF object to the PDF body.
     * Use this method only if you know what you're doing!
     * @param object
     * @param refNumber
     * @return a PdfIndirectObject
     * @throws IOException
     */
    public PDFIndirectObject addToBody(final PDFObj object, final int refNumber) throws IOException {
    	PDFIndirectObject iobj = body.add(object, refNumber);
        cacheObject(iobj);
        return iobj;
    }

	protected void cacheObject(PDFIndirectObject iobj) { }



	//    protected PDFReaderInstance currentPdfReaderInstance;

	public int getNewObjectNumber(final PDFReader reader, final int number, final int generation) {
		//        if (currentPdfReaderInstance == null || currentPdfReaderInstance.getReader() != reader) {
		//            currentPdfReaderInstance = getPdfReaderInstance(reader);
		//        }
		//        return currentPdfReaderInstance.getNewObjectNumber(number, generation);
		//TODO
		return 0;
	}

	public static void writeKeyInfo(OutputStream os) throws IOException {
		os.write(StringUtils.getISOBytes("%Woniu-1.0.0\n"));
	}

    protected int getIndirectReferenceNumber() {
        return body.getIndirectReferenceNumber();
    }
}
