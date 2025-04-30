/*
 * @(#)SchemaValueTypeBoolean.java	3.2.1	2015/07/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaValueTypeBoolean.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.type;


/**
 * 汎用フィルタ定義における、真偽値データ型を示すクラス。
 * このオブジェクトは、不変オブジェクトである。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaValueTypeBoolean extends SchemaValueType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String TYPENAME_BOOLEAN	= "Boolean";

	/** このオブジェクトの唯一のインスタンス **/
	static public final SchemaValueTypeBoolean instance = new SchemaValueTypeBoolean();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaValueTypeBoolean() {
		super(TYPENAME_BOOLEAN);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このデータ型に相当する JAVA クラスを取得する。
	 * @return	<code>Boolean</code> クラスオブジェクト
	 */
	@Override
	public Class<Boolean> getJavaClass() {
		return Boolean.class;
	}

	/**
	 * 指定された文字列から、このデータ型に応じた値に変換する。
	 * 文字列が <tt>null</tt> もしくは空文字列の場合、このメソッドは <tt>null</tt> を返す。
	 * @param value	値を示す文字列
	 * @return	文字列から変換された値のオブジェクト
	 * @throws SchemaValueFormatException	変換に失敗した場合
	 */
	@Override
//	public Boolean valueOf(String value) throws SchemaValueFormatException
	public Boolean convertFromString(String value) throws SchemaValueFormatException
	{
		if (value != null && !value.isEmpty()) {
			return Boolean.valueOf(value);
		} else {
			return null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
