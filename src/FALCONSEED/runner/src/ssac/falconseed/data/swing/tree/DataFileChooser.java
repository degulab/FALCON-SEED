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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)DataFileChooser.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.data.swing.tree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.TreePath;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.aadl.module.swing.FilenameValidator;
import ssac.util.Strings;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.SwingTools;

/**
 * ツリー形式のデータファイル選択ダイアログ
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class DataFileChooser extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final int FILE_ONLY = 0;
	static public final int FOLDER_ONLY = 1;
	static public final int FILE_OR_FOLDER = 2;
	
	static private final Dimension DEFAULT_MIN_SIZE = new Dimension(320, 320);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** ファイル名のバリデータ **/
	private final FilenameValidator	_validator;

	/** 選択方式 **/
	private int					_selectionMode = FILE_ONLY;
	/** 説明表示用ラベルコンポーネント **/
	private JTextPane				_lblDesc;
	/** ツリーを含むペイン **/
	private DataFileTreePane		_treePane;
	/** 新規作成したフォルダのパス **/
	private final ArrayList<VirtualFile> _createdFolders = new ArrayList<VirtualFile>();
	
	protected DataFileTreePane getTreePane() {
		return _treePane;
	}
	
	protected JTextPane getDescriptionLabel() {
		return _lblDesc;
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static public DataFileChooser createFileChooser(Component parentComponent, boolean allowCreateFolder,
														VirtualFile systemRoot, VirtualFile userRoot,
														VirtualFile initialSelection,
														String title, String description,
														String treeLabel, String inputLabel,
														VirtualFileFilter filter,
														FilenameValidator validator)
	{
		// create instance
		DataFileChooser chooser;
		Window win = SwingTools.getWindowForComponent(parentComponent);
		if (win instanceof Frame) {
			chooser = new DataFileChooser((Frame)win, title, validator);
		} else {
			chooser = new DataFileChooser((Dialog)win, title, validator);
		}
		
		// initial component
		chooser.initialComponent(allowCreateFolder, systemRoot, userRoot, initialSelection,
									description, treeLabel, inputLabel, filter);
		
		// completed
		return chooser;
	}
	
	protected DataFileChooser(Frame owner, String title, FilenameValidator validator)
	{
		super(owner, title, true);
		this._validator = validator;
	}
	
	protected DataFileChooser(Dialog owner, String title, FilenameValidator validator)
	{
		super(owner, title, true);
		this._validator = validator;
	}

	@Override
	public void initialComponent() {
		super.initialComponent();
	}
	
	public void initialComponent(boolean allowCreateFolder,
									VirtualFile systemRoot, VirtualFile userRoot, VirtualFile initialSelection,
									String description, String treeLabel, String inputLabel, VirtualFileFilter filter)
	{
		initialComponent();
		
		setDescription(description);
		getApplyButton().setVisible(allowCreateFolder);
		getTreePane().setTreeLabelText(treeLabel);
		if (filter != null) {
			getTreePane().getTreeModel().setFileFilter(filter);
		}
		getTreePane().setSystemRootDirectory(systemRoot);
		getTreePane().setUserRootDirectory(userRoot);
		getTreePane().setInputLabelText(inputLabel);
		if (initialSelection != null) {
			getTreePane().setSelectionFile(initialSelection);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isFileSelectionEnabled() {
		return (_selectionMode==FILE_ONLY || _selectionMode==FILE_OR_FOLDER);
	}
	
	public boolean isFolderSelectionEnabled() {
		return (_selectionMode==FOLDER_ONLY || _selectionMode==FILE_OR_FOLDER);
	}

	/**
	 * 現在のファイル選択モードを返す。デフォルトは <code>DataFileChooser.FILE_ONLY</code> となる。
	 * @return	選択を許可するファイルの種類。次のうちのどれか
	 * <ul>
	 * <li>DataFileChooser.FILE_ONLY
	 * <li>DataFileChooser.FOLDER_ONLY
	 * <li>DataFileChooser.FILE_OR_FOLDER
	 * </ul>
	 */
	public int getFileSelectionMode() {
		return _selectionMode;
	}

	/**
	 * ファイル選択モードを設定する。このファイル選択モードは、最終的にツリーで
	 * 選択されているファイルもしくはフォルダで決定可能かどうかを設定するものである。
	 * したがって、ツリー表示上の選択は単一選択のまま影響をうけるものではない。<br>
	 * <em>mode</em> に <code>DataFileChooser.FILE_ONLY</code> が指定された場合は
	 * ファイルのみが選択可能となる。<br>
	 * <em>mode</em> に <code>DataFileChooser.FOLDER_ONLY</code> が指定された場合は
	 * フォルダのみが選択可能となる。<br>
	 * <em>mode</em> に <code>DataFileChooser.FILE_OR_FOLDER</code> が指定された場合は
	 * ファイルとフォルダのどちらも選択可能となる。
	 * @param mode	選択を許可するファイルの種類
	 * <ul>
	 * <li>DataFileChooser.FILE_ONLY
	 * <li>DataFileChooser.FOLDER_ONLY
	 * <li>DataFileChooser.FILE_OR_FOLDER
	 * </ul>
	 * @throws IllegalArgumentException	<em>mode</em> が無効な場合
	 */
	public void setFileSelectionMode(int mode) {
		if (_selectionMode == mode) {
			return;		// not changed
		}
		
		if (mode==FILE_ONLY || mode==FOLDER_ONLY || mode==FILE_OR_FOLDER) {
			//int oldMode = _selectionMode;
			_selectionMode = mode;
			updateButtons();
		} else {
			throw new IllegalArgumentException("Incorrect Mode for file selection : " + mode);
		}
	}

	/**
	 * 説明テキストを返す。
	 * 説明テキストが設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getDescription() {
		String text = _lblDesc.getText();
		return (Strings.isNullOrEmpty(text) ? null : text);
	}

	/**
	 * 説明テキストを設定する。
	 * @param description	設定する説明テキストを表す文字列を指定する。
	 * 						説明テキストを表示しない場合は <tt>null</tt> を指定する。
	 */
	public void setDescription(String description) {
		if (Strings.isNullOrEmpty(description)) {
			_lblDesc.setVisible(false);
			_lblDesc.setText("");
		} else {
			_lblDesc.setText(description);
			_lblDesc.setVisible(true);
		}
	}

	/**
	 * バリデータの設定によりフィルタリングされた、入力フィールドのテキストを返す。
	 * バリデータが未定義の場合や、フィルタリングする要因がバリデータに定義されていない
	 * 場合は、入力フィールドのテキストをそのまま返す。
	 */
	public String getFilteredInputFieldText() {
		String text = _treePane.getInputFieldText();
		if (_validator != null) {
			return _validator.filterText(text);
		} else {
			return text;
		}
	}

	/**
	 * 入力フィールドの現在のテキストを返す。
	 */
	public String getInputFieldText() {
		return _treePane.getInputFieldText();
	}

	/**
	 * 入力フィールドに指定されたテキストを設定する。
	 */
	public void setInputFieldText(String text) {
		_treePane.setInputFieldText(text);
	}

	/**
	 * ツリーで選択されているノードの抽象パスを返す。
	 * 選択されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getSelectionFile() {
		return _treePane.getSelectionFile();
	}

	/**
	 * 指定された抽象パスを選択状態とする。
	 * 指定された抽象パスがツリーに存在しない場合、選択を解除する。
	 */
	public void setSelectionFile(VirtualFile file) {
		_treePane.setSelectionFile(file);
	}

	/**
	 * このダイアログ内で作成されたすべてのフォルダを抽象パスを
	 * 格納する配列を返す。作成されたフォルダがない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile[] getCreatedFolders() {
		if (_createdFolders.isEmpty()) {
			return null;
		} else {
			return _createdFolders.toArray(new VirtualFile[_createdFolders.size()]);
		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected void initDialog() {
		// 基本処理
		super.initDialog();
		
		// フィールドバリデータを接続
		if (_validator != null) {
			getTreePane().setInputValidator(_validator);
		}
		
		// 選択されているノードを表示
		getTreePane().scrollSelectionPathToVisible();
		
		// ボタンの表示を更新
		updateButtons();
		
		// 入力フィールドが表示されている場合は、
		// 入力フィールドにフォーカスを設定
		if (getTreePane().isInputFieldVisible()) {
			getTreePane().setFocusToInputField();
		}
	}

	@Override
	protected void dialogClose(int result) {
		// フィールドバリデータの関連を切断
		getTreePane().setInputValidator(null);
		
		// 基本処理
		super.dialogClose(result);
	}

	@Override
	protected void doApplyAction() {
		// このボタンはディレクトリ作成ボタンとして機能する
		final DataFileTreePane treePane = getTreePane();
		if (!treePane.isSelectionEmpty()) {
			TreePath treepath = treePane.getSelectionPath();
			if (canCreateFolder((FileTreeNode)treepath.getLastPathComponent())) {
				VirtualFile selectedFile = treePane.getSelectionFile();
				String strNewName = FileDialogManager.showSimpleCreateFolderDialog(this, selectedFile);
				if (!Strings.isNullOrEmpty(strNewName)) {
					// フォルダ作成(エラーメッセージはメソッド内で出力)
					TreePath newDirPath = treePane.getTreeComponent().createDirectory(this, treepath, strNewName);
					if (newDirPath != null) {
						// フォルダ作成成功
						_createdFolders.add(((FileTreeNode)newDirPath.getLastPathComponent()).getFileObject());
					}
				}
			}
			else {
				getApplyButton().setEnabled(false);
			}
		}
	}

	@Override
	protected boolean doOkAction() {
		final DataFileTreePane treePane = getTreePane();
		
		// 親フォルダが選択されていなければ、処理しない
		if (treePane.isSelectionEmpty()) {
			return false;
		}
		
		// 現在のファイルが選択可能かをチェックする
		if (!canChooseFile((FileTreeNode)treePane.getSelectionPath().getLastPathComponent())) {
			getOkButton().setEnabled(false);
			return false;
		}
		
		// バリデータがあれば、入力文字列をチェック
		if (!treePane.verifyInputField()) {
			return false;
		}
		
		// ダイアログを閉じる
		return true;
	}
	
	protected void onTreeSelectionChanged(TreeSelectionEvent tse) {
		updateButtons();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void updateButtons() {
		TreePath path = getTreePane().getSelectionPath();
		boolean toApplyEnable = false;
		boolean toOkEnable    = false;
		if (path != null) {
			FileTreeNode node = (FileTreeNode)path.getLastPathComponent();
			toApplyEnable = canCreateFolder(node);
			toOkEnable    = canChooseFile(node);
		}
		getApplyButton().setEnabled(toApplyEnable);
		getOkButton().setEnabled(toOkEnable);
	}
	
	protected boolean canCreateFolder(FileTreeNode node) {
		boolean allow = false;
		if (!node.isRoot()) {
			if (node.isDirectory()) {
				allow = true;
			}
		}
		return allow;
	}
	
	protected boolean canChooseFile(FileTreeNode node) {
		boolean allow = false;
		if (!node.isRoot()) {
			if (node.isDirectory()) {
				if (isFolderSelectionEnabled()) {
					allow = true;
				}
			}
			else {
				if (isFileSelectionEnabled()) {
					allow = true;
				}
			}
		}
		return allow;
	}
	
	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();
		
		setResizable(true);
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = DEFAULT_MIN_SIZE;
		}
		this.setMinimumSize(dmMin);
		setKeepMinimumSize(true);
		
		this.pack();
		this.setLocationRelativeTo(null);
	}

	@Override
	protected void setupMainContents() {
		// create content components
		this._lblDesc = createDescriptionLabel();
		this._treePane = createTreePane();
		
		// create main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// setup layout
		mainPanel.add(_treePane, BorderLayout.CENTER);
		mainPanel.add(_lblDesc, BorderLayout.NORTH);
		
		// add to root pane
		this.add(mainPanel, BorderLayout.CENTER);
	}

	@Override
	protected void setupActions() {
		super.setupActions();
		
		// setup actions
		getTreePane().addTreeSelectionListener(new TreeSelectionListener(){
			public void valueChanged(TreeSelectionEvent e) {
				onTreeSelectionChanged(e);
			}
		});
	}

	@Override
	protected Dimension getDefaultSize() {
		return super.getDefaultSize();
	}
	
	@Override
	protected String getDefaultApplyButtonCaption() {
		return CommonMessages.getInstance().InputTitle_NewFolder;
	}
	
	@Override
	protected JComponent createButtonsPanel() {
		// [フォルダ作成]ボタンは左寄せ
		JButton[] buttons = createButtons();
		if (buttons == null || buttons.length < 1) {
			// no buttons
			return null;
		}
		
		int maxHeight = adjustButtonSize(buttons);
		
		// Layout
		int index = 0;
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(buttons[index++]);
		btnBox.add(Box.createHorizontalGlue());
		for (; index < buttons.length; index++) {
			btnBox.add(buttons[index]);
			btnBox.add(Box.createHorizontalStrut(5));
		}
		btnBox.add(Box.createRigidArea(new Dimension(0, maxHeight+10)));
		return btnBox;
	}

	/**
	 * 説明を表示するコンポーネントを生成する。
	 * @return	生成されたコンポーネント
	 */
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

	/**
	 * モジュール実行定義用ツリーペインを生成する。
	 * このメソッドは、{@link DataFileTreePane#initialComponent()} も呼び出す。
	 * @return	生成された <code>MExecDefTreePane</code> オブジェクト
	 */
	protected DataFileTreePane createTreePane() {
		DataFileTreePane pane = new DataFileTreePane();
		pane.initialComponent();
		return pane;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
