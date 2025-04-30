/*
 * @(#)ConversionStructureJsonItem.java	3.4.0	2020/03/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON-CSV 変換のための、JSON 変換構造アイテム・モデル。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public class ConversionStructureJsonItem extends AbConversionStructureItem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このアイテムの行見出し **/
	protected String	_rowName;
	/** このアイテムが KeyValue 要素のときの名前、KeyValue 要素でない場合は <tt>null</tt> **/
	protected String	_jsonName;
	/** このアイテムがコレクション要素のときの親となるアイテム **/
	protected ConversionStructureJsonItem				_parent;
	/** このアイテムが JSON 配列のときの配列要素、JSON 配列ではない場合は <tt>null</tt> **/
	protected ArrayList<ConversionStructureJsonItem>	_aryElements;
	/** このアイテムが JSON マップのときの KeyValue 要素、JSON マップではない場合は <tt>null</tt> **/
	protected LinkedHashMap<String, ConversionStructureJsonItem>	_keyvalElements;
	
	/** このアイテムに関連付けられたユーザーデータ **/
	protected Object	_userdata;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ConversionStructureJsonItem() {
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasUserData() {
		return (_userdata != null);
	}
	
	public void clearThisAndAllDescendantsUserData() {
		_userdata = null;
		// KeyValue 要素
		if (_keyvalElements != null && !_keyvalElements.isEmpty()) {
			for (Map.Entry<String, ConversionStructureJsonItem> entry : _keyvalElements.entrySet()) {
				entry.getValue().clearThisAndAllDescendantsUserData();
			}
		}
		
		// リスト要素
		if (_aryElements != null && !_aryElements.isEmpty()) {
			int arylen = _aryElements.size();
			for (int index = 0; index < arylen; index++) {
				_aryElements.get(index).clearThisAndAllDescendantsUserData();
			}
		}
	}
	
	public Object getUserData() {
		return _userdata;
	}
	
	public void setUserData(Object obj) {
		_userdata = obj;
	}

	/**
	 * アイテムの子孫要素のアイテムインデックスをすべて更新し、表示用値も更新する。
	 * アイテムインデックスは、子要素の KeyValue 要素、List 要素の順に連続したアイテムインデックスが設定される。
	 */
	public void refreshAllDescendantsDisplayValues() {
		// 自身
		refreshDisplayValues();
		
		// KeyValue 要素
		int index = 0;
		if (_keyvalElements != null && !_keyvalElements.isEmpty()) {
			for (Map.Entry<String, ConversionStructureJsonItem> entry : _keyvalElements.entrySet()) {
				ConversionStructureJsonItem subitem = entry.getValue();
				subitem.setItemIndex(index++);
				subitem.refreshAllDescendantsDisplayValues();
			}
		}
		
		// リスト要素
		if (_aryElements != null && !_aryElements.isEmpty()) {
			for (ConversionStructureJsonItem subitem : _aryElements) {
				subitem.setItemIndex(index++);
				subitem.refreshAllDescendantsDisplayValues();
			}
		}
	}
	
//	/**
//	 * JSON ドキュメントをパースし、このアイテムの内容を更新する。
//	 * @param jsondoc
//	 * @return
//	 */
//	public boolean parseJsonDocument(Document jsondoc) {
//		return parseJsonMap(jsondoc);
//	}
	
	@SuppressWarnings("unchecked")
	public boolean parseJsonValue(Object jsonobj) {
		//--- データ型判定
		if (isJsonObject(jsonobj)) {
			// KeyValue 要素を登録
			boolean modified = addDataType(JAVA_JSON_OBJECT);
			ensureJsonMap();
			if (parseJsonMap((Map<String,?>)jsonobj))
				return true;
			else
				return modified;
		}
		else if (isJsonArray(jsonobj)) {
			// リスト要素を登録
			boolean modified = addDataType(JAVA_JSON_ARRAY);
			ensureJsonList();
			if (parseJsonArray((List<?>)jsonobj))
				return true;
			else
				return modified;
		}
		else if (jsonobj != null) {
			// 値のデータ型を登録
			if (MONGO_DECIMAL.isInstance(jsonobj)) {
				// この場合は、BigDecimal として登録
				return addDataType(BigDecimal.class);
			}
			else {
				// オブジェクトのクラスを登録
				return addDataType(jsonobj.getClass());
			}
		}
		else {
			// NULL の場合は、更新なしとする
			return false;
		}
	}
	
	protected boolean parseJsonMap(Map<String, ?> jsonmap) {
		boolean modified = false;
		LinkedHashMap<String, ConversionStructureJsonItem> keyvalelems;
		for (Map.Entry<String, ?> entry : jsonmap.entrySet()) {
			String jsonKey = entry.getKey();
			Object jsonVal = entry.getValue();
			// KeyValue 要素
			keyvalelems = ensureJsonMap();
			ConversionStructureJsonItem subitem = keyvalelems.get(jsonKey);
			if (subitem == null) {
				// 新規要素の場合は、KeyValue 要素を追加
				modified = true;
				subitem = new ConversionStructureJsonItem();
				subitem.setJsonName(jsonKey);
				subitem.setParent(this);
				keyvalelems.put(jsonKey, subitem);
			}
			// 値をパース
			if (subitem.parseJsonValue(jsonVal)) {
				modified = true;
			}
		}
		return modified;
	}
	
	protected boolean parseJsonArray(List<?> jsonary) {
		boolean modified = false;
		ArrayList<ConversionStructureJsonItem> aryelems;
		int arylen = jsonary.size();
		for (int index = 0; index < arylen; index++) {
			Object jsonVal = jsonary.get(index);
			// List 要素
			aryelems = ensureJsonList();
			ConversionStructureJsonItem subitem = (index < aryelems.size() ? aryelems.get(index) : null);
			if (subitem == null) {
				// 新規要素なら追加
				modified = true;
				subitem = new ConversionStructureJsonItem();
				subitem.setItemIndex(index);	// 位置がはっきりしているので、インデックス登録
				subitem.setParent(this);
				aryelems.add(subitem);
			}
			// 値をパース
			if (subitem.parseJsonValue(jsonVal)) {
				modified = true;
			}
		}
		return modified;
	}
	
	/**
	 * 現在のアイテムの内容で、表示用の値を更新する。
	 */
	public void refreshDisplayValues() {
		// データ型
		super.refreshDisplayValues();
		// 行見出しは、親のアイテムインデックスを辿り、ハイフンで連結
		StringBuilder buf = getTemporaryStringBuffer();
		if (_itemIndex >= 0) {
			collectItemNumber(buf);
			_rowName = buf.toString();
			buf.setLength(0);
		} else {
			_rowName = null;
		}
		// デフォルト名は、親のデフォルト名を取得し、親がマップの場合のみドットで連結
		if (_parent != null) {
			buf.append(_parent._defaultName);
			if (_jsonName!=null) {
				// 親がマップの場合のみ、ドットで連結
				buf.append('.');
			}
		}
		//--- 自身の名前は KeyValue 要素の名前、もしくはインデックス
		if (_jsonName != null) {
			buf.append(_jsonName);
		} else {
			buf.append('[');
			buf.append(_itemIndex);
			buf.append(']');
		}
		setDefaultName(buf.toString());
	}
	
	/**
	 * このアイテムの行見出しとして表示する文字列を取得する。
	 * @return	行見出しとする文字列、指定しない場合は <tt>null</tt>
	 */
	public String getRowName() {
		return _rowName;
	}
	
	public boolean hasParent() {
		return (_parent != null);
	}
	
	public ConversionStructureJsonItem getParent() {
		return _parent;
	}
	
	public void setParent(ConversionStructureJsonItem parent) {
		_parent = parent;
	}
	
	public boolean isPrimitiveData() {
		int numTypes = getJsonDataTypeCount();
		if (numTypes <= 0) {
			return false;	// NULL のみの場合は、プリミティブ型とみなさない
		}
		else if (numTypes == 1) {
			// データ型が唯一の場合、MAP でも ARRAY でもないならプリミティブ型
			return (!containsJsonDataType(JSON_DATATYPE_OBJECT) && !containsJsonDataType(JSON_DATATYPE_ARRAY));
		}
		else if (numTypes == 2) {
			// MAP と ARRAY のどちらかが含まれていない場合は、プリミティブ型も含まれている
			return (!containsJsonDataType(JSON_DATATYPE_OBJECT) || !containsJsonDataType(JSON_DATATYPE_ARRAY));
		}
		else {
			// MAP と ARRAY 以外も含まれているため、プリミティブ型
			return true;
		}
	}
	
	public boolean isCollectionType() {
		return (_aryElements != null || _keyvalElements != null);
	}
	
	public boolean isListType() {
		return (_aryElements != null);
	}
	
	public boolean isKeyValueType() {
		return (_keyvalElements != null);
	}

	/**
	 * このアイテムが JSON KeyValue 要素のときの、名前を取得する。
	 * @return	KeyValue 要素の名前、KeyValue 要素ではない場合は <tt>null</tt>
	 */
	public String getJsonName() {
		return _jsonName;
	}
	
	/**
	 * このアイテムに JSON KeyValue 要素としての名前を設定する。
	 * @param name	設定する名前
	 */
	public void setJsonName(String name) {
		_jsonName = name;
	}

	//------------------------------------------------------------
	// Public interface for JSON-List
	//------------------------------------------------------------
	
	public ArrayList<ConversionStructureJsonItem> getJsonList() {
		return _aryElements;
	}

	public ArrayList<ConversionStructureJsonItem> ensureJsonList() {
		if (_aryElements == null) {
			_aryElements = new ArrayList<ConversionStructureJsonItem>();
		}
		return _aryElements;
	}

	//------------------------------------------------------------
	// Public interface for JSON-Map(KeyValue elements)
	//------------------------------------------------------------
	
	public LinkedHashMap<String, ConversionStructureJsonItem> getJsonMap() {
		return _keyvalElements;
	}
	
	public LinkedHashMap<String, ConversionStructureJsonItem> ensureJsonMap() {
		if (_keyvalElements == null) {
			_keyvalElements = new LinkedHashMap<String, ConversionStructureJsonItem>();
		}
		return _keyvalElements;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void collectItemNumber(StringBuilder buf) {
		if (_parent != null && _parent._itemIndex >= 0) {
			// アイテムインデックスが設定されている親のみを対象(rootレベルのアイテムは無視)
			_parent.collectItemNumber(buf);
			buf.append('-');
		}
		buf.append(_itemIndex+1);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
