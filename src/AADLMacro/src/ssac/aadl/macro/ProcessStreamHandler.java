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
 * @(#)ProcessStreamHandler.java	2.0.0	2014/03/18 : move 'ssac.util.*' to 'ssac.aadl.macro.util.*' (recursive) 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ProcessStreamHandler.java	1.00	2008/11/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro;

import static ssac.aadl.macro.util.Validations.validNotNull;

import java.io.IOException;
import java.io.InputStream;

import ssac.aadl.macro.ProcessStreamList.StreamNode;
import ssac.aadl.macro.util.io.Files;
import ssac.aadl.macro.util.io.ReportPrinter;

/**
 * @deprecated	使用しません。
 * 複数のプロセスからの入力ストリームを処理するスレッド。
 * 
 * @version 2.0.0	2014/03/18
 * @since 1.00
 */
@Deprecated
public final class ProcessStreamHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final int DEFAULT_BUFFER_SIZE = 8192;
	static private final long STREAM_READ_INTERVAL = 100L;	// 100ms

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ProcessStreamList streamList;
	private final HandlerThread thread;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private ProcessStreamHandler() {
		this.streamList = new ProcessStreamList();
		this.thread = new HandlerThread();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public ProcessStreamHandler startHandler() {
		// スレッド生成
		ProcessStreamHandler newHandler = new ProcessStreamHandler();
		
		// スレッド実行開始
		newHandler.thread.start();
		
		// インスタンスを返す。
		return newHandler;
	}
	
	public void addStream(final InputStream inStream, final ReportPrinter printer) {
		validNotNull(inStream, "InputStream is null.");
		if (thread != null && !thread.stopRequest) {
			// ハンドラにストリームを登録
			streamList.add(inStream, printer);
			thread.interrupt();	// wake up
		}
		else {
			// ハンドラが存在しないか、停止要求の状態であれば、強制的にストリームを閉じる
			Files.closeStream(inStream);
		}
	}

	/**
	 * ハンドラ・スレッドへ停止要求を発行する。
	 * このメソッドは、スレッドの終了を待たない。
	 */
	public void stopHandler() {
		if (thread != null) {
			thread.stopRequest = true;
			thread.interrupt();	// スレッド再開要求
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private int outputFromStream(final byte[] buffer, final InputStream inStream, final ReportPrinter printer) {
		int read = 0;
		try {
			int len = inStream.available();
			if (len > DEFAULT_BUFFER_SIZE) {
				if (printer != null) {
					for (; len > 0; len = inStream.available()) {
						int r = inStream.read(buffer, 0, Math.max(len, DEFAULT_BUFFER_SIZE));
						if (r > 0) {
							read += r;
							printer.write(buffer, 0, r);
						} else
							break;
					}
				} else {
					for (; len > 0; len = inStream.available()) {
						int r = inStream.read(buffer, 0, Math.max(len, DEFAULT_BUFFER_SIZE));
						if (r > 0)
							read += r;
						else
							break;
					}
				}
			}
			else if (len > 0) {
				read = inStream.read(buffer, 0, len);
				if (read > 0 && printer != null) {
					printer.write(buffer, 0, read);
				}
			}
		}
		catch (IOException ex) {
			ex = null;
			read = -1;
		}
		return read;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	@Deprecated
	class HandlerThread extends Thread {
		private volatile boolean stopRequest;
		
		public HandlerThread() {
			super("ProcessStreamHandler_thread");
			this.stopRequest = false;
		}
		
		public void run() {
			final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int result;
			boolean readStream = false;
			StreamNode node = null;
			StreamNode next = null;
			
			for(;;) {
				// 初期化
				readStream = false;
				node = streamList.getFirst();
				while (node != null) {
					result = outputFromStream(buffer, node.getInputStream(), node.getReportPrinter());
					next = streamList.getNext(node);
					if (result < 0) {
						// ストリーム消滅のため、リストから削除
						Files.closeStream(node.getInputStream());
						streamList.remove(node);
					}
					else if (result > 0) {
						// データ読み込み
						readStream = true;
					}
					node = next;
					next = null;
				}
				
				// ストリームからデータが読み込まれなかった場合は、待機
				if (streamList.isEmpty()) {
					if (stopRequest) {
						// ストリームが存在せず停止要求がある場合、スレッドを停止
						break;
					} else {
						// 最長のウェイトで待機
						try {
							sleep(Long.MAX_VALUE);
						} catch (InterruptedException ex) {
							ex = null;	// 処理再開
						}
					}
				}
				else if (!readStream) {
					// 読み込みは行われなかったので、しばらく待機
					try {
						sleep(STREAM_READ_INTERVAL);
					} catch (InterruptedException ex) {
						ex = null;	// 処理再開
					}
				}
			}
			
			// 念のため、すべてのストリームを破棄して終了
			node = streamList.getFirst();
			while (node != null) {
				Files.closeStream(node.getInputStream());
				node = streamList.getNext(node);
			}
			streamList.clear();
			
			// スレッド終了
		}
	}
}
