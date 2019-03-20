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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ModuleFileChooser.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.tree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.TreePath;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.aadl.module.swing.FilenameValidator;
import ssac.util.Strings;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.swing.Application;
import ssac.util.swing.BasicDialog;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.SwingTools;

/**
 * ツリー形式のファイル選択ダイアログ
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class ModuleFileChooser extends BasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final int FILE_ONLY = 0;
	static public final int FOLDER_ONLY = 1;
	static public final int FILE_OR_FOLDER = 2;
	
	static private final Dimension DEFAULT_MIN_SIZE = new Dimension(320, 120);
	
	static private final VirtualFileFilter _filterForMainModule = new MainModuleFileFilter();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 選択方式 **/
	private int					_selectionMode = FILE_ONLY;
	/** モジュールパッケージに含まれるファイルの選択を許可するフラグ **/
	private boolean				_allowAssignedModulePackageFile = false;
	/** 説明表示用ラベルコンポーネント **/
	private JTextPane				_lblDesc;
	/** ツリーを含むペイン **/
	private ModuleFileTreePane		_treePane;
	/** ファイル名のバリデータ **/
	private FilenameValidator		_validator;
	/** 新規作成したフォルダのパス **/
	private final ArrayList<VirtualFile> _createdFolders = new ArrayList<VirtualFile>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleFileChooser(Frame owner,
								boolean rootVisible, boolean allowCreateFolder,
								VirtualFile root, VirtualFile initialSelection,
								String title, String description,
								String treeLabel, String inputLabel,
								VirtualFileFilter filter,
								FilenameValidator validator)
	{
		super(owner, title, true);
		setupComponents(rootVisible, allowCreateFolder, root, initialSelection,
						description, treeLabel, inputLabel, filter, validator);
	}
	
	public ModuleFileChooser(Dialog owner,
								boolean rootVisible, boolean allowCreateFolder,
								VirtualFile root, VirtualFile initialSelection,
								String title, String description,
								String treeLabel, String inputLabel,
								VirtualFileFilter filter,
								FilenameValidator validator)
	{
		super(owner, title, true);
		setupComponents(rootVisible, allowCreateFolder, root, initialSelection,
				description, treeLabel, inputLabel, filter, validator);
	}
	
	protected void setupComponents(boolean rootVisible, boolean allowCreateFolder,
									VirtualFile root, VirtualFile initialSelection,
									String description, String treeLabel, String inputLabel,
									VirtualFileFilter filter,
									FilenameValidator validator)
	{
		setDescription(description);
		btnApply.setVisible(allowCreateFolder);
		_treePane.setTreeLabelText(treeLabel);
		_treePane.setRootVisible(rootVisible);
		_treePane.setTreeRootFile(root, filter);
		_treePane.setInputLabelText(inputLabel);
		if (initialSelection != null) {
			_treePane.setSelectionFile(initialSelection);
		}
		_validator = validator;
		
		// setup actions
		_treePane.addTreeSelectionListener(new TreeSelectionListener(){
			public void valueChanged(TreeSelectionEvent e) {
				onTreeSelectionChanged(e);
			}
		});
		
		// setup dialog
		setResizable(true);
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = DEFAULT_MIN_SIZE;
		}
		this.setMinimumSize(dmMin);
		setKeepMinimumSize(true);
		
		// restore settings
		this.pack();
		this.setLocationRelativeTo(null);
		//loadSettings(null, null);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * メインモジュールを選択するためのファイル選択ダイアログを表示する。
	 * @param parentComponent	親コンポーネント
	 * @param rootProject	選択範囲とするルートプロジェクト
	 * @param initialSelection	初期選択位置
	 * @return	選択したファイルを返す。そうでない場合は <tt>null</tt> を返す。
	 */
	static public VirtualFile chooseMailModuleFile(Component parentComponent,
													VirtualFile rootProject,
													VirtualFile initialSelection)
	{
		String dlgTitle = CommonMessages.getInstance().Choose_mainmodule;
		String dlgTreeLabel = null;
		
		// ModuleFileChooser 生成
		Window owner = SwingTools.getWindowForComponent(parentComponent);
		ModuleFileChooser chooser;
		if (owner instanceof Dialog) {
			chooser = new ModuleFileChooser((Dialog)owner,
							true, false, rootProject, initialSelection,
							dlgTitle, null, dlgTreeLabel, null,
							_filterForMainModule,
							null);
			
		} else {
			chooser = new ModuleFileChooser((Frame)owner,
							true, false, rootProject, initialSelection,
							dlgTitle, null, dlgTreeLabel, null,
							_filterForMainModule,
							null);
		}

		// 表示
		chooser.setFileSelectionMode(FILE_ONLY);
		chooser.setAllowAssignedModulePackageFileSelection(false);
		chooser.setVisible(true);
		chooser.dispose();
		if (chooser.getDialogResult() == IDialogResult.DialogResult_OK) {
			return chooser.getSelectionFile();
		} else {
			return null;
		}
	}
	
	public boolean isFileSelectionEnabled() {
		return (_selectionMode==FILE_ONLY || _selectionMode==FILE_OR_FOLDER);
	}
	
	public boolean isFolderSelectionEnabled() {
		return (_selectionMode==FOLDER_ONLY || _selectionMode==FILE_OR_FOLDER);
	}

	/**
	 * 現在のファイル選択モードを返す。デフォルトは <code>ModuleFileChooser.FILE_ONLY</code> となる。
	 * @return	選択を許可するファイルの種類。次のうちのどれか
	 * <ul>
	 * <li>ModuleFileChooser.FILE_ONLY
	 * <li>ModuleFileChooser.FOLDER_ONLY
	 * <li>ModuleFileChooser.FILE_OR_FOLDER
	 * </ul>
	 */
	public int getFileSelectionMode() {
		return _selectionMode;
	}

	/**
	 * ファイル選択モードを設定する。このファイル選択モードは、最終的にツリーで
	 * 選択されているファイルもしくはフォルダで決定可能かどうかを設定するものである。
	 * したがって、ツリー表示上の選択は単一選択のまま影響をうけるものではない。<br>
	 * <em>mode</em> に <code>ModuleFileChooser.FILE_ONLY</code> が指定された場合は
	 * ファイルのみが選択可能となる。<br>
	 * <em>mode</em> に <code>ModuleFileChooser.FOLDER_ONLY</code> が指定された場合は
	 * フォルダのみが選択可能となる。<br>
	 * <em>mode</em> に <code>ModuleFileChooser.FILE_OR_FOLDER</code> が指定された場合は
	 * ファイルとフォルダのどちらも選択可能となる。
	 * @param mode	選択を許可するファイルの種類
	 * <ul>
	 * <li>ModuleFileChooser.FILE_ONLY
	 * <li>ModuleFileChooser.FOLDER_ONLY
	 * <li>ModuleFileChooser.FILE_OR_FOLDER
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
	 * モジュールパッケージに含まれるファイルやフォルダも選択可能であれば <tt>true</tt> を返す。
	 * デフォルトは <tt>false</tt> である。
	 */
	public boolean canAssignedModulePackageFileSelection() {
		return _allowAssignedModulePackageFile;
	}

	/**
	 * モジュールパッケージに含まれるファイルやフォルダも選択可能とするかを
	 * 設定する。この設定は、最終的にツリーで選択されているファイルもしくは
	 * フォルダで決定可能かどうかを設定するものである。
	 * @param allow	モジュールパッケージに含まれるファイルを選択可能とする
	 * 				場合は <tt>true</tt> を指定する。
	 */
	public void setAllowAssignedModulePackageFileSelection(boolean allow) {
		if (_allowAssignedModulePackageFile != allow) {
			_allowAssignedModulePackageFile = allow;
			updateButtons();
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
	// Internal methods
	//------------------------------------------------------------
	
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
	
	protected ModuleFileTreePane createTreePane() {
		return new ModuleFileTreePane();
	}

	@Override
	protected void setupMainPanel() {
		super.setupMainPanel();
		
		// create components
		this._lblDesc = createDescriptionLabel();
		this._treePane = createTreePane();
		
		// setup main panel layout
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		mainPanel.add(_treePane, BorderLayout.CENTER);
		mainPanel.add(_lblDesc, BorderLayout.NORTH);
	}

	@Override
	protected void setupButtonPanel() {
		int maxWidth = 0;
		int maxHeight = 0;
		Dimension dim;
		this.btnApply.setText(CommonMessages.getInstance().InputTitle_NewFolder);
		this.btnCancel.setText(CommonMessages.getInstance().Button_Cancel);
		this.btnOK.setText(CommonMessages.getInstance().Button_OK);
		dim = this.btnCancel.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		dim = this.btnOK.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		//---
		dim = new Dimension(maxWidth, maxHeight);
		this.btnOK    .setPreferredSize(dim);
		this.btnCancel.setPreferredSize(dim);
		
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(this.btnApply);
		btnBox.add(Box.createHorizontalGlue());
		btnBox.add(this.btnOK);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(this.btnCancel);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(Box.createRigidArea(new Dimension(0, dim.height+10)));
		
		// setup default button
		this.btnOK.setDefaultCapable(true);
		this.getContentPane().add(btnBox, BorderLayout.SOUTH);
		this.getRootPane().setDefaultButton(btnOK);
		
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
	
	protected void updateButtons() {
		TreePath path = _treePane.getSelectionPath();
		boolean toApplyEnable = false;
		boolean toOkEnable    = false;
		if (path != null) {
			ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
			toApplyEnable = canCreateFolder(node);
			toOkEnable    = canChooseFile(node);
		}
		btnApply.setEnabled(toApplyEnable);
		btnOK.setEnabled(toOkEnable);
	}
	
	protected boolean canCreateFolder(ModuleFileTreeNode node) {
		boolean allow = false;
		if (!node.isRoot()) {
			if (node.isDirectory()) {
				if (!node.isAssignedModulePackageFile()
					&& !node.isModulePackageRoot())
				{
					allow = true;
				}
			}
		}
		return allow;
	}
	
	protected boolean canChooseFile(ModuleFileTreeNode node) {
		boolean allow = false;
		if (!node.isRoot()) {
			if (node.isDirectory()) {
				if (isFolderSelectionEnabled()) {
					if (canAssignedModulePackageFileSelection()) {
						allow = true;
					}
					else if (!node.isAssignedModulePackageFile()) {
						if (node.isProjectRoot() || !node.isModulePackageRoot()) {
							allow = true;
						}
					}
				}
			}
			else {
				if (isFileSelectionEnabled()) {
					if (canAssignedModulePackageFileSelection()) {
						allow = true;
					}
					else if (!node.isAssignedModulePackageFile()) {
						allow = true;
					}
				}
			}
		}
		return allow;
	}

	@Override
	protected Dimension getDefaultSize() {
		return null;
	}

	@Override
	protected void initDialog() {
		// 基本処理
		super.initDialog();
		
		// フィールドバリデータを接続
		if (_validator != null) {
			_treePane.setInputValidator(_validator);
		}
		
		// 選択されているノードを表示
		_treePane.scrollSelectionPathToVisible();
		
		// ボタンの表示を更新
		updateButtons();
		
		// 入力フィールドが表示されている場合は、
		// 入力フィールドにフォーカスを設定
		if (_treePane.isInputFieldVisible()) {
			_treePane.setFocusToInputField();
		}
	}

	@Override
	protected void dialogClose(int result) {
		// フィールドバリデータの関連を切断
		_treePane.setInputValidator(null);
		
		// 基本処理
		super.dialogClose(result);
	}
	
	protected void onTreeSelectionChanged(TreeSelectionEvent tse) {
		updateButtons();
	}

	@Override
	protected void onButtonApply() {
		// このボタンはディレクトリ作成ボタンとして機能する
		if (!_treePane.isSelectionEmpty()) {
			TreePath path = _treePane.getSelectionPath();
			if (canCreateFolder((ModuleFileTreeNode)path.getLastPathComponent())) {
				VirtualFile selectedFile = _treePane.getSelectionFile();
				String strNewName = FileDialogManager.showSimpleCreateFolderDialog(this, selectedFile);
				if (!Strings.isNullOrEmpty(strNewName)) {
					// フォルダ作成
					boolean ret = _treePane.createFolder(path, strNewName);
					if (!ret) {
						// フォルダ作成失敗
						VirtualFile errFile = ((ModuleFileTreeNode)path.getLastPathComponent()).getFileObject().getChildFile(strNewName);
						String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_CREATE, null, errFile);
						Application.showErrorMessage(errmsg);
					} else {
						_createdFolders.add(_treePane.getSelectionFile());
					}
				}
			}
			else {
				btnApply.setEnabled(false);
			}
		}
	}

	@Override
	protected boolean onButtonOK() {
		// 親フォルダが選択されていなければ、処理しない
		if (_treePane.isSelectionEmpty()) {
			return false;
		}
		
		// 現在のファイルが選択可能かをチェックする
		if (!canChooseFile((ModuleFileTreeNode)_treePane.getSelectionPath().getLastPathComponent())) {
			btnOK.setEnabled(false);
			return false;
		}
		
		// バリデータがあれば、入力文字列をチェック
		if (!_treePane.verifyInputField()) {
			return false;
		}
		
		// ダイアログを閉じる
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class MainModuleFileFilter implements VirtualFileFilter
	{
		public boolean accept(VirtualFile pathname) {
			// 隠しファイルは許可しない
			if (pathname.isHidden()) {
				return false;
			}
			
			if (pathname.isDirectory()) {
				// プロジェクトルートではないモジュールパッケージルートは、
				// 内部変更不可とするので許可しない
				boolean rootProj = ModuleFileTreeModel.isProjectRootFile(pathname);
				boolean rootPack = ModuleFileTreeModel.isModulePackageRootFile(pathname);
				if (rootPack && !rootProj) {
					return false;
				}
			}
			else if (!ModuleFileManager.isAadlSourceFile(pathname)
					&& !ModuleFileManager.isAadlMacroFile(pathname)
					&& !ModuleFileManager.isJarFile(pathname))
			{
				// AADLソース、AADLマクロ、JARモジュール以外は
				// メインモジュールとはならないので、許可しない
				return false;
			}
			
			// 上記以外のものは許可する
			return true;
		}
	}
}
