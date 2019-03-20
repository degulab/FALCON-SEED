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
 * @(#)AbFilterEditModel.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.common.gui;

import javax.swing.DefaultListModel;

import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.ModuleFileType;
import ssac.falconseed.file.DefaultVirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.filter.common.gui.FilterEditModel.EditType;
import ssac.falconseed.module.FilterArgEditModelList;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.setting.MExecDefMacroFilter.IllegalMacroFilterPrefsException;
import ssac.falconseed.module.setting.MExecDefSettings;
import ssac.falconseed.module.swing.FilterArgEditModel;
import ssac.falconseed.module.swing.IFilterArgEditHandler;
import ssac.falconseed.module.swing.table.IModuleArgConfigTableModel;
import ssac.util.Objects;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;

/**
 * フィルタ専用編集ダイアログでの編集用データモデルを示すインタフェースの共通実装。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public abstract class AbFilterEditModel implements FilterEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//
	// data
	//
	
	/** このフィルタのモジュールデータ(サブフィルタは含まない) **/
	private final ModuleRuntimeData		_defModuleData;
	
	/** モジュール実行定義の編集用引数リスト **/
	private final FilterArgEditModelList	_defArgsList;
	
	//
	// swing model
	//

	/** フィルタ定義引数編集コンポーネント用データモデル **/
	private IModuleArgConfigTableModel	_defArgTableModel;

	/** フィルタ定義引数が変更されたときに呼び出されるハンドラ **/
	private IFilterArgEditHandler			_hDefArgEdit;
	
	//
	// local state
	//

	/** モジュール実行定義が配置可能なルートディレクトリの表示名 **/
	private final String		_strRootDisplayName;
	/** モジュール実行定義が配置可能なルートディレクトリ **/
	private final VirtualFile	_vfRootDir;
	/** デフォルトパスフォーマッター **/
	private final  VirtualFilePathFormatter	_vformDefault;

	/** モジュール実行定義の編集方法の指定 **/
	private FilterEditModel.EditType		_editType;

	/** 表示用パス文字列フォーマッター **/
	private VirtualFilePathFormatterList	_vfFormatter;
	
	/** モジュール実行定義のオリジナルルートディレクトリ **/
	private final VirtualFile	_vfOrgFilterRoot;
	/** モジュール実行定義の作業用ルートディレクトリ **/
	private final VirtualFile	_vfWorkFilterRoot;
	/** モジュール実行定義設定ファイルのパス **/
	private VirtualFile			_vfFilterPrefs;
	/** フィルタ定義引数の固定値となるファイルが格納されるディレクトリ **/
	private VirtualFile			_vfArgsRootDir;
	/** ローカルファイルが格納されるディレクトリ **/
	private VirtualFile			_vfFilesRootDir;

	/** モジュール実行定義が配置される親ディレクトリ **/
	private VirtualFile			_vfParent;
	/** 正規のフィルタ名 **/
	private String				_filterName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbFilterEditModel() {
		this._defModuleData = new ModuleRuntimeData();
		this._vfFormatter = new VirtualFilePathFormatterList(3);
		this._defArgsList = new FilterArgEditModelList();
		_strRootDisplayName = null;
		_vfRootDir = null;
		_vformDefault = null;
		_vfOrgFilterRoot = null;
		_vfWorkFilterRoot = null;
	}
	
	/**
	 * 指定されたパラメータで、このオブジェクトの新しいインスタンスを生成する。
	 * このメソッド呼び出し後、{@link #loadPreferences()} を呼び出し、モデル情報初期化を完了させること。
	 * @param type				編集種別
	 * @param defaultFormatter	標準のパスフォーマッター
	 * @param rootDisplayName	ルートディレクトリの表示名
	 * @param rootDir			ルートディレクトリ
	 * @param orgFilterDir		編集元のフィルタマクロのパス、編集ではない場合は <tt>null</tt>
	 * @param workFilterDir		編集作業に使用するディレクトリのパス
	 * @throws IllegalMacroFilterPrefsException	フィルタマクロ定義データを読み込んだ場合において、解読不能の不正なデータが含まれている場合（詳細メッセージ含む）
	 */
	public AbFilterEditModel(FilterEditModel.EditType type, VirtualFilePathFormatter defaultFormatter, String rootDisplayName,
								VirtualFile rootDir, VirtualFile orgFilterDir, VirtualFile workFilterDir)
	{
		if (rootDir == null)
			throw new NullPointerException("The specified root directory is null.");
		if (!rootDir.exists())
			throw new IllegalArgumentException("The specified root directory does not exists.");
		if (!rootDir.isDirectory())
			throw new IllegalArgumentException("The specified root directory is not directory.");
		if (orgFilterDir==null && workFilterDir==null)
			throw new NullPointerException();
		if (rootDisplayName != null && rootDisplayName.length() > 0) {
			this._strRootDisplayName = Files.CommonSeparator + rootDisplayName;
		} else {
			this._strRootDisplayName = Files.CommonSeparator + rootDir.getName();
		}
		this._vfRootDir = rootDir;
		this._vformDefault = defaultFormatter;
		this._editType = type;
		this._defModuleData = new ModuleRuntimeData();
		this._vfFormatter = new VirtualFilePathFormatterList(3);
		this._defArgsList = new FilterArgEditModelList();
//		this._modelDefArgTableModel = new ImplMacroFilterDefArgTableModel();
		
		this._vfOrgFilterRoot = orgFilterDir;
		this._vfWorkFilterRoot = workFilterDir;
		if (orgFilterDir != null) {
			this._vfParent = orgFilterDir.getParentFile();
			this._filterName = orgFilterDir.getName();
		}
		else {
			this._vfParent = rootDir;
			this._filterName = null;
		}
		
		// initialize target files
		VirtualFile vfTarget = (workFilterDir==null ? orgFilterDir : workFilterDir);
		//--- この場合の vfTarget は、対象モジュール実行定義のルートディレクトリ
		if (!vfTarget.isDirectory()) {
			// 対象がディレクトリではない
			throw new IllegalArgumentException("The specified target filter root is not directory : \"" + vfTarget.toString() + "\"");
		}
		this._vfFilterPrefs = MExecDefFileManager.getModuleExecDefDataFile(vfTarget);
		this._vfArgsRootDir  = vfTarget.getChildFile(MExecDefFileManager.MEXECDEF_MODULEARG_DIRNAME);
		this._vfFilesRootDir = vfTarget.getChildFile(MExecDefFileManager.MEXECDEF_FILES_ROOT_DIRNAME);
		
		// パスの更新
		updatePathFormatter();
	}

	/**
	 * 現在の設定で、このモデルの定義情報をファイルから読み込む。
	 */
	public void loadPreferences()
	{
		// load filter prefs
		MExecDefSettings defFilter = new MExecDefSettings();
		if (_vfFilterPrefs.exists()) {
			//--- モジュール定義ファイルが存在する場合は、読み込み
			defFilter.loadForTarget(_vfFilterPrefs);
		}
		this._defModuleData.setData(defFilter);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このフィルタ定義のモジュールデータを返す。
	 */
	public ModuleRuntimeData getModuleData() {
		return _defModuleData;
	}

	/**
	 * フィルタ定義引数編集コンポーネント用データモデルを返す。
	 */
	public IModuleArgConfigTableModel getMExecDefArgTableModel() {
		return _defArgTableModel;
	}
	
	/**
	 * フィルタ定義引数の固定値を格納するディレクトリを返す。
	 * @return	引数値格納ディレクトリを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getArgsRootDirectory() {
		return _vfArgsRootDir;
	}

	/**
	 * ローカルファイル格納ディレクトリを返す。
	 * @return	ローカルファイル格納ディレクトリを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getLocalFileRootDirectory() {
		return _vfFilesRootDir;
	}

	/**
	 * モジュール実行定義が配置されるルートディレクトリを取得する。
	 * このメソッドが返すルートディレクトリは、ファイルシステムのルートディレクトリではなく、
	 * アプリケーションレベルで管理されるルートディレクトリとなる。
	 * @return	ルートディレクトリの抽象パス。
	 */
	public VirtualFile getRootDirectory() {
		return _vfRootDir;
	}

	/**
	 * モジュール実行定義が配置されるルートディレクトリの表示名を取得する。
	 * @return	ルートディレクトリの表示名
	 */
	public String getRootDisplayName() {
		return _strRootDisplayName;
	}

	/**
	 * この編集モデルの正規モジュール実行定義ディレクトリを返す。
	 * このメソッドが <tt>null</tt> ではない抽象パスを返し、
	 * {@link #getWorkingFilterRootDirectory()} が <tt>null</tt> を返すとき、
	 * このモデルのすべてのデータは、このメソッドが返すディレクトリを基準としている。
	 * @return	正規モジュール実行定義ディレクトリの抽象パス。
	 * 			正規モジュール実行定義が存在しない場合は <tt>null</tt>
	 */
	public VirtualFile getOriginalFilterRootDirectory() {
		return _vfOrgFilterRoot;
	}

	/**
	 * この編集モデルの作業用モジュール実行定義ディレクトリを返す。
	 * このメソッドが <tt>null</tt> ではない抽象パスを返すとき、
	 * このモデルのすべてのデータは、このメソッドが返すディレクトリを基準としている。
	 * @return	作業用モジュール実行定義ディレクトリの抽象パス。
	 * 			作業コピーが未定義の場合は <tt>null</tt>
	 */
	public VirtualFile getWorkingFilterRootDirectory() {
		return _vfWorkFilterRoot;
	}

	/**
	 * この編集モデルの現在のモジュール実行定義ディレクトリを返す。
	 * このメソッドが返す抽象パスは、{@link #getOriginalFilterRootDirectory()} または
	 * {@link #getWorkingFilterRootDirectory()} のどちらかが返す抽象パスを返す。
	 * @return	現在のモジュール実行定義ディレクトリの抽象パス。
	 * 			モジュール実行定義が存在しない場合は <tt>null</tt>
	 */
	public VirtualFile getAvailableFilterRootDirectory() {
		return (_vfWorkFilterRoot==null ? _vfOrgFilterRoot : _vfWorkFilterRoot);
	}

	/**
	 * このモデルに設定された、モジュール実行定義の親ディレクトリを返す。
	 * 通常はモジュール実行定義が配置されたディレクトリの抽象パスを返すが、
	 * 新規作成時のみ親ディレクトリを {@link #setFilterParentDirectory(VirtualFile)} から変更できる。
	 * @return	現在のモジュール実行定義の親ディレクトリ
	 */
	public VirtualFile getFilterParentDirectory() {
		if (_vfParent == null) {
			VirtualFile vf = getOriginalFilterRootDirectory();
			return (vf==null ? _vfRootDir : vf.getParentFile());
		}
		else {
			return _vfParent;
		}
	}

	/**
	 * このモデルに新規モジュール実行定義の親ディレクトリを設定する。
	 * 基本的に、新規作成時のみ親ディレクトリを変更できる。	
	 * @param newDir	新しい親ディレクトリ
	 * @return	親ディレクトリが変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IllegalStateException	新規作成モードではない場合
	 */
	public boolean setFilterParentDirectory(VirtualFile newDir) {
		if (!isNewEditing())
			throw new IllegalStateException("Edit type is not NEW : " + String.valueOf(getEditType()));
		VirtualFile oldDir = _vfParent;
		_vfParent = newDir;
		return Objects.isEqual(oldDir, newDir);
	}

	/**
	 * このモデルに設定されたフィルタ名を返す。
	 * 通常はモジュール実行定義ディレクトリの名称を返すが、
	 * 新規作成時のみ親ディレクトリを  から変更できる。
	 * @return	現在のモジュール実行定義の名称
	 */
	public String getFilterName() {
		return (_filterName!=null ? _filterName : (_vfOrgFilterRoot!=null ? _vfOrgFilterRoot.getName() : null));
	}

	/**
	 * このモデルに新規モジュール実行定義名を設定する。
	 * 基本的に、新規作成時のみ名称を変更できる。
	 * @param newName	新しいモジュール実行定義名
	 * @return	名称が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IllegalStateException	新規作成モードではない場合
	 */
	public boolean setFilterName(String newName) {
		if (!isNewEditing())
			throw new IllegalStateException("Edit type is not NEW : " + String.valueOf(getEditType()));
		String oldName = _filterName;
		if (newName != null && newName.length() > 0)
			_filterName = newName;
		else
			_filterName = null;
		return Objects.isEqual(oldName, _filterName);
	}

	/**
	 * 現在のモジュール実行定義の説明を返す。
	 * @return	現在の機能説明
	 */
	public String getMExecDefDescription() {
		return _defModuleData.getDescription();
	}

	/**
	 * 新しいモジュール実行定義の説明を設定する。
	 * @param desc	新しい説明
	 * @return	機能説明が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setMExecDefDescription(String desc) {
		String oldDesc = _defModuleData.getDescription();
		_defModuleData.setDescription(desc);
		return Objects.isEqual(oldDesc, _defModuleData.getDescription());
	}

	/**
	 * モジュール実行定義を保持する設定ファイルを取得する。
	 * @return	設定ファイルの抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefPrefsFile() {
		return _vfFilterPrefs;
	}

	/**
	 * モジュール実行定義に格納される引数履歴ファイルを取得する。
	 * @return	引数履歴ファイルの抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefHistoryPrefsFile() {
		VirtualFile vfTarget = getAvailableFilterRootDirectory();
		if (vfTarget==null) {
			return null;
		} else {
			return vfTarget.getChildFile(MExecDefFileManager.MEXECDEF_HISTORY_FILENAME);
		}
	}

	/**
	 * このモデルのモジュールファイル抽象パスを返す。
	 * @return モジュールファイルを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefModuleFile() {
		VirtualFile vfModule = _defModuleData.getModuleFile();
//		if (vfModule == null && _vfMacroRootDir != null) {
//			vfModule = _vfMacroRootDir.getChildFile(MExecDefFileManager.MACROFILTER_AADLMACRO_FILENAME);
//		}
		return vfModule;
	}

	/**
	 * このモデルのモジュールファイルの設定ファイル抽象パスを返す。
	 * @return	モジュール設定ファイルを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefModulePrefsFile() {
		VirtualFile vfPrefs = getMExecDefModuleFile();
		if (vfPrefs != null) {
			vfPrefs = ModuleFileManager.getPrefsFile(vfPrefs);
		}
		return vfPrefs;
	}

	/**
	 * このモデルのモジュール実行定義のモジュールファイル種別を返す。
	 * @return <code>ModuleFileType.AADL_MACRO</code>
	 */
	public ModuleFileType getMExecDefModuleType() {
		return ModuleFileType.AADL_MACRO;
	}

	/**
	 * このモデルのモジュール実行定義メインクラス名を返す。
	 * @return <code>AADLMacroEngine</code> クラスのクラス名
	 */
	public String getMExecDefMainClass() {
		return _defModuleData.getModuleMainClass();
	}

	/**
	 * モジュール実行定義編集ダイアログの動作種別を取得する。
	 * @return	動作種別
	 */
	public FilterEditModel.EditType getEditType() {
		return _editType;
	}

	/**
	 * モジュール実行定義情報の編集種別を指定する。
	 * @param newType	新しい編集種別
	 */
	public void setEditType(FilterEditModel.EditType newType) {
		_editType = newType;
	}

	/**
	 * このモジュール実行定義が編集中なら <tt>true</tt> を返す。
	 * @return	編集中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isEditing() {
		return (_editType==FilterEditModel.EditType.NEW || _editType==FilterEditModel.EditType.MODIFY);
	}

	/**
	 * このモジュール実行定義が新規作成中なら <tt>true</tt> を返す。
	 * @return	新規作成中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isNewEditing() {
		return (_editType==EditType.NEW);
	}
	
	public String formatFilePath(VirtualFile file) {
		if (!file.isAbsolute()) {
			file = file.getAbsoluteFile();
		}
		
		if (_vfFormatter != null) {
			String path = _vfFormatter.formatPath(file);
			if (path != null)
				return path;
		}
		
		String path;
		if (file.isDescendingFrom(_vfRootDir)) {
			if (_vfRootDir.equals(file)) {
				path = _strRootDisplayName;
			} else {
				path = _strRootDisplayName + Files.CommonSeparator + file.relativePathFrom(_vfRootDir, Files.CommonSeparatorChar);
			}
		} else {
			path = file.toString();
		}
		return path;
	}
	
	public String formatLocalFilePath(VirtualFile file) {
		if (!file.isAbsolute()) {
			file = file.getAbsoluteFile();
		}
		
		if (_vfFormatter != null) {
			String path = _vfFormatter.formatPath(file);
			if (path != null)
				return path;
		}

		VirtualFile vfExecDefDir = getAvailableFilterRootDirectory();
		if (vfExecDefDir != null && file.isDescendingFrom(vfExecDefDir)) {
			return file.relativePathFrom(vfExecDefDir, Files.CommonSeparatorChar);
		} else {
			return file.getPath();
		}
	}

	/**
	 * 指定されたファイルが、このフィルタ内のファイルかを判定する。
	 * @param target	判定対象のパス
	 * @return	このフィルタ内のファイルであれば <tt>true</tt>、
	 * それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	public boolean isIncludedMExecDef(final VirtualFile target) {
		return _isIncludedMExecDefInLocal(target);
	}

	/**
	 * 指定されたファイルが、このフィルタ内のファイルかを判定する。
	 * @param target	判定対象のパス
	 * @return	このフィルタ内のファイルであれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	protected boolean _isIncludedMExecDefInLocal(final VirtualFile target) {
		VirtualFile vfBase = getAvailableFilterRootDirectory();
		if (vfBase == null) {
			return false;
		} else {
			return target.isDescendingFrom(vfBase);
		}
	}

	/**
	 * 指定されたファイルが固定引数値格納ディレクトリ以下のファイルかを判定する。
	 * @param target	判定対象のパス
	 * @return	固定引数値格納ディレクトリ以下のパスであれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	public boolean isArgsDataFile(VirtualFile target) {
		if (_vfArgsRootDir==null) {
			return false;
		} else {
			return target.isDescendingFrom(_vfArgsRootDir);
		}
	}
	
	/**
	 * 指定されたファイルがローカルデータディレクトリ以下のファイルかを判定する。
	 * @param target		判定対象のパス
	 * @return	ローカルデータであれば <tt>true</tt>，そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	public boolean isLocalDataFile(VirtualFile target) {
		if (_vfFilesRootDir == null) {
			return false;
		} else {
			return target.isDescendingFrom(_vfFilesRootDir);
		}
	}

	/**
	 * このフィルタのパスフォーマッターを取得する。
	 * @return	パスフォーマッターのオブジェクト
	 */
	public VirtualFilePathFormatterList getPathFormatter() {
		return _vfFormatter;
	}

	/**
	 * このフィルタのパスフォーマッターを更新する。
	 */
	public void updatePathFormatter() {
		_vfFormatter.clear();
		
		if (_vfWorkFilterRoot != null) {
			_vfFormatter.add(new DefaultVirtualFilePathFormatter(_vfWorkFilterRoot, "", false));	// 基準パスを表示しない
		}
		
		if (_vfOrgFilterRoot != null) {
			_vfFormatter.add(new DefaultVirtualFilePathFormatter(_vfOrgFilterRoot, "", false));	// 基準パスを表示しない
		}
		
		if (_vformDefault != null) {
			_vfFormatter.add(_vformDefault);
		}
	}

	//------------------------------------------------------------
	// Public interfaces for data model for component
	//------------------------------------------------------------

	/**
	 * 指定されたサブフィルタ引数が参照可能なフィルタ定義引数が存在する場合に <tt>true</tt> を返す。
	 * @param subFilterArgData	判定するサブフィルタの引数
	 * @return	参照可能引数が存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean hasReferenceableMExecDefArguments(IModuleArgConfig subFilterArgData) {
		if (subFilterArgData == null)
			return false;
		if (subFilterArgData.isFixedValue() || subFilterArgData.getParameterType()==null)
			return false;	// 引数に固定値が設定されている場合は、参照不可
		if (_defArgsList.isEmpty())
			return false;	// フィルタ定義引数が設定されていない場合は、参照不可

		ModuleArgType targetArgType = subFilterArgData.getType();
		int numArgs = _defArgsList.size();
		for (int index = 0; index < numArgs; index++) {
			// 属性が一致するもののみ
			FilterArgEditModel defarg = _defArgsList.get(index);
			if (targetArgType == defarg.getType()) {
				// 参照可能引数あり
				return true;
			}
		}
		
		// 参照可能引数なし
		return false;
	}

	/**
	 * 指定されたサブフィルタ引数が参照可能なフィルタ定義引数のみを保持する、リストコンポーネント用データモデルを返す。
	 * このデータモデルに格納されるデータは、<code>ModuleArgConfig</code> オブジェクトとなる。
	 * @param subFilterArgData	判定する引数
	 * @return	参照可能な引数を保持する <code>ListModel</code> オブジェクトを返す。
	 * 			参照可能な引数が存在しない場合、要素が空の <code>ListModel</code> オブジェクトを返す。
	 */
	public DefaultListModel getReferenceableMExecDefArguments(IModuleArgConfig subFilterArgData) {
		DefaultListModel listmodel = new DefaultListModel();
		
		if (subFilterArgData == null)
			return listmodel;
		if (subFilterArgData.isFixedValue() || subFilterArgData.getParameterType()==null)
			return listmodel;	// 引数に固定値が設定されている場合は、参照不可
		if (_defArgsList.isEmpty())
			return listmodel;	// フィルタ定義引数が設定されていない場合は、参照不可

		ModuleArgType targetArgType = subFilterArgData.getType();
		int numArgs = _defArgsList.size();
		for (int index = 0; index < numArgs; index++) {
			// 属性が一致するもののみ
			FilterArgEditModel defarg = _defArgsList.get(index);
			if (targetArgType == defarg.getType()) {
				// 参照可能引数あり
				listmodel.addElement(defarg);
			}
		}
		
		return listmodel;
	}

	//------------------------------------------------------------
	// Public interfaces for MExecDef argument list
	//------------------------------------------------------------

	/**
	 * このモデルに設定されている、フィルタ定義引数編集に対するハンドラを取得する。
	 * @return	ハンドラが設定されていればそのオブジェクト、設定されていない場合は <tt>null</tt>
	 */
	public IFilterArgEditHandler getMExecDefArgEditHandler() {
		return _hDefArgEdit;
	}

	/**
	 * このモデルに、フィルタ定義引数編集に対するハンドラを設定する。
	 * @param newHandler	設定するハンドラ、もしくは <tt>null</tt>
	 */
	public void setMExecDefArgEditHandler(final IFilterArgEditHandler newHandler) {
		_hDefArgEdit = newHandler;
	}

	/**
	 * フィルタ定義引数のリストを返す。
	 * このリストを直接更新した場合、他のデータモデルへは通知されない。
	 */
	public FilterArgEditModelList getMExecDefArgsList() {
		return _defArgsList;
	}

	/**
	 * フィルタ定義引数をすべて削除する。
	 * エラーチェックは行わない。
	 */
	public void clearMExecDefArguments() {
		_defArgsList.clear();
	}

	/**
	 * フィルタ定義引数が存在しない場合に <tt>true</tt> を返す。
	 */
	public boolean isMExecDefArgumentEmpty() {
		return _defArgsList.isEmpty();
	}
	
	/**
	 * フィルタ定義引数の総数を返す。
	 */
	public int getMExecDefArgumentCount() {
		return _defArgsList.size();
	}

	/**
	 * フィルタ定義引数のリストから、指定されたオブジェクトが格納されている位置を取得する。
	 * @param defArgData	判定する引数データ
	 * @return	引数データが存在する位置のインデックスを返す。存在しない場合は (-1)
	 */
	public int indexOfMExecDefArgument(final FilterArgEditModel defArgData) {
		return _defArgsList.indexOf(defArgData);
	}

	/**
	 * フィルタ定義引数リストから、指定されたインデックスに格納されている引数データを取得する。
	 * @param index		引数のインデックス
	 * @return	引数データ
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public FilterArgEditModel getMExecDefArgument(int index) {
		return _defArgsList.get(index);
	}

	/**
	 * フィルタ定義引数のリストから、指定されたインデックスに格納されているオブジェクトの引数番号を返す。
	 * @param index		引数のインデックス
	 * @return	引数番号
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public int getMExecDefArgumentNo(int index) {
		return _defArgsList.get(index).getArgNo();
	}

	/**
	 * フィルタ定義引数のリストから、指定されたインデックスに格納されているオブジェクトの引数種別を返す。
	 * @param index	引数のインデックス
	 * @return	引数種別
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public ModuleArgType getMExecDefArgumentType(int index) {
		return _defArgsList.get(index).getType();
	}

	/**
	 * フィルタ定義引数のリストから、指定されたインデックスに格納されているオブジェクトの引数説明を返す。
	 * @param index	引数のインデックス
	 * @return	引数説明
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public String getMExecDefArgumentDescription(int index) {
		return _defArgsList.get(index).getDescription();
	}

	/**
	 * フィルタ定義引数のリストから、指定されたインデックスに格納されているオブジェクトの引数値を返す。
	 * @param index	引数のインデックス
	 * @return 引数値
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public Object getMExecDefArgumentValue(int index) {
		return _defArgsList.get(index).getValue();
	}

	/**
	 * フィルタ定義引数のインデックスに対応する位置に、指定された引数説明を設定する。
	 * @param index	引数のインデックス
	 * @param desc	引数説明
	 * @return	引数説明が更新された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public boolean setMExecDefArgumentDescription(int index, String desc) {
		boolean modified = false;
		String oldDesc = _defArgsList.get(index).getDescription();
		if (!Objects.isEqual(oldDesc, desc)) {
			modified = true;
			_defArgsList.get(index).setDescription(desc);
			//--- フィルタ引数定義テーブルの更新
			_defArgTableModel.fireArgumentDescriptionUpdated(index);
		}
		return modified;
	}

	/**
	 * フィルタ定義引数のインデックスに対応する位置に、指定された引数値を設定する。
	 * @param index	引数のインデックス
	 * @param value	引数値
	 * @return	引数値が更新された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public boolean setMExecDefArgumentValue(int index, Object value) {
		boolean modified = false;
		Object oldValue = _defArgsList.get(index).getValue();
		if (!Objects.isEqual(oldValue, value)) {
			modified = true;
			_defArgsList.get(index).setValue(value);
			//--- フィルタ引数定義テーブルの更新
			_defArgTableModel.fireArgumentValueUpdated(index);
		}
		return modified;
	}

	/**
	 * フィルタ定義引数リストの終端に、指定された引数データを追加する。
	 * @param type		引数属性
	 * @param desc		引数説明
	 * @param value		引数値
	 */
	public void addMExecDefArgument(final ModuleArgType type, final String desc, final Object value) {
		insertMExecDefArgument(_defArgsList.size(), type, desc, value);
	}

	/**
	 * フィルタ定義引数リストの終端に、指定された引数データを追加する。
	 * @param newDefArg	追加する引数データ
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void addMExecDefArgument(final FilterArgEditModel newDefArg) {
		insertMExecDefArgument(_defArgsList.size(), newDefArg);
	}

	/**
	 * フィルタ定義引数リストの指定された位置に、指定された引数データを挿入する。
	 * @param index		挿入位置のインデックス、もしくはフィルタ定義引数リストの要素数
	 * @param type		引数属性
	 * @param desc		引数説明
	 * @param value		引数値
	 */
	public void insertMExecDefArgument(int index, final ModuleArgType type, final String desc, final Object value) {
		FilterArgEditModel arg = new FilterArgEditModel(1, type, desc, value);
		if (value instanceof IMExecArgParam) {
			arg.setValue((IMExecArgParam)value);
		}
		insertMExecDefArgument(index, arg);
	}

	/**
	 * フィルタ定義引数リストの指定された位置に、指定された引数データを挿入する。
	 * @param index		挿入位置のインデックス、もしくはフィルタ定義引数リストの要素数
	 * @param newDefArg		挿入する引数データ
	 * @throws NullPointerException	引数データが <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	インデックスが 0 未満、もしくは挿入前のリスト要素数より大きい場合
	 */
	public void insertMExecDefArgument(int index, final FilterArgEditModel newDefArg) {
		_defArgsList.add(index, newDefArg);
		//--- フィルタ引数定義テーブルの更新
		_defArgTableModel.fireTableRowsInserted(index, index);
		//--- 引数番号の表示更新
		if (_hDefArgEdit != null) {
			int firstArgIndex = index + 1;
			int lastArgIndex = _defArgsList.size() - 1;
			if (firstArgIndex <= lastArgIndex) {
				_hDefArgEdit.onChangedArgument(firstArgIndex, lastArgIndex);
			}
		}
	}

	/**
	 * フィルタ定義引数リストから、指定された引数データを一つ削除する。
	 * @param defArg	削除する引数データ
	 * @return 引数データが削除できた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean removeMExecDefArgument(final FilterArgEditModel defArg) {
		int index = _defArgsList.indexOf(defArg);
		if (index >= 0) {
			return removeMExecDefArgument(index);
		} else {
			return false;
		}
	}

	/**
	 * フィルタ定義引数リストから、指定された位置の引数データを削除する。
	 * @param index		削除する位置のインデックス
	 * @return	引数データが削除できた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean removeMExecDefArgument(int index) {
		FilterArgEditModel removed = _defArgsList.remove(index);
		if (removed != null) {
			//--- フィルタ引数定義テーブルの更新
			_defArgTableModel.fireTableRowsDeleted(index, index);
			//--- ハンドラへ通知
			if (_hDefArgEdit != null) {
				_hDefArgEdit.onRemovedArgument(removed);
				int firstArgIndex = index;
				int lastArgIndex = _defArgsList.size() - 1;
				if (firstArgIndex <= lastArgIndex) {
					// 引数番号の表示更新
					_hDefArgEdit.onChangedArgument(firstArgIndex, lastArgIndex);
				}
			}
		}
		return (removed != null);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
