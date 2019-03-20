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
 * @(#)SchemaCsvDataField	3.2.1	2015/07/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaCsvDataField	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.SchemaUtil;
import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;
import ssac.aadl.fs.module.schema.io.SchemaXmlUtil;
import ssac.aadl.fs.module.schema.type.SchemaValueType;

/**
 * CSV 形式での列データのスキーマを保持するクラス。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaCsvDataField extends SchemaDataField
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String	XmlElemHeaderList	= "HeaderList";
	static protected final String	XmlElemHeaderText	= "HeaderText";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** この列のヘッダー行の値の配列 **/
	private String[]	_headerValues = SchemaUtil.EMPTY_STRING_ARRAY;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public SchemaCsvDataField() {
		super();
	}
	
	public SchemaCsvDataField(SchemaValueType valueType) {
		super(valueType);
	}
	
	public SchemaCsvDataField(String name, SchemaValueType valueType, Object value) {
		super(name, valueType, value);
	}
	
	public SchemaCsvDataField(final SchemaCsvDataField src) {
		super(src);
		if (!src.isHeaderEmpty()) {
			_headerValues = Arrays.copyOf(src._headerValues, src._headerValues.length);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isHeaderEmpty() {
		return (_headerValues.length == 0);
	}
	
	public int getHeaderCount() {
		return _headerValues.length;
	}
	
	public boolean clearHeaderValues() {
		if (_headerValues.length == 0)
			return false;
		//--- modified
		_headerValues = SchemaUtil.EMPTY_STRING_ARRAY;
		return true;
	}
	
	public String[] getHeaderValues() {
		return _headerValues;
	}
	
	public String getHeaderValue(int index) {
		return _headerValues[index];
	}
	
	public void setHeaderValue(int index, String newValue) {
		_headerValues[index] = (newValue == null ? "" : newValue);
	}
	
	public boolean updateHeaderValue(int index, String newValue) {
		if (newValue == null)
			newValue = "";
		if (newValue.equals(_headerValues[index]))
			return false;
		//--- modified
		_headerValues[index] = newValue;
		return true;
	}
	
	public void addHeaderValue(String newValue) {
		_headerValues = Arrays.copyOf(_headerValues, _headerValues.length+1);
		_headerValues[_headerValues.length - 1] = (newValue==null ? "" : newValue);
	}
	
	public void insertHeaderValue(int index, String newValue) {
		int oldLength = _headerValues.length;
		if (index >= oldLength) {
			addHeaderValue(newValue);
			return;
		}
		
		String[] newFields = new String[_headerValues.length+1];
		if (index > 0) {
			System.arraycopy(_headerValues, 0, newFields, 0, index);
		}
		if (index < oldLength) {
			System.arraycopy(_headerValues, index, newFields, index+1, oldLength - index);
		}
		newFields[index] = (newValue==null ? "" : newValue);
	}
	
	public String removeHeaderValue(int index) {
		String oldValue = _headerValues[index];
		String[] newFields = new String[_headerValues.length-1];
		if (newFields.length > 0) {
			if (index > 0) {
				System.arraycopy(_headerValues, 0, newFields, 0, index);
			}
			if (index < newFields.length) {
				System.arraycopy(_headerValues, index+1, newFields, index, newFields.length - index);
			}
		}
		_headerValues = newFields;
		return oldValue;
	}
	
	public boolean adjustHeaderCount(int newSize) {
		if (newSize < 0)
			newSize = 0;
		if (_headerValues.length == newSize)
			return false;
		//--- modified length
		String[] newFields = Arrays.copyOf(_headerValues, newSize);
		if (_headerValues.length < newSize) {
			// fill empty string
			for (int index = _headerValues.length; index < newSize; ++index) {
				newFields[index] = "";
			}
		}
		_headerValues = newFields;
		return true;
	}
	
	public void growAndSetHeaderValue(int index, String newValue) {
		if (_headerValues.length <= index) {
			// growup
			adjustHeaderCount(index+1);
			setHeaderValue(index, newValue);
		}
		else {
			// replace
			setHeaderValue(index, newValue);
		}
	}
	
	public void setHeaderValues(String...newValues) {
		if (newValues.length > 0) {
			_headerValues = new String[newValues.length];
			for (int i = 0; i < newValues.length; ++i) {
				_headerValues[i] = (newValues[i]==null ? "" : newValues[i]);
			}
		}
		else {
			_headerValues = SchemaUtil.EMPTY_STRING_ARRAY;
		}
	}
	
	public void setHeaderValues(List<? extends String> newValues) {
		if (newValues.size() > 0) {
			_headerValues = new String[newValues.size()];
			for (int i = 0; i < _headerValues.length; ++i) {
				_headerValues[i] = newValues.get(i);
				if (_headerValues[i] == null)
					_headerValues[i] = "";
			}
		}
		else {
			_headerValues = SchemaUtil.EMPTY_STRING_ARRAY;
		}
	}
	
	public boolean updateHeaderValues(String...newValues) {
		boolean modified = false;
		if (newValues.length == _headerValues.length) {
			// same length
			for (int i = 0; i < newValues.length; ++i) {
				String nv = (newValues[i]==null ? "" : newValues[i]);
				if (!nv.equals(_headerValues[i])) {
					modified = true;
					_headerValues[i] = nv;
				}
			}
		}
		else {
			// different length
			modified = true;
			setHeaderValues(newValues);
		}
		return modified;
	}
	
	public boolean updateHeaderValues(List<? extends String> newValues) {
		boolean modified = false;
		if (newValues.size() == _headerValues.length) {
			// same length
			for (int i = 0; i < _headerValues.length; ++i) {
				String nv = newValues.get(i);
				if (nv == null)
					nv = "";
				if (!nv.equals(_headerValues[i])) {
					modified = true;
					_headerValues[i] = nv;
				}
			}
		}
		else {
			// different length
			modified = true;
			setHeaderValues(newValues);
		}
		return modified;
	}

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = 31 * h + Arrays.hashCode(_headerValues);
		return h;
	}

	//------------------------------------------------------------
	// Read interfaces for XML
	//------------------------------------------------------------

	@Override
	protected boolean xmlParseChildElement(SchemaConfigReader reader, XMLStreamReader xmlReader, String tagName)
	throws XMLStreamException
	{
		if (XmlElemHeaderList.equals(tagName)) {
			//--- 次のタグへ移動
			xmlReader.nextTag();
			
			// 子エレメントの処理
			ArrayList<String> headerTextList = new ArrayList<String>();
			for (;;) {
				int eventType = xmlReader.getEventType();
				if (eventType == XMLStreamConstants.END_ELEMENT) {
					break;
				}
				else if (eventType != XMLStreamConstants.START_ELEMENT) {
					String msg = String.format("<%s> : Unexpected XML event : [%s]", XmlElemHeaderList, SchemaXmlUtil.toXmlEventString(eventType));
					throw new XMLStreamException(msg, xmlReader.getLocation());
				}
				
				if (!xmlHeaderListChildElement(reader, xmlReader, xmlReader.getLocalName(), headerTextList)) {
					// Unsupported element
					String msg = String.format("<%s> : <%s> element is not supported.", XmlElemHeaderList, xmlReader.getLocalName());
					throw new XMLStreamException(msg, xmlReader.getLocation());
				}
				//--- 次のタグ
				xmlReader.nextTag();
			}
			//--- header text の保存
			setHeaderValues(headerTextList);
			
			SchemaXmlUtil.xmlValidEndElement(xmlReader, XmlElemHeaderList);
			return true;
		}
		
		return super.xmlParseChildElement(reader, xmlReader, tagName);
	}
	
	protected boolean xmlHeaderListChildElement(SchemaConfigReader reader, XMLStreamReader xmlReader, String tagName, List<String> headerTextList)
	throws XMLStreamException
	{
		assert (xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT);
		if (!XmlElemHeaderText.equals(tagName)) {
			return false;	// not HeaderText element
		}
		
		String text = xmlReader.getElementText();
		if (text == null)
			text = "";
		headerTextList.add(text);
		
		SchemaXmlUtil.xmlValidEndElement(xmlReader, XmlElemHeaderText);
		return true;
	}

	//------------------------------------------------------------
	// Write interfaces for XML
	//------------------------------------------------------------

	@Override
	protected void xmlWriteBodyElements(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		super.xmlWriteBodyElements(writer, xmlWriter);
		
		// headers
		xmlWriter.writeStartElement(XmlElemHeaderList);
		if (_headerValues.length > 0) {
			for (int i = 0; i < _headerValues.length; ++i) {
				String strText = _headerValues[i];
				xmlWriter.writeStartElement(XmlElemHeaderText);
				if (strText != null && strText.length() > 0) {
					xmlWriter.writeCharacters(strText);
				}
				xmlWriter.writeEndElement();
			}
		}
		xmlWriter.writeEndElement();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected boolean equalFields(Object obj) {
		if (!super.equalFields(obj))
			return false;
		
		SchemaCsvDataField aField = (SchemaCsvDataField)obj;
		
		if (!Arrays.equals(aField._headerValues, this._headerValues))
			return false;
		
		// equal all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
