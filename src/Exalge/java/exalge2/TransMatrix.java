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
 *  Copyright 2007-2014  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)TransMatrix.java	0.984	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransMatrix.java	0.970	2009/03/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransMatrix.java	0.95	2008/10/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransMatrix.java	0.93	2007/09/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransMatrix.java	0.91	2007/08/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransMatrix.java	0.90	2007/07/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import exalge2.io.csv.CsvFormatException;
import exalge2.io.csv.CsvReader;
import exalge2.io.csv.CsvRecordReader;
import exalge2.io.csv.CsvWriter;
import exalge2.io.xml.XmlDocument;
import exalge2.io.xml.XmlDomParseException;

/**
 * 
 * 交換代数の按分変換を実行する際の按分比率を含めた変換テーブルを保持するクラス。
 * <p>
 * このクラスには、按分元基底と、按分比率と按分先基底のペアを保持するテーブル
 * ({@link TransDivideRatios})とのマップにより変換テーブルが構成される。
 * <br>
 * このクラスの Map の実装は、挿入型の {@link java.util.LinkedHashMap <code>LinkedHashMap</code>} である。
 * したがって、挿入メソッド、ファイル入出力において、
 * クラス内での要素の順序は基本的に維持される。
 * <br>
 * 変換元基底ならびに変換先基底には、基底パターン(<code>{@link ExBasePattern}</code>)を指定する。
 * <p>
 * 按分変換では、変換元基底パターンにマッチする基底の値は、按分先基底へ分配される。
 * <br>
 * このとき、按分先基底の基底キーにワイルドカードが指定されている場合、
 * 按分元基底の対応する基底キーが、そのまま按分先基底のキーとなる。
 * <br>
 * 按分先基底パターンには、基底キーにワイルドカードとワイルドカード以外の文字が
 * 含まれるパターンは指定できない。
 * <p>
 * 按分比率テーブル{@link TransDivideRatios}は、按分比率と按分先基底パターンとのペアを
 * 要素とするテーブルとなる。按分計算においては、按分比率テーブル内の比率合計値が 1 ではないと
 * する計算方法と、比率合計値が 1 であるとする計算方法を選択できる。
 * 計算方法の設定は {@link #setUseTotalRatio(boolean)}、取得は {@link #isTotalRatioUsed()} で可能。<br>
 * 特に指定しない場合、按分比率テーブル内の比率合計値が 1 であるとする計算方法となる。計算方法の
 * 詳細については、{@link #transfer(ExBase, BigDecimal)} を参照のこと。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>TransMatrix</code> は、CSV ファイル、XML ファイル(XML ドキュメント)形式の
 * 入出力インタフェースを提供する。
 * <br>
 * 入出力されるファイルにおいて、共通の事項は、次のとおり。
 * <ul>
 * <li>基底キーに、次の文字は使用できない。<br>
 * &lt; &gt; - , ^ &quot; % &amp; ? | @ ' " (空白)
 * </ul>
 * CSVファイルは、カンマ区切りのテキストファイルで、次のフォーマットに従う。
 * <ul>
 * <li>行の先頭から、次のようなカラム構成となる。<br>
 * <i>属性</i>，<i>按分比率</i>，<i>名前キー</i>，<i>単位キー</i>，<i>時間キー</i>，<i>サブジェクトキー</i>
 * <li>文字コードは、実行するプラットフォームの処理系に依存する。
 * <li>空行は無視される。
 * <li>各カラムの前後空白は、無視される。
 * <li><code>'#'</code> で始まる行は、コメント行とみなす。
 * <li>属性に指定可能な文字列は、次のもののみ(大文字、小文字は区別しない)。
 * from to (空白)
 * <li>属性は、変換元基底、もしくは変換先基底であることを示すものである。
 * &quot;from&quot; が変換元基底、&quot;to&quot; が変換先基底を示す。
 * <li>属性が省略(空文字)の場合、to(変換先基底)とみなす。
 * <li>属性が &quot;from&quot; の按分比率は無視される。
 * <li>按分比率は 0 より大きい数値でなければならない。
 * <li>基底キー(名前キーも含む)のカラムが省略(空文字)の場合、ワイルドカード記号('*')が代入される。
 * <li>連続した &quot;to&quot; 属性の基底が、次の &quot;from&quot; 属性の基底に関連付けられる。
 * </ul>
 * また、CSV ファイルの読み込みにおいて、次の場合に {@link exalge2.io.csv.CsvFormatException} 例外がスローされる。
 * <ul>
 * <li>属性文字列が指定可能な文字列ではない場合
 * <li>按分比率が数値ではない、もしくは 0 より大きい値ではない
 * <li>変換先基底に対応する変換元基底が存在しない(&quot;to&quot; 属性の次に &quot;from&quot; 属性が出現しない)
 * <li>変換元基底に対応する変換先基底が存在しない(&quot;from&quot; 属性の直前に &quot;to&quot; 属性が出現しない)
 * <li>変換先基底の文字列に、ワイルドカード記号とワイルドカード記号以外の組み合わせが存在する
 * <li>変換元基底が重複している
 * <li>按分比率が省略(空文字)、数値以外、もしくは 0 以下である。
 * <li>１つの変換元基底に関連付けられる複数の変換先基底において、規定パターンが重複している
 * <li>各基底キーに使用できない文字が含まれている場合
 * </ul>
 * なお、CSV ファイルの入出力は、{@link #toCSV(File)}、{@link #fromCSV(File)} により行う。
 * また、指定した文字セットでの入出力は、{@link #toCSV(File, String)}、{@link #fromCSV(File, String)} 
 * により行う。
 * <p>
 * XMLドキュメントは、次の構成となる。XMLファイルも同様のフォーマットに従う。
 * <pre><code>
 * &lt;TransMatrix&gt;
 *   &lt;TransMatrixElem&gt;
 *     &lt;TransFrom&gt;
 *       &lt;Exbase name=&quot;<i>名前キー</i>&quot; unit=&quot;<i>単位キー</i>&quot; time=&quot;<i>時間キー</i>&quot; subject=&quot;<i>サブジェクトキー</i>&quot; /&gt;
 *     &lt;/TransFrom&gt;
 *     &lt;TransTo&gt;
 *       &lt;DivideTo ratio=&quot;<i>按分比率</i>&quot;&gt;
 *         &lt;Exbase name=&quot;<i>名前キー</i>&quot; unit=&quot;<i>単位キー</i>&quot; time=&quot;<i>時間キー</i>&quot; subject=&quot;<i>サブジェクトキー</i>&quot; /&gt;
 *       &lt;/DivideTo&gt;
 *            ・
 *             ・
 *             ・
 *     &lt;/TransTo&gt;
 *   &lt;/TransMatrixElem&gt;
 *        ・
 *         ・
 *         ・
 * &lt;/TransMatrix&gt;
 * </code></pre>
 * <ul>
 * <li>ルートノードは単一の &lt;TransMatrix&gt; となる。
 * <li>&lt;TransMatrixElem&gt; が、変換元基底と変換先基底のペアを示すノードとなる。
 * <li>&lt;TransFrom&gt; が、変換元基底を示すノードとなる。
 * <li>&lt;TransTo&gt; が、変換先基底のリストを示すノードとなる。
 * <li>&lt;DivideTo&gt; が、変換先基底と按分比率を示すノードとなる。
 * <li>&quot;ratio&quot; 属性が、按分比率を示す。
 * <li>&lt;Exbase&gt; が、基底パターンを示すノードとなる。属性が基底のキーとなる。
 * <li>&lt;Exbase&gt; ノードは、&lt;TrasnFrom&gt;、&lt;DivideTo&gt; ノードの子ノードであり、
 * それぞれのノードに必ず１つだけ存在する。
 * <li>&lt;DivideTo&gt; ノードは、&lt;TransTo&gt; の子ノードであり、１つ以上存在する。
 * <li>&lt;TrasnFrom&gt; ノードは、&lt;TransMatrixElem&gt; の子ノードであり、必ず１つだけ存在する。
 * <li>&lt;TrasnTo&gt; ノードは、&lt;TrasnMatrixElem&gt; の子ノードであり、必ず１つだけ存在する。
 * <li>&lt;TrasnMatrixElem&gt; ノードは、&lt;TrasnMatrix&gt; の子ノードであり、複数存在する。
 * <li>文字コードは、&quot;UTF-8&quot; とする。
 * <li>各基底キーの前後空白は無視される。
 * <li>基底キー(名前キーも含む)のカラムが省略(空文字)の場合、ワイルドカード記号('*')が代入される。
 * </ul>
 * また、XML ドキュメントのパース(XML ファイルの読み込み)において、
 * 次の場合に {@link exalge2.io.xml.XmlDomParseException} 例外がスローされる。
 * <ul>
 * <li>XML のノードツリー構成が正しくない場合
 * <li>変換元基底に対応する変換先基底が存在しない
 * <li>変換先基底に対応する変換元基底が存在しない
 * <li>変換先基底の文字列に、ワイルドカード記号とワイルドカード記号以外の組み合わせが存在する
 * <li>変換元基底が重複している
 * <li>按分比率が省略(空文字)、数値以外、もしくは 0 以下である
 * <li>１つの変換元基底に関連付けられる複数の変換先基底において、規定パターンが重複している
 * <li>各基底キーに使用できない文字が含まれている場合
 * </ul>
 * なお、XML ドキュメントの入出力は、{@link #toXML()}、{@link #fromXML(XmlDocument)} により行う。
 * また、XML ファイルの入出力は、{@link #toXML(File)}、{@link #fromXML(File)} により行う。
 * 
 * @version 0.984	2014/05/29
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @see TransDivideRatios
 * 
 * @since 0.90
 *
 */
