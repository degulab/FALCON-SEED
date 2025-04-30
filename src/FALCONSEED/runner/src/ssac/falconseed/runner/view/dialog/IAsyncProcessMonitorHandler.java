/*
 * @(#)IAsyncProcessMonitorHandler.java	3.0.0	2014/03/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view.dialog;


/**
 * プロセス実行モニタによって発生するイベントのハンドラ。
 * 
 * @version 3.0.0	2014/03/26
 * @since 3.0.0
 */
public interface IAsyncProcessMonitorHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ステータスバーのメッセージ領域に、新しいメッセージを設定する。
	 * <em>message</em> が <tt>null</tt> の場合は、空文字列が設定される。
	 * ステータスバーが存在しない場合、このメソッド呼び出しは無視される。
	 * @param message	メッセージを示す文字列
	 */
	public void setStatusBarMessage(String message);

	/**
	 * モニタが表示されたときに呼び出されるハンドラ。
	 * @param monitor	モニタオブジェクト
	 */
	public void onShownMonitor(AsyncProcessMonitorWindow monitor);

	/**
	 * モニタが非表示にされたときに呼び出されるハンドラ。
	 * @param monitor	モニタオブジェクト
	 */
	public void onHiddenMonitor(AsyncProcessMonitorWindow monitor);

	/**
	 * モニタが破棄された後に呼び出されるハンドラ。
	 * @param monitor	モニタオブジェクト
	 */
	public void onDisposedMonitor(AsyncProcessMonitorWindow monitor);
}
