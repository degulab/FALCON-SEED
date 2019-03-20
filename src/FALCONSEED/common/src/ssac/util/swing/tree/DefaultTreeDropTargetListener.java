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
 * @(#)DefaultTreeDropTargetListener.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.tree;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.TreePath;

/**
 * ツリーコンポーネントの標準的な <code>DropTargetListener</code> の実装。
 * この実装では、自動スクロール、ならびに、枝ノードの展開機能を実装する。
 *
 * @since 1.14	2009/12/09
 */
public class DefaultTreeDropTargetListener
implements DropTargetListener, ActionListener
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final int AUTOSCROLL_INSET = 10;
	static private final int DEFAULT_EXPAND_DELAY = 500;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private Timer		_timer;
	private Point		_lastPosition;
	private Rectangle	_outer = new Rectangle();
	private Rectangle	_inner = new Rectangle();
	private int		_hysteresis = 10;
	private boolean	_canImport;
	
	private JComponent	_component;
	
	private boolean	_autoExpand;
	private boolean	_startAutoExpand;
	private long		_expandDelayTime;
	private long		_startExpandTime;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DefaultTreeDropTargetListener() {
		this(false);
	}
	
	public DefaultTreeDropTargetListener(boolean enableAutoExpand) {
		super();
		this._autoExpand = enableAutoExpand;
		this._expandDelayTime = DEFAULT_EXPAND_DELAY;
	}

	//------------------------------------------------------------
	// implements java.awt.event.ActionListener interfaces
	//------------------------------------------------------------
	
	public synchronized void actionPerformed(ActionEvent ae) {
		updateAutoscrollRegion(_component);
		//--- auto expand
		if (_autoExpand && _startAutoExpand && (_component instanceof JTree) && (System.currentTimeMillis()-_startExpandTime)>=getAutoExpandDelay()) {
			JTree tree = (JTree)_component;
			TreePath path = getTreePathForLocation(tree, _lastPosition);
			if (path != null && !tree.getModel().isLeaf(path.getLastPathComponent()) && !tree.isExpanded(path)) {
				if (acceptExpandTreePath(tree, _lastPosition, path)) {
					tree.expandPath(path);
				}
			}
			_startAutoExpand = false;
		}
		//--- auto scroll
		if (_outer.contains(_lastPosition) && !_inner.contains(_lastPosition)) {
			autoscroll(_component, _lastPosition);
		}
	}

	//------------------------------------------------------------
	// implements java.awt.dnd.DropTargetListener interfaces
	//------------------------------------------------------------
	
	public void dragEnter(DropTargetDragEvent dtde) {
		_component = getComponent(dtde);
		TransferHandler th = _component.getTransferHandler();
		_canImport = th.canImport(_component, dtde.getCurrentDataFlavors());
		if (_canImport) {
			saveComponentState(_component);
			_lastPosition = dtde.getLocation();
			updateAutoscrollRegion(_component);
			initPropertiesIfNecessary();
		}
	}
	
	public void dragOver(DropTargetDragEvent dtde) {
		if (_canImport) {
			Point p = dtde.getLocation();
			updateInsertionLocation(_component, p);
			
			// check autoscroll
			synchronized(this) {
				int offsetx = Math.abs(p.x - _lastPosition.x);
				int offsety = Math.abs(p.y - _lastPosition.y);
				if (_autoExpand) {
					if (offsetx > 0 || offsety > 0) {
						// no autoexpand
						_startAutoExpand = false;
					} else if (!_startAutoExpand) {
						// begin autoexpand delay
						_startAutoExpand = true;
						_startExpandTime = System.currentTimeMillis();
					}
				}
				if (Math.abs(p.x - _lastPosition.x) > _hysteresis || Math.abs(p.y - _lastPosition.y) > _hysteresis) {
					// no autoscroll
					if (_timer.isRunning())
						_timer.stop();
				} else {
					if (!_timer.isRunning())
						_timer.start();
				}
				_lastPosition = p;
			}
		}
	}
	
	public void dragExit(DropTargetEvent dte) {
		if (_canImport) {
			restoreComponentState(_component);
		}
		cleanup();
	}
	
	public void drop(DropTargetDropEvent dtde) {
		if (_canImport) {
			restoreComponentStateForDrop(_component);
		}
		cleanup();
	}
	
	public void dropActionChanged(DropTargetDragEvent dtde) {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ツリーの自動展開が有効な場合に <tt>true</tt> を返す。
	 */
	public boolean isAutoExpandEnabled() {
		return _autoExpand;
	}

	/**
	 * ツリーの自動展開を設定する。
	 * @param enable	自動展開を有効にする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を指定する。
	 */
	public void setAutoExpandEnabled(boolean enable) {
		_autoExpand = enable;
		_startAutoExpand = false;
	}

	/**
	 * ツリーの自動展開までの待ち時間を取得する。
	 * @return	ツリー自動展開までの待ち時間となるミリ秒単位の値
	 */
	public long getAutoExpandDelay() {
		return _expandDelayTime;
	}

	/**
	 * ツリーの自動展開までの待ち時間を設定する。
	 * @param delay	ミリ秒単位の待ち時間
	 * @throws IllegalArgumentException	<code>delay</code> が 0未満の場合
	 */
	public void setAutoExpandDelay(long delay) {
		if (delay < 0L)
			throw new IllegalArgumentException("Invalid delay : " + delay);
		_expandDelayTime = delay;
		_startAutoExpand = false;
	}

	//------------------------------------------------------------
	// for Override interfaces
	//------------------------------------------------------------

	/**
	 * カーソル位置のツリーパスを取得する。
	 * この標準の実装では、カーソル位置にもっとも近いノードへのパスが取得される。
	 * @param tree	対象のツリーコンポーネント
	 * @param p		カーソル位置
	 * @return	カーソル位置に存在するとみなされるノードへのツリーパスを返す。
	 * 			ノードが存在しない場合は <tt>null</tt> を返す。
	 */
	protected TreePath getTreePathForLocation(JTree tree, Point p) {
		TreePath path = null;
		TreeUI ui = tree.getUI();
		if (ui != null) {
			path = ui.getClosestPathForLocation(tree, p.x, p.y);
		} else {
			path = tree.getClosestPathForLocation(p.x, p.y);
		}
		return path;
	}

	/**
	 * ソースコンポーネントの状態を保存する際に呼び出される。
	 * このメソッドは、コンポーネントに割り当てられている <code>TransferHandler</code> が、
	 * 現在の <code>DataFlavor</code> のインポートを許可する場合のみ呼び出される。
	 * @param c	ドロップターゲットとなるコンポーネント
	 */
	protected void saveComponentState(JComponent c) {}

	/**
	 * ドラッグ終了時に、コンポーネントの状態を保存した状態に戻す際に呼び出される。
	 * このメソッドは、コンポーネントに割り当てられている <code>TransferHandler</code> が、
	 * 現在の <code>DataFlavor</code> のインポートを許可する場合のみ呼び出される。
	 * @param c	ドロップターゲットとなるコンポーネント
	 */
	protected void restoreComponentState(JComponent c) {}

	/**
	 * ドロップ時にコンポーネントの状態を保存した状態に戻す際に呼び出される。
	 * このメソッドは、コンポーネントに割り当てられている <code>TransferHandler</code> が、
	 * 現在の <code>DataFlavor</code> のインポートを許可する場合のみ呼び出される。
	 * @param c	ドロップターゲットとなるコンポーネント
	 */
	protected void restoreComponentStateForDrop(JComponent c) {}

	/**
	 * ドラッグ時のカーソル位置を更新する際に呼び出される。
	 * このメソッドは、コンポーネントに割り当てられている <code>TransferHandler</code> が、
	 * 現在の <code>DataFlavor</code> のインポートを許可する場合のみ呼び出される。
	 * 
	 * @param c	ドロップターゲットとなるコンポーネント
	 * @param p	マウスカーソルの位置
	 */
	protected void updateInsertionLocation(JComponent c, Point p) {}

	/**
	 * ドラッグ時のカーソル位置にあるツリーノードの自動展開が行われる直前に、
	 * <em>path</em> に指定されたツリーパスの展開を問い合わせるために呼び出される。
	 * このメソッドは、カーソル位置にあるツリーノードが葉ノードではなく、展開されて
	 * いない場合にのみ呼び出される。
	 * @param tree	対象のツリーコンポーネント
	 * @param p		マウスカーソル位置
	 * @param path	対象のノードへのツリーパス
	 * @return	このノードの展開を許可する場合は <tt>true</tt>、許可しない場合は <tt>false</tt> を返す。
	 */
	protected boolean acceptExpandTreePath(JTree tree, Point p, TreePath path) {
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected JComponent getComponent(DropTargetEvent dte) {
		DropTargetContext context = dte.getDropTargetContext();
		return (JComponent)context.getComponent();
	}
	
	void updateAutoscrollRegion(JComponent c) {
		// compute the outer
		Rectangle visible = c.getVisibleRect();
		_outer.setBounds(visible);
		
		// compute the insets
		Insets i = new Insets(0,0,0,0);
		if (c instanceof Scrollable) {
			int minSize = 2 * AUTOSCROLL_INSET;
			
			if (visible.width >= minSize) {
				i.left = i.right = AUTOSCROLL_INSET;
			}
			
			if (visible.height >= minSize) {
				i.top = i.bottom = AUTOSCROLL_INSET;
			}
		}
		
		// set the inner from the insets
		_inner.setBounds(visible.x + i.left,
				visible.y + i.top,
				visible.width - (i.left + i.right),
				visible.height - (i.top + i.bottom));
	}
	
	void autoscroll(JComponent c, Point pos) {
		if (c instanceof Scrollable) {
			Scrollable s = (Scrollable)c;
			
			if (pos.y < _inner.y) {
				// scroll upward
				int dy = s.getScrollableUnitIncrement(_outer, SwingConstants.VERTICAL, -1);
				Rectangle r = new Rectangle(_inner.x, _outer.y - dy, _inner.width, dy);
				c.scrollRectToVisible(r);
			}
			else if (pos.y > (_inner.y + _inner.height)) {
				// scroll downward
				int dy = s.getScrollableUnitIncrement(_outer, SwingConstants.VERTICAL, 1);
				Rectangle r = new Rectangle(_inner.x, _outer.y + _outer.height, _inner.width, dy);
				c.scrollRectToVisible(r);
			}
			
			if (pos.x < _inner.x) {
				// scroll left
				int dx = s.getScrollableUnitIncrement(_outer, SwingConstants.HORIZONTAL, -1);
				Rectangle r = new Rectangle(_outer.x - dx, _inner.y, dx, _inner.height);
				c.scrollRectToVisible(r);
			}
			else if (pos.x > (_inner.x + _inner.width)) {
				// scroll right
				int dx = s.getScrollableUnitIncrement(_outer, SwingConstants.HORIZONTAL, 1);
				Rectangle r = new Rectangle(_outer.x + _outer.width, _inner.y, dx, _inner.height);
				c.scrollRectToVisible(r);
			}
		}
	}
	
	private void initPropertiesIfNecessary() {
		if (_timer == null) {
			Toolkit t = Toolkit.getDefaultToolkit();
			Integer initial  = new Integer(100);
			Integer interval = new Integer(100);
			
			try {
				initial = (Integer)t.getDesktopProperty("DnD.Autoscroll.initialDelay");
			} catch (Exception ex) {
				// ignore
			}
			try {
				interval = (Integer)t.getDesktopProperty("DnD.Autoscroll.interval");
			} catch (Exception ex) {
				// ignore
			}
			_timer = new Timer(interval.intValue(), this);
			
			_timer.setCoalesce(true);
			_timer.setInitialDelay(initial.intValue());
			
			try {
				_hysteresis = ((Integer)t.getDesktopProperty("DnD.Autoscroll.cursorHysteresis")).intValue();
			} catch (Exception ex) {
				// ignore
			}
		}
	}
	
	private void cleanup() {
		if (_timer != null) {
			_timer.stop();
		}
		_component = null;
		_lastPosition = null;
	}
}
