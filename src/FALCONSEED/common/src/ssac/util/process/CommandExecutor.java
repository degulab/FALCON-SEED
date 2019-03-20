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
 * @(#)CommandExecutor.java	1.21	2012/08/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommandExecutor.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommandExecutor.java	1.10	2008/12/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.logging.AppLogger;

/**
 * コマンドを別プロセスとして実行するクラス。
 * <p>
 * このクラスは、与えられたコマンドラインで子プロセスを起動する。
 * プロセスの標準出力、エラー出力は別スレッドで収集し、プロセスの
 * 終了監視も別スレッドで行われる。
 * プロセス起動の処理は、Java実装に依存する。
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
 * @version 1.21	2012/08/28
 * @since 1.10
 */
public final class CommandExecutor
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/**
	 * このアプリケーションが動作するプラットフォームが Windows であることを示すフラグ
	 */
	static private final boolean isWindows;
	
	private final ProcessBuilder	procBuilder;
	private final CommandOutput	cmdOutput;
	
	private boolean		isRunning;
	private int			exitCode;
	private Process		cmdProc;
	private OutputHandler	hOutput;
	private ProcessWatcher	watcher;
	
	static {
		// Initialize platform type
		String osname = System.getProperty("os.name");
		isWindows = (osname.indexOf("Windows") >= 0);
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CommandExecutor(String...commands) {
		this.procBuilder = new ProcessBuilder(commands);
		this.cmdOutput = new CommandOutput();
		enquoteCommand(this.procBuilder.command());
	}
	
	public CommandExecutor(List<String> commands) {
		this.procBuilder = new ProcessBuilder(commands);
		this.cmdOutput = new CommandOutput();
		enquoteCommand(this.procBuilder.command());
	}

	//------------------------------------------------------------
	// Helper methods
	//------------------------------------------------------------

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
	
	public CommandOutput getCommandOutput() {
		return this.cmdOutput;
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}
	
	public int getExitCode() {
		return this.exitCode;
	}

	/**
	 * プロセス実行が中断された場合に <tt>true</tt> を返す。
	 * @since 1.21
	 */
	public boolean isProcessInterrupted() {
		return (this.watcher==null ? false : this.watcher.isProcessInterrupted());
	}

	/**
	 * プロセスの実行開始時刻を取得する。
	 * @return	実行開始時刻を表すエポック
	 * @since 1.21
	 */
	public long getProcessStartTime() {
		return (this.watcher==null ? 0L : this.watcher.getProcessStartTime());
	}

	/**
	 * プロセスの実行終了時刻を取得する。
	 * 実行中の場合は、現在時刻を返す。
	 * @return	実行終了時刻を表すエポック
	 * @since 1.21
	 */
	public long getProcessEndTime() {
		return (this.watcher==null ? 0L : this.watcher.getProcessEndTime());
	}
	
	public long getProcessingTimeMillis() {
		return (this.watcher != null ? this.watcher.getProcessingTimeMillis() : 0L);
	}

	/*--- コマンドの変更は許可しない ---
	public List<String> command() {
		return this.procBuilder.command();
	}
	---*/
	
	public Map<String,String> environment() {
		return this.procBuilder.environment();
	}
	
	public File getWorkDirectory() {
		return this.procBuilder.directory();
	}
	
	public void setWorkDirectory(File dir) {
		this.procBuilder.directory(dir);
	}
	
	public String getProcessIdentifierString() {
		if (this.cmdProc != null) {
			return this.cmdProc.toString();
		} else {
			return "unknown process";
		}
	}
	
	public void start() throws IOException {
		if (this.cmdProc != null) {
			throw new RuntimeException("Already running!");
		}
		
		// Trace
		if (AppLogger.isInfoEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("[" + getProcessIdentifierString() + "] Start process...");
			//--- commands
			sb.append("\n  << Commands >>");
			for (int i = 0; i < this.procBuilder.command().size(); i++) {
				String str = this.procBuilder.command().get(i);
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
				Map<String,String> envmap = this.procBuilder.environment();
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
			File dir = this.procBuilder.directory();
			sb.append("\n  << Working directory >>");
			if (dir != null) {
				sb.append("\n    WorkDir=");
				sb.append(dir.getAbsolutePath());
			} else {
				sb.append("\n    nothing!");
			}
			AppLogger.info(sb.toString());
		}
		
		// 実行開始
		this.exitCode = 0;
		long startTime = System.currentTimeMillis();
		this.cmdProc = this.procBuilder.start();
		this.isRunning = true;
		
		// 出力ハンドラ開始
		this.hOutput = new OutputHandler(this.cmdProc);
		this.hOutput.start();
		
		// プロセス終了ハンドラ開始
		this.watcher = new ProcessWatcher(startTime);
		this.watcher.start();
	}
	
	public void destroy() {
		// is Running?
		if (this.cmdProc == null)
			return;		// run not yet
		
		// terminate ProcessWatcher
		if (this.watcher != null) {
			if (this.watcher.isAlive()) {
				this.watcher.interrupt();
				try {
					this.watcher.join();
				} catch (InterruptedException ex) {
					ex = null;
				}
			}
		}
		
		// 念のため
		this.isRunning = false;
		
		// terminate OutputHandler
		if (this.hOutput != null) {
			if (this.hOutput.isAlive()) {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException ex) {
					ex = null; // no action
				}
				this.hOutput.interrupt();
				try {
					this.hOutput.join();
				} catch (InterruptedException ex) {
					ex = null;
				}
			}
		}
		
		// ストリームリソースを開放
		Files.closeStream(this.cmdProc.getInputStream());
		Files.closeStream(this.cmdProc.getErrorStream());
		Files.closeStream(this.cmdProc.getOutputStream());
		
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("[" + getProcessIdentifierString() + "] destroied!");
		}
	}

	// コマンド終了まで待つ
	public void waitFor() throws InterruptedException {
		if (this.watcher != null && this.watcher.isAlive()) {
			this.watcher.join();
		}
	}

	// コマンド終了まで、指定された最大時間(単位：ミリ秒)待つ
	public void waitFor(long millis) throws InterruptedException {
		if (this.watcher != null && this.watcher.isAlive()) {
			this.watcher.join(millis);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private int idWatcher = 1;
	static private int idHandler = 1;
	
	static private synchronized String getProcessWatcherName() {
		idWatcher = (idWatcher < Integer.MAX_VALUE ? idWatcher+1 : 1);
		return "Executor-Watcher-" + idWatcher;
	}
	
	static private synchronized String getOutputHandlerName() {
		idHandler = (idHandler < Integer.MAX_VALUE ? idHandler+1 : 1);
		return "Executor-Handler-" + idHandler;
	}
	
	/**
	 * Windows プラットフォームにおいて、ダブルクオートによる
	 * エンクオートが必要な文字
	 */
	static private final char[] enquoteChars = {'*', '?', '\"'};
	
	/**
	 * コマンド文字列リストのエンクオート。
	 * このメソッドは、Windowsプラットフォームにおいて、エンクオートが
	 * 必要なコマンド文字列にのみ、エンクオート(ダブルクオートで囲む処理)を
	 * 行う。
	 * @param command	エンクオートするコマンド文字列リスト
	 * @return	エンクオートが行われた場合に <tt>true</tt> を返す。
	 */
	static private boolean enquoteCommand(List<String> command) {
		// Windows 以外は、enquote しない
		if (!isWindows) {
			return false;
		}
		
		// enquote
		boolean enquoted = false;
		for (int idx = 0; idx < command.size(); idx++) {
			String cmd = command.get(idx);
			if (!Strings.isNullOrEmpty(cmd) && Strings.contains(cmd, enquoteChars)) {
				cmd = "\"" + cmd.replaceAll("\"", "\\\\\"") + "\"";
				command.set(idx, cmd);
				enquoted = true;
			}
		}
		return enquoted;
	}

	//------------------------------------------------------------
	// implement ProcessWatcher class
	//------------------------------------------------------------
	
	protected final class ProcessWatcher extends Thread
	{
		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		private long		startTime;
		private long		endTime;
		private boolean	interrupted;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		public ProcessWatcher(long startTimeMillis) {
			super(getProcessWatcherName());
			this.startTime = startTimeMillis;
			this.endTime = startTimeMillis;
			this.interrupted = false;
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		/**
		 * プロセス実行が中断された場合に <tt>true</tt> を返す。
		 * @since 1.21
		 */
		public boolean isProcessInterrupted() {
			return interrupted;
		}

		/**
		 * プロセスの実行開始時刻を取得する。
		 * @return	実行開始時刻を表すエポック
		 * @since 1.21
		 */
		public long getProcessStartTime() {
			return startTime;
		}

		/**
		 * プロセスの実行終了時刻を取得する。
		 * 実行中の場合は現在時刻を返す。
		 * @return	実行終了時刻を表すエポック
		 * @since 1.21
		 */
		public long getProcessEndTime() {
			return (isRunning ? System.currentTimeMillis() : endTime);
		}
		
		public long getProcessingTimeMillis() {
			if (isRunning)
				return (System.currentTimeMillis() - this.startTime);
			else
				return (this.endTime - this.startTime);
		}
		
		public void run() {
			AppLogger.debug("Start ProcessWatcher thread.");
			if (cmdProc != null) {
				try {
					cmdProc.waitFor();
					exitCode = cmdProc.exitValue();
					interrupted = false;
				}
				catch (InterruptedException ex) {
					// スレッドへの割り込みなら、プロセスを中断
					AppLogger.debug("ProcessWatcher interrupted!", ex);
					cmdProc.destroy();
					try {
						cmdProc.waitFor();
						exitCode = cmdProc.exitValue();
					}
					catch (InterruptedException intExp) {
						intExp = null;
					}
					catch (Throwable unkExp) {
						AppLogger.debug(unkExp);
					}
					try {
						sleep(100L);
					} catch (InterruptedException ignoreExp) {
						ignoreExp = null;
					}
					exitCode = Integer.MIN_VALUE;
					interrupted = true;
					cmdOutput.push(true, "\nProcess canceled!\n");
				}
			}
			isRunning = false;
			endTime = System.currentTimeMillis();
			AppLogger.debug("End ProcessWatcher thread.");
		}
	}

	//------------------------------------------------------------
	// implement OutputHandler class
	//------------------------------------------------------------
	
	protected final class OutputHandler extends Thread
	{
		//------------------------------------------------------------
		// Definitions
		//------------------------------------------------------------
		
		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------
		
		private final InputStream stdout;
		private final InputStream stderr;
		private final ByteArrayOutputStream outArray;
		private final ByteArrayOutputStream errArray;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		protected OutputHandler(Process proc) {
			super(getOutputHandlerName());
			stdout = proc.getInputStream();
			stderr = proc.getErrorStream();
			outArray = new ByteArrayOutputStream();
			errArray = new ByteArrayOutputStream();
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------
		
		public void run() {
			AppLogger.debug("Start OutputHandler thread.");

			// 実行時の表示更新との同期用ウェイト
			final long sleepTime = 100L;
			
			while (isRunning) {
				storeBytes();
				try {
					sleep(sleepTime);
				}
				catch (InterruptedException ex) {
					AppLogger.debug("OutputHandler interrupted!", ex);
					storeBytes();
				}
			}
			
			while(storeBytes());
			AppLogger.debug("End OutputHandler thread.");
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------
		
		private boolean storeBytes() {
			storeBytes(stdout, outArray);
			storeBytes(stderr, errArray);

			boolean exist = false;
			if (outArray.size() > 0) {
				cmdOutput.push(false, outArray.toString());
				outArray.reset();
				exist = true;
			}
			if (errArray.size() > 0) {
				cmdOutput.push(true, errArray.toString());
				errArray.reset();
				exist = true;
			}
			
			return exist;
		}
		
		private boolean storeBytes(final InputStream target, final ByteArrayOutputStream buffer) {
			boolean flgRead = false;
			try {
				int size = target.available();
				if (size > 0) {
					byte[] bbuf = new byte[size];
					target.read(bbuf);
					buffer.write(bbuf);
					flgRead = true;
				}
			}
			catch (IOException ex) {
				AppLogger.debug(ex);
				ex = null;
				flgRead = false;
			}
			return flgRead;
		}
	}
}
