/*
 * @(#)SchemaValueTypeDecimal.java	3.2.1	2015/07/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaValueTypeDecimal.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.type;

import java.math.BigDecimal;

/**
 * 汎用フィルタ定義における、実数値データ型を示す基本クラス。
 * このオブジェクトは、不変オブジェクトである。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaValueTypeDecimal extends SchemaValueType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String TYPENAME_DECIMAL = "Decimal";

	/** このオブジェクトの唯一のインスタンス **/
	static public final SchemaValueTypeDecimal instance = new SchemaValueTypeDecimal();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaValueTypeDecimal() {
		super(TYPENAME_DECIMAL);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このデータ型に相当する JAVA クラスを取得する。
	 * @return	<code>BigDecimal</code> クラスオブジェクト
	 */
	@Override
	public Class<BigDecimal> getJavaClass() {
		return BigDecimal.class;
	}

	/**
	 * 指定された文字列から、このデータ型に応じた値に変換する。
	 * 文字列が <tt>null</tt> もしくは空文字列の場合、このメソッドは <tt>null</tt> を返す。
	 * @param value	値を示す文字列
	 * @return	文字列から変換された値のオブジェクト
	 * @throws SchemaValueFormatException	変換に失敗した場合
	 */
	@Override
//	public BigDecimal valueOf(String value) throws SchemaValueFormatException
	public BigDecimal convertFromString(String value) throws SchemaValueFormatException
	{
		if (value != null && !value.isEmpty()) {
			try {
				return new BigDecimal(value);
			}
			catch (NumberFormatException ex) {
				throw new SchemaValueFormatException(this, value, ex);
			}
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
