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
 *  Copyright 2007-2010  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)DtBaseSet.java	0.20	2010/02/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBaseSet.java	0.10	2008/08/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
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
import dtalge.io.IDataOutput;
import dtalge.io.internal.CsvReader;
import dtalge.io.internal.CsvWriter;
import dtalge.io.internal.XmlDocument;
import dtalge.util.Validations;
import dtalge.util.internal.CacheManager;
import dtalge.util.internal.XmlErrors;

/**
 * データ代数の基底の集合を保持するクラス。
 * 
 * <p>データ代数基底 <code>DtBase</code> インスタンスの集合であり、
 * 同じ値が重複して含まれないことを保証する。データ代数基底の同じ値とは、
 * {@link DtBase#equals(Object)} メソッドが <tt>true</tt> を返す場合を指す。
 * <br>
 * このクラスは、挿入型 {@link java.util.LinkedHashSet} の実装となる。
 * したがって、追加挿入メソッド、ファイル入出力において、
 * クラス内での基底の順序は基本的に維持される。詳細は、各メソッドの説明を参照のこと。
 * <p>
 * このクラスでは、<tt>null</tt> を許容しない。
 * <br>
 * また、<b>この実装は同期化されない</b>。
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
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>DtBaseSet</code> は、CSV ファイル、XML ファイル形式の入出力インタフェースを
 * 提供する。CSV 形式、XML 形式共に、次の制約がある。
 * <ul>
 * <li>基底キーに、次の文字は使用できない。<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt; &gt; - , ^ &quot; % &amp; ? | @ ' " (空白)
 * <li>基底のデータ型キーに指定可能な文字列は、次の通り。大文字、小文字は区別しない。<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&quot;boolean&quot;&nbsp;&nbsp;(真偽値)<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&quot;decimal&quot;&nbsp;&nbsp;(実数値)<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&quot;string&quot;&nbsp;&nbsp;(文字列)
 * <li>CSV 形式ファイルのテキスト・ファイル・エンコーディングは、入出力時に指定されない場合は、
 * プラットフォーム標準の文字コードとなる。
 * <li>XML 形式ファイルのテキスト・ファイル・エンコーディングは、&quot;UTF-8&quot; となる。
 * </ul>
 * また、CSV 形式、XML 形式のファイル入力時、ファイル内に同一の基底が複数存在する場合、
 * 重複する基底は除去される。
 * <p>
 * <b>&lt;CSV 形式&gt;</b>
 * <p>
 * CSV 形式のファイルは、カンマ区切りのテキストファイルであり、次のようなフォーマットとなる。
 * <ul>
 * <li>CSV ファイルの第 1 行目は、次のキーワードであること。大文字、小文字も区別される。
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;#DtbaseSet
 * <li>空白文字も有効な文字と認識する。
 * <li>1 行を 1 要素(基底)とし、行の先頭から次のような順序でカラムを識別する。
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;<i>名前キー</i>, <i>データ型キー</i>, <i>属性キー</i>, <i>主体キー</i>
 * <li>空白行(行の先頭で改行されている行)は、無視される。
 * <li>基底の属性キー、主体キーが空文字(長さが 0 の文字列)の場合、その基底キーは省略記号('#') に置き換えられる。
 * </ul>
 * CSV 形式ファイルの読み込みにおいて、次の場合に {@link dtalge.exception.CsvFormatException} 例外がスローされる。
 * <ul>
 * <li>CSV ファイルの第 1 行目が #DtbaseSet ではない場合
 * <li>基底の名前キーが省略された(空文字だった)場合
 * <li>基底のデータ型キーが省略された(空文字だった)場合
 * <li>基底の各基底キーに使用できない文字が含まれている場合
 * <li>基底のデータ型キーに、指定可能な文字列が記述されていない場合
 * </ul>
 * CSV 形式ファイルの入出力は、次のメソッドにより行う。
 * <ul>
 * <li>{@link #fromCSV(File)}
 * <li>{@link #fromCSV(File, String)}
 * <li>{@link #toCSV(File)}
 * <li>{@link #toCSV(File, String)}
 * </ul>
 * <p>
 * <b>&lt;XML 形式&gt;</b>
 * <p>
 * XML 形式ファイルのノード構成は、次の通りである。
 * <pre><code>
 * &lt;DtbaseSet&gt;
 *   &lt;Dtbase name=&quot;<i>名前キー</i>&quot; type=&quot;<i>データ型キー</i>&quot; attr=&quot;<i>属性キー</i>&quot; subject=&quot;<i>主体キー</i>&quot; /&gt;
 *         ・
 *         ・
 *         ・
 * &lt;/DtbaseSet&gt;
 * </code></pre>
 * <ul>
 * <li>ルートノードは、単一の &lt;DtbaseSet&gt; ノードとなる。
 * <li>&lt;DtbaseSet&gt; の子ノードは &lt;Dtbase&gt; のみであり、0 個以上指定できる。
 * <li>&lt;Dtbase&gt; ノードが、基底を示すノードとなる。
 * <li>&lt;Dtbase&gt; ノードの属性に基底のキーを指定する。名前キーとデータ型キー以外は省略可能。
 * <li>基底の属性キー、主体キーが省略された(もしくは空文字の)場合、その基底キーは省略記号('#')に置き換えられる。
 * </ul>
 * XML 形式ファイルの読み込みにおいて、次の場合に {@link org.xml.sax.SAXParseException} 例外がスローされる。
 * <ul>
 * <li>XML のノードツリー構成が正しくない場合
 * <li>基底の名前キーが省略された(空文字だった)場合
 * <li>基底のデータ型キーが省略された(空文字だった)場合
 * <li>基底の各基底キーに使用できない文字が含まれている場合
 * <li>基底のデータ型キーに、指定可能な文字列が記述されていない場合
 * </ul>
 * XML 形式ファイルの入出力は、次のメソッドにより行う。
 * <ul>
 * <li>{@link #fromXML(File)}
 * <li>{@link #toXML(File)}
 * </ul>
 * 
 * @version 0.20	2010/02/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @see java.util.LinkedHashSet
 */
