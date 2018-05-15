/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.image;

/** 
 * @ClassName: Rectangle <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月14日 上午10:07:32 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class Rectangle {

	/** the lower left x-coordinate. */
	protected float llx;

	/** the lower left y-coordinate. */
	protected float lly;

	/** the upper right x-coordinate. */
	protected float urx;

	/** the upper right y-coordinate. */
	protected float ury;

	/** The rotation of the Rectangle */
	protected int rotation = 0;

	/**
	 * Constructs a <CODE>Rectangle</CODE> -object.
	 *
	 * @param llx	lower left x
	 * @param lly	lower left y
	 * @param urx	upper right x
	 * @param ury	upper right y
	 */
	public Rectangle(final float llx, final float lly, final float urx, final float ury) {
		this.llx = llx;
		this.lly = lly;
		this.urx = urx;
		this.ury = ury;
	}

	/**
	 * Constructs a <CODE>Rectangle</CODE>-object.
	 *
	 * @param llx	lower left x
	 * @param lly	lower left y
	 * @param urx	upper right x
	 * @param ury	upper right y
	 * @param rotation the rotation (0, 90, 180, or 270)
	 * @since iText 5.0.6
	 */
	public Rectangle(final float llx, final float lly, final float urx, final float ury, final int rotation) {
		this(llx, lly, urx, ury);
		setRotation(rotation);
	}
	
	/**
	 * Constructs a <CODE>Rectangle</CODE> -object starting from the origin
	 * (0, 0).
	 *
	 * @param urx	upper right x
	 * @param ury	upper right y
	 */
	public Rectangle(final float urx, final float ury) {
		this(0, 0, urx, ury);
	}

	/**
	 * Sets the lower left x-coordinate.
	 *
	 * @param llx	the new value
	 */
	public void setLeft(final float llx) {
		this.llx = llx;
	}

	/**
	 * Returns the lower left x-coordinate.
	 *
	 * @return the lower left x-coordinate
	 */
	public float getLeft() {
		return llx;
	}

	/**
	 * Returns the lower left x-coordinate, considering a given margin.
	 *
	 * @param margin	a margin
	 * @return the lower left x-coordinate
	 */
	public float getLeft(final float margin) {
		return llx + margin;
	}

	/**
	 * Sets the upper right x-coordinate.
	 *
	 * @param urx	the new value
	 */
	public void setRight(final float urx) {
		this.urx = urx;
	}

	/**
	 * Returns the upper right x-coordinate.
	 *
	 * @return the upper right x-coordinate
	 */
	public float getRight() {
		return urx;
	}

	/**
	 * Returns the upper right x-coordinate, considering a given margin.
	 *
	 * @param margin	a margin
	 * @return the upper right x-coordinate
	 */
	public float getRight(final float margin) {
		return urx - margin;
	}

	/**
	 * Returns the width of the rectangle.
	 *
	 * @return	the width
	 */
	public float getWidth() {
		return urx - llx;
	}

	/**
	 * Sets the upper right y-coordinate.
	 *
	 * @param ury	the new value
	 */
	public void setTop(final float ury) {
		this.ury = ury;
	}

	/**
	 * Returns the upper right y-coordinate.
	 *
	 * @return the upper right y-coordinate
	 */
	public float getTop() {
		return ury;
	}

	/**
	 * Returns the upper right y-coordinate, considering a given margin.
	 *
	 * @param margin	a margin
	 * @return the upper right y-coordinate
	 */
	public float getTop(final float margin) {
		return ury - margin;
	}

	/**
	 * Sets the lower left y-coordinate.
	 *
	 * @param lly	the new value
	 */
	public void setBottom(final float lly) {
		this.lly = lly;
	}

	/**
	 * Returns the lower left y-coordinate.
	 *
	 * @return the lower left y-coordinate
	 */
	public float getBottom() {
		return lly;
	}

	/**
	 * Returns the lower left y-coordinate, considering a given margin.
	 *
	 * @param margin	a margin
	 * @return the lower left y-coordinate
	 */
	public float getBottom(final float margin) {
		return lly + margin;
	}

	/**
	 * Returns the height of the rectangle.
	 *
	 * @return the height
	 */
	public float getHeight() {
		return ury - lly;
	}

	/**
	 * Normalizes the rectangle.
	 * Switches lower left with upper right if necessary.
	 */
	public void normalize() {
		if (llx > urx) {
			float a = llx;
			llx = urx;
			urx = a;
		}
		if (lly > ury) {
			float a = lly;
			lly = ury;
			ury = a;
		}
	}
	

	/**
	 * Gets the rotation of the rectangle
	 *
	 * @return a rotation value
	 */
	public int getRotation() {
		return rotation;
	}

	/**
	 * PDF Page Rotation
	 * @param rotation 
	 * 				int
	 * 				Valid on [ 0, 90, 180, 270]
	 */
	public void setRotation(final int rotation) {
		this.rotation = rotation % 360;
		switch (this.rotation) {
		case 90:
		case 180:
		case 270:
            break;
        default:
			this.rotation = 0;
		}
	}
	
	/**
	 * 
	 * @Title: rotate  
	 * @Description: rotate 90 degree and return new Rectangle  
	 *
	 * @return
	 */
	public Rectangle rotate() {
		Rectangle rect = new Rectangle(lly, llx, ury, urx);
		rect.setRotation(rotation + 90);
		return rect;
	}
	

}
