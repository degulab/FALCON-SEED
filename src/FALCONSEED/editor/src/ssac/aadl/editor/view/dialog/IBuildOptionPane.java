/*
 * @(#)IBuildOptionPane.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IBuildOptionPane.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.Window;

import ssac.aadl.module.setting.AbstractSettings;

/**
 * ビルドオプションダイアログに組み込まれるパネルの機能を提供するインタフェース。
 * 
 * @version 1.14	2009/12/09
 */
public interface IBuildOptionPane {
	public AbstractSettings getOptionSettings();
	public void restoreOptionSettings();
	public void storeOptionSettings();
	/**
	 * このオプションを保持する設定情報が読み込み専用の場合に <tt>true</tt> を返す。
	 * @since 1.14
	 */
	public boolean isReadOnly();
	/**
	 * このコンポーネントを保持するウィンドウが最初に表示された
	 * 直後に呼び出されるイベント。
	 * @param source	このイベントを呼び出したウィンドウ
	 * @since 1.14
	 */
	public void onWindowOpened(Window source);
}
