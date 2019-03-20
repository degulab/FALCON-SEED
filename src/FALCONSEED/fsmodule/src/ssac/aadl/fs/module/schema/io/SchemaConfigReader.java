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
 * @(#)SchemaConfigReader.java	3.2.0	2015/06/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import ssac.aadl.fs.module.schema.SchemaConfig;

/**
 * 汎用フィルタ定義におけるスキーマ定義を読み込むクラス。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaConfigReader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このクラスの読み込み元として指定されたオブジェクト **/
	private Object	_targetObject;
	/** XML 読み込み用オブジェクト **/
	private XMLStreamReader	_xmlReader;
	/** オリジナルのストリーム **/
	private InputStream	_inStream;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaConfigReader(File file) throws FileNotFoundException, XMLStreamException {
		InputStream inStream = null;
		XMLStreamReader xmlReader = null;
		try {
			// open stream
			inStream = new FileInputStream(file);
			inStream = new BufferedInputStream(inStream);
			
			// create XML stream
			XMLInputFactory factory = XMLInputFactory.newInstance();
			xmlReader = factory.createXMLStreamReader(inStream);
			
			// succeeded
			_xmlReader = xmlReader;
			_inStream  = inStream;
			_targetObject = file;
			xmlReader = null;
			inStream = null;
		}
		finally {
			if (xmlReader != null) {
				xmlReader.close();
				xmlReader = null;
			}
			if (inStream != null) {
				try {
					inStream.close();
				} catch (Throwable ex) {
					// 例外は無視
				}
				inStream = null;
			}
		}
	}
	
	public SchemaConfigReader(InputStream iStream) throws XMLStreamException
	{
		XMLStreamReader xmlReader = null;
		try {
			// open stream
			InputStream bif = new BufferedInputStream(iStream);
			
			// create XML stream
			XMLInputFactory factory = XMLInputFactory.newInstance();
			xmlReader = factory.createXMLStreamReader(bif);
			
			// succeeded
			_xmlReader = xmlReader;
			_inStream  = bif;
			_targetObject = iStream;
			xmlReader = null;
			bif = null;
		}
		finally {
			if (xmlReader != null) {
				xmlReader.close();
				xmlReader = null;
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

	/**
	 * ストリームを閉じる
	 * @throws XMLStreamException	XML ストリームが閉じられない場合
	 */
	public void close() throws XMLStreamException
	{
		if (_xmlReader != null) {
			_xmlReader.close();	// 基本となる出力ストリームは閉じないとか言ってる
			_xmlReader = null;
		}
		if (_inStream != null) {
			try {
				_inStream.close();
			} catch (Throwable ex) {}	// 例外は無視
			_inStream = null;
		}
	}

	/**
	 * XML を読み込むオブジェクトを取得する。
	 * @return	<code>XMLStreamReader</code> オブジェクト
	 */
	public XMLStreamReader getXMLReader() {
		return _xmlReader;
	}

	/**
	 * 現在のストリームを読み込み、スキーマ定義オブジェクトを生成する。
	 * @return	XML ストリームから復元されたスキーマ定義オブジェクト
	 * @throws XMLStreamException
	 */
	public SchemaConfig readConfig() throws XMLStreamException
	{
		if (_xmlReader == null)
			throw new IllegalStateException("XML Document Reader was closed!");
		if (!_xmlReader.hasNext())
			throw new IllegalStateException("XML Document is empty.");
		int eventType = _xmlReader.getEventType();
		if (eventType != XMLStreamConstants.START_DOCUMENT)
			throw new XMLStreamException("XML Start document tag not found.", _xmlReader.getLocation());
		
		// SchemaConfig tag
		_xmlReader.nextTag();
		
		// read SchemaConfig
		SchemaConfig config = new SchemaConfig();
		config.readFromXml(this);
		
		// check end event
		if (_xmlReader.hasNext()) {
			if (_xmlReader.next() != XMLStreamConstants.END_DOCUMENT) {
				// 無視
			}
		}
		
		// ignore after elements
		return config;
	}
	
	public void printConfig(PrintStream printer) throws XMLStreamException
	{
		if (_xmlReader == null)
			throw new IllegalStateException("XML Document Reader was closed!");
		
		// read current
		SchemaXmlUtil.printXmlEvent(_xmlReader, printer);
		
		// read
		for (; _xmlReader.hasNext(); ) {
			int eventType = _xmlReader.next();
			int curEventType = _xmlReader.getEventType();
			if (eventType != curEventType) {
				throw new IllegalStateException();
			}
			SchemaXmlUtil.printXmlEvent(_xmlReader, printer);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
