/*
 * @(#)GenericSchemaElementTreeModel	3.2.1	2015/07/01
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericSchemaElementTreeModel	3.2.0	2015/06/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.util;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * 汎用フィルタ編集における、入出力スキーマ定義用ツリーモデル。
 * 入力と出力のどちらでも利用する機能の実装となる。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class GenericSchemaElementTreeModel extends DefaultTreeModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 2772847657254138227L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public GenericSchemaElementTreeModel(GenericSchemaElementTreeNode root) {
		super(root, true);	// 子を持つかどうかはノードに依存
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement DefaultTreeModel interfaces
	//------------------------------------------------------------

	@Override
	public void setRoot(TreeNode root) {
		if (root != null && !(root instanceof GenericSchemaElementTreeNode)) {
			throw new IllegalArgumentException("Root node object is not " + GenericSchemaElementTreeNode.class.getSimpleName() + " class : " + root.getClass().getName());
		}
		super.setRoot(root);
	}

	@Override
	public Object getRoot() {
		return (GenericSchemaElementTreeNode)super.getRoot();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
