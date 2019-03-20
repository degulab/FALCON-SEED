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
 * @(#)DtBasePattern.java	0.20	2010/02/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBasePattern.java	0.10	2008/08/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

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
import dtalge.util.Strings;
import dtalge.util.Validations;
import dtalge.util.internal.CacheManager;
import dtalge.util.internal.XmlErrors;

/**
 * データ代数 1 基底のマッチングパターンを保持するクラス。
 * 
 * <p>このクラスは、不変オブジェクト(Immutable)である。
 * <p>
 * 基本的にデータ代数基底クラス {@link DtBase} と同様のデータ構造となり、
 * 各基底キーを {@link DtBase} と同じ順序で保持する。
 * さらに、ワイルドカード記号({@link #WILDCARD})を基底キーに指定することができる。
 * <br>
 * ワイルドカード記号は、データ代数基底の基底に対し、0文字以上の文字と
 * マッチングする。
 * 
 * <p>このクラスでは、基底キーの指定において、キーが省略(<tt>null</tt> もしくは、
 * 長さ 0 の文字列が指定)された場合、ワイルドカード記号文字を代入する。
 * <p><b>注:</b>このクラスに格納されるデータ型キーは、指定された文字列そのものを
 * 格納する。<code>{@link DtBase}</code> のようにデータ型基底は
 * <b>正規化(小文字化)</b>されない。
 * 
 * @version 0.20	2010/02/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public final class DtBasePattern extends AbDtBase implements Comparable<DtBasePattern>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * 基底キーのワイルドカード記号文字列
	 */
	static public final String WILDCARD = "*";
	/**
	 * 基底キーのワイルドカード記号
	 */
	static public final char WILDCARD_CHAR = '*';

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 基底のマッチングパターンを格納する配列。
	 * この配列には、ワイルドカードのみのパターンは含まれない。
	 * 全ての基底キーがワイルドカードのみの場合、このフィールドは <tt>null</tt> となる。
	 */
	private PatternItem[] _patterns;

	//------------------------------------------------------------
	// Factories
	//------------------------------------------------------------
	
	/**
	 * 一意文字列キーから <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * <p>
	 * このコンストラクタでは、全ての基底キーが省略可能であり、使用禁止文字を含まない
	 * 任意の文字列を指定可能である。
	 * <p>
	 * このメソッドは、{@link dtalge.util.DtConditions#isCachedBaseEnabled()} メソッドが
	 *  <tt>true</tt> を返すとき、{@link dtalge.DtBasePattern#equals(Object)} メソッドによって
	 * 新規に生成されたインスタンスと等しいと判断される基底パターンがキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * 新しいインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に新しいインスタンスを返す。
	 * 
	 * @param oneStringKey	データ代数基底の一意文字列キー
	 * @return	指定した引数で生成された基底パターン
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	static public DtBasePattern newPattern(String oneStringKey) {
		return newPattern(oneStringKey.split(DELIMITOR));
	}
	
	/**
	 * 指定されたキーを持つ <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * <p>
	 * このコンストラクタでは、全ての基底キーが省略可能であり、使用禁止文字を含まない
	 * 任意の文字列を指定可能である。
	 * <p>
	 * このメソッドは、{@link dtalge.util.DtConditions#isCachedBaseEnabled()} メソッドが
	 *  <tt>true</tt> を返すとき、{@link dtalge.DtBasePattern#equals(Object)} メソッドによって
	 * 新規に生成されたインスタンスと等しいと判断される基底パターンがキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * 新しいインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に新しいインスタンスを返す。
	 * 
	 * @param nameKey	名前キー
	 * @param typeKey	データ型キー
	 * @return	指定した引数で生成された基底パターン
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	static public DtBasePattern newPattern(String nameKey, String typeKey) {
		return newPattern(new String[]{nameKey, typeKey});
	}
	
	/**
	 * 指定されたキーを持つ <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * <p>
	 * このコンストラクタでは、全ての基底キーが省略可能であり、使用禁止文字を含まない
	 * 任意の文字列を指定可能である。
	 * <p>
	 * このメソッドは、{@link dtalge.util.DtConditions#isCachedBaseEnabled()} メソッドが
	 *  <tt>true</tt> を返すとき、{@link dtalge.DtBasePattern#equals(Object)} メソッドによって
	 * 新規に生成されたインスタンスと等しいと判断される基底パターンがキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * 新しいインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に新しいインスタンスを返す。
	 * 
	 * @param nameKey	名前キー
	 * @param typeKey	データ型キー
	 * @param attrKey	単位キー
	 * @return	指定した引数で生成された基底パターン
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	static public DtBasePattern newPattern(String nameKey, String typeKey, String attrKey) {
		return newPattern(new String[]{nameKey, typeKey, attrKey});
	}
	
	/**
	 * 指定されたキーを持つ <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * <p>
	 * このコンストラクタでは、全ての基底キーが省略可能であり、使用禁止文字を含まない
	 * 任意の文字列を指定可能である。
	 * <p>
	 * このメソッドは、{@link dtalge.util.DtConditions#isCachedBaseEnabled()} メソッドが
	 *  <tt>true</tt> を返すとき、{@link dtalge.DtBasePattern#equals(Object)} メソッドによって
	 * 新規に生成されたインスタンスと等しいと判断される基底パターンがキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * 新しいインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に新しいインスタンスを返す。
	 * 
	 * @param nameKey	名前キー
	 * @param typeKey	データ型キー
	 * @param attrKey	単位キー
	 * @param subjectKey	主体キー
	 * @return	指定した引数で生成された基底パターン
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	static public DtBasePattern newPattern(String nameKey, String typeKey, String attrKey, String subjectKey) {
		return newPattern(new String[]{nameKey, typeKey, attrKey, subjectKey});
	}
	
	/**
	 * 指定された基底キー配列から <code>DtBasePattern</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * <p>
	 * このコンストラクタでは、全ての基底キーが省略可能であり、使用禁止文字を含まない
	 * 任意の文字列を指定可能である。
	 * <p>
	 * このメソッドは、{@link dtalge.util.DtConditions#isCachedBaseEnabled()} メソッドが
	 *  <tt>true</tt> を返すとき、{@link dtalge.DtBasePattern#equals(Object)} メソッドによって
	 * 新規に生成されたインスタンスと等しいと判断される基底パターンがキャッシュにすでに
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
	 * @return	指定した引数で生成された基底パターン
     * 
     * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	static public DtBasePattern newPattern(String[] baseKeys) {
		DtBasePattern targetPattern = new DtBasePattern(false, Validations.validNotNull(baseKeys));
		DtBasePattern cachedPattern = CacheManager.cacheDtBasePattern(targetPattern);
		if (targetPattern == cachedPattern) {
			// 同一インスタンスなら新規キャッシュのため、正規表現パターンを更新
			cachedPattern.updatePatterns();
		}
		return cachedPattern;
	}
	
	/**
	 * 指定された基底のキーを持つ <code>DtBasePattern</code> の新しいインスタンスを生成する。
	 * 基本的に各基底キーの文字列をそのまま継承する。基底キーに含まれるアスタリスク文字は
	 * ワイルドカードとみなされる。
	 * <p>
	 * このメソッドは、{@link dtalge.util.DtConditions#isCachedBaseEnabled()} メソッドが
	 *  <tt>true</tt> を返すとき、{@link dtalge.DtBasePattern#equals(Object)} メソッドによって
	 * 新規に生成されたインスタンスと等しいと判断される基底パターンがキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * 新しいインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に新しいインスタンスを返す。
	 * 
	 * @param base			データ代数基底
	 * @param ignoreTypeKey	データ型キーをワイルドカードとする場合は <tt>true</tt> を指定する
	 * @return	指定した引数で生成された基底パターン
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws IllegalArgumentException 次の場合、この例外がスローされる。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	static public DtBasePattern newPattern(DtBase base, boolean ignoreTypeKey) {
		if (ignoreTypeKey)
			return newPattern(base.getNameKey(), WILDCARD, base.getAttributeKey(), base.getSubjectKey());
		else
			return newPattern(base._baseKeys);
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 一意文字列キーから <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * <p>
	 * このコンストラクタでは、全ての基底キーが省略可能であり、使用禁止文字を含まない
	 * 任意の文字列を指定可能である。
	 * 
	 * @param oneStringKey	データ代数基底の一意文字列キー
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	public DtBasePattern(String oneStringKey) {
		this(true, oneStringKey.split(DELIMITOR));
	}

	/**
	 * 指定されたキーを持つ <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * <p>
	 * このコンストラクタでは、全ての基底キーが省略可能であり、使用禁止文字を含まない
	 * 任意の文字列を指定可能である。
	 * 
	 * @param nameKey	名前キー
	 * @param typeKey	データ型キー
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	public DtBasePattern(String nameKey, String typeKey) {
		this(true, nameKey, typeKey);
	}

	/**
	 * 指定されたキーを持つ <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * <p>
	 * このコンストラクタでは、全ての基底キーが省略可能であり、使用禁止文字を含まない
	 * 任意の文字列を指定可能である。
	 * 
	 * @param nameKey	名前キー
	 * @param typeKey	データ型キー
	 * @param attrKey	単位キー
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	public DtBasePattern(String nameKey, String typeKey, String attrKey) {
		this(true, nameKey, typeKey, attrKey);
	}

	/**
	 * 指定されたキーを持つ <code>DtBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * <p>
	 * このコンストラクタでは、全ての基底キーが省略可能であり、使用禁止文字を含まない
	 * 任意の文字列を指定可能である。
	 * 
	 * @param nameKey	名前キー
	 * @param typeKey	データ型キー
	 * @param attrKey	単位キー
	 * @param subjectKey	主体キー
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	public DtBasePattern(String nameKey, String typeKey, String attrKey, String subjectKey) {
		this(true, nameKey, typeKey, attrKey, subjectKey);
	}

	/**
	 * 指定された基底キー配列から <code>DtBasePattern</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * <p>
	 * このコンストラクタでは、全ての基底キーが省略可能であり、使用禁止文字を含まない
	 * 任意の文字列を指定可能である。
	 * 
	 * @param baseKeys 基底キー配列。この配列内のキーは、次の順序で指定されているものとする。
	 * <ol type="1" start="0">
	 * <li>名前キー(name key)
	 * <li>データ型キー(type key)
	 * <li>属性キー(attribute key)
	 * <li>主体キー(subject key)
	 * </ol>
     * 
     * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	public DtBasePattern(String[] baseKeys) {
		this(true, Validations.validNotNull(baseKeys));
	}

	/**
	 * 指定された基底のキーを持つ <code>DtBasePattern</code> の新しいインスタンスを生成する。
	 * 基本的に各基底キーの文字列をそのまま継承する。基底キーに含まれるアスタリスク文字は
	 * ワイルドカードとみなされる。
	 * 
	 * @param base			データ代数基底
	 * @param ignoreTypeKey	データ型キーをワイルドカードとする場合は <tt>true</tt> を指定する
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws IllegalArgumentException 次の場合、この例外がスローされる。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	public DtBasePattern(DtBase base, boolean ignoreTypeKey) {
		this(true, base.getNameKey(), (ignoreTypeKey ? WILDCARD : base.getTypeKey()),
				base.getAttributeKey(), base.getSubjectKey());
	}

	/**
	 * 指定された基底キー配列から <code>DtBasePattern</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。なお、基底キーの省略記号は、
	 * ワイルドカードには変換されない。
	 * <p>
	 * このコンストラクタでは、全ての基底キーが省略可能であり、使用禁止文字を含まない
	 * 任意の文字列を指定可能である。
	 * 
	 * @param withUpdatePatterns	マッチングパターンも同時に更新する場合は <tt>true</tt> を指定する。
	 * @param baseKeys 基底キー配列。この配列内のキーは、次の順序で指定されているものとする。
	 * <ol type="1" start="0">
	 * <li>名前キー(name key)
	 * <li>データ型キー(type key)
	 * <li>属性キー(attribute key)
	 * <li>主体キー(subject key)
	 * </ol>
     * 
     * @throws NullPointerException	<code>baseKeys</code> が <tt>null</tt> の場合
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>基底キー文字列に使用禁止文字が含まれている場合
	 * </ul>
	 */
	private DtBasePattern(boolean withUpdatePatterns, String...baseKeys) {
		int index = 0;

		// 指定された基底キーを代入する
		if (baseKeys.length > 0) {
			// 基底キー指定あり
			int maxKeys = Math.min(baseKeys.length, NUM_ALL_KEYS);
			for ( ; index < maxKeys; index++) {
				if (!Strings.isNullOrEmpty(baseKeys[index])) {
					// 基底キーとして無効な文字が含まれている場合は、エラーとする
					if (!Util.validBaseKeyString(baseKeys[index])) {
						String msg = "Illegal character is included in " + getKeyName(index) + ".";
						throw new IllegalArgumentException(msg);
					}
					// 基底キーを代入する
					if (WILDCARD.equals(baseKeys[index])) {
						_baseKeys[index] = WILDCARD;
					} else {
						_baseKeys[index] = CacheManager.cacheString(baseKeys[index]);
					}
				} else {
					// ワイルドカードを代入する
					_baseKeys[index] = WILDCARD;
				}
			}
		}
		
		// 未指定の基底キーにワイルドカードを代入する
		for ( ; index < NUM_ALL_KEYS; index++) {
			_baseKeys[index] = WILDCARD;
		}
		
		// update hashCode
		updateHashCode();
		
		// update patterns
		if (withUpdatePatterns) {
			updatePatterns();
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定の基底がこの基底パターンにマッチするかどうかを判定する。
	 * 
	 * @param base 一致を判定するデータ代数基底
	 * 
	 * @return このパターンと指定の基底が一致するときだけ true を返す
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public boolean matches(DtBase base) {
		return matchesByPattern(_patterns, base._baseKeys);
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
	public int compareTo(DtBasePattern base) {
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
	public int compareToIgnoreCase(DtBasePattern base) {
		return super.baseCompareToIgnoreCase(base);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/*
	 * @deprecated 正規表現を使用しないため、このメソッドは削除。
	 * 
	 * 指定された文字列を、ワイルドカードを含む正規表現マッチングパターン文字列に変換する。
	 * <p>
	 * このメソッドでは、基底のワイルドカード文字('*')を、正規表現任意文字(&quot;.*&quot;)に
	 * 変換する。また、エスケープ終了記号(&quot;\E&quot;)と同一の文字パターンが含まれている
	 * 場合には、適切なエスケープ処理を施す。<br>
	 * 通常の文字は、正規表現のエスケープ記号(&quot;\Q&quot; と &quot;\E&quot;)で囲む。
	 * <b>注:</b>このメソッドでは、入力文字列が１文字以上であることを前提とする。
	 * 
	 * @param strpattern	変換する文字列
	 * @return				正規表現パターン文字列
	 *
	private String convertWildcardToRegexpPattern(String strpattern) {
		int len = strpattern.length();
		StringBuffer sb = new StringBuffer(len * 3);
		sb.append("\\A\\Q");	// 入力の先頭を示す
		
		int index = 0;
		while (index < len) {
			char c = strpattern.charAt(index);
			if ('*' == c) {
				// wildcard
				sb.append("\\E.*\\Q");
				//--- 連続したワイルドカードはスキップ
				for (++index; (index < len) && (strpattern.charAt(index) == '*'); index++) ;
			}
			else if ('\\' == c) {
				// \E 文字セット?
				index++;
				if ((index < len) && (strpattern.charAt(index) == 'E')) {
					// 正規表現[\E]パターン
					sb.append("\\E\\\\E\\Q");
					index++;
				} else {
					sb.append(c);
				}
			}
			else {
				sb.append(c);
				index++;
			}
		}
		
		sb.append("\\E\\z");
		return sb.toString();
	}
	*/

	/**
	 * 内部基底キー文字列から、正規表現マッチングパターンを更新する。
	 * <p>
	 * 正規表現マッチングパターンは、patterns[]配列に格納されるが、
	 * 次の条件によって、格納されるオブジェクトが異なる。
	 * <p>
	 * <dl>
	 * <dt><b>[基底キー文字列がワイルドカードのみの場合]</b></dt>
	 * <dd>全ての文字にマッチするものとみなし、<tt>null</tt> を代入する。</dd>
	 * <dt><b>[ワイルドカードを含まない場合]</b></dt>
	 * <dd>基底キー文字列の String オブジェクトへの参照を代入する。</dd>
	 * <dt><b>[ワイルドカードを含む場合]</b></dt>
	 * <dd>正規表現パターンでコンパイル済みの Pattern オブジェクトへの参照を代入する。</dd>
	 * </dl>
	 * <p><b>注:</b>このメソッドは、_baseKey[] 配列が正しく設定されており、
	 * 全ての基底キー文字列に１文字以上の文字列が格納されていることを前提とする。
	 */
	private void updatePatterns() {
		this._patterns = makePattern(_baseKeys);
	}

	/**
	 * 基底キーの配列から、基底パターンの内部形式を生成する。
	 * この内部形式の基底パターンは、<code>PatternItem</code> の配列であり、
	 * 基底キーがワイルドカードのみのものはこのパターンに含まれない。
	 * また、全ての基底キーがワイルドカードの場合は <tt>null</tt> となる。
	 * @param baseKeys	基底キー配列(整形済みのもの)
	 * @return	内部形式の基底パターン
	 * 
	 * @since 0.20
	 */
	static protected PatternItem[] makePattern(String[] baseKeys) {
		ArrayList<PatternItem> items = new ArrayList<PatternItem>(baseKeys.length);
		
		for (int i = 0; i < baseKeys.length; i++) {
			PatternItem item = new PatternItem(i, baseKeys[i]);
			if (item.getPattern() != null) {
				items.add(item);
			}
		}
		
		PatternItem[] patterns = null;
		if (!items.isEmpty()) {
			patterns = items.toArray(new PatternItem[items.size()]);
			Arrays.sort(patterns);
		}
		return patterns;
	}

	/**
	 * 指定された基底パターンに、指定された基底キー配列が一致するかを判定する。
	 * @param patterns	内部形式の基底パターン
	 * @param baseKeys	基底キー配列(整形済みのもの)
	 * @return	一致した場合は <tt>true</tt>
	 * 
	 * @since 0.20
	 */
	static protected boolean matchesByPattern(PatternItem[] patterns, String[] baseKeys) {
		if (patterns != null) {
			for (PatternItem item : patterns) {
				int index = item.getKeyIndex();
				Object obj = item.getPattern();
				if (obj instanceof String) {
					if (!baseKeys[index].equals(obj)) {
						//--- not matched!
						return false;
					}
				}
				else if (obj instanceof WildcardPattern) {
					if (!((WildcardPattern)obj).matches(baseKeys[index])) {
						//--- not matched!
						return false;
					}
				}
			}
		}
		
		//--- matched!
		return true;
	}

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
	protected void writeFieldToCSV(CsvWriter writer)
		throws IOException
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
	 * 指定された <code>reader</code> の現在位置から、基底パターンデータを取得する。
	 * <br>
	 * 各基底キーは、現在のカラム位置から次の順序で読み込まれる。
	 * <ol type="1" start="1">
	 * <li>名前キー(name key)
	 * <li>データ型キー(type key)
	 * <li>属性キー(attribute key)
	 * <li>主体キー(subject key)
	 * </ol>
	 * <p>
	 * 読み込みにおいて、全ての基底キーは省略可能であり、
	 * 省略されている場合は省略記号({@link #WILDCARD})に置き換えられる。
	 * また、各キーに使用禁止文字が含まれている場合はエラーとなる。
	 * 
	 * @param reader 入力元となる <code>CsvReader.CsvFieldReader</code> オブジェクト
	 * 
	 * @return 生成された <code>DtBasePattern</code> の新しいインスタンスを返す。
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws IOException 読み込み時エラーが発生したときにスローされる
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 */
	static protected DtBasePattern readFieldFromCSV(CsvReader.CsvFieldReader reader)
		throws IOException, CsvFormatException
	{
		// name key
		String nameKey = reader.readValue();
		if (Strings.isNullOrEmpty(nameKey)) {
			nameKey = DtBasePattern.WILDCARD;
		}
		else if (!Util.validBaseKeyString(nameKey)) {
			throw new CsvFormatException("Illegal name key : " + nameKey,
											reader.getLineNo(), reader.getNextPosition());
		}
		
		// type key
		String typeKey = reader.readValue();
		if (Strings.isNullOrEmpty(typeKey)) {
			typeKey = DtBasePattern.WILDCARD;
		}
		else if (!Util.validBaseKeyString(typeKey)) {
			//--- 使用禁止文字が含まれている場合はエラー
			throw new CsvFormatException("Illegal type key : " + typeKey,
					reader.getLineNo(), reader.getNextPosition());
		}
		
		// attr key
		String attrKey = reader.readValue();
		if (Strings.isNullOrEmpty(attrKey)) {
			attrKey = DtBasePattern.WILDCARD;
		}
		else if (!Util.validBaseKeyString(attrKey)) {
			throw new CsvFormatException("Illegal attribute key : " + attrKey,
					reader.getLineNo(), reader.getNextPosition());
		}
		
		// subject key
		String subjectKey = reader.readValue();
		if (Strings.isNullOrEmpty(subjectKey)) {
			subjectKey = DtBasePattern.WILDCARD;
		}
		else if (!Util.validBaseKeyString(subjectKey)) {
			throw new CsvFormatException("Illegal subject key : " + subjectKey,
					reader.getLineNo(), reader.getNextPosition());
		}
		
		// make instance
		return DtBasePattern.newPattern(nameKey, typeKey, attrKey, subjectKey);
	}
	
	/**
	 * XMLファイルフォーマットでのデータ代数基底パターンのルートノード名
	 */
	static protected final String XML_ELEMENT_NAME = "Dtbase";
	
	/**
	 * 基底パターンデータを、XML ノードに変換する。
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
	 * 指定された XML 属性から、基底パターンデータを生成する。
	 * 属性の解析において、全ての基底キーは省略可能であり、
	 * 省略されている場合は、省略記号({@link #WILDCARD})に置き換えられる。
	 * また、各キーに使用禁止文字が含まれている場合はエラーとなる。
	 * 
	 * @param locator		SAX の読み込み位置を保持する <code>Locator</code> オブジェクト
	 * @param qName			前置修飾子を持つ修飾名。修飾名を使用できない場合は空文字列
	 * @param attributes	要素の属性。属性がない場合は空の <code>Attributes</code> オブジェクト
	 * 						になる。 
	 * 
	 * @return 生成された <code>DtBasePattern</code> の新しいインスタンスを返す。
	 * 
	 * @throws SAXException	SAX 例外。ほかの例外をラップしている可能性がある
	 */
	static protected DtBasePattern decodeFromXmlAttributes(Locator locator, String qName, Attributes attributes)
		throws SAXParseException
	{
		// name key
		String nameKey = attributes.getValue(XML_ATTR_NAMEKEY);
		if (Strings.isNullOrEmpty(nameKey)) {
			nameKey = DtBasePattern.WILDCARD;
		}
		else if (!Util.validBaseKeyString(nameKey)) {
			XmlErrors.errorParseException(locator, "Illegal name key : \"%s\"", String.valueOf(nameKey));
		}
		
		// type key
		String typeKey = attributes.getValue(XML_ATTR_TYPEKEY);
		if (Strings.isNullOrEmpty(typeKey)) {
			typeKey = DtBasePattern.WILDCARD;
		}
		else if (!Util.validBaseKeyString(typeKey)) {
			//--- 使用禁止文字が含まれている場合はエラー
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
		return DtBasePattern.newPattern(nameKey, typeKey, attrKey, subjectKey);
	}

	/**
	 * データ代数基底パターンのマッチングパターン要素を保持するクラス。
	 * <p>
	 * このクラスでは、パターンを適用する基底キーの位置(インデックス)と
	 * パターンとなるオブジェクトを保持する。
	 * パターンとなるオブジェクトは、次の条件によって、格納されるオブジェクトが異なる。
	 * <p>
	 * <dl>
	 * <dt><b>[基底キー文字列がワイルドカードのみの場合]</b></dt>
	 * <dd>全ての文字にマッチするものとみなし、<tt>null</tt> を代入する。</dd>
	 * <dt><b>[ワイルドカードを含まない場合]</b></dt>
	 * <dd>基底キー文字列の String オブジェクトへの参照を代入する。</dd>
	 * <dt><b>[ワイルドカードを含む場合]</b></dt>
	 * <dd>正規表現パターンでコンパイル済みの Pattern オブジェクトへの参照を代入する。</dd>
	 * </dl>
	 * また、マッチングの順序を明確にするため、次のような順序付けを行う。
	 * <blockquote>
	 * [文字列] < [正規表現パターン] < [ワイルドカードのみ]
	 * </blockquote>
	 * 
	 * @version 0.20	2010/02/25
	 * 
	 * @author SOARS Project.
	 * @author Y.Ishizuka(PieCake.inc,)
	 * 
	 * @since 0.10
	 */
	static protected final class PatternItem implements Comparable<PatternItem> {
		/**
		 * 固定文字列のみのパターンを示すID
		 */
		static public final int PATTERN_FIXED = (-1);
		/**
		 * ワイルドカードと固定文字列の混在パターンを示すID
		 */
		static public final int PATTERN_MIXED = (0);
		/**
		 * ワイルドカードのみのパターンを示すID
		 */
		static public final int PATTERN_WILDCARD = (1);

		/**
		 * ターゲットの基底キーインデックス
		 */
		private final int keyIndex;
		/**
		 * マッチングパターン
		 */
		private final Object pattern;
		
		/**
		 * 指定されたキーインデックスと基底キーパターンを保持する <code>PatternItem</code> の新しいインスタンスを生成する。
		 * このコンストラクタは、<code>key</code> に指定された文字列からマッチングパターンを生成する。
		 * 
		 * @param keyIndex	基底キーのインデックス
		 * @param key		基底キーの文字列
		 */
		public PatternItem(int keyIndex, String key) {
			this.keyIndex = keyIndex;
			this.pattern = WildcardPattern.parsePattern(key);
		}
		
		/**
		 * 基底キーインデックスを取得する。
		 * @return	基底キーインデックス
		 */
		public int getKeyIndex() {
			return keyIndex;
		}
		
		/**
		 * マッチングパターンを取得する。
		 * @return	マッチングパターン
		 */
		public Object getPattern() {
			return pattern;
		}

		/**
		 * パターンの種類を示すIDを取得する。
		 * @return	パターンID
		 */
		public int getPatternID() {
			if (pattern instanceof String) {
				return PATTERN_FIXED;
			}
			else if (pattern instanceof WildcardPattern) {
				return PATTERN_MIXED;
			}
			else {
				return PATTERN_WILDCARD;
			}
		}

		/**
		 * パターンの種別を比較する。
		 * このメソッドは、パターンのIDによって順位付けするためのメソッドとなる。
		 * パターンIDの大小関係は、次のようになる。
		 * <blockquote>
		 * {@link #PATTERN_FIXED} &lt; {@link #PATTERN_MIXED} &lt; {@link #PATTERN_WILDCARD}
		 * </blockquote>
		 */
		public int compareTo(PatternItem o) {
			int thisType = getPatternID();
			int otherType = o.getPatternID();
			
			if (thisType < otherType) {
				return -1;
			}
			else if (thisType > otherType) {
				return 1;
			}
			else {
				if (this.keyIndex < o.keyIndex)
					return -1;
				else if (this.keyIndex > o.keyIndex)
					return 1;
				else
					return 0;
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof PatternItem) {
				if (((PatternItem)obj).keyIndex == this.keyIndex) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return keyIndex;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getName());
			sb.append("@[");
			sb.append(keyIndex);
			sb.append(",");
			sb.append(pattern);
			sb.append("]");
			return sb.toString();
		}
	}

	/**
	 * アスタリスク(<code>*</code>)を0文字以上の任意文字列にマッチするワイルドカードと
	 * みなし、ワイルドカードを含むパターンとのマッチングを行うクラス。
	 * 
	 * @version 0.20	2010/02/25
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 *
	 * @since 0.20
	 */
	static protected final class WildcardPattern
	{
		/**
		 * 先頭のマッチングパターン
		 */
		private final String startsPattern;
		/**
		 * 終端のマッチングパターン
		 */
		private final String endsPattern;
		/**
		 * 中間のマッチングパターン
		 */
		private final String[] midPatterns;

		/**
		 * 指定された文字列からマッチングパターンを生成する。
		 * このメソッドでは、ワイルドカードを含まない固定文字列、
		 * ワイルドカードのみのパターン、ワイルドカードを含む文字列パターンとを
		 * 分類する。固定文字列なら <code>String</code> インスタンス、
		 * ワイルドカードを含む文字列パターンなら <code>WildcardPattern</code> インスタンス、
		 * ワイルドカードのみなら <tt>null</tt> を返す。
		 * @param pattern	パターン文字列
		 * @return	パターンの種別に応じたインスタンスを返す。
		 * @throws NullPointerException	引数が <tt>null</tt> の場合
		 */
		static public Object parsePattern(String pattern) {
			// ワイルドカードを検索
			int pos = pattern.indexOf(DtBasePattern.WILDCARD_CHAR);
			if (pos < 0) {
				// ワイルドカードは含まれていない
				return pattern;
			}
			
			// パターン生成
			WildcardPattern wpat = new WildcardPattern(pos, pattern);
			if (wpat.isWildcardOnly()) {
				//--- ワイルドカードのみのパターン
				return null;
			} else {
				//--- ワイルドカードを含む文字列パターン
				return wpat;
			}
		}

		/**
		 * 指定された文字列からマッチングパターンを生成する。
		 * @param firstWildcardPos	先頭のワイルドカード文字の位置
		 * @param pattern	ワイルドカードを含むマッチングパターン
		 */
		protected WildcardPattern(int firstWildcardPos, String pattern) {
			// 終端のワイルドカード文字位置を検索
			int lastWildcardPos = pattern.lastIndexOf(DtBasePattern.WILDCARD_CHAR);
			
			// 先頭固定文字列を保存
			if (firstWildcardPos > 0) {
				startsPattern = pattern.substring(0, firstWildcardPos);
			} else {
				startsPattern = null;
			}
			
			// 終端固定文字列を保存
			if (lastWildcardPos >= 0 && lastWildcardPos < (pattern.length()-1)) {
				endsPattern = pattern.substring(lastWildcardPos+1);
			} else {
				endsPattern = null;
			}
			
			// 中間固定文字列を保存
			if ((lastWildcardPos - firstWildcardPos) > 1) {
				String mid = pattern.substring(firstWildcardPos+1, lastWildcardPos);
				StringTokenizer st = new StringTokenizer(mid, DtBasePattern.WILDCARD);
				int num = st.countTokens();
				if (num > 0) {
					String[] tokens = new String[num];
					int i = 0;
					while (st.hasMoreTokens()) {
						tokens[i++] = st.nextToken();
					}
					midPatterns = tokens;
				} else {
					//--- 中間固定文字列は存在しない
					midPatterns = null;
				}
			} else {
				//--- 中間固定文字列は存在しない
				midPatterns = null;
			}
		}

		/**
		 * このパターンがワイルドカードのみのパターンかを判定する。
		 * @return	ワイルドカードのみのパターンなら <tt>true</tt>
		 */
		public boolean isWildcardOnly() {
			return (startsPattern==null && endsPattern==null && midPatterns==null);
		}

		/**
		 * 指定された文字列が、このパターンと一致するかを判定する。
		 * @param str	判定する文字列
		 * @return	パターンと一致した場合は <tt>true</tt>
		 */
		public boolean matches(String str) {
			// matches starts
			if (startsPattern != null) {
				if (!str.startsWith(startsPattern)) {
					//--- not matched!
					return false;
				}
				str = str.substring(startsPattern.length());
			}
			
			// matches ends
			if (endsPattern != null) {
				if (!str.endsWith(endsPattern)) {
					//--- not matched!
					return false;
				}
				str = str.substring(0, (str.length() - endsPattern.length()));
			}
			
			// matches middle patterns
			if (midPatterns != null) {
				for (String mid : midPatterns) {
					int findPos = str.indexOf(mid);
					if (findPos < 0) {
						//--- not matched!
						return false;
					}
					str = str.substring(findPos + mid.length());
				}
			}
			
			// pattern matched
			return true;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getName());
			sb.append("@[");
			sb.append(startsPattern);
			sb.append(",");
			if (midPatterns != null)
				sb.append(Arrays.toString(midPatterns));
			else
				sb.append("null");
			sb.append(",");
			sb.append(endsPattern);
			sb.append("]");
			return sb.toString();
		}
	}

	/**
	 * <code>{@link DtBasePattern}</code> クラスに関する補助機能を提供するユーティリティクラス。
	 * 
	 * @version 0.10	2008/08/25
	 * 
	 * @author SOARS Project.
	 * @author Y.Ishizuka(PieCake.inc,)
	 * 
	 * @since 0.10
	 */
	static public class Util
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
		 * <li>入力文字列が空文字列(長さ 0、もしくは <tt>null</tt>)なら、
		 * ワイルドカード({@link DtBasePattern#WILDCARD})とする
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
					// 省略はWILDCARD
					return DtBasePattern.WILDCARD;
				}
			}
			
			String result = null;
			if (input.length() > 0) {
				if (validBaseKeyString(input)) {
					result = input;
				}
			} else if (isAllowedOmitted) {
				// 省略は WILDCARD
				result = DtBasePattern.WILDCARD;
			}
			
			return result;
		}
	}
}
