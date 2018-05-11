/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import cn.woniu.lib.pdf.encode.ByteBuffer;

/** 
 * <CODE>PdfName</CODE> is an object that can be used as a name in a PDF-file.
 * <P>
 * A name, like a string, is a sequence of characters.
 * It must begin with a slash followed by a sequence of ASCII characters in
 * the range 32 through 136 except %, (, ), [, ], &lt;, &gt;, {, }, / and #.
 * Any character except 0x00 may be included in a name by writing its
 * two character hex code, preceded by #. The maximum number of characters
 * in a name is 127.<BR>
 * This object is described in the 'Portable Document Format Reference Manual
 * version 1.7' section 3.2.4 (page 56-58).
 * <BR> 
 *  <B>Example</B><BR>
 *    <B>/ASomeName123    ===  ASomeName123</B><BR>   
 *    <B>/.notepdf        ===  .notepdf</B><BR>
 *    <B>/1.2   	      ===  1.2</B><BR>
 *    <B>/Adobe#20Green   ===  Adobe Green</B><BR>
 */
public class PDFName extends PDFObj {

	private static final long serialVersionUID = -3702398806056324953L;
	
    private int hash = 0;
	
	public PDFName(String name) {
        this(name, true);
    }
	
	public PDFName(String name, boolean lengthCheck) {
        super(PDFObj.NAME);
        // The minimum number of characters in a name is 0, the maximum is 127 (the '/' not included)
        int length = name.length();
        if (lengthCheck && length > 127)
            throw new IllegalArgumentException("Name" + name + " is too long:"+ String.valueOf(length));
        bytes = encodeName(name);
    }
	
	public PDFName(byte bytes[]) {
        super(PDFObj.NAME, bytes);
    }

