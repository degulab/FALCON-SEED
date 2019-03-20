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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AbSubFrame.java	3.2.2	2015/10/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbSubFrame.java	2.1.0	2013/08/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import ssac.util.properties.ExConfiguration;

/**
 * 最も基本的なサブフレームの基本実装。
 * このコンポーネントでは、コンポーネントならびにウィンドウのリスナーを登録し、
 * リスナーに対応するインタフェースを実装するためのフレームワークを提供する。
 * コンストラクタでは、{@link #setDefaultCloseOperation(int)} が {@link javax.swing.WindowConstants#DO_NOTHING_ON_CLOSE} を引数として呼び出され、
 * このクラス標準の <code>WindowListener</code> と <code>ComponentListener</code> が設定される。
 * このクラスのコンストラクタを呼び出した直後は、必ず {@link #initialComponent()} を呼び出すこと。
 * {@link #initialComponent()} の呼び出しによって、ダイアログ内のコンポーネントが初期化される。
 * </blockquote>
 * 
 * @version 3.2.2
 * @since 2.1.0
 */
public abstract class AbSubFrame extends AbBaseFrame
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -4033344266803891228L;
	
	static protected final String KEY_SUBFRAMECLOSE_BY_ESC = "close.by.escape";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private ComponentListener	_listenerComponentEvents;
	private WindowListener		_listenerWindowEvents;
	private WindowStateListener	_listenerWindowStateEvents;
	private WindowFocusListener	_listenerWindowFocusEvents;

	/** このウィンドウの親ウィンドウ **/
	private final Window		_parentWindow;

//	/** 直前のノーマルウィンドウの境界 **/
//	private Rectangle			_lastNormalBounds;

	/** ウィンドウを閉じたときの理由(コード) **/
	private int					_closedReasonCode = 0;
