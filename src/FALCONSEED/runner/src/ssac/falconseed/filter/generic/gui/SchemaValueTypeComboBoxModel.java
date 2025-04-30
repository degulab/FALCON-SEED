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
