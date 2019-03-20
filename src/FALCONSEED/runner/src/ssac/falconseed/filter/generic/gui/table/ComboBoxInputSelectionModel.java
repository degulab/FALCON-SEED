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
 * @(#)ComboBoxInputSelectionModel.java	3.2.1	2015/07/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.table;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import ssac.falconseed.filter.generic.gui.ComboBoxOperandValueModel;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.falconseed.filter.generic.gui.GenericSchemaValueGroups;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaElementTreeNode;

/**
 * 汎用フィルタの入力定義における入力フィールドに対応するコンボボックスモデル。
 * 
 * @version 3.2.1
 * @since 3.2.1
 */
public class ComboBoxInputSelectionModel extends AbstractListModel implements ComboBoxOperandValueModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 対象のデータモデル **/
	private final GenericFilterEditModel	_editModel;

	/** フィールド選択用コンボボックスモデル **/
	private final ComboBoxInputFieldModel	_fieldComboBoxModel;
	
	/** 選択対象の入力テーブルツリーノード **/
	private GenericTableSchemaTreeNode		_selectedTableNode;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ComboBoxInputSelectionModel(GenericFilterEditModel editModel) {
		if (editModel == null)
			throw new NullPointerException("GenericFilterEditModel object is null.");
		_editModel = editModel;
		_selectedTableNode = null;
		_fieldComboBoxModel = new ComboBoxInputFieldModel();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public GenericFilterEditModel getEditModel() {
		return _editModel;
	}
	
	public ComboBoxModel getFieldComboBoxModel() {
		return _fieldComboBoxModel;
	}

	//------------------------------------------------------------
	// Implement ComboBoxOperandValueModel
	//------------------------------------------------------------
	
	@Override
	public GenericSchemaValueGroups getValueGroup() {
		return GenericSchemaValueGroups.INPUT;
	}

	@Override
	public void refresh() {
		fireContentsChanged(this, -1, -1);
	}

	//------------------------------------------------------------
	// Implement AbstractListModel interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement ComboBoxModel interfaces
	//------------------------------------------------------------

	@Override
	public GenericTableSchemaTreeNode getSelectedItem() {
		return _selectedTableNode;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		InputCsvFieldSchemaEditModel targetFieldModel;
		GenericTableSchemaTreeNode   targetTableNode;
		if (anItem instanceof GenericTableSchemaTreeNode) {
			// specified table node
			targetFieldModel = null;
			targetTableNode = (GenericTableSchemaTreeNode)anItem;
			GenericTableSchemaRootNode rootNode = _editModel.getInputTableSchemaRootNode();
			if (rootNode.getIndex(targetTableNode) < 0) {
				// unknown table node
				targetTableNode = null;
			}
		}
		else if (anItem instanceof InputCsvTableSchemaEditModel) {
			// specified table data model
			targetTableNode = _editModel.findInputTableSchemaTreeNode((InputCsvTableSchemaEditModel)anItem);
			targetFieldModel = null;
		}
		else if (anItem instanceof InputCsvFieldSchemaEditModel) {
			// find table node by field data model
			InputCsvFieldSchemaEditModel fieldModel = (InputCsvFieldSchemaEditModel)anItem;
			InputCsvTableSchemaEditModel tableModel = (InputCsvTableSchemaEditModel)fieldModel.getParentObject();
			targetTableNode = _editModel.findInputTableSchemaTreeNode(tableModel);
			if (targetTableNode != null) {
				targetFieldModel = fieldModel;
			} else {
				targetFieldModel = null;
			}
		}
		else {
			targetTableNode = null;
			targetFieldModel = null;
		}
		
		if (targetTableNode == _selectedTableNode) {
			// no changes
			if (targetFieldModel != null) {
				_fieldComboBoxModel.setSelectedItem(targetFieldModel);
			}
		}
		else {
			// table changed
			_selectedTableNode = targetTableNode;
			_fieldComboBoxModel._selectedField = targetFieldModel;
			_fieldComboBoxModel.refresh();
			fireContentsChanged(this, -1, -1);
		}
	}

	//------------------------------------------------------------
	// Implement ListModel interfaces
	//------------------------------------------------------------

	@Override
	public int getSize() {
		return _editModel.getInputTableSchemaCount();
	}

	@Override
	public GenericTableSchemaTreeNode getElementAt(int index) {
		return _editModel.getInputTableSchemaTreeNode(index);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * 汎用フィルタの入力定義における入力フィールドに対応するコンボボックスモデル。
	 * @version 3.2.1
	 * @since 3.2.1
	 */
	protected class ComboBoxInputFieldModel extends AbstractListModel implements ComboBoxOperandValueModel
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------

		private static final long serialVersionUID = 1L;

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		/** 選択したフィールドオブジェクト **/
		private InputCsvFieldSchemaEditModel	_selectedField;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Implement ComboBoxOperandValueModel interfaces
		//------------------------------------------------------------

		@Override
		public GenericSchemaValueGroups getValueGroup() {
			return GenericSchemaValueGroups.INPUT;
		}

		@Override
		public void refresh() {
			fireContentsChanged(this, -1, -1);
		}

		//------------------------------------------------------------
		// Implement ComboBoxModel interfaces
		//------------------------------------------------------------

		@Override
		public InputCsvFieldSchemaEditModel getSelectedItem() {
			return _selectedField;
		}

		@Override
		public void setSelectedItem(Object anItem) {
			if (anItem == _selectedField)
				return;		// no changes
			
			if (_selectedTableNode != null && anItem instanceof InputCsvFieldSchemaEditModel) {
				InputCsvFieldSchemaEditModel fieldModel = (InputCsvFieldSchemaEditModel)anItem;
				if (_selectedTableNode.getUserObject() == fieldModel.getParentObject()) {
					// anItem object is current table's field object
					_selectedField = fieldModel;
				}
				else {
					// anItem is not current table's field
					_selectedField = null;
				}
			}
			else {
				// anItem is not field
				_selectedField = null;
			}
			//--- refresh model
			fireContentsChanged(this, -1, -1);
		}

		//------------------------------------------------------------
		// Implement ListModel interfaces
		//------------------------------------------------------------

		@Override
		public int getSize() {
			return (_selectedTableNode==null ? 0 : _selectedTableNode.getChildCount());
		}

		@Override
		public InputCsvFieldSchemaEditModel getElementAt(int index) {
			GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)_selectedTableNode.getChildAt(index);
			return (InputCsvFieldSchemaEditModel)fieldNode.getUserObject();
		}
	}
}
