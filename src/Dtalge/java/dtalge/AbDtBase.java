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
 *  Copyright 2007-2010  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)AbDtBase.java	0.20	2010/02/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbDtBase.java	0.10	2008/08/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package dtalge;

import java.util.Arrays;

/**
 * データ代数の１基底を保持するデータ代数基底の抽象クラス。
 * 
 * <p>このクラスは、不変オブジェクト(Immutable)であり、
 * データ代数基底クラスの基本機能を提供する。
 * 
 * データ代数基底は、4項の基底キーで構成され、次の順序で保持される。
 * <ol type="1" start="0">
 * <li>名前キー(name key)
 * <li>データ型キー(type key)
 * <li>属性キー(attribute key)
 * <li>主体キー(subject key)
 * </ol>
 * 交換代数基底と異なり、データ代数基底にはハットは定義されない。
 * また、データ代数基底の基底キーには、次の制約がある。
 * <ul>
 * <li>基底キーに、次の文字は使用できない。<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt; &gt; - , ^ &quot; % &amp; ? | @ ' " (空白)
 * <li>データ型キーに指定可能な文字列は、次のもののみとする。大文字と小文字は区別されない。
 * <ul>
 * <li><b>&quot;boolean&quot;</b>&nbsp;-&nbsp;真偽値型
 * <li><b>&quot;decimal&quot;</b>&nbsp;-&nbsp;実数値型
 * <li><b>&quot;string&quot;</b>&nbsp;&nbsp;-&nbsp;文字列型
 * </ul></li>
 * </ul>
 * <p>
 * データ代数基底は、<b>一意文字列キー(one string key)</b>で表現することができる。<br>
 * 一意文字列キーは、タブ(<code>'\t'</code>)で各キーが連結された文字列であり、
 * 基底キーの順序通りに連結されたものを指す。
 * <br>
 * また、データ代数基底クラスのハッシュ値は、全基底キーが同一であれば、ハッシュ値も
 * 同一であることを保証する。
 * <p>
 * このクラスにおいては、基底キーは任意の文字列であり、特殊な意味を持つ記号などを
 * 判別する機能は派生クラスにて実装する。
 * 
 * @version 0.20	2010/02/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public abstract class AbDtBase
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * 一意文字列キーの基底キー区切り文字
	 */
	static public final String DELIMITOR = "\t";

	/**
	 * 基本的な基底キーの使用禁止文字
	 */
	static protected final char[] _ILLEGAL_BASEKEY_ARRAY = new char[]{' ','\t','\n','\u000B','\f','\r','<','>','-',',','^','%','&','?','|','@','\'','\"'};
	
	/**
	 * 基底キー総数
	 */
	static protected final int NUM_ALL_KEYS = 4;

	/**
	 * 基底の名前キーのインデックス
	 */
	static protected final int KEY_NAME = 0;
	/**
	 * 基底のデータ型キーのインデックス
	 */
	static protected final int KEY_TYPE = 1;
	/**
	 * 基底の属性キーのインデックス
	 */
	static protected final int KEY_ATTR = 2;
	/**
	 * 基底の主体キーのインデックス
	 */
	static protected final int KEY_SUBJECT = 3;

	// XML ノード名
	/**
	 * XMLファイルフォーマットでの名前キーの属性名
	 */
	static protected final String XML_ATTR_NAMEKEY = "name";
	/**
	 * XMLファイルフォーマットでのデータ型キーの属性名
	 */
	static protected final String XML_ATTR_TYPEKEY = "type";
	/**
	 * XMLファイルフォーマットでの属性キーの属性名
	 */
	static protected final String XML_ATTR_ATTRKEY = "attr";
	/**
	 * XMLファイルフォーマットでの主体キーの属性名
	 */
	static protected final String XML_ATTR_SUBJECTKEY = "subject";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 基底キーの文字列配列
	 */
	protected final String[] _baseKeys = new String[NUM_ALL_KEYS];

	/**
	 * 基底のハッシュ値
	 */
	transient protected int _hashCode;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * データ代数基底に含まれる全基底キーの総数を返す。
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
	 * 基底のデータ型キーを返す。
	 * 
	 * @return データ型キー
	 */
	public String getTypeKey() {
		return this._baseKeys[KEY_TYPE];
	}

	/**
	 * 基底の属性キーを返す。
	 * 
	 * @return 属性キー
	 */
	public String getAttributeKey() {
		return this._baseKeys[KEY_ATTR];
	}

	/**
	 * 基底の主体キーを返す。
	 * 
	 * @return 主体キー
	 */
	public String getSubjectKey() {
		return this._baseKeys[KEY_SUBJECT];
	}

	/**
	 * 基底の一意文字列キーを返す。
	 * 
	 * @return 一意文字列キー
	 */
	public String key() {
		StringBuffer sb = new StringBuffer();
		sb.append(_baseKeys[0]);
		for (int i = 1; i < _baseKeys.length; ++i) {
			sb.append(DELIMITOR);
			sb.append(_baseKeys[i]);
		}
		return sb.toString();
	}
	
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
		return _hashCode;
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
		if (obj instanceof AbDtBase) {
			if (obj == this) {
				// 同一インスタンスなら true
				return true;
			}
			else if (Arrays.equals(_baseKeys, ((AbDtBase)obj)._baseKeys)) {
				// 同一値
				return true;
			}
		}
		
		// not equal
		return false;
	}
	
	/**
	 * このインスタンスを文字列として出力する。
	 * <br>
	 * 出力形式は、次の通り。
	 * <blockquote>
	 * '&lt;' <i>名前キー</i> ',' <i>データ型キー</i> ',' <i>属性キー</i> ',' <i>主体キー</i> '&gt;'
	 * </blockquote>
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<");
		sb.append(this._baseKeys[0]);
		for (int i = 1; i < this._baseKeys.length; i++) {
			sb.append(",");
			sb.append(this._baseKeys[i]);
		}
		sb.append(">");
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Implements for Comparable interfaces
	//------------------------------------------------------------

	/**
	 * 2 つの基底を辞書的に比較する。<br>
	 * 比較は、格納されている基底キーの順序で、各基底キー文字列を
	 * 辞書的に比較する。つまり、最初の基底キーから順に比較を行い、
	 * 一致しない基底キーが出現した時点で、その結果を返す。
	 * 全ての基底キーが一致した場合の結果は 0 となる。
	 * <br>
	 * 辞書的な文字列の比較は、{@link java.lang.String#compareTo(String)} の
	 * 実装による比較結果となる。
	 * 
	 * @param base 比較対象の基底
	 * @return 引数の基底がこの基底と等しい場合は、値 0。
	 * 			この基底が引数の基底より辞書的に小さい場合は、0 より小さい値。
	 * 			この基底が引数の基底より辞書的に大きい場合は、0 より大きい値。
	 * 
	 * @see java.lang.String#compareTo(String)
	 */
	protected <E extends AbDtBase> int baseCompareTo(E base) {
		for (int i = 0; i < this._baseKeys.length; i++) {
			int cmp = this._baseKeys[i].compareTo(base._baseKeys[i]);
			if (cmp != 0) {
				return cmp;
			}
		}
		
		return 0;	// same values
	}

	/**
	 * 大文字と小文字の区別なしで、2 つの基底を辞書的に比較する。<br>
	 * 比較は、格納されている基底キーの順序で、各基底キー文字列を
	 * 大文字と小文字を区別せずに比較する。
	 * つまり、最初の基底キーから順に比較を行い、
	 * 大文字と小文字の区別なしでも一致しない基底キーが出現した時点で、その結果を返す。
	 * 全ての基底キーが一致した場合の結果は 0 となる。
	 * <br>
	 * この比較は、{@link java.lang.String#compareToIgnoreCase(String)} の
	 * 実装による比較結果となる。
	 * 
	 * 
	 * @param base 比較対象の基底
	 * @return 大文字と小文字の区別なしで、引数の基底がこの基底と等しい場合は、値 0。
	 * 			大文字と小文字の区別なしで、この基底が引数の基底より小さい場合は負の整数、
	 * 			この基底が引数の基底より大きい場合は正の整数。
	 * 
	 * @see java.lang.String#compareToIgnoreCase(String)
	 */
	protected <E extends AbDtBase> int baseCompareToIgnoreCase(E base) {
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
	 * 指定されたインデックスに対応する基底キー名を取得する。
	 * <br>
	 * 基底キー名のインデックスは、次のようになる。
	 * <ol type="1" start="0">
	 * <li>name key
	 * <li>type key
	 * <li>attr key
	 * <li>subject key
	 * </ol>
	 * なお、このインデックス以外では &quot;#<i>番号</i> key&quot; とする。
	 * このとき、<i>番号</i>は、インデックスに1を加算した値とする。
	 * 
	 * @param index	インデックス
	 * @return		キー名
	 */
	static protected String getKeyName(int index) {
		String strKeyName;
		if (KEY_NAME == index) {
			strKeyName = "name key";
		} else if (KEY_TYPE == index) {
			strKeyName = "type key";
		} else if (KEY_ATTR == index) {
			strKeyName = "attribute key";
		} else if (KEY_SUBJECT == index) {
			strKeyName = "subject key";
		} else {
			strKeyName = "#" + (index+1) + " key";
		}
		return strKeyName;
	}

	/**
	 * 現在の基底キーで、ハッシュ値を更新する。
	 */
	protected void updateHashCode() {
		_hashCode = Arrays.hashCode(_baseKeys);
	}
}
