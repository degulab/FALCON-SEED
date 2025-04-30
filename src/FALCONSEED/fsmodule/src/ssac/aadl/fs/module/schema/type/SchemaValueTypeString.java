/*
 * @(#)SchemaValueTypeString.java	3.2.1	2015/07/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaValueTypeString.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.type;


/**
 * 汎用フィルタ定義における、文字列データ型を示すクラス。
 * このオブジェクトは、不変オブジェクトである。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaValueTypeString extends SchemaValueType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String TYPENAME_STRING	= "String";

	/** このオブジェクトの唯一のインスタンス **/
	static public final SchemaValueTypeString instance = new SchemaValueTypeString();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaValueTypeString() {
		super(TYPENAME_STRING);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このデータ型に相当する JAVA クラスを取得する。
	 * @return	<code>String</code> クラスオブジェクト
	 */
	@Override
	public Class<String> getJavaClass() {
		return String.class;
	}

	/**
	 * 指定された文字列をそのまま返す。
	 * @param value	値を示す文字列
	 * @return	文字列から変換された値のオブジェクト
	 */
	@Override
//	public String valueOf(String value)
	public String convertFromString(String value) throws SchemaValueFormatException
	{
		return value;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
