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
 * @(#)SchemaOutputCsvDataTable	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaOutputCsvDataTable	3.2.0	2015/06/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema.table;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;

/**
 * CSV 形式での出力テーブルデータのスキーマを保持するクラス。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaOutputCsvDataTable extends SchemaCsvDataTable<SchemaOutputCsvDataField>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -8044708865924793019L;
	
	static protected final String	XmlAttrAutoHeaderRecord	= "AutoHeaderRecordEnabled";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 *  ヘッダーレコードをフィールド名から自動生成することを示すフラグ
	 *  @since 3.2.1
	 **/
	private boolean	_autoHeaderRecord;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のパラメータで、要素が空の新しいインスタンスを生成する。
	 */
	public SchemaOutputCsvDataTable() {
		super();
		_autoHeaderRecord = false;
	}

	/**
	 * 標準のパラメータで、指定された容量を持つ、要素が空の新しいインスタンスを生成する。
	 * @param initialCapacity	初期容量(0 以上)
	 * @throws IllegalArgumentException	<em>initialCapacity</em> が負の場合
	 */
	public SchemaOutputCsvDataTable(int initialCapacity) {
		super(initialCapacity);
		_autoHeaderRecord = false;
	}

	/**
	 * 指定されたオブジェクトの内容をコピーした、新しいインスタンスを生成する。
	 * @param copyListElements	コレクション要素をコピーする場合は <tt>true</tt>、コピーしない場合は <tt>false</tt>
	 * @param src	コピー元とするオブジェクト
	 * @throws NullPointerException	コピー元オブジェクトが <tt>null</tt> の場合
	 */
	public SchemaOutputCsvDataTable(boolean copyListElements, final SchemaOutputCsvDataTable src) {
		super(copyListElements, src);
		_autoHeaderRecord = src._autoHeaderRecord;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isAutoHeaderRecordEnabled() {
		return _autoHeaderRecord;
	}
	
	public void setAutoHeaderRecordEnabled(boolean toEnable) {
		_autoHeaderRecord = toEnable;
	}
	
	public boolean updateAutoHeaderRecordEnabled(boolean toEnable) {
		if (toEnable == _autoHeaderRecord)
			return false;
		//--- modified
		_autoHeaderRecord = toEnable;
		return true;
	}

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = 31 * h + (_autoHeaderRecord ? 1231 : 1237);
		return h;
	}

	//------------------------------------------------------------
	// Read Interfaces for XML
	//------------------------------------------------------------

	@Override
	protected boolean xmlParseAttributeValue(SchemaConfigReader reader, XMLStreamReader xmlReader, String attrName, String attrValue)
	throws XMLStreamException
	{
		// Auto header record enabled
		if (XmlAttrAutoHeaderRecord.equals(attrName)) {
			if (attrValue == null || attrValue.isEmpty()) {
				// auto header record disabled
				setAutoHeaderRecordEnabled(false);
			}
			else {
				// Boolean 変換
				setAutoHeaderRecordEnabled(Boolean.parseBoolean(attrValue));
			}
			return true;
		}
		
		return super.xmlParseAttributeValue(reader, xmlReader, attrName, attrValue);
	}

	//------------------------------------------------------------
	// Write Interfaces for XML
	//------------------------------------------------------------

	@Override
	protected void xmlWriteStartAttributes(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException {
		super.xmlWriteStartAttributes(writer, xmlWriter);
		
		xmlWriter.writeAttribute(XmlAttrAutoHeaderRecord, Boolean.toString(_autoHeaderRecord));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void appendParamString(StringBuilder buffer) {
		super.appendParamString(buffer);
		
		buffer.append(", autoHeaderRecordEnabled=");
		buffer.append(Boolean.toString(_autoHeaderRecord));
	}

	@Override
	protected boolean equalFields(Object obj) {
		if (!super.equalFields(obj))
			return false;
		
		SchemaOutputCsvDataTable aTable = (SchemaOutputCsvDataTable)obj;
		
		if (aTable._autoHeaderRecord != this._autoHeaderRecord)
			return false;
		
		// equal all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
