/*
 * @(#)OutputConsoleHandler.java	3.0.0	2014/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

/**
 * 標準出力、標準エラー出力のコンソールへの出力要求に応答するための、ハンドラーインタフェース。
 * このインタフェースは、{@link OutputConsoleWriter} オブジェクトに対する書き込みが
 * 発生した後、出力が必要なメッセージがある場合のみ、Swing スレッドから呼び出される。
 * このハンドラを登録した場合、速やかに出力処理を行うこと。
 * 
 * @version 3.0.0	2014/03/24
 * @since 3.0.0
 */
public interface OutputConsoleHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void doOutput(OutputString ostr);
}
