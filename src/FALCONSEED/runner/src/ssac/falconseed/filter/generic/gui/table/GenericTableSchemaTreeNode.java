/*
 * @(#)GenericTableSchemaTreeNode.java	3.2.1	2015/07/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericTableSchemaTreeNode.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.table;

import ssac.falconseed.filter.generic.gui.util.GenericSchemaElementTreeNode;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaTreeData;

/**
 * 汎用フィルタの入力フォーマット設定用ツリーパネル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericTableSchemaTreeNode extends GenericSchemaElementTreeNode
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

	public GenericTableSchemaTreeNode() {
		super(null, true);
	}
	
	public GenericTableSchemaTreeNode(GenericSchemaTreeData userData) {
		super(userData, true);
	}

//	public GenericTableSchemaTreeNode(SchemaObjectDataContainer<?> userObject) {
//		super(userObject, true);
//	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このノードがフィールドデータのノードであれば、<tt>true</tt> を返す。
	 * @return	常に <tt>false</tt>
	 * @since 3.2.1
	 */
	@Override
	public boolean isFieldNode() {
		return false;
	}
	
	/**
	 * このノードがテーブルデータのノードであれば、<tt>true</tt> を返す。
	 * @return	常に <tt>true</tt>
	 * @since 3.2.1
	 */
	@Override
	public boolean isTableNode() {
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
