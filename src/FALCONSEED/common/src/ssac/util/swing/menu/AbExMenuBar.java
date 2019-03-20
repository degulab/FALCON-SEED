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
 * @(#)AbExMenuBar.java	3.2.2	2015/10/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbExMenuBar.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.menu;

import static ssac.util.Validations.validNotNull;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ssac.util.Strings;

/**
 * 拡張メニューバーの共通実装を含む抽象クラス。
 * 拡張メニューバーを利用する場合は、このクラスをサブクラス化し、
 * ハンドラを実装すること。
 * 
 * @version 3.2.2
 * @since 1.14
 */
public abstract class AbExMenuBar extends JMenuBar
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -4064723317045298075L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * このメニューバーのイベントを処理するハンドラ
	 */
	protected IMenuHandler menuHandler = null;

	/**
	 * コマンド名とメニューアクションとのマップ
	 */
	protected final Map<String,Action> menuActionMap = new HashMap<String,Action>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbExMenuBar(final IMenuHandler handler) {
		super();
		this.menuHandler = handler;
		initMenu();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このメニューバーに設定されているメニューハンドラを取得する。
	 * @return	設定されているメニューハンドラのインスタンスを返す。
	 */
	public IMenuHandler getMenuHandler() {
		return menuHandler;
	}

	/**
	 * このメニューバーに新しいメニューハンドラを設定する。
	 * @param handler	新たに設定するメニューハンドラ
	 */
	public void setMenuHandler(IMenuHandler handler) {
		this.menuHandler = handler;
	}

	/**
	 * 定義されているメニューアクションを取得する。
	 * @param command	検索キーとなるコマンド名
	 * @return	コマンド名に対応するメニューアクションを返す。
	 * 			アクションが未定義の場合は <tt>null</tt> を返す。
	 */
	public Action getMenuAction(String command) {
		return menuActionMap.get(command);
	}

	/**
	 * 指定されたアクションを、新たにコマンド名に関連付ける。
	 * 指定したコマンド名にすでに関連付けられたアクションが定義されている場合、
	 * 指定されたアクションで既存のアクションを置き換える。
	 * 
	 * @param command	関連付けるコマンド名
	 * @param action	メニューアクション
	 * @return	コマンド名に対応する既存アクションを返す。
	 * アクションが未定義の場合は <tt>null</tt> を返す。
	 * @throws NullPointerException	<code>action</code> が <tt>null</tt> の場合
	 */
	public Action setMenuAction(String command, Action action) {
		return menuActionMap.put(command, validNotNull(action));
	}

	/**
	 * メニューバーに設定されているメニューから、コマンド文字列に対応するメニュー項目を取得する。
	 * コマンド文字列に対応するメニュー項目がない場合は <tt>null</tt> を返す。
	 * <p>
	 * このメソッドは、メニューバーのメニューを検索し、指定されたコマンド文字列を保持する
	 * メニュー項目のうち、最初に見つかったインスタンスを返す。
	 * 
	 * @param command	検索するキーとなるコマンド文字列
	 * @return	コマンド文字列に対応するメニュー項目を返す。存在しない場合は <tt>null</tt> を返す。
	 */
	public JMenuItem getMenuItemByCommand(String command) {
		if (Strings.isNullOrEmpty(command))
			return null;
		
		return JMenus.getMenuItemByCommand(this, command);
	}

	/**
	 * 指定されたインデックスのメニューの状態を更新する。
	 * <p>
	 * このメソッドは、指定された位置にあるメニュー(<code>JMenu</code>)の全てのメニュー項目に
	 * 対して更新イベントを発生させる。この更新イベントは、メニューバーに登録されているハンドラに
	 * 通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 * 
	 * @param menuIndex		メニューバーのメニュー位置を示すインデックス。先頭は 0。
	 * 
	 * @throws ArrayIndexOutOfBoundsException メニューインデックスが範囲外の場合
	 */
	public void updateMenuItem(int menuIndex) {
		JMenu menu = getMenu(menuIndex);
		if (menu != null) {
			fireMenuItemUpdate(menu);
		}
	}

	/**
	 * 指定されたコマンド文字列に対応するメニューの状態を更新する。
	 * <p>
	 * このメソッドは、指定されたコマンド文字列に対応するメニュー項目を検索し、その項目に
	 * 対して更新イベントを発生させる。コマンド文字列に対応するものがメニュー(<code>JMenu</code>)の
	 * 場合は、そのメニューに含まれる全てのメニュー項目に対して更新イベントを発生させる。
	 * この更新イベントは、メニューバーに登録されているハンドラに通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 * 
	 * @param command	検索するキーとなるコマンド文字列
	 */
	public void updateMenuItem(String command) {
		if (Strings.isNullOrEmpty(command))
			return;	
		
		JMenuItem item = JMenus.getMenuItemByCommand(this, command);
		if (item != null) {
			fireMenuItemUpdate(item);
		}
	}
	
	/**
	 * 全てのメニュー項目の状態を更新する。
	 * <p>
	 * このメソッドは、このメニューバーに登録されている全てのメニュー項目に対して
	 * 更新イベントを発生させる。この更新イベントは、メニューバーに登録されている
	 * ハンドラに通知される。
	 * <p>
	 * 基本的に、コマンド文字列が登録されていない項目は除外される。
	 */
	public void updateAllMenuItems() {
		int len = getMenuCount();
		for (int i = 0; i < len; i++) {
			JMenu menu = getMenu(i);
			if (menu != null) {
				fireMenuItemUpdate(menu);
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このメニューバーを初期化する。
	 */
	abstract protected void initMenu();

	/**
	 * コマンド文字列を格納するメニューアイテムの場合、そのメニューアイテムの
	 * 更新イベント・ハンドラを呼び出す。指定されたメニューアイテムが
	 * メニュー(<code>JMenu</code>)の場合、そのメニューに含まれる全てのメニュー
	 * アイテムについて再帰的に処理する。
	 * 
	 * @param item	更新イベント対象のメニューアイテム
	 */
	protected void fireMenuItemUpdate(JMenuItem item) {
		// call event handler for current item
		String command = item.getActionCommand();
		if (!Strings.isNullOrEmpty(command)) {
			fireMenuUpdatePerformed(command, item);
		}
		
		// call event handler, if item is JMenu
		if (item instanceof JMenu) {
			JMenu menu = (JMenu)item;
			int len = menu.getItemCount();
			for (int i = 0; i < len; i++) {
				JMenuItem childItem = menu.getItem(i);
				if (childItem != null) {
					fireMenuItemUpdate(childItem);
				}
			}
		}
	}

	/**
	 * アクションイベントにより、メニューハンドラのメニューアクションを
	 * 実行する。
	 * 
	 * @param メニューアクションのイベント
	 */
	protected void fireMenuActionPerformed(ActionEvent e) {
		if (menuHandler != null) {
			menuHandler.menuActionPerformed(e);
		}
	}

	/**
	 * メニュー更新要求により、メニューハンドラのメニュー更新アクションを
	 * 実行する。
	 * 
	 * @param command	コマンド
	 * @param source	更新対象の <code>JMenuItem</code> インスタンス
	 */
	protected void fireMenuUpdatePerformed(String command, Object source) {
		if (menuHandler != null) {
			menuHandler.menuUpdatePerformed(command, source);
		}
	}

	/**
	 * 指定されたメニューリソースから、このメニューバー専用アクションを
	 * 生成する。
	 * <p><b>注：</b>
	 * <blockquote>
	 * このメソッドで生成されたアクションは、このメニューバーの
	 * メニューアクションマップには登録されない。
	 * </blockquote>
	 * @param mir	アクションに反映するメニューリソース
	 * @return	新しいアクション
	 */
	protected Action createMenuAction(MenuItemResource mir) {
		return new AbMenuItemAction(mir){
			public void actionPerformed(ActionEvent e) {
				fireMenuActionPerformed(e);
			}
		};
	}

	/**
	 * 指定されたアクションを持つメニュー項目を生成する。
	 * <p><b>注：</b>
	 * <blockquote>
	 * このメソッドで生成されたアクションは、このメニューバーの
	 * メニューアクションマップには登録されない。
	 * </blockquote>
	 * @param action	メニュー項目に設定するアクション
	 * @return	生成されたメニュー項目
	 */
	protected JMenuItem createMenuItem(Action action) {
		return new ExMenuItem(true, false, action);
	}

	/**
	 * 指定されたメニューリソースから、アクションを持たない
	 * メニュー項目を生成する。
	 * @param mir	メニューリソース
	 * @return	生成されたメニュー項目
	 */
	protected JMenuItem createMenuItem(MenuItemResource mir) {
		return new ExMenuItem(true, false, mir);
	}

	/**
	 * 指定されたアクションでメニュー項目を生成する。
	 * このメソッドは、指定されたアクションがコマンド名を保持している
	 * 場合、このアクションをメニューアクションマップに登録する。
	 * 同名のコマンドがメニューアクションマップに登録されている場合は、
	 * 指定されたアクションに置き換える。
	 * @param action	メニュー項目に設定するアクション
	 * @return	生成されたメニュー項目
	 */
	protected JMenuItem createActionMenuItem(Action action) {
		String cmd = AbMenuItemAction.getCommandKey(action);
		JMenuItem item = new ExMenuItem(true, false, action);
		if (!Strings.isNullOrEmpty(cmd)) {
			menuActionMap.put(cmd, action);
		}
		return item;
	}

	/**
	 * 指定されたメニューリソースから新しいアクションを生成し、
	 * そのアクションでメニュー項目を生成する。
	 * このメソッドは、指定されたメニューリソースがコマンド名を保持している
	 * 場合、生成された新しいアクションをメニューアクションマップに登録する。
	 * 同名のコマンドがメニューアクションマップに登録されている場合は、
	 * このメソッドで生成されたアクションに置き換える。
	 * @param mir	メニューリソース
	 * @return	生成されたメニュー項目
	 */
	protected JMenuItem createActionMenuItem(MenuItemResource mir) {
		Action action = createMenuAction(mir);
		JMenuItem item = createActionMenuItem(action);
		return item;
	}

	/**
	 * 指定されたメニューリソースから、アクションを持たないメニューを
	 * 生成する。
	 * @param mir	メニューリソース
	 * @return	生成されたメニュー
	 */
	protected JMenu createMenu(MenuItemResource mir) {
		JMenu menu = mir.createMenu();
		return menu;
	}

	/**
	 * 指定されたメニューアイテムのアクションを、このメニューバーのコマンドマップから除外する。
	 * @param mitem	対象のメニューアイテム
	 * @return	メニューアイテムのアクションがコマンドマップから除外された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 3.2.2
	 */
	protected boolean releaseActionMenuItem(JMenuItem mitem) {
		return releaseMenuAction(mitem.getAction());
	}

	/**
	 * 指定されたアクションを、このメニューバーのコマンドマップから除外する。
	 * @param action	対象のアクション
	 * @return	対象のアクションがコマンドマップから除外された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 3.2.2
	 */
	protected boolean releaseMenuAction(Action action) {
		if (action != null) {
			String cmd = AbMenuItemAction.getCommandKey(action);
			if (!Strings.isNullOrEmpty(cmd)) {
				Action oldAction = menuActionMap.get(cmd);
				if (action == oldAction) {
					menuActionMap.remove(cmd);
					return true;
				}
			}
		}
		// not registered
		return false;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
