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
 * @(#)MExecDefHistory.java	3.1.0	2014/05/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefHistory.java	2.0.0	2012/11/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefHistory.java	1.22	2012/08/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefHistory.java	1.20	2012/03/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.setting;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.setting.AbstractSettings;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.IModuleConfig;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgCsvFile;
import ssac.falconseed.module.args.MExecArgParamManager;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.io.DefaultFile;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;

/**
 * モジュール実行定義の実行時引数の履歴。
 * 
 * @version 3.1.0	2014/05/14
 * @since 1.20
 */
public class MExecDefHistory extends AbstractSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String SECTION = "MExecDef.History";
	static protected final String KEY_ARGS   = SECTION + ".args";
	static protected final String KEY_ARGS_NUM = KEY_ARGS + ".num";
	static protected final String KEY_ARGS_REC = KEY_ARGS + ".record";
	static protected final String KEY_HISTORY		= SECTION;
	static protected final String KEY_HISTORY_NUM	= KEY_HISTORY + ".num";
	
	static protected final String ARGVAL_TYPE_FILE = "file";
	static protected final String ARGVAL_TYPE_STRING = "string";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** モジュール実行定義情報の格納ディレクトリ **/
	private VirtualFile	_vfTargetDir;
	
	/** モジュール実行時引数の履歴 **/
	private final ArrayList<ArrayList<ModuleArgData>> _history = new ArrayList<ArrayList<ModuleArgData>>();
//	private final ArrayList<ModuleArgData> _arglist = new ArrayList<ModuleArgData>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefHistory() {
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
	
	//
	//	History
	//

	/**
	 * 履歴が存在しない場合に <tt>true</tt> を返す。
	 */
	public boolean isHistoryEmpty() {
		return _history.isEmpty();
	}

	/**
	 * 履歴をすべて破棄する。
	 */
	public void clearHistory() {
		_history.clear();
	}

	/**
	 * 指定されたサイズを超えている場合に、指定されたサイズとなるよう、古い履歴を削除する。
	 * @param maxSize	最大サイズ、負の値の場合は 0 とみなす。
	 */
	public void ensureMaxSize(int maxSize) {
		if (maxSize > 0) {
			for (int ri = _history.size() - 1; ri >= maxSize; ri--) {
				_history.remove(ri);
			}
		} else {
			_history.clear();
		}
	}

	/**
	 * 保持されている履歴の総数を返す。
	 * @return	保持されている履歴の総数
	 */
	public int getNumHistory() {
		return _history.size();
	}

	/**
	 * 保持されている履歴のうち、指定されたモジュール実行定義設定情報の引数種別にあわない
	 * 履歴を削除する。
	 * @param medSettings	モジュール実行定義の設定情報
	 * @return 削除された履歴の数、削除されなかった場合は 0
	 */
	public int ensureArgsTypes(final MExecDefSettings medSettings) {
		int ret = 0;
		if (!_history.isEmpty()) {
			if (medSettings.isArgumentEmpty()) {
				// 引数定義が存在しない場合は、すべての履歴を削除
				ret = _history.size();
				_history.clear();
			} else {
				int numArgs = medSettings.getNumArguments();
				for (int hindex = _history.size()-1; hindex >= 0; hindex--) {
					ArrayList<ModuleArgData> hitem = _history.get(hindex);
					if (hitem.size() == numArgs) {
						boolean remove = false;
						for (int i = 0; i < numArgs; i++) {
							ModuleArgData argDef = medSettings.getArgument(i);
							ModuleArgData argHist = hitem.get(i);
							if (argDef.getType() != argHist.getType()) {
								remove = true;
								break;
							}
							Object histValue = argHist.getValue();
							if ((histValue instanceof VirtualFile) || (histValue instanceof File)) {
								//--- 履歴の値が抽象パス
								if (argDef.getType() != ModuleArgType.IN && argDef.getType() != ModuleArgType.OUT) {
									// 引数の型がファイルを示すものではないなら、削除
									remove = true;
									break;
								}
							}
							else if (histValue instanceof String) {
								//--- 履歴の値が文字列
								if (argDef.getType() == ModuleArgType.OUT) {
									if (!(argDef.getValue() instanceof MExecArgCsvFile)) {
										// 引数の型が OUT だが、引数定義の値が CSV ファイルではない場合
										// (テンポラリ出力のプレフィックスは受け付けられない)
										remove = true;
										break;
									}
								}
								else if (argDef.getType() != ModuleArgType.STR
										&& argDef.getType() != ModuleArgType.PUB
										&& argDef.getType() != ModuleArgType.SUB)
								{
									// 引数の型が文字列型([STR],[PUB],[SUB]ではないなら、削除
									remove = true;
									break;
								}
							}
							else if (histValue instanceof MExecArgTempFile) {
								//--- 履歴の値がテンポラリファイル
								if (argDef.getType() != ModuleArgType.OUT || !(argDef.getValue() instanceof MExecArgCsvFile)) {
									// 引数の型が OUT ではないか、引数定義の値が CSV ファイルではない場合
									remove = true;
									break;
								}
							}
							else if (histValue != null) {
								//--- 履歴の値が判別不能なデータ型
								remove = true;
								break;
							}
							//--- 全ての条件をパスした場合は、この要素は削除対象外
						}
						if (!remove) {
							// 全ての引数が削除対象外なら、履歴は削除しない
							continue;
						}
					}
					//--- 次の場合は、履歴を削除
					//--- ・引数の数があわない場合
					//--- ・引数の型が異なる場合
					//--- ・引数の値が適切ではない場合
					_history.remove(hindex);
					ret++;
				}
			}
		}
		return ret;
	}

	/**
	 * 保持されている履歴のうち、指定されたモジュール実行定義設定情報の引数種別にあわない
	 * 履歴を削除する。
	 * @param data	モジュール実行定義の設定情報
	 * @return 削除された履歴の数、削除されなかった場合は 0
	 * @since 1.22
	 */
	public int ensureArgsTypes(final IModuleConfig<? extends IModuleArgConfig> data) {
		int ret = 0;
		if (!_history.isEmpty()) {
			if (data.isEmptyArgument()) {
				// 引数定義が存在しない場合は、すべての履歴を削除
				ret = _history.size();
				_history.clear();
			} else {
				int numArgs = data.getArgumentCount();
				for (int hindex = _history.size()-1; hindex >= 0; hindex--) {
					ArrayList<ModuleArgData> hitem = _history.get(hindex);
					if (hitem.size() == numArgs) {
						boolean remove = false;
						for (int i = 0; i < numArgs; i++) {
							ModuleArgData argDef = data.getArgDefinition(i);
							ModuleArgData argHist = hitem.get(i);
							if (argDef.getType() != argHist.getType()) {
								remove = true;
								break;
							}
							Object histValue = argHist.getValue();
							if ((histValue instanceof VirtualFile) || (histValue instanceof File)) {
								//--- 履歴の値が抽象パス
								if (argDef.getType() != ModuleArgType.IN && argDef.getType() != ModuleArgType.OUT) {
									// 引数の型がファイルを示すものではないなら、削除
									remove = true;
									break;
								}
							}
							else if (histValue instanceof String) {
								//--- 履歴の値が文字列
								if (argDef.getType() == ModuleArgType.OUT) {
									if (!(argDef.getValue() instanceof MExecArgCsvFile)) {
										// 引数の型が OUT だが、引数定義の値が CSV ファイルではない場合
										// (テンポラリ出力のプレフィックスは受け付けられない)
										remove = true;
										break;
									}
								}
								else if (argDef.getType() != ModuleArgType.STR
										&& argDef.getType() != ModuleArgType.PUB
										&& argDef.getType() != ModuleArgType.SUB)
								{
									// 引数の型が文字列型([STR],[PUB],[SUB])ではないなら、削除
									remove = true;
									break;
								}
							}
							else if (histValue instanceof MExecArgTempFile) {
								//--- 履歴の値がテンポラリファイル
								if (argDef.getType() != ModuleArgType.OUT || !(argDef.getValue() instanceof MExecArgCsvFile)) {
									// 引数の型が OUT ではないか、引数定義の値が CSV ファイルではない場合
									remove = true;
									break;
								}
							}
							else if (histValue != null) {
								//--- 履歴の値が判別不能なデータ型
								remove = true;
								break;
							}
							//--- 全ての条件をパスした場合は、この要素は削除対象外
						}
						if (!remove) {
							// 全ての引数が削除対象外なら、履歴は削除しない
							continue;
						}
					}
					//--- 次の場合は、履歴を削除
					//--- ・引数の数があわない場合
					//--- ・引数の型が異なる場合
					//--- ・引数の値が適切ではない場合
					_history.remove(hindex);
					ret++;
				}
			}
		}
		return ret;
	}

	/**
	 * 指定された値と等しい要素が格納されている位置を取得する。
	 * このメソッドでは、<code>ModuleArgData.equals</code> メソッドがすべての要素で <tt>true</tt> を返すものを探す。
	 * @param arg	検索するオブジェクト
	 * @return	等しい要素がある場合はその位置を示すインデックス、それ以外の場合は (-1)
	 */
	public int indexOfHistoryItem(ArrayList<ModuleArgData> arg) {
		return _history.indexOf(arg);
	}
	
	/**
	 * 指定された値と等しい履歴が格納されている位置を取得する。
	 * このメソッドでは、引数説明を除く、属性と値が等しい場合に、履歴が等しいとみなす。
	 * @param arg	検索するオブジェクト
	 * @return	等しい履歴がある場合はその位置を示すインデックス、それ以外の場合は (-1)
	 */
	public int indexOfHistoryData(ArrayList<ModuleArgData> arg) {
		for (int i = 0; i < _history.size(); i++) {
			ArrayList<ModuleArgData> data = _history.get(i);
			if (equalHistoryData(data, arg)) {
				return i;
			}
		}
		//--- not found
		return (-1);
	}
	
	public ArrayList<ModuleArgData> getHistory(int index) {
		return _history.get(index);
	}

	/**
	 * このオブジェクトに新しい履歴を追加する。等しい履歴が存在する場合、その履歴をリストの先頭に移動する。
	 * @param newHistory	新しい履歴
	 */
	public void addHistory(ArrayList<ModuleArgData> newHistory) {
		if (newHistory == null)
			throw new NullPointerException("The specified ModuleArgData object is null.");
		
		int index = indexOfHistoryData(newHistory);
		if (index >= 0) {
			//--- history exists, remove from list
			_history.remove(index);
		}
		//--- new history insert to top
		_history.add(0, newHistory);
	}
	
	public ArrayList<ModuleArgData> removeHistory(int index) {
		return _history.remove(index);
	}
	
	public boolean removeHistory(ArrayList<ModuleArgData> history) {
		return _history.remove(history);
	}

	/**
	 * 指定された履歴位置の引数定義の配列を返す。
	 * @return	引数定義の配列
	 */
	public ModuleArgData[] getArgumentDataArray(int index) {
		ArrayList<ModuleArgData> arglist = _history.get(index);
		return arglist.toArray(new ModuleArgData[arglist.size()]);
	}

	//------------------------------------------------------------
	// Implement AbstractSettings interfaces
	//------------------------------------------------------------

	@Override
	public void commit() throws IOException {
		// 実行時引数の履歴を保存
		int numHistory = props.getIntegerValue(KEY_HISTORY_NUM, 0);
		int hindex = 0;
		if (!_history.isEmpty()) {
			int hlen = _history.size();
			for (; hindex < hlen; hindex++) {
				ArrayList<ModuleArgData> arglist = _history.get(hindex);
				String prefix = getHistoryRecordKey(hindex);
				int numArgs = props.getIntegerValue(prefix+KEY_ARGS_NUM, 0);
				int index = 0;
				if (arglist != null && !arglist.isEmpty()) {
					int len = arglist.size();
					for (; index < len; index++) {
						ModuleArgData data = arglist.get(index);
						setArgDataValue(_vfTargetDir, prefix+getArgsRecordKey(index), data);
					}
				}
				//--- 古いプロパティを削除
				for (; index < numArgs; index++) {
					props.clearProperty(prefix+getArgsRecordKey(index));
				}
				//--- 実行時引数設定の総数を保存
				props.setIntegerValue(prefix+KEY_ARGS_NUM, arglist.size());
			}
		}
		//--- 古いプロパティを削除
		for (; hindex < numHistory; hindex++) {
			String prefix = getHistoryRecordKey(hindex);
			int numArgs = props.getIntegerValue(prefix+KEY_ARGS_NUM, 0);
			for (int index = 0; index < numArgs; index++) {
				props.clearProperties(prefix+getArgsRecordKey(index));
			}
		}
		// 実行時引数設定の総数を保存
		props.setIntegerValue(KEY_HISTORY_NUM, _history.size());
		
		// プロパティの保存
		super.commit();
	}

	@Override
	public void rollback() {
		// プロパティの読み込み
		super.rollback();
		
		// 実行時引数の履歴を取得
		_history.clear();
		int numHistory = props.getIntegerValue(KEY_HISTORY_NUM, 0);
		for (int hindex = 0; hindex < numHistory; hindex++) {
			String prefix = getHistoryRecordKey(hindex);
			int numArgs = props.getIntegerValue(prefix+KEY_ARGS_NUM, 0);
			ArrayList<ModuleArgData> arglist = new ArrayList<ModuleArgData>(numArgs);
			for (int index = 0; index < numArgs; index++) {
				ModuleArgData data = getArgDataValue(_vfTargetDir, prefix+getArgsRecordKey(index));
				arglist.add(data);
			}
			//--- 履歴のエントリが空ではない場合に、履歴として保存
			if (!arglist.isEmpty()) {
				_history.add(arglist);
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected ArrayList<ModuleArgData> setArgument(int index, ArrayList<ModuleArgData> newHistory) {
		if (newHistory == null)
			throw new NullPointerException("The specified ModuleArgData object is null.");
		return _history.set(index, newHistory);
	}
	
	protected boolean equalHistoryData(List<ModuleArgData> item1, List<ModuleArgData> item2) {
		if (item1.size() != item2.size())
			return false;	// 要素数がことなる場合は、異なる履歴
		
		int len = item1.size();
		for (int i = 0; i < len; i++) {
			ModuleArgData arg1 = item1.get(i);
			ModuleArgData arg2 = item2.get(i);
			//--- タイプは同じか？
			if (arg1.getType() != arg2.getType())
				return false;	// タイプが異なるなら、異なる履歴
			//--- 値は同じか？
			if (!Objects.isEqual(arg1.getValue(), arg2.getValue()))
				return false;	// 値が異なるなら、異なる履歴
			//--- 説明は無視
		}
		
		// 同じ履歴
		return true;
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
	
	@Override
	protected void setSettingFile(VirtualFile targetFile) {
		super.setSettingFile(targetFile);
		this._vfTargetDir = targetFile.getParentFile();
	}
	
	protected void setArgDataValue(VirtualFile vfBase, String key, ModuleArgData data) {
		// プロパティへ設定するエントリの生成
		String[] argentry = new String[4];
		ModuleArgType argtype = data.getType();
		String        argdesc = data.getDescription();
		Object        argval  = data.getValue();
		//--- type
		if (argtype != null) {
			argentry[0] = argtype.toString();
		}
		//--- desc
		if (argdesc != null) {
			argentry[1] = argdesc;
		}
		//--- value
		if (argval != null) {
			if (argval instanceof VirtualFile) {
				// Real file
				argentry[2] = ARGVAL_TYPE_FILE;
				//--- vfBase 直下に存在するファイルなら、相対パスに変換
				if (((VirtualFile)argval).isDescendingFrom(vfBase)) {
					argentry[3] = ((VirtualFile)argval).relativePathFrom(vfBase, Files.CommonSeparatorChar);
				} else {
					argentry[3] = ((VirtualFile)argval).toURI().toString();
				}
			}
			else if (argval instanceof IMExecArgParam) {
				// parameter
				argentry[2] = MExecArgParamManager.getIdentifier((IMExecArgParam)argval);
				argentry[3] = null;
			}
			else {
				// String
				argentry[2] = ARGVAL_TYPE_STRING;
				argentry[3] = argval.toString();
			}
		}
		
		// プロパティへ設定
		props.setStringArray(key, argentry);
	}

	protected ModuleArgData getArgDataValue(VirtualFile vfBase, String key) {
		// プロパティから値を取得
		String[] argentry = new String[4];
		{
			String[] sval = props.getStringArray(key, null);
			if (sval != null && sval.length > 0) {
				System.arraycopy(sval, 0, argentry, 0, Math.min(sval.length, argentry.length));
			}
		}
		
		// プロパティ値から ModuleArgData インスタンスを生成
		ModuleArgData retdata = new ModuleArgData();
		//--- type
		if (argentry[0] != null) {
			ModuleArgType type = ModuleArgType.fromName(argentry[0]);
			retdata.setType(type==null ? ModuleArgType.NONE : type);
		}
		//--- description
		if (argentry[1] != null) {
			retdata.setDescription(argentry[1]);
		}
		//--- value
		if (!Strings.isNullOrEmpty(argentry[2])) {
			if (ARGVAL_TYPE_FILE.equalsIgnoreCase(argentry[2])) {
				// VirtualFile path
				if (!Strings.isNullOrEmpty(argentry[3])) {
					try {
						URI uri = new URI(argentry[3]);
						if (uri.isAbsolute()) {
							retdata.setValue(ModuleFileManager.fromURI(uri).getNormalizedFile());
						} else {
							retdata.setValue(vfBase.getChildFile(argentry[3]).getNormalizedFile());
						}
					} catch (Throwable ex) {
						String errmsg = "MExecDefHistory property [" + key + "] is illegal value : \"" + argentry[3] + "\"";
						AppLogger.warn(errmsg);
					}
				}
			} else if (ARGVAL_TYPE_STRING.equalsIgnoreCase(argentry[2])) {
				// String
				retdata.setValue(argentry[3]);
			} else {
				// IMExecArgParam
				IMExecArgParam val = MExecArgParamManager.fromIdentifier(argentry[2]);
				if (val != null) {
					retdata.setValue(val);
				} else {
					String errmsg = "MExecDefSettings property [" + key + "] is illegal value type : \"" + argentry[2] + "\"";
					AppLogger.warn(errmsg);
				}
			}
		}
		else {
			retdata.setValue(argentry[3]);
		}
		
		return retdata;
	}
	
	static protected final String getArgsRecordKey(int index) {
		return KEY_ARGS_REC + Integer.toString(index+1);
	}
	
	static protected final String getHistoryRecordKey(int index) {
		return KEY_HISTORY + Integer.toString(index+1);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
