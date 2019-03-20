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
 * @(#)MExecDefEditDialog.java	3.1.0	2014/05/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefEditDialog.java	2.0.0	2012/11/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefEditDialog.java	1.20	2012/03/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefEditDialog.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.macro.AADLMacroEngine;
import ssac.aadl.macro.file.CsvMacroFiles;
import ssac.aadl.macro.graph.AADLMacroGraphData;
import ssac.aadl.macro.graph.swing.AADLMacroGraphDialog;
import ssac.aadl.macro.graph.swing.ExportMacroGraphTask;
import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.ModuleFileType;
import ssac.aadl.module.setting.AadlJarProfile;
import ssac.aadl.module.setting.MacroExecSettings;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.aadl.module.swing.FilenameValidator;
import ssac.falconseed.file.DefaultVirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.file.VirtualFileProgressMonitorTask;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgPublish;
import ssac.falconseed.module.args.MExecArgString;
import ssac.falconseed.module.args.MExecArgSubscribe;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.setting.MExecDefHistory;
import ssac.falconseed.module.setting.MExecDefSettings;
import ssac.falconseed.module.swing.table.IMExecDefArgTableModel;
import ssac.falconseed.module.swing.table.MExecDefArgTablePane;
import ssac.falconseed.module.swing.tree.MEDLocalFileTreePanel;
import ssac.falconseed.module.swing.tree.MEDLocalFileTreePanel.FileTreeStatusBar;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.RunnerResources;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.logging.AppLogger;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.JStaticMultilineTextPane;
import ssac.util.swing.MenuToggleButton;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.menu.ExMenuItem;
import ssac.util.swing.menu.MenuItemResource;
import ssac.util.swing.tree.JTreePopupMenu;

/**
 * モジュール実行定義の編集ダイアログ。
 * モジュール実行定義は，モジュール実行定義名のフォルダ以下に，専用のファイルが格納される。
 * このフォルダには，ユーザー指定のファイルを格納するデータディレクトリがあり，編集中は作業コピーが作成される。
 * 
 * @version 3.1.0	2014/05/18
 */
public class MExecDefEditDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 * モジュール実行定義の編集方法
	 **/
	public enum EditType {
		VIEW,		// 参照(読み込み専用)
		NEW,		// 新規作成
		MODIFY,		// 編集
	};
	
	static private final Dimension DM_MIN_SIZE = new Dimension(600, 480);

	static private final String TREEMENU_ID_NEW_DIR	= "new.directory";
	static private final String TREEMENU_ID_COPY		= "copy";
	static private final String TREEMENU_ID_PASTE		= "paste";
	static private final String TREEMENU_ID_DELETE	= "delete";
	static private final String TREEMENU_ID_MOVETO	= "moveto";
	static private final String TREEMENU_ID_RENAME	= "rename";
	static private final String TREEMENU_ID_REFRESH	= "refresh";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ModuleFileDropTargetListener	_hDroppedModule = new ModuleFileDropTargetListener();
	private final FilenameValidator _validator = FileDialogManager.createDefaultFilenameValidator();

	private TreeContextMenuAction	_treeActionNewDir;
	private TreeContextMenuAction	_treeActionCopy;
	private TreeContextMenuAction	_treeActionPaste;
	private TreeContextMenuAction	_treeActionDelete;
	private TreeContextMenuAction	_treeActionMoveTo;
	private TreeContextMenuAction	_treeActionRename;
	private TreeContextMenuAction	_treeActionRefresh;

	/** モジュール実行定義の編集方法の指定 **/
	private EditType		_editType;
	/** 編集対象のモジュール実行定義データ **/
	private final MExecDefSettings	_data;

	/** モジュール実行定義が配置可能なルートディレクトリの表示名 **/
	private final String		_strRootDisplayName;
	/** モジュール実行定義が配置可能なルートディレクトリ **/
	private final VirtualFile	_vfRootDir;
	/**
	 *  デフォルトパスフォーマッター
	 *  @since 2.0.0
	 */
	private final  VirtualFilePathFormatter	_vformDefault;

	/** 表示用パス文字列フォーマッター **/
	private VirtualFilePathFormatterList	_vfFormatter;
	/** モジュール実行定義が配置される親ディレクトリ **/
	private VirtualFile	_vfParent;
	/** モジュール実行定義ローカルデータディレクトリ **/
	private VirtualFile	_vfFilesRootDir;
	/** モジュール実行定義ローカルデータディレクトリの作業ディレクトリ **/
	private VirtualFile	_vfFilesWorkDir;
	/** 実行対象モジュール **/
	private VirtualFile	_vfModule;
	/** 実行対象モジュール種別 **/
	private ModuleFileType	_typeModuleFile;
	/** AADL実行モジュールのプロファイル **/
	private AadlJarProfile	_moduleProfile;
	/** AADLマクロのプロファイル **/
	private MacroExecSettings	_macroProfile;

	/** 名称編集用テキストフィールド **/
	private JTextField				_cNameField;
	/** 名称表示用テキストコンポーネント **/
	private JStaticMultilineTextPane	_cNameLabel;
	/** 格納位置のパス **/
	private JStaticMultilineTextPane	_stcLocation;
	/** 実行対象のモジュールファイル **/
	private JStaticMultilineTextPane	_stcModuleFile;
	/** 実行対象モジュールのメインクラス名 **/
	private JStaticMultilineTextPane	_stcMainClass;
	/** 引数定義テーブル **/
	private MExecDefArgTablePane	_argTable;
	/** 機能説明のテキストエリア **/
	private JTextArea		_taDesc;
	/** モジュール設定パネル **/
	private JPanel						_paneModule;
	/** ローカルファイルツリーパネル **/
	private MEDLocalFileTreePanel		_paneTree;
	/** スプリッター **/
	private JSplitPane					_paneSplitter;
	/** ファイル情報用ステータスバー **/
	private FileTreeStatusBar			_statusbar;

	/** 位置選択ボタン **/
	private JButton		_btnChooseLocation;
	/** 選択されたモジュールのタイトルを名前に設定するボタン **/
	private JButton		_btnReadModuleTitle;
	/** 選択されたモジュールの機能説明を説明に設定するボタン **/
	private JButton		_btnReadModuleDesc;
	/** モジュール選択ボタン **/
	private JButton		_btnChooseModule;
	/** モジュールのグラフ化ボタン **/
	private JButton		_btnGraphModule;
	/** 引数の編集ボタン **/
	private MenuToggleButton	_btnArgEdit;
	/** 引数の削除ボタン **/
	private JButton		_btnArgDel;
//	/** 実行時の引数履歴削除のチェックボックス **/
//	private JCheckBox		_chkClearArgsHistory;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefEditDialog(Frame owner, EditType type, VirtualFilePathFormatter defaultFormatter, String rootDisplayName, VirtualFile rootDir, VirtualFile target) //throws IOException
	{
		super(owner, true);
		if (rootDir == null)
			throw new NullPointerException("The specified root directory is null.");
		if (!rootDir.exists())
			throw new IllegalArgumentException("The specified root directory does not exists.");
		if (!rootDir.isDirectory())
			throw new IllegalArgumentException("The specified root directory is not directory.");
		if (rootDisplayName != null && rootDisplayName.length() > 0) {
			this._strRootDisplayName = Files.CommonSeparator + rootDisplayName;
		} else {
			this._strRootDisplayName = Files.CommonSeparator + rootDir.getName();
		}
		this._vfRootDir = rootDir;
		this._vformDefault = defaultFormatter;
		this._editType = type;
		this._data = new MExecDefSettings();
		commonConstruction(target);
	}
	
	public MExecDefEditDialog(Dialog owner, EditType type, VirtualFilePathFormatter defaultFormatter, String rootDisplayName, VirtualFile rootDir, VirtualFile target) //throws IOException
	{
		super(owner, true);
		if (rootDir == null)
			throw new NullPointerException("The specified root directory is null.");
		if (!rootDir.exists())
			throw new IllegalArgumentException("The specified root directory does not exists.");
		if (!rootDir.isDirectory())
			throw new IllegalArgumentException("The specified root directory is not directory.");
		if (rootDisplayName != null && rootDisplayName.length() > 0) {
			this._strRootDisplayName = Files.CommonSeparator + rootDisplayName;
		} else {
			this._strRootDisplayName = Files.CommonSeparator + rootDir.getName();
		}
		this._vfRootDir = rootDir;
		this._vformDefault = defaultFormatter;
		this._editType = type;
		this._data = new MExecDefSettings();
		commonConstruction(target);
	}

	private final void commonConstruction(VirtualFile target) //throws IOException
	{
		// check
		this._vfFilesWorkDir = null;
		if (target == null) {
			if (_editType == EditType.VIEW || _editType == EditType.MODIFY) {
				// 参照もしくは編集対象不明
				throw new NullPointerException("The specified target file is null.");
			}
			// 新規作成でターゲット不明の場合は、親をルートに設定
			this._vfParent = _vfRootDir;	// [システム]または[ユーザー]ディレクトリ
			this._vfFilesRootDir = null;
			this._vfFilesWorkDir = null;
		} else {
			VirtualFile vfPrefs = MExecDefFileManager.getModuleExecDefDataFile(target);
			if (_editType == EditType.NEW) {
				//--- この場合の target は、モジュール実行定義を配置する親ディレクトリ
				if (!target.isDirectory()) {
					// 作成する位置がディレクトリではない
					throw new IllegalArgumentException("The specified target file is not directory.");
				}
				if (vfPrefs.exists()) {
					// 作成位置がモジュール実行定義ディレクトリである
					throw new IllegalArgumentException("The specified target file is module execution definition data.");
				}
				this._vfParent = target;
				this._vfFilesRootDir = null;
			} else {
				//--- この場合の target は、対象モジュール実行定義のルートディレクトリ
				if (!target.isDirectory()) {
					// 対象がディレクトリではない
					throw new IllegalArgumentException("The specified target file is not directory.");
				}
				if (!vfPrefs.exists() || !vfPrefs.isFile()) {
					// 対象がモジュール実行定義ではない
					throw new IllegalArgumentException("The specified target file is not module execution definition data.");
				}
				this._vfParent = target.getParentFile();
				//--- load prefs
				this._data.loadForTarget(vfPrefs);
				this._vfFilesRootDir = target.getChildFile(MExecDefFileManager.MEXECDEF_FILES_ROOT_DIRNAME);
			}
		}
	}
	
	@Override
	public void initialComponent() {
		super.initialComponent();
		
		// パスフォーマッターの生成
		_vfFormatter = new VirtualFilePathFormatterList(3);
		if (_vfFilesWorkDir != null) {
			_vfFormatter.add(new DefaultVirtualFilePathFormatter(_vfFilesWorkDir, MExecDefFileManager.MEXECDEF_FILES_ROOT_DIRNAME, false));
		}
		if (_vfFilesRootDir != null) {
			_vfFormatter.add(new DefaultVirtualFilePathFormatter(_vfFilesRootDir, MExecDefFileManager.MEXECDEF_FILES_ROOT_DIRNAME, false));
		}
		if (_vformDefault != null) {
			_vfFormatter.add(_vformDefault);
		}
		
		// 引数定義の相対パス表示のための基準パスを設定
		_argTable.setPathFormatter(_vfFormatter);
		_argTable.setBasePath(_data.getExecDefDirectory());
		
		// 引数定義の表示
		ModuleArgData[] args = _data.getArgumentDataArray();
		if (args != null && args.length > 0) {
			// 引数のパスを変換
			for (ModuleArgData arg : args) {
				if (arg.getType()==ModuleArgType.IN || arg.getType()==ModuleArgType.OUT) {
					Object argValue = arg.getValue();
					if (argValue instanceof VirtualFile) {
						argValue = convertToWorkingPath((VirtualFile)argValue);
						arg.setValue(argValue);
					}
				}
			}
			_argTable.updateTableModelByArgData(args);
		}
		
		// 設定情報の反映
		restoreConfiguration();
	}

	/**
	 * このダイアログコンポーネントのセットアップを行う。
	 * このコンポーネントでは、<code>initialComponent</code> を呼び出さずに、このメソッドを呼び出し初期化を行うこと。
	 * このメソッドでは、ローカルファイルの作業コピーの生成などを行う。
	 * @param parentComponent	メッセージボックスの親コンポーネント
	 * @return	ダイアログが表示可能なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 1.20
	 */
	public boolean setupComponent(Component parentComponent) {
		VirtualFile treeRootDir = null;
		_vfFilesWorkDir = null;
		if (isEditing()) {
			// 編集モードの場合は、作業コピーを生成する。
			SetupWorkingCopyProgressMonitorTask task = new SetupWorkingCopyProgressMonitorTask(_vfFilesRootDir);
			task.execute(parentComponent);
			task.showError(parentComponent);
			if (task.isTerminateRequested()) {
				// ユーザーによってキャンセルされた場合は、ダイアログを表示しない
				return false;
			}
			
			// エラーチェック
			if (task.getErrorCause() != null) {
				if (_editType == EditType.NEW) {
					// 新規作成モードでエラーが発生した場合は、ダイアログを表示しない
					return false;
				}
				
				// 読み込み専用モードで開く
				Application.showWarningMessage(RunnerMessages.getInstance().msgMExecDefOpenForReadOnly);
				_editType = EditType.VIEW;
				treeRootDir = _vfFilesRootDir;
			} else {
				// 作業コピー作成成功
				_vfFilesWorkDir = task.getWorkRootDirectory();
				treeRootDir = _vfFilesWorkDir;
			}
		} else {
			// 読み込み専用なら、作業コピーは作成しない
			treeRootDir = _vfFilesRootDir;
		}
		
		// ダイアログの初期化
		initialComponent();
		
		// ツリーのルート設定
		_paneTree.setRootDirectory(treeRootDir);
		//--- 初期状態ではルートノードを閉じた状態とする
		//_paneTree.getTreeComponent().collapseRow(0);
		return true;
	}

	/**
	 * 作業コピーが残っている場合は、削除する。
	 */
	public void cleanup() {
		if (_vfFilesWorkDir != null && _vfFilesWorkDir.exists()) {
			VirtualFileProgressMonitorTask.deleteAllFiles(_vfFilesWorkDir, true);	// 例外は無視
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

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
	 * モジュール実行定義が配置される場所の親ディレクトリを取得する。
	 * @return	ディレクトリの抽象パス
	 */
	public VirtualFile getParentDirectory() {
		return _vfParent;
	}

	/**
	 * モジュール実行定義を示すディレクトリを取得する。
	 * @return	モジュール実行定義を示すディレクトリを表す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefDirectory() {
		return _data.getExecDefDirectory();
	}

	/**
	 * モジュール実行定義を保持する設定ファイルを取得する。
	 * @return	設定ファイルの抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefPrefsFile() {
		return _data.getVirtualPropertyFile();
	}

	/**
	 * モジュール実行定義編集ダイアログの動作種別を取得する。
	 * @return	動作種別
	 */
	public EditType getEditType() {
		return _editType;
	}

	/**
	 * このモジュール実行定義が新規作成中なら <tt>true</tt> を返す。
	 * @return	新規作成中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 2.0.0
	 */
	public boolean isNewEditing() {
		return (_editType==EditType.NEW);
	}

	/**
	 * このモジュール実行定義が編集中なら <tt>true</tt> を返す。
	 * @return	編集中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isEditing() {
		return (_editType==EditType.NEW || _editType==EditType.MODIFY);
	}

	@Override
	public void restoreConfiguration() {
		super.restoreConfiguration();
		
		// restore divider location
		if (_paneSplitter != null) {
			int dl = getConfiguration().getDividerLocation(getConfigurationPrefix());
			if (dl > 0) {
				_paneSplitter.setDividerLocation(dl);
			}
		}
	}

	@Override
	public void storeConfiguration() {
		super.storeConfiguration();
		
		// store current divider location
		if (_paneSplitter != null) {
			int dl = _paneSplitter.getDividerLocation();
			getConfiguration().setDividerLocation(getConfigurationPrefix(), dl);
		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected void initDialog() {
		super.initDialog();
		
		// update jar module info
		_typeModuleFile = ModuleFileType.UNKNOWN;
		if (_data.isSpecifiedModuleFile()) {
			VirtualFile vfModule = convertToWorkingPath(_data.getModuleFile());
			if (!setModuleFile(vfModule, RunnerMessages.getInstance().MExecDefEditDlg_Label_Module)) {
				this._vfModule = null;
				this._moduleProfile = null;
				this._macroProfile = null;
			}
		}

		// initial display
		setMExecDefName(_data.getName());
		setMExecDefDesc(_data.getDescription());
		setLocationPath(_vfParent);
		redisplayModuleInfo();
		
		// adjust all column width
		_argTable.adjustTableAllColumnsPreferredWidth();
		
		// update buttons
		updateButtons();
		updateTreeContextMenu();
		
		// first focus
		_btnChooseModule.requestFocus();
	}

	/**
	 * [Cancel]ボタン押下時、もしくはダイアログが閉じられたときに呼び出されるイベントハンドラ。
	 * このメソッドが <tt>true</tt> を返すと、ダイアログが閉じられる。
	 * @return	処理が成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	@Override
	protected boolean doCancelAction() {
		// 変更の破棄
		if (_vfFilesWorkDir != null) {
			RollbackProgressMonitorTask task = new RollbackProgressMonitorTask(_vfFilesWorkDir);
			task.execute(this);
			task.showError(this);
		}
		
		// 完了
		return true;
	}

	/**
	 * [OK]ボタン押下時に呼び出されるイベントハンドラ。
	 * このメソッドが <tt>true</tt> を返すと、ダイアログが閉じられる。
	 * @return	処理が成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 */
	@Override
	protected boolean doOkAction() {
		// 念のため、表示専用の場合は無条件で閉じる
		if (getEditType() == EditType.VIEW) {
			// 変更の破棄
			return doCancelAction();
		}
		
		VirtualFile vfTargetDir;
		VirtualFile vfTargetPrefs;
		
		// 名前のチェック
		if (getEditType() == EditType.NEW) {
			if (!checkFilename()) {
				return false;
			}
			vfTargetDir = _vfParent.getChildFile(_cNameField.getText());
			vfTargetPrefs = MExecDefFileManager.getModuleExecDefDataFile(vfTargetDir);
		} else {
			vfTargetDir = _data.getExecDefDirectory();
			vfTargetPrefs = _data.getVirtualPropertyFile();
		}
		
		// モジュールのチェック
		if (_vfModule == null) {
			String errmsg = RunnerMessages.getInstance().msgMExecDefNothingModule;
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		else if (!_vfModule.exists()) {
			String errmsg = RunnerMessages.getInstance().msgMExecDefModuleNotFound;
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		// 実行時引数のチェック
		if (!checkArguments()) {
			return false;
		}
		
		// 書き込み可能かチェック
		if (!checkWritable(vfTargetDir, vfTargetPrefs)) {
			return false;
		}
		
		// モジュール実行定義の保存
		//--- 保存に失敗した場合もダイアログを閉じる
		doSaveSettings(vfTargetDir, vfTargetPrefs);
		return true;
	}

	/**
	 * フィルタの配置フォルダ選択ボタンをクリックしたときのイベント
	 * @since 2.0.0
	 */
	protected void onChooseLocationButton(ActionEvent ae) {
		// 編集モードのチェック
		if (!isNewEditing()) {
			throw new IllegalStateException("MExecDefEditDialog#onChooseLocationButton : Edit mode is not NEW : editMode=" + String.valueOf(getEditType()));
		}

		// 親フォルダの選択
		RunnerFrame mainFrame = (RunnerFrame)ModuleRunner.getApplicationMainFrame();
		VirtualFile vfDir = mainFrame.chooseMExecDefFolder(this,
									true,				// フォルダ作成を許可
									false,				// システムルートは表示しない
									RunnerMessages.getInstance().MExecDefFolderChooser_Title,	// タイトル
									null,				// 説明は表示しない
									_vfParent);			// 初期選択位置
		if (vfDir == null) {
			// user canceled
			return;
		}
		
		// 親フォルダの保存
		if (!vfDir.equals(_vfParent)) {
			_vfParent = vfDir;
			setLocationPath(vfDir);
		}
	}
	
	protected void onChooseModuleButton(ActionEvent ae) {
		// 初期選択ファイルの指定
		File initialFile;
		if (_vfModule != null && !_vfModule.isDescendingFrom(_vfRootDir)) {
			initialFile = FileChooserManager.getInitialDocumentFile(_vfModule);
		} else {
			initialFile = FileChooserManager.getInitialDocumentFile(null);
		}
		
		// ファイルの選択
		File selectedFile = FileChooserManager.chooseJarFile(this, initialFile,
								RunnerMessages.getInstance().MExecDefEditDlg_Title_ModuleChooser);
		if (selectedFile != null) {
			FileChooserManager.setLastChooseDocumentFile(selectedFile);
			VirtualFile vfile = ModuleFileManager.fromJavaFile(selectedFile).getNormalizedFile().getAbsoluteFile();
			setModuleFile(vfile);
		}
	}
	
	protected void onReadModuleTitle(ActionEvent ae) {
		if (_moduleProfile != null) {
			setMExecDefName(_moduleProfile.getTitle());
		}
		else if (_macroProfile != null) {
			setMExecDefName(_macroProfile.getTitle());
		}
	}
	
	protected void onReadModuleDesc(ActionEvent ae) {
		if (_moduleProfile != null) {
			setMExecDefDesc(_moduleProfile.getDescription());
		}
		else if (_macroProfile != null) {
			setMExecDefDesc(_macroProfile.getDescription());
		}
	}
	
	protected void onGraphModule(ActionEvent ae) {
		if (_macroProfile == null)
			return;		// no macro
		
		// GraphViz 実行ファイルの取得
		String strGraphVizPath = AppSettings.getInstance().getGraphVizPath();
		if (Strings.isNullOrEmpty(strGraphVizPath)) {
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgMExecDefGraphVizDotNotSet);
			return;
		}
		File fGraphViz = new File(strGraphVizPath);
		try {
			if (!(fGraphViz.exists() && fGraphViz.isFile() && fGraphViz.canRead())) {
				String errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgMExecDefGraphVizCannotAccess, null, fGraphViz.getAbsolutePath());
				Application.showErrorMessage(this, errmsg);
				return;
			}
		} catch (Throwable ex) {
			String errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgMExecDefGraphVizCannotAccess, ex, fGraphViz.getAbsolutePath());
			Application.showErrorMessage(this, errmsg);
			return;
		}
		
		// GraphViz の実行
		ExportMacroGraphTask task = new ExportMacroGraphTask(RunnerMessages.getInstance().MExecDefEditDlg_Graph_ProgressTitle, fGraphViz, _vfModule, AppSettings.getInstance().getAadlMacroEncodingName());
		if (!task.execute(this) || !task.isSucceeded()) {
			task.showError(this);
			return;
		}
		AADLMacroGraphData graphData = new AADLMacroGraphData(task.getAADLMacroFile());
		graphData.setDotFile(ModuleFileManager.fromJavaFile(task.getGraphDotFile()));
		graphData.setImageFile(ModuleFileManager.fromJavaFile(task.getGraphImageFile()));
		graphData.setGraphImage(task.getGraphImage());
		
		// グラフのダイアログ表示
		AADLMacroGraphDialog dlg = new AADLMacroGraphDialog(this, RunnerMessages.getInstance().AADLMacroGraphDlg_Title, true, graphData);
		dlg.initialComponent();
		dlg.setVisible(true);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void redisplayModuleInfo() {
		String path = null;
		String mainClass = null;
		if (_vfModule != null) {
			path = formatLocalFilePath(_vfModule);
			if (_macroProfile != null) {
				mainClass = AADLMacroEngine.class.getName();
			}
			else if (_moduleProfile != null) {
				mainClass = _moduleProfile.getMainClass();
			}
		}
		_stcModuleFile.setText(displayString(path));
		_stcMainClass.setText(displayString(mainClass));
	}
	
	protected void updateButtons() {
		if (_macroProfile != null) {
			_btnReadModuleTitle.setEnabled(getNameTextComponent().isEditable());
			_btnReadModuleDesc .setEnabled(_taDesc.isEditable());
			_btnGraphModule.setEnabled(true);
			_btnGraphModule.setVisible(true);
		}
		else if (_moduleProfile != null) {
			_btnReadModuleTitle.setEnabled(getNameTextComponent().isEditable());
			_btnReadModuleDesc .setEnabled(_taDesc.isEditable());
			_btnGraphModule.setVisible(false);
			_btnGraphModule.setEnabled(false);
		} else {
			_btnReadModuleTitle.setEnabled(false);
			_btnReadModuleDesc .setEnabled(false);
			_btnGraphModule.setVisible(false);
			_btnGraphModule.setEnabled(false);
		}
		
		_argTable.updateButtons();
	}
	
	protected String getMExecDefName() {
		return getNameTextComponent().getText();
	}
	
	protected void setMExecDefName(String text) {
		getNameTextComponent().setText(displayString(text));
	}
	
	protected String getMExecDefDesc() {
		return _taDesc.getText();
	}
	
	protected void setMExecDefDesc(String text) {
		_taDesc.setText(displayString(text));
		_taDesc.setCaretPosition(0);
	}
	
	protected void setLocationPath(VirtualFile file) {
		_stcLocation.setText(formatFilePath(file));
	}
	
	protected boolean setModuleFile(VirtualFile vfTarget) {
		if (!setModuleFile(vfTarget, CommonMessages.getInstance().labelFile)) {
			return false;
		}
		
		// モジュール情報の表示更新
		redisplayModuleInfo();
		updateButtons();
		
		if (_macroProfile != null) {
			// AADLマクロモジュール
			//--- 引数情報の更新
			_argTable.updateTableModelByArgDetails(_macroProfile.getArgumentDetails());
			//--- 実行定義名が入力されていなければ、モジュールの名前を利用
			if (Strings.isNullOrEmpty(getMExecDefName())) {
				setMExecDefName(_macroProfile.getTitle());
			}
			//--- 説明が入力されていなければ、モジュールの説明を利用
			if (Strings.isNullOrEmpty(getMExecDefDesc())) {
				setMExecDefDesc(_macroProfile.getDescription());
			}
		}
		else {
			// AADL JAR モジュール
			// AADLマクロモジュール
			//--- 引数情報の更新
			_argTable.updateTableModelByArgDetails(_moduleProfile.getArgumentDetails());
			//--- 実行定義名が入力されていなければ、モジュールの名前を利用
			if (Strings.isNullOrEmpty(getMExecDefName())) {
				setMExecDefName(_moduleProfile.getTitle());
			}
			//--- 説明が入力されていなければ、モジュールの説明を利用
			if (Strings.isNullOrEmpty(getMExecDefDesc())) {
				setMExecDefDesc(_moduleProfile.getDescription());
			}
		}
		
		// 更新完了
		return true;
	}
	
	protected boolean setModuleFile(VirtualFile vfTarget, String errmsgPrefix) {
		// ファイルの有無を確認
		if (!vfTarget.exists()) {
			// 存在しない
			String errmsg = CommonMessages.formatErrorMessage(
								String.format(RunnerMessages.getInstance().msgMExecDefTargetNotFound, errmsgPrefix),
								null, vfTarget.getPath());
			AppLogger.error(errmsg);
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		// 拡張子のチェック
		ModuleFileType mtype = ModuleFileType.UNKNOWN;
		AadlJarProfile jarprop = null;
		MacroExecSettings macroprop = null;
		if (ModuleFileManager.isJarFile(vfTarget)) {
			// Jar ファイル
			try {
				jarprop = new AadlJarProfile(vfTarget);
			}
			catch (Throwable ex) {
				String errmsg = CommonMessages.formatErrorMessage(
									String.format(RunnerMessages.getInstance().msgMExecDefTargetNotJar, errmsgPrefix),
									ex, vfTarget.getPath());
				AppLogger.error(errmsg, ex);
				Application.showErrorMessage(this, errmsg);
				return false;
			}
			if (!jarprop.hasProperties()) {
				// AADL実行モジュールではない(AADLコンパイラでコンパイルされていない)
				String errmsg = CommonMessages.formatErrorMessage(
									String.format(RunnerMessages.getInstance().msgMExecDefTargetNotAadlJar, errmsgPrefix),
									null, vfTarget.getPath());
				AppLogger.error(errmsg);
				Application.showErrorMessage(this, errmsg);
				return false;
			}
			mtype = ModuleFileType.AADL_JAR;
		}
		else if (ModuleFileManager.isAadlMacroFile(vfTarget)) {
			// AADL マクロファイル
			if (!CsvMacroFiles.isCsvMacroFile(ModuleFileManager.toJavaFile(vfTarget), AppSettings.getInstance().getAadlMacroEncodingName())) {
				// AADL マクロファイルではない
				String errmsg = CommonMessages.formatErrorMessage(
									String.format(RunnerMessages.getInstance().msgMExecDefTargetNotAadlMacro, errmsgPrefix),
									null, vfTarget.getPath());
				AppLogger.error(errmsg);
				Application.showErrorMessage(this, errmsg);
				return false;
			}
			//--- ローカルファイルのマクロかをチェック
			if (!isLocalDataFile(vfTarget)) {
				// ローカルファイルの AADL マクロではない
				String errmsg = RunnerMessages.getInstance().msgMEXecDefTargetNotLocalMacro;
				AppLogger.error(CommonMessages.formatErrorMessage(errmsg, null, vfTarget.getAbsolutePath(),
						(_vfFilesRootDir==null ? _vfFilesWorkDir.getAbsolutePath() : _vfFilesRootDir.getAbsolutePath())));
				Application.showErrorMessage(this, errmsg);
			}
			//--- AADL マクロ設定ファイルを読み込む
			macroprop = new MacroExecSettings();
			macroprop.loadForTarget(vfTarget);
			mtype = ModuleFileType.AADL_MACRO;
		}
		else {
			// 実行可能なファイルではない
			// jar ファイルじゃない
			String errmsg = CommonMessages.formatErrorMessage(
								String.format(RunnerMessages.getInstance().msgMExecDefModuleNotExecutable, errmsgPrefix),
								null, vfTarget.getPath());
			AppLogger.error(errmsg);
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		// completed
		this._vfModule = vfTarget;
		this._moduleProfile = jarprop;
		this._macroProfile  = macroprop;
		this._typeModuleFile = mtype;
		return true;
	}
	
	protected final String displayString(String text) {
		return (text == null ? "" : text);
	}
	
	protected String formatFilePath(VirtualFile file) {
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
	
	protected String formatLocalFilePath(VirtualFile file) {
		if (!file.isAbsolute()) {
			file = file.getAbsoluteFile();
		}
		
		if (_vfFormatter != null) {
			String path = _vfFormatter.formatPath(file);
			if (path != null)
				return path;
		}

		VirtualFile vfExecDefDir = _data.getExecDefDirectory();
		if (vfExecDefDir != null && file.isDescendingFrom(vfExecDefDir)) {
			return file.relativePathFrom(vfExecDefDir, Files.CommonSeparatorChar);
		} else {
			return file.getPath();
		}
	}
	
	protected String formatArgNo(int rowIndex) {
		return String.format("($%d)", rowIndex+1);
	}
	
	protected String formatArgType(int rowIndex, ModuleArgType type) {
		return String.format("($%d) %s", rowIndex+1, type.toString());
	}

	/**
	 * モジュール実行定義名の正当性をチェックする。
	 * @return	モジュール実行定義名で新しいディレクトリが作成可能な場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	protected boolean checkFilename() {
		// validator に、存在するファイル名を設定
		_validator.setAlreadyExistsFilenames(_vfParent);
		
		// verify
		boolean ret = _validator.verify(_cNameField, _cNameField.getText());
		if (!ret) {
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					_cNameField.requestFocusInWindow();
					_cNameField.selectAll();
				}
			});
		}
		return ret;
	}

	/**
	 * 実行時引数設定の正当性をチェックする。
	 * @return	実行時引数設定が正当な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean checkArguments() {
		IMExecDefArgTableModel model = _argTable.getTableModel();
		int rowCount = model.getRowCount();
		if (rowCount <= 0) {
			// 引数設定は存在しないので、正しいとする
			return true;
		}
		
		// 引数の型が指定されているかをチェックする
		for (int row = 0 ; row < rowCount; row++) {
			ModuleArgType type = model.getArgumentAttr(row);
			if (type == null || ModuleArgType.NONE == type) {
				String errmsg = formatArgNo(row) + " " + RunnerMessages.getInstance().msgMExecDefArgNothingAttr;
				Application.showErrorMessage(errmsg);
				return false;
			}
		}
		
		// 引数の値のチェック
		for (int row = 0 ; row < rowCount; row++) {
			ModuleArgType type = model.getArgumentAttr(row);
			Object val = model.getArgumentValue(row);
			if (val instanceof VirtualFile) {
				// Real file
				if (ModuleArgType.OUT == type) {
					String errmsg = formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseFixedOutput;
					Application.showErrorMessage(errmsg);
					return false;
				}
				else if (ModuleArgType.IN != type) {
					String errmsg = formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseFile;
					Application.showErrorMessage(errmsg);
					return false;
				}
				else {
					// type == ModuleArgType.IN
					//--- 存在確認
					VirtualFile file = (VirtualFile)val;
					boolean exists;
					try {
						exists = file.exists();
					}
					catch (SecurityException ex) {
						exists = false;
					}
					if (!exists) {
						String errmsg = formatArgType(row, type) + " " + CommonMessages.getInstance().msgFileNotFound;
						Application.showErrorMessage(errmsg);
						return false;
					}
				}
			}
			else if (val instanceof IMExecArgParam) {
				// Parameter
				if (val instanceof MExecArgString) {
					if (ModuleArgType.STR != type) {
						String errmsg = formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseString;
						Application.showErrorMessage(errmsg);
						return false;
					}
				}
				else if (val instanceof MExecArgPublish) {
					if (ModuleArgType.PUB != type) {
						String errmsg = formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUsePubParam;
						Application.showErrorMessage(errmsg);
						return false;
					}
				}
				else if (val instanceof MExecArgSubscribe) {
					if (ModuleArgType.SUB != type) {
						String errmsg = formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseSubParam;
						Application.showErrorMessage(errmsg);
						return false;
					}
				} else {
					if (ModuleArgType.IN != type && ModuleArgType.OUT != type) {
						String errmsg = formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseFile;
						Application.showErrorMessage(errmsg);
						return false;
					}
					if (val instanceof MExecArgTempFile) {
						if (ModuleArgType.IN == type) {
							String errmsg = formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseTempFile;
							Application.showErrorMessage(errmsg);
							return false;
						}
					}
				}
			}
			else {
				// String
				if (val == null || val.toString().length() < 1) {
					String errmsg = formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgNothingValue;
					Application.showErrorMessage(errmsg);
					return false;
				}
				
				if (ModuleArgType.STR != type && ModuleArgType.PUB != type && ModuleArgType.SUB != type) {
					String errmsg = formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseString;
					Application.showErrorMessage(errmsg);
					return false;
				}
			}
		}
		
		// 正当
		return true;
	}

	/**
	 * 指定のパスへの書き込み権限があるかを判定する。
	 * @param file	判定する抽象パス
	 * @return	書き込み可能なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected boolean checkWritable(VirtualFile file) {
		boolean canWritable;
		try {
			canWritable = file.canWrite();
		} catch (Throwable ex) {
			canWritable = false;
		}
		
		if (!canWritable) {
			String errmsg = CommonMessages.formatErrorMessage(CommonMessages.getInstance().msgNotHaveWritePermission, null, formatFilePath(file));
			AppLogger.error(errmsg);
			Application.showErrorMessage(errmsg);
		}
		return canWritable;
	}

	/**
	 * モジュール実行定義に含まれる全てのファイル、ならびに、作業コピーに含まれる全てのファイルが変更可能かをチェックする。
	 * @param vfExecDefDir	モジュール実行定義のルートディレクトリ
	 * @param vfFilesDir	モジュール実行定義のローカルファイルルートディレクトリ
	 * @param vfWorkDir		ローカルファイルの作業コピー
	 * @return	変更可能なら <tt>true</tt>、そうでない場合は変更不可能なファイルを示す抽象パス
	 */
	protected VirtualFile checkModifiable(final VirtualFile vfExecDefDir, final VirtualFile vfFilesDir, final VirtualFile vfWorkDir) {
		VirtualFile unmodifiable;
		
		// 作業コピーが変更可能かを確認
		VirtualFile skipFolder;
		if (vfWorkDir != null) {
			unmodifiable = VirtualFileProgressMonitorTask.checkModifiableAllFiles(vfWorkDir);
			if (unmodifiable != null) {
				return unmodifiable;
			}
			skipFolder = vfWorkDir;		// 作業コピーは変更可能なため、次のチェックではスキップ
		} else {
			skipFolder = vfFilesDir;	// 作業コピーは存在しないため、ローカルファイルディレクトリは変更しない
		}
		
		// モジュール実行定義フォルダ内の全てのファイルが変更可能かを確認
		if (vfExecDefDir.exists() && vfExecDefDir.isDirectory()) {
			VirtualFile[] files = vfExecDefDir.listFiles();
			for (VirtualFile svf : files) {
				if (!svf.equals(skipFolder)) {
					unmodifiable = VirtualFileProgressMonitorTask.checkModifiableAllFiles(svf);
					if (unmodifiable != null)
						return unmodifiable;
				}
			}
		}
		
		// 全て変更可能
		return null;
	}

	/**
	 * 指定のパスへの書き込み権限があるかを判定する。
	 * @param vfTargetDir		モジュール実行定義の格納ディレクトリ
	 * @param vfTargetPrefs		モジュール実行定義ファイル
	 * @return	書き込み可能なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean checkWritable(VirtualFile vfTargetDir, VirtualFile vfTargetPrefs) {
		// 親ディレクトリの書き込み可能チェック
		if (!checkWritable(_vfParent)) {
			return false;
		}
		
		// 格納ディレクトリのチェック
		if (!vfTargetDir.exists()) {
			return true;	// 存在しないので、書き込み可能
		}
		if (!vfTargetDir.isDirectory()) {
			String errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgMExecDefNotDirectory, null, vfTargetDir.toString());
			AppLogger.error(errmsg);
			Application.showErrorMessage(errmsg);
			return false;
		}
		if (!checkWritable(vfTargetDir)) {
			return false;
		}
		
		// 実行定義ファイルのチェック
		if (!vfTargetPrefs.exists()) {
			return true;	// 存在しないので、書き込み可能
		}
		if (!vfTargetPrefs.isFile()) {
			String errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgMExecDefPrefsNotFile, null, vfTargetPrefs.toString());
			AppLogger.error(errmsg);
			Application.showErrorMessage(errmsg);
			return false;
		}
		if (!checkWritable(vfTargetPrefs)) {
			return false;
		}
		
		// モジュール実行定義内ファイルの変更可能かのチェック
		VirtualFile unmodifiable = checkModifiable(vfTargetDir, _vfFilesRootDir, _vfFilesWorkDir);
		if (unmodifiable != null) {
			// 変更不可能なファイルあり（開かれている）
			String errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgNotModifiableFile, null, unmodifiable.getAbsolutePath());
			AppLogger.error(errmsg);
			Application.showErrorMessage(errmsg);
			return false;
		}
		
		// 書き込み可能
		return true;
	}

	/**
	 * 作業コピーのパスを正規のパスに変換する。作業コピーに格納されていない場合は変換しない。
	 * @param target		変換対象のパス
	 * @param filesRootDir	ローカルデータディレクトリ
	 * @param workRootDir	ローカルデータ作業ルートディレクトリ
	 * @return	変換後のパス
	 * @since 1.20
	 */
	static protected VirtualFile convertToCanonicalPath(VirtualFile target, VirtualFile filesRootDir, VirtualFile workRootDir) {
		VirtualFile vfResult = target;
		if (workRootDir != null && target.isDescendingFrom(workRootDir)) {
			VirtualFile vf = target.relativeFileFrom(workRootDir);
			if (!vf.isAbsolute()) {
				vfResult = filesRootDir.getChildFile(vf.getPath());
			}
		}
		return vfResult;
	}
	
	/**
	 * 作業コピーのパスを正規のパスに変換する。作業コピーに格納されていない場合は変換しない。
	 * @param target		変換対象のパス
	 * @return	変換後のパス
	 * @since 1.20
	 */
	protected VirtualFile convertToCanonicalPath(VirtualFile target) {
		return convertToCanonicalPath(target, _vfFilesRootDir, _vfFilesWorkDir);
	}

	/**
	 * 正規のパスを作業コピーのパスに変換する。正規のパスに格納されていない場合は変換しない。
	 * ローカルデータ作業ディレクトリが <tt>null</tt> の場合も変換しない。
	 * @param target		変換対象のパス
	 * @param filesRootDir	ローカルデータディレクトリ
	 * @param workRootDir	ローカルデータ作業ルートディレクトリ
	 * @return	変換後のパス
	 * @since 1.20
	 */
	static protected VirtualFile convertToWorkingPath(VirtualFile target, VirtualFile filesRootDir, VirtualFile workRootDir) {
		VirtualFile vfResult = target;
		if (workRootDir != null && target.isDescendingFrom(filesRootDir)) {
			VirtualFile vf = target.relativeFileFrom(filesRootDir);
			if (!vf.isAbsolute()) {
				target = workRootDir.getChildFile(vf.getPath());
			}
		}
		return vfResult;
	}
	
	/**
	 * 正規のパスを作業コピーのパスに変換する。正規のパスに格納されていない場合は変換しない。
	 * ローカルデータ作業ディレクトリが <tt>null</tt> の場合も変換しない。
	 * @param target		変換対象のパス
	 * @return	変換後のパス
	 * @since 1.20
	 */
	protected VirtualFile convertToWorkingPath(VirtualFile target) {
		return convertToWorkingPath(target, _vfFilesRootDir, _vfFilesWorkDir);
	}

	/**
	 * 指定されたファイルがローカルデータディレクトリもしくはローカルデータ作業ディレクトリ以下のファイルかを判定する。
	 * @param target		判定対象のパス
	 * @param filesRootDir	ローカルデータディレクトリ
	 * @param workRootDir	ローカルデータ作業ディレクトリ
	 * @return	ローカルデータであれば <tt>true</tt>，そうでない場合は <tt>false</tt>
	 * @since 1.20
	 */
	static protected boolean isLocalDataFile(VirtualFile target, VirtualFile filesRootDir, VirtualFile workRootDir) {
		if (filesRootDir != null) {
			if (target.isDescendingFrom(filesRootDir)) {
				return true;
			}
		}
		
		if (workRootDir != null) {
			if (target.isDescendingFrom(workRootDir)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 指定されたファイルがローカルデータディレクトリもしくはローカルデータ作業ディレクトリ以下のファイルかを判定する。
	 * @param target		判定対象のパス
	 * @return	ローカルデータであれば <tt>true</tt>，そうでない場合は <tt>false</tt>
	 * @since 1.20
	 */
	protected boolean isLocalDataFile(VirtualFile target) {
		return isLocalDataFile(target, _vfFilesRootDir, _vfFilesWorkDir);
	}

	/**
	 * 現在のモジュール実行定義を指定のパスへ保存する。
	 * この保存では、関連するファイルも保存される。
	 * @param vfTargetDir		モジュール実行定義の格納ディレクトリ
	 * @param vfTargetPrefs		モジュール実行定義ファイル
	 */
	protected void doSaveSettings(VirtualFile vfTargetDir, VirtualFile vfTargetPrefs) {
		Map<VirtualFile,VirtualFile> copyFiles = new LinkedHashMap<VirtualFile, VirtualFile>();
		Set<VirtualFile> retainFiles = new LinkedHashSet<VirtualFile>();
		VirtualFile vfUserRootDir;
		if (_vfFilesRootDir == null) {
			vfUserRootDir = vfTargetDir.getChildFile(MExecDefFileManager.MEXECDEF_FILES_ROOT_DIRNAME);
		} else {
			vfUserRootDir = _vfFilesRootDir;
		}
		
		// 消去禁止ファイルにモジュール実行定義格納ディレクトリを追加
		retainFiles.add(vfTargetDir);
		
		// 消去禁止ファイルにモジュール実行定義ファイルを追加
		retainFiles.add(vfTargetPrefs);
		
		// 消去禁止ファイルにローカルファイルルートを追加
		retainFiles.add(vfUserRootDir);
		retainFiles.add(_vfFilesWorkDir);
		
		// 実行モジュールのファイル情報(マクロ対応)
		VirtualFile vfOldFile = _data.getModuleFile();
		VirtualFile vfNormModule = convertToCanonicalPath(_vfModule, vfUserRootDir, _vfFilesWorkDir);
		if (vfNormModule.isDescendingFrom(vfUserRootDir)) {
			//--- ローカルファイル配下の実行モジュール
			_data.setModuleFile(vfNormModule);
		}
		else if (vfOldFile == null || !vfNormModule.equals(vfOldFile)) {
			//--- 格納ディレクトリ直下に保存
			VirtualFile vfNewFile = vfTargetDir.getChildFile(vfNormModule.getName());
			copyFiles.put(vfNormModule, vfNewFile);
			retainFiles.add(vfNewFile);
			_data.setModuleFile(vfNewFile);
			VirtualFile vfPrefsFile = ModuleFileManager.getPrefsFile(vfNormModule);
			if (vfPrefsFile.exists()) {
				VirtualFile vfNewPrefs = ModuleFileManager.getPrefsFile(vfNewFile);
				copyFiles.put(vfPrefsFile, vfNewPrefs);
				retainFiles.add(vfNewPrefs);
			}
		}
		else {
			//--- 実行モジュールの変更なし
			retainFiles.add(vfOldFile);
			retainFiles.add(ModuleFileManager.getPrefsFile(vfOldFile));
		}
		
		// データファイル(マクロ対応)
		VirtualFile vfArgsDir = vfTargetDir.getChildFile(MExecDefFileManager.MEXECDEF_MODULEARG_DIRNAME);
		IMExecDefArgTableModel model = _argTable.getTableModel();
		_data.clearArguments();
		int rowCount = model.getRowCount();
		for (int row = 0; row < rowCount; row++) {
			//--- ファイルの判定
			ModuleArgType argtype = model.getArgumentAttr(row);
			String        argdesc = model.getArgumentDescription(row);
			Object        argval  = model.getArgumentValue(row);
			if (argval instanceof VirtualFile) {
				VirtualFile vfArgFile = convertToCanonicalPath((VirtualFile)argval, vfUserRootDir, _vfFilesWorkDir);
				if (!vfArgFile.isDescendingFrom(vfUserRootDir) && !vfArgFile.isDescendingFrom(vfArgsDir)) {
					//--- 新規格納ファイル('$' + 引数番号 + '_' + ファイル名)
					VirtualFile vfOrgFile = vfArgFile;
					vfArgFile = vfArgsDir.getChildFile(String.format("$%d_%s", (row+1), vfOrgFile.getName()));
					copyFiles.put(vfOrgFile, vfArgFile);
				}
				argval = vfArgFile;
				retainFiles.add(vfArgFile);
			}
			//--- 引数設定の保存
			_data.addArgument(new ModuleArgData(argtype, argdesc, argval));
		}
		
		// プロパティの更新
//		//--- name
//		_data.setName(getNameTextComponent().getText());
		//--- desc
		_data.setDescription(_taDesc.getText());
		//--- module file type
		_data.setModuleFileType(_typeModuleFile);
		//--- mainclass-name
		_data.setModuleMainClass(_stcMainClass.getText());
		//--- 保存先ファイル名
		_data.setPropertyTarget(vfTargetPrefs);
		
		// ファイルの保存
//		FileSaveProgressMonitorTask task = new FileSaveProgressMonitorTask(_data, copyFiles, retainFiles);
		CommitProgressMonitorTask task = new CommitProgressMonitorTask(vfUserRootDir, _vfFilesWorkDir,
																			_data, copyFiles, retainFiles);
		task.execute(this);
		task.showError(this);
	}

	//------------------------------------------------------------
	// Internal methods (for Setup contents)
	//------------------------------------------------------------
	
	@Override
	protected void setupMainContents() {
		// create components
		createComponents();
		
		// setup component state
		switch (getEditType()) {
			case VIEW :
				{
					_cNameField = null;
					_cNameLabel = createNameLabel();
					_btnChooseLocation.setVisible(false);
					_btnChooseModule.setVisible(false);
					_btnArgEdit.setVisible(false);
					_btnArgDel.setVisible(false);
					_btnReadModuleTitle.setVisible(false);
					_btnReadModuleDesc.setVisible(false);
					_argTable.setEditable(false);	// read only
					_taDesc.setEditable(false);		// readonly
					setTitle(RunnerMessages.getInstance().MExecDefEditDlg_Title_VIEW);
				}
				break;
			case NEW :
				{
					_cNameField = createNameTextField();
					_cNameLabel = null;
					_btnChooseLocation.setVisible(true);
					_btnChooseModule.setVisible(true);
					_btnArgEdit.setVisible(true);
					_btnArgDel.setVisible(true);
					_btnReadModuleTitle.setVisible(true);
					_btnReadModuleDesc.setVisible(true);
					setTitle(RunnerMessages.getInstance().MExecDefEditDlg_Title_NEW);
				}
				break;
			default :
				{
					_cNameField = null;
					_cNameLabel = createNameLabel();
					_btnChooseLocation.setVisible(false);
					_btnChooseModule.setVisible(true);
					_btnArgEdit.setVisible(true);
					_btnArgDel.setVisible(true);
					_btnReadModuleTitle.setVisible(false);
					_btnReadModuleDesc.setVisible(true);
					setTitle(RunnerMessages.getInstance().MExecDefEditDlg_Title_MODIFY);
				}
		}

		// layout
		layoutComponents();
	}
	
	protected void layoutComponents() {
		// ラベルを生成
		JLabel lblName      = new JLabel(RunnerMessages.getInstance().MExecDefEditDlg_Label_Name + " :");
		JLabel lblLocation  = new JLabel(RunnerMessages.getInstance().MExecDefEditDlg_Label_Location + " :");
		JLabel lblArgs      = new JLabel(RunnerMessages.getInstance().MExecDefEditDlg_Label_Args + " :");
		JLabel lblDesc      = new JLabel(RunnerMessages.getInstance().MExecDefEditDlg_Label_Desc + " :");
		
		// 引数テーブルパネル
		JPanel pnlArgs = createModuleArgsTablePanel();
		
		// 引数ラベルパネル
		JPanel pnlArgTitle = new JPanel(new GridBagLayout());
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.fill = GridBagConstraints.NONE;
			//--- label
			gbc.anchor = GridBagConstraints.SOUTHWEST;
			pnlArgTitle.add(lblArgs, gbc);
			//--- glue
			gbc.gridx++;
			gbc.anchor = GridBagConstraints.SOUTHEAST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			pnlArgTitle.add(new JLabel(), gbc);
//			//--- checkbox
//			if (_editType == EditType.MODIFY) {
//				// 編集時のみ表示
//				gbc.gridx++;
//				gbc.fill = GridBagConstraints.NONE;
//				gbc.weightx = 0;
//				pnlArgTitle.add(_chkClearArgsHistory, gbc);
//			}
		}
		
		// スクロールペインを設定
		//--- desc
		JScrollPane scDesc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
											JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scDesc.setViewportView(_taDesc);
		
		// create main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		//--- initialize
		Insets vertSpacing = new Insets(2, 0, 0, 0);
		Insets vertSeparate = new Insets(8, 0, 0, 0);
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weighty = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		//--- name
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		mainPanel.add(lblName, gbc);
		gbc.gridx++;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		mainPanel.add(_btnReadModuleTitle, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.insets = vertSpacing;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(getNameTextComponent(), gbc);
		gbc.insets = vertSeparate;
		gbc.gridy++;
		//--- location
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		mainPanel.add(lblLocation, gbc);
		gbc.gridy++;
		{
			JPanel locpnl = new JPanel(new GridBagLayout());
			GridBagConstraints lgbc = new GridBagConstraints();
			lgbc.gridwidth = 1;
			lgbc.gridheight = 1;
			lgbc.gridx = 0;
			lgbc.gridy = 0;
			lgbc.weightx = 1;
			lgbc.weighty = 1;
			lgbc.anchor = GridBagConstraints.NORTHWEST;
			lgbc.fill = GridBagConstraints.HORIZONTAL;
			locpnl.add(_stcLocation, lgbc);
			lgbc.gridx++;
			lgbc.insets = new Insets(0,2,0,0);
			lgbc.weightx = 0;
			lgbc.anchor = GridBagConstraints.NORTHEAST;
			lgbc.fill = GridBagConstraints.NONE;
			locpnl.add(_btnChooseLocation, lgbc);
			gbc.gridx = 0;
			gbc.gridwidth = 3;
			gbc.weightx = 1;
			gbc.insets = vertSpacing;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			mainPanel.add(locpnl, gbc);
		}
		gbc.insets = vertSeparate;
		gbc.gridy++;
		//--- module
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(_paneModule, gbc);
		gbc.insets = vertSeparate;
		gbc.gridy++;
		//--- args
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		mainPanel.add(lblArgs, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.insets = vertSpacing;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(pnlArgs, gbc);
		gbc.insets = vertSeparate;
		gbc.weighty = 0;
		gbc.gridy++;
		//--- desc
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		mainPanel.add(lblDesc, gbc);
		gbc.gridx++;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		mainPanel.add(_btnReadModuleDesc, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.insets = vertSpacing;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(scDesc, gbc);
		gbc.insets = vertSeparate;
		gbc.weighty = 0;
		gbc.gridy++;
		
		// adjust panel size
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		//	GridBagLayout では、複数のコンポーネントで余った領域を分け合うとき、
		//	Component#getMinimumSize() や Component#getPrefferedSize() が返す
		//	値が大きいと、そのコンポーネントの実サイズが preffered を超えたときに、
		//	そのサイズが優先され、余分領域の配分が異なってしまう。
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		Dimension dmSize = new Dimension(50,50);
		_argTable.setMinimumSize(dmSize);
		_argTable.setPreferredSize(dmSize);
		scDesc.setMinimumSize(dmSize);
		scDesc.setPreferredSize(dmSize);
		pnlArgs.setMinimumSize(dmSize);
		pnlArgs.setPreferredSize(dmSize);
		//---
		dmSize = new Dimension(300, 200);
		mainPanel.setMinimumSize(dmSize);
		mainPanel.setPreferredSize(dmSize);
		
		// メインパネルの生成
		_paneSplitter.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		_paneSplitter.setResizeWeight(0);
		_paneSplitter.setLeftComponent(_paneTree);
		_paneSplitter.setRightComponent(mainPanel);
		JPanel outerMainPanel = new JPanel(new BorderLayout());
		outerMainPanel.add(_paneSplitter, BorderLayout.CENTER);
		outerMainPanel.add(_statusbar, BorderLayout.SOUTH);
		
		// add to main panel
		this.getContentPane().add(outerMainPanel, BorderLayout.CENTER);
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected JButton createApplyButton() {
		// no apply button
		return null;
	}

	@Override
	protected JButton createOkButton() {
		if (getEditType() == EditType.VIEW) {
			//--- 表示専用の場合は、[OK] ボタンは表示しない
			return null;
		} else {
			//--- 編集可能な場合のみ、[OK] ボタンを表示
			return super.createOkButton();
		}
	}

	@Override
	protected String getDefaultCancelButtonCaption() {
		if (getEditType() == EditType.VIEW) {
			//--- 表示専用の場合は、[Close] ボタンに変更
			return CommonMessages.getInstance().Button_Close;
		} else {
			//--- 編集可能な場合のみ、[Cancel] ボタンとする
			return super.getDefaultCancelButtonCaption();
		}
	}

	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();
		
		this.setResizable(true);
		//this.setStoreLocation(false);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200, 300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
	}

	@Override
	protected void setupActions() {
		super.setupActions();
		
		// 引数テーブルのドロップを、編集モードなら許可する
		_argTable.setValueDropEnabled(isEditing());
		
		// ファイルドロップアクション
		//--- module
		new DropTarget(_paneModule, DnDConstants.ACTION_COPY, _hDroppedModule, true);
		new DropTarget(_stcModuleFile, DnDConstants.ACTION_COPY, _hDroppedModule, true);
		new DropTarget(_stcMainClass, DnDConstants.ACTION_COPY, _hDroppedModule, true);
		
		// ボタンアクション
		_btnChooseLocation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onChooseLocationButton(ae);
			}
		});
		_btnChooseModule.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onChooseModuleButton(ae);
			}
		});
		_btnReadModuleTitle.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onReadModuleTitle(ae);
			}
		});
		_btnReadModuleDesc.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onReadModuleDesc(ae);
			}
		});
		_btnGraphModule.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onGraphModule(ae);
			}
		});
		
		// ツリーコンポーネントの標準ショーカットキーアクション
		_paneTree.getTreeComponent().setDefaultCopyAction(_treeActionCopy);
		_paneTree.getTreeComponent().setDefaultPasteAction(_treeActionPaste);
		_paneTree.getTreeComponent().setDefaultDeleteAction(_treeActionDelete);
	}

	/**
	 * ツリーコンポーネントにフォーカスを設定する。
	 */
	public void requestFocusInTree() {
		_paneTree.requestFocusInComponent();
	}

	//------------------------------------------------------------
	// Action events
	//------------------------------------------------------------
	
	@Override
	protected void onWindowActivated(WindowEvent e) {
		// ツリーのコンテキストメニューを更新
		if (e.getWindow() == this) {
			updateTreeContextMenu();
		}
	}

	protected void onTreeDoubleClicked(MouseEvent e) {
		// no entry
	}
	
	protected void onTreeMenuActionPerformed(ActionEvent ae) {
		String commandKey = ae.getActionCommand();
		AppLogger.debug("MExecDefEditDialog#onTreeMenuActionPerformed(" + commandKey + ")");
		if (TREEMENU_ID_NEW_DIR.equals(commandKey)) {
			// create directory
			AppLogger.debug("MExecDefEditDialog : context menu [Create Folder] selected.");
			if (isEditing()) {
				_paneTree.doCreateDirectory();
			} else {
				AppLogger.debug("MExecDefEditDialog : context menu [Create Folder] skipped cause view mode.");
			}
			_paneTree.requestFocusInComponent();
		}
		else if (TREEMENU_ID_COPY.equals(commandKey)) {
			// copy
			AppLogger.debug("MExecDefEditDialog : context menu [Copy] selected.");
			if (!_paneTree.canCopy()) {
				_treeActionCopy.setEnabled(false);
				_paneTree.requestFocusInComponent();
				return;
			}
			_paneTree.doCopy();
			_treeActionPaste.setEnabled(isEditing() && _paneTree.canPaste());
			_paneTree.requestFocusInComponent();
		}
		else if (TREEMENU_ID_PASTE.equals(commandKey)) {
			// paste
			AppLogger.debug("MExecDefEditDialog : context menu [Paste] selected.");
			if (!isEditing()) {
				AppLogger.debug("MExecDefEditDialog : context menu [Paste] skipped cause view mode.");
				_treeActionPaste.setEnabled(false);
				_paneTree.requestFocusInComponent();
				return;
			}
			else if (!_paneTree.canPaste()) {
				_treeActionPaste.setEnabled(false);
				_paneTree.requestFocusInComponent();
				return;
			}
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					_paneTree.doPaste();
					_paneTree.requestFocusInComponent();
				}
			});
		}
		else if (TREEMENU_ID_DELETE.equals(commandKey)) {
			// delete
			AppLogger.debug("MExecDefEditDialog : context menu [Delete] selected.");
			if (!isEditing()) {
				AppLogger.debug("MExecDefEditDialog : context menu [Delete] skipped cause view mode.");
				_treeActionDelete.setEnabled(false);
				_paneTree.requestFocusInComponent();
				return;
			}
			else if (!_paneTree.canDelete()) {
				_treeActionDelete.setEnabled(false);
				_paneTree.requestFocusInComponent();
				return;
			}
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					_paneTree.doDelete();
					_paneTree.requestFocusInComponent();
				}
			});
		}
		else if (TREEMENU_ID_MOVETO.equals(commandKey)) {
			// move to
			AppLogger.debug("MExecDefEditDialog : context menu [Move To] selected.");
			if (!isEditing()) {
				AppLogger.debug("MExecDefEditDialog : context menu [Move To] skipped cause view mode.");
				_treeActionMoveTo.setEnabled(false);
				_paneTree.requestFocusInComponent();
				return;
			}
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					_paneTree.doMoveTo();
					_paneTree.requestFocusInComponent();
				}
			});
		}
		else if (TREEMENU_ID_RENAME.equals(commandKey)) {
			// rename
			AppLogger.debug("MExecDefEditDialog : context menu [Rename] selected.");
			if (!isEditing()) {
				AppLogger.debug("MExecDefEditDialog : context menu [Rename] skipped cause view mode.");
				_treeActionRename.setEnabled(false);
				_paneTree.requestFocusInComponent();
				return;
			}
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					_paneTree.doRename();
					_paneTree.requestFocusInComponent();
				}
			});
		}
		else if (TREEMENU_ID_REFRESH.equals(commandKey)) {
			// refresh
			AppLogger.debug("MExecDefEditDialog : context menu [Refresh] selected.");
			if (_paneTree.isSelectionEmpty()) {
				_paneTree.refreshAllTree();
			} else {
				_paneTree.refreshSelectedTree();
			}
			updateTreeContextMenu();
			_paneTree.requestFocusInComponent();
		}
	}

	//------------------------------------------------------------
	// Internal methods (for Creation)
	//------------------------------------------------------------
	
	protected void createComponents() {
		// create components
		_stcLocation = createFilePathLabel();
		_stcModuleFile = createFilePathLabel();
		_stcMainClass  = createFilePathLabel();
		_argTable = createArgTable();
		_taDesc = createDescTextComponent();
		_paneTree = createFileTreePanel();
		_statusbar = createStatusBar();
		_paneSplitter = new JSplitPane();
		
		// create Buttons
		_btnChooseLocation = CommonResources.createBrowseButton(RunnerMessages.getInstance().MExecDefEditDlg_Tooltip_ChooseLocation);
		_btnChooseModule   = CommonResources.createBrowseButton(RunnerMessages.getInstance().MExecDefEditDlg_Tooltip_ChooseModule);
		_btnGraphModule    = CommonResources.createIconButton(RunnerResources.ICON_GRAPH, RunnerMessages.getInstance().MExecDefEditDlg_Tooltip_OutputGraph);
		_btnArgEdit  = CommonResources.createMenuIconButton(CommonResources.ICON_EDIT, CommonMessages.getInstance().Button_Edit);
		_btnArgDel   = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		_btnReadModuleTitle = createReadModuleInfoButton();
		_btnReadModuleDesc  = createReadModuleInfoButton();
//		_chkClearArgsHistory = createClearArgsHistoryCheckBox();
		
		_argTable.attachEditButton(_btnArgEdit);
		_argTable.attachDeleteButton(_btnArgDel);

		_paneModule = createModulePanel();
		_paneTree.setTreeContextMenu(createTreeContextMenu());
	}
	
	protected JPanel createModulePanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		Border bdLine = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border outsideBorder = BorderFactory.createTitledBorder(bdLine,	RunnerMessages.getInstance().MExecDefEditDlg_Label_Module, TitledBorder.LEFT, TitledBorder.TOP);
		Border insideBorder = BorderFactory.createEmptyBorder(0, 0, 5, 5);
		pnl.setBorder(BorderFactory.createCompoundBorder(outsideBorder, insideBorder));
		
		// create label
		JLabel lblFile      = new JLabel(CommonMessages.getInstance().labelFile + " :");
		JLabel lblMainClass = new JLabel(CommonMessages.getInstance().labelMainClass + " :");
		Dimension dmFieldSize = _stcModuleFile.getPreferredSize();
		Dimension dmLabelSize = lblFile.getPreferredSize();
		int height = dmFieldSize.height - dmLabelSize.height;
		if (height > 1) {
			int itop = height / 2;
			int ibottom = height - itop;
			Border bdMargin = BorderFactory.createEmptyBorder(itop, 0, ibottom, 0);
			lblFile.setBorder(bdMargin);
			lblMainClass.setBorder(bdMargin);
		}
		
		// layout
		Insets insets = new Insets(5,5,0,0);
		Insets btnInsets = new Insets(5,3,0,0);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = insets;
		gbc.gridy = 0;
		//--- file
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.NONE;
		pnl.add(lblFile, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnl.add(_stcModuleFile, gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.insets = btnInsets;
		pnl.add(_btnChooseModule, gbc);
		gbc.insets = insets;
		gbc.gridy++;
		//--- main class
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.NONE;
		pnl.add(lblMainClass, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnl.add(_stcMainClass, gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.insets = btnInsets;
		pnl.add(_btnGraphModule, gbc);
		gbc.insets = insets;
		gbc.gridy++;
		
		return pnl;
	}
	
	protected JPanel createModuleArgsTablePanel() {
		// create panel (no border)
		JPanel pnl = new JPanel(new GridBagLayout());
		
		// adjust button preferred size
		Dimension dm = _btnArgEdit.getPreferredSize();
		_btnArgEdit.setMinimumSize(dm);
		_btnArgDel.setPreferredSize(dm);
		_btnArgDel.setMinimumSize(dm);

		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		pnl.add(_argTable, gbc);
		
		gbc.gridheight = 1;
		gbc.gridx = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 2, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		pnl.add(_btnArgEdit, gbc);
		
		gbc.gridy = 1;
		gbc.insets = new Insets(2, 2, 0, 0);
		pnl.add(_btnArgDel, gbc);
		
		return pnl;
	}
	
	protected JTextField createNameTextField() {
		JTextField field = new JTextField();
		return field;
	}
	
	protected JStaticMultilineTextPane createNameLabel() {
		return new JStaticMultilineTextPane();
	}
	
	protected JTextComponent getNameTextComponent() {
		return (_cNameField==null ? _cNameLabel : _cNameField);
	}
	
	protected JStaticMultilineTextPane createFilePathLabel() {
		return new JStaticMultilineTextPane();
	}
	
	protected MExecDefArgTablePane createArgTable() {
		MExecDefArgTablePane pane = new MExecDefArgTablePane();
		pane.initialComponent();
		return pane;
	}
	
	protected JTextArea createDescTextComponent() {
		JTextArea ta = new JTextArea();
		ta.setEditable(true);
		ta.setLineWrap(true);
		return ta;
	}
	
	protected JButton createReadModuleInfoButton() {
		JButton btn = new JButton(RunnerMessages.getInstance().MExecDefEditDlg_Button_ReadModuleInfo);
		btn.setMargin(new Insets(1,1,1,1));
		return btn;
	}
	
//	protected JCheckBox createClearArgsHistoryCheckBox() {
//		JCheckBox chk = new JCheckBox(RunnerMessages.getInstance().MExecDefEditDlg_CheckBox_ClearArgsHistory);
//		chk.setSelected(true);
//		return chk;
//	}
	
	protected MEDLocalFileTreePanel createFileTreePanel() {
		MEDLocalFileTreePanel ptree = new MEDLocalFileTreePanel(){
			@Override
			protected void onTreeDoubleClicked(MouseEvent e) {
				MExecDefEditDialog.this.onTreeDoubleClicked(e);
			}

			@Override
			protected void onTreeSelectionAdjusted() {
				MExecDefEditDialog.this.updateTreeContextMenu();
				_statusbar.setMessage(_paneTree.getSelectedFileProperty());
			}
		};
		ptree.initialComponent();
		return ptree;
	}
	
	protected FileTreeStatusBar createStatusBar() {
		return new FileTreeStatusBar();
	}
	
	protected void updateTreeContextMenu() {
		_treeActionNewDir .setEnabled(isEditing() && _paneTree.canCreateDirectory());
		_treeActionCopy   .setEnabled(_paneTree.canCopy());
		_treeActionPaste  .setEnabled(isEditing() && _paneTree.canPaste());
		_treeActionDelete .setEnabled(isEditing() && _paneTree.canDelete());
		_treeActionMoveTo .setEnabled(isEditing() && _paneTree.canMoveTo());
		_treeActionRename .setEnabled(isEditing() && _paneTree.canRename());
		_treeActionRefresh.setEnabled(true);
	}
	
	protected JTreePopupMenu createTreeContextMenu() {
		// create actions
		_treeActionNewDir	= new TreeContextMenuAction(TREEMENU_ID_NEW_DIR,
				RunnerMessages.getInstance().menuFileNewFolder,
				CommonResources.ICON_NEW_DIR,
				null,
				null,
				KeyEvent.VK_F,
				null);
		_treeActionCopy		= new TreeContextMenuAction(TREEMENU_ID_COPY,
				RunnerMessages.getInstance().menuEditCopy,
				CommonResources.ICON_COPY,
				RunnerMessages.getInstance().tipEditCopy,
				null,
				KeyEvent.VK_C,
				MenuItemResource.getEditCopyShortcutKeyStroke());
		_treeActionPaste	= new TreeContextMenuAction(TREEMENU_ID_PASTE,
				RunnerMessages.getInstance().menuEditPaste,
				CommonResources.ICON_PASTE,
				RunnerMessages.getInstance().tipEditPaste,
				null,
				KeyEvent.VK_P,
				MenuItemResource.getEditPasteShortcutKeyStroke());
		_treeActionDelete	= new TreeContextMenuAction(TREEMENU_ID_DELETE,
				RunnerMessages.getInstance().menuEditDelete,
				CommonResources.ICON_DELETE,
				null,
				null,
				KeyEvent.VK_L,
				MenuItemResource.getEditDeleteShortcutKeyStroke());
		_treeActionMoveTo	= new TreeContextMenuAction(TREEMENU_ID_MOVETO,
				RunnerMessages.getInstance().menuFileMoveTo,
				CommonResources.ICON_BLANK,
				null,
				null,
				KeyEvent.VK_V,
				null);
		_treeActionRename	= new TreeContextMenuAction(TREEMENU_ID_RENAME,
				RunnerMessages.getInstance().menuFileRename,
				CommonResources.ICON_BLANK,
				null,
				null,
				KeyEvent.VK_M,
				null);
		_treeActionRefresh	= new TreeContextMenuAction(TREEMENU_ID_REFRESH,
				RunnerMessages.getInstance().menuFileRefresh,
				CommonResources.ICON_REFRESH,
				null,
				null,
				KeyEvent.VK_F,
				null);

		// create Menu component
		JMenuItem item;
		JTreePopupMenu menu = new JTreePopupMenu();
		if (isEditing()) {
			//--- 編集操作を表示
			//--- new
			JMenu newMenu = new JMenu(RunnerMessages.getInstance().menuFileNew);
			newMenu.setIcon(CommonResources.ICON_BLANK);
			newMenu.setMnemonic(KeyEvent.VK_N);
			{
				//--- folder
				item = new ExMenuItem(true, false, _treeActionNewDir);
				newMenu.add(item);
			}
			menu.add(newMenu);
			//---
			menu.addSeparator();
			//--- copy
			item = new ExMenuItem(true, false, _treeActionCopy);
			menu.add(item);
			//--- paste
			item = new ExMenuItem(true, false, _treeActionPaste);
			menu.add(item);
			//--- delete
			item = new ExMenuItem(true, false, _treeActionDelete);
			menu.add(item);
			//---
			menu.addSeparator();
			//--- move to
			item = new ExMenuItem(true, false, _treeActionMoveTo);
			menu.add(item);
			//--- rename
			item = new ExMenuItem(true, false, _treeActionRename);
			menu.add(item);
			//---
			menu.addSeparator();
			//--- refresh
			item = new ExMenuItem(true, false, _treeActionRefresh);
			menu.add(item);
		}
		else {
			//--- 閲覧操作のみ表示
			//--- new
			//--- copy
			item = new ExMenuItem(true, false, _treeActionCopy);
			menu.add(item);
			//--- paste
			//--- delete
			//--- move to
			//--- rename
			//---
			menu.addSeparator();
			//--- refresh
			item = new ExMenuItem(true, false, _treeActionRefresh);
			menu.add(item);
		}
		
		return menu;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class TreeContextMenuAction extends AbMenuItemAction
	{

		public TreeContextMenuAction() {
			super();
		}

		public TreeContextMenuAction(String name) {
			super(name);
		}

		public TreeContextMenuAction(MenuItemResource mir) {
			super(mir);
		}

		public TreeContextMenuAction(String name, Icon icon) {
			super(name, icon);
		}

		public TreeContextMenuAction(String actionCommand, String name,
				Icon icon, String tooltip, String description, int mnemonic,
				KeyStroke keyStroke) {
			super(actionCommand, name, icon, tooltip, description, mnemonic, keyStroke);
		}
		
		public void actionPerformed(ActionEvent e) {
			onTreeMenuActionPerformed(e);
		}
	}

	/**
	 * 作業コピー生成用プログレスタスク
	 */
	static protected class SetupWorkingCopyProgressMonitorTask extends VirtualFileProgressMonitorTask
	{
		private final VirtualFile	_vfFilesRootDir;
		private VirtualFile		_vfWorkRootDir;
		
		public SetupWorkingCopyProgressMonitorTask(final VirtualFile rootDir) {
			super(RunnerMessages.getInstance().MExecDefEditDlg_SetupWorkTask_title, null, null, 0, 0, 100, false);	// キャンセル無効
			this._vfFilesRootDir = rootDir;
		}
		
		public VirtualFile getFilesRootDirectory() {
			return _vfFilesRootDir;
		}
		
		public VirtualFile getWorkRootDirectory() {
			return _vfWorkRootDir;
		}
		
		@Override
		public void processTask() throws Throwable
		{
			AppLogger.info("Start " + getClass().getName() + " task...");
			
			// 作業コピーのディレクトリ準備
			try {
				if (_vfFilesRootDir == null) {
					// テンポラリ領域に作成
					// 新規作成:テンポラリディレクトリを生成
//					_vfWorkRootDir = ModuleFileManager.fromJavaFile(File.createTempFile(MExecDefFileManager.MEXECDEF_FILES_WORK_DIRNAME, null));
					_vfWorkRootDir = ModuleFileManager.fromJavaFile(AppSettings.createTemporaryFile(MExecDefFileManager.MEXECDEF_FILES_WORK_DIRNAME, null, false));
					AppLogger.info(getClass().getName() + " task, created temporary file successfully : \"" + _vfWorkRootDir.getAbsolutePath() + "\"");
					//--- テンポラリファイルを削除
					if (!_vfWorkRootDir.delete()) 
						throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, _vfWorkRootDir);
					//--- ディレクトリを作成
					if (!_vfWorkRootDir.mkdir()) {
						throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, _vfWorkRootDir);
					}
					AppLogger.info(getClass().getName() + " task, created temporary directory successfully : \"" + _vfWorkRootDir.getAbsolutePath() + "\"");
					AppLogger.info(getClass().getName() + " task Completed!");
					return;		// 作業コピーのコピー元が存在しないので、終了
				} else {
					// 作業コピーディレクトリを準備
					_vfWorkRootDir = _vfFilesRootDir.getParentFile().getChildFile(MExecDefFileManager.MEXECDEF_FILES_WORK_DIRNAME);
					if (!_vfWorkRootDir.exists()) {
						// ディレクトリ作成
						if (!_vfWorkRootDir.mkdir()) {
							throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, _vfWorkRootDir);
						}
					}
					else if (!_vfWorkRootDir.isDirectory()) {
						// ディレクトリではない
						throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, _vfWorkRootDir);
					}
				}
			}
			catch (VirtualFileOperationException ex) {
				// 作業コピーを削除
				if (_vfWorkRootDir != null) {
					setMaximum(getMinimum());
					deleteAllFiles(_vfWorkRootDir, true);	// 例外を無視
					_vfWorkRootDir = null;
				}
				throw ex;
			}
			catch (Throwable ex) {
				// 作業コピーを削除
				if (_vfWorkRootDir != null) {
					setMaximum(getMinimum());
					deleteAllFiles(_vfWorkRootDir, true);	// 例外を無視
					_vfWorkRootDir = null;
				}
				throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, _vfWorkRootDir, ex);
			}
			
			// 作業コピーの内容を削除
			deleteAllSubFiles(_vfWorkRootDir, true);	// 例外を無視
			
			// コピーするファイルの総数を取得
			int numFiles = _vfFilesRootDir.countFiles();
			if (AppLogger.isInfoEnabled()) {
				AppLogger.info("Start " + getClass().getName() + " task for " + numFiles + " files.");
			}
			//--- 総数の更新
			setMaximum(numFiles);
			incrementValue();
			
			// 作業コピーの作成
			setAllowOverwriteAllFilesFlag(true);
			VirtualFile[] files = _vfFilesRootDir.listFiles();
			try {
				if (files != null) {
					for (VirtualFile f : files) {
						copyRecursive(f, _vfWorkRootDir);
					}
				}
			}
			catch (VirtualFileOperationException ex) {
				// 作業コピーを削除
				if (_vfWorkRootDir != null) {
					setMaximum(getMinimum());
					deleteAllFiles(_vfWorkRootDir, true);	// 例外を無視
					_vfWorkRootDir = null;
				}
				throw ex;
			}
			catch (Throwable ex) {
				// 作業コピーを削除
				if (_vfWorkRootDir != null) {
					setMaximum(getMinimum());
					deleteAllFiles(_vfWorkRootDir, true);	// 例外を無視
					_vfWorkRootDir = null;
				}
				throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, _vfWorkRootDir, ex);
			}
			
			// 完了
			setValue(getMaximum());
			AppLogger.info(getClass().getName() + " task Completed!");
		}
	}
	
	/**
	 * キャンセル時のプログレスタスク。
	 * 作業コピーを破棄する。
	 */
	static protected class RollbackProgressMonitorTask extends VirtualFileProgressMonitorTask
	{
		private final VirtualFile	_vfWorkRootDir;
		
		public RollbackProgressMonitorTask(final VirtualFile workDir) {
			super(RunnerMessages.getInstance().MExecDefEditDlg_RollbackTask_title, null, null, 0, 0, 100, false);	// キャンセル無効
			this._vfWorkRootDir = workDir;
		}
		
		@Override
		public void processTask() throws Throwable
		{
			if (_vfWorkRootDir == null)
				return;
			
			AppLogger.info("Start " + getClass().getName() + " task...");
			
			// 処理対象のファイル総数をカウント
			int numFiles = _vfWorkRootDir.countFiles();
			if (AppLogger.isInfoEnabled()) {
				AppLogger.info("Start " + getClass().getName() + " task for " + numFiles + " files.");
			}
			//--- 総数をプログレスバーに設定
			setMaximum(numFiles + 1);
			incrementValue();
			
			deleteRecursive(_vfWorkRootDir);
			
			// 完了
			setValue(getMaximum());
			AppLogger.info(getClass().getName() + " task Completed!");
		}
	}
	
	/**
	 * モジュール実行定義編集のコミット
	 */
	static protected class CommitProgressMonitorTask extends VirtualFileProgressMonitorTask
	{
		private final VirtualFile	_vfFilesRootDir;
		private final VirtualFile	_vfWorkRootDir;
		private final MExecDefSettings				_settings;
		private final Map<VirtualFile,VirtualFile>	_copyFiles;
		private final Set<VirtualFile>				_retainFiles;
		
		public CommitProgressMonitorTask(final VirtualFile rootDir, final VirtualFile workDir,
										final MExecDefSettings settings, final Map<VirtualFile,VirtualFile> copyFiles, final Set<VirtualFile> retainFiles)
		{
			super(RunnerMessages.getInstance().MExecDefEditDlg_Title_SaveMExecDef, null, null, 0, 0, 100, false);
			this._vfFilesRootDir = rootDir;
			this._vfWorkRootDir  = workDir;
			this._settings = settings;
			this._copyFiles = copyFiles;
			this._retainFiles = retainFiles;
		}
		
		protected boolean cleanupSubFiles(VirtualFile file, final Set<VirtualFile> retainFiles) {
			boolean canRemoveDir = true;
			VirtualFile[] subfiles = file.listFiles();
			if (subfiles != null) {
				for (VirtualFile vf : subfiles) {
					if (!cleanupFiles(vf, retainFiles)) {
						//--- 除外ファイルが存在するため、このディレクトリは削除不可
						canRemoveDir = false;
					}
				}
			}
			return canRemoveDir;
		}
		
		protected boolean cleanupFiles(final VirtualFile file, final Set<VirtualFile> retainFiles) {
			if (file.exists()) {
				if (retainFiles.contains(file)) {
					// 除外対象のファイルなら削除しない。ディレクトリの場合は、サブファイルも全て削除しない
					setValue(getValue() + file.countFiles());
					return false;
				}
				
				if (file.isDirectory()) {
					// サブファイルの削除
					if (!cleanupSubFiles(file, retainFiles)) {
						// このディレクトリは削除しない
						incrementValue();
						return false;
					}
				}
				
				// ファイルの削除
				try {
					file.delete();
				} catch (Throwable ex) {
					throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, file, null, ex);
				}
				//--- check
				if (file.exists()) {
					throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, file, null);
				}
			}
			incrementValue();
			return true;
		}
		
		protected void copyFile(VirtualFile vfSrc, VirtualFile vfDst, byte[] buffer)
		{
			// 親ディレクトリの生成
			VirtualFile vfDir = vfDst.getParentFile();
			if (!vfDir.exists()) {
				try {
					if (!vfDir.mkdirs()) {
						throw new IOException("Failed to create directory.");
					}
				} catch (Throwable ex) {
					throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, vfDir, null, ex);
				}
			}
			
			// ファイルのコピー
			if (vfDst.exists() && vfDst.isDirectory()) {
				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, vfSrc, vfDst);
			}
			InputStream iStream = null;
			OutputStream oStream = null;
			try {
				iStream = vfSrc.getInputStream();
				oStream = vfDst.getOutputStream();
				
				Files.copy(iStream, oStream, buffer);
			}
			catch (Throwable ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, vfSrc, vfDst, ex);
			}
			finally {
				if (oStream != null) {
					Files.closeStream(oStream);
					oStream = null;
				}
				if (iStream != null) {
					Files.closeStream(iStream);
					iStream = null;
				}
			}
			//--- 更新日時のコピー
			try {
				long lm = vfSrc.lastModified();
				if (lm != 0L) {
					vfDst.setLastModified(lm);
				}
			} catch (Throwable ignoreEx) {}
			//--- 読み込み専用属性は、アプリケーションが管理するため適用しない
		}
		
		protected void commitLocalFileChanges(final VirtualFile vfFilesDir, final VirtualFile vfWorkDir) {
			// ローカルファイルデータを、作業コピーでリプレース
			setAllowOverwriteAllFilesFlag(true);	// 無条件に置換を許可
			//resetAllowOverwriteAllFilesFlag();		// （テスト）同名のファイルやディレクトリは置換確認
			forceRenameTo(vfWorkDir, vfFilesDir);	// 強制置換
			
			// ローカルファイルデータのディレクトリが不要なら、削除
			try {
				if (vfFilesDir.exists()) {
					String[] names = vfFilesDir.list();
					if (names == null || names.length <= 0) {
						// 中身のないディレクトリなら削除
						fileDeleteExactly(vfFilesDir);
					}
				}
			}
			catch (Throwable ignoreEx) {}
		}
		
		@Override
		public void processTask() throws Throwable
		{
			AppLogger.info("Start " + getClass().getName() + " task...");
			
			// モジュール実行定義ルートディレクトリの確認
			VirtualFile vfExecDefDir = _settings.getExecDefDirectory();
			try {
				if (!vfExecDefDir.exists()) {
					if (!vfExecDefDir.mkdirs()) {
						throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, vfExecDefDir, null);
					}
				}
			}
			catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, vfExecDefDir, null, ex);
			}
			
			// 処理総数を算定し、プログレスを設定
			int maxPos = 1;
			maxPos += _copyFiles.size();
			maxPos += (vfExecDefDir.countFiles() - 1);
			if (_vfWorkRootDir != null) {
				maxPos += _vfWorkRootDir.countFiles();
			}
			setMaximum(maxPos);
			
			// 必要なファイルのコピー
			byte[] buffer = new byte[BUF_SIZE];
			for (Map.Entry<VirtualFile, VirtualFile> entry : _copyFiles.entrySet()) {
				copyFile(entry.getKey(), entry.getValue(), buffer);
				incrementValue();
			}
			
			// ローカルファイルの変更適用
			if (_vfWorkRootDir != null) {
				commitLocalFileChanges(_vfFilesRootDir, _vfWorkRootDir);
			}
			
			// 引数のデータ型定義に合わせ、既存の履歴を消去
			VirtualFile vfHistory = vfExecDefDir.getChildFile(MExecDefFileManager.MEXECDEF_HISTORY_FILENAME);
			if (vfHistory.exists() && vfHistory.isFile()) {
				MExecDefHistory history = new MExecDefHistory();
				history.loadForTarget(vfHistory);
				if (!history.isHistoryEmpty()) {
					if (history.ensureArgsTypes(_settings) <= 0) {
						//--- 履歴が変更されなかったときは、履歴ファイルを削除しない
						_retainFiles.add(vfHistory);
					}
					else if (!history.isHistoryEmpty()) {
						//--- 履歴が変更され、履歴が残っている場合は、履歴を保存し、履歴ファイルを削除しない
						history.ensureMaxSize(AppSettings.getInstance().getHistoryMaxLength());
						if (!history.isHistoryEmpty()) {
							try {
								history.commit();
								_retainFiles.add(vfHistory);
							}
							catch (Throwable ex) {
								AppLogger.error("Failed to save to MED History file.\nFile : \"" + history.getVirtualPropertyFile().getAbsolutePath() + "\"", ex);
							}
						}
					}
				}
			}
			
			// 設定情報の保存
			try {
				_settings.commit();
			}
			catch (IOException ex) {
				AppLogger.error("Failed to save to Module Execution Definition file.\nFile : \"" + _settings.getVirtualPropertyFile().getAbsolutePath() + "\"", ex);
				throw ex;
			}
			incrementValue();
			
			// 不要なファイルの削除
			cleanupSubFiles(vfExecDefDir, _retainFiles);
			
			// 完了
			setValue(getMaximum());
			AppLogger.info(getClass().getName() + " task Completed!");
		}

		@Override
		protected String getDefaultErrorMessage() {
			return RunnerMessages.getInstance().msgMExecDefFailedToSave;
		}
	}

