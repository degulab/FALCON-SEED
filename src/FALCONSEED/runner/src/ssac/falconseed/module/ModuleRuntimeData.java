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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ModuleConfig.java	3.2.1	2015/07/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleConfig.java	3.2.0	2015/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleConfig.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleConfig.java	3.0.0	2014/03/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleConfig.java	2.0.0	2012/10/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleConfig.java	1.22	2012/08/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ssac.aadl.fs.module.generic.GenericFilterMain;
import ssac.aadl.fs.module.schema.SchemaConfig;
import ssac.aadl.macro.AADLMacroEngine;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.ModuleFileType;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgCsvFile;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.args.MExecArgTextFile;
import ssac.falconseed.module.args.MExecArgXmlFile;
import ssac.falconseed.module.setting.MExecDefSettings;
import ssac.falconseed.module.swing.MExecArgItemModel;
import ssac.falconseed.module.swing.MExecArgsModel;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.Classes;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.io.DefaultFile;
import ssac.util.io.VirtualFile;
import ssac.util.process.ClassPathSet;
import ssac.util.process.CommandExecutor;
import ssac.util.process.InterruptibleCommandExecutor;

/**
 * 実行時のモジュール引数と実行結果を保持するクラス。
 * このクラスでは、ユーザー定義データを保持することができる。
 * このオブジェクトのクローン時には、ユーザー定義データはシャローコピーとなり、
 * 編集時データはディープコピーとなる。
 * 
 * @version 3.2.1	2015/07/23
 * @since 1.22
 */
public class ModuleRuntimeData extends AbModuleConfig<ModuleArgConfig> implements IModuleResult<ModuleArgConfig>, Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
//	static private final long[] EMPTY_RUNNO_ARRAY	= new long[0];

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 実行開始時刻 **/
	private long	_lStartTime;
	/** 実行時間 **/
	private long	_lProcessTime;
	/** プロセス終了コード **/
	private int		_exitCode;
	/**
	 *  プロセス状態コード
	 *  @since 3.0.0
	 */
	private int		_procStatus;
	/** ユーザーによって中断されたフラグ **/
	private boolean	_userCanceled;
	/**
	 *  プロセスが出力した内容を保持するファイルの抽象パス
	 *  @since 3.0.0
	 */
	private File	_logFile;

