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
 * @(#)DtBase.java	0.20	2010/03/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBase.java	0.10	2008/08/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import dtalge.exception.CsvFormatException;
import dtalge.io.internal.CsvReader;
import dtalge.io.internal.CsvWriter;
import dtalge.io.internal.XmlDocument;
import dtalge.util.DtDataTypes;
import dtalge.util.Strings;
import dtalge.util.Validations;
import dtalge.util.internal.CacheManager;
import dtalge.util.internal.XmlErrors;

/**
 * データ代数の基底を表すクラス。
 * <p>
 * このクラスは、不変オブジェクト(Immutable)である。
 * <p>
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
 * <b>注:</b>基底のデータ型キーは、基底生成時点で正規化(小文字化)される。
 * これは、基底クラスから({@link #getTypeKey()} メソッドで)取得したデータ型キーが、
 * データ型定義クラス({@link DtDataTypes})のデータ型名(
 * {@link DtDataTypes#BOOLEAN},
 *  {@link DtDataTypes#DECIMAL}, {@link DtDataTypes#STRING})と
 * 同一のものであることを保証する。
 * <p>
 * データ代数基底は、<b>一意文字列キー(one string key)</b>で表現することができる。<br>
 * 一意文字列キーは、タブ(<code>'\t'</code>)で各キーが連結された文字列であり、
 * 基底キーの順序通りに連結されたものを指す。
 * <br>
 * また、データ代数基底クラスのハッシュ値は、全基底キーが同一であれば、ハッシュ値も
 * 同一であることを保証する。
 * <p>
 * 省略されている基底キーは省略記号<code>('#')</code>で表される。
 * 
 * @version 0.20	2010/02/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public final class DtBase extends AbDtBase implements Comparable<DtBase>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * 基底キーの省略記号文字
	 */
	static public final String OMITTED = "#";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Factories
	//------------------------------------------------------------
	
	/**
	 * 一意文字列キーから <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、省略可能なキーのみ省略記号が代入される。
	 * <p>
	 * このメソッドは、{@link dtalge.util.DtConditions#isCachedBaseEnabled()} メソッドが
	 *  <tt>true</tt> を返すとき、{@link dtalge.DtBase#equals(Object)} メソッドによって
	 * 新規に生成されたインスタンスと等しいと判断される基底がキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * 新しいインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に新しいインスタンスを返す。
	 * 
	 * @param oneStringKey	データ代数基底の一意文字列キー
	 * @return	指定した引数で生成された基底
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>データ型キーが指定可能な文字列以外の場合
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	static public DtBase newBase(String oneStringKey) {
		return CacheManager.cacheDtBase(new DtBase(oneStringKey));
	}
	
	/**
	 * 指定されたキーを持つ <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * このコンストラクタでは、基底キーの省略はできない。
	 * また、属性キー、主体キーには省略記号が自動的に代入される。
	 * <p>
	 * このメソッドは、{@link dtalge.util.DtConditions#isCachedBaseEnabled()} メソッドが
	 *  <tt>true</tt> を返すとき、{@link dtalge.DtBase#equals(Object)} メソッドによって
	 * 新規に生成されたインスタンスと等しいと判断される基底がキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * 新しいインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に新しいインスタンスを返す。
	 * 
	 * @param nameKey	名前キー
	 * @param typeKey	データ型キー
	 * @return	指定した引数で生成された基底
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>データ型キーが指定可能な文字列以外の場合
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	static public DtBase newBase(String nameKey, String typeKey) {
		return CacheManager.cacheDtBase(new DtBase(nameKey, typeKey));
	}
	
	/**
	 * 指定されたキーを持つ <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、省略可能なキーのみ省略記号が代入される。
	 * また、主体キーには省略記号が自動的に代入される。
	 * <p>
	 * このメソッドは、{@link dtalge.util.DtConditions#isCachedBaseEnabled()} メソッドが
	 *  <tt>true</tt> を返すとき、{@link dtalge.DtBase#equals(Object)} メソッドによって
	 * 新規に生成されたインスタンスと等しいと判断される基底がキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * 新しいインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に新しいインスタンスを返す。
	 * 
	 * @param nameKey	名前キー
	 * @param typeKey	データ型キー
	 * @param attrKey	単位キー
	 * @return	指定した引数で生成された基底
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>データ型キーが指定可能な文字列以外の場合
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	static public DtBase newBase(String nameKey, String typeKey, String attrKey) {
		return CacheManager.cacheDtBase(new DtBase(nameKey, typeKey, attrKey));
	}
	
	/**
	 * 指定されたキーを持つ <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、省略可能なキーのみ省略記号が代入される。
	 * <p>
	 * このメソッドは、{@link dtalge.util.DtConditions#isCachedBaseEnabled()} メソッドが
	 *  <tt>true</tt> を返すとき、{@link dtalge.DtBase#equals(Object)} メソッドによって
	 * 新規に生成されたインスタンスと等しいと判断される基底がキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * 新しいインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に新しいインスタンスを返す。
	 * 
	 * @param nameKey	名前キー
	 * @param typeKey	データ型キー
	 * @param attrKey	単位キー
	 * @param subjectKey	主体キー
	 * @return	指定した引数で生成された基底
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>データ型キーが指定可能な文字列以外の場合
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	static public DtBase newBase(String nameKey, String typeKey, String attrKey, String subjectKey) {
		return CacheManager.cacheDtBase(new DtBase(nameKey, typeKey, attrKey, subjectKey));
	}
	
	/**
	 * 指定された基底キー配列から <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、省略可能なキーのみ省略記号が代入される。
	 * <p>
	 * このメソッドは、{@link dtalge.util.DtConditions#isCachedBaseEnabled()} メソッドが
	 *  <tt>true</tt> を返すとき、{@link dtalge.DtBase#equals(Object)} メソッドによって
	 * 新規に生成されたインスタンスと等しいと判断される基底がキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * 新しいインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に新しいインスタンスを返す。
	 * 
	 * @param baseKeys 基底キー配列。この配列内のキーは、次の順序で指定されているものとする。
	 * <ol type="1" start="0">
	 * <li>名前キー(name key)
	 * <li>データ型キー(type key)
	 * <li>属性キー(attribute key)
	 * <li>主体キー(subject key)
	 * </ol>
	 * @return	指定した引数で生成された基底
     * 
     * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>データ型キーが指定可能な文字列以外の場合
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	static public DtBase newBase(String[] baseKeys) {
		return CacheManager.cacheDtBase(new DtBase(baseKeys));
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 一意文字列キーから <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、省略可能なキーのみ省略記号が代入される。
	 * 
	 * @param oneStringKey	データ代数基底の一意文字列キー
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>データ型キーが指定可能な文字列以外の場合
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	public DtBase(String oneStringKey) {
		this(oneStringKey.split(DELIMITOR));
	}

	/**
	 * 指定されたキーを持つ <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * このコンストラクタでは、基底キーの省略はできない。
	 * また、属性キー、主体キーには省略記号が自動的に代入される。
	 * 
	 * @param nameKey	名前キー
	 * @param typeKey	データ型キー
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>データ型キーが指定可能な文字列以外の場合
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	public DtBase(String nameKey, String typeKey) {
		this(new String[]{nameKey, typeKey});
	}

	/**
	 * 指定されたキーを持つ <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、省略可能なキーのみ省略記号が代入される。
	 * また、主体キーには省略記号が自動的に代入される。
	 * 
	 * @param nameKey	名前キー
	 * @param typeKey	データ型キー
	 * @param attrKey	単位キー
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>データ型キーが指定可能な文字列以外の場合
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	public DtBase(String nameKey, String typeKey, String attrKey) {
		this(new String[]{nameKey, typeKey, attrKey});
	}

	/**
	 * 指定されたキーを持つ <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、省略可能なキーのみ省略記号が代入される。
	 * 
	 * @param nameKey	名前キー
	 * @param typeKey	データ型キー
	 * @param attrKey	単位キー
	 * @param subjectKey	主体キー
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>データ型キーが指定可能な文字列以外の場合
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	public DtBase(String nameKey, String typeKey, String attrKey, String subjectKey) {
		this(new String[]{nameKey, typeKey, attrKey, subjectKey});
	}

	/**
	 * 指定された基底キー配列から <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、省略可能なキーのみ省略記号が代入される。
	 * 
	 * @param baseKeys 基底キー配列。この配列内のキーは、次の順序で指定されているものとする。
	 * <ol type="1" start="0">
	 * <li>名前キー(name key)
	 * <li>データ型キー(type key)
	 * <li>属性キー(attribute key)
	 * <li>主体キー(subject key)
	 * </ol>
     * 
     * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>データ型キーが指定可能な文字列以外の場合
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	public DtBase(String[] baseKeys) {
		// Check keys
		Validations.validNotNull(baseKeys);
		Validations.validArgument((baseKeys.length > KEY_NAME) && !Strings.isNullOrEmpty(baseKeys[KEY_NAME]), "No name key");
		Validations.validArgument((baseKeys.length > KEY_TYPE) && !Strings.isNullOrEmpty(baseKeys[KEY_TYPE]), "No type key");
		
		// 基底キー文字列が有効文字列かを検証する
		int maxKeys = Math.min(baseKeys.length, NUM_ALL_KEYS);
		for (int i = 0; i < maxKeys; i++) {
			if (!Strings.isNullOrEmpty(baseKeys[i])) {
				// 基底キーとして無効な文字が含まれている場合は、エラーとする
				if (!Util.validBaseKeyString(baseKeys[i])) {
					String msg = "Illegal character is included in " + getKeyName(i) + ".";
					throw new IllegalArgumentException(msg);
				}
			}
		}
		
		// name key
		_baseKeys[KEY_NAME] = CacheManager.cacheString(baseKeys[KEY_NAME]);
		
		// type key
		_baseKeys[KEY_TYPE] = DtDataTypes.fromName(baseKeys[KEY_TYPE]);
		
		// other keys
		for (int i = KEY_TYPE + 1; i < _baseKeys.length; ++i) {
			if ((baseKeys.length > i) && !Strings.isNullOrEmpty(baseKeys[i])) {
				_baseKeys[i] = CacheManager.cacheString(baseKeys[i]);
			} else {
				_baseKeys[i] = OMITTED;
			}
		}
		
		// update hashCode
		updateHashCode();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implements for Object interfaces
	//------------------------------------------------------------

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
	public int compareTo(DtBase base) {
		return super.baseCompareTo(base);
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
	public int compareToIgnoreCase(DtBase base) {
		return super.baseCompareToIgnoreCase(base);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定された <code>writer</code> の現在位置へ、基底データを出力する。
	 * <br>
	 * 各基底キーは、次の順序で CSV カラムとして出力される。
	 * <ol type="1" start="1">
	 * <li>名前キー(name key)
	 * <li>データ型キー(type key)
	 * <li>属性キー(attribute key)
	 * <li>主体キー(subject key)
	 * </ol>
	 * なお、このメソッドによる出力では、レコード終端記号(改行)は出力されない。
	 * 
	 * @param writer 出力先となる <code>CsvWriter</code> オブジェクト
	 * 
	 * @throws IOException 書き込みに失敗した場合にスローされる
	 */
	protected void writeFieldToCSV(CsvWriter writer) throws IOException
	{
		// name key
		writer.writeField(getNameKey());
		
		// type key
		writer.writeField(getTypeKey());
		
		// attr key
		writer.writeField(getAttributeKey());
		
		// subject key
		writer.writeField(getSubjectKey());
	}

	/**
	 * 指定された <code>reader</code> の現在位置から、基底データを取得する。
	 * <br>
	 * 各基底キーは、現在のカラム位置から次の順序で読み込まれる。
	 * <ol type="1" start="1">
	 * <li>名前キー(name key)
	 * <li>データ型キー(type key)
	 * <li>属性キー(attribute key)
	 * <li>主体キー(subject key)
	 * </ol>
	 * <p>
	 * 読み込みにおいて、名前キーとデータ型キー以外は省略可能であり、
	 * 省略されている場合は省略記号({@link #OMITTED})に置き換えられる。
	 * また、各キーに使用禁止文字が含まれている場合はエラーとなる。
	 * 
	 * @param reader 入力元となる <code>CsvReader.CsvFieldReader</code> オブジェクト
	 * 
	 * @return 生成された <code>DtBase</code> の新しいインスタンスを返す。
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws IOException 読み込み時エラーが発生したときにスローされる
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 */
	static protected DtBase readFieldFromCSV(CsvReader.CsvFieldReader reader)
		throws IOException, CsvFormatException
	{
		// name key
		String nameKey = reader.readValue();
		if (Strings.isNullOrEmpty(nameKey)) {
			throw new CsvFormatException("name key cannot be omitted.",
											reader.getLineNo(), reader.getNextPosition());
		}
		else if (!Util.validBaseKeyString(nameKey)) {
			throw new CsvFormatException("Illegal name key : " + nameKey,
											reader.getLineNo(), reader.getNextPosition());
		}
		
		// type key
		String typeKey = reader.readValue();
		if (Strings.isNullOrEmpty(typeKey)) {
			throw new CsvFormatException("type key cannot be omitted.",
											reader.getLineNo(), reader.getNextPosition());
		}
		else {
			try {
				typeKey = DtDataTypes.fromName(typeKey);
			}
			catch (Throwable ex) {
				throw new CsvFormatException(ex.getMessage(), reader.getLineNo(), reader.getNextPosition());
			}
		}
		
		// attr key
		String attrKey = reader.readValue();
		if (Strings.isNullOrEmpty(attrKey)) {
			attrKey = DtBase.OMITTED;
		}
		else if (!Util.validBaseKeyString(attrKey)) {
			throw new CsvFormatException("Illegal attribute key : " + attrKey,
					reader.getLineNo(), reader.getNextPosition());
		}
		
		// subject key
		String subjectKey = reader.readValue();
		if (Strings.isNullOrEmpty(subjectKey)) {
			subjectKey = DtBase.OMITTED;
		}
		else if (!Util.validBaseKeyString(subjectKey)) {
			throw new CsvFormatException("Illegal subject key : " + subjectKey,
					reader.getLineNo(), reader.getNextPosition());
		}
		
		// make instance
		return DtBase.newBase(nameKey, typeKey, attrKey, subjectKey);
	}

	/**
	 * テーブル形式のCSVファイル読み込み時に、基底キーを保持するコンテナクラス
	 * 
	 * @since 0.20
	 */
	static protected final class BaseKeyContainer extends AbDtBase {}

	/**
	 * テーブル形式のCSVファイルの現在のレコードを、名前キーのレコードとして読み込む。
	 * @param record	読み込むレコード
	 * @param keylist	読み込んだ基底キーを保持するバッファ。このオブジェクトの要素は空であること。
	 * @throws IOException 読み込み時エラーが発生したときにスローされる
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.20
	 */
	static protected void readNameKeyFieldsFromTableCsv(CsvReader.CsvRecord record, List<BaseKeyContainer> keylist)
		throws IOException, CsvFormatException
	{
		// check field exists
		if (!record.hasFields()) {
			throw new CsvFormatException("name key cannot be omitted.",
					record.getLineNo(), 1);
		}
		
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);

		// read fields
		do {
			String nameKey = freader.readValue();
			if (Strings.isNullOrEmpty(nameKey)) {
				throw new CsvFormatException("name key cannot be omitted.",
												freader.getLineNo(), freader.getNextPosition());
			}
			else if (!Util.validBaseKeyString(nameKey)) {
				throw new CsvFormatException("Illegal name key : " + nameKey,
												freader.getLineNo(), freader.getNextPosition());
			}
			
			// create base keys container
			BaseKeyContainer container = new BaseKeyContainer();
			container._baseKeys[KEY_NAME]    = nameKey;
			container._baseKeys[KEY_ATTR]    = DtBase.OMITTED;
			container._baseKeys[KEY_SUBJECT] = DtBase.OMITTED;
			keylist.add(container);
		} while (freader.hasNextField());
	}

	/**
	 * テーブル形式のCSVファイルの現在のレコードを、データ型キーのレコードとして読み込む。
	 * @param record	読み込むレコード
	 * @param keylist	読み込んだ基底キーを保持するバッファ。読み込み済み名前キーの情報が格納されていること。
	 * @throws IOException 読み込み時エラーが発生したときにスローされる
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.20
	 */
	static protected void readTypeKeyFieldsFromTableCsv(CsvReader.CsvRecord record, List<BaseKeyContainer> keylist)
		throws IOException, CsvFormatException
	{
		// check field exists
		if (!record.hasFields()) {
			throw new CsvFormatException("type key cannot be omitted.",
					record.getLineNo(), 1);
		}
		
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);

		// read fields
		Iterator<BaseKeyContainer> it = keylist.iterator();
		do {
			// read key
			String typeKey = freader.readValue();
			if (Strings.isNullOrEmpty(typeKey)) {
				throw new CsvFormatException("type key cannot be omitted.",
												freader.getLineNo(), freader.getNextPosition());
			}
			else {
				try {
					typeKey = DtDataTypes.fromName(typeKey);
				}
				catch (Throwable ex) {
					throw new CsvFormatException(ex.getMessage(), freader.getLineNo(), freader.getNextPosition());
				}
			}
			
			// check name key exists
			if (!it.hasNext()) {
				throw new CsvFormatException("There is not name key corresponding to type key.",
						freader.getLineNo(), freader.getNextPosition());
			}
			
			// update base keys container
			BaseKeyContainer container = it.next();
			container._baseKeys[KEY_TYPE] = typeKey;
		} while (freader.hasNextField());
		
		// check remain name keys
		if (it.hasNext()) {
			throw new CsvFormatException("There is not type key corresponding to name key.",
					freader.getLineNo(), freader.getNextPosition());
		}
	}

	/**
	 * テーブル形式のCSVファイルの現在のレコードを、属性キーのレコードとして読み込む。
	 * @param record	読み込むレコード
	 * @param keylist	読み込んだ基底キーを保持するバッファ。読み込み済み名前キーの情報が格納されていること。
	 * @throws IOException 読み込み時エラーが発生したときにスローされる
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.20
	 */
	static protected void readAttributeKeyFieldsFromTableCsv(CsvReader.CsvRecord record, List<BaseKeyContainer> keylist)
		throws IOException, CsvFormatException
	{
		// check field exists
		if (!record.hasFields()) {
			return;		// all attribute keys are omitted.
		}
		
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);

		// read fields
		Iterator<BaseKeyContainer> it = keylist.iterator();
		do {
			// read key
			String attrKey = freader.readValue();
			if (Strings.isNullOrEmpty(attrKey)) {
				attrKey = DtBase.OMITTED;
			}
			else if (!Util.validBaseKeyString(attrKey)) {
				throw new CsvFormatException("Illegal attribute key : " + attrKey,
						freader.getLineNo(), freader.getNextPosition());
			}
			
			// check name key exists
			if (!it.hasNext()) {
				throw new CsvFormatException("There is not name key corresponding to attribute key.",
						freader.getLineNo(), freader.getNextPosition());
			}
			
			// update base keys container
			BaseKeyContainer container = it.next();
			container._baseKeys[KEY_ATTR] = attrKey;
		} while (freader.hasNextField());
	}
	
	/**
	 * テーブル形式のCSVファイルの現在のレコードを、主体キーのレコードとして読み込む。
	 * @param record	読み込むレコード
	 * @param keylist	読み込んだ基底キーを保持するバッファ。読み込み済み名前キーの情報が格納されていること。
	 * @throws IOException 読み込み時エラーが発生したときにスローされる
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.20
	 */
	static protected void readSubjectKeyFieldsFromTableCsv(CsvReader.CsvRecord record, List<BaseKeyContainer> keylist)
		throws IOException, CsvFormatException
	{
		// check field exists
		if (!record.hasFields()) {
			return;		// all subject keys are omitted.
		}
		
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);

		// read fields
		Iterator<BaseKeyContainer> it = keylist.iterator();
		do {
			// read key
			String subjectKey = freader.readValue();
			if (Strings.isNullOrEmpty(subjectKey)) {
				subjectKey = DtBase.OMITTED;
			}
			else if (!Util.validBaseKeyString(subjectKey)) {
				throw new CsvFormatException("Illegal subject key : " + subjectKey,
						freader.getLineNo(), freader.getNextPosition());
			}
			
			// check name key exists
			if (!it.hasNext()) {
				throw new CsvFormatException("There is not name key corresponding to subject key.",
						freader.getLineNo(), freader.getNextPosition());
			}
			
			// update base keys container
			BaseKeyContainer container = it.next();
			container._baseKeys[KEY_SUBJECT] = subjectKey;
		} while (freader.hasNextField());
	}
	
	/**
	 * XMLファイルフォーマットでのデータ代数基底のルートノード名
	 */
	static protected final String XML_ELEMENT_NAME = "Dtbase";
	
	/**
	 * 基底データを、XML ノードに変換する。
	 * <br>
	 * 変換されるノードの構成は、次のようになる。
	 * <blockquote>
	 * &lt;Dtbase name=&quote;<i>nameKey</i>&quote;
	 *  type=&quote;<i>typeKey</i>&quote;
	 *  attr=&quote;<i>attrKey</i>&quote;
	 *  subject=&quote;<i>subjectKey</i>&quote; /&gt;
	 * </blockquote>
	 * 
	 * @param xmlDocument ノード生成に使用する {@link XmlDocument} インスタンス
	 * @return 生成された XML ノード
	 * 
	 * @throws DOMException ノード生成に失敗した場合
	 */
	protected Element encodeToElement(XmlDocument xmlDocument) throws DOMException
	{
		Element node = xmlDocument.createElement(XML_ELEMENT_NAME);
		
		// 名前キー
		node.setAttribute(XML_ATTR_NAMEKEY, getNameKey());
		
		// 単位キー
		node.setAttribute(XML_ATTR_TYPEKEY, getTypeKey());
		
		// 時間キー
		node.setAttribute(XML_ATTR_ATTRKEY, getAttributeKey());
		
		// サブジェクトキー
		node.setAttribute(XML_ATTR_SUBJECTKEY, getSubjectKey());
		
		return node;
	}

	/**
	 * 指定された XML 属性から、基底データを生成する。
	 * 属性の解析において、名前キーとデータ型キー以外は省略可能であり、
	 * 省略されている場合は、省略記号({@link #OMITTED})に置き換えられる。
	 * また、各キーに使用禁止文字が含まれている場合はエラーとなる。
	 * 
	 * @param locator		SAX の読み込み位置を保持する <code>Locator</code> オブジェクト
	 * @param qName			前置修飾子を持つ修飾名。修飾名を使用できない場合は空文字列
	 * @param attributes	要素の属性。属性がない場合は空の <code>Attributes</code> オブジェクト
	 * 						になる。 
	 * 
	 * @return 生成された <code>DtBase</code> の新しいインスタンスを返す。
	 * 
	 * @throws SAXException	SAX 例外。ほかの例外をラップしている可能性がある
	 */
	static protected DtBase decodeFromXmlAttributes(Locator locator, String qName, Attributes attributes)
		throws SAXParseException
	{
		// name key
		String nameKey = attributes.getValue(XML_ATTR_NAMEKEY);
		if (Strings.isNullOrEmpty(nameKey) || !Util.validBaseKeyString(nameKey)) {
			XmlErrors.errorParseException(locator, "Illegal name key : \"%s\"", String.valueOf(nameKey));
		}
		
		// type key
		String typeKey = attributes.getValue(XML_ATTR_TYPEKEY);
		try {
			typeKey = DtDataTypes.fromName(typeKey);
		}
		catch (Throwable ex) {
			XmlErrors.errorParseException(locator, "Illegal type key : \"%s\"", String.valueOf(typeKey));
		}
		
		// attr key
		String attrKey = attributes.getValue(XML_ATTR_ATTRKEY);
		if (Strings.isNullOrEmpty(attrKey)) {
			attrKey = DtBase.OMITTED;
		}
		else if (!Util.validBaseKeyString(attrKey)) {
			XmlErrors.errorParseException(locator, "Illegal attribute key : \"%s\"", String.valueOf(attrKey));
		}
		
		// subject key
		String subjectKey = attributes.getValue(XML_ATTR_SUBJECTKEY);
		if (Strings.isNullOrEmpty(subjectKey)) {
			subjectKey = DtBase.OMITTED;
		}
		else if (!Util.validBaseKeyString(subjectKey)) {
			XmlErrors.errorParseException(locator, "Illegal subject key : \"%s\"", String.valueOf(subjectKey));
		}
		
		// make instance
		return DtBase.newBase(nameKey, typeKey, attrKey, subjectKey);
	}

	//------------------------------------------------------------
	// Internal classes
	//------------------------------------------------------------

	/**
	 * <code>{@link DtBase}</code> クラスに関する補助機能を提供するユーティリティクラス。
	 * 
	 * @version 0.10	2008/08/25
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	static public final class Util
	{
		/**
		 * 指定された文字列が使用可能文字のみで構成されているかを検証する。
		 * <p>
		 * このメソッドでは、長さ 0 の文字列の場合は <tt>true</tt> を返す。
		 * また、引数が <tt>null</tt> の場合は例外をスローする。
		 * 
		 * @param key	検証する文字列
		 * @return	使用可能文字のみで構成される基底キーなら <tt>true</tt> を返す。
		 * 
		 * @throws NullPointerException	<code>key</code> が <tt>null</tt> の場合
		 */
		static protected boolean validBaseKeyString(final String key) {
			int len = key.length();
			for (int i = 0; i < len; i++) {
				char bc = key.charAt(i);
				for (char c : _ILLEGAL_BASEKEY_ARRAY) {
					if (bc == c) {
						// 使用禁止文字を発見
						return false;
					}
				}
			}
			// 正常
			return true;
		}
		
		/**
		 * 入力文字列が許容される基底キー文字列であるかを検証する。
		 * <br>
		 * <code>isAllowedOmitted</code> に <tt>true</tt> が指定されており、
		 * <code>input</code> が <tt>null</tt> もしくは、長さ 0 の文字列の場合は <tt>true</tt> を返す。
		 * 
		 * @param input 入力文字列
		 * @param isAllowedOmitted 長さ 0 もしくは <tt>null</tt> の文字列を許容する場合は <tt>true</tt>
		 * @return 許容される基底キー文字列なら <tt>true</tt>
		 */
		static public boolean isValidBaseKeyString(final String input, final boolean isAllowedOmitted) {
			boolean result = false;
			
			if (input != null && input.length() > 0) {
				result = validBaseKeyString(input);
			}
			else {
				result = isAllowedOmitted;	// 省略の許容による
			}
			
			return result;
		}
		
		/**
		 * 入力文字列を基底キー文字列として正しい形式に整形する。
		 * <br>
		 * 整形は、次のように行われる。
		 * <ul>
		 * <li>入力文字列が空文字列(長さ 0、もしくは <tt>null</tt>)なら、省略記号({@link DtBase#OMITTED})とする
		 * </ul>
		 * 入力文字列が基底キー文字列として許容されない場合は、<tt>null</tt> を返す。
		 * <br>
		 * <code>isAllowedOmitted</code> に <tt>false</tt> が指定されている場合、
		 * 空文字列は許容されない。
		 * 
		 * @param input 入力文字列
		 * @param isAllowedOmitted 長さ 0 もしくは <tt>null</tt> の文字列を許容する場合は <tt>true</tt>
		 * @return 整形後の基底キー文字列
		 */
		static public String filterBaseKey(final String input, final boolean isAllowedOmitted) {
			if (input == null) {
				if (isAllowedOmitted) {
					// 省略は省略記号
					return DtBase.OMITTED;
				}
			}
			
			String result = null;
			if (input.length() > 0) {
				if (validBaseKeyString(input)) {
					result = input;
				}
			} else if (isAllowedOmitted) {
				// 省略は省略記号
				result = DtBase.OMITTED;
			}
			
			return result;
		}
	}
}
