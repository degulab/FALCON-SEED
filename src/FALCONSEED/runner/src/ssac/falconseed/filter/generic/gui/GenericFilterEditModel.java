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
 * @(#)GenericFilterEditModel.java	3.2.1	2015/07/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericFilterEditModel.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import javax.swing.tree.TreePath;
import javax.xml.stream.XMLStreamException;

import ssac.aadl.fs.module.FilterArgType;
import ssac.aadl.fs.module.generic.GenericFilterMain;
import ssac.aadl.fs.module.schema.SchemaConfig;
import ssac.aadl.fs.module.schema.SchemaDTFormatConfig;
import ssac.aadl.fs.module.schema.SchemaElementObject;
import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.SchemaFilterArgValue;
import ssac.aadl.fs.module.schema.SchemaInstanceRelationMap;
import ssac.aadl.fs.module.schema.SchemaLiteralValue;
import ssac.aadl.fs.module.schema.SchemaObject;
import ssac.aadl.fs.module.schema.SchemaValueLink;
import ssac.aadl.fs.module.schema.exp.SchemaExpressionData;
import ssac.aadl.fs.module.schema.exp.SchemaExpressionList;
import ssac.aadl.fs.module.schema.exp.SchemaJoinConditionData;
import ssac.aadl.fs.module.schema.exp.SchemaJoinConditionList;
import ssac.aadl.fs.module.schema.io.SchemaConfigReader;
import ssac.aadl.fs.module.schema.table.SchemaInputCsvDataField;
import ssac.aadl.fs.module.schema.table.SchemaInputCsvDataTable;
import ssac.aadl.fs.module.schema.table.SchemaOutputCsvDataField;
import ssac.aadl.fs.module.schema.table.SchemaOutputCsvDataTable;
import ssac.aadl.fs.module.schema.type.RuntimeSchemaValueTypeManager;
import ssac.aadl.fs.module.schema.type.SchemaDateTimeFormats;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.ModuleFileType;
import ssac.falconseed.file.DefaultVirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.filter.common.gui.FilterEditModel;
import ssac.falconseed.filter.generic.gui.arg.GenericFilterArgEditModel;
import ssac.falconseed.filter.generic.gui.arg.GenericFilterDefArgsTableModel;
import ssac.falconseed.filter.generic.gui.exp.GenericExpressionElementEditModel;
import ssac.falconseed.filter.generic.gui.exp.GenericExpressionSchemaTableModel;
import ssac.falconseed.filter.generic.gui.exp.GenericJoinCondElementEditModel;
import ssac.falconseed.filter.generic.gui.exp.GenericJoinCondSchemaTableModel;
import ssac.falconseed.filter.generic.gui.table.GenericTableSchemaRootNode;
import ssac.falconseed.filter.generic.gui.table.GenericTableSchemaTreeNode;
import ssac.falconseed.filter.generic.gui.table.InputCsvFieldSchemaEditModel;
import ssac.falconseed.filter.generic.gui.table.InputCsvTableSchemaEditModel;
import ssac.falconseed.filter.generic.gui.table.OutputCsvFieldSchemaEditModel;
import ssac.falconseed.filter.generic.gui.table.OutputCsvTableSchemaEditModel;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaElementTreeModel;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaElementTreeNode;
import ssac.falconseed.module.FilterArgEditModelList;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgCsvFile;
import ssac.falconseed.module.args.MExecArgString;
import ssac.falconseed.module.setting.MExecDefMacroFilter.IllegalMacroFilterPrefsException;
import ssac.falconseed.module.setting.MExecDefSettings;
import ssac.falconseed.module.swing.FilterArgEditModel;
import ssac.falconseed.module.swing.IFilterArgEditHandler;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.Objects;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;

/**
 * 汎用フィルタ専用編集ダイアログのデータモデル。
 * このモデルは、汎用フィルタ専用編集ダイアログで生成されることを想定している。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericFilterEditModel implements FilterEditModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final int	INDEX_LEFT	= 0;
	static public final int	INDEX_RIGHT	= 1;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//
	// data
	//

	/** 実行時のデータ型マネージャ：データ型への値変換に利用 **/
	private final RuntimeSchemaValueTypeManager	_schemaValueTypeManager = new RuntimeSchemaValueTypeManager();
	
	/** このフィルタのモジュールデータ **/
	private final ModuleRuntimeData		_defModuleData;
	
	/** モジュール実行定義の編集用引数リスト **/
	private final FilterArgEditModelList	_defArgsList;

	/** 計算式に指定可能なフィルタ定義引数のリスト **/
	private final ArrayList<GenericFilterArgEditModel>	_defEditableArgsList;

	//--- DateTime formats
	/** この編集モデルにおける日付時刻書式編集用データモデル **/
	private SchemaDateTimeFormatEditModel	_dtfEditModel;

	/** スキーマオブジェクトのインスタンスの参照関係を保持するマップ **/
	private SchemaInstanceRelationMap		_schemaRelationMap = new SchemaInstanceRelationMap();

	/** ユーザー入力のフィルタ説明 **/
	private String	_strFilterDesc;
	
	//
	// swing model
	//

	/** フィルタ定義引数編集コンポーネント用データモデル **/
	private GenericFilterDefArgsTableModel	_defArgTableModel;

	/** フィルタ定義引数が変更されたときに呼び出されるハンドラ **/
	private IFilterArgEditHandler			_hDefArgEdit;

	/** 入力スキーマツリーのルートノード **/
	private GenericTableSchemaRootNode		_inputSchemaTreeRootNode;
	/** 出力スキーマツリーのルートノード **/
	private GenericTableSchemaRootNode		_outputSchemaTreeRootNode;
	/** 入力スキーマツリーのツリーモデル **/
	private GenericSchemaElementTreeModel		_inputSchemaTreeModel;
	/** 出力スキーマツリーのツリーモデル **/
	private GenericSchemaElementTreeModel		_outputSchemaTreeModel;
	/** 計算式スキーマのテーブルモデル **/
	private GenericExpressionSchemaTableModel	_expSchemaTableModel;
	/** 結合条件スキーマのテーブルモデル **/
	private GenericJoinCondSchemaTableModel		_joinSchemaTableModel;
	
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
	/** スキーマ定義ファイルのパス **/
	private VirtualFile			_vfSchemaPrefs;
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
	
	/**
	 * 指定されたパラメータで、このオブジェクトの新しいインスタンスを生成する。
	 * @param type				編集種別
	 * @param defaultFormatter	標準のパスフォーマッター
	 * @param rootDisplayName	ルートディレクトリの表示名
	 * @param rootDir			ルートディレクトリ
	 * @param orgFilterDir		編集元のフィルタマクロのパス、編集ではない場合は <tt>null</tt>
	 * @param workFilterDir		編集作業に使用するディレクトリのパス
	 * @throws IllegalMacroFilterPrefsException	フィルタマクロ定義データを読み込んだ場合において、解読不能の不正なデータが含まれている場合（詳細メッセージ含む）
	 */
	public GenericFilterEditModel(FilterEditModel.EditType type, VirtualFilePathFormatter defaultFormatter, String rootDisplayName,
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
		this._defEditableArgsList = new ArrayList<GenericFilterArgEditModel>();
		this._defArgTableModel = new GenericFilterDefArgsTableModel(this);
		this._expSchemaTableModel = new GenericExpressionSchemaTableModel();
		this._joinSchemaTableModel = new GenericJoinCondSchemaTableModel();
		
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
		this._vfSchemaPrefs = MExecDefFileManager.getGenericFilterDefDataFile(vfTarget);
		this._vfArgsRootDir  = vfTarget.getChildFile(MExecDefFileManager.MEXECDEF_MODULEARG_DIRNAME);
		this._vfFilesRootDir = vfTarget.getChildFile(MExecDefFileManager.MEXECDEF_FILES_ROOT_DIRNAME);
		
		// パスの更新
		updatePathFormatter();

		// 日付時刻書式の初期化
		_dtfEditModel = new SchemaDateTimeFormatEditModel();
		
		// 入出力スキーマ用ツリーモデルの生成
		_inputSchemaTreeRootNode  = new GenericTableSchemaRootNode();
		_outputSchemaTreeRootNode = new GenericTableSchemaRootNode();
		_inputSchemaTreeModel  = new GenericSchemaElementTreeModel(_inputSchemaTreeRootNode);
		_outputSchemaTreeModel = new GenericSchemaElementTreeModel(_outputSchemaTreeRootNode);
	}

	/**
	 * 現在の設定で、このモデルの定義情報をファイルから読み込む。
	 * @throws FileNotFoundException	スキーマ定義ファイルが存在しない場合
	 * @throws XMLStreamException		スキーマ定義ファイルの読み込みに失敗した場合
	 */
	public void loadPreferences() throws FileNotFoundException, XMLStreamException
	{
		// load filter prefs
		MExecDefSettings defFilter = new MExecDefSettings();
		defFilter.setPropertyTarget(_vfFilterPrefs);
		if (_vfFilterPrefs.exists()) {
			//--- モジュール定義ファイルが存在する場合は、読み込み
			defFilter.loadForTarget(_vfFilterPrefs);
			setupFilterArgsFromMExecDefSettings(defFilter);
		}
		this._defModuleData.setData(defFilter);
		
		// load schema definitions
		if (_vfSchemaPrefs.exists()) {
			//--- スキーマ定義ファイルが存在する場合は、読み込み
			SchemaConfig sconfig = null;
			InputStream iStream = null;
			SchemaConfigReader reader = null;
			try {
				iStream = _vfSchemaPrefs.getInputStream();
				reader = new SchemaConfigReader(iStream);
				iStream = null;
				
				// read
				sconfig = reader.readConfig();
			}
			finally {
				if (reader != null) {
					try {
						reader.close();
						reader = null;
					} catch (Throwable ex) {
						AppLogger.warn("Failed to close SchemaConfig file : \"" + _vfSchemaPrefs + "\"", ex);
					}
				}
				if (iStream != null) {
					try {
						iStream.close();
					} catch (Throwable ex) {
						AppLogger.warn("Failed to close SchemaConfig file : \"" + _vfSchemaPrefs + "\"", ex);
					}
				}
			}
			if (sconfig != null) {
				setupModelsFromSchemaConfig(sconfig);
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 現在の日付時刻書式編集データモデルを取得する。
	 * @return	日付時刻書式編集データモデル
	 */
	public SchemaDateTimeFormatEditModel getDateTimeFormatEditModel() {
		return _dtfEditModel;
	}

	/**
	 * 現在の日付時刻書式設定に応じた日付時刻書式オブジェクトを取得する。
	 * 現在の設定がフィルタ定義引数から指定する設定の場合は、標準の日付時刻書式オブジェクトを返す。
	 * @return	現在の設定に応じた日付時刻書式オブジェクト
	 */
	public SchemaDateTimeFormats getAvailableDateTimeFormats() {
		return _dtfEditModel.getDateTimeFormats();
	}

	/**
	 * 日付時刻書式入力用フィルタ定義引数を追加する。
	 * @return	追加したフィルタ定義引数のデータモデル
	 * @since 3.2.1
	 */
	public GenericFilterArgEditModel addDateTimeFormatFilterArgument() {
		String strDesc = RunnerMessages.getInstance().GenericFilterEditDlg_name_DateTimeFormat;
		addMExecDefArgument(ModuleArgType.STR, strDesc, MExecArgString.instance);	// 実行時指定
		return getMExecDefArgument(getMExecDefArgumentCount()-1);
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
	public GenericFilterDefArgsTableModel getMExecDefArgTableModel() {
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
//		return _defModuleData.getDescription();
		return _strFilterDesc;
	}

	/**
	 * 新しいモジュール実行定義の説明を設定する。
	 * @param desc	新しい説明
	 * @return	機能説明が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean setMExecDefDescription(String desc) {
//		String oldDesc = _defModuleData.getDescription();
//		_defModuleData.setDescription(desc);
//		return Objects.isEqual(oldDesc, _defModuleData.getDescription());
		
		if (desc != null && desc.isEmpty())
			desc = null;
		if (Objects.isEqual(_strFilterDesc, desc))
			return false;
		//--- modified
		_strFilterDesc = desc;
		return true;
	}

	/**
	 * 現在のスキーマ定義に基づき、フィルタ定義ファイル用の説明を更新する。
	 */
	public void updateMExecDefDescription() {
		String strDesc = (_strFilterDesc==null ? "" : _strFilterDesc);
		
		// TODO: スキーマ定義からドキュメント生成
		
		_defModuleData.setDescription(strDesc);
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
	 * 汎用フィルタのスキーマ定義ファイルを取得する。
	 * @return	スキーマ定義ファイルの抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getGenericSchemaFile() {
		return _vfSchemaPrefs;
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
		if (vfModule == null) {
			if (_vfWorkFilterRoot != null) {
				vfModule = _vfWorkFilterRoot.getChildFile(MExecDefFileManager.GENERICFILTER_SCHEMA_FILENAME);
			} else if (_vfRootDir != null) {
				vfModule = _vfRootDir.getChildFile(MExecDefFileManager.GENERICFILTER_SCHEMA_FILENAME);
			}
		}
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
		return ModuleFileType.GENERIC_FILTER;
	}

	/**
	 * このモデルのモジュール実行定義メインクラス名を返す。
	 * @return <code>GenericFilterMain</code> クラスのクラス名
	 */
	public String getMExecDefMainClass() {
		return GenericFilterMain.class.getName();
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

//	/**
//	 * 指定されたサブフィルタ引数が参照可能なフィルタ定義引数が存在する場合に <tt>true</tt> を返す。
//	 * @param subFilterArgData	判定するサブフィルタの引数
//	 * @return	参照可能引数が存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	public boolean hasReferenceableMExecDefArguments(IModuleArgConfig subFilterArgData) {
//		if (subFilterArgData == null)
//			return false;
//		if (subFilterArgData.isFixedValue() || subFilterArgData.getParameterType()==null)
//			return false;	// 引数に固定値が設定されている場合は、参照不可
//		if (_defArgsList.isEmpty())
//			return false;	// フィルタ定義引数が設定されていない場合は、参照不可
//
//		ModuleArgType targetArgType = subFilterArgData.getType();
//		int numArgs = _defArgsList.size();
//		for (int index = 0; index < numArgs; index++) {
//			// 属性が一致するもののみ
//			FilterArgEditModel defarg = _defArgsList.get(index);
//			if (targetArgType == defarg.getType()) {
//				// 参照可能引数あり
//				return true;
//			}
//		}
//		
//		// 参照可能引数なし
//		return false;
//	}

//	/**
//	 * 指定されたサブフィルタ引数が参照可能なフィルタ定義引数のみを保持する、リストコンポーネント用データモデルを返す。
//	 * このデータモデルに格納されるデータは、<code>ModuleArgConfig</code> オブジェクトとなる。
//	 * @param subFilterArgData	判定する引数
//	 * @return	参照可能な引数を保持する <code>ListModel</code> オブジェクトを返す。
//	 * 			参照可能な引数が存在しない場合、要素が空の <code>ListModel</code> オブジェクトを返す。
//	 */
//	public DefaultListModel getReferenceableMExecDefArguments(IModuleArgConfig subFilterArgData) {
//		DefaultListModel listmodel = new DefaultListModel();
//		
//		if (subFilterArgData == null)
//			return listmodel;
//		if (subFilterArgData.isFixedValue() || subFilterArgData.getParameterType()==null)
//			return listmodel;	// 引数に固定値が設定されている場合は、参照不可
//		if (_defArgsList.isEmpty())
//			return listmodel;	// フィルタ定義引数が設定されていない場合は、参照不可
//
//		ModuleArgType targetArgType = subFilterArgData.getType();
//		int numArgs = _defArgsList.size();
//		for (int index = 0; index < numArgs; index++) {
//			// 属性が一致するもののみ
//			FilterArgEditModel defarg = _defArgsList.get(index);
//			if (targetArgType == defarg.getType()) {
//				// 参照可能引数あり
//				listmodel.addElement(defarg);
//			}
//		}
//		
//		return listmodel;
//	}

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
	 * 計算式等で選択可能なフィルタ定義引数のリストを返す。
	 * このリストを直接更新した場合、他のデータモデルへは通知されない。
	 * @return	編集可能と位置付けられたフィルタ定義引数のリスト
	 */
	public List<GenericFilterArgEditModel> getEditableFilterArgsList() {
		return _defEditableArgsList;
	}

	/**
	 * フィルタ定義引数をすべて削除する。
	 * エラーチェックは行わない。
	 */
	public void clearMExecDefArguments() {
		_defArgsList.clear();
		_defEditableArgsList.clear();
	}

	/**
	 * フィルタ定義引数が存在しない場合に <tt>true</tt> を返す。
	 */
	public boolean isMExecDefArgumentEmpty() {
		return _defArgsList.isEmpty();
	}

	/**
	 * フィルタ定義引数のオブジェクトが登録されているかどうかを判定する。
	 * @param argment	判定するオブジェクト
	 * @return	登録されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean containsMExecDefArgument(Object argment) {
		return _defArgsList.contains(argment);
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
	public GenericFilterArgEditModel getMExecDefArgument(int index) {
		return (GenericFilterArgEditModel)_defArgsList.get(index);
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
	 * フィルタ定義引数のリストから、指定されたインデックスに格納されているオブジェクトのデータ型を返す。
	 * @param index	引数のインデックス
	 * @return	引数に設定されたデータ型
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public SchemaValueType getMExecDefArgumentDataType(int index) {
		return getMExecDefArgument(index).getValueType();
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
	 * フィルタ定義引数のインデックスに対応する位置に、指定されたデータ型を設定する。
	 * @param index	引数のインデックス
	 * @param newValueType	新しいデータ型
	 * @return	データ型が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
	 */
	public boolean setMExecDefArgumentDataType(int index, SchemaValueType newValueType) {
		if (getMExecDefArgument(index).updateValueType(newValueType)) {
			// modified
			_defArgTableModel.fireArgumentDataTypeUpdated(index);
			return true;
		} else {
			// no changes
			return false;
		}
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
	 * @param type		引数属性
	 * @param dataType	引数のデータ型
	 * @param desc		引数説明
	 * @param value		引数値
	 */
	public void addMExecDefArgument(final ModuleArgType type, final SchemaValueType dataType, final String desc, final Object value) {
		insertMExecDefArgument(_defArgsList.size(), type, dataType, desc, value);
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
		GenericFilterArgEditModel arg = new GenericFilterArgEditModel(index+1, type, desc, value);
		if (value instanceof IMExecArgParam) {
			arg.setValue((IMExecArgParam)value);
		}
		insertMExecDefArgument(index, arg);
	}
	
	/**
	 * フィルタ定義引数リストの指定された位置に、指定された引数データを挿入する。
	 * @param index		挿入位置のインデックス、もしくはフィルタ定義引数リストの要素数
	 * @param type		引数属性
	 * @param dataType	引数のデータ型
	 * @param desc		引数説明
	 * @param value		引数値
	 */
	public void insertMExecDefArgument(int index, final ModuleArgType type, final SchemaValueType dataType, final String desc, final Object value) {
		GenericFilterArgEditModel arg = new GenericFilterArgEditModel(index+1, type, desc, value);
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
		if (!(newDefArg instanceof GenericFilterArgEditModel))
			throw new IllegalArgumentException("'newDefArg' object is not GenericFilterArgEditModel class.");
		GenericFilterArgEditModel argModel = (GenericFilterArgEditModel)newDefArg;
		_defArgsList.add(index, argModel);
		if (isEditableMExecDefArgument(argModel)) {
			// 編集可能な引数のリストへの追加
			if (_defEditableArgsList.isEmpty()) {
				_defEditableArgsList.add(argModel);
			} else {
				int editableIndex;
				for (editableIndex = _defEditableArgsList.size() - 1; editableIndex >= 0; --editableIndex) {
					GenericFilterArgEditModel editableArgModel = _defEditableArgsList.get(editableIndex);
					if (editableArgModel.getArgNo() <= index) {
						break;
					}
				}
				_defEditableArgsList.add(editableIndex+1, argModel);
			}
		}
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
			//--- 編集可能フィルタ定義引数リストから削除
			_defEditableArgsList.remove((GenericFilterArgEditModel)removed);
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
					//--- 引数番号の更新通知
					for (int argIndex = firstArgIndex; argIndex <= lastArgIndex; ++argIndex) {
						GenericFilterArgEditModel argModel = (GenericFilterArgEditModel)_defArgsList.get(argIndex);
						notifyPrecedentDataChanged(argModel);
					}
				}
			}
		}
		return (removed != null);
	}

	//------------------------------------------------------------
	// Public interfaces for Generic Filter
	//------------------------------------------------------------

	/**
	 * 実行時のデータ型マネージャオブジェクトを取得する。
	 * @return	実行時データ型マネージャオブジェクト
	 * @since 3.2.1
	 */
	public RuntimeSchemaValueTypeManager getSchemaValueTypeManager() {
		return _schemaValueTypeManager;
	}

	/**
	 * 指定されたフィルタ定義引数編集モデルが、日付時刻書式入力用引数かどうかを判定する。
	 * @param argModel	判定対象のフィルタ定義引数編集モデル
	 * @return	日付時刻書式入力用引数なら <tt>true</tt>
	 * @since 3.2.1
	 */
	public boolean isDateTimeFormatMExecDefArgument(final GenericFilterArgEditModel argModel) {
		return (argModel != null && argModel == _dtfEditModel.getFilterArgumentModel());
	}

	/**
	 * 指定されたフィルタ定義引数編集モデルが、定義編集可能な引数かどうかを判定する。
	 * <p>なお、この編集モデルが編集中かどうかは考慮しない。
	 * @param argModel	判定対象のフィルタ定義引数編集モデル
	 * @return	定義編集可能なら <tt>true</tt>
	 * @since 3.2.1
	 */
	public boolean isEditableMExecDefArgument(final GenericFilterArgEditModel argModel) {
		if (argModel == null)
			return false;	// 存在しないので、編集不可
		if (argModel.getType() != ModuleArgType.STR)
			return false;	// [STR] 引数ではないので、編集不可
		if (isDateTimeFormatMExecDefArgument(argModel))
			return false;	// 日付時刻書式入力用引数なので、編集不可
		
		// 編集可能
		return true;
	}

	/**
	 * 指定された位置のフィルタ定義引数編集モデルが、定義編集可能な引数かどうかを判定する。
	 * <p>なお、この編集モデルが編集中かどうかは考慮しない。
	 * @param argIndex	判定対象の引数の位置を示すインデックス
	 * @return	定義編集可能なら <tt>true</tt>
	 * @throws IndexOutOfBoundsException	インデックスが範囲外の場合
	 * @since 3.2.1
	 */
	public boolean isEditableMExecDefArgument(int argIndex) {
		return isEditableMExecDefArgument(getMExecDefArgument(argIndex));
	}

	//------------------------------------------------------------
	// Public interfaces for Input/Output Schema
	//------------------------------------------------------------
	
	static protected final String DEFAULT_INPUT_SCHEMA_DESC_FORMAT	= "Input File %d (CSV)";
	static protected final String DEFAULT_OUTPUT_SCHEMA_DESC_FORMAT	= "Output File %d (CSV)";
	
	public String getDefaultNewInputTableSchemaDescription() {
		return getDefaultNewTableSchemaDescription(_inputSchemaTreeRootNode, DEFAULT_INPUT_SCHEMA_DESC_FORMAT);
	}
	
	public String getDefaultNewOutputTableSchemaDescription() {
		return getDefaultNewTableSchemaDescription(_outputSchemaTreeRootNode, DEFAULT_OUTPUT_SCHEMA_DESC_FORMAT);
	}
	
	public GenericSchemaElementTreeModel getInputTableSchemaTreeModel() {
		return _inputSchemaTreeModel;
	}
	
	public GenericSchemaElementTreeModel getOutputTableSchemaTreeModel() {
		return _outputSchemaTreeModel;
	}
	
	public GenericTableSchemaRootNode getInputTableSchemaRootNode() {
		return _inputSchemaTreeRootNode;
	}
	
	public GenericTableSchemaRootNode getOutputTableSchemaRootNode() {
		return _outputSchemaTreeRootNode;
	}
	
	public int getInputTableSchemaCount() {
		return _inputSchemaTreeRootNode.getChildCount();
	}
	
	public int getOutputTableSchemaCount() {
		return _outputSchemaTreeRootNode.getChildCount();
	}
	
	public GenericTableSchemaTreeNode getInputTableSchemaTreeNode(int index) {
		return (GenericTableSchemaTreeNode)_inputSchemaTreeRootNode.getChildAt(index);
	}
	
	public GenericTableSchemaTreeNode getOutputTableSchemaTreeNode(int index) {
		return (GenericTableSchemaTreeNode)_outputSchemaTreeRootNode.getChildAt(index);
	}
	
	protected String getDefaultNewTableSchemaDescription(GenericTableSchemaRootNode node, String format) {
		// 対象のテーブル数から説明を生成
		int cnt = node.getChildCount();
		String desc = String.format(format, cnt + 1);
		if (cnt == 0) {
			return desc;	// 最初のテーブル
		}
		
		// スキーマ説明の集合を生成し、重複しない説明を生成
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < cnt; ++i) {
			String str = ((GenericTableSchemaTreeNode)node.getChildAt(i)).getUserObject().getDescription();
			if (str != null && !str.isEmpty()) {
				set.add(str.toLowerCase());
			}
		}
		if (!set.contains(desc.toLowerCase())) {
			return desc;
		}
		int lim = cnt * 2 + 2;	// 現在の入力テーブル数の倍 + 2 を上限
		for (int i = cnt + 2; i < lim; ++i) {
			desc = String.format(format, i);
			if (!set.contains(desc.toLowerCase())) {
				return desc;
			}
		}
		//--- null を返して終了
		return null;
	}
	
//	/**
//	 * 現在の出力フィールドを、コンボボックスインデックスにより取得する。
//	 * このインデックスは、全テーブルを通してのインデックスとなる。
//	 * @param index	コンボボックスのインデックス
//	 * @return	インデックスに対応する出力フィールドオブジェクト、それ以外の場合は <tt>null</tt>
//	 */
//	public OutputCsvFieldSchemaEditModel getOutputSchemaFieldComboBoxItem(int index) {
//		if (index < 0)
//			return null;
//		
//		int len = _outputSchemaTreeRootNode.getChildCount();
//		for (int i = 0; i < len; ++i) {
//			GenericTableSchemaTreeNode node = (GenericTableSchemaTreeNode)_outputSchemaTreeRootNode.getChildAt(i);
//			int cc = node.getChildCount();
//			if (index < cc) {
//				return (OutputCsvFieldSchemaEditModel)((GenericSchemaElementTreeNode)node.getChildAt(index)).getUserObject();
//			}
//			index -= cc;
//		}
//		
//		return null;	// not found
//	}

	/**
	 * 現在の入力スキーマの全フィールドの総数を取得する。
	 * @return	入力スキーマの全フィールドの総数
	 */
	public int getInputSchemaTotalFieldCount() {
		int numTotalFields = 0;
		int numTables = _inputSchemaTreeRootNode.getChildCount();
		for (int index = 0; index < numTables; ++index) {
			numTotalFields += _inputSchemaTreeRootNode.getChildAt(index).getChildCount();
		}
		return numTotalFields;
	}

	/**
	 * 現在の入力フィールドを、コンボボックスインデックスにより取得する。
	 * このインデックスは、全テーブルを通してのインデックスとなる。
	 * @param index	コンボボックスのインデックス
	 * @return	インデックスに対応する入力フィールドオブジェクト、それ以外の場合は <tt>null</tt>
	 */
	public InputCsvFieldSchemaEditModel getInputSchemaFieldComboBoxItem(int index) {
		if (index < 0)
			return null;
		
		int len = _inputSchemaTreeRootNode.getChildCount();
		for (int i = 0; i < len; ++i) {
			GenericTableSchemaTreeNode node = (GenericTableSchemaTreeNode)_inputSchemaTreeRootNode.getChildAt(i);
			int cc = node.getChildCount();
			if (index < cc) {
				return (InputCsvFieldSchemaEditModel)((GenericSchemaElementTreeNode)node.getChildAt(index)).getUserObject();
			}
			index -= cc;
		}
		
		return null;	// not found
	}

	/**
	 * 新規作成された入力テーブルスキーマを、入力スキーマの終端に追加する。
	 * @param newDataModel	新たに生成されたデータモデル
	 * @return	追加された入力テーブルスキーマのツリーノードの位置を示すツリーパス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>newDataModel</em> の要素が空の場合
	 */
	public TreePath addInputTableSchema(InputCsvTableSchemaEditModel newDataModel)
	{
		if (newDataModel.isEmpty())
			throw new IllegalArgumentException("New data model has no fields.");
		
		// ツリーノードを生成
		GenericTableSchemaTreeNode tableNode = new GenericTableSchemaTreeNode(newDataModel);
		int numFields = newDataModel.size();
		for (int index = 0; index < numFields; ++index) {
			InputCsvFieldSchemaEditModel fieldModel = (InputCsvFieldSchemaEditModel)newDataModel.get(index);
			fieldModel.setParentObject(newDataModel);
			fieldModel.setElementNo(index+1);
			GenericSchemaElementTreeNode node = new GenericSchemaElementTreeNode(fieldModel);
			tableNode.add(node);
		}
		//--- 多重管理とならないよう、テーブルモデルから全フィールドを削除しておく（ツリーモデルには構造コピー済）
		newDataModel.removeAllElements();	// リストの要素のみをクリア
		
		// 新しい入力テーブル用フィルタ定義引数を追加
		int argIndex = getMExecDefArgumentCount();
		addMExecDefArgument(ModuleArgType.IN, newDataModel.getDescription(), MExecArgCsvFile.instance);	// 実行時指定 CSV
		GenericFilterArgEditModel argModel = getMExecDefArgument(argIndex);
		newDataModel.setFilterArgModel(argModel);
		newDataModel.setDescription(null);
		_schemaRelationMap.setReference(newDataModel, argModel);
		
		// ルートノードへ追加し、ツリーを更新
		int nodeIndex = _inputSchemaTreeRootNode.getChildCount();
		_inputSchemaTreeRootNode.add(tableNode);
		//--- ツリーモデルの更新通知
		_inputSchemaTreeModel.nodesWereInserted(_inputSchemaTreeRootNode, new int[]{nodeIndex});
		
		// 完了
		return new TreePath(tableNode.getPath());
	}

	/**
	 * 指定された入力スキーマツリーのテーブルノードが持つデータモデルを、新しいデータモデルの内容に更新する。
	 * このメソッドでは、既存のインスタンスはそのまま利用し、オブジェクトの内容のみを更新する。
	 * 削除されたフィールドについては、参照関係も破棄する。
	 * @param orgTableNode	既存の入力テーブルスキーマのツリーノード
	 * @param newTableModel	新しい入力テーブルスキーマのデータモデル(フィールドの要素を持つ)
	 * @param removedOrgFields	削除されたフィールドインスタンスのリスト
	 * @return	更新された入力テーブルスキーマのツリーノードへのパス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public TreePath updateInputTableSchema(GenericTableSchemaTreeNode orgTableNode, InputCsvTableSchemaEditModel newTableModel,
											ArrayList<InputCsvFieldSchemaEditModel>	removedOrgFields)
	{
		// 既存のテーブルノードの子ノードを破棄
		removeAllFieldsFromInputTableSchema(orgTableNode, false);	// 参照関係は破棄しない
		
		// 削除されたフィールドの参照関係を破棄
		for (InputCsvFieldSchemaEditModel removedField : removedOrgFields) {
			if (_schemaRelationMap.containsPrecedent(removedField)) {
				removePrecedentDataFromAllDependents(removedField);
			}
		}
		
		// テーブルモデルの更新
		InputCsvTableSchemaEditModel orgTableModel = (InputCsvTableSchemaEditModel)orgTableNode.getUserObject();
		//--- 新テーブルモデルの内容を反映
		GenericFilterArgEditModel argModel = orgTableModel.getFilterArgModel();
		setMExecDefArgumentDescription(argModel.getArgNo()-1, newTableModel.getDescription());
		orgTableModel.setDescription(null);
		orgTableModel.setHeaderRecordCount(newTableModel.getHeaderRecordCount());
		orgTableModel.setBaseCsvData(newTableModel.getBaseCsvData());
		//--- 旧テーブルモデルの内容をクリア(念のため)
		newTableModel.setFilterArgModel(null);
		newTableModel.setBaseCsvData(null);
		newTableModel.setParentObject(null);
		
		// 新しいフィールドノードの追加
		int numNewChildren = newTableModel.size();
		for (int index = 0; index < numNewChildren; ++index) {
			InputCsvFieldSchemaEditModel newFieldModel = (InputCsvFieldSchemaEditModel)newTableModel.get(index);
			if (newFieldModel.hasOriginalSchema()) {
				//--- オリジナルフィールドオブジェクトの内容を、新しいフィールドオブジェクトの内容で更新
				InputCsvFieldSchemaEditModel orgFieldModel = newFieldModel.getOriginalSchema();
				orgFieldModel.setFieldName(newFieldModel.getFieldName());
				orgFieldModel.setDescription(newFieldModel.getDescription());
				orgFieldModel.setHeaderValues(newFieldModel.getHeaderValues());
				orgFieldModel.setValueType(newFieldModel.getValueType());
				orgFieldModel.setOriginalSchema(null);
				//--- 新しいフィールドオブジェクトをクリア
				newFieldModel.setParentObject(null);
				newFieldModel.setOriginalSchema(null);
				//--- 処理対象を、オリジナルフィールドオブジェクトとする
				newFieldModel = orgFieldModel;
				//--- この処理により、参照関係の変更は不要だが、更新は必要
			}
			//--- 既存のテーブルモデルへ付け替え
			newFieldModel.setElementNo(index+1);
			newFieldModel.setParentObject(orgTableModel);
			//--- ツリーへ追加
			GenericSchemaElementTreeNode newFieldNode = new GenericSchemaElementTreeNode(newFieldModel);
			orgTableNode.add(newFieldNode);

			// 参照関係に基づく、表示の更新
			if (_schemaRelationMap.containsPrecedent(newFieldModel)) {
				notifyPrecedentDataChanged(newFieldModel);
			}
		}
		//--- 新しいテーブルモデルをクリア
		newTableModel.clear();

		// ツリーを更新
		_inputSchemaTreeModel.reload(orgTableNode);
		_inputSchemaTreeModel.nodeChanged(orgTableNode);
		
		// 完了
		return new TreePath(orgTableNode.getPath());
	}

	/**
	 * 指定された入力スキーマツリーのテーブルノードが持つ、すべてのフィールドを削除する。
	 * このメソッドの呼び出しでは、<em>withRemoveReference</em> に <tt>true</tt> が指定された場合、入力フィールドを参照する要素との参照関係を削除する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、ツリーに対して変更通知を行わない。
	 * </blockquote>
	 * @param tableNode	対象の入力テーブルスキーマツリーノード
	 * @param withRemoveReference	削除したフィールドの参照関係も破棄する場合は <tt>true</tt>
	 * @return	削除したフィールド数
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected int removeAllFieldsFromInputTableSchema(GenericTableSchemaTreeNode tableNode, boolean withRemoveReference) {
		int numChildren = tableNode.getChildCount();
		if (numChildren <= 0)
			return 0;	// no children
		
		// フィールドを削除し、要求に応じて参照関係を破棄
		for (int index = 0; index < numChildren; ++index) {
			GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)tableNode.getChildAt(index);
			InputCsvFieldSchemaEditModel fieldModel = (InputCsvFieldSchemaEditModel)fieldNode.getUserObject();
			if (withRemoveReference && _schemaRelationMap.containsPrecedent(fieldModel)) {
				// 入力フィールドを参照元から除去
				removePrecedentDataFromAllDependents(fieldModel);
			}
			fieldModel.setParentObject(null);
			fieldNode.setUserObject(null);
		}
		tableNode.removeAllChildren();
		
		// 削除したフィールド数を返す
		return numChildren;
	}

	/**
	 * 指定された入力スキーマツリーのテーブルノードが持つ、すべてのフィールドを削除し、ツリーを更新する。
	 * このメソッド呼び出しでは、入力フィールドを参照する要素との参照関係も削除する。
	 * @param tableNode	対象の入力テーブルスキーマツリーノード
	 * @return	フィールドが一つでも削除された場合は <tt>true</tt>、削除されなかった場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean deleteAllFieldsFromInputTableSchema(GenericTableSchemaTreeNode tableNode) {
		int numChildren = removeAllFieldsFromInputTableSchema(tableNode, true);	// 参照関係も破棄
		if (numChildren <= 0)
			return false;	// no children
		
		// ツリーへ反映
		_inputSchemaTreeModel.reload(tableNode);
		return true;
	}

	/**
	 * 指定された入力スキーマツリーのテーブルノードを削除し、ツリーを更新する。
	 * このメソッド呼び出しでは、入力テーブルに割り当てられたフィルタ定義引数も削除する。
	 * @param tableNode	削除対象の入力テーブルツリーノード
	 * @return	次に削除可能なテーブルノードへのツリーパス、テーブルノードが存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public TreePath deleteInputTableSchema(GenericTableSchemaTreeNode tableNode) {
		// フィールドを削除し、参照関係を破棄
		deleteAllFieldsFromInputTableSchema(tableNode);
		
		// フィルタ定義引数の削除
		InputCsvTableSchemaEditModel tableModel = (InputCsvTableSchemaEditModel)tableNode.getUserObject();
		GenericFilterArgEditModel argModel = (GenericFilterArgEditModel)tableModel.getFilterArgModel();
		tableModel.setFilterArgModel(null);
		_schemaRelationMap.removePrecedent(argModel);	// 入力引数は入力テーブルスキーマしか参照していないので、通知不要
		//--- フィルタ定義引数の削除
		int argIndex = argModel.getArgNo() - 1;
		removeMExecDefArgument(argIndex);	// 更新通知も含む
		
		// ツリーへ反映
		int childIndex = _inputSchemaTreeRootNode.getIndex(tableNode);
		_inputSchemaTreeRootNode.remove(childIndex);
		_inputSchemaTreeModel.nodesWereRemoved(_inputSchemaTreeRootNode, new int[]{childIndex}, new Object[]{tableNode});
		
		// 次に選択するツリーノードを取得
		int numChildren = _inputSchemaTreeRootNode.getChildCount();
		if (childIndex < numChildren) {
			return new TreePath(((GenericTableSchemaTreeNode)_inputSchemaTreeRootNode.getChildAt(childIndex)).getPath());
		} else if (numChildren > 0) {
			return new TreePath(((GenericTableSchemaTreeNode)_inputSchemaTreeRootNode.getChildAt(numChildren - 1)).getPath());
		} else {
			return null;
		}
	}

	/**
	 * 新規作成された出力テーブルスキーマを、ツリーの終端に追加する。
	 * このメソッドでは、指定されたインスタンスをそのまま使用し、ツリーのみで管理するように修正する。
	 * @param targetData	追加する出力テーブルスキーマの編集データモデル
	 * @return	追加したテーブルのツリーパス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 3.2.1
	 */
	public TreePath attachOutputTableSchema(OutputCsvTableSchemaEditModel targetData)
	{
		// テーブルのツリーノードを生成
		int numFields = targetData.size();
		GenericTableSchemaTreeNode tableNode = new GenericTableSchemaTreeNode(targetData);
		//--- フィルタ定義引数の追加
		addMExecDefArgument(ModuleArgType.OUT, targetData.getDescription(), MExecArgCsvFile.instance);	// 実行時指定 CSV
		GenericFilterArgEditModel argModel = getMExecDefArgument(getMExecDefArgumentCount()-1);
		targetData.setFilterArgModel(argModel);
		targetData.setDescription(null);
		//--- フィルタ定義引数とテーブルスキーマとの参照関係設定
		_schemaRelationMap.setReference(targetData, argModel);
		//--- ツリーへ反映
		GenericTableSchemaRootNode rootNode = _outputSchemaTreeRootNode;
		rootNode.add(tableNode);
		_outputSchemaTreeModel.nodesWereInserted(rootNode, new int[]{rootNode.getChildCount()-1});
		
		// テーブルノードへ要素を追加
		int[] addedIndices = new int[numFields];
		for (int index = 0; index < numFields; ++index) {
			OutputCsvFieldSchemaEditModel fieldData = (OutputCsvFieldSchemaEditModel)targetData.get(index);
			//--- 関連情報を更新
			fieldData.setParentObject(targetData);
			fieldData.setElementNo(index+1);
			//--- 参照オブジェクトがあれば、参照関係を保持
			SchemaElementValue targetValue = fieldData.getTargetValue();
			if (targetValue != null) {
				_schemaRelationMap.setReference(fieldData, targetValue);
			}
			//--- ツリーノード生成
			GenericSchemaElementTreeNode fieldNode = new GenericSchemaElementTreeNode(fieldData);
			//--- テーブルノードへ追加
			tableNode.add(fieldNode);
			addedIndices[index] = index;
			//--- verify
			fieldNode.verify();
		}
		//--- テーブルデータからフィールドデータを除去(ツリーで管理するため)
		targetData.clear();
		//--- テーブルノードのツリーへ反映
		_outputSchemaTreeModel.nodesWereInserted(tableNode, addedIndices);
		
		//--- テーブルノードのエラーチェック
		boolean oldError = tableNode.hasError();
		boolean newError = !tableNode.verify();
		if (newError || oldError) {
			_outputSchemaTreeModel.nodeChanged(tableNode);
		}

		// テーブルノードのパスを返す
		return new TreePath(tableNode.getPath());
	}

	/**
	 * 指定された参照オブジェクトを出力値とするフィールドを、ツリーの指定された位置に追加する。
	 * ツリーの位置が指定されない場合、終端の出力テーブルスキーマに追加する。
	 * なお、出力テーブルスキーマが一つも存在しない場合は、新しい出力テーブルスキーマを作成する。
	 * @param targetData	出力値のコレクション
	 * @param insertTreePos	フィールド挿入位置を示すツリーパス、終端に追加する場合は <tt>null</tt>
	 * @return	追加されたフィールドの先頭位置を示すツリーパス、追加されなかった場合は <tt>null</tt>
	 * @throws NullPointerException	<em>targetData</em> が <tt>null</tt> の場合
	 */
	public TreePath addOutputTableSchema(Collection<? extends SchemaElementValue> targetData, TreePath insertTreePos)
	{
		// check
		if (targetData.isEmpty())
			return null;
		
		// 追加基準位置の判定
		GenericTableSchemaTreeNode targetNode;
		OutputCsvTableSchemaEditModel targetTable;
		int addingStartChildIndex;
		if (insertTreePos == null) {
			// 最終位置に追加
			int numTables = _outputSchemaTreeRootNode.getChildCount();
			if (numTables == 0) {
				// テーブルを新規作成
				addNewOutputTable();
				numTables = _outputSchemaTreeRootNode.getChildCount();
			}
			targetNode = (GenericTableSchemaTreeNode)_outputSchemaTreeRootNode.getChildAt(numTables-1);
			targetTable = (OutputCsvTableSchemaEditModel)targetNode.getUserObject();
			addingStartChildIndex = targetNode.getChildCount();
		}
		else {
			// 指定された位置の次の位置に追加
			Object obj = insertTreePos.getLastPathComponent();
			if (obj instanceof GenericTableSchemaTreeNode) {
				//--- テーブルの先頭に追加
				targetNode = (GenericTableSchemaTreeNode)obj;
				targetTable = (OutputCsvTableSchemaEditModel)targetNode.getUserObject();
				addingStartChildIndex = 0;	// 先頭
			}
			else {
				//--- 要素の次に追加
				GenericSchemaElementTreeNode node = (GenericSchemaElementTreeNode)obj;
				targetNode = (GenericTableSchemaTreeNode)node.getParent();
				targetTable = (OutputCsvTableSchemaEditModel)targetNode.getUserObject();
				addingStartChildIndex = targetNode.getIndex(node) + 1;
			}
		}
		
		// 追加
		int cnt = 0;
		int[] addedIndices = new int[targetData.size()];
		int insertPos = addingStartChildIndex;
		for (SchemaElementValue elem : targetData) {
			//--- 追加する要素を生成
			OutputCsvFieldSchemaEditModel outfield = new OutputCsvFieldSchemaEditModel();
			outfield.setTargetValue(elem);
			outfield.setParentObject(targetTable);
			outfield.setElementNo(insertPos + 1);
			_schemaRelationMap.setReference(outfield, elem);
			GenericSchemaElementTreeNode outnode = new GenericSchemaElementTreeNode(outfield);
			//--- 指定位置へ挿入
			targetNode.insert(outnode, insertPos);
			addedIndices[cnt] = insertPos;
			++insertPos;
			++cnt;
			//--- verify
			outnode.verify();
		}
		//--- ツリーモデルを更新
		_outputSchemaTreeModel.nodesWereInserted(targetNode, addedIndices);
		//--- 親テーブルノードのエラーチェック
		boolean oldError = targetNode.hasError();
		boolean newError = !targetNode.verify();
		if (newError || oldError) {
			_outputSchemaTreeModel.nodeChanged(targetNode);
		}
		//--- 最初に追加されたフィールドを選択
		GenericSchemaElementTreeNode addedFirstNode = (GenericSchemaElementTreeNode)targetNode.getChildAt(addingStartChildIndex);
		return new TreePath(addedFirstNode.getPath());
	}

	/**
	 * フィールドが空の新規出力テーブルスキーマを、ツリーに追加する。
	 * 出力テーブルスキーマに対応するフィルタ定義引数も新規に作成する。
	 * @return	新しい出力テーブルスキーマのツリーノードの位置を示すツリーパス
	 */
	public TreePath addNewOutputTable() {
		// 新しいデータモデルを生成
		OutputCsvTableSchemaEditModel newTableModel = new OutputCsvTableSchemaEditModel();
		String defaultDesc = getDefaultNewOutputTableSchemaDescription();
		newTableModel.setAutoHeaderRecordEnabled(true);	// ヘッダー行自動生成をデフォルト
		//--- ツリーへ反映
		GenericTableSchemaRootNode rootNode = _outputSchemaTreeRootNode;
		GenericTableSchemaTreeNode tableNode = new GenericTableSchemaTreeNode(newTableModel);
		rootNode.add(tableNode);
		//--- フィルタ定義引数の追加
		addMExecDefArgument(ModuleArgType.OUT, defaultDesc, MExecArgCsvFile.instance);	// 実行時指定 CSV
		GenericFilterArgEditModel argModel = getMExecDefArgument(getMExecDefArgumentCount()-1);
		newTableModel.setFilterArgModel(argModel);
		_schemaRelationMap.setReference(newTableModel, argModel);
		//--- ツリーモデルの更新
		newTableModel.verify();
		_outputSchemaTreeModel.nodesWereInserted(rootNode, new int[]{rootNode.getChildCount()-1});
		//--- 生成されたノードへのツリーパスを返す
		return new TreePath(tableNode.getPath());
	}

	/**
	 * 指定された出力スキーマツリーのテーブルノードが持つデータモデルを、新しいデータモデルに置き換える。
	 * このメソッドでは、既存のテーブルノードの子ノードを削除(参照関係も破棄)する。
	 * その後、新しいデータモデルの要素をテーブルノードの子ノードとして追加し、参照関係を更新する。
	 * @param orgTableNode	既存の出力テーブルスキーマのツリーノード
	 * @param newTableModel	新しい出力テーブルスキーマのデータモデル(フィールドの要素を持つ)
	 * @return	置き換えられた出力テーブルスキーマのツリーノードへのパス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public TreePath replaceOutputTableSchema(GenericTableSchemaTreeNode orgTableNode, OutputCsvTableSchemaEditModel newTableModel) {
		// 既存のテーブルノードの子ノードを破棄
		removeAllFieldsFromOutputTableSchema(orgTableNode);
		
		// テーブルモデルの置換
		OutputCsvTableSchemaEditModel oldTableModel = (OutputCsvTableSchemaEditModel)orgTableNode.getUserObject();
		//--- フィルタ定義引数との参照関係を破棄
		GenericFilterArgEditModel argModel = oldTableModel.getFilterArgModel();
		_schemaRelationMap.removeReference(oldTableModel, argModel);
		oldTableModel.setFilterArgModel(null);
		//--- 新しいテーブルモデルをセット
		newTableModel.setFilterArgModel(argModel);
		_schemaRelationMap.setReference(newTableModel, argModel);
		setMExecDefArgumentDescription(argModel.getArgNo()-1, newTableModel.getDescription());
		newTableModel.setDescription(null);
		orgTableNode.setUserObject(newTableModel);
		
		// フィールドノードの追加
		int numNewChildren = newTableModel.size();
		for (int index = 0; index < numNewChildren; ++index) {
			OutputCsvFieldSchemaEditModel newFieldModel = (OutputCsvFieldSchemaEditModel)newTableModel.get(index);
			if (newFieldModel.hasTargetValue()) {
				// 出力値の参照を登録
				_schemaRelationMap.setReference(newFieldModel, newFieldModel.getTargetValue());
			}
			newFieldModel.setElementNo(index+1);
			newFieldModel.setParentObject(newTableModel);
			//--- 追加
			GenericSchemaElementTreeNode newFieldNode = new GenericSchemaElementTreeNode(newFieldModel);
			orgTableNode.add(newFieldNode);
			//--- verify
			newFieldNode.verify();
		}
		newTableModel.clear();

		// ツリーを更新
		orgTableNode.verify();
		_outputSchemaTreeModel.reload(orgTableNode);
		_outputSchemaTreeModel.nodeChanged(orgTableNode);
		
		// 完了
		return new TreePath(orgTableNode.getPath());
	}

	/**
	 * 指定された出力スキーマツリーのテーブルノードが持つ、すべてのフィールドを削除する。
	 * このメソッドの呼び出しでは、出力フィールドの出力値との参照関係を削除する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、ツリーに対して変更通知を行わない。
	 * </blockquote>
	 * @param tableNode	対象の出力テーブルスキーマツリーノード
	 * @return	削除したフィールド数
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected int removeAllFieldsFromOutputTableSchema(GenericTableSchemaTreeNode tableNode) {
		int numChildren = tableNode.getChildCount();
		if (numChildren <= 0)
			return 0;	// no children
		
		// フィールドを削除し、参照関係を破棄
		for (int index = 0; index < numChildren; ++index) {
			GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)tableNode.getChildAt(index);
			OutputCsvFieldSchemaEditModel fieldModel = (OutputCsvFieldSchemaEditModel)fieldNode.getUserObject();
			if (fieldModel.hasTargetValue()) {
				// 出力値の参照を破棄
				_schemaRelationMap.removeDependent(fieldModel);
				fieldModel.setTargetValue(null);
			}
			fieldModel.setParentObject(null);
			fieldNode.setUserObject(null);
		}
		tableNode.removeAllChildren();
		
		// 削除したフィールド数を返す
		return numChildren;
	}

	/**
	 * 指定された出力スキーマツリーのテーブルノードが持つ、すべてのフィールドを削除し、ツリーを更新する。
	 * このメソッド呼び出しでは、出力フィールドの出力値との参照関係も削除する。
	 * @param tableNode	対象の出力テーブルスキーマツリーノード
	 * @return	フィールドが一つでも削除された場合は <tt>true</tt>、削除されなかった場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean deleteAllFieldsFromOutputTableSchema(GenericTableSchemaTreeNode tableNode) {
		int numChildren = removeAllFieldsFromOutputTableSchema(tableNode);
		if (numChildren <= 0)
			return false;	// no children
		
		// ツリーへ反映
		tableNode.verify();
		_outputSchemaTreeModel.reload(tableNode);
		return true;
	}

	/**
	 * 指定された出力スキーマツリーのテーブルノードを削除し、ツリーを更新する。
	 * このメソッド呼び出しでは、出力テーブルに割り当てられたフィルタ定義引数も削除する。
	 * @param tableNode	削除対象の出力テーブルツリーノード
	 * @return	次に削除可能なテーブルノードへのツリーパス、テーブルノードが存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public TreePath deleteOutputTableSchema(GenericTableSchemaTreeNode tableNode) {
		// フィールドを削除し、参照関係を破棄
		deleteAllFieldsFromOutputTableSchema(tableNode);
		
		// フィルタ定義引数の削除
		OutputCsvTableSchemaEditModel tableModel = (OutputCsvTableSchemaEditModel)tableNode.getUserObject();
		GenericFilterArgEditModel argModel = (GenericFilterArgEditModel)tableModel.getFilterArgModel();
		tableModel.setFilterArgModel(null);
		_schemaRelationMap.removePrecedent(argModel);	// 出力引数は出力テーブルスキーマしか参照していないので、通知不要
		//--- フィルタ定義引数の削除
		int argIndex = argModel.getArgNo() - 1;
		removeMExecDefArgument(argIndex);	// 更新通知も含む
		
		// ツリーへ反映
		int childIndex = _outputSchemaTreeRootNode.getIndex(tableNode);
		_outputSchemaTreeRootNode.remove(childIndex);
		_outputSchemaTreeModel.nodesWereRemoved(_outputSchemaTreeRootNode, new int[]{childIndex}, new Object[]{tableNode});
		
		// 次に選択するツリーノードを取得
		int numChildren = _outputSchemaTreeRootNode.getChildCount();
		if (childIndex < numChildren) {
			return new TreePath(((GenericTableSchemaTreeNode)_outputSchemaTreeRootNode.getChildAt(childIndex)).getPath());
		} else if (numChildren > 0) {
			return new TreePath(((GenericTableSchemaTreeNode)_outputSchemaTreeRootNode.getChildAt(numChildren - 1)).getPath());
		} else {
			return null;
		}
	}

	/**
	 * 指定された出力スキーマツリーのフィールドノードを削除し、ツリーを更新する。
	 * @param srcNode	削除対象の出力フィールドノード
	 * @return	次に削除可能なノードへのツリーパス、同一テーブル内で削除可能なフィールドが存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	<em>srcNode</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	親が存在しない、もしくは親に含まれていないノードの場合
	 * @throws ClassCastException	<em>srcNode</em> のユーザーオブジェクトが出力フィールドデータではない場合
	 * @since 3.2.1
	 */
	public TreePath deleteOutputFieldSchema(GenericSchemaElementTreeNode srcNode) {
		// ソースデータモデルの取得
		OutputCsvFieldSchemaEditModel srcModel = (OutputCsvFieldSchemaEditModel)srcNode.getUserObject();
		GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)srcNode.getParent();
		if (tableNode == null)
			throw new IllegalArgumentException("The specified node has no parent.");
		
		// 削除位置のインデックスの取得
		int srcIndex = tableNode.getIndex(srcNode);
		if (srcIndex < 0)
			throw new IllegalArgumentException("The specified node is not included in it's parent.");
		
		// リンクの解除
		_schemaRelationMap.removeDependent(srcModel);
		
		// 削除
		tableNode.remove(srcIndex);
		
		// 要素番号の更新
		int numChildren = tableNode.getChildCount();
		int[] modifiedIndices = new int[numChildren - srcIndex];
		for (int index = srcIndex; index < numChildren; ++index) {
			GenericSchemaElementTreeNode childNode = (GenericSchemaElementTreeNode)tableNode.getChildAt(index);
			OutputCsvFieldSchemaEditModel childModel = (OutputCsvFieldSchemaEditModel)childNode.getUserObject();
			childModel.setElementNo(index+1);
			modifiedIndices[index - srcIndex] = index;
		}
		
		// ツリーへ反映
		TreePath result;
		_outputSchemaTreeModel.nodesWereRemoved(tableNode, new int[]{srcIndex}, new Object[]{srcNode});
		if (modifiedIndices.length > 0) {
			_outputSchemaTreeModel.nodesChanged(tableNode, modifiedIndices);
			result = new TreePath(((GenericSchemaElementTreeNode)tableNode.getChildAt(srcIndex)).getPath());
		}
		else if (numChildren > 0) {
			// select before field
			result = new TreePath(((GenericSchemaElementTreeNode)tableNode.getChildAt(numChildren-1)).getPath());
		}
		else {
			// table has no children
			result = null;
		}
		
		// 親テーブルノードのエラー情報更新
		boolean oldError = tableNode.hasError();
		boolean newError = !tableNode.verify();
		if (newError || oldError) {
			//--- エラー情報更新
			_outputSchemaTreeModel.nodeChanged(tableNode);
		}
		
		// completed
		return result;
	}

	/**
	 * 指定された出力スキーマツリーのフィールドノードを、指定された方向に一つ移動させる。
	 * @param srcNode	移動対象の出力フィールドノード
	 * @param direction	上(前)に移動する場合は負の値、下(後)に移動する場合は正の値
	 * @return	移動後のツリーパス、移動できなかった場合は <tt>null</tt>
	 * @throws NullPointerException	<em>srcNode</em> が <tt>null</tt> の場合
	 * @throws ClassCastException	<em>srcNode</em> のユーザーオブジェクトが出力フィールドデータではない場合
	 * @since 3.2.1
	 */
	public TreePath shiftOutputFieldSchema(GenericSchemaElementTreeNode srcNode, int direction) {
		// ソースデータモデルの取得
		OutputCsvFieldSchemaEditModel srcModel = (OutputCsvFieldSchemaEditModel)srcNode.getUserObject();
		
		// 移動先インデックスの取得
		int offset;
		if (direction < 0)
			offset = (-1);
		else if (direction > 0)
			offset = 1;
		else
			return null;	// no move
		int numChildren = srcNode.getParent().getChildCount();
		int srcIndex = srcNode.getParent().getIndex(srcNode);
		int dstIndex = srcIndex + offset;
		if (srcIndex < 0 || dstIndex < 0 || dstIndex >= numChildren)
			return null;	// out of range
		
		// 移動
		GenericSchemaElementTreeNode dstNode = (GenericSchemaElementTreeNode)srcNode.getParent().getChildAt(dstIndex);
		OutputCsvFieldSchemaEditModel dstModel = (OutputCsvFieldSchemaEditModel)dstNode.getUserObject();
		srcModel.setElementNo(dstIndex + 1);
		dstModel.setElementNo(srcIndex + 1);
		srcNode.setUserObject(dstModel);
		dstNode.setUserObject(srcModel);
		
		// ツリーモデルの更新
		_outputSchemaTreeModel.nodesChanged(dstNode.getParent(), new int[]{srcIndex, dstIndex});
		return new TreePath(dstNode.getPath());
	}
	
	/**
	 * 指定されたオブジェクトが各編集データから参照されているかどうかを判定する。
	 * @param obj	判定対象のオブジェクト
	 * @return	参照されていれば <tt>true</tt>
	 */
	public boolean isPrecedentReferenceObject(SchemaObject obj) {
		return _schemaRelationMap.containsPrecedent(obj);
	}

	/**
	 * 指定されたオブジェクトが、各編集データを参照しているかどうかを判定する。
	 * @param obj	判定対象のオブジェクト
	 * @return	他のオブジェクトを参照していれば <tt>true</tt>
	 */
	public boolean isDependentReferenceObject(SchemaObject obj) {
		return _schemaRelationMap.containsDependent(obj);
	}

	/**
	 * 指定されたオブジェクトの参照関係を登録する。
	 * @param dependent	参照する側のオブジェクト
	 * @param precedent	参照される側のオブジェクト
	 * @return	参照関係が登録された場合は <tt>true</tt>、すでに登録済みの場合は <tt>false</tt>
	 * @throws NullPointerException	<em>dependent</em> もしくは <em>precedent</em> が <tt>null</tt> の場合
	 */
	public boolean setObjectReference(SchemaObject dependent, SchemaObject precedent) {
		return _schemaRelationMap.setReference(dependent, precedent);
	}

	/**
	 * 指定されたオブジェクトが、各編集データを参照しているオブジェクトであれば、そのオブジェクトの参照関係を削除する。
	 * @param obj	判定対象のオブジェクト
	 * @return	他のオブジェクトを参照しており、参照関係が削除された場合は <tt>true</tt>
	 */
	public boolean removeDependentReference(SchemaObject obj) {
		return _schemaRelationMap.removeDependent(obj);
	}

	/**
	 * 参照されているスキーマ定義オブジェクトを参照から削除し、参照している側からも除去する。
	 * @param precedent	参照されているスキーマ定義オブジェクト
	 * @return	削除された場合は <tt>true</tt>、参照関係が存在しない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 3.2.1
	 */
	public boolean removePrecedentDataFromAllDependents(SchemaObject precedent) {
		if (!_schemaRelationMap.containsPrecedent(precedent))
			return false;	// no reference
		
		// 参照されている側を削除
		ArrayList<SchemaObject> depends = new ArrayList<SchemaObject>(_schemaRelationMap.getAllDependents(precedent));
		_schemaRelationMap.removePrecedent(precedent);

		// 参照している側から、参照している値を除去
		boolean removed = false;
		for (SchemaObject dependent : depends) {
			if (dependent instanceof OutputCsvFieldSchemaEditModel) {
				// 出力フィールドスキーマ
				if (removePrecedentDataFromOutputFieldSchema(precedent, (OutputCsvFieldSchemaEditModel)dependent))
					removed = true;
			}
			else if (dependent instanceof OutputCsvTableSchemaEditModel) {
				// 出力テーブルスキーマ
				if (removePrecedentDataFromOutputTableSchema(precedent, (OutputCsvTableSchemaEditModel)dependent))
					removed = true;
			}
			else if (dependent instanceof GenericJoinCondElementEditModel) {
				// 結合条件スキーマ
				if (removePrecedentDataFromJoinConditionSchema(precedent, (GenericJoinCondElementEditModel)dependent))
					removed = true;
			}
			else if (dependent instanceof GenericExpressionElementEditModel) {
				// 計算式スキーマ
				if (removePrecedentDataFromExpressionSchema(precedent, (GenericExpressionElementEditModel)dependent))
					removed = true;
			}
			else if (dependent instanceof InputCsvTableSchemaEditModel) {
				// 入力テーブルスキーマ
				if (removePrecedentDataFromInputTableSchema(precedent, (InputCsvTableSchemaEditModel)dependent))
					removed = true;
			}
			else {
				// 未定義の参照関係
				if (dependent == null)
					throw new IllegalStateException("Dependent object is null : precedent=" + precedent.toParamString());
				else
					throw new IllegalStateException("Unknown dependent object class : dependent=" + dependent.toParamString() + ", precedent=" + precedent.toParamString());
			}
		}
		return removed;
	}
	
	/**
	 * 指定された入力テーブルスキーマオブジェクトから、指定された参照値を削除し、状態を更新する。
	 * @param precedent	削除対象の参照される側のオブジェクト
	 * @param dependent	参照している側のオブジェクト
	 * @return	参照値が除去された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が編集対象のスキーマオブジェクトではない場合
	 */
	public boolean removePrecedentDataFromInputTableSchema(SchemaObject precedent, InputCsvTableSchemaEditModel dependent) {
		if (dependent == null)
			throw new NullPointerException();
		GenericTableSchemaTreeNode tableNode = findInputTableSchemaTreeNode(dependent);
		if (tableNode == null)
			throw new IllegalArgumentException("The dependent is not valid input table schema : dependent=" + dependent.toParamString());
		if (dependent.getFilterArgModel() == precedent) {
			// フィルタ定義引数の参照
			dependent.setFilterArgModel(null);
			// 正当性評価
			tableNode.verify();
			// 更新
			_inputSchemaTreeModel.nodeChanged(tableNode);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 指定された出力フィールドスキーマオブジェクトから、指定された参照値を削除し、状態を更新する。
	 * @param precedent	削除対象の参照される側のオブジェクト
	 * @param dependent	参照している側のオブジェクト
	 * @return	参照値が除去された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が編集対象のスキーマオブジェクトではない場合
	 */
	public boolean removePrecedentDataFromOutputFieldSchema(SchemaObject precedent, OutputCsvFieldSchemaEditModel dependent) {
		if (dependent == null)
			throw new NullPointerException();
		GenericSchemaElementTreeNode fieldNode = findOutputFieldSchemaTreeNode(dependent);
		if (fieldNode == null)
			throw new IllegalArgumentException("The dependent is not valid output field schema : dependent=" + dependent.toParamString());
		if (dependent.getTargetValue() == precedent) {
			// 出力値の参照
			dependent.setTargetValue(null);
			// 正当性評価
			fieldNode.verify();
			// 更新
			_outputSchemaTreeModel.nodeChanged(fieldNode);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 指定された出力テーブルスキーマオブジェクトから、指定された参照値を削除し、状態を更新する。
	 * @param precedent	削除対象の参照される側のオブジェクト
	 * @param dependent	参照している側のオブジェクト
	 * @return	参照値が除去された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が編集対象のスキーマオブジェクトではない場合
	 */
	public boolean removePrecedentDataFromOutputTableSchema(SchemaObject precedent, OutputCsvTableSchemaEditModel dependent) {
		if (dependent == null)
			throw new NullPointerException();
		GenericTableSchemaTreeNode tableNode = findOutputTableSchemaTreeNode(dependent);
		if (tableNode == null)
			throw new IllegalArgumentException("The dependent is not valid output table schema : dependent=" + dependent.toParamString());
		if (dependent.getFilterArgModel() == precedent) {
			// フィルタ定義引数の参照
			dependent.setFilterArgModel(null);
			// 正当性評価
			tableNode.verify();
			// 更新
			_outputSchemaTreeModel.nodeChanged(tableNode);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 指定された計算式スキーマオブジェクトから、指定された参照値を除去し、状態を更新する。
	 * @param precedent	削除対象の参照される側のオブジェクト
	 * @param dependent	参照している側のオブジェクト
	 * @return	参照値が除去された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が編集対象のスキーマオブジェクトではない場合
	 */
	public boolean removePrecedentDataFromExpressionSchema(SchemaObject precedent, GenericExpressionElementEditModel dependent) {
		int elemIndex = dependent.getElementNo() - 1;
		if (elemIndex < 0 || elemIndex >= _expSchemaTableModel.getDataModel().size())
			throw new IllegalArgumentException("The dependent is not valid expression element : dependent=" + dependent.toParamString());
		if (_expSchemaTableModel.getDataModel().get(elemIndex) != dependent)
			throw new IllegalArgumentException("The dependent is not valid expression element : dependent=" + dependent.toParamString());
		if (dependent.getLeftOperand() == precedent) {
			// 左オペランドの参照
			dependent.setLeftOperand(null);
			// 正当性評価
			dependent.verify();
			// 更新
			_expSchemaTableModel.fireTableRowsUpdated(elemIndex, elemIndex);
			return true;
		} else if (dependent.getRightOperand() == precedent) {
			// 右オペランドの参照
			dependent.setRightOperand(null);
			// 正当性評価
			dependent.verify();
			// 更新
			_expSchemaTableModel.fireTableRowsUpdated(elemIndex, elemIndex);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 指定された結合条件スキーマオブジェクトから、指定された参照値を除去し、状態を更新する。
	 * @param precedent	削除対象の参照される側のオブジェクト
	 * @param dependent	参照している側のオブジェクト
	 * @return	参照値が除去された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が編集対象のスキーマオブジェクトではない場合
	 */
	public boolean removePrecedentDataFromJoinConditionSchema(SchemaObject precedent, GenericJoinCondElementEditModel dependent) {
		int elemIndex = dependent.getElementNo() - 1;
		if (elemIndex < 0 || elemIndex >= _joinSchemaTableModel.getDataModel().size())
			throw new IllegalArgumentException("The dependent is not valid join condition element : dependent=" + dependent.toParamString());
		if (_joinSchemaTableModel.getDataModel().get(elemIndex) != dependent)
			throw new IllegalArgumentException("The dependent is not valid join condition element : dependent=" + dependent.toParamString());
		if (dependent.getLeftOperand() == precedent) {
			// 左オペランドの参照
			dependent.setLeftOperand(null);
			// 正当性評価
			dependent.verify();
			// 更新
			_joinSchemaTableModel.fireTableRowsUpdated(elemIndex, elemIndex);
			return true;
		} else if (dependent.getRightOperand() == precedent) {
			// 右オペランドの参照
			dependent.setRightOperand(null);
			// 正当性評価
			dependent.verify();
			// 更新
			_joinSchemaTableModel.fireTableRowsUpdated(elemIndex, elemIndex);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 指定されたオブジェクトを参照しているデータモデルに対し、その値が変化したことを通知する。
	 * @param precedent	参照される側のオブジェクト
	 * @return	指定されたオブジェクトを参照しているオブジェクトが存在した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IllegalStateException	参照しているオブジェクトが未知のデータ型の場合
	 */
	public boolean notifyPrecedentDataChanged(SchemaObject precedent) {
		if (!_schemaRelationMap.containsPrecedent(precedent))
			return false;	// no reference
		
		// 参照しているオブジェクトをすべて取得
		Collection<SchemaObject> dependents = _schemaRelationMap.getAllDependents(precedent);
		if (dependents.isEmpty())
			return false;	// no reference
		
		// 参照しているオブジェクトにデータ変更を通知
		for (SchemaObject obj : dependents) {
			if (obj instanceof OutputCsvFieldSchemaEditModel) {
				// 出力フィールドスキーマ
				notifyPrecedentDataChangedToOutputFieldSchema((OutputCsvFieldSchemaEditModel)obj);
			}
			else if (obj instanceof OutputCsvTableSchemaEditModel) {
				// 出力テーブルスキーマ
				notifyPrecedentDataChangedToOutputTableSchema((OutputCsvTableSchemaEditModel)obj);
			}
			else if (obj instanceof GenericExpressionElementEditModel) {
				// 計算式スキーマ
				notifyPrecedentDataChangedToExpressionSchema((GenericExpressionElementEditModel)obj);
			}
			else if (obj instanceof GenericJoinCondElementEditModel) {
				// 結合条件スキーマ
				notifyPrecedentDataChangedToJoinConditionSchema((GenericJoinCondElementEditModel)obj);
			}
			else if (obj instanceof InputCsvFieldSchemaEditModel) {
				// 入力フィールドスキーマ
				notifyPrecedentDataChangedToInputFieldSchema((InputCsvFieldSchemaEditModel)obj);
			}
			else if (obj instanceof InputCsvTableSchemaEditModel) {
				// 入力テーブルスキーマ
				notifyPrecedentDataChangedToInputTableSchema((InputCsvTableSchemaEditModel)obj);
			}
			else {
				// 未定義の参照関係
				if (obj == null)
					throw new IllegalStateException("Dependent object is null : precedent=" + precedent.toParamString());
				else
					throw new IllegalStateException("Unknown dependent object class : dependent=" + obj.toParamString() + ", precedent=" + precedent.toParamString());
			}
		}
		return true;
	}

	/**
	 * 指定された入力フィールドスキーマオブジェクトに、参照している値が更新されたことを通知する。
	 * @param dependent	参照している側のオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が編集対象のスキーマオブジェクトではない場合
	 */
	public void notifyPrecedentDataChangedToInputFieldSchema(InputCsvFieldSchemaEditModel dependent) {
		if (dependent == null)
			throw new NullPointerException();
		GenericSchemaElementTreeNode fieldNode = findInputFieldSchemaTreeNode(dependent);
		if (fieldNode == null)
			throw new IllegalArgumentException("The dependent is not valid input field schema : dependent=" + dependent.toParamString());
		//--- verify
		fieldNode.verify();
		//--- 更新
		_inputSchemaTreeModel.nodeChanged(fieldNode);
	}
	
	/**
	 * 指定された入力テーブルスキーマオブジェクトに、参照している値が更新されたことを通知する。
	 * @param dependent	参照している側のオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が編集対象のスキーマオブジェクトではない場合
	 */
	public void notifyPrecedentDataChangedToInputTableSchema(InputCsvTableSchemaEditModel dependent) {
		if (dependent == null)
			throw new NullPointerException();
		GenericTableSchemaTreeNode tableNode = findInputTableSchemaTreeNode(dependent);
		if (tableNode == null)
			throw new IllegalArgumentException("The dependent is not valid input table schema : dependent=" + dependent.toParamString());
		//--- verify
		tableNode.verify();
		//--- 更新
		_inputSchemaTreeModel.nodeChanged(tableNode);
	}
	
	/**
	 * 指定された出力フィールドスキーマオブジェクトに、参照している値が更新されたことを通知する。
	 * @param dependent	参照している側のオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が編集対象のスキーマオブジェクトではない場合
	 */
	public void notifyPrecedentDataChangedToOutputFieldSchema(OutputCsvFieldSchemaEditModel dependent) {
		if (dependent == null)
			throw new NullPointerException();
		GenericSchemaElementTreeNode fieldNode = findOutputFieldSchemaTreeNode(dependent);
		if (fieldNode == null)
			throw new IllegalArgumentException("The dependent is not valid output field schema : dependent=" + dependent.toParamString());
		//--- verify
		fieldNode.verify();
		//--- 更新
		_outputSchemaTreeModel.nodeChanged(fieldNode);
	}
	
	/**
	 * 指定された出力テーブルスキーマオブジェクトに、参照している値が更新されたことを通知する。
	 * @param dependent	参照している側のオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が編集対象のスキーマオブジェクトではない場合
	 */
	public void notifyPrecedentDataChangedToOutputTableSchema(OutputCsvTableSchemaEditModel dependent) {
		if (dependent == null)
			throw new NullPointerException();
		GenericTableSchemaTreeNode tableNode = findOutputTableSchemaTreeNode(dependent);
		if (tableNode == null)
			throw new IllegalArgumentException("The dependent is not valid output table schema : dependent=" + dependent.toParamString());
		//--- verify
		tableNode.verify();
		//--- 更新
		_outputSchemaTreeModel.nodeChanged(tableNode);
	}
	
	/**
	 * 指定された計算式スキーマオブジェクトに、参照している値が更新されたことを通知する。
	 * @param dependent	参照している側のオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が編集対象のスキーマオブジェクトではない場合
	 */
	public void notifyPrecedentDataChangedToExpressionSchema(GenericExpressionElementEditModel dependent) {
		int elemIndex = dependent.getElementNo() - 1;
		if (elemIndex < 0 || elemIndex >= _expSchemaTableModel.getDataModel().size())
			throw new IllegalArgumentException("The dependent is not valid expression element : dependent=" + dependent.toParamString());
		GenericExpressionElementEditModel expModel = _expSchemaTableModel.getDataModel().get(elemIndex);
		if (expModel != dependent)
			throw new IllegalArgumentException("The dependent is not valid expression element : dependent=" + dependent.toParamString());
		//--- verify
		expModel.verify();
		//--- 更新
		_expSchemaTableModel.fireTableRowsUpdated(elemIndex, elemIndex);
	}
	
	/**
	 * 指定された結合条件スキーマオブジェクトに、参照している値が更新されたことを通知する。
	 * @param dependent	参照している側のオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が編集対象のスキーマオブジェクトではない場合
	 */
	public void notifyPrecedentDataChangedToJoinConditionSchema(GenericJoinCondElementEditModel dependent) {
		int elemIndex = dependent.getElementNo() - 1;
		if (elemIndex < 0 || elemIndex >= _joinSchemaTableModel.getDataModel().size())
			throw new IllegalArgumentException("The dependent is not valid join condition element : dependent=" + dependent.toParamString());
		GenericJoinCondElementEditModel joinModel = _joinSchemaTableModel.getDataModel().get(elemIndex);
		if (joinModel != dependent)
			throw new IllegalArgumentException("The dependent is not valid join condition element : dependent=" + dependent.toParamString());
		//--- verify
		joinModel.verify();
		//--- 更新
		_joinSchemaTableModel.fireTableRowsUpdated(elemIndex, elemIndex);
	}

	/**
	 * 指定された入力フィールドスキーマオブジェクトを保持するツリーノードへのツリーパスを取得する。
	 * @param fieldModel	検索対象の入力フィールドスキーマオブジェクト
	 * @return	ツリーに存在するノードならそのツリーノード、それ以外の場合は <tt>null</tt>
	 */
	public GenericSchemaElementTreeNode findInputFieldSchemaTreeNode(InputCsvFieldSchemaEditModel fieldModel) {
		if (fieldModel != null) {
			InputCsvTableSchemaEditModel tableModel = (InputCsvTableSchemaEditModel)fieldModel.getParentObject();
			int numChildren = _inputSchemaTreeRootNode.getChildCount();
			for (int index = 0; index < numChildren; ++index) {
				GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)_inputSchemaTreeRootNode.getChildAt(index);
				if (tableNode.getUserObject() == tableModel) {
					int elemIndex = fieldModel.getElementNo() - 1;
					if (elemIndex >= 0 && elemIndex < tableNode.getChildCount()) {
						GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)tableNode.getChildAt(elemIndex);
						if (fieldNode.getUserObject() == fieldModel) {
							return fieldNode;
						}
					}
				}
			}
		}
		// not found
		return null;
	}

	/**
	 * 指定された出力フィールドスキーマオブジェクトを保持するツリーノードへのツリーパスを取得する。
	 * @param fieldModel	検索対象の出力フィールドスキーマオブジェクト
	 * @return	ツリーに存在するノードならそのツリーノード、それ以外の場合は <tt>null</tt>
	 */
	public GenericSchemaElementTreeNode findOutputFieldSchemaTreeNode(OutputCsvFieldSchemaEditModel fieldModel) {
		if (fieldModel != null) {
			OutputCsvTableSchemaEditModel tableModel = (OutputCsvTableSchemaEditModel)fieldModel.getParentObject();
			int numChildren = _outputSchemaTreeRootNode.getChildCount();
			for (int index = 0; index < numChildren; ++index) {
				GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)_outputSchemaTreeRootNode.getChildAt(index);
				if (tableNode.getUserObject() == tableModel) {
					int elemIndex = fieldModel.getElementNo() - 1;
					if (elemIndex >= 0 && elemIndex < tableNode.getChildCount()) {
						GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)tableNode.getChildAt(elemIndex);
						if (fieldNode.getUserObject() == fieldModel) {
							return fieldNode;
						}
					}
				}
			}
		}
		// not found
		return null;
	}

	/**
	 * 指定された入力テーブルスキーマオブジェクトを保持するツリーノードへのツリーパスを取得する。
	 * @param tableModel	検索対象の入力テーブルスキーマオブジェクト
	 * @return	ツリーに存在するノードならそのツリーノード、それ以外の場合は <tt>null</tt>
	 */
	public GenericTableSchemaTreeNode findInputTableSchemaTreeNode(InputCsvTableSchemaEditModel tableModel) {
		if (tableModel != null) {
			int numChildren = _inputSchemaTreeRootNode.getChildCount();
			for (int index = 0; index < numChildren; ++index) {
				GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)_inputSchemaTreeRootNode.getChildAt(index);
				if (tableNode.getUserObject() == tableModel) {
					return tableNode;
				}
			}
		}
		// not found
		return null;
	}
	
	/**
	 * 指定された出力テーブルスキーマオブジェクトを保持するツリーノードへのツリーパスを取得する。
	 * @param tableModel	検索対象の出力テーブルスキーマオブジェクト
	 * @return	ツリーに存在するノードならそのツリーノード、それ以外の場合は <tt>null</tt>
	 */
	public GenericTableSchemaTreeNode findOutputTableSchemaTreeNode(OutputCsvTableSchemaEditModel tableModel) {
		if (tableModel != null) {
			int numChildren = _outputSchemaTreeRootNode.getChildCount();
			for (int index = 0; index < numChildren; ++index) {
				GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)_outputSchemaTreeRootNode.getChildAt(index);
				if (tableNode.getUserObject() == tableModel) {
					return tableNode;
				}
			}
		}
		// not found
		return null;
	}

	/**
	 * 現在の定義内容から、保存用スキーマ定義データを取得する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * このメソッドを呼び出す前に、スキーマデータの整合性は確保しておくこと。
	 * </blockquote>
	 * @return	スキーマ定義データ
	 * @throws IllegalStateException	状態が適切ではない場合
	 */
	public SchemaConfig createSchemaConfig() {
		SchemaConfig config = new SchemaConfig();
		IdentityHashMap<SchemaElementObject, SchemaElementObject>	instancemap = new IdentityHashMap<SchemaElementObject, SchemaElementObject>();
		
		// filter description
		config.setDescription(_strFilterDesc);

		// フィルタ定義引数
		createAllFilterArgData(config, instancemap);
		
		// 日付時刻書式
		SchemaDTFormatConfig dtfConfig = config.getDateTimeFormatConfig();
		if (_dtfEditModel.hasFilterArgumentModel()) {
			GenericFilterArgEditModel argModel = _dtfEditModel.getFilterArgumentModel();
			SchemaFilterArgValue argValue = (SchemaFilterArgValue)instancemap.get(argModel);
			if (argValue == null) {
				// error
				String msg = "Filter argument schema for DateTime format does not found : " + String.valueOf(argModel);
				throw new IllegalStateException(msg);
			}
			dtfConfig.setFilterArgument(argValue);
			dtfConfig.setPattern(null);
		}
		else {
			dtfConfig.setFilterArgument(null);
			dtfConfig.setPatternString(_dtfEditModel.getPatternString());
		}
		
		// 入力スキーマ
		createAllInputFieldData(config, instancemap);
		
		// 計算式
		createAllExpressionData(config, instancemap);
		
		// 結合条件
		createAllJoinConditionData(config, instancemap);
		
		// 出力スキーマ
		createAllOutputFieldData(config, instancemap);

		// cleanup
		instancemap.clear();
		return config;
	}

	//------------------------------------------------------------
	// Public interfaces for Expression Schema
	//------------------------------------------------------------
	
	public GenericExpressionSchemaTableModel getExpressionSchemaTableModel() {
		return _expSchemaTableModel;
	}

	//------------------------------------------------------------
	// Public interfaces for Join Schema
	//------------------------------------------------------------
	
	public GenericJoinCondSchemaTableModel getJoinConditionSchemaTableModel() {
		return _joinSchemaTableModel;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * フィルター定義ファイルの内容から、このモデルのフィルタ定義引数を初期化する。
	 * @param filterDefs	フィルタ定義ファイルの内容を保持するオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected void setupFilterArgsFromMExecDefSettings(final MExecDefSettings filterDefs) {
		int numArgs = filterDefs.getNumArguments();
		for (int argidx = 0; argidx < numArgs; ++argidx) {
			ModuleArgData argData = filterDefs.getArgument(argidx);
			Object argValue = argData.getValue();
			//--- create argument element
			GenericFilterArgEditModel argModel = new GenericFilterArgEditModel(argidx+1, argData.getType(), argData.getDescription(), argValue);
			argModel.setValue(argValue);
			//--- add argument element
			_defArgsList.add(argModel);
			//--- editable?
			if (isEditableMExecDefArgument(argModel)) {
				// 計算式等で選択可能なフィルタ定義引数
				_defEditableArgsList.add(argModel);
			}
		}
	}

	/**
	 * スキーマ定義ファイルの内容から、このモデルのスキーマ定義を初期化する。
	 * @param sconfig	スキーマ定義データ
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected void setupModelsFromSchemaConfig(final SchemaConfig sconfig) {
		// create instance map
		IdentityHashMap<SchemaElementObject, SchemaElementObject>	instmap = new IdentityHashMap<SchemaElementObject, SchemaElementObject>();
		
		// filter description
		_strFilterDesc = sconfig.getDescription();
		
		// setup Filter arguments
		for (SchemaFilterArgValue schemaArgValue : sconfig.getFilterArgList()) {
			// 引数番号に対応するフィルタ定義引数を取得し、データ型のみ更新
			GenericFilterArgEditModel argModel = (GenericFilterArgEditModel)_defArgsList.get(schemaArgValue.getArgumentIndex());
			argModel.setValueType(schemaArgValue.getValueType());
			//--- インスタンスの対応をマップに登録
			instmap.put(schemaArgValue, argModel);
		}
		//--- update table model
		_defArgTableModel.fireTableStructureChanged();
		
		// setup SchemaDateTimeFormats
		SchemaDTFormatConfig dtfConfig = sconfig.getDateTimeFormatConfig();
		if (dtfConfig.hasFilterArgument()) {
			GenericFilterArgEditModel argModel = (GenericFilterArgEditModel)_defArgsList.get(dtfConfig.getFilterArgument().getArgumentIndex());
			_dtfEditModel.setFilterArgumentModel(argModel);
			_dtfEditModel.setFilterArgument(null);
			_dtfEditModel.setPattern(null);
		}
		else {
			_dtfEditModel.setFilterArgumentModel(null);
			_dtfEditModel.setFilterArgument(null);
			_dtfEditModel.setPatternString(dtfConfig.getPatternString());
		}
		
		// setup Input schemas
		setupAllInputFieldEditModel(sconfig, instmap);
		
		// setup Expressions
		setupAllExpressionEditModel(sconfig, instmap);
		
		// setup Join conditions
		setupAllJoinConditionEditModel(sconfig, instmap);
		
		// setup Output schemas
		setupAllOutputFieldEditModel(sconfig, instmap);
		
		// accomplished
		instmap.clear();
	}

	/**
	 * フィルタ定義引数の引数属性をスキーマ定義のフィルタ引数属性に変換する。
	 * @param orgArgType	フィルタ定義引数の属性値
	 * @return	スキーマ定義におけるフィルタ引数属性値
	 */
	protected FilterArgType convertSchemaFilterArgTypeFromModuleArgType(ModuleArgType orgArgType) {
		if (orgArgType != null) {
			if (ModuleArgType.IN == orgArgType) {
				return FilterArgType.IN;
			}
			else if (ModuleArgType.OUT == orgArgType) {
				return FilterArgType.OUT;
			}
			else if (ModuleArgType.STR == orgArgType) {
				return FilterArgType.STR;
			}
			else if (ModuleArgType.PUB == orgArgType) {
				return FilterArgType.PUB;
			}
			else if (ModuleArgType.SUB == orgArgType) {
				return FilterArgType.SUB;
			}
			else {
				return FilterArgType.NONE;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * スキーマ定義のフィルタ引数属性を、フィルタ定義引数の引数属性に変換する。
	 * @param srcArgType	スキーマ定義におけるフィルタ引数属性値
	 * @return	フィルタ定義引数の属性値
	 */
	protected ModuleArgType convertSchemaFilterArgTypeToModuleArgType(FilterArgType srcArgType) {
		if (srcArgType != null) {
			if (FilterArgType.IN == srcArgType) {
				return ModuleArgType.IN;
			}
			else if (FilterArgType.OUT == srcArgType) {
				return ModuleArgType.OUT;
			}
			else if (FilterArgType.STR == srcArgType) {
				return ModuleArgType.STR;
			}
			else if (FilterArgType.PUB == srcArgType) {
				return ModuleArgType.PUB;
			}
			else if (FilterArgType.SUB == srcArgType) {
				return ModuleArgType.SUB;
			}
			else {
				return ModuleArgType.NONE;
			}
		} else {
			return null;
		}
	}

	/**
	 * 現在のモデルデータから、スキーマ定義におけるフィルタ定義引数を生成する。
	 * @param config	スキーマ定義を格納するオブジェクト
	 * @param instmap	モデルデータとスキーマオブジェクトのインスタンスマップ
	 * @throws IllegalStateException	状態が適切ではない場合
	 */
	protected void createAllFilterArgData(SchemaConfig config, IdentityHashMap<SchemaElementObject, SchemaElementObject> instmap)
	{
		for (FilterArgEditModel model : _defArgsList) {
			GenericFilterArgEditModel argmodel = (GenericFilterArgEditModel)model;
			SchemaFilterArgValue argConfig = new SchemaFilterArgValue();
			argConfig.setName(argmodel.getName());
			argConfig.setDescription(argmodel.getDescription());
			argConfig.setElementNo(argmodel.getElementNo());
			argConfig.setValueType(argmodel.getValueType());
			argConfig.setArgumentType(convertSchemaFilterArgTypeFromModuleArgType(argmodel.getType()));
			argConfig.setValue(argmodel.getValue());
			instmap.put(argmodel, argConfig);
			config.getFilterArgList().add(argConfig);
		}
	}

	/**
	 * スキーマ定義データから、このモデルの入力スキーマ編集データを初期化する。
	 * @param config	スキーマ定義データ
	 * @param instmap	スキーマオブジェクトとモデルデータのインスタンスマップ
	 */
	protected void setupAllInputFieldEditModel(final SchemaConfig config, IdentityHashMap<SchemaElementObject, SchemaElementObject> instmap)
	{
		GenericTableSchemaRootNode rootNode = _inputSchemaTreeRootNode;
		for (SchemaInputCsvDataTable tableData : config.getInputTableList()) {
			//--- create input table edit model
			InputCsvTableSchemaEditModel tableModel = new InputCsvTableSchemaEditModel(false, tableData);	// 要素はコピーしない
			tableModel.setFilterArgument(null);	// スキーマ定義のフィルタ定義引数は持たない
			tableModel.setParentObject(null);	// 編集データモデルでは、親モデルはなし
			GenericFilterArgEditModel argModel = (GenericFilterArgEditModel)instmap.get(tableData.getFilterArgument());
			tableModel.setFilterArgModel(argModel);	// 編集用フィルタ定義引数のリンクを設定
			tableModel.setDescription(null);
			instmap.put(tableData, tableModel);
			//--- create input table tree node
			GenericTableSchemaTreeNode tableNode = new GenericTableSchemaTreeNode(tableModel);
			rootNode.add(tableNode);
			//--- put relation FilterArg and Input table
			_schemaRelationMap.setReference(tableModel, argModel);
			//--- create all field edit models in input table
			int numFields = tableData.size();
			for (int index = 0; index < numFields; ++index) {
				SchemaInputCsvDataField fieldData = tableData.get(index);
				//--- create input field edit model
				InputCsvFieldSchemaEditModel fieldModel = new InputCsvFieldSchemaEditModel(fieldData);
				fieldModel.setOriginalSchema(null);
				fieldModel.setParentObject(tableModel);
				fieldModel.setElementNo(index+1);	// 念のため、要素番号を更新
				instmap.put(fieldData, fieldModel);
				//--- create input field tree node
				GenericSchemaElementTreeNode fieldNode = new GenericSchemaElementTreeNode(fieldModel);
				tableNode.add(fieldNode);
				//--- verify
				fieldNode.verify();
			}
			//--- verify
			tableNode.verify();
		}
		//--- update model data
		_inputSchemaTreeModel.reload();
	}

	/**
	 * 現在のモデルデータから、スキーマ定義における入力スキーマを生成する。
	 * @param config	スキーマ定義を格納するオブジェクト
	 * @param instmap	モデルデータとスキーマオブジェクトのインスタンスマップ
	 * @throws IllegalStateException	状態が適切ではない場合
	 */
	protected void createAllInputFieldData(SchemaConfig config, IdentityHashMap<SchemaElementObject, SchemaElementObject> instmap)
	{
		GenericTableSchemaRootNode rootNode = _inputSchemaTreeRootNode;
		int numTables = rootNode.getChildCount();
		if (numTables == 0) {
			// error : no input schemas
			String msg = "Input table is nothing.";
			throw new IllegalArgumentException(msg);
		}
		
		for (int i = 0; i < numTables; ++i) {
			GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)rootNode.getChildAt(i);
			InputCsvTableSchemaEditModel tableModel = (InputCsvTableSchemaEditModel)tableNode.getUserObject();
			//--- create table data
			SchemaInputCsvDataTable configTable = new SchemaInputCsvDataTable(false, tableModel);	// 要素はコピーしない
			instmap.put(tableModel, configTable);
			configTable.clear();
			configTable.setElementNo(i+1);	// update element number
			//--- check filter argument
			GenericFilterArgEditModel argModel = tableModel.getFilterArgModel();
			SchemaFilterArgValue argValue = (SchemaFilterArgValue)instmap.get(argModel);
			if (argValue == null) {
				// error
				String msg = "Filter argument schema for Input table does not found : " + String.valueOf(argModel);
				throw new IllegalStateException(msg);
			}
			configTable.setDescription(null);
			configTable.setFilterArgument(argValue);
			//--- import all fields
			int numFields = tableNode.getChildCount();
			for (int f = 0; f < numFields; ++f) {
				GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)tableNode.getChildAt(f);
				InputCsvFieldSchemaEditModel fieldModel = (InputCsvFieldSchemaEditModel)fieldNode.getUserObject();
				//--- create field data
				SchemaInputCsvDataField configField = new SchemaInputCsvDataField(fieldModel);
				//--- add to table
				configTable.add(configField);
				configField.setParentObject(configTable);
				instmap.put(fieldModel, configField);
			}
			//--- add to table
			config.getInputTableList().add(configTable);
		}
	}
	
	/**
	 * スキーマ定義データから、このモデルの計算式スキーマ編集データを初期化する。
	 * @param config	スキーマ定義データ
	 * @param instmap	スキーマオブジェクトとモデルデータのインスタンスマップ
	 */
	protected void setupAllExpressionEditModel(final SchemaConfig config, IdentityHashMap<SchemaElementObject, SchemaElementObject> instmap)
	{
		SchemaExpressionList explist = config.getExpressionList();
		for (SchemaExpressionData expData : explist) {
			//--- setup left operand
			SchemaElementValue lop = expData.getLeftOperand();
			if (lop instanceof SchemaValueLink) {
				// SchemaValueLink
				SchemaValueLink link = (SchemaValueLink)lop;
				lop = (SchemaElementValue)instmap.get(link.getLinkTarget());
			}
			// else : SchemaLiteralValue
			//--- setup right operand
			SchemaElementValue rop = expData.getRightOperand();
			if (rop instanceof SchemaValueLink) {
				// SchemaValueLink
				SchemaValueLink link = (SchemaValueLink)rop;
				rop = (SchemaElementValue)instmap.get(link.getLinkTarget());
			}
			// else : SchemaLiteralValue
			
			//--- add edit model
			GenericExpressionElementEditModel expModel = _expSchemaTableModel.addNewElement(expData.getName(), expData.getOperator(), lop, rop);
			instmap.put(expData, expModel);
			//--- put operand relation
			if (!(lop instanceof SchemaLiteralValue)) {
				_schemaRelationMap.setReference(expModel, lop);
			}
			if (!(rop instanceof SchemaLiteralValue)) {
				_schemaRelationMap.setReference(expModel, rop);
			}
			
			//--- verify
			expModel.verify();
		}
	}
	
	/**
	 * 現在のモデルデータから、スキーマ定義における計算式スキーマを生成する。
	 * @param config	スキーマ定義を格納するオブジェクト
	 * @param instmap	モデルデータとスキーマオブジェクトのインスタンスマップ
	 * @throws IllegalStateException	状態が適切ではない場合
	 */
	protected void createAllExpressionData(SchemaConfig config, IdentityHashMap<SchemaElementObject, SchemaElementObject> instmap)
	{
		int numElements = _expSchemaTableModel.getRowCount();
		for (int i = 0; i < numElements; ++i) {
			GenericExpressionElementEditModel expModel = _expSchemaTableModel.getElement(i);
			//--- create left operand
			SchemaElementValue lop = expModel.getLeftOperand();
			if (lop != null) {
				//--- check instance
				if (!(lop instanceof SchemaLiteralValue)) {
					// 即値以外のオブジェクト
					SchemaElementValue srcLop = lop;
					lop = (SchemaElementValue)instmap.get(srcLop);
					if (lop == null) {
						// error : この式より前の式が存在しない
						String msg = "Source expression schema of left operand is not defined : " + srcLop.toParamString();
						throw new IllegalStateException(msg);
					}
					lop = new SchemaValueLink(lop);
				}
			} else {
				// error : No left operand
				String msg = "Expression has no left operand : " + expModel.toParamString();
				throw new IllegalStateException(msg);
			}
			//--- create right operand
			SchemaElementValue rop = expModel.getRightOperand();
			if (rop != null) {
				//--- check instance
				if (!(rop instanceof SchemaLiteralValue)) {
					// 即値以外のオブジェクト
					SchemaElementValue srcRop = rop;
					rop = (SchemaElementValue)instmap.get(srcRop);
					if (rop == null) {
						// error : この式より前の式が存在しない
						String msg = "Source expression schema of right operand is not defined : " + srcRop.toParamString();
						throw new IllegalStateException(msg);
					}
					rop = new SchemaValueLink(rop);
				}
			} else {
				// error : No right operand
				String msg = "Expression has no right operand : " + expModel.toParamString();
				throw new IllegalStateException(msg);
			}
			//--- create element data
			SchemaExpressionData expData = new SchemaExpressionData(expModel);
			expData.setLeftOperand(lop);
			expData.setRightOperand(rop);
			instmap.put(expModel, expData);
			config.getExpressionList().add(expData);
		}
	}
	
	/**
	 * スキーマ定義データから、このモデルの結合条件スキーマ編集データを初期化する。
	 * @param config	スキーマ定義データ
	 * @param instmap	スキーマオブジェクトとモデルデータのインスタンスマップ
	 */
	protected void setupAllJoinConditionEditModel(final SchemaConfig config, IdentityHashMap<SchemaElementObject, SchemaElementObject> instmap)
	{
		SchemaJoinConditionList joinlist = config.getJoinConditionList();
		for (SchemaJoinConditionData joinData : joinlist) {
			//--- setup left operand
			SchemaElementValue lop = joinData.getLeftOperand();
			if (lop instanceof SchemaValueLink) {
				// SchemaValueLink
				SchemaValueLink link = (SchemaValueLink)lop;
				lop = (SchemaElementValue)instmap.get(link.getLinkTarget());
			}
			//--- setup right operand
			SchemaElementValue rop = joinData.getRightOperand();
			if (rop instanceof SchemaValueLink) {
				// SchemaValueLink
				SchemaValueLink link = (SchemaValueLink)rop;
				rop = (SchemaElementValue)instmap.get(link.getLinkTarget());
			}
			
			//--- add edit model
			GenericJoinCondElementEditModel joinModel = _joinSchemaTableModel.addNewElement(joinData.getName(), joinData.getOperator(), lop, rop);
			instmap.put(joinData, joinModel);
			//--- put operand relation
			if (!(lop instanceof SchemaLiteralValue)) {
				_schemaRelationMap.setReference(joinModel, lop);
			}
			if (!(rop instanceof SchemaLiteralValue)) {
				_schemaRelationMap.setReference(joinModel, rop);
			}
			
			//--- verify
			joinModel.verify();
		}
	}
	
	/**
	 * 現在のモデルデータから、スキーマ定義における結合条件スキーマを生成する。
	 * @param config	スキーマ定義を格納するオブジェクト
	 * @param instmap	モデルデータとスキーマオブジェクトのインスタンスマップ
	 * @throws IllegalStateException	状態が適切ではない場合
	 */
	protected void createAllJoinConditionData(SchemaConfig config, IdentityHashMap<SchemaElementObject, SchemaElementObject> instmap)
	{
		int numElements = _joinSchemaTableModel.getRowCount();
		for (int i = 0; i < numElements; ++i) {
			GenericJoinCondElementEditModel joinModel = _joinSchemaTableModel.getElement(i);
			//--- create left operand
			SchemaElementValue lop = joinModel.getLeftOperand();
			if (lop != null) {
				//--- check instance
				SchemaElementValue srcLop = lop;
				lop = (SchemaElementValue)instmap.get(srcLop);
				if (lop == null) {
					// error : この式より前の式が存在しない
					String msg = "Join condition schema of left operand is not defined : " + srcLop.toParamString();
					throw new IllegalStateException(msg);
				}
				lop = new SchemaValueLink(lop);
			} else {
				// error : No left operand
				String msg = "Join condition has no left operand : " + joinModel.toParamString();
				throw new IllegalStateException(msg);
			}
			//--- create right operand
			SchemaElementValue rop = joinModel.getRightOperand();
			if (rop != null) {
				//--- check instance
				SchemaElementValue srcRop = rop;
				rop = (SchemaElementValue)instmap.get(srcRop);
				if (rop == null) {
					// error : この式より前の式が存在しない
					String msg = "Join condition schema of right operand is not defined : " + srcRop.toParamString();
					throw new IllegalStateException(msg);
				}
				rop = new SchemaValueLink(rop);
			} else {
				// error : No right operand
				String msg = "Join condition has no right operand : " + joinModel.toParamString();
				throw new IllegalStateException(msg);
			}
			//--- create element data
			SchemaJoinConditionData joinData = new SchemaJoinConditionData(joinModel);
			joinData.setLeftOperand(lop);
			joinData.setRightOperand(rop);
			instmap.put(joinModel, joinData);
			config.getJoinConditionList().add(joinData);
		}
	}
	
	/**
	 * スキーマ定義データから、このモデルの出力スキーマ編集データを初期化する。
	 * @param config	スキーマ定義データ
	 * @param instmap	スキーマオブジェクトとモデルデータのインスタンスマップ
	 */
	protected void setupAllOutputFieldEditModel(SchemaConfig config, IdentityHashMap<SchemaElementObject, SchemaElementObject> instmap)
	{
		GenericTableSchemaRootNode rootNode = _outputSchemaTreeRootNode;
		for (SchemaOutputCsvDataTable tableData : config.getOutputTableList()) {
			//--- create output table edit model
			OutputCsvTableSchemaEditModel tableModel = new OutputCsvTableSchemaEditModel(false, tableData);	// 要素はコピーしない
			tableModel.setFilterArgument(null);	// スキーマ定義のフィルタ定義引数は持たない
			tableModel.setParentObject(null);	// 編集データモデルでは親モデルはなし
			GenericFilterArgEditModel argModel = (GenericFilterArgEditModel)instmap.get(tableData.getFilterArgument());
			tableModel.setFilterArgModel(argModel);
			tableModel.setDescription(null);
			instmap.put(tableData, tableModel);
			//--- create output table tree node
			GenericTableSchemaTreeNode tableNode = new GenericTableSchemaTreeNode(tableModel);
			rootNode.add(tableNode);
			//--- put relation FilterArg and Output table
			_schemaRelationMap.setReference(tableModel, argModel);
			//--- create all field edit models in output table
			int numFields = tableData.size();
			for (int index = 0; index < numFields; ++index) {
				SchemaOutputCsvDataField fieldData = tableData.get(index);
				//--- create output field edit model
				OutputCsvFieldSchemaEditModel fieldModel = new OutputCsvFieldSchemaEditModel(fieldData);
				fieldModel.setParentObject(tableModel);
				fieldModel.setElementNo(index+1);	// 念のため、要素番号を更新
				SchemaElementValue target = fieldData.getTargetValue();
				if (target instanceof SchemaValueLink) {
					SchemaValueLink link = (SchemaValueLink)target;
					target = (SchemaElementValue)instmap.get(link.getLinkTarget());
					//--- put relation SchemaObject and Output field
					_schemaRelationMap.setReference(fieldModel, target);
				}
				fieldModel.setTargetValue(target);
				instmap.put(fieldData, fieldModel);
				//--- create output field tree node
				GenericSchemaElementTreeNode fieldNode = new GenericSchemaElementTreeNode(fieldModel);
				tableNode.add(fieldNode);
				//--- verify
				fieldNode.verify();
			}
			//--- verify
			tableNode.verify();
		}
		//--- update model data
		_outputSchemaTreeModel.reload();
	}
	
	/**
	 * 現在のモデルデータから、スキーマ定義における出力スキーマを生成する。
	 * @param config	スキーマ定義を格納するオブジェクト
	 * @param instmap	モデルデータとスキーマオブジェクトのインスタンスマップ
	 * @throws IllegalStateException	状態が適切ではない場合
	 */
	protected void createAllOutputFieldData(SchemaConfig config, IdentityHashMap<SchemaElementObject, SchemaElementObject> instmap)
	{
		GenericTableSchemaRootNode rootNode = _outputSchemaTreeRootNode;
		int numTables = rootNode.getChildCount();
		//--- ここでは、出力テーブルがなくても良しとする
		for (int i = 0; i < numTables; ++i) {
			GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)rootNode.getChildAt(i);
			OutputCsvTableSchemaEditModel tableModel = (OutputCsvTableSchemaEditModel)tableNode.getUserObject();
			//--- create table data
			SchemaOutputCsvDataTable configTable = new SchemaOutputCsvDataTable(false, tableModel);	// 要素はコピーしない
			instmap.put(tableModel, configTable);
			configTable.clear();
			//--- check filter argument
			FilterArgEditModel argModel = tableModel.getFilterArgModel();
			SchemaFilterArgValue argValue = (SchemaFilterArgValue)instmap.get(argModel);
			if (argValue == null) {
				// error
				String msg = "Filter argument schema for Output table does not found : " + String.valueOf(argModel);
				throw new IllegalStateException(msg);
			}
			configTable.setFilterArgument(argValue);
			configTable.setDescription(null);
			//--- import all fields
			int numFields = tableNode.getChildCount();
			for (int f = 0; f < numFields; ++f) {
				GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)tableNode.getChildAt(f);
				OutputCsvFieldSchemaEditModel fieldModel = (OutputCsvFieldSchemaEditModel)fieldNode.getUserObject();
				//--- create field data
				SchemaOutputCsvDataField configField = new SchemaOutputCsvDataField(fieldModel);
				//--- check target value
				SchemaElementValue targetValue = fieldModel.getTargetValue();
				if (targetValue == null) {
					// error : Field has no target
					String msg = "Output field has no target object : " + fieldModel.toParamString();
					throw new IllegalStateException(msg);
				}
				targetValue = (SchemaElementValue)instmap.get(targetValue);
				if (targetValue == null) {
					// error : Field's target is undefined
					String msg = "Output field's target is not defined : " + fieldModel.toParamString();
					throw new IllegalStateException(msg);
				}
				targetValue = new SchemaValueLink(targetValue);
				configField.setTargetValue(targetValue);
				//--- add to table
				configTable.add(configField);
				configField.setParentObject(configTable);
				instmap.put(fieldModel, configField);
			}
			//--- add to table
			config.getOutputTableList().add(configTable);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
