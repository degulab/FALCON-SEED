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
 * @(#)MacroNodeProcess.java	2.1.0	2014/05/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro;

import static ssac.aadl.macro.util.Validations.validArgument;
import static ssac.aadl.macro.util.Validations.validState;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ssac.aadl.macro.data.INodeLocation;
import ssac.aadl.macro.process.InterruptibleManagedProcess;
import ssac.aadl.macro.process.InterruptibleProcessBuilder;
import ssac.aadl.macro.process.ProcessUtil;

/**
 * AADLマクロノードのプロセスを保持するクラス。
 * 
 * @version 2.1.0	2014/05/29
 * @since 2.1.0
 */
public class MacroNodeProcess
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * AADLマクロエンジンのインスタンス
	 */
	private final AADLMacroEngine	_engine;
	/**
	 * マクロ定義位置情報
	 */
	private final INodeLocation	_macroLocation;

	/** 中断可能なJAVAプロセスを生成するプロセスビルダー **/
	private final InterruptibleProcessBuilder	_procBuilder;
	/** 中断機構を持つプロセスオブジェクト **/
	private InterruptibleManagedProcess			_cmdProc;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたコマンド文字列から、<code>MacroNodeProcess</code> の新しいインスタンスを生成する。
	 * 
	 * @param engine	AADLマクロエンジンのインスタンス
	 * @param workDir	作業ディレクトリの抽象パス
	 * @param location	マクロ要素の定義位置情報
	 * @param commands	コマンドライン引数(文字列)の配列
	 */
	public MacroNodeProcess(AADLMacroEngine engine, File workDir, INodeLocation location, String...commands) {
		validArgument(commands.length > 0);
		_engine = engine;
		_macroLocation = location;
		_procBuilder = new InterruptibleProcessBuilder(commands);
		setWorkDirectory(workDir);
		ProcessUtil.enquoteCommand(_procBuilder.command());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * マクロ要素の定義位置情報を取得する。
	 * @return 定義位置情報を返す。
	 */
	public INodeLocation getMacroLocation() {
		return _macroLocation;
	}

	/**
	 * プロセス起動の為のコマンドラインを取得する。
	 * @return	コマンドと引数のリスト
	 */
	public List<String> getCommandLine() {
		return _procBuilder.command();
	}

	/**
	 * マクロの作業ディレクトリを取得する。
	 * @return	作業ディレクトリ
	 */
	public File getWorkDirectory() {
		return _procBuilder.directory();
	}

	/**
	 * マクロの作業ディレクトリを設定する。
	 * @param dir	作業ディレクトリ
	 */
	public void setWorkDirectory(File dir) {
		_procBuilder.directory(dir);
	}

	/**
	 * 現在のコマンド設定で、プロセスを起動する。
	 */
	public void start() throws IOException {
		validState(_cmdProc == null);
		
		// プロセス起動
		_cmdProc = _procBuilder.start(_engine.getOutputPrinter(), _engine.getErrorPrinter());
	}

	/**
	 * プロセスが実行中の場合、実行を中断する。
	 */
	public void terminate() {
		_cmdProc.terminate();
	}

	/**
	 * プロセスが実行中の場合、強制終了する。
	 */
	public void kill() {
		_cmdProc.kill();
	}

	/**
	 * 実行が完了しているプロセスのクリーンアップを行う。
	 * プロセスが実行中の場合、このメソッドは例外をスローする。
	 * @return	プロセスの終了コード
	 * @throws IllegalThreadStateException	プロセスが実行中の場合
	 * @throws InterruptedException	標準出力、標準エラー出力の完了待機中に割り込みが発生した場合
	 */
	public int cleanup() {
		try {
			return _cmdProc.cleanup();
		}
		catch (InterruptedException ex) {
			throw new AssertionError("Interrupted exception when PipedStreamThread joining.");
		}
	}

	/**
	 * プロセス終了まで待機する。
	 * @return	終了コード
	 * @throws InterruptedException	待機中に割り込みが発生した場合
	 */
	public int waitFor() throws InterruptedException {
		return _cmdProc.waitFor();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
