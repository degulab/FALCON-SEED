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
 * @(#)ExTransfer.java	0.984	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExTransfer.java	0.982	2009/10/09 - test for ExBasePatternMultiMap use LinkedHashMap
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExTransfer.java	0.982	2009/09/13
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
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
import exalge2.io.csv.CsvWriter;
import exalge2.io.xml.XmlDocument;
import exalge2.io.xml.XmlDomParseException;

/**
 * 交換代数の変換定義を保持する変換テーブル。
 * <p>
 * この変換テーブルは、変換元基底パターン、変換先基底パターン、変換属性の関係を変換定義として
 * 保持する。この変換テーブルには、振替変換となる変換定義、按分変換となる変換定義を含めることが
 * できる。どの変換を行うかは、変換属性として指定する。
 * <p>
 * 変換元基底パターンならびに変換先基底パターンには、基底パターン(<code>{@link ExBasePattern}</code>)を指定する。
 * 基底パターン(<code>{@link ExBasePattern}</code>)を指定する場合、その基底パターンのハットキーはワイルドカードでなければならない。
 * 基底(<code>{@link ExBase}</code>)を指定することも可能であり、この場合、ハット基底をワイルドカード
 * とする基底パターンに変換される。
 * <p>
 * この変換定義による変換は、変換元となる交換代数元の要素から、変換定義の変換基底パターンに一致した基底の
 * 要素が、一致した変換元基底パターンを持つ変換定義によって変換される。変換後の基底は、変換元要素の
 * 基底が変換先基底パターンによって置き換えられたものとなる。変換先基底パターンの基底キーにワイルドカードが
 * 含まれている場合、その基底キーは変換元要素の基底の基底キーがそのまま変換後の基底キーとなる。
 * <p>
 * 変換属性は、変換の種類を示す属性名と、変換係数となる属性値を指定する。変換属性は、変換元となる
 * 交換代数元の要素の値を変換するときの変換方法を定義するものである。変換テーブル内の複数の変換定義に
 * おいて、変換元基底(パターン)が同一のものは、変換属性の属性名も同一でなければならない。<br>
 * この変換テーブルで指定可能な変換属性は、次の通り。
 * <dl>
 * <dt><b>ratio</b></dt>
 * <dd>同一の変換元基底パターンが定義された複数の変換定義において、
 * 属性値の合計によって算出される変換比率により値を変換する。
 * 属性値には 0 以上の実数値を指定できる。
 * 同一の変換元基底パターンを持つ変換定義が3つ存在し、
 * 属性値がそれぞれ、1、3、5 と指定されている場合、変換後の値は次のように算出される。<br>
 * &nbsp;&nbsp;&nbsp&nbsp;変換元要素の値×1/9<br>
 * &nbsp;&nbsp;&nbsp&nbsp;変換元要素の値×3/9<br>
 * &nbsp;&nbsp;&nbsp&nbsp;変換元要素の値×5/9<br>
 * &nbsp;&nbsp;&nbsp&nbsp;※ 9 = 1 + 3 + 5<br>
 * この属性を指定した変換定義の属性値には、同一変換元基底パターンを持つ複数の変換定義において、
 * 属性値の合計が 0 より大きい値となるように属性値を指定しなければならない。</dd>
 * <dt><b>multiply</b></dt>
 * <dd>指定された属性値を変換元要素の値に乗じる。この変換定義に指定する属性値には任意の実数値を指定できる。</dd>
 * <dt><b>aggre</b></dt>
 * <dd>基底のみの変換(振替変換)を行う。変換元要素の値は変換されない。この変換定義の指定において、
 * 同一の変換元基底パターンをもつ変換定義を複数指定することは許可しない。また、属性名に 'hat' が指定された
 * 変換定義の変換先基底パターンと同一の変換先基底パターンを指定することは許可しない。</dd>
 * <dt><b>hat</b></dt>
 * <dd>基底のみの変換(振替変換)を行い、変換後の基底にハット演算を行う。変換元要素の値は変換されない。
 * この変換定義の指定において、同一の変換元基底パターンをもつ変換定義を複数指定することは許可しない。
 * また、属性名に 'aggre' が指定された変換定義の変換先基底パターンと同一の変換先基底パターンを指定することは許可しない。</dd>
 * </dl>
 * <p>
 * このクラスの Map の実装は、挿入型の {@link java.util.LinkedHashMap <code>LinkedHashMap</code>} である。
 * そのため、クラス内での変換定義の順序は基本的に維持される。
 * <p>
 * <b>注：</b>変換テーブルは、一つの変換定義が一つの変換元基底パターンを持ち、ratio や multiply など、
 * 複数の変換定義において同一の変換元基底パターンを持つものも定義できる。このとき、変換対象の要素の基底に
 * マッチした変換元基底パターンと同一の変換元基底パターンを持つ全ての変換定義により、変換対象の要素が変換される。
 * ただし、一つの変換対象要素の基底が複数の異なる基底パターンにマッチする場合、変換対象要素の基底は最初にマッチした
 * パターンを変換元基底パターンとする全ての変換定義によってのみ、一度だけ変換される。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>ExTransfer</code> は、CSV ファイル、XML ファイル(XML ドキュメント)形式の
 * 入出力インタフェースを提供する。<br>
 * 入出力されるファイルにおいて、共通の事項は、次のとおり。
 * <ul>
 * <li>基底キーに、次の文字は使用できない。<br>
 * &lt; &gt; - , ^ &quot; % &amp; ? | @ ' " (空白)
 * </ul>
 * CSVファイルは、カンマ区切りのテキストファイルで、次のフォーマットに従う。
 * <ul>
 * <li>行の先頭から、次のようなカラム構成となる。<br>
 * <i>変換元基底パターン：名前キー</i>，<i>変換元基底パターン：単位キー</i>，<i>変換元基底パターン：時間キー</i>，<i>変換元基底パターン：サブジェクトキー</i>，
 * <i>変換先基底パターン：名前キー</i>，<i>変換先基底パターン：単位キー</i>，<i>変換先基底パターン：時間キー</i>，<i>変換先基底パターン：サブジェクトキー</i>，
 * <i>変換属性：属性名</i>，<i>変換属性：属性値</i>
 * <li>文字コードは、実行するプラットフォームの処理系に依存する。
 * <li>CSVファイルの先頭(第1行目)はヘッダー行となり、必ず次の文字列でなければならない(全て小文字)。<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;<b>from_name,from_unit,from_time,from_subject,to_name,to_unit,to_time,to_subject,attribute,value</b>
 * <li>空行は無視される。
 * <li>各カラムの前後空白は、無視される。
 * <li><code>'#'</code> で始まる行は、コメント行とみなす。
 * <li>属性名に指定可能な文字列は、次のもののみ(大文字、小文字は区別しない)。<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;<b>ratio</b> , <b>multiply</b> , <b>aggre</b> , <b>hat</b>
 * <li>属性名が省略された場合は、<b>aggre</b> 属性となる。この場合、属性値も省略できる。
 * <li>属性名が <b>hat</b> の場合、属性値は省略できる。
 * <li>属性名が <b>aggre</b> もしくは <b>hat</b> の場合、属性値にどのような値を指定しても、常に 1 となる。
 * <li>基底キー(名前キーも含む)のカラムが省略(空文字)の場合、ワイルドカード記号('*')が代入される。
 * </ul>
 * また、CSV ファイルの読み込みにおいて、次の場合に {@link exalge2.io.csv.CsvFormatException} 例外がスローされる。
 * <ul>
 * <li>各基底キーに使用できない文字が含まれている場合
 * <li>ヘッダー行の内容が正しくない、もしくはヘッダー行が存在しない場合
 * <li>同一の変換元基底パターンと変換先基底パターンの変換定義が複数記述されている場合
 * <li>同一の変換元基底パターンが記述された複数の変換定義において、変換属性の属性名が異なる場合
 * <li>変換属性の属性名に指定可能な文字列以外の文字列が記述されている場合
 * <li>変換属性の属性値が数値ではない場合
 * <li>変換属性の属性名に 'ratio' もしくは 'multiply' が記述されている変換定義において、属性値が数値ではない、もしくは省略されている場合
 * <li>変換属性の属性名に 'ratio' が記述されている変換定義において、変換属性の属性値が 0 以上の数値ではない場合
 * <li>同一の変換元基底パターンが記述された複数の変換定義において、変換属性の属性名が 'ratio' であり、
 * 属性値の合計値が 0 以下の場合
 * <li>変換属性の属性名が 'hat'、'aggre' もしくは省略されている複数の変換定義において、変換元基底パターンが重複する場合
 * <li>変換属性の属性名が 'hat'、'aggre' もしくは省略されている複数の変換定義において、同一の変換先基底パターンを持つ
 * 複数の変換定義の属性名が異なる('hat' もしくは 'aggre' のどちらかではない)場合
 * </ul>
 * なお、CSV ファイルの入出力は、{@link #toCSV(File)}、{@link #fromCSV(File)} により行う。
 * また、指定した文字セットでの入出力は、{@link #toCSV(File, String)}、{@link #fromCSV(File, String)} 
 * により行う。
 * <p>
 * XMLドキュメントは、次の構成となる。XMLファイルも同様のフォーマットに従う。
 * <pre><code>
 * &lt;ExTransfer&gt;
 *   &lt;ExTransferElem&gt;
 *     &lt;ElemFrom&gt;
 *       &lt;Exbase name=&quot;<i>変換元基底パターン：名前キー</i>&quot; unit=&quot;<i>変換元基底パターン：単位キー</i>&quot; time=&quot;<i>変換元基底パターン：時間キー</i>&quot; subject=&quot;<i>変換元基底パターン：サブジェクトキー</i>&quot; /&gt;
 *     &lt;/ElemFrom&gt;
 *     &lt;ElemTo attribute=&quot;<i>変換属性：属性名</i>&quot; value=&quot;<i>変換属性：属性値</i>&quot;&gt;
 *       &lt;Exbase name=&quot;<i>変換先基底パターン：名前キー</i>&quot; unit=&quot;<i>変換先基底パターン：単位キー</i>&quot; time=&quot;<i>変換先基底パターン：時間キー</i>&quot; subject=&quot;<i>変換先基底パターン：サブジェクトキー</i>&quot; /&gt;
 *     &lt;/ElemTo&gt;
 *            ・
 *            ・
 *            ・
 *   &lt;/ExTransferElem&gt;
 *         ・
 *         ・
 *         ・
 * &lt;/ExTransfer&gt;
 * </code></pre>
 * <ul>
 * <li>ルートノードは単一の &lt;ExTransfer&gt; となる。
 * <li>&lt;ExTransferElem&gt; が、変換定義を示すノードとなる。
 * <li>&lt;ElemFrom&gt; が、変換元基底パターンを示すノードとなる。
 * <li>&lt;ElemTo&gt; が、変換先基底パターンと変換属性を示すノードとなる。
 * <li>&lt;ElemTo&gt; の 'attribute' が、変換属性の属性名となる。
 * <li>&lt;ElemTo&gt; の 'value' が、変換属性の属性値となる。
 * <li>&lt;Exbase&gt; が、基底パターンを示すノードとなる。ノードの属性が基底のキーとなる。
 * <li>&lt;Exbase&gt; ノードは、&lt;ElemFrom&gt;、&lt;ElemTo&gt; ノードの子ノードであり、
 * それぞれのノードに必ず１つだけ存在する。
 * <li>&lt;ElemFrom&gt; ノードは、&lt;ExTransferElem&gt; の子ノードであり、必ず１つだけ存在する。
 * また、&lt;ExTransferElem&gt; の最初の子ノードでなければならない。
 * <li>&lt;ElemTo&gt; ノードは、&lt;ExTransferElem&gt; の子ノードであり、1 つ以上存在する。
 * <li>&lt;ExTransferElem&gt; ノードは、&lt;ExTransfer&gt; の子ノードであり、1 つ以上存在する。
 * <li>文字コードは、&quot;UTF-8&quot; とする。
 * <li>各基底キーの前後空白は無視される。
 * <li>基底キー(名前キーも含む)のカラムが省略(空文字)の場合、ワイルドカード記号('*')が代入される。
 * <li>&lt;ElemTo&gt; の 'attribute' に指定可能な文字列は、次のもののみ(大文字、小文字は区別しない)。<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;<b>ratio</b> , <b>multiply</b> , <b>aggre</b> , <b>hat</b>
 * <li>&lt;ElemTo&gt; の 'attribute' が省略(空文字)の場合、<b>aggre</b> が指定されたものとみなす。この場合、'value' も省略できる。
 * <li>属性名が <b>hat</b> の場合、属性値は省略できる。
 * <li>属性名が <b>aggre</b> もしくは <b>hat</b> の場合、属性値にどのような値を指定しても、常に 1 となる。
 * </ul>
 * また、XML ドキュメントのパース(XML ファイルの読み込み)において、
 * 次の場合に {@link exalge2.io.xml.XmlDomParseException} 例外がスローされる。
 * <ul>
 * <li>XML のノードツリー構成が正しくない場合
 * <li>各基底キーに使用できない文字が含まれている場合
 * <li>同一の変換元基底パターンと変換先基底パターンの変換定義が複数記述されている場合
 * <li>同一の変換元基底パターンが記述された複数の変換定義において、変換属性の属性名が異なる場合
 * <li>変換属性の属性名に指定可能な文字列以外の文字列が記述されている場合
 * <li>変換属性の属性値が数値ではない場合
 * <li>変換属性の属性名に 'ratio' もしくは 'multiply' が記述されている変換定義において、属性値が数値ではない、もしくは省略されている場合
 * <li>変換属性の属性名に 'ratio' が記述されている変換定義において、変換属性の属性値が 0 以上の数値ではない場合
 * <li>同一の変換元基底パターンが記述された複数の変換定義において、変換属性の属性名が 'ratio' であり、
 * 属性値の合計値が 0 以下の場合
 * <li>変換属性の属性名が 'hat'、'aggre' もしくは省略されている複数の変換定義において、変換元基底パターンが重複する場合
 * <li>変換属性の属性名が 'hat'、'aggre' もしくは省略されている複数の変換定義において、同一の変換先基底パターンを持つ
 * 複数の変換定義の属性名が異なる('hat' もしくは 'aggre' のどちらかではない)場合
 * </ul>
 * なお、XML ドキュメントの入出力は、{@link #toXML()}、{@link #fromXML(XmlDocument)} により行う。
 * また、XML ファイルの入出力は、{@link #toXML(File)}、{@link #fromXML(File)} により行う。
 * 
 * @version 0.984	2014/05/29
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.982
 */
public class ExTransfer implements exalge2.io.IDataOutput, Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** CSVフォーマットにおける変換元基底パターンの名前キー列の列名 **/
	static public final String HEADER_FROM_NAME    = "from_name";
	/** CSVフォーマットにおける変換元基底パターンの単位キー列の列名 **/
	static public final String HEADER_FROM_UNIT    = "from_unit";
	/** CSVフォーマットにおける変換元基底パターンの時間キー列の列名 **/
	static public final String HEADER_FROM_TIME    = "from_time";
	/** CSVフォーマットにおける変換元基底パターンの主体キー列の列名 **/
	static public final String HEADER_FROM_SUBJECT = "from_subject";
	/** CSVフォーマットにおける変換先基底パターンの名前キー列の列名 **/
	static public final String HEADER_TO_NAME      = "to_name";
	/** CSVフォーマットにおける変換先基底パターンの単位キー列の列名 **/
	static public final String HEADER_TO_UNIT      = "to_unit";
	/** CSVフォーマットにおける変換先基底パターンの時間キー列の列名 **/
	static public final String HEADER_TO_TIME      = "to_time";
	/** CSVフォーマットにおける変換先基底パターンの主体キー列の列名 **/
	static public final String HEADER_TO_SUBJECT   = "to_subject";
	/** CSVフォーマットにおける属性名列の列名 **/
	static public final String HEADER_ATTRIBUTE    = "attribute";
	/** CSVフォーマットにおける属性値列の列名 **/
	static public final String HEADER_VALUE        = "value";

	/** 比率属性を示す属性名 <b>'ratio'</b> **/
	static public final String ATTR_RATIO    = "ratio".intern();
	/** 乗算属性を示す属性名 <b>'multiply'</b> **/
	static public final String ATTR_MULTIPLY = "multiply".intern();
	/** 振替属性を示す属性名 <b>'aggre'</b> **/
	static public final String ATTR_AGGRE    = "aggre".intern();
	/** ハット付き振替属性を示す属性名 <b>'hat'</b> **/
	static public final String ATTR_HAT      = "hat".intern();
	
	static private final String[] HEADER_NAMES = {
		HEADER_FROM_NAME, HEADER_FROM_UNIT, HEADER_FROM_TIME, HEADER_FROM_SUBJECT,
		HEADER_TO_NAME, HEADER_TO_UNIT, HEADER_TO_TIME, HEADER_TO_SUBJECT,
		HEADER_ATTRIBUTE, HEADER_VALUE,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected LinkedHashMap<ExBasePattern, ITransferEntryMap> map;
	protected ExBasePatternMultiMap backmap;
	
	protected ExBasePatternIndexSet patIndex;
	
	protected int numElements = 0;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * デフォルトの初期容量と負荷係数で、空の <code>ExTransfer</code> を生成する。
	 */
	public ExTransfer() {
		this.map = new LinkedHashMap<ExBasePattern,ITransferEntryMap>();
		this.backmap = new ExBasePatternMultiMap();
		this.patIndex = new ExBasePatternIndexSet();
	}

	/**
	 * 指定された初期容量とデフォルトの負荷係数で、空の <code>ExTransfer</code> を生成する。
	 * @param initialCapacity	初期容量
	 * @throws IllegalArgumentException	初期容量が負の場合
	 */
	public ExTransfer(int initialCapacity) {
		this.map = new LinkedHashMap<ExBasePattern,ITransferEntryMap>(initialCapacity);
		this.backmap = new ExBasePatternMultiMap(initialCapacity);
		this.patIndex = new ExBasePatternIndexSet();
	}
	
	/**
	 * 指定された初期容量と負荷係数で、空の <code>ExTransfer</code> を作成する。
	 * @param initialCapacity	初期容量
	 * @param loadFactor		負荷係数
	 * @throws IllegalArgumentException	初期容量が負であるか、負荷係数が正ではない場合
	 */
	public ExTransfer(int initialCapacity, float loadFactor) {
		this.map = new LinkedHashMap<ExBasePattern,ITransferEntryMap>(initialCapacity, loadFactor);
		this.backmap = new ExBasePatternMultiMap(initialCapacity, loadFactor);
		this.patIndex = new ExBasePatternIndexSet();
	}

	/**
	 * デフォルトの初期容量と負荷係数で、空の <code>ExTransfer</code> を生成する。
	 * このコンストラクタでは、<code>usePatternIndex</code> が <tt>true</tt> の場合のみ、
	 * 変換元基底パターンのインデックスを生成する。
	 * @param usePatternIndex	変換元基底パターンのインデックスを生成する場合は <tt>true</tt>
	 */
	protected ExTransfer(boolean usePatternIndex) {
		this.map = new LinkedHashMap<ExBasePattern,ITransferEntryMap>();
		this.backmap = new ExBasePatternMultiMap();
		if (usePatternIndex)
			this.patIndex = new ExBasePatternIndexSet();
		else
			this.patIndex = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * すべての変換定義を削除する。
	 */
	public void clear() {
		numElements = 0;
		map.clear();
		backmap.clear();
		if (patIndex != null) {
			patIndex.clear();
		}
	}

	/**
	 * この変換テーブルが変換定義を一つも保持していない場合に <tt>true</tt> を返す。
	 * @return	この変換テーブルが変換定義を一つも保持していない場合は <tt>true</tt> を返す。
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * この変換テーブルが保持している変換定義数を返す。
	 * 変換定義数は、変換元基底パターンと変換先基底パターンとの組み合わせの総数となる。
	 * @return	この変換テーブルが保持している変換定義数を返す。
	 */
	public int size() {
		return numElements;
	}

	/**
	 * 指定された変換元基底パターンと変換先基底パターンとの組み合わせを保持しているかを検証する。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * @param from	変換元基底パターンとする交換代数基底
	 * @param to	変換先基底パターンとする交換代数基底
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在する場合は <tt>true</tt>
	 */
	public boolean contains(ExBase from, ExBase to) {
		return contains(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to));
	}

	/**
	 * 指定された変換元基底パターンと変換先基底パターンとの組み合わせを保持しているかを検証する。
	 * @param from	変換元基底パターン
	 * @param to	変換先基底パターン
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在する場合は <tt>true</tt>
	 */
	public boolean contains(ExBasePattern from, ExBasePattern to) {
		ITransferEntryMap entrymap = map.get(from);
		if (entrymap != null) {
			return entrymap.containsKey(to);
		}
		return false;
	}

	/**
	 * 指定された変換元基底パターンを保持しているかを検証する。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * @param from	変換元基底パターンとする交換代数基底
	 * @return	指定された変換元基底パターンがこの変換テーブルに存在する場合は <tt>true</tt>
	 */
	public boolean containsFrom(ExBase from) {
		return containsFrom(ExBasePattern.toPattern(from));
	}

	/**
	 * 指定された変換元基底パターンを保持しているかを検証する。
	 * @param from	変換元基底パターン
	 * @return	指定された変換元基底パターンがこの変換テーブルに存在する場合は <tt>true</tt>
	 */
	public boolean containsFrom(ExBasePattern from) {
		return map.containsKey(from);
	}

	/**
	 * 指定された変換先基底パターンを保持しているかを検証する。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * @param to	変換先基底パターンとする交換代数基底
	 * @return	指定された変換先基底パターンがこの変換テーブルに存在する場合は <tt>true</tt>
	 */
	public boolean containsTo(ExBase to) {
		return containsTo(ExBasePattern.toPattern(to));
	}

	/**
	 * 指定された変換先基底パターンを保持しているかを検証する。
	 * @param to	変換先基底パターン
	 * @return	指定された変換先基底パターンがこの変換テーブルに存在する場合は <tt>true</tt>
	 */
	public boolean containsTo(ExBasePattern to) {
		//if (map.isEmpty())
		//	return false;
		//for (ITransferEntryMap entrymap : map.values()) {
		//	if (entrymap.containsKey(to)) {
		//		return true;
		//	}
		//}
		//return false;
		return backmap.containsKey(to);
	}

	/**
	 * この変換テーブルが保持している全ての変換元基底パターンの集合を返す。
	 * <p>
	 * このメソッドが返す集合への操作は、この変換テーブルに影響しない。
	 * 
	 * @return	この変換テーブルが保持している全ての変換元基底パターンを格納する集合を返す。
	 * 			この変換テーブルに変換定義が存在しない場合は、空の集合を返す。
	 */
	public ExBasePatternSet fromPatterns() {
		return new ExBasePatternSet(map.keySet());
	}

	/**
	 * この変換テーブルが保持している全ての変換先基底パターンの集合を返す。
	 * <p>
	 * このメソッドが返す集合への操作は、この変換テーブルに影響しない。
	 * 
	 * @return	この変換テーブルが保持している全ての変換先基底パターンを格納する集合を返す。
	 * 			この変換テーブルに変換定義が存在しない場合は、空の集合を返す。
	 */
	public ExBasePatternSet toPatterns() {
		return new ExBasePatternSet(backmap.keySet());
	}

	/**
	 * この変換テーブルが保持している変換定義のうち、指定された変換元基底パターンに
	 * 関連付けられている全ての変換先基底パターンの集合を返す。
	 * <p>
	 * このメソッドが返す集合への操作は、この変換テーブルに影響しない。
	 * 
	 * @param from	関連付けられた変換定義を取得するためのキーとなる変換元基底パターン
	 * @return	指定された変換元基底パターンに関連付けられた変換定義が存在する場合は、
	 * 			変換元基底パターンに関連付けられている変換先基底パターンを格納する集合を返す。
	 * 			指定された変換元基底パターンに関連付けられている変換定義が存在しない場合は、
	 * 			空の集合を返す。
	 */
	public ExBasePatternSet toPatterns(ExBasePattern from) {
		ITransferEntryMap entrymap = map.get(from);
		if (entrymap != null && !entrymap.isEmpty()) {
			return new ExBasePatternSet(entrymap.keySet());
		} else {
			return new ExBasePatternSet();
		}
	}

	/**
	 * 指定された変換元基底パターンと変換先基底パターンとの組み合わせに関連付けられている
	 * 属性の属性名を取得する。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * @param from	変換元基底パターンとする交換代数基底
	 * @param to	変換先基底パターンとする交換代数基底
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在する場合は、
	 * 			その組み合わせに関連付けられた属性名を返す。組み合わせが存在しない場合は <tt>null</tt> を返す。
	 */
	public String getAttribute(ExBase from, ExBase to) {
		return getAttribute(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to));
	}

	/**
	 * 指定された変換元基底パターンと変換先基底パターンとの組み合わせに関連付けられている
	 * 属性の属性名を取得する。
	 * @param from	変換元基底パターン
	 * @param to	変換先基底パターン
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在する場合は、
	 * 			その組み合わせに関連付けられた属性名を返す。組み合わせが存在しない場合は <tt>null</tt> を返す。
	 */
	public String getAttribute(ExBasePattern from, ExBasePattern to) {
		TransferAttribute attr = getTransferAttribute(from, to);
		return (attr==null ? null : attr.getName());
	}

	/**
	 * 指定された変換元基底パターンと変換先基底パターンとの組み合わせに関連付けられている
	 * 属性の値を取得する。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * @param from	変換元基底パターンとする交換代数基底
	 * @param to	変換先基底パターンとする交換代数基底
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在する場合は、
	 * 			その組み合わせに関連付けられた属性の値を返す。組み合わせが存在しない場合は <tt>null</tt> を返す。
	 */
	public BigDecimal getValue(ExBase from, ExBase to) {
		return getValue(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to));
	}

	/**
	 * 指定された変換元基底パターンと変換先基底パターンとの組み合わせに関連付けられている
	 * 属性の値を取得する。
	 * @param from	変換元基底パターン
	 * @param to	変換先基底パターン
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在する場合は、
	 * 			その組み合わせに関連付けられた属性の値を返す。組み合わせが存在しない場合は <tt>null</tt> を返す。
	 */
	public BigDecimal getValue(ExBasePattern from, ExBasePattern to) {
		TransferAttribute attr = getTransferAttribute(from, to);
		return (attr==null ? null : attr.getValue());
	}
	
	/**
	 * 指定された変換元基底パターンに関連付けられている全ての組み合わせの属性値の合計を取得する。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * @param from	変換元基底パターンとする交換代数基底
	 * @return	変換元基底パターンがこの変換テーブルに存在する場合は、その全ての組み合わせに関連付けられた
	 * 			属性値の合計を返す。組み合わせが存在しない場合は <tt>null</tt> を返す。
	 */
	public BigDecimal getTotalValue(ExBase from) {
		return getTotalValue(ExBasePattern.toPattern(from));
	}

	/**
	 * 指定された変換元基底パターンに関連付けられている全ての組み合わせの属性値の合計を取得する。
	 * @param from	変換元基底パターン
	 * @return	変換元基底パターンがこの変換テーブルに存在する場合は、その全ての組み合わせに関連付けられた
	 * 			属性値の合計を返す。組み合わせが存在しない場合は <tt>null</tt> を返す。
	 */
	public BigDecimal getTotalValue(ExBasePattern from) {
		ITransferEntryMap entrymap = map.get(from);
		if (entrymap != null) {
			return entrymap.getTotalValue();
		} else {
			return null;
		}
	}

	/**
	 * 指定された変換元基底パターンに関連付けられている全ての変換先基底パターンを取得する。
	 * <p>
	 * このメソッドが返す集合への操作は、この変換テーブルに影響しない。
	 * 
	 * @param fromPattern	変換定義を取得するためのキーとなる変換元基底パターン
	 * @return	指定された変換元基底パターンに関連付けられた変換定義が存在する場合は、
	 * 			変換元基底パターンに関連付けられている全ての変換先基底パターンを格納する集合を返す。
	 * 			指定された変換元基底パターンに関連付けられている変換定義が存在しない場合は、
	 * 			空の集合を返す。
	 */
	public ExBasePatternSet lookup(ExBasePattern fromPattern) {
		ITransferEntryMap entrymap = map.get(fromPattern);
		if (entrymap != null) {
			return new ExBasePatternSet(entrymap.keySet());
		} else {
			return new ExBasePatternSet();
		}
	}

	/**
	 * 指定された基底パターン集合に含まれる変換元基底パターンについて、関連付けられている全ての
	 * 変換先基底パターンを取得する。
	 * <p>
	 * このメソッドが返す集合への操作は、この変換テーブルに影響しない。
	 * 
	 * @param fromPatternSet	変換定義を取得するためのキーとなる変換元基底パターンの集合
	 * @return	指定された集合の変換元基底パターンに関連付けられている変換定義が存在する場合は、
	 * 			変換元基底パターンに関連付けられている全ての変換先基底パターンを格納する集合を返す。
	 * 			指定された変換元基底パターンに関連付けられている変換定義が一つも存在しない場合は、
	 * 			空の集合を返す。
	 */
	public ExBasePatternSet lookup(ExBasePatternSet fromPatternSet) {
		ExBasePatternSet retset = new ExBasePatternSet();
		
		if (fromPatternSet != null && !fromPatternSet.isEmpty()) {
			for (ExBasePattern pattern : fromPatternSet) {
				ITransferEntryMap entrymap = map.get(pattern);
				if (entrymap != null) {
					retset.fastAddAll(entrymap.keySet());
				}
			}
		}
		
		return retset;
	}

	/**
	 * 指定された変換先基底パターンに関連付けられている全ての変換元基底パターンを取得する。
	 * <p>
	 * このメソッドが返す集合への操作は、この変換テーブルに影響しない。
	 * 
	 * @param toPattern	変換定義を取得するためのキーとなる変換先基底パターン
	 * @return	指定された変換先基底パターンに関連付けられた変換定義が存在する場合は、
	 * 			変換先基底パターンに関連付けられている全ての変換元基底パターンを格納する集合を返す。
	 * 			指定された変換先基底パターンに関連付けられている変換定義が存在しない場合は、
	 * 			空の集合を返す。
	 */
	public ExBasePatternSet inverseLookup(ExBasePattern toPattern) {
		ExBasePatternSet fromset = backmap.get(toPattern);
		if (fromset != null && !fromset.isEmpty()) {
			return new ExBasePatternSet(fromset);
		} else {
			return new ExBasePatternSet();
		}
	}

	/**
	 * 指定された基底パターン集合に含まれる変換先基底パターンについて、関連付けられている全ての
	 * 変換元基底パターンを取得する。
	 * <p>
	 * このメソッドが返す集合への操作は、この変換テーブルに影響しない。
	 * 
	 * @param toPatternSet	変換定義を取得するためのキーとなる変換先基底パターンの集合
	 * @return	指定された集合の変換先基底パターンに関連付けられている変換定義が存在する場合は、
	 * 			変換先基底パターンに関連付けられている全ての変換元基底パターンを格納する集合を返す。
	 * 			指定された変換先基底パターンに関連付けられている変換定義が一つも存在しない場合は、
	 * 			空の集合を返す。
	 */
	public ExBasePatternSet inverseLookup(ExBasePatternSet toPatternSet) {
		ExBasePatternSet retset = new ExBasePatternSet();
		
		if (toPatternSet != null && !toPatternSet.isEmpty()) {
			for (ExBasePattern pattern : toPatternSet) {
				ExBasePatternSet fromset = backmap.get(pattern);
				if (fromset != null && !fromset.isEmpty()) {
					retset.fastAddAll(fromset);
				}
			}
		}
		
		return retset;
	}

	/**
	 * 指定された変換元基底パターンを持つ変換定義を、指定された <code>transfer</code> に追加する。
	 * 指定された変換元基底パターンがこの変換定義に存在しない場合、<code>transfer</code> は変更されない。
	 * 
	 * @param transfer		変換定義の追加先となる <code>ExTransfer</code> オブジェクト
	 * @param fromPattern	変換定義を取得するためのキーとなる変換元基底パターン
	 * @throws IllegalArgumentException	追加する変換定義が正しくない場合にスローされる。
	 * 
	 * @see #put(ExBasePattern, ExBasePattern, exalge2.ExTransfer.TransferAttribute)
	 */
	protected void projectionEntry(ExTransfer transfer, ExBasePattern fromPattern) {
		ITransferEntryMap entrymap = map.get(fromPattern);
		if (entrymap != null) {
			for (Map.Entry<ExBasePattern, TransferAttribute> entry : entrymap.entrySet()) {
				transfer.put(fromPattern, entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * 指定された変換先基底パターンを持つ変換定義を、指定された <code>transfer</code> に追加する。
	 * 指定された変換先基底パターンがこの変換定義に存在しない場合、<code>transfer</code> は変更されない。
	 * 
	 * @param transfer		変換定義の追加先となる <code>ExTransfer</code> オブジェクト
	 * @param toPattern		変換定義を取得するためのキーとなる変換先基底パターン
	 * @throws IllegalArgumentException	追加する変換定義が正しくない場合にスローされる。
	 * 
	 * @see #put(ExBasePattern, ExBasePattern, exalge2.ExTransfer.TransferAttribute)
	 */
	protected void inverseProjectionEntry(ExTransfer transfer, ExBasePattern toPattern) {
		ExBasePatternSet fromset = backmap.get(toPattern);
		if (fromset != null && !fromset.isEmpty()) {
			for (ExBasePattern pattern : fromset) {
				TransferAttribute attr = getTransferAttribute(pattern, toPattern);
				transfer.put(pattern, toPattern, attr);
			}
		}
	}

	/**
	 * 指定された変換元基底パターン持つ変換定義のみを格納する、新しい <code>ExTransfer</code> を返す。
	 * 
	 * @param fromPattern	変換定義を取得するためのキーとなる変換元基底パターン
	 * @return	指定された変換元基底パターン持つ変換定義のみを格納した、新しい <code>ExTransfer</code> を返す。
	 * 			指定された変換元基底パターンが存在しない場合は、要素が空の <code>ExTransfer</code> を返す。
	 */
	public ExTransfer projection(ExBasePattern fromPattern) {
		ExTransfer transfer = new ExTransfer();
		projectionEntry(transfer, fromPattern);
		return transfer;
	}

	/**
	 * 指定された基底パターン集合に含まれる基底パターンを変換元基底パターンとする変換定義のみを格納する、
	 * 新しい <code>ExTransfer</code> を返す。
	 * 
	 * @param fromPatternSet	変換定義を取得するためのキーとなる変換元基底パターンの集合
	 * @return	指定された基底パターン集合に含まれる基底パターンが変換元基底パターンとなる全ての変換定義を
	 * 			格納した、新しい <code>ExTransfer</code> を返す。指定の変換元基底パターンが一つも存在しない
	 * 			場合は、要素が空の <code>ExTransfer</code> を返す。
	 */
	public ExTransfer projection(ExBasePatternSet fromPatternSet) {
		ExTransfer transfer = new ExTransfer();

		if (fromPatternSet != null && !fromPatternSet.isEmpty()) {
			for (ExBasePattern pattern : fromPatternSet) {
				projectionEntry(transfer, pattern);
			}
		}
		
		return transfer;
	}

	/**
	 * 指定された変換先基底パターンを持つ変換定義のみを格納する、新しい <code>ExTransfer</code> を返す。
	 * 
	 * @param toPattern	変換定義を取得するためのキーとなる変換先基底パターン
	 * @return	指定された変換先基底パターンを持つ変換定義のみを格納した、新しい <code>ExTransfer</code> を返す。
	 * 			指定された変換先基底パターンが存在しない場合は、要素が空の <code>ExTransfer</code> を返す。
	 */
	public ExTransfer inverseProjection(ExBasePattern toPattern) {
		ExTransfer transfer = new ExTransfer();
		inverseProjectionEntry(transfer, toPattern);
		return transfer;
	}

	/**
	 * 指定された基底パターン集合に含まれる基底パターンを変換先基底パターンとする変換定義のみを格納する、
	 * 新しい <code>ExTransfer</code> を返す。
	 * 
	 * @param toPatternSet	変換定義を取得するためのキーとなる変換先基底パターンの集合
	 * @return	指定された基底パターン集合に含まれる基底パターンが変換先基底パターンとなる全ての変換定義を
	 * 			格納した、新しい <code>ExTransfer</code> を返す。師弟の変換先基底パターンが一つも存在しない
	 * 			場合は、要素が空の <code>ExTransfer</code> を返す。
	 */
	public ExTransfer inverseProjection(ExBasePatternSet toPatternSet) {
		ExTransfer transfer = new ExTransfer();
		
		if (toPatternSet != null && !toPatternSet.isEmpty()) {
			for (ExBasePattern toPattern : toPatternSet) {
				inverseProjectionEntry(transfer, toPattern);
			}
		}
		
		return transfer;
	}

	/**
	 * 指定された変換元基底パターン、変換先基底パターン、属性名と属性値の変換定義を、
	 * この変換テーブルに登録する。指定された変換元基底パターンと変換先基底パターンの組み合わせが
	 * すでにこの変換テーブルに存在するとき、すでに存在する組み合わせに関連付けられた属性名と
	 * 指定された属性名が同一の場合のみ、指定された属性名と属性値に置き換えられる。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * 属性名が <tt>null</tt> もしくは空文字列の場合、属性名 {@link #ATTR_AGGRE} に置き換えられる。
	 * また、属性名が {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} の場合、属性値は 1 に置き換えられる。
	 * このメソッドは変換定義の登録において厳密なチェックを行う。次のケースの場合に例外がスローされる。
	 * <ul>
	 * <li>属性名が {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} のとき、指定された変換元基底パターンを含む変換定義がすでに存在する場合</li>
	 * <li>属性名が {@link #ATTR_AGGRE} のとき、指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、
	 * その変換定義の属性名が {@link #ATTR_HAT} の場合</li>
	 * <li>属性名が {@link #ATTR_HAT} のとき、指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、
	 * その変換定義の属性名が {@link #ATTR_AGGRE} の場合</li>
	 * <li>指定された変換元基底パターンがすでに存在するとき、存在する変換定義の属性名と指定された属性名が異なる場合</li>
	 * </ul>
	 * このチェックを行わずに変換定義を登録する場合は {@link #set(ExBase, ExBase, String, BigDecimal)} を使用する。
	 * 
	 * @param from		変換元基底パターンとする交換代数基底
	 * @param to		変換先基底パターンとする交換代数基底
	 * @param attr		基底パターンの組み合わせに関連付ける属性名
	 * @param value		基底パターンの組み合わせに関連付ける属性値
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在していなかった場合は <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code> もしくは </code>to</code> が <tt>null</tt> の場合。
	 * 									もしくは属性名が {@link #ATTR_RATIO}、{@link #ATTR_MULTIPLY} のときに <code>value</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>属性名が {@link #ATTR_AGGRE}、{@link #ATTR_HAT}、{@link #ATTR_RATIO}、{@link #ATTR_MULTIPLY} とは異なる文字列の場合</li>
	 * <li>属性名が {@link #ATTR_RATIO} のとき、指定された属性値が負の場合</li>
	 * <li>属性名が {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} のとき、指定された変換元基底パターンを含む変換定義がすでに存在する場合</li>
	 * <li>属性名が {@link #ATTR_AGGRE} のとき、指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、
	 * その変換定義の属性名が {@link #ATTR_HAT} の場合</li>
	 * <li>属性名が {@link #ATTR_HAT} のとき、指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、
	 * その変換定義の属性名が {@link #ATTR_AGGRE} の場合</li>
	 * <li>指定された変換元基底パターンがすでに存在するとき、存在する変換定義の属性名と指定された属性名が異なる場合</li>
	 * </ul>
	 */
	public boolean put(ExBase from, ExBase to, String attr, BigDecimal value) {
		return put(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to), attr, value);
	}
	
	/**
	 * 指定された変換元基底パターン、変換先基底パターン、属性名と属性値の変換定義を、
	 * この変換テーブルに登録する。指定された変換元基底パターンと変換先基底パターンの組み合わせが
	 * すでにこの変換テーブルに存在するとき、すでに存在する組み合わせに関連付けられた属性名と
	 * 指定された属性名が同一の場合のみ、指定された属性名と属性値に置き換えられる。
	 * 属性名が <tt>null</tt> もしくは空文字列の場合、属性名 {@link #ATTR_AGGRE} に置き換えられる。
	 * また、属性名が {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} の場合、属性値は 1 に置き換えられる。
	 * このメソッドは変換定義の登録において厳密なチェックを行う。次のケースの場合に例外がスローされる。
	 * <ul>
	 * <li>属性名が {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} のとき、指定された変換元基底パターンを含む変換定義がすでに存在する場合</li>
	 * <li>属性名が {@link #ATTR_AGGRE} のとき、指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、
	 * その変換定義の属性名が {@link #ATTR_HAT} の場合</li>
	 * <li>属性名が {@link #ATTR_HAT} のとき、指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、
	 * その変換定義の属性名が {@link #ATTR_AGGRE} の場合</li>
	 * <li>指定された変換元基底パターンがすでに存在するとき、存在する変換定義の属性名と指定された属性名が異なる場合</li>
	 * </ul>
	 * このチェックを行わずに変換定義を登録する場合は {@link #set(ExBasePattern, ExBasePattern, String, BigDecimal)} を使用する。
	 * 
	 * @param from		変換元基底パターン
	 * @param to		変換先基底パターン
	 * @param attr		基底パターンの組み合わせに関連付ける属性名
	 * @param value		基底パターンの組み合わせに関連付ける属性値
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在していなかった場合は <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code> もしくは </code>to</code> が <tt>null</tt> の場合。
	 * 									もしくは属性名が {@link #ATTR_RATIO}、{@link #ATTR_MULTIPLY} のときに <code>value</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>指定された基底パターンのハットキーがワイルドカード以外の場合</li>
	 * <li>属性名が {@link #ATTR_AGGRE}、{@link #ATTR_HAT}、{@link #ATTR_RATIO}、{@link #ATTR_MULTIPLY} とは異なる文字列の場合</li>
	 * <li>属性名が {@link #ATTR_RATIO} のとき、指定された属性値が負の場合</li>
	 * <li>属性名が {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} のとき、指定された変換元基底パターンを含む変換定義がすでに存在する場合</li>
	 * <li>属性名が {@link #ATTR_AGGRE} のとき、指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、
	 * その変換定義の属性名が {@link #ATTR_HAT} の場合</li>
	 * <li>属性名が {@link #ATTR_HAT} のとき、指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、
	 * その変換定義の属性名が {@link #ATTR_AGGRE} の場合</li>
	 * <li>指定された変換元基底パターンがすでに存在するとき、存在する変換定義の属性名と指定された属性名が異なる場合</li>
	 * </ul>
	 */
	public boolean put(ExBasePattern from, ExBasePattern to, String attr, BigDecimal value) {
		if (from == null)
			throw new NullPointerException("from pattern is null.");
		else if (!from.isWildcardHat())
			throw new IllegalArgumentException("from pattern hat key is not Wildcard.");
		if (to == null)
			throw new NullPointerException("to pattern is null.");
		else if (!to.isWildcardHat())
			throw new IllegalArgumentException("to pattern hat key is not Wildcard.");
		
		TransferAttribute newAttr = makeAttribute(attr, value);
		return put(from, to, newAttr);
	}

	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを比率属性({@link #ATTR_RATIO})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンと変換先基底パターンの組み合わせが
	 * すでにこの変換テーブルに存在するとき、すでに存在する組み合わせに関連付けられた属性名が {@link #ATTR_RATIO} の
	 * 場合のみ、指定された属性値に置き換えられる。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * このメソッドは変換定義の登録において厳密なチェックを行う。次のケースの場合に例外がスローされる。
	 * <ul>
	 * <li>指定された変換元基底パターンがすでに存在するとき、存在する変換定義の属性名が {@link #ATTR_RATIO} ではない場合</li>
	 * </ul>
	 * このチェックを行わずに変換定義を登録する場合は {@link #setRatio(ExBase, ExBase, BigDecimal)} を使用する。
	 * 
	 * @param from		変換元基底パターンとする交換代数基底
	 * @param to		変換先基底パターンとする交換代数基底
	 * @param value		比率属性の値
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在していなかった場合は <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code>、</code>to</code>、<code>value</code> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>指定された属性値が負の場合</li>
	 * <li>指定された変換元基底パターンがすでに存在するとき、存在する変換定義の属性名が {@link #ATTR_RATIO} ではない場合</li>
	 * </ul>
	 */
	public boolean putRatio(ExBase from, ExBase to, BigDecimal value) {
		return put(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to), ATTR_RATIO, value);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを比率属性({@link #ATTR_RATIO})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンと変換先基底パターンの組み合わせが
	 * すでにこの変換テーブルに存在するとき、すでに存在する組み合わせに関連付けられた属性名が {@link #ATTR_RATIO} の
	 * 場合のみ、指定された属性値に置き換えられる。
	 * このメソッドは変換定義の登録において厳密なチェックを行う。次のケースの場合に例外がスローされる。
	 * <ul>
	 * <li>指定された変換元基底パターンがすでに存在するとき、存在する変換定義の属性名が {@link #ATTR_RATIO} ではない場合</li>
	 * </ul>
	 * このチェックを行わずに変換定義を登録する場合は {@link #setRatio(ExBasePattern, ExBasePattern, BigDecimal)} を使用する。
	 * 
	 * @param from		変換元基底パターン
	 * @param to		変換先基底パターン
	 * @param value		比率属性の値
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在していなかった場合は <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code>、</code>to</code>、<code>value</code> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>指定された基底パターンのハットキーがワイルドカード以外の場合</li>
	 * <li>指定された属性値が負の場合</li>
	 * <li>指定された変換元基底パターンがすでに存在するとき、存在する変換定義の属性名が {@link #ATTR_RATIO} ではない場合</li>
	 * </ul>
	 */
	public boolean putRatio(ExBasePattern from, ExBasePattern to, BigDecimal value) {
		return put(from, to, ATTR_RATIO, value);
	}

	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを乗算属性({@link #ATTR_MULTIPLY})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンと変換先基底パターンの組み合わせが
	 * すでにこの変換テーブルに存在するとき、すでに存在する組み合わせに関連付けられた属性名が {@link #ATTR_MULTIPLY} の
	 * 場合のみ、指定された属性値に置き換えられる。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * このメソッドは変換定義の登録において厳密なチェックを行う。次のケースの場合に例外がスローされる。
	 * <ul>
	 * <li>指定された変換元基底パターンがすでに存在するとき、存在する変換定義の属性名が {@link #ATTR_MULTIPLY} ではない場合</li>
	 * </ul>
	 * このチェックを行わずに変換定義を登録する場合は {@link #setMultiply(ExBase, ExBase, BigDecimal)} を使用する。
	 * 
	 * @param from		変換元基底パターンとする交換代数基底
	 * @param to		変換先基底パターンとする交換代数基底
	 * @param value		乗算属性の値
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在していなかった場合は <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code>、</code>to</code>、<code>value</code> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>指定された変換元基底パターンがすでに存在するとき、存在する変換定義の属性名が {@link #ATTR_MULTIPLY} ではない場合</li>
	 * </ul>
	 */
	public boolean putMultiply(ExBase from, ExBase to, BigDecimal value) {
		return put(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to), ATTR_MULTIPLY, value);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを乗算属性({@link #ATTR_MULTIPLY})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンと変換先基底パターンの組み合わせが
	 * すでにこの変換テーブルに存在するとき、すでに存在する組み合わせに関連付けられた属性名が {@link #ATTR_MULTIPLY} の
	 * 場合のみ、指定された属性値に置き換えられる。
	 * このメソッドは変換定義の登録において厳密なチェックを行う。次のケースの場合に例外がスローされる。
	 * <ul>
	 * <li>指定された変換元基底パターンがすでに存在するとき、存在する変換定義の属性名が {@link #ATTR_MULTIPLY} ではない場合</li>
	 * </ul>
	 * このチェックを行わずに変換定義を登録する場合は {@link #setMultiply(ExBasePattern, ExBasePattern, BigDecimal)} を使用する。
	 * 
	 * @param from		変換元基底パターン
	 * @param to		変換先基底パターン
	 * @param value		乗算属性の値
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在していなかった場合は <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code>、</code>to</code>、<code>value</code> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>指定された基底パターンのハットキーがワイルドカード以外の場合</li>
	 * <li>指定された変換元基底パターンがすでに存在するとき、存在する変換定義の属性名が {@link #ATTR_MULTIPLY} ではない場合</li>
	 * </ul>
	 */
	public boolean putMultiply(ExBasePattern from, ExBasePattern to, BigDecimal value) {
		return put(from, to, ATTR_MULTIPLY, value);
	}

	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを振替属性({@link #ATTR_AGGRE})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンがこの変換テーブルに登録されていない場合のみ登録できる。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * このメソッドは変換定義の登録において厳密なチェックを行う。次のケースの場合に例外がスローされる。
	 * <ul>
	 * <li>指定された変換元基底パターンを含む変換定義がすでに存在する場合</li>
	 * <li>指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、その変換定義の属性名が {@link #ATTR_HAT} の場合</li>
	 * </ul>
	 * このチェックを行わずに変換定義を登録する場合は {@link #setAggregate(ExBase, ExBase)} を使用する。
	 * 
	 * @param from	変換元基底パターンとする交換代数基底
	 * @param to	変換先基底パターンとする交換代数基底
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在していなかった場合は <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code> もしくは </code>to</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>指定された変換元基底パターンを含む変換定義がすでに存在する場合</li>
	 * <li>指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、その変換定義の属性名が {@link #ATTR_HAT} の場合</li>
	 * </ul>
	 */
	public boolean putAggregate(ExBase from, ExBase to) {
		return put(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to), ATTR_AGGRE, BigDecimal.ONE);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを振替属性({@link #ATTR_AGGRE})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンがこの変換テーブルに登録されていない場合のみ登録できる。
	 * このメソッドは変換定義の登録において厳密なチェックを行う。次のケースの場合に例外がスローされる。
	 * <ul>
	 * <li>指定された変換元基底パターンを含む変換定義がすでに存在する場合</li>
	 * <li>指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、その変換定義の属性名が {@link #ATTR_HAT} の場合</li>
	 * </ul>
	 * このチェックを行わずに変換定義を登録する場合は {@link #setAggregate(ExBasePattern, ExBasePattern)} を使用する。
	 * 
	 * @param from	変換元基底パターン
	 * @param to	変換先基底パターン
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在していなかった場合は <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code> もしくは </code>to</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>指定された基底パターンのハットキーがワイルドカード以外の場合</li>
	 * <li>指定された変換元基底パターンを含む変換定義がすでに存在する場合</li>
	 * <li>指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、その変換定義の属性名が {@link #ATTR_HAT} の場合</li>
	 * </ul>
	 */
	public boolean putAggregate(ExBasePattern from, ExBasePattern to) {
		return put(from, to, ATTR_AGGRE, BigDecimal.ONE);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせをハット付き振替属性({@link #ATTR_HAT})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンがこの変換テーブルに登録されていない場合のみ登録できる。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * このメソッドは変換定義の登録において厳密なチェックを行う。次のケースの場合に例外がスローされる。
	 * <ul>
	 * <li>指定された変換元基底パターンを含む変換定義がすでに存在する場合</li>
	 * <li>指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、その変換定義の属性名が {@link #ATTR_AGGRE} の場合</li>
	 * </ul>
	 * このチェックを行わずに変換定義を登録する場合は {@link #setAggregate(ExBase, ExBase)} を使用する。
	 * 
	 * @param from	変換元基底パターンとする交換代数基底
	 * @param to	変換先基底パターンとする交換代数基底
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在していなかった場合は <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code> もしくは </code>to</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>指定された変換元基底パターンを含む変換定義がすでに存在する場合</li>
	 * <li>指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、その変換定義の属性名が {@link #ATTR_AGGRE} の場合</li>
	 * </ul>
	 */
	public boolean putHatAggregate(ExBase from, ExBase to) {
		return put(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to), ATTR_HAT, BigDecimal.ONE);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせをハット付き振替属性({@link #ATTR_HAT})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンがこの変換テーブルに登録されていない場合のみ登録できる。
	 * このメソッドは変換定義の登録において厳密なチェックを行う。次のケースの場合に例外がスローされる。
	 * <ul>
	 * <li>指定された変換元基底パターンを含む変換定義がすでに存在する場合</li>
	 * <li>指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、その変換定義の属性名が {@link #ATTR_AGGRE} の場合</li>
	 * </ul>
	 * このチェックを行わずに変換定義を登録する場合は {@link #setAggregate(ExBasePattern, ExBasePattern)} を使用する。
	 * 
	 * @param from	変換元基底パターン
	 * @param to	変換先基底パターン
	 * @return	指定された基底パターンの組み合わせがこの変換テーブルに存在していなかった場合は <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code> もしくは </code>to</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>指定された基底パターンのハットキーがワイルドカード以外の場合</li>
	 * <li>指定された変換元基底パターンを含む変換定義がすでに存在する場合</li>
	 * <li>指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、その変換定義の属性名が {@link #ATTR_AGGRE} の場合</li>
	 * </ul>
	 */
	public boolean putHatAggregate(ExBasePattern from, ExBasePattern to) {
		return put(from, to, ATTR_HAT, BigDecimal.ONE);
	}
	
	/**
	 * 指定された変換元基底パターン、変換先基底パターン、属性名と属性値の変換定義を、
	 * この変換テーブルに登録する。指定された変換元基底パターンと変換先基底パターンの
	 * 組み合わせがすでにこの変換テーブルに存在するとき、すでに存在する組み合わせに関連付けられた
	 * 属性名と指定された属性名が一致する場合は、指定された属性値に置き換えられる。
	 * 属性名が <tt>null</tt> もしくは空文字列の場合、属性名 {@link #ATTR_AGGRE} に置き換えられる。
	 * また、属性名が {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} の場合、属性値は 1 に置き換えられる。
	 * 指定された変換元基底パターンと同じ変換元基底パターンを含む変換定義が存在するとき、
	 * すでに存在する変換定義の属性名が指定された属性名と異なる場合、その変換元基底パターンに
	 * 関連する既存の変換定義は削除され、指定された変換定義が登録される。この振る舞いにより、
	 * 変換元基底パターンに関連付けられる複数の変換定義において、登録される属性は1種類に限定される。
	 * なお、登録する変換定義として振替属性({@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT})が指定された場合は、既存の変換元
	 * 基底パターンを含む組み合わせは破棄され、指定された新しい組み合わせが登録される。また、すでに登録されている振替属性の
	 * 変換定義のうち、変換先基底パターンが指定された変換先基底パターンと同一で、指定された属性名と異なる変換定義は
	 * 破棄される。この振る舞いにより、振替属性({@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT})においては、
	 * 一つの変換元基底パターンに関連付けられる変換定義は唯一となり、振替属性の変換定義において同一の変換先基底パターンを
	 * 持つ変換定義は {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} のどちらかの属性に統一される。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンとみなされる。
	 * 
	 * @param from		変換元基底パターンとする交換代数基底
	 * @param to		変換先基底パターンとする交換代数基底
	 * @param attr		基底パターンの組み合わせに関連付ける属性名
	 * @param value		基底パターンの組み合わせに関連付ける属性値
	 * @return	既存の定義が上書きされなかった場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code> もしくは </code>to</code> が <tt>null</tt> の場合。
	 * 									もしくは属性名が {@link #ATTR_RATIO}、{@link #ATTR_MULTIPLY} のときに <code>value</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>属性名が {@link #ATTR_AGGRE}、{@link #ATTR_HAT}、{@link #ATTR_RATIO}、{@link #ATTR_MULTIPLY} とは異なる文字列の場合</li>
	 * <li>属性名が {@link #ATTR_RATIO} のとき、指定された属性値が負の場合</li>
	 * </ul>
	 */
	public boolean set(ExBase from, ExBase to, String attr, BigDecimal value) {
		return set(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to), attr, value);
	}
	
	/**
	 * 指定された変換元基底パターン、変換先基底パターン、属性名と属性値の変換定義を、
	 * この変換テーブルに登録する。指定された変換元基底パターンと変換先基底パターンの
	 * 組み合わせがすでにこの変換テーブルに存在するとき、すでに存在する組み合わせに関連付けられた
	 * 属性名と指定された属性名が一致する場合は、指定された属性値に置き換えられる。
	 * 属性名が <tt>null</tt> もしくは空文字列の場合、属性名 {@link #ATTR_AGGRE} に置き換えられる。
	 * また、属性名が {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} の場合、属性値は 1 に置き換えられる。
	 * 指定された変換元基底パターンと同じ変換元基底パターンを含む変換定義が存在するとき、
	 * すでに存在する変換定義の属性名が指定された属性名と異なる場合、その変換元基底パターンに
	 * 関連する既存の変換定義は削除され、指定された変換定義が登録される。この振る舞いにより、
	 * 変換元基底パターンに関連付けられる複数の変換定義において、登録される属性は1種類に限定される。
	 * なお、登録する変換定義として振替属性({@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT})が指定された場合は、既存の変換元
	 * 基底パターンを含む組み合わせは破棄され、指定された新しい組み合わせが登録される。また、すでに登録されている振替属性の
	 * 変換定義のうち、変換先基底パターンが指定された変換先基底パターンと同一で、指定された属性名と異なる変換定義は
	 * 破棄される。この振る舞いにより、振替属性({@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT})においては、
	 * 一つの変換元基底パターンに関連付けられる変換定義は唯一となり、振替属性の変換定義において同一の変換先基底パターンを
	 * 持つ変換定義は {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} のどちらかの属性に統一される。
	 * 
	 * @param from		変換元基底パターン
	 * @param to		変換先基底パターン
	 * @param attr		基底パターンの組み合わせに関連付ける属性名
	 * @param value		基底パターンの組み合わせに関連付ける属性値
	 * @return	既存の定義が上書きされなかった場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code> もしくは </code>to</code> が <tt>null</tt> の場合。
	 * 									もしくは属性名が {@link #ATTR_RATIO}、{@link #ATTR_MULTIPLY} のときに <code>value</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>指定された基底パターンのハットキーがワイルドカード以外の場合</li>
	 * <li>属性名が {@link #ATTR_AGGRE}、{@link #ATTR_HAT}、{@link #ATTR_RATIO}、{@link #ATTR_MULTIPLY} とは異なる文字列の場合</li>
	 * <li>属性名が {@link #ATTR_RATIO} のとき、指定された属性値が負の場合</li>
	 * </ul>
	 */
	public boolean set(ExBasePattern from, ExBasePattern to, String attr, BigDecimal value) {
		if (from == null)
			throw new NullPointerException("from pattern is null.");
		else if (!from.isWildcardHat())
			throw new IllegalArgumentException("from pattern hat key is not Wildcard.");
		if (to == null)
			throw new NullPointerException("to pattern is null.");
		else if (!to.isWildcardHat())
			throw new IllegalArgumentException("to pattern hat key is not Wildcard.");
		
		TransferAttribute newAttr = makeAttribute(attr, value);
		return set(from, to, newAttr);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを比率属性({@link #ATTR_RATIO})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンと変換先基底パターンの
	 * 組み合わせがすでにこの変換テーブルに存在するとき、すでに存在する組み合わせに関連付けられた属性名が {@link #ATTR_RATIO} の
	 * 場合のみ、指定された属性値に置き換えられる。
	 * また、指定された変換元基底パターンと同じ変換元基底パターンを含む変換定義が存在するとき、
	 * すでに存在する変換定義の属性名が {@link #ATTR_RATIO} ではない場合は、その変換元基底パターンに
	 * 関連する既存の変換定義は削除され、指定された変換定義が登録される。この振る舞いにより、
	 * 変換元基底パターンに関連付けられる複数の変換定義において、登録される属性は1種類に限定される。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * 
	 * @param from		変換元基底パターンとする交換代数基底
	 * @param to		変換先基底パターンとする交換代数基底
	 * @param value		比率属性の値
	 * @return	既存の定義が上書きされなかった場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code>、</code>to</code>、<code>value</code> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された属性値が負の場合
	 */
	public boolean setRatio(ExBase from, ExBase to, BigDecimal value) {
		return set(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to), ATTR_RATIO, value);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを比率属性({@link #ATTR_RATIO})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンと変換先基底パターンの
	 * 組み合わせがすでにこの変換テーブルに存在するとき、すでに存在する組み合わせに関連付けられた属性名が {@link #ATTR_RATIO} の
	 * 場合のみ、指定された属性値に置き換えられる。
	 * また、指定された変換元基底パターンと同じ変換元基底パターンを含む変換定義が存在するとき、
	 * すでに存在する変換定義の属性名が {@link #ATTR_RATIO} ではない場合は、その変換元基底パターンに
	 * 関連する既存の変換定義は削除され、指定された変換定義が登録される。この振る舞いにより、
	 * 変換元基底パターンに関連付けられる複数の変換定義において、登録される属性は1種類に限定される。
	 * 
	 * @param from		変換元基底パターン
	 * @param to		変換先基底パターン
	 * @param value		比率属性の値
	 * @return	既存の定義が上書きされなかった場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code>、</code>to</code>、<code>value</code> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底パターンのハットキーがワイルドカード以外の場合、もしくは、
	 * 										指定された属性値が負の場合
	 */
	public boolean setRatio(ExBasePattern from, ExBasePattern to, BigDecimal value) {
		return set(from, to, ATTR_RATIO, value);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを乗算属性({@link #ATTR_MULTIPLY})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンと変換先基底パターンの
	 * 組み合わせがすでにこの変換テーブルに存在するとき、すでに存在する組み合わせに関連付けられた属性名が {@link #ATTR_MULTIPLY} の
	 * 場合のみ、指定された属性値に置き換えられる。
	 * また、指定された変換元基底パターンと同じ変換元基底パターンを含む変換定義が存在するとき、
	 * すでに存在する変換定義の属性名が {@link #ATTR_MULTIPLY} ではない場合は、その変換元基底パターンに
	 * 関連する既存の変換定義は削除され、指定された変換定義が登録される。この振る舞いにより、
	 * 変換元基底パターンに関連付けられる複数の変換定義において、登録される属性は1種類に限定される。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * 
	 * @param from		変換元基底パターンとする交換代数基底
	 * @param to		変換先基底パターンとする交換代数基底
	 * @param value		乗算属性の値
	 * @return	既存の定義が上書きされなかった場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code>、</code>to</code>、<code>value</code> のどれかが <tt>null</tt> の場合
	 */
	public boolean setMultiply(ExBase from, ExBase to, BigDecimal value) {
		return set(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to), ATTR_MULTIPLY, value);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを乗算属性({@link #ATTR_MULTIPLY})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンと変換先基底パターンの
	 * 組み合わせがすでにこの変換テーブルに存在するとき、すでに存在する組み合わせに関連付けられた属性名が {@link #ATTR_MULTIPLY} の
	 * 場合のみ、指定された属性値に置き換えられる。
	 * また、指定された変換元基底パターンと同じ変換元基底パターンを含む変換定義が存在するとき、
	 * すでに存在する変換定義の属性名が {@link #ATTR_MULTIPLY} ではない場合は、その変換元基底パターンに
	 * 関連する既存の変換定義は削除され、指定された変換定義が登録される。この振る舞いにより、
	 * 変換元基底パターンに関連付けられる複数の変換定義において、登録される属性は1種類に限定される。
	 * 
	 * @param from		変換元基底パターン
	 * @param to		変換先基底パターン
	 * @param value		乗算属性の値
	 * @return	既存の定義が上書きされなかった場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code>、</code>to</code>、<code>value</code> のどれかが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底パターンのハットキーがワイルドカード以外の場合
	 */
	public boolean setMultiply(ExBasePattern from, ExBasePattern to, BigDecimal value) {
		return set(from, to, ATTR_MULTIPLY, value);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを振替属性({@link #ATTR_AGGRE})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンと同じ変換元基底パターンを含む変換定義が存在するとき、
	 * 既存の変換元基底パターンを含む組み合わせは破棄され、指定された新しい組み合わせが登録される。
	 * また、すでに登録されている振替属性の変換定義のうち、変換先基底パターンが指定された変換先基底パターンと同一で、
	 * {@link #ATTR_HAT} 属性の変換定義は破棄される。
	 * この振る舞いにより、振替属性({@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT})においては、
	 * 一つの変換元基底パターンに関連付けられる変換定義は唯一となり、振替属性の変換定義において同一の変換先基底パターンを
	 * 持つ変換定義は {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} のどちらかの属性に統一される。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * 
	 * @param from		変換元基底パターンとする交換代数基底
	 * @param to		変換先基底パターンとする交換代数基底
	 * @return	既存の定義が上書きされなかった場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code> もしくは </code>to</code> が <tt>null</tt> の場合
	 */
	public boolean setAggregate(ExBase from, ExBase to) {
		return set(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to), ATTR_AGGRE, BigDecimal.ONE);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを振替属性({@link #ATTR_AGGRE})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンと同じ変換元基底パターンを含む変換定義が存在するとき、
	 * 既存の変換元基底パターンを含む組み合わせは破棄され、指定された新しい組み合わせが登録される。
	 * また、すでに登録されている振替属性の変換定義のうち、変換先基底パターンが指定された変換先基底パターンと同一で、
	 * {@link #ATTR_HAT} 属性の変換定義は破棄される。
	 * この振る舞いにより、振替属性({@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT})においては、
	 * 一つの変換元基底パターンに関連付けられる変換定義は唯一となり、振替属性の変換定義において同一の変換先基底パターンを
	 * 持つ変換定義は {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} のどちらかの属性に統一される。
	 * 
	 * @param from		変換元基底パターン
	 * @param to		変換先基底パターン
	 * @return	既存の定義が上書きされなかった場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code> もしくは </code>to</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底パターンのハットキーがワイルドカード以外の場合
	 */
	public boolean setAggregate(ExBasePattern from, ExBasePattern to) {
		return set(from, to, ATTR_AGGRE, BigDecimal.ONE);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせをハット付き振替属性({@link #ATTR_HAT})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンと同じ変換元基底パターンを含む変換定義が存在するとき、
	 * 既存の変換元基底パターンを含む組み合わせは破棄され、指定された新しい組み合わせが登録される。
	 * また、すでに登録されている振替属性の変換定義のうち、変換先基底パターンが指定された変換先基底パターンと同一で、
	 * {@link #ATTR_AGGRE} 属性の変換定義は破棄される。
	 * この振る舞いにより、振替属性({@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT})においては、
	 * 一つの変換元基底パターンに関連付けられる変換定義は唯一となり、振替属性の変換定義において同一の変換先基底パターンを
	 * 持つ変換定義は {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} のどちらかの属性に統一される。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * 
	 * @param from		変換元基底パターンとする交換代数基底
	 * @param to		変換先基底パターンとする交換代数基底
	 * @return	既存の定義が上書きされなかった場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code> もしくは </code>to</code> が <tt>null</tt> の場合
	 */
	public boolean setHatAggregate(ExBase from, ExBase to) {
		return set(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to), ATTR_HAT, BigDecimal.ONE);
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせをハット付き振替属性({@link #ATTR_HAT})の変換定義として
	 * この変換テーブルに登録する。指定された変換元基底パターンと同じ変換元基底パターンを含む変換定義が存在するとき、
	 * 既存の変換元基底パターンを含む組み合わせは破棄され、指定された新しい組み合わせが登録される。
	 * また、すでに登録されている振替属性の変換定義のうち、変換先基底パターンが指定された変換先基底パターンと同一で、
	 * {@link #ATTR_AGGRE} 属性の変換定義は破棄される。
	 * この振る舞いにより、振替属性({@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT})においては、
	 * 一つの変換元基底パターンに関連付けられる変換定義は唯一となり、振替属性の変換定義において同一の変換先基底パターンを
	 * 持つ変換定義は {@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT} のどちらかの属性に統一される。
	 * 
	 * @param from		変換元基底パターン
	 * @param to		変換先基底パターン
	 * @return	既存の定義が上書きされなかった場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>from</code> もしくは </code>to</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定された基底パターンのハットキーがワイルドカード以外の場合
	 */
	public boolean setHatAggregate(ExBasePattern from, ExBasePattern to) {
		return set(from, to, ATTR_HAT, BigDecimal.ONE);
	}

	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを、この変換テーブルから削除する。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * 
	 * @param from	変換元基底パターンとする交換代数基底
	 * @param to	変換先基底パターンとする交換代数基底
	 * @return	指定された基底パターンの組み合わせが存在していた場合は <tt>true</tt>
	 */
	public boolean remove(ExBase from, ExBase to) {
		return remove(ExBasePattern.toPattern(from), ExBasePattern.toPattern(to));
	}
	
	/**
	 * 指定された変換元基底パターンと変換先基底パターンの組み合わせを、この変換テーブルから削除する。
	 * 
	 * @param from	変換元基底パターン
	 * @param to	変換先基底パターン
	 * @return	指定された基底パターンの組み合わせが存在していた場合は <tt>true</tt>
	 */
	public boolean remove(ExBasePattern from, ExBasePattern to) {
		ITransferEntryMap entrymap = map.get(from);
		if (entrymap != null && entrymap.containsKey(to)) {
			if (entrymap.size() > 1) {
				entrymap.remove(to);
			} else {
				entrymap.get(to);
				map.remove(from);
				if (patIndex != null) {
					patIndex.remove(from);
				}
			}
			backmap.remove(to, from);
			numElements--;
			entrymap = null;
			return true;
		}
		//--- no remove
		return false;
	}

	/**
	 * 指定された変換元基底パターンに関連付けられているすべての変換定義を、この変換テーブルから削除する。
	 * 基底パターンとして指定された交換代数基底は、ハットキーをワイルドカードとする基底パターンと
	 * みなされる。
	 * 
	 * @param from	変換元基底パターンとする交換代数基底
	 * @return	指定された変換元基底パターンに関連付けられた変換定義が存在していた場合は <tt>true</tt>
	 */
	public boolean removeFrom(ExBase from) {
		return removeFrom(ExBasePattern.toPattern(from));
	}
	
	/**
	 * 指定された変換元基底パターンに関連付けられているすべての変換定義を、この変換テーブルから削除する。
	 * 
	 * @param from	変換元基底パターン
	 * @return	指定された変換元基底パターンに関連付けられた変換定義が存在していた場合は <tt>true</tt>
	 */
	public boolean removeFrom(ExBasePattern from) {
		if (patIndex != null) {
			patIndex.remove(from);
		}
		ITransferEntryMap entrymap = map.remove(from);
		if (entrymap != null) {
			for (ExBasePattern toPattern : entrymap.keySet()) {
				backmap.remove(toPattern, from);
			}
			numElements -= entrymap.size();
			entrymap = null;
			return true;
		}
		//--- no remove
		return false;
	}

	//------------------------------------------------------------
	// Public Transfer interfaces
	//------------------------------------------------------------

	/**
	 * 指定の基底に一致する、変換元基底パターンを返す。
	 * <br>
	 * 指定の基底に一致する、異なる変換元基底パターンが複数存在する場合、
	 * 最初に一致した変換元基底パターンを返す。
	 * <br>
	 * 一致するパターンが存在しない場合は <tt>null</tt> を返す。
	 * 
	 * @param targetBase パターンとの一致を検証する基底
	 * 
	 * @return 一致する変換元基底パターンを返す。
	 * 			一致するパターンが存在しない場合は <tt>null</tt> を返す。
	 */
	public ExBasePattern matchesFrom(ExBase targetBase) {
		if (targetBase == null) {
			return null;
		}
		
		if (patIndex != null) {
			return patIndex.matches(targetBase);
		}
		
		for (ExBasePattern fromPattern : map.keySet()) {
			if (fromPattern.matches(targetBase)) {
				// mached!
				return fromPattern;
			}
		}
		
		// No maches
		return null;
	}
	
	/**
	 * この変換テーブルの定義に基づき、指定された基底を変換する。
	 * 指定された基底が、このテーブルの変換元基底パターンのどれにも
	 * 一致しない場合、指定された基底は変換されずに結果の基底集合に含まれる。
	 * 指定された基底に一致する、異なる変換元基底パターンが複数存在する場合は、
	 * 最初に一致した基底パターンを変換元基底パターンとする全ての変換定義によってのみ変換される。
	 * 
	 * @param targetBase	変換対象の基底
	 * @return	変換後の基底を格納する新しい基底集合を返す。
	 * 			変換元基底パターンと一致しない場合は、変換せずにそのまま結果に含まれる。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public ExBaseSet transform(ExBase targetBase) {
		if (targetBase == null)
			throw new NullPointerException();
		
		// maches
		ExBasePattern matchedPattern = matchesFrom(targetBase);
		if (matchedPattern == null) {
			// no matches
			return new ExBaseSet(Arrays.asList(targetBase));
		}
		ITransferEntryMap entrymap = map.get(matchedPattern);
		if (entrymap == null || entrymap.isEmpty()) {
			throw new IllegalStateException(getErrorForUndefinedTranslationAhead(matchedPattern));
		}
		
		// translate base
		ExBaseSet resultSet = new ExBaseSet();
		//for (ExBasePattern to : entrymap.keySet()) {
		//	resultSet.fastAdd(to.translate(targetBase));
		//}
		entrymap.transform(resultSet, targetBase);
		return resultSet;
	}
	
	/**
	 * この変換テーブルの定義に基づき、指定された基底集合に含まれる全ての基底を
	 * 変換する。指定された基底集合に含まれる基底のうち、このテーブルの変換元
	 * 基底パターンのどれにも一致しないものは、変換せずに結果の基底集合に含まれる。
	 * 指定された基底に一致する、異なる変換元基底パターンが複数存在する場合は、
	 * 最初に一致した基底パターンを変換元基底パターンとする全ての変換定義によってのみ変換される。
	 * 
	 * @param targetBases	変換対象の基底集合
	 * @return	変換後の基底を格納する新しい基底集合を返す。
	 * 			変換元基底パターンと一致しない基底は、変換せずにそのまま結果に含まれる。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public ExBaseSet transform(ExBaseSet targetBases) {
		// check
		if (targetBases.isEmpty()) {
			return new ExBaseSet();
		}
		
		// translate
		ExBaseSet resultSet = new ExBaseSet();
		for (ExBase base : targetBases) {
			ExBasePattern matchedPattern = matchesFrom(base);
			if (matchedPattern != null) {
				ITransferEntryMap entrymap = map.get(matchedPattern);
				if (entrymap == null || entrymap.isEmpty()) {
					throw new IllegalStateException(getErrorForUndefinedTranslationAhead(matchedPattern));
				}
				//for (ExBasePattern to : entrymap.keySet()) {
				//	resultSet.fastAdd(to.translate(base));
				//}
				entrymap.transform(resultSet, base);
			} else {
				resultSet.fastAdd(base);
			}
		}
		
		return resultSet;
	}
	
	/**
	 * この変換テーブルの定義に基づき、指定された基底と値を変換する。
	 * 指定された基底が、このテーブルの変換元基底パターンのどれにも一致しない場合は <tt>null</tt> を返す。
	 * 指定された基底に一致する、異なる変換元基底パターンが複数存在する場合は、
	 * 最初に一致した基底パターンを変換元基底パターンとする全ての変換定義によってのみ変換される。
	 * 
	 * @param targetBase	変換対象の基底
	 * @param targetValue	変換対象の値
	 * 
	 * @return	変換後の基底と値を保持する交換代数元を返す。この元には変換対象の基底や値は含まれず、
	 * 			変換後の基底と値のみが含まれる。変換されなかった場合は <tt>null</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws ArithmeticException		'ratio'属性の変換において、変換比率の合計が 0 の場合にスローされる
	 */
	public Exalge transfer(ExBase targetBase, BigDecimal targetValue) {
		// 変換結果に元の値は含めない
		// check
		if (targetBase == null)
			throw new NullPointerException("Target ExBase is null.");
		if (targetValue == null)
			throw new NullPointerException("Target value is null.");

		// matches
		ExBasePattern matchedPattern = matchesFrom(targetBase);
		if (matchedPattern == null) {
			return null;
		}
		ITransferEntryMap entrymap = map.get(matchedPattern);
		if (entrymap == null || entrymap.isEmpty()) {
			throw new IllegalStateException(getErrorForUndefinedTranslationAhead(matchedPattern));
		}
		
		// transform
		Exalge result = null;
		try {
			result = new Exalge();
			entrymap.transfer(result, targetBase, targetValue);
		}
		catch (ArithmeticException ex) {
			BigDecimal totalRatio = entrymap.getTotalValue();
			throw new ArithmeticException(String.format("Failed to divide by total value(%s) : [from]%s",
						totalRatio.stripTrailingZeros().toPlainString(), matchedPattern.toString()));
		}
		return result;
	}

	/**
	 * この変換テーブルの変換定義に基づき、指定された交換代数元を変換する。
	 * 変換対象の基底に一致する、異なる変換元基底パターンが複数存在する場合は、
	 * 最初に一致した基底パターンを変換元基底パターンとする全ての変換定義によってのみ変換される。
	 * <p>
	 * 変換結果は、指定された交換代数元の全ての要素に、変換対象となった
	 * 基底のハットと元の値、変換後の基底と値が加算された結果となる。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param targetAlge	変換対象の交換代数元
	 * @return	変換結果となる交換代数元の新しいインスタンスを返す。一つも変換が行われなかった
	 * 			場合は、指定された交換代数元のインスタンスをそのまま返す。
	 * 
	 * @throws ArithmeticException		'ratio'属性の変換において、変換比率の合計が 0 の場合にスローされる
	 */
	protected Exalge transfer(Exalge targetAlge) {
		Exalge transAlge = new Exalge();
		for (Map.Entry<ExBase, BigDecimal> entry : targetAlge.data.entrySet()) {
			ExBasePattern matchedPattern = matchesFrom(entry.getKey());
			if (matchedPattern != null) {
				ITransferEntryMap entrymap = map.get(matchedPattern);
				if (entrymap == null || entrymap.isEmpty()) {
					throw new IllegalStateException(getErrorForUndefinedTranslationAhead(matchedPattern));
				}
				ExBase targetBase = entry.getKey();
				BigDecimal targetValue = entry.getValue();
				transAlge.plusValue(targetBase.hat(), targetValue);
				try {
					entrymap.transfer(transAlge, entry.getKey(), entry.getValue());
				}
				catch (ArithmeticException ex) {
					BigDecimal totalRatio = entrymap.getTotalValue();
					throw new ArithmeticException(String.format("Failed to divide by total value(%s) : [from]%s",
								totalRatio.stripTrailingZeros().toPlainString(), matchedPattern.toString()));
				}
			}
		}
		if (transAlge.isEmpty()) {
			// 変換なし
			return targetAlge;
		} else {
			// 変換結果を合成
			return targetAlge.plus(transAlge);
		}
	}

	//------------------------------------------------------------
	// Implement Object interfaces
	//------------------------------------------------------------

	/**
	 * <code>ExTransfer</code> インスタンスの新しいコピーを返す。
	 * 
	 * @return このオブジェクトのコピー
	 */
	@Override
	public ExTransfer clone() {
		try {
			ExTransfer dup = (ExTransfer)super.clone();
			dup.map = new LinkedHashMap<ExBasePattern,ITransferEntryMap>(this.map.size());
			dup.backmap = new ExBasePatternMultiMap(this.backmap.size());
			if (this.patIndex != null) {
				dup.patIndex = new ExBasePatternIndexSet();
			}
			dup.numElements = 0;
			
			for (Map.Entry<ExBasePattern, ITransferEntryMap> entry : this.map.entrySet()) {
				for (Map.Entry<ExBasePattern, TransferAttribute> toentry : entry.getValue().entrySet()) {
					dup.put(entry.getKey(), toentry.getKey(), toentry.getValue());
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
	 */
	@Override
	public int hashCode() {
		return map.hashCode();
	}

	/**
	 * 指定されたオブジェクトとこのオブジェクトが等しいかを検証する。
	 * <code>ExTransfer</code> では、2 つの検証するオブジェクトにおいて、
	 * 全ての変換定義が等しい場合に同値とみなす。
	 * 
	 * @param obj	このオブジェクトと比較するオブジェクト
	 * @return	指定されたオブジェクトがこのオブジェクトと等しい場合に <tt>true</tt>
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			// same instance
			return true;
		}
		else if (obj instanceof ExTransfer) {
			return ((ExTransfer)obj).map.equals(this.map);
		}
		
		// not equals
		return false;
	}

	/**
	 * このオブジェクトの文字列表現を返す。
	 * <br>文字列表現は、次のような形式となる。
	 * <blockquote>
	 * [ (改行)<br>
	 * <i>変換元基底パターン</i> -&gt; <i>変換先基底パターン</i> , <i>属性名</i>:<i>属性値</i> (改行)<br>
	 * <i>変換元基底パターン</i> -&gt; <i>変換先基底パターン</i> , <i>属性名</i>:<i>属性値</i> (改行)<br>
	 * <i>変換元基底パターン</i> -&gt; <i>変換先基底パターン</i> , <i>属性名</i>:<i>属性値</i> (改行)<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;・<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;・<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;・<br>
	 * ]
	 * </blockquote>
	 * 
	 * @return このオブジェクトの文字列表現
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		if (!map.isEmpty()) {
			sb.append("\n");
			for (Map.Entry<ExBasePattern, ITransferEntryMap> entry : map.entrySet()) {
				ExBasePattern fromPattern = entry.getKey();
				ITransferEntryMap entrymap = entry.getValue();
				for (Map.Entry<ExBasePattern, TransferAttribute> elem : entrymap.entrySet()) {
					sb.append(fromPattern);
					sb.append(" -> ");
					sb.append(elem.getKey());
					sb.append(" , ");
					sb.append(elem.getValue());
					sb.append("\n");
				}
			}
		}
		sb.append(']');
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 属性名を正規化する。正規化後のインスタンスは、同一属性名において
	 * 等しい。
	 * @param name	正規化する属性名
	 * @return 正規化後の文字列インスタンスを返す。属性名が正しくない場合は <tt>null</tt> を返す。
	 */
	protected String normalizeAttributeName(String name) {
		if (name==null || name.length() <= 0)
			return ATTR_AGGRE;
		if (ATTR_RATIO == name)
			return ATTR_RATIO;
		if (ATTR_MULTIPLY == name)
			return ATTR_MULTIPLY;
		if (ATTR_AGGRE == name)
			return ATTR_AGGRE;
		if (ATTR_HAT == name)
			return ATTR_HAT;
		
		String lname = name.toLowerCase();
		if (ATTR_RATIO.equals(lname))
			return ATTR_RATIO;
		if (ATTR_MULTIPLY.equals(lname))
			return ATTR_MULTIPLY;
		if (ATTR_AGGRE.equals(lname))
			return ATTR_AGGRE;
		if (ATTR_HAT.equals(lname))
			return ATTR_HAT;
		
		return null;
	}

	/**
	 * 指定された属性名と属性値から <code>TransferAttribute</code> の新しいインスタンスを生成する。
	 * @param attr		属性名
	 * @param value		属性値
	 * @return	生成された <code>TransferAttribute</code> のインスタンスを返す。
	 * @throws NullPointerException		属性名が {@link #ATTR_RATIO}、{@link #ATTR_MULTIPLY} のときに <code>value</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	属性名が正しくないか、指定された値が指定された属性名の
	 * 										値として不適切な場合
	 */
	protected TransferAttribute makeAttribute(String attr, BigDecimal value) {
		String attrname = normalizeAttributeName(attr);
		if (attrname == null)
			throw new IllegalArgumentException("Attribute is illegal : \"" + attr + "\"");
		if (ATTR_AGGRE == attrname)
			return TransferAttribute.AGGREGATE;
		if (ATTR_HAT == attrname)
			return TransferAttribute.HAT_AGGREGATE;
		if (value == null)
			throw new NullPointerException("Value is null.");
		if (ATTR_RATIO == attrname) {
			if (BigDecimal.ZERO.compareTo(value) > 0)
				throw new IllegalArgumentException(getErrorForRatioLessThanZero(value));
			return new TransferAttribute(attrname, value);
		} else {
			return new TransferAttribute(attrname, value);
		}
	}

	/**
	 * 指定された変換元基底パターンと変換先基底パターンの関連付けに対応する属性を取得する。
	 * @param from	変換元基底パターン
	 * @param to	変換先基底パターン
	 * @return	指定された変換元基底パターンと変換先基底パターンの関連付けが存在する場合は
	 * 			その関連付けに対応する属性を返す。それ以外の場合は <tt>null</tt> を返す。
	 */
	protected TransferAttribute getTransferAttribute(ExBasePattern from, ExBasePattern to) {
		ITransferEntryMap entrymap = map.get(from);
		return (entrymap==null ? null : entrymap.get(to));
	}
	
	/**
	 * 指定された変換先基底パターンと指定された属性名を持つ変換定義が存在するかを検証する。
	 * 
	 * @param toPattern	変換先基底パターン
	 * @param attrName	属性名
	 * @return	指定された変換先基底パターンと属性名を持つ変換定義が存在していれば <tt>true</tt> を返す。
	 */
	protected boolean containsTo(ExBasePattern toPattern, String attrName) {
		ExBasePatternSet set = backmap.get(toPattern);
		if (set != null) {
			for (ExBasePattern fromPattern : set) {
				TransferAttribute attr = getTransferAttribute(fromPattern, toPattern);
				if (attr != null && attrName.equals(attr.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 指定された変換先基底パターンと指定された属性名を持つ全ての変換定義の変換元基底パターン
	 * 集合を取得する。
	 * 
	 * @param toPattern	変換先基底パターン
	 * @param attrName	属性名
	 * @return	指定された変換先基底パターンと属性名を持つ全ての変換定義の変換元基底パターン集合を返す。
	 * 			指定された条件の変換定義が存在しない場合は要素が空の基底パターン集合を返す。
	 */
	protected ExBasePatternSet getFromPatterns(ExBasePattern toPattern, String attrName) {
		ExBasePatternSet set = backmap.get(toPattern);
		if (set == null || set.isEmpty()) {
			return new ExBasePatternSet(0);
		}
		
		ExBasePatternSet ret = new ExBasePatternSet(set.size());
		for (ExBasePattern fromPattern : set) {
			TransferAttribute attr = getTransferAttribute(fromPattern, toPattern);
			if (attr != null && attrName.equals(attr.getName())) {
				ret.fastAdd(fromPattern);
			}
		}
		return ret;
	}

	/**
	 * 指定された変換先基底パターンと指定された属性名を持つ全ての変換定義を削除する。
	 * 
	 * @param toPattern	変換先基底パターン
	 * @param attrName	属性名
	 * @return	指定された条件の変換定義が削除された場合に <tt>true</tt> を返す。
	 */
	protected boolean removeTo(ExBasePattern toPattern, String attrName) {
		ExBasePatternSet set = getFromPatterns(toPattern, attrName);
		if (set.isEmpty())
			return false;
		
		for (ExBasePattern fromPattern : set) {
			remove(fromPattern, toPattern);
		}
		return true;
	}

	/**
	 * 指定されたパターンをこの変換テーブルに関連付ける。
	 * このメソッドは、定義の関連付けにおいて、厳密なチェックを行う。
	 * 
	 * @param from	変換元基底パターン
	 * @param to	変換先基底パターン
	 * @param attr	変換属性
	 * @return	指定された変換元基底パターンと変換先基底パターンの関連付けが存在していなかった場合は <tt>true</tt> を返す。
	 * @throws IllegalArgumentException	次のケースにおいて、例外をスローする。
	 * <ul>
	 * <li>属性が<code>ATTR_AGGRE</code>の場合に指定された変換元基底パターンがすでに存在する場合</li>
	 * <li>属性名が {@link #ATTR_AGGRE} のとき、指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、
	 * その変換定義の属性名が {@link #ATTR_HAT} の場合</li>
	 * <li>属性名が {@link #ATTR_HAT} のとき、指定された変換先基底パターンと同一の変換先基底パターンを持つ変換定義がすでに存在し、
	 * その変換定義の属性名が {@link #ATTR_AGGRE} の場合</li>
	 * <li>変換元基底パターンに関連付けられている既存の属性が、指定された属性と異なる場合</li>
	 * </ul>
	 */
	protected boolean put(ExBasePattern from, ExBasePattern to, TransferAttribute attr) {
		ITransferEntryMap entrymap = map.get(from);
		if (entrymap == null) {
			// 新規パターン
			if (ATTR_AGGRE == attr.getName()) {
				if (containsTo(to, ATTR_HAT))
					throw new IllegalArgumentException(getErrorForToAggregatePatternMultiple(from, to, ATTR_HAT, ATTR_AGGRE));
				entrymap = new TransferAggreEntry(to, attr);
			}
			else if (ATTR_HAT == attr.getName()) {
				if (containsTo(to, ATTR_AGGRE))
					throw new IllegalArgumentException(getErrorForToAggregatePatternMultiple(from, to, ATTR_AGGRE, ATTR_HAT));
				entrymap = new TransferAggreEntry(to, attr);
			}
			else {
				entrymap = new TransferEntryMap(to, attr);
			}
			map.put(from, entrymap);
			backmap.put(to, from);
			numElements++;
			if (patIndex != null) {
				patIndex.add(from);
			}
			return true;
		}
		
		if (ATTR_AGGRE == attr.getName() || ATTR_HAT == attr.getName()) {
			// 振替変換定義の場合、変換元基底パターンの重複を認めない
			throw new IllegalArgumentException(getErrorForAggregatePatternMultiple(from));
		}
		if (entrymap.getAttributeName() != attr.getName()) {
			// 同一の変換元基底パターンの登録において、異なる属性の登録は認めない
			throw new IllegalArgumentException(getErrorForNotSameAttribute(from, entrymap.getAttributeName(), attr.getName()));
		}

		boolean result = (entrymap.put(to, attr) == null);
		if (result) {
			backmap.put(to, from);
			numElements++;
		}
		return result;
	}

	/**
	 * 指定されたパターンをこの変換テーブルに関連付ける。
	 * このメソッドは、無条件で既存の関連付けを指定された関連付けで上書きする。
	 * 変換元基底パターンに関連付けられている既存の属性が指定された属性と異なる場合、
	 * 指定の変換元基底パターンに関連付けられている関連付けを全て破棄し、指定された
	 * 関連付けを新たに登録する。
	 * また、登録する変換定義として振替属性({@link #ATTR_AGGRE} もしくは {@link #ATTR_HAT})が指定された場合は、
	 * すでに登録されている振替属性の変換定義のうち、変換先基底パターンが指定された変換先基底パターンと同一で、
	 * 指定された属性名と異なる変換定義は破棄される。
	 * 
	 * @param from	変換元基底パターン
	 * @param to	変換先基底パターン
	 * @param attr	変換属性
	 * @return	既存の定義が上書きされなかった場合に <tt>true</tt> を返す。
	 */
	protected boolean set(ExBasePattern from, ExBasePattern to, TransferAttribute attr) {
		boolean removedExistEntry = false;
		if (ATTR_AGGRE == attr.getName()) {
			removedExistEntry = removeTo(to, ATTR_HAT);
		}
		else if (ATTR_HAT == attr.getName()) {
			removedExistEntry = removeTo(to, ATTR_AGGRE);
		}
		
		ITransferEntryMap entrymap = map.get(from);
		if (entrymap == null) {
			// 新規パターン
			if (ATTR_AGGRE == attr.getName() || ATTR_HAT == attr.getName()) {
				entrymap = new TransferAggreEntry(to, attr);
			} else {
				entrymap = new TransferEntryMap(to, attr);
			}
			map.put(from, entrymap);
			backmap.put(to, from);
			numElements++;
			if (patIndex != null) {
				patIndex.add(from);
			}
			return (!removedExistEntry);
		}
		
		if (entrymap.getAttributeName() != attr.getName() || ATTR_AGGRE == attr.getName() || ATTR_HAT == attr.getName()) {
			// 既存の定義を変更
			for (ExBasePattern removeTo : entrymap.keySet()) {
				backmap.remove(removeTo, from);
			}
			numElements -= entrymap.size();
			if (ATTR_AGGRE == attr.getName() || ATTR_HAT == attr.getName()) {
				entrymap = new TransferAggreEntry(to, attr);
			} else {
				entrymap = new TransferEntryMap(to, attr);
			}
			map.put(from, entrymap);
			backmap.put(to, from);
			numElements++;
			return false;
		}
		
		// 振替以外の変換定義を上書き
		boolean result = (entrymap.put(to, attr) == null);
		if (result) {
			backmap.put(to, from);
			numElements++;
			result = (!removedExistEntry);
		}
		return result;
	}
	
	static protected boolean eq(Object obj1, Object obj2) {
		return (obj1==null ? obj2==null : obj1.equals(obj2));
	}
	
	static protected String formatEntry(ExBasePattern from, ExBasePattern to, TransferAttribute attr) {
		if (ATTR_AGGRE == attr.getName())
			return String.format("%s -> %s '%s'", from.toString(), to.toString(), attr.getName());
		else
			return String.format("%s -> %s '%s'=%s", from.toString(), to.toString(), attr.getName(), attr.getValue().toPlainString());
	}
	
	static protected String getErrorForAggregatePatternMultiple(ExBasePattern from) {
		return String.format("Aggregate pattern already exist : [from]%s", from.toString());
	}
	
	static protected String getErrorForNotSameAttribute(ExBasePattern from, String existAttr, String newAttr) {
		return String.format("Not same exist attribute and specified attribute : [from]%s , [exist attribute]%s , [specified attribute]%s",
								from.toString(), existAttr, newAttr);
	}
	
	static protected String getErrorForUndefinedTranslationAhead(ExBasePattern from) {
		return String.format("Undefined translation ahead : [from]%s", from.toString());
	}
	
	static protected String getErrorForRatioLessThanZero(BigDecimal value) {
		return ("Ratio value must be greater than or equal to zero : " + value.toPlainString());
	}
	
	static protected String getErrorForToAggregatePatternMultiple(ExBasePattern from, ExBasePattern to, String existAttr, String newAttr) {
		return String.format("To aggregate pattern already exist : [from]%s , [to]%s , [exist attribute]%s , [specified attribute]%s",
				from.toString(), to.toString(), existAttr, newAttr);
	}

	//------------------------------------------------------------
	// Public I/O interfaces
	//------------------------------------------------------------
	
	/**
	 * 変換テーブルの内容を、プラットフォーム標準の文字セットで指定のファイルに CSV フォーマットで出力する。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
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
	 * 変換テーブルの内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
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
	 * 変換テーブルの内容を、CSVフォーマットの文字列に変換する。
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
	 * プラットフォーム標準の文字セットで CSV フォーマットのファイルを読み込み、新しい変換テーブルを生成する。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>ExTransfer</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 */
	static public ExTransfer fromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		CsvReader reader = new CsvReader(csvFile);
		ExTransfer newTransfer = new ExTransfer();
		
		try {
			newTransfer.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newTransfer;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しい変換テーブルを生成する。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>ExTransfer</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	static public ExTransfer fromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		ExTransfer newTransfer = new ExTransfer();
		
		try {
			newTransfer.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newTransfer;
	}
	
	/**
	 * CSV フォーマットの文字列を読み込み、新しい変換テーブルを生成する。
	 * 
	 * @param csvString 読み込む CSV フォーマットの文字列
	 * @return 文字列の内容で生成された、新しい <code>ExTransfer</code> インスタンスを返す。
	 * 
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.984
	 */
	static public ExTransfer fromCsvString(String csvString) throws CsvFormatException
	{
		CsvReader reader = new CsvReader(new StringReader(csvString));
		ExTransfer newTransfer = new ExTransfer();
		
		try {
			newTransfer.readFromCSV(reader);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		finally {
			reader.close();
		}
		
		return newTransfer;
	}
	
	/**
	 * 変換テーブルの内容を、指定のファイルに XML フォーマットで出力する。
	 * 
	 * @param xmlFile 出力先ファイル
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 * @throws TransformerConfigurationException <code>Transformer</code> インスタンスを生成できない場合
	 * @throws TransformerException ファイルへの変換中に回復不能なエラーが発生した場合
	 */
	public void toXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, DOMException,
				TransformerConfigurationException, TransformerException
	{
		XmlDocument xml = toXML();
		xml.toFile(xmlFile);
	}
	
	/**
	 * 変換テーブルの内容から、XML ドキュメントを生成する。
	 * 
	 * @return 生成された XML ドキュメント({@link exalge2.io.xml.XmlDocument} インスタンス)
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 */
	public XmlDocument toXML()
		throws FactoryConfigurationError, ParserConfigurationException, DOMException
	{
		XmlDocument xml = new XmlDocument();
		Element root = encodeToDomElement(xml);
		xml.append(root);
		return xml;
	}
	
	/**
	 * 変換テーブルの内容を、XML フォーマットの文字列に変換する。
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
	 * XML フォーマットのファイルを読み込み、新しい変換テーブルを生成する。
	 * 
	 * @param xmlFile 読み込む XML ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>ExTransfer</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 */
	static public ExTransfer fromXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException,
				XmlDomParseException
	{
		XmlDocument xmlDocument = XmlDocument.fromFile(xmlFile);
		return ExTransfer.fromXML(xmlDocument);
	}
	
	/**
	 * 指定された XML ドキュメントを解析し、新しい変換テーブルを生成する。
	 * 
	 * @param xmlDocument 解析対象の XML ドキュメント
	 * 
	 * @return 解析により生成された、新しい <code>ExTransfer</code> インスタンスを返す。
	 * 
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 */
	static public ExTransfer fromXML(XmlDocument xmlDocument)
		throws XmlDomParseException
	{
		Element root = xmlDocument.getDocumentElement();
		
		if (root == null) {
			throw new XmlDomParseException("Root element not found.");
		}
		
		ExTransfer newTransfer = new ExTransfer();
		newTransfer.decodeFromDomElement(xmlDocument, root);
		
		return newTransfer;
	}
	
	/**
	 * XML フォーマットの文字列を読み込み、新しい変換テーブルを生成する。
	 * 
	 * @param xmlString 読み込む XML フォーマットの文字列
	 * 
	 * @return 文字列の内容で生成された、新しい <code>ExTransfer</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.984
	 */
	static public ExTransfer fromXmlString(String xmlString)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException, XmlDomParseException
	{
		XmlDocument xmlDocument = XmlDocument.fromXmlString(xmlString);
		
		return ExTransfer.fromXML(xmlDocument);
	}

	//------------------------------------------------------------
	// Internal methods for I/O
	//------------------------------------------------------------

	/**
	 * 指定された <code>writer</code> に、このオブジェクトの全ての要素を出力する。
	 * 
	 * @param writer	出力に使用する <code>CsvWriter</code> オブジェクト
	 * 
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void writeToCSV(CsvWriter writer) throws IOException
	{
		// write header line
		for (String hname : HEADER_NAMES) {
			writer.writeField(hname);
		}
		writer.newLine();
		
		// write values
		for (Map.Entry<ExBasePattern, ITransferEntryMap> entry : map.entrySet()) {
			ExBasePattern from = entry.getKey();
			ITransferEntryMap entrymap = entry.getValue();
			for (Map.Entry<ExBasePattern, TransferAttribute> elem : entrymap.entrySet()) {
				ExBasePattern to = elem.getKey();
				TransferAttribute attr = elem.getValue();
				//--- write from
				writer.writeField(from.getNameKey());
				writer.writeField(from.getUnitKey());
				writer.writeField(from.getTimeKey());
				writer.writeField(from.getSubjectKey());
				//--- write to
				writer.writeField(to.getNameKey());
				writer.writeField(to.getUnitKey());
				writer.writeField(to.getTimeKey());
				writer.writeField(to.getSubjectKey());
				//--- write attr
				if (ATTR_HAT == attr.getName()) {
					writer.writeField(attr.getName());
					writer.writeField(null);	// 省略フィールド(属性値)
				}
				else if (ATTR_AGGRE == attr.getName()) {
					writer.writeField(null);	// 省略フィールド(属性名)
					writer.writeField(null);	// 省略フィールド(属性値)
				}
				else {
					writer.writeField(attr.getName());
					if (BigDecimal.ZERO.compareTo(attr.getValue()) == 0) {
						writer.writeField(BigDecimal.ZERO.toPlainString());
					} else {
						writer.writeField(attr.getValue().stripTrailingZeros().toPlainString());
					}
				}
				//--- terminate record
				writer.newLine();
			}
		}
		writer.flush();
	}

	/**
	 * 指定された <code>reader</code> を使用して、CSVフォーマットのストリームを読み込み、
	 * このオブジェクトに格納する。
	 * 
	 * @param reader	読み込むストリームとなる <code>CsvReader</code> オブジェクト
	 * 
	 * @throws IOException			入出力エラーが発生した場合
	 * @throws CsvFormatException	CSVフォーマットエラーが発生した場合
	 */
	protected void readFromCSV(CsvReader reader) throws IOException, CsvFormatException
	{
		CsvReader.CsvRecord record;
		
		// コメント行は無効
		reader.setLineCommentEnable(false);
		
		// check header
		record = reader.readCsvRecord();
		if (record == null || !record.hasFields()) {
			// undefined ExTransfer CSV header
			throw new CsvFormatException("ExTransfer header not found.");
		}
		for (int i = 0; i < HEADER_NAMES.length; i++) {
			CsvReader.CsvField field = record.getField(i);
			if (field == null || !HEADER_NAMES[i].equalsIgnoreCase(field.getValue())) {
				throw new CsvFormatException("Illegal ExTransfer header.", record.getLineNo(), (i+1));
			}
		}
		
		// 2行目以降のコメント行は有効
		reader.setLineCommentEnable(true);
		
		// read elements
		while ((record = reader.readCsvRecord()) != null) {
			// 空行、もしくは値のないレコードはスキップ
			if (!record.hasFields() || !record.hasValues()) {
				continue;
			}
			
			// フィールドの値読み出し
			CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);
			//--- from pattern
			ExBasePattern from = ExBasePattern.readFieldFromCSV(freader, false);
			//--- to pattern
			ExBasePattern to = ExBasePattern.readFieldFromCSV(freader, false);
			//--- attr:name
			String inAttr = freader.readTrimmedValue();
			String attrName = normalizeAttributeName(inAttr);
			if (attrName == null) {
				throw new CsvFormatException("Illegal attribute : \"" + inAttr + "\"", freader.getLineNo(), freader.getNextPosition());
			}
			//--- attr:value
			TransferAttribute attr;
			String strValue = freader.readTrimmedValue();
			if (strValue != null && strValue.length() > 0) {
				BigDecimal value = null;
				try {
					value = new BigDecimal(strValue);
				} catch (Throwable ex) {
					ex = null;
				}
				if (value == null) {
					throw new CsvFormatException("Illegal value format : \"" + strValue + "\"", freader.getLineNo(), freader.getNextPosition());
				}
				//--- check value
				if (ATTR_AGGRE == attrName) {
					attr = TransferAttribute.AGGREGATE;
				} else if (ATTR_HAT == attrName) {
					attr = TransferAttribute.HAT_AGGREGATE;
				} else {
					if (BigDecimal.ZERO.compareTo(value) == 0)
						value = BigDecimal.ZERO;
					else
						value = value.stripTrailingZeros();
					if (ATTR_RATIO==attrName && BigDecimal.ZERO.compareTo(value) > 0)
						throw new CsvFormatException(getErrorForRatioLessThanZero(value), freader.getLineNo(), freader.getNextPosition());
					attr = new TransferAttribute(attrName, value);
				}
			}
			else if (ATTR_AGGRE == attrName) {
				attr = TransferAttribute.AGGREGATE;
			}
			else if (ATTR_HAT == attrName) {
				attr = TransferAttribute.HAT_AGGREGATE;
			}
			else {
				// undefined value
				throw new CsvFormatException("Undefined value.", freader.getLineNo(), freader.getNextPosition());
			}
			
			// same conversion pattern check
			if (contains(from, to)) {
				throw new CsvFormatException(String.format("from and to pattern already exist : [from]%s [to]%s", from, to), freader.getLineNo(), -1);
			}
			
			// put
			try {
				put(from, to, attr);
			} catch (IllegalArgumentException ex) {
				throw new CsvFormatException(ex.getLocalizedMessage(), freader.getLineNo(), -1);
			}
		}
		
		// check total ratio
		for (Map.Entry<ExBasePattern, ITransferEntryMap> entry : map.entrySet()) {
			ExBasePattern fromPattern = entry.getKey();
			ITransferEntryMap entrymap = entry.getValue();
			if (ATTR_RATIO == entrymap.getAttributeName()) {
				BigDecimal tr = entrymap.getTotalValue();
				if (tr == null || BigDecimal.ZERO.compareTo(tr) >= 0) {
					// illega total ratio
					throw new CsvFormatException(String.format("[%s] from pattern's total ratio must be greater than zero : total=%s",
							fromPattern.toString(), (tr==null ? "null" : tr.toPlainString())));
				}
			}
		}
	}

	/** &lt;ExTransfer&gt; 要素名 **/
	static protected final String XML_ROOT_ELEMENT_NAME = "ExTransfer";
	/** &lt;ExTransferElem&gt; 要素名 **/
	static protected final String XML_ITEM_ELEMENT_NAME = "ExTransferElem";
	/** &lt;ElemFrom&gt; 要素名 **/
	static protected final String XML_FROM_ELEMENT_NAME = "ElemFrom";
	/** &lt;ElemTo&gt; 要素名 **/
	static protected final String XML_TO_ELEMENT_NAME   = "ElemTo";
	/** &lt;ElemTo&gt; 要素の 'attribute' 属性名 **/
	static protected final String XML_ELEMTO_ATTR       = "attribute";
	/** &lt;ElemTo&gt; 要素の 'value' 属性名 **/
	static protected final String XML_ELEMTO_VALUE      = "value";

	/**
	 * このオブジェクトの全ての要素を、XML DOM ドキュメントのエレメントに変換する。
	 * 
	 * @param xmlDocument	エレメント生成に使用する XML DOM ドキュメント
	 * @return	生成されたエレメントを返す。
	 * @throws DOMException	タグの生成に失敗した場合
	 */
	protected Element encodeToDomElement(XmlDocument xmlDocument)	throws DOMException
	{
		// create node
		Element node = xmlDocument.createElement(XML_ROOT_ELEMENT_NAME);

		// create items
		for (Map.Entry<ExBasePattern, ITransferEntryMap> entry : map.entrySet()) {
			// create item element
			Element itemElement = xmlDocument.createElement(XML_ITEM_ELEMENT_NAME);
			// create ElemFrom element
			Element fromElement = xmlDocument.createElement(XML_FROM_ELEMENT_NAME);
			ExBasePattern from = entry.getKey();
			fromElement.appendChild(from.encodeToDomElement(xmlDocument, false));
			itemElement.appendChild(fromElement);
			// create ElemTo element
			ITransferEntryMap entrymap = entry.getValue();
			for (Map.Entry<ExBasePattern, TransferAttribute> elem : entrymap.entrySet()) {
				ExBasePattern to = elem.getKey();
				TransferAttribute attr = elem.getValue();
				Element toElement = xmlDocument.createElement(XML_TO_ELEMENT_NAME);
				if (ATTR_HAT == attr.getName()) {
					toElement.setAttribute(XML_ELEMTO_ATTR, attr.getName());
				}
				else if (ATTR_AGGRE != attr.getName()) {
					toElement.setAttribute(XML_ELEMTO_ATTR, attr.getName());
					if (BigDecimal.ZERO.compareTo(attr.getValue()) == 0) {
						toElement.setAttribute(XML_ELEMTO_VALUE, BigDecimal.ZERO.toPlainString());
					} else {
						toElement.setAttribute(XML_ELEMTO_VALUE, attr.getValue().stripTrailingZeros().toPlainString());
					}
				}
				//--- ATTR_AGGRE の場合、XMLノードの属性は省略する
				toElement.appendChild(to.encodeToDomElement(xmlDocument, false));
				itemElement.appendChild(toElement);
			}
			node.appendChild(itemElement);
		}
		
		// completed
		return node;
	}

	/**
	 * 指定された XML DOM ドキュメントのルート要素から、このオブジェクトの要素を復元する。
	 * 復元した要素は、このオブジェクトに格納される。
	 * 
	 * @param xmlDocument	XML DOM ドキュメント
	 * @param xmlElement	ルート要素
	 * @throws XmlDomParseException	XML ノード解析エラーが発生した場合
	 */
	protected void decodeFromDomElement(XmlDocument xmlDocument, Element xmlElement) throws XmlDomParseException
	{
		// check Element name
		String strInput = xmlElement.getNodeName();
		if (!strInput.equals(XML_ROOT_ELEMENT_NAME)) {
			// Illegal element name
			throw new XmlDomParseException(String.format("Root element must be <%s> : <%s>", XML_ROOT_ELEMENT_NAME, String.valueOf(strInput)));
		}
		
		// get child nodes
		Node child = xmlElement.getFirstChild();
		for (; child != null; child = child.getNextSibling()) {
			if (Node.ELEMENT_NODE == child.getNodeType()) {
				// decode item element
				decodeItemFromDomElement(xmlDocument, (Element)child);
			}
		}
		
		// check total ratio
		for (Map.Entry<ExBasePattern, ITransferEntryMap> entry : map.entrySet()) {
			ExBasePattern fromPattern = entry.getKey();
			ITransferEntryMap entrymap = entry.getValue();
			if (ATTR_RATIO == entrymap.getAttributeName()) {
				BigDecimal tr = entrymap.getTotalValue();
				if (tr == null || BigDecimal.ZERO.compareTo(tr) >= 0) {
					// illega total ratio
					throw new XmlDomParseException(String.format("[%s] from pattern's total ratio must be greater than zero : total=%s",
							fromPattern.toString(), (tr==null ? "null" : tr.toPlainString())));
				}
			}
		}
	}

	/**
	 * &lt;ExBase&gt; 要素を格納する要素から、交換代数基底パターンを復元する。
	 * &lt;ExBase&gt; 要素は子要素として唯一でない場合は、<code>XmlDomParseException</code> をスローする。
	 * 
	 * @param xmlDocument	XML DOM ドキュメント
	 * @param xmlElement	&lt;ExBase&gt; 要素を子に持つ要素
	 * @return	復元された <code>ExBasePattern</code> オブジェクト
	 * @throws XmlDomParseException	XML ノード解析エラーが発生した場合
	 */
	protected ExBasePattern decodePatternElement(XmlDocument xmlDocument, Element xmlElement) throws XmlDomParseException
	{
		ExBasePattern elemPattern = null;
		Node child = xmlElement.getFirstChild();
		for (; child != null ; child = child.getNextSibling()) {
			if (Node.ELEMENT_NODE == child.getNodeType()) {
				ExBasePattern pat = ExBasePattern.decodeFromDomElement(xmlDocument, (Element)child, false);
				if (elemPattern != null) {
					throw new XmlDomParseException(String.format("<%s> element must not be multiple.", String.valueOf(child.getNodeName())));
				}
				elemPattern = pat;
			}
		}
		
		if (elemPattern == null) {
			throw new XmlDomParseException(String.format("<%s> element not found in <%s>.", ExBasePattern.XML_ELEMENT_NAME, xmlElement.getNodeName()));
		}
		
		return elemPattern;
	}

	/**
	 * &lt;ElemTo&gt; 要素から、変換先基底パターンと変換属性を復元する。
	 * @param xmlDocument		XML DOM ドキュメント
	 * @param elemToElement		&lt;ElemTo&gt; 要素
	 * @return	復元された <code>ExBasePattern</code> と <code>TransferAttribute</code> オブジェクトを格納する
	 * 			配列を返す。この配列は、要素[0]に <code>ExBasePattern</code> インスタンス、要素[1]に <code>TransferAttribute</code> インスタンスを格納する。
	 * @throws XmlDomParseException	XML ノード解析エラーが発生した場合
	 */
	protected Object[] decodeElemTo(XmlDocument xmlDocument, Element elemToElement) throws XmlDomParseException
	{
		// get ExBasePattern
		ExBasePattern elemPattern = decodePatternElement(xmlDocument, elemToElement);
		
		// get [attribute] attribute
		String strAttr = elemToElement.getAttribute(XML_ELEMTO_ATTR);
		String normAttrName = normalizeAttributeName(strAttr);
		if (normAttrName == null) {
			throw new XmlDomParseException(String.format("Illegal attribute in <%s> : [%s]=\"%s\"",
					elemToElement.getNodeName(), XML_ELEMTO_ATTR, String.valueOf(strAttr)));
		}
		
		// get [value] attribute
		TransferAttribute attr;
		String strValue = elemToElement.getAttribute(XML_ELEMTO_VALUE);
		if (strValue != null && strValue.length() > 0) {
			BigDecimal value = null;
			try {
				value = new BigDecimal(strValue);
			} catch (Throwable ex) {
				ex = null;
			}
			if (value == null) {
				throw new XmlDomParseException(String.format("Illegal value format : [%s]=\"%s\"",
						XML_ELEMTO_VALUE, strValue));
			}
			//--- check value
			if (ATTR_AGGRE == normAttrName) {
				attr = TransferAttribute.AGGREGATE;
			} else if (ATTR_HAT == normAttrName) {
				attr = TransferAttribute.HAT_AGGREGATE;
			} else {
				if (BigDecimal.ZERO.compareTo(value) == 0)
					value = BigDecimal.ZERO;
				else
					value = value.stripTrailingZeros();
				if (ATTR_RATIO==normAttrName && BigDecimal.ZERO.compareTo(value) > 0) {
					throw new XmlDomParseException(getErrorForRatioLessThanZero(value));
				}
				attr = new TransferAttribute(normAttrName, value);
			}
		}
		else if (ATTR_AGGRE == normAttrName) {
			attr = TransferAttribute.AGGREGATE;
		}
		else if (ATTR_HAT == normAttrName) {
			attr = TransferAttribute.HAT_AGGREGATE;
		}
		else {
			// undefined value
			throw new XmlDomParseException(String.format("Undefined value : [%s]=\"%s\"", XML_ELEMTO_VALUE, String.valueOf(strValue)));
		}
		
		// return values
		return new Object[]{elemPattern, attr};
	}

	/**
	 * 一つの変換元基底パターンと、その変換元基底パターンの変換先定義を保持する &lt;ExTransferElem&gt; 要素から変換定義を復元し、
	 * このオブジェクトに格納する。
	 * 
	 * @param xmlDocument	XML DOM ドキュメント
	 * @param itemElement	&lt;ExTransferElem&gt; 要素
	 * @throws XmlDomParseException	XML ノード解析エラーが発生した場合
	 */
	protected void decodeItemFromDomElement(XmlDocument xmlDocument, Element itemElement) throws XmlDomParseException
	{
		// check item element
		String strName = itemElement.getNodeName();
		if (!strName.equals(XML_ITEM_ELEMENT_NAME)) {
			// Illegal element name
			throw new XmlDomParseException(String.format("The child of <%s> must be <%s> : <%s>",
					String.valueOf(itemElement.getNodeName()), XML_ITEM_ELEMENT_NAME, String.valueOf(strName)));
		}
		
		// parse item
		Node child;
		
		// search first element
		ExBasePattern fromPattern = null;
		child = itemElement.getFirstChild();
		for (; child != null; child = child.getNextSibling()) {
			if (Node.ELEMENT_NODE == child.getNodeType()) {
				strName = child.getNodeName();
				if (strName.equals(XML_FROM_ELEMENT_NAME)) {
					fromPattern = decodePatternElement(xmlDocument, (Element)child);
					break;
				} else if (strName.equals(XML_TO_ELEMENT_NAME)) {
					throw new XmlDomParseException(String.format("<%s> element not found before <%s>.", XML_FROM_ELEMENT_NAME, XML_TO_ELEMENT_NAME));
				} else {
					// illegal element
					throw new XmlDomParseException(String.format("The child of <%s> must be <%s> or <%s> : <%s>",
							String.valueOf(itemElement.getNodeName()), XML_FROM_ELEMENT_NAME, XML_TO_ELEMENT_NAME, String.valueOf(strName)));
				}
			}
		}
		
		// parse <ElemTo> element
		child = child.getNextSibling();
		for (; child != null; child = child.getNextSibling()) {
			if (Node.ELEMENT_NODE == child.getNodeType()) {
				strName = child.getNodeName();
				if (strName.equals(XML_TO_ELEMENT_NAME)) {
					Object[] ret = decodeElemTo(xmlDocument, (Element)child);
					ExBasePattern toPattern = (ExBasePattern)ret[0];
					// same conversion pattern check
					if (contains(fromPattern, toPattern)) {
						throw new XmlDomParseException(String.format("from and to pattern already exist : [from]%s [to]%s", fromPattern, toPattern));
					}
					// put
					try {
						put(fromPattern, toPattern, (TransferAttribute)ret[1]);
					} catch (IllegalArgumentException ex) {
						throw new XmlDomParseException(ex.getLocalizedMessage());
					}
				} else if (strName.equals(XML_FROM_ELEMENT_NAME)) {
					throw new XmlDomParseException(String.format("<%s> element must not be multiple.", String.valueOf(strName)));
				} else {
					// illegal element
					throw new XmlDomParseException(String.format("The child of <%s> must be <%s> or <%s> : <%s>",
							String.valueOf(itemElement.getNodeName()), XML_FROM_ELEMENT_NAME, XML_TO_ELEMENT_NAME, String.valueOf(strName)));
				}
			}
		}
	}

	//------------------------------------------------------------
	// Internal classes
	//------------------------------------------------------------

	/**
	 * 変換属性の名前と値のペア。
	 * このオブジェクトは、不変オブジェクトである。
	 * 
	 * @version 0.982	2009/09/13
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 * 
	 * @since 0.982
	 */
	static protected class TransferAttribute
	{
		static public final TransferAttribute AGGREGATE = new TransferAttribute(ATTR_AGGRE, BigDecimal.ONE);
		static public final TransferAttribute HAT_AGGREGATE = new TransferAttribute(ATTR_HAT, BigDecimal.ONE);
		
		private final String     name;
		private final BigDecimal value;
		private final int        hash;
		
		public TransferAttribute(String name, BigDecimal value) {
			int hn = name.hashCode();
			int hv;
			//--- name
			this.name = name;
			//--- value
			if (value == null) {
				this.value = null;
				hv = 0;
			}
			else if (BigDecimal.ZERO.compareTo(value) == 0) {
				this.value = BigDecimal.ZERO;
				hv = 0;
			}
			else {
				this.value = value.stripTrailingZeros();
				hv = this.value.hashCode();
			}
			this.hash = (hn ^ hv);
		}
		
		public String getName() {
			return name;
		}
		
		public BigDecimal getValue() {
			return value;
		}

		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			
			if (obj instanceof TransferAttribute) {
				TransferAttribute aAttr = (TransferAttribute)obj;
				if (aAttr.name.equals(this.name)) {
					if (aAttr.value != null) {
						if (this.value != null && aAttr.value.compareTo(this.value)==0) {
							return true;
						}
					}
					else if (this.value == null) {
						// aAttr.value == this.value == null
						return true;
					}
				}
			}
			
			return false;
		}

		@Override
		public String toString() {
			if (value != null)
				return (name+":"+value.toPlainString());
			else
				return (name+":null");
		}
	}

	/**
	 * 変換先基底パターンと属性との関連付けを保持するマップのインタフェース。
	 * 
	 * @version 0.982	2009/09/13
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 * 
	 * @since 0.982
	 */
	static protected interface ITransferEntryMap extends Map<ExBasePattern,TransferAttribute>
	{
		public String getAttributeName();
		public BigDecimal getTotalValue();
		public void updateTotalValue();
		public void transform(ExBaseSet transBases, final ExBase targetBase);
		public void transfer(Exalge transAlge, final ExBase targetBase, final BigDecimal targetValue);
	}

	/**
	 * <code>'aggre'</code> 属性、もしくは <code>'hat'</code> 属性の変換先基底パターンを保持するマップ。
	 * このマップは、要素を一つだけ保持する不変のマップである。
	 * 
	 * @version 0.982	2009/09/13
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 * 
	 * @since 0.982
	 */
	static protected class TransferAggreEntry extends AbstractMap<ExBasePattern,TransferAttribute> implements ITransferEntryMap
	{
		private final ExBasePattern		pattern;
		private final TransferAttribute	attr;
		
		public TransferAggreEntry(ExBasePattern pattern, TransferAttribute attr) {
			this.pattern = pattern;
			this.attr    = attr;
		}
		
		public String getAttributeName() {
			return attr.getName();
		}
		
		public BigDecimal getTotalValue() {
			return attr.getValue();
		}
		
		public void updateTotalValue() {}
		
		public void transform(ExBaseSet transBases, final ExBase targetBase) {
			ExBase retBase = pattern.translate(targetBase);
			if (ATTR_HAT == attr.getName()) {
				retBase = retBase.hat();
			}
			transBases.fastAdd(retBase);
		}
		
		public void transfer(Exalge transAlge, final ExBase targetBase, final BigDecimal targetValue) {
			ExBase retBase = pattern.translate(targetBase);
			if (ATTR_HAT == attr.getName()) {
				retBase = retBase.hat();
			}
			transAlge.plusValue(retBase, targetValue);
		}
		
		public int size() {
			return 1;
		}
		
		public boolean isEmpty() {
			return false;
		}
		
		public boolean containsKey(Object key) {
			return eq(key, pattern);
		}
		
		public boolean containsValue(Object value) {
			return eq(value, attr);
		}
		
		public TransferAttribute get(Object key) {
			return (eq(key, pattern) ? attr : null);
		}
		
		private transient Set<ExBasePattern> keySet = null;
		private transient Set<Map.Entry<ExBasePattern, TransferAttribute>> entrySet = null;
		private transient Collection<TransferAttribute> values = null;
		
		public Set<ExBasePattern> keySet() {
			if (keySet == null)
				keySet = Collections.singleton(pattern);
			return keySet;
		}
		
		public Set<Map.Entry<ExBasePattern, TransferAttribute>> entrySet() {
			if (entrySet == null)
				entrySet = Collections.singleton((Map.Entry<ExBasePattern,TransferAttribute>)new ImmutableEntry<ExBasePattern,TransferAttribute>(pattern,attr));
			return entrySet;
		}
		
		public Collection<TransferAttribute> values() {
			if (values == null)
				values = Collections.singleton(attr);
			return values;
		}
		
		static private class ImmutableEntry<K,V> implements Map.Entry<K,V> {
			final K k;
			final V v;
			
			ImmutableEntry(K key, V value) {
				k = key;
				v = value;
			}
			
			public K getKey()   { return k; }
			public V getValue() { return v; }
			
			public V setValue(V value) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean equals(Object obj) {
				if (!(obj instanceof Map.Entry))
					return false;
				Map.Entry e = (Map.Entry)obj;
				return (eq(e.getKey(), k) && eq(e.getValue(), v));
			}

			@Override
			public int hashCode() {
				return ((k==null ? 0 : k.hashCode()) ^ (v==null ? 0 : v.hashCode()));
			}

			@Override
			public String toString() {
				return (k+"="+v);
			}
		}
	}

	/**
	 * 変換先基底パターンと変換属性との関連付けを保持するマップ。
	 * このマップには、<code>'ratio'</code> 属性と <code>'multiply'</code> 属性のみの関連付けが保持される。
	 * 
	 * @version 0.982	2009/09/13
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 * 
	 * @since 0.982
	 */
	static protected class TransferEntryMap extends LinkedHashMap<ExBasePattern,TransferAttribute> implements ITransferEntryMap
	{
		private String     attrName;
		private BigDecimal totalValue;

		/**
		 * 指定された変換先基底パターンと属性との関連付けのみを保持する
		 * <code>TransferEntryMap</code> を生成する。
		 * @param key	変換先基底パターン
		 * @param value	属性
		 */
		public TransferEntryMap(ExBasePattern key, TransferAttribute value) {
			super();
			put(key, value);
			this.attrName = value.getName();
		}
		
		public String getAttributeName() {
			return attrName;
		}
		
		public BigDecimal getTotalValue() {
			if (totalValue == null) {
				updateTotalValue();
			}
			return totalValue;
		}
		
		public void updateTotalValue() {
			BigDecimal total = BigDecimal.ZERO;
			for (TransferAttribute attr : values()) {
				total = total.add(attr.getValue());
			}
			totalValue = total;
		}
		
		public void transform(ExBaseSet transBases, final ExBase targetBase) {
			for (ExBasePattern pattern : this.keySet()) {
				transBases.fastAdd(pattern.translate(targetBase));
			}
		}
		
		public void transfer(Exalge transAlge, final ExBase targetBase, final BigDecimal targetValue) {
			if (attrName == ATTR_RATIO) {
				// 按分
				BigDecimal totalRatio = getTotalValue();
				if (BigDecimal.ONE.compareTo(totalRatio) == 0) {
					//--- 合計値が 1 のため、そのまま乗算
					transMultiply(transAlge, targetBase, targetValue);
				} else {
					//--- 合計値が 1 ではないので、合計値で割る
					transDivide(transAlge, totalRatio, targetBase, targetValue);
				}
			} else {
				// 乗算
				transMultiply(transAlge, targetBase, targetValue);
			}
		}

		/**
		 * パラメータによる案分。
		 * @param transAlge		計算結果を格納する交換代数元のインスタンス
		 * @param totalRatio	案分比の合計値
		 * @param targetBase	計算対象の基底
		 * @param targetValue	計算対象の値
		 */
		protected void transDivide(Exalge transAlge, final BigDecimal totalRatio, final ExBase targetBase, final BigDecimal targetValue) {
			//@@@ modified by Y.Ishizuka : 2014.04.28 @@@
			for (Map.Entry<ExBasePattern, TransferAttribute> entry : this.entrySet()) {
				//--- 基底の変換
				ExBase rBase = entry.getKey().translate(targetBase);
				//--- 案分(targetValue*(ratio/totalRatio))
				BigDecimal dRate = entry.getValue().getValue().divide(totalRatio, MathContext.DECIMAL128);
				BigDecimal rValue = targetValue.multiply(dRate);
				//--- 結果の代入
				transAlge.plusValue(rBase, rValue);
			}
			/*@@@ old codes : 2014.04.28 @@@
			//--- 計算対象値を案分比合計値で除算
			BigDecimal unitValue = targetValue.divide(totalRatio, MathContext.DECIMAL128);
			for (Map.Entry<ExBasePattern, TransferAttribute> entry : this.entrySet()) {
				//--- 基底の変換
				ExBase rBase = entry.getKey().translate(targetBase);
				//--- 単位値を乗算
				BigDecimal rValue = unitValue.multiply(entry.getValue().getValue());
				//--- 結果の代入
				transAlge.plusValue(rBase, rValue);
			}
			**@@@ end of old codes : 2014.04.28 @@@*/
			//@@@ end of modified : 2014.04.28 @@@
		}
		
		/**
		 * パラメータによる乗算。
		 * @param transAlge		計算結果を格納する交換代数元のインスタンス
		 * @param targetBase	計算対象の基底
		 * @param targetValue	計算対象の値
		 */
		protected void transMultiply(Exalge transAlge, final ExBase targetBase, final BigDecimal targetValue) {
			for (Map.Entry<ExBasePattern, TransferAttribute> entry : this.entrySet()) {
				//--- 基底の変換
				ExBase rBase = entry.getKey().translate(targetBase);
				//--- 乗算
				BigDecimal rValue = targetValue.multiply(entry.getValue().getValue());
				//--- 結果の代入
				transAlge.plusValue(rBase, rValue);
			}
		}

		@Override
		public void clear() {
			this.totalValue = null;
			super.clear();
		}

		@Override
		public TransferAttribute put(ExBasePattern key, TransferAttribute value) {
			this.totalValue = null;
			return super.put(key, value);
		}

		@Override
		public void putAll(Map<? extends ExBasePattern, ? extends TransferAttribute> m) {
			this.totalValue = null;
			super.putAll(m);
		}

		@Override
		public TransferAttribute remove(Object key) {
			TransferAttribute removed = super.remove(key);
			if (removed != null) {
				this.totalValue = null;
			}
			return removed;
		}
	}
	
	/**
	 * 基底パターンのマルチマップ。
	 * 一つの基底パターンに対して複数の基底パターンとのマップを保持する。
	 * 
	 * @version 0.982	2009/10/09
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 * 
	 * @since 0.982
	 */
	static protected final class ExBasePatternMultiMap
	{
		protected final LinkedHashMap<ExBasePattern,ExBasePatternSet> map;
		
		public ExBasePatternMultiMap() {
			this.map = new LinkedHashMap<ExBasePattern,ExBasePatternSet>();
		}
		
		public ExBasePatternMultiMap(int initialCapacity) {
			this.map = new LinkedHashMap<ExBasePattern,ExBasePatternSet>(initialCapacity);
		}
		
		public ExBasePatternMultiMap(int initialCapacity, float loadFactor) {
			this.map = new LinkedHashMap<ExBasePattern,ExBasePatternSet>(initialCapacity, loadFactor);
		}
		
		public boolean isEmpty() {
			return map.isEmpty();
		}
		
		public int size() {
			return map.size();
		}
		
		public void clear() {
			map.clear();
		}
		
		public boolean contains(ExBasePattern key, ExBasePattern value) {
			if (!map.isEmpty()) {
				ExBasePatternSet set = map.get(key);
				if (set != null) {
					return set.contains(value);
				}
			}
			
			return false;
		}
		
		public boolean containsKey(Object key) {
			return map.containsKey(key);
		}
		
		public boolean containsValue(Object value) {
			if (!map.isEmpty()) {
				for (ExBasePatternSet set : map.values()) {
					if (set.contains(value)) {
						return true;
					}
				}
			}
			
			return false;
		}
		
		public Set<ExBasePattern> keySet() {
			return map.keySet();
		}
		
		public Collection<ExBasePatternSet> values() {
			return map.values();
		}
		
		public ExBasePatternSet get(Object key) {
			return map.get(key);
		}
		
		public boolean put(ExBasePattern key, ExBasePattern value) {
			if (key == null || value == null)
				throw new NullPointerException();
			
			return fastPut(key, value);
		}
		
		public boolean remove(ExBasePattern key, ExBasePattern value) {
			ExBasePatternSet set = map.get(key);
			if (set != null) {
				if (set.remove(value)) {
					if (set.isEmpty()) {
						map.remove(key);
					}
					return true;
				}
			}
			
			return false;
		}
		
		public boolean removeKey(ExBasePattern key) {
			return (map.remove(key) != null);
		}
		
		protected boolean fastPut(ExBasePattern key, ExBasePattern value) {
			//--- 値の整合性チェックはしない
			ExBasePatternSet set = map.get(key);
			if (set == null) {
				set = new ExBasePatternSet();
				set.fastAdd(value);
				map.put(key, set);
				return true;
			}
			
			return set.fastAdd(value);
		}

		@Override
		public int hashCode() {
			return map.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			
			if (obj instanceof ExBasePatternMultiMap) {
				return map.equals(((ExBasePatternMultiMap)obj).map);
			}
			
			return false;
		}

		@Override
		public String toString() {
			return map.toString();
		}
	}

	/**
	 * 基底パターンの基底キーインデックス集合。
	 * この集合では、ワイルドカードを含まない固定文字列を基底キーインデックスとして保持し、
	 * 高速な検索に対応する。
	 * なお、この集合はその特性上、要素数の取得やイテレートを行うためのインタフェースは
	 * 提供しない。
	 * 
	 * @version 0.982	2009/09/13
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 * 
	 * @since 0.982
	 */
	static protected final class ExBasePatternIndexSet
	{
		/**
		 * 基底キーについての固定文字列と基底パターンとの対応を保持するマップ。
		 */
		static private final class ExBasePatternIndex extends HashMap<String,Set<ExBasePattern>>
		{
			/**
			 * デフォルトの初期容量と負荷係数で、空の <code>ExBasePatternIndex</code> を生成する。
			 */
			public ExBasePatternIndex() {
				super();
			}

			/**
			 * 指定された初期容量と負荷係数で、空の <code>ExBasePatternIndex</code> を作成する。
			 * @param initialCapacity	初期容量
			 * @param loadFactor		負荷係数
			 * @throws IllegalArgumentException	初期容量が負であるか、負荷係数が正ではない場合
			 */
			public ExBasePatternIndex(int initialCapacity, float loadFactor) {
				super(initialCapacity, loadFactor);
			}

			/**
			 * 指定された初期容量とデフォルトの負荷係数で、空の <code>ExBasePatternIndex</code> を生成する。
			 * @param initialCapacity	初期容量
			 * @throws IllegalArgumentException	初期容量が負の場合
			 */
			public ExBasePatternIndex(int initialCapacity) {
				super(initialCapacity);
			}

			/**
			 * キーと値の関連付けを追加する。
			 * @param key		基底キーの固定文字列
			 * @param value		基底パターン
			 * @return	指定された基底キーと基底パターンとの関連付けが存在しなかった場合に <tt>true</tt> を返す。
			 */
			public boolean add(String key, ExBasePattern value) {
				Set<ExBasePattern> set = get(key);
				if (set == null) {
					set = new LinkedHashSet<ExBasePattern>();
					set.add(value);
					put(key, set);
					return true;
				} else {
					return set.add(value);
				}
			}

			/**
			 * キーと値の関連付けを削除する。
			 * @param key		基底キーの固定文字列
			 * @param value		基底パターン
			 * @return	指定された基底キーと基底パターンとの関連付けを削除した場合に <tt>true</tt> を返す。
			 */
			public boolean remove(String key, ExBasePattern value) {
				boolean removed = false;
				Set<ExBasePattern> set = get(key);
				if (set != null) {
					removed = set.remove(value);
					if (set.isEmpty()) {
						remove(key);
					}
				}
				return removed;
			}
		}

		/** 固定キーを含むパターンのインデックス **/
		protected final ExBasePatternIndex[] idx = new ExBasePatternIndex[ExBase.NUM_ALL_KEYS];
		/** 固定キーを含まないパターン **/
		protected final ExBasePatternSet vpat = new ExBasePatternSet();

		/**
		 * 空の <code>ExBasePatternIndexSet</code> を生成する。
		 */
		public ExBasePatternIndexSet() {
			//--- HATキーはインデックスを作成しない
			idx[ExBase.KEY_HAT]         = null;
			idx[ExBase.KEY_NAME]        = new ExBasePatternIndex();
			idx[ExBase.KEY_EXT_UNIT]    = new ExBasePatternIndex();
			idx[ExBase.KEY_EXT_TIME]    = new ExBasePatternIndex();
			idx[ExBase.KEY_EXT_SUBJECT] = new ExBasePatternIndex();
		}

		/**
		 * 固定文字列を持つ基底パターンが登録されていない場合に <tt>true</tt> を返す。
		 * @return
		 */
		protected boolean isEmptyFixedPattern() {
			return (idx[ExBase.KEY_NAME].isEmpty()
					&& idx[ExBase.KEY_EXT_UNIT].isEmpty()
					&& idx[ExBase.KEY_EXT_TIME].isEmpty()
					&& idx[ExBase.KEY_EXT_SUBJECT].isEmpty());
		}

		/**
		 * 基底パターンのエントリが空の場合に <tt>true</tt> を返す。
		 */
		public boolean isEmpty() {
			return (isEmptyFixedPattern() && vpat.isEmpty());
		}

		/**
		 * 基底パターンのエントリを全て削除する。
		 */
		public void clear() {
			idx[ExBase.KEY_NAME]       .clear();
			idx[ExBase.KEY_EXT_UNIT]   .clear();
			idx[ExBase.KEY_EXT_TIME]   .clear();
			idx[ExBase.KEY_EXT_SUBJECT].clear();
			vpat.clear();
		}
		
		protected Map<String,Set<ExBasePattern>> getKeyIndex(int keyIndex) {
			return idx[keyIndex];
		}
		
		protected Map<String,Set<ExBasePattern>> getNameKeyIndex() {
			return idx[ExBase.KEY_NAME];
		}
		
		protected Map<String,Set<ExBasePattern>> getUnitKeyIndex() {
			return idx[ExBase.KEY_EXT_UNIT];
		}
		
		protected Map<String,Set<ExBasePattern>> getTimeKeyIndex() {
			return idx[ExBase.KEY_EXT_TIME];
		}
		
		protected Map<String,Set<ExBasePattern>> getSubjectKeyIndex() {
			return idx[ExBase.KEY_EXT_SUBJECT];
		}
		
		protected Set<ExBasePattern> getNoIndexPatterns() {
			return vpat;
		}

		/**
		 * 基底パターンのエントリを追加する。
		 * このメソッドは、指定された基底パターンにワイルドカードを含まない固定文字列が基底キーとして
		 * 指定されている場合、そのパターンのインデックスを生成する。固定文字列が基底キーに含まれない
		 * 基底パターンはインデックスを持たないエントリとして追加される。
		 * @param pattern	登録する基底パターン
		 */
		public void add(ExBasePattern pattern) {
			boolean allWildcard = true;
			ExBasePattern.PatternItem[] items = pattern.getPatternItems();
			if (items != null && items.length > 0) {
				for (ExBasePattern.PatternItem item : items) {
					if (ExBasePattern.PatternItem.PATTERN_FIXED==item.getPatternID() && ExBase.KEY_HAT!=item.getKeyIndex()) {
						allWildcard = false;
						String key = (String)item.getPattern();
						idx[item.getKeyIndex()].add(key, pattern);
					}
				}
			}
			if (allWildcard) {
				vpat.add(pattern);
			}
		}

		/**
		 * 指定されたコレクションに含まれる全ての基底パターンをこのインデックスに追加する。
		 * @param patterns	基底パターンのコレクション
		 */
		public void addAll(Collection<? extends ExBasePattern> patterns) {
			if (patterns != null) {
				for (ExBasePattern pat : patterns) {
					add(pat);
				}
			}
		}

		/**
		 * 指定された基底パターンをこのインデックスから削除する。
		 * @param pattern	削除する基底パターン
		 */
		public void remove(ExBasePattern pattern) {
			if (pattern != null) {
				ExBasePattern.PatternItem[] items = pattern.getPatternItems();
				if (items != null && items.length > 0) {
					for (ExBasePattern.PatternItem item : items) {
						if (ExBasePattern.PatternItem.PATTERN_FIXED==item.getPatternID() && ExBase.KEY_HAT!=item.getKeyIndex()) {
							String key = (String)item.getPattern();
							idx[item.getKeyIndex()].remove(key, pattern);
						}
					}
				}
			}
			vpat.remove(pattern);
		}

		/**
		 * 指定されたコレクションに含まれる全ての基底パターンをこのインデックスから削除する。
		 * @param patterns	削除する基底パターンのコレクション
		 */
		public void removeAll(Collection<? extends ExBasePattern> patterns) {
			if (patterns != null) {
				for (ExBasePattern pat : patterns) {
					remove(pat);
				}
			}
		}

		/**
		 * 指定された基底に一致するパターンを、固定キーインデックスから検索する。
		 * 
		 * @param targetBase	検証する基底
		 * @return	一致するパターンが存在する場合はそのパターンを返す。存在しない場合は <tt>null</tt> を返す。
		 * 
		 * @throws NullPointerException	引数が <tt>null</tt> の場合
		 */
		protected ExBasePattern matchesFixedPatternIndex(ExBase targetBase) {
			int maxElements = Integer.MAX_VALUE;
			Set<ExBasePattern> validIndex = null;
			Set<ExBasePattern> index;
			
			//--- name
			index = idx[ExBase.KEY_NAME].get(targetBase.getNameKey());
			if (index != null && !index.isEmpty()) {
				validIndex = index;
				maxElements = index.size();
			}
			
			//--- unit
			index = idx[ExBase.KEY_EXT_UNIT].get(targetBase.getUnitKey());
			if (index != null && !index.isEmpty()) {
				if (validIndex == null || maxElements > index.size()) {
					validIndex = index;
					maxElements = index.size();
				}
			}
			
			//--- time
			index = idx[ExBase.KEY_EXT_TIME].get(targetBase.getTimeKey());
			if (index != null && !index.isEmpty()) {
				if (validIndex == null || maxElements > index.size()) {
					validIndex = index;
					maxElements = index.size();
				}
			}
			
			//--- subject
			index = idx[ExBase.KEY_EXT_SUBJECT].get(targetBase.getSubjectKey());
			if (index != null && !index.isEmpty()) {
				if (validIndex == null || maxElements > index.size()) {
					validIndex = index;
					maxElements = index.size();
				}
			}
			
			// matches
			if (validIndex != null) {
				// 固定キーを含むパターンとの一致検証
				for (ExBasePattern pat : validIndex) {
					if (pat.matches(targetBase)) {
						return pat;
					}
				}
			}
			//--- no matches
			return null;
		}

		/**
		 * 指定された基底に一致するパターンを、固定キーを含まないパターンから検索する。
		 * 
		 * @param targetBase	検証する基底
		 * @return	一致するパターンが存在する場合はそのパターンを返す。存在しない場合は <tt>null</tt> を返す。
		 */
		protected ExBasePattern matchesAllWildcardPattern(ExBase targetBase) {
			// 固定キーを含まないパターンとの一致検証
			for (ExBasePattern pat : vpat) {
				if (pat.matches(targetBase)) {
					return pat;
				}
			}
			//--- no matches
			return null;
		}

		/**
		 * 指定された基底に一致する基底パターンを検索する。
		 * @param targetBase	検証する基底
		 * @return	登録されている基底パターンの中で、指定された基底と一致するパターンを返す。
		 * 			一致するパターンが存在しない場合は <tt>null</tt> を返す。
		 * @throws NullPointerException	引数が <tt>null</tt> の場合
		 */
		public ExBasePattern matches(ExBase targetBase) {
			// 固定キーを含むパターンとの一致検証
			ExBasePattern result = matchesFixedPatternIndex(targetBase);
			if (result != null) {
				return result;
			}
			
			// 固定キーを含まないパターンとの一致検証
			return matchesAllWildcardPattern(targetBase);
		}
	}
}
