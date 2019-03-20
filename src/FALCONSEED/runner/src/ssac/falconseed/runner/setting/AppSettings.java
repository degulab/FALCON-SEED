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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AppSettings.java	3.3.0	2016/05/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	3.2.1	2015/07/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	3.2.0	2015/06/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	3.1.0	2014/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	3.0.0	2014/03/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	2.1.0	2013/08/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	2.0.0	2012/10/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	1.22	2012/08/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	1.20	2012/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	1.13	2011/11/07
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	1.10	2011/02/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.setting;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.UIManager;

import ssac.aadl.macro.AADLMacroEngine;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.setting.AbstractSettings;
import ssac.falconseed.common.FSStartupSettings;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.view.dialog.UserFolderChooser;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.JavaInfo;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.properties.ExConfiguration;
import ssac.util.properties.JavaXmlPropertiesModel;

/**
 * アプリケーションの設定情報。
 * 設定ダイアログにより設定される情報を操作するための機能を提供する。
 * 
 * @version 3.3.0
 */
public class AppSettings
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final String SECTION_STAT	= "Status";
	static public final String SECTION_PREFS	= "Preferences";
	
	//--- Excel 2 CSV
	static public final String Excel2CSV_PREFIX				= SECTION_STAT + ".Excel2CSV";
	static public final String Excel2CSV_EXCEL_INPUTFILE	= Excel2CSV_PREFIX + ".excel.input";
	static public final String Excel2CSV_BASE_OUTPUTDIR		= Excel2CSV_PREFIX + ".base.outdir";
	static public final String Excel2CSV_DESTEDIT_DLG		= Excel2CSV_PREFIX + ".DestEditDlg";
	static public final String Excel2CSV_DESTEDIT_OUT2TEMP	= Excel2CSV_DESTEDIT_DLG + ".AllOutToTemp";
	static public final String Excel2CSV_DESTEDIT_SHOWDEST	= Excel2CSV_DESTEDIT_DLG + ".AllShowDest";
	
	//--- target

	static public final String DOCUMENT			= SECTION_STAT + ".Document";
	static public final String MEXECDEF_EXPORT	= SECTION_STAT + ".MExecDef.Export";
	static public final String MEXECDEF_IMPORT	= SECTION_STAT + ".MExecDef.Import";
	static public final String JARMODULE		= SECTION_STAT + ".JarModule";
	static public final String MAINFRAME		= SECTION_STAT + ".MainFrame";
	static public final String OUTER_FRAME		= MAINFRAME + ".outer";
	static public final String INNER_FRAME		= MAINFRAME + ".inner";
	static public final String TOOL_FRAME		= MAINFRAME + ".tool";
	static public final String PROP_FRAME		= MAINFRAME + ".property";
	static public final String PREFERENCE_DLG	= SECTION_STAT + ".PreferenceDlg";
	static public final String JUMP_DLG			= SECTION_STAT + ".JumpDlg";
	static public final String FIND_DLG			= SECTION_STAT + ".FindDlg";

	static public final String MEXECDEF_FILE_CHOOSER_DLG	= SECTION_STAT + ".MExecDefFileChooserDlg";
	static public final String MEXECDEF_FOLDER_CHOOSER_DLG	= SECTION_STAT + ".MExecDefFolderChooserDlg";
	
	static public final String USERFOLDERCHOOSER_DLG		= SECTION_STAT + ".UserFolderChooserDlg";
	static public final String MACROFILTER_EDIT_DLG			= SECTION_STAT + ".MacroFilterEditDlg";
	static public final String GENERICFILTER_EDIT_DLG		= SECTION_STAT + ".GenericFilterEditDlg";
	static public final String GENERICINPUT_EDIT_DLG		= SECTION_STAT + ".GenericInputSchemaEditDlg";
	static public final String GENERICOUTPUT_EDIT_DLG		= SECTION_STAT + ".GenericOutputSchemaEditDlg";
	static public final String GENERICFILTERARG_EDIT_DLG	= SECTION_STAT + ".GenericFilterDefArgEditDlg";
	static public final String SCHEMADATETIMEFORMAT_EDIT_DLG	= SECTION_STAT + ".SchemaDateTimeFormatEditDlg";
	static public final String MEXECDEF_EDIT_DLG			= SECTION_STAT + ".MExecDefEditDlg";
	static public final String MEXECARG_EDIT_DLG			= SECTION_STAT + ".MExecArgsEditDlg";
	static public final String MEXECARG_VIEW_DLG			= SECTION_STAT + ".MExecArgsViewDlg";
	static public final String PROCESSMONITOR_DLG			= SECTION_STAT + ".ProcessMonitorDlg";
	static public final String CSVFILECONFIG_DLG			= SECTION_STAT + ".CsvFileConfigDlg";
	static public final String CSVFILESAVECONFIG_DLG		= SECTION_STAT + ".CsvFileSaveConfigDlg";
	static public final String EXPORTDTALGE_DLG				= SECTION_STAT + ".ExportDtalgeDlg";
	static public final String EXPORTMEXECDEF_DLG			= SECTION_STAT + ".ExportMExecDefDlg";
	static public final String IMPORTMEXECDEF_DLG			= SECTION_STAT + ".ImportMExecDefDlg";
	static public final String AADLMACROGRAPH_DLG			= SECTION_STAT + ".AADLMacroGraphDlg";
	static public final String MACROFILTERDEFARGREF_DLG		= SECTION_STAT + ".MacroFilterDefArgRefDlg";
	static public final String MACROSUBFILTERARGREF_DLG		= SECTION_STAT + ".MacroSubFilterArgRefDlg";
	static public final String MACROSUBFILTERCHOOSER_DLG	= SECTION_STAT + ".MacroSubFilterChooserDlg";
	static public final String CHARTCONFIG_DLG				= SECTION_STAT + ".ChartConfigDlg";
	static public final String CHARTVIEW_DLG				= SECTION_STAT + ".ChartViewDlg";
	static public final String CHARTSERIESEDIT_DLG			= SECTION_STAT + ".ChartSeriesEditDlg";
	static public final String CHARTFIELDSELECT_DLG			= SECTION_STAT + ".ChartFieldSelectDlg";
	static public final String CHARTMULTIFIELDSELECT_DLG	= SECTION_STAT + ".ChartMultiFieldSelectDlg";
	static public final String CHARTDATARANGEEDIT_DLG		= SECTION_STAT + ".ChartDataRangeEditDlg";
	
	static public final String PREFS_AUTOCLOSE_CONSOLE		= SECTION_PREFS + ".AutoCloseConsoleAfterExecution";
	static public final String PREFS_SHOW_CONSOLE				= SECTION_PREFS + ".ShowConsoleAtStart";

	static public final String VIEW_CONSOLE	= SECTION_PREFS + ".ConsolePanel";
	static public final String VIEW_CSVVIEWER	= SECTION_PREFS + ".CsvViewer";

	//--- status
	
	static public final String KEY_STAT_LOOK_AND_FEEL	= SECTION_STAT + ".LookAndFeelClass";
	
	//--- settings
	
	static public final String KEY_PREFS_JAVAHOME_PATH	= SECTION_PREFS + ".JavaHome" + ".path";
	static public final String KEY_PREFS_JAVAVM_OPTIONS	= SECTION_PREFS + ".JavaVM" + ".vmArgs";
	static public final String KEY_PREFS_COMPILER_VMARGS	= SECTION_PREFS + ".Compiler" + ".vmArgs";
	
	static public final String KEY_PREFS_GRAPHVIZ_PATH	= SECTION_PREFS + ".GraphViz" + ".path";
	
	static public final String KEY_PREFS_HISTORY_MAXLENGTH	= SECTION_PREFS + ".History" + ".maxLength";
	
	static public final String KEY_PREFS_AUTOCLOSE_CONSOLE		= SECTION_PREFS + ".AutoCloseConsoleAfterExecution";
	static public final String KEY_PREFS_SHOW_CONSOLE				= SECTION_PREFS + ".ShowConsoleAtStart";
	
