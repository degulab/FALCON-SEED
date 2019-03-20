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
 * @(#)DtAlgeSet.java	0.40	2012/06/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtAlgeSet.java	0.30	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtAlgeSet.java	0.20	2010/03/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtAlgeSet.java	0.10	2008/08/26
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

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

import dtalge.Dtalge.SaxDecoder;
import dtalge.exception.CsvFormatException;
import dtalge.io.IDataOutput;
import dtalge.io.internal.CsvReader;
import dtalge.io.internal.CsvWriter;
import dtalge.io.internal.XmlDocument;
import dtalge.util.DtDataTypes;
import dtalge.util.Validations;
import dtalge.util.internal.XmlErrors;

/**
 * データ代数の元(<code>{@link Dtalge}</code>)の集合を保持するクラス。
 * 
 * <p>データ代数の元(<code>{@link Dtalge}</code>)のインスタンスの集合であり、
 * 同じ値、もしくは同じインスタンスを格納できる。
 * <br>
 * このクラスは、{@link java.util.ArrayList} の実装となる。
 * したがって、挿入メソッド、ファイル入出力において、
 * クラス内での要素の順序は基本的に維持される。
 * <p>
 * このクラスでは、<tt>null</tt> を許容しない。
 * <br>また、<b>この実装は同期化されない</b>。
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
 * <code>DtAlgeSet</code> は、CSV ファイル、XML ファイル形式の入出力インタフェースを
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
 * また、CSV 形式、XML 形式のファイル入力時、1 つのデータ代数元においてファイル内に同一の基底が複数存在する場合、
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
 * <li>空白行(行の先頭で改行されている行)は、データ代数の元の区切りとする。
 * つまり、空白行と空白行の間の要素すべてが 1 つのデータ代数元({@link Dtalge})インスタンスに相当する。
 * なお、連続した空白行は、1 つのデータ代数元区切り(空白行)とみなす。
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
public class DtAlgeSet extends ArrayList<Dtalge> implements IDataOutput
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	protected static final Dtalge emptyAlge = new Dtalge();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * デフォルトの初期容量で、空の集合を生成する。
	 * 
	 * @see java.util.ArrayList#ArrayList()
	 */
	public DtAlgeSet() {
		super();
	}

	/**
	 * 指定された初期容量で空の集合を生成する。
	 * 
	 * @param initialCapacity	集合の初期容量
	 * 
	 * @throws IllegalArgumentException 指定された初期容量が負の場合
	 * 
	 * @see java.util.ArrayList#ArrayList(int)
	 */
	public DtAlgeSet(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * 指定されたコレクションの要素を含む集合を生成する。
	 * <br>
	 * これらの要素は、コレクションの反復子が返す順序で格納される。
	 * <br>
	 * コレクションに含まれる <tt>null</tt> 要素は無視される。
	 * 
	 * @param c	要素がリストに配置されるコレクション
	 * 
	 * @throws NullPointerException 指定されたコレクションが <tt>null</tt> の場合
	 * 
	 * @see java.util.ArrayList#ArrayList(Collection)
	 */
	public DtAlgeSet(Collection<? extends Dtalge> c) {
		super((int)Math.min((c.size()*100L)/100, Integer.MAX_VALUE));
		addAll(c);
	}

	/**
	 * 指定されたコレクションの要素を含む集合を生成する。
	 * <p>
	 * このコンストラクタは、Set の内容から単純に List を生成する
	 * ためのものであり、値のチェックは行わない。
	 * ArrayList の実装において、コレクションを引数にとるコンストラクタの
	 * 方が、良いので。
	 * 
	 * @param set	要素を持つ Set
	 * @param dummy	この引数は無視される。
	 */
	private DtAlgeSet(Set<Dtalge> set, boolean dummy) {
		super(set);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * データ代数集合を連結した、新しいデータ代数集合のインスタンスを返す。
	 * <br>
	 * 新しいデータ代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(データ代数元)の末尾に、指定された集合の要素を追加した集合となる。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	この集合と連結するデータ代数集合
	 * @return		この集合と指定された集合を連結した新しいデータ代数集合
	 * 
	 * @throws NullPointerException	指定された集合が <tt>null</tt> の場合
	 */
	public DtAlgeSet addition(DtAlgeSet set) {
		DtAlgeSet resultSet = newInstance(this.size() + set.size());
		resultSet.addAll(this);
		resultSet.addAll(set);
		return resultSet;
	}

	/**
	 * この集合から指定されたデータ代数集合の要素を除いた、新しいデータ代数集合の
	 * インスタンスを返す。
	 * <br>
	 * 新しいデータ代数集合のインスタンスには、この集合に含まれる要素(データ代数元)
	 * から、指定された集合の要素と等しいものを除いた集合となる。
	 * 同値の要素とみなすのは、要素の {@link dtalge.Dtalge#equals(Object)} メソッドが
	 * <tt>true</tt> を返す場合とする。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	この集合から取り除く要素を持つデータ代数集合
	 * @return	この集合から指定された集合の要素を除いた、新しいデータ代数集合
	 * 
	 * @throws NullPointerException	指定された集合が <tt>null</tt> の場合
	 */
	public DtAlgeSet subtraction(DtAlgeSet set) {
		DtAlgeSet resultSet = cloneInstance();
		resultSet.removeAll(Validations.validNotNull(set));
		return resultSet;
	}

	/**
	 * この集合から指定されたデータ代数集合の要素以外を除いた、新しいデータ代数集合の
	 * インスタンスを返す。
	 * <br>
	 * 新しいデータ代数集合のインスタンスには、この集合に含まれる要素(データ代数元)
	 * から、指定された集合に含まれない要素を除いた集合となる。
	 * 同値ではない要素とみなすのは、要素の {@link dtalge.Dtalge#equals(Object)} メソッドが
	 * <tt>false</tt> を返す場合とする。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	この集合において維持する要素を持つデータ代数集合
	 * @return この集合から指定された集合の要素以外を除いた、新しいデータ代数集合
	 * 
	 * @throws NullPointerException	指定された集合が <tt>null</tt> の場合
	 */
	public DtAlgeSet retention(DtAlgeSet set) {
		DtAlgeSet resultSet = cloneInstance();
		resultSet.retainAll(Validations.validNotNull(set));
		return resultSet;
	}

	/**
	 * 指定されたデータ代数集合との和を返す。
	 * <p>
	 * データ代数集合の和演算(this ∪ set)の結果は、次のようになる。
	 * <ul>
	 * <li>この集合と指定された集合の全ての要素を持つ。
	 * <li>同値の要素は含まれない(重複する要素は取り除かれる)。
	 * <li>要素をもたないデータ代数元は含まれない。
	 * </ul>
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	和をとるデータ代数集合の一方
	 * @return	2 つのデータ代数集合の和集合となるインスタンス
	 * 
	 * @throws NullPointerException 指定された集合が <tt>null</tt> の場合
	 */
	public DtAlgeSet union(DtAlgeSet set) {
		// union となる Set を生成
		Set<Dtalge> resultSet = new LinkedHashSet<Dtalge>(this.size()+set.size());
		resultSet.addAll(this);
		resultSet.addAll(set);
		// 要素が空の元を削除
		resultSet.remove(emptyAlge);
		// 念のため、null も削除
		resultSet.remove(null);
		// 生成された Set から、集合を生成
		return newInstance(resultSet);
	}

	/**
	 * 指定されたデータ代数集合との積(共通部分)を返す。
	 * <p>
	 * データ代数集合の積演算(this ∩ set)の結果は、次のようになる。
	 * <ul>
	 * <li>この集合と指定された集合の両方に含まれる要素のみを持つ。
	 * <li>同値の要素は含まれない(重複する要素は取り除かれる)。
	 * <li>要素をもたないデータ代数元は含まれない。
	 * </ul>
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	積をとるデータ代数集合の一方
	 * @return	2 つのデータ代数集合の積(共通)集合となるインスタンス
	 * 
	 * @throws NullPointerException	指定された集合が <tt>null</tt> の場合
	 */
	public DtAlgeSet intersection(DtAlgeSet set) {
		// どちらかの集合が空集合なら、空集合を返す
		if (set.isEmpty() || this.isEmpty()) {
			return newInstance(0);
		}
		// intersection 用Set を生成
		Set<Dtalge> resultSet = new LinkedHashSet<Dtalge>(this);
		Set<Dtalge> retainSet = new LinkedHashSet<Dtalge>(set);
		// 共通部分以外を削除
		resultSet.retainAll(retainSet);
		// 要素が空の元を削除
		resultSet.remove(emptyAlge);
		// 念のため、null も削除
		resultSet.remove(null);
		// 生成された Set から、集合を生成
		return newInstance(resultSet);
	}

	/**
	 * 指定されたデータ集合との差を返す。
	 * <p>
	 * データ代数集合の差演算(this - set)の結果は、次のようになる。
	 * <ul>
	 * <li>この集合から、指定された集合の要素以外の要素のみを持つ。
	 * <li>同値の要素は含まれない(重複する要素は取り除かれる)。
	 * <li>要素をもたないデータ代数元は含まれない。
	 * <li>
	 * </ul>
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	差し引くデータ代数集合
	 * @return	指定されたデータ代数集合を差し引いた結果となるインスタンス
	 * 
	 * @throws NullPointerException	指定された集合が <tt>null</tt> の場合
	 */
	public DtAlgeSet difference(DtAlgeSet set) {
		Validations.validNotNull(set);
		// 自身が空集合なら、空集合を返す
		if (this.isEmpty()) {
			return newInstance(0);
		}
		// difference 用 Set を生成
		Set<Dtalge> resultSet = new LinkedHashSet<Dtalge>(this);
		Set<Dtalge> removeSet = new LinkedHashSet<Dtalge>(set);
		// 指定された要素を除外
		resultSet.removeAll(removeSet);
		// 要素が空の元を削除
		resultSet.remove(emptyAlge);
		// 念のため、null も削除
		resultSet.remove(null);
		// 生成された Set から、集合を生成
		return newInstance(resultSet);
	}

	/**
	 * データ代数集合に含まれる全ての基底を取り出す。
	 * <br>
	 * このメソッドが返すデータ代数基底集合に、基底の重複はない。
	 * 
	 * @return	データ代数集合から取り出した基底集合
	 */
	public DtBaseSet getBases() {
		DtBaseSet bases = new DtBaseSet();
		for (Dtalge alge : this) {
			if (!alge.isEmpty()) {
				bases.addAll(alge.data.keySet());
			}
		}
		return bases;
	}

	
	/**
	 * このデータ代数集合に含まれる基底のうち、指定されたパターンに一致する
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
		
		// 要素が空なら、空集合
		if (this.isEmpty()) {
			return new DtBaseSet(0);
		}
		
		// マッチング
		DtBaseSet matchedBases = new DtBaseSet();
		DtBaseSet unmatchedBases = new DtBaseSet();
		for (Dtalge alge : this) {
			if (!alge.isEmpty()) {
				alge.updateMatchedBases(matchedBases, unmatchedBases, pattern);
			}
		}
		
		// 完了
		return matchedBases;
	}
	
	/**
	 * このデータ代数集合に含まれる基底のうち、指定されたパターンに一致する
	 * 基底のみを格納する基底集合を返す。
	 * <br>
	 * このメソッドが返す基底集合には、指定された基底パターン集合に含まれる
	 * 基底パターンのどれかに一致した基底が含まれる。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、もしくは、指定された基底
	 * パターン集合が空の場合、このメソッドは空の基底集合を返す。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return	パターンに一致した基底のみの集合
	 * 
	 * @throws NullPointerException	指定された基底パターン集合が <tt>null</tt> の場合
	 */
	public DtBaseSet getMatchedBases(DtBasePatternSet patterns) {
		Validations.validNotNull(patterns);
		
		// 要素が空、もしくは、パターン集合が空なら、空集合
		if (this.isEmpty() || patterns.isEmpty()) {
			return new DtBaseSet(0);
		}
		
		// マッチング
		DtBaseSet matchedBases = new DtBaseSet();
		DtBaseSet unmatchedBases = new DtBaseSet();
		for (Dtalge alge : this) {
			if (!alge.isEmpty()) {
				alge.updateMatchedBases(matchedBases, unmatchedBases, patterns);
			}
		}
		
		// 完了
		return matchedBases;
	}

	/**
	 * 自身に含まれるデータ代数元をすべて結合する。<br>
	 * このメソッドでは、全てのデータ代数元を、一つのデータ代数元に結合する。
	 * 同じ基底が異なるデータ代数元に存在する場合、内部的な結合の順序で上書きされる。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * この結合では、同じ基底の値は上書きされる。数値的な加算等の演算は行わない。
	 * </blockquote>
	 * 
	 * @return 結合結果の、新しいデータ代数元
	 * @since 0.30
	 */
	public Dtalge sum() {
		return Dtalge.sum(this);
	}

	/**
	 * 指定された基底に割り当てられた全ての値が格納された新しい文字列リストを返す。
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての値を格納する文字列リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の文字列リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が文字列型ではない場合
	 * @since 0.30
	 */
	public List<String> toStringList(DtBase base) {
		if (!DtDataTypes.STRING.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not String.");
		
		List<String> list = new ArrayList<String>(this.size());
		for (Dtalge alge : this) {
			if (alge.containsBase(base)) {
				list.add(alge.getString(base));
			}
		}
		
		return list;
	}
	
	/**
	 * 指定された基底に割り当てられた全ての重複しない(同値ではない)値が格納された新しい文字列リストを返す。
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての重複しない(同値ではない)値を格納する文字列リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の文字列リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が文字列型ではない場合
	 * @since 0.30
	 */
	public List<String> toDistinctStringList(DtBase base) {
		if (!DtDataTypes.STRING.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not String.");
		
		Set<String> set = new LinkedHashSet<String>();
		for (Dtalge alge : this) {
			if (alge.containsBase(base)) {
				set.add(alge.getString(base));
			}
		}
		
		return new ArrayList<String>(set);
	}
	
	/**
	 * 指定された基底に割り当てられた全ての値が格納された新しい実数値リストを返す。
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての値を格納する実数値リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の実数値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が実数型ではない場合
	 * @since 0.30
	 */
	public List<BigDecimal> toDecimalList(DtBase base) {
		if (!DtDataTypes.DECIMAL.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not Decimal.");
		
		List<BigDecimal> list = new ArrayList<BigDecimal>(this.size());
		for (Dtalge alge : this) {
			if (alge.containsBase(base)) {
				list.add(alge.getDecimal(base));
			}
		}
		
		return list;
	}
	
	/**
	 * 指定された基底に割り当てられた全ての重複しない(同値ではない)値が格納された新しい実数値リストを返す。
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての重複しない(同値ではない)値を格納する実数値リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の実数値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が実数型ではない場合
	 * @since 0.30
	 */
	public List<BigDecimal> toDistinctDecimalList(DtBase base) {
		if (!DtDataTypes.DECIMAL.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not Decimal.");

		List<BigDecimal> list = new ArrayList<BigDecimal>(this.size());
		Set<BigDecimal> set = new HashSet<BigDecimal>();
		for (Dtalge alge : this) {
			if (alge.containsBase(base)) {
				BigDecimal v = alge.getDecimal(base);
				BigDecimal stripped = (v==null ? v : v.stripTrailingZeros());
				if (!set.contains(stripped)) {
					set.add(stripped);
					list.add(v);
				}
			}
		}
		
		set = null;
		return list;
	}
	
	/**
	 * 指定された基底に割り当てられた全ての値が格納された新しい真偽値リストを返す。
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての値を格納する真偽値リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の真偽値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が真偽値型ではない場合
	 * @since 0.30
	 */
	public List<Boolean> toBooleanList(DtBase base) {
		if (!DtDataTypes.BOOLEAN.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not Boolean.");
		
		List<Boolean> list = new ArrayList<Boolean>(this.size());
		for (Dtalge alge : this) {
			if (alge.containsBase(base)) {
				list.add(alge.getBoolean(base));
			}
		}
		
		return list;
	}
	
	/**
	 * 指定された基底に割り当てられた全ての重複しない(同値ではない)値が格納された新しい真偽値リストを返す。
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての重複しない(同値ではない)値を格納する真偽値リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の真偽値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が真偽値型ではない場合
	 * @since 0.30
	 */
	public List<Boolean> toDistinctBooleanList(DtBase base) {
		if (!DtDataTypes.BOOLEAN.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not Boolean.");
		
		Set<Boolean> set = new LinkedHashSet<Boolean>();
		for (Dtalge alge : this) {
			if (alge.containsBase(base)) {
				set.add(alge.getBoolean(base));
			}
		}
		
		return new ArrayList<Boolean>(set);
	}

	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最小値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最小値を返す。
	 * 			最小値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 0.30
	 */
	public Object minValue(DtBase base) {
		if (base == null) throw new NullPointerException();
		return getMinValue(base);
	}
	
	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最大値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最大値を返す。
	 * 			最大値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 0.30
	 */
	public Object maxValue(DtBase base) {
		if (base == null) throw new NullPointerException();
		return getMaxValue(base);
	}
	
	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最小の実数値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * 指定された基底が実数型のデータ型ではない場合、このメソッドは例外をスローする。
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最小値を返す。
	 * 			最小値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が実数型ではない場合
	 * @since 0.30
	 */
	public BigDecimal minDecimal(DtBase base) {
		if (!DtDataTypes.DECIMAL.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not Decimal.");
		return (BigDecimal)getMinValue(base);
	}
	
	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最大の実数値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * 指定された基底が実数型のデータ型ではない場合、このメソッドは例外をスローする。
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最大値を返す。
	 * 			最大値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が実数型ではない場合
	 * @since 0.30
	 */
	public BigDecimal maxDecimal(DtBase base) {
		if (!DtDataTypes.DECIMAL.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not Decimal.");
		return (BigDecimal)getMaxValue(base);
	}

	/**
	 * 指定された基底に、指定された値が関連付けられているデータ代数元が存在するかを判定する。
	 * <em>base</em> に <tt>null</tt> が指定された場合、このメソッドは <tt>false</tt> を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 0.30
	 */
	public boolean containsValue(DtBase base, Object value) {
		if (base == null)
			return false;
		if (isEmpty())
			return false;

		if (value instanceof BigDecimal) {
			BigDecimal dvalue = (BigDecimal)value;
			for (Dtalge alge : this) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (dvalue==algevalue || ((algevalue instanceof BigDecimal) && 0==dvalue.compareTo((BigDecimal)algevalue))) {
						return true;
					}
				}
			}
			
		} else {
			for (Dtalge alge : this) {
				if (alge.containsBase(base)) {
					if (Dtalge.objectEquals(value, alge.get(base))) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	/**
	 * 指定された基底に、指定されたコレクションに含まれる値のどれか一つが関連付けられている
	 * データ代数元が存在するかを判定する。
	 * <em>base</em> に <tt>null</tt> が指定された場合、このメソッドは <tt>false</tt> を返す。
	 * @param base		データ代数基底
	 * @param values	判定する値を格納するコレクション
	 * @return			存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 0.30
	 */
	public boolean containsAnyValues(DtBase base, Collection<?> values) {
		if (base == null)
			return false;
		if (values==null || values.isEmpty())
			return false;
		if (isEmpty())
			return false;
		
		for (Dtalge alge : this) {
			if (alge.containsBase(base)) {
				Object algevalue = alge.get(base);
				if (algevalue instanceof BigDecimal) {
					BigDecimal dalgevalue = (BigDecimal)algevalue;
					for (Object val : values) {
						if (dalgevalue==val || ((val instanceof BigDecimal) && 0==dalgevalue.compareTo((BigDecimal)val))) {
							return true;
						}
					}
				} else {
					if (values.contains(algevalue)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	/**
	 * 自身に含まれる全てのデータ代数元に対し、
	 * {@link dtalge.Dtalge#oneValueProjection(Object)} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link dtalge.Dtalge#oneValueProjection(Object)} した結果となる
	 * データ代数元を持つ、新しいデータ代数集合を生成する。
	 * このとき、要素が空となるデータ代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @param value	取り出すようその値
	 * @return	指定された値と等しい要素のみを含むデータ代数元の集合
	 * 
	 * @see dtalge.Dtalge#oneValueProjection(Object)
	 * 
	 * @since 0.20
	 */
	public DtAlgeSet oneValueProjection(Object value) {
		// 要素がなければ、空集合を返す
		if (this.isEmpty()) {
			return newInstance(0);
		}
		
		// 全要素をプロジェクション
		DtAlgeSet newSet = newInstance(this.size());
		for (Dtalge alge : this) {
			if (!alge.isEmpty()) {
				Dtalge palge = alge.oneValueProjection(value);
				if (!palge.isEmpty()) {
					newSet.fastAdd(palge);	// 空ではない要素のみ
				}
			}
		}
		return newSet;
	}

	/**
	 * 自身に含まれる全てのデータ代数元に対し、
	 * {@link dtalge.Dtalge#valuesProjection(Collection)} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link dtalge.Dtalge#valuesProjection(Collection)} した結果となる
	 * データ代数元を持つ、新しいデータ代数集合を生成する。
	 * このとき、要素が空となるデータ代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @param values	取り出す要素の値のコレクション
	 * @return	指定されたコレクションに含まれる値と等しい要素のみを含む
	 * 			データ代数元の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @see dtalge.Dtalge#valuesProjection(Collection)
	 * 
	 * @since 0.20
	 */
	public DtAlgeSet valuesProjection(Collection<?> values) {
		// コレクションの要素がなければ、空集合を返す
		if (values.isEmpty()) {
			return newInstance(0);
		}
		
		// 要素がなければ、空集合を返す
		if (this.isEmpty()) {
			return newInstance(0);
		}
		
		// 全要素をプロジェクション
		DtAlgeSet newSet = newInstance(this.size());
		for (Dtalge alge : this) {
			if (!alge.isEmpty()) {
				Dtalge palge = alge.valuesProjection(values);
				if (!palge.isEmpty()) {
					newSet.fastAdd(palge);	// 空ではない要素のみ
				}
			}
		}
		return newSet;
	}

	/**
	 * 自身に含まれる全てのデータ代数元に対し、
	 * {@link dtalge.Dtalge#nullProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link dtalge.Dtalge#nullProjection()} した結果となる
	 * データ代数元を持つ、新しいデータ代数集合を生成する。
	 * このとき、要素が空となるデータ代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が <tt>null</tt> の要素のみを含むデータ代数元の集合
	 * 
	 * @see dtalge.Dtalge#nullProjection()
	 * 
	 * @since 0.20
	 */
	public DtAlgeSet nullProjection() {
		// 要素がなければ、空集合を返す
		if (this.isEmpty()) {
			return newInstance(0);
		}
		
		// 全要素をプロジェクション
		DtAlgeSet newSet = newInstance(this.size());
		for (Dtalge alge : this) {
			if (!alge.isEmpty()) {
				Dtalge palge = alge.nullProjection();
				if (!palge.isEmpty()) {
					newSet.fastAdd(palge);	// 空ではない要素のみ
				}
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれる全てのデータ代数元に対し、
	 * {@link dtalge.Dtalge#nonullProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link dtalge.Dtalge#nonullProjection()} した結果となる
	 * データ代数元を持つ、新しいデータ代数集合を生成する。
	 * このとき、要素が空となるデータ代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が <tt>null</tt> ではない要素のみを含むデータ代数元の集合
	 * 
	 * @see dtalge.Dtalge#nonullProjection()
	 * 
	 * @since 0.20
	 */
	public DtAlgeSet nonullProjection() {
		// 要素がなければ、空集合を返す
		if (this.isEmpty()) {
			return newInstance(0);
		}
		
		// 全要素をプロジェクション
		DtAlgeSet newSet = newInstance(this.size());
		for (Dtalge alge : this) {
			if (!alge.isEmpty()) {
				Dtalge palge = alge.nonullProjection();
				if (!palge.isEmpty()) {
					newSet.fastAdd(palge);	// 空ではない要素のみ
				}
			}
		}
		return newSet;
	}

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底で
	 * プロジェクションした結果を保持する集合を返す。
	 * <p>このメソッドは、この集合に含まれる全要素についてプロジェクション
	 * した結果となるデータ代数元を格納する、新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param base	取り出すデータ代数基底
	 * @return	取り出された要素を持つデータ代数元の集合
	 * 
	 * @throws NullPointerException 指定された基底が <tt>null</tt> の場合
	 * 
	 * @see dtalge.Dtalge#projection(DtBase)
	 */
	public DtAlgeSet projection(DtBase base) {
		Validations.validNotNull(base);
		
		// 要素がなければ、空集合を返す
		if (this.isEmpty()) {
			return newInstance(0);
		}
		
		// 全要素をプロジェクション
		DtAlgeSet newSet = newInstance(this.size());
		for (Dtalge alge : this) {
			if (!alge.isEmpty()) {
				Dtalge palge = alge.projection(base);
				if (!palge.isEmpty()) {
					newSet.fastAdd(palge);	// 空ではない要素のみ
				}
			}
		}
		return newSet;
	}

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底集合で
	 * プロジェクションした結果を保持する集合を返す。
	 * <p>このメソッドは、この集合に含まれる全要素についてプロジェクション
	 * した結果となるデータ代数元を格納する、新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param bases	取り出すデータ代数基底の集合
	 * @return	取り出された要素を持つデータ代数元の集合
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 * 
	 * @see dtalge.Dtalge#projection(DtBaseSet)
	 */
	public DtAlgeSet projection(DtBaseSet bases) {
		Validations.validNotNull(bases);
		
		// 要素がなければ、空集合を返す
		if (this.isEmpty() || bases.isEmpty()) {
			return newInstance(0);
		}
		
		// 全要素をプロジェクション
		DtAlgeSet newSet = newInstance(this.size());
		for (Dtalge alge : this) {
			if (!alge.isEmpty()) {
				Dtalge palge = alge.projection(bases);
				if (!palge.isEmpty()) {
					newSet.fastAdd(palge);	// 空ではない要素のみ
				}
			}
		}
		return newSet;
	}

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底パターンに
	 * よってプロジェクションした結果を保持する集合を返す。
	 * <p>
	 * このメソッドは、この集合に含まれる全要素について、指定された基底パターンに
	 * 一致する基底のみでプロジェクションした結果となるデータ代数元を格納する、
	 * 新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param pattern	基底パターン
	 * @return	基底パターンでプロジェクションした結果となるデータ代数元の集合
	 * 
	 * @throws	NullPointerException	指定された基底パターンが <tt>null</tt> の場合
	 * 
	 * @see dtalge.Dtalge#patternProjection(DtBasePattern)
	 */
	public DtAlgeSet patternProjection(DtBasePattern pattern) {
		Validations.validNotNull(pattern);
		
		// 要素が存在しなければ、空集合を返す
		if (this.isEmpty()) {
			return newInstance(0);
		}
		
		// 全要素をプロジェクション
		DtAlgeSet newSet = newInstance(this.size());
		for (Dtalge alge : this) {
			if (!alge.isEmpty()) {
				Dtalge palge = alge.patternProjection(pattern);
				if (!palge.isEmpty()) {
					newSet.fastAdd(palge);	// 空ではない要素のみ
				}
			}
		}
		return newSet;
	}

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底パターンに
	 * よってプロジェクションした結果を保持する集合を返す。
	 * <p>
	 * このメソッドは、この集合に含まれる全要素について、指定された基底パターン
	 * のいずれかに一致する基底のみでプロジェクションした結果となるデータ代数元を
	 * 格納する、新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return	基底パターンでプロジェクションした結果となるデータ代数元の集合
	 * 
	 * @throws NullPointerException	指定された基底パターン集合が <tt>null</tt> の場合
	 * 
	 * @see dtalge.Dtalge#patternProjection(DtBasePatternSet)
	 */
	public DtAlgeSet patternProjection(DtBasePatternSet patterns) {
		Validations.validNotNull(patterns);
		
		// 要素が存在しなければ、空集合を返す
		if (this.isEmpty() || patterns.isEmpty()) {
			return newInstance(0);
		}
		
		// 全要素をプロジェクション
		DtAlgeSet newSet = newInstance(this.size());
		for (Dtalge alge : this) {
			if (!alge.isEmpty()) {
				Dtalge palge = alge.patternProjection(patterns);
				if (!palge.isEmpty()) {
					newSet.fastAdd(palge);	// 空ではない要素のみ
				}
			}
		}
		return newSet;
	}

	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含むデータ代数元のみを取り出す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @since 0.30
	 */
	public DtAlgeSet selectEqualValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (this.isEmpty())
			return new DtAlgeSet(0);
		
		DtAlgeSet retset = new DtAlgeSet(this.size());
		if (value instanceof BigDecimal) {
			BigDecimal dvalue = (BigDecimal)value;
			for (Dtalge alge : this) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (dvalue==algevalue || ((algevalue instanceof BigDecimal) && 0==dvalue.compareTo((BigDecimal)algevalue))) {
						retset.fastAdd(alge);
					}
				}
			}
		} else {
			for (Dtalge alge : this) {
				if (alge.containsBase(base)) {
					if (Dtalge.objectEquals(value, alge.get(base))) {
						retset.fastAdd(alge);
					}
				}
			}
		}
		
		return retset;
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含むデータ代数元以外の
	 * データ代数元を全て取り出す。このメソッドが返すデータ代数集合には、指定された基底が存在しない
	 * データ代数元も含まれる。
	 * <p>この動作は、{@link #removeEqualValue(DtBase, Object)} と同じ。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @see #removeEqualValue(DtBase, Object)
	 * @since 0.30
	 */
	public DtAlgeSet selectNotEqualValue(DtBase base, Object value) {
		return removeEqualValue(base, value);
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値よりも小さい要素を含むデータ代数元のみを取り出す。
	 * 指定された値と比較不可能な要素は、結果には含まれない。また、<em>value</em> に
	 * <tt>null</tt> を指定した場合、このメソッドは要素が空のデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @since 0.30
	 */
	@SuppressWarnings("unchecked")
	public DtAlgeSet selectLessThanValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (value == null || !(value instanceof Comparable))
			return new DtAlgeSet(0);	// 比較不可能
		if (this.isEmpty())
			return new DtAlgeSet(0);
		try {
			DtDataTypes.validDataType(base.getTypeKey(), value);
		} catch (Throwable ex) {
			// cannot comparable
			return new DtAlgeSet(0);
		}

		int cmp;
		DtAlgeSet retset = new DtAlgeSet(this.size());
		for (Dtalge alge : this) {
			if (alge.containsBase(base)) {
				Object algevalue = alge.get(base);
				if (algevalue instanceof Comparable) {
					try {
						cmp = ((Comparable)algevalue).compareTo(value);
					} catch (Throwable ex) {
						// ignore exception
						cmp = 1;
					}
					if (cmp < 0) {
						// algevalue < value
						retset.fastAdd(alge);
					}
				}
			}
		}
		
		return retset;
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値よりも小さい、もしくは等しい要素を含む
	 * データ代数元のみを取り出す。
	 * 指定された値と比較不可能な要素は、結果には含まれない。また、<em>value</em> に
	 * <tt>null</tt> を指定した場合、このメソッドは要素が空のデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @since 0.30
	 */
	@SuppressWarnings("unchecked")
	public DtAlgeSet selectLessEqualValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (value == null || !(value instanceof Comparable))
			return new DtAlgeSet(0);	// 比較不可能
		if (this.isEmpty())
			return new DtAlgeSet(0);
		try {
			DtDataTypes.validDataType(base.getTypeKey(), value);
		} catch (Throwable ex) {
			// cannot comparable
			return new DtAlgeSet(0);
		}
		
		int cmp;
		DtAlgeSet retset = new DtAlgeSet(this.size());
		for (Dtalge alge : this) {
			if (alge.containsBase(base)) {
				Object algevalue = alge.get(base);
				if (algevalue instanceof Comparable) {
					try {
						cmp = ((Comparable)algevalue).compareTo(value);
					} catch (Throwable ex) {
						// ignore exception
						cmp = 1;
					}
					if (cmp <= 0) {
						// algevalue <= value
						retset.fastAdd(alge);
					}
				}
			}
		}
		
		return retset;
	}
	
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値よりも大きい要素を含むデータ代数元のみを取り出す。
	 * 指定された値と比較不可能な要素は、結果には含まれない。また、<em>value</em> に
	 * <tt>null</tt> を指定した場合、このメソッドは要素が空のデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @since 0.30
	 */
	@SuppressWarnings("unchecked")
	public DtAlgeSet selectGreaterThanValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (value == null || !(value instanceof Comparable))
			return new DtAlgeSet(0);	// 比較不可能
		if (this.isEmpty())
			return new DtAlgeSet(0);
		try {
			DtDataTypes.validDataType(base.getTypeKey(), value);
		} catch (Throwable ex) {
			// cannot comparable
			return new DtAlgeSet(0);
		}
		
		int cmp;
		DtAlgeSet retset = new DtAlgeSet(this.size());
		for (Dtalge alge : this) {
			if (alge.containsBase(base)) {
				Object algevalue = alge.get(base);
				if (algevalue instanceof Comparable) {
					try {
						cmp = ((Comparable)algevalue).compareTo(value);
					} catch (Throwable ex) {
						// ignore exception
						cmp = -1;
					}
					if (cmp > 0) {
						// algevalue > value
						retset.fastAdd(alge);
					}
				}
			}
		}
		
		return retset;
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値よりも大きい、もしくは等しい要素を含む
	 * データ代数元のみを取り出す。
	 * 指定された値と比較不可能な要素は、結果には含まれない。また、<em>value</em> に
	 * <tt>null</tt> を指定した場合、このメソッドは要素が空のデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @since 0.30
	 */
	@SuppressWarnings("unchecked")
	public DtAlgeSet selectGreaterEqualValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (value == null || !(value instanceof Comparable))
			return new DtAlgeSet(0);	// 比較不可能
		if (this.isEmpty())
			return new DtAlgeSet(0);
		try {
			DtDataTypes.validDataType(base.getTypeKey(), value);
		} catch (Throwable ex) {
			// cannot comparable
			return new DtAlgeSet(0);
		}
		
		int cmp;
		DtAlgeSet retset = new DtAlgeSet(this.size());
		for (Dtalge alge : this) {
			if (alge.containsBase(base)) {
				Object algevalue = alge.get(base);
				if (algevalue instanceof Comparable) {
					try {
						cmp = ((Comparable)algevalue).compareTo(value);
					} catch (Throwable ex) {
						// ignore exception
						cmp = -1;
					}
					if (cmp >= 0) {
						// algevalue >= value
						retset.fastAdd(alge);
					}
				}
			}
		}
		
		return retset;
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を、
	 * 指定されたデータ代数元で置き換えられた新しいデータ代数集合を返す。
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @param newAlge	新しいデータ代数元
	 * @return	条件に一致したデータ代数元が置き換えられた、新しいデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em>、もしくは <em>newAlge</em> が <tt>null</tt> の場合
	 * @since 0.30
	 */
	public DtAlgeSet replaceEqualValue(DtBase base, Object value, Dtalge newAlge) {
		if (base == null || newAlge == null) throw new NullPointerException();
		if (this.isEmpty())
			return new DtAlgeSet(0);
		
		DtAlgeSet retset = new DtAlgeSet(this.size());
		if (value instanceof BigDecimal) {
			BigDecimal dvalue = (BigDecimal)value;
			for (Dtalge alge : this) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (dvalue==algevalue || ((algevalue instanceof BigDecimal) && 0==dvalue.compareTo((BigDecimal)algevalue))) {
						alge = newAlge;
					}
				}
				retset.fastAdd(alge);
			}
		} else {
			for (Dtalge alge : this) {
				if (alge.containsBase(base)) {
					if (Dtalge.objectEquals(value, alge.get(base))) {
						alge = newAlge;
					}
				}
				retset.fastAdd(alge);
			}
		}
		
		return retset;
	}
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を、
	 * 指定されたデータ代数元で置き換える。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、自身の内容を書き換える、破壊型メソッドである。
	 * </blockquote>
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @param newAlge	新しいデータ代数元
	 * @return	自身の内容が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>base</em>、もしくは <em>newAlge</em> が <tt>null</tt> の場合
	 * @since 0.30
	 */
	public boolean updateEqualValue(DtBase base, Object value, Dtalge newAlge) {
		if (base == null || newAlge == null) throw new NullPointerException();
		if (this.isEmpty())
			return false;

		int len = this.size();
		boolean modified = false;
		if (value instanceof BigDecimal) {
			BigDecimal dvalue = (BigDecimal)value;
			for (int i = 0; i < len; i++) {
				Dtalge alge = this.get(i);
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (dvalue==algevalue || ((algevalue instanceof BigDecimal) && 0==dvalue.compareTo((BigDecimal)algevalue))) {
						this.set(i, newAlge);
						modified = true;
					}
				}
			}
		} else {
			for (int i = 0; i < len; i++) {
				Dtalge alge = this.get(i);
				if (alge.containsBase(base)) {
					if (Dtalge.objectEquals(value, alge.get(base))) {
						this.set(i, newAlge);
						modified = true;
					}
				}
			}
		}
		
		return modified;
	}

	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を
	 * 取り除いた、新しいデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元を含まない、新しいデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @since 0.30
	 */
	public DtAlgeSet removeEqualValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (this.isEmpty())
			return new DtAlgeSet(0);
		
		DtAlgeSet retset = new DtAlgeSet(this.size());
		if (value instanceof BigDecimal) {
			BigDecimal dvalue = (BigDecimal)value;
			for (Dtalge alge : this) {
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (dvalue==algevalue || ((algevalue instanceof BigDecimal) && 0==dvalue.compareTo((BigDecimal)algevalue))) {
						continue;
					}
				}
				retset.fastAdd(alge);
			}
		} else {
			for (Dtalge alge : this) {
				if (alge.containsBase(base)) {
					if (Dtalge.objectEquals(value, alge.get(base))) {
						continue;
					}
				}
				retset.fastAdd(alge);
			}
		}
		
		return retset;
	}

	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を
	 * 削除する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、自身の内容を書き換える、破壊型メソッドである。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	自身の内容が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @since 0.30
	 */
	public boolean deleteEqualValue(DtBase base, Object value) {
		if (base == null) throw new NullPointerException();
		if (this.isEmpty())
			return false;

		boolean modified = false;
		if (value instanceof BigDecimal) {
			BigDecimal dvalue = (BigDecimal)value;
			for (int i = this.size() - 1; i >= 0; i--) {
				Dtalge alge = this.get(i);
				if (alge.containsBase(base)) {
					Object algevalue = alge.get(base);
					if (dvalue==algevalue || ((algevalue instanceof BigDecimal) && 0==dvalue.compareTo((BigDecimal)algevalue))) {
						this.remove(i);
						modified = true;
					}
				}
			}
		} else {
			for (int i = this.size() - 1; i >= 0; i--) {
				Dtalge alge = this.get(i);
				if (alge.containsBase(base)) {
					if (Dtalge.objectEquals(value, alge.get(base))) {
						this.remove(i);
						modified = true;
					}
				}
			}
		}
		
		return modified;
	}

	/**
	 * 指定された基底に関連付けられている値のうち、指定されたシソーラス定義において
	 * 比較不可能な極大値のみを含むデータ代数元を取り出す。
	 * <p>このメソッドは、指定された基底に関連付けられている値をすべて比較し、
	 * シソーラス定義において比較可能な値の中から最大値のみを含むデータ代数元を取り出す。
	 * 取り出されたデータ代数元に含まれる値は、それぞれ比較不可能もしくは同値となっている。
	 * @param base	判定する値が関連付けられているデータ代数基底
	 * @param thes	シソーラス定義
	 * @return	比較不可能な極大値のみを含むデータ代数元を格納する、新しいデータ代数集合を返す。
	 * 			比較不可能な極大値が存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>base</em> が文字列型のデータ代数基底ではない場合
	 * @since 0.30
	 */
	public DtAlgeSet selectThesaurusMax(DtBase base, DtStringThesaurus thes) {
		// check
		if (base == null)
			throw new NullPointerException("DtBase argument is null.");
		if (thes == null)
			throw new NullPointerException("DtThesaurus object is null.");
		if (!DtDataTypes.STRING.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not String.");
		
		if (thes.isEmpty() || this.isEmpty()) {
			//--- No thesaurus relations or No alge elements
			return new DtAlgeSet();
		}
		// 極大値の元のみを取り出す
		Set<Integer> maxindices = new TreeSet<Integer>();
		Map<String, Set<Integer>> wordmap = new HashMap<String, Set<Integer>>();
		int len = this.size();
		for (int i = 0; i < len; i++) {
			Dtalge alge = this.get(i);
			//--- check DtBase contains
			if (!alge.containsBase(base)) {
				// 基底が存在しない
				continue;
			}
			//--- check string value contains in thesaurus
			String value = alge.getString(base);
			if (value == null || !thes.contains(value)) {
				// 語句がシソーラス定義に存在しない
				continue;
			}
			// シソーラスに定義されている語句を比較
			Iterator<Map.Entry<String, Set<Integer>>> it = wordmap.entrySet().iterator();
			while (it.hasNext()) {
				// 極大値候補との比較
				Map.Entry<String, Set<Integer>> entry = it.next();
				int cmp = thes.compareTo(value, entry.getKey());
				if (cmp < 0) {
					// value < entry.getKey()
					//--- 比較可能な値の中で最大値ではないので、候補に含めない
					value = null;
					break;
				}
				else if (cmp > 0) {
					// value > entry.getKey()
					//--- 比較可能な値の中で最大値のため、候補を除外
					maxindices.removeAll(entry.getValue());
					it.remove();
				}
				// else : value == entry.getKey()
				//--- 比較不可能もしくは同値のため、次の候補と比較
			}
			// 語句を候補へ登録
			if (value != null) {
				Integer candidx = Integer.valueOf(i);
				Set<Integer> candset = wordmap.get(value);
				if (candset == null) {
					// 新規候補を追加
					candset = new HashSet<Integer>();
					candset.add(candidx);
					wordmap.put(value, candset);
				} else {
					// 既存の候補に追加
					candset.add(candidx);
				}
				maxindices.add(candidx);
			}
		}
		//--- release map
		wordmap = null;
		
		// 新しいデータ代数集合に格納
		DtAlgeSet retset = new DtAlgeSet(maxindices.size());
		for (Integer index : maxindices) {
			retset.fastAdd(this.get(index.intValue()));
		}
		return retset;
	}
	
	/**
	 * 指定された基底に関連付けられている値のうち、指定されたシソーラス定義において
	 * 比較不可能な極小値のみを含むデータ代数元を取り出す。
	 * <p>このメソッドは、指定された基底に関連付けられている値をすべて比較し、
	 * シソーラス定義において比較可能な値の中から最小値のみを含むデータ代数元を取り出す。
	 * 取り出されたデータ代数元に含まれる値は、それぞれ比較不可能もしくは同値となっている。
	 * @param base	判定する値が関連付けられているデータ代数基底
	 * @param thes	シソーラス定義
	 * @return	比較不可能な極小値のみを含むデータ代数元を格納する、新しいデータ代数集合を返す。
	 * 			比較不可能な極小値が存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>base</em> が文字列型のデータ代数基底ではない場合
	 * @since 0.30
	 */
	public DtAlgeSet selectThesaurusMin(DtBase base, DtStringThesaurus thes) {
		// check
		if (base == null)
			throw new NullPointerException("DtBase argument is null.");
		if (thes == null)
			throw new NullPointerException("DtThesaurus object is null.");
		if (!DtDataTypes.STRING.equals(base.getTypeKey()))
			throw new IllegalArgumentException("DtBase data type is not String.");
		
		if (thes.isEmpty() || this.isEmpty()) {
			//--- No thesaurus relations or No alge elements
			return new DtAlgeSet();
		}

		// 極小値の元のみを取り出す
		Set<Integer> maxindices = new TreeSet<Integer>();
		Map<String, Set<Integer>> wordmap = new HashMap<String, Set<Integer>>();
		int len = this.size();
		for (int i = 0; i < len; i++) {
			Dtalge alge = this.get(i);
			//--- check DtBase contains
			if (!alge.containsBase(base)) {
				// 基底が存在しない
				continue;
			}
			//--- check string value contains in thesaurus
			String value = alge.getString(base);
			if (value == null || !thes.contains(value)) {
				// 語句がシソーラス定義に存在しない
				continue;
			}
			// シソーラスに定義されている語句を比較
			Iterator<Map.Entry<String, Set<Integer>>> it = wordmap.entrySet().iterator();
			while (it.hasNext()) {
				// 極小値候補との比較
				Map.Entry<String, Set<Integer>> entry = it.next();
				int cmp = thes.compareTo(value, entry.getKey());
				if (cmp > 0) {
					// value > entry.getKey()
					//--- 比較可能な値の中で最小値ではないので、候補に含めない
					value = null;
					break;
				}
				else if (cmp < 0) {
					// value < entry.getKey()
					//--- 比較可能な値の中で最小値のため、候補を除外
					maxindices.removeAll(entry.getValue());
					it.remove();
				}
				// else : value == entry.getKey()
				//--- 比較不可能もしくは同値のため、次の候補と比較
			}
			// 語句を候補へ登録
			if (value != null) {
				Integer candidx = Integer.valueOf(i);
				Set<Integer> candset = wordmap.get(value);
				if (candset == null) {
					// 新規候補を追加
					candset = new HashSet<Integer>();
					candset.add(candidx);
					wordmap.put(value, candset);
				} else {
					// 既存の候補に追加
					candset.add(candidx);
				}
				maxindices.add(candidx);
			}
		}
		//--- release map
		wordmap = null;
		
		// 新しいデータ代数集合に格納
		DtAlgeSet retset = new DtAlgeSet(maxindices.size());
		for (Integer index : maxindices) {
			retset.fastAdd(this.get(index.intValue()));
		}
		return retset;
	}
	
	//------------------------------------------------------------
	// Override
	//------------------------------------------------------------

	/**
	 * 集合の指定された位置にある要素を、指定された要素で置き換える。
	 * <br>
	 * このメソッドでは、<tt>null</tt> は許容されない。
	 * 
	 * @param index		置換される要素のインデックス
	 * @param element	指定された位置に格納される要素
	 * 
	 * @return	指定された位置に以前あった要素
	 * 
	 * @throws NullPointerException <code>element</code> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 * 
	 * @see java.util.ArrayList#set(int, Object)
	 */
	public Dtalge set(int index, Dtalge element) {
		return super.set(index, Validations.validNotNull(element));
	}

	/**
	 * 集合(リスト)の最後に、指定された要素を追加する。
	 * <br>
	 * このメソッドでは、<tt>null</tt> は許容されない。
	 * 
	 * @param element	追加する要素
	 * @return <tt>true</tt> (<code>Collection.add</code> の汎用規約通り)
	 * 
	 * @throws NullPointerException <code>element</code> が <tt>null</tt> の場合
	 * 
	 * @see java.util.ArrayList#add(Object)
	 */
	public boolean add(Dtalge element) {
		return super.add(Validations.validNotNull(element));
	}

	/**
	 * 集合の指定された位置に、指定された要素を挿入する。
	 * <br>
	 * 現在その位置にある要素と皇族の要素は後方に移動する。
	 * <br>
	 * このメソッドでは、<tt>null</tt> は許容されない。
	 * 
	 * @param index		挿入する位置のインデックス
	 * @param element	挿入する要素
	 * 
	 * @throws NullPointerException	<code>element</code> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 * 
	 * @see java.util.ArrayList#add(int, Object)
	 */
	public void add(int index, Dtalge element) {
		super.add(index, Validations.validNotNull(element));
	}

	/**
	 * 集合(リスト)の末尾に、指定されたコレクションの全ての要素を追加する。
	 * ただし、<tt>null</tt> の要素は無視される。
	 * <br>
	 * これらの要素は、指定されたコレクションの <code>Iterator</code> が返す
	 * 順序で追加される。
	 * 
	 * @param c	追加する要素のコレクション
	 * @return	この呼び出しの結果、この集合が変更された場合は <tt>true</tt>
	 * 
	 * @throws NullPointerException 指定されたコレクションが <tt>null</tt> の場合
	 * 
	 * @see java.util.ArrayList#addAll(Collection)
	 */
	public boolean addAll(Collection<? extends Dtalge> c) {
		int numCur = this.size();
		int numNew = c.size();
		ensureCapacity(numCur + numNew);
		for (Dtalge alge : c) {
			if (alge != null) {
				super.add(alge);
			}
		}
		return (numCur != this.size());
	}

	/**
	 * 集合(リスト)の指定された位置から、指定されたコレクションの全ての要素を挿入する。
	 * ただし、<tt>null</tt> の要素は無視される。
	 * <br>
	 * 指定された位置に要素がある場合、その位置の要素を移動させ、
	 * 後続の要素を後方に移動して、それぞれのインデックスを増やす。
	 * 新しい要素は、指定されたコレクションの <code>Iterator</code> が
	 * 返す順序で挿入される。
	 * 
	 * @param index	最初の要素を挿入する位置のインデックス
	 * @param c	挿入する要素のコレクション
	 * @return この呼び出しの結果、この集合が変更された場合は <tt>true</tt>
	 * 
	 * @throws NullPointerException	指定されたコレクションが <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 * 
	 * @see java.util.ArrayList#addAll(int, Collection)
	 */
	public boolean addAll(int index, Collection<? extends Dtalge> c) {
		int numCur = this.size();
		int numNew = c.size();
		ensureCapacity(numCur + numNew);
		for (Dtalge alge : c) {
			if (alge != null) {
				super.add(index++, alge);
			}
		}
		return (numCur != this.size());
	}

	/**
	 * 集合のシャローコピーを返す。
	 * 要素自体は複製されない。
	 * 
	 * @return この集合のシャローコピー
	 */
	public Object clone() {
		return (DtAlgeSet)super.clone();
	}

	/**
	 * 指定された基底の値で要素が並べ替えられた、新しいデータ代数集合を返す。
	 * 値の並べ替えにおいて、等しい値の順序は変更されない。
	 * <tt>null</tt> 値はどの値よりも小さい値として並べ替えられる。
	 * また、基底が存在しない要素は、<tt>null</tt> 値よりも小さい値として並べ替えられる。
	 * @param base			並べ替えのキーとする値が関連付けられているデータ代数基底
	 * @param ascending		昇順にソートする場合は <tt>true</tt>、降順にソートする場合は <tt>false</tt>
	 * @return	要素が並べ替えられた、新しいデータ代数集合
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws ClassCastException		基底に関連付けられた値が(<code>Comparable</code> インタフェースを実装していない)比較不可能の場合
	 * @since 0.30
	 */
	public DtAlgeSet sortedAlgesByValue(DtBase base, boolean ascending) {
		DtAlgeSet newset = this.cloneInstance();
		newset.sortAlgesByValue(base, ascending);
		return newset;
	}

	/**
	 * 指定された基底の値で要素が並べ替えられた、新しいデータ代数集合を返す。
	 * 値の並べ替えにおいて、等しい値の順序は変更されない。
	 * <tt>null</tt> 値はどの値よりも小さい値として並べ替えられる。
	 * また、基底が存在しない要素は、<tt>null</tt> 値よりも小さい値として並べ替えられる。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、自身の内容を書き換える、破壊型メソッドである。
	 * </blockquote>
	 * @param base			並べ替えのキーとする値が関連付けられているデータ代数基底
	 * @param ascending		昇順にソートする場合は <tt>true</tt>、降順にソートする場合は <tt>false</tt>
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws ClassCastException		基底に関連付けられた値が(<code>Comparable</code> インタフェースを実装していない)比較不可能の場合
	 * @since 0.30
	 */
	public void sortAlgesByValue(DtBase base, boolean ascending) {
		if (base == null)
			throw new NullPointerException();
		Collections.sort(this, new AlgesComparator(base, ascending));
	}
	
	static protected class AlgesComparator implements Comparator<Dtalge>
	{
		protected final DtBase	_base;
		protected final boolean	_ascending;
		
		public AlgesComparator(final DtBase base, final boolean ascending) {
			this._base = base;
			this._ascending = ascending;
		}

		@SuppressWarnings("unchecked")
		public int compare(Dtalge alge1, Dtalge alge2) {
			if (alge1 == alge2) {
				return 0;	// same
			}
			if (alge1 == null) {
				return (_ascending ? -1 : 1);	// alge1(null) < alge2(not null)
			}
			if (alge2 == null) {
				return (_ascending ? 1 : -1);		// alge1(not null) > alge2(null)
			}

			boolean existBase1 = alge1.containsBase(_base);
			boolean existBase2 = alge2.containsBase(_base);
			if (!existBase1) {
				if (!existBase2) {
					return 0;	// alge1(no base) == alge2(no base)
				} else {
					return (_ascending ? -1 : 1);	// alge1(no base) < alge2(has base)
				}
			}
			else if (!existBase2) {
				return (_ascending ? 1 : -1);		// alge1(has base) > alge2(no base)
			}
			
			Object value1 = alge1.get(_base);
			Object value2 = alge2.get(_base);
			if (value1 == value2) {
				return 0;	// same values
			}
			if (value1 == null) {
				return (_ascending ? -1 : 1);	// alge1(null value) < alge2(instanced value)
			}
			else if (value2 == null) {
				return (_ascending ? 1 : -1);		// alge1(instanced value) > alge2(null value)
			}
			else {
				if (_ascending) {
					return ((Comparable)value1).compareTo(value2);
				} else {
					return ((Comparable)value2).compareTo(value1);
				}
			}
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected DtAlgeSet newInstance(int initialCapacity) {
		return new DtAlgeSet(initialCapacity);
	}
	
	protected DtAlgeSet newInstance(Set<Dtalge> set) {
		return new DtAlgeSet(set, true);
	}
	
	protected DtAlgeSet cloneInstance() {
		return (DtAlgeSet)this.clone();
	}
	
	protected boolean fastAdd(Dtalge alge) {
		return super.add(alge);
	}
	
	protected boolean fastAddAll(Collection<? extends Dtalge> c) {
		return super.addAll(c);
	}

	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最小値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、引数の正当性をチェックしない。
	 * </blockquote>
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最小値を返す。
	 * 			最小値が取得できない場合は <tt>null</tt> を返す。
	 * @since 0.30
	 */
	@SuppressWarnings("unchecked")
	protected Object getMinValue(DtBase base) {
		if (this.isEmpty())
			return null;

		int index;
		int len = this.size();
		Object val = null;
		// 比較する初期値を取得
		for (index=0; index < len; index++) {
			Dtalge alge = this.get(index);
			if (alge.containsBase(base)) {
				Object algevalue = alge.get(base);
				if (algevalue instanceof Comparable) {
					val = algevalue;	// not null
					break;
				}
			}
		}
		if (val == null) {
			// 比較可能な値は存在しない
			return null;
		}
		
		// 値の比較
		for (index = index+1; index < len; index++) {
			Dtalge alge = this.get(index);
			if (alge.containsBase(base)) {
				Object algevalue = alge.get(base);
				if (algevalue instanceof Comparable) {
					try {
						if (((Comparable)algevalue).compareTo(val) < 0) {
							// algevalue < val
							val = algevalue;
						}
					} catch (Throwable ex) {
						// cannot to compare
						return null;
					}
				}
			}
		}
		
		return val;
	}
	
	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最大値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、引数の正当性をチェックしない。
	 * </blockquote>
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最大値を返す。
	 * 			最大値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 0.30
	 */
	@SuppressWarnings("unchecked")
	protected Object getMaxValue(DtBase base) {
		if (base == null) throw new NullPointerException();
		if (this.isEmpty())
			return null;

		int index;
		int len = this.size();
		Object val = null;
		// 比較する初期値を取得
		for (index=0; index < len; index++) {
			Dtalge alge = this.get(index);
			if (alge.containsBase(base)) {
				Object algevalue = alge.get(base);
				if (algevalue instanceof Comparable) {
					val = algevalue;	// not null
					break;
				}
			}
		}
		if (val == null) {
			// 比較可能な値は存在しない
			return null;
		}
		
		// 値の比較
		for (index = index+1; index < len; index++) {
			Dtalge alge = this.get(index);
			if (alge.containsBase(base)) {
				Object algevalue = alge.get(base);
				if (algevalue instanceof Comparable) {
					try {
						if (((Comparable)algevalue).compareTo(val) > 0) {
							// algevalue > val
							val = algevalue;
						}
					} catch (Throwable ex) {
						// cannot to compare
						return null;
					}
				}
			}
		}
		
		return val;
	}

	//------------------------------------------------------------
	// for I/O
	//------------------------------------------------------------
	
	/**
	 * データ代数集合の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
//			writeToTableCsv(writer);
//		}
//		finally {
//			writer.close();
//		}
		//--- 順序はオリジナル、nullは出力する
		toTableCSV(null, false, csvFile);
	}
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
//			writeToTableCsv(writer);
//		}
//		finally {
//			writer.close();
//		}
		//--- 順序はオリジナル、nullは出力する
		toTableCSV(null, false, csvFile, charsetName);
	}
	
	/**
	 * データ代数集合の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数集合の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数集合の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数集合の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数集合の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * @param orderedBases	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.40
	 */
	public void toTableCSV(DtBaseSet orderedBases, boolean withoutNull, File csvFile)
		throws IOException, FileNotFoundException
	{
		CsvWriter writer = new CsvWriter(csvFile);
		try {
			writeToTableCsv_v2(writer, orderedBases, withoutNull);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * @param orderedBases	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
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
	public void toTableCSV(DtBaseSet orderedBases, boolean withoutNull, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		CsvWriter writer = new CsvWriter(csvFile, charsetName);
		try {
			writeToTableCsv_v2(writer, orderedBases, withoutNull);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * データ代数集合の内容を、指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている要素の順序で出力される。
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
			writeToCsv_v2(writer);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている要素の順序で出力される。
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
			writeToCsv_v2(writer);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * CSV フォーマットのファイルを読み込み、新しいデータ代数集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DtAlgeSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 */
	static public DtAlgeSet fromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		CsvReader reader = new CsvReader(csvFile);
		DtAlgeSet newSet = new DtAlgeSet();
		
		try {
			newSet.readFromCsv(reader);
		}
		finally {
			reader.close();
		}
		
		return newSet;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しいデータ代数集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DtAlgeSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	static public DtAlgeSet fromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		DtAlgeSet newSet = new DtAlgeSet();
		
		try {
			newSet.readFromCsv(reader);
		}
		finally {
			reader.close();
		}
		
		return newSet;
	}
	
	/**
	 * データ代数集合の内容を、指定のファイルに XML フォーマットで出力する。
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
	 * XML フォーマットのファイルを読み込み、新しいデータ代数集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlFile 読み込む XML ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DtAlgeSet</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 */
	static public DtAlgeSet fromXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException
	{
		//XmlDocument xmlDocument = XmlDocument.fromFile(xmlFile);
		//return DtAlgeSet.fromXML(xmlDocument);
		
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

	/*
	 * CSVファイルフォーマットの第1行目のキーワード
	 */
	//static protected final String CSV_KEYWORD = "#DtalgebraSet";

	/*
	 * XMLファイルフォーマットでのデータ代数集合のルートノード名
	 */
	//static protected final String XML_ELEMENT_NAME = "DtalgebraSet";
	/*
	 * XMLファイルフォーマットでのデータ代数元のノード名
	 */
	//static protected final String XML_ELEMENT_GROUP = "Dtalgebra";
	/*
	 * XMLファイルフォーマットでのデータ代数元の要素ノード名
	 */
	//static protected final String XML_ELEMENT_ITEM = "Dtelem";
	/*
	 * XMLファイルフォーマットでのデータ代数元要素の値のノード名
	 */
	//static protected final String XML_ELEMENT_VALUE = "Dtvalue";
	
	/**
	 * データ代数集合の内容から、XML ドキュメントを生成する。
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
		
		Element root = encodeToElement(xml);
		xml.append(root);
		
		return xml;
	}

	/*
	 * CSVフォーマットで DtAlgeSet の内容を出力
	 * 
	 * @param writer	CSVファイル出力オブジェクト
	 * 
	 * @throws	IOException	入出力エラーが発生した場合
	 *
	protected void writeToCsv(CsvWriter writer)
		throws IOException
	{
		// キーワード出力
		writer.writeLine(Dtalge.CSV_KEYWORD);
		
		// データ出力
		Iterator<Dtalge> it = this.iterator();
		//--- 先頭エントリ
		while (it.hasNext()) {
			Dtalge alge = it.next();
			if (!alge.isEmpty()) {
				alge.writeToCsv(writer);
				break;
			}
		}
		//--- 2番目以降のエントリ
		while (it.hasNext()) {
			Dtalge alge = it.next();
			if (alge.isEmpty()) {
				// 出力なし
				continue;
			}
			
			// 元の区切り(空行)を挿入
			writer.writeBlankLine();
			
			// 元を出力
			alge.writeToCsv(writer);
		}
		
		writer.flush();
	}
	*****/

	/**
	 * CSVフォーマットで DtAlgeSet の内容を出力する。
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
		// キーワード出力
		writer.writeLine(Dtalge.CSV_KEYWORD_V2);

		// データ出力
		Iterator<Dtalge> it = this.iterator();
		//--- 先頭エントリ
		while (it.hasNext()) {
			Dtalge alge = it.next();
			if (!alge.isEmpty()) {
				alge.writeToCsv_v2(writer);
				break;
			}
		}
		//--- 2番目以降のエントリ
		while (it.hasNext()) {
			Dtalge alge = it.next();
			if (alge.isEmpty()) {
				// 出力なし
				continue;
			}

			// 元の区切り(空行)を挿入
			writer.writeBlankLine();

			// 元を出力
			alge.writeToCsv_v2(writer);
		}

		writer.flush();
	}

	/*
	 * テーブル形式のCSVフォーマットで DtAlgeSet の内容を出力
	 * 
	 * @param writer	CSVファイル出力オブジェクト
	 * @throws IOException	入出力エラーが発生した場合
	 * 
	 * @since 0.20
	 *
	protected void writeToTableCsv(CsvWriter writer)
		throws IOException
	{
		// キーワード出力
		writer.writeLine(Dtalge.CSV_TABLE_KEYWORD);
		
		// 基底集合の取得
		DtBaseSet bases = this.getBases();
		
		// データ出力
		if (!bases.isEmpty()) {
			// 全基底の出力
			Dtalge.writeBasesToTableCsv(writer, bases);
			
			// 全要素の値を出力
			for (Dtalge alge : this) {
				alge.writeValuesToTableCsv(writer, bases);
			}
		}
		
		// flush
		writer.flush();
	}
	*****/

	/**
	 * テーブル形式のCSVフォーマットで DtAlgeSet の内容を出力する。
	 * 基底のない要素は空文字をフィールドに出力し、
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値を出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープする。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底を出力する。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力する。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力せず、
	 * フィールドは空欄となる。
	 * 
	 * @param writer	CSVファイル出力オブジェクト
	 * @param orderedBases	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * 
	 * @since 0.40
	 */
	protected void writeToTableCsv_v2(CsvWriter writer, DtBaseSet orderedBases, boolean withoutNull)
		throws IOException
	{
		// キーワード出力
		writer.writeLine(Dtalge.CSV_TABLE_KEYWORD_V2);
		
		// 基底集合の取得
		Set<DtBase> bases = this.getBases();
		//--- 基底集合を並べ替える
		bases = Dtalge.sortByOrderedBaseSet(orderedBases, bases);
		
		// データ出力
		if (!bases.isEmpty()) {
			// 全基底の出力
			Dtalge.writeBasesToTableCsv(writer, bases);
			
			// 全要素の値を出力
			if (withoutNull) {
				//--- null や空文字は出力しない
				for (Dtalge alge : this) {
					alge.writeValuesToTableCsv_v2(writer, bases, Dtalge.CSV_VALUE_EMPTY);
				}
			} else {
				//--- 空文字を null として出力
				for (Dtalge alge : this) {
					alge.writeValuesToTableCsv_v2(writer, bases, Dtalge.CSV_COMMAND_NULL);
				}
			}
		}
		
		// flush
		writer.flush();
	}

	/**
	 * CSVフォーマットで DtAlgeSet の内容を読み込む。
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
		Dtalge.CsvFileType csvType = null;
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
//			if (Dtalge.CSV_KEYWORD.equals(strFieldValue)) {
//				isTableCsv = false;
//			}
//			else if (Dtalge.CSV_TABLE_KEYWORD.equals(strFieldValue)) {
//				isTableCsv = true;
//			}
//			else {
//				throw new CsvFormatException("Illegal DtAlgeSet file ID.", record.getLineNo(), 1);
//			}
			csvType = Dtalge.CsvFileType.fromCsvKeyword(strFieldValue);
			if (csvType == null) {
				throw new CsvFormatException("Illegal DtAlgeSet field ID.", record.getLineNo(), 1);
			}
			// 以降のフィールドは無視
		}

		// データ読み込み
		Dtalge newAlge = null;
//		if (isTableCsv) {
//			// テーブル形式のCSV読み込み
//			List<DtBase> baselist = Dtalge.readBasesFromTableCsv(reader);
//			if (baselist != null) {
//				while ((record = reader.readRecord()) != null) {
//					newAlge = new Dtalge(Dtalge.DEFAULT_INITIAL_CAPACITY);
//					newAlge.readValuesFromTableCsv(record, baselist);
//					super.add(newAlge);
//				}
//			}
//		}
//		else {
//			// CSV標準形の読み込み
//			while ((record = reader.readRecord()) != null) {
//				// 空行は元の区切りとする
//				if (!record.hasFields() || !record.hasValues()) {
//					if (newAlge != null && !newAlge.isEmpty()) {
//						// 元に実体があれば、集合に格納
//						super.add(newAlge);
//					}
//					newAlge = null;
//					continue;
//				}
//				
//				// レコードを読み込む
//				if (newAlge == null) {
//					newAlge = new Dtalge(Dtalge.DEFAULT_INITIAL_CAPACITY);
//				}
//				newAlge.readRecordFromCsv(record);
//			}
//			//--- 最後の元の格納
//			if (newAlge != null && !newAlge.isEmpty()) {
//				// 元に実体があれば、集合に格納
//				super.add(newAlge);
//			}
//		}
		if (csvType.isTable()) {
			// テーブル形式のCSV読み込み
			List<DtBase> baselist = Dtalge.readBasesFromTableCsv(reader);
			if (baselist != null) {
				if (csvType.version() == Dtalge.CsvFileType.V2) {
					//--- テーブル形式 v2
					while ((record = reader.readRecord()) != null) {
						newAlge = new Dtalge(Dtalge.DEFAULT_INITIAL_CAPACITY);
						newAlge.readValuesFromTableCsv_v2(record, baselist);
						super.add(newAlge);
					}
				} else {
					//--- 初期テーブル形式
					while ((record = reader.readRecord()) != null) {
						newAlge = new Dtalge(Dtalge.DEFAULT_INITIAL_CAPACITY);
						newAlge.readValuesFromTableCsv(record, baselist);
						super.add(newAlge);
					}
				}
			}
		}
		else {
			// CSV標準形の読み込み
			if (csvType.version() == Dtalge.CsvFileType.V2) {
				//--- v2
				while ((record = reader.readRecord()) != null) {
					// 空行は元の区切りとする
					if (!record.hasFields() || !record.hasValues()) {
						if (newAlge != null && !newAlge.isEmpty()) {
							// 元に実体があれば、集合に格納
							super.add(newAlge);
						}
						newAlge = null;
						continue;
					}
					
					// レコードを読み込む
					if (newAlge == null) {
						newAlge = new Dtalge(Dtalge.DEFAULT_INITIAL_CAPACITY);
					}
					newAlge.readRecordFromCsv_v2(record);
				}
			} else {
				//--- 初期形式
				while ((record = reader.readRecord()) != null) {
					// 空行は元の区切りとする
					if (!record.hasFields() || !record.hasValues()) {
						if (newAlge != null && !newAlge.isEmpty()) {
							// 元に実体があれば、集合に格納
							super.add(newAlge);
						}
						newAlge = null;
						continue;
					}
					
					// レコードを読み込む
					if (newAlge == null) {
						newAlge = new Dtalge(Dtalge.DEFAULT_INITIAL_CAPACITY);
					}
					newAlge.readRecordFromCsv(record);
				}
			}
			//--- 最後の元の格納
			if (newAlge != null && !newAlge.isEmpty()) {
				// 元に実体があれば、集合に格納
				super.add(newAlge);
			}
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
		// create root
		Element node = xmlDocument.createElement(Dtalge.XML_ELEMENT_NAME);
		
		// create entries
		for (Dtalge alge : this) {
			Element algeNode = alge.encodeToElement(xmlDocument);
			node.appendChild(algeNode);
		}
		
		// completed
		return node;
	}

	/**
	 * <code>DtAlgeSet</code> の SAX パーサーハンドラ。
	 * 
	 * @version 0.20	2010/02/25
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 */
	static class SaxParserHandler extends DefaultHandler
	{
		private final Stack<String> stackElement = new Stack<String>();
		private final DtAlgeSet algeSet = new DtAlgeSet();
		
		private Locator saxLocator = null;
		private SaxDecoder decoder = null;
		
		public DtAlgeSet getTargetInstance() {
			return algeSet;
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
				if (Dtalge.XML_ELEMENT_NAME.equals(qName)) {
					XmlErrors.validChildOfParent(saxLocator, qName, XmlErrors.peekStringStack(stackElement));
					
					// stack element
					stackElement.add(qName);
				}
				else if (Dtalge.XML_ELEMENT_GROUP.equals(qName)) {
					XmlErrors.validChildOfParent(saxLocator, qName, XmlErrors.peekStringStack(stackElement), Dtalge.XML_ELEMENT_NAME);
					decoder = new SaxDecoder(saxLocator, stackElement);
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
					algeSet.add(decoder.getTargetInstance());
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
