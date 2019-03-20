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
 * @(#)FilterArgumentDefEditModel.java	3.2.0	2015/06/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.common.gui;

import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.IModuleArgValue;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.util.Objects;

/**
 * 単一のフィルタ引数定義編集モデル。
 * <p>
 * このモデルのハッシュ値はインスタンス固有のハッシュ値となるため、フィールドの値が等しい場合でも等値とはならない場合がある。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class FilterArgumentDefEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 引数定義データ **/
	private ModuleArgConfig	_argument;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FilterArgumentDefEditModel(final int argno, final ModuleArgData argdef) {
		_argument = new ModuleArgConfig(argno, argdef);
	}
	
	public FilterArgumentDefEditModel(final int argno, final ModuleArgType type, final String desc, final Object value) {
		_argument = new ModuleArgConfig(argno, type, desc, value);
	}
	
	public FilterArgumentDefEditModel(final int argno, final ModuleArgType type, final String desc, final IMExecArgParam param, final Object value) {
		_argument = new ModuleArgConfig(argno, type, desc, param, value);
	}
	
	public FilterArgumentDefEditModel(final int argno, final IModuleArgValue value) {
		_argument = new ModuleArgConfig(argno, value);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 引数種別を返す。
	 */
	public ModuleArgType getType() {
		return _argument.getType();
	}

	/**
	 * 引数説明を返す。
	 */
	public String getDescription() {
		return _argument.getDescription();
	}

	/**
	 * 実行時の引数値が固定されているかを判定する。
	 * @return	実行時引数が固定されている場合は <tt>true</tt>、変更可能な場合は <tt>false</tt>
	 */
	public boolean isFixedValue() {
		return _argument.isFixedValue();
	}

	/**
	 * 引数定義のパラメータ種別を返す。
	 * @return	実行時可変の場合は <code>IMExecArgParam</code> オブジェクト、実行時固定の場合は <tt>null</tt>
	 */
	public IMExecArgParam getParameterType() {
		return _argument.getParameterType();
	}

	/**
	 * 引数値が設定されているかを判定する。
	 * @return	引数値が <tt>null</tt> ではない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean hasValue() {
		return _argument.hasValue();
	}

	/**
	 * 引数の値を取得する。
	 * @return	引数の値
	 */
	public Object getValue() {
		return _argument.getValue();
	}

	/**
	 * 引数値をクリアする。
	 * @return	引数値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean clear() {
		Object oldArgValue = _argument.getValue();
		_argument.clear();
		if (oldArgValue == null)
			return false;
		//--- modified
		return true;
	}

	/**
	 * この引数の番号を取得する。
	 * @return	1から始まる引数番号
	 */
	public int getArgNo() {
		return _argument.getArgNo();
	}
	
	/**
	 * この引数の番号を設定する。
	 * @param argno	1から始まる引数番号
	 * @return	引数番号が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IllegalArgumentException	引数が 0 以下の値の場合
	 */
	public boolean setArgNo(int argno) {
		if (argno <= 0)
			throw new IllegalArgumentException("Argument number is less equal Zero : " + argno);
		if (argno == _argument.getArgNo())
			return false;
		//--- modified
		_argument.setArgNo(argno);
		return true;
	}

	/**
	 * 引数属性を設定する。
	 * @param newType	新しい引数属性
	 * @return	引数属性が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setType(ModuleArgType newType) {
		if (newType == null)
			newType = ModuleArgType.NONE;
		if (newType == _argument.getType())
			return false;
		//--- modified
		_argument.setType(newType);
		return true;
	}

	/**
	 * 引数説明を設定する。
	 * @param desc	新しい説明の文字列
	 * @return	引数説明が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setDescription(String desc) {
		if (desc == null)
			desc = "";
		if (desc.equals(_argument.getDescription()))
			return false;
		//--- modified
		_argument.setDescription(desc);
		return true;
	}

	/**
	 * 引数の値を設定する。
	 * @param newValue	新しい引数の値
	 * @return	引数値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IllegalStateException	固定値が引数に設定されている場合
	 */
	public boolean setValue(Object newValue) {
		if (Objects.isEqual(newValue, _argument.getValue()))
			return false;
		//--- modified
		_argument.setValue(newValue);
		return true;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return (obj == this);
	}
	
	public boolean equalValues(Object obj) {
		if (obj instanceof FilterArgumentDefEditModel) {
			return _argument.equals(((FilterArgumentDefEditModel)obj)._argument);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("@");
		sb.append(Integer.toHexString(hashCode()));
		sb.append("[");
		//--- argno
		sb.append("argno=");
		sb.append(_argument.getArgNo());
		sb.append(", ");
		//--- type
		sb.append("type=");
		sb.append(_argument.getType());
		//--- desc
		sb.append(", desc=");
		String argdesc = _argument.getDescription();
		if (argdesc != null) {
			sb.append("\"");
			sb.append(argdesc);
			sb.append("\"");
		} else {
			sb.append("null");
		}
		//--- param
		sb.append(", param=");
		IMExecArgParam argparam = _argument.getParameterType();
		if (argparam != null)
			sb.append(argparam.getClass().getName());
		else
			sb.append("null");
		//--- value
		sb.append(", value=");
		Object argvalue = _argument.getValue();
		if (argvalue != null) {
			sb.append("\"");
			sb.append(argvalue);
			sb.append("\"");
		} else {
			sb.append("null");
		}
		sb.append("]");
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
