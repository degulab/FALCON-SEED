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
 * @(#)InterruptibleCommandExecutor.java	3.0.0	2014/03/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ssac.aadl.macro.process.InterruptibleManagedProcess;
import ssac.aadl.macro.process.InterruptibleProcessBuilder;
import ssac.aadl.macro.process.ManagedProcess;
import ssac.aadl.macro.process.ProcessUtil;
import ssac.aadl.macro.util.io.ReportPrinter;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.logging.AppLogger;


/**
 * コマンドを中断可能な別プロセスとして実行するクラス。
 * <p>
 * このクラスは、与えられたコマンドラインで子プロセスを起動する。
 * プロセスの標準出力、エラー出力は別スレッドで収集し、プロセスの
 * 終了監視も別スレッドで行われる。
 * プロセス起動の処理は、Java実装に依存するが、JAVAプログラムの場合に限り、
 * 中断処理が可能かラッパーをエントリクラスとして起動する。
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
public class InterruptibleCommandExecutor
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** コマンドステータス : コマンド未実行 **/
	static public final int	COMMAND_UNEXECUTED	= -1;
	/** コマンドステータス : コマンド（プロセス）が実行中であることを示す **/
	static public final int	COMMAND_RUNNING		= ManagedProcess.PROCESS_RUNNING;
	/** コマンドステータス : コマンド（プロセス）が終了していることを示す **/
	static public final int	COMMAND_FINISHED	= ManagedProcess.PROCESS_FINISHED;
	/** コマンドステータス : コマンド（プロセス）が中断処理中、もしくは強制停止中であることを示す **/
	static public final int	COMMAND_TERMINATING	= ManagedProcess.PROCESS_TERMINATING;
	/** コマンドステータス : コマンド（プロセス）が中断されたことを示す **/
	static public final int	COMMAND_INTERRUPTED	= ManagedProcess.PROCESS_INTERRUPTED;
	/** コマンドステータス : コマンド（プロセス）が強制停止されたことを示す **/
	static public final int	COMMAND_KILLED		= ManagedProcess.PROCESS_KILLED;
	/** コマンドステータス : コマンド（プロセス）が強制終了されたことを示す **/
	static public final int	COMMAND_DESTROYED	= ManagedProcess.PROCESS_DESTROYED;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 中断可能なプロセスを生成するプロセスビルダー **/
	private final InterruptibleProcessBuilder	_procBuilder;

	/** 実行するプロセスの標準出力、標準エラー出力の内容を受け取るライター **/
	private IOutputWriter						_outputWriter;

	/** 実行中のプロセス **/
	private InterruptibleManagedProcess			_cmdProc;
	/** プロセス実行終了時に呼び出されるハンドラー */
	private ExecutionTerminateHandler			_termHandler;

	/** プロセス終了を監視するスレッド **/
	private ProcessWatcher						_watcher;

	/** プロセス終了時に保存された終了コード **/
	private int		_exitCode;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public InterruptibleCommandExecutor(String...commands) {
		_procBuilder = new InterruptibleProcessBuilder(commands);
		ProcessUtil.enquoteCommand(_procBuilder.command());
	}
	
	public InterruptibleCommandExecutor(List<String> commands) {
		_procBuilder = new InterruptibleProcessBuilder(commands);
		ProcessUtil.enquoteCommand(_procBuilder.command());
	}

	//------------------------------------------------------------
	// Helper methods
	//------------------------------------------------------------

	/**
	 * テンポラリ領域に、標準的な重複しない名前のログファイルを生成する。
	 * ファイル名のプレフィックスは &quot;FSPROC&quot;、サフィックスは &quot;.log&quot; となる。
	 * なお、終了時に破棄されるように {@link java.io.File#deleteOnExit()} が呼び出される。
	 * @return	生成されたログファイルの抽象パス
	 * @throws IOException	ファイルが生成できなかった場合
	 * @throws SecurityException	セキュリティーマネージャーが存在する場合に、
	 * セキュリティーマネージャーの  SecurityManager.checkWrite(java.lang.String) メソッドがファイルの生成を許可しないとき
	 */
	static public File createDefaultLogFile() throws IOException
	{
		File file = File.createTempFile("FSPROC", ".log");
		file.deleteOnExit();
		return file;
	}

	/**
	 * コマンドリストに JAVA コマンドのパスを追加する。
	 * <em>javaCmdPath</em> が <tt>null</tt> もしくは空文字の場合、&quot;java&quot; 文字列を追加する。
	 * @param cmdList	対象の文字列リスト
	 * @param javaCmdPath	パスを示す文字列
	 * @since 1.17
	 */
	static public void appendJavaCommandPath(List<String> cmdList, String javaCmdPath) {
		if (!Strings.isNullOrEmpty(javaCmdPath)) {
			cmdList.add(javaCmdPath);
		} else {
			cmdList.add("java");
		}
	}

	/**
	 * コマンドリストに JAVA ヒープサイズを指定するオプションを追加する。
	 * このメソッドは、&quot;-Xmx&quot; と <em>maxSize</em> を連結した文字列をリストに追加する。
	 * <em>maxSize</em> が <tt>null</tt> もしくは空文字の場合、このメソッドは何もしない。
	 * @param cmdList	対象の文字列リスト
	 * @param maxSize	ヒープサイズを示す文字列
	 * @since 1.17
	 */
	static public void appendJavaMaxHeapSize(List<String> cmdList, String maxSize) {
		if (!Strings.isNullOrEmpty(maxSize)) {
			cmdList.add("-Xmx" + maxSize);
		}
	}

	/**
	 * コマンドリストに JAVA コマンドのオプションを追加する。
	 * <em>argsList</em> に指定された文字列に複数のオプションが含まれている場合、
	 * その内容を分割してリストに追加する。
	 * <em>argsList</em> が <tt>null</tt> もしくは空文字の場合、このメソッドは何もしない。
	 * @param cmdList	対象の文字列リスト
	 * @param argsList	オプションを示す文字列
	 * @since 1.17
	 */
	static public void appendJavaVMArgs(List<String> cmdList, String argsList) {
		if (!Strings.isNullOrEmpty(argsList)) {
			String[] args = Strings.splitCommandLineWithDoubleQuoteEscape(argsList);
			cmdList.addAll(Arrays.asList(args));
		}
	}

	/**
	 * コマンドリストにクラスパス・オプションを追加する。
	 * <em>paths</em> に格納されているパスを文字列に変換し、&quot;-classpath&quot; の
	 * パラメータとしてリストに追加する。
	 * <em>paths</em> が <tt>null</tt> もしくは空文字の場合、このメソッドは何もしない。
	 * @param cmdList	対象の文字列リスト
	 * @param paths		クラスパスの集合
	 * @since 1.17
	 */
	static public void appendClassPath(List<String> cmdList, ClassPathSet paths) {
		if (paths != null && !paths.isEmpty()) {
			cmdList.add("-classpath");
			cmdList.add(paths.getClassPathString());
		}
	}

	/**
	 * コマンドリストにメインクラス名を追加する。
	 * <em>mainClassName</em> が <tt>null</tt> もしくは空文字の場合、空文字をリストに追加する。
	 * @param cmdList		対象の文字列リスト
	 * @param mainClassName	メインクラス名を示す文字列
	 * @since 1.17
	 */
	static public void appendMainClassName(List<String> cmdList, String mainClassName) {
		if (!Strings.isNullOrEmpty(mainClassName)) {
			cmdList.add(mainClassName);
		} else {
			cmdList.add("");
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * プロセス実行に使用された、プロセス出力を受け取るライターオブジェクトを取得する。
	 * このメソッドが返すライターオブジェクトはプロセス実行時のものであり、{@link #getOutputWriter()} が返すオブジェクトとは異なる場合がある。
	 * @return	プロセス実行時に使用されたライターオブジェクト、使用されていない場合は <tt>null</tt>、
	 * 			プロセスが未実行の場合は、このオブジェクトに設定されているライターオブジェクト
	 * @since 3.0.0
	 */
	public IOutputWriter getProcessOutputWriter() {
		if (_watcher == null) {
			return _outputWriter;
		} else {
			return _watcher.getOutputWriter();
		}
	}

	/**
	 * このオブジェクトに設定されているプロセス出力を受け取るライターを取得する。
	 * @return	設定されているライターオブジェクト、設定されていない場合は <tt>null</tt>
	 * @since 3.0.0
	 */
	public IOutputWriter getOutputWriter() {
		return _outputWriter;
	}

	/**
	 * このオブジェクトに、プロセス出力を受け取る新しいライターを設定する。
	 * このメソッド呼び出し以降に実行されたコマンドの標準出力、標準エラー出力の内容は、設定されたライターに出力される。<br>
	 * <br>なお、プロセスが終了してもライターは閉じられない。
	 * <p>このメソッドは、実行中のコマンドには影響しない。
	 * @param newWriter	設定するライターオブジェクト、設定しない場合は <tt>null</tt>
	 * @since 3.0.0
	 */
	public void setOutputWriter(IOutputWriter newWriter) {
		_outputWriter = newWriter;
	}

	/**
	 * このオブジェクトが管理するプロセスの実行が何らかの要因で停止したときに呼び出される
	 * ハンドラーオブジェクトを取得する。
	 * @return	設定済みのハンドラーオブジェクト、設定されていない場合は <tt>null</tt>
	 */
	public ExecutionTerminateHandler getTerminateHandler() {
		return _termHandler;
	}

	/**
	 * このオブジェクトが管理するプロセスの実行が何らかの要因で停止したときに呼び出される
	 * ハンドラーオブジェクトを設定する。
	 * このメソッド呼び出し以降に実行されたコマンドが終了した際、設定されたハンドラーのメソッドが呼び出される。
	 * <p>このメソッドは、実行中のコマンドには影響しない。
	 * @param newHandler	ハンドラーオブジェクト、ハンドラーを設定しない場合は <tt>null</tt>
	 */
	public void setTerminateHandler(ExecutionTerminateHandler newHandler) {
		_termHandler = newHandler;
	}
	
	/**
	 * コマンドの状態を取得する。
	 * @return	次のステータスを返す。
	 * <dl>
	 * <dt>{@link #COMMAND_UNEXECUTED}</dt><dd>コマンド（プロセス）が未実行であることを示す</dd>
	 * <dt>{@link #COMMAND_RUNNING}</dt><dd>コマンド（プロセス）が実行中であることを示す</dd>
	 * <dt>{@link #COMMAND_FINISHED}</dt><dd>中断されずにコマンド（プロセス）が終了したことを示す</dd>
	 * <dt>{@link #COMMAND_TERMINATING}</dt><dd>コマンド（プロセス）中断処理中、もしくはコマンド（プロセス）強制停止中であることを示す</dd>
	 * <dt>{@link #COMMAND_INTERRUPTED}</dt><dd>コマンド（プロセス）が中断されたことを示す</dd>
	 * <dt>{@link #COMMAND_KILLED}</dt><dd>コマンド（プロセス）が強制的に停止されたことを示す</dd>
	 * <dt>{@link #COMMAND_DESTROYED}</dt><dd>コマンド（プロセス）が強制的に破棄(destroy)されたことを示す</dd>
	 * </dl>
	 * @since 3.0.0
	 */
	public int status() {
		if (_cmdProc == null) {
			return COMMAND_UNEXECUTED;
		} else {
			return _cmdProc.status();
		}
	}

	/**
	 * このオブジェクトが管理するコマンド（プロセス）が実行中かどうかを判定する。
	 * @return	実行中であれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isRunning() {
		if (_cmdProc == null) {
			return false;
		} else {
			return _cmdProc.isAlive();
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
	 * プロセス実行が中断要求に応答し中断された場合に <tt>true</tt> を返す。
	 * 未実行、実行中、中断要求への応答以外で終了している場合は <tt>false</tt> を返す。
	 * @since 1.21
	 */
	public boolean isProcessInterrupted() {
		if (_cmdProc == null) {
			return false;
		} else {
			return (_cmdProc.status() == COMMAND_INTERRUPTED);
		}
	}

	/**
	 * プロセスの実行開始時刻を取得する。
	 * @return	実行開始時刻を表すエポック
	 * @since 1.21
	 */
	public long getProcessStartTime() {
		return (_watcher==null ? 0L : _watcher.getProcessStartTime());
	}

	/**
	 * プロセスの実行終了時刻を取得する。
	 * 実行中の場合は、現在時刻を返す。
	 * @return	実行終了時刻を表すエポック
	 * @since 1.21
	 */
	public long getProcessEndTime() {
		return (_watcher==null ? 0L : _watcher.getProcessEndTime());
	}

	/**
	 * プロセスの実行時間を取得する。
	 * 実行中の場合は、現時点までの経過時間を返す。
	 * @return	実行開始から終了までの経過時間(ミリ秒)
	 */
	public long getProcessingTimeMillis() {
		return (_watcher==null ? 0L : _watcher.getProcessingTimeMillis());
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
			return _cmdProc.getProcess().toString();
		}
	}
	
	public void start() throws IOException {
		if (_cmdProc != null) {
			throw new RuntimeException("Already running!");
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
		//--- コマンド出力ライター
		IOutputWriter outputReceiver = _outputWriter;
		//--- 標準出力、標準エラー出力用の出力先オブジェクト
		ReportPrinter outReceiver = null;
		ReportPrinter errReceiver = null;
		if (outputReceiver != null) {
			outReceiver = new OutputPrinter(false, outputReceiver);
			errReceiver = new OutputPrinter(true, outputReceiver);
		}
		//--- 実行開始
		_exitCode = 0;
		long startTime = System.currentTimeMillis();
		_cmdProc = _procBuilder.start(outReceiver, errReceiver);
		AppLogger.debug("Process started!");
		//--- プロセス終了待機スレッドの開始
		_watcher = new ProcessWatcher(_termHandler, outputReceiver, startTime);
		_watcher.start();
	}

	/**
	 * 管理対象プロセスが実行中であれば、その実行を中断する。
	 * 起動したプロセスが中断要求に応答しない場合、このプロセスは終了しない場合がある。
	 */
	public void terminate() {
		if (_cmdProc != null) {
			_cmdProc.terminate();
		}
	}

	/**
	 * 管理対象プロセスが実行中であれば、その実行を強制的に停止する。
	 * プロセスを破棄せずに、実行中のプロセスを停止させる。
	 */
	public void kill() {
		if (_cmdProc != null) {
			_cmdProc.kill();
		}
	}

	/**
	 * 管理対象プロセスの監視を中止し、プロセス実行中であればそのプロセスを強制終了(破棄)する。
	 * このメソッドでは、プロセス実行中なら強制的に破棄するので、安全な停止とはならない。
	 */
	public void stopAndCleanup() {
		if (_cmdProc == null) {
			return;	// run not yet
		}
		
		// terminate ProcessWatcher
		if (_watcher != null && _watcher.isAlive()) {
			if (AppLogger.isWarnEnabled()) {
				AppLogger.warn("[" + getProcessIdentifierString() + "] watcher interrupt.");
			}
			_watcher.interrupt();
			try {
				_watcher.join();
			} catch (InterruptedException ex) {
				if (AppLogger.isWarnEnabled()) {
					AppLogger.warn("[" + getProcessIdentifierString() + "] Interrupted to join watcher!", ex);
				}
				ex = null;	// ignore InterruptedException
			}
		}
	}

	// コマンド終了まで待つ
	public void waitFor() throws InterruptedException {
		if (_watcher != null && _watcher.isAlive()) {
			_watcher.join();
		}
	}

	// コマンド終了まで、指定された最大時間(単位：ミリ秒)待つ
	public void waitFor(long millis) throws InterruptedException {
		if (_watcher != null && _watcher.isAlive()) {
			_watcher.join(millis);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private int idWatcher = 1;
	
	static private synchronized String getProcessWatcherName() {
		idWatcher = (idWatcher < Integer.MAX_VALUE ? idWatcher+1 : 1);
		return "Executor-Watcher-" + idWatcher;
	}

	//------------------------------------------------------------
	// implement ProcessWatcher class
	//------------------------------------------------------------
	
	protected final class ProcessWatcher extends Thread
	{
		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		/** コマンド終了時に呼び出すハンドラー **/
		private ExecutionTerminateHandler	_handler;
		/** コマンド出力を受け取るライター **/
		private IOutputWriter			_outReceiver;
		/** プロセス実行開始時刻 **/
		private long					_startTime;
		/** プロセス実行終了時刻 **/
		private long					_endTime;
		/** プロセス実行中であることを示すフラグ **/
		private volatile boolean		_running;
		/** 割り込み発生フラグ **/
		private boolean					_interrupted;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		public ProcessWatcher(ExecutionTerminateHandler handler, IOutputWriter receiver, long startTimeMillis) {
			super(getProcessWatcherName());
			_handler     = handler;
			_outReceiver = receiver;
			_startTime   = startTimeMillis;
			_endTime     = startTimeMillis;
			_interrupted = false;
			_running = (_cmdProc==null ? false : _cmdProc.isAlive());
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		/**
		 * プロセス実行の出力を受け取るライターオブジェクトを取得する。
		 * @return	設定されているライターオブジェクト、設定されていない場合は <tt>null</tt>
		 */
		public IOutputWriter getOutputWriter() {
			return _outReceiver;
		}

		/**
		 * プロセス実行が中断された場合に <tt>true</tt> を返す。
		 * @since 1.21
		 */
		public boolean isProcessInterrupted() {
			return _interrupted;
		}

		/**
		 * プロセスの実行開始時刻を取得する。
		 * @return	実行開始時刻を表すエポック
		 * @since 1.21
		 */
		public long getProcessStartTime() {
			return _startTime;
		}

		/**
		 * プロセスの実行終了時刻を取得する。
		 * 実行中の場合は現在時刻を返す。
		 * @return	実行終了時刻を表すエポック
		 * @since 1.21
		 */
		public long getProcessEndTime() {
			return (_running ? System.currentTimeMillis() : _endTime);
		}
		
		public long getProcessingTimeMillis() {
			if (_running)
				return (System.currentTimeMillis() - _startTime);
			else
				return (_endTime - _startTime);
		}
		
		public void run() {
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("Start [" + getName() + "] ProcessWatcher thread.");
			}
			
			if (_cmdProc != null) {
				// プロセス終了まで待機
				try {
					_exitCode = _cmdProc.waitFor();
					_interrupted = false;
				}
				catch (InterruptedException ex) {
					if (AppLogger.isDebugEnabled()) {
						AppLogger.debug("Interrupted [" + getName() + "] ProcessWatcher thread.", ex);
					}
					_exitCode = Integer.MIN_VALUE;
					_interrupted = true;
				}
				
				// プロセス後処理
				if (_cmdProc.isAlive()) {
					_cmdProc.kill();
					try {
						_exitCode = _cmdProc.waitFor();
					} catch (InterruptedException ex) {
						if (AppLogger.isDebugEnabled()) {
							AppLogger.debug("Interrupted [" + getName() + "] ProcessWatcher thread to wait kill.", ex);
						}
						_exitCode = Integer.MIN_VALUE;
						_interrupted = true;
					}
				}
				
				// プロセスクリーンアップ
				if (_cmdProc.isAlive()) {
					_cmdProc.destroy();
					try {
						_exitCode = _cmdProc.waitFor();
					} catch (InterruptedException ex) {
						if (AppLogger.isDebugEnabled()) {
							AppLogger.debug("Interrupted [" + getName() + "] ProcessWatcher thread to wait destroy.", ex);
						}
						_exitCode = Integer.MIN_VALUE;
						_interrupted = true;
					}
				}
				_endTime = System.currentTimeMillis();
				_running = false;
				//--- プロセスストリームのクローズ
				Files.closeStream(_cmdProc.getProcess().getInputStream());
				Files.closeStream(_cmdProc.getProcess().getErrorStream());
				Files.closeStream(_cmdProc.getProcess().getOutputStream());
				//--- プロセス中断メッセージ
				int stat = _cmdProc.status();
				if (stat != COMMAND_FINISHED || _interrupted) {
					AppLogger.warn("[" + getProcessIdentifierString() + "] process was canceled!");
					if (_outReceiver != null) {
						_outReceiver.println(true, "\nProcess canceled!");
					}
				}
				//--- cleanup
				try {
					_cmdProc.cleanup();
				} catch (Throwable ex) {
					if (AppLogger.isWarnEnabled()) {
						AppLogger.warn("Failed to cleanup [" + getProcessIdentifierString() + "] process.", ex);
					}
				}
			}
			else if (_running) {
				_endTime = System.currentTimeMillis();
				_running = false;
			}
			
			// プロセス終了ハンドラの呼び出し
			if (_handler != null) {
				_handler.executionTerminated(InterruptibleCommandExecutor.this);
			}
			
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("End [" + getName() + "] ProcessWatcher thread.");
			}
		}
	}
}
