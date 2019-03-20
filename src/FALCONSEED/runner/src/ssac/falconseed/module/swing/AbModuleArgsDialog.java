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
 * @(#)AbModuleArgsDialog.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbModuleArgsDialog.java	3.0.0	2014/03/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbModuleArgsDialog.java	1.22	2012/08/22
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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
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
import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.falconseed.file.swing.tree.DefaultFileTreeHandler;
import ssac.falconseed.file.swing.tree.DefaultFileTreeNode;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.RelatedModuleList;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.setting.MExecDefHistory;
import ssac.falconseed.module.swing.tree.MEDLocalFileTreePanel;
import ssac.falconseed.module.swing.tree.MEDLocalFileTreePanel.FileTreeStatusBar;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.RunnerResources;
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
 * モジュール実行定義や実行履歴からのモジュール実行時引数設定/表示ダイアログの共通機能
 * 
 * @version 3.1.0	2014/05/19
 * @since 1.22
 */
public abstract class AbModuleArgsDialog extends AbBasicDialog implements IMExecArgsDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -5313162523000534993L;

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
	 * ローカルファイルツリーパネル
	 */
	private MEDLocalFileTreePanel	_paneTree;
	/**
	 * ツリー配置用スプリッター
	 */
	private JSplitPane	_paneTreeSplitter;
	/**
	 * 外側のスプリッター
	 */
	private JSplitPane	_paneOuterSplitter;
	/**
	 * ファイル情報用ステータスバー
	 */
	private FileTreeStatusBar	_statusbar;
	/**
	 * 履歴ラベル
	 */
	private JLabel		_lblHistory;
	/**
	 * 履歴消去ボタン
	 */
	private JButton	_btnClearHistory;
	/**
	 * 複数モジュールの切り替えボタン
	 */
	private JButton	_btnFirstModule;
	private JButton	_btnPrevModule;
	private JButton	_btnNextModule;
	private JButton	_btnLastModule;
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
	 * 履歴番号
	 */
	private JMaskedNumberSpinner	_spnHistoryNo;
	/** 履歴番号変更イベントを無視するフラグ **/
	private boolean	_ignoreHistoryNoChangeEvent = false;
	
//	/**
//	 * 編集許可フラグ
//	 * @since 1.20
//	 */
//	private boolean	_editable = true;

	/** タイトルとして表示する文字列フォーマット **/
	private final String			_titleFormat;
	/** 引数設定のモデル **/
	protected final RelatedModuleList	_modulelist;
	/** 現在編集対象のモジュール実行設定情報 **/
	private final ModuleArgsEditModel	_argsmodel;
	/** 設定対象のモジュールのインデックス **/
	private int					_targetModuleIndex;

	/** モジュール実行定義の説明 **/
	private JTextComponent			_lblDesc;
	/** 引数設定ペイン **/
	private MExecArgsConfigPane	_argsPane;
	
	/**
	 * ダイアログハンドラ
	 */
	private MExecArgsDialogHandler	_handler = EmptyHandler;
	/**
	 * アクティブなビューを管理するマネージャ
	 */
	private ActiveViewManager _viewManager = new ActiveViewManager();
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbModuleArgsDialog(Frame owner, String titleFormat, boolean modal, boolean argsEditable, boolean argsHistoryEnabled, RelatedModuleList modules) {
		super(owner, titleFormat, modal);
		if (modules == null)
			throw new NullPointerException("The specified modules is null.");
		if (modules.isEmpty())
			throw new IllegalArgumentException("The specified modules is empty.");
		this._titleFormat = titleFormat;
		this._modulelist = modules;
		this._argsmodel = new ModuleArgsEditModel(argsEditable, argsHistoryEnabled);
	}
	
	public AbModuleArgsDialog(Dialog owner, String titleFormat, boolean modal, boolean argsEditable, boolean argsHistoryEnabled, RelatedModuleList modules) {
		super(owner, titleFormat, modal);
		if (modules == null)
			throw new NullPointerException("The specified modules is null.");
		if (modules.isEmpty())
			throw new IllegalArgumentException("The specified modules is empty.");
		this._titleFormat = titleFormat;
		this._modulelist = modules;
		this._argsmodel = new ModuleArgsEditModel(argsEditable, argsHistoryEnabled);
	}
	
	@Override
	public void initialComponent() {
		// 最初のモジュール実行定義を選択
		_targetModuleIndex = 0;
		_argsmodel.setData(_modulelist.getData(_targetModuleIndex));
		
		// 編集可能フラグの設定
		getArgsEditPane().setEditable(_argsmodel.isArgsEditable());
		
		// 基準パスの設定
		getArgsEditPane().setBasePath(_argsmodel.getExecDefDirectory().getParentFile());
		
		// コンポーネントの初期化
		super.initialComponent();
		
		// ツリーの初期化
		_paneTree.setRootDirectory(_argsmodel.getExecDefLocalFileRootDirectory());
		//--- 初期状態ではルートノードを閉じた状態とする
		//_paneTree.getTreeComponent().collapseRow(0);
		
		// 表示の更新
		redisplayTitle();
		redisplayDescription();
		redisplayHistory();
		redisplayArgs();
		updateVisibleModuleSelectButtons();
		
		// 最初の履歴を表示
//		if (_history != null && !_history.isHistoryEmpty()) {
//			_argsPane.setHistory(_history.getHistory(0));
//		}
		
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
		return getArgsEditPane().getViewingFileOnEditor();
	}

	/**
	 * このコンポーネントに、エディタビューで表示されているファイルの抽象パスを設定する。
	 * @param newFile	設定する抽象パス。抽象パスを設定しない場合は <tt>null</tt>
	 * @since 1.20
	 */
	public void setViewingFileOnEditor(VirtualFile newFile) {
		getArgsEditPane().setViewingFileOnEditor(newFile);
	}

	/**
	 * このダイアログに設定されたモデルの、モジュール実行定義のパスを返す。
	 */
	public String getMExecDefPath() {
		return _argsmodel.getExecDefDirectory().getPath();
	}

	/**
	 * このダイアログに設定されたモデルの、モジュール実行定義名を返す。
	 */
	public String getMExecDefName() {
		return _argsmodel.getData().getName();
	}

	/**
	 * このダイアログに設定されたモジュール実行定義リストを返す。
	 * @return <code>RelatedModuleList</code> オブジェクト
	 */
	public RelatedModuleList getModuleList() {
		return _modulelist;
	}

	/**
	 * 現在編集対象となっているモジュールのインデックスを返す。
	 * @return	モジュールのインデックス
	 */
	public int getSelectedModuleIndex() {
		return _targetModuleIndex;
	}
	
	public boolean isEditable() {
		return _argsmodel.isArgsEditable();
	}
	
	public boolean isArgsHistoryEnabled() {
		return _argsmodel.isArgsHistoryEnabled();
	}

	/**
	 * このダイアログに設定された引数設定履歴を返す。
	 * @return	<code>MExecDefHistory</code> オブジェクト、設定されていない場合は <tt>null</tt>
	 */
	public MExecDefHistory getArgsHistory() {
		return _argsmodel.getArgsHistory();
	}

	/**
	 * 設定されているダイアログハンドラを返す。
	 * @return	ハンドラが設定されている場合はそのオブジェクト、それ以外の場合は <tt>null</tt>
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

		VirtualFilePathFormatterList vfFormatter = _argsmodel.getFormatter();
		if (vfFormatter != null) {
			String path = vfFormatter.formatPath(file);
			if (path != null)
				return path;
		}
		
		return file.getPath();
	}
	
	protected ModuleArgsEditModel getArgsEditModel() {
		return _argsmodel;
	}
	
	protected MExecArgsConfigPane getArgsEditPane() {
		if (_argsPane == null) {
			_argsPane = createArgsEditPane();
		}
		return _argsPane;
	}
	
	protected JTextComponent createDescriptionLabel() {
		JMultilineLabel label = new JMultilineLabel();
		return label;
	}
	
	protected MExecArgsConfigPane createArgsEditPane() {
		MExecArgsConfigPane pane = new MExecArgsConfigPane();
		return pane;
	}
	
	protected MEDLocalFileTreePanel createFileTreePanel() {
		MEDLocalFileTreePanel ptree = new MEDLocalFileTreePanel(){
			@Override
			protected void onTreeDoubleClicked(MouseEvent e) {
				AbModuleArgsDialog.this.onTreeDoubleClicked(e);
			}

			@Override
			protected void onTreeSelectionAdjusted() {
				AbModuleArgsDialog.this.onTreeSelectionChanged();
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
	
	protected JComponent createModuleSelectButtonPanel() {
		_btnFirstModule = new JButton(RunnerResources.ICON_ARROW_FIRST);
		_btnFirstModule.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onClickFirstModuleButton();
			}
		});
		
		_btnLastModule  = new JButton(RunnerResources.ICON_ARROW_LAST);
		_btnLastModule.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onClickLastModuleButton();
			}
		});
		
		_btnPrevModule  = new JButton(RunnerResources.ICON_ARROW_PREV);
		_btnPrevModule.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onClickPrevModuleButton();
			}
		});
		
		_btnNextModule  = new JButton(RunnerResources.ICON_ARROW_NEXT);
		_btnNextModule.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onClickNextModuleButton();
			}
		});
		
		Box box = new Box(BoxLayout.X_AXIS);
		box.add(_btnFirstModule);
		box.add(_btnPrevModule);
		box.add(_btnNextModule);
		box.add(_btnLastModule);
		
		return box;
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
		_spnHistoryNo = new JMaskedNumberSpinner("#0", 0, 0, 0, 1);
		_spnHistoryNo.setVisible(false);
		_spnHistoryNo.setEnabled(false);
		_btnClearHistory.setVisible(false);
		_btnClearHistory.setEnabled(false);
		_lblHistory.setVisible(false);
		_lblHistory.setEnabled(false);
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
		MExecArgsConfigPane argsPane = getArgsEditPane();
		_paneTree = createFileTreePanel();
		_statusbar = createStatusBar();
		_paneTreeSplitter = new JSplitPane();
		_paneOuterSplitter = new JSplitPane();
		_paneTree.setMinimumSize(new Dimension(50,50));
		//--- tree context menu
		createTreeContextMenuActions();
		_paneTree.setTreeContextMenu(createTreeContextMenu());
		
		// setup data
//		_lblDesc.setText(_model.getSettings().getDescription());
//		_lblDesc.setCaretPosition(0);
//		argsPane.setModel(_model);
		
		// desc scroll pane
		JScrollPane scDesc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
											JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scDesc.setViewportView(_lblDesc);
		
		
		// args scroll pane
		JScrollPane scArgs = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
											JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scArgs.setViewportView(argsPane);
		
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
	protected JComponent createButtonsPanel() {
		JButton[] buttons = createButtons();
		if (buttons == null || buttons.length < 1) {
			// no buttons
			return null;
		}
		
		int maxHeight = adjustButtonSize(buttons);
		
		JComponent modSelButtons = createModuleSelectButtonPanel();
		
		// Layout
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(Box.createHorizontalGlue());
		if (_modulelist.size() > 1) {
			btnBox.add(modSelButtons);
			btnBox.add(Box.createHorizontalGlue());
		}
		for (JButton btn : buttons) {
			btnBox.add(btn);
			btnBox.add(Box.createHorizontalStrut(5));
		}
		btnBox.add(Box.createRigidArea(new Dimension(0, maxHeight+10)));
		return btnBox;
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
	
	protected void storeAllArgsHistories() {
		if (!_argsmodel.isArgsHistoryEnabled())
			return;		// 引数履歴保存は不要
		
		for (ModuleRuntimeData data : _modulelist) {
			MExecDefHistory history = ModuleArgsEditModel.getHistory(data);
			if (history != null) {
				storeArgsHistory(history, data);
				history = null;
			}
		}
	}
	
	protected void storeArgsHistory(final MExecDefHistory history, ModuleRuntimeData data) {
		if (history == null)
			return;		// 履歴の保存先が存在しない
		if (data.isEmptyArgument())
			return;		// 引数が存在しない
		
		// 履歴データを作成
		int numArgs = data.getArgumentCount();
		ArrayList<ModuleArgData> hitem = new ArrayList<ModuleArgData>(numArgs);
		for (int index = 0; index < numArgs; index++) {
			IModuleArgConfig arg = data.getArgument(index);
			ModuleArgData hdata = new ModuleArgData(arg.getType(), arg.getDescription());
			if (ModuleArgType.OUT == arg.getType()) {
				if (arg.getOutToTempEnabled()) {
					String tempprefix = arg.getTempFilePrefix();
					if (!Strings.isNullOrEmpty(tempprefix)) {
						// プレフィックス付きのテンポラリ出力指定
						hdata.setValue(tempprefix);
					} else {
						// プレフィックスなしのテンポラリ出力指定
						hdata.setValue(MExecArgTempFile.instance);
					}
				} else {
					hdata.setValue(arg.getValue());
				}
			}
			else {
				hdata.setValue(arg.getValue());
			}
			hitem.add(hdata);
		}
		
		// 新しい履歴を保存
		history.addHistory(hitem);
		commitArgsHistory(history);
	}
	
	protected void commitArgsHistory(final MExecDefHistory history) {
		history.ensureMaxSize(AppSettings.getInstance().getHistoryMaxLength());
		try {
			history.commit();
		}
		catch (Throwable ex) {
			AppLogger.error("Failed to save to MED History file.\nFile : \"" + history.getVirtualPropertyFile().getAbsolutePath() + "\"", ex);
		}
	}

	protected void setTargetModule(int index) {
		ModuleRuntimeData targetData = _modulelist.getData(index);
		_targetModuleIndex = index;
		_argsmodel.setData(targetData);
		
		redisplayTitle();
		redisplayDescription();
		redisplayFileTree();
		redisplayHistory();
		redisplayArgs();
		updateEnableModuleSelectionButtons();
		getArgsEditPane().scrollTopToVisible();
	}
	
	protected void redisplayTitle() {
		String name;
		if (_modulelist.size() > 1) {
			name = String.format("(%d/%d)%s", (_targetModuleIndex+1), _modulelist.size(), _argsmodel.getName());
		} else {
			name = _argsmodel.getName();
		}
		setTitle(String.format(_titleFormat, name));
	}
	
	protected void updateVisibleModuleSelectButtons() {
		if (_modulelist.size() > 1) {
			// 複数のモジュール定義
			_btnFirstModule.setVisible(true);
			_btnLastModule .setVisible(true);
			_btnPrevModule .setVisible(true);
			_btnNextModule .setVisible(true);
			updateEnableModuleSelectionButtons();
		}
		else {
			// 単一モジュールの場合は、選択ボタンを表示しない
			_btnFirstModule.setVisible(false);
			_btnLastModule .setVisible(false);
			_btnPrevModule .setVisible(false);
			_btnNextModule .setVisible(false);
			_btnFirstModule.setEnabled(false);
			_btnLastModule .setEnabled(false);
			_btnPrevModule .setEnabled(false);
			_btnNextModule .setEnabled(false);
		}
	}
	
	protected void updateEnableModuleSelectionButtons() {
		int maxIndex = _modulelist.size()-1;
		if (maxIndex <= 0) {
			_btnFirstModule.setEnabled(false);
			_btnPrevModule .setEnabled(false);
			_btnLastModule .setEnabled(false);
			_btnNextModule .setEnabled(false);
		}
		else if (_targetModuleIndex <= 0) {
			_btnFirstModule.setEnabled(false);
			_btnPrevModule .setEnabled(false);
			_btnLastModule .setEnabled(true);
			_btnNextModule .setEnabled(true);
		}
		else if (_targetModuleIndex >= maxIndex) {
			_btnFirstModule.setEnabled(true);
			_btnPrevModule .setEnabled(true);
			_btnLastModule .setEnabled(false);
			_btnNextModule .setEnabled(false);
		}
		else {
			_btnFirstModule.setEnabled(true);
			_btnPrevModule .setEnabled(true);
			_btnLastModule .setEnabled(true);
			_btnNextModule .setEnabled(true);
		}
	}
	
	protected void redisplayDescription() {
		_lblDesc.setText(_argsmodel.getData().getDescription());
		_lblDesc.setCaretPosition(0);
	}
	
	protected void redisplayFileTree() {
		_paneTree.setRootDirectory(_argsmodel.getExecDefLocalFileRootDirectory());
	}
	
	protected void redisplayHistory() {
		if (_argsmodel.isArgsHistoryEnabled()) {
			MExecDefHistory history = _argsmodel.getArgsHistory();
			if (history != null && !history.isHistoryEmpty()) {
				_ignoreHistoryNoChangeEvent = true;
				SpinnerNumberModel model = (SpinnerNumberModel)_spnHistoryNo.getModel();
				model.setMaximum(history.getNumHistory());
				model.setMinimum(1);
				model.setValue(1);
				_ignoreHistoryNoChangeEvent = false;
				_spnHistoryNo.setEnabled(true);
				_btnClearHistory.setEnabled(true);
				_lblHistory.setEnabled(true);
			}
			else {
				_ignoreHistoryNoChangeEvent = true;
				SpinnerNumberModel model = (SpinnerNumberModel)_spnHistoryNo.getModel();
				model.setMinimum(0);
				model.setValue(0);
				_ignoreHistoryNoChangeEvent = false;
				_spnHistoryNo.setEnabled(false);
				_btnClearHistory.setEnabled(false);
				_lblHistory.setEnabled(false);
			}
			_spnHistoryNo.setVisible(true);
			_btnClearHistory.setVisible(true);
			_lblHistory.setVisible(true);
		}
		else {
			// 履歴は表示しない
			SpinnerNumberModel model = (SpinnerNumberModel)_spnHistoryNo.getModel();
			model.setMinimum(0);
			model.setValue(0);
			_spnHistoryNo.setVisible(false);
			_spnHistoryNo.setEnabled(false);
			_btnClearHistory.setVisible(false);
			_btnClearHistory.setEnabled(false);
			_lblHistory.setVisible(false);
			_lblHistory.setEnabled(false);
		}
	}
	
	protected void redisplayArgs() {
		_argsPane.setBasePath(_argsmodel.getExecDefDirectory().getParentFile());
		_argsPane.setModel(_argsmodel.getData());
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				_argsPane.scrollTopToVisible();
			}
		});
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
		if (_argsmodel.isArgsHistoryEnabled()) {
			MExecDefHistory history = _argsmodel.getArgsHistory();
			if (history != null && !history.isHistoryEmpty()) {
				history.clearHistory();
				commitArgsHistory(history);
				//--- 履歴コントロールの再表示
				SpinnerNumberModel model = (SpinnerNumberModel)_spnHistoryNo.getModel();
				model.setMinimum(0);
				model.setValue(0);
				_lblHistory.setEnabled(false);
				_btnClearHistory.setEnabled(false);
				_spnHistoryNo.setEnabled(false);
			}
		}
	}

	/**
	 * 履歴番号入力テキストボックスの内容が変更されたときのアクションハンドラ
	 * @since 1.20
	 */
	protected void onHistoryNumberChanged() {
		MExecDefHistory history = _argsmodel.getArgsHistory();
		if (history == null || history.isHistoryEmpty()) {
			return;
		}
		
		// 履歴の取得
		if (!_ignoreHistoryNoChangeEvent) {
			Object value = _spnHistoryNo.getValue();
			if (!(value instanceof Number)) {
				return;		// not a value type
			}
			int historyNo = ((Number)value).intValue();
			if (historyNo <= 0 || historyNo > history.getNumHistory()) {
				return;
			}
			ArrayList<ModuleArgData> hitem = history.getHistory(historyNo-1);

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
	}
	
	protected void onClickFirstModuleButton() {
		if (_targetModuleIndex != 0) {
			setTargetModule(0);
		}
	}
	
	protected void onClickLastModuleButton() {
		if (_targetModuleIndex < (_modulelist.size()-1)) {
			setTargetModule(_modulelist.size()-1);
		}
	}
	
	protected void onClickPrevModuleButton() {
		if (_targetModuleIndex > 0) {
			setTargetModule(_targetModuleIndex - 1);
		}
	}
	
	protected void onClickNextModuleButton() {
		if (_targetModuleIndex < (_modulelist.size()-1)) {
			setTargetModule(_targetModuleIndex + 1);
		}
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
