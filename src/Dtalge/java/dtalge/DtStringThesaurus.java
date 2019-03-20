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
 *  Copyright 2007-2011  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)DtStringThesaurus.java	0.30	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtStringThesaurus.java	0.20	2010/02/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtStringThesaurus.java	0.10	2008/08/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
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

import dtalge.exception.CsvFormatException;
import dtalge.io.IDataOutput;
import dtalge.io.internal.CsvReader;
import dtalge.io.internal.CsvWriter;
import dtalge.io.internal.XmlDocument;
import dtalge.util.Strings;
import dtalge.util.Validations;
import dtalge.util.internal.CacheManager;
import dtalge.util.internal.XmlErrors;

/**
 * 語句のシソーラス定義を保持するクラス。
 * <p>
 * このシソーラス定義は、語句(文字列型データ)の大小関係(親子関係)を保持する。
 * 2 つの語句の直接的な関係を親子関係と呼び、子は親よりも小さいと定義する。
 * シソーラスに定義された関係において、ある語句より小さいものを子孫と呼び、
 * ある語句より大きいものを祖先と呼称する。
 * <p>
 * データ代数におけるシソーラスは、次のように定義される。
 * <dl>
 * <dt>シソーラス集合</dt>
 * <dd>語句の半順序構造が定義された集合をシソーラスと呼ぶ。語句 x と語句 y に
 * おいて、語句 x が語句 y よりも小さいとする関係を x &lt; y と表記する。</dd>
 * <dt>比較可能</dt>
 * <dd>シソーラス集合に含まれる語句 x,y において、x,y が (x&lt;y)∨(y&lt;x) のとき、
 * 語句 x,y は比較可能という。この場合、x～y と記す。</dd>
 * <dt>比較不能</dt>
 * <dd>シソーラス集合に含まれる語句 x,y において、x,y が ￢(x&lt;y)∧￢(y&lt;x) のとき、
 * 語句 x,y は比較不能という。この場合、￢(x～y) と記す。</dd>
 * <dt>分類集合</dt>
 * <dd>シソーラス集合に含まれる語句の集合Ｓにおいて、Ｓに含まれるすべての語句の関係が
 * 比較不能(∀x,y∈Ｓ；￢(x～y))のとき、この集合Ｓを分類集合と呼ぶ。分類集合は、
 * 概念の上下関係にある元を含まない集合のことである。</dd>
 * </dl>
 * <p>
 * このクラスのシソーラス定義には、次のような制約がある。
 * <ul>
 * <li><S>1つの子は、1つ以上の異なる親との関係を保持する。</S>
 * <li>ある語句の祖先(もしくは子孫)が、この語句自身となるような(循環関係となる)関係付けは行えない。
 * <li>語句は全て有効な文字列であることを前提とする。<tt>null</tt> や空文字(長さが 0 の文字列)は
 * 語句として扱えない。
 * <li>ある語句の関係は、シソーラス定義内で重複しないことを保証する。
 * </ul>
 * また、<b>この実装は同期化されない</b>。
 * <p>
 * <b>《入出力フォーマット》</b>
 * <br>
 * <code>DtStringThesaurus</code> は、CSV ファイル、XML ファイル形式の入出力インタフェースを
 * 提供する。CSV 形式、XML 形式共に、次の制約がある。
 * <ul>
 * <li>語句として、<tt>null</tt> もしくは空文字(長さが 0 の文字列)は指定できない。
 * <li>ある語句の先祖が、その語句自身となるような(循環関係となる)関係付けは指定できない。
 * <li><S>ある語句の直接の親は 1 つ以上の異なる語句を定義できる。</S>
 * <li>CSV 形式ファイルのテキスト・ファイル・エンコーディングは、入出力時に指定されない場合は、
 * プラットフォーム標準の文字コードとなる。
 * <li>XML 形式ファイルのテキスト・ファイル・エンコーディングは、&quot;UTF-8&quot; となる。
 * </ul>
 * <p>
 * <b>&lt;CSV 形式&gt;</b>
 * <p>
 * CSV 形式のファイルは、カンマ区切りのテキストファイルであり、次のようなフォーマットとなる。
 * <ul>
 * <li>CSV ファイルの第 1 行目は、次のキーワードであること。大文字、小文字も区別される。
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;#Thesaurus
 * <li>空白文字も有効な文字と認識する。
 * <li>1 行を 1 関係(組)とし、行の先頭から次のような順序でカラムを識別する。
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;<i>親データ値</i>, <i>子データ値</i>
 * <li>空白行(行の先頭で改行されている行)は、無視される。
 * </ul>
 * CSV 形式ファイルの読み込みにおいて、次の場合に {@link dtalge.exception.CsvFormatException} 例外がスローされる。
 * <ul>
 * <li>CSV ファイルの第 1 行目が #Thesaurus ではない場合
 * <li>各カラムのデータ値が省略された(空文字だった)場合
 * <li>語句の関係が循環している場合
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
 * &lt;Thesaurus&gt;
 *   &lt;ThesOrder&gt;
 *     &lt;ThesParent&gt;
 *       &lt;ThesValue&gt;<i>親データ値</i>&lt;/ThesValue&gt;
 *     &lt;/ThesParent&gt;
 *     &lt;ThesChildren&gt;
 *       &lt;ThesValue&gt;<i>子データ値</i>&lt;/ThesValue&gt;
 *              ・
 *              ・
 *              ・
 *     &lt;/ThesChildren&gt;
 *   &lt;/ThesOrder&gt;
 *         ・
 *         ・
 *         ・
 * &lt;/Thesaurus&gt;
 * </code></pre>
 * <ul>
 * <li>ルートノードは、単一の &lt;Thesaurus&gt; ノードとなる。
 * <li>&lt;Thesaurus&gt; の子ノードは &lt;ThesOrder&gt; のみであり、0 個以上指定できる。
 * <li>&lt;ThesOrder&gt; ノードが、語句の直接の親子関係を示すノードとなる。
 * <li>&lt;ThesOrder&gt; の子ノードは &lt;ThesParent&gt; と &lt;ThesChildren&gt; のみであり、
 * 必ず 1 つずつ指定する。順序は問わない。
 * <li>&lt;ThesParent&gt; ノードが、親となる語句を示すノードとなる。
 * <li>&lt;ThesParent&gt; の子ノードは &lt;ThesValue&gt; のみであり、必ず 1 つ指定する。
 * <li>&lt;ThesChildren&gt; ノードが、子となる語句を示すノードとなる。
 * <li>&lt;ThesChildren&gt; の子ノードは &lt;ThesValue&gt; のみであり、1 つ以上指定する。
 * <li>&lt;ThesValue&gt; ノードが、語句を示すノードとなる。
 * </ul>
 * XML 形式ファイルの読み込みにおいて、次の場合に {@link org.xml.sax.SAXParseException} 例外がスローされる。
 * <ul>
 * <li>XML のノードツリー構成が正しくない場合
 * <li>語句(親データ値もしくは子データ値)が省略された(空文字だった)場合
 * <li>語句の関係が循環している場合
 * </ul>
 * XML 形式ファイルの入出力は、次のメソッドにより行う。
 * <ul>
 * <li>{@link #fromXML(File)}
 * <li>{@link #toXML(File)}
 * </ul>
 * 
 * @version 0.30	2011/03/16
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class DtStringThesaurus implements IDataOutput
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String[] EmptyStringArray = new String[0];

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 関係[子->親]を保持するマップ。マップのキーは子の語句となり、
	 * マップの値は親の語句の集合となる。
	 */
	protected final Map<String,Set<String>>	relationMap;	// key:child, value:parents
	/**
	 * 関係[親->子]を保持するマップ。マップのキーは親の語句となり、
	 * マップの値は子の語句の集合となる。
	 */
	protected transient final Map<String,Set<String>> childrenMap;	// key:parent, value:children

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 空のシソーラス定義となる <code>DtStringThesaurus</code> の新しいインスタンスを生成する。
	 */
	public DtStringThesaurus() {
		this.relationMap = createMPRelationMap();
		this.childrenMap = createChildrenMap();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * シソーラス定義をすべてクリアする。
	 */
	public void clear() {
		relationMap.clear();
		childrenMap.clear();
	}

	/**
	 * シソーラス定義が存在しないなら <tt>true</tt> を返す。
	 * 
	 * @return	シソーラス定義が存在しない場合は <tt>true</tt>
	 */
	public boolean isEmpty() {
		return relationMap.isEmpty();
	}

	/**
	 * シソーラス定義の関係数を返す。
	 * 関係数とは、2 つの語句の直接的な結びつき(親子関係)を 1 とした場合の総数となる。
	 * 従って、親を複数持つ子がある場合、その関係もカウントされる。
	 * 
	 * @return 語句の直接的な関係数
	 */
	public int size() {
		if (relationMap.isEmpty()) {
			return 0;
		}
		
		int iSize = 0;
		for (Set<String> parents : relationMap.values()) {
			if (parents != null) {
				iSize += parents.size();
			}
		}
		
		return iSize;
	}

	/**
	 * 指定された語句が直接的な関係となる子を有する場合は <tt>true</tt> を返す。
	 * 
	 * @param word	判定する語句
	 * @return	直接の子が定義されている場合は <tt>true</tt>
	 */
	public boolean hasChild(String word) {
		return (word == null ? false : childrenMap.containsKey(word));
	}

	/**
	 * 指定された語句が直接的な関係となる親を有する場合は <tt>true</tt> を返す。
	 * 
	 * @param word	判定する語句
	 * @return	直接の親が定義されている場合は <tt>true</tt>
	 */
	public boolean hasParent(String word) {
		return (word == null ? false : relationMap.containsKey(word));
	}

	/**
	 * 指定された語句がシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param word	判定する語句
	 * @return	関係が定義されている語句であれば <tt>true</tt>
	 */
	public boolean contains(String word) {
		if (word != null)
			return (relationMap.containsKey(word) || childrenMap.containsKey(word));
		else
			return false;
	}
	
	/**
	 * 指定された語句の集合のどれか一つがシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param words		判定する語句の配列
	 * @return	シソーラス定義内に存在すれば <tt>true</tt>
	 */
	public boolean containsAny(String...words) {
		if (words != null) {
			for (String word : words) {
				if (contains(word)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 指定された語句の集合のどれか一つがシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param c		判定する語句の集合
	 * @return	シソーラス定義内に存在すれば <tt>true</tt>
	 */
	public boolean containsAny(Collection<? extends String> c) {
		if (c != null) {
			for (String word : c) {
				if (contains(word)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 指定された語句全てがシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param words		判定する語句の配列
	 * @return	シソーラス定義内に全て存在すれば <tt>true</tt>
	 */
	public boolean containsAll(String...words) {
		if (words == null || words.length < 1)
			return false;
		for (String word : words) {
			if (!contains(word)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 指定された語句全てがシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param c		判定する語句の集合
	 * @return	シソーラス定義内に全て存在すれば <tt>true</tt>
	 */
	public boolean containsAll(Collection<? extends String> c) {
		if (c == null || c.isEmpty())
			return false;
		for (String word : c) {
			if (!contains(word)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 指定された親子関係がシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param parent	親となる語句
	 * @param child		子となる語句
	 * @return	2 つの語句が親子として定義されていれば <tt>true</tt>
	 */
	public boolean containsRelation(String parent, String child) {
		boolean exist = false;
		if (parent != null) {
			Set<String> stockParents = (child == null ? null : relationMap.get(child));
			if (stockParents != null && stockParents.contains(parent)) {
				exist = true;
			}
		}
		return exist;
	}

	/**
	 * 指定された語句の直接の親となる全ての語句を取得する。
	 * @param word	判定する語句
	 * @return	親となる全ての語句を格納する配列を返す。
	 * 			指定された語句が登録されていない場合や、親となる語句が未定義の場合は、
	 * 			要素が空の配列を返す。
	 * @since 0.30
	 */
	public String[] getParents(String word) {
		String[] ret = EmptyStringArray;
		Set<String> stockParents = (word==null ? null : relationMap.get(word));
		if (stockParents != null && !stockParents.isEmpty()) {
			ret = stockParents.toArray(new String[stockParents.size()]);
		}
		return ret;
	}

	/**
	 * 指定された語句の直接の子となる全ての語句を取得する。
	 * @param word	判定する語句
	 * @return	子となる全ての語句を格納する配列を返す。
	 * 			指定された語句が登録されていない場合や、子となる語句が未定義の場合は、
	 * 			要素が空の配列を返す。
	 * @since 0.30
	 */
	public String[] getChildren(String word) {
		String[] ret = EmptyStringArray;
		Set<String> stockChildren = (word==null ? null : childrenMap.get(word));
		if (stockChildren != null && !stockChildren.isEmpty()) {
			ret = stockChildren.toArray(new String[stockChildren.size()]);
		}
		return ret;
	}
	
	/**
	 * 指定された語句の直接の親となる全ての語句を取得する。
	 * @param word	判定する語句
	 * @return	親となる全ての語句を格納する文字列リストを返す。
	 * 			指定された語句が登録されていない場合や、親となる語句が未定義の場合は、
	 * 			要素が空の文字列リストを返す。
	 * @since 0.30
	 */
	public List<String> getThesaurusParents(String word) {
		List<String> retlist;
		Set<String> stockParents = (word==null ? null : relationMap.get(word));
		if (stockParents != null && !stockParents.isEmpty()) {
			retlist = new ArrayList<String>(stockParents);
		} else {
			retlist = new ArrayList<String>(0);
		}
		return retlist;
	}
	
	/**
	 * 指定された語句の直接の子となる全ての語句を取得する。
	 * @param word	判定する語句
	 * @return	子となる全ての語句を格納する文字列リストを返す。
	 * 			指定された語句が登録されていない場合や、子となる語句が未定義の場合は、
	 * 			要素が空の文字列リストを返す。
	 * @since 0.30
	 */
	public List<String> getThesaurusChildren(String word) {
		List<String> retlist;
		Set<String> stockChildren = (word==null ? null : childrenMap.get(word));
		if (stockChildren != null && !stockChildren.isEmpty()) {
			retlist = new ArrayList<String>(stockChildren);
		} else {
			retlist = new ArrayList<String>(0);
		}
		return retlist;
	}

	/**
	 * 指定された 2 つの語句が比較可能(関係を持つ)であれば <tt>true</tt> を返す。
	 * なお、2 つの引数が同値の場合、<tt>false</tt> を返す。
	 * 
	 * @param word1		検証する語句
	 * @param word2		検証する語句のもう一方
	 * @return	比較可能であれば <tt>true</tt>
	 */
	public boolean isComparable(String word1, String word2) {
		// 同値？
		if (word1 == null || word2 == null) {
			return false;
		}
		if (word1.equals(word2)) {
			return false;	// 同値の場合は、比較不能？
		}
		
		// word1 が word2 の子孫か検証する
		if (hasRelation(word1, word2)) {
			return true;
		}
		
		// word2 が word1 の子孫か検証する
		if (hasRelation(word2, word1)) {
			return true;
		}
		
		// 比較不能(関係は存在しない)
		return false;
	}

	/**
	 * 指定された 2 つの語句をシソーラス定義に基づき比較する。
	 * <code>word1</code> が <code>word2</code> の子孫にあたる(<code>word1</code> &lt; <code>word2</code>)場合は負の値を返す。
	 * <code>word1</code> が <code>word2</code> の祖先にあたる(<code>word1</code> &gt; <code>word2</code>)場合は正の値を返す。
	 * 上記以外の場合は 0 を返す。<br>
	 * なお、2 つの語句のどちらかが <tt>null</tt> もしくは同値の場合も 0 を返す。
	 * 
	 * @param word1		比較する語句
	 * @param word2		比較する語句のもう一方
	 * @return	比較結果を返す。
	 */
	public int compareTo(String word1, String word2) {
		// 同値？
		if (word1 == null || word2 == null) {
			return 0;
		}
		if (word1.equals(word2)) {
			return 0;
		}
		
		// word1 < word2
		if (hasRelation(word1, word2)) {
			return (-1);
		}
		
		// word1 > word2
		if (hasRelation(word2, word1)) {
			return (1);
		}
		
		// no relation
		return (0);
	}

	/**
	 * 指定された語句の集合が分類集合かを判定する。
	 * <p>
	 * 分類集合は、それぞれの語句の全ての組み合わせで比較不可能であることが
	 * 条件となる。
	 * 
	 * @param words		検証する語句の集合
	 * @return	分類集合であれば <tt>true</tt>
	 */
	public boolean isClassificationSet(String...words) {
		// 集合の要素が存在しない場合は、分類集合とみなさない
		if (words == null || words.length < 1) {
			return false;
		}
		
		// 集合の要素がシソーラス定義にすべて存在しなければ、分類集合とみなさない
		if (!containsAll(words)) {
			return false;
		}
		
		// 要素が 1 つなら、分類集合とみなす
		if (words.length == 1) {
			return true;
		}
		
		// すべての要素の関係をチェックする
		int outerLimit = words.length - 1;
		int innerLimit = words.length;
		for (int i = 0; i < outerLimit; i++) {
			String word1 = words[i];
			for (int j = i+1; j < innerLimit; j++) {
				String word2 = words[j];
				if (isComparable(word1, word2)) {
					// 比較可能な語句を含むため、分類集合とみなさない
					return false;
				}
			}
		}
		
		// すべての語句の組み合わせが比較不可能なので、分類集合とみなす
		return true;
	}
	
	/**
	 * 指定された語句の集合が分類集合かを判定する。
	 * <p>
	 * 分類集合は、それぞれの語句の全ての組み合わせで比較不可能であることが
	 * 条件となる。
	 * 
	 * @param c		検証する語句の集合
	 * @return	分類集合であれば <tt>true</tt>
	 */
	public boolean isClassificationSet(Collection<? extends String> c) {
		// 集合の要素が存在しない場合は、分類集合とみなさない
		if (c == null || c.isEmpty()) {
			return false;
		}
		
		// 分類集合かを検証
		return isClassificationSet(c.toArray(new String[c.size()]));
	}

	/**
	 * 語句の関係をシソーラスへ登録する。
	 * <p>
	 * このメソッドは、2 つの語句の親子関係(大小関係)をシソーラスへ登録する。
	 * 指定された関係が登録済みの場合、このメソッドは <tt>false</tt> を返す。
	 * なお、子として指定された語句がすでに別の親の子として関係が定義されている
	 * 場合、すでに存在する親を新しく指定された親との関係として上書きする。
	 * <p>
	 * シソーラス定義の制約に基づき、次の場合は例外をスローする。
	 * <ul>
	 * <li>語句が <tt>null</tt> もしくは、長さ 0 の文字列の場合
	 * <li>指定されたた 2 つの語句が等しい場合
	 * <li><code>parent</code> の語句が子、<code>child</code> の語句が親として定義済みの場合(循環関係となる為)
	 * </ul>
	 * 
	 * @param parent	親として登録する語句
	 * @param child		子として登録する語句
	 * @return			新しい関係が登録された場合 <tt>true</tt>
	 * 
	 * @throws IllegalArgumentException	指定された語句もしくは語句の関係が適切ではない場合
	 */
	public boolean put(String parent, String child) {
		// check
		Validations.validArgument(!Strings.isNullOrEmpty(parent), "Illegal parent : %s", String.valueOf(parent));
		Validations.validArgument(!Strings.isNullOrEmpty(child), "Illegal child : %s", String.valueOf(child));
		Validations.validArgument(!parent.equals(child), "Parent word is same as child word : %s", String.valueOf(parent));
		
		// 文字列のキャッシュ
		String cachedParent = CacheManager.cacheString(parent);
		String cachedChild  = CacheManager.cacheString(child);
		
		// 登録済みの関係かチェックする
		if (containsRelation(cachedParent, cachedChild)) {
			// already exist relation
			return false;
		}
		
		// 循環参照のチェック
		if (hasRelation(cachedParent, cachedChild)) {
			// parent の親が child として登録済みの場合、この関係は循環参照となる。
			throw new IllegalArgumentException(
					String.format("Illegal [\"%s\" < \"%s\"] relation, because already exist [\"%s\" > \"%s\"] relation.",
					cachedChild, cachedParent, cachedChild, cachedParent));
		}
		
		// 登録
		//--- parent
		Set<String> parents = relationMap.get(cachedChild);
		if (parents == null) {
			parents = createParentsCollection();
			relationMap.put(cachedChild, parents);
		}
		parents.add(cachedParent);
		//--- child
		Set<String> children = childrenMap.get(cachedParent);
		if (children == null) {
			children = createChildrenCollection();
			childrenMap.put(cachedParent, children);
		}
		children.add(cachedChild);
		
		// 登録完了
		return true;
	}

	/**
	 * 指定された語句をシソーラスから除去する。
	 * <p>
	 * このメソッドは、指定された語句そのものをシソーラスから除去するため、
	 * この語句への全ての関係を除去する。
	 * 
	 * @param word	除去する語句
	 * @return	語句が除去された場合は <tt>true</tt>
	 */
	public boolean remove(String word) {
		if (word == null) {
			return false;
		}
		
		boolean removed = false;
		
		// 親との関係を除去
		Set<String> parents = relationMap.get(word);
		if (parents != null) {
			String[] aryParents = parents.toArray(new String[parents.size()]);
			// word->親の関係を除去
			for (String parent : aryParents) {
				remove(parent, word);
			}
			removed = true;
		}
		
		// word を親とするエントリを全て削除
		Set<String> children = childrenMap.get(word);
		if (children != null) {
			String[] aryChildren = children.toArray(new String[children.size()]);
			// 子->wordの関係を除去
			for (String child : aryChildren) {
				remove(word, child);
			}
			removed = true;
		}
		
		// finished
		return removed;
	}

	/**
	 * 指定された親子関係をシソーラスから除去する。
	 * 
	 * @param parent	除去する関係の親となる語句
	 * @param child		除去する関係の子となる語句
	 * @return	関係が除去された場合は <tt>true</tt>
	 */
	public boolean remove(String parent, String child) {
		// check exists relation
		if (!containsRelation(parent, child)) {
			// この親子関係は存在しない
			return false;
		}

		// remove from relationMap
		removeFromRelationMap(parent, child);
		
		// remove from childrenMap
		removeFromChildrenMap(parent, child);
		
		// finished
		return true;
	}

	/**
	 * このシソーラスのハッシュコード値を返す。
	 * シソーラスのハッシュコードは、シソーラス定義の各エントリのハッシュコード値の
	 * 合計である。これにより、任意の 2 つのシソーラス <tt>t1</tt> と <tt>t2</tt> に
	 * ついて、<tt>t1.equals(t2)</tt> の場合 <tt>t1.hashCode()==t2.hashCode()</tt> に
	 * なる。
	 * 
	 * @return	このシソーラスのハッシュコード値
	 */
	@Override
	public int hashCode() {
		// relationMap の hashCode を返す
		return relationMap.hashCode();
	}

	/**
	 * 指定されたオブジェクトとこのシソーラスが等しいかどうかを比較する。
	 * 指定されたオブジェクトがシソーラスであり、2 つのシソーラスが同じ
	 * 定義を表す場合に <tt>true</tt> を返す。
	 * 
	 * @param obj	このシソーラスと等しいかどうかを比較するオブジェクト
	 * @return	指定されたオブジェクトがこのシソーラスと等しい場合は <tt>true</tt>
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;	// same instance
		}
		
		if (!(obj instanceof DtStringThesaurus)) {
			return false;	// not same class
		}
		
		DtStringThesaurus another = (DtStringThesaurus)obj;
		// relationMap の内容が等しければ、等しいとする
		return (this.relationMap.equals(another.relationMap));
	}

	/**
	 * このシソーラスの文字列表現を返します。
	 * <p>
	 * 文字列表現は、エントリ(親と子の関係定義)の文字列表現を中括弧 (<tt>"{}"</tt>) で囲んで示すリストとなる。
	 * エントリの文字列表現は、1 つの親と 1 つ以上の子で構成され、次のように表される。
	 * <blockquote>
	 * <i>親</i><b>&gt;</b><b>{</b><i>子</i><b>,</b><i>子</i><b>,</b>...<b>}</b>
	 * </blockquote>
	 * 隣接するエントリの文字列表現は、文字 <tt>", "</tt> (コンマと空白文字) によって
	 * 区切られる。
	 * <p>
	 * この実装は空の文字列バッファを作成し、左中括弧を付加してから、定義のエントリを
	 * 反復して調べ、各エントリの文字列表現を順に付加していく。
	 * 最後のエントリの後には右中括弧が付加され、文字列バッファから文字列を取得して返す。
	 * 
	 * @return	このシソーラスの文字列表現
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		Iterator<Map.Entry<String, Set<String>>> it = childrenMap.entrySet().iterator();
		if (it.hasNext()) {
			Map.Entry<String, Set<String>> entry = it.next();
			sb.append(entry.getKey());
			sb.append(">");
			sb.append(entry.getValue());
		}
		while (it.hasNext()) {
			Map.Entry<String, Set<String>> entry = it.next();
			sb.append(", ");
			sb.append(entry.getKey());
			sb.append(">");
			sb.append(entry.getValue());
		}
		sb.append("}");
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 2 つの語句が関係を持つかを検証する。
	 * 現在のシソーラス定義において、<code>descendant</code> の先祖が
	 * <code>ancestor</code> なら、<tt>true</tt> を返す。
	 * なお、<code>descendant.equals(ancestor)</code> が <tt>true</tt> の
	 * 場合、このメソッドは <tt>false</tt> を返す。
	 * <p><b>注:</b>このメソッドでは、2 つの引数は <tt>null</tt> ではなく、
	 * 同値でもないことを前提としているため、<tt>null</tt> もしくは同値で
	 * あるかの検証は行わない。
	 * 
	 * @param descendant	子孫とみなす語句
	 * @param ancestor		先祖とみなす語句
	 * 
	 * @return	2 つの語句の関係が定義されている場合は <tt>true</tt>
	 */
	protected boolean hasRelation(Object descendant, Object ancestor) {
		// check exists child
		if (!relationMap.containsKey(descendant)) {
			return false;
		}
		// check exists parent
		if (!childrenMap.containsKey(ancestor)) {
			return false;
		}
		// check exists relation
		return hasRelationRecursive(descendant, ancestor);
	}
	
	/**
	 * 2 つの語句が関係を持つかを検証する。
	 * このメソッドは、<em>descendant</em> の親を辿り、<em>ancestor</em> が
	 * 見つかるまで再帰的に探索する。
	 * 
	 * @param descendant	子孫とみなす語句
	 * @param ancestor		先祖とみなす語句
	 * 
	 * @return	2 つの語句の関係が定義されている場合は <tt>true</tt>
	 * @since 0.30
	 */
	protected boolean hasRelationRecursive(Object descendant, Object ancestor) {
		Set<String> parents = relationMap.get(descendant);
		while (parents != null) {
			// has relation?
			if (parents.contains(ancestor)) {
				return true;
			}
			
			// next parents
			if (parents.size() == 1) {
				parents = relationMap.get(parents.iterator().next());
			} else {
				break;
			}
		}
		if (parents != null) {
			// next parents
			for (String parent : parents) {
				if (hasRelationRecursive(parent, ancestor)) {
					return true;
				}
			}
		}
		
		// not have relation
		return false;
	}
	
	/**
	 * 子親関係マップから、指定された関係を除去する。
	 * <p>
	 * このメソッドは子親関係マップからのみ関係を除去するものであり、
	 * 親子関係マップは変更されない。
	 * 
	 * @param parent	除去する関係の親となる語句
	 * @param child		除去する関係の子となる語句
	 * @return	関係が除去された場合は <tt>true</tt>
	 * 
	 * @since 0.30
	 */
	protected boolean removeFromRelationMap(String parent, String child) {
		boolean removed = false;

		Set<String> parents = relationMap.get(child);
		if (parents != null) {
			removed = parents.remove(parent);
			if (parents.isEmpty()) {
				relationMap.remove(child);
			}
		}
		
		return removed;
	}

	/**
	 * 親子関係マップから、指定された関係を除去する。
	 * <p>
	 * このメソッドは親子関係マップからのみ関係を除去するものであり、
	 * 子親関係マップは変更されない。
	 * 
	 * @param parent	除去する関係の親となる語句
	 * @param child		除去する関係の子となる語句
	 * @return	関係が除去された場合は <tt>true</tt>
	 */
	protected boolean removeFromChildrenMap(String parent, String child) {
		boolean removed = false;
		
		Set<String> children = childrenMap.get(parent);
		if (children != null) {
			removed = children.remove(child);
			if (children.isEmpty()) {
				childrenMap.remove(parent);
			}
		}
		
		return removed;
	}
	
	/**
	 * 子親マップの複数の親を格納するコレクションを生成する。
	 * @return 複数の親を格納する <code>Set</code> コレクション
	 * @since 0.30
	 */
	protected Set<String> createParentsCollection() {
		return new TreeSet<String>();
	}

	/**
	 * 親子マップの複数の子を格納するコレクションを生成する。
	 * 
	 * @return	複数の子を格納する <code>Set</code> コレクション
	 */
	protected Set<String> createChildrenCollection() {
		return new TreeSet<String>();
	}

	/**
	 * 子親関係マップを生成する。
	 * 
	 * @return 子から親への関係を格納するマップ
	 * @since 0.30
	 */
	protected Map<String,Set<String>> createMPRelationMap() {
		return new TreeMap<String,Set<String>>();
	}

	/**
	 * 親子関係マップを生成する。
	 * 
	 * @return 親から子への関係を格納するマップ
	 */
	protected Map<String,Set<String>> createChildrenMap() {
		return new TreeMap<String,Set<String>>();
	}

	//------------------------------------------------------------
	// I/O
	//------------------------------------------------------------

	/**
	 * シソーラスの内容を、指定のファイルに CSV フォーマットで出力する。
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
	 * シソーラスの内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
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
	 * CSV フォーマットのファイルを読み込み、新しいシソーラスを生成する。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DtBaseSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException シソーラス定義のデータが正しくない場合にスローされる
	 */
	static public DtStringThesaurus fromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException
	{
		CsvReader reader = new CsvReader(csvFile);
		DtStringThesaurus newSet = new DtStringThesaurus();
		try {
			newSet.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newSet;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しいシソーラスを生成する。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DtBaseSet</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException シソーラス定義のデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	static public DtStringThesaurus fromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		DtStringThesaurus newSet = new DtStringThesaurus();
		try {
			newSet.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newSet;
	}
	
	/**
	 * シソーラスの内容を、指定のファイルに XML フォーマットで出力する。
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
	 * XML フォーマットのファイルを読み込み、新しいシソーラスを生成する。
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
	static public DtStringThesaurus fromXML(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException
	{
		//XmlDocument xmlDocument = XmlDocument.fromFile(xmlFile);
		//return DtStringThesaurus.fromXML(xmlDocument);
		
		// SAX パーサ のファクトリー生成
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// フィーチャーの設定
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		// SAXパーサの生成
		SAXParser parser = factory.newSAXParser();
		// ハンドラの生成
		SaxParserHander saxHandler = new SaxParserHander();
		// パース
		parser.parse(xmlFile, saxHandler);
		//parser.parse(, handler, "hoge.dtd");
		
		return saxHandler.getTargetInstance();
	}

	//------------------------------------------------------------
	// Internal methods for I/O
	//------------------------------------------------------------

	/**
	 * CSVファイルフォーマットの第1行目のキーワード
	 */
	static protected final String CSV_KEYWORD = "#Thesaurus";

	/**
	 * XMLファイルフォーマットでのシソーラスのルートノード名
	 */
	static protected final String XML_ELEMENT_NAME = "Thesaurus";
	/**
	 * XMLファイルフォーマットでのシソーラスの関係定義ノード名
	 */
	static protected final String XML_ELEMENT_ORDER = "ThesOrder";
	/**
	 * XMLファイルフォーマットでのシソーラス関係の親ノード名
	 */
	static protected final String XML_ELEMENT_PARENT = "ThesParent";
	/**
	 * XMLファイルフォーマットでのシソーラス関係の子ノード名
	 */
	static protected final String XML_ELEMENT_CHILD = "ThesChildren";
	/**
	 * XMLファイルフォーマットでのシソーラス語句ノード名
	 */
	static protected final String XML_ELEMENT_VALUE = "ThesValue";

	// シソーラス定義をCSVフォーマットで出力する
	protected void writeToCSV(CsvWriter writer)
		throws IOException
	{
		// キーワード出力
		writer.writeLine(CSV_KEYWORD);

		// データ出力
		Iterator<Map.Entry<String, Set<String>>> it = childrenMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Set<String>> entry = it.next();
			String parent = entry.getKey();
			Set<String> children = entry.getValue();
			for (String child : children) {
				writer.writeField(parent);
				writer.writeField(child);
				writer.newLine();
			}
		}
		
		writer.flush();
	}

	// CSVからシソーラス定義を読み込む
	protected void readFromCSV(CsvReader reader)
		throws IOException, CsvFormatException
	{
		CsvReader.CsvRecord record;
		
		// 先頭１行目のキーワードをチェック
		record = reader.readRecord();
		if (record == null || !record.hasFields()) {
			// undefined DtStringThesaurus CSV ID
			throw new CsvFormatException("DtStringThesaurus file ID not found.");
		} else {
			CsvReader.CsvField field = record.getField(0);
			if (field == null || !CSV_KEYWORD.equals(field.getValue())) {
				throw new CsvFormatException("Illegal DtStringThesaurus file ID.", record.getLineNo(), 1);
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
			
			// read parent column
			String parent = freader.readValue();
			if (Strings.isNullOrEmpty(parent)) {
				throw new CsvFormatException("Parent word cannot be omitted.",
												freader.getLineNo(), freader.getNextPosition());
			}
			
			// read child column
			String child = freader.readValue();
			if (Strings.isNullOrEmpty(child)) {
				throw new CsvFormatException("Child word cannot be omitted.",
												freader.getLineNo(), freader.getNextPosition());
			}
			
			// put in thesaurus
			try {
				put(parent, child);
			}
			catch (IllegalArgumentException ex) {
				throw new CsvFormatException(ex.getMessage(), freader.getLineNo(), -1);
			}
		}
	}
	
	/**
	 * シソーラスの内容から、XML ドキュメントを生成する。
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

	// シソーラス定義の文字列からXMLエレメントを生成する。
	protected Element encodeToValueElement(XmlDocument xmlDocument, String value) throws DOMException
	{
		Element node = xmlDocument.createElement(XML_ELEMENT_VALUE);
		node.appendChild(xmlDocument.createTextNode(value));
		return node;
	}

	// シソーラス定義からXMLドキュメントを生成する
	protected Element encodeToElement(XmlDocument xmlDocument)	throws DOMException
	{
		// create node
		Element node = xmlDocument.createElement(XML_ELEMENT_NAME);
		
		// create entries
		Iterator<Map.Entry<String, Set<String>>> it = childrenMap.entrySet().iterator();
		while (it.hasNext()) {
			Element elemOrder = xmlDocument.createElement(XML_ELEMENT_ORDER);
			Map.Entry<String, Set<String>> entry = it.next();
			//--- create parent node
			String parent = entry.getKey();
			Element elemParent = xmlDocument.createElement(XML_ELEMENT_PARENT);
			elemParent.appendChild(encodeToValueElement(xmlDocument, parent));
			elemOrder.appendChild(elemParent);
			//--- create children nodes
			Set<String> children = entry.getValue();
			Element elemChildren = xmlDocument.createElement(XML_ELEMENT_CHILD);
			for (String child : children) {
				elemChildren.appendChild(encodeToValueElement(xmlDocument, child));
			}
			elemOrder.appendChild(elemChildren);
			node.appendChild(elemOrder);
		}
		
		// completed
		return node;
	}

	/**
	 * <code>DtStringThesaurus</code> の SAX パーサーハンドラ。
	 * 
	 * @version 0.20	2010/02/25
	 * 
	 * @author H.Deguchi(SOARS Project.)
	 * @author Y.Ishizuka(PieCake.inc,)
	 **/
	static class SaxParserHander extends DefaultHandler
	{
		private final Stack<String> stackElement = new Stack<String>();
		private final DtStringThesaurus thes = new DtStringThesaurus();
		private final StringBuilder strbuf = new StringBuilder();
		
		private Locator saxLocator = null;
		
		private String thesParent = null;
		private ArrayList<String> thesChildren = null;
		
		public DtStringThesaurus getTargetInstance() {
			return thes;
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
			// clear string buffer
			strbuf.setLength(0);
			
			// check node
			if (XML_ELEMENT_NAME.equals(qName)) {
				XmlErrors.validChildOfParent(saxLocator, qName, XmlErrors.peekStringStack(stackElement));
			}
			else if (XML_ELEMENT_ORDER.equals(qName)) {
				XmlErrors.validChildOfParent(saxLocator, qName, XmlErrors.peekStringStack(stackElement), XML_ELEMENT_NAME);
			}
			else if (XML_ELEMENT_PARENT.equals(qName)) {
				XmlErrors.validChildOfParent(saxLocator, qName, XmlErrors.peekStringStack(stackElement), XML_ELEMENT_ORDER);
				XmlErrors.validSingleElement(thesParent==null, saxLocator, qName);
			}
			else if (XML_ELEMENT_CHILD.equals(qName)) {
				XmlErrors.validChildOfParent(saxLocator, qName, XmlErrors.peekStringStack(stackElement), XML_ELEMENT_ORDER);
				XmlErrors.validSingleElement(thesChildren==null, saxLocator, qName);
				thesChildren = new ArrayList<String>();
			}
			else if (XML_ELEMENT_VALUE.equals(qName)) {
				XmlErrors.validChildOfParent(saxLocator, qName, XmlErrors.peekStringStack(stackElement), XML_ELEMENT_PARENT, XML_ELEMENT_CHILD);
				if (XML_ELEMENT_PARENT.equals(XmlErrors.peekStringStack(stackElement))) {
					XmlErrors.validSingleElement(thesParent==null, saxLocator, qName);
				}
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
			
			//
			if (XML_ELEMENT_VALUE.equals(qName)) {
				String strValue = strbuf.toString();
				strbuf.setLength(0);
				XmlErrors.validStringValue(saxLocator, strValue);
				String elemParent = XmlErrors.peekStringStack(stackElement);
				if (XML_ELEMENT_PARENT.equals(elemParent)) {
					thesParent = strValue;
				}
				else if (XML_ELEMENT_CHILD.equals(elemParent)) {
					thesChildren.add(strValue);
				}
			}
			else if (XML_ELEMENT_CHILD.equals(qName)) {
				XmlErrors.validNeedElement(thesChildren != null && !thesChildren.isEmpty(), saxLocator, qName, XML_ELEMENT_VALUE);
			}
			else if (XML_ELEMENT_PARENT.equals(qName)) {
				XmlErrors.validNeedElement(thesParent != null, saxLocator, qName, XML_ELEMENT_VALUE);
			}
			else if (XML_ELEMENT_ORDER.equals(qName)) {
				XmlErrors.validNeedElement(thesParent != null, saxLocator, qName, XML_ELEMENT_PARENT);
				XmlErrors.validNeedElement(thesChildren != null && !thesChildren.isEmpty(), saxLocator, qName, XML_ELEMENT_CHILD);
				// regist relation
				for (String child : thesChildren) {
					try {
						thes.put(thesParent, child);
					}
					catch (IllegalArgumentException ex) {
						throw new SAXParseException(ex.getMessage(), saxLocator, ex);
					}
				}
				// clear status
				thesParent = null;
				thesChildren = null;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			String elemParent = stackElement.isEmpty() ? null : stackElement.peek();
			if (XML_ELEMENT_VALUE.equals(elemParent)) {
				strbuf.append(ch, start, length);
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
