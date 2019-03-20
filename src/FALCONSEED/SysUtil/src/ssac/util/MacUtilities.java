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
 */
/*
 * @(#)MacUtilities.java	2012/07/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util;

import java.awt.Image;
import java.awt.PopupMenu;
import java.lang.reflect.Method;

import ssac.util.mac.MacScreenMenuHandler;

/**
 * Mac OS X 専用ユーティリティ
 * 
 * @version 2012/07/02
 */
public class MacUtilities
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String APP_DOCK_ICON_PATH	= "local.app.dock.icon.path";
	
	static public final String MAC_USE_SCREENMENUBAR	= "apple.laf.useScreenMenuBar";
	static public final String MAC_ABOUT_NAME = "com.apple.mrj.application.apple.menu.about.name";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private MacUtilities() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このアプリケーションプラットフォームが Mac OS の場合に <tt>true</tt> を返す。
	 * @return	Mac なら <tt>true</tt>
	 */
	static public boolean isMac() {
		String osname = System.getProperty("os.name").toLowerCase();
		return osname.startsWith("mac");
	}

	/**
	 * このアプリケーションプラットフォームが Mac OS X の場合に <tt>true</tt> を返す。
	 * @return	Mac OS X なら <tt>true</tt>
	 */
	static public boolean isMacOSX() {
		String osname = System.getProperty("os.name").toLowerCase();
		return osname.startsWith("mac os x");
	}

	/**
	 * Mac OS で、スクリーンメニューを表示する VM オプション文字列を返す。
	 * @return	VMオプションとして指定可能な文字列
	 */
	static public String vmOptionUseScreenMenuBar() {
		return "-D" + MAC_USE_SCREENMENUBAR + "=true";
	}

	/**
	 * Mac OS X で、スクリーンメニューに表示するアプリケーション名を設定する VM オプション文字列を返す。
	 * @param appName	アプリケーション名
	 * @return	VMオプションとして指定可能な文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public String vmOptionSetDockAppName(String appName) {
		return "-Xdock:name=" + appName;
	}

	/**
	 * Mac OS X で、Dock のアイコンを設定する VM オプション文字列を返す。
	 * @param path	アイコンファイルのパス
	 * @return	VMオプションとして指定可能な文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public String vmOptionSetDockIcon(String path) {
		return "-Xdock:icon=" + path;
	}

	/**
	 * Dock アイコンとして使用するファイルパスを通知するためのシステムプロパティを設定する VM オプション文字列を返す。
	 * @param path	アイコンファイルのパス
	 * @return	VMオプションとして指定可能な文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public String vmOptionSetDockIconPath(String path) {
		return "-D" + APP_DOCK_ICON_PATH + "=" + path;
	}

	/**
	 * スクリーンメニューのシステムメニュー項目に対応するハンドラを設定する。
	 * このメソッドでハンドリングするイベントは、About と Quit のみ。
	 * @param handler	<code>MacScreenMenuHandler</code> オブジェクト
	 */
	static public void setupScreenMenuHandler(final MacScreenMenuHandler handler)
	{
		if (isMac()) {
			try {
				Class<?> clz = Class.forName("ssac.util.mac.MacApplicationDelegator");
				Method mtd = clz.getMethod("setupScreenMenuHandler", MacScreenMenuHandler.class);
				mtd.invoke(null, handler);
			}
			catch (Throwable ex) {
				throw new RuntimeException("Failed to invoke ssac.util.mac.MacApplicationDelegator#setupScreenMenuHandler()", ex);
			}
		}
	}
	
	static public String getDockIconPath() {
		String str = System.getProperty(APP_DOCK_ICON_PATH);
		if (str != null && str.length() > 0) {
			return str;
		} else {
			return null;
		}
	}
	
	static public String getApplicationName() {
		String str = System.getProperty(MAC_ABOUT_NAME);
		if (str != null && str.length() > 0) {
			return str;
		} else {
			return null;
		}
	}
	
	static public void setApplicationName(String appName) {
		if (appName != null && appName.length() > 0) {
			System.setProperty(MAC_ABOUT_NAME, appName);
		} else {
			System.clearProperty(MAC_ABOUT_NAME);
		}
	}
	
	static public boolean getUseScreenMenuBar() {
		String str = System.getProperty(MAC_USE_SCREENMENUBAR);
		if (str != null && str.length() > 0) {
			return Boolean.valueOf(str);
		} else {
			return false;
		}
	}
	
	static public void setUseScreenMenuBar(boolean toUse) {
		System.setProperty(MAC_USE_SCREENMENUBAR, toUse ? "true" : "false");
	}
	
	static public PopupMenu getDockMenu() {
		if (isMacOSX()) {
			Object ret = null;
			try {
				Class<?> clz = Class.forName("ssac.util.mac.MacApplicationDelegator");
				Method mtd = clz.getMethod("getDockMenu");
				ret = mtd.invoke(null);
			}
			catch (Throwable ex) {
				throw new RuntimeException("Failed to invoke ssac.util.mac.MacApplicationDelegator#getDockMenu()", ex);
			}
			return (PopupMenu)ret;
		} else {
			return null;
		}
	}
	
	static public Image getDockIconImage() {
		if (isMacOSX()) {
			Object ret = null;
			try {
				Class<?> clz = Class.forName("ssac.util.mac.MacApplicationDelegator");
				Method mtd = clz.getMethod("getDockIconImage");
				ret = mtd.invoke(null);
			}
			catch (Throwable ex) {
				throw new RuntimeException("Failed to invoke ssac.util.mac.MacApplicationDelegator#getDockIconImage()", ex);
			}
			return (Image)ret;
		} else {
			return null;
		}
	}
	
	static public void setDockMenu(PopupMenu pmenu) {
		if (isMacOSX()) {
			try {
				Class<?> clz = Class.forName("ssac.util.mac.MacApplicationDelegator");
				Method mtd = clz.getMethod("setDockMenu", PopupMenu.class);
				mtd.invoke(null, pmenu);
			}
			catch (Throwable ex) {
				throw new RuntimeException("Failed to invoke ssac.util.mac.MacApplicationDelegator#setDockMenu()", ex);
			}
		}
	}
	
	static public void setDockIconImage(Image icon) {
		if (isMacOSX()) {
			try {
				Class<?> clz = Class.forName("ssac.util.mac.MacApplicationDelegator");
				Method mtd = clz.getMethod("setDockIconImage", Image.class);
				mtd.invoke(null, icon);
			}
			catch (Throwable ex) {
				throw new RuntimeException("Failed to invoke ssac.util.mac.MacApplicationDelegator#setDockIconImage()", ex);
			}
		}
	}
	
	static public void setDockIconBadge(String badge) {
		if (isMacOSX()) {
			try {
				Class<?> clz = Class.forName("ssac.util.mac.MacApplicationDelegator");
				Method mtd = clz.getMethod("setDockIconBadge", String.class);
				mtd.invoke(null, badge);
			}
			catch (Throwable ex) {
				throw new RuntimeException("Failed to invoke ssac.util.mac.MacApplicationDelegator#setDockIconBadge()", ex);
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	
//	{
//		list.add( Constant._macJava);
//		//if ( System.getProperty( Constant._system_default_file_encoding, "").equals( ""))
//			list.add( "-Dfile.encoding=UTF-8");
//		list.add( "-D" + Constant._systemDefaultFileEncoding + "=" + System.getProperty( Constant._systemDefaultFileEncoding, ""));
//		list.add( "-X" + Constant._macScreenMenuName + "=SOARS VisualShell");
//		//list.add( "-D" + Constant._mac_screen_menu_name + "=SOARS VisualShell");
//		list.add( "-X" + Constant._macIconFilename + "=../resource/icon/application/visualshell/icon.png");
//		list.add( "-D" + Constant._macScreenMenu + "=true");
//	}
	
//	public static boolean setup_screen_menu_handler(IMacScreenMenuHandler macScreenMenuHandler, Frame frame, String title) {
//		if ( 0 > System.getProperty( "os.name").indexOf( "Mac"))
//			return true;
//
//		List resultList = new ArrayList();
//		if ( !Reflection.execute_static_method( "soars.common.utility.swing.mac.MacScreenMenuHandler", "setup", new Class[] { IMacScreenMenuHandler.class}, new Object[] { macScreenMenuHandler}, resultList)
//			|| resultList.isEmpty() || null == resultList.get( 0) || !( resultList.get( 0) instanceof Boolean) || !(( Boolean)resultList.get( 0)).booleanValue()) {
//			JOptionPane.showMessageDialog( frame,
//				"Mac error : Reflection.execute_static_method( ... )\n"
//				+ " Class name : soars.common.utility.swing.mac.MacScreenMenuHandler\n"
//				+ " Method name : setup\n",
//				title,
//			JOptionPane.ERROR_MESSAGE);
//			return false;
//		}
//
//		return true;
//	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
