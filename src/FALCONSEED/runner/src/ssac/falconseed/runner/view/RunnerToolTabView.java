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
 * @(#)IRunnerTreeView.java	3.0.0	2014/03/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IRunnerTreeView.java	1.22	2012/08/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.swing.menu.IMenuActionHandler;

/**
 * タブによる切り替え可能なツールビュー
 * 
 * @version 3.0.0	2014/03/26
 * @since 1.22
 */
public class RunnerToolTabView extends JPanel implements IMenuActionHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1943189501582394892L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private JTabbedPane			_tab;
	private MExecHistoryToolView	_viewHistory;
	private MExecRunningToolView	_viewRunning;
	
	private ChangeListener	_chkConsoleAutoCloseListener;
	private ChangeListener	_chkConsoleShowAtStartListener;
	
	private JCheckBox		_chkConsoleAutoCloseForHistory;
	private JCheckBox		_chkConsoleAutoCloseForRunning;
	private JCheckBox		_chkConsoleShowAtStartForHistory;
	private JCheckBox		_chkConsoleShowAtStartForRunning;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	public RunnerToolTabView() {
		super(new BorderLayout());
	}
	
	public void initialComponent()
	{
		// create components
		this._viewHistory = createFilterHistoryToolView();
		this._viewRunning = createFilterRunningToolView();
		this._tab = createTabComponent();
		
		// setup tab
		this._tab.addTab(RunnerMessages.getInstance().ToolTabViewTitle_Running, _viewRunning);
		this._tab.addTab(RunnerMessages.getInstance().ToolTabViewTitle_History, _viewHistory);
		this.add(_tab, BorderLayout.CENTER);

		// setup actions
		setupActions();
	}

	/**
	 * フレームの情報を基に、このコンポーネントのセットアップを行う。
	 * @param frame	アプリケーションのフレームウィンドウ
	 */
	protected void initialSetup(RunnerFrame frame) {
		getHistoryToolView().initialSetup(frame);
		getRunningToolView().initialSetup(frame);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public JCheckBox createConsoleAutoCloseCheckBox() {
		JCheckBox chk = new JCheckBox(RunnerMessages.getInstance().ProcMonitorSetting_AutoCloseAfterExec);
		chk.setSelected(AppSettings.getInstance().getConsoleAutoClose());
		return chk;
	}
	
	static public JCheckBox createConsoleShowAtStartCheckBox() {
		JCheckBox chk = new JCheckBox(RunnerMessages.getInstance().ProcMonitorSetting_ShowConsoleAtStart);
		chk.setSelected(AppSettings.getInstance().getConsoleShowAtStart());
		return chk;
	}
	
	public JCheckBox getConsoleAutoCloseCheckBoxForHistory() {
		if (_chkConsoleAutoCloseForHistory == null) {
			_chkConsoleAutoCloseForHistory = createConsoleAutoCloseCheckBox();
			_chkConsoleAutoCloseForHistory.addChangeListener(getConsoleAutoCloseChangeListener());
		}
		return _chkConsoleAutoCloseForHistory;
	}
	
	public JCheckBox getConsoleAutoCloseCheckBoxForRunning() {
		if (_chkConsoleAutoCloseForRunning == null) {
			_chkConsoleAutoCloseForRunning = createConsoleAutoCloseCheckBox();
			_chkConsoleAutoCloseForRunning.addChangeListener(getConsoleAutoCloseChangeListener());
		}
		return _chkConsoleAutoCloseForRunning;
	}
	
	public JCheckBox getConsoleShowAtStartCheckBoxForHistory() {
		if (_chkConsoleShowAtStartForHistory == null) {
			_chkConsoleShowAtStartForHistory = createConsoleShowAtStartCheckBox();
			_chkConsoleShowAtStartForHistory.addChangeListener(getConsoleShowAtStartChangeListener());
		}
		return _chkConsoleShowAtStartForHistory;
	}
	
	public JCheckBox getConsoleShowAtStartCheckBoxForRunning() {
		if (_chkConsoleShowAtStartForRunning == null) {
			_chkConsoleShowAtStartForRunning = createConsoleShowAtStartCheckBox();
			_chkConsoleShowAtStartForRunning.addChangeListener(getConsoleShowAtStartChangeListener());
		}
		return _chkConsoleShowAtStartForRunning;
	}
	
	protected ChangeListener getConsoleAutoCloseChangeListener() {
		if (_chkConsoleAutoCloseListener == null) {
			_chkConsoleAutoCloseListener = new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					boolean selected = ((JCheckBox)e.getSource()).isSelected();
					if (_chkConsoleAutoCloseForHistory.isSelected() != selected) {
						_chkConsoleAutoCloseForHistory.setSelected(selected);
					}
					if (_chkConsoleAutoCloseForRunning.isSelected() != selected) {
						_chkConsoleAutoCloseForRunning.setSelected(selected);
					}
					if (AppSettings.getInstance().getConsoleAutoClose() != selected) {
						AppSettings.getInstance().setConsoleAutoClose(selected);
					}
				}
			};
		}
		return _chkConsoleAutoCloseListener;
	}
	
	protected ChangeListener getConsoleShowAtStartChangeListener() {
		if (_chkConsoleShowAtStartListener == null) {
			_chkConsoleShowAtStartListener = new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					boolean selected = ((JCheckBox)e.getSource()).isSelected();
					if (_chkConsoleShowAtStartForHistory.isSelected() != selected) {
						_chkConsoleShowAtStartForHistory.setSelected(selected);
					}
					if (_chkConsoleShowAtStartForRunning.isSelected() != selected) {
						_chkConsoleShowAtStartForRunning.setSelected(selected);
					}
					if (AppSettings.getInstance().getConsoleShowAtStart() != selected) {
						AppSettings.getInstance().setConsoleShowAtStart(selected);
					}
				}
			};
		}
		return _chkConsoleShowAtStartListener;
	}
	
	public boolean isEnabledHistoryToolView() {
		return isEnabledTabComponent(_viewHistory);
	}
	
	public boolean isEnabledRunningToolView() {
		return isEnabledTabComponent(_viewRunning);
	}
	
	public void setEnabledHistoryToolView(boolean toEnable) {
		setEnabledTabComponent(_viewHistory, toEnable);
	}
	
	public void setEnabledRunningToolView(boolean toEnable) {
		setEnabledTabComponent(_viewRunning, toEnable);
	}
	
	public boolean isHistoryToolViewSelected() {
		return (_tab.getSelectedComponent() == _viewHistory);
	}
	
	public boolean isRunningToolViewSelected() {
		return (_tab.getSelectedComponent() == _viewRunning);
	}
	
	public void selectHistoryToolView() {
		_tab.setSelectedComponent(_viewHistory);
	}
	
	public void selectRunningToolView() {
		_tab.setSelectedComponent(_viewRunning);
	}
	
	public MExecHistoryToolView getHistoryToolView() {
		return _viewHistory;
	}
	
	public MExecRunningToolView getRunningToolView() {
		return _viewRunning;
	}
	
	public IRunnerToolView getSelectedToolView() {
		return (IRunnerToolView)_tab.getSelectedComponent();
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
		getSelectedToolView().requestFocusInComponent();
	}

	/**
	 * 選択されているすべてのオブジェクトがコピー可能か検証する。
	 * @return	コピー可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canCopy() {
		return getSelectedToolView().canCopy();
	}

	/**
	 * 選択されているオブジェクトをクリップボードへコピーする
	 */
	public void doCopy() {
		getSelectedToolView().doCopy();
	}

	/**
	 * 現在クリップボードに存在する内容が選択位置に貼り付け可能か検証する。
	 * @return	貼り付け可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canPaste() {
		return getSelectedToolView().canPaste();
	}

	/**
	 * クリップボードに存在するデータを、コンポーネントにコピーする
	 */
	public void doPaste() {
		getSelectedToolView().doPaste();
	}

	/**
	 * 選択されているすべてのオブジェクトが削除可能か検証する。
	 * @return	削除可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canDelete() {
		return getSelectedToolView().canDelete();
	}

	/**
	 * 選択されているオブジェクトを削除する
	 */
	public void doDelete() {
		getSelectedToolView().doDelete();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
//	protected void initialSetup(RunnerFrame frame) {
//		// メニューアクセラレータキーを有効にするため、
//		// ツリーコンポーネントのマップを変更
//		JMenuBar menuBar = frame.getDefaultMainMenuBar();
//		KeyStroke[] strokes = SwingTools.getMenuAccelerators(menuBar);
//		_cTree.registMenuAcceleratorKeyStroke(strokes);
//	}
	
	protected boolean isEnabledTabComponent(Component component) {
		int tabIndex = _tab.indexOfComponent(component);
		if (tabIndex >= 0) {
			return _tab.isEnabledAt(tabIndex);
		} else {
			return false;
		}
	}
	
	protected void setEnabledTabComponent(Component component, boolean toEnable) {
		int tabIndex = _tab.indexOfComponent(component);
		if (tabIndex >= 0) {
			_tab.setEnabledAt(tabIndex, toEnable);
		}
	}
	
	private MExecHistoryToolView createFilterHistoryToolView() {
		MExecHistoryToolView tool = new MExecHistoryToolView(getConsoleShowAtStartCheckBoxForHistory(), getConsoleAutoCloseCheckBoxForHistory());
		tool.initialComponent();
		return tool;
	}
	
	private MExecRunningToolView createFilterRunningToolView() {
		MExecRunningToolView tool = new MExecRunningToolView(getConsoleShowAtStartCheckBoxForRunning(), getConsoleAutoCloseCheckBoxForRunning());
		tool.initialComponent();
		return tool;
	}
	
	private JTabbedPane createTabComponent() {
		// create component
		JTabbedPane tab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		tab.setFocusable(false);
		
		// completed
		return tab;
	}
	
	private void setupActions() {
		// tab mouse listener
		_tab.addMouseListener(new TabMouseHandler());
		
		// tab selection change
		_tab.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				onTabSelectionChanged(e);
			}
		});
	}

	//------------------------------------------------------------
	// Menu events
	//------------------------------------------------------------

	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		return getSelectedToolView().onProcessMenuSelection(command, source, action);
	}

	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		return getSelectedToolView().onProcessMenuUpdate(command, source, action);
	}

	//------------------------------------------------------------
	// Action events
	//------------------------------------------------------------
	
	protected void onTabSelectionChanged(ChangeEvent ce) {
		//System.err.println("RunnerToolTabView#onTabSelectionChanged()");
		if (getFrame().getMExecArgsEditDialog() == null) {
			getSelectedToolView().requestFocusInComponent();
			getFrame().updateAllMenuItems();
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * エディタタブのコンテキストメニュー用ハンドラ
	 */
	class TabMouseHandler extends MouseAdapter {
		public void mousePressed(MouseEvent me) {
			//int idx = _tab.indexAtLocation(me.getX(), me.getY());
			//System.err.println("RunnerTreeTabView.TabMouseHandler#mousePressed() - tab index=" + idx);
			if (!getFrame().isToolViewActivated()) {
				int index = _tab.indexAtLocation(me.getX(), me.getY());
				int selected = _tab.getSelectedIndex();
				if ((index < 0) || (selected == index)) {
					getSelectedToolView().requestFocusInComponent();
				}
			}
		}
	}
}