public class DtBaseSet extends AbDtBaseSet<DtBase> implements IDataOutput
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * デフォルトの初期容量で、空の集合を生成する。
	 * 
	 * @see java.util.LinkedHashSet#LinkedHashSet()
	 */
	public DtBaseSet() {
		super();
	}

    /**
     * 指定されたコレクションの要素を格納する新規基底集合を作成する。
     * <br>
     * デフォルトの負荷係数、および指定されたコレクションの要素を
     * 格納するのに十分な初期要領により作成される。
     * <br>
     * コレクションに含まれる <tt>null</tt> 要素は無視される。
     * 
     * @param c 要素がセットに配置される <code>DtBase</code> のコレクション
     * 
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
     * 
     * @see java.util.LinkedHashSet#LinkedHashSet(Collection)
     */
	public DtBaseSet(Collection<? extends DtBase> c) {
		super(Math.max((int) (c.size()/.75f) + 1, 16));
		addAll(c);
	}

    /**
     * 指定された初期容量および指定された負荷係数で、新しい空の基底集合を生成する。
     * 
     * @param initialCapacity 基底集合(セット)の初期容量
     * @param loadFactor 基底集合(セット)の負荷係数
     * 
     * @throws IllegalArgumentException	初期容量が 0 より小さいか、負荷係数が正の値ではない場合にスローされる
     * 
     * @see java.util.LinkedHashSet#LinkedHashSet(int, float)
     */
	public DtBaseSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

    /**
     * 指定された初期容量およびデフォルトの負荷係数で、新しい空の基底集合を生成する。
     * 
     * @param initialCapacity 基底集合(セット)の初期容量
     * 
     * @throws IllegalArgumentException	初期容量が 0 より小さい場合にスローされる
     * 
     * @see java.util.LinkedHashSet#LinkedHashSet(int)
     */
	public DtBaseSet(int initialCapacity) {
		super(initialCapacity);
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 基底集合を連結した、新しい基底集合のインスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #union(DtBaseSet)} と
	 * 同じ結果を返す。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	この集合と連結する基底集合
	 * @return		この集合と指定された集合を連結した新しい基底集合
	 * 
	 * @throws NullPointerException 指定された集合が <tt>null</tt> の場合
	 */
	public DtBaseSet addition(DtBaseSet set) {
		return union(set);
	}

	/**
	 * この集合から指定された基底集合の要素を除いた、新しい基底集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #difference(DtBaseSet)} と
	 * 同じ結果を返す。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	この集合から取り除く要素を持つ基底集合
	 * @return		この集合から指定された集合の要素を取り除いた、新しい基底集合
	 * 
	 * @throws NullPointerException 指定された集合が <tt>null</tt> の場合
	 */
	public DtBaseSet subtraction(DtBaseSet set) {
		return difference(set);
	}

	/**
	 * この集合から指定された基底集合の要素以外を除いた、新しい基底集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #intersection(DtBaseSet)} と
	 * 同じ結果を返す。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	この集合において維持する要素を持つ基底集合
	 * @return	この集合から指定された集合の要素以外を除いた、新しい基底集合
	 * 
	 * @throws NullPointerException	指定された集合が <tt>null</tt> の場合
	 */
	public DtBaseSet retention(DtBaseSet set) {
		return intersection(set);
	}

	/**
	 * 指定された基底集合との和を返す。
	 * <p>
	 * 基底集合の和演算(this ∪ set)は、この集合と指定された集合の全ての要素を
	 * 合成した集合を生成する。演算結果として生成された集合には、重複する要素は
	 * 存在しない。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set 和を取る基底集合の一方
	 * @return 2 つの基底集合の和となるインスタンス
	 * 
	 * @throws NullPointerException 指定された集合が <tt>null</tt> の場合
	 */
	public DtBaseSet union(DtBaseSet set) {
		DtBaseSet newSet = (DtBaseSet)clone();
		newSet.addAll(Validations.validNotNull(set));
		return newSet;
	}

	/**
	 * 指定の基底集合との積(共通部分)を返す。
	 * <p>
	 * 基底集合の積演算(this ∩ set)は、この集合と指定された集合の両方に含まれる
	 * 要素のみを格納する集合を生成する。演算結果として生成された集合には、
	 * 重複する要素は存在しない。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set 積を取る基底集合の一方
	 * @return 2 つの基底集合の積となるインスタンス
	 * 
	 * @throws NullPointerException 指定された集合が <tt>null</tt> の場合
	 */
	public DtBaseSet intersection(DtBaseSet set) {
		DtBaseSet newSet = (DtBaseSet)clone();
		newSet.retainAll(Validations.validNotNull(set));
		return newSet;
	}

	/**
	 * 指定された基底集合との差を返す。
	 * <p>
	 * 基底集合の差演算(this - set)は、この集合から指定された集合の要素を除いた
	 * 集合を生成する。演算結果として生成された集合には、重複する要素は存在しない。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set 差し引く基底の集合
	 * @return 指定された基底集合を差し引いた結果となるインスタンス
	 * 
	 * @throws NullPointerException 指定された集合が <tt>null</tt> の場合
	 */
	public DtBaseSet difference(DtBaseSet set) {
		DtBaseSet newSet = (DtBaseSet)clone();
		newSet.removeAll(Validations.validNotNull(set));
		return newSet;
	}

	/**
	 * この基底集合に含まれる基底のうち、指定されたパターンに一致する
	 * 基底のみを格納する基底集合を返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは空の基底集合を返す。
	 * 
	 * @param pattern	基底パターン
	 * @return	パターンに一致した基底のみの集合
	 * 
	 * @throws NullPointerException 指定された基底パターンが <tt>null</tt> の場合
	 */
	public DtBaseSet getMatchedBases(DtBasePattern pattern) {
		Validations.validNotNull(pattern);
		
		// 要素が空なら、空集合
		if (this.isEmpty()) {
			return new DtBaseSet(0);
		}
		
		// マッチング
		DtBaseSet matchedBases = new DtBaseSet();
		for (DtBase base : this) {
			if (pattern.matches(base)) {
				matchedBases.add(base);
			}
		}
		
		return matchedBases;
	}

	/**
	 * この基底集合に含まれる基底のうち、指定されたパターンに一致する
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
		for (DtBase base : this) {
			if (patterns.matches(base)) {
				matchedBases.add(base);
			}
		}
		
		return matchedBases;
	}

	//------------------------------------------------------------
	// Override
	//------------------------------------------------------------

    /**
     * 指定された要素が基底集合の要素として存在しない場合に、その要素を基底集合に追加する。
     * <p>
     * このメソッドでは、<tt>null</tt> は許容されない。
     * 
     * @param base 集合に追加する要素
     * 
     * @return 指定された要素を保持していなかった場合は true
     * 
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
     */
	public boolean add(DtBase base) {
		Validations.validNotNull(base, "'base' argument cannot be null.");
		DtBase cachedBase = CacheManager.cacheDtBase(base);
		boolean ret = super.add(cachedBase);
		return ret;
	}

    /**
     * 指定されたコレクションのすべての要素をこの基底集合に追加する。
     * <br>
     * 指定されたコレクションに含まれる <tt>null</tt> 要素は無視される。
     * 
     * @param c この基底集合に追加するコレクション
     * 
     * @return この呼び出しの結果、この基底集合が変更された場合は true
     * 
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
     * 
     * @see java.util.LinkedHashSet#addAll(Collection)
     */
	public boolean addAll(Collection<? extends DtBase> c) {
		boolean modified = false;
		Iterator<? extends DtBase> it = c.iterator();
		while (it.hasNext()) {
			DtBase base = it.next();
			if (base != null) {
				if (super.add(CacheManager.cacheDtBase(base))) {
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
	public Object clone() {
		DtBaseSet newSet = (DtBaseSet) super.clone();
		return newSet;
	}

	//------------------------------------------------------------
	// I/O
	//------------------------------------------------------------
	
	/**
	 * 基底集合の内容を、指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている要素の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
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
	 * 基底集合の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
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
	 * CSV フォーマットのファイルを読み込み、新しい基底集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DtBaseSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 */
	static public DtBaseSet fromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		CsvReader reader = new CsvReader(csvFile);
		DtBaseSet newSet = new DtBaseSet();
		try {
			newSet.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newSet;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しい基底集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DtBaseSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	static public DtBaseSet fromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		DtBaseSet newSet = new DtBaseSet();
		try {
			newSet.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newSet;
	}
	
	/**
	 * 基底集合の内容を、指定のファイルに XML フォーマットで出力する。
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
	 * XML フォーマットのファイルを読み込み、新しい基底集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param xmlFile 読み込む XML ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DtBaseSet</code> インスタンスを返す。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 */
	static public DtBaseSet fromXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException
	{
		//XmlDocument xmlDocument = XmlDocument.fromFile(xmlFile);
		//return DtBaseSet.fromXML(xmlDocument);
		
		// SAX パーサ のファクトリー生成
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
	// Internal methods
	//------------------------------------------------------------

	/**
	 * CSVファイルフォーマットの第1行目のキーワード
	 */
	static protected final String CSV_KEYWORD = "#DtbaseSet";

	/**
	 * XMLファイルフォーマットでの基底集合のルートノード名
	 */
	static protected final String XML_ELEMENT_NAME = "DtbaseSet";
	
	/**
	 * 基底集合の内容から、XML ドキュメントを生成する。
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

	/**
	 * CSVフォーマットで DtBaseSet の内容を出力
	 * 
	 * @param writer CSVファイル出力オブジェクト
	 * @throws IOException 入出力エラーが発生した場合
	 */
	protected void writeToCSV(CsvWriter writer)
		throws IOException
	{
		// キーワード出力
		writer.writeLine(CSV_KEYWORD);

		// データ出力
		for (DtBase base : this) {
			base.writeFieldToCSV(writer);
			writer.newLine();
		}
		writer.flush();
	}

	/**
	 * CSVフォーマットで DtBaseSet の内容を読み込む
	 * 
	 * @param reader CSVファイル入力オブジェクト
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 */
	protected void readFromCSV(CsvReader reader)
		throws IOException, CsvFormatException
	{
		CsvReader.CsvRecord record;
		
		// 先頭１行目のキーワードをチェック
		record = reader.readRecord();
		if (record == null || !record.hasFields()) {
			// undefined DtBaseSet CSV ID
			throw new CsvFormatException("DtBaseSet file ID not found.");
		} else {
			CsvReader.CsvField field = record.getField(0);
			if (field == null || !CSV_KEYWORD.equals(field.getValue())) {
				throw new CsvFormatException("Illegal DtBaseSet file ID.", record.getLineNo(), 1);
			}
			// 以降のフィールドは無視
		}
		
		// データ読み込み
		while ((record = reader.readRecord()) != null) {
			// 空行、もしくは値のないレコードはスキップ
			if (!record.hasFields() || !record.hasValues()) {
				continue;
			}
			
			// フィールドの値読み出し
			CsvReader.CsvFieldReader freader = new CsvReader.CsvFieldReader(record);
			DtBase newBase = DtBase.readFieldFromCSV(freader);
			fastAdd(newBase);
		}
	}

	/**
	 * このインスタンスの内容で、XMLドキュメントとなるエレメントを生成する。
	 * 
	 * @param xmlDocument	生成に使用するドキュメントオブジェクト
	 * @return 生成したエレメントのインスタンス
	 * @throws DOMException XMLのドキュメントエラー
	 */
	protected Element encodeToElement(XmlDocument xmlDocument)	throws DOMException
	{
		// create node
		Element node = xmlDocument.createElement(XML_ELEMENT_NAME);
		
		// create entries
		for (DtBase base : this) {
			Element child = base.encodeToElement(xmlDocument);
			node.appendChild(child);
		}
		
		// completed
		return node;
	}

	/**
	 * <code>DtBaseSet</code> の SAX パーサーハンドラ。
	 * 
	 * @version 0.20	2010/02/25
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 **/
	static class SaxParserHandler extends DefaultHandler
	{
		private final Stack<String> stackElement = new Stack<String>();
		
		private Locator saxLocator = null;
		
		private final DtBaseSet bset = new DtBaseSet();
		
		public DtBaseSet getTargetInstance() {
			return bset;
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
			if (XML_ELEMENT_NAME.equals(qName)) {
				XmlErrors.validChildOfParent(saxLocator, qName, XmlErrors.peekStringStack(stackElement));
			}
			else if (DtBase.XML_ELEMENT_NAME.equals(qName)) {
				XmlErrors.validChildOfParent(saxLocator, qName, XmlErrors.peekStringStack(stackElement), XML_ELEMENT_NAME);
				// create DtBase by attributes
				DtBase newBase = DtBase.decodeFromXmlAttributes(saxLocator, qName, attributes);
				bset.add(newBase);
			}
			else {
				XmlErrors.errorUnknownElement(saxLocator, qName);
			}
			
			// stack element
			stackElement.add(qName);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			// check element pair
			XmlErrors.validEndElement(saxLocator, XmlErrors.peekStringStack(stackElement), qName);
			
			// remove stack
			if (!stackElement.empty()) {
				stackElement.pop();
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			super.characters(ch, start, length);
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
