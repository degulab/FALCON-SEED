/*
 * @(#)BigDtAlgeSet.java	0.5.0	2019/02/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import dtalge.DtAlgeSet;
import dtalge.DtBase;
import dtalge.DtBasePattern;
import dtalge.DtBasePatternSet;
import dtalge.DtBaseSet;
import dtalge.Dtalge;
import dtalge.IDtStringThesaurus;
import dtalge.exception.CsvFormatException;
import redundantalge.db.BigIterator;
import redundantalge.db.mongo.MongoAlgeError;

/**
 * 大容量のデータ代数集合を表すオブジェクトのインタフェース。
 * 基本的に {@link dtalge.DtAlgeSet} と同様のインタフェースを提供する。
 * ただし、順序については不定である。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 */
public interface BigDtAlgeSet<TElem extends BigDtalge> extends Iterable<TElem>
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
	 * 指定されたデータ代数元が含まれているかを判定する。
	 * @param alge	判定するデータ代数元
	 * @return	含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean contains(Dtalge alge);

	/**
	 * 指定されたデータ代数元が含まれているかを判定する。
	 * @param alge	判定するデータ代数元
	 * @return	含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean contains(BigDtalge alge);

	/**
	 * 指定されたコレクションに含まれる要素のすべてが、自身に含まれているかどうかを判定する。
	 * @param c	判定するコレクション
	 * @return	すべての要素が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsAll(Collection<? extends Dtalge> c);

	/**
	 * 指定されたコレクションに含まれる要素のすべてが、自身に含まれているかどうかを判定する。
	 * @param set	判定するコレクション
	 * @return	すべての要素が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsAll(BigDtAlgeSet<? extends BigDtalge> set);
	
	/**
	 * このオブジェクトのデータ代数元に含まれるデータ代数要素にアクセスするイテレーターを取得する。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * @return	このオブジェクトに含まれるデータ代数要素のイテレーター
	 */
	public BigIterator<BigDtAlgeSetInnerElement> innerElementIterator();
	
	/**
	 * このオブジェクトのデータ代数元に含まれるデータ代数要素にアクセスするイテレーターを取得する。
	 * 要素が返されるときの順序は、データ代数元要素の格納順、基底、値の順に昇順ソートされた順序となる。
	 * @return	このオブジェクトに含まれるデータ代数要素のイテレーター
	 */
	public BigIterator<BigDtAlgeSetInnerElement> sortedInnerElementIterator();

	/**
	 * このオブジェクトデータ代数基底にアクセスするイテレーターを取得する。
	 * このイテレーターが返す順序は、基底キーの順に昇順ソートされたものとなる。
	 * @return	このオブジェクトに含まれるデータ代数基底のイテレーター
	 */
	public BigIterator<DtBase> dtbaseIterator();
	
	/**
	 * データ代数集合のデータ代数元の単位でのイテレーターを返す。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * @return	データ代数元のイテレーターオブジェクト
	 * @see ConcurrentModificationException
	 */
	public Iterator<TElem> iterator();

	/**
	 * この集合から、すべての要素を削除する。
	 */
	public void clear();

	/**
	 * 指定されたデータ代数元を、この集合に追加する。
	 * @param alge	追加するデータ代数元
	 * @return	追加された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean add(Dtalge alge);
	
	/**
	 * 指定されたデータ代数元を、この集合に追加する。
	 * @param alge	追加するデータ代数元
	 * @return	追加された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean add(BigDtalge alge);

	/**
	 * 指定されたデータ代数元を、この集合から削除する。
	 * @param alge	削除するデータ代数元
	 * @return	削除された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean remove(Dtalge alge);
	
	/**
	 * 指定されたデータ代数元を、この集合から削除する。
	 * @param alge	削除するデータ代数元
	 * @return	削除された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean remove(BigDtalge alge);

	/**
	 * 指定されたコレクションに含まれるすべてのデータ代数元を、この集合に追加する。
	 * @param c	追加するデータ代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean addAll(Collection<? extends Dtalge> c);
	
	/**
	 * 指定されたコレクションに含まれるすべてのデータ代数元を、この集合に追加する。
	 * @param set	追加するデータ代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean addAll(BigDtAlgeSet<? extends BigDtalge> set);

	/**
	 * 指定されたコレクションに含まれるすべてのデータ代数元を、この集合から削除する。
	 * @param c	削除するデータ代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean removeAll(Collection<? extends Dtalge> c);
	
	/**
	 * 指定されたコレクションに含まれるすべてのデータ代数元を、この集合から削除する。
	 * @param set	削除するデータ代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean removeAll(BigDtAlgeSet<? extends BigDtalge> set);

	/**
	 * 指定されたコレクションに含まれるすべてのデータ代数元のみを残し、その他のデータ代数元をこの集合から削除する。
	 * @param c	残すデータ代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean retainAll(Collection<? extends Dtalge> c);
	
	/**
	 * 指定されたコレクションに含まれるすべてのデータ代数元のみを残し、その他のデータ代数元をこの集合から削除する。
	 * @param set	残すデータ代数元のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean retainAll(BigDtAlgeSet<? extends BigDtalge> set);

	/**
	 * このオブジェクトの複製を生成する。
	 * 
	 * @return 複製された <code>BigDtAlgeSet</code> オブジェクト
	 */
	public BigDtAlgeSet<TElem> copy();

	/**
	 * データ代数集合を連結した、新しいデータ代数集合のインスタンスを返す。
	 * <br>
	 * 新しいデータ代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)の末尾に、引数で指定された集合の要素(元)を追加した
	 * 集合となる。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結するデータ代数集合
	 * @return		自身と引数に指定された集合を連結した新しいデータ代数集合
	 */
	public BigDtAlgeSet<TElem> addition(DtAlgeSet set);
	
	/**
	 * データ代数集合を連結した、新しいデータ代数集合のインスタンスを返す。
	 * <br>
	 * 新しいデータ代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)の末尾に、引数で指定された集合の要素(元)を追加した
	 * 集合となる。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結するデータ代数集合
	 * @return		自身と引数に指定された集合を連結した新しいデータ代数集合
	 */
	public BigDtAlgeSet<TElem> addition(BigDtAlgeSet<? extends BigDtalge> set);

	/**
	 * 引数に指定されたデータ代数集合の要素を除いた、新しいデータ代数集合の
	 * インスタンスを返す。
	 * <br>
	 * 新しいデータ代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)のうち、引数で指定された集合の要素(元)を除いた集合となる。
	 * 同一の要素とみなすのは、要素の {@link dtalge.db.mongo.MongoDtalge#equals(Object)}
	 * メソッドが <tt>true</tt> を返す場合とする。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つデータ代数集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しいデータ代数集合
	 */
	public BigDtAlgeSet<TElem> subtraction(DtAlgeSet set);
	
	/**
	 * 引数に指定されたデータ代数集合の要素を除いた、新しいデータ代数集合の
	 * インスタンスを返す。
	 * <br>
	 * 新しいデータ代数集合のインスタンスには、自身のインスタンスに含まれる
	 * 要素(元)のうち、引数で指定された集合の要素(元)を除いた集合となる。
	 * 同一の要素とみなすのは、要素の {@link dtalge.db.mongo.MongoDtalge#equals(Object)}
	 * メソッドが <tt>true</tt> を返す場合とする。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つデータ代数集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しいデータ代数集合
	 */
	public BigDtAlgeSet<TElem> subtraction(BigDtAlgeSet<? extends BigDtalge> set);
	
	/**
	 * データ代数に含まれる全ての基底を取り出す。
	 * <br>
	 * このメソッドが返すデータ代数基底集合に、基底の重複はない。
	 * 
	 * @return データ代数から取り出した基底集合
	 */
	public BigDtBaseSet getBases();
	
	/**
	 * このデータ代数集合に含まれる基底のうち、指定されたパターンに一致する
	 * 基底のみを格納する基底集合を返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは空の基底集合を返す。
	 * 
	 * @param pattern	基底パターン
	 * @return	パターンに一致した基底のみの集合
	 * 
	 * @throws NullPointerException	指定された基底パターンが <tt>null</tt> の場合
	 */
	public BigDtBaseSet getMatchedBases(DtBasePattern pattern);
	
	/**
	 * このデータ代数集合に含まれる基底のうち、指定されたパターンに一致する
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
	public BigDtBaseSet getMatchedBases(DtBasePatternSet patterns);

	/**
	 * 自身に含まれるデータ代数元をすべて結合する。<br>
	 * このメソッドでは、全てのデータ代数元を、一つのデータ代数元に結合する。
	 * 同じ基底が異なるデータ代数元に存在する場合、内部的な結合の順序で上書きされる。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * この結合では、同じ基底の値は上書きされる。数値的な加算等の演算は行わない。
	 * </blockquote>
	 * 
	 * @return 結合結果の、新しいデータ代数元
	 */
	public BigDtalge sum();

	/**
	 * 指定された基底に割り当てられた全ての値が格納された新しい文字列リストを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、取得した要素をメモリ上のリストとして取得するため、大容量のデータを扱う場合にメモリ不足が発生する可能性がある。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての値を格納する文字列リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の文字列リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が文字列型ではない場合
	 */
	public List<String> toStringList(DtBase base);
	
	/**
	 * 指定された基底に割り当てられた全ての重複しない(同値ではない)値が格納された新しい文字列リストを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、取得した要素をメモリ上のリストとして取得するため、大容量のデータを扱う場合にメモリ不足が発生する可能性がある。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての重複しない(同値ではない)値を格納する文字列リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の文字列リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が文字列型ではない場合
	 */
	public List<String> toDistinctStringList(DtBase base);
	
	/**
	 * 指定された基底に割り当てられた全ての値が格納された新しい実数値リストを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、取得した要素をメモリ上のリストとして取得するため、大容量のデータを扱う場合にメモリ不足が発生する可能性がある。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての値を格納する実数値リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の実数値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が実数型ではない場合
	 */
	public List<BigDecimal> toDecimalList(DtBase base);
	
	/**
	 * 指定された基底に割り当てられた全ての重複しない(同値ではない)値が格納された新しい実数値リストを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、取得した要素をメモリ上のリストとして取得するため、大容量のデータを扱う場合にメモリ不足が発生する可能性がある。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての重複しない(同値ではない)値を格納する実数値リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の実数値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が実数型ではない場合
	 */
	public List<BigDecimal> toDistinctDecimalList(DtBase base);
	
	/**
	 * 指定された基底に割り当てられた全ての値が格納された新しい真偽値リストを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、取得した要素をメモリ上のリストとして取得するため、大容量のデータを扱う場合にメモリ不足が発生する可能性がある。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての値を格納する真偽値リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の真偽値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が真偽値型ではない場合
	 */
	public List<Boolean> toBooleanList(DtBase base);
	
	/**
	 * 指定された基底に割り当てられた全ての重複しない(同値ではない)値が格納された新しい真偽値リストを返す。
	 * @param base	データ代数基底
	 * @return	基底に割り当てられた全ての重複しない(同値ではない)値を格納する真偽値リストを返す。
	 * 			基底に割り当てられた値が存在しない場合は、要素が空の真偽値リストを返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が真偽値型ではない場合
	 */
	public List<Boolean> toDistinctBooleanList(DtBase base);

	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最小値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最小値を返す。
	 * 			最小値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public Object minValue(DtBase base);
	
	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最大値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最大値を返す。
	 * 			最大値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public Object maxValue(DtBase base);
	
	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最小の実数値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * 指定された基底が実数型のデータ型ではない場合、このメソッドは例外をスローする。
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最小値を返す。
	 * 			最小値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が実数型ではない場合
	 */
	public BigDecimal minDecimal(DtBase base);
	
	/**
	 * 指定された基底に割り当てられている値のうち、<tt>null</tt> ではない最大の実数値を取得する。
	 * 比較不可能な値の場合や、指定された基底をもつデータ代数元が一つも存在しない場合、
	 * このメソッドは <tt>null</tt> を返す。
	 * 指定された基底が実数型のデータ型ではない場合、このメソッドは例外をスローする。
	 * @param base	比較する値のデータ代数基底
	 * @return	指定された基底に割り当てられている最大値を返す。
	 * 			最大値が取得できない場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	基底のデータ型が実数型ではない場合
	 */
	public BigDecimal maxDecimal(DtBase base);

	/**
	 * 指定された基底に、指定された値が関連付けられているデータ代数元が存在するかを判定する。
	 * <em>base</em> に <tt>null</tt> が指定された場合、このメソッドは <tt>false</tt> を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsValue(DtBase base, Object value);

	/**
	 * 指定された基底に、指定されたコレクションに含まれる値のどれか一つが関連付けられている
	 * データ代数元が存在するかを判定する。
	 * <em>base</em> に <tt>null</tt> が指定された場合、このメソッドは <tt>false</tt> を返す。
	 * @param base		データ代数基底
	 * @param values	判定する値を格納するコレクション
	 * @return			存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsAnyValues(DtBase base, Collection<?> values);

	/**
	 * 自身に含まれる全てのデータ代数元に対し、
	 * {@link dtalge.db.BigDtalge#oneValueProjection(Object)} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link dtalge.db.BigDtalge#oneValueProjection(Object)} した結果となる
	 * データ代数元を持つ、新しいデータ代数集合を生成する。
	 * このとき、要素が空となるデータ代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @param value	取り出すようその値
	 * @return	指定された値と等しい要素のみを含むデータ代数元の集合
	 * 
	 * @see dtalge.db.BigDtalge#oneValueProjection(Object)
	 */
	public BigDtAlgeSet<TElem> oneValueProjection(Object value);

	/**
	 * 自身に含まれる全てのデータ代数元に対し、
	 * {@link dtalge.db.BigDtalge#valuesProjection(Collection)} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link dtalge.db.BigDtalge#valuesProjection(Collection)} した結果となる
	 * データ代数元を持つ、新しいデータ代数集合を生成する。
	 * このとき、要素が空となるデータ代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @param values	取り出す要素の値のコレクション
	 * @return	指定されたコレクションに含まれる値と等しい要素のみを含む
	 * 			データ代数元の集合
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * 
	 * @see dtalge.db.BigDtalge#valuesProjection(Collection)
	 */
	public BigDtAlgeSet<TElem> valuesProjection(Collection<?> values);

	/**
	 * 自身に含まれる全てのデータ代数元に対し、
	 * {@link dtalge.db.BigDtalge#nullProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link dtalge.db.BigDtalge#nullProjection()} した結果となる
	 * データ代数元を持つ、新しいデータ代数集合を生成する。
	 * このとき、要素が空となるデータ代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が <tt>null</tt> の要素のみを含むデータ代数元の集合
	 * 
	 * @see dtalge.db.BigDtalge#nullProjection()
	 */
	public BigDtAlgeSet<TElem> nullProjection();
	
	/**
	 * 自身に含まれる全てのデータ代数元に対し、
	 * {@link dtalge.db.BigDtalge#nonullProjection()} した結果を保持する集合を返す。
	 * <br>
	 * このメソッドでは、全要素について {@link dtalge.db.BigDtalge#nonullProjection()} した結果となる
	 * データ代数元を持つ、新しいデータ代数集合を生成する。
	 * このとき、要素が空となるデータ代数元は除外される。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * @return	値が <tt>null</tt> ではない要素のみを含むデータ代数元の集合
	 * 
	 * @see dtalge.db.BigDtalge#nonullProjection()
	 */
	public BigDtAlgeSet<TElem> nonullProjection();

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底で
	 * プロジェクションした結果を保持する集合を返す。
	 * <p>このメソッドは、この集合に含まれる全要素についてプロジェクション
	 * した結果となるデータ代数元を格納する、新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param base	取り出すデータ代数基底
	 * @return	取り出された要素を持つデータ代数元の集合
	 * 
	 * @throws NullPointerException 指定された基底が <tt>null</tt> の場合
	 * 
	 * @see dtalge.db.BigDtalge#projection(DtBase)
	 */
	public BigDtAlgeSet<TElem> projection(DtBase base);

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底集合で
	 * プロジェクションした結果を保持する集合を返す。
	 * <p>このメソッドは、この集合に含まれる全要素についてプロジェクション
	 * した結果となるデータ代数元を格納する、新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param bases	取り出すデータ代数基底の集合
	 * @return	取り出された要素を持つデータ代数元の集合
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 * 
	 * @see dtalge.db.BigDtalge#projection(DtBaseSet)
	 */
	public BigDtAlgeSet<TElem> projection(DtBaseSet bases);

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底集合で
	 * プロジェクションした結果を保持する集合を返す。
	 * <p>このメソッドは、この集合に含まれる全要素についてプロジェクション
	 * した結果となるデータ代数元を格納する、新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param bases	取り出すデータ代数基底の集合
	 * @return	取り出された要素を持つデータ代数元の集合
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 * 
	 * @see dtalge.db.BigDtalge#projection(BigDtBaseSet)
	 */
	public BigDtAlgeSet<TElem> projection(BigDtBaseSet bases);

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底パターンに
	 * よってプロジェクションした結果を保持する集合を返す。
	 * <p>
	 * このメソッドは、この集合に含まれる全要素について、指定された基底パターンに
	 * 一致する基底のみでプロジェクションした結果となるデータ代数元を格納する、
	 * 新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param pattern	基底パターン
	 * @return	基底パターンでプロジェクションした結果となるデータ代数元の集合
	 * 
	 * @throws	NullPointerException	指定された基底パターンが <tt>null</tt> の場合
	 * 
	 * @see dtalge.db.BigDtalge#patternProjection(DtBasePattern)
	 */
	public BigDtAlgeSet<TElem> patternProjection(DtBasePattern pattern);

	/**
	 * この集合に含まれる全てのデータ代数元に対し、指定された基底パターンに
	 * よってプロジェクションした結果を保持する集合を返す。
	 * <p>
	 * このメソッドは、この集合に含まれる全要素について、指定された基底パターン
	 * のいずれかに一致する基底のみでプロジェクションした結果となるデータ代数元を
	 * 格納する、新しい集合を生成する。
	 * プロジェクションの結果、要素を持たないデータ代数元は除外される。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return	基底パターンでプロジェクションした結果となるデータ代数元の集合
	 * 
	 * @throws NullPointerException	指定された基底パターン集合が <tt>null</tt> の場合
	 * 
	 * @see dtalge.db.BigDtalge#patternProjection(DtBasePatternSet)
	 */
	public BigDtAlgeSet<TElem> patternProjection(DtBasePatternSet patterns);

	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含むデータ代数元のみを取り出す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	public BigDtAlgeSet<TElem> selectEqualValue(DtBase base, Object value);
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含むデータ代数元以外の
	 * データ代数元を全て取り出す。このメソッドが返すデータ代数集合には、指定された基底が存在しない
	 * データ代数元も含まれる。
	 * <p>この動作は、{@link #removeEqualValue(DtBase, Object)} と同じ。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @see #removeEqualValue(DtBase, Object)
	 */
	public BigDtAlgeSet<TElem> selectNotEqualValue(DtBase base, Object value);
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値よりも小さい要素を含むデータ代数元のみを取り出す。
	 * 指定された値と比較不可能な要素は、結果には含まれない。また、<em>value</em> に
	 * <tt>null</tt> を指定した場合、このメソッドは要素が空のデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	public BigDtAlgeSet<TElem> selectLessThanValue(DtBase base, Object value);
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値よりも小さい、もしくは等しい要素を含む
	 * データ代数元のみを取り出す。
	 * 指定された値と比較不可能な要素は、結果には含まれない。また、<em>value</em> に
	 * <tt>null</tt> を指定した場合、このメソッドは要素が空のデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	public BigDtAlgeSet<TElem> selectLessEqualValue(DtBase base, Object value);
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値よりも大きい要素を含むデータ代数元のみを取り出す。
	 * 指定された値と比較不可能な要素は、結果には含まれない。また、<em>value</em> に
	 * <tt>null</tt> を指定した場合、このメソッドは要素が空のデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	public BigDtAlgeSet<TElem> selectGreaterThanValue(DtBase base, Object value);
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値よりも大きい、もしくは等しい要素を含む
	 * データ代数元のみを取り出す。
	 * 指定された値と比較不可能な要素は、結果には含まれない。また、<em>value</em> に
	 * <tt>null</tt> を指定した場合、このメソッドは要素が空のデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元のみを格納する、新しいデータ代数集合を返す。
	 * 			条件に一致するデータ代数元が一つも存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	public BigDtAlgeSet<TElem> selectGreaterEqualValue(DtBase base, Object value);
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を、
	 * 指定されたデータ代数元で置き換えられた新しいデータ代数集合を返す。
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @param newAlge	新しいデータ代数元
	 * @return	条件に一致したデータ代数元が置き換えられた、新しいデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em>、もしくは <em>newAlge</em> が <tt>null</tt> の場合
	 */
	public BigDtAlgeSet<TElem> replaceEqualValue(DtBase base, Object value, Dtalge newAlge);
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を、
	 * 指定されたデータ代数元で置き換えられた新しいデータ代数集合を返す。
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @param newAlge	新しいデータ代数元
	 * @return	条件に一致したデータ代数元が置き換えられた、新しいデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em>、もしくは <em>newAlge</em> が <tt>null</tt> の場合
	 */
	public BigDtAlgeSet<TElem> replaceEqualValue(DtBase base, Object value, BigDtalge newAlge);
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を、
	 * 指定されたデータ代数元で置き換える。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、自身の内容を書き換える、破壊型メソッドである。
	 * </blockquote>
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @param newAlge	新しいデータ代数元
	 * @return	自身の内容が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>base</em>、もしくは <em>newAlge</em> が <tt>null</tt> の場合
	 */
	public boolean updateEqualValue(DtBase base, Object value, Dtalge newAlge);
	
	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を、
	 * 指定されたデータ代数元で置き換える。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、自身の内容を書き換える、破壊型メソッドである。
	 * </blockquote>
	 * @param base		データ代数基底
	 * @param value		判定する値
	 * @param newAlge	新しいデータ代数元
	 * @return	自身の内容が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>base</em>、もしくは <em>newAlge</em> が <tt>null</tt> の場合
	 */
	public boolean updateEqualValue(DtBase base, Object value, BigDtalge newAlge);

	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を
	 * 取り除いた、新しいデータ代数集合を返す。
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	条件に一致したデータ代数元を含まない、新しいデータ代数集合を返す。
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	public BigDtAlgeSet<TElem> removeEqualValue(DtBase base, Object value);

	/**
	 * 指定された基底に関連付けられた値が、指定された値と等しい要素を含む全てのデータ代数元を
	 * 削除する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、自身の内容を書き換える、破壊型メソッドである。
	 * </blockquote>
	 * @param base	データ代数基底
	 * @param value	判定する値
	 * @return	自身の内容が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 */
	public boolean deleteEqualValue(DtBase base, Object value);
	
	/**
	 * 指定された基底に関連付けられている値のうち、指定されたシソーラス定義において
	 * 比較不可能な極大値のみを含むデータ代数元を取り出す。
	 * <p>このメソッドは、指定された基底に関連付けられている値をすべて比較し、
	 * シソーラス定義において比較可能な値の中から最大値のみを含むデータ代数元を取り出す。
	 * 取り出されたデータ代数元に含まれる値は、それぞれ比較不可能もしくは同値となっている。
	 * @param base	判定する値が関連付けられているデータ代数基底
	 * @param thes	シソーラス定義
	 * @return	比較不可能な極大値のみを含むデータ代数元を格納する、新しいデータ代数集合を返す。
	 * 			比較不可能な極大値が存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>base</em> が文字列型のデータ代数基底ではない場合
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public BigDtAlgeSet<TElem> selectThesaurusMax(DtBase base, IDtStringThesaurus thes);
	
	/**
	 * 指定された基底に関連付けられている値のうち、指定されたシソーラス定義において
	 * 比較不可能な極小値のみを含むデータ代数元を取り出す。
	 * <p>このメソッドは、指定された基底に関連付けられている値をすべて比較し、
	 * シソーラス定義において比較可能な値の中から最小値のみを含むデータ代数元を取り出す。
	 * 取り出されたデータ代数元に含まれる値は、それぞれ比較不可能もしくは同値となっている。
	 * @param base	判定する値が関連付けられているデータ代数基底
	 * @param thes	シソーラス定義
	 * @return	比較不可能な極小値のみを含むデータ代数元を格納する、新しいデータ代数集合を返す。
	 * 			比較不可能な極小値が存在しない場合は、要素が空のデータ代数集合を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>base</em> が文字列型のデータ代数基底ではない場合
	 */
	public BigDtAlgeSet<TElem> selectThesaurusMin(DtBase base, IDtStringThesaurus thes);
	
	//------------------------------------------------------------
	// Override
	//------------------------------------------------------------

	/**
	 * 指定された基底の値で要素が並べ替えられた、新しいデータ代数集合を返す。
	 * 値の並べ替えにおいて、等しい値の順序は変更されない。
	 * <tt>null</tt> 値はどの値よりも小さい値として並べ替えられる。
	 * また、基底が存在しない要素は、<tt>null</tt> 値よりも小さい値として並べ替えられる。
	 * @param base			並べ替えのキーとする値が関連付けられているデータ代数基底
	 * @param ascending		昇順にソートする場合は <tt>true</tt>、降順にソートする場合は <tt>false</tt>
	 * @return	要素が並べ替えられた、新しいデータ代数集合
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws ClassCastException		基底に関連付けられた値が(<code>Comparable</code> インタフェースを実装していない)比較不可能の場合
	 */
	public BigDtAlgeSet<TElem> sortedAlgesByValue(DtBase base, boolean ascending);

	/**
	 * 指定された基底の値で要素が並べ替えられた、新しいデータ代数集合を返す。
	 * 値の並べ替えにおいて、等しい値の順序は変更されない。
	 * <tt>null</tt> 値はどの値よりも小さい値として並べ替えられる。
	 * また、基底が存在しない要素は、<tt>null</tt> 値よりも小さい値として並べ替えられる。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドは、自身の内容を書き換える、破壊型メソッドである。
	 * </blockquote>
	 * @param base			並べ替えのキーとする値が関連付けられているデータ代数基底
	 * @param ascending		昇順にソートする場合は <tt>true</tt>、降順にソートする場合は <tt>false</tt>
	 * @throws NullPointerException	<em>base</em> が <tt>null</tt> の場合
	 * @throws ClassCastException		基底に関連付けられた値が(<code>Comparable</code> インタフェースを実装していない)比較不可能の場合
	 */
	public void sortAlgesByValue(DtBase base, boolean ascending);

	//------------------------------------------------------------
	// for I/O
	//------------------------------------------------------------
	
	/**
	 * データ代数集合の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void toTableCSV(File csvFile)
		throws IOException, FileNotFoundException;
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public void toTableCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException;
	
	/**
	 * データ代数集合の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力される。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力される。
	 * 順序が指定されていない場合は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void toTableCsvWithBaseOrder(DtBaseSet baseOrder, File csvFile)
		throws IOException, FileNotFoundException;
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力される。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力される。
	 * 順序が指定されていない場合は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public void toTableCsvWithBaseOrder(DtBaseSet baseOrder, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException;
	
	/**
	 * データ代数集合の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。また、基底に対応する値が <tt>null</tt> もしくは
	 * 空文字列の場合、その値と基底を削除してから出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void toTableCsvWithoutNull(File csvFile)
		throws IOException, FileNotFoundException;
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。また、基底に対応する値が <tt>null</tt> もしくは
	 * 空文字列の場合、その値と基底を削除してから出力する。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>出力時は、このインスタンスに格納されている基底の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public void toTableCsvWithoutNull(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException;
	
	/**
	 * データ代数集合の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力される。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力される。
	 * 順序が指定されていない場合は、このインスタンスに格納されている基底の順序で出力される。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力せず、
	 * フィールドは空欄となる。
	 * 
	 * @param orderedBases	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void toTableCSV(DtBaseSet orderedBases, boolean withoutNull, File csvFile)
		throws IOException, FileNotFoundException;
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
	 * <p>
	 * 基底のない要素のフィールドは空欄となる。
	 * <tt>null</tt> の場合は <tt>null</tt> を示す特殊値が出力される。
	 * 特殊記号で始まる値は、特殊記号でエスケープされる。
	 * <br>順序指定用基底集合が指定された場合、その基底集合の順序の通りに基底が出力される。
	 * 順序指定用基底集合に含まれない基底は、行の終端にオリジナルの順序で出力される。
	 * 順序が指定されていない場合は、このインスタンスに格納されている基底の順序で出力される。
	 * <br><em>withoutNull</em> に <tt>true</tt> を指定した場合、<tt>null</tt> 値は出力せず、
	 * フィールドは空欄となる。
	 * 
	 * @param orderedBases	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public void toTableCSV(DtBaseSet orderedBases, boolean withoutNull, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException;
	
	/**
	 * データ代数集合の内容を、指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている要素の順序で出力される。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void toCSV(File csvFile)
		throws IOException, FileNotFoundException;
	
	/**
	 * データ代数集合の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
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
	 */
	public void toCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException;

	/**
	 * CSV フォーマットのファイルを読み込み、このオブジェクトに追加する。
	 * @param csvFile	読み込む CSV ファイル
	 * @return	このデータ代数集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 */
	public boolean addAllFromCSV(File csvFile)
		throws IOException, FileNotFoundException, CsvFormatException;
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、このオブジェクトに追加する。
	 * @param csvFile	読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * @return	このデータ代数集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public boolean addAllFromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException;
}