//	static public final String KEY_PREFS_ENCODING_AADL_SRC	= "aadl.source.encoding";
	static public final String KEY_PREFS_ENCODING_AADL_MACRO	= "aadl.macro.csv.encoding";
	static public final String KEY_PREFS_ENCODING_AADL_CSV	= "aadl.csv.encoding";
	static public final String KEY_PREFS_ENCODING_AADL_TXT	= "aadl.txt.encoding";
	
//	static public final String KEY_PREFS_USERFOLDER_LAST	= "runner.userfolder.latest";
//	static public final String KEY_PREFS_USERFOLDER_NUM	= "runner.userfolder.num";
//	static public final String KEY_PREFS_USERFOLDER_LIST	= "runner.userfolder.list";
	
	static protected final String SUFFIX_USERFOLDER_LAST	= ".latest";
	static protected final String SUFFIX_USERFOLDER_NUM	= ".num";
	static protected final String SUFFIX_USERFOLDER_LIST	= ".list";

	static public final String KEY_PREFIX_MEXECDEF_USERFOLDER	= "runner.userfolder";
	static public final String KEY_PREFIX_DATAFILE_USERFOLDER	= "runner.datafile.userfolder";

	/**
	 * 最後に選択されたフィルタパスの保存先となるキーのプレフィックス
	 * @since 3.1.0
	 */
	static public final String KEY_PREFIX_LASTSELECTED_FILEPATH		= SECTION_STAT + ".lastSelected.path";
	
	//--- local settings
	private static final String PROPERTY_HEADER = "Module Runner settings.";
	private static final String PROPERTY_NAME = "modulerunner.prefs";
	
	private static final String MEXECDEF_DIRNAME = "execdefs";
	private static final String DATAFILE_DIRNAME = "datafiles";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private JavaInfo	defJavaInfo;

	static private String[]	aryDefaultCompileClassPath = null;
	static private String[]	aryDefaultExecClassPath = null;
	static private String		defineFileEncoding = null;
	
	static private File	_defaultUserFilterRootDir	= null;
	static private File	_defaultUserDataRootDir		= null;

	private static AppSettings instance = null;
	
	private final ExConfiguration props;
	
	private JavaInfo	userJavaInfo;

	/** Java 標準のテンポラリディレクトリの抽象パス(絶対パス) **/
	static private File	_fSystemTempDir;

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
		
		// Java情報の更新
		refreshDefaultJavaInfo();
		instance.refreshUserJavaInfo();
	}
	
	public static void flush() throws IOException
	{
		assert instance != null : "Application Settings must be initialize!";
		
		// ファイルパス
		File propFile = getFile();
		
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
		
		return FSStartupSettings.getConfigFile(PROPERTY_NAME);
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

	/*---
	public String getReadEncodingForAadlSource(CompileSettings settings) {
		return settings.getTargetEncodingName(getAadlSourceEncodingName(), false);
	}
	---*/

	/*---
	public String getWriteEncodingForAadlSource(CompileSettings settings) {
		return settings.getTargetEncodingName(getAadlSourceEncodingName(), true);
	}
	---*/

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
	// Public interfaces for Temporary directory
	//------------------------------------------------------------

	/**
	 * アプリケーションのテンポラリディレクトリにテンポラリファイルを作成する。
	 * このメソッドでは、<em>prefix</em> の先頭に、自動的に &quot;fst&quot; を
	 * 付加する。
	 * <p><b>注：</b>
	 * <blockquote>
	 * テンポラリファイルが生成できた場合、そのファイルに対して {@link java.io.File#deleteOnExit()} が呼び出される。
	 * </blockquote>
	 * @param prefix	ファイル名を生成するために使用される接頭辞文字列。<tt>null</tt> も可。
	 * @param suffix	ファイル名を生成するために使用される接尾辞文字列。<tt>null</tt> も指定でき、その場合は、接尾辞 &quot;.tmp&quot; が使用される
	 * @param deleteOnExit	アプリケーション終了時に削除されるファイルとしてマークする場合は <tt>true</tt>
	 * @return 生成されたテンポラリファイルの抽象パス
	 * @return 生成されたテンポラリファイルの抽象パス
	 * @throws IOException			ファイルが生成できなかった場合
	 * @throws SecurityException	セキュリティーマネージャーが存在する場合に、
	 * 								セキュリティーマネージャーの {@link SecurityManager#checkWrite(String)}  メソッドがファイルの生成を許可しないとき
	 * @since 2.0.0
	 */
	static public File createTemporaryFile(String prefix, String suffix, boolean deleteOnExit) throws IOException
	{
		return createTemporaryFile(null, prefix, suffix, deleteOnExit);
	}
	
	/**
	 * アプリケーションのテンポラリディレクトリにテンポラリファイルを作成する。
	 * このメソッドでは、<em>prefix</em> の先頭に、自動的に &quot;fst&quot; を
	 * 付加する。
	 * <p><b>注：</b>
	 * <blockquote>
	 * テンポラリファイルが生成できた場合、そのファイルに対して {@link java.io.File#deleteOnExit()} が呼び出される。
	 * </blockquote>
	 * @param prefix	ファイル名を生成するために使用される接頭辞文字列。<tt>null</tt> も可。
	 * @param suffix	ファイル名を生成するために使用される接尾辞文字列。<tt>null</tt> も指定でき、その場合は、接尾辞 &quot;.tmp&quot; が使用される
	 * @param deleteOnExit	アプリケーション終了時に削除されるファイルとしてマークする場合は <tt>true</tt>
	 * @return 生成されたテンポラリファイルの抽象パス
	 * @return 生成されたテンポラリファイルの抽象パス
	 * @throws IOException			ファイルが生成できなかった場合
	 * @throws SecurityException	セキュリティーマネージャーが存在する場合に、
	 * 								セキュリティーマネージャーの {@link SecurityManager#checkWrite(String)}  メソッドがファイルの生成を許可しないとき
	 * @since 2.0.0
	 */
	static public VirtualFile createTemporaryVirtualFile(String prefix, String suffix, boolean deleteOnExit) throws IOException
	{
		return createTemporaryVirtualFile(null, prefix, suffix, deleteOnExit);
	}

	/**
	 * 指定されたディレクトリに、アプリケーションのテンポラリファイルを作成する。
	 * このメソッドでは、<em>prefix</em> の先頭に、自動的に &quot;fst&quot; を
	 * 付加する。<p>
	 * <em>parentDirectory</em> が <tt>null</tt> の場合、{@link #getAvailableTemporaryDirectory()} が返すディレクトリにテンポラリファイルが作成される。
	 * <p><b>注：</b>
	 * <blockquote>
	 * テンポラリファイルが生成できた場合、そのファイルに対して {@link java.io.File#deleteOnExit()} が呼び出される。
	 * </blockquote>
	 * @param parentDirectory	ファイルが生成されるディレクトリ。デフォルトの一時ファイルディレクトリが使用される場合は <tt>null</tt>	
	 * @param prefix	ファイル名を生成するために使用される接頭辞文字列。<tt>null</tt> も可。
	 * @param suffix	ファイル名を生成するために使用される接尾辞文字列。<tt>null</tt> も指定でき、その場合は、接尾辞 &quot;.tmp&quot; が使用される
	 * @param deleteOnExit	アプリケーション終了時に削除されるファイルとしてマークする場合は <tt>true</tt>
	 * @return 生成されたテンポラリファイルの抽象パス
	 * @throws IOException			ファイルが生成できなかった場合
	 * @throws SecurityException	セキュリティーマネージャーが存在する場合に、
	 * 								セキュリティーマネージャーの {@link SecurityManager#checkWrite(String)}  メソッドがファイルの生成を許可しないとき
	 * @since 2.0.0
     */
	static public File createTemporaryFile(File parentDirectory, String prefix, String suffix, boolean deleteOnExit) throws IOException
	{
		if (prefix != null && prefix.length() > 0) {
			prefix = "fst" + prefix;
		} else {
			prefix = "fst";
		}

		File file;
		if (parentDirectory != null) {
			file = File.createTempFile(prefix, suffix, parentDirectory);
		} else {
			file = File.createTempFile(prefix, suffix, getAvailableTemporaryDirectory());
		}
		if (deleteOnExit) {
			file.deleteOnExit();
		}
		
		return file;
	}

	/**
	 * 指定されたディレクトリに、アプリケーションのテンポラリファイルを作成する。
	 * このメソッドでは、<em>prefix</em> の先頭に、自動的に &quot;fst&quot; を
	 * 付加する。<p>
	 * <em>parentDirectory</em> が <tt>null</tt> の場合、{@link #getAvailableTemporaryDirectory()} が返すディレクトリにテンポラリファイルが作成される。
	 * <p><b>注：</b>
	 * <blockquote>
	 * テンポラリファイルが生成できた場合、そのファイルに対して {@link java.io.File#deleteOnExit()} が呼び出される。
	 * </blockquote>
	 * @param parentDirectory	ファイルが生成されるディレクトリ。デフォルトの一時ファイルディレクトリが使用される場合は <tt>null</tt>	
	 * @param prefix	ファイル名を生成するために使用される接頭辞文字列。<tt>null</tt> も可。
	 * @param suffix	ファイル名を生成するために使用される接尾辞文字列。<tt>null</tt> も指定でき、その場合は、接尾辞 &quot;.tmp&quot; が使用される
	 * @param deleteOnExit	アプリケーション終了時に削除されるファイルとしてマークする場合は <tt>true</tt>
	 * @return 生成されたテンポラリファイルの抽象パス
	 * @throws IllegalArgumentException	<em>parentDirectory</em> が <code>java.io.File</code> に変換できない場合
	 * @throws IOException			ファイルが生成できなかった場合
	 * @throws SecurityException	セキュリティーマネージャーが存在する場合に、
	 * 								セキュリティーマネージャーの {@link SecurityManager#checkWrite(String)}  メソッドがファイルの生成を許可しないとき
	 * @since 2.0.0
     */
	static public VirtualFile createTemporaryVirtualFile(VirtualFile parentDirectory, String prefix, String suffix, boolean deleteOnExit) throws IOException
	{
		File pdir;
		if (parentDirectory != null) {
			pdir = ModuleFileManager.toJavaFile(parentDirectory);
			if (pdir == null) {
				throw new IllegalArgumentException("Cannot convert 'parentDirectory' to java.io.File : " + parentDirectory.getClass().getName());
			}
		} else {
			pdir = null;
		}
		
		return ModuleFileManager.fromJavaFile(createTemporaryFile(pdir, prefix, suffix, deleteOnExit));
	}

	/**
	 * 現在有効なテンポラリディレクトリの抽象パスを返す。
	 * @return	テンポラリディレクトリを示す抽象パス(絶対パス)
	 * @since 2.0.0
	 */
	static public File getAvailableTemporaryDirectory() {
		// TODO: ユーザーがテンポラリディレクトリを指定可能なようにする。
		return getSystemTemporaryDirectory();
	}

	/**
	 * Java 標準のテンポラリディレクトリの抽象パスを返す。
	 * @return	テンポラリディレクトリを示す抽象パス(絶対パス)
	 * @since 2.0.0
	 */
	static public File getSystemTemporaryDirectory() {
		if (_fSystemTempDir == null) {
			_fSystemTempDir = new File(System.getProperty("java.io.tmpdir"));
		}
		return _fSystemTempDir;
	}

	//------------------------------------------------------------
	// Public interfaces for Data directory
	//------------------------------------------------------------

	/**
	 * 旧ユーザールートの直下にファイルもしくはフォルダが存在しているかを判定する。
	 * このメソッドは、旧バージョンとの互換性のために残されている。
	 * @param prefix	取得するディレクトリの種類を特定するパラメータ
	 * @return	旧ユーザールートディレクトリが存在し、その直下にファイルもしくはディレクトリが
	 * 			存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 1.13
	 */
	public boolean hasFilesInOldDefaultUserRootDirectory(String prefix) {
		File fDir = getOldDefaultUserRootDirectory(prefix, false);
		if (fDir != null && fDir.exists()) {
			String[] names = fDir.list();
			if (names != null && names.length > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * システムルートディレクトリの基準となるディレクトリのデフォルト抽象パスを返す。
	 * @return	基準となるディレクトリを示す抽象パス
	 * @since 1.10
	 */
	public File getDefaultSystemRootBaseDirectory() {
		return new File(ModuleRunner.getHomeDirFile(), "systemdata");
	}
	
	/**
	 * システムルートディレクトリのデフォルト抽象パスを返す。
	 * このメソッドは、<em>prefix</em> に指定する値によって、返すディレクトリが異なる。
	 * @param prefix	取得するディレクトリの種類を特定するパラメータ
	 * @return <em>prefix</em> が {@link #KEY_PREFIX_DATAFILE_USERFOLDER} の場合はデータファイル用システムルートディレクトリの抽象パスを返す。
	 * 			それ以外の場合はモジュール実行定義用システムルートディレクトリの抽象パスを返す。
	 * @since 1.10
	 */
	public File getDefaultSystemRootDirectory(String prefix) {
		File fDir = getDefaultSystemRootBaseDirectory();
		if (KEY_PREFIX_DATAFILE_USERFOLDER.equals(prefix)) {
			fDir = new File(fDir, DATAFILE_DIRNAME);
		}
		else {
			fDir = new File(fDir, MEXECDEF_DIRNAME);
		}
		return fDir;
	}

	/**
	 * モジュール実行定義を格納するシステムルートディレクトリの
	 * デフォルト抽象パスを返す。
	 * @return 抽象パス
	 */
	public File getDefaultSystemMExecDefRootDirectory() {
//		File fDir = new File(ModuleRunner.getHomeDirFile(), "systemdata");
//		fDir = new File(fDir, MEXECDEF_DIRNAME);
//		return fDir;
		return getDefaultSystemRootDirectory(KEY_PREFIX_MEXECDEF_USERFOLDER);
	}

	/**
	 * データファイルを格納するシステムルートディレクトリの
	 * デフォルト抽象パスを返す。
	 * @return	抽象パス
	 * @since 1.10
	 */
	public File getDefaultSystemDataRootDirectory() {
//		File fDir = new File(ModuleRunner.getHomeDirFile(), "systemdata");
//		fDir = new File(fDir, DATAFILE_DIRNAME);
//		return fDir;
		return getDefaultSystemRootDirectory(KEY_PREFIX_DATAFILE_USERFOLDER);
	}
	
	/**
	 * ユーザー領域として設定されているディレクトリの抽象パスを返す。
	 * ユーザーにより設定されたパスがユーザールートディレクトリとして
	 * 無効な場合は、デフォルトのユーザールートを返す。
	 * このメソッドは、<em>prefix</em> に指定する値によって、返すディレクトリが異なる。
	 * @param prefix	取得するディレクトリの種類を特定するパラメータ
	 * @return <em>prefix</em> が {@link #KEY_PREFIX_DATAFILE_USERFOLDER} の場合はデータファイル用ユーザールートディレクトリの抽象パスを返す。
	 * 			それ以外の場合はモジュール実行定義用ユーザールートディレクトリの抽象パスを返す。
	 * @since 1.10
	 */
	public File getAvailableUserRootDirectory(String prefix) {
		if (KEY_PREFIX_DATAFILE_USERFOLDER.equals(prefix)) {
			return getAvailableUserDataRootDirectory();
		}
		else {
			return getAvailableUserMExecDefRootDirectory();
		}
	}

	/**
	 * モジュール実行定義のユーザー領域として設定されているディレクトリの抽象パスを返す。
	 * ユーザーにより設定されたパスがユーザールートディレクトリとして
	 * 無効な場合は、デフォルトのユーザールートを返す。
	 * @return	抽象パス
	 */
	public File getAvailableUserMExecDefRootDirectory() {
		File fDir = getLatestUserFolder(KEY_PREFIX_MEXECDEF_USERFOLDER);
		if (fDir != null) {
			if (UserFolderChooser.verifyUserFolderRoot(null, true, false, fDir)) {
				return fDir;
			}
		}
		
		// invalid user choise
		return getDefaultUserMExecDefRootDirectory(true);
	}
	
	/**
	 * データファイルのユーザー領域として設定されているディレクトリの抽象パスを返す。
	 * ユーザーにより設定されたパスがユーザールートディレクトリとして
	 * 無効な場合は、デフォルトのユーザールートを返す。
	 * @return	抽象パス
	 * @since 1.10
	 */
	public File getAvailableUserDataRootDirectory() {
		File fDir = getLatestUserFolder(KEY_PREFIX_DATAFILE_USERFOLDER);
		if (fDir != null) {
			if (UserFolderChooser.verifyUserFolderRoot(null, true, false, fDir)) {
				return fDir;
			}
		}
		
		// invalid user choise
		return getDefaultUserDataRootDirectory(true);
	}
	
	/**
	 * ユーザールートディレクトリのデフォルト抽象パスを返す。
	 * このメソッドは、<em>prefix</em> に指定する値によって、返すディレクトリが異なる。
	 * @param prefix	取得するディレクトリの種類を特定するパラメータ
	 * @param ensureDir	<tt>true</tt> を指定した場合、ディレクトリが存在しない場合にそのディレクトリを作成する。
	 * @return <em>prefix</em> が {@link #KEY_PREFIX_DATAFILE_USERFOLDER} の場合はデータファイル用ユーザールートディレクトリの抽象パスを返す。
	 * 			それ以外の場合はモジュール実行定義用ユーザールートディレクトリの抽象パスを返す。
	 */
	public File getDefaultUserRootDirectory(String prefix, boolean ensureDir) {
		if (KEY_PREFIX_DATAFILE_USERFOLDER.equals(prefix)) {
			return getDefaultUserDataRootDirectory(ensureDir);
		}
		else {
			return getDefaultUserMExecDefRootDirectory(ensureDir);
		}
	}
	
	public File getOldDefaultUserRootDirectory(String prefix, boolean ensureDir) {
		if (KEY_PREFIX_DATAFILE_USERFOLDER.equals(prefix)) {
			return getOldDefaultUserDataRootDirectory(ensureDir);
		}
		else {
			return getOldDefaultUserMExecDefRootDirectory(ensureDir);
		}
	}

	/**
	 * モジュール実行定義を格納するユーザールートディレクトリの
	 * デフォルト抽象パスを返す。
	 * このパスは、システムプロパティ &quot;user.home&quot; が返す
	 * ディレクトリを基準とし、次のようなパスを取る。
	 * <blockquote>
	 * {user.home}/FALCONSEED/execdefs
	 * </blockquote>
	 * @param ensureDir	<tt>true</tt> を指定した場合、ディレクトリが存在しない場合にそのディレクトリを作成する。
	 * @return 抽象パス
	 */
	public File getDefaultUserMExecDefRootDirectory(boolean ensureDir) {
		if (_defaultUserFilterRootDir != null) {
			return _defaultUserFilterRootDir;
		}

		File fDir = null;
		String strPath = FSStartupSettings.getUserFilterDirProperty();
		if (!Strings.isNullOrEmpty(strPath)) {
			fDir = new File(strPath);
			if (ensureDir && !fDir.exists()) {
				try {
					Files.ensureDirectory(fDir);
				}
				catch (Throwable ex) {}
			}
		}
		else {
			strPath = FSStartupSettings.getAvailableUserRootDir();
			fDir = new File(strPath, FSStartupSettings.DIRNAME_USERAREA);
			fDir = new File(fDir, MEXECDEF_DIRNAME);
			if (ensureDir && !fDir.exists()) {
				try {
					Files.ensureDirectory(fDir);
				}
				catch (Throwable ex) {}
			}
		}
		
		_defaultUserFilterRootDir = fDir;
		return fDir;
	}

	/**
	 * モジュール実行定義を格納する旧ユーザールートディレクトリの
	 * デフォルト抽象パスを返す。
	 * このパスは、システムプロパティ &quot;user.home&quot; が返す
	 * ディレクトリを基準とし、次のようなパスを取る。
	 * <blockquote>
	 * {user.home}/.FALCONSEED/userdata/execdefs
	 * </blockquote>
	 * @param ensureDir	<tt>true</tt> を指定した場合、ディレクトリが存在しない場合にそのディレクトリを作成する。
	 * @return 抽象パス
	 */
	public File getOldDefaultUserMExecDefRootDirectory(boolean ensureDir) {
		String dir = System.getProperty("user.home");
		File fDir;
		if (dir != null && dir.length() > 0) {
			fDir = new File(dir, FSStartupSettings.DIRNAME_CONFIGTOP);
		} else {
			fDir = new File(FSStartupSettings.DIRNAME_CONFIGTOP);
		}
		fDir = new File(fDir, "userdata");
		fDir = new File(fDir, MEXECDEF_DIRNAME);
		if (ensureDir && !fDir.exists()) {
			try {
				Files.ensureDirectory(fDir);
			}
			catch (Throwable ex) {}
		}
		return fDir;
	}
	
	/**
	 * モジュール実行定義を格納するユーザールートディレクトリの
	 * デフォルト抽象パスを返す。
	 * このパスは、システムプロパティ &quot;user.home&quot; が返す
	 * ディレクトリを基準とし、次のようなパスを取る。
	 * <blockquote>
	 * {user.home}/FALCONSEED/datafiles
	 * </blockquote>
	 * @param ensureDir	<tt>true</tt> を指定した場合、ディレクトリが存在しない場合にそのディレクトリを作成する。
	 * @return 抽象パス
	 */
	public File getDefaultUserDataRootDirectory(boolean ensureDir) {
		if (_defaultUserDataRootDir != null) {
			return _defaultUserDataRootDir;
		}

		File fDir = null;
		String strPath = FSStartupSettings.getUserDataDirProperty();
		if (!Strings.isNullOrEmpty(strPath)) {
			fDir = new File(strPath);
			if (ensureDir && !fDir.exists()) {
				try {
					Files.ensureDirectory(fDir);
				}
				catch (Throwable ex) {}
			}
		}
		else {
			strPath = FSStartupSettings.getAvailableUserRootDir();
			fDir = new File(strPath, FSStartupSettings.DIRNAME_USERAREA);
			fDir = new File(fDir, DATAFILE_DIRNAME);
			if (ensureDir && !fDir.exists()) {
				try {
					Files.ensureDirectory(fDir);
				}
				catch (Throwable ex) {}
			}
		}
		
		_defaultUserDataRootDir = fDir;
		return fDir;
	}
	
	/**
	 * モジュール実行定義を格納する旧ユーザールートディレクトリの
	 * デフォルト抽象パスを返す。
	 * このパスは、システムプロパティ &quot;user.home&quot; が返す
	 * ディレクトリを基準とし、次のようなパスを取る。
	 * <blockquote>
	 * {user.home}/.FALCONSEED/userdata/datafiles
	 * </blockquote>
	 * @param ensureDir	<tt>true</tt> を指定した場合、ディレクトリが存在しない場合にそのディレクトリを作成する。
	 * @return 抽象パス
	 */
	public File getOldDefaultUserDataRootDirectory(boolean ensureDir) {
		String dir = System.getProperty("user.home");
		File fDir;
		if (dir != null && dir.length() > 0) {
			fDir = new File(dir, FSStartupSettings.DIRNAME_CONFIGTOP);
		} else {
			fDir = new File(FSStartupSettings.DIRNAME_CONFIGTOP);
		}
		fDir = new File(fDir, "userdata");
		fDir = new File(fDir, DATAFILE_DIRNAME);
		if (ensureDir && !fDir.exists()) {
			try {
				Files.ensureDirectory(fDir);
			}
			catch (Throwable ex) {}
		}
		return fDir;
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

	/*---
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
	---*/

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
	
	//--- KEY_PREFS_GRAPHVIZ_PATH
	
	public boolean isSpecifiedGraphVizPath() {
		return (!Strings.isNullOrEmpty(getGraphVizPath()));
	}
	
	public String getGraphVizPath() {
		return this.props.getString(KEY_PREFS_GRAPHVIZ_PATH, null);
	}
	
	public void setGraphVizPath(String strPath) {
		if (!Strings.isNullOrEmpty(strPath)) {
			this.props.setString(KEY_PREFS_GRAPHVIZ_PATH, strPath);
		} else {
			this.props.clearProperty(KEY_PREFS_GRAPHVIZ_PATH);
		}
	}
	
	//--- KEY_PREFS_HISTORY_MAXLENGTH
	
	public int getDefaultHistoryMaxLength() {
		return 10;		// 標準では 10個
	}
	
	public int getHistoryMaxLengthLimit() {
		return Integer.MAX_VALUE;
	}
	
	public int getHistoryMaxLength() {
		return this.props.getIntegerValue(KEY_PREFS_HISTORY_MAXLENGTH, getDefaultHistoryMaxLength());
	}
	
	public void setHistoryMaxLength(int maxLength) {
		if (maxLength < 1) {
			
		}
		if (maxLength == getDefaultHistoryMaxLength()) {
			this.props.clearProperty(KEY_PREFS_HISTORY_MAXLENGTH);
		}
		else if (maxLength < 1) {
			this.props.setIntegerValue(KEY_PREFS_HISTORY_MAXLENGTH, 1);
		}
		else if (maxLength > getHistoryMaxLengthLimit()) {
			this.props.setIntegerValue(KEY_PREFS_HISTORY_MAXLENGTH, getHistoryMaxLengthLimit());
		}
		else {
			this.props.setIntegerValue(KEY_PREFS_HISTORY_MAXLENGTH, maxLength);
		}
	}
	
	//--- KEY_PREFS_AUTOCLOSE_CONSOLE
	
	public boolean getDefaultConsoleAutoClose() {
		return false;
	}
	
	public boolean getConsoleAutoClose() {
		return this.props.getBooleanValue(KEY_PREFS_AUTOCLOSE_CONSOLE, getDefaultConsoleAutoClose());
	}
	
	public void setConsoleAutoClose(boolean toEnable) {
		this.props.setBooleanValue(KEY_PREFS_AUTOCLOSE_CONSOLE, toEnable);
	}
	
	//--- KEY_PREFS_SHOW_CONSOLE
	
	public boolean getDefaultConsoleShowAtStart() {
		return true;
	}
	
	public boolean getConsoleShowAtStart() {
		return this.props.getBooleanValue(KEY_PREFS_SHOW_CONSOLE, getDefaultConsoleShowAtStart());
	}
	
	public void setConsoleShowAtStart(boolean toEnable) {
		this.props.setBooleanValue(KEY_PREFS_SHOW_CONSOLE, toEnable);
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
			if (VIEW_CSVVIEWER.equals(prefix)) {
				// default CSV Viewer font
				targetFont = UIManager.getFont("Table.font");
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
	
	//--- USERFOLDER_LAST
	
	public boolean isSpecifiedLatestUserFolder(String prefix) {
		return (getLatestUserFolder(prefix) != null);
	}
	
	public File getLatestUserFolder(String prefix) {
		File lastPath = null;
		String strPath = this.props.getString(prefix + SUFFIX_USERFOLDER_LAST, null);
		if (!Strings.isNullOrEmpty(strPath)) {
			File file = new File(strPath);
			lastPath = file.getAbsoluteFile();
		}
		return lastPath;
	}
	
	public void setLatestUserFolder(String prefix, File lastPath) {
		if (lastPath != null) {
			this.props.setString(prefix + SUFFIX_USERFOLDER_LAST, lastPath.getAbsolutePath());
		} else {
			this.props.clearProperty(prefix + SUFFIX_USERFOLDER_LAST);
		}
	}
	
	//--- Last selected filter path
	
	/**
	 * 最後に選択されたフィルタパスの保存先となるプロパティキーを生成する。
	 * @param key	正規のキーの終端に付加する文字列
	 * @return	正規のプロパティキー
	 * @since 3.1.0
	 */
	protected String makeLastSelectedPathPropertyKey(String key) {
		if (Strings.isNullOrEmpty(key)) {
			return KEY_PREFIX_LASTSELECTED_FILEPATH;
		} else {
			return KEY_PREFIX_LASTSELECTED_FILEPATH + "." + key;
		}
	}

	/**
	 * 指定されたパスを、指定されたキーでプロパティに保存する。
	 * 基準ディレクトリの下にあるパスであれば相対パス、それ以外は絶対パスとして保存する。
	 * @param vfBase	相対パスの基準となるパス、<tt>null</tt> の場合は絶対パスとして保存
	 * @param lastSelection	保存するパス
	 * @param key	保存先とするプロパティ、<tt>null</tt> もしくは空文字の場合は、共通のプロパティへ保存
	 * @since 3.1.0
	 */
	public void setLastSelectedPath(VirtualFile vfBase, VirtualFile lastSelection, String key) {
		// キーの作成
		key = makeLastSelectedPathPropertyKey(key);
		
		// パスが無効の場合は、プロパティを削除
		if (lastSelection == null) {
			props.clearProperty(key);
			return;
		}
		
		// パスを保存
		if (!lastSelection.isAbsolute())
			lastSelection = lastSelection.getAbsoluteFile();
		if (vfBase != null && lastSelection.isDescendingFrom(vfBase)) {
			// 相対パスとして保存
			props.setString(key, lastSelection.relativePathFrom(vfBase));
		}
		else {
			// 絶対パスとして保存
			props.setString(key, lastSelection.toURI().toString());
		}
	}

	/**
	 * 指定されたプロパティキーからパスを読み込み、絶対パスに変換する。
	 * 相対パスとして保存されていたパスは、指定された基準パスに対する相対パスとして展開する。
	 * @param vfBase	基準パス、<tt>null</tt> の場合はカレントディレクトリに対する相対パスとして展開する。
	 * @param key	読み込むプロパティ、<tt>null</tt> もしくは空文字の場合は、共通のプロパティから読み込む
	 * @return	読み込まれた絶対パス、読み込めなかった場合は <tt>null</tt>
	 * @since 3.1.0
	 */
	public VirtualFile getLastSelectedPath(VirtualFile vfBase, String key) {
		// キーの作成
		key = makeLastSelectedPathPropertyKey(key);
		
		// プロパティの値を取得
		VirtualFile vfPath = null;
		String strPath = props.getString(key, null);
		if (strPath != null) {
			try {
				URI uri = new URI(strPath);
				if (uri.isAbsolute()) {
					// 絶対パスとして展開
					vfPath = ModuleFileManager.fromURI(uri).getNormalizedFile();
				}
				else if (vfBase != null) {
					vfPath = vfBase.getChildFile(strPath).getNormalizedFile();
				}
				else {
					vfPath = ModuleFileManager.fromJavaFile(new File(strPath)).getAbsoluteFile().getNormalizedFile();
				}
			}
			catch (Throwable ex) {
				vfPath = null;
			}
		}
		
		return vfPath;
	}

	
//	//--- KEY_PREFS_MEXECDEF_USERFOLDER_LAST
//	
//	public boolean isSpacifiedLatestMExecDefUserFolder() {
//		return isSpecifiedLatestUserFolder(KEY_PREFIX_MEXECDEF_USERFOLDER);
//	}
//	
//	public File getLatestMExecDefUserFolder() {
//		return getLatestUserFolder(KEY_PREFIX_MEXECDEF_USERFOLDER);
//	}
//	
//	public void setLatestMExecDefUserFolder(File lastPath) {
//		setLatestUserFolder(KEY_PREFIX_MEXECDEF_USERFOLDER, lastPath);
//	}
//	
//	//--- KEY_PREFS_DATAFILE_USERFOLDER_LAST
//	
//	public boolean isSpacifiedLatestDataFileUserFolder() {
//		return isSpecifiedLatestUserFolder(KEY_PREFIX_DATAFILE_USERFOLDER);
//	}
//	
//	public File getLatestDataFileUserFolder() {
//		return getLatestUserFolder(KEY_PREFIX_DATAFILE_USERFOLDER);
//	}
//	
//	public void setLatestDataFileUserFolder(File lastPath) {
//		setLatestUserFolder(KEY_PREFIX_DATAFILE_USERFOLDER, lastPath);
//	}
	
	//--- USERFOLDER_LIST
	
	static private final String getUserFolderListKey(String prefix, int index) {
		return (prefix + SUFFIX_USERFOLDER_LIST + Integer.toString(index+1));
	}
	
	public File[] getUserFolderArray(String prefix) {
		int len = this.props.getIntegerValue(prefix + SUFFIX_USERFOLDER_NUM, 0);
		if (len <= 0) {
			return null;
		}
		List<File> wslist = new ArrayList<File>(len);
		for (int i = 0; i < len; i++) {
			String strValue = this.props.getString(getUserFolderListKey(prefix, i), null);
			if (!Strings.isNullOrEmpty(strValue)) {
				File path = new File(strValue).getAbsoluteFile();
				if (path.exists() && path.isDirectory()) {
					wslist.add(path);
				}
			}
		}
		
		if (!wslist.isEmpty()) {
			return wslist.toArray(new File[wslist.size()]);
		} else {
			return null;
		}
	}
	
	public void setUserFolderArray(String prefix, File...files) {
		setUserFolderArray(prefix, files==null ? (Collection<? extends File>)null : Arrays.asList(files));
	}
	
	public void setUserFolderArray(String prefix, Collection<? extends File> files) {
		int oldLen = this.props.getIntegerValue(prefix + SUFFIX_USERFOLDER_NUM, 0);
		int index = 0;
		
		if (files != null && !files.isEmpty()) {
			for (File file : files) {
				this.props.setString(getUserFolderListKey(prefix, index++), file.getAbsolutePath());
			}
		}
		
		for (; index < oldLen; ) {
			this.props.clearProperty(getUserFolderListKey(prefix, index++));
		}
		
		this.props.setIntegerValue(prefix + SUFFIX_USERFOLDER_NUM, files.size());
	}
	
//	//--- KEY_PREFS_MEXECDEF_USERFOLDER_LIST
//	
//	public File[] getMExecDefUserFolderArray() {
//		return getUserFolderArray(KEY_PREFIX_MEXECDEF_USERFOLDER);
//	}
//	
//	public void setMExecDefUserFolderArray(File...files) {
//		setUserFolderArray(KEY_PREFIX_MEXECDEF_USERFOLDER, files);
//	}
//	
//	public void setMExecDefUserFolderArray(Collection<? extends File> files) {
//		setUserFolderArray(KEY_PREFIX_MEXECDEF_USERFOLDER, files);
//	}
//	
//	//--- KEY_PREFS_DATAFILE_USERFOLDER_LIST
//	
//	public File[] getDataFileUserFolderArray() {
//		return getUserFolderArray(KEY_PREFIX_DATAFILE_USERFOLDER);
//	}
//	
//	public void setDataFileUserFolderArray(File...files) {
//		setUserFolderArray(KEY_PREFIX_DATAFILE_USERFOLDER, files);
//	}
//	
//	public void setDataFileUserFolderArray(Collection<? extends File> files) {
//		setUserFolderArray(KEY_PREFIX_DATAFILE_USERFOLDER, files);
//	}

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
		File path = new File(ModuleRunner.getLibDirFile(), "aadlrt");
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
		File path = new File(ModuleRunner.getLibDirFile(), "aadlc");
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
}
