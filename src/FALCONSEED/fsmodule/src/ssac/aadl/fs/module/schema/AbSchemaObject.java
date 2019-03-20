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
 * @(#)AbSchemaObject.java	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;
import ssac.aadl.fs.module.schema.io.SchemaXmlUtil;
import ssac.aadl.runtime.util.Objects;

/**
 * 汎用フィルタ定義の共通インタフェースの基本実装。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public abstract class AbSchemaObject implements SchemaObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static protected final String	XmlElemDesc		= "Description";
	static protected final String	XmlAttrForName	= "Name";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このオブジェクトの名前 **/
	private String	_name;
	/** このオブジェクトの説明 **/
	private String	_desc;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のパラメータで、新しいインスタンスを生成する。
	 */
	public AbSchemaObject() {
		_name = null;
		_desc = null;
	}

	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param name
	 * @param desc
	 */
	public AbSchemaObject(String name, String desc) {
		_name = SchemaUtil.emptyStringToNull(name);
		_desc = SchemaUtil.emptyStringToNull(desc);
	}

	/**
	 * 指定されたオブジェクトの内容をコピーした、新しいインスタンスを生成する。
	 * @param src	コピー元のオブジェクト
	 */
	public AbSchemaObject(final SchemaObject src) {
		_name = src.getName();
		_desc = src.getDescription();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトのインスタンス ID を取得する。
	 * このメソッドが返す値は、{@link System#identityHashCode(Object)} の戻り値となる。
	 * @return	インスタンス ID
	 */
	@Override
	public int getInstanceId() {
		return System.identityHashCode(this);
	}

	/**
	 * このオブジェクトの名前を取得する。
	 * このメソッドが返す値は空文字ではない文字列、もしくは <tt>null</tt> である。
	 * @return	設定されている名前、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public String getName() {
		return _name;
	}

	/**
	 * このオブジェクトの名前を設定する。
	 * 新しい名前が空文字の場合、<tt>null</tt> が設定される。
	 * @param newName	新しい名前
	 */
	@Override
	public void setName(String newName) {
		_name = SchemaUtil.emptyStringToNull(newName);
	}
	
	/**
	 * このオブジェクトの名前を更新する。
	 * 新しい名前が空文字の場合、<tt>null</tt> が設定される。
	 * @param newName	新しい名前
	 * @return	名前が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean updateName(String newName) {
		newName = SchemaUtil.emptyStringToNull(newName);
		if (Objects.equals(newName, _name))
			return false;
		//--- modified
		_name = newName;
		return true;
	}

	/**
	 * このオブジェクトの説明を取得する。
	 * このメソッドが返す値は空文字ではない文字列、もしくは <tt>null</tt> である。
	 * @return	設定されている説明、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public String getDescription() {
		return _desc;
	}

	/**
	 * このオブジェクトの説明を設定する。
	 * 新しい説明が空文字の場合、<tt>null</tt> が設定される。
	 * @param newDesc	新しい説明
	 */
	@Override
	public void setDescription(String newDesc) {
		_desc = SchemaUtil.emptyStringToNull(newDesc);
	}

	/**
	 * このオブジェクトの説明を更新する。
	 * 新しい説明が空文字の場合、<tt>null</tt> が設定される。
	 * @param newDesc	新しい説明
	 * @return	説明が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean updateDescription(String newDesc) {
		newDesc = SchemaUtil.emptyStringToNull(newDesc);
		if (Objects.equals(newDesc, _desc))
			return false;
		//--- modified
		_desc = newDesc;
		return true;
	}

	/**
	 * このオブジェクトの名前を示す文字列を返す。
	 * 名前が設定されていない場合、クラス名とインスタンス ID の組合せとする。
	 * @return	名前を示す文字列
	 */
	@Override
	public String toNameString() {
		if (_name != null) {
			return _name;
		} else {
			return "";
		}
	}

	/**
	 * このオブジェクトを示す変数名表現を返す。
	 * @return	このオブジェクトを示す変数名表現
	 */
	@Override
	public String toVariableNameString() {
		return toNameString();
	}

	/**
	 * このオブジェクトのパラメータを示す文字列表現を返す。
	 * @return	パラメータを含む文字列表現
	 */
	@Override
	public String toParamString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(super.toString());
		buffer.append('[');
		appendParamString(buffer);
		buffer.append(']');
		return buffer.toString();
	}

	//------------------------------------------------------------
	// Implements java.lang.Object interfaces
	//------------------------------------------------------------

	@Override
	public int hashCode() {
		int h = (_name==null ? 0 : _name.hashCode());
		h = 31 * h + (_desc==null ? 0 : _desc.hashCode());
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj != null && obj.getClass().equals(this.getClass())) {
			if (equalFields(obj)) {
				return true;
			}
		}
		
		// not equals
		return false;
	}

	@Override
	public String toString() {
		return toVariableNameString();
	}

	//------------------------------------------------------------
	// Public interfaces for XML
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトの XML エレメント名を取得する。
	 * 基本的に、このオブジェクトクラスの {@link Class#getName()} が返す文字列とする。
	 * @return	このオブジェクトの XML エレメント名
	 */
	@Override
	public String getXmlElementName() {
		return getClass().getName();
	}

	/**
	 * 指定されたライターオブジェクトにより、このオブジェクトの XML 表現を出力する。
	 * @param writer	専用のライターオブジェクト
	 * @throws XMLStreamException	XML 出力に失敗した場合
	 */
	@Override
	public void writeToXml(SchemaConfigWriter writer) throws XMLStreamException
	{
		xmlWriteStartElement(writer, writer.getXMLWriter());
		
		xmlWriteDescriptionElement(writer, writer.getXMLWriter());
		xmlWriteBodyElements(writer, writer.getXMLWriter());
		
		writer.getXMLWriter().writeEndElement();
	}

	/**
	 * 指定されたリーダーオブジェクトの現在の位置から XML 表現を読み込み、このオブジェクトの内容を復元する。
	 * @param reader	専用のリーダーオブジェクト
	 * @throws XMLStreamException	XML 入力に失敗した場合
	 */
	@Override
	public void readFromXml(SchemaConfigReader reader) throws XMLStreamException
	{
		XMLStreamReader xmlReader = reader.getXMLReader();
		
		// start element
		SchemaXmlUtil.xmlValidStartElement(xmlReader, getXmlElementName());
		xmlParseAttributes(reader, xmlReader);
		//--- 次のタグへ
		xmlReader.nextTag();

		// 子エレメントの処理
		for (;;) {
			int eventType = xmlReader.getEventType();
			if (eventType == XMLStreamConstants.END_ELEMENT) {
				break;
			}
			else if (eventType != XMLStreamConstants.START_ELEMENT) {
				String msg = String.format("<%s> : Unexpected XML event : [%s]", getXmlElementName(), SchemaXmlUtil.toXmlEventString(eventType));
				throw new XMLStreamException(msg, xmlReader.getLocation());
			}
			
			if (!xmlParseChildElement(reader, xmlReader, xmlReader.getLocalName())) {
				// Unsupported element
				String msg = String.format("<%s> : <%s> element is not supported.", getXmlElementName(), xmlReader.getLocalName());
				throw new XMLStreamException(msg, xmlReader.getLocation());
			}
			//--- 次のタグへ
			xmlReader.nextTag();
		}
		
		// end element
		SchemaXmlUtil.xmlValidEndElement(reader.getXMLReader(), getXmlElementName());
	}
	
	//------------------------------------------------------------
	// Read Interfaces for XML
	//------------------------------------------------------------

	/**
	 * XML タグの全属性値を読み込み、対応するパラメータに設定する。
	 * @param reader	専用のリーダーオブジェクト
	 * @param xmlReader	XML ストリームオブジェクト
	 * @throws XMLStreamException	読み込みエラーが発生した場合、もしくはサポートされていない属性値、値の変換失敗
	 */
	protected void xmlParseAttributes(SchemaConfigReader reader, XMLStreamReader xmlReader) throws XMLStreamException
	{
		int attrcnt = xmlReader.getAttributeCount();
		for (int i = 0; i < attrcnt; ++i) {
			String attrName  = xmlReader.getAttributeLocalName(i);
			String attrValue = xmlReader.getAttributeValue(i);
			if (!xmlParseAttributeValue(reader, xmlReader, attrName, attrValue)) {
				// unsupported attribute
				String msg = String.format("<%s> : \"%s\" attribute is not supported : value=[%s]", getXmlElementName(), attrName, attrValue);
				throw new XMLStreamException(msg, xmlReader.getLocation());
			}
		}
	}

	/**
	 * XML タグの属性値を読み込み、対応するパラメータに設定する。
	 * サポートされていない属性名の場合、このメソッドは <tt>null</tt> を返す。
	 * @param reader	専用のリーダーオブジェクト
	 * @param xmlReader	XML ストリームオブジェクト
	 * @param attrName	属性名
	 * @param attrValue	属性値
	 * @return	この属性名がパラメータとなる場合は <tt>true</tt>、サポートされていない属性名の場合は <tt>false</tt>
	 * @throws XMLStreamException	XML 読み込みエラーが発生した場合、もしくは属性値が適切なデータ型に変換できない場合
	 */
	protected boolean xmlParseAttributeValue(SchemaConfigReader reader, XMLStreamReader xmlReader, String attrName, String attrValue) throws XMLStreamException
	{
		return xmlParseNameAttributeValue(reader, xmlReader, attrName, attrValue);
	}

	/**
	 * 'name' 属性値を読み込み、Name 値を設定する。
	 * @param reader	専用のリーダーオブジェクト
	 * @param xmlReader	XML ストリームオブジェクト
	 * @param attrName	属性名
	 * @param attrValue	属性値
	 * @return	'name'属性値なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws XMLStreamException	XML 読み込みエラーが発生した場合、もしくは属性値が適切なデータ型に変換できない場合
	 */
	protected boolean xmlParseNameAttributeValue(SchemaConfigReader reader, XMLStreamReader xmlReader, String attrName, String attrValue) throws XMLStreamException
	{
		if (XmlAttrForName.equals(attrName)) {
			setName(attrValue);
			return true;
		}
		
		return false;
	}

	/**
	 * XML タグの内容を読み込み、対応するパラメータに設定する。
	 * このメソッドが呼び出された時点では、START_ELEMENT が読み込み位置となっている。
	 * このメソッドで処理した場合、そのタグの END_ELEMENT が読み込み位置となっていることを前提とする。
	 * @param reader	専用のリーダーオブジェクト
	 * @param xmlReader	XML ストリームオブジェクト
	 * @param tagName	現在のタグ名
	 * @return	このタグがパラメータとなる場合は <tt>true</tt>、サポートされていない属性名の場合は <tt>false</tt>
	 * @throws XMLStreamException	XML 読み込みエラーが発生した場合、もしくはタグの内容解析に失敗した場合
	 */
	protected boolean xmlParseChildElement(SchemaConfigReader reader, XMLStreamReader xmlReader, String tagName) throws XMLStreamException
	{
		assert (xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT);
		
		return xmlParseDescriptionElement(reader, xmlReader, tagName);
	}

	/**
	 * 現在のタグ名が説明タグであれば、説明タグの内容を読み込み、対応するパラメータに設定する。
	 * 説明タグであれば、このメソッドから処理が戻るときには、説明タグの END_ELEMENT が読み込み位置となっている。
	 * @param reader	専用のリーダーオブジェクト
	 * @param xmlReader	XML ストリームオブジェクト
	 * @return	このタグがパラメータとなる場合は <tt>true</tt>、サポートされていない属性名の場合は <tt>false</tt>
	 * @throws XMLStreamException	XML 読み込みエラーが発生した場合、もしくはタグの内容解析に失敗した場合
	 */
	protected boolean xmlParseDescriptionElement(SchemaConfigReader reader, XMLStreamReader xmlReader, String tagName) throws XMLStreamException
	{
		assert (xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT);
		if (!XmlElemDesc.equals(tagName)) {
			return false;	// not description element
		}
		
		String text = xmlReader.getElementText();
		setDescription(text);
		
		SchemaXmlUtil.xmlValidEndElement(xmlReader, XmlElemDesc);
		return true;
	}

	//------------------------------------------------------------
	// Write Interfaces for XML
	//------------------------------------------------------------

	/**
	 * このオブジェクトの XML 開始エレメントを出力する。
	 * @param writer	専用のライターオブジェクト
	 * @param xmlWriter	現在の XML ライターオブジェクト
	 * @throws XMLStreamException	書き込みに失敗した場合
	 */
	protected void xmlWriteStartElement(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		writer.getXMLWriter().writeStartElement(getXmlElementName());

		xmlWriteNameAttribute(writer, xmlWriter);
		xmlWriteStartAttributes(writer, xmlWriter);
	}

	/**
	 * このオブジェクトの開始エレメントに、名前以外の属性を出力する。
	 * @param writer	専用のライターオブジェクト
	 * @param xmlWriter	現在の XML ライターオブジェクト
	 * @throws XMLStreamException	書き込みに失敗した場合
	 */
	protected void xmlWriteStartAttributes(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		// place holder
	}

	/**
	 * このオブジェクトの説明以外の内容を XML として出力する。
	 * @param writer	専用のライターオブジェクト
	 * @param xmlWriter	現在の XML ライターオブジェクト
	 * @throws XMLStreamException	書き込みに失敗した場合
	 */
	protected void xmlWriteBodyElements(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		// place holder
	}
	
	/**
	 * このオブジェクトの内容を XML として出力する。
	 * @param writer	専用のライターオブジェクト
	 * @param xmlWriter	現在の XML ライターオブジェクト
	 * @throws XMLStreamException	書き込みに失敗した場合
	 */
	protected void xmlWriteNameAttribute(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		if (_name != null) {
			xmlWriter.writeAttribute(XmlAttrForName, _name);
		}
	}
	
	/**
	 * このオブジェクトの説明が設定されている場合のみ、説明の内容を XML として出力する。
	 * @param writer	専用のライターオブジェクト
	 * @param xmlWriter	現在の XML ライターオブジェクト
	 * @throws XMLStreamException	書き込みに失敗した場合
	 */
	protected void xmlWriteDescriptionElement(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		if (_desc != null) {
			xmlWriter.writeStartElement(XmlElemDesc);
			xmlWriter.writeCharacters(_desc);
			xmlWriter.writeEndElement();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void appendParamString(StringBuilder buffer) {
		buffer.append("name=");
		SchemaUtil.appendStringParam(buffer, _name);
		buffer.append(", desc=");
		SchemaUtil.appendStringParam(buffer, _desc);
	}
	
	protected boolean hasName() {
		return (_name != null);
	}
	
	protected boolean hasDescription() {
		return (_desc != null);
	}
	
	protected boolean equalFields(Object obj) {
		AbSchemaObject aData = (AbSchemaObject)obj;
		
		if (!Objects.equals(aData._name, this._name))
			return false;
		
		if (!Objects.equals(aData._desc, this._desc))
			return false;
		
		// equal all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
