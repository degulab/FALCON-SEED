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
 * @(#)InputCsvTableSchemaEditModel.java	3.2.1	2015/07/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)InputCsvTableSchemaEditModel.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.table;

import ssac.aadl.fs.module.schema.table.SchemaInputCsvDataField;
import ssac.aadl.fs.module.schema.table.SchemaInputCsvDataTable;
import ssac.aadl.runtime.util.Objects;
import ssac.falconseed.filter.common.gui.util.FilterArgUtil;
import ssac.falconseed.filter.generic.gui.GenericSchemaElementValidation;
import ssac.falconseed.filter.generic.gui.GenericSchemaElementValidationImpl;
import ssac.falconseed.filter.generic.gui.arg.GenericFilterArgEditModel;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaTreeData;

/**
 * 汎用フィルタの入力 CSV テーブルスキーマ編集用データモデル。
 * このデータモデルは、編集時のテーブルモデルとしても利用される。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class InputCsvTableSchemaEditModel extends SchemaInputCsvDataTable implements GenericSchemaTreeData, GenericSchemaElementValidation
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 値のエラー情報を保持するオブジェクト **/
	private final GenericSchemaElementValidationImpl	_implValid = new GenericSchemaElementValidationImpl();

	/** 基準 CSV ファイル情報 **/
	private BaseCsvTableData	_baseCsvData;
	/** フィルタ定義引数の編集モデル **/
	private GenericFilterArgEditModel	_targetArgModel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 標準のパラメータで、要素が空の新しいインスタンスを生成する。
	 */
	public InputCsvTableSchemaEditModel() {
		super();
		_baseCsvData = null;
		_targetArgModel = null;
	}
	
	/**
	 * 標準のパラメータで、指定された容量を持つ、要素が空の新しいインスタンスを生成する。
	 * @param initialCapacity	初期容量(0 以上)
	 * @throws IllegalArgumentException	<em>initialCapacity</em> が負の場合
	 */
	public InputCsvTableSchemaEditModel(int initialCapacity) {
		super(initialCapacity);
		_baseCsvData = null;
		_targetArgModel = null;
	}
	
	/**
	 * 指定されたオブジェクトの内容をコピーした、新しいインスタンスを生成する。
	 * @param copyListElements	コレクション要素をコピーする場合は <tt>true</tt>、コピーしない場合は <tt>false</tt>
	 * @param srcModel	コピー元とするオブジェクト
	 * @throws NullPointerException	コピー元オブジェクトが <tt>null</tt> の場合
	 */
	public InputCsvTableSchemaEditModel(boolean copyListElements, SchemaInputCsvDataTable srcModel) {
		super(false, srcModel);	// ここでは要素はコピーしない
		if (copyListElements) {
			//--- deep copy
			for (SchemaInputCsvDataField field : srcModel) {
				super.add(new InputCsvFieldSchemaEditModel(field));
			}
		}
		if (srcModel instanceof InputCsvTableSchemaEditModel) {
			InputCsvTableSchemaEditModel srcEditModel = (InputCsvTableSchemaEditModel)srcModel;
			_baseCsvData = srcEditModel._baseCsvData;
			_targetArgModel = srcEditModel._targetArgModel;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasBaseCsvData() {
		return (_baseCsvData == null);
	}
	
	public BaseCsvTableData getBaseCsvData() {
		return _baseCsvData;
	}
	
	public void setBaseCsvData(BaseCsvTableData newCsvData) {
		_baseCsvData = newCsvData;
	}
	
	public GenericFilterArgEditModel getFilterArgModel() {
		return _targetArgModel;
	}
	
	public boolean setFilterArgModel(GenericFilterArgEditModel argmodel) {
		if (Objects.equals(argmodel, _targetArgModel))
			return false;
		//--- modified
		_targetArgModel = argmodel;
		return true;
	}

	//------------------------------------------------------------
	// Implement SchemaInputCsvDataTable interfaces
	//------------------------------------------------------------

	@Override
	public int hashCode() {
		return getInstanceId();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj == this);
	}
	
	@Override
	public String toNameString() {
		if (_targetArgModel != null) {
			String argName = filterArgToString();
			StringBuilder buffer = new StringBuilder();
			if (argName != null)
				buffer.append(argName);
			String name = getName();
			if (name != null) {
				buffer.append(name);
			}
			return buffer.toString();
		}
		else {
			return super.toNameString();
		}
	}

	//------------------------------------------------------------
	// Implement GenericSchemaTreeData interfaces
	//------------------------------------------------------------

	@Override
	public InputCsvTableSchemaEditModel getData() {
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
	
	protected String filterArgToString() {
		if (_targetArgModel == null) {
			return null;
		} else {
			return FilterArgUtil.formatArgType(_targetArgModel);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
