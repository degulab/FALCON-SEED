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
 * @(#)JMenus.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)JMenus.java	1.10	2008/12/01
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.menu;

import static ssac.util.Validations.validNotNull;
import static ssac.util.Validations.validArgument;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import ssac.util.Strings;

/**
 * Swing Menu 操作用ユーティリティ。
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.10
 */
public final class JMenus
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private JMenus() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたメニューバーに含まれる項目から、指定されたコマンド名と一致する
	 * 項目を検索し、最初に見つかった項目を返す。
	 * このメソッドは、指定されたメニューバーの階層を順次検索し、指定されたコマンド名を
	 * もつメニュー項目を見つける。コマンド名と一致する項目が <code>JMenu</code> の
	 * 場合もある。
	 * 
	 * @param menuBar	検索対象のメニューバー
	 * @param command	検索キーとなるコマンド名
	 * @return 指定されたコマンド名を持つメニュー項目もしくはメニューを返す。
	 * 			見つからない場合は <tt>null</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>menuBar</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<code>command</code> が <tt>null</tt> もしくは空文字列の場合
	 */
	static public JMenuItem getMenuItemByCommand(JMenuBar menuBar, String command) {
		validNotNull(menuBar);
		validArgument(!Strings.isNullOrEmpty(command));
		
		int len = menuBar.getMenuCount();
		for (int i = 0; i < len; i++) {
			JMenu menu = menuBar.getMenu(i);
			if (menu != null) {
				JMenuItem item = findMenuItemByCommand(menu, command);
				if (item != null) {
					return item;
				}
			}
		}
		
		// not found
		return null;
	}
	
	/**
	 * 指定されたポップアップメニューに含まれる項目から、指定されたコマンド名と一致する
	 * 項目を検索し、最初に見つかった項目を返す。
	 * このメソッドは、指定されたポップアップメニューの階層を順次検索し、指定されたコマンド名を
	 * もつメニュー項目を見つける。コマンド名と一致する項目が <code>JMenu</code> の
	 * 場合もある。
	 * 
	 * @param popupMenu	検索対象のポップアップメニュー
	 * @param command	検索キーとなるコマンド名
	 * @return 指定されたコマンド名を持つメニュー項目もしくはメニューを返す。
	 * 			見つからない場合は <tt>null</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>popupMenu</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<code>command</code> が <tt>null</tt> もしくは空文字列の場合
	 * 
	 * @since 1.14
	 */
	static public JMenuItem getMenuItemByCommand(JPopupMenu popupMenu, String command) {
		validNotNull(popupMenu);
		validArgument(!Strings.isNullOrEmpty(command));
		
		MenuElement[] elements = popupMenu.getSubElements();
		if (elements != null && elements.length > 0) {
			return findMenuItemByCommand(elements, command);
		} else {
			return null;
		}
	}

	/**
	 * 指定されたメニューに含まれる項目から、指定されたコマンド名と一致する
	 * 項目を検索し、最初に見つかった項目を返す。
	 * このメソッドは、指定されたメニューの階層を順次検索し、指定されたコマンド名を
	 * もつメニュー項目を見つける。コマンド名と一致する項目が <code>JMenu</code> の
	 * 場合もある。
	 * 
	 * @param menu	検索を開始するメニュー
	 * @param command	検索キーとなるコマンド名
	 * @return 指定されたコマンド名を持つメニュー項目もしくはメニューを返す。
	 * 			見つからない場合は <tt>null</tt> を返す。
	 * 
	 * @throws NullPointerException	<code>menu</code> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<code>command</code> が <tt>null</tt> もしくは空文字列の場合
	 */
	static public JMenuItem getMenuItemByCommand(JMenu menu, String command) {
		validNotNull(menu);
		validArgument(!Strings.isNullOrEmpty(command));
		
		return findMenuItemByCommand(menu, command);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * 指定されたメニューに含まれる項目から、指定されたコマンド名と一致する
	 * 項目を検索し、最初に見つかった項目を返す。
	 * このメソッドは、指定されたメニューの階層を順次検索し、指定されたコマンド名を
	 * もつメニュー項目を見つける。コマンド名と一致する項目が <code>JMenu</code> の
	 * 場合もある。
	 * <p><b>注：</b>
	 * <blockquote>
	 * このメソッドは、引数がすべて有効であることを前提とする。
	 * </blockquote>
	 * 
	 * @param menu	検索を開始するメニュー
	 * @param command	検索キーとなるコマンド名
	 * @return 指定されたコマンド名を持つメニュー項目もしくはメニューを返す。
	 * 			見つからない場合は <tt>null</tt> を返す。
	 */
	static private JMenuItem findMenuItemByCommand(JMenu menu, String command) {
		// judge JMenu
		if (command.equals(menu.getActionCommand())) {
			return menu;
		}
		
		// judge menu items
		int len = menu.getItemCount();
		for (int i = 0; i < len; i++) {
			JMenuItem item = menu.getItem(i);
			if (item instanceof JMenu) {
				JMenuItem subitem = findMenuItemByCommand((JMenu)item, command);
				if (subitem != null) {
					return subitem;
				}
			}
			else if (item != null) {
				if (command.equals(item.getActionCommand())) {
					return item;
				}
			}
		}
		
		// not found
		return null;
	}
	
	static private JMenuItem findMenuItemByCommand(MenuElement[] elements, String command) {
		for (MenuElement elem : elements) {
			if (elem instanceof JMenu) {
				JMenuItem subitem = findMenuItemByCommand((JMenu)elem, command);
				if (subitem != null) {
					return subitem;
				}
			}
			else if (elem instanceof JMenuItem) {
				JMenuItem item = (JMenuItem)elem;
				if (command.equals(item.getActionCommand())) {
					return item;
				}
			}
		}
		
		// not found
		return null;
	}
}
