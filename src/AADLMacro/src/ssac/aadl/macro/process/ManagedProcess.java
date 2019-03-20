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
 * @(#)ManagedProcess.java	2.0.0	2014/03/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.process;

import java.io.IOException;

import ssac.aadl.macro.util.io.ReportPrinter;
import ssac.aadl.macro.util.io.ReportStream;

/**
 * プロセス管理クラス。
 * このクラスでは、{@link java.lang.Process} クラスをラップし、
 * 標準ストリーム処理などを行う。
 * 
 * @version 2.0.0	2014/03/18
 * @since 2.0.0
 */
public class ManagedProcess
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** プロセス制御ステータス : プロセス実行中 **/
	static public final int PROCESS_RUNNING		= 0;
	/** プロセス制御ステータス : プロセス終了 **/
	static public final int	PROCESS_FINISHED	= 1;
	/** プロセス制御ステータス : プロセス中断処理中 **/
	static public final int	PROCESS_TERMINATING	= 2;
	/** プロセス制御ステータス : プロセスは中断された **/
	static public final int	PROCESS_INTERRUPTED	= 3;
	/** プロセス制御ステータス : プロセスは強制停止された **/
	static public final int	PROCESS_KILLED		= 4;
	/** プロセス制御ステータス : プロセスは強制終了された **/
	static public final int	PROCESS_DESTROYED	= 5;

	/** プロセス中断処理の内部ステータス : 中断は行われていない **/
	static protected final int	TERMSTAT_NONE			= 0;
	/** プロセス中断処理の内部ステータス : プロセス中断処理実行中 **/
	static protected final int	TERMSTAT_INTERRUPING	= 1;
	/** プロセス中断処理の内部ステータス : プロセス強制停止(Exit)実行中 **/
	static protected final int	TERMSTAT_KILLING		= 2;
	/** プロセス中断処理の内部ステータス : プロセス強制終了(Destroy)実行中 **/
	static protected final int	TERMSTAT_DESTROYING		= 3;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 監視対象プロセスの識別コード(プロセスIDとは限らない) */
	protected final long					_procHandle;
	/** 監視対象のプロセス */
	protected final Process					_process;
	/** プロセス標準出力の内容を処理するスレッド */
	protected final PipedStreamThread		_outStreamThread;
	/** プロセス標準エラー出力の内容を処理するスレッド */
	protected final PipedStreamThread		_errStreamThread;

	/** プロセス中断処理の内部ステータス **/
	protected volatile int	_terminateStatus	= TERMSTAT_NONE;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたプロセスを管理する新しいオブジェクトを生成する。
	 * 指定されたプロセスの標準出力、標準エラー出力は、指定されたストリームに出力される。
	 * <em>outReceiver</em> に <tt>null</tt> を指定した場合、プロセスの標準出力の内容は破棄される。
	 * <em>errReceiver</em> に <tt>null</tt> を指定した場合、プロセスの標準エラー出力の内容は破棄される。
	 * @param targetProcess			対象のプロセス
	 * @param outReceiver			プロセス標準出力の内容を受け取る <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @param autoCloseOutReceiver	<em>outReceiver</em> をプロセス終了時に自動的に閉じる場合は <tt>true</tt>、閉じない場合は <tt>false</tt> を指定する
	 * @param errReceiver			プロセス標準エラー出力の内容を受け取る <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @param autoCloseErrReceiver	<em>errReceiver</em> をプロセス終了時に自動的に閉じる場合は <tt>true</tt>、閉じない場合は <tt>false</tt> を指定する
	 * @throws NullPointerException	<em>targetProcess</em> が <tt>null</tt> の場合
	 */
	public ManagedProcess(Process targetProcess,
						ReportPrinter outReceiver, boolean autoCloseOutReceiver,
						ReportPrinter errReceiver, boolean autoCloseErrReceiver)
	{
		_process = targetProcess;
		_procHandle = ProcessUtil.getProcessHandle(targetProcess);
		// プロセスの標準出力と標準エラー出力のハンドルは自動クローズ
		_outStreamThread = new PipedStreamThread(targetProcess.getInputStream(), outReceiver, true, autoCloseOutReceiver);
		_errStreamThread = new PipedStreamThread(targetProcess.getErrorStream(), errReceiver, true, autoCloseErrReceiver);
		_outStreamThread.start();
		_errStreamThread.start();
	}

	/**
	 * 指定されたプロセスを管理する新しいオブジェクトを生成する。
	 * 指定されたプロセスの標準出力、標準エラー出力は、指定されたストリームに出力される。
	 * <em>outReceiver</em> に <tt>null</tt> を指定した場合、プロセスの標準出力の内容は破棄される。
	 * <em>errReceiver</em> に <tt>null</tt> を指定した場合、プロセスの標準エラー出力の内容は破棄される。
	 * なお、<em>outReceiver</em>、<em>errReceiver</em> に指定されたストリームの自動クローズは行わない。
	 * @param targetProcess			対象のプロセス
	 * @param outReceiver			プロセス標準出力の内容を受け取る <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @param errReceiver			プロセス標準エラー出力の内容を受け取る <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @throws NullPointerException	<em>targetProcess</em> が <tt>null</tt> の場合
	 */
	public ManagedProcess(Process targetProcess, ReportPrinter outReceiver, ReportPrinter errReceiver) {
		this(targetProcess, outReceiver, false, errReceiver, false);
	}

	/**
	 * 指定されたプロセスを管理する新しいオブジェクトを生成する。
	 * プロセスの標準出力、標準エラー出力は、システムの標準出力、標準エラー出力へリダイレクトされる。
	 * @param targetProcess			対象のプロセス
	 * @throws NullPointerException	<em>targetProcess</em> が <tt>null</tt> の場合
	 */
	public ManagedProcess(Process targetProcess) {
		this(targetProcess, new ReportStream(System.out), false, new ReportStream(System.err), false);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 管理対象のプロセスを返す。
	 * @return	プロセスオブジェクト
	 */
	public Process getProcess() {
		return _process;
	}

	/**
	 * プロセスの制御状態を取得する。
	 * @return	次のプロセス制御状態を返す。
	 * <dl>
	 * <dt>{@link #PROCESS_RUNNING}</dt><dd>プロセスが実行中であることを示す</dd>
	 * <dt>{@link #PROCESS_FINISHED}</dt><dd>中断されずにプロセスが終了したことを示す</dd>
	 * <dt>{@link #PROCESS_TERMINATING}</dt><dd>プロセス中断処理中、もしくはプロセス強制終了中であることを示す</dd>
	 * <dt>{@link #PROCESS_INTERRUPTED}</dt><dd>プロセスが中断されたことを示す</dd>
	 * <dt>{@link #PROCESS_KILLED}</dt><dd>プロセスが強制的に停止されたことを示す</dd>
	 * <dt>{@link #PROCESS_DESTROYED}</dt><dd>プロセスが外部から強制終了されたことを示す</dd>
	 * </dl>
	 */
	public int status() {
		boolean alive = isAlive();
		int term = _terminateStatus;
		if (term == TERMSTAT_INTERRUPING) {
			// プロセス中断処理中
			return (alive ? PROCESS_TERMINATING : PROCESS_INTERRUPTED);
		}
		else if (term == TERMSTAT_KILLING) {
			// プロセス強制停止処理中
			return (alive ? PROCESS_TERMINATING : PROCESS_KILLED);
		}
		else if (term == TERMSTAT_DESTROYING) {
			// プロセス強制終了処理中
			return (alive ? PROCESS_TERMINATING : PROCESS_DESTROYED);
		}
		else {
			// 中断リクエストなし
			return (alive ? PROCESS_RUNNING : PROCESS_FINISHED);
		}
	}
	
	/**
	 * 管理対象プロセスが実行中かどうかを判定する。
	 * 実行中の判定は、{@link java.lang.Process#exitValue()} が例外をスローした場合を実行中とする。
	 * @param process	対象プロセス
	 * @return	実行中なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isAlive() {
		try {
			_process.exitValue();
			return false;
		}
		catch (IllegalThreadStateException ex) {
			return true;
		}
	}

	/**
	 * 管理対象プロセスの終了コードを返す。
	 * @return	プロセスの終了コード
	 * @throws IllegalThreadStateException	監視対象プロセスがまだ終了していない場合
	 */
	public int exitValue() {
		return _process.exitValue();
	}

	/**
	 * 管理対象プロセスが実行中であれば、その実行を中断する。
	 * このメソッドでは {@link #destroy()} を呼び出すのみで、後処理は行わない。
	 * @see #destroy()
	 */
	public void terminate() {
		destroy();
	}

	/**
	 * 管理対象プロセスが実行中であれば、その実行を強制的に停止する。
	 * このメソッドでは {@link #destroy()} を呼び出すのみで、後処理は行わない。
	 * @see #destroy()
	 */
	public void kill() {
		destroy();
	}

	/**
	 * 管理対象プロセスが実行中であれば、そのプロセスを強制的に終了させる。
	 * このメソッドでは {@link java.lang.Process#destroy()} を呼び出すのみで、後処理は行わない。
	 */
	public void destroy()
	{
		if (isAlive()) {
			VerboseStream.categoryTrace("ManagedProcess#destroy", "$This process destroying.");
			_terminateStatus = TERMSTAT_DESTROYING;
			_process.destroy();
		}
	}

	/**
	 * 管理対象プロセスが終了している場合のみ、クリーンアップ処理を実行する。
	 * クリーンアップ処理は、プロセスの標準ストリームの動作が終了するまで待機し、プロセス標準ストリームをクローズする。
	 * <p>プロセスが実行中の場合、このメソッドは例外をスローする。
	 * @return	プロセスの終了コード
	 * @throws IllegalThreadStateException	プロセスが実行中の場合
	 * @throws InterruptedException	標準出力、標準エラー出力の完了待機中に割り込みが発生した場合
	 */
	public int cleanup() throws InterruptedException
	{
		int exitCode = _process.exitValue();
		
		try {
			// 標準出力、標準エラー出力の内容がすべて出力されるのを待つ
			_outStreamThread.join();
			VerboseStream.categoryTrace("ManagedProcess#cleanup", "$Output stream thread joined!");
			_errStreamThread.join();
			VerboseStream.categoryTrace("ManagedProcess#cleanup", "$Error stream thread joined!");
		}
		finally {
			// プロセスのクリーンアップ
			_process.destroy();	// 念のため：不要？
			try {
				_process.getOutputStream().close();
			} catch (IOException ex) {}
			try {
				_process.getInputStream().close();
			} catch (IOException ex) {}
			try {
				_process.getErrorStream().close();
			} catch (IOException ex) {}
		}
		
		return exitCode;
	}

	/**
	 * 管理対象プロセスが終了するまで、現在のスレッドを待機させる。
	 * 管理対象プロセスがすでに終了している場合、このメソッドはただちに復帰する。
	 * 管理対象プロセスが終了していない場合、呼び出し側スレッドはプロセスが終了するまでブロックされる。
	 * @return プロセスの終了コード。通常、0 は正常終了を示す。
	 * @throws InterruptedException	待機中に割り込みが発生した場合
	 */
	public int waitFor() throws InterruptedException
	{
		return _process.waitFor();
	}

//	/**
//	 * 管理対象プロセスが終了するまで、もしくは指定された待機時間が経過するまで、現在のスレッドを待機させる。
//	 * プロセスがすでに終了している場合、このメソッドはただちに復帰する。
//	 * @param timeout	ミリ秒単位の待機時間を指定する。
//	 * 					負の値の場合は待機時間に関係なくプロセスが終了するまで待機する。
//	 * 					0 を指定した場合、このメソッドはプロセスの状態を取得し、ただちに復帰する。
//	 * @return	プロセスが終了している場合は <tt>true</tt>、プロセスは終了せず待機時間を経過した場合は <tt>false</tt>
//	 * @throws InterruptedException	待機中に割り込みが発生した場合
//	 */
//	public boolean waitFor(long timeout) throws InterruptedException
//	{
//		// プロセスが終了していれば、即答
//		if (!isAlive()) {
//			return true;
//		}
//		else if (timeout == 0L) {
//			// 待機時間が 0 の場合も即答
//			return false;
//		}
//		
//		// 待機
//		if (timeout < 0L) {
//			// wait until the process to terminate.
//			VerboseStream.categoryTrace("ManagedProcess#waitFor", "$-> _process.waitFor()");
//			_process.waitFor();
//			VerboseStream.categoryTrace("ManagedProcess#waitFor", "$<- _process.waitFor()");
//			return true;
//		}
//		else {
//			// wait until the process to terminate or passed timeout
//			final CountDownLatch cdl = new CountDownLatch(1);
//			Thread waitThread = new Thread(new Runnable() {
//				public void run() {
//					VerboseStream.categoryTrace("ManagedProcess#waitFor.Thread#run", "$-> start");
//					try {
//						_process.waitFor();
//						cdl.countDown();
//					} catch (InterruptedException ignoreEx) {
//						VerboseStream.categoryTrace("ManagedProcess#waitFor.Thread#run", "$Wait interrupted");
//					}
//					VerboseStream.categoryTrace("ManagedProcess#waitFor.Thread#run", "$-> finished");
//				}
//			}, THREADNAME_PROCESSWATCHER);
//			try {
//				waitThread.start();
//				return cdl.await(timeout, TimeUnit.MILLISECONDS);
//			}
//			finally {
//				if (waitThread.isAlive()) {
//					VerboseStream.categoryTrace("ManagedProcess#waitFor", "$(finally) Do interrupt to waitThread");
//					waitThread.interrupt();
//				}
//			}
//		}
//	}
	
	/**
	 * 管理対象プロセスが終了するまで待機し、クリーンアップ処理を実行する。
	 * このメソッドは、クリーンアップ処理が終了するまで、現在のスレッドを待機させる。
	 * 管理対象プロセスもしくはクリーンアップ処理が終了していない場合、
	 * 呼び出し側スレッドはプロセスおよびクリーンアップ処理が終了するまでブロックされる。
	 * @return プロセスの終了コード。通常、0 は正常終了を示す。
	 * @throws InterruptedException	待機中に割り込みが発生した場合
	 */
	public int waitAndCleanup() throws InterruptedException
	{
		_process.waitFor();
		return cleanup();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * プロセスの標準出力、標準エラー出力の内容をリダイレクトするスレッドを停止する。
	 * このメソッドでは、プロセスの標準出力ストリーム、標準エラー出力ストリームもクローズする。
	 * ただし、プロセスの実行は停止しない。
	 */
	protected void stopPipedStreamThread() {
		// ストリームをクローズすることで、スレッド停止
		try {
			_process.getInputStream().close();
		} catch (IOException ignoreEx) {}
		try {
			_process.getErrorStream().close();
		} catch (IOException ignoreEx) {}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
