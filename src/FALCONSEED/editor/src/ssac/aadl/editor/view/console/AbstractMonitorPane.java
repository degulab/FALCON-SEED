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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AbstractMonitorPane.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbstractMonitorPane.java	1.10	2008/12/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbstractMonitorPane.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.build.ExecutorEvent;
import ssac.aadl.editor.build.ExecutorStoppedListener;
import ssac.aadl.editor.view.menu.EditorMenuResources;
import ssac.aadl.editor.view.text.ITextComponent;
import ssac.util.logging.AppLogger;
import ssac.util.process.CommandExecutor;
import ssac.util.process.OutputString;
import ssac.util.swing.RunningAnimationLabel;
import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.IMenuActionHandler;

/**
 * 情報ビューの各パネル共通の機能を提供する抽象クラス。
 * 
 * @version 1.14	2009/12/09
 */
public abstract class AbstractMonitorPane extends JPanel
implements ITextComponent, IMenuActionHandler
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static protected final int INSET_LR = 2;
	static protected final int INSET_TB = 0;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final Timer	animTimer;
	protected final ConsolePane	console;
	protected final JScrollPane	scroll;
	protected final RunningAnimationLabel	lblRunning;
	protected final JLabel	lblTarget;
	protected final JLabel	lblTime;
	protected final JButton	btnSetting;
	protected final JButton	btnRun;
	protected final JButton	btnCancel;
	protected final JButton	btnClear;
	
	protected CommandExecutor	executor;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbstractMonitorPane() {
		super(new BorderLayout());
		
		// create components
		this.animTimer = createTimer();
		this.console = new ConsolePane();
		this.scroll = new JScrollPane(this.console,
										JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.lblRunning = new RunningAnimationLabel();
		this.lblTarget = createEtchedLabel();
		this.lblTime = createTimeLabel();
		this.btnSetting = createSettingButton();
		this.btnRun = createRunButton();
		this.btnCancel = createCancelButton();
		this.btnClear = createClearButton();
		
		// setup components
		this.add(this.scroll, BorderLayout.CENTER);
		JPanel pnl = createControlPanel();
		if (pnl != null) {
			this.add(pnl, BorderLayout.NORTH);
		}
		
		// Focus border
		/*
		FocusedMatteBorder border = new FocusedMatteBorder(2,2,2,2,Color.GRAY,Color.BLUE);
		setBorder(border);
		this.console.addFocusListener(border);
		*/
		
		// update display
		updateProcessingTime(0L);
		updateButtons();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * プロセスの設定ダイアログが表示可能であるかを検証する。
	 * @return 設定ダイアログ表示可能であれば <tt>true</tt>
	 */
	public boolean canSetting() {
		return false;
	}

	/**
	 * プロセスが実行可能かを検証する。
	 * 
	 * @return 実行可能であれば true
	 */
	public boolean canRun() {
		return false;
	}

	/**
	 * プロセスが実行中かを検証する。
	 * 
	 * @return 実行中のプロセスがあれば true
	 */
	public boolean isRunning() {
		return (this.executor != null ? this.executor.isRunning() : false);
	}

	/**
	 * メッセージ表示領域をクリアする
	 */
	public void clear() {
		this.console.setText("");
		this.updateProcessingTime(0L);
	}
	
	/**
	 * プロセスの実行開始
	 */
	public void start() {
		// place holder
	}

	/**
	 * プロセスの実行停止
	 *
	 */
	public void stop() {
		// タイマー停止
		this.lblRunning.setRunning(false);
		if (this.animTimer.isRunning()) {
			this.animTimer.stop();
		}

		// 実行停止
		if (this.executor != null) {
			// 実行完了
			this.executor.destroy();
			/*
			if (this.executor.isRunning()) {
				//--- 実行中断
				this.executor.destroy();
			}
			*/
			// 表示更新
			updateProcessingTime(this.executor.getProcessingTimeMillis());
			showQueueingOutput();
			this.btnRun.setEnabled(true);
			this.btnCancel.setEnabled(false);
			//--- event
			fireExecutorStopped(this.executor);
			//--- executor 破棄
			this.executor = null;
		}
		
		// 表示更新
		updateButtons();
	}

	/**
	 * プロセス終了アクションを登録する。
	 * 
	 * @param l Executorアクション
	 */
	public void addExecutorStoppedListener(ExecutorStoppedListener l) {
		listenerList.add(ExecutorStoppedListener.class, l);
	}

	/**
	 * 指定のプロセス終了アクションを削除する。
	 * 
	 * @param l Executorアクション
	 */
	public void removeExecutorStoppedListener(ExecutorStoppedListener l) {
		listenerList.remove(ExecutorStoppedListener.class, l);
	}

	/**
	 * 登録されているプロセス終了アクションのリストを取得する。
	 * 
	 * @return 登録されている全Executorアクションの配列
	 */
	public ExecutorStoppedListener[] getExecutorStoppedListeners() {
		return listenerList.getListeners(ExecutorStoppedListener.class);
	}

	/**
	 * プロセス終了アクションを開始する。
	 * 
	 * @param executor 対象の CommandExecutor
	 */
	public void fireExecutorStopped(CommandExecutor executor) {
		ExecutorEvent event = new ExecutorEvent(this, executor);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==ExecutorStoppedListener.class) {
				((ExecutorStoppedListener)listeners[i+1]).executionStopped(event);
			}
		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	protected void onTimer(ActionEvent ae) {
		if (this.executor == null) {
			// 処理対象が存在しないので、停止
			stop();
			return;
		}
		
		if (!isRunning()) {
			// 実行終了のため、停止
			stop();
			return;
		}
		
		// 出力更新
		this.lblRunning.next();
		updateProcessingTime(this.executor.getProcessingTimeMillis());
		showQueueingOutput();
	}
	
	protected void onButtonSetting() {
		// place holder
	}
	
	protected void onButtonRun() {
		// 実行開始
		start();
	}
	
	protected void onButtonCancel() {
		// 実行停止
		stop();
	}
	
	protected void onButtonClear() {
		// 表示内容のクリア
		clear();
	}

	//------------------------------------------------------------
	// Update displays
	//------------------------------------------------------------

	// キュー内の出力文字列をコンソールに出力
	protected void showQueueingOutput() {
		if (this.executor != null) {
			OutputString ostr;
			while ((ostr = this.executor.getCommandOutput().pop()) != null) {
				if (ostr.isError()) {
					//--- stderr
					this.console.appendErrorString(ostr.getString());
				} else {
					//--- stdout
					this.console.appendOutputString(ostr.getString());
				}
			}
		}
	}

	// ターゲット内容の表示更新
	protected void updateTarget() {
		//--- place holder
	}

	// 実行時間の表示更新
	protected void updateProcessingTime(long time) {
		if (this.lblTime != null) {
			this.lblTime.setText(getTimeText(time));
		}
	}
	
	// ボタンの表示更新
	protected void updateButtons() {
		boolean enableSetting = canSetting();
		boolean enableRun = false;
		boolean enableCancel = false;
		if (canRun()) {
			if (isRunning()) {
				enableSetting = false;
				enableRun = false;
				enableCancel = true;
			} else {
				//enableSetting = canSetting();
				enableRun = true;
				enableCancel = false;
			}
		}
		if (btnSetting != null) {
			btnSetting.setEnabled(enableSetting);
		}
		if (btnRun != null) {
			btnRun.setEnabled(enableRun);
		}
		if (btnCancel != null) {
			btnCancel.setEnabled(enableCancel);
		}
	}

	//------------------------------------------------------------
	// implement ITextComponent interfaces
	//------------------------------------------------------------

	/**
	 * テキストコンポーネントの短いタイトルを取得する。
	 * 
	 * @return 短いタイトル
	 */
	public String getShortDescription() {
		return lblTarget.getText();
	}

	/**
	 * テキストコンポーネントの長いタイトルを取得する。
	 * 
	 * @return 長いタイトル
	 */
	public String getLongDescription() {
		return lblTarget.getToolTipText();
	}

	/**
	 * テキストコンポーネントのドキュメントが変更されているかを取得する。
	 * 
	 * @return 変更されていれば true
	 */
	public boolean isModified() {
		return false;
	}

	/**
	 * テキストコンポーネントが編集可能であるかを検証する。
	 * 
	 * @return 編集可能であれば true
	 */
	public boolean canEdit() {
		return (console.isEditable() && console.isEnabled());
	}

	/**
	 * テキストコンポーネントで選択されているテキストが
	 * 存在するかを検証する。
	 * 
	 * @return 選択テキストが存在していれば true
	 */
	public boolean hasSelectedText() {
		return (console.getSelectionStart() != console.getSelectionEnd());
	}

	/**
	 * テキストコンポーネントが Undo 可能かを検証する。
	 * 
	 * @return Undo 可能なら true
	 */
	public boolean canUndo() {
		return false;
	}

	/**
	 * テキストコンポーネントが Redo 可能かを検証する。
	 * 
	 * @return Redo 可能なら true
	 */
	public boolean canRedo() {
		return false;
	}

	/**
	 * テキストコンポーネントが Cut 可能かを検証する。
	 * 
	 * @return Cut 可能なら true
	 */
	public boolean canCut() {
		return false;
	}

	/**
	 * クリップボードにペースト可能なデータが存在し、
	 * テキストコンポーネントにペースト可能な状態かを検証する。
	 * 
	 * @return ペースト可能なら true
	 */
	public boolean canPaste() {
		return false;
	}

	/**
	 * テキストコンポーネントを取得する。
	 * 
	 * @return JTextComponent インスタンス
	 */
	public JTextComponent getTextComponent() {
		return console;
	}

	/**
	 * テキストコンポーネントに対して Undo する。
	 */
	public void undo() {}

	/**
	 * テキストコンポーネントに対して Redo する。
	 */
	public void redo() {}

	/**
	 * 選択テキストのカット
	 */
	public void cut() {}

	/**
	 * 選択テキストのコピー
	 */
	public void copy() {
		console.requestFocusInWindow();
		console.copy();
	}

	/**
	 * 現在のキャレット位置にペースト
	 */
	public void paste() {}

	/**
	 * 選択テキストのデリート
	 */
	public void delete() {}

	/**
	 * テキストコンポーネントの全てのテキストを選択する。
	 */
	public void selectAll() {
		console.requestFocusInWindow();
		console.selectAll();
	}

	/**
	 * テキストコンポーネントの行数を取得する。
	 * 
	 * @return テキストコンポーネントの行数
	 */
	public int getLineCount() {
		return console.getLineCount();
	}

	/**
	 * キャレットをドキュメントの先頭に移動する。
	 */
	public void jumpToBegin() {
		console.requestFocusInWindow();
		SwingTools.setCaretToBegin(console);
	}

	/**
	 * キャレットをドキュメントの終端に移動する。
	 *
	 */
	public void jumpToEnd() {
		console.requestFocusInWindow();
		SwingTools.setCaretToEnd(console);
	}

	/**
	 * キャレットを指定行の先頭に移動する。
	 * 行番号は、先頭行を 1 とする。
	 * 
	 * @param lineNo 1 から始まる行番号
	 */
	public void jumpToLine(int lineNo) {
		console.requestFocusInWindow();
		SwingTools.setCaretToLine(console, lineNo);
	}

	/**
	 * 所属するテキストコンポーネントにフォーカスを設定する。
	 *
	 */
	public void requestFocusInTextComponent() {
		console.setFocus();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void startProcess(CommandExecutor newExecutor) throws IOException {
		// check running
		if (isRunning()) {
			throw new RuntimeException("Already running executor!");
		}
		
		// execution start
		this.executor = newExecutor;
		this.executor.start();
		
		// start timer
		this.lblRunning.setRunning(true);
		this.animTimer.start();
		updateButtons();
	}
	
	// タイマーオブジェクトの生成
	protected Timer createTimer() {
		Timer tm = new Timer(200, new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onTimer(ae);
			}
		});
		return tm;
	}
	
	// 時間ラベルの生成
	protected JLabel createTimeLabel() {
		JLabel lbl = createEtchedLabel();
		lbl.setText("00000'00\"000");
		lbl.setHorizontalAlignment(JLabel.CENTER);
		Dimension dm = lbl.getPreferredSize();
		lbl.setPreferredSize(dm);
		lbl.setMinimumSize(dm);
		return lbl;
	}
	
	// [Setting]ボタン生成
	protected JButton createSettingButton() {
		JButton btn = CommonResources.createIconButton(CommonResources.ICON_SETTING, EditorMessages.getInstance().consoleTooltipSetting);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonSetting();
			}
		});
		return btn;
	}
	
	// [Run]ボタン生成
	protected JButton createRunButton() {
		//JButton btn = new JButton(AppMessages.getInstance().Button_Run);
		JButton btn = CommonResources.createIconButton(CommonResources.ICON_EXEC_START, EditorMessages.getInstance().consoleTooltipRun);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonRun();
			}
		});
		return btn;
	}
	
	// [Cancel]ボタン生成
	protected JButton createCancelButton() {
		//JButton btn = new JButton(AppMessages.getInstance().Button_Cancel);
		JButton btn = CommonResources.createIconButton(CommonResources.ICON_EXEC_STOP, EditorMessages.getInstance().consoleTooltipStop);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonCancel();
			}
		});
		return btn;
	}
	
	// [Clear]ボタン生成
	protected JButton createClearButton() {
		//JButton btn = new JButton(AppMessages.getInstance().Button_Clear);
		JButton btn = CommonResources.createIconButton(CommonResources.ICON_CONSOLE_CLEAR, EditorMessages.getInstance().consoleTooltipClear);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonClear();
			}
		});
		return btn;
	}
	
	// 制御バーの生成
	private JPanel createControlPanel() {
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
		pnl.add(lblRunning, gbc);
		gbc.gridx++;
		gbc.insets = new Insets(0, INSET_LR, 0, 0);
		//--- target
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(lblTarget, gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx++;
		//--- time
		if (lblTime != null) {
			pnl.add(lblTime, gbc);
			gbc.gridx++;
		}
		//--- [Setting] button
		if (btnSetting != null) {
			pnl.add(btnSetting, gbc);
			gbc.gridx++;
		}
		//--- [Run] button
		if (btnRun != null) {
			pnl.add(btnRun, gbc);
			gbc.gridx++;
		}
		//--- [Cancel] button
		if (btnCancel != null) {
			pnl.add(btnCancel, gbc);
			gbc.gridx++;
		}
		//--- [Clear] button
		if (btnClear != null) {
			pnl.add(btnClear, gbc);
			gbc.gridx++;
		}
		
		// 完了
		return pnl;
	}
	
	protected String getTimeText(long time) {
		long tmin, tsec, tmilli;
		if (time > 0L) {
			tmin = time / 60000L;
			time -= (tmin * 60000L);
			tsec = time / 1000L;
			tmilli = time - tsec * 1000L;
		} else {
			tmin = 0L;
			tsec = 0L;
			tmilli = 0L;
		}
		return String.format("%d'%02d\"%03d", tmin, tsec, tmilli);
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

	//------------------------------------------------------------
	// Menu events
	//------------------------------------------------------------

	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		if (EditorMenuResources.ID_EDIT_COPY.equals(command)) {
			onMenuSelectedEditCopy(action);
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_SELECTALL.equals(command)) {
			onMenuSelectedEditSelectAll(action);
			return true;
		}
		
		// no process
		return false;
	}

	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (EditorMenuResources.ID_EDIT_COPY.equals(command)) {
			action.setEnabled(console.canCopy());
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_SELECTALL.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		
		// no process
		return false;
	}
	
	// Menu : [Edit]-[Copy]
	protected void onMenuSelectedEditCopy(Action action) {
		AppLogger.debug("AbstractMonitorPane : [Edit]-[Copy] menu selection.");
		copy();
		//requestFocusInTextComponent();
	}
	
	// Menu : [Edit]-[Select all]
	protected void onMenuSelectedEditSelectAll(Action action) {
		AppLogger.debug("AbstractMonitorPane : [Edit]-[SelectAll] menu selection.");
		selectAll();
		//requestFocusInTextComponent();
	}
}
