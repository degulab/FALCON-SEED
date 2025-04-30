/*
 * @(#)ComboBoxExpressionOperandModel.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ComboBoxExpressionOperandModel.java	3.2.0	2015/06/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.exp;

import javax.swing.AbstractListModel;

import ssac.falconseed.filter.generic.gui.ComboBoxOperandValueModel;
import ssac.falconseed.filter.generic.gui.GenericSchemaValueGroups;

/**
 * 汎用フィルタの計算式定義に対応するコンボボックスモデル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class ComboBoxExpressionOperandModel extends AbstractListModel implements ComboBoxOperandValueModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 対象のデータモデル **/
	private final GenericExpressionSchemaTableModel	_targetModel;
	
	/** 選択したオブジェクト **/
	private GenericExpressionElementEditModel	_selectedObject;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ComboBoxExpressionOperandModel(GenericExpressionSchemaTableModel targetModel) {
		if (targetModel == null)
			throw new NullPointerException();
		_targetModel = targetModel;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public GenericExpressionSchemaTableModel getTargetModel() {
		return _targetModel;
	}

	//------------------------------------------------------------
	// Implement ComboBoxOperandValueModel
	//------------------------------------------------------------
	
	@Override
	public GenericSchemaValueGroups getValueGroup() {
		return GenericSchemaValueGroups.EXP;
	}
	
	@Override
	public void refresh() {
		GenericExpressionElementEditModel target = _selectedObject;
		if (target != null && !_targetModel.getDataModel().containsInstanceId(target)) {
			_selectedObject = null;
		}
		fireContentsChanged(this, -1, -1);
	}

	//------------------------------------------------------------
	// Implement AbstractListModel interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement ComboBoxModel interfaces
	//------------------------------------------------------------

	@Override
	public void setSelectedItem(Object anItem) {
		if (anItem != null) {
			if (!_targetModel.getDataModel().containsInstanceId(anItem)) {
				anItem = null;
			}
		}
		if (anItem == _selectedObject)
			return;
		
		_selectedObject = (GenericExpressionElementEditModel)anItem;
		fireContentsChanged(this, -1, -1);
	}

	@Override
	public Object getSelectedItem() {
		return _selectedObject;
	}

	//------------------------------------------------------------
	// Implement ListModel interfaces
	//------------------------------------------------------------

	@Override
	public int getSize() {
		return _targetModel.getDataModel().size();
	}

	@Override
	public Object getElementAt(int index) {
		return _targetModel.getDataModel().get(index);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
