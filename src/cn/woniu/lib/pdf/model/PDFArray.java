/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cn.woniu.lib.pdf.PDFReader;


/** 
 * @ClassName: PDFArray <br/> 
 * 
 * @since JDK 1.6 
 * @Description: TODO  <br/> 
 * <CODE>PdfArray</CODE> is the PDF Array object.
 * <P>
 * An array is a sequence of PDF objects. An array may contain a mixture of
 * object types.
 * An array is written as a left square bracket ([), followed by a sequence of
 * objects, followed by a right square bracket (]).<BR>
 * 
 * EXAMPLE: <BR>
 * 		<B>[549 3.14 false (Ralph) /SomeName]</B>
 */
public class PDFArray extends PDFObj implements Iterable<PDFObj>  {

	private static final long serialVersionUID = 6061570008807077580L;

	/** Array of PDFObjs */
	protected ArrayList<PDFObj> arrayList;

	public PDFArray() {
		super(ARRAY);
		arrayList = new ArrayList<PDFObj>();
	}

	public PDFArray(int capacity) {
		super(ARRAY);
		arrayList = new ArrayList<PDFObj>(capacity);
	}

	public PDFArray(final PDFObj object) {
		super(ARRAY);
		arrayList = new ArrayList<PDFObj>();
		arrayList.add(object);
	}

	public PDFArray(final float values[]) {
		super(ARRAY);
		arrayList = new ArrayList<PDFObj>();
		add(values);
	}

	public PDFArray(final int values[]) {
		super(ARRAY);
		arrayList = new ArrayList<PDFObj>();
		add(values);
	}

	public PDFArray(final List<PDFObj> list) {
		this();
		for (PDFObj element : list) {
			add(element);
		}

	}

	public PDFArray(final PDFArray array) {
		super(ARRAY);
		arrayList = new ArrayList<PDFObj>(array.arrayList);
	}

	@Override
	public String toString() {
		return arrayList.toString();
	}

	/**
	 * Overwrites a specified location of the array, returning the previous
	 * value
	 *
	 * @param idx The index of the element to be overwritten
	 * @param obj new value for the specified index
	 * @throws IndexOutOfBoundsException if the specified position doesn't exist
	 * @return the previous value
	 * @since 2.1.5
	 */
	public PDFObj set(final int idx, final PDFObj obj) {
		return arrayList.set(idx, obj);
	}

	/**
	 * Remove the element at the specified position from the array.
	 *
	 * Shifts any subsequent elements to the left (subtracts one from their
	 * indices).
	 *
	 * @param idx The index of the element to be removed.
	 * @throws IndexOutOfBoundsException the specified position doesn't exist
	 * @since 2.1.5
	 */
	public PDFObj remove(final int idx) {
		return arrayList.remove(idx);
	}

	/**
	 * Get the internal arrayList for this PDFArray.  Not Recommended.
	 *
	 * @deprecated
	 * @return the internal ArrayList.  Naughty Naughty.
	 */
	@Deprecated
	public ArrayList<PDFObj> getArrayList() {
		return arrayList;
	}

	/**
	 * Returns the number of entries in the array.
	 *
	 * @return		the size of the ArrayList
	 */
	public int size() {
		return arrayList.size();
	}

	/**
	 * Returns <CODE>true</CODE> if the array is empty.
	 *
	 * @return <CODE>true</CODE> if the array is empty
	 * @since 2.1.5
	 */
	public boolean isEmpty() {
		return arrayList.isEmpty();
	}

	/**
	 * Adds a <CODE>PDFObj</CODE> to the end of the <CODE>PDFArray</CODE>.
	 *
	 * The <CODE>PDFObj</CODE> will be the last element.
	 *
	 * @param object <CODE>PDFObj</CODE> to add
	 * @return always <CODE>true</CODE>
	 */
	public boolean add(final PDFObj object) {
		return arrayList.add(object);
	}

	/**
	 * Adds an array of <CODE>float</CODE> values to end of the
	 * <CODE>PDFArray</CODE>.
	 *
	 * The values will be the last elements.
	 * The <CODE>float</CODE> values are internally converted to
	 * <CODE>PDFNumeric</CODE> objects.
	 *
	 * @param values An array of <CODE>float</CODE> values to add
	 * @return always <CODE>true</CODE>
	 */
	public boolean add(final float values[]) {
		for (int k = 0; k < values.length; ++k)
			arrayList.add(new PDFNumeric(values[k]));
		return true;
	}

	/**
	 * Adds an array of <CODE>int</CODE> values to end of the <CODE>PDFArray</CODE>.
	 *
	 * The values will be the last elements.
	 * The <CODE>int</CODE> values are internally converted to
	 * <CODE>PDFNumeric</CODE> objects.
	 *
	 * @param values An array of <CODE>int</CODE> values to add
	 * @return always <CODE>true</CODE>
	 */
	public boolean add(final int values[]) {
		for (int k = 0; k < values.length; ++k)
			arrayList.add(new PDFNumeric(values[k]));
		return true;
	}

