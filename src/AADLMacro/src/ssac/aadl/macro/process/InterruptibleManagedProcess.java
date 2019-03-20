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
 * @(#)InterruptibleManagedProcess.java	2.0.0	2014/03/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.process;

import java.io.File;

import ssac.aadl.macro.util.io.ReportPrinter;
import ssac.aadl.macro.util.io.ReportStream;

/**
 * 生存制御ファイルによる中断可能なプロセスクラス。
 * <p>プロセス起動時に生存制御ファイルによる制御機構を組み込まれた JavaVM は、
 * 生存制御ファイルの消去によって内部メインスレッドに割り込みを発生させることで、JavaVM の
 * ShutdownHook を実行する。
 * <p>生存制御ファイルが指定されなかった場合、中断処理はプロセス強制終了({@link java.lang.Process#destroy()} の呼び出し)となる。
 * 
 * @version 2.0.0	2014/03/18
 * @since 2.0.0
 */
public class InterruptibleManagedProcess extends ManagedProcess
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** プロセス生存を制御するファイル */
	private final File		_fileAliveProcess;
	/** プロセス停止を制御するファイル **/
	private final File		_fileKillProcess;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたプロセスを管理する新しいオブジェクトを生成する。
	 * 指定されたプロセスの標準出力、標準エラー出力は、指定されたストリームに出力される。
	 * <em>outReceiver</em> に <tt>null</tt> を指定した場合、プロセスの標準出力の内容は破棄される。
	 * <em>errReceiver</em> に <tt>null</tt> を指定した場合、プロセスの標準エラー出力の内容は破棄される。
	 * @param processAliveFile		プロセス生存制御用ファイルの抽象パス、<tt>null</tt> の場合は外部からのプロセス強制終了による停止となる
	 * @param processKillFile		プロセス停止制御用ファイルの中小パス、<tt>null</tt> の場合は外部からのプロセス強制終了による停止となる
	 * @param targetProcess			対象のプロセス
	 * @param outReceiver			プロセス標準出力の内容を受け取る <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @param autoCloseOutReceiver	<em>outReceiver</em> をプロセス終了時に自動的に閉じる場合は <tt>true</tt>、閉じない場合は <tt>false</tt> を指定する
	 * @param errReceiver			プロセス標準エラー出力の内容を受け取る <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @param autoCloseErrReceiver	<em>errReceiver</em> をプロセス終了時に自動的に閉じる場合は <tt>true</tt>、閉じない場合は <tt>false</tt> を指定する
	 * @throws NullPointerException	<em>targetProcess</em> が <tt>null</tt> の場合
	 */
	public InterruptibleManagedProcess(File processAliveFile, File processKillFile, Process targetProcess,
										ReportPrinter outReceiver, boolean autoCloseOutReceiver,
										ReportPrinter errReceiver, boolean autoCloseErrReceiver)
	{
		super(targetProcess, outReceiver, autoCloseOutReceiver, errReceiver, autoCloseErrReceiver);
		if (processAliveFile != null && processKillFile != null) {
			_fileAliveProcess = processAliveFile;
			_fileKillProcess  = processKillFile;
		} else {
			_fileAliveProcess = null;
			_fileKillProcess  = null;
		}
	}

	/**
	 * 指定されたプロセスを管理する新しいオブジェクトを生成する。
	 * 指定されたプロセスの標準出力、標準エラー出力は、指定されたストリームに出力される。
	 * <em>outReceiver</em> に <tt>null</tt> を指定した場合、プロセスの標準出力の内容は破棄される。
	 * <em>errReceiver</em> に <tt>null</tt> を指定した場合、プロセスの標準エラー出力の内容は破棄される。
	 * なお、<em>outReceiver</em>、<em>errReceiver</em> に指定されたストリームの自動クローズは行わない。
	 * @param processAliveFile		プロセス生存制御用ファイルの抽象パス、<tt>null</tt> の場合は外部からのプロセス強制終了による停止となる
	 * @param processKillFile		プロセス停止制御用ファイルの中小パス、<tt>null</tt> の場合は外部からのプロセス強制終了による停止となる
	 * @param targetProcess			対象のプロセス
	 * @param outReceiver			プロセス標準出力の内容を受け取る <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @param errReceiver			プロセス標準エラー出力の内容を受け取る <code>ReportPrinter</code> オブジェクト、または <tt>null</tt>
	 * @throws NullPointerException	<em>targetProcess</em> が <tt>null</tt> の場合
	 */
	public InterruptibleManagedProcess(File processAliveFile, File processKillFile, Process targetProcess, ReportPrinter outReceiver, ReportPrinter errReceiver) {
		this(processAliveFile, processKillFile, targetProcess, outReceiver, false, errReceiver, false);
	}

	/**
	 * 指定されたプロセスを管理する新しいオブジェクトを生成する。
	 * プロセスの標準出力、標準エラー出力は、システムの標準出力、標準エラー出力へリダイレクトされる。
	 * @param processAliveFile		プロセス生存制御用ファイルの抽象パス、<tt>null</tt> の場合は外部からのプロセス強制終了による停止となる
	 * @param processKillFile		プロセス停止制御用ファイルの中小パス、<tt>null</tt> の場合は外部からのプロセス強制終了による停止となる
	 * @param targetProcess			対象のプロセス
	 * @throws NullPointerException	<em>targetProcess</em> が <tt>null</tt> の場合
	 */
	public InterruptibleManagedProcess(File processAliveFile, File processKillFile, Process targetProcess) {
		this(processAliveFile, processKillFile, targetProcess, new ReportStream(System.out), false, new ReportStream(System.err), false);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * プロセス生存制御用ファイルを返す。
	 * @return	抽象パス、設定されていない場合は <tt>null</tt>
	 */
	public File getProcessAliveFile() {
		return _fileAliveProcess;
	}

	/**
	 * プロセス生存制御ファイルが設定されているかを判定する。
	 * @return	生存制御ファイルが設定されていれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean hasProcessAliveFile() {
		return (_fileAliveProcess != null);
	}

	/**
	 * プロセス生存制御ファイルが存在するかを判定する。
	 * @return	生存制御ファイルが存在していれば <tt>true</tt>、存在しないもしくは設定されていない場合は <tt>false</tt>
	 */
	public boolean existProcessAliveFile() {
		if (_fileAliveProcess != null) {
			return _fileAliveProcess.exists();
		} else {
			return false;
		}
	}

	/**
	 * プロセス停止制御用ファイルを返す。
	 * @return	抽象パス、設定されていな場合は <tt>null</tt>
	 */
	public File getProcessKillFile() {
		return _fileKillProcess;
	}

	/**
	 * プロセス停止制御ファイルが設定されているかを判定する。
	 * @return	停止制御ファイルが設定されていれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean hasProcessKillFile() {
		return (_fileKillProcess != null);
	}

	/**
	 * プロセス停止制御ファイルが存在するかを判定する。
	 * @return	停止制御ファイルが存在していれば <tt>true</tt>、存在しないもしくは設定されていない場合は <tt>false</tt>
	 */
	public boolean existProcessKillFile() {
		if (_fileKillProcess != null) {
			return _fileKillProcess.exists();
		} else {
			return false;
		}
	}

	/**
	 * 管理対象プロセスの実行を中断する。
	 * プロセス生存制御ファイルが設定されている場合、そのファイルを削除することでプロセス中断を実行する。
	 * プロセス生存制御ファイルが指定されていない場合、もしくはプロセス生存制御ファイルが削除できない場合、
	 * このメソッドは {@link java.lang.Process#destroy()} を呼び出し、プロセスを強制終了させる。
	 */
	@Override
	public void terminate() {
		if (_fileAliveProcess != null) {
			if (isAlive()) {
				if (_fileAliveProcess.delete()) {
					// プロセス生存制御ファイルの削除成功
					VerboseStream.categoryTrace("InterruptibleManagedProcess#terminate", "$The process alive file deleted.");
					_terminateStatus = TERMSTAT_INTERRUPING;
				} else {
					// 削除失敗
					VerboseStream.categoryDebug("InterruptibleManagedProcess#terminate", "$Failed to delete the process alive file, then destroy the process!");
					super.destroy();
				}
			}
		} else {
			super.destroy();
		}
	}

	/**
	 * 管理対象プロセスを強制的に停止する。
	 * プロセス停止制御ファイルが設定されている場合、そのファイルを削除することでプロセス内部から停止処理を実行する。
	 * プロセス停止制御ファイルが指定されていない場合、もしくはプロセス停止制御ファイルが削除できない場合、
	 * このメソッドでは {@link #destroy()} を呼び出すのみで、後処理は行わない。
	 * @see #destroy()
	 */
	@Override
	public void kill() {
		if (_fileKillProcess != null) {
			if (isAlive()) {
				if (_fileKillProcess.delete()) {
					// プロセス停止ファイルの削除成功
					VerboseStream.categoryTrace("InterruptibleManagedProcess#kill", "$The process kill file deleted.");
					_terminateStatus = TERMSTAT_KILLING;
				} else {
					// 削除失敗
					VerboseStream.categoryDebug("InterruptibleManagedProcess#kill", "$Failed to delete the process kill file, then destroy the process!");
					super.destroy();
				}
			}
		} else {
			super.destroy();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