//	/**
//	 *  ユーザー定義データ
//	 *  @since 2.00
//	 */
//	private Object		_userData;
	
	/**
	 * 編集時データ
	 * @since 3.1.0
	 */
	private ModuleEditData	_editData;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleRuntimeData() {
		super();
		clearResults();
		_editData = null;
	}
	
	public ModuleRuntimeData(final MExecDefSettings settings) {
		super(settings);
		clearResults();
		_editData = null;
	}

	/**
	 * 互換性のためのコンストラクタ
	 * @param argsmodel	モジュール実行時引数のデータモデル
	 */
	public ModuleRuntimeData(final MExecArgsModel argsmodel) {
		super(argsmodel);
		clearResults();
		_editData = null;
	}
	
	/**
	 * コピーコンストラクタ。
	 * 同一クラスの場合、実行結果情報はクリアされるが、ユーザーデータはシャローコピーとなる。
	 */
	public ModuleRuntimeData(final IModuleConfig<? extends IModuleArgConfig> src) {
		super(src);
		clearResults();
		if (src instanceof ModuleRuntimeData) {
//			//--- user data
//			_userData = ((ModuleRuntimeData)src)._userData;
			//--- edit data
			ModuleEditData srcEditData = ((ModuleRuntimeData)src)._editData;
			_editData = (srcEditData==null ? null : srcEditData.clone());
		}
		
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

//	/**
//	 * 任意のモジュール実行完了を待機するかどうかを問い合わせる。
//	 * @return	任意のモジュール実行完了を待機する場合は <tt>true</tt>、
//	 * 			直前のモジュール実行完了後に実行する場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	public boolean isWaitSpecified() {
//		return _waitSpecified;
//	}
//
//	/**
//	 * 任意のモジュール実行完了を待機するかどうかを設定する。
//	 * @param enabled	任意のモジュール実行完了を待機する場合は <tt>true</tt>、
//	 * 					直前のモジュール実行完了後に実行する場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	public void setWaitSpecified(boolean enabled) {
//		_waitSpecified = enabled;
//	}
//
//	/**
//	 * 実行完了を待機するモジュールの有無を判定する。
//	 * @return	待機モジュールが一つもない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	public boolean isEmptyWaitModules() {
//		return (_waitModules==null || _waitModules.isEmpty());
//	}
//
//	/**
//	 * このモジュール実行完了後に実行すべきモジュールの有無を判定する。
//	 * @return	次に実行するモジュールが一つもない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	public boolean isEmptyNextModules() {
//		return (_nextModules==null || _nextModules.isEmpty());
//	}
//
//	/**
//	 * 実行完了を待機するモジュールの総数を返す。
//	 * @return	待機するモジュール数
//	 * @since 3.1.0
//	 */
//	public int getWaitModuleCount() {
//		return (_waitModules==null ? 0 : _waitModules.size());
//	}
//
//	/**
//	 * このモジュール実行完了後に実行すべきモジュールの総数を返す。
//	 * @return	次に実行するモジュール数
//	 * @since 3.1.0
//	 */
//	public int getNextModuleCount() {
//		return (_nextModules==null ? 0 : _nextModules.size());
//	}
//
//	/**
//	 * 実行完了を待機するモジュールの変更不可能なセットを取得する。
//	 * @return	待機モジュールの変更不可能なセット
//	 * @since 3.1.0
//	 */
//	public Set<ModuleRuntimeData> getWaitModuleSet() {
//		if (_waitModules != null && !_waitModules.isEmpty()) {
//			return Collections.unmodifiableSet(_waitModules);
//		} else {
//			return Collections.emptySet();
//		}
//	}
//
//	/**
//	 * このモジュール実行完了後に実行すべきモジュールの変更不可能なセットを取得する。
//	 * @return	次に実行するモジュールの変更不可能なセット
//	 * @since 3.1.0
//	 */
//	public Set<ModuleRuntimeData> getNextModuleSet() {
//		if (_nextModules != null && !_nextModules.isEmpty()) {
//			return Collections.unmodifiableSet(_nextModules);
//		} else {
//			return Collections.emptySet();
//		}
//	}
//	
//	/**
//	 * 実行完了を待機するモジュールの実行番号を取得する。
//	 * なお、実行番号は昇順にソートされる。
//	 * @return	待機するモジュールがあればその実行番号を格納した配列、なければ空の配列
//	 * @since 3.1.0
//	 */
//	public long[] getWaitModuleRunNumberArray() {
//		long[] numbers = EMPTY_RUNNO_ARRAY;
//		if (_waitModules != null && !_waitModules.isEmpty()) {
//			numbers = new long[_waitModules.size()];
//			int index = 0;
//			for (ModuleRuntimeData data : _waitModules) {
//				numbers[index++] = data.getRunNo();
//			}
//			Arrays.sort(numbers);
//		}
//		return numbers;
//	}
//	
//	/**
//	 * このモジュール実行完了後に実行すべきモジュールの実行番号を取得する。
//	 * なお、実行番号は昇順にソートされる。
//	 * @return	次に実行するモジュールがあればその実行番号を格納した配列、なければ空の配列
//	 */
//	public long[] getNextModuleRunNumberArray() {
//		long[] numbers = EMPTY_RUNNO_ARRAY;
//		if (_nextModules != null && !_nextModules.isEmpty()) {
//			numbers = new long[_nextModules.size()];
//			int index = 0;
//			for (ModuleRuntimeData data : _nextModules) {
//				numbers[index++] = data.getRunNo();
//			}
//			Arrays.sort(numbers);
//		}
//		return numbers;
//	}
//
//	/**
//	 * 実行完了を待機するモジュールの実行番号をカンマ区切りの文字列として取得する。
//	 * なお、実行番号は昇順にソートされる。
//	 * @return	待機するモジュールがあればその実行番号、なければ空文字列
//	 * @since 3.1.0
//	 */
//	public String getWaitModuleRunNumberString() {
//		long[] numbers = getWaitModuleRunNumberArray();
//		if (numbers.length > 0) {
//			StringBuilder sb = new StringBuilder();
//			sb.append(numbers[0]);
//			for (int i = 1; i < numbers.length; ++i) {
//				sb.append(',');
//				sb.append(numbers[i]);
//			}
//			return sb.toString();
//		} else {
//			return "";
//		}
//	}
//
//	/**
//	 * このモジュール実行完了後に実行すべきモジュールの実行番号をカンマ区切りの文字列として取得する。
//	 * なお、実行番号は昇順にソートされる。
//	 * @return	次に実行するモジュールがあればその実行番号、なければ空文字列
//	 */
//	public String getNextModuleRunNumberString() {
//		long[] numbers = getNextModuleRunNumberArray();
//		if (numbers.length > 0) {
//			StringBuilder sb = new StringBuilder();
//			sb.append(numbers[0]);
//			for (int i = 1; i < numbers.length; ++i) {
//				sb.append(',');
//				sb.append(numbers[i]);
//			}
//			return sb.toString();
//		} else {
//			return "";
//		}
//	}
//
//	/**
//	 * 指定されモジュールが、待機するモジュールとして登録されているかを判定する。
//	 * @param aModule	検査するモジュール
//	 * @return	登録されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	public boolean containsWaitModule(ModuleRuntimeData aModule) {
//		return (_waitModules==null ? false : _waitModules.contains(aModule));
//	}
//
//	/**
//	 * 指定されたモジュールが、次に実行するモジュールとして登録されているかを判定する。
//	 * @param aModule	検査するモジュール
//	 * @return	登録されている場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	public boolean containsNextModule(ModuleRuntimeData aModule) {
//		return (_nextModules==null ? false : _nextModules.contains(aModule));
//	}
//	
//	/**
//	 * 指定されたモジュールを、待機するモジュールとして登録する。
//	 * 指定されたモジュールには、このオブジェクトが次に実行されるモジュールとして登録される。
//	 * @param aModule	登録するモジュール
//	 * @return	登録された場合は <tt>true</tt>、すでに登録済みの場合は <tt>false</tt>
//	 * @throws NullPointerException	引数が <tt>null</tt> の場合
//	 * @since 3.1.0
//	 */
//	public boolean connectWaitModule(ModuleRuntimeData aModule) {
//		if (aModule == null)
//			throw new NullPointerException();
//		boolean result = addWaitModule(aModule);
//		if (result) {
//			aModule.addNextModule(this);
//		}
//		return result;
//	}
//	
//	/**
//	 * 指定されたモジュールを、次に実行するモジュールとして登録する。
//	 * 指定されたモジュールには、このオブジェクトが待機するモジュールとして登録される。
//	 * @param aModule	登録するモジュール
//	 * @return	登録された場合は <tt>true</tt>、すでに登録済みの場合は <tt>false</tt>
//	 * @throws NullPointerException	引数が <tt>null</tt> の場合
//	 * @since 3.1.0
//	 */
//	public boolean connectNextModule(ModuleRuntimeData aModule) {
//		if (aModule == null)
//			throw new NullPointerException();
//		boolean result = addNextModule(aModule);
//		if (result) {
//			aModule.addWaitModule(this);
//		}
//		return result;
//	}
//
//	/**
//	 * 指定されたモジュールを、待機するモジュールから除外する。
//	 * 指定されたモジュールの次に実行するモジュールからも、このオブジェクトが除外される。
//	 * @param aModule	除外するモジュール
//	 * @return	除外された場合は <tt>true</tt>、登録されていない場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	public boolean disconnectWaitModule(ModuleRuntimeData aModule) {
//		boolean result = removeWaitModule(aModule);
//		if (result) {
//			aModule.removeNextModule(this);
//		}
//		return result;
//	}
//
//	/**
//	 * 指定されたモジュールを、次に実行するモジュールから除外する。
//	 * 指定されたモジュールの待機するモジュールからも、このオブジェクトが除外される。
//	 * @param aModule	除外するモジュール
//	 * @return	除外された場合は <tt>true</tt>、登録されていない場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	public boolean disconnectNextModule(ModuleRuntimeData aModule) {
//		boolean result = removeNextModule(aModule);
//		if (result) {
//			aModule.removeWaitModule(this);
//		}
//		return result;
//	}
//
//	/**
//	 * 待機するモジュールをすべて除外する。
//	 * 待機するモジュールとして登録されていたモジュールの次に実行するモジュールからも、このオブジェクトが除外される。
//	 * @return	除外された場合は <tt>true</tt>、待機するモジュールが一つもない場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	public boolean disconnectAllWaitModules() {
//		if (isEmptyWaitModules())
//			return false;
//		
//		for (Iterator<ModuleRuntimeData> it = _waitModules.iterator(); it.hasNext(); ) {
//			ModuleRuntimeData aModule = it.next();
//			aModule.removeNextModule(this);
//			it.remove();
//		}
//		return true;
//	}
//
//	/**
//	 * 次に実行するモジュールをすべて除外する。
//	 * 次に実行するモジュールとして登録されていたモジュールの待機するモジュールからも、このオブジェクトが除外される。
//	 * @return	除外された場合は <tt>true</tt>、次に実行するモジュールが一つもない場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	public boolean disconnectAllNextModules() {
//		if (isEmptyNextModules())
//			return false;
//		
//		for (Iterator<ModuleRuntimeData> it = _nextModules.iterator(); it.hasNext(); ) {
//			ModuleRuntimeData aModule = it.next();
//			aModule.removeWaitModule(this);
//			it.remove();
//		}
//		return true;
//	}

	/**
	 * 実行結果をクリアする。
	 */
	public void clearResults() {
		_lStartTime   = 0;
		_lProcessTime = 0;
		_exitCode     = 0;
		_userCanceled = false;
		_logFile      = null;
		_procStatus   = InterruptibleCommandExecutor.COMMAND_UNEXECUTED;
	}

	/**
	 * モジュール実行が開始された日時(エポック)を取得する。
	 * @return	モジュール実行開始時のエポック、未実行の場合は 0
	 */
	public long getStartTime() {
		return _lStartTime;
	}

	/**
	 * モジュール実行開始時の日時をエポックで設定する。
	 * @param time	モジュール実行開始時の日時を示すエポック
	 */
	public void setStartTime(long time) {
		_lStartTime = time;
	}

	/**
	 * モジュール実行時間を取得する。
	 * @return	モジュール実行時間を示すミリ秒の値
	 */
	public long getProcessTime() {
		return _lProcessTime;
	}

	/**
	 * モジュール実行時間を設定する。
	 * @param time	モジュール実行時間をミリ秒で指定する
	 */
	public void setProcessTime(long time) {
		_lProcessTime = time;
	}

	/**
	 * モジュールの終了コードを取得する。
	 * @return	終了コード
	 */
	public int getExitCode() {
		return _exitCode;
	}

	/**
	 * モジュールの終了コードを設定する。
	 * @param code	終了コード
	 */
	public void setExitCode(int code) {
		_exitCode = code;
	}

	/**
	 * モジュール実行がユーザーによって停止されたかどうかを判定する。
	 * @return	ユーザーによって停止された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isUserCanceled() {
		return _userCanceled;
	}

	/**
	 * モジュール実行がユーザーによって停止されたかどうかを示す状態を設定する。
	 * @param flag	ユーザーによって停止されたとする場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を指定する
	 */
	public void setUserCanceled(boolean flag) {
		_userCanceled = flag;
	}

	/**
	 * モジュール実行が成功したかどうかを取得する。
	 * @return	モジュール実行が正常に完了した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isSucceeded() {
		return (_exitCode == 0);
	}

// removed @since 3.1.0
//	/**
//	 * このインスタンスに設定されたユーザー定義データを取得する。
//	 * @return	ユーザー定義データ、もしくは <tt>null</tt>
//	 * @since 2.00
//	 */
//	public Object getUserData() {
//		return _userData;
//	}
//
//	/**
//	 * このインスタンスにユーザー定義データを設定する。
//	 * @param data	ユーザー定義データ、もしくは <tt>null</tt>
//	 * @since 2.00
//	 */
//	public void setUserData(Object data) {
//		_userData = data;
//	}

	/**
	 * このインスタンスに編集時データが設定されているかどうかを判定する。
	 * @return	編集時データが設定されている場合は <tt>true</tt>、設定されていない場合は <tt>null</tt>
	 */
	public boolean hasEditData() {
		return (_editData != null);
	}

	/**
	 * このインスタンスに設定された編集時データを取得する。
	 * @return	編集時データが設定されている場合はそのオブジェクト、設定されていない場合は <tt>null</tt>
	 * @since 3.1.0
	 */
	public ModuleEditData getEditData() {
		return _editData;
	}

	/**
	 * このインスタンスに、指定された編集時データを設定する。
	 * @param editData	設定する編集時データ、もしくは <tt>null</tt>
	 * @return	編集時データの内容が変更された場合は <tt>true</tt>
	 * @since 3.1.0
	 */
	public boolean setEditData(ModuleEditData editData) {
		if (!Objects.isEqual(editData, _editData)) {
			_editData = editData;
			return true;
		}
		// no changes
		return false;
	}

	/**
	 * モジュールの編集時エラーの有無を判定する。
	 * @return	エラーがある場合は <tt>true</tt>、ない場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	public boolean hasModuleError() {
		return (hasEditData() ? getEditData().hasError() : false);
	}

	/**
	 * 設定されているモジュールの編集時エラー情報を取得する。
	 * @return	エラーがある場合はそのオブジェクト、ない場合は <tt>null</tt>
	 * @since 3.1.0
	 */
	public FilterDataError getModuleError() {
		return (hasEditData() ? getEditData().getError() : null);
	}

	/**
	 * モジュールの編集時エラー情報をクリアする。
	 * @return	エラーの内容が変更された場合は <tt>true</tt>
	 * @since 3.1.0
	 */
	public boolean clearModuleError() {
		return (hasEditData() ? getEditData().clearError() : false);
	}

	/**
	 * デフォルトのモジュール編集時データを生成する。
	 * @return	モジュール編集時データのインスタンス
	 * @since 3.1.0
	 */
	protected ModuleEditData createDefaultEditData() {
		return new ModuleEditData();
	}

	/**
	 * モジュールの編集時エラーを設定する。
	 * @param error	設定するエラー情報、エラーをクリアする場合は <tt>null</tt>
	 * @return	エラーの内容が変更された場合は <tt>true</tt>
	 * @since 3.1.0
	 */
	public boolean setModuleError(FilterDataError error) {
		if (hasEditData()) {
			return getEditData().setError(error);
		}
		else if (error != null) {
			_editData = new ModuleEditData();
			_editData.setError(error);
			return true;
		}
		
		// no changes
		return false;
	}

	/**
	 * このモジュールデータが実行したプロセスの状態を取得する。
	 * プロセスの状態コードは {@link ssac.util.process.InterruptibleCommandExecutor#status()} が返したコードとなる。
	 * @return	プロセスの状態を示すコード
	 * @see ssac.util.process.InterruptibleCommandExecutor#status()
	 * @since 3.0.0
	 */
	public int getProcessStatus() {
		return _procStatus;
	}

	/**
	 * このモジュールデータが実行したプロセスの状態を設定する。
	 * プロセスの状態コードは {@link ssac.util.process.InterruptibleCommandExecutor#status()} が返したコードであること。
	 * @param status	プロセスの状態を示すコード
	 * @see ssac.util.process.InterruptibleCommandExecutor#status()
	 * @since 3.0.0
	 */
	public void setProcessStatus(int status) {
		_procStatus = status;
	}

	/**
	 * このモジュール実行時の標準出力、標準エラー出力の内容を記録するログファイルのパスを返す。
	 * @return	ログファイルの抽象パス、設定されていない場合は <tt>null</tt>
	 * @since 3.0.0
	 */
	public File getLogFile() {
		return _logFile;
	}

	/**
	 * このモジュール実行時の標準出力、標準エラー出力の内容を記録するログファイルのパスを設定する。
	 * ログファイルのパスが設定された後に実行されたモジュールの出力は、設定されたログファイルに出力される。
	 * モジュール実行後に変更しても、コピーなどはされない。
	 * @param logFile	設定するログファイルの抽象パス、設定しない場合は <tt>null</tt>
	 * @since 3.0.0
	 */
	public void setLogFile(File logFile) {
		_logFile = logFile;
	}
	
	/**
	 * このオブジェクトの複製を生成する。
	 * 基本的にディープコピー。
	 */
	public ModuleRuntimeData clone() {
		try {
			ModuleRuntimeData newData = (ModuleRuntimeData)super.clone();
			newData._args = createArgsList(this);
//			newData._waitSpecified = false;	// wait only previous
//			newData._waitModules = null;	// clear paths
//			newData._nextModules = null;	// clear paths
			return newData;
		}
		catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	/**
	 * このオブジェクトのモジュール実行結果を設定する。
	 * @param stime			モジュール実行開始時の日時(エポック)
	 * @param executor		実行時のコマンド情報
	 * @param userCanceled	ユーザーによって中断された場合は <tt>true</tt> を指定する
	 * @throws NullPointerException	<em>executor</em> が <tt>null</tt> の場合
	 */
	public void setResults(final CommandExecutor executor, boolean userCanceled) {
		setUserCanceled(userCanceled);
		setProcessTime(executor.getProcessingTimeMillis());
		setExitCode(executor.getExitCode());
	}

	/**
	 * このオブジェクトのモジュール実行結果を設定する。
	 * @param stime			モジュール実行開始時の日時(エポック)
	 * @param executor		実行時のコマンド情報
	 * @param userCanceled	ユーザーによって中断された場合は <tt>true</tt> を指定する
	 * @throws NullPointerException	<em>executor</em> が <tt>null</tt> の場合
	 * @since 3.0.0
	 */
	public void setResults(final InterruptibleCommandExecutor executor, boolean userCanceled) {
		setUserCanceled(userCanceled);
		setProcessTime(executor.getProcessingTimeMillis());
		setExitCode(executor.getExitCode());
	}
	
	static public String formatErrorMessage(final ModuleRuntimeData data, String format, Object...args) {
		return formatErrorMessage(data, String.format(format, args));
	}
	
	static public String formatErrorMessage(final ModuleRuntimeData data, String message) {
		return formatErrorMessage(data, (IModuleArgConfig)null, message);
	}
	
	static public String formatErrorMessgae(final ModuleRuntimeData data, final IModuleArgConfig arg, String format, Object...args) {
		return formatErrorMessage(data, arg, String.format(format, args));
	}
	
	static public String formatErrorMessage(final ModuleRuntimeData data, final IModuleArgConfig arg, String message) {
		StringBuilder sb = new StringBuilder();
		if (data != null) {
			sb.append("[");
			long rno = data.getRunNo();
			if (rno != 0L) {
				sb.append(rno);
			}
			String name = data.getName();
			if (name != null && name.length() > 0) {
				if (rno != 0L)
					sb.append(":");
				sb.append(name);
			}
			sb.append("] ");
		}
		if (arg != null) {
			sb.append("($");
			sb.append(arg.getArgNo());
			sb.append(")");
			ModuleArgType atype = arg.getType();
			if (atype != null) {
				sb.append(atype);
			}
			sb.append(" : ");
		}
		if (message != null && message.length() > 0) {
			sb.append(message);
		}
		return sb.toString();
	}

	/**
	 * このオブジェクトの引数定義から、実行するコマンドを生成する。
	 * @return	生成されたコマンド
	 * @throws IOException				テンポラリファイル生成に失敗した場合
	 * @throws IllegalStateException	実行設定が正しくない場合
	 */
	public CommandExecutor createCommandExecutor() throws IOException
	{
		// check exist module
		VirtualFile vfModule = this.getModuleFile();
		if (vfModule == null)
			throw new IllegalStateException(formatErrorMessage(this, "Target module is nothing."));
		if (!vfModule.exists())
			throw new IllegalStateException(formatErrorMessage(this, "Target module is not found : \"%s\"", vfModule.toString()));
		if (!vfModule.isFile())
			throw new IllegalStateException(formatErrorMessage(this, "Target module is not file : \"%s\"", vfModule.toString()));
		File fModule = ModuleFileManager.toJavaFile(vfModule);
		if (fModule == null)
			throw new IllegalStateException(formatErrorMessage(this, "Target module is not local file : \"%s\"", vfModule.toURI()));
		
		// verify arguments
		correctArguments();
		
		// create CommandExecutor by module file type
		ModuleFileType ftype = this.getModuleType();
		if (ModuleFileType.AADL_JAR == ftype) {
			// AADL Execution module (jar)
			return createAADLModuleExecutor(fModule);
		}
		else if (ModuleFileType.AADL_MACRO == ftype) {
			// AADL Macro file (amf)
			return createAADLMacroExecutor(fModule);
		}
		else {
			// unknown file type
			throw new IllegalStateException(formatErrorMessage(this, "Target module is illegal file type : %s",String.valueOf(ftype)));
		}
	}
	
	/**
	 * このオブジェクトの引数定義から、中断可能な実行コマンドを生成する。
	 * @return	生成されたコマンド
	 * @throws IOException				テンポラリファイル生成に失敗した場合
	 * @throws IllegalStateException	実行設定が正しくない場合
	 * @since 3.0.0
	 */
	public InterruptibleCommandExecutor createInterruptibleCommandExecutor() throws IOException
	{
		// check exist module
		VirtualFile vfModule = this.getModuleFile();
		if (vfModule == null)
			throw new IllegalStateException(formatErrorMessage(this, "Target module is nothing."));
		if (!vfModule.exists())
			throw new IllegalStateException(formatErrorMessage(this, "Target module is not found : \"%s\"", vfModule.toString()));
		if (!vfModule.isFile())
			throw new IllegalStateException(formatErrorMessage(this, "Target module is not file : \"%s\"", vfModule.toString()));
		File fModule = ModuleFileManager.toJavaFile(vfModule);
		if (fModule == null)
			throw new IllegalStateException(formatErrorMessage(this, "Target module is not local file : \"%s\"", vfModule.toURI()));
		
		// verify arguments
		correctArguments();
		
		// create CommandExecutor by module file type
		ModuleFileType ftype = this.getModuleType();
		if (ModuleFileType.AADL_JAR == ftype) {
			// AADL Execution module (jar)
			return createInterruptibleAADLModuleExecutor(fModule);
		}
		else if (ModuleFileType.AADL_MACRO == ftype) {
			// AADL Macro file (amf)
			return createInterruptibleAADLMacroExecutor(fModule);
		}
		else if (ModuleFileType.GENERIC_FILTER == ftype) {
			// Generic filter file (xml)
			return createInterruptibleGenericFilterExecutor(fModule);
		}
		else {
			// unknown file type
			throw new IllegalStateException(formatErrorMessage(this, "Target module is illegal file type : %s",String.valueOf(ftype)));
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

//	/**
//	 * 指定されたモジュールを、待機するモジュールのセットに追加する。
//	 * このメソッドでは、セットへの追加のみで、指定されたモジュールは変更しない。
//	 * @param module	追加するモジュール
//	 * @return	追加された場合は <tt>true</tt>、すでにセットに含まれている場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	protected boolean addWaitModule(ModuleRuntimeData module) {
//		if (_waitModules == null) {
//			_waitModules = new HashSet<ModuleRuntimeData>();
//		}
//		return _waitModules.add(module);
//	}
//
//	/**
//	 * 指定されたモジュールを、次にするモジュールのセットに追加する。
//	 * このメソッドでは、セットへの追加のみで、指定されたモジュールは変更しない。
//	 * @param module	追加するモジュール
//	 * @return	追加された場合は <tt>true</tt>、すでにセットに含まれている場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	protected boolean addNextModule(ModuleRuntimeData module) {
//		if (_nextModules == null) {
//			_nextModules = new HashSet<ModuleRuntimeData>();
//		}
//		return _nextModules.add(module);
//	}
//	
//	/**
//	 * 待機するモジュールのセットから、指定されたモジュールを除外する。
//	 * このメソッドでは、セットから除外するのみで、指定されたモジュールは変更しない。
//	 * @param module	除外するモジュール
//	 * @return	除外された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	protected boolean removeWaitModule(ModuleRuntimeData module) {
//		return _waitModules.remove(module);
//	}
//	
//	/**
//	 * 次に実行するモジュールのセットから、指定されたモジュールを除外する。
//	 * このメソッドでは、セットから除外するのみで、指定されたモジュールは変更しない。
//	 * @param module	除外するモジュール
//	 * @return	除外された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @since 3.1.0
//	 */
//	protected boolean removeNextModule(ModuleRuntimeData module) {
//		return _nextModules.remove(module);
//	}

	/**
	 * モジュール実行定義による実行で使用する、標準的な
	 * テンポラリファイルを生成する。ここで生成されたファイルは
	 * <code>deleteOnExit()</code> メソッドが呼び出された状態となっている。
	 * @return	生成されたファイルの抽象パス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	private File createDefaultTemporaryArgumentFile() throws IOException
	{
		File tempfile = File.createTempFile("out", ".tmp");
		tempfile.deleteOnExit();
		return tempfile;
	}
	
	private File createTemporaryFile(String prefix, String suffix) throws IOException
	{
		if (prefix == null || prefix.length() < 3)
			prefix = "out";
		if (suffix == null || suffix.length() < 1)
			suffix = ".tmp";
		File tempfile = File.createTempFile(prefix, suffix);
		tempfile.deleteOnExit();
		return tempfile;
	}
	
	/**
	 * このオブジェクトの実行時データを整形する。
	 * @throws IOException				テンポラリファイル生成に失敗した場合
	 * @throws IllegalStateException	実行時の引数が正常に整形できなかった場合
	 */
	protected void correctArguments() throws IOException
	{
		for (IModuleArgConfig arg : this) {
			ModuleArgType atype = arg.getType();
			try {
				if (ModuleArgType.IN == atype) {
					correctInputFileArgument(arg);
				}
				else if (ModuleArgType.OUT == atype) {
					correctOutputFileArgument(arg);
				}
			}
			catch (IOException ex) {
				throw ex;
			}
			catch (Throwable ex) {
				throw new IllegalStateException(formatErrorMessage(this, arg, ex.getLocalizedMessage()), ex);
			}
			
			Object value = arg.getValue();
			if (value == null) {
				throw new IllegalStateException(formatErrorMessage(this, arg, "Argument value is null."));
			}
			else if (value.toString().length() <= 0) {
				throw new IllegalStateException(formatErrorMessage(this, arg, "Argument value is empty."));
			}
		}
	}

	/**
	 * [IN]属性の実行時引数の値を整形する。
	 * @param arg	対象引数のデータオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	パラメータ種別がサポート外の場合
	 */
	private void correctInputFileArgument(final IModuleArgConfig arg)
	{
		// パラメータチェック
		IMExecArgParam param = arg.getParameterType();
		if (param instanceof MExecArgTempFile) {
			//--- サポートしていない
			throw new IllegalArgumentException("MExecArgTempFile parameter not supported.");
		}
		
		// ファイルオブジェクトの取得
		Object value = arg.getValue();
		
		// [OUT]引数が指定されていれば、その値を取得
		if (value instanceof ModuleArgID) {
			ModuleArgID outArgID = (ModuleArgID)value;
			value = outArgID.getValue();
			arg.setValue(value);
		}

		// ファイルオブジェクトを正規化
		VirtualFile vfFile = null;
		if (value instanceof DefaultFile) {
			vfFile = (DefaultFile)value;
		}
		else if (value instanceof File) {
			vfFile = ModuleFileManager.fromJavaFile((File)value);
		}
		
		// 値が更新されていれば、代入
		if (vfFile != null && vfFile != value) {
			arg.setValue(vfFile);
		}
	}
	
	/**
	 * [OUT]属性の実行時引数の値を整形する。
	 * @param arg	対象引数のデータオブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IOException	テンポラリファイルの生成に失敗した場合
	 */
	private void correctOutputFileArgument(final IModuleArgConfig arg) throws IOException
	{
		// ファイルオブジェクトの取得
		Object value = arg.getValue();
		VirtualFile vfFile = null;
		if (value instanceof DefaultFile) {
			vfFile = (DefaultFile)value;
		}
		else if (value instanceof File) {
			vfFile = ModuleFileManager.fromJavaFile((File)value);
		}

		// 値の整形
		IMExecArgParam param = arg.getParameterType();
		if (param instanceof MExecArgTempFile) {
			// テンポラリファイル指定
			if (vfFile == null) {
				// テンポラリファイルを生成
				vfFile = ModuleFileManager.fromJavaFile(createDefaultTemporaryArgumentFile());
			}
		}
		else if (!arg.isFixedValue() && arg.getOutToTempEnabled()) {
			// テンポラリファイル指定
			String prefix = arg.getTempFilePrefix();
			String suffix = null;
			if (param instanceof MExecArgCsvFile) {
				suffix = RunnerMessages.getInstance().extCSV;
			}
			else if (param instanceof MExecArgXmlFile) {
				suffix = RunnerMessages.getInstance().extXML;
			}
			else if (param instanceof MExecArgTextFile) {
				suffix = RunnerMessages.getInstance().extTXT;
			}
			vfFile = ModuleFileManager.fromJavaFile(createTemporaryFile(prefix, suffix));
		}
		
		// 値が更新されていれば、代入
		if (vfFile != null && vfFile != value) {
			arg.setValue(vfFile);
		}
	}
	
	protected ArrayList<ModuleArgConfig> createArgsList() {
		return new ArrayList<ModuleArgConfig>();
	}
	
	protected ArrayList<ModuleArgConfig> createArgsList(final MExecDefSettings settings) {
		int numargs = settings.getNumArguments();
		ArrayList<ModuleArgConfig> list = new ArrayList<ModuleArgConfig>(numargs);
		for (int i = 0; i < numargs; i++) {
			ModuleArgConfig newarg = new ModuleArgConfig((i+1), settings.getArgument(i));
			list.add(newarg);
		}
		return list;
	}
	
	protected ArrayList<ModuleArgConfig> createArgsList(final MExecArgsModel argsmodel) {
		int numargs = argsmodel.getNumItems();
		ArrayList<ModuleArgConfig> list = new ArrayList<ModuleArgConfig>(numargs);
		for (int i = 0; i < numargs; i++) {
			MExecArgItemModel srcarg = argsmodel.getItem(i);
			ModuleArgConfig newarg = new ModuleArgConfig((i+1), srcarg.getType(), srcarg.getDescription(), srcarg.getParameter(), srcarg.getValue());
			newarg.setTempFilePrefix(srcarg.getTempFilePrefix());
			newarg.setOutToTempEnabled(srcarg.isOutToTempEnabled());
			newarg.setShowFileAfterRun(srcarg.isOptionEnabled());
			list.add(newarg);
		}
		return list;
	}
	
	protected ArrayList<ModuleArgConfig> createArgsList(final IModuleConfig<? extends IModuleArgConfig> src) {
		int numargs = src.getArgumentCount();
		ArrayList<ModuleArgConfig> list = new ArrayList<ModuleArgConfig>(numargs);
		for (int i = 0; i < numargs; i++) {
			ModuleArgConfig newarg = new ModuleArgConfig((i+1), src.getArgument(i));
			list.add(newarg);
		}
		return list;
	}

	/**
	 * このオブジェクトの引数定義から、AADL実行モジュールを実行するコマンドを生成する。
	 * @param fModule	実行モジュールの位置を示す <code>java.io.File</code> オブジェクト
	 * @return	生成されたコマンド
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数の設定が正しくない場合
	 */
	protected CommandExecutor createAADLModuleExecutor(File fModule)
	{
		assert fModule != null;
		assert this.getModuleType()==ModuleFileType.AADL_JAR;
		
		// create command
		List<String> cmdList = new ArrayList<String>();
		
		// java command
		CommandExecutor.appendJavaCommandPath(cmdList, AppSettings.getInstance().getCurrentJavaCommandPath());
		
		// Java VM arguments
		String params;
		//--- AADL csv encoding property
		//--- AADL csv encoding property
		params = AppSettings.getInstance().getAadlCsvEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-Daadl.csv.encoding=" + params);
		}
		//--- AADL txt encoding property
		params = AppSettings.getInstance().getAadlTxtEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-Daadl.txt.encoding=" + params);
		}
		//--- java VM arguments
		String maxMemorySize = ModuleRunner.getMaxMemorySize();
		String strVmArgs = AppSettings.getInstance().appendJavaVMoptions(this.getJavaVMArgs());
		if (!Strings.isNullOrEmpty(maxMemorySize)) {
			CommandExecutor.appendJavaMaxHeapSize(cmdList, maxMemorySize);
			if (!Strings.isNullOrEmpty(strVmArgs)) {
				//--- ignore -Xmx???? and -Xms???? option
				String[] vmArgs = Strings.splitCommandLineWithDoubleQuoteEscape(strVmArgs);
				for (String vma : vmArgs) {
					if (!vma.startsWith("-Xmx") && !vma.startsWith("-Xms")) {
						cmdList.add(vma);
					}
				}
			}
		} else {
			CommandExecutor.appendJavaVMArgs(cmdList, strVmArgs);
		}
		
		// Check user class-paths
		//File[] userClassPaths = datamodel.getSettings().getClassPathFiles();
		//for (File file : userClassPaths) {
		//	if (file == null)
		//		throw new IllegalArgumentException("User class path file is null.");
		//	if (!file.exists())
		//		throw new IllegalArgumentException("User class path file is not found : \"" + file.getPath() + "\"");
		//}
		
		// Check Exec libraries
		String[] execLibraries = AppSettings.getInstance().getExecLibraries();
		for (String path : execLibraries) {
			if (Strings.isNullOrEmpty(path))
				throw new IllegalStateException("Class path for execution in Editor environment is illegal value.");
			if (!(new File(path)).exists())
				throw new IllegalStateException("Class path for execution in Editor environment is not found : \"" + path + "\"");
		}
		
		//***********************************************
		// 現在実行可能なモジュールは、AADLモジュール(JAR)のみ
		//***********************************************
		
		// ClassPath
		ClassPathSet pathSet = new ClassPathSet();
		//pathSet.appendPaths(userClassPaths);
		pathSet.addPath(fModule);
		pathSet.appendPaths(execLibraries);
		CommandExecutor.appendClassPath(cmdList, pathSet);
		
		// Main class
		CommandExecutor.appendMainClassName(cmdList, this.getModuleMainClass());
		
		// Program arguments
		int numArgs = this.getArgumentCount();
		for (int i = 0; i < numArgs; i++) {
			Object value = this.getArgument(i).getValue();
			if (value instanceof DefaultFile) {
				cmdList.add(((DefaultFile)value).getAbsolutePath());
			}
			else if (value instanceof File) {
				cmdList.add(((File)value).getAbsolutePath());
			}
			else if (value != null) {
				cmdList.add(value.toString());
			}
			else {
				cmdList.add("");
			}
		}
		
		// create Executor
		CommandExecutor executor = new CommandExecutor(cmdList);
		
		// set working directory
		executor.setWorkDirectory(fModule.getParentFile());
		
		// completed
		return executor;
	}

	/**
	 * このオブジェクトの実行時引数から、AADLマクロを実行するコマンドを生成する。
	 * @param fModule	実行モジュールの位置を示す <code>java.io.File</code> オブジェクト
	 * @return	生成されたコマンド
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数の設定が正しくない場合
	 */
	protected CommandExecutor createAADLMacroExecutor(File fModule)
	{
		assert fModule != null;
		assert this.getModuleType() == ModuleFileType.AADL_MACRO;
		
		// create command
		List<String> cmdList = new ArrayList<String>();
		
		// java command
		CommandExecutor.appendJavaCommandPath(cmdList, AppSettings.getInstance().getCurrentJavaCommandPath());
		
		// Java VM arguments
		String params;
		//--- java VM arguments
		String maxMemorySize = ModuleRunner.getMaxMemorySize();
		String strVmArgs = AppSettings.getInstance().appendJavaVMoptions(this.getJavaVMArgs());
		if (!Strings.isNullOrEmpty(maxMemorySize)) {
			CommandExecutor.appendJavaMaxHeapSize(cmdList, maxMemorySize);
			if (!Strings.isNullOrEmpty(strVmArgs)) {
				//--- ignore -Xmx???? and -Xms???? option
				String[] vmArgs = Strings.splitCommandLineWithDoubleQuoteEscape(strVmArgs);
				for (String vma : vmArgs) {
					if (!vma.startsWith("-Xmx") && !vma.startsWith("-Xms")) {
						cmdList.add(vma);
					}
				}
			}
		} else {
			CommandExecutor.appendJavaVMArgs(cmdList, strVmArgs);
		}
		
		// Macro engine class-path
		ClassPathSet pathSet = new ClassPathSet();
		File macroClass = Classes.getClassSource(AADLMacroEngine.class);
		pathSet.addPath(macroClass.getAbsoluteFile());
		CommandExecutor.appendClassPath(cmdList, pathSet);
		
		// Macro main class
		CommandExecutor.appendMainClassName(cmdList, AADLMacroEngine.class.getName());
		
		// Macro options
		//--- debug
		if (ModuleRunner.isDebugEnabled()) {
			cmdList.add("-debug");
			cmdList.add("-verbose");
		}
		//--- encoding : AADL macro encoding property
		params = AppSettings.getInstance().getAadlMacroEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-macroencoding");
			cmdList.add(params);
		}
		//--- encoding : AADL csv encoding property
		params = AppSettings.getInstance().getAadlCsvEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-csvencoding");
			cmdList.add(params);
		}
		//--- encoding : AADL txt encoding property
		params = AppSettings.getInstance().getAadlTxtEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-txtencoding");
			cmdList.add(params);
		}
		//--- heapmax
		if (!Strings.isNullOrEmpty(maxMemorySize)) {
			cmdList.add("-heapmax");
			cmdList.add(maxMemorySize);
		}
		//--- Class path setting for AADL module
		String[] execLibraries = AppSettings.getInstance().getExecLibraries();
		if (execLibraries != null && execLibraries.length > 0) {
			StringBuilder sb = new StringBuilder(execLibraries[0]);
			for (int i = 1; i < execLibraries.length; i++) {
				sb.append(AADLMacroEngine.MACRO_CLASSPATH_SEPARATOR);
				sb.append(execLibraries[i]);
			}
			cmdList.add("-libpath");
			cmdList.add(sb.toString());
		}
		//--- VM options
		if (!Strings.isNullOrEmpty(strVmArgs)) {
			cmdList.add("-javavm");
			cmdList.add(strVmArgs);
		}
		
		// Target macro file
		cmdList.add(fModule.getAbsolutePath());
		
		// Module arguments
		int numArgs = this.getArgumentCount();
		for (int i = 0; i < numArgs; i++) {
			Object value = this.getArgument(i).getValue();
			if (value instanceof DefaultFile) {
				cmdList.add(((DefaultFile)value).getAbsolutePath());
			}
			else if (value instanceof File) {
				cmdList.add(((File)value).getAbsolutePath());
			}
			else if (value != null) {
				cmdList.add(value.toString());
			}
			else {
				cmdList.add("");
			}
		}
		
		// create Executor
		CommandExecutor executor = new CommandExecutor(cmdList);
		
		// set working directory
		executor.setWorkDirectory(fModule.getParentFile());
		
		// completed
		return executor;
	}

	/**
	 * このオブジェクトの引数定義から、中断可能なAADL実行モジュールを実行するコマンドを生成する。
	 * @param fModule	実行モジュールの位置を示す <code>java.io.File</code> オブジェクト
	 * @return	生成されたコマンド
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数の設定が正しくない場合
	 * @since 3.0.0
	 */
	protected InterruptibleCommandExecutor createInterruptibleAADLModuleExecutor(File fModule)
	{
		assert fModule != null;
		assert this.getModuleType()==ModuleFileType.AADL_JAR;
		
		// create command
		List<String> cmdList = new ArrayList<String>();
		
		// java command
		InterruptibleCommandExecutor.appendJavaCommandPath(cmdList, AppSettings.getInstance().getCurrentJavaCommandPath());
		
		// Java VM arguments
		String params;
		//--- AADL csv encoding property
		//--- AADL csv encoding property
		params = AppSettings.getInstance().getAadlCsvEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-Daadl.csv.encoding=" + params);
		}
		//--- AADL txt encoding property
		params = AppSettings.getInstance().getAadlTxtEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-Daadl.txt.encoding=" + params);
		}
		//--- java VM arguments
		String maxMemorySize = ModuleRunner.getMaxMemorySize();
		String strVmArgs = AppSettings.getInstance().appendJavaVMoptions(this.getJavaVMArgs());
		if (!Strings.isNullOrEmpty(maxMemorySize)) {
			InterruptibleCommandExecutor.appendJavaMaxHeapSize(cmdList, maxMemorySize);
			if (!Strings.isNullOrEmpty(strVmArgs)) {
				//--- ignore -Xmx???? and -Xms???? option
				String[] vmArgs = Strings.splitCommandLineWithDoubleQuoteEscape(strVmArgs);
				for (String vma : vmArgs) {
					if (!vma.startsWith("-Xmx") && !vma.startsWith("-Xms")) {
						cmdList.add(vma);
					}
				}
			}
		} else {
			InterruptibleCommandExecutor.appendJavaVMArgs(cmdList, strVmArgs);
		}
		
		// Check user class-paths
		//File[] userClassPaths = datamodel.getSettings().getClassPathFiles();
		//for (File file : userClassPaths) {
		//	if (file == null)
		//		throw new IllegalArgumentException("User class path file is null.");
		//	if (!file.exists())
		//		throw new IllegalArgumentException("User class path file is not found : \"" + file.getPath() + "\"");
		//}
		
		// Check Exec libraries
		String[] execLibraries = AppSettings.getInstance().getExecLibraries();
		for (String path : execLibraries) {
			if (Strings.isNullOrEmpty(path))
				throw new IllegalStateException("Class path for execution in Editor environment is illegal value.");
			if (!(new File(path)).exists())
				throw new IllegalStateException("Class path for execution in Editor environment is not found : \"" + path + "\"");
		}
		
		//***********************************************
		// 現在実行可能なモジュールは、AADLモジュール(JAR)のみ
		//***********************************************
		
		// ClassPath
		ClassPathSet pathSet = new ClassPathSet();
		//pathSet.appendPaths(userClassPaths);
		pathSet.addPath(fModule);
		pathSet.appendPaths(execLibraries);
		InterruptibleCommandExecutor.appendClassPath(cmdList, pathSet);
		
		// Main class
		InterruptibleCommandExecutor.appendMainClassName(cmdList, this.getModuleMainClass());
		
		// Program arguments
		int numArgs = this.getArgumentCount();
		for (int i = 0; i < numArgs; i++) {
			Object value = this.getArgument(i).getValue();
			if (value instanceof DefaultFile) {
				cmdList.add(((DefaultFile)value).getAbsolutePath());
			}
			else if (value instanceof File) {
				cmdList.add(((File)value).getAbsolutePath());
			}
			else if (value != null) {
				cmdList.add(value.toString());
			}
			else {
				cmdList.add("");
			}
		}
		
		// create Executor
		InterruptibleCommandExecutor executor = new InterruptibleCommandExecutor(cmdList);
		
		// set working directory
		executor.setWorkDirectory(fModule.getParentFile());
		
		// completed
		return executor;
	}

	/**
	 * このオブジェクトの実行時引数から、中断可能なAADLマクロを実行するコマンドを生成する。
	 * @param fModule	実行モジュールの位置を示す <code>java.io.File</code> オブジェクト
	 * @return	生成されたコマンド
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数の設定が正しくない場合
	 * @since 3.0.0
	 */
	protected InterruptibleCommandExecutor createInterruptibleAADLMacroExecutor(File fModule)
	{
		assert fModule != null;
		assert this.getModuleType() == ModuleFileType.AADL_MACRO;
		
		// create command
		List<String> cmdList = new ArrayList<String>();
		
		// java command
		InterruptibleCommandExecutor.appendJavaCommandPath(cmdList, AppSettings.getInstance().getCurrentJavaCommandPath());
		
		// Java VM arguments
		String params;
		//--- java VM arguments
		String maxMemorySize = ModuleRunner.getMaxMemorySize();
		String strVmArgs = AppSettings.getInstance().appendJavaVMoptions(this.getJavaVMArgs());
		if (!Strings.isNullOrEmpty(maxMemorySize)) {
			InterruptibleCommandExecutor.appendJavaMaxHeapSize(cmdList, maxMemorySize);
			if (!Strings.isNullOrEmpty(strVmArgs)) {
				//--- ignore -Xmx???? and -Xms???? option
				String[] vmArgs = Strings.splitCommandLineWithDoubleQuoteEscape(strVmArgs);
				for (String vma : vmArgs) {
					if (!vma.startsWith("-Xmx") && !vma.startsWith("-Xms")) {
						cmdList.add(vma);
					}
				}
			}
		} else {
			InterruptibleCommandExecutor.appendJavaVMArgs(cmdList, strVmArgs);
		}
		
		// Macro engine class-path
		ClassPathSet pathSet = new ClassPathSet();
		File macroClass = Classes.getClassSource(AADLMacroEngine.class);
		pathSet.addPath(macroClass.getAbsoluteFile());
		InterruptibleCommandExecutor.appendClassPath(cmdList, pathSet);
		
		// Macro main class
		InterruptibleCommandExecutor.appendMainClassName(cmdList, AADLMacroEngine.class.getName());
		
		// Macro options
		//--- debug
		if (ModuleRunner.isDebugEnabled()) {
			cmdList.add("-debug");
			cmdList.add("-verbose");
		}
		//--- encoding : AADL macro encoding property
		params = AppSettings.getInstance().getAadlMacroEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-macroencoding");
			cmdList.add(params);
		}
		//--- encoding : AADL csv encoding property
		params = AppSettings.getInstance().getAadlCsvEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-csvencoding");
			cmdList.add(params);
		}
		//--- encoding : AADL txt encoding property
		params = AppSettings.getInstance().getAadlTxtEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-txtencoding");
			cmdList.add(params);
		}
		//--- heapmax
		if (!Strings.isNullOrEmpty(maxMemorySize)) {
			cmdList.add("-heapmax");
			cmdList.add(maxMemorySize);
		}
		//--- Class path setting for AADL module
		String[] execLibraries = AppSettings.getInstance().getExecLibraries();
		if (execLibraries != null && execLibraries.length > 0) {
			StringBuilder sb = new StringBuilder(execLibraries[0]);
			for (int i = 1; i < execLibraries.length; i++) {
				sb.append(AADLMacroEngine.MACRO_CLASSPATH_SEPARATOR);
				sb.append(execLibraries[i]);
			}
			cmdList.add("-libpath");
			cmdList.add(sb.toString());
		}
		//--- VM options
		if (!Strings.isNullOrEmpty(strVmArgs)) {
			cmdList.add("-javavm");
			cmdList.add(strVmArgs);
		}
		
		// Target macro file
		cmdList.add(fModule.getAbsolutePath());
		
		// Module arguments
		int numArgs = this.getArgumentCount();
		for (int i = 0; i < numArgs; i++) {
			Object value = this.getArgument(i).getValue();
			if (value instanceof DefaultFile) {
				cmdList.add(((DefaultFile)value).getAbsolutePath());
			}
			else if (value instanceof File) {
				cmdList.add(((File)value).getAbsolutePath());
			}
			else if (value != null) {
				cmdList.add(value.toString());
			}
			else {
				cmdList.add("");
			}
		}
		
		// create Executor
		InterruptibleCommandExecutor executor = new InterruptibleCommandExecutor(cmdList);
		
		// set working directory
		executor.setWorkDirectory(fModule.getParentFile());
		
		// completed
		return executor;
	}

	/**
	 * このオブジェクトの実行時引数から、中断可能な汎用フィルタを実行するコマンドを生成する。
	 * @param fModule	実行モジュールの位置を示す <code>java.io.File</code> オブジェクト
	 * @return	生成されたコマンド
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数の設定が正しくない場合
	 * @since 3.2.0
	 */
	protected InterruptibleCommandExecutor createInterruptibleGenericFilterExecutor(File fModule)
	{
		assert fModule != null;
		assert this.getModuleType() == ModuleFileType.GENERIC_FILTER;
		
		// create command
		List<String> cmdList = new ArrayList<String>();
		
		// java command
		InterruptibleCommandExecutor.appendJavaCommandPath(cmdList, AppSettings.getInstance().getCurrentJavaCommandPath());
		
		// Java VM arguments
		String params;
		//--- AADL csv encoding property
		params = AppSettings.getInstance().getAadlCsvEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-Daadl.csv.encoding=" + params);
		}
		//--- AADL txt encoding property
		params = AppSettings.getInstance().getAadlTxtEncodingName();
		if (!Strings.isNullOrEmpty(params)) {
			cmdList.add("-Daadl.txt.encoding=" + params);
		}
		//--- java VM arguments
		String maxMemorySize = ModuleRunner.getMaxMemorySize();
		String strVmArgs = AppSettings.getInstance().appendJavaVMoptions(this.getJavaVMArgs());
		if (!Strings.isNullOrEmpty(maxMemorySize)) {
			InterruptibleCommandExecutor.appendJavaMaxHeapSize(cmdList, maxMemorySize);
			if (!Strings.isNullOrEmpty(strVmArgs)) {
				//--- ignore -Xmx???? and -Xms???? option
				String[] vmArgs = Strings.splitCommandLineWithDoubleQuoteEscape(strVmArgs);
				for (String vma : vmArgs) {
					if (!vma.startsWith("-Xmx") && !vma.startsWith("-Xms")) {
						cmdList.add(vma);
					}
				}
			}
		} else {
			InterruptibleCommandExecutor.appendJavaVMArgs(cmdList, strVmArgs);
		}
		
		// Check user class-paths
		//File[] userClassPaths = datamodel.getSettings().getClassPathFiles();
		//for (File file : userClassPaths) {
		//	if (file == null)
		//		throw new IllegalArgumentException("User class path file is null.");
		//	if (!file.exists())
		//		throw new IllegalArgumentException("User class path file is not found : \"" + file.getPath() + "\"");
		//}
		
		// Check Exec libraries
		String[] execLibraries = AppSettings.getInstance().getExecLibraries();
		for (String path : execLibraries) {
			if (Strings.isNullOrEmpty(path))
				throw new IllegalStateException("Class path for execution in Editor environment is illegal value.");
			if (!(new File(path)).exists())
				throw new IllegalStateException("Class path for execution in Editor environment is not found : \"" + path + "\"");
		}
		
		//***********************************************
		// 現在実行可能なモジュールは、汎用フィルタモジュールのみ
		//***********************************************
		
		// Generic filter engine class-path
		ClassPathSet pathSet = new ClassPathSet();
		//--- Generic filter engine class
		File genericEngineClass = Classes.getClassSource(GenericFilterMain.class);
		pathSet.addPath(genericEngineClass.getAbsoluteFile());
		//--- Schema configuration classes
		File schemaLibClass = Classes.getClassSource(SchemaConfig.class);
		pathSet.addPath(schemaLibClass.getAbsoluteFile());
		//--- User & system libraries
		//pathSet.appendPaths(userClassPaths);
		pathSet.appendPaths(execLibraries);
		InterruptibleCommandExecutor.appendClassPath(cmdList, pathSet);
		
		// Generic filter main class
		InterruptibleCommandExecutor.appendMainClassName(cmdList, GenericFilterMain.class.getName());
		
		// Target shema file
		cmdList.add(fModule.getAbsolutePath());
		
		// Module arguments
		int numArgs = this.getArgumentCount();
		for (int i = 0; i < numArgs; i++) {
			Object value = this.getArgument(i).getValue();
			if (value instanceof DefaultFile) {
				cmdList.add(((DefaultFile)value).getAbsolutePath());
			}
			else if (value instanceof File) {
				cmdList.add(((File)value).getAbsolutePath());
			}
			else if (value != null) {
				cmdList.add(value.toString());
			}
			else {
				cmdList.add("");
			}
		}
		
		// create Executor
		InterruptibleCommandExecutor executor = new InterruptibleCommandExecutor(cmdList);
		
		// set working directory
		executor.setWorkDirectory(fModule.getParentFile());
		
		// completed
		return executor;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
