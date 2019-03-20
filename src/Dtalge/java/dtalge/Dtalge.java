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
 *  Copyright 2007-2012  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)Dtalge.java	0.40	2012/06/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Dtalge.java	0.30	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Dtalge.java	0.20	2010/03/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Dtalge.java	0.10	2008/08/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import dtalge.exception.CsvFormatException;
import dtalge.exception.DtBaseNotFoundException;
import dtalge.exception.IllegalValueOfDataTypeException;
import dtalge.io.IDataOutput;
import dtalge.io.internal.CsvReader;
import dtalge.io.internal.CsvWriter;
import dtalge.io.internal.XmlDocument;
import dtalge.util.DtDataTypes;
import dtalge.util.Strings;
import dtalge.util.Validations;
import dtalge.util.internal.CacheManager;
import dtalge.util.internal.XmlErrors;

/**
 * データ代数の基底と値を保持するデータ代数元クラス。
 * 
 * <p>このクラスは、不変オブジェクト(Immutable)である。
 * 
 * <p>データ代数元の要素は、データ代数基底と値のペアを、データ代数基底をキーとする
 * Mapで保持する。したがって、同一基底の値が複数存在することはない。
 * <p>データ代数元の要素の値は、基底のデータ型キーに対応するデータ型との整合性を
 * 厳密に保つ。基底のデータ型キーと Java のクラスは、次のように対応する。
 * <dl>
 * <dt>&quot;boolean&quot;</dt><dd>{@link java.lang.Boolean}</dd>
 * <dt>&quot;decimal&quot;</dt><dd>{@link java.math.BigDecimal}</dd>
 * <dt>&quot;string&quot;</dt><dd>{@link java.lang.String}</dd>
 * </dl>
 * データ代数元に値と基底のペアとなる要素を代入する場合、基底のデータ型と値の
 * データ型が一致しない場合、例外をスローする。データ代数元では、値として <tt>null</tt>値を
 * 使用することも可能であり、値が <tt>null</tt>値の場合はデータ型に依存しない。
 * <p>データ代数元の基本的なオペレーションは、書換(代入)演算となる。
 * この書換演算は、交換代数元 x と 交換代数元 y において、次のように定義される。
 * <blockquote>
 * x = a&lt;e1&gt;+b&lt;e2&gt;<br>
 * y = d&lt;e1&gt;+e&lt;e3&gt;<br>
 * z = x+y = d&lt;e1&gt;+b&lt;e2&gt;+e&lt;e3&gt;
 * </blockquote>
 * なお、交換代数のような加算演算などの算術的演算は定義されない。
 * <p>
 * このクラスの Map の実装は、挿入型の {@link java.util.LinkedHashMap <code>LinkedHashMap</code>} である。
 * したがって、{@link #put(DtBase, Object) <code>put</code>} メソッド、ファイル入出力において、
 * クラス内での基底の順序は基本的に維持される。詳細は、各メソッドの説明を参照のこと。
 * 
 * <p>このクラスでは、<tt>null</tt>も値として有効であり、要素として格納される。
 * <br>
 * ただし、次のメソッド実行結果には、<tt>null</tt>値を値とする要素は除外される。
 * <ul>
 * <li>{@link #normalization()}
 * </ul>
 * <p>
 * このクラスの {@link #iterator()} メソッドによって返される反復子は、「フェイルファスト」である。
 * 反復子の作成後に、マップが構造的に変更されると、反復子は <code>ConcurrentModificationException</code> をスローする。
 * したがって、同時変更が行われると、反復子は、将来の予測できない時点において予測できない動作が発生する危険を回避するため、
 * ただちにかつ手際よく例外をスローする。
 * <br>
 * 通常、非同期の同時変更がある場合、確かな保証を行うことは不可能なので、反復子のフェイルファストの動作を保証することはできない。
 * フェイルファスト反復子は最善努力原則に基づき、<code>ConcurrentModificationException</code> をスローする。
 * したがって、正確を期すためにこの例外に依存するプログラムを書くことは誤りである。
 * 「反復子のフェイルファストの動作はバグを検出するためにのみ使用すべきである」
 * <p>
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>Dtalge</code> は、CSV ファイル、XML ファイル形式の入出力インタフェースを
 * 提供する。CSV 形式、XML 形式共に、次の制約がある。
 * <ul>
 * <li>基底キーに、次の文字は使用できない。<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt; &gt; - , ^ &quot; % &amp; ? | @ ' " (空白)
 * <li>基底のデータ型キーに指定可能な文字列は、次の通り。大文字、小文字は区別しない。<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&quot;boolean&quot;&nbsp;&nbsp;(真偽値)<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&quot;decimal&quot;&nbsp;&nbsp;(実数値)<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&quot;string&quot;&nbsp;&nbsp;(文字列)
 * <li>真偽値として指定可能な値は、真の場合は &quot;true&quot;(大文字小文字は区別しない)、
 * 偽の場合はそれ以外の文字列となる。
 * 基本的に、{@link java.lang.Boolean#Boolean(String)} メソッドで変換可能な文字列であれば良い。
 * <li>実数値として指定可能な値は、整数値、小数値、ならびに指数表現値である。
 * 基本的に、{@link java.math.BigDecimal#BigDecimal(String)} メソッドで変換可能な文字列であれば良い。
 * <li>文字列は任意の文字列を指定できる。
 * <li>CSV 形式のファイル入出力において、フィールドの値が &quot;!%&quot; で始まる場合、特殊な値として
 * 解釈される。フィールド先頭の値が &quot;!%!%&quot; の場合は、&quot;!%&quot; 文字列として読み込まれ、
 * 以降の文字も通常の文字列値として読み込まれる。
 * <li>ファイル入力において、値に空文字(長さが 0 の文字列)が記述された場合、基本的に <tt>null</tt>値と解釈される。
 * ただし、テーブル形式の CSV 標準形では、値と基底が存在しないものとみなす。
 * <li>ファイル出力において、値が空文字(長さが 0 の文字列)もしくは <tt>null</tt>値の場合、基本的には空文字列として
 * 出力される。ただし、テーブル形式の CSV 標準形では、&quot;!%N&quot; と出力される。
 * <li>CSV 形式ファイルのテキスト・ファイル・エンコーディングは、入出力時に指定されない場合は、
 * プラットフォーム標準の文字コードとなる。
 * <li>XML 形式ファイルのテキスト・ファイル・エンコーディングは、&quot;UTF-8&quot; となる。
 * </ul>
 * また、CSV 形式、XML 形式のファイル入力時、ファイル内に同一の基底が複数存在する場合、
 * 最後に出現した基底の値が保持される。これは、データ代数元の書換演算による結果である。
 * <p>
 * <b>&lt;CSV 形式(標準形)&gt;</b>
 * <p>
 * CSV 形式(標準形)のファイルは、カンマ区切りのテキストファイルであり、次のようなフォーマットとなる。
 * <ul>
 * <li>CSV ファイルの第 1 行目は、次のキーワードであること。大文字、小文字も区別される。
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;#DtalgebraSet2
 * <li>空白文字も有効な文字と認識する。
 * <li>1 行をデータ代数元の 1 要素とし、行の先頭から次のような順序でカラムを識別する。
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;<i>値</i>, <i>名前キー</i>, <i>データ型キー</i>, <i>属性キー</i>, <i>主体キー</i>
 * <li>空白行(行の先頭で改行されている行)は、無視される。
 * <li>値が空文字(長さが 0 の文字列)の場合、<tt>null</tt>値みなす。
 * <li>値の先頭が &quot;!%&quot; の場合、特殊値の指定とみなす。ただし、先頭が &quot;!%!%&quot; の場合は特殊値の
 * エスケープとみなし &quot;!%&quot; という文字列として解釈され、以降の文字も文字列値と解釈される。
 * <li>値が &quot;!%N&quot; の場合、<tt>null</tt>値とみなす。大文字小文字は区別しない。
 * <li>データ代数元の要素の値が <tt>null</tt>値、もしくは空文字(長さ 0 の文字列)の場合、空文字(長さが 0 の文字列)として出力される。
 * <li>未定義の特殊値を記述した場合はエラーとなる。
 * <li>基底の属性キー、主体キーが空文字(長さが 0 の文字列)の場合、その基底キーは省略記号('#') に置き換えられる。
 * </ul>
 * CSV 形式(標準形)ファイルの読み込みにおいて、次の場合に {@link dtalge.exception.CsvFormatException} 例外がスローされる。
 * <ul>
 * <li>CSV ファイルの第 1 行目が #DtalgebraSet2 ではない場合
 * <li>基底の名前キーが省略された(空文字だった)場合
 * <li>基底のデータ型キーが省略された(空文字だった)場合
 * <li>基底の各基底キーに使用できない文字が含まれている場合
 * <li>基底のデータ型キーに、指定可能な文字列が記述されていない場合
 * <li>値が基底のデータ型キーに指定されたデータ型に変換不可能な場合
 * <li>値が未定義の特殊値の場合
 * </ul>
 * CSV 形式(標準形)ファイルの入出力は、次のメソッドにより行う。
 * <ul>
 * <li>{@link #fromCSV(File)}
 * <li>{@link #fromCSV(File, String)}
 * <li>{@link #toCSV(File)}
 * <li>{@link #toCSV(File, String)}
 * </ul>
 * なお、ファイル先頭のキーワードが #DtalgebraSet の場合、旧形式のフォーマットとして読み込む。
 * 旧形式のフォーマットと判断された場合、ファイル読み込みにおいて、新フォーマットとは次の点が異なる。
 * <ul>
 * <li>特殊記号 &quot;!%&quot; が記述されていても、通常の文字列として読み込まれる。
 * </ul>
 * <p>
 * <b>&lt;CSV 形式(テーブル形式)&gt;</b>
 * <p>
 * CSV 形式(テーブル形式)のファイルは、カンマ区切りのテキストファイルであり、次のようなフォーマットとなる。
 * <ul>
 * <li>CSV ファイルの第 1 行目は、次のキーワードであること。大文字、小文字も区別される。
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;#DtalgebraTable2
 * <li>空白文字も有効な文字と認識する。
 * <li>基本的に、1 列に 1 データ代数基底とその基底に対応する値を記述する。
 * <li>第 2 行目～第 5 行目は、データ代数集合に含まれる全てのデータ代数基底(DtBase)の内容を記述する。
 * <ul>
 * <li>第 2 行目には、基底の名前キーを記述する。省略は許可しない。
 * <li>第 3 行目には、基底のデータ型キーを記述する。省略は許可しない。
 * <li>第 4 行目には、基底の属性キーを記述する。省略された場合は、省略記号('#')に置き換えられる。
 * <li>第 5 行目には、基底の主体キーを記述する。省略された場合は、省略記号('#')に置き換えられる。
 * </ul>
 * <li>第 6 行目以降は、1 行が 1 つのデータ代数元となり、データ代数元の値を、基底に対応する列に記述する。
 * <li>データ代数元の要素の値が <tt>null</tt>値、もしくは空文字(長さ 0 の文字列)の場合、&quot;!%N&quot; と出力される。
 * <li>データ代数元の要素に存在しない基底の列は、空文字が出力される。
 * <li>入力において、省略(空文字)された列の値は、値と基底が存在しないものとみなす。
 * <li>値の先頭が &quot;!%&quot; の場合、特殊値の指定とみなす。ただし、先頭が &quot;!%!%&quot; の場合は特殊値の
 * エスケープとみなし &quot;!%&quot; という文字列として解釈され、以降の文字も文字列値と解釈される。
 * <li>値が &quot;!%N&quot; の場合、<tt>null</tt>値とみなす。大文字小文字は区別しない。
 * <li>未定義の特殊値を記述した場合はエラーとなる。
 * <li>複数の列に同じ基底が記述されているとき、より行の終端に近い列の値がデータ代数元の値となる。
 * <li>空白行(行の先頭で改行されている行)、もしくは全ての列が省略(空文字)されている場合は、要素が空のデータ代数元とする。
 * </ul>
 * CSV 形式(テーブル形式)ファイルの読み込みにおいて、次の場合に {@link dtalge.exception.CsvFormatException} 例外がスローされる。
 * <ul>
 * <li>CSV ファイルの第 1 行目が #DtalgebraTable2 ではない場合
 * <li>基底の名前キーが省略された(空文字だった)場合
 * <li>基底のデータ型キーが省略された(空文字だった)場合
 * <li>基底の各基底キーに使用できない文字が含まれている場合
 * <li>基底のデータ型キーに、指定可能な文字列が記述されていない場合
 * <li>基底の名前キーが記述された列の総数よりも、他の行の列数が多い場合
 * <li>値が基底のデータ型キーに指定されたデータ型に変換不可能な場合
 * <li>値が未定義の特殊値の場合
 * </ul>
 * CSV 形式(テーブル形式)ファイルの入出力は、次のメソッドにより行う。
 * <ul>
 * <li>{@link #fromCSV(File)}
 * <li>{@link #fromCSV(File, String)}
 * <li>{@link #toTableCSV(File)}
 * <li>{@link #toTableCSV(File, String)}
 * </ul>
 * <b>(注)</b>
 * <blockquote>
 * この CSV 形式(テーブル形式)では、空文字の列の値は存在しない値(値と基底が存在しないもの)となります。
 * </blockquote>
 * なお、ファイル先頭のキーワードが #DtalgebraTable の場合、旧形式のフォーマットとして読み込む。
 * 旧形式のフォーマットと判断された場合、ファイル読み込みにおいて、新フォーマットとは次の点が異なる。
 * <ul>
 * <li>特殊記号 &quot;!%&quot; が記述されていても、通常の文字列として読み込まれる。
 * <li>CSV フィールドの値が空欄(長さが 0 の文字列)の場合は、<tt>null</tt>値として読み込まれる。
 * <li>空白行、もしくは全ての列が省略(空文字)されている場合、全ての基底に対応する値が <tt>null</tt>値となるデータ代数元となる。
 * </ul>
 * <p>
 * <b>&lt;XML 形式&gt;</b>
 * <p>
 * XML 形式ファイルのノード構成は、次の通りである。
 * <pre><code>
 * &lt;DtalgebraSet&gt;
 *   &lt;Dtalgebra&gt;
 *     &lt;Dtelem&gt;
 *       &lt;Dtvalue&gt;<i>値</i>&lt;/Dtvalue&gt;
 *       &lt;Dtbase name=&quot;<i>名前キー</i>&quot; type=&quot;<i>データ型キー</i>&quot; attr=&quot;<i>属性キー</i>&quot; subject=&quot;<i>主体キー</i>&quot; /&gt;
 *     &lt;/Dtelem&gt;
 *         ・
 *         ・
 *         ・
 *   &lt;/Dtalgebra&gt;
 *         ・
 *         ・
 *         ・
 * &lt;/DtalgebraSet&gt;
 * </code></pre>
 * <ul>
 * <li>ルートノードは、単一の &lt;DtalgebraSet&gt; ノードとなる。
 * <li>&lt;DtalgebraSet&gt; の子ノードは &lt;Dtalgebra&gt; のみであり、0 個以上指定できる。
 * <li>&lt;Dtalgebra&gt; ノードが、データ代数の元を示すノードとなる。
 * <li>&lt;Dtalgebra&gt; の子ノードは &lt;Dtelem&gt; のみであり、0 個以上指定できる。
 * <li>&lt;Dtelem&gt; ノードが、データ代数元の要素を示すノードとなる。
 * <li>&lt;Dtelem&gt; の子ノードは &lt;Dtvalue&gt; と &lt;Dtbase&gt; のみであり、必ず 1 つずつ指定する。順序は問わない。
 * <li>&lt;Dtvalue&gt; ノードが、データ代数元要素の値を示すノードとなる。
 * <li>&lt;Dtbase&gt; ノードが、データ代数元要素の基底を示すノードとなる。
 * <li>&lt;Dtbase&gt; ノードの属性に基底のキーを指定する。名前キーとデータ型キー以外は省略可能。
 * <li>&lt;Dtvalue&gt; ノードの値が省略された(もしくは空文字の)場合、<tt>null</tt>値とみなす。
 * <li>基底の属性キー、主体キーが省略された(もしくは空文字の)場合、その基底キーは省略記号('#')に置き換えられる。
 * </ul>
 * XML 形式ファイルの読み込みにおいて、次の場合に {@link org.xml.sax.SAXParseException} 例外がスローされる。
 * <ul>
 * <li>XML のノードツリー構成が正しくない場合
 * <li>基底の名前キーが省略された(空文字だった)場合
 * <li>基底のデータ型キーが省略された(空文字だった)場合
 * <li>基底の各基底キーに使用できない文字が含まれている場合
 * <li>基底のデータ型キーに、指定可能な文字列が記述されていない場合
 * <li>値が基底のデータ型キーに指定されたデータ型に変換不可能な場合
 * </ul>
 * XML 形式ファイルの入出力は、次のメソッドにより行う。
 * <ul>
 * <li>{@link #fromXML(File)}
 * <li>{@link #toXML(File)}
 * </ul>
 * 
 * @version 0.40	2012/06/13
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public final class Dtalge implements IDataOutput, Iterable<Dtalge>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
    static final int DEFAULT_INITIAL_CAPACITY = 16;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 基底と値のペア
	 */
	protected Map<DtBase,Object> data;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 値を持たない <code>Dtalge</code> の新しいインスタンスを生成する。
	 */
	public Dtalge() {
		this.data = createDefaultMap();
	}

	/**
	 * 指定された初期容量および負荷係数で、値を持たない <code>Dtalge</code> の新しいインスタンスを生成する。
	 * 
	 * @param initialCapacity	データ代数元(マップ)の初期容量
	 * @param loadFactor		データ代数元(マップ)の負荷係数
	 * 
	 * @throws IllegalArgumentException	初期容量が 0 より小さいか、負荷係数が正の値ではない場合にスローされる
	 * 
	 * @see	java.util.LinkedHashMap#LinkedHashMap(int, float)
	 */
	protected Dtalge(int initialCapacity, float loadFactor) {
		this.data = new LinkedHashMap<DtBase,Object>(initialCapacity, loadFactor);
	}

	/**
	 * 指定された初期容量およびデフォルトの負荷係数で、値を持たない <code>Dtalge</code> の新しいインスタンスを生成する。
	 * 
	 * @param initialCapacity	データ代数元(マップ)の初期容量
	 * 
	 * @throws IllegalArgumentException	初期容量が 0 より小さい場合にスローされる
	 * 
	 * @see	java.util.LinkedHashMap#LinkedHashMap(int)
	 */
	protected Dtalge(int initialCapacity) {
		this.data = new LinkedHashMap<DtBase,Object>(initialCapacity);
	}

	/**
	 * 指定された基底の値を持つ <code>Dtalge</code> の新しいインスタンスを生成する。
	 * 
	 * @param base	データ代数の基底
	 * @param value	データ代数の値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws IllegalValueOfDataTypeException	指定された値が基底のデータ型と異なる場合
	 */
	public Dtalge(DtBase base, Object value) {
		Validations.validNotNull(base, "'base' argument cannot be null.");
		DtDataTypes.validDataType(base.getTypeKey(), value);
		this.data = new LinkedHashMap<DtBase,Object>(2);	// 最小のマップ
		this.data.put(base, value);
	}

	/**
	 * 指定された基底と値の配列から <code>Dtalge</code> の新しいインスタンスを生成する。
	 * 
	 * <p>このコンストラクタは、基底と値が順に配置される <code>Object</code> の配列から
	 * 基底と値のペアを取り出し、その基底と値のペアを持つ <code>Dtalge</code> の
	 * 新しいインスタンスを生成する。生成されたインスタンスに格納されるデータは、
	 * 基底が重複しないものについて、指定された配列の順序が維持される。
	 * <br>
	 * 同一の基底が複数含まれている場合、同一基底の値は、すべて上書きされる。
	 * このとき、配列の先頭からみて最初に出現した基底の位置の値が置き換えられる。
	 * 
	 * <p><code>srcArray</code> は、次の条件で生成されている必要がある。
	 * <ul>
	 * <li><code>DtBase</code> と <code>Object</code> のインスタンスのみが含まれている。
	 * <li>配列の開始インデックスを 0 とすると、偶数インデックスに <code>DtBase</code> インスタンス、
	 * 奇数インデックスに <code>Object</code> インスタンスが格納されており、基底と値の
	 * ペアにより、データ代数要素が構成されている。
	 * <ol type="1" start="0">
	 * <li><code>DtBase</code> インスタンス
	 * <li><code>Object</code> インスタンス
	 * <li><code>DtBase</code> インスタンス
	 * <li><code>Object</code> インスタンス
	 * <li><code>DtBase</code> インスタンス
	 * <li><code>Object</code> インスタンス
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;・
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;・
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;・
	 * </ol>
	 * <li>指定された配列の要素数は偶数個(基底と値のペアが複数個)である。
	 * </ul>
	 * 
	 * @param srcArray	基底と値の連続した配列
	 * 
	 * @throws NullPointerException	指定された配列が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次の場合に、この例外がスローされる。
	 * <ul>
	 * <li>基底の位置に <code>DtBase</code> 以外のインスタンスが含まれている。
	 * <li>基底のインスタンスが <tt>null</tt> である。
	 * </ul>
	 * @throws IllegalValueOfDataTypeException	指定された値が対応する基底のデータ型と異なる場合
	 */
	public Dtalge(Object[] srcArray) {
		Validations.validNotNull(srcArray, "'srcArray' argument cannot be null.");
		Validations.validArgument((srcArray.length%2)==0, "Value is nothing.");
		
		this.data = new LinkedHashMap<DtBase,Object>(srcArray.length/2);
		
		for (int i = 0; i < srcArray.length; i+=2) {
			// check class
			Validations.validArgument(srcArray[i] instanceof DtBase, "srcArray[%d] is not DtBase.class.", i);
			// set element
			DtBase dtbase = (DtBase)srcArray[i];
			Object value  = srcArray[i+1];
			putValue(dtbase, value);
		}
	}

	/**
	 * 指定されたマップの要素を格納する <code>Dtalge</code> の新しいインスタンスを生成する。
	 * 
	 * <p>このコンストラクタは、指定されたマップに格納された基底と値をすべて格納する。このとき、
	 * 指定されたマップから要素を取得した順序で格納される。
	 * 
	 * @param srcMap	このデータ代数元に配置される要素を持つマップ
	 * 
	 * @throws NullPointerException	指定されたマップが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定されたマップのキーに <tt>null</tt> が含まれている場合
	 * @throws IllegalValueOfDataTypeException	指定された値が対応する基底のデータ型と異なる場合
	 */
	public Dtalge(Map<? extends DtBase, ? extends Object> srcMap) {
		Validations.validNotNull(srcMap, "'srcMap' argument cannot be null.");
		
		this.data = new LinkedHashMap<DtBase,Object>(srcMap.size());
		putAllValues(srcMap);
	}

	/**
	 * 指定されたデータ代数基底の要素を全て保持する <code>Dtalge</code> の
	 * 新しいインスタンスを生成する。
	 * このコンストラクタは、内部編集用のため、からなず可変長のマップ領域を
	 * 作成する。
	 * 
	 * @param srcAlge	コピー元のデータ代数元
	 * 
	 * @throws NullPointerException	指定されたデータ代数元が <tt>null</tt> の場合
	 */
	@SuppressWarnings("unchecked")
	private Dtalge(Dtalge srcAlge) {
		Validations.validNotNull(srcAlge, "'srcAlge' argument cannot be null.");
		if (srcAlge.data instanceof LinkedHashMap) {
			this.data = (LinkedHashMap<DtBase,Object>)((LinkedHashMap<DtBase,Object>)srcAlge.data).clone();
		} else {
			this.data = new LinkedHashMap<DtBase,Object>(srcAlge.data);
		}
	}
	
	/**
	 * コレクションに含まれるデータ代数元の全要素を全て保持する <code>Dtalge</code> の
	 * 新しいインスタンスを生成する。
	 * <br>
	 * このコンストラクタは、データ代数元のすべての要素を、1 つのデータ代数元に代入し、
	 * その纏められたデータ代数元のインスタンスを返すものとなる。
	 * <br>
	 * コレクションに含まれる <tt>null</tt> 要素は無視される。
	 * 
	 * @param c	データ代数元のコレクション
	 * @return	すべての元の要素を集約したデータ代数元を返す。
	 * 
	 * @throws NullPointerException 指定されたコレクションが <tt>null</tt> の場合
	 */
	protected Dtalge(Collection<? extends Dtalge> c) {
		Map<DtBase,Object> newMap = createDefaultMap();
		for (Dtalge alge : c) {
			if (alge != null && !alge.isEmpty()) {
				newMap.putAll(alge.data);
			}
		}
		
		this.data = newMap;
	}
	
	protected Map<DtBase,Object> createDefaultMap() {
		return new LinkedHashMap<DtBase,Object>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された 2 つの値が等しいかを判定する。
	 * 指定された値がどちらも <code>BigDecimal</code> オブジェクトの場合、
	 * {@link java.math.BigDecimal#compareTo(BigDecimal)} メソッドが 0 を
	 * 返したときに等しいとみなす。
	 * @param value1	比較する値の一方
	 * @param value2	比較する値のもう一方
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 0.30
	 */
	static public boolean valueEquals(Object value1, Object value2) {
		if (value1 == value2) {
			// インスタンスが同じ、もしくは、どちらも null の場合は、等しい
			return true;
		}
		
		if (value1 == null || value2 == null) {
			// どちらかが null の場合は、等しくない
			return false;
		}
		
		if ((value1 instanceof BigDecimal) && (value2 instanceof BigDecimal)) {
			return (0==((BigDecimal)value1).compareTo((BigDecimal)value2));
		} else {
			return value1.equals(value2);
		}
	}
	
	/**
	 * 指定された 2 つの値が等しいかを判定する。
	 * このメソッドは、{@link java.lang.Object#equals(Object)} メソッドの
	 * 結果を返す。
	 * @param value1	比較する値の一方
	 * @param value2	比較する値のもう一方
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 0.30
	 */
	static public boolean objectEquals(Object value1, Object value2) {
		if (value1 == value2) {
			// インスタンスが同じ、もしくは、どちらも null の場合は、等しい
			return true;
		}
		
		if (value1 == null || value2 == null) {
			// どちらかが null の場合は、等しくない
			return false;
		}
		
		return value1.equals(value2);
	}

	/**
	 * データ代数元の要素が空であることを示す。
	 * 
	 * @return 要素が一つも存在しない場合に <tt>true</tt> を返す。
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * データ代数元の要素数を返す。
	 * 
	 * @return	データ代数元の要素数
	 */
	public int getNumElements() {
		return data.size();
	}
	
	/**
	 * 指定された基底が、このインスタンスの要素に含まれているかを示す。
	 * 
	 * @param base	データ代数基底
	 * @return	指定の基底が要素に含まれている場合に <tt>true</tt> を返す。
	 */
	public boolean containsBase(DtBase base) {
		return data.containsKey(base);
	}

	/**
	 * 指定された基底のどれか 1 つが、このインスタンスの要素に含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるどれか 1 つが、この
	 * データ代数元の要素に存在した場合に <tt>true</tt> を返す。
	 * 指定された基底集合が <tt>null</tt> もしくは要素が空の場合は、<tt>false</tt> を返す。
	 * 
	 * @param bases	検証するデータ代数基底の集合
	 * @return	指定した基底集合のどれか 1 つが含まれている場合に <tt>true</tt> を返す。
	 */
	public boolean containsAnyBases(DtBaseSet bases) {
		if (bases != null && !bases.isEmpty() && !this.isEmpty()) {
			if (this.data.size() < bases.size()) {
				for (DtBase base : this.data.keySet()) {
					if (bases.contains(base)) {
						return true;
					}
				}
			}
			else {
				for (DtBase base : bases) {
					if (this.data.containsKey(base)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	/**
	 * 指定されたすべての基底が、このインスタンスの要素に含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるすべての基底が、この
	 * データ代数元の要素に存在した場合のみ <tt>true</tt> を返す。
	 * 指定された基底集合が <tt>null</tt> もしくは要素が空の場合は、<tt>false</tt> を返す。
	 * 
	 * @param bases	検証するデータ代数基底の集合
	 * @return	すべての基底が含まれている場合に <tt>true</tt> を返す。
	 */
	public boolean containsAllBases(DtBaseSet bases) {
		if (bases != null && !bases.isEmpty() && !this.isEmpty())
			return this.data.keySet().containsAll(bases);
		else
			return false;
	}

	/**
	 * 指定された値が、このインスタンスの要素に含まれているかを示す。
	 * 
	 * @param value	データ代数の値
	 * @return	指定の値が含まれている場合に <tt>true</tt> を返す。
	 */
	public boolean containsValue(Object value) {
		if (data.isEmpty()) {
			return false;
		}
		
		if (value instanceof BigDecimal) {
			for (Object algeValue : data.values()) {
				if ((algeValue instanceof BigDecimal)
					&& 0==((BigDecimal)value).compareTo((BigDecimal)algeValue))
				{
					return true;
				}
			}
			return false;
		} else {
			return data.containsValue(value);
		}
	}

	/**
	 * 指定されたコレクションに含まれる値のどれか 1 つが、
	 * このインスタンスの要素に含まれている場合に <tt>true</tt> を返す。
	 * <em>values</em> が <tt>null</tt> もしくは空の場合は、<tt>false</tt> を返す。
	 * @param values	検証する値のコレクション
	 * @return	指定されたコレクションに含まれる値のどれか 1 つが、このインスタンスの要素に含まれている場合は <tt>true</tt>
	 * 
	 * @since 0.20
	 */
	public boolean containsAnyValues(Collection<?> values) {
		if (values != null && !values.isEmpty() && !data.isEmpty()) {
			for (Object algeValue : data.values()) {
				if (algeValue instanceof BigDecimal) {
					for (Object val : values) {
						if ((val instanceof BigDecimal)
							&& 0==((BigDecimal)val).compareTo((BigDecimal)algeValue))
						{
							return true;
						}
					}
				} else {
					if (values.contains(algeValue)) {
						return true;
					}
				}
			}
		}
		return false;
		
	}

	/**
	 * 指定されたコレクションに含まれる全ての値が、このインスタンスの要素に
	 * 含まれている場合に <tt>true</tt> を返す。
	 * <em>values</em> が <tt>null</tt> もしくは空の場合は、<tt>false</tt> を返す。
	 * @param values	検証する値のコレクション
	 * @return	指定されたコレクションに含まれる全ての値が、このインスタンスの要素に含まれている場合は <tt>true</tt>
	 * 
	 * @since 0.20
	 */
	public boolean containsAllValues(Collection<?> values) {
		if (values == null || values.isEmpty() || data.isEmpty()) {
			return false;
		}
		
		for (Object value : values) {
			if (value instanceof BigDecimal) {
				BigDecimal dv = (BigDecimal)value;
				boolean exist = false;
				for (Object algeValue : data.values()) {
					if ((algeValue instanceof BigDecimal)
						&& 0==dv.compareTo((BigDecimal)algeValue))
					{
						exist = true;
						break;
					}
				}
				if (!exist) {
					return false;
				}
			} else if (!data.values().contains(value)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * このインスタンスの要素に <tt>null</tt> 値が含まれている場合に <tt>true</tt> を返す。
	 * @return	このインスタンスの要素に <tt>null</tt> 値が含まれている場合は <tt>true</tt>
	 * 
	 * @since 0.20
	 */
	public boolean containsNull() {
		return data.containsValue(null);
	}

	/**
	 * データ代数元に含まれる全ての基底を取り出す。
	 * 
	 * @return	データ代数元から取り出した基底集合
	 */
	public DtBaseSet getBases() {
		DtBaseSet newSet = new DtBaseSet(data.keySet());
		return newSet;
	}

	/**
	 * このデータ代数元に含まれる基底のうち、指定されたパターンに一致する
	 * 基底のみを格納する基底集合を返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは空の基底集合を返す。
	 * 
	 * @param pattern	基底パターン
	 * @return	パターンに一致した基底のみの集合
	 * 
	 * @throws NullPointerException	指定された基底パターンが <tt>null</tt> の場合
	 */
	public DtBaseSet getMatchedBases(DtBasePattern pattern) {
		Validations.validNotNull(pattern);
		
		// この元の要素が空なら、空集合を返す
		if (this.isEmpty()) {
			return new DtBaseSet(0);
		}
		
		// マッチする基底のみの集合を生成
		DtBaseSet newSet = new DtBaseSet(data.size());
		for (DtBase abase : data.keySet()) {
			if (pattern.matches(abase)) {
				newSet.fastAdd(abase);
			}
		}
		return newSet;
	}

	/**
	 * このデータ代数元に含まれる基底のうち、指定されたパターンに一致する
	 * 基底のみを格納する基底集合を返す。
	 * <br>
	 * このメソッドが返す基底集合には、指定された基底パターン集合に含まれる
	 * 基底パターンのどれかに一致した基底が含まれる。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、もしくは、データ代数元が
	 * 要素を持たない場合、このメソッドは空の基底集合を返す。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return	パターンに一致した基底のみの集合
	 * 
	 * @throws NullPointerException	指定された基底パターン集合が <tt>null</tt> の場合
	 */
	public DtBaseSet getMatchedBases(DtBasePatternSet patterns) {
		Validations.validNotNull(patterns);
		
		// この元の要素、もしくはパターン集合が空なら、空集合を返す
		if (this.isEmpty() || patterns.isEmpty()) {
			return new DtBaseSet(0);
		}
		
		// マッチする基底のみの集合を生成
		DtBaseSet newSet = new DtBaseSet(data.size());
		for (DtBase abase : data.keySet()) {
			if (patterns.matches(abase)) {
				newSet.fastAdd(abase);
			}
		}
		return newSet;
	}

	/**
	 * このデータ代数元に含まれる基底が指定されたパターンと一致するかを評価し、
	 * 指定された基底集合の内容を更新する。
	 * 基底パターンと一致した基底は <code>matchedBases</code> に、
	 * 一致しない基底は <code>unmatchedBases</code> に追加される。
	 * <p><b>注:</b>引数に指定されたパラメータが全て <tt>null</tt> では
	 * ないことを前提としている。
	 * このメソッドは、パラメータを評価しないため、メソッドの呼び出しには注意すること。
	 * 
	 * @param matchedBases		パターンと一致する基底を格納する基底集合への参照
	 * @param unmatchedBases	パターンと一致しない基底を格納する基底集合への参照
	 * @param pattern	評価するパターン
	 */
	protected void updateMatchedBases(DtBaseSet matchedBases, DtBaseSet unmatchedBases,
										DtBasePattern pattern)
	{
		for (DtBase abase : data.keySet()) {
			if (!matchedBases.contains(abase) && !unmatchedBases.contains(abase)) {
				if (pattern.matches(abase))
					matchedBases.fastAdd(abase);
				else
					unmatchedBases.fastAdd(abase);
			}
		}
	}
	
	/**
	 * このデータ代数元に含まれる基底が指定されたパターンと一致するかを評価し、
	 * 指定された基底集合の内容を更新する。
	 * 基底パターンと一致した基底は <code>matchedBases</code> に、
	 * 一致しない基底は <code>unmatchedBases</code> に追加される。
	 * <p><b>注:</b>引数に指定されたパラメータが全て <tt>null</tt> ではない
	 * ことを前提としている。
	 * このメソッドは、パラメータを評価しないため、メソッドの呼び出しには注意すること。
	 * 
	 * @param matchedBases		パターンと一致する基底を格納する基底集合への参照
	 * @param unmatchedBases	パターンと一致しない基底を格納する基底集合への参照
	 * @param patterns	評価するパターンの集合
	 */
	protected void updateMatchedBases(DtBaseSet matchedBases, DtBaseSet unmatchedBases,
										DtBasePatternSet patterns)
	{
		for (DtBase abase : data.keySet()) {
			if (!matchedBases.contains(abase) && !unmatchedBases.contains(abase)) {
				if (patterns.matches(abase))
					matchedBases.fastAdd(abase);
				else
					unmatchedBases.fastAdd(abase);
			}
		}
	}

	/**
	 * このデータ代数元の先頭の要素のみを取得する。
	 * 要素のコレクションの先頭は、このクラスのコレクションの実装に依存するため、
	 * 要素が 1 つしかないデータ代数元から要素を取得する場合に有効である。
	 * 要素が存在しない場合、このメソッドは例外をスローする。
	 * 
	 * @return データ代数元の先頭の要素を返す。
	 * 
	 * @throws NoSuchElementException	このデータ代数元に要素が存在しない場合
	 */
	protected Map.Entry<DtBase,Object> getElement() {
		return data.entrySet().iterator().next();
	}

	/**
	 * このデータ代数元の先頭に位置する要素の基底を取得する。
	 * 要素のコレクションの先頭は、このクラスのコレクションの実装に依存するため、
	 * 要素が 1 つしかないデータ代数元から要素を取得する場合に有効である。
	 * 
	 * @return	データ代数元の先頭に位置する要素の基底を返す。
	 * 
	 * @throws NoSuchElementException	このデータ代数元に要素が存在しない場合
	 */
	public DtBase getOneBase() {
		return getElement().getKey();
	}
	
	/**
	 * このデータ代数元の先頭に位置する要素の値を取得する。
	 * 要素のコレクションの先頭は、このクラスのコレクションの実装に依存するため、
	 * 要素が 1 つしかないデータ代数元から要素を取得する場合に有効である。
	 * 
	 * @return	データ代数元の先頭に位置する要素の値を返す。
	 * 
	 * @throws NoSuchElementException	このデータ代数元に要素が存在しない場合
	 */
	public Object getOneValue() {
		return getElement().getValue();
	}

	/**
	 * 指定された基底に対応する値を取り出す。
	 * 要素に含まれていない基底を指定した場合、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException 指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException 指定された基底が要素に含まれていない場合
	 */
	public Object get(DtBase base) {
		Validations.validNotNull(base, "'base' argument cannot be null.");
		if (!data.containsKey(base))
			throw new DtBaseNotFoundException(base.toString() + " not exist.");
		return data.get(base);
	}
	
	/**
	 * 指定された基底に対応する値を、指定のデータ型として取り出す。
	 * 指定された基底のデータ型キーと指定のデータ型が異なる場合や、
	 * 要素に含まれていない基底を指定した場合は、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException	指定された基底が要素に含まれていない場合
	 * @throws IllegalValueOfDataTypeException	指定された基底のデータ型キーと指定のデータ型が異なる場合
	 * 
	 * @since 0.20
	 */
	protected Object getByType(DtBase base, String typeName, Class<?> reqType) {
		Object val = get(base);
		if (!typeName.equals(base.getTypeKey())) {
			// illegal data type
			String msg = String.format("DtBase(%s) Type key is not %s.",
										base.toString(), typeName);
			throw new IllegalValueOfDataTypeException(typeName, reqType, val, msg);
		}
		return val;
	}

	/**
	 * 指定された基底に対応する値を、真偽値として取り出す。
	 * 指定された基底のデータ型キーが真偽値型ではない場合や、
	 * 要素に含まれていない基底を指定した場合は、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException	指定された基底が要素に含まれていない場合
	 * @throws IllegalValueOfDataTypeException	指定された基底のデータ型キーが真偽値型ではない場合
	 * 
	 * @since 0.20
	 */
	public Boolean getBoolean(DtBase base) {
		return ((Boolean)getByType(base, DtDataTypes.BOOLEAN, Boolean.class));
	}

	/**
	 * 指定された基底に対応する値を、文字列型として取り出す。
	 * 指定された基底のデータ型キーが文字列型ではない場合や、
	 * 要素に含まれていない基底を指定した場合は、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException	指定された基底が要素に含まれていない場合
	 * @throws IllegalValueOfDataTypeException	指定された基底のデータ型キーが文字列型ではない場合
	 * 
	 * @since 0.20
	 */
	public String getString(DtBase base) {
		return ((String)getByType(base, DtDataTypes.STRING, String.class));
	}
	
	/**
	 * 指定された基底に対応する値を、実数値として取り出す。
	 * 指定された基底のデータ型キーが実数値型ではない場合や、
	 * 要素に含まれていない基底を指定した場合は、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException	指定された基底が要素に含まれていない場合
	 * @throws IllegalValueOfDataTypeException	指定された基底のデータ型キーが実数値型ではない場合
	 * 
	 * @since 0.20
	 */
	public BigDecimal getDecimal(DtBase base) {
		return ((BigDecimal)getByType(base, DtDataTypes.DECIMAL, BigDecimal.class));
	}

	/**
	 * 指定された基底と値が代入された、<code>Dtalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定された値で上書きされる。この場合、
	 * 基底の順序は影響を受けない。
	 * 
	 * @param base	データ代数の基底
	 * @param value	データ代数の値
	 * 
	 * @return	代入後のデータ代数元の新しいインスタンス
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws IllegalValueOfDataTypeException	指定された値が基底のデータ型と異なる場合
	 */
	public Dtalge put(DtBase base, Object value) {
		Dtalge newAlge = new Dtalge(this);
		newAlge.putValue(base, value);
		return newAlge;
	}

	/**
	 * 指定されたデータ代数元のすべての要素が代入された、<code>Dtalge</code> の新しい
	 * インスタンスを返す。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定されたデータ代数元の値で上書きされる。この場合、
	 * 基底の順序は影響を受けない。
	 * 
	 * @param alge	代入するデータ代数元
	 * @return	代入後のデータ代数元の新しいインスタンス
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 */
	public Dtalge put(Dtalge alge) {
		Validations.validNotNull(alge, "'alge' argument cannot be null.");
		Dtalge newAlge = new Dtalge(this);
		newAlge.putValue(alge);
		return newAlge;
	}

	/**
	 * データ代数元の要素の値が <tt>null</tt> のものを除外した、<code>Dtalge</code> の
	 * 新しいインスタンスを返す。
	 * 
	 * @return	値が <tt>null</tt> の要素を除外した <code>Dtalge</code> インスタンス
	 */
	public Dtalge normalization() {
		Dtalge newAlge = new Dtalge(this.data.size());
		for (Map.Entry<DtBase,Object> entry : data.entrySet()) {
			DtBase dtbase = entry.getKey();
			Object value  = entry.getValue();
			if (value != null) {
				newAlge.data.put(dtbase, value);
			}
		}
		return newAlge;
	}

	/**
	 * コレクションに含まれるデータ代数元を結合する。
	 * <br>
	 * このメソッドは、コレクションに含まれる全てのデータ代数元を一つのデータ代数元に
	 * 結合するメソッドであり、同じ基底の値は、コレクションに格納されている順序で
	 * 上書きされる。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 結合する値の基底が存在しない場合、データ代数の値と基底のマップの終端に、
	 * 指定されたデータ代数コレクション(c)に格納されている順序で追加される。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * この結合は、同じ基底の値は上書きされる。数値的な加算等の演算は行わない。
	 * </blockquote>
	 * 
	 * @param c 結合するデータ代数の元のコレクション
	 * 
	 * @return 計算結果となる <code>Dtalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.30
	 */
	static public Dtalge sum(Collection<? extends Dtalge> c) {
		Dtalge newAlge = new Dtalge();
		Iterator<? extends Dtalge> it = c.iterator();
		while (it.hasNext()) {
			Dtalge alge = it.next();
			if (alge != null) {
				newAlge.putValue(alge);
			}
		}
		return newAlge;
	}

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * このインスタンスのハッシュ値を返す。
	 * 
	 * @return	ハッシュ値
	 */
	public int hashCode() {
		//return data.hashCode();
		int h = 0;
		for (Map.Entry<DtBase, Object> entry : data.entrySet()) {
			//--- キー(DtBase)のハッシュ値は、DtBase#hashCode() の値をそのまま利用する。
			int hk = (entry.getKey() == null ? 0 : entry.getKey().hashCode());
			//--- 値が BigDecimal の場合、BigDecimal#stripTrailingZeros() によって下位の桁の
			//--- 0(余分な0)を消去した後の BigDecimal#hashCode() の値とする。値が 0 の場合は
			//--- 強制的に 0 とする。
			//--- 値が BigDecimal 以外の場合は、Object#hashCode() の値をそのまま利用する。
			int hv = 0;
			Object val = entry.getValue();
			if (val instanceof BigDecimal) {
				BigDecimal dv = (BigDecimal)val;
				if (0!=BigDecimal.ZERO.compareTo(dv)) {
					hv = dv.stripTrailingZeros().hashCode();
				}
			} else if (val != null) {
				hv = val.hashCode();
			}
			h += (hk ^ hv);
		}
		return h;
	}

	/**
	 * 2 つのデータ代数元が同値であるかを検証する。
	 * <p>このメソッドはインスタンスの同一性ではなく、同値性を評価する。
	 * <br>
	 * 2 つのデータ代数元の関係が次のような場合に同値とみなす。
	 * <ul>
	 * <li>同一の基底が存在する。
	 * <li>同一(等しい)基底に対応する値が全て同値である。
	 * </ul>
	 * なお、基底に対応する値の同値性は、{@link Object#equals(Object)} メソッドの
	 * 実行結果によるものとする。
	 * ただし、値が実数値の場合は {@link java.math.BigDecimal#compareTo(BigDecimal)} の
	 * 結果が 0 となる場合を同値とする。
	 * 
	 * @param obj	同値性を判定するオブジェクトの一方
	 * 
	 * @return 同値である場合に <tt>true</tt> を返す。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			// same instance
			return true;
		}
		
		if (!(obj instanceof Dtalge)) {
			// not same class
			return false;
		}
		
		//return ((Dtalge)obj).data.equals(this.data);
		Dtalge alge = (Dtalge)obj;
		
		// 要素が空か判定
		if (data.isEmpty()) {
			if (alge.isEmpty()) {
				// 要素がどちらも空の場合は、同値とみなす
				return true;
			} else {
				// this.isEmpty() != alge.isEmpty()
				return false;
			}
		} else if (alge.isEmpty()) {
			// this.isEmpty() != alge.isEmpty()
			return false;
		}
		
		// 基底キーの比較
		Set<DtBase> thisBases = data.keySet();
		if (!thisBases.equals(alge.data.keySet())) {
			// 基底キーが異なるため、同値ではない
			return false;
		}
		
		// 全ての値を比較
		for (DtBase base : thisBases) {
			Object thisValue = data.get(base);
			Object algeValue = alge.data.get(base);
			if (thisValue == null) {
				if (algeValue != null) {
					// 一方が null
					return false;
				}
			}
			else if (algeValue == null) {
				// 一方が null
				return false;
			}
			else if (thisValue instanceof BigDecimal) {
				if (!(algeValue instanceof BigDecimal)) {
					// 値が同じデータ型ではない
					return false;
				}
				else if (0!=((BigDecimal)thisValue).compareTo((BigDecimal)algeValue)) {
					// 等しい実数値ではない
					return false;
				}
			}
			else if (!thisValue.equals(algeValue)) {
				// 異なる値
				return false;
			}
		}
		
		// 等しい
		return true;
	}

	/**
	 * このインスタンスの文字列表現を返す。
	 * <p>
	 * 出力形式は、次の通り。
	 * <blockquote>
	 * <i>値</i> <i>基底</i> '+' <i>値</i> <i>基底</i> '+' ...
	 * </blockquote>
	 * なお、要素が一つも存在しない場合、次のように出力される。
	 * <blockquote>
	 * ()
	 * </blockquote>
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (isEmpty()) {
			// empty
			sb.append("()");
		} else {
			// has elements
			Iterator<Map.Entry<DtBase, Object>> it = data.entrySet().iterator();
			Map.Entry<DtBase, Object> entry = it.next();
			//sb.append(entry.getValue());
			Object value = entry.getValue();
			if (value instanceof BigDecimal) {
				sb.append(((BigDecimal)value).stripTrailingZeros().toPlainString());
			} else {
				sb.append(value);
			}
			sb.append(entry.getKey());
			while (it.hasNext()) {
				entry = it.next();
				sb.append("+");
				//sb.append(entry.getValue());
				value = entry.getValue();
				if (value instanceof BigDecimal) {
					sb.append(((BigDecimal)value).stripTrailingZeros().toPlainString());
				} else {
					sb.append(value);
				}
				sb.append(entry.getKey());
			}
		}
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Implement Iterable interfaces
	//------------------------------------------------------------

	/**
	 * <code>Dtalge</code> の要素の反復子を返す。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * 
	 * @return	データ代数要素の <code>Iterator</code>
	 * 
	 * @see ConcurrentModificationException
	 */
	public Iterator<Dtalge> iterator() {
		return newDtalgeIterator();
	}

	/**
	 * データ代数元の要素イテレーターを生成する。
	 * 
	 * @return <code>Dtalge</code> の <code>Iterator</code>
	 */
	protected Iterator<Dtalge> newDtalgeIterator() {
		return new DtalgeIterator(this.data);
	}

	//------------------------------------------------------------
	// Operations
	//------------------------------------------------------------

	/**
	 * 指定された値と等しい要素のみを取り出す。<br>
	 * 指定の値と等しい要素が存在しない場合は、
	 * 要素を持たない <code>Dtalge</code> の新しいインスタンスを返す。
	 * @param value	取り出す要素の値
	 * @return	指定された値と等しい要素のみを含む <code>Dtalge</code> の新しいインスタンス
	 * 
	 * @since 0.20
	 */
	public Dtalge oneValueProjection(Object value) {
		Dtalge newAlge = new Dtalge();
		if (!data.isEmpty()) {
			if (value == null) {
				for (Map.Entry<DtBase, Object> entry : data.entrySet()) {
					if (entry.getValue() == null) {
						newAlge.putValue(entry.getKey(), entry.getValue());
					}
				}
			}
			else if (value instanceof BigDecimal) {
				BigDecimal d = (BigDecimal)value;
				for (Map.Entry<DtBase, Object> entry : data.entrySet()) {
					if ((entry.getValue() instanceof BigDecimal)
						&& 0==d.compareTo((BigDecimal)entry.getValue()))
					{
						newAlge.putValue(entry.getKey(), entry.getValue());
					}
				}
			}
			else {
				for (Map.Entry<DtBase, Object> entry : data.entrySet()) {
					if (value.equals(entry.getValue())) {
						newAlge.putValue(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		return newAlge;
	}

	/**
	 * 指定されたコレクションに含まれる値と等しい要素のみを取り出す。<br>
	 * 指定の値と等しい要素が存在しない場合は、
	 * 要素を持たない <code>Dtalge</code> の新しいインスタンスを返す。
	 * @param values	取り出す要素の値のコレクション
	 * @return	指定されたコレクションに含まれる値と等しい要素のみを含む
	 * 			<code>Dtalge</code> の新しいインスタンス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 0.20
	 */
	public Dtalge valuesProjection(Collection<?> values) {
		Dtalge newAlge = new Dtalge();
		if (!values.isEmpty() && !data.isEmpty()) {
			for (Map.Entry<DtBase, Object> entry : data.entrySet()) {
				Object algeValue = entry.getValue();
				if (algeValue instanceof BigDecimal) {
					for (Object val : values) {
						if ((val instanceof BigDecimal)
							&& 0==((BigDecimal)algeValue).compareTo((BigDecimal)val))
						{
							newAlge.putValue(entry.getKey(), algeValue);
							break;
						}
					}
				}
				else {
					if (values.contains(algeValue)) {
						newAlge.putValue(entry.getKey(), algeValue);
					}
				}
			}
		}
		return newAlge;
	}

	/**
	 * 値が <tt>null</tt> の要素のみを取り出す。<br>
	 * 値が <tt>null</tt> の要素が存在しない場合は、
	 * 要素を持たない <code>Dtalge</code> の新しいインスタンスを返す。
	 * @return	値が <tt>null</tt> の要素のみを含む <code>Dtalge</code> の新しいインスタンス
	 * 
	 * @since 0.20
	 */
	public Dtalge nullProjection() {
		Dtalge newAlge = new Dtalge();
		if (!data.isEmpty()) {
			for (Map.Entry<DtBase, Object> entry : data.entrySet()) {
				if (entry.getValue() == null) {
					newAlge.putValue(entry.getKey(), entry.getValue());
				}
			}
		}
		return newAlge;
	}

	/**
	 * 値が <tt>null</tt> ではない要素のみを取り出す。<br>
	 * 値が <tt>null</tt> ではない要素が存在しない場合は、
	 * 要素を持たない <code>Dtalge</code> の新しいインスタンスを返す。
	 * @return	値が <tt>null</tt> ではない要素のみを含む <code>Dtalge</code> の新しいインスタンス
	 * 
	 * @since 0.20
	 */
	public Dtalge nonullProjection() {
		Dtalge newAlge = new Dtalge();
		if (!data.isEmpty()) {
			for (Map.Entry<DtBase, Object> entry : data.entrySet()) {
				if (entry.getValue() != null) {
					newAlge.putValue(entry.getKey(), entry.getValue());
				}
			}
		}
		return newAlge;
	}

	/**
	 * この交換代数元から、指定された基底と一致する要素のみを取り出し、
	 * その要素のみを持つ <code>Dtalge</code> の新しいインスタンスを返す。
	 * <br>
	 * 指定された基底が存在しない場合、要素を持たない <code>Dtalge</code> の新しい
	 * インスタンスを返す。
	 * <p>
	 * このメソッドは、次のデータ代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[base](this)
	 * </blockquote>
	 * 
	 * @param base	取り出すデータ代数基底
	 * 
	 * @return 取り出された要素のみを含む <code>Dtalge</code> インスタンス
	 * 
	 * @throws NullPointerException 指定された基底が <tt>null</tt> の場合
	 */
	public Dtalge projection(DtBase base) {
		DtBase cachedBase = CacheManager.cacheDtBase(Validations.validNotNull(base));
		Dtalge newAlge = null;
		if (data.containsKey(cachedBase)) {
			newAlge = new Dtalge(cachedBase, data.get(cachedBase));
		} else {
			newAlge = new Dtalge();	// empty
		}
		return newAlge;
	}

	/**
	 * この交換代数元から、指定された基底集合に含まれる基底と一致する要素のみを
	 * 取り出し、その要素のみを持つ <code>Dtalge</code> の新しいインスタンスを返す。
	 * <br>
	 * 指定された基底が存在しない場合、要素を持たない <code>Dtalge</code> の
	 * 新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次のデータ代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[bases](this)
	 * </blockquote>
	 * 
	 * @param bases	取り出す基底が含まれるデータ代数基底集合
	 * 
	 * @return 取り出された要素のみを含む <code>Dtalge</code> インスタンス
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 */
	public Dtalge projection(DtBaseSet bases) {
		Validations.validNotNull(bases);
		// 基底がない場合は、空のインスタンスを返す
		if (bases.isEmpty()) {
			return new Dtalge();	// empty
		}
		
		// projection
		Dtalge newAlge;
		if (data.size() < bases.size()) {
			newAlge = new Dtalge(data.size());
			for (Map.Entry<DtBase, Object> entry : data.entrySet()) {
				DtBase base = entry.getKey();
				if (bases.contains(base)) {
					newAlge.data.put(base, entry.getValue());
				}
			}
		} else {
			newAlge = new Dtalge(bases.size());
			for (DtBase base : bases) {
				if (data.containsKey(base)) {
					newAlge.data.put(base, data.get(base));
				}
			}
		}
		
		return newAlge;
	}

	/**
	 * 指定の基底パターンに一致する基底の要素のみを取り出し、
	 * その要素のみを持つ <code>Dtalge</code> の新しいインスタンスを返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空のデータ代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている
	 * 基底の順序が極力維持される。
	 * 
	 * @param pattern	基底パターン
	 * @return	パターンに一致した要素のみを含む <code>Dtalge</code> インスタンス
	 * 
	 * @throws NullPointerException 指定された基底パターンが <tt>null</tt> の場合
	 */
	public Dtalge patternProjection(DtBasePattern pattern) {
		Validations.validNotNull(pattern);
		
		Map<DtBase,Object> newMap = null;
		if (!this.isEmpty()) {
			newMap = createDefaultMap();
			for (Map.Entry<DtBase,Object> entry : data.entrySet()) {
				DtBase base = entry.getKey();
				if (pattern.matches(base)) {
					newMap.put(base, entry.getValue());
				}
			}
		}
		
		Dtalge newAlge = new Dtalge();
		if (newMap != null && !newMap.isEmpty()) {
			// 空のマップを生成したマップに置き換え
			newAlge.data = newMap;
		}
		return newAlge;
	}

	/**
	 * 指定の集合の基底パターンに一致する基底の要素のみ取り出し、
	 * その要素のみを持つ <code>Dtalge</code> の新しいインスタンスを返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空のデータ代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている
	 * 基底の順序が極力維持される。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return	パターンに一致した要素のみを含む <code>Dtalge</code> インスタンス
	 * 
	 * @throws NullPointerException 指定された基底パターン集合が <tt>null</tt> の場合
	 */
	public Dtalge patternProjection(DtBasePatternSet patterns) {
		Validations.validNotNull(patterns);
		
		Map<DtBase,Object> newMap = null;
		if (!patterns.isEmpty() && !this.isEmpty()) {
			newMap = createDefaultMap();
			for (Map.Entry<DtBase,Object> entry : data.entrySet()) {
				DtBase base = entry.getKey();
				if (patterns.matches(base)) {
					newMap.put(base, entry.getValue());
				}
			}
		}
		
		Dtalge newAlge = new Dtalge();
		if (newMap != null && !newMap.isEmpty()) {
			// 空のマップを生成したマップに置き換え
			newAlge.data = newMap;
		}
		return newAlge;
	}

	//------------------------------------------------------------
	// 振替系演算
	//------------------------------------------------------------

	/**
	 * 指定された基底の値が <code>srcObj</code> と等しい場合にのみ、
	 * その値を <code>dstObj</code> に置き換えた、<code>Dtalge</code> の
	 * 新しいインスタンスを返す。
	 * <p>
	 * 次の場合、このデータ代数元と同じ要素を持つデータ代数元を返す。
	 * <ul>
	 * <li>指定された基底が、このデータ代数元に存在しない場合
	 * <li>指定された基底に対応する値が、<code>srcObj</code> と等しくない場合
	 * </ul>
	 * 
	 * @param base	振替対象とするデータ代数基底
	 * @param srcObj	振替元の値
	 * @param dstObj	振替先の値
	 * 
	 * @throws NullPointerException 指定された基底が <tt>null</tt> の場合
	 * @throws IllegalValueOfDataTypeException	指定された振替先の値が基底のデータ型と異なる場合
	 */
	public Dtalge thesconv(DtBase base, Object srcObj, Object dstObj) {
		//--- check
		Validations.validNotNull(base, "'base' argument cannot be null.");
		DtDataTypes.validDataType(base.getTypeKey(), dstObj);

		//--- convert
		Dtalge newAlge = null;
		if (this.containsBase(base)) {
			DtBase cachedBase = CacheManager.cacheDtBase(base);
			Object targetValue = this.data.get(cachedBase);
			if (srcObj == targetValue || (srcObj != null && srcObj.equals(targetValue))) {
				newAlge = new Dtalge(this);
				newAlge.putValue(cachedBase, dstObj);
			}
		}
		
		return (newAlge != null ? newAlge : this);
	}

	/**
	 * 指定されたシソーラス定義を基に、指定された基底の値を、指定された分類集合の値に振り替える。
	 * このメソッドは、振り替え後の新しいデータ代数元を返す。
	 * <p>
	 * シソーラス定義に基づく分類集合への値振替では、指定された基底の値が
	 * シソーラス定義に属し、分類集合に含まれる値よりも小さい場合、
	 * 分類集合内の関係する値へ振り替える。
	 * <br>
	 * このメソッドでは、分類集合の値とは関係を持たない値の場合、その元は振り替え不可能と
	 * みなし <tt>null</tt> を返す。
	 * <p>
	 * 指定された基底が、このデータ代数元に存在しない場合、このメソッドは例外をスローする。
	 * 
	 * @param base	振替対象とするデータ代数基底
	 * @param thes	シソーラス定義
	 * @param words	振替先の値の分類集合
	 * @return	振替に成功した場合は、振替後の要素を格納する新しい <code>Dtalge</code> の
	 * インスタンスを返す。振替不可能の場合は <tt>null</tt> を返す。
	 * 
	 * @throws NullPointerException 引数に指定されたオブジェクトが <tt>null</tt> の場合
	 * @throws IllegalArgumentException 基底のデータ型が文字列型ではない場合、
	 * 									もしくは、指定された語句の集合が分類集合ではない場合
	 * @throws DtBaseNotFoundException 指定された基底がこのデータ代数基底に含まれていない場合
	 */
	public Dtalge thesconv(DtBase base, DtStringThesaurus thes, String...words) {
		Validations.validNotNull(base, "'base' argument cannot be null.");
		Validations.validNotNull(thes, "'thes' argument cannot be null.");
		Validations.validNotNull(words, "String collection argument cannot be null.");
		//--- 文字列型?
		Validations.validArgument(DtDataTypes.STRING.equals(base.getTypeKey()), "Illegal DtBase data type : %s", base.getTypeKey());
		//--- 分類集合?
		Validations.validArgument(thes.isClassificationSet(words), "Words must be Classification set in thesaurus.");

		// 基底の値を取得(存在しなければ、例外スロー)
		Object value = get(base);
		String targetWord = (String)value;
		
		// リレーションある？
		if (targetWord != null) {	// シソーラスに null は含まれない
			for (String word : words) {
				if (word.equals(targetWord)) {
					// 同じ値なので変換せず、この元をそのまま返す。
					return this;
				}
				if (thes.hasRelation(targetWord, word)) {
					// シソーラス定義に関係がある
					DtBase cachedBase = CacheManager.cacheDtBase(base);
					return this.put(cachedBase, word);
				}
			}
		}
		
		// 分類集合と比較不能な値の場合、null を返す。
		return null;
	}
	
	/**
	 * 指定されたシソーラス定義を基に、指定された基底の値を、指定された分類集合の値に振り替える。
	 * このメソッドは、振り替え後の新しいデータ代数元を返す。
	 * <p>
	 * シソーラス定義に基づく分類集合への値振替では、指定された基底の値が
	 * シソーラス定義に属し、分類集合に含まれる値よりも小さい場合、
	 * 分類集合内の関係する値へ振り替える。
	 * <br>
	 * このメソッドでは、分類集合の値とは関係を持たない値の場合、その元は振り替え不可能と
	 * みなし <tt>null</tt> を返す。
	 * <p>
	 * 指定された基底が、このデータ代数元に存在しない場合、このメソッドは例外をスローする。
	 * 
	 * @param base	振替対象とするデータ代数基底
	 * @param thes	シソーラス定義
	 * @param words	振替先の値の分類集合
	 * @return	振替に成功した場合は、振替後の要素を格納する新しい <code>Dtalge</code> の
	 * インスタンスを返す。振替不可能の場合は <tt>null</tt> を返す。
	 * 
	 * @throws NullPointerException 引数に指定されたオブジェクトが <tt>null</tt> の場合
	 * @throws IllegalArgumentException 基底のデータ型が文字列型ではない場合、
	 * 									もしくは、指定された語句の集合が分類集合ではない場合
	 * @throws DtBaseNotFoundException 指定された基底がこのデータ代数基底に含まれていない場合
	 */
	public Dtalge thesconv(DtBase base, DtStringThesaurus thes, Collection<? extends String> words) {
		Validations.validNotNull(base, "'base' argument cannot be null.");
		Validations.validNotNull(thes, "'thes' argument cannot be null.");
		Validations.validNotNull(words, "String collection argument cannot be null.");
		//--- 文字列型?
		Validations.validArgument(DtDataTypes.STRING.equals(base.getTypeKey()), "Illegal DtBase data type : %s", base.getTypeKey());
		//--- 分類集合?
		Validations.validArgument(thes.isClassificationSet(words), "Words must be Classification set in thesaurus.");

		// 基底の値を取得(存在しなければ、例外スロー)
		Object value = get(base);
		String targetWord = (String)value;
		
		// リレーションある？
		if (targetWord != null) {	// シソーラスに null は含まれない
			for (String word : words) {
				if (word.equals(targetWord)) {
					// 同じ値なので変換せず、この元をそのまま返す。
					return this;
				}
				if (thes.hasRelation(targetWord, word)) {
					// シソーラス定義に関係がある
					DtBase cachedBase = CacheManager.cacheDtBase(base);
					return this.put(cachedBase, word);
				}
			}
		}
		
		// 分類集合と比較不能な値の場合、null を返す。
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定された基底と値を代入する。
	 * <p>すでに同一基底が存在する場合、指定された値で上書きする。
	 * <p><b>注:</b>このメソッドは、インスタンスの値を書き換える。
	 * 
	 * @param base	データ代数の基底
	 * @param value	データ代数の値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws IllegalValueOfDataTypeException	指定された値が基底のデータ型と異なる場合
	 */
	protected void putValue(DtBase base, Object value) {
		Validations.validNotNull(base, "'base' argument cannot be null.");
		DtDataTypes.validDataType(base.getTypeKey(), value);
		this.data.put(base, value);
	}

	/**
	 * 指定されたデータ代数元に含まれるすべての要素を代入する。
	 * <p>すでに同一基底が存在する場合、指定された値で上書きする。
	 * <p><b>注:</b>このメソッドは、インスタンスの値を書き換える。
	 * 
	 * @param alge	このデータ代数元に配置される要素を持つデータ代数元
	 * 
	 * @throws NullPointerException 指定されたデータ代数元が <tt>null</tt> の場合
	 */
	protected void putValue(Dtalge alge) {
		this.data.putAll(alge.data);
	}

	/**
	 * 指定された基底と値をすべて代入する。
	 * <p>すでに同一基底が存在する場合、指定された値で上書きする。
	 * また、基底のインスタンスが <tt>null</tt> のものは無視される。
	 * <p><b>注:</b>このメソッドは、インスタンスの値を書き換える。
	 * 
	 * @param map	このデータ代数元に配置される要素を持つマップ
	 * 
	 * @throws NullPointerException	指定されたマップが <tt>null</tt> の場合
	 * @throws IllegalArgumentException	指定されたマップのキーに <tt>null</tt> が含まれている場合
	 * @throws IllegalValueOfDataTypeException	指定された値が対応する基底のデータ型と異なる場合
	 */
	protected void putAllValues(Map<? extends DtBase, ? extends Object> map) {
		for (Map.Entry<? extends DtBase, ? extends Object> entry : map.entrySet()) {
			DtBase base = CacheManager.cacheDtBase(entry.getKey());
			Validations.validArgument(base != null, "Key object cannot be null.");
			DtDataTypes.validDataType(base.getTypeKey(), entry.getValue());
			this.data.put(base, entry.getValue());
		}
	}

	/**
	 * <code>Dtalge</code> クラスのイテレーター。
	 * <p>
	 * このクラスは、データ代数元<code>(Dtalge)</code>からデータ代数要素(単一の
	 * 基底と値を持つデータ代数元)を順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、{@link #remove()} はサポートされていない。
	 * 
	 * @version 0.10	2008/08/26
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 * 
	 */
	private static class DtalgeIterator implements Iterator<Dtalge> {
		final Iterator<Map.Entry<DtBase, Object>> entryIterator;
		
		public DtalgeIterator(Map<DtBase,Object> targetMap) {
			entryIterator = targetMap.entrySet().iterator();
		}
		
		/**
		 * 繰り返し処理でさらに要素がある場合に <tt>true</tt> を返す。
		 * つまり、{@link #next()} が例外をスローしないで要素を返す場合に <tt>true</tt> を返す。
		 * 
		 * @return 反復子がさらに要素をもつ場合は <tt>true</tt>
		 */
		public boolean hasNext() {
			return entryIterator.hasNext();
		}
		
		/**
		 * 繰り返し処理で次の要素を返す。{@link #hasNext()} メソッドが <tt>false</tt> を
		 * 返すまでこのメソッドを繰り返し呼び出すと、基になるコレクション内の要素が一度だけ
		 * 返される。
		 * 
		 * @return 繰り返し処理で次の要素を返す
		 * 
		 * @throws NoSuchElementException 繰り返し処理でそれ以上要素がない場合
		 */
		public Dtalge next() {
			Map.Entry<DtBase, Object> entry = entryIterator.next();
			return new Dtalge(entry.getKey(), entry.getValue());
		}

		/**
		 * <code>DtalgeIterator</code> クラスでは、このメソッドはサポートされない。
		 * 
		 * @throws UnsupportedOperationException この例外を必ずスローする
		 */
		public void remove() {
			throw new UnsupportedOperationException("Unsupported \"remove\" operation!");
		}
	}

	//------------------------------------------------------------
	// for I/O
	//------------------------------------------------------------
	
	/**
	 * データ代数元の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.20
	 */
	public void toTableCSV(File csvFile)
		throws IOException, FileNotFoundException
	{
//		CsvWriter writer = new CsvWriter(csvFile);
//		try {
//			// キーワード出力
//			writer.writeLine(CSV_TABLE_KEYWORD_V2);
//			// エントリ出力
//			//--- 順序はオリジナル、nullは出力する
//			writeToTableCsv_v2(writer, null, false);
//		}
//		finally {
//			writer.close();
//		}
		//--- 順序はオリジナル、nullは出力する
		toTableCSV(null, false, csvFile);
	}
	
	/**
	 * データ代数元の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.20
	 */
	public void toTableCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
//		CsvWriter writer = new CsvWriter(csvFile, charsetName);
//		try {
//			// キーワード出力
//			writer.writeLine(CSV_TABLE_KEYWORD_V2);
//			// エントリ出力
//			//--- 順序はオリジナル、nullは出力する
//			writeToTableCsv_v2(writer, null, false);
//		}
//		finally {
//			writer.close();
//		}
		//--- 順序はオリジナル、nullは出力する
		toTableCSV(null, false, csvFile, charsetName);
	}
	
	/**
	 * データ代数元の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力される。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力される。
	 * 順序が指定されていない場合は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.40
	 */
	public void toTableCsvWithBaseOrder(DtBaseSet baseOrder, File csvFile)
		throws IOException, FileNotFoundException
	{
		toTableCSV(baseOrder, false, csvFile);
	}
	
	/**
	 * データ代数元の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力される。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力される。
	 * 順序が指定されていない場合は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.40
	 */
	public void toTableCsvWithBaseOrder(DtBaseSet baseOrder, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		toTableCSV(baseOrder, false, csvFile, charsetName);
	}
	
	/**
	 * データ代数元の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。また、基底に対応する値が <tt>null</tt> もしくは
	 * 空文字列の場合、その値と基底を削除してから出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.40
	 */
	public void toTableCsvWithoutNull(File csvFile)
		throws IOException, FileNotFoundException
	{
		toTableCSV(null, true, csvFile);
	}
	
	/**
	 * データ代数元の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。また、基底に対応する値が <tt>null</tt> もしくは
	 * 空文字列の場合、その値と基底を削除してから出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.40
	 */
	public void toTableCsvWithoutNull(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		toTableCSV(null, true, csvFile, charsetName);
	}
	
	/**
	 * データ代数元の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力される。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力される。
	 * 順序が指定されていない場合は、このインスタンスに格納されている基底の順序で出力される。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力せず、
	 * フィールドは空欄となる。
	 * 
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.40
	 */
	public void toTableCSV(DtBaseSet baseOrder, boolean withoutNull, File csvFile)
		throws IOException, FileNotFoundException
	{
		CsvWriter writer = new CsvWriter(csvFile);
		try {
			// キーワード出力
			writer.writeLine(CSV_TABLE_KEYWORD_V2);
			// エントリ出力
			writeToTableCsv_v2(writer, baseOrder, withoutNull);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * データ代数元の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力される。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力される。
	 * 順序が指定されていない場合は、このインスタンスに格納されている基底の順序で出力される。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力せず、
	 * フィールドは空欄となる。
	 * 
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.40
	 */
	public void toTableCSV(DtBaseSet baseOrder, boolean withoutNull, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		CsvWriter writer = new CsvWriter(csvFile, charsetName);
		try {
			// キーワード出力
			writer.writeLine(CSV_TABLE_KEYWORD_V2);
			// エントリ出力
			writeToTableCsv_v2(writer, baseOrder, withoutNull);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * データ代数元の内容を、指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている基底の順序で出力される。
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
			// キーワード出力
			writer.writeLine(CSV_KEYWORD_V2);
			// エントリ出力
			writeToCsv_v2(writer);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * データ代数元の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
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
	 */
	public void toCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		CsvWriter writer = new CsvWriter(csvFile, charsetName);
		try {
			// キーワード出力
			writer.writeLine(CSV_KEYWORD_V2);
			// エントリ出力
			writeToCsv_v2(writer);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * CSV フォーマットのファイルを読み込み、新しいデータ代数元を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>Dtalge</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 */
	static public Dtalge fromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		CsvReader reader = new CsvReader(csvFile);
		Dtalge newAlge = new Dtalge(DEFAULT_INITIAL_CAPACITY);
		
		try {
			newAlge.readFromCsv(reader);
		}
		finally {
			reader.close();
		}
		
		return newAlge;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しいデータ代数元を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>Dtalge</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	static public Dtalge fromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		Dtalge newAlge = new Dtalge(DEFAULT_INITIAL_CAPACITY);
		
		try {
			newAlge.readFromCsv(reader);
		}
		finally {
			reader.close();
		}
		
		return newAlge;
	}
	
	/**
	 * データ代数元の内容を、指定のファイルに XML フォーマットで出力する。
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
	 */
	public void toXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, DOMException,
				TransformerConfigurationException, TransformerException
	{
		XmlDocument xml = toXML();
		
		xml.toFile(xmlFile);
	}
	
	/**
	 * XML フォーマットのファイルを読み込み、新しいデータ代数元を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlFile 読み込む XML ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>Dtalge</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 */
	static public Dtalge fromXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException
	{
		//XmlDocument xmlDocument = XmlDocument.fromFile(xmlFile);
		//return Dtalge.fromXML(xmlDocument);
		
		// for SAX
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// フィーチャーの設定
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		// SAXパーサの生成
		SAXParser parser = factory.newSAXParser();
		// ハンドラの生成
		SaxParserHandler saxHandler = new SaxParserHandler();
		// パース
		parser.parse(xmlFile, saxHandler);

		return saxHandler.getTargetInstance();
	}

	//------------------------------------------------------------
	// Internal methods for I/O
	//------------------------------------------------------------

	/**
	 * CSVファイルフォーマットの第1行目のキーワード
	 */
	static protected final String CSV_KEYWORD		= "#DtalgebraSet";
	static protected final String CSV_KEYWORD_V2	= "#DtalgebraSet2";
	/**
	 * テーブル形式のCSVファイルフォーマットの第1行目のキーワード
	 */
	static protected final String CSV_TABLE_KEYWORD		= "#DtalgebraTable";
	static protected final String CSV_TABLE_KEYWORD_V2	= "#DtalgebraTable2";

	static protected final String CSV_VALUE_EMPTY		= "";
	static protected final String CSV_COMMAND_PREFIX	= "!%";
	static protected final String CSV_ESCAPE_PREFIX	= "!%!%";
	static protected final String CSV_COMMAND_NULL	= CSV_COMMAND_PREFIX + "N";

	/** CSV フィールドの値が、通常の文字列であることを示す。 **/
	static protected final int	CSVVALTYPE_STRING		= 9999;
	/** CSV フィールドの値が、<tt>null</tt> もしくは空文字であることを示す。 **/
	static protected final int	CSVVALTYPE_NONE			= 0;
	/** CSV フィールドの値が、特殊記号によりエスケープされた文字列であることを示す。 **/
	static protected final int	CSVVALTYPE_ESCAPED		= 1;
	/** CSV フィールドの値が、<tt>null</tt> を表す特殊値であることを示す。 **/
	static protected final int	CSVVALTYPE_CMD_NULL		= 2;
	/** CSV フィールドの値が、未定義の特殊値であることを示す。 **/
	static protected final int	CSVVALTYPE_CMD_UNKNOWN	= (-1);

	/**
	 * XMLファイルフォーマットでのデータ代数集合のルートノード名
	 */
	static protected final String XML_ELEMENT_NAME = "DtalgebraSet";
	/**
	 * XMLファイルフォーマットでのデータ代数元のノード名
	 */
	static protected final String XML_ELEMENT_GROUP = "Dtalgebra";
	/**
	 * XMLファイルフォーマットでのデータ代数元の要素ノード名
	 */
	static protected final String XML_ELEMENT_ITEM = "Dtelem";
	/**
	 * XMLファイルフォーマットでのデータ代数元要素の値のノード名
	 */
	static protected final String XML_ELEMENT_VALUE = "Dtvalue";

	/**
	 * CSV フィールドの値から、値の種別を取得する。
	 * @param value	判定する値
	 * @return	次の値を返す。
	 * <ul>
	 * <li><tt>null</tt> もしくは空文字の場合は、<code>{@link #CSVVALTYPE_NONE}</code>
	 * <li>特殊記号によってエスケープされた文字列の場合は、<code>{@link #CSVVALTYPE_ESCAPED}</code>
	 * <li><tt>null</tt> を表す特殊値の場合は、<code>{@link #CSVVALTYPE_CMD_NULL}</code>
	 * <li>未定義の特殊値の場合は、<code>{@link #CSVVALTYPE_CMD_UNKNOWN}</code>
	 * <li>通常の文字列の場合は、<code>{@link #CSVVALTYPE_STRING}</code>
	 * </ul>
	 */
	static protected int getCsvValueType(String value) {
		if (value==null) {
			// 値なし
			return CSVVALTYPE_NONE;
		}
		
		int len = value.length();
		if (len <= 0) {
			// 値なし
			return CSVVALTYPE_NONE;
		}
		else if (value.startsWith(CSV_COMMAND_PREFIX)) {
			// 特殊値
			if (len == 3) {
				char ch = value.charAt(2);
				if (ch=='N' || ch=='n') {
					// null を表す特殊値
					return CSVVALTYPE_CMD_NULL;
				}
				else {
					// 未定義の特殊値
					return CSVVALTYPE_CMD_UNKNOWN;
				}
			}
			else if (value.startsWith(CSV_COMMAND_PREFIX, 2)) {
				// 特殊値のエスケープ
				return CSVVALTYPE_ESCAPED;
			}
			else {
				// 未定義の特殊値
				return CSVVALTYPE_CMD_UNKNOWN;
			}
		}
		else {
			// 通常の文字列
			return CSVVALTYPE_STRING;
		}
	}
	
	/**
	 * データ代数元の内容から、XML ドキュメントを生成する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている要素の順序で出力される。
	 * 
	 * @return 生成された XML ドキュメント({@link basealge.io.XmlDocument} インスタンス)
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 */
	protected XmlDocument toXML()
		throws FactoryConfigurationError, ParserConfigurationException, DOMException
	{
		XmlDocument xml = new XmlDocument();
		
		Element root = xml.createElement(XML_ELEMENT_NAME);
		xml.append(root);
		
		Element item = encodeToElement(xml);
		root.appendChild(item);
		
		return xml;
	}

	/*
	 * CSVフォーマットで Dtalge の内容を出力
	 * 
	 * @param writer	CSVファイル出力オブジェクト
	 * 
	 * @throws	IOException	入出力エラーが発生した場合
	 *
	protected void writeToCsv(CsvWriter writer)
		throws IOException
	{
		// データ出力
		for (Map.Entry<DtBase, Object> entry : this.data.entrySet()) {
			//--- value
			Object value = entry.getValue();
			if (value == null)
				writer.writeField("");
			else if (value instanceof BigDecimal)
				writer.writeField(((BigDecimal)value).stripTrailingZeros().toPlainString());
			else
				writer.writeField(value.toString());
			//--- base
			entry.getKey().writeFieldToCSV(writer);
			//--- new line
			writer.newLine();
		}
		
		writer.flush();
	}
	*****/
	
	/**
	 * CSVフォーマットで Dtalge の内容を出力する。
	 * <tt>null</tt> の場合は 空文字を出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープする。
	 * 
	 * @param writer	CSVファイル出力オブジェクト
	 * 
	 * @throws	IOException	入出力エラーが発生した場合
	 * 
	 * @since 0.40
	 */
	protected void writeToCsv_v2(CsvWriter writer)
		throws IOException
	{
		// データ出力
		for (Map.Entry<DtBase, Object> entry : this.data.entrySet()) {
			//--- value
			Object value = entry.getValue();
			if (value == null) {
				// null 値は、空文字とする
				writer.writeField(CSV_VALUE_EMPTY);
			}
			else {
				String strValue;
				if (value instanceof BigDecimal)
					strValue = ((BigDecimal)value).stripTrailingZeros().toPlainString();
				else
					strValue = value.toString();
				if (strValue.startsWith(CSV_COMMAND_PREFIX)) {
					// 特殊記号で始まるものは、特殊記号でエスケープ
					writer.writeField(CSV_COMMAND_PREFIX.concat(strValue));
				}
				else {
					// 通常の文字列
					writer.writeField(strValue);
				}
			}
			//--- base
			entry.getKey().writeFieldToCSV(writer);
			//--- new line
			writer.newLine();
		}
		
		writer.flush();
	}

	/**
	 * 順序指定用基底集合に従い、指定の基底集合を並べ替えた新しい基底集合を返す。
	 * 順序指定用基底集合に含まれない基底は、オリジナルの基底集合の順序に従い、新しい基底集合の終端に追加される。
	 * なお、並べ替えが必要ない場合は、オリジナルの基底集合オブジェクトをそのまま返す。
	 * @param baseOrder	順序指定用基底集合
	 * @param orgBases		並べ替え対象の基底集合
	 * @return	並べ替え済みの新しい基底集合を返す。並べ替えが行われなかった場合は、並べ替え対象の基底集合を返す。
	 * @since 0.40
	 */
	static protected Set<DtBase> sortByOrderedBaseSet(final DtBaseSet baseOrder, final Set<DtBase> orgBases)
	{
		if (orgBases == null || baseOrder == null || orgBases.isEmpty() || baseOrder.isEmpty()) {
			//--- 基底セットが空の場合は、オリジナルの基底セットを返す
			return orgBases;
		}
		
		// intersection
		DtBaseSet newSet = (DtBaseSet)baseOrder.clone();
		newSet.retainAll(orgBases);
		if (newSet.isEmpty()) {
			//--- 順序セットにオリジナル基底が含まれていない場合は、オリジナルの基底セットを返す。
			return orgBases;
		}
		
		// union
		newSet.addAll(orgBases);
		return newSet;
	}

	/*
	 * テーブル形式のCSVフォーマットで Dtalge の内容を出力
	 * 
	 * @param writer	CSVファイル出力オブジェクト
	 * @throws IOException	入出力エラーが発生した場合
	 * 
	 * @since 0.20
	 *
	protected void writeToTableCsv(CsvWriter writer)
		throws IOException
	{
		if (!data.isEmpty()) {
			// 基底集合を取得
			Set<DtBase> bases = this.data.keySet();
			
			// 基底情報を出力
			writeBasesToTableCsv(writer, bases);
			
			// 要素値を出力
			//--- null値と空文字列は空文字列(値なしのフィールド)として出力する
			writeValuesToTableCsv(writer, bases);
			
			writer.flush();
		}
	}
	*****/

	/**
	 * テーブル形式のCSVフォーマットで Dtalge の内容を出力する。
	 * 基底のない要素は空文字をフィールドに出力し、
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値を出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープする。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底を出力する。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力する。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力せず、
	 * フィールドは空欄となる。
	 * @param writer	CSVファイル出力オブジェクト
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @since 0.40
	 */
	protected void writeToTableCsv_v2(CsvWriter writer, DtBaseSet baseOrder, boolean withoutNull)
		throws IOException
	{
		if (!data.isEmpty()) {
			// 基底集合を取得
			Set<DtBase> bases = this.data.keySet();
			//--- 基底集合を並べ替える
			bases = sortByOrderedBaseSet(baseOrder, bases);
			
			// 基底情報を出力
			writeBasesToTableCsv(writer, bases);
			
			// 要素値を出力
			if (withoutNull) {
				//--- null や空文字は出力しない
				writeValuesToTableCsv_v2(writer, bases, CSV_VALUE_EMPTY);
			} else {
				//--- 空文字を null として出力
				writeValuesToTableCsv_v2(writer, bases, CSV_COMMAND_NULL);
			}
			
			writer.flush();
		}
	}

	/**
	 * テーブル形式のCSVフォーマットで、全ての DtBase の内容を出力
	 * <p><b>(注)</b>
	 * <blockquote>
	 * <em>bases</em> の要素が空ではないこと。このメソッドではチェックしない。
	 * </blockquote>
	 * 
	 * @param writer	CSVファイル出力オブジェクト
	 * @param bases		出力対象の基底の集合
	 * @throws IOException	入出力エラーが発生した場合
	 * 
	 * @since 0.20
	 */
	static protected void writeBasesToTableCsv(CsvWriter writer, Set<DtBase> bases)
		throws IOException
	{
		assert !bases.isEmpty();
		
		// バッファ作成
		StringBuilder sbName = new StringBuilder();
		StringBuilder sbType = new StringBuilder();
		StringBuilder sbAttr = new StringBuilder();
		StringBuilder sbSubj = new StringBuilder();
		
		// 先頭の要素を出力
		Iterator<DtBase> it = bases.iterator();
		DtBase base = it.next();
		sbName.append(writer.enquote(base.getNameKey()));
		sbType.append(writer.enquote(base.getTypeKey()));
		sbAttr.append(writer.enquote(base.getAttributeKey()));
		sbSubj.append(writer.enquote(base.getSubjectKey()));
		
		// ２番目以降の要素を出力
		char delim = writer.getDelimiterChar();
		while (it.hasNext()) {
			base = it.next();
			//--- name
			sbName.append(delim);
			sbName.append(writer.enquote(base.getNameKey()));
			//--- type
			sbType.append(delim);
			sbType.append(writer.enquote(base.getTypeKey()));
			//--- attr
			sbAttr.append(delim);
			sbAttr.append(writer.enquote(base.getAttributeKey()));
			//--- subject
			sbSubj.append(delim);
			sbSubj.append(writer.enquote(base.getSubjectKey()));
		}
		
		// 基底要素をCSVレコードとして出力
		//--- name
		writer.writeLine(sbName.toString());
		//--- type
		writer.writeLine(sbType.toString());
		//--- attr
		writer.writeLine(sbAttr.toString());
		//--- subject
		writer.writeLine(sbSubj.toString());
	}

	/*
	 * 指定された基底集合の順序に従い、この Dtalge の要素の値を
	 * テーブル形式のCSVフォーマットで出力する。
	 * <br>
	 * このメソッドは、この Dtalge の要素の値を、指定された基底集合の基底順序で
	 * 1レコードに出力する。
	 * 
	 * <p><b>(注)</b>
	 * <blockquote>
	 * <em>bases</em> の要素が空ではないこと。このメソッドではチェックしない。
	 * </blockquote>
	 * 
	 * @param writer	CSVファイル出力オブジェクト
	 * @param bases		出力対象の基底の集合
	 * @throws IOException	入出力エラーが発生した場合
	 * 
	 * @since 0.20
	 *
	protected void writeValuesToTableCsv(CsvWriter writer, Set<DtBase> bases)
		throws IOException
	{
		assert !bases.isEmpty();
		
		for (DtBase base : bases) {
			Object value = data.get(base);
			if (value == null)
				writer.writeField("");	// null値は空文字列とする
			else if (value instanceof BigDecimal)
				writer.writeField(((BigDecimal)value).stripTrailingZeros().toPlainString());
			else
				writer.writeField(value.toString());
		}
		
		writer.newLine();
	}
	*****/

	/**
	 * 指定された基底集合の順序に従い、この Dtalge の要素の値を
	 * テーブル形式のCSVフォーマットで出力する。
	 * 基底のない要素は空文字をフィールドに出力し、
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値を出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープする。
	 * <br>
	 * このメソッドでは、この Dtalge の要素の値を、指定された基底集合の基底順序で
	 * 1レコードに出力する。
	 * 
	 * <p><b>(注)</b>
	 * <blockquote>
	 * <em>bases</em> の要素が空ではないこと。このメソッドではチェックしない。
	 * </blockquote>
	 * 
	 * @param writer		CSVファイル出力オブジェクト
	 * @param bases			出力対象の基底の集合
	 * @param nullKeyword	<tt>null</tt> を表す特殊値
	 * @throws IOException	入出力エラーが発生した場合
	 * 
	 * @since 0.40
	 */
	protected void writeValuesToTableCsv_v2(CsvWriter writer, Set<DtBase> bases, String nullKeyword)
		throws IOException
	{
		assert !bases.isEmpty();

		for (DtBase base : bases) {
			if (!data.containsKey(base)) {
				// 基底なし
				writer.writeField(CSV_VALUE_EMPTY);	// 基底なしは空文字を出力
			}
			else {
				Object value = data.get(base);
				if (value == null) {
					// null 値
					writer.writeField(nullKeyword);
				}
				else {
					String strValue;
					if (value instanceof BigDecimal)
						strValue = ((BigDecimal)value).stripTrailingZeros().toPlainString();
					else
						strValue = value.toString();
					if (strValue.length() <= 0) {
						// 空文字列は null 値とする
						writer.writeField(nullKeyword);
					}
					else if (strValue.startsWith(CSV_COMMAND_PREFIX)) {
						// 特殊記号で始まるものは、特殊記号でエスケープ
						writer.writeField(CSV_COMMAND_PREFIX.concat(strValue));
					}
					else {
						// 通常の文字列
						writer.writeField(strValue);
					}
				}
			}
		}
		
		writer.newLine();
	}

	/**
	 * CSVフォーマットで Dtalge の内容を読み込む。
	 * このメソッドでは、指定されたデータ代数集合のCSVファイルを、
	 * データ代数元として読み込む。
	 * 
	 * @param reader	CSVファイル入力オブジェクト
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 */
	protected void readFromCsv(CsvReader reader)
		throws IOException, CsvFormatException
	{
		CsvReader.CsvRecord record;
		CsvFileType	csvType = null;
//		boolean isTableCsv = false;
		
		// 先頭１行目のキーワードをチェック
		record = reader.readRecord();
		if (record == null || !record.hasFields()) {
			// undefined Dtalge CSV ID
			throw new CsvFormatException("DtAlgeSet file ID not found.");
		} else {
			CsvReader.CsvField field = record.getField(0);
			if (field == null) {
				throw new CsvFormatException("Illegal DtAlgeSet file ID.", record.getLineNo(), 1);
			}
			String strFieldValue = field.getValue();
//			if (CSV_KEYWORD.equals(strFieldValue)) {
//				isTableCsv = false;
//			}
//			else if (CSV_TABLE_KEYWORD.equals(strFieldValue)) {
//				isTableCsv = true;
//			}
//			else {
//				throw new CsvFormatException("Illegal DtAlgeSet file ID.", record.getLineNo(), 1);
//			}
			csvType = CsvFileType.fromCsvKeyword(strFieldValue);
			if (csvType == null) {
				throw new CsvFormatException("Illegal DtAlgeSet file ID.", record.getLineNo(), 1);
			}
			// 以降のフィールドは無視
		}
		
		// データ読み込み
//		if (isTableCsv) {
//			// テーブル形式のCSVファイル読み込み
//			List<DtBase> baselist = readBasesFromTableCsv(reader);
//			if (baselist != null) {
//				while ((record = reader.readRecord()) != null) {
//					readValuesFromTableCsv(record, baselist);
//				}
//			}
//		}
//		else {
//			// CSV標準形の読み込み
//			while ((record = reader.readRecord()) != null) {
//				// 空行、もしくは値のないレコードはスキップ
//				if (!record.hasFields() || !record.hasValues()) {
//					continue;
//				}
//				
//				// フィールドの値読み出し
//				readRecordFromCsv(record);
//			}
//		}
		if (csvType.isTable()) {
			// テーブル形式のCSVファイル読み込み
			List<DtBase> baselist = readBasesFromTableCsv(reader);
			if (baselist != null) {
				if (csvType.version() == CsvFileType.V2) {
					//--- テーブル形式 v2
					while ((record = reader.readRecord()) != null) {
						readValuesFromTableCsv_v2(record, baselist);
					}
				} else {
					//--- 初期テーブル形式
					while ((record = reader.readRecord()) != null) {
						readValuesFromTableCsv(record, baselist);
					}
				}
			}
		}
		else {
			// CSV標準形の読み込み
			if (csvType.version() == CsvFileType.V2) {
				//--- v2
				while ((record = reader.readRecord()) != null) {
					// 空行、もしくは値のないレコードはスキップ
					if (!record.hasFields() || !record.hasValues()) {
						continue;
					}
					// フィールドの値読み出し
					readRecordFromCsv_v2(record);
				}
			} else {
				//--- 初期形式
				while ((record = reader.readRecord()) != null) {
					// 空行、もしくは値のないレコードはスキップ
					if (!record.hasFields() || !record.hasValues()) {
						continue;
					}
					// フィールドの値読み出し
					readRecordFromCsv(record);
				}
			}
		}
	}

	/**
	 * CSVフォーマットの1レコードの内容を読み込む。
	 * <br>値のフィールドが空欄の場合、<tt>null</tt> の値として読み込む。
	 * なお、このメソッドでは特殊記号に関する処理は行わない。
	 * 
	 * @param recReader		CSVフォーマットの1レコード入力オブジェクト
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 */
	protected void readRecordFromCsv(CsvReader.CsvRecord record)
		throws IOException, CsvFormatException
	{
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);
		
		// value
		String strValue = freader.readValue();
		if (Strings.isNullOrEmpty(strValue)) {
			// 長さ 0 の文字列も null とみなす
			strValue = null;
		}
		
		// base
		DtBase newBase = DtBase.readFieldFromCSV(freader);
		
		// データ型の正当性チェック
		Object value = null;
		try {
			value = DtDataTypes.valueOf(newBase.getTypeKey(), strValue);
		}
		catch (Throwable ex) {
			throw new CsvFormatException(ex.getMessage(), freader.getLineNo(), 1);
		}
		
		// データを追加
		this.putValue(newBase, value);
	}
	
	/**
	 * CSVフォーマットの1レコードの内容を読み込む。
	 * <br>値のフィールドが空欄の場合、<tt>null</tt> の値として読み込む。
	 * また、特殊記号で始まる値は、特殊値として読み込む。
	 * 
	 * @param recReader		CSVフォーマットの1レコード入力オブジェクト
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 * @since 0.40
	 */
	protected void readRecordFromCsv_v2(CsvReader.CsvRecord record)
		throws IOException, CsvFormatException
	{
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);
		
		// value
		String strValue = freader.readValue();
//		if (Strings.isNullOrEmpty(strValue)) {
//			// 長さ 0 の文字列も null とみなす
//			strValue = null;
//		}
		int vtype = getCsvValueType(strValue);
		switch (vtype) {
			case CSVVALTYPE_NONE :
				strValue = null;
				break;
			case CSVVALTYPE_ESCAPED :
				strValue = strValue.substring(CSV_COMMAND_PREFIX.length());
				break;
			case CSVVALTYPE_CMD_NULL :
				strValue = null;
				break;
			case CSVVALTYPE_CMD_UNKNOWN :
				throw new CsvFormatException("Undefined keyword : " + strValue, freader.getLineNo(), 1);
			// CSVVALTYPE_STRING :
		}
		
		// base
		DtBase newBase = DtBase.readFieldFromCSV(freader);
		
		// データ型の正当性チェック
		Object value = null;
		try {
			value = DtDataTypes.valueOf(newBase.getTypeKey(), strValue);
		}
		catch (Throwable ex) {
			throw new CsvFormatException(ex.getMessage(), freader.getLineNo(), 1);
		}
		
		// データを追加
		this.putValue(newBase, value);
	}

	/**
	 * テーブル形式のCSVフォーマットから基底のレコードを読み込む。
	 * @param reader	CSVファイル入力オブジェクト
	 * @return	読み込んだ基底のリスト。フィールドの順序の通りに基底が格納される。
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 * 
	 * @since 0.20
	 */
	static protected List<DtBase> readBasesFromTableCsv(CsvReader reader)
		throws IOException, CsvFormatException
	{
		CsvReader.CsvRecord record;
		
		// name キーのレコードチェック
		record = reader.readRecord();
		if (record == null) {
			// レコードは存在しない
			return null;
		}
		else if (!record.hasFields()) {
			// 以降、フィールドの有無をチェック
			int errLineNo = record.getLineNo();
			int errFieldNo = 1;
			while ((record = reader.readRecord()) != null) {
				if (record.hasFields() || record.hasValues()) {
					// フィールドが存在する場合、名前キーが省略されたとみなしエラーとする。
					throw new CsvFormatException("name key cannot be omitted.", errLineNo, errFieldNo);
				}
			}
			// フィールドが存在しないため、空の要素とみなす
			return null;
		}
		
		// バッファの生成
		ArrayList<DtBase.BaseKeyContainer> keylist = new ArrayList<DtBase.BaseKeyContainer>();
		
		// name キーの読み込み
		DtBase.readNameKeyFieldsFromTableCsv(record, keylist);
		
		// type キーの読み込み
		record = reader.readRecord();
		if (record == null) {
			// type キーの省略は許可しない
			throw new CsvFormatException("type key cannot be omitted.", reader.getLineNo(), 1);
		}
		DtBase.readTypeKeyFieldsFromTableCsv(record, keylist);
		
		// attr キーの読み込み
		record = reader.readRecord();
		if (record != null) {
			DtBase.readAttributeKeyFieldsFromTableCsv(record, keylist);
		}
		
		// subject キーの読み込み
		record = reader.readRecord();
		if (record != null) {
			DtBase.readSubjectKeyFieldsFromTableCsv(record, keylist);
		}
		
		// 基底キーの集合を生成
		ArrayList<DtBase> baselist = new ArrayList<DtBase>(keylist.size());
		for (DtBase.BaseKeyContainer container : keylist) {
			DtBase newBase = DtBase.newBase(container._baseKeys);
			baselist.add(newBase);
		}
		return baselist;
	}

	/**
	 * テーブル形式のCSVフォーマットから、値のレコードを1レコード読み込む。
	 * <br>値のフィールドが空欄の場合、<tt>null</tt> の値として読み込む。
	 * なお、このメソッドでは特殊記号に関する処理は行わない。
	 * @param record	CSVフォーマットの1レコード入力オブジェクト
	 * @param baselist	基底のリスト。フィールドの順序通りに読み込まれた基底が格納されていること。
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 * 
	 * @since 0.20
	 */
	protected void readValuesFromTableCsv(CsvReader.CsvRecord record, List<DtBase> baselist)
		throws IOException, CsvFormatException
	{
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);
		
		// read values
		for (DtBase newBase : baselist) {
			// read value
			String strValue = freader.readValue();
			if (Strings.isNullOrEmpty(strValue)) {
				// 長さ 0 の文字列も null とみなす
				strValue = null;
			}
			
			// データ型の正当性チェック
			Object value = null;
			try {
				value = DtDataTypes.valueOf(newBase.getTypeKey(), strValue);
			}
			catch (Throwable ex) {
				throw new CsvFormatException(ex.getMessage(), freader.getLineNo(), freader.getNextPosition());
			}
			
			// データを追加
			this.putValue(newBase, value);
		}
		
		// まだフィールドが存在する場合は、対応する基底が存在しない
		if (freader.hasNextField()) {
			throw new CsvFormatException("There is not DtBase corresponding to value.",
					freader.getLineNo(), freader.getNextPosition()+1);
		}
	}

	/**
	 * テーブル形式のCSVフォーマットから、値のレコードを1レコード読み込む。
	 * <br>値のフィールドが空欄の場合、値も基底も存在しない要素として扱う。
	 * また、特殊記号で始まる値は、特殊値として読み込む。
	 * @param record	CSVフォーマットの1レコード入力オブジェクト
	 * @param baselist	基底のリスト。フィールドの順序通りに読み込まれた基底が格納されていること。
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws CsvFormatException	カラムのデータが正しくない場合
	 * 
	 * @since 0.40
	 */
	protected void readValuesFromTableCsv_v2(CsvReader.CsvRecord record, List<DtBase> baselist)
		throws IOException, CsvFormatException
	{
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);

		// read values
		for (DtBase newBase : baselist) {
			// read value
			String strValue = freader.readValue();
			int vtype = getCsvValueType(strValue);
			if (vtype != CSVVALTYPE_NONE) {
				// 文字列が存在する場合のみ、基底に対応する値をデータ代数元に追加する。
				// CSVフィールドが空欄の場合、テーブル形式においては、値と基底が存在しない
				// ものとして扱う。これはメモリ使用量を減らすための対策。
				switch (vtype) {
					case CSVVALTYPE_ESCAPED :
						strValue = strValue.substring(CSV_COMMAND_PREFIX.length());
						break;
					case CSVVALTYPE_CMD_NULL :
						strValue = null;
						break;
					case CSVVALTYPE_CMD_UNKNOWN :
						throw new CsvFormatException("Undefined keyword : " + strValue, freader.getLineNo(), freader.getNextPosition());
					// CSVVALTYPE_STRING :
				}
				
				// データ型の正当性チェック
				Object value = null;
				try {
					value = DtDataTypes.valueOf(newBase.getTypeKey(), strValue);
				}
				catch (Throwable ex) {
					throw new CsvFormatException(ex.getMessage(), freader.getLineNo(), freader.getNextPosition());
				}

				// データを追加
				this.putValue(newBase, value);
			}
		}

		// まだフィールドが存在する場合は、対応する基底が存在しない
		if (freader.hasNextField()) {
			throw new CsvFormatException("There is not DtBase corresponding to value.",
					freader.getLineNo(), freader.getNextPosition()+1);
		}
	}

	/**
	 * このインスタンスの内容で、XMLドキュメントとなるエレメントを生成する。
	 * 
	 * @param xmlDocument	生成に使用するドキュメントオブジェクト
	 * @return	生成したエレメントのインスタンス
	 * @throws DOMException	XMLのドキュメントエラー
	 */
	protected Element encodeToElement(XmlDocument xmlDocument) throws DOMException
	{
		// create node
		Element node = xmlDocument.createElement(XML_ELEMENT_GROUP);
		
		// create entries
		for (Map.Entry<DtBase, Object> entry : this.data.entrySet()) {
			//--- value
			Object value = entry.getValue();
			Element xmlValue = xmlDocument.createElement(XML_ELEMENT_VALUE);
			if (value == null)
				xmlValue.appendChild(xmlDocument.createTextNode(""));
			else if (value instanceof BigDecimal)
				xmlValue.appendChild(xmlDocument.createTextNode(((BigDecimal)value).stripTrailingZeros().toPlainString()));
			else
				xmlValue.appendChild(xmlDocument.createTextNode(value.toString()));
			
			//--- base
			DtBase base = entry.getKey();
			Element xmlBase = base.encodeToElement(xmlDocument);
			
			// regist to group element
			Element xmlItem = xmlDocument.createElement(XML_ELEMENT_ITEM);
			xmlItem.appendChild(xmlValue);
			xmlItem.appendChild(xmlBase);
			node.appendChild(xmlItem);
		}
		
		// completed
		return node;
	}
	
	static protected class CsvFileType {
		static public int	V1 = 1;
		static public int V2 = 2;
		
		protected final boolean		_isTable;
		protected final int			_version;
		
		static public CsvFileType fromCsvKeyword(String keyword) {
			if (CSV_KEYWORD_V2.equals(keyword)) {
				return new CsvFileType(false, V2);
			}
			else if (CSV_TABLE_KEYWORD_V2.equals(keyword)) {
				return new CsvFileType(true, V2);
			}
			else if (CSV_KEYWORD.equals(keyword)) {
				return new CsvFileType(false, V1);
			}
			else if (CSV_TABLE_KEYWORD.equals(keyword)) {
				return new CsvFileType(true, V1);
			}
			else {
				// undefined keyword
				return null;
			}
		}
		
		public CsvFileType(boolean isTable, int version) {
			_isTable = isTable;
			_version = version;
		}
		
		public boolean isTable() {
			return _isTable;
		}
		
		public int version() {
			return _version;
		}
		
		public String keyword() {
			if (_isTable) {
				if (_version == V2) {
					return CSV_TABLE_KEYWORD_V2;
				} else {
					return CSV_TABLE_KEYWORD;
				}
			} else {
				if (_version == V2) {
					return CSV_KEYWORD_V2;
				} else {
					return CSV_KEYWORD;
				}
			}
		}
	}

	/**
	 * <code>Dtalge</code> の <code>&lt;Dtalgebra&gt;</code> ノードの
	 * SAX デコーダー。
	 * このクラスは、<code>&lt;Dtalgebra&gt;</code> ノードに含まれる
	 * 全てのエレメントから、<code>Dtalge</code> インスタンスを生成するための
	 * 機能を提供する。
	 * 
	 * @version 0.20	2010/02/25
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	static protected class SaxDecoder {
		protected boolean				isFinished = false;
		
		protected final Locator		saxLocator;
		protected final Dtalge		parsedAlge;
		
		protected final Stack<String>	stackElement = new Stack<String>();
		protected final StringBuilder	parsedChars  = new StringBuilder();
		protected String				parsedValue  = null;
		protected DtBase				parsedBase   = null;

		/**
		 * SAX デコーダーの新しいインスタンスを生成する。
		 * 
		 * @param saxLocator	<code>Locator</code> オブジェクト
		 * @param prevElements	このデコーダーを呼び出す時点での、SAXパースにおける
		 * 						ノード名の階層スタック
		 */
		public SaxDecoder(Locator saxLocator, Stack<String> prevElements) {
			this(saxLocator, prevElements, new Dtalge());
		}
		
		/**
		 * SAX デコーダーの新しいインスタンスを生成する。
		 * 
		 * @param saxLocator	<code>Locator</code> オブジェクト
		 * @param prevElements	このデコーダーを呼び出す時点での、SAXパースにおける
		 * 						ノード名の階層スタック
		 * @param alge			解析時に取得した要素を格納する <code>Dtalge</code> のインスタンス
		 */
		public SaxDecoder(Locator saxLocator, Stack<String> prevElements, Dtalge alge) {
			this.saxLocator = saxLocator;
			this.stackElement.addAll(prevElements);
			this.parsedAlge = alge;
		}

		/**
		 * このデコーダーが解析するノードが終了した場合は <tt>true</tt> を返す。
		 * 
		 * @return	このデコーダーの解析が終了している場合に <tt>true</tt>
		 */
		public boolean isDecodeFinished() {
			return isFinished;
		}

		/**
		 * このデコーダーによって生成された <code>Dtalge</code> のインスタンスを返す。
		 * 
		 * @return <code>Dtalge</code> インスタンス
		 */
		public Dtalge getTargetInstance() {
			return parsedAlge;
		}

		/**
		 * XMLノードの開始通知を受け取る。
		 * このメソッドでは、このクラスが解析するノードのルートとなる <code>&lt;Dtalgebra&gt;</code> に
		 * ついては、その上位ノードが何であるかをチェックしないので、このメソッド呼び出しの前に
		 * 必要に応じてチェックすること。
		 * 
		 * @param uri			名前空間 <code>URI</code>。要素が名前空間 <code>URI</code> を持たない場合、
		 * 						または名前空間処理が行われない場合は空文字列
		 * @param localName		接頭辞を含まないローカル名。名前空間処理が行われない場合は空文字列
		 * @param qName			接頭辞を持つ修飾名。修飾名を使用できない場合は空文字列
		 * @param attributes	要素に追加された属性。属性がない場合は空の <code>Attributes</code> オブジェクト
		 * 						になる。<code>decodeStartElement</code> が返されたあとのこのオブジェクトの
		 * 						値は定義されない 
		 * @throws SAXException	SAX 例外。ほかの例外をラップしている可能性がある
		 */
		public void decodeStartElement(String uri, String localName, String qName,
										Attributes attributes)
			throws SAXException
		{
			// clear string buffer
			parsedChars.setLength(0);
			
			// check node
			if (XML_ELEMENT_GROUP.equals(qName)) {
				// top ノードは上位はチェック済みとし、チェックしない
			}
			else if (XML_ELEMENT_ITEM.equals(qName)) {
				XmlErrors.validChildOfParent(saxLocator, qName,
						XmlErrors.peekStringStack(stackElement), XML_ELEMENT_GROUP);
			}
			else if (XML_ELEMENT_VALUE.equals(qName)) {
				XmlErrors.validChildOfParent(saxLocator, qName,
						XmlErrors.peekStringStack(stackElement), XML_ELEMENT_ITEM);
				XmlErrors.validSingleElement(parsedValue==null, saxLocator, qName);
			}
			else if (DtBase.XML_ELEMENT_NAME.equals(qName)) {
				XmlErrors.validChildOfParent(saxLocator, qName,
						XmlErrors.peekStringStack(stackElement), XML_ELEMENT_ITEM);
				XmlErrors.validSingleElement(parsedBase==null, saxLocator, qName);
				//--- docode DtBase attributes
				parsedBase = DtBase.decodeFromXmlAttributes(saxLocator, qName, attributes);
			}
			else {
				XmlErrors.errorUnknownElement(saxLocator, qName);
			}
			
			// stack element
			stackElement.add(qName);
		}

		/**
		 * XMLノードの終了通知を受け取る。
		 * このメソッドでは、このクラスが解析するノードのルートとなる <code>&lt;Dtalgebra&gt;</code> を
		 * 受け取った場合、このクラスによるノードの解析が完了したことを示す <tt>true</tt> を返す。
		 * 
		 * @param uri			名前空間 <code>URI</code>。要素が名前空間 <code>URI</code> を持たない場合、
		 * 						または名前空間処理が行われない場合は空文字列
		 * @param localName		接頭辞を含まないローカル名。名前空間処理が行われない場合は空文字列
		 * @param qName			接頭辞を持つ修飾名。修飾名を使用できない場合は空文字列
		 * 
		 * @return	このメソッドでの解析が終了した場合は <tt>true</tt>
		 * 
		 * @throws SAXException	SAX 例外。ほかの例外をラップしている可能性がある
		 */
		public boolean decodeEndElement(String uri, String localName, String qName)
			throws SAXException
		{
			// check element pair
			XmlErrors.validEndElement(saxLocator,
					XmlErrors.peekStringStack(stackElement), qName);
			
			// remove stack
			if (!stackElement.empty()) {
				stackElement.pop();
			}
			
			// decode elements
			if (XML_ELEMENT_VALUE.equals(qName)) {
				String strValue = parsedChars.toString();
				parsedChars.setLength(0);
				parsedValue = (strValue != null ? strValue : "");
			}
			else if (XML_ELEMENT_ITEM.equals(qName)) {
				XmlErrors.validNeedElement(parsedValue != null, saxLocator,
						qName, XML_ELEMENT_VALUE);
				XmlErrors.validNeedElement(parsedBase != null, saxLocator,
						qName, DtBase.XML_ELEMENT_NAME);
				
				// データ型の正当性チェック
				Object value = null;
				try {
					value = DtDataTypes.valueOf(parsedBase.getTypeKey(),
										(parsedValue.length() > 0 ? parsedValue : null));
				}
				catch (Exception ex) {
					throw new SAXParseException(ex.getLocalizedMessage(), saxLocator, ex);
				}
				
				// データを追加
				parsedAlge.putValue(parsedBase, value);
				
				parsedBase = null;
				parsedValue = null;
			}
			else if (XML_ELEMENT_GROUP.equals(qName)) {
				isFinished = true;
			}
			
			return isFinished;
		}

		/**
		 * 文字データの通知を受け取る。
		 * 
		 * @param ch		XML 文書の文字
		 * @param start		配列内の開始位置
		 * @param length	配列から読み取られる文字数 
		 * @throws SAXException	SAX 例外。ほかの例外をラップしている可能性がある
		 */
		public void decodeCharacters(char[] ch, int start, int length)
			throws SAXException
		{
			String elemParent = stackElement.isEmpty() ? null : stackElement.peek();
			if (XML_ELEMENT_VALUE.equals(elemParent)) {
				parsedChars.append(ch, start, length);
			}
		}
	}

	/**
	 * <code>Dtalge</code> の SAX パーサーハンドラ。
	 * 
	 * @version 0.20	2010/02/25
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	static class SaxParserHandler extends DefaultHandler
	{
		private final Stack<String> stackElement = new Stack<String>();
		private final Dtalge alge = new Dtalge();
		
		private Locator saxLocator = null;
		private SaxDecoder decoder = null;
		
		public Dtalge getTargetInstance() {
			return alge;
		}
		
		//------------------------------------------------------------
		// Implement DefaultHandler interfaces
		//------------------------------------------------------------

		@Override
		public void setDocumentLocator(Locator locator) {
			// cache Locator instance
			saxLocator = locator;
		}

		@Override
		public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException
		{
			return super.resolveEntity(publicId, systemId);
		}

		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException
		{
			// check node
			if (decoder != null) {
				decoder.decodeStartElement(uri, localName, qName, attributes);
			}
			else {
				if (XML_ELEMENT_NAME.equals(qName)) {
					XmlErrors.validChildOfParent(saxLocator, qName, XmlErrors.peekStringStack(stackElement));
					
					// stack element
					stackElement.add(qName);
				}
				else if (XML_ELEMENT_GROUP.equals(qName)) {
					XmlErrors.validChildOfParent(saxLocator, qName, XmlErrors.peekStringStack(stackElement), XML_ELEMENT_NAME);
					decoder = new SaxDecoder(saxLocator, stackElement, alge);
					decoder.decodeStartElement(uri, localName, qName, attributes);
				}
				else {
					XmlErrors.errorUnknownElement(saxLocator, qName);
					
					// stack element
					stackElement.add(qName);
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
			throws SAXException
		{
			if (decoder != null) {
				boolean finished = decoder.decodeEndElement(uri, localName, qName);
				if (finished) {
					decoder = null;
				}
			}
			else {
				// check element pair
				XmlErrors.validEndElement(saxLocator, XmlErrors.peekStringStack(stackElement), qName);
				
				// remove stack
				if (!stackElement.empty()) {
					stackElement.pop();
				}
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (decoder != null) {
				decoder.decodeCharacters(ch, start, length);
			}
		}

		@Override
		public void error(SAXParseException e) throws SAXException {
			throw e;
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			throw e;
		}

		@Override
		public void warning(SAXParseException e) throws SAXException {
			// warning print to System.err
			super.warning(e);
			String msg;
			Throwable wrapped = e.getException();
			if (wrapped != null) {
				msg = XmlErrors.formatMessage(
						XmlErrors.Format.WARNING_PLUS_WRAPPED_MSG,
						XmlErrors.getMessageAndLocation(e),
						wrapped.getMessage());
			} else {
				msg = XmlErrors.formatMessage(
						XmlErrors.Format.WARNING_MSG,
						XmlErrors.getMessageAndLocation(e));
			}
			System.err.println(msg);
		}
	}
}
