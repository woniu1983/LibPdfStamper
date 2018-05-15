/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model.derivate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.woniu.lib.pdf.image.PDFImage;
import cn.woniu.lib.pdf.model.PDFBoolean;
import cn.woniu.lib.pdf.model.PDFDictionary;
import cn.woniu.lib.pdf.model.PDFIndirectReference;
import cn.woniu.lib.pdf.model.PDFName;
import cn.woniu.lib.pdf.model.PDFNumeric;
import cn.woniu.lib.pdf.model.PDFStream;

/** 
 * @ClassName: ImageStream <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月14日 下午1:12:54 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class ImageStream extends PDFStream {

	protected PDFName name = null;

	protected PDFImage image = null;

	public ImageStream(PDFImage image, String name, PDFIndirectReference maskRef) throws IOException {
		super();
		this.image = image;
		if (name == null){ 
			generateImgResName( image );
		} else {
			this.name = new PDFName(name);
		}

		put(PDFName.TYPE, PDFName.XOBJECT);
		put(PDFName.SUBTYPE, PDFName.IMAGE);
		put(PDFName.WIDTH, new PDFNumeric(image.getWidth()));
		put(PDFName.HEIGHT, new PDFNumeric(image.getHeight()));

		// TODO 不支持
		//		if (image.getLayer() != null) { 
		//			put(PDFName.OC, image.getLayer().getRef());
		//		}

		if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 0xff)) {
			put(PDFName.IMAGEMASK, PDFBoolean.PDFTRUE);
		}

		if (maskRef != null) {
			if (image.isSmask()) {
				put(PDFName.SMASK, maskRef);
			} else {
				put(PDFName.MASK, maskRef);
			}
		}

		if (image.isMask() && image.isInverted()) {
			put(PDFName.DECODE, new PDFLiteral("[1 0]"));
		}

		// TODO 不支持
		//		if (image.isInterpolation()) {
		//			put(PDFName.INTERPOLATE, PDFBoolean.PDFTRUE);
		//		}

		InputStream is = null;
		try {
			// deal with transparency    TODO transparency == null
			int transparency[] = image.getTransparency();
			if (transparency != null && !image.isMask() && maskRef == null) {
				StringBuilder s = new StringBuilder("[");
				for (int k = 0; k < transparency.length; ++k) {
					s.append(transparency[k]).append(" ");
				}
				s.append("]");
				put(PDFName.MASK, new PDFLiteral(s.toString()));
			}

			// Raw PDFImage data
			if (image.isImgRaw()) {
				// will also have the CCITT parameters
				int colorspace = image.getColorspace();
				bytes = image.getRawData();
				put(PDFName.LENGTH, new PDFNumeric(bytes.length));

				int bpc = image.getBpc();
				if (bpc > 0xff) {
					//TODO 不支持
					//					if (!image.isMask()) {
					//						put(PDFName.COLORSPACE, PDFName.DEVICEGRAY);
					//					}
					//					put(PDFName.BITSPERCOMPONENT, new PDFNumeric(1));
					//					put(PDFName.FILTER, PDFName.CCITTFAXDECODE);
					//					int k = bpc - PDFImage.CCITTG3_1D;
					//					PDFDictionary decodeparms = new PDFDictionary();
					//					if (k != 0)
					//						decodeparms.put(PDFName.K, new PDFNumeric(k));
					//					if ((colorspace & PDFImage.CCITT_BLACKIS1) != 0)
					//						decodeparms.put(PDFName.BLACKIS1, PDFBoolean.PDFTRUE);
					//					if ((colorspace & PDFImage.CCITT_ENCODEDBYTEALIGN) != 0)
					//						decodeparms.put(PDFName.ENCODEDBYTEALIGN, PDFBoolean.PDFTRUE);
					//					if ((colorspace & PDFImage.CCITT_ENDOFLINE) != 0)
					//						decodeparms.put(PDFName.ENDOFLINE, PDFBoolean.PDFTRUE);
					//					if ((colorspace & PDFImage.CCITT_ENDOFBLOCK) != 0)
					//						decodeparms.put(PDFName.ENDOFBLOCK, PDFBoolean.PDFFALSE);
					//					decodeparms.put(PDFName.COLUMNS, new PDFNumeric(image.getWidth()));
					//					decodeparms.put(PDFName.ROWS, new PDFNumeric(image.getHeight()));
					//					put(PDFName.DECODEPARMS, decodeparms);

				} else {
					switch(colorspace) {
					case 1:
						put(PDFName.COLORSPACE, PDFName.DEVICEGRAY);
						if (image.isInverted()) {
							put(PDFName.DECODE, new PDFLiteral("[1 0]"));
						}
						break;
					case 3:
						put(PDFName.COLORSPACE, PDFName.DEVICERGB);
						if (image.isInverted()) {
							put(PDFName.DECODE, new PDFLiteral("[1 0 1 0 1 0]"));
						}
						break;
					case 4:
					default:
						put(PDFName.COLORSPACE, PDFName.DEVICECMYK);
						if (image.isInverted()) {
							put(PDFName.DECODE, new PDFLiteral("[1 0 1 0 1 0 1 0]"));
						}
					}

					PDFDictionary additional = image.getAdditional();
					if (additional != null) {
						putAll(additional);
					}

					if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 8)) {
						remove(PDFName.COLORSPACE);
					}

					put(PDFName.BITSPERCOMPONENT, new PDFNumeric(image.getBpc()));
					if (image.isDeflated()) {
						put(PDFName.FILTER, PDFName.FLATEDECODE);
					} else {
						flateCompress(image.getCompressionLevel());
					}
				}
				return;
			}
			// Not get here TODO
			// TODO 不支持
//			// GIF, JPEG or PNG
//			String errorID;
//			if (image.getRawData() == null){
//				throw new IOException("Can't get Image RawData");
//				//				is = image.getUrl().openStream();
//				//				errorID = image.getUrl().toString();
//			}
//			else{
//				is = new java.io.ByteArrayInputStream(image.getRawData());
//				errorID = "Byte array";
//			}
//
//			switch(image.type()) {
//			case PDFImage.JPEG:
//				//TODO
//				break;
//			case PDFImage.JPEG2000:
//				//TODO
//				break;
//			case PDFImage.JBIG2:
//				//TODO
//				break;
//			default:
//				throw new IOException("unknown image format");
//			}
//			if (image.getCompressionLevel() > NO_COMPRESSION) {
//				flateCompress(image.getCompressionLevel());
//			}
//			put(PDFName.LENGTH, new PDFNumeric(streamBytes.size()));
		}
		finally {
			if (is != null) {
				try{
					is.close();
				} catch (Exception ee) {
				}
			}
		}
	}

	public PDFName getName() {
		return name;
	}

	public PDFImage getImage() {
		return image;
	}


	private void generateImgResName(PDFImage img ) {
		name = new PDFName("img" + Long.toHexString(img.getImgId()));
	}



	static final int TRANSFERSIZE = 4096;

	static void transferBytes(InputStream in, OutputStream out, int len) throws IOException {
		byte buffer[] = new byte[TRANSFERSIZE];
		if (len < 0)
			len = 0x7fff0000;
		int size;
		while (len != 0) {
			size = in.read(buffer, 0, Math.min(len, TRANSFERSIZE));
			if (size < 0)
				return;
			out.write(buffer, 0, size);
			len -= size;
		}
	}
}
