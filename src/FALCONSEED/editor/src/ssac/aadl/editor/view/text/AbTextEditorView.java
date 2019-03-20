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
 * @(#)AbTextEditorView.java	1.17	2011/02/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbTextEditorView.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbTextEditorView.java	1.14	2009/12/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbTextEditorView.java	1.10	2008/12/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.text;

import static ssac.util.Validations.validNotNull;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;

import ssac.aadl.editor.document.IEditorDocument;
import ssac.aadl.editor.document.IEditorTextDocument;
import ssac.aadl.editor.plugin.IComponentManager;
import ssac.aadl.editor.view.EditorFrame;
import ssac.aadl.editor.view.EditorMenuBar;
import ssac.aadl.editor.view.IEditorView;
import ssac.aadl.editor.view.dialog.AbFindReplaceHandler;
import ssac.aadl.editor.view.dialog.JumpDialog;
import ssac.aadl.editor.view.menu.EditorMenuResources;
import ssac.util.Strings;
import ssac.util.logging.AppLogger;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.StatusBar;
import ssac.util.swing.SwingTools;
import ssac.util.swing.TextEditorPane;

/**
 * テキスト・エディタ・コンポーネントにスクロール機能を付加したコンポーネント。
 * 
 * @version 1.17	2011/02/02
 * 
 * @since 1.10
 */
public abstract class AbTextEditorView<D extends IEditorTextDocument> extends JScrollPane implements ITextComponent, IEditorView
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static protected final int defaultFontSize = 12;
	static protected final int defaultTabSize = 4;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final UndoManager			undoMan		= new UndoManager();
	protected final TextPaneFocusHandler	hTextFocus	= new TextPaneFocusHandler();
	protected final CaretHandler			hCaret		= new CaretHandler();
	protected final UndoHandler			hUndo		= new UndoHandler();
	protected final DocumentHandler		hDocument	= new DocumentHandler();
	protected final TextFindReplaceHandler hFind	= new TextFindReplaceHandler();
	
	protected final LineNumberPane	paneLine;
	protected final TextEditorPane	paneText;
	
	private D	textDocument;

	private volatile boolean		compoundEdits = false;
	private CompoundEdit			packEdits = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbTextEditorView(D document) {
		super(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		// setup components
		paneText = createTextEditorPane();
		paneLine = new LineNumberPane(paneText);
		initViewports();
		
		setDocument(document);
	}
	
	private void initViewports() {
		this.setViewportView(paneText);
		this.getViewport().setBackground(paneText.getBackground());
		this.setRowHeaderView(paneLine);
		
		// Mouse action
		paneText.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				evaluatePopupMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				evaluatePopupMenu(e);
			}
			protected void evaluatePopupMenu(MouseEvent me) {
				if (!me.isPopupTrigger())
					return;
				EditorFrame frame = getFrame();
				JPopupMenu pmenu = (frame==null ? null : frame.getActiveEditorConextMenu());
				if (pmenu != null) {
					pmenu.show(me.getComponent(), me.getX(), me.getY());
					me.getComponent().requestFocusInWindow();
				}
			}
		});
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このビューに新しいドキュメントを関連付ける。
	 * 
	 * @param newDocument	新しいドキュメント
	 * 
	 * @throws NullPointerException	<code>newDocument</code> が <tt>null</tt> の場合
	 */
	public void setDocument(D newDocument) {
		validNotNull(newDocument);
		
		D oldDocument = textDocument;
		if (oldDocument == newDocument) {
			// same instance
			return;
		}
		
		// detach old document
		paneLine.detachOwnerText();
		paneText.removeFocusListener(hTextFocus);
		paneText.removeCaretListener(hCaret);
		if (oldDocument != null) {
			oldDocument.removeDocumentListener(hDocument);
			oldDocument.removeUndoableEditListener(hUndo);
			
			// release document resources
			oldDocument.releaseViewResources();
		}
		
		// attach new document
		textDocument = newDocument;
		paneText.setDocument(newDocument);
		paneText.setTabSize(defaultTabSize);
		paneLine.attachOwnerText(paneText);
		paneText.addCaretListener(hCaret);
		paneText.addFocusListener(hTextFocus);
		newDocument.addDocumentListener(hDocument);
		newDocument.addUndoableEditListener(hUndo);
		
		// refresh
		refreshEditingStatus();
	}

	public void updateEditorMenus() {
		EditorFrame frame = getFrame();
		if (frame != null) {
			frame.updateMenuItem(EditorMenuResources.ID_FILE_SAVE);
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_UNDO);
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_REDO);
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_CUT);
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_COPY);
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_PASTE);
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_DELETE);
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_SELECTALL);
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_JUMP);
		}
	}
	/*
	public void refreshEdit() {
		endCompoundUndoableEdit();
		undoMan.discardAllEdits();
		updateEditorModifiedProperty();
		updateEditorSelectionChangedProperty();
		updateEditorMenus();
	}
	*/

	/**
	 * このエディタビューの標準フォントを返す。
	 * @return エディタビューの標準フォント
	 */
	static public Font getDefaultFont() {
		return SwingTools.getDefaultEditorFont(defaultFontSize);
	}
	
	public Font getEditorFont() {
		return paneText.getFont();
	}
	
	public void setEditorFont(Font font) {
		// change TextEditorPane font
		paneText.setFont(font);
		paneLine.revalidate();
		paneLine.repaint();
	}

	/**
	 * テキストエディタのUndo可能編集内容の集約を開始する。
	 * Undo可能編集内容の集約は、複数の編集操作を１回のUndoで元に戻すため、
	 * 複数の編集内容をコンテナに集約するための操作となる。
	 * 
	 * @since 1.04
	 */
	public void startCompoundUndoableEdit() {
		compoundEdits = true;
	}

	/**
	 * テキストエディタのUndo可能編集内容の集約を終了する。
	 * この操作は、<code>{@link #startCompoundUndoableEdit()}</code> によって開始された編集操作の
	 * 集約を完了し、コンテナに集約された編集操作を UndoManger へ登録する。
	 * <p>
	 * <b>(注)</b>
	 * <blockquote>
	 * このメソッドは、Swingアイテムを操作するメソッドを内部で呼び出すため、
	 * EventQueue が実行されているスレッドから、呼び出すことが必須となる。
	 * </blockquote>
	 *
	 * @since 1.04
	 */
	public void endCompoundUndoableEdit() {
		if (compoundEdits) {
			if (packEdits != null) {
				packEdits.end();
				undoMan.addEdit(packEdits);
				packEdits = null;
				EditorFrame frame = getFrame();
				if (frame != null) {
					frame.updateMenuItem(EditorMenuResources.ID_EDIT_UNDO);
					frame.updateMenuItem(EditorMenuResources.ID_EDIT_REDO);
				}
			}
			compoundEdits = false;
		}
	}

	//------------------------------------------------------------
	// implements IEditorView interfaces
	//------------------------------------------------------------
	
	/**
	 * このビューオブジェクトに関連付けられている全てのリソースを開放する。
	 * @since 1.16
	 */
	public void destroy() {
		// detach document
		paneLine.detachOwnerText();
		paneText.removeFocusListener(hTextFocus);
		paneText.removeCaretListener(hCaret);
		D oldDocument = getDocument();
		if (oldDocument != null) {
			oldDocument.removeDocumentListener(hDocument);
			oldDocument.removeUndoableEditListener(hUndo);
			
			// release document resources
			oldDocument.releaseViewResources();
		}
	}
	
	/**
	 * このドキュメントの保存先ファイルが読み取り専用の場合に <tt>true</tt> を返す。
	 * ファイルそのものが読み取り専用ではない場合でも、モジュールパッケージに
	 * 含まれるファイルの場合にも <tt>true</tt> を返す。
	 * @since 1.14
	 */
	public boolean isReadOnly() {
		return getFrame().isReadOnlyFile(getDocumentFile());
	}

	/**
	 * テキストコンポーネントのドキュメントが変更されているかを取得する。
	 * 
	 * @return 変更されていれば true
	 */
	public boolean isModified() {
		if (textDocument != null) {
			Boolean value = (Boolean)getClientProperty(IEditorView.PROP_MODIFIED);
			return (value != null ? value.booleanValue() : false);
		} else {
			return false;
		}
	}
	
	/**
	 * ドキュメントに適用されたエンコーディングとなる文字セット名を返す。
	 * このメソッドの実装では <tt>null</tt> を返してはならない。
	 */
	public String getLastEncodingName() {
		return textDocument.getLastEncodingName();
	}

	public String getDocumentTitle() {
		return (textDocument==null ? null : textDocument.getTitle());
	}

	public File getDocumentFile() {
		return (textDocument==null ? null : textDocument.getTargetFile());
	}

	public String getDocumentPath() {
		File targetFile = getDocumentFile();
		return (targetFile==null ? null : targetFile.getAbsolutePath());
	}

	public boolean hasFocusInComponent() {
		return paneText.hasFocus();
	}

	public void onChangedEditorFont(IComponentManager manager, Font font) {
		setEditorFont(font);
	}

	public void refreshEditingStatus() {
		endCompoundUndoableEdit();
		undoMan.discardAllEdits();
		updateEditorModifiedProperty();
		updateEditorSelectionChangedProperty();
		updateEditorMenus();
	}

	public void requestFocusInComponent() {
		paneText.setFocus();
	}
	
	/**
	 * このビューに関連付けられているドキュメントを取得する。
	 * @return	このビューに関連付けられているドキュメントを返す。
	 * 			ドキュメントが関連付けられていない場合は <tt>null</tt> を返す。	
	 */
	public D getDocument() {
		return textDocument;
	}
	
	/**
	 * このビューに関連付けられたドキュメントの設定情報を、
	 * 最新の情報に更新する。
	 * @return	更新された場合に <tt>true</tt> を返す。
	 * @since 1.14
	 */
	public boolean refreshDocumentSettings() {
		if (textDocument != null) {
			return textDocument.refreshSettings();
		} else {
			return false;
		}
	}
	
	/**
	 * このビューのコンポーネントを取得する。
	 * このメソッドは基本的に <code>this</code> インスタンスを返す。
	 * @return	このビューのコンポーネントオブジェクト
	 */
	public JComponent getComponent() {
		return this;
	}
	
	/**
	 * このビューを格納するエディタフレームを取得する。
	 * @return	このビューを格納するエディタフレームのインスタンス。
	 * 			このビューがフレームに格納されていない場合は <tt>null</tt> を返す。
	 */
	public EditorFrame getFrame() {
		Window parentFrame = SwingUtilities.windowForComponent(this);
		if (parentFrame instanceof EditorFrame)
			return (EditorFrame)parentFrame;
		else
			return null;
	}
	
	/**
	 * このドキュメントを管理するコンポーネント・マネージャを返す。
	 * @return	コンポーネント・マネージャ
	 */
	public IComponentManager getManager() {
		return textDocument.getManager();
	}
	
	/**
	 * このビューに関連付けられたドキュメント専用のメニューバーを返す。
	 * 専用メニューバーが未定義の場合は <tt>null</tt> を返す。
	 * @return	ドキュメント専用メニューバー
	 */
	public EditorMenuBar getDocumentMenuBar() {
		return null;	// 標準メニューバーを使用
	}

	/**
	 * このビューに関連付けられたドキュメントの保存先ファイルが、
	 * 移動可能かを判定する。
	 * @return 移動可能なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 1.17
	 */
	public boolean canMoveDocumentFile() {
		// ドキュメントがない場合は、移動不可
		IEditorDocument document = getDocument();
		if (document == null) {
			return false;
		}
		
		// ドキュメントのファイルが移動可能かを判定する。
		return document.canMoveTargetFile();
	}

	/**
	 * 常に <tt>true</tt> を返す。
	 * @return <tt>true</tt>
	 */
	public boolean canFindReplace() {
		return true;
	}
	
	/**
	 * このテキストエディタ専用の検索／置換処理のイベントハンドラを返す。
	 * @return	テキストエディタ用検索／置換処理イベントハンドラを返す。
	 */
	public TextFindReplaceHandler getFindReplaceHandler() {
		return hFind;
	}

	//------------------------------------------------------------
	// implements IEditorMenuActionHandler interfaces
	//------------------------------------------------------------
	
	public boolean onProcessMenuSelection(String command, Object source, Action action) {
		if (Strings.isNullOrEmpty(command))
			return false;	// undefined command
		
		if (EditorMenuResources.ID_EDIT_UNDO.equals(command)) {
			onMenuSelectedEditUndo();
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_REDO.equals(command)) {
			onMenuSelectedEditRedo();
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_CUT.equals(command)) {
			onMenuSelectedEditCut();
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_COPY.equals(command)) {
			onMenuSelectedEditCopy();
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_PASTE.equals(command)) {
			onMenuSelectedEditPaste();
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_DELETE.equals(command)) {
			onMenuSelectedEditDelete();
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_SELECTALL.equals(command)) {
			onMenuSelectedEditSelectAll();
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_JUMP.equals(command)) {
			onMenuSelectedEditJump();
			return true;
		}
		/*--- delete : 1.10 2008/12/06
		else if (EditorMenuResources.ID_FIND_NEXT.equals(command)) {
			onMenuSelectedFindNext();
			return true;
		}
		else if (EditorMenuResources.ID_FIND_PREV.equals(command)) {
			onMenuSelectedFindPrev();
			return true;
		}
		else if (EditorMenuResources.ID_FIND_FIND.equals(command)) {
			onMenuSelectedFindFind();
			return true;
		}
		--- delete : 1.10 2008/12/06 */
		
		// not processed
		return false;
	}

	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (Strings.isNullOrEmpty(command) || action == null)
			return false;	// undefined command
		
		if (EditorMenuResources.ID_EDIT_UNDO.equals(command)) {
			action.setEnabled(canUndo());
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_REDO.equals(command)) {
			action.setEnabled(canRedo());
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_CUT.equals(command)) {
			action.setEnabled(canCut());
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_COPY.equals(command)) {
			action.setEnabled(hasSelectedText());
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_PASTE.equals(command)) {
			action.setEnabled(canPaste());
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_DELETE.equals(command)) {
			action.setEnabled(canCut());
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_SELECTALL.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (EditorMenuResources.ID_EDIT_JUMP.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		/*--- delete : 1.10 2008/12/06
		else if (EditorMenuResources.ID_FIND_FIND.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (EditorMenuResources.ID_FIND_PREV.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		else if (EditorMenuResources.ID_FIND_NEXT.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		--- delete : 1.10 2008/12/06 */
		else if (EditorMenuResources.ID_FILE_SAVE.equals(command)) {
			action.setEnabled(!isReadOnly() && isModified());
			return true;
		}
		else if (EditorMenuResources.ID_FILE_SAVEAS.equals(command)) {
			action.setEnabled(true);
			return true;
		}
		
		// not processed
		return false;
	}

	//------------------------------------------------------------
	// implements ITextComponent interfaces
	//------------------------------------------------------------

	/**
	 * テキストコンポーネントが編集可能であるかを検証する。
	 * 
	 * @return 編集可能であれば true
	 */
	public boolean canEdit() {
		return (paneText.isEditable() && paneText.isEnabled());
	}

	/**
	 * テキストコンポーネントで選択されているテキストが
	 * 存在するかを検証する。
	 * 
	 * @return 選択テキストが存在していれば true
	 */
	public boolean hasSelectedText() {
		return (paneText.getSelectionStart() != paneText.getSelectionEnd());
	}

	/**
	 * テキストコンポーネントが Undo 可能かを検証する。
	 * 
	 * @return Undo 可能なら true
	 */
	public boolean canUndo() {
		if (canEdit()) {
			return undoMan.canUndo();
		} else {
			return false;
		}
	}

	/**
	 * テキストコンポーネントが Redo 可能かを検証する。
	 * 
	 * @return Redo 可能なら true
	 */
	public boolean canRedo() {
		if (canEdit()) {
			return undoMan.canRedo();
		} else {
			return false;
		}
	}

	/**
	 * テキストコンポーネントが Cut 可能かを検証する。
	 * @return	Cut 可能なら true
	 */
	public boolean canCut() {
		if (canEdit()) {
			return hasSelectedText();
		} else {
			return false;
		}
	}

	/**
	 * クリップボードにペースト可能なデータが存在し、
	 * テキストコンポーネントにペースト可能な状態かを検証する。
	 * 
	 * @return ペースト可能なら true
	 */
	public boolean canPaste() {
		if (canEdit()) {
			return SwingTools.existStringInClipboard(paneText);
		} else {
			return false;
		}
	}

	/**
	 * テキストコンポーネントを取得する。
	 * 
	 * @return JTextComponent インスタンス
	 */
	public TextEditorPane getTextComponent() {
		return paneText;
	}

	/**
	 * テキストコンポーネントに対して Undo する。
	 */
	public void undo() {
		try {
			undoMan.undo();
		} catch (CannotUndoException ex) {
			AppLogger.debug("Unable to undo.", ex);
		}
		updateEditorModifiedProperty();
		EditorFrame frame = getFrame();
		if (frame != null) {
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_UNDO);
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_REDO);
		}
	}

	/**
	 * テキストコンポーネントに対して Redo する。
	 */
	public void redo() {
		try {
			undoMan.redo();
		} catch (CannotRedoException ex) {
			AppLogger.debug("Unable to redo.", ex);
		}
		EditorFrame frame = getFrame();
		if (frame != null) {
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_UNDO);
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_REDO);
		}
	}

	/**
	 * 選択テキストのカット
	 */
	public void cut() {
		paneText.requestFocusInWindow();
		paneText.cut();
		EditorFrame frame = getFrame();
		if (frame != null) {
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_PASTE);
		}
	}

	/**
	 * 選択テキストのコピー
	 */
	public void copy() {
		paneText.requestFocusInWindow();
		paneText.copy();
		EditorFrame frame = getFrame();
		if (frame != null) {
			frame.updateMenuItem(EditorMenuResources.ID_EDIT_PASTE);
		}
	}

	/**
	 * 現在のキャレット位置にペースト
	 */
	public void paste() {
		paneText.requestFocusInWindow();
		paneText.paste();
	}

	/**
	 * 選択テキストのデリート
	 */
	public void delete() {
		paneText.requestFocusInWindow();
		paneText.replaceSelection("");
	}

	/**
	 * テキストコンポーネントの全てのテキストを選択する。
	 */
	public void selectAll() {
		paneText.requestFocusInWindow();
		paneText.selectAll();
	}

	/**
	 * テキストコンポーネントの行数を取得する。
	 * 
	 * @return テキストコンポーネントの行数
	 */
	public int getLineCount() {
		return paneText.getLineCount();
	}

	/**
	 * キャレットをドキュメントの先頭に移動する。
	 */
	public void jumpToBegin() {
		paneText.requestFocusInWindow();
		SwingTools.setCaretToBegin(paneText);
	}

	/**
	 * キャレットをドキュメントの終端に移動する。
	 *
	 */
	public void jumpToEnd() {
		paneText.requestFocusInWindow();
		SwingTools.setCaretToEnd(paneText);
	}

	/**
	 * キャレットを指定行の先頭に移動する。
	 * 行番号は、先頭行を 1 とする。
	 * 
	 * @param lineNo 1 から始まる行番号
	 */
	public void jumpToLine(int lineNo) {
		paneText.requestFocusInWindow();
		SwingTools.setCaretToLine(paneText, lineNo);
	}

	/**
	 * 所属するテキストコンポーネントにフォーカスを設定する。
	 *
	 */
	public void requestFocusInTextComponent() {
		requestFocusInComponent();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

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
	
	// Menu : [Edit]-[Select all]
	protected void onMenuSelectedEditSelectAll() {
		AppLogger.debug("catch [Edit]-[SelectAll] menu selection.");
		selectAll();
		requestFocusInComponent();
	}
	
	// Menu : [Edit]-[Jump to Line]
	protected void onMenuSelectedEditJump() {
		AppLogger.debug("catch [Edit]-[Jump] menu selection.");
		// Jump dialog
		int maxLines = getLineCount();
		JumpDialog dlg = new JumpDialog(getFrame(), maxLines);
		dlg.setVisible(true);
		int dlgResult = dlg.getDialogResult();
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("Dialog result : " + dlgResult);
		}
		if (dlgResult == IDialogResult.DialogResult_OK) {
			int toLineNo = dlg.getSelectedLineNo();
			jumpToLine(toLineNo);
		}
		requestFocusInComponent();
	}

	/*--- delete : 1.10 2008/12/06
	// Menu : [Find]-[Find]
	protected void onMenuSelectedFindFind() {
		AppLogger.debug("catch [Find]-[Find] menu selection.");
		requestFocusInComponent();
		EditorFrame frame = getFrame();
		if (frame != null) {
			frame.showFindReplaceDialog(hFind);
		}
	}
	
	// Menu : [Find]-[Prev]
	protected void onMenuSelectedFindPrev() {
		AppLogger.debug("catch [Find]-[Prev] menu selection");
		requestFocusInComponent();
		if (!hFind.findPrev()) {
			// not found keyword
			FindDialog.showNotFoundMessage(getFrame(), hFind.getKeywordString());
		}
		requestFocusInComponent();
	}
	
	// Menu : [Find]-[Next]
	protected void onMenuSelectedFindNext() {
		AppLogger.debug("catch [Find]-[Next] menu selection.");
		requestFocusInComponent();
		if (!hFind.findNext()) {
			// not found keyword
			FindDialog.showNotFoundMessage(getFrame(), hFind.getKeywordString());
		}
		requestFocusInComponent();
	}
	--- delete : 1.10 2008/12/06 */
	
	private TextEditorPane createTextEditorPane() {
		final TextEditorPane te = new TextEditorPane();
		te.setBackground(Color.WHITE);
		te.setForeground(Color.BLACK);
		
		// font
		/*
		Font font = AppSettings.getInstance().getFont(AppSettings.EDITOR);
		if (font == null) {
			font = getDefaultFont();
		}
		AppLogger.debug("TextEditorPane.setFont(" + font.toString() + ")");
		te.setFont(font);
		*/
		te.setFont(getDefaultFont());
		
		return te;
	}
	
	private void updateEditorModifiedProperty() {
		/*--- modified : 1.10 2008/12/06
		/*======= old
		if (undoMan.canUndo()) {
			setEditorModifiedProperty(true);
		} else if (textDocument != null) {
			setEditorModifiedProperty(textDocument.isModified());
		} else {
			setEditorModifiedProperty(false);
		}
		/*======= old =====*/
		if (undoMan.canUndo()) {
			setEditorModifiedProperty(true);
		} else if (textDocument != null) {
			setEditorModifiedProperty(textDocument.isNewDocument());
		} else {
			setEditorModifiedProperty(false);
		}
		/*--- modified : 1.10 2008/12/06 ---*/
	}
	
	private void updateEditorSelectionChangedProperty() {
		setEditorSelectionChangedProperty(hasSelectedText());
	}
	
	private void setEditorModifiedProperty(boolean modified) {
		Boolean oldValue = (Boolean)getClientProperty(ITextComponent.PROP_MODIFIED);
		boolean oldModified = (oldValue != null ? oldValue.booleanValue() : false);
		if (textDocument != null) {
			textDocument.setModifiedFlag(modified);
		}
		if (modified != oldModified) {
			putClientProperty(ITextComponent.PROP_MODIFIED, modified);
		}
	}
	
	private void setEditorSelectionChangedProperty(boolean selected) {
		Boolean oldValue = (Boolean)getClientProperty(ITextComponent.PROP_SELECTED);
		boolean oldSelected = (oldValue != null ? oldValue.booleanValue() : false);
		if (selected != oldSelected) {
			putClientProperty(ITextComponent.PROP_SELECTED, selected);
		}
	}

	// Undo handler
	class UndoHandler implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			//undoMan.addEdit(e.getEdit());
			//MainFrame mf = (MainFrame)AADLEditor.getApplicationMainFrame();
			//mf.refreshMenuItem(MainMenu.MI_EDIT_UNDO);
			//mf.refreshMenuItem(MainMenu.MI_EDIT_REDO);
			//--- since 1.04
			if (compoundEdits) {
				// CompoundEdit へ登録
				if (packEdits == null) {
					packEdits = new CompoundEdit();
				}
				packEdits.addEdit(e.getEdit());
			} else {
				// UndoManager へ登録
				undoMan.addEdit(e.getEdit());
				EditorFrame frame = getFrame();
				if (frame != null) {
					frame.updateMenuItem(EditorMenuResources.ID_EDIT_UNDO);
					frame.updateMenuItem(EditorMenuResources.ID_EDIT_REDO);
				}
			}
		}
	}
	
	// Document handler
	class DocumentHandler implements DocumentListener {
		//--- change
		public void changedUpdate(DocumentEvent e) {}
		//--- remove
		public void removeUpdate(DocumentEvent e) {
			// 状態更新？
			setEditorModifiedProperty(true);
		}
		//--- insert
		public void insertUpdate(DocumentEvent e) {
			// 状態更新？
			setEditorModifiedProperty(true);
		}
	}
	
	// Caret handler
	class CaretHandler implements CaretListener {
		public void caretUpdate(CaretEvent ce) {
			boolean selected = (ce.getDot() != ce.getMark());
			EditorFrame frame = getFrame();
			if (frame != null) {
				frame.getMenuAction(EditorMenuResources.ID_EDIT_CUT).setEnabled(selected);
				frame.getMenuAction(EditorMenuResources.ID_EDIT_COPY).setEnabled(selected);
				frame.getMenuAction(EditorMenuResources.ID_EDIT_DELETE).setEnabled(selected);
				//--- update location indicator in status bar
				if (ce.getSource() instanceof TextEditorPane) {
					((TextEditorPane)ce.getSource()).updateLocationForStatusBar(frame.getStatusBar());
				}
			}
			setEditorSelectionChangedProperty(selected);
		}
	}
	
	// Text pane focus handler
	class TextPaneFocusHandler implements FocusListener {
		public void focusGained(FocusEvent e) {
			Component cmp = e.getComponent();
			EditorFrame frame = getFrame();
			if (frame != null && cmp instanceof TextEditorPane) {
				StatusBar infoBar = frame.getStatusBar();
				TextEditorPane tp = (TextEditorPane)cmp;
				infoBar.setInsertMode(!tp.isOverwriteMode());
				tp.updateLocationForStatusBar(infoBar);
			}
		}
		public void focusLost(FocusEvent e) {
			Component cmp = e.getComponent();
			EditorFrame frame = getFrame();
			if (frame != null && cmp instanceof TextEditorPane) {
				StatusBar infoBar = frame.getStatusBar();
				infoBar.clearMessage();
				infoBar.clearInsertMode();
				infoBar.clearPosition();
			}
		}
	}
	
	// Find handler
	class TextFindReplaceHandler extends AbFindReplaceHandler
	{
		public TextFindReplaceHandler() {
			super();
		}

		/**
		 * 検索操作を許可するかどうかを判定する。
		 * @return	検索操作を許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
		 * @since 1.16
		 */
		public boolean allowFindOperation() {
			return canFindReplace();
		}

		/**
		 * 置換操作を許可するかどうかを判定する。
		 * @return	置換操作を許可する場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
		 */
		public boolean allowReplaceOperation() {
			return canEdit();
		}

		// 次を検索
		public boolean findNext() {
			return searchText(true);
		}

		// 前を検索
		public boolean findPrev() {
			return searchText(false);
		}

		// 置換して次へ
		public boolean replaceNext() {
			// replace
			String selected = paneText.getSelectedText();
			if (selected != null) {
				String strKeyword = getKeywordString();
				if (isIgnoreCase()) {
					selected = selected.toLowerCase();
					strKeyword = strKeyword.toLowerCase();
				}
				if (selected.equals(strKeyword)) {
					// 対象キーワードは選択済みのため、置換
					//--- 2008.08.18 : 置換は、UndoableEdit が 2 つ発行されるため、集約
					//---              (1) delete selected text
					//---              (2) insert text at caret position
					startCompoundUndoableEdit();
					getTextComponent().replaceSelection(getReplaceString());
					endCompoundUndoableEdit();
					_replaceCount++;
				}
			}
			
			// 次を検索
			return searchText(true);
		}

		// すべて置換
		public boolean replaceAll() {
			// 初期化
			_replaceCount = 0;
			
			// 検索キーワード取得
			String strKeyword = getKeywordString();
			if (Strings.isNullOrEmpty(strKeyword)) {
				return false;	// search keyword nothing!
			}
			int lenKeyword = strKeyword.length();
			
			// 検索対象テキスト取得
			String nowText = paneText.getText();
			
			// 大文字／小文字の区別
			if (isIgnoreCase()) {
				// 大文字小文字を区別しない
				nowText = nowText.toLowerCase();
				strKeyword = strKeyword.toLowerCase();
			}
			
			// 置換テキストの位置情報を取得
			ArrayList<Integer> poslist = null;
			int fpos = nowText.indexOf(strKeyword);
			if (fpos >= 0) {
				poslist = new ArrayList<Integer>();
				do {
					poslist.add(fpos);
					fpos = nowText.indexOf(strKeyword, (fpos+lenKeyword));
				} while (fpos >= 0);
			}
			
			// 検索キーワードが存在しないなら終了
			if (poslist == null) {
				return false;
			}
			
			// 現在位置から終端までの置換
			startCompoundUndoableEdit();
			String strReplace = getReplaceString();
			int subWords = strReplace.length() - lenKeyword;
			for (int pos : poslist) {
				//--- 置換範囲を設定
				int spos = pos + subWords * _replaceCount;
				int epos = spos + lenKeyword;
				paneText.setCaretPosition(spos);
				paneText.moveCaretPosition(epos);
				//--- 置換
				paneText.replaceSelection(strReplace);
				_replaceCount++;
			}
			endCompoundUndoableEdit();

			// 完了
			return (_replaceCount>0);
		}
		
		private boolean searchText(boolean dirToEnd) {
			// check
			String strKeyword = getKeywordString();
			if (Strings.isNullOrEmpty(strKeyword)) {
				// not found
				return false;
			}
			
			// search
			String nowText = paneText.getText();
			if (isIgnoreCase()) {
				// 大文字小文字を区別しない
				nowText = nowText.toLowerCase();
				strKeyword = strKeyword.toLowerCase();
			}
			int found = -1;
			if (dirToEnd) {
				// 下へ検索
				int spos = paneText.getSelectionEnd();
				found = nowText.indexOf(strKeyword, spos);
				if (found < 0) {
					// 先頭から検索
					found = nowText.indexOf(strKeyword);
				}
			} else {
				// 上へ検索
				int spos = paneText.getSelectionStart();
				String subText = nowText.substring(0, spos);
				found = subText.lastIndexOf(strKeyword);
				if (found < 0) {
					// 終端から検索
					found = nowText.lastIndexOf(strKeyword);
				}
			}
			
			// result
			if (found >= 0) {
				// 発見
				//editor.getTextComponent().setSelectionStart(found);
				//editor.getTextComponent().setSelectionEnd(found + strKeyword.length());
				paneText.setCaretPosition(found);
				paneText.moveCaretPosition(found + strKeyword.length());
				return true;
			}
			else {
				// 見つからない
				return false;
			}
		}
	}
}
