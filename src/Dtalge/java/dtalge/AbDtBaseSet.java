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
 * @(#)AbDtBaseSet.java	0.10	2008/08/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * データ代数の基底の集合を保持するクラスの抽象クラス。
 * 
 * データ代数基底集合に関する共通機能を提供する。
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
 * 
 * @version 0.10	2008/08/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @see java.util.LinkedHashSet
 */
public abstract class AbDtBaseSet<E extends AbDtBase> extends LinkedHashSet<E>
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
	public AbDtBaseSet() {
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
     * @param c 要素がセットに配置される基底のコレクション
     * 
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
     * 
     * @see java.util.LinkedHashSet#LinkedHashSet(Collection)
     */
	public AbDtBaseSet(Collection<? extends E> c) {
		super(Math.max((int) (c.size()/.75f) + 1, 16));
		addAll(c);
	}

    /**
     * 指定された初期容量および指定された負荷係数で、新しい空の基底集合を生成する。
     * 
     * @param initialCapacity 基底集合(セット)の初期容量
     * @param loadFactor 基底集合(セット)の負荷係数
     * 
     * @throws 初期容量が 0 より小さいか、負荷係数が正の値ではない場合にスローされる
     * 
     * @see java.util.LinkedHashSet#LinkedHashSet(int, float)
     */
	public AbDtBaseSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

    /**
     * 指定された初期容量およびデフォルトの負荷係数で、新しい空の基底集合を生成する。
     * 
     * @param initialCapacity 基底集合(セット)の初期容量
     * 
     * @throws 初期容量が 0 より小さい場合にスローされる
     * 
     * @see java.util.LinkedHashSet#LinkedHashSet(int)
     */
	public AbDtBaseSet(int initialCapacity) {
		super(initialCapacity);
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	/**
	 * この基底集合に含まれる基底の名前キーのみの集合を取得する。
	 * このメソッドが返す集合は、次のような規則で構成される。
	 * <ul>
	 * <li>キーの文字列は重複しない
	 * <li>キーの文字列は、文字列の自然順序付けにより昇順にソートされる
	 * </ul>
	 * 
	 * @param withoutOmitted	省略記号('#') を除外する場合は <tt>true</tt> を指定する
	 * @return	基底の名前キーの集合
	 */
	public Set<String> getBaseNameKeySet(boolean withoutOmitted) {
		return getBaseKeySet(DtBase.KEY_NAME, withoutOmitted);
	}
	
	/**
	 * この基底集合に含まれる基底のデータ型キーのみの集合を取得する。
	 * このメソッドが返す集合は、次のような規則で構成される。
	 * <ul>
	 * <li>キーの文字列は重複しない
	 * <li>キーの文字列は、文字列の自然順序付けにより昇順にソートされる
	 * </ul>
	 * 
	 * @param withoutOmitted	省略記号('#') を除外する場合は <tt>true</tt> を指定する
	 * @return	基底のデータ型キーの集合
	 */
	public Set<String> getBaseTypeKeySet(boolean withoutOmitted) {
		return getBaseKeySet(DtBase.KEY_TYPE, withoutOmitted);
	}
	
	/**
	 * この基底集合に含まれる基底の属性キーのみの集合を取得する。
	 * このメソッドが返す集合は、次のような規則で構成される。
	 * <ul>
	 * <li>キーの文字列は重複しない
	 * <li>キーの文字列は、文字列の自然順序付けにより昇順にソートされる
	 * </ul>
	 * 
	 * @param withoutOmitted	省略記号('#') を除外する場合は <tt>true</tt> を指定する
	 * @return	基底の属性キーの集合
	 */
	public Set<String> getBaseAttributeKeySet(boolean withoutOmitted) {
		return getBaseKeySet(DtBase.KEY_ATTR, withoutOmitted);
	}
	
	/**
	 * この基底集合に含まれる基底の主体キーのみの集合を取得する。
	 * このメソッドが返す集合は、次のような規則で構成される。
	 * <ul>
	 * <li>キーの文字列は重複しない
	 * <li>キーの文字列は、文字列の自然順序付けにより昇順にソートされる
	 * </ul>
	 * 
	 * @param withoutOmitted	省略記号('#') を除外する場合は <tt>true</tt> を指定する
	 * @return	基底の主体キーの集合
	 */
	public Set<String> getBaseSubjectKeySet(boolean withoutOmitted) {
		return getBaseKeySet(DtBase.KEY_SUBJECT, withoutOmitted);
	}

	/**
	 * 指定されたインデックスの位置にある基底キーのみの集合を取得する。
	 * このメソッドが返す集合は、次のような規則で構成される。
	 * <ul>
	 * <li>キーの文字列は重複しない
	 * <li>キーの文字列は、文字列の自然順序付けにより昇順にソートされる
	 * </ul>
	 * 
	 * @param index		取得する基底キーのインデックス
	 * @param withoutOmitted	省略記号('#') を除外する場合は <tt>true</tt> を指定する
	 * @return	指定された基底キーの集合
	 * 
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	protected Set<String> getBaseKeySet(int index, boolean withoutOmitted) {
		if (withoutOmitted)
			return getBaseKeySet(index, Collections.singleton(DtBase.OMITTED));
		else
			return getBaseKeySet(index, null);
	}

	/**
	 * 指定されたインデックスの位置にある基底キーのみの集合を取得する。
	 * このメソッドが返す集合は、次のような規則で構成される。
	 * <ul>
	 * <li>キーの文字列は重複しない
	 * <li>キーの文字列は、文字列の自然順序付けにより昇順にソートされる
	 * </ul>
	 * <p>
	 * <code>withoutStrings</code> が <tt>null</tt> ではない場合、この引数のコレクションに
	 * 含まれる文字列は、このメソッドが返す集合から除外される。
	 * 
	 * @param index		取得する基底キーのインデックス
	 * @param withoutStrings	除外する文字列の集合
	 * @return	指定された基底キーの集合
	 */
	protected Set<String> getBaseKeySet(int index, Collection<? extends String> withoutStrings) {
		Set<String> resultSet = new TreeSet<String>();
		for (E base : this) {
			resultSet.add(base._baseKeys[index]);
		}
		if (withoutStrings != null && !withoutStrings.isEmpty()) {
			resultSet.removeAll(withoutStrings);
		}
		return resultSet;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定された要素が基底集合の要素として存在しない場合に、その要素を基底集合に追加する。
	 * <p>
	 * このメソッドは、値の整合性はチェックせず、単純に集合へ追加する。
	 * <p><b>注:</b>このメソッドの引数に指定する値は、キャッシュメソッドを
	 * 適用したインスタンスであり、かつ、<tt>null</tt> ではないことを前提とする。
	 * 
	 * @param base	集合に追加する要素
	 * @return	指定された要素を保持していなかった場合は <tt>true</tt>
	 * 
	 * @see java.util.LinkedHashSet#add(Object)
	 */
	protected boolean fastAdd(E base) {
		return super.add(base);
	}

    /**
     * 指定されたコレクションのすべての要素をこの基底集合に追加する。
     * <br>
     * このメソッドは、値の整合性はチェックせず、単純に集合へ追加する。
	 * <p><b>注:</b>このメソッドの引数に指定する値は、キャッシュメソッドを
	 * 適用したインスタンスであり、かつ、<tt>null</tt> ではないことを前提とする。
     * 
     * @param c この基底集合に追加するコレクション
     * 
     * @return この呼び出しの結果、この集合が変更された場合は true
     * 
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
     * 
     * @see java.util.LinkedHashSet#addAll(Collection)
     */
	protected boolean fastAddAll(Collection<? extends E> c) {
		return super.addAll(c);
	}
}
