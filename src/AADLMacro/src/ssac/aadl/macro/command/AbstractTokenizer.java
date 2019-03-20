/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AbstractTokenizer.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbstractTokenizer.java	1.10	2009/12/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbstractTokenizer.java	1.00	2008/11/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.command;

import java.util.ArrayList;
import java.util.List;

/**
 * マクロのトークン解析クラスの基本クラス。
 * 
 * @version 2.1.0	2014/05/29
 *
 * @since 1.00
 */
public abstract class AbstractTokenizer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String[] EMPTY_STRING_ARRAY = new String[0];
	
	static public final int MIN_MACROARG_ID = 1;
	static public final int MAX_MACROARG_ID = 99;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final String srcText;
	protected final int srcLength;
	
	private final List<CommandToken> tokenList = new ArrayList<CommandToken>();
	
	private int tokenPos = 0;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbstractTokenizer(String text) {
		this.srcText = (text == null ? "" : text);
		this.srcLength = this.srcText.length();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int countTokens() {
		return tokenList.size();
	}
	
	public boolean hasMoreTokens() {
		return (tokenPos < tokenList.size());
	}

	/**
	 * 現在位置より一つ前のトークンを取得する。
	 * このメソッドの呼び出しでは、現在位置は変わらない。
	 * @return	取得したトークン、現在位置が先頭の場合は <tt>null</tt>
	 */
	public CommandToken getPreviousToken() {
		try {
			return tokenList.get(tokenPos-1);
		}
		catch (IndexOutOfBoundsException ignoreEx) {
			ignoreEx = null;
			return null;
		}
	}

	/**
	 * 現在位置より一つ先のトークンを取得する。
	 * このメソッドの呼び出しでは、現在位置は変わらない。
	 * @return	取得したトークン、現在位置が終端の場合は <tt>null</tt>
	 * @since 2.1.0
	 */
	public CommandToken getNextToken() {
		try {
			return tokenList.get(tokenPos+1);
		}
		catch (IndexOutOfBoundsException ignoreEx) {
			ignoreEx = null;
			return null;
		}
	}

	/**
	 * 現在位置のトークンを取得する。
	 * このメソッドの呼び出しでは、現在位置は変わらない。
	 * @return	取得したトークン、現在位置が終端に達している場合は <tt>null</tt>
	 */
	public CommandToken getToken() {
		try {
			return tokenList.get(tokenPos);
		}
		catch (IndexOutOfBoundsException ignoreEx) {
			ignoreEx = null;
			return null;
		}
	}
	
	public void consume() {
		tokenPos++;
	}
	
	public void backtrack() {
		tokenPos--;
	}
	
	public int getCurrentPosition() {
		return tokenPos;
	}
	
	public void setCurrentPosition(int newPos) {
		if (newPos < 0)
			tokenPos = 0;
		else if (newPos > tokenList.size())
			tokenPos = tokenList.size();
		else
			tokenPos = newPos;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("@");
		sb.append(Integer.toHexString(hashCode()));
		if (tokenList != null)
			sb.append(tokenList.toString());
		else
			sb.append("[]");
		return sb.toString();
	}

	//------------------------------------------------------------
	// Public helper interfaces
	//------------------------------------------------------------
	
	static public int indexOfIdentifierStart(final String text, final int startIndex) {
		return text.indexOf("${", startIndex);
	}
	
	static public int indexOfTemporaryIdentifierStart(final String text, final int startIndex) {
		int len = text.length()-5;
		int sIndex = startIndex;
		int index, idStart;
		String idText;
		do {
			index = text.indexOf('{', sIndex);
			if (index < 0)
				return (-1);
			if ((index-sIndex) >= 4) {
				idStart = index-4;
				idText = text.substring(idStart, index);
				if (idText.equalsIgnoreCase("$tmp")) {
					return idStart;
				}
			}
			sIndex = index+1;
		} while (sIndex < len);
		//--- not found
		return (-1);
	}
	
	static public int indexOfIdentifierEnd(final String text, final int startIndex) {
		return text.indexOf("}", startIndex);
	}

	/*
	 * テンポラリファイル識別子として正当なテキストか判定する。
	 * @param text	判定する文字列
	 * @return	テンポラリファイル識別子が含まれていない場合は (0)、正当なテンポラリファイル
	 * 			識別子の記述であれば (1)、そうでない場合は (-1) を返す。
	 *
	static public int isValidTemporaryID(final String text) {
		if (text == null)
			return 0;
		int sIndex = indexOfTemporaryIdentifierStart(text, 0);
		if (sIndex < 0)
			return 0;
		if (sIndex != 0)
			return (-1);	// invalid ID
		int idIndex = sIndex + 5;
		int eIndex = indexOfIdentifierEnd(text, idIndex);
		if (eIndex < 0)
			return (-1);	// invalid ID
		if (eIndex == idIndex)
			return (-1);	// invalid ID
		String id = text.substring(idIndex, eIndex);
		if (isDigit(id)) {
			return (1);	// valid ID
		} else {
			return (-1);	// invalid ID
		}
	}
	*/

	/**
	 * テンポラリファイル識別子として正当なテキストを取得する。
	 * テンポラリファイル識別子として正当なものは、大文字小文字を区別せずに、
	 * $tmp{文字} という書式が唯一含まれているものとする。この識別子の前後に
	 * 文字が存在する場合は、エラーとする。
	 * @param text	対象の文字列
	 * @return	テンポラリファイル識別子が存在しない場合は <tt>null</tt> を返す。
	 * 			テンポラリ識別子があるが、正当な記述ではない場合は空文字列(長さ 0 の文字列)を返す。
	 * 			正当な識別子であれば、唯一のテンポラリファイル識別子($tmp{数字})を返す。
	 * 			正当な場合に返される文字列は、先頭の'$tmp'のみ小文字に変換される。
	 * 
	 * @since 1.10
	 */
	static public String getValidTemporaryID(final String text) {
		if (text == null)
			return null;
		int len = text.length();
		int nextIndex = 0;
		int sIndex, eIndex, idIndex;
		sIndex = indexOfTemporaryIdentifierStart(text, nextIndex);
		if (sIndex < 0)
			return null;	// not exists
		if (sIndex != 0)
			return "";		// invalid characters with start
		idIndex = sIndex + 5;
		eIndex = indexOfIdentifierEnd(text, idIndex);
		if (eIndex < 0)
			return "";		// invalid temporary ID
		if (eIndex == idIndex)
			return "";		// invalid temporary ID
		nextIndex = eIndex + 1;
		if (nextIndex < len)
			return "";		// invalid characters with end
		
		return ("$tmp{" + text.substring(idIndex));
	}

	/**
	 * マクロ実行時引数の識別子として正当なテキストを取得する。
	 * マクロ実行時引数識別子として正当なものは、${1} ～ ${99} までのものとなる。
	 * @param text	対象の文字列
	 * @return	マクロ実行時引数の識別子が存在しない場合は <tt>null</tt> を返す。
	 * 			マクロ実行時引数の識別子があるが、正当な記述ではない場合は空の配列を返す。
	 * 			すべて正当な識別子であれば、すべての識別子の配列を返す。
	 * 
	 * @since 1.10
	 */
	static public String[] getValidMacroArgumentID(final String text) {
		if (text == null)
			return null;
		int len = text.length();
		int nextIndex = 0;
		int sIndex, eIndex, idIndex;
		String idText;
		ArrayList<String> idlist = null;
		do {
			sIndex = indexOfIdentifierStart(text, nextIndex);
			if (sIndex < 0)
				break;
			idIndex = sIndex + 2;
			eIndex = indexOfIdentifierEnd(text, idIndex);
			if (eIndex < 0)
				return EMPTY_STRING_ARRAY;	// invalid ID
			if (eIndex == idIndex)
				return EMPTY_STRING_ARRAY;	// invalid ID
			idText = text.substring(idIndex, eIndex);
			if (!isDigit(idText)) {
				return EMPTY_STRING_ARRAY;	// invalid ID
			}
			//--- check value
			try {
				int val = Integer.parseInt(idText);
				if (val < MIN_MACROARG_ID || val > MAX_MACROARG_ID) {
					return EMPTY_STRING_ARRAY;	// invalid ID
				}
			} catch (NumberFormatException ex) {
				return EMPTY_STRING_ARRAY;	// invalid ID
			}
			//--- add to list
			nextIndex = eIndex + 1;
			idText = text.substring(sIndex, nextIndex);
			if (idlist == null) {
				idlist = new ArrayList<String>();
				idlist.add(idText);
			} else {
				idlist.add(idText);
			}
		} while (nextIndex < len);
		
		if (idlist == null) {
			return null;
		} else {
			return idlist.toArray(new String[idlist.size()]);
		}
	}

	/**
	 * 参照プロセス名<code>('${' <i>IDENTIFIER</i> '}')</code> もしくは
	 * 実行時引数識別子<code>('${' <i>digits</i> '}')</code> もしくは
	 * テンポラリファイル識別子<code>('$tmp{' <i>digits</i> '}')</code> もしくは
	 * に文字列内で一致する箇所の先頭
	 * 位置(インデックス)を検索する。このメソッドが位置を返した場合、その位置以降に出現する
	 * 参照プロセス名終端文字<code>('}')</code>の間はプロセス名として正等であることが保証される。
	 * 参照プロセス名がみつからない場合は、(-1) を返す。
	 * 
	 * @param text			検索する文字列
	 * @param startIndex	検索を開始する位置(インデックス)
	 * @return 参照プロセス名の先頭位置を返す。見つからない場合は (-1) を返す。
	 */
	static public int findRefIdentifierStart(final String text, final int startIndex) {
		int len = text.length();
		int nextIndex = startIndex;
		int dollarIndex, sbIndex, ebIndex, idIndex;
		String strRefType;
		// ヘッダー
		do {
			//--- find '{'
			sbIndex = text.indexOf('{', nextIndex);
			if (sbIndex < 0)
				return (-1);	// not found '{'
			//--- find '$'
			for (dollarIndex = sbIndex - 1; dollarIndex >= nextIndex; dollarIndex--) {
				if ('$' == text.charAt(dollarIndex)) {
					break;
				}
			}
			if (dollarIndex < nextIndex) {
				nextIndex = sbIndex + 1;
				continue;	// not found '$' before '{'
			}
			idIndex = sbIndex + 1;
			nextIndex = idIndex;
			strRefType = text.substring(dollarIndex+1, sbIndex);
			if (strRefType.length() == 0) {
				// found '${' pattern
				ebIndex = text.indexOf('}', nextIndex);
				if (ebIndex < 0)
					return (-1);	// not found '}'
				nextIndex = ebIndex + 1;
				if (idIndex < ebIndex) {
					char c = text.charAt(idIndex++);
					if (isIdentifierStart(c)) {
						for (; idIndex < ebIndex; idIndex++) {
							c = text.charAt(idIndex);
							if (!isIdentifierStart(c) && !isDigit(c)) {
								break;
							}
						}
						if (idIndex == ebIndex) {
							// 参照プロセス名として正当
							return dollarIndex;
						}
					}
					else if (isDigit(c)) {
						for (; idIndex < ebIndex; idIndex++) {
							if (!isDigit(text.charAt(idIndex))) {
								break;
							}
						}
						if (idIndex == ebIndex) {
							// 実行時引数識別子として正当
							return dollarIndex;
						}
					}
				}
			}
			else if (strRefType.equalsIgnoreCase("tmp")) {
				// found '$tmp{' pattern
				ebIndex = text.indexOf('}', nextIndex);
				if (ebIndex < 0)
					return (-1);	// not found '}'
				nextIndex = ebIndex + 1;
				if (idIndex < ebIndex) {
					// テンポラリファイル識別子として正当
					return dollarIndex;
				}
			}
			// else : pattern not found
		} while (nextIndex < len);
		// not found ID
		return (-1);

		/*--- old codes
		int top = text.indexOf("${", startIndex);
		if (top < 0)
			return (-1);
		// ID先頭文字
		int pos = top + 2;
		if (pos < len && isIdentifierStart(text.charAt(pos))) {
			// found
			pos+=1;
			int epos = text.indexOf("}", pos);
			if (epos < 0)	// 終端が見つからないので、参照プロセス名なし
				return (-1);
			for (; pos < epos; pos++) {
				char c = text.charAt(pos);
				if (!isIdentifierStart(c) && !isDigit(c))
					break;
			}
			if (pos == epos) {
				// 参照プロセス名として正当な先頭
				return top;
			}
			else {
				// 参照記号内にIdentifierではない文字が含まれている。
				pos = epos + 1;
				if (pos < len)
					return findRefIdentifierStart(text, pos);
				else
					return (-1);
			}
		} else {
			// not found
			return findRefIdentifierStart(text, pos);
		}
		**--- end of old codes ---*/
	}

	/**
	 * 参照プロセス名の終端文字<code>('}')</code>の位置(インデックス)を返す。
	 * これは、検索開始位置から検索し、最初に見つかった終端文字の位置であり、
	 * その位置より前の文字列が参照プロセス名として正当かは保証しない。
	 * みつからない場合は (-1) を返す。
	 * 
	 * @param text			検索する文字列
	 * @param startIndex	検索を開始する位置(インデックス)
	 * @return 参照プロセス名の終端文字位置を返す。見つからない場合は (-1) を返す。
	 */
	static public int findRefIdentifierEnd(String text, int startIndex) {
		return text.indexOf("}", startIndex);
	}
	
	static public boolean isIdentifierString(String text) {
		int len = text.length();
		if (len <= 0)
			return false;
		//--- first char
		if (!isIdentifierStart(text.charAt(0)))
			return false;
		//--- next
		for (int i = 1; i < len; i++) {
			char c = text.charAt(i);
			if (!isIdentifierStart(c) && !isDigit(c))
				return false;
		}
		//--- valid text
		return true;
	}
	
	static public boolean isIntegerString(String text) {
		int len = text.length();
		if (len <= 0)
			return false;
		//--- check
		int i = 0;
		char c = text.charAt(i);
		if (c == '+' || c == '-') {
			i++;
		}
		for (; i < len; i++) {
			c = text.charAt(i);
			if (!isDigit(c)) {
				return false;
			}
		}
		//--- valid text
		return true;
	}
	
	static public boolean isReferenceIdentifierString(String text) {
		int len = text.length();
		if (len < 4)
			return false;
		//--- start chars
		if (!text.startsWith("${"))
			return false;
		if (!text.endsWith("}"))
			return false;
		//--- inner identifier
		String pn = text.substring(2, (len-1));
		return isIdentifierString(pn);
	}
	
	static public String getReferencedIdentifier(String text) {
		int len = text.length();
		if (len < 4)
			return null;
		return text.substring(2, (len-1));
	}
	
	static protected final boolean isDigit(final char c) {
		if ('0' <= c && c <= '9')
			return true;
		else
			return false;
	}
	
	static protected final boolean isIdentifierStart(final char c) {
		if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '_')
			return true;
		else
			return false;
	}
	
	static protected final boolean isDigit(final String text) {
		if (text == null)
			return false;
		int len = text.length();
		if (len <= 0)
			return false;
		for (int i = 0; i < len; i++) {
			 if (!isDigit(text.charAt(i))) {
				 return false;
			 }
		}
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected final int read(final int index) {
		try {
			return srcText.charAt(index);
		}
		catch (StringIndexOutOfBoundsException ignoreEx) {
			ignoreEx = null;
			return -1;
		}
	}
	
	protected final String getText(final int index, final int count) {
		return srcText.substring(index, (index+count));
	}
	
	protected final void addToken(final int type, final TextLocation loc, final String text) {
		CommandToken token = new CommandToken(type, loc.line, loc.posInLine, text);
		tokenList.add(token);
	}
	
	protected void skipWhitespace(TextLocation loc) {
		while (loc.index < srcLength) {
			char c = srcText.charAt(loc.index);
			if (c == '\r') {
				loc.seek(1);
				if (loc.index < srcLength && srcText.charAt(loc.index) == '\n') {
					loc.seek(1);
				}
				loc.newLine();
			}
			else if (c == '\n') {
				loc.seek(1);
				loc.newLine();
			}
			else if (Character.isWhitespace(c)) {
				loc.seek(1);
			}
			else {
				return;
			}
		}
	}
	
	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected final class TextLocation {
		private int index;
		private int line;
		private int posInLine;
		
		public TextLocation() {
			reset();
		}
		public final int getIndex() {
			return index;
		}
		public final void setIndex(int index) {
			this.index = index;
		}
		public final int getLine() {
			return line;
		}
		public final int getPositionInLine() {
			return posInLine;
		}
		public final void reset() {
			index = 0;
			line  = 1;
			posInLine = 0;
		}
		public final void seek(int count) {
			index += count;
			posInLine += count;
		}
		public final void newLine() {
			line++;
			posInLine = 0;
		}
	}
}
