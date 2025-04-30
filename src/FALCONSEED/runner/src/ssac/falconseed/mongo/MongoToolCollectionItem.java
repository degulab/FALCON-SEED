/*
 * @(#)MongoToolCollectionItem.java	3.4.0	2020/03/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mongo;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

/**
 * MongoDBコレクションテーブルのアイテムデータ。
 * 一つのコレクションの情報を保持する。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class MongoToolCollectionItem implements IMongoToolCollectionItem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** MongoDB コレクション **/
	protected final MongoCollection<Document>	_mcol;
	
	/** キャッシュされたドキュメント数 **/
	protected long	_cachedDocumentCount;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MongoToolCollectionItem(MongoCollection<Document> mongocollection) {
		if (mongocollection == null)
			throw new NullPointerException("MongoCollection object is null.");
		_mcol = mongocollection;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public MongoCollection<Document> getCollectionObject() {
		return _mcol;
	}
	
	/**
	 * このオブジェクトのコレクションに関する情報を更新する。
	 * MongoDB に対する操作でエラーが発生した場合は、例外をスローする。
	 * @return	情報が更新された場合は <tt>true</tt>、変化がない場合は <tt>false</tt>
	 * @throws com.mongodb.MongoException
	 */
	public boolean refresh() {
		long newCount = _mcol.countDocuments();
		if (_cachedDocumentCount != newCount) {
			// 変更あり
			_cachedDocumentCount = newCount;
			return true;
		}
		else {
			// 変更なし
			return false;
		}
	}
	
	/**
	 * このオブジェクトが保持するコレクション名を返す。
	 * @return	コレクション名
	 */
	public String getCollectionName() {
		return _mcol.getNamespace().getCollectionName();
	}
	
	/**
	 * このオブジェクトが保持するコレクションのドキュメント数を返す。
	 * このメソッドは、基本的にキャッシュされているドキュメント数を返す。
	 * @return	ドキュメント数
	 */
	public long	getDocumentCount() {
		return _cachedDocumentCount;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
