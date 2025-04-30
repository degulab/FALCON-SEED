/*
 * @(#)MongoToolSortedCollectionTableModel.java	3.4.0	2020/03/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;

import redundantalge.db.mongo.MongoSession;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.swing.table.AbSpreadSheetTableModel;

/**
 * MongoDBツール・ダイアログに表示するコレクションテーブルのモデル。
 * このモデルでは、常にコレクション名でソートされる。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class MongoToolSortedCollectionTableModel extends AbSpreadSheetTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	static public final int CI_COLLECTION_NAME	= 0;
	static public final int CI_COLLECTION_COUNT	= 1;
	
	static protected final String[] COLUMN_NAMES = {
		RunnerMessages.getInstance().MongoToolCollectionTableColumn_name,
		RunnerMessages.getInstance().MongoToolCollectionTableColumn_count,
	};
	
	static protected final CollectionNameComparator	_namedCollectionComparator = new CollectionNameComparator();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** MongoDB セッション **/
	protected MongoSession							_msession;
	/** MongoDB コレクション情報のリスト **/
	protected ArrayList<MongoToolCollectionItem>	_items;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MongoToolSortedCollectionTableModel() {
		_items = new ArrayList<MongoToolCollectionItem>();
	}
	
	public MongoToolSortedCollectionTableModel(int capacity) {
		_items = new ArrayList<MongoToolCollectionItem>(capacity);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 新しい MongoDB セッションを設定する。
	 * セッションが更新された場合は、テーブルの内容も更新する。
	 * @param newSession	新しいセッションオブジェクト、セッションを設定しない場合は <tt>null</tt>
	 * @return	内容が更新された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws com.mongodb.MongoException	MongoDB へのアクセスに失敗した場合
	 */
	public boolean setMongoSession(MongoSession newSession) {
		if (_msession == newSession) {
			// 同一インスタンスの場合は、変更しない
			return false;
		}

		_msession = newSession;
		if (newSession == null) {
			// クリア
			boolean modified = false;
			if (!_items.isEmpty()) {
				modified = true;
				int len = _items.size();
				_items.clear();
				fireTableRowsDeleted(0, len-1);
			}
			return modified;
		}
		else {
			return refresh();
		}
	}
	
	public boolean isEmpty() {
		return _items.isEmpty();
	}
	
	public boolean containsName(String colName) {
		return (find(colName) >= 0);
	}
	
	/**
	 * 指定されたコレクション名に該当するアイテムの位置を取得する。
	 * @param colName	検索するコレクション名
	 * @return	該当するアイテムが存在する場合はその位置を示すインデックス、そうでない場合は (-(挿入ポイント)-1) を返す。
	 */
	public int find(String colName) {
		return Collections.binarySearch(_items, new ComparingCollectionNameItem(colName), _namedCollectionComparator);
	}
	
	/**
	 * 指定された位置のアイテムを取得する。
	 * @param index	アイテムのインデックス
	 * @return	インデックスに対応するアイテムオブジェクト、インデックスが範囲外の場合は <tt>null</tt>
	 */
	public MongoToolCollectionItem get(int index) {
		if (index >= 0 && index < _items.size())
			return _items.get(index);
		else
			return null;
	}
	
	/**
	 * 指定されたコレクションを、このモデルに追加する。
	 * 同名のコレクションがすでに存在する場合は、指定されたコレクションを保持するアイテムに置き換えられる。
	 * @param mcol	追加する MongoDB のコレクションオブジェクト
	 * @return	新たに追加された場合はその位置を示すインデックス、置き換えられた場合は (-(対象インデックス)-1) を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalStateException	MongoDB への接続が確立していない場合
	 * @throws com.mongodb.MongoException	MongoDB へのアクセスに失敗した場合
	 */
	public int put(MongoCollection<Document> mcol) {
		validConnectedMongoSession();
		
		// アイテムオブジェクトを生成
		MongoToolCollectionItem newItem = new MongoToolCollectionItem(mcol);
		newItem.refresh();
		
		// 挿入位置を検索
		int newIndex = Collections.binarySearch(_items, newItem, _namedCollectionComparator);
		
		// 更新
		if (newIndex < 0) {
			// 新規追加
			newIndex = -(newIndex + 1);
			_items.add(newIndex, newItem);
			fireTableRowsInserted(newIndex, newIndex);
		}
		else {
			// 置換
			_items.set(newIndex, newItem);
			fireTableRowsUpdated(newIndex, newIndex);
			newIndex = (-newIndex) - 1;
		}
		return newIndex;
	}
	
	/**
	 * 指定された位置のアイテムを削除する。
	 * このメソッドは、MongoDB のコレクションそのものは削除しない。
	 * @param index	削除対象のインデックス
	 * @return	削除された場合はその位置に存在していたアイテムオブジェクト、それ以外の場合は <tt>null</tt>
	 */
	public MongoToolCollectionItem remove(int index) {
		if (index >= 0 && index < _items.size()) {
			MongoToolCollectionItem removed = _items.remove(index);
			fireTableRowsDeleted(index, index);
			return removed;
		}
		else {
			// out of range
			return null;
		}
	}
	
	/**
	 * 指定されたコレクション名と一致するアイテムを削除する。
	 * このメソッドは、MongoDB のコレクションそのものは削除しない。
	 * @param colName	削除対象のコレクション名
	 * @return	削除できた場合はそのアイテムの位置を示すインデックス、存在しない場合は (-1)
	 */
	public int remove(String colName) {
		int index = find(colName);
		if (index >= 0) {
			remove(index);
		}
		else {
			// not found
			index = (-1);
		}
		return index;
	}
	
	/**
	 * このオブジェクトのすべてのアイテムを削除する。
	 * このメソッドは、MongoDB のコレクションそのものは削除しない。
	 * @return	一つ以上削除された場合は <tt>treu</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean removeAll() {
		int len = _items.size();
		if (len > 0) {
			// remove all
			_items.clear();
			fireTableRowsDeleted(0, len-1);
			return true;
		}
		else {
			// no items
			return false;
		}
	}
	
	public MongoSession	getSession() {
		return _msession;
	}
	
	/**
	 * MongoDB にアクセスし、すべてのコレクション情報を更新する。
	 * @return	情報が更新された場合は <tt>true</tt>、変化がない場合は <tt>false</tt>
	 * @throws IllegalStateException	MongoDB への接続が確立していない場合
	 * @throws com.mongodb.MongoException	MongoDB へのアクセスに失敗した場合
	 */
	public boolean refresh() {
		if (_msession == null) {
			// MongoDB セッションが設定されていない場合は、何もしない
			return false;
		}
		validConnectedMongoSession();
		
		// コレクション収集
		TreeSet<String>	collectionNameSet = new TreeSet<String>();
		MongoIterable<String> result = _msession.getDatabase().listCollectionNames();
		for (String cname : result) {
			if (cname != null && !cname.isEmpty()) {
				collectionNameSet.add(cname);
			}
		}
		
		// 削除と更新
		boolean modified = false;
		for (int i = _items.size() - 1; i >= 0; i--) {
			MongoToolCollectionItem item = _items.get(i);
			if (collectionNameSet.remove(item.getCollectionName())) {
				// コレクションあり
				if (item.refresh()) {
					// 更新
					modified = true;
					fireTableRowsUpdated(i, i);
				}
			}
			else {
				// コレクション削除済み
				modified = true;
				_items.remove(i);
				fireTableRowsDeleted(i, i);
			}
		}
		
		// コレクション追加
		for (String cname : collectionNameSet) {
			// アイテムオブジェクトを生成
			MongoToolCollectionItem newItem = new MongoToolCollectionItem(_msession.getDatabase().getCollection(cname));
			newItem.refresh();
			
			// 挿入
			int newIndex = Collections.binarySearch(_items, newItem, _namedCollectionComparator);
			if (newIndex < 0) {
				modified = true;
				newIndex = -(newIndex + 1);
				_items.add(newIndex, newItem);
				fireTableRowsInserted(newIndex, newIndex);
			}
		}
		
		return modified;
	}

	/**
	 * 指定されたコレクション名で、新しいコレクションを生成する。
	 * @param newCollectionName	コレクション名
	 * @return	追加されたアイテムの位置を示すインデックス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が空文字列の場合、もしくはすでに存在するコレクション名の場合
	 * @throws IllegalStateException	MongoDB への接続が確立していない場合
	 * @throws com.mongodb.MongoException	MongoDB へのアクセスに失敗した場合
	 */
	public int createCollection(String newCollectionName) {
		if (newCollectionName.isEmpty())
			throw new IllegalArgumentException("newCollectionName is empty.");
		validConnectedMongoSession();
		
		// コレクションが存在しない場合は、MongoDB にコレクション作成
		if (!_msession.collectionExists(newCollectionName)) {
			_msession.getDatabase().createCollection(newCollectionName);
		}
		
		// コレクションを登録
		MongoToolCollectionItem newItem = new MongoToolCollectionItem(_msession.getDatabase().getCollection(newCollectionName));
		newItem.refresh();
		
		// 挿入
		int newIndex = Collections.binarySearch(_items, newItem, _namedCollectionComparator);
		// 更新
		if (newIndex < 0) {
			// 新規追加
			newIndex = -(newIndex + 1);
			_items.add(newIndex, newItem);
			fireTableRowsInserted(newIndex, newIndex);
		}
		else {
			// 置換(念のため)
			_items.set(newIndex, newItem);
			fireTableRowsUpdated(newIndex, newIndex);
			//newIndex = (-newIndex) - 1;
		}
		return newIndex;
	}

	/**
	 * 指定された位置のコレクションを、MongoDB から削除する。
	 * @param index	削除対象のインデックス
	 * @return	削除に成功した場合はそのアイテムのオブジェクト、それ以外の場合は <tt>null</tt>
	 * @throws IllegalStateException	MongoDB への接続が確立していない場合
	 * @throws com.mongodb.MongoException	MongoDB へのアクセスに失敗した場合
	 */
	public MongoToolCollectionItem deleteCollection(int index) {
		if (index >= 0 && index < _items.size()) {
			MongoToolCollectionItem removed = _items.get(index);
			validConnectedMongoSession();
			removed.getCollectionObject().drop();
			//--- 削除成功
			_items.remove(index);
			fireTableRowsDeleted(index, index);
			return removed;
		}
		else {
			// out of range
			return null;
		}
	}

	//------------------------------------------------------------
	// Implement AbstractTableModel interfaces
	//------------------------------------------------------------

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex >= 0 && columnIndex < COLUMN_NAMES.length) {
			return COLUMN_NAMES[columnIndex];
		}
		else {
			return super.getColumnName(columnIndex);
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == CI_COLLECTION_NAME) {
			return String.class;
		}
		else if (columnIndex == CI_COLLECTION_COUNT) {
			return Long.class;
		}
		else {
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// always uneditable
		return false;
	}

	//------------------------------------------------------------
	// Implement TableModel interfaces
	//------------------------------------------------------------

	@Override
	public int getRowCount() {
		return _items.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= 0 && rowIndex < _items.size()) {
			if (columnIndex == CI_COLLECTION_NAME) {
				return _items.get(rowIndex).getCollectionName();
			}
			else if (columnIndex == CI_COLLECTION_COUNT) {
				return _items.get(rowIndex).getDocumentCount();
			}
		}
		
		// not exist
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void validConnectedMongoSession() {
		if (_msession == null)
			throw new IllegalStateException("MongoDB session object is null.");
		if (!_msession.isConnected())
			throw new IllegalStateException("MongoDB session is disconnected.");
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class ComparingCollectionNameItem implements IMongoToolCollectionItem
	{
		private final String	_name;
		
		public ComparingCollectionNameItem(String collectionName) {
			_name = (collectionName==null ? "" : collectionName);
		}

		@Override
		public boolean refresh() {
			return false;
		}

		@Override
		public String getCollectionName() {
			return _name;
		}

		@Override
		public long getDocumentCount() {
			return 0;
		}
	}
	
	static protected class CollectionNameComparator implements Comparator<IMongoToolCollectionItem>
	{
		@Override
		public int compare(IMongoToolCollectionItem o1, IMongoToolCollectionItem o2) {
			return o1.getCollectionName().compareTo(o2.getCollectionName());
		}
	}
}