public class TransMatrix implements exalge2.io.IDataOutput, Cloneable
{
	static private final String DIR_TO = "to";
	static private final String DIR_FROM = "from";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 按分比率と基底のマップ
	 */
	private LinkedHashMap<ExBasePattern,TransDivideRatios> transMap;
	private boolean useTotalRatio;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 要素を持たない <code>TransMatrix</code> の新しいインスタンスを生成する。
	 * <p>
	 * このコンストラクタでは、「比率合計値が 1 である」とする計算方法がデフォルトとなる。
	 */
	public TransMatrix() {
		this.transMap = new LinkedHashMap<ExBasePattern,TransDivideRatios>();
		//--- 「比率合計値が 1」となる計算方法をデフォルトとする。
		this.useTotalRatio = false;
	}

	/**
	 * 要素を持たない <code>TransMatrix</code> の新しいインスタンスを生成する。
	 * <p>
	 * 按分比率の計算方法は、<code>useTotalRatio</code> となる。
	 * 
	 * @param useTotalRatio 按分比率合計値から按分比率を算出する場合は <tt>true</tt>、
	 * 						 指定の按分比率をそのまま使用する場合は <tt>false</tt> を指定する。
	 */
	public TransMatrix(boolean useTotalRatio) {
		this.transMap = new LinkedHashMap<ExBasePattern,TransDivideRatios>();
		this.useTotalRatio = useTotalRatio;
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	/**
	 * 按分変換テーブルの要素が空であることを示す。
	 * 
	 * @return 要素が空なら true を返す。
	 */
	public boolean isEmpty() {
		return this.transMap.isEmpty();
	}

	/**
	 * 按分変換テーブルの要素数を返す。
	 * <br>
	 * 要素数は、変換元基底パターンと変換先按分比率テーブルのペアの数となる。
	 * 
	 * @return 要素数
	 */
	public int size() {
		return this.transMap.size();
	}

	/**
	 * 按分比率の計算方法を返す。
	 * 
	 * @return 按分比率合計値から按分比率を算出する場合は <tt>true</tt>、
	 * 			指定の按分比率をそのまま使用する場合は <tt>false</tt> を返す。
	 */
	public boolean isTotalRatioUsed() {
		return this.useTotalRatio;
	}

	/**
	 * 指定の基底パターンが按分元基底に含まれているかを検証する。
	 * 
	 * @param pattern 検証対象の基底パターン
	 * @return 按分元基底に含まれていれば true を返す。
	 */
	public boolean containsTransFrom(ExBasePattern pattern) {
		return this.transMap.containsKey(pattern);
	}

	/**
	 * 指定の基底パターンが按分先基底に含まれているかを検証する。
	 * 
	 * @param pattern 検証対象の基底パターン
	 * @return 按分先基底に含まれていれば true を返す。
	 */
	public boolean containsTransTo(ExBasePattern pattern) {
		boolean result = false;
		Iterator<TransDivideRatios> it = this.transMap.values().iterator();
		while (it.hasNext()) {
			TransDivideRatios ratios = it.next();
			if (ratios.containsPattern(pattern)) {
				result = true;
				break;
			}
		}
		
		return result;
	}

	/**
	 * 按分変換テーブルに含まれる按分元基底パターンの集合を取得する。
	 * 
	 * @return 振替元基底パターンの集合を返す。
	 */
	public Set<ExBasePattern> transFromPatterns() {
		return this.transMap.keySet();
	}

	/**
	 * 按分変換テーブルに含まれる按分比率テーブルのコレクションビューを返す。
	 * 
	 * @return 按分比率テーブルのコレクションビューを返す。
	 */
	public Collection<TransDivideRatios> transToRatios() {
		return this.transMap.values();
	}

	/**
	 * 指定された按分元基底パターンに対応する按分比率テーブルを取得する。
	 * <br>
	 * 対応する按分比率テーブルが存在しない場合は <tt>null</tt> を返す。
	 * 
	 * @param transFrom 検索する按分元基底パターン
	 * @return 按分比率テーブルを返す。存在しない場合は <tt>null</tt> を返す。
	 */
	public TransDivideRatios getTransTo(ExBasePattern transFrom) {
		return this.transMap.get(transFrom);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 按分変換テーブルの全要素を削除する。
	 * 
	 */
	public void clear() {
		this.transMap.clear();
	}

	/**
	 * 按分比率の計算方法を設定する。
	 * 
	 * @param useTotalRatio 按分比率合計値から按分比率を算出する場合は <tt>true</tt>、
	 * 						 指定の按分比率をそのまま使用する場合は <tt>false</tt> を指定する。
	 */
	public void setUseTotalRatio(boolean useTotalRatio) {
		this.useTotalRatio = useTotalRatio;
	}

	/**
	 * このオブジェクトに格納されている全ての按分比率テーブルの
	 * 按分比率合計値を更新する。
	 * 
	 * @since 0.970
	 */
	public void updateTotalRatios() {
		for (TransDivideRatios ratios : transMap.values()) {
			if (ratios != null) {
				ratios.updateTotalRatio();
			}
		}
	}

	/**
	 * 按分変換テーブルに、按分元基底パターンと按分比率テーブルのペアを登録する。
	 * <br>
	 * 按分元基底パターンがすでに登録されている場合、新しい按分比率テーブルに上書きされる。
	 * 
	 * @param transFrom 按分元基底パターン
	 * @param transTo 按分比率テーブル
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public void put(ExBasePattern transFrom, TransDivideRatios transTo) {
		// Check
		if (transFrom == null)
			throw new NullPointerException("transFrom is null");
		if (transTo == null)
			throw new NullPointerException("transTo is null");
		
		// put
		this.transMap.put(transFrom, transTo);
	}

	/**
	 * 按分変換テーブルから、指定の按分元基底パターンと一致するペアを削除する。
	 * 
	 * @param transFrom 削除する按分元基底パターン
	 */
	public void remove(ExBasePattern transFrom) {
		this.transMap.remove(transFrom);
	}

	/**
	 * 指定の基底に一致する、按分元基底パターンを返す。
	 * <br>
	 * 指定の基底に一致する、異なる按分元基底パターンが複数存在する場合、
	 * 最初に一致した按分元基底パターンを返す。
	 * <br>
	 * 一致するパターンが存在しない場合は <tt>null</tt> を返す。
	 * 
	 * @param targetBase パターンとの一致を検証する基底
	 * 
	 * @return 一致する按分元基底パターンを返す。
	 * 			一致するパターンが存在しない場合は <tt>null</tt> を返す。
	 */
	public ExBasePattern machesTransFrom(ExBase targetBase) {
		Iterator<ExBasePattern> it = this.transMap.keySet().iterator();
		while (it.hasNext()) {
			ExBasePattern pat = it.next();
			// maches
			if (pat.matches(targetBase)) {
				// mached!
				return pat;
			}
		}
		
		// No maches
		return null;
	}

	/**
	 * この按分変換テーブルの定義に基づき、指定された基底を変換する。
	 * 指定された基底が、このテーブルの按分元基底のどれにも一致しない
	 * 場合、指定された基底は変換されずに結果の基底集合に含まれる。<br>
	 * 指定の基底に一致する、異なる按分元基底パターンが複数存在する場合、
	 * 最初に一致した按分元基底パターンに関連付けられた変換定義により
	 * 基底の変換が行われる。
	 * 
	 * @param targetBase	変換対象の基底
	 * @return	変換後の基底を格納する新しい基底集合を返す。
	 * 			按分元基底と一致しない場合は、変換せずにそのまま結果に含まれる。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws IllegalStateException	一致する按分元基底に対応する按分先基底が存在しない場合にスローされる
	 * 
	 * @since 0.970
	 */
	public ExBaseSet transform(ExBase targetBase) {
		if (targetBase == null)
			throw new NullPointerException();
		
		// maches
		ExBasePattern transFrom = machesTransFrom(targetBase);
		if (transFrom == null) {
			// no maches
			return new ExBaseSet(Arrays.asList(targetBase));
		}
		
		// translate base
		ExBaseSet resultSet = new ExBaseSet();
		TransDivideRatios transTo = transMap.get(transFrom);
		if (transTo.isEmpty()) {
			throw new IllegalStateException("Undefined translation ahead at matrix from " + transFrom.toString() + ".");
		}
		for (ExBasePattern toPattern : transTo.patterns()) {
			resultSet.fastAdd(toPattern.translate(targetBase));
		}

		return resultSet;
	}

	/**
	 * この按分変換テーブルの定義に基づき、指定された基底集合に含まれる
	 * 全ての基底を変換する。指定された基底集合に含まれる基底のうち、
	 * このテーブルの按分元基底のどれにも一致しないものは、変換せずに
	 * 結果の基底集合に含まれる。<br>
	 * 指定の基底に一致する、異なる按分元基底パターンが複数存在する場合、
	 * 最初に一致した按分元基底パターンに関連付けられた変換定義により
	 * 基底の変換が行われる。
	 * 
	 * @param targetBases	変換対象の基底集合
	 * @return	変換後の基底を格納する新しい基底集合を返す。
	 * 			按分元基底と一致しない基底は、変換せずにそのまま結果に含まれる。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws IllegalStateException	一致する按分元基底に対応する按分先基底が存在しない場合にスローされる
	 * 
	 * @since 0.970
	 */
	public ExBaseSet transform(ExBaseSet targetBases) {
		// check
		if (targetBases.isEmpty()) {
			return new ExBaseSet();
		}
		
		// translate
		ExBaseSet resultSet = new ExBaseSet();
		for (ExBase base : targetBases) {
			ExBasePattern transFrom = machesTransFrom(base);
			if (transFrom != null) {
				TransDivideRatios transTo = transMap.get(transFrom);
				if (transTo.isEmpty()) {
					throw new IllegalStateException("Undefined translation ahead at matrix from " + transFrom.toString() + ".");
				}
				for (ExBasePattern toPattern : transTo.patterns()) {
					resultSet.fastAdd(toPattern.translate(base));
				}
			} else {
				resultSet.fastAdd(base);
			}
		}
		
		return resultSet;
	}

	/**
	 * 指定された基底と値から、按分変換後の交換代数の元を算出する。
	 * <p>
	 * このメソッドでは、按分比率計算方法の設定{@link #setUseTotalRatio(boolean)}により、
	 * 按分値の算出方法が異なる。
	 * <br>
	 * <code>{@link #setUseTotalRatio(boolean)}</code> で <code>true</code> が指定されている場合、
	 * 「按分比率合計値が 1 ではない」場合の計算を実行する。つまり、
	 * 次の計算により按分後の値を算出する。
	 * <blockquote>
	 * 按分値i ＝ 指定値 ÷ 按分比率合計値 × 按分比率i
	 * </blockquote>
	 * また、<code>{@link #setUseTotalRatio(boolean)}</code> で <code>false</code> が指定されている場合、
	 * 「按分比率合計値が 1 である」場合の計算を実行する。つまり、
	 * 次の計算により按分後の値を算出する。
	 * <blockquote>
	 * 按分値i ＝ 指定値 × 按分比率i
	 * </blockquote>
	 * <p>
	 * 除算においては、{@link MathContext#DECIMAL128} の精度で結果を保持する。
	 * そのため、1 つの値を 3 当分するような按分変換の場合、誤差が発生する。
	 * <br>
	 * 指定の基底に一致する、異なる按分元基底パターンが複数存在する場合、
	 * 最初に一致した按分元基底パターンに関連付けられた按分比率により
	 * 按分変換が行われる。
	 * <p>
	 * 指定の基底が按分対象ではない場合、このメソッドは <tt>null</tt> を返す。
	 * 
	 * @param targetBase 按分元となる値の基底
	 * @param targetValue 按分元となる値
	 * 
	 * @return 按分後の交換代数の元
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws IllegalStateException	一致する按分元基底に対応する按分先基底が存在しない場合にスローされる
	 * @throws ArithmeticException		按分計算において 0 除算が発生した場合にスローされる
	 * 
	 * @see TransDivideRatios#divideTransfer(ExBase, BigDecimal, boolean)
	 */
	public Exalge transfer(ExBase targetBase, BigDecimal targetValue) {
		// Check
		if (targetBase == null)
			throw new NullPointerException("targetBase is null");
		if (targetValue == null)
			throw new NullPointerException("targetValue is null");
		
		// maches
		ExBasePattern transFrom = machesTransFrom(targetBase);
		if (transFrom == null) {
			// No maches
			return null;
		}
		
		// get transTo
		TransDivideRatios transTo = getTransTo(transFrom);
		
		// divide translate
		try {
			return transTo.divideTransfer(targetBase, targetValue, useTotalRatio);
		}
		catch (IllegalStateException ex) {
			throw new IllegalStateException("Undefined translation ahead at matrix from " + transFrom.toString() + ".");
		}
		catch (ArithmeticException ex) {
			String msg = ex.getLocalizedMessage();
			if (msg == null || msg.length() <= 0) {
				msg = "Division undefined";
			}
			throw new ArithmeticException(msg + " at matrix " + transFrom.toString() + " -> " + transTo.toString() + ".");
		}
	}

	//------------------------------------------------------------
	// Implement Object interfaces
	//------------------------------------------------------------

	/**
	 * <code>TransMatrix</code> インスタンスの新しいコピーを返す。
	 * このオブジェクトに格納されている <code>ExBasePattern</code> インスタンスは
	 * それ自体は複製されないが、<code>TransDivideRatios</code> インスタンスは
	 * それ自体も複製される。<code>TransDivideRatios</code> の
	 * 複製には {@link exalge2.TransDivideRatios#clone()} メソッドが呼び出される。
	 * 
	 * @return このオブジェクトのコピー
	 * @since 0.970
	 * 
	 * @see exalge2.TransDivideRatios#clone()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public TransMatrix clone() {
		try {
			TransMatrix dup = (TransMatrix)super.clone();
			dup.transMap = (LinkedHashMap<ExBasePattern,TransDivideRatios>)this.transMap.clone();
			for (Map.Entry<ExBasePattern, TransDivideRatios> entry : this.transMap.entrySet()) {
				if (entry.getValue() != null) {
					dup.transMap.put(entry.getKey(), (TransDivideRatios)entry.getValue().clone());
				}
			}
			return dup;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * このオブジェクトのハッシュコード値を返す。
	 * このハッシュコード値は、任意の 2 つのオブジェクト <code>t1</code> と <code>t2</code> に
	 * ついて、<code>t1.equals(t2)</code> の場合 <code>t1.hashCode()==t2.hashCode()</code> になる。
	 * 
	 * @return ハッシュコード値
	 * @since 0.970
	 */
	@Override
	public int hashCode() {
		return (Boolean.valueOf(useTotalRatio).hashCode() + transMap.hashCode());
	}

	/**
	 * 指定されたオブジェクトとこのオブジェクトが等しいかを検証する。
	 * <code>TransMatrix</code> では、2 つの検証するオブジェクトにおいて、
	 * 全ての按分元基底パターンが同値であり、同一按分元基底パターンに対応する
	 * 按分比率テーブルが等しい場合に同値とみなす。
	 * 按分比率テーブルの同値検証には {@link exalge2.TransDivideRatios#equals(Object)} メソッドが呼び出される。
	 * 
	 * @param obj	このオブジェクトと比較するオブジェクト
	 * @return	指定されたオブジェクトがこのオブジェクトと等しい場合に <tt>true</tt>
	 * 
	 * @since 0.970
	 * 
	 * @see exalge2.TransDivideRatios#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			// same instance
			return true;
		}
		else if (obj instanceof TransMatrix) {
			TransMatrix aMatrix = (TransMatrix)obj;
			if (aMatrix.useTotalRatio == this.useTotalRatio) {
				return aMatrix.transMap.equals(this.transMap);
			}
		}
		
		// not equals
		return false;
	}

	/**
	 * このオブジェクトの文字列表現を返す。
	 * <br>文字列表現は、次のような形式となる。
	 * <blockquote>
	 * useTotalRatio( <i>bool値</i> )[ (改行)<br>
	 * <i>按分元基底パターン</i> -&gt; <i>按分比率テーブルの文字列表現</i> (改行)<br>
	 * <i>按分元基底パターン</i> -&gt; <i>按分比率テーブルの文字列表現</i> (改行)<br>
	 * <i>按分元基底パターン</i> -&gt; <i>按分比率テーブルの文字列表現</i> (改行)<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;・<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;・<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;・
	 * </blockquote>
	 * 
	 * @return このオブジェクトの文字列表現
	 * @since 0.970
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("useTotalRatio(");
		sb.append(useTotalRatio);
		sb.append(")[");
		if (!transMap.isEmpty()) {
			sb.append("\n");
			for (Map.Entry<ExBasePattern, TransDivideRatios> entry : transMap.entrySet()) {
				sb.append(entry.getKey());
				sb.append(" -> ");
				sb.append(entry.getValue());
				sb.append("\n");
			}
		}
		sb.append(']');
		return sb.toString();
	}

	//------------------------------------------------------------
	// I/O
	//------------------------------------------------------------

	/**
	 * 按分変換テーブルの内容を、指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている要素の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.91
	 * 
	 */
	public void toCSV(File csvFile)
		throws IOException, FileNotFoundException
	{
		CsvWriter writer = new CsvWriter(csvFile);
		try {
			writeToCSV(writer);
		}
		finally {
			writer.close();
		}
	}

	/**
	 * 按分変換テーブルの内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.93
	 * 
	 */
	public void toCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		CsvWriter writer = new CsvWriter(csvFile, charsetName);
		try {
			writeToCSV(writer);
		}
		finally {
			writer.close();
		}
	}

	/**
	 * 案分変換テーブルの内容を、CSVフォーマットの文字列に変換する。
	 * <p>
	 * 変換時は、このインスタンスに格納されている基底の順序で変換される。
	 * 
	 * @return	CSVフォーマットの文字列、要素が空の場合は空文字列
	 * @since 0.984
	 */
	public String toCsvString()
	{
		StringWriter strw = new StringWriter();
		CsvWriter writer = new CsvWriter(strw);
		try {
			writeToCSV(writer);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		finally {
			writer.close();
		}
		return strw.toString();
	}

	/**
	 * CSV フォーマットのファイルを読み込み、新しい按分変換テーブルを生成する。
	 * <br>
	 * 新しく生成されたインスタンスは、按分比率合計値が 1 であるとする計算方法となる。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>TransMatrix</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.91
	 */
	static public TransMatrix fromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		CsvReader reader = new CsvReader(csvFile);
		TransMatrix newMatrix = new TransMatrix();
		
		try {
			newMatrix.readFromCSV(reader);
			//--- 計算方法のデフォルトは、デフォルトコンストラクタに依存する。
		}
		finally {
			reader.close();
		}
		
		return newMatrix;
	}

	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しい按分変換テーブルを生成する。
	 * <br>
	 * 新しく生成されたインスタンスは、按分比率合計値が 1 であるとする計算方法となる。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>TransMatrix</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.93
	 */
	static public TransMatrix fromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		TransMatrix newMatrix = new TransMatrix();
		
		try {
			newMatrix.readFromCSV(reader);
			//--- 計算方法のデフォルトは、デフォルトコンストラクタに依存する。
		}
		finally {
			reader.close();
		}
		
		return newMatrix;
	}
	
