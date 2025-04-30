/*
 * @(#)AppSettings.java	2.0.0	2025/02/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AppSettings.java	1.0.0	2022/12/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.setting;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.UIManager;

import ssac.aadl.common.StartupSettings;
import ssac.aadl.module.setting.AbstractSettings;
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
 * @version 2.0.0
 */
public class DtContainerEditorSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String SECTION_STAT		= "Status";
	static public final String SECTION_PREFS	= "Preferences";
	
	//--- target

	static public final String DOCUMENT				= SECTION_STAT + ".Document";
	static public final String MAINFRAME			= SECTION_STAT + ".MainFrame";
	static public final String OUTER_FRAME			= MAINFRAME + ".outer";
	static public final String INNER_FRAME			= MAINFRAME + ".inner";
	static public final String PROP_FRAME			= MAINFRAME + ".property";
	static public final String PREFERENCE_DLG		= SECTION_STAT + ".PreferenceDlg";
	static public final String FIND_DLG				= SECTION_STAT + ".FindDlg";
	static public final String WORKSPACE_CHOOSER	= SECTION_STAT + ".WorkspaceChooser";
	
	static public final String DE_SIMPLESLIPS_CSV_DLG	= SECTION_STAT + ".DoubleEntrySimpleSlipsCsvDlg";

	static public final String EDITOR	= SECTION_PREFS + ".EditorPanel";
	static public final String CONSOLE	= SECTION_PREFS + ".ConsolePanel";
	static public final String COMPILE	= SECTION_PREFS + ".CompilePanel";
	
	//--- Content import/export dialog
	static public final String CONTENT_PREFS			= SECTION_STAT + ".content";
	static public final String CONTENT_IMPORT_PREFS		= CONTENT_PREFS + ".import";
	static public final String CONTENT_EXPORT_PREFS		= CONTENT_PREFS + ".export";
	static public final String CONTENT_INSERT_DLG		= CONTENT_IMPORT_PREFS + ".insertDlg";
	static public final String CONTENT_REPLACE_DLG		= CONTENT_IMPORT_PREFS + ".replaceDlg";
	static public final String CONTENT_EXPORT_DLG		= CONTENT_EXPORT_PREFS + ".exportDlg";

	//--- status
	
	static public final String KEY_STAT_LOOK_AND_FEEL	= SECTION_STAT + ".LookAndFeelClass";
	
	//--- settings
	
	static public final String KEY_PREFS_JAVAHOME_PATH	= SECTION_PREFS + ".JavaHome" + ".path";
	
	static public final String KEY_PREFS_ENCODING_AADL_CSV		= "aadl.csv.encoding";
	
	//--- local settings
	private static final String PROPERTY_HEADER = "DataContainerEditor settings.";
	private static final String PROPERTY_NAME = "dtcontainereditor.prefs";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private JavaInfo	_defJavaInfo;

	static private String	_defCsvFileEncoding = null;

	private static DtContainerEditorSettings _instance = null;
	
	private final ExConfiguration _props;

	//------------------------------------------------------------
	// Static interfaces
	//------------------------------------------------------------
	
	public static DtContainerEditorSettings getInstance() {
		return _instance;
	}
	
	public static void initialize()
	{
		// 'file.encoding' プロパティの設定
		//--- for Apple Mac
		{
			String osname = System.getProperty("os.name");
			if (0 <= osname.indexOf("Mac")) {
				_defCsvFileEncoding = "-Dfile.encoding=UTF-8";
			} else {
				_defCsvFileEncoding = null;
			}
		}
		
		// インスタンス生成
		_instance = new DtContainerEditorSettings();
		
		// デフォルトプロパティの初期化
		
		// ファイルパス
		File propFile = getFile();
		
		// プロパティのロード
		try {
			_instance._props.loadFile(propFile);
		}
		catch (Throwable ex) {
			AppLogger.debug(AbstractSettings.getLoadErrorMessage(propFile), ex);
		}
		
		// Java情報の更新
		refreshDefaultJavaInfo();
	}
	
	public static void flush() throws IOException
	{
		assert _instance != null : "Application Settings must be initialize!";
		
		// ファイルパス
		File propFile = getFile();
		
		// プロパティのセーブ
		_instance._props.saveFile(propFile, PROPERTY_HEADER);
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
		return StartupSettings.getConfigFile(PROPERTY_NAME);
	}
	
	protected DtContainerEditorSettings() {
		this._props = new ExConfiguration(new JavaXmlPropertiesModel());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	// ExConfiguration の取得
	public ExConfiguration getConfiguration() {
		return this._props;
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
		_defJavaInfo = new JavaInfo(strPath);
		_defJavaInfo.collect();
	}

	/**
	 * 動作環境におけるデフォルトのJava情報を取得する。
	 * このメソッドは必ず値を返す。
	 * 
	 * @return JavaInfo インスタンス
	 */
	public JavaInfo getDefaultJavaInfo() {
		return _defJavaInfo;
	}

	/**
	 * 有効なJava情報を取得する。
	 * ユーザ定義のJava情報が存在しない場合は、デフォルトのJava情報を返す。
	 * 
	 * @return JavaInfo インスタンス
	 */
	public JavaInfo getCurrentJavaInfo() {
		return _defJavaInfo;
	}

	/**
	 * 有効なJava情報のバージョン番号を取得する。
	 * バージョンが取得できない場合は null を返す。
	 * 
	 * @return バージョン番号を示す文字列
	 */
	public String getCurrentJavaVersion() {
		return getCurrentJavaInfo().getVersionString();
	}
	
	/**
	 * 有効なJava情報のメジャーバージョン番号を取得する。
	 * メジャーバージョン番号が取得できない場合は 0 を返す。
	 * @return メジャーバージョン番号
	 */
	public int getCurrentJavaMajorNumber() {
		return getCurrentJavaInfo().getMajorVersionNumber();
	}
	
	/**
	 * 有効なJava情報のコンパイラーバージョン番号を取得する。
	 * コンパイラーバージョンが取得できない場合は null を返す。
	 * @return バージョン番号を示す文字列
	 */
	public String getCurrentJavaCompilerVersion() {
		return getCurrentJavaInfo().getCompilerVersionString();
	}
	
	/**
	 * 有効なJava情報にコンパイラーが含まれているかどかを判定する。
	 * @return	コンパイラーが含まれている場合は <tt>true</tt>
	 */
	public boolean existCurrentJavaCompiler() {
		return getCurrentJavaInfo().existJavaCompiler();
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
	 * 有効なJava情報のコンパイラ Jar ファイルを取得する。
	 * 存在しない場合は null を返す。
	 * 
	 * @return Javaコンパイラ jar の絶対パス
	 */
	public File getCurrentJavaCompilerJarFile() {
		return getCurrentJavaInfo().getCompilerJarFile();
	}

	/**
	 * 有効なJava情報のコンパイラ jar ファイルを取得する。
	 * 存在しない場合は null を返す。
	 * 
	 * @return Javaコンパイラ jar の絶対パス
	 */
	public String getCurrentJavaCompilerJarPath() {
		return getCurrentJavaInfo().getCompilerJarPath();
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
	
	//--- KEY_PREFS_JAVAHOME_PATH
	
	public boolean isSpecifiedJavaHomePath() {
		return (!Strings.isNullOrEmpty(getJavaHomePath()));
	}
	
	public String getJavaHomePath() {
		return this._props.getString(KEY_PREFS_JAVAHOME_PATH, null);
	}

	//------------------------------------------------------------
	// Public interfaces for Preferences
	//------------------------------------------------------------

	public String getDefaultJsonEncodingName() {
		return "UTF-8";
	}

	//------------------------------------------------------------
	// Public interfaces for Preferences
	//------------------------------------------------------------
	
	//--- KEY_STAT_LOOK_AND_FEEL
	
	public String getNeedLookAndFeelClassName() {
		String retValue = this._props.getString(KEY_STAT_LOOK_AND_FEEL, null);
		if (Strings.isNullOrEmpty(retValue)) {
			retValue = null;
		}
		return retValue;
	}
	
	//--- KEYNAME_STATE
	
	public int getWindowState(String prefix) {
		return this._props.getWindowState(prefix);
	}
	
	public void setWindowState(String prefix, int state) {
		this._props.setWindowState(prefix, state);
	}
	
	//--- KEYNAME_LOC
	
	public Point getWindowLocation(String prefix) {
		return this._props.getWindowLocation(prefix);
	}
	
	public void setWindowLocation(String prefix, Point pos) {
		this._props.setWindowLocation(prefix, pos);
	}
	
	//--- KEYNAME_SIZE
	
	public Dimension getWindowSize(String prefix) {
		return this._props.getWindowSize(prefix);
	}
	
	public void setWindowSize(String prefix, Dimension size) {
		this._props.setWindowSize(prefix, size);
	}
	
	//--- KEYNAME_DIVLOC
	
	public int getDividerLocation(String prefix) {
		return this._props.getDividerLocation(prefix);
	}
	
	public void setDividerLocation(String prefix, int pos) {
		this._props.setDividerLocation(prefix, pos);
	}
	
	//--- KEYNAME_LASTFILE
	
	public String getLastFilename(String prefix) {
		return this._props.getLastFilename(prefix);
	}
	
	public void setLastFilename(String prefix, String filename) {
		this._props.setLastFilename(prefix, filename);
	}
	
	public File getLastFile(String prefix) {
		String strPath = this._props.getLastFilename(prefix);
		return (Strings.isNullOrEmpty(strPath) ? null : new File(strPath));
	}
	
	public void setLastFile(String prefix, File file) {
		if (file == null)
			this._props.setLastFilename(prefix, null);
		else
			this._props.setLastFilename(prefix, file.getAbsolutePath());
	}
	
	//--- KEY_PREFS_ENCODING_???
	
	protected boolean isSpecifiedEncodingName(String propName) {
		String strEncoding = this._props.getString(propName, null);
		return (!Strings.isNullOrEmpty(strEncoding));
	}
	
	protected String getEncodingName(String propName) {
		String strEncoding = this._props.getString(propName, null);
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
			this._props.setString(propName, name);
		} else {
			this._props.clearProperty(propName);
		}
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
	
	//--- Font
	
	public Font getFont(String prefix) {
		Font targetFont = this._props.getFont(prefix);
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
		this._props.setFont(prefix, font);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
