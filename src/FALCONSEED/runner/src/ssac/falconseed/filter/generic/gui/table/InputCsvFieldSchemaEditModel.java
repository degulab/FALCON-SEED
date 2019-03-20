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
 * @(#)InputCsvFieldSchemaEditModel.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)InputCsvFieldSchemaEditModel.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.table;

import ssac.aadl.fs.module.schema.table.SchemaInputCsvDataField;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.runtime.util.Objects;
import ssac.falconseed.filter.generic.gui.GenericSchemaElementValidation;
import ssac.falconseed.filter.generic.gui.GenericSchemaElementValidationImpl;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaTreeData;

/**
 * 汎用フィルタの CSV 入力フィールドスキーマ編集用データモデル。
 * <p>このオブジェクトのクローンでは、オリジナルスキーマはシャローコピーとなる。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class InputCsvFieldSchemaEditModel extends SchemaInputCsvDataField implements GenericSchemaTreeData, GenericSchemaElementValidation
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 値のエラー情報を保持するオブジェクト **/
	private final GenericSchemaElementValidationImpl	_implValid = new GenericSchemaElementValidationImpl();

	/** 編集対象のオリジナルスキーマ **/
	private InputCsvFieldSchemaEditModel	_orgFieldSchema;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public InputCsvFieldSchemaEditModel() {
		super();
		_orgFieldSchema = null;
	}
	
	public InputCsvFieldSchemaEditModel(SchemaValueType valueType) {
		super(valueType);
		_orgFieldSchema = null;
	}
	
	public InputCsvFieldSchemaEditModel(final SchemaInputCsvDataField src) {
		super(src);
		if (src instanceof InputCsvFieldSchemaEditModel) {
			_orgFieldSchema = (InputCsvFieldSchemaEditModel)src;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasOriginalSchema() {
		return (_orgFieldSchema != null);
	}
	
	public InputCsvFieldSchemaEditModel getOriginalSchema() {
		return _orgFieldSchema;
	}
	
	public void setOriginalSchema(InputCsvFieldSchemaEditModel original) {
		_orgFieldSchema = original;
	}

	//------------------------------------------------------------
	// Implement SchemaOutputCsvDataField interfaces
	//------------------------------------------------------------

	/**
	 * ハッシュ値として、インスタンス ID を返す。
	 * @see #getInstanceId() 
	 */
	@Override
	public int hashCode() {
		return getInstanceId();
	}

	/**
	 * オブジェクトのインスタンスが同一かどうかを比較し、同じインスタンスの場合のみ <tt>true</tt> を返す。
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj == this);
	}

	//------------------------------------------------------------
	// Implement GenericSchemaTreeData interfaces
	//------------------------------------------------------------

	@Override
	public InputCsvFieldSchemaEditModel getData() {
		return this;
	}

	@Override
	public String toTreeString() {
		return toLocalVariableNameString();
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
		// clear error
		clearError();
		
		// エラーチェックはなし
		return true;
	}

	/**
	 * エラーの有無を返す。
	 * @return	エラーがある場合は <tt>true</tt>、エラーのない場合は <tt>false</tt>
	 */
	@Override
	public boolean hasError() {
		return _implValid.hasError();
	}

	/**
	 * 有効なエラーメッセージを返す。
	 * @return	有効なエラーメッセージ、エラーが存在しない場合は <tt>null</tt>
	 */
	@Override
	public String getAvailableErrorMessage() {
		return _implValid.getAvailableErrorMessage();
	}

	/**
	 * 設定されているエラーメッセージを返す。
	 * @return	設定されているエラーメッセージ、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public String getErrorMessage() {
		return _implValid.getErrorMessage();
	}

	/**
	 * 設定されているエラー要因を返す。
	 * @return	エラー要因となる例外オブジェクト、設定されていない場合は <tt>null</tt>
	 */
	@Override
	public Throwable getErrorCause() {
		return _implValid.getErrorCause();
	}

	/**
	 * エラー情報をクリアする。
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean clearError() {
		return _implValid.clearError();
	}

	/**
	 * エラーメッセージを設定する。
	 * @param errmsg	設定するエラーメッセージ、もしくは <tt>null</tt>
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean setError(String errmsg) {
		return _implValid.setError(errmsg);
	}

	/**
	 * エラー要因を設定する。
	 * @param cause	設定するエラー要因、もしくは <tt>null</tt>
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean setError(Throwable cause) {
		return _implValid.setError(cause);
	}

	/**
	 * エラーメッセージとエラー要因を設定する。
	 * @param errmsg	設定するエラーメッセージ、もしくは <tt>null</tt>
	 * @param cause		設定するエラー要因、もしくは <tt>null</tt>
	 * @return	状態が変更された場合は <tt>true</tt>
	 */
	@Override
	public boolean setError(String errmsg, Throwable cause) {
		return _implValid.setError(errmsg, cause);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected boolean equalFields(Object obj) {
		if (!super.equalFields(obj))
			return false;
		
		InputCsvFieldSchemaEditModel aModel = (InputCsvFieldSchemaEditModel)obj;
		
		if (!Objects.equals(aModel._orgFieldSchema, this._orgFieldSchema))
			return false;
		
		// equal all fields
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
