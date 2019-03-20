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
 * @(#)ExBase.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBase.java	0.970	2009/03/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBase.java	0.960	2009/02/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBase.java	0.94	2008/06/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBase.java	0.93	2007/09/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBase.java	0.92	2007/08/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBase.java	0.91	2007/08/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBase.java	0.90	2007/07/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBase.java	0.10	2004/12/13
 *     - created by Ibuki(Tokyo institute)
 */

package exalge2;

import java.io.IOException;
import java.util.regex.Pattern;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import exalge2.io.csv.CsvFormatException;
import exalge2.io.csv.CsvReader;
import exalge2.io.csv.CsvRecordReader;
import exalge2.io.csv.CsvWriter;
import exalge2.io.xml.XmlDocument;
import exalge2.io.xml.XmlDomParseException;
import exalge2.util.Strings;

/**
 * 交換代数の１基底を保持する交換代数基底クラス。
 * 
 * <p>このクラスは、不変オブジェクト(Immutable)である。
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
 * 基底キーの順序通りに連結されたものを指す。<br>
 * また、交換代数基底クラスのハッシュ値は、全基底キーが同一であれば、ハッシュ値を
 * 同一であることを保障する。
 * 
 * <p>このクラスにおいては、基底キーは任意の文字列であり、特殊な意味を持つ記号などを
 * 判別する機能は派生クラスにて実装する。
 * 省略されている拡張基底キーは省略記号<code>('#')</code>で表される。
 * 一意文字列キーにおける交換代数基底キーの各キーの順序は、交換代数基底内で保持される
 * 各規定キーの順序の通りでなければならない。
 * 
 * <p>このクラスでは、拡張基底キーの指定において、キーが省略(<tt>null</tt> もしくは、
 * 長さ 0 の文字列が指定)された場合、省略記号文字({@link #OMITTED})を代入する。
 * <br>
 * 基本基底キー(「名前キー」ならびに「ハットキー」)は省略できない。
 * また、ハットキーに指定できる値は、次のもののみとなる。
 * <ul>
 * <li>{@link #NO_HAT} - 交換代数基底が<b>hat無し基底</b>であるこを示す
 * <li>{@link #HAT} - 交換代数基底が<b>hat(^)基底</b>であることを示す
 * </ul>
 * ハットキー以外の基底キーには、次の文字は使用できない。
 * <blockquote>
 * &lt; &gt; - , ^ &quot; % &amp; ? | @ ' " (空白)
 * </blockquote>
 * 
 * @version 0.982	2009/09/13
 * 
 * @author H.Debuchi(SOARS Project.)
 * @author Li Hou(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public final class ExBase extends AbExBase implements Comparable<ExBase>
{
	/**
	 * 交換代数基底が <b>hat(^)基底</b> であることを示す基底キー。
	 */
	static public final String HAT= "HAT";

	/**
	 * 交換代数基底が <b>hatなし基底</b> であることを示す基底キー。
	 */
	static public final String NO_HAT= "NO_HAT";

	/**
	 * 拡張基底キーの省略記号文字
	 */
	static public final String OMITTED = "#";

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 一意文字列キーから <code>ExBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された拡張基底キーには、省略記号が代入される。
	 * 
	 * @param oneStringKey	交換代数基底の一意文字列キー
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>ハットキーが、{@link #NO_HAT} もしくは {@link #HAT} 以外の場合
	 * </ul>
	 */
	public ExBase(String oneStringKey) {
		super();
		String[] keys = oneStringKey.split(DELIMITOR);

		initialize(keys);
	}

	/**
	 * 指定された基底キー配列から <code>ExBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された拡張基底キーには、省略記号が代入される。
	 * 
	 * @param baseKeys 基底キー配列。この配列内のキーは、次の順序で指定されているものとする。
	 * <ol type="1" start="0">
	 * <li>名前キー(name key)
	 * <li>ハットキー(hat key)
	 * <li>単位キー(unit key)
	 * <li>時間キー(time key)
	 * <li>サブジェクトキー(subject key)
	 * </ol>
     * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>ハットキーが、{@link #NO_HAT} もしくは {@link #HAT} 以外の場合
	 * </ul>
	 * 
	 * @since 0.90
	 */
	public ExBase(String[] baseKeys) {
		super();
		// initialize
		initialize(baseKeys);
	}

	/**
	 * 指定された名前キー、ハットキー、拡張基底キーから <code>ExBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された拡張基底キーには、省略記号が代入される。
	 * 
	 * @param nameKey	交換代数基底の名前キー
	 * @param hatKey	交換代数基底のハットキー。指定可能な値は、{@link #NO_HAT} もしくは {@link #HAT} のみ。
	 * @param extendedKeys	拡張基底キー配列。この配列内のキーは、次の順序で指定されているものとする。
	 * <ol type="1" start="0">
	 * <li>単位キー(unit key)
	 * <li>時間キー(time key)
	 * <li>サブジェクトキー(subject key)
	 * </ol>
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>ハットキーが、{@link #NO_HAT} もしくは {@link #HAT} 以外の場合
	 * </ul>
	 * 
	 * @since 0.90
	 */
	public ExBase(String nameKey, String hatKey, String[] extendedKeys) {
		super();
		// create parameter
		int len = NUM_BASIC_KEYS + (extendedKeys != null ? extendedKeys.length : 0);
		String[] keys = new String[len];
		keys[KEY_NAME] = nameKey;
		keys[KEY_HAT]  = hatKey;
		for (int i = 0; i < extendedKeys.length; i++) {
			keys[NUM_BASIC_KEYS + i] = extendedKeys[i];
		}
		// initialize
		initialize(keys);
	}

	/**
	 * 指定された名前キー、ハットキーから <code>ExBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された拡張基底キーには、省略記号が代入される。
	 * 
	 * @param nameKey	名前キー
	 * @param hatKey	交換代数基底のハットキー。指定可能な値は、{@link #NO_HAT} もしくは {@link #HAT} のみ。
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>ハットキーが、{@link #NO_HAT} もしくは {@link #HAT} 以外の場合
	 * </ul>
	 */
	public ExBase(String nameKey, String hatKey) {
		super();
		initialize(new String[]{nameKey, hatKey});
	}

	/**
	 * 指定されたキーを持つ <code>ExBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された拡張基底キーには、省略記号が代入される。
	 * 
	 * @param nameKey	名前キー
	 * @param hatKey	交換代数基底のハットキー。指定可能な値は、{@link #NO_HAT} もしくは {@link #HAT} のみ。
	 * @param unitKey	単位キー
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>ハットキーが、{@link #NO_HAT} もしくは {@link #HAT} 以外の場合
	 * </ul>
	 */
	public ExBase(String nameKey, String hatKey, String unitKey) {
		super();
		initialize(new String[]{nameKey, hatKey, unitKey});
	}

	/**
	 * 指定されたキーを持つ <code>ExBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された拡張基底キーには、省略記号が代入される。
	 * 
	 * @param nameKey	名前キー
	 * @param hatKey	交換代数基底のハットキー。指定可能な値は、{@link #NO_HAT} もしくは {@link #HAT} のみ。
	 * @param unitKey	単位キー
	 * @param timeKey	時間キー
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>ハットキーが、{@link #NO_HAT} もしくは {@link #HAT} 以外の場合
	 * </ul>
	 */
	public ExBase(String nameKey, String hatKey, String unitKey, String timeKey) {
		super();
		initialize(new String[]{nameKey, hatKey, unitKey, timeKey});
	}

	/**
	 * 指定されたキーを持つ <code>ExBase</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された拡張基底キーには、省略記号が代入される。
	 * 
	 * @param nameKey	名前キー
	 * @param hatKey	交換代数基底のハットキー。指定可能な値は、{@link #NO_HAT} もしくは {@link #HAT} のみ。
	 * @param unitKey	単位キー
	 * @param timeKey	時間キー
	 * @param subjectKey	サブジェクトキー
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>ハットキーが、{@link #NO_HAT} もしくは {@link #HAT} 以外の場合
	 * </ul>
	 * 
	 * @since 0.90
	 */
	public ExBase(String nameKey, String hatKey, String unitKey, String timeKey, String subjectKey) {
		super();
		initialize(new String[]{nameKey, hatKey, unitKey, timeKey, subjectKey});
	}

	/**
	 * コピーコンストラクタ
	 * 
	 * @param src コピー元の <code>ExBase</code> インスタンス
	 * 
	 * @throws NullPointerException 引数に <tt>null</tt> が指定された場合にスローされる
	 * 
	 * @since 0.90
	 */
	protected ExBase(ExBase src) {
		super();
		this.copyFrom(src);
	}

	/**
	 * 交換代数基底クラスの初期化。<br>
	 * 指定された基底キーにより、基底を初期化する。
	 * <br>
	 * 省略された拡張基底キーには、省略記号が代入される。
	 * 
	 * @param keys	交換代数の基底となるキーの配列。
	 * 				<code>KEY_NAME</code> および <code>KEY_HAT</code> 以外のキーが <tt>null</tt>、長さ 0、
	 * 				<code>"#"</code>の場合は、省略されたキーとみなす。
	 * 
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code> もしくは、長さ 0 の場合
	 * <li>ハットキーが、{@link #NO_HAT} もしくは {@link #HAT} 以外の場合
	 * <li>キーに不正な文字が含まれている場合
	 * </ul>
	 * 
	 * @since 0.90
	 */
	private void initialize(String[] keys) {
		// Check basic keys
		if (keys.length < NUM_BASIC_KEYS) {
			if (keys.length < 1)
				throw new IllegalArgumentException("No name key");
			else
				throw new IllegalArgumentException("No hat key");
		}
		//--- name key
		if (keys[KEY_NAME] == null || keys[KEY_NAME].length() <= 0)
			throw new IllegalArgumentException("No name key");
		//--- hat key
		if (keys[KEY_HAT] == null || !isValidHatKey(keys[KEY_HAT]))
			throw new IllegalArgumentException("No hat key");
		
		// 基底キー文字列が有効文字列かを検証する
		int maxKeys = Math.min(keys.length, NUM_ALL_KEYS);
		for (int i = 0; i < maxKeys; i++) {
			if (keys[i] != null && keys[i].length() > 0 && i != KEY_HAT) {
				// 基底キーとして無効な文字が含まれている場合は、エラーとする
				/*--- modified : 2009/03/10 ---*/
				/*---@@@ old codes @@@---
				if (!ExBase.Util.getAllowedBaseKeyPattern().matcher(keys[i]).matches()) {
					String msg = "Illegal character is included in " + getKeyName(i) + ".";
					throw new IllegalArgumentException(msg);
				}
				---@@@ end of old codes @@@---*/
				if (ExBase.Util._illegalCandidates.isIncluded(keys[i])) {
					String msg = "Illegal character is included in " + getKeyName(i) + ".";
					throw new IllegalArgumentException(msg);
				}
				/*--- end of modified : 2009/03/10 ---*/
			}
		}
		
		// create keys, default key is omitted('#')
		initializeBaseKeys(keys, OMITTED);
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	/**
	 * 基底が <b>hat基底</b> であることを示す。
	 * 
	 * @return 基底が <b>hat基底</b> であれば、true を返す
	 */
	public boolean isHat() {
		return ExBase.HAT.equals(this._baseKeys[KEY_HAT]);
	}

	/**
	 * 基底が <b>hatなし基底</b> であることを示す。
	 * 
	 * @return 基底が <b>hatなし基底</b> であれば、true を返す。
	 */
	public boolean isNoHat() {
		return ExBase.NO_HAT.equals(this._baseKeys[KEY_HAT]);
	}

	/**
	 * 指定のキーIDに対応する拡張基底キーが省略されていることを示す。
	 * 
	 * @param keyid 拡張基底キーのキーID。指定可能な値は、次のいずれかとなる。
	 * <ul>
	 * <li>{@link ExtendedKeyID#UNIT} - 単位キー
	 * <li>{@link ExtendedKeyID#TIME} - 時間キー
	 * <li>{@link ExtendedKeyID#SUBJECT} - サブジェクトキー
	 * </ul>
	 * 
	 * @return 指定の拡張基底キーが省略されている場合は true を返す。
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.90
	 */
	public boolean isExtendedKeyOmitted(ExtendedKeyID keyid) {
		return OMITTED.equals(getExtendedKey(keyid));
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 拡張基底キーの単位キーを置換する。
	 * 
	 * @param newKey 新しいキー。<tt>null</tt> もしくは、長さ 0 の文字列の場合、省略記号が代入される。
	 * 
	 * @return 単位キーが置き換えられた新しい <code>ExBase</code> インスタンスを返す
	 * 
	 * @since 0.90
	 */
	public ExBase replaceUnitKey(String newKey) {
		return replaceExtendedKey(ExtendedKeyID.UNIT, newKey);
	}
	
	/**
	 * 拡張基底キーの時間キーを置換する。
	 * 
	 * @param newKey 新しいキー。<tt>null</tt> もしくは、長さ 0 の文字列の場合、省略記号が代入される。
	 * 
	 * @return 時間キーが置き換えられた新しい <code>ExBase</code> インスタンスを返す
	 * 
	 * @since 0.90
	 */
	public ExBase replaceTimeKey(String newKey) {
		return replaceExtendedKey(ExtendedKeyID.TIME, newKey);
	}

	/**
	 * 拡張基底キーを置換する。
	 * 
	 * @param keyid 置き換え対象の拡張基底キーのキーID。指定可能な値は、次のいずれかとなる。
	 * <ul>
	 * <li>{@link ExtendedKeyID#UNIT} - 単位キー
	 * <li>{@link ExtendedKeyID#TIME} - 時間キー
	 * <li>{@link ExtendedKeyID#SUBJECT} - サブジェクトキー
	 * </ul>
	 * @param newKey 新しいキー。<tt>null</tt> もしくは、長さ 0 の文字列の場合、省略記号が代入される。
	 * 
	 * @return 拡張基底キーが置き換えられた新しい <code>ExBase</code> インスタンスを返す
	 * 
	 * @throws NullPointerException <tt>keyid</tt> が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.90
	 */
	public ExBase replaceExtendedKey(ExtendedKeyID keyid, String newKey) {
		// Check keyid
		if (keyid == null) {
			throw new NullPointerException();
		}
		
		// replace
		ExBase result = new ExBase(this);	// copy
		result.modifyExtendedKey(keyid, filterExtendedKey(newKey));
		
		return result;
	}

	/**
	 * 交換代数基底の拡張基底キーを合成する。
	 * <br>
	 * 合成において、拡張基底キーが異なるものは、省略されたキーに置き換えられる。
	 * 基本基底キーが同一でない場合は、例外をスローする。
	 * 
	 * @param aBase 合成する基底の一方
	 * 
	 * @return 合成後の新しい <code>ExBase</code> インスタンスを返す
	 * 
	 * @throws NullPointerException 引数に <tt>null</tt>が指定された場合にスローされる
	 * @throws IllegalArgumentException 基本的キーが異なる場合にスローされる
	 * 
	 * @since 0.90
	 */
	public ExBase margeExtendedKeys(ExBase aBase) {
		return margeExtendedKeys(aBase, null, null);
	}
	
	/**
	 * 交換代数基底の拡張基底キーを合成し、指定の拡張キーを置換する。
	 * <br>
	 * 合成において、拡張基底キーが異なるものは、省略記号に置き換えられる。
	 * <br>
	 * 基本基底キーが同一でない場合は、例外をスローする。
	 * 
	 * <p>このメソッドでは、<tt>keyid</tt> が <tt>null</tt> の場合は、拡張基底キーの
	 * 置き換えを行わない合成処理となる。
	 * 
	 * @param aBase 合成する基底の一方
	 * @param keyid 置き換え対象の拡張基底キーのキーID。指定可能な値は、次のいずれかとなる。
	 * <ul>
	 * <li>{@link ExtendedKeyID#UNIT} - 単位キー
	 * <li>{@link ExtendedKeyID#TIME} - 時間キー
	 * <li>{@link ExtendedKeyID#SUBJECT} - サブジェクトキー
	 * </ul>
	 * <tt>keyid</tt> が <tt>null</tt> の場合、拡張基底キーの置き換えを行わない合成処理となる。
	 * @param newKey 新しいキー。<tt>null</tt> もしくは長さ 0 の文字列の場合、省略記号で置き換えられる。
	 * 
	 * @return 合成後の新しい <code>ExBase</code> インスタンスを返す
	 * 
	 * @throws NullPointerException <tt>aBase</tt> に <tt>null</tt>が指定された場合にスローされる
	 * @throws IllegalArgumentException 基本基底キーが異なる場合にスローされる
	 * 
	 * @since 0.90
	 */
	public final ExBase margeExtendedKeys(ExBase aBase, ExtendedKeyID keyid, String newKey) {
		// Check
		if (!aBase.getBasicKeysString().equals(this.getBasicKeysString())) {
			throw new IllegalArgumentException("Not same basic keys");
		}
		// Marge
		ExBase result;
		if (this.equals(aBase)) {
			// same ExBase
			if (keyid != null) {
				// replace to new key
				result = replaceExtendedKey(keyid, newKey);
			} else {
				result = this;
			}
		}
		else {
			// Marge proc
			result = new ExBase(this);	// copy
			for (int i = NUM_BASIC_KEYS; i < result._baseKeys.length; i++) {
				if (!result._baseKeys[i].equals(aBase._baseKeys[i])) {
					// replace to omitted char
					result._baseKeys[i] = OMITTED;
				}
			}
			if (keyid != null) {
				// force replace extended key & update one string key
				result.modifyExtendedKey(keyid, filterExtendedKey(newKey));
			} else {
				/*--- modified : 2009/03/10 ---*/
				// update one string key
				//result.updateOneStringKey();
				// setup status
				result.setupStatus();
				/*--- end of modified : 2009/03/10 ---*/
			}
		}
		return result;
	}

	/**
	 * 基底に対する＾計算 項目はそのまま、＾キーは反転する
	 * 
	 * @return ＾計算された後の基底
	 */
	public final ExBase hat() {
		ExBase result = new ExBase(this);	// copy
		result._baseKeys[KEY_HAT]  = (this.isHat() ? ExBase.NO_HAT : ExBase.HAT);
		/*--- modified : 2009/03/10 ---*/
		//result.updateOneStringKey();
		result.setupStatus();
		/*--- end of modified : 2009/03/10 ---*/
		return result;
	}

	/**
	 * 基底をハット基底にする。
	 * <p>このメソッドは、基底のハットキーを {@link #HAT} にした基底を返す。
	 * ハットキーがすでに {@link #HAT} のものは、そのまま返す。
	 * 
	 * @return	ハット(^)を付加した基底
	 * 
	 * @since 0.960
	 */
	public final ExBase setHat() {
		if (isHat()) {
			return this;
		} else {
			ExBase result = new ExBase(this);	// copy
			result._baseKeys[KEY_HAT] = ExBase.HAT;
			/*--- modified : 2009/03/10 ---*/
			//result.updateOneStringKey();
			result.setupStatus();
			/*--- end of modified : 2009/03/10 ---*/
			return result;
		}
	}

	/**
	 * 基底をハットなし基底にする。
	 * <p>このメソッドは、基底のハットキーを {@link #NO_HAT} にした基底を返す。
	 * ハットキーがすでに {@link #NO_HAT} のものは、そのまま返す。
	 * 
	 * @return	ハット(^)を除去した基底
	 * 
	 * @since 0.960
	 */
	public final ExBase removeHat() {
		if (isHat()) {
			ExBase result = new ExBase(this);	// copy
			result._baseKeys[KEY_HAT] = ExBase.NO_HAT;
			/*--- modified : 2009/03/10 ---*/
			//result.updateOneStringKey();
			result.setupStatus();
			/*--- end of modified : 2009/03/10 ---*/
			return result;
		} else {
			return this;
		}
	}
	
	/**
	 * このインスタンスを文字列として出力する。
	 * <br>
	 * 出力形式は、次の通り。
	 * <blockquote>
	 * <b>[ハットなし基底]</b>
	 * <br>
	 * '&lt;' <i>名前キー</i> ',' <i>単位キー</i> ',' <i>時間キー</i> ',' <i>主体キー</i> '&gt;'
	 * <br>
	 * <b>[ハット基底]</b>
	 * <br>
	 * '^&lt;' <i>名前キー</i> ',' <i>単位キー</i> ',' <i>時間キー</i> ',' <i>主体キー</i> '&gt;'
	 * </blockquote>
	 * 
	 * @since 0.94
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (isHat()) {
			sb.append("^");
		}
		sb.append("<");
		sb.append(this._baseKeys[KEY_NAME]);
		for (int i = NUM_BASIC_KEYS; i < this._baseKeys.length; i++) {
			sb.append(",");
			sb.append(this._baseKeys[i]);
		}
		sb.append(">");
		
		return sb.toString();
	}
	
	/**
	 * このメソッドは{@link #toString()}に集約された。
	 * 
	 * @return 交換代数文字列
	 * 
	 * @see #toString()
	 * 
	 * @since 0.90
	 * 
	 * @deprecated このメソッドは{@link #toString()}に集約された。
	 */
	public String toFormattedString() {
		return toString();
	}

	//------------------------------------------------------------
	// Comparable interfaces
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
	 * 
	 * @since 0.93
	 * 
	 */
	public int compareTo(ExBase base) {
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
	 * 
	 * @since 0.93
	 * 
	 */
	public int compareToIgnoreCase(ExBase base) {
		return super.baseCompareToIgnoreCase(base);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 文字列が <tt>null</tt> もしくは、長さ 0 の場合、省略記号に置き換える
	 * 
	 */
	private String filterExtendedKey(String newKey) {
		if (newKey != null && newKey.length() > 0)
			return newKey;
		else
			return OMITTED;
	}

	/**
	 * ハットキー文字列が厳密に内部定義文字列と一致しているかを検証する。
	 * 
	 * @param hatKey 検証する文字列
	 * @return 内部定義文字列と一致している場合は <tt>true</tt>
	 */
	private boolean isValidHatKey(String hatKey) {
		if (ExBase.HAT.equals(hatKey))
			return true;	// 正等
		else if (ExBase.NO_HAT.equals(hatKey))
			return true;	// 正等
		// 不正
		return false;
	}
	
	static private String formatIllegalKeyMessage(String keyname, String input) {
		if (Strings.isNullOrEmpty(input))
			return (keyname + " key is not specified.");
		else
			return ("Illegal " + keyname + " key : \"" + input + "\"");
	}

	/**
	 * 指定された <code>writer</code> の現在位置へ、基底データを出力する。
	 * <br>
	 * 各基底キーは、次の順序で CSV カラムとして出力される。
	 * <ol type="1" start="1">
	 * <li>ハットキー
	 * <li>名前キー
	 * <li>単位キー
	 * <li>時間キー
	 * <li>主体キー
	 * </ol>
	 * なお、このメソッドによる出力では、レコード終端記号(改行)は出力されない。
	 * 
	 * @param writer		出力先となる <code>CsvWriter</code> オブジェクト
	 * 
	 * @throws IOException	書き込みに失敗した場合
	 * 
	 * @since 0.982
	 */
	protected void writeFieldToCSV(CsvWriter writer) throws IOException
	{
		// ハットキー
		writer.writeField(getHatKey());
		
		// 名前キー
		writer.writeField(getNameKey());
		
		// 単位キー
		writer.writeField(getUnitKey());
		
		// 時間キー
		writer.writeField(getTimeKey());
		
		// 主体キー
		writer.writeField(getSubjectKey());
	}
	

	/**
	 * 指定された <code>writer</code> の現在位置へ、基底データを出力する。
	 * <br>
	 * 各基底キーは、次の順序で CSV カラムとして出力される。
	 * <ol type="1" start="1">
	 * <li>ハットキー
	 * <li>名前キー
	 * <li>全ての拡張基底キー
	 * </ol>
	 * 
	 * @param writer 出力先となる <code>CsvWriter</code> オブジェクト
	 * 
	 * @throws IOException 書き込みに失敗した場合にスローされる
	 * 
	 * @since 0.91
	 * 
	 * @deprecated このメソッドは削除されます。新しい {@link #writeFieldToCSV(CsvWriter)} を使用してください。
	 */
	protected void writeToCSV(CsvWriter writer) throws IOException
	{
		// ハットキー
		writer.writeColumn(getHatKey(), false);
		
		// 名前キー
		writer.writeColumn(getNameKey(), false);
		
		// 単位キー
		writer.writeColumn(getUnitKey(), false);
		
		// 時間キー
		writer.writeColumn(getTimeKey(), false);
		
		// サブジェクトキー
		writer.writeColumn(getSubjectKey(), true);
	}
	
	/**
	 * 指定された <code>reader</code> の現在位置から、基底データを取得する。
	 * <br>
	 * 各基底キーは、現在のカラム位置から次の順序で読み込まれる。
	 * <ol type="1" start="1">
	 * <li>ハットキー
	 * <li>名前キー
	 * <li>単位キー
	 * <li>時間キー
	 * <li>主体キー
	 * </ol>
	 * <p>
	 * なお、ハットキーは、{@link ExBase.Util#getAllowedHatKeyPattern()} で
	 * 定義されているマッチングパターンと一致しなければ、フォーマットエラーとなる。
	 * <br>
	 * また、ハットキー以外の基底キーは、{@link ExBase.Util#getAllowedBaseKeyPattern()} で
	 * 定義されているマッチングパターンと一致しなければ、フォーマットエラーとなる。
	 * <p>
	 * 読み込みにおいて、名前キー以外は省略可能であり、省略されている場合は省略記号({@link #OMITTED})に
	 * 置き換えられる。ただし、ハットキーのみ、省略された場合は {@link #NO_HAT} となる。
	 * 
	 * @param reader	読み込み対象のリーダー
	 * 
	 * @return 読み込まれた値から生成された <code>ExBase</code> オブジェクト
	 * 
	 * @throws NullPointerException <code>reader</code> が <tt>null</tt> の場合
	 * @throws IOException 読み込み時エラーが発生したとき
	 * @throws CsvFormatException フィールドのデータが正しくない場合
	 * 
	 * @since 0.982
	 */
	static protected ExBase readFieldFromCSV(CsvReader.CsvFieldReader reader)
		throws IOException, CsvFormatException
	{
		String strInput;
		
		// hat key
		strInput = reader.readTrimmedValue();
		String hatKey = ExBase.Util.filterHatKey(strInput, true);
		if (hatKey == null) {
			throw new CsvFormatException(formatIllegalKeyMessage("hat", strInput), reader.getLineNo(), reader.getNextPosition());
		}
		
		// name key
		strInput = reader.readTrimmedValue();
		String nameKey = ExBase.Util.filterBaseKey(strInput, false);
		if (nameKey == null) {
			throw new CsvFormatException(formatIllegalKeyMessage("name", strInput), reader.getLineNo(), reader.getNextPosition());
		}
		
		// unit key
		strInput = reader.readTrimmedValue();
		String unitKey = ExBase.Util.filterBaseKey(strInput, true);
		if (unitKey == null) {
			throw new CsvFormatException(formatIllegalKeyMessage("unit", strInput), reader.getLineNo(), reader.getNextPosition());
		}
		
		// time key
		strInput = reader.readTrimmedValue();
		String timeKey = ExBase.Util.filterBaseKey(strInput, true);
		if (timeKey == null) {
			throw new CsvFormatException(formatIllegalKeyMessage("time", strInput), reader.getLineNo(), reader.getNextPosition());
		}
		
		// subject key
		strInput = reader.readTrimmedValue();
		String subjectKey = ExBase.Util.filterBaseKey(strInput, true);
		if (subjectKey == null) {
			throw new CsvFormatException(formatIllegalKeyMessage("subject", strInput), reader.getLineNo(), reader.getNextPosition());
		}
		
		// make ExBasePattern instance
		ExBase rBase = new ExBase(nameKey, hatKey, unitKey, timeKey, subjectKey);
		return rBase;
	}

	/**
	 * 指定された <code>reader</code> の現在位置から、基底データを取得する。
	 * <br>
	 * 各基底キーは、現在のカラム位置から次の順序で読み込まれる。
	 * <ol type="1" start="1">
	 * <li>ハットキー
	 * <li>名前キー
	 * <li>全ての拡張基底キー
	 * </ol>
	 * <p>
	 * なお、ハットキーは、{@link ExBase.Util#getAllowedHatKeyPattern()} で
	 * 定義されているマッチングパターンと一致しなければ、フォーマットエラーとなる。
	 * <br>
	 * また、ハットキー以外の基底キーは、{@link ExBase.Util#getAllowedBaseKeyPattern()} で
	 * 定義されているマッチングパターンと一致しなければ、フォーマットエラーとなる。
	 * <p>
	 * 読み込みにおいて、名前キー以外は省略可能であり、省略されている場合は省略記号({@link #OMITTED})に
	 * 置き換えられる。ただし、ハットキーのみ、省略された場合は {@link #NO_HAT} となる。
	 * 
	 * @param reader 入力元となる <code>CsvRecordReader</code> オブジェクト
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws IOException 読み込み時エラーが発生したときにスローされる
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.91
	 * 
	 * @deprecated このメソッドは削除されます。新しい {@link #readFieldFromCSV(exalge2.io.csv.CsvReader.CsvFieldReader)} を使用してください。
	 */
	protected void readFromCSV(CsvRecordReader reader)
		throws IOException, CsvFormatException
	{
		String strInput;
		String strValue;
		
		// ハットキー
		strInput = reader.readStringColumn(true);
		strValue = ExBase.Util.filterHatKey(strInput, true);
		if (strValue == null) {
			throw new CsvFormatException(CsvFormatException.ILLEGAL_COLUMN_PATTERN,
					reader.getLineNumber(), reader.getColumnNumber()-1);
		}
		this._baseKeys[KEY_HAT] = strValue;
		
		/*--- modified : 2009/03/10 ---*/
		/*---@@@ old codes @@@---
		// 名前キー
		strInput = reader.readTrimmedStringColumn(ExBase.Util.getAllowedBaseKeyPattern(), false);
		this._baseKeys[KEY_NAME] = strInput;
		
		// 単位キー
		strInput = reader.readTrimmedStringColumn(ExBase.Util.getAllowedBaseKeyPattern(), true);
		if (strInput == null) {
			strInput = ExBase.OMITTED;
		}
		this._baseKeys[KEY_EXT_UNIT] = strInput;
		
		// 時間キー
		strInput = reader.readTrimmedStringColumn(ExBase.Util.getAllowedBaseKeyPattern(), true);
		if (strInput == null) {
			strInput = ExBase.OMITTED;
		}
		this._baseKeys[KEY_EXT_TIME] = strInput;
		
		// サブジェクトキー
		strInput = reader.readTrimmedStringColumn(ExBase.Util.getAllowedBaseKeyPattern(), true);
		if (strInput == null) {
			strInput = ExBase.OMITTED;
		}
		this._baseKeys[KEY_EXT_SUBJECT] = strInput;
		
		// 一意文字列キーの更新
		updateOneStringKey();
		---@@@ end of old codes @@@---*/
		// 名前キー
		strInput = reader.readTrimmedStringColumn(false);
		strValue = ExBase.Util.filterBaseKey(strInput, false);
		if (strValue == null) {
			throw new CsvFormatException(strInput, CsvFormatException.ILLEGAL_COLUMN_PATTERN,
					reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_NAME] = strValue;
		
		// 単位キー
		strInput = reader.readTrimmedStringColumn(true);
		strValue = ExBase.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			throw new CsvFormatException(strInput, CsvFormatException.ILLEGAL_COLUMN_PATTERN,
					reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_EXT_UNIT] = strValue;
		
		// 時間キー
		strInput = reader.readTrimmedStringColumn(true);
		strValue = ExBase.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			throw new CsvFormatException(strInput, CsvFormatException.ILLEGAL_COLUMN_PATTERN,
					reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_EXT_TIME] = strValue;
		
		// 主体キー
		strInput = reader.readTrimmedStringColumn(true);
		strValue = ExBase.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			throw new CsvFormatException(strInput, CsvFormatException.ILLEGAL_COLUMN_PATTERN,
					reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_EXT_SUBJECT] = strValue;
		
		// 内部情報の更新
		setupStatus();
		/*--- end of modified : 2009/03/10 ---*/
	}
	
	/**
	 * 基底データを、XML ノードに変換する。
	 * <br>
	 * 変換されるノードの構成は、次のようになる。
	 * <blockquote>
	 * &lt;Exbase
	 *  hat=&quote;<i>hatKey</i>&quote;
	 *  name=&quote;<i>nameKey</i>&quote;
	 *  unit=&quote;<i>unitKey</i>&quote;
	 *  time=&quote;<i>timeKey</i>&quote;
	 *  subject=&quote;<i>subjectKey</i>&quote; /&gt;
	 * </blockquote>
	 * 
	 * @param xmlDocument ノード生成に使用する {@link XmlDocument} インスタンス
	 * @return 生成された XML ノード
	 * 
	 * @throws DOMException ノード生成に失敗した場合
	 * 
	 * @since 0.982
	 */
	protected Element encodeToDomElement(XmlDocument xmlDocument) throws DOMException
	{
		Element node = xmlDocument.createElement(XML_ELEMENT_NAME);
		
		// ハットキー
		node.setAttribute(XML_ATTR_HATKEY, getHatKey());
		
		// 名前キー
		node.setAttribute(XML_ATTR_NAMEKEY, getNameKey());
		
		// 単位キー
		node.setAttribute(XML_ATTR_UNITKEY, getUnitKey());
		
		// 時間キー
		node.setAttribute(XML_ATTR_TIMEKEY, getTimeKey());
		
		// サブジェクトキー
		node.setAttribute(XML_ATTR_SUBJECTKEY, getSubjectKey());
		
		return node;
	}
	
	/**
	 * 指定された XML ノードから、基底パターンデータを取得する。
	 * <br>
	 * 次の XML ノード構成から、基底パターンデータを取得する。
	 * <blockquote>
	 * &lt;Exbase
	 *  hat=&quote;<i>hatKey</i>&quote;
	 *  name=&quote;<i>nameKey</i>&quote;
	 *  unit=&quote;<i>unitKey</i>&quote;
	 *  time=&quote;<i>timeKey</i>&quote;
	 *  subject=&quote;<i>subjectKey</i>&quote; /&gt;
	 * </blockquote>
	 * <p>
	 * 読み込みにおいて、名前キー以外は省略可能であり、省略されている場合は省略記号({@link #OMITTED})に
	 * 置き換えられる。ただし、ハットキーのみ、省略された場合は {@link #NO_HAT} となる。
	 * 
	 * @param xmlDocument ノードが所属する {@link XmlDocument} インスタンス
	 * @param xmlElement 解析対象の XML ノード
	 * 
	 * @return 解析した値から生成された <code>ExBase</code> オブジェクト
	 * 
	 * @throws XmlDomParseException ノード解析エラーが発生した場合
	 * 
	 * @since 0.982
	 */
	static protected ExBase decodeFromDomElement(XmlDocument xmlDocument, Element xmlElement) throws XmlDomParseException
	{
		String strInput;
		
		// check Element name
		strInput = xmlElement.getNodeName();
		if (!strInput.equals(XML_ELEMENT_NAME)) {
			// Illegal element name
			throw new XmlDomParseException(String.format("The child of <%s> must be <%s> : <%s>",
					String.valueOf(xmlElement.getParentNode().getNodeName()), XML_ELEMENT_NAME, String.valueOf(strInput)));
		}
		
		// hat key
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_HATKEY));
		String hatKey = ExBase.Util.filterHatKey(strInput, true);
		if (hatKey == null) {
			throw new XmlDomParseException(formatIllegalKeyMessage("hat", strInput));
		}
		
		// name key
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_NAMEKEY));
		String nameKey = ExBase.Util.filterBaseKey(strInput, false);
		if (nameKey == null) {
			throw new XmlDomParseException(formatIllegalKeyMessage("name", strInput));
		}
		
		// unit key
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_UNITKEY));
		String unitKey = ExBase.Util.filterBaseKey(strInput, true);
		if (unitKey == null) {
			throw new XmlDomParseException(formatIllegalKeyMessage("unit", strInput));
		}
		
		// time key
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_TIMEKEY));
		String timeKey = ExBase.Util.filterBaseKey(strInput, true);
		if (timeKey == null) {
			throw new XmlDomParseException(formatIllegalKeyMessage("time", strInput));
		}
		
		// subject key
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_SUBJECTKEY));
		String subjectKey = ExBase.Util.filterBaseKey(strInput, true);
		if (subjectKey == null) {
			throw new XmlDomParseException(formatIllegalKeyMessage("subject", strInput));
		}
		
		// make ExBasePattern instance
		ExBase rBase = new ExBase(nameKey, hatKey, unitKey, timeKey, subjectKey);
		return rBase;
	}
	
	/**
	 * 基底データを、XML ノードに変換する。
	 * <br>
	 * 変換されるノードの構成は、次のようになる。
	 * <blockquote>
	 * &lt;Exbase hat=&quote;<i>hatKey</i>&quote;
	 *  name=&quote;<i>nameKey</i>&quote;
	 *  unit=&quote;<i>unitKey</i>&quote;
	 *  time=&quote;<i>timeKey</i>&quote;
	 *  subject=&quote;<i>subjectKey</i>&quote; /&gt;
	 * </blockquote>
	 * 
	 * @param xmlDocument ノード生成に使用する {@link XmlDocument} インスタンス
	 * @return 生成された XML ノード
	 * 
	 * @throws DOMException ノード生成に失敗した場合
	 * 
	 * @since 0.91
	 * 
	 * @deprecated このメソッドは削除されます。新しい {@link #encodeToDomElement(XmlDocument)} を使用してください。
	 */
	protected Element encodeToElement(XmlDocument xmlDocument) throws DOMException
	{
		Element node = xmlDocument.createElement(XML_ELEMENT_NAME);
		
		// ハットキー
		node.setAttribute(XML_ATTR_HATKEY, getHatKey());
		
		// 名前キー
		node.setAttribute(XML_ATTR_NAMEKEY, getNameKey());
		
		// 単位キー
		node.setAttribute(XML_ATTR_UNITKEY, getUnitKey());
		
		// 時間キー
		node.setAttribute(XML_ATTR_TIMEKEY, getTimeKey());
		
		// サブジェクトキー
		node.setAttribute(XML_ATTR_SUBJECTKEY, getSubjectKey());
		
		return node;
	}
	
	/**
	 * 指定された XML ノードから、基底データを取得する。
	 * <br>
	 * 次の XML ノード構成から、基底データを取得する。
	 * <blockquote>
	 * &lt;Exbase hat=&quote;<i>hatKey</i>&quote;
	 *  name=&quote;<i>nameKey</i>&quote;
	 *  unit=&quote;<i>unitKey</i>&quote;
	 *  time=&quote;<i>timeKey</i>&quote;
	 *  subject=&quote;<i>subjectKey</i>&quote; /&gt;
	 * </blockquote>
	 * <p>
	 * 読み込みにおいて、名前キー以外は省略可能であり、省略されている場合は省略記号({@link #OMITTED})に
	 * 置き換えられる。ただし、ハットキーのみ、省略された場合は {@link #NO_HAT} となる。
	 * 
	 * @param xmlDocument ノードが所属する {@link XmlDocument} インスタンス
	 * @param xmlElement 解析対象の XML ノード
	 * 
	 * @throws XmlDomParseException ノード解析エラーが発生した場合
	 * 
	 * @since 0.91
	 * 
	 * @deprecated このメソッドは削除されます。新しい {@link #decodeFromDomElement(XmlDocument, Element)} を使用してください。
	 */
	protected void decodeFromElement(XmlDocument xmlDocument, Element xmlElement)
		throws XmlDomParseException
	{
		String strInput;
		String strValue;
		
		// check Element name
		strInput = xmlElement.getNodeName();
		if (!strInput.equals(XML_ELEMENT_NAME)) {
			// Illegal element name
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_ELEMENT_NAME,
											xmlDocument, xmlElement, null, null);
		}
		
		// ハットキー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_HATKEY));
		strValue = ExBase.Util.filterHatKey(strInput, true);
		if (strValue == null) {
			// Illegal hat key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_HATKEY, strInput);
		}
		this._baseKeys[KEY_HAT] = strValue;
		
		// 名前キー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_NAMEKEY));
		strValue = ExBase.Util.filterBaseKey(strInput, false);
		if (strValue == null) {
			// Illegal name key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_NAMEKEY, strInput);
		}
		this._baseKeys[KEY_NAME] = strValue;
		
		// 単位キー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_UNITKEY));
		strValue = ExBase.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			// Illegal name key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_UNITKEY, strInput);
		}
		this._baseKeys[KEY_EXT_UNIT] = strValue;
		
		// 時間キー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_TIMEKEY));
		strValue = ExBase.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			// Illegal name key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_TIMEKEY, strInput);
		}
		this._baseKeys[KEY_EXT_TIME] = strValue;
		
		// サブジェクトキー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_SUBJECTKEY));
		strValue = ExBase.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			// Illegal name key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_SUBJECTKEY, strInput);
		}
		this._baseKeys[KEY_EXT_SUBJECT] = strValue;

		/*--- modified : 2009/03/10 ---*/
		/*---@@@ old codes @@@---
		// 一意文字列キーの更新
		updateOneStringKey();
		---@@@ end of old codes @@@---*/
		// 内部情報の更新
		setupStatus();
		/*--- end of modified : 2009/03/10 ---*/
	}

	/**
	 * <code>{@link ExBase}</code> クラスに関する補助機能を提供するユーティリティクラス。
	 * 
	 * @version 0.91 2007/08/09
	 * 
	 * @author SOARS Project.
	 * @author Y.Ishizuka(PieCake.inc,)
	 * 
	 * @since 0.91
	 */
	static public class Util
	{
		/**
		 * <code>ExBase</code> で許可される、ハットキー文字列のマッチングパターン
		 * <br>
		 * このパターンは、禁止文字列(ワイルドカードも禁止文字列)を除いた、
		 * 許容されるハットキー文字列にマッチする。大文字小文字は区別しない。
		 * <br>
		 * "NO_HAT"、"HAT"、"^"(ハット記号)、以外の文字列は許容されない。
		 * 
		 * @deprecated (0.970)マッチングアルゴリズムが変更されたため、このフィールドは削除されます。
		 */
		static protected final Pattern _AllowedHatPattern
					= Pattern.compile("\\A(" + _BASIC_HATKEY_LIST + ")\\z", Pattern.CASE_INSENSITIVE);

		/**
		 * <code>ExBase</code> で許可される、基底キー文字列のマッチングパターン
		 * <br>
		 * このパターンは、禁止文字列を除いた、
		 * 許容される基底キー文字列にマッチする。
		 * <br>アスタリスクも文字として許容される。
		 * 
		 * @deprecated (0.970)マッチングアルゴリズムが変更されたため、このフィールドは削除されます。
		 */
		static protected final Pattern _AllowedKeyPattern
					= Pattern.compile("\\A[^" + _ILLEGAL_BASEKEY_LIST + "]+\\z");

		/**
		 * <code>ExBase</code> の基底キーとして使用できない文字のセット
		 * 
		 * @since 0.970
		 */
		static protected final CharCandidates _illegalCandidates = new CharCandidates(_ILLEGAL_BASEKEY_CHARS);

		/**
		 * <code>ExBase</code> で許可される、ハットキー文字列のマッチングパターンを返す。
		 * <br>
		 * このパターンは、禁止文字列(ワイルドカードも禁止文字列)を除いた、
		 * 許容されるハットキー文字列にマッチする。大文字小文字は区別しない。
		 * <br>
		 * "NO_HAT"、"HAT"、"^"(ハット記号)、以外の文字列は許容されない。
		 * <br>
		 * このパターンでは、長さ 0 の文字列は許容されない。
		 * 
		 * @return <code>ExBase</code> のハットキー許容パターン
		 * 
		 * @deprecated (0.970)マッチングアルゴリズムが変更されたため、このメソッドは削除されます。
		 */
		static public Pattern getAllowedHatKeyPattern() {
			return _AllowedHatPattern;
		}
		
		/**
		 * <code>ExBase</code> で許可される、基底キー文字列のマッチングパターンを返す。
		 * <br>
		 * このパターンは、禁止文字列(ワイルドカードも禁止文字列)を除いた、
		 * 許容される基底キー文字列にマッチする。
		 * <br>
		 * このパターンでは、長さ 0 の文字列は許容されない。
		 * 
		 * @return <code>ExBase</code> の基底キー許容パターン
		 * 
		 * @deprecated (0.970)マッチングアルゴリズムが変更されたため、このメソッドは削除されます。
		 */
		static public Pattern getAllowedBaseKeyPattern() {
			return _AllowedKeyPattern;
		}

		/**
		 * 入力文字列が許容されるハットキー文字列であるかを検証する。
		 * <br>
		 * <code>isAllowedOmitted</code> に <tt>true</tt> が指定されており、
		 * <code>input</code> が <tt>null</tt> もしくは、長さ 0 の文字列の場合は <tt>true</tt> を返す。
		 * 
		 * @param input 入力文字列
		 * @param isAllowedOmitted 長さ 0 もしくは <tt>null</tt> の文字列を許容する場合は <tt>true</tt>
		 * @return 許容されるハットキー文字列なら <tt>true</tt>
		 */
		static public boolean isValidHatKeyString(final String input, final boolean isAllowedOmitted) {
			boolean result = false;

			if (input != null && input.length() > 0) {
				/*--- modified : 2009/03/10 ---*/
				/*---@@@ old codes @@@---
				result = getAllowedHatKeyPattern().matcher(input).matches();
				---@@@ end of old codes @@@---*/
				if (isHatString(input))
					result = true;
				else if (isNoHatString(input))
					result = true;
				/*--- end of modified : 2009/03/10 ---*/
			}
			else {
				result = isAllowedOmitted;	// 省略の許容による
			}
			
			return result;
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
				/*--- modified : 2009/03/10 ---*/
				/*---@@@ old codes @@@---
				result = getAllowedBaseKeyPattern().matcher(input).matches();
				---@@@ end of old codes @@@---*/
				result = _illegalCandidates.isExcluded(input);
				/*--- end of modified : 2009/03/10 ---*/
			}
			else {
				result = isAllowedOmitted;	// 省略の許容による
			}
			
			return result;
		}

		/**
		 * 入力文字列を正規ハットキー({@link ExBase#NO_HAT} もしくは {@link ExBase#HAT})に変換する。
		 * <br>
		 * 入力文字列がハットキー文字列として許容されない場合は、<tt>null</tt> を返す。
		 * <br>
		 * <code>isAllowedOmitted</code> に <tt>true</tt> が指定されており、
		 * <code>input</code> が <tt>null</tt> もしくは、長さ 0 の文字列の場合は {@link ExBase#NO_HAT} を返す。
		 * <br>
		 * このメソッドでは、入力文字列の前後の空白は無視される。
		 * 
		 * @param input 入力文字列
		 * @param isAllowedOmitted 長さ 0 もしくは <tt>null</tt> の文字列を許容する場合は <tt>true</tt>
		 * @return 正規ハットキー
		 */
		static public String filterHatKey(final String input, final boolean isAllowedOmitted) {
			if (input == null) {
				if (isAllowedOmitted) {
					// 省略は NO_HAT
					return ExBase.NO_HAT;
				} else {
					// 省略不可の場合は null
					return null;
				}
			}

			String result = null;
			String srcValue = input.trim();
			if (srcValue.length() > 0) {
				/*--- modified : 2009/03/10 ---*/
				/*---@@@ old codes @@@---
				if (getAllowedHatKeyPattern().matcher(srcValue).matches()) {
					if (_IS_HAT_PATTERN.matcher(srcValue).matches())
						result = ExBase.HAT;
					else
						result = ExBase.NO_HAT;
				}
				---@@@ end of old codes @@@---*/
				if (isHatString(srcValue))
					result = ExBase.HAT;
				else if (isNoHatString(srcValue))
					result = ExBase.NO_HAT;
				/*--- end of modified : 2009/03/10 ---*/
			} else if (isAllowedOmitted) {
				// 省略は NO_HAT
				result = ExBase.NO_HAT;
			}
			
			return result;
		}
		
		/**
		 * 入力文字列を基底キー文字列として正しい形式に整形する。
		 * <br>
		 * 整形は、次のように行われる。
		 * <ul>
		 * <li>入力文字列の前後の空白を削除する
		 * <li>入力文字列が空文字列(長さ 0、もしくは <tt>null</tt>)なら、省略記号({@link ExBase#OMITTED})とする
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
					return ExBase.OMITTED;
				} else {
					// 省略不可の場合は null
					return null;
				}
			}
			
			String result = null;
			String srcValue = input.trim();
			if (srcValue.length() > 0) {
				/*--- modified : 2009/03/10 ---*/
				/*---@@@ old codes @@@---
				if (getAllowedBaseKeyPattern().matcher(srcValue).matches()) {
					result = srcValue;
				}
				---@@@ end of old codes @@@---*/
				if (_illegalCandidates.isExcluded(srcValue)) {
					result = srcValue;
				}
				/*--- end of modified : 2009/03/10 ---*/
			} else if (isAllowedOmitted) {
				// 省略は省略記号
				result = ExBase.OMITTED;
			}
			
			return result;
		}
	}
}