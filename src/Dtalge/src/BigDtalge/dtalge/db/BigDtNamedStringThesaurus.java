/*
 * @(#)BigDtNamedStringThesaurus.java	0.5.0	2019/02/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ConcurrentModificationException;
import java.util.List;

import dtalge.DtStringThesaurus;
import dtalge.exception.CsvFormatException;
import redundantalge.db.BigIterator;
import redundantalge.db.mongo.MongoAlgeError;

/**
 * 大容量の名前付きシソーラス定義を保持するクラス。
 * <p>名前に対応する要素は、データ代数のシソーラス定義(<code>MongoDtStringThesaurus</code>)として利用できる。
 * このオブジェクトは、シソーラス名をキー、シソーラス定義オブジェクト(<code>MongoDtStringThesaurus</code>)を値と
 * するマップであり、シソーラス名に関連付けられるシソーラス定義オブジェクトは一つである。
 * このオブジェクトでは、名前のないシソーラス定義も保持することができ、無名シソーラス定義として
 * 一つだけ保持することができる。
 * 
 * <p><b>この実装は同期化されない</b>。
 * <p>
 * シソーラス名(シソーラス定義ID) が同一のドキュメントは、同じシソーラス定義に所属していることを示す。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface BigDtNamedStringThesaurus<TElem extends BigDtStringThesaurus<? extends BigDtStringThesaurusElement>>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
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
	 * このマップから、すべての要素を削除する。
	 */
	public void clear();

	/**
	 * シソーラス定義が一つも存在しないなら <tt>true</tt> を返す。
	 * 
	 * @return	シソーラス定義が一つも存在しない場合は <tt>true</tt>
	 */
	public boolean isEmpty();
	
	/**
	 * このオブジェクトに格納されているシソーラス定義数を返す。
	 * このオブジェクトが返す値は、シソーラス定義名の総数となる。
	 * 
	 * @return シソーラス定義数
	 */
	public long size();
	
	/**
	 * このオブジェクトに格納されているシソーラス定義の関係数を返す。
	 * このオブジェクトが返す値は、シソーラス定義に含まれる親子関係数の合計値となる。
	 * @return	全シソーラス定義の親子関係数の合計
	 */
	public long getNumRelations();

	/**
	 * 指定されたシソーラス名を保持している場合に <tt>true</tt> を返す。
	 * シソーラス名に <tt>null</tt> もしくは空文字列を指定した場合、
	 * 無名シソーラスを保持しているかどうかを判定する。
	 * @param name	シソーラス名
	 * @return	指定されたシソーラス名を保持している場合は <tt>true</tt>
	 */
	public boolean containsName(String name);

	/*
	 * 指定されたシソーラスと等しいシソーラスを保持している場合に <tt>true</tt> を返す。
	 * @param thes	判定するシソーラス
	 * @return	指定されたシソーラスを保持している場合は <tt>true</tt>
	 */
	//public boolean containsThesaurus(dtalge.DtStringThesaurus thes) {
	//	return _thesmap.containsValue(thes);
	//}

	/**
	 * 指定されたシソーラス名に対応するシソーラスを取得する。
	 * シソーラス名に <tt>null</tt> もしくは空文字列を指定した場合、
	 * 無名シソーラスを返す。
	 * @param name	シソーラス名
	 * @return	シソーラス名に対応するシソーラスを返す。
	 * 			シソーラス名に対応するシソーラスが存在しない場合は <tt>null</tt> を返す。
	 */
	public BigDtStringThesaurus<? extends BigDtStringThesaurusElement> getThesaurus(String name);

	/**
	 * 指定されたシソーラスのシソーラス名を返す。
	 * 指定されたシソーラスと等しいシソーラスを複数保持している場合は、
	 * 最初に見つかったシソーラスのシソーラス名を返す。
	 * @param thes	検索対象のシソーラス
	 * @return	指定のシソーラスと等しいシソーラスのうち、最初に見つかった
	 * 			シソーラスのシソーラス名を返す。
	 * 			無名シソーラスの場合は空文字列を返す。
	 * 			見つからなかった場合は <tt>null</tt> を返す。
	 */
	public String nameOf(DtStringThesaurus thes);

	/**
	 * 指定されたシソーラスに対応するすべてのシソーラス名を取得する。
	 * 無名シソーラスの場合、シソーラス名には空文字列が格納される。
	 * @param thes	検索対象のシソーラス
	 * @return	指定されたシソーラスに対応するシソーラス名をすべて格納するリストを返す。
	 * 			指定されたシソーラスを保持していない場合は、要素が空のリストを返す。
	 */
	public List<String> findAllNamesOf(DtStringThesaurus thes);

	/**
	 * このオブジェクトが保持しているすべてのシソーラス名を取得する。
	 * @return	すべてのシソーラス名を格納するリストを返す。
	 * 			シソーラスが一つも存在しない場合は、要素が空のリストを返す。
	 */
	public List<String> getAllThesaurusNames();

	/**
	 * このオブジェクトの複製を生成する。
	 * 
	 * @return 実体が複製されたオブジェクトの新しいインスタンス
	 * @throws MongoAlgeError	データベースの処理が正常に行えなかった場合
	 */
	public BigDtNamedStringThesaurus<TElem> copy();
	
	/**
	 * 指定されたシソーラス名で、語句の関係を登録する。
	 * <p>
	 * このメソッドは、2 つの語句の親子関係(大小関係)をシソーラス名に対応するシソーラスへ登録する。
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
	 * @param name		シソーラス名
	 * @param parent	親として登録する語句
	 * @param child		子として登録する語句
	 * @return			新しい関係が登録された場合 <tt>true</tt>
	 * 
	 * @throws IllegalArgumentException	指定された語句もしくは語句の関係が適切ではない場合
	 */
	public boolean put(String name, String parent, String child);

	/**
	 * 指定されたシソーラス名で、指定されたシソーラスを登録する。
	 * シソーラス名が <tt>null</tt> もしくは空文字列の場合は、無名シソーラスとして登録する。
	 * 指定したシソーラス名がすでに登録されている場合、指定されたシソーラスで置き換える。
	 * @param name	シソーラス名
	 * @param thes	シソーラス
	 * @return	このオブジェクトのシソーラス定義が変更された場合に <tt>true</tt> を返す
	 * @throws	NullPointerException	<em>thes</em> が <tt>null</tt> の場合
	 */
	public boolean put(String name, DtStringThesaurus thes);

	/**
	 * 指定されたシソーラス名で、指定されたシソーラスを登録する。
	 * シソーラス名が <tt>null</tt> もしくは空文字列の場合は、無名シソーラスとして登録する。
	 * 指定したシソーラス名がすでに登録されている場合、指定されたシソーラスで置き換える。
	 * @param name	シソーラス名
	 * @param thes	シソーラス
	 * @return	このオブジェクトのシソーラス定義が変更された場合に <tt>true</tt> を返す
	 * @throws	NullPointerException	<em>thes</em> が <tt>null</tt> の場合
	 */
	public boolean put(String name, BigDtStringThesaurus<? extends BigDtStringThesaurusElement> thes);

	/**
	 * 指定されたシソーラス定義から、シソーラス名とシソーラスとの対応をすべてコピーする。
	 * シソーラス名とシソーラスとの対応は、指定された対応で置き換えられる。
	 * @param namedthes	コピーするシソーラス名とシソーラスとの対応
	 * @return	このオブジェクトのシソーラス定義が変更された場合に <tt>true</tt> を返す
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean putAll(BigDtNamedStringThesaurus<? extends BigDtStringThesaurus<? extends BigDtStringThesaurusElement>> namedthes);

	/**
	 * 指定されたシソーラス定義から、指定された親子関係をシソーラスから除去する。
	 * 
	 * @param name		シソーラス定義名
	 * @param parent	除去する関係の親となる語句
	 * @param child		除去する関係の子となる語句
	 * @return	関係が除去された場合は <tt>true</tt>
	 */
	public boolean remove(String name, String parent, String child);
	
	/**
	 * 指定されたシソーラス定義を除去する。
	 * @param name	除去するシソーラス名
	 * @return	シソーラス定義が除去された場合は <tt>true</tt>
	 */
	public boolean removeThesaurus(String name);
	
	/**
	 * このオブジェクトに含まれる最小要素にアクセスするイテレーターを取得する。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * @return	このオブジェクトに含まれる一つの親子関係を表す最小要素のイテレーター
	 */
	public BigIterator<BigDtNamedStringThesaurusInnerElement> innerElementIterator();
	
	/**
	 * このオブジェクトに含まれる最小要素にアクセスするイテレーターを取得する。
	 * 要素が返されるときの順序は、シソーラス定義名、親子関係の親語句、親子関係の子語句の順に辞書順ソートされた順序となる。
	 * @return	このオブジェクトに含まれる一つの親子関係を表す最小要素のイテレーター
	 */
	public BigIterator<BigDtNamedStringThesaurusInnerElement> sortedInnerElementIterator();
	
	/**
	 * 名前付きシソーラス定義のシソーラス定義ごとのイテレーターを返す。
	 * 要素が返されるときの順序は、このクラスのコレクションの実装に依存する。
	 * @return	シソーラス定義のイテレーターオブジェクト
	 * @see ConcurrentModificationException
	 */
	public BigIterator<TElem> iterator();

	/**
	 * 名前付きシソーラス定義の内容を、指定のファイルに CSV フォーマットで出力する。
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
	 * 名前付きシソーラス定義の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
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
	 * @return	このシソーラス定義が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
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
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws CsvFormatException カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public boolean addAllFromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, CsvFormatException, UnsupportedEncodingException;
}
