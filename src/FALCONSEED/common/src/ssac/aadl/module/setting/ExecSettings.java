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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ExecSettings.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExecSettings.java	1.10	2009/01/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExecSettings.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import java.io.File;
import java.io.IOException;

import ssac.util.Strings;

/**
 * 実行設定情報を保持するクラス。
 * ビルドオプションダイアログの実行オプション・パネルにより設定される
 * 情報を操作するための機能を提供する。
 * 
 * @version 1.14	2009/12/09
 */
public class ExecSettings extends ClassPathSettings
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final String SECTION = "Execute";

	//static public final String GROUP_JAVACMD	= SECTION + ".JavaCommand";
	static public final String GROUP_TARGET	= SECTION + ".Target";
	static public final String GROUP_PROGRAM	= SECTION + ".Program";
	static public final String GROUP_JAVAVM	= SECTION + ".JavaVM";
	static public final String GROUP_WORKDIR	= SECTION + ".WorkDir";
	
	//static public final String KEY_JAVACMD_SPECIFY	= GROUP_JAVACMD + ".specify";
	//static public final String KEY_JAVACMD_PATH		= GROUP_JAVACMD + ".path";
	
	static public final String KEY_TARGET_FILE = GROUP_TARGET + ".file";
	static public final String KEY_TARGET_MAIN = GROUP_TARGET + ".main-class";
	
	static public final String KEY_PROGRAM_ARGS = GROUP_PROGRAM + ".args";
	static public final String KEY_PROGRAM_ARGS_NUM = KEY_PROGRAM_ARGS + ".num";
	static public final String KEY_PROGRAM_ARGS_VAL = KEY_PROGRAM_ARGS + ".value";
	
	static public final String KEY_JAVAVM_ARGS = GROUP_JAVAVM + ".args";
	
	static public final String KEY_WORKDIR_SPECIFY	= GROUP_WORKDIR + ".specify";
	static public final String KEY_WORKDIR_PATH		= GROUP_WORKDIR + ".path";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//private File javaCommand;
	private File targetFile;
	private File workdir;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ExecSettings() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	//--- KEY_JAVACMD_SPECIFY
	
	//public boolean isSpecifiedJavaCommand() {
	//	return this.props.getBooleanValue(KEY_JAVACMD_SPECIFY);
	//}
	
	//public void setJavaCommandSpecified(boolean toSpecify) {
	//	this.props.setBooleanValue(KEY_JAVACMD_SPECIFY, toSpecify);
	//}
	
	//--- KEY_JAVACMD_PATH
	
	//public File getTargetJavaCommandFile() {
	//	if (isSpecifiedJavaCommand() && javaCommand != null) {
	//		return javaCommand;
	//	}
	//	
	//	return AppSettings.getInstance().getCurrentJavaCommandFile();
	//}
	
	//public String getTargetJavaCommandPath() {
	//	if (isSpecifiedJavaCommand() && javaCommand != null) {
	//		if (javaCommand.exists()) {
	//			return javaCommand.getPath();
	//		}
	//	}
	//	
	//	return AppSettings.getInstance().getCurrentJavaCommandPath();
	//}
	
	//public File getJavaCommandFile() {
	//	return javaCommand;
	//}
	
	//public String getJavaCommandPath() {
	//	if (javaCommand != null)
	//		return javaCommand.getPath();
	//	else
	//		return null;
	//}
	
	//public void setJavaCommandFile(File cmdFile) {
	//	if (cmdFile != null)
	//		javaCommand = cmdFile.getAbsoluteFile();
	//	else
	//		javaCommand = null;
	//}
	
	//public void setJavaCommandPath(String cmdPath) {
	//	if (cmdPath != null && cmdPath.length() > 0) {
	//		setJavaCommandFile(new File(cmdPath));
	//	} else {
	//		javaCommand = null;
	//	}
	//}
	
	//--- KEY_TARGET_FILE
	
	public boolean hasTargetFile() {
		return (targetFile != null);
	}
	
	public File getTargetFile() {
		return targetFile;
	}
	
	public String getTargetPath() {
		if (targetFile != null) {
			return targetFile.getPath();
		} else {
			return null;
		}
	}
	
	public void setTargetFile(File file) {
		if (file != null) {
			targetFile = file.getAbsoluteFile();
		} else {
			targetFile = null;
		}
	}
	
	public void setTargetPath(String path) {
		if (path != null && path.length() > 0) {
			setTargetFile(new File(path));
		} else {
			targetFile = null;
		}
	}
	
	//--- KEY_TARGET_MAIN
	
	public boolean hasTargetMainClass() {
		return (!Strings.isNullOrEmpty(getTargetMainClass()));
	}
	
	public String getTargetMainClass() {
		return this.props.getString(KEY_TARGET_MAIN, null);
	}
	
	public void setTargetMainClass(String mainClass) {
		if (mainClass != null && mainClass.length() > 0) {
			this.props.setString(KEY_TARGET_MAIN, mainClass);
		} else {
			this.props.clearProperty(KEY_TARGET_MAIN);
		}
	}
	
	//--- KEY_PROGRAM_ARGS
	
	public String[] getProgramArgs() {
		String[] argValues;
		int numArgs = props.getInteger(KEY_PROGRAM_ARGS_NUM, Integer.valueOf(0));
		if (numArgs > 0) {
			argValues = new String[numArgs];
			for (int i = 0; i < numArgs; i++) {
				argValues[i] = props.getString(getProgramArgsValueKey(i), "");
			}
		}
		else {
			argValues = EMPTY_STRING_ARRAY;
		}
		return argValues;
		//return this.props.getStringArray(KEY_PROGRAM_ARGS, EMPTY_STRING_ARRAY);
	}

	/*
	public ArgsTableModel getProgramArgsList() {
		String[] args = getProgramArgs();
		return new ArgsTableModel(args);
	}
	*/
	
	public void setProgramArgs(String[] args) {
		int numArgs = props.getInteger(KEY_PROGRAM_ARGS_NUM, Integer.valueOf(0));
		int newArgsCount = 0;
		int index = 0;
		if (args != null && args.length > 0) {
			newArgsCount = args.length;
			for (; index < newArgsCount; index++) {
				if (Strings.isNullOrEmpty(args[index]))
					props.clearProperty(getProgramArgsValueKey(index));
				else
					props.setString(getProgramArgsValueKey(index), args[index]);
			}
		}
		//--- 余分なプロパティは削除
		for (; index < numArgs; index++) {
			props.clearProperty(getProgramArgsValueKey(index));
		}
		//--- 要素数を保存
		props.setInteger(KEY_PROGRAM_ARGS_NUM, newArgsCount);
		
		//if (args != null && args.length > 0) {
		//	this.props.setStringArray(KEY_PROGRAM_ARGS, args);
		//} else {
		//	this.props.clearProperty(KEY_PROGRAM_ARGS);
		//}
	}

	/*
	public void setProgramArgsList(ArgsTableModel list) {
		setProgramArgs(list.toStringArray());
	}
	*/
	
	//--- KEY_JAVAVM_ARGS
	
	public String getJavaVMArgs() {
		return this.props.getString(KEY_JAVAVM_ARGS, "");
	}
	
	public void setJavaVMArgs(String args) {
		if (args != null && args.length() > 0) {
			this.props.setString(KEY_JAVAVM_ARGS, args);
		} else {
			this.props.clearProperty(KEY_JAVAVM_ARGS);
		}
	}
	
	//--- KEY_WORKDIR_SPECIFY
	
	public boolean isSpecifiedWorkDir() {
		return this.props.getBooleanValue(KEY_WORKDIR_SPECIFY);
	}
	
	public void setWorkDirSpecified(boolean toSpecify) {
		this.props.setBooleanValue(KEY_WORKDIR_SPECIFY, toSpecify);
	}
	
	//--- KEY_WORKDIR_PATH
	
	public File getDefaultWorkDirFile() {
		File defDir = null;
		
		// ターゲットファイルと同一のパス
		if (targetFile != null && targetFile.exists()) {
			// ターゲットファイルが存在するなら、そのディレクトリ
			defDir = targetFile.getAbsoluteFile().getParentFile();
		}
		else {
			File propFile = getJavaPropertyFile();
			if (propFile != null && propFile.exists()) {
				// プロパティファイルが存在するなら、そのディレクトリ
				defDir = propFile.getAbsoluteFile().getParentFile();
			}
		}
		
		return defDir;
	}
	
	public String getDefaultWorkDirPath() {
		File wdir = getDefaultWorkDirFile();
		if (wdir != null) {
			return wdir.getPath();
		} else {
			return null;
		}
	}

	/*
	public String getDefaultWorkDirPath() {
		// ターゲットファイルと同一のパス
		File dir;
		if (hasTargetFile()) {
			File file = new File(getTargetFile());
			dir = file.getParentFile();
		} else {
			dir = new File("");
		}
		return dir.getAbsolutePath();
	}
	*/
	
	public File getWorkDirFile() {
		return workdir;
	}
	
	public String getWorkDirPath() {
		if (workdir != null) {
			return workdir.getPath();
		} else {
			return null;
		}
	}
	
	public void setWorkDirFile(File file) {
		if (file != null) {
			workdir = file.getAbsoluteFile();
		} else {
			workdir = null;
		}
	}
	
	public void setWorkDirPath(String path) {
		if (path != null && path.length() > 0) {
			setWorkDirFile(new File(path));
		} else {
			workdir = null;
		}
	}

	//------------------------------------------------------------
	// Implement ClassPathSettings interfaces
	//------------------------------------------------------------

	@Override
	public void commit() throws IOException {
		// ファイルを相対パスに変換し、プロパティに保存
		//setAbsoluteJavaFileProperty(KEY_JAVACMD_PATH, javaCommand);
		setAbsoluteJavaFileProperty(KEY_TARGET_FILE, targetFile);
		setAbsoluteJavaFileProperty(KEY_WORKDIR_PATH, workdir);

		// プロパティの保存
		super.commit();
	}

	@Override
	public void rollback() {
		// プロパティの読み込み
		super.rollback();
		
		// プロパティからファイル情報を取得する
		//--- プロパティのファイル情報は、相対パスで保存されている場合がある
		//--- 相対パスの場合は、このプロパティファイルからの相対とする
		//javaCommand = getAbsoluteJavaFileProperty(KEY_JAVACMD_PATH, null);
		targetFile = getAbsoluteJavaFileProperty(KEY_TARGET_FILE, null);
		workdir = getAbsoluteJavaFileProperty(KEY_WORKDIR_PATH, null);
		
		// 旧プロパティを新プロパティへマップ
		if (!props.containsKey(KEY_PROGRAM_ARGS_NUM)) {
			if (props.containsKey(KEY_PROGRAM_ARGS)) {
				String[] oldArgs = props.getStringArray(KEY_PROGRAM_ARGS, EMPTY_STRING_ARRAY);
				setProgramArgs(oldArgs);
				props.clearProperty(KEY_PROGRAM_ARGS);
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private final String getProgramArgsValueKey(int index) {
		return KEY_PROGRAM_ARGS_VAL + Integer.toString(index+1);
	}
}
