/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import cn.woniu.lib.pdf.PDFReader;


/** 
 * @ClassName: PDFDictionary <br/> 
 * @Description: TODO  <br/> 
 * 
 * <CODE>PdfDictionary</CODE> is the Pdf dictionary object.
 * <P>
 * A dictionary is an associative table containing pairs of objects.
 * The first element of each pair is called the <I>key</I> and the second
 * element is called the <I>value</I>.
 * Unlike dictionaries in the PostScript language, a key must be a
 * <CODE>PdfName</CODE>.
 * A value can be any kind of <CODE>PdfObject</CODE>, including a dictionary.
 * A dictionary is generally used to collect and tie together the attributes
 * of a complex object, with each key-value pair specifying the name and value
 * of an attribute.<BR>
 * A dictionary is represented by two left angle brackets (<<), followed by a
 * sequence of key-value pairs, followed by two right angle brackets (>>).<BR>
 * This object is described in the 'Portable Document Format Reference Manual
 * version 1.7' section 3.2.6 (page 59-60).
 * 
 * EXAMPLE<BR>
 *  <B><< /Type /Example<BR>
 *  /Subtype /DictionaryExample<BR>
 *  /Version 0.01<BR>
 *  /SubDictionary << /Item1 0.4<BR>
 *  				  /Item2 true >><BR>
 *  >></B><BR>
 */
public class PDFDictionary extends PDFObj {

	private static final long serialVersionUID = 8540060144684079158L;

	/** This is a possible type of dictionary */
    public static final PDFName FONT = PDFName.FONT;

    /** This is a possible type of dictionary */
    public static final PDFName OUTLINES = PDFName.OUTLINES;

    /** This is a possible type of dictionary */
    public static final PDFName PAGE = PDFName.PAGE;

    /** This is a possible type of dictionary */
    public static final PDFName PAGES = PDFName.PAGES;

    /** This is a possible type of dictionary */
    public static final PDFName CATALOG = PDFName.CATALOG;

    /** This is the type of this dictionary */
    private PDFName dictionaryType = null;

    /** This is the hashmap that contains all the values and keys of the dictionary */
    protected LinkedHashMap<PDFName, PDFObj> hashMap;
    
    public PDFDictionary() {
        super(DICTIONARY);
        hashMap = new LinkedHashMap<PDFName, PDFObj>();
    }

    public PDFDictionary(int capacity) {
        super(DICTIONARY);
        hashMap = new LinkedHashMap<PDFName, PDFObj>(capacity);
    }

    public PDFDictionary(final PDFName type) {
        this();
        dictionaryType = type;
        put(PDFName.TYPE, dictionaryType);
    }

    @Override
    public String toString() {
        if (get(PDFName.TYPE) == null)
            return "Dictionary";
        return "Dictionary of type: " + get(PDFName.TYPE);
    }

    // DICTIONARY CONTENT METHODS

    /**
     * Associates the specified <CODE>PDFObj</CODE> as <VAR>value</VAR> with
     * the specified <CODE>PDFName</CODE> as <VAR>key</VAR> in this map.
     *
     * If the map previously contained a mapping for this <VAR>key</VAR>, the
     * old <VAR>value</VAR> is replaced. If the <VAR>value</VAR> is
     * <CODE>null</CODE> or <CODE>PdfNull</CODE> the key is deleted.
     *
     * @param key a <CODE>PDFName</CODE>
     * @param object the <CODE>PDFObj</CODE> to be associated with the
     *   <VAR>key</VAR>
     */
    public void put(final PDFName key, final PDFObj object) {
        if (key == null)
            throw new IllegalArgumentException("key is null");
        if (object == null || object.isNull())
            hashMap.remove(key);
        else
            hashMap.put(key, object);
    }

