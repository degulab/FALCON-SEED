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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ModuleArgValue.java	2.0.0	2012/10/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgValue.java	1.22	2012/08/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.util.Objects;

/**
 * モジュール実行時の引数値を保持するクラス。
 * 
 * @version 2.0.0	2012/10/28
 * @since 1.22
 */
public class ModuleArgValue implements IModuleArgValue, Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 引数の種別(IN/OUT/STR) **/
	protected ModuleArgType	_defType;
	/** パラメータ定義 **/
	protected IMExecArgParam	_param;
	
	/** 引数の説明 **/
	protected String				_defDesc;
	/** 実行時の引数値 **/
	protected Object				_value;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleArgValue(final ModuleArgData argdef) {
		this(argdef.getType(), argdef.getDescription(), argdef.getValue());
	}
	
	public ModuleArgValue(final ModuleArgType type, final String desc, final Object value) {
		this._defType  = type;
		this._defDesc  = desc;
		if (value instanceof IMExecArgParam) {
			this._param = (IMExecArgParam)value;
			this._value = null;
		} else {
			this._param = null;
			this._value = value;
		}
	}
	
	public ModuleArgValue(final ModuleArgType type, final String desc, final IMExecArgParam param, final Object value) {
		this._defType  = type;
		this._defDesc  = desc;
		this._param = param;
		this._value = value;
	}
	
	public ModuleArgValue(final IModuleArgValue value) {
		this._defType = value.getType();
		this._defDesc = value.getDescription();
		this._param   = value.getParameterType();
		this._value   = value.getValue();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 引数種別を返す。
	 */
	public ModuleArgType getType() {
		return _defType;
	}

	/**
	 * 引数説明を返す。
	 */
	public String getDescription() {
		return _defDesc;
	}

	/**
	 * 実行時の引数値が固定されているかを判定する。
	 * @return	実行時引数が固定されている場合は <tt>true</tt>、変更可能な場合は <tt>false</tt>
	 */
	public boolean isFixedValue() {
		return (_param==null);
	}

	/**
	 * 引数定義のパラメータ種別を返す。
	 * @return	実行時可変の場合は <code>IMExecArgParam</code> オブジェクト、実行時固定の場合は <tt>null</tt>
	 */
	public IMExecArgParam getParameterType() {
		return _param;
	}

	/**
	 * 引数値が設定されているかを判定する。
	 * @return	引数値が <tt>null</tt> ではない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean hasValue() {
		return (_value != null);
	}

	/**
	 * 引数の値を取得する。
	 * @return	引数の値
	 */
	public Object getValue() {
		return _value;
	}
	
	/**
	 * このオブジェクトの複製を生成する。
	 */
	public ModuleArgValue clone() {
		try {
			ModuleArgValue newData = (ModuleArgValue)super.clone();
			return newData;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public int hashCode() {
		int hv = 1;
		hv = hv * 31 + _defType.hashCode();
		hv = hv * 31 + (_defDesc==null ? 0 : _defDesc.hashCode());
		hv = hv * 31 + (_param==null ? 0 : _param.hashCode());
		hv = hv * 31 + (_value==null ? 0 : _value.hashCode());
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
		if (!(obj instanceof ModuleArgValue))
			return false;
		
		ModuleArgValue aData = (ModuleArgValue)obj;
		
		//--- basic parameters
		if (aData._defType != this._defType)
			return false;
		if (!Objects.isEqual(aData._defDesc, this._defDesc))
			return false;
		if (!Objects.isEqual(aData._value, this._value))
			return false;
		if (aData._param != this._param)
			return false;

		// equal all fields
		return true;
	}
	
	protected void appendParameters(StringBuilder buffer) {
		//--- type
		buffer.append("type=");
		buffer.append(_defType);
		//--- desc
		buffer.append(", desc=");
		if (_defDesc != null) {
			buffer.append("\"");
			buffer.append(_defDesc);
			buffer.append("\"");
		} else {
			buffer.append("null");
		}
		//--- param
		buffer.append(", param=");
		if (_param != null)
			buffer.append(_param.getClass().getName());
		else
			buffer.append("null");
		//--- value
		buffer.append(", value=");
		if (_value != null) {
			buffer.append("\"");
			buffer.append(_value);
			buffer.append("\"");
		} else {
			buffer.append("null");
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
