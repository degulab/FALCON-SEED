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
 * @(#)ProcessMonitorDialog.java	1.22	2012/08/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ProcessMonitorDialog.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
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
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import ssac.aadl.common.CommonMessages;
import ssac.falconseed.common.FSStartupSettings;
import ssac.falconseed.module.swing.MExecArgsModel;
import ssac.falconseed.module.swing.MExecArgsViewDialog;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.process.CommandExecutor;
import ssac.util.process.OutputString;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.ConsoleTextPane;
import ssac.util.swing.RunningAnimationLabel;

/**
 * モジュール実行定義の実行状態を表示するモニタダイアログ。
 * 
 * @version 1.22	2012/08/21
 */
public class ProcessMonitorDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final int INSET_LR = 2;
	static protected final int INSET_TB = 0;
	static protected final int DEFAULT_TIMER_INTERVAL = 200;
	
	static private final Dimension DM_MIN_SIZE = new Dimension(640, 320);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** ユーザーによる実行中断 **/
	private boolean _termByUser = false;

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
	/** 引数表示ダイアログ **/
	private MExecArgsViewDialog	_viewArgs;

	/** 実行プロセス **/
	protected CommandExecutor	_executor;
	/** モジュール実行定義の実行モデル **/
	protected final MExecArgsModel	_model;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ProcessMonitorDialog(Frame owner, CommandExecutor command, MExecArgsModel datamodel) {
		super(owner, String.format(RunnerMessages.getInstance().ProcMonitorDlg_Title_Main, datamodel.getSettings().getName()), true);
		if (command == null)
			throw new NullPointerException("The specified command is null.");
		this._model = datamodel;
		this._executor = command;
		setConfiguration(AppSettings.PROCESSMONITOR_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public ProcessMonitorDialog(Dialog owner, CommandExecutor command, MExecArgsModel datamodel) {
		super(owner, String.format(RunnerMessages.getInstance().ProcMonitorDlg_Title_Main, datamodel.getSettings().getName()), true);
		if (command == null)
			throw new NullPointerException("The specified command is null.");
		this._model = datamodel;
		this._executor = command;
		setConfiguration(AppSettings.PROCESSMONITOR_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	public void initialComponent() {
		super.initialComponent();

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
		return _termByUser;
	}

	/**
	 * コマンドを実行する。
	 * コマンドの実行が開始されると、モーダルダイアログとしてモニタが表示される。
	 * @throws	IOException	コマンドの実行に失敗した場合
	 * @throws IllegalStateException	コマンドが実行中の場合
	 */
	public void start() throws IOException
	{
		// check running
		if (isRunning()) {
			throw new IllegalStateException("Already running executor!");
		}
		
		// execution start
		_executor.start();
		
		// dialog to visible
		setVisible(true);
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
		if (isRunning()) {
			stop();
			_termByUser = true;
		} else {
			_termByUser = false;
		}
		return true;
	}

	/**
	 * 監視タイマーのイベント
	 */
	protected void onTimer(ActionEvent ae) {
		// 監視対象のチェック
		if (_executor == null) {
			// 監視対象が存在しないので、停止
			stop();
			return;
		}
		
		// 実行状態のチェック
		if (!isRunning()) {
			// 実行終了のため、停止
			stop();
			return;
		}
		
		// 出力更新
		getRunIndicator().next();
		updateProcessingTime(_executor.getProcessingTimeMillis());
		showQueueingOutput();
	}

	/**
	 * [実行時引数の表示]ボタン
	 */
	protected void onButtonShowArgs() {
		if (_viewArgs == null) {
			_viewArgs = new MExecArgsViewDialog(this, _model, false);
			_viewArgs.initialComponent();
		}
		_viewArgs.setVisible(true);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

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

	/**
	 * コマンドが実行中かを検証する。
	 * 
	 * @return 実行中のコマンドがあれば true
	 */
	protected boolean isRunning() {
		return (_executor != null ? _executor.isRunning() : false);
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
		if (_executor != null) {
			// 実行停止
			_executor.destroy();
			
			// 表示更新
			updateProcessingTime(_executor.getProcessingTimeMillis());
			showQueueingOutput();
			updateButtons();
			//--- event
			//fireExecutorStopped(_executor);
			//--- executor 破棄
			_executor = null;
		}
		
		// ボタンの状態更新
		updateButtons();
	}

	/**
	 * キュー内の出力文字列をコンソールに出力
	 */
	protected void showQueueingOutput() {
		if (_executor != null) {
			OutputString ostr;
			while ((ostr = _executor.getCommandOutput().pop()) != null) {
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
		if (_lblName != null) {
			String strName = "";
			VirtualFile vfModule = _model.getSettings().getModuleFile();
			if (vfModule != null) {
				VirtualFile vfBasePath = _model.getSettings().getExecDefDirectory().getParentFile();
				strName = vfModule.relativePathFrom(vfBasePath, Files.CommonSeparatorChar);
			}
			_lblName.setText(strName);
		}
	}

	/**
	 * 実行開始からの経過時間の表示
	 */
	protected void updateProcessingTime(long time) {
		JLabel label = getTimeLabel();
		if (label != null) {
			label.setText(ContinuousProcessMonitorDialog.formatTime(time));
		}
	}
	
	/**
	 * ボタンの状態の更新
	 */
	protected void updateButtons() {
		if (isRunning()) {
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
		
		gbc.gridy = 1;
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
	
//	protected String formatTime(long time) {
//		long tmin, tsec, tmilli;
//		if (time > 0L) {
//			tmin = time / 60000L;
//			time -= (tmin * 60000L);
//			tsec = time / 1000L;
//			tmilli = time - tsec * 1000L;
//		} else {
//			tmin = 0L;
//			tsec = 0L;
//			tmilli = 0L;
//		}
//		return String.format("%d'%02d\"%03d", tmin, tsec, tmilli);
//	}
	
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
