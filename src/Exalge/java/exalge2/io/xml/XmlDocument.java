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
 *  Copyright 2007-2014  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)XmlDocument.java	0.984	2014/04/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)XmlDocument.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 交換代数コア・パッケージの XML ドキュメントクラス。
 * <p>
 * このクラスは、{@link org.w3c.dom.Document}クラスの拡張であり、
 * 交換代数コア・パッケージ向けの XML に関する機能を提供する。
 * 
 * @version 0.984 2014/04/23
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.91
 *
 */
public class XmlDocument
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * 交換代数コア・パッケージにおける XML のキャラクターセット
	 */
	static public final String XML_CHARSET = "UTF-8";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * XML ドキュメントの実体
	 */
	private final Document xmldoc;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 空の XML ドキュメントを生成する。
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 */
	public XmlDocument()
		throws FactoryConfigurationError, ParserConfigurationException
	{
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		dbfactory.setValidating(false);
		
		DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
		
		this.xmldoc = docbuilder.newDocument();
	}

	/**
	 * 指定されたタグ名のエレメントを持つ XML ドキュメントを生成する。
	 * 
	 * @param docElementName XML ドキュメントに含めるタグ名
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws DOMException タグの生成に失敗した場合
	 */
	public XmlDocument(String docElementName)
		throws FactoryConfigurationError, ParserConfigurationException, DOMException
	{
		this();
		Element topElem = this.xmldoc.createElement(docElementName);
		this.xmldoc.appendChild(topElem);
	}

	/**
	 * 指定された {@link org.w3c.dom.Document} インスタンスを保持する XML ドキュメントを生成する。
	 * <p>
	 * このコンストラクタでは、指定されたドキュメントのインスタンスを内部に保持する。
	 * 
	 * @param doc {@link org.w3c.dom.Document} インスタンス
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public XmlDocument(Document doc) {
		if (doc == null) {
			throw new NullPointerException();
		}
		this.xmldoc = doc;
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	/**
	 * {@link org.w3c.dom.Document} インスタンスを返す。
	 * 
	 * @return {@link org.w3c.dom.Document} インスタンス
	 */
	public Document getDocument() {
		return this.xmldoc;
	}

	/**
	 * XML ドキュメントの最上位要素を返す。
	 * 要素が存在しない場合は <tt>null</tt> を返す。
	 * 
	 * @return XMLドキュメントの最上位要素
	 */
	public Element getDocumentElement() {
		return this.xmldoc.getDocumentElement();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * XML ドキュメントに要素を追加する。
	 * 
	 * @throws DOMException 要素追加に失敗した場合
	 */
	public void append(Node xmlNode) throws DOMException
	{
		this.xmldoc.appendChild(xmlNode);
	}

	/**
	 * 指定されたタグ名の要素を生成する。
	 * 
	 * @param tagName 要素のタグ名
	 * @return 生成された要素
	 * 
	 * @throws DOMException 要素の作成に失敗した場合
	 */
	public Element createElement(String tagName) throws DOMException
	{
		return this.xmldoc.createElement(tagName);
	}

	/**
	 * 指定された文字列を持つ Text ノードを生成する。
	 * 
	 * @param data ノードのデータ
	 * @return 生成された Text ノード
	 * 
	 * @throws DOMException Text ノードの生成に失敗した場合
	 */
	public Text createTextNode(String data) throws DOMException
	{
		return this.xmldoc.createTextNode(data);
	}
	
	//------------------------------------------------------------
	// I/O
	//------------------------------------------------------------

	/**
	 * XML文書を指定のファイルに出力する。
	 * 
	 * @param xmlFile XML 文書の出力先となるファイル
	 * 
	 * @throws TransformerConfigurationException <code>Transformer</code> インスタンスを生成できない場合
	 * @throws TransformerException ファイルへの変換中に回復不能なエラーが発生した場合
	 * 
	 */
	public void toFile(File xmlFile)
		throws TransformerConfigurationException, TransformerException
	{
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer transformer = tfactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, XML_CHARSET);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult sr = new StreamResult(xmlFile);
        transformer.transform(new DOMSource(this.xmldoc), sr); 
	}

	/**
	 * XML文書を文字列に変換する。
	 * 
	 * @return	XML書式の文字列
	 * @throws TransformerConfigurationException <code>Transformer</code> インスタンスを生成できない場合
	 * @throws TransformerException 文字列への変換中に回復不能なエラーが発生した場合
	 * 
	 * @since 0.984
	 */
	public String toXmlString()
		throws TransformerConfigurationException, TransformerException
	{
		StringWriter strw = new StringWriter();
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer transformer = tfactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, XML_CHARSET);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult sr = new StreamResult(strw);
		transformer.transform(new DOMSource(this.xmldoc), sr);
		return strw.toString();
	}

	/**
	 * 指定されたファイルから XML 文書を読み込み、<code>XmlDocument</code> インスタンスを生成する。
	 * 
	 * @param xmlFile XML 文書として読み込むファイル
	 * 
	 * @return 新しい <code>XmlDocument</code> インスタンス
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 */
	static public XmlDocument fromFile(File xmlFile)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException
	{
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		dbfactory.setValidating(false);
		dbfactory.setIgnoringComments(true);
		dbfactory.setExpandEntityReferences(true);
		
		DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
		Document dom = docbuilder.parse(xmlFile);
		
		return new XmlDocument(dom);
	}

	/**
	 * 指定されたXML書式の文字列から、<code>XmlDocument</code> インスタンスを生成する。
	 * @param xmlString	XML書式の文字列
	 * 
	 * @return 新しい <code>XmlDocument</code> インスタンス
	 * 
	 * @throws FactoryConfigurationError XML の実装が使用できないかインスタンス化できない場合
	 * @throws ParserConfigurationException DocumentBuilder を構成できない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws SAXException XML文書構成がエラーの場合
	 * 
	 * @since 0.984
	 */
	static public XmlDocument fromXmlString(String xmlString)
		throws FactoryConfigurationError, ParserConfigurationException, IOException, SAXException
	{
		DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		dbfactory.setValidating(false);
		dbfactory.setIgnoringComments(true);
		dbfactory.setExpandEntityReferences(true);

		InputSource isrc = new InputSource(new StringReader(xmlString));
		DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
		Document dom = docbuilder.parse(isrc);
		
		return new XmlDocument(dom);
	}
}
