/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.image;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import cn.woniu.lib.pdf.encode.ByteBuffer;
import cn.woniu.lib.pdf.model.PDFArray;
import cn.woniu.lib.pdf.model.PDFDictionary;
import cn.woniu.lib.pdf.model.PDFName;
import cn.woniu.lib.pdf.model.PDFNumeric;
import cn.woniu.lib.pdf.model.PDFObj;
import cn.woniu.lib.pdf.model.PDFString;
import cn.woniu.lib.pdf.model.derivate.PDFLiteral;
import cn.woniu.lib.pdf.util.Logger;



/** 
 * @ClassName: PNGImage <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月13日 下午9:13:53 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PNGImage {

	private static final int TRANSFERSIZE = 4096;
	private static final PDFName intents[] = {
			PDFName.PERCEPTUAL,
			PDFName.RELATIVECOLORIMETRIC,
			PDFName.SATURATION,
			PDFName.ABSOLUTECOLORIMETRIC};
	
    private static final int PNG_FILTER_NONE = 0;
    private static final int PNG_FILTER_SUB = 1;
    private static final int PNG_FILTER_UP = 2;
    private static final int PNG_FILTER_AVERAGE = 3;
    private static final int PNG_FILTER_PAETH = 4;

	// File 
	private InputStream is;
	private  DataInputStream dataStream;
	
	// Image attribute and data
	private int width;
	private int height;
	private int bitDepth;
	private int colorType;
	private int transRedGray = -1;
	private int transGreen = -1;
	private int transBlue = -1;
	private int inputBands;
	private int bytesPerPixel; // number of bytes per input pixel
	private byte colorTable[];
	private float gamma = 1f;
	private boolean hasCHRM = false;
	private int compressionMethod;
	private int filterMethod;
	private int interlaceMethod;

	private float xW, yW, xR, yR, xG, yG, xB, yB;
	private byte image[];
	private byte smask[];
	private byte trans[];

	private SimpleByteArrayOutputStream idat = new SimpleByteArrayOutputStream();
	private int dpiX;
	private int dpiY;
	private float XYRatio;
	private boolean genBWMask;
	private boolean palShades;
	private ICCProfile icc_profile;

	// PDF Objects
	private PDFDictionary additional = new PDFDictionary();
	private PDFName intent;

	PNGImage(InputStream is) {
		this.is = is;
	}

	public static PDFImage getImage(String file) throws IOException {
		return getImage(new FileInputStream(file));
	}

	public static PDFImage getImage(InputStream is) throws IOException {
		PNGImage png = new PNGImage(is);
		return png.readImage();
	}

	private PDFImage readImage() throws IOException {
		readPng();
//		checkIccProfile();
		
		PDFImage image = decodeImageData();
		return image;
	}

	private void readPng() throws IOException {
		for (int i = 0; i < PNGID.length; i++) {
			if (PNGID[i] != is.read())	{
				throw new IOException(("Not valid png file"));
			}
		}

		byte buffer[] = new byte[TRANSFERSIZE];
		while (true) {
			int len = PngUtil.getInt(is);
			String marker = PngUtil.getString(is);
			if (len < 0 || !PngUtil.checkMarker(marker)) {
				throw new IOException(("corrupted png file"));
			}

			if (IDAT.equals(marker)) {
				int size;
				while (len != 0) {
					size = is.read(buffer, 0, Math.min(len, TRANSFERSIZE));
					if (size < 0)
						return;
					idat.write(buffer, 0, size);
					len -= size;
				}
			}
			else if (tRNS.equals(marker)) {
				switch (colorType) {
				case 0:
					if (len >= 2) {
						len -= 2;
						int gray = PngUtil.getWord(is);
						if (bitDepth == 16)
							transRedGray = gray;
						else
							additional.put(PDFName.MASK, new PDFLiteral("["+gray+" "+gray+"]"));
					}
					break;
				case 2:
					if (len >= 6) {
						len -= 6;
						int red = PngUtil.getWord(is);
						int green = PngUtil.getWord(is);
						int blue = PngUtil.getWord(is);
						if (bitDepth == 16) {
							transRedGray = red;
							transGreen = green;
							transBlue = blue;
						}
						else
							additional.put(PDFName.MASK, new PDFLiteral("["+red+" "+red+" "+green+" "+green+" "+blue+" "+blue+"]"));
					}
					break;
				case 3:
					if (len > 0) {
						trans = new byte[len];
						for (int k = 0; k < len; ++k)
							trans[k] = (byte)is.read();
						len = 0;
					}
					break;
				}
				PngUtil.skip(is, len);
			}
			else if (IHDR.equals(marker)) {
				width = PngUtil.getInt(is);
				height = PngUtil.getInt(is);

				bitDepth = is.read();
				colorType = is.read();
				compressionMethod = is.read();
				filterMethod = is.read();
				interlaceMethod = is.read();
			}
			else if (PLTE.equals(marker)) {
				if (colorType == 3) {
					PDFArray colorspace = new PDFArray();
					colorspace.add(PDFName.INDEXED);
					colorspace.add(getColorspace());
					colorspace.add(new PDFNumeric(len / 3 - 1));
					ByteBuffer colortable = new ByteBuffer();
					while ((len--) > 0) {
						colortable.append_i(is.read());
					}
					colorspace.add(new PDFString(this.colorTable = colortable.toByteArray()));
					additional.put(PDFName.COLORSPACE, colorspace);
				}
				else {
					PngUtil.skip(is, len);
				}
			}
			else if (pHYs.equals(marker)) {
				int dx = PngUtil.getInt(is);
				int dy = PngUtil.getInt(is);
				int unit = is.read();
				if (unit == 1) {
					dpiX = (int)(dx * 0.0254f + 0.5f);
					dpiY = (int)(dy * 0.0254f + 0.5f);
				} else {
					if (dy != 0) {
						XYRatio = (float)dx / (float)dy;
					}
				}
			}
			else if (cHRM.equals(marker)) {
				xW = PngUtil.getInt(is) / 100000f;
				yW = PngUtil.getInt(is) / 100000f;
				xR = PngUtil.getInt(is) / 100000f;
				yR = PngUtil.getInt(is) / 100000f;
				xG = PngUtil.getInt(is) / 100000f;
				yG = PngUtil.getInt(is) / 100000f;
				xB = PngUtil.getInt(is) / 100000f;
				yB = PngUtil.getInt(is) / 100000f;
				hasCHRM = !(Math.abs(xW)<0.0001f||Math.abs(yW)<0.0001f||Math.abs(xR)<0.0001f||Math.abs(yR)<0.0001f||Math.abs(xG)<0.0001f||Math.abs(yG)<0.0001f||Math.abs(xB)<0.0001f||Math.abs(yB)<0.0001f);
			}
			else if (sRGB.equals(marker)) {
				int ri = is.read();
				intent = intents[ri];
				gamma = 2.2f;
				xW = 0.3127f;
				yW = 0.329f;
				xR = 0.64f;
				yR = 0.33f;
				xG = 0.3f;
				yG = 0.6f;
				xB = 0.15f;
				yB = 0.06f;
				hasCHRM = true;
			}
			else if (gAMA.equals(marker)) {
				int gm = PngUtil.getInt(is);
				if (gm != 0) {
					gamma = 100000f / gm;
					if (!hasCHRM) {
						xW = 0.3127f;
						yW = 0.329f;
						xR = 0.64f;
						yR = 0.33f;
						xG = 0.3f;
						yG = 0.6f;
						xB = 0.15f;
						yB = 0.06f;
						hasCHRM = true;
					}
				}
			}
			else if (iCCP.equals(marker)) {
				//TODO NotSupport
//				do {
//					--len;
//				} while (is.read() != 0);
//				is.read();
//				--len;
//				byte icccom[] = new byte[len];
//				int p = 0;
//				while (len > 0) {
//					int r = is.read(icccom, p, len);
//					if (r < 0)
//						throw new IOException(("premature.end.of.file"));
//					p += r;
//					len -= r;
//				}
//				byte iccp[] = PDFReader.FlateDecode(icccom, true);
//				icccom = null;
//				try {
//					icc_profile = ICC_Profile.getInstance(iccp);
//				}
//				catch (RuntimeException e) {
//					icc_profile = null;
//				}
			}
			else if (IEND.equals(marker)) {
				break;
			}
			else {
				PngUtil.skip(is, len);
			}
			PngUtil.skip(is, 4);
		}
	}

	private PDFImage decodeImageData() {
        try {
            int pal0 = 0;
            int palIdx = 0;
            palShades = false;
            if (trans != null) {
                for (int k = 0; k < trans.length; ++k) {
                    int n = trans[k] & 0xff;
                    if (n == 0) {
                        ++pal0;
                        palIdx = k;
                    }
                    if (n != 0 && n != 255) {
                        palShades = true;
                        break;
                    }
                }
            }
            if ((colorType & 4) != 0)
                palShades = true;
            genBWMask = (!palShades && (pal0 > 1 || transRedGray >= 0));
            if (!palShades && !genBWMask && pal0 == 1) {
                additional.put(PDFName.MASK, new PDFLiteral("["+palIdx+" "+palIdx+"]"));
            }
            boolean needDecode = (interlaceMethod == 1) || (bitDepth == 16) || ((colorType & 4) != 0) || palShades || genBWMask;
            switch (colorType) {
                case 0:
                    inputBands = 1;
                    break;
                case 2:
                    inputBands = 3;
                    break;
                case 3:
                    inputBands = 1;
                    break;
                case 4:
                    inputBands = 2;
                    break;
                case 6:
                    inputBands = 4;
                    break;
            }
            if (needDecode) {
                decodeIdat();
            }
            int components = inputBands;
            if ((colorType & 4) != 0)
                --components;
            int bpc = bitDepth;
            if (bpc == 16) {
                bpc = 8;
            }
            PDFImage img;
            if (image != null) {
                if (colorType == 3) {
                    img = new ImageData(width, height, components, bpc, image);
                } else {
            		img = new ImageData(width, height, components, bpc, image);
            		img.transparency = null;
                }
            }
            else {
                img = new ImageData(width, height, components, bpc, idat.toByteArray());
                img.setDeflated(true);
                PDFDictionary decodeparms = new PDFDictionary();
                decodeparms.put(PDFName.BITSPERCOMPONENT, new PDFNumeric(bitDepth));
                decodeparms.put(PDFName.PREDICTOR, new PDFNumeric(15));
                decodeparms.put(PDFName.COLUMNS, new PDFNumeric(width));
                decodeparms.put(PDFName.COLORS, new PDFNumeric((colorType == 3 || (colorType & 2) == 0) ? 1 : 3));
                additional.put(PDFName.DECODEPARMS, decodeparms);
            }
            if (additional.get(PDFName.COLORSPACE) == null)
                additional.put(PDFName.COLORSPACE, getColorspace());
            if (intent != null)
                additional.put(PDFName.INTENT, intent);
            if (additional.size() > 0)
                img.setAdditional(additional);
            if (icc_profile != null) {
//                img.tagICC(icc_profile); // TODO
            }
            if (palShades) {
//                Image im2 = Image.getInstance(width, height, 1, 8, smask);
                PDFImage im2 = new ImageData(width, height, 1, 8, smask);
                im2.makeMask();
                img.setImageMask(im2);
            }
            if (genBWMask) {
            	PDFImage im2 = new ImageData(width, height, 1, 1, smask);
                im2.makeMask();
                img.setImageMask(im2);
            }
            img.setDpi(dpiX, dpiY);
            img.setXYRatio(XYRatio);
            img.setOriginalType(PDFImage.ORIGINAL_PNG);
            return img;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
	
		
	}

	private PDFObj getColorspace() {
		if (icc_profile != null) {
			if ((colorType & 2) == 0)
				return PDFName.DEVICEGRAY;
			else
				return PDFName.DEVICERGB;
		}
		if (gamma == 1f && !hasCHRM) {
			if ((colorType & 2) == 0)
				return PDFName.DEVICEGRAY;
			else
				return PDFName.DEVICERGB;
		}
		else {
			PDFArray array = new PDFArray();
			PDFDictionary dic = new PDFDictionary();
			if ((colorType & 2) == 0) {
				if (gamma == 1f)
					return PDFName.DEVICEGRAY;
				array.add(PDFName.CALGRAY);
				dic.put(PDFName.GAMMA, new PDFNumeric(gamma));
				dic.put(PDFName.WHITEPOINT, new PDFLiteral("[1 1 1]"));
				array.add(dic);
			}
			else {
				PDFObj wp = new PDFLiteral("[1 1 1]");
				array.add(PDFName.CALRGB);
				if (gamma != 1f) {
					PDFArray gm = new PDFArray();
					PDFNumeric n = new PDFNumeric(gamma);
					gm.add(n);
					gm.add(n);
					gm.add(n);
					dic.put(PDFName.GAMMA, gm);
				}
				if (hasCHRM) {
					float z = yW*((xG-xB)*yR-(xR-xB)*yG+(xR-xG)*yB);
					float YA = yR*((xG-xB)*yW-(xW-xB)*yG+(xW-xG)*yB)/z;
					float XA = YA*xR/yR;
					float ZA = YA*((1-xR)/yR-1);
					float YB = -yG*((xR-xB)*yW-(xW-xB)*yR+(xW-xR)*yB)/z;
					float XB = YB*xG/yG;
					float ZB = YB*((1-xG)/yG-1);
					float YC = yB*((xR-xG)*yW-(xW-xG)*yW+(xW-xR)*yG)/z;
					float XC = YC*xB/yB;
					float ZC = YC*((1-xB)/yB-1);
					float XW = XA+XB+XC;
					float YW = 1;//YA+YB+YC;
					float ZW = ZA+ZB+ZC;
					PDFArray wpa = new PDFArray();
					wpa.add(new PDFNumeric(XW));
					wpa.add(new PDFNumeric(YW));
					wpa.add(new PDFNumeric(ZW));
					wp = wpa;
					PDFArray matrix = new PDFArray();
					matrix.add(new PDFNumeric(XA));
					matrix.add(new PDFNumeric(YA));
					matrix.add(new PDFNumeric(ZA));
					matrix.add(new PDFNumeric(XB));
					matrix.add(new PDFNumeric(YB));
					matrix.add(new PDFNumeric(ZB));
					matrix.add(new PDFNumeric(XC));
					matrix.add(new PDFNumeric(YC));
					matrix.add(new PDFNumeric(ZC));
					dic.put(PDFName.MATRIX, matrix);
				}
				dic.put(PDFName.WHITEPOINT, wp);
				array.add(dic);
			}
			return array;
		}
	}

	void decodeIdat() {
        int nbitDepth = bitDepth;
        if (nbitDepth == 16)
            nbitDepth = 8;
        int size = -1;
        bytesPerPixel = (bitDepth == 16) ? 2 : 1;
        switch (colorType) {
            case 0:
                size = (nbitDepth * width + 7) / 8 * height;
                break;
            case 2:
                size = width * 3 * height;
                bytesPerPixel *= 3;
                break;
            case 3:
                if (interlaceMethod == 1)
                    size = (nbitDepth * width + 7) / 8 * height;
                bytesPerPixel = 1;
                break;
            case 4:
                size = width * height;
                bytesPerPixel *= 2;
                break;
            case 6:
                size = width * 3 * height;
                bytesPerPixel *= 4;
                break;
        }
        if (size >= 0)
            image = new byte[size];
        if (palShades)
            smask = new byte[width * height];
        else if (genBWMask)
            smask = new byte[(width + 7) / 8 * height];
        ByteArrayInputStream bai = new ByteArrayInputStream(idat.getBuf(), 0, idat.size());
        InputStream infStream = new InflaterInputStream(bai, new Inflater());
        this.dataStream = new DataInputStream(infStream);
        
        if (interlaceMethod != 1) {
            decodePass(0, 0, 1, 1, width, height);
        }
        else {
            decodePass(0, 0, 8, 8, (width + 7)/8, (height + 7)/8);
            decodePass(4, 0, 8, 8, (width + 3)/8, (height + 7)/8);
            decodePass(0, 4, 4, 8, (width + 3)/4, (height + 3)/8);
            decodePass(2, 0, 4, 4, (width + 1)/4, (height + 3)/4);
            decodePass(0, 2, 2, 4, (width + 1)/2, (height + 1)/4);
            decodePass(1, 0, 2, 2, width/2, (height + 1)/2);
            decodePass(0, 1, 1, 2, width, height/2);
        }

        try {
            this.dataStream.close();
        } catch (IOException e) {
        	Logger.Error("Datastream of PngImage#decodeIdat didn't close properly.");
        	e.printStackTrace();
        }
    }
    
    void decodePass( int xOffset, int yOffset,
    int xStep, int yStep,
    int passWidth, int passHeight) {
        if ((passWidth == 0) || (passHeight == 0)) {
            return;
        }
        
        int bytesPerRow = (inputBands*passWidth*bitDepth + 7)/8;
        byte[] curr = new byte[bytesPerRow];
        byte[] prior = new byte[bytesPerRow];
        
        // Decode the (sub)image row-by-row
        int srcY, dstY;
        for (srcY = 0, dstY = yOffset;
        srcY < passHeight;
        srcY++, dstY += yStep) {
            // Read the filter type byte and a row of data
            int filter = 0;
            try {
                filter = dataStream.read();
                dataStream.readFully(curr, 0, bytesPerRow);
            } catch (Exception e) {
                // empty on purpose
            }
            
            switch (filter) {
                case PNG_FILTER_NONE:
                    break;
                case PNG_FILTER_SUB:
                    decodeSubFilter(curr, bytesPerRow, bytesPerPixel);
                    break;
                case PNG_FILTER_UP:
                    decodeUpFilter(curr, prior, bytesPerRow);
                    break;
                case PNG_FILTER_AVERAGE:
                    decodeAverageFilter(curr, prior, bytesPerRow, bytesPerPixel);
                    break;
                case PNG_FILTER_PAETH:
                    decodePaethFilter(curr, prior, bytesPerRow, bytesPerPixel);
                    break;
                default:
                    // Error -- uknown filter type
                    throw new RuntimeException("Unknown png filter");
            }
            
            processPixels(curr, xOffset, xStep, dstY, passWidth);
            
            // Swap curr and prior
            byte[] tmp = prior;
            prior = curr;
            curr = tmp;
        }
    }
    
    void processPixels(byte curr[], int xOffset, int step, int y, int width) {
        int srcX, dstX;

        int out[] = getPixel(curr);
        int sizes = 0;
        switch (colorType) {
            case 0:
            case 3:
            case 4:
                sizes = 1;
                break;
            case 2:
            case 6:
                sizes = 3;
                break;
        }
        if (image != null) {
            dstX = xOffset;
            int yStride = (sizes*this.width*(bitDepth == 16 ? 8 : bitDepth)+ 7)/8;
            for (srcX = 0; srcX < width; srcX++) {
                setPixel(image, out, inputBands * srcX, sizes, dstX, y, bitDepth, yStride);
                dstX += step;
            }
        }
        if (palShades) {
            if ((colorType & 4) != 0) {
                if (bitDepth == 16) {
                    for (int k = 0; k < width; ++k)
                        out[k * inputBands + sizes] >>>= 8;
                }
                int yStride = this.width;
                dstX = xOffset;
                for (srcX = 0; srcX < width; srcX++) {
                    setPixel(smask, out, inputBands * srcX + sizes, 1, dstX, y, 8, yStride);
                    dstX += step;
                }
            }
            else { //colorType 3
                int yStride = this.width;
                int v[] = new int[1];
                dstX = xOffset;
                for (srcX = 0; srcX < width; srcX++) {
                    int idx = out[srcX];
                    if (idx < trans.length)
                        v[0] = trans[idx];
                    else
                    	v[0] = 255; // Patrick Valsecchi
                    setPixel(smask, v, 0, 1, dstX, y, 8, yStride);
                    dstX += step;
                }
            }
        }
        else if (genBWMask) {
            switch (colorType) {
                case 3: {
                    int yStride = (this.width + 7) / 8;
                    int v[] = new int[1];
                    dstX = xOffset;
                    for (srcX = 0; srcX < width; srcX++) {
                        int idx = out[srcX];
                        v[0] = ((idx < trans.length && trans[idx] == 0) ? 1 : 0);
                        setPixel(smask, v, 0, 1, dstX, y, 1, yStride);
                        dstX += step;
                    }
                    break;
                }
                case 0: {
                    int yStride = (this.width + 7) / 8;
                    int v[] = new int[1];
                    dstX = xOffset;
                    for (srcX = 0; srcX < width; srcX++) {
                        int g = out[srcX];
                        v[0] = (g == transRedGray ? 1 : 0);
                        setPixel(smask, v, 0, 1, dstX, y, 1, yStride);
                        dstX += step;
                    }
                    break;
                }
                case 2: {
                    int yStride = (this.width + 7) / 8;
                    int v[] = new int[1];
                    dstX = xOffset;
                    for (srcX = 0; srcX < width; srcX++) {
                        int markRed = inputBands * srcX;
                        v[0] = (out[markRed] == transRedGray && out[markRed + 1] == transGreen 
                            && out[markRed + 2] == transBlue ? 1 : 0);
                        setPixel(smask, v, 0, 1, dstX, y, 1, yStride);
                        dstX += step;
                    }
                    break;
                }
            }
        }
    }
    

    
    static int getPixel(byte image[], int x, int y, int bitDepth, int bytesPerRow) {
        if (bitDepth == 8) {
            int pos = bytesPerRow * y + x;
            return image[pos] & 0xff;
        }
        else {
            int pos = bytesPerRow * y + x / (8 / bitDepth);
            int v = image[pos] >> (8 - bitDepth * (x % (8 / bitDepth))- bitDepth);
            return v & ((1 << bitDepth) - 1);
        }
    }
    
    static void setPixel(byte image[], int data[], int offset, int size, int x, int y, int bitDepth, int bytesPerRow) {
        if (bitDepth == 8) {
            int pos = bytesPerRow * y + size * x;
            for (int k = 0; k < size; ++k)
                image[pos + k] = (byte)data[k + offset];
        }
        else if (bitDepth == 16) {
            int pos = bytesPerRow * y + size * x;
            for (int k = 0; k < size; ++k)
                image[pos + k] = (byte)(data[k + offset] >>> 8);
        }
        else {
            int pos = bytesPerRow * y + x / (8 / bitDepth);
            int v = data[offset] << (8 - bitDepth * (x % (8 / bitDepth))- bitDepth);
            image[pos] |= v;
        }
    }
    
    int[] getPixel(byte curr[]) {
        switch (bitDepth) {
            case 8: {
                int out[] = new int[curr.length];
                for (int k = 0; k < out.length; ++k)
                    out[k] = curr[k] & 0xff;
                return out;
            }
            case 16: {
                int out[] = new int[curr.length / 2];
                for (int k = 0; k < out.length; ++k)
                    out[k] = ((curr[k * 2] & 0xff) << 8) + (curr[k * 2 + 1] & 0xff);
                return out;
            }
            default: {
                int out[] = new int[curr.length * 8 / bitDepth];
                int idx = 0;
                int passes = 8 / bitDepth;
                int mask = (1 << bitDepth) - 1;
                for (int k = 0; k < curr.length; ++k) {
                    for (int j = passes - 1; j >= 0; --j) {
                        out[idx++] = (curr[k] >>> (bitDepth * j)) & mask; 
                    }
                }
                return out;
            }
        }
    }
    
    private static void decodeSubFilter(byte[] curr, int count, int bpp) {
        for (int i = bpp; i < count; i++) {
            int val;
            
            val = curr[i] & 0xff;
            val += curr[i - bpp] & 0xff;
            
            curr[i] = (byte)val;
        }
    }
    
    private static void decodeUpFilter(byte[] curr, byte[] prev,
    int count) {
        for (int i = 0; i < count; i++) {
            int raw = curr[i] & 0xff;
            int prior = prev[i] & 0xff;
            
            curr[i] = (byte)(raw + prior);
        }
    }
    
    private static void decodeAverageFilter(byte[] curr, byte[] prev,
    int count, int bpp) {
        int raw, priorPixel, priorRow;
        
        for (int i = 0; i < bpp; i++) {
            raw = curr[i] & 0xff;
            priorRow = prev[i] & 0xff;
            
            curr[i] = (byte)(raw + priorRow/2);
        }
        
        for (int i = bpp; i < count; i++) {
            raw = curr[i] & 0xff;
            priorPixel = curr[i - bpp] & 0xff;
            priorRow = prev[i] & 0xff;
            
            curr[i] = (byte)(raw + (priorPixel + priorRow)/2);
        }
    }
    
    private static int paethPredictor(int a, int b, int c) {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);
        
        if ((pa <= pb) && (pa <= pc)) {
            return a;
        } else if (pb <= pc) {
            return b;
        } else {
            return c;
        }
    }
    
    private static void decodePaethFilter(byte[] curr, byte[] prev,
    int count, int bpp) {
        int raw, priorPixel, priorRow, priorRowPixel;
        
        for (int i = 0; i < bpp; i++) {
            raw = curr[i] & 0xff;
            priorRow = prev[i] & 0xff;
            
            curr[i] = (byte)(raw + priorRow);
        }
        
        for (int i = bpp; i < count; i++) {
            raw = curr[i] & 0xff;
            priorPixel = curr[i - bpp] & 0xff;
            priorRow = prev[i] & 0xff;
            priorRowPixel = prev[i - bpp] & 0xff;
            
            curr[i] = (byte)(raw + paethPredictor(priorPixel,
            priorRow,
            priorRowPixel));
        }
    }

	/** PNG 图像标志位:  0x89 50 4e 47 0d 0a 1a 0a */
	public static final int[] PNGID = {0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};

	public static final String IHDR = "IHDR";

	public static final String PLTE = "PLTE";

	public static final String IDAT = "IDAT";

	public static final String IEND = "IEND";

	public static final String tRNS = "tRNS";

	public static final String pHYs = "pHYs";

	public static final String gAMA = "gAMA";

	public static final String cHRM = "cHRM";

	public static final String sRGB = "sRGB";

	public static final String iCCP = "iCCP";
	
	public static void main(String[] args) {
		try {
			PDFImage image = PNGImage.getImage("E:\\Projects\\Github\\p.png");
			image.setAbsolutePosition(111, 222);
			image.setRotationDegrees(45);
			image.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
