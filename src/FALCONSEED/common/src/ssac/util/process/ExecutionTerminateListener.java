/*
 * @(#)ExecutionTerminateListener.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.util.EventListener;

/**
 * コマンド実行時の終了イベントを受け取りリスナークラス。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public interface ExecutionTerminateListener extends EventListener
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void executionTerminated(CommandExecutorEvent ceEvent);
}
