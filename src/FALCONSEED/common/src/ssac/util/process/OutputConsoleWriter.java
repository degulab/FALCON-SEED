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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)OutputConsoleWriter.java	3.0.0	2014/03/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.SwingUtilities;

/**
 * 標準出力、標準エラー出力の内容を、指定されたコンソールに書き込むクラス。
 * コンソールには非同期で出力するが、コンソールへの出力が完了するまで、
 * ここでの処理はブロックされる。
 * 
 * @version 3.0.0	2014/03/25
 * @since 3.0.0
 */
public class OutputConsoleWriter implements IOutputWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String	CONSOLE_NEWLINE	= "\n";
	
	static protected final EmptyOutputConsoleHandler	NULL_HANDLER	= new EmptyOutputConsoleHandler();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** コンソール出力用のキュー **/
	protected ArrayBlockingQueue<OutputString>	_queue;
	/** コンソール出力ランナー **/
	private OutputConsoleInvoker	_invoker;
	
	/** コンソール出力ハンドラ **/
	private volatile OutputConsoleHandler	_handler;
	/** コンソール出力が有効であることを示すフラグ **/
	private volatile boolean	_enabled;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public OutputConsoleWriter() {
		this(null);
	}
	
	public OutputConsoleWriter(OutputConsoleHandler handler) {
		_queue = new ArrayBlockingQueue<OutputString>(1, true);
		_invoker = new OutputConsoleInvoker();
		setHandler(handler);
		_enabled = true;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean getEnabled() {
		return _enabled;
	}
	
	public void setEnabled(boolean toEnable) {
		_enabled = toEnable;
		if (!_enabled) {
			_queue.clear();
		}
	}
	
	public OutputConsoleHandler getHandler() {
		return (_handler==NULL_HANDLER ? null : _handler);
	}
	
	public void setHandler(OutputConsoleHandler handler) {
		_handler = (handler==null ? NULL_HANDLER : handler);
	}

	//------------------------------------------------------------
	// Implement ssac.util.process.ICommandOutputBroker interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトでは特別なロックオブジェクトは使用しないため、常に <tt>null</tt> を返す。
	 * @return	常に <tt>null</tt>
	 */
	@Override
	public Object getLockObject() {
		return null;
	}

	@Override
	public boolean checkError() {
		return false;
	}

	@Override
	public void flush() {
		flushQueue();
	}

	@Override
	public void close() {
		_enabled = false;	// close
		flushQueue();
	}

	@Override
	public void print(boolean isError, char c) {
		if (_enabled) {
			OutputString ostr = new OutputString(isError, String.valueOf(c));
			putToQueue(ostr);
		}
	}

	@Override
	public void print(boolean isError, String str) {
		if (_enabled) {
			OutputString ostr = new OutputString(isError, (str==null ? "null" : str));
			putToQueue(ostr);
		}
	}

	@Override
	public void print(boolean isError, Object obj) {
		if (_enabled) {
			OutputString ostr = new OutputString(isError, String.valueOf(obj));
			putToQueue(ostr);
		}
	}

	@Override
	public void printf(boolean isError, String format, Object... args) {
		if (_enabled) {
			OutputString ostr = new OutputString(isError, String.format(format, args));
			putToQueue(ostr);
		}
	}

	@Override
	public void printf(boolean isError, Locale l, String format, Object... args) {
		if (_enabled) {
			OutputString ostr = new OutputString(isError, String.format(l, format, args));
			putToQueue(ostr);
		}
	}

	@Override
	public void println(boolean isError, char c) {
		if (_enabled) {
			OutputString ostr = new OutputString(isError, String.valueOf(c).concat(CONSOLE_NEWLINE));
			putToQueue(ostr);
		}
	}

	@Override
	public void println(boolean isError, String str) {
		if (_enabled) {
			if (str == null) {
				str = "null".concat(CONSOLE_NEWLINE);
			} else {
				str = str.concat(CONSOLE_NEWLINE);
			}
			OutputString ostr = new OutputString(isError, str);
			putToQueue(ostr);
		}
	}

	@Override
	public void println(boolean isError, Object obj) {
		if (_enabled) {
			OutputString ostr = new OutputString(isError, String.valueOf(obj).concat(CONSOLE_NEWLINE));
			putToQueue(ostr);
		}
	}

	@Override
	public void printfln(boolean isError, String format, Object... args) {
		if (_enabled) {
			OutputString ostr = new OutputString(isError, String.format(format, args).concat(CONSOLE_NEWLINE));
			putToQueue(ostr);
		}
	}

	@Override
	public void printfln(boolean isError, Locale l, String format, Object... args) {
		if (_enabled) {
			OutputString ostr = new OutputString(isError, String.format(l, format, args).concat(CONSOLE_NEWLINE));
			putToQueue(ostr);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void flushQueue() {
		if (SwingUtilities.isEventDispatchThread()) {
			// キューの内容を空にして、メッセージを直接出力
			for (OutputString qstr = _queue.poll(); qstr != null; qstr = _queue.poll()) {
				_handler.doOutput(qstr);
			}
		}
		else {
			if (!_queue.isEmpty()) {
				// invoke
				SwingUtilities.invokeLater(_invoker);
			}
		}
	}
	
	protected void putToQueue(OutputString ostr) {
		if (SwingUtilities.isEventDispatchThread()) {
			// キューの内容を空にして、メッセージを直接出力
			for (OutputString qstr = _queue.poll(); qstr != null; qstr = _queue.poll()) {
				_handler.doOutput(qstr);
			}
			_handler.doOutput(ostr);
		}
		else {
			// キューが一杯ならブロッキング
			try {
				// put and blocking
				_queue.put(ostr);
				// invoke
				SwingUtilities.invokeLater(_invoker);
			}
			catch (InterruptedException ex) {
				_enabled = false;	// stop output to console
				_queue.clear();
				Thread.currentThread().interrupt();
			}
		}
	}
	
	protected void doOutputToConsole() {
		OutputString ostr = _queue.poll();
		if (ostr != null) {
			_handler.doOutput(ostr);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static private class EmptyOutputConsoleHandler implements OutputConsoleHandler
	{
		@Override
		public void doOutput(OutputString ostr) {}
	}
	
	protected class OutputConsoleInvoker implements Runnable
	{
		@Override
		public void run() {
			doOutputToConsole();
		}
	}
}