    /**
     * Compares this object with the specified object for order.
     * Returns a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.<p>
     *
     * @param name the PdfName to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     * from being compared to this Object.
     */
    public int compareTo(PDFName name) {
        byte myBytes[] = bytes;
        byte objBytes[] = name.bytes;
        int len = Math.min(myBytes.length, objBytes.length);
        for(int i = 0; i < len; i++) {
            if (myBytes[i] > objBytes[i])
                return 1;
            if (myBytes[i] < objBytes[i])
                return -1;
        }
        if (myBytes.length < objBytes.length)
            return -1;
        if (myBytes.length > objBytes.length)
            return 1;
        return 0;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param   obj   the reference object with which to compare.
     * @return  <code>true</code> if this object is the same as the obj
     * argument; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof PDFName)
            return compareTo((PDFName)obj) == 0;
        return false;
    }

    /**
     * Returns a hash code value for the object.
     * This method is supported for the benefit of hashtables such as those provided by
     * <code>java.util.Hashtable</code>.
     *
     * @return  a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            int ptr = 0;
            int len = bytes.length;
            for (int i = 0; i < len; i++)
                h = 31*h + (bytes[ptr++] & 0xff);
            hash = h;
        }
        return h;
    }

    /**
     * Encodes a plain name given in the unescaped form "AB CD" into "/AB#20CD".
     *
     * @param name the name to encode
     * @return the encoded name
     */
    public static byte[] encodeName(String name) {
    	int length = name.length();
    	ByteBuffer buf = new ByteBuffer(length + 20);
    	buf.append('/');
    	char c;
    	char chars[] = name.toCharArray();
    	for (int k = 0; k < length; k++) {
    		c = (char)(chars[k] & 0xff);
    		// Escape special characters
    		switch (c) {
    			case ' ':
    			case '%':
    			case '(':
    			case ')':
    			case '<':
    			case '>':
    			case '[':
    			case ']':
    			case '{':
    			case '}':
    			case '/':
    			case '#':
    				buf.append('#');
    				buf.append(Integer.toString(c, 16));
    				break;
    			default:
    				if (c >= 32 && c <= 126)
    					buf.append(c);
    				else {
    					buf.append('#');
    					if (c < 16)
    						buf.append('0');
    					buf.append(Integer.toString(c, 16));
    				}
    				break;
    			}
    		}
    	return buf.toByteArray();
    }

    /**
     * Decodes an escaped name given in the form "/AB#20CD" into "AB CD".
     *
     * @param name the name to decode
     * @return the decoded name
     */
    public static String decodeName(String name) {
        StringBuffer buf = new StringBuffer();
        try {
            int len = name.length();
            for (int k = 1; k < len; ++k) {
                char c = name.charAt(k);
                if (c == '#') {
                	char c1 = name.charAt(k + 1);
                	char c2 = name.charAt(k + 2);
                    c = (char)((getHex(c1) << 4) + getHex(c2));
                    k += 2;
                }
                buf.append(c);
            }
        }
        catch (IndexOutOfBoundsException e) {
            // empty on purpose
        }
        return buf.toString();
    }
    
    public static int getHex(int v) {
        if (v >= '0' && v <= '9')
            return v - '0';
        if (v >= 'A' && v <= 'F')
            return v - 'A' + 10;
        if (v >= 'a' && v <= 'f')
            return v - 'a' + 10;
        return -1;
    }
	

    public static final PDFName _3D = new PDFName("3D");
    public static final PDFName A = new PDFName("A");
    public static final PDFName A85 = new PDFName("A85");
    public static final PDFName AA = new PDFName("AA");
    public static final PDFName ABSOLUTECOLORIMETRIC = new PDFName("AbsoluteColorimetric");
    
    public static final PDFName AC = new PDFName("AC");
    
    public static final PDFName ACROFORM = new PDFName("AcroForm");
    
    public static final PDFName ACTION = new PDFName("Action");
    public static final PDFName ACTIVATION = new PDFName("Activation");
    public static final PDFName ADBE = new PDFName("ADBE");
    public static final PDFName ACTUALTEXT = new PDFName("ActualText");
    
    public static final PDFName ADBE_PKCS7_DETACHED = new PDFName("adbe.pkcs7.detached");
    
    public static final PDFName ADBE_PKCS7_S4 =new PDFName("adbe.pkcs7.s4");
    
    public static final PDFName ADBE_PKCS7_S5 =new PDFName("adbe.pkcs7.s5");
    
    public static final PDFName ADBE_PKCS7_SHA1 = new PDFName("adbe.pkcs7.sha1");
    
    public static final PDFName ADBE_X509_RSA_SHA1 = new PDFName("adbe.x509.rsa_sha1");
    
    public static final PDFName ADOBE_PPKLITE = new PDFName("Adobe.PPKLite");
    
    public static final PDFName ADOBE_PPKMS = new PDFName("Adobe.PPKMS");
    
    public static final PDFName AESV2 = new PDFName("AESV2");
    
    public static final PDFName AESV3 = new PDFName("AESV3");
    public static final PDFName AF = new PDFName("AF");
    public static final PDFName AFRELATIONSHIP = new PDFName("AFRelationship");
    public static final PDFName AHX = new PDFName("AHx");
    
    public static final PDFName AIS = new PDFName("AIS");
    
    public static final PDFName ALL = new PDFName("All");
    
    public static final PDFName ALLPAGES = new PDFName("AllPages");
    public static final PDFName ALT = new PDFName("Alt");
    public static final PDFName ALTERNATE = new PDFName("Alternate");
    public static final PDFName ALTERNATEPRESENTATION = new PDFName("AlternatePresentations");
    public static final PDFName ALTERNATES = new PDFName("Alternates");
    public static final PDFName AND = new PDFName("And");
    public static final PDFName ANIMATION = new PDFName("Animation");
    
    public static final PDFName ANNOT = new PDFName("Annot");
    
    public static final PDFName ANNOTS = new PDFName("Annots");
    
    public static final PDFName ANTIALIAS = new PDFName("AntiAlias");
    
    public static final PDFName AP = new PDFName("AP");
    
    public static final PDFName APP = new PDFName("App");
    
    public static final PDFName APPDEFAULT = new PDFName("AppDefault");
    public static final PDFName ART = new PDFName("Art");
    
    public static final PDFName ARTBOX = new PDFName("ArtBox");
    public static final PDFName ARTIFACT = new PDFName("Artifact");
    
    public static final PDFName ASCENT = new PDFName("Ascent");
    
    public static final PDFName AS = new PDFName("AS");
    
    public static final PDFName ASCII85DECODE = new PDFName("ASCII85Decode");
    
    public static final PDFName ASCIIHEXDECODE = new PDFName("ASCIIHexDecode");
    public static final PDFName ASSET = new PDFName("Asset");
    public static final PDFName ASSETS = new PDFName("Assets");
    public static final PDFName ATTACHED = new PDFName("Attached");
    
    public static final PDFName AUTHEVENT = new PDFName("AuthEvent");
    
    public static final PDFName AUTHOR = new PDFName("Author");
    
    public static final PDFName B = new PDFName("B");
    public static final PDFName BACKGROUND = new PDFName("Background");
    public static final PDFName BACKGROUNDCOLOR = new PDFName("BackgroundColor");
    
    public static final PDFName BASEENCODING = new PDFName("BaseEncoding");
    
    public static final PDFName BASEFONT = new PDFName("BaseFont");
    public static final PDFName BASEVERSION = new PDFName("BaseVersion");
    
    public static final PDFName BBOX = new PDFName("BBox");
    
    public static final PDFName BC = new PDFName("BC");
    
    public static final PDFName BG = new PDFName("BG");
    public static final PDFName BIBENTRY = new PDFName("BibEntry");
    
    public static final PDFName BIGFIVE = new PDFName("BigFive");
    public static final PDFName BINDING = new PDFName("Binding");
    public static final PDFName BINDINGMATERIALNAME = new PDFName("BindingMaterialName");
    
    public static final PDFName BITSPERCOMPONENT = new PDFName("BitsPerComponent");
    
    public static final PDFName BITSPERSAMPLE = new PDFName("BitsPerSample");
    
    public static final PDFName BL = new PDFName("Bl");
    
    public static final PDFName BLACKIS1 = new PDFName("BlackIs1");
    
    public static final PDFName BLACKPOINT = new PDFName("BlackPoint");
    public static final PDFName BLOCKQUOTE = new PDFName("BlockQuote");
    
    public static final PDFName BLEEDBOX = new PDFName("BleedBox");
    
    public static final PDFName BLINDS = new PDFName("Blinds");
    
    public static final PDFName BM = new PDFName("BM");
    
    public static final PDFName BORDER = new PDFName("Border");
    public static final PDFName BOTH = new PDFName("Both");
    
    public static final PDFName BOUNDS = new PDFName("Bounds");
    
    public static final PDFName BOX = new PDFName("Box");
    
    public static final PDFName BS = new PDFName("BS");
    
    public static final PDFName BTN = new PDFName("Btn");
    
    public static final PDFName BYTERANGE = new PDFName("ByteRange");
    
    public static final PDFName C = new PDFName("C");
    
    public static final PDFName C0 = new PDFName("C0");
    
    public static final PDFName C1 = new PDFName("C1");
    
    public static final PDFName CA = new PDFName("CA");
    
    public static final PDFName ca = new PDFName("ca");
    
    public static final PDFName CALGRAY = new PDFName("CalGray");
    
    public static final PDFName CALRGB = new PDFName("CalRGB");
    
    public static final PDFName CAPHEIGHT = new PDFName("CapHeight");
    public static final PDFName CARET = new PDFName("Caret");
    public static final PDFName CAPTION = new PDFName("Caption");
    
    public static final PDFName CATALOG = new PDFName("Catalog");
    
    public static final PDFName CATEGORY = new PDFName("Category");
    public static final PDFName CB = new PDFName("cb");
    
    public static final PDFName CCITTFAXDECODE = new PDFName("CCITTFaxDecode");
    public static final PDFName CENTER = new PDFName("Center");
    
    public static final PDFName CENTERWINDOW = new PDFName("CenterWindow");
    
    public static final PDFName CERT = new PDFName("Cert");
    public static final PDFName CERTS = new PDFName("Certs");
    
    public static final PDFName CF = new PDFName("CF");
    
    public static final PDFName CFM = new PDFName("CFM");
    
    public static final PDFName CH = new PDFName("Ch");
    
    public static final PDFName CHARPROCS = new PDFName("CharProcs");
    
    public static final PDFName CHECKSUM = new PDFName("CheckSum");
    
    public static final PDFName CI = new PDFName("CI");
    
    public static final PDFName CIDFONTTYPE0 = new PDFName("CIDFontType0");
    
    public static final PDFName CIDFONTTYPE2 = new PDFName("CIDFontType2");
    public static final PDFName CIDSET = new PDFName("CIDSet");
    
    public static final PDFName CIDSYSTEMINFO = new PDFName("CIDSystemInfo");
    
    public static final PDFName CIDTOGIDMAP = new PDFName("CIDToGIDMap");
    
    public static final PDFName CIRCLE = new PDFName("Circle");
    public static final PDFName CLASSMAP = new PDFName("ClassMap");
    public static final PDFName CLOUD = new PDFName("Cloud");
    public static final PDFName CMD = new PDFName("CMD");
    
    public static final PDFName CO = new PDFName("CO");
    public static final PDFName CODE = new PDFName("Code");
    public static final PDFName COLOR = new PDFName("Color");
    public static final PDFName COLORANTS = new PDFName("Colorants");
    
    public static final PDFName COLORS = new PDFName("Colors");
    
    public static final PDFName COLORSPACE = new PDFName("ColorSpace");
    public static final PDFName COLORTRANSFORM = new PDFName("ColorTransform");
    
    public static final PDFName COLLECTION = new PDFName("Collection");
    
    public static final PDFName COLLECTIONFIELD = new PDFName("CollectionField");
    
    public static final PDFName COLLECTIONITEM = new PDFName("CollectionItem");
    
    public static final PDFName COLLECTIONSCHEMA = new PDFName("CollectionSchema");
    
    public static final PDFName COLLECTIONSORT = new PDFName("CollectionSort");
    
    public static final PDFName COLLECTIONSUBITEM = new PDFName("CollectionSubitem");
    public static final PDFName COLSPAN = new PDFName("ColSpan");
    public static final PDFName COLUMN = new PDFName("Column");
    
    public static final PDFName COLUMNS = new PDFName("Columns");
    public static final PDFName CONDITION = new PDFName("Condition");
    public static final PDFName CONFIGS = new PDFName("Configs");
    public static final PDFName CONFIGURATION = new PDFName("Configuration");
    public static final PDFName CONFIGURATIONS = new PDFName("Configurations");
    
    public static final PDFName CONTACTINFO = new PDFName("ContactInfo");
    
    public static final PDFName CONTENT = new PDFName("Content");
    
    public static final PDFName CONTENTS = new PDFName("Contents");
    
    public static final PDFName COORDS = new PDFName("Coords");
    
    public static final PDFName COUNT = new PDFName("Count");
    public static final PDFName COURIER = new PDFName("Courier");
    public static final PDFName COURIER_BOLD = new PDFName("Courier-Bold");
    public static final PDFName COURIER_OBLIQUE = new PDFName("Courier-Oblique");
    public static final PDFName COURIER_BOLDOBLIQUE = new PDFName("Courier-BoldOblique");
    
    public static final PDFName CREATIONDATE = new PDFName("CreationDate");
    
    public static final PDFName CREATOR = new PDFName("Creator");
    
    public static final PDFName CREATORINFO = new PDFName("CreatorInfo");
    public static final PDFName CRL = new PDFName("CRL");
    public static final PDFName CRLS = new PDFName("CRLs");
    
    public static final PDFName CROPBOX = new PDFName("CropBox");
    
    public static final PDFName CRYPT = new PDFName("Crypt");
    
    public static final PDFName CS = new PDFName("CS");
    public static final PDFName CUEPOINT = new PDFName("CuePoint");
    public static final PDFName CUEPOINTS = new PDFName("CuePoints");
    public static final PDFName CYX = new PDFName("CYX");
    
    public static final PDFName D = new PDFName("D");
    
    public static final PDFName DA = new PDFName("DA");
    
    public static final PDFName DATA = new PDFName("Data");
    
    public static final PDFName DC = new PDFName("DC");
    public static final PDFName DCS = new PDFName("DCS");
    
    public static final PDFName DCTDECODE = new PDFName("DCTDecode");
    public static final PDFName DECIMAL = new PDFName("Decimal");
    public static final PDFName DEACTIVATION = new PDFName("Deactivation");
    
    public static final PDFName DECODE = new PDFName("Decode");
    
    public static final PDFName DECODEPARMS = new PDFName("DecodeParms");
    public static final PDFName DEFAULT = new PDFName("Default");
    public static final PDFName DEFAULTCRYPTFILTER = new PDFName("DefaultCryptFilter");
    
    public static final PDFName DEFAULTCMYK = new PDFName("DefaultCMYK");
    
    public static final PDFName DEFAULTGRAY = new PDFName("DefaultGray");
    
    public static final PDFName DEFAULTRGB = new PDFName("DefaultRGB");
    
    public static final PDFName DESC = new PDFName("Desc");
    
    public static final PDFName DESCENDANTFONTS = new PDFName("DescendantFonts");
    
    public static final PDFName DESCENT = new PDFName("Descent");
    
    public static final PDFName DEST = new PDFName("Dest");
    
    public static final PDFName DESTOUTPUTPROFILE = new PDFName("DestOutputProfile");
    
    public static final PDFName DESTS = new PDFName("Dests");
    
    public static final PDFName DEVICEGRAY = new PDFName("DeviceGray");
    
    public static final PDFName DEVICERGB = new PDFName("DeviceRGB");
    
    public static final PDFName DEVICECMYK = new PDFName("DeviceCMYK");
    public static final PDFName DEVICEN = new PDFName("DeviceN");
    
    public static final PDFName DI = new PDFName("Di");
    
    public static final PDFName DIFFERENCES = new PDFName("Differences");
    
    public static final PDFName DISSOLVE = new PDFName("Dissolve");
    
    public static final PDFName DIRECTION = new PDFName("Direction");
    
    public static final PDFName DISPLAYDOCTITLE = new PDFName("DisplayDocTitle");
    
    public static final PDFName DIV = new PDFName("Div");
    
    public static final PDFName DL = new PDFName("DL");
    
    public static final PDFName DM = new PDFName("Dm");
    
    public static final PDFName DOCMDP = new PDFName("DocMDP");
    
    public static final PDFName DOCOPEN = new PDFName("DocOpen");
    public static final PDFName DOCTIMESTAMP = new PDFName( "DocTimeStamp" );
    public static final PDFName DOCUMENT = new PDFName( "Document" );
    
    public static final PDFName DOMAIN = new PDFName("Domain");
    public static final PDFName DOS = new PDFName("DOS");
    
    public static final PDFName DP = new PDFName("DP");
    
    public static final PDFName DR = new PDFName("DR");
    
    public static final PDFName DS = new PDFName("DS");
    public static final PDFName DSS = new PDFName("DSS");
    
    public static final PDFName DUR = new PDFName("Dur");
    
    public static final PDFName DUPLEX = new PDFName("Duplex");
    
    public static final PDFName DUPLEXFLIPSHORTEDGE = new PDFName("DuplexFlipShortEdge");
    
    public static final PDFName DUPLEXFLIPLONGEDGE = new PDFName("DuplexFlipLongEdge");
    
    public static final PDFName DV = new PDFName("DV");
    
    public static final PDFName DW = new PDFName("DW");
    
    public static final PDFName E = new PDFName("E");
    
    public static final PDFName EARLYCHANGE = new PDFName("EarlyChange");
    
    public static final PDFName EF = new PDFName("EF");
    public static final PDFName EFF = new PDFName("EFF");
    public static final PDFName EFOPEN = new PDFName("EFOpen");
    public static final PDFName EMBEDDED = new PDFName("Embedded");
    
    public static final PDFName EMBEDDEDFILE = new PDFName("EmbeddedFile");
    
    public static final PDFName EMBEDDEDFILES = new PDFName("EmbeddedFiles");
    
    public static final PDFName ENCODE = new PDFName("Encode");
    
    public static final PDFName ENCODEDBYTEALIGN = new PDFName("EncodedByteAlign");
    
    public static final PDFName ENCODING = new PDFName("Encoding");
    
    public static final PDFName ENCRYPT = new PDFName("Encrypt");
    
    public static final PDFName ENCRYPTMETADATA = new PDFName("EncryptMetadata");
    public static final PDFName END = new PDFName("End");
    public static final PDFName ENDINDENT = new PDFName("EndIndent");
    
    public static final PDFName ENDOFBLOCK = new PDFName("EndOfBlock");
    
    public static final PDFName ENDOFLINE = new PDFName("EndOfLine");
    public static final PDFName EPSG = new PDFName("EPSG");
    public static final PDFName ESIC = new PDFName("ESIC");
    public static final PDFName ETSI_CADES_DETACHED = new PDFName("ETSI.CAdES.detached");
    
    public static final PDFName ETSI_RFC3161 = new PDFName("ETSI.RFC3161");
    
    public static final PDFName EXCLUDE = new PDFName("Exclude");
    
    public static final PDFName EXTEND = new PDFName("Extend");
    public static final PDFName EXTENSIONS = new PDFName("Extensions");
    public static final PDFName EXTENSIONLEVEL = new PDFName("ExtensionLevel");
    
    public static final PDFName EXTGSTATE = new PDFName("ExtGState");
    
    public static final PDFName EXPORT = new PDFName("Export");
    
    public static final PDFName EXPORTSTATE = new PDFName("ExportState");
    
    public static final PDFName EVENT = new PDFName("Event");
    
    public static final PDFName F = new PDFName("F");
    public static final PDFName FAR = new PDFName("Far");
    
    public static final PDFName FB = new PDFName("FB");
    public static final PDFName FD = new PDFName("FD");
    
    public static final PDFName FDECODEPARMS = new PDFName("FDecodeParms");
    
    public static final PDFName FDF = new PDFName("FDF");
    
    public static final PDFName FF = new PDFName("Ff");
    
    public static final PDFName FFILTER = new PDFName("FFilter");
    public static final PDFName FG = new PDFName("FG");
    
    public static final PDFName FIELDMDP = new PDFName("FieldMDP");
    
    public static final PDFName FIELDS = new PDFName("Fields");
    public static final PDFName FIGURE = new PDFName( "Figure" );
    
    public static final PDFName FILEATTACHMENT = new PDFName("FileAttachment");
    
    public static final PDFName FILESPEC = new PDFName("Filespec");
    
    public static final PDFName FILTER = new PDFName("Filter");
    
    public static final PDFName FIRST = new PDFName("First");
    
    public static final PDFName FIRSTCHAR = new PDFName("FirstChar");
    
    public static final PDFName FIRSTPAGE = new PDFName("FirstPage");
    
    public static final PDFName FIT = new PDFName("Fit");
    
    public static final PDFName FITH = new PDFName("FitH");
    
    public static final PDFName FITV = new PDFName("FitV");
    
    public static final PDFName FITR = new PDFName("FitR");
    
    public static final PDFName FITB = new PDFName("FitB");
    
    public static final PDFName FITBH = new PDFName("FitBH");
    
    public static final PDFName FITBV = new PDFName("FitBV");
    
    public static final PDFName FITWINDOW = new PDFName("FitWindow");
    public static final PDFName FL = new PDFName("Fl");
    
    public static final PDFName FLAGS = new PDFName("Flags");
    public static final PDFName FLASH = new PDFName("Flash");
    public static final PDFName FLASHVARS = new PDFName("FlashVars");
    
    public static final PDFName FLATEDECODE = new PDFName("FlateDecode");
    
    public static final PDFName FO = new PDFName("Fo");
    
    public static final PDFName FONT = new PDFName("Font");
    
    public static final PDFName FONTBBOX = new PDFName("FontBBox");
    
    public static final PDFName FONTDESCRIPTOR = new PDFName("FontDescriptor");
    
    public static final PDFName FONTFAMILY = new PDFName("FontFamily");
    
    public static final PDFName FONTFILE = new PDFName("FontFile");
    
    public static final PDFName FONTFILE2 = new PDFName("FontFile2");
    
    public static final PDFName FONTFILE3 = new PDFName("FontFile3");
    
    public static final PDFName FONTMATRIX = new PDFName("FontMatrix");
    
    public static final PDFName FONTNAME = new PDFName("FontName");
    
    public static final PDFName FONTWEIGHT = new PDFName("FontWeight");
    public static final PDFName FOREGROUND = new PDFName("Foreground");
    
    public static final PDFName FORM = new PDFName("Form");
    
    public static final PDFName FORMTYPE = new PDFName("FormType");
    public static final PDFName FORMULA = new PDFName( "Formula" );
    
    public static final PDFName FREETEXT = new PDFName("FreeText");
    
    public static final PDFName FRM = new PDFName("FRM");
    
    public static final PDFName FS = new PDFName("FS");
    
    public static final PDFName FT = new PDFName("FT");
    
    public static final PDFName FULLSCREEN = new PDFName("FullScreen");
    
    public static final PDFName FUNCTION = new PDFName("Function");
    
    public static final PDFName FUNCTIONS = new PDFName("Functions");
    
    public static final PDFName FUNCTIONTYPE = new PDFName("FunctionType");
    public static final PDFName GAMMA = new PDFName("Gamma");
    public static final PDFName GBK = new PDFName("GBK");
    public static final PDFName GCS = new PDFName("GCS");
    public static final PDFName GEO = new PDFName("GEO");
    public static final PDFName GEOGCS = new PDFName("GEOGCS");
    public static final PDFName GLITTER = new PDFName("Glitter");
    public static final PDFName GOTO = new PDFName("GoTo");
    public static final PDFName GOTO3DVIEW= new PDFName("GoTo3DView");
    public static final PDFName GOTOE = new PDFName("GoToE");
    public static final PDFName GOTOR = new PDFName("GoToR");
    public static final PDFName GPTS = new PDFName("GPTS");
    public static final PDFName GROUP = new PDFName("Group");
    public static final PDFName GTS_PDFA1 = new PDFName("GTS_PDFA1");
    public static final PDFName GTS_PDFX = new PDFName("GTS_PDFX");
    public static final PDFName GTS_PDFXVERSION = new PDFName("GTS_PDFXVersion");
    public static final PDFName H = new PDFName("H");
    public static final PDFName H1 = new PDFName( "H1" );
    public static final PDFName H2 = new PDFName("H2");
    public static final PDFName H3 = new PDFName("H3");
    public static final PDFName H4 = new PDFName("H4");
    public static final PDFName H5 = new PDFName("H5");
    public static final PDFName H6 = new PDFName("H6");
    public static final PDFName HALFTONENAME = new PDFName("HalftoneName");
    public static final PDFName HALFTONETYPE = new PDFName("HalftoneType");
    public static final PDFName HALIGN = new PDFName("HAlign");
    public static final PDFName HEADERS = new PDFName("Headers");
    public static final PDFName HEIGHT = new PDFName("Height");
    
    public static final PDFName HELV = new PDFName("Helv");
    public static final PDFName HELVETICA = new PDFName("Helvetica");
    public static final PDFName HELVETICA_BOLD = new PDFName("Helvetica-Bold");
    public static final PDFName HELVETICA_OBLIQUE = new PDFName("Helvetica-Oblique");
    public static final PDFName HELVETICA_BOLDOBLIQUE = new PDFName("Helvetica-BoldOblique");
    public static final PDFName HF = new PDFName("HF");
    
    public static final PDFName HID = new PDFName("Hid");
    
    public static final PDFName HIDE = new PDFName("Hide");
    
    public static final PDFName HIDEMENUBAR = new PDFName("HideMenubar");
    
    public static final PDFName HIDETOOLBAR = new PDFName("HideToolbar");
    
    public static final PDFName HIDEWINDOWUI = new PDFName("HideWindowUI");
    
    public static final PDFName HIGHLIGHT = new PDFName("Highlight");
    public static final PDFName HOFFSET = new PDFName("HOffset");
    public static final PDFName HT = new PDFName("HT");
    public static final PDFName HTP = new PDFName("HTP");
    
    public static final PDFName I = new PDFName("I");
    public static final PDFName IC = new PDFName("IC");
    
    public static final PDFName ICCBASED = new PDFName("ICCBased");
    
    public static final PDFName ID = new PDFName("ID");
    
    public static final PDFName IDENTITY = new PDFName("Identity");
    
    public static final PDFName IDTREE = new PDFName("IDTree");
    
    public static final PDFName IF = new PDFName("IF");
    public static final PDFName IM = new PDFName("IM");
    
    public static final PDFName IMAGE = new PDFName("Image");
    
    public static final PDFName IMAGEB = new PDFName("ImageB");
    
    public static final PDFName IMAGEC = new PDFName("ImageC");
    
    public static final PDFName IMAGEI = new PDFName("ImageI");
    
    public static final PDFName IMAGEMASK = new PDFName("ImageMask");
    
    public static final PDFName INCLUDE = new PDFName("Include");
    public static final PDFName IND = new PDFName("Ind");
    
    public static final PDFName INDEX = new PDFName("Index");
    
    public static final PDFName INDEXED = new PDFName("Indexed");
    
    public static final PDFName INFO = new PDFName("Info");
    
    public static final PDFName INK = new PDFName("Ink");
    
    public static final PDFName INKLIST = new PDFName("InkList");
    public static final PDFName INSTANCES = new PDFName("Instances");
    
    public static final PDFName IMPORTDATA = new PDFName("ImportData");
    
    public static final PDFName INTENT = new PDFName("Intent");
    
    public static final PDFName INTERPOLATE = new PDFName("Interpolate");
    
    public static final PDFName ISMAP = new PDFName("IsMap");
    
    public static final PDFName IRT = new PDFName("IRT");
    
    public static final PDFName ITALICANGLE = new PDFName("ItalicAngle");
    public static final PDFName ITXT = new PDFName("ITXT");
    
    public static final PDFName IX = new PDFName("IX");
    
    public static final PDFName JAVASCRIPT = new PDFName("JavaScript");
    public static final PDFName JBIG2DECODE = new PDFName("JBIG2Decode");
    public static final PDFName JBIG2GLOBALS = new PDFName("JBIG2Globals");
    
    public static final PDFName JPXDECODE = new PDFName("JPXDecode");
    
    public static final PDFName JS = new PDFName("JS");
    public static final PDFName JUSTIFY = new PDFName("Justify");
    
    public static final PDFName K = new PDFName("K");
    
    public static final PDFName KEYWORDS = new PDFName("Keywords");
    
    public static final PDFName KIDS = new PDFName("Kids");
    
    public static final PDFName L = new PDFName("L");
    
    public static final PDFName L2R = new PDFName("L2R");
    public static final PDFName LAB = new PDFName("Lab");
    /**
     * An entry specifying the natural language, and optionally locale. Use this
     * to specify the Language attribute on a Tagged Pdf element.
     * For the content usage dictionary, use {@link #LANGUAGE}
     */
    public static final PDFName LANG = new PDFName("Lang");
    /**
     * A dictionary type, strictly for use in the content usage dictionary. For
     * dictionary entries in Tagged Pdf, use {@link #LANG}
     */
    public static final PDFName LANGUAGE = new PDFName("Language");
    
    public static final PDFName LAST = new PDFName("Last");
    
    public static final PDFName LASTCHAR = new PDFName("LastChar");
    
    public static final PDFName LASTPAGE = new PDFName("LastPage");
    
    public static final PDFName LAUNCH = new PDFName("Launch");
    public static final PDFName LAYOUT = new PDFName("Layout");
    public static final PDFName LBL = new PDFName("Lbl");
    public static final PDFName LBODY = new PDFName("LBody");
    
    public static final PDFName LENGTH = new PDFName("Length");
    
    public static final PDFName LENGTH1 = new PDFName("Length1");
    public static final PDFName LI = new PDFName("LI");
    
    public static final PDFName LIMITS = new PDFName("Limits");
    
    public static final PDFName LINE = new PDFName("Line");
    public static final PDFName LINEAR = new PDFName("Linear");
    public static final PDFName LINEHEIGHT = new PDFName("LineHeight");
    
    public static final PDFName LINK = new PDFName("Link");
    public static final PDFName LIST = new PDFName("List");
    
    public static final PDFName LISTMODE = new PDFName("ListMode");
    
    public static final PDFName LISTNUMBERING = new PDFName("ListNumbering");
    
    public static final PDFName LOCATION = new PDFName("Location");
    
    public static final PDFName LOCK = new PDFName("Lock");
    public static final PDFName LOCKED = new PDFName("Locked");
    public static final PDFName LOWERALPHA = new PDFName("LowerAlpha");
    public static final PDFName LOWERROMAN = new PDFName("LowerRoman");
    public static final PDFName LPTS = new PDFName("LPTS");
    
    public static final PDFName LZWDECODE = new PDFName("LZWDecode");
    
    public static final PDFName M = new PDFName("M");
    public static final PDFName MAC = new PDFName("Mac");
    public static final PDFName MATERIAL = new PDFName("Material");
    
    public static final PDFName MATRIX = new PDFName("Matrix");
    public static final PDFName MAC_EXPERT_ENCODING = new PDFName("MacExpertEncoding");
    public static final PDFName MAC_ROMAN_ENCODING = new PDFName("MacRomanEncoding");
    
    public static final PDFName MARKED = new PDFName("Marked");
    
    public static final PDFName MARKINFO = new PDFName("MarkInfo");
    
    public static final PDFName MASK = new PDFName("Mask");
    public static final PDFName MAX_LOWER_CASE = new PDFName("max");
    public static final PDFName MAX_CAMEL_CASE = new PDFName("Max");
    
    public static final PDFName MAXLEN = new PDFName("MaxLen");
    
    public static final PDFName MEDIABOX = new PDFName("MediaBox");
    
    public static final PDFName MCID = new PDFName("MCID");
    
    public static final PDFName MCR = new PDFName("MCR");
    public static final PDFName MEASURE = new PDFName("Measure");
    
    public static final PDFName METADATA = new PDFName("Metadata");
    public static final PDFName MIN_LOWER_CASE = new PDFName("min");
    public static final PDFName MIN_CAMEL_CASE = new PDFName("Min");
    
    public static final PDFName MK = new PDFName("MK");
    
    public static final PDFName MMTYPE1 = new PDFName("MMType1");
    
    public static final PDFName MODDATE = new PDFName("ModDate");
    public static final PDFName MOVIE = new PDFName("Movie");
    
    public static final PDFName N = new PDFName("N");
    
    public static final PDFName N0 = new PDFName("n0");
    
    public static final PDFName N1 = new PDFName("n1");
    
    public static final PDFName N2 = new PDFName("n2");
    
    public static final PDFName N3 = new PDFName("n3");
    
    public static final PDFName N4 = new PDFName("n4");
    
    public static final PDFName NAME = new PDFName("Name");
    
    public static final PDFName NAMED = new PDFName("Named");
    
    public static final PDFName NAMES = new PDFName("Names");
    public static final PDFName NAVIGATION = new PDFName("Navigation");
    public static final PDFName NAVIGATIONPANE = new PDFName("NavigationPane");
    public static final PDFName NCHANNEL = new PDFName("NChannel");
    public static final PDFName NEAR = new PDFName("Near");
    
    public static final PDFName NEEDAPPEARANCES = new PDFName("NeedAppearances");
    public static final PDFName NEEDRENDERING= new PDFName("NeedsRendering");
    
    public static final PDFName NEWWINDOW = new PDFName("NewWindow");
    
    public static final PDFName NEXT = new PDFName("Next");
    
    public static final PDFName NEXTPAGE = new PDFName("NextPage");
    
    public static final PDFName NM = new PDFName("NM");
    
    public static final PDFName NONE = new PDFName("None");
    
    public static final PDFName NONFULLSCREENPAGEMODE = new PDFName("NonFullScreenPageMode");
    public static final PDFName NONSTRUCT = new PDFName("NonStruct");
    public static final PDFName NOT = new PDFName("Not");
    public static final PDFName NOTE = new PDFName("Note");
    public static final PDFName NUMBERFORMAT = new PDFName("NumberFormat");
    
    public static final PDFName NUMCOPIES = new PDFName("NumCopies");
    
    public static final PDFName NUMS = new PDFName("Nums");
    
    public static final PDFName O = new PDFName("O");
    public static final PDFName OBJ = new PDFName("Obj");
    public static final PDFName OBJR = new PDFName("OBJR");
    
    public static final PDFName OBJSTM = new PDFName("ObjStm");
    
    public static final PDFName OC = new PDFName("OC");
    
    public static final PDFName OCG = new PDFName("OCG");
    
    public static final PDFName OCGS = new PDFName("OCGs");
    
    public static final PDFName OCMD = new PDFName("OCMD");
    
    public static final PDFName OCPROPERTIES = new PDFName("OCProperties");
    public static final PDFName OCSP = new PDFName("OCSP");
    public static final PDFName OCSPS = new PDFName("OCSPs");
    
    public static final PDFName OE = new PDFName("OE");
    
    public static final PDFName Off = new PDFName("Off");
    
    public static final PDFName OFF = new PDFName("OFF");
    
    public static final PDFName ON = new PDFName("ON");
    
    public static final PDFName ONECOLUMN = new PDFName("OneColumn");
    
    public static final PDFName OPEN = new PDFName("Open");
    
    public static final PDFName OPENACTION = new PDFName("OpenAction");
    
    public static final PDFName OP = new PDFName("OP");
    
    public static final PDFName op = new PDFName("op");
    public static final PDFName OPI = new PDFName("OPI");
    
    public static final PDFName OPM = new PDFName("OPM");
    
    public static final PDFName OPT = new PDFName("Opt");
    public static final PDFName OR = new PDFName("Or");
    
    public static final PDFName ORDER = new PDFName("Order");
    
    public static final PDFName ORDERING = new PDFName("Ordering");
    public static final PDFName ORG = new PDFName("Org");
    public static final PDFName OSCILLATING = new PDFName("Oscillating");

    
    public static final PDFName OUTLINES = new PDFName("Outlines");
    
    public static final PDFName OUTPUTCONDITION = new PDFName("OutputCondition");
    
    public static final PDFName OUTPUTCONDITIONIDENTIFIER = new PDFName("OutputConditionIdentifier");
    
    public static final PDFName OUTPUTINTENT = new PDFName("OutputIntent");
    
    public static final PDFName OUTPUTINTENTS = new PDFName("OutputIntents");
    public static final PDFName OVERLAYTEXT = new PDFName("OverlayText");
    
    public static final PDFName P = new PDFName("P");
    
    public static final PDFName PAGE = new PDFName("Page");
    public static final PDFName PAGEELEMENT = new PDFName("PageElement");
    
    public static final PDFName PAGELABELS = new PDFName("PageLabels");
    
    public static final PDFName PAGELAYOUT = new PDFName("PageLayout");
    
    public static final PDFName PAGEMODE = new PDFName("PageMode");
    
    public static final PDFName PAGES = new PDFName("Pages");
    
    public static final PDFName PAINTTYPE = new PDFName("PaintType");
    
    public static final PDFName PANOSE = new PDFName("Panose");
    
    public static final PDFName PARAMS = new PDFName("Params");
    
    public static final PDFName PARENT = new PDFName("Parent");
    
    public static final PDFName PARENTTREE = new PDFName("ParentTree");
    public static final PDFName PARENTTREENEXTKEY = new PDFName( "ParentTreeNextKey" );
    public static final PDFName PART = new PDFName( "Part" );
    public static final PDFName PASSCONTEXTCLICK = new PDFName("PassContextClick");
    
    public static final PDFName PATTERN = new PDFName("Pattern");
    
    public static final PDFName PATTERNTYPE = new PDFName("PatternType");
    public static final PDFName PB = new PDFName("pb");
    public static final PDFName PC = new PDFName("PC");
    
    public static final PDFName PDF = new PDFName("PDF");
    
    public static final PDFName PDFDOCENCODING = new PDFName("PDFDocEncoding");
    public static final PDFName PDU = new PDFName("PDU");
    
    public static final PDFName PERCEPTUAL = new PDFName("Perceptual");
    
    public static final PDFName PERMS = new PDFName("Perms");
    
    public static final PDFName PG = new PDFName("Pg");
    public static final PDFName PI = new PDFName("PI");
    
    public static final PDFName PICKTRAYBYPDFSIZE = new PDFName("PickTrayByPDFSize");
    public static final PDFName PIECEINFO = new PDFName("PieceInfo");
    public static final PDFName PLAYCOUNT = new PDFName("PlayCount");
    public static final PDFName PO = new PDFName("PO");
    public static final PDFName POLYGON = new PDFName("Polygon");
    public static final PDFName POLYLINE = new PDFName("PolyLine");
    
    public static final PDFName POPUP = new PDFName("Popup");
    public static final PDFName POSITION = new PDFName("Position");
    
    public static final PDFName PREDICTOR = new PDFName("Predictor");
    
    public static final PDFName PREFERRED = new PDFName("Preferred");
    public static final PDFName PRESENTATION = new PDFName("Presentation");
    
    public static final PDFName PRESERVERB = new PDFName("PreserveRB");
    public static final PDFName PRESSTEPS = new PDFName("PresSteps");
    
    public static final PDFName PREV = new PDFName("Prev");
    
    public static final PDFName PREVPAGE = new PDFName("PrevPage");
    
    public static final PDFName PRINT = new PDFName("Print");
    
    public static final PDFName PRINTAREA = new PDFName("PrintArea");
    
    public static final PDFName PRINTCLIP = new PDFName("PrintClip");
    public static final PDFName PRINTERMARK = new PDFName("PrinterMark");
    public static final PDFName PRINTFIELD = new PDFName("PrintField");
    
    public static final PDFName PRINTPAGERANGE = new PDFName("PrintPageRange");
    
    public static final PDFName PRINTSCALING = new PDFName("PrintScaling");
    
    public static final PDFName PRINTSTATE = new PDFName("PrintState");
    public static final PDFName PRIVATE = new PDFName("Private");
    
    public static final PDFName PROCSET = new PDFName("ProcSet");
    
    public static final PDFName PRODUCER = new PDFName("Producer");
    public static final PDFName PROJCS = new PDFName("PROJCS");
    
    public static final PDFName PROP_BUILD = new PDFName("Prop_Build");
    
    public static final PDFName PROPERTIES = new PDFName("Properties");
    
    public static final PDFName PS = new PDFName("PS");
    public static final PDFName PTDATA = new PDFName("PtData");
    
    public static final PDFName PUBSEC = new PDFName("Adobe.PubSec");
    public static final PDFName PV = new PDFName("PV");
    
    public static final PDFName Q = new PDFName("Q");
    
    public static final PDFName QUADPOINTS = new PDFName("QuadPoints");
    public static final PDFName QUOTE = new PDFName("Quote");
    
    public static final PDFName R = new PDFName("R");
    
    public static final PDFName R2L = new PDFName("R2L");
    
    public static final PDFName RANGE = new PDFName("Range");
    public static final PDFName RB = new PDFName("RB");
    public static final PDFName rb = new PDFName("rb");
    
    public static final PDFName RBGROUPS = new PDFName("RBGroups");
    
    public static final PDFName RC = new PDFName("RC");
    public static final PDFName RD = new PDFName("RD");
    
    public static final PDFName REASON = new PDFName("Reason");
    
    public static final PDFName RECIPIENTS = new PDFName("Recipients");
    
    public static final PDFName RECT = new PDFName("Rect");
    public static final PDFName REDACT = new PDFName("Redact");
    
    public static final PDFName REFERENCE = new PDFName("Reference");
    
    public static final PDFName REGISTRY = new PDFName("Registry");
    
    public static final PDFName REGISTRYNAME = new PDFName("RegistryName");
    public static final PDFName RELATIVECOLORIMETRIC = new PDFName("RelativeColorimetric");
    
    public static final PDFName RENDITION = new PDFName("Rendition");
    public static final PDFName REPEAT = new PDFName("Repeat");
    
    public static final PDFName RESETFORM = new PDFName("ResetForm");
    
    public static final PDFName RESOURCES = new PDFName("Resources");
    public static final PDFName REQUIREMENTS = new PDFName("Requirements");
    public static final PDFName REVERSEDCHARS = new PDFName("ReversedChars");
    
    public static final PDFName RI = new PDFName("RI");
    public static final PDFName RICHMEDIA = new PDFName("RichMedia");
    public static final PDFName RICHMEDIAACTIVATION = new PDFName("RichMediaActivation");
    public static final PDFName RICHMEDIAANIMATION = new PDFName("RichMediaAnimation");
    public static final PDFName RICHMEDIACOMMAND = new PDFName("RichMediaCommand");
    public static final PDFName RICHMEDIACONFIGURATION = new PDFName("RichMediaConfiguration");
    public static final PDFName RICHMEDIACONTENT = new PDFName("RichMediaContent");
    public static final PDFName RICHMEDIADEACTIVATION = new PDFName("RichMediaDeactivation");
    public static final PDFName RICHMEDIAEXECUTE = new PDFName("RichMediaExecute");
    public static final PDFName RICHMEDIAINSTANCE = new PDFName("RichMediaInstance");
    public static final PDFName RICHMEDIAPARAMS = new PDFName("RichMediaParams");
    public static final PDFName RICHMEDIAPOSITION = new PDFName("RichMediaPosition");
    public static final PDFName RICHMEDIAPRESENTATION = new PDFName("RichMediaPresentation");
    public static final PDFName RICHMEDIASETTINGS = new PDFName("RichMediaSettings");
    public static final PDFName RICHMEDIAWINDOW = new PDFName("RichMediaWindow");
    public static final PDFName RL = new PDFName("RL");
    public static final PDFName ROLE = new PDFName("Role");
    public static final PDFName RO = new PDFName("RO");
    
    public static final PDFName ROLEMAP = new PDFName("RoleMap");
    
    public static final PDFName ROOT = new PDFName("Root");
    
    public static final PDFName ROTATE = new PDFName("Rotate");
    public static final PDFName ROW = new PDFName("Row");
    
    public static final PDFName ROWS = new PDFName("Rows");
    public static final PDFName ROWSPAN = new PDFName("RowSpan");
    public static final PDFName RP = new PDFName("RP");
    public static final PDFName RT = new PDFName("RT");
    public static final PDFName RUBY = new PDFName( "Ruby" );
    
    public static final PDFName RUNLENGTHDECODE = new PDFName("RunLengthDecode");
    
    public static final PDFName RV = new PDFName("RV");
    
    public static final PDFName S = new PDFName("S");
    
    public static final PDFName SATURATION = new PDFName("Saturation");
    
    public static final PDFName SCHEMA = new PDFName("Schema");
    public static final PDFName SCOPE = new PDFName("Scope");
    
    public static final PDFName SCREEN = new PDFName("Screen");
    public static final PDFName SCRIPTS = new PDFName("Scripts");
    
    public static final PDFName SECT = new PDFName("Sect");
    
    public static final PDFName SEPARATION = new PDFName("Separation");
    
    public static final PDFName SETOCGSTATE = new PDFName("SetOCGState");
    public static final PDFName SETTINGS = new PDFName("Settings");
    
    public static final PDFName SHADING = new PDFName("Shading");
    
    public static final PDFName SHADINGTYPE = new PDFName("ShadingType");
    
    public static final PDFName SHIFT_JIS = new PDFName("Shift-JIS");
    
    public static final PDFName SIG = new PDFName("Sig");
    
    public static final PDFName SIGFIELDLOCK = new PDFName("SigFieldLock");
    
    public static final PDFName SIGFLAGS = new PDFName("SigFlags");
    
    public static final PDFName SIGREF = new PDFName("SigRef");
    
    public static final PDFName SIMPLEX = new PDFName("Simplex");
    
    public static final PDFName SINGLEPAGE = new PDFName("SinglePage");
    
    public static final PDFName SIZE = new PDFName("Size");
    
    public static final PDFName SMASK = new PDFName("SMask");

    public static final PDFName SMASKINDATA = new PDFName("SMaskInData");
    
    public static final PDFName SORT = new PDFName("Sort");
    public static final PDFName SOUND = new PDFName("Sound");
    public static final PDFName SPACEAFTER = new PDFName("SpaceAfter");
    public static final PDFName SPACEBEFORE = new PDFName("SpaceBefore");
    
    public static final PDFName SPAN = new PDFName("Span");
    public static final PDFName SPEED = new PDFName("Speed");
    
    public static final PDFName SPLIT = new PDFName("Split");
    
    public static final PDFName SQUARE = new PDFName("Square");
    public static final PDFName SQUIGGLY = new PDFName("Squiggly");
    public static final PDFName SS = new PDFName("SS");
    
    public static final PDFName ST = new PDFName("St");
    
    public static final PDFName STAMP = new PDFName("Stamp");
    
    public static final PDFName STATUS = new PDFName("Status");
    
    public static final PDFName STANDARD = new PDFName("Standard");
    public static final PDFName START = new PDFName("Start");
    public static final PDFName STARTINDENT = new PDFName("StartIndent");
    
    public static final PDFName STATE = new PDFName("State");
    
    public static final PDFName STDCF = new PDFName("StdCF");
    
    public static final PDFName STEMV = new PDFName("StemV");
    
    public static final PDFName STMF = new PDFName("StmF");
    
    public static final PDFName STRF = new PDFName("StrF");
    
    public static final PDFName STRIKEOUT = new PDFName("StrikeOut");
    public static final PDFName STRUCTELEM = new PDFName("StructElem");
    
    public static final PDFName STRUCTPARENT = new PDFName("StructParent");
    
    public static final PDFName STRUCTPARENTS = new PDFName("StructParents");
    
    public static final PDFName STRUCTTREEROOT = new PDFName("StructTreeRoot");
    
    public static final PDFName STYLE = new PDFName("Style");
    
    public static final PDFName SUBFILTER = new PDFName("SubFilter");
    
    public static final PDFName SUBJECT = new PDFName("Subject");
    
    public static final PDFName SUBMITFORM = new PDFName("SubmitForm");
    
    public static final PDFName SUBTYPE = new PDFName("Subtype");
    public static final PDFName SUMMARY = new PDFName("Summary");
    
    public static final PDFName SUPPLEMENT = new PDFName("Supplement");
    
    public static final PDFName SV = new PDFName("SV");
    
    public static final PDFName SW = new PDFName("SW");
    public static final PDFName SYMBOL = new PDFName("Symbol");
    /**
     * T is very commonly used for various dictionary entries, including title
     * entries in a Tagged PDF element dictionary, and target dictionaries.
     */
    public static final PDFName T = new PDFName("T");
    public static final PDFName TA = new PDFName("TA");
    public static final PDFName TABLE = new PDFName("Table");
    public static final PDFName TABS = new PDFName("Tabs");
    public static final PDFName TBODY = new PDFName("TBody");
    public static final PDFName TD = new PDFName("TD");
    public static final PDFName TR = new PDFName("TR");
    public static final PDFName TR2 = new PDFName("TR2");
    
    public static final PDFName TEXT = new PDFName("Text");
    public static final PDFName TEXTALIGN = new PDFName("TextAlign");
    public static final PDFName TEXTDECORATIONCOLOR = new PDFName("TextDecorationColor");
    public static final PDFName TEXTDECORATIONTHICKNESS = new PDFName("TextDecorationThickness");
    public static final PDFName TEXTDECORATIONTYPE = new PDFName("TextDecorationType");
    public static final PDFName TEXTINDENT = new PDFName("TextIndent");
    public static final PDFName TFOOT = new PDFName("TFoot");
    public static final PDFName TH = new PDFName("TH");
    public static final PDFName THEAD = new PDFName("THead");
    
    public static final PDFName THUMB = new PDFName("Thumb");
    
    public static final PDFName THREADS = new PDFName("Threads");
    
    public static final PDFName TI = new PDFName("TI");
    public static final PDFName TIME = new PDFName("Time");
    
    public static final PDFName TILINGTYPE = new PDFName("TilingType");
    public static final PDFName TIMES_ROMAN = new PDFName("Times-Roman");
    public static final PDFName TIMES_BOLD = new PDFName("Times-Bold");
    public static final PDFName TIMES_ITALIC = new PDFName("Times-Italic");
    public static final PDFName TIMES_BOLDITALIC = new PDFName("Times-BoldItalic");
    public static final PDFName TITLE = new PDFName("Title");
    
    public static final PDFName TK = new PDFName("TK");
    
    public static final PDFName TM = new PDFName("TM");
    public static final PDFName TOC = new PDFName("TOC");
    public static final PDFName TOCI = new PDFName("TOCI");
    
    public static final PDFName TOGGLE = new PDFName("Toggle");
    public static final PDFName TOOLBAR = new PDFName("Toolbar");
    
    public static final PDFName TOUNICODE = new PDFName("ToUnicode");
    
    public static final PDFName TP = new PDFName("TP");
    public static final PDFName TABLEROW = new PDFName( "TR" );
    
    public static final PDFName TRANS = new PDFName("Trans");
    
    public static final PDFName TRANSFORMPARAMS = new PDFName("TransformParams");
    
    public static final PDFName TRANSFORMMETHOD = new PDFName("TransformMethod");
    
    public static final PDFName TRANSPARENCY = new PDFName("Transparency");
    public static final PDFName TRANSPARENT = new PDFName("Transparent");
    public static final PDFName TRAPNET = new PDFName("TrapNet");
    
    public static final PDFName TRAPPED = new PDFName("Trapped");
    
    public static final PDFName TRIMBOX = new PDFName("TrimBox");
    
    public static final PDFName TRUETYPE = new PDFName("TrueType");
    public static final PDFName TS = new PDFName("TS");
    public static final PDFName TTL = new PDFName("Ttl");
    
    public static final PDFName TU = new PDFName("TU");
    public static final PDFName TV = new PDFName("tv");
    
    public static final PDFName TWOCOLUMNLEFT = new PDFName("TwoColumnLeft");
    
    public static final PDFName TWOCOLUMNRIGHT = new PDFName("TwoColumnRight");
    
    public static final PDFName TWOPAGELEFT = new PDFName("TwoPageLeft");
    
    public static final PDFName TWOPAGERIGHT = new PDFName("TwoPageRight");
    
    public static final PDFName TX = new PDFName("Tx");
    
    public static final PDFName TYPE = new PDFName("Type");
    
    public static final PDFName TYPE0 = new PDFName("Type0");
    
    public static final PDFName TYPE1 = new PDFName("Type1");
    public static final PDFName TYPE3 = new PDFName("Type3");
    public static final PDFName U = new PDFName("U");
    
    public static final PDFName UE = new PDFName("UE");
    public static final PDFName UF = new PDFName("UF");
    public static final PDFName UHC = new PDFName("UHC");
    public static final PDFName UNDERLINE = new PDFName("Underline");
    public static final PDFName UNIX = new PDFName("Unix");
    public static final PDFName UPPERALPHA = new PDFName("UpperAlpha");
    public static final PDFName UPPERROMAN = new PDFName("UpperRoman");
    
    public static final PDFName UR = new PDFName("UR");
    
    public static final PDFName UR3 = new PDFName("UR3");
    
    public static final PDFName URI = new PDFName("URI");
    
    public static final PDFName URL = new PDFName("URL");
    
    public static final PDFName USAGE = new PDFName("Usage");
    
    public static final PDFName USEATTACHMENTS = new PDFName("UseAttachments");
    
    public static final PDFName USENONE = new PDFName("UseNone");
    
    public static final PDFName USEOC = new PDFName("UseOC");
    
    public static final PDFName USEOUTLINES = new PDFName("UseOutlines");
    
    public static final PDFName USER = new PDFName("User");
    
    public static final PDFName USERPROPERTIES = new PDFName("UserProperties");
    
    public static final PDFName USERUNIT = new PDFName("UserUnit");
    
    public static final PDFName USETHUMBS = new PDFName("UseThumbs");
    public static final PDFName UTF_8 = new PDFName("utf_8");
    
    public static final PDFName V = new PDFName("V");
    
    public static final PDFName V2 = new PDFName("V2");
    public static final PDFName VALIGN = new PDFName("VAlign");
    public static final PDFName VE = new PDFName("VE");
    
    public static final PDFName VERISIGN_PPKVS = new PDFName("VeriSign.PPKVS");
    
	public static final PDFName VERSION = new PDFName("Version");
    public static final PDFName VERTICES = new PDFName("Vertices");
    public static final PDFName VIDEO = new PDFName("Video");
    
    public static final PDFName VIEW = new PDFName("View");
    public static final PDFName VIEWS = new PDFName("Views");
    
    public static final PDFName VIEWAREA = new PDFName("ViewArea");
    
    public static final PDFName VIEWCLIP = new PDFName("ViewClip");
    
    public static final PDFName VIEWERPREFERENCES = new PDFName("ViewerPreferences");
    public static final PDFName VIEWPORT = new PDFName("Viewport");
    
    public static final PDFName VIEWSTATE = new PDFName("ViewState");
    
    public static final PDFName VISIBLEPAGES = new PDFName("VisiblePages");
    public static final PDFName VOFFSET = new PDFName("VOffset");
    public static final PDFName VP = new PDFName("VP");
    public static final PDFName VRI = new PDFName("VRI");
    public static final PDFName W = new PDFName("W");
    public static final PDFName W2 = new PDFName("W2");
    public static final PDFName WARICHU = new PDFName("Warichu");
    public static final PDFName WATERMARK = new PDFName("Watermark");
    public static final PDFName WC = new PDFName("WC");
    public static final PDFName WIDGET = new PDFName("Widget");
    public static final PDFName WIDTH = new PDFName("Width");
    
    public static final PDFName WIDTHS = new PDFName("Widths");
    public static final PDFName WIN = new PDFName("Win");
    public static final PDFName WIN_ANSI_ENCODING = new PDFName("WinAnsiEncoding");
    public static final PDFName WINDOW = new PDFName("Window");
    public static final PDFName WINDOWED = new PDFName("Windowed");
    public static final PDFName WIPE = new PDFName("Wipe");
    
    public static final PDFName WHITEPOINT = new PDFName("WhitePoint");
    public static final PDFName WKT = new PDFName("WKT");
    
    public static final PDFName WP = new PDFName("WP");
    public static final PDFName WS = new PDFName("WS");
    public static final PDFName WT = new PDFName("WT");
    
    public static final PDFName X = new PDFName("X");
     
    public static final PDFName XA = new PDFName("XA");
     
    public static final PDFName XD = new PDFName("XD");
    
    public static final PDFName XFA = new PDFName("XFA");
    
    public static final PDFName XML = new PDFName("XML");
    
    public static final PDFName XOBJECT = new PDFName("XObject");
    public static final PDFName XPTS = new PDFName("XPTS");
    
    public static final PDFName XREF = new PDFName("XRef");
    
    public static final PDFName XREFSTM = new PDFName("XRefStm");
    public static final PDFName XSTEP = new PDFName("XStep");
    public static final PDFName XYZ = new PDFName("XYZ");
    public static final PDFName YSTEP = new PDFName("YStep");
    public static final PDFName ZADB = new PDFName("ZaDb");
    public static final PDFName ZAPFDINGBATS = new PDFName("ZapfDingbats");
    public static final PDFName ZOOM = new PDFName("Zoom");
    


	public static Map<String, PDFName> staticNames;
	
	static {
        Field fields[] = PDFName.class.getDeclaredFields();
        staticNames = new HashMap<String, PDFName>( fields.length );
        final int flags = Modifier.STATIC | Modifier.PUBLIC | Modifier.FINAL;
        try {
            for (int fldIdx = 0; fldIdx < fields.length; ++fldIdx) {
                Field curFld = fields[fldIdx];
                if ((curFld.getModifiers() & flags) == flags &&
                    curFld.getType().equals( PDFName.class )) {
                	PDFName name = (PDFName)curFld.get( null );
                    staticNames.put( decodeName( name.toString() ), name );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
