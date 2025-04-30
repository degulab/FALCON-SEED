/*
 * @(#)IStatusBar.java	2.0.0	2012/10/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

/**
 * ステータスバーの基本的なインタフェース。
 * 
 * @version 2.0.0	2012/10/09
 * @since 2.0.0
 */
public interface IStatusBar
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ステータスバーのメッセージ領域に設定されたメッセージを返す。
	 * このメソッドは <tt>null</tt> を返さない。
	 * @return	メッセージを示す文字列
	 */
	public String getMessage();

	/**
	 * ステータスバーのメッセージ領域に、新しいメッセージを設定する。
	 * <em>message</em> が <tt>null</tt> の場合は、空文字列が設定される。
	 * @param message	メッセージを示す文字列
	 */
	public void setMessage(String message);
}
