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
 * @(#)MacroFilterEditDialog.java	3.2.1	2015/07/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroFilterEditDialog.java	3.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroFilterEditDialog.java	2.0.0	2012/11/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.macro.data.MacroData;
import ssac.aadl.macro.file.CsvMacroFiles;
import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgDetail;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.setting.MacroExecSettings;
import ssac.falconseed.file.RollbackWorkingCopyProgressTask;
import ssac.falconseed.file.SetupWorkingCopyProgressTask;
import ssac.falconseed.file.VirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFileProgressMonitorTask;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.MacroFilterDefArgList;
import ssac.falconseed.module.MacroSubModuleRuntimeData;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.ModuleDataList;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.RelatedModuleList;
import ssac.falconseed.module.setting.MExecDefHistory;
import ssac.falconseed.module.setting.MExecDefMacroFilter;
import ssac.falconseed.module.setting.MExecDefMacroFilter.DefMacroFilterInvalidStates;
import ssac.falconseed.module.setting.MExecDefMacroFilter.MacroFilterDefArgInvalidState;
import ssac.falconseed.module.setting.MExecDefMacroFilter.SubFilterInvalidState;
import ssac.falconseed.module.setting.MExecDefSettings;
import ssac.falconseed.module.swing.MacroFilterEditModel.EditType;
import ssac.falconseed.module.swing.tree.MEDLocalFileTreePanel;
import ssac.falconseed.module.swing.tree.MEDLocalFileTreePanel.FileTreeStatusBar;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.dialog.MessageDetailDialog;
import ssac.util.Objects;
import ssac.util.io.Files;
import ssac.util.io.NumberedVirtualFileManager;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileManager;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.logging.AppLogger;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.Application;
import ssac.util.swing.ExDialog;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.menu.ExMenuItem;
import ssac.util.swing.menu.MenuItemResource;
import ssac.util.swing.tree.JTreePopupMenu;

/**
 * マクロフィルタ専用の編集ダイアログ。
 * <p>マクロフィルタのファイル構成は以下の通り。
 * <pre>
 * フィルタ名/
 *   +-- .mexecdef.prefs ・・・・・・・・・モジュール実行定義ファイル
 *   +-- .mexecdef.history ・・・・・・・・引数履歴ファイル
 *   +-- .macrofilter.prefs  ・・・・・・・マクロフィルタ定義ファイル
 *   +-- args/ ・・・・・・・・・・・・・・フィルタ定義引数の固定引数値となるファイル群
 *   |     +-- a1/                           ファイル名が重複しないように、連番が付加された
 *   |     +-- a2/                           ディレクトリが作成される。
 *   |     |   ・
 *   |     |   ・
 *   |     |   ・
 *   |     +-- an/
 *   +-- macro/  ・・・・・・・・・・・・・マクロデータルート
 *   |     +-- FilterMacro.amf ・・・・・・マクロフィルタのAADLマクロファイル
 *   |     +-- FilterMacro.amf.prefs ・・・AADLマクロ設定ファイル
 *   |     +-- filters/  ・・・・・・・・・サブフィルタ群
 *   |           +-- f1/
 *   |           +-- f2/                     フィルタ名が重複しないように、連番が付加された
 *   |           |   ・                      ディレクトリが作成される。
 *   |           |   ・
 *   |           |   ・
 *   |           +-- fn/
 *   +-- files/  ・・・・・・・・・・・・・ローカルファイルルート
 *   |     +-- ユーザー指定のファイル群
 *   +-- $work/  ・・・・・・・・・・・・・作業ディレクトリ(作業コピー)
 *         +-- .mexecdef.prefs               作業コピーは、モジュール実行定義ルートディレクトリ内の
 *         +-- .mexecdef.history             (作業コピーを除く)すべてのファイルが複製される。
 *         +-- .macrofilter.prefs
 *         +-- args/
 *         +-- macro/
 *         +-- files/
 * </pre>
 * 
 * @version 3.2.1
 * @since 2.0.0
 */
public class MacroFilterEditDialog extends ExDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -1486319255590020959L;

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

	private TreeContextMenuAction	_treeActionNewDir;
	private TreeContextMenuAction	_treeActionCopy;
	private TreeContextMenuAction	_treeActionPaste;
	private TreeContextMenuAction	_treeActionDelete;
	private TreeContextMenuAction	_treeActionMoveTo;
	private TreeContextMenuAction	_treeActionRename;
	private TreeContextMenuAction	_treeActionRefresh;
	

	/** 編集対象のデータモデル **/
	private MacroFilterEditModel	_editModel;

	/** 作業ディレクトリ **/
	private VirtualFile			_vfWorkDir;
	
	/** ローカルファイルツリーパネル **/
	private MEDLocalFileTreePanel		_paneTree;
	/** スプリッター **/
	private JSplitPane					_paneSplitter;
	/** タブ **/
	private JTabbedPane				_tabEditor;
	/** ファイル情報用ステータスバー **/
	private FileTreeStatusBar			_statusbar;
	/** フィルタ定義編集パネル **/
	private MacroFilterDefEditPane		_paneDefEditor;
	/** フィルタ構成編集パネル **/
	private MacroFilterConstEditPane	_paneConstEditor;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroFilterEditDialog(Frame owner) {
		super(owner, true);
	}
	
	public MacroFilterEditDialog(Dialog owner) {
		super(owner, true);
	}

//	/**
//	 * 作業コピーが残っている場合は、削除する。
//	 */
//	public void cleanup() {
//		if (_model.hasWorkingFiles() && _model.getWorkingFilesRootDir().exists()) {
//			VirtualFileProgressMonitorTask.deleteAllFiles(_model.getWorkingFilesRootDir(), true);	// 例外は無視
//		}
//	}

	/**
	 * マクロフィルタ編集ダイアログの初期化。
	 * @param editType			編集種別
	 * @param defaultFormatter	標準のパスフォーマッター
	 * @param rootDisplayName	ルートディレクトリの表示名
	 * @param rootDir			ルートディレクトリ
	 * @param target			編集対象のフィルタマクロのパス、編集以外の場合は基準の親ディレクトリもしくは <tt>null</tt>
	 * @param preFilters		フィルタマクロ生成対象の実行履歴リスト(履歴番号のまま)、履歴からの生成ではない場合は <tt>null</tt>
	 * @return	初期化に成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean initialComponent(MacroFilterEditModel.EditType editType,
									VirtualFilePathFormatter defaultFormatter,
									String rootDisplayName, VirtualFile rootDir,
									VirtualFile target, RelatedModuleList preFilters)
	{
		// 引数をチェック
		if (rootDir == null)
			throw new NullPointerException("The specified root directory is null.");
		if (!rootDir.exists())
			throw new IllegalArgumentException("The specified root directory does not exists.");
		if (!rootDir.isDirectory())
			throw new IllegalArgumentException("The specified root directory is not directory.");
		
		// check
		VirtualFile vfParentDir;
		VirtualFile vfTargetDir;
		if (target == null) {
			if (editType == EditType.VIEW || editType == EditType.MODIFY) {
				// 参照もしくは編集対象不明
				throw new NullPointerException("The specified target file is null.");
			}
			vfParentDir = null;
			vfTargetDir = null;
		} else {
			VirtualFile vfPrefs = MExecDefFileManager.getModuleExecDefDataFile(target);
			VirtualFile vfMacroPrefs = MExecDefFileManager.getMacroFilterDefDataFile(target);
			if (editType == EditType.NEW) {
				//--- この場合の target は、モジュール実行定義を配置する親ディレクトリ
				if (!target.isDirectory()) {
					// 作成する位置がディレクトリではない
					throw new IllegalArgumentException("The specified target file is not directory.");
				}
				if (vfPrefs.exists()) {
					// 作成位置がモジュール実行定義ディレクトリである
					throw new IllegalArgumentException("The specified target file is module execution definition data.");
				}
				vfParentDir = target;
				vfTargetDir = null;
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
				if (!vfMacroPrefs.exists() || !vfMacroPrefs.isFile()) {
					// 対象がマクロフィルタ定義ではない
					throw new IllegalArgumentException("The specified target file is not Macro filter definition data.");
				}
				vfParentDir = target.getParentFile();
				vfTargetDir = target;
			}
		}
		
		// 作業コピーの作成
		VirtualFile workingDir = null;
		if (editType==MacroFilterEditModel.EditType.NEW || editType==MacroFilterEditModel.EditType.MODIFY) {
			// 編集モードの場合は、作業コピーを生成する。
			SetupWorkingCopyProgressTask task = new SetupWorkingCopyProgressTask(vfTargetDir);
			task.execute(getOwner());
			task.showError(getOwner());
			if (task.isTerminateRequested()) {
				// ユーザーによってキャンセルされた場合は、ダイアログを表示しない
				return false;
			}
			
			// エラーチェック
			if (task.getErrorCause() != null) {
				if (editType == MacroFilterEditModel.EditType.NEW) {
					// 新規作成モードでエラーが発生した場合は、ダイアログを表示しない
					return false;
				}
				
				// 読み込み専用モードで開く
				Application.showWarningMessage(RunnerMessages.getInstance().msgMExecDefOpenForReadOnly);
				editType = MacroFilterEditModel.EditType.VIEW;
			} else {
				// 作業コピー作成成功
				workingDir = task.getWorkDirectory();
			}
		}
		// else : 読み込み専用なら、作業コピーは作成しない
		
		// フィルタのルートディレクトリからデータモデルを生成
		MacroFilterEditModel newEditModel = null;
		try {
			newEditModel = new MacroFilterEditModel(editType, defaultFormatter, rootDisplayName, rootDir, vfTargetDir, workingDir, preFilters);
			if (newEditModel.isNewEditing()) {
				newEditModel.setFilterParentDirectory(vfParentDir);
			}
		}
		catch (OutOfMemoryError ex) {
			// @since 3.2.1
			// メモリ不足
			System.gc();
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(getOwner(), errmsg);
			//--- 作業コピーを削除
			if (workingDir != null) {
				RollbackWorkingCopyProgressTask task = new RollbackWorkingCopyProgressTask(workingDir);
				task.execute(getOwner());
			}
			return false;
		}
		catch (Throwable ex) {
			//--- エラー
			String errmsg = CommonMessages.formatErrorMessage(CommonMessages.getInstance().msgCouldNotProcessComplete, ex);
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(getOwner(), errmsg);
			//--- 作業コピーを削除
			if (workingDir != null) {
				RollbackWorkingCopyProgressTask task = new RollbackWorkingCopyProgressTask(workingDir);
				task.execute(getOwner());
			}
			return false;
		}
		if (newEditModel.hasInvalidDataStatesWhenReadDefs()) {
			DefMacroFilterInvalidStates states = newEditModel.getInvalidDataStatesWhenReadDefs();
			//--- 不正データ情報の表示
			showMacroFilterInvalidStatesMessage(states);
		}
		
		// モデルを設定する
		this._vfWorkDir = workingDir;
		this._editModel = newEditModel;
		
		// 基本クラスの処理
		return super.initialComponent();
	}
	
	protected void showMacroFilterInvalidStatesMessage(DefMacroFilterInvalidStates states) {
		// check
		if (states == null || !states.hasError())
			return;
		
		String title = CommonMessages.getInstance().msgboxTitleWarn;
		String msg = RunnerMessages.getInstance().msgMacroFilterDefInvalidData;
		StringBuilder sb = new StringBuilder();
		
		// フィルタマクロ定義引数
		String lbl = RunnerMessages.getInstance().MacroFilterEditDlg_Label_MacroFilter
					+ RunnerMessages.getInstance().MExecDefEditDlg_Label_Args + ":";
		for (MacroFilterDefArgInvalidState defargstate : states.getDefArgsStateList()) {
			sb.append(lbl);
			//--- argno
			int argno = defargstate.argNo();
			if (argno > 0) {
				sb.append("($");
				sb.append(argno);
				sb.append(")");
			}
			sb.append("\n\t");
			sb.append(defargstate.message());
			sb.append("\n");
		}
		
		// サブフィルタ
		for (SubFilterInvalidState substate : states.getSubFilterStateList()) {
			int argno = substate.argNo();
			String detmsg = "\n\t" + substate.message();
			if (argno > 0) {
				// 引数が対象
				if (argno <= substate.moduleData().getArgumentCount()) {
					detmsg = ModuleRuntimeData.formatErrorMessage(substate.moduleData(), substate.moduleData().getArgument(argno-1), detmsg);
				}
				else {
					detmsg = String.format("($%d) : %s", argno, detmsg);
					detmsg = ModuleRuntimeData.formatErrorMessage(substate.moduleData(), detmsg);
				}
			}
			else {
				// モジュールが対象
				detmsg = ModuleRuntimeData.formatErrorMessage(substate.moduleData(), detmsg);
			}
			sb.append(detmsg);
			sb.append("\n");
		}
		String det = sb.toString();
		MessageDetailDialog.showErrorDetailMessage(getOwner(), title, msg, det);
	}

	@Override
	protected boolean onInitialComponent() {
		if (!super.onInitialComponent()) {
			return false;
		}
		
		// ダイアログタイトルの設定
		switch (_editModel.getEditType()) {
			case VIEW :
				setTitle(RunnerMessages.getInstance().MacroFilterEditDlg_Title_VIEW);
				break;
			case NEW :
				setTitle(RunnerMessages.getInstance().MacroFilterEditDlg_Title_NEW);
				break;
			default :
				setTitle(RunnerMessages.getInstance().MacroFilterEditDlg_Title_MODIFY);
		}
		
		// コンポーネントの生成
		_paneTree = createFileTreePanel();
		_statusbar = createStatusBar();
		_paneSplitter = new JSplitPane();
		_tabEditor = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		_paneDefEditor = new MacroFilterDefEditPane(_editModel);
		_paneDefEditor.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		_paneConstEditor = new MacroFilterConstEditPane();
		_paneConstEditor.setEditModel(_editModel);
		
		// メインパネルの生成
		_paneTree.setTreeContextMenu(createTreeContextMenu());
		_tabEditor.addTab(RunnerMessages.getInstance().MacroFilterEditDlg_TabTitle_Definition, _paneDefEditor);
		_tabEditor.addTab(RunnerMessages.getInstance().MacroFilterEditDlg_TabTitle_SubFilters, _paneConstEditor);
		_paneSplitter.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		_paneSplitter.setResizeWeight(0);
		_paneSplitter.setLeftComponent(_paneTree);
		_paneSplitter.setRightComponent(_tabEditor);
		JPanel outerMainPanel = new JPanel(new BorderLayout());
		outerMainPanel.add(_paneSplitter, BorderLayout.CENTER);
		outerMainPanel.add(_statusbar, BorderLayout.SOUTH);
		//--- add to main panel
		this.getContentPane().add(outerMainPanel, BorderLayout.CENTER);
		
		// 成功
		return true;
	}

	@Override
	protected boolean postInitialComponent() {
		// Setup Actions
		//--- ツリーコンポーネントの標準ショーカットキーアクション
		_paneTree.getTreeComponent().setDefaultCopyAction(_treeActionCopy);
		_paneTree.getTreeComponent().setDefaultPasteAction(_treeActionPaste);
		_paneTree.getTreeComponent().setDefaultDeleteAction(_treeActionDelete);
		
		// ツリーのルート設定
		_paneTree.setRootDirectory(_editModel.getLocalFileRootDirectory());
		//--- 初期状態ではルートノードを閉じた状態とする
		//_paneTree.getTreeComponent().collapseRow(0);
		
		// サブフィルタ変更リスナー
		_paneConstEditor.addSubFilterDataModelListener(new SubFilterValuesEditModelListener());
		_paneConstEditor.setFilterDialogHandler(new MacroFilterDialogHandler());
		
		// Setup Dialog conditions
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		//this.setStoreLocation(false);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200, 300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
		
		// 完了
		return super.postInitialComponent();
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
		return _editModel.getRootDirectory();
	}

	/**
	 * モジュール実行定義が配置されるルートディレクトリの表示名を取得する。
	 * @return	ルートディレクトリの表示名
	 */
	public String getRootDisplayName() {
		return _editModel.getRootDisplayName();
	}

//	/**
//	 * モジュール実行定義が配置される場所の親ディレクトリを取得する。
//	 * @return	ディレクトリの抽象パス
//	 */
//	public VirtualFile getParentDirectory() {
//		return _editModel.getParentDirectory();
//	}
//
//	/**
//	 * モジュール実行定義を示すディレクトリを取得する。
//	 * @return	モジュール実行定義を示すディレクトリを表す抽象パスを返す。
//	 * 			未定義の場合は <tt>null</tt> を返す。
//	 */
//	public VirtualFile getMExecDefDirectory() {
//		return _editModel.getMExecDefDirectory();
//	}
//
//	/**
//	 * モジュール実行定義を保持する設定ファイルを取得する。
//	 * @return	設定ファイルの抽象パスを返す。
//	 * 			未定義の場合は <tt>null</tt> を返す。
//	 */
//	public VirtualFile getMExecDefPrefsFile() {
//		return _editModel.getMExecDefPrefsFile();
//	}

	/**
	 * モジュール実行定義編集ダイアログの動作種別を取得する。
	 * @return	動作種別
	 */
	public MacroFilterEditModel.EditType getEditType() {
		return _editModel.getEditType();
	}
	
	public boolean isEditing() {
		return _editModel.isEditing();
	}

	/**
	 * このダイアログのデータモデルが管理するフィルタマクロ定義の
	 * 親ディレクトリを返す。
	 * @return	親ディレクトリの抽象パス
	 */
	public VirtualFile getParentDirectory() {
		return _editModel.getFilterParentDirectory();
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	/**
	 * このダイアログの標準サイズを返す。
	 * @return	ダイアログの標準サイズを返す。標準サイズを指定しない場合は <tt>null</tt> を返す。
	 */
	@Override
	public Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected void onRestoreConfiguration(ExConfiguration config, String prefix) {
		super.onRestoreConfiguration(config, prefix);
		
		// restore divider location
		if (config != null) {
			if (_paneSplitter != null) {
				int dl = config.getDividerLocation(prefix);
				if (dl > 0) {
					_paneSplitter.setDividerLocation(dl);
				}
			}
			
			if (_paneDefEditor != null) {
				_paneDefEditor.restoreConfiguration(config, prefix);
			}
			
			if (_paneConstEditor != null) {
				_paneConstEditor.restoreConfiguration(config, prefix);
			}
		}
	}

	@Override
	protected void onStoreConfiguration(ExConfiguration config, String prefix) {
		super.onStoreConfiguration(config, prefix);
		
		// store current divider location
		if (config != null) {
			if (_paneSplitter != null) {
				int dl = _paneSplitter.getDividerLocation();
				config.setDividerLocation(prefix, dl);
			}
			
			if (_paneDefEditor != null) {
				_paneDefEditor.storeConfiguration(config, prefix);
			}
			
			if (_paneConstEditor != null) {
				_paneConstEditor.storeConfiguration(config, prefix);
			}
		}
	}

	@Override
	protected void onWindowOpened(WindowEvent e) {
		super.onWindowOpened(e);

		// adjust all column width
		_paneDefEditor.adjustArgTableAllColumnsPreferredWidth();
		_paneConstEditor.initialAllTableColumnWidth();
		
		// update buttons
		updateButtons();
		updateTreeContextMenu();
	}

	@Override
	protected boolean doCancelAction() {
		// [OK] 以外の要因でダイアログを閉じる場合は、ローカルファイルを元に戻す
		if (_vfWorkDir != null) {
			RollbackWorkingCopyProgressTask task = new RollbackWorkingCopyProgressTask(_vfWorkDir);
			task.execute(this);
			task.showError(this);
			_vfWorkDir = null;
		}
		
		// ダイアログを閉じる
		return true;
	}

	@Override
	protected boolean doOkAction() {
		VirtualFile vfTargetDir;
		VirtualFile vfTargetPrefs;
		VirtualFile vfMacroFilterPrefs;
		
		// サブフィルタの有無を確認
		if(_editModel.isSubFilterEmpty()) {
			//--- サブフィルタが存在しない
			_tabEditor.setSelectedComponent(_paneConstEditor);
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgMacroFilterNoSubfilter);
			return false;
		}
		
		// サブフィルタの引数チェック
		if (!_paneConstEditor.verifyData()) {
			//--- エラー発生
			_tabEditor.setSelectedComponent(_paneConstEditor);
			//--- 最初のエラーを取得
			_paneConstEditor.showFirstError();
			return false;
		}
		
		// サブフィルタの待機なしフィルタの引数チェック(@since 3.1.0)
		if (!_paneConstEditor.verifyNoWaitAsyncModuleArguments(_tabEditor)) {
			//--- ユーザーによる中断
			return false;
		}
		
		// タブの切替
		_tabEditor.setSelectedComponent(_paneDefEditor);
		
		// 名前のチェック
		if (_editModel.isNewEditing()) {
			if (!_paneDefEditor.checkFilename()) {
				return false;
			}
		}
		
		// 実行時引数のチェック
		if (!_paneDefEditor.checkArguments()) {
			return false;
		}
		
		// 設定情報の反映
		if (_editModel.isNewEditing()) {
			_editModel.setFilterName(_paneDefEditor.getMExecDefName());
			_editModel.setFilterParentDirectory(_paneDefEditor.getMExecDefParentDirectory());
		}
		_editModel.setMExecDefDescription(_paneDefEditor.getMExecDefDescription());
		
		// 書き込み可能かチェック
		vfTargetDir = _paneDefEditor.getMExecDefDirectory();
		vfTargetPrefs = MExecDefFileManager.getModuleExecDefDataFile(vfTargetDir);
		vfMacroFilterPrefs = MExecDefFileManager.getMacroFilterDefDataFile(vfTargetDir);
		if (!checkWritable(vfTargetDir, vfTargetPrefs, vfMacroFilterPrefs)) {
			return false;
		}
		
		// 変更のコミット
		CommitProgressTask task = new CommitProgressTask(_editModel, vfTargetDir, _editModel.getWorkingFilterRootDirectory());
		task.execute(this);
		task.showError(this);
		//--- 保存に失敗した場合もダイアログを閉じる
		
		// ダイアログを閉じる
		return true;
	}

	@Override
	protected void onClickOkButton(ActionEvent e) {
		// 念のため、表示専用の場合は無条件で閉じる
		if (!_editModel.isEditing()) {
			onClickCancelButton(e);
			return;
		}

		// 編集中なら [OK] ボタン押下時の処理を行う
		super.onClickOkButton(e);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void updateButtons() {
		_paneDefEditor.updateStatus();
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
			String errmsg = CommonMessages.formatErrorMessage(CommonMessages.getInstance().msgNotHaveWritePermission, null, _editModel.formatFilePath(file));
			AppLogger.error(errmsg);
			Application.showErrorMessage(errmsg);
		}
		return canWritable;
	}

	/**
	 * モジュール実行定義に含まれる全てのファイル(作業コピーを除く)が変更可能かをチェックする。
	 * @param vfExecDefDir	モジュール実行定義のルートディレクトリ
	 * @param vfWorkDir		作業ディレクトリ
	 * @return
	 */
	protected VirtualFile checkModifiable(final VirtualFile vfExecDefDir, final VirtualFile vfWorkDir) {
		// モジュール実行定義フォルダ内のすべてのファイルが変更可能かを確認
		if (vfExecDefDir.exists() && vfExecDefDir.isDirectory()) {
			VirtualFile unmodifiable;
			VirtualFile[] files = vfExecDefDir.listFiles();
			for (VirtualFile svf : files) {
				if (!svf.equals(vfWorkDir)) {
					// 作業ディレクトリ以外のファイルを検査
					unmodifiable = VirtualFileProgressMonitorTask.checkModifiableAllFiles(svf);
					if (unmodifiable != null) {
						return unmodifiable;
					}
				}
			}
		}
		// all modifiable
		return null;
	}

//	/**
//	 * モジュール実行定義に含まれる全てのファイル、ならびに、作業コピーに含まれる全てのファイルが変更可能かをチェックする。
//	 * @param vfExecDefDir	モジュール実行定義のルートディレクトリ
//	 * @param vfFilesDir	モジュール実行定義のローカルファイルルートディレクトリ
//	 * @param vfWorkDir		ローカルファイルの作業コピー
//	 * @return	変更可能なら <tt>true</tt>、そうでない場合は変更不可能なファイルを示す抽象パス
//	 */
//	protected VirtualFile checkModifiable(final VirtualFile vfExecDefDir, final VirtualFile vfFilesDir, final VirtualFile vfWorkDir) {
//		VirtualFile unmodifiable;
//		
//		// 作業コピーが変更可能かを確認
//		VirtualFile skipFolder;
//		if (vfWorkDir != null) {
//			unmodifiable = VirtualFileProgressMonitorTask.checkModifiableAllFiles(vfWorkDir);
//			if (unmodifiable != null) {
//				return unmodifiable;
//			}
//			skipFolder = vfWorkDir;		// 作業コピーは変更可能なため、次のチェックではスキップ
//		} else {
//			skipFolder = vfFilesDir;	// 作業コピーは存在しないため、ローカルファイルディレクトリは変更しない
//		}
//		
//		// モジュール実行定義フォルダ内の全てのファイルが変更可能かを確認
//		if (vfExecDefDir.exists() && vfExecDefDir.isDirectory()) {
//			VirtualFile[] files = vfExecDefDir.listFiles();
//			for (VirtualFile svf : files) {
//				if (!svf.equals(skipFolder)) {
//					unmodifiable = VirtualFileProgressMonitorTask.checkModifiableAllFiles(svf);
//					if (unmodifiable != null)
//						return unmodifiable;
//				}
//			}
//		}
//		
//		// 全て変更可能
//		return null;
//	}

	/**
	 * 指定のパスへの書き込み権限があるかを判定する。
	 * @param vfTargetDir		モジュール実行定義の格納ディレクトリ
	 * @param vfTargetPrefs		モジュール実行定義ファイル
	 * @param vfMacroPrefs		マクロフィルタ専用定義ファイル
	 * @return	書き込み可能なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean checkWritable(VirtualFile vfTargetDir, VirtualFile vfTargetPrefs, VirtualFile vfMacroPrefs) {
		// 親ディレクトリの書き込み可能チェック
		if (!checkWritable(_paneDefEditor.getMExecDefParentDirectory())) {
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
		
		// マクロフィルタ専用定義ファイルのチェック
		if (!vfMacroPrefs.exists()) {
			return true;	// 存在しないので、書き込み可能
		}
		if (!vfMacroPrefs.isFile()) {
			String errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgMExecDefPrefsNotFile, null, vfMacroPrefs.toString());
			AppLogger.error(errmsg);
			Application.showErrorMessage(errmsg);
			return false;
		}
		if (!checkWritable(vfMacroPrefs)) {
			return false;
		}
		
		// モジュール実行定義内ファイルの変更可能かのチェック
		//VirtualFile unmodifiable = checkModifiable(vfTargetDir, _model.getLocalFilesRootDir(), _model.getWorkingFilesRootDir());
		VirtualFile unmodifiable = checkModifiable(vfTargetDir, _vfWorkDir);
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
	 * 現在のモジュール実行定義を指定のパスへ保存する。
	 * この保存では、関連するファイルも保存される。
	 * @param vfTargetDir		モジュール実行定義の格納ディレクトリ
	 * @param vfTargetPrefs		モジュール実行定義ファイル
	 */
	protected void doSaveSettings(VirtualFile vfTargetDir, VirtualFile vfTargetPrefs) {
	}

	//------------------------------------------------------------
	// Internal methods (for Setup contents)
	//------------------------------------------------------------

	/**
	 * ツリーコンポーネントにフォーカスを設定する。
	 */
	public void requestFocusInTree() {
		_paneTree.requestFocusInComponent();
	}
	
	@Override
	protected JButton createApplyButton() {
		// [Apply] ボタンは生成しない
		return null;
	}

	@Override
	protected JButton createCancelButton() {
		JButton btn = super.createCancelButton();
		if (!_editModel.isEditing()) {
			//--- 表示専用の場合は [Close] ボタンに変更
			btn.setText(CommonMessages.getInstance().Button_Close);
		}
		return btn;
	}

	@Override
	protected JButton createOkButton() {
		if (!_editModel.isEditing()) {
			//--- 表示専用の場合は [OK] ボタンは表示しない
			return null;
		}
		else {
			return super.createOkButton();
		}
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
	
	protected MEDLocalFileTreePanel createFileTreePanel() {
		MEDLocalFileTreePanel ptree = new MEDLocalFileTreePanel(){
			@Override
			protected void onTreeDoubleClicked(MouseEvent e) {
				MacroFilterEditDialog.this.onTreeDoubleClicked(e);
			}

			@Override
			protected void onTreeSelectionAdjusted() {
				MacroFilterEditDialog.this.updateTreeContextMenu();
				if (_paneTree.isSelectionEmpty()) {
					// このダイアログのファイルツリーの選択解除
					if (_paneConstEditor.isFileTreeSelectionEmpty()) {
						//--- サブフィルタのファイルツリーが選択されていなければ、ステータスバーの表示更新
						_statusbar.setMessage(_paneTree.getSelectedFileProperty());
					}
				}
				else {
					// このダイアログのファイルツリーの選択変更
					//--- サブフィルタのファイルツリー選択状態を解除
					_paneConstEditor.clearFileTreeSelection();
					//--- このダイアログのファイルツリー選択内容をステータスバーに表示
					_statusbar.setMessage(_paneTree.getSelectedFileProperty());
				}
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

	/**
	 * 現在のフィルタ設定状態から、保存時の情報を生成する。
	 * @param editModel			マクロフィルタ編集データモデル
	 * @param defPrefs			モジュール実行定義を格納するオブジェクト
	 * @param defMacroFilter	マクロフィルタ定義を格納するオブジェクト
	 * @param aadlMacroData		AADLマクロ定義データを格納するオブジェクト
	 * @param aadlMacroSettings	AADLマクロ設定情報を格納するオブジェクト
	 * @param mapCopyFiles		コピーするファイルまたはディレクトリのコピー元とコピー先のマップ
	 * @param retainFiles		削除対象から除外するファイルまたはディレクトリの集合
	 * @return 正常に作成できた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static protected boolean buildCommitFileInfo(final MacroFilterEditModel editModel,
											final MExecDefSettings defPrefs, final MExecDefMacroFilter defMacroFilter,
											final MacroData aadlMacroData, final MacroExecSettings aadlMacroSettings,
											final Map<VirtualFile, VirtualFile> mapCopyFiles, final Set<VirtualFile> retainFiles)
	{
		// フィルタ定義引数とサブフィルタリストのコピーを作成
		MacroFilterDefArgList defFilterArgs = new MacroFilterDefArgList(editModel.getMExecDefArgumentCount());
		ModuleDataList<MacroSubModuleRuntimeData> subFilterList = new ModuleDataList<MacroSubModuleRuntimeData>(editModel.getSubFilterCount());
		copyFilterInfo(editModel.getMExecDefArgsList(), defFilterArgs, editModel.getSubFilterList(), subFilterList);
		
		// モジュール実行定義を生成
		setupMExecDefModuleInfo(editModel, defPrefs, aadlMacroSettings, retainFiles);
		if (!setupMExecDefModuleArguments(editModel, defPrefs, defFilterArgs, aadlMacroSettings, mapCopyFiles, retainFiles))
			return false;
		
		// サブフィルタの定義を生成
		if (!setupSubFilters(editModel, subFilterList, mapCopyFiles, retainFiles)) {
			return false;
		}
		
		// マクロフィルタ定義を更新
		defMacroFilter.setPropertyTarget(editModel.getMacroFilterDefPrefsFile());
		defMacroFilter.setMacroFilterData(defFilterArgs, subFilterList);
		
		// マクロ定義データを作成
		MacroFilterDefEditPane.buildAADLMacroData(aadlMacroData, editModel.getMacroRootDirectory(), subFilterList);
		
		// 削除しないファイル／ディレクトリを登録
		//--- マクロ格納ディレクトリ
		retainFiles.add(editModel.getMacroRootDirectory());
		//--- ローカルファイル格納ディレクトリ
		retainFiles.add(editModel.getLocalFileRootDirectory());
		//--- 固定引数値格納ディレクトリは引数の有無による
		
		// 完了
		return true;
	}

	/**
	 * フィルタ定義引数リストとサブフィルタリストの要素をコピーする。要素のコピーでは新しいインスタンスが生成されるが、
	 * 各要素が保持する引数値はシャローコピーとなる。そのため、引数値が他引数へのリンク(参照)の場合、その参照先を
	 * コピーによって生成されたオブジェクトに変換する。
	 * @param srcArgList		コピー元のフィルタ定義引数リスト
	 * @param dstArgList		コピー先のフィルタ定義引数リスト
	 * @param srcFilterList		コピー元のサブフィルタリスト
	 * @param dstFilterList		コピー先のサブフィルタリスト
	 */
	static protected void copyFilterInfo(final MacroFilterDefArgList srcArgList, final MacroFilterDefArgList dstArgList,
											final ModuleDataList<MacroSubModuleRuntimeData> srcFilterList,
											final ModuleDataList<MacroSubModuleRuntimeData> dstFilterList)
	{
		// フィルタ定義引数のコピー
		Map<FilterArgEditModel,FilterArgEditModel> mapDefArgs;
		if (srcArgList.isEmpty())
			mapDefArgs = Collections.emptyMap();
		else
			mapDefArgs = new HashMap<FilterArgEditModel, FilterArgEditModel>();
		for (FilterArgEditModel arg : srcArgList) {
			FilterArgEditModel dupArg = arg.clone();
			mapDefArgs.put(arg, dupArg);
			dstArgList.add(dupArg);
		}
		
		// サブフィルタリストのコピー
		Map<MacroSubModuleRuntimeData,MacroSubModuleRuntimeData> mapModules;
		if (srcFilterList.isEmpty())
			mapModules = Collections.emptyMap();
		else
			mapModules = new HashMap<MacroSubModuleRuntimeData, MacroSubModuleRuntimeData>();
		for (MacroSubModuleRuntimeData srcModule : srcFilterList) {
			MacroSubModuleRuntimeData dstModule = srcModule.clone();
			mapModules.put(srcModule, dstModule);
			dstFilterList.add(dstModule);
		}
		
		// 新しいサブフィルタの値を正規化(コピー後のインスタンスに関連付け)
		Object srcValue, dstValue;
		for (int index = 0; index < srcFilterList.size(); ++index) {
			MacroSubModuleRuntimeData dstModule = dstFilterList.get(index);
			//--- 引数設定の正規化
			for (ModuleArgConfig dstarg : dstModule) {
				srcValue = dstarg.getValue();
				if (srcValue instanceof FilterArgReferValueEditModel) {
					// 文字列付き実行時引数参照の複製(@since 3.1.0)
					FilterArgReferValueEditModel refValue = (FilterArgReferValueEditModel)srcValue;
					FilterArgEditModel dstRefer = mapDefArgs.get(refValue.getReferencedArgument());
					if (dstRefer == null) {
						throw new IllegalArgumentException("Linked undefined Filter definition argument : " + String.valueOf(dstarg));
					}
					dstarg.setValue(new FilterArgReferValueEditModel(dstRefer, refValue.getPrefix(), refValue.getSuffix()));
				}
				else if (srcValue instanceof IModuleArgConfig) {
					// 単純な実行時引数参照の複製
					dstValue = mapDefArgs.get(srcValue);
					if (dstValue == null) {
						throw new IllegalArgumentException("Linked undefined Filter definition argument : " + String.valueOf(dstarg));
					}
					dstarg.setValue(dstValue);
				}
				else if (srcValue instanceof ModuleArgID) {
					ModuleArgID srcArgID = (ModuleArgID)srcValue;
					dstValue = mapModules.get(srcArgID.getData());
					if (dstValue == null) {
						throw new IllegalArgumentException("Refered undefined Sub-Filter argument : " + String.valueOf(dstarg));
					}
					dstarg.setValue(new ModuleArgID((ModuleRuntimeData)dstValue, srcArgID.argNo()));
				}
			}
			//--- サブフィルタ実行順序リンクの複製(@since 3.1.0)
			MacroSubModuleRuntimeData srcModule = srcFilterList.get(index);
			boolean linkEnabled = srcModule.getRunOrderLinkEnabled();
			dstModule.setRunOrderLinkEnabled(linkEnabled);
			if (linkEnabled) {
				Set<MacroSubModuleRuntimeData> waitModules = srcModule.getWaitModuleSet();
				for (MacroSubModuleRuntimeData wm : waitModules) {
					dstModule.connectWaitModule(mapModules.get(wm));
				}
			}
		}
		
		mapDefArgs.clear();
		mapModules.clear();
	}

	/**
	 * モジュール実行定義のモジュール情報の設定
	 */
	static protected void setupMExecDefModuleInfo(final MacroFilterEditModel editModel, final MExecDefSettings defPrefs,
													final MacroExecSettings aadlMacroSettings, final Set<VirtualFile> retainFiles)
	{
		// 正規のモジュール実行定義データの生成
		defPrefs.setPropertyTarget(editModel.getMExecDefPrefsFile());
		//--- モジュールファイルの設定
		defPrefs.setModuleFile(editModel.getMExecDefModuleFile());
		//--- モジュールタイプの設定
		defPrefs.setModuleFileType(editModel.getMExecDefModuleType());
		//--- メインクラスの設定
		defPrefs.setModuleMainClass(editModel.getMExecDefMainClass());
		//--- 機能説明の設定
		defPrefs.setDescription(editModel.getMExecDefDescription());

		// AADLマクロ設定ファイルの生成
		aadlMacroSettings.setPropertyTarget(editModel.getMExecDefModulePrefsFile());
		//--- タイトル
		aadlMacroSettings.setTitle(editModel.getFilterName());
		//--- 機能説明
		aadlMacroSettings.setDescription(editModel.getMExecDefDescription());
		//--- 注釈
		aadlMacroSettings.setNote("Automatically generated by ModuleRunner/FALCON-SEED.");
		
		// 削除対象外ファイルパスの追加(履歴は含まない)
		retainFiles.add(editModel.getMExecDefPrefsFile());
		retainFiles.add(editModel.getMacroFilterDefPrefsFile());
		retainFiles.add(editModel.getMExecDefModuleFile());
		retainFiles.add(editModel.getMExecDefModulePrefsFile());
	}

	/**
	 * モジュール実行定義の引数情報の設定
	 * @return	正常に設定できた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static protected boolean setupMExecDefModuleArguments(final MacroFilterEditModel editModel, final MExecDefSettings defPrefs,
															final MacroFilterDefArgList destFilterArgs, final MacroExecSettings aadlMacroSettings,
															final Map<VirtualFile, VirtualFile> mapCopyFiles, final Set<VirtualFile> retainFiles)
	{
		if (destFilterArgs.isEmpty()) {
			return true;		// no arguments
		}
		
		// 引数の固定ファイルのうち、フィルタに含まれないパスを収集
		HashSet<VirtualFile> externalFiles = new HashSet<VirtualFile>();
		VirtualFile vfBaseDir = editModel.getAvailableFilterRootDirectory();
		for (ModuleArgConfig argdata : destFilterArgs) {
			VirtualFile vfFile = VirtualFileManager.toVirtualFile(argdata.getValue());
			if (vfFile != null && !vfFile.isDescendingFrom(vfBaseDir)) {
				//--- 外部ファイルを収集
				externalFiles.add(vfFile);
			}
		}
		if (!externalFiles.isEmpty()) {
			// 外部ファイルがある場合は、コピーマップのエントリを生成
			VirtualFile vfArgDir = editModel.getArgsRootDirectory();
			NumberedVirtualFileManager vfman = new NumberedVirtualFileManager(vfArgDir, MExecDefFileManager.MEXECDEF_MODULEARG_SUBDIR_PREFIX, null);
			if (!vfman.updateFreeNumbers()) {
				AppLogger.error("MacroFilterEditDialog - Cannot copy MExecDef arguments : No more available argument directory : \"" + vfArgDir.toString() + "\"");
				return false;
			}
			try {
				vfman.assignFreeNumberedFile(mapCopyFiles, externalFiles);
			}
			catch (Throwable ex) {
				AppLogger.error("MacroFilterEditDialog - Cannot copy MExecDef arguments : No more available argument directory : \"" + vfArgDir.toString() + "\"", ex);
				return false;
			}
		}
		
		// パスを変換し、モジュール実行定義情報を保存する
		boolean hasArgsFile = false;
		for (ModuleArgConfig argdata : destFilterArgs) {
			ModuleArgDetail aadlArgData = new ModuleArgDetail(argdata.getType(), argdata.getDescription());
			ModuleArgData defArgData = new ModuleArgData(argdata.getType(), argdata.getDescription(), argdata.getValue());
			VirtualFile vfFile = VirtualFileManager.toVirtualFile(argdata.getValue());
			if (vfFile != null) {
				//--- ファイルなら、パスを変換
				VirtualFile vfDest = mapCopyFiles.get(vfFile);
				if (vfDest != null) {
					// 新しい配置先を指定
					hasArgsFile = true;
					defArgData.setValue(vfDest);
					argdata.setValue(vfDest);
					//--- 削除除外対象に追加
					retainFiles.add(vfDest);
					retainFiles.add(vfDest.getParentFile());	//--- 親ディレクトリも追加
				}
			}
			//--- 引数を追加
			defPrefs.addArgument(defArgData);
			aadlMacroSettings.addArgument(aadlArgData);
		}
		if (hasArgsFile) {
			//--- 固定引数値となるファイルが取り込まれる場合、
			//--- フィルタ定義引数の固定引数値格納ディレクトリを削除除外対象に追加
			retainFiles.add(editModel.getArgsRootDirectory());
		}
		
		return true;
	}
	/**
	 * モジュール実行定義の引数情報の設定
	 * @return	正常に設定できた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static protected boolean setupSubFilters(final MacroFilterEditModel editModel, final ModuleDataList<MacroSubModuleRuntimeData> subFilterList,
										final Map<VirtualFile, VirtualFile> mapCopyFiles, final Set<VirtualFile> retainFiles)
	{
		if (subFilterList.isEmpty()) {
			return true;	// no sub-filters
		}
		
		// 既存のサブフィルタのパスを収集
		Map<String,Set<VirtualFile>> existSubFilters;
		VirtualFile vfSubFiltersRoot = editModel.getSubFiltersRootDirectory();
		if (vfSubFiltersRoot != null && vfSubFiltersRoot.exists()) {
			existSubFilters = createExistsSubFilterNameMap(vfSubFiltersRoot);
		} else {
			existSubFilters = Collections.emptyMap();
		}
		
		// 新規に追加されたサブフィルタの情報を収集
		ArrayList<VirtualFile> newFilters = new ArrayList<VirtualFile>(subFilterList.size());
		HashMap<VirtualFile,VirtualFile> filterPathMap = new HashMap<VirtualFile,VirtualFile>();
//		VirtualFile vfBaseDir = _paneDefEditor.getMExecDefDirectory();
		for (MacroSubModuleRuntimeData module : subFilterList) {
			VirtualFile vfTarget = module.getExecDefDirectory();
			if (vfTarget.isDescendingFrom(vfSubFiltersRoot)) {
				//--- 既存のサブフィルタ
				filterPathMap.put(vfTarget, vfTarget);
			}
			else if (!filterPathMap.containsKey(vfTarget)) {
				//--- 新規の外部フィルタ
				VirtualFile vfDest = getSameExistSubFilter(existSubFilters, module);
				filterPathMap.put(vfTarget, vfDest);
				if (vfDest == null) {
					newFilters.add(vfTarget);
				}
			}
		}
		
		// 新規に追加されたサブフィルタのコピー先を確定
		if (!newFilters.isEmpty()) {
			// 外部ファイルがある場合は、コピーマップのエントリを生成
			NumberedVirtualFileManager vfman = new NumberedVirtualFileManager(vfSubFiltersRoot, MExecDefFileManager.MACROFILTER_SUBFILTER_SUBDIR_PREFIX, null);
			if (!vfman.updateFreeNumbers()) {
				AppLogger.error("MacroFilterEditDialog - Cannot copy Sub-Filters : No more available Sub-Filter directory : \"" + vfSubFiltersRoot.toString() + "\"");
				return false;
			}
			try {
				vfman.assignFreeNumberedFile(filterPathMap, newFilters);
			}
			catch (Throwable ex) {
				AppLogger.error("MacroFilterEditDialog - Cannot copy Sub-Filters : No more available Sub-Filter directory : \"" + vfSubFiltersRoot.toString() + "\"");
				return false;
			}
		}
		
		// サブフィルタの位置を変換
		VirtualFile vfBaseDir = editModel.getAvailableFilterRootDirectory();
		for (MacroSubModuleRuntimeData module : subFilterList) {
			VirtualFile vfOldTarget = module.getExecDefDirectory();
			VirtualFile vfNewTarget = filterPathMap.get(vfOldTarget);
			retainFiles.add(vfNewTarget);
			boolean noChanges = vfNewTarget.equals(vfOldTarget);
			if (!noChanges) {
				//--- 更新
				module.updateMExecDefDirectory(vfNewTarget, true);
			}
			for (ModuleArgConfig arg : module) {
				VirtualFile vfFile = VirtualFileManager.toVirtualFile(arg.getValue());
				if (vfFile != null && !vfFile.isDescendingFrom(vfBaseDir)) {
					//--- 外部ファイルはパス変換
					if (vfFile.isDescendingFrom(vfOldTarget)) {
						//--- 自サブフィルタのファイル参照
						VirtualFile vfNewFile = vfNewTarget.getChildFile(vfFile.relativePathFrom(vfOldTarget));
						retainFiles.add(vfNewFile);
						arg.setValue(vfNewFile);
					}
					else {
						//--- 他サブフィルタの引数かチェック
						for (Map.Entry<VirtualFile, VirtualFile> entry : filterPathMap.entrySet()) {
							VirtualFile vfAnother = entry.getKey();
							if (!vfOldTarget.equals(entry.getKey()) && vfFile.isDescendingFrom(vfAnother)) {
								VirtualFile vfNewFile = filterPathMap.get(vfAnother).getChildFile(vfFile.relativePathFrom(vfAnother));
								retainFiles.add(vfNewFile);
								arg.setValue(vfNewFile);
							}
						}
					}
				}
			}
		}
		
		// 新たにコピーするフィルタのパスマップを更新
		for (VirtualFile vfNewFilter : newFilters) {
			mapCopyFiles.put(vfNewFilter, filterPathMap.get(vfNewFilter));
		}
		
		return true;
	}

	/**
	 * 既存のサブフィルタの名前と場所のマップを生成する。
	 * @param vfSubFiltersDir	サブフィルタのルートディレクトリ
	 * @return	既存のサブフィルタが存在する場合はマップオブジェクト、存在しない場合は要素が空のマップ
	 */
	static protected Map<String,Set<VirtualFile>> createExistsSubFilterNameMap(final VirtualFile vfSubFiltersDir)
	{
		VirtualFile[] filelist = vfSubFiltersDir.listFiles();
		if (filelist == null || filelist.length <= 0)
			return Collections.emptyMap();	// no subfilters

		Map<String,Set<VirtualFile>> map = new HashMap<String,Set<VirtualFile>>();
		for (VirtualFile vfFile : filelist) {
			VirtualFile[] seqlist = vfFile.listFiles();
			if (seqlist != null) {
				for (VirtualFile filter : seqlist) {
					if (filter.exists() && filter.isDirectory()) {
						String name = filter.getName().toLowerCase();
						Set<VirtualFile> subset = map.get(name);
						if (subset == null) {
							subset = new HashSet<VirtualFile>();
							map.put(name, subset);
						}
						if (!subset.contains(filter)) {
							subset.add(filter);
						}
					}
				}
			}
		}
		
		return map;
	}

	/**
	 * 指定されたサブフィルタが既存のサブフィルタと同じ内容の場合、既存サブフィルタの抽象パスを返す。
	 * @return	同じとみなされたフィルタのパスを返す。同じものが存在しない場合は <tt>null</tt>
	 */
	static protected VirtualFile getSameExistSubFilter(final Map<String,Set<VirtualFile>> existSubFilters,final ModuleRuntimeData targetModule)
	{
		Set<VirtualFile> sameNameFilters = existSubFilters.get(targetModule.getName().toLowerCase());
		if (sameNameFilters == null || sameNameFilters.isEmpty())
			return null;	// no same filter

		VirtualFile vfTarget = targetModule.getExecDefDirectory();
		for (VirtualFile vfFilter : sameNameFilters) {
			if (isSameFilter(vfTarget, vfFilter)) {
				return vfFilter;
			}
		}
		return null;
	}

	/**
	 * モジュール情報を比較する。
	 * @param filter1	比較するモジュール情報の一方
	 * @param filter2	比較するモジュール情報のもう一方
	 * @return	同じフィルタとみなされた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static protected boolean isSameFilter(final VirtualFile file1, final VirtualFile file2)
	{
		// 名前
		if (!file1.getName().equalsIgnoreCase(file2.getName()))
			return false;
		
		// モジュール実行定義設定ファイルの比較
		VirtualFile vfPrefs1 = MExecDefFileManager.getModuleExecDefDataFile(file1);
		VirtualFile vfPrefs2 = MExecDefFileManager.getModuleExecDefDataFile(file2);
		//--- 最終更新日
		if (vfPrefs1.lastModified() != vfPrefs2.lastModified())
			return false;
		//--- サイズ
		if (vfPrefs1.length() != vfPrefs2.length())
			return false;
		//--- 内容
		{
			MExecDefSettings settings1 = new MExecDefSettings();
			MExecDefSettings settings2 = new MExecDefSettings();
			settings1.loadForTarget(vfPrefs1);
			settings2.loadForTarget(vfPrefs2);
			if (!settings1.equalsProperties(settings2))
				return false;
			//--- モジュールファイルの比較
			VirtualFile vfModule1 = settings1.getModuleFile();
			VirtualFile vfModule2 = settings2.getModuleFile();
			if (vfModule1==null || vfModule2==null)
				return false;	// 念のため
			if (vfModule1.lastModified() != vfModule2.lastModified())
				return false;
			if (vfModule1.length() != vfModule2.length())
				return false;
		}
		
		// マクロフィルタ定義ファイルの比較
		VirtualFile vfMacros1 = MExecDefFileManager.getMacroFilterDefDataFile(file1);
		VirtualFile vfMacros2 = MExecDefFileManager.getMacroFilterDefDataFile(file2);
		if (vfMacros1.exists() != vfMacros2.exists())
			return false;
		if (vfMacros1.exists()) {
			//--- 最終更新日
			if (vfMacros1.lastModified() != vfMacros2.lastModified())
				return false;
			//--- サイズ
			if (vfMacros1.length() != vfMacros2.length())
				return false;
			//--- 内容
			MExecDefMacroFilter settings1 = new MExecDefMacroFilter();
			MExecDefMacroFilter settings2 = new MExecDefMacroFilter();
			settings1.loadForTarget(vfMacros1);
			settings2.loadForTarget(vfMacros2);
			if (!settings1.equals(settings2))
				return false;
		}
		
		// ローカルファイルの内容が更新された場合、定義ファイルの日付も更新されるため、
		// これ以上のチェックは行わない
		return true;
	}
	
	static protected boolean isEqualsFilterFilePath(final VirtualFile file1, final VirtualFile base1,
														final VirtualFile file2, final VirtualFile base2)
	{
		if (file1 != null && file2 != null) {
			VirtualFile vfPath1 = file1.relativeFileFrom(base1, Files.CommonSeparatorChar);
			VirtualFile vfPath2 = file2.relativeFileFrom(base2, Files.CommonSeparatorChar);
			return Objects.isEqual(vfPath1, vfPath2);
		}
		else {
			return false;
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class MacroFilterDialogHandler implements IFilterDialogHandler
	{
		@Override
		public void onFileTreeSelectionChanged(MEDLocalFileTreePanel treePanel) {
			if (treePanel.isSelectionEmpty()) {
				// サブフィルタファイルツリーの選択解除
				if (_paneTree.isSelectionEmpty()) {
					//--- このダイアログのファイルツリーが選択されていなければ、ステータスバーの表示更新
					setStatusBarMessage(treePanel.getSelectedFileProperty());
				}
			}
			else {
				// サブフィルタファイルツリーの選択変更
				//--- このダイアログのファイルツリー選択状態を解除
				_paneTree.clearSelection();
				//--- サブフィルタのファイルツリー選択内容をステータスバーに表示
				setStatusBarMessage(treePanel.getSelectedFileProperty());
			}
		}

		@Override
		public boolean canCloseDialog(IFilterDialog dlg) {
			return false;
		}

		@Override
		public boolean doCloseFileOnEditor(IFilterDialog dlg, VirtualFile file) {
			return false;
		}

		@Override
		public boolean doOpenFileByCsvOnEditor(IFilterDialog dlg, VirtualFile file) {
			return false;
		}

		@Override
		public boolean doOpenFileOnEditor(IFilterDialog dlg, VirtualFile file) {
			return false;
		}

		@Override
		public String getStatusBarMessage() {
			return _statusbar.getMessage();
		}

		@Override
		public void setStatusBarMessage(String message) {
			_statusbar.setMessage(message);
		}

		@Override
		public void onClosedDialog(IFilterDialog dlg) {}

		@Override
		public void onHiddenDialog(IFilterDialog dlg) {}

		@Override
		public void onShownDialog(IFilterDialog dlg) {}
	}
	
	protected class SubFilterValuesEditModelListener implements IFilterValuesEditModelListener
	{
		@Override
		public void dataChanged(FilterValuesEditModelEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
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
	
	static public class CommitProgressTask extends VirtualFileProgressMonitorTask
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		private final MacroFilterEditModel	_editModel;
		/** オリジナルの抽象パス **/
		private final VirtualFile	_vfRootDir;
		private final boolean		_alreadyExistRootDir;
		/** 作業コピーの抽象パス **/
		private final VirtualFile	_vfWorkDir;
		/** 作業領域へコピーするファイルのマップ(Key=コピー元、Value=コピー先) **/
		private final Map<VirtualFile,VirtualFile>	_copyFiles;
		/** 作業領域から削除するファイルの集合(ディレクトリ含む) **/
		private final Set<VirtualFile>				_retainFiles;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		public CommitProgressTask(final MacroFilterEditModel editModel, final VirtualFile vfRootDir, final VirtualFile vfWorkDir)
		{
			super(RunnerMessages.getInstance().MExecDefEditDlg_Title_SaveMExecDef, null, null, 0, 0, 100, false);
			if (editModel == null)
				throw new IllegalArgumentException("Edit data model is null.");
			if (vfRootDir == null)
				throw new IllegalArgumentException("Root directory path is null.");
			if (vfWorkDir == null)
				throw new IllegalArgumentException("Working directory path is null.");
			this._editModel = editModel;
			this._vfRootDir = vfRootDir;
			this._vfWorkDir = vfWorkDir;
			this._copyFiles = new HashMap<VirtualFile, VirtualFile>();
			this._retainFiles = new HashSet<VirtualFile>();
			this._alreadyExistRootDir = (_vfRootDir==null ? false : _vfRootDir.exists());
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------
		
		@Override
		public void showError(Component parentComponent) {
			if (getErrorCause() instanceof IllegalStateException) {
				String errmsg = RunnerMessages.getInstance().msgMExecDefFailedToBuildForSave;
				AppLogger.error(errmsg, getErrorCause());
				Application.showErrorMessage(parentComponent, errmsg);
				return;
			}
			
			super.showError(parentComponent);
		}
		
		@Override
		public void processTask() throws Throwable
		{
			AppLogger.info("Start " + getClass().getName() + " task...");
			
			// データの生成
			MExecDefSettings defPrefs = new MExecDefSettings();
			{
				MExecDefMacroFilter defMacroFilter    = new MExecDefMacroFilter();
				MacroData           aadlMacroData     = new MacroData();
				MacroExecSettings	aadlMacroSettings = new MacroExecSettings();
				if (!buildCommitFileInfo(_editModel, defPrefs, defMacroFilter, aadlMacroData, aadlMacroSettings, _copyFiles, _retainFiles)) {
					throw new IllegalStateException("Failed to build files for commit.");
				}
				
				// 定義ファイルの保存
				//--- フィルタ定義ファイル
				defPrefs.commit();
				//--- マクロフィルタ定義ファイル
				defMacroFilter.commit();
				//--- AADLマクロ定義ファイル
				ensureParentDirectory(defPrefs.getModuleFile());
				CsvMacroFiles.toFile(aadlMacroData, ModuleFileManager.toJavaFile(defPrefs.getModuleFile()), AppSettings.getInstance().getAadlMacroEncodingName());			
				//--- AADLマクロ定義設定ファイル
				aadlMacroSettings.commit();
			}
			
			// モジュール実行定義ルートディレクトリの確認
			ensureDirectory(_vfRootDir);
			
			// 処理総数を算定し、プログレスを設定
			int maxPos = countProgressFiles() + 1;
			setMaximum(maxPos);
			
			// 必要なファイルのコピー
			copyAllFiles();

			// 引数のデータ型定義に合わせ、既存の履歴を消去
			VirtualFile vfHistory = _vfWorkDir.getChildFile(MExecDefFileManager.MEXECDEF_HISTORY_FILENAME);
			if (vfHistory.exists() && vfHistory.isFile()) {
				MExecDefHistory history = new MExecDefHistory();
				history.loadForTarget(vfHistory);
				if (!history.isHistoryEmpty()) {
					if (history.ensureArgsTypes(defPrefs) <= 0) {
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
			
			// 不要なファイルの削除
			cleanupFiles(_vfWorkDir, _retainFiles);
			
			// 作業コピーを移動
			setAllowOverwriteAllFilesFlag(true);	// 無条件に置換を許可
			//resetAllowOverwriteAllFilesFlag();		// （テスト）同名のファイルやディレクトリは置換確認
			_retainFiles.clear();
			VirtualFile[] subfiles = _vfWorkDir.listFiles();
			if (subfiles != null && subfiles.length > 0) {
				for (VirtualFile vfSrc : subfiles) {
					VirtualFile vfDst = _vfRootDir.getChildFile(vfSrc.getName());
					_retainFiles.add(vfDst);
					forceRenameTo(vfSrc, vfDst);
				}
			}
			
			// 正規ディレクトリの不要なファイルを削除
			subfiles = _vfRootDir.listFiles();
			if (subfiles != null) {
				for (VirtualFile vf : subfiles) {
					if (!_retainFiles.contains(vf)) {
						setValue(getValue()+vf.countFiles());
						deleteAllFiles(vf, true);
					}
				}
			}
			
			// 完了
			setValue(getMaximum());
			AppLogger.info(getClass().getName() + " task Completed!");
		}

		@Override
		public void cleanupTask() {
			// 作業コピーを強制的に破棄する
			setAllowOverwriteAllFilesFlag(true);	// 無条件に置換を許可
			deleteAllFiles(_vfWorkDir, true);	// 例外は無視する
			
			// 新規作成でエラーが発生した場合場合、作成したルートディレクトリは削除する
			if (!_alreadyExistRootDir && _vfRootDir != null) {
				if (_editModel.isNewEditing() && getErrorCause() != null) {
					setAllowOverwriteAllFilesFlag(true);	// 無条件に置換を許可
					deleteAllFiles(_vfRootDir, true);	// 例外は無視する
				}
			}
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------

		/**
		 * 指定された抽象パスの親ディレクトリが存在しない場合、そのディレクトリを作成する。
		 * @param vfFile	親ディレクトリを必要とするファイルの抽象パス
		 */
		protected void ensureParentDirectory(VirtualFile vfFile) {
			VirtualFile vfParent = vfFile.getParentFile();
			if (vfParent != null) {
				ensureDirectory(vfParent);
			}
		}

		/**
		 * 指定された抽象パスのディレクトリが存在しない場合、そのディレクトリを作成する。
		 * @param vfDir		ディレクトリを示す抽象パス
		 */
		protected void ensureDirectory(VirtualFile vfDir) {
			if (!vfDir.exists()) {
				try {
					if (!vfDir.mkdirs()) {
						throw new IOException("Failed to create directory.");
					}
				} catch (Throwable ex) {
					throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, vfDir, null, ex);
				}
			}
		}

		/**
		 * このタスクで処理するファイル総数を算出する。
		 */
		protected int countProgressFiles() {
			int numFiles = 0;
			
			// 正規のファイルと作業コピーのファイル総数を算出
			if (_vfWorkDir.isDescendingFrom(_vfRootDir)) {
				// 作業コピーは正規ディレクトリ下
				numFiles += _vfRootDir.countFiles();
			}
			else {
				// 作業コピーは正規ディレクトリ外
				numFiles += _vfRootDir.countFiles();
				numFiles += _vfWorkDir.countFiles();
			}
			
			// コピーするファイル数をカウント
			for (Map.Entry<VirtualFile, VirtualFile> entry : _copyFiles.entrySet()) {
				VirtualFile vfSource = entry.getKey();
				if (!vfSource.equals(entry.getValue())) {
					if (vfSource.exists()) {
						if (vfSource.isDirectory()) {
							//--- ディレクトリの場合は、中身もすべて対象
							numFiles += vfSource.countFiles();
						}
						else {
							++numFiles;
						}
					}
				}
			}
			
			return numFiles;
		}
		
		protected void removeMExecDefHistoryFile(VirtualFile vfFilterDir) {
			VirtualFile vfHist = vfFilterDir.getChildFile(MExecDefFileManager.MEXECDEF_HISTORY_FILENAME);
			if (vfHist.exists()) {
				deleteAllFiles(vfHist, true);	// 例外はスローしない
			}
		}
		
		protected void copyAllFiles() {
			byte[] buffer = new byte[BUF_SIZE];
			for (Map.Entry<VirtualFile, VirtualFile> entry : _copyFiles.entrySet()) {
				VirtualFile vfSource = entry.getKey();
				if (vfSource.exists()) {
					if (vfSource.isDirectory()) {
						//--- ディレクトリ
						copyDirectory(vfSource, entry.getValue(), buffer);
						//--- ディレクトリの場合はサブフィルタそのものの場合があり、
						//--- 引数履歴保存ファイルは削除する
						removeMExecDefHistoryFile(entry.getValue());
					}
					else {
						//--- 単一ファイル
						copyFile(vfSource, entry.getValue(), buffer);
					}
				}
			}
		}
		
		protected void copyDirectory(VirtualFile vfSrc, VirtualFile vfDst, byte[] buffer) {
			// ディレクトリの生成
			ensureDirectory(vfDst);
			
			// ファイルのコピー
			VirtualFile[] filelist = vfSrc.listFiles();
			if (filelist != null && filelist.length > 0) {
				for (VirtualFile vf : filelist) {
					if (vf.isDirectory()) {
						copyDirectory(vf, vfDst.getChildFile(vf.getName()), buffer);
					}
					else {
						copyFile(vf, vfDst.getChildFile(vf.getName()), buffer);
					}
				}
			}
			
			// 完了
			incrementValue();
		}
		
		protected void copyFile(VirtualFile vfSrc, VirtualFile vfDst, byte[] buffer)
		{
			// 親ディレクトリの生成
			ensureParentDirectory(vfDst);
			
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
			
			// 完了
			incrementValue();
		}
		
//		protected boolean cleanupSubFiles(VirtualFile file, final Set<VirtualFile> retainFiles) {
//			boolean canRemoveDir = true;
//			VirtualFile[] subfiles = file.listFiles();
//			if (subfiles != null) {
//				for (VirtualFile vf : subfiles) {
//					if (!cleanupFiles(vf, retainFiles)) {
//						//--- 除外ファイルが存在するため、このディレクトリは削除不可
//						canRemoveDir = false;
//					}
//				}
//			}
//			return canRemoveDir;
//		}
		
		protected boolean cleanupFiles(final VirtualFile file, final Set<VirtualFile> retainFiles) {
			if (file.exists()) {
				if (retainFiles.contains(file)) {
					// 除外対象のファイルなら削除しない。ディレクトリの場合は、サブファイルも全て削除しない
					setValue(getValue() + file.countFiles());
					return false;
				}
				
				if (file.isDirectory()) {
					// サブファイルの削除
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
					if (!canRemoveDir) {
						// このディレクトリは削除しない
						incrementValue();
						return false;
					}
				}
				
				// ファイルの削除
				try {
					file.delete();
					if (file.exists()) {
						fileDeleteExactly(file);
					}
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

		//------------------------------------------------------------
		// Inner classes
		//------------------------------------------------------------

		/**
		 * 指定されたファイルセットのすべてのファイルを削除する。
		 * このファイルセットは、削除する順序に並べられた集合であり、
		 * ディレクトリが含まれている場合、そのディレクトリにファイルが残っている場合は削除しない。
		 * @param removeFileSet		削除順に並べられた削除対象ファイルの集合
		 */
		protected void removeFiles(final Set<VirtualFile> removeFileSet) {
			for (VirtualFile file : removeFileSet) {
				if (file.exists()) {
					if (file.isDirectory()) {
						VirtualFile[] fl = file.listFiles();
						if (fl == null || fl.length <= 0) {
							deleteFile(file);
						}
					} else {
						deleteFile(file);
					}
				}
				incrementValue();
			}
		}

		/**
		 * 指定された抽象パスのファイルを削除する。
		 * このメソッドでは、削除に失敗した場合でも例外をスローする。
		 * @param file	削除するファイルの抽象パス
		 */
		protected void deleteFile(final VirtualFile file) {
			try {
				if (!fileDeleteExactly(file)) {
					throw new IOException("Failed to delete file.");
				}
			}
			catch (Throwable ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, file, null, ex);
			}
		}

		@Override
		protected String getDefaultErrorMessage() {
			return RunnerMessages.getInstance().msgMExecDefFailedToSave;
		}

	}
}
