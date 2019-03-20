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
 * @(#)AbSchemaValue.java	3.2.2	2015/10/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbSchemaValue.java	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeManager;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeString;
import ssac.aadl.runtime.util.Objects;

/**
 * 汎用フィルタ定義における値を保持するオブジェクトインタフェースの共通実装。
 * <p>なお、値は保存対象とはならない。
 * 
 * @version 3.2.2
 * @since 3.2.0
 */
public abstract class AbSchemaValue extends AbSchemaObject implements SchemaValue
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String	XmlAttrValueType	= "valueType";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このオブジェクトのデータ型 **/
	private SchemaValueType	_valueType;
	/** このオブジェクトの値 **/
	private Object			_value;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のパラメータで、新しいインスタンスを生成する。
	 */
	public AbSchemaValue() {
		super();
		_valueType = SchemaValueTypeString.instance;
		_value     = null;
	}

	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param name
	 * @param desc
	 */
	
	public AbSchemaValue(String name, String desc, SchemaValueType valueType, Object value) {
		super(name, desc);
		_valueType = SchemaValueTypeManager.ensureValueTypeWithDefault(valueType);
		_value     = value;
	}

	/**
	 * 指定されたオブジェクトの内容をコピーした、新しいインスタンスを生成する。
	 * @param src	コピー元のオブジェクト
	 */
	public AbSchemaValue(final SchemaValue src) {
		super(src);
		_valueType = src.getValueType();
		_value     = src.getValue();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトのデータ型を取得する。
	 * @return	データ型(<tt>null</tt> 以外)
	 */
	@Override
	public SchemaValueType getValueType() {
		return _valueType;
	}

	/**
	 * このオブジェクトのデータ型を設定する。
	 * <em>newValueType</em> が <tt>null</tt> の場合、文字列型が設定される。
	 * @param newValueType	新しいデータ型
	 */
	@Override
	public void setValueType(SchemaValueType newValueType) {
		_valueType = SchemaValueTypeManager.ensureValueTypeWithDefault(newValueType);
	}

	/**
	 * このオブジェクトのデータ型を更新する。
	 * <em>newValueType</em> が <tt>null</tt> の場合、文字列型が設定される。
	 * @param newValueType	新しいデータ型
	 * @return	データ型が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean updateValueType(SchemaValueType newValueType) {
		newValueType = SchemaValueTypeManager.ensureValueTypeWithDefault(newValueType);
		if (newValueType.equals(_valueType))
			return false;
		//--- modified
		_valueType = newValueType;
		return true;
	}

	/**
	 * このオブジェクトが値を持つかどうかを判定する。
	 * @return	このオブジェクトの値が <tt>null</tt> でない場合は <tt>true</tt>
	 */
	public boolean hasValue() {
		return (_value != null);
	}

	/**
	 * このオブジェクトが保持する値を取得する。
	 * @return	このオブジェクトが持つ値
	 */
	@Override
	public Object getValue() {
		return _value;
	}

	/**
	 * このオブジェクトに新しい値を設定する。
	 * @param newValue	新しい値
	 */
	@Override
	public void setValue(Object newValue) {
		_value = newValue;
	}

	/**
	 * このオブジェクトに新しい値を設定する。
	 * @param newValue	新しい値
	 * @return	このオブジェクトの値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean updateValue(Object newValue) {
		// (3.2.2)BigDecimal でも正しく比較する
		if (Objects.valueEquals(newValue, _value))
			return false;
		//--- modified
		_value = newValue;
		return true;
	}

	//------------------------------------------------------------
	// Implements AbSchemaObject interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトを示す変数名表現を返す。
	 * @return	このオブジェクトを示す変数名表現
	 */
	@Override
	public String toVariableNameString() {
		return (toNameString() + SchemaUtil.VALUETYPE_SEPARATOR_CHAR + _valueType.getTypeName());
	}

	//------------------------------------------------------------
	// Implements java.lang.Object interfaces
	//------------------------------------------------------------

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = 31 * h + (_valueType==null ? 0 : _valueType.hashCode());
		h = 31 * h + (_value==null ? 0 : _value.hashCode());
		return h;
	}

	//------------------------------------------------------------
	// Read interfaces for XML
	//------------------------------------------------------------

	@Override
	protected boolean xmlParseAttributeValue(SchemaConfigReader reader, XMLStreamReader xmlReader, String attrName, String attrValue)
	throws XMLStreamException
	{
		// value type
		if (XmlAttrValueType.equals(attrName)) {
			if (attrValue == null || attrValue.isEmpty()) {
				// default value type
				setValueType(null);
			}
			else {
				try {
					SchemaValueType vtype = SchemaValueTypeManager.valueTypeOf(attrValue);
					setValueType(vtype);
				} catch (Throwable ex) {
					String msg = String.format("<%s> : \"%s\" attribute value is wrong : value=\"%s\"", getXmlElementName(), attrName, attrValue);
					throw new XMLStreamException(msg, xmlReader.getLocation(), ex);
				}
			}
			return true;
		}
		
		return super.xmlParseAttributeValue(reader, xmlReader, attrName, attrValue);
	}

	//------------------------------------------------------------
	// Write interfaces for XML
	//------------------------------------------------------------

	@Override
	protected void xmlWriteStartAttributes(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		super.xmlWriteStartAttributes(writer, xmlWriter);

		if (_valueType != null) {
			xmlWriter.writeAttribute(XmlAttrValueType, _valueType.getTypeName());
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void appendParamString(StringBuilder buffer) {
		super.appendParamString(buffer);
		
		buffer.append(", valueType=");
		buffer.append(_valueType);
		
		buffer.append(", value=");
		buffer.append(_value);
	}

	@Override
	protected boolean equalFields(Object obj) {
		if (!super.equalFields(obj))
			return false;
		
		AbSchemaValue aData = (AbSchemaValue)obj;
		
		if (!Objects.equals(aData._valueType, this._valueType))
			return false;

		// (3.2.2) BigDecimal の値でも比較できるようにする
		if (!Objects.valueEquals(aData._value, this._value))
			return false;
		
		// equal all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
