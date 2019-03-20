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
 * @(#)SchemaConfig.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaConfig.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ssac.aadl.fs.module.schema.SchemaValueLink.SchemaValueLinkElemLocation;
import ssac.aadl.fs.module.schema.SchemaValueLink.SchemaValueLinkParameter;
import ssac.aadl.fs.module.schema.exp.SchemaExpressionData;
import ssac.aadl.fs.module.schema.exp.SchemaExpressionList;
import ssac.aadl.fs.module.schema.exp.SchemaJoinConditionData;
import ssac.aadl.fs.module.schema.exp.SchemaJoinConditionList;
import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;
import ssac.aadl.fs.module.schema.io.SchemaXmlUtil;
import ssac.aadl.fs.module.schema.table.SchemaInputCsvDataField;
import ssac.aadl.fs.module.schema.table.SchemaInputCsvDataTable;
import ssac.aadl.fs.module.schema.table.SchemaInputDataTableList;
import ssac.aadl.fs.module.schema.table.SchemaOutputCsvDataField;
import ssac.aadl.fs.module.schema.table.SchemaOutputCsvDataTable;
import ssac.aadl.fs.module.schema.table.SchemaOutputDataTableList;
import ssac.aadl.runtime.util.Objects;

/**
 * 汎用フィルタ定義におけるスキーマ定義を保持するクラス。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class SchemaConfig extends AbSchemaObject
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String	XmlElement						= "SchemaConfig";
//	static protected final String	XmlAttrSchemaName				= "SchemaName";
	static protected final String	XmlAttrAutoDocument				= "AutoDocument";
	static protected final String	FilterArgXmlElement				= "FilterArguments";
	static protected final String	InputTablesXmlElement			= "InputTables";
	static protected final String	OutputTablesXmlElement			= "OutputTables";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** スキーマ定義に関する説明を自動生成するかどうかのフラグ **/
	private boolean						_autoSchemaDocument;
	/** 日付時刻書式設定 **/
	private SchemaDTFormatConfig		_configDtFormats;
	/** フィルター定義引数のリスト **/
	private SchemaFilterArgList			_filterArgList;
	/** 入力テーブル定義のリスト **/
	private SchemaInputDataTableList	_inputTableList;
	/** 出力テーブル定義のリスト **/
	private SchemaOutputDataTableList	_outputTableList;
	/** 計算式定義のリスト **/
	private SchemaExpressionList		_expList;
	/** 結合条件定義のリスト **/
	private SchemaJoinConditionList		_joinList;
	/** 参照されている入力テーブルのマップ **/
	private Map<Integer, SchemaInputCsvDataField>	_referencedInputFieldMap;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaConfig() {
		_autoSchemaDocument = false;
		_configDtFormats = new SchemaDTFormatConfig();
		_filterArgList   = new SchemaFilterArgList();
		_inputTableList  = new SchemaInputDataTableList();
		_outputTableList = new SchemaOutputDataTableList();
		_expList         = new SchemaExpressionList();
		_joinList        = new SchemaJoinConditionList();
		_referencedInputFieldMap = new HashMap<Integer, SchemaInputCsvDataField>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * スキーマ定義に関するドキュメントを自動生成するかどうかの設定を取得する。
	 * @return	自動生成する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 3.2.1
	 */
	public boolean getAutoSchemaDocumentEnabled() {
		return _autoSchemaDocument;
	}

	/**
	 * スキーマ定義に関するドキュメントを自動生成するかどうかを設定する。
	 * @param toEnable	自動生成する場合は <tt>true</tt>、しない場合は <tt>false</tt>
	 * @since 3.2.1
	 */
	public void setAutoSchemaDocumentEnabled(boolean toEnable) {
		_autoSchemaDocument = toEnable;
	}

	/**
	 * 最低限必要な引数の数を返す。
	 * @return	フィルタ引数の最低必要数
	 */
	public int getLeastFilterArgumentCount() {
		return _filterArgList.size();
	}
	
	public Collection<SchemaInputCsvDataField> getReferencedInputFields() {
		return Collections.unmodifiableCollection(_referencedInputFieldMap.values());
	}
	
	public boolean isReferencedInputCsvDataField(SchemaInputCsvDataField field) {
		return _referencedInputFieldMap.containsKey(field.getInstanceId());
	}
	
	public SchemaFilterArgList getFilterArgList() {
		return _filterArgList;
	}
	
	public SchemaDTFormatConfig getDateTimeFormatConfig() {
		return _configDtFormats;
	}
	
	public SchemaInputDataTableList getInputTableList() {
		return _inputTableList;
	}
	
	public SchemaOutputDataTableList getOutputTableList() {
		return _outputTableList;
	}
	
	public SchemaExpressionList getExpressionList() {
		return _expList;
	}
	
	public SchemaJoinConditionList getJoinConditionList() {
		return _joinList;
	}
	
	public boolean refreshAllElementNumbers() {
		boolean modified = false;
		
		if (_filterArgList.refreshAllElementNumbers())
			modified = true;
		
		if (_inputTableList.refreshAllElementNumbers())
			modified = true;
		
		if (_outputTableList.refreshAllElementNumbers())
			modified = true;
		
		if (_expList.refreshAllElementNumbers())
			modified = true;
		
		if (_joinList.refreshAllElementNumbers())
			modified = true;
		
		return modified;
	}

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = 31 * h + (_autoSchemaDocument ? 1231 : 1237);
		h = 31 * h + (_filterArgList == null ? 0 : _filterArgList.hashCode());
		h = 31 * h + (_configDtFormats==null ? 0 : _configDtFormats.hashCode());
		h = 31 * h + (_inputTableList == null ? 0 : _inputTableList.hashCode());
		h = 31 * h + (_outputTableList == null ? 0 : _outputTableList.hashCode());
		h = 31 * h + (_expList == null ? 0 : _expList.hashCode());
		h = 31 * h + (_joinList == null ? 0 : _joinList.hashCode());
		return h;
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
		XMLStreamReader xmlReader = reader.getXMLReader();
		
		// start element
		SchemaXmlUtil.xmlValidStartElement(xmlReader, getXmlElementName());
		xmlParseAttributes(reader, xmlReader);
		//--- 次のタグへ
		xmlReader.nextTag();
		
		// Description
		if (xmlParseDescriptionElement(reader, xmlReader, xmlReader.getLocalName())) {
			xmlReader.nextTag();
		}
		
		// Filter argument list(required)
		_filterArgList.readFromXml(reader);
		xmlReader.nextTag();
		
		// DateTime format config(optional)
		if (xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT && _configDtFormats.getXmlElementName().equals(xmlReader.getLocalName()))
		{
			_configDtFormats.readFromXml(reader);
			xmlReader.nextTag();
		}
		
		// Input table list(required)
		_inputTableList.readFromXml(reader);
		xmlReader.nextTag();
		
		// Expression list(required)
		_expList.readFromXml(reader);
		xmlReader.nextTag();
		
		// Join condition list(required)
		_joinList.readFromXml(reader);
		xmlReader.nextTag();
		
		// Output table list(required)
		_outputTableList.readFromXml(reader);
		xmlReader.nextTag();
		
		// update link parameter to object
		updateLinkParameterToInstance();
		
		// end element
		SchemaXmlUtil.xmlValidEndElement(reader.getXMLReader(), getXmlElementName());
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
		// 自動ドキュメント生成フラグの取得
		if (XmlAttrAutoDocument.equals(attrName)) {
			if (attrValue == null || attrValue.isEmpty()) {
				// auto document disabled
				setAutoSchemaDocumentEnabled(false);
			}
			else {
				// Boolean 変換
				setAutoSchemaDocumentEnabled(Boolean.parseBoolean(attrValue));
			}
			return true;
		}
		
		// 基本クラスの処理
		return xmlParseNameAttributeValue(reader, xmlReader, attrName, attrValue);
	}
	
	protected void updateLinkParameterToInstance() throws XMLStreamException
	{
		// clear referenced map
		_referencedInputFieldMap.clear();
		
		// DateTime format config
		{
			SchemaFilterArgValue argConfig = _configDtFormats.getFilterArgument();
			if (argConfig != null) {
				SchemaFilterArgValue linkedArgConfig = _filterArgList.get(argConfig.getArgumentIndex());
				_configDtFormats.setFilterArgument(linkedArgConfig);
			}
		}
		
		// input tables
		for (SchemaInputCsvDataTable table : _inputTableList) {
			SchemaFilterArgValue argConfig = table.getFilterArgument();
			SchemaFilterArgValue linkedArgConfig = _filterArgList.get(argConfig.getArgumentIndex());
			table.setFilterArgument(linkedArgConfig);
		}
		
		// expression list
		for (SchemaExpressionData exp : _expList) {
			//--- left operand
			SchemaElementValue obj = exp.getLeftOperand();
			if (obj instanceof SchemaValueLink) {
				valueLinkParameterToInstance(exp, (SchemaValueLink)obj);
			}
			
			//--- right operand
			obj = exp.getRightOperand();
			if (obj instanceof SchemaValueLink) {
				valueLinkParameterToInstance(exp, (SchemaValueLink)obj);
			}
		}
		
		// join condition
		for (SchemaJoinConditionData join : _joinList) {
			//--- left operand
			SchemaValueLink link = (SchemaValueLink)join.getLeftOperand();
			if (link != null) {
				valueLinkParameterToInstance(join, link);
			}
			
			//--- right operand
			link = (SchemaValueLink)join.getRightOperand();
			if (link != null) {
				valueLinkParameterToInstance(join, link);
			}
		}
		
		// output tables
		for (SchemaOutputCsvDataTable table : _outputTableList) {
			SchemaFilterArgValue argConfig = table.getFilterArgument();
			SchemaFilterArgValue linkedArgConfig = _filterArgList.get(argConfig.getArgumentIndex());
			table.setFilterArgument(linkedArgConfig);
			for (SchemaOutputCsvDataField field : table) {
				SchemaElementValue obj = field.getTargetValue();
				if (obj instanceof SchemaValueLink) {
					valueLinkParameterToInstance(field, (SchemaValueLink)obj);
				}
			}
		}
	}
	
	protected SchemaValueObject valueLinkParameterToInstance(SchemaObject dependent, SchemaValueLink valueLink) throws XMLStreamException
	{
		SchemaValueLinkParameter linkParam = valueLink.getLinkParameter();
		if (linkParam == null || linkParam.getLinkList().isEmpty()) {
			String msg = String.format("<%s> Linked value instance not found.", dependent.toVariableNameString());
			throw new XMLStreamException(msg);
		}
		
		// check first link
		SchemaValueLinkElemLocation elemPos = linkParam.getLinkList().get(0);
		int elemNo = elemPos.elementNo;
		String className = elemPos.className;
		if (SchemaInputCsvDataTable.class.getName().equals(className)) {
			if (elemNo > 0 && elemNo <= _inputTableList.size()) {
				SchemaInputCsvDataTable table = _inputTableList.get(elemNo-1);
				if (linkParam.getLinkList().size() > 1) {
					elemPos = linkParam.getLinkList().get(1);
					elemNo = elemPos.elementNo;
					className = elemPos.className;
					if (SchemaInputCsvDataField.class.getName().equals(className)) {
						if (elemNo > 0 && elemNo <= table.size()) {
							SchemaInputCsvDataField value = table.get(elemNo-1);
							valueLink.clearLinkParameter();
							valueLink.setLinkTarget(value);
							//--- add reference
							_referencedInputFieldMap.put(value.getInstanceId(), value);
							return value;
						}
					}
				}
			}
		}
		else if (SchemaOutputCsvDataTable.class.getName().equals(className)) {
			if (elemNo > 0 && elemNo <= _outputTableList.size()) {
				SchemaOutputCsvDataTable table = _outputTableList.get(elemNo-1);
				if (linkParam.getLinkList().size() > 1) {
					elemPos = linkParam.getLinkList().get(1);
					elemNo = elemPos.elementNo;
					className = elemPos.className;
					if (SchemaOutputCsvDataField.class.getName().equals(className)) {
						if (elemNo > 0 && elemNo <= table.size()) {
							SchemaOutputCsvDataField value = table.get(elemNo-1);
							valueLink.clearLinkParameter();
							valueLink.setLinkTarget(value);
							return value;
						}
					}
				}
			}
		}
		else if (SchemaExpressionData.class.getName().equals(className)) {
			if (elemNo > 0 && elemNo <= _expList.size()) {
				SchemaExpressionData value = _expList.get(elemNo-1);
				valueLink.clearLinkParameter();
				valueLink.setLinkTarget(value);
				return value;
			}
		}
		else if (SchemaExpressionList.class.getName().equals(className)) {
			if (linkParam.getLinkList().size() > 1) {
				elemPos = linkParam.getLinkList().get(1);
				elemNo = elemPos.elementNo;
				className = elemPos.className;
				if (SchemaExpressionData.class.getName().equals(className)) {
					if (elemNo > 0 && elemNo <= _expList.size()) {
						SchemaExpressionData value = _expList.get(elemNo-1);
						valueLink.clearLinkParameter();
						valueLink.setLinkTarget(value);
						return value;
					}
				}
			}
		}
		else if (SchemaJoinConditionData.class.getName().equals(className)) {
			if (elemNo > 0 && elemNo <= _joinList.size()) {
				SchemaJoinConditionData value = _joinList.get(elemNo-1);
				valueLink.clearLinkParameter();
				valueLink.setLinkTarget(value);
				return value;
			}
		}
		else if (SchemaJoinConditionList.class.getName().equals(className)) {
			if (linkParam.getLinkList().size() > 1) {
				elemPos = linkParam.getLinkList().get(1);
				elemNo = elemPos.elementNo;
				className = elemPos.className;
				if (SchemaJoinConditionData.class.getName().equals(className)) {
					if (elemNo > 0 && elemNo <= _joinList.size()) {
						SchemaJoinConditionData value = _joinList.get(elemNo-1);
						valueLink.clearLinkParameter();
						valueLink.setLinkTarget(value);
						return value;
					}
				}
			}
		}
		else if (SchemaFilterArgValue.class.getName().equals(className)) {
			if (elemNo > 0 && elemNo <= _filterArgList.size()) {
				SchemaFilterArgValue value = _filterArgList.get(elemNo-1);
				valueLink.clearLinkParameter();
				valueLink.setLinkTarget(value);
				return value;
			}
		}
		else if (SchemaFilterArgList.class.getName().equals(className)) {
			if (linkParam.getLinkList().size() > 1) {
				elemPos = linkParam.getLinkList().get(1);
				elemNo = elemPos.elementNo;
				className = elemPos.className;
				if (SchemaFilterArgValue.class.getName().equals(className)) {
					if (elemNo > 0 && elemNo <= _filterArgList.size()) {
						SchemaFilterArgValue value = _filterArgList.get(elemNo-1);
						valueLink.clearLinkParameter();
						valueLink.setLinkTarget(value);
						return value;
					}
				}
			}
		}

		// unsupported class path
		String msg = String.format("<%s> Linked value's path is unsupported : %s", dependent.toVariableNameString(), className);
		throw new XMLStreamException(msg, linkParam.getXmlLocation());
	}

	//------------------------------------------------------------
	// Write Interfaces for XML
	//------------------------------------------------------------

	@Override
	protected void xmlWriteStartAttributes(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException {
		super.xmlWriteStartAttributes(writer, xmlWriter);

		// auto document flag
		xmlWriter.writeAttribute(XmlAttrAutoDocument, Boolean.toString(_autoSchemaDocument));
	}

	@Override
	protected void xmlWriteBodyElements(SchemaConfigWriter writer, XMLStreamWriter xmlWriter) throws XMLStreamException
	{
		super.xmlWriteBodyElements(writer, xmlWriter);
		
		// Filter argument list(required)
		_filterArgList.writeToXml(writer);
		
		// DateTime format config(optional)
		if (!_configDtFormats.isDefault()) {
			_configDtFormats.writeToXml(writer);
		}
		
		// Input table list(required)
		_inputTableList.writeToXml(writer);
		
		// Expression list(required)
		_expList.writeToXml(writer);
		
		// Join condition list(required)
		_joinList.writeToXml(writer);
		
		// Output table list(required)
		_outputTableList.writeToXml(writer);
	}

	@Override
	protected void appendParamString(StringBuilder buffer) {
		super.appendParamString(buffer);
		
		buffer.append(", autoSchemaDocument=");
		buffer.append(_autoSchemaDocument);
		
		buffer.append(", filterArgList=");
		buffer.append(_filterArgList.toParamString());
		
		buffer.append(", dateTimeFormatConfig=");
		buffer.append(_configDtFormats.toParamString());
		
		buffer.append(", inputTableList=");
		buffer.append(_inputTableList.toParamString());
		
		buffer.append(", expList=");
		buffer.append(_expList.toParamString());
		
		buffer.append(", joinList=");
		buffer.append(_joinList.toParamString());
		
		buffer.append(", outputTableList=");
		buffer.append(_outputTableList.toParamString());
	}

	@Override
	protected boolean equalFields(Object obj) {
		if (!super.equalFields(obj))
			return false;
		
		SchemaConfig aConfig = (SchemaConfig)obj;
		
		if (aConfig._autoSchemaDocument != this._autoSchemaDocument)
			return false;
		
		if (!Objects.equals(aConfig._filterArgList, this._filterArgList))
			return false;
		
		if (!Objects.equals(aConfig._configDtFormats, this._configDtFormats))
			return false;
		
//		if (!Objects.equals(aConfig._inputTableList, this._inputTableList))
//			return false;
		
		if (!Objects.equals(aConfig._expList, this._expList))
			return false;
		
		if (!Objects.equals(aConfig._joinList, this._joinList))
			return false;
		
//		if (!Objects.equals(aConfig._outputTableList, this._outputTableList))
//			return false;
		
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
