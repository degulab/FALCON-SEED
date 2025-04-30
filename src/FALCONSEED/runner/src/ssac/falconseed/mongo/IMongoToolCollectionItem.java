/*
 * @(#)IMongoToolCollectionItem.java	3.4.0	2020/03/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mongo;

/**
 * MongoDBコレクションテーブルのアイテムデータのインタフェース。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public interface IMongoToolCollectionItem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトのコレクションに関する情報を更新する。
	 * MongoDB に対する操作でエラーが発生した場合は、例外をスローする。
	 * @return	情報が更新された場合は <tt>true</tt>、変化がない場合は <tt>false</tt>
	 * @throws com.mongodb.MongoException
	 */
	public boolean refresh();
	
	/**
	 * このオブジェクトが保持するコレクション名を返す。
	 * @return	コレクション名
	 */
	public String getCollectionName();
	
	/**
	 * このオブジェクトが保持するコレクションのドキュメント数を返す。
	 * このメソッドは、基本的にキャッシュされているドキュメント数を返す。
	 * @return	ドキュメント数
	 */
	public long	getDocumentCount();
}
