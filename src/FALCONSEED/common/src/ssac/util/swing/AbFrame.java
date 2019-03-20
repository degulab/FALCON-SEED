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
 * @(#)AbFrame.java	2.00	2012/09/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;

/**
 * 最も基本的なフレームの基本実装。
 * このコンポーネントでは、コンポーネントならびにウィンドウのリスナーを登録し、
 * リスナーに対応するインタフェースを実装するためのフレームワークを提供する。
 * コンストラクタでは、{@link #setDefaultCloseOperation(int)} が {@link javax.swing.WindowConstants#DO_NOTHING_ON_CLOSE} を引数として呼び出され、
 * このクラス標準の <code>WindowListener</code> と <code>ComponentListener</code> が設定される。
 * このクラスのコンストラクタを呼び出した直後は、必ず {@link #initialComponent()} を呼び出すこと。
 * {@link #initialComponent()} の呼び出しによって、ダイアログ内のコンポーネントが初期化される。
 * </blockquote>
 * 
 * @version 2.00	2012/09/19
 * @since 2.00
 */
public abstract class AbFrame extends JFrame
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private ComponentListener		_listenerComponentEvents;
	private WindowListener			_listenerWindowEvents;
	private WindowStateListener	_listenerWindowStateEvents;
	private WindowFocusListener	_listenerWindowFocusEvents;

	/** 直前のノーマルウィンドウの境界 **/
	private Rectangle				_lastNormalBounds;
	/** 最小サイズを維持するフラグ **/
	private boolean				_flgKeepMinimumSize = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 初期状態が不可視である、新しい </code>Frame</code> を構築します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JFrame#JFrame()
	 */
	public AbFrame() throws HeadlessException {
		super();
		constructAbFrame();
	}

	/**
	 * 指定されたタイトルで、初期状態で不可視の新しい <code>Frame</code> を構築します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param title	フレームのタイトル
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JFrame#JFrame(String)
	 */
	public AbFrame(String title) throws HeadlessException {
		super(title);
		constructAbFrame();
	}

	/**
	 * <code>Frame</code> を、画面デバイスの指定された <code>GraphicsConfiguration</code> にタイトルなしで作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param gc	新しい <code>Frame</code> の構築に使用する <code>GraphicsConfiguration</code>。
	 * 				<em>gc</em> が <tt>null</tt> の場合、システムのデフォルトの <code>GraphicsConfiguration</code> が使用される
	 * @throws IllegalArgumentException	<em>gc</em> がスクリーンデバイスのものではない場合。
	 * 										この例外は <code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合に必ずスローされる
	 * @see javax.swing.JFrame#JFrame(GraphicsConfiguration)
	 */
	public AbFrame(GraphicsConfiguration gc) {
		super(gc);
		constructAbFrame();
	}

	/**
	 * <code>Frame</code> を、指定されたタイトルで、画面デバイスの指定された <code>GraphicsConfiguration</code> に作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param title	フレームのボーダーに表示されるタイトル。<tt>null</tt> 値は空の文字列 &quot;&quot; として扱われる
	 * @param gc	新しい <code>Frame</code> の構築に使用する <code>GraphicsConfiguration</code>。
	 * 				<em>gc</em> が <tt>null</tt> の場合、システムのデフォルトの <code>GraphicsConfiguration</code> が使用される
	 * @throws IllegalArgumentException	<em>gc</em> がスクリーンデバイスのものではない場合。
	 * 										この例外は <code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合に必ずスローされる
	 * @see javax.swing.JFrame#JFrame(String, GraphicsConfiguration)
	 */
	public AbFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		constructAbFrame();
	}
	
	private final void constructAbFrame() {
		_lastNormalBounds = new Rectangle();
		getBounds(_lastNormalBounds);
		
		// setup basic window events
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addComponentListener(getDefaultComponentListener());
		addWindowListener(getDefaultWindowListener());
	}

	/**
	 * ウィンドウの初期化を行う。
	 * このメソッドは、コンストラクタ呼び出し直後に、最初に呼び出す。
	 * @return	初期化に成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean initialComponent() {
		if (!preInitialComponent())
			return false;
		
		if (!onInitialComponent())
			return false;
		
		if (!postInitialComponent())
			return false;
		
		return true;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ノーマルウィンドウでの最新の左上位置を返す。
	 * @return	ノーマルウィンドウでの最新左上位置
	 */
	public Point getLastNormalLocation() {
		return _lastNormalBounds.getLocation();
	}

	/**
	 * ノーマルウィンドウでの最新のサイズを返す。
	 * @return	ノーマルウィンドウでの最新サイズ
	 */
	public Dimension getLastNormalSize() {
		return _lastNormalBounds.getSize();
	}

	/**
	 * ノーマルウィンドウでの最新の境界を返す。
	 * @return	ノーマルウィンドウでの最新境界
	 */
	public Rectangle getLastNormalBounds() {
		return new Rectangle(_lastNormalBounds);
	}

	/**
	 * ウィンドウの最小サイズを維持する場合に <tt>true</tt> を返す。
	 */
	public boolean isKeepMinimumSize() {
		return _flgKeepMinimumSize;
	}

	/**
	 * ウィンドウの最小サイズを維持するかどうかを設定する。
	 * @param toAllow	最小サイズを維持する場合は <tt>true</tt>、維持しない場合は <tt>false</tt>
	 */
	public void setKeepMinimumSize(boolean toAllow) {
		if (_flgKeepMinimumSize != toAllow) {
			_flgKeepMinimumSize = toAllow;
			if (toAllow) {
				ensureMinimumSize();
			}
		}
	}

	//------------------------------------------------------------
	// Window events
	//------------------------------------------------------------
	
	//************************************************************
	// Setup events
	//************************************************************

	/**
	 * コンポーネントの初期化の前に必要な初期化を行う。
	 * このメソッドは、{@link #initialComponent()} の中で最初に呼び出される。
	 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean preInitialComponent() {
		// place holder
		return true;
	}

	/**
	 * コンポーネントの初期化を行う。
	 * このメソッドは、{@link #initialComponent()} の中で、{@link #preInitialComponent()} の次に呼び出される。
	 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean onInitialComponent() {
		// place holder
		return true;
	}

	/**
	 * コンポーネントの初期化後の処理を行う。
	 * このメソッドは、{@link #initialComponent()} の中で、{@link #onInitialComponent()} の次に呼び出される。
	 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean postInitialComponent() {
		// place holder
		return true;
	}
	
	//************************************************************
	// Component events
	//************************************************************

	/**
	 * ウィンドウの位置が変わると呼び出されます。
	 * このメソッドでは、ノーマルウィンドウの最新の位置を保存します。
	 */
	protected void onWindowMoved(ComponentEvent e) {
		if (getExtendedState() == Frame.NORMAL) {
			_lastNormalBounds.setLocation(getX(), getY());
		}
	}

	/**
	 * ウィンドウのサイズが変わると呼び出されます。
	 * このメソッドでは、ノーマルウィンドウの最新のサイズを保存します。
	 */
	protected void onWindowResized(ComponentEvent e) {
		if (getExtendedState() == Frame.NORMAL) {
			_lastNormalBounds.setSize(getWidth(), getHeight());
		}
	}

	/**
	 * ウィンドウが可視になると呼び出されます。
	 */
	protected void onWindowShown(ComponentEvent e) {}

	/**
	 * ウィンドウが不可視になると呼び出されます。
	 */
	protected void onWindowHidden(ComponentEvent e) {}
	
	//************************************************************
	// Window events
	//************************************************************

	/**
	 * ウィンドウが最初に可視になったときに呼び出されます。
	 */
	protected void onWindowOpened(WindowEvent e) {}

	/**
	 * ユーザーが、ウィンドウのシステムメニューでウィンドウを閉じようとしたときに呼び出されます。
	 * このメソッドでは、{@link #closeWindow()} を呼び出します。
	 * @param e
	 */
	protected void onWindowClosing(WindowEvent e) {
		closeWindow();
	}

	/**
	 * ウィンドウに対する <code>dispose</code> の呼び出し結果として、ウィンドウがクローズされたときに呼び出されます。
	 */
	protected void onWindowClosed(WindowEvent e) {}

	/**
	 * ウィンドウが通常の状態から最小化された状態に変更されたときに呼び出されます。
	 */
	protected void onWindowIconified(WindowEvent e) {}

	/**
	 * ウィンドウが最小化された状態から通常の状態に変更されたときに呼び出されます。
	 */
	protected void onWindowDeiconified(WindowEvent e) {}

	/**
	 * ウィンドウがアクティブウィンドウに設定されると呼び出されます。
	 */
	protected void onWindowActivated(WindowEvent e) {}

	/**
	 * ウィンドウがアクティブウィンドウでなくなったときに呼び出されます。
	 */
	protected void onWindowDeactivated(WindowEvent e) {}
	
	//************************************************************
	// Window state events
	//************************************************************

	/**
	 * {@link #isWindowStateEventsEnabled()} が <tt>true</tt> を返すとき、
	 * ウィンドウの状態が変更されると呼び出されます。
	 */
	protected void onWindowStateChanged(WindowEvent e) {}
	
	//************************************************************
	// Window focus events
	//************************************************************

	/**
	 * {@link #isWindowFocusEventsEnabled()} が <tt>true</tt> を返すとき、
	 * ウィンドウがフォーカスされたウィンドウに設定されたときに呼び出されます。
	 */
	protected void onWindowGainedFocus(WindowEvent e) {}
	
	/**
	 * {@link #isWindowFocusEventsEnabled()} が <tt>true</tt> を返すとき、
	 * ウィンドウがフォーカスされたウィンドウではなくなったときに呼び出されます。
	 */
	protected void onWindowLostFocus(WindowEvent e) {}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * {@link #isKeepMinimumSize()} が返す状態に関係なく、
	 * ウィンドウのサイズが <code>getMinimumSize()</code> が返すサイズよりも小さいときに
	 * その最小サイズに合わせます。
	 */
	protected void ensureMinimumSize() {
		Dimension dmMin = getMinimumSize();
		if (dmMin != null && (getWidth() < dmMin.width || getHeight() < dmMin.height)) {
			setSize(Math.max(getWidth(), dmMin.width), Math.max(getHeight(), dmMin.height));
		}
	}

	/**
	 * ウィンドウを閉じてよいかどうかを判定します。
	 * このメソッドは、{@link #closeWindow()} から呼び出され、このメソッドが <tt>true</tt> を返した場合に <code>dispose</code> が呼び出されます。
	 * @return	ウィンドウを閉じる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean canCloseWindow() {
		return true;
	}

	/**
	 * ウィンドウを閉じて良い場合にウィンドウを閉じます。
	 * このメソッドでは、{@link #canCloseWindow()} が <tt>true</tt> を返した場合のみ、<code>dispose</code> を呼び出してリソースを破棄します。
	 * @see #canCloseWindow()
	 */
	protected void closeWindow() {
		if (canCloseWindow()) {
			this.dispose();
		}
	}

	/**
	 * このクラス標準の <code>WindowStateListener</code> が設定されている場合に <tt>true</tt> を返します。
	 */
	protected boolean isWindowStateEventsEnabled() {
		if (_listenerWindowStateEvents != null) {
			WindowStateListener[] listeners = getWindowStateListeners();
			for (WindowStateListener l : listeners) {
				if (l == _listenerWindowStateEvents) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * このクラス標準の <code>WindowStateListener</code> の有効／無効を設定します。
	 * @param toEnable	有効にする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected void enableWindowStateEvents(boolean toEnable) {
		boolean curEnabled = isWindowStateEventsEnabled();
		if (!curEnabled && toEnable) {
			addWindowStateListener(getDefaultWindowStateListener());
		}
		else if (curEnabled && !toEnable) {
			removeWindowStateListener(_listenerWindowStateEvents);
			_listenerWindowStateEvents = null;
		}
	}

	/**
	 * このクラス標準の <code>WindowFocusListener</code> が設定されている場合に <tt>true</tt> を返します。
	 */
	protected boolean isWindowFocusEventsEnabled() {
		if (_listenerWindowFocusEvents != null) {
			WindowFocusListener[] listeners = getWindowFocusListeners();
			for (WindowFocusListener l : listeners) {
				if (l == _listenerWindowFocusEvents) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * このクラス標準の <code>WindowFocusListener</code> の有効／無効を設定します。
	 * @param toEnable	有効にする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected void enableWindowFocusEvents(boolean toEnable) {
		boolean curEnabled = isWindowFocusEventsEnabled();
		if (!curEnabled && toEnable) {
			addWindowFocusListener(getDefaultWindowFocusListener());
		}
		else if (curEnabled && !toEnable) {
			removeWindowFocusListener(_listenerWindowFocusEvents);
			_listenerWindowFocusEvents = null;
		}
	}
	
	protected ComponentListener getDefaultComponentListener() {
		if (_listenerComponentEvents == null) {
			_listenerComponentEvents = new ComponentListener(){
				@Override
				public void componentShown(ComponentEvent e) {
					onWindowShown(e);
				}
				
				@Override
				public void componentHidden(ComponentEvent e) {
					onWindowHidden(e);
				}

				@Override
				public void componentMoved(ComponentEvent e) {
					onWindowMoved(e);
				}

				@Override
				public void componentResized(ComponentEvent e) {
					onWindowResized(e);
				}
			};
		}
		return _listenerComponentEvents;
	}
	
	protected WindowListener getDefaultWindowListener() {
		if (_listenerWindowEvents == null) {
			_listenerWindowEvents = new WindowListener(){
				@Override
				public void windowOpened(WindowEvent e) {
					onWindowOpened(e);
				}

				@Override
				public void windowClosing(WindowEvent e) {
					onWindowClosing(e);
				}

				@Override
				public void windowClosed(WindowEvent e) {
					onWindowClosed(e);
				}
				
				@Override
				public void windowActivated(WindowEvent e) {
					onWindowActivated(e);
				}

				@Override
				public void windowDeactivated(WindowEvent e) {
					onWindowDeactivated(e);
				}

				@Override
				public void windowIconified(WindowEvent e) {
					onWindowIconified(e);
				}

				@Override
				public void windowDeiconified(WindowEvent e) {
					onWindowDeiconified(e);
				}
			};
		}
		return _listenerWindowEvents;
	}
	
	protected WindowStateListener getDefaultWindowStateListener() {
		if (_listenerWindowStateEvents == null) {
			_listenerWindowStateEvents = new WindowStateListener(){
				@Override
				public void windowStateChanged(WindowEvent e) {
					onWindowStateChanged(e);
				}
			};
		}
		return _listenerWindowStateEvents;
	}
	
	protected WindowFocusListener getDefaultWindowFocusListener() {
		if (_listenerWindowFocusEvents == null) {
			_listenerWindowFocusEvents = new WindowFocusListener(){
				@Override
				public void windowGainedFocus(WindowEvent e) {
					onWindowGainedFocus(e);
				}

				@Override
				public void windowLostFocus(WindowEvent e) {
					onWindowLostFocus(e);
				}
			};
		}
		return _listenerWindowFocusEvents;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
