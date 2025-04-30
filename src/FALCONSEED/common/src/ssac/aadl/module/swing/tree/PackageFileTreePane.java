/*
 * @(#)PackageFileTreePane.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.tree;

import ssac.aadl.module.ModuleFileManager;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;

/**
 * パッケージの選択を行うツリーペイン
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class PackageFileTreePane extends ModuleFileTreePane
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

	public PackageFileTreePane() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected ModuleFileTreeModel createDefaultTreeModel(VirtualFile rootFile, VirtualFileFilter vfFilter) {
		PackageFileTreeModel newModel = new PackageFileTreeModel(rootFile);
		if (rootFile != null) {
			newModel.setRootNodeIcon(ModuleFileManager.getSystemDisplayIcon(rootFile));
		}
		if (vfFilter != null) {
			newModel.setFileFilter(vfFilter);
		}
		return newModel;
	}
}
