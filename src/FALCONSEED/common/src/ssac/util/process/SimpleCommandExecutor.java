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
 * @(#)SimpleCommandExecutor.java	3.0.0	2014/03/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.Map;

import ssac.aadl.macro.process.ProcessUtil;
import ssac.util.io.Files;
import ssac.util.logging.AppLogger;

/**
 * コマンドの単純実行を行うクラス。
 * <p>このクラスは、与えられたコマンドラインで子プロセスを起動し、
 * 指定のタイムアウト時間が経過するまで処理をブロックする。
 * タイムアウト時間が経過してもプロセスが終了しない場合は、{@link java.lang.Process#destroy()} を呼び出し強制終了する。
 * <p>プロセスの標準出力、標準エラー出力は、それぞれ文字列バッファに格納され、個別に取得することができる。
 * <p>この実装は、Windowsプラットフォームの場合、コマンド文字列を修飾する。
 * これは、Windowsプラットフォームにおいてプロセスを開始すると、アスタリスクや
 * クエスチョン、ダブルクオーテーションがワイルドカードやエスケープとして
 * 認識されてしまうため、これを回避するためにダブルクオーテーションによる
 * エスケープを行う。通常、コマンド文字列要素にアスタリスク、クエスチョンが
 * 含まれている場合は、その要素の先頭と終端にダブルクオーテーションを付加する。
 * ダブルクオーテーションが含まれている場合、バックスラッシュ(￥記号)を
 * ダブルクオーテーション一つずつに追加し、ダブルクオーテーションをエスケープ
 * する。
 * Windows以外のプラットフォームでは、入力文字列そのものが新しいプロセスに
 * 渡される。
 * 
 * @version 3.0.0	2014/03/25
 * @since 3.0.0
 */
public class SimpleCommandExecutor
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** プロセスビルダー **/
	private final ProcessBuilder	_procBuilder;

	/** 実行中のプロセス **/
	private Process					_cmdProc;
	
	/** 標準出力の内容を保持するバッファ **/
	ByteArrayOutputStream			_bufStdout = new ByteArrayOutputStream();
	/** 標準エラー出力の内容を保持するバッファ **/
	ByteArrayOutputStream			_bufStderr = new ByteArrayOutputStream();

	/** プロセスが強制終了されたことを示すフラグ **/
	private boolean	_destroyed;
	/** プロセス終了時に保存された終了コード **/
	private int		_exitCode;
	/** プロセス実行開始時刻 **/
	private long	_ltStart = 0L;
	/** プロセス実行停止時刻 **/
	private long	_ltEnd   = 0L;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SimpleCommandExecutor(String...commands) {
		_procBuilder = new ProcessBuilder(commands);
		ProcessUtil.enquoteCommand(_procBuilder.command());
	}
	
	public SimpleCommandExecutor(List<String> commands) {
		_procBuilder = new ProcessBuilder(commands);
		ProcessUtil.enquoteCommand(_procBuilder.command());
	}

	//------------------------------------------------------------
	// Helper methods
	//------------------------------------------------------------

	/**
	 * コマンドリストに JAVA コマンドのパスを追加する。
	 * <em>javaCmdPath</em> が <tt>null</tt> もしくは空文字の場合、&quot;java&quot; 文字列を追加する。
	 * @param cmdList	対象の文字列リスト
	 * @param javaCmdPath	パスを示す文字列
	 */
	static public void appendJavaCommandPath(List<String> cmdList, String javaCmdPath) {
		CommandExecutor.appendJavaCommandPath(cmdList, javaCmdPath);
	}

	/**
	 * コマンドリストに JAVA ヒープサイズを指定するオプションを追加する。
	 * このメソッドは、&quot;-Xmx&quot; と <em>maxSize</em> を連結した文字列をリストに追加する。
	 * <em>maxSize</em> が <tt>null</tt> もしくは空文字の場合、このメソッドは何もしない。
	 * @param cmdList	対象の文字列リスト
	 * @param maxSize	ヒープサイズを示す文字列
	 */
	static public void appendJavaMaxHeapSize(List<String> cmdList, String maxSize) {
		CommandExecutor.appendJavaMaxHeapSize(cmdList, maxSize);
	}

	/**
	 * コマンドリストに JAVA コマンドのオプションを追加する。
	 * <em>argsList</em> に指定された文字列に複数のオプションが含まれている場合、
	 * その内容を分割してリストに追加する。
	 * <em>argsList</em> が <tt>null</tt> もしくは空文字の場合、このメソッドは何もしない。
	 * @param cmdList	対象の文字列リスト
	 * @param argsList	オプションを示す文字列
	 */
	static public void appendJavaVMArgs(List<String> cmdList, String argsList) {
		CommandExecutor.appendJavaVMArgs(cmdList, argsList);
	}

	/**
	 * コマンドリストにクラスパス・オプションを追加する。
	 * <em>paths</em> に格納されているパスを文字列に変換し、&quot;-classpath&quot; の
	 * パラメータとしてリストに追加する。
	 * <em>paths</em> が <tt>null</tt> もしくは空文字の場合、このメソッドは何もしない。
	 * @param cmdList	対象の文字列リスト
	 * @param paths		クラスパスの集合
	 */
	static public void appendClassPath(List<String> cmdList, ClassPathSet paths) {
		CommandExecutor.appendClassPath(cmdList, paths);
	}

	/**
	 * コマンドリストにメインクラス名を追加する。
	 * <em>mainClassName</em> が <tt>null</tt> もしくは空文字の場合、空文字をリストに追加する。
	 * @param cmdList		対象の文字列リスト
	 * @param mainClassName	メインクラス名を示す文字列
	 */
	static public void appendMainClassName(List<String> cmdList, String mainClassName) {
		CommandExecutor.appendMainClassName(cmdList, mainClassName);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトが管理するコマンド（プロセス）が実行中かどうかを判定する。
	 * @return	実行中であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isRunning() {
		if (_cmdProc == null) {
			return false;
		} else {
			return ProcessUtil.isProcessAlive(_cmdProc);
		}
	}

	/**
	 * このオブジェクトが管理するコマンド（プロセス）の終了コードを取得する。
	 * コマンドが未実行もしくは実行中の場合は 0 を返す。
	 * @return	コマンドの終了コード
	 */
	public int getExitCode() {
		return _exitCode;
	}

	/**
	 * 実行したプロセスの標準出力に出力された文字列を返す。
	 * @return	標準出力の内容をすべて連結した文字列
	 */
	public String getOutputString() {
		synchronized (_bufStdout) {
			return _bufStdout.toString();
		}
	}

	/**
	 * 実行したプロセスの標準エラー出力に出力された文字列を返す。
	 * @return	標準エラー出力の内容をすべて連結した文字列
	 */
	public String getErrorString() {
		synchronized (_bufStderr) {
			return _bufStderr.toString();
		}
	}

	/**
	 * プロセス実行中に {@link java.lang.Process#destroy()} によって強制終了されたかどうかを判定する。
	 * @return	強制終了されていれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isProcessDestroyed() {
		return _destroyed;
	}

	/**
	 * プロセスの実行開始時刻を取得する。
	 * @return	実行開始時刻を表すエポック
	 * @since 1.21
	 */
	public long getProcessStartTime() {
		return (_cmdProc==null ? 0L : _ltStart);
	}

	/**
	 * プロセスの実行終了時刻を取得する。
	 * 実行中の場合は、現在時刻を返す。
	 * @return	実行終了時刻を表すエポック
	 */
	public long getProcessEndTime() {
		if (_cmdProc == null) {
			return 0L;
		}
		else if (ProcessUtil.isProcessAlive(_cmdProc)) {
			return System.currentTimeMillis();
		}
		else {
			return _ltEnd;
		}
	}

	/**
	 * プロセスの実行時間を取得する。
	 * 実行中の場合は、現時点までの経過時間を返す。
	 * @return	実行開始から終了までの経過時間(ミリ秒)
	 */
	public long getProcessingTimeMillis() {
		return (getProcessEndTime() - getProcessStartTime());
	}

	/*--- コマンドの変更は許可しない ---
	public List<String> command() {
		return this.procBuilder.command();
	}
	---*/
	
	public Map<String,String> environment() {
		return _procBuilder.environment();
	}
	
	public File getWorkDirectory() {
		return _procBuilder.directory();
	}
	
	public void setWorkDirectory(File dir) {
		_procBuilder.directory(dir);
	}

	public boolean redirectErrorStream() {
		return _procBuilder.redirectErrorStream();
	}

	public void redirectErrorStream(boolean redirectErrorStream) {
		_procBuilder.redirectErrorStream(redirectErrorStream);
	}
	
	public String getProcessIdentifierString() {
		if (_cmdProc == null) {
			return "unknown process";
		} else {
			return _cmdProc.toString();
		}
	}

	/**
	 * プロセスの実行を開始し、指定されたタイムアウト時間が経過するまでプロセス終了を待機する。
	 * タイムアウト時間が経過してもプロセスが終了しなかった場合、そのプロセスは強制終了され、
	 * 終了コードには {@link java.lang.Integer#MIN_VALUE} が返される。
	 * <p>プロセス待機中に、このメソッドを実行中のスレッドに対して割り込みが発生した場合、
	 * 実行中のプロセスを強制終了して {@link java.io.InterruptedIOException} 例外をスローする。
	 * @param timeout	プロセス終了までの待機時間(ミリ秒)、0 以下の値を指定した場合はプロセス終了まで待機する
	 * @return	プロセスの終了コードを返す。待機時間が経過した場合は {@link java.lang.Integer#MIN_VALUE} を返す。
	 * @throws OutOfMemoryError			プロセスが出力する内容を保持するバッファのメモリが不足した場合
	 * @throws InterruptedIOException	プロセス待機中にこのスレッドに対して割り込みが発生した場合
	 * @throws IOException	コマンドの実行に失敗した場合
	 * @throws IllegalStateException	コマンドが実行中、もしくはすでに実行が完了している場合
	 */
	public int exec(long timeout) throws IOException
	{
		if (_cmdProc != null) {
			throw new IllegalStateException("Already running!");
		}
		
		// Trace
		if (AppLogger.isInfoEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("[" + getProcessIdentifierString() + "] Start process...");
			//--- commands
			sb.append("\n  << Commands >>");
			for (int i = 0; i < _procBuilder.command().size(); i++) {
				String str = _procBuilder.command().get(i);
				sb.append("\n    cmd[");
				sb.append(i);
				sb.append("]=");
				if (str != null) {
					sb.append("[");
					sb.append(str);
					sb.append("]");
				} else {
					sb.append("null");
				}
			}
			//--- envs
			if (AppLogger.isTraceEnabled()) {
				Map<String,String> envmap = _procBuilder.environment();
				sb.append("\n  << Environments >>");
				if (envmap != null && !envmap.isEmpty()) {
					for (Map.Entry<String,String> elem : envmap.entrySet()) {
						sb.append("\n");
						sb.append(elem);
					}
				} else {
					sb.append("\n    nothing!");
				}
			}
			//--- workdir
			File dir = _procBuilder.directory();
			sb.append("\n  << Working directory >>");
			if (dir != null) {
				sb.append("\n    WorkDir=");
				sb.append(dir.getAbsolutePath());
			} else {
				sb.append("\n    nothing!");
			}
			AppLogger.info(sb.toString());
		}
		
		// コマンド実行
		ProcessStreamBufferThread thout = null;
		ProcessStreamBufferThread therr = null;
		_cmdProc = _procBuilder.start();
		_ltStart = System.currentTimeMillis();
		_ltEnd   = _ltStart;
		String procid = _cmdProc.toString();
		thout = new ProcessStreamBufferThread(procid, _cmdProc.getInputStream(), _bufStdout);
		therr = new ProcessStreamBufferThread(procid, _cmdProc.getErrorStream(), _bufStderr);
		thout.start();
		therr.start();
		//--- 待機
		try {
			if (timeout > 0L) {
				// 指定時間まで待機
				for (; ProcessUtil.isProcessAlive(_cmdProc) && (System.currentTimeMillis()-_ltStart) < timeout; ) {
					Thread.sleep(100L);	// 100ミリ秒間隔でチェック
				}
			}
			else {
				// 終了まで待機
				_exitCode = _cmdProc.waitFor();
				_ltEnd = System.currentTimeMillis();
			}
		}
		catch (InterruptedException ex) {
			String strmsg = "[" + _cmdProc.toString() + "] Simple command execution was interrupted!";
			AppLogger.debug(strmsg, ex);
			throw new InterruptedIOException(strmsg);
		}
		finally {
			//--- 待機タイムアウトならプロセス停止
			if (ProcessUtil.isProcessAlive(_cmdProc)) {
				_cmdProc.destroy();
				_exitCode = Integer.MIN_VALUE;
				_destroyed = true;
			}
			_ltEnd = System.currentTimeMillis();
			//--- ストリームをすべて閉じる
			Files.closeStream(_cmdProc.getInputStream());
			Files.closeStream(_cmdProc.getErrorStream());
			Files.closeStream(_cmdProc.getOutputStream());
		}
		
		// プロセス出力監視スレッドの待機
		try {
			thout.join();
		} catch (InterruptedException ignoreEx) {}
		try {
			therr.join();
		} catch (InterruptedException ignoreEx) {}
		
		// スレッドのエラーチェック
		//--- stdout
		Throwable lastError = thout.getLastError();
		if (lastError instanceof OutOfMemoryError) {
			String strmsg = "[" + _cmdProc.toString() + "] Out of memory : Simple command output(stdout)";
			AppLogger.debug(strmsg, lastError);
			throw new OutOfMemoryError(strmsg);
		}
		else if (AppLogger.isDebugEnabled() && lastError != null) {
			AppLogger.debug("[" + _cmdProc.toString() + "] stdout receiver thread caught exception.", lastError);
		}
		//--- stderr
		lastError = therr.getLastError();
		if (lastError instanceof OutOfMemoryError) {
			String strmsg = "[" + _cmdProc.toString() + "] Out of memory : Simple command output(stderr)";
			AppLogger.debug(strmsg, lastError);
			throw new OutOfMemoryError(strmsg);
		}
		else if (AppLogger.isDebugEnabled() && lastError != null) {
			AppLogger.debug("[" + _cmdProc.toString() + "] stderr receiver thread caught exception.", lastError);
		}
		
		// 終了コードを返す
		return _exitCode;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// implement StreamBufferThread class
	//------------------------------------------------------------
	
	protected final class ProcessStreamBufferThread extends Thread
	{
		/** このスレッドの停止要因となった例外 **/
		private Throwable				_lastError;
		/** 読み込み対象のストリーム **/
		private InputStream				_inStream;
		/** 読み込んだ内容を保持するバッファ **/
		private ByteArrayOutputStream	_outBuffer;
		
		public ProcessStreamBufferThread(String procName, InputStream targetStream, ByteArrayOutputStream buffer) {
			super("SimpleExecutor-" + String.valueOf(procName) + "-Stream");
			_lastError = null;
			_inStream = targetStream;
			_outBuffer = buffer;
		}
		
		public Throwable getLastError() {
			return _lastError;
		}
		
		public void run() {
			byte[] buffer = new byte[2048];
			
			// transfer stream data
			try {
				int r = _inStream.read(buffer);
				for (; r >= 0; ) {
					synchronized (_outBuffer) {
						_outBuffer.write(buffer, 0, r);
					}
					r = _inStream.read(buffer);
				}
			}
			catch (Throwable ex) {
				_lastError = ex;
			}
			finally {
				try {
					synchronized (_outBuffer) {
						_outBuffer.flush();
					}
				} catch (Throwable ex) {
					if (_lastError == null) {
						_lastError = ex;
					}
				}
			}
		}
	}
}
