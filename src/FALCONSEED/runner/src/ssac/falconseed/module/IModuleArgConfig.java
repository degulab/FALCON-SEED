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
 * @(#)IModuleArgConfig.java	2.0.0	2012/10/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IModuleArgConfig.java	1.22	2012/08/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;


/**
 * モジュール実行時の引数設定値のインタフェース。
 * 
 * @version 2.0.0	2012/10/16
 * @since 1.22
 */
public interface IModuleArgConfig extends IModuleArgValue
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * この引数の番号を取得する。
	 * @return	1から始まる引数番号
	 */
	public int getArgNo();
	
	/**
	 * この引数の番号を設定する。
	 * @param argno	1から始まる引数番号
	 * @since 2.0.0
	 */
	public void setArgNo(int argno);

	/**
	 * 引数説明を設定する。
	 * @param desc	新しい説明の文字列
	 */
	public void setDescription(String desc);

	/**
	 * 引数の値を設定する。
	 * @param newValue	新しい引数の値
	 */
	public void setValue(Object newValue);

	/**
	 * テンポラリファイルへの出力設定を取得する。
	 * @return	テンポラリファイルへ出力する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean getOutToTempEnabled();

	/**
	 * テンポラリファイルへの出力を設定する。
	 * @param toEnable	テンポラリファイルへ出力する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public void setOutToTempEnabled(boolean toEnable);

	/**
	 * テンポラリファイルのプレフィックスが指定されているかを判定する。
	 * @return	プレフィックスが指定されている場合は <tt>true</tt>、指定されていない場合は <tt>false</tt>
	 */
	public boolean hasTempFilePrefix();

	/**
	 * 指定されたテンポラリファイル用プレフィックスを取得する。
	 * @return	プレフィックスが指定されている場合はその文字列、指定されていない場合は <tt>null</tt>
	 */
	public String getTempFilePrefix();

	/**
	 * テンポラリファイル用プレフィックスを設定する。
	 * @param prefix	テンポラリファイルのプレフィックス、指定しない場合は <tt>null</tt> もしくは長さが 0 の文字列
	 */
	public void setTempFilePrefix(String prefix);

	/**
	 * 実行完了後にファイルを表示するかどうかの設定を取得する。
	 * @return	表示する場合は <tt>true</tt>、表示しない場合は <tt>false</tt>
	 */
	public boolean getShowFileAfterRun();

	/**
	 * 実行完了後にファイルを表示するかどうかを設定する。
	 * @param toShow	表示する場合は <tt>true</tt>、表示しない場合は <tt>false</tt>
	 */
	public void setShowFileAfterRun(boolean toShow);
}
