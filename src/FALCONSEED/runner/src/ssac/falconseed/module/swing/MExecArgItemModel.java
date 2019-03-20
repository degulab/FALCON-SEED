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
 * @(#)MExecArgItemModel.java	1.20	2012/03/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgItemModel.java	1.13	2011/11/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgItemModel.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.args.IMExecArgParam;

/**
 * モジュール実行時引数の１引数設定。
 * このモデルに代入可能な値は、{@link java.io.File} と {@link java.lang.String} に限定する。
 * {@link ssac.util.io.VirtualFile} が指定された場合は、{@link java.io.File} に変換するが、
 * 変換できないパスの場合は、<tt>null</tt> とする。
 * 現時点では、{@link java.io.File} ではなく {@link ssac.util.io.VirtualFile} とする。
 * 
 * @version 1.20	2012/03/24
 */
public class MExecArgItemModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 引数属性 **/
	private final ModuleArgType	_type;
	/** 引数説明 **/
	private final String			_desc;
	/** 引数のパラメータ設定 **/
	private final IMExecArgParam	_param;

	/** 引数固有のオプションフラグ **/
	private boolean	_option;
	/** 引数の値 **/
	private Object		_value;

	/** テンポラリファイルへの出力フラグ **/
	private boolean	_outToTemp;
	/** テンポラリファイル出力フラグがセットされる直前のオプションフラグ **/
	private boolean	_saveOptionBeforeOutToTemp;
	/** テンポラリファイルのプレフィックス **/
	private String		_tempPrefix;

	/** 引数の編集許可フラグ **/
	private boolean	_editable = true;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecArgItemModel(ModuleArgType type, String desc, IMExecArgParam param, boolean option, Object value)
	{
		if (type == null)
			throw new NullPointerException("The spcefieid argument attribute type is null.");
		this._type = type;
		this._desc = desc;
		this._param = param;
		this._option = option;
		this._value  = value;
		this._outToTemp = false;
		this._saveOptionBeforeOutToTemp = false;
		this._tempPrefix = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEditable() {
		return _editable;
	}
	
	public void setEditable(boolean editable) {
		this._editable = editable;
	}
	
	public ModuleArgType getType() {
		return _type;
	}
	
	public String getDescription() {
		return _desc;
	}
	
	public IMExecArgParam getParameter() {
		return _param;
	}
	
	public boolean isOptionEnabled() {
		return _option;
	}
	
	public void setOptionEnabled(boolean toEnable) {
		_option = toEnable;
	}
	
	public Object getValue() {
		return _value;
	}
	
	public void setValue(Object value) {
		_value = value;
	}
	
	public boolean isOutToTempEnabled() {
		return _outToTemp;
	}
	
	public void setOutToTempEnabled(boolean toEnable) {
		if (_outToTemp != toEnable) {
			if (toEnable) {
				_saveOptionBeforeOutToTemp = _option;
				_outToTemp = true;
			} else {
				_option = _saveOptionBeforeOutToTemp;
				_outToTemp = false;
			}
		}
	}
	
	public String getTempFilePrefix() {
		return _tempPrefix;
	}
	
	public void setTempFilePrefix(String prefix) {
		if (prefix == null || prefix.length() < 1) {
			_tempPrefix = null;
		} else {
			_tempPrefix = prefix;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
