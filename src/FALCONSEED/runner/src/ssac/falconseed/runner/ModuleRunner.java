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
 * @(#)ModuleRunner.java	3.3.0	2016/05/31
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	3.2.2	2015/10/20 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	3.2.1	2015/07/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	3.2.0	2015/06/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	3.1.3	2015/05/25 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	3.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	3.0.0	2014/03/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	2.1.0	2013/08/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	2.0.0	2012/11/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	1.22	2012/08/31
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	1.21	2012/06/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	1.20	2012/03/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	1.13	2012/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	1.10	2011/02/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleRunner.java	1.00	2010/12/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.JavaAppExecutor;
import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.common.FSEnvironment;
import ssac.falconseed.common.FSStartupSettings;
import ssac.falconseed.editor.plugin.PluginManager;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.util.MacUtilities;
import ssac.util.Strings;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.SwingTools;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * モジュールランナーのアプリケーション・メインクラス
 * 
 * @version 3.3.0 2016/05/31
 */
public class ModuleRunner extends Application
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String NAME = "Module Runner";
	
	static public final String BUILD = "20160531";
	
	static public final String LOCAL_VERSION = NAME + " (" + BUILD + ")";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private Image	_appIconImage;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	protected ModuleRunner() {
		super();
	}

	/**
	 * アプリケーション唯一のインスタンスを取得する。
	 * @return	<code>ModuleRunner</code> クラスのインスタンス
	 */
	static public ModuleRunner getInstance() {
		return ((ModuleRunner)Application.getInstance());
	}
	
	//------------------------------------------------------------
	// Entry point
	//------------------------------------------------------------

	/**
	 * アプリケーションのエントリ・ポイント
	 * 
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		// 正常起動メッセージを最初に標準出力に出力
		System.out.println(JavaAppExecutor.SUCCEEDED_MESSAGE);
		
		try {
			// 引数確認
			for (int i = 0; i < args.length; i++) {
				if (FSStartupSettings.LANGUAGE_OPTION.equals(args[i]) && ((i+1) < args.length) && !args[i+1].equals("")) {
					// has locale
					i++;
					FSStartupSettings.setLocalLanguageOption(args[i]);
					Locale defLocale = new Locale(args[i]);
					Locale.setDefault(defLocale);
				}
				else if (FSStartupSettings.DEBUG_OPTION.equals(args[i])) {
					FSStartupSettings.setDebugOption(true);
				}
			}

			// 起動設定取得
			FSStartupSettings.initialize();

			// ログ初期化
			String logDir = FSStartupSettings.getLoggingDirProperty();
			if (Strings.isNullOrEmpty(logDir)) {
				//--- logging.dir が定義されていない場合は、logging.dir=user.home とする
				System.setProperty(FSStartupSettings.SYSPROP_LOGGING_DIR, System.getProperty("user.home"));
			}
			AppLogger.setLogger(ModuleRunner.class);
			AppLogger.info("\n<<<<< Start - " + LOCAL_VERSION + " >>>>>");
		}
		catch (Throwable ex) {
			String errmsg = "Failed to initialization for startup settings!";
			printMainError(ex, errmsg);
			showMainError(ex, errmsg);
			System.err.println("\n<<<<< Abort(1) - " + LOCAL_VERSION + " >>>>>");
			System.exit(1);
		}
		
		// 実行パラメータ・ログ
		if (AppLogger.isInfoEnabled()) {
			//--- Debug mode
			if (FSStartupSettings.isSpecifiedDebugOption()) {
				AppLogger.info("[Mode] Debug mode");
			}
			//--- ロケール
			AppLogger.info("[Locale] "
							+ Locale.getDefault().getDisplayLanguage()
							+ "(\"" + Locale.getDefault().getLanguage()
							+ "\")");
			//--- ロケーション
			AppLogger.info("[config.dir] " + FSStartupSettings.getAvailableConfigDir());
			AppLogger.info("[logging.dir] " + FSStartupSettings.getLoggingDirProperty());
			//--- 最大メモリサイズのチェック
			String strMemorySize = getMaxMemorySize();
			if (!Strings.isNullOrEmpty(strMemorySize)) {
				AppLogger.info("[Max memory size] " + strMemorySize);
			} else {
				AppLogger.info("[Max memory size] Default size");
			}
		}
		
		//--- メッセージ初期化
		msgboxTitleInfo  = CommonMessages.getInstance().msgboxTitleInfo;
		msgboxTitleWarn  = CommonMessages.getInstance().msgboxTitleWarn;
		msgboxTitleError = CommonMessages.getInstance().msgboxTitleError;
		
		//--- Home 初期化
		try {
			FSStartupSettings.initApplicationHome(ModuleRunner.class);
		}
		catch (Throwable ex) {
			String errmsg = NAME + " cannot initialize Application Home directory!";
			AppLogger.fatal(errmsg, ex);
			showMainError(ex, errmsg);
			AppLogger.info("\n<<<<< Abort(2) - " + LOCAL_VERSION + " >>>>>");
			System.exit(2);
		}
		//--- logging for Home
		if (AppLogger.isInfoEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append(NAME + " Home directories.");
			sb.append("\n    Home=");
			sb.append(FSStartupSettings.getHomeDirAbsolutePath());
			sb.append("\n    Lib=");
			sb.append(FSStartupSettings.getLibDirAbsolutePath());
			AppLogger.info(sb.toString());
		}
		//--- Environment 初期化
		FSEnvironment.initialize(FSStartupSettings.getHomeDirFile());
		if (AppLogger.isInfoEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append(NAME + " environment root directory.");
			sb.append("\n    Environment root=");
			File fEnvDir = FSEnvironment.getInstance().rootDirectory();
			if (fEnvDir != null) {
				sb.append(fEnvDir.getAbsolutePath());
			} else {
				sb.append("Nothing!");
			}
			AppLogger.info(sb.toString());
		}

		//--- アプリケーション初期化
		try {
			//--- プラグイン初期化
			PluginManager.setupPlugins();
			//--- アプリケーションインスタンス生成
			instance = new ModuleRunner();
			SwingUtilities.invokeLater(instance);
		}
		catch (Throwable ex) {
			String errmsg = NAME + " cannot invoked Application context thread!";
			AppLogger.fatal(errmsg, ex);
			showMainError(ex, errmsg);
			AppLogger.info("\n<<<<< Abort(3) - " + LOCAL_VERSION + " >>>>>");
			System.exit(3);
		}

		AppLogger.debug("*End of " + ModuleRunner.class.getName() + "#main()");
	}

	/**
	 * アプリケーションのメイン・ルーチン
	 */
	public void run() {
		AppLogger.info("===== Start to Run Application context thread.");
		try {
			// 設定情報の初期化
			AppSettings.initialize();
			
			// 実行環境のチェック
			if (AppLogger.isInfoEnabled()) {
				String strmsg;
				StringBuffer sb = new StringBuffer();
				sb.append("Target java information...");
				// Properties
				sb.append("\n  < Properties >");
				//--- "java.home" System property value
				sb.append("\n      System \"java.home\"=");
				strmsg = System.getProperty("java.home");
				sb.append(strmsg != null ? strmsg : "null");
				//--- "JAVA_HOME" Environment value
				sb.append("\n      Env \"JAVA_HOME\"=");
				strmsg = System.getenv("JAVA_HOME");
				sb.append(strmsg != null ? strmsg : "null");
				//--- ".JavaHome" Local property value
				sb.append("\n      Local \".JavaHome\"=");
				strmsg = AppSettings.getInstance().getJavaHomePath();
				sb.append(strmsg != null ? strmsg : "null");
				// Java Info
				sb.append("\n  < Java information >");
				File f;
				//--- Home
				sb.append("\n      Home=");
				f = AppSettings.getInstance().getCurrentJavaHomeFile();
				sb.append(f != null ? f.getAbsolutePath() : "null");
				//--- Compiler
				sb.append("\n      Compiler=");
				f = AppSettings.getInstance().getCurrentJavaCompilerFile();
				sb.append(f != null ? f.getAbsolutePath() : "null");
				//--- Command
				sb.append("\n      Command=");
				f = AppSettings.getInstance().getCurrentJavaCommandFile();
				sb.append(f != null ? f.getAbsolutePath() : "null");
				//--- Version
				sb.append("\n      Version=");
				strmsg = AppSettings.getInstance().getCurrentJavaVersion();
				sb.append(strmsg != null ? strmsg : "null");
				sb.append("\n..... end of information");
				AppLogger.info(sb.toString());
			}
		
			// Look & Feel の初期化
			JFrame.setDefaultLookAndFeelDecorated(false);	// Frame概観は、ネイティブに依存
			String needLF = AppSettings.getInstance().getNeedLookAndFeelClassName();
			if (!SwingTools.setupLookAndFeel(needLF)) {
				AppLogger.error("Failed to setup Look & Feel.");
				return;
			}
			//--- Check Look & Feel
			if (AppLogger.isInfoEnabled()) {
				UIManager.LookAndFeelInfo[] lfi = UIManager.getInstalledLookAndFeels();
				if (lfi != null) {
					LookAndFeel lf = UIManager.getLookAndFeel();
					StringBuffer sb = new StringBuffer();
					sb.append("Installed Look & Feel entries...");
					for (int i = 0; i < lfi.length; i++) {
						if (lfi[i].getClassName().equals(lf.getClass().getName())) {
							sb.append("\n[*] ");
						} else {
							sb.append("\n[ ] ");
						}
						sb.append(lfi[i].getName());
						sb.append("<");
						sb.append(lfi[i].getClassName());
						sb.append(">");
					}
					AppLogger.info(sb.toString());
				}
				else {
					AppLogger.info("Nothing installed Look & Feel entries.");
				}
			}
			
			// For Mac OS X (Change L&F for JTabbedPane
//			if (MacUtilities.isMacOSX()) {
//				AppLogger.info("Mac OS X, setting extra UIManager resources for TabbedPane.");
//				UIManager.put("TabbedPaneUI", "javax.swing.plaf.metal.MetalTabbedPaneUI");
//				UIManager.put("TabbedPane.selectedTabPadInsets", new Insets(2, 2, 2, 1));
//				UIManager.put("TabbedPane.tabsOpaque", Boolean.TRUE);
//				UIManager.put("TabbedPane.darkShadow", new Color(122, 138, 153));
//				//UIManager.put("TabbedPane.background", new Color(184,207,229));
//				UIManager.put("TabbedPane.selectHighlight", new Color(255, 255, 255));
//				//UIManager.put("TabbedPane.foreground", new Color(51,51,51));
//				UIManager.put("TabbedPane.textIconGap", 4);
//				UIManager.put("TabbedPane.highlight", new Color(255, 255, 255));
//				UIManager.put("TabbedPane.unselectedBackground", new Color(238, 238, 238));
//				UIManager.put("TabbedPane.tabRunOverlay", 2);
//				UIManager.put("TabbedPane.light", new Color(238, 238, 238));
//				UIManager.put("TabbedPane.tabsOverlapBorder", Boolean.FALSE);
//				UIManager.put("TabbedPane.selected", new Color(200, 221, 242));
//				UIManager.put("TabbedPane.contentBorderInsets", new Insets(4, 2, 3, 3));
//				UIManager.put("TabbedPane.contentAreaColor", new Color(220, 221, 242));
//				UIManager.put("TabbedPane.tabAreaInsets", new Insets(2, 2, 0, 6));
//				UIManager.put("TabbedPane.contentOpaque", Boolean.TRUE);
//				UIManager.put("TabbedPane.focus", new Color(99, 130, 191));
//				UIManager.put("TabbedPane.tabAreaBackground", new Color(218, 218, 218));
//				UIManager.put("TabbedPane.shadow", new Color(184, 207, 229));
//				UIManager.put("TabbedPane.tabInsets", new Insets(0, 9, 1, 9));
//				UIManager.put("TabbedPane.borderHightlightColor", new Color(99, 130, 191));
//			}
			
			// ModuleItemManager 初期化
			ModuleFileManager.initialize();
			
			// SpreadSheetTable のエラーメッセージ初期化
			SpreadSheetTable.setErrorMessage(SpreadSheetTable.ERROR_CANNOT_PASTE_MULTIPLE_AREA, CommonMessages.getInstance().msgSpreadSheetPasteInMultiple);
			
			// アイコンファイルのロード
//			Image imgIcon = null;
//			{
//				File fIcon = new File(getLibDirFile(), "resources/icon/ModuleRunner.png");
//				if (fIcon.exists() && fIcon.isFile()) {
//					imgIcon = Toolkit.getDefaultToolkit().createImage(fIcon.getAbsolutePath());
//				}
//			}
			Image imgIcon = getAppIconImage();
			
			// メインフレームの初期化
			RunnerFrame frame = new RunnerFrame();
			this.mainFrame = frame;
			if (imgIcon != null) {
				frame.setIconImage(imgIcon);
			}
			frame.initialComponent();
			if (MacUtilities.isMac()) {
				MacUtilities.setupScreenMenuHandler(frame);
			}
	
			// メインフレームの表示
			frame.setVisible(true);
		}
		catch (Throwable ex) {
			String errmsg = "Failed to start " + NAME + "!";
			AppLogger.fatal(errmsg, ex);
			showMainError(ex, errmsg);
			AppLogger.info("\n<<<<< Abort(4) - " + LOCAL_VERSION + " >>>>>\n");
			System.exit(4);
		}
		finally {
			AppLogger.info("===== End of Application context thread.");
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public Image getAppIconImage() {
		if (_appIconImage == null) {
			File fIcon = new File(getLibDirFile(), "resources/icon/ModuleRunner.png");
			if (fIcon.exists() && fIcon.isFile()) {
				_appIconImage = Toolkit.getDefaultToolkit().createImage(fIcon.getAbsolutePath());
			}
		}
		return _appIconImage;
	}
	
	static protected void showMainError(Throwable ex, String message) {
		String desc = (message==null ? "" : message);
		if (ex != null) {
			desc = desc + "\n    " + ex.toString();
		}
		JOptionPane.showMessageDialog(null, desc, NAME + " Error", JOptionPane.ERROR_MESSAGE);
	}
	
	static protected void printMainError(Throwable ex, String message) {
		String msg = "[" + NAME + " Error] " + (message==null ? "" : message);
		if (ex != null) {
			msg = msg + "\n    " + ex.toString();
		}
		System.err.println(msg);
	}

	static public final String getHomeDirAbsolutePath() {
		return FSStartupSettings.getHomeDirAbsolutePath();
	}
	
	static public final File getHomeDirFile() {
		return FSStartupSettings.getHomeDirFile();
	}
	
	static public final String getLibDirAbsolutePath() {
		return FSStartupSettings.getLibDirAbsolutePath();
	}
	
	static public final File getLibDirFile() {
		return FSStartupSettings.getLibDirFile();
	}

	/**
	 * このアプリケーションがデバッグモードの場合は <tt>true</tt> を返す。
	 */
	static public final boolean isDebugEnabled() {
		return FSStartupSettings.isSpecifiedDebugOption();
	}

	/**
	 * このアプリケーションにおける最大メモリ(ヒープ)サイズを取得する。
	 * このメソッドが返す文字列は、Javaコマンドオプション"-Xmx"
	 * に指定可能な文字列とする。
	 * 
	 * @return 最大メモリサイズを示す文字列を返す。指定されて
	 * 			いない場合は null を返す。
	 */
	static public final String getMaxMemorySize() {
		int memSize = FSStartupSettings.getAvailableMemorySize();
		if (memSize > 0) {
			return (String.valueOf(memSize) + "m");
		} else {
			return null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
