/*
 * @(#)ModuleArgDesc.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module;

import ssac.util.Objects;

/**
 * モジュールの引数設定情報。
 * このオブジェクトのフィールドは不変とする。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class ModuleArgDetail
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 引数型 **/
	private final ModuleArgType	_type;
	/** 引数説明 **/
	private final String			_desc;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleArgDetail(ModuleArgType type) {
		this(type, null);
	}
	
	public ModuleArgDetail(ModuleArgType type, String desc) {
		this._type = type;
		this._desc = desc;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ModuleArgType type() {
		return _type;
	}
	
	public String description() {
		return _desc;
	}

	@Override
	public int hashCode() {
		int ht = (_type==null ? 0 : _type.hashCode());
		int hd = (_desc==null ? 0 : _desc.hashCode());
		return (ht ^ hd);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof ModuleArgDetail) {
			ModuleArgDetail aDesc = (ModuleArgDetail)obj;
			if (aDesc._type==this._type && Objects.isEqual(aDesc._desc, this._desc)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		String typename;
		if (_type == null) {
			typename = "<>";
		} else {
			typename = "<" + _type.typeName() + ">";
		}
		if (_desc==null) {
			return typename;
		} else {
			return typename + "[" + _desc.toString() + "]";
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
