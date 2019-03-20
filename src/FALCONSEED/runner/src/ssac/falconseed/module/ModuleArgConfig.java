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
 * @(#)ModuleArgConfig.java	2.0.0	2012/11/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgConfig.java	1.22	2012/08/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.util.Objects;

/**
 * モジュール実行時の引数設定値を保持するクラス。
 * このクラスでは、ユーザー定義データを保持することができる。
 * このオブジェクトのクローン時には、ユーザー定義データはシャローコピーとなる。
 * 
 * @version 2.0.0	2012/11/08
 * @since 1.22
 */
public class ModuleArgConfig extends ModuleArgValue implements IModuleArgConfig, Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 引数番号 **/
	private int	_argno;

	/** テンポラリファイル名に付加するプレフィックス **/
	private String		_tempPrefix = null;
	/** テンポラリ出力するフラグ **/
	private boolean	_outToTemp = false;
	/** 出力結果を表示するフラグ **/
	private boolean	_showFileAfterRun = false;

	/**
	 *  ユーザー定義データ
	 *  @since 2.00
	 */
	private Object		_userData;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleArgConfig(final int argno, final ModuleArgData argdef) {
		this(argno, argdef.getType(), argdef.getDescription(), argdef.getValue());
	}
	
	public ModuleArgConfig(final int argno, final ModuleArgType type, final String desc, final Object value) {
		super(type, desc, value);
		setArgNo(argno);
	}
	
	public ModuleArgConfig(final int argno, final ModuleArgType type, final String desc, final IMExecArgParam param, final Object value) {
		super(type, desc, param, value);
		setArgNo(argno);
	}
	
	public ModuleArgConfig(final int argno, final IModuleArgValue value) {
		super(value);
		setArgNo(argno);
		if (value instanceof IModuleArgConfig) {
			IModuleArgConfig sconf = (IModuleArgConfig)value;
			this._tempPrefix       = sconf.getTempFilePrefix();
			this._outToTemp        = sconf.getOutToTempEnabled();
			this._showFileAfterRun = sconf.getShowFileAfterRun();
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void clear() {
		this._value = null;
		this._tempPrefix = null;
		this._outToTemp = false;
		this._showFileAfterRun = false;
	}

	/**
	 * この引数の番号を取得する。
	 * @return	1から始まる引数番号
	 */
	public int getArgNo() {
		return _argno;
	}
	
	/**
	 * この引数の番号を設定する。
	 * @param argno	1から始まる引数番号
	 * @throws IllegalArgumentException	引数が 0 以下の値の場合
	 * @since 2.0.0
	 */
	public void setArgNo(int argno) {
		if (argno <= 0)
			throw new IllegalArgumentException("Argument number is less equal Zero : " + argno);
		this._argno = argno;
	}

	/**
	 * 引数属性を設定する。
	 * @param newType	新しい引数属性
	 */
	public void setType(ModuleArgType newType) {
		_defType = (newType==null ? ModuleArgType.NONE : newType);
	}

	/**
	 * 引数説明を設定する。
	 * @param desc	新しい説明の文字列
	 */
	public void setDescription(String desc) {
		_defDesc = (desc==null ? "" : desc);
	}

	/**
	 * 引数の値を設定する。
	 * @param newValue	新しい引数の値
	 * @throws IllegalStateException	固定値が引数に設定されている場合
	 */
	public void setValue(Object newValue) {
//		if (isFixedValue())
//			throw new IllegalStateException("The value of this argument can not be changed!");
		_value = newValue;
	}

	/**
	 * テンポラリファイルへの出力設定を取得する。
	 * @return	テンポラリファイルへ出力する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean getOutToTempEnabled() {
		return _outToTemp;
	}

	/**
	 * テンポラリファイルへの出力を設定する。
	 * @param toEnable	テンポラリファイルへ出力する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws IllegalStateException	固定値が引数に設定されている場合
	 */
	public void setOutToTempEnabled(boolean toEnable) {
//		if (isFixedValue())
//			throw new IllegalStateException("The value of this argument can not be changed!");
		_outToTemp = toEnable;
	}

	/**
	 * テンポラリファイルのプレフィックスが指定されているかを判定する。
	 * @return	プレフィックスが指定されている場合は <tt>true</tt>、指定されていない場合は <tt>false</tt>
	 */
	public boolean hasTempFilePrefix() {
		return (_tempPrefix != null);
	}

	/**
	 * 指定されたテンポラリファイル用プレフィックスを取得する。
	 * @return	プレフィックスが指定されている場合はその文字列、指定されていない場合は <tt>null</tt>
	 */
	public String getTempFilePrefix() {
		return _tempPrefix;
	}

	/**
	 * テンポラリファイル用プレフィックスを設定する。
	 * @param prefix	テンポラリファイルのプレフィックス、指定しない場合は <tt>null</tt> もしくは長さが 0 の文字列
	 */
	public void setTempFilePrefix(String prefix) {
		if (prefix != null && prefix.length() > 0) {
			_tempPrefix = prefix;
		} else {
			_tempPrefix = null;
		}
	}

	/**
	 * 実行完了後にファイルを表示するかどうかの設定を取得する。
	 * @return	表示する場合は <tt>true</tt>、表示しない場合は <tt>false</tt>
	 */
	public boolean getShowFileAfterRun() {
		return _showFileAfterRun;
	}

	/**
	 * 実行完了後にファイルを表示するかどうかを設定する。
	 * @param toShow	表示する場合は <tt>true</tt>、表示しない場合は <tt>false</tt>
	 */
	public void setShowFileAfterRun(boolean toShow) {
		_showFileAfterRun = toShow;
	}

	/**
	 * このインスタンスに設定されたユーザー定義データを取得する。
	 * @return	ユーザー定義データ、もしくは <tt>null</tt>
	 * @since 2.00
	 */
	public Object getUserData() {
		return _userData;
	}

	/**
	 * このインスタンスにユーザー定義データを設定する。
	 * @param data	ユーザー定義データ、もしくは <tt>null</tt>
	 * @since 2.00
	 */
	public void setUserData(Object data) {
		_userData = data;
	}
	
	/**
	 * このオブジェクトの複製を生成する。
	 */
	public ModuleArgConfig clone() {
		ModuleArgConfig newData = (ModuleArgConfig)super.clone();
		return newData;
	}

	@Override
	public int hashCode() {
		int hv = super.hashCode();
		hv = hv * 31 + _argno;
		hv = hv * 31 + (_tempPrefix==null ? 0 : _tempPrefix.hashCode());
		hv = hv * 31 + (_outToTemp ? 1231 : 1237);
		hv = hv * 31 + (_showFileAfterRun ? 1231 : 1237);
		hv = hv * 31 + getUserDataHashCode();
		return hv;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected int getUserDataHashCode() {
		return (_userData==null ? 0 : _userData.hashCode());
	}
	
	protected boolean isEqualUserData(Object aUserData) {
		return Objects.isEqual(_userData, aUserData);
	}

	@Override
	protected boolean isEqualFields(Object obj) {
		if (!(obj instanceof ModuleArgConfig))
			return false;
		
		if (!super.isEqualFields(obj))
			return false;
		
		ModuleArgConfig aData = (ModuleArgConfig)obj;
		
		//--- option parameters
		if (aData._argno != this._argno)
			return false;
		if (!Objects.isEqual(aData._tempPrefix, this._tempPrefix))
			return false;
		if (aData._outToTemp != this._outToTemp)
			return false;
		if (aData._showFileAfterRun != this._showFileAfterRun)
			return false;
		if (!isEqualUserData(aData._userData))
			return false;

		// equal all fields
		return true;
	}

	@Override
	protected void appendParameters(StringBuilder buffer) {
		//--- argno
		buffer.append("argno=");
		buffer.append(_argno);
		buffer.append(", ");
		
		//--- basic parameters
		super.appendParameters(buffer);
		
		//--- option parameters
		buffer.append(", outToTemp=");
		buffer.append(_outToTemp);
		buffer.append(", tmpPrefix=");
		if (_tempPrefix!=null && _tempPrefix.length() > 0) {
			buffer.append("\"");
			buffer.append(_tempPrefix);
			buffer.append("\"");
		} else {
			buffer.append("null");
		}
		
		buffer.append(", showFileAfterRun=");
		buffer.append(_showFileAfterRun);
		
		buffer.append(", userData=");
		buffer.append(_userData);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
