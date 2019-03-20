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
 * @(#)ContinuousProcessMonitorDialog.java	1.22	2012/08/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ssac.aadl.common.CommonMessages;
import ssac.falconseed.common.FSStartupSettings;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.RelatedModuleList;
import ssac.falconseed.module.swing.ModuleArgsViewDialog;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.process.CommandExecutor;
import ssac.util.process.OutputString;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.ConsoleTextPane;
import ssac.util.swing.RunningAnimationLabel;

/**
 * モジュール実行定義の実行状態を表示するモニタダイアログ。
 * 
 * @version 1.22	2012/08/21
 * @since 1.22
 */
public class ContinuousProcessMonitorDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final int INSET_LR = 2;
	static protected final int INSET_TB = 0;
	static protected final int DEFAULT_TIMER_INTERVAL = 200;
	
	static private final Dimension DM_MIN_SIZE = new Dimension(640, 320);

	/** 実行完了時の状態：実行完了待ち **/
	static public int	WAITING			= -1;
	/** 実行完了時の状態：全モジュール正常終了 **/
	static public int ALL_SUCCEEDED	= 0;
	/** 実行完了時の状態：モジュール実行失敗 **/
	static public int FAILED			= 1;
	/** 実行完了時の状態：ユーザによるキャンセル **/
	static public int USER_CANCELED	= 2;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 実行完了時の状態 **/
	private int		_resultState = WAITING;
	
	/** モジュール実行開始時刻(エポック) **/
	private long		_startTime;
	/** モジュール実行終了時刻(エポック) **/
	private long		_endTime;

	/** 監視タイマー **/
	private Timer	_animTimer;

	/** コンソールテキスト表示コンポーネント **/
	private ConsoleTextPane		_console;
	/** 実行時アニメーション表示ラベル **/
	private RunningAnimationLabel	_runIndicator;

	/** 実行モジュール表示ラベル(for Debug) **/
	private JLabel		_lblName;
	/** 実行経過時間表示ラベル **/
	private JLabel		_lblTime;
	/** 引数表示ボタン **/
	private JButton	_btnShowArgs;
	/** 実行完了モジュール数のプログレスバー **/
	private JProgressBar	_prgFinishedModuleCount;
	/** 引数表示ダイアログ **/
	private ModuleArgsViewDialog	_viewArgs;

	/** 実行プロセス **/
	protected CommandExecutor		_curExecutor;
	/** 実行対象のモジュールデータ **/
	protected ModuleRuntimeData	_targetData;
	/** 実行対象のモジュールデータのインデックス **/
	protected int					_targetIndex;

	/** 実行対象のモジュールデータ **/
	protected RelatedModuleList	_modulelist;
	/** このダイアログのオーナーとなるメインフレーム **/
	protected final RunnerFrame	_mainframe;
	/** モジュール実行数を表示する場合は <tt>true</tt> **/
	protected final boolean		_visibleCount;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたモジュールを実行する。
	 * @param frame		メインフレーム
	 * @param modules	実行対象のモジュールを保持するオブジェクト
	 * @return	すべてのモジュール実行が正常に完了した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean runModule(final RunnerFrame frame, final ModuleRuntimeData module) {
		// check
		if (frame == null)
			throw new NullPointerException("'frame' argument is null.");
		if (module == null)
			throw new NullPointerException("'module' argument is null.");
		
		// ダイアログ生成
		RelatedModuleList modlist = new RelatedModuleList(1);
		modlist.add(module);
		ContinuousProcessMonitorDialog dlg = new ContinuousProcessMonitorDialog(frame, modlist, false);
		dlg.initialComponent();
		if (!dlg.startTargetModule(frame)) {
			dlg.dispose();
			return false;
		}
		dlg.updateFirstModuleStartTime();
		dlg.setVisible(true);
		dlg.dispose();
		
		return dlg.isAllSucceeded();
	}

	/**
	 * 指定されたすべてのモジュールを実行する。
	 * @param frame		メインフレーム
	 * @param modules	実行対象のモジュールを保持するオブジェクト
	 * @return	すべてのモジュール実行が正常に完了した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>modules</em> が空、もしくは <tt>null</tt> 要素を含む場合
	 */
	static public boolean runAllModules(final RunnerFrame frame, final RelatedModuleList modules) {
		// check
		if (frame == null)
			throw new NullPointerException("'frame' argument is null.");
		if (modules == null)
			throw new NullPointerException("'modules' argument is null.");
		if (modules.isEmpty())
			throw new IllegalArgumentException("Module is empty.");
		
		// ダイアログ生成
		ContinuousProcessMonitorDialog dlg = new ContinuousProcessMonitorDialog(frame, modules, true);
		dlg.initialComponent();
		if (!dlg.startTargetModule(frame)) {
			dlg.dispose();
			return false;
		}
		dlg.updateFirstModuleStartTime();
		dlg.setVisible(true);
		dlg.dispose();
		
		return dlg.isAllSucceeded();
	}
	
	protected ContinuousProcessMonitorDialog(RunnerFrame mainframe, RelatedModuleList modules, boolean showCount) {
		super(mainframe, String.format(RunnerMessages.getInstance().ProcMonitorDlg_Title_Main, ""), true);
		this._mainframe  = mainframe;
		this._modulelist = modules;
		if (modules.size() > 1 || showCount) {
			//--- モジュール数が複数、もしくはカウント表示が要求された場合は表示する。
			this._visibleCount = true;
		} else {
			//--- モジュール数が 1 で、カウント表示が要求されていない場合は非表示にする。
			this._visibleCount = false;
		}
		setConfiguration(AppSettings.PROCESSMONITOR_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	public void initialComponent() {
		super.initialComponent();
		
		// setup progress bar
		_prgFinishedModuleCount.setMinimum(0);
		_prgFinishedModuleCount.setMaximum(_modulelist.size());
		_prgFinishedModuleCount.setStringPainted(true);
		_prgFinishedModuleCount.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				JProgressBar pbar = (JProgressBar)e.getSource();
				pbar.setString(String.format("%d/%d", pbar.getValue(), pbar.getMaximum()));
			}
		});
		_prgFinishedModuleCount.setValue(0);
		if (!_visibleCount) {
			_prgFinishedModuleCount.setVisible(false);
		}
		_targetIndex = 0;
		_targetData  = _modulelist.getData(_targetIndex);

		// 表示の初期化
		updateTargetName();
		updateProcessingTime(0L);
		updateButtons();
		
		// 設定情報の反映
		restoreConfiguration();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ユーザーによって実行が中断された場合は <tt>true</tt>、実行が終了した場合は <tt>false</tt> を返す。
	 */
	public boolean isTerminatedByUser() {
		return (_resultState == USER_CANCELED);
	}

	/**
	 * すべてのモジュール実行が正常終了した場合は <tt>true</tt> を返す。
	 */
	public boolean isAllSucceeded() {
		return (_resultState == ALL_SUCCEEDED);
	}

	/**
	 * モジュール実行が異常終了した場合は <tt>true</tt> を返す。
	 */
	public boolean isModuleFailed() {
		return (_resultState == FAILED);
	}

	/**
	 * 実行完了時の状態を返す。
	 */
	public int getResultStatus() {
		return _resultState;
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected void initDialog() {
		super.initDialog();
		
		// start timer
		getRunIndicator().setRunning(true);
		getTimer().start();
		updateButtons();
	}

	@Override
	protected boolean doCancelAction() {
		stop();
		return true;
	}

	/**
	 * 監視タイマーのイベント
	 */
	protected void onTimer(ActionEvent ae) {
		// 監視対象のチェック
		if (_curExecutor == null) {
			// 監視対象が存在しないので、停止
			stop();
			return;
		}
		
		// 実行状態のチェック
		if (!checkProcessAndStartNext()) {
			// 終了
			stop();
			return;
		}
		
		// 出力更新
		getRunIndicator().next();
		updateProcessingTime(getTotalProcessTime());
		showQueueingOutput();
	}

	/**
	 * [実行時引数の表示]ボタン
	 */
	protected void onButtonShowArgs() {
		if (_viewArgs == null) {
			_viewArgs = new ModuleArgsViewDialog(this, _modulelist, false);
			_viewArgs.initialComponent();
		}
		_viewArgs.setVisible(true);
	}

	@Override
	protected void dialogClose(int result) {
		// 引数表示ダイアログの破棄
		if (_viewArgs != null) {
			_viewArgs.dispose();
			_viewArgs = null;
		}
		
		// このダイアログの破棄
		super.dialogClose(result);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void printStartProcessMessage(ModuleRuntimeData data) {
		String msg = ModuleRuntimeData.formatErrorMessage(data, "Start...");
		getConsoleTextPane().appendOutputString(msg + "\n");
	}
	
	protected void printFinishedProcessMessage(ModuleRuntimeData data) {
		if (data.isUserCanceled()) {
			String msg = ModuleRuntimeData.formatErrorMessage(data, "Canceled.");
			getConsoleTextPane().appendOutputString("\n" + msg + "\n");
		}
		else if (data.isSucceeded()) {
			String msg = ModuleRuntimeData.formatErrorMessage(data, "Succeeded(%d, time=%s)", data.getExitCode(), formatTime(data.getProcessTime()));
			getConsoleTextPane().appendOutputString(msg + "\n");
		}
		else {
			String msg = ModuleRuntimeData.formatErrorMessage(data, "Failed(%d, time=%s)", data.getExitCode(), formatTime(data.getProcessTime()));
			getConsoleTextPane().appendErrorString(msg + "\n");
		}
	}
	
	protected void updateFirstModuleStartTime() {
		_startTime = (_curExecutor==null ? System.currentTimeMillis() : _curExecutor.getProcessStartTime());
	}

	/**
	 * 現在のターゲットモジュールの実行を開始する。
	 * @param parentComponent	メッセージボックスを表示する際の親ウィンドウ、メッセージボックスを表示しない場合は <tt>null</tt>
	 * @return 実行が開始された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean startTargetModule(Component parentComponent) {
		if (_targetData == null) {
			stopTimer();
			String errmsg = String.format("Target Module Data(index=%d) is null!", _targetIndex);
			AppLogger.error("<ContinuousProcessMonitorDialog> " + errmsg);
			ModuleRunner.showErrorMessage(parentComponent, errmsg);
			_resultState = FAILED;
			return false;
		}

		CommandExecutor executor = null;
		try {
			executor = _targetData.createCommandExecutor();
		}
		catch (IOException ex) {
			String errmsg = ModuleRuntimeData.formatErrorMessage(_targetData, RunnerMessages.getInstance().msgFailedToCreateTempFile);
			AppLogger.error("<ContinuousProcessMonitorDialog> " + errmsg, ex);
			if (parentComponent != null) {
				ModuleRunner.showErrorMessage(parentComponent, errmsg);
			} else {
				getConsoleTextPane().appendErrorString("\n" + errmsg + "\n");
			}
			executor = null;
		}
		catch (Throwable ex) {
			String errmsg = ModuleRuntimeData.formatErrorMessage(_targetData, RunnerMessages.getInstance().msgIllegalModuleArguments);
			AppLogger.error("<ContinuousProcessMonitorDialog> " + errmsg, ex);
			errmsg = CommonMessages.formatErrorMessage(errmsg, ex);
			if (parentComponent != null) {
				ModuleRunner.showErrorMessage(parentComponent, errmsg);
			} else {
				getConsoleTextPane().appendErrorString("\n" + errmsg + "\n");
			}
			executor = null;
		}
		if (executor==null) {
			_resultState = FAILED;
			return false;
		}

		// start process
		try {
			executor.start();
		}
		catch (Throwable ex) {
			stopTimer();
			String errmsg = ModuleRuntimeData.formatErrorMessage(_targetData, CommonMessages.getInstance().msgCouldNotExecute);
			AppLogger.error("<ContinuousProcessMonitorDialog> " + errmsg, ex);
			if (parentComponent != null) {
				ModuleRunner.showErrorMessage(parentComponent, errmsg);
			} else {
				getConsoleTextPane().appendErrorString("\n" + errmsg + "\n");
			}
			_resultState = FAILED;
			return false;
		}
		
		//--- update executor
		_curExecutor = executor;
		_targetData.setStartTime(executor.getProcessStartTime());
		printStartProcessMessage(_targetData);
		
		return true;
	}

	/**
	 * 現在実行中のモジュールを監視し、実行完了なら次のモジュールを実行する。
	 * @return	実行継続中なら <tt>true</tt>、実行が終了した場合は <tt>false</tt>
	 */
	protected boolean checkProcessAndStartNext() {
		if (_curExecutor != null) {
			if (isProcessRunning()) {
				//--- 実行中なら、次回チェック
				return true;
			}
			
			// 実行完了
			CommandExecutor executor = _curExecutor;
			executor.destroy();
			_endTime = executor.getProcessEndTime();
			_targetData.setResults(executor, false);
			updateProcessingTime(getTotalProcessTime());
			showQueueingOutput();
			printFinishedProcessMessage(_targetData);
			_curExecutor = null;
			_mainframe.addHistoryData(_targetData);
			incrementFinishedModuleCount();
			
			// エラーなら、実行完了
			if (!_targetData.isSucceeded()) {
				_resultState = FAILED;
				return false;
			}
		}
		
		// 次の実行を開始
		++_targetIndex;
		if (_targetIndex >= _modulelist.size()) {
			_resultState = ALL_SUCCEEDED;
			return false;
		}
		_targetData = _modulelist.getData(_targetIndex);
		updateTargetName();
		return startTargetModule(null);
	}

	/**
	 * 最初のモジュールの実行開始からの経過時間を計測する。
	 * @return	最初のモジュール実行開始からの経過時間(ミリ秒)
	 */
	protected long getTotalProcessTime() {
		if (_curExecutor != null) {
			return (_curExecutor.getProcessEndTime() - _startTime);
		} else {
			return (_endTime - _startTime);
		}
	}

	/**
	 * 実行対象のプロセスが実行中かを検証する。
	 * 
	 * @return プロセスが実行中なら <tt>true</tt>
	 */
	protected boolean isProcessRunning() {
		return (_curExecutor==null ? false : _curExecutor.isRunning());
	}

	/**
	 * タイマーの停止
	 */
	protected void stopTimer() {
		Timer timer = getTimer();
		if (timer != null && timer.isRunning()) {
			timer.stop();
		}
		timer = null;
	}

	/**
	 * プロセスの実行停止
	 */
	protected void stop() {
		// タイマー停止
		getRunIndicator().setRunning(false);
		Timer timer = getTimer();
		if (timer != null && timer.isRunning()) {
			timer.stop();
		}
		timer = null;

		// コマンドの実行停止
		if (_curExecutor != null) {
			// 実行停止
			CommandExecutor executor = _curExecutor;
			executor.destroy();
			_endTime = executor.getProcessEndTime();
			_targetData.setResults(executor, executor.isProcessInterrupted());
			updateProcessingTime(getTotalProcessTime());
			showQueueingOutput();
			printFinishedProcessMessage(_targetData);
			updateButtons();
			_curExecutor = null;
			_mainframe.addHistoryData(_targetData);
			if (_targetData.isUserCanceled())
				_resultState = USER_CANCELED;
			else if (!_targetData.isSucceeded())
				_resultState = FAILED;
		}
		
		// 未完のモジュールの有無をチェック
		if (_resultState == WAITING && _targetIndex < _modulelist.size()) {
			// 実行継続中であり、未完のモジュールが存在する
			_resultState = USER_CANCELED;
		}
		
		// ボタンの状態更新
		updateButtons();
	}

	/**
	 * キュー内の出力文字列をコンソールに出力
	 */
	protected void showQueueingOutput() {
		if (_curExecutor != null) {
			OutputString ostr;
			while ((ostr = _curExecutor.getCommandOutput().pop()) != null) {
				if (ostr.isError()) {
					//--- stderr
					getConsoleTextPane().appendErrorString(ostr.getString());
				} else {
					//--- stdout
					getConsoleTextPane().appendOutputString(ostr.getString());
				}
			}
		}
	}

	/**
	 * 実行対象に関する表示の更新
	 */
	protected void updateTargetName() {
		String title = "";
		String modulename = "";
		if (_targetData != null) {
			if (_visibleCount) {
				title = String.format("(%d/%d) %s", _targetIndex+1, _modulelist.size(), _targetData.getName());
			} else {
				title = _targetData.getName();
			}
			
			VirtualFile vfModule = _targetData.getModuleFile();
			if (vfModule != null) {
				VirtualFile vfBasePath = _targetData.getExecDefDirectory().getParentFile();
				modulename = vfModule.relativePathFrom(vfBasePath, Files.CommonSeparatorChar);
			}
		}
		
		setTitle(String.format(RunnerMessages.getInstance().ProcMonitorDlg_Title_Main, title));
		if (_lblName != null) {
			_lblName.setText(modulename);
		}
	}
	
	/**
	 * 実行完了モジュール数の取得
	 */
	protected int getFinishedModuleCount() {
		return (_prgFinishedModuleCount==null ? 0 : _prgFinishedModuleCount.getValue());
	}
	
	/**
	 * 実行完了モジュール数の増加
	 */
	protected void incrementFinishedModuleCount() {
		if (_prgFinishedModuleCount != null) {
			_prgFinishedModuleCount.setValue(_prgFinishedModuleCount.getValue() + 1);
		}
	}

	/**
	 * 実行開始からの経過時間の表示
	 */
	protected void updateProcessingTime(long time) {
		JLabel label = getTimeLabel();
		if (label != null) {
			label.setText(formatTime(time));
		}
	}
	
	/**
	 * ボタンの状態の更新
	 */
	protected void updateButtons() {
		if (isProcessRunning()) {
			// 実行中
			getCancelButton().setText(RunnerMessages.getInstance().ProcMonitorDlg_Button_Stop);
		} else {
			// 実行完了
			getCancelButton().setText(CommonMessages.getInstance().Button_Close);
		}
	}

	@Override
	protected void setupMainContents() {
		// create control panel
		JComponent cControl = createControlPanel();
		
		// create scrollable console
		JScrollPane scroll = new JScrollPane(getConsoleTextPane(),
											JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
											//JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
											JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// layout
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(cControl, gbc);
		gbc.gridy++;
		
		JProgressBar pbar = getFinishedModuleCountProgressBar();
		mainPanel.add(pbar, gbc);
		gbc.gridy++;
		
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 0, 0, 0);
		mainPanel.add(scroll, gbc);
		
		this.add(mainPanel, BorderLayout.CENTER);
	}

	@Override
	protected JButton createApplyButton() {
		// no apply button
		return null;
	}

	@Override
	protected JButton createOkButton() {
		// no OK button
		return null;
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
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
	
	protected Timer createTimer() {
		Timer tm = new Timer(DEFAULT_TIMER_INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onTimer(ae);
			}
		});
		return tm;
	}
	
	protected JLabel createNameLabel() {
		return createEtchedLabel();
	}
	
	protected JLabel createTimeLabel() {
		JLabel lbl = createEtchedLabel();
		lbl.setText("00000'00\"000");
		lbl.setHorizontalAlignment(JLabel.CENTER);
		Dimension dm = lbl.getPreferredSize();
		lbl.setPreferredSize(dm);
		lbl.setMinimumSize(dm);
		return lbl;
	}
	
	protected Border createLabelBorder() {
		Border bd = BorderFactory.createCompoundBorder(
						BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
						BorderFactory.createEmptyBorder(INSET_TB, INSET_LR, INSET_TB, INSET_LR)
					);
		return bd;
	}
	
	protected JLabel createEtchedLabel() {
		Border bd = createLabelBorder();
		JLabel lbl = new JLabel(" ");
		lbl.setBorder(bd);
		return lbl;
	}
	
	protected JProgressBar createProgressBar() {
		JProgressBar bar = new JProgressBar(JProgressBar.HORIZONTAL);
		return bar;
	}
	
	protected ConsoleTextPane createConsoleTextPane() {
		ConsoleTextPane pane = new ConsoleTextPane();
		pane.setLineWrap(true);			// 右端で折り返す
		pane.setLineLengthLimit(512);	// １行の文字数を 512 文字に制限
		return pane;
	}
	
	protected RunningAnimationLabel createRunIndicator() {
		RunningAnimationLabel label = new RunningAnimationLabel();
		//label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		return label;
	}
	
	protected JButton createShowArgsButton() {
		JButton btn = new JButton(RunnerMessages.getInstance().ProcMonitorDlg_Button_ShowArgs);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonShowArgs();
			}
		});
		return btn;
	}
	
	protected JComponent createControlPanel() {
		// パネル生成
		JPanel pnl = new JPanel(new GridBagLayout());
		
		// Layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		//--- running status
		pnl.add(getRunIndicator(), gbc);
		gbc.gridx++;
		gbc.insets = new Insets(0, INSET_LR, 0, 0);
		//--- time
		JLabel lblTime = getTimeLabel();
		if (lblTime != null) {
			pnl.add(lblTime, gbc);
			gbc.gridx++;
		}
		//--- target
		JLabel lblName = null;
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			lblName = getNameLabel();
		}
		if (lblName == null) {
			lblName = new JLabel(); // dummy
		}
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(lblName, gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx++;
		//--- [ShowArgs] button
		pnl.add(getShowArgsButton(), gbc);
		
		// 完了
		return pnl;
		
	}
	
	static public String formatTime(long millisec) {
		long tmilli = millisec % 1000L;
		
		millisec /= 1000L;
		long tsec = millisec % 60L;
		
		millisec /= 60L;
		long tmin = millisec % 60L;
		
		long thor = millisec / 60L;
		
		return String.format("%d:%02d'%02d\"%03d", thor, tmin, tsec, tmilli);
	}
	
	protected Timer getTimer() {
		if (_animTimer == null) {
			_animTimer = createTimer();
		}
		return _animTimer;
	}
	
	protected JLabel getNameLabel() {
		if (_lblName == null) {
			_lblName = createNameLabel();
		}
		return _lblName;
	}
	
	protected JLabel getTimeLabel() {
		if (_lblTime == null) {
			_lblTime = createTimeLabel();
		}
		return _lblTime;
	}
	
	protected JProgressBar getFinishedModuleCountProgressBar() {
		if (_prgFinishedModuleCount == null) {
			_prgFinishedModuleCount = createProgressBar();
		}
		return _prgFinishedModuleCount;
	}
	
	protected ConsoleTextPane getConsoleTextPane() {
		if (_console == null) {
			_console = createConsoleTextPane();
		}
		return _console;
	}
	
	protected RunningAnimationLabel getRunIndicator() {
		if (_runIndicator == null) {
			_runIndicator = createRunIndicator();
		}
		return _runIndicator;
	}
	
	protected JButton getShowArgsButton() {
		if (_btnShowArgs == null) {
			_btnShowArgs = createShowArgsButton();
		}
		return _btnShowArgs;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
