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
 * @(#)MenuItemResource.java	1.00	2008/11/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.menu;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * メニューアイテムのリソースを保持するクラス。
 * 
 * @version 1.00	2008/11/28
 */
public class MenuItemResource
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * コマンド文字列
	 */
	protected String		command;
	/**
	 * 説明文字列
	 */
	protected String		name;
	/**
	 * ツールチップテキスト
	 */
	protected String		tooltip;
	/**
	 * 詳細説明文字列
	 */
	protected String		description;
	/**
	 * アイコン
	 */
	protected Icon			icon;
	/**
	 * キーボードニーモニック
	 */
	protected int			mnemonic = '\0';
	/**
	 * アクセラレータキー
	 */
	protected KeyStroke	accelerator;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 情報を持たない <code>MenuItemResource</code> の新しいインスタンスを生成する。
	 */
	public MenuItemResource() {
	}

	/**
	 * 指定された説明文字列のみを格納する、
	 * <code>MenuItemResource</code> の新しいインスタンスを生成する。
	 * @param name	説明文字列
	 */
	public MenuItemResource(String name) {
		setName(name);
	}

	/**
	 * 指定された説明文字列とアイコンを格納する、
	 * <code>MenuItemResource</code> の新しいインスタンスを生成する。
	 * @param name	説明文字列
	 * @param icon	アイコン
	 */
	public MenuItemResource(String name, Icon icon) {
		setName(name);
		setIcon(icon);
	}

	/**
	 * 指定された説明文字列とキーボードニーモニックを格納する、
	 * <code>MenuItemResource</code> の新しいインスタンスを生成する。
	 * @param name		説明文字列
	 * @param mnemonic	キーボードニーモニック
	 */
	public MenuItemResource(String name, int mnemonic) {
		setName(name);
		setMnemonic(mnemonic);
	}

	/**
	 * 指定された説明文字列、アイコン、キーボードニーモニックを格納する、
	 * <code>MenuItemResource</code> の新しいインスタンスを生成する。
	 * @param name		説明文字列
	 * @param icon		アイコン
	 * @param mnemonic	キーボードニーモニック
	 */
	public MenuItemResource(String name, Icon icon, int mnemonic) {
		setName(name);
		setIcon(icon);
		setMnemonic(mnemonic);
	}

	/**
	 * 指定されたパラメータを格納する、
	 * <code>MenuItemResource</code> の新しいインスタンスを生成する。
	 * @param name				説明文字列
	 * @param icon				アイコン
	 * @param mnemonic			キーボードニーモニック
	 * @param acceleratorKey	アクセラレータキーとするキーコード
	 */
	public MenuItemResource(String name, Icon icon, int mnemonic, int acceleratorKey) {
		this(name, icon, mnemonic);
		setAccelerator(acceleratorKey);
	}

	/**
	 * 指定されたパラメータを格納する、
	 * <code>MenuItemResource</code> の新しいインスタンスを生成する。
	 * @param name			説明文字列
	 * @param icon			アイコン
	 * @param mnemonic		キーボードニーモニック
	 * @param accelerator	アクセラレータキー
	 */
	public MenuItemResource(String name, Icon icon, int mnemonic, KeyStroke accelerator) {
		this(name, icon, mnemonic);
		setAccelerator(accelerator);
	}

	/**
	 * 指定されたパラメータを格納する、
	 * <code>MenuItemResource</code> の新しいインスタンスを生成する。
	 * @param name			説明文字列
	 * @param icon			アイコン
	 * @param tooltip		ツールチップテキスト
	 * @param description	詳細説明文字列
	 * @param mnemonic		キーボードニーモニック
	 * @param keyStroke		アクセラレータキー
	 */
	public MenuItemResource(String name, Icon icon, String tooltip, String description,
							int mnemonic, KeyStroke keyStroke)
	{
		this(name, icon);
		setToolTip(tooltip);
		setDescription(description);
		setMnemonic(mnemonic);
		setAccelerator(keyStroke);
	}

	/**
	 * 指定されたパラメータを格納する、
	 * <code>MenuItemResource</code> の新しいインスタンスを生成する。
	 * @param actionCommand	コマンド文字列
	 * @param name			説明文字列
	 * @param icon			アイコン
	 * @param tooltip		ツールチップテキスト
	 * @param description	詳細説明文字列
	 * @param mnemonic		キーボードニーモニック
	 * @param keyStroke		アクセラレータキー
	 */
	public MenuItemResource(String actionCommand, String name,
							Icon icon, String tooltip, String description,
							int mnemonic, KeyStroke keyStroke)
	{
		this(name, icon);
		setCommandKey(actionCommand);
		setToolTip(tooltip);
		setDescription(description);
		setMnemonic(mnemonic);
		setAccelerator(keyStroke);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public JMenu createMenu() {
		JMenu menu = new JMenu(this.name);
		menu.setMnemonic(this.mnemonic);
		if (this.command != null)
			menu.setActionCommand(this.command);
		if (this.icon != null)
			menu.setIcon(this.icon);
		if (this.tooltip != null)
			menu.setToolTipText(this.tooltip);
		if (this.accelerator != null)
			menu.setAccelerator(this.accelerator);
		return menu;
	}
	
	public JMenuItem createMenuItem() {
		JMenuItem mi = new JMenuItem(this.name, this.mnemonic);
		if (this.command != null)
			mi.setActionCommand(this.command);
		if (this.icon != null)
			mi.setIcon(this.icon);
		if (this.tooltip != null)
			mi.setToolTipText(this.tooltip);
		if (this.accelerator != null)
			mi.setAccelerator(this.accelerator);
		return mi;
	}
	
	public JButton createButton() {
		JButton btn = new JButton(getName(), getIcon());
		if (this.command != null)
			btn.setActionCommand(this.command);
		if (this.tooltip != null)
			btn.setToolTipText(this.tooltip);
		btn.setMnemonic(this.mnemonic);
		return btn;
	}

	/**
	 * このリソースに格納されているコマンド文字列を取得する。
	 * @return	コマンド文字列
	 */
	public String getCommandKey() {
		return this.command;
	}

	/**
	 * 指定されたコマンド文字列を、このリソースに格納する。
	 * @param strValue	コマンド文字列
	 */
	public void setCommandKey(String strValue) {
		this.command = strValue;
	}

	/**
	 * このリソースに格納されている説明文字列を取得する。
	 * @return	説明文字列
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 指定された説明文字列を、このリソースに格納する。
	 * @param strValue
	 */
	public void setName(String strValue) {
		this.name = strValue;
	}

	/**
	 * このリソースに格納されているツールチップテキストを取得する。
	 * @return	ツールチップテキスト
	 */
	public String getToolTip() {
		return this.tooltip;
	}

	/**
	 * 指定されたツールチップテキストを、このリソースに格納する。
	 * @param strValue	ツールチップテキスト
	 */
	public void setToolTip(String strValue) {
		this.tooltip = strValue;
	}

	/**
	 * このリソースに格納されている詳細説明文字列を取得する。
	 * @return	詳細説明文字列
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * 指定された詳細説明文字列を、このリソースに格納する。
	 * @param strValue	詳細説明文字列
	 */
	public void setDescription(String strValue) {
		this.description = strValue;
	}

	/**
	 * このリソースに格納されているアイコンを取得する。
	 * @return	アイコン
	 */
	public Icon getIcon() {
		return this.icon;
	}

	/**
	 * 指定されたアイコンを、このリソースに格納する。
	 * @param newIcon	アイコン
	 */
	public void setIcon(Icon newIcon) {
		this.icon = newIcon;
	}

	/**
	 * このリソースに格納されているキーボードニーモニックを取得する。
	 * @return	キーボードニーモニック
	 */
	public int getMnemonic() {
		return this.mnemonic;
	}

	/**
	 * 指定されたキーボードニーモニックを、このリソースに格納する。
	 * @param mnemonic	キーボードニーモニック
	 */
    public void setMnemonic(char mnemonic) {
    	int vk = (int)mnemonic;
    	if (vk >= 'a' && vk <= 'z')
    		vk -= ('a' - 'A');
    	setMnemonic(vk);
    }

    /**
     * 指定されたキーボードニーモニックを、このリソースに格納する。
     * @param mnemonic	キーボードニーモニック
     */
	public void setMnemonic(int mnemonic) {
		this.mnemonic = mnemonic;
	}

	/**
	 * このリソースに格納されているアクセラレータキーを取得する。
	 * @return	アクセラレータキー
	 */
	public KeyStroke getAccelerator() {
		return this.accelerator;
	}

	/**
	 * 指定されたキーコードによって生成されたアクセラレータキーを、このリソースに格納する。
	 * このメソッドは、指定されたキーコードとデフォルトのショートカットキーマスクを組み合わせて
	 * アクセラレータキーを生成する。
	 * @param keyCode	アクセラレータキーとするキーコード
	 */
	public void setAccelerator(int keyCode) {
		setAccelerator(KeyStroke.getKeyStroke(keyCode, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	/**
	 * 指定されたキーコードと修飾子から生成されたアクセラレータキーを、このリソースに格納する。
	 * このメソッドは、指定されたキーコードと修飾子を組み合わせてアクセラレータキーを生成する。
	 * @param keyCode		キーコード
	 * @param keyModifiers	修飾子
	 */
	public void setAccelerator(int keyCode, int keyModifiers) {
		setAccelerator(KeyStroke.getKeyStroke(keyCode, keyModifiers));
	}

	/**
	 * 指定されたアクセラレータキーを、このリソースに格納する。
	 * @param keyStroke	アクセラレータキー
	 */
	public void setAccelerator(KeyStroke keyStroke) {
		this.accelerator = keyStroke;
	}

	/**
	 * キーボードショートカットとなるキーストロークを取得する。
	 * このメソッドは、プラットフォームに依存したショートカットキーの修飾子と、
	 * 指定されたキーコードから、キーストロークを取得する。
	 * @param keyCode	キーコード
	 * @return	キーストローク
	 */
	static public final KeyStroke getMenuShortcutKeyStroke(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
	}
	
	/**
	 * キーボードショートカットとなるキーストロークを取得する。
	 * このメソッドは、プラットフォームに依存したショートカットキーの修飾子と指定された修飾子の論理和の結果と、
	 * 指定されたキーコードから、キーストロークを取得する。
	 * @param keyCode	キーコード
	 * @return	キーストローク
	 */
	static public final KeyStroke getMenuShortcutKeyStroke(int keyCode, int appendedModifiers) {
		return KeyStroke.getKeyStroke(keyCode, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | appendedModifiers);
	}

	/*
	static public int getDefaultKeyModifier() {
		return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	}
	
	static public KeyStroke makeAccelerator(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, getDefaultKeyModifier());
	}
	
	static public KeyStroke makeAccelerator(int keyCode, int keyModifiers) {
		return KeyStroke.getKeyStroke(keyCode, keyModifiers);
	}
	*/

	/**
	 * 標準的な編集メニューの Undo ショートカットキーの組み合わせを返す。
	 * 基本的には [Ctrl]+[Z] もしくは [command]+[Z] となる。
	 */
	static public final KeyStroke getEditUndoShortcutKeyStroke() {
		return getMenuShortcutKeyStroke(KeyEvent.VK_Z);
	}
	
	/**
	 * 標準的な編集メニューの Redo ショートカットキーの組み合わせを返す。
	 * 基本的には [Ctrl]+[Y] もしくは [command]+[Y] となる。
	 */
	static public final KeyStroke getEditRedoShortcutKeyStroke() {
		return getMenuShortcutKeyStroke(KeyEvent.VK_Y);
	}
	
	/**
	 * 標準的な編集メニューの Cut ショートカットキーの組み合わせを返す。
	 * 基本的には [Ctrl]+[X] もしくは [command]+[X] となる。
	 */
	static public final KeyStroke getEditCutShortcutKeyStroke() {
		return getMenuShortcutKeyStroke(KeyEvent.VK_X);
	}
	
	/**
	 * 標準的な編集メニューの Copy ショートカットキーの組み合わせを返す。
	 * 基本的には [Ctrl]+[C] もしくは [command]+[C] となる。
	 */
	static public final KeyStroke getEditCopyShortcutKeyStroke() {
		return getMenuShortcutKeyStroke(KeyEvent.VK_C);
	}
	
	/**
	 * 標準的な編集メニューの Paste ショートカットキーの組み合わせを返す。
	 * 基本的には [Ctrl]+[V] もしくは [command]+[V] となる。
	 */
	static public final KeyStroke getEditPasteShortcutKeyStroke() {
		return getMenuShortcutKeyStroke(KeyEvent.VK_V);
	}
	
	/**
	 * 標準的な編集メニューの Delete ショートカットキーの組み合わせを返す。
	 * 基本的には [Delete] となる。
	 */
	static public final KeyStroke getEditDeleteShortcutKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
	}
	
	/**
	 * 標準的な編集メニューの全選択ショートカットキーの組み合わせを返す。
	 * 基本的には [Ctrl]+[A] もしくは [command]+[A] となる。
	 */
	static public final KeyStroke getEditSelectAllShortcutKeyStroke() {
		return getMenuShortcutKeyStroke(KeyEvent.VK_A);
	}
	
	/**
	 * 標準的な検索メニューの［前を検索］ショートカットキーの組み合わせを返す。
	 * 基本的には [Shift]+[F3] となる。
	 */
	static public final KeyStroke getFindPrevShortcutKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F3, KeyEvent.SHIFT_DOWN_MASK);
	}
	
	/**
	 * 標準的な編集メニューの［次を検索］ショートカットキーの組み合わせを返す。
	 * 基本的には [F3] となる。
	 */
	static public final KeyStroke getFindNextShortcutKeyStroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
