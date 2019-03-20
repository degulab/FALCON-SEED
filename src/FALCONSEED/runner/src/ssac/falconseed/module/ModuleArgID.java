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
 * @(#)ModuleArgID.java	2.0.0	2012/09/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleArgID.java	1.22	2012/08/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import ssac.aadl.module.ModuleArgType;

/**
 * モジュール実行定義引数を一意に識別するためのキー。
 * このキーは、引数が属するモジュール実行定義データと、
 * 1 から始まる引数番号によって構成される。
 * 
 * @version 2.0.0	2012/09/29
 * @since 1.22
 */
public class ModuleArgID
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 引数の属するモジュール実行定義データ **/
	private final ModuleRuntimeData	_data;
	/** 引数番号(1～) **/
	private final	int				_argno;
	
	private transient final int	_hashcode;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された引数で、このオブジェクトを生成する。
	 * @param data		モジュール実行定義データオブジェクト
	 * @param argno		1から始まる引数番号
	 * @throws NullPointerException	<em>data</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>argno</em> が 0 以下の場合
	 */
	public ModuleArgID(final ModuleRuntimeData data, int argno) {
		// check
		if (data == null)
			throw new NullPointerException("'data' argument is null.");
		if (argno <= 0)
			throw new IllegalArgumentException("'argno' less equal ZERO : " + argno);
		
		// set
		this._data  = data;
		this._argno = argno;

		// create Hash code
		int hv = _data.hashCode();
		hv = hv * 31 + _argno;
		this._hashcode = hv;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * モジュール引数キーの短い表示名を取得する。
	 * このメソッドでは、次のような書式で文字列を生成する。
	 * <blockquote>
	 * [<i>runNo</i>]($<i>argno</i>)
	 * </blockquote>
	 * <em>runNo</em> は実行番号であり、この番号が 0 のときは省略される。
	 * <em>argno</em> は 1 から始まる引数番号が出力される。
	 * @return 短い表示名
	 */
	static public String formatShortDisplayString(final ModuleArgID argid) {
		String str = null;
		if (argid != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			long rno = argid.getData().getRunNo();
			if (rno != 0L) {
				sb.append(rno);
			}
			sb.append("] ");
			IModuleArgConfig arg = argid.getData().getArgument(argid.argNo()-1);
			if (arg != null) {
				sb.append("($");
				sb.append(arg.getArgNo());
				sb.append(")");
			}
			str = sb.toString();
		}
		return str;
	}

	/**
	 * モジュール引数キーの表示名を取得する。
	 * このメソッドでは、次のような書式で文字列を生成する。
	 * <blockquote>
	 * [<i>runNo</i>:<i>name</i>]($<i>argno</i>)[<i>argtype</i>]
	 * </blockquote>
	 * <em>runNo</em> は実行番号であり、この番号が 0 のときは省略される。
	 * <em>name</em> はモジュール実行定義名、<em>argno</em> は 1 から始まる引数番号、
	 * <em>argtype</em> は引数種別が出力される。
	 * @return 表示名
	 */
	static public String formatDisplayString(final ModuleArgID argid) {
		String str = null;
		if (argid != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			long rno = argid.getData().getRunNo();
			if (rno != 0L) {
				sb.append(rno);
			}
			String name = argid.getData().getName();
			if (name != null && name.length() > 0) {
				if (rno != 0L)
					sb.append(":");
				sb.append(name);
			}
			sb.append("] ");
			IModuleArgConfig arg = argid.getData().getArgument(argid.argNo()-1);
			if (arg != null) {
				sb.append("($");
				sb.append(arg.getArgNo());
				sb.append(")");
				ModuleArgType atype = arg.getType();
				if (atype != null) {
					sb.append(atype);
				}
			}
			str = sb.toString();
		}
		return str;
	}
	
	public long runNo() {
		return _data.getRunNo();
	}
	
	public int argNo() {
		return _argno;
	}
	
	public ModuleRuntimeData getData() {
		return _data;
	}
	
	public IModuleArgConfig getArgument() {
		return _data.getArgument(_argno-1);
	}
	
	public Object getValue() {
		return _data.getArgument(_argno-1).getValue();
	}

	@Override
	public int hashCode() {
		return _hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof ModuleArgID) {
			ModuleArgID aItem = (ModuleArgID)obj;
			if (aItem._hashcode == this._hashcode) {
				if (aItem._data==this._data && aItem._argno==this._argno) {
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append("@[");
		sb.append("data=");
		sb.append(_data);
		sb.append(", argNo=");
		sb.append(_argno);
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