	/**
	 * CSV フォーマットの文字列を読み込み、新しい案分変換テーブルを生成する。
	 * <br>
	 * 新しく生成されたインスタンスは、按分比率合計値が 1 であるとする計算方法となる。
	 * <p>
	 * 基本的に、文字列に記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvString 読み込む CSV フォーマットの文字列
	 * @return 文字列の内容で生成された、新しい <code>TransMatrix</code> インスタンスを返す。
	 * 
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.984
	 */
	static public TransMatrix fromCsvString(String csvString) throws CsvFormatException
	{
		CsvReader reader = new CsvReader(new StringReader(csvString));
		TransMatrix newMatrix = new TransMatrix();
		
		try {
			newMatrix.readFromCSV(reader);
			//--- 計算方法のデフォルトは、デフォルトコンストラクタに依存する。
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		finally {
			reader.close();
		}
		
		return newMatrix;
	}

	/**
	 * 按分変換テーブルの内容を、指定のファイルに XML フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている要素の順序で出力される。
	 * 
	 * @param xmlFile 出力先ファイル
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 * @throws TransformerConfigurationException <code>Transformer</code> インスタンスを生成できない場合
	 * @throws TransformerException ファイルへの変換中に回復不能なエラーが発生した場合
	 * 
	 * @since 0.91
	 */
	public void toXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, DOMException,
				TransformerConfigurationException, TransformerException
	{
		XmlDocument xml = toXML();
		
		xml.toFile(xmlFile);
	}

	/**
	 * 按分変換テーブルの内容から、XML ドキュメントを生成する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている要素の順序で出力される。
	 * 
	 * @return 生成された XML ドキュメント({@link exalge2.io.xml.XmlDocument} インスタンス)
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 * 
	 * @since 0.91
	 */
	public XmlDocument toXML()
		throws FactoryConfigurationError, ParserConfigurationException, DOMException
	{
		XmlDocument xml = new XmlDocument();
		
		Element root = encodeToElement(xml);
		xml.append(root);
		
		return xml;
	}
	
	/**
	 * 案分変換テーブルの内容を、XML フォーマットの文字列に変換する。
	 * <p>
	 * 変換時は、このインスタンスに格納されている基底の順序で変換される。
	 * 
	 * @return	XML フォーマットの文字列
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 * @throws TransformerConfigurationException <code>Transformer</code> インスタンスを生成できない場合
	 * @throws TransformerException ファイルへの変換中に回復不能なエラーが発生した場合
	 * 
	 * @since 0.984
	 */
	public String toXmlString()
		throws FactoryConfigurationError, ParserConfigurationException, DOMException,
				TransformerConfigurationException, TransformerException
	{
		XmlDocument xml = toXML();

		return xml.toXmlString();
	}

	/**
	 * XML フォーマットのファイルを読み込み、新しい按分変換テーブルを生成する。
	 * <br>
	 * 新しく生成されたインスタンスは、按分比率合計値が 1 であるとする計算方法となる。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlFile 読み込む XML ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>TransMatrix</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.91
	 */
	static public TransMatrix fromXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException,
				XmlDomParseException
	{
		XmlDocument xmlDocument = XmlDocument.fromFile(xmlFile);
		
		return TransMatrix.fromXML(xmlDocument);
	}

	/**
	 * 指定された XML ドキュメントを解析し、新しい按分変換テーブルを生成する。
	 * <br>
	 * 新しく生成されたインスタンスは、按分比率合計値が 1 であるとする計算方法となる。
	 * <p>
	 * 基本的に、XML ノードの順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlDocument 解析対象の XML ドキュメント
	 * 
	 * @return 解析により生成された、新しい <code>TransMatrix</code> インスタンスを返す。
	 * 
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.91
	 */
	static public TransMatrix fromXML(XmlDocument xmlDocument)
		throws XmlDomParseException
	{
		Element root = xmlDocument.getDocumentElement();
		
		if (root == null) {
			throw new XmlDomParseException(XmlDomParseException.ELEMENT_NOT_FOUND,
											xmlDocument, null, null, XML_ROOT_ELEMENT_NAME);
		}
		
		TransMatrix newMatrix = new TransMatrix();
		newMatrix.decodeFromElement(xmlDocument, root);
		//--- 計算方法のデフォルトは、デフォルトコンストラクタに依存する。
		
		return newMatrix;
	}
	
	/**
	 * XML フォーマットの文字列を読み込み、新しい案分変換テーブルを生成する。
	 * <br>
	 * 新しく生成されたインスタンスは、按分比率合計値が 1 であるとする計算方法となる。
	 * <p>
	 * 基本的に、文字列に記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlString 読み込む XML フォーマットの文字列
	 * 
	 * @return 文字列の内容で生成された、新しい <code>TransMatrix</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.984
	 */
	static public TransMatrix fromXmlString(String xmlString)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException, XmlDomParseException
	{
		XmlDocument xmlDocument = XmlDocument.fromXmlString(xmlString);
		
		return TransMatrix.fromXML(xmlDocument);
	}

	//------------------------------------------------------------
	// Internal methods for I/O
	//------------------------------------------------------------
	
	protected void writeToCSV(CsvWriter writer)
		throws IOException
	{
		Iterator<ExBasePattern> itTransFrom = this.transMap.keySet().iterator();
		while (itTransFrom.hasNext()) {
			ExBasePattern patFrom = itTransFrom.next();
			TransDivideRatios toList = this.transMap.get(patFrom);
			if (!toList.isEmpty()) {
				// write TransTo items
				Iterator<ExBasePattern> itTransTo = toList.patterns().iterator();
				while (itTransTo.hasNext()) {
					ExBasePattern patTo = itTransTo.next();
					BigDecimal toRatio = toList.getRatio(patTo);
					//--- write dir
					writer.writeColumn(DIR_TO, false);
					//--- write ratio
					writer.writeColumn(toRatio.toPlainString(), false);
					//--- write pattern
					patTo.writeToCSV(writer);
					//---
					writer.flush();
				}
				// write TransFrom item
				//--- write dir
				writer.writeColumn(DIR_FROM, false);
				//--- write blank
				writer.writeColumn("", false);
				//--- write pattern
				patFrom.writeToCSV(writer);
				//---
				writer.flush();
			}
		}
	}
	
	protected void readFromCSV(CsvReader reader)
		throws IOException, CsvFormatException
	{
		CsvRecordReader recReader;
		TransDivideRatios toList = null;
		while ((recReader = reader.readRecord()) != null) {
			try {
				// 空行、コメント行は無視
				if (recReader.isBlankLine() || recReader.isCommentLine()) {
					// skip
					continue;
				}
				
				// dir
				String strDir = recReader.readTrimmedStringColumn(true);
				if (isToDirection(strDir, recReader.getLineNumber())) {
					// Trans "to" direction
					//--- Divide ratio
					BigDecimal toRatio = recReader.readBigDecimalColumn(false);
					if (toRatio.compareTo(BigDecimal.ZERO) < 0) {
						// 0 未満の値はエラー
						throw new CsvFormatException(CsvFormatException.ILLEGAL_DIVIDE_RATIO,
								recReader.getLineNumber(), recReader.getColumnNumber()-1);
					}
					//--- read ExBasePattern
					ExBasePattern patTo = new ExBasePattern(ExBasePattern.WILDCARD, ExBasePattern.WILDCARD);
					patTo.readFromCSVForTransToPattern(recReader);
					//--- 
					if (toList == null) {
						toList = new TransDivideRatios();
					}
					//--- check TransTo already exist
					if (toList.containsPattern(patTo)) {
						throw new CsvFormatException(CsvFormatException.DIR_TO_ALREADY_EXIST,
								recReader.getLineNumber()-1, 3);
					}
					//--- append to TrandDivideRatios
					toList.put(patTo, toRatio);
				}
				else {
					// Trans "from" direction
					//--- skip ratio column
					recReader.readStringColumn(true);
					//--- read ExBasePattern
					ExBasePattern patFrom = new ExBasePattern(ExBasePattern.WILDCARD, ExBasePattern.WILDCARD);
					patFrom.readFromCSV(recReader);
					//--- check exist to
					if (toList == null || toList.isEmpty()) {
						throw new CsvFormatException(CsvFormatException.DIR_TO_NOT_EXIST,
								recReader.getLineNumber()-1, 1);
					}
					//--- check TransFrom already exist
					if (this.containsTransFrom(patFrom)) {
						throw new CsvFormatException(CsvFormatException.DIR_FROM_ALREADY_EXIST,
								recReader.getLineNumber()-1, 3);
					}
					//--- append to TransMatrix
					toList.updateTotalRatio();
					this.put(patFrom, toList);
					toList = null;	// clear
				}
			}
			finally {
				recReader.close();
			}
		}
		
		// check remain from list
		if (toList != null && !toList.isEmpty()) {
			throw new CsvFormatException(CsvFormatException.DIR_FROM_NOT_EXIST,
					reader.getLineNumber(), 1);
		}
	}
	
	protected boolean isToDirection(String strDir, int lineNumber)
		throws CsvFormatException
	{
		if (strDir == null) {
			// same "to"
			return true;
		}
		else if (DIR_FROM.equalsIgnoreCase(strDir)) {
			// same "from"
			return false;
		}
		else if (DIR_TO.equalsIgnoreCase(strDir)) {
			// same "to"
			return true;
		}
		else {
			// Illegal column pattern
			throw new CsvFormatException(CsvFormatException.ILLEGAL_COLUMN_PATTERN, lineNumber, 1);
		}
	}
	
	static protected final String XML_ROOT_ELEMENT_NAME = "TransMatrix";
	static protected final String XML_ITEM_ELEMENT_NAME = "TransMatrixElem";
	static protected final String XML_FROM_ELEMENT_NAME = "TransFrom";
	static protected final String XML_TO_ELEMENT_NAME = "TransTo";
	static protected final String XML_DIVIDE_ELEMENT_NAME = "DivideTo";
	static protected final String XML_ATTR_DIV_RATIO = "ratio";
	
	protected Element encodeToElement(XmlDocument xmlDocument)	throws DOMException
	{
		// create node
		Element node = xmlDocument.createElement(XML_ROOT_ELEMENT_NAME);

		// create items
		Iterator<ExBasePattern> itTransFrom = this.transMap.keySet().iterator();
		while (itTransFrom.hasNext()) {
			ExBasePattern patFrom = itTransFrom.next();
			TransDivideRatios toList = this.transMap.get(patFrom);
			if (!toList.isEmpty()) {
				// create item element
				Element itemElement = xmlDocument.createElement(XML_ITEM_ELEMENT_NAME);
				
				// create TransFrom element
				Element transFromElement = xmlDocument.createElement(XML_FROM_ELEMENT_NAME);
				{
					Element patElement = patFrom.encodeToElement(xmlDocument);
					transFromElement.appendChild(patElement);
				}
				
				// create TransTo element
				Element transToElement = xmlDocument.createElement(XML_TO_ELEMENT_NAME);
				Iterator<ExBasePattern> itTransTo = toList.patterns().iterator();
				while (itTransTo.hasNext()) {
					ExBasePattern patTo = itTransTo.next();
					BigDecimal toRatio = toList.getRatio(patTo);
					//--- create DivideTo element
					Element divToElement = xmlDocument.createElement(XML_DIVIDE_ELEMENT_NAME);
					divToElement.setAttribute(XML_ATTR_DIV_RATIO, toRatio.toPlainString());
					//--- create pattern element
					Element patElement = patTo.encodeToElement(xmlDocument);
					//--- regist to TransTo element
					divToElement.appendChild(patElement);
					transToElement.appendChild(divToElement);
				}
				
				// regist to item element
				itemElement.appendChild(transFromElement);
				itemElement.appendChild(transToElement);
				node.appendChild(itemElement);
			}
		}
		
		// completed
		return node;
	}
	
	protected void decodeFromElement(XmlDocument xmlDocument, Element xmlElement)
		throws XmlDomParseException
	{
		// check Element name
		String strInput = xmlElement.getNodeName();
		if (!strInput.equals(XML_ROOT_ELEMENT_NAME)) {
			// Illegal element name
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_ELEMENT_NAME,
											xmlDocument, xmlElement, null, null);
		}
		
		// get child nodes
		Node child = xmlElement.getFirstChild();
		for (; child != null; child = child.getNextSibling()) {
			if (Node.ELEMENT_NODE == child.getNodeType()) {
				// decode item element
				decodeFromItemElement(xmlDocument, (Element)child);
			}
		}
	}
	
