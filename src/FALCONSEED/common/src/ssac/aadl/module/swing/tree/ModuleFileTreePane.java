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
 * @(#)ModuleFileTreePane.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.tree;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ssac.aadl.common.CommonResources;
import ssac.aadl.module.swing.FilenameValidator;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.logging.AppLogger;
import ssac.util.swing.tree.DnDTree;

/**
 * ワークスペースのファイル選択ペイン・コンポーネント。
 * ファイルを選択するためのツリーコンポーネントと、名前を入力する
 * テキストフィールドを持つパネル。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class ModuleFileTreePane extends JPanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String EmptyTreePathText = " ";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final InputFieldFocusRequester _hFocusField = new InputFieldFocusRequester();
	private final NoDragDropTreeHandler _hTree = new NoDragDropTreeHandler();

	/** ツリーコンポーネント **/
	protected ModuleTree	_tree;
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

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleFileTreePane() {
		super(new GridBagLayout());
		initialComponent();
	}

	/**
	 * 表示するコンポーネントオブジェクトを生成し、
	 * パネルにレイアウトする。
	 */
	private final void initialComponent() {
		// create Components
		//--- tree component set
		this._treeLabel = new JLabel();
		this._treePath  = createTreePathLabel();
		this._tree      = createTreeComponent();
		JScrollPane scTree = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
											JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scTree.setViewportView(this._tree);
		//--- input component set
		this._inputLabel = new JLabel();
		this._inputField = createInputField();
		this._inputPanel = new JPanel(new BorderLayout());
		this._inputPanel.add(this._inputField, BorderLayout.CENTER);
		this._inputPanel.add(this._inputLabel, BorderLayout.NORTH);
		
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
		this.add(_treeLabel, gbc);
		//--- Tree Path Label
		gbc.gridy++;
		this.add(_treePath, gbc);
		//--- Tree
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(scTree, gbc);
		//--- Input panel
		gbc.gridy++;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(CommonResources.DIALOG_CONTENT_MARGIN, 0, 0, 0);
		this.add(_inputPanel, gbc);
		
		// setup properties
		_treePath.setText(EmptyTreePathText);
		
		// setup actions
		_tree.addTreeSelectionListener(new TreeSelectionListener(){
			public void valueChanged(TreeSelectionEvent event) {
				onTreeSelectionChanged(event);
			}
		});
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたツリーノードの子に、指定された名前のフォルダを作成する。
	 * 作成後、このフォルダを選択状態とする。
	 * @param parentPath	親フォルダとなるノードを示すツリーパス
	 * @param newName		新しいフォルダ名
	 * @return	フォルダが作成された場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す
	 * @throws NullPointerException	<em>parentPath</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	次の場合に例外をスローする
	 * <ul>
	 * <li><em>newName</em> が <tt>null</tt> もしくは空文字列の場合</li>
	 * <li><em>parentPath</em> がこのツリーのモデルのノードを示すものではない場合
	 * <li><em>parentPath</em> の終端ノードがディレクトリではない場合
	 * </ul>
	 */
	public boolean createFolder(TreePath parentPath, String newName) {
		if (Strings.isNullOrEmpty(newName))
			throw new IllegalArgumentException("newName argument is empty.");
		ModuleFileTreeModel model = _tree.getModel();
		ModuleFileTreeNode parentNode = (ModuleFileTreeNode)parentPath.getLastPathComponent();
		if (!parentNode.isNodeAncestor((ModuleFileTreeNode)model.getRoot())) {
			throw new IllegalArgumentException("parentPath last node is not decsending from Tree root.");
		}
		if (!parentNode.isDirectory()) {
			throw new IllegalArgumentException("parentPath last node is not directory.");
		}
		
		// フォルダ作成
		VirtualFile newFolder = parentNode.getFileObject().getChildFile(newName);
		boolean ret;
		try {
			ret = newFolder.mkdir();
			if (!ret) {
				AppLogger.error("ModuleFolderTreePane#createFolder - Could not create new folder : \"" + newFolder.toString() + "\"");
			}
		} catch (SecurityException ex) {
			ret = false;
			AppLogger.error("ModuleFolderTreePane#createFolder - Failed to create new folder : \"" + newFolder.toString() + "\"", ex);
		}
		if (!ret)
			return false;
		
		// ツリーへ反映
		model.refreshNode(parentNode);
		TreePath newFolderPath = parentPath.pathByAddingChild(parentNode.getChildNodeByFile(newFolder));
		_tree.refreshTree(newFolderPath);
		_tree.expandPath(parentPath);
		_tree.setSelectionPath(newFolderPath);
		return true;
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
						((ModuleFileTreeNode)path.getLastPathComponent()).getFileObject()
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

	/**
	 * ツリーコンポーネントのルートに設定されている抽象パスを取得する。
	 * ルートが設定されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getTreeRootFile() {
		return _tree.getModel().getRootFile();
	}

	/**
	 * ツリーコンポーネントのルートを設定する。
	 * @param newRootFile	新たに設定する抽象パス
	 */
	public void setTreeRootFile(VirtualFile newRootFile) {
		setTreeRootFile(newRootFile, ModuleFileTreeModel.getDefaultFileFilter());
	}
	
	public void setTreeRootFile(VirtualFile newRootFile, VirtualFileFilter filter) {
		ModuleFileTreeModel newModel = createDefaultTreeModel(newRootFile, filter);
		_tree.setModel(newModel);
	}
	
	public ModuleFileTreeModel getTreeModel() {
		return _tree.getModel();
	}
	
	public void setTreeModel(ModuleFileTreeModel newModel) {
		_tree.setModel(newModel);
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
			return ((ModuleFileTreeNode)path.getLastPathComponent()).getFileObject();
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
	
	//
	// Events
	//
	
	public void addTreeSelectionListener(TreeSelectionListener l) {
		_tree.addTreeSelectionListener(l);
	}
	
	public void removeTreeSelectionListener(TreeSelectionListener l) {
		_tree.removeTreeSelectionListener(l);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

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
	protected ModuleTree createTreeComponent() {
		ModuleTree tree = new ModuleTree();
		tree.setDragEnabled(false);		// ドラッグ禁止
		tree.setTreeHandler(_hTree);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		//tree.setExpandsSelectedPaths(true);
		return tree;
	}

	/**
	 * 入力フィールドを生成する。
	 */
	protected JTextField createInputField() {
		JTextField field = new JTextField();
		return field;
	}

	/**
	 * 指定した抽象パスをルートとするツリーモデルを生成する。
	 * @param rootFile	ルートとなる抽象パスを示す <code>VirtualFile</code> オブジェクト
	 * @return	生成された <code>EditorFileTreeModel</code> オブジェクト
	 */
	protected ModuleFileTreeModel createDefaultTreeModel(VirtualFile rootFile, VirtualFileFilter vfFilter) {
		ModuleFileTreeModel newModel = new ModuleFileTreeModel(rootFile);
		if (vfFilter != null) {
			newModel.setFileFilter(vfFilter);
		}
		return newModel;
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
			ModuleFileTreeNode node = (ModuleFileTreeNode)path.getLastPathComponent();
			VirtualFile file = node.getFileObject();
			String relPath = file.relativePathFrom(_tree.getRootFile(), Files.CommonSeparatorChar);
			_treePath.setText(Strings.isNullOrEmpty(relPath) ? EmptyTreePathText : relPath);
			if (_validator != null) {
				_validator.setAlreadyExistsFilenames(file);
			}
		}
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
	 * 
	 * @version 1.14	2009/12/09
	 * @since 1.14
	 */
	protected class NoDragDropTreeHandler extends ModuleTree.DefaultTreeHandler
	{

		@Override
		public int acceptTreeDragOverDropAction(DnDTree tree, DropTargetDragEvent dtde, TreePath dragOverPath) {
			// ドロップを許可しない
			return DnDConstants.ACTION_NONE;
		}

		@Override
		public boolean canTransferImport(DnDTree tree, DataFlavor[] transferFlavors) {
			// インポートを許可しない
			return false;
		}

		@Override
		public Transferable createTransferable(DnDTree tree) {
			// 常に null を返す。
			return null;
		}

		@Override
		public int getTransferSourceAction(DnDTree tree) {
			// ドラッグアクションはなし
			return DnDConstants.ACTION_NONE;
		}

		@Override
		public boolean importTransferData(DnDTree tree, Transferable t) {
			// インポートしない
			return false;
		}

		@Override
		public boolean dropTransferData(DnDTree tree, Transferable t, int action) {
			// 常に false を返す
			return false;
		}
	}
}
