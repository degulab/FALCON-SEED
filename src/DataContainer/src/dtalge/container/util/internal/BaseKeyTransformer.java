package dtalge.container.util.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * 基底の使用禁止文字変換ユーティリティ。
 * <p>
 * 
 * 交換代数として変換する際、交換代数基底キーとして使用できない文字が含まれている場合、次のように置換されます。
 * <ul>
 * 	<li>半角スペース  -&gt; __#sp#__</li>
 *  <li>水平タブ      -&gt; __#\t#__</li>
 *  <li>垂直タブ      -&gt; __#vt#__</li>
 *  <li>LF(改行文字)  -&gt; __#\n#__</li>
 *  <li>CR(改行文字)  -&gt; __#\r#__</li>
 *  <li>ページ区切り  -&gt; __#\f#__</li>
 *  <li>&lt;            -&gt; __#lt#__</li>
 *  <li>&gt;            -&gt; __#gt#__</li>
 *  <li>-            -&gt; __#hyp#__</li>
 *  <li>,            -&gt; __#com#__</li>
 *  <li>^            -&gt; __#hat#__</li>
 *  <li>%            -&gt; __#per#__</li>
 *  <li>&amp;            -&gt; __#amp#__</li>
 *  <li>?            -&gt; __#qm#__</li>
 *  <li>|            -&gt; __#or#__</li>
 *  <li>@            -&gt; __#at#__</li>
 *  <li>'            -&gt; __#apo#__</li>
 *  <li>"            -&gt; __#quo#__</li>
 * </ul>

 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class BaseKeyTransformer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static protected final char[] _ILLEGAL_BASEKEY_CHARS = new char[]{' ','\t','\n','\u000B','\f','\r','<','>','-',',','^','%','&','?','|','@','\'','\"'};
	
	static protected final String[] _TRANS_BASEKEY_STRING = new String[]{
		"__#sp#__","__#\\t#__","__#\\n#__","__#vt#__","__#\\f#__","__#\\r#__","__#lt#__","__#gt#__","__#hyp#__",
		"__#com#__","__#hat#__","__#per#__","__#amp#__","__#qm#__","__#or#__","__#at#__","__#apo#__","__#quo#__",
	};
	
	static protected final int TRANSPATTERN_INSIDE_MAXLEN = 3;
	static protected final int TRANSPATTERN_MINLEN = 3+3+2;
	
	static protected final Map<Character,String> _transmap = createTransformMap();
	static protected final Map<String,Character> _resumemap = createResumeMap();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	private BaseKeyTransformer() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された文字列に基底キーの使用禁止文字が含まれているかを判定する。
	 * @param str	判定する文字列
	 * @return	使用禁止文字が含まれている場合は true、それ以外の場合は false
	 */
	static public boolean containsInvalidCharacters(String str) {
		if (str == null || str.isEmpty())
			return false;

		if (str != null && !str.isEmpty()) {
			int len = str.length();
			// find illegal char
			for (int pos = 0; pos < len; pos++) {
				if (_transmap.containsKey(str.charAt(pos))) {
					return true;
				}
			}
		}
		
		// not found
		return false;
	}

	/**
	 * 基底キーの使用禁止文字を除去する。
	 * @param str	対象の文字列、または <code>null</code>
	 * @return	除去後の文字列、<em>str</em> が <code>null</code> の場合は <code>null</code>
	 */
	static public String removeInvalidCharacters(String str)
	{
		if (str == null)
			return str;

		int len = str.length();
		if (len > 0) {
			int pos = 0;
			// find illegal char
			for ( ; pos < len; pos++) {
				if (_transmap.containsKey(str.charAt(pos))) {
					break;
				}
			}
			// check has illegal char
			if (pos < len) {
				char ch;
				StringBuilder sb = new StringBuilder(len);
				if (pos > 0) {
					sb.append(str.substring(0, pos));
				}
				do {
					//--- skip illegal chars
					for (++pos; pos < len; pos++) {
						ch = str.charAt(pos);
						if (!_transmap.containsKey(ch)) {
							break;
						}
					}
					//--- find next illegal chars
					if (pos < len) {
						int spos = pos;
						for (++pos; pos < len; pos++) {
							ch = str.charAt(pos);
							if (_transmap.containsKey(ch)) {
								break;
							}
						}
						sb.append(str.substring(spos, pos));
					}
				} while (pos < len);
				str = sb.toString();
			}
		}

		return str;
	}

	/**
	 * 基底キーの使用禁止文字を置き換える。
	 * @param str	変換元の文字列、または <code>null</code>
	 * @return	変換後の文字列、<em>str</em> が <code>null</code> の場合は <code>null</code>
	 */
	static public String transformInvalidCharacters(String str)
	{
		if (str == null)
			return str;

		int len = str.length();
		if (len > 0) {
			int pos = 0;
			// find illegal char
			for ( ; pos < len; pos++) {
				if (_transmap.containsKey(str.charAt(pos))) {
					break;
				}
			}
			// check has illegal char
			if (pos < len) {
				char ch;
				StringBuilder sb = new StringBuilder(len*2);
				if (pos > 0) {
					sb.append(str.substring(0, pos));
				}
				do {
					//--- transform illegal chars
					do {
						ch = str.charAt(pos);
						String ts = _transmap.get(ch);
						if (ts == null) {
							break;
						}
						sb.append(ts);
						++pos;
					} while (pos < len);
					//--- find next illegal chars
					if (pos < len) {
						int spos = pos;
						for (++pos; pos < len; pos++) {
							ch = str.charAt(pos);
							if (_transmap.containsKey(ch)) {
								break;
							}
						}
						sb.append(str.substring(spos, pos));
					}
				} while (pos < len);
				str = sb.toString();
			}
		}

		return str;
	}

	/**
	 * 置き換えられた使用禁止文字を、元の文字列に戻す。
	 * @param basekey	置き換え済みの文字列、または <code>null</code>
	 * @return	使用禁止文字を含む文字列、<em>str</em> が <code>null</code> の場合は <code>null</code>
	 */
	static public String resumeInvalidCharacters(String basekey)
	{
		if (basekey == null)
			return basekey;

		int len = basekey.length();
		if (len >= TRANSPATTERN_MINLEN) {
			String strpat = null;
			int pos = 0;
			// find pattern
			for ( ; pos < len; pos++) {
				if ('_' == basekey.charAt(pos)) {
					if ((len-pos) >= TRANSPATTERN_MINLEN) {
						strpat = getMatchedTransformedPattern(basekey, len, pos);
						if (strpat != null) {
							break;
						}
					}
				}
			}
			// check has pattern
			if (strpat != null) {
				StringBuilder sb = new StringBuilder(len);
				if (pos > 0) {
					sb.append(basekey.substring(0, pos));
				}
				sb.append(_resumemap.get(strpat));
				pos += strpat.length();
				strpat = null;
				int spos = pos;
				do {
					//--- find pattern
					for ( ; pos < len; pos++) {
						if ('_' == basekey.charAt(pos)) {
							if ((len-pos) >= TRANSPATTERN_MINLEN) {
								strpat = getMatchedTransformedPattern(basekey, len, pos);
								if (strpat != null) {
									break;
								}
							}
						}
					}
					//--- resume pattern
					if (strpat != null) {
						if (spos < pos) {
							sb.append(basekey.substring(spos, pos));
						}
						sb.append(_resumemap.get(strpat));
						pos += strpat.length();
						spos = pos;
						strpat = null;
					}
				} while (pos < len);
				if (spos < pos) {
					sb.append(basekey.substring(spos, pos));
				}
				basekey = sb.toString();
			}
		}

		return basekey;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	static protected String getMatchedTransformedPattern(final String str, final int len, final int startPos)
	{
		int pos = startPos;
		//--- first mark : begin pattern
		if ('_' != str.charAt(pos++)) {
			return null;
		}
		//--- second mark : begin pattern
		if ('_' != str.charAt(pos++)) {
			return null;
		}
		//--- third mark : begin pattern
		if ('#' != str.charAt(pos++)) {
			return null;
		}
		//--- get inside pattern
		char ch = 0;
		int patlen = 0;
		for (; pos < len && patlen <= TRANSPATTERN_INSIDE_MAXLEN; pos++, patlen++) {
			ch = str.charAt(pos);
			if ('#' == ch) {
				break;
			}
		}
		//--- first mark : end pattern
		if (pos >= len || '#' != ch) {
			return null;
		}
		++pos;
		//--- second mark : end pattern
		if (pos >= len || '_' != str.charAt(pos++)) {
			return null;
		}
		//--- third mark : end pattern
		if (pos >= len || '_' != str.charAt(pos++)) {
			return null;
		}

		// check pattern
		String pat = str.substring(startPos, pos);
		if (_resumemap.containsKey(pat)) {
			return pat;
		} else {
			return null;
		}
	}

	static protected Map<Character,String> createTransformMap() {
		Map<Character,String> map = new HashMap<Character,String>();
		for (int i = 0; i < _ILLEGAL_BASEKEY_CHARS.length; i++) {
			map.put(_ILLEGAL_BASEKEY_CHARS[i], _TRANS_BASEKEY_STRING[i]);
		}
		return map;
	}

	static protected Map<String,Character> createResumeMap() {
		java.util.Map<String,Character> map = new HashMap<String,Character>();
		for (int i = 0; i < _ILLEGAL_BASEKEY_CHARS.length; i++) {
			map.put(_TRANS_BASEKEY_STRING[i], _ILLEGAL_BASEKEY_CHARS[i]);
		}
		return map;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
