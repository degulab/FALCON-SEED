/*
 * @(#)ExecutorStoppedListener.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.build;

import java.util.EventListener;

/**
 * コマンド実行時の終了イベントを受け取りリスナークラス。
 * 
 * @version 1.00 2008/03/24
 */
public interface ExecutorStoppedListener extends EventListener
{
	public void executionStopped(ExecutorEvent ee);
}
