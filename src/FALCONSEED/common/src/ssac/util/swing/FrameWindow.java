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
 * @(#)FrameWindow.java	3.2.2	2015/10/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FrameWindow.java	2.0.0	2012/11/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FrameWindow.java	1.10	2009/01/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ssac.util.properties.ExConfiguration;

/**
 * フレームを持つウィンドウの基本クラス
 * 
 * @version 3.2.2
 */
public class FrameWindow extends AbBaseFrame
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------

	private static final long serialVersionUID = -1753720328962341102L;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private ComponentListener	_listenerComponentEvents;
	private WindowListener		_listenerWindowEvents;
	private WindowStateListener	_listenerWindowStateEvents;
	private WindowFocusListener	_listenerWindowFocusEvents;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FrameWindow() {
		super();
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	
	public void initialComponent() {
		
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal interfaces
	//------------------------------------------------------------
	
	protected boolean canCloseWindow() {
		return true;
	}
	
	protected void closeWindow() {
		if (canCloseWindow()) {
			this.dispose();
		}
	}

	//------------------------------------------------------------
	// Events
	//------------------------------------------------------------
	
	//************************************************************
	// Component events
	//************************************************************
	
	protected void onWindowResized(ComponentEvent e) {}
	
	protected void onWindowMoved(ComponentEvent e) {}
	
	protected void onWindowShown(ComponentEvent e) {}
	
	protected void onWindowHidden(ComponentEvent e) {}
	
	//************************************************************
	// Window events
	//************************************************************
	
	protected void onWindowOpened(WindowEvent e) {}
	
	protected boolean onWindowClosing(WindowEvent e) { return canCloseWindow(); }
	
	protected void onWindowClosed(WindowEvent e) {}
	
	protected void onWindowIconified(WindowEvent e) {}
	
	protected void onWindowDeiconified(WindowEvent e) {}
	
	protected void onWindowActivated(WindowEvent e) {}
	
	protected void onWindowDeactivated(WindowEvent e) {}
	
	//************************************************************
	// Window state events
	//************************************************************
	
	protected void onWindowStateChanged(WindowEvent e) {}
	
	//************************************************************
	// Window focus events
	//************************************************************
	
	protected void onWindowGainedFocus(WindowEvent e) {}
	
	protected void onWindowLostFocus(WindowEvent e) {}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * ウィンドウが閉じられた後、{@link #onWindowClosed(WindowEvent)} が呼び出された後に、
	 * アプリケーションを終了させるために呼び出されるイベントハンドラ。
	 * 通常、このメソッドでは、{@link java.lang.System#exit(int)} に 0 を渡して呼び出す。
	 * @param e	イベントオブジェクト
	 * @since 3.2.2
	 */
	protected void onShutdownApplication(WindowEvent e) {
		System.exit(0);
	}
	
	protected boolean isComponentEventsEnabled() {
		return (_listenerComponentEvents != null);
	}
	
	protected void enableComponentEvents(boolean toEnable) {
		if (!isComponentEventsEnabled() && toEnable) {
			_listenerComponentEvents = new ComponentListener() {
				public void componentResized(ComponentEvent e) {
					saveWindowSizeOnResized(e);
					onWindowResized(e);
				}
				public void componentMoved(ComponentEvent e) {
					saveWindowLocationOnMoved(e);
					onWindowMoved(e);
				}
				public void componentShown(ComponentEvent e) {
					onWindowShown(e);
				}
				public void componentHidden(ComponentEvent e) {
					onWindowHidden(e);
				}
			};
			addComponentListener(_listenerComponentEvents);
		}
		else if (isComponentEventsEnabled() && !toEnable) {
			removeComponentListener(_listenerComponentEvents);
			_listenerComponentEvents = null;
		}
	}
	
	protected boolean isWindowEventsEnabled() {
		return (_listenerWindowEvents != null);
	}

	protected void enableWindowEvents(boolean toEnable) {
		if (!isWindowEventsEnabled() && toEnable) {
			_listenerWindowEvents = new WindowListener() {
				public void windowOpened(WindowEvent e) {
					// ウィンドウの位置とサイズを必要なら調整(3.2.2)
					adjustWindowBoundsWhenOpened(e);
					onWindowOpened(e);
				}
				public void windowClosing(WindowEvent e) {
					boolean beClose = onWindowClosing(e);
//					int opClose = (beClose ? JFrame.DISPOSE_ON_CLOSE : JFrame.DO_NOTHING_ON_CLOSE);
//					setDefaultCloseOperation(opClose);
					if (beClose) {
						dispose();
					}
				}
				public void windowClosed(WindowEvent e) {
					onWindowClosed(e);
					onShutdownApplication(e);
				}
				public void windowIconified(WindowEvent e) {
					onWindowIconified(e);
				}
				public void windowDeiconified(WindowEvent e) {
					onWindowDeiconified(e);
				}
				public void windowActivated(WindowEvent e) {
					onWindowActivated(e);
				}
				public void windowDeactivated(WindowEvent e) {
					onWindowDeactivated(e);
				}
			};
			addWindowListener(_listenerWindowEvents);
		}
		else if (isWindowEventsEnabled() && !toEnable) {
			removeWindowListener(_listenerWindowEvents);
			_listenerWindowEvents = null;
		}
	}

	protected boolean isWindowStateEventsEnabled() {
		return (_listenerWindowStateEvents != null);
	}

	protected void enableWindowStateEvents(boolean toEnable) {
		if (!isWindowStateEventsEnabled() && toEnable) {
			_listenerWindowStateEvents = new WindowStateListener() {
				public void windowStateChanged(WindowEvent e) {
			    	onWindowStateChanged(e);
			    }
			};
			addWindowStateListener(_listenerWindowStateEvents);
		}
		else if (isWindowStateEventsEnabled() && !toEnable) {
			removeWindowStateListener(_listenerWindowStateEvents);
			_listenerWindowStateEvents = null;
		}
	}

	protected boolean isWindowFocusEventsEnabled() {
		return (_listenerWindowFocusEvents != null);
	}

	protected void enableWindowFocusEvents(boolean toEnable) {
		if (!isWindowFocusEventsEnabled() && toEnable) {
			_listenerWindowFocusEvents = new WindowFocusListener() {
				public void windowGainedFocus(WindowEvent e) {
					onWindowGainedFocus(e);
				}
				public void windowLostFocus(WindowEvent e) {
					onWindowLostFocus(e);
				}
			};
			addWindowFocusListener(_listenerWindowFocusEvents);
		}
		else if (isWindowFocusEventsEnabled() && !toEnable) {
			removeWindowFocusListener(_listenerWindowFocusEvents);
			_listenerWindowFocusEvents = null;
		}
	}
}
