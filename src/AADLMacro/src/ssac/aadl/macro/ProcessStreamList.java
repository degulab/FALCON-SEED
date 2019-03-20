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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ProcessStreamList.java	2.0.0	2014/03/18 : move 'ssac.util.*' to 'ssac.aadl.macro.util.*' (recursive) 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ProcessStreamList.java	1.00	2008/11/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro;

import java.io.InputStream;

import ssac.aadl.macro.util.io.ReportPrinter;

/**
 * @deprecated	使用しません。
 * 実行中プロセスのストリームを管理するリスト。
 * 
 * @version 2.0.0	2014/03/18
 * @since 1.00
 */
@Deprecated
public class ProcessStreamList
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 同期オブジェクト・インスタンス
	 */
	private final Object mutex = new Object();

	/**
	 * リスト先頭ノード
	 */
	private StreamNode firstNode = null;
	/**
	 * リスト終端ノード
	 */
	private StreamNode lastNode  = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * <code>ProcessStreamList</code> の新しいインスタンスを生成する。
	 */
	public ProcessStreamList() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		synchronized (mutex) {
			return (firstNode == null);
		}
	}
	
	public void clear() {
		synchronized (mutex) {
			StreamNode node = null;
			StreamNode next = firstNode;
			while (next != null) {
				node = next;
				next = node.next;
				node.prev = null;
				node.next = null;
			}
			node = null;
			next = null;
			firstNode = null;
			lastNode  = null;
		}
	}
	
	public StreamNode getFirst() {
		synchronized (mutex) {
			return firstNode;
		}
	}
	
	public StreamNode getLast() {
		synchronized (mutex) {
			return lastNode;
		}
	}
	
	public boolean hasPrev(StreamNode curNode) {
		synchronized (mutex) {
			return (curNode.prev != null);
		}
	}
	
	public boolean hasNext(StreamNode curNode) {
		synchronized (mutex) {
			return (curNode.next != null);
		}
	}
	
	public StreamNode getPrev(StreamNode curNode) {
		synchronized (mutex) {
			return curNode.prev;
		}
	}
	
	public StreamNode getNext(StreamNode curNode) {
		synchronized (mutex) {
			return curNode.next;
		}
	}
	
	public StreamNode add(final InputStream inStream, final ReportPrinter printer) {
		StreamNode node = new StreamNode(inStream, printer);
		synchronized (mutex) {
			if (lastNode == null) {
				firstNode = node;
				lastNode  = node;
			} else {
				lastNode.next = node;
				node.prev = lastNode;
				lastNode = node;
			}
		}
		return node;
	}
	
	public void remove(StreamNode node) {
		synchronized (mutex) {
			if (node.prev != null) {
				node.prev.next = node.next;
			} else {
				firstNode = node.next;
			}
			if (node.next != null) {
				node.next.prev = node.prev;
			} else {
				lastNode = node.prev;
			}
			node.prev = null;
			node.next = null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner class
	//------------------------------------------------------------

	@Deprecated
	static class StreamNode {
		private final InputStream inStream;
		private final ReportPrinter printer;
		private StreamNode prev = null;
		private StreamNode next = null;
		
		private StreamNode(InputStream inStream, ReportPrinter printer) {
			this.inStream = inStream;
			this.printer  = printer;
		}
		
		public InputStream getInputStream() {
			return inStream;
		}
		
		public ReportPrinter getReportPrinter() {
			return printer;
		}
	}
}
