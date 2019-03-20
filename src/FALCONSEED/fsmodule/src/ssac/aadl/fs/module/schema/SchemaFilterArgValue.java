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
 * @(#)SchemaFilterArgValue.java	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.FilterArgType;
import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;

/**
 * スキーマオブジェクトにおけるフィルター定義引数の値を保持するクラス。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaFilterArgValue extends SchemaValueObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String	XmlAttrArgType	= "argType";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** フィルタ定義引数の属性 **/
	private FilterArgType	_argType;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public SchemaFilterArgValue() {
		super();
		_argType  = null;
	}
	
	public SchemaFilterArgValue(FilterArgType argType, String desc) {
		this(-1, argType, desc);
	}
	
	public SchemaFilterArgValue(int argIndex, FilterArgType argType, String desc) {
		this(argIndex, argType, desc, null);
	}
	
	public SchemaFilterArgValue(int argIndex, FilterArgType argType, String desc, Object value) {
		super();
		setDescription(desc);
		setValue(value);
		setArgumentIndex(argIndex);
		_argType = argType;
	}
	
	public SchemaFilterArgValue(final SchemaFilterArgValue src) {
		super(src);
		_argType  = src._argType;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isValidArgumentIndex() {
		return (getElementNo() < 0 ? false : true);
	}
	
	public int getArgumentIndex() {
		return (isValidArgumentIndex() ? (getElementNo() - 1) : SchemaElementValue.INVALID_ELEMENT_NO);
	}
	
	public void setArgumentIndex(int newArgIndex) {
		setElementNo(newArgIndex < 0 ? SchemaElementValue.INVALID_ELEMENT_NO : (newArgIndex+1));
	}
	
	public boolean updateArgumentIndex(int newArgIndex) {
		if (newArgIndex >= 0)
			newArgIndex += 1;
		return updateElementNo(newArgIndex);
	}
	
	public FilterArgType getArgumentType() {
		return _argType;
	}
	
	public void setArgumentType(FilterArgType newArgType) {
		_argType = newArgType;
	}
	
	public boolean updateArgumentType(FilterArgType newArgType) {
		if (newArgType == _argType)
			return false;
		//--- modified
		_argType = newArgType;
		return true;
	}

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = 31 * h + (_argType==null ? 0 : _argType.hashCode());
		return h;
	}

	@Override
	public String toNameString() {
		StringBuilder buffer = new StringBuilder();
		
		//--- arg no
		if (isValidElementNo()) {
			buffer.append("($");
			buffer.append(getArgumentIndex() + 1);
			buffer.append(')');
		} else {
			buffer.append("()");
		}
		
		//--- arg type
		if (_argType == null) {
			buffer.append("[]");
		} else {
			buffer.append(_argType);
		}
		
		//--- name
		if (hasName()) {
			buffer.append(getName());
		} else if (buffer.length() == 0) {
			buffer.append(getClass().getName());
			buffer.append('@');
			buffer.append(Integer.toHexString(getInstanceId()));
		}
		
		return buffer.toString();
	}

	@Override
	public String toVariableNameString() {
		StringBuilder buffer = new StringBuilder();
		
		if (hasParentObject()) {
			buffer.append(getParentObject().toVariableNameString());
			buffer.append(SchemaUtil.CLASS_SEPARATOR_CHAR);
		}

		buffer.append(toNameString());
		
		buffer.append(SchemaUtil.VALUETYPE_SEPARATOR_CHAR);
		buffer.append(getValueType().getTypeName());
		return buffer.toString();
	}

	//------------------------------------------------------------
	// Public interfaces for XML
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Read interfaces for XML
	//------------------------------------------------------------

	@Override
	protected boolean xmlParseAttributeValue(SchemaConfigReader reader, XMLStreamReader xmlReader, String attrName, String attrValue)
	throws XMLStreamException
	{
		if (XmlAttrArgType.equals(attrName)) {
			if (attrValue == null || attrValue.isEmpty()) {
				// no argument type
				setArgumentType(null);
			} else {
				// check argument type
				FilterArgType argType = FilterArgType.fromName(attrValue);
				if (argType == null) {
					String msg = String.format("<%s> : \"%s\" attribute value is not Filter argument type : value=\"%s\"", getXmlElementName(), attrName, attrValue);
					throw new XMLStreamException(msg, xmlReader.getLocation());
				}
				setArgumentType(argType);
			}
			
			// Module argument type
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
		
		if (_argType != null) {
			xmlWriter.writeAttribute(XmlAttrArgType, _argType.toString());
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
//	protected String createFilterArgNameString() {
//		int argIndex = getElementNo();
//		if (_argType == null) {
//			return String.format("($%d)", (argIndex + 1));
//		} else {
//			return String.format("($%d)%s", (argIndex + 1), _argType.toString());
//		}
//	}

	@Override
	protected void appendParamString(StringBuilder buffer) {
		super.appendParamString(buffer);
		
		buffer.append(", argType=");
		buffer.append(_argType);
	}

	@Override
	protected boolean equalFields(Object obj) {
		if (!super.equalFields(obj))
			return false;
		
		SchemaFilterArgValue aValue = (SchemaFilterArgValue)obj;
		
		if (aValue._argType != this._argType)
			return false;
		
		// equal all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
