/*
 * @(#)BigExalge.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.db;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.ConcurrentModificationException;

import exalge2.ExBase;
import exalge2.ExBasePattern;
import exalge2.ExBasePatternSet;
import exalge2.ExBaseSet;
import exalge2.ExTransfer;
import exalge2.Exalge;
import exalge2.ExtendedKeyID;
import exalge2.TransMatrix;
import exalge2.TransTable;
import redundantalge.db.BigIterator;

/**
 * 大容量の交換代数元を表すオブジェクトのインタフェース。
 * 
 * @version 0.990
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface BigExalge extends Iterable<Exalge>
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
	 * 交換代数の要素が空であることを示す。
	 * 
	 * @return 要素が一つも存在しない場合に true を返す。
	 */
	public boolean isEmpty();
	
	/**
	 * 交換代数の要素数を返す。
	 * 
	 * @return 交換代数の要素数
	 */
	public long getNumElements();

	/**
	 * 指定された基底が、このインスタンスの要素に含まれているかを示す。
	 * 
	 * @param base 交換代数基底
	 * @return 指定の基底が要素に含まれている場合に <tt>true</tt> を返す。
	 */
	public boolean containsBase(ExBase base);

	/**
	 * 指定された基底が、このインスタンスの要素に全て含まれているかを示す。
	 * 指定された基底集合の要素が空の場合、このメソッドは <tt>true</tt> を返す。
	 * 
	 * @param bases	交換代数基底集合
	 * @return	指定された基底集合の全ての基底が、この交換代数元に含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean containsAllBases(ExBaseSet bases);

	/**
	 * 指定された基底が、このインスタンスの要素に全て含まれているかを示す。
	 * 指定された基底集合の要素が空の場合、このメソッドは <tt>true</tt> を返す。
	 * 
	 * @param bases	交換代数基底集合
	 * @return	指定された基底集合の全ての基底が、この交換代数元に含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean containsAllBases(BigExBaseSet bases);

	/**
	 * 指定された基底のどれか 1 つが、このインスタンスの要素に含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるどれか 1 つが、この
	 * 交換代数元の要素に存在した場合に <tt>true</tt> を返す。
	 * 
	 * @param bases	検証する基底の集合
	 * @return	指定された基底集合のどれか 1 つが含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean containsAnyBases(ExBaseSet bases);

	/**
	 * 指定された基底のどれか 1 つが、このインスタンスの要素に含まれているかを示す。
	 * <p>このメソッドは、指定された基底集合に含まれるどれか 1 つが、この
	 * 交換代数元の要素に存在した場合に <tt>true</tt> を返す。
	 * 
	 * @param bases	検証する基底の集合
	 * @return	指定された基底集合のどれか 1 つが含まれている場合に <tt>true</tt> を返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean containsAnyBases(BigExBaseSet bases);
	
	/**
	 * 指定された値が、このインスタンスの要素に含まれているかを示す。
	 * 
	 * @param value 交換代数の値
	 * @return 指定の値が要素に含まれている場合に <tt>true</tt> を返す。
	 */
	public boolean containsValue(BigDecimal value);

	/**
	 * このインスタンスの要素に <tt>null</tt> 値が含まれている場合に <tt>true</tt> を返す。
	 * @return	このインスタンスの要素に <tt>null</tt> 値が含まれている場合は <tt>true</tt>
	 */
	public boolean containsNull();
	
	/**
	 * このオブジェクトの交換代数要素にアクセスするイテレーターを取得する。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * @return	このオブジェクトに含まれる交換代数要素のイテレーター
	 */
	public BigIterator<BigExalgeElement> elementIterator();

	/**
	 * このオブジェクト交換代数基底にアクセスするイテレーターを取得する。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * @return	このオブジェクトに含まれる交換代数基底のイテレーター
	 */
	public BigIterator<ExBase> exbaseIterator();

	/**
	 * <code>Exalge</code> の要素の反復子を返す。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * 
	 * @return 交換代数要素の <code>Iterator</code>
	 * 
	 * @see ConcurrentModificationException
	 */
	public BigIterator<Exalge> iterator();

	/**
	 * 2つの交換代数が同値であるかを検証する。
	 * <br>
	 * このメソッドは、インスタンスの同一性ではなく、同値性を評価する。
	 * <br>{@link #isEqualValues(Exalge)} とは異なり、実数値 0 の基底も
	 * 評価対象となる。
	 * <br>
	 * 2つの交換代数が同値であるのは、次の場合となる。
	 * <ul>
	 * <li>2つの交換代数において、同一の基底が存在する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * 2つの <code>Exalge</code> インスタンスが返すハッシュコードが一致するとは
	 * 限らない。
	 * 
	 * @param alge 同値性を比較する対象の交換代数
	 * 
	 * @return 2つの交換代数が同値である場合は <tt>true</tt>
	 */
	public boolean isSameValues(Exalge alge);

	/**
	 * 2つの交換代数が同値であるかを検証する。
	 * <br>
	 * このメソッドは、インスタンスの同一性ではなく、同値性を評価する。
	 * <br>{@link #isEqualValues(Exalge)} とは異なり、実数値 0 の基底も
	 * 評価対象となる。
	 * <br>
	 * 2つの交換代数が同値であるのは、次の場合となる。
	 * <ul>
	 * <li>2つの交換代数において、同一の基底が存在する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * 2つの <code>Exalge</code> インスタンスが返すハッシュコードが一致するとは
	 * 限らない。
	 * 
	 * @param alge 同値性を比較する対象の交換代数
	 * 
	 * @return 2つの交換代数が同値である場合は <tt>true</tt>
	 */
	public boolean isSameValues(BigExalge alge);

	/**
	 * 2つの交換代数が等しいかを検証する。
	 * <br>
	 * このメソッドは、インスタンスの同一性ではなく、値としての同等性を評価する。
	 * <br>{@link #isSameValues(Exalge)} とは異なり、実数値 0 の基底は
	 * 評価対象としない。
	 * <br>
	 * 2つの交換代数が同等であるのは、次の場合となる。
	 * <ul>
	 * <li>要素の値が 0 のものは無視する。
	 * <li>要素の値が 0 ではないものの基底が、2つの交換代数において全て一致する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * 2つの <code>Exalge</code> インスタンスが返すハッシュコードが一致するとは
	 * 限らない。
	 * <b>注：</b>
	 * <blockquote>
	 * 値が <tt>null</tt> のものは、無視しない。
	 * </blockquote>
	 * 
	 * @param alge 同等性を比較する対象の交換代数
	 * 
	 * @return 2つの交換代数が等しい場合は <tt>true</tt>
	 */
	public boolean isEqualValues(Exalge alge);

	/**
	 * 2つの交換代数が等しいかを検証する。
	 * <br>
	 * このメソッドは、インスタンスの同一性ではなく、値としての同等性を評価する。
	 * <br>{@link #isSameValues(Exalge)} とは異なり、実数値 0 の基底は
	 * 評価対象としない。
	 * <br>
	 * 2つの交換代数が同等であるのは、次の場合となる。
	 * <ul>
	 * <li>要素の値が 0 のものは無視する。
	 * <li>要素の値が 0 ではないものの基底が、2つの交換代数において全て一致する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * 2つの <code>Exalge</code> インスタンスが返すハッシュコードが一致するとは
	 * 限らない。
	 * <b>注：</b>
	 * <blockquote>
	 * 値が <tt>null</tt> のものは、無視しない。
	 * </blockquote>
	 * 
	 * @param alge 同等性を比較する対象の交換代数
	 * 
	 * @return 2つの交換代数が等しい場合は <tt>true</tt>
	 */
	public boolean isEqualValues(BigExalge alge);

	/**
	 * この交換代数元に含まれる基底を一つだけ取り出す。
	 * <p>
	 * このメソッドは、{@link #projection(ExBase)} によって要素が一つだけ
	 * 格納されている交換代数元から、その基底を取り出すために利用する。
	 * 取り出される基底は、このインスタンスの実装に依存する。そのため、
	 * 複数の要素が格納されている場合、どの基底が取り出されるかを保証する
	 * ものではない。
	 * <p>
	 * 要素が一つも存在しない場合、このメソッドは例外をスローする。
	 * 
	 * @return	この交換代数元に含まれる基底の一つを返す。
	 * 
	 * @throws java.util.NoSuchElementException	要素が存在しない場合
	 */
	public ExBase getOneBase();
	
	/**
	 * この交換代数元に含まれる全ての基底を取り出す。
	 * 
	 * @return この交換代数元に含まれる全ての基底の集合
	 */
	public BigExBaseSet getBases();
	
	/**
	 * 指定された文字列の配列に含まれるどれか一つの文字列と一致する
	 * 名前キーを持つ基底のみを取得する。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する名前キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 */
	public BigExBaseSet getBasesByNameKey(String[] values);
	
	/**
	 * 指定された文字列の配列に含まれるどれか一つの文字列と一致する
	 * 単位キーを持つ基底のみを取得する。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する単位キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 */
	public BigExBaseSet getBasesByUnitKey(String[] values);
	
	/**
	 * 指定された文字列の配列に含まれるどれか一つの文字列と一致する
	 * 時間キーを持つ基底のみを取得する。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する時間キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 */
	public BigExBaseSet getBasesByTimeKey(String[] values);
	
	/**
	 * 指定された文字列の配列に含まれるどれか一つの文字列と一致する
	 * 主体キーを持つ基底のみを取得する。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する主体キーを持つ基底の集合を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の基底集合を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の基底集合を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 */
	public BigExBaseSet getBasesBySubjectKey(String[] values);

	/**
	 * この交換代数元に含まれる基底のうち、ハットなし基底のみを取り出す。
	 * 
	 * @return	この交換代数元に含まれるハットなし基底の集合
	 */
	public BigExBaseSet getNoHatBases();

	/**
	 * この交換代数元に含まれる基底のうち、ハット基底のみを取り出す。
	 * 
	 * @return	この交換代数元に含まれるハット基底の集合
	 */
	public BigExBaseSet getHatBases();

	/**
	 * この交換代数元から、全ての基底についてハットを除去した
	 * 基底集合を取得する。
	 * 
	 * @return	この交換代数元に含まれる全基底のハット除去後の基底集合
	 */
	public BigExBaseSet getBasesWithRemoveHat();

	/**
	 * この交換代数元から、全ての基底についてハットを付加した
	 * 基底集合を取得する。
	 * 
	 * @return	この交換代数元に含まれる全基底のハット付加後の基底集合
	 */
	public BigExBaseSet getBasesWithSetHat();

	/**
	 * 指定された基底と対応する値を取り出す。
	 * <br>
	 * 要素に含まれていない基底を指定した場合は、0 を返す。
	 * <p>
	 * <b>(注)</b> 0 が返された場合でも、指定した基底が存在しないとは限らない。
	 * 実数値 0 の基底が要素に含まれている場合もある。
	 * 
	 * @param exbase 指定された基底
	 * @return 指定された基底と対応する値
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigDecimal get(ExBase exbase);

	/**
	 * 指定された基底と対応する値を取り出す。
	 * <br>
	 * 要素に含まれていない基底を指定した場合は、<tt>null</tt> を返す。
	 * <p>
	 * <b>(注)</b> <tt>null</tt> が返された場合でも、指定した基底が存在しないとは限らない。
	 * <tt>null</tt> 値の基底が要素に含まれている場合もある。
	 * @param exbase	指定された基底
	 * @return	指定された基底と対応する値、もしくは <tt>null</tt>
	 */
	public BigDecimal getRealValue(ExBase exbase);

	/**
	 * 指定された基底と値が代入された、Exalgeの新しいインスタンスを返す。
	 * <p>
	 * 指定した基底が存在していない場合、交換代数の値と基底のマップの終端に
	 * 追加される。
	 * 
	 * <p>すでに同一基底が存在する場合、指定された値で上書きされる。この場合、
	 * 基底の順序は影響を受けない。
	 * <br>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * 
	 * @param exbase 交換代数の基底
	 * @param value  値(<tt>null</tt> の場合は、そのまま)
	 * @return	指定された基底と値が代入された新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、
	 * 								同一基底の値との加算演算が行えない場合
	 * 
	 * @see exalge2.ExBase
	 * @see exalge2.ExBase#hat()
	 */
	public BigExalge put(ExBase exbase, BigDecimal value);

	/**
	 * 交換代数の要素の値が 0 のものを要素から削除した、Exalgeの新しいインスタンスを返す。
	 * 
	 * @return 要素の値が 0 のものを除いた Exalge インスタンス
	 */
	public BigExalge normalization();

	/**
	 * Exalgeの複製を生成する。
	 * 
	 * @return 複製されたExalge
	 */
	public BigExalge copy();
	
	/**
	 * 交換代数の全基底について、拡張基底キーの単位キーを置換する。
	 * <p>
	 * 変換の結果、同一のキーが複数存在する場合、全ての同一キーの値は加算される。
	 * このとき、既存の基底順序の最初に出現した基底の位置に集約される。
	 * 
	 * @param newKey 新しいキー。<tt>null</tt> もしくは、長さ 0 の文字列の場合、省略記号が代入される。
	 * 
	 * @return 単位キーが置き換えられたExalgeを返す
	 */
	public BigExalge replaceUnitKey(String newKey);
	
	/**
	 * 交換代数の全基底について、拡張基底キーの時間キーを置換する。
	 * <p>
	 * 変換の結果、同一のキーが複数存在する場合、全ての同一キーの値は加算される。
	 * このとき、既存の基底順序の最初に出現した基底の位置に集約される。
	 * 
	 * @param newKey 新しいキー。<tt>null</tt> もしくは、長さ 0 の文字列の場合、省略記号が代入される。
	 * 
	 * @return 時間キーが置き換えられたExalgeを返す
	 */
	public BigExalge replaceTimeKey(String newKey);
	
	/**
	 * 交換代数の全基底について、指定された拡張基底キーを置換する。
	 * <p>
	 * 変換の結果、同一のキーが複数存在する場合、全ての同一キーの値は加算される。
	 * このとき、既存の基底順序の最初に出現した基底の位置に集約される。
	 * 
	 * @param keyid 置き換え対象の拡張基底キーのキーID。指定可能な値は、次のいずれかとなる。
	 * <ul>
	 * <li>{@link ExtendedKeyID#UNIT} - 単位キー
	 * <li>{@link ExtendedKeyID#TIME} - 時間キー
	 * <li>{@link ExtendedKeyID#SUBJECT} - サブジェクトキー
	 * </ul>
	 * @param newKey 新しいキー。<tt>null</tt> もしくは、長さ 0 の文字列の場合、省略記号が代入される。
	 * 
	 * @return 拡張基底キーが置き換えられたExalgeを返す
	 *
	 * @throws NullPointerException <tt>keyid</tt> が <tt>null</tt> の場合にスローされる
	 * 
	 * @see ExBase
	 * @see ExtendedKeyID
	 */
	public BigExalge replaceExtendedKey(ExtendedKeyID keyid, String newKey);

	/**
	 * 2つの交換代数が同値であるかを検証する。
	 * <br>
	 * このメソッドは{@link #isSameValues(Exalge)}と同様に、
	 * インスタンスの同一性ではなく、同値性を評価する。
	 * <br>{@link #isEqualValues(Exalge)} とは異なり、実数値 0 の基底も
	 * 評価対象となる。
	 * <br>
	 * 2つの交換代数が同値であるのは、次の場合となる。
	 * <ul>
	 * <li>2つの交換代数において、同一の基底が存在する。
	 * <li>2つの交換代数において、同一(等しい)基底に対する値が全て同値である。
	 * </ul>
	 * <p>
	 * 
	 * @param obj 同値かを判定するオブジェクトの一方
	 * 
	 * @return 2つの交換代数が同値である場合は <tt>true</tt>
	 */
	public boolean equals(Object obj);

	/**
	 * このインスタンスのハッシュ値を返す。
	 * 
	 * @return ハッシュ値
	 */
	public int hashCode();
	
	/**
	 * このインスタンスの全要素を文字列として出力する。
	 * <br>
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
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @param value	取り出す要素の値
	 * @return	指定された値と等しい要素のみを含む <code>Exalge</code> の新しいインスタンス
	 */
	public BigExalge oneValueProjection(BigDecimal value);

	/**
	 * 指定されたコレクションに含まれる値と等しい要素のみを取り出す。<br>
	 * 指定の値と等しい要素が存在しない場合は、
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @param values	取り出す要素の値のコレクション
	 * @return	指定されたコレクションに含まれる値と等しい要素のみを含む
	 * 			<code>Exalge</code> の新しいインスタンス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public BigExalge valuesProjection(Collection<? extends BigDecimal> values);

	/**
	 * 値が <tt>null</tt> の要素のみを取り出す。<br>
	 * 値が <tt>null</tt> の要素が存在しない場合は、
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @return	値が <tt>null</tt> の要素のみを含む <code>Exalge</code> の新しいインスタンス
	 */
	public BigExalge nullProjection();

	/**
	 * 値が <tt>null</tt> ではない要素のみを取り出す。<br>
	 * 値が <tt>null</tt> ではない要素が存在しない場合は、
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @return	値が <tt>null</tt> ではない要素のみを含む <code>Exalge</code> の新しいインスタンス
	 */
	public BigExalge nonullProjection();

	/**
	 * 値が 0 の要素のみを取り出す。<br>
	 * 値が 0 の要素が存在しない場合は、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @return	値が 0 の要素のみを含む <code>Exalge</code> の新しいインスタンス
	 */
	public BigExalge zeroProjection();
	
	/**
	 * 値が 0 ではない要素のみを取り出す。<br>
	 * 値が 0 ではない要素が存在しない場合は、
	 * 要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * @return	値が 0 ではない要素のみを含む <code>Exalge</code> の新しいインスタンス
	 */
	public BigExalge notzeroProjection();
	
	/**
	 * 指定の基底のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[base](this)
	 * </blockquote>
	 * 
	 * @param base 基底
	 * 
	 * @return 取り出した基底の値のみを含む <code>Exalge</code>の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExalge projection(ExBase base);
	
	/**
	 * 指定の基底集合に含まれる基底のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[bases](this)
	 * </blockquote>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 維持される。
	 * 
	 * @param bases 基底集合
	 * 
	 * @return 取り出した基底の値のみを含む <code>Exalge</code>の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExalge projection(ExBaseSet bases);
	
	/**
	 * 指定の基底集合に含まれる基底のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[bases](this)
	 * </blockquote>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 維持される。
	 * 
	 * @param bases 基底集合
	 * 
	 * @return 取り出した基底の値のみを含む <code>Exalge</code>の新しいインスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExalge projection(BigExBaseSet bases);

	/**
	 * 指定された基底とハット基底キー以外が一致する要素のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[removeHat(base) ∪ setHat(base)](this)
	 * </blockquote>
	 * 
	 * @param base	基底(ハット基底キーは無視される)
	 * @return	取り出した基底の値のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public BigExalge generalProjection(ExBase base);
	
	/**
	 * 指定された基底集合に含まれる基底の、ハット基底キー以外が一致する要素のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[removeHat(bases) ∪ setHat(bases)](this)
	 * </blockquote>
	 * 
	 * @param bases	基底集合(ハット基底キーは無視される)
	 * @return	取り出した基底の値のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public BigExalge generalProjection(ExBaseSet bases);
	
	/**
	 * 指定された基底集合に含まれる基底の、ハット基底キー以外が一致する要素のみを取り出す。
	 * <br>
	 * 指定の基底が存在しない場合、要素を持たない <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = Proj[removeHat(bases) ∪ setHat(bases)](this)
	 * </blockquote>
	 * 
	 * @param bases	基底集合(ハット基底キーは無視される)
	 * @return	取り出した基底の値のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public BigExalge generalProjection(BigExBaseSet bases);

	/**
	 * 指定の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された基底のハットキー以外の基底キーに一致する基底のみを
	 * 取得し、その集合を返す。ハットキー以外の基底キーにアスタリスク
	 * 文字<code>('*')</code>が含まれている場合、0文字以上の任意の文字に
	 * マッチするワイルドカードとみなす。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param base	基底パターンとみなす交換代数基底
	 * @return		パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExalge patternProjection(ExBase base);

	/**
	 * 指定の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された基底パターンに一致する基底のみを取得し、その基底のみを
	 * 持つ交換代数元を返す。
	 * <br>
	 * 基底パターンに一致する基底が存在しない場合、このメソッドは
	 * 空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param pattern	基底パターン
	 * @return			パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExalge patternProjection(ExBasePattern pattern);

	/**
	 * 指定の集合の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された集合に含まれる基底の、ハットキー以外の基底キーに
	 * 一致する基底のみを取得し、その集合を返す。ハットキー以外の
	 * 基底キーにアスタリスク文字<code>('*')</code>が含まれている場合、
	 * 0文字以上の任意の文字にマッチするワイルドカードとみなす。
	 * <br>
	 * 返される交換代数元に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param bases	基底パターンとみなす基底の集合
	 * @return		パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExalge patternProjection(ExBaseSet bases);

	/**
	 * 指定の集合の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された集合に含まれる基底の、ハットキー以外の基底キーに
	 * 一致する基底のみを取得し、その集合を返す。ハットキー以外の
	 * 基底キーにアスタリスク文字<code>('*')</code>が含まれている場合、
	 * 0文字以上の任意の文字にマッチするワイルドカードとみなす。
	 * <br>
	 * 返される交換代数元に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param bases	基底パターンとみなす基底の集合
	 * @return		パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExalge patternProjection(BigExBaseSet bases);

	/**
	 * 指定の集合の基底パターンに一致する基底を持つ交換代数のみを取り出す。
	 * <br>
	 * 指定された基底パターンに一致する基底のみを取得し、その基底のみを
	 * 持つ交換代数元を返す。
	 * <br>
	 * 返される交換代数元に含まれる基底は、指定の集合に含まれる基底パターンの
	 * どれかに一致した基底となる。基底パターンに一致する基底が存在しない場合、
	 * このメソッドは空の交換代数元を返す。
	 * <br>
	 * このメソッドが返すインスタンスでは、元(this)のインスタンスに格納されている基底の順序が
	 * 極力維持される。
	 * 
	 * @param patterns	基底パターンの集合
	 * @return		パターンに一致した基底のみを含む <code>Exalge</code> の新しいインスタンス
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExalge patternProjection(ExBasePatternSet patterns);
	
	/**
	 * 指定された文字列配列のどれか一つに一致する名前キーを持つ要素のみを取り出す。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する名前キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 */
	public BigExalge projectionByNameKey(String[] values);
	
	/**
	 * 指定された文字列配列のどれか一つに一致する単位キーを持つ要素のみを取り出す。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する単位キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 */
	public BigExalge projectionByUnitKey(String[] values);
	
	/**
	 * 指定された文字列配列のどれか一つに一致する時間キーを持つ要素のみを取り出す。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する時間キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 */
	public BigExalge projectionByTimeKey(String[] values);
	
	/**
	 * 指定された文字列配列のどれか一つに一致する主体キーを持つ要素のみを取り出す。
	 * 
	 * @param values	検索キーとなる文字列の配列
	 * @return	指定の文字列と一致する主体キーを持つ要素のみとなる交換代数元を返す。
	 * 			一致する基底が見つからなかった場合は、要素が空の交換代数元を返す。
	 * 			<code>values</code> の要素が空の場合も、要素が空の交換代数元を返す。
	 * 
	 * @throws NullPointerException	<code>values</code> が <tt>null</tt> の場合
	 */
	public BigExalge projectionBySubjectKey(String[] values);

	/**
	 * 指定の基底と値を加算した結果を持つ、<code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) + value&lt;exbase&gt;
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 加算する値の基底が存在しない場合、交換代数の値と基底のマップの終端に
	 * 追加される。
	 * 
	 * @param exbase 指定された基底
	 * @param value 値
	 * @return 加算の結果
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 */
	public BigExalge plus(ExBase exbase, BigDecimal value);

	/**
	 * 指定の交換代数を加算した結果を持つ、<code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) + (plusData)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 加算する値の基底が存在しない場合、交換代数の値と基底のマップの終端に、
	 * 指定された交換代数(plusData)に格納されている順序で追加される。
	 * 
	 * @param plusData　加算される交換代数
	 * @return　加算された後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 */
	public BigExalge plus(Exalge plusData);

	/**
	 * 指定の交換代数を加算した結果を持つ、<code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 値がマイナスの時、自動的に＾計算を行って値をプラスにするように設計されている。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) + (plusData)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 加算する値の基底が存在しない場合、交換代数の値と基底のマップの終端に、
	 * 指定された交換代数(plusData)に格納されている順序で追加される。
	 * 
	 * @param plusData　加算される交換代数
	 * @return　加算された後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 */
	public BigExalge plus(BigExalge plusData);

	/**
	 * 指定された基底と値を、交換代数に加算する。
	 * <p>
	 * (注)このメソッドは、インスタンスの値を書き換える。
	 *<p>
	 * 値がマイナスの時、自動的に ^ 計算を行って値をプラスにするように設計されている。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param base 交換代数の基底
	 * @param value 値
	 * @return このオブジェクト
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 */
	public BigExalge add(ExBase base, BigDecimal value);

	/**
	 * 指定された交換代数の元を、この交換代数に加算する。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param alge 加算する交換代数の元
	 * @return このオブジェクト
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 */
	public BigExalge add(Exalge alge);

	/**
	 * 指定された交換代数の元を、この交換代数に加算する。
	 * <p>
	 * (注) このメソッドは<b>破壊的メソッド</b>である。
	 * 
	 * @param alge 加算する交換代数の元
	 * @return このオブジェクト
	 * @throws NullPointerException 引数が <tt>null</tt> の場合、
	 * 								もしくは、値が <tt>null</tt> であり、同一基底の
	 * 								値との加算演算が行えない場合
	 */
	public BigExalge add(BigExalge alge);

	/**
	 * 交換代数元の逆元を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数の逆元は、次のように定義されている。
	 * <blockquote>
	 * x = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * invElement(x) = C&lt;e&gt; + D^&lt;e&gt;<br>
	 * このとき、C と D は、a と b の関係によって、次のように定義される。<br>
	 * a &gt; b のとき、C=1/(a-b)、D=0<br>
	 * a &lt; b のとき、C=0、D=1/(b-a)<br>
	 * a = b のとき、C=0、D=0
	 * </blockquote>
	 * この逆元は、交換代数元の逆数と次のような関係が成立する。
	 * <blockquote>
	 * invElement(x) = inverse(~x)
	 * </blockquote>
	 * 交換代数元の逆元の演算結果から、0値をもつ要素は除外される。
	 * 
	 * @return この交換代数元の逆元となる、新しい交換代数元を返す。
	 */
	public BigExalge invElement();

	/**
	 * 交換代数元の積(要素積)を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数元の積は、次のように定義されている。
	 * <blockquote>
	 * v = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * w = c&lt;e&gt; + d^&lt;e&gt;<br>
	 * v*w = (ac + bd)&lt;e&gt; + (ad + bc)^&lt;e&gt;<br>
	 * </blockquote>
	 * このとき、バー(~)演算は以下の等式が成立する。
	 * <blockquote>
	 * (~v)*(~w) = ~(v*w)
	 * </blockquote>
	 * 従って、交換代数元の積は、次の演算を行うものとなる。
	 * <blockquote>
	 * bases = (this.getBases() ∪ x.getBases()).removeHat()<br>
	 * (return) = (this) * (x)<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;= ∑{ v*w | bs&lt;-bases, v=this.generalProjection(bs), w=x.generalProjection(bs)}
	 * </blockquote>
	 * また、積の定義において対となる基底が存在しない場合は、0値を持つ要素とみなし、
	 * 演算した結果から0値を持つ要素を除外する。例として、次のような元の積を示す。
	 * <blockquote>
	 * (a&lt;e&gt; + b^&lt;e&gt;) * (c&lt;e&gt;) → (a&lt;e&gt; + b^&lt;e&gt;) * (c&lt;e&gt; + 0^&lt;e&gt;) = ac&lt;e&gt; + bc^&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt; + b^&lt;e&gt;) * (d^&lt;e&gt;) → (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = bd&lt;e&gt; + ad^&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt;) * (d^&lt;e&gt;) → (a&lt;e&gt; + 0^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = 0&lt;e&gt; + ad^&lt;e&gt; → ad^&lt;e&gt;
	 * <br>
	 * (b^&lt;e&gt;) * (d^&lt;e&gt;) → (0&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = bd&lt;e&gt; + 0^&lt;e&gt; → bd&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt;) * () → (a&lt;e&gt; + 0^&lt;e&gt;) * (0&lt;e&gt; + 0^&lt;e&gt;) = 0&lt;e&gt; + 0^&lt;e&gt; → ()
	 * </blockquote>
	 * 
	 * @param x	この交換代数元との積をとる、交換代数元の一方
	 * @return	演算後の交換代数
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public BigExalge multiple(Exalge x);

	/**
	 * 交換代数元の積(要素積)を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数元の積は、次のように定義されている。
	 * <blockquote>
	 * v = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * w = c&lt;e&gt; + d^&lt;e&gt;<br>
	 * v*w = (ac + bd)&lt;e&gt; + (ad + bc)^&lt;e&gt;<br>
	 * </blockquote>
	 * このとき、バー(~)演算は以下の等式が成立する。
	 * <blockquote>
	 * (~v)*(~w) = ~(v*w)
	 * </blockquote>
	 * 従って、交換代数元の積は、次の演算を行うものとなる。
	 * <blockquote>
	 * bases = (this.getBases() ∪ x.getBases()).removeHat()<br>
	 * (return) = (this) * (x)<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;= ∑{ v*w | bs&lt;-bases, v=this.generalProjection(bs), w=x.generalProjection(bs)}
	 * </blockquote>
	 * また、積の定義において対となる基底が存在しない場合は、0値を持つ要素とみなし、
	 * 演算した結果から0値を持つ要素を除外する。例として、次のような元の積を示す。
	 * <blockquote>
	 * (a&lt;e&gt; + b^&lt;e&gt;) * (c&lt;e&gt;) → (a&lt;e&gt; + b^&lt;e&gt;) * (c&lt;e&gt; + 0^&lt;e&gt;) = ac&lt;e&gt; + bc^&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt; + b^&lt;e&gt;) * (d^&lt;e&gt;) → (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = bd&lt;e&gt; + ad^&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt;) * (d^&lt;e&gt;) → (a&lt;e&gt; + 0^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = 0&lt;e&gt; + ad^&lt;e&gt; → ad^&lt;e&gt;
	 * <br>
	 * (b^&lt;e&gt;) * (d^&lt;e&gt;) → (0&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + d^&lt;e&gt;) = bd&lt;e&gt; + 0^&lt;e&gt; → bd&lt;e&gt;
	 * <br>
	 * (a&lt;e&gt;) * () → (a&lt;e&gt; + 0^&lt;e&gt;) * (0&lt;e&gt; + 0^&lt;e&gt;) = 0&lt;e&gt; + 0^&lt;e&gt; → ()
	 * </blockquote>
	 * 
	 * @param x	この交換代数元との積をとる、交換代数元の一方
	 * @return	演算後の交換代数
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public BigExalge multiple(BigExalge x);

	/**
	 * 交換代数の全要素に対して指定の値を乗算した結果を持つ、
	 * <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 演算結果が 0 となる場合でも、その要素は保持される。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) × (x)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 
	 * @param x　乗算の倍率(正の値であること)
	 * 
	 * @return　乗算された後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws ArithmeticException 引数に負の値が指定された場合にスローされる
	 */
	public BigExalge multiple(BigDecimal x);
	
	/**
	 * 交換代数元の商(要素商)を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数元の商は、交換代数の逆元を用いて次のように定義されている。
	 * <blockquote>
	 * v = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * w = c&lt;e&gt; + d^&lt;e&gt;<br>
	 * v/w = v * invElement(w) = (ac + bd)&lt;e&gt; + (ad + bc)^&lt;e&gt;<br>
	 * (c &gt; d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * ((1/(c-d))&lt;e&gt; + 0^&lt;e&gt;) = a(1/(c-d))&lt;e&gt; + b(1/(c-d))^&lt;e&gt;<br>
	 * (c &lt; d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + (1/(d-c))^&lt;e&gt;) = b(1/(d-c))&lt;e&gt; + a(1/(d-c))^&lt;e&gt;<br>
	 * (c = d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + 0^&lt;e&gt;) = 0&lt;e&gt; + 0^&lt;e&gt;
	 * </blockquote>
	 * 商の定義において対となる基底が存在しない場合は、0値を持つ要素とみなし、
	 * 演算した結果から0値を持つ要素を除外する。
	 * 
	 * @param x	この交換代数元との商をとる、交換代数元の一方
	 * @return	演算後の交換代数
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public BigExalge divide(Exalge x);
	
	/**
	 * 交換代数元の商(要素商)を演算し、その結果を格納する <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 交換代数元の商は、交換代数の逆元を用いて次のように定義されている。
	 * <blockquote>
	 * v = a&lt;e&gt; + b^&lt;e&gt;<br>
	 * w = c&lt;e&gt; + d^&lt;e&gt;<br>
	 * v/w = v * invElement(w) = (ac + bd)&lt;e&gt; + (ad + bc)^&lt;e&gt;<br>
	 * (c &gt; d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * ((1/(c-d))&lt;e&gt; + 0^&lt;e&gt;) = a(1/(c-d))&lt;e&gt; + b(1/(c-d))^&lt;e&gt;<br>
	 * (c &lt; d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + (1/(d-c))^&lt;e&gt;) = b(1/(d-c))&lt;e&gt; + a(1/(d-c))^&lt;e&gt;<br>
	 * (c = d) のとき、v/w = (a&lt;e&gt; + b^&lt;e&gt;) * (0&lt;e&gt; + 0^&lt;e&gt;) = 0&lt;e&gt; + 0^&lt;e&gt;
	 * </blockquote>
	 * 商の定義において対となる基底が存在しない場合は、0値を持つ要素とみなし、
	 * 演算した結果から0値を持つ要素を除外する。
	 * 
	 * @param x	この交換代数元との商をとる、交換代数元の一方
	 * @return	演算後の交換代数
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public BigExalge divide(BigExalge x);

	/**
	 * 交換代数の全要素に対して指定の値で除算した結果を持つ、
	 * <code>Exalge</code> の新しいインスタンスを返す。
	 * <p>
	 * 除算においては、{@link MathContext#DECIMAL128} の精度で結果を保持する。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = (this) ÷ (x)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 
	 * @param x 除算する値(正の値であること)
	 * 
	 * @return 除算された後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 * @throws ArithmeticException 引数の 0 もしくは負の値が指定された場合にスローされる
	 */
	public BigExalge divide(BigDecimal x);

	/**
	 * 交換代数のノルム計算、交換代数内の値の総和
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = |(this)|
	 * </blockquote>
	 * 
	 * @return　値の総和
	 */
	public BigDecimal norm();

	/**
	 * 交換代数の＾計算、すべての項目に対して、＾の部分の演算
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = ^ (this)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序を維持する。
	 * 
	 * @return　＾計算された後の交換代数
	 */
	public BigExalge hat();

	/**
	 * 交換代数の整合を行った結果を保持する、新しい <code>Exalge</code> インスタンスを返す。
	 * <p>
	 * 交換代数の整合は、基底キーのハットキー以外が同一のもので、HATとNO_HATの
	 * 値を加算(正確には、NO_HATの値－HATの値)した結果となる。
	 * <br>
	 * 整合後の値が正の値であれば、基底のハットキーはNO_HAT、負の値であればHATと
	 * し、値が 0 になる基底は、要素から削除される。
	 * <p>
	 * このメソッドは、次の交換代数演算を行うものである。
	 * <blockquote>
	 * (return) = ~ (this)
	 * </blockquote>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が極力維持されるが、
	 * 元(this)の順序を保障するものではない。
	 * 
	 * @return 演算結果の交換代数
	 */
	public BigExalge bar();

	/**
	 * 交換代数の整合を行った結果を保持する、新しい <code>Exalge</code> インスタンスを返す。
	 * <p>
	 * 交換代数の整合は、基底キーのハットキー以外が同一のもので、HATとNO_HATの
	 * 値を加算(正確には、NO_HATの値－HATの値)した結果となる。
	 * <br>
	 * 整合後の値が正の値であれば、基底のハットキーはNO_HAT、負の値であればHATと
	 * し、整合の結果の値が 0 になる基底は、要素から削除される。
	 * <p>このバー演算は、交換代数元の通常のバー演算よりも厳密であり、
	 * ハットなし基底とハット基底のペア(ハットキー以外の基底キーが同一のもの)でない場合、
	 * その要素は値に関わらず結果に出力される。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が極力維持されるが、
	 * 元(this)の順序を保障するものではない。
	 *
	 * @return 演算結果の交換代数
	 */
	public BigExalge strictBar();
	
	/**
	 * 交換代数の整合を行った結果を保持する、新しい <code>Exalge</code> インスタンスを返す。
	 * <p>
	 * 交換代数の整合は、基底キーのハットキー以外が同一のもので、HATとNO_HATの
	 * 値を加算(正確には、NO_HATの値－HATの値)した結果となる。
	 * <br>
	 * 整合後の値が正の値であれば、基底のハットキーはNO_HAT、負の値であればHATとし、整合の結果の値が 0 になる基底は、
	 * <em>leaveAsNoHat</em> が <tt>true</tt> であればハットなし基底、
	 * <em>leaveAsNoHat</em> が <tt>false</tt> であればハット基底として結果に出力される。
	 * <p>このバー演算は、交換代数元の通常のバー演算よりも厳密であり、
	 * ハットなし基底とハット基底のペア(ハットキー以外の基底キーが同一のもの)でない場合、
	 * その要素は値に関わらず結果に出力される。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が極力維持されるが、
	 * 元(this)の順序を保障するものではない。
	 *
	 * @param leaveAsNoHat	バー演算の結果、値が 0 となる要素の基底をハットなし基底とする場合は <tt>true</tt>、ハット基底とする場合は <tt>false</tt>
	 * @return 演算結果の交換代数
	 */
	public BigExalge strictBarLeaveZero(boolean leaveAsNoHat);

	/**
	 * 交換代数の逆数を算出する。
	 * <br>
	 * 交換代数の逆数は、全要素の実数値の逆数を持つものを指す。
	 * <p>
	 * (例)<br>
	 * 交換代数 X が<br>
	 * &nbsp;&nbsp;X = 5&lt;e1&gt; + 2&circ;&lt;e2&gt;<br>
	 * のとき、Y = X<sup>-1</sup> (Y = X.inverse()) は、次のようになる。<br>
	 * &nbsp;&nbsp;Y = X<sup>-1</sup> = 0.2&lt;e1&gt; + 0.5&circ;&lt;e2&gt;<br>
	 * <p>
	 * 逆数の演算において、{@link MathContext#DECIMAL128} の精度で結果を保持する。
	 * 実数値によって計算誤差が含まれる場合があるため、逆数の逆数が元の値とは
	 * ならない (つまり、X &ne; (X<sup>-1</sup>)<sup>-1</sup>) ことに注意すること。
	 * <p>
	 * 交換代数の要素が空の場合、結果も要素が空の交換代数となる。
	 * <br>
	 * また、実数値 0 の要素は無視され、演算結果に含まれない。
	 * <p>
	 * このメソッドでは、新しいインスタンスの基底の順序は、元(this)の基底の順序が維持される。
	 * 
	 * @return 逆数演算後の交換代数
	 */
	public BigExalge inverse();

	//------------------------------------------------------------
	// 振替系演算
	//------------------------------------------------------------
	
	/**
	 * 単一基底による基底の置換
	 * <p>
	 * この変換は、変換元基底に一致する基底を、変換先基底で示される基底に置換する。
	 * 基底は、ハット基底キーがワイルドカードとなっている基底パターンとみなされる。
	 * ハット基底キー以外の基底キーにワイルドカードを指定することも可能。変換先基底に
	 * ワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * なお、変換元基底に一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換操作は基底の置換となり、変換元基底は結果に含まれない。
	 * 
	 * @param fromBase	変換元基底
	 * @param toBase	変換先基底
	 * @return	変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public BigExalge transform(ExBase fromBase, ExBase toBase);
	
	/**
	 * 単一基底パターンによる基底の置換
	 * <p>
	 * この変換は、変換元基底パターンに一致する基底を、変換先基底パターンで示される基底に置換する。
	 * 変換先基底パターンにワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * なお、変換元基底パターンに一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換操作は基底の置換となり、変換元基底は結果に含まれない。
	 * 
	 * @param fromPattern	変換元基底パターン
	 * @param toPattern		変換先基底パターン
	 * @return	変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public BigExalge transform(ExBasePattern fromPattern, ExBasePattern toPattern);
	
	/**
	 * 複数基底による単一基底への基底の置換
	 * <p>
	 * この変換は、変換元基底のどれか一つに一致する基底を、変換先基底で示される基底に置換する。
	 * 基底は、ハット基底キーがワイルドカードとなっている基底パターンとみなされる。
	 * ハット基底キー以外の基底キーにワイルドカードを指定することも可能。変換先基底に
	 * ワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * なお、変換元基底に一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換操作は基底の置換となり、変換元基底は結果に含まれない。
	 * 
	 * @param fromBases	変換元基底集合
	 * @param toBase	変換先基底
	 * @return	変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public BigExalge transform(ExBaseSet fromBases, ExBase toBase);
	
	/**
	 * 複数基底パターンによる単一基底への基底の置換
	 * <p>
	 * この変換は、変換元基底パターンのどれか一つに一致する基底を、変換先基底パターンで示される基底に置換する。
	 * 変換先基底パターンにワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * なお、変換元基底パターンに一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換操作は基底の置換となり、変換元基底は結果に含まれない。
	 * 
	 * @param fromPatterns	変換元基底パターン集合
	 * @param toPattern		変換先基底パターン
	 * @return	変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public BigExalge transform(ExBasePatternSet fromPatterns, ExBasePattern toPattern);

	/**
	 * 指定された変換テーブルの変換定義に基づき、この交換代数元(<code>Exalge</code> オブジェクト)を変換する。
	 * 変換対象の基底に一致する、異なる変換元基底パターンが複数存在する場合は、
	 * 最初に一致した基底パターンを変換元基底パターンとする全ての変換定義によってのみ変換される。
	 * <p>
	 * 変換結果は、この交換代数元の全ての要素に、変換対象となった
	 * 基底のハットと元の値、変換後の全ての基底と値が加算された結果となる。
	 * <p>
	 * 変換方法については、{@link ExTransfer} クラスの説明を参照のこと。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param transfer	変換に使用する {@link ExTransfer} オブジェクト
	 * @return	変換結果となる交換代数元の新しいインスタンスを返す。一つも変換が行われなかった
	 * 			場合は、このオブジェクトをそのまま返す。
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ArithmeticException		'ratio'属性の変換において、変換比率の合計が 0 の場合にスローされる
	 * 
	 * @see ExTransfer
	 */
	public BigExalge transfer(ExTransfer transfer);

	/**
	 * 振替変換。Exalge#aggreTransfer(アグリゲーション)と同じ処理
	 * 
	 * @param table 振替変換テーブル
	 * @return 振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExalge transfer(TransTable table);

	/**
	 * 単一基底による振替変換(アグリゲーション)
	 * <p>
	 * この変換は、振替元基底に一致する基底を、振替先基底で示される基底に変換する。
	 * 基底は、ハット基底キーがワイルドカードとなっている基底パターンとみなされる。
	 * ハット基底キー以外の基底キーにワイルドカードを指定することも可能。振替先基底に
	 * ワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * 変換の際、変換対象の基底について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。これは、次のような振る舞いとなる。
	 * <blockquote>
	 * x = a^&lt;e1&gt; + b&lt;e2&gt;<br>
	 * x.aggreTransfer(&lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e2&gt; + a&lt;e1&gt; + a^&lt;e3&gt;
	 * </blockquote>
	 * なお、振替元基底に一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param fromBase	振替元基底
	 * @param toBase	振替先基底
	 * @return	振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public BigExalge aggreTransfer(ExBase fromBase, ExBase toBase);
	
	/**
	 * 単一基底パターンによる振替変換(アグリゲーション)
	 * <p>
	 * この変換は、振替元基底パターンに一致する基底を、振替先基底パターンで示される基底に変換する。
	 * 振替先基底パターンにワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを
	 * 指定しても、基底キー全体が変換対象基底キーに置き換わる。<br>
	 * 変換の際、変換対象の基底について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。これは、次のような振る舞いとなる。
	 * <blockquote>
	 * x = a^&lt;e1&gt; + b&lt;e2&gt;<br>
	 * x.aggreTransfer(&lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e2&gt; + a&lt;e1&gt; + a^&lt;e3&gt;
	 * </blockquote>
	 * なお、振替元基底パターンに一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param fromPattern	振替元基底パターン
	 * @param toPattern		振替先基底パターン
	 * @return	振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public BigExalge aggreTransfer(ExBasePattern fromPattern, ExBasePattern toPattern);
	
	/**
	 * 複数基底による単一基底への振替変換(アグリゲーション)
	 * <p>
	 * この変換は、振替元基底のどれか一つに一致する基底を、振替先基底で示される基底に変換する。
	 * 基底は、ハット基底キーがワイルドカードとなっている基底パターンとみなされる。
	 * ハット基底キー以外の基底キーにワイルドカードを指定することも可能。振替先基底に
	 * ワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * 変換の際、変換対象の基底について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。これは、次のような振る舞いとなる。
	 * <blockquote>
	 * x = a^&lt;e1&gt; + b&lt;e2&gt;<br>
	 * x.aggreTransfer(&lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e2&gt; + a&lt;e1&gt; + a^&lt;e3&gt;
	 * </blockquote>
	 * なお、振替元基底に一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param fromBases	振替元基底集合
	 * @param toBase	振替先基底
	 * @return	振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public BigExalge aggreTransfer(ExBaseSet fromBases, ExBase toBase);
	
	/**
	 * 複数基底パターンによる単一基底への振替変換(アグリゲーション)
	 * <p>
	 * この変換は、振替元基底パターンのどれか一つに一致する基底を、振替先基底パターンで示される基底に変換する。
	 * 振替先基底パターンにワイルドカードを含める場合、基底キー文字列の一部にワイルドカードを指定しても、
	 * 基底キー全体が変換対象基底キーに置き換わる。<br>
	 * 変換の際、変換対象の基底について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。これは、次のような振る舞いとなる。
	 * <blockquote>
	 * x = a^&lt;e1&gt; + b&lt;e2&gt;<br>
	 * x.aggreTransfer(&lt;e1&gt;, &lt;e3&gt;) = a^&lt;e1&gt; + b&lt;e2&gt; + a&lt;e1&gt; + a^&lt;e3&gt;
	 * </blockquote>
	 * なお、振替元基底パターンに一致する基底が存在しない場合、この交換代数元と同じものを返す。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param fromPatterns	振替元基底パターン集合
	 * @param toPattern		振替先基底パターン
	 * @return	振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public BigExalge aggreTransfer(ExBasePatternSet fromPatterns, ExBasePattern toPattern);

	/**
	 * 振替変換(アグリゲーション)
	 * <p>
	 * この変換は、指定された振替変換テーブルの変換定義に基づく振替変換を行う。
	 * この交換代数元の要素の基底に一致する、異なる振替元基底パターンが複数存在する
	 * 場合、最初に一致した振替元基底パターンの振替先基底パターンによって振替変換が
	 * 行われる。
	 * 変換の際、変換対象の要素について、基底のハットと変換後の基底が変換対象基底の値で
	 * 加算される。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param table 振替変換テーブル
	 * @return 振替変換後の交換代数
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public BigExalge aggreTransfer(TransTable table);

	/**
	 * 按分変換
	 * <p>
	 * この変換は、変換元基底パターンに一致する基底を、指定された按分比率で変換先基底パターンで
	 * 示される基底に変換する。
	 * 変換の際、変換対象の要素について、基底のハットと変換後の交換代数が加算される。
	 * <p>
	 * このメソッドでは、按分変換マトリクスの按分比率計算方法の
	 * 設定({@link exalge2.TransMatrix#isTotalRatioUsed()} が返す値)により、
	 * 按分値の算出方法が異なる。計算方法の詳細については、{@link exalge2.TransMatrix#transfer(ExBase, BigDecimal)} を参照のこと。
	 * <p>
	 * 按分比率計算における除算においては、{@link MathContext#DECIMAL128} の精度で結果を保持する。
	 * そのため、1 つの値を 3 当分するような按分変換の場合、誤差が発生する。
	 * <br>
	 * 指定の基底に一致する、異なる按分元基底パターンが複数存在する場合、
	 * 最初に一致した按分元基底パターンに関連付けられた変換定義により
	 * 按分変換が行われる。
	 * <p>
	 * <b>(注)</b> この変換では、Bar演算は行わない。変換結果を集約する場合、必要に応じて、
	 * Sum演算、Bar演算を行うこと。
	 * 
	 * @param matrix 按分比率マトリクス
	 * @return 按分変換後の交換代数
	 * 
	 * @see TransMatrix#transfer(ExBase, BigDecimal)
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws IllegalStateException	一致する按分元基底に対応する按分先基底が存在しない場合にスローされる
	 * @throws ArithmeticException		按分計算において 0 除算が発生した場合にスローされる
	 */
	public BigExalge divideTransfer(TransMatrix matrix);
}
