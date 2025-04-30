/*
 * @(#)BigDtBaseSet.java	0.5.0	2019/02/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Set;

import dtalge.DtBase;
import dtalge.DtBasePattern;
import dtalge.DtBasePatternSet;
import dtalge.DtBaseSet;
import dtalge.exception.CsvFormatException;
import redundantalge.db.BigIterator;

/**
 * 大容量のデータ代数基底集合を表すオブジェクトのインタフェース。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface BigDtBaseSet extends Iterable<DtBase>
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
	public boolean contains(DtBase base);

	/**
	 * 指定されたコレクションに含まれる要素のすべてが、自身に含まれているかどうかを判定する。
	 * @param c	判定するコレクション
	 * @return	すべての要素が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsAll(Collection<? extends DtBase> c);

	/**
	 * 指定されたコレクションに含まれる要素のすべてが、自身に含まれているかどうかを判定する。
	 * @param set	判定するコレクション
	 * @return	すべての要素が含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsAll(BigDtBaseSet set);

	/**
	 * 指定されたコレクションに含まれる要素のどれか一つが、自身に含まれているかどうかを判定する。
	 * @param c	判定するコレクション
	 * @return	どれか一つの要素が含まれている場合は <tt>true</tt>、一つも含まれていない場合は <tt>false</tt>
	 */
	public boolean containsAny(Collection<? extends DtBase> c);
	
	/**
	 * 指定されたコレクションに含まれる要素のどれか一つが、自身に含まれているかどうかを判定する。
	 * @param set	判定するコレクション
	 * @return	どれか一つの要素が含まれている場合は <tt>true</tt>、一つも含まれていない場合は <tt>false</tt>
	 */
	public boolean containsAny(BigDtBaseSet set);

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
	public Set<String> getBaseNameKeySet(boolean withoutOmitted);
	
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
	public Set<String> getBaseTypeKeySet(boolean withoutOmitted);
	
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
	public Set<String> getBaseAttributeKeySet(boolean withoutOmitted);
	
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
	public Set<String> getBaseSubjectKeySet(boolean withoutOmitted);
	
	/**
	 * このオブジェクトのイテレーターを返す。
	 * @return	イテレーターオブジェクト
	 */
	public BigIterator<DtBase> iterator();

	/**
	 * この集合から、すべての要素を削除する。
	 */
	public void clear();

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
	public boolean add(DtBase base);

	/**
	 * 指定された基底を、この集合から削除する。
	 * @param base	削除する基底
	 * @return	削除された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean remove(DtBase base);

	/**
	 * 指定されたコレクションに含まれるすべての基底を、この集合に追加する。
	 * @param c	追加する基底のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public boolean addAll(Collection<? extends DtBase> c);
	
	/**
	 * 指定された基底集合に含まれるすべての基底を、この集合に追加する。
	 * @param set	追加する基底の集合
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
     * @throws NullPointerException 引数が <tt>null</tt> の場合にスローされる
	 */
	public boolean addAll(BigDtBaseSet set);

	/**
	 * 指定されたコレクションに含まれるすべての基底を、この集合から削除する。
	 * @param c	削除する基底のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean removeAll(Collection<? extends DtBase> c);
	
	/**
	 * 指定された集合に含まれるすべての基底を、この集合から削除する。
	 * @param set	削除する基底の集合
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean removeAll(BigDtBaseSet set);

	/**
	 * 指定されたコレクションに含まれるすべての基底のみを残し、その他の基底をこの集合から削除する。
	 * @param c	残す基底のコレクション
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean retainAll(Collection<? extends DtBase> c);

	/**
	 * 指定された基底集合に含まれるすべての基底のみを残し、その他の基底をこの集合から削除する。
	 * @param set	残す基底の集合
	 * @return	この集合が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean retainAll(BigDtBaseSet set);

	/**
	 * このオブジェクトの複製を生成する。
	 * 
	 * @return 複製された <code>BigDtBaseSet</code> オブジェクト
	 */
	public BigDtBaseSet copy();

	/**
	 * 基底集合を連結した、新しい基底集合のインスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #union(DtBaseSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結する基底集合
	 * @return		自身と引数に指定された集合を連結した新しい基底集合
	 */
	public BigDtBaseSet addition(DtBaseSet set);
	
	/**
	 * 基底集合を連結した、新しい基底集合のインスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #union(BigDtBaseSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	連結する基底集合
	 * @return		自身と引数に指定された集合を連結した新しい基底集合
	 */
	public BigDtBaseSet addition(BigDtBaseSet set);

	/**
	 * 引数に指定された基底集合の要素を除いた、新しい基底集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #difference(DtBaseSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つ基底集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しい基底集合
	 */
	public BigDtBaseSet subtraction(DtBaseSet set);

	/**
	 * 引数に指定された基底集合の要素を除いた、新しい基底集合の
	 * インスタンスを返す。
	 * <br>
	 * 基底集合では、要素の基底が重複することはないため、{@link #difference(BigDtBaseSet)} と
	 * 同じ結果を返す。
	 * <br>
	 * このメソッドは非破壊メソッドとなり、このインスタンスは変更されない。
	 * 
	 * @param set	自身の集合要素から取り除く要素を持つ基底集合
	 * @return		自身の集合要素から引数に指定された集合要素を取り除いた、
	 * 				新しい基底集合
	 */
	public BigDtBaseSet subtraction(BigDtBaseSet set);

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
	public BigDtBaseSet retention(DtBaseSet set);

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
	public BigDtBaseSet retention(BigDtBaseSet set);

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
	public BigDtBaseSet union(DtBaseSet set);

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
	public BigDtBaseSet union(BigDtBaseSet set);

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
	public BigDtBaseSet intersection(DtBaseSet set);

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
	public BigDtBaseSet intersection(BigDtBaseSet set);

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
	public BigDtBaseSet difference(DtBaseSet set);

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
	public BigDtBaseSet difference(BigDtBaseSet set);

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
	public BigDtBaseSet getMatchedBases(DtBasePattern pattern);

	// TODO: 将来的に大容量化に対応が必要かは、要検討
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
	public BigDtBaseSet getMatchedBases(DtBasePatternSet patterns);

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
		throws IOException, FileNotFoundException;
	
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
