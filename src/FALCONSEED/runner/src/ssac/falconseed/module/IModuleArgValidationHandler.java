/*
 * @(#)IModuleArgValidationHandler.java	1.22	2012/08/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

/**
 * モジュール実行時引数の正当性検査におけるイベントハンドラ・インタフェース。
 * 
 * @version 1.22	2012/08/22
 * @since 1.22
 */
public interface IModuleArgValidationHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * エラーをクリアする。
	 */
	public void clearError();

	/**
	 * 新しいエラーを設定する。
	 * <em>errmsg</em> が <tt>null</tt> もしくは空文字列の場合は、
	 * {@link #clearError()} を呼び出したのと同じ結果となる。
	 * @param errmsg	設定するエラーメッセージ
	 */
	public void setError(String errmsg);
}
