/*
 * @(#)SchemaValue.java	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema;

import ssac.aadl.fs.module.schema.type.SchemaValueType;

/**
 * 汎用フィルタ定義における値を保持するオブジェクトの共通インタフェース。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public interface SchemaValue extends SchemaObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトのデータ型を取得する。
	 * @return	データ型(<tt>null</tt> 以外)
	 */
	public SchemaValueType getValueType();

	/**
	 * このオブジェクトのデータ型を設定する。
	 * <em>newValueType</em> が <tt>null</tt> の場合、文字列型が設定される。
	 * @param newValueType	新しいデータ型
	 */
	public void setValueType(SchemaValueType newValueType);

	/**
	 * このオブジェクトのデータ型を更新する。
	 * <em>newValueType</em> が <tt>null</tt> の場合、文字列型が設定される。
	 * @param newValueType	新しいデータ型
	 * @return	データ型が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean updateValueType(SchemaValueType newValueType);

	/**
	 * このオブジェクトが値を持つかどうかを判定する。
	 * @return	このオブジェクトの値が <tt>null</tt> でない場合は <tt>true</tt>
	 */
	public boolean hasValue();

	/**
	 * このオブジェクトが保持する値を取得する。
	 * @return	このオブジェクトが持つ値
	 */
	public Object getValue();

	/**
	 * このオブジェクトに新しい値を設定する。
	 * @param newValue	新しい値
	 */
	public void setValue(Object newValue);

	/**
	 * このオブジェクトに新しい値を設定する。
	 * @param newValue	新しい値
	 * @return	このオブジェクトの値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean updateValue(Object newValue);
}
