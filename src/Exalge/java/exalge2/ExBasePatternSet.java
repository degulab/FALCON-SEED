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
 * @(#)ExBasePatternSet.java	0.984	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBasePatternSet.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBasePatternSet.java	0.94	2008/05/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

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
 * 交換代数の基底パターンの集合を保持するクラス。
 * 
 * <p>交換代数基底パターン <code>ExBasePattern</code> インスタンスの集合であり、
 * 同じ値が重複して含まれることはない。
 * <br>
 * このクラスは、挿入型 {@link java.util.LinkedHashSet} の実装となる。
 * したがって、追加挿入メソッド、ファイル入出力において、
 * クラス内での基底パターンの順序は基本的に維持される。詳細は、各メソッドの説明を参照のこと。
 * <p>
 * このクラスでは、<tt>null</tt> を許容しない。
 * <br>
 * また、<b>この実装は同期化されない</b>。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>ExBasePatternSet</code> は、CSV ファイル、XML ファイル(XML ドキュメント)形式の
 * 入出力インタフェースを提供する。
 * <br>
 * 入出力されるファイルにおいて、共通の事項は、次のとおり。
 * <ul>
 * <li>基底キーに、次の文字は使用できない。<br>
 * &lt; &gt; - , ^ &quot; % &amp; ? | @ ' " (空白)
 * <li>ハットキーに指定可能な文字列は、次のもののみ(大文字、小文字は区別しない)。<br>
 * &nbsp;&nbsp;* HAT ^ NO_HAT (空白)
 * </ul>
 * CSVファイルは、カンマ区切りのテキストファイルで、次のフォーマットに従う。
 * <ul>
 * <li>行の先頭から、次のようなカラム構成となる。<br>
 * <i>ハットキー</i>，<i>名前キー</i>，<i>単位キー</i>，<i>時間キー</i>，<i>サブジェクトキー</i>
 * <li>文字コードは、実行するプラットフォームの処理系に依存する。
 * <li>CSVファイルの先頭(第1行目)は識別子となり、必ず次の文字列でなければならない(大文字、小文字は区別しない)。<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;<b>#ExBasePatternSet</b>
 * <li>CSVファイルの第2行目はヘッダー行となり、必ず次の文字列でなければならない(全て小文字)。<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;<b>hat,name,unit,time,subject</b>
 * <li>空行は無視される。
 * <li>各カラムの前後空白は、無視される。
 * <li><code>'#'</code> で始まる行は、コメント行とみなす。
 * <li>ハットキーが省略(空文字)の場合、ワイルドカード記号('*')が代入される。
 * <li>名前キー以降のカラムが省略(空文字)の場合、ワイルドカード記号('*')が代入される。
 * </ul>
 * また、CSV ファイルの読み込みにおいて、次の場合に {@link exalge2.io.csv.CsvFormatException} 例外がスローされる。
 * <ul>
 * <li>識別子が正しくない、もしくは識別子が存在しない場合
 * <li>ヘッダー行の内容が正しくない、もしくはヘッダー行が存在しない場合
 * <li>ハットキー文字列が指定可能な文字列ではない場合
 * <li>各基底キーに使用できない文字が含まれている場合
 * </ul>
 * なお、CSV ファイルの入出力は、{@link #toCSV(File)}、{@link #fromCSV(File)} により行う。
 * また、指定した文字セットでの入出力は、{@link #toCSV(File, String)}、{@link #fromCSV(File, String)} 
 * により行う。
 * <p>
 * XMLドキュメントは、次の構成となる。XMLファイルも同様のフォーマットに従う。
 * <pre><code>
 * &lt;ExbasePatternSet&gt;
 *   &lt;Exbase hat=&quot;<i>ハットキー</i>&quot; name=&quot;<i>名前キー</i>&quot; unit=&quot;<i>単位キー</i>&quot; time=&quot;<i>時間キー</i>&quot; subject=&quot;<i>サブジェクトキー</i>&quot; /&gt;
 *        ・
 *         ・
 *         ・
 * &lt;/ExbasePatternSet&gt;
 * </code></pre>
 * <ul>
 * <li>ルートノードは単一の &lt;ExbasePatternSet&gt; となる。
 * <li>&lt;Exbase&gt; が、基底パターンを示すノードとなる。属性が基底パターンの基底キーとなる。
 * <li>&lt;Exbase&gt; ノードは、&lt;ExbasePatternSet&gt; の子ノードであり、複数存在する。
 * <li>文字コードは、&quot;UTF-8&quot; とする。
 * <li>各基底キーの前後空白は無視される。
 * <li>ハットキーが省略された場合、ワイルドカード記号('*')が代入される。
 * <li>基底キーが省略された場合、ワイルドカード記号('*')が代入される。
 * </ul>
 * また、XML ドキュメントのパース(XML ファイルの読み込み)において、
 * 次の場合に {@link exalge2.io.xml.XmlDomParseException} 例外がスローされる。
 * <ul>
 * <li>XML のノードツリー構成が正しくない場合
 * <li>ハットキー文字列が指定可能な文字列ではない場合
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
 * 
 * @see java.util.HashSet
 *
 */
public class ExBasePatternSet extends LinkedHashSet<ExBasePattern> implements exalge2.io.IDataOutput
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** CSV フォーマットにおけるファイル先頭の識別子 **/
	static public final String HEADER_ID = "#ExBasePatternSet";

	/** CSV フォーマットにおけるハットキー列の列名 **/
	static public final String HEADER_HAT     = "hat";
	/** CSV フォーマットにおける名前キー列の列名 **/
	static public final String HEADER_NAME    = "name";
	/** CSV フォーマットにおける単位キー列の列名 **/
	static public final String HEADER_UNIT    = "unit";
	/** CSV フォーマットにおける時間キー列の列名 **/
	static public final String HEADER_TIME    = "time";
	/** CSV フォーマットにおけるサブジェクトキー列の列名 **/
	static public final String HEADER_SUBJECT = "subject";

	static private final String[] HEADER_NAMES = {
		HEADER_HAT, HEADER_NAME, HEADER_UNIT, HEADER_TIME, HEADER_SUBJECT,
	};
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 新しい空の基底パターン集合を生成する。
	 * 
	 * @see java.util.LinkedHashSet#LinkedHashSet()
	 */
	public ExBasePatternSet() {
		super();
	}

    /**
     * 指定されたコレクションの要素を格納する新規基底パターン集合を作成する。
     * <br>
     * デフォルトの負荷係数、および指定されたコレクションの要素を
     * 格納するのに十分な初期要領により作成される。
     * <br>
     * コレクションに含まれる <tt>null</tt> 要素は無視される。
     * 
     * @param c 要素がセットに配置される <code>ExBasePattern</code> のコレクション
     * 
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
     * 
     * @see java.util.LinkedHashSet#LinkedHashSet(Collection)
     */
	public ExBasePatternSet(Collection<? extends ExBasePattern> c) {
		super(Math.max((int) (c.size()/.75f) + 1, 16));
		addAll(c);
	}

    /**
     * 指定された初期容量および指定された負荷係数で、新しい空の基底パターン集合を生成する。
     * 
     * @param initialCapacity 基底パターン集合(セット)の初期容量
     * @param loadFactor 基底パターン集合(セット)の負荷係数
     * 
     * @throws 初期容量が 0 より小さいか、負荷係数が正の値ではない場合にスローされる
     * 
     * @see java.util.LinkedHashSet#LinkedHashSet(int, float)
     */
	public ExBasePatternSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

    /**
     * 指定された初期容量およびデフォルトの負荷係数で、新しい空の基底パターン集合を生成する。
     * 
     * @param initialCapacity 基底集合(セット)の初期容量
     * 
     * @throws 初期容量が 0 より小さい場合にスローされる
     * 
     * @see java.util.LinkedHashSet#LinkedHashSet(int)
     */
	public ExBasePatternSet(int initialCapacity) {
		super(initialCapacity);
	}

    /**
     * 指定された基底集合の要素を格納する新規基底パターン集合を作成する。
     * <br>
     * 基本的に各基底キーの文字列をそのまま継承する。基底キーに含まれる
     * アスタリスク文字はワイルドカードとみなされる。
     * <br>
     * コレクションに含まれる <tt>null</tt> 要素は無視される。
     * 
     * @param bases 要素がセットに配置される交換代数基底集合
     * @param ignoreHatKey	<tt>true</tt>の場合、ハットキーはワイルドカードに置き換えられる。
     * 
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
     * 
     * @see java.util.LinkedHashSet#LinkedHashSet(Collection)
     */
	public ExBasePatternSet(ExBaseSet bases, boolean ignoreHatKey) {
		super(bases.size());
		Iterator<ExBase> it = bases.iterator();
		while (it.hasNext()) {
			ExBasePattern pattern = new ExBasePattern(it.next(), ignoreHatKey);
			add(pattern);
		}
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 基底パターン集合を連結した、新しい基底パターン集合のインスタンスを返す。
	 * <br>
	 * 基底パターン集合では、要素の基底パターンが重複することはないため、{@link #union(ExBasePatternSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結する基底パターン集合
	 * @return		自身と引数に指定された集合を連結した新しい基底パターン集合
	 * 
	 * @since 0.982
	 */
	public ExBasePatternSet addition(ExBasePatternSet set) {
		ExBasePatternSet newSet = (ExBasePatternSet)clone();
		newSet.fastAddAll(set);
		return newSet;
	}

	/**
	 * 引数に指定された基底パターン集合の要素を除いた、新しい基底パターン集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底パターン集合では、要素の基底パターンが重複することはないため、{@link #difference(ExBasePatternSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つ基底パターン集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しい基底パターン集合
	 * 
	 * @since 0.982
	 */
	public ExBasePatternSet subtraction(ExBasePatternSet set) {
		ExBasePatternSet newSet = (ExBasePatternSet)clone();
		newSet.removeAll(set);
		return newSet;
	}

	/**
	 * 指定の基底パターン集合との和を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 和を取る基底パターン集合の一方
	 * @return 二つの基底パターン集合の和となるインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public ExBasePatternSet union(ExBasePatternSet set) {
		ExBasePatternSet newSet = (ExBasePatternSet)clone();
		newSet.fastAddAll(set);
		return newSet;
	}

	/**
	 * 指定の基底パターン集合との積(共通部分)を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 積を取る基底パターン集合の一方
	 * @return 二つの基底パターン集合の積となるインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public ExBasePatternSet intersection(ExBasePatternSet set) {
		ExBasePatternSet newSet = (ExBasePatternSet)clone();
		newSet.retainAll(set);
		return newSet;
	}

	/**
	 * 指定の基底パターン集合との差を返す。
	 * <br>
	 * 集合の差は、このインスタンスの基底パターン集合から、
	 * 指定された基底パターン集合を除いた集合となる。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 差し引く基底パターンの集合
	 * @return 指定された基底パターン集合を差し引いた結果となるインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public ExBasePatternSet difference(ExBasePatternSet set) {
		ExBasePatternSet newSet = (ExBasePatternSet)clone();
		newSet.removeAll(set);
		return newSet;
	}
	
	/**
	 * 指定の基底がこの基底パターン集合のどれかにマッチするかどうかを判定する。
	 * 
	 * @param exbase 一致を判定する交換代数基底
	 * 
	 * @return このパターン集合のどれかと指定の基底が一致するときだけ true を返す
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public boolean matches(ExBase exbase) {
		if (exbase == null)
			throw new NullPointerException();
		
		Iterator<ExBasePattern> it = iterator();
		while (it.hasNext()) {
			ExBasePattern pattern = it.next();
			if (pattern.matches(exbase)) {
				return true;
			}
		}
		
		return false;	// no matched
	}

	//------------------------------------------------------------
	// Override
	//------------------------------------------------------------

    /**
     * 指定された要素が基底パターン集合の要素として存在しない場合に、
     * その要素を基底パターン集合に追加する。
     * <p>
     * このメソッドでは、<tt>null</tt> は許容されない。
     * 
     * @param pattern 集合に追加する要素
     * 
     * @return 指定された要素を保持していなかった場合は true
     * 
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
     */
	public boolean add(ExBasePattern pattern) {
		// Check
		if (pattern == null)
			throw new NullPointerException();
		// add
		return super.add(pattern);
	}

    /**
     * 指定されたコレクションのすべての要素をこの基底パターン集合に追加する。
     * <br>
     * 指定されたコレクションに含まれる <tt>null</tt> 要素は無視される。
     * 
     * @param c この基底集合に追加するコレクション
     * 
     * @return この呼び出しの結果、この基底パターン集合が変更された場合は true
     * 
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
     * 
     * @see java.util.LinkedHashSet#addAll(Collection)
     */
	public boolean addAll(Collection<? extends ExBasePattern> c) {
		boolean modified = false;
		Iterator<? extends ExBasePattern> it = c.iterator();
		while (it.hasNext()) {
			ExBasePattern pattern = it.next();
			if (pattern != null) {
				if (super.add(pattern)) {
					modified = true;
				}
			}
		}
		return modified;
	}

    /**
     * 基底集合のシャローコピーを返す。
     * 要素自体は複製されない。
     * 
     * @return この基底集合のシャローコピー
     */
	public ExBasePatternSet clone() {
		ExBasePatternSet newSet = (ExBasePatternSet) super.clone();
		return newSet;
	}

	//------------------------------------------------------------
	// I/O
	//------------------------------------------------------------
	
	/**
	 * 基底パターン集合の内容を、指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @since 0.982
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
	 * 基底パターン集合の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
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
	 * @since 0.982
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
	 * 基底パターン集合の内容を、CSVフォーマットの文字列に変換する。
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
	 * CSV フォーマットのファイルを読み込み、新しい基底パターン集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>ExBasePatternSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.982
	 */
	static public ExBasePatternSet fromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		CsvReader reader = new CsvReader(csvFile);
		ExBasePatternSet newSet = new ExBasePatternSet();
		try {
			newSet.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newSet;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しい基底パターン集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>ExBasePatternSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.982
	 */
	static public ExBasePatternSet fromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		ExBasePatternSet newSet = new ExBasePatternSet();
		try {
			newSet.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newSet;
	}
	
	/**
	 * CSV フォーマットの文字列を読み込み、新しい基底パターン集合を生成する。
	 * <p>
	 * 基本的に、文字列に記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvString 読み込む CSV フォーマットの文字列
	 * @return 文字列の内容で生成された、新しい <code>ExBasePatternSet</code> インスタンスを返す。
	 * 
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.984
	 */
	static public ExBasePatternSet fromCsvString(String csvString) throws CsvFormatException
	{
		CsvReader reader = new CsvReader(new StringReader(csvString));
		ExBasePatternSet newSet = new ExBasePatternSet();
		
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
	 * 基底パターン集合の内容を、指定のファイルに XML フォーマットで出力する。
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
	 * @since 0.982
	 */
	public void toXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, DOMException,
				TransformerConfigurationException, TransformerException
	{
		XmlDocument xml = toXML();
		
		xml.toFile(xmlFile);
	}
	
	/**
	 * 基底パターン集合の内容から、XML ドキュメントを生成する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @return 生成された XML ドキュメント({@link exalge2.io.xml.XmlDocument} インスタンス)
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 * 
	 * @since 0.982
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
	 * 基底パターン集合の内容を、XML フォーマットの文字列に変換する。
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
	 * XML フォーマットのファイルを読み込み、新しい基底パターン集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlFile 読み込む XML ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>ExBasePatternSet</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.982
	 */
	static public ExBasePatternSet fromXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException,
				XmlDomParseException
	{
		XmlDocument xmlDocument = XmlDocument.fromFile(xmlFile);
		
		return ExBasePatternSet.fromXML(xmlDocument);
	}
	
	/**
	 * 指定された XML ドキュメントを解析し、新しい基底パターン集合を生成する。
	 * <p>
	 * 基本的に、XML ノードの順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlDocument 解析対象の XML ドキュメント
	 * 
	 * @return 解析により生成された、新しい <code>ExBasePatternSet</code> インスタンスを返す。
	 * 
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.982
	 */
	static public ExBasePatternSet fromXML(XmlDocument xmlDocument)
		throws XmlDomParseException
	{
		Element root = xmlDocument.getDocumentElement();
		
		if (root == null) {
			throw new XmlDomParseException("Root element not found.");
		}

		ExBasePatternSet newSet = new ExBasePatternSet();
		newSet.decodeFromElement(xmlDocument, root);
		
		return newSet;
	}
	
	/**
	 * XML フォーマットの文字列を読み込み、新しい基底パターン集合を生成する。
	 * <p>
	 * 基本的に、文字列に記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlString 読み込む XML フォーマットの文字列
	 * 
	 * @return 文字列の内容で生成された、新しい <code>ExBasePatternSet</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.984
	 */
	static public ExBasePatternSet fromXmlString(String xmlString)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException, XmlDomParseException
	{
		XmlDocument xmlDocument = XmlDocument.fromXmlString(xmlString);
		
		return ExBasePatternSet.fromXML(xmlDocument);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

    /**
     * 指定された要素が基底パターン集合の要素として存在しない場合に、その要素を基底パターン集合に追加する。
     * このメソッドは、値の整合性をチェックせず、そのままコレクションに追加する。
     * 
     * @param pattern 集合に追加する要素
     * 
     * @return 指定された要素を保持していなかった場合は true
     * 
     * @see java.util.HashSet#add(Object)
     * 
     * @since 0.982
     */
	protected boolean fastAdd(ExBasePattern pattern) {
		return super.add(pattern);
	}

    /**
     * 指定されたコレクションのすべての要素をこの基底パターン集合に追加する。
     * このメソッドは、値の整合性をチェックせず、そのままコレクションに追加する。
     * 
     * @param c この基底パターン集合に追加するコレクション
     * 
     * @return この呼び出しの結果、この基底パターン集合が変更された場合は true
     * 
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
     * 
     * @see java.util.HashSet#addAll(Collection)
     * 
     * @since 0.982
     */
	protected boolean fastAddAll(Collection<? extends ExBasePattern> c) {
		return super.addAll(c);
	}
	
	/**
	 * 指定された <code>writer</code> に、このオブジェクトの全ての要素を出力する。
	 * 
	 * @param writer	出力に使用する <code>CsvWriter</code> オブジェクト
	 * 
	 * @throws IOException	入出力エラーが発生した場合
	 * 
	 * @since 0.982
	 */
	protected void writeToCSV(CsvWriter writer) throws IOException
	{
		// write header
		//--- ID
		writer.writeLine(HEADER_ID);
		//--- field header
		for (String hname : HEADER_NAMES) {
			writer.writeField(hname);
		}
		writer.newLine();
		
		// write elements
		for (ExBasePattern pattern : this) {
			pattern.writeFieldToCSV(writer, true);
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
	 * 
	 * @since 0.982
	 */
	protected void readFromCSV(CsvReader reader) throws IOException, CsvFormatException
	{
		CsvReader.CsvRecord record;
		
		// コメント行は無効
		reader.setLineCommentEnable(false);
		
		// check ID
		record = reader.readCsvRecord();
		if (record == null || !record.hasFields()) {
			// undefined ExBasePatternSet CSV ID
			throw new CsvFormatException("ExBasePatternSet file ID not found.");
		} else {
			CsvReader.CsvField field = record.getField(0);
			if (field == null || !HEADER_ID.equalsIgnoreCase(field.getValue())) {
				throw new CsvFormatException("Illegal ExBasePatternSet file ID.", record.getLineNo(), 1);
			}
			// 以降のフィールドは無視
		}
		
		// check header
		record = reader.readCsvRecord();
		if (record == null || !record.hasFields()) {
			// undefined ExBasePatternSet CSV header
			throw new CsvFormatException("ExBasePatternSet header not found.");
		}
		for (int i = 0; i < HEADER_NAMES.length; i++) {
			CsvReader.CsvField field = record.getField(i);
			if (field == null || !HEADER_NAMES[i].equalsIgnoreCase(field.getValue())) {
				throw new CsvFormatException("Illegal ExBasePatternSet header.", record.getLineNo(), (i+1));
			}
		}
		
		// 2行目以降のコメントは有効
		reader.setLineCommentEnable(true);
		
		// read elements
		while ((record = reader.readCsvRecord()) != null) {
			// 空行、もしくは値のないレコードはスキップ
			if (!record.hasFields() || !record.hasValues()) {
				continue;
			}
			
			// フィールドの値読み出し
			CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);
			ExBasePattern pattern = ExBasePattern.readFieldFromCSV(freader, true);
			
			// add
			fastAdd(pattern);
		}
	}
	
	/** &lt;ExBasePatternSet&gt; 要素名 **/
	static protected final String XML_ELEMENT_NAME = "ExbasePatternSet";
	
	/**
	 * このオブジェクトの全ての要素を、XML DOM ドキュメントのエレメントに変換する。
	 * 
	 * @param xmlDocument	エレメント生成に使用する XML DOM ドキュメント
	 * @return	生成されたエレメントを返す。
	 * @throws DOMException	タグの生成に失敗した場合
	 * 
	 * @since 0.982
	 */
	protected Element encodeToElement(XmlDocument xmlDocument)	throws DOMException
	{
		// create node
		Element node = xmlDocument.createElement(XML_ELEMENT_NAME);
		
		// create items
		for (ExBasePattern pattern : this) {
			node.appendChild(pattern.encodeToDomElement(xmlDocument, true));
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
	 * 
	 * @since 0.982
	 */
	protected void decodeFromElement(XmlDocument xmlDocument, Element xmlElement)
		throws XmlDomParseException
	{
		// check Element name
		String strInput = xmlElement.getNodeName();
		if (!strInput.equals(XML_ELEMENT_NAME)) {
			// Illegal element name
			throw new XmlDomParseException(String.format("Root element must be <%s> : <%s>", XML_ELEMENT_NAME, String.valueOf(strInput)));
		}
		
		// get child nodes
		Node child = xmlElement.getFirstChild();
		for (; child != null; child = child.getNextSibling()) {
			if (Node.ELEMENT_NODE == child.getNodeType()) {
				// decode item element
				ExBasePattern pat = ExBasePattern.decodeFromDomElement(xmlDocument, (Element)child, true);
				fastAdd(pat);
			}
		}
	}
}
