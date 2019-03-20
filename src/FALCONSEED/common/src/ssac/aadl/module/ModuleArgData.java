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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ModuleArgData.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module;

import ssac.aadl.module.ModuleArgType;
import ssac.util.Objects;

/**
 * モジュール引数の設定情報。
 * このクラスでは、引数種別({@link ModuleArgType})、引数説明(<code>String</code>)、引数の値(<code>Object</code>)を保持する。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public class ModuleArgData implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 引数の種別(IN/OUT/STR) **/
	private ModuleArgType	_type;
	/** 引数の説明 **/
	private String			_desc;
	/** 引数の値 **/
	private Object			_value;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleArgData() {
		this(ModuleArgType.NONE);
	}
	
	public ModuleArgData(ModuleArgType type) {
		this(type, null);
	}
	
	public ModuleArgData(ModuleArgType type, String desc) {
		this(type, desc, null);
	}
	
	public ModuleArgData(ModuleArgType type, String desc, Object value) {
		setType(type);
		setDescription(desc);
		setValue(value);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ModuleArgType getType() {
		return _type;
	}
	
	public void setType(ModuleArgType type) {
		this._type = type;
	}
	
	public String getDescription() {
		return _desc;
	}
	
	public void setDescription(String desc) {
		this._desc = desc;
	}
	
	public Object getValue() {
		return _value;
	}
	
	public void setValue(Object value) {
		this._value = value;
	}

	@Override
	protected ModuleArgData clone() throws CloneNotSupportedException {
		try {
			ModuleArgData newData = (ModuleArgData)super.clone();
			return newData;
		}
		catch (CloneNotSupportedException ex) {
		    // this shouldn't happen, since we are Cloneable
		    throw new InternalError();
		}
	}

	@Override
	public int hashCode() {
		int hv = 1;
		hv = hv * 31 + _type.hashCode();
		hv = hv * 31 + (_desc==null ? 0 : _desc.hashCode());
		hv = hv * 31 + (_value==null ? 0 : _value.hashCode());
		return hv;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof ModuleArgData) {
			ModuleArgData aData = (ModuleArgData)obj;
			if (aData._type==this._type && Objects.isEqual(aData._desc, this._desc) && Objects.isEqual(aData._value, this._value)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("@");
		sb.append(Integer.toHexString(System.identityHashCode(this)));
		sb.append("[type=");
		sb.append(_type);
		sb.append(", desc=");
		if (_desc!=null) {
			sb.append("\"");
			sb.append(_desc);
			sb.append("\"");
		} else {
			sb.append("null");
		}
		sb.append(", value=");
		if (_value!=null) {
			sb.append("\"");
			sb.append(_value);
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
