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
 * @(#)MExecHistoryToolView.java	3.0.0	2014/03/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.module.swing.table.MExecRunningTableModel;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.RunnerResources;
import ssac.falconseed.runner.menu.RunnerMenuBar;
import ssac.falconseed.runner.menu.RunnerMenuResources;
import ssac.falconseed.runner.view.dialog.AsyncProcessMonitorWindow;
import ssac.falconseed.runner.view.dialog.IAsyncProcessMonitorHandler;
import ssac.util.swing.SwingTools;
import ssac.util.swing.table.SpreadSheetColumnHeader;
import ssac.util.swing.table.SpreadSheetRowHeader;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * フィルタ実行中リストのビュー
 * 
 * @version 3.0.0	2014/03/28
 * @since 3.0.0
 */
public class MExecRunningToolView  extends JPanel implements IRunnerToolView, IAsyncProcessMonitorHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 2878560629273448501L;
	
	/** タイマー実行間隔(ミリ秒) **/
	static protected final int DEFAULT_TIMER_INTERVAL	= 200;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 実行中モジュールを保持するテーブルモデル **/
	private MExecRunningTableModel	_model;
	/** テーブルコンポーネント **/
	private MExecRunningTable		_cTable;
	/** 更新タイマー **/
	private Timer					_updateTimer;

	private JButton					_btnStopAll;
	private JButton					_btnStopSelected;
	private JCheckBox				_chkConsoleAutoClose;
	private JCheckBox				_chkConsoleShowAtStart;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	public MExecRunningToolView(JCheckBox chkConsoleShowAtStart, JCheckBox chkConsoleAutoClose) {
		super(new BorderLayout());
		_model = new MExecRunningTableModel();
		this._chkConsoleAutoClose  = chkConsoleAutoClose;
		this._chkConsoleShowAtStart= chkConsoleShowAtStart;
	}
	
	public void initialComponent() {
		// create components
		_btnStopAll = CommonResources.createIconButton(CommonResources.ICON_EXEC_STOPALL, RunnerMessages.getInstance().ProcMonitorDlg_Button_StopAll);
		//_btnStopAll.setText(RunnerMessages.getInstance().ProcMonitorDlg_Button_StopAll);
		_btnStopAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickStopAllButton(e);
			}
		});
		_btnStopAll.setEnabled(false);
		
		_btnStopSelected = CommonResources.createIconButton(CommonResources.ICON_EXEC_STOP, RunnerMessages.getInstance().ProcMonitorDlg_Button_StopSelected);
		_btnStopSelected.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickStopSelectionButton(e);
			}
		});
		_btnStopSelected.setEnabled(false);
		
		this._cTable = new MExecRunningTable();
		this._cTable.setModel(_model);
		this._cTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);	// 不連続複数選択可能
		this._cTable.setRowSelectionAllowed(true);		// 行選択を許可
		this._cTable.setColumnSelectionAllowed(false);	// 列選択は許可しない
		SpreadSheetRowHeader rowHeader = _cTable.getTableRowHeader();
		rowHeader.setFixedCellHeight(_cTable.getRowHeight());
		
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
		_chkConsoleShowAtStart.setMargin(new Insets(0,0,0,0));
		_chkConsoleAutoClose  .setMargin(new Insets(0,0,0,0));
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
	 * 実行モニタを追加する。
	 * @param monitor	追加するモニタ
	 * @return	追加された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean addRunningMonitor(final AsyncProcessMonitorWindow monitor) {
		_model.addItem(monitor);
		int targetRowIndex = _model.getRowCount()-1;
		_cTable.setRowSelectionInterval(targetRowIndex, targetRowIndex);
		_cTable.scrollToVisibleCell(targetRowIndex, MExecRunningTableModel.COL_STATUS);
		//--- 更新ハンドラ
		monitor.setMonitorHandler(this);
		startTimer();
		return true;
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
	
//	public boolean canRunLatest() {
//		if (getFrame().isVisibleMExecArgsEditDialog()) {
//			return false;	// 引数設定ダイアログ表示中は、無効
//		}
//		return (_cTable.getRowCount() > 0);
//	}
//	
//	public boolean canRunFrom() {
//		if (getFrame().isVisibleMExecArgsEditDialog()) {
//			return false;	// 引数設定ダイアログ表示中は、無効
//		}
//		return !isSelectionEmpty();
//	}
//	
//	public boolean canRunBefore() {
//		if (getFrame().isVisibleMExecArgsEditDialog()) {
//			return false;	// 引数設定ダイアログ表示中は、無効
//		}
//		int minRowIndex = getMinSelectedRow();
//		return (minRowIndex > 0);
//	}
//	
//	public boolean canRunSelected() {
//		if (getFrame().isVisibleMExecArgsEditDialog()) {
//			return false;	// 引数設定ダイアログ表示中は、無効
//		}
//		return !isSelectionEmpty();
//	}
	
	public boolean canStopSelection() {
		// 選択が一つ以上
		return (!isSelectionEmpty());
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
		return false;
	}

	/**
	 * 選択されているオブジェクトを削除する
	 */
	public void doDelete() {
		// No entry
	}
	
	public void doSelectAll() {
		_cTable.selectAll();
	}

	/**
	 * すべてのモジュールの実行を停止する。
	 * すべてが停止中のモジュールの場合、強制終了するかどうかを問い合わせる。
	 * @return	操作が実行された場合は <tt>true</tt>、停止が実行されたなかったもしくはユーザーによって拒否された場合は <tt>false</tt>
	 */
	public boolean doStopAll() {
		if (_model.isEmpty())
			return false;	// モニタが存在しない

		boolean modified = false;
		boolean hasArrowKill = false;
		for (int row = 0; row < _model.getRowCount(); ++row) {
			AsyncProcessMonitorWindow monitor = _model.getItem(row);
			if (monitor.isProcessRunning()) {
				// 未停止で実行中
				monitor.doTerminate();
				_model.fireTableRowsUpdated(row, row);
				modified = true;
			}
			else if (monitor.canKillAfterTermRequested()) {
				hasArrowKill = true;
			}
		}
		
		if (!modified && hasArrowKill) {
			// 問い合わせ
			int ret = JOptionPane.showConfirmDialog(this, RunnerMessages.getInstance().confirmKillAllTerminatingFilters,
													CommonMessages.getInstance().msgboxTitleWarn, JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION) {
				return false;	// operation canceled
			}
			// 強制終了
			for (int row = 0; row < _model.getRowCount(); ++row) {
				AsyncProcessMonitorWindow monitor = _model.getItem(row);
				if (monitor.canKillAfterTermRequested()) {
					monitor.doKill();
					_model.fireTableRowsUpdated(row, row);
					modified = true;
				}
			}
		}
		
		return modified;
	}

	/**
	 * 選択されているモジュールの実行を停止する。
	 * すべてが停止中のモジュールの場合、強制終了するかどうかを問い合わせる。
	 * @return	操作が実行された場合は <tt>true</tt>、停止が実行されたなかったもしくはユーザーによって拒否された場合は <tt>false</tt>
	 */
	public boolean doStopSelected() {
		if (isSelectionEmpty())
			return false;	// 選択されたモニタが存在しない
		
		int rows[] = _cTable.getSelectedRows();
		if (rows.length <= 0)
			return false;	// no selection

		boolean modified = false;
		boolean hasArrowKill = false;
		for (int row : rows) {
			AsyncProcessMonitorWindow monitor = _model.getItem(row);
			if (monitor.isProcessRunning()) {
				// 未停止で実行中
				monitor.doTerminate();
				_model.fireTableRowsUpdated(row, row);
				modified = true;
			}
			else if (monitor.canKillAfterTermRequested()) {
				hasArrowKill = true;
			}
		}
		
		if (!modified && hasArrowKill) {
			// 問い合わせ
			int ret = JOptionPane.showConfirmDialog(this, RunnerMessages.getInstance().confirmKillSelectedTerminatingFilters,
													CommonMessages.getInstance().msgboxTitleWarn, JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION) {
				return false;	// operation canceled
			}
			// 強制終了
			for (int row : rows) {
				AsyncProcessMonitorWindow monitor = _model.getItem(row);
				if (monitor.canKillAfterTermRequested()) {
					monitor.doKill();
					_model.fireTableRowsUpdated(row, row);
					modified = true;
				}
			}
		}
		
		return modified;
	}

	/**
	 * 実行状態管理中のオブジェクトをシャットダウンする。
	 * 実行中のものがあれば、強制終了するか問い合わせる。
	 * すでに実行が終了しているものは、そのまま破棄する。
	 * @param parentComponent	メッセージの親となるコンポーネント
	 * @return	すべてシャットダウンされた場合は <tt>true</tt>、ユーザーによりキャンセルされた場合は <tt>false</tt>
	 */
	public boolean shutdown(Component parentComponent) {
		boolean doShutdown = false;
		for (int row = _model.getRowCount() - 1; row >= 0; --row) {
			AsyncProcessMonitorWindow monitor = _model.getItem(row);
			if (!doShutdown) {
				if (monitor.isProcessRunning() || monitor.isProcessTerminating()) {
					int ret = JOptionPane.showConfirmDialog(parentComponent,
								RunnerMessages.getInstance().confirmKillAllFilters,
								CommonMessages.getInstance().msgboxTitleWarn,
								JOptionPane.OK_CANCEL_OPTION);
					if (ret != JOptionPane.OK_OPTION) {
						// user canceled
						return false;
					}
					doShutdown = true;
				}
				//--- shutdown
				monitor.shutdown();
				_model.removeRow(row);
			}
			else {
				monitor.shutdown();
				_model.removeRow(row);
			}
		}
		return true;
	}

	//------------------------------------------------------------
	// Implement ssac.falconseed.runner.view.dialog.IAsyncProcessMonitorHandler interfaces
	//------------------------------------------------------------

	@Override
	public void onDisposedMonitor(AsyncProcessMonitorWindow monitor) {
		// 破棄されたものをリストから除外し、結果ファイルを開く
		if (monitor != null) {
			_model.removeItem(monitor);
			if (_model.getRowCount() == 0) {
				stopTimer();
			}
			getFrame().openResultFilesByModuleResults(monitor.getModuleData());
		}
	}

	@Override
	public void onHiddenMonitor(AsyncProcessMonitorWindow monitor) {
		// 何もしない
	}

	@Override
	public void onShownMonitor(AsyncProcessMonitorWindow monitor) {
		// 何もしない
	}

	@Override
	public void setStatusBarMessage(String message) {
		// 何もしない
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void setupActions() {
		// Table
		ListSelectionListener tableSelListener = new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				onTableSelectionChanged(e);
			}
		};
		_cTable.getSelectionModel().addListSelectionListener(tableSelListener);
		
		_model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				onTableModelChanged(e);
			}
		});
	}

	/**
	 * タイマーの生成
	 */
	protected Timer createTimer() {
		Timer tm = new Timer(DEFAULT_TIMER_INTERVAL, new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onTimer();
			}
		});
		return tm;
	}

	/**
	 * タイマーの開始。
	 */
	protected void startTimer() {
		if (_updateTimer == null) {
			_updateTimer = createTimer();
		}
		_updateTimer.start();
	}

	/**
	 * タイマーの停止。
	 */
	protected void stopTimer() {
		if(_updateTimer != null && _updateTimer.isRunning()) {
			_updateTimer.stop();
		}
		_updateTimer = null;
	}

	/**
	 * タイマーによる更新処理
	 */
	protected void onTimer() {
		int len = _model.getRowCount();
		if (len > 0) {
			_model.fireTableRowsUpdated(0, len-1);
		}
	}
	
	private JToolBar createToolBar() {
		JToolBar bar = new JToolBar(JToolBar.HORIZONTAL);
		bar.setFloatable(false);
		
		//--- stop all
		bar.add(_btnStopAll);
		
		//---
		bar.add(Box.createHorizontalStrut(20));
		
		//--- stop selected
		bar.add(_btnStopSelected);
		
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

	//------------------------------------------------------------
	// Menu events
	//------------------------------------------------------------

	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		if (RunnerMenuResources.ID_EDIT_SELECTALL.equals(command)) {
			doSelectAll();
			requestFocusInComponent();
			return true;
		}
		
		// no process
		return false;
	}

	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (RunnerMenuResources.ID_EDIT_SELECTALL.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		
		// no process
		return false;
	}

	//------------------------------------------------------------
	// Action events
	//------------------------------------------------------------
	
	protected void onTableModelChanged(TableModelEvent tme) {
		_btnStopAll.setEnabled(!_model.isEmpty());
	}
	
	protected void onTableSelectionChanged(ListSelectionEvent e) {
		_btnStopSelected.setEnabled(!isSelectionEmpty());
	}
	
	protected void onClickStopAllButton(ActionEvent ae) {
		doStopAll();
	}
	
	protected void onClickStopSelectionButton(ActionEvent ae) {
		doStopSelected();
	}
	
	protected void onClickSelectAllButton(ActionEvent ae) {
		doSelectAll();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class ControlButtonsPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		protected final JButton _btnStop	= CommonResources.createIconButton(CommonResources.ICON_EXEC_STOP, RunnerMessages.getInstance().ProcMonitorDlg_Button_Stop);
		protected final JButton _btnShow	= CommonResources.createIconButton(CommonResources.ICON_TEXT_WINDOW, RunnerMessages.getInstance().ProcMonitorDlg_Button_ShowConsole);
		
		public ControlButtonsPanel() {
			super();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setOpaque(true);
			setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));
			
			_btnStop.setFocusable(false);
			_btnStop.setRolloverEnabled(false);
			_btnStop.setVisible(true);
			_btnStop.setEnabled(true);
			
			_btnShow.setFocusable(false);
			_btnShow.setRolloverEnabled(false);
			_btnShow.setVisible(true);
			_btnShow.setEnabled(true);
			
			add(_btnStop);
			add(Box.createHorizontalStrut(5));
			add(_btnShow);
		}
	}
	
	static protected class ControlButtonsRenderer extends ControlButtonsPanel implements TableCellRenderer
	{
		private static final long serialVersionUID = 1L;

		public ControlButtonsRenderer() {
			super();
			setName("Table.cellRenderer");
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			this.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

			// ボタンの状態を設定
			if (row >= 0 && column==MExecRunningTableModel.COL_CONTROL) {
				TableModel model = table.getModel();
				if (model instanceof MExecRunningTableModel) {
					AsyncProcessMonitorWindow wnd = ((MExecRunningTableModel)model).getItem(row);
					if (wnd.isProcessRunning() || wnd.isProcessTerminating()) {
						//--- running or terminating
						_btnStop.setEnabled(true);
					}
					else {
						//--- finished, terminated, killed
						_btnStop.setEnabled(false);
					}
				}
				else {
					_btnStop.setEnabled(false);
				}
			}
			
			return this;
		}
	}
	
	static protected class ControlButtonsEditor extends ControlButtonsPanel implements TableCellEditor
	{
		private static final long serialVersionUID = 1L;
		transient protected ChangeEvent _changeEvent = null;
		
		public ControlButtonsEditor(final JTable table) {
			super();
			
			MouseListener ml = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					ButtonModel bm = ((JButton)e.getSource()).getModel();
					if (bm.isPressed() && table.isRowSelected(table.getEditingRow()) && e.isControlDown()) {
						setBackground(table.getBackground());
					}
				}
			};
			
			_btnStop.addMouseListener(ml);
			_btnShow.addMouseListener(ml);
			
			_btnStop.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int row = table.convertRowIndexToModel(table.getEditingRow());
					fireEditingStopped();
					MExecRunningTableModel model = ((MExecRunningTableModel)table.getModel());
					if (row >= 0 && row < model.getRowCount()) {
						AsyncProcessMonitorWindow monitor = model.getItem(row);
						if (monitor.isProcessRunning()) {
							// 未停止で実行中
							monitor.doTerminate();
							model.fireTableRowsUpdated(row, row);
						}
						else if (monitor.isEnabledKillButtonAfterTermRequested()) {
							// 強制終了(強制終了操作が可能な場合のみ)
							//--- 問い合わせ
							int ret = JOptionPane.showConfirmDialog(table, RunnerMessages.getInstance().confirmKillTerminatingFilter,
																	CommonMessages.getInstance().msgboxTitleWarn, JOptionPane.OK_CANCEL_OPTION);
							if (ret == JOptionPane.OK_OPTION) {
								monitor.doKill();
								model.fireTableRowsUpdated(row, row);
							}
						}
					}
				}
			});
			
			_btnShow.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int row = table.convertRowIndexToModel(table.getEditingRow());
					fireEditingStopped();
					if (row >= 0 && row < table.getModel().getRowCount()) {
						AsyncProcessMonitorWindow monitor = ((MExecRunningTableModel)table.getModel()).getItem(row);
						monitor.showAndPopupWindow();
					}
				}
			});
			
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					fireEditingStopped();
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			setBackground(table.getSelectionBackground());
			return this;
		}

		@Override
		public Object getCellEditorValue() {
			return "";
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}

		@Override
		public void cancelCellEditing() {
			fireEditingCanceled();
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			return true;
		}

		@Override
		public boolean stopCellEditing() {
			fireEditingStopped();
			return true;
		}

		@Override
		public void addCellEditorListener(CellEditorListener l) {
			listenerList.add(CellEditorListener.class, l);
		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
			listenerList.remove(CellEditorListener.class, l);
		}
		
		public CellEditorListener[] getCellEditorListeners() {
			return listenerList.getListeners(CellEditorListener.class);
		}
		
		protected void fireEditingStopped() {
			Object[] listeners = listenerList.getListenerList();
			for (int i = listeners.length-2; i >= 0; i -= 2) {
				if (listeners[i] == CellEditorListener.class) {
					if (_changeEvent == null) {
						_changeEvent = new ChangeEvent(this);
					}
					((CellEditorListener)listeners[i+1]).editingStopped(_changeEvent);
				}
			}
		}
		
		protected void fireEditingCanceled() {
			Object[] listeners = listenerList.getListenerList();
			for (int i = listeners.length-2; i >= 0; i -= 2) {
				if (listeners[i] == CellEditorListener.class) {
					if (_changeEvent == null) {
						_changeEvent = new ChangeEvent(this);
					}
					((CellEditorListener)listeners[i+1]).editingCanceled(_changeEvent);
				}
			}
		}
	}
	
	static protected class MExecRunningTableCellRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		private final Border _innerCellMargine = BorderFactory.createEmptyBorder(0, 3, 0, 3);
		
		public MExecRunningTableCellRenderer() {
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
			if (model instanceof MExecRunningTableModel && row >= 0 && column==MExecRunningTableModel.COL_STATUS) {
				AsyncProcessMonitorWindow wnd = ((MExecRunningTableModel)model).getItem(row);
				if (wnd.isProcessRunning() || wnd.isProcessTerminating()) {
					//--- running or terminating
					setIcon(RunnerResources.ICON_RUN);
				}
				else if (wnd.getModuleData().isUserCanceled()) {
					//--- canceled
					setIcon(RunnerResources.ICON_BALL_GRAY);
				}
				else if (wnd.getModuleData().isSucceeded()) {
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
	
	static protected class MExecRunningTable extends SpreadSheetTable
	{
		private static final long serialVersionUID = 1L;
		private boolean _initialized = false;
		
		public MExecRunningTable() {
			super();
			setAdjustOnlyVisibleRows(false);			// 列幅の自動調整では全行を対象
			setAdjustOnlySelectedCells(false);			// 列幅の自動調整では選択セルのみに限定しない
			setDefaultRenderer(String.class, new MExecRunningTableCellRenderer());
			setDefaultRenderer(AsyncProcessMonitorWindow.class, new ControlButtonsRenderer());
	        // initialization completed
			_initialized = true;
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		@Override
		public void setModel(TableModel dataModel) {
			super.setModel(dataModel);
			
			TableColumn col = getColumnModel().getColumn(MExecRunningTableModel.COL_CONTROL);
			ControlButtonsRenderer btnRenderer = new ControlButtonsRenderer();
			Dimension dm = btnRenderer.getPreferredSize();
			col.setCellRenderer(btnRenderer);
			col.setCellEditor(new ControlButtonsEditor(this));
			col.setPreferredWidth(dm.width);
			setRowHeight(dm.height);

			Font font = UIManager.getFont("Table.font");
			if (font != null) {
				// 初期推奨幅の設定
				MExecRunningTableCellRenderer renderer = new MExecRunningTableCellRenderer();
				renderer.setFont(font);
				//--- Status
				renderer.setIcon(RunnerResources.ICON_BALL_GRAY);
				renderer.setText("  Cancel  ");
				col = getColumnModel().getColumn(MExecRunningTableModel.COL_STATUS);
				col.setPreferredWidth(renderer.getPreferredSize().width);
				renderer.setIcon(null);
				//--- Name
				renderer.setText(" CSVフィールド値の日付時刻範囲によるレコード抽出 ");
				col = getColumnModel().getColumn(MExecRunningTableModel.COL_NAME);
				col.setPreferredWidth(renderer.getPreferredSize().width);
				//--- ExecStart
				renderer.setText(" 99/99 99:99 ");
				col = getColumnModel().getColumn(MExecRunningTableModel.COL_EXEC_START);
				col.setPreferredWidth(renderer.getPreferredSize().width);
				//--- ExecTime
				renderer.setText(" 999:99'99\"999 ");
				col = getColumnModel().getColumn(MExecRunningTableModel.COL_EXEC_TIME);
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

		//------------------------------------------------------------
		// Inner classes
		//------------------------------------------------------------
	}
}
