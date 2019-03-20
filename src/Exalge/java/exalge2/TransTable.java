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
 * @(#)TransTable.java	0.984	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransTable.java	0.970	2009/03/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransTable.java	0.93	2007/09/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransTable.java	0.91	2007/08/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransTable.java	0.90	2007/07/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
 * 交換代数の振替変換(アグリゲーション)を実行する際の
 * 変換テーブルを保持するクラス。
 * <p>
 * このクラスには、変換元基底と変換先基底のマップにより変換テーブルが構成される。
 * <br>
 * このクラスの Map の実装は、挿入型の {@link java.util.LinkedHashMap <code>LinkedHashMap</code>} である。
 * したがって、挿入メソッド、ファイル入出力において、
 * クラス内での要素の順序は基本的に維持される。
 * <br>
 * 変換元基底ならびに変換先基底には、基底パターン(<code>{@link ExBasePattern}</code>)を指定する。
 * <p>
 * 振替変換では、変換元基底パターンにマッチする基底は、変換先基底へ振替られる。
 * <br>
 * このとき、振替先基底の基底キーにワイルドカードが指定されている場合、
 * 振替元基底の対応する基底キーが、そのまま振替先基底のキーとなる。
 * <br>
 * 振替先基底パターンには、基底キーにワイルドカードとワイルドカード以外の文字が
 * 含まれるパターンは指定できない。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>TransTable</code> は、CSV ファイル、XML ファイル(XML ドキュメント)形式の
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
 * <i>属性</i>，<i>名前キー</i>，<i>単位キー</i>，<i>時間キー</i>，<i>サブジェクトキー</i>
 * <li>文字コードは、実行するプラットフォームの処理系に依存する。
 * <li>空行は無視される。
 * <li>各カラムの前後空白は、無視される。
 * <li><code>'#'</code> で始まる行は、コメント行とみなす。
 * <li>属性に指定可能な文字列は、次のもののみ(大文字、小文字は区別しない)。
 * from to (空白)
 * <li>属性は、変換元基底、もしくは変換先基底であることを示すものである。
 * &quot;from&quot; が変換元基底、&quot;to&quot; が変換先基底を示す。
 * <li>属性が省略(空文字)の場合、from(変換元基底)とみなす。
 * <li>基底キー(名前キーも含む)のカラムが省略(空文字)の場合、ワイルドカード記号('*')が代入される。
 * <li>連続した &quot;from&quot; 属性の基底が、次の &quot;to&quot; 属性の基底に関連付けられる。
 * </ul>
 * また、CSV ファイルの読み込みにおいて、次の場合に {@link exalge2.io.csv.CsvFormatException} 例外がスローされる。
 * <ul>
 * <li>属性文字列が指定可能な文字列ではない場合
 * <li>変換元基底に対応する変換先基底が存在しない(&quot;from&quot; 属性の次に &quot;to&quot; 属性が出現しない)
 * <li>変換先基底に対応する変換元基底が存在しない(&quot;to&quot; 属性の直前に &quot;from&quot; 属性が出現しない)
 * <li>変換先基底の文字列に、ワイルドカード記号とワイルドカード記号以外の組み合わせが存在する
 * <li>変換元基底が重複している
 * <li>各基底キーに使用できない文字が含まれている場合
 * </ul>
 * なお、CSV ファイルの入出力は、{@link #toCSV(File)}、{@link #fromCSV(File)} により行う。
 * また、指定した文字セットでの入出力は、{@link #toCSV(File, String)}、{@link #fromCSV(File, String)} 
 * により行う。
 * <p>
 * XMLドキュメントは、次の構成となる。XMLファイルも同様のフォーマットに従う。
 * <pre><code>
 * &lt;TransTable&gt;
 *   &lt;TransTableElem&gt;
 *     &lt;TransFrom&gt;
 *       &lt;Exbase name=&quot;<i>名前キー</i>&quot; unit=&quot;<i>単位キー</i>&quot; time=&quot;<i>時間キー</i>&quot; subject=&quot;<i>サブジェクトキー</i>&quot; /&gt;
 *            ・
 *             ・
 *             ・
 *     &lt;/TransFrom&gt;
 *     &lt;TransTo&gt;
 *       &lt;Exbase name=&quot;<i>名前キー</i>&quot; unit=&quot;<i>単位キー</i>&quot; time=&quot;<i>時間キー</i>&quot; subject=&quot;<i>サブジェクトキー</i>&quot; /&gt;
 *     &lt;/TransTo&gt;
 *   &lt;/TransTableElem&gt;
 *        ・
 *         ・
 *         ・
 * &lt;/TransTable&gt;
 * </code></pre>
 * <ul>
 * <li>ルートノードは単一の &lt;TransTable&gt; となる。
 * <li>&lt;TransTableElem&gt; が、変換元基底と変換先基底のペアを示すノードとなる。
 * <li>&lt;TransFrom&gt; が、変換元基底のリストを示すノードとなる。
 * <li>&lt;TransTo&gt; が、変換先基底を示すノードとなる。
 * <li>&lt;Exbase&gt; が、基底パターンを示すノードとなる。属性が基底のキーとなる。
 * <li>&lt;Exbase&gt; ノードは、&lt;TrasnFrom&gt;、&lt;TrasnTo&gt; ノードの子ノードであり、
 * &lt;TrasnFrom&gt; ノードには１つ以上、&lt;TrasnTo&gt; ノードには必ず１つだけ存在する。
 * <li>&lt;TrasnFrom&gt; ノードは、&lt;TransTableElem&gt; の子ノードであり、必ず１つだけ存在する。
 * <li>&lt;TrasnTo&gt; ノードは、&lt;TrasnTableElem&gt; の子ノードであり、必ず１つだけ存在する。
 * <li>&lt;TrasnTableElem&gt; ノードは、&lt;TrasnTable&gt; の子ノードであり、複数存在する。
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
 * <li>各基底キーに使用できない文字が含まれている場合
 * </ul>
 * なお、XML ドキュメントの入出力は、{@link #toXML()}、{@link #fromXML(XmlDocument)} により行う。
 * また、XML ファイルの入出力は、{@link #toXML(File)}、{@link #fromXML(File)} により行う。
 * 
 * 
 * @version 0.984	2014/05/29
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.90
 */
public class TransTable implements exalge2.io.IDataOutput, Cloneable
{
	static private final String DIR_TO = "to";
	static private final String DIR_FROM = "from";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 変換対応テーブル
	 */
	private LinkedHashMap<ExBasePattern,ExBasePattern> transMap;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 空の変換テーブルを持つ <code>TransTable</code> の新しいインスタンスを生成する。
	 */
	public TransTable() {
		this.transMap = new LinkedHashMap<ExBasePattern,ExBasePattern>();
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	/**
	 * 振替変換テーブルの要素が空であることを示す。
	 * 
	 * @return 要素が空なら true を返す。
	 */
	public boolean isEmpty() {
		return this.transMap.isEmpty();
	}

	/**
	 * 振替変換テーブルの要素数を返す。
	 * 
	 * @return 要素数
	 */
	public int size() {
		return this.transMap.size();
	}

	/**
	 * 指定の基底パターンが振替元基底に含まれているかを検証する。
	 * 
	 * @param pattern 検証対象の基底パターン
	 * @return 振替元基底に含まれていれば true を返す。
	 */
	public boolean containsTransFrom(ExBasePattern pattern) {
		return this.transMap.containsKey(pattern);
	}

	/**
	 * 指定の基底パターンが振替先基底に含まれているかを検証する。
	 * 
	 * @param pattern 検証対象の基底パターン
	 * @return 振替先基底に含まれていれば true を返す。
	 */
	public boolean containsTransTo(ExBasePattern pattern) {
		return this.transMap.containsValue(pattern);
	}

	/**
	 * 振替変換テーブルに含まれる振替元基底パターンの集合を取得する。
	 * 
	 * @return 振替元基底パターンの集合を返す。
	 */
	public Set<ExBasePattern> transFromPatterns() {
		return this.transMap.keySet();
	}

	/**
	 * 振替変換テーブルに含まれる振替先基底パターンのコレクションビューを返す。
	 * 
	 * @return 振替先基底パターンのコレクションビューを返す。
	 */
	public Collection<ExBasePattern> transToPatterns() {
		return this.transMap.values();
	}

	/**
	 * 指定された振替元基底パターンに対応する振替先基底パターンを取得する。
	 * <br>
	 * 対応する振替先基底パターンが存在しない場合は <tt>null</tt> を返す。
	 * 
	 * @param transFrom 検索する振替元基底パターン
	 * @return 振替先基底パターンを返す。存在しない場合は <tt>null</tt> を返す。
	 */
	public ExBasePattern getTransTo(ExBasePattern transFrom) {
		return this.transMap.get(transFrom);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 振替変換テーブルの全要素を削除する。
	 * 
	 */
	public void clear() {
		this.transMap.clear();
	}

	/**
	 * 振替変換テーブルに、振替元基底パターンと振替先基底パターンのペアを登録する。
	 * 振替元基底パターンがすでに登録されている場合、新しい振替先基底パターンに上書きされる。
	 * 
	 * @param transFrom 振替元基底パターン
	 * @param transTo 振替先基底パターン
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public void put(ExBasePattern transFrom, ExBasePattern transTo) {
		// Check
		if (transFrom == null)
			throw new NullPointerException("No transFrom");
		if (transTo == null)
			throw new NullPointerException("No transTo");
		
		// put
		this.transMap.put(transFrom, transTo);
	}

	/**
	 * 振替変換テーブルから、指定の振替元基底パターンと一致するペアを削除する。
	 * 
	 * @param transFrom 削除する振替元基底パターン
	 */
	public void remove(ExBasePattern transFrom) {
		this.transMap.remove(transFrom);
	}

	/**
	 * 指定の基底に一致する、振替元基底パターンを返す。
	 * <br>
	 * 指定の基底に一致する、異なる振替元基底パターンが複数存在する場合、
	 * 最初に一致した振替元基底パターンを返す。
	 * <br>
	 * 一致するパターンが存在しない場合は <tt>null</tt> を返す。
	 * 
	 * @param targetBase パターンとの一致を検証する基底
	 * 
	 * @return 一致する振替元基底パターンを返す。
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
	 * この振替変換テーブルの定義に基づき、指定された基底を変換する。
	 * 指定された基底が、このテーブルの振替元基底のどれにも一致しない
	 * 場合、指定された基底を変換せずに返す。<br>
	 * 指定の基底に一致する、異なる振替元基底パターンが複数存在する場合、
	 * 最初に一致した基底パターンを振替元基底パターンとする振替先基底パターンによって
	 * 振替変換が行われる。
	 * 
	 * @param targetBase	変換対象の基底
	 * @return	変換後の基底を返す。振替元基底と一致しない場合は、指定された基底を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public ExBase transform(ExBase targetBase) {
		if (targetBase == null)
			throw new NullPointerException();
		
		// maches
		ExBasePattern transFrom = machesTransFrom(targetBase);
		if (transFrom == null) {
			// no maches
			return targetBase;
		}
		
		// translate base
		ExBasePattern transTo = transMap.get(transFrom);
		return transTo.translate(targetBase);
	}

	/**
	 * この振替変換テーブルの定義に基づき、指定された基底集合に含まれる
	 * 全ての基底を変換する。指定された基底集合に含まれる基底のうち、
	 * このテーブルの振替元基底のどれにも一致しないものは、変換せずに
	 * 結果の基底集合に含まれる。<br>
	 * 指定の基底に一致する、異なる振替元基底パターンが複数存在する場合、
	 * 最初に一致した基底パターンを振替元基底パターンとする振替先基底パターンによって
	 * 振替変換が行われる。
	 * 
	 * @param targetBases	変換対象の基底集合
	 * @return	変換後の基底を格納する新しい基底集合を返す。
	 * 			振替元基底と一致しない基底は、変換せずにそのまま結果に含まれる。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @since 0.970
	 */
	public ExBaseSet transform(ExBaseSet targetBases) {
		// check
		if (targetBases.isEmpty()) {
			return new ExBaseSet();
		}
		
		// translate
		ExBaseSet resultSet = new ExBaseSet(targetBases.size());
		for (ExBase base : targetBases) {
			resultSet.fastAdd(transform(base));
		}
		
		return resultSet;
	}

	/**
	 * 指定の基底から、振替変換後の基底を取得する。
	 * <br>
	 * 指定の基底に一致する、異なる振替元基底パターンが複数存在する場合、
	 * 最初に一致した振替元基底パターンの振替先基底パターンによって
	 * 振替変換が行われる。
	 * 
	 * @param targetBase 変換する基底
	 * @return 振替変換後の基底を返す。振替対象ではない場合は null を返す。
	 */
	public ExBase transfer(ExBase targetBase) {
		// maches
		ExBasePattern transFrom = machesTransFrom(targetBase);
		if (transFrom == null) {
			// No maches
			return null;
		}
		
		// get transTo
		ExBasePattern transTo = getTransTo(transFrom);
		
		// translate
		return transTo.translate(targetBase);
	}

	//------------------------------------------------------------
	// Implement Object interfaces
	//------------------------------------------------------------
	
	/**
	 * <code>TransTable</code> インスタンスの新しいシャローコピーを返す。
	 * このオブジェクトに格納されている <code>ExBasePattern</code> インスタンスについては、
	 * それ自体は複製されない。
	 * 
	 * @return このオブジェクトのコピー
	 * @since 0.970
	 */
	@Override
	@SuppressWarnings("unchecked")
	public TransTable clone() {
		try {
			TransTable dup = (TransTable)super.clone();
			dup.transMap = (LinkedHashMap<ExBasePattern,ExBasePattern>)this.transMap.clone();
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
		return transMap.hashCode();
	}

	/**
	 * 指定されたオブジェクトとこのオブジェクトが等しいかを検証する。
	 * <code>TransTable</code> では、2 つの検証するオブジェクトにおいて、
	 * 全ての振替元基底パターンが同値であり、同一振替元基底パターンに対応する
	 * 振替先基底パターンが等しい場合に同値とみなす。
	 * 
	 * @param obj	このオブジェクトと比較するオブジェクト
	 * @return	指定されたオブジェクトがこのオブジェクトと等しい場合に <tt>true</tt>
	 * 
	 * @since 0.970
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			// same instance
			return true;
		}
		else if (obj instanceof TransTable) {
			return ((TransTable)obj).transMap.equals(this.transMap);
		}
		
		// not equals
		return false;
	}

	/**
	 * このオブジェクトの文字列表現を返す。
	 * <br>文字列表現は、次のような形式となる。
	 * <blockquote>
	 * [ (改行)<br>
	 * <i>振替元基底パターン</i> -&gt; <i>振替先基底パターン</i> (改行)<br>
	 * <i>振替元基底パターン</i> -&gt; <i>振替先基底パターン</i> (改行)<br>
	 * <i>振替元基底パターン</i> -&gt; <i>振替先基底パターン</i> (改行)<br>
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
		sb.append('[');
		if (!transMap.isEmpty()) {
			sb.append("\n");
			for (Map.Entry<ExBasePattern, ExBasePattern> entry : transMap.entrySet()) {
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
	 * 振替変換テーブルの内容を、指定のファイルに CSV フォーマットで出力する。
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
	 * 振替変換テーブルの内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
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
	 * 振替変換テーブルの内容を、CSVフォーマットの文字列に変換する。
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
	 * CSV フォーマットのファイルを読み込み、新しい振替変換テーブルを生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>TransTable</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.91
	 */
	static public TransTable fromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		CsvReader reader = new CsvReader(csvFile);
		TransTable newTable = new TransTable();
		
		try {
			newTable.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newTable;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しい振替変換テーブルを生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>TransTable</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * 
	 * @since 0.93
	 */
	static public TransTable fromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		TransTable newTable = new TransTable();
		
		try {
			newTable.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newTable;
	}
	
	/**
	 * CSV フォーマットの文字列を読み込み、新しい振替変換テーブルを生成する。
	 * <p>
	 * 基本的に、文字列に記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvString 読み込む CSV フォーマットの文字列
	 * @return 文字列の内容で生成された、新しい <code>TransTable</code> インスタンスを返す。
	 * 
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * 
	 * @since 0.984
	 */
	static public TransTable fromCsvString(String csvString) throws CsvFormatException
	{
		CsvReader reader = new CsvReader(new StringReader(csvString));
		TransTable newTable = new TransTable();
		
		try {
			newTable.readFromCSV(reader);
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		finally {
			reader.close();
		}
		
		return newTable;
	}
	
	/**
	 * 振替変換テーブルの内容を、指定のファイルに XML フォーマットで出力する。
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
	 * 振替変換テーブルの内容から、XML ドキュメントを生成する。
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
	 * 振替変換テーブルの内容を、XML フォーマットの文字列に変換する。
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
	 * XML フォーマットのファイルを読み込み、新しい振替変換テーブルを生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlFile 読み込む XML ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>TransTable</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.91
	 */
	static public TransTable fromXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException,
				XmlDomParseException
	{
		XmlDocument xmlDocument = XmlDocument.fromFile(xmlFile);
		
		return TransTable.fromXML(xmlDocument);
	}
	
	/**
	 * 指定された XML ドキュメントを解析し、新しい振替変換テーブルを生成する。
	 * <p>
	 * 基本的に、XML ノードの順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlDocument 解析対象の XML ドキュメント
	 * 
	 * @return 解析により生成された、新しい <code>TransTable</code> インスタンスを返す。
	 * 
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.91
	 */
	static public TransTable fromXML(XmlDocument xmlDocument)
		throws XmlDomParseException
	{
		Element root = xmlDocument.getDocumentElement();
		
		if (root == null) {
			throw new XmlDomParseException(XmlDomParseException.ELEMENT_NOT_FOUND,
											xmlDocument, null, null, XML_ROOT_ELEMENT_NAME);
		}
		
		TransTable newTable = new TransTable();
		newTable.decodeFromElement(xmlDocument, root);
		
		return newTable;
	}
	
	/**
	 * XML フォーマットの文字列を読み込み、新しい振替変換テーブルを生成する。
	 * <p>
	 * 基本的に、文字列に記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlString 読み込む XML フォーマットの文字列
	 * 
	 * @return 文字列の内容で生成された、新しい <code>TransTable</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * @throws XmlDomParseException XML ノード解析エラーが発生した場合
	 * 
	 * @since 0.984
	 */
	static public TransTable fromXmlString(String xmlString)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException, XmlDomParseException
	{
		XmlDocument xmlDocument = XmlDocument.fromXmlString(xmlString);
		
		return TransTable.fromXML(xmlDocument);
	}

	//------------------------------------------------------------
	// Internal methods for I/O
	//------------------------------------------------------------
	
	protected void writeToCSV(CsvWriter writer)
		throws IOException
	{
		// グループマップの生成
		LinkedHashMap<ExBasePattern, ArrayList<ExBasePattern>> map = createReverseMap();
		
		// グループ出力
		Iterator<ExBasePattern> it = map.keySet().iterator();
		while (it.hasNext()) {
			ExBasePattern patTo = it.next();
			ArrayList<ExBasePattern> fromList = map.get(patTo);
			// write TransFrom pattern
			Iterator<ExBasePattern> itList = fromList.iterator();
			while (itList.hasNext()) {
				ExBasePattern patFrom = itList.next();
				// dir
				writer.writeColumn(DIR_FROM, false);
				// pattern
				patFrom.writeToCSV(writer);
				writer.flush();
			}
			// write TransTo pattern
			//--- dir
			writer.writeColumn(DIR_TO, false);
			//--- pattern
			patTo.writeToCSV(writer);
			writer.flush();
		}
	}
	
	protected void readFromCSV(CsvReader reader)
		throws IOException, CsvFormatException
	{
		CsvRecordReader recReader;
		ArrayList<ExBasePattern> fromList = new ArrayList<ExBasePattern>();
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
					ExBasePattern patTo = new ExBasePattern(ExBasePattern.WILDCARD, ExBasePattern.WILDCARD);
					patTo.readFromCSVForTransToPattern(recReader);
					//--- check exist from
					if (fromList.isEmpty()) {
						throw new CsvFormatException(CsvFormatException.DIR_FROM_NOT_EXIST,
								recReader.getLineNumber()-1, 1);
					}
					//--- append to TransTable
					Iterator<ExBasePattern> it = fromList.iterator();
					while (it.hasNext()) {
						ExBasePattern patFrom = it.next();
						this.put(patFrom, patTo);
					}
					//--- clear from list
					fromList.clear();
				}
				else {
					// Trans "from" direction
					ExBasePattern patFrom = new ExBasePattern(ExBasePattern.WILDCARD, ExBasePattern.WILDCARD);
					patFrom.readFromCSV(recReader);
					//--- check TransFrom already exist
					if (this.containsTransFrom(patFrom) || fromList.contains(patFrom)) {
						throw new CsvFormatException(CsvFormatException.DIR_FROM_ALREADY_EXIST,
								recReader.getLineNumber()-1, 2);
					}
					//--- append TransFrom
					fromList.add(patFrom);
				}
			}
			finally {
				recReader.close();
			}
		}
		
		// check remain from list
		if (!fromList.isEmpty()) {
			throw new CsvFormatException(CsvFormatException.DIR_TO_NOT_EXIST,
					reader.getLineNumber(), 1);
		}
	}
	
	static protected final String XML_ROOT_ELEMENT_NAME = "TransTable";
	static protected final String XML_ITEM_ELEMENT_NAME = "TransTableElem";
	static protected final String XML_FROM_ELEMENT_NAME = "TransFrom";
	static protected final String XML_TO_ELEMENT_NAME = "TransTo";
	
	protected Element encodeToElement(XmlDocument xmlDocument)	throws DOMException
	{
		// create node
		Element node = xmlDocument.createElement(XML_ROOT_ELEMENT_NAME);
		
		// グループマップの生成
		LinkedHashMap<ExBasePattern, ArrayList<ExBasePattern>> map = createReverseMap();
		
		// グループ出力
		Iterator<ExBasePattern> it = map.keySet().iterator();
		while (it.hasNext()) {
			ExBasePattern patTo = it.next();
			ArrayList<ExBasePattern> fromList = map.get(patTo);
			// create TransFrom patten nodes
			Element transFromElement = xmlDocument.createElement(XML_FROM_ELEMENT_NAME);
			Iterator<ExBasePattern> itList = fromList.iterator();
			while (itList.hasNext()) {
				ExBasePattern patFrom = itList.next();
				Element patElement = patFrom.encodeToElement(xmlDocument);
				transFromElement.appendChild(patElement);
			}
			// create TransTo pattern node
			Element transToElement = xmlDocument.createElement(XML_TO_ELEMENT_NAME);
			{
				Element patElement = patTo.encodeToElement(xmlDocument);
				transToElement.appendChild(patElement);
			}
			// regist group elements
			Element itemElement = xmlDocument.createElement(XML_ITEM_ELEMENT_NAME);
			itemElement.appendChild(transFromElement);
			itemElement.appendChild(transToElement);
			node.appendChild(itemElement);
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
		
		// decode TransTo pattern
		int patCount = 0;
		ExBasePattern patTo = null;
		child = transToElement.getFirstChild();
		for (; child != null; child = child.getNextSibling()) {
			if (Node.ELEMENT_NODE == child.getNodeType()) {
				patTo = new ExBasePattern(ExBasePattern.WILDCARD, ExBasePattern.WILDCARD);
				patTo.decodeFromElementForTransToPattern(xmlDocument, (Element)child);
				patCount++;
			}
		}
		if (patCount > 1) {
			throw new XmlDomParseException(XmlDomParseException.DIR_TO_EXIST_MULTIPLE,
											xmlDocument, transToElement, null, null);
		}
		else if (patCount < 1) {
			throw new XmlDomParseException(XmlDomParseException.DIR_TO_NOT_EXIST,
											xmlDocument, transToElement, null, null);
		}
		
		// decode TransFrom pattern
		patCount = 0;
		child = transFromElement.getFirstChild();
		for (; child != null; child = child.getNextSibling()) {
			if (Node.ELEMENT_NODE == child.getNodeType()) {
				ExBasePattern patFrom = new ExBasePattern(ExBasePattern.WILDCARD, ExBasePattern.WILDCARD);
				patFrom.decodeFromElement(xmlDocument, (Element)child);
				patCount++;
				//--- check already exist TransFrom
				if (this.containsTransFrom(patFrom)) {
					throw new XmlDomParseException(XmlDomParseException.DIR_FROM_ALREADY_EXIST,
							xmlDocument, (Element)child, null, patFrom.key());
				}
				//--- regist to TransTable
				this.put(patFrom, patTo);
			}
		}
		if (patCount < 1) {
			throw new XmlDomParseException(XmlDomParseException.DIR_FROM_NOT_EXIST,
											xmlDocument, transFromElement, null, null);
		}
	}
	
	protected LinkedHashMap<ExBasePattern, ArrayList<ExBasePattern>> createReverseMap() {
		LinkedHashMap<ExBasePattern, ArrayList<ExBasePattern>> map = new LinkedHashMap<ExBasePattern, ArrayList<ExBasePattern>>();
		
		Iterator<ExBasePattern> it = this.transMap.keySet().iterator();
		while (it.hasNext()) {
			ExBasePattern patFrom = it.next();
			ExBasePattern patTo = getTransTo(patFrom);
			ArrayList<ExBasePattern> list;
			if (map.containsKey(patTo)) {
				list = map.get(patTo);
			} else {
				list = new ArrayList<ExBasePattern>();
				map.put(patTo, list);
			}
			list.add(patFrom);
		}
		
		return map;
	}
	
	protected boolean isToDirection(String strDir, int lineNumber)
		throws CsvFormatException
	{
		if (strDir == null) {
			// same "from"
			return false;
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このオブジェクトの変換定義マップを取得する。
	 * @return 変換定義マップ
	 * @since 0.970
	 */
	protected Map<ExBasePattern,ExBasePattern> getTransMap() {
		return transMap;
	}
}
