/*
 * @(#)ComboBoxOperandValueModel.java	3.2.0	2015/06/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui;

import javax.swing.ComboBoxModel;

/**
 * 汎用フィルタのプロパティ編集パネルにおけるオペランド値のためのコンボボックスモデル。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public interface ComboBoxOperandValueModel extends ComboBoxModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public GenericSchemaValueGroups getValueGroup();
	
	public void refresh();
}
