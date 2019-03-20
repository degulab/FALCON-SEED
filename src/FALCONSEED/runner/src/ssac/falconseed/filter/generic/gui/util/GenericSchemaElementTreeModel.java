/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
