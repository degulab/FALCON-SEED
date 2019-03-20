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
 * @(#)SchemaBinaryOperation.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.exp;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.SchemaUtil;
import ssac.aadl.fs.module.schema.SchemaValueObject;
import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;
import ssac.aadl.fs.module.schema.io.SchemaXmlUtil;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeManager;
import ssac.aadl.runtime.util.Objects;

/**
 * 汎用フィルタ定義における、二項演算定義データの基本クラス。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public abstract class SchemaBinaryOperationData extends SchemaValueObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String	XmlAttrOperator		= "operator";
	static protected final String	XmlAttrResultType	= "resultType";
	static protected final String	XmlElemLeftOperand	= "leftOperand";
	static protected final String	XmlElemRightOperand	= "rightOperand";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 演算子 **/
	private SchemaValueType		_resultType;
	private String				_operator;
	private SchemaElementValue	_leftOperand;
	private SchemaElementValue	_rightOperand;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaBinaryOperationData() {
		this(null, null, null);
	}
	
	public SchemaBinaryOperationData(String operator) {
		this(operator, null, null);
	}
	
	public SchemaBinaryOperationData(String operator, SchemaElementValue leftValue, SchemaElementValue rightValue) {
		super();
		_resultType   = null;
		_operator     = operator;
		_leftOperand  = leftValue;
		_rightOperand = rightValue;
	}
	
	public SchemaBinaryOperationData(final SchemaBinaryOperationData src) {
		super(src);
		_resultType   = src._resultType;
		_operator     = src._operator;
		_leftOperand  = src._leftOperand;
		_rightOperand = src._rightOperand;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public SchemaValueType getResultType() {
		return _resultType;
	}
	
	public String getOperator() {
		return _operator;
	}
	
	public void setOperator(String newOperator) {
		_operator = newOperator;
	}
	
	public boolean updateOperator(String newOperator) {
		if (Objects.equals(newOperator, _operator))
			return false;
		//--- modified
		_operator = newOperator;
		return true;
	}
	
	public boolean hasLeftOperand() {
		return (_leftOperand != null);
	}
	
	public boolean hasRightOperand() {
		return (_rightOperand != null);
	}
	
	public SchemaValueType getLeftValueType() {
		return (_leftOperand==null ? null : _leftOperand.getValueType());
	}
	
	public SchemaValueType getRightValueType() {
		return (_rightOperand==null ? null : _rightOperand.getValueType());
	}
	
	public SchemaElementValue getLeftOperand() {
		return _leftOperand;
	}
	
	public SchemaElementValue getRightOperand() {
		return _rightOperand;
	}
	
	public void setLeftOperand(SchemaElementValue newOperand) {
		_leftOperand = newOperand;
	}
	
	public void setRightOperand(SchemaElementValue newOperand) {
		_rightOperand = newOperand;
	}
	
	public boolean updateLeftOperand(SchemaElementValue newOperand) {
		if (Objects.equals(newOperand, _leftOperand))
			return false;
		//--- modified
		_leftOperand = newOperand;
		return true;
	}
	
	public boolean updateRightOperand(SchemaElementValue newOperand) {
		if (Objects.equals(newOperand, _rightOperand))
			return false;
		//--- modified
		_rightOperand = newOperand;
		return true;
	}

	/**
	 * このオブジェクトを示す変数名表現を返す。
	 * @return	このオブジェクトを示す変数名表現
	 */
	@Override
	public String toVariableNameString() {
		StringBuilder buffer = new StringBuilder();
		
		if (hasParentObject()) {
			buffer.append(getParentObject().toVariableNameString());
			buffer.append(SchemaUtil.CLASS_SEPARATOR_CHAR);
		}
		
		buffer.append(getTypeName());
		if (isValidElementNo()) {
			buffer.append('[');
			buffer.append(getElementNo());
			buffer.append(']');
		} else {
			buffer.append("[]");
		}
		
		if (hasName()) {
			buffer.append(getName());
		}
		
		buffer.append(SchemaUtil.VALUETYPE_SEPARATOR_CHAR);
		buffer.append(_resultType==null ? "(Unknown)" : _resultType.getTypeName());
		return buffer.toString();
	}

	/**
	 * このオブジェクトの演算種別を示す文字列を返す。
	 */
	public String getTypeName() {
		return "BinaryOp";
	}
	
	public abstract boolean isValidOperation();
	
	public abstract boolean refreshResultValueType();

	//------------------------------------------------------------
	// Implements SchemaObject interfaces
	//------------------------------------------------------------

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = 31 * h + (_resultType == null ? 0 : _resultType.hashCode());
		h = 31 * h + (_operator==null ? 0 : _operator.hashCode());
		h = 31 * h + (_leftOperand==null ? 0 : _leftOperand.hashCode());
		h = 31 * h + (_rightOperand==null ? 0 : _rightOperand.hashCode());
		return h;
	}

	//------------------------------------------------------------
	// Read interfaces for XML
	//------------------------------------------------------------

	@Override
	protected boolean xmlParseAttributeValue(SchemaConfigReader reader, XMLStreamReader xmlReader, String attrName, String attrValue)
	throws XMLStreamException
	{
		if (XmlAttrOperator.equals(attrName)) {
			if (attrValue != null && attrValue.length() > 0) {
				setOperator(attrValue);
			} else {
				setOperator(null);
			}
			return true;
		}
		
		if (XmlAttrResultType.equals(attrName)) {
			if (attrValue == null || attrValue.isEmpty()) {
				// default value type
				setResultType(null);
			}
			else {
				try {
					SchemaValueType vtype = SchemaValueTypeManager.valueTypeOf(attrValue);
					setResultType(vtype);
				} catch (Throwable ex) {
					String msg = String.format("<%s> : \"%s\" attribute value is wrong : value=\"%s\"", getXmlElementName(), attrName, attrValue);
					throw new XMLStreamException(msg, xmlReader.getLocation(), ex);
				}
			}
			return true;
		}
		
		return super.xmlParseAttributeValue(reader, xmlReader, attrName, attrValue);
	}

	@Override
	protected boolean xmlParseChildElement(SchemaConfigReader reader,XMLStreamReader xmlReader, String tagName)
	throws XMLStreamException
	{
		if (XmlElemLeftOperand.equals(tagName)) {
			// left operand
			setLeftOperand(xmlParseOperandObject(reader, xmlReader, tagName));
			return true;
		}
		
		if (XmlElemRightOperand.equals(tagName)) {
			// right operand
			setRightOperand(xmlParseOperandObject(reader, xmlReader, tagName));
			return true;
		}
		
		return super.xmlParseChildElement(reader, xmlReader, tagName);
	}
	
	protected SchemaValueObject xmlParseOperandObject(SchemaConfigReader reader, XMLStreamReader xmlReader, String tagName)
	throws XMLStreamException
	{
		//--- 次のタグへ
		xmlReader.nextTag();
		
		// 値の判定
		String valueTagName = xmlReader.getLocalName();
		SchemaValueObject operandValue = null;
		try {
			operandValue = SchemaXmlUtil.createObjectInstanceByElementTagName(SchemaValueObject.class, xmlReader, tagName, valueTagName);
		}
		catch (ClassCastException ex) {
			// Unsupported element
			String msg = String.format("<%s> : <%s> element is not supported.", tagName, valueTagName);
			throw new XMLStreamException(msg, xmlReader.getLocation(), ex);
		}
		if (operandValue == null) {
			// Unsupported element
			String msg = String.format("<%s> : <%s> element is not supported.", tagName, valueTagName);
			throw new XMLStreamException(msg, xmlReader.getLocation());
		}
		
		// 値の取得
		operandValue.readFromXml(reader);
		//--- 次のタグへ
		xmlReader.nextTag();
		
		//--- 終端チェック
		SchemaXmlUtil.xmlValidEndElement(xmlReader, tagName);
		return operandValue;
	}

	//------------------------------------------------------------
	// Write interfaces for XML
	//------------------------------------------------------------
	
	@Override
	protected void xmlWriteStartAttributes(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		super.xmlWriteStartAttributes(writer, xmlWriter);
		
		// operator
		if (_operator != null) {
			xmlWriter.writeAttribute(XmlAttrOperator, _operator);
		}
		
		// resultType
		if (_resultType != null) {
			xmlWriter.writeAttribute(XmlAttrResultType, _resultType.getTypeName());
		}
	}

	@Override
	protected void xmlWriteBodyElements(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		super.xmlWriteBodyElements(writer, xmlWriter);
		
		// left operand
		if (_leftOperand != null) {
			xmlWriter.writeStartElement(XmlElemLeftOperand);
			_leftOperand.writeToXml(writer);
			xmlWriter.writeEndElement();
		}
		
		// right operand
		if (_rightOperand != null) {
			xmlWriter.writeStartElement(XmlElemRightOperand);
			_rightOperand.writeToXml(writer);
			xmlWriter.writeEndElement();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void setResultType(SchemaValueType resultType) {
		_resultType = resultType;
	}

	protected boolean updateResultType(SchemaValueType resultType) {
		if (Objects.equals(resultType, _resultType))
			return false;
		//--- modified
		_resultType = resultType;
		setValueType(resultType);
		return true;
	}

	@Override
	protected void appendParamString(StringBuilder buffer) {
		super.appendParamString(buffer);
		
		buffer.append(", resultType=");
		buffer.append(_resultType);
		
		buffer.append(", operator=");
		buffer.append(_operator);
		
		buffer.append(", leftOperand=");
		buffer.append(_leftOperand==null ? null : _leftOperand.toParamString());
		
		buffer.append(", rightOperand=");
		buffer.append(_rightOperand==null ? null : _rightOperand.toParamString());
	}

	@Override
	protected boolean equalFields(Object obj) {
		if (!super.equalFields(obj))
			return false;
		
		SchemaBinaryOperationData aData = (SchemaBinaryOperationData)obj;
		
		if (!Objects.equals(aData._resultType, this._resultType))
			return false;
		
		if (!Objects.equals(aData._operator, this._operator))
			return false;
		
		if (!Objects.equals(aData._leftOperand, this._leftOperand))
			return false;
		
		if (!Objects.equals(aData._rightOperand, this._rightOperand))
			return false;
		
		// equal all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
