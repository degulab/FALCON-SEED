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
 * @(#)MExecDefSubFilters.java	3.1.3	2015/05/22 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefSubFilters.java	3.1.0	2014/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefSubFilters.java	2.0.0	2012/11/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.setting;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;

import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.setting.AbstractSettings;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.MacroFilterDefArgList;
import ssac.falconseed.module.MacroSubModuleRuntimeData;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.ModuleDataList;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgParamManager;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.swing.FilterArgEditModel;
import ssac.falconseed.module.swing.FilterArgReferValueEditModel;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.Strings;
import ssac.util.io.DefaultFile;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileManager;
import ssac.util.logging.AppLogger;

/**
 * マクロフィルタ定義情報のプロパティ
 * 
 * @version 3.1.3	2015/05/22
 * @since 2.0.0
 */
public class MExecDefMacroFilter extends AbstractSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final Integer[] EMPTY_SUBFILTER_WAITNUMBERS = new Integer[0];

	/** マクロフィルタ定義情報のセクション名 **/
	static protected final String SECTION = "MExecDef.MacroFilter";
	/** マクロフィルタ定義情報のキー名：フィルタ定義引数グループ **/
	static protected final String KEY_MEXECDEFARGS_PREFIX	= SECTION + ".mexecdef.args";
	/** マクロフィルタ定義情報のキー名：フィルタ定義引数総数 **/
	static protected final String KEY_MEXECDEFARGS_NUM		= KEY_MEXECDEFARGS_PREFIX + ".num";
	/** マクロフィルタ定義情報のキー名：フィルタ定義引数のレコード **/
	static protected final String KEY_MEXECDEFARGS_REC		= KEY_MEXECDEFARGS_PREFIX + ".record";
	/** マクロフィルタ定義情報のキー名：サブフィルタ定義グループ **/
	static protected final String KEY_SUBFILTER_PREFIX		= SECTION + ".subfilter";
	/** マクロフィルタ定義情報のキー名：サブフィルタ数 **/
	static protected final String KEY_SUBFILTER_NUM			= KEY_SUBFILTER_PREFIX + ".num";
	/** 定義情報を示すサフィックス **/
	static protected final String SUFFIX_SUBFILTER_PREFS	= ".prefs";
	/** ソースとなる定義情報を示すサフィックス **/
	static protected final String SUFFIX_SUBFILTER_SRCPREFS	= ".sourcePrefs";
	/** 引数情報を示すサフィックス **/
	static protected final String SUFFIX_SUBFILTER_ARGS		= ".args";
	/** サブフィルタの引数総数を示すサフィックス **/
	static protected final String SUFFIX_SUBFILTER_ARGS_NUM	= SUFFIX_SUBFILTER_ARGS + ".num";
	/** サブフィルタの引数レコードを示すサフィックス **/
	static protected final String SUFFIX_SUBFILTER_ARGS_REC	= SUFFIX_SUBFILTER_ARGS + ".record";
	/**
	 * サブフィルタのコメントを示すサフィックス
	 * @since 3.1.0
	 */
	static protected final String SUFFIX_SUBFILTER_COMMENT	= ".comment";
	/**
	 * サブフィルタの引数参照付加情報を示すサフィックス
	 * @since 3.1.0
	 */
	static protected final String SUFFIX_SUBFILTER_REFARG	= ".refarg";
	/**
	 * サブフィルタの引数参照の前に付加する文字列情報を示すサフィックス
	 * @since 3.1.0
	 */
	static protected final String SUFFIX_SUBFILTER_REFARG_PREFIX	= SUFFIX_SUBFILTER_REFARG + ".prefix";
	/**
	 * サブフィルタの引数参照の後に付加する文字列情報を示すサフィックス
	 */
	static protected final String SUFFIX_SUBFILTER_REFARG_SUFFIX	= SUFFIX_SUBFILTER_REFARG + ".suffix";
	/**
	 * サブフィルタのリンク情報を示すサフィックス
	 * @since 3.1.0
	 */
	static protected final String SUFFIX_SUBFILTER_LINK			= ".link";
	/**
	 * サブフィルタのリンク情報の有効設定を示すサフィックス。
	 * このサフィックスによって生成されたプロパティキーの値が <tt>true</tt> の場合のみ、リンク情報が有効であることを示す。
	 * @since 3.1.0
	 */
	static protected final String SUFFIX_SUBFILTER_LINK_ENABLED	= SUFFIX_SUBFILTER_LINK + ".enabled";
	/**
	 * サブフィルタの待機するフィルタ番号を示すサフィックス
	 * @since 3.1.0
	 */
	static protected final String SUFFIX_SUBFILTER_LINK_WAIT	= SUFFIX_SUBFILTER_LINK + ".wait";

	/** 引数値が指定されたファイルパスであることを示す値 **/
	static protected final String ARGVAL_TYPE_FILE = "file";
	/** 引数値が指定された文字列であることを示す値 **/
	static protected final String ARGVAL_TYPE_STRING = "string";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** モジュール実行定義情報の格納ディレクトリ **/
	private VirtualFile	_vfTargetDir;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefMacroFilter() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	//
	//	Name
	//
	
	/**
	 * モジュール実行定義名が設定されている場合に <tt>true</tt> を返す。
	 * 有効な文字列が設定されていない場合は <tt>false</tt> を返す。
	 */
	public boolean isSpecifiedName() {
		return (getName() != null);
	}
	
	/**
	 * モジュール実行定義名を返す。
	 * 有効な文字列が設定されていない場合は <tt>null</tt> を返す。
	 * @return	名前
	 */
	public String getName() {
		if (_vfTargetDir != null) {
			return _vfTargetDir.getName();
		} else {
			return null;
		}
	}
	
	//
	//	Module Execution Definition Directory
	//

	/**
	 * このモジュール実行定義が格納されているディレクトリが定義されているかを判定する。
	 * @return	格納場所が定義されている場合は <tt>true</tt>、未定義の場合は <tt>false</tt>
	 */
	public boolean hasExecDefDirectory() {
		return _vfTargetDir != null;
	}

	/**
	 * このモジュール実行定義が格納されているディレクトリの抽象パスを返す。
	 * 格納場所が未定義の場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getExecDefDirectory() {
		return _vfTargetDir;
	}

	/**
	 * 現在のプロパティから、マクロフィルタ定義データを取得する。
	 * このメソッドでは、引数に指定されたオブジェクトの内容はクリアしない。
	 * そのため、このメソッドを呼び出す前にオブジェクトの内容はクリアしておくこと。
	 * なお、読み込みデータの整合性に問題がある場合、このメソッドが返す {@link DefMacroFilterErrorStates} にエラー情報が格納される。
	 * @param defArgList		プロパティのフィルタ定義引数情報を受け取るオブジェクト
	 * @param subFilterList		プロパティのサブフィルタ定義情報を受け取るオブジェクト
	 * @return	正常に取得できた場合は <tt>null</tt>、それ以外の場合はデータ不正情報を格納する {@link DefMacroFilterInvalidStates} オブジェクト
	 * @throws IllegalMacroFilterPrefsException	解読不能の不正なデータが含まれている場合（詳細メッセージ含む）
	 */
	public DefMacroFilterInvalidStates getMacroFilterData(final MacroFilterDefArgList defArgList, final ModuleDataList<MacroSubModuleRuntimeData> subFilterList) {
		DefMacroFilterInvalidStates invalidStates = new DefMacroFilterInvalidStates();	// added : ver.3.1.3
		
		// 実行時引数リストの取得
		getFilterDefArgsList(invalidStates, _vfTargetDir, defArgList);
		
		// サブモジュールリストの取得
		int numModules = props.getIntegerValue(KEY_SUBFILTER_NUM, 0);
		for (int index = 0; index < numModules; index++) {
			String key = getSubFilterKey(index);
			//--- サブモジュールの取得（実行番号もインデックスから付加される）
			MacroSubModuleRuntimeData moduledata = getSubModuleData(invalidStates, _vfTargetDir, index, key, defArgList, subFilterList);
			//--- 実行番号更新
			//moduledata.setRunNo(index+1);
			//--- サブフィルタ登録
			subFilterList.add(moduledata);
		}
		
		// サブモジュールの実行順序情報を取得
		getSubModuleLink(invalidStates, subFilterList);

		// 終了
		if (invalidStates.hasError()) {
			// 不正データあり
			return invalidStates;
		} else {
			// 正常
			return null;
		}
	}

	/**
	 * 引数に指定された定義データを、このオブジェクトのプロパティに格納する。
	 * @param defArgList		フィルタ定義引数情報
	 * @param subFilterList		サブフィルタ定義情報
	 */
	public void setMacroFilterData(final MacroFilterDefArgList defArgList, final ModuleDataList<MacroSubModuleRuntimeData> subFilterList) {
		// 実行時引数リストの保存
		setFilterDefArgsList(_vfTargetDir, defArgList);
		
		// サブモジュールリストの保存
		int numModules = subFilterList.size();
		props.setIntegerValue(KEY_SUBFILTER_NUM, numModules);
		for (int index = 0; index < numModules; index++) {
			String key = getSubFilterKey(index);
			setSubModuleData(_vfTargetDir, key, subFilterList.get(index));
		}
		
		// サブモジュールの実行順序リンクの保存
		setSubModuleLink(subFilterList);
	}

	//------------------------------------------------------------
	// Implement AbstractSettings interfaces
	//------------------------------------------------------------

	@Override
	public void commit() throws IOException {
		// プロパティの保存
		super.commit();
	}

	@Override
	public void rollback() {
		// プロパティの読み込み
		super.rollback();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * すべてのプロパティをクリアする。
	 */
	protected void clearProperties() {
		props.clearPropertiesByKeyPrefix(SECTION);
	}

	/**
	 * このオブジェクトの保存先ファイルを、指定の抽象パスに更新する。
	 * このメソッドでは、指定された抽象パスの拡張子を変更しない。
	 * 指定された抽象パスと設定済みの抽象パスが等しい場合、このメソッドは何もしない。
	 * @param targetFile	設定する抽象パス
	 * @return	抽象パスが更新された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	@Override
	protected boolean updateSettingFile(File targetFile) {
		return updateSettingFile(new DefaultFile(targetFile));
	}

	/**
	 * このオブジェクトの保存先ファイルを、指定の抽象パスに更新する。
	 * このメソッドでは、指定された抽象パスの拡張子を変更しない。
	 * 指定された抽象パスと設定済みの抽象パスが等しい場合、このメソッドは何もしない。
	 * @param targetFile	設定する抽象パス
	 * @return	抽象パスが更新された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	@Override
	protected boolean updateSettingFile(VirtualFile targetFile) {
		targetFile = targetFile.getAbsoluteFile();
		if (!targetFile.equals(getVirtualPropertyFile())) {
			setSettingFile(targetFile);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 指定された抽象パスを、このオブジェクトの保存先ファイルに設定し、
	 * プロパティファイルの更新日時をリセット(0 に設定)する。
	 * @param targetFile	設定する抽象パス
	 */
	@Override
	protected void setSettingFile(VirtualFile targetFile) {
		super.setSettingFile(targetFile);
		this._vfTargetDir = targetFile.getParentFile();
	}

	/**
	 * このマクロフィルタそのものの引数定義を、プロパティから取得する。
	 * 正常に取得できないパラメータが存在する場合、<em>invalidStates</em> に情報が追加される。
	 * @param invalidStates	不整合エラー情報を保持するオブジェクト
	 * @param vfBase		相対パス解決のための基準パス
	 * @param defArgList	マクロフィルタの定義引数を受け取る、引数リスト
	 * @throws IllegalMacroFilterPrefsException	解読不能の不正なデータが含まれている場合（詳細メッセージ含む）
	 */
	protected void getFilterDefArgsList(DefMacroFilterInvalidStates invalidStates, final VirtualFile vfBase, final MacroFilterDefArgList defArgList) {
		// 実行時引数リストの総数取得
		int numArgs = props.getIntegerValue(KEY_MEXECDEFARGS_NUM, 0);

		// 実行時引数リストの取得
		for (int argidx = 0; argidx < numArgs; argidx++) {
			String key = getFilterDefArgsRecordKey(argidx);
			// プロパティから値を取得
			String[] argentry = new String[5];
			{
				String[] sval = props.getStringArray(key, null);
				if (sval != null && sval.length > 0) {
					System.arraycopy(sval, 0, argentry, 0, Math.min(sval.length, argentry.length));
				}
			}
			//--- type
			ModuleArgType type = null;
			if (!Strings.isNullOrEmpty(argentry[0])) {
				type = ModuleArgType.fromName(argentry[0]);
				if (type == null) {
					//--- illegal data
					String errmsg = RunnerMessages.getInstance().msgMacroFilterDefInvalidArgType;
					AppLogger.warn(getIllegalPropertyMessage(key, errmsg + " : \"" + argentry[0] + "\""));
					invalidStates.addDefArgState(MacroFilterDefArgInvalidState.WARN_InvalidArgType, argidx+1, errmsg);
				}
			} else {
				String errmsg = RunnerMessages.getInstance().msgMacroFilterDefNoArgType;
				AppLogger.warn(getIllegalPropertyMessage(key, errmsg + " : \"" + argentry[0] + "\""));
				invalidStates.addDefArgState(MacroFilterDefArgInvalidState.WARN_InvalidArgType, argidx+1, errmsg);
			}
			//--- description
			String desc = argentry[1];
			//--- parameter type
			IMExecArgParam param = MExecArgParamManager.fromIdentifier(argentry[2]);
			//--- value
			Object value = null;
			if (!Strings.isNullOrEmpty(argentry[3])) {
				if (ARGVAL_TYPE_FILE.equalsIgnoreCase(argentry[3])) {
					//--- VirtualFile path
					if (!Strings.isNullOrEmpty(argentry[4])) {
						try {
							value = convertPropertyToVirtualFile(key, vfBase, argentry[4]);
						}
						catch (Throwable ex) {
							String errmsg = RunnerMessages.getInstance().msgMacroFilterDefInvalidArgFile;
							AppLogger.warn(getIllegalPropertyMessage(key, errmsg + " : \"" + argentry[4] + "\""), ex);
							invalidStates.addDefArgState(MacroFilterDefArgInvalidState.WARN_ValueNotFile, argidx+1, errmsg);
						}
					}
				}
				else if (ARGVAL_TYPE_STRING.equalsIgnoreCase(argentry[3])) {
					//--- String
					value = argentry[4];
				}
				else {
					value = MExecArgParamManager.fromIdentifier(argentry[3]);
					if (value == null) {
						String errmsg = RunnerMessages.getInstance().msgMacroFilterDefInvalidArgFile;
						AppLogger.warn(getIllegalPropertyMessage(key, errmsg + " : \"" + argentry[3] + "\""));
						invalidStates.addDefArgState(MacroFilterDefArgInvalidState.WARN_InvalidValueType, argidx+1, errmsg);
					}
				}
			}
			
			// 引数オブジェクトの生成
			FilterArgEditModel argdata = new FilterArgEditModel(argidx+1, type, desc, param, value);
			defArgList.add(argdata);
		}
	}

	/**
	 * このマクロフィルタそのものの引数定義を、プロパティに設定する。
	 * @param vfBase		相対パス変換のための基準パス
	 * @param defArgList	マクロフィルタの定義引数リスト
	 */
	protected void setFilterDefArgsList(final VirtualFile vfBase, final MacroFilterDefArgList defArgList) {
		// 実行時引数リストの総数保存
		int numArgs = defArgList.size();
		props.setIntegerValue(KEY_MEXECDEFARGS_NUM, numArgs);
		
		// 実行時引数リストの保存
		for (int argidx = 0; argidx < numArgs; argidx++) {
			String key = getFilterDefArgsRecordKey(argidx);
			IModuleArgConfig argdef = defArgList.get(argidx);
			ModuleArgType argtype = argdef.getType();
			IMExecArgParam argparam = argdef.getParameterType();
			Object argval = argdef.getValue();
			String argdesc = argdef.getDescription();
			String[] argentry = new String[5];
			//--- type
			if (argtype != null) {
				argentry[0] = argtype.toString();
			}
			//--- description
			if (argdesc != null) {
				argentry[1] = argdesc;
			}
			//--- parameter type
			if (argparam != null) {
				argentry[2] = MExecArgParamManager.getIdentifier(argparam);
			}
			//--- value
			if (argval instanceof IMExecArgParam) {
				// parameter
				argentry[3] = MExecArgParamManager.getIdentifier((IMExecArgParam)argval);
				argentry[4] = null;
			}
			else if (argval instanceof VirtualFile) {
				argentry[3] = ARGVAL_TYPE_FILE;
				argentry[4] = convertFileToProperty(vfBase, (VirtualFile)argval);
			}
			else if (argval != null) {
				argentry[3] = ARGVAL_TYPE_STRING;
				argentry[4] = argval.toString();
			}
			props.setStringArray(key, argentry);
		}
	}

	/**
	 * 指定されたレコードキーに対応するサブフィルタの定義情報から、新しいサブモジュールデータオブジェクトを生成する。
	 * 正常に取得できないパラメータが存在する場合、<em>invalidStates</em> に情報が追加される。
	 * @param invalidStates	不整合エラー情報を保持するオブジェクト
	 * @param vfBase		相対パス解決のための基準パス
	 * @param subFilterIndex	サブフィルターインデックス
	 * @param key			サブフィルタのレコードを識別するためのプロパティキー
	 * @param defArgList	このマクロフィルタの引数定義リスト
	 * @param moduleList	これまでに取得できたサブフィルタのモジュールデータのリスト
	 * @return	取得したサブモジュールデータオブジェクト
	 * @throws IllegalMacroFilterPrefsException	解読不能の不正なデータが含まれている場合（詳細メッセージ含む）
	 */
	protected MacroSubModuleRuntimeData getSubModuleData(DefMacroFilterInvalidStates invalidStates, VirtualFile vfBase,
														int subFilterIndex, String key,
														final MacroFilterDefArgList defArgList,
														final ModuleDataList<MacroSubModuleRuntimeData> moduleList)
	{
		// 実行番号の生成
		long targetRunNo = subFilterIndex + 1;
		
		// モジュール実行定義の読み込み
		String keyPath = key + SUFFIX_SUBFILTER_PREFS;
		String path = props.getString(keyPath, null);
		MacroSubModuleRuntimeData moduledata;
		if (!Strings.isNullOrEmpty(path)) {
			try {
				VirtualFile vfPrefs = convertPropertyToVirtualFile(keyPath, vfBase, path);
				if (!vfPrefs.exists())
					throw new IllegalStateException("MExecDef prefs file is nothing!");
				if (!vfPrefs.isFile())
					throw new IllegalStateException("MExecDef prefs file is not file!");
				MExecDefSettings settings = new MExecDefSettings();
				settings.loadForTarget(vfPrefs);
				//--- インスタンス生成
				moduledata = new MacroSubModuleRuntimeData(settings);
				//--- 実行番号登録
				moduledata.setRunNo(targetRunNo);
			}
			catch (Throwable ex) {
				String errmsg = RunnerMessages.getInstance().msgMacroFilterDefFailedToEdit
						+ "\n" + RunnerMessages.getInstance().msgMacroSubFilterNotFound
						+ " : [" + targetRunNo + "] \"" + path + "\"";
				throw new IllegalMacroFilterPrefsException(getIllegalPropertyMessage(keyPath, errmsg), ex);
			}
		}
		else {
			String errmsg = RunnerMessages.getInstance().msgMacroFilterDefFailedToEdit
							+ "\n" + RunnerMessages.getInstance().msgMacroSubFilterNotFound + " : [" + targetRunNo + "]";
			throw new IllegalMacroFilterPrefsException(getIllegalPropertyMessage(keyPath, errmsg));
		}
		
		// モジュール関連情報の読み込み
		String keySourcePath = key + SUFFIX_SUBFILTER_SRCPREFS;
		String srcPath = props.getString(keySourcePath, null);
		if (!Strings.isNullOrEmpty(srcPath)) {
			//--- オリジナルのパスは絶対パス
			try {
				VirtualFile vfPrefs = convertPropertyToVirtualFile(keySourcePath, null, srcPath);
				//--- ファイルの有無は、現時点ではチェックしない
				moduledata.getEditData().setOriginalMExecDefDir(vfPrefs);
			}
			catch (Throwable ignoreEx) {
				//--- 例外は無視
				AppLogger.warn(getIllegalPropertyMessage(keySourcePath, "Illegal source MExecDef prefs file path : \"" + path + "\""), ignoreEx);
			}
		}
		
		// モジュール固有のコメントの読み込み
		String strComment = props.getString(key + SUFFIX_SUBFILTER_COMMENT, null);
		moduledata.setComment(strComment);
		
		// 引数値の取得
		//--- 引数の総数取得
		String keyNumArgs = key + SUFFIX_SUBFILTER_ARGS_NUM;
		int numArgs = props.getIntegerValue(keyNumArgs, 0);
		if (numArgs != moduledata.getArgumentCount()) {
			String errmsg = RunnerMessages.getInstance().msgMacroFilterDefFailedToEdit
					+ "\n" + RunnerMessages.getInstance().msgMacroSubFilterArgCountNotEquals
					+ " : [" + targetRunNo + ":" + moduledata.getName() + "]"
					+ " filter=" + moduledata.getArgumentCount() + ", property=" + numArgs;
			AppLogger.warn(getIllegalPropertyMessage(keyNumArgs, errmsg));
			invalidStates.addSubFilterState(SubFilterInvalidState.WARN_ArgCountNotEquals, targetRunNo, moduledata, errmsg);
			numArgs = Math.min(numArgs, moduledata.getArgumentCount());
		}
		//--- 引数値の取得
		for (int argidx = 0; argidx < numArgs; argidx++) {
			String argkey = key + getSubFilterArgsRecordSuffix(argidx);
			getSubModuleArgValue(invalidStates, targetRunNo, moduledata, vfBase, argkey, argidx, defArgList, moduleList);
		}
		
		return moduledata;
	}

	/**
	 * 指定されたサブフィルタ情報を、指定されたプロパティキーで、プロパティに保存する。
	 * @param vfBase	相対パス変換のための基準パス
	 * @param key		保存先を示すプロパティキー
	 * @param data		保存するサブフィルタのモジュールデータ
	 */
	protected void setSubModuleData(VirtualFile vfBase, String key, MacroSubModuleRuntimeData data) {
		// モジュール実行定義ファイルパスの保存
		props.setString(key + SUFFIX_SUBFILTER_PREFS, convertFileToProperty(vfBase, data.getExecDefPrefsFile()));
		
		// モジュール関連情報の保存
		//--- ソースとなるモジュール実行定義設定ファイルの場所
		VirtualFile vfSrcPrefs = VirtualFileManager.toVirtualFile(data.getEditData().getOriginalMExecDefDir());
		if (vfSrcPrefs != null) {
			props.setString(key + SUFFIX_SUBFILTER_SRCPREFS, convertFileToProperty(null, vfSrcPrefs));
		} else {
			props.clearProperty(key + SUFFIX_SUBFILTER_SRCPREFS);
		}
		
		// モジュール固有のコメントの保存
		String strComment = data.getComment();
		if (!Strings.isNullOrEmpty(strComment)) {
			// コメントあり
			props.setString(key + SUFFIX_SUBFILTER_COMMENT, strComment);
		} else {
			// コメントなし
			props.clearProperty(key + SUFFIX_SUBFILTER_COMMENT);
		}
		
		// 引数値の保存
		props.setIntegerValue(key + SUFFIX_SUBFILTER_ARGS_NUM, data.getArgumentCount());
		for (int argidx = 0; argidx < data.getArgumentCount(); argidx++) {
			String argkey = key + getSubFilterArgsRecordSuffix(argidx);
			setSubModuleArgValue(vfBase, argkey, data.getArgument(argidx));
		}
		
		// 待機モジュール情報の保存
	}

	/**
	 * 指定されたレコードキーに対応するサブフィルタの定義情報から、引数設定を取得する。
	 * 正常に取得できないパラメータが存在する場合、<em>invalidStates</em> に情報が追加される。
	 * @param invalidStates	不整合エラー情報を保持するオブジェクト
	 * @param targetRunNo	サブモジュールの現在の実行番号
	 * @param subModuleData	サブモジュールデータ
	 * @param vfBase		相対パス解決のための基準パス
	 * @param key			サブフィルタの引数定義レコードを識別するためのプロパティキー
	 * @param argIndex		対象引数のインデックス
	 * @param defArgList	このマクロフィルタの引数定義リスト
	 * @param moduleList	これまでに取得できたサブフィルタのモジュールデータのリスト
	 * @throws IllegalMacroFilterPrefsException	解読不能の不正なデータが含まれている場合（詳細メッセージ含む）
	 */
	protected void getSubModuleArgValue(DefMacroFilterInvalidStates invalidStates, long targetRunNo, MacroSubModuleRuntimeData subModuleData,
											VirtualFile vfBase, String key, int argIndex,
											final MacroFilterDefArgList defArgList, final ModuleDataList<MacroSubModuleRuntimeData> moduleList)
	{
		// 引数を取得
		ModuleArgConfig arg = subModuleData.getArgument(argIndex);
		int targetArgNo = argIndex + 1;
		
		// プロパティから値を取得
		String[] argentry = new String[3];
		{
			String[] sval = props.getStringArray(key, null);
			if (sval != null && sval.length > 0) {
				System.arraycopy(sval, 0, argentry, 0, Math.min(sval.length, argentry.length));
			}
		}
		
		// プロパティ値を変換
		//--- type
		if (argentry[0] != null) {
			ModuleArgType type = ModuleArgType.fromName(argentry[0]);
			if (arg.getType() != type) {
				//--- illegal data
				AppLogger.warn(getIllegalPropertyMessage(key, "Argument type is not correct : definition[($%d)%s]", arg.getArgNo(), String.valueOf(arg.getType())));
				//--- 無視
			}
		}
		//--- value
		if (!Strings.isNullOrEmpty(argentry[1])) {
			if (FilterArgEditModel.class.getName().equals(argentry[1])) {
				//--- argument link
				try {
					FilterArgEditModel argValue = convertPropertyToArgLink(argentry[2], defArgList);
					arg.setValue(argValue);
					//--- prefix & suffix
					String strPrefix = props.getString(key + SUFFIX_SUBFILTER_REFARG_PREFIX, null);
					String strSuffix = props.getString(key + SUFFIX_SUBFILTER_REFARG_SUFFIX, null);
					if (!Strings.isNullOrEmpty(strPrefix) || !Strings.isNullOrEmpty(strSuffix)) {
						// 参照引数のprefix,suffixのどちらかが登録されていれば、その付加情報を引数値として取得
						FilterArgReferValueEditModel refValue = new FilterArgReferValueEditModel(argValue, strPrefix, strSuffix);
						arg.setValue(refValue);
					}
				}
				catch (Throwable ex) {
					String errmsg = RunnerMessages.getInstance().msgMacroSubFilterArgInvalidDefRefer + " : Refer to [" + String.valueOf(argentry[2]) + "]";
					AppLogger.warn(getIllegalPropertyMessage(key, errmsg), ex);
					invalidStates.addSubFilterState(SubFilterInvalidState.WARN_IllegalDefArgRefer, targetRunNo, subModuleData, targetArgNo, errmsg);
				}
			}
			else if (ModuleArgID.class.getName().equals(argentry[1])) {
				//--- ModuleArgID
				try {
					arg.setValue(convertPropertyToModuleArgId(argentry[2], moduleList));
				}
				catch (Throwable ex) {
					String errmsg = RunnerMessages.getInstance().msgMacroSubFilterArgReferNotFound + " : Refer to [" + String.valueOf(argentry[2]) + "]";
					AppLogger.warn(getIllegalPropertyMessage(key, errmsg), ex);
					invalidStates.addSubFilterState(SubFilterInvalidState.WARN_IllegalModuleArgId, targetRunNo, subModuleData, targetArgNo, errmsg);
				}
			}
			else if (ARGVAL_TYPE_FILE.equalsIgnoreCase(argentry[1])) {
				//--- VirtualFile path
				if (!Strings.isNullOrEmpty(argentry[2])) {
					try {
						arg.setValue(convertPropertyToVirtualFile(key, vfBase, argentry[2]));
					}
					catch (Throwable ex) {
						String errmsg = RunnerMessages.getInstance().msgMacroSubFilterArgInvalidFileValue + " : \"" + String.valueOf(argentry[2]) + "\"";
						AppLogger.warn(getIllegalPropertyMessage(key, errmsg), ex);
						invalidStates.addSubFilterState(SubFilterInvalidState.WARN_InvaludArgFileValue, targetRunNo, subModuleData, targetArgNo, errmsg);
					}
				}
				else {
					//--- no data
					AppLogger.warn(getIllegalPropertyMessage(key, "File value is nothing!"));
				}
			}
			else if (ARGVAL_TYPE_STRING.equalsIgnoreCase(argentry[1])) {
				//--- String
				if (!Strings.isNullOrEmpty(argentry[2])) {
					arg.setValue(argentry[2]);
				} else {
					//--- no data
					AppLogger.warn(getIllegalPropertyMessage(key, "String value is nothing!"));
				}
			}
			else {
				IMExecArgParam val = MExecArgParamManager.fromIdentifier(argentry[1]);
				if (val instanceof MExecArgTempFile) {
					//--- temporary file
					arg.setValue(val);
					arg.setOutToTempEnabled(true);
				}
				else if (val != null) {
					//--- parameter
					arg.setValue(val);
				}
				else {
					AppLogger.warn(getIllegalPropertyMessage(key, "Illegal value type : \"" + argentry[2] + "\""));
				}
			}
		}
		else {
			//--- illegal data
			AppLogger.warn(getIllegalPropertyMessage(key, "Argument value is nothing!"));
		}
	}
	
	/**
	 * 指定された引数設定を、指定されたプロパティキーで、プロパティに保存する。
	 * @param vfBase	相対パス変換のための基準パス
	 * @param key		引数設定の保存先を示すプロパティキー
	 * @param arg		保存する引数設定
	 */
	protected void setSubModuleArgValue(VirtualFile vfBase, String key, IModuleArgConfig arg) {
		// プロパティへ設定するエントリの生成
		String[] argentry = new String[3];	// type, valueType, value
		ModuleArgType argtype = arg.getType();
		Object        argval  = arg.getValue();
		if (argval == null && arg.getOutToTempEnabled()) {
			//--- テンポラリファイル
			argval = MExecArgTempFile.instance;
		}
		//--- type
		if (argtype != null) {
			argentry[0] = argtype.toString();
		}
		//--- value
		if (argval instanceof IModuleArgConfig) {
			// 実行時引数定義へのリンク
			argentry[1] = FilterArgEditModel.class.getName();	// 編集モデルのクラス名
			argentry[2] = convertArgLinkToProperty((IModuleArgConfig)argval);
			//--- prefix/suffix のチェック
			if (argval instanceof FilterArgReferValueEditModel) {
				FilterArgReferValueEditModel refval = (FilterArgReferValueEditModel)argval;
				//--- prefix
				if (!refval.isEmptyPrefix()) {
					props.setString(key + SUFFIX_SUBFILTER_REFARG_PREFIX, refval.getPrefix());
				} else {
					props.clearProperty(key + SUFFIX_SUBFILTER_REFARG_PREFIX);
				}
				//--- suffix
				if (!refval.isEmptySuffix()) {
					props.setString(key + SUFFIX_SUBFILTER_REFARG_SUFFIX, refval.getSuffix());
				} else {
					props.clearProperty(key + SUFFIX_SUBFILTER_REFARG_SUFFIX);
				}
			} else {
				// prefix/suffix はなし
				props.clearProperty(key + SUFFIX_SUBFILTER_REFARG_PREFIX);
				props.clearProperty(key + SUFFIX_SUBFILTER_REFARG_SUFFIX);
			}
		}
		else if (argval instanceof ModuleArgID) {
			// 他引数へのリンク
			argentry[1] = ModuleArgID.class.getName();
			argentry[2] = convertModuleArgIdToProperty((ModuleArgID)argval);
		}
		else if (argval instanceof IMExecArgParam) {
			// parameter
			argentry[1] = MExecArgParamManager.getIdentifier((IMExecArgParam)argval);
			argentry[2] = null;
		}
		else if (argval instanceof VirtualFile) {
			argentry[1] = ARGVAL_TYPE_FILE;
			argentry[2] = convertFileToProperty(vfBase, (VirtualFile)argval);
		}
		else if (argval != null) {
			argentry[1] = ARGVAL_TYPE_STRING;
			argentry[2] = argval.toString();
		}
		
		// プロパティへ設定
		props.setStringArray(key, argentry);
	}

	/**
	 * 指定されたサブフィルタリストに対応する実行順序情報を、プロパティから取得し、サブフィルタリストを更新する。
	 * 正常に取得できないパラメータが存在する場合、<em>errorStates</em> に情報が追加される。
	 * @param invalidStates	不整合エラー情報を保持するオブジェクト
	 * @param modulelist	このフィルタマクロのサブフィルタリスト
	 * @throws IllegalMacroFilterPrefsException	解読不能の不正なデータが含まれている場合（詳細メッセージ含む）
	 * @since 3.1.0
	 */
	protected void getSubModuleLink(DefMacroFilterInvalidStates invalidStates, final ModuleDataList<MacroSubModuleRuntimeData> modulelist) {
		int numModules = modulelist.size();
		for (int i = 0; i < numModules; ++i) {
			MacroSubModuleRuntimeData data = modulelist.get(i);
			String keySubFilter = getSubFilterKey(i);
			
			// リンク情報有効／無効のプロパティ取得
			data.disconnectAllWaitModules();	// 既存のリンクは解除
			String keyLinkEnabled = keySubFilter + SUFFIX_SUBFILTER_LINK_ENABLED;
			boolean linkEnabled = props.getBooleanValue(keyLinkEnabled, false);
			data.setRunOrderLinkEnabled(linkEnabled);
			
			// リンク情報が有効な場合のみ、待機リンクを取得
			if (linkEnabled) {
				String keyWaitLink = keySubFilter + SUFFIX_SUBFILTER_LINK_WAIT;
				Integer[] numbers = EMPTY_SUBFILTER_WAITNUMBERS;
				try {
					numbers = props.getIntegerArray(keyWaitLink, EMPTY_SUBFILTER_WAITNUMBERS);
				}
				catch (Throwable ex) {
					String strNumbers = props.getString(keyWaitLink, "");
					String errmsg = RunnerMessages.getInstance().msgMacroFilterDefFailedToEdit
							+ RunnerMessages.getInstance().msgMacroSubFilterInvalidWaitNumber
							+ " : [" + data.getRunNo() + ":" + data.getName() + "] + \"" + String.valueOf(strNumbers) + "\"";
					throw new IllegalMacroFilterPrefsException(getIllegalPropertyMessage(keyWaitLink, errmsg), ex);
				}
				for (Integer num : numbers) {
					int index = num - 1;
					if (index < 0 || index >= numModules) {
						String errmsg = RunnerMessages.getInstance().msgMacroSubFilterOutOfBoundWaitNumber + " : [" + num + "]";
						AppLogger.warn(getIllegalPropertyMessage(keyWaitLink, errmsg));
						invalidStates.addSubFilterState(SubFilterInvalidState.WARN_OutOfBOundWaitNumber, data.getRunNo(), data, errmsg);
					}
					else if (index == i) {
						String errmsg = RunnerMessages.getInstance().msgMacroSubFilterInvalidWaitNumber + " : [" + num + "]";
						AppLogger.warn(getIllegalPropertyMessage(keyWaitLink, errmsg));
						invalidStates.addSubFilterState(SubFilterInvalidState.WARN_InvalidWaitNumber, data.getRunNo(), data, errmsg);
					}
					else {
						data.connectWaitModule(modulelist.get(index));
					}
				}
			}
		}
	}
	
	/**
	 * 指定されたサブフィルタリストの実行順序情報を、プロパティに保存する。
	 * @param modulelist	実行順序情報を持つサブフィルタリスト
	 * @since 3.1.0
	 */
	protected void setSubModuleLink(final ModuleDataList<MacroSubModuleRuntimeData> modulelist) {
		int numModules = modulelist.size();
		ArrayList<Integer> numlist = null;
		for (int i = 0; i < numModules; ++i) {
			MacroSubModuleRuntimeData data = modulelist.get(i);
			String keySubFilter = getSubFilterKey(i);
			
			// リンク情報のプロパティ保存
			String keyLinkEnabled = keySubFilter + SUFFIX_SUBFILTER_LINK_ENABLED;
			String keyWaitLink = keySubFilter + SUFFIX_SUBFILTER_LINK_WAIT;
			boolean linkEnabled = data.getRunOrderLinkEnabled();
			if (linkEnabled) {
				// リンク情報が有効の場合は、リンク有効フラグを保存
				props.setBooleanValue(keyLinkEnabled, linkEnabled);
				Set<MacroSubModuleRuntimeData> waitModules = data.getWaitModuleSet();
				if (waitModules.isEmpty()) {
					// 待機フィルタが一つもない場合は、待機フィルタ番号プロパティを削除
					props.clearProperty(keyWaitLink);
				} else {
					// 待機フィルタの番号(index+1)を、整数配列として保存
					//--- サブフィルタ番号バッファの確保
					if (numlist == null)
						numlist = new ArrayList<Integer>(waitModules.size());
					else
						numlist.ensureCapacity(waitModules.size());
					//--- サブフィルタ番号を取得
					for (MacroSubModuleRuntimeData wm : waitModules) {
						numlist.add(modulelist.indexOf(wm) + 1);
					}
					//--- プロパティへ保存
					props.setPropertyValueList(keyWaitLink, numlist);
					//--- バッファのクリア
					numlist.clear();
				}
			}
			else {
				// リンク情報が無効の場合は、リンク情報プロパティを削除
				props.clearProperty(keyLinkEnabled);
				props.clearProperty(keyWaitLink);
			}
		}
	}

	/**
	 * 指定された引数設定から、引数番号を示す文字列を取得する。
	 * @param argdata	対象の引数設定
	 * @return	変換された文字列。指定された引数が <tt>null</tt> の場合は <tt>null</tt>
	 */
	protected String convertArgLinkToProperty(final IModuleArgConfig argdata) {
		if (argdata == null) {
			return null;
		} else {
			return String.valueOf(argdata.getArgNo());
		}
	}

	/**
	 * 指定された参照先引数番号に対応する、このフィルタマクロの定義引数を取得する。
	 * @param strValue		引数番号を示す文字列(数字のみ)
	 * @param defArgList	このフィルタマクロの定義引数リスト
	 * @return	引数番号に対応する引数設定情報を保持するオブジェクト
	 * @throws NumberFormatException	<em>strValue</em> が整数値ではない場合
	 * @throws IllegalArgumentException	引数番号が <em>defArgList</em> の範囲外の場合
	 */
	protected FilterArgEditModel convertPropertyToArgLink(final String strValue, final MacroFilterDefArgList defArgList)
	throws NumberFormatException
	{
		int argno = Integer.parseInt(strValue);
		if (argno < 1 || argno > defArgList.size()) {
			throw new IllegalArgumentException("Illegal argument no. : " + String.valueOf(strValue));
		}
		return defArgList.get(argno-1);
	}

	/**
	 * 引数IDを文字列に変換する。
	 * @param argid	引数ID
	 * @return	引数IDを示す文字列。指定された引数が <tt>null</tt> の場合は <tt>null</tt>
	 */
	protected String convertModuleArgIdToProperty(final ModuleArgID argid) {
		if (argid == null) {
			return null;
		} else {
			return String.format("%d:$%d", argid.runNo(), argid.argNo());
		}
	}

	/**
	 * 引数IDを示す文字列から、引数IDに対応する引数設定を取得する。
	 * @param strValue		引数IDを示す文字列
	 * @param moduleList	サブフィルタのリスト
	 * @return	サブフィルタリストにおける、引数IDオブジェクト。指定された文字列が <tt>null</tt> もしくは空文字の場合は <tt>null</tt>
	 * @throws NumberFormatException	引数IDを示す文字列が、数値に変換できない場合
	 * @throws IllegalArgumentException	引数IDに対応する引数が存在しない場合
	 */
	protected ModuleArgID convertPropertyToModuleArgId(final String strValue, final ModuleDataList<MacroSubModuleRuntimeData> moduleList)
	throws NumberFormatException
	{
		ModuleArgID argid = null;
		
		if (!Strings.isNullOrEmpty(strValue)) {
			String[] strno = strValue.split("\\:\\$");
			long runno = Long.parseLong(strno[0]);
			int argno = Integer.parseInt(strno[1]);
			MacroSubModuleRuntimeData data = moduleList.findByRunNo(runno);
			if (data == null)
				throw new IllegalArgumentException("Does not exist ModuleRuntimeData by runNo=" + runno);
			if (argno < 1 || argno > data.getArgumentCount())
				throw new IllegalArgumentException("Does not exist Argument by argNo=" + argno);
			argid = new ModuleArgID(data, argno);
		}
		
		return argid;
	}

	/**
	 * 基準パスをもとに、指定されたパスを相対パスを示す文字列に変換する。
	 * @param vfBase	基準パス
	 * @param vfTarget	変換対象のパス
	 * @return	相対パスを示す文字列。<em>vfTarget</em> が <tt>null</tt> の場合は <tt>null</tt>
	 */
	protected String convertFileToProperty(VirtualFile vfBase, VirtualFile vfTarget) {
		String strPath = null;
		
		if (vfTarget != null) {
			if (!vfTarget.isAbsolute()) {
				vfTarget = vfTarget.getAbsoluteFile().getNormalizedFile();
			}
			if (vfBase != null && vfTarget.isDescendingFrom(vfBase)) {
				//--- 相対パスに変換
				strPath = vfTarget.relativePathFrom(vfBase, Files.CommonSeparatorChar);
			} else {
				//--- 絶対パスのまま URI に変換
				strPath = vfTarget.toURI().toString();
			}
		}
		
		return strPath;
	}

	/**
	 * 指定された相対パスを示す文字列を、基準パスをもとに絶対パスに変換する。
	 * @param key		未使用
	 * @param vfBase	基準パス
	 * @param path		変換する相対パスを示す文字列
	 * @return	絶対パス。<em>path</em> が <tt>null</tt> もしくは空文字の場合は <tt>null</tt>
	 * @throws URISyntaxException	<em>path</em> が URI の書式として正しくない場合
	 */
	protected VirtualFile convertPropertyToVirtualFile(String key, VirtualFile vfBase, String path)
	throws URISyntaxException
	{
		VirtualFile vfPath = null;
		
		if (!Strings.isNullOrEmpty(path)) {
			URI uri = new URI(path);
			if (uri.isAbsolute()) {
				vfPath = ModuleFileManager.fromURI(uri).getNormalizedFile();
			}
			else if (vfBase != null) {
				vfPath = vfBase.getChildFile(path).getNormalizedFile();
			}
			else {
				vfPath = ModuleFileManager.fromJavaFile(new File(path)).getAbsoluteFile().getNormalizedFile();
			}
		}
		
		return vfPath;
	}

	static protected final String getIllegalPropertyMessage(String key, String format, Object...params) {
		return getIllegalPropertyMessage(key, String.format(format, params));
	}
	
	static protected final String getIllegalPropertyMessage(String key, String message) {
		String strret = "MExecDefSubFilters(key=" + String.valueOf(key) + ") ";
		if (Strings.isNullOrEmpty(message)) {
			strret = strret + "illegal value.";
		} else {
			strret = strret + message;
		}
		return strret;
	}

	/**
	 * サブフィルタ内の引数インデックスに対応する、引数情報プロパティキーを取得する。
	 * なお、このプロパティキーは、サブフィルタのフィルタ定義用プロパティキーに連結して使用するサフィックスを示す。
	 * @param index	サブフィルタのインデックス(0～)
	 * @return	インデックスに対応するサブフィルタの引数情報用プロパティキー
	 */
	static protected final String getSubFilterArgsRecordSuffix(int index) {
		return SUFFIX_SUBFILTER_ARGS_REC + Integer.toString(index+1);
	}

	/**
	 * サブフィルタのインデックスに対応する、フィルタ定義プロパティキーを取得する。
	 * @param index	サブフィルタのインデックス(0～)
	 * @return	インデックスに対応するサブフィルタのフィルタ定義用プロパティキー
	 */
	static protected final String getSubFilterKey(int index) {
		return KEY_SUBFILTER_PREFIX + Integer.toString(index+1);
	}

	/**
	 * フィルタマクロの引数インデックスに対応する、引数情報プロパティキーを取得する。
	 * @param index	フィルタマクロの引数定義における引数インデックス(0～)
	 * @return	インデックスに対応するフィルタマクロの引数定義用プロパティキー
	 */
	static protected final String getFilterDefArgsRecordKey(int index) {
		return KEY_MEXECDEFARGS_REC + Integer.toString(index+1);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * フィルターマクロ定義データに解読不可能な不正データが存在する場合にスローされる例外。
	 * @version 3.1.3
	 * @since 3.1.3
	 */
	static public class IllegalMacroFilterPrefsException extends RuntimeException
	{
		private static final long serialVersionUID = -8515018693843498972L;

		public IllegalMacroFilterPrefsException() {
			super();
		}

		public IllegalMacroFilterPrefsException(String message) {
			super(message);
		}

		public IllegalMacroFilterPrefsException(Throwable cause) {
			super(cause);
		}

		public IllegalMacroFilterPrefsException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	/**
	 * フィルターマクロ定義の読み込み時のエラーに関する詳細情報を保持するオブジェクト。
	 * @version 3.1.3
	 * @since 3.1.3
	 */
	static public class DefMacroFilterInvalidStates
	{
		private final ArrayList<MacroFilterDefArgInvalidState>	_defArgsStateList	= new ArrayList<MacroFilterDefArgInvalidState>();
		private final ArrayList<SubFilterInvalidState>			_subFilterStateList	= new ArrayList<SubFilterInvalidState>();
		
		public DefMacroFilterInvalidStates() {
		}
		
		public void clear() {
			_defArgsStateList.clear();
			_subFilterStateList.clear();
		}
		
		public boolean hasError() {
			return (!_defArgsStateList.isEmpty() || !_subFilterStateList.isEmpty());
		}
		
		public boolean hasDefArgsError() {
			return (!_defArgsStateList.isEmpty());
		}
		
		public boolean hasSubFilterError() {
			return (!_subFilterStateList.isEmpty());
		}
		
		public ArrayList<MacroFilterDefArgInvalidState> getDefArgsStateList() {
			return _defArgsStateList;
		}
		
		public ArrayList<SubFilterInvalidState> getSubFilterStateList() {
			return _subFilterStateList;
		}
		
		public void addDefArgState(int code, int argno, String message) {
			_defArgsStateList.add(new MacroFilterDefArgInvalidState(code, argno, message));
		}
		
		public void addSubFilterState(int code, long runno, ModuleRuntimeData moduledata, String message) {
			_subFilterStateList.add(new SubFilterInvalidState(code, runno, moduledata, message));
		}
		
		public void addSubFilterState(int code, long runno, ModuleRuntimeData moduledata, int argno, String message) {
			_subFilterStateList.add(new SubFilterInvalidState(code, runno, moduledata, argno, message));
		}
	}

	/**
	 * フィルターマクロの引数定義読み込み時のエラー情報を保持する不変オブジェクト。
	 * @version 3.1.3
	 * @since 3.1.3
	 */
	static public class MacroFilterDefArgInvalidState
	{
		static public final int	WARN_InvalidArgType		= 1;
		static public final int	WARN_ValueNotFile		= 2;
		static public final int	WARN_InvalidValueType	= 3;
		
		private final int		_code;
		private final int		_argno;
		private final String	_message;
		
		public MacroFilterDefArgInvalidState(int code, int argno, String message) {
			_code    = code;
			_argno   = argno;
			_message = message;
		}
		
		public int		code()		{ return _code; }
		public int		argNo()		{ return _argno; }
		public String	message()	{ return _message; }
	}

	/**
	 * サブフィルター定義読み込み時のエラー情報を保持する、不変オブジェクト。
	 * @version 3.1.3
	 * @since 3.1.3
	 */
	static public class SubFilterInvalidState
	{
		static public final int	WARN_ArgCountNotEquals		= 1;
		static public final int	WARN_IllegalModuleArgId		= 2;
		static public final int	WARN_IllegalDefArgRefer		= 3;
		static public final int	WARN_InvaludArgFileValue	= 4;
		static public final int	WARN_InvalidWaitNumber		= 5;
		static public final int	WARN_OutOfBOundWaitNumber	= 6;
		
		private final int				_code;
		private final long				_runno;
		private final ModuleRuntimeData	_moduledata;
		private final int				_argno;
		private final String			_message;
		
		public SubFilterInvalidState(int code, long runno, ModuleRuntimeData moduledata, String message) {
			this(code, runno, moduledata, -1, message);
		}
		
		public SubFilterInvalidState(int code, long runno, ModuleRuntimeData moduledata, int argno, String message) {
			_code       = code;
			_runno		= runno;
			_moduledata = moduledata;
			_argno      = argno;
			_message    = message;
		}
		
		public int					code()			{ return _code; }
		public long					runNo()			{ return _runno; }
		public ModuleRuntimeData	moduleData()	{ return _moduledata; }
		public int					argNo()			{ return _argno; }
		public String				message()		{ return _message; }
	}
}
