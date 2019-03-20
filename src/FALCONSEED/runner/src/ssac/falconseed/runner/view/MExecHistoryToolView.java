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
 * @(#)MExecHistoryToolView.java	3.1.3	2015/05/19 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecHistoryToolView.java	3.0.0	2014/03/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecHistoryToolView.java	2.0.0	2012/11/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecHistoryToolView.java	1.22	2012/08/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileType;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.RelatedModuleList;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.setting.MExecDefSettings;
import ssac.falconseed.module.swing.MacroFilterEditDialog;
import ssac.falconseed.module.swing.MacroFilterEditModel;
import ssac.falconseed.module.swing.table.MExecHistoryTableModel;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.RunnerResources;
import ssac.falconseed.runner.menu.RunnerMenuBar;
import ssac.falconseed.runner.menu.RunnerMenuResources;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.dialog.AsyncProcessMonitorWindow;
import ssac.falconseed.runner.view.dialog.MessageDetailDialog;
import ssac.util.Objects;
import ssac.util.Validations;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.MenuItemResource;
import ssac.util.swing.table.SpreadSheetColumnHeader;
import ssac.util.swing.table.SpreadSheetRowHeader;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * フィルタ実行履歴のビュー
 * 
 * @version 3.1.3	2015/05/19
 * @since 1.22
 */
public class MExecHistoryToolView extends JPanel implements IRunnerToolView
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -4409899678343905173L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final ToolBarButtonListener	_hToolBarButtonSelection = new ToolBarButtonListener();

	/** 履歴記録有効状態を示すトグルボタン **/
	private JToggleButton			_btnHistRecordingEnabled;
	private JButton				_btnHistRunSelected;
	private JButton				_btnHistRunFrom;
	private JButton				_btnHistRunBefore;
	private JButton				_btnHistRunLatest;
	private JButton				_btnHistRunAsSelected;
	private JButton				_btnHistRunAsFrom;
	private JButton				_btnHistRunAsBefore;
	private JButton				_btnHistRunAsLatest;
	private JButton				_btnHistDelete;
	private JButton				_btnHistNewFilter;
	/**
	 * フィルタ実行時にコンソールを表示する設定
	 * @since 3.0.0
	 */
	private JCheckBox				_chkConsoleShowAtStart;
	/**
	 * フィルタ実行後にコンソールを閉じる設定
	 * @since 3.0.0
	 */
	private JCheckBox				_chkConsoleAutoClose;
	/** 履歴を保持するテーブルモデル **/
	private MExecHistoryTableModel	_model;
	/** テーブルコンポーネント **/
	private MExecHistoryTable		_cTable;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	public MExecHistoryToolView(JCheckBox chkConsoleShowAtStart, JCheckBox chkConsoleAutoClose) {
		super(new BorderLayout());
		_model = new MExecHistoryTableModel();
		this._chkConsoleAutoClose  = chkConsoleAutoClose;
		this._chkConsoleShowAtStart= chkConsoleShowAtStart;
	}
	
	public void initialComponent() {
		// create components
		this._btnHistRecordingEnabled = createHistoryRecordingEnabledButton();
		this._btnHistRunSelected   = createToolBarButton(RunnerMenuResources.ID_FILTER_HISTORY_RUN_SELECTED);
		this._btnHistRunFrom       = createToolBarButton(RunnerMenuResources.ID_FILTER_HISTORY_RUN_FROM);
		this._btnHistRunBefore     = createToolBarButton(RunnerMenuResources.ID_FILTER_HISTORY_RUN_BEFORE);
		this._btnHistRunLatest     = createToolBarButton(RunnerMenuResources.ID_FILTER_HISTORY_RUN_LATEST);
		this._btnHistRunAsSelected = createToolBarButton(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_SELECTED);
		this._btnHistRunAsFrom     = createToolBarButton(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_FROM);
		this._btnHistRunAsBefore   = createToolBarButton(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_BEFORE);
		this._btnHistRunAsLatest   = createToolBarButton(RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_LATEST);
		this._btnHistDelete        = createToolBarButton(RunnerMenuResources.ID_FILTER_HISTORY_DELETE);
		this._btnHistNewFilter     = createToolBarButton(RunnerMenuResources.ID_FILTER_NEW_BY_HISTORY);
		this._cTable = new MExecHistoryTable();
		this._cTable.setModel(_model);
		this._cTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);	// 不連続複数選択可能
		this._cTable.setRowSelectionAllowed(true);		// 行選択を許可
		this._cTable.setColumnSelectionAllowed(false);	// 列選択は許可しない
		SpreadSheetRowHeader rowHeader = _cTable.getTableRowHeader();
		
		// setup Main component
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sc.setViewportView(_cTable);
		sc.setRowHeaderView(rowHeader);
		sc.getViewport().setBackground(UIManager.getColor("Table.background"));
		JPanel panel = new JPanel();
		panel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		sc.setCorner(JScrollPane.UPPER_LEFT_CORNER, panel);
		this.add(sc, BorderLayout.CENTER);
		
		// create tool bar
		JToolBar bar = createToolBar();
		this.add(bar, BorderLayout.NORTH);

		// setup actions
		setupActions();
	}
	
	protected void initialSetup(RunnerFrame frame) {
		RunnerMenuBar menuBar = frame.getDefaultMainMenuBar();
		// メニューアクセラレータキーを有効にするため、
		// コンポーネントのマップを変更
		KeyStroke[] strokes = SwingTools.getMenuAccelerators(menuBar);
		_cTable.registMenuAcceleratorKeyStroke(strokes);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 履歴記録が有効な場合に <tt>true</tt> を返す。
	 */
	public boolean isHistoryRecordingEnabled() {
		return _btnHistRecordingEnabled.isSelected();
	}

	/**
	 * 履歴記録の有効／無効を設定する。
	 * @param toEnable	有効にする場合は <tt>true</tt>、無効にする場合は <tt>false</tt> を指定する。
	 */
	public void setHistoryRecordingEnabled(boolean toEnable) {
		if (_btnHistRecordingEnabled.isSelected() != toEnable) {
			_btnHistRecordingEnabled.setSelected(toEnable);
			getFrame().updateMenuItem(RunnerMenuResources.ID_FILTER_RECORD_HISTORY);
		}
	}

	/**
	 * 実行履歴を追加する。
	 * このメソッドでは、履歴記録が有効ではない場合、履歴は追加しない。
	 * @param data	実行結果を含むモジュール実行時データ
	 * @return	追加された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.22
	 */
	public boolean addHistory(final ModuleRuntimeData data) {
		if (!isHistoryRecordingEnabled()) {
			return false;
		}
		
		_model.addItem(data);
		int targetRowIndex = _model.getRowCount()-1;
		_cTable.setRowSelectionInterval(targetRowIndex, targetRowIndex);
		_cTable.scrollToVisibleCell(targetRowIndex, MExecHistoryTableModel.COL_RESULT);
		return true;
	}

	/*
	public boolean addHistory(final MExecArgsModel argsmodel, final long stime, final CommandExecutor executor, boolean userCanceled) {
		if (!isHistoryRecordingEnabled()) {
			return false;
		}
		
		ModuleRuntimeData data = new ModuleRuntimeData(argsmodel);
		data.setUserCanceled(userCanceled);
		data.setExitCode(executor.getExitCode());
		data.setStartTime(stime);
		data.setProcessTime(executor.getProcessingTimeMillis());
		_model.addItem(data);
		_cTable.setRowSelectionInterval(_model.getRowCount()-1, _model.getRowCount()-1);
		return true;
	}
	*/
	
	public int getHistoryLimit() {
		return _model.getLimitCount();
	}
	
	public void setHistoryLimit(int limit) {
		_model.setLimitCount(limit);
	}
	
	public boolean isSelectionEmpty() {
		return (_cTable.getSelectedRowCount() == 0);
	}
	
	public int getSelectionCount() {
		return _cTable.getSelectedRowCount();
	}
	
	public int getMinSelectedRow() {
		return _cTable.getSelectionModel().getMinSelectionIndex();
	}
	
	public int getMaxSelectedRow() {
		return _cTable.getSelectionModel().getMaxSelectionIndex();
	}
	
	public boolean canRunLatest() {
		if (getFrame().isVisibleMExecArgsEditDialog()) {
			return false;	// 引数設定ダイアログ表示中は、無効
		}
		return (_cTable.getRowCount() > 0);
	}
	
	public boolean canRunFrom() {
		if (getFrame().isVisibleMExecArgsEditDialog()) {
			return false;	// 引数設定ダイアログ表示中は、無効
		}
		return !isSelectionEmpty();
	}
	
	public boolean canRunBefore() {
		if (getFrame().isVisibleMExecArgsEditDialog()) {
			return false;	// 引数設定ダイアログ表示中は、無効
		}
		int minRowIndex = getMinSelectedRow();
		return (minRowIndex > 0);
	}
	
	public boolean canRunSelected() {
		if (getFrame().isVisibleMExecArgsEditDialog()) {
			return false;	// 引数設定ダイアログ表示中は、無効
		}
		return !isSelectionEmpty();
	}

	/**
	 * 選択履歴からフィルタ作成が可能かを判定する。
	 * @return	作成可能な状態なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 2.00
	 */
	public boolean canCreateFilterByHistory() {
		if (getFrame().isVisibleMExecArgsEditDialog()) {
			return false;	// 引数設定ダイアログ表示中は、無効
		}
		return !isSelectionEmpty();
	}
	
	/**
	 * このビューを格納するエディタフレームを取得する。
	 * @return	このビューを格納するエディタフレームのインスタンス。
	 * 			このビューがフレームに格納されていない場合は <tt>null</tt> を返す。
	 */
	public RunnerFrame getFrame() {
		Window parentFrame = SwingUtilities.windowForComponent(this);
		if (parentFrame instanceof RunnerFrame)
			return (RunnerFrame)parentFrame;
		else
			return null;
	}

	/**
	 * このビューのメインコンポーネントにフォーカスを設定する。
	 */
	public void requestFocusInComponent() {
		_cTable.setFocus();
	}

	/**
	 * 選択されているすべてのオブジェクトがコピー可能か検証する。
	 * @return	コピー可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canCopy() {
		return false;
	}

	/**
	 * 選択されているオブジェクトをクリップボードへコピーする
	 */
	public void doCopy() {
		// No entry
	}

	/**
	 * 現在クリップボードに存在する内容が選択位置に貼り付け可能か検証する。
	 * @return	貼り付け可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canPaste() {
		return false;
	}

	/**
	 * クリップボードに存在するデータを、コンポーネントにコピーする
	 */
	public void doPaste() {
		// No entry
	}

	/**
	 * 選択されているすべてのオブジェクトが削除可能か検証する。
	 * @return	削除可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canDelete() {
		if (getFrame().isVisibleHistoryModuleArgsEditDialog()) {
			return false;	// 引数設定ダイアログ表示中は、無効
		}
		return !isSelectionEmpty();
	}

	/**
	 * 選択されているオブジェクトを削除する
	 */
	public void doDelete() {
		// 操作可能な状態でないなら、処理しない
		if (!canDelete()) {
			return;
		}
		
		// 削除範囲を取得
		int[] selrows = _cTable.getSelectedRows();
		Arrays.sort(selrows);
		
		// 削除チェック
		// テンポラリとか連動とか？
		
		// 終端から削除
		for (int i = selrows.length-1; i >= 0; i--) {
			_model.removeRow(selrows[i]);
		}
	}
	
	public void doSelectAll() {
		_cTable.selectAll();
	}

	/**
	 * 選択されている履歴以前のすべての履歴を、履歴の引数のまま実行する。
	 * @param withEditArgs	引数を編集する場合は <tt>true</tt>、編集しない場合は <tt>false</tt>
	 * @return	すべてのモジュール実行が正常に完了した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean doRunBefore(boolean withEditArgs) {
		// 最小選択行より前のレコードを取得
		int numRows = getMinSelectedRow();
		if (numRows <= 0)
			return false;		// 最小選択行より前に履歴が存在しない
		RelatedModuleList histlist = new RelatedModuleList(numRows);
		for (int row = 0; row < numRows; row++) {
			ModuleRuntimeData histData = _model.getItem(row);
			histlist.add(histData.clone());	// 内容が更新されるため、クローン化
		}
		//--- リレーションを更新
		histlist.updateRelations();
		
		// 選択履歴の実行
		return execByHistory(withEditArgs, histlist);
	}

	/**
	 * 選択されている履歴以後のすべての履歴を、履歴の引数のまま実行する。
	 * @param withEditArgs	引数を編集する場合は <tt>true</tt>、編集しない場合は <tt>false</tt>
	 * @return	すべてのモジュール実行が正常に完了した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean doRunFrom(boolean withEditArgs) {
		// 最小選択行以降のレコードを取得
		int row = getMinSelectedRow();
		if (row < 0)
			return false;		// 選択された履歴が存在しない
		int numRows = _model.getRowCount();
		RelatedModuleList histlist = new RelatedModuleList(numRows - row);
		for (; row < numRows; row++) {
			ModuleRuntimeData histData = _model.getItem(row);
			histlist.add(histData.clone());	// 内容が更新されるため、クローン化
		}
		//--- リレーションを更新
		histlist.updateRelations();
		
		// 選択履歴の実行
		return execByHistory(withEditArgs, histlist);
	}

	/**
	 * 最新の履歴を、履歴の引数のまま実行する。
	 * @param withEditArgs	引数を編集する場合は <tt>true</tt>、編集しない場合は <tt>false</tt>
	 * @return	すべてのモジュール実行が正常に完了した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean doRunLatest(boolean withEditArgs) {
		// 最新(最後)のレコードを取得
		int numRows = _model.getRowCount();
		if (numRows <= 0)
			return false;		// 履歴が存在しない
		RelatedModuleList histlist = new RelatedModuleList(1);
		{
			ModuleRuntimeData histData = _model.getItem(numRows - 1);
			histlist.add(histData.clone());	// 内容が更新されるため、クローン化
		}
		//--- リレーションを更新
		histlist.updateRelations();
		
		// 選択履歴の実行
		return execByHistory(withEditArgs, histlist);
	}

	/**
	 * 選択されているすべての履歴を、履歴の引数のまま実行する。
	 * @param withEditArgs	引数を編集する場合は <tt>true</tt>、編集しない場合は <tt>false</tt>
	 * @return	すべてのモジュール実行が正常に完了した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean doRunSelected(boolean withEditArgs) {
		// 選択されたすべてのレコードを取得
		if (isSelectionEmpty())
			return false;		// 選択された履歴が存在しない
		int rows[] = _cTable.getSelectedRows();
		if (rows.length <= 0)
			return false;		// no selection
		RelatedModuleList histlist = new RelatedModuleList(rows.length);
		for (int row : rows) {
			ModuleRuntimeData histData = _model.getItem(row);
			histlist.add(histData.clone());	// 内容が更新されるため、クローン化
		}
		//--- リレーションを更新
		histlist.updateRelations();
		
		// 選択履歴の実行
		return execByHistory(withEditArgs, histlist);
	}

	/**
	 * 選択されているすべての履歴から、マクロフィルタを新規作成する。
	 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 2.00
	 */
	public boolean doCreateFilterByHistory()
	{
//		// テスト
//		if (true) {
//			// テストデータ作成
//			/*
//#AADLMacro
//echo,,名前の匿名化
//java,,名前の匿名化,../module/CryptValuesByMD5.jar,,,,[IN],${1},[STR],1,[STR],2,[OUT],$tmp{../result/result1.csv}
//echo,,男性のみ抽出
//java,,男性のみ抽出,../module/ExtractByRegexp.jar,,,,[IN],$tmp{../result/result1.csv},[STR],1,[STR],4,[STR],男性,[OUT],$tmp{../result/result2.csv}
//echo,,東京都神奈川のみ抽出
//java,,東京都神奈川のみ抽出,../module/ExtractByRegexp.jar,,,,[IN],$tmp{../result/result2.csv},[STR],1,[STR],5,[STR],(東京|神奈川),[OUT],$tmp{../result/result3.csv}
//echo,,20代と30代の抽出
//java,,20代と30代の抽出,../module/ExtractCompareDecimalRange.jar,,,,[IN],$tmp{../result/result3.csv},[STR],1,[STR],3,[STR],AND,[STR],>=,[STR],20,[STR],<,[STR],40,[OUT],../result/result4.csv
//echo,,睡眠時間に関する回答カテゴリごとの集計
//java,,睡眠時間に関する回答カテゴリごとの集計,../module/EqualValuesCounter.jar,,,,[IN],../result/result4.csv,[STR],1,[STR],8,[STR],人,[OUT],${2}
//#end
//			 * 
//			 */
//			RelatedModuleList list = new RelatedModuleList();
//			MExecDefSettings settings;
//			ModuleRuntimeData data;
//			//--- 1
//			settings = new MExecDefSettings();
//			settings.loadForTarget(getFrame().getMExecDefUserRootDirectory().getChildFile("BasicFilter/Convert/CryptValuesByMD5/.mexecdef.prefs"));
//			data = new ModuleRuntimeData(settings);
//			data.getArgument(0).setValue(getFrame().getDataFileSystemRootDirectory().getChildFile("SampleData/BasicFilter/sampleDataSource10000.csv"));
//			data.getArgument(1).setValue("1");
//			data.getArgument(2).setValue("2");
//			data.getArgument(3).setValue(getFrame().getDataFileUserRootDirectory().getChildFile("test/testMacroFilter1.csv"));
//			data.setRunNo(1);
//			list.add(data);
//			//--- 2
//			settings = new MExecDefSettings();
//			settings.loadForTarget(getFrame().getMExecDefUserRootDirectory().getChildFile("BasicFilter/Extract/CSVフィールド値のパターンマッチによるレコード抽出/.mexecdef.prefs"));
//			data = new ModuleRuntimeData(settings);
//			data.getArgument(0).setValue(getFrame().getDataFileUserRootDirectory().getChildFile("test/testMacroFilter1.csv"));
//			data.getArgument(1).setValue("1");
//			data.getArgument(2).setValue("4");
//			data.getArgument(3).setValue("男性");
//			data.getArgument(4).setValue(getFrame().getDataFileUserRootDirectory().getChildFile("test/testMacroFilter2.csv"));
//			data.getArgument(4).setOutToTempEnabled(true);
//			data.setRunNo(2);
//			list.add(data);
//			//--- 3
//			settings = new MExecDefSettings();
//			settings.loadForTarget(getFrame().getMExecDefUserRootDirectory().getChildFile("BasicFilter/Extract/CSVフィールド値のパターンマッチによるレコード抽出/.mexecdef.prefs"));
//			data = new ModuleRuntimeData(settings);
//			data.getArgument(0).setValue(getFrame().getDataFileUserRootDirectory().getChildFile("test/testMacroFilter2.csv"));
//			data.getArgument(1).setValue("1");
//			data.getArgument(2).setValue("5");
//			data.getArgument(3).setValue("(東京|神奈川)");
//			data.getArgument(4).setValue(getFrame().getDataFileUserRootDirectory().getChildFile("test/testMacroFilter3.csv"));
//			data.setRunNo(3);
//			list.add(data);
//			//--- update relations
//			list.updateRelations();
//			
//			// ダイアログ表示テスト
//			// フィルタマクロ作成ダイアログの表示
//			MacroFilterEditDialog dlg = new MacroFilterEditDialog(getFrame());
//			dlg.setConfiguration(AppSettings.MACROFILTER_EDIT_DLG, AppSettings.getInstance().getConfiguration());
//			boolean ret = dlg.initialComponent(MacroFilterEditModel.EditType.NEW,
//												getFrame().getMExecDefPathFormatter(),
//												getFrame().getMExecDefUserRootDisplayName(),
//												getFrame().getMExecDefUserRootDirectory(),
//												null,
//												list);
//			if (!ret) {
//				// error
//				return false;
//			}
//			dlg.setVisible(true);
//			dlg.dispose();
//			return false;
//		}
//		
//		
		// 選択されたすべてのレコードを取得
		if (isSelectionEmpty())
			return false;		// 選択された履歴が存在しない
		int rows[] = _cTable.getSelectedRows();
		if (rows.length <= 0)
			return false;		// no selection
		RelatedModuleList histlist = new RelatedModuleList(rows.length);
		for (int row : rows) {
			ModuleRuntimeData histData = _model.getItem(row);
			histlist.add(histData.clone());	// 内容が更新されるため、クローン化
		}
		//--- リレーションを更新
		histlist.updateRelations();
		
		// 選択履歴のチェック
		//--- 選択履歴のチェックにおいて履歴番号(実行番号)が使用される
		if (!checkHistoryModuleExecutable(true, histlist)) {
			return false;		// 実行不可
		}
		
		// モジュール実行結果の初期化
		for (int i = 0; i < histlist.size(); i++) {
			ModuleRuntimeData data = histlist.getData(i);
			data.clearResults();
//			data.setRunNo(i+1);		// Added : Ver.3.1.2 (もし履歴の実行番号を更新するなら、ここ)
		}
		
		// フィルタマクロ作成ダイアログの表示
		MacroFilterEditDialog dlg = new MacroFilterEditDialog(getFrame());
		dlg.setConfiguration(AppSettings.MACROFILTER_EDIT_DLG, AppSettings.getInstance().getConfiguration());
		boolean ret = dlg.initialComponent(MacroFilterEditModel.EditType.NEW,
											getFrame().getFramePathFormatter(),
											getFrame().getMExecDefUserRootDisplayName(),
											getFrame().getMExecDefUserRootDirectory(),
											null,
											histlist);
		if (!ret) {
			// error
			return false;
		}
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled!
			return false;
		}
		
		// 作成されたファイルの情報を更新
		VirtualFile vfParent = dlg.getParentDirectory();
		if (vfParent != null) {
			getFrame().refreshMExecDefTree(vfParent);
		}
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected abstract class HistoryState {
		private final int					_code;
		private final ModuleRuntimeData	_data;
		
		public HistoryState(int code, ModuleRuntimeData data) {
			this._code = code;
			this._data = data;
		}
		
		public int getCode() {
			return _code;
		}
		
		public ModuleRuntimeData getData() {
			return _data;
		}
	}
	
	static protected class HistoryErrorState extends HistoryState {
		static public final int	FILTER_DIR_NOT_FOUND	= -1;
		static public final int	FILTER_PREFS_NOT_FOUND	= -2;
		static public final int	MODULE_NOT_FOUND		= -3;
		static public final int	ARGUMENTS_RESTRUCTURED	= -4;

		public HistoryErrorState(int code, ModuleRuntimeData data) {
			super(code, data);
		}
	}
	
	static protected class HistoryWarnState extends HistoryState {
		static public final int	MODULE_FILE_CHANGED	= 1;
		static public final int	MODULE_FILE_UPDATED	= 2;
		
		public HistoryWarnState(int code, ModuleRuntimeData data) {
			super(code, data);
		}
	}

	/**
	 * 指定されたリストに格納されているモジュール実行履歴から、モジュールを実行する。
	 * <p>このモジュールでは、エラーメッセージを表示する。
	 * <b>注意</b>
	 * <blockquote>
	 * このメソッドでは、引数に指定されたリストに格納されている履歴データの内容は書き換えられるため、
	 * 書き換えられてもよいインスタンスを指定すること。
	 * </blockquote>
	 * @param withEditArgs	引数を編集する場合は <tt>true</tt>、編集しない場合は <tt>false</tt>
	 * @param histlist	実行履歴の管理オブジェクト
	 * @return	すべてのモジュール実行が正常に完了した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean execByHistory(boolean withEditArgs, final RelatedModuleList histlist) {
		// 選択履歴のチェック
		if (!checkHistoryModuleExecutable(false, histlist)) {
			return false;		// 実行不可
		}
		
		// モジュールの実行番号と実行結果の初期化
		for (int i = 0; i < histlist.size(); i++) {
			ModuleRuntimeData data = histlist.getData(i);
			data.clearResults();
			data.setRunNo(i+1);
		}
		
		// 履歴引数の編集
		if (withEditArgs) {
			//--- 編集ダイアログの表示(モードレス)
			getFrame().showHistoryModuleArgsEditDialog(histlist);
			return false;
		}
		
		// 実行(引数編集なしに実行)
		//boolean ret = ContinuousProcessMonitorDialog.runAllModules(getFrame(), histlist);
		//if (ret) {
		//	getFrame().openResultFilesByModuleResults(histlist);
		//}
		//return ret;
		AsyncProcessMonitorWindow monitor = AsyncProcessMonitorWindow.runAllModules(getFrame(), histlist,
																		AppSettings.getInstance().getConsoleShowAtStart(),
																		AppSettings.getInstance().getConsoleAutoClose());
		if (monitor != null) {
			getFrame().addAsyncProcessMonitor(monitor);
			return true;
		}
		else {
			// failed to start
			return false;
		}
	}

	/**
	 * 指定されたリストに格納されているモジュール実行履歴が実行可能かをチェックする。
	 * このチェックでは、モジュール実行定義を履歴データと比較し、モジュールが変更されていないかをチェックする。
	 * <p>このモジュールでは、エラーメッセージを表示する。
	 * <b>注意</b>
	 * <blockquote>
	 * このメソッドでは、引数に指定されたリストに格納されている履歴データの内容は書き換えられるため、
	 * 書き換えられてもよいインスタンスを指定すること。
	 * </blockquote>
	 * @param createFilterMacro	フィルタマクロ作成前に呼び出された場合は <tt>true</tt>、履歴実行時の場合は <tt>false</tt>
	 * @param histlist	実行履歴の管理オブジェクト
	 * @return	すべてのモジュール実行が正常に完了した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @return 実行可能なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean checkHistoryModuleExecutable(boolean createFilterMacro, final RelatedModuleList histlist) {
		ArrayList<HistoryErrorState> errorList = new ArrayList<HistoryErrorState>();
		ArrayList<HistoryWarnState> warnList = new ArrayList<HistoryWarnState>();
		
		int numModules = histlist.size();
		int maxIndex = numModules - 1;
		
		for (int index = 0; index < numModules; index++) {
			ModuleRuntimeData histdata = histlist.getData(index);
			
			// モジュール実行定義ディレクトリの存在チェック
			if (!histdata.isExistExecDefDirectory()) {
				//--- モジュール実行定義のディレクトリが存在しない
				errorList.add(new HistoryErrorState(HistoryErrorState.FILTER_DIR_NOT_FOUND, histdata));
				continue;
			}
			
			// モジュール実行定義の読み込み
			VirtualFile vfPrefs = histdata.getExecDefPrefsFile();
			boolean exists = false;
			try {
				if (vfPrefs.exists() && vfPrefs.isFile()) {
					exists = true;
				}
			} catch (Throwable ignoreEx) {
				ignoreEx = null;
				exists = false;
			}
			if (!exists) {
				//--- モジュール実行定義の定義ファイルが存在しない
				errorList.add(new HistoryErrorState(HistoryErrorState.FILTER_PREFS_NOT_FOUND, histdata));
				continue;
			}
			MExecDefSettings settings = new MExecDefSettings();
			settings.loadForTarget(vfPrefs);
			
			// 引数構成のチェック
			int numArgs = histdata.getArgumentCount();
			if (numArgs != settings.getNumArguments()) {
				//--- モジュール実行定義の引数構成が変更されている
				errorList.add(new HistoryErrorState(HistoryErrorState.ARGUMENTS_RESTRUCTURED, histdata));
				continue;
			}
			else {
				boolean modArgs = false;
				for (int i = 0; i < numArgs; i++) {
					ModuleArgData argdata = settings.getArgument(i);
					IModuleArgConfig histarg = histdata.getArgument(i);
					if (argdata.getType() != histarg.getType()) {
						modArgs = true;
						break;
					}
					Object argvalue = argdata.getValue();
					if (argvalue instanceof IMExecArgParam) {
						if (argvalue != histarg.getParameterType()) {
							modArgs = true;
							break;
						}
					}
					else if (argvalue != null && (!histarg.isFixedValue() || !argvalue.equals(histarg.getValue()))) {
						modArgs = true;
						break;
					}
				}
				if (modArgs) {
					//--- モジュール実行定義の引数構成が変更されている
					//--- ・引数の属性が異なる
					//--- ・引数の実行時引数指定の内容が異なる
					//--- ・引数の固定値が異なる
					//--- ・引数の固定値と実行時指定の定義が異なる
					errorList.add(new HistoryErrorState(HistoryErrorState.ARGUMENTS_RESTRUCTURED, histdata));
					continue;
				}
			}
			
			// 最終モジュール実行設定以外の実行結果ファイル出力フラグを OFF にする。
			if (index < maxIndex) {
				for (int i = 0; i < numArgs; i++) {
					IModuleArgConfig histarg = histdata.getArgument(i);
					if (ModuleArgType.OUT == histarg.getType()) {
						histarg.setShowFileAfterRun(false);
					}
				}
			}
			
			// モジュールの存在チェック
			ModuleFileType mtype = settings.getModuleFileType();
			VirtualFile vfModule = settings.getModuleFile();
			String strMainClass  = settings.getModuleMainClass();
			boolean existFilterModule = false;
			try {
				if (vfModule.exists() && vfModule.isFile()) {
					existFilterModule = true;
				}
			} catch (Throwable ex) {
				existFilterModule = false;
			}
			if (!existFilterModule && !histdata.isExistModuleFile()) {
				//--- 定義のモジュールファイルと履歴のモジュールファイルのどちらも存在しない
				errorList.add(new HistoryErrorState(HistoryErrorState.MODULE_NOT_FOUND, histdata));
				continue;
			}
			else if (!mtype.equals(histdata.getModuleType())
				|| !vfModule.equals(histdata.getModuleFile())
				|| !Objects.isEqual(strMainClass, histdata.getModuleMainClass()))
			{
				//--- モジュールファイルが異なる
				warnList.add(new HistoryWarnState(HistoryWarnState.MODULE_FILE_CHANGED, histdata));
				continue;
			}
			
			// モジュールファイルの変更チェック
			if (histdata.isModifiedModuleFile()) {
				//--- モジュールが更新されている
				warnList.add(new HistoryWarnState(HistoryWarnState.MODULE_FILE_UPDATED, histdata));
				continue;
			}
		}
		
		// 実行履歴は実行不可
		if (!errorList.isEmpty()) {
			showHistoryErrorMessage(createFilterMacro, errorList);
			return false;
		}
		
		// 実行履歴のモジュール変更
		if (!warnList.isEmpty()) {
			int ret = showConfirmHistoryWarnMessage(createFilterMacro, warnList);
			if (ret != MessageDetailDialog.OK_OPTION) {
				// キャンセル
				return false;
			}
			// 最新のモジュール定義を反映する。
			for (HistoryWarnState state : warnList) {
				ModuleRuntimeData data = state.getData();
				MExecDefSettings settings = new MExecDefSettings();
				settings.loadForTarget(data.getExecDefPrefsFile());
				data.updateModuleConfig(settings);
			}
		}

		// 実行継続
		return true;
	}
	
	protected void showHistoryErrorMessage(boolean createFilterMacro, final List<? extends HistoryErrorState> errlist) {
		VirtualFilePathFormatterList vfFormatter = getFrame().getMExecDefPathFormatter();
		String msg = RunnerMessages.getInstance().msgIllegalHistoryData + "\n"
					+ (createFilterMacro ? RunnerMessages.getInstance().msgFailedToCreateFilterByHistory : RunnerMessages.getInstance().msgFailedToExecuteByHistory);
		StringBuilder sb = new StringBuilder();
		StringBuilder msgbuf = new StringBuilder();
		for (HistoryErrorState state : errlist) {
			ModuleRuntimeData data = state.getData();
			msgbuf.setLength(0);
			msgbuf.append("\n\t");
			msgbuf.append(RunnerMessages.getInstance().MExecDefEditDlg_Label_Location);
			msgbuf.append(" : ");
			msgbuf.append(vfFormatter.formatPath(data.getExecDefDirectory()));
			msgbuf.append("\n\t");
			switch (state.getCode()) {
				case HistoryErrorState.FILTER_DIR_NOT_FOUND :
				case HistoryErrorState.FILTER_PREFS_NOT_FOUND :
					msgbuf.append(RunnerMessages.getInstance().msgHistoryErrorCauseModuleNotFound);
					break;
				case HistoryErrorState.ARGUMENTS_RESTRUCTURED :
					msgbuf.append(RunnerMessages.getInstance().msgHistoryErrorCauseArgDefChanged);
					break;
				case HistoryErrorState.MODULE_NOT_FOUND :
					msgbuf.append(RunnerMessages.getInstance().msgHistoryErrorCauseModuleFileNotFound);
					break;
				default :
					{
						msgbuf.append(RunnerMessages.getInstance().msgHistoryErrorCauseUndefined);
						msgbuf.append("(");
						msgbuf.append(state.getCode());
						msgbuf.append(")");
					}
			}
			msgbuf.append("\n");
			sb.append(ModuleRuntimeData.formatErrorMessage(data, msgbuf.toString()));
		}
		String det = sb.toString();
		String title = (createFilterMacro ? RunnerMessages.getInstance().MExecHistory_Title_CreateFilter : RunnerMessages.getInstance().MExecHistory_Title_Execution);
		MessageDetailDialog.showErrorDetailMessage(this, title, msg, det);
	}
	
	protected int showConfirmHistoryWarnMessage(boolean createFilterMacro, final List<? extends HistoryWarnState> warnlist) {
		VirtualFilePathFormatterList vfFormatter = getFrame().getMExecDefPathFormatter();
		String msg = (createFilterMacro ? RunnerMessages.getInstance().confirmHistoryWarningForCreate : RunnerMessages.getInstance().confirmHistoryWarningForExec);
		StringBuilder sb = new StringBuilder();
		StringBuilder msgbuf = new StringBuilder();
		for (HistoryWarnState state : warnlist) {
			ModuleRuntimeData data = state.getData();
			msgbuf.setLength(0);
			msgbuf.append("\n\t");
			msgbuf.append(RunnerMessages.getInstance().MExecDefEditDlg_Label_Location);
			msgbuf.append(" : ");
			msgbuf.append(vfFormatter.formatPath(data.getExecDefDirectory()));
			msgbuf.append("\n\t");
			switch (state.getCode()) {
				case HistoryWarnState.MODULE_FILE_CHANGED :
					msgbuf.append(RunnerMessages.getInstance().msgHistoryWarnCauseModuleDefChanged);
					break;
				case HistoryWarnState.MODULE_FILE_UPDATED :
					msgbuf.append(RunnerMessages.getInstance().msgHistoryWarnCauseModuleFileOverwrited);
					break;
				default :
					{
						msgbuf.append(RunnerMessages.getInstance().msgHistoryWarnCauseUndefined);
						msgbuf.append("(");
						msgbuf.append(state.getCode());
						msgbuf.append(")");
					}
			}
			msgbuf.append("\n");
			sb.append(ModuleRuntimeData.formatErrorMessage(data, msgbuf.toString()));
		}
		String det = sb.toString();
		String title = (createFilterMacro ? RunnerMessages.getInstance().MExecHistory_Title_CreateFilter : RunnerMessages.getInstance().MExecHistory_Title_Execution);
		return MessageDetailDialog.showConfirmDetailMessage(this, title, msg, det, MessageDetailDialog.OK_CANCEL_OPTION);
	}
	
	private JToggleButton createHistoryRecordingEnabledButton() {
		MenuItemResource mir = RunnerMenuResources.getMenuResource(RunnerMenuResources.ID_FILTER_RECORD_HISTORY);
		Validations.validArgument(mir != null);
		
		JToggleButton btn = new JToggleButton();
		btn.setActionCommand(mir.getCommandKey());
		btn.setToolTipText(mir.getToolTip());
		btn.setIcon(RunnerResources.ICON_RECORD_OFF);
		btn.setSelectedIcon(RunnerResources.ICON_RECORD_ON);
		btn.setMargin(new Insets(0,0,0,0));
		btn.setSelected(true);	// default
		
		// setup action
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				getFrame().updateMenuItem(RunnerMenuResources.ID_FILTER_RECORD_HISTORY);
			}
		});
		
		return btn;
	}
	
	private JButton createToolBarButton(String command) {
		MenuItemResource mir = RunnerMenuResources.getMenuResource(command);
		Validations.validArgument(mir != null);
		
		JButton btn = new JButton(mir.getIcon());
		btn.setToolTipText(mir.getToolTip());
		btn.setActionCommand(mir.getCommandKey());
		btn.setMargin(new Insets(0,0,0,0));
		
		btn.addActionListener(_hToolBarButtonSelection);
		
		return btn;
	}
	
	private JToolBar createToolBar() {
		JToolBar bar = new JToolBar(JToolBar.HORIZONTAL);
		bar.setFloatable(false);
		
		//---[Filter]-[Record History]
		bar.add(_btnHistRecordingEnabled);
		//---
		bar.add(Box.createHorizontalStrut(20));
		//--- [Filter]-[Run History]-[Selected]
		bar.add(_btnHistRunSelected);
		//--- [Filter]-[Run History]-[From]
		bar.add(_btnHistRunFrom);
		//--- [Filter]-[Run History]-[Before]
		bar.add(_btnHistRunBefore);
//		//--- [Filter]-[Run History]-[Latest]
//		bar.add(_btnHistRunLatest);
		//---
		bar.add(Box.createHorizontalStrut(10));
		//--- [Filter]-[Run History As]-[Selected]
		bar.add(_btnHistRunAsSelected);
		//--- [Filter]-[Run History As]-[From]
		bar.add(_btnHistRunAsFrom);
		//--- [Filter]-[Run History As]-[Before]
		bar.add(_btnHistRunAsBefore);
//		//--- [Filter]-[Run History As]-[Latest]
//		bar.add(_btnHistRunAsLatest);
		//---
		bar.add(Box.createHorizontalStrut(10));
		//--- [Filter]-[New By History]
		bar.add(_btnHistNewFilter);
		//---
		bar.add(Box.createHorizontalStrut(20));
		//--- [Filter]-[History Delete]
		bar.add(_btnHistDelete);
		//---
		bar.add(Box.createHorizontalGlue());
		//--- [Checkbox]
		bar.add(_chkConsoleShowAtStart);
		//---
		bar.add(Box.createHorizontalStrut(10));
		//--- [Checkbox]
		bar.add(_chkConsoleAutoClose);
		//---
		bar.add(Box.createHorizontalStrut(5));
		
		return bar;
	}
	
//
//	private MExecDefTree createTreeComponent() {
//		MExecDefTree newTree = new MExecDefTree(null, _hTree){
//			@Override
//			protected void onTreeSelectionAdjusted() {
//				super.onTreeSelectionAdjusted();
//				// このビュー専用の処理
//				MExecDefTreeView.this.onTreeSelectionAdjusted();
//			}
//		};
//		return newTree;
//	}
	
	private void setupActions() {
//		// Tree
//		//--- Mouse listener
//		_cTree.addMouseListener(new TreeComponentMouseHandler());

		// Table
		_cTable.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				evaluateTablePopupMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				evaluateTablePopupMenu(e);
			}
		});
		_cTable.getTableRowHeader().addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				_cTable.setFocus();
				evaluateRowHeaderPopupMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				evaluateRowHeaderPopupMenu(e);
			}
		});
		ListSelectionListener tableSelListener = new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				onTableSelectionChanged(e);
			}
		};
		_cTable.getSelectionModel().addListSelectionListener(tableSelListener);
	}

	//------------------------------------------------------------
	// Menu events
	//------------------------------------------------------------

	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		if (RunnerMenuResources.ID_EDIT_COPY.equals(command)) {
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_PASTE.equals(command)) {
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_DELETE.equals(command)) {
			doDelete();
			requestFocusInComponent();
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_SELECTALL.equals(command)) {
			doSelectAll();
			requestFocusInComponent();
			return true;
		}
		
		// no process
		return false;
	}

	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (RunnerMenuResources.ID_EDIT_COPY.equals(command)) {
			action.setEnabled(canCopy());
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_PASTE.equals(command)) {
			action.setEnabled(canPaste());
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_DELETE.equals(command)) {
			action.setEnabled(canDelete());
			return true;
		}
		else if (RunnerMenuResources.ID_EDIT_SELECTALL.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		
		// no process
		return false;
	}
	
	protected void onToolBarButtonSelected(String command) {
		if (RunnerMenuResources.ID_FILTER_HISTORY_RUN_SELECTED.equals(command)) {
			AppLogger.debug("toolbar [Filter]-[Run History]-[Selected] selected.");
			if (doRunSelected(false)) {
				getFrame().updateAllMenuItems();
				getFrame().setFocusToActiveEditor();
			}
			else {
				requestFocusInComponent();
			}
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUN_FROM.equals(command)) {
			AppLogger.debug("toolbar [Filter]-[Run History]-[From] selected.");
			if (doRunFrom(false)) {
				getFrame().updateAllMenuItems();
				getFrame().setFocusToActiveEditor();
			}
			else {
				requestFocusInComponent();
			}
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUN_BEFORE.equals(command)) {
			AppLogger.debug("toolbar [Filter]-[Run History]-[Before] selected.");
			if (doRunBefore(false)) {
				getFrame().updateAllMenuItems();
				getFrame().setFocusToActiveEditor();
			}
			else {
				requestFocusInComponent();
			}
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUN_LATEST.equals(command)) {
			AppLogger.debug("toolbar [Filter]-[Run History]-[Latest] selected.");
			if (doRunLatest(false)) {
				getFrame().updateAllMenuItems();
				getFrame().setFocusToActiveEditor();
			}
			else {
				requestFocusInComponent();
			}
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_SELECTED.equals(command)) {
			AppLogger.debug("toolbar [Filter]-[Run As History]-[Selected] selected.");
			doRunSelected(true);
			if (!getFrame().isVisibleMExecArgsEditDialog()) {
				requestFocusInComponent();
			}
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_FROM.equals(command)) {
			AppLogger.debug("toolbar [Filter]-[Run As History]-[Selected] selected.");
			doRunFrom(true);
			if (!getFrame().isVisibleMExecArgsEditDialog()) {
				requestFocusInComponent();
			}
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_BEFORE.equals(command)) {
			AppLogger.debug("toolbar [Filter]-[Run As History]-[Selected] selected.");
			doRunBefore(true);
			if (!getFrame().isVisibleMExecArgsEditDialog()) {
				requestFocusInComponent();
			}
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_LATEST.equals(command)) {
			AppLogger.debug("toolbar [Filter]-[Run As History]-[Selected] selected.");
			doRunLatest(true);
			if (!getFrame().isVisibleMExecArgsEditDialog()) {
				requestFocusInComponent();
			}
		}
		else if (RunnerMenuResources.ID_FILTER_NEW_BY_HISTORY.equals(command)) {
			AppLogger.debug("toolbar [Filter]-[New By History] selected.");
			doCreateFilterByHistory();
			if (!getFrame().isVisibleMExecArgsEditDialog()) {
				requestFocusInComponent();
			}
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_DELETE.equals(command)) {
			AppLogger.debug("toolbar [Filter]-[Delete History] selected.");
			doDelete();
			requestFocusInComponent();
		}
	}
	
	public void onHistoryMenuUpdate(String command, Action action) {
		if (RunnerMenuResources.ID_FILTER_NEW_BY_HISTORY.equals(command)) {
			boolean enable = canCreateFilterByHistory();
			_btnHistNewFilter.setEnabled(enable);
			action.setEnabled(enable);
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUN_SELECTED.equals(command) || RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_SELECTED.equals(command)) {
			boolean enable = canRunSelected();
			_btnHistRunSelected.setEnabled(enable);
			_btnHistRunAsSelected.setEnabled(enable);
			action.setEnabled(enable);
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUN_FROM.equals(command) || RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_FROM.equals(command)) {
			boolean enable = canRunFrom();
			_btnHistRunFrom.setEnabled(enable);
			_btnHistRunAsFrom.setEnabled(enable);
			action.setEnabled(enable);
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUN_BEFORE.equals(command) || RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_BEFORE.equals(command)) {
			boolean enable = canRunBefore();
			_btnHistRunBefore.setEnabled(enable);
			_btnHistRunAsBefore.setEnabled(enable);
			action.setEnabled(enable);
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_RUN_LATEST.equals(command) || RunnerMenuResources.ID_FILTER_HISTORY_RUNAS_LATEST.equals(command)) {
			boolean enable = canRunLatest();
			_btnHistRunLatest.setEnabled(enable);
			_btnHistRunAsLatest.setEnabled(enable);
			action.setEnabled(enable);
		}
		else if (RunnerMenuResources.ID_FILTER_HISTORY_DELETE.equals(command)) {
			boolean enable = canDelete();
			_btnHistDelete.setEnabled(enable);
			action.setEnabled(enable);
		}
		else {
			action.setEnabled(false);
		}
	}
	
	// menu : [Edit]-[Copy]
	protected void onSelectedMenuEditCopy(Action action) {
		AppLogger.debug("MExecDefTreeView : menu [Edit]-[Copy] selected.");
		if (!canCopy()) {
			action.setEnabled(false);
			return;
		}
		doCopy();
		requestFocusInComponent();
	}
	
	// menu : [Edit]-[Paste]
	protected void onSelectedMenuEditPaste(Action action) {
		AppLogger.debug("MExecDefTreeView : menu [Edit]-[Paste] selected.");
		if (!canPaste()) {
			action.setEnabled(false);
			return;
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				doPaste();
				requestFocusInComponent();
			}
		});
	}
	
	// menu : [Edit]-[Delete]
	protected void onSelectedMenuEditDelete(Action action) {
		AppLogger.debug("MExecDefTreeView : menu [Edit]-[Delete] selected.");
		if (!canDelete()) {
			action.setEnabled(false);
			return;
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				doDelete();
				requestFocusInComponent();
			}
		});
	}

	//------------------------------------------------------------
	// Action events
	//------------------------------------------------------------
	
	protected void onTableSelectionChanged(ListSelectionEvent e) {
		RunnerFrame frame = getFrame();
		if (frame != null) {
			frame.updateMenuItem(RunnerMenuResources.ID_EDIT_DELETE);
			frame.updateMenuItem(RunnerMenuResources.ID_EDIT_SELECTALL);
			frame.updateMenuItem(RunnerMenuResources.ID_FILTER_HISTORY_DELETE);
			frame.updateMenuItem(RunnerMenuResources.ID_FILTER_MENU);
		}
	}
	
	protected void evaluateRowHeaderPopupMenu(MouseEvent me) {
		// コンテキストメニュー表示のトリガ検証
		if (!me.isPopupTrigger())
			return;
		
		// ポップアップメニューの取得
		requestFocusInComponent();
		
		// 選択状態の検証
		int row = _cTable.rowAtPoint(me.getPoint());
		if (!_cTable.isRowSelectedAllColumns(row)) {
			//--- コンテキストメニュー表示トリガとなるマウスイベント発生位置が
			//--- 選択済み行ではない場合、すべての選択を解除してイベント発生
			//--- 位置の行を選択する。
			if (row >= 0) {
				_cTable.changeRowHeaderSelection(row, false, false);
			} else {
				_cTable.clearSelection();
			}
		}

		// コンテキストメニューの表示
		RunnerMenuBar menuBar = getFrame().getActiveMainMenuBar();
		if (menuBar != null) {
			menuBar.getHistoryContextMenu().show(me.getComponent(), me.getX(), me.getY());
		}
	}
	
	protected void evaluateTablePopupMenu(MouseEvent me) {
		if (me.isPopupTrigger()) {
			requestFocusInComponent();
			RunnerMenuBar menuBar = getFrame().getActiveMainMenuBar();
			if (menuBar != null) {
				menuBar.getHistoryContextMenu().show(me.getComponent(), me.getX(), me.getY());
			}
		}
		
		// コンテキストメニュー表示のトリガ検証
		if (!me.isPopupTrigger())
			return;
		
		// ポップアップメニューの取得
		requestFocusInComponent();
		
		// 選択状態の検証
		int row = _cTable.rowAtPoint(me.getPoint());
		if (!_cTable.isRowSelected(row)) {
			//--- コンテキストメニュー表示トリガとなるマウスイベント発生位置が
			//--- 選択済み行ではない場合、すべての選択を解除してイベント発生
			//--- 位置の行を選択する。
			if (row >= 0) {
				_cTable.changeSelection(row, 0, false, false);
			} else {
				_cTable.clearSelection();
			}
		}

		// コンテキストメニューの表示
		RunnerMenuBar menuBar = getFrame().getActiveMainMenuBar();
		if (menuBar != null) {
			menuBar.getHistoryContextMenu().show(me.getComponent(), me.getX(), me.getY());
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class ToolBarButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			onToolBarButtonSelected(e.getActionCommand());
		}
	}
	
	static protected class MExecHistoryTableCellRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		private final Border _innerCellMargine = BorderFactory.createEmptyBorder(0, 3, 0, 3);
		
		public MExecHistoryTableCellRenderer() {
			super();
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
														boolean isSelected, boolean hasFocus,
														int row, int column)
		{
			// 標準のレンダラを生成
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			// セル内余白を追加
			Border newBorder = getBorder();
			if (newBorder != null) {
				newBorder = BorderFactory.createCompoundBorder(newBorder, _innerCellMargine);
			} else {
				newBorder = _innerCellMargine;
			}
			setBorder(newBorder);
			
			// 選択時以外の色を設定
			TableModel model = table.getModel();
			if (model instanceof MExecHistoryTableModel && row >= 0 && column==MExecHistoryTableModel.COL_RESULT) {
				ModuleRuntimeData data = ((MExecHistoryTableModel)model).getItem(row);
				if (data.isUserCanceled()) {
					//--- canceled
					setIcon(RunnerResources.ICON_BALL_GRAY);
				}
				else if (data.isSucceeded()) {
					//--- succeeded
					setIcon(RunnerResources.ICON_BALL_GREEN);
				}
				else {
					//--- failed
					setIcon(RunnerResources.ICON_BALL_RED);
				}
			}
			else {
				setIcon(null);
			}
			
			return comp;
		}
	}
	
	static protected class MExecHistoryTable extends SpreadSheetTable
	{
		private static final long serialVersionUID = 1L;
		private boolean _initialized = false;
		
		public MExecHistoryTable() {
			super();
			setAdjustOnlyVisibleRows(false);			// 列幅の自動調整では全行を対象
			setAdjustOnlySelectedCells(false);			// 列幅の自動調整では選択セルのみに限定しない
			setDefaultRenderer(String.class, new MExecHistoryTableCellRenderer());
			_initialized = true;
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		@Override
		public void setModel(TableModel dataModel) {
			super.setModel(dataModel);

			Font font = UIManager.getFont("Table.font");
			if (font != null) {
				// 初期推奨幅の設定
				TableColumn col;
				MExecHistoryTableCellRenderer renderer = new MExecHistoryTableCellRenderer();
				renderer.setFont(font);
				//--- Result
				renderer.setIcon(RunnerResources.ICON_BALL_GRAY);
				renderer.setText("  Cancel  ");
				col = getColumnModel().getColumn(MExecHistoryTableModel.COL_RESULT);
				col.setPreferredWidth(renderer.getPreferredSize().width);
				renderer.setIcon(null);
				//--- Name
				renderer.setText(" CSVフィールド値の日付時刻範囲によるレコード抽出 ");
				col = getColumnModel().getColumn(MExecHistoryTableModel.COL_NAME);
				col.setPreferredWidth(renderer.getPreferredSize().width);
				//--- ExecStart
				renderer.setText(" 99/99 99:99 ");
				col = getColumnModel().getColumn(MExecHistoryTableModel.COL_EXEC_START);
				col.setPreferredWidth(renderer.getPreferredSize().width);
				//--- ExecTime
				renderer.setText(" 999:99'99\"999 ");
				col = getColumnModel().getColumn(MExecHistoryTableModel.COL_EXEC_TIME);
				col.setPreferredWidth(renderer.getPreferredSize().width);
			}
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------

		@Override
		protected JTableHeader createDefaultTableHeader() {
			SpreadSheetColumnHeader th = (SpreadSheetColumnHeader)super.createDefaultTableHeader();
			// このテーブルでは、行単位の選択とするため、
			// 列選択や強調表示は無効とする。
			th.setEnableChangeSelection(false);
			th.setVisibleSelection(false);
			return th;
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (_initialized) {
				int rowHeaderWidth = (getModel().getRowCount() > 0 ? -1 : 10);
				getTableRowHeader().setFixedCellWidth(rowHeaderWidth);
			}
			
			super.tableChanged(e);
		}

		@Override
		public boolean isSelectionAllColumns() {
			if (getColumnSelectionAllowed()) {
				return super.isSelectionAllColumns();
			} else {
				return true;
			}
		}

		@Override
		protected RowHeaderModel createRowHeaderModel() {
			// モジュール引数テーブル専用の行ヘッダーモデル。
			// このモデルが返す値は、'$'と数字を連結させた文字列となる。
			return new SpreadSheetTable.RowHeaderModel(){
				private static final long serialVersionUID = 1L;

				@Override
				public Object getElementAt(int index) {
					TableModel model = getModel();
					if (model instanceof MExecHistoryTableModel) {
						return ((MExecHistoryTableModel)model).getHistoryNo(index);
					} else {
						return super.getElementAt(index);
					}
				}
			};
		}

		//------------------------------------------------------------
		// Inner classes
		//------------------------------------------------------------
	}
}
