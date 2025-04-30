/*
 * @(#)EmptyMExecArgsDialogHandler.java	2.0.0	2012/10/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.falconseed.module.swing.tree.MEDLocalFileTreePanel;
import ssac.util.io.VirtualFile;

/**
 * 何も処理を行わない、フィルタダイアログ用ハンドラ。
 * @version 2.0.0	2012/10/09
 * @since 2.0.0
 */
public class EmptyFilterDialogHandler implements IFilterDialogHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final EmptyFilterDialogHandler _instance = new EmptyFilterDialogHandler();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public final EmptyFilterDialogHandler getInstance() {
		return _instance;
	}

	@Override
	public void onFileTreeSelectionChanged(MEDLocalFileTreePanel treePanel) {}

	public String getStatusBarMessage() {
		return "";
	}

	public void setStatusBarMessage(String message) {}
	
	public boolean doCloseFileOnEditor(IFilterDialog dlg, VirtualFile file) {
		// 常に <tt>false</tt> を返す。
		return false;
	}

	public boolean doOpenFileOnEditor(IFilterDialog dlg, VirtualFile file) {
		// 常に <tt>false</tt> を返す。
		return false;
	}
	
	public boolean doOpenFileByCsvOnEditor(IFilterDialog dlg, VirtualFile file) {
		// 常に <tt>false</tt> を返す。
		return false;
	}

	public void onClosedDialog(IFilterDialog dlg) {}

	public void onHiddenDialog(IFilterDialog dlg) {}
	
	public boolean canCloseDialog(IFilterDialog dlg) {
		// 常に <tt>true</tt> を返す。
		return true;
	}

	public void onShownDialog(IFilterDialog dlg) {}
}
