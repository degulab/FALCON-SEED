/*
 * @(#)DtContainerContentDetailView.java	1.1.0	2023/01/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerContentDetailView.java	1.0.0	2022/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import dtalge.container.editor.DtContainerEditor;
import dtalge.container.editor.content.DtContainerContentTypes;
import dtalge.container.editor.content.IDtContainerEditView;
import dtalge.container.editor.content.common.table.AbDTCContentAlgeEditView;
import dtalge.container.editor.content.common.table.DTCContentDtalgeEditView;
import dtalge.container.editor.content.common.table.DTCContentExalgeEditView;
import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;
import dtalge.container.editor.view.DtContainerEditorFrame;
import ssac.util.swing.menu.IMenuActionHandler;

/**
 * データコンテナ要素の編集ビューの共通実装。
 * このビューは、サイズ変更不可の JSplitPane の派生とし、上部にはツリーの位置情報を表示する。
 * 
 * @version 1.1.0
 */
public class DtContainerContentDetailView extends JPanel implements IMenuActionHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	/** 何も表示しないアイテムビューの ID **/
	static public final String VIEWID_EMPTY		= "EmptyView";
	/** Dtalge の内容をテーブル形式で編集するアイテムビューの ID **/
	static public final String VIEWID_EXALGE	= "ExalgeEditView";
	/** Dtalge の内容をテーブル形式で編集するアイテムビューの ID **/
	static public final String VIEWID_DTALGE	= "DtalgeEditView";
	/** ノートの内容をテーブル形式で編集するアイテムビューの ID **/
	static public final String VIEWID_NOTE		= "NoteEditView";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** このビューを管理する、親のビュー、{@code null} ではない **/
	protected IDtContainerEditView		_parentEditorView;
	/** このビューでの編集対象ツリーノード **/
	private IDtContainerContentTreeNode	_targetNode;
	
	/** 編集ビューを切り替えるためのレイアウトマネージャー **/
	private CardLayout		_cardLayout;
	/** 現在表示されているカードの ID **/
	private String			_currentCard;
	/** 何も表示しない詳細ビュー **/
	private JPanel		_cardEmptyView;
	/** 交換代数元の編集テーブルを保持する詳細ビュー **/
	private DTCContentExalgeEditView	_cardExalgeEditView;
	/** データ代数元の編集テーブルを保持する詳細ビュー **/
	private DTCContentDtalgeEditView	_cardDtalgeEditView;
	/** ノート編集テーブルを保持する詳細ビュー **/
	private DTCContentDtalgeEditView	_cardNoteEditView;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtContainerContentDetailView(IDtContainerEditView parentView) {
		super();
		if (parentView == null)
			throw new NullPointerException("Parent editor view is null");
		_parentEditorView = parentView;
		
		// カードビュー
		_cardLayout = new CardLayout();
		setLayout(_cardLayout);
	}
	
	public void initialComponent() {
		// create componenents
		createComponents();
		
		// setup layout
		setupLayout();
		
		// setup actions
		setupActions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
//	public DTCContentExalgeEditView getExalgeEditView() {
//		return _cardExalgeEditView;
//	}
//	
//	public DTCContentDtalgeEditView getDtalgeEditView() {
//		return _cardDtalgeEditView;
//	}
//	
//	public DTCContentDtalgeEditView getNoteEditView() {
//		return _cardNoteEditView;
//	}
	
	/**
	 * アクティブな代数元編集ビューを取得する。
	 * @return	アクティブな代数編集ビュー、アクティブなビューが存在しない場合は {@code null}
	 * @since 1.1.0
	 */
	public AbDTCContentAlgeEditView getActiveAlgeEditView() {
		if (_cardNoteEditView.isVisible()) {
			return _cardNoteEditView;
		}
		else if (_cardDtalgeEditView.isVisible()) {
			return _cardDtalgeEditView;
		}
		else if (_cardExalgeEditView.isVisible()) {
			return _cardExalgeEditView;
		}
		else {
			return null;
		}
	}
	
	/**
	 * このビューが所属するメインフレームを取得する。
	 * @return	{@code null} ではない、メインフレーム
	 */
	public DtContainerEditorFrame getFrame() {
		return (DtContainerEditorFrame)DtContainerEditor.getApplicationMainFrame();
	}
	
	/**
	 * このビューが所属する親エディタビューを取得する。
	 * 親エディタビューは、メインフレームにタブで表示される、ドキュメントごとの編集ビューを意味する。
	 * @return	{@code null} ではない、親エディタビュー
	 */
	public IDtContainerEditView getParentEditorView() {
		return _parentEditorView;
	}
	
	/**
	 * このビューに設定されている対象ツリーノードを取得する。
	 * @return	設定されている編集対象ツリーノード、設定されていない場合は {@code null}
	 */
	public IDtContainerContentTreeNode getTargetTreeNode() {
		return _targetNode;
	}
	
	/**
	 * このビューに、編集対象のツリーノードを設定する。
	 * @param newTreeNode	編集対象として設定するツリーノード
	 */
	public void setTargetTreeNode(IDtContainerContentTreeNode newTreeNode) {
		if (newTreeNode == _targetNode) {
			return;	// no changes
		}
		
		IDtContainerContentTreeNode oldTreeNode = _targetNode;
		if (oldTreeNode != null) {
			detachTargetTreeNode(oldTreeNode);
		}
		
		_targetNode = newTreeNode;
		if (newTreeNode != null) {
			attachTargetTreeNode(newTreeNode);
		}
		
		refreshViewingStatus();
	}
	
	/**
	 * このビュー内のメインビューのコンポーネントにフォーカス設定をリクエストする。
	 */
	public void requestFocusInComponent() {
		if (VIEWID_NOTE.equals(_currentCard)) {
			_cardNoteEditView.requestFocusInComponent();
		}
		else if (VIEWID_EXALGE.equals(_currentCard)) {
			_cardExalgeEditView.requestFocusInComponent();
		}
		else if (VIEWID_DTALGE.equals(_currentCard)) {
			_cardDtalgeEditView.requestFocusInComponent();
		}
	}
	
	/**
	 * 現在のノードで、コンテントパスの表示を更新する。
	 */
	public void refreshContentPathString() {
		if (VIEWID_NOTE.equals(_currentCard)) {
			_cardNoteEditView.refreshContentPathByTargetTreeNode();
		}
		else if (VIEWID_EXALGE.equals(_currentCard)) {
			_cardExalgeEditView.refreshContentPathByTargetTreeNode();
		}
		else if (VIEWID_DTALGE.equals(_currentCard)) {
			_cardDtalgeEditView.refreshContentPathByTargetTreeNode();
		}
	}
	
	/**
	 * 現在のノードで表示を更新する。
	 */
	public void refreshViewingStatus() {
//		if (VIEWID_NOTE.equals(_currentCard)) {
//			_cardNoteEditView.refreshViewingStatus();
//		}
//		else if (VIEWID_STATIC.equals(_currentCard)) {
//			_cardStaticTextView.refreshViewingStatus();
//		}
		refreshMainView();
		refreshContentPathString();
	}
	
//	/**
//	 * 編集対象コンテントの位置表示を更新する。
//	 */
//	public void refreshContentPath() {
//		if (_targetNode == null) {
//			// 表示テキストなし
//			_stcContentPath.setText("");
//		}
//		else {
//			// パスを表示
//			_stcContentPath.setText(_targetNode.getContentPathString());
//		}
//	}

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
		// ノートエディタが表示されていれば、そのビューでメニュー処理
		if (_cardNoteEditView.isVisible() && _cardNoteEditView.onProcessMenuSelection(command, source, action)) {
			return true;
		}
		
		// Exalge エディタが表示されていれば、そのビューでメニュー処理
		if (_cardExalgeEditView.isVisible() && _cardExalgeEditView.onProcessMenuSelection(command, source, action)) {
			return true;
		}
		
		// Dtalge エディタが表示されていれば、そのビューでメニュー処理
		if (_cardDtalgeEditView.isVisible() && _cardDtalgeEditView.onProcessMenuSelection(command, source, action)) {
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
		// ノートエディタが表示されていれば、そのビューでメニュー処理
		if (_cardNoteEditView.isVisible() && _cardNoteEditView.onProcessMenuUpdate(command, source, action)) {
			return true;
		}
		
		// Exalge エディタが表示されていれば、そのビューでメニュー処理
		if (_cardExalgeEditView.isVisible() && _cardExalgeEditView.onProcessMenuUpdate(command, source, action)) {
			return true;
		}
		
		// Dtalge エディタが表示されていれば、そのビューでメニュー処理
		if (_cardDtalgeEditView.isVisible() && _cardDtalgeEditView.onProcessMenuUpdate(command, source, action)) {
			return true;
		}
		
		// その他は、処理しない
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * 現在のデータモデルに合わて、ビューの表示を更新する。
	 */
	protected void refreshMainView() {
		if (_targetNode == null) {
			_cardLayout.show(this, VIEWID_EMPTY);
			_currentCard = VIEWID_EMPTY;
			return;
		}
		
		DtContainerContentTypes nodeType = _targetNode.getContentType();
		switch (nodeType) {
			case ContentDtSlipNote:
			case ContentDtBinderNote:
				_cardNoteEditView.setEditModel(_targetNode);
				_cardLayout.show(this, VIEWID_NOTE);
				_currentCard = VIEWID_NOTE;
				break;
			case ContentExalge:
				_cardExalgeEditView.setEditModel(_targetNode);
				_cardLayout.show(this, VIEWID_EXALGE);
				_currentCard = VIEWID_EXALGE;
				break;
			case ContentDtalge:
				_cardDtalgeEditView.setEditModel(_targetNode);
				_cardLayout.show(this, VIEWID_DTALGE);
				_currentCard = VIEWID_DTALGE;
				break;
			default:
				_cardLayout.show(this, VIEWID_EMPTY);
				_currentCard = VIEWID_EMPTY;
		}
	}
	
	/**
	 * このビューに設定されている編集対象のツリーノードを、ビューから切り離す。
	 * @param treeNode	デタッチするツリーノード、指定しない場合は {@code null}
	 */
	protected void detachTargetTreeNode(IDtContainerContentTreeNode treeNode) {
		if (treeNode == null) {
			return;	// no detach
		}
		
		// detach
	}
	
	/**
	 * このビューに編集対象のツリーノードを割り付ける。
	 * @param treeNode	アタッチするツリーノード、指定しない場合は {@code null}
	 */
	protected void attachTargetTreeNode(IDtContainerContentTreeNode treeNode) {
		if (treeNode == null) {
			return;	// no attach
		}
		
		// attach
	}
	
	/**
	 * このビューのコンポーネントを生成する。
	 */
	protected void createComponents() {
		// empty detail view
		_cardEmptyView = new JPanel();
		
		//// static text view
		//_cardStaticTextView = new DtContainerContentStaticTextView(this);
		//_cardStaticTextView.initialComponent();
		
		// Note editor
		_cardNoteEditView = new DTCContentDtalgeEditView(this);
		_cardNoteEditView.initialComponent();
		
		// Exalge editor
		_cardExalgeEditView = new DTCContentExalgeEditView(this);
		_cardExalgeEditView.initialComponent();
		
		// Dtalge editor
		_cardDtalgeEditView = new DTCContentDtalgeEditView(this);
		_cardDtalgeEditView.initialComponent();
	}
	
	/**
	 * このビューのコンポーネントを配置する。
	 */
	protected void setupLayout() {
		// card layout
		this.add(_cardEmptyView, VIEWID_EMPTY);
		this.add(_cardNoteEditView, VIEWID_NOTE);
		this.add(_cardExalgeEditView, VIEWID_EXALGE);
		this.add(_cardDtalgeEditView, VIEWID_DTALGE);
		_cardLayout.show(this, VIEWID_EMPTY);
	}
	
	/**
	 * このビュー内のアクションを初期化する。
	 */
	protected void setupActions() {
		// place holder
	}
	
//	/**
//	 * コンテントパスを表示するテキストコンポーネントを生成する。
//	 * @return	生成されたテキストコンポーネント
//	 */
//	protected JTextComponent createContentPathTextComponent() {
//		JTextField txt = new JTextField();
//		txt.setEditable(false);
//		return txt;
//	}
//	
//	protected JComponent createContentPathPanel() {
//		JPanel pnl = new JPanel(new GridBagLayout());
//		pnl.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
//		
//		JLabel lblPath = new JLabel(DtContainerEditorMessages.getInstance().editor_lbl_ObjectPath + ": ");
//		
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.gridwidth = 1;
//		gbc.gridheight = 1;
//		gbc.weighty = 1;
//		gbc.gridy = 0;
//		//--- content path
//		gbc.gridx = 0;
//		gbc.weightx = 0;
//		gbc.fill = GridBagConstraints.NONE;
//		gbc.anchor = GridBagConstraints.EAST;
//		pnl.add(lblPath, gbc);
//		gbc.gridx++;
//		gbc.weightx = 1;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		gbc.anchor = GridBagConstraints.WEST;
//		pnl.add(_stcContentPath, gbc);
//		
//		return pnl;
//	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static public class DtContainerContentStaticTextView extends AbDtContainerContentItemView
	{
		private static final long serialVersionUID = 1L;
		
		private JTextArea	_cText;
		
		public DtContainerContentStaticTextView(DtContainerContentDetailView parentContainer) {
			super(parentContainer);
		}
		
		public String getText() {
			return _cText.getText();
		}
		
		public void setText(String text) {
			_cText.setText(text);
		}
		
		/**
		 * コピー操作が可能かどうかを判定する。
		 * @return	操作が可能なら {@code true}
		 */
		@Override
		public boolean canCopy() {
			// オブジェクトパスが選択されていれば、コピー可能
			if (canContentPathCopy()) {
				return true;
			}
			
			// テキストコンポーネントがフォーカスを持っており、テキストの一部が選択されていれば、コピー可能
			if (_cText.getSelectionEnd() > 0 && _cText.getSelectionEnd() > _cText.getSelectionStart()) {
				// 選択テキストあり
				return true;
			}
			else {
				// 選択テキストなし
				return false;
			}
		}
		
		/**
		 * コピー操作を実行する。
		 */
		@Override
		public void copy() {
			// テキストコンポーネントで選択されたテキストがあれば、コピー
			if (_cText.getSelectionEnd() > 0 && _cText.getSelectionEnd() > _cText.getSelectionStart()) {
				// 選択テキストあり
				_cText.copy();
				return;
			}
			
			// オブジェクトパスで選択されたテキストがあれば、コピー
			doContentPathCopy();
		}
		
		/**
		 * このビュー内のメインビューのコンポーネントにフォーカス設定をリクエストする。
		 */
		@Override
		public void requestFocusInComponent() {
			_cText.requestFocusInWindow();
		}
		
		/**
		 * 現在のノードで表示を更新する。
		 */
		@Override
		public void refreshViewingStatus() {
			IDtContainerContentTreeNode ndTarget = getTargetTreeNode();
			if (ndTarget == null) {
				_cText.setText("");
			}
			else {
				Object objValue = ndTarget.getUserObject();
				_cText.setText(objValue==null ? "" : objValue.toString());
			}
		}
		
		/**
		 * このビューのコンポーネントを生成する。
		 */
		@Override
		protected void createComponents() {
			super.createComponents();
			
			_cText = new JTextArea();
			_cText.setEditable(false);
			_cText.setLineWrap(true);
			_cText.setWrapStyleWord(true);
		}
		
		/**
		 * このビューのコンポーネントを配置する。
		 */
		@Override
		protected void setupLayout() {
			super.setupLayout();
			
			JScrollPane scText = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scText.setViewportView(_cText);
			
			this.add(scText, BorderLayout.CENTER);
		}
		
		/**
		 * このビュー内のアクションを初期化する。
		 */
		protected void setupActions() {
			// place holder
			_cText.addCaretListener(new CaretListener() {
				@Override
				public void caretUpdate(CaretEvent e) {
					onCaretChangedAtContentPathComponent(e);
				}
			});
		}
	}
}