    /**
     * Associates the specified <CODE>PDFObj</CODE> as value to the
     * specified <CODE>PDFName</CODE> as key in this map.
     *
     * If the <VAR>value</VAR> is a <CODE>PdfNull</CODE>, it is treated just as
     * any other <CODE>PDFObj</CODE>. If the <VAR>value</VAR> is
     * <CODE>null</CODE> however nothing is done.
     *
     * @param key a <CODE>PDFName</CODE>
     * @param value the <CODE>PDFObj</CODE> to be associated to the
     * <VAR>key</VAR>
     */
    public void putEx(final PDFName key, final PDFObj value) {
        if (key == null)
            throw new IllegalArgumentException("key is null");
        if (value == null)
            return;
        put(key, value);
    }

    /**
     * Copies all of the mappings from the specified <CODE>PDFDictionary</CODE>
     * to this <CODE>PDFDictionary</CODE>.
     *
     * These mappings will replace any mappings previously contained in this
     * <CODE>PDFDictionary</CODE>.
     *
     * @param dic The <CODE>PDFDictionary</CODE> with the mappings to be
     *   copied over
     */
    public void putAll(final PDFDictionary dic) {
        hashMap.putAll(dic.hashMap);
    }

    /**
     * Removes a <CODE>PDFObj</CODE> and its <VAR>key</VAR> from the
     * <CODE>PDFDictionary</CODE>.
     *
     * @param key a <CODE>PDFName</CODE>
     */
    public void remove(final PDFName key) {
        hashMap.remove(key);
    }

    /**
     * Removes all the <CODE>PDFObj</CODE>s and its <VAR>key</VAR>s from the
     * <CODE>PDFDictionary</CODE>.
     * @since 5.0.2
     */
    public void clear() {
        hashMap.clear();
    }

    /**
     * Returns the <CODE>PDFObj</CODE> associated to the specified
     * <VAR>key</VAR>.
     *
     * @param key a <CODE>PDFName</CODE>
     * @return the </CODE>PDFObj</CODE> previously associated to the
     *   <VAR>key</VAR>
     */
    public PDFObj get(final PDFName key) {
        return hashMap.get(key);
    }

    public PDFObj getDirectObject(final PDFName key) {
        return PDFReader.getPdfObject(get(key));
    }

    /**
     * Get all keys that are set.
     *
     */
    public Set<PDFName> getKeys() {
        return hashMap.keySet();
    }

    /**
     * Returns the number of <VAR>key</VAR>-<VAR>value</VAR> mappings in this
     * <CODE>PDFDictionary</CODE>.
     *
     * @return the number of <VAR>key</VAR>-<VAR>value</VAR> mappings in this
     *   <CODE>PDFDictionary</CODE>.
     */
    public int size() {
        return hashMap.size();
    }

    /**
     * Returns <CODE>true</CODE> if this <CODE>PDFDictionary</CODE> contains a
     * mapping for the specified <VAR>key</VAR>.
     *
     * @return <CODE>true</CODE> if the key is set, otherwise <CODE>false</CODE>.
     */
    public boolean contains(final PDFName key) {
        return hashMap.containsKey(key);
    }

    // DICTIONARY TYPE METHODS

    /**
     * Checks if a <CODE>Dictionary</CODE> is of the type FONT.
     *
     * @return <CODE>true</CODE> if it is, otherwise <CODE>false</CODE>.
     */
    public boolean isFont() {
        return checkType(FONT);
    }

    /**
     * Checks if a <CODE>Dictionary</CODE> is of the type PAGE.
     *
     * @return <CODE>true</CODE> if it is, otherwise <CODE>false</CODE>.
     */
    public boolean isPage() {
        return checkType(PAGE);
    }

    /**
     * Checks if a <CODE>Dictionary</CODE> is of the type PAGES.
     *
     * @return <CODE>true</CODE> if it is, otherwise <CODE>false</CODE>.
     */
    public boolean isPages() {
        return checkType(PAGES);
    }

    /**
     * Checks if a <CODE>Dictionary</CODE> is of the type CATALOG.
     *
     * @return <CODE>true</CODE> if it is, otherwise <CODE>false</CODE>.
     */
    public boolean isCatalog() {
        return checkType(CATALOG);
    }

