/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
