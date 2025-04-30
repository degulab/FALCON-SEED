/*
 * @(#)AbConversionStructureItem.java	3.4.0	2020/03/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import ssac.util.Validations;

/**
 * JSON-CSV 変換のための、変換構造アイテム・モデルの共通実装。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public abstract class AbConversionStructureItem implements IConversionStructureItem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
//	static public final String[]	EMPTY_HEADER_NAMES	= new String[0];
//	
//	static public final Class<?>[]	EMPTY_JAVA_DATA_TYPES	= new Class<?>[0];
	
	//
	// 許可する JSON 要素(NULL型はないので、無視)
	//
	static public final Class<?>	JAVA_JSON_BOOLEAN	= Boolean.class;
	static public final Class<?>	JAVA_JSON_NUMBER	= Number.class;
	static public final Class<?>	JAVA_JSON_STRING	= String.class;
	static public final Class<?>	JAVA_JSON_ARRAY		= List.class;
	static public final Class<?>	JAVA_JSON_OBJECT	= Map.class;
	static public final Class<?>	MONGO_DECIMAL	= org.bson.types.Decimal128.class;
	
	static public final Class<?>[]	JSON_JAVA_TYPES = {
		JAVA_JSON_BOOLEAN, JAVA_JSON_NUMBER, JAVA_JSON_STRING, JAVA_JSON_ARRAY, JAVA_JSON_OBJECT, MONGO_DECIMAL,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 文字列成型用テンポラリバッファ **/
	static private StringBuilder	_bufStrings;

	/** このアイテムの位置を表すインデックス、未設定の場合は (-1) **/
	protected int		_itemIndex = (-1);
	/** このアイテムのデフォルト名、設定されていない場合は <tt>null</tt>。 <code>refresh</code> メソッドの呼び出しで更新される **/
	protected String	_defaultName;
	/** このアイテムのユーザー定義名、設定されてない場合は <tt>null</tt> **/
	protected String	_customName;
	
	/** このアイテムの Java データ型をすべて保持するセット、使用されていない場合は <tt>null</tt> **/
	private Set<Class<?>>	_javaDataTypeSet;
	/** このアイテムの JSON データ型をすべて保持するセット、使用されていない場合は <tt>null</tt> **/
	private Set<String>		_jsonDataTypeSet;
	/** このアイテムの JSON データ型を表す文字列 **/
	private String			_displayDataTypes;
	
	/** このアイテムに関連付けられたアイテム **/
	private IConversionStructureItem	_attached;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Helpers
	//------------------------------------------------------------
	
	static public String getJsonDataType(Object obj) {
		if (obj == null)
			return null;
		
		if (JAVA_JSON_BOOLEAN.isInstance(obj))
			return JSON_DATATYPE_BOOLEAN;
		else if (JAVA_JSON_NUMBER.isInstance(obj))
			return JSON_DATATYPE_NUMBER;
		else if (MONGO_DECIMAL.isInstance(obj))
			return JSON_DATATYPE_NUMBER;
		else if (JAVA_JSON_ARRAY.isInstance(obj))
			return JSON_DATATYPE_ARRAY;
		else if (JAVA_JSON_OBJECT.isInstance(obj))
			return JSON_DATATYPE_OBJECT;
		else {
			// 上記以外は、すべて文字列型
			return JSON_DATATYPE_STRING;
		}
	}
	
	static public String convertJavaToJsonDataType(Class<?> javatype) {
		if (javatype == null)
			return null;
		
		if (JAVA_JSON_BOOLEAN.isAssignableFrom(javatype))
			return JSON_DATATYPE_BOOLEAN;
		else if (JAVA_JSON_NUMBER.isAssignableFrom(javatype))
			return JSON_DATATYPE_NUMBER;
		else if (MONGO_DECIMAL.isAssignableFrom(javatype))
			return JSON_DATATYPE_NUMBER;
		else if (JAVA_JSON_ARRAY.isAssignableFrom(javatype))
			return JSON_DATATYPE_ARRAY;
		else if (JAVA_JSON_OBJECT.isAssignableFrom(javatype))
			return JSON_DATATYPE_OBJECT;
		else {
			// 上記以外は、すべて文字列型
			return JSON_DATATYPE_STRING;
		}
	}
	
	static public boolean isJsonObject(Object obj) {
		return JAVA_JSON_OBJECT.isInstance(obj);
	}
	
	static public boolean isJsonArray(Object obj) {
		return JAVA_JSON_ARRAY.isInstance(obj);
	}
	
	static public boolean isJsonPrimitive(Object obj) {
		// NULL は特殊な値のため、プリミティブから除外する
		if (obj == null)
			return false;
		
		// どんなデータ型かは不定なため、Map と List 以外はプリミティブ扱い
		return (!JAVA_JSON_OBJECT.isInstance(obj) && !JAVA_JSON_ARRAY.isInstance(obj));
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 現在のアイテムの内容で、表示用の値を更新する。
	 */
	public void refreshDisplayValues() {
		refreshDisplayDataTypeStringByJson();
	}
	
	/**
	 * このアイテムの位置を表すインデックスを取得する。
	 * @return	アイテムインデックス、設定されていない場合は (-1)
	 */
	public int getItemIndex() {
		return _itemIndex;
	}
	
	/**
	 * このアイテムの位置を表すインデックスを設定する。
	 * @param newIndex	設定するアイテムインデックス、無効とする場合は (-1)
	 */
	public void setItemIndex(int newIndex) {
		_itemIndex = (newIndex < 0 ? (-1) : newIndex);
	}
	
	/**
	 * このアイテムの行見出しとして表示する文字列を取得する。
	 * @return	行見出しとする文字列、指定しない場合は <tt>null</tt>
	 */
	public String getRowName() {
		return (_itemIndex < 0 ? null : String.valueOf(_itemIndex+1));
	}
	
	/**
	 * このアイテムの名前を表す文字列を取得する。
	 * @return	名前
	 */
	public String getDisplayName() {
		return (_customName==null ? _defaultName : _customName);
	}
	
	/**
	 * このアイテムの標準名を取得する。
	 * @return	標準名、設定されていない場合は <tt>null</tt>
	 */
	public String getDefaultName() {
		return _defaultName;
	}
	
	/**
	 * このアイテムに標準名を設定する。
	 * @param newName	設定する名前、未設定とする場合は <tt>null</tt>
	 */
	public void setDefaultName(String newName) {
		_defaultName = newName;
	}
	
	/**
	 * このアイテムのユーザー定義名を取得する。
	 * @return	ユーザー定義名、設定されていない場合は <tt>null</tt>
	 */
	public String getCustomName() {
		return _customName;
	}
	
	/**
	 * このアイテムにユーザー定義名を設定する。
	 * @param newName	設定する名前、未設定とする場合は <tt>null</tt>
	 */
	public void setCustomName(String newName) {
		_customName = newName;
	}
	
	/**
	 * このアイテムにデータ型が指定されていないかどうかを判定する。
	 * @return	データ型が一つも指定されていない場合は <tt>true</tt>
	 */
	public boolean isEmptyDataTypes() {
		return (_jsonDataTypeSet==null ? true : _jsonDataTypeSet.isEmpty());
	}
	
	/**
	 * このアイテムに指定されているすべてのデータ型をクリアする。
	 */
	public void clearDataTypes() {
		if (_jsonDataTypeSet != null) {
			_jsonDataTypeSet.clear();
			_jsonDataTypeSet = null;
		}
		if (_javaDataTypeSet != null) {
			_javaDataTypeSet.clear();
			_javaDataTypeSet = null;
		}
	}
	
	/**
	 * このアイテムに指定された JSON データ型の総数を取得する。
	 * @return	JSON データ型の総数
	 */
	public int getJsonDataTypeCount() {
		return (_jsonDataTypeSet==null ? 0 : _jsonDataTypeSet.size());
	}
	
	/**
	 * このアイテムに指定された Java データ型の総数を取得する。
	 * @return	Java データ型の総数
	 */
	public int getJavaDataTypeCount() {
		return (_javaDataTypeSet==null ? 0 : _javaDataTypeSet.size());
	}
	
	/**
	 * 指定された JSON データ型が、このアイテムに指定されているかどうかを判定する。
	 * @param dtype	判定する JSON データ型
	 * @return	指定されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsJsonDataType(String dtype) {
		if (dtype == null)
			return false;
		else
			return (_jsonDataTypeSet==null ? false : _jsonDataTypeSet.contains(dtype.toLowerCase()));
	}
	
	/**
	 * 指定された Java データ型が、このアイテムに指定されているかどうかを判定する。
	 * @param dtype	判定する Java データ型
	 * @return	指定されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsJavaDataType(Class<?> dtype) {
		return (_javaDataTypeSet==null ? false : _javaDataTypeSet.contains(dtype));
	}
	
	/**
	 * このアイテムに指定されているすべての JSON データ型を取得する。
	 * @return	このアイテムのすべての JSON データ型を保持する配列、指定されていない場合は空の配列
	 */
	public String[] getJsonDataTypes() {
		return (_jsonDataTypeSet==null ? new String[0] : _jsonDataTypeSet.toArray(new String[_jsonDataTypeSet.size()]));
	}
	
	/**
	 * このアイテムに指定されているすべての Java データ型を取得する。
	 * @return	このアイテムのすべての Java データ型を保持する配列、指定されていない場合は空の配列
	 */
	public Class<?>[] getJavaDataTypes() {
		return (_javaDataTypeSet==null ? new Class<?>[0] : _javaDataTypeSet.toArray(new Class<?>[_javaDataTypeSet.size()]));
	}
	
	/**
	 * 指定されたオブジェクトの Java データ型をこのアイテムに追加し、Java データ型に対応する JSON データ型も追加する。
	 * <em>obj</em> が <code>Class</code> の場合は、そのクラス型から JSON データ型を取得する。
	 * なお、<em>obj</em> が <tt>null</tt> の場合、このメソッドは何もせずに <tt>false</tt> を返す。
	 * @param obj	追加するデータ型の Java オブジェクト(または、Java Class)
	 * @return	新たに追加された場合は <tt>true</tt>、すでに指定されている場合は <tt>false</tt>
	 */
	public boolean addDataType(Object obj) {
		if (obj == null)
			return false;

		Class<?> javaClass;
		if (obj instanceof Class) {
			javaClass = (Class<?>)obj;
		}
		else {
			javaClass = obj.getClass();
		}

		if (addJavaDataType(javaClass)) {
			String jsonType = convertJavaToJsonDataType(javaClass);
			addJsonDataType(jsonType);
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * 指定された Java データ型を、このアイテムに追加する。
	 * @param dtype	追加する Java データ型
	 * @return	新たに追加された場合は <tt>true</tt>、すでに指定されている場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean addJsonDataType(String dtype) {
		return ensureJsonDataTypeSet().add(Validations.validNotNull(dtype).toLowerCase());
	}
	
	/**
	 * 指定された Java データ型を、このアイテムに追加する。
	 * @param dtype	追加する Java データ型
	 * @return	新たに追加された場合は <tt>true</tt>、すでに指定されている場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean addJavaDataType(Class<?> dtype) {
		return ensureJavaDataTypeSet().add(Validations.validNotNull(dtype));
	}
	
	/**
	 * 指定された JSON データ型を、このアイテムから削除する。
	 * Java データ型の指定は変更されない。
	 * @param dtype	削除する JSON データ型
	 * @return	削除された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean removeJsonDataType(String dtype) {
		if (dtype != null && _jsonDataTypeSet != null) {
			return _jsonDataTypeSet.remove(dtype.toLowerCase());
		} else {
			return false;
		}
	}
	
	/**
	 * 指定された Java データ型を、このアイテムから削除する。
	 * JSON データ型の指定は変更されない。
	 * @param dtype	削除する Java データ型
	 * @return	削除された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean removeJavaDataType(Class<?> dtype) {
		if (dtype != null && _javaDataTypeSet != null) {
			return _javaDataTypeSet.remove(dtype);
		} else {
			return false;
		}
	}
	
	/**
	 * このアイテムのデータ型を表す文字列表現を取得する。
	 * @return	データ型表現
	 */
	public String getDisplayDataTypeString() {
		return _displayDataTypes;
	}
	
	/**
	 * このアイテムのデータ型を表す文字列表現を設定する。
	 * @param newValue	新しい表現
	 * @return	変更された場合は <tt>true</tt>
	 */
	public boolean setDisplayDataTypeString(String newValue) {
		if (!Objects.equals(newValue, _displayDataTypes)) {
			_displayDataTypes = newValue;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * このアイテムに別のアイテムが関連付けらえているかどうかを判定する。
	 * @return	関連付けられている場合は <tt>true</tt>
	 */
	public boolean hasAttachedItem() {
		return (_attached != null);
	}
	
	/**
	 * このアイテムに関連付けられている別のアイテムを取得する。
	 * @return	関連付けられているアイテム、関連付けられていない場合は <tt>null</tt>
	 */
	public IConversionStructureItem getAttachedItem() {
		return _attached;
	}
	
	/**
	 * このアイテムに別のアイテムを関連付ける。
	 * @param item	関連付けるアイテム、関連付けを解除する場合は <tt>null</tt>
	 */
	public void setAttachedItem(IConversionStructureItem item) {
		_attached = item;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected StringBuilder getTemporaryStringBuffer() {
		if (_bufStrings == null) {
			_bufStrings = new StringBuilder();
			return _bufStrings;
		}
		else {
			_bufStrings.setLength(0);
			return _bufStrings;
		}
	}
	
	protected void refreshDisplayDataTypeString() {
		refreshDisplayDataTypeStringByJson();
	}
	
	protected void refreshDisplayDataTypeStringByJson() {
		if (_jsonDataTypeSet==null || _jsonDataTypeSet.isEmpty()) {
			setDisplayDataTypeString(null);
		} else {
			setDisplayDataTypeString(concatItems(_jsonDataTypeSet, ','));
		}
	}
	
	protected void refreshDisplayDataTypeStringByJava() {
		if (_javaDataTypeSet==null || _javaDataTypeSet.isEmpty()) {
			setDisplayDataTypeString(null);
		} else {
			setDisplayDataTypeString(concatJavaDataTypes(_javaDataTypeSet));
		}
	}
	
	protected Set<String> ensureJsonDataTypeSet() {
		if (_jsonDataTypeSet == null) {
			_jsonDataTypeSet = new LinkedHashSet<String>();
		}
		return _jsonDataTypeSet;
	}
	
	protected Set<Class<?>> ensureJavaDataTypeSet() {
		if (_javaDataTypeSet == null) {
			_javaDataTypeSet = new LinkedHashSet<Class<?>>();
		}
		return _javaDataTypeSet;
	}
	
	protected String concatItems(Collection<?> items, char delimchar) {
		StringBuilder buf = getTemporaryStringBuffer();
		Iterator<?> it = items.iterator();
		if (it.hasNext()) {
			buf.append(it.next());
		}
		for (; it.hasNext(); ) {
			buf.append(delimchar);
			buf.append(it.next());
		}
		return buf.toString();
	}
	
	protected String concatNames(Collection<?> names) {
		return concatItems(names, DEFAULT_NAME_DELIMITER_CHAR);
	}
	
	protected String concatJavaDataTypes(Collection<Class<?>> dataTypes) {
		StringBuilder buf = getTemporaryStringBuffer();
		Iterator<Class<?>> it = dataTypes.iterator();
		if (it.hasNext()) {
			buf.append(it.next().getSimpleName());
		}
		for (; it.hasNext(); ) {
			buf.append(DEFAULT_DATATYPE_DELIMITER_CHAR);
			buf.append(it.next().getSimpleName());
		}
		return buf.toString();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