	protected void decodeFromItemElement(XmlDocument xmlDocument, Element itemElement)
		throws XmlDomParseException
	{
		// check item element
		String strName = itemElement.getNodeName();
		if (!strName.equals(XML_ITEM_ELEMENT_NAME)) {
			// Illegal element name
			throw new XmlDomParseException(XmlDomParseException.ILLEGAL_ELEMENT_NAME,
											xmlDocument, itemElement, null, null);
		}
		
		// search child nodes
		Node child;
		Element transToElement = null;
		Element transFromElement = null;
		//--- find TransTo/TransFrom node
		child = itemElement.getFirstChild();
		for (; child != null; child = child.getNextSibling()) {
			if (Node.ELEMENT_NODE == child.getNodeType()) {
				strName = child.getNodeName();
				if (strName.equals(XML_FROM_ELEMENT_NAME)) {
					// transFrom element
					if (transFromElement != null) {
						// multiple exist TransFrom
						throw new XmlDomParseException(XmlDomParseException.DIR_FROM_EXIST_MULTIPLE,
														xmlDocument, (Element)child, null, null);
					}
					transFromElement = (Element)child;
				}
				else if (strName.equals(XML_TO_ELEMENT_NAME)) {
					// transTo element
					if (transToElement != null) {
						// multiple exist TransTo
						throw new XmlDomParseException(XmlDomParseException.DIR_TO_EXIST_MULTIPLE,
														xmlDocument, (Element)child, null, null);
					}
					transToElement = (Element)child;
				}
			}
		}
		//--- check exist TransTo/TransFrom node
		if (transFromElement == null) {
			// TransFrom not found
			throw new XmlDomParseException(XmlDomParseException.DIR_FROM_NOT_EXIST,
											xmlDocument, itemElement, null, null);
		}
		if (transToElement == null) {
			// TransTo not found
			throw new XmlDomParseException(XmlDomParseException.DIR_TO_NOT_EXIST,
											xmlDocument, itemElement, null, null);
		}
		
		// decode TransFrom pattern
		ExBasePattern patFrom = decodeFromTransFromElement(xmlDocument, transFromElement);
		
		// decode TransTo patterns
		TransDivideRatios ratios = decodeFromTransToElement(xmlDocument, transToElement);
		
		// regist to TransMatrix
		ratios.updateTotalRatio();
		this.put(patFrom, ratios);
	}
	
