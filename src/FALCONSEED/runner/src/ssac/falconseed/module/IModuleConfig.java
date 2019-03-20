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
 * @(#)IModuleConfig.java	3.1.0	2014/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IModuleConfig.java	1.22	2012/08/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleFileType;
import ssac.util.io.VirtualFile;

/**
 * モジュール情報とモジュール引数設定値のインタフェース。
 * 
 * @version 3.1.0	2014/05/12
 * @since 1.22
 */
public interface IModuleConfig<T extends IModuleArgConfig> extends Iterable<T>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * モジュール実行番号を取得する。
	 * 実行番号は、モジュール実行時に割り当てられる識別番号である。
	 * @return	実行番号
	 */
	public long getRunNo();

	/**
	 * モジュール実行番号を設定する。
	 * 実行番号は、モジュール実行時に割り当てる識別番号である。
	 * @param rno	新しい実行番号
	 */
	public void setRunNo(long rno);

	/**
	 * モジュール実行定義の名前を取得する。
	 * @return	モジュール実行定義名
	 */
	public String getName();

	/**
	 * モジュール実行定義の説明を返す。
	 * @return	説明
	 */
	public String getDescription();

	/**
	 * このモジュールのコメントを取得する。
	 * @return	<tt>null</tt> ではない文字列
	 * @since 3.1.0
	 */
	public String getComment();

	/**
	 * モジュール実行定義のディレクトリが存在しているかを判定する。
	 * @return	存在していれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isExistExecDefDirectory();

	/**
	 * モジュール実行定義の設定ファイルが存在しているかを判定する。
	 * @return	存在していれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isExistExecDefPrefsFile();

	/**
	 * 実行対象のモジュールファイルが存在しているかを判定する。
	 * @return	存在していれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isExistModuleFile();

	/**
	 * モジュール実行定義のディレクトリを取得する。
	 * @return	モジュール実行定義のルートディレクトリの位置を示す抽象パス
	 */
	public VirtualFile getExecDefDirectory();

	/**
	 * モジュール実行定義の設定ファイルを取得する。
	 * @return	モジュール実行定義の設定ファイルの位置を示す抽象パス
	 */
	public VirtualFile getExecDefPrefsFile();

	/**
	 * 実行対象のモジュールファイルを取得する。
	 * @return	モジュールファイルの位置を示す抽象パス
	 */
	public VirtualFile getModuleFile();

	/**
	 * 実行対象のモジュールファイルの、現在のサイズを取得する。
	 * @return	モジュールファイルのバイト数
	 */
	public long getModuleFileLength();

	/**
	 * 実行対象のモジュールファイルの、現在の最終更新日時を取得する。
	 * このメソッドでは、モジュールファイルが存在しない場合は 0 を返す。
	 * @return	モジュールファイルの最終更新日時
	 */
	public long getModuleFileLastModified();

	/**
	 * 実行対象のモジュールファイルが変更されているかを判定する。
	 * このメソッドでは、モジュールファイルが存在しない場合は <tt>false</tt> を返す。
	 * @return	変更されていれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isModifiedModuleFile();

	/**
	 * 実行対象のモジュールが実行された時点での、モジュールファイルのサイズを取得する。
	 * 実行前の場合は、現在のモジュールファイルのサイズを返す。
	 * @return	モジュール実行時点での、モジュールファイルのバイト数
	 */
	public long getModuleFileLengthAtRuntime();

	/**
	 * 実行対象のモジュールが実行された時点での、モジュールファイルのサイズを取得する。
	 * 実行前の場合は、現在のモジュールファイルの最終更新日時を返す。
	 * @return	モジュール実行時点での、モジュールファイルの最終更新日時
	 */
	public long getModuleFileLastModifiedAtRuntime();

	/**
	 * モジュールの種別を取得する。
	 * @return	モジュール種別を示す <code>ModuleFileType</code> オブジェクト
	 */
	public ModuleFileType getModuleType();

	/**
	 * モジュールのメインクラスを取得する。
	 * @return	モジュールのメインクラス名
	 */
	public String getModuleMainClass();

	/**
	 * モジュールに指定された Java VM オプションを取得する。
	 * @return	オプションが指定されている場合はその文字列、指定されていない場合は <tt>null</tt>
	 */
	public String getJavaVMArgs();

	/**
	 * モジュールの引数が存在しない場合に <tt>true</tt> を返す。
	 * @return	引数が存在しない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isEmptyArgument();

	/**
	 * モジュール定義の引数の数を取得する。
	 * @return	引数の数
	 */
	public int getArgumentCount();

	/**
	 * 引数定義情報を取得する。
	 * @param index	取得する引数のインデックス。
	 * @return	引数定義情報の新しいインスタンス
	 */
	public ModuleArgData getArgDefinition(int index);

	/**
	 * 実行時引数の値を取得する。
	 * @param index	取得する引数のインデックス
	 * @return	実行時引数の値を保持する <code>IRuntimeMExecArgValue</code> オブジェクト
	 * @throws ArrayIndexOutOfBoundsException	インデックスが範囲外の場合
	 */
	public IModuleArgConfig getArgument(int index);
}
