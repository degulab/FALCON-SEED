/*
 * @(#)BigDtStringThesaurus.java	0.5.0	2019/02/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

import dtalge.DtStringThesaurus;
import dtalge.IDtStringThesaurus;
import dtalge.db.mongo.MongoDtStringThesaurus;
import dtalge.exception.CsvFormatException;
import redundantalge.db.BigIterator;

/**
 * 大容量のシソーラス定義を保持するクラス。
 * 基本的に、{@link dtalge.DtStringThesaurus} と同様のインタフェースを提供する。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface BigDtStringThesaurus<TElem extends BigDtStringThesaurusElement> extends IDtStringThesaurus, Iterable<TElem>
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
	 * シソーラス定義をすべてクリアする。
	 */
	public void clear();

	/**
	 * シソーラス定義が存在しないなら <tt>true</tt> を返す。
	 * 
	 * @return	シソーラス定義が存在しない場合は <tt>true</tt>
	 */
	public boolean isEmpty();

	/**
	 * シソーラス定義の関係数を返す。
	 * 関係数とは、2 つの語句の直接的な結びつき(親子関係)を 1 とした場合の総数となる。
	 * 従って、親を複数持つ子がある場合、その関係もカウントされる。
	 * 
	 * @return 語句の直接的な関係数
	 */
	public long size();

	/**
	 * 指定された語句が直接的な関係となる子を有する場合は <tt>true</tt> を返す。
	 * 
	 * @param word	判定する語句
	 * @return	直接の子が定義されている場合は <tt>true</tt>
	 */
	public boolean hasChild(String word);

	/**
	 * 指定された語句が直接的な関係となる親を有する場合は <tt>true</tt> を返す。
	 * 
	 * @param word	判定する語句
	 * @return	直接の親が定義されている場合は <tt>true</tt>
	 */
	public boolean hasParent(String word);

	/**
	 * 指定された語句がシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param word	判定する語句
	 * @return	関係が定義されている語句であれば <tt>true</tt>
	 */
	public boolean contains(String word);
	
	/**
	 * 指定された語句の集合のどれか一つがシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param words		判定する語句の配列
	 * @return	シソーラス定義内に存在すれば <tt>true</tt>
	 */
	public boolean containsAny(String...words);

	/**
	 * 指定された語句の集合のどれか一つがシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param c		判定する語句の集合
	 * @return	シソーラス定義内に存在すれば <tt>true</tt>
	 */
	public boolean containsAny(Collection<? extends String> c);
	
	/**
	 * 指定された語句全てがシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param words		判定する語句の配列
	 * @return	シソーラス定義内に全て存在すれば <tt>true</tt>
	 */
	public boolean containsAll(String...words);

	/**
	 * 指定された語句全てがシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param c		判定する語句の集合
	 * @return	シソーラス定義内に全て存在すれば <tt>true</tt>
	 */
	public boolean containsAll(Collection<? extends String> c);

	/**
	 * 指定された親子関係がシソーラス定義内に存在する場合は <tt>true</tt> を返す。
	 * 
	 * @param parent	親となる語句
	 * @param child		子となる語句
	 * @return	2 つの語句が親子として定義されていれば <tt>true</tt>
	 */
	public boolean containsRelation(String parent, String child);

	/**
	 * 指定された語句の直接の親となる全ての語句を取得する。
	 * @param word	判定する語句
	 * @return	親となる全ての語句を格納する配列を返す。
	 * 			指定された語句が登録されていない場合や、親となる語句が未定義の場合は、
	 * 			要素が空の配列を返す。
	 */
	public String[] getParents(String word);

	/**
	 * 指定された語句の直接の子となる全ての語句を取得する。
	 * @param word	判定する語句
	 * @return	子となる全ての語句を格納する配列を返す。
	 * 			指定された語句が登録されていない場合や、子となる語句が未定義の場合は、
	 * 			要素が空の配列を返す。
	 */
	public String[] getChildren(String word);
	
	/**
	 * 指定された語句の直接の親となる全ての語句を取得する。
	 * @param word	判定する語句
	 * @return	親となる全ての語句を格納する文字列リストを返す。
	 * 			指定された語句が登録されていない場合や、親となる語句が未定義の場合は、
	 * 			要素が空の文字列リストを返す。
	 */
	public List<String> getThesaurusParents(String word);
	
	/**
	 * 指定された語句の直接の子となる全ての語句を取得する。
	 * @param word	判定する語句
	 * @return	子となる全ての語句を格納する文字列リストを返す。
	 * 			指定された語句が登録されていない場合や、子となる語句が未定義の場合は、
	 * 			要素が空の文字列リストを返す。
	 */
	public List<String> getThesaurusChildren(String word);

	/**
	 * 指定された 2 つの語句が比較可能(関係を持つ)であれば <tt>true</tt> を返す。
	 * なお、2 つの引数が同値の場合、<tt>false</tt> を返す。
	 * 
	 * @param word1		検証する語句
	 * @param word2		検証する語句のもう一方
	 * @return	比較可能であれば <tt>true</tt>
	 */
	public boolean isComparable(String word1, String word2);

	/**
	 * 指定された語句の集合が分類集合かを判定する。
	 * <p>
	 * 分類集合は、それぞれの語句の全ての組み合わせで比較不可能であることが
	 * 条件となる。
	 * 
	 * @param words		検証する語句の集合
	 * @return	分類集合であれば <tt>true</tt>
	 */
	public boolean isClassificationSet(String...words);
	
	/**
	 * 指定された語句の集合が分類集合かを判定する。
	 * <p>
	 * 分類集合は、それぞれの語句の全ての組み合わせで比較不可能であることが
	 * 条件となる。
	 * 
	 * @param c		検証する語句の集合
	 * @return	分類集合であれば <tt>true</tt>
	 */
	public boolean isClassificationSet(Collection<? extends String> c);

	/**
	 * このオブジェクトの複製を生成する。
	 * 
	 * @return 実体が複製されたオブジェクトの新しいインスタンス
	 */
	public MongoDtStringThesaurus copy();

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
	public boolean put(String parent, String child);

	/**
	 * 指定された語句をシソーラスから除去する。
	 * <p>
	 * このメソッドは、指定された語句そのものをシソーラスから除去するため、
	 * この語句への全ての関係を除去する。
	 * 
	 * @param word	除去する語句
	 * @return	語句が除去された場合は <tt>true</tt>
	 */
	public boolean remove(String word);

	/**
	 * 指定された親子関係をシソーラスから除去する。
	 * 
	 * @param parent	除去する関係の親となる語句
	 * @param child		除去する関係の子となる語句
	 * @return	関係が除去された場合は <tt>true</tt>
	 */
	public boolean remove(String parent, String child);

	/**
	 * 2 つのシソーラス定義が同値であるかを検証する。
	 * 指定されたオブジェクトがシソーラスであり、2 つのシソーラスが同じ
	 * 定義を表す場合に <tt>true</tt> を返す。
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * このオブジェクトが返すハッシュコード値と <code>thes.hashCode()</code> の値が一致するとは限らない。
	 * 
	 * @param thes 同値性を比較する対象のシソーラス定義
	 * 
	 * @return 指定されたオブジェクトがこのシソーラスと等しい場合は <tt>true</tt>
	 */
	public boolean isSameRelations(DtStringThesaurus thes);

	/**
	 * 2 つのシソーラス定義が同値であるかを検証する。
	 * 指定されたオブジェクトがシソーラスであり、2 つのシソーラスが同じ
	 * 定義を表す場合に <tt>true</tt> を返す。
	 * <p>
	 * 現在の実装において、このメソッドにより同等と判定された場合でも、
	 * このオブジェクトが返すハッシュコード値と <code>thes.hashCode()</code> の値が一致するとは限らない。
	 * 
	 * @param thes 同値性を比較する対象のシソーラス定義
	 * 
	 * @return 指定されたオブジェクトがこのシソーラスと等しい場合は <tt>true</tt>
	 */
	public boolean isSameRelations(BigDtStringThesaurus<? extends BigDtStringThesaurusElement> thes);

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
	public int hashCode();

	/**
	 * 指定されたオブジェクトとこのシソーラスが等しいかどうかを比較する。
	 * 指定されたオブジェクトがシソーラスであり、2 つのシソーラスが同じ
	 * 定義を表す場合に <tt>true</tt> を返す。
	 * 
	 * @param obj	このシソーラスと等しいかどうかを比較するオブジェクト
	 * @return	指定されたオブジェクトがこのシソーラスと等しい場合は <tt>true</tt>
	 */
	@Override
	public boolean equals(Object obj);

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
	public String toString();
	
	/**
	 * このオブジェクトのイテレーターを返す。
	 * @return	イテレーターオブジェクト
	 */
	@Override
	public BigIterator<TElem> iterator();

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
		throws IOException, FileNotFoundException;
	
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
		throws IOException, FileNotFoundException, UnsupportedEncodingException;

	/**
	 * CSV フォーマットのファイルを読み込み、このオブジェクトに追加する。
	 * @param csvFile	読み込む CSV ファイル
	 * @return	このシソーラス定義が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
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
	 * @return	このシソーラス定義が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws UnsupportedOperationException	このオブジェクトがビューの場合
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public boolean addAllFromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException;
}
