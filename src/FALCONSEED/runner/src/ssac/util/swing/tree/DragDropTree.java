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
 * @(#)DragDropTree.java	1.20	2012/03/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.tree;

//import java.awt.Component;
//import java.awt.Point;
//import java.awt.Rectangle;
//import java.awt.datatransfer.DataFlavor;
//import java.awt.datatransfer.StringSelection;
//import java.awt.datatransfer.Transferable;
//import java.awt.dnd.DnDConstants;
//import java.awt.dnd.DragGestureEvent;
//import java.awt.dnd.DragGestureListener;
//import java.awt.dnd.DragGestureRecognizer;
//import java.awt.dnd.DragSource;
//import java.awt.dnd.DragSourceAdapter;
//import java.awt.dnd.DragSourceDragEvent;
//import java.awt.dnd.DragSourceDropEvent;
//import java.awt.dnd.DragSourceEvent;
//import java.awt.dnd.DragSourceListener;
//import java.awt.dnd.DragSourceMotionListener;
//import java.awt.dnd.DropTarget;
//import java.awt.dnd.DropTargetDragEvent;
//import java.awt.dnd.DropTargetDropEvent;
//import java.awt.dnd.DropTargetEvent;
//import java.awt.event.InputEvent;
//import java.io.File;
//import java.util.Hashtable;
//import java.util.Vector;
//
//import javax.swing.Icon;
//import javax.swing.JComponent;
//import javax.swing.JTree;
//import javax.swing.TransferHandler;
//import javax.swing.tree.TreeModel;
//import javax.swing.tree.TreeNode;
//import javax.swing.tree.TreePath;
//
//import ssac.falconseed.data.swing.DefaultFileTransferable;
//import ssac.util.Objects;
//import ssac.util.logging.AppLogger;

/**
 * ドラッグ＆ドロップに対応したツリーコンポーネント。
 * 
 * @version 1.20	2012/03/22
 * @since 1.20
 */
public class DragDropTree //extends ExTree implements DragGestureListener
{
//	//------------------------------------------------------------
//	// Constants
//	//------------------------------------------------------------
//
//	//------------------------------------------------------------
//	// Fields
//	//------------------------------------------------------------
//	
//	/** ドラッグ＆ドロップ用のツリーハンドラ **/
//	private IDragDropTreeHandler	_dndHandler	= null;
//	/** ドラッグ中のカーソル位置に存在するツリーノードのパス **/
//	private TreePath _dragOverPath = null;
//	/** ドラッグ時のツリー自動展開を有効にするフラグ **/
//	private boolean _autoExpandWhenDragOver = true;
//	/** このコンポーネントのドロップターゲットサイトでドラッグ中であることを示すフラグ **/
//	private boolean _dndTargetEntered = false;
//	/** ドラッグ中の操作において、ドラッグソースとドロップターゲットがどちらもこのコンポーネントであることを示すフラグ **/
//	private boolean _dndLocalDragging = false;
//
//	/** このコンポーネント標準の <code>DefaultTreeDropTargetListener</code> **/
//	transient private DefaultTreeDragSourceHandler	_hDragSourceHandler = new DefaultTreeDragSourceHandler();
//	transient private DefaultTreeDropTargetHandler	_hDropTargetHandler;
//	
//	transient private Point		_startDragPoint;
//	transient private DragSource	_dragSource;
//	transient private DropTarget	_dropTarget;
//
//	//------------------------------------------------------------
//	// Constructions
//	//------------------------------------------------------------
//
//	public DragDropTree() {
//		super();
//		constructDragDropTree();
//	}
//
//	public DragDropTree(Hashtable<?, ?> value) {
//		super(value);
//		constructDragDropTree();
//	}
//
//	public DragDropTree(Object[] value) {
//		super(value);
//		constructDragDropTree();
//	}
//
//	public DragDropTree(TreeModel newModel) {
//		super(newModel);
//		constructDragDropTree();
//	}
//
//	public DragDropTree(TreeNode root, boolean asksAllowsChildren) {
//		super(root, asksAllowsChildren);
//		constructDragDropTree();
//	}
//
//	public DragDropTree(TreeNode root) {
//		super(root);
//		constructDragDropTree();
//	}
//
//	public DragDropTree(Vector<?> value) {
//		super(value);
//		constructDragDropTree();
//	}
//	
//	protected void constructDragDropTree() {
//		// setup Drag & Drop
//		setDragEnabled(true);
//		_dragSource = new DragSource();
//		DragGestureRecognizer dgr = _dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
//		
//		setTransferHandler(new TransferHandler(){
//			@Override
//			public int getSourceActions(JComponent c) {
//				AppLogger.debug(getClass().getName() + "#getSouceAction()");
//				//return super.getSourceActions(c);
//				return COPY;
//			}
//		});
//		
//		// setup Drop target
//		DefaultTreeDropTargetHandler handler = getDefaultTreeDropTargetHandler();
//		if (handler != null) {
//			_dropTarget = new DropTarget(this, handler);
//		}
//	}
//
//	//------------------------------------------------------------
//	// Public interfaces
//	//------------------------------------------------------------
//
//	/**
//	 * このコンポーネントのドラッグ＆ドロップに対応するハンドラを返す。
//	 * @return 設定されている <code>IDragDropTreeHandler</code> を返す。
//	 * 			設定されていない場合は <tt>null</tt> を返す。
//	 */
//	public IDragDropTreeHandler getDragDropTreeHandler() {
//		return _dndHandler;
//	}
//
//	/**
//	 * このコンポーネントに、ドラッグ＆ドロップに対応するハンドラを設定する。
//	 * @param newHandler	新たに設定する <code>IDragDropTreeHandler</code> オブジェクト。
//	 * 						<tt>null</tt> を指定した場合は、既存のハンドラが破棄される。
//	 */
//	public void setDragDropTreeHandler(IDragDropTreeHandler newHandler) {
//		_dndHandler = newHandler;
//		// 更新処理が必要なら記述
//	}
//
//	/**
//	 * ドラッグ中のカーソルが、子を持つノード上にあるときに、自動的に展開するかどうかを判定する。
//	 * @return	自動的に展開するときは <tt>true</tt>、そうでない場合は <tt>false</t>
//	 */
//	public boolean isAutoExpandWhenDragOverEnabled() {
//		return _autoExpandWhenDragOver;
//	}
//
//	/**
//	 * ドラッグ中のカーソルが、子を持つノード上にあるときに、自動的に展開するかどうかを設定する。
//	 * @param enable	自動的に展開する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
//	 */
//	public void setAutoExpandWhenDragOverEnabled(boolean enable) {
//		_autoExpandWhenDragOver = enable;
//		/*
//		DefaultTreeDropTargetListener listener = getDefaultTreeDropTargetListener();
//		if (listener != null) {
//			listener.setAutoExpandEnabled(enable);
//		}
//		*/
//	}
//
//	/**
//	 * このコンポーネントのドロップターゲットサイトでドラッグ中の場合に <tt>true</tt> を返す。
//	 */
//	public boolean isDragTargetEntered() {
//		return _dndTargetEntered;
//	}
//
//	/**
//	 * ドラッグ中の操作において、ドラッグソースとドロップターゲットが
//	 * どちらもこのコンポーネントであれば <tt>true</tt> を返す。
//	 */
//	public boolean isLocalDragging() {
//		return _dndLocalDragging;
//	}
//
//	/**
//	 * ドラッグ中のカーソル位置に存在するツリーノードへのパスを返す。
//	 * カーソル位置にノードが存在しない場合や、ドラッグ中ではない場合には <tt>null</tt> を返す。
//	 */
//	public TreePath getDragOverPath() {
//		return _dragOverPath;
//	}
//
//	/**
//	 * ドラッグ中のカーソル位置に存在するツリーノードを返す。
//	 * カーソル位置にノードが存在しない場合や、ドラッグ中ではない場合には <tt>null</tt> を返す。
//	 */
//	public Object getDragOverTreeObject() {
//		if (_dragOverPath != null)
//			return _dragOverPath.getLastPathComponent();
//		else
//			return null;
//	}
//
//	//------------------------------------------------------------
//	// Internal Event handler
//	//------------------------------------------------------------
//
//	/**
//	 * 指定された値が選択された状態かどうかを判定する。
//	 * このメソッドは標準のレンダラから呼び出され、テキストやアイコンの描画前に選択状態を変更することができる。
//	 * @param value		対象の値
//	 * @param selected	ノードが選択されている場合は <tt>true</tt>
//	 * @param expanded	ノードが展開されている場合は <tt>true</tt>
//	 * @param leaf		ノードが葉ノードの場合は <tt>true</tt>
//	 * @param row		ノードの表示行を指定する <code>int</code> 値。0 は最初の行
//	 * @param hasFocus	ノードがフォーカスを持つ場合は <tt>true</tt>
//	 * @return	選択された状態とする場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
//	 */
//	@Override
//	protected boolean isValueSelectedOnRendering(Object value, boolean selected, boolean expanded,
//													boolean leaf, int row, boolean hasFocus)
//	{
//		if (value != null && getDragOverTreeObject()==value) {
//			// ドラッグオーバーノードの強調表示
//			selected = true;
//		}
//		return selected;
//	}
//
//	//------------------------------------------------------------
//	// Internal methods
//	//------------------------------------------------------------
//
//	/**
//	 * このコンポーネントのドロップターゲットサイトでドラッグ中であることを
//	 * 示すフラグを設定する。
//	 * @param toEntered	このコンポーネントでドラッグ中であれば <tt>true</tt>、
//	 * 					そうでない場合は <tt>false</tt> を指定する。
//	 */
//	protected void setDragTargetEntered(boolean toEntered) {
//		_dndTargetEntered = toEntered;
//	}
//
//	/**
//	 * ドラッグ中の操作において、ドラッグソースとドロップターゲットが
//	 * どちらもこのコンポーネントであることを示すフラグを設定する。
//	 * @param toDragging	このコンポーネントのみでドラッグ＆ドロップ操作が
//	 * 						行われている場合は <tt>true</tt>、
//	 * 						そうでない場合は <tt>false</tt> を指定する。
//	 */
//	protected void setLocalDragging(boolean toDragging) {
//		_dndLocalDragging = toDragging;
//	}
//
//	/**
//	 * ドラッグ中のカーソル位置に存在するツリーノードを設定する。
//	 * @param path	ドラッグ中のカーソル位置とするツリーパス
//	 */
//	protected void setDragOverPath(TreePath path) {
//		if (!Objects.isEqual(_dragOverPath, path)) {
//			TreePath oldPath = _dragOverPath;
//			_dragOverPath = path;
//			if (path != null) {
//				Rectangle rBounds = getPathBounds(path);
//				if (oldPath != null) {
//					rBounds = rBounds.union(getPathBounds(oldPath));
//				}
//				repaint(rBounds);
//			}
//			else if (oldPath != null) {
//				Rectangle rBounds = getPathBounds(oldPath);
//				repaint(rBounds);
//			}
//		}
//	}
//	
//	protected DefaultTreeDropTargetHandler createDefaultTreeDropTargetHandler() {
//		return new DefaultTreeDropTargetHandler(true);
//	}
//	
//	protected DefaultTreeDropTargetHandler getDefaultTreeDropTargetHandler() {
//		if (_hDropTargetHandler == null) {
//			_hDropTargetHandler = createDefaultTreeDropTargetHandler();
//		}
//		return _hDropTargetHandler;
//	}
//
//	//------------------------------------------------------------
//	// Tree Drag & Drop events
//	//------------------------------------------------------------
//
//	/**
//	 * ドラッグ時のカーソル位置にあるツリーノードの自動展開が行われる直前に、
//	 * <em>path</em> に指定されたツリーパスの展開を問い合わせるために呼び出される。
//	 * このメソッドは、カーソル位置にあるツリーノードが葉ノードではなく、展開されて
//	 * いない場合にのみ呼び出される。
//	 * @param p		マウスカーソル位置
//	 * @param path	対象のノードへのツリーパス
//	 * @return	このノードの展開を許可する場合は <tt>true</tt>、許可しない場合は <tt>false</tt> を返す。
//	 */
//	protected boolean acceptAutoExpandTreePath(Point p, TreePath path) {
//		return isAutoExpandWhenDragOverEnabled();
//	}
//	
//	public void dragGestureRecognized(DragGestureEvent dge) {
//		AppLogger.debug("dragGestureRecognized");
//
//		Point clickPoint = dge.getDragOrigin();
//		TreePath path = getPathForLocation(clickPoint.x, clickPoint.y);
//		if (path == null) {
//			AppLogger.debug("not on a node");
//			return;
//		}
//		
//		_startDragPoint = clickPoint;
//		IDragDropTreeHandler handler = getDragDropTreeHandler();
////		if (handler != null) {
////			Transferable t = handler.createTransferable(DragDropTree.this);
////			if (t != null) {
////				_dragSource.startDrag(dge, null, t, _hDragSourceHandler);
////			}
////		}
//		//--- for test
//		Transferable t = new DefaultFileTransferable(new File("D:\\PieCake\\Projects\\TokyoInstitute\\SOARS\\EclipseWS_3.4\\FALCONSEED201202\\testdata\\dragdroptest.txt"));
//		_dragSource.startDrag(dge, null, t, _hDragSourceHandler);
//	}
//
//	//------------------------------------------------------------
//	// Inner classes
//	//------------------------------------------------------------
//
//	/**
//	 * <code>DragDropTree</code> 専用の <code>DragSourceListener</code>
//	 * 
//	 * @version 1.20	2012/03/22
//	 * @since 1.20
//	 */
//	protected class DefaultTreeDragSourceHandler implements DragSourceListener, DragSourceMotionListener
//	{
//		public void dragEnter(DragSourceDragEvent dsde) {
//			AppLogger.debug(getClass().getName() + "#dragEnter : dsde=" + String.valueOf(dsde));
////			Component srcComp = dsde.getDragSourceContext().getComponent();
////			if (srcComp instanceof DnDTree) {
////				DnDTree srcTree = (DnDTree)srcComp;
////				srcTree.setDnDLocalDragging(srcTree.isDnDTargetEntered());
////			}
//		}
//
//		public void dragExit(DragSourceEvent dse) {
//			AppLogger.debug(getClass().getName() + "#dragExit : dse=" + String.valueOf(dse));
////			Component srcComp = dse.getDragSourceContext().getComponent();
////			if (srcComp instanceof DnDTree) {
////				((DnDTree)srcComp).setDnDLocalDragging(false);
////			}
//		}
//
//		public void dragOver(DragSourceDragEvent dsde) {
//			AppLogger.debug(getClass().getName() + "#dragOver : dsde=" + String.valueOf(dsde));
////			Component srcComp = dsde.getDragSourceContext().getComponent();
////			if (srcComp instanceof DnDTree) {
////				DnDTree srcTree = (DnDTree)srcComp;
////				srcTree.setDnDLocalDragging(srcTree.isDnDTargetEntered());
////			}
//		}
//
//		public void dragDropEnd(DragSourceDropEvent dsde) {
//			AppLogger.debug(getClass().getName() + "#dragDropEnd : dsde=" + String.valueOf(dsde));
////			Component srcComp = dsde.getDragSourceContext().getComponent();
////			if (srcComp instanceof DnDTree) {
////				((DnDTree)srcComp).setDnDLocalDragging(false);
////			}
//		}
//
//		public void dragMouseMoved(DragSourceDragEvent dsde) {
//			AppLogger.debug(getClass().getName() + "#dragMouseMoved : dsde=" + String.valueOf(dsde));
//			
//		}
//
//		public void dropActionChanged(DragSourceDragEvent dsde) {
//			AppLogger.debug(getClass().getName() + "#dropActionChanged : dsde=" + String.valueOf(dsde));
//			
//		}
//	}
//	
//	/**
//	 * <code>DragDropTree</code> 専用の <code>DropTargetListener</code>
//	 * 
//	 * @version 1.20	2012/03/22
//	 * @since 1.20
//	 */
//	protected class DefaultTreeDropTargetHandler extends DefaultTreeDropTargetListener
//	{
//		private boolean canImport;
//		
//		private boolean actionSupported(int action) {
//			return (action & (DnDConstants.ACTION_COPY_OR_MOVE | DnDConstants.ACTION_LINK)) != DnDConstants.ACTION_NONE;
//		}
//		
//		public DefaultTreeDropTargetHandler() {
//			super();
//		}
//		
//		public DefaultTreeDropTargetHandler(boolean enableAutoExpand) {
//			super(enableAutoExpand);
//		}
//
//		@Override
//		protected boolean acceptExpandTreePath(JTree tree, Point p, TreePath path) {
//			AppLogger.debug(getClass().getName() + "#acceptExpandTreePath");
//			if (tree instanceof DragDropTree) {
//				DragDropTree dndTree = (DragDropTree)tree;
//				return dndTree.acceptAutoExpandTreePath(p, path);
//			} else {
//				return super.acceptExpandTreePath(tree, p, path);
//			}
//		}
//
//		@Override
//		public void dragEnter(DropTargetDragEvent dtde) {
//			AppLogger.debug(getClass().getName() + "#dragEnter");
//			super.dragEnter(dtde);
//			
//			JComponent component = getComponent(dtde);
//			if (component instanceof DragDropTree) {
//				((DragDropTree)component).setDragTargetEntered(true);
//			}
//
//			DataFlavor[] flavors = dtde.getCurrentDataFlavors();
//			TransferHandler th = component.getTransferHandler();
//			if (th != null && th.canImport(component, flavors)) {
//				canImport = true;
//			} else {
//				canImport = false;
//			}
//			
//			int dropAction = dtde.getDropAction();
//			if (canImport && actionSupported(dropAction)) {
//				dtde.acceptDrag(dropAction);
//			} else {
//				dtde.rejectDrag();
//			}
//		}
//
//		@Override
//		public void dragExit(DropTargetEvent dte) {
//			AppLogger.debug(getClass().getName() + "#dragExit");
//			super.dragExit(dte);
//			JComponent component = getComponent(dte);
//			if (component instanceof DragDropTree) {
//				DragDropTree dndTree = (DragDropTree)component;
//				dndTree.setDragTargetEntered(false);
//				dndTree.setDragOverPath(null);
//			}
//		}
//
//		@Override
//		public void dragOver(DropTargetDragEvent dtde) {
//			AppLogger.debug(getClass().getName() + "#dragOver");
//			super.dragOver(dtde);
//			if (true) {
//				dtde.acceptDrag(DnDConstants.ACTION_COPY);
//				return;
//			}
//			
//			int dropAction;
//			JComponent component = getComponent(dtde);
//			if (component instanceof DragDropTree) {
//				DragDropTree dndTree = (DragDropTree)component;
//				TreePath path = getTreePathForLocation(dndTree, dtde.getLocation());
//				dndTree.setDragOverPath(path);	// update Drag over tree path
//				
//				// accept drop action
//				IDragDropTreeHandler treeHandler = dndTree.getDragDropTreeHandler();
//				if (canImport && treeHandler != null) {
//					dropAction = treeHandler.acceptTreeDragOverDropAction(dndTree, dtde, path);
//				} else {
//					dropAction = dtde.getDropAction();
//				}
//			}
//			else {
//				dropAction = dtde.getDropAction();
//			}
//			
//			if (canImport && actionSupported(dropAction)) {
//				dtde.acceptDrag(dropAction);
//			} else {
//				dtde.rejectDrag();
//			}
//		}
//
//		@Override
//		public void drop(DropTargetDropEvent dtde) {
//			AppLogger.debug(getClass().getName() + "#drop");
//			super.drop(dtde);
//			
//			int dropAction = dtde.getDropAction();
//			if (!canImport || !actionSupported(dropAction)) {
//				dtde.rejectDrop();
//				return;
//			}
//			
//			JComponent component = getComponent(dtde);
//			if (component instanceof DragDropTree) {
//				DragDropTree dndTree = (DragDropTree)component;
//				IDragDropTreeHandler treeHandler = dndTree.getDragDropTreeHandler();
//				if (treeHandler != null) {
//					dtde.acceptDrop(dropAction);
//					
//					try {
//						Transferable t = dtde.getTransferable();
//						dtde.dropComplete(treeHandler.dropTransferData(dndTree, t, dropAction));
//					} catch (RuntimeException ex) {
//						dtde.dropComplete(false);
//					}
//				} else {
//					TransferHandler th = dndTree.getTransferHandler();
//					if (th != null) {
//						dtde.acceptDrop(dropAction);
//						
//						try {
//							Transferable t = dtde.getTransferable();
//							dtde.dropComplete(th.importData(component, t));
//						} catch (RuntimeException ex) {
//							dtde.dropComplete(false);
//						}
//					} else {
//						dtde.rejectDrop();
//					}
//				}
//				
//				dndTree.setDragTargetEntered(false);
//				dndTree.setDragOverPath(null);
//			}
//			else {
//				TransferHandler th = component.getTransferHandler();
//				if (th != null) {
//					dtde.acceptDrop(dropAction);
//					
//					try {
//						Transferable t = dtde.getTransferable();
//						dtde.dropComplete(th.importData(component, t));
//					} catch (RuntimeException ex) {
//						dtde.dropComplete(false);
//					}
//				} else {
//					dtde.rejectDrop();
//				}
//			}
//		}
//
//		@Override
//		public void dropActionChanged(DropTargetDragEvent dtde) {
//			AppLogger.debug(getClass().getName() + "#dropActionChanged");
//			super.dropActionChanged(dtde);
//			
//			int dropAction = dtde.getDropAction();
//			if (canImport && actionSupported(dropAction)) {
//				dtde.acceptDrag(dropAction);
//			} else {
//				dtde.rejectDrag();
//			}
//		}
//	}
//	
////	/**
////	 * <code>DnDTree</code> 専用の <code>TransferHandler</code>
////	 * 
////	 * @version 1.14	2009/12/09
////	 * @since 1.14
////	 */
////	static protected class DnDTreeTransferHandler extends TransferHandler
////	{
////		private final DnDTree	_tree;
////		
////		public DnDTreeTransferHandler(DnDTree tree) {
////			super();
////			if (tree == null)
////				throw new IllegalArgumentException("tree argument is null!");
////			this._tree = tree;
////		}
////
////		@Override
////		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
////			IDnDTreeHandler handler = _tree.getTreeHandler();
////			if (handler != null) {
////				if (_tree != comp)
////					throw new AssertionError("component != DnDTree");
////				if (handler.canTransferImport(_tree, transferFlavors)) {
////					return true;
////				}
////			}
////			// for Default
////			return super.canImport(comp, transferFlavors);
////		}
////
////		@Override
////		protected Transferable createTransferable(JComponent comp) {
////			IDnDTreeHandler handler = _tree.getTreeHandler();
////			if (handler != null) {
////				if (_tree != comp)
////					throw new AssertionError("component != DnDTree");
////				Transferable t = handler.createTransferable(_tree);
////				if (t != null) {
////					return t;
////				}
////			}
////			// for Default
////			return super.createTransferable(comp);
////		}
////
////		@Override
////		public void exportAsDrag(JComponent comp, InputEvent e, int action) {
////			// << ドラッグソースにリスナーを追加 >>
////			// この方法は、DragSource#getDefaultDragSource() が静的な
////			// インスタンスを返すことで実現している。
////			// この静的な DragSource インスタンスに対し、このツリーコンポーネント
////			// 専用の DragSourceListener を設定し、そこでこのコンポーネントのみ
////			// でのドラッグ＆ドロップかを判定している。
////			// 
////			DragSource dndSource = DragSource.getDefaultDragSource();
////			if (dndSource != null) {
////				boolean alreadyExists = false;
////				DragSourceListener[] listeners = dndSource.getDragSourceListeners();
////				if (listeners != null && listeners.length > 0) {
////					for (DragSourceListener dsl : listeners) {
////						if (dsl == dndTreeDragSourceHandler) {
////							alreadyExists = true;
////							break;
////						}
////					}
////				}
////				if (!alreadyExists) {
////					dndSource.addDragSourceListener(dndTreeDragSourceHandler);
////				}
////			}
////
////			// 基本の処理
////			super.exportAsDrag(comp, e, action);
////		}
////
////		@Override
////		protected void exportDone(JComponent source, Transferable data, int action) {
////			// for Default
////			super.exportDone(source, data, action);
////		}
////
////		@Override
////		public int getSourceActions(JComponent c) {
////			IDnDTreeHandler handler = _tree.getTreeHandler();
////			if (handler != null) {
////				if (_tree != c)
////					throw new AssertionError("component != DnDTree");
////				return handler.getTransferSourceAction(_tree);
////			} else {
////				return super.getSourceActions(c);
////			}
////		}
////
////		@Override
////		public Icon getVisualRepresentation(Transferable t) {
////			IDnDTreeHandler handler = _tree.getTreeHandler();
////			if (handler != null) {
////				return handler.getTransferVisualRepresentation(_tree, t);
////			} else {
////				return super.getVisualRepresentation(t);
////			}
////		}
////
////		@Override
////		public boolean importData(JComponent comp, Transferable t) {
////			IDnDTreeHandler handler = _tree.getTreeHandler();
////			if (handler != null) {
////				if (_tree != comp)
////					throw new AssertionError("comp != DnDTree");
////				if (handler.importTransferData(_tree, t)) {
////					return true;
////				}
////			}
////			// for Default
////			return super.importData(comp, t);
////		}
////	}
}
