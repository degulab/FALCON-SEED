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
 * @(#)SchemaConfigWriter.java	3.2.0	2015/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.SchemaConfig;

/**
 * 汎用フィルタ定義におけるスキーマ定義を書き込むクラス。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaConfigWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このクラスの書き込み先として指定されたオブジェクト **/
	private Object	_targetObject;
	/** XML 書き込み用オブジェクト **/
	private XMLStreamWriter	_xmlWriter;
	/** オリジナルのストリーム **/
	private OutputStream	_outStream;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたファイルに書き込みを行う、新しいインスタンスを生成する。
	 * @param file	書き込み対象ファイルの位置を示すパス
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルのオープンに失敗した場合
	 * @throws XMLStreamException		ストリームの作成に失敗した場合
	 */
	public SchemaConfigWriter(File file) throws FileNotFoundException, XMLStreamException
	{
		FileOutputStream fos = null;
		XMLStreamWriter xmlWriter = null;
		try {
			// open stream
			fos = new FileOutputStream(file);

			// create XML stream
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			xmlWriter = factory.createXMLStreamWriter(fos, SchemaXmlUtil.XML_ENCODING);
			
			// succeeded
			_xmlWriter = xmlWriter;
			_outStream = fos;
			_targetObject = file;
			xmlWriter = null;
			fos = null;
		}
		finally {
			if (xmlWriter != null) {
				xmlWriter.close();
				xmlWriter = null;
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Throwable ex) {
					// 例外は無視
				}
				fos = null;
			}
		}
	}
	
	public SchemaConfigWriter(OutputStream oStream) throws XMLStreamException
	{
		XMLStreamWriter xmlWriter = null;
		try {
			// create XML stream
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			xmlWriter = factory.createXMLStreamWriter(oStream, SchemaXmlUtil.XML_ENCODING);
			
			// succeeded
			_xmlWriter = xmlWriter;
			_outStream = oStream;
			if (_targetObject == null) {
				_targetObject = oStream;
			}
			xmlWriter = null;
		}
		finally {
			if (xmlWriter != null) {
				xmlWriter.close();
				xmlWriter = null;
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このクラスの書き込み先として指定されたオブジェクトを返す。
	 */
	public Object getTargetObject() {
		return _targetObject;
	}
	
	public void close() throws XMLStreamException
	{
		if (_xmlWriter != null) {
			_xmlWriter.close();	// 基本となる出力ストリームは閉じないとか言ってる
			_xmlWriter = null;
		}
		if (_outStream != null) {
			try {
				_outStream.close();
			} catch (Throwable ex) {}	// 例外は無視
			_outStream = null;
		}
	}

	/**
	 * XML を書き込むオブジェクトを取得する。
	 * @return <code>XMLStreamWriter</code> オブジェクト
	 */
	public XMLStreamWriter getXMLWriter() {
		return _xmlWriter;
	}

	/**
	 * 指定されたスキーマ定義を書き出す。
	 * @param config	書き出す対象のスキーマ定義
	 */
	public void writeConfig(SchemaConfig config) throws XMLStreamException
	{
		if (config == null)
			throw new NullPointerException();
		if (_xmlWriter == null)
			throw new IllegalStateException("XML Document Writer was closed!");
		
		// start document
		_xmlWriter.writeStartDocument(SchemaXmlUtil.XML_ENCODING, "1.0");
		_xmlWriter.writeCharacters("\r\n");
		
		// config
		config.writeToXml(this);
		
		// end document
		_xmlWriter.writeEndDocument();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
