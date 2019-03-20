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
 * @(#)SchemaObjectList	3.2.0	2015/06/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;
import ssac.aadl.fs.module.schema.io.SchemaXmlUtil;
import ssac.aadl.runtime.util.Objects;

/**
 * スキーマの値オブジェクトベースのリスト。
 * <p>このオブジェクトのインタフェースでは、要素番号は更新されない。
 * <p><b>注意：</b>
 * <blockquote>
 * このオブジェクトは同期化されない。
 * </blockquote>
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaObjectList<E extends SchemaObject> extends ArrayList<E> implements SchemaObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -316266378031581538L;
	
	static protected final String	XmlElemList	= "ListElements";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final LocalSchemaObjectImpl	_implObject;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のパラメータで、要素が空の新しいインスタンスを生成する。
	 */
	public SchemaObjectList() {
		super();
		_implObject = new LocalSchemaObjectImpl();
	}

	/**
	 * 標準のパラメータで、指定された容量を持つ、要素が空の新しいインスタンスを生成する。
	 * @param initialCapacity	初期容量(0 以上)
	 * @throws IllegalArgumentException	<em>initialCapacity</em> が負の場合
	 */
	public SchemaObjectList(int initialCapacity) {
		super(initialCapacity);
		_implObject = new LocalSchemaObjectImpl();
	}

	/**
	 * 指定されたオブジェクトの内容をコピーした、新しいインスタンスを生成する。
	 * @param copyListElements	コレクション要素をコピーする場合は <tt>true</tt>、コピーしない場合は <tt>false</tt>
	 * @param src	コピー元とするオブジェクト
	 * @throws NullPointerException	コピー元オブジェクトが <tt>null</tt> の場合
	 */
	public SchemaObjectList(boolean copyListElements, final SchemaObjectList<? extends E> src) {
		super(src.size());
		if (copyListElements)
			super.addAll(src);
		_implObject = new LocalSchemaObjectImpl(src._implObject);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * リスト要素のみを削除する。
	 */
	public void removeAllElements() {
		super.clear();
	}
	
	public boolean containsInstanceId(Object obj) {
		if (!isEmpty()) {
			for (E elem : this) {
				if (obj == elem) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public int indexOfInstanceId(Object obj) {
		if (!isEmpty()) {
			int lim = size();
			for (int index = 0; index < lim; ++lim) {
				if (obj == get(index)) {
					return index;
				}
			}
		}
		return (-1);
	}
	
	public int lastIndexOfInstanceId(Object obj) {
		if (!isEmpty()) {
			for (int index = size() - 1; index >= 0; --index) {
				if (obj == get(index)) {
					return index;
				}
			}
		}
		return (-1);
	}

	/**
	 * このリストの各要素について、要素番号を持つオブジェクトであれば要素番号を更新する。
	 * なお、要素番号は基本的に要素の位置を示すインデックス + 1 とする。
	 * @return	要素番号が一つ以上変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean refreshAllElementNumbers() {
		boolean modified = false;
		int len = size();
		for (int i = 0; i < len; ++i) {
			SchemaObject obj = get(i);
			if (obj instanceof SchemaObjectList) {
				if (((SchemaObjectList<?>)obj).refreshAllElementNumbers()) {
					modified = true;
				}
			}
			if (obj instanceof SchemaElementObject) {
				if (((SchemaElementObject)obj).updateElementNo(i+1)) {
					modified = true;
				}
			}
		}
		return modified;
	}

	//------------------------------------------------------------
	// Implements SchemaObject interfaces
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
		return _implObject.getName();
	}

	/**
	 * このオブジェクトの名前を設定する。
	 * 新しい名前が空文字の場合、<tt>null</tt> が設定される。
	 * @param newName	新しい名前
	 */
	@Override
	public void setName(String newName) {
		_implObject.setName(newName);
	}
	
	/**
	 * このオブジェクトの名前を更新する。
	 * 新しい名前が空文字の場合、<tt>null</tt> が設定される。
	 * @param newName	新しい名前
	 * @return	名前が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean updateName(String newName) {
		return _implObject.updateName(newName);
	}

	/**
	 * このオブジェクトの説明を取得する。
	 * このメソッドが返す値は空文字ではない文字列、もしくは <tt>null</tt> である。
	 * @return	設定されている説明、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public String getDescription() {
		return _implObject.getDescription();
	}

	/**
	 * このオブジェクトの説明を設定する。
	 * 新しい説明が空文字の場合、<tt>null</tt> が設定される。
	 * @param newDesc	新しい説明
	 */
	@Override
	public void setDescription(String newDesc) {
		_implObject.setDescription(newDesc);
	}

	/**
	 * このオブジェクトの説明を更新する。
	 * 新しい説明が空文字の場合、<tt>null</tt> が設定される。
	 * @param newDesc	新しい説明
	 * @return	説明が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean updateDescription(String newDesc) {
		return _implObject.updateDescription(newDesc);
	}

	/**
	 * このオブジェクトの名前を示す文字列を返す。
	 * 名前が設定されていない場合、クラス名とインスタンス ID の組合せとする。
	 * @return	名前を示す文字列
	 */
	@Override
	public String toNameString() {
		if (_implObject.hasName()) {
			return _implObject.getName();
		} else {
			return getClass().getName() + "@" + Integer.toHexString(getInstanceId());
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
		buffer.append(getClass().getName() + "@" + Integer.toHexString(getInstanceId()));
		buffer.append('[');
		appendParamString(buffer);
		appendListParamString(buffer);
		buffer.append(']');
		return buffer.toString();
	}

	//------------------------------------------------------------
	// Implements java.lang.Object interfaces
	//------------------------------------------------------------

	@Override
	public int hashCode() {
		int h = _implObject.hashCode();
		h = 31 * h + super.hashCode();
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (super.equals(obj)) {
			if (obj.getClass().equals(this.getClass())) {
				if (equalFields(obj)) {
					return true;
				}
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
		
		xmlWriteDescriptionElement(writer, writer.getXMLWriter());	// 説明の出力
		xmlWriteBodyElements(writer, writer.getXMLWriter());		// Body の出力
		xmlWriteListElements(writer, writer.getXMLWriter());		// リスト要素を囲む要素の出力
		
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
			//--- 次のタグ
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
		return _implObject.xmlParseNameAttributeValue(reader, xmlReader, attrName, attrValue);
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

		// element list
		if (xmlParseElementListElement(reader, xmlReader, tagName))
			return true;
		
		// description
		return _implObject.xmlParseDescriptionElement(reader, xmlReader, tagName);
	}
	
	protected boolean xmlParseElementListElement(SchemaConfigReader reader, XMLStreamReader xmlReader, String tagName) throws XMLStreamException
	{
		if (!XmlElemList.equals(tagName))
			return false;
		//--- 次のタグへ移動
		xmlReader.nextTag();
		
		// 子エレメントの処理
		for (;;) {
			int eventType = xmlReader.getEventType();
			if (eventType == XMLStreamConstants.END_ELEMENT) {
				break;
			}
			else if (eventType != XMLStreamConstants.START_ELEMENT) {
				String msg = String.format("<%s> : Unexpected XML event : [%s]", XmlElemList, SchemaXmlUtil.toXmlEventString(eventType));
				throw new XMLStreamException(msg, xmlReader.getLocation());
			}
			
			if (!xmlParseListChildElement(reader, xmlReader, xmlReader.getLocalName())) {
				// Unsupported element
				String msg = String.format("<%s> : <%s> element is not supported.", XmlElemList, xmlReader.getLocalName());
				throw new XMLStreamException(msg, xmlReader.getLocation());
			}
			//--- 次のタグへ
			xmlReader.nextTag();
		}
		
		// end element
		SchemaXmlUtil.xmlValidEndElement(reader.getXMLReader(), XmlElemList);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	protected boolean xmlParseListChildElement(SchemaConfigReader reader, XMLStreamReader xmlReader, String tagName) throws XMLStreamException
	{
		assert (xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT);
		
		// create instance
		E obj;
		//--- check by cast
		try {
			obj = (E)SchemaXmlUtil.createObjectInstanceByElementTagName(SchemaObject.class, xmlReader, XmlElemList, tagName);
		} catch (ClassCastException ex) {
			// unsupported tagName
			return false;
		}
		if (obj == null) {
			// unsupported tagName
			return false;
		}
		
		// read xml
		obj.readFromXml(reader);
		
		// add to current list
		add(obj);
		if (obj instanceof SchemaElementValue) {
			// set parent to element
			((SchemaElementValue)obj).setParentObject(this);
		}
		
		return true;
	}
	
//	protected E createObjectInstanceByElementTagName(SchemaConfigReader reader, XMLStreamReader xmlReader, String parentTag, String tagName)
//	throws XMLStreamException
//	{
//		// check class name
//		Class<?> clazz = null;
//		try {
//			clazz = Class.forName(tagName);
//		}
//		catch (ClassNotFoundException ex) {
//			// unsupported class
//			return null;
//		}
//		
//		// create SchemaObject instance
//		try {
//			SchemaObject obj = (SchemaObject)clazz.newInstance();
//			return (E)obj;
//		} catch (InstantiationException ex) {
//			String msg = String.format("<%s> : \"%s\" class cannot instantiation.", parentTag, tagName);
//			throw new XMLStreamException(msg, xmlReader.getLocation(), ex);
//		} catch (IllegalAccessException ex) {
//			String msg = String.format("<%s> : \"%s\" class cannot instantiation.", parentTag, tagName);
//			throw new XMLStreamException(msg, xmlReader.getLocation(), ex);
//		} catch (ClassCastException ex) {
//			String msg = String.format("<%s> : \"%s\" class is not this list's member class.", parentTag, tagName);
//			throw new XMLStreamException(msg, xmlReader.getLocation(), ex);
//		}
//	}

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
	 * このオブジェクトの説明とリスト要素以外の内容を XML として出力する。
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
		_implObject.xmlWriteNameAttribute(writer, xmlWriter);
	}
	
	/**
	 * このオブジェクトの説明が設定されている場合のみ、説明の内容を XML として出力する。
	 * @param writer	専用のライターオブジェクト
	 * @param xmlWriter	現在の XML ライターオブジェクト
	 * @throws XMLStreamException	書き込みに失敗した場合
	 */
	protected void xmlWriteDescriptionElement(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		_implObject.xmlWriteDescriptionElement(writer, xmlWriter);
	}

	/**
	 * このオブジェクトのリスト要素を囲む XML エレメント名を取得する。
	 * @return	リスト要素を囲むエレメント名
	 */
	protected String getListElementName() {
		return XmlElemList;
	}

	/**
	 * このリストの要素を囲む XML 表現を出力する。
	 * @param writer	専用のライターオブジェクト
	 * @param xmlWriter	現在の XML ライターオブジェクト
	 * @throws XMLStreamException	書き込みに失敗した場合
	 */
	protected void xmlWriteListElements(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		xmlWriter.writeStartElement(getListElementName());

		if (!isEmpty()) {
			xmlWriteListBody(writer, xmlWriter);
		}
		
		xmlWriter.writeEndElement();
	}

	/**
	 * このリストのすべての要素の XML 表現を出力する。
	 * このメソッドは、リストが空の場合は呼び出されない。
	 * @param writer	専用のライターオブジェクト
	 * @param xmlWriter	現在の XML ライターオブジェクト
	 * @throws XMLStreamException	書き込みに失敗した場合
	 */
	protected void xmlWriteListBody(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		for (SchemaObject obj : this) {
			obj.writeToXml(writer);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void appendListParamString(StringBuilder buffer) {
		buffer.append(", size=");
		buffer.append(size());
		
		buffer.append(", list=");
		if (super.isEmpty()) {
			buffer.append("[]");
		} else {
			buffer.append('[');
			Iterator<E> it = super.iterator();
			E elem = it.next();
			buffer.append(elem==null ? elem : elem.toParamString());
			for (; it.hasNext(); ) {
				buffer.append(", ");
				elem = it.next();
				buffer.append(elem==null ? elem : elem.toParamString());
			}
			buffer.append(']');
		}
	}

	protected void appendParamString(StringBuilder buffer) {
		_implObject.appendParamString(buffer);
	}

	protected boolean equalFields(Object obj) {
		SchemaObjectList<?> aList = (SchemaObjectList<?>)obj;
		
		if (!Objects.equals(aList._implObject, this._implObject))
			return false;
		
		// equal all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class LocalSchemaObjectImpl extends AbSchemaObject
	{
		public LocalSchemaObjectImpl() {
			super();
		}

		public LocalSchemaObjectImpl(String name, String desc) {
			super(name, desc);
		}
		
		public LocalSchemaObjectImpl(final AbSchemaObject src) {
			super(src);
		}
	}
}