//	/** アイコン化ステートを保存するフラグ **/
//	private boolean				_flgStoreIconicState = false;
//	/** ウィンドウステートを保存するフラグ **/
//	private boolean				_flgStoreWindowState = true;
//	/** 位置情報を保存するフラグ **/
//	private boolean				_flgStoreWindowLocation = true;
//	/** サイズ情報を保存するフラグ **/
//	private boolean				_flgStoreWindowSize = true;
//	/** 最小サイズを維持するフラグ **/
//	private boolean				_flgKeepMinimumSize = false;
	/** このダイアログの状態を保存する際のキープレフィックス **/
	private String				_configPrefix;
	/** このダイアログの状態を保存する <code>ExConfiguration</code> オブジェクト **/
	private ExConfiguration		_configProps;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 初期状態が不可視である、新しい </code>Frame</code> を構築します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param parent	親ウィンドウを指定する。親ウィンドウを指定しない場合は <tt>null</tt>
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JFrame#JFrame()
	 */
	public AbSubFrame(Window parent) throws HeadlessException {
		super();
		_parentWindow = parent;
		constructAbSubFrame();
	}

	/**
	 * 指定されたタイトルで、初期状態で不可視の新しい <code>Frame</code> を構築します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param parent	親ウィンドウを指定する。親ウィンドウを指定しない場合は <tt>null</tt>
	 * @param title	フレームのタイトル
	 * @throws HeadlessException	<code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合
	 * @see javax.swing.JFrame#JFrame(String)
	 */
	public AbSubFrame(Window parent, String title) throws HeadlessException {
		super(title);
		_parentWindow = parent;
		constructAbSubFrame();
	}

	/**
	 * <code>Frame</code> を、画面デバイスの指定された <code>GraphicsConfiguration</code> にタイトルなしで作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param parent	親ウィンドウを指定する。親ウィンドウを指定しない場合は <tt>null</tt>
	 * @param gc	新しい <code>Frame</code> の構築に使用する <code>GraphicsConfiguration</code>。
	 * 				<em>gc</em> が <tt>null</tt> の場合、システムのデフォルトの <code>GraphicsConfiguration</code> が使用される
	 * @throws IllegalArgumentException	<em>gc</em> がスクリーンデバイスのものではない場合。
	 * 										この例外は <code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合に必ずスローされる
	 * @see javax.swing.JFrame#JFrame(GraphicsConfiguration)
	 */
	public AbSubFrame(Window parent, GraphicsConfiguration gc) {
		super(gc);
		_parentWindow = parent;
		constructAbSubFrame();
	}

	/**
	 * <code>Frame</code> を、指定されたタイトルで、画面デバイスの指定された <code>GraphicsConfiguration</code> に作成します。
	 * このコンストラクタはコンポーネントのローカルプロパティーを <code>JComponent.getDefaultLocale</code> によって返された値に設定します。
	 * @param parent	親ウィンドウを指定する。親ウィンドウを指定しない場合は <tt>null</tt>
	 * @param title	フレームのボーダーに表示されるタイトル。<tt>null</tt> 値は空の文字列 &quot;&quot; として扱われる
	 * @param gc	新しい <code>Frame</code> の構築に使用する <code>GraphicsConfiguration</code>。
	 * 				<em>gc</em> が <tt>null</tt> の場合、システムのデフォルトの <code>GraphicsConfiguration</code> が使用される
	 * @throws IllegalArgumentException	<em>gc</em> がスクリーンデバイスのものではない場合。
	 * 										この例外は <code>GraphicsEnvironment.isHeadless()</code> が <tt>true</tt> を返す場合に必ずスローされる
	 * @see javax.swing.JFrame#JFrame(String, GraphicsConfiguration)
	 */
	public AbSubFrame(Window parent, String title, GraphicsConfiguration gc) {
		super(title, gc);
		_parentWindow = parent;
		constructAbSubFrame();
	}
	
	private final void constructAbSubFrame() {
//		_lastNormalBounds = new Rectangle();
//		getBounds(_lastNormalBounds);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	/**
	 * ウィンドウの初期化を行う。
	 * このメソッドは、コンストラクタ呼び出し直後に、最初に呼び出す。
	 */
	public void initialComponent() {
		onInitialContents();
		
		onSetupConditions();
		
		onSetupBounds();
		
		onSetupActions();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このウィンドウに設定された親ウィンドウを返す。
	 * 親ウィンドウが設定されていない場合は <tt>null</tt> を返す。
	 */
	public Window getParentWindow() {
		return _parentWindow;
	}

//	/**
//	 * ノーマルウィンドウでの最新の左上位置を返す。
//	 * @return	ノーマルウィンドウでの最新左上位置
//	 */
//	public Point getLastNormalLocation() {
//		return _lastNormalBounds.getLocation();
//	}
//
//	/**
//	 * ノーマルウィンドウでの最新のサイズを返す。
//	 * @return	ノーマルウィンドウでの最新サイズ
//	 */
//	public Dimension getLastNormalSize() {
//		return _lastNormalBounds.getSize();
//	}
//
//	/**
//	 * ノーマルウィンドウでの最新の境界を返す。
//	 * @return	ノーマルウィンドウでの最新境界
//	 */
//	public Rectangle getLastNormalBounds() {
//		return new Rectangle(_lastNormalBounds);
//	}
//
//	/**
//	 * ウィンドウの最小サイズを維持する場合に <tt>true</tt> を返す。
//	 */
//	public boolean isKeepMinimumSize() {
//		return _flgKeepMinimumSize;
//	}
//
//	/**
//	 * ウィンドウの最小サイズを維持するかどうかを設定する。
//	 * @param toAllow	最小サイズを維持する場合は <tt>true</tt>、維持しない場合は <tt>false</tt>
//	 */
//	public void setKeepMinimumSize(boolean toAllow) {
//		if (_flgKeepMinimumSize != toAllow) {
//			_flgKeepMinimumSize = toAllow;
//			if (toAllow) {
//				ensureMinimumSize();
//			}
//		}
//	}
//
//	/**
//	 * ウィンドウの状態の保存が許可されている場合は <tt>true</tt> を返す。
//	 */
//	public boolean isStoreWindowState() {
//		return _flgStoreWindowState;
//	}
//
//	/**
//	 * ウィンドウの状態を保存する際に、アイコン化ステートも保存する場合は <tt>true</tt> を返す。
//	 */
//	public boolean isStoreIconicState() {
//		return _flgStoreIconicState;
//	}
//
//	/**
//	 * ウィンドウ位置の保存が許可されている場合は <tt>true</tt> を返す。
//	 */
//	public boolean isStoreWindowLocation() {
//		return _flgStoreWindowLocation;
//	}
//
//	/**
//	 * ウィンドウサイズの保存が許可されている場合は <tt>true</tt> を返す。
//	 */
//	public boolean isStoreWindowSize() {
//		return _flgStoreWindowSize;
//	}
//
//	/**
//	 * ウィンドウの状態の保存を許可もしくは禁止する。
//	 * @param toAllow	保存を許可する場合は <tt>true</tt>、禁止する場合は <tt>false</tt>
//	 */
//	public void setStoreWindowState(boolean toAllow) {
//		_flgStoreWindowState = toAllow;
//	}
//
//	/**
//	 * ウィンドウの状態保存において、アイコン化状態の保存を許可もしくは禁止する。
//	 * アイコン化状態の保存を禁止した場合、ノーマルの状態が保存される。
//	 * @param toAllow	保存を許可する場合は <tt>true</tt>、禁止する場合は <tt>false</tt>
//	 */
//	public void setStoreIconicState(boolean toAllow) {
//		_flgStoreIconicState = toAllow;
//	}
//
//	/**
//	 * ウィンドウ位置の保存を許可もしくは禁止する。
//	 * @param toAllow	保存を許可する場合は <tt>true</tt>、禁止する場合は <tt>false</tt>
//	 */
//	public void setStoreWindowLocation(boolean toAllow) {
//		_flgStoreWindowLocation = toAllow;
//	}
//
//	/**
//	 * ウィンドウサイズの保存を許可もしくは禁止する。
//	 * @param toAllow	保存を許可する場合は <tt>true</tt>、禁止する場合は <tt>false</tt>
//	 */
//	public void setStoreWindowSize(boolean toAllow) {
//		_flgStoreWindowSize = toAllow;
//	}

	/**
	 * このウィンドウの標準状態を返す。
	 * @return	ウィンドウの標準状態を返す。標準状態を指定しない場合は <tt>Frame.NORMAL</tt> を返す。
	 * @see #getExtendedState()
	 */
	public int getDefaultWindowState() {
		return Frame.NORMAL;
	}

//	/**
//	 * このダイアログの標準位置を返す。
//	 * @return	ダイアログの標準位置を返す。標準位置を指定しない場合は <tt>null</tt> を返す。
//	 */
//	public Point getDefaultWindowLocation() {
//		return null;
//	}
//
//	/**
//	 * このダイアログの標準サイズを返す。
//	 * @return	ダイアログの標準サイズを返す。標準サイズを指定しない場合は <tt>null</tt> を返す。
//	 */
//	public Dimension getDefaultWindowSize() {
//		return null;
//	}

	/**
	 * <code>[ESC]</code> キーによってウィンドウが閉じられる場合は <tt>true</tt> を返す。
	 */
	public boolean isCloseByEscapeKeyEnabled() {
		InputMap imap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		Object key = imap.get(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		return (KEY_SUBFRAMECLOSE_BY_ESC.equals(key));
	}

	/**
	 * <code>[ESC]</code> キーによってウィンドウを閉じるかどうかを設定する。
	 * @param toEnable	キー入力でウィンドウを閉じる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public void setCloseByEscapeKeyEnabled(boolean toEnable) {
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		if (toEnable) {
			// set [ESC] key action
			InputMap imap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			imap.put(ks, KEY_SUBFRAMECLOSE_BY_ESC);
			getRootPane().getActionMap().put(KEY_SUBFRAMECLOSE_BY_ESC, new AbstractAction(){
				@Override
				public void actionPerformed(ActionEvent e) {
					onWindowClosingByEscapeKey(e);
				}
			});
		}
		else {
			// remove [ESC] key action
			InputMap imap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			Object key = imap.get(ks);
			if (KEY_SUBFRAMECLOSE_BY_ESC.equals(key)) {
				imap.remove(ks);
			}
			getRootPane().getActionMap().remove(KEY_SUBFRAMECLOSE_BY_ESC);
		}
	}

	/**
	 * ダイアログの状態を保存するキーのプレフィックスを返す。
	 * プレフィックスが設定されていない場合は <tt>null</tt> を返す。
	 */
	public String getConfigurationPrefix() {
		return _configPrefix;
	}

	/**
	 * ダイアログの状態を保存する <code>ExConfiguration</code> オブジェクトが設定されていれば <tt>true</tt> を返す。
	 */
	public boolean hasConfiguration() {
		return (_configProps != null);
	}

	/**
	 * ダイアログの状態を保存する <code>ExConfiguration</code> オブジェクトを返す。
	 * @return	このダイアログに設定されている <code>ExConfiguration</code> オブジェクトを返す。
	 * 			設定されていない場合は <tt>null</tt> を返す。
	 */
	public ExConfiguration getConfiguration() {
		return _configProps;
	}

	/**
	 * ダイアログの状態を保存する <code>ExConfiguration</code> オブジェクトと、
	 * 保存時のキーのプレフィックスを設定する。
	 * @param prefix	キーのプレフィックスとする文字列
	 * @param config	<code>ExConfiguration</code> オブジェクト
	 */
	public void setConfiguration(String prefix, ExConfiguration config) {
		this._configPrefix = prefix;
		this._configProps  = config;
	}

	/**
	 * ウィンドウが閉じられた理由コードを返す。
	 * @return	ウィンドウが閉じられた理由コード
	 */
	public int getClosedReason() {
		return _closedReasonCode;
	}

	/**
	 * ウィンドウが閉じられた理由コードを設定する。
	 * @param reasonCode	理由コード
	 */
	protected void setClosedReason(int reasonCode) {
		_closedReasonCode = reasonCode;
	}

	//------------------------------------------------------------
	// Window events
	//------------------------------------------------------------
	
	//************************************************************
	// Setup events
	//************************************************************

	/**
	 * コンポーネントのコンテンツの初期化を行う。
	 * このメソッドは、{@link #initialComponent()} の中で最初に呼び出される。
	 */
	protected void onInitialContents() {
		// place holder
	}

	/**
	 * コンポーネントの状態などを設定する。
	 * このメソッドは、{@link #initialComponent()} の中で、{@link #onInitialContents()} の次に呼び出される。
	 */
	protected void onSetupConditions() {
		// place holder
	}
	
	/**
	 * コンポーネントの位置やサイズを設定する。
	 * このメソッドは、{@link #initialComponent()} の中で、{@link #onSetupConditions()} の次に呼び出される。
	 */
	protected void onSetupBounds() {
		// 設定のリストア
		if (hasConfiguration()) {
			onRestoreConfiguration(getConfiguration(), getConfigurationPrefix());
		} else {
			// サイズの調整
			pack();
			setLocationRelativeTo(getParentWindow());
//			getBounds(_lastNormalBounds);
		}
	}
	
	/**
	 * コンポーネントのアクションを設定する。
	 * このメソッドでは、コンポーネントリスナーとウィンドウリスナーが設定される。
	 * このメソッドは、{@link #initialComponent()} の中で、{@link #onSetupBounds()} の次に呼び出される。
	 */
	protected void onSetupActions() {
		// setup basic window events
		addComponentListener(getDefaultComponentListener());
		addWindowListener(getDefaultWindowListener());
	}

	/**
	 * コンポーネントの状態を、設定された <code>ExConfiguration</code> オブジェクトからリストアする。
	 * このメソッドは、{@link #onSetupBounds()} の中で呼び出される。
	 * このコンポーネントに <code>ExConfiguration</code> オブジェクトが設定されていない場合は呼び出されない。
	 * @param config	<code>ExConfiguration</code> オブジェクト(必ず <tt>null</tt> 以外)
	 * @param prefix	プロパティのアクセスに使用するキーのプレフィックス、プレフィックスを使用しない場合は <tt>null</tt>
	 */
	protected void onRestoreConfiguration(final ExConfiguration config, final String prefix) {
		restoreWindowStatus(config, prefix);
//		// restore window size
//		Dimension winSize = null;
//		if (isStoreWindowSize()) {
//			winSize = config.getWindowSize(prefix);
//			if (winSize == null) {
//				winSize = getDefaultWindowSize();
//			}
//		} else {
//			winSize = getDefaultWindowSize();
//		}
//		//--- set size
//		if (winSize == null) {
//			this.pack();
//		} else {
//			this.setSize(winSize);
//		}
//		
//		// restore location
//		Point winPos = null;
//		if (isStoreWindowLocation()) {
//			winPos = config.getWindowLocation(prefix);
//			if (winPos == null) {
//				winPos = getDefaultWindowLocation();
//			}
//		} else {
//			winPos = getDefaultWindowLocation();
//		}
//		//--- setLocation
//		if (winPos == null) {
//			this.setLocationRelativeTo(getParentWindow());
//		} else {
//			this.setLocation(winPos);
//		}
//		
//		// check screen bounds
//		Rectangle rcBounds = getBounds();
//		Rectangle rcClipped = SwingTools.convertIntoAllScreenDesktopBounds(rcBounds);
//		if (rcClipped != null && !rcBounds.equals(rcClipped)) {
//			setSize(rcBounds.getSize());
//			setLocation(rcBounds.getLocation());
//		}
//		getBounds(_lastNormalBounds);
//		
//		// restore window state
//		if (isStoreWindowState()) {
//			int winState = config.getWindowState(prefix);
//			if (winState != Frame.NORMAL) {
//				setExtendedState(winState);
//			}
//		}
	}
	
	/**
	 * コンポーネントの状態を、設定された <code>ExConfiguration</code> オブジェクトに保存する。
	 * このメソッドは、{@link #doCloseDialog()} の中で、{@link #dispose()} を呼び出す直前に呼び出される。
	 * このコンポーネントに <code>ExConfiguration</code> オブジェクトが設定されていない場合は呼び出されない。
	 * @param config	<code>ExConfiguration</code> オブジェクト(必ず <tt>null</tt> 以外)
	 * @param prefix	プロパティのアクセスに使用するキーのプレフィックス、プレフィックスを使用しない場合は <tt>null</tt>
	 */
	protected void onStoreConfiguration(final ExConfiguration config, final String prefix) {
		storeWindowStatus(config, prefix);
//		int wstate = getExtendedState();
//		
//		// store current window state
//		if (isStoreWindowState()) {
//			if ((wstate & Frame.ICONIFIED) != 0) {
//				if (isStoreIconicState()) {
//					config.setWindowState(prefix, wstate);
//				} else {
//					config.setWindowState(prefix, Frame.NORMAL);
//				}
//			} else {
//				config.setWindowState(prefix, wstate);
//			}
//		}
//		
//		// store current window location
//		if (isStoreWindowLocation()) {
//			Point defPos = getDefaultWindowLocation();
//			Point curPos = getLastNormalLocation();
//			if (defPos != null && defPos.equals(curPos)) {
//				curPos = null;
//			}
//			config.setWindowLocation(prefix, curPos);
//		}
//		
//		// store current window size
//		if (isStoreWindowSize()) {
//			Dimension defSize = getDefaultWindowSize();
//			Dimension curSize = getLastNormalSize();
//			// defSize == curSize なら、プロパティのエントリは null とする
//			if (defSize != null && defSize.equals(curSize)) {
//				curSize = null;
//			}
//			config.setWindowSize(prefix, curSize);
//		}
	}
	
	//************************************************************
	// Component events
	//************************************************************

	/**
	 * ウィンドウの位置が変わると呼び出されます。
	 * このメソッドでは、ノーマルウィンドウの最新の位置を保存します。
	 */
	protected void onWindowMoved(ComponentEvent e) {
//		if (getExtendedState() == Frame.NORMAL) {
//			_lastNormalBounds.setLocation(getX(), getY());
//		}
	}

	/**
	 * ウィンドウのサイズが変わると呼び出されます。
	 * このメソッドでは、ノーマルウィンドウの最新のサイズを保存します。
	 */
	protected void onWindowResized(ComponentEvent e) {
//		if (getExtendedState() == Frame.NORMAL) {
//			if (isKeepMinimumSize()) {
//				ensureMinimumSize();
//			}
//			_lastNormalBounds.setSize(getWidth(), getHeight());
//		}
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
	 * {@link #isCloseByEscapeKeyEnabled()} が <tt>true</tt> を返すとき、
	 * ユーザーが <code>[ESC]</code> キーによってウィンドウを閉じようとしたときに呼び出されます。
	 * このメソッドでは、{@link #closeDialog(int))} を引数 0 で呼び出します。
	 */
	protected void onWindowClosingByEscapeKey(ActionEvent e) {
		closeWindow(0);
	}

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
		closeWindow(0);
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

//	/**
//	 * {@link #isKeepMinimumSize()} が返す状態に関係なく、
//	 * ウィンドウのサイズが <code>getMinimumSize()</code> が返すサイズよりも小さいときに
//	 * その最小サイズに合わせます。
//	 */
//	protected void ensureMinimumSize() {
//		Dimension dmMin = getMinimumSize();
//		if (dmMin != null && (getWidth() < dmMin.width || getHeight() < dmMin.height)) {
//			setSize(Math.max(getWidth(), dmMin.width), Math.max(getHeight(), dmMin.height));
//		}
//	}

	/**
	 * ウィンドウを閉じてリソースを破棄します。
	 * このメソッドでは、<code>hasConfiguration()</code> が <tt>true</tt> を返した場合に <code>onStoreConfiguration()</code> を呼び出し、
	 * 最後に <code>dispose()</code> を実行します。
	 * 通常、このメソッドは {@link #closeDialog(int)} から呼び出されます。
	 * <p><b>注：</b>
	 * <blockquote>
	 * このメソッドの実行では、ウィンドウリソースがすでに破棄されているかどうかは判定しません。
	 * <code>onStoreConfiguration</code> が呼び出されたときに、ウィンドウリソースがすでに破棄されている場合は、
	 * システムが例外をスローしますので、このメソッドを直接呼び出す際は注意してください。
	 * </blockquote>
	 * @see #dispose()
	 * @see #hasConfiguration()
	 * @see #onStoreConfiguration(ExConfiguration, String)
	 * @see #closeWindow(int)
	 */
	protected void doCloseWindow() {
		if (hasConfiguration()) {
			onStoreConfiguration(getConfiguration(), getConfigurationPrefix());
		}
		this.dispose();
	}

	/**
	 * ウィンドウを閉じて良い場合にウィンドウを閉じます。
	 * このメソッドでは、{@link #canCloseWindow()} が <tt>true</tt> を返した場合のみ、
	 * 指定された理由コードを保存してウィンドウリソースを破棄します。
	 * ウィンドウリソースの破棄は、理由コード設定後、{@link #isDisplayable()} が <tt>true</tt> を返した場合のみ、
	 * {@link #doCloseWindow()} を呼び出します。
	 * @see #canCloseWindow()
	 * @see #isDisplayable()
	 * @see #doCloseWindow()
	 */
	protected void closeWindow(int reasonCode) {
		if (canCloseWindow()) {
			setClosedReason(reasonCode);
			if (isDisplayable()) {
				doCloseWindow();
			}
		}
	}

	/**
	 * ウィンドウを閉じてよいかどうかを判定します。
	 * このメソッドは、{@link #closeWindow()} から呼び出され、このメソッドが <tt>true</tt> を返した場合のみ、ウィンドウリソースが破棄されます。
	 * @return	ウィンドウを閉じる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean canCloseWindow() {
		return true;
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
					saveWindowLocationOnMoved(e);
					onWindowMoved(e);
				}

				@Override
				public void componentResized(ComponentEvent e) {
					saveWindowSizeOnResized(e);
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
					adjustWindowBoundsWhenOpened(e);
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
