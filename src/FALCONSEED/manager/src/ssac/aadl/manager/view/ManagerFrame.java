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
 * @(#)ManagerFrame.java	1.22	2012/11/06
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ManagerFrame.java	1.21	2012/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ManagerFrame.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.manager.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.net.URI;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.PackageBaseSettings;
import ssac.aadl.manager.ManagerMessages;
import ssac.aadl.manager.PackageManager;
import ssac.aadl.manager.menu.ManagerMenuBar;
import ssac.aadl.manager.menu.ManagerMenuResources;
import ssac.aadl.manager.setting.AppSettings;
import ssac.aadl.manager.swing.dialog.PackageBaseChooser;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.PackageBaseLocation;
import ssac.falconseed.common.FSEnvironment;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.logging.AppLogger;
import ssac.util.mac.MacScreenMenuHandler;
import ssac.util.swing.FrameWindow;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.menu.IMenuActionHandler;
import ssac.util.swing.menu.IMenuHandler;
import ssac.util.swing.menu.JMenus;

/**
 * モジュールマネージャのメインフレーム用基底クラス。
 * <p>
 * このクラスは、モジュールマネージャのフレームワークを提供する。
 * 
 * @version 1.22	2012/11/06
 * @since 1.14
 */
public class ManagerFrame extends FrameWindow implements IMenuHandler, IMenuActionHandler, MacScreenMenuHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このフレームの標準メニューバー **/
	protected ManagerMenuBar		_defMainMenu;
	
	// Components
	private JSplitPane			_viewSplit;
	private ManagerTreeView	_viewTree;
	private ModuleFileInfoView	_viewInfo;
	private JTextField			_stcBaseLocation;
	
	// State
	/** 最新のフレーム位置 **/
	private Point		_lastFrameLocation;
	/** 最新のフレームサイズ **/
	private Dimension	_lastFrameSize;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	public ManagerFrame() {
		super();
	}

	/**
	 * メインフレームの初期化。
	 * このメソッドは、アプリケーション唯一のインスタンスから呼び出される。
	 */
	@Override
	public void initialComponent() {
		super.initialComponent();
		setTitle(getFrameTitle());
		
		_lastFrameLocation = new Point();
		_lastFrameSize = new Dimension();
		
		// setup Main menu
		_defMainMenu = new ManagerMenuBar(this);
		setManagerMenuBar(_defMainMenu);
		
		// setup Views
		_viewSplit = createMainPanel();
		getContentPane().add(_viewSplit, BorderLayout.CENTER);
		
		// setup Package base location
		JPanel pnlLocation = createPackageBaseLocationBar();
		getContentPane().add(pnlLocation, BorderLayout.NORTH);
		
		// setup Package base
		_viewTree.initialSetup(this);
		PackageBaseLocation lastLocation = null;
		{
			PackageBaseSettings setting = PackageBaseSettings.getInstance();
			URI uri = setting.getLastPackageBase();
			if (uri != null) {
				try {
					lastLocation = new PackageBaseLocation(uri);
				}
				catch (Throwable ex) {
					AppLogger.error("Failed to get Package base location from URI : \"" + uri.toString() + "\"", ex);
					lastLocation = null;
				}
			}
		}
		if (lastLocation == null) {
			throw new AssertionError("Package base does not selected!");
		}
		try {
			if (!lastLocation.getFileObject().exists()) {
				String msg = "Package base does not exists : \"" + lastLocation.toString() + "\"";
				AppLogger.fatal(msg);
				throw new AssertionError(msg);
			}
			if (!lastLocation.getFileObject().isDirectory()) {
				String msg = "Package base is not directory : \"" + lastLocation.toString() + "\"";
				AppLogger.fatal(msg);
				throw new AssertionError(msg);
			}
			if (ModuleFileManager.getTopProjectPrefsFile(lastLocation.getFileObject(), null) != null) {
				String msg = "Package base is Project root directory : \"" + lastLocation.toString() + "\"";
				AppLogger.fatal(msg);
				throw new AssertionError(msg);
			}
			if (ModuleFileManager.getTopModulePackagePrefsFile(lastLocation.getFileObject(), null) != null) {
				String msg = "Package base is Module package root directory : \"" + lastLocation.toString() + "\"";
				AppLogger.fatal(msg);
				throw new AssertionError(msg);
			}
		}
		catch (SecurityException ex) {
			String msg = "Package base directory cannot access : \"" + lastLocation.toString() + "\"";
			AppLogger.fatal(msg, ex);
			throw new AssertionError(msg);
		}
		_viewTree.setBaseLocation(lastLocation);
		_stcBaseLocation.setText(lastLocation.getFileObject().toString());
		
		// setup Status bar
		//--- none
		
		// setup Window actions
		enableComponentEvents(true);
		enableWindowEvents(true);
		enableWindowStateEvents(true);
		setupComponentActions();
		
		// setup drop targets
		//--- none
		
		// update all menu items
		updateAllMenuItems();
		
		// restore View's settings
		restoreSettings();
	}

	//------------------------------------------------------------
	// Components creation
	//------------------------------------------------------------
	
	private JSplitPane createMainPanel() {
		// create component
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		pane.setResizeWeight(0);
		
		// create views
		_viewTree = new ManagerTreeView();
		_viewInfo = new ModuleFileInfoView();
		
		// add to split
		pane.setLeftComponent(_viewTree);
		pane.setRightComponent(_viewInfo);
		
		// completed
		return pane;
	}
	
	private JPanel createPackageBaseLocationBar() {
		// Label
		JLabel lblName = new JLabel(ManagerMessages.getInstance().appPackBaseLocationLabel + " ");
		_stcBaseLocation = new JTextField();
		Color cr = _stcBaseLocation.getBackground();
		_stcBaseLocation.setEditable(false);
		_stcBaseLocation.setFocusable(false);
		_stcBaseLocation.setBackground(cr);
		
		JPanel pnl = new JPanel(new BorderLayout());
		pnl.setBorder(BorderFactory.createEmptyBorder(0, 3, 1, 3));
		pnl.add(_stcBaseLocation, BorderLayout.CENTER);
		pnl.add(lblName, BorderLayout.WEST);
		
		return pnl;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 標準メニューバーを返す。このメソッドが返すメニューバーは、
	 * 現在アクティブなメニューバーではない場合もある。
	 * 
	 * @return	標準メニューバー
	 */
	public ManagerMenuBar getDefaultMenuBar() {
		return _defMainMenu;
	}

	/**
	 * アクティブなメニューバーを返す。
	 * 基本的に、このフレームに現在設定されているメニューバーで
	 * <code>ManagerMenuBar</code> インスタンスである場合に、
	 * そのインスタンスを返す。この条件に当てはまらない場合は <tt>null</tt> を返す。
	 * @return	アクティブなメニューバー
	 */
	public ManagerMenuBar getActiveMenuBar() {
		JMenuBar bar = getJMenuBar();
		if (bar instanceof ManagerMenuBar)
			return ((ManagerMenuBar)bar);
		else
			return null;
	}

	/**
	 * このフレームに定義されているメニューアクションを取得する。
	 * このメニューアクションは、アクティブなエディタメニューバーからのみ
	 * 取得される。
	 * @param command	検索キーとなるコマンド名
	 * @return	コマンド名に対応するメニューアクションを返す。
	 * 			アクションが未定義の場合は <tt>null</tt> を返す。
	 */
	public Action getMenuAction(String command) {
		ManagerMenuBar bar = getActiveMenuBar();
		if (bar != null) {
			return bar.getMenuAction(command);
		}
		return null;
	}

	/**
	 * メニューバーに設定されているメニューから、コマンド文字列に対応するメニュー項目を取得する。
	 * コマンド文字列に対応するメニュー項目がない場合は <tt>null</tt> を返す。
	 * <p>
	 * このメソッドは、メニューバーのメニューを検索し、指定されたコマンド文字列を保持する
	 * メニュー項目のうち、最初に見つかったインスタンスを返す。
	 * 
	 * @param command	検索するキーとなるコマンド文字列
	 * @return	コマンド文字列に対応するメニュー項目を返す。存在しない場合は <tt>null</tt> を返す。
	 */
	public JMenuItem getMenuItemByCommand(String command) {
		if (Strings.isNullOrEmpty(command))
			return null;
		
		JMenuBar bar = getJMenuBar();
		if (bar == null)
			return null;
		
		return JMenus.getMenuItemByCommand(bar, command);
	}

	/**
	 * 指定されたインデックスのメニューの状態を更新する。
	 * <p>
	 * このメソッドは、指定された位置にあるメニュー(<code>JMenu</code>)の全てのメニュー項目に
	 * 対して更新イベントを発生させる。この更新イベントは、メニューバーに登録されているハンドラに
	 * 通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 * 
	 * @param menuIndex		メニューバーのメニュー位置を示すインデックス。先頭は 0。
	 * 
	 * @throws ArrayIndexOutOfBoundsException メニューインデックスが範囲外の場合
	 */
	public void updateMenuItem(int menuIndex) {
		ManagerMenuBar bar = getActiveMenuBar();
		if (bar != null) {
			bar.updateMenuItem(menuIndex);
		}
	}

	/**
	 * 指定されたコマンド文字列に対応するメニューの状態を更新する。
	 * <p>
	 * このメソッドは、指定されたコマンド文字列に対応するメニュー項目を検索し、その項目に
	 * 対して更新イベントを発生させる。コマンド文字列に対応するものがメニュー(<code>JMenu</code>)の
	 * 場合は、そのメニューに含まれる全てのメニュー項目に対して更新イベントを発生させる。
	 * この更新イベントは、メニューバーに登録されているハンドラに通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 * 
	 * @param command	検索するキーとなるコマンド文字列
	 */
	public void updateMenuItem(String command) {
		ManagerMenuBar bar = getActiveMenuBar();
		if (bar != null) {
			bar.updateMenuItem(command);
		}
	}
	
	/**
	 * 全てのメニュー項目の状態を更新する。
	 * <p>
	 * このメソッドは、このメニューバーに登録されている全てのメニュー項目に対して
	 * 更新イベントを発生させる。この更新イベントは、メニューバーに登録されている
	 * ハンドラに通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 */
	public void updateAllMenuItems() {
		ManagerMenuBar bar = getActiveMenuBar();
		if (bar != null) {
			bar.updateAllMenuItems();
		}
	}

	/**
	 * 現在選択されているパッケージの情報を更新する。
	 */
	public void updatePackageInfo() {
		if (_viewTree.getSelectionCount() == 1) {
			// 単一選択なら、そのノードの情報を表示
			_viewInfo.setTargetNode(_viewTree.getSelectionNode());
		} else {
			// 複数選択の場合は表示しない
			_viewInfo.setTargetNode(null);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * アクティブなビューのタイトルを含めた、このフレームの
	 * タイトル文字列を返す。
	 * @return	フレームのタイトル文字列
	 */
	private String getFrameTitle() {
		String title = ManagerMessages.getInstance().appMainTitle;
		//if (activeEditor != null) {
		//	title = activeEditor.getDocumentTitle() + " - " + title;
		//}
		return title;
	}

	/**
	 * アクティブなメニューバーを設定する。メニューバーが更新されると、
	 * 関連するツールバーも更新される。
	 * 
	 * @param menuBar
	 */
	private void setManagerMenuBar(ManagerMenuBar menuBar) {
		Validations.validNotNull(menuBar);
		JMenuBar oldBar = getJMenuBar();
		
		// 新しいメニューバーがすでに設定済みなら、処理しない
		if (oldBar == menuBar)
			return;

		// 古いツールバーを削除
		if (oldBar instanceof ManagerMenuBar) {
			JToolBar oldToolBar = ((ManagerMenuBar)oldBar).getMainToolBar();
			getContentPane().remove(oldToolBar);
		}
		
		// 新しいメニューとツールバーを設定する
		if (menuBar.getMenuHandler() != this) {
			menuBar.setMenuHandler(this);	// メニューハンドラをこのフレームにする。
		}
		JToolBar toolBar = menuBar.getMainToolBar();
		toolBar.setFloatable(false);
		toolBar.setFocusable(false);
		setJMenuBar(menuBar);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		menuBar.revalidate();
		toolBar.revalidate();
	}

	/**
	 * アプリケーションのプロパティから、コンポーネントの状態を復帰する。
	 */
	private void restoreSettings() {
		AppSettings settings = AppSettings.getInstance();

		// restore window states
		int      mainState = settings.getWindowState(AppSettings.MAINFRAME);
		Point     mainLoc   = settings.getWindowLocation(AppSettings.MAINFRAME);
		Dimension mainSize  = settings.getWindowSize(AppSettings.MAINFRAME);
		//--- Window state
		if (mainState != JFrame.NORMAL) {
			this.setExtendedState(mainState);
		}
		if (this.getExtendedState() == JFrame.NORMAL) {
			//--- Window size
			if (mainSize != null) {
				this.setSize(mainSize);
			} else {
				// default size
				this.setSize(800, 600);
			}
			//--- Window location
			if (mainLoc != null) {
				this.setLocation(mainLoc);
			} else {
				// default location
				this.setLocationRelativeTo(null);
			}
		}
		
		// restore View states
		//--- Divider location
		int divloc = settings.getDividerLocation(AppSettings.MAINFRAME);
		if (divloc < 0) {
			// default location
			Dimension dm = getSize();
			if (dm.height < 50) {
				dm = this.getSize();
			}
			divloc = dm.height / 3 * 2;
		}
		_viewSplit.setDividerLocation(divloc);
		
		// restore View fonts
		//updateFontBySettings();
	}

	/**
	 * 現在のコンポーネントの状態を、アプリケーションのプロパティとして保存する。
	 */
	private void storeSettings() {
		AppSettings settings = AppSettings.getInstance();
		// Window states
		settings.setWindowState(AppSettings.MAINFRAME, getExtendedState());
		settings.setWindowLocation(AppSettings.MAINFRAME, _lastFrameLocation);
		settings.setWindowSize(AppSettings.MAINFRAME, _lastFrameSize);
		// Views states
		settings.setDividerLocation(AppSettings.MAINFRAME, _viewSplit.getDividerLocation());
	}
	
	/**
	 * メインフレームをクローズ可能か検証する。
	 * 
	 * @return クローズ可能なら <tt>true</tt> を返す。
	 */
	@Override
	protected boolean canCloseWindow() {
		AppLogger.debug("Can close window?");

		/*
		// 全てのドキュメントを閉じる
		if (!closeAllEditors()) {
			// user Canceled
			AppLogger.debug("--- user canceled!");
			return false;
		}
		AppLogger.debug("--- close application");
		
		// 検索ダイアログを破棄
		if (dlgFind != null) {
			dlgFind.close();
		}
		*/
		
		// save preferences
		storeSettings();
		
		return true;
	}

	//------------------------------------------------------------
	// implements IMenuHandler interfaces
	//------------------------------------------------------------

	public void menuActionPerformed(ActionEvent e) {
		// コマンド名が未定義の場合は、処理しない
		String pCommand = e.getActionCommand();
		if (Strings.isNullOrEmpty(pCommand))
			return;

		// アクションが未定義のものは処理しない
		Object pSource  = e.getSource();
		Action pAction = null;
		if (pSource instanceof AbstractButton)
			pAction = ((AbstractButton)pSource).getAction();
		if (pAction == null)
			return;
		
		// ツリービュー固有のハンドラ
		//if (_viewTree != null && _viewTree.hasFocus()) {
		//--- 現在は、ツリー操作が基本のため、フォーカスがなくても操作可能
		if (_viewTree != null) {
			if (_viewTree.onProcessMenuSelection(pCommand, pSource, pAction)) {
				// process is terminate.
				return;
			}
		}
		
		// このイベントが処理されていない場合は、このフレームのイベントハンドラを呼び出す
		onProcessMenuSelection(pCommand, pSource, pAction);
	}

	public void menuUpdatePerformed(String command, Object source) {
		// コマンド名が未定義の場合は、処理しない
		if (Strings.isNullOrEmpty(command))
			return;

		// アクションが未定義のものは処理しない
		Action pAction = null;
		if (source instanceof AbstractButton)
			pAction = ((AbstractButton)source).getAction();
		if (pAction == null)
			return;
		
		// ツリービュー固有のハンドラ
		//if (_viewTree != null && _viewTree.hasFocus()) {
		//--- 現在は、ツリー操作が基本のため、フォーカスがなくても操作可能
		if (_viewTree != null) {
			if (_viewTree.onProcessMenuUpdate(command, source, pAction)) {
				// process is terminate.
				return;
			}
		}
		
		// このイベントが処理されていない場合は、このフレームのイベントハンドラを呼び出す
		onProcessMenuUpdate(command, source, pAction);
	}

	//------------------------------------------------------------
	// Menu handlers
	//------------------------------------------------------------

	/**
	 * メニュー項目の選択時に呼び出されるハンドラ・メソッド。
	 * このメソッドはメニューアクションから呼び出される。
	 * このメソッドが呼び出されると、アクティブドキュメント、アクティブドキュメントの
	 * コンポーネント、このフレームのデフォルト処理の順に、ハンドラが呼び出される。
	 * 
	 * @param command	このイベント要因のコマンド文字列
	 * @param source	このイベント要因のソースオブジェクト。
	 * 					ソースオブジェクトが未定義の場合は <tt>null</tt>。
	 * @param action	このイベント要因のソースオブジェクトに割り当てられたアクション。
	 * 					アクションが未定義の場合は <tt>null</tt>。
	 * @return	このハンドラ内で処理が完結した場合は <tt>true</tt> を返す。イベントシーケンスの
	 * 			別のハンドラに処理を委譲する場合は <tt>false</tt> を返す。
	 */
	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		if (ManagerMenuResources.ID_FILE_NEW_DIR.equals(command)) {
			onSelectedMenuFileNewDir(action);
			return true;
		}
		else if (ManagerMenuResources.ID_FILE_MOVETO.equals(command)) {
			onSelectedMenuFileMoveTo(action);
			return true;
		}
		else if (ManagerMenuResources.ID_FILE_RENAME.equals(command)) {
			onSelectedMenuFileRename(action);
			return true;
		}
		else if (ManagerMenuResources.ID_FILE_REFRESH.equals(command)) {
			onSelectedMenuFileRefresh(action);
			return true;
		}
		else if (ManagerMenuResources.ID_FILE_WS_REFRESH.equals(command)) {
			onSelectedMenuFilePackageBaseRefresh(action);
			return true;
		}
		else if (ManagerMenuResources.ID_FILE_WS_SELECT.equals(command)) {
			onSelectedMenuFilePackageBaseSelect(action);
			return true;
		}
		else if (ManagerMenuResources.ID_FILE_QUIT.equals(command)) {
			onSelectedMenuFileQuit(action);
			return true;
		}
		else if (ManagerMenuResources.ID_HELP_ABOUT.equals(command)) {
			onSelectedMenuHelpAbout(action);
			return true;
		}
		
		return false;
	}

	/**
	 * メニュー項目の更新要求時に呼び出されるハンドラ・メソッド。
	 * このメソッドはメニュー項目更新メソッドから呼び出される。
	 * このメソッドが呼び出されると、アクティブドキュメント、アクティブドキュメントの
	 * コンポーネント、このフレームのデフォルト処理の順に、ハンドラが呼び出される。
	 * 
	 * @param command	このイベント要因のコマンド文字列
	 * @param source	このイベント要因のソースオブジェクト。
	 * 					ソースオブジェクトが未定義の場合は <tt>null</tt>。
	 * @param action	このイベント要因のソースオブジェクトに割り当てられたアクション。
	 * 					アクションが未定義の場合は <tt>null</tt>。
	 * @return	このハンドラ内で処理が完結した場合は <tt>true</tt> を返す。イベントシーケンスの
	 * 			別のハンドラに処理を委譲する場合は <tt>false</tt> を返す。
	 */
	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (ManagerMenuResources.ID_FILE_NEW_DIR.equals(command)) {
			action.setEnabled(_viewTree.canCreate());
			return true;
		}
		else if (ManagerMenuResources.ID_FILE_MOVETO.equals(command)) {
			action.setEnabled(_viewTree.canMoveTo());
			return true;
		}
		else if (ManagerMenuResources.ID_FILE_RENAME.equals(command)) {
			action.setEnabled(_viewTree.canRename());
			return true;
		}
		else if (ManagerMenuResources.ID_FILE_REFRESH.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (ManagerMenuResources.ID_FILE_WS_REFRESH.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (ManagerMenuResources.ID_FILE_WS_SELECT.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (ManagerMenuResources.ID_FILE_QUIT.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (ManagerMenuResources.ID_HELP_ABOUT.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (action != null) {
			action.setEnabled(false);
		}
		
		return false;
	}

	// menu : [File]-[New Folder]
	protected void onSelectedMenuFileNewDir(Action action) {
		AppLogger.debug("menu [File]-[New Folder] selected.");
		_viewTree.doCreateDirectory();
		_viewTree.requestFocusInComponent();
	}

	// menu : [File]-[Move to]
	protected void onSelectedMenuFileMoveTo(Action action) {
		AppLogger.debug("menu [File]-[Move To] selected.");
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				_viewTree.doMoveTo();
				_viewTree.requestFocusInComponent();
			}
		});
	}

	// menu : [File]-[Rename]
	protected void onSelectedMenuFileRename(Action action) {
		AppLogger.debug("menu [File]-[Rename] selected.");
		_viewTree.doRename();
		_viewTree.requestFocusInComponent();
	}

	// menu : [File]-[Refresh]
	protected void onSelectedMenuFileRefresh(Action action) {
		AppLogger.debug("menu [File]-[Refresh] selected.");
		if (_viewTree.isSelectionEmpty()) {
			_viewTree.refreshAllTree();
		} else {
			_viewTree.refreshSelectedTree();
		}
		_viewTree.requestFocusInComponent();
	}

	// menu : [File]-[PackageBase refresh]
	protected void onSelectedMenuFilePackageBaseRefresh(Action action) {
		AppLogger.debug("menu [File]-[refresh Package base] selected.");
		_viewTree.refreshAllTree();
		_viewTree.requestFocusInComponent();
	}

	// menu : [File]-[PackageBase select]
	protected void onSelectedMenuFilePackageBaseSelect(Action action) {
		AppLogger.debug("menu [File]-[select Package base] selected.");
		// select Package Base
		URI oldURI = PackageBaseSettings.getInstance().getLastPackageBase();
		PackageBaseLocation oldLocation = _viewTree.getBaseLocation();
		PackageBaseChooser chooser = new PackageBaseChooser(this,
											AppSettings.PACKBASE_CHOOSER,
											AppSettings.getInstance().getConfiguration());
		chooser.setVisible(true);
		if (chooser.getDialogResult() == IDialogResult.DialogResult_OK) {
			PackageBaseLocation newLocation = null;
			URI uri = PackageBaseSettings.getInstance().getLastPackageBase();
			if (uri != null) {
				try {
					newLocation = new PackageBaseLocation(uri);
				} catch (Throwable ex) {
					AppLogger.error("Failed to create PackageBaseLocation from uri : \"" + uri.toString() + "\"", ex);
					newLocation = null;
				}
			}
			boolean validPackBase = true;
			if (newLocation == null) {
				validPackBase = false;
				String errmsg = CommonMessages.getInstance().msgPackBaseNoSelection;
				AppLogger.error(errmsg + " : \"" + String.valueOf(uri) + "\"");
				PackageManager.showErrorMessage(this, errmsg);
			}
			else if (!PackageBaseChooser.verifyPackageBase(this, false, newLocation.getFileObject())) {
				validPackBase = false;
			}
			if (!validPackBase) {
				PackageBaseSettings.getInstance().setLastPackageBase(oldURI);
				try {
					PackageBaseSettings.getInstance().commit();
				} catch (Throwable ex) {
					AppLogger.warn("Failed to commit PackageBaseSettings.", ex);
				}
				return;
			}
			
			// 現在のパッケージベースと同一なら処理しない
			if (newLocation.equals(oldLocation)) {
				_viewTree.requestFocusInComponent();
				return;
			}
			
			// 新しいワークスペースを表示
			_viewTree.setBaseLocation(newLocation);
			_stcBaseLocation.setText(newLocation.getFileObject().toString());
			updateAllMenuItems();
			updatePackageInfo();
		}
		_viewTree.requestFocusInWindow();
	}

	// menu : [File]-[Quit]
	protected void onSelectedMenuFileQuit(Action action) {
		AppLogger.debug("menu [File]-[Quit] selected.");
		closeWindow();
	}

	public void onMacMenuQuit() {
		AppLogger.debug("screen menu Application-[Quit] selected.");
		closeWindow();
	}
	
	// menu : [Help]-[About]
	protected void onSelectedMenuHelpAbout(Action action) {
		AppLogger.debug("menu [Help]-[About] selected.");
		showAboutDialog();
	}
	
	public void onMacMenuAbout() {
		AppLogger.debug("screen menu Application-[About] selected.");
		showAboutDialog();
	}
	
	protected void showAboutDialog() {
//		JOptionPane.showMessageDialog(this,
//				PackageManager.VERSION_MESSAGE,
//				ManagerMessages.getInstance().appMainTitle,
//				JOptionPane.INFORMATION_MESSAGE);
		
		// 1.22 : Show message with FALCON-SEED version
		String version_message = FSEnvironment.getInstance().title();
		if (!Strings.isNullOrEmpty(version_message)) {
			version_message = version_message + "\n - " + PackageManager.LOCAL_VERSION;
		} else {
			version_message = PackageManager.LOCAL_VERSION;
		}

		JOptionPane.showMessageDialog(this,
				version_message,
				ManagerMessages.getInstance().appMainTitle,
				JOptionPane.INFORMATION_MESSAGE);
		_viewTree.requestFocusInComponent();
	}

	//------------------------------------------------------------
	// Event handler actions
	//------------------------------------------------------------

	protected void setupComponentActions() {
		/*
		// Editor tab
		tabEditor.addMouseListener(new EditorTabMouseHandler());
		tabEditor.addChangeListener(new EditorTabSelectionHandler());
		
		// Info tab
		tabInfo.addMouseListener(new InfoTabMouseHandler());
		tabInfo.addChangeListener(new InfoTabSelectionHandler());
		
		// Build pane
		paneBuild.addExecutorStoppedListener(new CompileStoppedHandler());
		
		// Console pane
		paneConsole.addExecutorStoppedListener(new RunStoppedHandler());
		*/
	}

	//------------------------------------------------------------
	// Component actions override to FrameWindow
	//------------------------------------------------------------
	
	// --- Window opened event
	@Override
	protected void onWindowOpened(WindowEvent we) {
		// save window bounds
		_lastFrameLocation.setLocation(this.getLocationOnScreen());
		_lastFrameSize.setSize(this.getSize());
	}

	// --- Window activated event
	@Override
	protected void onWindowActivated(WindowEvent e) {
		// アクティブなエディタの編集メニューを更新
		if (e.getWindow() == this) {
			updateAllMenuItems();
		}
	}

	// --- Window closed event
	@Override
	protected void onWindowClosed(WindowEvent we) {
		// save Preferences
		try {
			AppSettings.flush();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		AppLogger.info("\n<<<<< Finished - " + PackageManager.SIMPLE_VERSION_INFO + " >>>>>\n");
	}
	
	// --- Window resized event
	@Override
	protected void onWindowResized(ComponentEvent ce) {
		if (getExtendedState() == JFrame.NORMAL) {
			_lastFrameSize.setSize(this.getSize());
		}
	}
	
	// --- Window moved event
	@Override
	protected void onWindowMoved(ComponentEvent ce) {
		if (getExtendedState() == JFrame.NORMAL) {
			try {
				_lastFrameLocation.setLocation(this.getLocationOnScreen());
			} catch(IllegalComponentStateException icse) {
				AppLogger.debug(icse);
			}
		}
	}
}
