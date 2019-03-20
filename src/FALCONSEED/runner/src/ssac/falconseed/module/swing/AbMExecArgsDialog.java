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
 * @(#)AbMExecArgsDialog.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbMExecArgsDialog.java	3.0.0	2014/03/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbMExecArgsDialog.java	1.22	2012/08/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbMExecArgsDialog.java	1.20	2012/03/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbMExecArgsDialog.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbMExecArgsDialog.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.editor.view.ActiveViewManager;
import ssac.falconseed.file.DefaultVirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.file.swing.tree.DefaultFileTreeHandler;
import ssac.falconseed.file.swing.tree.DefaultFileTreeNode;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.setting.MExecDefHistory;
import ssac.falconseed.module.swing.tree.MEDLocalFileTreePanel;
import ssac.falconseed.module.swing.tree.MEDLocalFileTreePanel.FileTreeStatusBar;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.Objects;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.JMaskedNumberSpinner;
import ssac.util.swing.JMultilineLabel;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.menu.ExMenuItem;
import ssac.util.swing.menu.MenuItemResource;
import ssac.util.swing.tree.DnDTree;
import ssac.util.swing.tree.JTreePopupMenu;
import dtalge.util.Strings;

/**
 * モジュール実行時引数設定/表示ダイアログの共通機能
 * 
 * @version 3.1.0	2014/05/19
 */
public abstract class AbMExecArgsDialog extends AbBasicDialog implements IMExecArgsDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -1139441568620123347L;

	//	static private final int FIXED_DESC_HEIGHT = 150;
	static private final Dimension DM_MIN_SIZE = new Dimension(640, 480);

	static protected final String TREEMENU_ID_OPEN		= "open";
	static protected final String TREEMENU_ID_TYPEDOPEN	= "open.type";
	static protected final String TREEMENU_ID_TYPEDOPEN_CSV	= "open.type.csv";
	static protected final String TREEMENU_ID_COPY		= "copy";
	static protected final String TREEMENU_ID_REFRESH	= "refresh";
	
	static protected final MExecArgsDialogHandler EmptyHandler = EmptyMExecArgsDialogHandler.getInstance();
	
	private final MExecArgsFileTreeHandler _hTree = new MExecArgsFileTreeHandler();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	protected TreeContextMenuAction	_treeActionOpen;
	protected TreeContextMenuAction	_treeActionTypedOpen;
	protected TreeContextMenuAction	_treeActionTypedOpenCsv;
	protected TreeContextMenuAction	_treeActionCopy;
	protected TreeContextMenuAction	_treeActionRefresh;

	/**
	 * 表示用パス文字列フォーマッター
	 * @since 1.20
	 */
	private VirtualFilePathFormatterList	_vfFormatter;
	/**
	 * ローカルファイルツリーパネル
	 * @since 1.20
	 */
	private MEDLocalFileTreePanel	_paneTree;
	/**
	 * ツリー配置用スプリッター
	 * @since 1.20
	 */
	private JSplitPane	_paneTreeSplitter;
	/**
	 * 外側のスプリッター
	 */
	private JSplitPane	_paneOuterSplitter;
	/**
	 * ファイル情報用ステータスバー
	 * @since 1.20
	 */
	private FileTreeStatusBar	_statusbar;
	/**
	 * 履歴ラベル
	 * @since 1.20
	 */
	private JLabel		_lblHistory;
	/**
	 * 履歴消去ボタン
	 * @since 1.20
	 */
	private JButton	_btnClearHistory;
	/**
	 * 履歴番号
	 * @since 1.20
	 */
	private JMaskedNumberSpinner	_spnHistoryNo;
	
//	/**
//	 * 編集許可フラグ
//	 * @since 1.20
//	 */
//	private boolean	_editable = true;
	
	/** 引数設定のモデル **/
	private final MExecArgsModel	_model;
	/**
	 * 引数設定の履歴
	 * @since 1.20
	 */
	private final MExecDefHistory	_history;

	/** モジュール実行定義の説明 **/
	private JTextComponent		_lblDesc;
	/** 引数設定ペイン **/
	private MExecArgsEditPane	_argsPane;
	/**
	 * 実行開始時にコンソールを表示する設定
	 * @since 3.0.0
	 */
	protected JCheckBox	_chkConsoleShowAtStart;
	/**
	 * 実行終了時にコンソールを閉じる設定
	 * @since 3.0.0
	 */
	protected JCheckBox	_chkConsoleAutoClose;
	
	/**
	 * ダイアログハンドラ
	 * @since 1.20
	 */
	private MExecArgsDialogHandler	_handler = EmptyHandler;
	/**
	 * アクティブなビューを管理するマネージャ
	 * @since 1.20
	 */
	private ActiveViewManager _viewManager = new ActiveViewManager();
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbMExecArgsDialog(Frame owner, String title, boolean modal, MExecArgsModel argsmodel, MExecDefHistory argshistory) {
		super(owner, title, modal);
		if (argsmodel == null)
			throw new NullPointerException("The specified arguments model is null.");
		this._model = argsmodel;
		this._history = argshistory;
	}
	
	public AbMExecArgsDialog(Dialog owner, String title, boolean modal, MExecArgsModel argsmodel, MExecDefHistory argshistory) {
		super(owner, title, modal);
		if (argsmodel == null)
			throw new NullPointerException("The specified arguments model is null.");
		this._model = argsmodel;
		this._history = argshistory;
	}
	
	@Override
	public void initialComponent() {
		// 基準パスの設定
		VirtualFile vfMEDdir = _model.getSettings().getExecDefDirectory();
		VirtualFile vfFilesRootDir = vfMEDdir.getChildFile(MExecDefFileManager.MEXECDEF_FILES_ROOT_DIRNAME);
		getArgsEditPane().setBasePath(vfMEDdir.getParentFile());
		//--- create path formatter
		_vfFormatter = new VirtualFilePathFormatterList();
		//--- local files root
		_vfFormatter.add(new DefaultVirtualFilePathFormatter(vfFilesRootDir, null));
		//--- Module Execution Definition
		_vfFormatter.add(new DefaultVirtualFilePathFormatter(vfMEDdir, null));
		
		// コンポーネントの初期化
		super.initialComponent();
		
		// ツリーの初期化
		_paneTree.setRootDirectory(vfFilesRootDir);
		//--- 初期状態ではルートノードを閉じた状態とする
		//_paneTree.getTreeComponent().collapseRow(0);
		
		// 最初の履歴を表示
		if (_history != null && !_history.isHistoryEmpty()) {
			_argsPane.setHistory(_history.getHistory(0));
		}
		
		// 設定情報の反映
		restoreConfiguration();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * コンソールを実行開始時に表示するかどうかの設定を取得する。
	 * 編集時以外では <tt>false</tt> を返す。
	 * @since 3.0.0
	 */
	public boolean isConsoleShowAtStart() {
		return (_chkConsoleShowAtStart==null ? false : _chkConsoleShowAtStart.isSelected());
	}

	/**
	 * コンソールを実行終了時に閉じるかどうかの設定を取得する。
	 * 編集時以外では <tt>false</tt> を返す。
	 * @since 3.0.0
	 */
	public boolean isConsoleAutoClose() {
		return (_chkConsoleAutoClose==null ? false : _chkConsoleAutoClose.isSelected());
	}

	/**
	 * このコンポーネントに設定された、エディタビューで表示されているファイルの抽象パスを返す。
	 * 設定されていない場合は <tt>null</tt> を返す。
	 * @since 1.20
	 */
	public VirtualFile getViewingFileOnEditor() {
		return _argsPane.getViewingFileOnEditor();
	}

	/**
	 * このコンポーネントに、エディタビューで表示されているファイルの抽象パスを設定する。
	 * @param newFile	設定する抽象パス。抽象パスを設定しない場合は <tt>null</tt>
	 * @since 1.20
	 */
	public void setViewingFileOnEditor(VirtualFile newFile) {
		_argsPane.setViewingFileOnEditor(newFile);
	}

	/**
	 * このダイアログに設定されたモデルの、モジュール実行定義のパスを返す。
	 * @since 1.20
	 */
	public String getMExecDefPath() {
		return _model.getSettings().getExecDefDirectory().getPath();
	}

	/**
	 * このダイアログに設定されたモデルの、モジュール実行定義名を返す。
	 * @since 1.20
	 */
	public String getMExecDefName() {
		return _model.getSettings().getExecDefDirectory().getName();
	}

	/**
	 * このダイアログに設定された引数モデルを返す。
	 * @return	<code>MExecArgsModel</code> オブジェクト
	 * @since 1.20
	 */
	public MExecArgsModel getArgsModel() {
		return _model;
	}

	/**
	 * このダイアログに設定された引数設定履歴を返す。
	 * @return	<code>MExecDefHistory</code> オブジェクト、設定されていない場合は <tt>null</tt>
	 * @since 1.20
	 */
	public MExecDefHistory getArgsHistory() {
		return _history;
	}

	/**
	 * 設定されているダイアログハンドラを返す。
	 * @return	ハンドラが設定されている場合はそのオブジェクト、それ以外の場合は <tt>null</tt>
	 * @since 1.20
	 */
	public MExecArgsDialogHandler getDialogHandler() {
		if (_handler == EmptyHandler)
			return null;
		else
			return _handler;
	}

	/**
	 * このダイアログオブジェクトに、新しいダイアログハンドラを設定する。
	 * @param newHandler	設定するハンドラ、無効にする場合は <tt>null</tt>
	 * @since 1.20
	 */
	public void setDialogHandler(final MExecArgsDialogHandler newHandler) {
		_handler = (newHandler==null ? EmptyHandler : newHandler);
	}

//	/**
//	 * このダイアログで編集が許可されている場合に <tt>true</tt> を返す。
//	 * @since 1.20
//	 */
//	public boolean isEditable() {
//		return _editable;
//	}
//
//	/**
//	 * このダイアログで編集を許可するかどうかを設定する。
//	 * @param allowEdit	編集可能とする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 1.20
//	 */
//	public void setEditable(boolean allowEdit) {
//		if (_editable != allowEdit) {
//			_editable = allowEdit;
//		}
//	}

	@Override
	public void restoreConfiguration() {
		super.restoreConfiguration();
		
		// restore divider location
		if (_paneTreeSplitter != null) {
			int dl = getConfiguration().getDividerLocation(getConfigurationPrefix()+"tree");
			if (dl > 0) {
				_paneTreeSplitter.setDividerLocation(dl);
			}
		}
		if (_paneOuterSplitter != null) {
			int dl = getConfiguration().getDividerLocation(getConfigurationPrefix()+"outer");
			if (dl > 0) {
				_paneOuterSplitter.setDividerLocation(dl);
			}
		}
	}

	@Override
	public void storeConfiguration() {
		super.storeConfiguration();
		
		// store current divider location
		if (_paneTreeSplitter != null) {
			int dl = _paneTreeSplitter.getDividerLocation();
			getConfiguration().setDividerLocation(getConfigurationPrefix()+"tree", dl);
		}
		if (_paneOuterSplitter != null) {
			int dl = _paneOuterSplitter.getDividerLocation();
			getConfiguration().setDividerLocation(getConfigurationPrefix()+"outer", dl);
		}
	}

	/**
	 * ダイアログが表示されている場合はダイアログを閉じ、リソースを開放する。
	 * このメソッドでは、{@link java.awt.Window#dispose()} を呼び出す。
	 * @since 1.20
	 */
	public void destroy() {
		if (this.isDisplayable()) {
			dialogClose(DialogResult_Cancel);
		}
	}
	
	@Override
	protected boolean doCancelAction() {
		return _handler.canCloseDialog(this);
	}

	@Override
	protected boolean doOkAction() {
		return _handler.canCloseDialog(this);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected String formatFilePath(VirtualFile file) {
		if (!file.isAbsolute()) {
			file = file.getAbsoluteFile();
		}
		
		if (_vfFormatter != null) {
			String path = _vfFormatter.formatPath(file);
			if (path != null)
				return path;
		}
		
		return file.getPath();
	}
	
	protected MExecArgsEditPane getArgsEditPane() {
		if (_argsPane == null) {
			_argsPane = createArgsEditPane();
		}
		return _argsPane;
	}
	
	protected JTextComponent createDescriptionLabel() {
		JMultilineLabel label = new JMultilineLabel();
		return label;
	}
	
	protected MExecArgsEditPane createArgsEditPane() {
		MExecArgsEditPane pane = new MExecArgsEditPane();
		return pane;
	}
	
	protected MEDLocalFileTreePanel createFileTreePanel() {
		MEDLocalFileTreePanel ptree = new MEDLocalFileTreePanel(){
			@Override
			protected void onTreeDoubleClicked(MouseEvent e) {
				AbMExecArgsDialog.this.onTreeDoubleClicked(e);
			}

			@Override
			protected void onTreeSelectionAdjusted() {
				AbMExecArgsDialog.this.onTreeSelectionChanged();
			}
		};
		ptree.initialComponent();
		ptree.getTreeComponent().setTreeHandler(_hTree);
		return ptree;
	}
	
	protected FileTreeStatusBar createStatusBar() {
		FileTreeStatusBar statusbar = new FileTreeStatusBar();
		statusbar.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		return statusbar;
	}
	
	protected void updateTreeContextMenu() {
		if (_treeActionOpen != null)
			_treeActionOpen.setEnabled(_paneTree.canOpenFile());
		if (_treeActionTypedOpen != null)
			_treeActionTypedOpen.setEnabled(_paneTree.canOpenFileByCSV());
		if (_treeActionTypedOpenCsv != null)
			_treeActionTypedOpenCsv.setEnabled(_paneTree.canOpenFileByCSV());
		if (_treeActionCopy != null)
			_treeActionCopy   .setEnabled(_paneTree.canCopy());
		if (_treeActionRefresh != null)
			_treeActionRefresh.setEnabled(true);
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
	
	protected JPanel createArgsTitlePanel() {
		// create components
		JLabel lblTitle = new JLabel(RunnerMessages.getInstance().MExecDefEditDlg_Label_Args);
		_lblHistory = new JLabel(RunnerMessages.getInstance().MExecArgsEditDlg_Label_History);
		_btnClearHistory = CommonResources.createIconButton(CommonResources.ICON_CONSOLE_CLEAR, RunnerMessages.getInstance().MEXecArgsEditDlg_Tooltip_HistoryAllClear);
		_spnHistoryNo = new JMaskedNumberSpinner("#0", 1, 1, AppSettings.getInstance().getHistoryMaxLengthLimit(), 1);
		
		// 履歴の初期化
		if (_history == null) {
			_spnHistoryNo = new JMaskedNumberSpinner("#0", 0, 0, 0, 1);
			_spnHistoryNo.setVisible(false);
			_spnHistoryNo.setEnabled(false);
			_btnClearHistory.setVisible(false);
			_btnClearHistory.setEnabled(false);
			_lblHistory.setVisible(false);
			_lblHistory.setEnabled(false);
		}
		else if (_history.isHistoryEmpty()) {
			_spnHistoryNo = new JMaskedNumberSpinner("#0", 0, 0, 0, 1);
			_spnHistoryNo.setEnabled(false);
			_btnClearHistory.setEnabled(false);
			_lblHistory.setEnabled(false);
		}
		else {
			_spnHistoryNo = new JMaskedNumberSpinner("#0", 1, 1, _history.getNumHistory(), 1);
		}
		//--- setup minimum size;
		Dimension dim = _spnHistoryNo.getPreferredSize();
		if (dim.width < 80) {
			dim.width = 80;
		}
		_spnHistoryNo.setPreferredSize(dim);
		_spnHistoryNo.setMinimumSize(dim);
		
		// layout
		JPanel pnl = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill   = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- title
		pnl.add(lblTitle, gbc);
		//--- dummy
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnl.add(new JLabel(), gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		//--- label
		gbc.gridx++;
		pnl.add(_lblHistory, gbc);
		//--- clear button
		gbc.gridx++;
		pnl.add(_btnClearHistory, gbc);
		//--- field
		gbc.gridx++;
		pnl.add(_spnHistoryNo, gbc);
		
		// actions
		//--- history clear button
		_btnClearHistory.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onHistoryAllClearButton();
			}
		});
		//--- history spin action
		_spnHistoryNo.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				onHistoryNumberChanged();
			}
		});
		
		return pnl;
	}
	
	protected JPanel createMainPanel() {
		// create local components
		this._lblDesc = createDescriptionLabel();
		MExecArgsEditPane argsPane = getArgsEditPane();
		_paneTree = createFileTreePanel();
		_statusbar = createStatusBar();
		_paneTreeSplitter = new JSplitPane();
		_paneOuterSplitter = new JSplitPane();
		_paneTree.setMinimumSize(new Dimension(50,50));
		//--- tree context menu
		createTreeContextMenuActions();
		_paneTree.setTreeContextMenu(createTreeContextMenu());
		
		// setup data
		_lblDesc.setText(_model.getSettings().getDescription());
		_lblDesc.setCaretPosition(0);
		argsPane.setModel(_model);
		
		// desc scroll pane
		JScrollPane scDesc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
											JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scDesc.setViewportView(_lblDesc);
//		{
//			Border scBorder;
//			Border inBorder;
//			if (scDesc.getBorder() != null) {
//				inBorder = BorderFactory.createCompoundBorder(
//								BorderFactory.createEmptyBorder(3, 5, 3, 5),
//								scDesc.getBorder());
//			} else {
//				inBorder = BorderFactory.createEmptyBorder(3, 5, 3, 5);
//			}
//			scBorder = BorderFactory.createCompoundBorder(
//							BorderFactory.createTitledBorder(RunnerMessages.getInstance().MExecDefEditDlg_Label_Desc),
//							inBorder);
//			scDesc.setBorder(scBorder);
//		}
//		scDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIXED_DESC_HEIGHT));
//		scDesc.setMinimumSize(new Dimension(50, 50));
		
		
		// args scroll pane
		JScrollPane scArgs = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
											JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scArgs.setViewportView(argsPane);
		{
//			Border scBorder;
//			Border inBorder;
//			if (scArgs.getBorder() != null) {
//				inBorder = BorderFactory.createCompoundBorder(
//								BorderFactory.createEmptyBorder(3, 5, 3, 5),
//								scArgs.getBorder());
//			} else {
//				inBorder = BorderFactory.createEmptyBorder(3, 5, 3, 5);
//			}
//			scBorder = BorderFactory.createCompoundBorder(
//							BorderFactory.createTitledBorder(RunnerMessages.getInstance().MExecDefEditDlg_Label_Args),
//							inBorder);
//			scArgs.setBorder(scBorder);
		}
		
//		// args panel
//		JPanel argsPanel = new JPanel(new GridBagLayout());
//		argsPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 0, 5));
//		//--- set background color for scroll pane
//		scroll.setBackground(argsPanel.getBackground());
//		//--- layout
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		gbc.gridwidth = 1;
//		gbc.gridheight = 1;
//		gbc.anchor = GridBagConstraints.NORTHWEST;
//		//--- desc
//		gbc.weightx = 1;
//		gbc.weighty = 0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		argsPanel.add(scDesc, gbc);
//		gbc.gridy++;
//		//--- args
//		gbc.weightx = 1;
//		gbc.weighty = 1;
//		gbc.fill = GridBagConstraints.BOTH;
//		argsPanel.add(scroll, gbc);
//		gbc.gridy++;
//		
//		// main panel
//		JPanel mainPanel = new JPanel(new BorderLayout());
//		_paneSplitter.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
//		_paneSplitter.setResizeWeight(0);
//		_paneSplitter.setLeftComponent(_paneTree);
//		_paneSplitter.setRightComponent(argsPanel);
//		mainPanel.add(_paneSplitter, BorderLayout.CENTER);
//		mainPanel.add(_statusbar, BorderLayout.SOUTH);
		
		// args label panel
		JPanel pnlArgsLabel = createArgsTitlePanel();	// with history controls

		// main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel pnlDesc = new JPanel(new BorderLayout());
		JPanel pnlArgs = new JPanel(new BorderLayout());
		Border pnlBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		pnlDesc.setBorder(pnlBorder);
		pnlArgs.setBorder(pnlBorder);
		_paneTree.setBorder(pnlBorder);
		scDesc.setBackground(mainPanel.getBackground());
		scArgs.setBackground(mainPanel.getBackground());
		pnlDesc.add(scDesc, BorderLayout.CENTER);
		pnlDesc.add(new JLabel(RunnerMessages.getInstance().MExecDefEditDlg_Label_Desc), BorderLayout.NORTH);
		pnlArgs.add(scArgs, BorderLayout.CENTER);
		pnlArgs.add(pnlArgsLabel, BorderLayout.NORTH);
		_paneTree.add(new JLabel(CommonMessages.getInstance().labelFile), BorderLayout.NORTH);
		//--- active view manager
		_viewManager.setKeepLastActivation(false);	// アクティブなコンポーネントが存在しないなら、アクティブボーダーは表示しない
		_viewManager.registerComponent(pnlDesc);
		_viewManager.registerComponent(_paneTree);
		_viewManager.registerComponent(pnlArgs);
		//--- tree & args
		_paneTreeSplitter.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		_paneTreeSplitter.setResizeWeight(0);
		_paneTreeSplitter.setLeftComponent(_paneTree);
		_paneTreeSplitter.setRightComponent(pnlArgs);
		//--- desc & tree splitter
		_paneOuterSplitter.setOrientation(JSplitPane.VERTICAL_SPLIT);
		_paneOuterSplitter.setResizeWeight(0);
		_paneOuterSplitter.setTopComponent(pnlDesc);
		_paneOuterSplitter.setBottomComponent(_paneTreeSplitter);
		//--- main panel layout
		mainPanel.add(_paneOuterSplitter, BorderLayout.CENTER);
		mainPanel.add(_statusbar, BorderLayout.SOUTH);

		return mainPanel;
	}

	@Override
	protected void setupMainContents() {
		JPanel mainPanel = createMainPanel();
		
		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected JButton createApplyButton() {
		// no apply button
		return null;
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

	@Override
	protected void setupActions() {
		super.setupActions();
		
		// ツリーコンポーネントの標準ショーカットキーアクション
		if (_treeActionCopy != null) {
			_paneTree.getTreeComponent().setDefaultCopyAction(_treeActionCopy);
		}
	}
	
	protected void storeArgsHistory() {
		if (_history == null)
			return;		// 履歴の保存先が存在しない
		if (_model.isItemEmpty())
			return;		// 引数が存在しない
		
		// 履歴データを作成
		ArrayList<ModuleArgData> hitem = new ArrayList<ModuleArgData>(_model.getNumItems());
		for (int index = 0; index < _model.getNumItems(); index++) {
			MExecArgItemModel item = _model.getItem(index);
			ModuleArgData hdata = new ModuleArgData(item.getType(), item.getDescription());
			if (ModuleArgType.OUT == item.getType()) {
				if (item.isOutToTempEnabled()) {
					String tempprefix = item.getTempFilePrefix();
					if (!Strings.isNullOrEmpty(tempprefix)) {
						// プレフィックス付きのテンポラリ出力指定
						hdata.setValue(tempprefix);
					} else {
						// プレフィックスなしのテンポラリ出力指定
						hdata.setValue(MExecArgTempFile.instance);
					}
				} else {
					hdata.setValue(item.getValue());
				}
			}
			else {
				hdata.setValue(item.getValue());
			}
			hitem.add(hdata);
		}
		
		// 新しい履歴を保存
		_history.addHistory(hitem);
		commitArgsHistory();
	}
	
	protected void commitArgsHistory() {
		_history.ensureMaxSize(AppSettings.getInstance().getHistoryMaxLength());
		try {
			_history.commit();
		}
		catch (Throwable ex) {
			AppLogger.error("Failed to save to MED History file.\nFile : \"" + _history.getVirtualPropertyFile().getAbsolutePath() + "\"", ex);
		}
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected void initDialog() {
		super.initDialog();

		// scroll to top item
		getArgsEditPane().scrollTopToVisible();
		
		// verify
		getArgsEditPane().verifyItemValues();
		
		// refresh tree context menu
		updateTreeContextMenu();
	}
	
	@Override
	protected void onWindowActivated(WindowEvent e) {
		// ツリーのコンテキストメニューを更新
		if (e.getWindow() == this) {
			updateTreeContextMenu();
		}
	}

	@Override
	protected void onWindowClosed(WindowEvent e) {
		_handler.onClosedDialog(this);
	}
	
	@Override
	protected void onShown(ComponentEvent e) {
		_handler.onShownDialog(this);
	}

	@Override
	protected void onHidden(ComponentEvent e) {
		_handler.onHiddenDialog(this);
	}
	
	protected void onTreeSelectionChanged() {
		updateTreeContextMenu();
		_statusbar.setMessage(_paneTree.getSelectedFileProperty());
	}

	protected void onTreeDoubleClicked(MouseEvent e) {
		// ダブルクリック時には、ファイルを開く
		//--- ダブルクリック後、マウスイベントの位置情報から対象パスを取得すると、
		//--- ツリーがスクロールした場合に想定していない位置が取得され、そのファイルを開くことに
		//--- なってしまうので、ダブルクリック後は単一選択になることを前提に、
		//--- 位置から取得したパスと、選択されているパスが一致した場合のみ、
		//--- そのパスのファイルを開く
		TreePath pathByPos = _paneTree.getTreeComponent().getPathForLocation(e.getX(), e.getY());
		TreePath pathBySel = _paneTree.getTreeComponent().getSelectionPath();	// ダブルクリック後は単一選択を前提
		if (pathByPos == null || !Objects.isEqual(pathByPos, pathBySel))
			return;		// no target node
		
		// ノードの取得
		DefaultFileTreeNode node = (DefaultFileTreeNode)pathByPos.getLastPathComponent();
		if (node.isDirectory())
			return;		// no file
		
		// ファイルを開く
		_handler.doOpenFileOnEditor(this, node.getFileObject());
	}
	
	protected void onTreeMenuActionPerformed(ActionEvent ae) {
		String commandKey = ae.getActionCommand();
		if (TREEMENU_ID_OPEN.equals(commandKey)) {
			// open
			AppLogger.debug("MExecDefEditDialog : context menu [Open] selected.");
			if (!_paneTree.canOpenFile()) {
				_treeActionOpen.setEnabled(false);
				return;
			}
			VirtualFile[] files = _paneTree.getSelectionFiles();
			if (files != null && files.length > 0) {
				for (VirtualFile vf : files) {
					if (vf != null) {
						_handler.doOpenFileOnEditor(this, vf);
					}
				}
			}
		}
		else if (TREEMENU_ID_TYPEDOPEN_CSV.equals(commandKey)) {
			// typed open - csv
			AppLogger.debug("MExecDefEditDialog : context menu [Typed open]-[csv] selected.");
			if (!_paneTree.canOpenFileByCSV()) {
				_treeActionTypedOpenCsv.setEnabled(false);
				_treeActionTypedOpen.setEnabled(false);
				return;
			}
			VirtualFile file = _paneTree.getSelectionFile();
			if (file != null) {
				_handler.doOpenFileByCsvOnEditor(this, file);
			}
		}
		else if (TREEMENU_ID_COPY.equals(commandKey)) {
			// copy
			AppLogger.debug("MExecDefEditDialog : context menu [Copy] selected.");
			if (!_paneTree.canCopy()) {
				_treeActionCopy.setEnabled(false);
				return;
			}
			_paneTree.doCopy();
			//_treeActionPaste.setEnabled(_paneTree.canPaste());
			_paneTree.requestFocusInComponent();
		}
		else if (TREEMENU_ID_REFRESH.equals(commandKey)) {
			// refresh
			AppLogger.debug("MExecDefEditDialog : context menu [Refresh] selected.");
			if (_paneTree.isSelectionEmpty()) {
				_paneTree.refreshAllTree();
			} else {
				_paneTree.refreshSelectedTree();
			}
			updateTreeContextMenu();
			_paneTree.requestFocusInComponent();
		}
	}

	/**
	 * このモジュールの履歴をすべて消去するボタン押下時のアクションハンドラ
	 * @since 1.20
	 */
	protected void onHistoryAllClearButton() {
		if (_history != null && !_history.isHistoryEmpty()) {
			_history.clearHistory();
			commitArgsHistory();
			//--- 履歴コントロールの再表示
			SpinnerNumberModel model = (SpinnerNumberModel)_spnHistoryNo.getModel();
			model.setMinimum(0);
			model.setValue(0);
			_lblHistory.setEnabled(false);
			_btnClearHistory.setEnabled(false);
			_spnHistoryNo.setEnabled(false);
		}
	}

	/**
	 * 履歴番号入力テキストボックスの内容が変更されたときのアクションハンドラ
	 * @since 1.20
	 */
	protected void onHistoryNumberChanged() {
		if (_history == null || _history.isHistoryEmpty()) {
			return;
		}
		
		// 履歴の取得
		Object value = _spnHistoryNo.getValue();
		if (!(value instanceof Number)) {
			return;		// not a value type
		}
		int historyNo = ((Number)value).intValue();
		if (historyNo <= 0 || historyNo > _history.getNumHistory()) {
			return;
		}
		ArrayList<ModuleArgData> hitem = _history.getHistory(historyNo-1);

		// 履歴の適用
		//--- 表示位置の保存
		final Rectangle rc = _argsPane.getVisibleRect();
		_argsPane.setHistory(hitem);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_argsPane.scrollRectToVisible(rc);
			}
		});
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
	static protected class MExecArgsFileTreeHandler extends DefaultFileTreeHandler
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
