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
 * @(#)JAppExecutor.java	2012/07/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)JAppExecutor.java	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

/**
 * JAVAアプリケーションのプロセス実行管理クラス。
 * 
 * @version 2012/07/02
 * @since 2011/02/14
 */
public class JavaAppExecutor
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * 正常起動を示す、アプリケーションが最初に出力するメッセージ。
	 * 正常起動の監視対象アプリケーションは、起動直後にこのメッセージを
	 * 標準出力に出力することが、監視の前提条件となる。
	 */
	static public final String SUCCEEDED_MESSAGE = "@*@*@_Succeeded_to_start_application_process_@*@*@";
	
	static protected final int BUFSIZE	= 1024;
	static protected final long WATCH_INTERVAL = 1000;	// 1000ms
	static protected final long WATCH_TIMEOUT  = 5000;	// 5000ms

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final byte[]	_succeededMessageBytes;
	private final String	_charsetName;
	private final ProcessWatchHandler	_handler;
	private final String	_appName;
	
	private ProcessBuilder _builder;
	private Process		_process;
	private ProcessWatcher	_watcher;
	private ByteArrayOutputStream	_bufInput;
	private ByteArrayOutputStream	_bufError;

	private int		_exitCode = 0;
	private boolean	_procIsRunning = false;
	private boolean	_procSucceeded = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JavaAppExecutor(String appName, ProcessBuilder pb, String charsetName, ProcessWatchHandler handler) {
		if (pb == null)
			throw new NullPointerException("The specified ProcessBuilder object is null.");
		byte[] smsgBytes;
		String csName;
		if (charsetName != null && charsetName.length() > 0) {
			try {
				smsgBytes = SUCCEEDED_MESSAGE.getBytes(charsetName);
				csName = charsetName;
			} catch (UnsupportedEncodingException ignoreEx) {
				smsgBytes = SUCCEEDED_MESSAGE.getBytes();
				csName = null;
			}
		} else {
			smsgBytes = SUCCEEDED_MESSAGE.getBytes();
			csName = null;
		}
		this._appName = appName;
		this._handler = handler;
		this._charsetName = csName;
		this._succeededMessageBytes = smsgBytes;
		this._builder = pb;
		this._bufInput = new ByteArrayOutputStream(BUFSIZE);
		this._bufError = new ByteArrayOutputStream(BUFSIZE);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getApplicationName() {
		return _appName;
	}
	
	public String getInputMessage() {
		String ret;
		if (_charsetName != null) {
			try {
				ret = _bufInput.toString(_charsetName);
			} catch (UnsupportedEncodingException ex) {
				ret = _bufInput.toString();
			}
		} else {
			ret = _bufInput.toString();
		}
		return ret;
	}
	
	public String getErrorMessage() {
		String ret;
		if (_charsetName != null) {
			try {
				ret = _bufError.toString(_charsetName);
			} catch (UnsupportedEncodingException ex) {
				ret = _bufError.toString();
			}
		} else {
			ret = _bufError.toString();
		}
		return ret;
	}
	
	public int getProcessExitCode() {
		return _exitCode;
	}
	
	public boolean isProcessRunning() {
		return _procIsRunning;
	}
	
	public boolean isProcessSucceeded() {
		return _procSucceeded;
	}
	
	public void startProcess() throws IOException
	{
		startProcess(System.out, System.err);
	}
	
	public void startProcess(PrintStream psout, PrintStream pserr) throws IOException
	{
		if (_process!=null || _watcher!=null) {
			throw new IllegalStateException("Process watcher already running!");
		}
		
		_process = _builder.start();
		long startTimeMillis = System.currentTimeMillis();
		_procIsRunning = true;
		
		// start Process stream handler
		new BufferedStreamHandler(_process.getInputStream(), psout, _bufInput, BUFSIZE).start();
		new BufferedStreamHandler(_process.getErrorStream(), pserr, _bufError, BUFSIZE).start();
		
		// start Process watcher
		_watcher = new ProcessWatcher(startTimeMillis);
		_watcher.start();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private int _idWatcher = 1;
	static private int _idHandler = 1;
	
	static private synchronized String getProcessWatcherName() {
		_idWatcher = (_idWatcher < Integer.MAX_VALUE ? _idWatcher+1 : 1);
		return "JAppExecutor-Watcher-" + _idWatcher;
	}
	
	static private synchronized String getOutputHandlerName() {
		_idHandler = (_idHandler < Integer.MAX_VALUE ? _idHandler+1 : 1);
		return "JAppExecutor-Handler-" + _idHandler;
	}

	/**
	 * 監視対象バッファの先頭に正常起動メッセージが出力されているかを検査する。
	 * @return	バッファの先頭が正常起動メッセージと同じ場合は 1 を返す。
	 * 			バッファの先頭が正常起動メッセージではない場合は -1 を返す。
	 * 			バッファリングされたバイト数が正常起動メッセージに満たない場合は 0 を返す。
	 */
	protected int checkSucceededMessage() {
		if (_bufInput.size() >= _succeededMessageBytes.length) {
			int len = _succeededMessageBytes.length;
			byte[] bbuf = _bufInput.toByteArray();
			for (int i = 0; i < len; i++) {
				if (bbuf[i] != _succeededMessageBytes[i]) {
					// not equals
					return (-1);
				}
			}
			//--- equals
			return (1);
		} else {
			return (0);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * プロセス監視スレッドの実行が終了したときに呼び出されるハンドラのインタフェース
	 */
	static public interface ProcessWatchHandler {
		public void processWatchFinished(JavaAppExecutor executor);
	}

	/**
	 * プロセス待機の割り込みを実行するタイマータスク
	 */
	protected final class InterruptTimerTask extends TimerTask
	{
		public void run() {
			//--- 一定間隔での待機中断
			_watcher.interrupt();
		}
	}

	/**
	 * プロセスを監視するスレッド
	 */
	protected final class ProcessWatcher extends Thread
	{
		private final long	_lStartTimeMillis;
		
		public ProcessWatcher(long startTimeMillis) {
			super(getProcessWatcherName());
			this._lStartTimeMillis = startTimeMillis;
		}
		
		public void run() {
			// 割り込みタイマーの起動
			Timer tm = new Timer();
			try {
				tm.schedule(new InterruptTimerTask(), WATCH_INTERVAL, WATCH_INTERVAL);
			} catch (Throwable ex) {
				tm = null;
			}
			if (tm == null) {
				// タイマー起動に失敗した場合は、監視しない
				return;
			}
			
			// 正常起動の監視
			int ret;
			boolean isTimeout = false;
			boolean isRunning = true;
			do {
				try {
					_process.waitFor();
					isRunning = false;
				} catch (InterruptedException ex) {
					if ((System.currentTimeMillis()-_lStartTimeMillis) >= WATCH_TIMEOUT) {
						// watch timeout
						isTimeout = true;
					}
				}
				
				ret = checkSucceededMessage();
				if (ret != 0) {
					break;
				}
			} while (isRunning && !isTimeout);
			
			// 割り込みタイマー停止
			tm.cancel();
			tm = null;
			
			// 正常起動の判定
			if (ret > 0) {
				// 正常起動の場合は、実行完了を待たずに終了
				_procSucceeded = true;
			}
			
			// 終了コードの取得
			if (isRunning) {
				try {
					_exitCode = _process.exitValue();
					_procIsRunning = false;
				} catch (IllegalThreadStateException ex) {}
			} else {
				_exitCode = _process.exitValue();
				_procIsRunning = false;
			}
			
			// 終了の通知
			if (_handler != null) {
				Runnable task = new Runnable(){
					public void run() {
						_handler.processWatchFinished(JavaAppExecutor.this);
					}
				};
				SwingUtilities.invokeLater(task);
			}
		}
	}

	/**
	 * 指定された容量までバッファに蓄積するストリームハンドラ。
	 * このクラスは、コンストラクタ呼び出しで指定された <code>ByteArrayOutputStream</code> オブジェクトに、
	 * 指定容量まで監視ストリームの内容をバッファリングする。
	 * 指定容量までバッファリングするか、バッファが無効な場合、バッファリングをせずにストリーム処理をする。
	 */
	static protected class BufferedStreamHandler extends Thread
	{
		private final InputStream	_inStream;
		private final PrintStream	_outStream;
		private final int			_buflimit;
		private ByteArrayOutputStream	_buffer;
		
		public BufferedStreamHandler(InputStream inStream, PrintStream outStream, ByteArrayOutputStream buf, int bufLimit) {
			super(getOutputHandlerName());
			this._inStream = inStream;
			this._outStream = outStream;
			this._buflimit = (bufLimit > 0 ? bufLimit : 0);
			this._buffer = buf;
		}
		
		public void run() {
			int	c = 0;

			// バッファリング
			if (_buflimit > 0 && _buffer != null) {
				int bufcnt = 0;
				if (_outStream != null) {
					try {
						while ((c = _inStream.read()) >= 0) {
							_outStream.write(c);
							_buffer.write(c);
							++bufcnt;
							if (bufcnt >= _buflimit) {
								// 指定容量到達でバッファリング終了
								break;
							}
						}
					}
					catch (IOException ignoreEx) {}
				} else {
					try {
						while ((c = _inStream.read()) >= 0) {
							_buffer.write(c);
							++bufcnt;
							if (bufcnt >= _buflimit) {
								// 指定容量到達でバッファリング終了
								break;
							}
						}
					}
					catch (IOException ignoreEx) {}
				}
			}

			// 不要なバッファの解放
			_buffer = null;

			// ストリーム監視
			if (c >= 0) {
				if (_outStream != null) {
					try {
						while ((c = _inStream.read()) >= 0) {
							_outStream.write(c);
						}
					}
					catch (IOException ignoreEx) {}
				} else {
					try {
						while ((c = _inStream.read()) >= 0) {}
					}
					catch (IOException ignoreEx) {}
				}
			}
		}
	}
}
