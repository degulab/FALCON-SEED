/*
 * @(#)DtContainerContentTreeModel.java	1.0.0	2022/12/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.tree;

import javax.swing.tree.DefaultTreeModel;

/**
 * データコンテナ専用のコンテントツリーモデル。
 * 
 * @version	1.0.0
 */
public class DtContainerContentTreeModel extends DefaultTreeModel
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
	
	public DtContainerContentTreeModel(IDtContainerContentTreeNode rootNode) {
		super(rootNode);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
