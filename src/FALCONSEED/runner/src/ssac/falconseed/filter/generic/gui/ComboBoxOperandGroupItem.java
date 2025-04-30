/*
 * @(#)ComboBoxOperandGroupItem.java	3.2.0	2015/06/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui;

import javax.swing.ComboBoxModel;

/**
 * 汎用フィルタのプロパティ編集パネルにおけるオペランド値のグループと値リストとの対応を保持するクラス。
 * このオブジェクトが保持する値リストは、<code>ComboBoxModel</code> となる。
 * なお、このオブジェクトは不変である。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class ComboBoxOperandGroupItem
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 値グループを示す列挙値 **/
	private final GenericSchemaValueGroups	_group;
	/** 値グループに対応する <code>ComboBoxModel</code> インスタンス、もしくは <tt>null</tt> **/
	private final ComboBoxModel				_cmbModel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ComboBoxOperandGroupItem(GenericSchemaValueGroups group, ComboBoxModel cmbModel) {
		_group = group;
		_cmbModel = cmbModel;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public GenericSchemaValueGroups getValueGroup() {
		return _group;
	}
	
	public ComboBoxModel getComboBoxModel() {
		return _cmbModel;
	}

	@Override
	public String toString() {
		return _group.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
