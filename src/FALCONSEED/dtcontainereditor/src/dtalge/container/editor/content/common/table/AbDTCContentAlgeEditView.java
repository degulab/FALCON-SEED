/*
 * @(#)DTCContentAlgeEditView.java	1.1.0	2023/01/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.table;

import java.awt.BorderLayout;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;

import dtalge.container.editor.content.common.swing.AbDtContainerContentItemView;
import dtalge.container.editor.content.common.swing.DtContainerContentDetailView;
import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;
import dtalge.container.editor.menu.DtContainerEditorMenuBar;
import dtalge.container.editor.menu.DtContainerEditorMenuResources;
import dtalge.container.editor.view.DtContainerEditorFrame;
import ssac.aadl.common.CommonMessages;
import ssac.util.Strings;
import ssac.util.Validations;
import ssac.util.io.CsvReader;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.menu.MenuItemResource;
import ssac.util.swing.table.SpreadSheetColumnHeader;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * 交換代数元もしくはデータ代数元を編集するテーブルを配置したスクロール可能なビューの共通実装。
 * 
 * @version 1.1.0
 * @since 1.1.0
 */
public abstract class AbDTCContentAlgeEditView extends AbDtContainerContentItemView
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	static private KeyStroke[] _menuAccelerators = null;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
//	/** アンドゥマネージャー(将来用) **/
//	private UndoManager								_undoMan;
	/** このビューが管理するテーブルコンポーネント用データモデルの変更イベントを受け取るハンドラ **/
	private DTCTableModelHandler					_hTableModel;
