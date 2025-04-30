/*
 * @(#)BigDtalge.java	0.5.0	2019/02/24
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
import java.util.NoSuchElementException;

import dtalge.DtBase;
import dtalge.DtBasePattern;
import dtalge.DtBasePatternSet;
import dtalge.DtBaseSet;
import dtalge.DtStringThesaurus;
import dtalge.Dtalge;
import dtalge.db.mongo.MongoDtalge;
import dtalge.exception.CsvFormatException;
import dtalge.exception.DtBaseNotFoundException;
import dtalge.exception.IllegalValueOfDataTypeException;
import redundantalge.db.BigIterator;

/**
 * 大容量のデータ代数元を表すオブジェクトのインタフェース。
 * 基本的に {@link dtalge.Dtalge} と同様のインタフェースを提供する。
 * ただし、順序については不定である。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface BigDtalge
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
	 * データ代数元の要素が空であることを示す。
	 * 
	 * @return 要素が一つも存在しない場合に true を返す。
	 */
	public boolean isEmpty();
	
	/**
	 * データ代数元の要素数を返す。
	 * 
	 * @return	データ代数元の要素数
	 */
	public long getNumElements();

	/**
	 * 指定された基底が、このインスタンスの要素に含まれているかを示す。
	 * 
	 * @param base データ代数基底
	 * @return 指定の基底が要素に含まれている場合に <tt>true</tt> を返す。
	 */
	public boolean containsBase(DtBase base);

	/**
	 * 指定された基底が、このインスタンスの要素に全て含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるすべての基底が、この
	 * データ代数元の要素に存在した場合のみ <tt>true</tt> を返す。
	 * 指定された基底集合の要素が空の場合は、<tt>false</tt> を返す。
	 * 
	 * @param bases	検証するデータ代数基底の集合
	 * @return	すべての基底が含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean containsAllBases(DtBaseSet bases);

	/**
	 * 指定された基底が、このインスタンスの要素に全て含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるすべての基底が、この
	 * データ代数元の要素に存在した場合のみ <tt>true</tt> を返す。
	 * 指定された基底集合の要素が空の場合は、<tt>false</tt> を返す。
	 * 
	 * @param bases	検証するデータ代数基底の集合
	 * @return	すべての基底が含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean containsAllBases(BigDtBaseSet bases);

	/**
	 * 指定された基底のどれか 1 つが、このインスタンスの要素に含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるどれか 1 つが、この
	 * データ代数元の要素に存在した場合に <tt>true</tt> を返す。
	 * 指定された基底集合の要素が空の場合は、<tt>false</tt> を返す。
	 * 
	 * @param bases	検証するデータ代数基底の集合
	 * @return	指定した基底集合のどれか 1 つが含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean containsAnyBases(DtBaseSet bases);

	/**
	 * 指定された基底のどれか 1 つが、このインスタンスの要素に含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるどれか 1 つが、この
	 * データ代数元の要素に存在した場合に <tt>true</tt> を返す。
	 * 指定された基底集合の要素が空の場合は、<tt>false</tt> を返す。
	 * 
	 * @param bases	検証するデータ代数基底の集合
	 * @return	指定した基底集合のどれか 1 つが含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean containsAnyBases(BigDtBaseSet bases);

	/**
	 * 指定された値が、このインスタンスの要素に含まれているかを示す。
	 * 
	 * @param value	データ代数の値
	 * @return	指定の値が含まれている場合に <tt>true</tt> を返す。
	 */
	public boolean containsValue(Object value);

	/**
	 * このインスタンスの要素に <tt>null</tt> 値が含まれている場合に <tt>true</tt> を返す。
	 * @return	このインスタンスの要素に <tt>null</tt> 値が含まれている場合は <tt>true</tt>
	 */
	public boolean containsNull();

	/**
	 * 指定されたコレクションに含まれる全ての値が、このインスタンスの要素に
	 * 含まれている場合に <tt>true</tt> を返す。
	 * <em>values</em> が <tt>null</tt> もしくは空の場合は、<tt>false</tt> を返す。
	 * @param values	検証する値のコレクション
	 * @return	指定されたコレクションに含まれる全ての値が、このインスタンスの要素に含まれている場合は <tt>true</tt>
	 */
	public boolean containsAllValues(Collection<?> values);

	/**
	 * 指定されたコレクションに含まれる値のどれか 1 つが、
	 * このインスタンスの要素に含まれている場合に <tt>true</tt> を返す。
	 * <em>values</em> が <tt>null</tt> もしくは空の場合は、<tt>false</tt> を返す。
	 * @param values	検証する値のコレクション
	 * @return	指定されたコレクションに含まれる値のどれか 1 つが、このインスタンスの要素に含まれている場合は <tt>true</tt>
	 */
	public boolean containsAnyValues(Collection<?> values);
	
	/**
	 * このオブジェクトのデータ代数要素にアクセスする変更不可能なイテレーターを取得する。
	 * このイテレーターが返す順序は、このクラスのコレクションの実装に依存する。
	 * @return	このオブジェクトに含まれるデータ代数要素の変更不可能なイテレーター
	 */
	public BigIterator<BigDtalgeElement> elementIterator();
	
	/**
	 * このオブジェクトのデータ代数要素にアクセスする変更不可能なイテレーターを取得する。
	 * このイテレーターが返す順序は、基底キーと値の順に昇順ソートされたものとなる。
	 * @return	このオブジェクトに含まれるデータ代数要素の変更不可能なイテレーター
	 */
	public BigIterator<BigDtalgeElement> sortedElementIterator();

	/**
	 * このオブジェクトデータ代数基底にアクセスする変更不可能なイテレーターを取得する。
	 * このイテレーターが返す順序は、基底キーの順に昇順ソートされたものとなる。
	 * @return	このオブジェクトに含まれるデータ代数基底の変更不可能なイテレーター
	 */
	public BigIterator<DtBase> dtbaseIterator();

	/**
	 * <code>Dtalge</code> の要素の変更不可能な反復子を返す。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * 
	 * @return データ代数要素の <code>Iterator</code>
	 * 
	 * @see ConcurrentModificationException
	 */
	public BigIterator<Dtalge> iterator();

	/**
	 * このデータ代数元の先頭に位置する要素の基底を取得する。
	 * 要素のコレクションの先頭は、このクラスのコレクションの実装に依存するため、
	 * 要素が 1 つしかないデータ代数元から要素を取得する場合に有効である。
	 * 
	 * @return	データ代数元の先頭に位置する要素の基底を返す。
	 * 
	 * @throws java.util.NoSuchElementException	要素が存在しない場合
	 */
	public DtBase getOneBase();

	/**
	 * このデータ代数元の先頭に位置する要素の値を取得する。
	 * 要素のコレクションの先頭は、このクラスのコレクションの実装に依存するため、
	 * 要素が 1 つしかないデータ代数元から要素を取得する場合に有効である。
	 * 
	 * @return	データ代数元の先頭に位置する要素の値を返す。
	 * 
	 * @throws NoSuchElementException	このデータ代数元に要素が存在しない場合
	 */
	public Object getOneValue();
	
	/**
	 * このデータ代数元に含まれる全ての基底を取り出す。
	 * 
	 * @return このデータ代数元に含まれる全ての基底の集合
	 */
	public BigDtBaseSet getBases();

	/**
	 * このデータ代数元に含まれる基底のうち、指定されたパターンに一致する
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
	 * このデータ代数元に含まれる基底のうち、指定されたパターンに一致する
	 * 基底のみを格納する基底集合を返す。
	 * <br>
	 * このメソッドが返す基底集合には、指定された基底パターン集合に含まれる
	 * 基底パターンのどれかに一致した基底が含まれる。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、もしくは、データ代数元が
	 * 要素を持たない場合、このメソッドは空の基底集合を返す。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return	パターンに一致した基底のみの集合
	 * 
	 * @throws NullPointerException	指定された基底パターン集合が <tt>null</tt> の場合
	 */
	public BigDtBaseSet getMatchedBases(DtBasePatternSet patterns);

	/**
	 * 指定された基底に対応する値を取り出す。
	 * 要素に含まれていない基底を指定した場合、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException 指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException 指定された基底が要素に含まれていない場合
	 */
	public Object get(DtBase base);

	/**
	 * 指定された基底に対応する値を、真偽値として取り出す。
	 * 指定された基底のデータ型キーが真偽値型ではない場合や、
	 * 要素に含まれていない基底を指定した場合は、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException	指定された基底が要素に含まれていない場合
	 * @throws IllegalValueOfDataTypeException	指定された基底のデータ型キーが真偽値型ではない場合
	 */
	public Boolean getBoolean(DtBase base);

	/**
	 * 指定された基底に対応する値を、文字列型として取り出す。
	 * 指定された基底のデータ型キーが文字列型ではない場合や、
	 * 要素に含まれていない基底を指定した場合は、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException	指定された基底が要素に含まれていない場合
	 * @throws IllegalValueOfDataTypeException	指定された基底のデータ型キーが文字列型ではない場合
	 */
	public String getString(DtBase base);
	
	/**
	 * 指定された基底に対応する値を、実数値として取り出す。
	 * 指定された基底のデータ型キーが実数値型ではない場合や、
	 * 要素に含まれていない基底を指定した場合は、このメソッドは例外をスローする。
	 * 
	 * @param base	取り出す値の基底
	 * @return	基底に対応する値
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws DtBaseNotFoundException	指定された基底が要素に含まれていない場合
	 * @throws IllegalValueOfDataTypeException	指定された基底のデータ型キーが実数値型ではない場合
	 */
	public BigDecimal getDecimal(DtBase base);

	/**
	 * 指定された基底と値が代入された、<code>BigDtalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定された値で上書きされる。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param base	データ代数の基底
	 * @param value	データ代数の値
	 * 
	 * @return	代入後のデータ代数元の新しいインスタンス
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws IllegalValueOfDataTypeException	指定された値が基底のデータ型と異なる場合
	 */
	public BigDtalge put(DtBase base, Object value);

	/**
	 * 指定されたデータ代数元のすべての要素が代入された、<code>MongoDtalge</code> の新しい
	 * インスタンスを返す。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定されたデータ代数元の値で上書きされる。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param alge	代入するデータ代数元
	 * @return	代入後のデータ代数元の新しいインスタンス
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 */
	public BigDtalge put(Dtalge alge);

	/**
	 * 指定されたデータ代数元のすべての要素が代入された、<code>MongoDtalge</code> の新しい
	 * インスタンスを返す。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定されたデータ代数元の値で上書きされる。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @param alge	代入するデータ代数元
	 * @return	代入後のデータ代数元の新しいインスタンス
	 * 
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 */
	public BigDtalge put(BigDtalge alge);

	/**
	 * 指定された基底と値を、このデータ代数に加算する。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定された値で上書きされる。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 * 
	 * @param base データ代数の基底
	 * @param value 値
	 * @return このオブジェクト
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 * @throws IllegalValueOfDataTypeException	指定された値が基底のデータ型と異なる場合
	 */
	public BigDtalge add(DtBase base, Object value);
	
	/**
	 * 指定されたデータ代数元のすべての要素を、このデータ代数に加算する。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定された値で上書きされる。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 * 
	 * @param alge	代入するデータ代数元
	 * @return このオブジェクト
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 */
	public BigDtalge add(Dtalge alge);
	
	/**
	 * 指定されたデータ代数元のすべての要素を、このデータ代数に加算する。
	 * <p>
	 * 指定した基底が存在していない場合、データ代数元の基底と値のマップ終端に追加される。
	 * すでに同一基底が存在する場合、指定された値で上書きされる。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 * 
	 * @param alge	代入するデータ代数元
	 * @return このオブジェクト
	 * @throws NullPointerException	指定された基底が <tt>null</tt> の場合
	 */
	public BigDtalge add(BigDtalge alge);

	/**
	 * データ代数元の要素の値が <tt>null</tt> のものを除外した、<code>MongoDtalge</code> の
	 * 新しいインスタンスを返す。
	 * <p>
	 * このメソッドは非破壊メソッドであり、このインスタンスは変更されない。
	 * 
	 * @return	値が <tt>null</tt> の要素を除外した <code>Dtalge</code> インスタンス
	 */
	public BigDtalge normalization();

	/**
	 * このデータ代数元から、要素の値が <tt>null</tt> のものを削除する。
	 * @return	このデータ代数元の内容が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean normalize();

	/**
	 * Dtalgeの複製を生成する。
	 * 
	 * @return 複製されたDtalge
	 */
	public BigDtalge copy();

	/**
	 * コレクションに含まれるデータ代数元を結合する。
	 * <br>
	 * このメソッドは、コレクションに含まれる全てのデータ代数元を一つのデータ代数元に
	 * 結合するメソッドであり、同じ基底の値は、コレクションに格納されている順序で
	 * 上書きされる。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 結合する値の基底が存在しない場合、データ代数の値と基底のマップの終端に、
	 * 指定されたデータ代数コレクション(c)に格納されている順序で追加される。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * この結合は、同じ基底の値は上書きされる。数値的な加算等の演算は行わない。
	 * </blockquote>
	 * 
	 * @param c 結合するデータ代数の元のコレクション
	 * 
	 * @return 計算結果となる <code>Dtalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	//static public Dtalge sum(Collection<? extends Dtalge> c);

	/**
	 * 2 つのデータ代数元が同値であるかを検証する。
	 * <p>このメソッドはインスタンスの同一性ではなく、同値性を評価する。
	 * <br>
	 * 2 つのデータ代数元の関係が次のような場合に同値とみなす。
	 * <ul>
	 * <li>同一の基底が存在する。
	 * <li>同一(等しい)基底に対応する値が全て同値である。
	 * </ul>
	 * なお、基底に対応する値の同値性は、{@link Object#equals(Object)} メソッドの
	 * 実行結果によるものとする。
	 * ただし、値が実数値の場合は {@link java.math.BigDecimal#compareTo(BigDecimal)} の
	 * 結果が 0 となる場合を同値とする。
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * このオブジェクトが返すハッシュコード値と <code>alge.hashCode()</code> の値が一致するとは限らない。
	 * 
	 * @param alge 同値性を比較する対象のデータ代数
	 * 
	 * @return 2つのデータ代数が同値である場合は <tt>true</tt>
	 */
	public boolean isSameValues(Dtalge alge);

	/**
	 * 2 つのデータ代数元が同値であるかを検証する。
	 * <p>このメソッドはインスタンスの同一性ではなく、同値性を評価する。
	 * <br>
	 * 2 つのデータ代数元の関係が次のような場合に同値とみなす。
	 * <ul>
	 * <li>同一の基底が存在する。
	 * <li>同一(等しい)基底に対応する値が全て同値である。
	 * </ul>
	 * なお、基底に対応する値の同値性は、{@link Object#equals(Object)} メソッドの
	 * 実行結果によるものとする。
	 * ただし、値が実数値の場合は {@link java.math.BigDecimal#compareTo(BigDecimal)} の
	 * 結果が 0 となる場合を同値とする。
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * このオブジェクトが返すハッシュコード値と <code>alge.hashCode()</code> の値が一致するとは限らない。
	 * 
	 * @param alge 同値性を比較する対象のデータ代数
	 * 
	 * @return 2つのデータ代数が同値である場合は <tt>true</tt>
	 */
	public boolean isSameValues(BigDtalge alge);

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * このインスタンスのハッシュ値を返す。
	 * 
	 * @return	ハッシュ値
	 */
	public int hashCode();

	/**
	 * 2 つのデータ代数元が同値であるかを検証する。
	 * <p>このメソッドはインスタンスの同一性ではなく、同値性を評価する。
	 * <br>
	 * 2 つのデータ代数元の関係が次のような場合に同値とみなす。
	 * <ul>
	 * <li>同一の基底が存在する。
	 * <li>同一(等しい)基底に対応する値が全て同値である。
	 * </ul>
	 * なお、基底に対応する値の同値性は、{@link Object#equals(Object)} メソッドの
	 * 実行結果によるものとする。
	 * ただし、値が実数値の場合は {@link java.math.BigDecimal#compareTo(BigDecimal)} の
	 * 結果が 0 となる場合を同値とする。
	 * 
	 * @param obj	同値性を判定するオブジェクトの一方
	 * 
	 * @return 同値である場合に <tt>true</tt> を返す。
	 */
	public boolean equals(Object obj);

	/**
	 * このインスタンスの文字列表現を返す。
	 * <p>
	 * 出力形式は、次の通り。
	 * <blockquote>
	 * <i>値</i> <i>基底</i> '+' <i>値</i> <i>基底</i> '+' ...
	 * </blockquote>
	 * なお、要素が一つも存在しない場合、次のように出力される。
	 * <blockquote>
	 * ()
	 * </blockquote>
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString();

	//------------------------------------------------------------
	// Operations
	//------------------------------------------------------------
	/**
	 * 指定された値と等しい要素のみを取り出す。<br>
	 * 指定の値と等しい要素が存在しない場合は、
	 * 要素を持たない <code>BigDtalge</code> の新しいインスタンスを返す。
	 * @param value	取り出す要素の値
	 * @return	指定された値と等しい要素のみを含む <code>MongoDtalge</code> の新しいインスタンス
	 */
	public BigDtalge oneValueProjection(Object value);

	/**
	 * 指定されたコレクションに含まれる値と等しい要素のみを取り出す。<br>
	 * 指定の値と等しい要素が存在しない場合は、
	 * 要素を持たない <code>BigDtalge</code> の新しいインスタンスを返す。
	 * @param values	取り出す要素の値のコレクション
	 * @return	指定されたコレクションに含まれる値と等しい要素のみを含む
	 * 			<code>BigDtalge</code> の新しいインスタンス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public BigDtalge valuesProjection(Collection<?> values);

	/**
	 * 値が <tt>null</tt> の要素のみを取り出す。<br>
	 * 値が <tt>null</tt> の要素が存在しない場合は、
	 * 要素を持たない <code>BigDtalge</code> の新しいインスタンスを返す。
	 * @return	値が <tt>null</tt> の要素のみを含む <code>BigDtalge</code> の新しいインスタンス
	 */
	public BigDtalge nullProjection();

	/**
	 * 値が <tt>null</tt> ではない要素のみを取り出す。<br>
	 * 値が <tt>null</tt> ではない要素が存在しない場合は、
	 * 要素を持たない <code>BigDtalge</code> の新しいインスタンスを返す。
	 * @return	値が <tt>null</tt> ではない要素のみを含む <code>BigDtalge</code> の新しいインスタンス
	 */
	public BigDtalge nonullProjection();
	
	/**
	 * このデータ代数元から、指定された基底と一致する要素のみを取り出し、
	 * その要素のみを持つ <code>BigDtalge</code> の新しいインスタンスを返す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>BigDtalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次のデータ代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[base](this)
	 * </blockquote>
	 * 
	 * @param base 基底
	 * 
	 * @return 取り出した基底の値のみを含む <code>BigDtalge</code>の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public MongoDtalge projection(DtBase base);

	/**
	 * このデータ代数元から、指定された基底集合に含まれる基底と一致する要素のみを
	 * 取り出し、その要素のみを持つ <code>BigDtalge</code> の新しいインスタンスを返す。
	 * <br>
	 * 指定された基底が存在しない場合、要素を持たない <code>BigDtalge</code> の
	 * 新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次のデータ代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[bases](this)
	 * </blockquote>
	 * 
	 * @param bases	取り出す基底が含まれるデータ代数基底集合
	 * 
	 * @return 取り出された要素のみを含む <code>BigDtalge</code> インスタンス
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 */
	public BigDtalge projection(DtBaseSet bases);

	/**
	 * このデータ代数元から、指定された基底集合に含まれる基底と一致する要素のみを
	 * 取り出し、その要素のみを持つ <code>BigDtalge</code> の新しいインスタンスを返す。
	 * <br>
	 * 指定された基底が存在しない場合、要素を持たない <code>BigDtalge</code> の
	 * 新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次のデータ代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[bases](this)
	 * </blockquote>
	 * 
	 * @param bases	取り出す基底が含まれるデータ代数基底集合
	 * 
	 * @return 取り出された要素のみを含む <code>BigDtalge</code> インスタンス
	 * 
	 * @throws NullPointerException 指定された基底集合が <tt>null</tt> の場合
	 */
	public BigDtalge projection(BigDtBaseSet bases);

	/**
	 * 指定の基底パターンに一致する基底を持つデータ代数のみを取り出す。
	 * <br>
	 * 指定された基底パターンに一致する基底のみを取得し、その基底のみを
	 * 持つデータ代数元を返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空のデータ代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param pattern	基底パターン
	 * @return			パターンに一致した基底のみを含む <code>BigDtalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public BigDtalge patternProjection(DtBasePattern pattern);

	/**
	 * 指定の集合の基底パターンに一致する基底を持つデータ代数のみを取り出す。
	 * <br>
	 * 指定された基底パターンに一致する基底のみを取得し、その基底のみを
	 * 持つデータ代数元を返す。
	 * <br>
	 * 返されるデータ代数元に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空のデータ代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return		パターンに一致した基底のみを含む <code>BigDtalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public BigDtalge patternProjection(DtBasePatternSet patterns);

	//------------------------------------------------------------
	// 振替系演算
	//------------------------------------------------------------

	/**
	 * 指定された基底の値が <code>srcObj</code> と等しい場合にのみ、
	 * その値を <code>dstObj</code> に置き換えた、<code>BigDtalge</code> の
	 * 新しいインスタンスを返す。
	 * <p>
	 * 次の場合、このデータ代数元自身のインスタンスを返す。
	 * <ul>
	 * <li>指定された基底が、このデータ代数元に存在しない場合
	 * <li>指定された基底に対応する値が、<code>srcObj</code> と等しくない場合
	 * </ul>
	 * 
	 * @param base	振替対象とするデータ代数基底
	 * @param srcObj	振替元の値
	 * @param dstObj	振替先の値
	 * @return	振替が行われた場合は振替結果を保持する新しい <code>BigDtalge</code> インスタンス、
	 * 			振替が行われなかった場合は <code>this</code>
	 * 
	 * @throws NullPointerException 指定された基底が <tt>null</tt> の場合
	 * @throws IllegalValueOfDataTypeException	指定された振替先の値が基底のデータ型と異なる場合
	 */
	public BigDtalge thesconv(DtBase base, Object srcObj, Object dstObj);

	/**
	 * 指定されたシソーラス定義を基に、指定された基底の値を、指定された分類集合の値に振り替える。
	 * このメソッドは、振り替え後の新しいデータ代数元を返す。
	 * <p>
	 * シソーラス定義に基づく分類集合への値振替では、指定された基底の値が
	 * シソーラス定義に属し、分類集合に含まれる値よりも小さい場合、
	 * 分類集合内の関係する値へ振り替える。
	 * <br>
	 * このメソッドでは、分類集合の値とは関係を持たない値の場合、その元は振り替え不可能と
	 * みなし <tt>null</tt> を返す。
	 * <p>
	 * 指定された基底が、このデータ代数元に存在しない場合、このメソッドは例外をスローする。
	 * 
	 * @param base	振替対象とするデータ代数基底
	 * @param thes	シソーラス定義
	 * @param words	振替先の値の分類集合
	 * @return	振替に成功した場合は、振替後の要素を格納する新しい <code>BigDtalge</code> の
	 * インスタンスを返す。振替不可能の場合は <tt>null</tt> を返す。
	 * 
	 * @throws NullPointerException 引数に指定されたオブジェクトが <tt>null</tt> の場合
	 * @throws IllegalArgumentException 基底のデータ型が文字列型ではない場合、
	 * 									もしくは、指定された語句の集合が分類集合ではない場合
	 * @throws DtBaseNotFoundException 指定された基底がこのデータ代数基底に含まれていない場合
	 */
	public BigDtalge thesconv(DtBase base, DtStringThesaurus thes, String...words);
	
	/**
	 * 指定されたシソーラス定義を基に、指定された基底の値を、指定された分類集合の値に振り替える。
	 * このメソッドは、振り替え後の新しいデータ代数元を返す。
	 * <p>
	 * シソーラス定義に基づく分類集合への値振替では、指定された基底の値が
	 * シソーラス定義に属し、分類集合に含まれる値よりも小さい場合、
	 * 分類集合内の関係する値へ振り替える。
	 * <br>
	 * このメソッドでは、分類集合の値とは関係を持たない値の場合、その元は振り替え不可能と
	 * みなし <tt>null</tt> を返す。
	 * <p>
	 * 指定された基底が、このデータ代数元に存在しない場合、このメソッドは例外をスローする。
	 * 
	 * @param base	振替対象とするデータ代数基底
	 * @param thes	シソーラス定義
	 * @param words	振替先の値の分類集合
	 * @return	振替に成功した場合は、振替後の要素を格納する新しい <code>BigDtalge</code> の
	 * インスタンスを返す。振替不可能の場合は <tt>null</tt> を返す。
	 * 
	 * @throws NullPointerException 引数に指定されたオブジェクトが <tt>null</tt> の場合
	 * @throws IllegalArgumentException 基底のデータ型が文字列型ではない場合、
	 * 									もしくは、指定された語句の集合が分類集合ではない場合
	 * @throws DtBaseNotFoundException 指定された基底がこのデータ代数基底に含まれていない場合
	 */
	public BigDtalge thesconv(DtBase base, DtStringThesaurus thes, Collection<? extends String> words);

	//------------------------------------------------------------
	// for I/O
	//------------------------------------------------------------
	
	/**
	 * データ代数元の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数元の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数元の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数元の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数元の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数元の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * データ代数元の内容を、指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void toTableCSV(DtBaseSet baseOrder, boolean withoutNull, File csvFile)
		throws IOException, FileNotFoundException;
	
	/**
	 * データ代数元の内容を、指定された文字セットで指定のファイルにテーブル形式の CSV フォーマットで出力する。
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
	 * @param baseOrder	順序指定用基底集合。順序を指定しない場合は <tt>null</tt>
	 * @param withoutNull	<tt>null</tt> を出力しない場合は <tt>true</tt>
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public void toTableCSV(DtBaseSet baseOrder, boolean withoutNull, File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException;
	
	/**
	 * データ代数元の内容を、指定のファイルに CSV フォーマットで出力する。
	 * <p>
	 * 出力時は、このインスタンスに格納されている基底の順序で出力される。
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
	 * データ代数元の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
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
	 */
	public void toCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException;

	/**
	 * CSV フォーマットのファイルを読み込み、このオブジェクトに追加する。
	 * @param csvFile	読み込む CSV ファイル
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
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
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public boolean addAllFromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException;
}
