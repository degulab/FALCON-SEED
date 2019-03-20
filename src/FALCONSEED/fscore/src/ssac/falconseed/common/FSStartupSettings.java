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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FSStartupSettings.java	2.1.0	2013/08/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FSStartupSettings.java	2.00	2012/09/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FSStartupSettings.java	1.22	2012/07/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FSStartupSettings.java	1.13	2012/02/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FSStartupSettings.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FSStartupSettings.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.common;

import java.io.File;
import java.util.Locale;

import ssac.util.Classes;
import ssac.util.Strings;
import ssac.util.properties.ExProperties;
import ssac.util.properties.JavaXmlPropertiesModel;

/**
 * FALCON-SEED 起動用共通パラメータ
 * 
 * @version 2.1.0	2013/08/13
 */
public class FSStartupSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	static public final String DEBUG_OPTION = "-debug";
	static public final String VERBOSE_OPTION = "-verbose";
	static public final String LANGUAGE_OPTION = "-language";
	static public final String ADVANCED_OPTION = "-advanced";

	static public final String SYSPROP_CONFIG_DIR  = "config.dir";
	static public final String SYSPROP_LOGGING_DIR = "logging.dir";
	static public final String SYSPROP_MEMORY_SIZE = "memory.size";
	
	static public final String SYSPROP_USER_ROOT_DIR	= "fs.user.home.dir";
	static public final String SYSPROP_USER_FILTER_DIR	= "fs.user.filter.dir";
	static public final String SYSPROP_USER_DATA_DIR	= "fs.user.data.dir";

	static public final int DEF_MEMORY_SIZE = 128;
	static public final int MAX_MEMORY_SIZE = 1000000;
	
	//static public final String DIRNAME_APPTOP = ".FALCONSEED";
	static public final String DIRNAME_CONFIGTOP = ".FALCONSEED";
	static public final String DIRNAME_USERAREA = "FALCONSEED";
	static private final String DIRNAME_SETTINGS = "settings";
	
	static private final String SYSPROP_USER_HOME = "user.home";
	static private final String SYSPROP_TEMP_DIR  = "java.io.tmpdir";
	
	static private final String PROPERTY_HEADER = "FALCON-SEED startup settings.";
	static private final String PROPERTY_FILENAME = "fsstartup.prefs";

	static private final String KEY_PREFS_APP_LANGUAGE = "app.language";
	static private final String KEY_PREFS_RUNNER_MEMORY_SIZE = "runner.memory.size";
	static private final String KEY_PREFS_MOQUETTE_MEMORY_SIZE = "moquette.memory.size";
	static private final String KEY_PREFS_JAVAPROCCONF_FORCE = "javaproc.config.force";
	static private final String KEY_PREFS_JAVAPROCCONF_VM_ARGS = "javaproc.config.vmargs";
	
	static protected final String APP_HOME = "app.home";
	static protected final String APP_LIB  = "app.lib";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private FSStartupSettings instance = null;
	
	static private boolean optionDebug = false;
	static private boolean optionVerbose = false;
	static private boolean optionAdvanced = false;
	
	static private final Locale	systemDefaultLocale;
	static private String optionLanguage = null;
	
	private final ExProperties props;
	
	static {
		systemDefaultLocale = Locale.getDefault();
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private FSStartupSettings() {
		this.props = new ExProperties(new JavaXmlPropertiesModel());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	//--- KEY_PREFS_RUNNER_MEMORY_SIZE
	
	public boolean isSpecifiedRunnerMemorySize() {
		try {
			Integer memsize = props.getInteger(KEY_PREFS_RUNNER_MEMORY_SIZE, null);
			return (memsize!=null && memsize.intValue() > 0);
		}
		catch (Throwable ex) {
			return false;
		}
	}
	
	public int getRunnerMemorySize() {
		try {
			Integer memsize = props.getInteger(KEY_PREFS_RUNNER_MEMORY_SIZE, null);
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
	
	public void setRunnerMemorySize(int value) {
		if (value >= 0) {
			if (value > MAX_MEMORY_SIZE)
				value = MAX_MEMORY_SIZE;
			props.setInteger(KEY_PREFS_RUNNER_MEMORY_SIZE, Integer.valueOf(value));
		} else {
			props.clearProperty(KEY_PREFS_RUNNER_MEMORY_SIZE);
		}
	}
	
	//--- KEY_PREFS_MOQUETTE_MEMORY_SIZE
	
	public boolean isSpecifiedMoquetteMemorySize() {
		try {
			Integer memsize = props.getInteger(KEY_PREFS_MOQUETTE_MEMORY_SIZE, null);
			return (memsize!=null && memsize.intValue() > 0);
		}
		catch (Throwable ex) {
			return false;
		}
	}
	
	public int getMoquetteMemorySize() {
		try {
			Integer memsize = props.getInteger(KEY_PREFS_MOQUETTE_MEMORY_SIZE, null);
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

	public void setMoquetteMemorySize(int value) {
		if (value >= 0) {
			if (value > MAX_MEMORY_SIZE)
				value = MAX_MEMORY_SIZE;
			props.setInteger(KEY_PREFS_MOQUETTE_MEMORY_SIZE, Integer.valueOf(value));
		} else {
			props.clearProperty(KEY_PREFS_MOQUETTE_MEMORY_SIZE);
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
	// Public interfaces for Locale
	//------------------------------------------------------------
	
	//--- KEY_PREFS_APP_LANGUAGE
	public boolean isSpecifiedAppLanguage() {
		String lang = props.getString(KEY_PREFS_APP_LANGUAGE, null);
		return (!Strings.isNullOrEmpty(lang));
	}
	
	public String getAppLanguage() {
		return props.getString(KEY_PREFS_APP_LANGUAGE, null);
	}
	
	public void setAppLanguage(String lang) {
		if (!Strings.isNullOrEmpty(lang)) {
			props.setString(KEY_PREFS_APP_LANGUAGE, lang);
		} else {
			props.clearProperty(KEY_PREFS_APP_LANGUAGE);
		}
	}
	
	static public boolean isSpecifiedLocalLanguageOption() {
		return (optionLanguage != null);
	}
	
	static public String getLocalLanguageOption() {
		return optionLanguage;
	}
	
	static public void setLocalLanguageOption(String language) {
		if (language != null && language.length() > 0)
			optionLanguage = language;
		else
			optionLanguage = null;
	}
	
	static public Locale getAvailableAppLocale() {
		if (optionLanguage != null) {
			return new Locale(optionLanguage);
		}
		
		if (instance != null) {
			String lang = instance.getAppLanguage();
			if (!Strings.isNullOrEmpty(lang)) {
				return new Locale(lang);
			}
		}
		
		return Locale.getDefault();
	}
	
	static public Locale getDefaultLocaleWhenStartup() {
		return systemDefaultLocale;
	}
	
	static public boolean isDefaultLocaleWhenStartup(final Locale locale) {
		return (systemDefaultLocale.equals(locale));
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
		return System.getProperty(APP_HOME);
	}
	
	static public final File getHomeDirFile() {
		return new File(getHomeDirAbsolutePath());
	}
	
	static public final String getLibDirAbsolutePath() {
		return System.getProperty(APP_LIB);
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
	
	static public boolean isSpecifiedAdvancedOption() {
		return optionAdvanced;
	}
	
	static public void setAdvancedOption(boolean enableAdvanced) {
		optionAdvanced = enableAdvanced;
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
		File dpath = new File(dir, DIRNAME_CONFIGTOP);
		dpath = new File(dpath, DIRNAME_SETTINGS);
		
		// create File class instance
		return new File(dpath, propFilename);
	}
	
	static public String getLoggingDirProperty() {
		return System.getProperty(SYSPROP_LOGGING_DIR);
	}
	
	static public String getUserRootDirProperty() {
		return System.getProperty(SYSPROP_USER_ROOT_DIR);
	}
	
	static public String getAvailableUserRootDir() {
		String strPath = getUserRootDirProperty();
		if (Strings.isNullOrEmpty(strPath)) {
			strPath = System.getProperty(SYSPROP_USER_HOME);
		}
		return strPath;
	}
	
	static public String getUserFilterDirProperty() {
		return System.getProperty(SYSPROP_USER_FILTER_DIR);
	}
	
	static public String getUserDataDirProperty() {
		return System.getProperty(SYSPROP_USER_DATA_DIR);
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
	 * 起動設定から Runner のヒープサイズを取得する。
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
		if (instance.isSpecifiedRunnerMemorySize()) {
			int iSize = instance.getRunnerMemorySize();
			if (iSize > 0) {
				return iSize;
			}
		}
		
		// 定義なし
		return 0;
	}
	
	static public FSStartupSettings getInstance() {
		return instance;
	}
	
	static public void initialize() {
		// インスタンス生成
		instance = new FSStartupSettings();
		
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
		String strHomeDir = System.getProperty(APP_HOME);
		try {
			if (!Strings.isNullOrEmpty(strHomeDir)) {
				File dirHome = new File(strHomeDir);
				if (dirHome.exists() && dirHome.isDirectory()) {
					String strLibDir = System.getProperty(APP_LIB);
					if (!Strings.isNullOrEmpty(strLibDir)) {
						File dirLib = new File(strLibDir);
						if (dirLib.exists() && dirLib.isDirectory()) {
							flgExistHome = true;
						}
					}
					else {
						File dirLib = new File(dirHome, "lib");
						if (dirLib.exists() && dirLib.isDirectory()) {
							System.setProperty(APP_LIB, dirLib.getAbsolutePath());
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
			System.setProperty(APP_LIB, fLib.getAbsolutePath());

			// Application home
			File fHome = fLib.getParentFile();
			System.setProperty(APP_HOME, fHome.getAbsolutePath());
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