//	/** このビューが管理するテーブルのアンドゥイベントハンドラ **/
//	private DTCTableUndoHandler						_hTableUndo;
	/** このビューが管理するテーブルコンポーネントの選択変更イベントを受けとるハンドラ **/
	private DTCTableSelectionHandler				_hTableSelection;
	/** このビューの管理するテーブルのセル情報の編集がコミットされたときのハンドラ **/
	private DTCTableCellInfoEditActionHandler		_ciEditCommitAction;
	/** このビューの管理するテーブルのセル情報の編集がキャンセルされたときのハンドラ **/
	private DTCTableCellInfoEditActionHandler		_ciEditCancelAction;
	
	/** 現在選択されているセルの総数 **/
	private int		_numSelectedCells;
	/** 選択されているセルが列全体を含む場合は {@code true} **/
	private boolean	_isSelectedAllColumns;
	/** 選択されているセルが単一であり、かつ編集可能なら {@code true} **/
	private boolean	_isSelectedEditableOneCell;
	
	/** テーブルヘッダー用ポップアップメニュー **/
	private JPopupMenu						_tableHeaderPopupMenu;

	/** 編集対象のデータモデル、設定されていない場合は {@code null} */
	protected AbDTCContentAlgeEditModel		_targetModel;
	/** 編集用テーブルペイン **/
	protected DTCContentAlgeEditTablePane	_paneTable;
	/** 編集用テーブルをビューに持つスクロールペイン **/
	protected JScrollPane					_paneScroll;
	
	private volatile boolean				_compoundEdits = false;
	private CompoundEdit					_packEdits = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbDTCContentAlgeEditView(DtContainerContentDetailView parentContainer) {
		super(parentContainer);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このビューに設定されている編集対象データモデルを取得する。
	 * @return	設定されている編集対象データモデル、設定されていない場合は {@code null}
	 */
	public AbDTCContentAlgeEditModel getEditModel() {
		return _targetModel;
	}
	
	/**
	 * このビューに、新しい編集対象データモデルを設定する。
	 * <em>newTargetTreeNode</em> が {@code null} ではない場合、<em>newTargetTreeNode</em> のユーザーデータが、
	 * {@link AbDTCContentAlgeEditModel} インスタンスでなければならない。
	 * @param newTargetTreeNode	設定する編集対象データモデルを保持するツリーノード、設定なしとする場合は {@code null}
	 * @throws IllegalArgumentException	<em>newTargetTreeNode</em> のユーザーデータが {@link AbDTCContentAlgeEditModel} インスタンスでない場合
	 */
	public void setEditModel(IDtContainerContentTreeNode newTargetTreeNode) {
		// validation
		AbDTCContentAlgeEditModel newEditModel;
		if (newTargetTreeNode != null) {
			if (!(newTargetTreeNode.getUserObject() instanceof AbDTCContentAlgeEditModel)) {
				// invalid user data
				throw new IllegalArgumentException("Target tree node has no DtContainerContentDtalgeEditModel instance : " + newTargetTreeNode.getContentPathString());
			}
			newEditModel = (AbDTCContentAlgeEditModel)newTargetTreeNode.getUserObject();
		}
		else {
			newEditModel = null;
		}

		// same instance?
		if (newEditModel == _targetModel)
			return;		// no changes
		
		// detach old model
		//IDtContainerContentTreeNode oldTreeNode = _targetTreeNode;
		IDtContainerContentTreeNode oldTreeNode = null;
		AbDTCContentAlgeEditModel oldEditModel = _targetModel;
		if (oldEditModel != null) {
			detachEditModel(oldTreeNode, oldEditModel);
		}
		
		// switch data model for components
		//_targetTreeNode = newTargetTreeNode;
		_targetModel = newEditModel;
		if (newEditModel != null) {
			// change table model
			_paneTable.setModel(newEditModel);
			//--- attach new model
			attachEditModel(newTargetTreeNode, newEditModel);
		}
		else {
			// use default
			_paneTable.setModel(getEmptyEditModel());
		}
		
		// refresh
		refreshEditingStatus();
	}
	
	/**
	 * 編集対象データモデルとこのビューとの関連付けを解除する。
	 * 主に、イベントリスナーなどのアクションを削除する。
	 * @param treeNode	関連付けを解除するツリーノード({@code null} 以外)
	 * @param editModel	関連付けを解除するデータモデル({@code null} 以外)
	 */
	protected void detachEditModel(IDtContainerContentTreeNode treeNode, AbDTCContentAlgeEditModel editModel) {
		editModel.removeTableModelListener(getTableEditHandler());
//		editModel.removeUndoableEditListener(getTableUndoHandler());
		editModel.setPreEditHandler(null);
	}
	
	/**
	 * 編集対象モデルとこのビューを関連付ける。
	 * 主に、イベントリスナーなどのアクションを設定する。
	 * @param treeNode	関連付けるツリーノード({@code null} 以外)
	 * @param editModel	関連付けるデータモデル({@code null} 以外)
	 */
	protected void attachEditModel(IDtContainerContentTreeNode treeNode, AbDTCContentAlgeEditModel editModel) {
		editModel.setPreEditHandler(_preEditHandler);
		editModel.addTableModelListener(getTableEditHandler());
//		editModel.addUndoableEditListener(getTableUndoHandler());
	}
	
	/**
	 * このビューのコンポーネントがフォーカスを保持しているかを判定する。
	 * ビューが複数のコンポーネントを持つ場合、フォーカスを保持するべきコンポーネントにフォーカスがあれば {@code true} を返す。
	 * 
	 * @return	コンポーネントがフォーカスを所持していれば {@code true}
	 */
	public boolean hasFocusInComponent() {
		return _paneTable.hasFocus();
	}
	
	/**
	 * このビューのコンポーネントにフォーカスを要求する。
	 * ビューが複数のコンポーネントを持つ場合、標準となるコンポーネントにフォーカスを設定する。
	 */
	public void requestFocusInComponent() {
		_paneTable.setFocus();
	}
	
	/**
	 * このビューに関連付けられたドキュメントの状態から、
	 * このエディタの編集状態を更新する。
	 */
	public void refreshEditingStatus() {
		endCompoundUndoableEdit();
//		getUndoManager().discardAllEdits();
		updateEditorModifiedProperty();
		//updateEditorSelectionChangedProperty();
		updateEditorMenus();
	}

	/**
	 * フレームメニューの状態を更新する。
	 */
	public void updateEditorMenus() {
		DtContainerEditorFrame frame = getFrame();
		if (frame != null) {
			fireUpdateMenusByEditState(frame);
		}
	}

	/**
	 * テーブルコンポーネントを取得する。
	 * 
	 * @return {@link DTCContentAlgeEditTablePane} インスタンス
	 */
	public DTCContentAlgeEditTablePane getTableComponent() {
		return _paneTable;
	}

	//------------------------------------------------------------
	// Implements interfaces for Editing
	//------------------------------------------------------------

	/**
	 * テーブルエディタのUndo可能編集内容の集約を開始する。
	 * Undo可能編集内容の集約は、複数の編集操作を１回のUndoで元に戻すため、
	 * 複数の編集内容をコンテナに集約するための操作となる。
	 */
	public void startCompoundUndoableEdit() {
		this._compoundEdits = true;
	}

	/**
	 * テーブルエディタのUndo可能編集内容の集約を終了する。
	 * この操作は、<code>{@link #startCompoundUndoableEdit()}</code> によって開始された編集操作の
	 * 集約を完了し、コンテナに集約された編集操作を UndoManger へ登録する。
	 * <p>
	 * <b>(注)</b>
	 * <blockquote>
	 * このメソッドは、Swingアイテムを操作するメソッドを内部で呼び出すため、
	 * EventQueue が実行されているスレッドから、呼び出すことが必須となる。
	 * </blockquote>
	 */
	public void endCompoundUndoableEdit() {
		if (this._compoundEdits) {
			if (this._packEdits != null) {
				this._packEdits.end();
//				getUndoManager().addEdit(this._packEdits);
				this._packEdits = null;
				DtContainerEditorFrame frame = getFrame();
				if (frame != null) {
					frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_UNDO);
					frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_REDO);
				}
			}
			this._compoundEdits = false;
		}
	}

	/**
	 * コンポーネントで選択されているセルが
	 * 存在するかを検証する。
	 * 
	 * @return 選択セルが存在していれば true
	 */
	public boolean hasSelectedCells() {
		return (_paneTable.getSelectedRowCount()>0 && _paneTable.getSelectedColumnCount()>0);
	}
	
	/**
	 * このビューオブジェクトに関連付けられている全てのリソースを開放する。
	 */
	public void destroy() {
		// detach document
		AbDTCContentAlgeEditModel curEditModel = getEditModel();
		if (curEditModel != null) {
			detachEditModel(getTargetTreeNode(), curEditModel);
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
	@Override
	public boolean canEdit() {
		return (_paneTable.isEditable() && _paneTable.isEnabled());
	}

	/**
	 * コンポーネントが Undo 可能かを検証する。
	 * 
	 * @return Undo 可能なら true
	 */
	@Override
	public boolean canUndo() {
		return false;
		// TODO: Undo 機能の実装
		/*
		if (canEdit()) {
			return getUndoManager().canUndo();
		} else {
			return false;
		}
		*/
	}

	/**
	 * コンポーネントが Redo 可能かを検証する。
	 * 
	 * @return Redo 可能なら true
	 */
	@Override
	public boolean canRedo() {
		return false;
		// TODO: Undo 機能の実装
		/*
		if (canEdit()) {
			return getUndoManager().canRedo();
		} else {
			return false;
		}
		*/
	}

	/**
	 * コンポーネントが Cut 可能かを検証する。
	 * @return	Cut 可能なら true
	 */
	@Override
	public boolean canCut() {
		if (canEdit()) {
			return hasSelectedCells();
		} else {
			return false;
		}
	}

	/**
	 * コンポーネントが Copy 可能かを検証する。
	 * @return Copy 可能なら true
	 */
	@Override
	public boolean canCopy() {
		if (hasSelectedCells()) {
			// 選択されているセルがあれば、コピー可能
			return true;
		}
		else if (canContentPathCopy()) {
			// オブジェクトパスの一部が選択されていれば、コピー可能
			return false;
		}
		
		// 上記以外の場合は、コピー不可
		return false;
	}

	/**
	 * クリップボードにペースト可能なデータが存在し、
	 * コンポーネントにペースト可能な状態かを検証する。
	 * 
	 * @return ペースト可能なら true
	 */
	@Override
	public boolean canPaste() {
		if (canEdit()) {
			//--- クリップボードのデータの有無を判定
			final SpreadSheetTable table = getTableComponent();
			Clipboard clip = table.getToolkit().getSystemClipboard();
			Transferable trans = clip.getContents(table);
			if (trans != null && trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * 削除操作が可能かどうかを判定する。
	 * @return	操作が可能なら {@code true}
	 */
	@Override
	public boolean canDelete() {
		return canCut();
	}
	
	/**
	 * アンドゥ操作を実行する。
	 */
	@Override
	public void undo() {
		// place holder
	}
	
	/**
	 * リドゥ操作を実行する。
	 */
	@Override
	public void redo() {
		// place holder
	}
	
	/**
	 * 切り取り操作を実行する。
	 */
	@Override
	public void cut() {
		final SpreadSheetTable table = getTableComponent();
		table.requestFocusInWindow();
		table.cut();
	}
	
	/**
	 * コピー操作を実行する。
	 */
	@Override
	public void copy() {
		if (hasSelectedCells()) {
			// セルが選択されていれば、その内容をコピー
			final SpreadSheetTable table = getTableComponent();
			table.requestFocusInWindow();
			table.copy();
		}
		else if (canContentPathCopy()) {
			// オブジェクトパスが選択されていれば、その内容をコピー
			doContentPathCopy();
		}
	}
	
	/**
	 * 貼り付け操作を実行する。
	 */
	@Override
	public void paste() {
		final SpreadSheetTable table = getTableComponent();
		table.requestFocusInWindow();
		table.paste();
	}
	
	/**
	 * 削除操作を実行する。
	 */
	@Override
	public void delete() {
		final SpreadSheetTable table = getTableComponent();
		table.requestFocusInWindow();
		table.delete();
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
			//onMenuSelectedEditUndo();
			//return true;
			// TODO: undo
			return false;
		}
		else if (DtContainerEditorMenuResources.ID_EDIT_REDO.equals(command)) {
			//onMenuSelectedEditRedo();
			//return true;
			// TODO: Redo
			return false;
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
		else if (DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_ABOVE.equals(command)) {
			onMenuSelectedTableInsertRowsAbove(action);
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_BELOW.equals(command)) {
			onMenuSelectedTableInsertRowsBelow(action);
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_COPIED.equals(command)) {
			onMenuSelectedTableRowInsertCopiedCells(action);
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_ROW_CUT.equals(command)) {
			onMenuSelectedTableCutRows(action);
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_ROW_DELETE.equals(command)) {
			onMenuSelectedTableDeleteRows(action);
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_ROW_SELECT.equals(command)) {
			onMenuSelectedTableSelectRows(action);
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
		if (Strings.isNullOrEmpty(command) || action == null)
			return false;	// undefined command
		
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
		else if (DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_ABOVE.equals(command)) {
			action.setEnabled(_numSelectedCells > 0 && _isSelectedAllColumns);
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_BELOW.equals(command)) {
			action.setEnabled(_numSelectedCells > 0 && _isSelectedAllColumns);
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_COPIED.equals(command)) {
			action.setEnabled(_numSelectedCells > 0 && _isSelectedAllColumns && canPaste());
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_ROW_CUT.equals(command)) {
			action.setEnabled(_numSelectedCells > 0 && _isSelectedAllColumns);
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_ROW_DELETE.equals(command)) {
			action.setEnabled(_numSelectedCells > 0 && _isSelectedAllColumns);
			return true;
		}
		else if (DtContainerEditorMenuResources.ID_TABLE_ROW_SELECT.equals(command)) {
			action.setEnabled(_numSelectedCells > 0);
			return true;
		}
		
		// その他は、処理しない
		return false;
	}

//	/**
//	 * コンポーネントに対して Undo する。
//	 */
//	public void undo() {
//		try {
//			getUndoManager().undo();
//		} catch (CannotUndoException ex) {
//			AppLogger.debug("Unable to undo.", ex);
//		}
//		updateEditorModifiedProperty();
//		DtContainerEditorFrame frame = getFrame();
//		if (frame != null) {
//			frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_UNDO);
//			frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_REDO);
//		}
//	}

//	/**
//	 * コンポーネントに対して Redo する。
//	 */
//	public void redo() {
//		try {
//			getUndoManager().redo();
//		} catch (CannotRedoException ex) {
//			AppLogger.debug("Unable to redo.", ex);
//		}
//		DtContainerEditorFrame frame = getFrame();
//		if (frame != null) {
//			frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_UNDO);
//			frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_REDO);
//		}
//	}

//	// Menu : [Edit]-[Undo]
//	protected void onMenuSelectedEditUndo() {
//		AppLogger.debug("catch [Edit]-[Undo] menu selection.");
//		undo();
//		requestFocusInComponent();
//	}
	
//	// Menu : [Edit]-[Redo]
//	protected void onMenuSelectedEditRedo() {
//		AppLogger.debug("catch [Edit]-[Redo] menu selection.");
//		redo();
//		requestFocusInComponent();
//	}
	
	// Menu : [Edit]-[Cut]
	@Override
	protected void onMenuSelectedEditCut() {
		AppLogger.debug("catch [Edit/Table]-[Cut] menu selection.");
		cut();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Copy]
	@Override
	protected void onMenuSelectedEditCopy() {
		AppLogger.debug("catch [Edit/Table]-[Copy] menu selection.");
		copy();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Paste]
	@Override
	protected void onMenuSelectedEditPaste() {
		AppLogger.debug("catch [Edit/Table]-[Paste] menu selection.");
		paste();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Delete]
	@Override
	protected void onMenuSelectedEditDelete() {
		AppLogger.debug("catch [Edit/Table]-[Delete] menu selection.");
		delete();
		requestFocusInComponent();
	}
	
	// Menu : [Table]-[Row insert above]
	protected void onMenuSelectedTableInsertRowsAbove(Action action) {
		AppLogger.debug("catch [Table]-[Insert rows above] menu selection.");
		
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int [] selRows = table.getSelectedRows();
		int numSelCols = table.getSelectedColumnCount();
		if (selRows.length > 0 && numSelCols==table.getColumnCount()) {
			Arrays.sort(selRows);
			//--- 行数を調整(全体行数が Integer.MAX_VALUE を超えないように)
			int numNewRows = Math.min(selRows.length, (Integer.MAX_VALUE - table.getRowCount()));
			if (numNewRows > 0) {
				//--- 選択行数分の空行を挿入
				int startRowIndex = selRows[0];
				int endRowIndex = startRowIndex;
				//getDocument().startCompoundUndoableEdit();
				for (int i = 0; i < numNewRows; i++) {
					endRowIndex = startRowIndex + i;
					getEditModel().insertRow(endRowIndex, null);
				}
				//getDocument().endCompoundUndoableEdit();
				
				// 挿入した行のみに選択を設定
				table.changeRowHeaderSelection(endRowIndex, false, false);
				table.changeRowHeaderSelection(startRowIndex, false, true);
			}
			else {
				//--- 最大行数をオーバー
				//Application.showErrorMessage(this, MacroMessages.getInstance().msgCannotInsertFurtherRows);
			}
		}
		requestFocusInComponent();
	}
	
	// Menu : [Table]-[Row insert below]
	protected void onMenuSelectedTableInsertRowsBelow(Action action) {
		AppLogger.debug("catch [Table]-[Insert rows below] menu selection.");
		
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int [] selRows = table.getSelectedRows();
		int numSelCols = table.getSelectedColumnCount();
		if (selRows.length > 0 && numSelCols==table.getColumnCount()) {
			Arrays.sort(selRows);
			//--- 行数を調整(全体行数が Integer.MAX_VALUE を超えないように)
			int numNewRows = Math.min(selRows.length, (Integer.MAX_VALUE - table.getRowCount()));
			if (numNewRows > 0) {
				int startRowIndex = selRows[selRows.length-1] + 1;
				int endRowIndex = startRowIndex;
				if (startRowIndex < table.getRowCount()) {
					// 最終選択行の次の行に挿入
					//getDocument().startCompoundUndoableEdit();
					for (int i = 0; i < numNewRows; i++) {
						endRowIndex = startRowIndex + i;
						getEditModel().insertRow(endRowIndex, null);
					}
					//getDocument().endCompoundUndoableEdit();
				}
				else {
					// 行終端に追加
					startRowIndex = table.getRowCount();
					//getDocument().startCompoundUndoableEdit();
					for (int i = 0; i < numNewRows; i++) {
						getEditModel().insertRow(getEditModel().getActualRowCount(), null);
					}
					//getDocument().endCompoundUndoableEdit();
					endRowIndex = table.getRowCount()-1;
				}
				
				// 挿入した行のみに選択を設定
				table.changeRowHeaderSelection(endRowIndex, false, false);
				table.changeRowHeaderSelection(startRowIndex, false, true);
			}
			else {
				//--- 最大行数をオーバー
				//Application.showErrorMessage(this, MacroMessages.getInstance().msgCannotInsertFurtherRows);
			}
		}
		requestFocusInComponent();
	}
	
	// Menu : [Table]-[Row insert copied]
	protected void onMenuSelectedTableRowInsertCopiedCells(Action action) {
		AppLogger.debug("catch [Table]-[Insert copied cells] menu selection.");
		
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int [] selRows = table.getSelectedRows();
		int numSelCols = table.getSelectedColumnCount();
		if (selRows.length > 0 && numSelCols==table.getColumnCount()) {
			Arrays.sort(selRows);
			//--- 選択領域の先頭行へ挿入
			pasteNewRowFromClipboard(selRows[0]);
		}
		requestFocusInComponent();
	}
	
	protected void pasteNewRowFromClipboard(int rowIndex) {
		// 転送可能なオブジェクトを取得
		Clipboard clip = getToolkit().getSystemClipboard();
		Transferable trans = clip.getContents(this);
		//--- 文字列以外は貼り付け禁止
		if (trans == null || !trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return;
		}
		
		// 転送データを取得
		final SpreadSheetTable table = getTableComponent();
		ArrayList<Object[]> copiedRows = new ArrayList<Object[]>();
		try {
			String data = (String)trans.getTransferData(DataFlavor.stringFlavor);
			if (!Strings.isNullOrEmpty(data)) {
				//--- 許容行数を取得
				int remainRowCount = Integer.MAX_VALUE - table.getRowCount();
				if (remainRowCount > 0) {
					//--- クリップボードの内容を取得
					CsvReader reader = new CsvReader(new BufferedReader(new StringReader(data)));
					try {
						reader.setDelimiterChar('\t');
						CsvReader.CsvRecord record;
						while ((record = reader.readRecord()) != null) {
							Object[] rowModel = null;
							int numFields = record.getNumFields();
							if (numFields > 0) {
								int limit = Math.min(numFields, table.getColumnCount());
								String[] newRow = new String[limit];
								for (int i = 0; i < limit; i++) {
									CsvReader.CsvField field = record.getField(i);
									if (field != null) {
										newRow[i] = field.getValue();
									}
								}
								//--- 行モデル生成
								rowModel = getEditModel().createRowDataByStrings(newRow);
							}
							//--- 行を追加
							copiedRows.add(rowModel);
							if (copiedRows.size() >= remainRowCount) {
								//--- 許容行数到達
								break;
							}
						}
					}
					finally {
						reader.close();
					}
				}
				else {
					//--- 最大行数をオーバー
					//Application.showErrorMessage(this, MacroMessages.getInstance().msgCannotInsertFurtherRows);
				}
			}
		}
		catch (IOException ex) {
			AppLogger.error("Cannot to get Transferable data from System clipboard.", ex);
		}
		catch (UnsupportedFlavorException ex) {
			AppLogger.error("Unsupported String flavor in System clipboard data.", ex);
		}
		//--- 転送データが存在しなければ、処理しない
		if (copiedRows.isEmpty()) {
			return;
		}
		
		// 転送データを新しい行として挿入
		//getDocument().startCompoundUndoableEdit();
		for (int i = 0; i < copiedRows.size(); i++) {
			getEditModel().insertRow((rowIndex + i), copiedRows.get(i));
		}
		//getDocument().endCompoundUndoableEdit();
		
		// 挿入した行のみに選択を設定
		table.changeRowHeaderSelection((rowIndex + copiedRows.size() - 1), false, false);
		table.changeRowHeaderSelection(rowIndex, false, true);
	}
	
	// Menu : [Table]-[Row cut]
	protected void onMenuSelectedTableCutRows(Action action) {
		AppLogger.debug("catch [Table]-[Cut rows] menu selection.");
		
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int [] selRows = table.getSelectedRows();
		int numSelCols = table.getSelectedColumnCount();
		if (selRows.length > 0 && numSelCols==table.getColumnCount()) {
			Arrays.sort(selRows);
			//--- 選択セルのコピー
			table.copy();
			//--- 編集操作の集約開始
			//getDocument().startCompoundUndoableEdit();
			//--- 選択行を削除(終端に近い行から削除)
			for (int i = selRows.length-1; i >= 0; i--) {
				getEditModel().removeRow(selRows[i]);
			}
			//--- 最小行数を下回る場合は、行数を調整
			getEditModel().ensureMinimumRowCount();
			//--- 編集操作の集約完了
			//getDocument().endCompoundUndoableEdit();
			//--- 選択解除
			table.clearSelection();
		}
		requestFocusInComponent();
	}
	
	// Menu : [Table]-[Row delete]
	protected void onMenuSelectedTableDeleteRows(Action action) {
		AppLogger.debug("catch [Table]-[Delete rows] menu selection.");
		
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int [] selRows = table.getSelectedRows();
		int numSelCols = table.getSelectedColumnCount();
		if (selRows.length > 0 && numSelCols==table.getColumnCount()) {
			Arrays.sort(selRows);
			//--- 選択行を削除(終端に近い行から削除)
			// getDocument().startCompountUndobaleEdit();
			for (int i = selRows.length-1; i >= 0; i--) {
				getEditModel().removeRow(selRows[i]);
			}
			//--- 最小行数を下回る場合は、行数を調整
			getEditModel().ensureMinimumRowCount();
			//getDocument().endCompoundUndoableEdit();
			//--- 選択解除
			table.clearSelection();
		}
		requestFocusInComponent();
	}
	
	// Menu : [Table]-[Row select]
	protected void onMenuSelectedTableSelectRows(Action action) {
		AppLogger.debug("catch [Table]-[Select rows] menu selection.");
		
		final SpreadSheetTable table = getTableComponent();
		if (!table.isEditable()) {
			requestFocusInComponent();
			return;
		}
		if (table.isEditing()) {
			table.removeEditor();
		}
		int numSelRows = table.getSelectedRowCount();
		int numSelCols = table.getSelectedColumnCount();
		if (numSelRows > 0 && numSelCols > 0 && numSelCols!=table.getColumnCount()) {
			table.selectAllColumns();
		}
		requestFocusInComponent();
	}
	
	protected void updateEditorModifiedProperty() {
		getParentEditorView().updateEditorModifiedProperty();
	}
	
	protected void setEditorModifiedProperty(boolean modified) {
		if (_targetModel != null) {
			_targetModel.setModifiedFlag(modified);

			if (!getParentEditorView().isModified() && modified) {
				// ドキュメントが変更されておらず、このビューのデータモデルの更新をマークする場合のみ、ドキュメントの更新フラグを変更する
				getParentEditorView().getDocument().setModifiedFlag(modified);
				getParentEditorView().updateEditorModifiedProperty();
			}
			
//			if (!_parentView.isModified() && modified) {
//				// ドキュメントが変更されておらず、このビューのデータモデルの更新をマークする場合のみ、ドキュメントの更新フラグを変更する
//				_parentView.getDocument().setModifiedFlag(modified);
//				_parentView.updateEditorModifiedProperty();
//			}
		}
	}
	
//	protected void updateEditorSelectionChangedProperty() {
//		setEditorSelectionChangedProperty(hasSelectedCells());
//	}
//	
//	protected void setEditorSelectionChangedProperty(boolean selected) {
//		Boolean oldValue = (Boolean)getClientProperty(ITextComponent.PROP_SELECTED);
//		boolean oldSelected = (oldValue != null ? oldValue.booleanValue() : false);
//		if (selected != oldSelected) {
//			putClientProperty(ITextComponent.PROP_SELECTED, selected);
//		}
//	}

	/**
	 * 編集状態の更新によって関連するメニュー更新が必要な場合に呼び出されるメソッド。
	 * 
	 * @param frame	親フレームのインスタンス(<tt>null</tt> 以外)
	 */
	protected void fireUpdateMenusByEditState(DtContainerEditorFrame frame) {
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_FILE_SAVE);
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_UNDO);
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_REDO);
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_CUT);
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_COPY);
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_PASTE);
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_DELETE);
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_TABLE_CUT);
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_TABLE_COPY);
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_TABLE_PASTE);
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_TABLE_DELETE);
	}

	/**
	 * カット／コピーの実行によって関連するメニュー更新が必要な場合に呼び出されるメソッド。
	 * 
	 * @param frame	親フレームのインスタンス(<tt>null</tt> 以外)
	 */
	protected void fireUpdateMenusByCutCopy(DtContainerEditorFrame frame) {
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_PASTE);
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_TABLE_PASTE);
		frame.updateMenuItem(DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_COPIED);
	}

	//------------------------------------------------------------
	// Event handlers
	//------------------------------------------------------------
	
	protected IDTCContentAlgePreEditHandler	_preEditHandler = new IDTCContentAlgePreEditHandler() {
		@Override
		public void preModifyingCellValue(Object curValue, Object newValue, int rowIndex, int columnIndex) {
			preModifyingTableCellValue(curValue, newValue, rowIndex, columnIndex);
		}
		
		@Override
		public void preInsertingRows(int firstRowIndex, int lastRowIndex) {
			preInsertingTableRows(firstRowIndex, lastRowIndex);
		}
		
		@Override
		public void preDeletingRows(int firstRowIndex, int lastRowIndex) {
			preDeletingTableRows(firstRowIndex, lastRowIndex);
		}
	};
	
	protected void preModifyingTableCellValue(Object curValue, Object newValue, int modelRowIndex, int modelColumnIndex) {
		if (AppLogger.isTraceEnabled()) {
			String msg = String.format("called [%s#preModifyingTableCellValue] : curValue=%s, newValue=%s, modelRowIndex=%d, modelColumnIndex=%s",
					getClass().getName(), String.valueOf(curValue), String.valueOf(newValue), modelRowIndex, modelColumnIndex);
			AppLogger.trace(msg);;
		}
		if (getTargetTreeNode() != null) {
			getTargetTreeNode().keepTransferableDataBeforeEditing();
		}
	}
	
	protected void preInsertingTableRows(int firstModelRowIndex, int lastModelRowIndex) {
		if (AppLogger.isTraceEnabled()) {
			String msg = String.format("called [%s#preInsertingTableRows] : firstModelRowIndex=%d, lastModelRowIndex=%s",
					getClass().getName(), firstModelRowIndex, lastModelRowIndex);
			AppLogger.trace(msg);;
		}
		if (getTargetTreeNode() != null) {
			getTargetTreeNode().keepTransferableDataBeforeEditing();
		}
	}
	
	protected void preDeletingTableRows(int firstModelRowIndex, int lastModelRowIndex) {
		if (AppLogger.isTraceEnabled()) {
			String msg = String.format("called [%s#preDeletingTableRows] : firstModelRowIndex=%d, lastModelRowIndex=%s",
					getClass().getName(), firstModelRowIndex, lastModelRowIndex);
			AppLogger.trace(msg);;
		}
		if (getTargetTreeNode() != null) {
			getTargetTreeNode().keepTransferableDataBeforeEditing();
		}
	}
	
	protected void onTableCellInfoEditCommitted() {
		// TODO:
	}
	
	protected void onTableCellInfoEditCanceled() {
		// TODO:
	}
	
	/**
	 * テーブルの値が変更されたときに呼び出されるイベントハンドラ
	 * @param tme	イベントオブジェクト
	 */
	protected void onTableModelChanged(TableModelEvent tme) {
		setEditorModifiedProperty(true);
	}
	
	protected void onTableSelectionChanged(ListSelectionEvent lse) {
		updateSelectionStatus(lse.getValueIsAdjusting());
		boolean selected = (_numSelectedCells > 0);
		
		DtContainerEditorFrame frame = getFrame();
		if (frame != null) {
			//--- [Table] menu
			frame.getMenuAction(DtContainerEditorMenuResources.ID_EDIT_CUT).setEnabled(selected);
			frame.getMenuAction(DtContainerEditorMenuResources.ID_EDIT_COPY).setEnabled(selected);
			frame.getMenuAction(DtContainerEditorMenuResources.ID_EDIT_DELETE).setEnabled(selected);
			frame.getMenuAction(DtContainerEditorMenuResources.ID_TABLE_CUT).setEnabled(selected);
			frame.getMenuAction(DtContainerEditorMenuResources.ID_TABLE_COPY).setEnabled(selected);
			frame.getMenuAction(DtContainerEditorMenuResources.ID_TABLE_DELETE).setEnabled(selected);
			
			frame.getMenuAction(DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_ABOVE).setEnabled(selected && _isSelectedAllColumns);
			frame.getMenuAction(DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_BELOW).setEnabled(selected && _isSelectedAllColumns);
			frame.getMenuAction(DtContainerEditorMenuResources.ID_TABLE_ROW_INSERT_COPIED).setEnabled(selected && _isSelectedAllColumns && canPaste());
			frame.getMenuAction(DtContainerEditorMenuResources.ID_TABLE_ROW_CUT).setEnabled(selected && _isSelectedAllColumns);
			frame.getMenuAction(DtContainerEditorMenuResources.ID_TABLE_ROW_DELETE).setEnabled(selected && _isSelectedAllColumns);
			frame.getMenuAction(DtContainerEditorMenuResources.ID_TABLE_ROW_SELECT).setEnabled(selected);
			
			//--- update location indicator in status bar
			if (!lse.getValueIsAdjusting()) {
				//getTableComponent().updateLocationForStatusBar(frame.getStatusBar());
			}
		}
		
		//setEditorSElectionChangedProperty(selected);
		
		// セル情報パネルの内容を更新する
		//updateCellInfoPane();
	}
	
	/**
	 * テーブル列ヘッダーのコンテキストメニューを表示する。
	 * このメソッドは、ポップアップメニュー表示のマウスイベントから呼び出される。
	 * このメソッド内では、マウスイベントがコンテキストメニュー表示のトリガであるかを
	 * <code>{@link java.awt.event.MouseEvent#isPopupTrigger()}</code> により検証すること。
	 * 
	 * @param me	マウスイベント
	 */
	protected void evaluateColumnHeaderPopupMenu(MouseEvent me) {
		// コンテキストメニュー表示のトリガ検証
		if (!me.isPopupTrigger())
			return;
		
		// ポップアップメニューが存在しない場合は、何もしない
		JPopupMenu pmenu = getTableHeaderPopupMenu();
		if (pmenu == null) {
			return;
		}

		// 編集状態の解除
		final SpreadSheetTable table = getTableComponent();
		if (table.isEditing()) {
			table.removeEditor();
		}
		
		// 選択状態の検証
		//--- [Shift]キーが押されている状態なら、選択状態は変更しない
		if ((me.getModifiers() & MouseEvent.SHIFT_DOWN_MASK)==0) {
			int col = table.columnAtPoint(me.getPoint());
			if (!table.isColumnSelectedAllRows(col)) {
				//--- コンテキストメニュー表示トリガとなるマウスイベント発生位置が
				//--- 選択済み列ではない場合、すべての選択を解除してイベント発生
				//--- 位置の列を選択する。
				if (col >= 0) {
					table.changeColumnHeaderSelection(col, false, false);
				} else {
					table.clearSelection();
				}
			}
		}

		// コンテキストメニューの表示
		pmenu.show(me.getComponent(), me.getX(), me.getY());
		me.getComponent().requestFocusInWindow();
	}
	
	protected void onTableHeaderPopupMenuSelected_AdjustColumnWidth(ActionEvent ae) {
		int targetColumnIndex = getTableComponent().getSelectedColumn();
		if (targetColumnIndex >= 0) {
			JTableHeader header = getTableComponent().getTableHeader();
			if (header instanceof SpreadSheetColumnHeader) {
				((SpreadSheetColumnHeader)header).adjustSelectedColumnWidth(targetColumnIndex);
			}
		}
	}
	
	protected void onTableHeaderPopupMenuSelected_SetColumnWidthToDefault(ActionEvent ae) {
		int targetColumnIndex = getTableComponent().getSelectedColumn();
		if (targetColumnIndex >= 0) {
			JTableHeader header = getTableComponent().getTableHeader();
			if (header instanceof SpreadSheetColumnHeader) {
				((SpreadSheetColumnHeader)header).setSelectedColumnWidthToDefault(targetColumnIndex);
			}
		}
	}
	
	/**
	 * エディタテーブルのセルのコンテキストメニューを表示する。
	 * このメソッドは、ポップアップメニュー表示のマウスイベントから呼び出される。
	 * このメソッド内では、マウスイベントがコンテキストメニュー表示のトリガであるかを
	 * <code>{@link java.awt.event.MouseEvent#isPopupTrigger()}</code> により検証すること。
	 * 
	 * @param me	マウスイベント
	 */
	protected void evaluateTableCellPopupMenu(MouseEvent me) {
		// コンテキストメニュー表示のトリガ検証
		if (!me.isPopupTrigger())
			return;

		final SpreadSheetTable table = getTableComponent();
		if (table.isEditing()) {
			table.removeEditor();
		}
		
		// 選択状態の検証
		int col = table.columnAtPoint(me.getPoint());
		int row = table.rowAtPoint(me.getPoint());
		if (!table.isCellSelected(row, col)) {
			//--- コンテキストメニュー表示トリガとなるマウスイベント発生位置が
			//--- 選択済みセルではない場合、すべての選択を解除してイベント発生
			//--- 位置のセルを選択する。
			if (col >= 0 && row >= 0) {
				table.changeSelection(row, col, false, false);
			} else {
				table.clearSelection();
			}
		}

		// コンテキストメニューの表示
		DtContainerEditorMenuBar menubar = getFrame().getActiveEditorMenuBar();
		JPopupMenu pmenu = (menubar==null ? null : menubar.getContentEditorTableCellContextMenu());
		if (pmenu != null) {
			pmenu.show(me.getComponent(), me.getX(), me.getY());
			me.getComponent().requestFocusInWindow();
		}
	}
	
	/**
	 * エディタテーブル行ヘッダーのコンテキストメニューを表示する。
	 * このメソッドは、ポップアップメニュー表示のマウスイベントから呼び出される。
	 * このメソッド内では、マウスイベントがコンテキストメニュー表示のトリガであるかを
	 * <code>{@link java.awt.event.MouseEvent#isPopupTrigger()}</code> により検証すること。
	 * 
	 * @param me	マウスイベント
	 */
	protected void evaluateTableRowHeaderPopupMenu(MouseEvent me) {
		// コンテキストメニュー表示のトリガ検証
		if (!me.isPopupTrigger())
			return;

		final SpreadSheetTable table = getTableComponent();
		if (table.isEditing()) {
			table.removeEditor();
		}
		
		// 選択状態の検証
		int row = table.rowAtPoint(me.getPoint());
		if (!table.isRowSelectedAllColumns(row)) {
			//--- コンテキストメニュー表示トリガとなるマウスイベント発生位置が
			//--- 選択済み行ではない場合、すべての選択を解除してイベント発生
			//--- 位置の行を選択する。
			if (row >= 0) {
				table.changeRowHeaderSelection(row, false, false);
			} else {
				table.clearSelection();
			}
		}

		// コンテキストメニューの表示
		// TODO: 行ヘッダーのポップアップメニュー表示
		DtContainerEditorMenuBar menubar = getFrame().getActiveEditorMenuBar();
		JPopupMenu pmenu = (menubar==null ? null : menubar.getContentEditorTableRowHeaderContextMenu());
		if (pmenu != null) {
			pmenu.show(me.getComponent(), me.getX(), me.getY());
			me.getComponent().requestFocusInWindow();
		}
	}

	//------------------------------------------------------------
	// Field accessor
	//------------------------------------------------------------

