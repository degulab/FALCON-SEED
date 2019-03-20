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
 * @(#)StartupSettings.java	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)StartupSettings.java	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.common;

import java.io.File;
import java.util.Locale;

import ssac.util.Classes;
import ssac.util.Strings;
import ssac.util.properties.ExProperties;
import ssac.util.properties.JavaXmlPropertiesModel;

/**
 * AADL開発環境起動用パラメータ
 * 
 * @version 2012/07/02
 * 
 * @since 2009/12/09
 */
public class StartupSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static public final String VERBOSE_OPTION = "-verbose";
	static public final String DEBUG_OPTION = "-debug";
	static public final String LANGUAGE_OPTION = "-language";
	
	static public final String SYSPROP_CONFIG_DIR  = "config.dir";
	static public final String SYSPROP_LOGGING_DIR = "logging.dir";
	static public final String SYSPROP_MEMORY_SIZE = "aadleditor.memory.size";

	static public final int DEF_MEMORY_SIZE = 128;
	static public final int MAX_MEMORY_SIZE = 1000000;
	
	static private final String DIRNAME_AADLEDITOR = ".AADLEditor";
	
	static private final String SYSPROP_USER_HOME = "user.home";
	static private final String SYSPROP_TEMP_DIR  = "java.io.tmpdir";
	
	static private final String PROPERTY_HEADER = "AADLEditor startup settings.";
	static private final String PROPERTY_FILENAME = "startup.prefs";

	static private final String KEY_PREFS_MANAGER_MEMORY_SIZE = "manager.memory.size";
	static private final String KEY_PREFS_EDITOR_MEMORY_SIZE = "editor.memory.size";
	static private final String KEY_PREFS_JAVAPROCCONF_FORCE = "javaproc.config.force";
	static private final String KEY_PREFS_JAVAPROCCONF_VM_ARGS = "javaproc.config.vmargs";
	
	static protected final String AADLEditor_HOME = "aadl.editor.home";
	static protected final String AADLEditor_LIB  = "aadl.editor.lib";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private StartupSettings instance = null;
	
	static private String optionLanguage = null;
	static private boolean optionDebug = false;
	static private boolean optionVerbose = false;
	
	private final ExProperties props;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private StartupSettings() {
		this.props = new ExProperties(new JavaXmlPropertiesModel());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	//--- KEY_PREFS_MANAGER_MEMORY_SIZE
	
	public boolean isSpecifiedManagerMemorySize() {
		try {
			Integer memsize = props.getInteger(KEY_PREFS_MANAGER_MEMORY_SIZE, null);
			return (memsize!=null && memsize.intValue() > 0);
		}
		catch (Throwable ex) {
			return false;
		}
	}
	
	public int getManagerMemorySize() {
		try {
			Integer memsize = props.getInteger(KEY_PREFS_MANAGER_MEMORY_SIZE, null);
			if (memsize!=null && memsize.intValue() > 0) {
				if (memsize.intValue() > MAX_MEMORY_SIZE)
					return MAX_MEMORY_SIZE;
				else
					return memsize.intValue();
			} else {
				return 0;
			}
		}
		catch (Throwable ex) {
			ex = null;
			return 0;
		}
	}
	
	public void setManagerMemorySize(int value) {
		if (value >= 0) {
			if (value > MAX_MEMORY_SIZE)
				value = MAX_MEMORY_SIZE;
			props.setInteger(KEY_PREFS_MANAGER_MEMORY_SIZE, Integer.valueOf(value));
		} else {
			props.clearProperty(KEY_PREFS_MANAGER_MEMORY_SIZE);
		}
	}
	
	//--- KEY_PREFS_EDITOR_MEMORY_SIZE
	
	public boolean isSpecifiedEditorMemorySize() {
		try {
			Integer memsize = props.getInteger(KEY_PREFS_EDITOR_MEMORY_SIZE, null);
			return (memsize!=null && memsize.intValue() > 0);
		}
		catch (Throwable ex) {
			return false;
		}
	}
	
	public int getEditorMemorySize() {
		try {
			Integer memsize = props.getInteger(KEY_PREFS_EDITOR_MEMORY_SIZE, null);
			if (memsize!=null && memsize.intValue() > 0) {
				if (memsize.intValue() > MAX_MEMORY_SIZE)
					return MAX_MEMORY_SIZE;
				else
					return memsize.intValue();
			} else {
				return 0;
			}
		}
		catch (Throwable ex) {
			ex = null;
			return 0;
		}
	}
	
	public void setEditorMemorySize(int value) {
		if (value >= 0) {
			if (value > MAX_MEMORY_SIZE)
				value = MAX_MEMORY_SIZE;
			props.setInteger(KEY_PREFS_EDITOR_MEMORY_SIZE, Integer.valueOf(value));
		} else {
			props.clearProperty(KEY_PREFS_EDITOR_MEMORY_SIZE);
		}
	}
	
	//--- KEY_PREFS_JAVAPROCCONF_FORCE
	
	public boolean isForceJavaProcessConfig() {
		return props.getBooleanValue(KEY_PREFS_JAVAPROCCONF_FORCE);
	}
	
	public void setForceJavaProcessConfig(boolean toForce) {
		if (toForce) {
			props.setBooleanValue(KEY_PREFS_JAVAPROCCONF_FORCE, true);
		} else {
			props.clearProperty(KEY_PREFS_JAVAPROCCONF_FORCE);
		}
	}
	
	//--- KEY_PREFS_JAVAPROCCONF_VM_ARGS
	public boolean isSpecifiedJavaProcessVMArgs() {
		String vmargs = props.getString(KEY_PREFS_JAVAPROCCONF_VM_ARGS, null);
		return (!Strings.isNullOrEmpty(vmargs));
	}
	
	public String getJavaProcessVMArgs() {
		return props.getString(KEY_PREFS_JAVAPROCCONF_VM_ARGS, null);
	}
	
	public void setJavaProcessVMArgs(String vmargs) {
		if (!Strings.isNullOrEmpty(vmargs)) {
			props.setString(KEY_PREFS_JAVAPROCCONF_VM_ARGS, vmargs);
		} else {
			props.clearProperty(KEY_PREFS_JAVAPROCCONF_VM_ARGS);
		}
	}

	//------------------------------------------------------------
	// Public Helper interfaces
	//------------------------------------------------------------
	
	static public String getLoadErrorMessage(File targetFile) {
		return "Failed to load properties from \"" + (targetFile != null ? targetFile : "null") + "\"";
	}
	
	static public String getSaveErrorMessage(File targetFile) {
		return "Failed to save properties to \"" + (targetFile != null ? targetFile : "null") + "\"";
	}

	static public final String getHomeDirAbsolutePath() {
		return System.getProperty(AADLEditor_HOME);
	}
	
	static public final File getHomeDirFile() {
		return new File(getHomeDirAbsolutePath());
	}
	
	static public final String getLibDirAbsolutePath() {
		return System.getProperty(AADLEditor_LIB);
	}
	
	static public final File getLibDirFile() {
		return new File(getLibDirAbsolutePath());
	}
	
	static public boolean isVerboseMode() {
		return (optionVerbose || optionDebug);
	}
	
	static public boolean isSpecifiedVerboseOption() {
		return optionVerbose;
	}
	
	static public void setVerboseOption(boolean enableVerbose) {
		optionVerbose = enableVerbose;
	}
	
	static public boolean isSpecifiedDebugOption() {
		return optionDebug;
	}
	
	static public void setDebugOption(boolean enableDebug) {
		optionDebug = enableDebug;
	}
	
	static public String getAvailableLanguage() {
		if (optionLanguage == null) {
			String lang = Locale.getDefault().getLanguage();
			return (lang != null && lang.length() > 0 ? lang : null);
		} else {
			return optionLanguage;
		}
	}
	
	static public boolean isSpecifiedLanguageOption() {
		return (optionLanguage != null);
	}
	
	static public String getLanguageOption() {
		return optionLanguage;
	}
	
	static public void setLanguageOption(String language) {
		if (language != null && language.length() > 0)
			optionLanguage = language;
		else
			optionLanguage = null;
	}
	
	static public String getConfigDirProperty() {
		return System.getProperty(SYSPROP_CONFIG_DIR);
	}
	
	static public String getAvailableConfigDir() {
		return getSystemDirProperty(SYSPROP_CONFIG_DIR);
	}
	
	static public File getConfigFile(String propFilename) {
		// get directory for property file
		String dir = getSystemDirProperty(SYSPROP_CONFIG_DIR);
		File dpath = new File(dir, DIRNAME_AADLEDITOR);
		
		// create File class instance
		return new File(dpath, propFilename);
	}
	
	static public String getLoggingDirProperty() {
		return System.getProperty(SYSPROP_LOGGING_DIR);
	}
	
	static public Integer getMemorySizeProperty() {
		String strMemsize = System.getProperty(SYSPROP_MEMORY_SIZE);
		if (Strings.isNullOrEmpty(strMemsize)) {
			return null;
		}
		// conversion
		Integer retValue;
		try {
			retValue = Integer.valueOf(strMemsize);
			if (retValue.intValue() < 0)
				retValue = Integer.valueOf(0);
			else if (retValue.intValue() > MAX_MEMORY_SIZE)
				retValue = Integer.valueOf(MAX_MEMORY_SIZE);
		}
		catch (Throwable ex) {
			retValue = null;
		}
		return retValue;
	}

	/**
	 * 起動設定から AADLEditor のヒープサイズを取得する。
	 * このメソッドが返す値は、MB単位の数値とする。
	 * 有効なサイズが指定されていない場合は 0 を返す。
	 * 
	 * @return	MB単位のヒープサイズ
	 */
	static public int getAvailableMemorySize() {
		// プロパティを優先
		Integer memSize = getMemorySizeProperty();
		if (memSize != null && memSize.intValue() > 0) {
			return memSize;
		}
		
		// プロパティファイルのエントリ取得
		if (instance.isSpecifiedEditorMemorySize()) {
			int iSize = instance.getEditorMemorySize();
			if (iSize > 0) {
				return iSize;
			}
		}
		
		// 定義なし
		return 0;
	}
	
	static public StartupSettings getInstance() {
		return instance;
	}
	
	static public void initialize() {
		// インスタンス生成
		instance = new StartupSettings();
		
		// デフォルトプロパティの初期化
		
		// ファイルパス
		File propFile = getConfigFile(PROPERTY_FILENAME);
		
		// プロパティのロード
		try {
			instance.props.loadFile(propFile);
		}
		catch (Throwable ex) {
			printLoadErrorMessage(propFile, ex);
			ex = null;
		}
		
		// Java情報の更新
	}
	
	public static void flush()
	{
		assert instance != null : "Startup Settings must be initialize!";
		
		// ファイルパス
		File propFile = getConfigFile(PROPERTY_FILENAME);
		
		// プロパティのセーブ
		try {
			instance.props.saveFile(propFile, PROPERTY_HEADER);
		}
		catch (Throwable ex) {
			printSaveErrorMessage(propFile, ex);
			ex = null;
		}
	}

	/**
	 * アプリケーションの位置情報(アプリケーションのルートディレクトリ)を、
	 * 指定されたクラスが配置されているディレクトリから取得する。
	 * @param targetClass	位置を取得するための基準となるアプリケーションのクラス
	 */
	static public final void initApplicationHome(Class<?> targetClass) {
		boolean flgExistHome = false;
		
		// Check Properties
		String strHomeDir = System.getProperty(AADLEditor_HOME);
		try {
			if (!Strings.isNullOrEmpty(strHomeDir)) {
				File dirHome = new File(strHomeDir);
				if (dirHome.exists() && dirHome.isDirectory()) {
					String strLibDir = System.getProperty(AADLEditor_LIB);
					if (!Strings.isNullOrEmpty(strLibDir)) {
						File dirLib = new File(strLibDir);
						if (dirLib.exists() && dirLib.isDirectory()) {
							flgExistHome = true;
						}
					}
					else {
						File dirLib = new File(dirHome, "lib");
						if (dirLib.exists() && dirLib.isDirectory()) {
							System.setProperty(AADLEditor_LIB, dirLib.getAbsolutePath());
							flgExistHome = true;
						}
					}
				}
			}
		}
		catch (Throwable ignoreEx) {
			flgExistHome = false;
		}
		
		// get Home and Lib directories by target class location
		if (!flgExistHome) {
			// Libraries home
			File fLib = Classes.getClassSource(targetClass);
			if (isSpecifiedDebugOption()) {
				if (fLib != null) {
					System.out.println("[Debug] StartupSetting#initApplicationHome : "
							+ targetClass.getName() + " location = "
							+ fLib.getAbsolutePath());
				} else {
					System.out.println("[Debug] StartupSetting#initApplicationHome : "
							+ targetClass.getName() + " location = null");
				}
			}
			if (fLib != null && fLib.exists()) {
				if (Strings.endsWithIgnoreCase(fLib.getName(), ".jar")) {
					// this file is .jar file on "lib" directory.
					fLib = fLib.getParentFile();
				} else {
					// this file is .class file on "AADLEditor/bin" directory.
					fLib = new File(fLib.getParentFile(), "lib");
				}
			} else {
				File fCur = new File("");
				try {
					fLib = fCur.getCanonicalFile();
				} catch (Throwable ignoreEx) {
					fLib = fCur.getAbsoluteFile();
				}
				if (!"lib".equalsIgnoreCase(fLib.getName())) {
					fCur = new File("lib");
					try {
						fLib = fCur.getCanonicalFile();
					} catch (Throwable ignoreEx) {
						fLib = fCur.getAbsoluteFile();
					}
				}
			}
			System.setProperty(AADLEditor_LIB, fLib.getAbsolutePath());

			// Application home
			File fHome = fLib.getParentFile();
			System.setProperty(AADLEditor_HOME, fHome.getAbsolutePath());
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private String getSystemDirProperty(String name) {
		String dir = System.getProperty(name);
		if (Strings.isNullOrEmpty(dir)) {
			dir = System.getProperty(SYSPROP_USER_HOME);
			if (Strings.isNullOrEmpty(dir)) {
				dir = System.getProperty(SYSPROP_TEMP_DIR);
			}
		}
		return dir;
	}
	
	static protected void printLoadErrorMessage(File targetFile, Throwable ex) {
		String msg = "[Warning] Failed to load properties from \"" + (targetFile != null ? targetFile : "null") + "\"";
		if (ex != null) {
			msg = msg + "\n    " + ex.toString();
		}
		System.err.println(msg);
	}
	
	static protected void printSaveErrorMessage(File targetFile, Throwable ex) {
		String msg = "[Warning] Failed to save properties to \"" + (targetFile != null ? targetFile : "null") + "\"";
		if (ex != null) {
			msg = msg + "\n    " + ex.toString();
		}
		System.err.println(msg);
	}
}
