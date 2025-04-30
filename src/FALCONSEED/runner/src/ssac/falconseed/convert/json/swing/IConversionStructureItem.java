/*
 * @(#)IConversionStructureItem.java	3.4.0	2020/03/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

/**
 * JSON-CSV 変換のための、変換構造アイテム・モデルのインタフェース。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public interface IConversionStructureItem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public char		DEFAULT_NAME_DELIMITER_CHAR	= '_';
	static public String	DEFAULT_NAME_DELIMITER_STR	= ""+DEFAULT_NAME_DELIMITER_CHAR;
	
	static public char		DEFAULT_DATATYPE_DELIMITER_CHAR	= ',';
	static public String	DEFAULT_DATATYPE_DELIMITER_STR	= ""+DEFAULT_DATATYPE_DELIMITER_CHAR;
	
	static public String	JSON_DATATYPE_BOOLEAN	= "boolean";
	static public String	JSON_DATATYPE_NUMBER	= "number";
	static public String	JSON_DATATYPE_STRING	= "string";
	static public String	JSON_DATATYPE_ARRAY		= "array";
	static public String	JSON_DATATYPE_OBJECT	= "object";
	static public String	JSON_DATATYPE_NAME		= "name";

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	/**
	 * 現在のアイテムの内容で、表示用の値を更新する。
	 */
	public void refreshDisplayValues();
	
	/**
	 * このアイテムの位置を表すインデックスを取得する。
	 * @return	アイテムインデックス、設定されていない場合は (-1)
	 */
	public int getItemIndex();
	
	/**
	 * このアイテムの位置を表すインデックスを設定する。
	 * @param newIndex	設定するアイテムインデックス、無効とする場合は (-1)
	 */
	public void setItemIndex(int newIndex);
	
	/**
	 * このアイテムの行見出しとして表示する文字列を取得する。
	 * @return	行見出しとする文字列、指定しない場合は <tt>null</tt>
	 */
	public String getRowName();
	
	/**
	 * このアイテムの表示名を取得する。
	 * @return	表示名
	 */
	public String getDisplayName();
	
	/**
	 * このアイテムの標準名を取得する。
	 * @return	標準名、設定されていない場合は <tt>null</tt>
	 */
	public String getDefaultName();
	
	/**
	 * このアイテムに標準名を設定する。
	 * @param newName	設定する名前、未設定とする場合は <tt>null</tt>
	 */
	public void setDefaultName(String newName);
	
	/**
	 * このアイテムのユーザー定義名を取得する。
	 * @return	ユーザー定義名、設定されていない場合は <tt>null</tt>
	 */
	public String getCustomName();
	
	/**
	 * このアイテムにユーザー定義名を設定する。
	 * @param newName	設定する名前、未設定とする場合は <tt>null</tt>
	 */
	public void setCustomName(String newName);
	
	/**
	 * このアイテムのデータ型を表す文字列表現を取得する。
	 * @return	データ型表現
	 */
	public String getDisplayDataTypeString();
	
	/**
	 * このアイテムに別のアイテムが関連付けらえているかどうかを判定する。
	 * @return	関連付けられている場合は <tt>true</tt>
	 */
	public boolean hasAttachedItem();
	
	/**
	 * このアイテムに関連付けられている別のアイテムを取得する。
	 * @return	関連付けられているアイテム、関連付けられていない場合は <tt>null</tt>
	 */
	public IConversionStructureItem getAttachedItem();
	
	/**
	 * このアイテムに別のアイテムを関連付ける。
	 * @param item	関連付けるアイテム、関連付けを解除する場合は <tt>null</tt>
	 */
	public void setAttachedItem(IConversionStructureItem item);
}