//	/**
//	 * このビューのアンドゥマネージャを取得する。
//	 * @return	<code>UndoManager</code> のインスタンス
//	 */
//	protected UndoManager getUndoManager() {
//		return _undoMan;
//	}

	/**
	 * このビューのドキュメントハンドラを取得する。
	 * @return	<code>DTCTableModelHandler</code> のインスタンス
	 */
	protected DTCTableModelHandler getTableEditHandler() {
		return _hTableModel;
	}

//	/**
//	 * このビューのアンドゥハンドラを取得する。
//	 * @return	<code>DTCTableUndoHandler</code> のインスタンス
//	 */
//	protected DTCTableUndoHandler getTableUndoHandler() {
//		return _hTableUndo;
//	}

	/**
	 * このビューのテーブルセレクションハンドラを取得する。
	 * @return	<code>DTCTableSelectionHandler</code> のインスタンス
	 */
	protected DTCTableSelectionHandler getTableSelectionHandler() {
		return _hTableSelection;
	}

	/**
	 * このビューのスクロールコンポーネントを取得する。
	 * @return	<code>JScrollPane</code> のインスタンス
	 */
	protected JScrollPane getScrollPane() {
		return _paneScroll;
	}

	/**
	 * テーブル列ヘッダのポップアップメニューを取得する。
	 * @return	<code>JPopupMenu</code> オブジェクト
	 */
	protected JPopupMenu getTableHeaderPopupMenu() {
		if (_tableHeaderPopupMenu == null) {
			_tableHeaderPopupMenu = createTableHeaderPopupMenu();
		}
		return _tableHeaderPopupMenu;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	abstract protected AbDTCContentAlgeEditModel getEmptyEditModel();
	
	/**
	 * テーブルコンポーネントを生成し、テーブルモデル設定前の初期化を行う。
	 * @return	生成されたテーブルコンポーネント
	 */
	protected DTCContentAlgeEditTablePane createEditTableComponent() {
		DTCContentAlgeEditTablePane table = new DTCContentAlgeEditTablePane() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onFinishedTableEditCutAction() {
				if (AppLogger.isTraceEnabled()) {
					AppLogger.trace("called DTCContentAlgeEditTablePane#onFinishedTableEditCutAction() in " + AbDTCContentAlgeEditView.this.getClass().getName());
				}
				fireUpdateMenusByCutCopy(getFrame());
			}
			
			@Override
			protected void onFinishedTableEditCopyAction() {
				if (AppLogger.isTraceEnabled()) {
					AppLogger.trace("called DTCContentAlgeEditTablePane#onFinishedTableEditCopyAction() in " + AbDTCContentAlgeEditView.this.getClass().getName());
				}
				fireUpdateMenusByCutCopy(getFrame());
			}
			
			@Override
			protected void onFinishedTableEditPasteAction() {
				if (AppLogger.isTraceEnabled()) {
					AppLogger.trace("called DTCContentAlgeEditTablePane#onFinishedTableEditPasteAction() in " + AbDTCContentAlgeEditView.this.getClass().getName());
				}
			}
			
			@Override
			protected void onFinishedTableEditDeleteAction() {
				if (AppLogger.isTraceEnabled()) {
					AppLogger.trace("called DTCContentAlgeEditTablePane#onFinishedTableEditDeleteAction() in " + AbDTCContentAlgeEditView.this.getClass().getName());
				}
			}
		};
		//table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//table.setColumnSelectionAllowed(true);
		//table.setRowSelectionAllowed(true);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//table.getTableHeader().setReorderingAllowed(false);
		//table.getTableHeader().setResizingAllowed(true);
		//table.setShowGrid(true);
		//table.setGridColor(new Color(128,128,128));
		//table.getTableRowHeader().setFixedCellWidth(50);
		
		return table;
	}
	
	/**
	 * このビューのコンポーネントを生成する。
	 */
	@Override
	protected void createComponents() {
		super.createComponents();
		
		// table
		_paneTable = createEditTableComponent();
		// ダミーモデルを設定
		_paneTable.setModel(getEmptyEditModel());
		
		// スクロールビュー
		_paneScroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		_paneScroll.setViewportView(_paneTable);
		_paneScroll.setRowHeaderView(_paneTable.getTableRowHeader());
		_paneScroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, SpreadSheetTable.createUpperLeftCornerComponent());
		
		// Event listeners
		_hTableModel = createTableModelHandler();
		//_hTableUndo = createTableUndoHandler();
		_hTableSelection = createTableSelectionHandler();
	}

	/**
	 * このビューのアンドゥマネージャのインスタンスを生成する。
	 * @return	<code>UndoManager</code> のインスタンス
	 */
	protected UndoManager createUndoManager() {
		return new UndoManager();
	}

	/**
	 * このビューのテーブルモデル変更イベントハンドラのインスタンスを生成する。
	 * @return	<code>DTCTableModelHandler</code> のインスタンス
	 */
	protected DTCTableModelHandler createTableModelHandler() {
		return new DTCTableModelHandler();
	}

