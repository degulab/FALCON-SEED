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
 * @(#)Exalge.java	0.984	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Exalge.java	0.983	2010/02/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Exalge.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Exalge.java	0.970	2009/03/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Exalge.java	0.960	2009/02/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Exalge.java	0.94	2008/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Exalge.java	0.931	2007/09/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Exalge.java	0.93	2007/09/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Exalge.java	0.92	2007/08/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Exalge.java	0.91	2007/08/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Exalge.java	0.90	2007/07/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Exalge.java	0.10	2004/12/13
 *     - created by Li Hou(SOARS Project.)
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
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
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
import exalge2.util.Strings;

/**
 * 交換代数の基底と値を保持する交換代数クラス。
 * 
 * <p>このクラスは、不変オブジェクト(Immutable)である。
 * 
 * <p>交換代数値は、交換代数基底と値のペアを、交換代数基底をキーとする
 * Mapで保持する。したがって、同一基底の値が複数存在することはない。
 * <br>
 * このクラスの Map の実装は、挿入型の {@link java.util.LinkedHashMap <code>LinkedHashMap</code>} である。
 * したがって、{@link #put(ExBase, BigDecimal) <code>put</code>} メソッドや 
 * {@link #plus(ExBase, BigDecimal) <code>plus</code>} メソッド、ファイル入出力において、
 * クラス内での基底の順序は基本的に維持される。詳細は、各メソッドの説明を参照のこと。
 * 
 * <p>このクラスでは、実数値 0 の基底もデータとして保持される。
 * 基本的に、演算によって実数値 0 となる場合も、その基底は保持される。
 * <br>
 * ただし、次のメソッド実行結果には、実数値 0 の基底は含まれない。
 * <ul>
 * <li>{@link #clean()}
 * <li>{@link #bar()}
 * <li>{@link #inverse()}
 * <li>{@link #multiple(Exalge)}
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
 * <code>Exalge</code> は、CSV ファイル、XML ファイル(XML ドキュメント)形式の
 * 入出力インタフェースを提供する。
 * <br>
 * 入出力されるファイルにおいて、共通の事項は、次のとおり。
 * <ul>
 * <li>基底キーに、次の文字は使用できない。<br>
 * &lt; &gt; - , ^ &quot; % &amp; ? | @ ' " (空白)
 * <li>ハットキーに指定可能な文字列は、次のもののみ(大文字、小文字は区別しない)。<br>
 * HAT ^ NO_HAT (空白)
 * </ul>
 * CSVファイルは、カンマ区切りのテキストファイルで、次のフォーマットに従う。
 * <ul>
 * <li>行の先頭から、次のようなカラム構成となる。<br>
 * <i>値</i>，<i>ハットキー</i>，<i>名前キー</i>，<i>単位キー</i>，<i>時間キー</i>，<i>サブジェクトキー</i>
 * <li>文字コードは、実行するプラットフォームの処理系に依存する。
 * <li>空行は無視される。
 * <li>各カラムの前後空白は、無視される。
 * <li><code>'#'</code> で始まる行は、コメント行とみなす。
 * <li>先頭カラムが &quot;null&quot;(大文字小文字は区別しない)の場合は、<tt>null</tt>値とみなす。
 * <li>ハットキーが省略(空文字)の場合、ハットなし(NO_HAT)とみなす。
 * <li>単位キー以降のカラムが省略(空文字)の場合、省略記号('#')が代入される。
 * </ul>
 * また、CSV ファイルの読み込みにおいて、次の場合に {@link exalge2.io.csv.CsvFormatException} 例外がスローされる。
 * <ul>
 * <li>先頭カラムが &quot;null&quot; ではなく、数値として正しくない場合
 * <li>ハットキー文字列が指定可能な文字列ではない場合
 * <li>名前キーが省略(空文字)された場合
 * <li>各基底キーに使用できない文字が含まれている場合
 * </ul>
 * なお、CSV ファイルの入出力は、{@link #toCSV(File)}、{@link #fromCSV(File)} により行う。
 * また、指定した文字セットでの入出力は、{@link #toCSV(File, String)}、{@link #fromCSV(File, String)} 
 * により行う。
 * <p>
 * XMLドキュメントは、次の構成となる。XMLファイルも同様のフォーマットに従う。
 * <pre><code>
 * &lt;ExalgebraSet&gt;
 *   &lt;Exalgebra&gt;
 *     &lt;Exelem value=&quot;<i>値</i>&quot;&gt;
 *       &lt;Exbase hat=&quot;<i>ハットキー</i>&quot; name=&quot;<i>名前キー</i>&quot; unit=&quot;<i>単位キー</i>&quot; time=&quot;<i>時間キー</i>&quot; subject=&quot;<i>サブジェクトキー</i>&quot; /&gt;
 *     &lt;/Exelem&gt;
 *        ・
 *         ・
 *         ・
 *   &lt;/Exalgebra&gt;
 *        ・
 *         ・
 *         ・
 * &lt;/ExalgebraSet&gt;
 * </code></pre>
 * <ul>
 * <li>ルートノードは単一の &lt;ExalgebraSet&gt; となる。
 * <li>&lt;Exalgebra&gt; が、交換代数の元を示すノードとなる。
 * <li>&lt;Exelem&gt; が、交換代数の要素を示すノードとなる。
 * <li>&quot;value&quot; 属性が、交換代数要素の値を示す。
 * <li>&lt;Exbase&gt; が、交換代数の要素の基底を示すノードとなる。属性が基底のキーとなる。
 * <li>&lt;Exbase&gt; ノードは、&lt;Exelem&gt; の唯一の子ノードであり、必ず１つだけ存在する。
 * <li>&lt;Exelem&gt; ノードは、&lt;Exalgebra&gt; の子ノードであり、複数存在する。
 * <li>&lt;Exalgebra&gt; ノードは、&lt;ExagebraSet&gt; の子ノードであり、複数存在する。
 * <li>文字コードは、&quot;UTF-8&quot; とする。
 * <li>各基底キーの前後空白は無視される。
 * <li>&quot;value&quot; 属性の値が &quot;null&quot;(大文字小文字は区別しない)の場合は、<tt>null</tt>値とみなす。
 * <li>ハットキーが省略された場合、ハットなし(NO_HAT)とみなす。
 * <li>名前キー以外の基底キーが省略された場合、省略記号('#')が代入される。
 * </ul>
 * また、XML ドキュメントのパース(XML ファイルの読み込み)において、
 * 次の場合に {@link exalge2.io.xml.XmlDomParseException} 例外がスローされる。
 * <ul>
 * <li>XML のノードツリー構成が正しくない場合
 * <li>&quot;value&quot; 属性に指定された文字列が &quot;null&quot; ではなく、数値として正しくない場合
 * <li>ハットキー文字列が指定可能な文字列ではない場合
 * <li>名前キーが省略(空文字)された場合
 * <li>各基底キーに使用できない文字が含まれている場合
 * </ul>
 * なお、XML ドキュメントの入出力は、{@link #toXML()}、{@link #fromXML(XmlDocument)} により行う。
 * また、XML ファイルの入出力は、{@link #toXML(File)}、{@link #fromXML(File)} により行う。
 * 
 * @version 0.984	2014/05/29
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Li Hou(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public final class Exalge implements exalge2.io.IDataOutput, Iterable<Exalge>
{

	/**
	 * 基底と値のペア
	 */
	protected LinkedHashMap<ExBase,BigDecimal>	data;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 値を持たない <code>Exalge</code> の新しいインスタンスを生成する。
	 */
	public Exalge() {
		this.data = new LinkedHashMap<ExBase,BigDecimal>();
	}

    /**
     * 指定された初期容量および指定された負荷係数で、値を持たない <code>Exalge</code> の新しいインスタンスを生成する。
     * 
     * @param initialCapacity 交換代数元(マップ)の初期容量
     * @param loadFactor 交換代数元(マップ)の負荷係数
     * 
     * @throws 初期容量が 0 より小さいか、負荷係数が正の値ではない場合にスローされる
     * 
     * @see java.util.LinkedHashMap#LinkedHashMap(int, float)
     * 
     * @since 0.94
     */
	protected Exalge(int initialCapacity, float loadFactor) {
		this.data = new LinkedHashMap<ExBase,BigDecimal>(initialCapacity, loadFactor);
	}

    /**
     * 指定された初期容量およびデフォルトの負荷係数で、値を持たない <code>Exalge</code> の新しいインスタンスを生成する。
     * 
     * @param initialCapacity 交換代数元(マップ)の初期容量
     * 
     * @throws 初期容量が 0 より小さい場合にスローされる
     * 
     * @see java.util.LinkedHashMap#LinkedHashMap(int)
     * 
     * @since 0.94
     */
	protected Exalge(int initialCapacity) {
		this.data = new LinkedHashMap<ExBase,BigDecimal>(initialCapacity);
	}

	/**
	 * 指定された基底の値を持つ <code>Exalge</code> の新しいインスタンスを生成する。
	 * 
	 * @param oneStringKey	交換代数基底の一意文字列キー
	 * @param value		交換代数の値
	 * 
	 * @throws NullPointerException <tt>value</tt> が <tt>null</tt>の場合
	 * @throws	IllegalArgumentException	次の場合、この例外が発生する。
	 * <ul>
	 * <li>名前キーが <code>null</code>もしくは、長さ 0 の場合
	 * <li>ハットキーが、{@link ExBase#NO_HAT} もしくは {@link ExBase#HAT} 以外の場合
	 * </ul>
	 */
	public Exalge(String oneStringKey, BigDecimal value) {
		this(new ExBase(oneStringKey), value);
	}

	/**
	 * 指定された基底の値を持つ <code>Exalge</code> の新しいインスタンスを生成する。
	 * 
	 * @param base 交換代数の基底
	 * @param value 交換代数の値
	 * 
	 * @throws NullPointerException <em>base</em> が <tt>null</tt> の場合、
	 * 								もしくは、<em>value</em> が <tt>null</tt> であり、
	 * 								同一基底の値との加算演算が行えない場合
	 */
	public Exalge(ExBase base, BigDecimal value) {
		this.data = new LinkedHashMap<ExBase,BigDecimal>(1);
		if (base == null)
			throw new NullPointerException("base is null");
		//if (value == null)
		//	throw new NullPointerException("value is null");
		this.putValue(base, value);	// 自身書き換え
	}

	/**
	 * 指定された基底と値の配列から <code>Exalge</code> の新しいインスタンスを生成する。
	 * 
	 * <p>このメソッドは、基底と値が順に配置される <code>Object</code> の配列から
	 * 基底と値のペアを取り出し、その基底と値のペアを持つ <code>Exalge</code> の新しいインスタンスを
	 * 生成する。生成されたインスタンスに格納されるデータは、基底が重複しないものについて、
	 * 指定された配列の順序が維持される。
	 * <br>
	 * 同一の基底が複数含まれている場合、同一基底の値は、すべて加算される。
	 * このとき、配列の先頭からみて最初に出現した基底の位置に集約される。
	 * 
	 * <p><code>srcArray</code> は、次の条件で生成されている必要がある。
	 * <ul>
	 * <li><code>ExBase</code> と <code>BigDecimal</code> のインスタンスのみが含まれている。
	 * <li>配列の開始インデックスを 0 とすると、偶数インデックスに <code>ExBase</code> インスタンス、
	 *     奇数インデックスに <code>BigDecimal</code> インスタンスが格納されており、基底と値のペアに
	 *     より、交換代数要素が構成されている。
	 * <ol type="1" start="0">
	 * <li><code>ExBase</code> インスタンス
	 * <li><code>BigDecimal</code> インスタンス
	 * <li><code>ExBase</code> インスタンス
	 * <li><code>BigDecimal</code> インスタンス
	 * <li><code>ExBase</code> インスタンス
	 * <li><code>BigDecimal</code> インスタンス
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;・
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;・
	 * <li>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;・
	 * </ol>
	 * <li>指定された配列の要素数は偶数個(基底と値のペアが複数個)である。
	 * </ul>
	 * 
	 * @param srcArray 基底と値の連続した配列
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、
	 * 								同一基底の値との加算演算が行えない場合
	 * @throws IllegalArgumentException 次の場合に、この例外がスローされる。
	 * <ul>
	 * <li><code>ExBase</code> と <code>BigDecimal</code> 以外のインスタンスが含まれている。
	 * <li>基底と値が正しい順序で格納されていない。
	 * <li>基底のインスタンスが <tt>null</tt> である。
	 * </ul>
	 * 
	 * @since 0.90
	 */
	public Exalge(Object[] srcArray) {
		this();
		// Check
		if (srcArray == null)
			throw new NullPointerException();
		if ((srcArray.length % 2) != 0)
			throw new IllegalArgumentException("Value is nothing");
		// set values
		for (int i = 0; i < srcArray.length; i+=2) {
			// check class
			if (!(srcArray[i] instanceof ExBase))
				throw new IllegalArgumentException("srcArray[" + i + "] is not ExBase.class");
			if (srcArray[i+1]!=null && !(srcArray[i+1] instanceof BigDecimal))
				throw new IllegalArgumentException("srcArray[" + (i+1) + "] is not BigDecimal.class");
			// set element
			ExBase exbase = (ExBase)srcArray[i];
			BigDecimal exval = (BigDecimal)srcArray[i+1];
			this.plusValue(exbase, exval);
		}
	}

	/**
	 * 指定された基底と値のマップから <code>Exalge</code> の新しいインスタンスを生成する。
	 * 
	 * <p>このメソッドは、<code>ExBase</code> をキーとし、<code>BigDecimal</code> を値とする
	 * 交換代数要素のマップから、同じ内容(要素)を持つ <code>Exalge</code> の新しいインスタンスを
	 * 生成する。
	 * <p>
	 * 指定されたマップからの値取得には、<code>Iterator</code> を使用し、マップから取得した
	 * 順序で新しいインスタンスに格納される。
	 * 
	 * @param srcMap 基底と値のペアを保持するマップ
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、
	 * 								同一基底の値との加算演算が行えない場合
	 * 
	 * @since 0.90
	 */
	public Exalge(Map<ExBase,BigDecimal> srcMap) {
		this(srcMap.size());
		// set values
		Iterator<Entry<ExBase,BigDecimal>> it = srcMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<ExBase,BigDecimal> mapItem = it.next();
			ExBase exbase = mapItem.getKey();
			BigDecimal exval = mapItem.getValue();
			if (exbase != null) {
				this.putValue(exbase, exval);
			}
		}
	}

	/**
	 * 指定された <code>Exalge</code> インスタンスのデータコピーを持つ、
	 * <code>Exalge</code> の新しいインスタンスを生成する。
	 * 
	 * @param src	元になる <code>Exalge</code> インスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.90
	 */
	@SuppressWarnings("unchecked")
	private Exalge(Exalge src) {
		if (src == null) {
			throw new NullPointerException();
		}
		
		// データコピー
		this.data = (LinkedHashMap<ExBase,BigDecimal>)src.data.clone();
	}

	/**
	 * <code>Exalge</code> の新しいインスタンスを生成する。
	 * 
	 * @return <code>Exalge</code> の新しいインスタンス
	 * 
	 * @since 0.92
	 */
	protected Exalge newInstance() {
		return new Exalge();
	}
	
    /**
     * 指定された初期容量およびデフォルトの負荷係数で、<code>Exalge</code> の新しいインスタンスを生成する。
     * 
     * @param initialCapacity 交換代数元(マップ)の初期容量
     * @return <code>Exalge</code> の新しいインスタンス
     * 
     * @throws 初期容量が 0 より小さい場合にスローされる
     * 
     * @see java.util.LinkedHashMap#LinkedHashMap(int)
     * 
     * @since 0.94
     */
	protected Exalge newInstance(int initialCapacity) {
		return new Exalge(initialCapacity);
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	/**
	 * 交換代数の要素が空であることを示す。
	 * 
	 * @return 要素が一つも存在しない場合に true を返す。
	 * 
	 * @since 0.90
	 */
	public boolean isEmpty() {
		return this.data.isEmpty();
	}
	
	/**
	 * 交換代数の要素数を返す。
	 * 
	 * @return 交換代数の要素数
	 * 
	 * @since 0.90
	 */
	public int getNumElements() {
		return this.data.size();
	}

	/**
	 * 指定された基底が、このインスタンスの要素に含まれているかを示す。
	 * 
	 * @param base 交換代数基底
	 * @return 指定の基底が要素に含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @since 0.90
	 */
	public boolean containsBase(ExBase base) {
		return this.data.containsKey(base);
	}

	/**
	 * 指定された基底が、このインスタンスの要素に全て含まれているかを示す。
	 * 指定された基底集合の要素が空の場合、このメソッドは <tt>true</tt> を返す。
	 * 
	 * @param bases	交換代数基底集合
	 * @return	指定された基底集合の全ての基底が、この交換代数元に含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public boolean containsAllBases(ExBaseSet bases) {
		return this.data.keySet().containsAll(bases);
	}

	/**
	 * 指定された基底のどれか 1 つが、このインスタンスの要素に含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるどれか 1 つが、この
	 * 交換代数元の要素に存在した場合に <tt>true</tt> を返す。
	 * 
	 * @param bases	検証する基底の集合
	 * @return	指定された基底集合のどれか 1 つが含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 0.983
	 */
	public boolean containsAnyBases(ExBaseSet bases) {
		if (!bases.isEmpty()) {
			if (data.size() < bases.size()) {
				for (ExBase base : data.keySet()) {
					if (bases.contains(base)) {
						return true;
					}
				}
			} else {
				for (ExBase base : bases) {
					if (data.containsKey(base)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 指定された値が、このインスタンスの要素に含まれているかを示す。
	 * 
	 * @param value 交換代数の値
	 * @return 指定の値が要素に含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @since 0.90
	 */
	public boolean containsValue(BigDecimal value) {
		if (!data.isEmpty()) {
			if (value == null) {
				for (BigDecimal algeValue : data.values()) {
					if (algeValue == null) {
						return true;
					}
				}
			} else {
				for (BigDecimal algeValue : data.values()) {
					if (algeValue != null && 0==value.compareTo(algeValue)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * このインスタンスの要素に <tt>null</tt> 値が含まれている場合に <tt>true</tt> を返す。
	 * @return	このインスタンスの要素に <tt>null</tt> 値が含まれている場合は <tt>true</tt>
	 * 
	 * @since 0.983
	 */
	public boolean containsNull() {
		return data.containsValue(null);
	}

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * <code>Exalge</code> の要素の反復子を返す。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * 
	 * @return 交換代数要素の <code>Iterator</code>
	 * 
	 * @see ConcurrentModificationException
	 * 
	 * @since 0.94
	 */
	public Iterator<Exalge> iterator() {
		return newExalgeIterator();
	}

	/**
	 * 2つの交換代数が同値であるかを検証する。
	 * <br>
	 * このメソッドは、インスタンスの同一性ではなく、同値性を評価する。
	 * <br>{@link #isEqualValues(Exalge)} とは異なり、実数値 0 の基底も
	 * 評価対象となる。
	 * <br>
	 * 2つの交換代数が同値であるのは、次の場合となる。
	 * <ul>
	 * <li>2つの交換代数において、同一の基底が存在する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * 2つの <code>Exalge</code> インスタンスが返すハッシュコードが一致するとは
	 * 限らない。
	 * 
	 * @param alge 同値性を比較する対象の交換代数
	 * 
	 * @return 2つの交換代数が同値である場合は <tt>true</tt>
	 * 
	 * @since 0.93
	 */
	public boolean isSameValues(Exalge alge) {
		if (alge == null) {
			return false;
		}

		//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
		// 要素が空か判定
		if (this.isEmpty()) {
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
		Set<ExBase> thisBases = this.data.keySet();
		if (!thisBases.equals(alge.data.keySet())) {
			// 基底キーが異なるため、同値ではない
			return false;
		}
		
		// 全ての値を比較(null 値を許容)
		for (ExBase base : thisBases) {
			BigDecimal thisValue = this.data.get(base);
			BigDecimal algeValue = alge.data.get(base);
			if (thisValue!=algeValue && (thisValue==null || algeValue==null || thisValue.compareTo(algeValue)!=0)) {
				// どちらか一方が null、もしくは実数値が同値ではない
				return false;
			}
		}
		/*@@@ old codes : 2010.02.17 @@@
		int sizeThis = this.data.size();
		int sizeAlge = alge.data.size();
		if (sizeThis != sizeAlge) {
			// 基底の要素数が異なるため、同値ではない
			return false;
		}
		if (sizeThis <= 0) {
			// どちらも空の元なら、同値とみなす
			return true;
		}
		
		// 合成基底集合を生成
		HashSet<ExBase> allBases;
		if (sizeAlge > sizeThis) {
			allBases = new HashSet<ExBase>(alge.data.keySet());
			allBases.addAll(this.data.keySet());
		} else {
			allBases = new HashSet<ExBase>(this.data.keySet());
			allBases.addAll(alge.data.keySet());
		}
		
		// 全ての値を比較
		Iterator<ExBase> it = allBases.iterator();
		while (it.hasNext()) {
			ExBase base = it.next();
			BigDecimal thisValue = this.data.get(base);
			BigDecimal algeValue = alge.data.get(base);
			if (thisValue == null) {
				// 異なる基底を持つため、同値ではない
				return false;
			}
			else if (algeValue == null) {
				// 異なる基底を持つため、同値ではない
				return false;
			}
			else if (thisValue!=algeValue && (thisValue==null || algeValue==null || thisValue.compareTo(algeValue) != 0)) {
				// 同一基底で値が異なるため、同値ではない
				return false;
			}
		}
		**@@@ end of old codes : 2010.02.17 @@@*/
		//@@@ end of modified : 2010.02.17 @@@
		
		// 全て同値
		return true;
	}

	/**
	 * 2つの交換代数が等しいかを検証する。
	 * <br>
	 * このメソッドは、インスタンスの同一性ではなく、値としての同等性を評価する。
	 * <br>{@link #isSameValues(Exalge)} とは異なり、実数値 0 の基底は
	 * 評価対象としない。
	 * <br>
	 * 2つの交換代数が同等であるのは、次の場合となる。
	 * <ul>
	 * <li>要素の値が 0 のものは無視する。
	 * <li>要素の値が 0 ではないものの基底が、2つの交換代数において全て一致する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * 2つの <code>Exalge</code> インスタンスが返すハッシュコードが一致するとは
	 * 限らない。
	 * <b>注：</b>
	 * <blockquote>
	 * 値が <tt>null</tt> のものは、無視しない。
	 * </blockquote>
	 * 
	 * @param alge 同等性を比較する対象の交換代数
	 * 
	 * @return 2つの交換代数が等しい場合は <tt>true</tt>
	 * 
	 * @since 0.91
	 */
	public boolean isEqualValues(Exalge alge) {
		if (alge == null) {
			return false;
		}

		//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
		int sizeThis = this.data.size();
		int sizeAlge = alge.data.size();
		if (sizeThis <= 0 && sizeAlge <= 0) {
			// どちらも空の元なら、同等とみなす
			return true;
		}
		
		// 合成基底集合を生成
		HashSet<ExBase> allBases;
		if (sizeAlge > sizeThis) {
			allBases = new HashSet<ExBase>(alge.data.keySet());
			allBases.addAll(this.data.keySet());
		} else {
			allBases = new HashSet<ExBase>(this.data.keySet());
			allBases.addAll(alge.data.keySet());
		}

		// 全ての値を比較
		for (ExBase base : allBases) {
			boolean existThis = this.data.containsKey(base);
			boolean existAlge = alge.data.containsKey(base);
			BigDecimal thisValue = this.data.get(base);
			BigDecimal algeValue = alge.data.get(base);
			if (existThis) {
				//--- base exists in this
				if (existAlge) {
					//--- base exists in this & alge
					if (thisValue!=algeValue && (thisValue==null || algeValue==null || thisValue.compareTo(algeValue)!=0)) {
						// どちらか一方が null、もしくは実数値が同値ではない場合は、同等ではない
						return false;
					}
				} else {
					//--- base only exists in this
					if (thisValue==null || thisValue.compareTo(BigDecimal.ZERO)!=0) {
						// this にのみ存在する基底の値が 0 以外の場合は、同等ではない
						return false;
					}
				}
			}
			else {
				//--- base exists in alge
				if (algeValue==null || algeValue.compareTo(BigDecimal.ZERO)!=0) {
					// alge にのみ存在する基底の値が 0 以外の場合は、同等ではない
					return false;
				}
			}
		}
		/*@@@ old codes : 2010.02.17 @@@
		// 全ての値を比較
		Iterator<ExBase> it = allBases.iterator();
		while (it.hasNext()) {
			ExBase base = it.next();
			BigDecimal thisValue = this.data.get(base);
			BigDecimal algeValue = alge.data.get(base);
			if (thisValue == null) {
				if (algeValue.compareTo(BigDecimal.ZERO) != 0) {
					// 異なる基底と値を持つため、同等ではない
					return false;
				}
			}
			else if (algeValue == null) {
				if (thisValue.compareTo(BigDecimal.ZERO) != 0) {
					// 異なる基底と値を持つため、同等ではない
					return false;
				}
			}
			else if (thisValue.compareTo(algeValue) != 0) {
				// 同一基底で値が異なるため、同等ではない
				return false;
			}
		}
		**@@@ end of old codes : 2010.02.17 @@@*/
		//@@@ end of modified : 2010.02.17 @@@
		
		// 全て同値
		return true;
	}

	/**
	 * この交換代数元に含まれる基底を一つだけ取り出す。
	 * <p>
	 * このメソッドは、{@link #projection(ExBase)} によって要素が一つだけ
	 * 格納されている交換代数元から、その基底を取り出すために利用する。
	 * 取り出される基底は、このインスタンスの実装に依存する。そのため、
	 * 複数の要素が格納されている場合、どの基底が取り出されるかを保証する
	 * ものではない。
	 * <p>
	 * 要素が一つも存在しない場合、このメソッドは例外をスローする。
	 * 
	 * @return	この交換代数元に含まれる基底の一つを返す。
	 * 
	 * @throws java.util.NoSuchElementException	要素が存在しない場合
	 * 
	 * @since 0.970
	 */
	public ExBase getOneBase() {
		return this.data.keySet().iterator().next();
	}
	
	/**
	 * この交換代数元に含まれる全ての基底を取り出す。
	 * 
	 * @return この交換代数元に含まれる全ての基底の集合
	 * 
	 * @since 0.90
	 */
	public ExBaseSet getBases() {
		ExBaseSet retBases = new ExBaseSet(this.data.size());
		retBases.fastAddAll(this.data.keySet());
		return retBases;
	}
	
	/**
	 * 指定された文字列の配列に含まれるどれか一つの文字列と一致する
	 * 名前キーを持つ基底のみを取得する。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する名前キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * 
	 * @since 0.982
	 */
	public ExBaseSet getBasesByNameKey(String[] values) {
		return getBasesByBaseKey(ExBase.KEY_NAME, makeBaseKeyTargetSet(values));
	}
	
	/**
	 * 指定された文字列の配列に含まれるどれか一つの文字列と一致する
	 * 単位キーを持つ基底のみを取得する。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する単位キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * 
	 * @since 0.982
	 */
	public ExBaseSet getBasesByUnitKey(String[] values) {
		return getBasesByBaseKey(ExBase.KEY_EXT_UNIT, makeBaseKeyTargetSet(values));
	}
	
	/**
	 * 指定された文字列の配列に含まれるどれか一つの文字列と一致する
	 * 時間キーを持つ基底のみを取得する。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する時間キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * 
	 * @since 0.982
	 */
	public ExBaseSet getBasesByTimeKey(String[] values) {
		return getBasesByBaseKey(ExBase.KEY_EXT_TIME, makeBaseKeyTargetSet(values));
	}
	
	/**
	 * 指定された文字列の配列に含まれるどれか一つの文字列と一致する
	 * 主体キーを持つ基底のみを取得する。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する主体キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * 
	 * @since 0.982
	 */
	public ExBaseSet getBasesBySubjectKey(String[] values) {
		return getBasesByBaseKey(ExBase.KEY_EXT_SUBJECT, makeBaseKeyTargetSet(values));
	}

	/**
	 * この交換代数元に含まれる基底のうち、ハットなし基底のみを取り出す。
	 * 
	 * @return	この交換代数元に含まれるハットなし基底の集合
	 * 
	 * @since 0.970
	 */
	public ExBaseSet getNoHatBases() {
		ExBaseSet retBases = new ExBaseSet();
		for (ExBase base : this.data.keySet()) {
			if (base.isNoHat()) {
				retBases.fastAdd(base);
			}
		}
		return retBases;
	}

	/**
	 * この交換代数元に含まれる基底のうち、ハット基底のみを取り出す。
	 * 
	 * @return	この交換代数元に含まれるハット基底の集合
	 * 
	 * @since 0.970
	 */
	public ExBaseSet getHatBases() {
		ExBaseSet retBases = new ExBaseSet();
		for (ExBase base : this.data.keySet()) {
			if (base.isHat()) {
				retBases.fastAdd(base);
			}
		}
		return retBases;
	}

	/**
	 * この交換代数元から、全ての基底についてハットを除去した
	 * 基底集合を取得する。
	 * 
	 * @return	この交換代数元に含まれる全基底のハット除去後の基底集合
	 * 
	 * @since 0.970
	 */
	public ExBaseSet getBasesWithRemoveHat() {
		if (this.isEmpty()) {
			return new ExBaseSet();
		}
		else {
			ExBaseSet retBases = new ExBaseSet(this.data.size());
			for (ExBase base : this.data.keySet()) {
				retBases.fastAdd(base.removeHat());
			}
			return retBases;
		}
	}

	/**
	 * この交換代数元から、全ての基底についてハットを付加した
	 * 基底集合を取得する。
	 * 
	 * @return	この交換代数元に含まれる全基底のハット付加後の基底集合
	 * 
	 * @since 0.970
	 */
	public ExBaseSet getBasesWithSetHat() {
		if (this.isEmpty()) {
			return new ExBaseSet();
		}
		else {
			ExBaseSet retBases = new ExBaseSet(this.data.size());
			for (ExBase base : this.data.keySet()) {
				retBases.fastAdd(base.setHat());
			}
			return retBases;
		}
	}

	/**
	 * 指定された基底と対応する値を取り出す。
	 * <br>
	 * 要素に含まれていない基底を指定した場合は、0 を返す。
	 * <p>
	 * <b>(注)</b> 0 が返された場合でも、指定した基底が存在しないとは限らない。
	 * 実数値 0 の基底が要素に含まれている場合もある。
	 * 
	 * @param exbase 指定された基底
	 * @return 指定された基底と対応する値
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigDecimal get(ExBase exbase) {
		// Check
		if (exbase == null) {
			throw new NullPointerException();
		}

		// get
		//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
		return (data.containsKey(exbase) ? data.get(exbase) : BigDecimal.ZERO);
		/*@@@ old codes : 2010.02.17 @@@
		BigDecimal exval = data.get(exbase);
		return (exval==null ? BigDecimal.ZERO : exval);
		**@@@ end of old codes : 2010.02.17 @@@*/
		//@@@ end of modified : 2010.02.17 @@@
	}

	/**
	 * 指定された基底と対応する値を取り出す。
	 * 要素に含まれていない基底を指定した場合は、0 を返す。
	 * <p>
	 * このメソッドは、引数をチェックしないため、{@link #get(ExBase)}の高速版となる。
	 * 
	 * @param exbase	指定された基底
	 * @return	指定された基底と対応する値
	 * 
	 * @since 0.970
	 */
	protected BigDecimal fastGet(ExBase exbase) {
		//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
		return (data.containsKey(exbase) ? data.get(exbase) : BigDecimal.ZERO);
		/*@@@ old codes : 2010.02.17 @@@
		BigDecimal exval = data.get(exbase);
		return (exval==null ? BigDecimal.ZERO : exval);
		**@@@ end of old codes : 2010.02.17 @@@*/
		//@@@ end of modified : 2010.02.17 @@@
	}

	/**
	 * 指定された基底と値が代入された、Exalgeの新しいインスタンスを返す。
	 * <p>
	 * 指定した基底が存在していない場合、交換代数の値と基底のマップの終端に
	 * 追加される。
	 * 
	 * <p>すでに同一基底が存在する場合、指定された値で上書きされる。この場合、
	 * 基底の順序は影響を受けない。
	 * <br>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * 
	 * @param exbase 交換代数の基底
	 * @param value  値(<tt>null</tt> の場合は、そのまま)
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、
	 * 								同一基底の値との加算演算が行えない場合
	 * 
	 * @see exalge2.ExBase
	 * @see exalge2.ExBase#hat()
	 */
	public Exalge put(ExBase exbase, BigDecimal value) {
		// Check
		if (exbase == null) {
			throw new NullPointerException("exbase is null");
		}

		//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
		// Put
		Exalge newAlge = this.copy();
		newAlge.putValue(exbase, value);
		/*@@@ old codes : 2010.02.17 @@@
		// value
		BigDecimal newValue;
		if (value == null)
			newValue = BigDecimal.ZERO;
		else
			newValue = value;
		
		// Put
		Exalge newAlge = this.copy();
		newAlge.putValue(exbase, newValue);	// newAlge 書き換え
		**@@@ end of old codes : 2010.02.17 @@@*/
		//@@@ end of modified : 2010.02.17 @@@
		
		return newAlge;
	}

	/**
	 * 交換代数の要素の値が 0 のものを要素から削除した、Exalgeの新しいインスタンスを返す。
	 * 
	 * @return 要素の値が 0 のものを除いた Exalge インスタンス
	 * 
	 * @since 0.94
	 */
	public Exalge normalization() {
		Exalge result = null;
		if (!this.isEmpty()) {
			result = this.copy();
			Iterator<Map.Entry<ExBase,BigDecimal>> it = data.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<ExBase, BigDecimal> entry = it.next();
				if (entry.getValue().compareTo(BigDecimal.ZERO) == 0) {
					result.data.remove(entry.getKey());	// result 書き換え
				}
			}
		}
		else {
			result = this.newInstance(0);
		}
		return result;
	}

	/**
	 * @deprecated (0.94)このメソッドは、{@link #normalization()} に置き換えられました。
	 * 
	 * 交換代数の要素の値が 0 のものを要素から削除した、Exalgeの新しいインスタンスを返す。
	 * 
	 * @see #normalization()
	 * 
	 * @since 0.90
	 */
	public Exalge clean() {
		return normalization();
	}

	/**
	 * Exalgeの複製を生成する。
	 * 
	 * @return 複製されたExalge
	 */
	public Exalge copy() {
		return new Exalge(this);
	}
	
	/**
	 * 交換代数の全基底について、拡張基底キーの単位キーを置換する。
	 * <p>
	 * 変換の結果、同一のキーが複数存在する場合、全ての同一キーの値は加算される。
	 * このとき、既存の基底順序の最初に出現した基底の位置に集約される。
	 * 
	 * @param newKey 新しいキー。<tt>null</tt> もしくは、長さ 0 の文字列の場合、省略記号が代入される。
	 * 
	 * @return 単位キーが置き換えられたExalgeを返す
	 * 
	 * @since 0.90
	 */
	public Exalge replaceUnitKey(String newKey) {
		return replaceExtendedKey(ExtendedKeyID.UNIT, newKey);
	}
	
	/**
	 * 交換代数の全基底について、拡張基底キーの時間キーを置換する。
	 * <p>
	 * 変換の結果、同一のキーが複数存在する場合、全ての同一キーの値は加算される。
	 * このとき、既存の基底順序の最初に出現した基底の位置に集約される。
	 * 
	 * @param newKey 新しいキー。<tt>null</tt> もしくは、長さ 0 の文字列の場合、省略記号が代入される。
	 * 
	 * @return 時間キーが置き換えられたExalgeを返す
	 * 
	 * @since 0.90
	 */
	public Exalge replaceTimeKey(String newKey) {
		return replaceExtendedKey(ExtendedKeyID.TIME, newKey);
	}
	
	/**
	 * 交換代数の全基底について、指定された拡張基底キーを置換する。
	 * <p>
	 * 変換の結果、同一のキーが複数存在する場合、全ての同一キーの値は加算される。
	 * このとき、既存の基底順序の最初に出現した基底の位置に集約される。
	 * 
	 * @param keyid 置き換え対象の拡張基底キーのキーID。指定可能な値は、次のいずれかとなる。
	 * <ul>
	 * <li>{@link ExtendedKeyID#UNIT} - 単位キー
	 * <li>{@link ExtendedKeyID#TIME} - 時間キー
	 * <li>{@link ExtendedKeyID#SUBJECT} - サブジェクトキー
	 * </ul>
	 * @param newKey 新しいキー。<tt>null</tt> もしくは、長さ 0 の文字列の場合、省略記号が代入される。
	 * 
	 * @return 拡張基底キーが置き換えられたExalgeを返す
	 *
	 * @throws NullPointerException <tt>keyid</tt> が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.90
	 * 
	 * @see ExBase
	 * @see ExtendedKeyID
	 */
	public Exalge replaceExtendedKey(ExtendedKeyID keyid, String newKey) {
		// Check
		if (keyid == null)
			throw new NullPointerException("No keyid");

		// replace
		Exalge result = this.newInstance();
		Iterator<ExBase> it = data.keySet().iterator();
		while( it.hasNext()) {
			ExBase srcBase = it.next();
			BigDecimal value = data.get(srcBase);
			ExBase dstBase = srcBase.replaceExtendedKey(keyid, newKey);
			result.plusValue(dstBase, value);
		}
		return result;
	}

	/**
	 * 2つの交換代数が同値であるかを検証する。
	 * <br>
	 * このメソッドは{@link #isSameValues(Exalge)}と同様に、
	 * インスタンスの同一性ではなく、同値性を評価する。
	 * <br>{@link #isEqualValues(Exalge)} とは異なり、実数値 0 の基底も
	 * 評価対象となる。
	 * <br>
	 * 2つの交換代数が同値であるのは、次の場合となる。
	 * <ul>
	 * <li>2つの交換代数において、同一の基底が存在する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 
	 * @param obj 同値かを判定するオブジェクトの一方
	 * 
	 * @return 2つの交換代数が同値である場合は <tt>true</tt>
	 * 
	 * @since 0.94
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			// same instance
			return true;
		}
		
		if (!(obj instanceof Exalge)) {
			// not same class
			return false;
		}
		
		return isSameValues((Exalge)obj);
	}

	/**
	 * このインスタンスのハッシュ値を返す。
	 * 
	 * @return ハッシュ値
	 * 
	 * @since 0.94
	 */
	public int hashCode() {
		int h = 0;
		Iterator<Map.Entry<ExBase,BigDecimal>> eit = this.data.entrySet().iterator();
		while (eit.hasNext()) {
			Map.Entry<ExBase, BigDecimal> entry = eit.next();
			//--- キー(ExBase)のハッシュ値は、ExBase#hashCode() の値をそのまま利用する。
			int hk = (entry.getKey() == null ? 0 : entry.getKey().hashCode());
			//--- 値(BigDecimal)のハッシュ値は、BigDecimal#stripTrailingZeros() によって下位の桁の
			//--- 0(余分な0)を消去した後の BigDecimal#hashCode() の値とする。
			//--- これは、通常BigDecimalでは、値[1] と 値[1.00]とでことなるハッシュ値となるため。
			//--- 但し、値が "0"、".0"、"0.0"、"0.00" の場合、BigDecimal#stripTrailingZeros() の
			//--- 結果は変わらず、値 0 ではあるが全て異なるハッシュ値を返すため、値 0 の場合は
			//--- 強制的に 0 とする。
			int hv = ((entry.getValue() == null || BigDecimal.ZERO.compareTo(entry.getValue())==0)
					? 0
					: entry.getValue().stripTrailingZeros().hashCode());
			h += (hk ^ hv);
		}
    	return h;
	}
	
	/**
	 * このインスタンスの全要素を文字列として出力する。
	 * <br>
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
		StringBuffer sb = new StringBuffer();
		if (isEmpty()) {
			// 要素なし
			sb.append("()");
		}
		else {
			Iterator<Map.Entry<ExBase,BigDecimal>> it = data.entrySet().iterator();
			if (it.hasNext()) {
				Map.Entry<ExBase,BigDecimal> entry = it.next();
				if (entry.getValue()==null)
					sb.append("null");
				else
					sb.append(entry.getValue().stripTrailingZeros().toPlainString());
				sb.append(entry.getKey().toString());
			}
			while (it.hasNext()) {
				Map.Entry<ExBase,BigDecimal> entry = it.next();
				sb.append("+");
				if (entry.getValue()==null)
					sb.append("null");
				else
					sb.append(entry.getValue().stripTrailingZeros().toPlainString());
				sb.append(entry.getKey().toString());
			}
		}
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

	/**
	 * 交換代数の表示用メソッド
	 * 
	 * @deprecated 将来、削除される可能性がある。
	 */
	public void show() {
		System.out.print("交換代数＝");
		if (data.isEmpty()) {
			System.out.println("Empty");
		}
		else {
			Iterator<ExBase> it = data.keySet().iterator();
			if (it.hasNext()) {
				ExBase base = it.next();
				System.out.print(data.get(base));
				System.out.println(base.toFormattedString());
			}
			while (it.hasNext()) {
				ExBase base = it.next();
				System.out.print("　　　　＋");
				System.out.print(data.get(base));
				System.out.println(base.toFormattedString());
			}
		}
	}

	//------------------------------------------------------------
	// Operations
	//------------------------------------------------------------

	/**
	 * 指定された値と等しい要素のみを取り出す。<br>
	 * 指定の値と等しい要素が存在しない場合は、
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @param value	取り出す要素の値
	 * @return	指定された値と等しい要素のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @since 0.983
	 */
	public Exalge oneValueProjection(BigDecimal value) {
		Exalge newAlge = this.newInstance();
		if (!data.isEmpty()) {
			if (value == null) {
				for (Map.Entry<ExBase, BigDecimal> entry : data.entrySet()) {
					if (entry.getValue() == null) {
						newAlge.putValue(entry.getKey(), entry.getValue());
					}
				}
			} else {
				for (Map.Entry<ExBase, BigDecimal> entry : data.entrySet()) {
					BigDecimal algeValue = entry.getValue();
					if (algeValue != null && 0==algeValue.compareTo(value)) {
						newAlge.putValue(entry.getKey(), algeValue);
					}
				}
			}
		}
		return newAlge;
	}

	/**
	 * 指定されたコレクションに含まれる値と等しい要素のみを取り出す。<br>
	 * 指定の値と等しい要素が存在しない場合は、
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @param values	取り出す要素の値のコレクション
	 * @return	指定されたコレクションに含まれる値と等しい要素のみを含む
	 * 			<code>Exalge</code> の新しいインスタンス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 0.983
	 */
	public Exalge valuesProjection(Collection<? extends BigDecimal> values) {
		Exalge newAlge = this.newInstance();
		if (!values.isEmpty() && !data.isEmpty()) {
			for (Map.Entry<ExBase, BigDecimal> entry : data.entrySet()) {
				BigDecimal algeValue = entry.getValue();
				if (algeValue == null) {
					if (values.contains(algeValue)) {
						newAlge.putValue(entry.getKey(), algeValue);
					}
				} else {
					for (BigDecimal value : values) {
						if (value != null && 0==algeValue.compareTo(value)) {
							newAlge.putValue(entry.getKey(), algeValue);
							break;
						}
					}
				}
			}
		}
		return newAlge;
	}

	/**
	 * 値が <tt>null</tt> の要素のみを取り出す。<br>
	 * 値が <tt>null</tt> の要素が存在しない場合は、
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @return	値が <tt>null</tt> の要素のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @since 0.983
	 */
	public Exalge nullProjection() {
		Exalge newAlge = this.newInstance();
		if (!data.isEmpty()) {
			for (Map.Entry<ExBase, BigDecimal> entry : data.entrySet()) {
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
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @return	値が <tt>null</tt> ではない要素のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @since 0.983
	 */
	public Exalge nonullProjection() {
		Exalge newAlge = this.newInstance();
		if (!data.isEmpty()) {
			for (Map.Entry<ExBase, BigDecimal> entry : data.entrySet()) {
				if (entry.getValue() != null) {
					newAlge.putValue(entry.getKey(), entry.getValue());
				}
			}
		}
		return newAlge;
	}

	/**
	 * 値が 0 の要素のみを取り出す。<br>
	 * 値が 0 の要素が存在しない場合は、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @return	値が 0 の要素のみを含む <code>Exalge</code> の新しいインスタンス
	 * @since 0.984
	 */
	public Exalge zeroProjection() {
		Exalge newAlge = this.newInstance();
		if (!data.isEmpty()) {
			for (Map.Entry<ExBase, BigDecimal> entry : data.entrySet()) {
				BigDecimal value = entry.getValue();
				if (value != null && value.signum() == 0) {
					// extract zero value elements
					newAlge.putValue(entry.getKey(), value);
				}
			}
		}
		return newAlge;
	}
	
	/**
	 * 値が 0 ではない要素のみを取り出す。<br>
	 * 値が 0 ではない要素が存在しない場合は、
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @return	値が 0 ではない要素のみを含む <code>Exalge</code> の新しいインスタンス
	 * @since 0.984
	 */
	public Exalge notzeroProjection() {
		Exalge newAlge = this.newInstance();
		if (!data.isEmpty()) {
			for (Map.Entry<ExBase, BigDecimal> entry : data.entrySet()) {
				BigDecimal value = entry.getValue();
				if (value == null || value.signum() != 0) {
					// extract non-zero value elements
					newAlge.putValue(entry.getKey(), value);
				}
			}
		}
		return newAlge;
	}
	
	/**
	 * 指定の基底のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[base](this)
	 * </blockquote>
	 * 
	 * @param base 基底
	 * 
	 * @return 取り出した基底の値のみを含む <code>Exalge</code>の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.90
	 */
	public Exalge projection(ExBase base) {
		// Check
		if (base == null) {
			throw new NullPointerException();
		}

		// projection
		Exalge newAlge = this.newInstance();
		if (this.data.containsKey(base)) {
			BigDecimal exval = this.data.get(base);
			newAlge.putValue(base, exval);	// newAlge書き換え
		}
		return newAlge;
	}
	
	/**
	 * 指定の基底集合に含まれる基底のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[bases](this)
	 * </blockquote>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 維持される。
	 * 
	 * @param bases 基底集合
	 * 
	 * @return 取り出した基底の値のみを含む <code>Exalge</code>の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.90
	 */
	public Exalge projection(ExBaseSet bases) {
		// Check
		if (bases == null) {
			throw new NullPointerException();
		}
		
		// projection
		Exalge newAlge = this.copy();
		newAlge.data.keySet().retainAll(bases);
		/*---
		Exalge newAlge = this.newInstance();
		Iterator<ExBase> it = bases.iterator();
		while (it.hasNext()) {
			ExBase target = it.next();
			if (this.data.containsKey(target)) {
				BigDecimal exval = this.data.get(target);
				newAlge.putValue(target, this.data.get(target));	// newAlge書き換え
			}
		}
		---*/
		return newAlge;
	}

	/**
	 * 指定された基底とハット基底キー以外が一致する要素のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[removeHat(base) ∪ setHat(base)](this)
	 * </blockquote>
	 * 
	 * @param base	基底(ハット基底キーは無視される)
	 * @return	取り出した基底の値のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 0.960
	 */
	public Exalge generalProjection(ExBase base) {
		// Check
		if (base == null) {
			throw new NullPointerException();
		}
		
		// projection
		ExBase key;
		//BigDecimal value;
		Exalge newAlge = this.newInstance(2);
		//--- noHat base
		key = base.removeHat();
		//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
		if (this.data.containsKey(key)) {
			newAlge.data.put(key, this.data.get(key));
		}
		/*@@@ old codes : 2010.02.17 @@@
		value = this.data.get(key);
		if (value != null) {
			newAlge.data.put(key, value);
		}
		**@@@ end of old codes : 2010.02.17 @@@*/
		//@@@ end of modified : 2010.02.17 @@@
		//--- hat base
		key = base.setHat();
		//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
		if (this.data.containsKey(key)) {
			newAlge.data.put(key, this.data.get(key));
		}
		/*@@@ old codes : 2010.02.17 @@@
		value = this.data.get(key);
		if (value != null) {
			newAlge.data.put(key, value);
		}
		**@@@ end of old codes : 2010.02.17 @@@*/
		//@@@ end of modified : 2010.02.17 @@@
		
		return newAlge;
	}
	
	/**
	 * 指定された基底集合に含まれる基底の、ハット基底キー以外が一致する要素のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[removeHat(bases) ∪ setHat(bases)](this)
	 * </blockquote>
	 * 
	 * @param bases	基底集合(ハット基底キーは無視される)
	 * @return	取り出した基底の値のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 0.960
	 */
	public Exalge generalProjection(ExBaseSet bases) {
		// Check
		if (bases == null) {
			throw new NullPointerException();
		}
		
		// make target bases
		ExBaseSet targetBases = bases.removeHat();
		targetBases.fastAddAll(targetBases.setHat());
		
		// projection
		return projection(targetBases);
	}

	/**
	 * 指定の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された基底のハットキー以外の基底キーに一致する基底のみを
	 * 取得し、その集合を返す。ハットキー以外の基底キーにアスタリスク
	 * 文字<code>('*')</code>が含まれている場合、0文字以上の任意の文字に
	 * マッチするワイルドカードとみなす。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param base	基底パターンとみなす交換代数基底
	 * @return		パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.94
	 */
	public Exalge patternProjection(ExBase base) {
		ExBasePattern pattern = new ExBasePattern(base, true);
		return patternProjection(pattern);
	}

	/**
	 * 指定の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された基底パターンに一致する基底のみを取得し、その基底のみを
	 * 持つ交換代数元を返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param pattern	基底パターン
	 * @return			パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.94
	 */
	public Exalge patternProjection(ExBasePattern pattern) {
		ExBaseSet matchedBases = this.getBases().getMatchedBases(pattern);
		Exalge newAlge;
		if (matchedBases.isEmpty()) {
			newAlge = this.newInstance(0);
		} else {
			newAlge = this.projection(matchedBases);
		}
		return newAlge;
	}

	/**
	 * 指定の集合の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された集合に含まれる基底の、ハットキー以外の基底キーに
	 * 一致する基底のみを取得し、その集合を返す。ハットキー以外の
	 * 基底キーにアスタリスク文字<code>('*')</code>が含まれている場合、
	 * 0文字以上の任意の文字にマッチするワイルドカードとみなす。
	 * <br>
	 * 返される交換代数元に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param bases	基底パターンとみなす基底の集合
	 * @return		パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.94
	 */
	public Exalge patternProjection(ExBaseSet bases) {
		ExBasePatternSet patterns = new ExBasePatternSet(bases, true);
		return patternProjection(patterns);
	}

	/**
	 * 指定の集合の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された基底パターンに一致する基底のみを取得し、その基底のみを
	 * 持つ交換代数元を返す。
	 * <br>
	 * 返される交換代数元に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return		パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.94
	 */
	public Exalge patternProjection(ExBasePatternSet patterns) {
		ExBaseSet matchedBases = this.getBases().getMatchedBases(patterns);
		Exalge newAlge;
		if (matchedBases.isEmpty()) {
			newAlge = this.newInstance(0);
		} else {
			newAlge = this.projection(matchedBases);
		}
		return newAlge;
	}
	
	/**
	 * 指定された文字列配列のどれか一つに一致する名前キーを持つ要素のみを取り出す。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する名前キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * 
	 * @since 0.982
	 */
	public Exalge projectionByNameKey(String[] values) {
		return projectionByBaseKey(ExBase.KEY_NAME, makeBaseKeyTargetSet(values));
	}
	
	/**
	 * 指定された文字列配列のどれか一つに一致する単位キーを持つ要素のみを取り出す。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する単位キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * 
	 * @since 0.982
	 */
	public Exalge projectionByUnitKey(String[] values) {
		return projectionByBaseKey(ExBase.KEY_EXT_UNIT, makeBaseKeyTargetSet(values));
	}
	
	/**
	 * 指定された文字列配列のどれか一つに一致する時間キーを持つ要素のみを取り出す。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する時間キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * 
	 * @since 0.982
	 */
	public Exalge projectionByTimeKey(String[] values) {
		return projectionByBaseKey(ExBase.KEY_EXT_TIME, makeBaseKeyTargetSet(values));
	}
	
	/**
	 * 指定された文字列配列のどれか一つに一致する主体キーを持つ要素のみを取り出す。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する主体キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 * 
	 * @since 0.982
	 */
	public Exalge projectionBySubjectKey(String[] values) {
		return projectionByBaseKey(ExBase.KEY_EXT_SUBJECT, makeBaseKeyTargetSet(values));
	}

	/**
	 * 指定の基底と値を加算した結果を持つ、<code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) + value&lt;exbase&gt;
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 加算する値の基底が存在しない場合、交換代数の値と基底のマップの終端に
	 * 追加される。
	 * 
	 * @param exbase 指定された基底
	 * @param value 値
	 * @return 加算の結果
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 */
	public Exalge plus(ExBase exbase, BigDecimal value) {
		// Check
		if (exbase == null)
			throw new NullPointerException("exbase is null");
		// plus
		Exalge result = this.copy();
		result.plusValue(exbase, value);
		return result;
	}

	/**
	 * 指定の交換代数を加算した結果を持つ、<code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) + (plusData)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 加算する値の基底が存在しない場合、交換代数の値と基底のマップの終端に、
	 * 指定された交換代数(plusData)に格納されている順序で追加される。
	 * 
	 * @param plusData　加算される交換代数
	 * @return　加算された後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 */
	public Exalge plus(Exalge plusData) {
		Exalge result = this.copy();
		result.plusValues(plusData);
		return result;
	}

	/**
	 * 指定された基底と値を、交換代数に加算する。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 *<p>
	 * 値がマイナスの時、自動的に ^ 計算を行って値をプラスにするように設計されている。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param base 交換代数の基底
	 * @param value 値
	 * @return このオブジェクト
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 * 
	 * @since 0.984
	 */
	public Exalge add(ExBase base, BigDecimal value) {
		// Check
		if (base == null)
			throw new NullPointerException("ExBase is null");
		// plus
		plusValue(base, value);
		return this;
	}

	/**
	 * 指定された交換代数の元を、この交換代数に加算する。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param alge 加算する交換代数の元
	 * @return このオブジェクト
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 * 
	 * @since 0.984
	 */
	public Exalge add(Exalge alge) {
		plusValues(alge);
		return this;
	}

	/**
	 * 交換代数元の逆元を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数の逆元は、次のように定義されている。
	 * <blockquote>
	 * x = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * invElement(x) = C&lt;e&gt; + D^&lt;e&gt;<br>
	 * このとき、C と D は、a と b の関係によって、次のように定義される。<br>
	 * a &gt; b のとき、C=1/(a-b)、D=0<br>
	 * a &lt; b のとき、C=0、D=1/(b-a)<br>
	 * a = b のとき、C=0、D=0
	 * </blockquote>
	 * この逆元は、交換代数元の逆数と次のような関係が成立する。
	 * <blockquote>
	 * invElement(x) = inverse(~x)
	 * </blockquote>
	 * 交換代数元の逆元の演算結果から、0値をもつ要素は除外される。
	 * 
	 * @return この交換代数元の逆元となる、新しい交換代数元を返す。
	 * 
	 * @since 0.970
	 */
	public Exalge invElement() {
		Exalge newAlge = this.newInstance();
		if (!data.isEmpty()) {
			// 基底の収集
			ExBaseSet nohatBases = new ExBaseSet(data.size());
			for (ExBase base : data.keySet()) {
				nohatBases.fastAdd(base.removeHat());
			}
			
			// 逆元の計算[ai>bi:di=0,ci=1/(ai-bi) ; ai<bi:ci=0,di=1/(bi-ai) ; ai=bi:ci=0,di=0]
			for (ExBase nBase : nohatBases) {
				ExBase hBase = nBase.setHat();
				BigDecimal a = fastGet(nBase);
				BigDecimal b = fastGet(hBase);
				int comp = a.compareTo(b);
				if (comp > 0) {
					//--- a > b : (1/(a-b))<e>
					newAlge.data.put(nBase, BigDecimal.ONE.divide(a.subtract(b), MathContext.DECIMAL128));
				}
				else if (comp < 0) {
					//--- a < b : (1/(b-a))^<e>
					newAlge.data.put(hBase, BigDecimal.ONE.divide(b.subtract(a), MathContext.DECIMAL128));
				}
				// else //--- a == b : 結果が 0 となるので、結果に含めない
			}
		}
		return newAlge;
	}

	/**
	 * 交換代数元の積(要素積)を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数元の積は、次のように定義されている。
	 * <blockquote>
	 * v = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * w = c&lt;e&gt; + d^&lt;e&gt;<br>
	 * v*w = (ac + bd)&lt;e&gt; + (ad + bc)^&lt;e&gt;<br>
	 * </blockquote>
	 * このとき、バー(~)演算は以下の等式が成立する。
	 * <blockquote>
	 * (~v)*(~w) = ~(v*w)
	 * </blockquote>
	 * 従って、交換代数元の積は、次の演算を行うものとなる。
	 * <blockquote>
	 * bases = (this.getBases() ∪ x.getBases()).removeHat()<br>
	 * (return) = (this) * (x)<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;= ∑{ v*w | bs&lt;-bases, v=this.generalProjection(bs), w=x.generalProjection(bs)}
	 * </blockquote>
	 * また、積の定義において対となる基底が存在しない場合は、0値を持つ要素とみなし、
	 * 演算した結果から0値を持つ要素を除外する。例として、次のような元の積を示す。
	 * <blockquote>
	 * (a&lt;e&gt; + b^&lt;e&gt;) * (c&lt;e&gt;) → (a&lt;e&gt; + b^&lt;e&gt;) * (c&lt;e&gt; + 0^&lt;e&gt;) = ac&lt;e&gt; + bc^&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt; + b^&lt;e&gt;) * (d^&lt;e&gt;) → (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = bd&lt;e&gt; + ad^&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt;) * (d^&lt;e&gt;) → (a&lt;e&gt; + 0^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = 0&lt;e&gt; + ad^&lt;e&gt; → ad^&lt;e&gt;
	 * <br>
	 * (b^&lt;e&gt;) * (d^&lt;e&gt;) → (0&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = bd&lt;e&gt; + 0^&lt;e&gt; → bd&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt;) * () → (a&lt;e&gt; + 0^&lt;e&gt;) * (0&lt;e&gt; + 0^&lt;e&gt;) = 0&lt;e&gt; + 0^&lt;e&gt; → ()
	 * </blockquote>
	 * 
	 * @param x	この交換代数元との積をとる、交換代数元の一方
	 * @return	演算後の交換代数
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public Exalge multiple(Exalge x) {
		if (x == null) {
			throw new NullPointerException();
		}
		
		if (this.isEmpty() || x.isEmpty()) {
			//--- どちらか一方が存在しない場合は、空の交換代数元を返す
			return newInstance(0);
		}
		
		// 計算する基底を収集(素直な演算)
		/*---
		ExBaseSet nohatBases = new ExBaseSet(this.getNumElements() * x.getNumElements());
		for (ExBase base : this.data.keySet()) {
			nohatBases.fastAdd(base.removeHat());
		}
		for (ExBase base : x.data.keySet()) {
			nohatBases.fastAdd(base.removeHat());
		}
		
		// 格納する元の生成
		Exalge retAlge = newInstance();
		
		// 積の演算[ (a<e> + b^<e>) * (c<e> + d^<e>) = (ac+bd)<e> + (ad+bc)^<e> ]
		for (ExBase nBase : nohatBases) {
			ExBase hBase = nBase.setHat();
			BigDecimal a = this.get(nBase);
			BigDecimal b = this.get(hBase);
			BigDecimal c = x.get(nBase);
			BigDecimal d = x.get(hBase);
			BigDecimal nValue = a.multiply(c).add(b.multiply(d));
			BigDecimal hValue = a.multiply(d).add(b.multiply(c));
			if (BigDecimal.ZERO.compareTo(nValue) != 0) {
				retAlge.putValue(nBase, nValue);
			}
			if (BigDecimal.ZERO.compareTo(hValue) != 0) {
				retAlge.putValue(hBase, hValue);
			}
		}
		---*/
		
		// 積の演算(存在基底のみ)
		//    基本的に、両方に存在する基底のみが対象(一方のみに存在するものは0値要素)となるので、
		//    片側にしか存在しない基底セットは無視できる
		//    → 要素が少ないほうのハットを除去した基底のみで操作すれば良い。
		//--- 計算対象基底の収集
		ExBaseSet nohatBases;
		int numThis = this.getNumElements();
		int numX    = x.getNumElements();
		if (numThis > numX) {
			nohatBases = new ExBaseSet(numX);
			for (ExBase base : x.data.keySet()) {
				nohatBases.fastAdd(base.removeHat());
			}
		} else {
			nohatBases = new ExBaseSet(numThis);
			for (ExBase base : this.data.keySet()) {
				nohatBases.fastAdd(base.removeHat());
			}
		}
		//--- 結果を格納する元の生成
		Exalge retAlge = newInstance();
		//--- 積の演算[ (a<e> + b^<e>) * (c<e> + d^<e>) = (ac+bd)<e> + (ad+bc)^<e> ]
		for (ExBase nBase : nohatBases) {
			ExBase hBase = nBase.setHat();
			BigDecimal a = fastGet(nBase);
			BigDecimal b = fastGet(hBase);
			BigDecimal c = x.fastGet(nBase);
			BigDecimal d = x.fastGet(hBase);
			BigDecimal nValue = a.multiply(c).add(b.multiply(d));
			BigDecimal hValue = a.multiply(d).add(b.multiply(c));
			if (BigDecimal.ZERO.compareTo(nValue) != 0) {
				retAlge.data.put(nBase, nValue);
			}
			if (BigDecimal.ZERO.compareTo(hValue) != 0) {
				retAlge.data.put(hBase, hValue);
			}
		}
		
		// 完了
		return retAlge;
	}

	/**
	 * 交換代数の全要素に対して指定の値を乗算した結果を持つ、
	 * <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 演算結果が 0 となる場合でも、その要素は保持される。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) × (x)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 
	 * @param x　乗算の倍率(正の値であること)
	 * 
	 * @return　乗算された後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws ArithmeticException 引数に負の値が指定された場合にスローされる
	 */
	public Exalge multiple(BigDecimal x) {
		// Check
		if (x.compareTo(BigDecimal.ZERO) < 0) {
			// minus value
			throw new ArithmeticException("Multiply by minus value");
		}
		
		// Multiple
		Exalge result = this.newInstance();
		Iterator<ExBase> it = this.data.keySet().iterator();
		while (it.hasNext()) {
			ExBase exbase = it.next();
			BigDecimal value = this.get(exbase).multiply(x);
			result.putValue(exbase, value);	// result 書き換え
		}
		return result;
	}
	
	/**
	 * 交換代数元の商(要素商)を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数元の商は、交換代数の逆元を用いて次のように定義されている。
	 * <blockquote>
	 * v = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * w = c&lt;e&gt; + d^&lt;e&gt;<br>
	 * v/w = v * invElement(w) = (ac + bd)&lt;e&gt; + (ad + bc)^&lt;e&gt;<br>
	 * (c &gt; d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * ((1/(c-d))&lt;e&gt; + 0^&lt;e&gt;) = a(1/(c-d))&lt;e&gt; + b(1/(c-d))^&lt;e&gt;<br>
	 * (c &lt; d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + (1/(d-c))^&lt;e&gt;) = b(1/(d-c))&lt;e&gt; + a(1/(d-c))^&lt;e&gt;<br>
	 * (c = d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + 0^&lt;e&gt;) = 0&lt;e&gt; + 0^&lt;e&gt;
	 * </blockquote>
	 * 商の定義において対となる基底が存在しない場合は、0値を持つ要素とみなし、
	 * 演算した結果から0値を持つ要素を除外する。
	 * 
	 * @param x	この交換代数元との商をとる、交換代数元の一方
	 * @return	演算後の交換代数
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public Exalge divide(Exalge x) {
		if (x == null) {
			throw new NullPointerException();
		}
		
		if (this.isEmpty() || x.isEmpty()) {
			//--- どちらか一方が存在しない場合は、空の交換代数元を返す
			return newInstance(0);
		}
		
		// 商の演算(存在基底のみ)
		//    基本的に、両方に存在する基底のみが対象(一方のみに存在するものは0値要素)となるので、
		//    片側にしか存在しない基底セットは無視できる
		//    → 要素が少ないほうのハットを除去した基底のみで操作すれば良い。
		//--- 計算対象基底の収集
		ExBaseSet nohatBases;
		int numThis = this.getNumElements();
		int numX    = x.getNumElements();
		if (numThis > numX) {
			nohatBases = new ExBaseSet(numX);
			for (ExBase base : x.data.keySet()) {
				nohatBases.fastAdd(base.removeHat());
			}
		} else {
			nohatBases = new ExBaseSet(numThis);
			for (ExBase base : this.data.keySet()) {
				nohatBases.fastAdd(base.removeHat());
			}
		}
		//--- 結果を格納する元の生成
		Exalge retAlge = newInstance();
		//--- 商の演算[ (a<e> + b^<e>) / (c<e> + d^<e>) =
		//              if (c>d) := (a<e> + b^<e>) * (1/(c-d))<e>  = a(1/(c-d))<e> + b(1/(c-d))^<e>
		//              if (c<d) := (a<e> + b^<e>) * (1/(d-c))^<e> = b(1/(d-c))<e> + a(1/(d-c))^<e>
		//              if (c=d) := 0<e> + 0^<e>
		for (ExBase nBase : nohatBases) {
			ExBase hBase = nBase.setHat();
			BigDecimal a = fastGet(nBase);
			BigDecimal b = fastGet(hBase);
			BigDecimal c = x.fastGet(nBase);
			BigDecimal d = x.fastGet(hBase);
			int comp = c.compareTo(d);
			if (comp > 0) {
				//--- c > d : a(1/(c-d))<e> + b(1/(c-d))^<e>
				BigDecimal inv = BigDecimal.ONE.divide(c.subtract(d), MathContext.DECIMAL128);
				BigDecimal nValue = a.multiply(inv);
				BigDecimal hValue = b.multiply(inv);
				if (BigDecimal.ZERO.compareTo(nValue) != 0) {
					retAlge.data.put(nBase, nValue);
				}
				if (BigDecimal.ZERO.compareTo(hValue) != 0) {
					retAlge.data.put(hBase, hValue);
				}
			}
			else if (comp < 0) {
				//--- b(1/(d-c))<e> + a(1/(d-c))^<e>
				BigDecimal inv = BigDecimal.ONE.divide(d.subtract(c), MathContext.DECIMAL128);
				BigDecimal nValue = b.multiply(inv);
				BigDecimal hValue = a.multiply(inv);
				if (BigDecimal.ZERO.compareTo(nValue) != 0) {
					retAlge.data.put(nBase, nValue);
				}
				if (BigDecimal.ZERO.compareTo(hValue) != 0) {
					retAlge.data.put(hBase, hValue);
				}
			}
		}
		
		// 完了
		return retAlge;
	}

	/**
	 * 交換代数の全要素に対して指定の値で除算した結果を持つ、
	 * <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 除算においては、{@link MathContext#DECIMAL128} の精度で結果を保持する。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) ÷ (x)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 
	 * @param x 除算する値(正の値であること)
	 * 
	 * @return 除算された後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws ArithmeticException 引数の 0 もしくは負の値が指定された場合にスローされる
	 * 
	 * @since 0.90
	 */
	public Exalge divide(BigDecimal x) {
		// Check
		int cmp = x.compareTo(BigDecimal.ZERO);
		if (cmp == 0) {
			// by Zero
			throw new ArithmeticException("Division by zero");
		} else if (cmp < 0) {
			// minus value
			throw new ArithmeticException("Division by minus value");
		}
		
		// Divide
		Exalge result = this.newInstance();
		Iterator<ExBase> it = this.data.keySet().iterator();
		while (it.hasNext()) {
			ExBase exbase = it.next();
			BigDecimal value = this.get(exbase).divide(x, MathContext.DECIMAL128);	// double と同程度の精度による桁制限＆丸め
			result.putValue(exbase, value);	// result 書き換え
		}
		return result;
	}
	
	/**
	 * 交換代数の要素積を演算する。
	 * 
	 * <p>
	 * 交換代数の要素積は、2 つの交換代数において、基本基底キー(名前キーおよび
	 * ハットキー)が一致するものを要素ペアとし、要素のペアの値を乗算する。
	 * <br>
	 * 要素ペアにおいて、拡張基底キーが一致しない場合、一致しない拡張基底キーが
	 * 省略記号('#')に置き換えられた基底に変換される。
	 * <br>
	 * 要素積を演算する場合は、次の条件が必要となる。
	 * <ul>
	 * <li>交換代数(Exalge)内で、拡張基底キーが統一
	 * (同一交換代数内の全要素において拡張基底キーがすべて同一)されていること。
	 * <li>交換代数(Exalge)内で、基本基底キーが重複していないこと。
	 * </ul>
	 * 上記の条件に一致しない交換代数が指定された場合は、例外をスローする。
	 * <p>
	 * 要素ペアが存在し、演算結果の実数値が 0 となるものは、演算結果に含まれる。
	 * <br>
	 * 要素ペアが存在しない要素は、演算結果には含まれないものとする。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) @ (alge)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 
	 * @param alge 要素積を計算する交換代数の一方
	 * @return 計算結果の交換代数
	 * 
	 * @throws NullPointerException <code>alge</code> が <tt>null</tt> の場合にスローされる
	 * @throws ArithmeticException 要素積演算の条件が満たされていない場合にスローされる
	 * 
	 * @since 0.90
	 * 
	 * @deprecated 交換代数元の積 {@link #multiple(Exalge)} が定義されたため、このメソッドは削除されます。
	 */
	public Exalge elementMultiple(Exalge alge) {
		return elementMultiple(alge, null, null);
	}
	
	/**
	 * 交換代数の要素積を演算し、拡張基底キーの単位キーを指定のキーに置き換える。
	 * <p>
	 * 交換代数の要素積は、2 つの交換代数において、基本基底キー(名前キーおよび
	 * ハットキー)が一致するものを要素ペアとし、要素のペアの値を乗算する。
	 * <br>
	 * 要素ペアにおいて、拡張基底キーが一致しない場合、一致しない拡張基底キーが
	 * 省略記号('#')に置き換えられた基底に変換される。
	 * <br>
	 * 要素積を演算する場合は、次の条件が必要となる。
	 * <ul>
	 * <li>交換代数(Exalge)内で、拡張基底キーが統一
	 * (同一交換代数内の全要素において拡張基底キーがすべて同一)されていること。
	 * <li>交換代数(Exalge)内で、基本基底キーが重複していないこと。
	 * </ul>
	 * 上記の条件に一致しない交換代数が指定された場合は、例外をスローする。
	 * <p>
	 * 要素ペアが存在し、演算結果の実数値が 0 となるものは、演算結果に含まれる。
	 * <br>
	 * 要素ペアが存在しない要素は、演算結果には含まれないものとする。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) @ (alge)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 
	 * @param alge 要素積を計算する交換代数の一方
	 * @param unitKey 置き換える新しい単位キー。<tt>null</tt>の場合、強制的に省略記号に置き換えられる。
	 * 
	 * @return 計算結果の交換代数
	 * 
	 * @throws NullPointerException <code>alge</code> が <tt>null</tt> の場合にスローされる
	 * @throws ArithmeticException 要素積演算の条件が満たされていない場合にスローされる
	 * 
	 * @since 0.90
	 * 
	 * @deprecated 交換代数元の積 {@link #multiple(Exalge)} が定義されたため、このメソッドは削除されます。
	 */
	public Exalge elementMultiple(Exalge alge, String unitKey) {
		return elementMultiple(alge, ExtendedKeyID.UNIT, unitKey);
	}
	
	/**
	 * 交換代数の要素積を演算し、指定された拡張基底キーを指定のキーに置き換える。
	 * <p>
	 * 交換代数の要素積は、2 つの交換代数において、基本基底キー(名前キーおよび
	 * ハットキー)が一致するものを要素ペアとし、要素のペアの値を乗算する。
	 * <br>
	 * 要素ペアにおいて、拡張基底キーが一致しない場合、一致しない拡張基底キーが
	 * 省略記号('#')に置き換えられた基底に変換される。
	 * <br>
	 * 要素積を演算する場合は、次の条件が必要となる。
	 * <ul>
	 * <li>交換代数(Exalge)内で、拡張基底キーが統一
	 * (同一交換代数内の全要素において拡張基底キーがすべて同一)されていること。
	 * <li>交換代数(Exalge)内で、基本基底キーが重複していないこと。
	 * </ul>
	 * 上記の条件に一致しない交換代数が指定された場合は、例外をスローする。
	 * <p>
	 * 要素ペアが存在し、演算結果の実数値が 0 となるものは、演算結果に含まれる。
	 * <br>
	 * 要素ペアが存在しない要素は、演算結果には含まれないものとする。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) @ (alge)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 
	 * @param alge 要素積を計算する交換代数の一方
	 * @param keyid 置き換え対象の拡張基底キーのキーID。指定可能な値は、次のいずれかとなる。
	 * <ul>
	 * <li>{@link ExtendedKeyID#UNIT} - 単位キー
	 * <li>{@link ExtendedKeyID#TIME} - 時間キー
	 * <li>{@link ExtendedKeyID#SUBJECT} - サブジェクトキー
	 * </ul>
	 * この値が <tt>null</tt> の場合、拡張基底キーの置き換えは行われない。
	 * @param newKey 置き換える新しい単位キー。<tt>null</tt>の場合、強制的に省略記号に置き換えられる。
	 * 
	 * @return 計算結果の交換代数
	 * 
	 * @throws NullPointerException <code>alge</code> が <tt>null</tt> の場合にスローされる
	 * @throws ArithmeticException 要素積演算の条件が満たされていない場合にスローされる
	 * 
	 * @since 0.90
	 * 
	 * @deprecated 交換代数元の積 {@link #multiple(Exalge)} が定義されたため、このメソッドは削除されます。
	 */
	public Exalge elementMultiple(Exalge alge, ExtendedKeyID keyid, String newKey) {
		// 名前キーの重複チェックと、ペア生成のためのマップ
		//--- マップ生成(this)
		LinkedHashMap<String,ExBase> thisNameMap;
		try {
			thisNameMap = this.createBasicKeyMapForElementMultiply();
		} catch (ArithmeticException ex) {
			throw new ArithmeticException("This Exalge " + ex.getMessage());
		}
		//--- マップ生成(this)
		LinkedHashMap<String,ExBase> algeNameMap;
		try {
			algeNameMap = alge.createBasicKeyMapForElementMultiply();
		} catch (ArithmeticException ex) {
			throw new ArithmeticException("Argument Exalge " + ex.getMessage());
		}

		// 要素積の演算
		Exalge ret = this.newInstance();
		Iterator<String> it = thisNameMap.keySet().iterator();
		while (it.hasNext()) {
			String nameKey = it.next();
			if (algeNameMap.containsKey(nameKey)) {
				ExBase thisBase = thisNameMap.get(nameKey);
				ExBase algeBase = algeNameMap.get(nameKey);
				BigDecimal retValue = this.get(thisBase).multiply(alge.get(algeBase));
				//--- setup new ExBase
				ExBase newBase;
				if (keyid != null) {
					//--- marge & replace
					newBase = thisBase.margeExtendedKeys(algeBase, keyid, newKey);
				} else {
					//--- marge only
					newBase = thisBase.margeExtendedKeys(algeBase);
				}
				//--- set result
				//ret.putValue(newBase, retValue);
				ret.plusValue(newBase, retValue);
			}
		}
		return ret;
	}

	/**
	 * 要素積演算のための、基本基底キーマップを生成する。
	 * <br>
	 * 要素積演算の条件を満たさない要素が含まれている場合は、例外をスローする。
	 * 
	 * @return 要素積演算用の基本基底キーマップ
	 * 
	 * @throws ArithmeticException 要素積演算の条件が満たされていない場合にスローされる
	 * 
	 * @since 0.90
	 * 
	 * @deprecated 交換代数元の積 {@link #multiple(Exalge)} が定義されたため、このメソッドは削除されます。
	 */
	protected LinkedHashMap<String,ExBase> createBasicKeyMapForElementMultiply() {
		LinkedHashMap<String,ExBase> map = new LinkedHashMap<String,ExBase>();
		String strExtendedKeys = null;
		Iterator<ExBase> it = data.keySet().iterator();
		while (it.hasNext()) {
			ExBase base = it.next();
			//--- Check dimension
			if (strExtendedKeys != null) {
				if (!strExtendedKeys.equals(base.getExtendedKeysString())) {
					throw new ArithmeticException("is not the same dimension");
				}
			} else {
				strExtendedKeys = base.getExtendedKeysString();
			}
			//--- Check basic keys
			String strBasicKeys = base.getBasicKeysString();
			if (map.containsKey(strBasicKeys)) {
				throw new ArithmeticException("overlapped basic keys");
			}
			map.put(strBasicKeys, base);
		}
		return map;
	}

	/**
	 * 交換代数のノルム計算、交換代数内の値の総和
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = |(this)|
	 * </blockquote>
	 * 
	 * @return　値の総和
	 */
	public BigDecimal norm() {
		
		BigDecimal norm = BigDecimal.ZERO;
		Iterator<ExBase> it = this.data.keySet().iterator();
		while (it.hasNext()) {
			ExBase exbase = it.next();
			norm = norm.add(this.data.get(exbase));
		}
		return norm;
	}

	/**
	 * 交換代数の＾計算、すべての項目に対して、＾の部分の演算
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = ^ (this)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 
	 * @return　＾計算された後の交換代数
	 */
	public Exalge hat() {
		Exalge result = this.newInstance();
		Iterator<ExBase> it = this.data.keySet().iterator();
		while (it.hasNext()) {
			ExBase exbase = it.next();
			result.putValue(exbase.hat(), this.data.get(exbase));	// result書き換え
		}
		return result;
	}

	/**
	 * 交換代数の整合を行った結果を保持する、新しい <code>Exalge</code> インスタンスを返す。
	 * <p>
	 * 交換代数の整合は、基底キーのハットキー以外が同一のもので、HATとNO_HATの
	 * 値を加算(正確には、NO_HATの値－HATの値)した結果となる。
	 * <br>
	 * 整合後の値が正の値であれば、基底のハットキーはNO_HAT、負の値であればHATと
	 * し、値が 0 になる基底は、要素から削除される。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = ~ (this)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が極力維持されるが、
	 * 元(this)の順序を保障するものではない。
	 * 
	 * @return 演算結果の交換代数
	 */
	public Exalge bar() {
		Exalge result = this.newInstance();	// new instance for result
		
		Iterator<ExBase> it = this.data.keySet().iterator();
		
		while (it.hasNext()) {
			ExBase exbase = it.next();
			ExBase hatbase = exbase.hat();
			
			// NO_HATの基底を基準とし、対応するHAT基底との演算結果を
			// 新しいインスタンスに代入する。
			// 対応するペアが存在しないものは、そのまま新しいインスタンスに
			// 代入する
			
			if (exbase.isNoHat()) {
				// NO_HAT基底への処理
				//    exbase  -- NO_HAT基底
				//    hatbase -- HAT基底
				BigDecimal value = this.get(exbase);
				if (this.containsBase(hatbase)) {
					//-- 対応ペアあり
					value = value.subtract(this.get(hatbase));
				}
				// 結果の代入(対応ペアなしなら、そのまま代入
				if (value.compareTo(BigDecimal.ZERO) != 0) {
					// 0以外なら代入
					result.putValue(exbase, value);	// result 書き換え
				}
			}
			else {
				// HAT基底への処理
				//    exbase  -- HAT基底
				//    hatbase -- NO_HAT基底
				if (!this.containsBase(hatbase)) {
					//-- 対応ペアなし
					BigDecimal value = this.get(exbase);
					if (value.compareTo(BigDecimal.ZERO) != 0) {
						// 0以外なら代入
						result.putValue(exbase, value);	// result 書き換え
					}
				}
				// 対応ペアありの場合は、NO_HAT基底検知時に演算を行うため、
				// ここでの処理は不要
			}
		}
		
		return result;
	}

	/**
	 * 交換代数の整合を行った結果を保持する、新しい <code>Exalge</code> インスタンスを返す。
	 * <p>
	 * 交換代数の整合は、基底キーのハットキー以外が同一のもので、HATとNO_HATの
	 * 値を加算(正確には、NO_HATの値－HATの値)した結果となる。
	 * <br>
	 * 整合後の値が正の値であれば、基底のハットキーはNO_HAT、負の値であればHATと
	 * し、整合の結果の値が 0 になる基底は、要素から削除される。
	 * <p>このバー演算は、交換代数元の通常のバー演算よりも厳密であり、
	 * ハットなし基底とハット基底のペア(ハットキー以外の基底キーが同一のもの)でない場合、
	 * その要素は値に関わらず結果に出力される。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が極力維持されるが、
	 * 元(this)の順序を保障するものではない。
	 *
	 * @return 演算結果の交換代数
	 * 
	 * @since 0.984
	 */
	public Exalge strictBar() {
		Exalge result = this.newInstance();	// new instance for result
		
		for (Map.Entry<ExBase, BigDecimal> entry : data.entrySet()) {
			ExBase exbase = entry.getKey();
			ExBase hatbase = exbase.hat();
			
			// NO_HATの基底を基準とし、対応するHAT基底との演算結果を新しいインスタンスに代入する。
			// 対応するペアが存在しないものは、そのまま新しいインスタンスに代入する
			
			if (!data.containsKey(hatbase)) {
				// 対応ペアなしなら、値やハットに関係なく、そのまま代入
				result.data.put(exbase, entry.getValue());
			}
			else if (exbase.isNoHat()) {
				// 対応ペアあり、NO_HAT基底
				BigDecimal value = entry.getValue().subtract(data.get(hatbase));
				//--- 0 以外なら代入
				if (value.signum() != 0) {
					result.putValue(exbase, value);
				}
			}
			// else : 対応ペアあり、HAT基底 : NO_HAT基底検知時に演算を行うため、ここでの処理は不要
		}
		
		return result;
	}
	
	/**
	 * 交換代数の整合を行った結果を保持する、新しい <code>Exalge</code> インスタンスを返す。
	 * <p>
	 * 交換代数の整合は、基底キーのハットキー以外が同一のもので、HATとNO_HATの
	 * 値を加算(正確には、NO_HATの値－HATの値)した結果となる。
	 * <br>
	 * 整合後の値が正の値であれば、基底のハットキーはNO_HAT、負の値であればHATとし、整合の結果の値が 0 になる基底は、
	 * <em>leaveAsNoHat</em> が <tt>true</tt> であればハットなし基底、
	 * <em>leaveAsNoHat</em> が <tt>false</tt> であればハット基底として結果に出力される。
	 * <p>このバー演算は、交換代数元の通常のバー演算よりも厳密であり、
	 * ハットなし基底とハット基底のペア(ハットキー以外の基底キーが同一のもの)でない場合、
	 * その要素は値に関わらず結果に出力される。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が極力維持されるが、
	 * 元(this)の順序を保障するものではない。
	 *
	 * @param leaveAsNoHat	バー演算の結果、値が 0 となる要素の基底をハットなし基底とする場合は <tt>true</tt>、ハット基底とする場合は <tt>false</tt>
	 * @return 演算結果の交換代数
	 * 
	 * @since 0.984
	 */
	public Exalge strictBarLeaveZero(boolean leaveAsNoHat) {
		Exalge result = this.newInstance();	// new instance for result
		
		for (Map.Entry<ExBase, BigDecimal> entry : data.entrySet()) {
			ExBase exbase = entry.getKey();
			ExBase hatbase = exbase.hat();
			
			// NO_HATの基底を基準とし、対応するHAT基底との演算結果を新しいインスタンスに代入する。
			// 対応するペアが存在しないものは、そのまま新しいインスタンスに代入する
			
			if (!data.containsKey(hatbase)) {
				// 対応ペアなしなら、値やハットに関係なく、そのまま代入
				result.data.put(exbase, entry.getValue());
			}
			else if (exbase.isNoHat()) {
				// 対応ペアあり、NO_HAT基底
				BigDecimal value = entry.getValue().subtract(data.get(hatbase));
				if (value.signum() != 0) {
					//--- 0 以外なら代入
					result.putValue(exbase, value);
				}
				else {
					//--- 0 なら、
					//--- leaveAsNoHat=true -> NO_HAT基底を残す
					//--- leaveAsNoHat=false -> HAT基底を残す
					if (leaveAsNoHat)
						result.data.put(exbase, BigDecimal.ZERO);
					else
						result.data.put(hatbase, BigDecimal.ZERO);
				}
			}
			// else : 対応ペアあり、HAT基底 : NO_HAT基底検知時に演算を行うため、ここでの処理は不要
		}
		
		return result;
	}

	/**
	 * 交換代数の逆数を算出する。
	 * <br>
	 * 交換代数の逆数は、全要素の実数値の逆数を持つものを指す。
	 * <p>
	 * (例)<br>
	 * 交換代数 X が<br>
	 * &nbsp;&nbsp;X = 5&lt;e1&gt; + 2&circ;&lt;e2&gt;<br>
	 * のとき、Y = X<sup>-1</sup> (Y = X.inverse()) は、次のようになる。<br>
	 * &nbsp;&nbsp;Y = X<sup>-1</sup> = 0.2&lt;e1&gt; + 0.5&circ;&lt;e2&gt;<br>
	 * <p>
	 * 逆数の演算において、{@link MathContext#DECIMAL128} の精度で結果を保持する。
	 * 実数値によって計算誤差が含まれる場合があるため、逆数の逆数が元の値とは
	 * ならない (つまり、X &ne; (X<sup>-1</sup>)<sup>-1</sup>) ことに注意すること。
	 * <p>
	 * 交換代数の要素が空の場合、結果も要素が空の交換代数となる。
	 * <br>
	 * また、実数値 0 の要素は無視され、演算結果に含まれない。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が維持される。
	 * 
	 * @return 逆数演算後の交換代数
	 * 
	 * @since 0.92
	 */
	public Exalge inverse() {
		Exalge newAlge = this.newInstance();
		Iterator<ExBase> it = data.keySet().iterator();
		while (it.hasNext()) {
			ExBase exbase = it.next();
			BigDecimal exval = this.get(exbase);
			// inverse
			if (exval.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal exinv = BigDecimal.ONE.divide(exval, MathContext.DECIMAL128);	// double と同程度の精度による桁制限＆丸め
				newAlge.putValue(exbase, exinv);
			}
		}
		return newAlge;
	}

	/**
	 * コレクションに含まれる交換代数の元の総和を返す。
	 * <br>
	 * 交換代数の元の総和は、交換代数基底が同一のもの同士の値の加算となる。
	 * <br>
	 * コレクションに含まれる <tt>null</tt> の要素は無視される。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 加算する値の基底が存在しない場合、交換代数の値と基底のマップの終端に、
	 * 指定された交換代数コレクション(c)に格納されている順序で追加される。
	 * 
	 * @param c 総和を計算する交換代数の元のコレクション
	 * 
	 * @return 計算結果となる <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	static public Exalge sum(Collection<? extends Exalge> c) {
		Exalge newAlge = new Exalge();
		Iterator<? extends Exalge> it = c.iterator();
		while (it.hasNext()) {
			Exalge alge = it.next();
			if (alge != null) {
				newAlge.plusValues(alge);
			}
		}
		return newAlge;
	}

	//------------------------------------------------------------
	// 振替系演算
	//------------------------------------------------------------
	
	/**
	 * 単一基底による基底の置換
	 * <p>
	 * この変換は、変換元基底に一致する基底を、変換先基底で示される基底に置換する。
	 * 基底は、ハット基底キーがワイルドカードとなっている基底パターンとみなされる。
	 * ハット基底キー以外の基底キーにワイルドカードを指定することも可能。変換先基底に
	 * ワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * なお、変換元基底に一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換操作は基底の置換となり、変換元基底は結果に含まれない。
	 * 
	 * @param fromBase	変換元基底
	 * @param toBase	変換先基底
	 * @return	変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public Exalge transform(ExBase fromBase, ExBase toBase) {
		if (fromBase == null)
			throw new NullPointerException("fromBase is null.");
		if (toBase == null)
			throw new NullPointerException("toBase is null.");
		
		// 基底パターンの生成
		ExBasePattern.PatternItem[] fromPattern = ExBasePattern.makePatternWithoutHatKey(fromBase._baseKeys);

		// 変換
		Exalge retAlge = this.copy();
		Exalge transAlge = new Exalge();
		for (ExBase base : data.keySet()) {
			if (ExBasePattern.matchesByPattern(fromPattern, base._baseKeys)) {
				//--- パターン一致
				transAlge.plusValue(ExBasePattern.translateBaseKeyWithoutHatKey(toBase._baseKeys, base), data.get(base));
				retAlge.data.remove(base);
			}
		}
		
		// 変換結果の合成
		if (transAlge.isEmpty()) {
			// 変換後の要素が存在しないので、この元をそのまま返す。
			return this;
		}
		else {
			// 変換後の要素を合成
			retAlge.plusValues(transAlge);
			return retAlge;
		}
	}
	
	/**
	 * 単一基底パターンによる基底の置換
	 * <p>
	 * この変換は、変換元基底パターンに一致する基底を、変換先基底パターンで示される基底に置換する。
	 * 変換先基底パターンにワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * なお、変換元基底パターンに一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換操作は基底の置換となり、変換元基底は結果に含まれない。
	 * 
	 * @param fromPattern	変換元基底パターン
	 * @param toPattern		変換先基底パターン
	 * @return	変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public Exalge transform(ExBasePattern fromPattern, ExBasePattern toPattern) {
		if (fromPattern == null)
			throw new NullPointerException("fromPattern is null.");
		if (toPattern == null)
			throw new NullPointerException("toPattern is null.");
		
		// 変換
		Exalge retAlge = this.copy();
		Exalge transAlge = new Exalge();
		for (ExBase base : data.keySet()) {
			if (fromPattern.matches(base)) {
				//--- パターン一致
				transAlge.plusValue(toPattern.translate(base), data.get(base));
				retAlge.data.remove(base);
			}
		}
		
		// 変換結果の合成
		if (transAlge.isEmpty()) {
			// 変換後の要素が存在しないので、この元をそのまま返す。
			return this;
		}
		else {
			// 変換後の要素を合成
			retAlge.plusValues(transAlge);
			return retAlge;
		}
	}
	
	/**
	 * 複数基底による単一基底への基底の置換
	 * <p>
	 * この変換は、変換元基底のどれか一つに一致する基底を、変換先基底で示される基底に置換する。
	 * 基底は、ハット基底キーがワイルドカードとなっている基底パターンとみなされる。
	 * ハット基底キー以外の基底キーにワイルドカードを指定することも可能。変換先基底に
	 * ワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * なお、変換元基底に一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換操作は基底の置換となり、変換元基底は結果に含まれない。
	 * 
	 * @param fromBases	変換元基底集合
	 * @param toBase	変換先基底
	 * @return	変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public Exalge transform(ExBaseSet fromBases, ExBase toBase) {
		if (fromBases == null)
			throw new NullPointerException("fromBases is null.");
		if (toBase == null)
			throw new NullPointerException("toBase is null.");
		
		if (fromBases.isEmpty()) {
			//--- 振替元がない場合は、この元をそのまま返す。
			return this;
		}
		
		// 基底パターンの生成
		ExBasePattern.PatternItem[][] fromPatterns = new ExBasePattern.PatternItem[fromBases.size()][];
		int index = 0;
		for (ExBase base : fromBases) {
			fromPatterns[index++] = ExBasePattern.makePatternWithoutHatKey(base._baseKeys);
		}
		
		// 変換
		Exalge retAlge = this.copy();
		Exalge transAlge = new Exalge();
		for (ExBase base : data.keySet()) {
			for (ExBasePattern.PatternItem[] pattern : fromPatterns) {
				if (ExBasePattern.matchesByPattern(pattern, base._baseKeys)) {
					//--- パターン一致
					transAlge.plusValue(ExBasePattern.translateBaseKeyWithoutHatKey(toBase._baseKeys, base), data.get(base));
					retAlge.data.remove(base);
					break;
				}
			}
		}
		
		// 変換結果の合成
		if (transAlge.isEmpty()) {
			// 変換後の要素が存在しないので、この元をそのまま返す。
			return this;
		}
		else {
			// 変換後の要素を合成
			retAlge.plusValues(transAlge);
			return retAlge;
		}
	}
	
	/**
	 * 複数基底パターンによる単一基底への基底の置換
	 * <p>
	 * この変換は、変換元基底パターンのどれか一つに一致する基底を、変換先基底パターンで示される基底に置換する。
	 * 変換先基底パターンにワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * なお、変換元基底パターンに一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換操作は基底の置換となり、変換元基底は結果に含まれない。
	 * 
	 * @param fromPatterns	変換元基底パターン集合
	 * @param toPattern		変換先基底パターン
	 * @return	変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public Exalge transform(ExBasePatternSet fromPatterns, ExBasePattern toPattern) {
		if (fromPatterns == null)
			throw new NullPointerException("fromPatterns is null.");
		if (toPattern == null)
			throw new NullPointerException("toPattern is null.");
		
		if (fromPatterns.isEmpty()) {
			//--- 振替元がない場合は、この元をそのまま返す。
			return this;
		}
		
		// 変換
		Exalge retAlge = this.copy();
		Exalge transAlge = new Exalge();
		for (ExBase base : data.keySet()) {
			if (fromPatterns.matches(base)) {
				//--- パターン一致
				transAlge.plusValue(toPattern.translate(base), data.get(base));
				retAlge.data.remove(base);
			}
		}
		
		// 変換結果の合成
		if (transAlge.isEmpty()) {
			// 変換後の要素が存在しないので、この元をそのまま返す。
			return this;
		}
		else {
			// 変換後の要素を合成
			retAlge.plusValues(transAlge);
			return retAlge;
		}
	}

	/**
	 * 指定された変換テーブルの変換定義に基づき、この交換代数元(<code>Exalge</code> オブジェクト)を変換する。
	 * 変換対象の基底に一致する、異なる変換元基底パターンが複数存在する場合は、
	 * 最初に一致した基底パターンを変換元基底パターンとする全ての変換定義によってのみ変換される。
	 * <p>
	 * 変換結果は、この交換代数元の全ての要素に、変換対象となった
	 * 基底のハットと元の値、変換後の全ての基底と値が加算された結果となる。
	 * <p>
	 * 変換方法については、{@link ExTransfer} クラスの説明を参照のこと。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param transfer	変換に使用する {@link ExTransfer} オブジェクト
	 * @return	変換結果となる交換代数元の新しいインスタンスを返す。一つも変換が行われなかった
	 * 			場合は、このオブジェクトをそのまま返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ArithmeticException		'ratio'属性の変換において、変換比率の合計が 0 の場合にスローされる
	 * 
	 * @see ExTransfer
	 * 
	 * @since 0.982
	 */
	public Exalge transfer(ExTransfer transfer) {
		return transfer.transfer(this);
	}

	/**
	 * 振替変換。Exalge#aggreTransfer(アグリゲーション)と同じ処理
	 * 
	 * @param table 振替変換テーブル
	 * @return 振替変換後の交換代数
	 * 
	 * @since 0.90
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public Exalge transfer(TransTable table) {
		// aggreTransfer() と同じ機能
		return aggreTransfer(table);
	}

	/**
	 * 単一基底による振替変換(アグリゲーション)
	 * <p>
	 * この変換は、振替元基底に一致する基底を、振替先基底で示される基底に変換する。
	 * 基底は、ハット基底キーがワイルドカードとなっている基底パターンとみなされる。
	 * ハット基底キー以外の基底キーにワイルドカードを指定することも可能。振替先基底に
	 * ワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * 変換の際、変換対象の基底について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。これは、次のような振る舞いとなる。
	 * <blockquote>
	 * x = a^&lt;e1&gt; + b&lt;e2&gt;<br>
	 * x.aggreTransfer(&lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e2&gt; + a&lt;e1&gt; + a^&lt;e3&gt;
	 * </blockquote>
	 * なお、振替元基底に一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param fromBase	振替元基底
	 * @param toBase	振替先基底
	 * @return	振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public Exalge aggreTransfer(ExBase fromBase, ExBase toBase) {
		if (fromBase == null)
			throw new NullPointerException("fromBase is null.");
		if (toBase == null)
			throw new NullPointerException("toBase is null.");
		
		// 基底パターンの生成
		ExBasePattern.PatternItem[] fromPattern = ExBasePattern.makePatternWithoutHatKey(fromBase._baseKeys);

		// 変換
		Exalge transAlge = new Exalge();
		for (ExBase base : data.keySet()) {
			if (ExBasePattern.matchesByPattern(fromPattern, base._baseKeys)) {
				//--- パターン一致
				ExBase tBase = ExBasePattern.translateBaseKeyWithoutHatKey(toBase._baseKeys, base);
				BigDecimal value = data.get(base);
				transAlge.plusValue(base.hat(), value);
				transAlge.plusValue(tBase, value);
			}
		}
		
		// 変換結果の合成
		if (transAlge.isEmpty()) {
			// 振替後の要素が存在しないので、この元をそのまま返す。
			return this;
		}
		else {
			// 振替後の要素を合成
			return this.plus(transAlge);
		}
	}
	
	/**
	 * 単一基底パターンによる振替変換(アグリゲーション)
	 * <p>
	 * この変換は、振替元基底パターンに一致する基底を、振替先基底パターンで示される基底に変換する。
	 * 振替先基底パターンにワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを
	 * 指定しても、基底キー全体が変換対象基底キーに置き換わる。<br>
	 * 変換の際、変換対象の基底について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。これは、次のような振る舞いとなる。
	 * <blockquote>
	 * x = a^&lt;e1&gt; + b&lt;e2&gt;<br>
	 * x.aggreTransfer(&lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e2&gt; + a&lt;e1&gt; + a^&lt;e3&gt;
	 * </blockquote>
	 * なお、振替元基底パターンに一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param fromPattern	振替元基底パターン
	 * @param toPattern		振替先基底パターン
	 * @return	振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public Exalge aggreTransfer(ExBasePattern fromPattern, ExBasePattern toPattern) {
		if (fromPattern == null)
			throw new NullPointerException("fromPattern is null.");
		if (toPattern == null)
			throw new NullPointerException("toPattern is null.");
		
		// 変換
		Exalge transAlge = new Exalge();
		for (ExBase base : data.keySet()) {
			if (fromPattern.matches(base)) {
				//--- パターン一致
				ExBase tBase = toPattern.translate(base);
				BigDecimal value = data.get(base);
				transAlge.plusValue(base.hat(), value);
				transAlge.plusValue(tBase, value);
			}
		}
		
		// 変換結果の合成
		if (transAlge.isEmpty()) {
			// 振替後の要素が存在しないので、この元をそのまま返す。
			return this;
		}
		else {
			// 振替後の要素を合成
			return this.plus(transAlge);
		}
	}
	
	/**
	 * 複数基底による単一基底への振替変換(アグリゲーション)
	 * <p>
	 * この変換は、振替元基底のどれか一つに一致する基底を、振替先基底で示される基底に変換する。
	 * 基底は、ハット基底キーがワイルドカードとなっている基底パターンとみなされる。
	 * ハット基底キー以外の基底キーにワイルドカードを指定することも可能。振替先基底に
	 * ワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * 変換の際、変換対象の基底について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。これは、次のような振る舞いとなる。
	 * <blockquote>
	 * x = a^&lt;e1&gt; + b&lt;e2&gt;<br>
	 * x.aggreTransfer(&lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e2&gt; + a&lt;e1&gt; + a^&lt;e3&gt;
	 * </blockquote>
	 * なお、振替元基底に一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param fromBases	振替元基底集合
	 * @param toBase	振替先基底
	 * @return	振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public Exalge aggreTransfer(ExBaseSet fromBases, ExBase toBase) {
		if (fromBases == null)
			throw new NullPointerException("fromBases is null.");
		if (toBase == null)
			throw new NullPointerException("toBase is null.");
		
		if (fromBases.isEmpty()) {
			//--- 振替元がない場合は、この元をそのまま返す。
			return this;
		}
		
		// 基底パターンの生成
		ExBasePattern.PatternItem[][] fromPatterns = new ExBasePattern.PatternItem[fromBases.size()][];
		int index = 0;
		for (ExBase base : fromBases) {
			fromPatterns[index++] = ExBasePattern.makePatternWithoutHatKey(base._baseKeys);
		}
		
		// 変換
		Exalge transAlge = new Exalge();
		for (ExBase base : data.keySet()) {
			for (ExBasePattern.PatternItem[] pattern : fromPatterns) {
				if (ExBasePattern.matchesByPattern(pattern, base._baseKeys)) {
					//--- パターン一致
					ExBase tBase = ExBasePattern.translateBaseKeyWithoutHatKey(toBase._baseKeys, base);
					BigDecimal value = data.get(base);
					transAlge.plusValue(base.hat(), value);
					transAlge.plusValue(tBase, value);
					break;
				}
			}
		}
		
		// 変換結果の合成
		if (transAlge.isEmpty()) {
			// 振替後の要素が存在しないので、この元をそのまま返す。
			return this;
		}
		else {
			// 振替後の要素を合成
			return this.plus(transAlge);
		}
	}
	
	/**
	 * 複数基底パターンによる単一基底への振替変換(アグリゲーション)
	 * <p>
	 * この変換は、振替元基底パターンのどれか一つに一致する基底を、振替先基底パターンで示される基底に変換する。
	 * 振替先基底パターンにワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * 変換の際、変換対象の基底について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。これは、次のような振る舞いとなる。
	 * <blockquote>
	 * x = a^&lt;e1&gt; + b&lt;e2&gt;<br>
	 * x.aggreTransfer(&lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e2&gt; + a&lt;e1&gt; + a^&lt;e3&gt;
	 * </blockquote>
	 * なお、振替元基底パターンに一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param fromPatterns	振替元基底パターン集合
	 * @param toPattern		振替先基底パターン
	 * @return	振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public Exalge aggreTransfer(ExBasePatternSet fromPatterns, ExBasePattern toPattern) {
		if (fromPatterns == null)
			throw new NullPointerException("fromPatterns is null.");
		if (toPattern == null)
			throw new NullPointerException("toPattern is null.");
		
		if (fromPatterns.isEmpty()) {
			//--- 振替元がない場合は、この元をそのまま返す。
			return this;
		}
		
		// 変換
		Exalge transAlge = new Exalge();
		for (ExBase base : data.keySet()) {
			if (fromPatterns.matches(base)) {
				//--- パターン一致
				ExBase tBase = toPattern.translate(base);
				BigDecimal value = data.get(base);
				transAlge.plusValue(base.hat(), value);
				transAlge.plusValue(tBase, value);
			}
		}
		
		// 変換結果の合成
		if (transAlge.isEmpty()) {
			// 振替後の要素が存在しないので、この元をそのまま返す。
			return this;
		}
		else {
			// 振替後の要素を合成
			return this.plus(transAlge);
		}
	}

	/**
	 * 振替変換(アグリゲーション)
	 * <p>
	 * この変換は、指定された振替変換テーブルの変換定義に基づく振替変換を行う。
	 * この交換代数元の要素の基底に一致する、異なる振替元基底パターンが複数存在する
	 * 場合、最初に一致した振替元基底パターンの振替先基底パターンによって振替変換が
	 * 行われる。
	 * 変換の際、変換対象の要素について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param table 振替変換テーブル
	 * @return 振替変換後の交換代数
	 * 
	 * @since 0.90
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public Exalge aggreTransfer(TransTable table) {
		Exalge aggre = this.newInstance();
		Iterator<ExBase> it = this.data.keySet().iterator();
		while (it.hasNext()) {
			ExBase base = it.next();
			BigDecimal value = this.data.get(base);
			//--- 振替変換(要素)
			ExBase aggreBase = table.transfer(base);
			if (aggreBase != null) {
				aggre.plusValue(base.hat(), value);
				aggre.plusValue(aggreBase, value);
			}
		}
		// Bar演算
		//return this.plus(aggre).bar();
		return this.plus(aggre);
	}

	/**
	 * 按分変換
	 * <p>
	 * この変換は、変換元基底パターンに一致する基底を、指定された按分比率で変換先基底パターンで
	 * 示される基底に変換する。
	 * 変換の際、変換対象の要素について、基底のハットと変換後の交換代数が加算される。
	 * <p>
	 * このメソッドでは、按分変換マトリクスの按分比率計算方法の
	 * 設定({@link exalge2.TransMatrix#isTotalRatioUsed()} が返す値)により、
	 * 按分値の算出方法が異なる。計算方法の詳細については、{@link exalge2.TransMatrix#transfer(ExBase, BigDecimal)} を参照のこと。
	 * <p>
	 * 按分比率計算における除算においては、{@link MathContext#DECIMAL128} の精度で結果を保持する。
	 * そのため、1 つの値を 3 当分するような按分変換の場合、誤差が発生する。
	 * <br>
	 * 指定の基底に一致する、異なる按分元基底パターンが複数存在する場合、
	 * 最初に一致した按分元基底パターンに関連付けられた変換定義により
	 * 按分変換が行われる。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param matrix 按分比率マトリクス
	 * @return 按分変換後の交換代数
	 * 
	 * @since 0.90
	 * 
	 * @see TransMatrix#transfer(ExBase, BigDecimal)
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws IllegalStateException	一致する按分元基底に対応する按分先基底が存在しない場合にスローされる
	 * @throws ArithmeticException		按分計算において 0 除算が発生した場合にスローされる
	 */
	public Exalge divideTransfer(TransMatrix matrix) {
		Exalge div = this.newInstance();
		Iterator<ExBase> it = this.data.keySet().iterator();
		while (it.hasNext()) {
			ExBase srcBase = it.next();
			BigDecimal srcValue = this.data.get(srcBase);
			//--- 按分変換(要素)
			Exalge ret = matrix.transfer(srcBase, srcValue);
			if (ret != null) {
				div.plusValue(srcBase.hat(), srcValue);
				div.plusValues(ret);
			}
		}
		// Bar演算
		//return this.plus(div).bar();
		return this.plus(div);
	}

	//------------------------------------------------------------
	// for I/O
	//------------------------------------------------------------
	
	/**
	 * 交換代数の内容を、指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている基底の順序で出力される。
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
	 * 交換代数の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
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
	 * 交換代数の内容を、CSVフォーマットの文字列に変換する。
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
	 * CSV フォーマットのファイルを読み込み、新しい交換代数を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>Exalge</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.91
	 */
	static public Exalge fromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		CsvReader reader = new CsvReader(csvFile);
		Exalge newAlge = new Exalge();
		
		try {
			newAlge.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newAlge;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しい交換代数を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>Exalge</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.93
	 */
	static public Exalge fromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		Exalge newAlge = new Exalge();
		
		try {
			newAlge.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newAlge;
	}
	
	/**
	 * CSV フォーマットの文字列を読み込み、新しい交換代数を生成する。
	 * <p>
	 * 基本的に、文字列に記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvString 読み込む CSV フォーマットの文字列
	 * @return 文字列の内容で生成された、新しい <code>Exalge</code> インスタンスを返す。
	 * 
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.984
	 */
	static public Exalge fromCsvString(String csvString) throws CsvFormatException
	{
		CsvReader reader = new CsvReader(new StringReader(csvString));
		Exalge newAlge = new Exalge();
		
		try {
			newAlge.readFromCSV(reader);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		finally {
			reader.close();
		}
		
		return newAlge;
	}
	
	/**
	 * 交換代数の内容を、指定のファイルに XML フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている基底の順序で出力される。
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
	 * 交換代数の内容から、XML ドキュメントを生成する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている基底の順序で出力される。
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
		
		Element root = xml.createElement(XML_TOP_ELEMENT_NAME);
		xml.append(root);
		
		Element item = encodeToElement(xml);
		root.appendChild(item);
		
		return xml;
	}
	
	/**
	 * 交換代数の内容を、XML フォーマットの文字列に変換する。
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
	 * XML フォーマットのファイルを読み込み、新しい交換代数を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlFile 読み込む XML ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>Exalge</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.91
	 */
	static public Exalge fromXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException,
				XmlDomParseException
	{
		XmlDocument xmlDocument = XmlDocument.fromFile(xmlFile);
		
		return Exalge.fromXML(xmlDocument);
	}
	
	/**
	 * 指定された XML ドキュメントを解析し、新しい交換代数を生成する。
	 * <p>
	 * 基本的に、XML ノードの順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlDocument 解析対象の XML ドキュメント
	 * 
	 * @return 解析により生成された、新しい <code>Exalge</code> インスタンスを返す。
	 * 
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.91
	 */
	static public Exalge fromXML(XmlDocument xmlDocument)
		throws XmlDomParseException
	{
		Element root = xmlDocument.getDocumentElement();
		
		// Check exist root element
		if (root == null) {
			throw new XmlDomParseException("Root element not found.");
		}

		// Check root element
		String strName = root.getNodeName();
		if (!strName.equals(XML_TOP_ELEMENT_NAME)) {
			// Illegal element name
			throw new XmlDomParseException(String.format("Root element must be <%s> : <%s>", XML_TOP_ELEMENT_NAME, String.valueOf(strName)));
		}
		
		// get child nodes
		Exalge newAlge = new Exalge();
		Node child = root.getFirstChild();
		for (; child != null; child = child.getNextSibling()) {
			short childNodeType = child.getNodeType();
			if (childNodeType == Node.ELEMENT_NODE) {
				newAlge.decodeFromElement(xmlDocument, (Element)child, (Element)root);
			}
		}
		
		return newAlge;
	}
	
	/**
	 * XML フォーマットの文字列を読み込み、新しい交換代数を生成する。
	 * <p>
	 * 基本的に、文字列に記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlString 読み込む XML フォーマットの文字列
	 * 
	 * @return 文字列の内容で生成された、新しい <code>Exalge</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.984
	 */
	static public Exalge fromXmlString(String xmlString)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException, XmlDomParseException
	{
		XmlDocument xmlDocument = XmlDocument.fromXmlString(xmlString);
		
		return Exalge.fromXML(xmlDocument);
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
		for (Map.Entry<ExBase, BigDecimal> entry : data.entrySet()) {
			// write value
			//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
			if (entry.getValue()==null)
				writer.writeField("null");
			else
				writer.writeField(entry.getValue().stripTrailingZeros().toPlainString());
			/*@@@ old codes : 2010.02.17 @@@
			writer.writeField(entry.getValue().stripTrailingZeros().toPlainString());
			**@@@ end of old codes : 2010.02.17 @@@*/
			//@@@ end of modified : 2010.02.17 @@@
			
			// write base
			entry.getKey().writeFieldToCSV(writer);
			
			// new line
			writer.newLine();
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
	protected void readFromCSV(CsvReader reader)
		throws IOException, CsvFormatException
	{
		CsvReader.CsvRecord record;
		
		// コメント行は有効
		reader.setLineCommentEnable(true);
		
		// read elements
		while ((record = reader.readCsvRecord()) != null) {
			// 空行、もしくは値のないレコードはスキップ
			if (!record.hasFields() || !record.hasValues()) {
				continue;
			}
			
			// read record
			readRecordFromCSV(record);
		}
	}
	
	/**
	 * 指定されたCSVレコードから、値と基底を読み込む。
	 * 
	 * @param record	<code>CsvReader.CsvRecord</code> オブジェクト
	 * @throws IOException	読み込みエラーが発生したとき
	 * @throws CsvFormatException CSVフォーマットエラーが発生した場合
	 * 
	 * @since 0.982
	 */
	protected void readRecordFromCSV(CsvReader.CsvRecord record)
		throws IOException, CsvFormatException
	{
		// setup field reader
		CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);

		// read value
		String strValue = freader.readTrimmedValue();
		//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
		if (Strings.isNullOrEmpty(strValue)) {
			throw new CsvFormatException("Exalge value is not specified!", freader.getLineNo(), freader.getNextPosition());
		}
		
		BigDecimal value = null;
		if (!"null".equalsIgnoreCase(strValue)) {
			try {
				value = new BigDecimal(strValue);
			}
			catch (NumberFormatException ex) {
				throw new CsvFormatException("Illegal Exalge value : " + strValue, freader.getLineNo(), freader.getNextPosition());
			}
		}
		/*@@@ old codes : 2010.02.17 @@@
		if (strValue == null) {
			throw new CsvFormatException("Exalge value is not specified!", freader.getLineNo(), freader.getNextPosition());
		}
		
		BigDecimal value = null;
		try {
			value = new BigDecimal(strValue);
		}
		catch (NumberFormatException ex) {
			throw new CsvFormatException("Illegal Exalge value : " + strValue, freader.getLineNo(), freader.getNextPosition());
		}
		**@@@ end of old codes : 2010.02.17 @@@*/
		//@@@ end of modified : 2010.02.17 @@@
		
		// read base
		ExBase base = ExBase.readFieldFromCSV(freader);
		
		// 代入(加算)
		plusValue(base, value);
	}

	/*
	 * 指定されたリーダーから、値と基底を読み込む。
	 * 
	 * @param reader	<code>CsvRecordReader</code> オブジェクト
	 * @throws IOException	読み込みエラーが発生したとき
	 * @throws CsvFormatException CSVフォーマットエラーが発生した場合
	 * 
	 * @deprecated このメソッドは削除されます。新しい {@link #readRecordFromCSV(exalge2.io.csv.CsvReader.CsvRecord)} を使用してください。
	 *
	protected void readRecordFromCSV(CsvRecordReader reader)
		throws IOException, CsvFormatException
	{
		//BigDecimal newValue = reader.readBigDecimalColumn(true);
		//if (newValue == null) {
		//	newValue = BigDecimal.ZERO;
		//}
		BigDecimal newValue = reader.readBigDecimalColumn(false);
		
		// ZERO でも代入する
		
		// 基底
		ExBase newBase = new ExBase("init", ExBase.NO_HAT);
		newBase.readFromCSV(reader);
		
		// 代入(加算)
		plusValue(newBase, newValue);
	}
	*/

	static protected final String XML_TOP_ELEMENT_NAME = "ExalgebraSet";
	static protected final String XML_GROUP_ELEMENT_NAME = "Exalgebra";
	static protected final String XML_ELEMENT_NAME = "Exelem";
	static protected final String XML_ATTR_VALUE = "value";
	
	protected Element encodeToElement(XmlDocument xmlDocument)	throws DOMException
	{
		// create node
		Element node = xmlDocument.createElement(XML_GROUP_ELEMENT_NAME);
		
		// create entries
		for (Map.Entry<ExBase, BigDecimal> entry : data.entrySet()) {
			// create item
			Element item = xmlDocument.createElement(XML_ELEMENT_NAME);
			//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
			if (entry.getValue()==null)
				item.setAttribute(XML_ATTR_VALUE, "null");
			else
				item.setAttribute(XML_ATTR_VALUE, entry.getValue().stripTrailingZeros().toPlainString());
			/*@@@ old codes : 2010.02.17 @@@
			item.setAttribute(XML_ATTR_VALUE, entry.getValue().stripTrailingZeros().toPlainString());
			**@@@ end of old codes : 2010.02.17 @@@*/
			//@@@ end of modified : 2010.02.17 @@@
			
			// create ExBase element
			Element base = entry.getKey().encodeToDomElement(xmlDocument);
			
			// regist to group element
			item.appendChild(base);
			node.appendChild(item);
		}
		
		// completed
		return node;
	}
	
	protected void decodeFromElement(XmlDocument xmlDocument, Element xmlElement, Element xmlParent)
		throws XmlDomParseException
	{
		// check group Element name
		String strInput = xmlElement.getNodeName();
		if (!strInput.equals(XML_GROUP_ELEMENT_NAME)) {
			// Illegal element name
			throw new XmlDomParseException(String.format("The child of <%s> must be <%s> : <%s>",
					String.valueOf(xmlParent.getNodeName()), XML_GROUP_ELEMENT_NAME, String.valueOf(strInput)));
		}
		
		// decode internal items
		Node child = xmlElement.getFirstChild();
		for (; child != null; child = child.getNextSibling()) {
			short childNodeType = child.getNodeType();
			if (childNodeType == Node.ELEMENT_NODE) {
				Element itemElement = (Element)child;
				// check item element
				strInput = itemElement.getNodeName();
				if (!strInput.equals(XML_ELEMENT_NAME)) {
					// Illegal element name
					throw new XmlDomParseException(String.format("The child of <%s> must be <%s> : <%s>",
							String.valueOf(xmlElement.getNodeName()), XML_ELEMENT_NAME, String.valueOf(strInput)));
				}
				// get "value" attribute
				String strValue = itemElement.getAttribute(XML_ATTR_VALUE);
				//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
				if (strValue != null) strValue = strValue.trim();
				if (Strings.isNullOrEmpty(strValue)) {
					throw new XmlDomParseException(String.format("<%s> is not specified!", XML_ATTR_VALUE));
				}
				BigDecimal retValue = null;
				if (!"null".equalsIgnoreCase(strValue)) {
					try {
						retValue = new BigDecimal(strValue);
					}
					catch (NumberFormatException ex) {
						throw new XmlDomParseException(String.format("Illegal <%s> value : %s", XML_ATTR_VALUE, strValue));
					}
				}
				/*@@@ old codes : 2010.02.17 @@@
				strValue = strValue.trim();
				if (Strings.isNullOrEmpty(strValue)) {
					throw new XmlDomParseException(String.format("<%s> is not specified!", XML_ATTR_VALUE));
				}
				// convert value
				BigDecimal retValue = null;
				try {
					retValue = new BigDecimal(strValue);
				}
				catch (NumberFormatException ex) {
					throw new XmlDomParseException(String.format("Illegal <%s> value : %s", XML_ATTR_VALUE, strValue));
				}
				**@@@ end of old codes : 2010.02.17 @@@*/
				//@@@ end of modified : 2010.02.17 @@@
				// decode ExBase node
				ExBase newBase = null;
				Node baseChild = itemElement.getFirstChild();
				for (; baseChild != null; baseChild = baseChild.getNextSibling()) {
					childNodeType = baseChild.getNodeType();
					if (childNodeType == Node.ELEMENT_NODE) {
						if (newBase != null) {
							throw new XmlDomParseException(String.format("<%s> element must not be multiple.", baseChild.getNodeName()));
						}
						newBase = ExBase.decodeFromDomElement(xmlDocument, (Element)baseChild);
					}
				}
				if (newBase == null) {
					// not found <Exbase> node
					throw new XmlDomParseException(String.format("<%s> element not found.", ExBase.XML_ELEMENT_NAME));
				}
				// regist value
				this.plusValue(newBase, retValue);
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/*
	 * このオブジェクトのコピーを生成する。
	 * <br>
	 * このメソッドでは、唯一のフィールドであるマップの 
	 * {@link java.util.LinkedHashMap#clone() <code>clone()</code>} メソッドにより
	 * フィールドのコピーが生成されるため、キーと値それ自体は複製されない。
	 * 
	 * @return このオブジェクトのコピー
	 * 
	 * @since 0.93
	 *
	@Override
	@SuppressWarnings("unchecked")
	protected Object clone() throws CloneNotSupportedException {
		// インスタンスの複製
		Exalge newAlge = (Exalge)super.clone();
		
		// フィールドの複製
		newAlge.data = (LinkedHashMap<ExBase,BigDecimal>)this.data.clone();
		
		return newAlge;
	}
	***/

	/**
	 * 文字列配列から、{@link java.util.HashSet} インスタンスを生成する。
	 * 
	 * @return 指定された文字列を全て格納する、{@link java.util.HashSet} インスタンスを返す。
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	protected Set<String> makeBaseKeyTargetSet(String...targets) {
		if (targets.length == 1) {
			return Collections.singleton(targets[0]);
		} else if (targets.length > 1) {
			return new HashSet<String>(Arrays.asList(targets));
		} else {
			return Collections.<String>emptySet();
		}
	}

	/**
	 * 指定されたイテレータから、基底キーの <code>keyIndex</code> の位置に、<code>targets</code> に含まれる
	 * 文字列を持つ要素を取得する。
	 * <p>
	 * 要素が見つかった場合、イテレータはその要素の次の位置を示す。
	 * 要素が見つからなかった場合は、<tt>null</tt> を返す。
	 * 
	 * @param it		検索を開始する位置を示すイテレータ
	 * @param keyIndex	判定対象の基底キーを示すインデックス
	 * @param targets	検索キーとなる文字列のセット
	 * @return	要素が見つかった場合、要素のインスタンスを返す。見つからなかった場合は <tt>null</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ArrayIndexOutOfBoundsException	<code>keyIndex</code> が範囲外の場合
	 * @throws java.util.NoSuchElementException	イテレータが終端の位置に到達している場合
	 * 
	 * @since 0.970
	 */
	protected Map.Entry<ExBase, BigDecimal> getNextByBaseKey(Iterator<Map.Entry<ExBase,BigDecimal>> it, int keyIndex, Set<String> targets) {
		do {
			Map.Entry<ExBase, BigDecimal> entry = it.next();
			if (targets.contains(entry.getKey()._baseKeys[keyIndex])) {
				// found
				return entry;
			}
		} while (it.hasNext());
		
		// not found
		return null;
	}

	/**
	 * 指定された文字列セットのどれか一つに一致する基底キーを持つ基底を取得する。
	 * <p>基底キーはインデックスで指定する。
	 * 
	 * @param keyIndex	判定対象の基底キーを示すインデックス
	 * @param targets	検索キーとなる文字列のセット
	 * @return	指定の文字列と一致する基底キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>targets</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>targets</code> が <tt>null</tt> の場合
	 * @throws ArrayIndexOutOfBoundsException	<code>keyIndex</code> が範囲外の場合
	 * 
	 * @since 0.970
	 */
	protected ExBaseSet getBasesByBaseKey(int keyIndex, Set<String> targets) {
		ExBaseSet retBases = new ExBaseSet();
		if (!targets.isEmpty()) {
			Iterator<Map.Entry<ExBase, BigDecimal>> it = this.data.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<ExBase, BigDecimal> entry = getNextByBaseKey(it, keyIndex, targets);
				if (entry != null) {
					retBases.fastAdd(entry.getKey());
				} else {
					break;
				}
			}
		}
		return retBases;
	}

	/**
	 * 指定された文字列セットのどれか一つに一致する基底キーを持つ要素のみを取り出す。
	 * <p>基底キーはインデックスで指定する。
	 * 
	 * @param keyIndex	判定対象の基底キーを示すインデックス
	 * @param targets	検索キーとなる文字列のセット
	 * @return	指定の文字列と一致する基底キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>targets</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>targets</code> が <tt>null</tt> の場合
	 * @throws ArrayIndexOutOfBoundsException	<code>keyIndex</code> が範囲外の場合
	 * 
	 * @since 0.970
	 */
	protected Exalge projectionByBaseKey(int keyIndex, Set<String> targets) {
		Exalge retAlge = newInstance();
		if (!targets.isEmpty()) {
			Iterator<Map.Entry<ExBase, BigDecimal>> it = this.data.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<ExBase, BigDecimal> entry = getNextByBaseKey(it, keyIndex, targets);
				if (entry != null) {
					retAlge.data.put(entry.getKey(), entry.getValue());
				} else {
					break;
				}
			}
		}
		return retAlge;
	}

	/**
	 * 交換代数内部のすべての値をクリア(０に設定)する。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 */
	protected void clearValue() {
		BigDecimal value = BigDecimal.ZERO;
		Iterator<ExBase> it = data.keySet().iterator();
		while (it.hasNext()) {
			ExBase base = it.next();
			put(base, value);
		}
	}

	/**
	 * 指定された基底と値を代入する。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 * <p>
	 * すでに同一基底が存在する場合、指定された値で上書きされる。
	 * <br>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param base 交換代数の基底
	 * @param value 値
	 */
	protected void putValue(ExBase base, BigDecimal value) {
		//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
		if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
			this.data.put(base.hat(), value.abs());
		} else {
			this.data.put(base, value);
		}
		/*@@@ old codes : 2010.02.17 @@@
		if (value.compareTo(BigDecimal.ZERO) >= 0) {
			this.data.put(base, value);
		} else {
			this.data.put(base.hat(), value.abs());
		}
		**@@@ end of old codes : 2010.02.17 @@@*/
		//@@@ end of modified : 2010.02.17 @@@
	}

	/**
	 * 指定された基底と値を、交換代数に加算する。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 *<p>
	 * 値がマイナスの時、自動的に ^ 計算を行って値をプラスにするように設計されている。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param base 交換代数の基底
	 * @param value 値
	 * 
	 * @since 0.90
	 */
	protected void plusValue(ExBase base, BigDecimal value) {
		ExBase tgBase;
		BigDecimal tgValue;
		//@@@ modified by Y.Ishizuka : 2010.02.17 @@@
		if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
			tgBase  = base.hat();
			tgValue = value.abs();
		} else {
			tgBase  = base;
			tgValue = value;
		}
		/*@@@ old codes : 2010.02.17 @@@
		if (value.compareTo(BigDecimal.ZERO) >= 0) {
			tgBase  = base;
			tgValue = value;
		} else {
			// 負の値は^演算後の正の値とする
			tgBase  = base.hat();
			tgValue = value.abs();
		}
		**@@@ end of old codes : 2010.02.17 @@@*/
		//@@@ end of modified : 2010.02.17 @@@
		// 同一基底に加算
		if (this.containsBase(tgBase))
			tgValue = tgValue.add(this.data.get(tgBase));
		this.data.put(tgBase, tgValue);
	}

	/**
	 * 指定された交換代数の元を、この交換代数に加算する。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param alge 加算する交換代数の元
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.90
	 */
	protected void plusValues(Exalge alge) {
		Iterator<ExBase> it = alge.data.keySet().iterator();
		while (it.hasNext()) {
			ExBase exbase = it.next();
			BigDecimal exval = alge.data.get(exbase);
			this.plusValue(exbase, exval);
		}
	}

	/**
	 * 指定された基底を＾計算した後のものに対して、 存在してるかどうかのチェック
	 * 
	 * @param exbase 基底
	 * @return ある？
	 */
	protected boolean hasAfterHat(ExBase exbase) {
		return containsBase(exbase.hat());
	}

	/**
	 * 交換代数元の要素イテレーターを生成する。
	 * 
	 * @return <code>Exalge</code> の <code>Iterator</code>
	 * 
	 * @since 0.94
	 */
	protected Iterator<Exalge> newExalgeIterator() {
		return new ExalgeIterator(this.data);
	}
	
	/**
	 * <code>Exalge</code> クラスのイテレーター。
	 * <p>
	 * このクラスは、交換代数元<code>(Exalge)</code>から交換代数要素(単一の基底と値を持つ交換代数元)を
	 * 順次取得するためのイテレーターである。
	 * <p>
	 * このクラスのインタフェースでは、{@link #remove()} はサポートされていない。
	 * 
	 * @version 0.94 2008/04/16
	 * 
	 * @author SOARS Project.
	 * @author Y.Ishizuka(PieCake.inc,)
	 * 
	 * @since 0.94
	 */
	private static class ExalgeIterator implements Iterator<Exalge> {
		final Iterator<Map.Entry<ExBase,BigDecimal>> entryIterator;
		
		public ExalgeIterator(LinkedHashMap<ExBase,BigDecimal> targetMap) {
			entryIterator = targetMap.entrySet().iterator();
		}
		
		/**
		 * 繰り返し処理でさらに要素がある場合に <code>true</code> を返す。
		 * つまり、{@link #next()} が例外をスローしないで要素を返す場合に <code>true</code> を返す。
		 * 
		 * @return 反復子がさらに要素をもつ場合は <code>true</code>
		 */
		public boolean hasNext() {
			return entryIterator.hasNext();
		}

		/**
		 * 繰り返し処理で次の要素を返す。{@link #hasNext()} メソッドが <code>false</code> を
		 * 返すまでこのメソッドを繰り返し呼び出すと、基になるコレクション内の各要素が一度だけ
		 * 返される。
		 * 
		 * @return 繰り返し処理で次の要素を返す
		 * 
		 * @throws java.util.NoSuchElementException 繰り返し処理でそれ以上要素がない場合
		 */
		public Exalge next() {
			Map.Entry<ExBase,BigDecimal> entry = entryIterator.next();
			return new Exalge(entry.getKey(), entry.getValue());
		}

		/**
		 * <code>ExalgeIterator</code> クラスでは、このメソッドはサポートされない。
		 * 
		 * @throws UnsupportedOperationException この例外を必ずスローする
		 */
		public void remove() {
			throw new UnsupportedOperationException("Unsupported \"remove\" operation!");
		}
	}
}