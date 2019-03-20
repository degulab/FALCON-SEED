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
 * @(#)MacroFilterDefEditPane.java	3.1.0	2014/05/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroFilterDefEditPane.java	2.0.0	2012/10/31
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.macro.command.MacroAction;
import ssac.aadl.macro.data.MacroData;
import ssac.aadl.macro.data.MacroNode;
import ssac.aadl.macro.data.ModuleArgument;
import ssac.aadl.macro.file.CsvMacroFiles;
import ssac.aadl.macro.graph.AADLMacroGraphData;
import ssac.aadl.macro.graph.swing.AADLMacroGraphDialog;
import ssac.aadl.macro.graph.swing.ExportMacroGraphTask;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.ModuleFileType;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.aadl.module.swing.FilenameValidator;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.MacroSubModuleRuntimeData;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.ModuleDataList;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgPublish;
import ssac.falconseed.module.args.MExecArgString;
import ssac.falconseed.module.args.MExecArgSubscribe;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.swing.MacroFilterEditModel.EditType;
import ssac.falconseed.module.swing.table.AbMacroFilterDefArgTableModel;
import ssac.falconseed.module.swing.table.IMExecDefArgTableModel;
import ssac.falconseed.module.swing.table.MacroFilterDefArgTablePane;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.RunnerResources;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.Application;
import ssac.util.swing.JStaticMultilineTextPane;
import ssac.util.swing.MenuToggleButton;

/**
 * マクロフィルタ専用のフィルタ定義編集パネル。
 * 
 * @version 3.1.0	2014/05/26
 * @since 2.0.0
 */
public class MacroFilterDefEditPane extends JPanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final FilenameValidator _validator = FileDialogManager.createDefaultFilenameValidator();

	/** データモデル **/
	private MacroFilterEditModel		_editModel;
	
	/** 親フォルダの抽象パス **/
	private VirtualFile			_vfParent;

	/** 実行対象のモジュールファイル **/
	private JStaticMultilineTextPane	_stcModuleFile;
	/** 実行対象モジュールのメインクラス名 **/
	private JStaticMultilineTextPane	_stcMainClass;

	/** 名称編集用テキストフィールド **/
	private JTextField					_cNameField;
	/** 名称表示用テキストコンポーネント **/
	private JStaticMultilineTextPane	_cNameLabel;
	/** 格納位置のパス **/
	private JStaticMultilineTextPane	_stcLocation;
	/** 引数定義テーブル **/
	private MacroFilterDefArgTablePane	_argTable;
	/** 機能説明のテキストエリア **/
	private JTextArea					_taDesc;
	/** モジュール表示パネル **/
	private JPanel						_paneModule;

	/** 位置選択ボタン **/
	private JButton			_btnChooseLocation;
	/** モジュールのグラフ化ボタン **/
	private JButton		_btnGraphModule;
	/** 引数の編集ボタン **/
	private MenuToggleButton	_btnArgEdit;
	/** 引数の削除ボタン **/
	private JButton			_btnArgDel;
	/** 引数の上へ移動ボタン **/
	private JButton			_btnArgUp;
	/** 引数の下へ移動ボタン **/
	private JButton			_btnArgDown;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroFilterDefEditPane(final MacroFilterEditModel editModel) {
		super(new GridBagLayout());
		this._editModel = editModel;
		
		createComponents();
		layoutComponents();
		
		// 引数定義の相対パス表示のための基準パスを設定
		_argTable.setPathFormatter(_editModel.getPathFormatter());
		_argTable.setBasePath(_editModel.getAvailableFilterRootDirectory());
		_argTable.setEditModel(_editModel);
		
		// 固定モジュール情報の設定
		redisplayModuleInfo();
		
		// 定義情報の表示
		_vfParent = _editModel.getFilterParentDirectory();
		setMExecDefDescription(_editModel.getMExecDefDescription());
		setLocationPath(_vfParent);
		setMExecDefName(_editModel.getFilterName());
		
		setupActions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 現在設定されているフィルタ定義が配置されるディレクトリを返す。
	 * @return フィルタの親ディレクトリを示す抽象パスを返す。
	 * 			未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefParentDirectory() {
		return _vfParent;
	}
	
	/**
	 * 現在コンポーネントに設定されているフィルタ定義の場所とフィルタ名から、
	 * モジュール実行定義ディレクトリを返す。モジュール実行定義ディレクトリが
	 * 配置される親ディレクトリやフィルタ名が未定の場合は <tt>null</tt> を返す。
	 * @return	フィルタのルートディレクトリを示す抽象パスを返す。
	 * 			ルートディレクトリが未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getMExecDefDirectory() {
		VirtualFile vfDir = null;
		String name = getMExecDefName();
		if (_vfParent != null && name != null && name.length() > 0) {
			vfDir = _vfParent.getChildFile(name);
		}
		return vfDir;
	}

	/**
	 * 現在のフィルタ名を返す。未定義の場合は <tt>null</tt> を返す。
	 * @return	現在のフィルタ名
	 */
	
	public String getMExecDefName() {
		return getNameTextComponent().getText();
	}

	/**
	 * 現在のフィルタ説明を返す。
	 * @return	現在のフィルタ機能説明
	 */
	public String getMExecDefDescription() {
		return _taDesc.getText();
	}
	
	public void adjustArgTableAllColumnsPreferredWidth() {
		_argTable.adjustTableAllColumnsPreferredWidth();
	}
	
	public void updateStatus() {
		_argTable.updateButtons();
	}
	
	public AbMacroFilterDefArgTableModel getTableModel() {
		return _editModel.getMExecDefArgTableModel();
	}

	/**
	 * モジュール実行定義名の正当性をチェックする。
	 * @return	モジュール実行定義名で新しいディレクトリが作成可能な場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean checkFilename() {
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
	public boolean checkArguments() {
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
				String errmsg = MacroFilterEditModel.formatArgNo(row) + " " + RunnerMessages.getInstance().msgMExecDefArgNothingAttr;
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
					String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseFixedOutput;
					Application.showErrorMessage(errmsg);
					return false;
				}
				else if (ModuleArgType.IN != type) {
					String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseFile;
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
						String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + CommonMessages.getInstance().msgFileNotFound;
						Application.showErrorMessage(errmsg);
						return false;
					}
				}
			}
			else if (val instanceof IMExecArgParam) {
				// Parameter
				if (val instanceof MExecArgString) {
					if (ModuleArgType.STR != type) {
						String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseString;
						Application.showErrorMessage(errmsg);
						return false;
					}
				}
				else if (val instanceof MExecArgPublish) {
					if (ModuleArgType.PUB != type) {
						String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUsePubParam;
						Application.showErrorMessage(errmsg);
						return false;
					}
				}
				else if (val instanceof MExecArgSubscribe) {
					if (ModuleArgType.SUB != type) {
						String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseSubParam;
						Application.showErrorMessage(errmsg);
						return false;
					}
				} else {
					if (ModuleArgType.IN != type && ModuleArgType.OUT != type) {
						String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseFile;
						Application.showErrorMessage(errmsg);
						return false;
					}
					if (val instanceof MExecArgTempFile) {
						if (ModuleArgType.IN == type) {
							String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseTempFile;
							Application.showErrorMessage(errmsg);
							return false;
						}
					}
				}
			}
			else {
				// String
				if (val == null || val.toString().length() < 1) {
					String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgNothingValue;
					Application.showErrorMessage(errmsg);
					return false;
				}
				
				if (ModuleArgType.STR != type && ModuleArgType.PUB != type && ModuleArgType.SUB != type) {
					String errmsg = MacroFilterEditModel.formatArgType(row, type) + " " + RunnerMessages.getInstance().msgMExecDefArgCannotUseString;
					Application.showErrorMessage(errmsg);
					return false;
				}
			}
		}
		
		// 正当
		return true;
	}

	public void restoreConfiguration(ExConfiguration config, String prefix) {
		// no entry
	}

	public void storeConfiguration(ExConfiguration config, String prefix) {
		// no entry
	}

	//------------------------------------------------------------
	// Actions
	//------------------------------------------------------------

	/**
	 * フィルタの配置フォルダ選択ボタンをクリックしたときのイベント
	 */
	protected void onChooseLocationButton(ActionEvent ae) {
		// 編集モードのチェック
		if (_editModel==null || _editModel.getEditType() != EditType.NEW) {
			throw new IllegalStateException("MacroFilterDefEditPane#onChooseLocationButton : Edit mode is not NEW : editMode=" + String.valueOf(_editModel.getEditType()));
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

	/**
	 * グラフ表示ボタンをクリックしたときのイベント
	 */
	protected void onGraphModule(ActionEvent ae) {
		if (_editModel == null) {
			return;		// no process
		}
		
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
		
		// AADLマクロ定義ファイルの取得
		VirtualFile vfMacroFile = null;
		if (_editModel.isEditing()) {
			// 編集中なら、テンポラリのマクロファイルを生成
			File fTempMacro = null;
			try {
				fTempMacro = createTemporaryMacroFile();
				fTempMacro.deleteOnExit();	// 念のため
				generateAADLMacroFile(fTempMacro, _editModel.getSubFilterList());
			} catch (Throwable ex) {
				String errmsg = RunnerMessages.getInstance().msgGraphVizFailedToCreateWorkFile;
				AppLogger.error(errmsg, ex);
				Application.showErrorMessage(this, errmsg);
				return;
			}
			vfMacroFile = ModuleFileManager.fromJavaFile(fTempMacro);
		} else {
			// 閲覧なら、モジュールファイルをそのまま指定
			vfMacroFile = _editModel.getMExecDefModuleFile();
		}
		
		// GraphViz の実行
		ExportMacroGraphTask task = new ExportMacroGraphTask(RunnerMessages.getInstance().MExecDefEditDlg_Graph_ProgressTitle, fGraphViz, vfMacroFile, AppSettings.getInstance().getAadlMacroEncodingName());
		if (!task.execute(this) || !task.isSucceeded()) {
			task.showError(this);
			return;
		}
		AADLMacroGraphData graphData = new AADLMacroGraphData(task.getAADLMacroFile());
		graphData.setDotFile(ModuleFileManager.fromJavaFile(task.getGraphDotFile()));
		graphData.setImageFile(ModuleFileManager.fromJavaFile(task.getGraphImageFile()));
		graphData.setGraphImage(task.getGraphImage());
		
		// テンポラリマクロファイルの削除
		if (_editModel.isEditing()) {
			try {
				vfMacroFile.delete();
			} catch (Throwable ignoreEx) {ignoreEx = null; }
		}
		
		// グラフのダイアログ表示
		AADLMacroGraphDialog dlg;
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Dialog)
			dlg = new AADLMacroGraphDialog((Dialog)w, RunnerMessages.getInstance().AADLMacroGraphDlg_Title, true, graphData);
		else if (w instanceof Frame)
			dlg = new AADLMacroGraphDialog((Frame)w, RunnerMessages.getInstance().AADLMacroGraphDlg_Title, true, graphData);
		else
			dlg = new AADLMacroGraphDialog((Frame)null, RunnerMessages.getInstance().AADLMacroGraphDlg_Title, true, graphData);
		dlg.initialComponent();
		dlg.setVisible(true);
	}

	/**
	 * 指定されたフィルタリストから、指定された抽象パスにAADLマクロ定義ファイルを生成する。
	 * 指定された抽象パスには、生成されたAADLマクロ定義ファイルが強制的に上書きされる。
	 * また、AADLマクロ定義ファイルを格納するディレクトリが存在していない場合は、例外をスローする。
	 * @param fMacroFile		AADLマクロ定義ファイルの出力先を示す抽象パス
	 * @param filterList		マクロとして実行するフィルタ情報が格納されたリスト
	 * @throws IOException		ファイルが出力できなかった場合
	 */
	static public void generateAADLMacroFile(final File fMacroFile, final ModuleDataList<? extends ModuleRuntimeData> filterList) throws IOException
	{
		MacroData mdata = new MacroData();
		buildAADLMacroData(mdata, ModuleFileManager.fromJavaFile(fMacroFile.getParentFile()), filterList);
		CsvMacroFiles.toFile(mdata, fMacroFile, AppSettings.getInstance().getAadlMacroEncodingName());
	}

	/**
	 * 指定されたモジュールデータの実行番号から、一意のモジュールIDを取得する。
	 * @param moduledata	モジュールデータ
	 * @return	モジュールID
	 * @throws NullPointerException	モジュールデータが <tt>null</tt> の場合
	 * @since 3.1.0
	 */
	static public String getModuleIdByRunNo(ModuleRuntimeData moduledata) {
		return String.format("MID_%d", moduledata.getRunNo());
	}

	/**
	 * 待機されずに実行中のすべてのモジュールを待機するコマンドを生成
	 * @param noWaitModuleIdSet	待機されていないモジュールIDの集合
	 * @return	待機コマンド
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 * @throws NoSuchElementException	待機モジュールID集合が空の場合
	 * @since 3.1.0
	 */
	static String createWaitCommand(Set<String> noWaitModuleIdSet) {
		StringBuilder sb = new StringBuilder();
		sb.append("wait(");
		Iterator<String> it = noWaitModuleIdSet.iterator();
		sb.append(it.next());
		for (; it.hasNext(); ) {
			sb.append(',');
			sb.append(it.next());
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * 待機するモジュールデータの集合から、非同期実行コマンドオプションを生成する。
	 * 待機するモジュールの集合が空の場合、非同期で即座に実行を開始するコマンドオプションを生成する。
	 * 待機するモジュールの集合が空ではない場合、指定モジュール実行完了後のスケジュール実行コマンドオプションを生成する。
	 * なお、待機の有無により、<em>noWaitModuleIdSet</em> の内容が変更される。
	 * @param waitModules	待機するモジュールの集合
	 * @param targetModuleId	このコマンドオプションを適用するモジュールのモジュールID
	 * @param noWaitModuleIdSet	待機されないモジュールIDの集合(このメソッドが変更する)
	 * @return	生成されたコマンドオプション(セミコロン(:)を終端に付加)
	 * @throws NullPointerException	待機するモジュールの集合が <tt>null</tt> の場合
	 * @since 3.1.0
	 */
	static String createAsyncCommandOption(Set<MacroSubModuleRuntimeData> waitModules, String targetModuleId, Set<String> noWaitModuleIdSet) {
		if (waitModules.isEmpty()) {
			//--- 待機するモジュールがないので、非同期に即実行
			noWaitModuleIdSet.add(targetModuleId);	// 即実行するモジュールIDを待機されないモジュールID集合へ追加
			return "start:";
		}
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("after(");
			Iterator<MacroSubModuleRuntimeData> it = waitModules.iterator();
			String waitModuleId = getModuleIdByRunNo(it.next());
			sb.append(waitModuleId);
			noWaitModuleIdSet.remove(waitModuleId);	// 待機されたモジュールIDは、待機されないID集合から除外
			for (; it.hasNext(); ) {
				sb.append(',');
				waitModuleId = getModuleIdByRunNo(it.next());
				sb.append(waitModuleId);
				noWaitModuleIdSet.remove(waitModuleId);	// 待機されたモジュールIDは、待機されないID集合から除外
			}
			sb.append("):");
			//--- このモジュール自身は非同期的にスケジュール実行されるので、待機されないモジュールID集合へIDを追加
			noWaitModuleIdSet.add(targetModuleId);
			return sb.toString();
		}
	}

	/**
	 * 指定されたサブフィルタリストから、AADLマクロ定義データを生成する。
	 * マクロに保存されるデータは、通常 <em>vfMacroDir</em> からの相対パスとして設定される。
	 * <em>vfMacroDir</em> が <tt>null</tt> の場合は、そのまま保存される。
	 * @param destMacroData		AADLマクロ定義を格納するオブジェクト(内容は初期化されない)
	 * @param vfMacroDir		マクロファイルが配置されるディレクトリの抽象パス
	 * @param filterList		マクロとして実行するフィルタ情報が格納されたリスト
	 * @return	生成された AADL マクロデータオブジェクト
	 * @throws NullPointerException	<em>subFilterList</em> が <tt>null</tt> の場合
	 */
	static void buildAADLMacroData(final MacroData destMacroData, final VirtualFile vfMacroDir, final ModuleDataList<? extends ModuleRuntimeData> filterList)
	{
		HashSet<String> noWaitModuleIdSet = new HashSet<String>();	// 待機されなかったモジュールIDの集合
		HashMap<ModuleArgID,String> mapOutValue = new HashMap<ModuleArgID, String>();
		int lastNoWaitAsyncModuleIndex = (-1);	// 直前の待機なし非同期実行モジュールのマクロデータにおけるノードリストのインデックス
		for (ModuleRuntimeData moduledata : filterList) {
			boolean noWaitAsyncModule = false;	// 現在のモデルが待機なし非同期実行モデルなら true
			String moduleID = getModuleIdByRunNo(moduledata);
			
			// フィルタのエントリを生成
			MacroNode mnode = new MacroNode();
			//--- コマンド
			String strCommand;
			if (moduledata.getModuleType() == ModuleFileType.AADL_MACRO) {
				//--- マクロファイル
				strCommand = MacroAction.MACRO.toString();
			} else {
				//--- JAR ファイル
				strCommand = MacroAction.JAVA.toString();
			}
			if (moduledata instanceof MacroSubModuleRuntimeData) {
				// 実行順序リンクを反映(@since 3.1.0)
				MacroSubModuleRuntimeData subModule = (MacroSubModuleRuntimeData)moduledata;
				if (subModule.getRunOrderLinkEnabled()) {
					// 実行順序オプションをコマンドに付加
					Set<MacroSubModuleRuntimeData> waitModules = subModule.getWaitModuleSet();
					strCommand = createAsyncCommandOption(waitModules, moduleID, noWaitModuleIdSet).concat(strCommand);
					//--- 非同期実行のモジュールには、プロセス名を付加
					mnode.setProcessName(moduleID);
					//--- 待機なし非同期実行モデルなら、挿入位置を更新
					if (subModule.isEmptyWaitModules()) {
						noWaitAsyncModule = true;
						++lastNoWaitAsyncModuleIndex;
					}
				}
				else {
					// 同期実行
					if (!noWaitModuleIdSet.isEmpty()) {
						// 待機されなかったモジュールがあれば、待機するコマンドを付加
						MacroNode waitNode = new MacroNode();
						waitNode.setCommand(createWaitCommand(noWaitModuleIdSet));
						//--- ノードの追加
						destMacroData.addMacroNode(waitNode);
						//--- 待機されなかったモジュールをクリア
						noWaitModuleIdSet.clear();
					}
					if (!subModule.isEmptyNextModules()) {
						// 実行順序指定における待機対象モジュールなら、プロセス名を付加
						mnode.setProcessName(moduleID);
					}
				}
			}
			else if (!noWaitModuleIdSet.isEmpty()) {
				// 待機されなかったモジュールがあれば、待機するコマンドを付加
				MacroNode waitNode = new MacroNode();
				waitNode.setCommand(createWaitCommand(noWaitModuleIdSet));
				//--- ノードの追加
				destMacroData.addMacroNode(waitNode);
				//--- 待機されなかったモジュールをクリア
				noWaitModuleIdSet.clear();
			}
			mnode.setCommand(strCommand);
			//--- コメント
			String strComment = moduledata.getComment();
			if (Strings.isNullOrEmpty(strComment)) {
				//--- コメントが指定されていなければ、実行番号とモジュール名
				mnode.setComment(String.format("%d:%s", moduledata.getRunNo(), moduledata.getName()));
			} else {
				//--- コメントが指定されていれば、そのコメントを使用
				mnode.setComment(strComment);
			}
			//--- モジュール
			if (vfMacroDir != null)
				mnode.setJarModulePath(moduledata.getModuleFile().relativePathFrom(vfMacroDir, Files.CommonSeparatorChar));
			else
				mnode.setJarModulePath(moduledata.getModuleFile().getPath());
			//--- 引数
			for (ModuleArgConfig argdata : moduledata) {
				//--- value
				String strValue = argumentValueToMacroString(vfMacroDir, mapOutValue, moduledata, argdata);
				//--- type
				ModuleArgType argtype = argdata.getType();
				String strType;
				if (ModuleArgType.IN == argtype) {
					strType = ModuleArgument.ARGTYPE_IN;
				}
				else if (ModuleArgType.OUT == argtype) {
					strType = ModuleArgument.ARGTYPE_OUT;
					// [OUT] 引数の場合は、位置と値を保存
					ModuleArgID argid = new ModuleArgID(moduledata, argdata.getArgNo());
					mapOutValue.put(argid, strValue);
				}
				else if (ModuleArgType.STR == argtype) {
					strType = ModuleArgument.ARGTYPE_STR;
				}
				else if (ModuleArgType.PUB == argtype) {
					strType = ModuleArgument.ARGTYPE_PUB;
					// [PUB] 引数の場合は、位置と値を保存
					ModuleArgID argid = new ModuleArgID(moduledata, argdata.getArgNo());
					mapOutValue.put(argid, strValue);
				}
				else if (ModuleArgType.SUB == argtype) {
					strType = ModuleArgument.ARGTYPE_SUB;
				}
				else {
					strType = "";
				}
				//--- 引数追加
				mnode.addModuleArgument(strType, strValue);
			}
			//--- ノードの追加
			if (noWaitAsyncModule) {
				//--- 待機なし非同期実行モデルなら、指定の挿入位置へ挿入
				destMacroData.insertMacroNode(lastNoWaitAsyncModuleIndex, mnode);
			} else {
				//--- リストの終端へ追加
				destMacroData.addMacroNode(mnode);
			}
		}

		// 作成完了
		mapOutValue.clear();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたサブフィルタの引数値を、AADLマクロの引数値として設定可能な文字列に変換する。
	 * @param vfMacroDir	マクロファイルが配置されるディレクトリの抽象パス
	 * @param mapOutValue	他サブフィルタの出力引数値のマップ
	 * @param moduledata	対象のフィルタ情報
	 * @param argdata		対象の引数値
	 * @return	AADLマクロ用に整形された文字列を返す。
	 */
	static protected String argumentValueToMacroString(final VirtualFile vfMacroDir, final Map<ModuleArgID, String> mapOutValue,
														final ModuleRuntimeData moduledata, final ModuleArgConfig argdata)
	{
		String strValue;
		Object value = argdata.getValue();

		if (argdata.getOutToTempEnabled() || (value instanceof MExecArgTempFile)) {
			// テンポラリ出力
			strValue = String.format("$tmp{%d:%d}", moduledata.getRunNo(), argdata.getArgNo());
		}
		else if (value instanceof FilterArgReferValueEditModel) {
			// フィルタ定義引数の参照（prefix/suffix付き)
			FilterArgReferValueEditModel refval = (FilterArgReferValueEditModel)value;
			strValue = String.format("%s${%d}%s", refval.getPrefix(), refval.getArgNo(), refval.getSuffix());
		}
		else if (value instanceof IModuleArgConfig) {
			// フィルタ定義引数の参照
			strValue = String.format("${%d}", ((IModuleArgConfig)value).getArgNo());
		}
		else if (value instanceof ModuleArgID) {
			// 他フィルタの引数参照
			strValue = mapOutValue.get((ModuleArgID)value);
			if (strValue == null) {
				AppLogger.warn("(MacroFilterDefEditPane#argumentValueToMacroString - Failed to refer to argument of other filter : " + value.toString());
				strValue = value.toString();
			}
		}
		else if (value instanceof VirtualFile) {
			// ファイル
			if (vfMacroDir != null)
				strValue = ((VirtualFile)value).relativePathFrom(vfMacroDir, Files.CommonSeparatorChar);
			else
				strValue = ((VirtualFile)value).getPath();
		}
		else if ((value instanceof VirtualFile) || (value instanceof File)) {
			// ファイル
			VirtualFile vfFile = ModuleFileManager.fromJavaFile((File)value);
			if (vfMacroDir != null)
				strValue = vfFile.relativePathFrom(vfMacroDir, Files.CommonSeparatorChar);
			else
				strValue = vfFile.getPath();
		}
		else if (value != null) {
			// 文字列
			strValue = value.toString();
		}
		else {
			// null は空文字
			strValue = "";
		}
		
		return strValue;
	}

	/**
	 * マクロファイルを一時的に生成するための、テンポラリファイルを生成する。
	 * @return	テンポラリファイルの抽象パス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected File createTemporaryMacroFile() throws IOException
	{
		File fDir = ModuleFileManager.toJavaFile(_editModel.getMacroRootDirectory());
		if (fDir != null) {
			if (!fDir.exists()) {
				fDir.mkdirs();
			}
			if (!fDir.exists() || !fDir.isDirectory()) {
				fDir = null;
			}
		}
		
		return AppSettings.createTemporaryFile(fDir, MExecDefFileManager.MACROFILTER_AADLMACRO_BASENAME, ModuleFileManager.EXT_AADL_MACRO, true);	// delete on exit
	}
	
	protected void redisplayModuleInfo() {
		String path = MExecDefFileManager.MACROFILTER_MACRO_ROOT_DIRNAME + "/" + MExecDefFileManager.MACROFILTER_AADLMACRO_FILENAME;
		String mainClass = (_editModel==null ? "" : _editModel.getMExecDefMainClass());
		_stcModuleFile.setText(path);
		_stcMainClass.setText(mainClass);
	}
	
	protected void setMExecDefName(String text) {
		getNameTextComponent().setText(Strings.nullToEmpty(text));
	}
	
	protected void setMExecDefDescription(String text) {
		_taDesc.setText(Strings.nullToEmpty(text));
		_taDesc.setCaretPosition(0);
	}
	
	protected void setLocationPath(VirtualFile file) {
		_stcLocation.setText(_editModel==null ? file.toString() : _editModel.formatFilePath(file));
	}
	
	protected void createComponents() {
		// create components
		_stcLocation = createFilePathLabel();
		_stcModuleFile = createFilePathLabel();
		_stcMainClass  = createFilePathLabel();
		_argTable    = createArgTable();
		_taDesc      = createDescTextComponent();
		
		// create Buttons
		_btnChooseLocation = CommonResources.createBrowseButton(RunnerMessages.getInstance().MExecDefEditDlg_Tooltip_ChooseLocation);
		_btnGraphModule    = CommonResources.createIconButton(RunnerResources.ICON_GRAPH, RunnerMessages.getInstance().MExecDefEditDlg_Tooltip_OutputGraph);
		_btnArgEdit  = CommonResources.createMenuIconButton(CommonResources.ICON_EDIT, CommonMessages.getInstance().Button_Edit);
		_btnArgDel   = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		_btnArgUp    = CommonResources.createIconButton(CommonResources.ICON_ARROW_UP, CommonMessages.getInstance().Button_Up);
		_btnArgDown  = CommonResources.createIconButton(CommonResources.ICON_ARROW_DOWN, CommonMessages.getInstance().Button_Down);
		
		_argTable.attachEditButton(_btnArgEdit);
		_argTable.attachDeleteButton(_btnArgDel);
		_argTable.attachUpButton(_btnArgUp);
		_argTable.attachDownButton(_btnArgDown);
		
		_paneModule = createModulePanel();
		
		// setup component state
		switch (_editModel.getEditType()) {
			case VIEW :
				{
					_cNameField = null;
					_cNameLabel = createNameLabel();
					_btnChooseLocation.setVisible(false);
					_btnArgEdit.setVisible(false);
					_btnArgDel .setVisible(false);
					_btnArgUp  .setVisible(false);
					_btnArgDown.setVisible(false);
					_argTable  .setEditable(false);		// read only
					_taDesc    .setEditable(false);		// readonly
				}
				break;
			case NEW :
				{
					_cNameField = createNameTextField();
					_cNameLabel = null;
					_btnChooseLocation.setVisible(true);
					_btnArgEdit.setVisible(true);
					_btnArgDel .setVisible(true);
					_btnArgUp  .setVisible(true);
					_btnArgDown.setVisible(true);
					_argTable  .setEditable(true);
					_taDesc    .setEditable(true);
				}
				break;
			default :
				{
					_cNameField = null;
					_cNameLabel = createNameLabel();
					_btnChooseLocation.setVisible(false);
					_btnArgEdit.setVisible(true);
					_btnArgDel .setVisible(true);
					_btnArgUp  .setVisible(true);
					_btnArgDown.setVisible(true);
					_argTable  .setEditable(true);
					_taDesc    .setEditable(true);
				}
		}
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
		}
		
		// スクロールペインを設定
		//--- desc
		JScrollPane scDesc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
											JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scDesc.setViewportView(_taDesc);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		//--- initialize
		Insets vertSpacing = new Insets(2, 0, 0, 0);
		Insets compMargin  = new Insets(2, 2, 0, 0);
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
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		this.add(lblName, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.insets = vertSpacing;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(getNameTextComponent(), gbc);
		gbc.insets = vertSeparate;
		gbc.gridy++;
		//--- location
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		this.add(lblLocation, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.insets = vertSpacing;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(_stcLocation, gbc);
		//gbc.gridy++;
		gbc.gridx = 1;
		gbc.gridwidth = 1;
		gbc.insets = compMargin;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.NONE;
		this.add(_btnChooseLocation, gbc);
		gbc.insets = vertSeparate;
		gbc.gridy++;
		//--- module
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(_paneModule, gbc);
		gbc.insets = vertSeparate;
		gbc.gridy++;
		//--- args
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		this.add(lblArgs, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.insets = vertSpacing;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(pnlArgs, gbc);
		gbc.insets = vertSeparate;
		gbc.weighty = 0;
		gbc.gridy++;
		//--- desc
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		this.add(lblDesc, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.insets = vertSpacing;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(scDesc, gbc);
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
		this.setMinimumSize(dmSize);
		this.setPreferredSize(dmSize);
	}

	protected void setupActions() {
		// 引数テーブルのドロップを、編集モードなら許可する
		_argTable.setValueDropEnabled(_editModel.isEditing());
		
		// ボタンアクション
		_btnChooseLocation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onChooseLocationButton(ae);
			}
		});
		_btnGraphModule.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onGraphModule(ae);
			}
		});
	}
	
	protected JPanel createModuleArgsTablePanel() {
		// create panel (no border)
		JPanel pnl = new JPanel(new GridBagLayout());
		
		// adjust button preferred size
		Dimension dm = _btnArgEdit.getPreferredSize();
		_btnArgEdit.setMinimumSize(dm);
		_btnArgDel.setPreferredSize(dm);
		_btnArgDel.setMinimumSize(dm);
		_btnArgUp.setPreferredSize(dm);
		_btnArgUp.setMinimumSize(dm);
		_btnArgDown.setPreferredSize(dm);
		_btnArgDown.setMinimumSize(dm);

		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 4;
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
		
		gbc.gridy = 2;
		pnl.add(_btnArgUp, gbc);
		
		gbc.gridy = 3;
		pnl.add(_btnArgDown, gbc);
		
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
	
	protected MacroFilterDefArgTablePane createArgTable() {
		MacroFilterDefArgTablePane pane = new MacroFilterDefArgTablePane();
		pane.initialComponent();
		return pane;
	}
	
	protected JTextArea createDescTextComponent() {
		JTextArea ta = new JTextArea();
		ta.setEditable(true);
		ta.setLineWrap(true);
		return ta;
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

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
