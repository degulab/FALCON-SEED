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
 * @(#)MExecDefSettings.java	2.0.0	2012/11/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefSettings.java	1.20	2012/03/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecDefSettings.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.setting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileType;
import ssac.aadl.module.setting.AbstractSettings;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgParamManager;
import ssac.util.Strings;
import ssac.util.io.DefaultFile;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;

/**
 * モジュール実行定義の設定情報。
 * 
 * @version 2.0.0	2012/11/08
 */
public class MExecDefSettings extends AbstractSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String SECTION = "MExecDef";
//	static protected final String KEY_NAME   = SECTION + ".name";
	static protected final String KEY_DESC   = SECTION + ".description";
	static protected final String KEY_MODULE_TYPE = SECTION + ".module.type";
	static protected final String KEY_MODULE_PATH = SECTION + ".module.path";
	static protected final String KEY_MODULE_MAIN = SECTION + ".module.mainclass";
	static protected final String KEY_ARGS   = SECTION + ".args";
	static protected final String KEY_ARGS_NUM = KEY_ARGS + ".num";
	static protected final String KEY_ARGS_REC = KEY_ARGS + ".record";
	
	static protected final String ARGVAL_TYPE_FILE = "file";
	static protected final String ARGVAL_TYPE_STRING = "string";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** モジュール実行定義情報の格納ディレクトリ **/
	private VirtualFile	_vfTargetDir;
	/** 実行対象モジュール **/
	private VirtualFile	_vfModule;
	
	/** モジュール実行引数定義 **/
	private final ArrayList<ModuleArgData> _arglist = new ArrayList<ModuleArgData>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefSettings() {
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
	//	Description
	//

	/**
	 * モジュール実行定義の説明が設定されている場合に <tt>true</tt> を返す。
	 * 有効な文字列が設定されていない場合は <tt>false</tt> を返す。
	 */
	public boolean isSpecifiedDescription() {
		return (getDescription() != null);
	}

	/**
	 * モジュール実行定義の説明を返す。
	 * 有効な文字列が設定されていない場合は <tt>null</tt> を返す。
	 * @return	説明
	 */
	public String getDescription() {
		String strDesc = props.getString(KEY_DESC, null);
		return (Strings.isNullOrEmpty(strDesc) ? null : strDesc);
	}

	/**
	 * モジュール実行定義の説明を設定する。
	 * <em>desc</em> が <tt>null</tt> もしくは空文字列の場合、エントリを除去する。
	 * @param desc	説明を示す文字列
	 */
	public void setDescription(String desc) {
		if (Strings.isNullOrEmpty(desc)) {
			props.clearProperty(KEY_DESC);
		} else {
			props.setString(KEY_DESC, desc);
		}
	}
	
	//
	//	Module File Type
	//

	/**
	 * 実行モジュールのファイル種別が設定されている場合に <tt>true</tt> を返す。
	 * 有効な種別が設定されていない場合は <tt>false</tt> を返す。
	 */
	public boolean isSpecifiedModuleFileType() {
		ModuleFileType type = getModuleFileType();
		return (type != null && ModuleFileType.UNKNOWN != type);
	}

	/**
	 * 実行モジュールのファイル種別を返す。
	 * @return	ファイル種別を返す。
	 * 			エントリが存在しない場合は {@link ModuleFileType#UNKNOWN} を返す。
	 * 			文字列が種別に該当しない場合は <tt>null</tt> を返す。
	 */
	public ModuleFileType getModuleFileType() {
		String strDesc = props.getString(KEY_MODULE_TYPE, null);
		return ModuleFileType.fromName(strDesc);
	}

	/**
	 * 実行モジュールのファイル種別を設定する。
	 * 指定されたファイル種別が <tt>null</tt> もしくは {@link ModuleFileType#UNKNOWN} の場合、エントリを除去する。
	 * @param type	ファイル種別
	 */
	public void setModuleFileType(ModuleFileType type) {
		if (type != null && ModuleFileType.UNKNOWN != type) {
			props.setString(KEY_MODULE_TYPE, type.toString());
		} else {
			props.clearProperty(KEY_MODULE_TYPE);
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
	//	Module file
	//

	/**
	 * 実行モジュールを示す抽象パスが設定されている場合に <tt>true</tt> を返す。
	 */
	public boolean isSpecifiedModuleFile() {
		return (_vfModule != null);
	}

	/**
	 * 実行モジュールを示す抽象パスを返す。
	 * このメソッドが返すパスは、絶対パスを表す。
	 * @return	抽象パスを返す。設定されてない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getModuleFile() {
		return _vfModule;
	}

	/**
	 * 実行モジュールを示す抽象パスを設定する。
	 * @param file	抽象パス
	 */
	public void setModuleFile(VirtualFile file) {
		if (!file.isAbsolute()) {
			file = file.getAbsoluteFile();
		}
		this._vfModule = file.getNormalizedFile();
	}
	
	//
	//	Main-Class name
	//
	
	/**
	 * 実行モジュールのメインクラス名が設定されている場合に <tt>true</tt> を返す。
	 * 有効な文字列が設定されていない場合は <tt>false</tt> を返す。
	 */
	public boolean isSpecifiedModuleMainClass() {
		return (getModuleMainClass() != null);
	}
	
	/**
	 * 実行モジュールのメインクラス名を返す。
	 * 有効な文字列が設定されていない場合は <tt>null</tt> を返す。
	 * @return	メインクラス名
	 */
	public String getModuleMainClass() {
		String strName = props.getString(KEY_MODULE_MAIN, null);
		return (Strings.isNullOrEmpty(strName) ? null : strName);
	}
	
	/**
	 * 実行モジュールのメインクラス名を設定する。
	 * <em>name</em> が <tt>null</tt> もしくは空文字列の場合、エントリを除去する。
	 * @param name	メインクラス名を示す文字列
	 */
	public void setModuleMainClass(String name) {
		if (Strings.isNullOrEmpty(name)) {
			props.clearProperty(KEY_MODULE_MAIN);
		} else {
			props.setString(KEY_MODULE_MAIN, name);
		}
	}
	
	//
	//	Args
	//

	/**
	 * 引数設定が存在しない場合に <tt>true</tt> を返す。
	 */
	public boolean isArgumentEmpty() {
		return _arglist.isEmpty();
	}

	/**
	 * 引数設定をすべて破棄する。
	 */
	public void clearArguments() {
		_arglist.clear();
	}

	/**
	 * 保持されている引数設定の総数を返す。
	 * @return	保持されている引数設定の総数
	 */
	public int getNumArguments() {
		return _arglist.size();
	}
	
	public int indexOfArgument(ModuleArgData arg) {
		return _arglist.indexOf(arg);
	}
	
	public ModuleArgData getArgument(int index) {
		return _arglist.get(index);
	}
	
	public ModuleArgData setArgument(int index, ModuleArgData newArg) {
		return _arglist.set(index, newArg);
	}
	
	public void addArgument(ModuleArgData newArg) {
		_arglist.add(newArg);
	}
	
	public void insertArgument(int index, ModuleArgData newArg) {
		_arglist.add(index, newArg);
	}
	
	public ModuleArgData removeArgument(int index) {
		return _arglist.remove(index);
	}
	
	public boolean removeArgument(ModuleArgData arg) {
		return _arglist.remove(arg);
	}

	/**
	 * 指定された位置の引数属性を取得する。
	 * @param index	取得する位置のインデックス
	 * @return	指定された位置の引数属性を返す。
	 * 			指定された位置に引数が存在しない場合は <tt>null</tt> を返す。
	 */
	public ModuleArgType getArgumentType(int index) {
		try {
			ModuleArgData arg = getArgument(index);
			return arg.getType();
		} catch (Throwable ex) {
			return null;
		}
	}

	/**
	 * 指定された位置の引数説明を取得する。
	 * @param index	取得する位置のインデックス
	 * @return	指定された位置の引数説明を返す。
	 * 			指定された位置に引数が存在しない場合は <tt>null</tt> を返す。
	 */
	public String getArgumentDescription(int index) {
		try {
			ModuleArgData arg = getArgument(index);
			return arg.getDescription();
		} catch (Throwable ex) {
			return null;
		}
	}

	/**
	 * 指定された位置の引数の値を取得する。
	 * @param index	取得する位置のインデックス
	 * @return	指定された位置の引数の値を返す。
	 * 			指定された位置に引数が存在しない場合は <tt>null</tt> を返す。
	 */
	public Object getArgumentValue(int index) {
		try {
			ModuleArgData arg = getArgument(index);
			return arg.getValue();
		} catch (Throwable ex) {
			return null;
		}
	}

	/**
	 * 引数定義の配列を返す。
	 * @return	引数定義の配列
	 */
	public ModuleArgData[] getArgumentDataArray() {
		return _arglist.toArray(new ModuleArgData[_arglist.size()]);
	}
	
	//
	//	Args
	//
	
	public String getJavaVMArgs() {
		// 将来用
		return null;
	}

	//------------------------------------------------------------
	// Implement AbstractSettings interfaces
	//------------------------------------------------------------

	@Override
	public void commit() throws IOException {
		// 実行モジュールのパスを保存
		if (_vfModule != null) {
			String strPath = _vfModule.relativePathFrom(_vfTargetDir, Files.CommonSeparatorChar);
			props.setString(KEY_MODULE_PATH, strPath);
		} else {
			props.clearProperty(KEY_MODULE_PATH);
		}
		
		// 実行時引数の設定を保存
		int numArgs = props.getIntegerValue(KEY_ARGS_NUM, 0);
		int index = 0;
		if (!_arglist.isEmpty()) {
			// 新しい設定を保存
			int len = _arglist.size();
			for (; index < len; index++) {
				ModuleArgData data = _arglist.get(index);
				setArgDataValue(_vfTargetDir, getArgsRecordKey(index), data);
			}
		}
		// 古いプロパティを削除
		for (; index < numArgs; index++) {
			props.clearProperty(getArgsRecordKey(index));
		}
		// 実行時引数設定の総数を保存
		props.setIntegerValue(KEY_ARGS_NUM, _arglist.size());
		
		// プロパティの保存
		super.commit();
	}

	@Override
	public void rollback() {
		// プロパティの読み込み
		super.rollback();
		
		// 実行モジュールのパスを取得
		String strPath = props.getString(KEY_MODULE_PATH, null);
		if (Strings.isNullOrEmpty(strPath)) {
			this._vfModule = null;
		} else {
			this._vfModule = _vfTargetDir.getChildFile(strPath).getNormalizedFile();
		}
		
		// 実行時引数の設定を取得
		_arglist.clear();
		int numArgs = props.getIntegerValue(KEY_ARGS_NUM, 0);
		for (int i = 0; i < numArgs; i++) {
			ModuleArgData data = getArgDataValue(_vfTargetDir, getArgsRecordKey(i));
			_arglist.add(data);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

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
				//--- 相対パスに変換(vfBase 直下に保存されていることを前提とする)
				argentry[3] = ((VirtualFile)argval).relativePathFrom(vfBase, Files.CommonSeparatorChar);
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
					VirtualFile vf = vfBase.getChildFile(argentry[3]).getNormalizedFile();
					retdata.setValue(vf.getAbsoluteFile());
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
					AppLogger.debug(errmsg);
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

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
