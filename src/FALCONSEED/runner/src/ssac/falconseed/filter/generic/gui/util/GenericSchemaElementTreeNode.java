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
 * @(#)SchemaObjectDataContainer	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SchemaObjectDataContainer	3.2.0	2015/06/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.util;

import javax.swing.tree.DefaultMutableTreeNode;

import ssac.falconseed.filter.generic.gui.GenericSchemaElementValidation;
import ssac.falconseed.runner.RunnerMessages;

/**
 * 汎用フィルタ編集における、ツリーコンポーネント用のツリーノードとなるデータコンテナ。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericSchemaElementTreeNode extends DefaultMutableTreeNode implements GenericSchemaElementValidation
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -7955955683748252954L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public GenericSchemaElementTreeNode() {
		super(null, false);
	}

	public GenericSchemaElementTreeNode(GenericSchemaTreeData userObject) {
		super(userObject, false);
	}

	public GenericSchemaElementTreeNode(GenericSchemaTreeData userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このノードがフィールドデータのノードであれば、<tt>true</tt> を返す。
	 * @return	常に <tt>true</tt>
	 * @since 3.2.1
	 */
	public boolean isFieldNode() {
		return true;
	}
	
	/**
	 * このノードがテーブルデータのノードであれば、<tt>true</tt> を返す。
	 * @return	常に <tt>false</tt>
	 * @since 3.2.1
	 */
	public boolean isTableNode() {
		return false;
	}

	//------------------------------------------------------------
	// Implements DefaultMutableTreeNode interfaces
	//------------------------------------------------------------

	@Override
	public GenericSchemaTreeData getUserObject() {
		return (GenericSchemaTreeData)super.getUserObject();
	}

	@Override
	public void setUserObject(Object userObject) {
		if (userObject != null && !(userObject instanceof GenericSchemaTreeData)) {
			// invalid class instance
			throw new IllegalArgumentException("userObject is not GenericSchemaTreeData class");
		}
		
		super.setUserObject(userObject);
	}

	@Override
	public String toString() {
		GenericSchemaTreeData container = getUserObject();
		if (container == null)
			return null;
		else
			return container.toTreeString();
	}

	//------------------------------------------------------------
	// Implements GenericSchemaElementValidation interfaces
	//------------------------------------------------------------

	/**
	 * スキーマ定義要素の正当性を判定する。
	 * エラーがあれば、エラー情報を設定する。
	 * @return	正当なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean verify() {
		// ユーザーデータをチェック
		Object userobj = getUserObject();
		if (userobj instanceof GenericSchemaElementValidation) {
			GenericSchemaElementValidation validModel = (GenericSchemaElementValidation)userobj;
			if (isTableNode()) {
				// 列数のみをチェック
				validModel.clearError();
				if (getChildCount() == 0) {
					validModel.setError(RunnerMessages.getInstance().msgGenericFilterEdit_Table_NoField);
					return false;
				}
				//--- 正当
				return true;
			}
			else {
				// データモデルにチェックを委譲
				return validModel.verify();
			}
		}
		else {
			return true;
		}
	}

	/**
	 * エラーの有無を返す。
	 * @return	エラーがある場合は <tt>true</tt>、エラーのない場合は <tt>false</tt>
	 */
	@Override
	public boolean hasError() {
		// ユーザーデータをチェック
		Object userobj = getUserObject();
		if (userobj instanceof GenericSchemaElementValidation) {
			GenericSchemaElementValidation validModel = (GenericSchemaElementValidation)userobj;
			return validModel.hasError();
		}
		else {
			return false;
		}
	}

	/**
	 * 有効なエラーメッセージを返す。
	 * @return	有効なエラーメッセージ、エラーが存在しない場合は <tt>null</tt>
	 */
	@Override
	public String getAvailableErrorMessage() {
		// ユーザーデータをチェック
		Object userobj = getUserObject();
		if (userobj instanceof GenericSchemaElementValidation) {
			GenericSchemaElementValidation validModel = (GenericSchemaElementValidation)userobj;
			return validModel.getAvailableErrorMessage();
		}
		else {
			return null;
		}
	}

	/**
	 * 設定されているエラーメッセージを返す。
	 * @return	設定されているエラーメッセージ、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public String getErrorMessage() {
		// ユーザーデータをチェック
		Object userobj = getUserObject();
		if (userobj instanceof GenericSchemaElementValidation) {
			GenericSchemaElementValidation validModel = (GenericSchemaElementValidation)userobj;
			return validModel.getErrorMessage();
		}
		else {
			return null;
		}
	}

	/**
	 * 設定されているエラー要因を返す。
	 * @return	エラー要因となる例外オブジェクト、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public Throwable getErrorCause() {
		// ユーザーデータをチェック
		Object userobj = getUserObject();
		if (userobj instanceof GenericSchemaElementValidation) {
			GenericSchemaElementValidation validModel = (GenericSchemaElementValidation)userobj;
			return validModel.getErrorCause();
		}
		else {
			return null;
		}
	}

	/**
	 * エラー情報をクリアする。
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean clearError() {
		// ユーザーデータをチェック
		Object userobj = getUserObject();
		if (userobj instanceof GenericSchemaElementValidation) {
			GenericSchemaElementValidation validModel = (GenericSchemaElementValidation)userobj;
			return validModel.clearError();
		}
		else {
			return false;
		}
	}

	/**
	 * エラーメッセージを設定する。
	 * @param errmsg	設定するエラーメッセージ、もしくは <tt>null</tt>
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean setError(String errmsg) {
		// ユーザーデータをチェック
		Object userobj = getUserObject();
		if (userobj instanceof GenericSchemaElementValidation) {
			GenericSchemaElementValidation validModel = (GenericSchemaElementValidation)userobj;
			return validModel.setError(errmsg);
		}
		else {
			return false;
		}
	}

	/**
	 * エラー要因を設定する。
	 * @param cause	設定するエラー要因、もしくは <tt>null</tt>
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean setError(Throwable cause) {
		// ユーザーデータをチェック
		Object userobj = getUserObject();
		if (userobj instanceof GenericSchemaElementValidation) {
			GenericSchemaElementValidation validModel = (GenericSchemaElementValidation)userobj;
			return validModel.setError(cause);
		}
		else {
			return false;
		}
	}

	/**
	 * エラーメッセージとエラー要因を設定する。
	 * @param errmsg	設定するエラーメッセージ、もしくは <tt>null</tt>
	 * @param cause		設定するエラー要因、もしくは <tt>null</tt>
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean setError(String errmsg, Throwable cause) {
		// ユーザーデータをチェック
		Object userobj = getUserObject();
		if (userobj instanceof GenericSchemaElementValidation) {
			GenericSchemaElementValidation validModel = (GenericSchemaElementValidation)userobj;
			return validModel.setError(errmsg, cause);
		}
		else {
			return false;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
