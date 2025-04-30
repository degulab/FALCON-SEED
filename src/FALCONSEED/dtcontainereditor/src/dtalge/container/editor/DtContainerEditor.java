/*
 * @(#)DtContainerEditor.java	2.0.0	2025/02/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerEditor.java	1.1.0	2023/01/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerEditor.java	1.0.0	2022/12/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import dtalge.container.editor.setting.DtContainerEditorSettings;
import dtalge.container.editor.view.DtContainerEditorFrame;
import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.JavaAppExecutor;
import ssac.falconseed.common.FSEnvironment;
import ssac.falconseed.common.FSStartupSettings;
import ssac.util.Strings;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.SwingTools;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * データコンテナエディタのアプリケーション・メインクラス
 * 
 * @version 2.0.0
 */
public class DtContainerEditor extends Application
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String NAME = "Data-Container Editor";
	
	static public final String BUILD = "2.0.0.20250228";
	
	static public final String LOCAL_VERSION = NAME + " (" + BUILD + ")";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private Image	_appIconImage;

	//------------------------------------------------------------
	// Entry method
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
			AppLogger.setLogger(DtContainerEditor.class);
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
			FSStartupSettings.initApplicationHome(DtContainerEditor.class);
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
///			PluginManager.setupPlugins();
			//--- アプリケーションインスタンス生成
			instance = new DtContainerEditor();
			SwingUtilities.invokeLater(instance);
		}
		catch (Throwable ex) {
			String errmsg = NAME + " cannot invoked Application context thread!";
			AppLogger.fatal(errmsg, ex);
			showMainError(ex, errmsg);
			AppLogger.info("\n<<<<< Abort(3) - " + LOCAL_VERSION + " >>>>>");
			System.exit(3);
		}

		AppLogger.debug("*End of " + DtContainerEditor.class.getName() + "#main()");
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	protected DtContainerEditor() {
		super();
	}

	/**
	 * アプリケーション唯一のインスタンスを取得する。
	 * 
	 * @return <code>DtContainerEditor</code> クラスのインスタンス
	 */
	static public DtContainerEditor getInstance() {
		return ((DtContainerEditor)Application.getInstance());
	}

	//------------------------------------------------------------
	// Entry point
	//------------------------------------------------------------

	/**
	 * アプリケーションのメイン・ルーチン
	 */
	public void run() {
		AppLogger.info("===== Start to Run Application context thread.");
		try {
			// 設定情報の初期化
			DtContainerEditorSettings.initialize();
			
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
				strmsg = DtContainerEditorSettings.getInstance().getJavaHomePath();
				sb.append(strmsg != null ? strmsg : "null");
				// Java Info
				sb.append("\n  < Java information >");
				File f;
				//--- Home
				sb.append("\n      Home=");
				f = DtContainerEditorSettings.getInstance().getCurrentJavaHomeFile();
				sb.append(f != null ? f.getAbsolutePath() : "null");
				//--- Command
				sb.append("\n      Command=");
				f = DtContainerEditorSettings.getInstance().getCurrentJavaCommandFile();
				sb.append(f != null ? f.getAbsolutePath() : "null");
				//--- Version
				sb.append("\n      Version=");
				strmsg = DtContainerEditorSettings.getInstance().getCurrentJavaVersion();
				sb.append(strmsg != null ? strmsg : "null");
				sb.append("\n..... end of information");
				AppLogger.info(sb.toString());
			}
		
			// Look & Feel の初期化
			JFrame.setDefaultLookAndFeelDecorated(false);	// Frame概観は、ネイティブに依存
			String needLF = DtContainerEditorSettings.getInstance().getNeedLookAndFeelClassName();
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
			
			// SpreadSheetTable のエラーメッセージ初期化
			SpreadSheetTable.setErrorMessage(SpreadSheetTable.ERROR_CANNOT_PASTE_MULTIPLE_AREA, CommonMessages.getInstance().msgSpreadSheetPasteInMultiple);
			
			// アイコンファイルのロード
			Image imgIcon = getAppIconImage();
			
			// メインフレームの初期化
			DtContainerEditorFrame frame = new DtContainerEditorFrame();
			this.mainFrame = frame;
			if (imgIcon != null) {
				frame.setIconImage(imgIcon);
			}
			frame.initialComponent();
	
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
			File fIcon = new File(getLibDirFile(), "resources/icon/DtContainerEditor.png");
			if (fIcon.exists() && fIcon.isFile()) {
				_appIconImage = Toolkit.getDefaultToolkit().createImage(fIcon.getAbsolutePath());
			}
			if (_appIconImage == null) {
				final String APP_ICON_BASEDIR = "dtcontainereditor/resource/";
				final String APP_ICON_RESOURCE = "images/DtContainerEditorAppIcon.png";
				URL url = DtContainerEditor.class.getClassLoader().getResource(APP_ICON_RESOURCE);
				if (url != null) {
					_appIconImage = Toolkit.getDefaultToolkit().createImage(url);
				} else {
					_appIconImage = Toolkit.getDefaultToolkit().createImage(APP_ICON_BASEDIR + APP_ICON_RESOURCE);
				}
			}
		}
		return _appIconImage;
	}
	
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
