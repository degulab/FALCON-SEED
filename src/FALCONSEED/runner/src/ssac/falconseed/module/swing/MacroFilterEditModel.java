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
 * @(#)MacroFilterEditModel.java	3.1.3	2015/05/22 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroFilterEditModel.java	3.1.0	2014/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroFilterEditModel.java	2.0.0	2012/10/30
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import ssac.aadl.macro.AADLMacroEngine;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.ModuleFileType;
import ssac.falconseed.file.DefaultVirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.MacroFilterDefArgList;
import ssac.falconseed.module.MacroSubModuleRuntimeData;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.ModuleDataList;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.RelatedModuleList;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.setting.MExecDefMacroFilter;
import ssac.falconseed.module.setting.MExecDefMacroFilter.DefMacroFilterInvalidStates;
import ssac.falconseed.module.setting.MExecDefMacroFilter.IllegalMacroFilterPrefsException;
import ssac.falconseed.module.setting.MExecDefSettings;
import ssac.falconseed.module.swing.table.AbMacroFilterDefArgTableModel;
import ssac.falconseed.module.swing.table.ReferenceableSubFilterArgTableModel;
import ssac.util.Objects;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileManager;

/**
 * マクロフィルタ専用編集ダイアログのデータモデル。
 * このモデルは、マクロフィルタ専用編集ダイアログで生成されることを想定している。
 * 
 * @version 3.1.3	2015/05/22
 * @since 2.0.0
 */
public class MacroFilterEditModel
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

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//
	// data
	//
	
	/** このフィルタのモジュールデータ(サブフィルタは含まない) **/
	private final ModuleRuntimeData		_defModuleData;
	
	/** モジュール実行定義の編集用引数リスト **/
	private final MacroFilterDefArgList	_defArgsList;
	
	/** サブフィルタのリスト **/
	private final ModuleDataList<MacroSubModuleRuntimeData>	_subFilterList;
//	private final ModuleRuntimeDataList	_subFilterList;
	
	//
	// swing model
	//

	/** フィルタ定義引数編集コンポーネント用データモデル **/
	private final ImplMacroFilterDefArgTableModel	_modelDefArgTableModel;

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
	private EditType		_editType;

	/** 表示用パス文字列フォーマッター **/
	private VirtualFilePathFormatterList	_vfFormatter;
	
	/** モジュール実行定義のオリジナルルートディレクトリ **/
	private final VirtualFile	_vfOrgFilterRoot;
	/** モジュール実行定義の作業用ルートディレクトリ **/
	private final VirtualFile	_vfWorkFilterRoot;
	/** モジュール実行定義設定ファイルのパス **/
	private VirtualFile	_vfFilterPrefs;
	/** マクロフィルタの定義ファイルのパス **/
	private VirtualFile	_vfMacroFilterPrefs;
	/** フィルタ定義引数の固定値となるファイルが格納されるディレクトリ **/
	private VirtualFile	_vfArgsRootDir;
	/** マクロ(サブフィルタ)が格納されるディレクトリ **/
	private VirtualFile	_vfMacroRootDir;
	/** ローカルファイルが格納されるディレクトリ **/
	private VirtualFile	_vfFilesRootDir;

	/** モジュール実行定義が配置される親ディレクトリ **/
	private VirtualFile	_vfParent;
	/** 正規のフィルタ名 **/
	private String			_filterName;
	/**
	 * フィルタマクロ定義データ読み込み時の不正データ情報を保持するクラス。
	 * 不正データ情報がない場合は <tt>null</tt>
	 * @since 3.1.3
	 */
	private DefMacroFilterInvalidStates	_invalidStatesOnRead;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
//	public MacroFilterEditModel(EditType type, VirtualFilePathFormatter defaultFormatter, String rootDisplayName, VirtualFile rootDir,
//								VirtualFile orgFilterDir, VirtualFile workFilterDir)
//	{
//		this(type, defaultFormatter, rootDisplayName, rootDir, orgFilterDir, workFilterDir, null);
//	}
	
	/**
	 * 指定されたパラメータで、このオブジェクトの新しいインスタンスを生成する。
	 * @param type				編集種別
	 * @param defaultFormatter	標準のパスフォーマッター
	 * @param rootDisplayName	ルートディレクトリの表示名
	 * @param rootDir			ルートディレクトリ
	 * @param orgFilterDir		編集元のフィルタマクロのパス、編集ではない場合は <tt>null</tt>
	 * @param workFilterDir		編集作業に使用するディレクトリのパス
	 * @param preFilters		フィルタマクロ生成対象の実行履歴リスト(履歴番号のまま)、履歴からの生成ではない場合は <tt>null</tt>
	 * @throws IllegalMacroFilterPrefsException	フィルタマクロ定義データを読み込んだ場合において、解読不能の不正なデータが含まれている場合（詳細メッセージ含む）
	 */
	public MacroFilterEditModel(EditType type, VirtualFilePathFormatter defaultFormatter, String rootDisplayName, VirtualFile rootDir,
								VirtualFile orgFilterDir, VirtualFile workFilterDir, RelatedModuleList preFilters)
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
		if (preFilters != null && preFilters.size() > 0) {
			// 編集用サブフィルタリストの生成
			this._subFilterList = new ModuleDataList<MacroSubModuleRuntimeData>(preFilters.size());
			HashMap<ModuleRuntimeData, MacroSubModuleRuntimeData> mapHistToEdit = new HashMap<ModuleRuntimeData, MacroSubModuleRuntimeData>();
			//--- 編集用サブフィルタのインスタンスを生成(引数値はシャローコピー)
			long lRunNo = 1;
			for (ModuleRuntimeData data : preFilters) {
				MacroSubModuleRuntimeData aModule = new MacroSubModuleRuntimeData(data);
				aModule.setRunNo(lRunNo++);			// 実行番号の更新
				_subFilterList.add(aModule);
				mapHistToEdit.put(data, aModule);	// 履歴モジュールデータと編集用フィルタデータの対応関係を登録 : Added Ver.3.1.3
			}
			//--- 引数の参照リンクを編集用インスタンスに置換 : Added : Ver.3.1.3
			for (int i = 0; i < _subFilterList.size(); ++i) {
				MacroSubModuleRuntimeData aModule = _subFilterList.get(i);
				int numArgs = aModule.getArgumentCount();
				for (int argidx = 0; argidx < numArgs; ++argidx) {
					ModuleArgConfig argdata = aModule.getArgument(argidx);
					Object argvalue = argdata.getValue();
					if (argvalue instanceof ModuleArgID) {
						ModuleArgID srcArgId = (ModuleArgID)argvalue;
						MacroSubModuleRuntimeData dstModule = mapHistToEdit.get(srcArgId.getData());
						if (dstModule != null) {
							// 履歴の引数参照を、編集用インスタンスの参照に更新
							argdata.setValue(new ModuleArgID(dstModule, srcArgId.argNo()));
						} else {
							// 編集用インスタンスが存在しないため、引数値を空にする
							argdata.setValue(null);
						}
					}
				}
			}
		} else {
			this._subFilterList = new ModuleDataList<MacroSubModuleRuntimeData>();
		}
		this._defArgsList = new MacroFilterDefArgList();
		this._modelDefArgTableModel = new ImplMacroFilterDefArgTableModel();
		
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
		
		commonConstruction(workFilterDir==null ? orgFilterDir : workFilterDir);
	}

	private final void commonConstruction(VirtualFile target) //throws IOException
	{
		assert (target != null);
		//--- この場合の target は、対象モジュール実行定義のルートディレクトリ
		if (!target.isDirectory()) {
			// 対象がディレクトリではない
			throw new IllegalArgumentException("The specified target filter root is not directory : \"" + target.toString() + "\"");
		}
		this._vfFilterPrefs = MExecDefFileManager.getModuleExecDefDataFile(target);
		this._vfMacroFilterPrefs = MExecDefFileManager.getMacroFilterDefDataFile(target);
		this._vfArgsRootDir  = target.getChildFile(MExecDefFileManager.MEXECDEF_MODULEARG_DIRNAME);
		this._vfMacroRootDir = target.getChildFile(MExecDefFileManager.MACROFILTER_MACRO_ROOT_DIRNAME);
		this._vfFilesRootDir = target.getChildFile(MExecDefFileManager.MEXECDEF_FILES_ROOT_DIRNAME);
		
		// load filter prefs
		MExecDefSettings defFilter = new MExecDefSettings();
		defFilter.setPropertyTarget(_vfFilterPrefs);
		if (_vfFilterPrefs.exists()) {
			//--- モジュール定義ファイルが存在する場合は、読み込み
			defFilter.loadForTarget(_vfFilterPrefs);
		}
		this._defModuleData.setData(defFilter);
		//--- マクロフィルタ定義の取得
		MExecDefMacroFilter defMacro = new MExecDefMacroFilter();
		defMacro.setPropertyTarget(_vfMacroFilterPrefs);
		if (_vfMacroFilterPrefs.exists()) {
			//--- フィルタマクロ定義ファイルが存在する場合は、読み込み
			defMacro.loadForTarget(_vfMacroFilterPrefs);
			DefMacroFilterInvalidStates states = defMacro.getMacroFilterData(_defArgsList, _subFilterList);
			if (states != null && states.hasError()) {
				_invalidStatesOnRead = states;
			}
		}
		
		// パスの更新
		updatePathFormatter();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 定義データ読み込み時の不正データ情報の有無を判定する。
	 * @return	定義データ読み込み時の不正データ情報を保持している場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 3.1.3
	 */
	public boolean hasInvalidDataStatesWhenReadDefs() {
		return (_invalidStatesOnRead != null);
	}

	/**
	 * 定義データ読み込み時の不正データ情報をクリアする。
	 * @since 3.1.3
	 */
	public void clearInvalidDataStatesWhenReadDefs() {
		if (_invalidStatesOnRead != null) {
			_invalidStatesOnRead.clear();
			_invalidStatesOnRead = null;
		}
	}

	/**
	 * 定義データ読み込み時の不正データ情報を取得する。
	 * @return	不正データ情報を保持している場合はそのオブジェクト、それ以外の場合は <tt>null</tt>
	 * @since 3.1.3
	 */
	public DefMacroFilterInvalidStates getInvalidDataStatesWhenReadDefs() {
		return _invalidStatesOnRead;
	}

	/**
	 * このフィルタ定義のモジュールデータを返す。
	 */
	public ModuleRuntimeData getModuleData() {
		return _defModuleData;
	}

	/**
	 * フィルタ定義引数編集コンポーネント用データモデルを返す。
	 */
	public AbMacroFilterDefArgTableModel getMExecDefArgTableModel() {
		return _modelDefArgTableModel;
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
	 * マクロデータ格納ディレクトリを返す。
	 * @return	マクロデータ格納ディレクトリを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMacroRootDirectory() {
		return _vfMacroRootDir;
	}

	/**
	 * マクロデータ格納ディレクトリ内のサブフィルタ格納ディレクトリを返す。
	 * @return	サブフィルタ格納ディレクトリを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getSubFiltersRootDirectory() {
		if (_vfMacroRootDir==null) {
			return null;
		} else {
			return _vfMacroRootDir.getChildFile(MExecDefFileManager.MACROFILTER_SUBFILTER_DIRNAME_);
		}
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
	 * マクロフィルタ定義を保持する設定ファイルを取得する。
	 * @return	設定ファイルの抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMacroFilterDefPrefsFile() {
		return _vfMacroFilterPrefs;
	}

	/**
	 * このモデルのモジュールファイル抽象パスを返す。
	 * @return モジュールファイルを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefModuleFile() {
		VirtualFile vfModule = _defModuleData.getModuleFile();
		if (vfModule == null && _vfMacroRootDir != null) {
			vfModule = _vfMacroRootDir.getChildFile(MExecDefFileManager.MACROFILTER_AADLMACRO_FILENAME);
		}
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
		return  AADLMacroEngine.class.getName();
	}

	/**
	 * モジュール実行定義編集ダイアログの動作種別を取得する。
	 * @return	動作種別
	 */
	public EditType getEditType() {
		return _editType;
	}

	/**
	 * モジュール実行定義情報の編集種別を指定する。
	 * @param newType	新しい編集種別
	 */
	public void setEditType(EditType newType) {
		_editType = newType;
	}

	/**
	 * このモジュール実行定義が編集中なら <tt>true</tt> を返す。
	 * @return	編集中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isEditing() {
		return (_editType==EditType.NEW || _editType==EditType.MODIFY);
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
	 * 引数番号を、フィルタマクロの引数参照を示す文字列に整形する。
	 * @param argno	引数番号
	 * @return	整形した文字列
	 */
	static public String formatLinkArgNo(int argno) {
		return String.format("${%d}", argno);
	}

	/**
	 * 指定された引数情報を、フィルタマクロの引数参照を示す文字列に整形する。
	 * @param referArg	引数情報
	 * @return	整形した文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 3.1.0
	 */
	static public String formatLinkArgNo(IModuleArgConfig referArg) {
		if (referArg instanceof FilterArgReferValueEditModel) {
			// フィルタマクロ定義引数の参照
			FilterArgReferValueEditModel refDefArg = (FilterArgReferValueEditModel)referArg;
			return String.format("%s${%d}%s", refDefArg.getPrefix(), refDefArg.getArgNo(), refDefArg.getSuffix());
		} else {
			// その他の引数参照
			return String.format("${%d}", referArg.getArgNo());
		}
	}

	/**
	 * 指定されたインデックスを、引数番号を示す文字列に整形する。
	 * @param rowIndex	引数の位置を示すインデックス
	 * @return	整形した文字列
	 */
	static public String formatArgNo(int rowIndex) {
		return String.format("($%d)", rowIndex+1);
	}

	/**
	 * 指定されたインデックスと引数属性を、表示形式となる文字列に整形する。
	 * @param rowIndex	引数の位置を示すインデックス
	 * @param type		引数属性
	 * @return	整形した文字列
	 * @throws NullPointerException	<em>type</em> が <tt>null</tt> の場合
	 */
	static public String formatArgType(int rowIndex, ModuleArgType type) {
		return String.format("($%d) %s", rowIndex+1, type.toString());
	}

	/**
	 * <em>vfFromDir</em> 以下のパスの場合に、<em>vfToDir</em> 以下のパスに変換する。
	 * <em>vfFromDir</em> もしくは <em>vfToDir</em> のどちらかが <tt>null</tt> の場合、
	 * <em>vfToDir</em> 以下の場合、<em>vfFromDir</em> 以下ではない場合、パスは変換されない。
	 * @param target		変換対象のパス
	 * @param vfFromDir		変換元の基準パス
	 * @param vfToDir		変換先の基準パス
	 * @return	変換後のパス
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	static public VirtualFile convertBasePath(VirtualFile target, VirtualFile vfFromDir, VirtualFile vfToDir) {
		VirtualFile vfResult = target;
		if (vfFromDir != null && vfToDir != null) {
			if (!target.isDescendingFrom(vfToDir) && !target.isDescendingFrom(vfFromDir)) {
				VirtualFile vf = target.relativeFileFrom(vfFromDir);
				if (!vf.isAbsolute()) {
					vfResult = vfToDir.getChildFile(vf.getPath());
				}
			}
		}
		return vfResult;
	}

	/**
	 * <em>target</em> が、<em>path1</em> もしくは <em>path2</em> のどちらかに格納されているかを判定する。
	 * @param target	判定対象のパス
	 * @param path1		基準となるパス
	 * @param path2		基準となるパス
	 * @return	どちらかのパス配下のものであれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	static public boolean isDescendingFrom(VirtualFile target, VirtualFile path1, VirtualFile path2) {
		if (path1 != null) {
			if (target.isDescendingFrom(path1)) {
				return true;
			}
		}
		
		if (path2 != null) {
			if (target.isDescendingFrom(path2)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * 指定されたファイルが、このマクロフィルタ内のファイルかを判定する。
	 * この判定では、編集中に追加されたサブフィルタも判定の対象とする。
	 * @param target	判定対象のパス
	 * @return	このマクロフィルタ内のファイル、もしくは編集中に追加されたサブフィルタに属するファイルであれば <tt>true</tt>、
	 * それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	public boolean isIncludedMExecDef(final VirtualFile target) {
		// このフィルタルート配下か?
		if (isIncludedMExecDefWithoutSubFilters(target)) {
			return true;
		}
		
		// サブフィルタ配下か?
		for (MacroSubModuleRuntimeData moduledata : _subFilterList) {
			if (target.isDescendingFrom(moduledata.getExecDefDirectory())) {
				return true;
			}
		}
		
		// no included
		return false;
	}

	/**
	 * 指定されたファイルが、このマクロフィルタ内のファイルかを判定する。
	 * このメソッドでは、サブフィルタ内のファイルかどうかの判定は行わず、
	 * このフィルタ定義の基準パスに属するかどうかのみ判定する。
	 * @return	このマクロフィルタ内のファイルであれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	public boolean isIncludedMExecDefWithoutSubFilters(VirtualFile target) {
		VirtualFile vfBase = getAvailableFilterRootDirectory();
		if (vfBase == null) {
			return false;
		} else {
			return target.isDescendingFrom(vfBase);
		}
	}
	
	/**
	 * 指定されたファイルが、このマクロフィルタ内のファイルかを判定する。
	 * この判定では、編集中に追加されたサブフィルタも判定の対象とする。
	 * @param target	判定対象のパス
	 * @param ignoreModule	判定時に除外するサブフィルタデータ
	 * @return	このマクロフィルタ内のファイル、もしくは編集中に追加されたサブフィルタに属するファイルであれば <tt>true</tt>、
	 * それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	public boolean isIncludedMExecDef(VirtualFile target, ModuleRuntimeData ignoreModule) {
		if (isIncludedMExecDefWithoutSubFilters(target))
			return true;
		
		return isSubFilterFile(target, ignoreModule);
	}

	/**
	 * 指定されたファイルが、このマクロフィルタに含まれるサブフィルタ内のファイルかを判定する。
	 * @param target	判定対象のパス
	 * @param ignoreModule	判定時に除外するサブフィルタデータ
	 * @return	サブフィルタ内のファイルであれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	public boolean isSubFilterFile(VirtualFile target, ModuleRuntimeData ignoreModule) {
		for (MacroSubModuleRuntimeData moduledata : _subFilterList) {
			if (moduledata != ignoreModule) {
				if (target.isDescendingFrom(moduledata.getExecDefDirectory())) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * <em>targetModule</em> 内のファイルを参照しているサブフィルタを取得する。
	 * @param resultset		参照関係にあるサブフィルタを格納するセット
	 * @param targetModule	判定対象のフィルタデータ
	 * @return	ファイル参照しているサブフィルタが存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean getReferedSubFilterForFile(Set<ModuleRuntimeData> resultset, ModuleRuntimeData targetModule) {
		boolean found = false;
		VirtualFile vfBase = targetModule.getExecDefDirectory();
		for (MacroSubModuleRuntimeData moduledata : _subFilterList) {
			if (moduledata != targetModule) {
				for (ModuleArgConfig argdata : moduledata) {
					VirtualFile vfTarget = VirtualFileManager.toVirtualFile(argdata.getValue());
					if (vfTarget != null && vfTarget.isDescendingFrom(vfBase)) {
						found = true;
						resultset.add(moduledata);
					}
				}
			}
		}
		return found;
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
	 * 指定されたファイルがマクロデータディレクトリ以下のファイルかを判定する。
	 * @param target	判定対象のパス
	 * @return	マクロデータディレクトリに格納されているパスであれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 */
	public boolean isMacroDataFile(VirtualFile target) {
		if (_vfMacroRootDir == null) {
			return false;
		} else {
			return target.isDescendingFrom(_vfMacroRootDir);
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
	
	public VirtualFilePathFormatterList getPathFormatter() {
		return _vfFormatter;
	}
	
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

	/**
	 * 指定されたサブフィルタ引数が参照可能なサブフィルタの引数が存在する場合に <tt>true</tt> を返す。
	 * @param moduledata		判定する引数が属するモジュール定義情報
	 * @param subFilterArgData	判定する引数
	 * @return	参照可能引数が存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean hasReferenceableSubFilterArguments(ModuleRuntimeData moduledata, IModuleArgConfig subFilterArgData) {
		if (subFilterArgData == null || moduledata == null)
			return false;
		ModuleArgType subFilterArgType = subFilterArgData.getType();
		if (ModuleArgType.IN != subFilterArgType && ModuleArgType.SUB != subFilterArgType)
			return false;	// [IN],[SUB] 引数以外は、参照不可
		if (subFilterArgData.isFixedValue() || subFilterArgData.getParameterType()==null)
			return false;	// 引数に固定値が設定されている場合は、参照不可
		if (_subFilterList.isEmpty())
			return false;	// サブフィルタが存在しなければ、参照不可

		IMExecArgParam argparam = subFilterArgData.getParameterType();
		for (MacroSubModuleRuntimeData data : _subFilterList) {
			if (data == moduledata) {
				// ターゲット引数が属するモジュール以降は、参照不可
				break;
			}
			
			// モジュールの引数判定
			if (ModuleArgType.IN == subFilterArgType) {
				for (ModuleArgConfig refarg : data) {
					if (ModuleArgType.OUT == refarg.getType()) {
						// 参照可能な引数は [OUT] 属性のみ
						if (refarg.isFixedValue()) {
							// 固定値の引数なら、参照可能
							return true;
						}
						else if (refarg.getParameterType() == argparam) {
							// 実行時可変パラメータの型が同一なら、参照可能
							return true;
						}
					}
				}
			}
			else if (ModuleArgType.SUB == subFilterArgType) {
				for (ModuleArgConfig refarg : data) {
					if (ModuleArgType.PUB == refarg.getType()) {
						// 参照可能な引数は [PUB] 属性のみ
						return true;
					}
				}
			}
		}

		// 参照可能引数なし
		return false;
	}

	/**
	 * 指定されたサブフィルタ引数が参照可能なサブフィルタの引数のみを保持する、リストコンポーネント用データモデルのリストを返す。
	 * このデータモデルに格納されるデータは、<code>ModuleArgID</code> オブジェクトとなる。
	 * @param moduledata		判定する引数が属するモジュール定義情報
	 * @param subFilterArgData	判定する引数
	 * @return	参照可能な引数を保持するリストを返す。
	 * 			参照可能な引数が存在しない場合、要素が空のリストを返す。
	 */
	public List<ListModel> getReferenceableSubFilterArguments(ModuleRuntimeData moduledata, IModuleArgConfig subFilterArgData) {
		ArrayList<ListModel> list = new ArrayList<ListModel>();
		
		if (subFilterArgData == null || moduledata == null)
			return list;
		ModuleArgType subFilterArgType = subFilterArgData.getType();
		if (ModuleArgType.IN != subFilterArgType && ModuleArgType.SUB != subFilterArgType)
			return list;	// [IN],[SUB] 引数以外は、参照不可
		if (subFilterArgData.isFixedValue() || subFilterArgData.getParameterType()==null)
			return list;	// 引数に固定値が設定されている場合は、参照不可
		if (_subFilterList.isEmpty())
			return list;	// サブフィルタが存在しなければ、参照不可

		IMExecArgParam argparam = subFilterArgData.getParameterType();
		if (ModuleArgType.IN == subFilterArgType) {
			for (MacroSubModuleRuntimeData data : _subFilterList) {
				if (data == moduledata) {
					// ターゲット引数が属するモジュール以降は、参照不可
					break;
				}
				
				// モジュールの引数判定
				DefaultListModel lmodel = null;
				for (ModuleArgConfig refarg : data) {
					if (ModuleArgType.OUT == refarg.getType()) {
						// 参照可能な引数は [OUT] 属性のみ
						if (refarg.isFixedValue() || refarg.getParameterType()==argparam) {
							// 固定値の引数、もしくは実行時可変パラメータの型が同一なら、参照可能
							if (lmodel == null)
								lmodel = new DefaultListModel();
							ModuleArgID argid = new ModuleArgID(data, refarg.getArgNo());
							lmodel.addElement(argid);
						}
					}
				}
				if (lmodel != null) {
					list.add(lmodel);
				}
			}
		}
		else if (ModuleArgType.SUB == subFilterArgType) {
			for (MacroSubModuleRuntimeData data : _subFilterList) {
				if (data == moduledata) {
					// ターゲット引数が属するモジュール以降は、参照不可
					break;
				}
				
				// モジュールの引数判定
				DefaultListModel lmodel = null;
				for (ModuleArgConfig refarg : data) {
					if (ModuleArgType.PUB == refarg.getType()) {
						// 参照可能な引数は [PUB] 属性のみ
						if (lmodel == null)
							lmodel = new DefaultListModel();
						ModuleArgID argid = new ModuleArgID(data, refarg.getArgNo());
						lmodel.addElement(argid);
					}
				}
				if (lmodel != null) {
					list.add(lmodel);
				}
			}
		}
		
		return list;
	}

	/**
	 * 指定されたサブフィルタ引数が参照可能なサブフィルタの引数のみを保持する、テーブルコンポーネント用データモデルのリストを返す。
	 * このデータモデルに格納されるデータは、<code>ModuleArgID</code> オブジェクトとなる。
	 * @param moduledata		判定する引数が属するモジュール定義情報
	 * @param subFilterArgData	判定する引数
	 * @return	参照可能な引数を保持するリストを返す。
	 * 			参照可能な引数が存在しない場合、要素が空のリストを返す。
	 * @since 3.1.0
	 */
	public List<ReferenceableSubFilterArgTableModel> getReferenceableSubFilterArgumentTableModel(ModuleRuntimeData moduledata, IModuleArgConfig subFilterArgData) {
		ArrayList<ReferenceableSubFilterArgTableModel> list = new ArrayList<ReferenceableSubFilterArgTableModel>();
		
		if (subFilterArgData == null || moduledata == null)
			return list;
		ModuleArgType subFilterArgType = subFilterArgData.getType();
		if (ModuleArgType.IN != subFilterArgType && ModuleArgType.SUB != subFilterArgType)
			return list;	// [IN],[SUB] 引数以外は、参照不可
		if (subFilterArgData.isFixedValue() || subFilterArgData.getParameterType()==null)
			return list;	// 引数に固定値が設定されている場合は、参照不可
		if (_subFilterList.isEmpty())
			return list;	// サブフィルタが存在しなければ、参照不可

		IMExecArgParam argparam = subFilterArgData.getParameterType();
		if (ModuleArgType.IN == subFilterArgType) {
			for (MacroSubModuleRuntimeData data : _subFilterList) {
				if (data == moduledata) {
					// ターゲット引数が属するモジュール以降は、参照不可
					break;
				}
				
				// モジュールの引数判定
				ReferenceableSubFilterArgTableModel lmodel = null;
				for (ModuleArgConfig refarg : data) {
					if (ModuleArgType.OUT == refarg.getType()) {
						// 参照可能な引数は [OUT] 属性のみ
						if (refarg.isFixedValue() || refarg.getParameterType()==argparam) {
							// 固定値の引数、もしくは実行時可変パラメータの型が同一なら、参照可能
							if (lmodel == null)
								lmodel = new ReferenceableSubFilterArgTableModel(this);
							ModuleArgID argid = new ModuleArgID(data, refarg.getArgNo());
							lmodel.addRowData(argid);
						}
					}
				}
				if (lmodel != null) {
					list.add(lmodel);
				}
			}
		}
		else if (ModuleArgType.SUB == subFilterArgType) {
			for (MacroSubModuleRuntimeData data : _subFilterList) {
				if (data == moduledata) {
					// ターゲット引数が属するモジュール以降は、参照不可
					break;
				}
				
				// モジュールの引数判定
				ReferenceableSubFilterArgTableModel lmodel = null;
				for (ModuleArgConfig refarg : data) {
					if (ModuleArgType.PUB == refarg.getType()) {
						// 参照可能な引数は [PUB] 属性のみ
						if (lmodel == null)
							lmodel = new ReferenceableSubFilterArgTableModel(this);
						ModuleArgID argid = new ModuleArgID(data, refarg.getArgNo());
						lmodel.addRowData(argid);
					}
				}
				if (lmodel != null) {
					list.add(lmodel);
				}
			}
		}
		
		return list;
	}

	//------------------------------------------------------------
	// Public interfaces for MExecDef argument list
	//------------------------------------------------------------
	
	public IFilterArgEditHandler getMExecDefArgEditHandler() {
		return _hDefArgEdit;
	}
	
	public void setMExecDefArgEditHandler(final IFilterArgEditHandler newHandler) {
		_hDefArgEdit = newHandler;
	}

	/**
	 * 指定された値がサブフィルタからの参照値である場合に <tt>true</tt> を返す。
	 * @param defArgData	判定するフィルタ定義引数
	 * @return	参照値であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean isReferencedValueFromSubFilter(final Object refValue) {
		if (!_subFilterList.isEmpty()) {
			if (_hDefArgEdit != null) {
				return _hDefArgEdit.isReferencedValue(refValue);
			}
			else {
				for (MacroSubModuleRuntimeData data : _subFilterList) {
					for (ModuleArgConfig subarg : data) {
						if (refValue.equals(subarg.getValue())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * フィルタ定義引数のリストを返す。
	 * このリストを直接更新した場合、他のデータモデルへは通知されない。
	 */
	public MacroFilterDefArgList getMExecDefArgsList() {
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
	
	public ModuleArgType getMExecDefArgumentType(int index) {
		return _defArgsList.get(index).getType();
	}
	
	public String getMExecDefArgumentDescription(int index) {
		return _defArgsList.get(index).getDescription();
	}
	
	public Object getMExecDefArgumentValue(int index) {
		return _defArgsList.get(index).getValue();
	}
	
	public boolean setMExecDefArgumentDescription(int index, String desc) {
		boolean modified = false;
		String oldDesc = _defArgsList.get(index).getDescription();
		if (!Objects.isEqual(oldDesc, desc)) {
			modified = true;
			_defArgsList.get(index).setDescription(desc);
			//--- フィルタ引数定義テーブルの更新
			_modelDefArgTableModel.fireArgumentDescriptionUpdated(index);
		}
		return modified;
	}
	
	public boolean setMExecDefArgumentValue(int index, Object value) {
		boolean modified = false;
		Object oldValue = _defArgsList.get(index).getValue();
		if (!Objects.isEqual(oldValue, value)) {
			modified = true;
			_defArgsList.get(index).setValue(value);
			//--- フィルタ引数定義テーブルの更新
			_modelDefArgTableModel.fireArgumentValueUpdated(index);
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
		_modelDefArgTableModel.fireTableRowsInserted(index, index);
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
			_modelDefArgTableModel.fireTableRowsDeleted(index, index);
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
	// Public interfaces for Sub-filter list
	//------------------------------------------------------------

	/**
	 * サブフィルタのモジュール実行定義データのリストを返す。
	 * このリストを直接更新した場合、他のデータモデルへは通知されない。
	 */
	public ModuleDataList<MacroSubModuleRuntimeData> getSubFilterList() {
		return _subFilterList;
	}

	/**
	 * サブフィルタが存在しない場合に <tt>true</tt> を返す。
	 */
	public boolean isSubFilterEmpty() {
		return _subFilterList.isEmpty();
	}

	/**
	 * サブフィルタの総数を返す。
	 */
	public int getSubFilterCount() {
		return _subFilterList.size();
	}
	
	/**
	 * サブフィルタに設定された実行番号に一致する、最初のサブフィルタのインデックスを返す。
	 * このメソッドでは、リストの先頭から検索し、最初に見つかったサブフィルタのインデックスを返す。
	 * @param runNo		検索する実行番号
	 * @return	最初に見つかったサブフィルタのインデックス、見つからなかった場合は (-1)
	 */
	public int indexBySubFiltersRunNo(long runNo) {
		return _subFilterList.indexByRunNo(runNo);
	}

	/**
	 * サブフィルタリストから、指定されたオブジェクトが格納されている位置を取得する。
	 * @param data	判定するモジュール実行定義データ
	 * @return	オブジェクトが存在する位置のインデックスを返す。存在しない場合は (-1)
	 */
	public int indexOfSubFilter(final ModuleRuntimeData data) {
		return _subFilterList.indexOf(data);
	}

	/**
	 * サブフィルタリストから、指定されたインデックスに格納されているモジュール実行定義データを取得する。
	 * @param index		インデックス
	 * @return	モジュール実行定義データ
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public MacroSubModuleRuntimeData getSubFilter(int index) {
		return _subFilterList.get(index);
	}

	/**
	 * サブフィルタリストから、指定されたインデックスに格納されているモジュール実行定義データの実行番号を取得する。
	 * @param index		インデックス
	 * @return	モジュール実行定義データの実行番号
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public long getSubFilterRunNo(int index) {
		return _subFilterList.get(index).getRunNo();
	}

	//------------------------------------------------------------
	// Public interfaces for Sub-filter's argument relation
	//------------------------------------------------------------
	
//	public void refreshSubFilterArgumentsRelation() {
////		_mapArgsRelation.clear();
//		for (ModuleRuntimeData data : _subFilterList) {
//			for (IModuleArgConfig arg : data) {
//				Object value = arg.getValue();
//				if (value instanceof ModuleArgID) {
//					//--- リレーション発見
//					ModuleArgID destArgID = new ModuleArgID(data, arg.getArgNo());
//					ModuleArgID srcArgID  = (ModuleArgID)value;
//					//--- リレーション登録
////					_mapArgsRelation.put(srcArgID, destArgID);
//				}
//			}
//		}
//	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * マクロフィルタの定義引数編集テーブル用のデータモデル。
	 * このモデルは、<code>MacroFilterEditModel</code> の編集に応じて更新されるため、
	 * サブクラスとして実装している。
	 */
	private class ImplMacroFilterDefArgTableModel extends AbMacroFilterDefArgTableModel
	{
		private static final long serialVersionUID = 1L;

		public boolean hasEditModel() {
			return true;
		}
		
		public MacroFilterEditModel getEditModel() {
			return MacroFilterEditModel.this;
		}
		
		public void onMovedRows(int firstRow, int lastRow) {
			if (_hDefArgEdit != null) {
				_hDefArgEdit.onChangedArgument(firstRow, lastRow);
			}
		}
	}
}
