/*
 * @(#)BigExBaseSet.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.db;

import java.util.Collection;

import exalge2.ExBase;
import exalge2.ExBasePattern;
import exalge2.ExBasePatternSet;
import exalge2.ExBaseSet;
import redundantalge.db.BigIterator;

/**
 * 大容量の交換代数基底集合を表すオブジェクトのインタフェース。
 * 
 * @version 0.990
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface BigExBaseSet extends Iterable<ExBase>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトが管理するストレージを削除する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、このオブジェクトが管理するストレージが一時的なものかどうかに関係なく、
	 * 管理対象のストレージを削除するので、注意すること。
	 * なお、このメソッドを呼び出した後、このオブジェクトを利用するとデータが空の状態となる。
	 * </blockquote>
	 */
	public void delete();
	
	/**
	 * このオブジェクトのハッシュ値を返す。
	 * @return	ハッシュ値
	 */
	public int hashCode();

	/**
	 * 指定されたオブジェクトとこのオブジェクトが等しいかを判定する。
	 * @param obj	判定するオブジェクト
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean equals(Object obj);

	/**
	 * このオブジェクトの要素が空かどうかを判定する。
	 * @return	要素が空の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isEmpty();
	
	/**
	 * このオブジェクトが保持する要素数を返す。
	 * @return	要素数
	 */
	public long size();

	/**
	 * 指定された基底が含まれているかを判定する。
	 * @param base	判定する基底
	 * @return	含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean contains(ExBase base);

	/**
	 * 指定されたコレクションに含まれる要素のすべてが、自身に含まれているかどうかを判定する。
	 * @param c	判定するコレクション
	 * @return	すべての要素が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsAll(Collection<? extends ExBase> c);

	/**
	 * 指定されたコレクションに含まれる要素のすべてが、自身に含まれているかどうかを判定する。
	 * @param set	判定するコレクション
	 * @return	すべての要素が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsAll(BigExBaseSet set);

	/**
	 * 指定されたコレクションに含まれる要素のどれか一つが、自身に含まれているかどうかを判定する。
	 * @param c	判定するコレクション
	 * @return	どれか一つの要素が含まれている場合は <tt>true</tt>、一つも含まれていない場合は <tt>false</tt>
	 */
	public boolean containsAny(Collection<? extends ExBase> c);
	
	/**
	 * 指定されたコレクションに含まれる要素のどれか一つが、自身に含まれているかどうかを判定する。
	 * @param set	判定するコレクション
	 * @return	どれか一つの要素が含まれている場合は <tt>true</tt>、一つも含まれていない場合は <tt>false</tt>
	 */
	public boolean containsAny(BigExBaseSet set);
	
	/**
	 * このオブジェクトのイテレーターを返す。
	 * @return	イテレーターオブジェクト
	 */
	public BigIterator<ExBase> iterator();

	/**
	 * この集合から、すべての要素を削除する。
	 */
	public void clear();

	/**
	 * 指定された基底を、この集合に追加する。
	 * @param base	追加する基底
	 * @return	追加された場合は <tt>true</tt>、すでに存在する場合は <tt>false</tt>
	 */
	public boolean add(ExBase base);

	/**
	 * 指定された基底を、この集合から削除する。
	 * @param base	削除する基底
	 * @return	削除された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean remove(ExBase base);

	/**
	 * 指定されたコレクションに含まれるすべての基底を、この集合に追加する。
	 * @param c	追加する基底のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean addAll(Collection<? extends ExBase> c);
	
	/**
	 * 指定された基底集合に含まれるすべての基底を、この集合に追加する。
	 * @param set	追加する基底の集合
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean addAll(BigExBaseSet set);

	/**
	 * 指定されたコレクションに含まれるすべての基底を、この集合から削除する。
	 * @param c	削除する基底のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean removeAll(Collection<? extends ExBase> c);
	
	/**
	 * 指定された集合に含まれるすべての基底を、この集合から削除する。
	 * @param set	削除する基底の集合
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean removeAll(BigExBaseSet set);

	/**
	 * 指定されたコレクションに含まれるすべての基底のみを残し、その他の基底をこの集合から削除する。
	 * @param c	残す基底のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean retainAll(Collection<? extends ExBase> c);

	/**
	 * 指定された基底集合に含まれるすべての基底のみを残し、その他の基底をこの集合から削除する。
	 * @param set	残す基底の集合
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean retainAll(BigExBaseSet set);

	/**
	 * このオブジェクトの複製を生成する。
	 * 
	 * @return 複製された <code>BigExBaseSet</code> オブジェクト
	 */
	public BigExBaseSet copy();

	/**
	 * 基底集合を連結した、新しい基底集合のインスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #union(ExBaseSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結する基底集合
	 * @return		自身と引数に指定された集合を連結した新しい基底集合
	 */
	public BigExBaseSet addition(ExBaseSet set);
	
	/**
	 * 基底集合を連結した、新しい基底集合のインスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #union(BigExBaseSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結する基底集合
	 * @return		自身と引数に指定された集合を連結した新しい基底集合
	 */
	public BigExBaseSet addition(BigExBaseSet set);

	/**
	 * 引数に指定された基底集合の要素を除いた、新しい基底集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #difference(ExBaseSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つ基底集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しい基底集合
	 */
	public BigExBaseSet subtraction(ExBaseSet set);

	/**
	 * 引数に指定された基底集合の要素を除いた、新しい基底集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #difference(BigExBaseSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つ基底集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しい基底集合
	 */
	public BigExBaseSet subtraction(BigExBaseSet set);

	/**
	 * 指定の基底集合との和を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 和を取る基底集合の一方
	 * @return 二つの基底集合の和となるインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExBaseSet union(ExBaseSet set);

	/**
	 * 指定の基底集合との和を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 和を取る基底集合の一方
	 * @return 二つの基底集合の和となるインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExBaseSet union(BigExBaseSet set);

	/**
	 * 指定の基底集合との積(共通部分)を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 積を取る基底集合の一方
	 * @return 二つの基底集合の積となるインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExBaseSet intersection(ExBaseSet set);

	/**
	 * 指定の基底集合との積(共通部分)を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 積を取る基底集合の一方
	 * @return 二つの基底集合の積となるインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExBaseSet intersection(BigExBaseSet set);

	/**
	 * 指定の基底集合との差を返す。
	 * <br>
	 * 集合の差は、このインスタンスの基底集合から、指定された基底集合を除いた集合となる。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 差し引く基底の集合
	 * @return 指定された基底集合を差し引いた結果となるインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExBaseSet difference(ExBaseSet set);

	/**
	 * 指定の基底集合との差を返す。
	 * <br>
	 * 集合の差は、このインスタンスの基底集合から、指定された基底集合を除いた集合となる。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set 差し引く基底の集合
	 * @return 指定された基底集合を差し引いた結果となるインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExBaseSet difference(BigExBaseSet set);

	/**
	 * この基底集合から、ハットなし基底のみを取り出す。
	 * 
	 * @return	この集合に含まれるハットなし基底の集合
	 */
	public BigExBaseSet getNoHatBases();

	/**
	 * この基底集合から、ハット基底のみを取り出す。
	 * 
	 * @return	この集合に含まれるハット基底の集合
	 */
	public BigExBaseSet getHatBases();

	/**
	 * 指定の基底パターンに一致する基底の集合を取得する。
	 * <br>
	 * 指定された基底のハットキー以外の基底キーに一致する基底のみを
	 * 取得し、その集合を返す。ハットキー以外の基底キーにアスタリスク
	 * 文字<code>('*')</code>が含まれている場合、0文字以上の任意の文字に
	 * マッチするワイルドカードとみなす。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空の基底集合を返す。
	 * 
	 * @param base	基底パターンとみなす交換代数基底
	 * @return		パターンに一致した基底の集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExBaseSet getMatchedBases(ExBase base);

	/**
	 * 指定の基底パターンに一致する基底の集合を取得する。
	 * <br>
	 * 指定された基底パターンに一致する基底のみを取得し、その
	 * 集合を返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空の基底集合を返す。
	 * 
	 * @param pattern	基底パターン
	 * @return			パターンに一致した基底の集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExBaseSet getMatchedBases(ExBasePattern pattern);

	/**
	 * 指定の集合の基底パターンに一致する基底の集合を取得する。
	 * <br>
	 * 指定された集合に含まれる基底の、ハットキー以外の基底キーに
	 * 一致する基底のみを取得し、その集合を返す。ハットキー以外の
	 * 基底キーにアスタリスク文字<code>('*')</code>が含まれている場合、
	 * 0文字以上の任意の文字にマッチするワイルドカードとみなす。
	 * <br>
	 * 返される基底集合に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の基底集合を返す。
	 * 
	 * @param bases	基底パターンとみなす基底の集合
	 * @return		パターンに一致した基底の集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExBaseSet getMatchedBases(ExBaseSet bases);

	// TODO: 将来的に大容量化に対応が必要かは、要検討
	/**
	 * 指定の集合の基底パターンに一致する基底の集合を取得する。
	 * <br>
	 * 指定された集合に含まれる基底の、ハットキー以外の基底キーに
	 * 一致する基底のみを取得し、その集合を返す。ハットキー以外の
	 * 基底キーにアスタリスク文字<code>('*')</code>が含まれている場合、
	 * 0文字以上の任意の文字にマッチするワイルドカードとみなす。
	 * <br>
	 * 返される基底集合に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の基底集合を返す。
	 * <p><b>注意</b>
	 * <blockquote>
	 * このメソッドは、指定された交換代数基底集合から、交換代数基底パターン集合を「メモリ」上に生成する。
	 * そのため、基底集合の量によってはメモリ不足が発生する恐れがあるので、注意すること。
	 * </blockquote>
	 * 
	 * @param bases	基底パターンとみなす基底の集合
	 * @return		パターンに一致した基底の集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExBaseSet getMatchedBases(BigExBaseSet bases);

	/**
	 * 指定の集合の基底パターンに一致する基底の集合を取得する。
	 * <br>
	 * 指定された集合に含まれる基底パターンに一致する基底のみを取得し、
	 * その集合を返す。
	 * <br>
	 * 返される基底集合に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の基底集合を返す。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return			パターンに一致した基底の集合
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExBaseSet getMatchedBases(ExBasePatternSet patterns);
	
	/**
	 * 基底集合に含まれる全てのハットなし基底をハット基底に置き換えた、
	 * 新しい基底集合を返す。このメソッドが返す基底集合に含まれる基底は、
	 * 全てハット基底となる。
	 * <p>このメソッドは、基底のハットキーを {@link exalge2.ExBase#HAT} にした
	 * 基底に置き換える。ハットキーがすでに {@link exalge2.ExBase#HAT} のものは、
	 * そのまま格納される。
	 * 
	 * @return	すべての基底にハット(^)を付加した、新しい基底集合
	 */
	public BigExBaseSet setHat();
	
	/**
	 * 基底集合に含まれる全てのハット基底をハットなし基底に置き換えた、
	 * 新しい基底集合を返す。このメソッドが返す基底集合に含まれる基底は、
	 * 全てハットなし基底となる。
	 * <p>このメソッドは、基底のハットキーを {@link exalge2.ExBase#NO_HAT} にした
	 * 基底に置き換える。ハットキーがすでに {@link exalge2.ExBase#NO_HAT} のものは、
	 * そのまま格納される。
	 * 
	 * @return	すべての基底のハット(^)を除去した、新しい基底集合
	 */
	public BigExBaseSet removeHat();

	/**
	 * この基底集合にすべての基底について、ハットありとハットなしの両方を含む基底集合を生成する。
	 * @return	この集合に含まれるすべての基底についてハットありとハットなしの基底を含む新しい基底集合
	 */
	public BigExBaseSet generalBases();
}
