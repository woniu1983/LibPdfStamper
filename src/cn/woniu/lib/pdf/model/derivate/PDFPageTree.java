/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model.derivate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cn.woniu.lib.pdf.PDFReader;
import cn.woniu.lib.pdf.encode.IntHashtable;
import cn.woniu.lib.pdf.model.PDFArray;
import cn.woniu.lib.pdf.model.PDFDictionary;
import cn.woniu.lib.pdf.model.PDFName;
import cn.woniu.lib.pdf.model.PDFNumeric;
import cn.woniu.lib.pdf.model.PDFObj;


/** 
 * @ClassName: PDFPage <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月13日 下午7:46:41 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFPageTree {

	protected static final PDFName pageInhCandidates[] = {
			PDFName.MEDIABOX, PDFName.ROTATE, PDFName.RESOURCES, PDFName.CROPBOX
	};

	private final PDFReader reader;

	/** 
	 * ArrayList with the indirect references to every page. 
	 * Element 0 = page 1; 1 = page 2;
	 */
	private ArrayList<PRIndirectReference> pageRefs;

	/**
	 *  intHashtable that does the same thing as refsn in case of partial reading:
	 *  major difference: not all the pages are read. */
	private IntHashtable refHashTable;

	/** Page number of the last page that was read (partial reading only) */
	private int lastPageRead = -1;

	/** stack to which pages dictionaries are pushed to keep track of the current page attributes */
	private ArrayList<PDFDictionary> pageDics;

	private boolean keepPages;
	/**
	 * Keeps track of all pages nodes to avoid circular references.
	 */
	private Set<PDFObj> pagesNodes = new HashSet<PDFObj>();

	public PDFPageTree(final PDFReader reader) throws IOException {
		this.reader = reader;
		readPages();
	}

	void readPages() throws IOException {
		if (pageRefs != null) {
			return;
		}
		refHashTable = null;
		pageRefs = new ArrayList<PRIndirectReference>();
		pageDics = new ArrayList<PDFDictionary>();
		iteratePages((PRIndirectReference)reader.catalog.get(PDFName.PAGES));
		pageDics = null;
		reader.rootPages.put(PDFName.COUNT, new PDFNumeric(pageRefs.size()));
	}

	private void iteratePages(final PRIndirectReference rpage) throws IOException {
		PDFDictionary page = (PDFDictionary)PDFReader.getPdfObject(rpage);
		if (page == null)
			return;
		if (!pagesNodes.add(PDFReader.getPdfObject(rpage)))
			throw new IOException("illegal pages tree");
		PDFArray kidsPR = page.getAsArray(PDFName.KIDS);
		// reference to a leaf
		if (kidsPR == null) {
			page.put(PDFName.TYPE, PDFName.PAGE);
			PDFDictionary dic = pageDics.get(pageDics.size() - 1);
			PDFName key;
			for (Object element : dic.getKeys()) {
				key = (PDFName) element;
				if (page.get(key) == null)
					page.put(key, dic.get(key));
			}
			if (page.get(PDFName.MEDIABOX) == null) {
				PDFArray arr = new PDFArray(new float[]{0,0,595,842});
				page.put(PDFName.MEDIABOX, arr);
			}
			pageRefs.add(rpage);
		}
		// reference to a branch
		else {
			page.put(PDFName.TYPE, PDFName.PAGES);
			pushPageAttributes(page);
			for (int k = 0; k < kidsPR.size(); ++k){
				PDFObj obj = kidsPR.getPDFObj(k);
				if (!obj.isIndirect()) {
					while (k < kidsPR.size())
						kidsPR.remove(k);
					break;
				}
				iteratePages((PRIndirectReference)obj);
			}
			popPageAttributes();
		}
	}


	/**
	 * Adds a PDFDictionary to the pageInh stack to keep track of the page attributes.
	 * @param nodePages	a Pages dictionary
	 */
	private void pushPageAttributes(final PDFDictionary nodePages) {
		PDFDictionary dic = new PDFDictionary();
		if (!pageDics.isEmpty()) {
			dic.putAll(pageDics.get(pageDics.size() - 1));
		}
		for (int k = 0; k < pageInhCandidates.length; ++k) {
			PDFObj obj = nodePages.get(pageInhCandidates[k]);
			if (obj != null)
				dic.put(pageInhCandidates[k], obj);
		}
		pageDics.add(dic);
	}

	/**
	 * Removes the last PdfDictionary that was pushed to the pageInh stack.
	 */
	private void popPageAttributes() {
		pageDics.remove(pageDics.size() - 1);
	}

	public PDFDictionary getPageDic(final int pageNum) {
		PRIndirectReference ref = getPageOrigRef(pageNum);
		return (PDFDictionary)PDFReader.getPdfObject(ref);
	}

	public PRIndirectReference getPageOrigRef(int pageNum){
		--pageNum;
		if (pageNum < 0 || pageNum >= size()) {
			return null;
		}
		if (this.pageRefs != null) {
			return this.pageRefs.get(pageNum);
		} else {
			return null; //TODO
		}
	}

	public int size() {
		if (this.pageRefs != null) {
			return this.pageRefs.size();
		}
		return 0;
	}

}
