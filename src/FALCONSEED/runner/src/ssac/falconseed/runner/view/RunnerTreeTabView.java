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
 * @(#)RunnerTreeTabView.java	3.2.0	2015/06/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerTreeTabView.java	2.0.0	2012/11/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerTreeTabView.java	1.22	2012/07/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerTreeTabView.java	1.20	2012/03/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerTreeTabView.java	1.10	2011/02/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ssac.aadl.common.CommonResources;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.RunnerResources;
import ssac.util.io.VirtualFile;
import ssac.util.swing.menu.IMenuActionHandler;

/**
 * タブによる切り替え可能なツリービュー
 * 
 * @version 3.2.0
 * @since 1.10
 */
public class RunnerTreeTabView extends JPanel implements IMenuActionHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -1638709565179003925L;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private JTabbedPane		_tab;
	private MExecDefTreeView	_viewMExecDefTree;
	private DataFileTreeView	_viewDataFileTree;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	public RunnerTreeTabView() {
		super(new BorderLayout());
	}
	
	public void initialComponent(VirtualFile mexecSystemRootDir, VirtualFile mexecUserRootDir,
									VirtualFile dataSystemRootDir, VirtualFile dataUserRootDir)
	{
		if (mexecSystemRootDir == null)
			throw new NullPointerException("The specified MExecDef System root directory is null.");
		if (mexecUserRootDir == null)
			throw new NullPointerException("The specified MExecDef User root directory is null.");
		if (dataSystemRootDir == null)
			throw new NullPointerException("The specified DataFile System root directory is null.");
		if (dataUserRootDir == null)
			throw new NullPointerException("The specified DataFile User root directory is null.");
		
		// create components
		this._viewMExecDefTree = createMExecDefTreeView(mexecSystemRootDir, mexecUserRootDir);
		this._viewDataFileTree = createDataFileTreeView(dataSystemRootDir, dataUserRootDir);
		this._tab = createTabComponent();
		
		// setup tab
		this._tab.addTab("", RunnerResources.ICON_FILTER, _viewMExecDefTree, RunnerMessages.getInstance().TreeTabViewTitle_MExecDef);
		this._tab.addTab("", CommonResources.ICON_FILE, _viewDataFileTree, RunnerMessages.getInstance().TreeTabViewTitle_DataFile);
		this.add(_tab, BorderLayout.CENTER);

		// setup actions
		setupActions();
	}

	/**
	 * フレームの情報を基に、このコンポーネントのセットアップを行う。
	 * @param frame	アプリケーションのフレームウィンドウ
	 */
	protected void initialSetup(RunnerFrame frame) {
		getMExecDefTreeView().initialSetup(frame);
		getDataFileTreeView().initialSetup(frame);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEnabledMExecDefTreeView() {
		return isEnabledTabComponent(_viewMExecDefTree);
	}
	
	public boolean isEnabledDataFileTreeView() {
		return isEnabledTabComponent(_viewDataFileTree);
	}
	
	public void setEnabledMExecDefTreeView(boolean toEnable) {
		setEnabledTabComponent(_viewMExecDefTree, toEnable);
	}
	
	public void setEnabledDataFileTreeView(boolean toEnable) {
		setEnabledTabComponent(_viewDataFileTree, toEnable);
	}
	
	public boolean isMExecDefTreeViewSelected() {
		return (_tab.getSelectedComponent() == _viewMExecDefTree);
	}
	
	public boolean isDataFileTreeViewSelected() {
		return (_tab.getSelectedComponent() == _viewDataFileTree);
	}
	
	public void selectMExecDefTreeView() {
		_tab.setSelectedComponent(_viewMExecDefTree);
	}
	
	public void selectDataFileTreeView() {
		_tab.setSelectedComponent(_viewDataFileTree);
	}
	
	public MExecDefTreeView getMExecDefTreeView() {
		return _viewMExecDefTree;
	}
	
	public DataFileTreeView getDataFileTreeView() {
		return _viewDataFileTree;
	}
	
	public IRunnerTreeView getSelectedTreeView() {
		return (IRunnerTreeView)_tab.getSelectedComponent();
	}
	
	public boolean isSelectionEmpty() {
		return getSelectedTreeView().isSelectionEmpty();
	}
	
	public int getSelectionCount() {
		return getSelectedTreeView().getSelectionCount();
	}
	
	public VirtualFile getSelectionFile() {
		return getSelectedTreeView().getSelectionFile();
	}
	
	public VirtualFile[] getSelectionFiles() {
		return getSelectedTreeView().getSelectionFiles();
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
	 * ルートノードを起点に、表示されているすべてのツリーノードを
	 * 最新の情報に更新する。
	 */
	public void refreshAllTree() {
		getSelectedTreeView().refreshAllTree();
	}

	/**
	 * 選択されているノードを起点に、そのノードの下位で表示されている
	 * すべてのツリーノードを最新の情報に更新する。
	 */
	public void refreshSelectedTree() {
		getSelectedTreeView().refreshSelectedTree();
	}

	/**
	 * 指定された抽象パスを持つノードの表示を更新する。
	 * 表示されていないノードの場合や、ツリー階層内の抽象パスではない
	 * 場合は何もしない。
	 * @param targetFile	対象の抽象パス
	 */
	public void refreshFileTree(VirtualFile targetFile) {
		getSelectedTreeView().refreshFileTree(targetFile);
	}

	/**
	 * このビューのメインコンポーネントにフォーカスを設定する。
	 */
	public void requestFocusInComponent() {
		getSelectedTreeView().requestFocusInComponent();
	}
	
	//
	// File operations
	//

	/**
	 * ディレクトリの新規作成を許可する状態であれば <tt>true</tt> を返す。
	 */
	public boolean canCreateDirectory() {
		return getSelectedTreeView().canCreateDirectory();
	}
	
	/**
	 * ファイルの新規作成を許可する状態であれば <tt>true</tt> を返す。
	 */
	public boolean canCreateFile() {
		return getSelectedTreeView().canCreateFile();
	}
	
	/**
	 * モジュール実行定義の新規作成を許可する状態であれば <tt>true</tt> を返す。
	 */
	public boolean canCreateMExecDef() {
		if (isMExecDefTreeViewSelected()) {
			return getMExecDefTreeView().canCreateMExecDef();
		} else {
			return false;
		}
	}

	/**
	 * 汎用フィルタの新規作成を許可する状態であれば <tt>true</tt> を返す。
	 * @since 3.2.0
	 */
	public boolean canCreateGenericFilter() {
		if (isMExecDefTreeViewSelected()) {
			return getMExecDefTreeView().canCreateGenericFilter();
		} else {
			return false;
		}
	}
	
	/**
	 * フィルタマクロ定義の新規作成を許可する状態であれば <tt>true</tt> を返す。
	 * @since 2.0.0
	 */
	public boolean canCreateFilterMacro() {
		if (isMExecDefTreeViewSelected()) {
			return getMExecDefTreeView().canCreateFilterMacro();
		} else {
			return false;
		}
	}

	/**
	 * 選択されているノードについて、モジュール実行定義の表示もしくは編集が可能かを判定する。
	 * @return	表示もしくは編集が可能なモジュール実行定義が選択されている場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>
	 */
	public boolean canShowMExecDef() {
		if (isMExecDefTreeViewSelected()) {
			return getMExecDefTreeView().canShowMExecDef();
		} else {
			return false;
		}
	}

	/**
	 * 選択されているモジュール実行定義の内容を表示する。
	 * モジュール実行定義が選択されていない場合は、何もしない。
	 */
	public void doShowMExecDef() {
		if (isMExecDefTreeViewSelected()) {
			getMExecDefTreeView().doShowMExecDef();
		}
	}

	/**
	 * ディレクトリ選択ダイアログを表示し、選択されたディレクトリを返す。
	 * @param title			ディレクトリ選択ダイアログのタイトルとする文字列
	 * @param initParent	初期選択位置とする抽象パス。このパスがディレクトリでは
	 * 						ない場合は、親ディレクトリを初期選択位置とする。
	 * 						ツリーに存在しない抽象パスであれば選択しない。
	 * @return	選択された抽象パスを返す。選択されなかった場合は <tt>null</tt> を返す。
	 */
	public VirtualFile chooseDirectory(String title, VirtualFile initParent) {
		return getSelectedTreeView().chooseDirectory(title, initParent);
	}

	/**
	 * ディレクトリを新規に作成するためのディレクトリ名入力ダイアログを表示し、
	 * 入力されたディレクトリ名で新規フォルダを作成する。
	 * @return	正常に作成できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 */
	public boolean doCreateDirectory() {
		return getSelectedTreeView().doCreateDirectory();
	}

	/**
	 * モジュール実行定義を新規に作成するためのダイアログを表示し、
	 * 入力された情報で新規モジュール実行定義を作成する。
	 * @return	正常に作成できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 */
	public boolean doCreateMExecDef() {
		if (isMExecDefTreeViewSelected()) {
			return getMExecDefTreeView().doCreateMExecDef();
		} else {
			return false;
		}
	}

	/**
	 * 汎用フィルタを新規に作成するためのダイアログを表示し、
	 * 入力された情報で新規汎用フィルタを作成する。
	 * @return	正常に作成できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 */
	public boolean doCreateGenericFilter() {
		if (isMExecDefTreeViewSelected()) {
			return getMExecDefTreeView().doCreateGenericFilter();
		} else {
			return false;
		}
	}

	/**
	 * フィルタマクロ定義を新規に作成するためのダイアログを表示し、
	 * 入力された情報で新規フィルタマクロ定義を作成する。
	 * @return	正常に作成できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 * @since 2.0.0
	 */
	public boolean doCreateFilterMacro() {
		if (isMExecDefTreeViewSelected()) {
			return getMExecDefTreeView().doCreateFilterMacro();
		} else {
			return false;
		}
	}

	/**
	 * ツリーノードで選択されているノードの名前変更が可能であれば <tt>true</tt> を返す。
	 * 名前変更可能な条件は次の通り。
	 * <ul>
	 * <li>単一の選択である</li>
	 * <li>ユーザールート配下のディレクトリもしくはファイルが選択されている</li>
	 * </ul>
	 */
	public boolean canRename() {
		return getSelectedTreeView().canRename();
	}
	
	/**
	 * 選択されているノードが示すファイル名を変更する。
	 */
	public void doRename() {
		getSelectedTreeView().doRename();
	}

	/**
	 * ツリーノードで選択されているノードが移動可能であれば <tt>true</tt> を返す。
	 * 移動可能となる条件は次の通り。
	 * <ul>
	 * <li>ユーザールート配下のディレクトリもしくはファイルが選択されている</li>
	 * </ul>
	 */
	public boolean canMoveTo() {
		return getSelectedTreeView().canMoveTo();
	}

	/**
	 * 選択されているノードを、ユーザー指定の場所に移動する。
	 */
	public void doMoveTo() {
		getSelectedTreeView().doMoveTo();
	}

	/**
	 * 選択されているすべてのノードがコピー可能か検証する。
	 * 選択されたノードがすべて同一階層で、ルートではない場合にコピー可能とする。
	 * @return	コピー可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canCopy() {
		return getSelectedTreeView().canCopy();
	}

	/**
	 * 選択されているノードが示すファイルをクリップボードへコピーする
	 */
	public void doCopy() {
		getSelectedTreeView().doCopy();
	}

	/**
	 * 現在クリップボードに存在する内容が選択位置に貼り付け可能か検証する。
	 * ファイルもしくはフォルダであり、ノードが一つだけ選択されている場合のみ許可する。
	 * @return	貼り付け可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canPaste() {
		return getSelectedTreeView().canPaste();
	}

	/**
	 * クリップボードに存在するデータを、ワークスペースにコピーする
	 */
	public void doPaste() {
		getSelectedTreeView().doPaste();
	}

	/**
	 * 選択されているすべてのノードが削除可能か検証する。
	 * ルートノード以外のノードが選択されている場合は削除可能とする。
	 * @return	削除可能であれば <tt>true</tt> を返す。
	 * 			未選択の場合は <tt>false</tt> を返す。
	 */
	public boolean canDelete() {
		return getSelectedTreeView().canDelete();
	}

	/**
	 * 選択されているノードが示すファイルを削除する
	 */
	public void doDelete() {
		getSelectedTreeView().doDelete();
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
	
	private MExecDefTreeView createMExecDefTreeView(VirtualFile vfSystemDir, VirtualFile vfUserDir) {
		MExecDefTreeView tree = new MExecDefTreeView();
		tree.initialComponent(vfSystemDir, vfUserDir);
		return tree;
	}
	
	private DataFileTreeView createDataFileTreeView(VirtualFile vfSystemDir, VirtualFile vfUserDir) {
		DataFileTreeView tree = new DataFileTreeView();
		tree.initialComponent(vfSystemDir, vfUserDir);
		return tree;
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
		return getSelectedTreeView().onProcessMenuSelection(command, source, action);
	}

	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		return getSelectedTreeView().onProcessMenuUpdate(command, source, action);
	}

	//------------------------------------------------------------
	// Action events
	//------------------------------------------------------------
	
	protected void onTabSelectionChanged(ChangeEvent ce) {
		//System.err.println("RunnerTreeTabView#onTabSelectionChanged()");
		if (getFrame().getMExecArgsEditDialog() == null) {
			getSelectedTreeView().requestFocusInComponent();
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
			if (!getFrame().isTreeViewActivated()) {
				int index = _tab.indexAtLocation(me.getX(), me.getY());
				int selected = _tab.getSelectedIndex();
				if ((index < 0) || (selected == index)) {
					getSelectedTreeView().requestFocusInComponent();
				}
			}
		}
	}
}
