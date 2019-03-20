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
 * @(#)AbBaseFrame.java	3.2.2	2015/10/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import ssac.util.Objects;
import ssac.util.properties.ExConfiguration;

/**
 * すべてのフレームのベース機能の共通実装。
 * おもに、ウィンドウの位置やサイズ、ステートを記憶するためのベース機能を提供する。
 * 
 * @version 3.2.2
 * @since 3.2.2
 */
public class AbBaseFrame extends JFrame
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -3793876078886919612L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このウィンドウのノーマル時の最後の表示位置 **/
	private Point		_lastWindowLocation;
	/** このウィンドウのノーマル時の最後の表示サイズ **/
	private Dimension	_lastWindowSize;
	/** ウィンドウ最小サイズを維持するフラグ(デフォルト=<tt>false</tt>) **/
	private boolean		_flgKeepMinimumSize = false;
	/** ウィンドウステートを保存するフラグ(デフォルト=<tt>true</tt>) **/
	private boolean		_flgStoreState = true;
	/** ウィンドウ位置情報を保存するフラグ(デフォルト=<tt>true</tt>) **/
	private boolean		_flgStoreLocation = true;
	/** ウィンドウサイズ情報を保存するフラグ(デフォルト=<tt>true</tt>) **/
	private boolean		_flgStoreSize = true;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public AbBaseFrame() throws HeadlessException {
		super();
	}

	public AbBaseFrame(String title) throws HeadlessException {
		super(title);
	}

	public AbBaseFrame(GraphicsConfiguration gc) {
		super(gc);
	}

	public AbBaseFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

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
			if (isDisplayable() && toAllow) {
				ensureWindowMinimumSize();
			}
		}
	}

	/**
	 * ウィンドウステートの保存が許可されている場合は <tt>true</tt> を返す。
	 */
	public boolean isStoreWindowState() {
		return _flgStoreState;
	}

	/**
	 * ウィンドウ位置の保存が許可されている場合は <tt>true</tt> を返す。
	 */
	public boolean isStoreLocation() {
		return _flgStoreLocation;
	}

	/**
	 * ウィンドウサイズの保存が許可されている場合は <tt>true</tt> を返す。
	 */
	public boolean isStoreSize() {
		return _flgStoreSize;
	}

	/**
	 * ウィンドウステートの保存を許可もしくは禁止する。
	 * @param toAllow	保存を許可する場合は <tt>true</tt>、禁止する場合は <tt>false</tt>
	 */
	public void setStoreWindowState(boolean toAllow) {
		_flgStoreState = toAllow;
	}

	/**
	 * ウィンドウ位置の保存を許可もしくは禁止する。
	 * @param toAllow	保存を許可する場合は <tt>true</tt>、禁止する場合は <tt>false</tt>
	 */
	public void setStoreLocation(boolean toAllow) {
		_flgStoreLocation = toAllow;
	}

	/**
	 * ウィンドウサイズの保存を許可もしくは禁止する。
	 * @param toAllow	保存を許可する場合は <tt>true</tt>、禁止する場合は <tt>false</tt>
	 */
	public void setStoreSize(boolean toAllow) {
		_flgStoreSize = toAllow;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * ウィンドウの標準サイズを返す。
	 * @return	規定された標準サイズ、標準サイズを規定しない場合は <tt>null</tt>
	 */
	protected Dimension getDefaultSize() {
		return null;
	}

	/**
	 * 現在保存されているウィンドウ位置(スクリーン座標系)を返す。
	 * @return	スクリーン座標系でのノーマルウィンドウ位置
	 */
	protected Point getLastWindowLocation() {
		return _lastWindowLocation;
	}

	/**
	 * 指定されたウィンドウ位置(スクリーン座標系)を保存する。
	 * @param p	保存する位置
	 */
	protected void setLastWindowLocation(Point p) {
		_lastWindowLocation = p;
	}

	/**
	 * 現在保存されているウィンドウサイズを返す。
	 * @return	ノーマルウィンドウサイズ
	 */
	protected Dimension getLastWindowSize() {
		return _lastWindowSize;
	}

	/**
	 * 指定されたウィンドウサイズを保存する。
	 * @param d	保存するサイズ
	 */
	protected void setLastWindowSize(Dimension d) {
		_lastWindowSize = d;
	}

	/**
	 * 指定されたプロパティに保存されているウィンドウの状態を復元する。
	 * @param config		プロパティオブジェクト
	 * @param keyPrefix		ウィンドウの状態保存キーのプレフィックス
	 * @param defaultSize	標準のサイズ、もしくは <tt>null</tt>
	 * @param minSize		最小サイズ、もしくは <tt>null</tt>
	 */
	protected void restoreWindowStatus(ExConfiguration config, String keyPrefix) {
		// get window status from properties
		int       mainState = (isStoreWindowState() && config != null ? config.getWindowState(keyPrefix) : JFrame.NORMAL);
		Point     mainLoc   = (isStoreLocation()    && config != null ? config.getWindowLocation(keyPrefix) : null);
		Dimension mainSize  = (isStoreSize()        && config != null ? config.getWindowSize(keyPrefix) : null);
		if (mainSize == null) {
			// サイズが保存されていない場合は、標準サイズ
			mainSize = getDefaultSize();
		}
//		// TO DO: Debug
//		{
//			System.err.println(getClass().getName() + "#restoreWindowStatus() : window state property from \"" + String.valueOf(keyPrefix) + "\" -> " + mainState);
//			System.err.println(getClass().getName() + "#restoreWindowStatus() : window location property from \"" + String.valueOf(keyPrefix) + "\" -> " + String.valueOf(mainLoc));
//			System.err.println(getClass().getName() + "#restoreWindowStatus() : window size property from \"" + String.valueOf(keyPrefix) + "\" -> " + String.valueOf(mainSize));
//		}
		
		// 位置とサイズの調整
		WindowRectangle wrc = SwingTools.adjustWindowLocationIntoScreen(this, mainLoc, mainSize, getMinimumSize());
		mainLoc  = wrc.getLocation();
		mainSize = wrc.getSize();
		_lastWindowLocation = mainLoc;
		_lastWindowSize = mainSize;
		
		// サイズの設定
//		// TO DO: Debug
//		System.err.println(getClass().getName() + "#restoreWindowStatus() : Restore window size <- " + String.valueOf(mainSize));
		if (mainSize != null) {
			setPreferredSize(mainSize);
			setSize(mainSize);
		} else {
			pack();
		}
		
		// 位置の設定
//		// TO DO: Debug
//		System.err.println(getClass().getName() + "#restoreWindowStatus() : Restore window location <- " + String.valueOf(mainLoc));
		if (mainLoc != null) {
			setLocation(mainLoc);
		} else {
			setLocationRelativeTo(getOwner());
		}
		
		// ステートの設定
		if (mainState != JFrame.NORMAL) {
//			// TO DO: Debug
//			System.err.println(getClass().getName() + "#restoreWindowStatus() : Restore window state <- " + mainState);
			setExtendedState(mainState);
		}
	}

	/**
	 * ウィンドウの状態を、指定されたプロパティに保存する。
	 * @param config	プロパティオブジェクト
	 * @param keyPrefix	ウィンドウの状態保存キーのプレフィックス
	 */
	protected void storeWindowStatus(ExConfiguration config, String keyPrefix) {
		if (config != null) {
			//--- Window state
			if (isStoreWindowState()) {
				config.setWindowState(keyPrefix, getExtendedState());
//				// TO DO: Debug
//				System.err.println(getClass().getName() + "#storeWindowState() : Store window state to \"" + String.valueOf(keyPrefix) + "\" <- " + getExtendedState());
			}
			//--- Location on screen
			if (isStoreLocation()) {
				config.setWindowLocation(keyPrefix, _lastWindowLocation);
//				// TO DO: Debug
//				System.err.println(getClass().getName() + "#storeWindowState() : Store window location to \"" + String.valueOf(keyPrefix) + "\" <- " + String.valueOf(_lastFrameScreenLocation));
			}
			//--- Size
			if (isStoreSize()) {
				if (_lastWindowSize != null && Objects.isEqual(_lastWindowSize, getDefaultSize())) {
					// 最終サイズと標準サイズが同一なら、プロパティは除去
					config.setWindowSize(keyPrefix, null);
//					// TO DO: Debug
//					System.err.println(getClass().getName() + "#storeWindowState() : Store window size to \"" + String.valueOf(keyPrefix) + "\" <- null");
				} else {
					// 最終サイズをプロパティに保存
					config.setWindowSize(keyPrefix, _lastWindowSize);
//					// TO DO: Debug
//					System.err.println(getClass().getName() + "#storeWindowState() : Store window size to \"" + String.valueOf(keyPrefix) + "\" <- " + String.valueOf(_lastFrameSize));
				}
			}
		}
	}

	/**
	 * {@link #isKeepMinimumSize()} が返す状態に関係なく、
	 * ウィンドウのサイズが <code>getMinimumSize()</code> が返すサイズよりも小さいときに
	 * その最小サイズに合わせます。
	 */
	protected void ensureWindowMinimumSize() {
		Dimension dmMin = getMinimumSize();
		if (dmMin != null && (getWidth() < dmMin.width || getHeight() < dmMin.height)) {
			setSize(Math.max(getWidth(), dmMin.width), Math.max(getHeight(), dmMin.height));
		}
	}

	/**
	 * ウィンドウがオープンされた直後に、ウィンドウ領域調整のために呼び出されるイベントハンドラ。
	 * このメソッドは、{@link java.awt.event.WindowListener#windowOpened(WindowEvent)} メソッドで最初に呼び出されるものとする。
	 * @param e	イベントオブジェクト
	 */
	protected void adjustWindowBoundsWhenOpened(WindowEvent e) {
		// ウィンドウフレームがノーマルの場合のみ、調整アルゴリズム実行
		SwingTools.adjustWindowBoundsIntoWindowCenterScreen(this);
//		// TO DO :Debug
//		{
//			Rectangle rcOld = getBounds();
//			if (SwingTools.adjustWindowBoundsIntoWindowCenterScreen(this)) {
//				Rectangle rcNew = getBounds();
//				System.err.println(getClass().getName() + "#adjustWindowBoundsWhenOpened() : Window bounds adjusted : old=" + rcOld + ", new=" + rcNew);
//			}
//		}
	}

	/**
	 * ウィンドウが移動した直後に、ウィンドウ位置の保存のために呼び出されるイベントハンドラ。
	 * このメソッドは、{@link java.awt.event.ComponentListener#componentMoved(ComponentEvent)} メソッドで最初に呼び出されるものとする。
	 * @param e	イベントオブジェクト
	 */
	protected void saveWindowLocationOnMoved(ComponentEvent e) {
		if (isVisible() && getExtendedState() == JFrame.NORMAL) {
			// ウィンドウが表示されており、ノーマルフレームの状態のときのみ保存
			if (_lastWindowLocation == null) {
				_lastWindowLocation = getLocationOnScreen();
			} else {
				_lastWindowLocation.setLocation(getLocationOnScreen());
			}
//			// TO DO: Debug
//			System.err.println(getClass().getName() + " : Save window location on screen : " + _lastFrameScreenLocation);
		}
	}

	/**
	 * ウィンドウサイズが変更された直後に、ウィンドウサイズの保存のために呼び出されるイベントハンドラ。
	 * このメソッドは、{@link java.awt.event.ComponentListener#componentResized(ComponentEvent)} メソッドで最初に呼び出されるものとする。
	 * @param e	イベントオブジェクト
	 */
	protected void saveWindowSizeOnResized(ComponentEvent e) {
		if (getExtendedState() == JFrame.NORMAL) {
			//--- 最小サイズ調整
			if (isKeepMinimumSize()) {
				ensureWindowMinimumSize();
			}
			//--- ウィンドウが表示されていれば、サイズ保存
			if (isVisible()) {
				if (_lastWindowSize == null) {
					_lastWindowSize = getSize();
				} else {
					getSize(_lastWindowSize);
				}
//				// TO DO: Debug
//				System.err.println(getClass().getName() + " : Save window size : " + _lastFrameSize);
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
