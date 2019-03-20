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
 * @(#)SchemaLiteralValue.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaLiteralValue.java	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;
import ssac.aadl.fs.module.schema.io.SchemaXmlUtil;
import ssac.aadl.fs.module.schema.type.SchemaValueFormatException;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeDateTime;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeString;
import ssac.aadl.runtime.util.Objects;

/**
 * スキーマオブジェクトにおける即値を保持するオブジェクト。
 * このオブジェクトの値は、即値として指定された文字列そのものを保持する。
 * 指定されたデータ型に対応する値はキャッシュされており、変換メソッド呼び出しでキャッシュが有効になる。
 * 値が変更された場合はキャッシュがクリアされる。
 * <p>このオブジェクトでは、要素番号ならびに要素の親オブジェクトの設定はすべて無効となる。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaLiteralValue extends SchemaValueObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String	XmlElemValue	= "StaticValue";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 即値として設定された文字列 **/
	private String	_specifiedText;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public SchemaLiteralValue() {
		super();
		_specifiedText = null;
	}
	
	public SchemaLiteralValue(SchemaValueType valueType, String specifiedText) {
		super(valueType);
		_specifiedText = SchemaUtil.emptyStringToNull(specifiedText);
	}

	public SchemaLiteralValue(final SchemaLiteralValue src) {
		super(src);
		_specifiedText = src._specifiedText;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public void setElementNo(int newElemNo) {
		super.setElementNo(SchemaElementValue.INVALID_ELEMENT_NO);
	}

	@Override
	public boolean updateElementNo(int newElemNo) {
		return super.updateElementNo(SchemaElementValue.INVALID_ELEMENT_NO);
	}

	@Override
	public void setParentObject(SchemaObject newParent) {
		super.setParentObject(null);
	}

	@Override
	public boolean updateParentObject(SchemaObject newParent) {
		return super.updateParentObject(null);
	}
	
	public String getSpecifiedText() {
		return _specifiedText;
	}
	
	public void setSpecifiedText(String newText) {
		_specifiedText = newText;
		super.setValue(null);
	}
	
	public boolean updateSpecifiedText(String newText) {
		if (Objects.equals(newText, _specifiedText))
			return false;
		//--- modified
		_specifiedText = newText;
		super.setValue(null);
		return true;
	}

	/**
	 * データ型に対応する値のキャッシュをクリアする。
	 */
	public void clearRealValueCache() {
		super.setValue(null);
	}

	/**
	 * 現在の指定値が、現在のデータ型に対応する値に変換可能かを判定する。
	 * 変換不可能な場合、データ型に対応する値のキャッシュは更新されない。
	 * @return
	 */
	public boolean isValidSpecifiedValue() {
		try {
			Object realValue = getValueType().convertFromString(_specifiedText);
			super.setValue(realValue);
			return true;
		} catch (SchemaValueFormatException ex) {
			return false;
		}
	}

	/**
	 * データ型に対応する実際の値オブジェクトを取得する。
	 * 実際の値への変換が発生した際、変換に失敗した場合はキャッシュがクリアされる。
	 * @return	データ型に対応する実際の値オブジェクト
	 * @throws SchemaValueFormatException	値の変換に失敗した場合
	 */
	public Object getRealValue() throws SchemaValueFormatException
	{
		Object realValue = super.getValue();
		if (realValue != null) {
			return realValue;
		}
		
		if (_specifiedText == null) {
			return null;
		}
		
		realValue = getValueType().convertFromString(_specifiedText);
		super.setValue(realValue);
		return realValue;
	}

	@Override
	public void setValueType(SchemaValueType newValueType) {
		super.setValueType(newValueType);
		super.setValue(null);
	}

	@Override
	public boolean updateValueType(SchemaValueType newValueType) {
		if (super.updateValueType(newValueType)) {
			super.setValue(null);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Object getValue()
	{
		try {
			return getRealValue();
		} catch (SchemaValueFormatException ex) {
			throw new IllegalStateException();
		}
	}

	@Override
	public void setValue(Object newValue) {
		super.setValue(null);
	}

	@Override
	public boolean updateValue(Object newValue) {
		return super.updateValue(null);
	}

	@Override
	public String toVariableNameString() {
		String strName  = getName();
		String strValue = _specifiedText;

		StringBuilder sb = new StringBuilder();
		SchemaValueType vtype = getValueType();
		sb.append('(');
		sb.append(vtype.getTypeName());
		sb.append(')');
		
		if (strName != null && !strName.isEmpty()) {
			sb.append(strName);
			sb.append('=');
		}

		if (strValue == null) {
			sb.append("null");
		}
		else if (SchemaValueTypeDateTime.instance.equals(vtype)) {
			// datetime
			sb.append('\'');
			sb.append(strValue);
			sb.append('\'');
		}
		else if (SchemaValueTypeString.instance.equals(vtype)) {
			// string
			sb.append('\"');
			sb.append(strValue);
			sb.append('\"');
		}
		else {
			// others
			sb.append(strValue);
		}
		return sb.toString();
	}

	//------------------------------------------------------------
	// Read interfaces for XML
	//------------------------------------------------------------

	@Override
	protected boolean xmlParseChildElement(SchemaConfigReader reader, XMLStreamReader xmlReader, String tagName)
	throws XMLStreamException
	{
		if (XmlElemValue.equals(tagName)) {
			String strValue = xmlReader.getElementText();
			if (strValue == null || strValue.isEmpty()) {
				setSpecifiedText(null);
			} else {
				setSpecifiedText(strValue);
				try {
					getRealValue();
				} catch (SchemaValueFormatException ex) {
					// illegal value format
					String msg = String.format("<%s> : <%s> value cannot cast to \'%s\' : value=\"%s\"",
							getXmlElementName(), tagName, getValueType().toString(), strValue);
					throw new XMLStreamException(msg, xmlReader.getLocation());
				}
			}

			SchemaXmlUtil.xmlValidEndElement(xmlReader, tagName);
			return true;
		}
		
		return super.xmlParseChildElement(reader, xmlReader, tagName);
	}

	//------------------------------------------------------------
	// Write interfaces for XML
	//------------------------------------------------------------

	@Override
	protected void xmlWriteBodyElements(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		super.xmlWriteBodyElements(writer, xmlWriter);
		
		//--- static value
		Object value = getValue();
		if (value != null) {
			xmlWriter.writeStartElement(XmlElemValue);
			String strValue = _specifiedText;
			if (strValue != null) {
				xmlWriter.writeCharacters(strValue);
			}
			xmlWriter.writeEndElement();
		}
	}

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = 31 * h + (_specifiedText==null ? 0 : _specifiedText.hashCode());
		return h;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void appendParamString(StringBuilder buffer) {
		super.appendParamString(buffer);
		
		buffer.append(", specifiedText=");
		buffer.append(_specifiedText);
	}

	@Override
	protected boolean equalFields(Object obj) {
		if (!super.equalFields(obj))
			return false;
		
		SchemaLiteralValue aData = (SchemaLiteralValue)obj;
		
		if (!Objects.equals(aData._specifiedText, this._specifiedText))
			return false;
		
		// equal all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