//	/**
//	 * このビューのアンドゥハンドラのインスタンスを生成する。
//	 * @return	<code>DTCTableUndoHandler</code> のインスタンス
//	 */
//	protected DTCTableUndoHandler createTableUndoHandler() {
//		return new DTCTableUndoHandler();
//	}

	/**
	 * このビューのテーブルセレクションハンドラのインスタンスを生成する。
	 * @return	<code>DTCTableSelectionHandler</code> のインスタンス
	 */
	protected DTCTableSelectionHandler createTableSelectionHandler() {
		return new DTCTableSelectionHandler();
	}
	
	/**
	 * このビューのコンポーネントを配置する。
	 */
	@Override
	protected void setupLayout() {
		super.setupLayout();
		
		// layout
		this.add(_paneScroll, BorderLayout.CENTER);
	}
	
	/**
	 * このビュー内のアクションを初期化する。
	 */
	@Override
	protected void setupActions() {
		super.setupActions();
		
		// テーブル選択変更イベント
		final DTCTableSelectionHandler selectionHandler = getTableSelectionHandler();
		getTableComponent().getSelectionModel().addListSelectionListener(selectionHandler);
		getTableComponent().getColumnModel().getSelectionModel().addListSelectionListener(selectionHandler);
		
		// Mouse action for Table header
		getTableComponent().getTableHeader().addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				evaluateColumnHeaderPopupMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				evaluateColumnHeaderPopupMenu(e);
			}
		});
		
		// カラム幅の自動調節は行わない
		//_paneTable.adjustAllColumnsPreferredWidth();
		
		// Mouse action for table
		getTableComponent().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				evaluateTableCellPopupMenu(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				evaluateTableCellPopupMenu(e);
			}
		});
		
		// Mouse action for table row header
		getTableComponent().getTableRowHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				evaluateTableRowHeaderPopupMenu(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				evaluateTableRowHeaderPopupMenu(e);
			}
		});
		
		// Drop target
		//--- テーブルの列・行ヘッダやスクロールバーでは、ドロップを受け付けないようにするための処置
		new DropTarget(this, DnDConstants.ACTION_COPY, new EmptyDropTargetListener(), true);
		//--- テーブル標準のトランスファーを無効にする(こうしないと、デフォルトで何かを受け付けてしまう)
		getTableComponent().setTransferHandler(null);
