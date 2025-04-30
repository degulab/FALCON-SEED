/*
 * @(#)DtJsonIOHelper.java	0.1.0	2022/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.io;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import dtalge.util.Strings;

/**
 * JSON 形式での入出力に用いるヘルパー。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
final public class DtJsonIOHelper
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static public final String JSON_NULL = "null";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static protected final char[] HEX_DIGIT = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	static protected final int[] JSON_ESCAPE_CHARS = new int[128];
	static {
		// ASCII: 0 - 31
		for (int i = 0; i < 32; ++i) {
			JSON_ESCAPE_CHARS[i] = -1;
		}
		JSON_ESCAPE_CHARS['\b'] = 'b';
		JSON_ESCAPE_CHARS['\t'] = 't';
		JSON_ESCAPE_CHARS['\n'] = 'n';
		JSON_ESCAPE_CHARS['\f'] = 'f';
		JSON_ESCAPE_CHARS['\r'] = 'r';
		JSON_ESCAPE_CHARS['"'] = '"';
		JSON_ESCAPE_CHARS['\\'] = '\\';
		JSON_ESCAPE_CHARS[0x7F] = -1;	// [DEL]
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private DtJsonIOHelper() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 文字セット名から文字セットオブジェクトを取得する。
	 * 文字セット名が <code>null</code> もしくは空文字の場合は、<code>UTF-8</code> が指定されたものとみなす。
	 * @param csName	文字セット名
	 * @return	文字セット名に対応する文字セットオブジェクト
	 * @throws UnsupportedEncodingException	指定された文字セット名がサポートされていない場合
	 */
	static public Charset ensureEncoding(String csName) throws UnsupportedEncodingException
	{
		if (Strings.isNullOrEmpty(csName)) {
			return StandardCharsets.UTF_8;
		}
		else {
			try {
				return Charset.forName(csName);
			}
			catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {
				throw new UnsupportedEncodingException("Unsupported character-set name for encoding : " + csName);
			}
		}
	}
	
	static public void appendEncodedString(Appendable abuf, String value) throws IOException
	{
		// null
		if (value == null) {
			abuf.append(JSON_NULL);
			return;
		}
		
		// string
		//--- start quote
		abuf.append('"');
		//--- chars
		final int strlen = value.length();
		int spos = 0;
		for (int i = 0; i < strlen; i++) {
			int c = value.charAt(i);
			if (c < JSON_ESCAPE_CHARS.length) {
				// ASCII
				int token = JSON_ESCAPE_CHARS[c];
				if (token == 0) {
					// printable ASCII character
				}
				else if (token > 0) {
					// escape character
					if (spos < i) abuf.append(value, spos, i);	// exclude value[i]
					abuf.append('\\');
					abuf.append((char)token);
					spos = i + 1;	// set next position to spos
				}
				else if (token == -1) {
					if (spos < i) abuf.append(value, spos, i);	// exclude value[i]
					//--- escape
					abuf.append("\\u00");
					abuf.append(HEX_DIGIT[c / 16]);
					abuf.append(HEX_DIGIT[c % 16]);
					spos = i + 1;	// set next position to spos
				}
			}
			else if (c == '\u2028') {
				// LINE SEPARATOR 対応
				if (spos < i) abuf.append(value, spos, i);	// exclude value[i]
				abuf.append("\\u2028");	// escape
				spos = i + 1;	// set next position to spos
			}
			else if (c == '\u2029') {
				// PARAGRAPH SEPARATOR 対応
				if (spos < i) abuf.append(value, spos, i);	// exclude value[i]
				abuf.append("\\u2029");	// escape
				spos = i + 1;	// set next position to spos
			}
		}
		if (spos < strlen) abuf.append(value, spos, strlen);	// append remains
		//--- end quote
		abuf.append('"');
	}
	
	static public String encodeString(String value) {
		StringBuilder strbuf = new StringBuilder();
		try {
			appendEncodedString(strbuf, value);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return strbuf.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
