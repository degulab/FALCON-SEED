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
 * @(#)MacroExecSettings.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import java.io.File;

import ssac.util.Strings;

/**
 * AADLマクロ実行設定情報を保持するクラス。
 * ビルドオプションダイアログの実行オプション・パネルにより設定される
 * マクロ専用の情報を操作するための機能を提供する。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class MacroExecSettings extends ModuleSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String SECTION = "MacroExecute";
	
	static protected final String GROUP_PROGRAM  = SECTION + ".Program";
	static protected final String GROUP_JAVAVM   = SECTION + ".JavaVM";
	
	static protected final String KEY_PROGRAM_ARGS = GROUP_PROGRAM + ".args";
	static protected final String KEY_PROGRAM_ARGS_NUM = KEY_PROGRAM_ARGS + ".num";
	static protected final String KEY_PROGRAM_ARGS_VAL = KEY_PROGRAM_ARGS + ".value";
	
	static protected final String KEY_JAVAVM_ARGS = GROUP_JAVAVM + ".args";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroExecSettings() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasTargetFile() {
		return (getVirtualPropertyFile() != null);
	}
	
	public File getTargetFile() {
		File fp = getJavaPropertyFile();
		if (fp == null) {
			return null;
		} else {
			String path = fp.getPath();
			return new File(path.substring(0, (path.length() - FILE_EXT.length())));
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
	}
	
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
	}
	
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private final String getProgramArgsValueKey(int index) {
		return KEY_PROGRAM_ARGS_VAL + Integer.toString(index+1);
	}
}
