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
 * @(#)SchemaValueTypeComboBoxModel.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeManager;

/**
 * 汎用フィルタのデータ型を選択するためのコンボボックスモデル。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class SchemaValueTypeComboBoxModel extends AbstractListModel implements ComboBoxModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 選択されているデータ型、選択されていない場合は <tt>null</tt> **/
	private SchemaValueType	_selectedType;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SchemaValueTypeComboBoxModel() {
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement ComboBoxMode interfaces
	//------------------------------------------------------------

	@Override
	public SchemaValueType getSelectedItem() {
		return _selectedType;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		if (anItem instanceof SchemaValueType) {
			_selectedType = (SchemaValueType)anItem;
		} else {
			_selectedType = null;
		}
	}

	//------------------------------------------------------------
	// Implement AbstractListModel interfaces
	//------------------------------------------------------------

	@Override
	public int getSize() {
		return SchemaValueTypeManager.getBasicTypeList().size();
	}

	@Override
	public Object getElementAt(int index) {
		return SchemaValueTypeManager.getBasicTypeList().get(index);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
