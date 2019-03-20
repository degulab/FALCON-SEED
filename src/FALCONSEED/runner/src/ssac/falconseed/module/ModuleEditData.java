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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ModuleEditData	3.1.0	2014/05/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import ssac.util.Objects;
import ssac.util.io.VirtualFile;

/**
 * モジュールデータに付加する、編集時情報を保持するクラス。
 * このデータは、モジュールデータに付加される。
 * 
 * @version 3.1.0	2014/05/19
 * @since 3.1.0
 */
public class ModuleEditData implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * このモジュールのオリジナルの位置を示す抽象パス。
	 * オリジナルの位置が保存されていない場合は <tt>null</tt>。
	 * このフィールドは、フィルタマクロのサブフィルタで使用される。
	 */
	private VirtualFile	_vfOrgMExecDefDir;
	/**
	 * 編集エラー情報。
	 * このモジュールに関する最も優先順位の高いエラーが設定される。
	 * エラー情報がない場合は <tt>null</tt>
	 */
	private FilterDataError	_errEdit;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleEditData() {
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このモジュールのオリジナルの位置を取得する。
	 * 主にフィルタマクロのサブフィルタのオリジナル位置を示す。
	 * @return	オリジナル位置が設定されていればその抽象パス、設定されていない場合は <tt>null</tt>
	 */
	public VirtualFile getOriginalMExecDefDir() {
		return _vfOrgMExecDefDir;
	}

	/**
	 * このモジュールのオリジナルの位置を設定する。
	 * 主にフィルタマクロのサブフィルタのオリジナル位置を示す。
	 * @param vfPath	設定する抽象パス、設定なしとする場合は <tt>null</tt>
	 */
	public void setOriginalMExecDefDir(VirtualFile vfPath) {
		_vfOrgMExecDefDir = vfPath;
	}

	/**
	 * 編集時エラーの有無を判定する。
	 * @return	エラーがある場合は <tt>true</tt>、ない場合は <tt>false</tt>
	 */
	public boolean hasError() {
		return (_errEdit != null);
	}

	/**
	 * 設定されている編集時エラー情報を取得する。
	 * @return	エラーがある場合はそのオブジェクト、ない場合は <tt>null</tt>
	 */
	public FilterDataError getError() {
		return _errEdit;
	}

	/**
	 * 編集時エラー情報をクリアする。
	 * @return	エラーの内容が変更された場合は <tt>true</tt>
	 */
	public boolean clearError() {
		return setError(null);
	}

	/**
	 * 編集時エラーを設定する。
	 * @param error	設定するエラー情報、エラーをクリアする場合は <tt>null</tt>
	 * @return	エラーの内容が変更された場合は <tt>true</tt>
	 */
	public boolean setError(FilterDataError error) {
		if (!Objects.isEqual(error, _errEdit)) {
			// 変更あり
			_errEdit = error;
			return true;
		}
		
		// 変更なし
		return false;
	}

	/**
	 * このオブジェクトのクローンを生成する。
	 * クローンのフィールドは、すべてシャローコピーとなる。
	 * @return	複製された新しいインスタンス
	 */
	@Override
	public ModuleEditData clone() {
		try {
			ModuleEditData newData = (ModuleEditData)super.clone();
			return newData;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError("Clone not supported : " + getClass().getName());
		}
	}

	@Override
	public int hashCode() {
		int hv = 0;
		hv = hv * 31 + (_vfOrgMExecDefDir==null ? 0 : _vfOrgMExecDefDir.hashCode());
		hv = hv * 31 + (_errEdit==null ? 0 : _errEdit.hashCode());
		return hv;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		return isEqualFields(obj);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("@[");
		appendParameters(sb);
		sb.append("]");
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean isEqualFields(Object obj) {
		if (!(obj instanceof ModuleEditData))
			return false;
		
		ModuleEditData aData = (ModuleEditData)obj;
		
		//--- parameters
		if (!Objects.isEqual(aData._vfOrgMExecDefDir, this._vfOrgMExecDefDir))
			return false;
		if (!Objects.isEqual(aData._errEdit, this._errEdit))
			return false;

		// equal all fields
		return true;
	}

	protected void appendParameters(StringBuilder buffer) {
		//--- vfOrgMExecDefDir
		buffer.append("orgMExecDefDir=");
		if (_vfOrgMExecDefDir == null) {
			buffer.append("null");
		} else {
			buffer.append("\"");
			buffer.append(_vfOrgMExecDefDir);
			buffer.append("\"");
		}
		
		//--- errCause
		buffer.append(", errEdit=");
		buffer.append(_errEdit);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
