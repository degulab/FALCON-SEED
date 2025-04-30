/*
 * @(#)IListControlHandler.java	2.0.0	2012/10/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.list;

import java.awt.event.ActionEvent;

/**
 * <code>{@link ListController}</code> クラスに登録されたボタンの通知を受け取る
 * インタフェース。
 * 
 * @version 2.0.0 2012/10/19
 */
public interface IListControlHandler {
	/**
	 * [追加]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonAdd(ActionEvent ae);
	/**
	 * [編集]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonEdit(ActionEvent ae);
	/**
	 * [削除]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonDelete(ActionEvent ae);
	/**
	 * [クリア]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonClear(ActionEvent ae);
	/**
	 * [上へ移動]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonUp(ActionEvent ae);
	/**
	 * [下へ移動]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonDown(ActionEvent ae);
}
