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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)DnDTree.java	1.17	2010/11/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DnDTree.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.tree;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import ssac.util.Objects;
import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.MenuItemResource;

/**
 * ドラッグ＆ドロップに対応したツリーコンポーネント。
 * 
 * @version 1.17	2010/11/24
 * @since 1.14
 */
public class DnDTree extends JTree
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String TreeEditCutActionName    = "dndtree.edit.cut";
	static protected final String TreeEditCopyActionName   = "dndtree.edit.copy";
	static protected final String TreeEditPasteActionName  = "dndtree.edit.paste";
	static protected final String TreeEditDeleteActionName = "dndtree.edit.delete";
	
	static private final String MenuAccelerationActionName = "Dummy.menu.accelerator";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static protected final DnDTreeDragSourceHandler dndTreeDragSourceHandler = new DnDTreeDragSourceHandler();
	
	static private final Action editCutAction    = new EditActionHandler(TreeEditCutActionName, "cut");
	static private final Action editCopyAction   = new EditActionHandler(TreeEditCopyActionName, "copy");
	static private final Action editPasteAction  = new EditActionHandler(TreeEditPasteActionName, "paste");
	static private final Action editDeleteAction = new EditActionHandler(TreeEditDeleteActionName, "delete");
	static private final Action dummyMenuAction  = new MenuAcceleratorActionHandler();

	/** このツリーの外観や挙動を制御するハンドラ **/
	private IDnDTreeHandler	_treeHandler = null;
	/** ドラッグ中のカーソル位置に存在するツリーノードのパス **/
	private TreePath			_dragOverPath = null;
	/** ドラッグ時のツリー自動展開を有効にするフラグ **/
	private boolean _autoExpandWhenDragOver = true;
	/** このコンポーネントのドロップターゲットサイトでドラッグ中であることを示すフラグ **/
	private boolean _dndTargetEntered = false;
	/** ドラッグ中の操作において、ドラッグソースとドロップターゲットがどちらもこのコンポーネントであることを示すフラグ **/
	private boolean _dndLocalDragging = false;

	/** このコンポーネント標準の <code>DefaultTreeDropTargetListener</code> **/
	transient private DefaultTreeDropTargetListener	_defDropTargetListener;
	/** このコンポーネント標準の <code>TransferHandler</code> **/
	transient private TransferHandler					_defTransferHandler;
	/** このコンポーネント標準の <code>TreeCellRenderer</code> **/
	transient private TreeCellRenderer				_defTreeCellRenderer;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public DnDTree() {
		super();
		initialSetup();
	}

	public DnDTree(Hashtable<?, ?> value) {
		super(value);
		initialSetup();
	}

	public DnDTree(Object[] value) {
		super(value);
		initialSetup();
	}

	public DnDTree(TreeModel newModel) {
		super(newModel);
		initialSetup();
	}

	public DnDTree(TreeNode root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
		initialSetup();
	}

	public DnDTree(TreeNode root) {
		super(root);
		initialSetup();
	}

	public DnDTree(Vector<?> value) {
		super(value);
		initialSetup();
	}
	
	private void initialSetup() {
		// setup status
		setRootVisible(true);
		setEditable(false);
		
		// setup TreeCellRenderer
		setCellRenderer(getDefaultTreeCellRenderer());
		
		// setup TransferHandler
		setTransferHandler(getDefaultTransferHandler());
		
		// setup DropSource
		setDragEnabled(true);
		//DnDTreeDragSource dndSource = new DnDTreeDragSource(this, DnDConstants.ACTION_COPY_OR_MOVE);
		
		// setup DropTargetListener
		DefaultTreeDropTargetListener listener = getDefaultTreeDropTargetListener();
		if (listener != null) {
			new DropTarget(this, listener);
		}
		
		// 編集アクションの設定
		setupEditActions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このツリーコンポーネントのカット・アクションを返す。
	 * @return	カット・アクション
	 */
	static public final Action getCutAction() {
		return editCutAction;
	}
	
	/**
	 * このツリーコンポーネントのコピー・アクションを返す。
	 * @return	コピー・アクション
	 */
	static public final Action getCopyAction() {
		return editCopyAction;
	}
	
	/**
	 * このツリーコンポーネントのペースト・アクションを返す。
	 * @return	ペースト・アクション
	 */
	static public final Action getPasteAction() {
		return editPasteAction;
	}
	
	/**
	 * このツリーコンポーネントの削除アクションを返す。
	 * @return	削除アクション
	 */
	static public final Action getDeleteAction() {
		return editDeleteAction;
	}

	public boolean isAutoExpandWhenDragOverEnabled() {
		return _autoExpandWhenDragOver;
	}
	
	public void setAutoExpandWhenDragOverEnabled(boolean enable) {
		_autoExpandWhenDragOver = enable;
		/*
		DefaultTreeDropTargetListener listener = getDefaultTreeDropTargetListener();
		if (listener != null) {
			listener.setAutoExpandEnabled(enable);
		}
		*/
	}

	/**
	 * このコンポーネントのドロップターゲットサイトでドラッグ中の場合に <tt>true</tt> を返す。
	 */
	public boolean isDnDTargetEntered() {
		return _dndTargetEntered;
	}

	/**
	 * このコンポーネントのドロップターゲットサイトでドラッグ中であることを
	 * 示すフラグを設定する。
	 * @param toEntered	このコンポーネントでドラッグ中であれば <tt>true</tt>、
	 * 					そうでない場合は <tt>false</tt> を指定する。
	 */
	protected void setDnDTargetEntered(boolean toEntered) {
		_dndTargetEntered = toEntered;
	}

	/**
	 * ドラッグ中の操作において、ドラッグソースとドロップターゲットが
	 * どちらもこのコンポーネントであれば <tt>true</tt> を返す。
	 */
	public boolean isDnDLocalDragging() {
		return _dndLocalDragging;
	}

	/**
	 * ドラッグ中の操作において、ドラッグソースとドロップターゲットが
	 * どちらもこのコンポーネントであることを示すフラグを設定する。
	 * @param toDragging	このコンポーネントのみでドラッグ＆ドロップ操作が
	 * 						行われている場合は <tt>true</tt>、
	 * 						そうでない場合は <tt>false</tt> を指定する。
	 */
	protected void setDnDLocalDragging(boolean toDragging) {
		_dndLocalDragging = toDragging;
	}

	/**
	 * このコンポーネントの挙動制御を行うハンドラを取得する。
	 * @return 設定されている <code>IDnDTreeHandler</code> を返す。
	 * 			設定されていない場合は <tt>null</tt> を返す。
	 */
	public IDnDTreeHandler getTreeHandler() {
		return _treeHandler;
	}

	/**
	 * このコンポーネントに挙動制御を行うハンドラを設定する。
	 * @param newHandler	新たに設定する <code>IDnDTreeHandler</code> オブジェクト。
	 * 						<tt>null</tt> を指定した場合は、既存のハンドラが破棄される。
	 */
	public void setTreeHandler(IDnDTreeHandler newHandler) {
		//IDnDTreeHandler oldHandler = _treeHandler;
		_treeHandler = newHandler;
		//if (oldHandler != newHandler) {
		//	// 必要な更新処理
		//}
	}

	/**
	 * レンダリングによって呼び出され、指定された値をテキストに変換する。
	 * この実装は、<code>value.toString</code> を返し、ほかのすべての引数を無視する。
	 * 変換を制御するには、このメソッドをサブクラス化し、必要な任意の引数を使用する。
	 * ツリーハンドラが設定されている場合は、そのハンドラのメソッドを呼び出す。
	 * @param value		テキストに変換する <code>Object</code>
	 * @param selected	ノードが選択されている場合は <tt>true</tt>
	 * @param expanded	ノードが展開されている場合は <tt>true</tt>
	 * @param leaf		ノードが葉ノードの場合は <tt>true</tt>
	 * @param row		ノードの表示行を指定する <code>int</code> 値。0 は最初の行
	 * @param hasFocus	ノードがフォーカスを持つ場合は <tt>true</tt>
	 * @return	ノードの値の <code>String</code> 表現
	 */
	@Override
	public String convertValueToText(Object value, boolean selected, boolean expanded,
									boolean leaf, int row, boolean hasFocus)
	{
		/*
		IDnDTreeHandler handler = getTreeHandler();
		if (handler != null) {
			String strValue = handler.convertValueToText(this, value, selected, expanded, leaf, row, hasFocus);
			if (strValue != null) {
				return strValue;
			}
		}
		*/
		// for Default
		return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
	}

	/**
	 * レンダリングによって呼び出され、指定された値をアイコンに変換する。
	 * 特殊な変換を行うには、このメソッドをサブクラス化し、必要な任意の引数を使用する。
	 * ツリーハンドラが設定されている場合は、そのハンドラのメソッドを呼び出す。
	 * @param value		アイコンに変換する <code>Object</code>
	 * @param selected	ノードが選択されている場合は <tt>true</tt>
	 * @param expanded	ノードが展開されている場合は <tt>true</tt>
	 * @param leaf		ノードが葉ノードの場合は <tt>true</tt>
	 * @param row		ノードの表示行を指定する <code>int</code> 値。0 は最初の行
	 * @param hasFocus	ノードがフォーカスを持つ場合は <tt>true</tt>
	 * @return	<code>Icon</code> オブジェクト。デフォルトのアイコン表示のままとする場合は <tt>null</tt> を返す。
	 */
	public Icon convertValueToIcon(Object value, boolean selected, boolean expanded,
									boolean leaf, int row, boolean hasFocus)
	{
		/*
		IDnDTreeHandler handler = getTreeHandler();
		if (handler != null) {
			Icon icon = handler.convertValueToIcon(this, value, selected, expanded, leaf, row, hasFocus);
			if (icon != null) {
				return icon;
			}
		}
		*/
		// for Default
		return null;
	}

	/**
	 * レンダリングによって呼び出され、指定された値のツールチップ文字列を取得する。
	 * この実装は、<code>getToolTipText()</code> を返し、すべての引数を無視する。
	 * 特殊なツールチップを取得するには、このメソッドをサブクラス化し、必要な任意の引数を使用する。
	 * ツリーハンドラが設定されている場合は、そのハンドラのメソッドを呼び出す。
	 * @param value		対象の値を示す <code>Object</code>
	 * @param selected	ノードが選択されている場合は <tt>true</tt>
	 * @param expanded	ノードが展開されている場合は <tt>true</tt>
	 * @param leaf		ノードが葉ノードの場合は <tt>true</tt>
	 * @param row		ノードの表示行を指定する <code>int</code> 値。0 は最初の行
	 * @param hasFocus	ノードがフォーカスを持つ場合は <tt>true</tt>
	 * @return	ツールチップ文字列。ツールチップを表示しない場合は <tt>null</tt>
	 */
	public String getToolTipText(Object value, boolean selected, boolean expanded,
									boolean leaf, int row, boolean hasFocus)
	{
		/*
		IDnDTreeHandler handler = getTreeHandler();
		if (handler != null) {
			String tooltip = handler.getToolTipText(this, value, selected, expanded, leaf, row, hasFocus);
			if (tooltip != null) {
				return tooltip;
			}
		}
		*/
		// for Default
		return super.getToolTipText();
	}

	/**
	 * このコンポーネントに、メニューアクセラレータキーとなるキーストロークを
	 * 登録する。ここで登録されたキーストロークは、テーブル内で処理をせず、
	 * メニューコンポーネントのキーストロークとして処理される。
	 * <b>注：</b>
	 * <blockquote>
	 * 次のキーストロークは、<code>DnDTree</code> 独自の処理を実行する。
	 * <ul>
	 * <li>[Ctrl]+[X] - カット
	 * <li>[Ctrl]+[C] - コピー
	 * <li>[Ctrl]+[V] - ペースト
	 * <li>[Delete]   - 削除
	 * </ul>
	 * 上記以外のキーストロークも <code>DnDTree</code> の標準キーストローク
	 * マッピングとして登録されているものもある。
	 * </blockquote>
	 * 
	 * @param strokes	登録するキーストローク
	 * @throws NullPointerException	<code>strokes</code> が <tt>null</tt> の場合、
	 * 									もしくは、<code>strokes</code> 内の要素に <tt>null</tt> が含まれている場合
	 * 
	 * @since 1.10
	 */
	public void registMenuAcceleratorKeyStroke(KeyStroke...strokes) {
		// check argument
		if (strokes == null)
			throw new NullPointerException("'strokes' is null.");
		
		// get Maps
		InputMap imap = getInputMap(WHEN_FOCUSED);
		ActionMap amap = getActionMap();

		// regist key strokes
		for (KeyStroke ks : strokes) {
			if (ks == null)
				throw new NullPointerException("'strokes' in null element.");
			SwingTools.registerActionToMaps(imap, amap, ks, MenuAccelerationActionName, dummyMenuAction);
		}
	}

	/**
	 * ドラッグ中のカーソル位置に存在するツリーノードへのパスを返す。
	 * カーソル位置にノードが存在しない場合や、ドラッグ中ではない場合には <tt>null</tt> を返す。
	 */
	public TreePath getDragOverPath() {
		return _dragOverPath;
	}

	/**
	 * ドラッグ中のカーソル位置に存在するツリーノードを返す。
	 * カーソル位置にノードが存在しない場合や、ドラッグ中ではない場合には <tt>null</tt> を返す。
	 */
	public Object getDragOverTreeObject() {
		if (_dragOverPath != null)
			return _dragOverPath.getLastPathComponent();
		else
			return null;
	}

	/**
	 * このコンポーネントのツールチップ表示を制御する。
	 * なお、現在のステータスは取得できない。
	 * @param enable	ツールチップを有効にする場合は <tt>true</tt>、無効にする場合は <tt>false</tt>
	 */
	public void setTooltipEnabled(boolean enable) {
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        if (enable) {
        	toolTipManager.registerComponent(this);
        } else {
        	toolTipManager.unregisterComponent(this);
        }
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * ドラッグ中のカーソル位置に存在するツリーノードを設定する。
	 * @param path	ドラッグ中のカーソル位置とするツリーパス
	 */
	protected void setDragOverPath(TreePath path) {
		if (!Objects.isEqual(_dragOverPath, path)) {
			TreePath oldPath = _dragOverPath;
			_dragOverPath = path;
			if (path != null) {
				Rectangle rBounds = getPathBounds(path);
				if (oldPath != null) {
					rBounds = rBounds.union(getPathBounds(oldPath));
				}
				repaint(rBounds);
			}
			else if (oldPath != null) {
				Rectangle rBounds = getPathBounds(oldPath);
				repaint(rBounds);
			}
		}
	}
	
	protected DnDTreeCellRenderer createDefaultTreeCellRenderer() {
		DnDTreeCellRenderer renderer = new DnDTreeCellRenderer();
		return renderer;
	}
	
	protected DnDTreeDropTargetListener createDefaultTreeDropTargetListener() {
		DnDTreeDropTargetListener listener = new DnDTreeDropTargetListener(true);
		return listener;
	}
	
	protected DnDTreeTransferHandler createDefaultTransferHandler() {
		DnDTreeTransferHandler handler = new DnDTreeTransferHandler(this);
		return handler;
	}
	
	protected TreeCellRenderer getDefaultTreeCellRenderer() {
		if (_defTreeCellRenderer == null) {
			_defTreeCellRenderer = createDefaultTreeCellRenderer();
		}
		return _defTreeCellRenderer;
	}
	
	protected TransferHandler getDefaultTransferHandler() {
		if (_defTransferHandler == null) {
			_defTransferHandler = createDefaultTransferHandler();
		}
		return _defTransferHandler;
	}
	
	protected DefaultTreeDropTargetListener getDefaultTreeDropTargetListener() {
		if (_defDropTargetListener == null) {
			_defDropTargetListener = createDefaultTreeDropTargetListener();
		}
		return _defDropTargetListener;
	}

	/**
	 * このツリーコンポーネントの編集アクションを設定する。
	 * 基本的に、InputMap と ActionMap に対し、キーストロークに応じた
	 * アクションの定義となる。
	 */
	protected void setupEditActions() {
		// get Maps
		InputMap imap = getInputMap(WHEN_FOCUSED);
		ActionMap amap = getActionMap();
		
		// 編集アクション設定
		if (imap != null) {
			//--- cut
			SwingTools.registerActionToMaps(imap, amap,
					MenuItemResource.getEditCutShortcutKeyStroke(),
					getCutAction().getValue(Action.ACTION_COMMAND_KEY),
					getCutAction());
			//--- copy
			SwingTools.registerActionToMaps(imap, amap,
					MenuItemResource.getEditCopyShortcutKeyStroke(),
					getCopyAction().getValue(Action.ACTION_COMMAND_KEY),
					getCopyAction());
			//--- paste
			SwingTools.registerActionToMaps(imap, amap,
					MenuItemResource.getEditPasteShortcutKeyStroke(),
					getPasteAction().getValue(Action.ACTION_COMMAND_KEY),
					getPasteAction());
			//--- delete
			SwingTools.registerActionToMaps(imap, amap,
					MenuItemResource.getEditDeleteShortcutKeyStroke(),
					getDeleteAction().getValue(Action.ACTION_COMMAND_KEY),
					getDeleteAction());
		}
		
		// 既存のカット、コピー、ペーストアクションを変更
		Object key;
		//--- cut
		key = TransferHandler.getCutAction().getValue(Action.NAME);
		if (amap.get(key) != null) {
			amap.put(key, getCutAction());
		}
		//--- copy
		key = TransferHandler.getCopyAction().getValue(Action.NAME);
		if (amap.get(key) != null) {
			amap.put(key, getCopyAction());
		}
		//--- paste
		key = TransferHandler.getPasteAction().getValue(Action.NAME);
		if (amap.get(key) != null) {
			amap.put(key, getPasteAction());
		}
	}

	/**
	 * 編集機能のキーストロークによるアクションハンドラ。
	 * カット、コピー、ペースト、削除の処理を行う。
	 * @param commandKey	操作の種別を表すコマンド名
	 */
	protected void onEditActionPerformed(String commandKey) {
		// 編集中の場合は、編集をキャンセル
		if (isEditing()) {
			cancelEditing();
		}
		
		// 処理方法はツリーの実装に依存するため、
		// デフォルトでは何も処理を行わない
	}
	
	@Override
	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
		/*
		if (MenuItemResource.getEditCopyShortcutKeyStroke().equals(ks)) {
			StringBuilder sb = new StringBuilder();
			sb.append("[Debug] DnDTree#processKeyBinding");
			sb.append("\n  - <KeyStroke> " + ks.toString());
			sb.append("\n  - <KeyEvent>  " + e.toString());
			sb.append("\n  - <condition> " + condition);
			sb.append("\n  - <pressed>   " + pressed);
			System.err.println(sb.toString());
		}
		/**/
		if (condition == WHEN_FOCUSED) {
			// (注) JTree の場合、UI の InputMap が存在するのは WHEN_FOCUSED になっている
			//--- 基本的なJComponent.processKeyBinding を処理する。
			//--- このコンポーネントがキーバインディングを処理してしまい、
			//--- メニューアクセラレーターが有効とならない問題を回避するため、
			//--- 独自の処理によりコンポーネントのキーバインディングを処理してしまう。
			//--- getInputMap(int,boolean) や getActionMap(boolean) は、同一パッケージのみのアクセスとなっており、
			//--- 同様の操作を行うためのインタフェースは用意されていないので、create されても仕方がない
			InputMap imap = getInputMap(condition);
			ActionMap amap = getActionMap();
			if (imap != null && amap != null && isEnabled()) {
				Object binding = imap.get(ks);
				Action action = (binding == null) ? null : amap.get(binding);
				//--- Test for default Input map
				/*
				System.err.println("@@@ DnDTree#processKeyBinding - show all default key stroke mapping in InputMap");
				{
					System.err.println("  - <binding> " + String.valueOf(binding) + " / <action> " + String.valueOf(action));
					System.err.println("  - <KeyStroke> " + ks + " / <KeyEvent> " + e.toString());
					java.util.TreeMap<String, String> km = new java.util.TreeMap<String,String>();
					KeyStroke[] keys = imap.allKeys();
					for (KeyStroke mks : keys) {
						Object name = imap.get(mks);
						Action maction = amap.get(name);
						String str = "  - KS[" + mks + "] / Key[" + String.valueOf(name) + "] / Action[" + String.valueOf(maction) + "]";
						km.put(mks.toString(), str);
					}
					for (java.util.Map.Entry<String, String> entry : km.entrySet()) {
						System.err.println(entry.getValue());
					}
				}
				System.err.println("@@@ DnDTree#processKeyBinding - End of show all default key stroke mapping.");
				/**/
				//--- End of test for default input map
				if (action instanceof MenuAcceleratorActionHandler) {
					//--- この場合、キーストロークはメニューアクセラレータとして
					//--- 登録されているため、このクラスインスタンスでは処理しない
					return false;
				}
				else if (action != null) {
					boolean isProcessed = SwingUtilities.notifyAction(action, ks, e, this, e.getModifiers());
					if (isProcessed) {
						//--- ここでキーストロークが処理された場合は、以降の処理は必要ない
						//--- JTree#processKeyBinding の実装の通り
						return isProcessed;
					}
				}
			}
		}

		// このコンポーネントの標準のキーストローク処理
		return super.processKeyBinding(ks, e, condition, pressed);
	}

	//------------------------------------------------------------
	// Tree Drag & Drop events
	//------------------------------------------------------------

	/**
	 * ドラッグ時のカーソル位置にあるツリーノードの自動展開が行われる直前に、
	 * <em>path</em> に指定されたツリーパスの展開を問い合わせるために呼び出される。
	 * このメソッドは、カーソル位置にあるツリーノードが葉ノードではなく、展開されて
	 * いない場合にのみ呼び出される。
	 * @param p		マウスカーソル位置
	 * @param path	対象のノードへのツリーパス
	 * @return	このノードの展開を許可する場合は <tt>true</tt>、許可しない場合は <tt>false</tt> を返す。
	 */
	protected boolean acceptAutoExpandTreePath(Point p, TreePath path) {
		return isAutoExpandWhenDragOverEnabled();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * <code>DnDTree</code> 専用のレンダラー。
	 * 
	 * @version 1.14	2009/12/09
	 * @since 1.14
	 */
	static protected class DnDTreeCellRenderer extends DefaultTreeCellRenderer
	{
		public Component getTreeCellRendererComponent(
				JTree tree,
				Object value,
				boolean sel,
				boolean expanded,
				boolean leaf, int row,
				boolean hasFocus)
		{
			// DnDTree 専用レンダリング
			Icon targetIcon = null;
			String tooltip = null;
			if (tree instanceof DnDTree) {
				DnDTree dndTree = (DnDTree)tree;
				
				// ドラッグオーバーノードの強調表示
				if (value!=null && dndTree.getDragOverTreeObject()==value) {
					sel = true;
				}
				
				// 個別アイコン設定
				targetIcon = dndTree.convertValueToIcon(value, sel, expanded, leaf, row, hasFocus);
				
				// ツールチップ
				tooltip = dndTree.getToolTipText(value, sel, expanded, leaf, row, hasFocus);
			}
			
			// default
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			
			// アイコン設定
			if (targetIcon != null) {
				if (!tree.isEnabled()) {
					setDisabledIcon(targetIcon);
				} else {
					setIcon(targetIcon);
				}
			}
			
			// ツールチップ設定
			setToolTipText(tooltip);
			
			// 完了
			return this;
		}
	}
	
	/**
	 * このツリーの編集アクションハンドラ。
	 * カット、コピー、ペースト、削除のキーストロークのトリガによって
	 * 実行される。
	 * 
	 * @version 1.14	2009/12/09
	 * @since 1.14
	 */
	static protected class EditActionHandler extends AbstractAction {
		public EditActionHandler(String commandKey, String name) {
			super(name);
			putValue(ACTION_COMMAND_KEY, commandKey);
		}
		
		public void actionPerformed(ActionEvent ae) {
//			AppLogger.debug("DnDTree.EditActionHandler#actionPerformed(" + ae.toString() + ")");
			Object srcComp = ae.getSource();
			if (srcComp instanceof DnDTree) {
				((DnDTree)srcComp).onEditActionPerformed(ae.getActionCommand());
			}
		}
	}

	/**
	 * メニューアクセラレータキー用のアクションハンドラ。
	 * このクラスは、JTreeのキーストロークマッピング(InputMap)を変更するために
	 * インスタンス化され、アクションマップでこのインスタンスを取得した場合は、
	 * <code>JTree#processKeyBinding()</code> を呼び出さないように処理する。
	 * これは、JTree標準のキーストロークマッピングに処理されないためのもの。
	 * <p>このクラスはダミーインスタンスとなり、このクラスインスタンスが
	 * 何らかのアクションを実行することはない。
	 * 
	 * @version 1.14	2009/12/09
	 * @since 1.14
	 */
	static class MenuAcceleratorActionHandler extends AbstractAction {
		public MenuAcceleratorActionHandler() {
			super(MenuAccelerationActionName);
		}
		
		public void actionPerformed(ActionEvent ae) {}	// 処理しない
	}
	
	/**
	 * <code>DnDTree</code> 専用の <code>DropTargetListener</code>
	 * 
	 * @version 1.14	2009/12/09
	 * @since 1.14
	 */
	static protected class DnDTreeDropTargetListener extends DefaultTreeDropTargetListener
	{
		private boolean canImport;
		
		private boolean actionSupported(int action) {
			return (action & (DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK)) != DnDConstants.ACTION_NONE;
		}
		
		public DnDTreeDropTargetListener() {
			super();
		}
		
		public DnDTreeDropTargetListener(boolean enableAutoExpand) {
			super(enableAutoExpand);
		}

		@Override
		protected boolean acceptExpandTreePath(JTree tree, Point p, TreePath path) {
			if (tree instanceof DnDTree) {
				DnDTree dndTree = (DnDTree)tree;
				return dndTree.acceptAutoExpandTreePath(p, path);
			} else {
				return super.acceptExpandTreePath(tree, p, path);
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			super.dragEnter(dtde);
			
			JComponent component = getComponent(dtde);
			if (component instanceof DnDTree) {
				((DnDTree)component).setDnDTargetEntered(true);
			}

			DataFlavor[] flavors = dtde.getCurrentDataFlavors();
			TransferHandler th = component.getTransferHandler();
			if (th != null && th.canImport(component, flavors)) {
				canImport = true;
			} else {
				canImport = false;
			}
			
			int dropAction = dtde.getDropAction();
			if (canImport && actionSupported(dropAction)) {
				dtde.acceptDrag(dropAction);
			} else {
				dtde.rejectDrag();
			}
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			super.dragExit(dte);
			JComponent component = getComponent(dte);
			if (component instanceof DnDTree) {
				DnDTree dndTree = (DnDTree)component;
				dndTree.setDnDTargetEntered(false);
				dndTree.setDragOverPath(null);
			}
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			super.dragOver(dtde);
			
			int dropAction;
			JComponent component = getComponent(dtde);
			if (component instanceof DnDTree) {
				DnDTree dndTree = (DnDTree)component;
				TreePath path = getTreePathForLocation(dndTree, dtde.getLocation());
				dndTree.setDragOverPath(path);	// update Drag over tree path
				
				// accept drop action
				IDnDTreeHandler treeHandler = dndTree.getTreeHandler();
				if (canImport && treeHandler != null) {
					dropAction = treeHandler.acceptTreeDragOverDropAction(dndTree, dtde, path);
				} else {
					dropAction = dtde.getDropAction();
				}
			}
			else {
				dropAction = dtde.getDropAction();
			}
			
			if (canImport && actionSupported(dropAction)) {
				dtde.acceptDrag(dropAction);
			} else {
				dtde.rejectDrag();
			}
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			super.drop(dtde);
			
			int dropAction = dtde.getDropAction();
			if (!canImport || !actionSupported(dropAction)) {
				dtde.rejectDrop();
				return;
			}
			
			JComponent component = getComponent(dtde);
			if (component instanceof DnDTree) {
				DnDTree dndTree = (DnDTree)component;
				IDnDTreeHandler treeHandler = dndTree.getTreeHandler();
				if (treeHandler != null) {
					dtde.acceptDrop(dropAction);
					
					try {
						Transferable t = dtde.getTransferable();
						dtde.dropComplete(treeHandler.dropTransferData(dndTree, t, dropAction));
					} catch (RuntimeException ex) {
						dtde.dropComplete(false);
					}
				} else {
					TransferHandler th = dndTree.getTransferHandler();
					if (th != null) {
						dtde.acceptDrop(dropAction);
						
						try {
							Transferable t = dtde.getTransferable();
							dtde.dropComplete(th.importData(component, t));
						} catch (RuntimeException ex) {
							dtde.dropComplete(false);
						}
					} else {
						dtde.rejectDrop();
					}
				}
				
				dndTree.setDnDTargetEntered(false);
				dndTree.setDragOverPath(null);
			}
			else {
				TransferHandler th = component.getTransferHandler();
				if (th != null) {
					dtde.acceptDrop(dropAction);
					
					try {
						Transferable t = dtde.getTransferable();
						dtde.dropComplete(th.importData(component, t));
					} catch (RuntimeException ex) {
						dtde.dropComplete(false);
					}
				} else {
					dtde.rejectDrop();
				}
			}
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {
			super.dropActionChanged(dtde);
			
			int dropAction = dtde.getDropAction();
			if (canImport && actionSupported(dropAction)) {
				dtde.acceptDrag(dropAction);
			} else {
				dtde.rejectDrag();
			}
		}
	}

	/**
	 * <code>DnDTree</code> 専用の <code>DragSourceListener</code>
	 * 
	 * @version 1.14	2009/12/09
	 * @since 1.14
	 */
	static protected class DnDTreeDragSourceHandler extends DragSourceAdapter
	{
		@Override
		public void dragEnter(DragSourceDragEvent dsde) {
			Component srcComp = dsde.getDragSourceContext().getComponent();
			if (srcComp instanceof DnDTree) {
				DnDTree srcTree = (DnDTree)srcComp;
				srcTree.setDnDLocalDragging(srcTree.isDnDTargetEntered());
			}
		}

		@Override
		public void dragExit(DragSourceEvent dse) {
			Component srcComp = dse.getDragSourceContext().getComponent();
			if (srcComp instanceof DnDTree) {
				((DnDTree)srcComp).setDnDLocalDragging(false);
			}
		}

		@Override
		public void dragOver(DragSourceDragEvent dsde) {
			Component srcComp = dsde.getDragSourceContext().getComponent();
			if (srcComp instanceof DnDTree) {
				DnDTree srcTree = (DnDTree)srcComp;
				srcTree.setDnDLocalDragging(srcTree.isDnDTargetEntered());
			}
		}

		@Override
		public void dragDropEnd(DragSourceDropEvent dsde) {
			Component srcComp = dsde.getDragSourceContext().getComponent();
			if (srcComp instanceof DnDTree) {
				((DnDTree)srcComp).setDnDLocalDragging(false);
			}
		}
	}
	
	/**
	 * <code>DnDTree</code> 専用の <code>TransferHandler</code>
	 * 
	 * @version 1.14	2009/12/09
	 * @since 1.14
	 */
	static protected class DnDTreeTransferHandler extends TransferHandler
	{
		private final DnDTree	_tree;
		
		public DnDTreeTransferHandler(DnDTree tree) {
			super();
			if (tree == null)
				throw new IllegalArgumentException("tree argument is null!");
			this._tree = tree;
		}

		@Override
		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			IDnDTreeHandler handler = _tree.getTreeHandler();
			if (handler != null) {
				if (_tree != comp)
					throw new AssertionError("component != DnDTree");
				if (handler.canTransferImport(_tree, transferFlavors)) {
					return true;
				}
			}
			// for Default
			return super.canImport(comp, transferFlavors);
		}

		@Override
		protected Transferable createTransferable(JComponent comp) {
			IDnDTreeHandler handler = _tree.getTreeHandler();
			if (handler != null) {
				if (_tree != comp)
					throw new AssertionError("component != DnDTree");
				Transferable t = handler.createTransferable(_tree);
				if (t != null) {
					return t;
				}
			}
			// for Default
			return super.createTransferable(comp);
		}

		@Override
		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
			// << ドラッグソースにリスナーを追加 >>
			// この方法は、DragSource#getDefaultDragSource() が静的な
			// インスタンスを返すことで実現している。
			// この静的な DragSource インスタンスに対し、このツリーコンポーネント
			// 専用の DragSourceListener を設定し、そこでこのコンポーネントのみ
			// でのドラッグ＆ドロップかを判定している。
			// 
			DragSource dndSource = DragSource.getDefaultDragSource();
			if (dndSource != null) {
				boolean alreadyExists = false;
				DragSourceListener[] listeners = dndSource.getDragSourceListeners();
				if (listeners != null && listeners.length > 0) {
					for (DragSourceListener dsl : listeners) {
						if (dsl == dndTreeDragSourceHandler) {
							alreadyExists = true;
							break;
						}
					}
				}
				if (!alreadyExists) {
					dndSource.addDragSourceListener(dndTreeDragSourceHandler);
				}
			}

			// 基本の処理
			super.exportAsDrag(comp, e, action);
		}

		@Override
		protected void exportDone(JComponent source, Transferable data, int action) {
			// for Default
			super.exportDone(source, data, action);
		}

		@Override
		public int getSourceActions(JComponent c) {
			IDnDTreeHandler handler = _tree.getTreeHandler();
			if (handler != null) {
				if (_tree != c)
					throw new AssertionError("component != DnDTree");
				return handler.getTransferSourceAction(_tree);
			} else {
				return super.getSourceActions(c);
			}
		}

		@Override
		public Icon getVisualRepresentation(Transferable t) {
			IDnDTreeHandler handler = _tree.getTreeHandler();
			if (handler != null) {
				return handler.getTransferVisualRepresentation(_tree, t);
			} else {
				return super.getVisualRepresentation(t);
			}
		}

		@Override
		public boolean importData(JComponent comp, Transferable t) {
			IDnDTreeHandler handler = _tree.getTreeHandler();
			if (handler != null) {
				if (_tree != comp)
					throw new AssertionError("comp != DnDTree");
				if (handler.importTransferData(_tree, t)) {
					return true;
				}
			}
			// for Default
			return super.importData(comp, t);
		}
	}
}