	/**
	 * Inserts the specified element at the specified position.
	 *
	 * Shifts the element currently at that position (if any) and
	 * any subsequent elements to the right (adds one to their indices).
	 *
	 * @param index The index at which the specified element is to be inserted
	 * @param element The element to be inserted
	 * @throws IndexOutOfBoundsException if the specified index is larger than the
	 *   last position currently set, plus 1.
	 * @since 2.1.5
	 */
	public void add(final int index, final PDFObj element) {
		arrayList.add(index, element);
	}

	/**
	 * Inserts a <CODE>PDFObj</CODE> at the beginning of the
	 * <CODE>PDFArray</CODE>.
	 *
	 * The <CODE>PDFObj</CODE> will be the first element, any other elements
	 * will be shifted to the right (adds one to their indices).
	 *
	 * @param object The <CODE>PDFObj</CODE> to add
	 */
	public void addFirst(final PDFObj object) {
		arrayList.add(0, object);
	}

	/**
	 * Checks if the <CODE>PDFArray</CODE> already contains a certain
	 * <CODE>PDFObj</CODE>.
	 *
	 * @param object The <CODE>PDFObj</CODE> to check
	 * @return <CODE>true</CODE>
	 */
	public boolean contains(final PDFObj object) {
		return arrayList.contains(object);
	}

	/**
	 * Returns the list iterator for the array.
	 *
	 * @return a ListIterator
	 */
	public ListIterator<PDFObj> listIterator() {
		return arrayList.listIterator();
	}

	/**
	 * Returns the <CODE>PDFObj</CODE> with the specified index.
	 *
	 * A possible indirect references is not resolved, so the returned
	 * <CODE>PDFObj</CODE> may be either a direct object or an indirect
	 * reference, depending on how the object is stored in the
	 * <CODE>PDFArray</CODE>.
	 *
	 * @param idx The index of the <CODE>PDFObj</CODE> to be returned
	 * @return A <CODE>PDFObj</CODE>
	 */
	public PDFObj getPDFObj(final int idx) {
		return arrayList.get(idx);
	}

	/**
	 * Returns the <CODE>PDFObj</CODE> with the specified index, resolving
	 * a possible indirect reference to a direct object.
	 *
	 * Thus this method will never return a <CODE>PdfIndirectReference</CODE>
	 * object.
	 *
	 * @param idx The index of the <CODE>PDFObj</CODE> to be returned
	 * @return A direct <CODE>PDFObj</CODE> or <CODE>null</CODE>
	 */
	public PDFObj getDirectObject(final int index) {
		return PDFReader.getPdfObject(getPDFObj(index));
	}

	/**
	 * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFDictionary</CODE>,
	 * resolving indirect references.
	 *
	 * The object corresponding to the specified index is retrieved and
	 * resolvedto a direct object.
	 * If it is a <CODE>PDFDictionary</CODE>, it is cast down and returned as such.
	 * Otherwise <CODE>null</CODE> is returned.
	 *
	 * @param idx The index of the <CODE>PDFObj</CODE> to be returned
	 * @return the corresponding <CODE>PDFDictionary</CODE> object,
	 *   or <CODE>null</CODE>
	 */
	public PDFDictionary getAsDict(final int idx) {
		PDFDictionary dict = null;
		PDFObj orig = getDirectObject(idx);
		if (orig != null && orig.isDictionary()){
			dict = (PDFDictionary) orig;
		}            
		return dict;
	}

	/**
	 * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFArray</CODE>,
	 * resolving indirect references.
	 *
	 * The object corresponding to the specified index is retrieved and
	 * resolved to a direct object.
	 * If it is a <CODE>PDFArray</CODE>, it is cast down and returned as such.
	 * Otherwise <CODE>null</CODE> is returned.
	 *
	 * @param idx The index of the <CODE>PDFObj</CODE> to be returned
	 * @return the corresponding <CODE>PDFArray</CODE> object,
	 *   or <CODE>null</CODE>
	 */
	public PDFArray getAsArray(final int idx) {
		PDFArray array = null;
		PDFObj orig = getDirectObject(idx);
		if (orig != null && orig.isArray())
			array = (PDFArray) orig;
		return array;
	}

	/**
	 * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFStream</CODE>,
	 * resolving indirect references.
	 *
	 * The object corresponding to the specified index is retrieved and
	 * resolved to a direct object.
	 * If it is a <CODE>PDFStream</CODE>, it is cast down and returned as such.
	 * Otherwise <CODE>null</CODE> is returned.
	 *
	 * @param idx The index of the <CODE>PDFObj</CODE> to be returned
	 * @return the corresponding <CODE>PDFStream</CODE> object,
	 *   or <CODE>null</CODE>
	 */
	public PDFStream getAsStream(final int idx) {
		PDFStream stream = null;
		PDFObj orig = getDirectObject(idx);
		if (orig != null && orig.isStream())
			stream = (PDFStream) orig;
		return stream;
	}