	protected ExBasePattern decodeFromTransFromElement(XmlDocument xmlDocument, Element transFromElement)
		throws XmlDomParseException
	{
		int patCount = 0;
		ExBasePattern patFrom = new ExBasePattern(ExBasePattern.WILDCARD, ExBasePattern.WILDCARD);

		Node child = transFromElement.getFirstChild();
		for (; child != null; child = child.getNextSibling()) {
			if (Node.ELEMENT_NODE == child.getNodeType()) {
				patFrom.decodeFromElement(xmlDocument, (Element)child);
				patCount++;
			}
		}
		
		// check
		if (patCount > 1) {
			throw new XmlDomParseException(XmlDomParseException.DIR_FROM_EXIST_MULTIPLE,
											xmlDocument, transFromElement, null, null);
		}
		else if (patCount < 1) {
			throw new XmlDomParseException(XmlDomParseException.DIR_FROM_NOT_EXIST,
											xmlDocument, transFromElement, null, null);
		}
		
		// check TransFrom already exist
		if (this.containsTransFrom(patFrom)) {
			throw new XmlDomParseException(XmlDomParseException.DIR_FROM_ALREADY_EXIST,
											xmlDocument, transFromElement, null, patFrom.key());
		}
		
		// completed
		return patFrom;
	}
	
