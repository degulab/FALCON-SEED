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
 * @(#)AbMenuItemAction.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.menu;

import java.awt.Toolkit;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import ssac.util.Objects;
import ssac.util.swing.menu.MenuItemResource;

/**
 * メニューアクション用基底クラス。
 * <p>
 * このクラスは、{@link javax.swing.AbstractAction} の機能拡張であり、
 * {@link ssac.aadl.editor.plugin.core.swing.menu.MenuItemResource} の
 * インスタンスからセットアップするためのインタフェースを提供する。
 * アクションの基本的な動作に変更はない。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public abstract class AbMenuItemAction extends AbstractAction
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -598933364814503850L;

	static public final String Name			= Action.NAME;
	static public final String COMMAND		= Action.ACTION_COMMAND_KEY;
	static public final String ICON			= Action.SMALL_ICON;
	static public final String TOOLTIP		= Action.SHORT_DESCRIPTION;
	static public final String DESCRIPTION	= Action.LONG_DESCRIPTION;
	static public final String MNEMONIC		= Action.MNEMONIC_KEY;
	static public final String ACCELERATOR	= Action.ACCELERATOR_KEY;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * デフォルトの設定で、このオブジェクトの新しいインスタンスを生成する。
	 */
	public AbMenuItemAction() {
		super();
	}

	/**
	 * 指定された説明文字列とデフォルトのアイコンを使用して、
	 * このオブジェクトの新しいインスタンスを生成する。 
	 * @param name	説明文字列
	 */
	public AbMenuItemAction(String name) {
		super(name);
	}

	/**
	 * 指定された説明文字列とアイコンを使用して、
	 * このオブジェクトの新しいインスタンスを生成する。 
	 * @param name	説明文字列
	 * @param icon	アイコン
	 */
	public AbMenuItemAction(String name, Icon icon) {
		super(name, icon);
	}

	/**
	 * 指定されたパラメータを使用して、
	 * このオブジェクトの新しいインスタンスを生成する。 
	 * @param actionCommand	コマンド文字列
	 * @param name			説明文字列
	 * @param icon			アイコン
	 * @param tooltip		ツールチップテキスト
	 * @param description	詳細説明文字列
	 * @param mnemonic		キーボードニーモニック
	 * @param keyStroke		アクセラレータキー
	 */
	public AbMenuItemAction(String actionCommand, String name,
								Icon icon, String tooltip, String description,
								int mnemonic, KeyStroke keyStroke)
	{
		super(name, icon);
		setCommandKey(actionCommand);
		setToolTip(tooltip);
		setDescription(description);
		setMnemonic(mnemonic);
		setAccelerator(keyStroke);
	}

	/**
	 * 指定されたメニューリソースを使用して、
	 * このオブジェクトの新しいインスタンスを生成する。 
	 * @param mir	アクションに設定するメニューリソース
	 */
	public AbMenuItemAction(MenuItemResource mir) {
		super();
		setMenuItemResource(mir);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたメニューリソースを使用して、
	 * このアクション定義を更新する。
	 * 
	 * @param mir	アクションに設定するメニューリソース
	 */
	public void setMenuItemResource(MenuItemResource mir) {
		if (mir != null) {
			setName(mir.getName());
			setIcon(mir.getIcon());
			setCommandKey(mir.getCommandKey());
			setToolTip(mir.getToolTip());
			setDescription(mir.getDescription());
			setMnemonic(mir.getMnemonic());
			setAccelerator(mir.getAccelerator());
		}
		else {
			// clear all
			setName(null);
			setIcon(null);
			setCommandKey(null);
			setToolTip(null);
			setDescription(null);
			setMnemonic('\0');
			setAccelerator(null);
		}
	}

	/**
	 * このアクションの説明文字列を取得する。
	 * @return	説明文字列
	 */
	public String getName() {
		return (String)getValue(NAME);
	}

	/**
	 * 指定された説明文字列を、このアクションに設定する。
	 * @param strValue	説明文字列
	 */
	public void setName(String strValue) {
		if (!Objects.isEqual(strValue, getName())) {
			putValue(NAME, strValue);
		}
	}

	/**
	 * このアクションのコマンド文字列を取得する。
	 * @return	コマンド文字列
	 */
	public String getCommandKey() {
		return (String)getValue(COMMAND);
	}

	/**
	 * 指定されたコマンド文字列を、このアクションに設定する。
	 * @param strValue	コマンド文字列
	 */
	public void setCommandKey(String strValue) {
		if (!Objects.isEqual(strValue, getCommandKey())) {
			putValue(COMMAND, strValue);
		}
	}

	/**
	 * このアクションのツールチップテキストを取得する。
	 * @return	ツールチップテキスト
	 */
	public String getToolTip() {
		return (String)getValue(TOOLTIP);
	}

	/**
	 * 指定されたツールチップテキストを、このアクションに設定する。
	 * @param strValue	ツールチップテキスト
	 */
	public void setToolTip(String strValue) {
		if (!Objects.isEqual(strValue, getToolTip())) {
			putValue(TOOLTIP, strValue);
		}
	}

	/**
	 * このアクションの詳細説明文字列を取得する。
	 * @return	詳細説明文字列
	 */
	public String getDescription() {
		return (String)getValue(DESCRIPTION);
	}

	/**
	 * 指定された詳細説明文字列を、このアクションに設定する。
	 * @param strValue	詳細説明文字列
	 */
	public void setDescription(String strValue) {
		if (!Objects.isEqual(strValue, getDescription())) {
			putValue(DESCRIPTION, strValue);
		}
	}

	/**
	 * このアクションのアイコンを取得する。
	 * @return	アイコン
	 */
	public Icon getIcon() {
		return (Icon)getValue(ICON);
	}

	/**
	 * 指定されたアイコンを、このアクションに設定する。
	 * @param icon	アイコン
	 */
	public void setIcon(Icon icon) {
		if (icon != getIcon()) {
			putValue(ICON, icon);
		}
	}

	/**
	 * このアクションのキーボードニーモニックを取得する。
	 * @return	キーボードニーモニック
	 */
	public int getMnemonic() {
		Integer n = (Integer)getValue(MNEMONIC);
		return (n == null ? '\0' : n.intValue());
	}

	/**
	 * 指定されたキーボードニーモニックを、このアクションに設定する。
	 * @param mnemonic	キーボードニーモニック
	 */
	public void setMnemonic(int mnemonic) {
		if (mnemonic != getMnemonic()) {
			if (mnemonic != '\0') {
				putValue(MNEMONIC, mnemonic);
			} else {
				putValue(MNEMONIC, null);
			}
		}
	}

	/**
	 * このアクションのアクセラレータキーを取得する。
	 * @return	アクセラレータキー
	 */
	public KeyStroke getAccelerator() {
		return (KeyStroke)getValue(ACCELERATOR);
	}

	/**
	 * 指定されたアクセラレータキーを、このアクションに設定する。
	 * @param keyStroke	アクセラレータキー
	 */
	public void setAccelerator(KeyStroke keyStroke) {
		if (!Objects.isEqual(keyStroke, getAccelerator())) {
			putValue(ACCELERATOR, keyStroke);
		}
	}

	/**
	 * 指定されたキーコードを、このアクションのアクセラレータキーとして設定する。
	 * このメソッドは、デフォルトのショートカットキーマスクを組み合わせて
	 * アクセラレータキーを生成する。
	 * @param keyCode	アクセラレータキーとするキーコード
	 */
	public void setAccelerator(int keyCode) {
		setAccelerator(KeyStroke.getKeyStroke(keyCode, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	/**
	 * 指定されたキーコードと修飾子を、このアクションのアクセラレータキーとして設定する。
	 * このメソッドは、指定されたキーコードと修飾子から、アクセラレータキーを生成する。
	 * @param keyCode		キーコード
	 * @param keyModifiers	修飾子
	 */
	public void setAccelerator(int keyCode, int keyModifiers) {
		setAccelerator(KeyStroke.getKeyStroke(keyCode, keyModifiers));
	}

	//------------------------------------------------------------
	// Helper interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたアクションの説明文字列を取得する。
	 * @param action 対象のアクション
	 * @return	説明文字列
	 */
	static public final String getName(final Action action) {
		return (String)action.getValue(NAME);
	}

	/**
	 * 指定されたアクションのコマンド文字列を取得する。
	 * @param action 対象のアクション
	 * @return	コマンド文字列
	 */
	static public final String getCommandKey(final Action action) {
		return (String)action.getValue(COMMAND);
	}

	/**
	 * 指定されたアクションのツールチップテキストを取得する。
	 * @param action 対象のアクション
	 * @return	ツールチップテキスト
	 */
	static public final String getToolTip(final Action action) {
		return (String)action.getValue(TOOLTIP);
	}

	/**
	 * 指定されたアクションの詳細説明文字列を取得する。
	 * @param action 対象のアクション
	 * @return	詳細説明文字列
	 */
	static public final String getDescription(final Action action) {
		return (String)action.getValue(DESCRIPTION);
	}

	/**
	 * 指定されたアクションのアイコンを取得する。
	 * @param action 対象のアクション
	 * @return	アイコン
	 */
	static public final Icon getIcon(final Action action) {
		return (Icon)action.getValue(ICON);
	}

	/**
	 * 指定されたアクションのキーボードニーモニックを取得する。
	 * @param action 対象のアクション
	 * @return	キーボードニーモニック
	 */
	static public final int getMnemonic(final Action action) {
		Integer n = (Integer)action.getValue(MNEMONIC);
		return (n == null ? '\0' : n.intValue());
	}

	/**
	 * 指定されたアクションのアクセラレータキーを取得する。
	 * @param action 対象のアクション
	 * @return	アクセラレータキー
	 */
	static public final KeyStroke getAccelerator(final Action action) {
		return (KeyStroke)action.getValue(ACCELERATOR);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
