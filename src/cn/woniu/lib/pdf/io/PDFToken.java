/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.io;

import java.io.IOException;
import java.io.RandomAccessFile;

import cn.woniu.lib.pdf.util.PDFConstant;
import cn.woniu.lib.pdf.util.StringUtils;

/** 
 * @ClassName: PDFToken <br/> 
 * @Description: TODO  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月13日 上午11:01:01 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFToken {

	//	private final PDFInputFile file;

	private final RandomAccessFile file;

	/** PDF 头部起始位置 (%PDF- 的偏移量) */
	private long headerOffset;
	private String version;

	protected TokenType type;
	protected String stringValue;
	protected int reference;
	protected int generation;
	protected boolean hexString;

    private final StringBuilder outBuf = new StringBuilder();

	public PDFToken(RandomAccessFile file, long headerOffset) throws IOException {
		if (file == null) {
			throw new IOException("RandomAccessFile is null");
		}
		
		this.file = file;
		this.headerOffset = headerOffset <= 0 ? 0 : headerOffset;
	}

	public long getStartxref() throws IOException {
		int arrLength = 1024;
		long fileLength = file.length();
		long pos = fileLength - arrLength;
		if (pos < 1) pos = 1;
		byte[] buff = new byte[arrLength];
		while (pos > 0) {
			file.seek(pos);
			int len = this.file.read(buff);
			int index = StringUtils.search(buff, 0, len, PDFConstant.PDF_STARTXREF.getBytes());
			if (index >= 0) {
				return pos + index;
			}
			pos = pos - arrLength + PDFConstant.PDF_STARTXREF.length();
		}
		throw new IOException("Not Found startxref");
	}

	public boolean nextToken() throws IOException {
        int ch = 0;
        do {
            ch = file.read();
        } while (ch != -1 && isWhitespace(ch));
        if (ch == -1){
            type = TokenType.ENDOFFILE;
            return false;
        }

        // Note:  We have to initialize stringValue here, after we've looked for the end of the stream,
        // to ensure that we don't lose the value of a token that might end exactly at the end
        // of the stream
        outBuf.setLength(0);
        stringValue = EMPTY;

        switch (ch) {
            case '[':
                type = TokenType.START_ARRAY;
                break;
            case ']':
                type = TokenType.END_ARRAY;
                break;
            case '/':
            {
                outBuf.setLength(0);
                type = TokenType.NAME;
                while (true) {
                    ch = file.read();
                    if (delims[ch + 1])
                        break;
                    if (ch == '#') {
                        ch = (getHex(file.read()) << 4) + getHex(file.read());
                    }
                    outBuf.append((char)ch);
                }
                backOnePosition(ch);
                break;
            }
            case '>':
                ch = file.read();
                if (ch != '>')
                    throw new IOException("greaterthan.not.expected");
                type = TokenType.END_DIC;
                break;
            case '<':
            {
                int v1 = file.read();
                if (v1 == '<') {
                    type = TokenType.START_DIC;
                    break;
                }
                outBuf.setLength(0);
                type = TokenType.STRING;
                hexString = true;
                int v2 = 0;
                while (true) {
                    while (isWhitespace(v1))
                        v1 = file.read();
                    if (v1 == '>')
                        break;
                    v1 = getHex(v1);
                    if (v1 < 0)
                        break;
                    v2 = file.read();
                    while (isWhitespace(v2))
                        v2 = file.read();
                    if (v2 == '>') {
                        ch = v1 << 4;
                        outBuf.append((char)ch);
                        break;
                    }
                    v2 = getHex(v2);
                    if (v2 < 0)
                        break;
                    ch = (v1 << 4) + v2;
                    outBuf.append((char)ch);
                    v1 = file.read();
                }
                if (v1 < 0 || v2 < 0)
                    throw new IOException("error.reading.string");
                break;
            }
            case '%':
                type = TokenType.COMMENT;
                do {
                    ch = file.read();
                } while (ch != -1 && ch != '\r' && ch != '\n');
                break;
            case '(':
            {
                outBuf.setLength(0);
                type = TokenType.STRING;
                hexString = false;
                int nesting = 0;
                while (true) {
                    ch = file.read();
                    if (ch == -1)
                        break;
                    if (ch == '(') {
                        ++nesting;
                    }
                    else if (ch == ')') {
                        --nesting;
                    }
                    else if (ch == '\\') {
                        boolean lineBreak = false;
                        ch = file.read();
                        switch (ch) {
                            case 'n':
                                ch = '\n';
                                break;
                            case 'r':
                                ch = '\r';
                                break;
                            case 't':
                                ch = '\t';
                                break;
                            case 'b':
                                ch = '\b';
                                break;
                            case 'f':
                                ch = '\f';
                                break;
                            case '(':
                            case ')':
                            case '\\':
                                break;
                            case '\r':
                                lineBreak = true;
                                ch = file.read();
                                if (ch != '\n')
                                    backOnePosition(ch);
                                break;
                            case '\n':
                                lineBreak = true;
                                break;
                            default:
                            {
                                if (ch < '0' || ch > '7') {
                                    break;
                                }
                                int octal = ch - '0';
                                ch = file.read();
                                if (ch < '0' || ch > '7') {
                                    backOnePosition(ch);
                                    ch = octal;
                                    break;
                                }
                                octal = (octal << 3) + ch - '0';
                                ch = file.read();
                                if (ch < '0' || ch > '7') {
                                    backOnePosition(ch);
                                    ch = octal;
                                    break;
                                }
                                octal = (octal << 3) + ch - '0';
                                ch = octal & 0xff;
                                break;
                            }
                        }
                        if (lineBreak)
                            continue;
                        if (ch < 0)
                            break;
                    }
                    else if (ch == '\r') {
                        ch = file.read();
                        if (ch < 0)
                            break;
                        if (ch != '\n') {
                            backOnePosition(ch);
                            ch = '\n';
                        }
                    }
                    if (nesting == -1)
                        break;
                    outBuf.append((char)ch);
                }
                if (ch == -1)
                    throw new IOException("error.reading.string");
                break;
            }
            default:
            {
                outBuf.setLength(0);
                if (ch == '-' || ch == '+' || ch == '.' || (ch >= '0' && ch <= '9')) {
                    type = TokenType.NUMBER;
                    boolean isReal = false;
                    int numberOfMinuses = 0;
                    if (ch == '-') {
                        // Take care of number like "--234". If Acrobat can read them so must we.
                        do {
                            ++numberOfMinuses;
                            ch = file.read();
                        } while (ch == '-');
                        outBuf.append('-');
                    }
                    else {
                        outBuf.append((char)ch);
                        // We don't need to check if the number is real over here
                        // as we need to know that fact only in case if there are any minuses.
                        ch = file.read();
                    }
                    while (ch != -1 && ((ch >= '0' && ch <= '9') || ch == '.')) {
                        if (ch == '.')
                            isReal = true;
                        outBuf.append((char)ch);
                        ch = file.read();
                    }
                    if (numberOfMinuses > 1 && !isReal) {
                        // Numbers of integer type and with more than one minus before them
                        // are interpreted by Acrobat as zero.
                        outBuf.setLength(0);
                        outBuf.append('0');
                    }
                }
                else {
                    type = TokenType.OTHER;
                    do {
                        outBuf.append((char)ch);
                        ch = file.read();
                    } while (!delims[ch + 1]);
                }
                if(ch != -1)
                	backOnePosition(ch);
                break;
            }
        }
        if (outBuf != null)
            stringValue = outBuf.toString();
        return true;
    }
	
	public void nextValidToken() throws IOException {
        int level = 0;
        String n1 = null;
        String n2 = null;
        long ptr = 0;
        while (nextToken()) {
            if (type == TokenType.COMMENT)
                continue;
            switch (level) {
                case 0:
                {
                    if (type != TokenType.NUMBER)
                        return;
                    ptr = file.getFilePointer();
                    n1 = stringValue;
                    ++level;
                    break;
                }
                case 1:
                {
                    if (type != TokenType.NUMBER) {
                        file.seek(ptr);
                        type = TokenType.NUMBER;
                        stringValue = n1;
                        return;
                    }
                    n2 = stringValue;
                    ++level;
                    break;
                }
                default:
                {
                    if (type != TokenType.OTHER || !stringValue.equals("R")) {
                        file.seek(ptr);
                        type = TokenType.NUMBER;
                        stringValue = n1;
                        return;
                    }
                    type = TokenType.REF;
                    reference = Integer.parseInt(n1);
                    generation = Integer.parseInt(n2);
                    return;
                }
            }
        }
        
        if (level == 1){ // if the level 1 check returns EOF, then we are still looking at a number - set the type back to NUMBER
        	type = TokenType.NUMBER;
        }
        // if we hit here, the file is either corrupt (stream ended unexpectedly),
        // or the last token ended exactly at the end of a stream.  This last
        // case can occur inside an Object Stream.
    }
	
	public void backOnePosition(int ch) throws IOException {
        if (ch != -1) {
        	long currIndex = file.getFilePointer();
        	if (currIndex > 0) {
            	file.seek(currIndex-1);
        	}
        }
    }


    /**
     * Reads data into the provided byte[]. Checks on leading whitespace.
     * See {@link #isWhitespace(int) isWhiteSpace(int)} or {@link #isWhitespace(int, boolean) isWhiteSpace(int, boolean)}
     * for a list of whitespace characters.
     * <br />The same as calling {@link #readLineSegment(byte[], boolean) readLineSegment(input, true)}.
     *
     * @param input byte[]
     * @return boolean
     * @throws IOException
     * @since 5.5.1
     */
    public boolean readLineSegment(byte[] input) throws IOException {
        return readLineSegment(input, true);
    }

    /**
     * Reads data into the provided byte[]. Checks on leading whitespace.
     * See {@link #isWhitespace(int) isWhiteSpace(int)} or {@link #isWhitespace(int, boolean) isWhiteSpace(int, boolean)}
     * for a list of whitespace characters.
     *
     * @param input byte[]
     * @param isNullWhitespace boolean to indicate whether '0' is whitespace or not.
     *                         If in doubt, use true or overloaded method {@link #readLineSegment(byte[]) readLineSegment(input)}
     * @return boolean
     * @throws IOException
     * @since 5.5.1
     */
    public boolean readLineSegment(byte input[], boolean isNullWhitespace) throws IOException {
        int c = -1;
        boolean eol = false;
        int ptr = 0;
        int len = input.length;
        // ssteward, pdftk-1.10, 040922:
        // skip initial whitespace; added this because PdfReader.rebuildXref()
        // assumes that line provided by readLineSegment does not have init. whitespace;
        if ( ptr < len ) {
            while ( isWhitespace( (c = read()), isNullWhitespace ) );
        }
        while ( !eol && ptr < len ) {
            switch (c) {
                case -1:
                case '\n':
                    eol = true;
                    break;
                case '\r':
                    eol = true;
                    long cur = getFilePointer();
                    if ((read()) != '\n') {
                        seek(cur);
                    }
                    break;
                default:
                    input[ptr++] = (byte)c;
                    break;
            }

            // break loop? do it before we read() again
            if ( eol || len <= ptr ) {
                break;
            } else {
                c = read();
            }
        }
        if (ptr >= len) {
            eol = false;
            while (!eol) {
                switch (c = read()) {
                    case -1:
                    case '\n':
                        eol = true;
                        break;
                    case '\r':
                        eol = true;
                        long cur = getFilePointer();
                        if ((read()) != '\n') {
                            seek(cur);
                        }
                        break;
                }
            }
        }
        
        if ((c == -1) && (ptr == 0)) {
            return false;
        }
        if (ptr + 2 <= len) {
            input[ptr++] = (byte)' ';
            input[ptr] = (byte)'X';
        }
        return true;
    }
	
    public TokenType getTokenType() {
        return this.type;
    }
    
    public String getStringValue() {
        return this.stringValue;
    }
    
    public int getReference() {
        return this.reference;
    }
    
    public int getGeneration() {
        return this.generation;
    }
    
    public long longValue() {
        return Long.parseLong(this.stringValue);
    }
    
    public int intValue() {
        return Integer.parseInt(this.stringValue);
    }
    
    public boolean isHexString() {
        return this.hexString;
    }

	public String readString(int size) throws IOException {
		StringBuilder buf = new StringBuilder();
		int ch;
		while ((size--) > 0) {
			ch = read();
			if (ch == -1)
				break;
			buf.append((char)ch);
		}
		return buf.toString();
	}

	public void seek(long pos) throws IOException {
		file.seek(pos);
	}
	
	public void seekOffset(long pos) throws IOException {
		file.seek(pos + this.headerOffset);
	}

	public long getFilePointer() throws IOException {
		return file.getFilePointer();
	}

	public long length() throws IOException {
		return file.length();
	}

	public int read() throws IOException {
		return file.read();
	}

	public void close() throws IOException {
		file.close();
	}



	public enum TokenType {
		NUMBER,
		STRING,
		NAME,
		COMMENT,
		START_ARRAY,
		END_ARRAY,
		START_DIC,
		END_DIC,
		REF,
		OTHER,
		ENDOFFILE
	}

	public static final boolean delims[] = {
			true,  true,  false, false, false, false, false, false, false, false,
			true,  true,  false, true,  true,  false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, true,  false, false, false, false, true,  false,
			false, true,  true,  false, false, false, false, false, true,  false,
			false, false, false, false, false, false, false, false, false, false,
			false, true,  false, true,  false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, true,  false, true,  false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false, false, false};

	static final String EMPTY = "";

	/**
	 * Is a certain character a whitespace? Currently checks on the following: '0', '9', '10', '12', '13', '32'.
	 * <br />The same as calling {@link #isWhitespace(int, boolean) isWhiteSpace(ch, true)}.
	 * @param ch int (char)
	 * @return boolean
	 */
	public static final boolean isWhitespace(int ch) {
		return isWhitespace(ch, true);
	}

	/**
	 * Checks whether a character is a whitespace. Currently checks on the following: '0', '9', '10', '12', '13', '32'.
	 * @param ch int
	 * @param isWhitespace boolean
	 * @return boolean
	 * @since 5.5.1
	 */
	public static final boolean isWhitespace(int ch, boolean isWhitespace) {
		return ( (isWhitespace && (ch == 0)) || ch == 9 || ch == 10 || ch == 12 || ch == 13 || ch == 32);
	}

	public static final boolean isDelimiter(int ch) {
		return (ch == '(' || ch == ')' || ch == '<' || ch == '>' || ch == '[' || ch == ']' || ch == '/' || ch == '%');
	}

	public static final boolean isDelimiterWhitespace(int ch) {
		return delims[ch + 1];
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
}
