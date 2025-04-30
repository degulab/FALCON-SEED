/*
 * @(#)BigExalgeSet.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.db;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import exalge2.ExAlgeSet;
import exalge2.ExBase;
import exalge2.ExBasePattern;
import exalge2.ExBasePatternSet;
import exalge2.ExBaseSet;
import exalge2.Exalge;
import redundantalge.db.BigIterator;

/**
 * 大容量の交換代数集合を表すオブジェクトのインタフェース。
 * 
 * @version 0.990
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface BigExAlgeSet<TElem extends BigExalge> extends Iterable<TElem>
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
	 * 指定された交換代数元が含まれているかを判定する。
	 * @param alge	判定する交換代数元
	 * @return	含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean contains(Exalge alge);

	/**
	 * 指定された交換代数元が含まれているかを判定する。
	 * @param alge	判定する交換代数元
	 * @return	含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean contains(BigExalge alge);

	/**
	 * 指定されたコレクションに含まれる要素のすべてが、自身に含まれているかどうかを判定する。
	 * @param c	判定するコレクション
	 * @return	すべての要素が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsAll(Collection<? extends Exalge> c);

	/**
	 * 指定されたコレクションに含まれる要素のすべてが、自身に含まれているかどうかを判定する。
	 * @param set	判定するコレクション
	 * @return	すべての要素が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsAll(BigExAlgeSet<? extends BigExalge> set);
	
	/**
	 * このオブジェクトの交換代数元に含まれる交換代数要素にアクセスするイテレーターを取得する。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * @return	このオブジェクトに含まれる交換代数要素のイテレーター
	 */
	public BigIterator<BigExAlgeSetInnerElement> innerElementIterator();

	/**
	 * このオブジェクト交換代数基底にアクセスするイテレーターを取得する。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * @return	このオブジェクトに含まれる交換代数基底のイテレーター
	 */
	public BigIterator<ExBase> exbaseIterator();
	
	/**
	 * 交換代数集合の交換代数元の単位でのイテレーターを返す。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * @return	交換代数元のイテレーターオブジェクト
	 * @see ConcurrentModificationException
	 */
	public Iterator<TElem> iterator();

	/**
	 * この集合から、すべての要素を削除する。
	 */
	public void clear();

	/**
	 * 指定された交換代数元を、この集合に追加する。
	 * @param alge	追加する交換代数元
	 * @return	追加された場合は <tt>true</tt>、すでに存在する場合は <tt>false</tt>
	 */
	public boolean add(Exalge alge);
	
	/**
	 * 指定された交換代数元を、この集合に追加する。
	 * @param alge	追加する交換代数元
	 * @return	追加された場合は <tt>true</tt>、すでに存在する場合は <tt>false</tt>
	 */
	public boolean add(BigExalge alge);

	/**
	 * 指定された交換代数元を、この集合から削除する。
	 * @param alge	削除する交換代数元
	 * @return	削除された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean remove(Exalge alge);
	
	/**
	 * 指定された交換代数元を、この集合から削除する。
	 * @param alge	削除する交換代数元
	 * @return	削除された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean remove(BigExalge alge);

	/**
	 * 指定されたコレクションに含まれるすべての交換代数元を、この集合に追加する。
	 * @param c	追加する交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean addAll(Collection<? extends Exalge> c);
	
	/**
	 * 指定されたコレクションに含まれるすべての交換代数元を、この集合に追加する。
	 * @param set	追加する交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean addAll(BigExAlgeSet<? extends BigExalge> set);

	/**
	 * 指定されたコレクションに含まれるすべての交換代数元を、この集合から削除する。
	 * @param c	削除する交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean removeAll(Collection<? extends Exalge> c);
	
	/**
	 * 指定されたコレクションに含まれるすべての交換代数元を、この集合から削除する。
	 * @param set	削除する交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean removeAll(BigExAlgeSet<? extends BigExalge> set);

	/**
	 * 指定されたコレクションに含まれるすべての交換代数元のみを残し、その他の交換代数元をこの集合から削除する。
	 * @param c	残す交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean retainAll(Collection<? extends Exalge> c);
	
	/**
	 * 指定されたコレクションに含まれるすべての交換代数元のみを残し、その他の交換代数元をこの集合から削除する。
	 * @param set	残す交換代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean retainAll(BigExAlgeSet<? extends BigExalge> set);

	/**
	 * このオブジェクトの複製を生成する。
	 * 
	 * @return 複製された <code>BigExAlgeSet</code> オブジェクト
	 */
	public BigExAlgeSet<TElem> copy();

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
	 */
	public BigExAlgeSet<TElem> addition(ExAlgeSet set);
	
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
	 */
	public BigExAlgeSet<TElem> addition(BigExAlgeSet<? extends BigExalge> set);

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
	 */
	public BigExAlgeSet<TElem> subtraction(ExAlgeSet set);
	
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
	 */
	public BigExAlgeSet<TElem> subtraction(BigExAlgeSet<? extends BigExalge> set);
	
	/**
	 * 交換代数に含まれる全ての基底を取り出す。
	 * <br>
	 * このメソッドが返す交換代数基底集合に、基底の重複はない。
	 * 
	 * @return 交換代数から取り出した基底集合
	 */
	public BigExBaseSet getBases();

	/**
	 * この交換代数集合に含まれる基底のうち、ハットなし基底のみを取り出す。
	 * 
	 * @return	この交換代数集合に含まれるハットなし基底の集合
	 */
	public BigExBaseSet getNoHatBases();

	/**
	 * この交換代数集合に含まれる基底のうち、ハット基底のみを取り出す。
	 * 
	 * @return	この交換代数集合に含まれるハット基底の集合
	 */
	public BigExBaseSet getHatBases();

	/**
	 * この交換代数集合から、全ての基底についてハットを除去した
	 * 基底集合を取得する。
	 * 
	 * @return	この交換代数集合に含まれる全基底のハット除去後の基底集合
	 */
	public BigExBaseSet getBasesWithRemoveHat();

	/**
	 * この交換代数集合から、全ての基底についてハットを付加した
	 * 基底集合を取得する。
	 * 
	 * @return	この交換代数集合に含まれる全基底のハット付加後の基底集合
	 */
	public BigExBaseSet getBasesWithSetHat();

	/**
	 * 自身に含まれる全ての交換代数の総和を計算した結果を取得する
	 * 
	 * @return 計算結果の交換代数
	 */
	public BigExalge sum();
	
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
	 */
	public BigExAlgeSet<TElem> oneValueProjection(BigDecimal value);
	
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
	 */
	public BigExAlgeSet<TElem> valuesProjection(Collection<? extends BigDecimal> values);
	
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
	 */
	public BigExAlgeSet<TElem> nullProjection();
	
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
	 */
	public BigExAlgeSet<TElem> nonullProjection();
	
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
	 */
	public BigExAlgeSet<TElem> zeroProjection();
	
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
	 */
	public BigExAlgeSet<TElem> notzeroProjection();

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
	 */
	public BigExAlgeSet<TElem> projection(ExBase base);

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
	public BigExAlgeSet<TElem> projection(ExBaseSet baseset);
	
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
	public BigExAlgeSet<TElem> projection(BigExBaseSet baseset);
	
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
	 */
	public BigExAlgeSet<TElem> generalProjection(ExBase base);
	
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
	 */
	public BigExAlgeSet<TElem> generalProjection(ExBaseSet baseset);
	
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
	 */
	public BigExAlgeSet<TElem> generalProjection(BigExBaseSet baseset);

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
	 * @see exalge2.Exalge#patternProjection(ExBase)
	 */
	public BigExAlgeSet<TElem> patternProjection(ExBase base);

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
	 * @see exalge2.Exalge#patternProjection(ExBasePattern)
	 */
	public BigExAlgeSet<TElem> patternProjection(ExBasePattern pattern);

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
	 * @see exalge2.Exalge#patternProjection(ExBaseSet)
	 */
	public BigExAlgeSet<TElem> patternProjection(ExBaseSet bases);

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
	 * @see exalge2.Exalge#patternProjection(ExBaseSet)
	 */
	public BigExAlgeSet<TElem> patternProjection(BigExBaseSet bases);

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
	 * @see exalge2.Exalge#patternProjection(ExBasePatternSet)
	 */
	public BigExAlgeSet<TElem> patternProjection(ExBasePatternSet patterns);
}
