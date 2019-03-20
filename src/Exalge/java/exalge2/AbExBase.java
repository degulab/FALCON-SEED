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
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)AbExBase.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbExBase.java	0.970	2009/03/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbExBase.java	0.94	2008/06/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbExBase.java	0.93	2007/09/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbExBase.java	0.91	2007/08/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbExBase.java	0.90	2007/07/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 交換代数の１基底を保持する交換代数基底の抽象クラス。
 * 
 * <p>このクラスは、不変オブジェクト(Immutable)であり、
 * 交換代数基底クラスの基本機能を提供する。
 * 
 * <p>交換代数の基底は、基本基底キー(basic base key)と
 * 拡張基底キー(extended base key)から構成され、次の順序で保持される。
 * <ol type="1" start="0">
 * <li>名前キー(name key)
 * <li>ハットキー(hat key)
 * <li>単位キー(unit key)
 * <li>時間キー(time key)
 * <li>サブジェクトキー(subject key)
 * </ol>
 * 基本基底キーは、「名前キー」と「ハットキー」。
 * <br>
 * 拡張基底キーは、「単位キー」、「時間キー」、「サブジェクトキー」。
 * <br>
 * 基底におけるキーの順序は、上記の通りとなる。
 * 
 * <p>交換代数基底は、<b>一意文字列キー(one string key)</b>で表現することができる。<br>
 * 一意文字列キーは、<code>'-'</code>(ハイフン)で各キーが連結された文字列であり、
 * 基底キーの順序通りに連結されたものを指す。
 * <br>
 * また、交換代数基底クラスのハッシュ値は、全基底キーが同一であれば、ハッシュ値を
 * 同一であることを保障する。
 * 
 * <p>このクラスにおいては、基底キーは任意の文字列であり、特殊な意味を持つ記号などを
 * 判別する機能は派生クラスにて実装する。
 * 
 * @version 0.982	2009/09/13
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Li Hou(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.90
 */
public abstract class AbExBase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String _HAT_MARK = "^";
	
	static protected final String _HATKEY_WITH_HAT_LIST = ExBase.HAT + "|\\" + _HAT_MARK;
	
	static protected final String _HATKEY_NO_HAT_LIST = ExBase.NO_HAT;

	/**
	 * 基本的なハットキー許容パターン
	 */
	static protected final String _BASIC_HATKEY_LIST = _HATKEY_NO_HAT_LIST + "|" + _HATKEY_WITH_HAT_LIST;
	
	static protected final Pattern _IS_HAT_PATTERN
				= Pattern.compile("\\A(" + _HATKEY_WITH_HAT_LIST + ")\\z", Pattern.CASE_INSENSITIVE);

	/**
	 * 基本的な基底キーの使用禁止文字パターン
	 * @deprecated (0.970)マッチングアルゴリズムの変更により、このフィールドは削除されます。
	 */
	//static protected final String _ILLEGAL_BASEKEY_LIST = "\\s<>\\-+/=\\^,~%&?|@'\"";
	static protected final String _ILLEGAL_BASEKEY_LIST = "\\s\\<\\>\\-\\,\\^\\%\\&\\?\\|\\@\\'\\\"";
	
	/**
	 * 基本的な基底キーの使用禁止文字
	 */
	static protected final char[] _ILLEGAL_BASEKEY_CHARS = new char[]{' ','\t','\n','\u000B','\f','\r','<','>','-',',','^','%','&','?','|','@','\'','\"'};

	/**
	 * 基本基底キー総数
	 */
	static protected final int NUM_BASIC_KEYS = 2;
	
	/**
	 * 基底キー総数
	 */
	static protected final int NUM_ALL_KEYS = NUM_BASIC_KEYS + ExtendedKeyID.NUM_KEYS;
	
	static protected final int KEY_NAME = 0;
	static protected final int KEY_HAT  = 1;
	static protected final int KEY_EXT_UNIT    = NUM_BASIC_KEYS + ExtendedKeyID.UNIT.intValue();
	static protected final int KEY_EXT_TIME    = NUM_BASIC_KEYS + ExtendedKeyID.TIME.intValue();
	static protected final int KEY_EXT_SUBJECT = NUM_BASIC_KEYS + ExtendedKeyID.SUBJECT.intValue();

	/**
	 * 一意文字列キーの基底キー区切り文字
	 */
	static public final String DELIMITOR = "-";

	// XML ノード名
	static protected final String XML_ELEMENT_NAME = "Exbase";
	static protected final String XML_ATTR_HATKEY = "hat";
	static protected final String XML_ATTR_NAMEKEY = "name";
	static protected final String XML_ATTR_UNITKEY = "unit";
	static protected final String XML_ATTR_TIMEKEY = "time";
	static protected final String XML_ATTR_SUBJECTKEY = "subject";

	/**
	 * 指定された文字列がハットを示す文字列であるかを判定する。
	 * ハットとみなす文字列は、大文字小文字を区別しない <code>&quot;{@value ExBase#HAT}&quot</code>、もしくは <code>&quot;^&quot;</code> のみとなる。
	 * @param strHatKey	判定する文字列
	 * @return	ハットを示す文字列である場合は <tt>true</tt>
	 * 
	 * @since 0.970
	 */
	static protected final boolean isHatString(String strHatKey) {
		return (ExBase.HAT.equalsIgnoreCase(strHatKey) || _HAT_MARK.equals(strHatKey));
	}

	/**
	 * 指定された文字列がハットなしを示す文字列であるかを判定する。
	 * ハットなしとみなす文字列は、大文字小文字を区別しない <code>&quot;{@value ExBase#NO_HAT}&quot;</code> のみとなる。
	 * @param strHatKey	判定する文字列
	 * @return	ハットなしを示す文字列である場合は <tt>true</tt>
	 * 
	 * @since 0.970
	 */
	static protected final boolean isNoHatString(String strHatKey) {
		return (ExBase.NO_HAT.equalsIgnoreCase(strHatKey));
	}

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 基底キーの文字列配列
	 */
	protected String[] _baseKeys;
	/**
	 * 基底のハッシュ値
	 */
	transient protected int _hashcode;
	/*
	 * 基底キーの一意文字列キー
	 *
	transient protected String _oneStringKey;
	*/
	
	//------------------------------------------------------------
	// Initialization
	//------------------------------------------------------------

	/**
	 * 交換代数基底の初期化。
	 * <br>
	 * 指定された基底キーにより、基底キー配列を初期化する。
	 * 全基底キーにおいて、指定されたキー文字列が <tt>null</tt> もしくは、長さ 0 の文字列の場合、
	 * <tt>defaultKey</tt> に指定された文字列がキー文字列としてセットされる。
	 * <br>
	 * このメソッドでは、キー文字列の正当性検証は行わない。
	 * 
	 * @param keys	交換代数の基底となるキーの配列。次の順序で指定されているものとする。
	 * <ol type="1" start="0">
	 * <li>名前キー
	 * <li>ハットキー
	 * <li>単位キー
	 * <li>時間キー
	 * <li>サブジェクトキー
	 * </ol>
	 * 
	 * @param defaultKey 基底キー省略時に使用されるキー文字列。必ず有効文字列を指定すること。
	 * 
	 * @throws	IllegalArgumentException	<tt>defaultKey</tt> が <tt>null</tt> もしくは、
	 * 										長さ 0 の文字列の場合にスローされる
	 */
	protected void initializeBaseKeys(String[] keys, String defaultKey) {
		// Check defaultKey
		if (defaultKey == null || defaultKey.length() <= 0)
			throw new IllegalArgumentException("defaultKey is illegal");
		
		// create keys
		this._baseKeys = new String[NUM_ALL_KEYS];
		
		// set keys
		int remain = 0;
		if (keys != null) {
			for (; (remain < keys.length) && (remain < NUM_ALL_KEYS); remain++) {
				if (keys[remain] != null && keys[remain].length() > 0) {
					// 指定のキー文字列をセット
					this._baseKeys[remain] = keys[remain];
				} else {
					// 指定の defaultKey をセット
					this._baseKeys[remain] = defaultKey;
				}
			}
		}
		
		// set default key to remain keys
		for (; remain < NUM_ALL_KEYS; remain++) {
			this._baseKeys[remain] = defaultKey;
		}
		
		// setup status
		setupStatus();
	}

	/**
	 * 交換代数基底の全ての基底キーに指定された文字列を設定する。
	 * このメソッドは、指定された値のチェックを行わないため、
	 * 基底キーの値として正当であることが保証されていることを前提とする。
	 * 
	 * @param baseKey	基底キーとする文字列
	 * 
	 * @since 0.982
	 */
	protected void fillBaseKeys(String baseKey) {
		this._baseKeys = new String[NUM_ALL_KEYS];
		for (int i = 0; i < NUM_ALL_KEYS; i++) {
			this._baseKeys[i] = baseKey;
		}
		setupStatus();
	}

	/*
	 * 現在の基底キー文字列から、一意文字列キーを更新する。
	 *
	protected void updateOneStringKey() {
		StringBuffer sb = new StringBuffer();
		sb.append(this._baseKeys[KEY_NAME]);
		for (int i = 1; i < this._baseKeys.length; i++) {
			sb.append(DELIMITOR);
			sb.append(this._baseKeys[i]);
		}
		this._oneStringKey = sb.toString();
	}
	*/

	/**
	 * 基底のキー文字列以外のステータスを設定する。
	 */
	protected void setupStatus() {
		// ハッシュ値の更新
		_hashcode = makeOneStringKey().hashCode();
	}
	
	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	/**
	 * 交換代数基底に含まれる全基底キーの総数を返す。
	 * 
	 * @return 全基底キー総数
	 */
	static public int getNumAllKeys() {
		return NUM_ALL_KEYS;
	}

	/**
	 * 基底の名前キーを返す。
	 * 
	 * @return 名前キー
	 */
	public String getNameKey() {
		return this._baseKeys[KEY_NAME];
	}

	/**
	 * 基底のハットキーを返す。
	 * 
	 * @return ハットキー
	 */
	public String getHatKey() {
		return this._baseKeys[KEY_HAT];
	}

	/**
	 * 基底の単位キーを返す。
	 * 
	 * @return 単位キー
	 */
	public String getUnitKey() {
		return this._baseKeys[KEY_EXT_UNIT];
	}

	/**
	 * 基底の時間キーを返す。
	 * 
	 * @return 時間キー
	 */
	public String getTimeKey() {
		return this._baseKeys[KEY_EXT_TIME];
	}

	/**
	 * 基底のサブジェクトキーを返す。
	 * 
	 * @return サブジェクトキー
	 */
	public String getSubjectKey() {
		return this._baseKeys[KEY_EXT_SUBJECT];
	}

	/**
	 * 指定のキーIDに対応する拡張基底キーを取得する。
	 * 
	 * @param keyid 拡張基底キーのキーID。指定可能な値は、次のいずれかとなる。
	 * <ul>
	 * <li>{@link ExtendedKeyID#UNIT} - 単位キー
	 * <li>{@link ExtendedKeyID#TIME} - 時間キー
	 * <li>{@link ExtendedKeyID#SUBJECT} - サブジェクトキー
	 * </ul>
	 * 
	 * @return 指定の拡張基底キー
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public String getExtendedKey(ExtendedKeyID keyid) {
		// Check keyid
		if (keyid == null) {
			throw new NullPointerException();
		}
		
		// get extended key
		return this._baseKeys[NUM_BASIC_KEYS + keyid.intValue()];
	}

	/**
	 * 基底の一意文字列キーを返す。
	 * 
	 * @return 一意文字列キー
	 */
	public String key() {
		//return this._oneStringKey;
		return makeOneStringKey();
	}

	/**
	 * 基本基底キーのみで構成された一意文字列を返す。
	 * <br>
	 * 基本基底キーの一意文字列は、「名前キー」と「ハットキー」を
	 * 一意文字列キー区切り文字({@link #DELIMITOR})で連結したもの。
	 * 
	 * @return 基本基底キーの一意文字列
	 */
	public String getBasicKeysString() {
		return (this._baseKeys[KEY_NAME] + DELIMITOR + this._baseKeys[KEY_HAT]);
	}

	/**
	 * 拡張基底キーのみで構成された一意文字列を返す。
	 * <br>
	 * 拡張基底キーの一意文字列は、「単位キー」、「時間キー」、「サブジェクトキー」を
	 * 一意文字列キー区切り文字({@link #DELIMITOR})で連結したもの。
	 * 
	 * @return 拡張基底キーの一意文字列
	 */
	public String getExtendedKeysString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this._baseKeys[NUM_BASIC_KEYS]);
		for (int i = NUM_BASIC_KEYS + 1; i < this._baseKeys.length; i++) {
			sb.append(DELIMITOR);
			sb.append(this._baseKeys[i]);
		}
		return sb.toString();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * ハッシュコード値を返す。<br>
	 * ハッシュコード値は、基底の一意文字列キーのハッシュコード値に相当する。
	 * そのため、基底キーが同一であれば、ハッシュコード値も同一となる。
	 * 
	 * @return ハッシュコード値
	 *  
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (_hashcode == 0) {
			// 一意文字列キーのhashCodeをExBaseのhashCodeとする。
			_hashcode = makeOneStringKey().hashCode();
		}
		return _hashcode;
	}

	/**
	 * 基底が等しいかどうかを示す。
	 * <br>
	 * 基底キーが同一の場合のみ、true を返す。
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// 同一クラスであり、基底の一意文字列キーが同一の場合、等しいとする。
		if (obj instanceof AbExBase) {
			if (obj == this) {
				// 同一インスタンスなら true
				return true;
			}
			else if (this.key().equals(((AbExBase)obj).key())) {
				// 同一値
				return true;
			}
		}
		
		// not equal
		return false;
	}

	/**
	 * 一意文字列キーを返す。
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return key();
	}

	//------------------------------------------------------------
	// Implements for Comparable interfaces
	//------------------------------------------------------------

	protected <E extends AbExBase> int baseCompareTo(E base) {
		for (int i = 0; i < this._baseKeys.length; i++) {
			int cmp = this._baseKeys[i].compareTo(base._baseKeys[i]);
			if (cmp != 0) {
				return cmp;
			}
		}
		
		return 0;	// same values
	}

	protected <E extends AbExBase> int baseCompareToIgnoreCase(E base) {
		for (int i = 0; i < this._baseKeys.length; i++) {
			int cmp = this._baseKeys[i].compareToIgnoreCase(base._baseKeys[i]);
			if (cmp != 0) {
				return cmp;
			}
		}
		
		return 0;	// equal values
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 現在の基底キー文字列から、一意文字列キーを生成する。
	 * 
	 * @since 0.970
	 */
	protected String makeOneStringKey() {
		// 文字数の合計値を算出
		int len = 0;
		for (String key : _baseKeys) {
			len += key.length();
		}
		len = len + (_baseKeys.length - 1);	// '-' の分
		
		// 一意文字列キーを生成
		StringBuilder sb = new StringBuilder(len);
		sb.append(_baseKeys[0]);
		for (int i = 1; i < _baseKeys.length; i++) {
			sb.append(DELIMITOR);
			sb.append(_baseKeys[i]);
		}
		
		return sb.toString();
	}

	/**
	 * 指定されたインデックスに対応する基底キー名を取得する。
	 * <br>
	 * 基底キー名のインデックスは、次のようになる。
	 * <ol type="1" start="0">
	 * <li>name key
	 * <li>hat key
	 * <li>unit key
	 * <li>time key
	 * <li>subject key
	 * </ol>
	 * なお、このインデックス以外では &quot;#<i>番号</i> key&quot; とする。
	 * このとき、<i>番号</i>は、インデックスに1を加算した値とする。
	 * 
	 * @param index	インデックス
	 * @return		キー名
	 * 
	 * @since 0.94
	 */
	static protected String getKeyName(int index) {
		String strKeyName;
		if (KEY_NAME == index) {
			strKeyName = "name key";
		} else if (KEY_HAT == index) {
			strKeyName = "hat key";
		} else if (KEY_EXT_UNIT == index) {
			strKeyName = "unit key";
		} else if (KEY_EXT_TIME == index) {
			strKeyName = "time key";
		} else if (KEY_EXT_SUBJECT == index) {
			strKeyName = "subject key";
		} else {
			strKeyName = "#" + (index+1) + " key";
		}
		return strKeyName;
	}

	/**
	 * 指定された基底クラスのフィールドを、このインスタンスにコピーする。
	 */
	protected void copyFrom(AbExBase base) {
		this._baseKeys = base._baseKeys.clone();
		this._hashcode = base._hashcode;
		//this._oneStringKey = base._oneStringKey;
	}

	/**
	 * 指定された拡張基底キーで、このインスタンスの拡張基底キーを更新する。
	 * <br>
	 * 拡張基底キー更新とともに、一意文字列キーも更新される。
	 * 
	 * @param keyid 更新対象の拡張基底キーのキーID。指定可能な値は、次のいずれかとなる。
	 * <ul>
	 * <li>{@link ExtendedKeyID#UNIT} - 単位キー
	 * <li>{@link ExtendedKeyID#TIME} - 時間キー
	 * <li>{@link ExtendedKeyID#SUBJECT} - サブジェクトキー
	 * </ul>
	 * @param newKey
	 */
	protected void modifyExtendedKey(ExtendedKeyID keyid, String newKey) {
		this._baseKeys[NUM_BASIC_KEYS + keyid.intValue()] = newKey;
		setupStatus();
		//updateOneStringKey();
	}

	/*
	 * 指定された <code>writer</code> の現在位置へ、基底データを出力する。
	 * <br>
	 * このメソッドは、派生クラスにて実装すること。
	 * 
	 * @param writer 出力先となる <code>CsvWriter</code> オブジェクト
	 * 
	 * @throws UnsupportedOperationException 常にスローされる
	 * 
	 * @since 0.91
	 *
	protected void writeToCSV(CsvWriter writer)
		throws IOException
	{
		throw new UnsupportedOperationException();
	}
	*/

	/*
	 * 指定された <code>reader</code> の現在位置から、基底データを取得する。
	 * <br>
	 * このメソッドは、派生クラスにて実装すること。
	 * 
	 * @param reader 入力元となる <code>CsvRecordReader</code> オブジェクト
	 * 
	 * @throws UnsupportedOperationException 常にスローされる
	 * 
	 * @since 0.91
	 *
	protected void readFromCSV(CsvRecordReader reader)
		throws IOException, CsvFormatException
	{
		throw new UnsupportedOperationException();
	}
	*/

	/*
	 * クラスインスタンスのデータを、XML ノードに変換する。
	 * <br>
	 * このメソッドは、派生クラスにて実装すること。
	 * 
	 * @param xmlDocument ノード生成に使用する {@link XmlDocument} インスタンス
	 * @return 生成された XML ノード
	 * 
	 * @throws DOMException ノード生成に失敗した場合
	 * 
	 * @since 0.91
	 *
	protected Element encodeToElement(XmlDocument xmlDocument) throws DOMException
	{
		throw new UnsupportedOperationException();
	}
	*/

	/*
	 * 指定された XML ノードから、クラスインスタンスのデータを更新する。
	 * <br>
	 * このメソッドは、派生クラスにて実装すること。
	 * 
	 * @param xmlDocument ノードが所属する {@link XmlDocument} インスタンス
	 * @param xmlElement 解析対象の XML ノード
	 * 
	 * @throws XmlDomParseException ノード解析エラーが発生した場合
	 * 
	 * @since 0.91
	 *
	protected void decodeFromElement(XmlDocument xmlDocument, Element xmlElement)
		throws XmlDomParseException
	{
		throw new UnsupportedOperationException();
	}
	*/

	/**
	 * 文字候補を保持するセット。
	 * このセットを利用し、ある文字列にこのセットの文字が含まれているか否かの
	 * 判定に利用する。
	 * 
	 * @version 0.970	2009/03/10
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 *
	 * @since 0.970
	 */
	static protected final class CharCandidates
	{
		/**
		 * 候補文字の配列
		 */
		private final char[] candidates;
		/**
		 * 候補文字の最小文字
		 */
		private char minCandidateChar;
		/**
		 * 候補文字の最大文字
		 */
		private char maxCandidateChar;

		/**
		 * 指定された全ての文字を候補とする、<code>CharCandidates</code> の新しいインスタンスを生成する。
		 * @param candidates	候補とする文字、もしくは文字配列
		 * @throws NullPointerException	文字配列が <tt>null</tt> の場合
		 * @throws IllegalArgumentException	候補文字が一つも指定されていない場合
		 */
		public CharCandidates(char...candidates) {
			if (candidates.length <= 0)
				throw new IllegalArgumentException();
			this.candidates = new char[candidates.length];
			System.arraycopy(candidates, 0, this.candidates, 0, candidates.length);
			setupCandidates();
		}

		/**
		 * 指定された文字列に含まれる全ての文字を候補とする、<code>CharCandidates</code> の新しいインスタンスを生成する。
		 * @param candidates	候補とする文字とする文字列
		 * @throws NullPointerException	引数が <tt>null</tt> の場合
		 * @throws IllegalArgumentException	候補文字が一つも指定されていない場合
		 */
		public CharCandidates(String candidates) {
			int len = candidates.length();
			if (len <= 0)
				throw new IllegalArgumentException();
			this.candidates = new char[len];
			for (int i = 0; i < len; i++) {
				this.candidates[i] = candidates.charAt(i);
			}
			setupCandidates();
		}

		/**
		 * 候補文字をセットアップする。
		 * このメソッドでは、候補文字配列を昇順にソートし、
		 * 文字の最大値と最小値を保存する。
		 */
		private void setupCandidates() {
			Arrays.sort(candidates);
			minCandidateChar = candidates[0];
			maxCandidateChar = candidates[candidates.length-1];
		}

		/**
		 * 候補文字数を返す。
		 * @return	候補文字数
		 */
		public int size() {
			return candidates.length;
		}

		/**
		 * 候補の最小文字を返す。
		 * @return	候補の最小文字
		 */
		public char getMininumCandidateChar() {
			return minCandidateChar;
		}

		/**
		 * 候補の最大文字を返す。
		 * @return	候補の最大文字
		 */
		public char getMaximumCandidateChar() {
			return maxCandidateChar;
		}

		/**
		 * 指定した文字が候補に含まれているかを判定する。
		 * @param c	判定する文字
		 * @return	候補に含まれている場合は <tt>true</tt>
		 */
		public boolean contains(char c) {
			if (minCandidateChar <= c && c <= maxCandidateChar) {
				if (Arrays.binarySearch(candidates, c) >= 0) {
					return true;
				}
			}
			return false;
		}

		/**
		 * 指定した文字列に候補文字が含まれているかを判定する。
		 * @param str	判定する文字列
		 * @return		候補文字が含まれている場合は <tt>true</tt>
		 */
		public boolean isIncluded(String str) {
			if (str == null)
				return false;
			
			int len = str.length();
			if (len <= 0)
				return false;
			
			for (int i = 0; i < len; i++) {
				char c = str.charAt(i);
				if (contains(c)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * 指定した文字列に候補文字が含まれていないかを判定する。
		 * @param str	判定する文字列
		 * @return		候補文字が含まれていない場合は <tt>true</tt>
		 */
		public boolean isExcluded(String str) {
			return !isIncluded(str);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(candidates);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			
			if (obj instanceof CharCandidates) {
				if (Arrays.equals(this.candidates, ((CharCandidates)obj).candidates)) {
					return true;
				}
			}
			
			return false;
		}

		@Override
		public String toString() {
			return Arrays.toString(candidates);
		}
	}
}
