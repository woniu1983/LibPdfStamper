/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.image;

import java.io.IOException;
import java.net.URL;

import cn.woniu.lib.pdf.model.PDFDictionary;
import cn.woniu.lib.pdf.model.PDFIndirectReference;
import cn.woniu.lib.pdf.model.PDFStream;

/** 
 * @ClassName: PDFImage <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月13日 下午8:53:03 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFImage extends Rectangle {

    // attributes

	/** The image type. */
	protected int type;

	/** The URL of the image. */
	protected URL url;

	/** The raw data of the image. */
	protected byte rawData[];

	/** The bits per component of the raw image(1, 2, 4, 8). It also flags a CCITT image. */
	protected int bpc = 1;

	/** this is the colorspace. */
	protected int colorspace = -1;

    // rotation, note that the superclass also has a rotation value.

	/** This is the rotation of the image in radians. */
	protected float rotationRadians;

    /** Holds value of property initialRotation. */
    private float initialRotation;

	/** This is the absolute X-position of the image. */
	protected float absoluteX = Float.NaN;

	/** This is the absolute Y-position of the image. */
	protected float absoluteY = Float.NaN;

	/** This is the width of the image without rotation. */
	protected float plainWidth;

	/** This is the width of the image without rotation. */
	protected float plainHeight;

	/** This is the scaled width of the image taking rotation into account. */
	protected float scaledWidth;

	/** This is the original height of the image taking rotation into account. */
	protected float scaledHeight;

	// DPI info

	/** Holds value of property dpiX. */
	protected int dpiX = 0;

	/** Holds value of property dpiY. */
	protected int dpiY = 0;

	// XY Ratio

	/** Holds value of property XYRatio. */
	private float XYRatio = 0;

	/**
	 * Gets the X/Y pixel dimensionless aspect ratio.
	 *
	 * @return the X/Y pixel dimensionless aspect ratio
	 */
	public float getXYRatio() {
		return this.XYRatio;
	}

	/**
	 * Sets the X/Y pixel dimensionless aspect ratio.
	 *
	 * @param XYRatio
	 *            the X/Y pixel dimensionless aspect ratio
	 */
	public void setXYRatio(final float XYRatio) {
		this.XYRatio = XYRatio;
	}

	

	// original type and data

	/** Holds value of property originalType. */
	protected int originalType = ORIGINAL_NONE;

	/** Holds value of property originalData. */
	protected byte[] originalData;

	/**
	 * Getter for property originalType.
	 *
	 * @return Value of property originalType.
	 *
	 */
	public int getOriginalType() {
		return this.originalType;
	}

	/**
	 * Setter for property originalType.
	 *
	 * @param originalType
	 *            New value of property originalType.
	 *
	 */
	public void setOriginalType(final int originalType) {
		this.originalType = originalType;
	}
	
	public PDFImage(final URL url) {
		super(0, 0);
		this.url = url;
//		this.alignment = DEFAULT;
		rotationRadians = 0;
	}

	// the following values are only set for specific types of images.


	/** Is this image a mask? */
	protected boolean mask = false;

	/** The image that serves as a mask for this image. */
	protected PDFImage imageMask;

	/** Holds value of property smask. */
	private boolean smask;

	/**
	 * Returns <CODE>true</CODE> if this <CODE>Image</CODE> is a mask.
	 *
	 * @return <CODE>true</CODE> if this <CODE>Image</CODE> is a mask
	 */
	public boolean isMask() {
		return mask;
	}

	/**
	 * Make this <CODE>Image</CODE> a mask.
	 *
	 * @throws DocumentException
	 *             if this <CODE>Image</CODE> can not be a mask
	 */
	public void makeMask() throws IOException {
		if (!isMaskCandidate())
			throw new IOException("this.image.can.not.be.an.image.mask");
		mask = true;
	}

	/**
	 * Returns <CODE>true</CODE> if this <CODE>Image</CODE> has the
	 * requisites to be a mask.
	 *
	 * @return <CODE>true</CODE> if this <CODE>Image</CODE> can be a mask
	 */
	public boolean isMaskCandidate() {
		if (type == IMGRAW) {
			if (bpc > 0xff)
				return true;
		}
		return colorspace == 1;
	}

	/**
	 * Gets the explicit masking.
	 *
	 * @return the explicit masking
	 */
	public PDFImage getImageMask() {
		return imageMask;
	}

	/**
	 * Sets the explicit masking.
	 *
	 * @param mask
	 *            the mask to be applied
	 * @throws DocumentException
	 *             on error
	 */
	public void setImageMask(final PDFImage mask) throws IOException {
		if (this.mask)
			throw new IOException("an.image.mask.cannot.contain.another.image.mask");
		if (!mask.mask)
			throw new IOException("the.image.mask.is.not.a.mask.did.you.do.makemask");
		imageMask = mask;
		smask = mask.bpc > 1 && mask.bpc <= 8;
	}

	/**
	 * Getter for property smask.
	 *
	 * @return Value of property smask.
	 *
	 */
	public boolean isSmask() {
		return this.smask;
	}

	/**
	 * Setter for property smask.
	 *
	 * @param smask
	 *            New value of property smask.
	 */
	public void setSmask(final boolean smask) {
		this.smask = smask;
	}

	/** this is the transparency information of the raw image */
	protected int transparency[];

	/** Holds value of property deflated. */
	protected boolean deflated = false;

	/**
	 * Getter for property deflated.
	 *
	 * @return Value of property deflated.
	 *
	 */
	public boolean isDeflated() {
		return this.deflated;
	}

	/**
	 * Setter for property deflated.
	 *
	 * @param deflated
	 *            New value of property deflated.
	 */
	public void setDeflated(final boolean deflated) {
		this.deflated = deflated;
	}

	/** a dictionary with additional information */
	private PDFDictionary additional = null;

	/**
	 * Getter for the dictionary with additional information.
	 *
	 * @return a PdfDictionary with additional information.
	 */
	public PDFDictionary getAdditional() {
		return this.additional;
	}

	/**
	 * Sets the /Colorspace key.
	 *
	 * @param additional
	 *            a PdfDictionary with additional information.
	 */
	public void setAdditional(final PDFDictionary additional) {
		this.additional = additional;
	}

	/**
	 * Gets the dots-per-inch in the X direction. Returns 0 if not available.
	 *
	 * @return the dots-per-inch in the X direction
	 */
	public int getDpiX() {
		return dpiX;
	}

	/**
	 * Gets the dots-per-inch in the Y direction. Returns 0 if not available.
	 *
	 * @return the dots-per-inch in the Y direction
	 */
	public int getDpiY() {
		return dpiY;
	}

	/**
	 * Sets the dots per inch value
	 *
	 * @param dpiX
	 *            dpi for x coordinates
	 * @param dpiY
	 *            dpi for y coordinates
	 */
	public void setDpi(final int dpiX, final int dpiY) {
		this.dpiX = dpiX;
		this.dpiY = dpiY;
	}
	

	public int getBpc() {
		return this.bpc;
	}
	
	public boolean isImgRaw() {
		return type == IMGRAW;
	}

	public byte[] getRawData() {
		return this.rawData;
	}

	public int getColorspace() {
		return this.colorspace;
	}
	
	/** Image color inversion */
	protected boolean invert = false;

	public boolean isInverted() {
		return this.invert;
	}

	public int[] getTransparency() {
		return this.transparency;
	}

    protected int compressionLevel = PDFStream.DEFAULT_COMPRESSION; // BEST_SPEED

	public int getCompressionLevel() {
		return this.compressionLevel;
	}

	// Rotation and Trans
	/**
	 * 设置图片在PDF页面偏移的绝对位置（视觉左下角为原点）
	 *
	 * @param absoluteX
	 * @param absoluteY
	 */
	public void setAbsolutePosition(final float absoluteX, final float absoluteY) {
		this.absoluteX = absoluteX;
		this.absoluteY = absoluteY;
	}

	public boolean hasAbsoluteX() {
		return !Float.isNaN(absoluteX);
	}

	public float getAbsoluteX() {
		return absoluteX;
	}

	public boolean hasAbsoluteY() {
		return !Float.isNaN(absoluteY);
	}

	public float getAbsoluteY() {
		return absoluteY;
	}

	/**
	 * <B>setRotationDegrees</B><BR>
	 * calc image rotation to matrix.
	 * 
	 * @param degree 
	 *            
	 */
	public void setRotationDegrees(final float degree) {
		double d = Math.PI;
		setRotation(degree / 180 * (float) d);
	}

	/**
	 * Sets the rotation of the image in radians.
	 *
	 * @param r
	 *            rotation in radians
	 */
	private void setRotation(final float r) {
		double d = 2.0 * Math.PI;
		rotationRadians = (float) ((r + initialRotation) % d);
		if (rotationRadians < 0) {
			rotationRadians += d;
		}
		float[] matrix = matrix();
		scaledWidth = matrix[DX] - matrix[CX];
		scaledHeight = matrix[DY] - matrix[CY];
	}
	

	public float getScaledWidth() {
		return scaledWidth;
	}

	public float getScaledHeight() {
		return scaledHeight;
	}

	/**
	 * Returns the transformation matrix of the image.
	 *
	 * @return an array [AX, AY, BX, BY, CX, CY, DX, DY]
	 */
	public float[] matrix() {
		return matrix(1);
	}

	/**
	 * Returns the transformation matrix of the image.
	 *
	 * @return an array [AX, AY, BX, BY, CX, CY, DX, DY]
	 */
	public float[] matrix(float scalePercentage) {
		float[] matrix = new float[8];
		float cosX = (float) Math.cos(rotationRadians);
		float sinX = (float) Math.sin(rotationRadians);
		matrix[AX] = plainWidth * cosX * scalePercentage;
		matrix[AY] = plainWidth * sinX * scalePercentage;
		matrix[BX] = -plainHeight * sinX * scalePercentage;
		matrix[BY] = plainHeight * cosX * scalePercentage;
		if (rotationRadians < Math.PI / 2f) {
			matrix[CX] = matrix[BX];
			matrix[CY] = 0;
			matrix[DX] = matrix[AX];
			matrix[DY] = matrix[AY] + matrix[BY];
		} else if (rotationRadians < Math.PI) {
			matrix[CX] = matrix[AX] + matrix[BX];
			matrix[CY] = matrix[BY];
			matrix[DX] = 0;
			matrix[DY] = matrix[AY];
		} else if (rotationRadians < Math.PI * 1.5f) {
			matrix[CX] = matrix[AX];
			matrix[CY] = matrix[AY] + matrix[BY];
			matrix[DX] = matrix[BX];
			matrix[DY] = 0;
		} else {
			matrix[CX] = 0;
			matrix[CY] = matrix[AY];
			matrix[DX] = matrix[AX] + matrix[BX];
			matrix[DY] = matrix[BY];
		}
		return matrix;
	}
	


	/**
	 * Gets the type of the text element.
	 *
	 * @return a type
	 */
	public int type() {
		return this.type;
	}

    // image from indirect reference

    /**
     * Holds value of property directReference.
     * An image is embedded into a PDF as an Image XObject.
     * This object is referenced by a PdfIndirectReference object.
     */
    private PDFIndirectReference directReference;

    /**
     * Getter for property directReference.
     * @return Value of property directReference.
     */
    public PDFIndirectReference getDirectReference() {
        return this.directReference;
    }

    /**
     * Setter for property directReference.
     * @param directReference New value of property directReference.
     */
    public void setDirectReference(final PDFIndirectReference directReference) {
        this.directReference = directReference;
    }
    

    protected Long imgId = getSerialId();
    		
	/**
	 * Returns a serial id for the Image (reuse the same image more than once)
	 *
	 * @return a serialId
	 */
	public Long getImgId() {
		return this.imgId;
	}

	// serial stamping

	/** a static that is used for attributing a unique id to each image. */
	private static long serialId = 0L;

	/** Creates a new serial id.
	 * @return the new serialId */
	static protected synchronized Long getSerialId() {
		++serialId;
		return Long.valueOf(serialId);
	}
    
    // Original Image Type
	/** type of image */
	public static final int ORIGINAL_NONE = 0;

	/** type of image */
	public static final int ORIGINAL_JPEG = 1;

	/** type of image */
	public static final int ORIGINAL_PNG = 2;

	/** type of image */
	public static final int ORIGINAL_GIF = 3;

	/** type of image */
	public static final int ORIGINAL_BMP = 4;

	/** type of image */
	public static final int ORIGINAL_TIFF = 5;

	/** type of image */
	public static final int ORIGINAL_WMF = 6;

	/** type of image */
    public static final int ORIGINAL_PS = 7;

	/** type of image */
	public static final int ORIGINAL_JPEG2000 = 8;

	/** type of image */
	public static final int ORIGINAL_JBIG2 = 9;    


	
	// Data Type
	public static final int PTABLE = 23;

	public static final int ANNOTATION = 29;

	public static final int RECTANGLE = 30;

	public static final int JPEG = 32;

	public static final int JPEG2000 = 33;

	public static final int IMGRAW = 34;

	public static final int IMGTEMPLATE = 35;
	
	public static final int JBIG2 = 36;
	
	// coordinate
	
	/** This represents a coordinate in the transformation matrix. */
	public static final int AX = 0;

	/** This represents a coordinate in the transformation matrix. */
	public static final int AY = 1;

	/** This represents a coordinate in the transformation matrix. */
	public static final int BX = 2;

	/** This represents a coordinate in the transformation matrix. */
	public static final int BY = 3;

	/** This represents a coordinate in the transformation matrix. */
	public static final int CX = 4;

	/** This represents a coordinate in the transformation matrix. */
	public static final int CY = 5;

	/** This represents a coordinate in the transformation matrix. */
	public static final int DX = 6;

	/** This represents a coordinate in the transformation matrix. */
	public static final int DY = 7;

}
