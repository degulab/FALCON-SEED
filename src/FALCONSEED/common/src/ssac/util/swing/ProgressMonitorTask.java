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
 * @(#)ProgressMonitorTask.java	2.0.0	2012/11/01
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ProgressMonitorTask.java	1.17	2011/02/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ProgressMonitorTask.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ProgressMonitorTask.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ProgressMonitorTask.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import ssac.aadl.common.CommonResources;
import ssac.util.Strings;

/**
 * プログレスモニターダイアログ表示中に実行するタスク。
 * 
 * @version 2.0.0	2012/11/01
 * 
 * @since 1.14
 */
public abstract class ProgressMonitorTask implements Runnable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ダイアログのタイトル **/
	private final String	_title;
	/** ダイアログ内に表示する説明 **/
	private String			_desc;
	/** 進捗状況を表す文字列 **/
	private String			_note;

	/** 最小値 **/
	private int			_min;
	/** 最大値 **/
	private int			_max;
	/** 現在の値 **/
	private int			_value;

	/** 中断を可能とするフラグ **/
	private final boolean		_canCancel;

	/** 中断要求があったことを示すフラグ **/
	private boolean		_terminate;
	/** 最後に発生した例外 **/
	private Throwable		_cause;

	/** ダイアログの表示更新間隔(ミリ秒) **/
	private int			_refreshInterval;

	/** ダイアログが実行されていることを示すオブジェクト **/
	private ProgressMonitorDialog	_dialog;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ProgressMonitorTask(String title,
								String description,
								String note,
								int min,
								int max,
								int refreshInterval)
	{
		this(title, description, note, min, max, refreshInterval, true);
	}
	
	public ProgressMonitorTask(String title,
								String description,
								String note,
								int min,
								int max,
								int refreshInterval,
								boolean canCancel)
	{
		this._title = (Strings.isNullOrEmpty(title) ? null : title);
		this._desc  = (Strings.isNullOrEmpty(description) ? null : description);
		this._note  = (Strings.isNullOrEmpty(note) ? null : note);

		this._min   = min;
		this._max   = max;
		this._value = min;
		
		this._refreshInterval = refreshInterval;
		this._canCancel = canCancel;
	}

	//------------------------------------------------------------
	// Task interface
	//------------------------------------------------------------
	
	/**
	 * このタスクの処理をこのメソッド内に記述する。
	 * このメソッドは Swing のイベントディスパッチスレッドとは異なる
	 * スレッドで実行される。
	 * <p>
	 * この実装において、{@link #isTerminateRequested()} が <tt>true</tt> を
	 * 返す場合はこの処理をすみやかに中断するよう、{@link #isTerminateRequested()} の
	 * 戻り値を常に監視すること。
	 * <p>
	 * このメソッド内で例外が発生した場合、未処理の例外はこのオブジェクトに
	 * 保存される。保存された例外は {@link #getErrorCause()} メソッドで取得する。
	 */
	abstract public void processTask() throws Throwable;

	/**
	 * タスク処理が終了した後に実行する処理をこのメソッド内に記述する。
	 * {@link #processTask()} メソッドの実行が終了した後、エラーの状態に関係なく
	 * このメソッドが呼び出される。
	 * <p>なお、このメソッド内で発生した例外はすべて無視されるので、エラー処理
	 * などはこのメソッド内で独自に実装すること。
	 * @since 2.0.0
	 */
	public void cleanupTask() {}

	/**
	 * このメソッドは、タスクが実行されるスレッドから呼び出されるエントリポイント。
	 * このメソッドを直接呼び出してはならない。
	 */
	public final void run() {
		try {
			processTask();
		}
		catch (Throwable ex) {
			_cause = ex;
		}
		
		//--- cleanup
		try {
			cleanupTask();
		}
		catch (Throwable ignoreEx) {
			ignoreEx = null;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このタスクを実行する。
	 * 実行開始と同時に、指定されたコンポーネントの親ウィンドウを親とする
	 * プログレスバーを持つダイアログを表示する。
	 * 基本的にこのメソッドは制御を戻さない。タスクの処理が完了、もしくは
	 * 中断された場合に、このメソッドは制御を戻す。
	 * @return タスクが正常に終了した場合は <tt>true</tt> を返す。
	 * 			タスク内で例外がスローされた場合、もしくはユーザによって中断された場合は <tt>false</tt> を返す。
	 */
	public final boolean execute(Component parentComponent) {
		_terminate = false;
		_cause = null;
		Window owner = SwingTools.getWindowForComponent(parentComponent);
		if (owner instanceof Frame)
			_dialog = new ProgressMonitorDialog((Frame)owner, this);
		else
			_dialog = new ProgressMonitorDialog((Dialog)owner, this);
		_dialog.pack();
		_dialog.setLocationRelativeTo(owner);
		{
			// 待たずにダイアログを表示
			_dialog.startProgress();
			_dialog.setVisible(true);
		}
		_dialog.dispose();
		int dr = _dialog.getDialogResult();
		_dialog = null;
		return (getErrorCause() == null && dr == IDialogResult.DialogResult_OK);
	}
	
	public int getRefreshInterval() {
		return _refreshInterval;
	}
	
	public void setRefreshInterval(int interval) {
		if (interval < 0L)
			throw new IllegalArgumentException("Invalid Refresh interval : " + interval);
		_refreshInterval = interval;
	}
	
	public String getTitle() {
		return _title;
	}
	
	public String getDescription() {
		return _desc;
	}
	
	public String getNote() {
		return _note;
	}
	
	public int getMinimum() {
		return _min;
	}
	
	public int getMaximum() {
		return _max;
	}
	
	public int getValue() {
		return _value;
	}
	
	public void setDescription(String description) {
		_desc = description;
	}
	
	public void setNote(String note) {
		_note = note;
	}
	
	public void setMinimum(int min) {
		_min = min;
	}
	
	public void setMaximum(int max) {
		_max = max;
	}
	
	public void setValue(int value) {
		_value = value;
	}
	
	public void incrementValue() {
		setValue(_value += 1);
	}
	
	public boolean canCancel() {
		return _canCancel;
	}
	
	public boolean isTerminateRequested() {
		return _terminate;
	}
	
	public void requestTerminate() {
		_terminate = true;
	}
	
	public Throwable getErrorCause() {
		return _cause;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected int showConfirmDialog(Object message)
	{
		return showConfirmDialog(message, null, JOptionPane.YES_NO_CANCEL_OPTION);
	}
	
	protected int showConfirmDialog(Object message, String title, int optionType)
	{
		return showConfirmDialog(message, title, optionType, JOptionPane.QUESTION_MESSAGE);
	}
	
	protected int showConfirmDialog(Object message, String title,
									int optionType, int messageType)
	{
		return showConfirmDialog(message, title, optionType, messageType, null);
	}
	
	protected int showConfirmDialog(Object message, String title, int optionType,
									int messageType, Icon icon)
	{
		return showOptionDialog(message, title, optionType, messageType, icon, null, null);
	}

	/**
	 * 指定されたパラメータでオプションダイアログを表示する。
	 * このメソッドは、<code>EventDispatchThread</code> から呼び出さないでください。
	 * <p>
	 * オプションダイアログが閉じられるまで、このメソッドは処理スレッドに
	 * 制御を戻さない。なお、このダイアログの親コンポーネントは、
	 * プログレスモニターダイアログとなる。
	 * <p>
	 * <b>(注)</b>
	 * <blockquote>
	 * オプションダイアログの実行で例外がスローされた場合、その例外は無視され、
	 * {@link JOptionPane#CLOSED_OPTION} が返される。
	 * </blockquote>
	 * 
	 * @param message		表示する <code>Object</code>
	 * @param title			ダイアログのタイトル文字列を指定する。
	 * 						この引数が <tt>null</tt> の場合、<code>JOptionPane</code> 標準の
	 * 						タイトルが適用される。
	 * @param optionType	ダイアログで選択可能なオプションを示す整数
	 * @param messageType	主にプラグイン可能な Look & Feel のアイコンを指定するために
	 * 						使用されるメッセージの種類を表す整数。
	 * 						ERROR_MESSAGE、INFORMATION_MESSAGE、WARNING_MESSAGE、
	 * 						QUESTION_MESSAGE、または PLAIN_MESSAGE
	 * @param icon			ダイアログに表示するアイコン
	 * @param options		ユーザが選択可能な項目を示すオブジェクトの配列。
	 * 						オブジェクトがコンポーネントの場合は適切に描画される。
	 * 						String 以外のオブジェクトは toString メソッドを使用して
	 * 						描画される。このパラメータが <tt>null</tt> の場合、
	 * 						オプションは Look & Feel で決まる
	 * @param initialValue	ダイアログのデフォルト選択を示すオブジェクト。
	 * 						<em>options</em> が使用される場合にだけ意味を持つ。
	 * 						<tt>null<tt> の場合もある
	 * @return	ユーザが選択したオプションを示す整数。ユーザがダイアログを閉じた場合は CLOSED_OPTION
	 */
	protected int showOptionDialog(Object message, String title, int optionType,
									int messageType, Icon icon, Object[] options,
									Object initialValue)
	{
        if (EventQueue.isDispatchThread()) {
            throw new Error("Cannot call showConfirmMessage from the event dispatcher thread");
        }
		
        OptionDialogInvoker invoker
        = new OptionDialogInvoker(_dialog, message, title, optionType,
        						messageType, icon, options, initialValue);
        
        int ret;
        try {
        	SwingUtilities.invokeAndWait(invoker);
        	ret = invoker.getResult();
        } catch (Throwable ex) {
        	ex.printStackTrace();
        	ret = JOptionPane.CLOSED_OPTION;
        }
    	
    	return ret;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * オプションダイアログを表示するための invoker。
	 * 
	 * @version 1.14	2009/12/09
	 * @since 1.14
	 */
	static protected class OptionDialogInvoker implements Runnable
	{
		private final Dialog	_parentComponent;
		private final String	_title;
		private final int		_optionType;
		private final int		_messageType;
		private final Icon		_icon;
		private final Object	_message;
		private final Object[]	_options;
		private final Object	_initialValue;
		
		private int	_dialogResult;
		
		public OptionDialogInvoker(Dialog parentComponent,
									Object message, String title, int optionType,
									int messageType, Icon icon, Object[] options,
									Object initialValue)
		{
			this._parentComponent = parentComponent;
			this._title           = title;
			this._optionType      = optionType;
			this._messageType     = messageType;
			this._icon            = icon;
			this._message         = message;
			this._options         = options;
			this._initialValue    = initialValue;
		}
		
		public int getResult() {
			return _dialogResult;
		}

		public void run() {
			// setup Dialog title
			String dlgTitle;
			if (_title == null) {
				dlgTitle = UIManager.getString("OptionPane.titleText");
			} else {
				dlgTitle = _title;
			}
			
			// show Option dialog
			_dialogResult = JOptionPane.showOptionDialog(
											_parentComponent,_message, dlgTitle,
											_optionType,_messageType, _icon,
											_options, _initialValue);
		}
	}
	
	/**
	 * プログレスモニターダイアログ。
	 * 
	 * @version 1.17	2011/02/15
	 * 
	 * @since 1.14
	 */
	static protected class ProgressMonitorDialog extends BasicDialog
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------
		
		static private final int DEFAULT_UPDATE_INTERVAL = 100;

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		/** 進行状況を表示するプログレスバー **/
		protected JProgressBar	_progress;
		/** 説明表示用ラベルコンポーネント **/
		protected JTextPane	_lblDesc;
		/** 進行状況を監視するタイマー **/
		protected Timer		_timer;
		/** タスクスレッド **/
		protected Thread		_thread;

		/** 進行状況を監視するタスク **/
		protected final ProgressMonitorTask	_task;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		protected ProgressMonitorDialog(Frame owner, ProgressMonitorTask task) {
			super(owner, normalizeTitle(task.getTitle()), true);
			this._task = task;
			setupComponents();
		}
		
		protected ProgressMonitorDialog(Dialog owner, ProgressMonitorTask task) {
			super(owner, normalizeTitle(task.getTitle()), true);
			this._task = task;
			setupComponents();
		}
		
		protected void setupComponents() {
			if (_task.canCancel()) {
				setCancelButton();
			}
			
			setDescription(_task.getDescription());
			// setup dialog
			setResizable(false);
			//setResizable(true);	// for test
			setMinimumSize(new Dimension(320, 100));
			setKeepMinimumSize(true);
		}
		
		static protected String normalizeTitle(String title) {
			if (Strings.isNullOrEmpty(title)) {
				return ("Progress...");
			} else {
				return title;
			}
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------

		/**
		 * このダイアログに設定されている説明テキストを返す。
		 * 設定されていない場合は <tt>null</tt> を返す。
		 */
		protected String getDescription() {
			String desc = _lblDesc.getText();
			return (Strings.isNullOrEmpty(desc) ? null : desc);
		}

		/**
		 * このダイアログの説明テキストを設定する。
		 * @param description	表示する説明テキストを表す文字列。
		 * 						説明テキストを表示しない場合は <tt>null</tt>
		 */
		protected void setDescription(String description) {
			String oldDesc = _lblDesc.getText();
			String newDesc = (Strings.isNullOrEmpty(description) ? "" : description);
			if (!oldDesc.equals(newDesc)) {
				if (newDesc.length() > 0) {
					_lblDesc.setText(newDesc);
					_lblDesc.setVisible(true);
				} else {
					_lblDesc.setVisible(false);
					_lblDesc.setText("");
				}
			}
		}
		
		protected void startProgress() {
			if (_thread != null)
				throw new AssertionError("_thread is started!");
			
			// スレッド起動
			_thread = new Thread(_task, _task.getClass().getName());
			_thread.start();
			
			// 更新用タイマー起動
			if (_timer == null) {
				int interval = (_task.getRefreshInterval() >= 0L ? _task.getRefreshInterval() : DEFAULT_UPDATE_INTERVAL);
				_timer = new Timer(interval, new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						if (_timer != null && _timer.isRunning()) {
							onTimer();
						}
					}
				});
				_timer.setInitialDelay(interval);
				_timer.setCoalesce(true);
			}
			_timer.start();
		}
		
		protected void stopTimer() {
			if (_timer != null) {
				_timer.stop();
				_timer = null;
			}
		}
		
		protected JTextPane createDescriptionLabel() {
			JTextPane pane = new JTextPane();
			pane.setEditable(false);
			pane.setFocusable(false);
			pane.setForeground(UIManager.getColor("Label.foreground"));
			pane.setOpaque(false);
			
			SimpleAttributeSet attr = new SimpleAttributeSet();
			StyleConstants.setLineSpacing(attr, -0.2f);
			pane.setParagraphAttributes(attr, true);
			
			pane.setMargin(new Insets(CommonResources.DIALOG_CONTENT_MARGIN,
										CommonResources.DIALOG_CONTENT_MARGIN,
										CommonResources.DIALOG_CONTENT_MARGIN,
										CommonResources.DIALOG_CONTENT_MARGIN));
			
			pane.setText("description");
			return pane;
		}
		
		protected JProgressBar createProgressBar() {
			JProgressBar bar = new JProgressBar();
			bar.setBorderPainted(true);
			bar.setStringPainted(true);
			bar.setIndeterminate(true);
			return bar;
		}

		@Override
		protected void setupMainPanel() {
			super.setupMainPanel();
			
			// create components
			this._lblDesc  = createDescriptionLabel();
			this._progress = createProgressBar();
			
			// setup main panel layout
			mainPanel.setLayout(new GridBagLayout());
			mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.weighty = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			mainPanel.add(_progress, gbc);
			
			// setup message layout
			this.getContentPane().add(_lblDesc, BorderLayout.NORTH);
		}
		
		protected void setCancelButton() {
			Dimension dim = this.btnCancel.getPreferredSize();
			Box btnBox = Box.createHorizontalBox();
			btnBox.add(Box.createHorizontalGlue());
			btnBox.add(this.btnCancel);
			btnBox.add(Box.createHorizontalStrut(5));
			btnBox.add(Box.createRigidArea(new Dimension(0, dim.height+10)));
			
			// setup default button
			this.btnCancel.setDefaultCapable(true);
			this.getContentPane().add(btnBox, BorderLayout.SOUTH);
			
			// setup close action for [ESC] key
			AbstractAction act = new AbstractAction("Cancel"){
				public void actionPerformed(ActionEvent ae) {
					ActionListener[] listeners = btnCancel.getActionListeners();
					for (ActionListener l : listeners) {
						l.actionPerformed(ae);
					}
				}
			};
			InputMap imap = this.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel-by-esc");
			this.getRootPane().getActionMap().put("cancel-by-esc", act);
		}

		@Override
		protected void setupButtonPanel() {
			int maxWidth = 0;
			int maxHeight = 0;
			Dimension dim;
			this.btnCancel.setText(UIManager.getString("OptionPane.cancelButtonText"));
			dim = this.btnCancel.getPreferredSize();
			maxWidth = Math.max(maxWidth, dim.width);
			maxHeight = Math.max(maxHeight, dim.height);
			//---
			dim = new Dimension(maxWidth, maxHeight);
			//btnApply .setMinimumSize(dim);
			this.btnApply .setPreferredSize(dim);
			//btnOK    .setMinimumSize(dim);
			this.btnOK    .setPreferredSize(dim);
			//btnCancel.setMinimumSize(dim);
			this.btnCancel.setPreferredSize(dim);
		}

		@Override
		protected Dimension getDefaultSize() {
			return null;
		}

		@Override
		protected void initDialog() {
			// 基本処理
			super.initDialog();
			
			// プログレスバーの初期設定
			_progress.setMinimum(_task.getMinimum());
			_progress.setMaximum(_task.getMaximum());
			_progress.setValue(_task.getValue());
			boolean indeterminate = (_task.getMinimum() == _task.getMaximum());
			_progress.setIndeterminate(indeterminate);
			_progress.setStringPainted(!indeterminate);
			_progress.setString(_task.getNote());
			
			// 処理の開始
			//startProgress();
		}

		@Override
		protected void dialogClose(int result) {
			// タイマー停止
			stopTimer();
			
			// スレッドを破棄
			_thread = null;
			
			// 基本処理
			super.dialogClose(result);
		}

		@Override
		protected boolean onButtonCancel() {
			// 中断要求フラグを設定
			_task.requestTerminate();
			
			// キャンセルボタンを無効にする
			btnCancel.setEnabled(false);
			
			// プログレスバーの表示を不確定モードにする
			_progress.setIndeterminate(true);
			
			// ダイアログは閉じない
			return false;
		}

		/**
		 * プログレスバーの表示を更新する。
		 */
		protected void refreshProgress() {
			// 状態を更新
			//--- 処理がキャンセルされていない場合のみ
			if (isVisible() && !_task.isTerminateRequested()) {
				//--- 説明
				setDescription(_task.getDescription());
				//--- 進捗
				_progress.setString(_task.getNote());
				//--- バー
				int min = _task.getMinimum();
				int max = _task.getMaximum();
				int val = _task.getValue();
				boolean indet = (min == max);
				//--- min
				if (_progress.getMinimum() != min)
					_progress.setMinimum(min);
				//--- max
				if (_progress.getMaximum() != max)
					_progress.setMaximum(max);
				//--- val
				if (_progress.getValue() != val)
					_progress.setValue(val);
				//--- indeterminate
				if (_progress.isIndeterminate() != indet) {
					_progress.setIndeterminate(indet);
					_progress.setStringPainted(!indet);
				}
			}
			
		}

		/**
		 * スレッドが実行中で無ければ、スレッドを破棄
		 */
		protected void watchThread() {
			// スレッドの状態を確認
			if (_thread != null && !_thread.isAlive()) {
				// スレッドは終了
				//--- 念のため、終了を確認
				try {
					_thread.join();
				} catch (InterruptedException ignoreEx) { ignoreEx=null; }
				//--- スレッドを破棄
				_thread = null;
				
				// タイマー停止
				stopTimer();
				
				// ダイアログを閉じる
				/*==================================================================**
				 * Mac の Java では、java.swing.Timer のアクションリスナーの中で 
				 * ダイアログを閉じるメソッドを実行すると、なぜかスレッドが終了
				 * しない場合がある。たぶん、タイマースレッドかイベントディスパッチ
				 * スレッドにタイマーイベントが残っているか何かだと思うが、原因不明。
				 * そのため、SwingUtilities.invokeLater() を使用。
				/*==================================================================*/
				SwingUtilities.invokeLater(new Runnable(){
					public void run() {
						if (_task.isTerminateRequested()) {
							// 中断
							dialogClose(IDialogResult.DialogResult_Cancel);
						} else {
							// 完了
							dialogClose(IDialogResult.DialogResult_OK);
						}
					}
				});
			}
		}
		
		protected void onTimer() {
			// 表示を更新
			refreshProgress();
			
			// スレッドの状態を確認
			watchThread();
		}
	}
}
