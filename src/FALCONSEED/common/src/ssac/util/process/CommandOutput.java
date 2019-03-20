/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CommandOutput.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * プロセスにより出力される標準出力、エラー出力の文字列を保持するクラス。
 * <p>
 * このクラスの実装は、<code>java.util.concurrent.ConsurrentLinkedQueue</code> であり、
 * 標準出力かエラー出力を示すステータスを保持する。
 * 
 * @version 1.00	2008/03/24
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
