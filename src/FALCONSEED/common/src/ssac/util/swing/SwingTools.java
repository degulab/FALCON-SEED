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
 * @(#)SwingTools.java	3.2.2	2015/10/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SwingTools.java	3.2.0	2015/06/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SwingTools.java	2.0.0	2012/10/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SwingTools.java	1.19	2012/02/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SwingTools.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SwingTools.java	1.10	2009/02/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SwingTools.java	1.10	2009/01/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SwingTools.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import ssac.util.Objects;
import ssac.util.Validations;
import ssac.util.logging.AppLogger;

/**
 * Swing用ユーティリティ
 *
 * 
 * @version 3.2.2
 */
public class SwingTools
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final KeyStroke[] EMPTY_STROKES = new KeyStroke[0];
	static private final InputMap[] EMPTY_INPUTMAPS = new InputMap[0];
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたコンポーネントが所属する、ダイアログオブジェクトを取得する。
	 * このメソッドでは、最も近い <code>Window</code> コンポーネントを検索し、
	 * その <code>Window</code> コンポーネントが <code>Dialog</code> でなければ <tt>null</tt> を返す。
	 * @param component		起点となるコンポーネント
	 * @return	直近のオーナーである <code>Dialog</code> オブジェクト、見つからなければ <tt>null</tt>
	 * @since 2.0.0
	 */
	static public Dialog getDialogAncestor(Component component) {
		Dialog ret = null;
		if (component != null) {
			Window w = SwingUtilities.getWindowAncestor(component);
			if (w instanceof Dialog) {
				ret = (Dialog)w;
			}
		}
		return ret;
	}
	
	/**
	 * 指定されたコンポーネントが所属する、フレームオブジェクトを取得する。
	 * このメソッドでは、最も近い <code>Window</code> コンポーネントを検索し、
	 * その <code>Window</code> コンポーネントが <code>Frame</code> でなければ <tt>null</tt> を返す。
	 * @param component		起点となるコンポーネント
	 * @return	直近のオーナーである <code>Frame</code> オブジェクト、見つからなければ <tt>null</tt>
	 * @since 2.0.0
	 */
	static public Frame getFrameAncestor(Component component) {
		Frame ret = null;
		if (component != null) {
			Window w = SwingUtilities.getWindowAncestor(component);
			if (w instanceof Frame) {
				ret = (Frame)w;
			}
		}
		return ret;
	}

	/**
	 * 指定されたコンポーネントが所属する Window を返す。
	 * @since 1.14
	 */
	static public Window getWindowForComponent(Component parentComponent)
	throws HeadlessException
	{
		if (parentComponent == null) {
			return JOptionPane.getRootFrame();
		}
		if (parentComponent instanceof Frame || parentComponent instanceof Dialog) {
			return (Window)parentComponent;
		}
		
		return getWindowForComponent(parentComponent.getParent());
	}
	
	/**
	 * 指定されたクラス名の Look & Feel を設定する。
	 * Look & Feel のクラス名に null を指定した場合は、システムのデフォルトが
	 * 使用される。
	 * 
	 * @param LFName Look & Feel の完全クラス名
	 * @return 正しく設定された場合に true を返す。
	 */
	static public boolean setupLookAndFeel(String LFName) {
		if (LFName != null) {
			if (LFName.equalsIgnoreCase("none")) {
				// この場合は、デフォルトの設定を行わない
				// Debug
				AppLogger.debug("Not set Look & Feel.");
				return true;
			}

			// 指定のLook&Feelを設定
			if (setup_look_and_feel(LFName))
				return true;
		}

		// Setup default look&feel
		String osname = System.getProperty( "os.name");
		if ( 0 <= osname.indexOf( "Windows") || 0 <= osname.indexOf( "Mac")) {
			return setup_look_and_feel( UIManager.getSystemLookAndFeelClassName());
		}
		else {
			if ( setup_look_and_feel( "javax.swing.plaf.metal.MetalLookAndFeel"))
				return true;

			return setup_look_and_feel( UIManager.getSystemLookAndFeelClassName());
		}
	}

	/**
	 * JTreeコンポーネントの接続線表示状態を取得する。
	 * このメソッドが返す状態は、表示／非表示設定の状態であり、
	 * Ｌ＆Ｆによってはこの設定が反映されないものもある。
	 * @return	接続線が表示される場合は <tt>true</tt>、
	 * 			表示されない場合は <tt>false</tt> を返す。
	 * @since 1.14
	 */
	static public final boolean isPaintedTreeLines() {
		Boolean painted = (Boolean)UIManager.get("Tree.paintLines");
		return painted.booleanValue();
	}

	/**
	 * JTreeコンポーネントの接続線の表示／非表示を設定する。
	 * このメソッドが設定する状態は、表示／非表示設定の状態であり、
	 * Ｌ＆Ｆによってはこの設定が反映されないものもある。
	 * @param painted	接続線を表示する場合は <tt>true</tt>、
	 * 					表示しない場合は <tt>false</tt> を指定する。
	 * @since 1.14
	 */
	static public final void setPaintTreeLines(boolean painted) {
		UIManager.put("Tree.paintLines", Boolean.valueOf(painted));
	}

	/**
	 * エディタ用のデフォルトフォントを取得する。
	 * 基本的に等幅フォントを選択する。
	 * 
	 * @return Font インスタンス
	 */
	static public Font getDefaultEditorFont() {
		return getDefaultEditorFont(10);
	}
	
	static public Font getDefaultEditorFont(int defFontSize) {
		final String[] defFamily = {
			"ＭＳ ゴシック",
		};
		
		// 利用可能なフォントを取得
		GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontFamilyNames = genv.getAvailableFontFamilyNames();
		HashSet<String> ffSet = new HashSet<String>(Arrays.asList(fontFamilyNames));
		
		// 利用可能なフォントを生成
		String fname = null;
		for (String s : defFamily) {
			if (ffSet.contains(s)) {
				fname = s;
				break;
			}
		}
		if (fname == null) {
			fname = "Monospaced";
		}
		Font defFont = new Font(fname, Font.PLAIN, defFontSize);
		
		return defFont;
	}

	/**
	 * クリップボードに格納されているコンテンツを取得する。
	 * @param requestor
	 * @return	クリップボードに存在するコンテンツを表す <code>Transferable</code> オブジェクト。
	 * 			コンテンツが存在しない場合は <tt>null</tt>
	 */
	static public Transferable getSystemClipboardContents(Component requestor) {
		Clipboard clip;
		Transferable trans;
		if (requestor != null) {
			clip = requestor.getToolkit().getSystemClipboard();
			trans = clip.getContents(requestor);
		} else {
			clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			trans = clip.getContents(null);
		}
		return trans;
	}

	/**
	 * クリップボードに文字データが存在しているかを検証する。
	 *
	 * @param requestor
	 * @return クリップボードに文字データが存在していれば true
	 */
	static public boolean existStringInClipboard(Component requestor) {
		// 起動直後のシステムでも、空のデータがクリップボードから取得できるが、
		// 念のため、null 対策
		Transferable trans = getSystemClipboardContents(requestor);
		boolean ret = (trans != null && trans.isDataFlavorSupported(DataFlavor.stringFlavor));
		return ret;
	}

	/**
	 * クリップボードにファイルリストが存在しているかを検証する。
	 * @param requestor
	 * @return	クリップボードにファイルリストが存在していれば <tt>true</tt>
	 */
	static public boolean existFileListInClipboard(Component requestor) {
		// 起動直後のシステムでも、空のデータがクリップボードから取得できるが、
		// 念のため、null 対策
		Transferable trans = getSystemClipboardContents(requestor);
		boolean ret = (trans != null && trans.isDataFlavorSupported(DataFlavor.javaFileListFlavor));
		return ret;
	}

	/**
	 * 指定されたテキストコンポーネントにおける現在のキャレット位置を
	 * 含む行番号を取得する。行番号は、1から始まるものとする。
	 * 
	 * @param component 対象のテキストコンポーネント
	 * @return キャレットが存在する行の 1 から始まる行番号
	 */
	static public int getLineAtCaret(JTextComponent component) {
		int caretPos = component.getCaretPosition();
		Element root = component.getDocument().getDefaultRootElement();
		return root.getElementIndex(caretPos)+1;
	}

	/**
	 * ドキュメントの先頭にキャレットを移動する。
	 * 
	 * @param component 対象のテキストコンポーネント
	 */
	static public void setCaretToBegin(JTextComponent component) {
		component.setCaretPosition(0);
	}

	/**
	 * ドキュメントの先頭にキャレットを移動する。
	 * 
	 * @param component 対象のテキストコンポーネント
	 */
	static public void setCaretToEnd(JTextComponent component) {
		Document doc = component.getDocument();
		component.setCaretPosition(doc.getLength());
	}

	/**
	 * 指定された行の先頭にキャレットを移動する。
	 * 行番号は、1から始まるものとする。
	 * 
	 * @param component 対象のテキストコンポーネント
	 * @param lineNo キャレット移動先となる行の 1 から始まる行番号
	 */
	static public void setCaretToLine(JTextComponent component, int lineNo) {
		Document doc = component.getDocument();
		Element root = doc.getDefaultRootElement();
		int targetLineNo = Math.max(1, Math.min(root.getElementCount(), lineNo));
		Element elem = root.getElement(targetLineNo-1);
		component.setCaretPosition(elem.getStartOffset());
	}

	/**
	 * 現在の画面解像度を取得する。
	 * 
	 * @return 画面解像度
	 */
	static public Rectangle getLocalDisplayRectangle() {
		GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		DisplayMode dm = genv.getDefaultScreenDevice().getDisplayMode();
		return new Rectangle(dm.getWidth(), dm.getHeight());
	}

	/**
	 * 中央に配置された Window の最大の境界を返します。
	 * これらの境界はタスクバーやメニューバーなどのネイティブのウィンドウ処理システムの
	 * オブジェクトを処理します。返された境界は 1 つの例外を持つ単一のディスプレイに
	 * 常駐します。すべてのディスプレイを通して Window が中央に配置されるマルチスクリーン
	 * システムの場合、このメソッドは全体のディスプレイ領域の境界を返します。
	 * 
	 * @return
	 */
	static public Rectangle getLocalDesktopRectangle() {
		GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return genv.getMaximumWindowBounds();
	}

	/**
	 * すべてのスクリーンの、最大領域を取得する。
	 * この最大領域には、タスクバーなどの領域も含まれる。
	 * @return	スクリーンの領域の配列
	 * @since 1.19
	 */
	static public Rectangle[] getAllScreenBounds() {
		GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defdev = genv.getDefaultScreenDevice();
		GraphicsDevice[] devs = genv.getScreenDevices();
		Rectangle[] rects = new Rectangle[devs.length];
		int rcIndex = 0;
		//--- default screen device
		rects[rcIndex++] = defdev.getDefaultConfiguration().getBounds();
		//--- other screen devices
		for (int i = 0; i < devs.length; i++) {
			GraphicsDevice gd = devs[i];
			if (gd != defdev) {
				rects[rcIndex++] = gd.getDefaultConfiguration().getBounds();
			}
		}
		return rects;
	}

	/**
	 * すべてのスクリーンの、表示可能な最大領域を取得する。
	 * この最大領域は、タスクバーなどの領域を除外した領域となる。
	 * @return	スクリーンの表示可能な領域の配列
	 * @since 1.19
	 */
	static public Rectangle[] getAllScreenDesktopBounds() {
		GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defdev = genv.getDefaultScreenDevice();
		GraphicsDevice[] devs = genv.getScreenDevices();
		Rectangle[] rects = new Rectangle[devs.length];
		int rcIndex = 0;
		//--- default screen device
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(defdev.getDefaultConfiguration());
		Rectangle rc = defdev.getDefaultConfiguration().getBounds();
		rc.x += insets.left;
		rc.y += insets.top;
		rc.width  -= (insets.left + insets.right);
		rc.height -= (insets.top + insets.bottom);
		rects[rcIndex++] = rc;
		//--- other screen devices
		for (int i = 0; i < devs.length; i++) {
			GraphicsDevice gd = devs[i];
			if (gd != defdev) {
				rc = gd.getDefaultConfiguration().getBounds();
				rects[rcIndex++] = rc;
			}
		}
		return rects;
	}

	/** 最小ウィンドウサイズ **/
	static protected final Dimension MIN_WINDOW_SIZE	= new Dimension(50, 50);

	/**
	 * 指定された領域を、現在のスクリーンで表示可能な位置に調整する。
	 * この調整では、サイズと位置が指定されており、ウィンドウのコーナーのうち 2 点以上がスクリーン外にある場合のみ、
	 * ウィンドウがあるスクリーンの中央へ表示するように位置を調整する。
	 * ここで指定された領域の座標系は、スクリーン座標系とみなす。
	 * @param window		対象ウィンドウオブジェクト
	 * @param screenBounds	調整する領域
	 * @param minSize		サイズが未定義の場合に利用する最小サイズ情報、不要の場合は <tt>null</tt>
	 * @return	調整されたスクリーン座標系での領域
	 * @throws NullPointerException	<em>window</em> もしくは <em>restoreScreenBounds</em> が <tt>null</tt> の場合
	 * @since 3.2.2
	 */
	static public WindowRectangle adjustWindowLocationIntoScreen(Window window, WindowRectangle screenBounds, Dimension minSize) {
		return adjustWindowLocationIntoScreen(window, screenBounds.getLocation(), screenBounds.getSize(), minSize);
	}

	/**
	 * 指定された領域を、現在のスクリーンで表示可能な位置に調整する。
	 * この調整では、サイズと位置が指定されており、ウィンドウのコーナーのうち 2 点以上がスクリーン外にある場合のみ、
	 * ウィンドウがあるスクリーンの中央へ表示するように位置を調整する。
	 * ここで指定された領域の座標系は、スクリーン座標系とみなす。
	 * @param window		対象ウィンドウオブジェクト
	 * @param screenPos		調整する領域の位置、もしくは <tt>null</tt>
	 * @param size			調整する領域のサイズ、もしくは <tt>null</tt>
	 * @param minSize		サイズが未定義の場合に利用する最小サイズ情報、不要の場合は <tt>null</tt>
	 * @return	調整されたスクリーン座標系での領域
	 * @throws NullPointerException	<em>window</em> が <tt>null</tt> の場合
	 * @since 3.2.2
	 */
	static public WindowRectangle adjustWindowLocationIntoScreen(Window window, Point screenPos, Dimension size, Dimension minSize) {
		// 座標系チェック
		boolean coordScreen;
		try {
			coordScreen = Objects.isEqual(window.getLocation(), window.getLocationOnScreen());
		} catch (Throwable ignoreEx) {
			coordScreen = true;
		}
		
		// 位置とサイズの両方が指定されない場合は、位置サイズともに未定義の領域を返す
		if (screenPos == null && size == null) {
			return new WindowRectangle();
		}
		
		// 最小サイズ
		int mw, mh;
		if (minSize != null) {
			mw = Math.max(minSize.width , MIN_WINDOW_SIZE.width);
			mh = Math.max(minSize.height, MIN_WINDOW_SIZE.height);
		} else {
			mw = MIN_WINDOW_SIZE.width;
			mh = MIN_WINDOW_SIZE.height;
		}
		
		// 初期サイズ
		int tw, th;
		if (size != null) {
			tw = Math.max(size.width , mw);
			th = Math.max(size.height, mh);
		} else {
			tw = mw;
			th = mh;
		}
		
		// 初期位置
		int tx1, ty1, tx2, ty2;
		if (screenPos != null) {
			tx1 = screenPos.x;
			ty1 = screenPos.y;
		} else {
			tx1 = 0;
			ty1 = 0;
		}
		{
			long ltx2 = (long)tx1 + (long)tw - 1L;	// 表示される領域の頂点として調整
			long lty2 = (long)ty1 + (long)th - 1L;	// 表示される領域の頂点として調整
			if (ltx2 > Integer.MAX_VALUE) ltx2 = Integer.MAX_VALUE;
			if (lty2 > Integer.MAX_VALUE) lty2 = Integer.MAX_VALUE;
			tx2 = (int)ltx2;
			ty2 = (int)lty2;
		}
		
		// スクリーン領域に頂点が含まれるかを判定
		int[][] points = {
				{ tx1, ty1 },	// Left-top
				{ tx1, ty2 },	// Left-bottom
				{ tx2, ty2 },	// Right-bottom
				{ tx2, ty1 },	// Right-top
		};
		Rectangle[] insides = {null, null, null, null};
		Rectangle[] screens = getAllScreenDesktopBounds();
		int insideCount = 0;
		for (Rectangle rc : screens) {
			//--- 判定
			for (int i = 0; i < points.length; ++i) {
				if (insides[i]==null && rc.contains(points[i][0], points[i][1])) {
					// point in rect
					insides[i] = rc;
					++insideCount;
				}
			}
		}
		
		// 調整
		WindowRectangle wrc = new WindowRectangle();
		if (insideCount >= 3) {
			// スクリーン領域に含まれる頂点が 3 点以上の場合は、そのままとする
			if (screenPos != null)
				wrc.setLocation(tx1, ty1);
			if (size != null)
				wrc.setSize(tw, th);
		}
		else if (screenPos != null) {
			// 1 辺が含まれる場合
			Rectangle centerScreen = null;
			if (insides[0] != null && insides[3] != null) {
				// 上辺が含まれる場合、上辺の中央が含まれているスクリーンをターゲットとする
				int cx = (points[0][0] + points[3][0]) / 2;
				int cy = (points[0][1] + points[3][1]) / 2;
				centerScreen = (insides[0].contains(cx, cy) ? insides[0] : insides[3]);
			}
			else if (insides[1] != null && insides[2] != null) {
				// 下辺が含まれる場合、下辺の中央が含まれているスクリーンをターゲットとする
				int cx = (points[1][0] + points[2][0]) / 2;
				int cy = (points[1][1] + points[2][1]) / 2;
				centerScreen = (insides[1].contains(cx, cy) ? insides[1] : insides[2]);
			}
			else if (insides[0] != null && insides[1] != null) {
				// 左辺が含まれる場合、左辺の中央が含まれているスクリーンをターゲットとする
				int cx = (points[0][0] + points[1][0]) / 2;
				int cy = (points[0][1] + points[1][1]) / 2;
				centerScreen = (insides[0].contains(cx, cy) ? insides[0] : insides[1]);
			}
			else if (insides[2] != null && insides[3] != null) {
				// 右辺が含まれる場合、右辺の中央が含まれているスクリーンをターゲットとする
				int cx = (points[2][0] + points[3][0]) / 2;
				int cy = (points[2][1] + points[3][1]) / 2;
				centerScreen = (insides[2].contains(cx, cy) ? insides[2] : insides[3]);
			}
			//--- 辺中央が含まれているスクリーンの中央に配置
			if (centerScreen == null) {
				centerScreen = screens[0];	// 対象スクリーンがない場合は、メインスクリーン
				screenPos = null;	// この場合、位置は不定とする
			}
			//--- X 座標
			if (tw > centerScreen.width) {
				tx1 = centerScreen.x;
				tw  = centerScreen.width;
			} else {
				tx1 = (centerScreen.width - tw) / 2 + centerScreen.x;
			}
			//--- Y 座標
			if (th > centerScreen.height) {
				ty1 = centerScreen.y;
				th  = centerScreen.height;
			} else {
				ty1 = (centerScreen.height - th) / 2 + centerScreen.y;
			}
			//--- 反映
			if (size != null)
				wrc.setSize(tw, th);
			if (screenPos != null)
				wrc.setLocation(tx1, ty1);
		}
		else if (size != null) {
			// サイズのみの場合は、メインスクリーンに収まるサイズに修正
			if (tw > screens[0].width)
				tw = screens[0].width;
			if (th > screens[0].height)
				th = screens[0].height;
			wrc.setSize(tw, th);
		}
		// else : 上記以外では、位置とサイズともに無しとする
		
		// 必要なら、座標系変換
		if (wrc.getLocation() != null && !coordScreen) {
			//--- スクリーン座標を変換
			Window ownerWindow = window.getOwner();
			if (ownerWindow != null) {
				SwingUtilities.convertPointFromScreen(wrc.getLocation(), ownerWindow);
			}
		}
		
		// 完了
		return wrc;
	}

	/**
	 * 指定されたウィンドウのコーナーが 2 点以上スクリーン外にある場合に、ウィンドウの中心が位置するスクリーンに収まるよう、位置とサイズを調整する。
	 * @param window	対象のウィンドウ
	 * @return	調整された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 3.2.2
	 */
	static public boolean adjustWindowBoundsIntoWindowCenterScreen(Window window) {
		// ノーマルフレームでの処理を行う
		if (window instanceof Frame) {
			Frame frame = (Frame)window;
			if (frame.getExtendedState() != Frame.NORMAL) {
				// ノーマルフレームではないので、現在の位置とサイズの調整は行わない
				return false;
			}
		}
		
		// 念のため、座標系チェック
		boolean coordScreen = Objects.isEqual(window.getLocation(), window.getLocationOnScreen());

		// 現在の位置とサイズを取得
		Rectangle rcWindow = new Rectangle(window.getLocationOnScreen(), window.getSize());
		Rectangle[] screens = getAllScreenDesktopBounds();

		// 初期位置
		int tx1 = rcWindow.x;
		int ty1 = rcWindow.y;
		int tx2, ty2;
		{
			long ltx2 = (long)tx1 + (long)rcWindow.width  - 1L;	// 表示される領域の頂点として調整
			long lty2 = (long)ty1 + (long)rcWindow.height - 1L;	// 表示される領域の頂点として調整
			if (ltx2 > Integer.MAX_VALUE) ltx2 = Integer.MAX_VALUE;
			if (lty2 > Integer.MAX_VALUE) lty2 = Integer.MAX_VALUE;
			tx2 = (int)ltx2;
			ty2 = (int)lty2;
		}

		// 含まれる位置の確認
		int[][] points = {
				{ tx1, ty1 },	// Left-top
				{ tx1, ty2 },	// Left-bottom
				{ tx2, ty2 },	// Right-bottom
				{ tx2, ty1 },	// Right-tom
				{ (int)rcWindow.getCenterX(), (int)rcWindow.getCenterY() },	// Center
		};
		Rectangle[] insides = { null, null, null, null, null };
		int insideCornerCount = 0;
		for (Rectangle rc : screens) {
			for (int i = 0; i < points.length; ++i) {
				if (insides[i] == null && rc.contains(points[i][0], points[i][1])) {
					insides[i] = rc;
					if (i < 4) {
						// コーナー座標のみ、カウントする
						++insideCornerCount;
					}
				}
			}
		}
		
		// 調整が不要なら終了
		if (insideCornerCount >= 3) {
			// コーナーが 3 点以上含まれていれば、そのまま
			return false;
		}
		else if (insides[4] != null && (insides[0] != null || insides[3] != null)) {
			// 中央が含まれていて、左上か右上のどちらかが含まれている場合も、移動可能とみなし、そのまま
			return false;
		}
		
		// 位置調整
		Rectangle rcTarget = insides[4];	// Rect in center
		if (rcTarget == null) {
			rcTarget = screens[0];	// Main screen
		}
		//--- X 座標
		if (rcWindow.width >= rcTarget.width) {
			rcWindow.width = rcTarget.width;
			rcWindow.x = rcTarget.x;
		} else {
			rcWindow.x = rcTarget.x + ((rcTarget.width - rcWindow.width) / 2);
		}
		//--- Y 座標
		if (rcWindow.height >= rcTarget.height) {
			rcWindow.height = rcTarget.height;
			rcWindow.y = rcTarget.y;
		} else {
			rcWindow.y = rcTarget.y + ((rcTarget.height - rcWindow.height) / 2);
		}
		
		// ウィンドウへ反映
		if (!coordScreen) {
			//--- スクリーン座標を変換
			Window ownerWindow = window.getOwner();
			if (ownerWindow != null) {
				Point pt = rcWindow.getLocation();
				SwingUtilities.convertPointFromScreen(pt, ownerWindow);
				rcWindow.x = pt.x;
				rcWindow.y = pt.y;
			}
		}
		//--- 位置とサイズを設定
		window.setSize(rcWindow.width, rcWindow.height);
		window.setLocation(rcWindow.x, rcWindow.y);
		return true;
	}

//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された矩形領域のどれかに、指定された位置座標が含まれるかどうかを判定する。
//	 * @param bounds	矩形領域の配列
//	 * @param pos		判定する位置座標
//	 * @return	指定された位置がどれかの矩形に含まれている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
//	 */
//	static protected boolean _containsInRectangles(final Rectangle[] bounds, final Point pos) {
//		for (Rectangle rc : bounds) {
//			if (rc.contains(pos)) {
//				return true;
//			}
//		}
//		return false;
//	}
//	
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された位置が、指定されたどれかの領域内に含まれているかを判定する。
//	 * <p>
//	 * 引数が <tt>null</tt> の場合は、<tt>false</tt> を返す。
//	 * @param bounds	判定に使用する領域の配列
//	 * @param pos	判定する位置
//	 * @return	指定された位置がどれかの領域に含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 1.19
//	 */
//	static public boolean containsInBounds(final Rectangle[] bounds, final Point pos) {
//		if (pos == null || bounds == null)
//			return false;
//		
//		return _containsInRectangles(bounds, pos);
//	}
//	
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された位置が、表示可能な領域内に存在するかを検査する。
//	 * このメソッドでは、タスクバーなどの領域も含まれる。
//	 * <p>
//	 * 引数が <tt>null</tt> の場合は、<tt>false</tt> を返す。
//	 * @param pos	判定する位置
//	 * @return	指定された位置が表示可能領域に含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 1.19
//	 */
//	static public boolean containsInAllScreenBounds(final Point pos) {
//		return containsInBounds(getAllScreenBounds(), pos);
//	}
//
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された位置が、表示可能な領域内に存在するかを検査する。
//	 * このメソッドでは、タスクバーなどの領域は表可能領域から除外して判定する。
//	 * <p>
//	 * 引数が <tt>null</tt> の場合は、<tt>false</tt> を返す。
//	 * @param pos	判定する位置
//	 * @return	指定された位置が表示可能領域に含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 1.19
//	 */
//	static public boolean containsInAllScreenDesktopBounds(Point pos) {
//		return containsInBounds(getAllScreenDesktopBounds(), pos);
//	}
//	
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された矩形の四隅すべてが、指定されたどれかの領域内に含まれているかを判定する。
//	 * <p>
//	 * 引数が <tt>null</tt> の場合は、<tt>false</tt> を返す。
//	 * @param bounds	判定に使用する領域の配列
//	 * @param rect	判定する領域
//	 * @return	四隅すべてがどれかの領域に含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 1.19
//	 */
//	static public boolean containsInBounds(final Rectangle[] bounds, final Rectangle rect) {
//		if (rect == null || bounds == null || bounds.length <= 0)
//			return false;
//
//		Point[] cps = new Point[]{
//				new Point(rect.x, rect.y),
//				new Point(rect.x, rect.y+rect.height),
//				new Point(rect.x+rect.width, rect.y),
//				new Point(rect.x+rect.width, rect.y+rect.height),
//		};
//		for (Point cp : cps) {
//			if (!_containsInRectangles(bounds, cp)) {
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された矩形の四隅が、表示可能な領域内に存在するかを検査する。
//	 * このメソッドでは、タスクバーなどの領域も含まれる。
//	 * <p>
//	 * 引数が <tt>null</tt> の場合は、<tt>false</tt> を返す。
//	 * @param rect	判定する領域
//	 * @return	四隅がすべて表示可能領域内に含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 1.19
//	 */
//	static public boolean containsInAllScreenBounds(Rectangle rect) {
//		return containsInBounds(getAllScreenBounds(), rect);
//	}
//
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された矩形の四隅が、表示可能な領域内に存在するかを検査する。
//	 * このメソッドでは、タスクバーなどの領域は表可能領域から除外して判定する。
//	 * <p>
//	 * 引数が <tt>null</tt> の場合は、<tt>false</tt> を返す。
//	 * @param rect	判定する領域
//	 * @return	四隅がすべて表示可能領域内に含まれている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 1.19
//	 */
//	static public boolean containsInAllScreenDesktopBounds(Rectangle rect) {
//		return containsInBounds(getAllScreenDesktopBounds(), rect);
//	}
//
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された矩形を、指定された領域内に収める。
//	 * 指定された領域に収まらない場合は、指定された領域配列の先頭要素の領域内に納める。
//	 * @param bounds	判定に使用する領域の配列
//	 * @param rect	調整する領域
//	 * @return	調整後の領域を返す。調整できない場合は <tt>null</tt> を返す。
//	 * @since 1.19
//	 */
//	static public Rectangle convertIntoAllBounds(final Rectangle[] bounds, final Rectangle rect) {
//		if (rect==null || bounds==null || bounds.length <= 0)
//			return null;
//		
//		Rectangle result = null;
//		for (Rectangle rc : bounds) {
//			Rectangle dest = rc.intersection(rect);
//			if (!dest.isEmpty()) {
//				if (result == null) {
//					result = dest;
//				} else {
//					result.add(dest);
//				}
//			}
//		}
//		if (result==null || result.isEmpty()) {
//			return null;
//		}
//		
//		if (containsInBounds(bounds, result)) {
//			// included all corners in all screens
//			return result;
//		} else {
//			// intersection for Maximum bounds
//			result = bounds[0].intersection(result);
//			return (result.isEmpty() ? null : result);
//		}
//	}
//	
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された矩形を、タスクバーなどの領域を含むすべての表示可能領域内に収める。
//	 * 表示可能領域に収まらない場合は、メインの表示領域内に収める。
//	 * @param bounds	判定に使用する領域の配列
//	 * @param rect	調整する領域
//	 * @return	調整後の領域を返す。調整できない場合は <tt>null</tt> を返す。
//	 * @since 1.19
//	 */
//	static public Rectangle convertIntoAllScreenBounds(final Rectangle rect) {
//		return convertIntoAllBounds(getAllScreenBounds(), rect);
//	}
//	
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された矩形を、タスクバーなどの領域を除くすべての表示可能領域内に収める。
//	 * 表示可能領域に収まらない場合は、メインの表示領域内に収める。
//	 * @param bounds	判定に使用する領域の配列
//	 * @param rect	調整する領域
//	 * @return	調整後の領域を返す。調整できない場合は <tt>null</tt> を返す。
//	 * @since 1.19
//	 */
//	static public Rectangle convertIntoAllScreenDesktopBounds(final Rectangle rect) {
//		return convertIntoAllBounds(getAllScreenDesktopBounds(), rect);
//	}
//
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された位置とサイズを、指定された領域に収まる矩形に変換する。
//	 * <p><em>size</em> が <tt>null</tt> の場合は <em>defSize</em> に指定されたサイズを使用する。
//	 * <em>size</em> と <em>defSize</em> のどちらも <tt>null</tt> の場合は、(50,50) のサイズとする。
//	 * <p><em>pos</em> が <tt>null</tt> の場合、<em>bounds</em> の先頭要素の領域の中心位置とする。
//	 * もし、サイズが先頭要素の領域よりも大きい場合、位置は (0,0) とする。
//	 * @param bounds	判定に使用する領域の配列
//	 * @param pos		調整する位置
//	 * @param size		調整するサイズ
//	 * @param defSize	デフォルトのサイズ
//	 * @return	変換後の領域を返す。変換できない場合は <tt>null</tt> を返す。
//	 * @since 1.19
//	 */
//	static public Rectangle convertIntoAllBounds(final Rectangle[] bounds, final Point pos, final Dimension size, final Dimension defSize) {
//		Rectangle rect = new Rectangle();
//		//--- setup Size
//		if (size == null) {
//			if (defSize == null) {
//				rect.setSize(50, 50);
//			} else {
//				rect.setSize(defSize);
//			}
//		}
//		else {
//			rect.setSize(size);
//		}
//		//--- setup Location
//		if (pos == null) {
//			int x = 0, y = 0;
//			if (bounds!=null && bounds.length > 0 && !bounds[0].isEmpty()) {
//				if (bounds[0].width > rect.width) {
//					x = (bounds[0].width - rect.width) / 2 + bounds[0].x;
//				}
//				if (bounds[0].height > rect.height) {
//					y = (bounds[0].height - rect.height) / 2 + bounds[0].y;
//				}
//			}
//			rect.setLocation(x, y);
//		}
//		else {
//			rect.setLocation(pos);
//		}
//		//--- check rectangle
//		if (rect.isEmpty()) {
//			return null;
//		}
//		
//		return convertIntoAllBounds(bounds, rect);
//	}
//	
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された位置とサイズを、タスクバーなどの領域を含むすべての表示可能領域内に収まる矩形に変換する。
//	 * <p><em>size</em> が <tt>null</tt> の場合は <em>defSize</em> に指定されたサイズを使用する。
//	 * <em>size</em> と <em>defSize</em> のどちらも <tt>null</tt> の場合は、(50,50) のサイズとする。
//	 * <p><em>pos</em> が <tt>null</tt> の場合、メインの表示領域の中心位置とする。
//	 * もし、サイズがメインの表示領域よりも大きい場合、位置は (0,0) とする。
//	 * @param pos		調整する位置
//	 * @param size		調整するサイズ
//	 * @param defSize	デフォルトのサイズ
//	 * @return	変換後の領域を返す。変換できない場合は <tt>null</tt> を返す。
//	 * @since 1.19
//	 */
//	static public Rectangle convertIntoAllScreenBounds(final Point pos, final Dimension size, final Dimension defSize) {
//		return convertIntoAllBounds(getAllScreenBounds(), pos, size, defSize);
//	}
//	
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 指定された位置とサイズを、タスクバーなどの領域を除くすべての表示可能領域内に収まる矩形に変換する。
//	 * <p><em>size</em> が <tt>null</tt> の場合は <em>defSize</em> に指定されたサイズを使用する。
//	 * <em>size</em> と <em>defSize</em> のどちらも <tt>null</tt> の場合は、(50,50) のサイズとする。
//	 * <p><em>pos</em> が <tt>null</tt> の場合、メインの表示領域の中心位置とする。
//	 * もし、サイズがメインの表示領域よりも大きい場合、位置は (0,0) とする。
//	 * @param pos		調整する位置
//	 * @param size		調整するサイズ
//	 * @param defSize	デフォルトのサイズ
//	 * @return	変換後の領域を返す。変換できない場合は <tt>null</tt> を返す。
//	 * @since 1.19
//	 */
//	static public Rectangle convertIntoAllScreenDesktopBounds(final Point pos, final Dimension size, final Dimension defSize) {
//		return convertIntoAllBounds(getAllScreenDesktopBounds(), pos, size, defSize);
//	}
//
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 座標値を現在のデスクトップ領域内に収める。
//	 * このメソッドでは、最終位置の右下(20,20)の領域を残す。
//	 * 
//	 * @param pos 修正する位置情報
//	 * @return 修正後の位置情報
//	 */
//	static public Point convertIntoDesktop(Point pos) {
//		return convertIntoDesktop(pos, new Dimension(20,20));
//	}
//
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * 座標値を現在のデスクトップ領域内に収める。
//	 * このメソッドでは、最終位置の右下に指定されたサイズの領域を残す。
//	 * 
//	 * @param pos 修正する位置情報
//	 * @param size 最低確報領域
//	 * @return 修正後の位置情報
//	 */
//	static public Point convertIntoDesktop(Point pos, Dimension size) {
//		// pos == null なら、そのまま返す
//		if (pos == null) {
//			return null;
//		}
//		
//		// size == null なら、デフォルト
//		if (size == null) {
//			size = new Dimension(20,20);
//		}
//		
//		// デスクトップ領域内の位置取得
//		Rectangle rc = getLocalDesktopRectangle();
//		int mr = rc.x + rc.width - size.width;
//		int mb = rc.y + rc.height - size.height;
//		int px = Math.max(rc.x, Math.min(mr, pos.x));
//		int py = Math.max(rc.y, Math.min(mb, pos.y));
//		if (pos.x == px && pos.y == py)
//			return pos;
//		else
//			return new Point(px, py);
//	}
//
//	/**
//	 * @deprecated	このメソッドは削除予定。
//	 * サイズを現在のデスクトップ領域内に収める。
//	 * 
//	 * @param size 修正するサイズ情報
//	 * @return 修正後のサイズ情報
//	 */
//	static public Dimension convertIntoDesktop(Dimension size) {
//		// size == null なら、そのまま返す
//		if (size == null) {
//			return null;
//		}
//		
//		// デスクトップ領域内に収まるサイズ取得
//		Rectangle rc = getLocalDesktopRectangle();
//		int sw = Math.min(rc.width, size.width);
//		int sh = Math.min(rc.height, size.height);
//		if (size.width == sw && size.height == sh)
//			return size;
//		else
//			return new Dimension(sw, sh);
//	}
	
	static public String getOmittedString(FontMetrics fm, String text, int limitWidth) {
		if (text == null || fm == null || limitWidth <= 0) {
			return text;
		}

		String strText = text;
		int w = fm.stringWidth(strText);
		if (w > limitWidth) {
			int sw = (fm.getMaxAdvance() > 0 ? fm.getMaxAdvance() : fm.stringWidth("W")) * 3;
			int mhw = (limitWidth - sw) / 2;
			int bi, ei, tcw;
			//--- front
			for (tcw = 0, bi = 0; bi < strText.length(); bi++) {
				tcw += fm.charWidth(strText.charAt(bi));
				if (tcw > mhw) {
					--bi;
					break;
				}
			}
			//--- end
			for (tcw = 0, ei = strText.length() - 1; ei >= 0; ei--) {
				tcw += fm.charWidth(strText.charAt(ei));
				if (tcw > mhw) {
					++ei;
					break;
				}
			}
			//--- make
			String bstr = strText.substring(0, bi);
			String estr = strText.substring(ei);
			strText = bstr + "..." + estr;
		}
		return strText;
	}
	
	static public void registerActionToMaps(InputMap imap, ActionMap amap, KeyStroke keyStroke, Object name, Action action) {
		Validations.validNotNull(imap, "InputMap instance is null.");
		Validations.validNotNull(amap, "ActionMap instance is null.");
		Validations.validNotNull(keyStroke, "KeyStroke instance is null.");
		Validations.validNotNull(name, "String instance is null.");
		Validations.validNotNull(action, "Action instance is null.");
		
		imap.put(keyStroke, name);
		amap.put(name, action);
	}

	/**
	 * コンポーネントの InputMap でサポートするコンディションかを判定する。
	 * @param condition				判定するコンディション
	 * @param supportedConditions	サポートするコンディションの配列
	 * @return	<em>condition</em> が <em>supportedConditions</em> に含まれている場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt> を返す。
	 * @since 1.14
	 */
	static public boolean isSupportedInputMapCondition(int condition, int...supportedConditions) {
		if (supportedConditions != null && supportedConditions.length > 0) {
			for (int si : supportedConditions) {
				if (si == condition) {
					return true;
				}
			}
		}
		return false;	// not supported
	}

	/**
	 * 指定されたすべてのコンディションについて、UI が <em>component</em> のコンディションに
	 * 指定した InputMap の配列を返す。 
	 * @param component		対象のコンポーネント
	 * @param conditions	取得するコンディションの配列
	 * @return	取得した InputMap の配列を返す。コンディションに対応する InputMap が
	 * 			設定されていない場合は空の配列を返す。
	 * @throws NullPointerException	<em>component</em> が <tt>null</tt> の場合
	 * @since 1.14
	 */
	static public InputMap[] getUIInputMaps(JComponent component, int...conditions) {
		if (conditions != null && conditions.length > 0) {
			ArrayList<InputMap> imlist = new ArrayList<InputMap>(conditions.length);
			for (int cond : conditions) {
				InputMap imap = SwingUtilities.getUIInputMap(component, cond);
				if (imap != null) {
					imlist.add(imap);
				}
			}
			if (imlist.isEmpty()) {
				return EMPTY_INPUTMAPS;
			} else {
				return imlist.toArray(new InputMap[imlist.size()]);
			}
		} else {
			return EMPTY_INPUTMAPS;
		}
	}

	/**
	 * メニューバーに登録されているメニュー項目から、指定されたキーストロークに
	 * 対応するアクションリスナーを取得する。
	 * 
	 * @param menuBar	対象のメニューバー
	 * @param ks		キーストローク
	 * @return	キーストロークに対応するアクションリスナー。対応するリスナーが存在しない場合は <tt>null</tt> を返す。
	 * 
	 * @since 1.10
	 */
	static public ActionListener getMenuActionForKeyStroke(JMenuBar menuBar, KeyStroke ks) {
		if (menuBar == null) {
			return null;
		}
		
		MenuElement[] subElements = menuBar.getSubElements();
		for (MenuElement elem : subElements) {
			ActionListener al = getMenuActionForKeyStrokeRecursive(elem, ks);
			if (al != null) {
				return al;
			}
		}
		
		//--- not found
		return null;
	}
	
	static private ActionListener getMenuActionForKeyStrokeRecursive(MenuElement menuElement, KeyStroke ks) {
		if (menuElement == null) {
			return null;
		}
		
		Component comp = menuElement.getComponent();
		if (comp instanceof JComponent) {
			ActionListener al = ((JComponent)comp).getActionForKeyStroke(ks);
			if (al != null) {
				return al;
			}
		}
		
		MenuElement[] subElements = menuElement.getSubElements();
		for (MenuElement elem : subElements) {
			ActionListener al = getMenuActionForKeyStrokeRecursive(elem, ks);
			if (al != null) {
				return al;
			}
		}
		
		//--- not found
		return null;
	}

	/**
	 * 指定されたメニューバーから、メニュー項目に登録されているアクセラレータキーのキーストロークを収集する。
	 * <code>ignoreStrokes</code> が指定された場合、収集したキーストロークから <code>ignoreStrokes</code> に
	 * 含まれる全てのキーストロークを除外する。
	 * 
	 * @param menuBar	アクセラレータキーを収集するメニューバー
	 * @param ignoreStrokes	結果から除外するキーストローク
	 * @return	メニューバーに含まれるアクセラレータキーのキーストロークを格納した配列を返す。
	 * 			アクセラレータキーが存在しないか、全てのキーストロークが場外された場合は要素が空の配列を返す。
	 * 
	 * @since 1.10
	 */
	static public KeyStroke[] getMenuAccelerators(JMenuBar menuBar, KeyStroke...ignoreStrokes) {
		if (ignoreStrokes != null && ignoreStrokes.length > 0) {
			return getMenuAccelerators(menuBar, Arrays.asList(ignoreStrokes));
		} else {
			return getMenuAccelerators(menuBar, Collections.<KeyStroke>emptyList());
		}
	}
	
	/**
	 * 指定されたメニューバーから、メニュー項目に登録されているアクセラレータキーのキーストロークを収集する。
	 * <code>ignoreStrokes</code> が指定された場合、収集したキーストロークから <code>ignoreStrokes</code> に
	 * 含まれる全てのキーストロークを除外する。
	 * 
	 * @param menuBar	アクセラレータキーを収集するメニューバー
	 * @param ignoreStrokes	結果から除外するキーストロークのコレクション
	 * @return	メニューバーに含まれるアクセラレータキーのキーストロークを格納した配列を返す。
	 * 			アクセラレータキーが存在しないか、全てのキーストロークが場外された場合は要素が空の配列を返す。
	 * 
	 * @since 1.10
	 */
	static public KeyStroke[] getMenuAccelerators(JMenuBar menuBar, Collection<KeyStroke> ignoreStrokes) {
		// Check
		if (menuBar == null)
			throw new NullPointerException("JMenuBar instance is null.");
		
		// create buffer for exist KeyStroke
		HashSet<KeyStroke> strokes = new HashSet<KeyStroke>();
		
		// collect menu accelerator key strokes
		MenuElement[] subElements = menuBar.getSubElements();
		for (MenuElement elem : subElements) {
			getMenuAccelerators(elem, strokes);
		}
		
		// remove ignore strokes from exist strokes
		if (!strokes.isEmpty() && ignoreStrokes != null && !ignoreStrokes.isEmpty()) {
			strokes.removeAll(ignoreStrokes);
		}
		
		// return strokes
		if (strokes.isEmpty()) {
			return EMPTY_STROKES;
		} else {
			return strokes.toArray(new KeyStroke[strokes.size()]);
		}
	}
	
	static private void getMenuAccelerators(MenuElement menuElement, Set<KeyStroke> existStrokes) {
		if (menuElement == null) {
			return;
		}
		
		Component comp = menuElement.getComponent();
		if (comp instanceof JMenuItem) {
			KeyStroke ks = ((JMenuItem)comp).getAccelerator();
			if (ks != null) {
				existStrokes.add(ks);
			}
		}
		
		MenuElement[] subElements = menuElement.getSubElements();
		for (MenuElement elem : subElements) {
			getMenuAccelerators(elem, existStrokes);
		}
	}
	
	//
	// for Dimension
	//

	/**
	 * <em>dest</em> の幅と高さを、<em>src</em> の幅と高さによって拡張します。
	 * <em>dest</em> の幅と高さが小さくなることはありません。
	 * @param dest	拡張する <code>Dimension</code> オブジェクト
	 * @param src	拡張の基準とする <code>Dimension</code> オブジェクト
	 * @since 2.0.0
	 */
	static public void expand(Dimension dest, Dimension src) {
		if (dest.width < src.width)
			dest.width = src.width;
		if (dest.height < src.height)
			dest.height = src.height;
	}

	/**
	 * <em>dest</em> の幅と高さを、<em>src</em> の幅と高さによって縮小します。
	 * <em>dest</em> の幅と高さが大きくなることはありません。
	 * @param dest	縮小する <code>Dimension</code> オブジェクト
	 * @param src	縮小の基準とする <code>Dimension</code> オブジェクト
	 * @since 2.0.0
	 */
	static public void shrink(Dimension dest, Dimension src) {
		if (dest.width > src.width)
			dest.width = src.width;
		if (dest.height > src.height)
			dest.height = src.height;
	}

	/**
	 * 指定された <code>Dimension</code> を比較し、最小の幅と高さを持つ新しい <code>Dimension</code> を返します。
	 * @param a		<code>Dimension</code> の一方
	 * @param b		<code>Dimension</code> のもう一方
	 * @return	最小の幅と高さを持つ <code>Dimension</code>
	 * @since 2.0.0
	 */
	static public Dimension intersection(Dimension a, Dimension b) {
		return new Dimension(Math.min(a.width, b.width), Math.min(a.height, b.height));
	}
	
	/**
	 * 指定された <code>Dimension</code> を比較し、最大の幅と高さを持つ新しい <code>Dimension</code> を返します。
	 * @param a		<code>Dimension</code> の一方
	 * @param b		<code>Dimension</code> のもう一方
	 * @return	最大の幅と高さを持つ <code>Dimension</code>
	 * @since 2.0.0
	 */
	static public Dimension union(Dimension a, Dimension b) {
		return new Dimension(Math.max(a.width, b.width), Math.max(a.height, b.height));
	}

	/**
	 * ツールバーに表示するラベルコンポーネントを生成する。
	 * このメソッドでは、ラベルのボーダーに上下左右 2 ピクセルずつの余白を設定する。
	 * @param caption	ラベルに表示する文字列
	 * @return	生成されたラベルコンポーネント
	 * @since 3.2.0
	 */
	static public JLabel createToolBarLabel(String caption) {
		JLabel lbl = new JLabel(caption);
		lbl.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		return lbl;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected boolean setup_look_and_feel(String look_and_feel) {
		try {
			UIManager.setLookAndFeel( look_and_feel);
		} catch (ClassNotFoundException e) {
			AppLogger.debug(e);
			return false;
		} catch (InstantiationException e) {
			AppLogger.debug(e);
			return false;
		} catch (IllegalAccessException e) {
			AppLogger.debug(e);
			return false;
		} catch (UnsupportedLookAndFeelException e) {
			AppLogger.debug(e);
			return false;
		}
		
		// Debug
		AppLogger.debug("[Look & Feel]" + look_and_feel);
		
		return true;
	}
}
