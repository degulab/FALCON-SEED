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
 * @(#)SchemaDTFormatConfig.java	3.2.1	2015/07/21
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
import ssac.aadl.fs.module.schema.type.SchemaDateTimeFormats;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeDateTime;
import ssac.aadl.runtime.util.Objects;

/**
 * 日付時刻書式の設定を保持するクラス。
 * @version 3.2.1
 * @since 3.2.1
 */
public class SchemaDTFormatConfig extends AbSchemaObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static protected final String	DTFPatternsXmlElement			= "DTFormatPatterns";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 日付時刻書式フォーマット **/
	private SchemaDateTimeFormats	_dtformat;
	/** 日付時刻書式を取得するフィルタ定義引数 **/
	private SchemaFilterArgValue	_filterArg;
	/** 日付時刻パターンのカンマ区切りの文字列 **/
	private String					_patterns;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaDTFormatConfig() {
		super();
		_dtformat   = null;
		_filterArg  = null;
		_patterns   = null;
	}
	
	public SchemaDTFormatConfig(final SchemaDTFormatConfig src) {
		_dtformat   = src._dtformat;
		_filterArg  = src._filterArg;
		_patterns   = src._patterns;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトの状態が標準状態(初期状態)かどうかを判定する。
	 * 標準状態とは、フィルタ定義引数が指定されておらず、パターンも登録されていない状態を指す。
	 * @return	標準状態であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isDefault() {
		return (_filterArg == null && getDateTimeFormats().isOnlyDefaultFormat());
	}

	/**
	 * 現在の設定に基づく、日付時刻書式オブジェクトを取得する。
	 * @return	<code>SchemaDateTimeFormats</code> オブジェクト
	 * @throws IllegalArgumentException	フィルタ引数の値もしくは登録されたパターンが、日付時刻書式として正しくない場合
	 */
	public SchemaDateTimeFormats getDateTimeFormats() {
		if (_dtformat == null) {
			_dtformat = getAvailableDateTimeFormats();
		}
		return _dtformat;
	}
	
	public boolean hasFilterArgument() {
		return (_filterArg != null);
	}
	
	public SchemaFilterArgValue getFilterArgument() {
		return _filterArg;
	}
	
	public void setFilterArgument(SchemaFilterArgValue arg) {
		_filterArg = arg;
		_dtformat = null;
	}
	
	public boolean updateFilterArgument(SchemaFilterArgValue arg) {
		if (Objects.equals(arg, _filterArg))
			return false;
		//--- modified
		_filterArg = arg;
		_dtformat = null;
		return true;
	}
	
	public String getPatternString() {
		return _patterns;
	}
	
	public void setPatternString(String patternText) {
		setPattern(new SchemaDateTimeFormats(patternText));
	}
	
	public void setPattern(SchemaDateTimeFormats dtf) {
		if (dtf != null && !dtf.isOnlyDefaultFormat())
			_patterns = dtf.toString();
		else
			_patterns = null;
		_dtformat = null;
	}
	
	public boolean updatePatternString(String patternText) {
		return updatePattern(new SchemaDateTimeFormats(patternText));
	}
	
	public boolean updatePattern(SchemaDateTimeFormats dtf) {
		String strPattern;
		if (dtf != null && !dtf.isOnlyDefaultFormat())
			strPattern = dtf.toString();
		else
			strPattern = null;
		if (Objects.equals(strPattern, _patterns))
			return false;
		//--- modified
		_patterns = strPattern;
		_dtformat = null;
		return true;
	}

	//------------------------------------------------------------
	// Implement AbSchemaObject interfaces
	//------------------------------------------------------------
	
	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj == this);
	}

	@Override
	public String toString() {
		return toParamString();
	}

	@Override
	protected void appendParamString(StringBuilder buffer) {
		super.appendParamString(buffer);
		
		buffer.append(", filterArg=");
		buffer.append(_filterArg==null ? "null" : _filterArg.toParamString());
		
		buffer.append(", patterns=");
		buffer.append(_patterns);
	}

	//------------------------------------------------------------
	// Read Interfaces for XML
	//------------------------------------------------------------

	/**
	 * 指定されたリーダーオブジェクトの現在の位置から XML 表現を読み込み、このオブジェクトの内容を復元する。
	 * このメソッドで処理した場合、そのタグの END_ELEMENT が読み込み位置となっていることを前提とする。
	 * @param reader	専用のリーダーオブジェクト
	 * @throws XMLStreamException	XML 入力に失敗した場合
	 */
	@Override
	public void readFromXml(SchemaConfigReader reader) throws XMLStreamException {
		// check
		if (!getXmlElementName().equals(reader.getXMLReader().getLocalName())) {
			// Not SchemaDTFormatConfig object : skipped
			return;
		}
		
		super.readFromXml(reader);
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
		
		if (xmlParseDTFormatPatterns(reader, xmlReader, tagName))
			return true;
		
		return super.xmlParseChildElement(reader, xmlReader, tagName);
	}
	
	protected boolean xmlParseDTFormatPatterns(SchemaConfigReader reader, XMLStreamReader xmlReader, String tagName) throws XMLStreamException
	{
		assert (xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT);
		if (!DTFPatternsXmlElement.equals(tagName))
			return false;
		
		// check text
		String strText = xmlReader.getElementText();
		try {
			SchemaDateTimeFormats dtf = new SchemaDateTimeFormats(strText);
			if (dtf.isOnlyDefaultFormat()) {
				_patterns = null;
			} else {
				_patterns = dtf.toString();
			}
		}
		catch (Throwable ex) {
			String msg = String.format("<%s> : Text is not Date time format patterns : value=[%s]",
					DTFPatternsXmlElement, strText);
			throw new XMLStreamException(msg, xmlReader.getLocation(), ex);
		}
		_dtformat = null;
		
		// end element
		SchemaXmlUtil.xmlValidEndElement(reader.getXMLReader(), DTFPatternsXmlElement);
		return true;
	}

	//------------------------------------------------------------
	// Write Interfaces for XML
	//------------------------------------------------------------

	@Override
	protected void xmlWriteBodyElements(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		super.xmlWriteBodyElements(writer, xmlWriter);
		
		if (_filterArg != null) {
			_filterArg.writeToXml(writer);
		}
		
		xmlWriteDTFormatPatternSet(writer, xmlWriter);
	}

	/**
	 * 日付時刻書式パターン集合の XML 表現を出力する。
	 * @param writer	専用のライターオブジェクト
	 * @param xmlWriter	現在の XML ライターオブジェクト
	 * @throws XMLStreamException	書き込みに失敗した場合
	 */
	protected void xmlWriteDTFormatPatternSet(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		xmlWriter.writeStartElement(DTFPatternsXmlElement);
		
		if (_patterns == null) {
			xmlWriter.writeCharacters(_patterns);
		}
		
		xmlWriter.writeEndElement();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected SchemaDateTimeFormats getAvailableDateTimeFormats() {
		if (_filterArg != null) {
			// use filter argument
			Object argValue = _filterArg.getValue();
			if (argValue == null) {
				// default
				return SchemaValueTypeDateTime.instance.getSubParameter();
			} else {
				return new SchemaDateTimeFormats(argValue.toString());
			}
		}
		else if (_patterns != null) {
			// custom
			return new SchemaDateTimeFormats(_patterns);
		}
		else {
			// default
			return SchemaValueTypeDateTime.instance.getSubParameter();
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
