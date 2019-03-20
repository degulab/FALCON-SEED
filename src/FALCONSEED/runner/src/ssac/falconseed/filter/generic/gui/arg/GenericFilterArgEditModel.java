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
 * @(#)GenericFilterArgEditModel.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericFilterArgEditModel.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.arg;

import javax.xml.stream.XMLStreamException;

import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.SchemaObject;
import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.io.SchemaConfigWriter;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeManager;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeString;
import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.runtime.util.Objects;
import ssac.falconseed.filter.common.gui.util.FilterArgUtil;
import ssac.falconseed.module.IModuleArgValue;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.swing.FilterArgEditModel;

/**
 * 汎用フィルタ編集ダイアログ専用の、フィルタ定義引数データモデル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericFilterArgEditModel extends FilterArgEditModel implements SchemaElementValue
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private SchemaValueType	_schemaValueType = SchemaValueTypeString.instance;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericFilterArgEditModel(final int argno, final ModuleArgData argdef) {
		super(argno, argdef);
	}
	
	public GenericFilterArgEditModel(final int argno, final ModuleArgType type, final String desc, final Object value) {
		super(argno, type, desc, value);
	}
	
	public GenericFilterArgEditModel(final int argno, final ModuleArgType type, final String desc, final IMExecArgParam param, final Object value) {
		super(argno, type, desc, param, value);
	}
	
	public GenericFilterArgEditModel(final int argno, final IModuleArgValue value) {
		super(argno, value);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement SchemaElementValue interfaces
	//------------------------------------------------------------

	@Override
	public boolean isValidElementNo() {
		return (getArgNo() > 0);
	}

	@Override
	public int getElementNo() {
		return getArgNo();
	}

	@Override
	public void setElementNo(int newElemNo) {
		setArgNo(newElemNo);
	}

	@Override
	public boolean updateElementNo(int newElemNo) {
		int curArgNo = getArgNo();
		if (newElemNo == curArgNo)
			return false;
		//--- modified
		setArgNo(newElemNo);
		return true;
	}

	@Override
	public boolean hasParentObject() {
		return false;
	}

	@Override
	public SchemaObject getParentObject() {
		return null;
	}

	@Override
	public void setParentObject(SchemaObject newParent) {
	}

	@Override
	public boolean updateParentObject(SchemaObject newParent) {
		return false;
	}

	@Override
	public int getInstanceId() {
		return System.identityHashCode(this);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setName(String newName) {
	}

	@Override
	public boolean updateName(String newName) {
		return false;
	}

	@Override
	public boolean updateDescription(String newDesc) {
		if (newDesc == null)
			newDesc = "";
		if (newDesc.equals(_defDesc))
			return false;
		//--- modified
		_defDesc = newDesc;
		return true;
	}

	@Override
	public String toNameString() {
		return FilterArgUtil.formatArgType(this);
	}

	@Override
	public String toVariableNameString() {
		return FilterArgUtil.formatArgType(this);
	}

	@Override
	public String toParamString() {
		return super.toString();
	}

	@Override
	public String toString() {
		return toVariableNameString() + ":" + _schemaValueType.toString();
	}

	@Override
	public String getXmlElementName() {
		return getClass().getName();
	}

	@Override
	public void writeToXml(SchemaConfigWriter writer) throws XMLStreamException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void readFromXml(SchemaConfigReader reader) throws XMLStreamException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public SchemaValueType getValueType() {
		return _schemaValueType;
	}

	@Override
	public void setValueType(SchemaValueType newValueType) {
		_schemaValueType = SchemaValueTypeManager.ensureValueTypeWithDefault(newValueType);
	}

	@Override
	public boolean updateValueType(SchemaValueType newValueType) {
		newValueType = SchemaValueTypeManager.ensureValueTypeWithDefault(newValueType);
		if (newValueType.equals(_schemaValueType))
			return false;
		//--- modified
		_schemaValueType = newValueType;
		return true;
	}

	@Override
	public boolean updateValue(Object newValue) {
		//--- 値の変更を優先するため、BigDecimal でも厳密に比較
		if (Objects.equals(newValue, super.getValue()))
			return false;
		super.setValue(newValue);
		return false;
	}

	//------------------------------------------------------------
	// Implement Object interfaces
	//------------------------------------------------------------

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj == this);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
