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
 * @(#)AadlJarProperties.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import java.io.File;
import java.io.IOException;

import ssac.aadl.module.ModuleArgDetail;
import ssac.aadl.module.ModuleArgType;
import ssac.util.Strings;
import ssac.util.properties.ExProperties;
import ssac.util.properties.JavaXmlPropertiesModel;

/**
 * AADL実行モジュール(JARファイル)に含めるプロパティ。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class AadlJarProperties extends ExProperties
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static private final String AADL_PROFILE  = "aadl.jar.";
	static private final String KEY_REVISION  = AADL_PROFILE + ".revision";
	static private final String KEY_SRCNAME   = AADL_PROFILE + ".srcfilename";
	static private final String KEY_TITLE     = AADL_PROFILE + ".title";
	static private final String KEY_DESC      = AADL_PROFILE + ".description";
	static private final String KEY_NOTE      = AADL_PROFILE + ".note";
	static private final String KEY_ARGS      = AADL_PROFILE + ".args";
	static private final String KEY_ARGS_NUM  = KEY_ARGS + ".num";
	static private final String KEY_ARGS_TYPE = KEY_ARGS + ".type";
	static private final String KEY_ARGS_DESC = KEY_ARGS + ".desc";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AadlJarProperties() {
		super(new JavaXmlPropertiesModel());
	}
	
	public AadlJarProperties(File srcFile, CompileSettings settings) {
		super(new JavaXmlPropertiesModel());
		setSourceFilename(srcFile.getName());
		setRevision(settings.getNextRevision());
		setTitle(settings.getTitle());
		setDescription(settings.getDescription());
		setNote(settings.getNote());
		setArgumentDetails(settings.getArgumentDetails());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このプロパティをテンポラリファイルに保存する。
	 * このメソッドで作成されたテンポラリファイルは、終了時に破棄されるよう
	 * マークされる。
	 * @return	生成されたファイルの位置を示す抽象パス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public File saveToTempFile() throws IOException
	{
		File tmpFile = File.createTempFile("AadlJarProp", ".xml");
		tmpFile.deleteOnExit();
		saveFile(tmpFile);
		return tmpFile;
	}
	
	//
	// Revision
	//
	
	public int getRevision() {
		return getInteger(KEY_REVISION, Integer.valueOf(0));
	}
	
	public void setRevision(int value) {
		if (value < 0)
			throw new IllegalArgumentException("'value' must greater than or equal 0 : " + value);
		if (value > 0)
			setInteger(KEY_REVISION, value);
		else
			clearProperty(KEY_REVISION);
	}

	//
	// Source Filename
	//
	
	public String getSourceFilename() {
		return getString(KEY_SRCNAME, null);
	}
	
	public void setSourceFilename(String name) {
		if (Strings.isNullOrEmpty(name)) {
			clearProperty(KEY_SRCNAME);
		} else {
			setString(KEY_SRCNAME, name);
		}
	}
	
	//
	// Title
	//
	
	public String getTitle() {
		return getString(KEY_TITLE, null);
	}
	
	public void setTitle(String title) {
		if (Strings.isNullOrEmpty(title)) {
			clearProperty(KEY_TITLE);
		} else {
			setString(KEY_TITLE, title);
		}
	}
	
	//
	// Description
	//
	
	public String getDescription() {
		return getString(KEY_DESC, null);
	}
	
	public void setDescription(String description) {
		if (Strings.isNullOrEmpty(description)) {
			clearProperty(KEY_DESC);
		} else {
			setString(KEY_DESC, description);
		}
	}
	
	//
	// Note
	//
	
	public String getNote() {
		return getString(KEY_NOTE, null);
	}
	
	public void setNote(String note) {
		if (Strings.isNullOrEmpty(note)) {
			clearProperty(KEY_NOTE);
		} else {
			setString(KEY_NOTE, note);
		}
	}
	
	//
	// Program argument details
	//
	
	public ModuleArgDetail[] getArgumentDetails() {
		int numDetails = getInteger(KEY_ARGS_NUM, Integer.valueOf(0));
		ModuleArgDetail[] details = new ModuleArgDetail[numDetails];
		for (int index = 0; index < numDetails; index++) {
			String strType = getString(getArgsTypeKey(index), null);
			String strDesc = getString(getArgsDescKey(index), null);
			ModuleArgType argType = ModuleArgType.fromName(strType);
			details[index] = new ModuleArgDetail(argType, strDesc);
		}
		return details;
	}
	
	public void setArgumentDetails(ModuleArgDetail[] details) {
		int numDetails = getInteger(KEY_ARGS_NUM, Integer.valueOf(0));
		int newNumDetails = 0;
		int index = 0;
		//--- 指定の情報を保存
		if (details != null && details.length > 0) {
			newNumDetails = details.length;
			for (; index < newNumDetails; index++) {
				ModuleArgDetail arg = details[index];
				if (arg == null) {
					clearProperty(getArgsTypeKey(index));
					clearProperty(getArgsDescKey(index));
				} else {
					//--- type
					if (arg.type() == null)
						clearProperty(getArgsTypeKey(index));
					else
						setString(getArgsTypeKey(index), arg.type().typeName());
					//--- description
					if (Strings.isNullOrEmpty(arg.description()))
						clearProperty(getArgsDescKey(index));
					else
						setString(getArgsDescKey(index), arg.description());
				}
			}
		}
		//--- 不要なプロパティを破棄
		for (; index < numDetails; index++) {
			clearProperty(getArgsTypeKey(index));
			clearProperty(getArgsDescKey(index));
		}
		//--- 総数を保存
		setInteger(KEY_ARGS_NUM, newNumDetails);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected final String getArgsTypeKey(int index) {
		return (KEY_ARGS_TYPE + Integer.toString(index+1));
	}
	
	static protected final String getArgsDescKey(int index) {
		return (KEY_ARGS_DESC + Integer.toString(index+1));
	}
}
