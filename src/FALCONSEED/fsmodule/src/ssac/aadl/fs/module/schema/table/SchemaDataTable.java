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
 * @(#)SchemaDataTable	3.2.1	2015/07/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaDataTable	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.table;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.SchemaElementObject;
import ssac.aadl.fs.module.schema.SchemaFilterArgValue;
import ssac.aadl.fs.module.schema.SchemaObject;
import ssac.aadl.fs.module.schema.SchemaObjectList;
import ssac.aadl.fs.module.schema.SchemaUtil;
import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;
import ssac.aadl.fs.module.schema.io.SchemaXmlUtil;
import ssac.aadl.runtime.util.Objects;

/**
 * テーブルデータのスキーマを保持するクラス。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaDataTable<E extends SchemaDataField> extends SchemaObjectList<E> implements SchemaElementObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -8686615792593545952L;
	
	static protected final String	XmlAttrElemNo	= "elemNumber";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このオブジェクトの要素番号 **/
	private int	_elemNo = SchemaElementObject.INVALID_ELEMENT_NO;
	/** このオブジェクトの親オブジェクト **/
	private SchemaObject	_elemParent;

	/** このテーブルが対応するフィルタ定義引数 **/
	private SchemaFilterArgValue	_filterArg;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のパラメータで、要素が空の新しいインスタンスを生成する。
	 */
	public SchemaDataTable() {
		super();
	}

	/**
	 * 標準のパラメータで、指定された容量を持つ、要素が空の新しいインスタンスを生成する。
	 * @param initialCapacity	初期容量(0 以上)
	 * @throws IllegalArgumentException	<em>initialCapacity</em> が負の場合
	 */
	public SchemaDataTable(int initialCapacity) {
		super(initialCapacity);
	}
	
	/**
	 * 指定されたオブジェクトの内容をコピーした、新しいインスタンスを生成する。
	 * @param copyListElements	コレクション要素をコピーする場合は <tt>true</tt>、コピーしない場合は <tt>false</tt>
	 * @param src	コピー元とするオブジェクト
	 * @throws NullPointerException	コピー元オブジェクトが <tt>null</tt> の場合
	 */
	public SchemaDataTable(boolean copyListElements, final SchemaDataTable<? extends E> src) {
		super(copyListElements, src);
		_elemNo     = src._elemNo;
		_elemParent = src._elemParent;
		_filterArg  = src._filterArg;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトに設定された要素番号が有効かどうかを判定する。
	 * @return	要素番号が無効の場合は <tt>false</tt>
	 */
	@Override
	public boolean isValidElementNo() {
		return (_elemNo > SchemaElementObject.INVALID_ELEMENT_NO);
	}
	
	/**
	 * このオブジェクトの要素番号を取得する。
	 * @return	正の要素番号、要素番号が無効の場合は (-1)
	 */
	@Override
	public int getElementNo() {
		return _elemNo;
	}

	/**
	 * このオブジェクトに要素番号を設定する。
	 * @param newElemNo	新しい要素番号、無効とする場合は負の値
	 */
	@Override
	public void setElementNo(int newElemNo) {
		_elemNo = SchemaUtil.normalizeElementNo(newElemNo);
	}

	/**
	 * このオブジェクトに要素番号を設定する。
	 * @param newElemNo	新しい要素番号、無効とする場合は負の値
	 * @return	要素番号が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean updateElementNo(int newElemNo) {
		newElemNo = SchemaUtil.normalizeElementNo(newElemNo);
		if (newElemNo == _elemNo)
			return false;
		//--- modified
		_elemNo = newElemNo;
		return true;
	}

	/**
	 * この要素の親となるオブジェクトが設定されているかを判定する。
	 * @return	要素の親オブジェクトが設定されている場合は <tt>true</tt>
	 */
	@Override
	public boolean hasParentObject() {
		return (_elemParent != null);
	}

	/**
	 * 設定されているこの要素の親オブジェクトを取得する。
	 * @return	設定されている親オブジェクト、設定されていな場合は <tt>null</tt>
	 */
	@Override
	public SchemaObject getParentObject() {
		return _elemParent;
	}

	/**
	 * このオブジェクトに親オブジェクトを設定する。
	 * @param newParent	新しい親オブジェクト
	 */
	@Override
	public void setParentObject(SchemaObject newParent) {
		_elemParent = newParent;
	}

	/**
	 * このオブジェクトに親オブジェクトを設定する。
	 * このメソッドでは、親オブジェクトが同一かどうか（インスタンスが同じかどうか)を判定し、
	 * 異なるオブジェクトであれば更新する。
	 * @param newParent	新しい親オブジェクト
	 * @return	親オブジェクトのインスタンスが変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean updateParentObject(SchemaObject newParent) {
		if (newParent == _elemParent)
			return false;
		//--- modified
		_elemParent = newParent;
		return true;
	}
	
	public SchemaFilterArgValue getFilterArgument() {
		return _filterArg;
	}
	
	public void setFilterArgument(SchemaFilterArgValue arg) {
		_filterArg = arg;
	}
	
	public boolean updateFilterArgument(SchemaFilterArgValue arg) {
		if (Objects.equals(arg, _filterArg))
			return false;
		//--- modified
		_filterArg = arg;
		return true;
	}

	@Override
	public String toNameString() {
		if (_filterArg != null) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(_filterArg.toNameString());
			String name = getName();
			if (name != null) {
				buffer.append(name);
			}
			return buffer.toString();
		}
		else {
			return super.toNameString();
		}
	}

	/**
	 * このオブジェクトを示す変数名表現を返す。
	 * @return	このオブジェクトを示す変数名表現
	 */
	@Override
	public String toVariableNameString() {
		if (_elemParent == null) {
			return toLocalVariableNameString();
		}
		else {
			StringBuilder buffer = new StringBuilder();
			if (_elemParent != null) {
				buffer.append(_elemParent.toVariableNameString());
				buffer.append(SchemaUtil.CLASS_SEPARATOR_CHAR);
			}
			buffer.append(toLocalVariableNameString());
			return buffer.toString();
		}
	}
	
	public String toLocalVariableNameString() {
		//--- 要素番号は無視
		return super.toVariableNameString();
//		if (_elemNo < 0) {
//			// no element number
//			return super.toVariableNameString();
//		} else {
//			// with element number
//			return String.format("[%d]%d", _elemNo, super.toVariableNameString());
//		}
	}

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = 31 * h + _elemNo;
//		h = 31 * h + (_elemParent==null ? 0 : _elemParent.hashCode());
		h = 31 * h + (_filterArg==null ? 0 : _filterArg.hashCode());
		return h;
	}

	//------------------------------------------------------------
	// Read Interfaces for XML
	//------------------------------------------------------------

	@Override
	protected boolean xmlParseAttributeValue(SchemaConfigReader reader, XMLStreamReader xmlReader, String attrName, String attrValue)
	throws XMLStreamException
	{
		// element no
		if (XmlAttrElemNo.equals(attrName)) {
			if (attrValue == null || attrValue.isEmpty()) {
				// invalid element number
				setElementNo(SchemaElementObject.INVALID_ELEMENT_NO);
			}
			else {
				// 整数変換
				try {
					setElementNo(Integer.parseInt(attrValue));
				}
				catch (Throwable ex) {
					String msg = String.format("<%s> : \"%s\" attribute value is not Integer value : value=\"%s\"", getXmlElementName(), attrName, attrValue);
					throw new XMLStreamException(msg, xmlReader.getLocation(), ex);
				}
			}
			return true;
		}
		
		return super.xmlParseAttributeValue(reader, xmlReader, attrName, attrValue);
	}

	@Override
	protected boolean xmlParseChildElement(SchemaConfigReader reader, XMLStreamReader xmlReader, String tagName)
	throws XMLStreamException
	{
		SchemaFilterArgValue argValue = SchemaXmlUtil.createObjectInstanceByElementTagName(SchemaFilterArgValue.class, xmlReader, getXmlElementName(), tagName);
		if (argValue != null) {
			// filter argument value
			argValue.readFromXml(reader);
			setFilterArgument(argValue);
			return true;
		}
		
		return super.xmlParseChildElement(reader, xmlReader, tagName);
	}

	//------------------------------------------------------------
	// Write Interfaces for XML
	//------------------------------------------------------------

	@Override
	protected void xmlWriteStartAttributes(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		super.xmlWriteStartAttributes(writer, xmlWriter);
		
		// element no
		if (isValidElementNo()) {
			xmlWriter.writeAttribute(XmlAttrElemNo, Integer.toString(_elemNo));
		}
	}

	@Override
	protected void xmlWriteBodyElements(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		super.xmlWriteBodyElements(writer, xmlWriter);
		
		if (_filterArg != null) {
			_filterArg.writeToXml(writer);
		}
	}

	@Override
	protected void xmlWriteListBody(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		super.xmlWriteListBody(writer, xmlWriter);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void appendParamString(StringBuilder buffer) {
		super.appendParamString(buffer);
		
		buffer.append(", elemNo=");
		buffer.append(_elemNo);
		
		buffer.append(", elemParent=");
		if (_elemParent == null) {
			buffer.append("null");
		} else {
			buffer.append(_elemParent.getClass().getName());
			buffer.append('@');
			buffer.append(_elemParent.getInstanceId());
		}
		
		buffer.append(", filterArg=");
		buffer.append(_filterArg==null ? "null" : _filterArg.toParamString());
	}

	@Override
	protected boolean equalFields(Object obj) {
		if (!super.equalFields(obj))
			return false;
		
		SchemaDataTable<?> aList = (SchemaDataTable<?>)obj;
		
		if (aList._elemNo != this._elemNo)
			return false;

//		if (aList._elemParent != this._elemParent)
//			return false;
		
		if (!Objects.equals(aList._filterArg, this._filterArg))
			return false;
		
		// equal all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
