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
 * @(#)AppSettings.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.manager.setting;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import ssac.aadl.common.StartupSettings;
import ssac.util.Strings;
import ssac.util.logging.AppLogger;
import ssac.util.properties.ExConfiguration;
import ssac.util.properties.JavaXmlPropertiesModel;

/**
 * アプリケーションの設定情報。
 * 設定ダイアログにより設定される情報を操作するための機能を提供する。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class AppSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String SECTION_STAT	= "Status";
	static public final String SECTION_PREFS	= "Preferences";
	
	//--- target

	static public final String MAINFRAME			= SECTION_STAT + ".MainFrame";
	//static public final String PREFERENCE_DLG		= SECTION_STAT + ".PreferenceDlg";
	
	static public final String PACKBASE_CHOOSER = SECTION_STAT + ".PackageBaseChooser";

	//--- status
	
	static public final String KEY_STAT_LOOK_AND_FEEL	= SECTION_STAT + ".LookAndFeelClass";
	
	//static public final String KEY_STAT_MODULES_ROOT = SECTION_STAT + ".modules.root";
	
	//--- settings
	
	//--- local settings
	private static final String PROPERTY_HEADER = "PackageManager settings.";
	private static final String PROPERTY_NAME = "packagemanager.prefs";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private static AppSettings instance = null;
	
	private final ExConfiguration props;

	//------------------------------------------------------------
	// Static interfaces
	//------------------------------------------------------------
	
	public static AppSettings getInstance() {
		return instance;
	}
	
	public static void initialize()
	{
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
			AppLogger.debug(StartupSettings.getLoadErrorMessage(propFile), ex);
		}
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

	//------------------------------------------------------------
	// Public interfaces for Java information
	//------------------------------------------------------------

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
	
	//--- KEY_STAT_MODULES_ROOT

	/*
	public URI getLastModulesRoot() {
		String retValue = this.props.getString(KEY_STAT_MODULES_ROOT, null);
		if (Strings.isNullOrEmpty(retValue)) {
			return null;
		}

		URI retURI;
		try {
			retURI = new URI(retValue);
		} catch (Throwable ex) {
			AppLogger.debug("Faild to create URI from \"" + retValue + "\"", ex);
			retURI = null;
		}
		return retURI;
	}
	
	public void setLastModulesRoot(URI uri) {
		if (uri == null) {
			this.props.setString(KEY_STAT_MODULES_ROOT, null);
		} else {
			String strURI = uri.toString();
			this.props.setString(KEY_STAT_MODULES_ROOT, strURI);
		}
	}
	*/
	
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
