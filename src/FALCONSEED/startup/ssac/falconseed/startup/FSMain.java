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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FSMain.java	2014/10/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FSMain.java	2012/07/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FSMain.java	2010/12/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.startup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import javax.swing.JOptionPane;

/**
 * FALCON-SEED ランチャー起動専用アプリケーション
 * 
 * @version 2014/10/20
 * @since 2010/12/24
 */
public class FSMain
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String DEBUG_OPTION = "-debug";
	
	static private final String VERBOSE_OPTION = "-verbose";
	
	static private final String JAR_LAUNCHER = "lib/fslauncher.jar";
	
	static private final String APP_DOCK_ICON	= "lib/resources/icon/FALCONSEED.icns";
	
	static private final String APP_NAME = "FALCON-SEED";
	
	static private final String SYSPROP_ENDORSED_DIRS	= "java.endorsed.dirs";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private boolean		_optionDebug = false;
	private boolean		_optionVerbose = false;
	private String 		_javaCommandPath = null;
	private List<String>	_lVMOptions;
	private List<String>	_lAppArgs;
	
	private int			_errcode = 0;
	private String			_errmsg = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private FSMain(String[] args) {
		this._lVMOptions = new ArrayList<String>();
		this._lAppArgs   = new ArrayList<String>();
		if (args != null && args.length > 0) {
			for (String arg : args) {
				if (arg != null && arg.length() > 0) {
					if (arg.startsWith("-J")) {
						// VM option
						String vmarg = arg.substring(2);
						if (vmarg.length() > 0) {
							_lVMOptions.add(vmarg);
						}
					}
					else {
						// Application argument
						_lAppArgs.add(arg);
						//--- check option
						if (DEBUG_OPTION.equals(arg)) {
							_optionDebug = true;
						}
						else if (VERBOSE_OPTION.equals(arg)) {
							_optionVerbose = true;
						}
					}
				}
			}
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * アプリケーションのエントリ・ポイント
	 * 
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		FSMain appmain = new FSMain(args);
		if (appmain.isVerboseMode()) {
			System.out.println("<<< Start '" + APP_NAME + "' startup program >>>");
		}
		if (appmain.isDebugMode()) {
			System.out.println("[Debug] " + formatSystemProperties());
		}
		int result = appmain.startup();
		if (result != 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("Failed to start " + APP_NAME + "!");
			String strmsg = appmain.getErrorMessage();
			if (strmsg != null && strmsg.length() > 0) {
				sb.append("\n\n[Reason]\n");
				sb.append(strmsg);
			}
			String strerr = sb.toString();
			if (!appmain.isVerboseMode()) {
				System.err.println("[Error] " + strerr);
			}
			JOptionPane.showMessageDialog(null, strerr, APP_NAME, JOptionPane.ERROR_MESSAGE);
			System.exit(result);
		}
		if (appmain.isVerboseMode()) {
			System.out.println("<<< '" + APP_NAME + "' startup program completed! >>>");
		}
	}
	
	private int startup() {
		File fLauncher = new File(JAR_LAUNCHER);
		if (isDebugMode()) {
			//System.out.println("[Debug] (Rel) fHome=\"" + fHome.getPath() + "\"");
			//System.out.println("[Debug] (Abs) fHome=\"" + fHome.getAbsolutePath() + "\"");
			System.out.println("[Debug] (Rel) fLauncher=\"" + fLauncher.getPath() + "\"");
			System.out.println("[Debug] (Abs) fLauncher=\"" + fLauncher.getAbsolutePath() + "\"");
		}
		try {
			// Check THIS jar
			if (!fLauncher.exists()) {
				return setError(1, "'" + JAR_LAUNCHER + "' not found!");
			}
			if (!fLauncher.isFile() || !fLauncher.canRead()) {
				return setError(1, "Cannot access to '" + JAR_LAUNCHER + "'");
			}
		}
		catch (Throwable ex) {
			StringBuilder sb = new StringBuilder();
			sb.append("Cannot access to any modules!\n(" + ex.toString() + ")");
			return setError(1, sb.toString());
		}
		
		// Launcher startup
		if (!startProcess(buildCommandForLauncher(fLauncher))) {
			return getErrorCode();
		}
		
		// succeeded
		return 0;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private boolean isVerboseMode() {
		return (_optionVerbose || _optionDebug);
	}
	
	private boolean isDebugMode() {
		return _optionDebug;
	}
//	
//	private boolean isSpecifiedDebugOption() {
//		return _optionDebug;
//	}

	private int getErrorCode() {
		return _errcode;
	}
	
	private String getErrorMessage() {
		return _errmsg;
	}
	
	private int setError(int errcode, String errmsg) {
		_errcode = errcode;
		_errmsg = errmsg;
		return errcode;
	}
	
	private String getCurrentJavaCommandPath() {
		if (_javaCommandPath == null) {
			File javahome = new File(System.getProperty("java.home"));
			String osname = System.getProperty("os.name");
			if (0 <= osname.indexOf("Windows")) {
				// windows
				File cmd = new File(javahome, "bin\\java.exe");
				if (cmd.exists()) {
					_javaCommandPath = cmd.getAbsolutePath();
				} else {
					_javaCommandPath = "java";
				}
			} else {
				// other OS
				File cmd = new File(javahome, "bin/java");
				if (cmd.exists()) {
					_javaCommandPath = cmd.getAbsolutePath();
				} else {
					cmd = new File(javahome, "Commands/java");
					if (cmd.exists()) {
						_javaCommandPath = cmd.getAbsolutePath();
					} else {
						_javaCommandPath = "java";
					}
				}
			}
		}
		return _javaCommandPath;
	}
	
	private List<String> buildCommandForLauncher(final File fLauncher) {
		ArrayList<String> cmdlist = new ArrayList<String>();
		
		// Java command
		cmdlist.add(getCurrentJavaCommandPath());
		
		// VM options
		if (!_lVMOptions.isEmpty()) {
			cmdlist.addAll(_lVMOptions);
		}
		//--- java endorsed dirs
		cmdlist.add(buildJavaEndorsedDirs(fLauncher));
		//--- for Apple Mac
		String osname = System.getProperty("os.name");
		if (0 <= osname.indexOf("Mac")) {
			//--- Set file encoding for UTF-8
			cmdlist.add("-Dfile.encoding=UTF-8");
			//--- Set use screen menu
			cmdlist.add("-Dapple.laf.useScreenMenuBar=true");
			//--- Set Dock name
			cmdlist.add("-Xdock:name=" + APP_NAME);
			//--- Set Dock Icon
			File fIcon = new File(APP_DOCK_ICON);
			try {
				if (fIcon.exists() && fIcon.isFile()) {
					cmdlist.add("-Xdock:icon=" + fIcon.getAbsolutePath());
				}
			}
			catch (Throwable ignoreEx) {ignoreEx=null;}
		}
		
		// Launcher startup command
		cmdlist.add("-jar");
		cmdlist.add(fLauncher.getPath());
		
		// Arguments for Application
		if (!_lAppArgs.isEmpty()) {
			cmdlist.addAll(_lAppArgs);
		}
		
		return cmdlist;
	}
	
	private boolean startProcess(List<String> commandList) {
		if (isDebugMode()) {
			System.out.println("Launch process...");
			for(int i = 0; i < commandList.size(); i++) {
				System.out.println("  [" + i + "]=" + String.valueOf(commandList.get(i)));
			}
		}
		
		// start Process
		ProcessBuilder pb = new ProcessBuilder(commandList);
		Process proc = null;
		try {
			proc = pb.start();
		}
		catch (IOException ex) {
			StringBuilder sb = new StringBuilder();
			sb.append("Failed to start new Process.\n(" + ex.toString() + ")");
			setError(2, sb.toString());
			return false;
		}
		if (isDebugMode()) {
			System.out.println("Launch succeeded!");
		}
		
		// start Process stream handler
		if (isVerboseMode()) {
			new StreamHandler(proc.getInputStream(), System.out).start();
			new StreamHandler(proc.getErrorStream(), System.err).start();
		}
		try {
			proc.getOutputStream().close();
			if (!isVerboseMode()) {
				proc.getInputStream().close();
				proc.getErrorStream().close();
			}
		} catch (Throwable ignoreEx) {}
		
		// OK
		return true;
	}
	
	static private String formatSystemProperties() {
		StringBuilder sb = new StringBuilder();
		sb.append("[System Properties list]\n");

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
		
		sb.append("----- end of System Propeties list -----");
		return sb.toString();
	}

	/**
	 * &quot;java.endorsed.dirs&quot; に、&quot;lib/modules&quot; ディレクトリを
	 * 追加した、新しいプロパティを指定する文字列を生成する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * これは、QGIS(正確には、QGIS の必須ライブラリである GDAL)がインストール時に、
	 * <code>commons-logging-1.1.3.jar</code> を、Java Extensions ディレクトリに
	 * 格納してしまい、この Extensions ディレクトリにライブラリが配置されると、Log4J 用 Logger
	 * 実装クラスがロードできない問題に対処するためのものである。
	 * </blockquote>
	 * @param fLauncher	FALCON-SEED アプリケーションランチャーライブラリのパス(存在していること)
	 * @return	生成されたプロパティ指定を示す文字列
	 * @since 2014/10/17
	 */
	static private String buildJavaEndorsedDirs(final File fLauncher) {
		StringBuilder sb = new StringBuilder();
		sb.append("-D");
		sb.append(SYSPROP_ENDORSED_DIRS);
		sb.append("=");
		
		File fModuleLibDir = new File(fLauncher.getAbsoluteFile().getParentFile(), "modules");
		String oldEndorsedDirs = System.getProperty(SYSPROP_ENDORSED_DIRS);
		if (oldEndorsedDirs != null && oldEndorsedDirs.length() > 0) {
			sb.append(fModuleLibDir.getPath());
			sb.append(File.pathSeparatorChar);
			sb.append(oldEndorsedDirs);
		}
		else {
			sb.append(fModuleLibDir.getPath());
		}
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class StreamHandler extends Thread
	{
		private final InputStream _inStream;
		private final PrintStream _outStream;
		
		StreamHandler(InputStream inStream) {
			this._inStream = inStream;
			this._outStream = null;
		}
		
		StreamHandler(InputStream inStream, PrintStream outStream) {
			this._inStream = inStream;
			this._outStream = outStream;
		}
		
		public void run() {
			int c;
			if (_outStream != null) {
				try {
					while ((c = _inStream.read()) >= 0) {
						_outStream.write(c);
					}
				}
				catch (IOException ignoreEx) {}
			} else {
				try {
					while ((c = _inStream.read()) >= 0) {}
				}
				catch (IOException ignoreEx) {}
			}
			try {
				_inStream.close();
			} catch (Throwable ignoreEx) {}
		}
	}
}
