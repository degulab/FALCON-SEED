/*
 * @(#)EmptyMExecArgsDialogHandler.java	1.22	2012/08/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.util.io.VirtualFile;

/**
 * 何も処理を行わない、モジュール実行時引数ダイアログ用ハンドラ。
 * @version 1.22	2012/08/21
 * @since 1.22
 */
public class EmptyMExecArgsDialogHandler implements MExecArgsDialogHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final EmptyMExecArgsDialogHandler _instance = new EmptyMExecArgsDialogHandler();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public final EmptyMExecArgsDialogHandler getInstance() {
		return _instance;
	}
	
	public boolean doCloseFileOnEditor(IMExecArgsDialog dlg, VirtualFile file) {
		// 常に <tt>false</tt> を返す。
		return false;
	}

	public boolean doOpenFileOnEditor(IMExecArgsDialog dlg, VirtualFile file) {
		// 常に <tt>false</tt> を返す。
		return false;
	}
	
	public boolean doOpenFileByCsvOnEditor(IMExecArgsDialog dlg, VirtualFile file) {
		// 常に <tt>false</tt> を返す。
		return false;
	}

	public void onClosedDialog(IMExecArgsDialog dlg) {}

	public void onHiddenDialog(IMExecArgsDialog dlg) {}
	
	public boolean canCloseDialog(IMExecArgsDialog dlg) {
		// 常に <tt>true</tt> を返す。
		return true;
	}

	public void onShownDialog(IMExecArgsDialog dlg) {}
}
