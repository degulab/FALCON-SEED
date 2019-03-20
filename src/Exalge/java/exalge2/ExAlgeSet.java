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
 * @(#)ExAlgeSet.java	0.984	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExAlgeSet.java	0.983	2010/02/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExAlgeSet.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExAlgeSet.java	0.970	2009/03/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExAlgeSet.java	0.960	2009/02/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExAlgeSet.java	0.94	2008/05/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExAlgeSet.java	0.931	2007/09/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExAlgeSet.java	0.93	2007/09/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExAlgeSet.java	0.91	2007/08/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExAlgeSet.java	0.90	2007/07/27
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
 * 交換代数の元(<code>{@link Exalge}</code>)の集合を保持するクラス。
 * 
 * <p>交換代数の元(<code>{@link Exalge}</code>)のインスタンスの
 * 集合であり、同じ値、もしくは同じインスタンスが含まれる。
 * <br>
 * このクラスは、{@link java.util.ArrayList} の実装となる。
 * したがって、挿入メソッド、ファイル入出力において、
 * クラス内での要素の順序は基本的に維持される。
 * <p>
 * このクラスでは、<tt>null</tt> を許容しない。
 * <br>
 * また、<b>この実装は同期化されない</b>。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>ExAlgeSet</code> は、CSV ファイル、XML ファイル(XML ドキュメント)形式の
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
 * <li>空白行を交換代数の元の区切りとする。つまり、空白行と空白行の間の要素が全てが
 * １つの <code>{@link Exalge}<code> インスタンスに相当する。
 * <li>連続した空白行は、１行分の空白行とみなす。
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
 * <li>&lt;Exalgebra&gt; が、交換代数の元(１つの <code>{@link Exalge}</code> インスタンス)を示すノードとなる。
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
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.90
 */
public class ExAlgeSet extends ArrayList<Exalge> implements exalge2.io.IDataOutput
{
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * デフォルトの初期容量で空の集合を生成する。
	 * 
	 * @see java.util.ArrayList#ArrayList()
	 */
	public ExAlgeSet() {
		super();
	}

	/**
	 * 指定された初期サイズで空の集合を生成する。
	 * 
	 * 指定された初期サイズで空のリストを作成します。
	 * 
	 * @param initialCapacity 集合の初期容量
	 * 
	 * @throws IllegalArgumentException 指定された初期容量が負の場合にスローされる
	 * 
	 * @see java.util.ArrayList#ArrayList(int)
	 */
	public ExAlgeSet(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * 指定されたコレクションの要素を含む集合を生成する。
	 * <br>
	 * これらの要素は、コレクションの反復子が返す順序で格納される。
	 * <br>
	 * コレクションに格納される <tt>null</tt> 要素は無視される。
	 * 
	 * @param c 要素がリストに配置されるコレクション
	 * 
	 * @throws NullPointerException - 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @see java.util.ArrayList#ArrayList(Collection)
	 */
	public ExAlgeSet(Collection<? extends Exalge> c) {
		super((int)Math.min((c.size()*110L)/100, Integer.MAX_VALUE));
		addAll(c);
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 交換代数集合を連結した、新しい交換代数集合のインスタンスを返す。
	 * <br>
	 * 新しい交換代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)の末尾に、引数で指定された集合の要素(元)を追加した
	 * 集合となる。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結する交換代数集合
	 * @return		自身と引数に指定された集合を連結した新しい交換代数集合
	 * 
	 * @since 0.94
	 */
	public ExAlgeSet addition(ExAlgeSet set) {
		ExAlgeSet resultSet = new ExAlgeSet(this.size() + set.size());
		resultSet.addAll(this);
		resultSet.addAll(set);
		return resultSet;
	}

	/**
	 * 引数に指定された交換代数集合の要素を除いた、新しい交換代数集合の
	 * インスタンスを返す。
	 * <br>
	 * 新しい交換代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)のうち、引数で指定された集合の要素(元)を除いた集合となる。
	 * 同一の要素とみなすのは、要素の {@link exalge2.Exalge#equals(Object)}
	 * メソッドが <tt>true</tt> を返す場合とする。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つ交換代数集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しい交換代数集合
	 * 
	 * @since 0.94
	 */
	public ExAlgeSet subtraction(ExAlgeSet set) {
		ExAlgeSet resultSet = new ExAlgeSet(this);
		resultSet.removeAll(set);
		return resultSet;
	}
	
	/**
	 * 交換代数に含まれる全ての基底を取り出す。
	 * <br>
	 * このメソッドが返す交換代数基底集合に、基底の重複はない。
	 * 
	 * @return 交換代数から取り出した基底集合
	 * 
	 * @since 0.94
	 */
	public ExBaseSet getBases() {
		ExBaseSet retBases = new ExBaseSet();
		for (Exalge alge : this) {
			if (!alge.isEmpty()) {
				retBases.fastAddAll(alge.data.keySet());
			}
		}
		return retBases;
	}

	/**
	 * この交換代数集合に含まれる基底のうち、ハットなし基底のみを取り出す。
	 * 
	 * @return	この交換代数集合に含まれるハットなし基底の集合
	 * 
	 * @since 0.970
	 */
	public ExBaseSet getNoHatBases() {
		ExBaseSet retBases = new ExBaseSet();
		for (Exalge alge : this) {
			if (!alge.isEmpty()) {
				for (ExBase base : alge.data.keySet()) {
					if (base.isNoHat()) {
						retBases.fastAdd(base);
					}
				}
			}
		}
		return retBases;
	}

	/**
	 * この交換代数集合に含まれる基底のうち、ハット基底のみを取り出す。
	 * 
	 * @return	この交換代数集合に含まれるハット基底の集合
	 * 
	 * @since 0.970
	 */
	public ExBaseSet getHatBases() {
		ExBaseSet retBases = new ExBaseSet();
		for (Exalge alge : this) {
			if (!alge.isEmpty()) {
				for (ExBase base : alge.data.keySet()) {
					if (base.isHat()) {
						retBases.fastAdd(base);
					}
				}
			}
		}
		return retBases;
	}

	/**
	 * この交換代数集合から、全ての基底についてハットを除去した
	 * 基底集合を取得する。
	 * 
	 * @return	この交換代数集合に含まれる全基底のハット除去後の基底集合
	 * 
	 * @since 0.970
	 */
	public ExBaseSet getBasesWithRemoveHat() {
		ExBaseSet retBases = new ExBaseSet();
		for (Exalge alge : this) {
			if (!alge.isEmpty()) {
				for (ExBase base : alge.data.keySet()) {
					retBases.fastAdd(base.removeHat());
				}
			}
		}
		return retBases;
	}

	/**
	 * この交換代数集合から、全ての基底についてハットを付加した
	 * 基底集合を取得する。
	 * 
	 * @return	この交換代数集合に含まれる全基底のハット付加後の基底集合
	 * 
	 * @since 0.970
	 */
	public ExBaseSet getBasesWithSetHat() {
		ExBaseSet retBases = new ExBaseSet();
		for (Exalge alge : this) {
			if (!alge.isEmpty()) {
				for (ExBase base : alge.data.keySet()) {
					retBases.fastAdd(base.setHat());
				}
			}
		}
		return retBases;
	}

	/**
	 * 自身に含まれる全ての交換代数の総和を計算した結果を取得する
	 * 
	 * @return 計算結果の交換代数
	 */
	public Exalge sum() {
		return Exalge.sum(this);
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、
	 * {@link exalge2.Exalge#oneValueProjection(BigDecimal)} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link exalge2.Exalge#oneValueProjection(BigDecimal)} した結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @param value	取り出す要素の値
	 * @return	指定された値と等しい要素のみを含む交換代数元の集合
	 * 
	 * @see exalge2.Exalge#oneValueProjection(BigDecimal)
	 * 
	 * @since 0.983
	 */
	public ExAlgeSet oneValueProjection(BigDecimal value) {
		//--- 要素なし
		if (this.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- プロジェクション
		ExAlgeSet newSet = new ExAlgeSet(this.size());
		for (Exalge alge : this) {
			Exalge palge = alge.oneValueProjection(value);
			if (!palge.isEmpty()) {
				newSet.fastAdd(palge);	// 空ではない要素のみを含む
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、
	 * {@link exalge2.Exalge#valuesProjection(Collection)} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link exalge2.Exalge#valuesProjection(Collection)} した結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @param values	取り出す要素の値のコレクション
	 * @return	指定されたコレクションに含まれる値と等しい要素のみを含む
	 * 			交換代数元の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @see exalge2.Exalge#valuesProjection(Collection)
	 * 
	 * @since 0.983
	 */
	public ExAlgeSet valuesProjection(Collection<? extends BigDecimal> values) {
		//--- コレクションの要素なし
		if (values.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- 要素なし
		if (this.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- プロジェクション
		ExAlgeSet newSet = new ExAlgeSet(this.size());
		for (Exalge alge : this) {
			Exalge palge = alge.valuesProjection(values);
			if (!palge.isEmpty()) {
				newSet.fastAdd(palge);	// 空ではない要素のみを含む
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、
	 * {@link exalge2.Exalge#nullProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link exalge2.Exalge#nullProjection()} した結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が <tt>null</tt> の要素のみを含む交換代数元の集合
	 * 
	 * @see exalge2.Exalge#nullProjection()
	 * 
	 * @since 0.983
	 */
	public ExAlgeSet nullProjection() {
		//--- 要素なし
		if (this.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- プロジェクション
		ExAlgeSet newSet = new ExAlgeSet(this.size());
		for (Exalge alge : this) {
			Exalge palge = alge.nullProjection();
			if (!palge.isEmpty()) {
				newSet.fastAdd(palge);	// 空ではない要素のみを含む
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、
	 * {@link exalge2.Exalge#nonullProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link exalge2.Exalge#nonullProjection()} した結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が <tt>null</tt> ではない要素のみを含む交換代数元の集合
	 * 
	 * @see exalge2.Exalge#nonullProjection()
	 * 
	 * @since 0.983
	 */
	public ExAlgeSet nonullProjection() {
		//--- 要素なし
		if (this.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- プロジェクション
		ExAlgeSet newSet = new ExAlgeSet(this.size());
		for (Exalge alge : this) {
			Exalge palge = alge.nonullProjection();
			if (!palge.isEmpty()) {
				newSet.fastAdd(palge);	// 空ではない要素のみを含む
			}
		}
		return newSet;
	}

	/**
	 * 自身に含まれるすべての交換代数元に対し、
	 * {@link exalge2.Exalge#zeroProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link exalge2.Exalge#zeroProjection()} した結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が 0 の要素のみを含む交換代数元の集合
	 * 
	 * @see exalge2.Exalge#zeroProjection()
	 * @since 0.984
	 */
	public ExAlgeSet zeroProjection() {
		//--- 要素なし
		if (this.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- プロジェクション
		ExAlgeSet newSet = new ExAlgeSet(this.size());
		for (Exalge alge : this) {
			Exalge palge = alge.zeroProjection();
			if (!palge.isEmpty()) {
				newSet.fastAdd(palge);	// 空ではない要素のみを含む
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれるすべての交換代数元に対し、
	 * {@link exalge2.Exalge#notzeroProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link exalge2.Exalge#notzeroProjection()} した結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が 0 ではない要素のみを含む交換代数元の集合
	 * 
	 * @see exalge2.Exalge#notzeroProjection()
	 * @since 0.984
	 */
	public ExAlgeSet notzeroProjection() {
		//--- 要素なし
		if (this.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- プロジェクション
		ExAlgeSet newSet = new ExAlgeSet(this.size());
		for (Exalge alge : this) {
			Exalge palge = alge.notzeroProjection();
			if (!palge.isEmpty()) {
				newSet.fastAdd(palge);	// 空ではない要素のみを含む
			}
		}
		return newSet;
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、プロジェクションした
	 * 結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素についてプロジェクションした結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param base	取り出す基底
	 * @return		指定の基底でプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @see exalge2.Exalge#projection(ExBase)
	 * 
	 * @since 0.94
	 */
	public ExAlgeSet projection(ExBase base) {
		//--- 要素なし
		if (this.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- プロジェクション
		ExAlgeSet newSet = new ExAlgeSet(this.size());
		Iterator<Exalge> it = this.iterator();
		while (it.hasNext()) {
			Exalge palge = it.next().projection(base);
			if (!palge.isEmpty()) {
				newSet.add(palge);	// 空ではない要素のみを含む
			}
		}
		return newSet;
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、プロジェクションした
	 * 結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素についてプロジェクションした結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param baseset	取り出す基底の集合
	 * @return			指定の基底集合でプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @see exalge2.Exalge#projection(ExBaseSet)
	 */
	public ExAlgeSet projection(ExBaseSet baseset) {
		//--- 要素なし
		if (this.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- プロジェクション
		ExAlgeSet newSet = new ExAlgeSet(this.size());
		Iterator<Exalge> it = this.iterator();
		while (it.hasNext()) {
			Exalge palge = it.next().projection(baseset);
			if (!palge.isEmpty()) {
				newSet.add(palge);	// 空ではない要素のみを含む
			}
		}
		return newSet;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、ハット基底キーを無視したプロジェクションの
	 * 結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素についてハット基底キーを無視したプロジェクションの結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param base	取り出す基底
	 * @return		指定の基底でプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @see exalge2.Exalge#generalProjection(ExBase)
	 * 
	 * @since 0.960
	 */
	public ExAlgeSet generalProjection(ExBase base) {
		//--- Check
		if (base == null) {
			throw new NullPointerException();
		}
		
		//--- 要素なし
		if (this.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- 基底生成
		ExBaseSet targetBases = new ExBaseSet(2);
		targetBases.fastAdd(base.removeHat());
		targetBases.fastAdd(base.setHat());
		
		//--- projection
		ExAlgeSet newSet = new ExAlgeSet(this.size());
		for (Exalge alge : this) {
			Exalge retProj = alge.projection(targetBases);
			if (!retProj.isEmpty()) {
				newSet.add(retProj);
			}
		}
		
		return newSet;
	}
	
	/**
	 * 自身に含まれる全ての交換代数元に対し、ハット基底キーを無視したプロジェクションの
	 * 結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素についてハット基底キーを無視したプロジェクションの結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param baseset	取り出す基底の集合
	 * @return			指定の基底集合でプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @see exalge2.Exalge#generalProjection(ExBaseSet)
	 * 
	 * @since 0.960
	 */
	public ExAlgeSet generalProjection(ExBaseSet baseset) {
		//--- Check
		if (baseset == null) {
			throw new NullPointerException();
		}
		
		//--- 要素なし
		if (this.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- 基底生成
		ExBaseSet targetBases = baseset.removeHat();
		targetBases.fastAddAll(targetBases.setHat());
		
		//--- projection
		ExAlgeSet newSet = new ExAlgeSet(this.size());
		for (Exalge alge : this) {
			Exalge retProj = alge.projection(targetBases);
			if (!retProj.isEmpty()) {
				newSet.add(retProj);
			}
		}
		
		return newSet;
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、基底パターンによって
	 * プロジェクションした結果を保持する集合を返す。
	 * <br>
	 * <br>
	 * 指定された基底のハットキー以外の基底キーに一致する基底のみを
	 * 取得し、その集合を返す。ハットキー以外の基底キーにアスタリスク
	 * 文字<code>('*')</code>が含まれている場合、0文字以上の任意の文字に
	 * マッチするワイルドカードとみなす。
	 * <br>
	 * このメソッドでは、全要素について、この基底パターンに一致する基底のみで
	 * プロジェクションした結果となる交換代数元を持つ、新しい交換代数集合を生成する。
	 * このとき、プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param base	基底パターンとみなす交換代数基底
	 * @return		指定の基底パターンでプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.94
	 * 
	 * @see exalge2.Exalge#patternProjection(ExBase)
	 */
	public ExAlgeSet patternProjection(ExBase base) {
		ExBasePattern pattern = new ExBasePattern(base, true);
		return patternProjection(pattern);
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、基底パターンによって
	 * プロジェクションした結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について、指定された基底パターンに一致する基底のみで
	 * プロジェクションした結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param pattern	基底パターン
	 * @return		指定の基底パターンでプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.94
	 * 
	 * @see exalge2.Exalge#patternProjection(ExBasePattern)
	 */
	public ExAlgeSet patternProjection(ExBasePattern pattern) {
		//--- 要素なし
		if (this.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- 取得対象基底の収集
		ExBaseSet targetBases = this.getBases().getMatchedBases(pattern);
		
		//--- プロジェクション
		return this.projection(targetBases);
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、基底パターンによって
	 * プロジェクションした結果を保持する集合を返す。
	 * <br>
	 * 指定された集合に含まれる基底の、ハットキー以外の基底キーに
	 * 一致する基底のみを取得し、その集合を返す。ハットキー以外の
	 * 基底キーにアスタリスク文字<code>('*')</code>が含まれている場合、
	 * 0文字以上の任意の文字にマッチするワイルドカードとみなす。
	 * <br>
	 * このメソッドでは、全要素について、指定された基底パターンのどれかに一致する
	 * 基底のみでプロジェクションした結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param bases	基底パターンとみなす基底の集合
	 * @return		指定の基底パターンでプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.94
	 * 
	 * @see exalge2.Exalge#patternProjection(ExBaseSet)
	 */
	public ExAlgeSet patternProjection(ExBaseSet bases) {
		ExBasePatternSet patterns = new ExBasePatternSet(bases, true);
		return patternProjection(patterns);
	}

	/**
	 * 自身に含まれる全ての交換代数元に対し、基底パターンによって
	 * プロジェクションした結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について、指定された基底パターンのどれかに一致する
	 * 基底のみでプロジェクションした結果となる
	 * 交換代数元を持つ、新しい交換代数集合を生成する。このとき、
	 * プロジェクションの結果、要素が空となる交換代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return		指定の基底パターンでプロジェクションした結果を持つ交換代数集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * 
	 * @since 0.94
	 * 
	 * @see exalge2.Exalge#patternProjection(ExBasePatternSet)
	 */
	public ExAlgeSet patternProjection(ExBasePatternSet patterns) {
		//--- 要素なし
		if (this.isEmpty()) {
			return new ExAlgeSet(0);
		}
		
		//--- 取得対象基底の収集
		ExBaseSet targetBases = this.getBases().getMatchedBases(patterns);
		
		//--- プロジェクション
		return this.projection(targetBases);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Override
	//------------------------------------------------------------

	/**
	 * 集合の指定された位置にある要素を、指定された要素で置き換える。
	 * <br>
	 * このメソッドでは、<tt>null</tt> は許容されない。
	 * 
	 * @param index 置換される要素のインデックス
	 * @param element 指定された位置に格納される要素
	 * 
	 * @return 指定された位置に以前あった要素
	 * 
	 * @throws NullPointerException <code>element</code> が <tt>null</tt> の場合にスローされる
	 * @throws IndexOutOfBoundsException インデックスが範囲外の場合にスローされる
	 * 
	 * @see java.util.ArrayList#set(int, Object)
	 */
	public Exalge set(int index, Exalge element) {
		// check
		if (element == null)
			throw new NullPointerException("element is null");
		// set
		return super.set(index, element);
	}

	/**
	 * 集合(リスト)の最後に、指定された要素を追加する。
	 * <br>
	 * このメソッドでは、<tt>null</tt> は許容されない。
	 * 
	 * @param alge 追加される要素
	 * 
	 * @return true (Collection.add の汎用規約どおり)
	 * 
	 * @throws NullPointerException <code>alge</code> が <tt>null</tt> の場合にスローされる
	 * 
	 * @see java.util.ArrayList#add(Object)
	 */
	public boolean add(Exalge alge) {
		// check
		if (alge == null)
			throw new NullPointerException("arge is null");
		// add
		return super.add(alge);
	}

	/**
	 * 集合の指定された位置に、指定された要素を挿入する。
	 * <br>
	 * 現在その位置にある要素と後続の要素は後方に移動する。
	 * <br>
	 * このメソッドでは、<tt>null</tt> は許容されない。
	 * 
	 * @param index 挿入する位置のインデックス
	 * @param alge 挿入される要素
	 * 
	 * @throws NullPointerException <code>alge</code> が <tt>null</tt> の場合にスローされる
	 * @throws IndexOutOfBoundsException インデックスが範囲外の場合にスローされる
	 * 
	 * @see java.util.ArrayList#add(int, Object)
	 */
	public void add(int index, Exalge alge) {
		// check
		if (alge == null)
			throw new NullPointerException("arge is null");
		// add
		super.add(index, alge);
	}

	/**
	 * 集合(リスト)の末尾に、指定されたコレクションの全ての要素を追加する。
	 * ただし、<tt>null</tt> の要素は無視される。
	 * <br>
	 * これらの要素は、指定されたコレクションの <code>Iterator</code> が返す
	 * 順序で追加される。
	 * 
	 * @param c 追加する要素のコレクション
	 * 
	 * @return この呼び出しの結果、この集合が変更された場合は true
	 * 
	 * @throws NullPointerException 指定されたコレクションが <tt>null</tt> の場合にスローされる
	 * 
	 * @see java.util.ArrayList#addAll(Collection)
	 * 
	 */
	public boolean addAll(Collection<? extends Exalge> c) {
		int numCur = this.size();
		int numNew = c.size();
		ensureCapacity( this.size() + numNew );
		Iterator<? extends Exalge> it = c.iterator();
		while (it.hasNext()) {
			Exalge alge = it.next();
			if (alge != null) {
				super.add(alge);
			}
		}
		return (numCur != this.size());
	}

	/**
	 * 集合(リスト)の指定された位置から、指定されたコレクションの
	 * すべての要素を挿入する。
	 * ただし、<tt>null</tt> の要素は無視される。
	 * <br>
	 * 指定された位置に要素がある場合、その位置の要素を移動させ、
	 * 後続の要素を後方に移動して、それぞれのインデックスを増やす。
	 * 新しい要素は、指定されたコレクションの <code>Iterator</code> が
	 * 返す順序で挿入される。
	 * 
	 * @param index 最初の要素を挿入する位置のインデックス
	 * @param c 挿入される要素のコレクション
	 * 
	 * @return この呼び出しの結果、この集合が変更された場合は true
	 * 
	 * @throws NullPointerException 指定されたコレクションが <tt>null</tt> の場合にスローされる
	 * @throws IndexOutOfBoundsException インデックスが範囲外の場合
	 * 
	 * @see java.util.ArrayList#addAll(int, Collection)
	 * 
	 */
	public boolean addAll(int index, Collection<? extends Exalge> c) {
		ExAlgeSet newSet = new ExAlgeSet(c);
		return super.addAll(index, newSet);
	}
	
    /**
     * 集合のシャローコピーを返す。
     * 要素自体は複製されない。
     * 
     * @return この集合のシャローコピー
     */
	public ExAlgeSet clone() {
		ExAlgeSet newSet = (ExAlgeSet) super.clone();
		return newSet;
	}

    /**
     * 指定された要素を集合に追加する。
     * このメソッドは、値の整合性をチェックせず、そのままコレクションに追加する。
     * 
     * @param alge 集合に追加する要素
     * 
     * @return 指定された要素が追加された場合に true
     * 
     * @see java.util.HashSet#add(Object)
     * 
     * @since 0.982
     */
	protected boolean fastAdd(Exalge alge) {
		return super.add(alge);
	}

    /**
     * 指定されたコレクションのすべての要素をこの集合に追加する。
     * このメソッドは、値の整合性をチェックせず、そのままコレクションに追加する。
     * 
     * @param c この集合に追加するコレクション
     * 
     * @return この呼び出しの結果、この集合が変更された場合は true
     * 
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
     * 
     * @see java.util.HashSet#addAll(Collection)
     * 
     * @since 0.982
     */
	protected boolean fastAddAll(Collection<? extends Exalge> c) {
		return super.addAll(c);
	}

	//------------------------------------------------------------
	// I/O
	//------------------------------------------------------------
	
	/**
	 * 交換代数集合の内容を、指定のファイルに CSV フォーマットで出力する。
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
	 * 交換代数集合の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
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
	 * @since 0.91
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
	 * 交換代数集合の内容を、CSVフォーマットの文字列に変換する。
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
	 * CSV フォーマットのファイルを読み込み、新しい交換代数集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>ExAlgeSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.91
	 */
	static public ExAlgeSet fromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		CsvReader reader = new CsvReader(csvFile);
		ExAlgeSet newSet = new ExAlgeSet();
		
		try {
			newSet.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newSet;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しい交換代数集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>ExAlgeSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.93
	 */
	static public ExAlgeSet fromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		ExAlgeSet newSet = new ExAlgeSet();
		
		try {
			newSet.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newSet;
	}
	
	/**
	 * CSV フォーマットの文字列を読み込み、新しい交換代数集合を生成する。
	 * <p>
	 * 基本的に、文字列に記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvString 読み込む CSV フォーマットの文字列
	 * @return 文字列の内容で生成された、新しい <code>ExAlgeSet</code> インスタンスを返す。
	 * 
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.984
	 */
	static public ExAlgeSet fromCsvString(String csvString) throws CsvFormatException
	{
		CsvReader reader = new CsvReader(new StringReader(csvString));
		ExAlgeSet newSet = new ExAlgeSet();
		
		try {
			newSet.readFromCSV(reader);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		finally {
			reader.close();
		}
		
		return newSet;
	}
	
	/**
	 * 交換代数集合の内容を、指定のファイルに XML フォーマットで出力する。
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
	 * 交換代数集合の内容から、XML ドキュメントを生成する。
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
	 * 交換代数集合の内容を、XML フォーマットの文字列に変換する。
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
	 * XML フォーマットのファイルを読み込み、新しい交換代数集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlFile 読み込む XML ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>ExAlgeSet</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.91
	 */
	static public ExAlgeSet fromXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException,
				XmlDomParseException
	{
		XmlDocument xmlDocument = XmlDocument.fromFile(xmlFile);
		
		return ExAlgeSet.fromXML(xmlDocument);
	}
	
	/**
	 * 指定された XML ドキュメントを解析し、新しい交換代数集合を生成する。
	 * <p>
	 * 基本的に、XML ノードの順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlDocument 解析対象の XML ドキュメント
	 * 
	 * @return 解析により生成された、新しい <code>ExAlgeSet</code> インスタンスを返す。
	 * 
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.91
	 */
	static public ExAlgeSet fromXML(XmlDocument xmlDocument)
		throws XmlDomParseException
	{
		Element root = xmlDocument.getDocumentElement();
		
		// Check exist root element
		if (root == null) {
			throw new XmlDomParseException("Root element not found.");
		}
		
		ExAlgeSet newSet = new ExAlgeSet();
		newSet.decodeFromElement(xmlDocument, root);
		
		return newSet;
	}
	
	/**
	 * XML フォーマットの文字列を読み込み、新しい交換代数集合を生成する。
	 * <p>
	 * 基本的に、文字列に記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlString 読み込む XML フォーマットの文字列
	 * 
	 * @return 文字列の内容で生成された、新しい <code>ExAlgeSet</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.984
	 */
	static public ExAlgeSet fromXmlString(String xmlString)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException, XmlDomParseException
	{
		XmlDocument xmlDocument = XmlDocument.fromXmlString(xmlString);
		
		return ExAlgeSet.fromXML(xmlDocument);
	}

	//------------------------------------------------------------
	// Internal methods for I/O
	//------------------------------------------------------------
	
	protected void writeToCSV(CsvWriter writer)
		throws IOException
	{
		Iterator<Exalge> it = this.iterator();
		
		// 最初の有効なエントリ
		while (it.hasNext()) {
			Exalge alge = it.next();
			if (!alge.isEmpty()) {
				alge.writeToCSV(writer);
				break;
			}
		}
		
		// 2番目以降の有効なエントリ
		while (it.hasNext()) {
			Exalge alge = it.next();
			if (alge.isEmpty()) {
				// 出力なし
				continue;
			}
			
			// 交換代数の元の区切りを挿入
			writer.writeBlankLine();
			
			// 交換代数の元を出力
			alge.writeToCSV(writer);
		}
		
		writer.flush();
	}
	
	protected void readFromCSV(CsvReader reader)
		throws IOException, CsvFormatException
	{
		CsvReader.CsvRecord record;
		
		// コメント行は有効
		reader.setLineCommentEnable(true);
		
		// read elements
		Exalge newAlge = null;
		while ((record = reader.readCsvRecord()) != null) {
			// 空行、コメント行は、元の区切りとする
			if (!record.hasFields() || !record.hasValues()) {
				if (newAlge != null && !newAlge.isEmpty()) {
					// 元に実体があれば、集合に格納
					fastAdd(newAlge);
				}
				newAlge = null;
				continue;
			}
			
			// read record
			if (newAlge == null) {
				newAlge = new Exalge();
			}
			newAlge.readRecordFromCSV(record);
		}
		
		if (newAlge != null && !newAlge.isEmpty()) {
			// 元に実体があれば、集合に格納
			fastAdd(newAlge);
		}
	}
	
	protected Element encodeToElement(XmlDocument xmlDocument)	throws DOMException
	{
		// create node
		Element node = xmlDocument.createElement(Exalge.XML_TOP_ELEMENT_NAME);
		
		// create entries
		Iterator<Exalge> it = this.iterator();
		while (it.hasNext()) {
			Exalge alge = it.next();
			Element algeNode = alge.encodeToElement(xmlDocument);
			node.appendChild(algeNode);
		}
		
		// completed
		return node;
	}
	
	protected void decodeFromElement(XmlDocument xmlDocument, Element xmlElement)
		throws XmlDomParseException
	{
		// check top Element name
		String strInput = xmlElement.getNodeName();
		if (!strInput.equals(Exalge.XML_TOP_ELEMENT_NAME)) {
			// Illegal element name
			throw new XmlDomParseException(String.format("Root element must be <%s> : <%s>", Exalge.XML_TOP_ELEMENT_NAME, String.valueOf(strInput)));
		}
		
		// decode internal items
		Node child = xmlElement.getFirstChild();
		for (; child != null; child = child.getNextSibling()) {
			short childNodeType = child.getNodeType();
			if (childNodeType == Node.ELEMENT_NODE) {
				Element itemElement = (Element)child;
				
				Exalge newAlge = new Exalge();
				newAlge.decodeFromElement(xmlDocument, itemElement, xmlElement);
				// 元に実体があれば、集合に格納
				if (!newAlge.isEmpty()) {
					this.add(newAlge);
				}
			}
		}
	}
}
