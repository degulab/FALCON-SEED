/*
 * @(#)CommandOutput.java	1.0.0	2013/02/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mqtt.broker.util;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * プロセスにより出力される標準出力、エラー出力の文字列を保持するクラス。
 * <p>
 * このクラスの実装は、<code>java.util.concurrent.ConsurrentLinkedQueue</code> であり、
 * 標準出力かエラー出力を示すステータスを保持する。
 * 
 * @version 1.0.0	2013/02/28
 */
public class CommandOutput
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ConcurrentLinkedQueue<OutputString> queue;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandOutput() {
		this.queue = new ConcurrentLinkedQueue<OutputString>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public int getCount() {
		return queue.size();
	}
	
	public void push(boolean isError, String strout) {
		push(new OutputString(isError, strout));
	}
	
	public void push(OutputString cmdout) {
		queue.add(cmdout);
	}
	
	public OutputString peek() {
		return queue.peek();
	}
	
	public OutputString pop() {
		return queue.poll();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
