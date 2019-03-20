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
 * @(#)InterruptibleJavaMainProcessImpl.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)InterruptibleJavaMainProcessImpl.java	2.0.0	2014/03/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.process;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Java プロセス専用の、中断可能プロセスの実装。
 * このクラスは、実行対象 Java プロセスのラッパー実装となる。
 * 
 * @version 2.1.0	2014/05/29
 * @since 2.0.0
 */
public class InterruptibleJavaMainProcessImpl
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 中断処理監視インターバル(ミリ秒) */
	static private final long	INTERVAL_WATCH	= 200L;
	/** 中断処理フェーズの最大待機時間(ミリ秒) **/
	static private final long	WAITTIME_TERMINATION_PHASE = 10L * 1000L;	// max:10秒

	static private final String NAME_FILEWATCHER	= "ProcessAliveFileWatcher";
	static private final String NAME_SHUTDOWNHOOK	= "TerminateShutdown";

	/** JAVAプロセスに中断要求(メインスレッドへの割り込み)が発生したことを示すプロパティ名、中断要求があった場合は <tt>true</tt> がセットされる */
	static public final String	PROPKEY_AADL_INTERRUPTED	= "aadl.interrupted";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このクラスの唯一のインスタンス **/
	static private InterruptibleJavaMainProcessImpl	_instance;

	/** このクラスの管理対象プロセスのメインスレッド **/
	private final	Thread			_thMain;
	/** このクラスの管理対象プロセスの生存制御ファイル **/
	private final File			_fAlive;
	/** このクラスの管理対象プロセスの停止制御ファイル **/
	private final File			_fKill;
	/** このプロセス専用の監視スレッド **/
	private TerminateWatcherThread	_thWatcher;

	/** 管理対象プロセスに対して中断要求が発生したことを示すフラグ **/
	private volatile boolean		_flgTerminateRequested = false;
	/** 管理対象プロセスの中断要求に対し応答中であることを示すフラグ **/
	private volatile boolean		_flgAcceptTermination = false;
	/** 管理対象プロセス監視がアクティブ(有効)であることを示すフラグ **/
	private volatile boolean		_flgWatcherActive = false;
	/** 中断処理開始時刻(ミリ秒) **/
	private volatile long		_ltStartTermination;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * このクラスの {@link #main(String[])} で最初に呼び出されるコンストラクタ。
	 * @param aliveFile	プロセス生存制御ファイル
	 * @param killFile		プロセス停止制御ファイル
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	private InterruptibleJavaMainProcessImpl(File aliveFile, File killFile) {
		_thMain = Thread.currentThread();
		_fAlive = aliveFile;
		_fKill  = killFile;
	}

	//------------------------------------------------------------
	// Public helper methods
	//------------------------------------------------------------

	/**
	 * このプロセスに中断要求が発生している場合、その中断要求に対し応答を開始したことを示す内部ステータスをセットする。
	 * 中断要求が発生していない場合、このメソッドは何も行わずに <tt>false</tt> を返す。
	 * <p>このメソッドが <tt>true</tt> を返す場合、このメソッドを呼び出したスレッドの割り込みステータスもクリアされる。
	 * なお、このメソッドが <tt>true</tt> を返す間は、このメソッド呼び出しの度に、このメソッドを呼び出したスレッドの
	 * 割り込みステータスがクリアされる。
	 * @return	中断要求があった場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static public boolean acceptTerminateRequest() {
		if (_instance != null)
			return _instance._acceptTerminateRequest();
		else
			return false;
	}

	/**
	 * このプロセスに中断要求があったかどうかを判定する。
	 * このメソッドは、中断要求があったかどうかを判定するのみであり、中断要求に対する応答は行わない。<br>
	 * このメソッドが <tt>true</tt> を返した場合、{@link #acceptTerminateReqest()} を呼び出して
	 * 中断要求への応答を開始したことを示す必要がある。
	 * 中断要求発生からおよそ {@link #WAITTIME_TERMINATION_PHASE} ミリ秒以内に応答を行わなかった場合、このプロセスは強制的に停止される。
	 * <p>このメソッド呼び出しでは、このメソッドを呼び出したスレッドの割り込みステータスはクリアされない。
	 * @return	中断要求があった場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static public boolean isTerminateRequested() {
		if (_instance != null)
			return _instance._isTerminateRequested();
		else
			return false;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 対象 Java プロセスを中断可能とするための、エントリポイント。
	 * このメソッドの引数では、<code>args[0]</code> がプロセス生存制御ファイルの絶対パス、
	 * <code>args[1]</code> がプロセス停止制御ファイルの絶対パス、
	 * <code>args[2]</code> が実行対象 Java プロセスのメインクラス名、
	 * <code>args[3]</code> 以降が実行対象 Java プロセスへの引数となっている。
	 * @param args	中断可能 Java プロセスの引数配列。
	 */
	public static void main(String[] args) throws Exception
	{
		VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl#main", "@Start");
		if (VerboseStream.isTrace()) {
			if (args.length > 0) {
				for (int i = 0; i < args.length; ++i) {
					VerboseStream.formatCategoryTrace("InterruptibleJavaMainProcessImpl#main", "args[%d]=%s", i, (args[i]==null ? "" : args[i]));
				}
			} else {
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl#main", "No arguments.");
			}
		}
		
		// args[0] : process alive file
		// args[1] : process kill file
		// args[2] : target main class
		// args[3]～ : arguments for target main
		if (args.length < 1) {
			System.err.println("@InterruptibleJavaMain : Process alive file is not supplied!");
			System.exit(90);
		}
		else if (args.length < 2) {
			System.err.println("@InterruptibleJavaMain : Process kill file is not supplied!");
			System.exit(90);
		}
		else if (args.length < 3) {
			System.err.println("@InterruptibleJavaMain : Target main class name is not supplied!");
			System.exit(90);
		}
		String strProcessAliveFile = args[0];
		String strProcessKillFile  = args[1];
		String strTargetMainClass  = args[2];
		String[] targetArgs = new String[args.length - 3];
		System.arraycopy(args, 3, targetArgs, 0, targetArgs.length);
		
		// check watch file
		File fileAlive = new File(strProcessAliveFile);
		if (!fileAlive.exists()) {
			System.err.println("@InterruptibleJavaMain : Process alive file not found : " + String.valueOf(strProcessAliveFile));
			System.exit(91);
		}
		else if (!fileAlive.isFile()) {
			System.err.println("@InterruptibleJavaMain : Process alive file is not a file : " + String.valueOf(strProcessAliveFile));
			System.exit(92);
		}
		File fileKill = new File(strProcessKillFile);
		if (!fileKill.exists()) {
			System.err.println("@InterruptibleJavaMain : Process kill file not found : " + String.valueOf(strProcessKillFile));
			System.exit(91);
		}
		else if (!fileKill.isFile()) {
			System.err.println("@InterruptibleJavaMain : Process kill file is not a file : " + String.valueOf(strProcessKillFile));
			System.exit(92);
		}
		
		// create instance
		_instance = new InterruptibleJavaMainProcessImpl(fileAlive, fileKill);

		// add shutdown hook
		Runtime.getRuntime().addShutdownHook(new TerminateShutdownThread(_instance));
		
		// run target main
		try {
			// start watcher thread
			try {
				_instance.startWatcherThread();
			} catch (Throwable ex) {
				System.err.println("@InterruptibleJavaMain : Failed to start Process terminate watcher thread : " + ex.toString());
				System.exit(93);
			}
			
			// start target main
			Class<?> targetClass = Class.forName(strTargetMainClass);
			Method mMain = targetClass.getMethod("main", String[].class);
			mMain.invoke(null, (Object)targetArgs);
			
			VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl#main", "@Finished");
		}
		finally {
			VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl#main (finally)", "@Call finishedNormally()");
			_instance.finishNormally();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このプロセスに中断要求が発生している場合、その中断要求に対し応答を開始したことを示す内部ステータスをセットする。
	 * 中断要求が発生していない場合、このメソッドは何も行わずに <tt>false</tt> を返す。
	 * <p>このメソッドが <tt>true</tt> を返す場合、このメソッドを呼び出したスレッドの割り込みステータスもクリアされる。
	 * なお、このメソッドが <tt>true</tt> を返す間は、このメソッド呼び出しの度に、このメソッドを呼び出したスレッドの
	 * 割り込みステータスがクリアされる。
	 * @return	中断要求があった場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean _acceptTerminateRequest() {
		synchronized (this) {
			if (_flgTerminateRequested) {
				Thread.interrupted();	// 割り込みステータスのクリア
				if (!_flgAcceptTermination) {
					_flgAcceptTermination = true;
				}
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * このプロセスに中断要求があったかどうかを判定する。
	 * このメソッドは、中断要求があったかどうかを判定するのみであり、中断要求に対する応答は行わない。<br>
	 * このメソッドが <tt>true</tt> を返した場合、{@link #acceptTerminateReqest()} を呼び出して
	 * 中断要求への応答を開始したことを示す必要がある。
	 * 中断要求発生からおよそ {@link #WAITTIME_TERMINATION_PHASE} ミリ秒以内に応答を行わなかった場合、このプロセスは強制的に停止される。
	 * <p>このメソッド呼び出しでは、このメソッドを呼び出したスレッドの割り込みステータスはクリアされない。
	 * @return	中断要求があった場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean _isTerminateRequested() {
		synchronized (this) {
			return _flgTerminateRequested;
		}
	}

	/**
	 * このプロセスが標準的に終了したことを通知する。
	 */
	protected void finishNormally() {
		synchronized (this) {
			_flgWatcherActive = false;
		}
	}

	/**
	 * このプロセスの中断監視スレッドを開始する。
	 */
	protected void startWatcherThread() {
		synchronized (this) {
			_flgWatcherActive = true;
			_thWatcher = new TerminateWatcherThread();
			_thWatcher.start();
		}
	}

	/**
	 * メインスレッドへの割り込みを発生させる。
	 * 現在は、メインスレッドに対してのみ {@link Thread#interrupt()} を呼び出す。
	 */
	protected void interruptMainThread() {
		_thMain.interrupt();
	}

	/**
	 * メインスレッドが実行中かどうかを判定する。
	 * @return	メインスレッド実行中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean isAliveMainThread() {
		return _thMain.isAlive();
	}

	/**
	 * プロセス中断要求監視を継続するかどうかを判定する。
	 * プロセス中断要求が発生した場合、もしくは管理終了フラグがセットされた場合、このメソッドは <tt>false</tt> を返す。
	 * 中断要求が発生した場合のみ、中断要求が発生したことを示す内部フラグをセットする。
	 * @return	中断要求監視を継続する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean continueWatchTermination() {
		synchronized (this) {
			//--- 監視終了チェック
			if (!_flgWatcherActive) {
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl#continueWatchTermination", "%** _flgWatcherActive = false");
				return false;	// 監視終了
			}
			//--- 強制終了要求チェック
			if (!_fKill.exists()) {
				return false;	// 強制終了要求発生
			}
			//--- 中断要求チェック
			if (!_fAlive.exists()) {
				// 中断要求発生
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl#continueWatchTermination", "%** _fAlive.exists() = false");
				_flgTerminateRequested = true;
				_ltStartTermination = System.currentTimeMillis();	// 中断処理待機開始
				interruptMainThread();
				return false;
			}
			//--- 中断監視継続
			return true;
		}
	}

	/**
	 * プロセス中断処理待機を継続するかどうかを判定する。
	 * このメソッドでは、次の場合に <tt>false</tt> を返す。
	 * <ul>
	 * <li>監視終了が要求された場合
	 * <li>強制終了が要求された場合
	 * <li>中断要求発生から応答中フラグがセットされないまま {@link #WAITTIME_TERMINATION_PHASE} ミリ秒経過した場合
	 * </ul>
	 * @return	中断処理待機を継続する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean continueWaitTermination() {
		synchronized (this) {
			//--- 監視終了チェック
			if (!_flgWatcherActive) {
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl#continueWaitTermination", "%** _flgWatcherActive = false");
				return false;	// 監視終了
			}
			//--- 強制終了要求チェック
			if (!_fKill.exists()) {
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl#continueWaitTermination", "%** _fKill.exists() = false");
				return false;	// 強制終了要求発生
			}
			//--- 待機時間タイムアウトチェック
			if (!_flgAcceptTermination && (System.currentTimeMillis() - _ltStartTermination) >= WAITTIME_TERMINATION_PHASE) {
				if (VerboseStream.isTrace()) {
					VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl#continueWaitTermination", "%** _flgAcceptTermination = false, Timeout(" + String.valueOf(System.currentTimeMillis()-_ltStartTermination) + "ms)");
				}
				return false;	// 待機タイムアウト
			}
			//--- 中断処理待機継続
			return true;
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * このプロセスのシャットダウンフック用スレッドオブジェクト。
	 * 
	 * @version 2.0.0	2014/03/18
	 * @since 2.0.0
	 */
	static protected class TerminateShutdownThread extends Thread
	{
		/** このプロセスの管理オブジェクト */
		private InterruptibleJavaMainProcessImpl	_manager;
		
		public TerminateShutdownThread(InterruptibleJavaMainProcessImpl impl) {
			super(NAME_SHUTDOWNHOOK);
			_manager = impl;
		}
		
		public void run() {
			// シャットダウンシーケンス開始
			VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateShutdownThread#run", "%-> Start");
			
			// 監視終了
			VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateShutdownThread#run", "%** Finish termination watch normally.");
			_manager.finishNormally();
			
			// 管理ファイルの削除
			VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateShutdownThread#run", "%** Remove watch files.");
			_manager._fKill.delete();
			_manager._fAlive.delete();
			
			// もしメインスレッドが稼働中なら、メインスレッドに割り込みをかける
			if (_manager.isAliveMainThread()) {
				// メインスレッドが生きていれば、割り込み
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateShutdownThread#run", "% Do interrupt to main thread");
				_manager.interruptMainThread();
				//--- メインスレッドの待機は行わない
//			} else {
//				// メインスレッドは終了している
//				System.out.println("%[Trace] ** TerminateShutdownThread#run(), Main thread already terminated.");
			}
			
			// シャットダウンシーケンス終了
			VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateShutdownThread#run", "%<- Finished");
			_manager = null;
		}
	}

	/**
	 * プロセス生存制御ファイルの監視スレッド。
	 * 
	 * @version 2.0.0	2014/03/18
	 * @since 2.0.0
	 */
	private class TerminateWatcherThread extends Thread
	{
		/**
		 * 監視スレッドの新しいインスタンスを生成する。
		 */
		public TerminateWatcherThread() {
			super(NAME_FILEWATCHER);
		}

		@Override
		public void run() {
			VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateWatcherThread#run", "#-> Start");
			
			// 中断要求の監視
			try {
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateWatcherThread#run - Check request termination", "#-> Start");
				for (; continueWatchTermination(); ) {
					// 中断処理監視継続なら、スリープを挟んで監視続行
					Thread.sleep(INTERVAL_WATCH);
				}
			}
			catch (InterruptedException ignoreEx) {
				// 監視中断の場合、プロセス生存制御ファイルが存在し、_flgWatcherActive フラグも true
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateWatcherThread#run - Check request termination", "#** Caught InterruptedException");
			}
			finally {
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateWatcherThread#run - Check request termination", "#<- Finished");
			}
			
			// 監視終了チェック
			if (!_flgWatcherActive) {
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateWatcherThread#run", "#<- Normaly finished, cause _flgWatcherActive=false");
				return;	// 監視終了
			}
			
			// 中断要求応答完了の監視
			try {
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateWatcherThread#run - Wait termination", "#-> Start");
				for (; continueWaitTermination(); ) {
					// 中断処理待機継続なら、スリープを挟んで監視続行
					Thread.sleep(INTERVAL_WATCH);
				}
			}
			catch (InterruptedException ignoreEx) {
				// 監視中断の場合、プロセス強制終了制御ファイルが存在し、_flgWatcherActive フラグも true
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateWatcherThread#run - Wait termination", "#** Caught InterruptedException");
			}
			finally {
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateWatcherThread#run - Wait termination", "#<- Finished");
			}
			
			// 監視終了チェック
			if (!_flgWatcherActive) {
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateWatcherThread#run", "#<- Normaly finished, cause _flgWatcherActive=false");
				return;	// 監視終了
			}
			
			// exit process
			if (isAliveMainThread()) {
				// 監視終了要求はなく、メインスレッドも実行中なら、プロセスを強制的に終了させる。
				VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateWatcherThread#run", "#Timed out termination phase, then call System.exit(99)");
				System.out.flush();
				System.err.flush();
				System.exit(99);
			}
			VerboseStream.categoryTrace("InterruptibleJavaMainProcessImpl.TerminateWatcherThread#run", "#<- Normaly finished");
		}
	}
}
