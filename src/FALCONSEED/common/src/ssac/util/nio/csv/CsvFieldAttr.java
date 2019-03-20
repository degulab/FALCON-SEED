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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CsvFieldAttr.java	1.17	2010/11/19
 *     - create by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio.csv;

/**
 * CSV フィールドの属性情報。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public final class CsvFieldAttr
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** フィールドの名前(未定義なら <tt>null</tt>  **/
	private String			_name;
	/** フィールド値の変換可能なクラス(デフォルトは <code>String</code>) **/
	private Class<?>		_datatype;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFieldAttr() {
		this(null, null);
	}
	
	public CsvFieldAttr(String fieldName) {
		this(fieldName, null);
	}
	
	public CsvFieldAttr(String fieldName, Class<?> datatype) {
		this._name = filterFieldName(fieldName);
		this._datatype = datatype;
	}
	
	public CsvFieldAttr(final CsvFieldAttr attr) {
		this._name = attr._name;
		this._datatype = attr._datatype;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された文字列をフィールド名として利用可能な文字列に整形する。
	 * フィールド名は、文字列の前後の空白を除去し、文字列の長さが 1 以上の
	 * ものをフィールド名として利用できる。
	 * @return	整形済みのフィールド名を返す。フィールド名として有効では
	 * 			ない場合は <tt>null</tt> を返す。
	 */
	static public final String filterFieldName(String fieldName) {
		if (fieldName != null) {
			String strName = fieldName.trim();
			if (strName.length() > 0) {
				return strName;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * CSV フィールド名を取得する。
	 * @return	有効な CSV フィールド名を返す。
	 * 			CSV フィールド名が設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getFieldName() {
		return _name;
	}

	/**
	 * CSV フィールド名を設定する。
	 * このメソッドでは、指定された文字列は {@link #filterFieldName(String)} によって
	 * 整形された後に、このオブジェクトに設定される。
	 * @param fieldName	CSV フィールド名とする文字列。
	 */
	public void setFieldName(String fieldName) {
		this._name = filterFieldName(fieldName);
	}

	/**
	 * CSV フィールドのデータ型を取得する。
	 * @return	CSV フィールドのデータ型を示すクラスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public Class<?> getDataType() {
		return _datatype;
	}

	/**
	 * CSV フィールドのデータ型を設定する。
	 * @param type	データ型とするクラス。
	 * 				<tt>null</tt> を指定した場合は未定義となる。
	 */
	public void setDataType(Class<?> type) {
		this._datatype = type;
	}

	/**
	 * このオブジェクトの属性情報を、指定されたオブジェクトにコピーする。
	 * @param attr	CSV属性情報を受け取るバッファ
	 */
	public void getAttributes(CsvFieldAttr attr) {
		attr._name     = this._name;
		attr._datatype = this._datatype;
	}

	/**
	 * このオブジェクトの属性情報を、指定された属性情報に設定する。
	 * @param attr	CSVフィールド属性
	 */
	public void setAttributes(final CsvFieldAttr attr) {
		this._name     = attr._name;
		this._datatype = attr._datatype;
	}

	@Override
	public int hashCode() {
		int result = 1;
		//--- name
		result = 31 * result + (_name==null ? 0 : _name.hashCode());
		//--- type
		result = 31 * result + (_datatype==null ? 0 : _datatype.hashCode());
        //---
        return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof CsvFieldAttr) {
			CsvFieldAttr attr = (CsvFieldAttr)obj;
			if ((attr._name==this._name) || (attr._name!=null && attr._name.equals(this._name))) {
				if ((attr._datatype==this._datatype) || (attr._datatype!=null && attr._datatype.equals(this._datatype))) {
					return true;
				}
			}
		}
		
		// not equal
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("[name=");
		if (_name == null) {
			sb.append("null");
		} else {
			sb.append("\"");
			sb.append(_name);
			sb.append("\"");
		}
		sb.append(", datatype=");
		if (_datatype==null) {
			sb.append("null");
		} else {
			sb.append("(");
			sb.append(_datatype.getName());
			sb.append(")");
		}
		sb.append("]");
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
