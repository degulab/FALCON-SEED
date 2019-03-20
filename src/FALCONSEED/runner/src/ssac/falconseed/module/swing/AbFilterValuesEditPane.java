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
 * @(#)AbFilterValuesEditPane.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbFilterValuesEditPane.java	2.0.0	2012/10/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.file.swing.tree.DefaultFileTreeHandler;
import ssac.falconseed.file.swing.tree.DefaultFileTreeNode;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.setting.MExecDefHistory;
import ssac.falconseed.module.swing.tree.MEDLocalFileTreePanel;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.Objects;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.JMultilineLabel;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.menu.ExMenuItem;
import ssac.util.swing.menu.MenuItemResource;
import ssac.util.swing.tree.DnDTree;
import ssac.util.swing.tree.JTreePopupMenu;

/**
 * フィルタの実行時設定を編集するためのユーザーインタフェース。
 * このペインは、ファイルツリー、機能説明、引数値設定ペインの３つのペインで構成されている。
 * 
 * @version 3.1.0	2014/05/19
 * @since 2.0.0
 */
public abstract class AbFilterValuesEditPane extends JSplitPane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	static protected final String TREEMENU_ID_OPEN			= "open";
	static protected final String TREEMENU_ID_TYPEDOPEN		= "open.type";
	static protected final String TREEMENU_ID_TYPEDOPEN_CSV	= "open.type.csv";
	static protected final String TREEMENU_ID_COPY			= "copy";
	static protected final String TREEMENU_ID_REFRESH			= "refresh";
	
	private final FilterArgsFileTreeHandler _hTree = new FilterArgsFileTreeHandler();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	protected TreeContextMenuAction	_treeActionOpen;
	protected TreeContextMenuAction	_treeActionTypedOpen;
	protected TreeContextMenuAction	_treeActionTypedOpenCsv;
	protected TreeContextMenuAction	_treeActionCopy;
	protected TreeContextMenuAction	_treeActionRefresh;

	/**
	 * 機能説明を最も外側のペインとするフラグ
	 */
	private final boolean		_outerPaneIsDesc;
	/**
	 * 機能説明ラベルにフィルタ名を表示するフラグ
	 */
	private boolean _visibleFilterName;
	/**
	 * ダイアログハンドラ
	 */
	private IFilterDialogHandler	_handler = EmptyFilterDialogHandler.getInstance();

	/**
	 * 内部分割用スプリッター
	 */
	private JSplitPane	_spInner;

	/**
	 * フィルタの機能説明タイトルラベル
	 */
	private JLabel			_lblDescTitle;
	/**
	 *  フィルタの機能説明コンポーネント
	 */
	private JTextComponent			_lblDesc;
	/**
	 * ローカルファイルツリーコンポーネント
	 */
	private MEDLocalFileTreePanel	_pnlTree;
	/**
	 * 引数値編集コンポーネント
	 */
	private AbFilterArgValuesEditPane	_pnlValues;

	/**
	 * データモデル
	 */
	private IFilterValuesEditModel		_model;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbFilterValuesEditPane(boolean outerPaneIsDescription) {
		super();
		this._outerPaneIsDesc = outerPaneIsDescription;
		
		if (outerPaneIsDescription) {
			// 外側スプリッタは縦分割、引数コンポーネントは内側スプリッタの右側
			this.setOrientation(JSplitPane.VERTICAL_SPLIT);
			this.setResizeWeight(0);
			this._spInner = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			this._spInner.setResizeWeight(0);
			this.setBottomComponent(_spInner);
		} else {
			// 外側スプリッタは横分割、引数コンポーネントは内側スプリッタの下側
			this.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			this.setResizeWeight(0);
			this._spInner = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			this._spInner.setResizeWeight(0);
			this.setRightComponent(_spInner);
		}
	}
	
	public void initialComponent() {
		this._model = createDefaultDataModel();
		
		// create Sub components
		JPanel pnlDesc = createDescriptionPane();
		JPanel pnlTree = createTreePane();
		JPanel pnlVals = createValuesPane();
		
		// layout Sub components
		if (isOuterPaneDescription()) {
			// 外側スプリッタは縦分割、引数コンポーネントは内側スプリッタの右側
			this.setTopComponent(pnlDesc);
			this.setBottomComponent(_spInner);
			_spInner.setLeftComponent(pnlTree);
			_spInner.setRightComponent(pnlVals);
		} else {
			// 外側スプリッタは横分割、引数コンポーネントは内側スプリッタの下側
			this.setLeftComponent(pnlTree);
			this.setRightComponent(_spInner);
			_spInner.setTopComponent(pnlDesc);
			_spInner.setBottomComponent(pnlVals);
		}
		
		// ツリーコンポーネントの標準ショーカットキーアクション
		if (_treeActionCopy != null) {
			_pnlTree.getTreeComponent().setDefaultCopyAction(_treeActionCopy);
		}
		updateTreeContextMenu();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 現在のデータモデルに従い、引数の表示内容を更新する。
	 */
	public void refreshDisplayAllArguments() {
		_pnlValues.refreshDisplay();
	}

	/**
	 * 現在のデータモデルに従い、フィルタ名の表示内容を更新する
	 */
	public void refreshDisplayFilterName() {
		if (isVisibleFilterName() && _lblDescTitle != null && _lblDescTitle.isDisplayable()) {
			_lblDescTitle.setText(getDescriptionTitle());
		}
	}

	/**
	 * このコンポーネントに設定されているデータモデルを返す。
	 */
	public IFilterValuesEditModel getDataModel() {
		return _model;
	}

	/**
	 * このコンポーネントに新しいデータモデルを設定する。
	 * <em>newModel</em> がすでに設定されているデータモデルと等しいと判断された場合、
	 * このメソッドではデータモデルの置き換えは行わない。
	 * @param newModel	新しいモデルを設定する。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void setDataModel(IFilterValuesEditModel newModel) {
		if (!newModel.equals(_model)) {
			IFilterValuesEditModel oldModel = _model;
			_model = newModel;
			onChangedDataModel(oldModel, newModel);
		}
	}

	/**
	 * このコンポーネントを配置するオーナーダイアログを返す。
	 */
	public IFilterDialog getOwnerDialog() {
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof IFilterDialog) {
			return (IFilterDialog)w;
		} else {
			return null;
		}
	}

	/**
	 * 機能説明ラベルにフィルタ名を表示する場合に <tt>true</tt> を返す。
	 */
	public boolean isVisibleFilterName() {
		return _visibleFilterName;
	}

	/**
	 * 機能説明ラベルにフィルタ名を表示するかどうかを設定する。
	 * @param toVisible		表示する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public void setVisibleFilterName(boolean toVisible) {
		_visibleFilterName = toVisible;
		if (_lblDescTitle != null && _lblDescTitle.isDisplayable()) {
			_lblDescTitle.setText(getDescriptionTitle());
		}
	}

	/**
	 * 最も外側に位置するペインが機能説明である場合に <tt>true</tt> を返す。
	 * このメソッドが <tt>false</tt> を返す場合は、ファイルツリーが最も外側のペインとなる。
	 */
	public boolean isOuterPaneDescription() {
		return _outerPaneIsDesc;
	}

	/**
	 * 機能説明が配置されたスプリッタを返す。
	 */
	public JSplitPane getDescriptionSplitter() {
		if (isOuterPaneDescription()) {
			return this;
		} else {
			return _spInner;
		}
	}

	/**
	 * ファイルツリーが配置されたスプリッタを返す。
	 */
	public JSplitPane getTreeSplitter() {
		if (isOuterPaneDescription()) {
			return _spInner;
		} else {
			return this;
		}
	}

	/**
	 * 引数編集パネルが配置されたスプリッタを返す。
	 */
	public JSplitPane getValuesSplitter() {
		return _spInner;
	}

	/**
	 * 機能説明スプリッターの分割位置を取得する。
	 * @return	スプリッターの分割位置
	 */
	public int getDescriptionDividerLocation() {
		return getDescriptionSplitter().getDividerLocation();
	}

	/**
	 * 機能説明スプリッターの分割位置を設定する。
	 * @param location	新しい分割位置
	 */
	public void setDescriptionDividerLocation(int location) {
		getDescriptionSplitter().setDividerLocation(location);
	}

	/**
	 * ファイルツリースプリッターの分割位置を取得する。
	 * @return	スプリッターの分割位置
	 */
	public int getTreeDividerLocation() {
		return getTreeSplitter().getDividerLocation();
	}

	/**
	 * ファイルツリースプリッターの分割位置を設定する。
	 * @param location	新しい分割位置
	 */
	public void setTreeDividerLocation(int location) {
		getTreeSplitter().setDividerLocation(location);
	}

	/**
	 * 機能説明ペインを取得する。
	 * このメソッドが返すコンポーネントは、スプリッターに配置されたコンポーネントである。
	 * @return	ペインのコンポーネント、初期化されていない場合は <tt>null</tt>
	 */
	public JPanel getDescriptionPane() {
		return (JPanel)getDescriptionSplitter().getTopComponent();
	}

	/**
	 * ファイルツリーペインを取得する。
	 * このメソッドが返すコンポーネントは、スプリッターに配置されたコンポーネントである。
	 * @return	ペインのコンポーネント、初期化されていない場合は <tt>null</tt>
	 */
	public JPanel getTreePane() {
		return (JPanel)getTreeSplitter().getLeftComponent();
	}

	/**
	 * このコンポーネントのファイルツリーコンポーネントを返す。
	 * このメソッドが返すコンポーネントは、ツリーコンポーネントを内部に持つ
	 * コンポーネントペインである。
	 * @return	<code>MEDLocalFileTreePanel</code> オブジェクト
	 */
	public MEDLocalFileTreePanel getTreeComponent() {
		return _pnlTree;
	}

	/**
	 * 引数値編集ペインを取得する。
	 * このメソッドが返すコンポーネントは、スプリッターに配置されたコンポーネントである。
	 * @return	ペインのコンポーネント、初期化されていない場合は <tt>null</tt>
	 */
	public JPanel getValuesPane() {
		if (_outerPaneIsDesc) {
			// 引数コンポーネントは、内側スプリッターの右側
			return (JPanel)_spInner.getRightComponent();
		} else {
			// 引数コンポーネントは、内側スプリッターの下側
			return (JPanel)_spInner.getBottomComponent();
		}
	}

	/**
	 * 設定されているダイアログハンドラを取得する。
	 * @return	<code>IFilterDialogHandler</code> のインスタンス
	 */
	public IFilterDialogHandler getFilterDialogHandler() {
		return _handler;
	}

	/**
	 * 新しいダイアログハンドラを設定する。
	 * <em>newHandler</em> が <tt>null</tt> の場合、{@link EmptyFilterDialogHandler#getInstance()} が返す
	 * インスタンスが設定される。
	 * @param newHandler	<code>IFilterDialogHandler</code> のインスタンス
	 */
	public void setFilterDialogHandler(IFilterDialogHandler newHandler) {
		if (newHandler == null) {
			_handler = EmptyFilterDialogHandler.getInstance();
		} else {
			_handler = newHandler;
		}
	}

	/**
	 * ツリーコンテキストメニューの状態を更新する。
	 */
	protected void updateTreeContextMenu() {
		if (_treeActionOpen != null)
			_treeActionOpen.setEnabled(_pnlTree.canOpenFile());
		if (_treeActionTypedOpen != null)
			_treeActionTypedOpen.setEnabled(_pnlTree.canOpenFileByCSV());
		if (_treeActionTypedOpenCsv != null)
			_treeActionTypedOpenCsv.setEnabled(_pnlTree.canOpenFileByCSV());
		if (_treeActionCopy != null)
			_treeActionCopy   .setEnabled(_pnlTree.canCopy());
		if (_treeActionRefresh != null)
			_treeActionRefresh.setEnabled(true);
	}

	public void restoreConfiguration(ExConfiguration config, String prefix) {
		// restore divider location
		if (config != null) {
			// outer splitter
			int dl = config.getDividerLocation(prefix + ".FilterValuesEditPane.outer");
			if (dl > 0)
				this.setDividerLocation(dl);
			// inner splitter
			dl = config.getDividerLocation(prefix + ".FilterValuesEditPane.inner");
			if (dl > 0 && _spInner != null)
				_spInner.setDividerLocation(dl);
		}
	}

	public void storeConfiguration(ExConfiguration config, String prefix) {
		// store divider location
		if (config != null) {
			// outer splitter
			int dl = this.getDividerLocation();
			config.setDividerLocation(prefix + ".FilterValuesEditPane.outer", dl);
			// inner splitter
			if (_spInner != null) {
				dl = _spInner.getDividerLocation();
				config.setDividerLocation(prefix + ".FilterValuesEditPane.inner", dl);
			}
		}
	}

	/**
	 * 先頭引数が表示されるようにスクロールする。
	 */
	public void scrollTopArgumentToVisible() {
		getArgValuesEditPane().scrollTopToVisible();
	}

	/**
	 * 終端引数が表示されるようにスクロールする。
	 */
	public void scrollBottomArgumentToVisible() {
		getArgValuesEditPane().scrollBottomToVisible();
	}
	
	/**
	 * 指定された位置の引数が表示されるようにスクロールする。
	 * ただし、指定された位置のアイテムが非表示の場合や、インデックスが
	 * 無効な場合、このメソッドはなにもしない。
	 * @param argIndex	引数の位置を示すインデックス
	 */
	public void scrollArgumentToVisible(int argIndex) {
		getArgValuesEditPane().scrollItemToVisible(argIndex);
	}

	/**
	 * 指定された引数インデックスに対応する箇所に、入力フォーカスを設定する。
	 * インデックスが適切ではない場合、このコンポーネントにフォーカスを設定する。
	 * @param argIndex	引数インデックス
	 */
	public void setFocusToArgument(int argIndex) {
		getArgValuesEditPane().setFocusToArgument(argIndex);
	}

	//------------------------------------------------------------
	// Internal events
	//------------------------------------------------------------
	
	protected void refreshDisplayDescription() {
		_lblDesc.setText(_model.getDescription());
		_lblDesc.setCaretPosition(0);
	}
	
	protected void refreshDisplayFileTree() {
		VirtualFile vfRoot = _model.getExecDefLocalFileRootDirectory();
		_pnlTree.setRootDirectory(vfRoot);
		_pnlTree.getTreeComponent().clearSelection();
		updateTreeContextMenu();
	}

	/**
	 * このコンポーネントのデータモデルが変更されたときに呼び出される。
	 * このメソッドが呼び出された時点で、<code>getDataModel()</code> が返すインスタンスは、
	 * <em>newModel</em> と同一のものとなっている。
	 */
	protected void onChangedDataModel(IFilterValuesEditModel oldModel, IFilterValuesEditModel newModel) {
		refreshDisplayFilterName();
		refreshDisplayDescription();
		refreshDisplayFileTree();
		_pnlValues.setDataModel(newModel);
	}
	
	protected void onTreeSelectionChanged() {
		updateTreeContextMenu();
		_handler.onFileTreeSelectionChanged(_pnlTree);
	}

	protected void onTreeDoubleClicked(MouseEvent e) {
		// ダブルクリック時には、ファイルを開く
		//--- ダブルクリック後、マウスイベントの位置情報から対象パスを取得すると、
		//--- ツリーがスクロールした場合に想定していない位置が取得され、そのファイルを開くことに
		//--- なってしまうので、ダブルクリック後は単一選択になることを前提に、
		//--- 位置から取得したパスと、選択されているパスが一致した場合のみ、
		//--- そのパスのファイルを開く
		TreePath pathByPos = _pnlTree.getTreeComponent().getPathForLocation(e.getX(), e.getY());
		TreePath pathBySel = _pnlTree.getTreeComponent().getSelectionPath();	// ダブルクリック後は単一選択を前提
		if (pathByPos == null || !Objects.isEqual(pathByPos, pathBySel))
			return;		// no target node
		
		// ノードの取得
		DefaultFileTreeNode node = (DefaultFileTreeNode)pathByPos.getLastPathComponent();
		if (node.isDirectory())
			return;		// no file
		
		// ファイルを開く
		_handler.doOpenFileOnEditor(getOwnerDialog(), node.getFileObject());
	}
	
	protected void onTreeMenuActionPerformed(ActionEvent ae) {
		String commandKey = ae.getActionCommand();
		if (TREEMENU_ID_OPEN.equals(commandKey)) {
			// open
			AppLogger.debug("AbFilterValuesEditPane : context menu [Open] selected.");
			if (!_pnlTree.canOpenFile()) {
				_treeActionOpen.setEnabled(false);
				return;
			}
			VirtualFile[] files = _pnlTree.getSelectionFiles();
			if (files != null && files.length > 0) {
				for (VirtualFile vf : files) {
					if (vf != null) {
						_handler.doOpenFileOnEditor(getOwnerDialog(), vf);
					}
				}
			}
		}
		else if (TREEMENU_ID_TYPEDOPEN_CSV.equals(commandKey)) {
			// typed open - csv
			AppLogger.debug("AbFilterValuesEditPane : context menu [Typed open]-[csv] selected.");
			if (!_pnlTree.canOpenFileByCSV()) {
				_treeActionTypedOpenCsv.setEnabled(false);
				_treeActionTypedOpen.setEnabled(false);
				return;
			}
			VirtualFile file = _pnlTree.getSelectionFile();
			if (file != null) {
				_handler.doOpenFileByCsvOnEditor(getOwnerDialog(), file);
			}
		}
		else if (TREEMENU_ID_COPY.equals(commandKey)) {
			// copy
			AppLogger.debug("AbFilterValuesEditPane : context menu [Copy] selected.");
			if (!_pnlTree.canCopy()) {
				_treeActionCopy.setEnabled(false);
				return;
			}
			_pnlTree.doCopy();
			//_treeActionPaste.setEnabled(_paneTree.canPaste());
			_pnlTree.requestFocusInComponent();
		}
		else if (TREEMENU_ID_REFRESH.equals(commandKey)) {
			// refresh
			AppLogger.debug("AbFilterValuesEditPane : context menu [Refresh] selected.");
			if (_pnlTree.isSelectionEmpty()) {
				_pnlTree.refreshAllTree();
			} else {
				_pnlTree.refreshSelectedTree();
			}
			updateTreeContextMenu();
			_pnlTree.requestFocusInComponent();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 引数値編集パネルを返す。
	 */
	protected AbFilterArgValuesEditPane getArgValuesEditPane() {
		return _pnlValues;
	}

	/**
	 * 指定された履歴情報の内容をコミットする。
	 */
	protected void commitArgsHistory(final MExecDefHistory history) {
		history.ensureMaxSize(AppSettings.getInstance().getHistoryMaxLength());
		try {
			history.commit();
		}
		catch (Throwable ex) {
			AppLogger.error("Failed to save to MED History file.\nFile : \"" + history.getVirtualPropertyFile().getAbsolutePath() + "\"", ex);
		}
	}

	/**
	 * フィルタ名のこのコンポーネントにおける表示名を取得する。
	 * @return	フィルタ表示名を返す。フィルタが指定されていない場合は <tt>null</tt> を返す。
	 */
	public String getFormattedFilterName() {
		ModuleRuntimeData data = _model.getModuleData();
		if (data != null) {
			long rno = data.getRunNo();
			if (rno != 0L) {
				return String.format("[%d] %s", rno, data.getName());
			} else {
				return data.getName();
			}
		}
		else {
			return null;
		}
	}

	/**
	 * 機能説明タイトルに表示する文字列を取得する。
	 * @return	機能説明タイトルとする文字列
	 */
	protected String getDescriptionTitle() {
		if (_visibleFilterName) {
			String strName = getFormattedFilterName();
			if (strName != null) {
				return RunnerMessages.getInstance().MExecDefEditDlg_Label_Desc + " : " + strName;
			}
			else {
				return RunnerMessages.getInstance().MExecDefEditDlg_Label_Desc;
			}
		}
		else {
			return RunnerMessages.getInstance().MExecDefEditDlg_Label_Desc;
		}
	}

	/**
	 * このコンポーネント標準のデータモデルを生成する。
	 */
	protected IFilterValuesEditModel createDefaultDataModel() {
		return new EmptyFilterValuesEditModel();
	}

	/**
	 * 各ペインのボーダー
	 * @return <code>Border</code> オブジェクト
	 */
	protected Border createPaneBorder() {
		return BorderFactory.createEmptyBorder(3, 3, 3, 3);		
	}

	/**
	 * 機能説明用テキストコンポーネントの生成
	 * @return <code>JTextComponent</code> オブジェクト
	 */
	protected JTextComponent createDescriptionLabel() {
		JMultilineLabel label = new JMultilineLabel();
		return label;
	}

	/**
	 * ファイルツリーコンポーネントの生成
	 * @return	<code>MEDLocalFileTreePanel</code> オブジェクト
	 */
	protected MEDLocalFileTreePanel createFileTreePanel() {
		MEDLocalFileTreePanel ptree = new MEDLocalFileTreePanel(){
			@Override
			protected void onTreeDoubleClicked(MouseEvent e) {
				AbFilterValuesEditPane.this.onTreeDoubleClicked(e);
			}

			@Override
			protected void onTreeSelectionAdjusted() {
				AbFilterValuesEditPane.this.onTreeSelectionChanged();
			}
		};
		ptree.initialComponent();
		ptree.getTreeComponent().setTreeHandler(_hTree);
		return ptree;
	}
	
	protected void createTreeContextMenuActions() {
		// create actions
		_treeActionOpen		= new TreeContextMenuAction(TREEMENU_ID_OPEN,
										RunnerMessages.getInstance().menuTreeFileOpen,
										CommonResources.ICON_BLANK,
										null,
										null,
										KeyEvent.VK_O,
										null);
		_treeActionTypedOpen	= new TreeContextMenuAction(TREEMENU_ID_TYPEDOPEN,
										RunnerMessages.getInstance().menuTreeFileOpenTyped,
										CommonResources.ICON_BLANK,
										null,
										null,
										KeyEvent.VK_T,
										null);
		_treeActionTypedOpenCsv	= new TreeContextMenuAction(TREEMENU_ID_TYPEDOPEN_CSV,
										RunnerMessages.getInstance().menuTreeFileOpenTypedCsv,
										null,
										null,
										null,
										KeyEvent.VK_C,
										null);
		_treeActionCopy		= new TreeContextMenuAction(TREEMENU_ID_COPY,
										RunnerMessages.getInstance().menuEditCopy,
										CommonResources.ICON_COPY,
										RunnerMessages.getInstance().tipEditCopy,
										null,
										KeyEvent.VK_C,
										MenuItemResource.getEditCopyShortcutKeyStroke());
		_treeActionRefresh	= new TreeContextMenuAction(TREEMENU_ID_REFRESH,
										RunnerMessages.getInstance().menuFileRefresh,
										CommonResources.ICON_REFRESH,
										null,
										null,
										KeyEvent.VK_F,
										null);
	}
	
	protected JTreePopupMenu createTreeContextMenu() {

		// create Menu component
		JMenuItem item;
		JTreePopupMenu menu = new JTreePopupMenu();
		//--- open
		item = new ExMenuItem(true, false, _treeActionOpen);
		menu.add(item);
		//--- typed open
		JMenu typedOpenMenu = new JMenu(_treeActionTypedOpen);
		{
			//--- csv
			item = new ExMenuItem(true, false, _treeActionTypedOpenCsv);
			typedOpenMenu.add(item);
		}
		menu.add(typedOpenMenu);
		//---
		menu.addSeparator();
		//--- copy
		item = new ExMenuItem(true, false, _treeActionCopy);
		menu.add(item);
		//---
		menu.addSeparator();
		//--- refresh
		item = new ExMenuItem(true, false, _treeActionRefresh);
		menu.add(item);
		
		return menu;
	}
	
	/**
	 * 機能説明ペインの生成
	 * @return	<code>JPanel</code> オブジェクト
	 */
	protected JPanel createDescriptionPane() {
		// create text component for Description
		this._lblDescTitle = new JLabel(getDescriptionTitle());
		this._lblDesc = createDescriptionLabel();
		
		// create panel
		JPanel panel = new JPanel(new BorderLayout());
		JScrollPane scDesc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scDesc.setViewportView(_lblDesc);
		scDesc.setBackground(panel.getBackground());
		panel.setBorder(createPaneBorder());
		panel.add(scDesc, BorderLayout.CENTER);
		panel.add(_lblDescTitle, BorderLayout.NORTH);

		Dimension dmMin = panel.getMinimumSize();
		dmMin.height = 50;
		panel.setMinimumSize(dmMin);
		
		return panel;
	}

	/**
	 * ファイルツリーペインの生成
	 * @return <code>JPanel</code> オブジェクト
	 */
	protected JPanel createTreePane() {
		// create component for Tree
		this._pnlTree = createFileTreePanel();
		_pnlTree.setMinimumSize(new Dimension(50,50));
		createTreeContextMenuActions();
		_pnlTree.setTreeContextMenu(createTreeContextMenu());
		_pnlTree.setBorder(createPaneBorder());
		_pnlTree.add(new JLabel(CommonMessages.getInstance().labelFile), BorderLayout.NORTH);
		
		return _pnlTree;
	}

	/**
	 * 引数値編集パネルを生成する。
	 * @return	<code>AbFilterArgValuesEditPane</code> オブジェクト
	 */
	abstract protected AbFilterArgValuesEditPane createArgValuesEditPanel();

	/**
	 * 引数値編集ペインの生成
	 * @return	<code>JPanel</code> オブジェクト
	 */
	protected JPanel createValuesPane() {
		// create sub components
		this._pnlValues = createArgValuesEditPanel();
		_pnlValues.setMinimumSize(new Dimension(50,50));
		_pnlValues.setBorder(createPaneBorder());
		
		return _pnlValues;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class TreeContextMenuAction extends AbMenuItemAction
	{

		public TreeContextMenuAction() {
			super();
		}

		public TreeContextMenuAction(String name) {
			super(name);
		}

		public TreeContextMenuAction(MenuItemResource mir) {
			super(mir);
		}

		public TreeContextMenuAction(String name, Icon icon) {
			super(name, icon);
		}

		public TreeContextMenuAction(String actionCommand, String name,
				Icon icon, String tooltip, String description, int mnemonic,
				KeyStroke keyStroke) {
			super(actionCommand, name, icon, tooltip, description, mnemonic, keyStroke);
		}
		
		public void actionPerformed(ActionEvent e) {
			onTreeMenuActionPerformed(e);
		}
	}

	/**
	 * ツリーコンポーネントの制御を行う標準のハンドラ。
	 * このコンポーネントでは、コピーのドラッグのみを許可する。
	 */
	static protected class FilterArgsFileTreeHandler extends DefaultFileTreeHandler
	{
		@Override
		public int acceptTreeDragOverDropAction(DnDTree tree, DropTargetDragEvent dtde, TreePath dragOverPath) {
			// ドロップ処理は未実装
			return TransferHandler.NONE;
		}

		@Override
		public boolean dropTransferData(DnDTree tree, Transferable t, int action) {
			// ドロップ処理は未実装
			return false;
		}

		@Override
		public int getTransferSourceAction(DnDTree tree) {
			//--- コピーのみ許可
			int action;
			TreePath[] paths = tree.getSelectionPaths();
			if (paths != null && paths.length > 0) {
				// コピー操作のみ許可
				action = TransferHandler.COPY;
			} else {
				action = TransferHandler.NONE;
			}
			return action;
		}
	}
}
