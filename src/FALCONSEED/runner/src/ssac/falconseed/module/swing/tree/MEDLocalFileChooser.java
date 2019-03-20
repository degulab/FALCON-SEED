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
 * @(#)MEDLocalFileChooser.java	1.20	2012/03/13
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.aadl.module.swing.FilenameValidator;
import ssac.falconseed.file.swing.tree.AliasFileTreeData;
import ssac.falconseed.file.swing.tree.DefaultFileTreeModel;
import ssac.falconseed.file.swing.tree.DefaultFileTreeNode;
import ssac.falconseed.file.swing.tree.FileTree;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.util.Strings;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.SwingTools;
import ssac.util.swing.tree.DnDTree;
import ssac.util.swing.tree.IDnDTreeHandler;

/**
 * ツリー形式のモジュール実行定義ローカルファイル選択ダイアログ
 * 
 * @version 1.20	2012/03/13
 * @since 1.20
 */
public class MEDLocalFileChooser extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String EmptyTreePathText = " ";
	
	static public final int FILE_ONLY = 0;
	static public final int FOLDER_ONLY = 1;
	static public final int FILE_OR_FOLDER = 2;
	
	static private final Dimension DEFAULT_MIN_SIZE = new Dimension(320, 320);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final InputFieldFocusRequester _hFocusField = new InputFieldFocusRequester();
	private final NoDragDropTreeHandler _hTree = new NoDragDropTreeHandler();

	/** ツリーコンポーネント **/
	protected FileTree		_tree;
	/** ツリーコンポーネント用ラベル **/
	protected JLabel		_treeLabel;
	/** ツリー選択パス表示用ラベル **/
	protected JLabel		_treePath;
	/** 入力用ラベルとフィールドを持つパネル **/
	protected JPanel		_inputPanel;
	/** 入力フィールド用ラベル **/
	protected JLabel		_inputLabel;
	/** 入力フィールド **/
	protected JTextField	_inputField;
	/** 入力フィールドの入力制限を行うバリデータ **/
	protected FilenameValidator _validator;

	/** 選択方式 **/
	private int					_selectionMode = FILE_ONLY;
	/** 説明表示用ラベルコンポーネント **/
	protected JTextPane			_lblDesc;
	/** 新規作成したフォルダのパス **/
	protected final ArrayList<VirtualFile> _createdFolders = new ArrayList<VirtualFile>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static public MEDLocalFileChooser createFileChooser(Component parentComponent, boolean allowCreateFolder,
														VirtualFile rootDir,
														VirtualFile initialSelection,
														String title, String description,
														String treeLabel, String inputLabel,
														VirtualFileFilter filter,
														FilenameValidator validator)
	{
		// create instance
		MEDLocalFileChooser chooser;
		Window win = SwingTools.getWindowForComponent(parentComponent);
		if (win instanceof Frame) {
			chooser = new MEDLocalFileChooser((Frame)win, title, validator);
		} else {
			chooser = new MEDLocalFileChooser((Dialog)win, title, validator);
		}
		
		// initial component
		chooser.initialComponent(allowCreateFolder, rootDir, initialSelection,
									description, treeLabel, inputLabel, filter);
		
		// completed
		return chooser;
	}
	
	protected MEDLocalFileChooser(Frame owner, String title, FilenameValidator validator)
	{
		super(owner, title, true);
		this._validator = validator;
	}
	
	protected MEDLocalFileChooser(Dialog owner, String title, FilenameValidator validator)
	{
		super(owner, title, true);
		this._validator = validator;
	}

	@Override
	public void initialComponent() {
		super.initialComponent();
	}
	
	public void initialComponent(boolean allowCreateFolder,
									VirtualFile rootDir, VirtualFile initialSelection,
									String description, String treeLabel, String inputLabel, VirtualFileFilter filter)
	{
		initialComponent();
		
		setDescription(description);
		getApplyButton().setVisible(allowCreateFolder);
		setTreeLabelText(treeLabel);
		if (filter != null) {
			_tree.getModel().setFileFilter(filter);
		}
		setRootDirectory(rootDir);
		setInputLabelText(inputLabel);
		if (initialSelection != null) {
			setSelectionFile(initialSelection);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ルートディレクトリを取得する。
	 * @return	ルートディレクトリ，設定されていない場合は <tt>null</tt>
	 */
	public VirtualFile getRootDirectory() {
		if (_tree != null) {
			return _tree.getModel().getRoot().getFileObject();
		} else {
			return null;
		}
	}

	/**
	 * ルートディレクトリを設定する。
	 * @param rootDir	新しいルートディレクトリ
	 * @throws IllegalArgumentException	<em>rootDir</em> がディレクトリではない場合
	 * @throws IllegalStateException		ツリーコンポーネントが生成されていない場合
	 */
	public void setRootDirectory(VirtualFile rootDir) {
		if (_tree == null)
			throw new IllegalStateException("Tree component does not exists.");
		if (rootDir != null && !rootDir.isDirectory())
			throw new IllegalArgumentException("The specified file is not directory : \"" + rootDir.getAbsolutePath() + "\"");
		
		DefaultFileTreeNode rootNode = _tree.getModel().getRoot();
		VirtualFile oldRootDir = rootNode.getFileObject();
		if (oldRootDir == rootDir || (rootDir != null && rootDir.equals(oldRootDir))) {
			// 変更なし
			return;
		}
		
		// ファイルの更新とツリーの表示更新
		rootNode.getFileData().replaceFile(rootDir);
		refreshAllTree();
	}
	
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
		String text = getInputFieldText();
		if (_validator != null) {
			return _validator.filterText(text);
		} else {
			return text;
		}
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

	/**
	 * ツリーのノードが選択されていない場合は <tt>true</tt>
	 */
	public boolean isSelectionEmpty() {
		return _tree.isSelectionEmpty();
	}

	/**
	 * ツリーで選択されているノード数を返す。
	 * このクラスでは単一選択のみ許可しているので、
	 * このメソッドが返す値は 0 もしくは 1 となる。
	 * @return	選択されているノード数
	 */
	public int getSelectionCount() {
		return _tree.getSelectionCount();
	}

	/**
	 * ツリーで選択されているノードへのツリーパスを返す。
	 * 選択されていない場合は <tt>null</tt> を返す。
	 */
	public TreePath getSelectionPath() {
		return _tree.getSelectionPath();
	}

	/**
	 * ツリーラベルの文字列を返す。
	 */
	public String getTreeLabelText() {
		return _treeLabel.getText();
	}

	/**
	 * ツリーラベルに表示する文字列を設定する。
	 * @param text	ツリーラベルに表示する文字列を指定する
	 */
	public void setTreeLabelText(String text) {
		_treeLabel.setText(text);
	}

	/**
	 * 入力フィールドのラベルに設定されている文字列を返す。
	 * 入力フィールドが表示されていない場合は <tt>null</tt> を返す。
	 */
	public String getInputLabelText() {
		if (_inputPanel.isVisible()) {
			return _inputLabel.getText();
		} else {
			return null;
		}
	}

	/**
	 * 入力フィールドのラベルに表示する文字列を設定する。
	 * @param text	ラベルに表示する文字列を指定する。
	 * 				<tt>null</tt> もしくは空文字列の場合、
	 * 				入力フィールドは非表示となりバリデータも無効となる。
	 */
	public void setInputLabelText(String text) {
		if (Strings.isNullOrEmpty(text)) {
			_inputPanel.setVisible(false);
			_inputLabel.setText("");
		} else {
			_inputLabel.setText(text);
			_inputPanel.setVisible(true);
		}
	}

	/**
	 * 設定されているバリデータを返す。
	 */
	public FilenameValidator getInputValidator() {
		return _validator;
	}

	/**
	 * 入力を制限するバリデータを設定する。
	 * このバリデータは入力フィールドが表示されている場合のみ適用される。
	 * @param newValidator	新たに設定するバリデータ
	 */
	public void setInputValidator(FilenameValidator newValidator) {
		if (newValidator == _validator) {
			return;
		}
		
		FilenameValidator oldValidator = _validator;
		_validator = newValidator;
		
		if (oldValidator != null) {
			oldValidator.setTargetTextField(null);
			oldValidator.clearAlreadyExistsFilenames();
		}
		
		if (newValidator != null) {
			newValidator.setTargetTextField(_inputField);
			TreePath path = _tree.getSelectionPath();
			if (path == null) {
				newValidator.clearAlreadyExistsFilenames();
			} else {
				newValidator.setAlreadyExistsFilenames(
						((DefaultFileTreeNode)path.getLastPathComponent()).getFileObject()
				);
			}
		}
	}

	/**
	 * 入力フィールドの現在のテキストを返す。
	 */
	public String getInputFieldText() {
		return _inputField.getText();
	}

	/**
	 * 入力フィールドに表示するテキストを設定する。
	 * @param text	表示する文字列
	 */
	public void setInputFieldText(String text) {
		if (Strings.isNullOrEmpty(text)) {
			_inputField.setText("");
		} else {
			_inputField.setText(text);
		}
	}

	/**
	 * 入力フィールドが表示されている場合に <tt>true</tt> を返す。
	 */
	public boolean isInputFieldVisible() {
		return _inputPanel.isVisible();
	}

	/**
	 * 入力フィールドに入力されているテキストを、設定されている
	 * バリデータで検証する。
	 * @return	バリデータが設定されている場合、入力内容が正当であれば <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt> を返す。
	 * 			入力フィールドが表示されていない場合や、バリデータが設定されていない
	 * 			場合は <tt>true</tt> を返す。
	 */
	public boolean verifyInputField() {
		if (_inputPanel.isVisible() && _validator != null) {
			return _validator.verify(_inputField, _inputField.getText());
		} else {
			return true;
		}
	}
	
	public boolean isRootVisible() {
		return _tree.isRootVisible();
	}
	
	public void setRootVisible(boolean rootVisible) {
		_tree.setRootVisible(rootVisible);
	}

	/**
	 * ツリーコンポーネントで選択されているノードの抽象パスを返す。
	 * 選択されているノードが存在しない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getSelectionFile() {
		TreePath path = _tree.getSelectionPath();
		if (path != null) {
			return ((DefaultFileTreeNode)path.getLastPathComponent()).getFileObject();
		} else {
			return null;
		}
	}

	/**
	 * 指定された抽象パスが示すファイルを選択する。指定された抽象パスがファイルを示す
	 * 場合は、上位のフォルダを選択する。
	 * このメソッドでは、ツリーに表示されていない抽象パスが指定された場合は、選択を
	 * 解除する。
	 * @param file	選択するファイルを示す抽象パスを指定する。<tt>null</tt> が指定された
	 * 				場合は選択を解除する。
	 * @return	選択された場合は <tt>true</tt>、選択が解除された場合は <tt>false</tt> を返す。
	 */
	public boolean setSelectionFile(VirtualFile file) {
		// 引数が null なら、選択解除
		if (file == null) {
			_tree.clearSelection();
			return false;
		}
		
		// ファイルフィルターで表示を許可されたファイルを取得
		VirtualFileFilter filter = _tree.getModel().getFileFilter();
		if (filter != null) {
			do {
				if (filter.accept(file)) {
					break;
				}
				file = file.getParentFile();
			} while (file != null);
		}
		
		// 引数がディレクトリではないなら、親のディレクトリをターゲットとする
		if (!file.isDirectory()) {
			file = file.getParentFile();
			if (file == null) {
				// 親のディレクトリが存在しないなら、選択解除
				_tree.clearSelection();
				return false;
			}
		}
		
		// ルートノードから子ノードを辿り、指定された抽象パスまでのツリーパスを生成
		TreePath targetTreePath = _tree.getModel().getTreePathForFile(file);
		if (targetTreePath == null) {
			_tree.clearSelection();
			return false;
		} else {
			_tree.getModel().reload();
			_tree.expandPath(targetTreePath.getParentPath());
			_tree.setSelectionPath(targetTreePath);
			return true;
		}
	}

	/**
	 * ルートノードを起点に、表示されているすべてのツリーノードを
	 * 最新の情報に更新する。
	 */
	public void refreshAllTree() {
		Object rootNode = _tree.getModel().getRoot();
		if (rootNode != null) {
			_tree.refreshTree(new TreePath(rootNode));
		}
	}

	/**
	 * 選択されているノードを起点に、そのノードの下位で表示されている
	 * すべてのツリーノードを最新の情報に更新する。
	 */
	public void refreshSelectedTree() {
		if (_tree.getSelectionCount() > 0) {
			TreePath[] paths = _tree.getSelectionPaths();
			for (TreePath path : paths) {
				_tree.refreshTree(path);
			}
		}
	}

	/**
	 * 指定された抽象パスを持つノードの表示を更新する。
	 * 表示されていないノードの場合や、ツリー階層内の抽象パスではない
	 * 場合は何もしない。
	 * @param targetFile	対象の抽象パス
	 */
	public void refreshFileTree(VirtualFile targetFile) {
		TreePath path = _tree.getModel().getTreePathForFile(targetFile);
		if (path != null) {
			_tree.refreshTree(path);
		}
	}

	/**
	 * 選択されているパスの終端ノードが表示されるよう、
	 * スクロールする。
	 */
	public void scrollSelectionPathToVisible() {
		TreePath path = _tree.getSelectionPath();
		if (path != null) {
			_tree.scrollPathToVisible(path);
		}
	}
	
	public void setFocusToTree() {
		_tree.setFocus();
	}
	
	public void setFocusToInputField() {
		if (_inputPanel.isVisible() && !_inputField.hasFocus()) {
			SwingUtilities.invokeLater(_hFocusField);
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
			setInputValidator(_validator);
		}
		
		// 選択されているノードを表示
		scrollSelectionPathToVisible();
		
		// ボタンの表示を更新
		updateButtons();
		
		// 入力フィールドが表示されている場合は、
		// 入力フィールドにフォーカスを設定
		if (isInputFieldVisible()) {
			setFocusToInputField();
		}
	}

	@Override
	protected void dialogClose(int result) {
		// フィールドバリデータの関連を切断
		setInputValidator(null);
		
		// 基本処理
		super.dialogClose(result);
	}

	@Override
	protected void doApplyAction() {
		// このボタンはディレクトリ作成ボタンとして機能する
		if (!isSelectionEmpty()) {
			TreePath treepath = getSelectionPath();
			if (canCreateFolder((DefaultFileTreeNode)treepath.getLastPathComponent())) {
				VirtualFile selectedFile = getSelectionFile();
				String strNewName = FileDialogManager.showSimpleCreateFolderDialog(this, selectedFile);
				if (!Strings.isNullOrEmpty(strNewName)) {
					// フォルダ作成(エラーメッセージはメソッド内で出力)
					TreePath newDirPath = _tree.createDirectory(this, treepath, strNewName);
					if (newDirPath != null) {
						// フォルダ作成成功
						_createdFolders.add(((DefaultFileTreeNode)newDirPath.getLastPathComponent()).getFileObject());
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
		// 親フォルダが選択されていなければ、処理しない
		if (isSelectionEmpty()) {
			return false;
		}
		
		// 現在のファイルが選択可能かをチェックする
		if (!canChooseFile((DefaultFileTreeNode)getSelectionPath().getLastPathComponent())) {
			getOkButton().setEnabled(false);
			return false;
		}
		
		// バリデータがあれば、入力文字列をチェック
		if (!verifyInputField()) {
			return false;
		}
		
		// ダイアログを閉じる
		return true;
	}

	/**
	 * ツリーの選択状態が変更されたときに呼び出されるイベントハンドラ
	 * @param tse	<code>TreeSelectionEvent</code> オブジェクト
	 */
	protected void onTreeSelectionChanged(TreeSelectionEvent tse) {
		TreePath path = _tree.getSelectionPath();
		if (path == null) {
			_treePath.setText(EmptyTreePathText);
			if (_validator != null) {
				_validator.clearAlreadyExistsFilenames();
			}
		} else {
			String relPath = _tree.getModel().formatFilePath(path);
			_treePath.setText(relPath);
			if (_validator != null) {
				DefaultFileTreeNode node = (DefaultFileTreeNode)path.getLastPathComponent();
				_validator.setAlreadyExistsFilenames(node.getFileObject());
			}
		}
		
		updateButtons();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void updateButtons() {
		TreePath path = getSelectionPath();
		boolean toApplyEnable = false;
		boolean toOkEnable    = false;
		if (path != null) {
			DefaultFileTreeNode node = (DefaultFileTreeNode)path.getLastPathComponent();
			toApplyEnable = canCreateFolder(node);
			toOkEnable    = canChooseFile(node);
		}
		getApplyButton().setEnabled(toApplyEnable);
		getOkButton().setEnabled(toOkEnable);
	}
	
	protected boolean canCreateFolder(DefaultFileTreeNode node) {
		boolean allow = false;
		if (node.isDirectory()) {
			allow = true;
		}
		return allow;
	}
	
	protected boolean canChooseFile(DefaultFileTreeNode node) {
		boolean allow = false;
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
		//--- tree component set
		this._treeLabel = new JLabel();
		this._treePath  = createTreePathLabel();
		this._tree      = createTreeComponent();
		//--- input component set
		this._inputLabel = new JLabel();
		this._inputField = createInputField();
		this._inputPanel = new JPanel(new BorderLayout());
		this._inputPanel.add(this._inputField, BorderLayout.CENTER);
		this._inputPanel.add(this._inputLabel, BorderLayout.NORTH);
		//--- Chooser components
		this._lblDesc = createDescriptionLabel();
		JPanel treePanel = createTreePanel();
		
		// create main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// setup layout
		mainPanel.add(treePanel, BorderLayout.CENTER);
		mainPanel.add(_lblDesc, BorderLayout.NORTH);
		
		// add to root pane
		this.add(mainPanel, BorderLayout.CENTER);
	}

	@Override
	protected void setupActions() {
		super.setupActions();
		
		// setup properties
		_treePath.setText(EmptyTreePathText);
		
		// setup actions
		_tree.addTreeSelectionListener(new TreeSelectionListener(){
			public void valueChanged(TreeSelectionEvent event) {
				onTreeSelectionChanged(event);
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
	 * モジュール実行定義用ツリーパネルを生成する。
	 * @return	生成されたコンポーネント
	 */
	protected JPanel createTreePanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		// setup scroll for tree
		JScrollPane scTree = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
											JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scTree.setViewportView(_tree);
		
		// setup layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//--- Tree Label
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(_treeLabel, gbc);
		//--- Tree Path Label
		gbc.gridy++;
		panel.add(_treePath, gbc);
		//--- Tree
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(scTree, gbc);
		//--- Input panel
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(CommonResources.DIALOG_CONTENT_MARGIN, 0, 0, 0);
		panel.add(_inputPanel, gbc);
		
		return panel;
	}

	/**
	 * ツリーで選択されたノードのパスを表示するラベルを生成する。
	 */
	protected JLabel createTreePathLabel() {
		JLabel label = new JLabel();
		Border ob = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		Border ib = BorderFactory.createEmptyBorder(2, 2, 2, 2);
		Border bd = BorderFactory.createCompoundBorder(ob, ib);
		label.setBorder(bd);
		return label;
	}

	/**
	 * ツリーコンポーネントを生成する。
	 */
	protected FileTree createTreeComponent() {
		// モデル生成
		AliasFileTreeData filedata = new MEDLocalRootFileTreeData(null, MExecDefFileManager.MEXECDEF_FILES_ROOT_DIRNAME);
		DefaultFileTreeModel model = new DefaultFileTreeModel(new DefaultFileTreeNode(filedata));
		
		// ツリー生成
		FileTree newTree = new FileTree(model, _hTree);
		newTree.setRootVisible(true);		// ルートノードを表示
		newTree.setShowsRootHandles(true);	// 最上位にハンドルを表示する
		newTree.setTooltipEnabled(false);	// ツールチップは表示しない
		newTree.setDragEnabled(false);		// ドラッグ禁止
		newTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		//newTree.setExpandsSelectedPaths(true);
		
		return newTree;
	}

	/**
	 * 入力フィールドを生成する。
	 */
	protected JTextField createInputField() {
		JTextField field = new JTextField();
		return field;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * 入力フィールドにフォーカスを設定するアクションクラス。
	 */
	protected class InputFieldFocusRequester implements Runnable {
		public void run() {
			if (!_inputField.requestFocusInWindow()) {
				_inputField.requestFocus();
			}
		}
	}

	/**
	 * ツリーコンポーネントのドラッグを禁止するハンドラ
	 */
	static protected class NoDragDropTreeHandler implements IDnDTreeHandler
	{
		public int acceptTreeDragOverDropAction(DnDTree tree, DropTargetDragEvent dtde, TreePath dragOverPath) {
			// ドロップを許可しない
			return DnDConstants.ACTION_NONE;
		}

		public boolean canTransferImport(DnDTree tree, DataFlavor[] transferFlavors) {
			// インポートを許可しない
			return false;
		}

		public Transferable createTransferable(DnDTree tree) {
			// 常に null を返す。
			return null;
		}

		public int getTransferSourceAction(DnDTree tree) {
			// ドラッグアクションはなし
			return DnDConstants.ACTION_NONE;
		}

		public boolean importTransferData(DnDTree tree, Transferable t) {
			// インポートしない
			return false;
		}

		public boolean dropTransferData(DnDTree tree, Transferable t, int action) {
			// 常に false を返す
			return false;
		}

		public Icon getTransferVisualRepresentation(DnDTree tree, Transferable t) {
			return null;
		}
	}
}
