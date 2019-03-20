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
 * @(#)SchemaOutputCsvDataField	3.2.1	2015/07/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaOutputCsvDataField	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.table;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.SchemaObject;
import ssac.aadl.fs.module.schema.SchemaUtil;
import ssac.aadl.fs.module.schema.SchemaValueObject;
import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;
import ssac.aadl.fs.module.schema.io.SchemaXmlUtil;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.runtime.util.Objects;

/**
 * CSV 形式での出力列データのスキーマを保持するクラス。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaOutputCsvDataField extends SchemaCsvDataField
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String	XmlElemTarget	= "TargetValue";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** この出力フィールドに出力する値へのリンク **/
	private SchemaElementValue	_targetValue;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public SchemaOutputCsvDataField() {
		super();
	}
	
	public SchemaOutputCsvDataField(SchemaValueType valueType) {
		super(valueType);
	}

	public SchemaOutputCsvDataField(String name, SchemaValueType valueType, Object value) {
		super(name, valueType, value);
	}
	
	public SchemaOutputCsvDataField(final SchemaOutputCsvDataField src) {
		super(src);
		_targetValue = src._targetValue;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasTargetValue() {
		return (_targetValue != null);
	}
	
	public SchemaElementValue getTargetValue() {
		return _targetValue;
	}
	
	public void setTargetValue(SchemaElementValue newTarget) {
		_targetValue = newTarget;
	}
	
	public boolean updateTargetValue(SchemaElementValue newTarget) {
		if (newTarget == _targetValue)
			return false;
		//--- modified
		_targetValue = newTarget;
		return true;
	}

	@Override
	public String toNameString() {
		String name = getName();
		if (name != null && !name.isEmpty()) {
			return name;
		}
		else if (_targetValue != null) {
			return _targetValue.toNameString();
		}
		else {
			return "";
		}
	}

	@Override
	public String toVariableNameString() {
		SchemaObject elemParent = getParentObject();
		if (elemParent == null) {
			return toLocalVariableNameString();
		}
		else {
			StringBuilder buffer = new StringBuilder();
			if (elemParent != null) {
				buffer.append(elemParent.toVariableNameString());
				buffer.append(SchemaUtil.CLASS_SEPARATOR_CHAR);
			}
			buffer.append(toLocalVariableNameString());
			return buffer.toString();
		}
	}
	
	@Override
	public String toLocalVariableNameString() {
		int elemNo = getElementNo();
		if (elemNo < 0) {
			// no element number
			return String.format("[]%s <= %s", toNameString(), (_targetValue==null ? "" : _targetValue.toVariableNameString()));
		} else {
			// with element number
			return String.format("[%d]%s <= %s", elemNo, toNameString(), (_targetValue==null ? "" : _targetValue.toVariableNameString()));
		}
	}

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = 31 * h + (_targetValue==null ? 0 : _targetValue.hashCode());
		return h;
	}

	//------------------------------------------------------------
	// Read interfaces for XML
	//------------------------------------------------------------

	@Override
	protected boolean xmlParseChildElement(SchemaConfigReader reader, XMLStreamReader xmlReader, String tagName)
	throws XMLStreamException
	{
		if (XmlElemTarget.equals(tagName)) {
			//--- 次のタグへ
			xmlReader.nextTag();
			
			// 値の判定
			SchemaValueObject targetValue = SchemaXmlUtil.createObjectInstanceByElementTagName(SchemaValueObject.class, xmlReader, tagName, xmlReader.getLocalName());
			if (targetValue == null) {
				// Unsupported element
				String msg = String.format("<%s> : <%s> element is not supported.", tagName, xmlReader.getLocalName());
				throw new XMLStreamException(msg, xmlReader.getLocation());
			}
			
			// 値の取得
			targetValue.readFromXml(reader);
			setTargetValue(targetValue);
			//--- 次のタグへ
			xmlReader.nextTag();
			
			//--- 終端チェック
			SchemaXmlUtil.xmlValidEndElement(xmlReader, XmlElemTarget);
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
		
		// write target value object
		if (_targetValue != null) {
			xmlWriter.writeStartElement(XmlElemTarget);
			_targetValue.writeToXml(writer);
			xmlWriter.writeEndElement();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void appendParamString(StringBuilder buffer) {
		super.appendParamString(buffer);
		
		buffer.append(", targetValue=");
		buffer.append(_targetValue==null ? "null" : _targetValue.toParamString());
	}

	@Override
	protected boolean equalFields(Object obj) {
		if (!super.equalFields(obj))
			return false;
		
		SchemaOutputCsvDataField aData = (SchemaOutputCsvDataField)obj;
		
		if (!Objects.equals(aData._targetValue, this._targetValue))
			return false;
		
		// equal all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
