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
 * @(#)GenericTableSchemaRootNode.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericTableSchemaRootNode.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.table;

import ssac.falconseed.filter.generic.gui.util.GenericSchemaElementTreeNode;

/**
 * 汎用フィルタ編集における、入出力スキーマのルートノード。
 * このオブジェクトは、複数のテーブルスキーマ用ツリーノードを持つ。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericTableSchemaRootNode extends GenericSchemaElementTreeNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -7848220002494134723L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericTableSchemaRootNode() {
		super(null, true);	// 複数の子を持つ
	}

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
	 * @return	常に <tt>false</tt>
	 * @since 3.2.1
	 */
	@Override
	public boolean isTableNode() {
		return false;
	}

	//------------------------------------------------------------
	// Implements GenericSchemaElementValidation interfaces
	//------------------------------------------------------------

	/**
	 * 常に <tt>true</tt>
	 */
	@Override
	public boolean verify() {
		return true;
	}

	/**
	 * 常に <tt>false</tt>
	 */
	@Override
	public boolean hasError() {
		return false;
	}

	/**
	 * 常に <tt>null</tt>
	 */
	@Override
	public String getAvailableErrorMessage() {
		return null;
	}

	/**
	 * 常に <tt>null</tt>
	 */
	@Override
	public String getErrorMessage() {
		return null;
	}

	/**
	 * 常に <tt>null</tt>
	 */
	@Override
	public Throwable getErrorCause() {
		return null;
	}

	/**
	 * 常に <tt>false</tt>
	 */
	@Override
	public boolean clearError() {
		return false;
	}

	/**
	 * 常に <tt>false</tt>
	 */
	@Override
	public boolean setError(String errmsg) {
		return false;
	}

	/**
	 * 常に <tt>false</tt>
	 */
	@Override
	public boolean setError(Throwable cause) {
		return false;
	}

	/**
	 * 常に <tt>false</tt>
	 */
	@Override
	public boolean setError(String errmsg, Throwable cause) {
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
