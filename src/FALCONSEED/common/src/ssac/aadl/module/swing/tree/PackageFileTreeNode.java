/*
 * @(#)PackageFileTreeNode.java	1.14	2009/12/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.tree;

import ssac.util.io.VirtualFile;

/**
 * パッケージ専用のツリーコンポーネントのノードとなるツリーノード
 * 
 * @version 1.14	2009/12/14
 * @since 1.14
 */
public class PackageFileTreeNode extends ModuleFileTreeNode
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
	
	public PackageFileTreeNode(VirtualFile file) {
		super(file);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public PackageFileTreeNode getRegisteredProject() {
		// パッケージ専用ではプロジェクトを無視する
		return null;
	}

	@Override
	protected void loadProjectProperties() {
		// パッケージ専用ではプロジェクト設定ファイルをロードしない
		this._propProject = null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
