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
 * @(#)ExBasePattern.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBasePattern.java	0.970	2009/03/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBasePattern.java	0.960	2009/02/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBasePattern.java	0.94	2008/06/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBasePattern.java	0.93	2007/09/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBasePattern.java	0.92	2007/08/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBasePattern.java	0.91	2007/08/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBasePattern.java	0.90	2007/07/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import exalge2.io.csv.CsvFormatException;
import exalge2.io.csv.CsvReader;
import exalge2.io.csv.CsvRecordReader;
import exalge2.io.csv.CsvWriter;
import exalge2.io.xml.XmlDocument;
import exalge2.io.xml.XmlDomParseException;
import exalge2.io.xml.XmlErrors;
import exalge2.util.Strings;

/**
 * 交換代数１基底のマッチングパターンを保持するクラス。
 * 
 * <p>このクラスは、不変オブジェクト(Immutable)である。<br>
 * 基本的に交換代数基底クラス {@link ExBase} と同様のデータ構造となり、
 * 基本基底キーや拡張基底キーを、{@link ExBase} と同じ順序で保持する。
 * さらに、ワイルドカード記号({@link #WILDCARD})を基底キーに指定することができる。
 * <br>
 * ワイルドカード記号は、交換代数基底の基底に対し、0文字以上の文字と
 * マッチングする。
 * 
 * <p>このクラスでは、基底キーの指定において、キーが省略(<tt>null</tt> もしくは、
 * 長さ 0 の文字列が指定)された場合、ワイルドカード記号文字を代入する。
 * <p>
 * ハットキー以外の基底キーには、次の文字は使用できない。
 * <blockquote>
 * &lt; &gt; - , ^ &quot; % &amp; ? | @ ' " (空白)
 * </blockquote>
 * 
 * @version 0.982	2009/09/13
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.90
 */
public final class ExBasePattern extends AbExBase implements Comparable<ExBasePattern>
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

	/*
	static private final Pattern replaceTermPattern = Pattern.compile("\\\\E");
	static private final String replaceEscapeTerm = "\\\\E\\\\\\\\E\\\\Q";
	static private final Pattern replacePattern = Pattern.compile("\\*+");
	static private final String replaceWildcard = "\\\\E[^-]*\\\\Q";
	*/

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/*
	 * 基底キーのマッチングパターン
	 * <br>
	 * 全基底キーでワイルドカードが含まれない場合、
	 * このフィールドは <tt>null</tt> となる。
	 *
	private Pattern machingPattern;
	*/

	/**
	 * 基底キーのマッチングパターン。
	 * この配列には、ワイルドカードのみのパターンは含まれない。
	 * 全ての基底キーがワイルドカードのみの場合、このフィールドは <tt>null</tt> となる。
	 * @since 0.970
	 */
	private PatternItem[] patternItems;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 一意文字列キーから <code>ExBasePattern</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * 
	 * @param oneStringKey	交換代数基底の一意文字列キー
	 * 
	 * @throws IllegalArgumentException 次の場合、この例外がスローされる。
	 * <ul>
	 * <li>ハットキーが {@link ExBase#HAT}、{@link ExBase#NO_HAT}、もしくはワイルドカード以外
	 * </ul>
	 */
	public ExBasePattern(String oneStringKey) {
		super();
		String[] keys = oneStringKey.split(DELIMITOR);

		initialize(keys);
	}

	/**
	 * 指定された基底キー配列から <code>ExBasePattern</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
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
	 * @throws IllegalArgumentException 次の場合、この例外がスローされる。
	 * <ul>
	 * <li>ハットキーが {@link ExBase#HAT}、{@link ExBase#NO_HAT}、もしくはワイルドカード以外
	 * </ul>
	 */
	public ExBasePattern(String[] baseKeys) {
		super();
		// initialize
		initialize(baseKeys);
	}

	/**
	 * 指定された名前キー、ハットキー、拡張基底キーから <code>ExBasePattern</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * 
	 * @param nameKey	交換代数基底の名前キー。
	 * @param hatKey	交換代数基底のハットキー。指定可能な値は、{@link ExBase#NO_HAT}、{@link ExBase#HAT}、
	 * 					もしくはワイルドーカード。
	 * @param extendedKeys	拡張基底キー配列。この配列内のキーは、次の順序で指定されているものとする。
	 * <ol type="1" start="0">
	 * <li>単位キー(unit key)
	 * <li>時間キー(time key)
	 * <li>サブジェクトキー(subject key)
	 * </ol>
	 * 
	 * @throws IllegalArgumentException 次の場合、この例外がスローされる。
	 * <ul>
	 * <li>ハットキーが {@link ExBase#HAT}、{@link ExBase#NO_HAT}、もしくはワイルドカード以外
	 * </ul>
	 */
	public ExBasePattern(String nameKey, String hatKey, String[] extendedKeys) {
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
	 * 指定された名前キー、ハットキーから <code>ExBasePattern</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * 
	 * @param nameKey	名前キー
	 * @param hatKey	交換代数基底のハットキー。指定可能な値は、{@link ExBase#NO_HAT}、{@link ExBase#HAT}、
	 * 					もしくはワイルドーカード。
	 * 
	 * @throws IllegalArgumentException 次の場合、この例外がスローされる。
	 * <ul>
	 * <li>ハットキーが {@link ExBase#HAT}、{@link ExBase#NO_HAT}、もしくはワイルドカード以外
	 * </ul>
	 */
	public ExBasePattern(String nameKey, String hatKey) {
		super();
		initialize(new String[]{nameKey, hatKey});
	}

	/**
	 * 指定されたキーを持つ <code>ExBasePattern</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * 
	 * @param nameKey	名前キー
	 * @param hatKey	交換代数基底のハットキー。指定可能な値は、{@link ExBase#NO_HAT}、{@link ExBase#HAT}、
	 * 					もしくはワイルドーカード。
	 * @param unitKey	単位キー
	 * 
	 * @throws IllegalArgumentException 次の場合、この例外がスローされる。
	 * <ul>
	 * <li>ハットキーが {@link ExBase#HAT}、{@link ExBase#NO_HAT}、もしくはワイルドカード以外
	 * </ul>
	 */
	public ExBasePattern(String nameKey, String hatKey, String unitKey) {
		super();
		initialize(new String[]{nameKey, hatKey, unitKey});
	}

	/**
	 * 指定されたキーを持つ <code>ExBasePattern</code> の新しいインスタンスを生成する。
	 * <br>
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * 
	 * @param nameKey	名前キー
	 * @param hatKey	交換代数基底のハットキー。指定可能な値は、{@link ExBase#NO_HAT}、{@link ExBase#HAT}、
	 * 					もしくはワイルドーカード。
	 * @param unitKey	単位キー
	 * @param timeKey	時間キー
	 * 
	 * @throws IllegalArgumentException 次の場合、この例外がスローされる。
	 * <ul>
	 * <li>ハットキーが {@link ExBase#HAT}、{@link ExBase#NO_HAT}、もしくはワイルドカード以外
	 * </ul>
	 */
	public ExBasePattern(String nameKey, String hatKey, String unitKey, String timeKey) {
		super();
		initialize(new String[]{nameKey, hatKey, unitKey, timeKey});
	}

	/**
	 * 指定されたキーを持つ <code>ExBasePattern</code> の新しいインスタンスを生成する。
	 * 省略された基底キーには、ワイルドカードが代入される。
	 * 
	 * @param nameKey	名前キー
	 * @param hatKey	交換代数基底のハットキー。指定可能な値は、{@link ExBase#NO_HAT}、{@link ExBase#HAT}、
	 * 					もしくはワイルドーカード。
	 * @param unitKey	単位キー
	 * @param timeKey	時間キー
	 * @param subjectKey	サブジェクトキー
	 * 
	 * @throws IllegalArgumentException 次の場合、この例外がスローされる。
	 * <ul>
	 * <li>ハットキーが {@link ExBase#HAT}、{@link ExBase#NO_HAT}、もしくはワイルドカード以外
	 * </ul>
	 */
	public ExBasePattern(String nameKey, String hatKey, String unitKey, String timeKey, String subjectKey) {
		super();
		initialize(new String[]{nameKey, hatKey, unitKey, timeKey, subjectKey});
	}

	/**
	 * 指定された基底のキーを持つ <code>ExBasePattern</code> の新しいインスタンスを生成する。
	 * 基本的に各基底キーの文字列をそのまま継承する。基底キーに含まれるアスタリスク文字は
	 * ワイルドカードとみなされる。
	 * 
	 * @param base			交換代数基底
	 * @param ignoreHatKey	<tt>true</tt>の場合、ハットキーはワイルドカードに置き換えられる。
	 * 
	 * @throws IllegalArgumentException 次の場合、この例外がスローされる。
	 * <ul>
	 * <li>ハットキーが {@link ExBase#HAT}、{@link ExBase#NO_HAT}、もしくはワイルドカード以外
	 * </ul>
	 * 
	 * @since 0.94
	 */
	public ExBasePattern(ExBase base, boolean ignoreHatKey) {
		super();
		String[] keys = base._baseKeys.clone();
		if (ignoreHatKey) {
			keys[KEY_HAT] = WILDCARD;
		}
		initialize(keys);
	}

	/**
	 * 全ての基底キーがワイルドカードとなる <code>ExBasePattern</code> の新しいインスタンスを生成する。
	 *
	 * @since 0.982
	 */
	protected ExBasePattern() {
		fillBaseKeys(WILDCARD);
		this.patternItems = null;
	}

	/**
	 * <code>ExBasePattern</code> クラスの初期化。<br>
	 * 指定された基底キーにより、基底を初期化する。
	 * 
	 * @param keys	交換代数の基底となるキーの配列。
	 * 				<tt>null</tt>、長さ 0、の場合は、ワイルドカードが代入される。
	 * 
	 * @throws IllegalArgumentException 次の場合、この例外がスローされる。
	 * <ul>
	 * <li>ハットキーが {@link ExBase#HAT}、{@link ExBase#NO_HAT}、もしくはワイルドカード以外
	 * <li>キーに不正な文字が含まれている場合
	 * </ul>
	 */
	private void initialize(String[] keys) {
		// Check hat keys
		if (keys != null && keys.length >= ExBase.NUM_BASIC_KEYS) {
			// ハットキーが省略されている場合は、チェック対象外(ワイルドカードにする)
			if (keys[KEY_HAT] != null && keys[KEY_HAT].length() > 0 && !isValidHatKey(keys[KEY_HAT])) {
				// ハットキーに有効な文字列が定義されているが、
				// HAT でも NO_HAT でも ワイルドカードでもない
				throw new IllegalArgumentException("hat key is illegal");
			}
		}
		
		// 基底キー文字列が有効文字列かを検証する
		int maxKeys = Math.min(keys.length, NUM_ALL_KEYS);
		for (int i = 0; i < maxKeys; i++) {
			if (keys[i] != null && keys[i].length() > 0 && i != KEY_HAT) {
				// 基底キーとして無効な文字が含まれている場合は、エラーとする
				/*--- modified : 2009/03/10 ---*/
				/*---@@@ old codes @@@---
				if (!ExBasePattern.Util.getAllowedBaseKeyPattern().matcher(keys[i]).matches()) {
					String msg = "Illegal character is included in " + getKeyName(i) + ".";
					throw new IllegalArgumentException(msg);
				}
				---@@@ end of old codes @@@---*/
				if (ExBasePattern.Util._illegalCandidates.isIncluded(keys[i])) {
					String msg = "Illegal character is included in " + getKeyName(i) + ".";
					throw new IllegalArgumentException(msg);
				}
				/*--- end of modified : 2009/03/10 ---*/
			}
		}
		
		// create keys, default key is wildcard('*')
		initializeBaseKeys(keys, WILDCARD);
		
		// create maching pattern
		updateMatchingPattern();
	}

	/**
	 * 現在の基底キーにより、マッチングパターンを更新する。
	 */
	private void updateMatchingPattern() {
		this.patternItems = makePattern(_baseKeys);
	}

	/**
	 * 基底キーの配列から、基底パターンの内部形式を生成する。
	 * この内部形式の基底パターンは、<code>PatternItem</code> の配列であり、
	 * 基底キーがワイルドカードのみのものはこのパターンに含まれない。
	 * また、全ての基底キーがワイルドカードの場合は <tt>null</tt> となる。
	 * @param baseKeys	基底キー配列(整形済みのもの)
	 * @return	内部形式の基底パターン
	 * 
	 * @since 0.970
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
	 * 基底キーの配列から、基底パターンの内部形式を生成する。
	 * この内部形式の基底パターンは、<code>PatternItem</code> の配列であり、
	 * 基底キーがワイルドカードのみのものはこのパターンに含まれない。
	 * また、全ての基底キーがワイルドカードの場合は <tt>null</tt> となる。
	 * このメソッドでは、ハットキーはワイルドカードのみのパターンとして、
	 * 指定された基底キー配列のハットキーは無視される。
	 * 
	 * @param baseKeys	基底キー配列(整形済みのもの)
	 * @return	内部形式の基底パターン
	 * 
	 * @since 0.970
	 */
	static protected PatternItem[] makePatternWithoutHatKey(String[] baseKeys) {
		ArrayList<PatternItem> items = new ArrayList<PatternItem>(baseKeys.length);
		
		//--- name key
		PatternItem item = new PatternItem(KEY_NAME, baseKeys[KEY_NAME]);
		if (item.getPattern() != null) {
			items.add(item);
		}
		
		//--- skip hat key
		
		//--- extended keys
		for (int i = NUM_BASIC_KEYS; i < baseKeys.length; i++) {
			item = new PatternItem(i, baseKeys[i]);
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
	 * @since 0.970
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
	 * 基底キーパターンにより、指定された基底を変換する。
	 * 基底キーパターンによる変換では、基底キーパターンにワイルドカードが含まれていない場合は
	 * 基底キーパターンの基底キー文字列、ワイルドカードが含まれている場合は指定された基底の
	 * 基底キー文字列によって、新しい基底を生成する。
	 * <p>
	 * 基底キーパターンによる変換において、ワイルドカードの数や位置は考慮しない。
	 * 
	 * @param patternBaseKeys	基底キーパターンの配列(整形済みのもの)
	 * @param exbase			変換する基底
	 * @return	変換後の新しい基底
	 * 
	 * @since 0.970
	 */
	static protected ExBase translateBaseKey(String[] patternBaseKeys, ExBase exbase) {
		ExBase newBase = new ExBase(exbase);
		
		for (int i = 0; i < patternBaseKeys.length; i++) {
			if (patternBaseKeys[i].indexOf(WILDCARD_CHAR) < 0) {
				//--- ワイルドカードを含まないパターンは、パターンの基底キーを代入(置き換える)
				newBase._baseKeys[i] = patternBaseKeys[i];
			}
		}
		
		newBase.setupStatus();
		return newBase;
	}
	
	/**
	 * 基底キーパターンにより、指定された基底を変換する。
	 * 基底キーパターンによる変換では、基底キーパターンにワイルドカードが含まれていない場合は
	 * 基底キーパターンの基底キー文字列、ワイルドカードが含まれている場合は指定された基底の
	 * 基底キー文字列によって、新しい基底を生成する。<br>
	 * このメソッドでは、ハット基底キーをワイルドカードとみなす。
	 * <p>
	 * 基底キーパターンによる変換において、ワイルドカードの数や位置は考慮しない。
	 * 
	 * @param patternBaseKeys	基底キーパターンの配列(整形済みのもの)
	 * @param exbase			変換する基底
	 * @return	変換後の新しい基底
	 * 
	 * @since 0.970
	 */
	static protected ExBase translateBaseKeyWithoutHatKey(String[] patternBaseKeys, ExBase exbase) {
		ExBase newBase = new ExBase(exbase);
		
		// name key
		if (patternBaseKeys[KEY_NAME].indexOf(WILDCARD_CHAR) < 0) {
			//--- ワイルドカードを含まないパターンは、パターンの基底キーを代入(置き換える)
			newBase._baseKeys[KEY_NAME] = patternBaseKeys[KEY_NAME];
		}
		
		// skip hat key
		
		// other keys
		for (int i = NUM_BASIC_KEYS; i < patternBaseKeys.length; i++) {
			if (patternBaseKeys[i].indexOf(WILDCARD_CHAR) < 0) {
				//--- ワイルドカードを含まないパターンは、パターンの基底キーを代入(置き換える)
				newBase._baseKeys[i] = patternBaseKeys[i];
			}
		}
		
		newBase.setupStatus();
		return newBase;
		
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された交換代数基底を、ハットキーをワイルドカードとする基底パターンに変換する。
	 * @param base	変換する交換代数基底
	 * @return 変換した <code>ExBasePattern</code> を返す。
	 * 指定された <code>base</code> が <tt>null</tt> の場合は <tt>null</tt> を返す。
	 * 
	 * @since 0.982
	 */
	static public final ExBasePattern toPattern(ExBase base) {
		return (base==null ? null : new ExBasePattern(base, true));
	}

	/**
	 * ハット基底キーがワイルドカードであることを示す。
	 * @return ハット基底キーがワイルドカードであれば、true を返す
	 * 
	 * @since 0.982
	 */
	public boolean isWildcardHat() {
		return ExBasePattern.WILDCARD.equals(this._baseKeys[KEY_HAT]);
	}

	/**
	 * 基底パターンをハット基底パターンにする。
	 * <p>このメソッドは、基底パターンのハットキーを {@link ExBase#HAT} にした基底を返す。
	 * ハットキーがすでに {@link ExBase#HAT} のものは、そのまま返す。
	 * ハットキーが {@link #WILDCARD} の場合も、{@link ExBase#HAT} にする。
	 * 
	 * @return	ハット基底パターン
	 * 
	 * @since 0.960
	 */
	public final ExBasePattern setHat() {
		if (_baseKeys[KEY_HAT].equals(ExBase.HAT)) {
			return this;
		} else {
			return new ExBasePattern(_baseKeys[KEY_NAME], ExBase.HAT,
										_baseKeys[KEY_EXT_UNIT],
										_baseKeys[KEY_EXT_TIME],
										_baseKeys[KEY_EXT_SUBJECT]);
		}
	}

	/**
	 * 基底パターンをハットなし基底パターンにする。
	 * <p>このメソッドは、基底パターンのハットキーを {@link ExBase#NO_HAT} にした基底を返す。
	 * ハットキーがすでに {@link ExBase#NO_HAT} のものは、そのまま返す。
	 * ハットキーが {@link #WILDCARD} の場合も、{@link ExBase#NO_HAT} にする。
	 * @return ハットなし基底パターン
	 * @since 0.960
	 */
	public final ExBasePattern setNoHat() {
		if (_baseKeys[KEY_HAT].equals(ExBase.NO_HAT)) {
			return this;
		} else {
			return new ExBasePattern(_baseKeys[KEY_NAME], ExBase.NO_HAT,
										_baseKeys[KEY_EXT_UNIT],
										_baseKeys[KEY_EXT_TIME],
										_baseKeys[KEY_EXT_SUBJECT]);
		}
	}

	/**
	 * 基底パターンのハットキーがワイルドカードとなる基底パターンにする。
	 * <p>このメソッドは、基底パターンのハットキーを {@link #WILDCARD} にした基底を返す。
	 * ハットキーがすでに {@link #WILDCARD} のものは、そのまま返す。
	 * @return 基底キーがワイルドカードの基底パターン
	 * @since 0.960
	 */
	public final ExBasePattern setWildcardHat() {
		if (_baseKeys[KEY_HAT].equals(WILDCARD)) {
			return this;
		} else {
			return new ExBasePattern(_baseKeys[KEY_NAME], WILDCARD,
										_baseKeys[KEY_EXT_UNIT],
										_baseKeys[KEY_EXT_TIME],
										_baseKeys[KEY_EXT_SUBJECT]);
		}
	}

	/**
	 * 指定の基底がこの基底パターンにマッチするかどうかを判定する。
	 * 
	 * @param exbase 一致を判定する交換代数基底
	 * 
	 * @return このパターンと指定の基底が一致するときだけ true を返す
	 */
	public boolean matches(ExBase exbase) {
		if (exbase == null)
			return false;

		/*--- modified : 2009/03/10 ---*/
		/*--- @@@ old codes @@@ ---
		if (this.machingPattern != null) {
			// exbase.key() と、正規表現パターンが一致するかを判定する
			return this.machingPattern.matcher(exbase.key()).matches();
		}
		else {
			// 完全一致かを判定する
			return exbase.equals(this);
		}
		--- @@@ end of oldCodes @@@---*/
		return matchesByPattern(patternItems, exbase._baseKeys);
		/*--- end of modified : 2009/03/10 ---*/
	}

	/**
	 * 現在のパターンで、指定の基底を変換する。
	 * <br>
	 * ワイルドカードが指定されていない基底キーは、パターンの基底キーに置き換わる。
	 * <br>
	 * ワイルドカードが指定されている基底キーは、指定された基底キーのままとなる。
	 * <p>
	 * この処理では、基底キーにワイルドカードとワイルドカード以外の文字が混在している
	 * キーが含まれている場合、ワイルドカード指定とみなして、変換を行わない。
	 * 
	 * @param exbase 変換元の基底
	 * @return 変換後の基底
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public ExBase translate(ExBase exbase) {
		/*--- modified : 2009/03/10 ---*/
		/*--- @@@ old codes @@@ ---
		String[] newKeys = new String[NUM_ALL_KEYS];
		for (int i = 0; i < this._baseKeys.length; i++) {
			if (this._baseKeys[i].indexOf(WILDCARD) >= 0) {
				// ワイルドカード指定(置き換えない)
				newKeys[i] = exbase._baseKeys[i];
			}
			else {
				// ワイルドカードなし(置き換える)
				newKeys[i] = this._baseKeys[i];
			}
		}
		
		return new ExBase(newKeys);
		--- @@@ end of old codes @@@---*/
		return translateBaseKey(this._baseKeys, exbase);
		/*--- end of modified : 2009/03/10 ---*/
	}

	//------------------------------------------------------------
	// Comparable interfaces
	//------------------------------------------------------------

	/**
	 * 2 つの基底パターンを辞書的に比較する。<br>
	 * 比較は、格納されている基底キーの順序で、各基底キー文字列を
	 * 辞書的に比較する。つまり、最初の基底キーから順に比較を行い、
	 * 一致しない基底キーが出現した時点で、その結果を返す。
	 * 全ての基底キーが一致した場合の結果は 0 となる。
	 * <br>
	 * 辞書的な文字列の比較は、{@link java.lang.String#compareTo(String)} の
	 * 実装による比較結果となる。
	 * 
	 * @param basePattern 比較対象の基底パターン
	 * @return 引数の基底パターンがこの基底パターンと等しい場合は、値 0。
	 * 			この基底パターンが引数の基底パターンより辞書的に小さい場合は、0 より小さい値。
	 * 			この基底パターンが引数の基底パターンより辞書的に大きい場合は、0 より大きい値。
	 * 
	 * @see java.lang.String#compareTo(String)
	 * 
	 * @since 0.93
	 * 
	 */
	public int compareTo(ExBasePattern basePattern) {
		return super.baseCompareTo(basePattern);
	}

	/**
	 * 大文字と小文字の区別なしで、2 つの基底パターンを辞書的に比較する。<br>
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
	 * @param basePattern 比較対象の基底パターン
	 * @return 大文字と小文字の区別なしで、引数の基底パターンがこの基底パターンと等しい場合は、値 0。
	 * 			大文字と小文字の区別なしで、この基底パターンが引数の基底パターンより小さい場合は負の整数、
	 * 			この基底パターンが引数の基底パターンより大きい場合は正の整数。
	 * 
	 * @see java.lang.String#compareToIgnoreCase(String)
	 * 
	 * @since 0.93
	 * 
	 */
	public int compareToIgnoreCase(ExBasePattern basePattern) {
		return super.baseCompareToIgnoreCase(basePattern);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/*--- removed : 2009/03/10 ---
	protected Pattern getMatchingPattern() {
		return this.machingPattern;
	}
	--- end of removed : 2009/03/10 ---*/

	/**
	 * このインスタンスのマッチングパターンを取得する。
	 * @return マッチングパターン
	 * 
	 * @since 0.970
	 */
	protected PatternItem[] getPatternItems() {
		return this.patternItems;
	}
	
	private boolean isValidHatKey(String hatKey) {
		if (ExBase.HAT.equals(hatKey))
			return true;	// 正等
		else if (ExBase.NO_HAT.equals(hatKey))
			return true;	// 正等
		else if (WILDCARD.equals(hatKey))
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
	 * <li>全ての拡張基底キー
	 * </ol>
	 * 
	 * @param writer 出力先となる <code>CsvWriter</code> オブジェクト
	 * 
	 * @throws IOException 書き込みに失敗した場合にスローされる
	 * 
	 * @since 0.91
	 * 
	 * @deprecated このメソッドは削除されます。新しい {@link #writeFieldToCSV(CsvWriter, boolean)} を使用してください。
	 */
	protected void writeToCSV(CsvWriter writer) throws IOException
	{
		// ハットキーは出力しない
		
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
	 * 指定された <code>writer</code> の現在位置へ、基底パターンデータを出力する。
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
	 * @param withHatKey	ハットキーを出力する場合は <tt>true</tt>、出力しない場合は <tt>false</tt> を指定する。
	 * 
	 * @throws IOException	書き込みに失敗した場合
	 * 
	 * @since 0.982
	 */
	protected void writeFieldToCSV(CsvWriter writer, boolean withHatKey) throws IOException
	{
		// hat key
		if (withHatKey) {
			writer.writeField(getHatKey());
		}
		
		// name key
		writer.writeField(getNameKey());
		
		// unit key
		writer.writeField(getUnitKey());
		
		// time key
		writer.writeField(getTimeKey());
		
		// subject key
		writer.writeField(getSubjectKey());
	}
	
	/**
	 * 指定された <code>reader</code> の現在位置から、基底パターンデータを取得する。
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
	 * ハットキーは、ハットキーとして有効な文字列でもワイルドカードでもない場合はフォーマットエラーとなる。
	 * また、ハットキー以外の基底キーは、基底キーとして有効な文字列ではない場合にフォーマットエラーとなる。
	 * <p>
	 * 読み込みにおいて、全ての基底キーは省略可能であり、省略されている場合は
	 * ワイルドカード({@link #WILDCARD})に置き換えられる。
	 * <br>
	 * ハットキーが読み込み対象から除外された場合、ハットキーはワイルドカードとなる。
	 * 
	 * @param reader	読み込み対象のリーダー
	 * @param withHatKey	ハットキーも読み込み対象とする場合は <tt>true</tt>、ハットキーを読み込み対象から除外する場合は <tt>false</tt> を指定する。
	 * 
	 * @return 読み込まれた値から生成された <code>ExBasePattern</code> オブジェクト
	 * 
	 * @throws NullPointerException <code>reader</code> が <tt>null</tt> の場合
	 * @throws IOException 読み込み時エラーが発生したとき
	 * @throws CsvFormatException フィールドのデータが正しくない場合
	 * 
	 * @since 0.982
	 */
	static protected ExBasePattern readFieldFromCSV(CsvReader.CsvFieldReader reader, boolean withHatKey) throws IOException, CsvFormatException
	{
		String strInput;
		
		// hat key
		String hatKey = WILDCARD;
		if (withHatKey) {
			strInput = reader.readTrimmedValue();
			hatKey = ExBasePattern.Util.filterHatKey(strInput, true);
			if (hatKey == null) {
				throw new CsvFormatException(formatIllegalKeyMessage("hat", strInput), reader.getLineNo(), reader.getNextPosition());
			}
		}
		
		// name key
		strInput = reader.readTrimmedValue();
		String nameKey = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (nameKey == null) {
			throw new CsvFormatException(formatIllegalKeyMessage("name", strInput), reader.getLineNo(), reader.getNextPosition());
		}
		
		// unit key
		strInput = reader.readTrimmedValue();
		String unitKey = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (unitKey == null) {
			throw new CsvFormatException(formatIllegalKeyMessage("unit", strInput), reader.getLineNo(), reader.getNextPosition());
		}
		
		// time key
		strInput = reader.readTrimmedValue();
		String timeKey = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (timeKey == null) {
			throw new CsvFormatException(formatIllegalKeyMessage("time", strInput), reader.getLineNo(), reader.getNextPosition());
		}
		
		// subject key
		strInput = reader.readTrimmedValue();
		String subjectKey = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (subjectKey == null) {
			throw new CsvFormatException(formatIllegalKeyMessage("subject", strInput), reader.getLineNo(), reader.getNextPosition());
		}
		
		// make ExBasePattern instance
		ExBasePattern rPattern = new ExBasePattern();
		rPattern._baseKeys[KEY_HAT] = hatKey;
		rPattern._baseKeys[KEY_NAME] = nameKey;
		rPattern._baseKeys[KEY_EXT_UNIT] = unitKey;
		rPattern._baseKeys[KEY_EXT_TIME] = timeKey;
		rPattern._baseKeys[KEY_EXT_SUBJECT] = subjectKey;
		rPattern.setupStatus();
		rPattern.updateMatchingPattern();
		
		return rPattern;
	}

	/**
	 * 指定された <code>reader</code> の現在位置から、基底データを取得する。
	 * <br>
	 * 各基底キーは、現在のカラム位置から次の順序で読み込まれる。
	 * <ol type="1" start="1">
	 * <li>名前キー
	 * <li>全ての拡張基底キー
	 * </ol>
	 * <p>
	 * 基底キーは、{@link ExBasePattern.Util#getAllowedBaseKeyPattern()} で
	 * 定義されているマッチングパターンと一致しなければ、フォーマットエラーとなる。
	 * <p>
	 * 読み込みにおいて、全ての基底キーは省略可能であり、省略されている場合は
	 * ワイルドカード({@link #WILDCARD})に置き換えられる。
	 * <br>
	 * また、ハットキーもワイルドカードに置き換えられる。
	 * 
	 * @param reader 入力元となる <code>CsvRecordReader</code> オブジェクト
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws IOException 読み込み時エラーが発生したときにスローされる
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.91
	 * 
	 * @deprecated このメソッドは削除されます。新しい {@link #readFieldFromCSV(exalge2.io.csv.CsvReader.CsvFieldReader, boolean)} を使用してください。
	 */
	protected void readFromCSV(CsvRecordReader reader)
		throws IOException, CsvFormatException
	{
		/*--- modified : 2009/03/10 ---*/
		/*--- @@@ old codes @@@---
		String strInput;
		
		// ハットキーは存在しない
		this._baseKeys[KEY_HAT] = ExBasePattern.WILDCARD;
		
		// 名前キー
		strInput = reader.readTrimmedStringColumn(ExBasePattern.Util.getAllowedBaseKeyPattern(), true);
		if (strInput == null) {
			strInput = ExBasePattern.WILDCARD;
		}
		this._baseKeys[KEY_NAME] = strInput;
		
		// 単位キー
		strInput = reader.readTrimmedStringColumn(ExBasePattern.Util.getAllowedBaseKeyPattern(), true);
		if (strInput == null) {
			strInput = ExBasePattern.WILDCARD;
		}
		this._baseKeys[KEY_EXT_UNIT] = strInput;
		
		// 時間キー
		strInput = reader.readTrimmedStringColumn(ExBasePattern.Util.getAllowedBaseKeyPattern(), true);
		if (strInput == null) {
			strInput = ExBasePattern.WILDCARD;
		}
		this._baseKeys[KEY_EXT_TIME] = strInput;
		
		// サブジェクトキー
		strInput = reader.readTrimmedStringColumn(ExBasePattern.Util.getAllowedBaseKeyPattern(), true);
		if (strInput == null) {
			strInput = ExBasePattern.WILDCARD;
		}
		this._baseKeys[KEY_EXT_SUBJECT] = strInput;
		
		// 内部情報更新
		updateOneStringKey();
		updateMatchingPattern();
		--- @@@ end of old codes @@@ ---*/
		String strInput;
		String strValue;
		
		// ハットキーは存在しない
		this._baseKeys[KEY_HAT] = ExBasePattern.WILDCARD;
		
		// 名前キー
		strInput = reader.readTrimmedStringColumn(true);
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			throw new CsvFormatException(strInput, CsvFormatException.ILLEGAL_COLUMN_PATTERN, reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_NAME] = strValue;
		
		// 単位キー
		strInput = reader.readTrimmedStringColumn(true);
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			throw new CsvFormatException(strInput, CsvFormatException.ILLEGAL_COLUMN_PATTERN, reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_EXT_UNIT] = strValue;
		
		// 時間キー
		strInput = reader.readTrimmedStringColumn(true);
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			throw new CsvFormatException(strInput, CsvFormatException.ILLEGAL_COLUMN_PATTERN, reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_EXT_TIME] = strValue;
		
		// 主体キー
		strInput = reader.readTrimmedStringColumn(true);
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			throw new CsvFormatException(strInput, CsvFormatException.ILLEGAL_COLUMN_PATTERN, reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_EXT_SUBJECT] = strValue;
		
		// 内部情報更新
		setupStatus();
		updateMatchingPattern();
		/*--- end of modified : 2009/03/10 ---*/
	}
	
	/**
	 * 指定された <code>reader</code> の現在位置から、基底データを取得する。
	 * <br>
	 * 各基底キーは、現在のカラム位置から次の順序で読み込まれる。
	 * <ol type="1" start="1">
	 * <li>名前キー
	 * <li>全ての拡張基底キー
	 * </ol>
	 * <p>
	 * 基底キーは、{@link ExBasePattern.Util#getAllowedBaseKeyPattern()} で
	 * 定義されているマッチングパターンと一致しなければ、フォーマットエラーとなる。
	 * <p>
	 * 読み込みにおいて、全ての基底キーは省略可能であり、省略されている場合は
	 * ワイルドカード({@link #WILDCARD})に置き換えられる。
	 * <br>
	 * また、ハットキーもワイルドカードに置き換えられる。
	 * <p>
	 * このメソッドでは、変換先基底パターンを読み込むメソッドであり、
	 * 変換先パターンとして、1つ以上のワイルドカードとワイルドカード以外の文字が
	 * 組み合わされている基底キー文字列は不正な基底キーとして判定する。
	 * 
	 * @param reader 入力元となる <code>CsvRecordReader</code> オブジェクト
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws IOException 読み込み時エラーが発生したときにスローされる
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.91
	 * 
	 * @deprecated このメソッドは削除されます。新しい {@link #readFieldFromCSV(exalge2.io.csv.CsvReader.CsvFieldReader, boolean)} を使用してください。
	 */
	protected void readFromCSVForTransToPattern(CsvRecordReader reader)
		throws IOException, CsvFormatException
	{
		/*--- modified : 2009/03/10 ---*/
		/*---@@@ old codes @@@---
		String strInput;
		
		// ハットキーは存在しない
		this._baseKeys[KEY_HAT] = ExBasePattern.WILDCARD;
		
		// 名前キー
		strInput = reader.readTrimmedStringColumn(ExBasePattern.Util.getAllowedBaseKeyPattern(), true);
		if (strInput == null) {
			strInput = ExBasePattern.WILDCARD;
		}
		else if (!ExBasePattern.Util.isAllowBaseKeyForTransToPattern(strInput)) {
			throw new CsvFormatException(CsvFormatException.ILLEGAL_DIR_TO_WILDCARD_PATTERN,
					reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_NAME] = strInput;
		
		// 単位キー
		strInput = reader.readTrimmedStringColumn(ExBasePattern.Util.getAllowedBaseKeyPattern(), true);
		if (strInput == null) {
			strInput = ExBasePattern.WILDCARD;
		}
		else if (!ExBasePattern.Util.isAllowBaseKeyForTransToPattern(strInput)) {
			throw new CsvFormatException(CsvFormatException.ILLEGAL_DIR_TO_WILDCARD_PATTERN,
					reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_EXT_UNIT] = strInput;
		
		// 時間キー
		strInput = reader.readTrimmedStringColumn(ExBasePattern.Util.getAllowedBaseKeyPattern(), true);
		if (strInput == null) {
			strInput = ExBasePattern.WILDCARD;
		}
		else if (!ExBasePattern.Util.isAllowBaseKeyForTransToPattern(strInput)) {
			throw new CsvFormatException(CsvFormatException.ILLEGAL_DIR_TO_WILDCARD_PATTERN,
					reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_EXT_TIME] = strInput;
		
		// サブジェクトキー
		strInput = reader.readTrimmedStringColumn(ExBasePattern.Util.getAllowedBaseKeyPattern(), true);
		if (strInput == null) {
			strInput = ExBasePattern.WILDCARD;
		}
		else if (!ExBasePattern.Util.isAllowBaseKeyForTransToPattern(strInput)) {
			throw new CsvFormatException(CsvFormatException.ILLEGAL_DIR_TO_WILDCARD_PATTERN,
					reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_EXT_SUBJECT] = strInput;
		
		// 内部情報更新
		updateOneStringKey();
		updateMatchingPattern();
		---@@@ end of old codes @@@---*/
		String strInput;
		String strValue;
		
		// ハットキーは存在しない
		this._baseKeys[KEY_HAT] = ExBasePattern.WILDCARD;
		
		// 名前キー
		strInput = reader.readTrimmedStringColumn(true);
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			throw new CsvFormatException(strInput, CsvFormatException.ILLEGAL_COLUMN_PATTERN, reader.getLineNumber(), reader.getColumnNumber());
		}
		else if (!ExBasePattern.Util.isAllowBaseKeyForTransToPattern(strValue)) {
			throw new CsvFormatException(CsvFormatException.ILLEGAL_DIR_TO_WILDCARD_PATTERN, reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_NAME] = strValue;
		
		// 単位キー
		strInput = reader.readTrimmedStringColumn(true);
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			throw new CsvFormatException(strInput, CsvFormatException.ILLEGAL_COLUMN_PATTERN, reader.getLineNumber(), reader.getColumnNumber());
		}
		else if (!ExBasePattern.Util.isAllowBaseKeyForTransToPattern(strValue)) {
			throw new CsvFormatException(CsvFormatException.ILLEGAL_DIR_TO_WILDCARD_PATTERN, reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_EXT_UNIT] = strValue;
		
		// 時間キー
		strInput = reader.readTrimmedStringColumn(true);
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			throw new CsvFormatException(strInput, CsvFormatException.ILLEGAL_COLUMN_PATTERN, reader.getLineNumber(), reader.getColumnNumber());
		}
		else if (!ExBasePattern.Util.isAllowBaseKeyForTransToPattern(strValue)) {
			throw new CsvFormatException(CsvFormatException.ILLEGAL_DIR_TO_WILDCARD_PATTERN, reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_EXT_TIME] = strValue;
		
		// 主体キー
		strInput = reader.readTrimmedStringColumn(true);
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			throw new CsvFormatException(strInput, CsvFormatException.ILLEGAL_COLUMN_PATTERN, reader.getLineNumber(), reader.getColumnNumber());
		}
		else if (!ExBasePattern.Util.isAllowBaseKeyForTransToPattern(strValue)) {
			throw new CsvFormatException(CsvFormatException.ILLEGAL_DIR_TO_WILDCARD_PATTERN, reader.getLineNumber(), reader.getColumnNumber());
		}
		this._baseKeys[KEY_EXT_SUBJECT] = strValue;
		
		// 内部情報更新
		setupStatus();
		updateMatchingPattern();
		/*--- end of modified : 2009/03/10 ---*/
	}
	
	/**
	 * 基底データを、XML ノードに変換する。
	 * <br>
	 * 変換されるノードの構成は、次のようになる。
	 * <blockquote>
	 * &lt;Exbase
	 *  name=&quote;<i>nameKey</i>&quote;
	 *  unit=&quote;<i>unitKey</i>&quote;
	 *  time=&quote;<i>timeKey</i>&quote;
	 *  subject=&quote;<i>subjectKey</i>&quote; /&gt;
	 * </blockquote>
	 * <br>
	 * <b>(注)</b>ハットキーは省略される。
	 * 
	 * @param xmlDocument ノード生成に使用する {@link XmlDocument} インスタンス
	 * @return 生成された XML ノード
	 * 
	 * @throws DOMException ノード生成に失敗した場合
	 * 
	 * @since 0.91
	 * 
	 * @deprecated このメソッドは削除されます。新しい {@link #encodeToDomElement(XmlDocument, boolean)} を使用してください。
	 */
	protected Element encodeToElement(XmlDocument xmlDocument) throws DOMException
	{
		Element node = xmlDocument.createElement(XML_ELEMENT_NAME);
		
		// ハットキーは省略
		//node.setAttribute(XML_ATTR_HATKEY, getHatKey());
		
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
	 * &lt;Exbase
	 *  name=&quote;<i>nameKey</i>&quote;
	 *  unit=&quote;<i>unitKey</i>&quote;
	 *  time=&quote;<i>timeKey</i>&quote;
	 *  subject=&quote;<i>subjectKey</i>&quote; /&gt;
	 * </blockquote>
	 * <b>(注)</b>ハットキーは無視される。
	 * 基底キーは、{@link ExBasePattern.Util#getAllowedBaseKeyPattern()} で
	 * 定義されているマッチングパターンと一致しなければ、フォーマットエラーとなる。
	 * <p>
	 * 読み込みにおいて、全ての基底キーは省略可能であり、省略されている場合は
	 * ワイルドカード({@link #WILDCARD})に置き換えられる。
	 * <br>
	 * また、ハットキーもワイルドカードに置き換えられる。
	 * 
	 * @param xmlDocument ノードが所属する {@link XmlDocument} インスタンス
	 * @param xmlElement 解析対象の XML ノード
	 * 
	 * @throws XmlDomParseException ノード解析エラーが発生した場合
	 * 
	 * @since 0.91
	 * 
	 * @deprecated このメソッドは削除されます。新しい {@link #decodeFromDomElement(XmlDocument, Element, boolean)} を使用してください。
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
		
		// ハットキーは存在しない
		this._baseKeys[KEY_HAT] = ExBasePattern.WILDCARD;
		
		// 名前キー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_NAMEKEY));
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			// Illegal name key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_NAMEKEY, strInput);
		}
		this._baseKeys[KEY_NAME] = strValue;
		
		// 単位キー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_UNITKEY));
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			// Illegal name key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_UNITKEY, strInput);
		}
		this._baseKeys[KEY_EXT_UNIT] = strValue;
		
		// 時間キー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_TIMEKEY));
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			// Illegal name key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_TIMEKEY, strInput);
		}
		this._baseKeys[KEY_EXT_TIME] = strValue;
		
		// サブジェクトキー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_SUBJECTKEY));
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			// Illegal name key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_SUBJECTKEY, strInput);
		}
		this._baseKeys[KEY_EXT_SUBJECT] = strValue;
		
		// 内部情報更新
		/*--- modified : 2009/03/10 ---*/
		/*---@@@ old codes @@@---
		updateOneStringKey();
		---@@@ end of old codes @@@---*/
		setupStatus();
		/*--- end of modified : 2009/03/10 ---*/
		updateMatchingPattern();
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
	 * <br>
	 * ハットキーがエンコード対象から除外された場合、ハットキーはエンコードされない。
	 * 
	 * @param xmlDocument ノード生成に使用する {@link XmlDocument} インスタンス
	 * @param withHatKey	ハットキーをエンコードする場合は <tt>true</tt>、エンコードしない場合は <tt>false</tt> を指定する。
	 * @return 生成された XML ノード
	 * 
	 * @throws DOMException ノード生成に失敗した場合
	 * 
	 * @since 0.982
	 */
	protected Element encodeToDomElement(XmlDocument xmlDocument, boolean withHatKey) throws DOMException
	{
		Element node = xmlDocument.createElement(XML_ELEMENT_NAME);
		
		// ハットキー
		if (withHatKey) {
			node.setAttribute(XML_ATTR_HATKEY, getHatKey());
		}
		
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
	 * ハットキーは、ハットキーとして有効な文字列でもワイルドカードでもない場合はフォーマットエラーとなる。
	 * また、ハットキー以外の基底キーは、基底キーとして有効な文字列ではない場合にフォーマットエラーとなる。
	 * <p>
	 * 読み込みにおいて、全ての基底キーは省略可能であり、省略されている場合は
	 * ワイルドカード({@link #WILDCARD})に置き換えられる。
	 * <br>
	 * ハットキーが解析対象から除外された場合、ハットキーはワイルドカードとなる。
	 * 
	 * @param xmlDocument ノードが所属する {@link XmlDocument} インスタンス
	 * @param xmlElement 解析対象の XML ノード
	 * @param withHatKey	ハットキーも解析対象とする場合は <tt>true</tt>、ハットキーを解析対象から除外する場合は <tt>false</tt> を指定する。
	 * 
	 * @return 解析した値から生成された <code>ExBasePattern</code> オブジェクト
	 * 
	 * @throws XmlDomParseException ノード解析エラーが発生した場合
	 * 
	 * @since 0.982
	 */
	static protected ExBasePattern decodeFromDomElement(XmlDocument xmlDocument, Element xmlElement, boolean withHatKey) throws XmlDomParseException
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
		String hatKey = WILDCARD;
		if (withHatKey) {
			strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_HATKEY));
			hatKey = ExBasePattern.Util.filterHatKey(strInput, true);
			if (hatKey == null) {
				throw new XmlDomParseException(formatIllegalKeyMessage("hat", strInput));
			}
		}
		
		// name key
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_NAMEKEY));
		String nameKey = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (nameKey == null) {
			throw new XmlDomParseException(formatIllegalKeyMessage("name", strInput));
		}
		
		// unit key
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_UNITKEY));
		String unitKey = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (unitKey == null) {
			throw new XmlDomParseException(formatIllegalKeyMessage("unit", strInput));
		}
		
		// time key
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_TIMEKEY));
		String timeKey = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (timeKey == null) {
			throw new XmlDomParseException(formatIllegalKeyMessage("time", strInput));
		}
		
		// subject key
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_SUBJECTKEY));
		String subjectKey = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (subjectKey == null) {
			throw new XmlDomParseException(formatIllegalKeyMessage("subject", strInput));
		}
		
		// make ExBasePattern instance
		ExBasePattern rPattern = new ExBasePattern();
		rPattern._baseKeys[KEY_HAT] = hatKey;
		rPattern._baseKeys[KEY_NAME] = nameKey;
		rPattern._baseKeys[KEY_EXT_UNIT] = unitKey;
		rPattern._baseKeys[KEY_EXT_TIME] = timeKey;
		rPattern._baseKeys[KEY_EXT_SUBJECT] = subjectKey;
		rPattern.setupStatus();
		rPattern.updateMatchingPattern();
		
		return rPattern;
	}

	/**
	 * 指定された XML 属性から、基底パターンデータを生成する。
	 * 属性の解析において、全ての基底キーは省略可能であり、
	 * 省略されている場合は、省略記号({@link #WILDCARD})に置き換えられる。
	 * また、各キーに使用禁止文字が含まれている場合はエラーとなる。
	 * 
	 * @param locator		SAX の読み込み位置を保持する <code>Locator</code> オブジェクト
	 * @param localName		接頭辞を含まないローカル名。名前空間処理が行われない場合は空文字列
	 * @param attributes	要素の属性。属性がない場合は空の <code>Attributes</code> オブジェクト
	 * 						になる。 
	 * @param withHatKey	ハットキーも解析対象とする場合は <tt>true</tt>、ハットキーを解析対象から除外する場合は <tt>false</tt> を指定する。
	 * 
	 * @return 生成された <code>ExBasePattern</code> の新しいインスタンスを返す。
	 * 
	 * @throws SAXException	SAX 例外。ほかの例外をラップしている可能性がある
	 * 
	 * @since 0.982
	 */
	static protected ExBasePattern decodeFromXmlAttributes(Locator locator, String localName, Attributes attributes, boolean withHatKey)
		throws SAXParseException
	{
		String strInput;
		
		// hat key
		String hatKey = WILDCARD;
		if (withHatKey) {
			strInput = Strings.trim(attributes.getValue(XML_ATTR_HATKEY));
			hatKey = ExBasePattern.Util.filterHatKey(strInput, true);
			if (hatKey == null) {
				XmlErrors.errorParseException(locator, formatIllegalKeyMessage("hat", strInput));
			}
		}
		
		// name key
		strInput = Strings.trim(attributes.getValue(XML_ATTR_NAMEKEY));
		String nameKey = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (nameKey == null) {
			XmlErrors.errorParseException(locator, formatIllegalKeyMessage("name", strInput));
		}
		
		// unit key
		strInput = Strings.trim(attributes.getValue(XML_ATTR_UNITKEY));
		String unitKey = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (unitKey == null) {
			XmlErrors.errorParseException(locator, formatIllegalKeyMessage("unit", strInput));
		}
		
		// time key
		strInput = Strings.trim(attributes.getValue(XML_ATTR_TIMEKEY));
		String timeKey = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (timeKey == null) {
			XmlErrors.errorParseException(locator, formatIllegalKeyMessage("time", strInput));
		}
		
		// subject key
		strInput = Strings.trim(attributes.getValue(XML_ATTR_SUBJECTKEY));
		String subjectKey = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (subjectKey == null) {
			XmlErrors.errorParseException(locator, formatIllegalKeyMessage("subject", strInput));
		}
		
		// make ExBasePattern instance
		ExBasePattern rPattern = new ExBasePattern();
		rPattern._baseKeys[KEY_HAT] = hatKey;
		rPattern._baseKeys[KEY_NAME] = nameKey;
		rPattern._baseKeys[KEY_EXT_UNIT] = unitKey;
		rPattern._baseKeys[KEY_EXT_TIME] = timeKey;
		rPattern._baseKeys[KEY_EXT_SUBJECT] = subjectKey;
		rPattern.setupStatus();
		rPattern.updateMatchingPattern();
		
		return rPattern;
	}
	
	/**
	 * 指定された XML ノードから、基底データを取得する。
	 * <br>
	 * 次の XML ノード構成から、基底データを取得する。
	 * <blockquote>
	 * &lt;Exbase
	 *  name=&quote;<i>nameKey</i>&quote;
	 *  unit=&quote;<i>unitKey</i>&quote;
	 *  time=&quote;<i>timeKey</i>&quote;
	 *  subject=&quote;<i>subjectKey</i>&quote; /&gt;
	 * </blockquote>
	 * 基底キーは、{@link ExBasePattern.Util#getAllowedBaseKeyPattern()} で
	 * 定義されているマッチングパターンと一致しなければ、フォーマットエラーとなる。
	 * <p>
	 * 読み込みにおいて、全ての基底キーは省略可能であり、省略されている場合は
	 * ワイルドカード({@link #WILDCARD})に置き換えられる。
	 * <br>
	 * また、ハットキーもワイルドカードに置き換えられる。
	 * <p>
	 * このメソッドでは、変換先基底パターンを読み込むメソッドであり、
	 * 変換先パターンとして、1つ以上のワイルドカードとワイルドカード以外の文字が
	 * 組み合わされている基底キー文字列は不正な基底キーとして判定する。
	 * 
	 * @param xmlDocument ノードが所属する {@link XmlDocument} インスタンス
	 * @param xmlElement 解析対象の XML ノード
	 * 
	 * @throws XmlDomParseException ノード解析エラーが発生した場合
	 * 
	 * @since 0.91
	 * 
	 * @deprecated このメソッドは削除されます。新しい {@link #decodeFromDomElement(XmlDocument, Element, boolean)} を使用してください。
	 */
	protected void decodeFromElementForTransToPattern(XmlDocument xmlDocument, Element xmlElement)
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
		
		// ハットキーは存在しない
		this._baseKeys[KEY_HAT] = ExBasePattern.WILDCARD;
		
		// 名前キー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_NAMEKEY));
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			// Illegal key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_NAMEKEY, strInput);
		}
		else if (!ExBasePattern.Util.isAllowBaseKeyForTransToPattern(strValue)) {
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_DIR_TO_WILDCARD_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_NAMEKEY, strInput);
		}
		this._baseKeys[KEY_NAME] = strValue;
		
		// 単位キー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_UNITKEY));
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			// Illegal key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_UNITKEY, strInput);
		}
		else if (!ExBasePattern.Util.isAllowBaseKeyForTransToPattern(strValue)) {
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_DIR_TO_WILDCARD_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_UNITKEY, strInput);
		}
		this._baseKeys[KEY_EXT_UNIT] = strValue;
		
		// 時間キー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_TIMEKEY));
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			// Illegal key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_TIMEKEY, strInput);
		}
		else if (!ExBasePattern.Util.isAllowBaseKeyForTransToPattern(strValue)) {
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_DIR_TO_WILDCARD_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_TIMEKEY, strInput);
		}
		this._baseKeys[KEY_EXT_TIME] = strValue;
		
		// サブジェクトキー
		strInput = Strings.trim(xmlElement.getAttribute(XML_ATTR_SUBJECTKEY));
		strValue = ExBasePattern.Util.filterBaseKey(strInput, true);
		if (strValue == null) {
			// Illegal key
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_BASEKEY_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_SUBJECTKEY, strInput);
		}
		else if (!ExBasePattern.Util.isAllowBaseKeyForTransToPattern(strValue)) {
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_DIR_TO_WILDCARD_PATTERN,
											xmlDocument, xmlElement, XML_ATTR_SUBJECTKEY, strInput);
		}
		this._baseKeys[KEY_EXT_SUBJECT] = strValue;
		
		// 内部情報更新
		/*--- modified : 2009/03/10 ---*/
		/*---@@@ old codes @@@---
		updateOneStringKey();
		---@@@ end of old codes @@@---*/
		setupStatus();
		/*--- end of modified : 2009/03/10 ---*/
		updateMatchingPattern();
	}

	/**
	 * <code>{@link ExBasePattern}</code> クラスに関する補助機能を提供するユーティリティクラス。
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
		 * <code>ExBasePattern</code> で許可される、ハットキー文字列のマッチングパターン
		 * <br>
		 * このパターンは、禁止文字列を除いた、
		 * 許容されるハットキー文字列にマッチする。大文字小文字は区別しない。
		 * <br>ワイルドカードも許容される。
		 * <br>
		 * "NO_HAT"、"HAT"、"^"(ハット記号)、ワイルドカード以外の文字列は許容されない。
		 * 
		 * @deprecated (0.970)マッチングアルゴリズムが変更されたため、このフィールドは削除されます。
		 */
		static protected final Pattern _AllowedHatPattern
					= Pattern.compile("\\A(" + _BASIC_HATKEY_LIST + "|\\*)\\z", Pattern.CASE_INSENSITIVE);

		/**
		 * <code>ExBasePattern</code> で許可される、基底キー文字列のマッチングパターン
		 * <br>
		 * このパターンは、禁止文字列を除いた、
		 * 許容される基底キー文字列にマッチする。
		 * <br>ワイルドカードも許容される。
		 * 
		 * @deprecated (0.970)マッチングアルゴリズムが変更されたため、このフィールドは削除されます。
		 */
		static protected final Pattern _AllowedKeyPattern
					= Pattern.compile("\\A[^" + _ILLEGAL_BASEKEY_LIST + "]+\\z");

		/**
		 * <code>ExBasePattern</code> の基底キーとして使用できない文字のセット
		 * 
		 * @since 0.970
		 */
		static protected final CharCandidates _illegalCandidates = new CharCandidates(_ILLEGAL_BASEKEY_CHARS);

		/**
		 * <code>ExBasePattern</code> で許可される、ハットキー文字列のマッチングパターンを返す。
		 * <br>
		 * このパターンは、禁止文字列を除いた、
		 * 許容されるハットキー文字列にマッチする。大文字小文字は区別しない。
		 * <br>ワイルドカードも許容される。
		 * <br>
		 * "NO_HAT"、"HAT"、"^"(ハット記号)、ワイルドカード以外の文字列は許容されない。
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
		 * <code>ExBasePattern</code> で許可される、基底キー文字列のマッチングパターンを返す。
		 * <br>
		 * このパターンは、禁止文字列を除いた、
		 * 許容される基底キー文字列にマッチする。
		 * <br>ワイルドカードも許容される。
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
		 * 変換先基底パターンの基底キーとして許容される文字列かを検証する。
		 * <br>
		 * 変換先基底パターンにおいては、1つ以上のワイルドカードとワイルドカード以外の文字を
		 * 組み合わせた基底パターンは許可されない。
		 * <p>
		 * このメソッドの引数となる基底パターン文字列は、通常の基底パターン文字列として
		 * 許容された文字列であることを前提とする。
		 * 
		 * @param strKey 許容済み基底キー文字列
		 * @return 変換先基底パターンの基底キーとして許容されるものであれば <tt>true</tt>
		 */
		static public boolean isAllowBaseKeyForTransToPattern(String strKey) {
			if (strKey.indexOf(ExBasePattern.WILDCARD) >= 0) {
				if (!ExBasePattern.WILDCARD.equals(strKey)) {
					// 変換先パターンでは、ワイルドカードを含む場合、
					// 単一のワイルドカード文字のみ許可
					return false;
				}
			}
			
			return true;
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
				else if (WILDCARD.equals(input))
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
		 * 入力文字列を正規ハットキー(ワイルドカード({@link ExBasePattern#WILDCARD})、
		 * {@link ExBase#NO_HAT} もしくは {@link ExBase#HAT})に変換する。
		 * <br>
		 * 入力文字列がハットキー文字列として許容されない場合は、<tt>null</tt> を返す。
		 * <br>
		 * <code>isAllowedOmitted</code> に <tt>true</tt> が指定されており、
		 * <code>input</code> が <tt>null</tt> もしくは、長さ 0 の文字列の場合は、
		 * ワイルドカード({@link ExBasePattern#WILDCARD}) を返す。
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
					// 省略は WILDCARD
					return ExBasePattern.WILDCARD;
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
					if (ExBasePattern.WILDCARD.equals(srcValue))
						result = ExBasePattern.WILDCARD;
					else if (_IS_HAT_PATTERN.matcher(srcValue).matches())
						result = ExBase.HAT;
					else
						result = ExBase.NO_HAT;
				}
				---@@@ end of old codes @@@---*/
				if (ExBasePattern.WILDCARD.equals(srcValue))
					result = ExBasePattern.WILDCARD;
				else if (isHatString(srcValue))
					result = ExBase.HAT;
				else if (isNoHatString(srcValue))
					result = ExBase.NO_HAT;
				/*--- end of modified : 2009/03/10 ---*/
			} else if (isAllowedOmitted) {
				// 省略は WILDCARD
				result = ExBasePattern.WILDCARD;
			}
			
			return result;
		}
		
		/**
		 * 入力文字列を基底キー文字列として正しい形式に整形する。
		 * <br>
		 * 整形は、次のように行われる。
		 * <ul>
		 * <li>入力文字列の前後の空白を削除する
		 * <li>入力文字列が空文字列(長さ 0、もしくは <tt>null</tt>)なら、
		 * ワイルドカード({@link ExBasePattern#WILDCARD})とする
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
					return ExBasePattern.WILDCARD;
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
				// 省略は WILDCARD
				result = ExBasePattern.WILDCARD;
			}
			
			return result;
		}
	}

	/**
	 * 基底キー単位のマッチングパターンとターゲットインデックスを保持するクラス。
	 * 
	 * @version 0.970	2009/03/10
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 *
	 * @since 0.970
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
	 * @version 0.970	2009/03/10
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 *
	 * @since 0.970
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
			int pos = pattern.indexOf(ExBasePattern.WILDCARD_CHAR);
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
			int lastWildcardPos = pattern.lastIndexOf(ExBasePattern.WILDCARD_CHAR);
			
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
				StringTokenizer st = new StringTokenizer(mid, ExBasePattern.WILDCARD);
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
}
