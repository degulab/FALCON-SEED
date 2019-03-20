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
 * @(#)SchemaXmlUtil.java	3.2.0	2015/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.io;

import java.io.PrintStream;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import ssac.aadl.fs.module.schema.SchemaObject;

/**
 * XML ユーティリティ。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaXmlUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String XML_ENCODING	= "UTF-8";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private SchemaXmlUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public SchemaObject createObjectInstanceByElementTabName(XMLStreamReader xmlReader, String parentTag, String tagName)
	throws XMLStreamException
	{
		return createObjectInstanceByElementTagName(SchemaObject.class, xmlReader, parentTag, tagName);
	}
	
	@SuppressWarnings("unchecked")
	static public <T> T createObjectInstanceByElementTagName(Class<? extends T> newType, XMLStreamReader xmlReader, String parentTag, String tagName)
	throws XMLStreamException
	{
		// check class name
		Class<?> clazz = null;
		try {
			clazz = Class.forName(tagName);
		}
		catch (ClassNotFoundException ex) {
			// unsupported class
			return null;
		}
		if (!newType.isAssignableFrom(clazz)) {
			// unassingnable target class
			return null;
		}
		
		// create SchemaObject instance
		try {
			Object obj = clazz.newInstance();
			return (T)obj;
		} catch (InstantiationException ex) {
			String msg = String.format("<%s> : \"%s\" class cannot instantiation.", parentTag, tagName);
			throw new XMLStreamException(msg, xmlReader.getLocation(), ex);
		} catch (IllegalAccessException ex) {
			String msg = String.format("<%s> : \"%s\" class cannot instantiation.", parentTag, tagName);
			throw new XMLStreamException(msg, xmlReader.getLocation(), ex);
		} catch (ClassCastException ex) {
			String msg = String.format("<%s> : \"%s\" class is not this list's member class.", parentTag, tagName);
			throw new XMLStreamException(msg, xmlReader.getLocation(), ex);
		}
	}

	/**
	 * 現在の要素が <code>START_ELEMENT</code> であり、指定されたローカル名であることを確認する。
	 * @param xmlReader	XML ストリームオブジェクト
	 * @param localName	判定するローカル名
	 * @throws XMLStreamException	XML 読み込みエラーが発生した場合
	 */
	static public void xmlValidStartElement(XMLStreamReader xmlReader, String localName) throws XMLStreamException
	{
		if (xmlReader.getEventType() != XMLStreamConstants.START_ELEMENT)
			throw new XMLStreamException("</" + localName + "> tag not found : [" + toXmlEventString(xmlReader.getEventType()), xmlReader.getLocation());
		if (!localName.equals(xmlReader.getLocalName()))
			throw new XMLStreamException("</" + localName + "> tag not found : current = </" + String.valueOf(xmlReader.getLocalName()) + ">", xmlReader.getLocation());
	}

	/**
	 * 現在の要素が <code>END_ELEMENT</code> であり、指定されたローカル名であることを確認する。
	 * @param xmlReader	XML ストリームオブジェクト
	 * @param localName	判定するローカル名
	 * @throws XMLStreamException	XML 読み込みエラーが発生した場合
	 */
	static public void xmlValidEndElement(XMLStreamReader xmlReader, String localName) throws XMLStreamException
	{
		if (xmlReader.getEventType() != XMLStreamConstants.END_ELEMENT)
			throw new XMLStreamException("</" + localName + "> tag not found : [" + toXmlEventString(xmlReader.getEventType()), xmlReader.getLocation());
		if (!localName.equals(xmlReader.getLocalName()))
			throw new XMLStreamException("</" + localName + "> tag not found : current = </" + String.valueOf(xmlReader.getLocalName()) + ">", xmlReader.getLocation());
	}

	/**
	 * <code>XMLStreamReader</code> が返すイベントタイプを文字列に変換する。
	 * @param eventType	イベントタイプ
	 * @return	変換した文字列
	 */
	static public String toXmlEventString(int eventType) {
		switch (eventType) {
			case XMLStreamConstants.START_DOCUMENT :
				return "START_DOCUMENT";
			case XMLStreamConstants.END_DOCUMENT :
				return "END_DOCUMENT";
			case XMLStreamConstants.START_ELEMENT :
				return "START_ELEMENT";
			case XMLStreamConstants.END_ELEMENT :
				return "END_ELEMENT";
			case XMLStreamConstants.DTD :
				return "DTD";
			case XMLStreamConstants.CDATA :
				return "CDATA";
			case XMLStreamConstants.COMMENT :
				return "COMMENT";
			case XMLStreamConstants.SPACE :
				return "SPACE";
			case XMLStreamConstants.ATTRIBUTE :
				return "ATTRIBUTE";
			case XMLStreamConstants.CHARACTERS :
				return "CHARACTERS";
			case XMLStreamConstants.ENTITY_DECLARATION :
				return "ENTITY_DECLARATION";
			case XMLStreamConstants.ENTITY_REFERENCE :
				return "ENTITY_REFERENCE";
			case XMLStreamConstants.NAMESPACE :
				return "NAMESPACE";
			case XMLStreamConstants.NOTATION_DECLARATION :
				return "NOTATION_DECLARATION";
			case XMLStreamConstants.PROCESSING_INSTRUCTION :
				return "PROCESSING_INSTRUCTION";
			default :
				return "Unknown(" + Integer.toHexString(eventType) + ")";
		}
	}

	/**
	 * 指定された XML ストリームリーダーの現在位置の情報を出力する。
	 * @param xmlr	XML ストリームリーダー
	 * @param printer	出力先のストリーム
	 */
	static public void printXmlEvent(XMLStreamReader xmlr, PrintStream printer)
	{
		int eventType = xmlr.getEventType();
		
		// print event
		printer.print("EVENT:[" + xmlr.getLocation().getLineNumber()
				+ "][" + xmlr.getLocation().getColumnNumber()
				+ "][" + toXmlEventString(eventType) + "(" + Integer.toString(eventType) + ")] ");
		
		// print info
		printer.print(" [");
		switch (xmlr.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				printer.print("<");
				printName(xmlr, printer);
				printNamespaces(xmlr, printer);
				printAttributes(xmlr, printer);
				printer.print(">");
				break;

			case XMLStreamConstants.END_ELEMENT:
				printer.print("</");
				printName(xmlr, printer);
				printer.print(">");
				break;

			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.CHARACTERS:
				int start = xmlr.getTextStart();
				int length = xmlr.getTextLength();
				printer.print(new String(xmlr.getTextCharacters(), start, length));
				break;

			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				printer.print("<?");
				if (xmlr.hasText())
					printer.print(xmlr.getText());
				printer.print("?>");
				break;

			case XMLStreamConstants.CDATA:
				printer.print("<![CDATA[");
				start = xmlr.getTextStart();
				length = xmlr.getTextLength();
				printer.print(new String(xmlr.getTextCharacters(), start, length));
				printer.print("]]>");
				break;

			case XMLStreamConstants.COMMENT:
				printer.print("<!--");
				if (xmlr.hasText())
					printer.print(xmlr.getText());
				printer.print("-->");
				break;

			case XMLStreamConstants.ENTITY_REFERENCE:
				printer.print(xmlr.getLocalName()+"=");
				if (xmlr.hasText())
					printer.print("["+xmlr.getText()+"]");
				break;

			case XMLStreamConstants.START_DOCUMENT:
				printer.print("<?xml");
				printer.print(" version='"+xmlr.getVersion()+"'");
				printer.print(" encoding='"+xmlr.getCharacterEncodingScheme()+"'");
				if (xmlr.isStandalone())
					printer.print(" standalone='yes'");
				else
					printer.print(" standalone='no'");
				printer.print("?>");
				break;
		}
		printer.println("]");
	}

	private static void printName(XMLStreamReader xmlr, PrintStream printer){
		if(xmlr.hasName()){
			String prefix = xmlr.getPrefix();
			String uri = xmlr.getNamespaceURI();
			String localName = xmlr.getLocalName();
			printName(prefix,uri,localName, printer);
		}
	}

	private static void printName(String prefix, String uri, String localName, PrintStream printer) {
		if (uri != null && !("".equals(uri)) ) printer.print("['"+uri+"']:");
		if (prefix != null) printer.print(prefix+":");
		if (localName != null) printer.print(localName);
	}

	private static void printAttributes(XMLStreamReader xmlr, PrintStream printer){
		for (int i=0; i < xmlr.getAttributeCount(); i++) {
			printAttribute(xmlr,i, printer);
		}
	}

	private static void printAttribute(XMLStreamReader xmlr, int index, PrintStream printer) {
		String prefix = xmlr.getAttributePrefix(index);
		String namespace = xmlr.getAttributeNamespace(index);
		String localName = xmlr.getAttributeLocalName(index);
		String value = xmlr.getAttributeValue(index);
		printer.print(" ");
		printName(prefix,namespace,localName, printer);
		printer.print("='"+value+"'");
	}

	private static void printNamespaces(XMLStreamReader xmlr, PrintStream printer) {
		for (int i=0; i < xmlr.getNamespaceCount(); i++) {
			printNamespace(xmlr,i, printer);
		}
	}

	private static void printNamespace(XMLStreamReader xmlr, int index, PrintStream printer) {
		String prefix = xmlr.getNamespacePrefix(index);
		String uri = xmlr.getNamespaceURI(index);
		printer.print(" ");
		if (prefix == null)
			printer.print("xmlns='"+uri+"'");
		else
			printer.print("xmlns:"+prefix+"='"+uri+"'");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
