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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AADLEditor.java	3.2.2	2015/10/20 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLEditor.java	3.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLEditor.java	1.22	2012/11/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLEditor.java	1.21	2012/10/05
 *     - modified by Y.Ishizuka(PieCake.inc,) - bugfix
 * @(#)AADLEditor.java	1.21	2012/08/31
 *     - modified by Y.Ishizuka(PieCake.inc,) - update for Mac OS X
 * @(#)AADLEditor.java	1.20	2012/06/13
 *     - modified by Y.Ishizuka(PieCake.inc,) - update compiler & dtalge
 * @(#)AADLEditor.java	1.19	2012/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,) - bugfix
 * @(#)AADLEditor.java	1.19	2011/07/12
 *     - modified by Y.Ishizuka(PieCake.inc,) - update compiler
 * @(#)AADLEditor.java	1.19	2011/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLEditor.java	1.17	2011/02/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLEditor.java	1.17	2010/12/01
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLEditor.java	1.16	2010/09/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLEditor.java	1.15	2010/03/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLEditor.java	1.14	2009/12/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLEditor.java	1.11	2009/03/11
 *     - modified by Y.Ishizuka(PieCake.inc,) - update compiler & exalge2
 * @(#)AADLEditor.java	1.10	2009/02/12
 *     - modified by Y.Ishizuka(PieCake.inc,) - bugfix
 * @(#)AADLEditor.java	1.10	2009/02/09
 *     - modified by Y.Ishizuka(PieCake.inc,) - bugfix
 * @(#)AADLEditor.java	1.10	2009/01/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLEditor.java	1.01	2008/04/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AADLEditor.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor;

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
import ssac.aadl.common.StartupSettings;
import ssac.aadl.editor.plugin.PluginManager;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.editor.view.EditorFrame;
import ssac.aadl.editor.view.dialog.WorkspaceChooser;
import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.common.FSEnvironment;
import ssac.util.MacUtilities;
import ssac.util.Strings;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.SwingTools;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * AADLエディタのアプリケーション・メインクラス
 * 
 * @version 3.2.2	2015/10/20
 */
public class AADLEditor extends Application
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String NAME = "AADL Editor";
	
	static public final String VERSION = "3.2.2";
	static public final String BUILD = "20151020";
	
	static public final String SIMPLE_VERSION_INFO
									= NAME + " " + VERSION + "(" + BUILD + ")";
	
	static public final String VERSION_MESSAGE
									= NAME + "\n"
									+ " - Version " + VERSION
									+ " (" + BUILD + ")";
	
	static public final String LOCAL_VERSION = NAME + " (" + BUILD + ")";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	protected AADLEditor() {
		super();
	}

	/**
	 * アプリケーション唯一のインスタンスを取得する。
	 * 
	 * @return <code>AADLEditor</code> クラスのインスタンス
	 */
	static public AADLEditor getInstance() {
		return ((AADLEditor)Application.getInstance());
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
				if (StartupSettings.LANGUAGE_OPTION.equals(args[i]) && ((i+1) < args.length) && !args[i+1].equals("")) {
					// has locale
					i++;
					StartupSettings.setLanguageOption(args[i]);
					Locale defLocale = new Locale(args[i]);
					Locale.setDefault(defLocale);
				}
				else if (StartupSettings.DEBUG_OPTION.equals(args[i])) {
					StartupSettings.setDebugOption(true);
				}
			}

			// 起動設定取得
			StartupSettings.initialize();

			// ログ初期化
			String logDir = StartupSettings.getLoggingDirProperty();
			if (Strings.isNullOrEmpty(logDir)) {
				//--- logging.dir が定義されていない場合は、logging.dir=user.home とする
				System.setProperty(StartupSettings.SYSPROP_LOGGING_DIR, System.getProperty("user.home"));
			}
			AppLogger.setLogger(AADLEditor.class);
			AppLogger.info("\n<<<<< Start - " + SIMPLE_VERSION_INFO + " >>>>>");
		}
		catch (Throwable ex) {
			String errmsg = "Failed to initialization for startup settings!";
			printMainError(ex, errmsg);
			showMainError(ex, errmsg);
			System.err.println("\n<<<<< Abort(1) - " + SIMPLE_VERSION_INFO + " >>>>>");
			System.exit(1);
		}
		
		// 実行パラメータ・ログ
		if (AppLogger.isInfoEnabled()) {
			//--- Debug mode
			if (StartupSettings.isSpecifiedDebugOption()) {
				AppLogger.info("[Mode] Debug mode");
			}
			//--- ロケール
			AppLogger.info("[Locale] "
							+ Locale.getDefault().getDisplayLanguage()
							+ "(\"" + Locale.getDefault().getLanguage()
							+ "\")");
			//--- ロケーション
			AppLogger.info("[config.dir] " + StartupSettings.getAvailableConfigDir());
			AppLogger.info("[logging.dir] " + StartupSettings.getLoggingDirProperty());
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
			StartupSettings.initApplicationHome(AADLEditor.class);
		}
		catch (Throwable ex) {
			String errmsg = "AADLEditor cannot initialize Application Home directory!";
			AppLogger.fatal(errmsg, ex);
			showMainError(ex, errmsg);
			AppLogger.info("\n<<<<< Abort(2) - " + SIMPLE_VERSION_INFO + " >>>>>");
			System.exit(2);
		}
		//--- logging for Home
		if (AppLogger.isInfoEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("AADLEditor Home directories.");
			sb.append("\n    Home=");
			sb.append(StartupSettings.getHomeDirAbsolutePath());
			sb.append("\n    Lib=");
			sb.append(StartupSettings.getLibDirAbsolutePath());
			AppLogger.info(sb.toString());
		}
		//--- Environment 初期化
		FSEnvironment.initialize(StartupSettings.getHomeDirFile());
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
			instance = new AADLEditor();
			//SwingUtilities.invokeLater(instance);
			SwingUtilities.invokeLater(instance);
		}
		catch (Throwable ex) {
			String errmsg = "AADL Editor cannot invoked Application context thread!";
			AppLogger.fatal(errmsg, ex);
			showMainError(ex, errmsg);
			AppLogger.info("\n<<<<< Abort(3) - " + SIMPLE_VERSION_INFO + " >>>>>");
			System.exit(3);
		}

		AppLogger.debug("*End of " + AADLEditor.class.getName() + "#main()");
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
			if (AppSettings.getInstance().getCurrentJavaCompilerFile() == null) {
				AppLogger.warn(EditorMessages.getInstance().msgNotFoundJDK);
				showWarningMessage(null, EditorMessages.getInstance().msgNotFoundJDK);
			}
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
			
			// ModuleItemManager 初期化
			ModuleFileManager.initialize();
			
			// SpreadSheetTable のエラーメッセージ初期化
			SpreadSheetTable.setErrorMessage(SpreadSheetTable.ERROR_CANNOT_PASTE_MULTIPLE_AREA, EditorMessages.getInstance().msgSpreadSheetPasteInMultiple);
			
			// アイコンファイルのロード
			Image imgIcon = null;
			{
				try {
					File fIcon = new File(getLibDirFile(), "resources/icon/AE_Editor.png");
					if (fIcon.exists() && fIcon.isFile()) {
						imgIcon = Toolkit.getDefaultToolkit().createImage(fIcon.getAbsolutePath());
					}
				}
				catch (Throwable ignoreEx) {
					ignoreEx = null;
					imgIcon = null;
				}
			}
			
			// ワークスペース選択ダイアログの表示
			File selectedPath = null;
			{
				JFrame iconframe = null;
				if (imgIcon != null) {
					iconframe = new JFrame("TempFrame");	// 荒業－いずれは JFrame 化が必要
					iconframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					iconframe.setIconImage(imgIcon);
				}
				WorkspaceChooser chooser = new WorkspaceChooser(iconframe);
				try {
					chooser.setAlwaysOnTop(true);
				} catch (SecurityException ignoreException) {}
				chooser.setVisible(true);
				if (iconframe != null) {
					iconframe.dispose();
					iconframe = null;
				}
				if (chooser.getDialogResult() == IDialogResult.DialogResult_OK) {
					selectedPath = AppSettings.getInstance().getLastWorkspace();
					if (!WorkspaceChooser.verifyWorkspaceRoot(null, false, selectedPath)) {
						selectedPath = null;
					}
				}
			}

			if (selectedPath != null) {
				// メインフレームの初期化
				EditorFrame frame = new EditorFrame();
				this.mainFrame = frame;
				if (imgIcon != null) {
					frame.setIconImage(imgIcon);
				}
				frame.initialComponent();
				if (MacUtilities.isMac()) {
					MacUtilities.setupScreenMenuHandler(frame);
				}
			
				// メインフレームの表示
				this.mainFrame.setVisible(true);
			}
		}
		catch (Throwable ex) {
			String errmsg = "Failed to start AADL Editor!";
			AppLogger.fatal(errmsg, ex);
			showMainError(ex, errmsg);
			AppLogger.info("\n<<<<< Abort(4) - " + SIMPLE_VERSION_INFO + " >>>>>\n");
			System.exit(4);
		}
		finally {
			AppLogger.info("===== End of Application context thread.");
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public void showMainError(Throwable ex, String message) {
		String desc = (message==null ? "" : message);
		if (ex != null) {
			desc = desc + "\n    " + ex.toString();
		}
		JOptionPane.showMessageDialog(null, desc, "AADL Editor Error", JOptionPane.ERROR_MESSAGE);
	}
	
	static protected void printMainError(Throwable ex, String message) {
		String msg = "[AADL Editor Error] " + (message==null ? "" : message);
		if (ex != null) {
			msg = msg + "\n    " + ex.toString();
		}
		System.err.println(msg);
	}

	/*
	public File[] getDefaultClassPaths() {
		if (defaultClassPaths != null)
			return defaultClassPaths;
		
		Vector<File> paths = new Vector<File>();
		//--- Exalge2.jar
		paths.add(new File("lib/Exalge2.jar"));
		
		defaultClassPaths = paths.toArray(new File[paths.size()]);
		return defaultClassPaths;
	}
	*/

	static public final String getHomeDirAbsolutePath() {
		return StartupSettings.getHomeDirAbsolutePath();
	}
	
	static public final File getHomeDirFile() {
		return StartupSettings.getHomeDirFile();
	}
	
	static public final String getLibDirAbsolutePath() {
		return StartupSettings.getLibDirAbsolutePath();
	}
	
	static public final File getLibDirFile() {
		return StartupSettings.getLibDirFile();
	}

	/**
	 * AADLエディタがデバッグモードの場合は <tt>true</tt> を返す。
	 * @since 1.13
	 */
	static public final boolean isDebugEnabled() {
		return StartupSettings.isSpecifiedDebugOption();
	}

	/**
	 * AADLエディタにおける最大メモリ(ヒープ)サイズを取得する。
	 * このメソッドが返す文字列は、Javaコマンドオプション"-Xmx"
	 * に指定可能な文字列とする。
	 * 
	 * @return 最大メモリサイズを示す文字列を返す。指定されて
	 * 			いない場合は null を返す。
	 */
	static public final String getMaxMemorySize() {
		int memSize = StartupSettings.getAvailableMemorySize();
		if (memSize > 0) {
			return (String.valueOf(memSize) + "m");
		} else {
			return null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