//		//--- テーブルに新たに設定するドロップターゲット
//		new DropTarget(table, DnDConstants.ACTION_COPY, hFileDropped, true);
		
		// メニューアクセラレータキーのテーブルへの登録
		KeyStroke[] strokes = getMenuAccelerators();
		getTableComponent().registMenuAcceleratorKeyStroke(strokes);
		
		// ステータス更新
		updateSelectionStatus(false);
	}
	
	/**
	 * 現在のメニューのアクセラレーターキーの情報を、すべて取得する。
	 * @return	アクセラレーターキーのストロークの配列
	 */
	private KeyStroke[] getMenuAccelerators() {
		if (_menuAccelerators == null) {
			DtContainerEditorMenuBar menubar = getFrame().getActiveEditorMenuBar();
			if (menubar != null) {
				//--- カット、コピー、ペースト、削除、全て選択のショートカットキーは、
				//--- SpreadSheetTable クラスで登録されているため、除外
				_menuAccelerators = SwingTools.getMenuAccelerators(menubar,
						MenuItemResource.getEditPasteShortcutKeyStroke(),
						MenuItemResource.getEditCopyShortcutKeyStroke(),
						MenuItemResource.getEditCutShortcutKeyStroke(),
						MenuItemResource.getEditDeleteShortcutKeyStroke(),
						MenuItemResource.getEditSelectAllShortcutKeyStroke());
			}
			else {
				_menuAccelerators = new KeyStroke[0];
			}
		}
		return _menuAccelerators;
	}
	
	/**
	 * セル選択状態に関わる内部ステータスを更新する。
	 * @param isAdjusting	サイズ調整中なら {@code true}
	 */
	protected void updateSelectionStatus(boolean isAdjusting) {
		final SpreadSheetTable table = getTableComponent();
		int[] selRows = table.getSelectedRows();
		int[] selCols = table.getSelectedColumns();
		
		_numSelectedCells = selRows.length * selCols.length;
		if (_numSelectedCells > 0) {
			//_isSelectedAllRows = (selRows.length == paneTable.getRowCount());
			_isSelectedAllColumns = (selCols.length == table.getColumnCount());
			if (!isAdjusting) {
				_isSelectedEditableOneCell = false;
				if (table.isEditable()) {
					//--- Selected one cell is editable
					if (_numSelectedCells == 1 && table.isCellEditable(selRows[0], selCols[0])) {
						_isSelectedEditableOneCell = true;
					}
				}
			}
		} else {
			//_isSelectedAllRows = false;
			_isSelectedAllColumns = false;
			_isSelectedEditableOneCell = false;
		}
	}

	/**
	 * テーブル列ヘッダのポップアップメニューを生成する。
	 * @return	<code>JPopupMenu</code> オブジェクト
	 */
	protected JPopupMenu createTableHeaderPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		//--- 列幅の自動調整
		menu.add(new AbstractAction(CommonMessages.getInstance().TableHeaderPopupMenu_Column_AdjustColumnWidth) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onTableHeaderPopupMenuSelected_AdjustColumnWidth(e);
			}
		});
		//--- 標準の列幅に設定
		menu.add(new AbstractAction(CommonMessages.getInstance().TableHeaderPopupMenu_Column_SetWidthToDefault){
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onTableHeaderPopupMenuSelected_SetColumnWidthToDefault(e);
			}
		});

		// completed
		return menu;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * セル情報ペインのテキスト編集操作専用のアクション。
	 * このクラス・インスタンスは、セル情報ペインのキャンセルボタン、
	 * コミットボタンのアクションとして使用される。また、テキスト
	 * エディタの[Enter]キーおよび[Escape]キーのアクションとしても
	 * 利用される。
	 * @version 1.1.0
	 * @since 1.1.0
	 */
	protected class DTCTableCellInfoEditActionHandler extends AbMenuItemAction
	{
		private static final long serialVersionUID = 1L;
		/** コミット操作用コマンド名 **/
		static public final String COMMAND_COMMIT = "cellinfo.editor.commit";
		/** キャンセル操作用コマンド名 **/
		static public final String COMMAND_CANCEL = "cellinfo.editor.cancel";
		
		public DTCTableCellInfoEditActionHandler(String command, String name) {
			super(name);
			Validations.validArgument(!Strings.isNullOrEmpty(command), "'command' argument is null or empty.");
			setCommandKey(command);
		}

		public void actionPerformed(ActionEvent e) {
			String key = getCommandKey();
			
			if (COMMAND_COMMIT.equals(key)) {
				onTableCellInfoEditCommitted();
			}
			else if (COMMAND_CANCEL.equals(key)) {
				onTableCellInfoEditCanceled();
			}
		}
	}
	
	/**
	 * 詳細ビュー用のデータモデルの変更イベントハンドラ
	 * @version 1.1.0
	 * @since 1.1.0
	 */
	protected class DTCTableModelHandler implements TableModelListener
	{
		public void tableChanged(TableModelEvent e) {
			onTableModelChanged(e);
		}
	}

	/**
	 * テーブルの選択状態変更イベントハンドラ
	 * @version 1.1.0
	 * @since 1.1.0
	 */
	protected class DTCTableSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			onTableSelectionChanged(e);
		}
	};

//	/**
//	 * 詳細ビュー用のデータモデルのUndo可能編集発生時のイベントハンドラ
//	 */
//	protected class DTCTableUndoHandler implements UndoableEditListener {
//		public void undoableEditHappened(UndoableEditEvent e) {
//			if (_compoundEdits) {
//				// CompoundEdit へ登録
//				if (_packEdits == null) {
//					_packEdits = new CompoundEdit();
//				}
//				_packEdits.addEdit(e.getEdit());
//			} else {
//				// UndoManager へ登録
//				_undoMan.addEdit(e.getEdit());
//				DtContainerEditorFrame frame = getFrame();
//				if (frame != null) {
//					frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_UNDO);
//					frame.updateMenuItem(DtContainerEditorMenuResources.ID_EDIT_REDO);
//				}
//			}
//		}
//	}

	/**
	 * 何も処理を行わない、ドロップリスナー。
	 * @version 1.1.0
	 * @since 1.1.0
	 */
	class EmptyDropTargetListener extends DropTargetAdapter {
		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			//--- 全て禁止
			dtde.rejectDrag();
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			//--- 全て禁止
			dtde.rejectDrop();
		}
	}
}