	/**
	 * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFString</CODE>,
	 * resolving indirect references.
	 *
	 * The object corresponding to the specified index is retrieved and
	 * resolved to a direct object.
	 * If it is a <CODE>PDFString</CODE>, it is cast down and returned as such.
	 * Otherwise <CODE>null</CODE> is returned.
	 *
	 * @param idx The index of the <CODE>PDFObj</CODE> to be returned
	 * @return the corresponding <CODE>PDFString</CODE> object,
	 *   or <CODE>null</CODE>
	 */
	public PDFString getAsString(final int idx) {
		PDFString string = null;
		PDFObj orig = getDirectObject(idx);
		if (orig != null && orig.isString())
			string = (PDFString) orig;
		return string;
	}

	/**
	 * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFNumeric</CODE>,
	 * resolving indirect references.
	 *
	 * The object corresponding to the specified index is retrieved and
	 * resolved to a direct object.
	 * If it is a <CODE>PDFNumeric</CODE>, it is cast down and returned as such.
	 * Otherwise <CODE>null</CODE> is returned.
	 *
	 * @param idx The index of the <CODE>PDFObj</CODE> to be returned
	 * @return the corresponding <CODE>PDFNumeric</CODE> object,
	 *   or <CODE>null</CODE>
	 */
	public PDFNumeric getAsNumber(final int idx) {
		PDFNumeric number = null;
		PDFObj orig = getDirectObject(idx);
		if (orig != null && orig.isNumber())
			number = (PDFNumeric) orig;
		return number;
	}

	/**
	 * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFName</CODE>,
	 * resolving indirect references.
	 *
	 * The object corresponding to the specified index is retrieved and
	 * resolved to a direct object.
	 * If it is a <CODE>PDFName</CODE>, it is cast down and returned as such.
	 * Otherwise <CODE>null</CODE> is returned.
	 *
	 * @param idx The index of the <CODE>PDFObj</CODE> to be returned
	 * @return the corresponding <CODE>PDFName</CODE> object,
	 *   or <CODE>null</CODE>
	 */
	public PDFName getAsName(final int idx) {
		PDFName name = null;
		PDFObj orig = getDirectObject(idx);
		if (orig != null && orig.isName())
			name = (PDFName) orig;
		return name;
	}

	/**
	 * Returns a <CODE>PDFObj</CODE> as a <CODE>PdfBoolean</CODE>,
	 * resolving indirect references.
	 *
	 * The object corresponding to the specified index is retrieved and
	 * resolved to a direct object.
	 * If it is a <CODE>PdfBoolean</CODE>, it is cast down and returned as
	 * such. Otherwise <CODE>null</CODE> is returned.
	 *
	 * @param idx The index of the <CODE>PDFObj</CODE> to be returned
	 * @return the corresponding <CODE>PdfBoolean</CODE> object,
	 *   or <CODE>null</CODE>
	 */
	public PDFBoolean getAsBoolean(final int idx) {
		PDFBoolean bool = null;
		PDFObj orig = getDirectObject(idx);
		if (orig != null && orig.isBoolean())
			bool = (PDFBoolean) orig;
		return bool;
	}

	/**
	 * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFIndirectReference</CODE>.
	 *
	 * The object corresponding to the specified index is retrieved.
	 * If it is a <CODE>PDFIndirectReference</CODE>, it is cast down and
	 * returned as such. Otherwise <CODE>null</CODE> is returned.
	 *
	 * @param idx The index of the <CODE>PDFObj</CODE> to be returned
	 * @return the corresponding <CODE>PDFIndirectReference</CODE> object,
	 *   or <CODE>null</CODE>
	 */
	public PDFIndirectReference getAsIndirectObject(final int idx) {
		PDFIndirectReference ref = null;
		PDFObj orig = getPDFObj(idx); // not getDirect this time.
		if (orig instanceof PDFIndirectReference)
			ref = (PDFIndirectReference) orig;
		return ref;
	}

	public Iterator<PDFObj> iterator() {
		return arrayList.iterator();
	}

	public long[] asLongArray(){
		long[] rslt = new long[size()];
		for (int k = 0; k < rslt.length; ++k) {
			rslt[k] = getAsNumber(k).longValue();
		}
		return rslt;
	}

	public double[] asDoubleArray() {
		double[] rslt = new double[size()];
		for (int k = 0; k < rslt.length; ++k) {
			rslt[k] = getAsNumber(k).doubleValue();
		}
		return rslt;
	}


	@Override
	public void write(final OutputStream os) throws IOException {
		os.write('[');

		Iterator<PDFObj> iter = arrayList.iterator();
		PDFObj object;
		int type = 0;
		
		if (iter.hasNext()) {
			object = iter.next();
			if (object == null) {
				object = PDFNull.PDFNULL;
			}
			object.write(os);
		}
		
		while (iter.hasNext()) {
			object = iter.next();
			if (object == null) {
				object = PDFNull.PDFNULL;
			}
			type = object.type();
			if (type != PDFObj.ARRAY 
					&& type != PDFObj.DICTIONARY 
					&& type != PDFObj.NAME 
					&& type != PDFObj.STRING) {
				os.write(' ');
			}
			object.write(os);
		}
		os.write(']');
	}
}
