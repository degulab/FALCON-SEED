/*
 * @(#)EmptyFilterValuesEditModel.java	3.1.0	2014/05/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EmptyFilterValuesEditModel.java	2.0.0	2012/11/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.falconseed.module.ModuleArgConfig;

/**
 * フィルタ実行時引数値を保持する、要素が空のデータモデル。
 * 
 * @version 3.1.0	2014/05/18
 * @since 2.0.0
 */
public class EmptyFilterValuesEditModel extends AbFilterValuesEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public EmptyFilterValuesEditModel() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public boolean isEditing() {
		return false;
	}

	@Override
	public boolean isArgsEditable() {
		return false;
	}

	@Override
	public boolean isArgsHistoryEnabled() {
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	@Override
	protected boolean verifyInputArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		return true;	// no error
	}

	@Override
	protected boolean verifyOutputArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		return true;	// no error
	}

	@Override
	protected boolean verifyStringArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		return true;	// no error
	}

	@Override
	protected boolean verifyPublishArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		return true;	// no error
	}

	@Override
	protected boolean verifySubscribeArgumentValue(int argIndex, boolean withoutEvent, ModuleArgConfig argdata) {
		return true;	// no error
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
