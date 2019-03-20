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
 * @(#)MacroSequencer.java	2.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroSequencer.java	2.0.0	2014/03/23 : modified and move 'ssac.util.*' to 'ssac.aadl.macro.util.*' (recursive) 
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroSequencer.java	1.10	2009/12/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroSequencer.java	1.00	2008/12/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ssac.aadl.macro.command.AbstractTokenizer;
import ssac.aadl.macro.command.CommandActionNode;
import ssac.aadl.macro.command.CommandConditionNode;
import ssac.aadl.macro.command.CommandNode;
import ssac.aadl.macro.command.CommandTermConditionNode;
import ssac.aadl.macro.command.MacroAction;
import ssac.aadl.macro.command.MacroModifier;
import ssac.aadl.macro.command.MacroStatus;
import ssac.aadl.macro.data.MacroData;
import ssac.aadl.macro.data.MacroNode;
import ssac.aadl.macro.file.CsvFormatException;
import ssac.aadl.macro.file.CsvMacroFiles;
import ssac.aadl.macro.process.InterruptibleJavaMainProcessImpl;

/**
 * AADLマクロの実行制御。
 * 
 * @version 2.1.0	2014/05/29
 * @since 1.00
 */
public class MacroSequencer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * このシーケンサが動作するプラットフォームが Windows であることを示すフラグ
	 */
	static protected final boolean isWindows;

	/**
	 * 標準の終了条件。この終了条件は、終了コードが 0 以外の場合に
	 * 終了する条件となる。この条件は、Windows以外のプラットフォームに
	 * おいて、プロセス終了コードが 0～255(Unsigned byte)の範囲でしか
	 * 返されない問題に対処するため。
	 */
	static private final CommandTermConditionNode DEFAULT_TERM_COND;
	
	static {
		//--- プラットフォームを示すフラグの設定
		String osname = System.getProperty("os.name");
		isWindows = (osname.indexOf("Windows") >= 0);
		
		//--- 標準終了条件の設定
		DEFAULT_TERM_COND = new CommandTermConditionNode(null) {
			public boolean isTermExitCode(int exitCode) {
				return (exitCode != 0);
			}
		};
	}

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * マクロ実行エンジンのインスタンス
	 */
	private final AADLMacroEngine engine;
	/**
	 * マクロ定義ファイル
	 */
	private final File	            macroFile;

	/**
	 * マクロ定義データ
	 */
	private MacroData macroData;
	/**
	 * 現在の終了条件
	 */
	private CommandTermConditionNode termCond = DEFAULT_TERM_COND;

	/**
	 * マクロノードと実行制御ノードとのマップ。
	 * @since 2.1.0
	 */
	private Map<MacroNode, MacroNodeExecutor>	_mapExecutor;
	/**
	 * 最初に実行するメインストリームの実行ノード
	 * @since 2.1.0
	 */
	private MacroNodeExecutor	_topExecutor;
	/**
	 * 実行中を示すフラグ
	 * @since 2.1.0
	 */
	private volatile boolean		_running = false;
	/**
	 * 実行終了処理中であることを示すフラグ
	 * @since 2.1.0
	 */
	private volatile boolean		_terminating = false;
	/**
	 * マクロ実行制御用スレッドプール
	 * @since 2.1.0
	 */
	private ExecutorService	_macroExecutor;
	/**
	 * 同期用ロックオブジェクト
	 * @since 2.1.0
	 */
	private final Object	_lock;
	/**
	 * 次に処理するエグゼキューターのブロッキングキュー。
	 * このキューには、プロセスが実行完了したエグゼキューターと次に実行すべきエグゼキューターが登録される。
	 * このオブジェクトは、ロックオブジェクトとしても利用される。
	 * @since 2.1.0
	 */
	private final LinkedBlockingQueue<MacroNodeExecutor>	_nextExecQueue;
	/**
	 * 現在実行中のノードが完了した後に実行予定のエグゼキューター。
	 * このリストには、複数のノードからリンクされているノードは、そのリンク数分登録されるので、
	 * 実行が開始されたノードは即座に取り除かれる。
	 * @since 2.1.0
	 */
	private final LinkedList<MacroNodeExecutor>			_willNextExecNodes;
	/**
	 * プロセス実行中のエグゼキューター
	 * @since 2.1.0
	 */
	private final LinkedList<MacroNodeExecutor>			_activeExecNodes;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * <code>MacroSequencer</code> の新しいインスタンスを生成する。
	 * @param engine	マクロ実行エンジンのインスタンス
	 * @param macroFile	マクロ定義ファイルの絶対パス
	 */
	public MacroSequencer(final AADLMacroEngine engine, File macroFile) {
		this.engine = engine;
		this.macroFile = macroFile;
		this._nextExecQueue = new LinkedBlockingQueue<MacroNodeExecutor>();	// 最大個数は Integer.MAX_VALUE
		this._lock = new Object();
		this._willNextExecNodes = new LinkedList<MacroNodeExecutor>();
		this._activeExecNodes   = new LinkedList<MacroNodeExecutor>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * マクロの実行を開始する。
	 * @return	正常に終了した場合は 0、それ以外の場合は 0以外
	 */
	public int play() {
		// check running
		synchronized (_lock) {
			if (_running) {
				// already running
				engine.printError("\"%s\" macro already running in current sequencer.", macroFile.getPath());
				return AADLMacroEngine.EXITCODE_FATAL_ERROR;
			}
			_running = true;
			_terminating = false;
		}

		// initialize
		final String macroFileName = macroFile.getPath();
		int result = AADLMacroEngine.EXITCODE_SUCCEEDED;
		_macroExecutor = null;
		_mapExecutor = null;
		_topExecutor = null;
		macroData = null;
		termCond  = DEFAULT_TERM_COND;
		
		engine.printMessage("start macro \"%s\"", macroFileName);
		
		// マクロファイル解析(サブルーチン化 : @since 2.1.0)
		result = loadMacroFromFile(macroFileName);
		if (result != AADLMacroEngine.EXITCODE_SUCCEEDED) {
			// error
			synchronized (_lock) {
				_running = false;
				return result;
			}
		}
		
		// コンパイルのみの場合は、ここで終了
		if (engine.getCommandLine().isCheckOnly()) {
			engine.printMessage("...Macro syntax OK!");
			synchronized (_lock) {
				_running = false;
				return AADLMacroEngine.EXITCODE_SUCCEEDED;
			}
		}
		
		// 実行時引数のマップを生成(サブルーチン化 : @since 2.1.0)
		result = setupRuntimeArguments(macroFileName);
		if (result != AADLMacroEngine.EXITCODE_SUCCEEDED) {
			// error
			synchronized (_lock) {
				_running = false;
				return result;
			}
		}
		
		// テンポラリファイルのマップを生成(サブルーチン化 : @since 2.1.0)
		result = setupTemporaryFiles(macroFileName);
		if (result != AADLMacroEngine.EXITCODE_SUCCEEDED) {
			// error
			synchronized (_lock) {
				_running = false;
				return result;
			}
		}
		
		// マクロ実行制御のためのエグゼキューター生成(added : @since 2.1.0)
		result = setupExecutionOrder(macroFileName);
		if (result != AADLMacroEngine.EXITCODE_SUCCEEDED) {
			// error
			synchronized (_lock) {
				_running = false;
				return result;
			}
		}
		
		// マクロ実行
		engine.printTrace("\"%s\" macro executing...", macroFileName);
		boolean hasError = false;
		int lastExitCode = AADLMacroEngine.EXITCODE_SUCCEEDED;
		if (_topExecutor != null) {
			// スレッドプールの生成
			_macroExecutor = Executors.newCachedThreadPool();

			// マクロノードの実行
			_nextExecQueue.add(_topExecutor);	// 最初のエグゼキューターの登録
			_willNextExecNodes.add(_topExecutor);
			try {
				for (;;)
				{
					// 外部からのプロセス中断要求応答
					if (InterruptibleJavaMainProcessImpl.acceptTerminateRequest()) {
						// プロセス中断要求への応答
						_terminating = true;
						lastExitCode = AADLMacroEngine.EXITCODE_TERMINATE_EXEC;
						hasError = true;
						break;	// 処理終了
					}

					// 次にキューの先頭を取得
					MacroNodeExecutor curExecutor;
					synchronized (_lock) {
						if (_terminating) {
							// 終了処理中なら、以降の処理は行わない
							break;
						}
						else if (_activeExecNodes.isEmpty() && _nextExecQueue.isEmpty()) {
							// 実行中プロセスがなく、キューイングされたエグゼキューターも存在しない場合、以降の処理は行わない
							break;
						}
						// キューから取得(取得できるまで待機)
						//--- これはデッドロックする!
						//curExecutor = _nextExecQueue.take();
					}
					// キューから取得(取得できるまで待機)
					curExecutor = _nextExecQueue.take();
					MacroStatus curStatus = curExecutor.getStatus();
					
					// ステータスによる実行制御
					if (curStatus == MacroStatus.TERMINATED) {
						// コマンド実行中断通知：中断されたコマンドは結果不定のため、無視
						continue;
					}
					else if (curStatus == MacroStatus.SKIPPED) {
						// コマンド実行スキップ：スキップされたコマンドは無視
						continue;
					}
					else if (curStatus == MacroStatus.COMPLETED) {
						// 処理が完全に終了している：この状態は重複実行しない
						continue;
					}
					else if (curStatus == MacroStatus.PROC_FINISHED) {
						// コマンド実行終了通知：実行結果の判定を行う
						curExecutor.setStatus(MacroStatus.COMPLETED);
						//--- 終了コード判定
						Integer curExitCode = curExecutor.getExitCode();
						if (curExitCode != null) {
							//--- プロセス名が設定されていれば、終了コードを保存
							String procName = curExecutor.getTargetProcessName();
							if (procName != null && procName.length() > 0) {
								macroData.putProcessExitCode(procName, curExitCode);
							}
							//--- マクロ中断判定
							if (isTerminationCondition(curExitCode)) {
								// マクロ実行を中止
								_terminating = true;
								showTerminatedInfo(curExecutor.getTargetNode(), curExitCode);
								lastExitCode = curExitCode;
								hasError = true;
								break;	// 実行ループを終了
							}
						}
						//--- 処理継続なら、次に実行するエグゼキューターをキューイング
						//--- start 修飾子が付加されている場合は、開始と同時に実行されるエグゼキューターは含まない
						enqueuNextExecutor(curExecutor, curExecutor.getCommandModifier() != MacroModifier.START);
						continue;	// 処理継続
					}
					else if (curStatus != MacroStatus.UNEXECUTED) {
						// 想定外の通知：エラー
						_terminating = true;
						engine.printError("%s Unexecutable macro command, status=%s, exit code=%s.",
								macroData.getMacroNameWithLocation(curExecutor.getTargetNode().getLocation()),
								String.valueOf(curStatus), String.valueOf(curExecutor.getExitCode()));
						lastExitCode = AADLMacroEngine.EXITCODE_CANNOT_EXEC;
						hasError = true;
						break;
					}
					
					// 未実行エグゼキューターの実行を開始
					try {
						if (!curExecutor.execNode(lastExitCode)) {
							// exit コマンドによる終了
							break;
						}
					}
					catch (IOException ex) {
						// Failed to start process.
						_terminating = true;
						String exmsg = ex.getLocalizedMessage();
						if (exmsg != null && exmsg.length() > 0) {
							engine.printError("%s Failed to execute macro process : %s",
									macroData.getMacroNameWithLocation(curExecutor.getTargetNode().getLocation()), exmsg);
						} else {
							engine.printError("%s Failed to execute macro process.",
								macroData.getMacroNameWithLocation(curExecutor.getTargetNode().getLocation()));
						}
						engine.printStackTrace(ex);
						lastExitCode = AADLMacroEngine.EXITCODE_CANNOT_EXEC;
						hasError = true;
						break;
					}
					//--- 終了判定
					Integer curExitCode = curExecutor.getExitCode();
					if (curExitCode != null) {
						//--- 終了条件に一致したら break;
						lastExitCode = curExitCode;
						if (isTerminationCondition(lastExitCode)) {
							_terminating = true;
							showTerminatedInfo(curExecutor.getTargetNode(), lastExitCode);
							hasError = true;
							break;
						}
					}
				}
			}
			catch (InterruptedException ex) {
				// Illegal interruption.
				_terminating = true;
				boolean requestTermination = InterruptibleJavaMainProcessImpl.acceptTerminateRequest();
				String exmsg = ex.getLocalizedMessage();
				if (requestTermination) {
					// プロセス実行元からの終了要求に基づく
					if (exmsg != null && exmsg.length() > 0) {
						engine.printError("%s stopped by terminate request : %s",
								macroData.getMacroName(), exmsg);
					} else {
						engine.printError("%s stopped by terminate request.",
								macroData.getMacroName());
					}
					// プロセス中断要求への応答
					lastExitCode = AADLMacroEngine.EXITCODE_TERMINATE_EXEC;
					hasError = true;
				}
				else {
					// 予期せぬ中断
					if (exmsg != null && exmsg.length() > 0) {
						engine.printError("%s stopped by interruption unexpected : %s",
								macroData.getMacroName(), exmsg);
					} else {
						engine.printError("%s stopped by interruption unexpected.",
								macroData.getMacroName());
					}
					engine.printStackTrace(ex);
					// プロセス待機中の割り込み
					lastExitCode = AADLMacroEngine.EXITCODE_FATAL_ERROR;
					hasError = true;
				}
			}
			
			// 実行中のプロセスは待機中断により強制終了
			_terminating = true;
			Thread.interrupted();	// clear interrupt signal
			killAllActiveMacros(true, true, InterruptibleJavaMainProcessImpl.acceptTerminateRequest());
			
			// 起動したスレッドを終了
			shutdownExecutorService();
		}

		if (hasError) {
			engine.printError("\"%s\" terminated(%d)", macroFileName, lastExitCode);
		}
		else {
			engine.printMessage("\"%s\" finished(%d)", macroFileName, lastExitCode);
			//--- check executed nodes
			if (engine.isDebugMode()) {
				boolean existUnexecuted = false;
				for (MacroNode node : macroData.macroNodes()) {
					MacroNodeExecutor executor = _mapExecutor.get(node);
					if (executor == null) {
						if (!existUnexecuted) {
							existUnexecuted = true;
							engine.printDebug("@@@ Exist unexecuted nodes : \"%s\"...", macroData.getMacroName());
						}
						engine.printDebug("  No executor : %s", String.valueOf(node));
					}
					else {
						MacroStatus status = executor.getStatus();
						if (status == null || status == MacroStatus.UNEXECUTED) {
							if (!existUnexecuted) {
								existUnexecuted = true;
								engine.printDebug("@@@ Exist unexecuted nodes : \"%s\"...", macroData.getMacroName());
							}
							engine.printDebug("  Unexecuted : %s", String.valueOf(node));
						}
						else if (status == MacroStatus.RUNNING) {
							if (!existUnexecuted) {
								existUnexecuted = true;
								engine.printDebug("@@@ Exist unexecuted nodes : \"%s\"...", macroData.getMacroName());
							}
							engine.printDebug("  Running : %s", String.valueOf(node));
						}
					}
				}
				if (existUnexecuted) {
					engine.printDebug("@@@ .....end of exists.");
				} else {
					engine.printDebug("@@@ All macro nodes completed! : \"%s\"", macroData.getMacroName());
				}
			}
		}
		synchronized (_lock) {
			_running = false;
		}
		return lastExitCode;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたマクロをファイルを読み込み、実行するマクロデータを生成する。
	 * @param macroFileName	マクロファイルのパスを示す文字列
	 * @return	正常に読み込めた場合は 0、失敗した場合は 0 以外を返す。
	 * @since 2.1.0
	 */
	private int loadMacroFromFile(String macroFileName)
	{
		// マクロファイル解析
		engine.printTrace("\"%s\" macro file parsing...", macroFileName);
		String csvEncoding = engine.getAvailableMacroEncoding();
		try {
			macroData = CsvMacroFiles.fromFile(macroFile, csvEncoding);
		}
		catch (FileNotFoundException ex) {
			engine.printError("\"%s\" file not found.", macroFile.getPath());
			engine.printStackTrace(ex);
			return AADLMacroEngine.EXITCODE_FATAL_ERROR;
		}
		catch (UnsupportedEncodingException ex) {
			engine.printError("Unsupported '%s' encoding.", String.valueOf(csvEncoding));
			engine.printStackTrace(ex);
			return AADLMacroEngine.EXITCODE_FATAL_ERROR;
		}
		catch (CsvFormatException ex) {
			engine.printError("\"%s\" %s : %s", macroFile.getName(), ex.getPositionString(), ex.getLocalizedMessage());
			engine.printStackTrace(ex);
			return AADLMacroEngine.EXITCODE_FATAL_ERROR;
		}
		catch (IOException ex) {
			String msg = ex.getLocalizedMessage();
			if (msg != null && msg.length() > 0)
				engine.printError("Failed to read \"%s\" file : %s", macroFile.getName(), msg);
			else
				engine.printError("Failed to read \"%s\" file.", macroFile.getName());
			return AADLMacroEngine.EXITCODE_FATAL_ERROR;
		}
		
		return AADLMacroEngine.EXITCODE_SUCCEEDED;
	}

	/**
	 * 現在のマクロデータに対し、実行時引数を割り当てる。
	 * @param macroFileName	マクロファイルのパスを示す文字列
	 * @return	正常に読み込めた場合は 0、失敗した場合は 0 以外を返す。
	 * @since 2.1.0
	 */
	private int setupRuntimeArguments(String macroFileName)
	{
		// 実行時引数のマップを生成
		Map<String, String> mapArgs = macroData.macroArgs();
		if (!mapArgs.isEmpty()) {
			boolean notExistArg = false;
			int maxArgID = 0;
			for (String argID : mapArgs.keySet()) {
				int id;
				try {
					id = Integer.parseInt(argID.substring(2, argID.length()-1));
				} catch (Throwable ex) {
					engine.printError(CsvMacroFiles.errorIllegalMacroArgumentID(argID));
					engine.printStackTrace(ex);
					return AADLMacroEngine.EXITCODE_FATAL_ERROR;
				}
				if (id < AbstractTokenizer.MIN_MACROARG_ID || id > AbstractTokenizer.MAX_MACROARG_ID) {
					engine.printError(CsvMacroFiles.errorIllegalMacroArgumentID(argID));
					return AADLMacroEngine.EXITCODE_FATAL_ERROR;
				}
				if (maxArgID < id) {
					maxArgID = id;
				}
				String strArg = engine.getMacroArgument(id);
				if (strArg == null) {
					notExistArg = true;
				} else {
					mapArgs.put(argID, strArg);
				}
			}
			if (notExistArg) {
				String strerr = String.format("\"%s\" macro argument is insufficient.\nNeed %d arguments.",
												macroFileName, maxArgID);
				engine.printError(strerr);
				return AADLMacroEngine.EXITCODE_FATAL_ERROR;
			}
		}
		
		// 成功
		return AADLMacroEngine.EXITCODE_SUCCEEDED;
	}

	/**
	 * 実行時のテンポラリファイルを割り当てる。
	 * @param macroFileName	マクロファイルのパスを示す文字列
	 * @return	正常に読み込めた場合は 0、失敗した場合は 0 以外を返す。
	 * @since 2.1.0
	 */
	private int setupTemporaryFiles(String macroFileName) {
		// テンポラリファイルのマップを生成
		Map<String,File> mapTempFiles = macroData.tempFiles();
		if (!mapTempFiles.isEmpty()) {
			//--- ファイルの生成
			String tmpPrefix = "AMF" + System.nanoTime() + "_";
			for (String tempID : mapTempFiles.keySet()) {
				try {
					File f = File.createTempFile(tmpPrefix, null);
					f.deleteOnExit();
					f = f.getAbsoluteFile();
					mapTempFiles.put(tempID, f);
					engine.printDebug("setup temporary file : [%s]=%s", tempID, f.getPath());
				}
				catch (Throwable ex) {
					String strerr = String.format("\"%s\" macro cannot create temporary file by '%s'",
												macroFileName, tempID);
					engine.printError(strerr);
					engine.printStackTrace(ex);
					return AADLMacroEngine.EXITCODE_FATAL_ERROR;
				}
			}
			/////////// ファイル名が重複して使用されないように、破棄しない
		}
		
		// 成功
		return AADLMacroEngine.EXITCODE_SUCCEEDED;
	}

	/**
	 * 指定されたノードに対応するエグゼキューターを生成し、管理マップに登録する。
	 * すでに管理マップに登録されているノードであれば、そのエグゼキューターを返す。
	 * @param node	対象マクロノード
	 * @return	生成もしくは登録済みエグゼキューター
	 * @since 2.1.0
	 */
	private MacroNodeExecutor getOrCreateExecutor(MacroNode node) {
		// 既存のエグゼキューターを取得
		MacroNodeExecutor executor = _mapExecutor.get(node);
		if (executor != null) {
			return executor;	// すでに生成済み
		}
		
		// コマンド判別
		if (node.getCommandAction() == MacroAction.GROUP) {
			// グループノード
			executor = new BasicMacroNodeExecutor(node);
		}
		else if (node.getCommandAction() == MacroAction.WAIT) {
			// wait ノード
			executor = new MacroWaitNodeExecutor(node);
		}
		else {
			// その他のコマンド
			executor = new BasicMacroNodeExecutor(node);
		}
		_mapExecutor.put(node, executor);
		return executor;
	}

	/**
	 * 現在のマクロデータから、実行制御のためのノードマップを生成し、実行順序を構成する。
	 * @param macroFileName	マクロファイルのパスを示す文字列
	 * @return	正常に読み込めた場合は 0、失敗した場合は 0 以外を返す。
	 * @since 2.1.0
	 */
	private int setupExecutionOrder(String macroFileName) {
		_mapExecutor = new HashMap<MacroNode, MacroNodeExecutor>();
		_topExecutor = null;
		MacroGroupNodeWaiter grpWaiter = null;	// グループ実行ノードを待機する特殊なエグゼキューター
		if (!macroData.isEmptyMacroNodes()) {
			MacroNodeExecutor lastMainStreamNode = null;
			for (MacroNode node : macroData.macroNodes()) {
				// ノードに対応するエグゼキューターを生成
				MacroNodeExecutor executor = getOrCreateExecutor(node);
				
				// グループノードの判別
				//--- グループノードが存在した場合は、そのブロック終端に待機用エグゼキューターを追加
				if (node.getCommandAction() == MacroAction.GROUP) {
					//--- グループノードなら、待機するエグゼキューターに登録
					if (grpWaiter == null) {
						grpWaiter = new MacroGroupNodeWaiter();
					}
					grpWaiter.add(executor);
					executor.addToNext(grpWaiter);	// グループノードの実行完了後に実行するのはグループ待機
				}
				else if (grpWaiter != null) {
					//--- グループノード待機のエグゼキューターがあれば、メインストリームに挿入
					lastMainStreamNode = grpWaiter;
					grpWaiter = null;
				}
				
				// コマンド種別判定
				MacroModifier modifier = node.getCommandModifier();
				if (modifier != null && modifier == MacroModifier.AFTER) {
					//--- 非同期実行：待機(メインストリーム外)
					// この場合は、バックグラウンドで実行開始するため、待機関係を追加
					for (String waitProcName : node.getCommandProcessNameListAsModifier()) {
						//--- プロセス名に対応するノードを取得
						MacroNode waitNode = macroData.getMacroNodeByProcessName(waitProcName);
						assert(waitNode != null);
						//--- ノードに対応するエグゼキューターを取得もしくは生成
						MacroNodeExecutor waitExecutor = getOrCreateExecutor(waitNode);
						//--- 待機関係を追加(このexecutorが、waitExecutorの次に実行される)
						waitExecutor.addToNext(executor);
					}
				}
				else {
					//--- 同期実行
					if (node.getCommandAction() == MacroAction.WAIT) {
						//--- wait コマンドの場合、待機関係を追加
						for (String waitProcName : node.getCommandProcessNameListAsAction()) {
							//--- プロセス名に対応するノードを取得
							MacroNode waitNode = macroData.getMacroNodeByProcessName(waitProcName);
							assert(waitNode != null);
							//--- ノードに対応するエグゼキューターを取得もしくは生成
							MacroNodeExecutor waitExecutor = getOrCreateExecutor(waitNode);
							//--- 待機関係を追加(このexecutorが、waitExecutorの次に実行される)
							waitExecutor.addToNext(executor);
						}
					}
					//--- after 修飾子以外のコマンドは、メインストリームでの実行(waitも含む)
					if (_topExecutor == null)
						_topExecutor = executor;
					if (lastMainStreamNode != null) {
						if (lastMainStreamNode.getCommandAction() == MacroAction.GROUP
							|| lastMainStreamNode.getCommandModifier() == MacroModifier.START)
						{
							// '&'コマンド、もしくは 'start'修飾子のあるコマンドなら、非同期で開始するエグゼキューターを順序付け
							lastMainStreamNode.addToStart(executor);
						}
						else {
							// 上記以外のコマンドなら、実行完了後に開始するエグゼキューターを順序付け
							lastMainStreamNode.addToNext(executor);
						}
					}
					lastMainStreamNode = executor;
				}
			}
			
			// チェック
			if (_topExecutor == null) {
				// error : 最初に実行するコマンドが存在しない
				String strerr = String.format("No command to be executed first in \"%s\" macro.", macroFileName);
				engine.printError(strerr);
				return AADLMacroEngine.EXITCODE_FATAL_ERROR;
			}
			
			if (engine.isDebugMode()) {
				// 実行リンクの表示
				StringBuilder sb = new StringBuilder();
				sb.append("Execution order :");
				for (MacroNode node : macroData.macroNodes()) {
					MacroNodeExecutor exec = _mapExecutor.get(node);
					if (exec == null) {
						sb.append("\n  no executor : ");
						sb.append(node.toString());
						continue;
					}
					
					sb.append("\n  ");
					sb.append(exec);
					for (MacroNodeExecutor subexec : exec.getStartSet()) {
						sb.append("\n      (start)-->");
						sb.append(subexec);
					}
					for (MacroNodeExecutor subexec : exec.getNextSet()) {
						sb.append("\n      (next)-->");
						sb.append(subexec);
					}
					for (MacroNodeExecutor subexec : exec.getPreviousSet()) {
						sb.append("\n      <--(prev) ");
						sb.append(subexec);
					}
				}
				sb.append("\n(end of execution order)\n");
				engine.printDebug(sb.toString());
			}
		}
		
		// 成功
		return AADLMacroEngine.EXITCODE_SUCCEEDED;
	}

	/**
	 * 指定されたエグゼキューターの次に実行するエグゼキューターから、終了していないエグゼキューターのみキューに登録する。
	 * <p><b><i>注意：</i></b>
	 * <blockquote>
	 * このメソッドは同期化されないが、キューへの登録のみキュークラスの機能により同期化されている。
	 * このメソッドを何らかの同期ブロックに入れると、デッドロックする恐れがある。
	 * </blockquote>
	 * @param executor	対象のエグゼキューター
	 * @param withStartNodes	開始と同時に実行するエグゼキューターもあわせてキューに登録する場合は <tt>true</tt>
	 * @return	キューに登録された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 2.1.0
	 */
	private boolean enqueuNextExecutor(MacroNodeExecutor executor, boolean withStartNodes) {
		if (_terminating)
			return false;	// 終了処理中はキューイングしない
		
		// 終了していないエグゼキューターのみをキューイング
		boolean enqueued = false;
		//--- 開始と同時に実行するエグゼキューター
		if (withStartNodes && !executor.isEmptyStarts()) {
			for (MacroNodeExecutor target : executor.getStartSet()) {
				if (target.getStatus() == MacroStatus.UNEXECUTED) {
					enqueued = true;
					_nextExecQueue.add(target);	// キューが一杯なら例外をスロー
				}
			}
		}
		//--- 次に実行するエグゼキューター
		if (!executor.isEmptyNexts()) {
			for (MacroNodeExecutor target : executor.getNextSet()) {
				if (target.getStatus() == MacroStatus.UNEXECUTED) {
					enqueued = true;
					_nextExecQueue.add(target);	// キューが一杯なら例外をスロー
				}
			}
		}
		
		// 完了
		return enqueued;
	}
	
	/**
	 * 指定されたエグゼキューターの開始と同時に実行するエグゼキューターから、終了していないエグゼキューターのみキューに登録する。
	 * <p><b><i>注意：</i></b>
	 * <blockquote>
	 * このメソッドは同期化されないが、キューへの登録のみキュークラスの機能により同期化されている。
	 * このメソッドを何らかの同期ブロックに入れると、デッドロックする恐れがある。
	 * </blockquote>
	 * @param executor	対象のエグゼキューター
	 * @return	キューに登録された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 2.1.0
	 */
	private boolean enqueuStartExecutor(MacroNodeExecutor executor) {
		if (_terminating)
			return false;	// 終了処理中はキューイングしない
		
		// 終了していないエグゼキューターのみをキューイング
		boolean enqueued = false;
		//--- 開始と同時に実行するエグゼキューター
		if (!executor.isEmptyStarts()) {
			for (MacroNodeExecutor target : executor.getStartSet()) {
				if (target.getStatus() == MacroStatus.UNEXECUTED) {
					enqueued = true;
					_nextExecQueue.add(target);	// キューが一杯なら例外をスロー
				}
			}
		}
		return enqueued;
	}

	/**
	 * 終了条件によるマクロ終了時のメッセージを表示する
	 * @param node				停止した位置のマクロノード
	 * @param causeExitCode	停止要因となった終了コード
	 */
	private void showTerminatedInfo(final MacroNode node, int causeExitCode) {
		engine.printError("Macro execution terminated by the exit code (%d) corresponding error condition : [line:%d]",
				causeExitCode, node.getLocation().getStartPosition());
	}

	/**
	 * プロセス開始メッセージの表示
	 * @param process	プロセス情報
	 */
	private void showStartProcessInfo(MacroNodeProcess process) {
		// print information for starting process
		if (engine.isVerboseMode()) {
			engine.printTrace("start %s", macroData.getMacroNameWithLocation(process.getMacroLocation()));
		}
		
		// print debug for starting process
		if (engine.isDebugMode()) {
			StringBuilder sb = new StringBuilder();
			sb.append("execute following command");
			File workDir = process.getWorkDirectory();
			if (workDir != null) {
				sb.append(" on \"");
				sb.append(workDir.toString());
				sb.append("\"");
			}
			sb.append(".");
			List<String> cmds = process.getCommandLine();
			if (cmds.isEmpty()) {
				sb.append("\n    noting!");
			} else {
				int i = 0;
				for (String str : cmds) {
					sb.append("\n    [");
					sb.append(++i);
					sb.append("]=");
					sb.append(str);
				}
			}
			engine.printDebug(sb.toString());
		}
	}

	/**
	 * プロセス終了メッセージの表示
	 * @param exitCode	プロセス終了コード
	 * @param process	プロセス情報
	 */
	private void showEndProcessInfo(int exitCode, MacroNodeProcess process) {
		if (engine.isVerboseMode() || engine.isDebugMode()) {
			String msg = String.format("end(code:%d) %s", exitCode, macroData.getMacroNameWithLocation(process.getMacroLocation()));
			if (engine.isVerboseMode())
				engine.printTrace(msg);
			else
				engine.printDebug(msg); 
		}
	}

	/**
	 * プロセス中断メッセージの表示
	 * @param exitCode	プロセス終了コード
	 * @param process	プロセス情報
	 * @since 2.1.0
	 */
	private void showTerminatedProcessInfo(int exitCode, MacroNodeProcess process) {
		if (engine.isVerboseMode() || engine.isDebugMode()) {
			String msg = String.format("Terminated (code:%d) %s", exitCode, macroData.getMacroNameWithLocation(process.getMacroLocation()));
			if (engine.isVerboseMode())
				engine.printTrace(msg);
			else
				engine.printDebug(msg); 
		}
	}

	/**
	 * プロセス強制終了メッセージの表示
	 * @param exitCode	プロセス終了コード
	 * @param process	プロセス情報
	 * @since 2.1.0
	 */
	private void showKilledProcessInfo(int exitCode, MacroNodeProcess process) {
		if (engine.isVerboseMode() || engine.isDebugMode()) {
			String msg = String.format("Killed (code:%d) %s", exitCode, macroData.getMacroNameWithLocation(process.getMacroLocation()));
			if (engine.isVerboseMode())
				engine.printTrace(msg);
			else
				engine.printDebug(msg); 
		}
	}

	/**
	 * マクロ要素の条件式を判定し、条件に一致しない場合はスキップする。
	 * このメソッドは、(終了条件式ではない)条件式をもつコマンドを
	 * 前提とする。
	 * 
	 * @param node	マクロノード
	 * @return	スキップとなる場合は <tt>true</tt> を返す。
	 */
	private boolean isSkippedMacro(final MacroNode node) {
		CommandActionNode can = node.getCommandNode();
		
		// 条件式が存在しない場合はスキップしない
		if (!can.hasChildren())
			return false;
		
		// 条件式ノードを取得
		CommandNode child = can.getChild(0);
		if (child instanceof CommandConditionNode) {
			// スキップ判定用条件式
			CommandConditionNode cond = (CommandConditionNode)child;
			boolean isSkipped = !cond.isTrueCondition(macroData);
			if (isSkipped && engine.isDebugMode()) {
				engine.printDebug("skipped %s : '%s'",
						macroData.getMacroNameWithLocation(node.getLocation()),
						node.getCommandString());
			}
			return isSkipped;
		}
		else {
			// スキップ判定用条件式ではないので、スキップしない
			return false;
		}
	}

	/**
	 * 指定された終了コードが終了条件に一致しているかを判定する。
	 * 
	 * @param exitCode	判定する終了コード
	 * @return	終了条件に一致した場合は <tt>true</tt> を返す。
	 */
	private boolean isTerminationCondition(final int exitCode) {
		if (termCond != null) {
			return termCond.isTermExitCode(exitCode);
		} else {
			// 終了条件が設定されていないコマンドの場合、終了コード判定では終了しない。
			return false;
		}
	}

	/**
	 * このメソッド呼び出し以降の、新たなマクロの実行を停止する。
	 * @return	実行中のマクロノード(エグゼキューター)が存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 2.1.0
	 */
	protected boolean stopMacroActivation() {
		_terminating = true;
		synchronized (_lock) {
			return (!_activeExecNodes.isEmpty());	// 実行中のエグゼキューターがあれば true を返す
		}
	}

// removed @since 2.1.0
//	/**
//	 * アクティブなプロセスが存在するかを判定する。
//	 * @return	アクティブなプロセスが存在している場合は <tt>true</tt>
//	 */
//	protected boolean hasActiveMacros() {
//		synchronized (_lock) {
//			return (!_activeExecNodes.isEmpty());
//		}
//	}

	/**
	 * アクティブなすべてのプロセスを強制終了する。
	 * <p><em>waitAllTermination</em> に <tt>false</tt> が指定されている場合は、シャットダウンフックからの呼び出しであることを示す。
	 * また、<em>termRequested</em> が <tt>true</tt> の場合は、外部プロセスからの中断要求に応答したことを示す。
	 * 
	 * @param reportError	強制終了したプロセス情報をエラーとして出力する場合は <tt>true</tt>
	 * @param waitAllTermination	すべてのプロセス終了を待機する場合は <tt>true</tt>、待機せずに強制終了する場合は <tt>false</tt>
	 * @param termRequested		外部プロセスからの中断要求に応答した場合は <tt>true</tt>
	 */
	protected void killAllActiveMacros(boolean reportError, boolean waitAllTermination, boolean termRequested) {
		List<MacroNodeExecutor> waitproclist = null;		// 終了待機するエグゼキューターのリスト
		StringBuilder msgbuf = null;
		
		// プロセスの中断もしくは強制終了指示
		synchronized (_lock) {
			if (_activeExecNodes.isEmpty()) {
				return;	// no active process
			}
			
			//--- メッセージバッファの初期化
			if (reportError || engine.isDebugMode()) {
				msgbuf = new StringBuilder();
				if (!waitAllTermination) {
					// シャットダウンフックからの強制終了であることを示す
					msgbuf.append("The following process was killed by shutdown.");
				}
				else if (termRequested) {
					// 中断要求への応答による中断処理であることを示す
					msgbuf.append("The following process was canceled by interrupt.");
				}
				else {
					// 終了コード判定に基づくエラーによる中断であることを示す
					msgbuf.append("The following process was canceled by error in other macro command.");
				}
			}

			//--- プロセス中断または強制終了
			if (waitAllTermination) {
				// プロセス中断要求
				waitproclist = new ArrayList<MacroNodeExecutor>(_activeExecNodes.size());
				Iterator<MacroNodeExecutor> it = _activeExecNodes.iterator();
				for (; it.hasNext(); ) {
					MacroNodeExecutor elem = it.next();
					MacroNodeProcess proc = elem.getProcess();
					if (proc != null) {
						if (engine.isDebugMode()) {
							engine.printDebug("@@@ Request termination : " + String.valueOf(elem.getTargetNode().getLocation()));
						}
						proc.terminate();	// 中断要求
						waitproclist.add(elem);
						if (msgbuf != null) {
							msgbuf.append("\n    (canceled) ");
							msgbuf.append(macroData.getMacroNameWithLocation(elem.getTargetNode().getLocation()));
						}
					}
					else {
						it.remove();
					}
				}
			} else {
				// プロセス強制終了
				for (MacroNodeExecutor elem : _activeExecNodes) {
					MacroNodeProcess proc = elem.getProcess();
					if (proc != null) {
						if (engine.isDebugMode()) {
							engine.printDebug("@@@ Do kill process : " + String.valueOf(elem.getTargetNode().getLocation()));
						}
						proc.kill();	// 強制終了
						if (msgbuf != null) {
							msgbuf.append("\n    (killed) ");
							msgbuf.append(macroData.getMacroNameWithLocation(elem.getTargetNode().getLocation()));
						}
					}
				}
				_activeExecNodes.clear();	// 終了を待機しないので、リストをクリア
			}
		}
		
		// 終了待機
		if (waitproclist != null) {
			for (MacroNodeExecutor elem : waitproclist) {
				try {
					if (engine.isDebugMode()) {
						engine.printDebug("@@@ Waiting termination : " + String.valueOf(elem.getTargetNode().getLocation()));
					}
					//sb.append("\n    --- do waitFor() process.");
					elem.getProcess().waitFor();
					//sb.append("\n    --- do cleanup() process.");
					elem.getProcess().cleanup();
					if (engine.isDebugMode()) {
						engine.printDebug("@@@ Done wait termination : " + String.valueOf(elem.getTargetNode().getLocation()));
					}
					if (engine.isDebugMode()) {
						msgbuf.append("\n    Process was terminated : ");
						msgbuf.append(macroData.getMacroNameWithLocation(elem.getTargetNode().getLocation()));
					}
					synchronized (_activeExecNodes) {
						_activeExecNodes.remove(elem);	// 管理リストから除外
					}
				}
				catch (InterruptedException ignoreEx) {
					// 待機中に割り込み発生なら、待機終了
					if (msgbuf != null) {
						msgbuf.append("\n    --- Caught interrupt in termination waiting.");
					}
					break;
				}
			}
		}
		
		// メッセージ出力
		if (msgbuf != null) {
			if (reportError)
				engine.printError(msgbuf.toString());
			else
				engine.printDebug(msgbuf.toString());
		}
	}

	/**
	 * スレッドプール管理サービスをシャットダウンする。
	 * 最大 60 秒待機し、それでも終了できなかった場合は、即座にシャットダウンを適用する。
	 */
	protected void shutdownExecutorService() {
		// synchronized (_lock) すると、デッドロック!
		if (_macroExecutor != null && !_macroExecutor.isTerminated()) {
			try {
				engine.printDebug("Executor service(\"%s\") : start shutdown phase...", macroData.getMacroName());
				_macroExecutor.shutdown();
				if (!_macroExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
					// 最大時間待っても終わらない場合は、即終了実行
					engine.printDebug("Executor service(\"%s\") : Timed out await termination, shutdown now...", macroData.getMacroName());
				}
			}
			catch (InterruptedException ex) {
				if (engine.isDebugMode()) {
					engine.printStackTrace(ex);
					engine.printDebug("Executor service(\"%s\") : Interrupted to await termination, shutodown now...", macroData.getMacroName());
				}
				_macroExecutor.shutdownNow();
			}
			engine.printDebug("Executor service(\"%s\") : shutdown phase finished!", macroData.getMacroName());
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * AADLマクロのノード単位で実行するエグゼキューター。
	 * 実行時のマクロノード、および実行ステータス、実行順序の前後関係を保持する。
	 * 
	 * @version 2.1.0	2014/05/29
	 * @since 2.1.0
	 */
	protected class BasicMacroNodeExecutor extends MacroNodeExecutor
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		/** 実行対象ノード。<tt>null</tt> なら、メインストリームの終端であることを示す。 **/
		protected final MacroNode			_targetNode;
		/** 終了コード、未完もしくは未定義の場合は <tt>null</tt> **/
		protected volatile Integer			_exitCode;
		/** 実行プロセス情報(プロセスを起動した場合) **/
		protected volatile MacroNodeProcess	_process;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		protected BasicMacroNodeExecutor(final MacroNode node) {
			super();
			
			// asign
			_targetNode = node;
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------
		
		public MacroNode getTargetNode() {
			return _targetNode;
		}
		
		/**
		 * このノードが実行されたときに開始されたプロセスオブジェクトを取得する。
		 * @return	プロセスが開始していればそのオブジェクト、なければ <tt>null</tt>
		 */
		public MacroNodeProcess getProcess() {
			return _process;
		}

		/**
		 * このノードのプロセスオブジェクトを設定する。
		 * @param proc	設定するプロセスオブジェクト、もしくは <tt>null</tt>
		 */
		public void setProcess(MacroNodeProcess proc) {
			_process = proc;
		}
		
		/**
		 * このノードを実行する。
		 * 必要に応じて、スレッドを起動し、実行完了を待機する。
		 * @param lastExitCode	直前の終了コード 
		 * @return	正常に終了し処理が継続できる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 */
		public boolean execNode(int lastExitCode) throws IOException
		{
			// スキップ判定
			if (isSkippedMacro(_targetNode)) {
				// スキップした場合は、ステータスを更新
				synchronized (this) {
					_status = MacroStatus.SKIPPED;
					_exitCode = null;
				}
				enqueuNextExecutor(this, true);	// 次のエグゼキューターをキューイング
				return true;	// 実行継続
			}
			
			// スケジュール実行判定
			if (_targetNode.getCommandModifier() == MacroModifier.AFTER) {
				//--- すべての待機ノードが実行完了していなければ、スキップ
				for (String targetProcName : _targetNode.getCommandProcessNameListAsModifier()) {
					MacroNodeExecutor waitExecutor = _mapExecutor.get(macroData.getMacroNodeByProcessName(targetProcName));
					if (waitExecutor != null) {
						MacroStatus targetStatus = waitExecutor.getStatus();
						if (targetStatus != MacroStatus.SKIPPED && targetStatus != MacroStatus.COMPLETED) {
							// 実行がスキップもしくは終了していない場合(中断を除く)は、中止（別のエグゼキューターへ委譲)
							if (engine.isDebugMode()) {
								engine.printDebug("AFTER modifier %s : process(%s) is not completed : %s",
										macroData.getMacroNameWithLocation(_targetNode.getLocation()),
										String.valueOf(targetProcName), String.valueOf(targetStatus));
							}
							return true;	// 実行継続
						}
						else if (engine.isDebugMode()) {
							// デバッグメッセージ
							engine.printDebug("AFTER modifier %s : Wait succeeded of process(%s) = %s",
									macroData.getMacroNameWithLocation(_targetNode.getLocation()),
									String.valueOf(targetProcName), String.valueOf(targetStatus));
						}
					}
				}
				// 待機完了
				if (engine.isDebugMode()) {
					engine.printDebug("AFTER modifier %s : Wait succeeded all processes.",
							macroData.getMacroNameWithLocation(_targetNode.getLocation()));
				}
			}
			
			// 外部からのプロセス中断要求をチェック
			if (InterruptibleJavaMainProcessImpl.acceptTerminateRequest()) {
				// プロセス中断要求への応答
				_terminating = true;
				synchronized (this) {
					_status   = MacroStatus.SKIPPED;
					_exitCode = AADLMacroEngine.EXITCODE_TERMINATE_EXEC;
				}
				return false;	// terminate
			}
			
			// コマンド実行
			return execCommand(_targetNode.getCommandAction(), lastExitCode);
		}
		
		public void run() {
			if (_process != null) {
				waitProcess(this, !_terminating);
			}
			if (engine.isDebugMode()) {
				engine.printDebug("@@@ Executor task finished : " + String.valueOf(_targetNode.getLocation()));
			}
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------

		/**
		 * 指定されたエグゼキューターのプロセスを、指定されたコマンドで起動する。
		 * 起動したプロセスはエグゼキューターに格納され、実行完了を監視するスレッドを開始する。
		 * @param executor		プロセスを開始するエグゼキューター
		 * @param commands		プロセス起動コマンド
		 * @throws IOException	プロセス起動に失敗した場合
		 * @since 2.1.0
		 */
		private void startProcess(final MacroNodeExecutor executor, final String...commands) throws IOException
		{
			synchronized (this) {
				executor._status = MacroStatus.RUNNING;
				MacroNodeProcess proc = new MacroNodeProcess(engine, macroData.getWorkDir(), executor.getTargetNode().getLocation(), commands);
				showStartProcessInfo(proc);
				proc.start();
				executor.setProcess(proc);
			}
			
			// 実行完了待機スレッドの開始
			synchronized (_lock) {
				//--- 実行中リストへ登録
				_activeExecNodes.add(executor);
				//--- 開始と同時に実行するエグゼキューターをキューイング
				enqueuStartExecutor(executor);
				//--- 待機スレッド開始
				_macroExecutor.execute(executor);
			}
		}

		/**
		 * 指定されたエグゼキューターのプロセス完了を待機する。
		 * <em>eqneueu</em> に <tt>true</tt> が指定されている場合、終了もしくは中断に関わらず、
		 * 指定されたエグゼキューターをキューに追加する。
		 * 待機が中断されたときは、プロセスの実行を中断し、プロセス中断処理完了まで待機する。
		 * プロセスがない場合、処理は行わず、キューへの追加も行わずに <tt>true</tt> を返す。
		 * @param executor	待機するプロセスを持つエグゼキューター
		 * @param enqueue	プロセス完了後にエグゼキューターをキューに登録する場合は <tt>true</tt>
		 * @return	待機成功した場合は <tt>true</tt>、待機が中断された場合は <tt>false</tt>
		 * @throws NullPointerException <em>executor</em> が <tt>null</tt> の場合
		 * @since 2.1.0
		 */
		private boolean waitProcess(final MacroNodeExecutor executor, boolean enqueue) {
			boolean finished = true;
			MacroNodeProcess proc = executor.getProcess();
			if (proc != null) {
				// 通常の待機
				if (engine.isDebugMode()) {
					engine.printDebug("@@@ waitProcess : Waiting finish : " + String.valueOf(_targetNode.getLocation()));
				}
				try {
					executor._exitCode = proc.waitFor();
				}
				catch (InterruptedException ex) {
					finished = false;
				}
				
				// 中断要求があれば、プロセスを中断する
				boolean killed = false;
				if (_terminating || !finished) {
					finished = false;
					if (engine.isDebugMode()) {
						engine.printDebug("@@@ waitProcess : Request termination : " + String.valueOf(_targetNode.getLocation()));
					}
					proc.terminate();
					try {
						executor._exitCode = proc.waitFor();
						if (engine.isDebugMode()) {
							engine.printDebug("@@@ waitProcess : Done wait termination : " + String.valueOf(_targetNode.getLocation()));
						}
					}
					catch (InterruptedException ex) {
						// 中断処理待機が中断された場合は、kill
						executor._exitCode = AADLMacroEngine.EXITCODE_TERMINATE_EXEC;
						killed = true;
						if (engine.isDebugMode()) {
							engine.printDebug("@@@ waitProcess : Do kill process : " + String.valueOf(_targetNode.getLocation()));
						}
						proc.kill();
					}
				}
				
				// クリーンアップ
				proc.cleanup();
					
				// ステータス更新
				synchronized (this) {
					if (finished) {
						executor._status = MacroStatus.PROC_FINISHED;
						showEndProcessInfo(executor._exitCode, proc);
					}
					else if (killed) {
						executor._status = MacroStatus.TERMINATED;
						showKilledProcessInfo(executor._exitCode, proc);
					}
					else {
						executor._status = MacroStatus.TERMINATED;
						showTerminatedProcessInfo(executor._exitCode, proc);
					}
				}
					
				// 実行中リストからの除去と、終了判定のためのキューイング
				synchronized (_lock) {	// デッドロックに注意!
					// 実行中リストからの除去
					_activeExecNodes.remove(this);
					
					// 終了中ではなくキューイングが要求されている場合のみ、このエグゼキューターをキューへ登録
					if (!_terminating && enqueue) {
						if (engine.isDebugMode()) {
							engine.printDebug("@@@ waitProcess : Enqueue finished executor : " + String.valueOf(_targetNode.getLocation()));
						}
						_nextExecQueue.add(executor);	// キューが一杯なら例外
					}
				}
				if (engine.isDebugMode()) {
					engine.printDebug("@@@ waitProcess : Done " + String.valueOf(_targetNode.getLocation()));
				}
			}
			return finished;
		}

		/**
		 * 指定されたアクションに応じて、実行を開始する。
		 * @param action		このオブジェクトで実行するアクション
		 * @param lastExitCode	直前の終了コード 
		 * @return	正常に終了し処理が継続できる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 * @throws IOException	プロセス起動に失敗した場合
		 */
		protected boolean execCommand(MacroAction action, int lastExitCode) throws IOException
		{
			// waitコマンドは別のクラスで実装
			switch (action) {
				case GROUP:		//--- JAVA グループ実行コマンド
					return doExecGroup();
				case JAVA:		//--- JAVA コマンド
					return doExecJava();
				case MACRO:		//--- サブマクロコマンド
					return doExecMacro();
				case SHELL:		//--- Shell コマンド
					return doExecShell();
				case ERRORCOND:	//--- エラー条件設定コマンド
					return doExecErrorCond();
				case EXIT:		//--- 終了コマンド
					return doExecExit(lastExitCode);
				case ECHO:		//--- ECHO コマンド
					return doExecEcho();
				default :		//--- コメントとして処理
					return doExecComment();
			}
		}

		/**
		 * コメントの実行し、次に実行予定のエグゼキューターをキューイングする。
		 * コメントは、デバッグモードでのみ、その内容を所定のストリームへ出力する。
		 * @return	常に <tt>true</tt>
		 */
		protected boolean doExecComment() {
			if (engine.isDebugMode()) {
				engine.printDebug("no execution command %s : '%s'",
							macroData.getMacroNameWithLocation(_targetNode.getLocation()),
							_targetNode.getCommandString());
			}
			//--- 完了
			synchronized (this) {
				_exitCode = null;	// 終了コード判定不要
				_status = MacroStatus.COMPLETED;
			}
			enqueuNextExecutor(this, true);	// 終了コード判定不要の為、次のエグゼキューターをキューイング
			return true;	// 実行継続
		}

		/**
		 * 'echo' コマンドの実行を実行し、次に実行予定のエグゼキューターをキューイングする。
		 * @return	常に <tt>true</tt>
		 */
		protected boolean doExecEcho() {
			synchronized (this) {
				_status = MacroStatus.RUNNING;
				// echo
				String strEcho = _targetNode.getEchoString(macroData);
				engine.printMessage(strEcho);
				
				// プロセスIDが定義されている場合は、常に 0 をセット
				String pn = _targetNode.getProcessName();
				if (pn != null && pn.length() > 0) {
					macroData.putProcessExitCode(pn, 0);
				}
				// ※エコーコマンドには終了条件を適用しない
				_exitCode = null;	// 終了コード判定不要
				_status = MacroStatus.COMPLETED;
			}
			enqueuNextExecutor(this, true);	// 終了コード判定不要の為、次のエグゼキューターをキューイング
			return true;	// 実行継続
		}

		/**
		 * 'errorcond' コマンドの実行し、次に実行予定のエグゼキューターをキューイングする。
		 * @return	常に <tt>true</tt>
		 */
		protected boolean doExecErrorCond()
		{
			synchronized (this) {
				_status = MacroStatus.RUNNING;
				//--- 終了条件設定
				if (engine.isDebugMode()) {
					engine.printDebug("set error condition %s : '%s'",
							macroData.getMacroNameWithLocation(_targetNode.getLocation()),
							_targetNode.getCommandString());
				}
				CommandActionNode can = _targetNode.getCommandNode();
				if (can.hasChildren())
					termCond = (CommandTermConditionNode)can.getChild(0);
				else
					termCond = null;
				//--- 完了
				_exitCode = null;	// 終了コード判定不要
				_status = MacroStatus.COMPLETED;
			}
			enqueuNextExecutor(this, true);	// 終了コード判定不要の為、次のエグゼキューターをキューイング
			return true;	// 実行継続
		}

		/**
		 * 'exit' コマンドの実行し、必要であれば次に実行予定のエグゼキューターをキューイングする。
		 * @param	直前の終了コード 
		 * @return	このコマンド呼び出し時点でマクロ終了となるため、常に <tt>false</tt>
		 */
		protected boolean doExecExit(int lastExitCode) {
			synchronized (this) {
				_status = MacroStatus.RUNNING;
				//--- set last exit code
				if (engine.isDebugMode()) {
					engine.printDebug("exit macro %s : '%s'",
							macroData.getMacroNameWithLocation(_targetNode.getLocation()),
							_targetNode.getCommandString());
				}
				_exitCode = _targetNode.getExitCodeFromProcessName(macroData, lastExitCode);
				//--- 完了
				_exitCode = null;	// 終了コード判定不要
				_status = MacroStatus.COMPLETED;
			}
			//--- 完了(マクロ終了)
			return false;
		}
		
		/**
		 * '&' コマンドの実行し、プロセス開始できたらプロセス完了待機スレッドを開始する。
		 * @return	終了コード、スキップされた場合は <tt>null</tt>
		 * @throws IOException	プロセス起動に失敗した場合
		 */
		protected boolean doExecGroup() throws IOException
		{
			//--- JAVAコマンドグループ : 非同期に開始
			assert(_targetNode.getCommandModifier() == null);
			startProcess(this, _targetNode.getJavaModuleCommandArray(engine, macroData));
			return true;
		}
		
		/**
		 * 'java' コマンドの実行し、プロセス開始できたらプロセス完了待機スレッドを開始する。
		 * @return	終了コード、スキップされた場合は <tt>null</tt>
		 * @throws IOException	プロセス起動に失敗した場合
		 */
		protected boolean doExecJava() throws IOException
		{
			//--- JAVA コマンド
			startProcess(this, _targetNode.getJavaModuleCommandArray(engine, macroData));
			return true;
		}
		
		/**
		 * 'macro' コマンドの実行し、プロセス開始できたらプロセス完了待機スレッドを開始する。
		 * @return	終了コード、スキップされた場合は <tt>null</tt>
		 * @throws IOException	プロセス起動に失敗した場合
		 */
		protected boolean doExecMacro() throws IOException
		{
			//--- サブマクロ
			startProcess(this, _targetNode.getSubMacroCommandArray(engine, macroData));
			return true;
		}
		
		/**
		 * 'exec' コマンドの実行し、プロセス開始できたらプロセス完了待機スレッドを開始する。
		 * @return	終了コード、スキップされた場合は <tt>null</tt>
		 * @throws IOException	プロセス起動に失敗した場合
		 */
		protected boolean doExecShell() throws IOException
		{
			//--- Shell コマンド
			startProcess(this, _targetNode.getShellCommandArray(macroData));
			return true;
		}
	}
	
	/**
	 * AADLマクロの 'wait'コマンドによる実行完了を待機するエグゼキューター。
	 * 実行時のマクロノード、および実行ステータス、実行順序の前後関係を保持する。
	 * このノードの実行は、待機対象の各コマンド実行後にそれぞれキューイングされ呼び出される。
	 * そのため、待機対象コマンドの実行完了判定において、すべての待機対象コマンドの実行が
	 * 終了している場合のみ、次のコマンドを実行する。
	 * 
	 * @version 2.1.0	2014/05/29
	 * @since 2.1.0
	 */
	protected class MacroWaitNodeExecutor extends BasicMacroNodeExecutor
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		protected MacroWaitNodeExecutor(final MacroNode node) {
			super(node);
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------
		
		/**
		 * このノードが実行されたときに開始されたプロセスオブジェクトを取得する。
		 * @return	常に <tt>null</tt>
		 */
		public MacroNodeProcess getProcess() {
			return null;
		}

		/**
		 * このメソッドはサポートされない。
		 * @throws UnsupportedOperationException
		 */
		public void setProcess(MacroNodeProcess proc) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void run() {
			// No entry
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------

		/**
		 * 指定されたアクションに応じて、実行を開始する。
		 * @param action		このオブジェクトで実行するアクション
		 * @param lastExitCode	直前の終了コード 
		 * @return	正常に終了し処理が継続できる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 */
		@Override
		protected boolean execCommand(MacroAction action, int lastExitCode) throws IOException
		{
			if (action == MacroAction.WAIT) {
				//--- 待機(wait)コマンド
				synchronized (this) {
					// 処理が完了するまでエグゼキューターのステータスは変更しない(同じインスタンスが複数回キューで処理されるため)
					Integer resultExitCode = null;
					//--- 実行完了チェック(このエグゼキューターの前に実行されるすべてのエグゼキューターの完了を待機)
					for (MacroNodeExecutor waitExecutor : getPreviousSet()) {
						MacroStatus targetStatus = waitExecutor.getStatus();
						Integer targetExitCode = waitExecutor.getExitCode();
						String targetProcName = waitExecutor.getTargetProcessName();
						//--- 待機対象プロセス名かチェック
						if (!_targetNode.getCommandProcessNameListAsAction().contains(targetProcName)) {
							continue;
						}
						//--- 状態チェック
						if (targetStatus != MacroStatus.SKIPPED && targetStatus != MacroStatus.COMPLETED) {
							// 完了していないコマンドなら、他の待機に委譲
							if (engine.isDebugMode()) {
								engine.printDebug("wait macro %s : process(%s) is not completed : %s",
										macroData.getMacroNameWithLocation(_targetNode.getLocation()),
										String.valueOf(targetProcName), String.valueOf(targetStatus));
							}
							return true;	// 実行は継続
						}
						else if (engine.isDebugMode()) {
							// デバッグメッセージ
							engine.printDebug("wait macro %s : Wait succeeded of process(%s) = %s",
									macroData.getMacroNameWithLocation(_targetNode.getLocation()),
									String.valueOf(targetProcName), String.valueOf(targetStatus));
						}
						//--- 終了コードを保存
						if (targetExitCode != null) {
							resultExitCode = targetExitCode;
						}
					}
					
					// 待機完了
					if (engine.isDebugMode()) {
						engine.printDebug("wait macro %s : Wait succeeded all processes, exit code = %s",
								macroData.getMacroNameWithLocation(_targetNode.getLocation()),
								String.valueOf(resultExitCode));
					}
					//--- 最後の終了コードを登録
					if (resultExitCode != null) {
						String procName = _targetNode.getProcessName();
						if (procName != null && procName.length() > 0) {
							macroData.putProcessExitCode(procName, resultExitCode);
						}
					}
					_exitCode = resultExitCode;
					_status = MacroStatus.COMPLETED;
				}
				enqueuNextExecutor(this, true);	// 終了コード判定不要の為、次のエグゼキューターをキューイング
				return true;	// 実行継続
			}
			else {
				//--- コメント
				return doExecComment();
			}
		}
	}
	
	/**
	 * AADLマクロのグループノードの実行完了を待機するエグゼキューター。
	 * 実行時のマクロノード、および実行ステータス、実行順序の前後関係を保持する。
	 * 
	 * @version 2.1.0	2014/05/29
	 * @since 2.1.0
	 */
	protected class MacroGroupNodeWaiter extends MacroNodeExecutor
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		/** 待機対象のグループノードのリスト **/
		protected final ArrayList<MacroNodeExecutor>	_grpNodeList;
		/** 最後に実行完了判定したグループノード **/
		protected volatile MacroNodeExecutor			_lastGroupNode;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		protected MacroGroupNodeWaiter() {
			super();
			_grpNodeList = new ArrayList<MacroNodeExecutor>();
			_lastGroupNode = null;
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		@Override
		public String getTargetProcessName() {
			return null;	// プロセス名はなし
		}

		@Override
		public MacroNode getTargetNode() {
			return (_lastGroupNode==null ? null : _lastGroupNode.getTargetNode());
		}
		
		/**
		 * このノードが実行されたときに開始されたプロセスオブジェクトを取得する。
		 * @return	常に <tt>null</tt>
		 */
		public MacroNodeProcess getProcess() {
			return null;
		}

		/**
		 * このメソッドはサポートされない。
		 * @throws UnsupportedOperationException
		 */
		public void setProcess(MacroNodeProcess proc) {
			throw new UnsupportedOperationException();
		}

		/**
		 * このノードを実行する。
		 * 必要に応じて、スレッドを起動し、実行完了を待機する。
		 * @param lastExitCode	直前の終了コード 
		 * @return	正常に終了し処理が継続できる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 */
		public boolean execNode(int lastExitCode) throws IOException
		{
			//--- グループ待機専用
			synchronized (this) {
				// 処理が完了するまでエグゼキューターのステータスは変更しない(同じインスタンスが複数回キューで処理されるため)
				Integer resultExitCode = null;
				//--- 実行完了チェック
				for (MacroNodeExecutor targetExecutor : _grpNodeList) {
					String procname = targetExecutor.getTargetProcessName();
					MacroStatus targetStatus = targetExecutor.getStatus();
					Integer targetExitCode = targetExecutor.getExitCode();
					if (targetStatus != MacroStatus.SKIPPED && targetStatus != MacroStatus.COMPLETED) {
						// 完了していないコマンドなら、他の待機に委譲
						if (engine.isDebugMode()) {
							engine.printDebug("wait macro %s : process(%s) is not completed : %s",
									macroData.getMacroNameWithLocation(targetExecutor.getTargetNode().getLocation()),
									String.valueOf(procname), String.valueOf(targetStatus));
						}
						return true;	// 実行は継続
					}
					else if (engine.isDebugMode()) {
						// デバッグメッセージ
						engine.printDebug("Group wait executor for %s : Wait succeeded of process(%s) = %s",
								macroData.getMacroNameWithLocation(targetExecutor.getTargetNode().getLocation()),
								String.valueOf(procname), String.valueOf(targetStatus));
					}
					//--- 終了コードを保存
					if (targetExitCode != null) {
						resultExitCode = targetExitCode;
					}
				}
				// 待機完了
				if (engine.isDebugMode()) {
					engine.printDebug("Group wait executor : Wait succeeded all processes, exit code = %s",
							String.valueOf(resultExitCode));
				}
				//--- 最後の終了コードを登録
				_exitCode = resultExitCode;
				_status = MacroStatus.COMPLETED;
			}
			enqueuNextExecutor(this, true);	// 終了コード判定不要の為、次のエグゼキューターをキューイング
			return true;	// 実行継続
		}
		
		public void add(MacroNodeExecutor grpNodeExecutor) {
			_grpNodeList.add(grpNodeExecutor);
		}

		public void run() {
			// no entry
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getName());
			sb.append("@");
			sb.append(Integer.toHexString(hashCode()));
			sb.append('[');
			//--- command
			appendCommandString(sb, null);
			//--- process name
			sb.append(", ");
			appendProcessNameString(sb, null);
			sb.append(']');
			
			return sb.toString();
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------
	}
}
