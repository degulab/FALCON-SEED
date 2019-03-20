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
 * @(#)AppSettings.java	1.21	2012/08/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	1.14	2009/12/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	1.10	2008/12/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.setting;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.UIManager;

import ssac.aadl.common.StartupSettings;
import ssac.aadl.editor.AADLEditor;
import ssac.aadl.macro.AADLMacroEngine;
import ssac.aadl.module.setting.AbstractSettings;
import ssac.aadl.module.setting.CompileSettings;
import ssac.util.Strings;
import ssac.util.io.JavaInfo;
import ssac.util.logging.AppLogger;
import ssac.util.properties.ExConfiguration;
import ssac.util.properties.JavaXmlPropertiesModel;
import ssac.util.swing.TextEditorPane;

/**
 * アプリケーションの設定情報。
 * 設定ダイアログにより設定される情報を操作するための機能を提供する。
 * 
 * @version 1.21	2012/08/21
 */
public class AppSettings
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final String SECTION_STAT	= "Status";
	static public final String SECTION_PREFS	= "Preferences";
	
	//--- target

	static public final String DOCUMENT			= SECTION_STAT + ".Document";
	static public final String JARMODULE			= SECTION_STAT + ".JarModule";
	static public final String MAINFRAME			= SECTION_STAT + ".MainFrame";
	static public final String OUTER_FRAME		= MAINFRAME + ".outer";
	static public final String INNER_FRAME		= MAINFRAME + ".inner";
	static public final String PROP_FRAME			= MAINFRAME + ".property";
	static public final String PREFERENCE_DLG		= SECTION_STAT + ".PreferenceDlg";
	static public final String BUILDOPTION_DLG	= SECTION_STAT + ".BuildOptionDlg";
	static public final String JUMP_DLG			= SECTION_STAT + ".JumpDlg";
	static public final String FIND_DLG			= SECTION_STAT + ".FindDlg";
	static public final String COMPILEALLFOLDER_DLG = SECTION_STAT + ".CompileAllFolderDlg";
	static public final String WORKSPACE_CHOOSER	= SECTION_STAT + ".WorkspaceChooser";

	static public final String EDITOR		= SECTION_PREFS + ".EditorPanel";
	static public final String CONSOLE	= SECTION_PREFS + ".ConsolePanel";
	static public final String COMPILE	= SECTION_PREFS + ".CompilePanel";
	
	static public final String PACKBASE_CHOOSER = SECTION_STAT + ".PackageBaseChooser";

	//--- status
	
	static public final String KEY_STAT_LOOK_AND_FEEL	= SECTION_STAT + ".LookAndFeelClass";
	
	//--- settings
	
	static public final String KEY_PREFS_JAVAHOME_PATH	= SECTION_PREFS + ".JavaHome" + ".path";
	static public final String KEY_PREFS_JAVAVM_OPTIONS	= SECTION_PREFS + ".JavaVM" + ".vmArgs";
	static public final String KEY_PREFS_COMPILER_VMARGS	= SECTION_PREFS + ".Compiler" + ".vmArgs";
	
	//static public final String KEY_PREFS_JAVAC_JAR		= CompileSettings.KEY_JAVAC_JAR;
	//static private final String KEY_PREFS_ENCODING_NAME	= CompileSettings.KEY_ENCODING_NAME;
	//static public final String KEY_PREFS_JAVACMD_PATH		= ExecSettings.KEY_JAVACMD_PATH;
	
	//static public final String KEY_PREFS_ENCODING_AADL_SRC	= CompileSettings.KEY_ENCODING_NAME;
	static public final String OLD_KEY_PREFS_ENCODING_AADL_SRC = "Compile.Encoding.name";
	static public final String KEY_PREFS_ENCODING_AADL_SRC	= "aadl.source.encoding";
	static public final String KEY_PREFS_ENCODING_AADL_MACRO	= "aadl.macro.csv.encoding";
	static public final String KEY_PREFS_ENCODING_AADL_CSV	= "aadl.csv.encoding";
	static public final String KEY_PREFS_ENCODING_AADL_TXT	= "aadl.txt.encoding";
	
	static public final String KEY_PREFS_LAST_WORKSPACE	= "editor.workspace.latest";
	static public final String KEY_PREFS_WORKSPACE_LIST	= "editor.workspace.list";

	/*--- delete : 1.10 2008/12/05
	static private final String[] DEF_COMPILE_LIBS = new String[]{
		"antlr-runtime-3.0.1.jar",
		"Exalge2.jar",
		"aadlc.jar",
	};
	static private final String[] DEF_EXEC_LIBS = new String[]{
		"Exalge2.jar"
	};
	--- delete : 1.10 2008/12/05 */
	
	//--- local settings
	private static final String PROPERTY_HEADER = "AADLEditor settings.";
	private static final String PROPERTY_NAME = "settings.prefs";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private JavaInfo	defJavaInfo;

	static private String[]	aryDefaultCompileClassPath = null;
	static private String[]	aryDefaultExecClassPath = null;
	static private String		defineFileEncoding = null;

	private static AppSettings instance = null;
	
	private final ExConfiguration props;
	
	private JavaInfo	userJavaInfo;

	//------------------------------------------------------------
	// Static interfaces
	//------------------------------------------------------------
	
	public static AppSettings getInstance() {
		return instance;
	}
	
	public static void initialize()
	{
		// 'file.encoding' プロパティの設定
		//--- for Apple Mac
		{
			String osname = System.getProperty("os.name");
			if (0 <= osname.indexOf("Mac")) {
				defineFileEncoding = "-Dfile.encoding=UTF-8";
			} else {
				defineFileEncoding = null;
			}
		}
		
		// インスタンス生成
		instance = new AppSettings();
		
		// デフォルトプロパティの初期化
		
		// ファイルパス
		File propFile = getFile();
		
		// プロパティのロード
		try {
			instance.props.loadFile(propFile);
		}
		catch (Throwable ex) {
			AppLogger.debug(AbstractSettings.getLoadErrorMessage(propFile), ex);
		}
		
		// 旧プロパティのコンバート
		// OLD_KEY_PREFS_ENCODING_AADL_SRC -> KEY_PREFS_ENCODING_AADL_SRC
		{
			String strValue = instance.props.getString(KEY_PREFS_ENCODING_AADL_SRC, null);
			if (Strings.isNullOrEmpty(strValue)) {
				strValue = instance.props.getString(OLD_KEY_PREFS_ENCODING_AADL_SRC, null);
				if (!Strings.isNullOrEmpty(strValue)) {
					instance.props.setString(KEY_PREFS_ENCODING_AADL_SRC, strValue);
				}
			}
			instance.props.clearProperty(OLD_KEY_PREFS_ENCODING_AADL_SRC);
		}
		
		// Java情報の更新
		refreshDefaultJavaInfo();
		instance.refreshUserJavaInfo();
	}
	
	public static void flush() throws IOException
	{
		assert instance != null : "Application Settings must be initialize!";
		
		// ファイルパス
		File propFile = getFile();
		
		// 旧プロパティの消去
		instance.props.clearProperty(OLD_KEY_PREFS_ENCODING_AADL_SRC);
		
		// プロパティのセーブ
		instance.props.saveFile(propFile, PROPERTY_HEADER);
	}
	
	public static String getPropertyFilePath() {
		File propFile = getFile();
		if (propFile != null) {
			return propFile.getAbsolutePath();
		} else {
			return null;
		}
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected static File getFile() {
		/*
		// get directory for property file
		String dir = System.getProperty("user.home");
		if (Strings.isNullOrEmpty(dir)) {
			dir = System.getProperty("java.io.tmpdir");
		}
		File dpath = new File(dir, ".AADLEditor");
		
		// create File class instance
		return new File(dpath, PROPERTY_NAME);
		*/
		
		return StartupSettings.getConfigFile(PROPERTY_NAME);
	}
	
	protected AppSettings() {
		this.props = new ExConfiguration(new JavaXmlPropertiesModel());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	// ExConfiguration の取得
	public ExConfiguration getConfiguration() {
		return this.props;
	}

	// コンパイル時のデフォルトライブラリを取得
	public String[] getCompileLibraries() {
		return getDefaultCompileClassPaths();
	}

	// 実行時のデフォルトライブラリを取得
	public String[] getExecLibraries() {
		return getDefaultExecClassPaths();
	}
	
	public String getReadEncodingForAadlSource(CompileSettings settings) {
		return settings.getTargetEncodingName(getAadlSourceEncodingName(), false);
	}
	
	public String getWriteEncodingForAadlSource(CompileSettings settings) {
		return settings.getTargetEncodingName(getAadlSourceEncodingName(), true);
	}

	//------------------------------------------------------------
	// Public interfaces for Java information
	//------------------------------------------------------------

	/**
	 * 動作環境におけるデフォルトのJava情報を更新する。
	 * デフォルトのJava情報は、次のプロパティから取得した
	 * Javaホームパスを基準に更新される。
	 *   (1) "JAVA_HOME" 環境変数
	 *   (2) "java.home" システムプロパティ
	 * ※上記順序の通りに検索する
	 */
	static protected void refreshDefaultJavaInfo() {
		String strPath;
		
		// Environment value
		strPath = JavaInfo.getEnvironmentJavaHome();
		
		// System property value
		if (Strings.isNullOrEmpty(strPath)) {
			strPath = JavaInfo.getSystemPropertyJavaHome();
		}
		
		// create JavaInfo
		defJavaInfo = new JavaInfo(strPath);
		defJavaInfo.collect();
	}

	/**
	 * ユーザ定義のJava情報を更新する。
	 * ユーザ定義のJava情報は、KEY_PREFS_JAVAHOME_PATH キーに
	 * 設定されているプロパティ値のJavaホームパスを基準に
	 * 更新される。
	 *
	 */
	public void refreshUserJavaInfo() {
		if (isSpecifiedJavaHomePath()) {
			this.userJavaInfo = new JavaInfo(getJavaHomePath());
			this.userJavaInfo.collect();
		} else {
			this.userJavaInfo = null;
		}
	}

	/**
	 * 動作環境におけるデフォルトのJava情報を取得する。
	 * このメソッドは必ず値を返す。
	 * 
	 * @return JavaInfo インスタンス
	 */
	public JavaInfo getDefaultJavaInfo() {
		return defJavaInfo;
	}

	/**
	 * ユーザによるJava情報(Javaホームパス)が定義されているかを検証する。
	 * 
	 * @return 定義済みなら true
	 */
	public boolean isSpecifiedUserJavaInfo() {
		return (this.userJavaInfo != null);
	}

	/**
	 * ユーザにより定義されたJava情報を取得する。
	 * 定義されていない場合は null を返す。
	 * 
	 * @return JavaInfo インスタンス
	 */
	public JavaInfo getUserJavaInfo() {
		return this.userJavaInfo;
	}

	/**
	 * 有効なJava情報を取得する。
	 * ユーザ定義のJava情報が存在しない場合は、デフォルトのJava情報を返す。
	 * 
	 * @return JavaInfo インスタンス
	 */
	public JavaInfo getCurrentJavaInfo() {
		return (this.userJavaInfo != null ? this.userJavaInfo : defJavaInfo);
	}

	/**
	 * ユーザ定義のJava情報を設定する。
	 * この情報が設定されると、プロパティ"KEY_PREFS_JAVAHOME_PATH"も
	 * 自動的に更新される。
	 * 
	 * @param newInfo
	 */
	public void setUserJavaInfo(JavaInfo newInfo) {
		this.userJavaInfo = newInfo;
		if (newInfo != null) {
			setJavaHomePath(newInfo.getHomePath());
		} else {
			setJavaHomePath(null);
		}
	}

	/**
	 * 有効なJava情報のバージョン番号を取得する。
	 * バージョンが取得できない場合は null を返す。
	 * 
	 * @return バージョン番号を示す文字列
	 */
	public String getCurrentJavaVersion() {
		return getCurrentJavaInfo().getVersion();
	}

	/**
	 * 有効なJava情報のJavaホームパスを取得する。
	 * 
	 * @return Javaホームパスの絶対パス
	 */
	public File getCurrentJavaHomeFile() {
		return getCurrentJavaInfo().getHomeFile();
	}
	
	/**
	 * 有効なJava情報のJavaホームパスを取得する。
	 * 
	 * @return Javaホームパスの絶対パス
	 */
	public String getCurrentJavaHomePath() {
		return getCurrentJavaInfo().getHomePath();
	}

	/**
	 * 有効なJava情報のコンパイラ・ファイルを取得する。
	 * コンパイラが存在しない場合は null を返す。
	 * 
	 * @return Javaコンパイラの絶対パス
	 */
	public File getCurrentJavaCompilerFile() {
		return getCurrentJavaInfo().getCompilerFile();
	}

	/**
	 * 有効なJava情報のコンパイラ・ファイルを取得する。
	 * コンパイラが存在しない場合は null を返す。
	 * 
	 * @return Javaコンパイラの絶対パス
	 */
	public String getCurrentJavaCompilerPath() {
		return getCurrentJavaInfo().getCompilerPath();
	}

	/**
	 * 有効なJava情報のJavaコマンドを取得する。
	 * Javaコマンドが存在しない場合は null を返す。
	 * 
	 * @return Javaコマンドの絶対パス
	 */
	public File getCurrentJavaCommandFile() {
		return getCurrentJavaInfo().getCommandFile();
	}
	
	/**
	 * 有効なJava情報のJavaコマンドを取得する。
	 * Javaコマンドが存在しない場合は null を返す。
	 * 
	 * @return Javaコマンドの絶対パス
	 */
	public String getCurrentJavaCommandPath() {
		return getCurrentJavaInfo().getCommandPath();
	}

	//------------------------------------------------------------
	// Public interfaces for Preferences
	//------------------------------------------------------------
	
	//--- KEY_STAT_LOOK_AND_FEEL
	
	public String getNeedLookAndFeelClassName() {
		String retValue = this.props.getString(KEY_STAT_LOOK_AND_FEEL, null);
		if (Strings.isNullOrEmpty(retValue)) {
			retValue = null;
		}
		return retValue;
	}
	
	//--- KEYNAME_STATE
	
	public int getWindowState(String prefix) {
		return this.props.getWindowState(prefix);
	}
	
	public void setWindowState(String prefix, int state) {
		this.props.setWindowState(prefix, state);
	}
	
	//--- KEYNAME_LOC
	
	public Point getWindowLocation(String prefix) {
		return this.props.getWindowLocation(prefix);
	}
	
	public void setWindowLocation(String prefix, Point pos) {
		this.props.setWindowLocation(prefix, pos);
	}
	
	//--- KEYNAME_SIZE
	
	public Dimension getWindowSize(String prefix) {
		return this.props.getWindowSize(prefix);
	}
	
	public void setWindowSize(String prefix, Dimension size) {
		this.props.setWindowSize(prefix, size);
	}
	
	//--- KEYNAME_DIVLOC
	
	public int getDividerLocation(String prefix) {
		return this.props.getDividerLocation(prefix);
	}
	
	public void setDividerLocation(String prefix, int pos) {
		this.props.setDividerLocation(prefix, pos);
	}
	
	//--- KEYNAME_LASTFILE
	
	public String getLastFilename(String prefix) {
		return this.props.getLastFilename(prefix);
	}
	
	public void setLastFilename(String prefix, String filename) {
		this.props.setLastFilename(prefix, filename);
	}
	
	public File getLastFile(String prefix) {
		String strPath = this.props.getLastFilename(prefix);
		return (Strings.isNullOrEmpty(strPath) ? null : new File(strPath));
	}
	
	public void setLastFile(String prefix, File file) {
		if (file == null)
			this.props.setLastFilename(prefix, null);
		else
			this.props.setLastFilename(prefix, file.getAbsolutePath());
	}
	
	//--- KEY_PREFS_ENCODING_NAME

	/*
	private boolean isSpecifiedEncodingName() {
		String strEncoding = this.props.getString(KEY_PREFS_ENCODING_NAME, null);
		return (!Strings.isNullOrEmpty(strEncoding));
	}
	
	private String getDefaultEncodingName() {
		return JEncodingComboBox.getDefaultEncoding();
	}
	
	private String getEncodingName() {
		String strEncoding = this.props.getString(KEY_PREFS_ENCODING_NAME, null);
		if (!Strings.isNullOrEmpty(strEncoding)) {
			try {
				Charset cs = Charset.forName(strEncoding);
				if (cs == null || !cs.canEncode()) {
					// default encoding
					strEncoding = getDefaultEncodingName();
				}
			} catch (Throwable ex) {
				// default encoding
				strEncoding = getDefaultEncodingName();
			}
		}
		else {
			// default encoding
			strEncoding = getDefaultEncodingName();
		}
		return strEncoding;
	}
	
	private void setEncodingName(String name) {
		if (name != null && name.length() > 0) {
			this.props.setString(KEY_PREFS_ENCODING_NAME, name);
		} else {
			this.props.clearProperty(KEY_PREFS_ENCODING_NAME);
		}
	}
	*/
	
	//--- KEY_PREFS_ENCODING_???
	
	protected boolean isSpecifiedEncodingName(String propName) {
		String strEncoding = this.props.getString(propName, null);
		return (!Strings.isNullOrEmpty(strEncoding));
	}
	
	protected String getEncodingName(String propName) {
		String strEncoding = this.props.getString(propName, null);
		if (!Strings.isNullOrEmpty(strEncoding)) {
			try {
				Charset cs = Charset.forName(strEncoding);
				if (cs != null && cs.canEncode()) {
					return strEncoding;
				}
			} catch (Throwable ex) {}
		}
		
		return null;
	}
	
	protected void setEncodingName(String propName, String name) {
		if (name != null && name.length() > 0) {
			this.props.setString(propName, name);
		} else {
			this.props.clearProperty(propName);
		}
	}
	
	//--- KEY_PREFS_ENCODING_AADL_SRC
	
	public String getDefaultAadlSourceEncodingName() {
		//return JEncodingComboBox.getDefaultEncoding();
		return "UTF-8";
	}
	
	public boolean isSpecifiedAadlSourceEncodingName() {
		return isSpecifiedEncodingName(KEY_PREFS_ENCODING_AADL_SRC);
	}
	
	public String getAadlSourceEncodingName() {
		String encoding = getEncodingName(KEY_PREFS_ENCODING_AADL_SRC);
		if (Strings.isNullOrEmpty(encoding))
			return getDefaultAadlSourceEncodingName();
		else
			return encoding;
	}
	
	public void setAadlSourceEncodingName(String name) {
		setEncodingName(KEY_PREFS_ENCODING_AADL_SRC, name);
	}
	
	//--- KEY_PREFS_ENCODING_AADL_MACRO
	
	public String getDefaultAadlMacroEncodingName() {
		return AADLMacroEngine.getDefaultMacroEncoding();
	}
	
	public boolean isSpecifiedAadlMacroEncodingName() {
		return isSpecifiedEncodingName(KEY_PREFS_ENCODING_AADL_MACRO);
	}
	
	public String getAadlMacroEncodingName() {
		String encoding = getEncodingName(KEY_PREFS_ENCODING_AADL_MACRO);
		if (Strings.isNullOrEmpty(encoding))
			return getDefaultAadlMacroEncodingName();
		else
			return encoding;
	}
	
	public void setAadlMacroEncodingName(String name) {
		setEncodingName(KEY_PREFS_ENCODING_AADL_MACRO, name);
	}
	
	//--- KEY_PREFS_ENCODING_AADL_CSV
	
	public String getDefaultAadlCsvEncodingName() {
		return "MS932";
	}
	
	public boolean isSpecifiedAadlCsvEncodingName() {
		return isSpecifiedEncodingName(KEY_PREFS_ENCODING_AADL_CSV);
	}
	
	public String getAadlCsvEncodingName() {
		String encoding = getEncodingName(KEY_PREFS_ENCODING_AADL_CSV);
		if (Strings.isNullOrEmpty(encoding))
			return getDefaultAadlCsvEncodingName();
		else
			return encoding;
	}
	
	public void setAadlCsvEncodingName(String name) {
		setEncodingName(KEY_PREFS_ENCODING_AADL_CSV, name);
	}
	
	//--- KEY_PREFS_ENCODING_AADL_TXT
	
	public String getDefaultAadlTxtEncodingName() {
		//return JEncodingComboBox.getDefaultEncoding();
		return "MS932";	// CSV と同じデフォルトにする
	}
	
	public boolean isSpecifiedAadlTxtEncodingName() {
		return isSpecifiedEncodingName(KEY_PREFS_ENCODING_AADL_TXT);
	}
	
	public String getAadlTxtEncodingName() {
		String encoding = getEncodingName(KEY_PREFS_ENCODING_AADL_TXT);
		if (Strings.isNullOrEmpty(encoding))
			return getDefaultAadlTxtEncodingName();
		else
			return encoding;
	}
	
	public void setAadlTxtEncodingName(String name) {
		setEncodingName(KEY_PREFS_ENCODING_AADL_TXT, name);
	}
	
	//--- KEY_PREFS_JAVAHOME_PATH
	
	public boolean isSpecifiedJavaHomePath() {
		return (!Strings.isNullOrEmpty(getJavaHomePath()));
	}
	
	public String getJavaHomePath() {
		return this.props.getString(KEY_PREFS_JAVAHOME_PATH, null);
	}
	
	public void setJavaHomePath(String strPath) {
		if (!Strings.isNullOrEmpty(strPath)) {
			this.props.setString(KEY_PREFS_JAVAHOME_PATH, strPath);
		} else {
			this.props.clearProperty(KEY_PREFS_JAVAHOME_PATH);
		}
	}
	
	//--- KEY_PREFS_JAVAVM_OPTIONS
	
	public String appendJavaVMoptions(String execVMargs) {
		String appVMargs = getJavaVMoptions();
		if (!Strings.isNullOrEmpty(defineFileEncoding)) {
			if (Strings.isNullOrEmpty(appVMargs)) {
				appVMargs = defineFileEncoding;
			} else {
				appVMargs = defineFileEncoding + " " + appVMargs;
			}
		}
		if (Strings.isNullOrEmpty(execVMargs)) {
			return appVMargs;
		} else if (Strings.isNullOrEmpty(appVMargs)) {
			return execVMargs;
		} else {
			return (execVMargs + " " + appVMargs);
		}
	}
	
	public boolean isSpecifiedJavaVMoptions() {
		return (!Strings.isNullOrEmpty(getJavaVMoptions()));
	}
	
	public String getJavaVMoptions() {
		return this.props.getString(KEY_PREFS_JAVAVM_OPTIONS, null);
	}

	/**
	 * @deprecated このメソッドは削除される。
	 */
	public List<String> getJavaVMoptionList() {
		List<String> vmList;
		String strOptions = getJavaVMoptions();
		if (!Strings.isNullOrEmpty(strOptions)) {
			String[] args = Strings.splitCommandLineWithDoubleQuoteEscape(strOptions);
			vmList = Arrays.asList(args);
		} else {
			vmList = Collections.emptyList();
		}
		return vmList;
	}
	
	public void setJavaVMoptions(String options) {
		if (!Strings.isNullOrEmpty(options)) {
			this.props.setString(KEY_PREFS_JAVAVM_OPTIONS, options);
		} else {
			this.props.clearProperty(KEY_PREFS_JAVAVM_OPTIONS);
		}
	}
	
	//--- KEY_PREFS_COMPILER_VMARGS
	
	public String appendCompilerVMargs(String compileVMargs) {
		String appVMargs = getCompilerVMargs();
		if (!Strings.isNullOrEmpty(defineFileEncoding)) {
			if (Strings.isNullOrEmpty(appVMargs)) {
				appVMargs = defineFileEncoding;
			} else {
				appVMargs = defineFileEncoding + " " + appVMargs;
			}
		}
		if (Strings.isNullOrEmpty(compileVMargs)) {
			return appVMargs;
		} else if (Strings.isNullOrEmpty(appVMargs)) {
			return compileVMargs;
		} else {
			return (compileVMargs + " " + appVMargs);
		}
	}
	
	public boolean isSpecifiedCompilerVMargs() {
		return (!Strings.isNullOrEmpty(getCompilerVMargs()));
	}
	
	public String getCompilerVMargs() {
		return this.props.getString(KEY_PREFS_COMPILER_VMARGS, null);
	}

	/**
	 * @deprecated このメソッドは削除される。
	 */
	public List<String> getCompilerVMargsList() {
		List<String> vmList;
		String strOptions = getCompilerVMargs();
		if (!Strings.isNullOrEmpty(strOptions)) {
			String[] args = Strings.splitCommandLineWithDoubleQuoteEscape(strOptions);
			vmList = Arrays.asList(args);
		} else {
			vmList = Collections.emptyList();
		}
		return vmList;
	}

	public void setCompilerVMargs(String options) {
		if (!Strings.isNullOrEmpty(options)) {
			this.props.setString(KEY_PREFS_COMPILER_VMARGS, options);
		} else {
			this.props.clearProperty(KEY_PREFS_COMPILER_VMARGS);
		}
	}
	
	//--- Font
	
	public Font getFont(String prefix) {
		Font targetFont = this.props.getFont(prefix);
		if (targetFont == null) {
			// default
			if (EDITOR.equals(prefix)) {
				// default Editor font
				targetFont =TextEditorPane.getDefaultFont();
			} else {
				// default Console font
				targetFont = UIManager.getFont("TextPane.font");
			}
		}
		return targetFont;
	}
	
	public void setFont(String prefix, Font font) {
		this.props.setFont(prefix, font);
	}
	
	//--- KEY_PREFS_LAST_WORKSPACE
	
	public boolean isSpecifiedLastWorkspace() {
		return (getLastWorkspace() != null);
	}
	
	public File getLastWorkspace() {
		File lastPath = null;
		String strPath = this.props.getString(KEY_PREFS_LAST_WORKSPACE, null);
		if (!Strings.isNullOrEmpty(strPath)) {
			File file = new File(strPath);
			lastPath = file.getAbsoluteFile();
		}
		return lastPath;
	}
	
	public void setLastWorkspace(File lastPath) {
		if (lastPath != null) {
			this.props.setString(KEY_PREFS_LAST_WORKSPACE, lastPath.getAbsolutePath());
		} else {
			this.props.clearProperty(KEY_PREFS_LAST_WORKSPACE);
		}
	}
	
	//--- KEY_PREFS_WORKSPACE_LIST
	
	public boolean isSpecifiedWorkspaceList() {
		return (!Strings.isNullOrEmpty(this.props.getString(KEY_PREFS_WORKSPACE_LIST, null)));
	}
	
	public File[] getWorkspaceList() {
		File[] wslist = null;
		String[] list = this.props.getStringArray(KEY_PREFS_WORKSPACE_LIST, null);
		if (list != null && list.length > 0) {
			int len = list.length;
			wslist = new File[len];
			for (int i = 0; i < len; i++) {
				wslist[i] = (new File(list[i])).getAbsoluteFile();
			}
		}
		return wslist;
	}
	
	public void setWorkspaceList(File[] pathlist) {
		if (pathlist != null && pathlist.length > 0) {
			int len = pathlist.length;
			String[] paths = new String[len];
			for (int i = 0; i < len; i++) {
				paths[i] = pathlist[i].getAbsolutePath();
			}
			this.props.setStringArray(KEY_PREFS_WORKSPACE_LIST, paths);
		} else {
			this.props.clearProperty(KEY_PREFS_WORKSPACE_LIST);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private String[] getDefaultCompileClassPaths() {
		if (aryDefaultCompileClassPath != null) {
			return aryDefaultCompileClassPath;
		}

		//aryDefaultCompileClassPath = getDefaultLibraryPaths(DEF_COMPILE_LIBS);
		String[] defExecLibs = getDefaultExecLibraryPaths();
		String[] defCompLibs = getDefaultCompileLibraryPaths();
		aryDefaultCompileClassPath = new String[defExecLibs.length + defCompLibs.length];
		int idx = 0;
		for (String path : defExecLibs) {
			aryDefaultCompileClassPath[idx++] = path;
		}
		for (String path : defCompLibs) {
			aryDefaultCompileClassPath[idx++] = path;
		}
		return aryDefaultCompileClassPath;
	}
	
	static private String[] getDefaultExecClassPaths() {
		if (aryDefaultExecClassPath != null) {
			return aryDefaultExecClassPath;
		}
		
		//aryDefaultExecClassPath = getDefaultLibraryPaths(DEF_EXEC_LIBS);
		aryDefaultExecClassPath = getDefaultExecLibraryPaths();
		return aryDefaultExecClassPath;
	}
	
	static private final java.io.FileFilter javaLibFilter = new java.io.FileFilter(){
		public boolean accept(File pathname) {
			String name = pathname.getName();
			if (Strings.startsWithIgnoreCase(name, ".")) {
				// Mac から Windows へ USB経由でファイルコピーした場合の対応
				return false;
			}
			if (Strings.endsWithIgnoreCase(name, ".jar")) {
				return true;
			}
			else if (Strings.endsWithIgnoreCase(name, ".zip")) {
				return true;
			}
			return false;	// not accept
		}
	};

	/**
	 * AADLモジュールの実行に必要なランタイムライブラリを取得する。
	 * このメソッドは、AADLエディタのパス構成で <code>&quot;lib/aadlrt&quot;</code> ディレクトリに
	 * 格納されている Jar もしくは Zip ファイルをすべて収集する。
	 * @return	標準実行ライブラリの絶対パスリスト
	 */
	static private String[] getDefaultExecLibraryPaths() {
		File path = new File(AADLEditor.getLibDirFile(), "aadlrt");
		try {
			path = path.getAbsoluteFile().getCanonicalFile();
		} catch (Throwable ex) {
			path = path.getAbsoluteFile();
		}
		File[] libFiles = path.listFiles(javaLibFilter);
		if (libFiles != null && libFiles.length > 0) {
			String[] aryPaths = new String[libFiles.length];
			for (int i = 0; i < libFiles.length; i++) {
				aryPaths[i] = libFiles[i].getAbsolutePath();
				AppLogger.debug("For execution default library [" + aryPaths[i] + "]");
			}
			return aryPaths;
		} else {
			AppLogger.warn("For execution default libraries noting!");
			return new String[0];
		}
	}

	/**
	 * AADLのコンパイルに必要なランタイムライブラリを取得する。
	 * このメソッドは、AADLエディタのパス構成で <code>&quot;lib/aadlc&quot;</code> ディレクトリに
	 * 格納されている Jar もしくは Zip ファイルをすべて収集する。
	 * @return	標準コンパイルライブラリの絶対パスリスト
	 */
	static private String[] getDefaultCompileLibraryPaths() {
		File path = new File(AADLEditor.getLibDirFile(), "aadlc");
		try {
			path = path.getAbsoluteFile().getCanonicalFile();
		} catch (Throwable ex) {
			path = path.getAbsoluteFile();
		}
		File[] libFiles = path.listFiles(javaLibFilter);
		if (libFiles != null && libFiles.length > 0) {
			String[] aryPaths = new String[libFiles.length];
			for (int i = 0; i < libFiles.length; i++) {
				aryPaths[i] = libFiles[i].getAbsolutePath();
				AppLogger.debug("For compile default library [" + aryPaths[i] + "]");
			}
			return aryPaths;
		} else {
			AppLogger.warn("For compile default libraries nothing!");
			return new String[0];
		}
	}

	/*--- delete : 1.10 2008/12/05
	static private String[] getDefaultLibraryPaths(String[] filenames) {
		File path = AADLEditor.getLibDirFile();
		//if (!path.exists()) {
		//	path = new File("lib").getAbsoluteFile();
		//}
		
		String[] aryPaths = new String[filenames.length];
		for (int i = 0; i < aryPaths.length; i++) {
			File libPath = new File(path, filenames[i]);
			try {
				if (!libPath.exists()) {
					AppLogger.warn("Library file [" + libPath.getAbsolutePath() + "] not found!");
				}
			}
			catch (Throwable ex) {
				AppLogger.warn("Library file [" + libPath.getAbsolutePath() + "] not found!", ex);
			}
			try {
				aryPaths[i] = libPath.getCanonicalPath();
			}
			catch (Throwable ignoreEx) {
				aryPaths[i] = libPath.getAbsolutePath();
			}
			if (AppLogger.isDebugEnabled()) {
				AppLogger.debug("Library file path [" + libPath.getAbsolutePath() + "]");
				AppLogger.debug("        real path [" + aryPaths[i] + "]");
			}
		}
		
		return aryPaths;
	}
	--- delete : 1.10 2008/12/05 */
}
