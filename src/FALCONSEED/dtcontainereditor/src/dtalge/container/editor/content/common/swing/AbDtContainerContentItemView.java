/*
 * @(#)DtContainerContentDetailView.java	1.0.0	2022/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.IDtContainerEditView;
import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;
import dtalge.container.editor.menu.DtContainerEditorMenuResources;
import dtalge.container.editor.view.DtContainerEditorFrame;
import ssac.util.logging.AppLogger;
import ssac.util.swing.menu.IMenuActionHandler;

/**
 * {@link DtContainerContentDetailView} に表示する編集機能別ビューの共通実装。
 * 
 * @version 1.0.0
 */
public class AbDtContainerContentItemView extends JPanel implements IMenuActionHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** このビューを表示する親コンテナコンポーネント、{@code null} ではない **/
	protected DtContainerContentDetailView	_parentContainer;

	/** 編集対象のコンテントパスを表示するテキストコンポーネント **/
	private JTextComponent	_stcContentPath;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbDtContainerContentItemView(DtContainerContentDetailView parentContainer) {
		super(new BorderLayout());
		if (parentContainer == null)
			throw new NullPointerException("Parent container view is null");
		_parentContainer = parentContainer;
	}
	
	public void initialComponent() {
		// create componenents
		createComponents();
		
		// setup layouts
		setupLayout();
		
		// setup actions
		setupActions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このビューが所属するメインフレームを取得する。
	 * @return	{@code null} ではない、メインフレーム
	 */
	public DtContainerEditorFrame getFrame() {
		return _parentContainer.getFrame();
	}
	
	/**
	 * このビューが所属する親エディタビューを取得する。
	 * 親エディタビューは、メインフレームにタブで表示される、ドキュメントごとの編集ビューを意味する。
	 * @return	{@code null} ではない、親エディタビュー
	 */
	public IDtContainerEditView getParentEditorView() {
		return _parentContainer.getParentEditorView();
	}
	
	/**
	 * このビューに設定されている対象ツリーノードを取得する。
	 * @return	設定されている編集対象ツリーノード、設定されていない場合は {@code null}
	 */
	public IDtContainerContentTreeNode getTargetTreeNode() {
		return _parentContainer.getTargetTreeNode();
	}
	
	/**
	 * オブジェクトパスを表示するテキストフィールドを取得する。
	 * @return	オブジェクトパスを表示するテキストフィールド
	 */
	public JTextComponent getContentPathComponent() {
		return _stcContentPath;
	}
	
	/**
	 * 現在設定されているオブジェクトパスを取得する。
	 * @return	設定されているオブジェクトパスを表す文字列
	 */
	public String getContentPathString() {
		return _stcContentPath.getText();
	}
	
	/**
	 * オブジェクトパスを設定する。
	 * @param newPath	新しいオブジェクトパスを表す文字列
	 */
	public void setContentPathString(String newPath) {
		_stcContentPath.setText(newPath);
	}
	
	/**
	 * このビュー内のメインビューのコンポーネントにフォーカス設定をリクエストする。
	 */
	public void requestFocusInComponent() {
		// place holder
	}
	
	/**
	 * 現在のノードで表示を更新する。
	 */
	public void refreshViewingStatus() {
		// place holder
	}
	
	/**
	 * 編集対象コンテントの位置表示を更新する。
	 */
	public void refreshContentPathByTargetTreeNode() {
		IDtContainerContentTreeNode ndTarget = getTargetTreeNode();
		if (ndTarget == null) {
			// 表示テキストなし
			_stcContentPath.setText("");
		}
		else {
			// パスを表示
			_stcContentPath.setText(ndTarget.getContentPathString());
		}
	}
	
	/**
	 * オブジェクトパスの一部が選択されていれば、コピー可能と判定する。
	 * @return	コピー可能なら {@code true}
	 */
	public boolean canContentPathCopy() {
		if (_stcContentPath.getSelectionEnd() > 0 && _stcContentPath.getSelectionEnd() > _stcContentPath.getSelectionStart()) {
			// オブジェクトパスの一部選択あり
			return true;
		}
		else {
			// オブジェクトパスの選択なし
			return false;
		}
	}
	
	/**
	 * オブジェクトパスの一部が選択されていれば、その選択範囲の文字列をクリップボードにコピーする。
	 * 選択されていない場合は、何も行わない。
	 */
	public void doContentPathCopy() {
		if (canContentPathCopy()) {
			_stcContentPath.copy();
		}
	}

	//------------------------------------------------------------
	// Menu operations
	//------------------------------------------------------------

	/**
	 * コンポーネントが編集可能であるかを検証する。
	 * 
	 * @return 編集可能であれば true
	 */
	public boolean canEdit() {
		return false;
	}
	
	/**
	 * アンドゥ操作が可能かどうかを判定する。
	 * @return	操作が可能なら {@code true}
	 */
	public boolean canUndo() {
		return false;
	}
	
	/**
	 * リドゥ操作が可能かどうかを判定する。
	 * @return	操作が可能なら {@code true}
	 */
	public boolean canRedo() {
		return false;
	}
	
	/**
	 * 切り取り操作が可能かどうかを判定する。
	 * @return	操作が可能なら {@code true}
	 */
	public boolean canCut() {
		return false;
	}
	
	/**
	 * コピー操作が可能かどうかを判定する。
	 * @return	操作が可能なら {@code true}
	 */
	public boolean canCopy() {
		return canContentPathCopy();
	}
	
	/**
	 * 貼り付け操作が可能かどうかを判定する。
	 * @return	操作が可能なら {@code true}
	 */
	public boolean canPaste() {
		return false;
	}
	
	/**
	 * 削除操作が可能かどうかを判定する。
	 * @return	操作が可能なら {@code true}
	 */
	public boolean canDelete() {
		return false;
	}
	
	/**
	 * アンドゥ操作を実行する。
	 */
	public void undo() {
		// place holder
	}
	
	/**
	 * リドゥ操作を実行する。
	 */
	public void redo() {
		// place holder
	}
	
	/**
	 * 切り取り操作を実行する。
	 */
	public void cut() {
		// place holder
	}
	
	/**
	 * コピー操作を実行する。
	 */
	public void copy() {
		doContentPathCopy();
	}
	
	/**
	 * 貼り付け操作を実行する。
	 */
	public void paste() {
		// place holder
	}
	
	/**
	 * 削除操作を実行する。
	 */
	public void delete() {
		// place holder
	}

	//------------------------------------------------------------
	// Menu event handler
	//------------------------------------------------------------
	
	/**
	 * メニュー項目の選択時に呼び出されるハンドラ・メソッド。
	 * 
	 * @param command	このイベント要因のコマンド文字列
	 * @param source	このイベント要因のソースオブジェクト。
	 * 					ソースオブジェクトが未定義の場合は <tt>null</tt>。
	 * @param action	このイベント要因のソースオブジェクトに割り当てられたアクション。
	 * 					アクションが未定義の場合は <tt>null</tt>。
	 * @return	このハンドラ内で処理が完結した場合は <tt>true</tt> を返す。イベントシーケンスの
	 * 			別のハンドラに処理を委譲する場合は <tt>false</tt> を返す。
	 */
	@Override
	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		if (DtContainerEditorMenuResources.ID_EDIT_UNDO.equals(command)) {
			onMenuSelectedEditUndo();
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_EDIT_REDO.equals(command)) {
			onMenuSelectedEditRedo();
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_EDIT_CUT.equals(command)) {
			onMenuSelectedEditCut();
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_EDIT_COPY.equals(command)) {
			onMenuSelectedEditCopy();
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_EDIT_PASTE.equals(command)) {
			onMenuSelectedEditPaste();
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_EDIT_DELETE.equals(command)) {
			onMenuSelectedEditDelete();
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_CUT.equals(command)) {
			onMenuSelectedEditCut();
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_COPY.equals(command)) {
			onMenuSelectedEditCopy();
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_PASTE.equals(command)) {
			onMenuSelectedEditPaste();
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_DELETE.equals(command)) {
			onMenuSelectedEditDelete();
			return true;
		}
		
		// その他は、処理しない
		return false;
	}
	
	/**
	 * メニュー項目の更新要求時に呼び出されるハンドラ・メソッド。
	 * 現時点では、このメソッドでは処理を行わないため、常に {@code false} を返す。
	 * 
	 * @param command	このイベント要因のコマンド文字列
	 * @param source	このイベント要因のソースオブジェクト。
	 * 					ソースオブジェクトが未定義の場合は <tt>null</tt>。
	 * @param action	このイベント要因のソースオブジェクトに割り当てられたアクション。
	 * 					アクションが未定義の場合は <tt>null</tt>。
	 * @return	このハンドラ内で処理が完結した場合は <tt>true</tt> を返す。イベントシーケンスの
	 * 			別のハンドラに処理を委譲する場合は <tt>false</tt> を返す。
	 */
	@Override
	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (DtContainerEditorMenuResources.ID_EDIT_UNDO.equals(command)) {
			action.setEnabled(canUndo());
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_EDIT_REDO.equals(command)) {
			action.setEnabled(canRedo());
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_EDIT_CUT.equals(command)) {
			action.setEnabled(canCut());
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_EDIT_COPY.equals(command)) {
			action.setEnabled(canCopy());
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_EDIT_PASTE.equals(command)) {
			action.setEnabled(canPaste());
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_EDIT_DELETE.equals(command)) {
			action.setEnabled(canDelete());
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_CUT.equals(command)) {
			action.setEnabled(canCut());
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_COPY.equals(command)) {
			action.setEnabled(canCopy());
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_PASTE.equals(command)) {
			action.setEnabled(canPaste());
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_DELETE.equals(command)) {
			action.setEnabled(canDelete());
			return true;
		}
		
		// その他は、処理しない
		return false;
	}

	// Menu : [Edit]-[Undo]
	protected void onMenuSelectedEditUndo() {
		AppLogger.debug("catch [Edit]-[Undo] menu selection.");
		undo();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Redo]
	protected void onMenuSelectedEditRedo() {
		AppLogger.debug("catch [Edit]-[Redo] menu selection.");
		redo();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Cut]
	protected void onMenuSelectedEditCut() {
		AppLogger.debug("catch [Edit]-[Cut] menu selection.");
		cut();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Copy]
	protected void onMenuSelectedEditCopy() {
		AppLogger.debug("catch [Edit]-[Copy] menu selection.");
		copy();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Paste]
	protected void onMenuSelectedEditPaste() {
		AppLogger.debug("catch [Edit]-[Paste] menu selection.");
		paste();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Delete]
	protected void onMenuSelectedEditDelete() {
		AppLogger.debug("catch [Edit]-[Delete] menu selection.");
		delete();
		requestFocusInComponent();
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	/**
	 * オブジェクトパスを表示するテキストコンポーネントのキャレットが変更されたときに呼び出されるイベントハンドラ。
	 * 主に、テキストの選択状態の変更などを意味する。
	 * @param ce	キャレット変更イベントオブジェクト
	 */
	protected void onCaretChangedAtContentPathComponent(CaretEvent ce) {
		DtContainerEditorFrame frame = getFrame();
		if (frame != null) {
			frame.getMenuAction(DtContainerEditorMenuResources.ID_EDIT_COPY).setEnabled(canCopy());
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * このビューのコンポーネントを生成する。
	 */
	protected void createComponents() {
		// content path
		_stcContentPath = createContentPathTextComponent();
	}
	
	/**
	 * コンテントパスを表示するテキストコンポーネントを生成する。
	 * @return	生成されたテキストコンポーネント
	 */
	protected JTextComponent createContentPathTextComponent() {
		JTextField txt = new JTextField();
		txt.setEditable(false);
		return txt;
	}
	
	protected JComponent createContentPathPanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		
		JLabel lblPath = new JLabel(DtContainerEditorMessages.getInstance().editor_lbl_ObjectPath + ": ");
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbc.gridy = 0;
		//--- content path
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		pnl.add(lblPath, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		pnl.add(_stcContentPath, gbc);
		
		return pnl;
	}
	
	/**
	 * このビューのコンポーネントを配置する。
	 */
	protected void setupLayout() {
		this.add(createContentPathPanel(), BorderLayout.NORTH);
	}
	
	/**
	 * このビュー内のアクションを初期化する。
	 */
	protected void setupActions() {
		// place holder
		_stcContentPath.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				onCaretChangedAtContentPathComponent(e);
			}
		});
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
