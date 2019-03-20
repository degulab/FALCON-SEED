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
 * @(#)AsyncProcessMonitorWindow.java	3.2.2	2015/10/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AsyncProcessMonitorWindow.java	3.0.0	2014/03/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
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
import ssac.util.process.CommandOutputBroker;
import ssac.util.process.InterruptibleCommandExecutor;
import ssac.util.process.OutputLogChannel;
import ssac.util.process.OutputLogChannelWriter;
import ssac.util.swing.AbBaseFrame;
import ssac.util.swing.BlockingConsoleTextPane;
import ssac.util.swing.RunningAnimationLabel;

/**
 * モジュール実行定義の実行状態を表示するモニタウィンドウ。
 * このウィンドウは、実行状態とプロセス出力内容を表示するコンソールを持ち、
 * プロセスの実行とは関係なく、表示非表示が可能となる。
 * また、実行の中断や強制停止を可能とする。
 * 
 * @version 3.2.2
 * @since 3.0.0
 */
public class AsyncProcessMonitorWindow extends AbBaseFrame
implements ActionListener, ComponentListener, WindowListener, WindowStateListener, WindowFocusListener
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -447774941236695272L;
	
	static protected final int INSET_LR = 2;
	static protected final int INSET_TB = 0;
	/** タイマー実行間隔(ミリ秒) **/
	static protected final int DEFAULT_TIMER_INTERVAL	= 200;
	/** 中断開始から強制終了可能となるまでの時間(ミリ秒) **/
	static protected final long DELAY_TERM_ARROW_KILL	= 1000L;
	
	static private final Dimension DM_MIN_SIZE	= new Dimension(640, 320);
	static private final Dimension DM_DEF_SIZE	= new Dimension(640, 320);

	/** 実行完了時の状態：実行完了待ち **/
	static public int	WAITING			= -1;
	/** 実行完了時の状態：全モジュール正常終了 **/
	static public int ALL_SUCCEEDED	= 0;
	/** 実行完了時の状態：モジュール実行失敗 **/
	static public int FAILED			= 1;
	/** 実行完了時の状態：ユーザによるキャンセル **/
	static public int USER_CANCELED	= 2;
	
	static protected final String	CMD_STOP		= "stop";
	static protected final String	CMD_KILL		= "kill";
	static protected final String	CMD_COPYTOCLIP	= "copyToC";
	static protected final String	CMD_CLOSE		= "close";
	static protected final String	CMD_SHOWARGS	= "showArgs";
	static protected final String	CMD_AUTOCLOSE	= "autoClose";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//
	// 状態およびデータ
	//

	/** シャットダウンフェーズで強制停止処理を行うことを示すフラグ */
	protected boolean _shutdown = false;
	
//	/** このウィンドウの最後の位置 **/
//	private Point		_lastFrameLocation;
//	/** このウィンドウの最後のサイズ **/
//	private Dimension	_lastFrameSize;

	/** 実行完了時の状態 **/
	private int		_resultState = WAITING;
	
	/** モジュール実行開始時刻(エポック) **/
	private long		_startTime;
	/** モジュール中断開始時刻(エポック) **/
	private long		_termStartTime;
	/** モジュール実行終了時刻(エポック) **/
	private long		_endTime;
	/** 直前のプロセス実行状態 **/
	private int			_lastStatus = InterruptibleCommandExecutor.COMMAND_UNEXECUTED;
	/** 状態を含まないウィンドウタイトル **/
	private String		_windowTitle;

	/** 実行プロセス **/
	protected InterruptibleCommandExecutor	_curExecutor;
	/** 実行対象モジュールデータのみを格納するモジュールリスト(互換性のため) **/
	protected RelatedModuleList			_moduleList;
	/** 実行対象のモジュールデータ **/
	protected ModuleRuntimeData			_targetData;
	/** 実行対象のモジュールデータのインデックス **/
	protected int							_targetIndex;
	/** 実行完了時に自動的に閉じるフラグの初期値 **/
	protected boolean						_initialAutoClose;
	/** 中断処理実行中 **/
	protected boolean		_doTerminate;
	/** 強制終了実行中 **/
	protected boolean		_doKilled;

	//
	// コンポーネント
	//

	/** このダイアログのオーナーとなるメインフレーム **/
	protected final RunnerFrame	_mainframe;

	/** ハンドラ **/
	protected IAsyncProcessMonitorHandler	_handler;

	/** 監視タイマー **/
	private Timer	_animTimer;

	/** コンソールテキスト表示コンポーネント **/
	private BlockingConsoleTextPane	_console;
	/** 実行時アニメーション表示ラベル **/
	private RunningAnimationLabel	_runIndicator;
	
	/** 実行完了モジュール数のプログレスバー **/
	private JProgressBar	_prgFinishedModuleCount;

	/** 実行モジュール表示ラベル(for Debug) **/
	private JLabel		_lblName;
	/** 実行経過時間表示ラベル **/
	private JLabel		_lblTime;
	/** 閉じるボタン **/
	private JButton		_btnClose;
	/** 停止ボタン **/
	private JButton		_btnStop;
	/** 強制終了ボタン **/
	private JButton		_btnKill;
	/** クリップボードへコピーボタン **/
	private JButton		_btnCopyToClipboard;
	/** 引数表示ボタン **/
	private JButton		_btnShowArgs;
	/** 自動的に閉じるチェックボックス **/
	private JCheckBox	_chkAutoClose;
	/** 引数表示ダイアログ **/
	private ModuleArgsViewDialog	_viewArgs;
	/** モジュール実行数を表示する場合は <tt>true</tt> **/
	protected final boolean		_visibleCount;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたモジュールを実行する。
	 * @param frame		メインフレーム
	 * @param modules	実行対象のモジュールを保持するオブジェクト
	 * @param consoleShowAtStart	実行開始後にウィンドウを表示する場合は <tt>true</tt>
	 * @param consoleAutoClose		実行完了時に自動的に閉じる場合は <tt>true</tt>
	 * @return	正常に実行が開始された場合はウィンドウオブジェクト、それ以外の場合は <tt>null</tt>
	 * @return	すべてのモジュール実行が正常に完了した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public AsyncProcessMonitorWindow runModule(final RunnerFrame frame, final ModuleRuntimeData module,
														boolean consoleShowAtStart, boolean consoleAutoClose)
	{
		// check
		if (frame == null)
			throw new NullPointerException("'frame' argument is null.");
		if (module == null)
			throw new NullPointerException("'module' argument is null.");
		
		// ウィンドウ生成
		RelatedModuleList modlist = new RelatedModuleList(1);
		modlist.add(module);
		AsyncProcessMonitorWindow wnd = new AsyncProcessMonitorWindow(frame, modlist, false, consoleAutoClose);
		wnd.initialComponent();
		if (!wnd.startTargetModule(frame)) {
			if (wnd.isDisplayable()) {
				wnd.dispose();
			}
			return null;
		}
		
		// 実行状態更新
		wnd.updateFirstModuleStartTime();
		if (consoleShowAtStart) {
			wnd.showAndPopupWindow();
		}
		return wnd;
	}

	/**
	 * 指定されたすべてのモジュールを実行する。
	 * @param frame		メインフレーム
	 * @param modules	実行対象のモジュールを保持するオブジェクト
	 * @param consoleShowAtStart	実行開始後にウィンドウを表示する場合は <tt>true</tt>
	 * @param consoleAutoClose		実行完了時に自動的に閉じる場合は <tt>true</tt>
	 * @return	正常に実行が開始された場合はウィンドウオブジェクト、それ以外の場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>modules</em> が空、もしくは <tt>null</tt> 要素を含む場合
	 */
	static public AsyncProcessMonitorWindow runAllModules(final RunnerFrame frame, final RelatedModuleList modules,
															boolean consoleShowAtStart, boolean consoleAutoClose)
	{
		// check
		if (frame == null)
			throw new NullPointerException("'frame' argument is null.");
		if (modules == null)
			throw new NullPointerException("'modules' argument is null.");
		if (modules.isEmpty())
			throw new IllegalArgumentException("Module is empty.");
		
		// ウィンドウ生成
		boolean showCount = false;
		AsyncProcessMonitorWindow wnd = new AsyncProcessMonitorWindow(frame, modules, showCount, consoleAutoClose);
		wnd.initialComponent();
		if (!wnd.startTargetModule(frame)) {
			if (wnd.isDisplayable()) {
				wnd.dispose();
			}
			return null;
		}
		
		// 実行状態更新
		wnd.updateFirstModuleStartTime();
		if (consoleShowAtStart) {
			wnd.showAndPopupWindow();
		}
		return wnd;
	}

	/**
	 * 新しいコンポーネントを生成する。
	 * @param mainframe	メインフレーム
	 * @param modules		実行対象モジュールの実行順リスト
	 * @param showCount		実行中のモジュール数を表示する場合は <tt>true</tt>
	 * @param consoleAutoClose	実行完了時に自動的に閉じる
	 */
	protected AsyncProcessMonitorWindow(final RunnerFrame mainframe, final RelatedModuleList modules, boolean showCount, boolean consoleAutoClose)
	{
		super();
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		if (modules.size() > 1 || showCount) {
			//--- モジュール数が複数、もしくはカウント表示が要求された場合は表示する。
			this._visibleCount = true;
		} else {
			//--- モジュール数が 1 で、カウント表示が要求されていない場合は非表示にする。
			this._visibleCount = false;
		}
		setStoreWindowState(false);	// ウィンドウステートは保存しない(3.2.2)
		_mainframe = mainframe;
		_moduleList = modules;
		_initialAutoClose = consoleAutoClose;
		_lastStatus = InterruptibleCommandExecutor.COMMAND_UNEXECUTED;
		setTitle(String.format(RunnerMessages.getInstance().ProcMonitorDlg_Title_Main, ""));
		//setConfiguration(AppSettings.PROCESSMONITOR_DLG, AppSettings.getInstance().getConfiguration());
	}

	/**
	 * コンポーネントの初期化
	 */
	public void initialComponent() {
		// initialize frame
		Image imgIcon = ModuleRunner.getAppIconImage();
		if (imgIcon != null) {
			setIconImage(imgIcon);
		}
//		_lastFrameLocation = null;
//		_lastFrameSize = new Dimension();
		
		// setup main contents
		setupMainContents();
		
		// setup progress bar
		_prgFinishedModuleCount.setMinimum(0);
		_prgFinishedModuleCount.setMaximum(_moduleList.size());
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
		_targetData  = _moduleList.getData(_targetIndex);
		
		// minimum size
		setMinimumSize(DM_MIN_SIZE);
		
		// setup Window actions
		addComponentListener(this);
		addWindowListener(this);
		addWindowStateListener(this);
		//setupComponentActions();

		// 表示の初期化
		setupTitleModuleNames();
		updateProcessingTime(0L);
		setupTitleByLastStatus();
		setupButtonsByLastStatus();
		
		// restore View's settings
		restoreSettings();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ModuleRuntimeData getModuleData() {
		return _targetData;
	}
	
	public IAsyncProcessMonitorHandler getMonitorHandler() {
		return _handler;
	}
	
	public void setMonitorHandler(IAsyncProcessMonitorHandler handler) {
		_handler = handler;
	}
	
	public int getProcessStatus() {
		return _lastStatus;
	}

	/**
	 * 最初のモジュールの実行開始時刻を返す。
	 * @return	時刻(エポック)
	 */
	public long getProcessStartTime() {
		return _startTime;
	}

	/**
	 * 最初のモジュール実行開始からの経過時間を計測する。
	 * @return	最初のモジュール実行開始からの経過時間(ミリ秒)
	 */
	public long getProcessingTime() {
		if (_curExecutor != null) {
			return (_curExecutor.getProcessEndTime() - _startTime);
		} else {
			return (_endTime - _startTime);
		}
	}

	/**
	 * プロセスが実行中である場合に <tt>true</tt> を返す。
	 * プロセスが中断処理中もしくは強制停止中の場合、このメソッドは <tt>false</tt> を返す。
	 */
	public boolean isProcessRunning() {
		return (_lastStatus == InterruptibleCommandExecutor.COMMAND_RUNNING);
	}

	/**
	 * プロセスが中断処理中、もしくは強制停止中である場合に <tt>true</tt> を返す。
	 * プロセスが正常に実行中の場合、このメソッドは <tt>false</tt> を返す。
	 */
	public boolean isProcessTerminating() {
		return (_lastStatus == InterruptibleCommandExecutor.COMMAND_TERMINATING);
	}

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

	/**
	 * 実行中断が可能な場合に、実行を中断する。
	 * @return	中断が開始された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean doTerminate() {
		boolean modified = false;
		if (_curExecutor != null) {
			int stat = _curExecutor.status();
			if (stat == InterruptibleCommandExecutor.COMMAND_RUNNING) {
				// 実行中の場合に中断可能
				_doTerminate = true;
				_curExecutor.terminate();
				_termStartTime = System.currentTimeMillis();
				updateStatus();
				if (_curExecutor.status() != InterruptibleCommandExecutor.COMMAND_RUNNING) {
					modified = true;
				}
			}
		}
		return modified;
	}

	/**
	 * 中断要求後に強制停止操作（ボタン）が可能となったかどうかを判定する。
	 * このメソッドはユーザーインタフェースにおいて、強制停止操作を可能とするかどうかを判定するために利用される。
	 * @return	強制停止操作が未実行であり、強制停止操作可能な場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isEnabledKillButtonAfterTermRequested() {
		if (canKillAfterTermRequested()) {
			if ((System.currentTimeMillis() - _termStartTime) >= DELAY_TERM_ARROW_KILL) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 中断処理中であり、強制停止操作が可能かどうかを判定する。
	 * このメソッドは強制停止の実行を問い合わせるために利用される。
	 * @return	強制停止操作が未実行であり、強制停止可能な場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean canKillAfterTermRequested() {
		if (_curExecutor != null) {
			int stat = _curExecutor.status();
			if (stat==InterruptibleCommandExecutor.COMMAND_TERMINATING && _doTerminate && !_doKilled) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 実行を強制停止する。
	 */
	public void doKill() {
		if (_curExecutor != null) {
			int stat = _curExecutor.status();
			if (stat==InterruptibleCommandExecutor.COMMAND_RUNNING || stat==InterruptibleCommandExecutor.COMMAND_TERMINATING) {
				_doKilled = true;
				_curExecutor.kill();
				updateStatus();
			}
		}
	}

	/**
	 * プロセスが稼働中であれば、シャットダウンフェーズにて強制停止を行う。
	 * 強制停止の場合、終了ハンドラは呼び出さない。
	 */
	public void shutdown() {
		_shutdown = true;
		_doKilled = true;
		getConsoleTextPane().setEnabled(false);
		if (_curExecutor != null) {
			_curExecutor.kill();
			getConsoleTextPane().flushUntilQueueEmpty();
			_curExecutor.getProcessOutputWriter().close();
		}
		getConsoleTextPane().flushUntilQueueEmpty();
		dispose();
	}

	/**
	 * このモニタを最前面に表示する。
	 */
	public void showAndPopupWindow() {
		setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				toFront();
			}
		});
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	protected void closeWindow() {
		//--- 引数表示ダイアログを閉じる
		if (_viewArgs != null) {
			_viewArgs.setVisible(false);
			//_viewArgs.dispose();
			//_viewArgs = null;
		}
		//--- このダイアログを閉じる
		setVisible(false);
	}

	/**
	 * ボタンがクリックされたときに呼び出されるハンドラ
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		String cmd = ae.getActionCommand();
		if (CMD_CLOSE.equals(cmd)) {
			// [閉じる]ボタン
			closeWindow();
		}
		else if (CMD_STOP.equals(cmd)) {
			// [停止]ボタン
			if (_curExecutor != null) {
				doTerminate();
			}
		}
		else if (CMD_KILL.equals(cmd)) {
			// [強制終了]ボタン
			if (_curExecutor != null) {
				int ret = JOptionPane.showConfirmDialog(this, RunnerMessages.getInstance().confirmKillTerminatingFilter,
												CommonMessages.getInstance().msgboxTitleWarn, JOptionPane.OK_CANCEL_OPTION);
				if (ret == JOptionPane.OK_OPTION) {
					doKill();
				}
			}
		}
		else if (CMD_COPYTOCLIP.equals(cmd)) {
			// [クリップボードへコピー]ボタン
			String txt = getConsoleTextPane().getText();
			if (txt != null) {
				StringSelection ss = new StringSelection(txt);
				Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
				cb.setContents(ss, null);
			}
		}
		else if (CMD_SHOWARGS.equals(cmd)) {
			// [引数表示]ボタン
			if (_viewArgs == null) {
				_viewArgs = new ModuleArgsViewDialog(this, _moduleList, false);
				_viewArgs.initialComponent();
			}
			_viewArgs.setVisible(true);
		}
	}

	//------------------------------------------------------------
	// Internal methods (実行制御)
	//------------------------------------------------------------

	/**
	 * このモニタの状態を更新する。
	 * @return	実行継続中なら <tt>true</tt>、実行が終了した場合は <tt>false</tt>
	 */
	public boolean doUpdate() {
		// 監視対象のチェック
		if (_curExecutor == null) {
			// 監視対象が存在しないので、停止
			stop();
			return false;
		}
		
		// 実行状態のチェック
		if (!checkProcessAndStartNext()) {
			// 継続不要
			stop();
			return false;
		}
		
		// 出力更新
		getRunIndicator().next();
		updateProcessingTime(getProcessingTime());
		//--- プロセス出力は別の機構で処理中
		return true;
	}

	/**
	 * 現在実行中のモジュールを監視し、実行完了なら次のモジュールを実行する。
	 * 実行中のものがなければ、次のモジュールへシフトする。
	 * @return	実行継続中なら <tt>true</tt>、実行が終了した場合は <tt>false</tt>
	 */
	protected boolean checkProcessAndStartNext() {
		if (_curExecutor != null) {
			if (_curExecutor.isRunning()) {
				//--- プロセスが稼働中なら、チェック継続
				return true;
			}
		
			// 実行完了(プロセス停止)
			InterruptibleCommandExecutor executor = _curExecutor;
			//executor.destroy();
			executor.kill();	//-- 念のため(不要?)
			getConsoleTextPane().flushUntilQueueEmpty();	// ブロッキングキューの内容をすべてコンソールに出力
			_endTime = executor.getProcessEndTime();
			_targetData.setResults(executor, isUserCancelState(executor.status()));
			updateProcessingTime(getProcessingTime());
			printFinishedProcessMessage(_targetData);
			updateStatus();	// プロセス情報が破棄される前に更新
			_curExecutor.getProcessOutputWriter().close();	// ログを閉じる
			_curExecutor = null;
			_mainframe.addHistoryData(_targetData);
			incrementFinishedModuleCount();
			
			// 終了状態の確認
			if (_targetData.isUserCanceled()) {
				// ユーザー停止要求により停止
				_resultState = USER_CANCELED;
				return false;	// 実行中断
			}
			else if (!_targetData.isSucceeded()) {
				// 異常終了
				_resultState = FAILED;
				return false;	// 実行中断
			}
		}
		
		// 次の実行を開始
		++_targetIndex;
		if (_targetIndex >= _moduleList.size()) {
			_resultState = ALL_SUCCEEDED;	// すべて実行完了
			updateStatus();
			return false;
		}
		else if (_doTerminate || _doKilled) {
			// プロセス終了のタイミングで、ユーザーによる中断要求が発生していた場合、
			// 残りのモジュールは実行しない
			_resultState = USER_CANCELED;
			updateStatus();
			return false;
		}
		
		// 次のモジュールを実行
		_targetData = _moduleList.getData(_targetIndex);
		setupTitleModuleNames();
		return startTargetModule(null);	//--- プロセス実行
	}

	/**
	 * プロセスの実行停止
	 */
	protected void stop() {
		// タイマー停止
		getRunIndicator().setRunning(false);
		stopTimer();

		// コマンドの実行停止
		if (_curExecutor != null) {
			// 実行停止
			InterruptibleCommandExecutor executor = _curExecutor;
			//executor.destroy();
			executor.kill();	//--- 実行中であった場合は、強制停止
			getConsoleTextPane().flushUntilQueueEmpty();	// ブロッキングキューの内容をすべてコンソールに出力
			_endTime = executor.getProcessEndTime();
			_targetData.setResults(executor, isUserCancelState(executor.status()));
			updateProcessingTime(getProcessingTime());
			printFinishedProcessMessage(_targetData);
			updateStatus();	// プロセス情報が破棄される前に更新
			_curExecutor.getProcessOutputWriter().close();	// ログを閉じる
			_curExecutor = null;
			_mainframe.addHistoryData(_targetData);
			if (_targetData.isUserCanceled())
				_resultState = USER_CANCELED;
			else if (!_targetData.isSucceeded())
				_resultState = FAILED;
		}
		
		// 未完のモジュールの有無をチェック
		if (_resultState == WAITING && _targetIndex < _moduleList.size()) {
			// 実行継続中であり、未完のモジュールが存在する
			_resultState = USER_CANCELED;
		}
		
		// 表示の最終更新
		updateStatus();
		
		// 表示もしくは破棄処理
		if (_chkAutoClose.isSelected()) {
			// 自動的な破棄が要求されている
			if (isDisplayable()) {
				dispose();
			}
		}
		else {
			// 自動で閉じない場合は、ウィンドウをポップアップ
			showAndPopupWindow();
		}
	}

	/**
	 * 現在のターゲットモジュールの実行を開始する。
	 * @param parentComponent	メッセージボックスを表示する際の親ウィンドウ、メッセージボックスを表示しない場合は <tt>null</tt>
	 * @return 実行が開始された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean startTargetModule(Component parentComponent) {
		if (_targetData == null) {
			// 初回呼び出し時のみ、ここに入る可能性あり
			stopTimer();
			String errmsg = "Target Module Data is null!";
			AppLogger.error("<AsyncProcessMonitorWindow> " + errmsg);
			ModuleRunner.showErrorMessage(parentComponent, errmsg);
			_resultState = FAILED;
			return false;
		}
		
		// コマンドの出力を受け付けるライターの生成
		getConsoleTextPane().setQueueingEnabled(true);
		//--- ログファイルの生成
		File fLogFile = null;
		try {
			fLogFile = InterruptibleCommandExecutor.createDefaultLogFile();
		}
		catch (Throwable ex) {
			String errmsg = RunnerMessages.getInstance().msgFailedToCreateCommandLogFile;
			AppLogger.error("<AsyncProcessMonitorWindow> " + errmsg, ex);
			if (parentComponent != null) {
				ModuleRunner.showErrorMessage(parentComponent, errmsg);
			} else {
				getConsoleTextPane().println(true, "");
				getConsoleTextPane().println(true, errmsg);
			}
			_resultState = FAILED;
			return false;
		}
		//--- ログファイルのオープン
		boolean hasError = true;
		OutputLogChannel logChannel = null;
		OutputLogChannelWriter logWriter = null;
		try {
			logChannel = new OutputLogChannel(fLogFile, false);
			logWriter = logChannel.getWriter();
			hasError = false;
		}
		catch (Throwable ex) {
			String errmsg = CommonMessages.formatErrorMessage(RunnerMessages.getInstance().msgFailedToOpenCommandLogFile, ex, fLogFile);
			AppLogger.error("<AsyncProcessMonitorWindow> " + errmsg, ex);
			if (parentComponent != null) {
				ModuleRunner.showErrorMessage(parentComponent, errmsg);
			} else {
				getConsoleTextPane().println(true, "");
				getConsoleTextPane().println(true, errmsg);
			}
			_resultState = FAILED;
			fLogFile.delete();
			return false;
		}
		finally {
			if (hasError) {
				if (logChannel != null) {
					logChannel.close();
				}
				logChannel = null;
				logWriter = null;
				fLogFile.delete();
			}
		}
		//--- ライター生成(新規に作成)
		CommandOutputBroker broker = new CommandOutputBroker();
		broker.addWriter(logWriter);
		broker.addWriter(getConsoleTextPane());
		_targetData.setLogFile(fLogFile);

		// エグゼキューターの生成
		InterruptibleCommandExecutor executor = null;
		try {
			executor = _targetData.createInterruptibleCommandExecutor();
			executor.setOutputWriter(broker);
		}
		catch (IOException ex) {
			String errmsg = ModuleRuntimeData.formatErrorMessage(_targetData, RunnerMessages.getInstance().msgFailedToCreateTempFile);
			AppLogger.error("<AsyncProcessMonitorWindow> " + errmsg, ex);
			if (parentComponent != null) {
				ModuleRunner.showErrorMessage(parentComponent, errmsg);
			} else {
				getConsoleTextPane().println(true, "");
				getConsoleTextPane().println(true, errmsg);
			}
			executor = null;
		}
		catch (Throwable ex) {
			String errmsg = ModuleRuntimeData.formatErrorMessage(_targetData, RunnerMessages.getInstance().msgIllegalModuleArguments);
			AppLogger.error("<AsyncProcessMonitorWindow> " + errmsg, ex);
			errmsg = CommonMessages.formatErrorMessage(errmsg, ex);
			if (parentComponent != null) {
				ModuleRunner.showErrorMessage(parentComponent, errmsg);
			} else {
				getConsoleTextPane().println(true, "");
				getConsoleTextPane().println(true, errmsg);
			}
			executor = null;
		}
		if (executor==null) {
			_resultState = FAILED;
			broker.close();
			fLogFile.delete();
			_targetData.setLogFile(null);
			return false;
		}

		// start process
		try {
			executor.start();
		}
		catch (Throwable ex) {
			stopTimer();
			String errmsg = ModuleRuntimeData.formatErrorMessage(_targetData, CommonMessages.getInstance().msgCouldNotExecute);
			AppLogger.error("<AsyncProcessMonitorWindow> " + errmsg, ex);
			if (parentComponent != null) {
				ModuleRunner.showErrorMessage(parentComponent, errmsg);
			} else {
				getConsoleTextPane().println(true, "");
				getConsoleTextPane().println(true, errmsg);
			}
			_resultState = FAILED;
			broker.close();
			return false;
		}
		
		//--- update executor
		_curExecutor = executor;
		_targetData.setStartTime(executor.getProcessStartTime());
		printStartProcessMessage(_targetData);
		
		// start timer
		getRunIndicator().setRunning(true);
		startTimer();	//-- タイマーが開始していなければ、タイマー始動
		updateStatus();	//-- 表示内容を更新
		
		return true;
	}

	/**
	 * ユーザーによって停止されたことを示すプロセス状態コードか判定する。
	 * 中断による停止、強制停止、強制破棄のどれかの状態コードであれば、このメソッドは <tt>true</tt> を返す。
	 * @return	ユーザーによる停止の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean isUserCancelState(int status) {
		switch (status) {
			case InterruptibleCommandExecutor.COMMAND_INTERRUPTED :
			case InterruptibleCommandExecutor.COMMAND_KILLED :
			case InterruptibleCommandExecutor.COMMAND_DESTROYED :
				return true;
			default :
				return false;
		}
	}

	/**
	 * 実行状態を更新し、変更があれば、タイトルおよびボタン状態などを更新する。
	 * <br>実行時間は変更されない。
	 */
	protected void updateStatus() {
		// 現在のプロセス情報で、直前のステータスを更新
		if (_curExecutor != null) {
			_lastStatus = _curExecutor.status();
		}
		
		// 表示内容を更新
		setupTitleByLastStatus();
		setupButtonsByLastStatus();
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
	
	protected JProgressBar createProgressBar() {
		JProgressBar bar = new JProgressBar(JProgressBar.HORIZONTAL);
		return bar;
	}
	
	protected JProgressBar getFinishedModuleCountProgressBar() {
		if (_prgFinishedModuleCount == null) {
			_prgFinishedModuleCount = createProgressBar();
		}
		return _prgFinishedModuleCount;
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
	
	protected void updateFirstModuleStartTime() {
		_startTime = (_curExecutor==null ? System.currentTimeMillis() : _curExecutor.getProcessStartTime());
	}
	
	protected void setupTitleModuleNames() {
		String titleName  = "";
		String moduleName = "";
		if (_targetData != null) {
			if (_visibleCount) {
				titleName = String.format("(%d/%d) %s", _targetIndex+1, _moduleList.size(), _targetData.getName());
			} else {
				titleName = _targetData.getName();
			}
			
			VirtualFile vfModule = _targetData.getModuleFile();
			if (vfModule != null) {
				VirtualFile vfBasePath = _targetData.getExecDefDirectory().getParentFile();
				moduleName = vfModule.relativePathFrom(vfBasePath, Files.CommonSeparatorChar);
			}
		}

		_windowTitle = String.format(RunnerMessages.getInstance().ProcMonitorDlg_Title_Main, titleName);
		setTitle(_windowTitle);
		if (_lblName != null) {
			_lblName.setText(moduleName);
		}
	}

	/**
	 * 直前のプロセス実行状態に基づき、ウィンドウタイトルを設定する。
	 */
	protected void setupTitleByLastStatus() {
		if (_resultState == WAITING) {
			if (_lastStatus == InterruptibleCommandExecutor.COMMAND_RUNNING) {
				// 実行中
				setTitle(String.format("%s (%s)", _windowTitle, RunnerMessages.getInstance().ProcMonitorDlg_State_Running));
				return;
			}
			else if (_lastStatus == InterruptibleCommandExecutor.COMMAND_TERMINATING) {
				// 中断処理中
				setTitle(String.format("%s (%s)", _windowTitle, RunnerMessages.getInstance().ProcMonitorDlg_State_Terminating));
				return;
			}
		}
		
		// 上記以外では、停止または未実行
		setTitle(_windowTitle);	// この場合は、タイトルへの修飾はなし
	}

	/**
	 * 直前のプロセス実行状態に基づき、ボタン状態を設定する。
	 */
	protected void setupButtonsByLastStatus() {
		RunningAnimationLabel lblRunning = getRunIndicator();
		JButton btnStop = getStopButton();
		JButton btnKill = getKillButton();
		if (_resultState == WAITING) {
			if (_lastStatus == InterruptibleCommandExecutor.COMMAND_RUNNING) {
				// 実行中
				lblRunning.setRunning(true);
				btnStop.setEnabled(!_doTerminate);
				btnStop.setVisible(true);
				btnKill.setEnabled(false);
				btnKill.setVisible(false);
				return;
			}
			else if (_lastStatus == InterruptibleCommandExecutor.COMMAND_TERMINATING) {
				// 中断処理中
				lblRunning.setRunning(true);
				if (isEnabledKillButtonAfterTermRequested()) {
					// 強制終了可能
					btnKill.setEnabled(!_doKilled);
					btnKill.setVisible(true);
					btnStop.setEnabled(false);
					btnStop.setVisible(false);
				} else {
					// 中断処理待機中
					btnStop.setEnabled(false);
					btnStop.setVisible(true);
					btnKill.setEnabled(false);
					btnKill.setVisible(false);
				}
				return;
			}
		}
		// 停止または未実行
		lblRunning.setRunning(false);
		btnStop.setEnabled(false);
		btnStop.setVisible(true);
		btnKill.setEnabled(false);
		btnKill.setVisible(false);
	}
	
	protected void printStartProcessMessage(ModuleRuntimeData data) {
		String msg = ModuleRuntimeData.formatErrorMessage(data, "Start...");
		getConsoleTextPane().println(false, msg);
	}
	
	protected void printFinishedProcessMessage(ModuleRuntimeData data) {
		if (data.isUserCanceled()) {
			String msg = ModuleRuntimeData.formatErrorMessage(data, "Canceled.");
			getConsoleTextPane().println(false, msg);
		}
		else if (data.isSucceeded()) {
			String msg = ModuleRuntimeData.formatErrorMessage(data, "Succeeded(%d, time=%s)", data.getExitCode(), formatTime(data.getProcessTime()));
			getConsoleTextPane().println(false, msg);
		}
		else {
			String msg = ModuleRuntimeData.formatErrorMessage(data, "Failed(%d, time=%s)", data.getExitCode(), formatTime(data.getProcessTime()));
			getConsoleTextPane().println(true, msg);
		}
	}

	/**
	 * タイマーの生成
	 */
	protected Timer createTimer() {
		Timer tm = new Timer(DEFAULT_TIMER_INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				doUpdate();
			}
		});
		return tm;
	}

	/**
	 * タイマーの開始。
	 * 更新処理は外部から行う。
	 */
	protected void startTimer() {
		if (_animTimer == null) {
			_animTimer = createTimer();
		}
		if (!_animTimer.isRunning()) {
			_animTimer.start();
		}
	}

	/**
	 * タイマーの停止。
	 * 更新処理は外部から行う。
	 */
	protected void stopTimer() {
		if (_animTimer != null && _animTimer.isRunning()) {
			_animTimer.stop();
		}
		_animTimer = null;
	}

	/**
	 * このウィンドウの規定標準サイズを返す。
	 * @since 3.2.2
	 */
	@Override
	protected Dimension getDefaultSize() {
		return DM_DEF_SIZE;
	}

	/**
	 * アプリケーションのプロパティから、コンポーネントの状態を復帰する。
	 */
	private void restoreSettings() {
		final String key = AppSettings.PROCESSMONITOR_DLG;
		AppSettings settings = AppSettings.getInstance();

		// restore window states
		restoreWindowStatus(settings.getConfiguration(), key);
//		Point     mainLoc   = settings.getWindowLocation(key);
//		Dimension mainSize  = settings.getWindowSize(key);
//		if (mainLoc != null) {
//			if (mainSize != null) {
//				Rectangle rc = SwingTools.convertIntoAllScreenDesktopBounds(new Rectangle(mainLoc, mainSize));
//				if (rc != null) {
//					mainLoc  = rc.getLocation();
//					mainSize = rc.getSize();
//				} else {
//					mainLoc  = null;
//					mainSize = null;
//				}
//			} else {
//				mainLoc = null;
//			}
//		}
//		else if (mainSize != null) {
//			mainSize = SwingTools.convertIntoDesktop(mainSize);
//		}
//		//--- Window size
//		if (mainSize != null) {
//			_lastFrameSize.setSize(mainSize);
//		} else {
//			_lastFrameSize.setSize(DM_DEF_SIZE);
//		}
//		this.setSize(_lastFrameSize);
//		//--- Window location
//		if (mainLoc != null) {
//			_lastFrameLocation = new Point(mainLoc);
//			this.setLocation(mainLoc);
//		} else {
//			// default location
//			_lastFrameLocation = null;
//			this.setLocationRelativeTo(null);
//		}
//		//--- no state
	}

	/**
	 * 現在のコンポーネントの状態を、アプリケーションのプロパティとして保存する。
	 */
	private void storeSettings() {
		final String key = AppSettings.PROCESSMONITOR_DLG;
		AppSettings settings = AppSettings.getInstance();
		// Window states
		storeWindowStatus(settings.getConfiguration(), key);
	}

	/**
	 * このコンポーネントの要素を生成する
	 */
	protected void setupMainContents() {
		// create control panel
		JComponent cControl = createControlPanel();
		
		// create button panel
		JComponent cButtons = createButtonPanel();
		
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
		//--- control
		mainPanel.add(cControl, gbc);
		gbc.gridy++;
		//--- progress bar
		JProgressBar pbar = getFinishedModuleCountProgressBar();
		mainPanel.add(pbar, gbc);
		gbc.gridy++;
		//--- text
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 0, 0, 0);
		mainPanel.add(scroll, gbc);
		gbc.gridy++;
		//--- buttons
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(cButtons, gbc);
		
		// add
		getContentPane().add(mainPanel, BorderLayout.CENTER);
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
		gbc.gridx++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		//--- 停止ボタン
		pnl.add(getStopButton(), gbc);
		gbc.gridx++;
		//--- 強制終了ボタン(初期状態は非表示)
		pnl.add(getKillButton(), gbc);
		getKillButton().setVisible(false);
		gbc.gridx++;
		//--- クリップボードへコピーボタン
		pnl.add(getCopyToClipboardButton(), gbc);
		gbc.gridx++;
		//--- [ShowArgs] button
		gbc.anchor = GridBagConstraints.EAST;
		pnl.add(getShowArgsButton(), gbc);
		
		// 完了
		return pnl;
	}
	
	protected JComponent createButtonPanel() {
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(getAutoCloseCheckBox());
		btnBox.add(Box.createHorizontalGlue());
		btnBox.add(getCloseButton());
		return btnBox;
	}
	
	static public String formatShortTime(long millisec) {
		millisec /= 1000L;
		long tsec = millisec % 60L;
		
		millisec /= 60L;
		long tmin = millisec % 60L;
		
		long thor = millisec / 60L;
		
		return String.format("%d:%02d'%02d\"", thor, tmin, tsec);
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

	/**
	 * 実行状態インジケーターを取得する。
	 * @return	JLabel
	 */
	protected RunningAnimationLabel getRunIndicator() {
		if (_runIndicator == null) {
			RunningAnimationLabel label = new RunningAnimationLabel();
			//label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			_runIndicator = label;
		}
		return _runIndicator;
	}

	/**
	 * 名前ラベルを取得する。
	 * @return	JLabel
	 */
	protected JLabel getNameLabel() {
		if (_lblName == null) {
			_lblName = createNameLabel();
		}
		return _lblName;
	}

	/**
	 * 実行時間ラベルを取得する。
	 * @return	JLabel
	 */
	protected JLabel getTimeLabel() {
		if (_lblTime == null) {
			_lblTime = createTimeLabel();
		}
		return _lblTime;
	}

	/**
	 * 停止ボタンを取得する。
	 * @return	JButton
	 */
	protected JButton getStopButton() {
		if (_btnStop == null) {
			JButton btn = CommonResources.createIconButton(CommonResources.ICON_EXEC_STOP,
								RunnerMessages.getInstance().ProcMonitorDlg_Button_Stop);
			btn.setActionCommand(CMD_STOP);
			btn.addActionListener(this);
			_btnStop = btn;
		}
		return _btnStop;
	}

	/**
	 * 強制終了ボタンを取得する。
	 * @return	JButton
	 */
	protected JButton getKillButton() {
		if (_btnKill == null) {
			JButton btn = CommonResources.createIconButton(CommonResources.ICON_DELETE,
								RunnerMessages.getInstance().ProcMonitorDlg_Button_Kill);
			btn.setActionCommand(CMD_KILL);
			btn.addActionListener(this);
			btn.setEnabled(false);
			_btnKill = btn;
		}
		return _btnKill;
	}

	/**
	 * クリップボードへコピーボタンを取得する。
	 * @return	JButton
	 */
	protected JButton getCopyToClipboardButton() {
		if (_btnCopyToClipboard == null) {
			JButton btn = CommonResources.createIconButton(CommonResources.ICON_COPY,
								RunnerMessages.getInstance().ProcMonitorDlg_Button_CopyToClip);
			btn.setActionCommand(CMD_COPYTOCLIP);
			btn.addActionListener(this);
			_btnCopyToClipboard = btn;
		}
		return _btnCopyToClipboard;
	}

	/**
	 * 引数ダイアログ表示ボタンを取得する。
	 * @return	JButton
	 */
	protected JButton getShowArgsButton() {
		if (_btnShowArgs == null) {
			JButton btn = new JButton(RunnerMessages.getInstance().ProcMonitorDlg_Button_ShowArgs);
			btn.setActionCommand(CMD_SHOWARGS);
			btn.addActionListener(this);
			_btnShowArgs = btn;
		}
		return _btnShowArgs;
	}

	/**
	 * 閉じるボタンを取得する。
	 * @return	JButton
	 */
	protected JButton getCloseButton() {
		if (_btnClose == null) {
			JButton btn = new JButton(CommonMessages.getInstance().Button_Close);
			btn.setActionCommand(CMD_CLOSE);
			btn.addActionListener(this);
			_btnClose = btn;
		}
		return _btnClose;
	}

	/**
	 * 自動的に閉じるチェックボックスを取得する。
	 * @return	JCheckBox
	 */
	protected JCheckBox getAutoCloseCheckBox() {
		if (_chkAutoClose == null) {
			JCheckBox chk = new JCheckBox(RunnerMessages.getInstance().ProcMonitorSetting_AutoCloseAfterExec, _initialAutoClose);
			_chkAutoClose = chk;
		}
		return _chkAutoClose;
	}

	/**
	 * コンソールテキストコンポーネントを取得する。
	 * @return
	 */
	protected BlockingConsoleTextPane getConsoleTextPane() {
		if (_console == null) {
			BlockingConsoleTextPane pane = new BlockingConsoleTextPane();
			pane.setLineWrap(true);			// 右端で折り返す
			pane.setLineLengthLimit(512);	// １行の文字数を 512 文字に制限
			_console = pane;
		}
		return _console;
	}

	//------------------------------------------------------------
	// Implement ComponentListener interfaces
	//------------------------------------------------------------

	@Override
	public void componentShown(ComponentEvent e) {
		if (_handler != null) {
			_handler.onShownMonitor(this);
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		if (_handler != null) {
			_handler.onHiddenMonitor(this);
		}
		
		// プロセスの実行が終了している場合、このモニタオブジェクトを破棄する
		if (_curExecutor == null) {
			if (isDisplayable()) {
				dispose();
			}
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		saveWindowLocationOnMoved(e);
//		if (this.getExtendedState() == JFrame.NORMAL) {
//			//--- save last normal frame location
//			try {
//				if (_lastFrameLocation != null) {
//					_lastFrameLocation.setLocation(this.getLocationOnScreen());
//				} else {
//					_lastFrameLocation = new Point(this.getLocationOnScreen());
//				}
//			} catch(IllegalComponentStateException icse) {
//				AppLogger.debug(icse);
//			}
//		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (this.getExtendedState() == JFrame.NORMAL) {
			//--- keep minimum size
			{
				Dimension dmMin = getMinimumSize();
				int cw = getSize().width;
				int ch = getSize().height;
				if (dmMin != null && (cw < dmMin.width || ch < dmMin.height)) {
					setSize((cw<dmMin.width)?dmMin.width:cw, (ch<dmMin.height)?dmMin.height:ch);
				}
			}
			//--- save last normal frame size
			saveWindowSizeOnResized(e);
		}
	}
	
	//------------------------------------------------------------
	// Implement WindowListener interfaces
	//------------------------------------------------------------

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {
		// save preferences
		storeSettings();
		
		// event
		if (!_shutdown && _handler != null) {
			_handler.onDisposedMonitor(this);
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		closeWindow();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {
//		// save window bounds
//		this._lastFrameSize.setSize(this.getSize());
//		try {
//			if (_lastFrameLocation != null) {
//				_lastFrameLocation.setLocation(this.getLocationOnScreen());
//			} else {
//				_lastFrameLocation = new Point(this.getLocationOnScreen());
//			}
//		} catch(IllegalComponentStateException icse) {
//			AppLogger.debug(icse);
//		}
		// adjust window bounds
		adjustWindowBoundsWhenOpened(e);
		
		// first focus
		_console.requestFocusInWindow();
	}
	
	//------------------------------------------------------------
	// Implement WindowStateListener interfaces
	//------------------------------------------------------------

	@Override
	public void windowStateChanged(WindowEvent e) {}
	
	//------------------------------------------------------------
	// Implement WindowFocusListener interfaces
	//------------------------------------------------------------

	@Override
	public void windowGainedFocus(WindowEvent e) {}

	@Override
	public void windowLostFocus(WindowEvent e) {}
	
	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
