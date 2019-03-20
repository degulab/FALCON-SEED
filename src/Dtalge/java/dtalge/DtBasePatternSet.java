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
 * @(#)DtBasePatternSet.java	0.20	2010/02/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBasePatternSet.java	0.10	2008/08/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
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
 * データ代数の基底パターンの集合を保持するクラス。
 * <p>
 * データ代数基底パターン <code>{@link DtBasePattern}</code> インスタンスの集合であり、
 * 同じ値が重複して含まれないことを保証する。データ代数基底パターンの同じ値とは、
 * {@link DtBasePattern#equals(Object)} メソッドが <tt>true</tt> を返す場合を指す。
 * <br>
 * このクラスは、挿入型 {@link java.util.LinkedHashSet} の実装となる。
 * したがって、追加挿入メソッド、ファイル入出力において、クラス内での基底パターンの順序は
 * 基本的に維持される。詳細は、各メソッドの説明を参照のこと。
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
 * <code>DtBasePatternSet</code> は、CSV ファイル、XML ファイル形式の入出力インタフェースを
 * 提供する。CSV 形式、XML 形式共に、次の制約がある。
 * <ul>
 * <li>基底キーに、次の文字は使用できない。<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt; &gt; - , ^ &quot; % &amp; ? | @ ' " (空白)
 * <li>CSV 形式ファイルのテキスト・ファイル・エンコーディングは、入出力時に指定されない場合は、
 * プラットフォーム標準の文字コードとなる。
 * <li>XML 形式ファイルのテキスト・ファイル・エンコーディングは、&quot;UTF-8&quot; となる。
 * </ul>
 * また、CSV 形式、XML 形式のファイル入力時、ファイル内に同一の基底パターンが複数存在する場合、
 * 重複する基底パターンは除去される。
 * <p>
 * <b>&lt;CSV 形式&gt;</b>
 * <p>
 * CSV 形式のファイルは、カンマ区切りのテキストファイルであり、次のようなフォーマットとなる。
 * <ul>
 * <li>CSV ファイルの第 1 行目は、次のキーワードであること。大文字、小文字も区別される。
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;#DtbasePatternSet
 * <li>空白文字も有効な文字と認識する。
 * <li>1 行を 1 要素(基底パターン)とし、行の先頭から次のような順序でカラムを識別する。
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;<i>名前キー</i>, <i>データ型キー</i>, <i>属性キー</i>, <i>主体キー</i>
 * <li>空白行(行の先頭で改行されている行)は、無視される。
 * <li>基底パターンの各基底キーが空文字(長さが 0 の文字列)の場合、その基底キーはワイルドカード記号('*') に置き換えられる。
 * </ul>
 * CSV 形式ファイルの読み込みにおいて、次の場合に {@link dtalge.exception.CsvFormatException} 例外がスローされる。
 * <ul>
 * <li>CSV ファイルの第 1 行目が #DtbasePatternSet ではない場合
 * <li>基底の各基底キーに使用できない文字が含まれている場合
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
 * &lt;DtbasePatternSet&gt;
 *   &lt;Dtbase name=&quot;<i>名前キー</i>&quot; type=&quot;<i>データ型キー</i>&quot; attr=&quot;<i>属性キー</i>&quot; subject=&quot;<i>主体キー</i>&quot; /&gt;
 *         ・
 *         ・
 *         ・
 * &lt;/DtbasePatternSet&gt;
 * </code></pre>
 * <ul>
 * <li>ルートノードは、単一の &lt;DtbasePatternSet&gt; ノードとなる。
 * <li>&lt;DtbasePatternSet&gt; の子ノードは &lt;Dtbase&gt; のみであり、0 個以上指定できる。
 * <li>&lt;Dtbase&gt; ノードが、基底パターンを示すノードとなる。
 * <li>&lt;Dtbase&gt; ノードの属性に基底のキーを指定する。各基底キーは省略可能。
 * <li>基底の各基底キーが省略された(もしくは空文字の)場合、その基底キーはワイルドカード記号('*')に置き換えられる。
 * </ul>
 * XML 形式ファイルの読み込みにおいて、次の場合に {@link org.xml.sax.SAXParseException} 例外がスローされる。
 * <ul>
 * <li>XML のノードツリー構成が正しくない場合
 * <li>基底の各基底キーに使用できない文字が含まれている場合
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
public class DtBasePatternSet extends AbDtBaseSet<DtBasePattern> implements IDataOutput
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
	 * 要素を持たない <code>DtBasePatternSet</code> の新しいインスタンスを生成する。
	 * 
	 * @see	java.util.LinkedHashSet#LinkedHashSet()
	 */
	public DtBasePatternSet() {
		super();
	}

	/**
	 * 指定されたコレクションの要素を格納する <code>DtBasePattenSet</code> の
	 * 新しいインスタンスを生成する。
	 * <br>
	 * デフォルトの負荷係数、および指定されたコレクションの要素を
	 * 格納するのに十分な初期容量により作成される。
	 * <br>
	 * コレクションに含まれる <tt>null</tt> 要素は無視される。
	 * 
	 * @param c	要素がセットに配置される <code>DtBasePatten</code> のコレクション
	 * 
	 * @throws NullPointerException 指定されたコレクションが <tt>null</tt> の場合
	 * 
	 * @see java.util.LinkedHashSet#LinkedHashSet(Collection)
	 */
	public DtBasePatternSet(Collection<? extends DtBasePattern> c) {
		super(Math.max((int)(c.size()/.75f) + 1, 16));
		addAll(c);
	}

	/**
	 * 指定された初期容量および負荷係数で、<code>DtBasePatternSet</code> の
	 * 新しいインスタンスを生成する。
	 * 
	 * @param initialCapacity	基底パターン集合(セット)の初期容量
	 * @param loadFactor	基底パターン集合(セット)の負荷係数
	 * 
	 * @throws IllegalArgumentException	初期容量が 0 より小さいか、負荷係数が正の値ではない場合
	 * 
	 * @see java.util.LinkedHashSet#LinkedHashSet(int, float)
	 */
	public DtBasePatternSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * 指定された初期容量およびデフォルトの負荷係数で、<code>DtBasePatternSet</code> の
	 * 新しいインスタンスを生成する。
	 * 
	 * @param initialCapacity	基底パターン集合(セット)の初期容量
	 * 
	 * @throws IllegalArgumentException	初期容量が 0 より小さい場合
	 * 
	 * @see java.util.LinkedHashSet#LinkedHashSet(int)
	 */
	public DtBasePatternSet(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * 指定された基底集合の要素を格納する <code>DtBasePatternSet</code> の
	 * 新しいインスタンスを生成する。
	 * <br>
	 * 基本的に各基底キーの文字列をそのまま継承し、<code>DtBasePattern</code> インスタンスに
	 * 変換した要素を格納する。基底キーに含まれるアスタリスク文字はワイルドカードとみなされる。
	 * <br>
	 * コレクションに含まれる <tt>null</tt> 要素は無視される。
	 * 
	 * @param bases	要素がセットに配置されるデータ代数基底集合
	 * @param ignoreTypeKey	データ型キーをワイルドカードとする場合は <tt>true</tt> を指定する
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 */
	public DtBasePatternSet(DtBaseSet bases, boolean ignoreTypeKey) {
		super(Math.max((int)(bases.size()/.75f) + 1, 16));
		addAll(bases, ignoreTypeKey);
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
	 * 基底パターン集合では、要素の基底パターンが重複することはないため、
	 * {@link #union(DtBasePatternSet)} と同じ結果を返す。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	この集合と連結する基底パターン集合
	 * @return		この集合と指定された集合を連結した新しい基底パターン集合
	 * 
	 * @throws NullPointerException 指定された集合が <tt>null</tt> の場合
	 */
	public DtBasePatternSet addition(DtBasePatternSet set) {
		return union(set);
	}

	/**
	 * この集合から指定された基底パターン集合の要素を除いた、新しい基底パターン集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底パターン集合では、要素の基底パターンが重複することはないため、
	 * {@link #union(DtBasePatternSet)} と同じ結果を返す。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	この集合から取り除く要素を持つ基底パターン集合
	 * @return		この集合から指定された集合の要素を取り除いた、新しい基底パターン集合
	 * 
	 * @throws NullPointerException 指定された集合が <tt>null</tt> の場合
	 */
	public DtBasePatternSet subtraction(DtBasePatternSet set) {
		return difference(set);
	}

	/**
	 * この集合から指定された基底パターン集合の要素以外を除いた、新しい基底パターン集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底パターン集合では、要素の基底パターンが重複することはないため、
	 * {@link #union(DtBasePatternSet)} と同じ結果を返す。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set	この集合において維持する要素を持つ基底パターン集合
	 * @return	この集合から指定された集合の要素以外を除いた、新しい基底パターン集合
	 * 
	 * @throws NullPointerException	指定された集合が <tt>null</tt> の場合
	 */
	public DtBasePatternSet retention(DtBasePatternSet set) {
		return intersection(set);
	}

	/**
	 * 指定された基底パターン集合との和を返す。
	 * <p>
	 * 基底パターン集合の和演算(this ∪ set)は、この集合と指定された集合の全ての要素を
	 * 合成した集合を生成する。演算結果として生成された集合には、重複する要素は
	 * 存在しない。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set 和を取る基底パターン集合の一方
	 * @return 2 つの基底パターン集合の和となるインスタンス
	 * 
	 * @throws NullPointerException 指定された集合が <tt>null</tt> の場合
	 */
	public DtBasePatternSet union(DtBasePatternSet set) {
		DtBasePatternSet newSet = (DtBasePatternSet)clone();
		newSet.addAll(Validations.validNotNull(set));
		return newSet;
	}

	/**
	 * 指定の基底パターン集合との積(共通部分)を返す。
	 * <p>
	 * 基底パターン集合の積演算(this ∩ set)は、この集合と指定された集合の両方に含まれる
	 * 要素のみを格納する集合を生成する。演算結果として生成された集合には、
	 * 重複する要素は存在しない。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set 積を取る基底パターン集合の一方
	 * @return 2 つの基底パターン集合の積となるインスタンス
	 * 
	 * @throws NullPointerException 指定された集合が <tt>null</tt> の場合
	 */
	public DtBasePatternSet intersection(DtBasePatternSet set) {
		DtBasePatternSet newSet = (DtBasePatternSet)clone();
		newSet.retainAll(Validations.validNotNull(set));
		return newSet;
	}

	/**
	 * 指定された基底パターン集合との差を返す。
	 * <p>
	 * 基底パターン集合の差演算(this - set)は、この集合から指定された集合の要素を除いた
	 * 集合を生成する。演算結果として生成された集合には、重複する要素は存在しない。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param set 差し引く基底パターンの集合
	 * @return 指定された基底パターン集合を差し引いた結果となるインスタンス
	 * 
	 * @throws NullPointerException 指定された集合が <tt>null</tt> の場合
	 */
	public DtBasePatternSet difference(DtBasePatternSet set) {
		DtBasePatternSet newSet = (DtBasePatternSet)clone();
		newSet.removeAll(Validations.validNotNull(set));
		return newSet;
	}

	/**
	 * 指定の基底がこの基底パターン集合のどれかにマッチするかどうかを判定する。
	 * 
	 * @param base	一致を判定する交換代数基底
	 * @return	このパターン集合のどれかと指定された基底が一致する場合のみ <tt>true</tt> を返す
	 */
	public boolean matches(DtBase base) {
		if (base == null)
			return false;
		
		for (DtBasePattern pattern : this) {
			if (pattern.matches(base)) {
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
	 * @param pattern	集合に追加する要素
	 * @return	指定された要素を保持していなかった場合は <tt>true</tt>
	 * 
	 * @throws NullPointerException 指定されたパターンが <tt>null</tt> の場合
	 */
	public boolean add(DtBasePattern pattern) {
		Validations.validNotNull(pattern, "'pattern' argument cannot be null.");
		return super.add(CacheManager.cacheDtBasePattern(pattern));
	}

	/**
	 * 指定されたコレクションのすべての要素をこの基底パターン集合に追加する。
	 * <br>
	 * 指定されたコレクションに含まれる <tt>null</tt> 要素は無視される。
	 * 
	 * @param c	この基底パターン集合に追加する要素のコレクション
	 * @return	この呼び出しの結果、この基底パターン集合が変更された場合は <tt>true</tt>
	 * 
	 * @throws NullPointerException	指定されたコレクションが <tt>null</tt> の場合
	 * 
	 * @see java.util.LinkedHashSet#addAll(Collection)
	 */
	public boolean addAll(Collection<? extends DtBasePattern> c) {
		boolean modified = false;
		for (DtBasePattern pattern : c) {
			if (pattern != null) {
				if (super.add(CacheManager.cacheDtBasePattern(pattern))) {
					modified = true;
				}
			}
		}
		return modified;
	}
	
	/**
	 * 指定された基底集合のすべての要素を <code>DtBasePattern</code> に変換し、
	 * この基底パターン集合に追加する。
	 * <br>
	 * 基本的に各基底キーの文字列をそのまま継承し、<code>DtBasePattern</code> インスタンスに
	 * 変換した要素を格納する。基底キーに含まれるアスタリスク文字はワイルドカードとみなされる。
	 * <br>
	 * コレクションに含まれる <tt>null</tt> 要素は無視される。
	 * 
	 * @param bases	要素がセットに配置されるデータ代数基底集合
	 * @param ignoreTypeKey	データ型キーをワイルドカードとする場合は <tt>true</tt> を指定する
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 */
	public boolean addAll(DtBaseSet bases, boolean ignoreTypeKey) {
		boolean modified = false;
		for (DtBase base : bases) {
			if (base != null) {
				if (super.add(DtBasePattern.newPattern(base, ignoreTypeKey))) {
					modified = true;
				}
			}
		}
		return modified;
	}

	/**
	 * 基底パターン集合のシャローコピーを返す。
	 * 要素自体は複製されない。
	 * 
	 * @return この基底パターン集合のシャローコピー
	 */
	public Object clone() {
		DtBasePatternSet newSet = (DtBasePatternSet)super.clone();
		return newSet;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// I/O
	//------------------------------------------------------------
	
	/**
	 * 基底パターン集合の内容を、指定のファイルに CSV フォーマットで出力する。
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
	 * 基底パターン集合の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
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
	 * CSV フォーマットのファイルを読み込み、新しい基底パターン集合を生成する。
	 * <p>
	 * 基本的に、ファイルに記述された要素の順序で、新しいインスタンスに格納される。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DtBasePatternSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 */
	static public DtBasePatternSet fromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		CsvReader reader = new CsvReader(csvFile);
		DtBasePatternSet newSet = new DtBasePatternSet();
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
	 * @return ファイルの内容で生成された、新しい <code>DtBasePatternSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	static public DtBasePatternSet fromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		DtBasePatternSet newSet = new DtBasePatternSet();
		try {
			newSet.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newSet;
	}
	
	/**
	 * 基底パターン集合の内容を、指定のファイルに XML フォーマットで出力する。
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
	 * XML フォーマットのファイルを読み込み、新しい基底パターン集合を生成する。
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
	static public DtBasePatternSet fromXML(File xmlFile)
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
	static protected final String CSV_KEYWORD = "#DtbasePatternSet";

	/**
	 * XMLファイルフォーマットでの基底パターン集合のルートノード名
	 */
	static protected final String XML_ELEMENT_NAME = "DtbasePatternSet";
	
	/**
	 * 基底パターン集合の内容から、XML ドキュメントを生成する。
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
	 * CSVフォーマットで DtBasePatternSet の内容を出力
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
		for (DtBasePattern pattern : this) {
			pattern.writeFieldToCSV(writer);
			writer.newLine();
		}
		writer.flush();
	}

	/**
	 * CSVフォーマットで DtBasePatternSet の内容を読み込む
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
			// undefined DtBasePatternSet CSV ID
			throw new CsvFormatException("DtBasePatternSet file ID not found.");
		} else {
			CsvReader.CsvField field = record.getField(0);
			if (field == null || !CSV_KEYWORD.equals(field.getValue())) {
				throw new CsvFormatException("Illegal DtBasePatternSet file ID.", record.getLineNo(), 1);
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
			DtBasePattern newPattern = DtBasePattern.readFieldFromCSV(freader);
			fastAdd(newPattern);
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
		for (DtBasePattern pattern : this) {
			Element child = pattern.encodeToElement(xmlDocument);
			node.appendChild(child);
		}
		
		// completed
		return node;
	}

	/**
	 * <code>DtBasePatternSet</code> の SAX パーサーハンドラ。
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
		
		private final DtBasePatternSet bset = new DtBasePatternSet();
		
		public DtBasePatternSet getTargetInstance() {
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
			else if (DtBasePattern.XML_ELEMENT_NAME.equals(qName)) {
				XmlErrors.validChildOfParent(saxLocator, qName, XmlErrors.peekStringStack(stackElement), XML_ELEMENT_NAME);
				// create DtBasePattern by attributes
				DtBasePattern newPattern =
					DtBasePattern.decodeFromXmlAttributes(saxLocator, qName, attributes);
				bset.add(newPattern);
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
