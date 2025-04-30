/*
 * @(#)ExecutionTerminateHandler.java	3.0.0	2014/03/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

/**
 * コマンド実行終了時に、コマンド（プロセス）監視スレッドから呼び出されるインタフェース。
 * 
 * @version 3.0.0	2014/03/25
 * @since 3.0.0
 */
public interface ExecutionTerminateHandler
{
	/**
	 * コマンド（プロセス）終了時に呼び出されるメソッド。
	 * このメソッドは、コマンド（プロセス）が終了の状態に関わらず、実行が停止した時点で、
	 * コマンド監視スレッドから呼び出される。
	 * <p><b><i>注意：</i></b>
	 * <blockquote>
	 * このメソッド内での処理は、コマンド監視スレッド内での処理となる。
	 * </blockquote>
	 * @param executor	実行が終了したプロセスを管理するコマンドエグゼキューター
	 */
	public void executionTerminated(InterruptibleCommandExecutor executor);
}
