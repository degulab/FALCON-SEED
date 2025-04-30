/*
 * @(#)IMenuHandler.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.menu;

import java.awt.event.ActionEvent;

/**
 * メニュー・アクション・ハンドラとなるインタフェース。
 * <p>
 * このインタフェースは、エディタのメインフレームに適用され、
 * メニューアクションを処理するためのフレームワークとなる。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public interface IMenuHandler
{
	/**
	 * メニュー項目の選択時に呼び出されるハンドラ・メソッド。
	 * 
	 * @param e		メニュー項目選択時のイベントオブジェクト
	 */
	public void menuActionPerformed(ActionEvent e);
	
	/**
	 * メニュー項目の更新要求時に呼び出されるハンドラ・メソッド。
	 * 
	 * @param command	このイベント要因のコマンド文字列
	 * @param source	このイベント要因のソースオブジェクト。
	 * 					ソースオブジェクトが未定義の場合は <tt>null</tt>。
	 */
	public void menuUpdatePerformed(String command, Object source);
}