    /**
     * Checks if a <CODE>Dictionary</CODE> is of the type OUTLINES.
     *
     * @return <CODE>true</CODE> if it is, otherwise <CODE>false</CODE>.
     */
    public boolean isOutlineTree() {
        return checkType(OUTLINES);
    }
    
    /**
     * Checks the type of the dictionary.
     * @param type the type you're looking for
     * @return true if the type of the dictionary corresponds with the type you're looking for
     */
    public boolean checkType(PDFName type) {
    	if (type == null)
    		return false;
    	if (dictionaryType == null)
    		dictionaryType = getAsName(PDFName.TYPE);
    	return type.equals(dictionaryType);
    }

    // OTHER METHODS

    public void merge(final PDFDictionary other) {
        hashMap.putAll(other.hashMap);
    }

    public void mergeDifferent(final PDFDictionary other) {
        for (PDFName key : other.hashMap.keySet()) {
            if (!hashMap.containsKey(key))
                hashMap.put(key, other.hashMap.get(key));
        }
    }

     // DOWNCASTING GETTERS
     // @author Mark A Storer (2/17/06)

    /**
     * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFDictionary</CODE>,
     * resolving indirect references.
     *
     * The object associated with the <CODE>PDFName</CODE> given is retrieved
     * and resolved to a direct object.
     * If it is a <CODE>PDFDictionary</CODE>, it is cast down and returned as
     * such. Otherwise <CODE>null</CODE> is returned.
     *
     * @param key A <CODE>PDFName</CODE>
     * @return the associated <CODE>PDFDictionary</CODE> object,
     *   or <CODE>null</CODE>
     */
    public PDFDictionary getAsDict(final PDFName key) {
        PDFDictionary dict = null;
        PDFObj orig = getDirectObject(key);
        if (orig != null && orig.isDictionary())
            dict = (PDFDictionary) orig;
        return dict;
    }

    /**
     * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFArray</CODE>,
     * resolving indirect references.
     *
     * The object associated with the <CODE>PDFName</CODE> given is retrieved
     * and resolved to a direct object.
     * If it is a <CODE>PDFArray</CODE>, it is cast down and returned as such.
     * Otherwise <CODE>null</CODE> is returned.
     *
     * @param key A <CODE>PDFName</CODE>
     * @return the associated <CODE>PDFArray</CODE> object,
     *   or <CODE>null</CODE>
     */
    public PDFArray getAsArray(final PDFName key) {
        PDFArray array = null;
        PDFObj orig = getDirectObject(key);
        if (orig != null && orig.isArray())
            array = (PDFArray) orig;
        return array;
    }

    /**
     * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFStream</CODE>,
     * resolving indirect references.
     *
     * The object associated with the <CODE>PDFName</CODE> given is retrieved
     * and resolved to a direct object.
     * If it is a <CODE>PDFStream</CODE>, it is cast down and returned as such.
     * Otherwise <CODE>null</CODE> is returned.
     *
     * @param key A <CODE>PDFName</CODE>
     * @return the associated <CODE>PDFStream</CODE> object,
     *   or <CODE>null</CODE>
     */
    public PDFStream getAsStream(final PDFName key) {
        PDFStream stream = null;
        PDFObj orig = getDirectObject(key);
        if (orig != null && orig.isStream())
            stream = (PDFStream) orig;
        return stream;
    }

    /**
     * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFString</CODE>,
     * resolving indirect references.
     *
     * The object associated with the <CODE>PDFName</CODE> given is retrieved
     * and resolved to a direct object.
     * If it is a <CODE>PDFString</CODE>, it is cast down and returned as such.
     * Otherwise <CODE>null</CODE> is returned.
     *
     * @param key A <CODE>PDFName</CODE>
     * @return the associated <CODE>PDFString</CODE> object,
     *   or <CODE>null</CODE>
     */
    public PDFString getAsString(final PDFName key) {
        PDFString string = null;
        PDFObj orig = getDirectObject(key);
        if (orig != null && orig.isString())
            string = (PDFString) orig;
        return string;
    }

