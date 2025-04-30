/*
 * @(#)GenericSchemaValueType.java	3.2.0	2015/06/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.type;


/**
 * 汎用フィルタ定義における値データ型に基づく、文字列から値オブジェクトへの変換に失敗したことを示す例外。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaValueFormatException extends Exception
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 5982412579740703861L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 例外発生元のデータ型オブジェクト **/
	private SchemaValueType	_valueType;
	/** 例外の発生要因となった文字列 **/
	private String			_strValue;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaValueFormatException(SchemaValueType valueType, String strValue) {
		super();
		_valueType = valueType;
		_strValue  = strValue;
	}
	
	public SchemaValueFormatException(SchemaValueType valueType, String strValue, String message) {
		super(message);
		_valueType = valueType;
		_strValue  = strValue;
	}
	
	public SchemaValueFormatException(SchemaValueType valueType, String strValue, Throwable cause) {
		super(cause);
		_valueType = valueType;
		_strValue  = strValue;
	}
	
	public SchemaValueFormatException(SchemaValueType valueType, String strValue, String message, Throwable cause) {
		super(message, cause);
		_valueType = valueType;
		_strValue  = strValue;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public SchemaValueType getValueType() {
		return _valueType;
	}
	
	public String getStringValue() {
		return _strValue;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(" : ");
		sb.append("valueType=");
		sb.append(_valueType==null ? "null" : _valueType.toString());
		sb.append(", value=");
		if (_strValue==null) {
			sb.append("null");
		} else {
			sb.append('\"');
			sb.append(_strValue);
			sb.append('\"');
		}
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
