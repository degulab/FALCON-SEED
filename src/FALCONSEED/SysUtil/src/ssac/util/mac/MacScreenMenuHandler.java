/*
 * @(#)MacUtilities.java	2012/07/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.mac;

/**
 * Mac スクリーンメニューのシステムメニュー項目に対応するハンドラ。
 * 
 * @version 2012/07/02
 */
public interface MacScreenMenuHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * About メニュー項目に対応するイベントハンドラ。
	 */
	public void onMacMenuAbout();

	/**
	 * Quit メニュー項目に対応するイベントハンドラ。
	 */
	public void onMacMenuQuit();
}