    /**
     * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFNumeric</CODE>,
     * resolving indirect references.
     *
     * The object associated with the <CODE>PDFName</CODE> given is retrieved
     * and resolved to a direct object.
     * If it is a <CODE>PDFNumeric</CODE>, it is cast down and returned as such.
     * Otherwise <CODE>null</CODE> is returned.
     *
     * @param key A <CODE>PDFName</CODE>
     * @return the associated <CODE>PDFNumeric</CODE> object,
     *   or <CODE>null</CODE>
     */
    public PDFNumeric getAsNumber(final PDFName key) {
        PDFNumeric number = null;
        PDFObj orig = getDirectObject(key);
        if (orig != null && orig.isNumber())
            number = (PDFNumeric) orig;
        return number;
    }

    /**
     * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFName</CODE>,
     * resolving indirect references.
     *
     * The object associated with the <CODE>PDFName</CODE> given is retrieved
     * and resolved to a direct object.
     * If it is a <CODE>PDFName</CODE>, it is cast down and returned as such.
     * Otherwise <CODE>null</CODE> is returned.
     *
     * @param key A <CODE>PDFName</CODE>
     * @return the associated <CODE>PDFName</CODE> object,
     *   or <CODE>null</CODE>
     */
    public PDFName getAsName(final PDFName key) {
        PDFName name = null;
        PDFObj orig = getDirectObject(key);
        if (orig != null && orig.isName())
            name = (PDFName) orig;
        return name;
    }

    /**
     * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFBoolean</CODE>,
     * resolving indirect references.
     *
     * The object associated with the <CODE>PDFName</CODE> given is retrieved
     * and resolved to a direct object.
     * If it is a <CODE>PDFBoolean</CODE>, it is cast down and returned as such.
     * Otherwise <CODE>null</CODE> is returned.
     *
     * @param key A <CODE>PDFName</CODE>
     * @return the associated <CODE>PDFBoolean</CODE> object,
     *   or <CODE>null</CODE>
     */
    public PDFBoolean getAsBoolean(final PDFName key) {
        PDFBoolean bool = null;
        PDFObj orig = getDirectObject(key);
        if (orig != null && orig.isBoolean())
            bool = (PDFBoolean)orig;
        return bool;
    }

    /**
     * Returns a <CODE>PDFObj</CODE> as a <CODE>PDFIndirectReference</CODE>.
     *
     * The object associated with the <CODE>PDFName</CODE> given is retrieved
     * If it is a <CODE>PDFIndirectReference</CODE>, it is cast down and returned
     * as such. Otherwise <CODE>null</CODE> is returned.
     *
     * @param key A <CODE>PDFName</CODE>
     * @return the associated <CODE>PDFIndirectReference</CODE> object,
     *   or <CODE>null</CODE>
     */
    public PDFIndirectReference getAsIndirectObject(final PDFName key) {
        PDFIndirectReference ref = null;
        PDFObj orig = get(key); // not getDirect this time.
        if (orig != null && orig.isIndirect())
            ref = (PDFIndirectReference) orig;
        return ref;
    }
    
    @Override
    public void write(final OutputStream os) throws IOException {
        os.write('<');
        os.write('<');
        // loop over all the object-pairs in the HashMap
        PDFObj value;
        int type = 0;
        for (Entry<PDFName, PDFObj> e : hashMap.entrySet()) {
        	e.getKey().write(os);
        	value = e.getValue();
			type = value.type();
        	if (type != PDFObj.ARRAY 
        			&& type != PDFObj.DICTIONARY 
        			&& type != PDFObj.NAME 
        			&& type != PDFObj.STRING) {
                os.write(' ');
        	}
            value.write(os);
        }
        os.write('>');
        os.write('>');
    }
}
