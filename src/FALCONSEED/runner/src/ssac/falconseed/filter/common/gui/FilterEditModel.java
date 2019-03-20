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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FilterEditModel.java	3.1.0	2015/06/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.common.gui;

import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileType;
import ssac.falconseed.module.FilterArgEditModelList;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.swing.FilterArgEditModel;
import ssac.falconseed.module.swing.IFilterArgEditHandler;
import ssac.falconseed.module.swing.table.IModuleArgConfigTableModel;
import ssac.util.io.VirtualFile;

/**
 * フィルタ専用編集ダイアログのデータモデルの共通インタフェース。
 * このモデルは、フィルタに共通のデータモデルに関するインタフェースとなる。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public interface FilterEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * モジュール実行定義の編集方法
	 **/
	public enum EditType {
		VIEW,		// 参照(読み込み専用)
		NEW,		// 新規作成
		MODIFY,		// 編集
	};

	/**
	 * このフィルタ定義のモジュールデータを返す。
	 */
	public ModuleRuntimeData getModuleData();

	/**
	 * フィルタ定義引数編集コンポーネント用データモデルを返す。
	 */
	public IModuleArgConfigTableModel getMExecDefArgTableModel();
	
	/**
	 * フィルタ定義引数の固定値を格納するディレクトリを返す。
	 * @return	引数値格納ディレクトリを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getArgsRootDirectory();

	/**
	 * ローカルファイル格納ディレクトリを返す。
	 * @return	ローカルファイル格納ディレクトリを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getLocalFileRootDirectory();

	/**
	 * モジュール実行定義が配置されるルートディレクトリを取得する。
	 * このメソッドが返すルートディレクトリは、ファイルシステムのルートディレクトリではなく、
	 * アプリケーションレベルで管理されるルートディレクトリとなる。
	 * @return	ルートディレクトリの抽象パス。
	 */
	public VirtualFile getRootDirectory();

	/**
	 * モジュール実行定義が配置されるルートディレクトリの表示名を取得する。
	 * @return	ルートディレクトリの表示名
	 */
	public String getRootDisplayName();

	/**
	 * この編集モデルの正規モジュール実行定義ディレクトリを返す。
	 * このメソッドが <tt>null</tt> ではない抽象パスを返し、
	 * {@link #getWorkingFilterRootDirectory()} が <tt>null</tt> を返すとき、
	 * このモデルのすべてのデータは、このメソッドが返すディレクトリを基準としている。
	 * @return	正規モジュール実行定義ディレクトリの抽象パス。
	 * 			正規モジュール実行定義が存在しない場合は <tt>null</tt>
	 */
	public VirtualFile getOriginalFilterRootDirectory();

	/**
	 * この編集モデルの作業用モジュール実行定義ディレクトリを返す。
	 * このメソッドが <tt>null</tt> ではない抽象パスを返すとき、
	 * このモデルのすべてのデータは、このメソッドが返すディレクトリを基準としている。
	 * @return	作業用モジュール実行定義ディレクトリの抽象パス。
	 * 			作業コピーが未定義の場合は <tt>null</tt>
	 */
	public VirtualFile getWorkingFilterRootDirectory();

	/**
	 * この編集モデルの現在のモジュール実行定義ディレクトリを返す。
	 * このメソッドが返す抽象パスは、{@link #getOriginalFilterRootDirectory()} または
	 * {@link #getWorkingFilterRootDirectory()} のどちらかが返す抽象パスを返す。
	 * @return	現在のモジュール実行定義ディレクトリの抽象パス。
	 * 			モジュール実行定義が存在しない場合は <tt>null</tt>
	 */
	public VirtualFile getAvailableFilterRootDirectory();

	/**
	 * このモデルに設定された、モジュール実行定義の親ディレクトリを返す。
	 * 通常はモジュール実行定義が配置されたディレクトリの抽象パスを返す。
	 * @return	現在のモジュール実行定義の親ディレクトリ
	 */
	public VirtualFile getFilterParentDirectory();

	/**
	 * このモデルに設定されたフィルタ名を返す。
	 * 通常はモジュール実行定義ディレクトリの名称を返す。
	 * @return	現在のモジュール実行定義の名称
	 */
	public String getFilterName();

	/**
	 * 現在のモジュール実行定義の説明を返す。
	 * @return	現在の機能説明
	 */
	public String getMExecDefDescription();

	/**
	 * モジュール実行定義を保持する設定ファイルを取得する。
	 * @return	設定ファイルの抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefPrefsFile();

	/**
	 * モジュール実行定義に格納される引数履歴ファイルを取得する。
	 * @return	引数履歴ファイルの抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefHistoryPrefsFile();

	/**
	 * このモデルのモジュールファイル抽象パスを返す。
	 * @return モジュールファイルを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefModuleFile();

	/**
	 * このモデルのモジュールファイルの設定ファイル抽象パスを返す。
	 * @return	モジュール設定ファイルを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefModulePrefsFile();

	/**
	 * このモデルのモジュール実行定義のモジュールファイル種別を返す。
	 * @return <code>ModuleFileType.AADL_MACRO</code>
	 */
	public ModuleFileType getMExecDefModuleType();

	/**
	 * このモデルのモジュール実行定義メインクラス名を返す。
	 * @return <code>AADLMacroEngine</code> クラスのクラス名
	 */
	public String getMExecDefMainClass();

	/**
	 * モジュール実行定義編集ダイアログの動作種別を取得する。
	 * @return	動作種別
	 */
	public EditType getEditType();

	/**
	 * このモジュール実行定義が編集中なら <tt>true</tt> を返す。
	 * @return	編集中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isEditing();

	/**
	 * このモジュール実行定義が新規作成中なら <tt>true</tt> を返す。
	 * @return	新規作成中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isNewEditing();
	
	public String formatFilePath(VirtualFile file);
	
	public String formatLocalFilePath(VirtualFile file);

	/**
	 * 指定されたファイルが、このフィルタ内のファイルかを判定する。
	 * @param target	判定対象のパス
	 * @return	このフィルタ内のファイルであれば <tt>true</tt>、
	 * それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	public boolean isIncludedMExecDef(final VirtualFile target);

	/**
	 * 指定されたファイルが固定引数値格納ディレクトリ以下のファイルかを判定する。
	 * @param target	判定対象のパス
	 * @return	固定引数値格納ディレクトリ以下のパスであれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	public boolean isArgsDataFile(VirtualFile target);
	
	/**
	 * 指定されたファイルがローカルデータディレクトリ以下のファイルかを判定する。
	 * @param target		判定対象のパス
	 * @return	ローカルデータであれば <tt>true</tt>，そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	public boolean isLocalDataFile(VirtualFile target);

	//------------------------------------------------------------
	// Public interfaces for MExecDef argument list
	//------------------------------------------------------------
	
	public IFilterArgEditHandler getMExecDefArgEditHandler();
	
	public void setMExecDefArgEditHandler(final IFilterArgEditHandler newHandler);

	/**
	 * フィルタ定義引数のリストを返す。
	 * このリストを直接更新した場合、他のデータモデルへは通知されない。
	 */
	public FilterArgEditModelList getMExecDefArgsList();

	/**
	 * フィルタ定義引数をすべて削除する。
	 * エラーチェックは行わない。
	 */
	public void clearMExecDefArguments();

	/**
	 * フィルタ定義引数が存在しない場合に <tt>true</tt> を返す。
	 */
	public boolean isMExecDefArgumentEmpty();
	
	/**
	 * フィルタ定義引数の総数を返す。
	 */
	public int getMExecDefArgumentCount();

	/**
	 * フィルタ定義引数のリストから、指定されたオブジェクトが格納されている位置を取得する。
	 * @param defArgData	判定する引数データ
	 * @return	引数データが存在する位置のインデックスを返す。存在しない場合は (-1)
	 */
	public int indexOfMExecDefArgument(final FilterArgEditModel defArgData);

	/**
	 * フィルタ定義引数リストから、指定されたインデックスに格納されている引数データを取得する。
	 * @param index		引数のインデックス
	 * @return	引数データ
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public FilterArgEditModel getMExecDefArgument(int index);

	/**
	 * フィルタ定義引数のリストから、指定されたインデックスに格納されているオブジェクトの引数番号を返す。
	 * @param index		引数のインデックス
	 * @return	引数番号
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public int getMExecDefArgumentNo(int index);
	
	public ModuleArgType getMExecDefArgumentType(int index);
	
	public String getMExecDefArgumentDescription(int index);
	
	public Object getMExecDefArgumentValue(int index);
	
	public boolean setMExecDefArgumentDescription(int index, String desc);
	
	public boolean setMExecDefArgumentValue(int index, Object value);

	/**
	 * フィルタ定義引数リストの終端に、指定された引数データを追加する。
	 * @param type		引数属性
	 * @param desc		引数説明
	 * @param value		引数値
	 */
	public void addMExecDefArgument(final ModuleArgType type, final String desc, final Object value);

	/**
	 * フィルタ定義引数リストの終端に、指定された引数データを追加する。
	 * @param newDefArg	追加する引数データ
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void addMExecDefArgument(final FilterArgEditModel newDefArg);

	/**
	 * フィルタ定義引数リストの指定された位置に、指定された引数データを挿入する。
	 * @param index		挿入位置のインデックス、もしくはフィルタ定義引数リストの要素数
	 * @param type		引数属性
	 * @param desc		引数説明
	 * @param value		引数値
	 */
	public void insertMExecDefArgument(int index, final ModuleArgType type, final String desc, final Object value);

	/**
	 * フィルタ定義引数リストの指定された位置に、指定された引数データを挿入する。
	 * @param index		挿入位置のインデックス、もしくはフィルタ定義引数リストの要素数
	 * @param newDefArg		挿入する引数データ
	 * @throws NullPointerException	引数データが <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	インデックスが 0 未満、もしくは挿入前のリスト要素数より大きい場合
	 */
	public void insertMExecDefArgument(int index, final FilterArgEditModel newDefArg);

	/**
	 * フィルタ定義引数リストから、指定された引数データを一つ削除する。
	 * @param defArg	削除する引数データ
	 * @return 引数データが削除できた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean removeMExecDefArgument(final FilterArgEditModel defArg);

	/**
	 * フィルタ定義引数リストから、指定された位置の引数データを削除する。
	 * @param index		削除する位置のインデックス
	 * @return	引数データが削除できた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean removeMExecDefArgument(int index);
}