	protected TransDivideRatios decodeFromTransToElement(XmlDocument xmlDocument, Element transToElement)
		throws XmlDomParseException
	{
		TransDivideRatios divs = new TransDivideRatios();
		
		Node child = transToElement.getFirstChild();
		for (; child != null; child = child.getNextSibling()) {
			// skip not Element nodes
			if (Node.ELEMENT_NODE != child.getNodeType()) {
				continue;
			}
			
			// check DivideTo element
			Element divElement = (Element)child;
			String strName = divElement.getNodeName();
			if (!strName.equals(XML_DIVIDE_ELEMENT_NAME)) {
				throw new XmlDomParseException(XmlDomParseException.ILLEGAL_ELEMENT_NAME,
												xmlDocument, divElement, null, null);
			}
			
			// check Divide ratio
			String strValue = divElement.getAttribute(XML_ATTR_DIV_RATIO);
			strValue = strValue.trim();
			//--- convert value
			BigDecimal retValue = null;
			try {
				retValue = new BigDecimal(strValue);
			}
			catch (NumberFormatException ex) {
				throw new XmlDomParseException(XmlDomParseException.NUMBER_FORMAT_EXCEPTION,
												xmlDocument, divElement, XML_ATTR_DIV_RATIO, strValue);
			}
			if (retValue.compareTo(BigDecimal.ZERO) < 0) {
				// 0 未満はエラー
				throw new XmlDomParseException(XmlDomParseException.ILLEGAL_DIVIDE_RATIO,
												xmlDocument, divElement, XML_ATTR_DIV_RATIO, strValue);
			}
			
			// get pattern
			int patCount = 0;
			Element elemTransTo = null;
			ExBasePattern patTo = new ExBasePattern(ExBasePattern.WILDCARD, ExBasePattern.WILDCARD);
			Node divChild = divElement.getFirstChild();
			for (; divChild != null; divChild = divChild.getNextSibling()) {
				if (Node.ELEMENT_NODE == divChild.getNodeType()) {
					elemTransTo = (Element)divChild;
					patTo.decodeFromElementForTransToPattern(xmlDocument, elemTransTo);
					patCount++;
				}
			}
			//--- check
			if (patCount > 1) {
				throw new XmlDomParseException(XmlDomParseException.DIR_TO_EXIST_MULTIPLE,
												xmlDocument, divElement, null, null);
			}
			else if (patCount < 1) {
				throw new XmlDomParseException(XmlDomParseException.DIR_TO_NOT_EXIST,
												xmlDocument, divElement, null, null);
			}
			//--- check TransTo already exist
			if (divs.containsPattern(patTo)) {
				throw new XmlDomParseException(XmlDomParseException.DIR_TO_ALREADY_EXIST,
												xmlDocument, elemTransTo, null, patTo.key());
			}
			
			// regist ratio & pattern
			divs.put(patTo, retValue);
		}
		
		// check ratios count
		if (divs.isEmpty()) {
			throw new XmlDomParseException(XmlDomParseException.DIR_TO_NOT_EXIST,
											xmlDocument, transToElement, null, null);
		}
		
		// completed
		return divs;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このオブジェクトの変換定義マップを取得する。
	 * @return 変換定義マップ
	 * @since 0.970
	 */
	protected Map<ExBasePattern,TransDivideRatios> getTransMap() {
		return transMap;
	}
}
