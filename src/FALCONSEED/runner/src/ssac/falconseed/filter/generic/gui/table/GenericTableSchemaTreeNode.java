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
