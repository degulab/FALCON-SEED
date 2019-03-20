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
 * @(#)LauncherMain.java	3.3.0	2016/05/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherMain.java	3.1.3	2015/05/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherMain.java	3.1.2	2014/10/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherMain.java	2.1.0	2013/08/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherMain.java	2.0.0	2012/11/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherMain.java	1.22	2012/08/31
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherMain.java	1.21	2012/06/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherMain.java	1.20	2012/03/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherMain.java	1.13	2012/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherMain.java	1.12	2011/10/31
 *     - modified by Y.Ishizuka(PieCake.inc,) - update only version date
 * @(#)LauncherMain.java	1.12	2011/07/12
 *     - modified by Y.Ishizuka(PieCake.inc,) - update compiler
 * @(#)LauncherMain.java	1.12	2011/06/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherMain.java	1.11	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherMain.java	1.10	2011/02/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherMain.java	1.00	2010/12/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.launcher;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeSet;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ssac.aadl.common.StartupSettings;
import ssac.falconseed.common.FSEnvironment;
import ssac.falconseed.common.FSStartupSettings;
import ssac.util.Classes;
import ssac.util.MacUtilities;
import ssac.util.Strings;

/**
 * FALCON-SEED ランチャー
 * 
 * @version 3.3.0	2016/05/30
 */
public final class LauncherMain implements Runnable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String NAME = "FALCON-SEED";
	static public final String BUILD = "20160531";
	
	static public final String LOCAL_VERSION = "Launcher (" + BUILD + ")";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private String	javaCommandPath = null;
	static private File	appHome = null;
	static private File	appLibDir = null;
	static private Image	appIcon = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private LauncherMain() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * アプリケーションのエントリ・ポイント
	 * 
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		//--- check command line
		try {
			// 引数確認
			for (int i = 0; i < args.length; i++) {
				if (FSStartupSettings.LANGUAGE_OPTION.equals(args[i]) && ((i+1) < args.length) && !args[i+1].equals("")) {
					// has locale
					i++;
					FSStartupSettings.setLocalLanguageOption(args[i]);
				}
				else if (FSStartupSettings.DEBUG_OPTION.equals(args[i])) {
					FSStartupSettings.setDebugOption(true);
				}
				else if (FSStartupSettings.VERBOSE_OPTION.equals(args[i])) {
					FSStartupSettings.setVerboseOption(true);
				}
				else if (FSStartupSettings.ADVANCED_OPTION.equals(args[i])) {
					FSStartupSettings.setAdvancedOption(true);
				}
			}
			
			// debug
			if (FSStartupSettings.isVerboseMode() && FSStartupSettings.isSpecifiedDebugOption()) {
				String msg = "[" + NAME + " Debug] " + formatSystemProperties();
				System.err.println(msg);
			}
			
			// 標準出力、標準エラー出力の無効化
			if (!FSStartupSettings.isVerboseMode()) {
				System.setErr(new NullPrintStream());
				System.setOut(new NullPrintStream());
			}

			// 起動設定取得
			FSStartupSettings.initialize();
			if (FSStartupSettings.isSpecifiedAdvancedOption()) {
				StartupSettings.initialize();
			}
			
			// 言語設定
			if (!FSStartupSettings.getInstance().isSpecifiedAppLanguage()) {
				FSStartupSettings.getInstance().setAppLanguage(FSStartupSettings.getDefaultLocaleWhenStartup().getLanguage());
			}
			Locale appLocale = FSStartupSettings.getAvailableAppLocale();
			if (!FSStartupSettings.isDefaultLocaleWhenStartup(appLocale)) {
				Locale.setDefault(appLocale);
			}
		}
		catch (Throwable ex) {
			String errmsg = "Failed to initialization for startup settings!";
			printLauncherError(ex, errmsg);
			showLauncherError(ex, errmsg);
			System.exit(1);
		}

		//--- アプリケーション初期化
		try {
			LauncherMain instance = new LauncherMain();
			SwingUtilities.invokeLater(instance);
		}
		catch (Throwable ex) {
			String errmsg = NAME + " cannot invoked Application context thread!";
			printLauncherError(ex, errmsg);
			showLauncherError(ex, errmsg);
			System.exit(2);
		}
	}

	/**
	 * アプリケーションのメイン・ルーチン
	 */
	public void run() {
		try {
			// Look & Feel の初期化
			JFrame.setDefaultLookAndFeelDecorated(false);	// Frame概観は、ネイティブに依存

			// Setup default look&feel
			setupDefaultLookAndFeel();
			
			// Initialize for FALCON-SEED environment
			FSEnvironment.initialize(getAppHome());
			
			// アイコンファイルのロード
			Image imgIcon = null;
			{
				try {
					File fIcon = new File(getAppHome(), "lib/resources/icon/FALCONSEED.png");
					if (fIcon.exists() && fIcon.isFile()) {
						imgIcon = Toolkit.getDefaultToolkit().createImage(fIcon.getAbsolutePath());
					}
				}
				catch (Throwable ignoreEx) {
					ignoreEx = null;
					imgIcon = null;
				}
			}
			appIcon = imgIcon;
		
			// メインフレームの初期化
			LauncherView mainFrame = new LauncherView();
			if (imgIcon != null) {
				mainFrame.setIconImage(imgIcon);
			}
			mainFrame.initialComponent();
			if (MacUtilities.isMac()) {
				MacUtilities.setupScreenMenuHandler(mainFrame);
			}
		
			// メインフレームの表示
			mainFrame.setVisible(true);
		}
		catch (Throwable ex) {
			String errmsg = NAME + " application error!";
			printLauncherError(ex, errmsg);
			showLauncherError(ex, errmsg);
			System.exit(3);
		}
	}
	
	static public final String getCurrentJavaCommandPath() {
		if (javaCommandPath == null) {
			File javahome = new File(System.getProperty("java.home"));
			String osname = System.getProperty("os.name");
			if (0 <= osname.indexOf("Windows")) {
				// windows
				File cmd = new File(javahome, "bin\\java.exe");
				if (cmd.exists()) {
					javaCommandPath = cmd.getAbsolutePath();
				} else {
					javaCommandPath = "java";
				}
			} else {
				// other OS
				File cmd = new File(javahome, "bin/java");
				if (cmd.exists()) {
					javaCommandPath = cmd.getAbsolutePath();
				} else {
					cmd = new File(javahome, "Commands/java");
					if (cmd.exists()) {
						javaCommandPath = cmd.getAbsolutePath();
					} else {
						javaCommandPath = "java";
					}
				}
			}
		}
		return javaCommandPath;
	}
	
	static public final File getAppHome() {
		if (appHome == null) {
			File fHome = Classes.getClassSource(LauncherMain.class);
			if (fHome != null && fHome.exists()) {
				if (Strings.endsWithIgnoreCase(fHome.getName(), ".jar")) {
					// this file is ".jar" on home directory.
					fHome = fHome.getParentFile();
				} else {
					// this file is LauncherMain.class file on "FALCONSEED/bin" directory.
					fHome = fHome.getParentFile();
				}
				if ("lib".equalsIgnoreCase(fHome.getName())) {
					fHome = fHome.getParentFile();
				}
			} else {
				File fCur = new File("");
				try {
					fHome = fCur.getCanonicalFile();
				} catch (Throwable ignoreEx) {
					fHome = fCur.getAbsoluteFile();
				}
			}
			appHome = fHome;
		}
		return appHome;
	}
	
	static public final File getAppLibDir() {
		if (appLibDir == null) {
			appLibDir = new File(getAppHome(), "lib");
		}
		return appLibDir;
	}
	
	static public final Image getAppIconImage() {
		return appIcon;
	}

	//------------------------------------------------------------
	// Helper methods
	//------------------------------------------------------------
	
	static public void showMessageDialog(Component parentComponent, Object message)
	throws HeadlessException
	{
		Locale l = (parentComponent==null ? Locale.getDefault() : parentComponent.getLocale());
		showMessageDialog(parentComponent, message, UIManager.getString("OptionPane.messageDialogTitle", l), JOptionPane.INFORMATION_MESSAGE);
	}
	
	static public void showMessageDialog(Component parentComponent, Object message, String title, int messageType)
	throws HeadlessException
	{
		showMessageDialog(parentComponent, message, title, messageType, null);
	}
	
	static public void showMessageDialog(Component parentComponent, Object message, String title, int messageType, Icon icon)
	throws HeadlessException
	{
		showOptionDialog(parentComponent, message, title, JOptionPane.DEFAULT_OPTION, messageType, icon, null, null);
	}
	
	static public int showOptionDialog(Component parentComponent, Object message, String title, int optionType,
											int messageType, Icon icon, Object[] options, Object initialValue)
	throws HeadlessException
	{
		JOptionPane pane = new JOptionPane(message, messageType, optionType, icon, options, initialValue);
        pane.setInitialValue(initialValue);
        pane.setComponentOrientation(((parentComponent == null) ? JOptionPane.getRootFrame() : parentComponent).getComponentOrientation());
        JDialog dialog = pane.createDialog(parentComponent, title);
        pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();
        
        Object selectedValue = pane.getValue();
        if(selectedValue == null)
        	return JOptionPane.CLOSED_OPTION;
        if(options == null) {
        	if(selectedValue instanceof Integer)
        		return ((Integer)selectedValue).intValue();
        	return JOptionPane.CLOSED_OPTION;
        }
        for(int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
        	if(options[counter].equals(selectedValue))
        		return counter;
        }
        return JOptionPane.CLOSED_OPTION;
	}
	
	static public void showLauncherError(Throwable ex, String message) {
		String desc = (message==null ? "" : message);
		if (ex != null) {
			desc = desc + "\n    " + ex.toString();
		}
		showMessageDialog(null, desc, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	static protected void printLauncherError(Throwable ex, String message) {
		if (FSStartupSettings.isVerboseMode()) {
			String msg = "[" + NAME + " Error] " + (message==null ? "" : message);
			if (ex != null) {
				msg = msg + "\n    " + ex.toString();
			}
			System.err.println(msg);
		}
	}
	
	static protected void printLauncherError(Throwable ex, String format, Object...args) {
		printLauncherError(ex, String.format(format, args));
	}
	
	static protected void printLauncherWarn(Throwable ex, String message) {
		if (FSStartupSettings.isVerboseMode()) {
			String msg = "[" + NAME + " Warning] " + (message==null ? "" : message);
			if (ex != null) {
				msg = msg + "\n    " + ex.toString();
			}
			System.err.println(msg);
		}
	}
	
	static protected void printLauncherWarn(Throwable ex, String format, Object...args) {
		printLauncherWarn(ex, String.format(format, args));
	}
	
	static protected void printLauncherDebug(Throwable ex) {
		printLauncherDebug(ex, null);
	}
	
	static protected void printLauncherDebug(Throwable ex, String message) {
		if (FSStartupSettings.isVerboseMode() && FSStartupSettings.isSpecifiedDebugOption()) {
			String msg = "[" + NAME + " Debug] " + (message==null ? "" : message);
			if (ex != null) {
				msg = msg + "\n    " + ex.toString();
			}
			System.err.println(msg);
		}
	}
	
	static protected void printLauncherDebug(Throwable ex, String format, Object...args) {
		printLauncherDebug(ex, String.format(format, args));
	}
	
	static protected boolean setupDefaultLookAndFeel() {
		String osname = System.getProperty( "os.name");
		if ( 0 <= osname.indexOf( "Windows") || 0 <= osname.indexOf( "Mac")) {
			return setup_look_and_feel( UIManager.getSystemLookAndFeelClassName());
		}
		else {
			if ( setup_look_and_feel( "javax.swing.plaf.metal.MetalLookAndFeel"))
				return true;

			return setup_look_and_feel( UIManager.getSystemLookAndFeelClassName());
		}
	}
	
	static protected boolean setup_look_and_feel(String look_and_feel) {
		try {
			UIManager.setLookAndFeel( look_and_feel);
			return true;
		}
		catch (Throwable ex) {
			printLauncherWarn(ex, "Failed to set LookAndFeel[" + String.valueOf(look_and_feel) + "]");
			return false;
		}
	}
	
	static private String formatSystemProperties() {
		StringBuilder sb = new StringBuilder();
		sb.append("[System Properties list on FALCON-SEED Launcher]\n");

		Properties sysprop = System.getProperties();
		TreeSet<String> nameset = new TreeSet<String>();
		Enumeration<?> e = sysprop.propertyNames();
		for (; e.hasMoreElements(); ) {
			nameset.add((String)e.nextElement());
		}
		
		for (String name : nameset) {
			String value = sysprop.getProperty(name);
			if (value != null) {
				sb.append(name);
				sb.append("=");
				if ("line.separator".equals(name)) {
					sb.append(value.replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
				} else {
					sb.append(value);
				}
				sb.append("\n");
			}
		}
		
		sb.append("----- end of System Propeties list on FALCON-SEED Launcher -----");
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected final class NullOutputStream extends OutputStream
	{
		public NullOutputStream() {}
		
		@Override
		public void write(int b) throws IOException {
			// No entry
		}
	}
	
	static protected final class NullPrintStream extends PrintStream
	{
		public NullPrintStream() {
			super(new NullOutputStream());
		}
	}
}