//	/**
//	 * ファイル保存時のプログレスタスク
//	 */
//	static protected class FileSaveProgressMonitorTask extends ProgressMonitorTask
//	{
//		static protected final int BUF_SIZE = 50000;
//		
//		private final MExecDefSettings				_settings;
//		private final Map<VirtualFile,VirtualFile>	_copyFiles;
//		private final Set<VirtualFile>				_retainFiles;
//		
//		static private final String[] svnNames = {".svn", "_svn"};
//		
//		public FileSaveProgressMonitorTask(final MExecDefSettings settings, final Map<VirtualFile,VirtualFile> copyFiles, final Set<VirtualFile> retainFiles) {
//			super(RunnerMessages.getInstance().MExecDefEditDlg_Title_SaveMExecDef, null, null, 0, 0, 100, false);
//			this._settings = settings;
//			this._copyFiles = copyFiles;
//			this._retainFiles = retainFiles;
//		}
//		
//		protected boolean collectRemoveFiles(final VirtualFile dir, final Set<VirtualFile> retainFiles, List<VirtualFile> removeFiles) {
//			boolean canRemoveDir = true;
//			VirtualFile[] filelist = dir.listFiles();
//			if (filelist != null && filelist.length > 0) {
//				// SubVersion 対応
//				for (String rName : svnNames) {
//					VirtualFile vfRetain = dir.getChildFile(rName);
//					if (vfRetain.exists()) {
//						// subversion 管理ファイルが存在する場合、このディレクトリと、
//						// ディレクトリに含まれる全てのファイルの削除を禁止
//						return false;
//					}
//				}
//
//				// 削除可能なファイルの判定
//				for (VirtualFile file : filelist) {
//					if (file.isDirectory()) {
//						//--- subversion 対応
//						if (!collectRemoveFiles(file, retainFiles, removeFiles)) {
//							// 子ディレクトリが削除不可なら、このディレクトリも削除不可
//							canRemoveDir = false;
//						}
//					}
//					else {
//						boolean canRemoveFile = true;
//						//--- 削除可能か判定
//						if (retainFiles.contains(file)) {
//							canRemoveFile = false;
//							canRemoveDir = false;
//						}
//						//--- 削除可能なら削除ファイルに追加
//						if (canRemoveFile) {
//							removeFiles.add(file);
//						}
//					}
//				}
//			}
//			//--- このディレクトリが削除可能なら、削除リストに追加
//			if (canRemoveDir) {
//				if (retainFiles.contains(dir)) {
//					canRemoveDir = false;
//				} else {
//					removeFiles.add(dir);
//				}
//			}
//			return canRemoveDir;
//		}
//		
//		protected void copyFile(VirtualFile vfSrc, VirtualFile vfDst, byte[] buffer)
//		{
//			// 親ディレクトリの生成
//			VirtualFile vfDir = vfDst.getParentFile();
//			if (!vfDir.exists()) {
//				try {
//					if (!vfDir.mkdirs()) {
//						throw new IOException("Failed to create directory.");
//					}
//				} catch (Throwable ex) {
//					throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, vfDir, null, ex);
//				}
//			}
//			
//			// ファイルのコピー
//			if (vfDst.exists() && vfDst.isDirectory()) {
//				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, vfSrc, vfDst);
//			}
//			InputStream iStream = null;
//			OutputStream oStream = null;
//			try {
//				iStream = vfSrc.getInputStream();
//				oStream = vfDst.getOutputStream();
//				
//				Files.copy(iStream, oStream, buffer);
//			}
//			catch (Throwable ex) {
//				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, vfSrc, vfDst, ex);
//			}
//			finally {
//				if (oStream != null) {
//					Files.closeStream(oStream);
//					oStream = null;
//				}
//				if (iStream != null) {
//					Files.closeStream(iStream);
//					iStream = null;
//				}
//			}
//			//--- 更新日時のコピー
//			try {
//				long lm = vfSrc.lastModified();
//				if (lm != 0L) {
//					vfDst.setLastModified(lm);
//				}
//			} catch (Throwable ignoreEx) {}
//			//--- 読み込み専用属性は、アプリケーションが管理するため適用しない
//		}
//		
//		@Override
//		public void processTask() throws Throwable
//		{
//			// 削除ファイルの収集
//			ArrayList<VirtualFile> removeFiles = new ArrayList<VirtualFile>();
//			collectRemoveFiles(_settings.getExecDefDirectory(), _retainFiles, removeFiles);
//			
//			// 処理総数を算定し、プログレスを設定
//			int maxPos = 1;
//			maxPos += _copyFiles.size();
//			maxPos += removeFiles.size();
//			setMaximum(maxPos);
//			
//			// 設定情報の保存
//			try {
//				_settings.commit();
//			}
//			catch (IOException ex) {
//				AppLogger.error("Failed to save to Module Execution Definition file.\nFile : \"" + _settings.getVirtualPropertyFile().getAbsolutePath() + "\"", ex);
//				throw ex;
//			}
//			incrementValue();
//			
//			// 必要なファイルのコピー
//			byte[] buffer = new byte[BUF_SIZE];
//			for (Map.Entry<VirtualFile, VirtualFile> entry : _copyFiles.entrySet()) {
//				copyFile(entry.getKey(), entry.getValue(), buffer);
//				incrementValue();
//			}
//			
//			// ファイルの削除
//			for (VirtualFile file : removeFiles) {
//				if (file.exists()) {
//					try {
//						file.delete();
//					} catch (Throwable ex) {
//						throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, file, null, ex);
//					}
//					//--- check
//					if (file.exists()) {
//						throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, file, null);
//					}
//				}
//				incrementValue();
//			}
//		}
//
//		/**
//		 * プログレスモニタータスクの処理中に発生したエラーに関するメッセージを表示する。
//		 * エラーが発生していない場合は、処理しない。
//		 * 
//		 * @param parentComponent	メッセージボックスのオーナー
//		 */
//		public void showError(Component parentComponent) {
//			if (getErrorCause() instanceof VirtualFileOperationException) {
//				VirtualFileOperationException ex = (VirtualFileOperationException)getErrorCause();
//				String strSourcePath = (ex.getSourceFile() != null ? ex.getSourceFile().toString() : null);
//				String strTargetPath = (ex.getTargetFile() != null ? ex.getTargetFile().toString() : null);
//				String errmsg;
//				if (ex.getOperationType() == VirtualFileOperationException.MKDIR) {
//					errmsg = String.format(CommonMessages.getInstance().msgCouldNotCreateDirectory + "\nFile : \"%s\"", strSourcePath);
//				}
//				else if (ex.getOperationType() == VirtualFileOperationException.COPY) {
//					errmsg = String.format(CommonMessages.getInstance().msgCouldNotCopyTo, strSourcePath);
//				}
//				else if (ex.getOperationType() == VirtualFileOperationException.DELETE) {
//					errmsg = String.format(CommonMessages.getInstance().msgCouldNotDelete, strSourcePath);
//				}
//				else {
//					StringBuilder sb = new StringBuilder();
//					sb.append(RunnerMessages.getInstance().msgMExecDefFailedToSave);
//					sb.append("\n  type:" + ex.getOperationType());
//					if (strSourcePath != null) {
//						sb.append("\n  source:\"" + strSourcePath + "\"");
//					}
//					if (strTargetPath != null) {
//						sb.append("\n  target:\"" + strTargetPath + "\"");
//					}
//					if (ex.getCause() != null) {
//						sb.append("\n  cause:(");
//						sb.append(ex.getCause().getClass().getName());
//						sb.append(")");
//						String msg = ex.getCause().getLocalizedMessage();
//						if (msg == null) {
//							sb.append("\n        " + msg);
//						}
//					}
//					errmsg = sb.toString();
//				}
//				
//				AppLogger.error(errmsg, ex);
//				Application.showErrorMessage(parentComponent, errmsg);
//			}
//			else if (getErrorCause() != null) {
//				String errmsg = CommonMessages.formatErrorMessage(
//									RunnerMessages.getInstance().msgMExecDefFailedToSave,
//									getErrorCause());
//				AppLogger.error(errmsg, getErrorCause());
//				Application.showErrorMessage(parentComponent, errmsg);
//			}
//		}
//	}

	/**
	 * モジュールファイル(Jar) のドロップを受け付けるハンドラ
	 */
	class ModuleFileDropTargetListener  implements DropTargetListener
	{
		private boolean canImport = false;
		
		public void dragEnter(DropTargetDragEvent dtde) {
			canImport = false;
			// サポートする DataFlavor のチェック
			if (!dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// サポートされないデータ形式
				dtde.rejectDrag();
				return;
			}
			
			// ソースアクションにコピーが含まれていない場合は、許可しない
			if ((dtde.getSourceActions() & DnDConstants.ACTION_COPY) == 0) {
				// サポートされないドロップアクション
				dtde.rejectDrag();
				return;
			}
			
			// ドロップアクションをコピー操作に限定
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
			canImport = true;
		}
		
		public void dragExit(DropTargetEvent dte) {
			canImport = false;
		}

		public void dropActionChanged(DropTargetDragEvent dtde) {}

		public void dragOver(DropTargetDragEvent dtde) {
			if (canImport) {
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
			} else {
				dtde.rejectDrag();
			}
		}
		
		public void drop(DropTargetDropEvent dtde) {
			// サポートする DataFlavor のチェック
			if (!dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// サポートされないデータ形式
				dtde.rejectDrop();
				return;
			}
			
			// ソースアクションにコピーが含まれていない場合は、許可しない
			if ((dtde.getSourceActions() & DnDConstants.ACTION_COPY) == 0) {
				// サポートされないドロップアクション
				dtde.rejectDrop();
				return;
			}
			
			// データソースの取得
			dtde.acceptDrop(DnDConstants.ACTION_COPY);
			Transferable t = dtde.getTransferable();
			if (t == null) {
				dtde.rejectDrop();
				return;
			}
			
			// ファイルを取得する
			try {
				File targetFile;
				List<?> filelist = (List<?>)t.getTransferData(DataFlavor.javaFileListFlavor);
				//--- 設定ファイル(*.prefs) を除くパスのみを収集
				Set<File> fileset = new LinkedHashSet<File>();
				for (Object elem : filelist) {
					if (elem instanceof File) {
						targetFile = (File)elem;
						if (!Strings.endsWithIgnoreCase(targetFile.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
							fileset.add(targetFile);
						}
					}
				}
				
				// 先頭のファイルのみ受け入れる
				if (!fileset.isEmpty()) {
					Iterator<File> it = fileset.iterator();
					targetFile = it.next();
					setModuleFile(ModuleFileManager.fromJavaFile(targetFile));
				}
				
				// ドロップ完了
				dtde.dropComplete(true);
			}
			catch (UnsupportedFlavorException ex) {
				AppLogger.error("Failed to drop to editor.", ex);
			}
			catch (IOException ex) {
				AppLogger.error("Failed to drop to editor.", ex);
			}
			
			// drop を受け付けない
			dtde.rejectDrop();
		}
	}
}
