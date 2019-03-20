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
 * @(#)OutputCsvFieldSchemaEditModel.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)OutputCsvFieldSchemaEditModel.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.table;

import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.table.SchemaOutputCsvDataField;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.falconseed.filter.generic.gui.GenericSchemaElementValidation;
import ssac.falconseed.filter.generic.gui.GenericSchemaElementValidationImpl;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaTreeData;
import ssac.falconseed.runner.RunnerMessages;

/**
 * 汎用フィルタの CSV 出力フィールドスキーマ編集用データモデル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class OutputCsvFieldSchemaEditModel extends SchemaOutputCsvDataField implements GenericSchemaTreeData, GenericSchemaElementValidation
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 値のエラー情報を保持するオブジェクト **/
	private final GenericSchemaElementValidationImpl	_implValid = new GenericSchemaElementValidationImpl();
	
	/** 編集対象のオリジナルフィールドデータモデル **/
	private OutputCsvFieldSchemaEditModel	_orgModel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public OutputCsvFieldSchemaEditModel() {
		super();
	}
	
	public OutputCsvFieldSchemaEditModel(SchemaValueType valueType) {
		super(valueType);
	}
	
	public OutputCsvFieldSchemaEditModel(SchemaValueType valueType, Object value) {
		super(valueType);
		setValue(value);
	}
	
	public OutputCsvFieldSchemaEditModel(final SchemaOutputCsvDataField src) {
		super(src);
		if (src instanceof OutputCsvFieldSchemaEditModel) {
			_orgModel = (OutputCsvFieldSchemaEditModel)src;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasOriginalModel() {
		return (_orgModel != null);
	}
	
	public OutputCsvFieldSchemaEditModel getOriginalModel() {
		return _orgModel;
	}
	
	public void setOriginalModel(OutputCsvFieldSchemaEditModel orgModel) {
		_orgModel = orgModel;
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
	public OutputCsvFieldSchemaEditModel getData() {
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
		
		// 出力対象のチェック
		SchemaElementValue targetValue = getTargetValue();
		if (targetValue == null) {
			// No target value
			setError(RunnerMessages.getInstance().msgGenericFilterEdit_OutputField_NoTargetValue);
			return false;
		}
		
		// 正当
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

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
