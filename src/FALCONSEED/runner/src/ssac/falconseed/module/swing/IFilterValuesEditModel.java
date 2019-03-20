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
 * @(#)IFilterValuesEditModel.java	2.0.0	2012/11/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.setting.MExecDefHistory;
import ssac.util.io.VirtualFile;

/**
 * フィルタ実行時引数値を保持するデータモデル。
 * このデータモデルは、フィルタ実行時引数値を設定するためのユーザインタフェースに使用する。
 * 
 * @version 2.0.0	2012/11/08
 * @since 2.0.0
 */
public interface IFilterValuesEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * フィルタが編集中であれば <tt>true</tt> を返す。
	 */
	public boolean isEditing();

	/**
	 * 引数値の編集が可能であれば <tt>true</tt> を返す。
	 */
	public boolean isArgsEditable();

	/**
	 * 引数履歴情報が利用可能であれば <tt>true</tt> を返す。
	 */
	public boolean isArgsHistoryEnabled();

	/**
	 * モジュール実行定義の名前を取得する。
	 * @return	モジュール実行定義が存在していればその名前、それ以外の場合は <tt>null</tt>
	 */
	public String getName();

	/**
	 * モジュール実行定義の説明を取得する。
	 * @return	モジュール実行定義が存在していればその説明、それ以外の場合は <tt>null</tt>
	 */
	public String getDescription();

	/**
	 * 引数履歴情報を取得する。
	 * @return	引数履歴情報が利用可能であればそのオブジェクト、それ以外の場合は <tt>null</tt>
	 */
	public MExecDefHistory getArgsHistory();

	/**
	 * モジュール実行定義のルートディレクトリが配置されているディレクトリを取得する。
	 * @return	モジュール実行定義が存在していればその親の抽象パス、それ以外の場合は <tt>null</tt>
	 */
	public VirtualFile getExecDefParentDirectory();

	/**
	 * モジュール実行定義のルートディレクトリを取得する。
	 * @return	モジュール実行定義が存在していればその抽象パス、それ以外の場合は <tt>null</tt>
	 */
	public VirtualFile getExecDefDirectory();

	/**
	 * モジュール実行定義内のローカルファイルルートディレクトリを取得する。
	 * @return	モジュール実行定義が存在していれば対応する抽象パス、それ以外の場合は <tt>null</tt>
	 */
	public VirtualFile getExecDefLocalFileRootDirectory();

	/**
	 * パス整形用フォーマッターを取得する。
	 * @return	モジュール実行定義が存在していれば対応するオブジェクト、それ以外の場合は <tt>null</tt>
	 */
	public VirtualFilePathFormatterList getFormatter();

	/**
	 * モジュール実行設定情報を取得する。
	 * @return	モジュール実行設定情報が登録されていればそのオブジェクト、それ以外の場合は <tt>null</tt>
	 */
	public ModuleRuntimeData getModuleData();

	/**
	 * モジュール実行設定情報を設定する。
	 * @param newData	設定するモジュール実行設定情報
	 * @return	データが変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IllegalArgumentException	指定されたモジュール実行設定情報に対応するモジュール実行定義が存在しない場合
	 */
	public boolean setModuleData(final ModuleRuntimeData newData);

	/**
	 * データモデルが変更されるたびに通知されるリストにリスナーを追加する。
	 * @param l		追加するリスナーオブジェクト
	 */
	public void addDataModelListener(IFilterValuesEditModelListener l);

	/**
	 * データモデルが変更されるたびに通知されるリストからリスナーを削除する。
	 * @param l		削除するリスナーオブジェクト
	 */
	public void removeDataModelListener(IFilterValuesEditModelListener l);
}
