/*
 * @(#)EmptyMacroSubFilterArgValueTableModel.java	3.1.0	2014/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EmptyMacroSubFilterArgValueTableModel.java	2.0.0	2012/10/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import ssac.falconseed.module.IModuleArgConfig;

/**
 * マクロフィルタを構成するサブフィルタの引数値設定用テーブルモデルのデータを持たない実装。
 * 
 * @version 3.1.0	2014/05/16
 * @since 2.0.0
 */
public class EmptyMacroSubFilterArgValueTableModel extends AbMacroSubFilterArgValueTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EmptyMacroSubFilterArgValueTableModel() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public int getRowCount() {
		return 0;
	}

	@Override
	public IModuleArgConfig getArgument(int rowIndex) {
		return null;
	}

	@Override
	public void setArgumentValue(int rowIndex, Object newValue) {
		// no entry
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
