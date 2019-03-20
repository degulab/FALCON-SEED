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
 * @(#)IModuleArgValue.java	1.22	2012/08/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.args.IMExecArgParam;

/**
 * モジュール実行時の引数値のインタフェース。
 * 
 * @version 1.22	2012/08/10
 * @since 1.22
 */
public interface IModuleArgValue
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * 引数種別を返す。
	 */
	public ModuleArgType getType();

	/**
	 * 引数説明を返す。
	 */
	public String getDescription();

	/**
	 * 実行時の引数値が固定されているかを判定する。
	 * @return	実行時引数が固定されている場合は <tt>true</tt>、変更可能な場合は <tt>false</tt>
	 */
	public boolean isFixedValue();

	/**
	 * 引数定義のパラメータ種別を返す。
	 * @return	実行時可変の場合は <code>IMExecArgParam</code> オブジェクト、実行時固定の場合は <tt>null</tt>
	 */
	public IMExecArgParam getParameterType();

	/**
	 * 引数値が設定されているかを判定する。
	 * @return	引数値が <tt>null</tt> ではない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean hasValue();

	/**
	 * 引数の値を取得する。
	 * @return	引数の値
	 */
	public Object getValue();
}
