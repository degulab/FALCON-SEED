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
 * @(#)JavaInfo.java	3.0.0	2014/03/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)JavaInfo.java	2.0.0	2012/10/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)JavaInfo.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ssac.util.logging.AppLogger;
import ssac.util.process.SimpleCommandExecutor;

/**
 * 実行する環境にインストールされているJavaの情報を収集するクラス。
 * Javaホームディレクトリから、Javaバージョン、Javaコマンド、Javaコンパイラの
 * 位置を収集する。
 * 
 * @version 3.0.0	2014/03/25
 */
public class JavaInfo
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String OS_WINDOWS	= "Windows";
	static private final String OS_MAC		= "Mac";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final File fHome;
	
	private File	fJavaCommand;
	private File	fCompilerJar;
	
	private String	strJavaVersion;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JavaInfo(String javahome) {
		if (javahome == null)
			throw new NullPointerException();
		this.fHome = new File(javahome).getAbsoluteFile();
	}
	
	public JavaInfo(File javahome) {
		if (javahome == null)
			throw new NullPointerException();
		this.fHome = javahome.getAbsoluteFile();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public String getOSName() {
		return System.getProperty("os.name");
	}
	
	static public String getEnvironmentJavaHome() {
		return System.getenv("JAVA_HOME");
	}
	
	static public String getSystemPropertyJavaHome() {
		return System.getProperty("java.home");
	}
	
	static public boolean isWindows() {
		String osname = getOSName();
		int idx = osname.indexOf(OS_WINDOWS);
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("OS=[" + osname + "]  This is " + (idx >= 0 ? "" : "not ") + "Windows.");
		}
		return (idx >= 0);
	}

	static public boolean isMac() {
		String osname = getOSName();
		int idx = osname.indexOf(OS_MAC);
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("OS=[" + osname + "]  This is " + (idx >= 0 ? "" : "not ") + "Mac.");
		}
		return (idx >= 0);
	}
	
	public void clear() {
		this.fJavaCommand = null;
		this.fCompilerJar = null;
		this.strJavaVersion = null;
	}
	
	public File getHomeFile() {
		return this.fHome;
	}
	
	public String getHomePath() {
		return this.fHome.getAbsolutePath();
	}
	
	public File getCommandFile() {
		return this.fJavaCommand;
	}
	
	public String getCommandPath() {
		return (this.fJavaCommand != null ? this.fJavaCommand.getAbsolutePath() : null);
	}
	
	public File getCompilerFile() {
		return this.fCompilerJar;
	}
	
	public String getCompilerPath() {
		return (this.fCompilerJar != null ? this.fCompilerJar.getAbsolutePath() : null);
	}
	
	public String getVersion() {
		return this.strJavaVersion;
	}
	
	public void collect() {
		if (isWindows()) {
			collectForWindows();
		} else {
			collectForOthers();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void collectForWindows() {
		// パス検証
		File targetHome = this.fHome;
		String strCurName = this.fHome.getName();
		if (strCurName.equalsIgnoreCase("jre")) {
			targetHome = this.fHome.getParentFile();
		}
		
		// コマンド検索
		this.fJavaCommand = getExistFile(targetHome, "bin\\java.exe");
		//--- バージョン情報取得
		if (this.fJavaCommand != null) {
			try {
				this.strJavaVersion = getJavaVersion(this.fJavaCommand.getAbsolutePath());
			}
			catch (Throwable ex) {
				AppLogger.debug("Failed to exec \"" + this.fJavaCommand.getAbsolutePath() + "\" -version", ex);
			}
		}
		
		// コンパイラ検索
		File fJar = getExistFile(targetHome, "lib\\tools.jar");
		if (fJar == null) {
			return;		// not found;
		}
		if (!isIncludeJavaCompilerClass(fJar)) {
			return;		// not found;
		}
		this.fCompilerJar = fJar;
	}
	
	private void collectForOthers() {
		// パス検証
		File targetHome = this.fHome;
		String strCurName = this.fHome.getName();
		if (strCurName.equalsIgnoreCase("jre") || strCurName.equalsIgnoreCase("home")) {
			targetHome = this.fHome.getParentFile();
		}
		AppLogger.debug("Collect target : " + targetHome.getAbsolutePath());
		
		// コマンド検索
		this.fJavaCommand = getExistFile(targetHome, "bin/java");
		if (this.fJavaCommand == null) {
			this.fJavaCommand = getExistFile(targetHome, "Commands/java");
		}
		//--- java 7 対応
		if (this.fJavaCommand == null) {
			this.fJavaCommand = getExistFile(targetHome, "Home/bin/java");
		}
		//--- バージョン情報取得
		if (this.fJavaCommand != null) {
			try {
				this.strJavaVersion = getJavaVersion(this.fJavaCommand.getAbsolutePath());
			}
			catch (Throwable ex) {
				AppLogger.debug("Failed to exec \"" + this.fJavaCommand.getAbsolutePath() + "\" -version", ex);
			}
		}
		
		// コンパイラ検索
		File fJar = getExistFile(targetHome, "lib/tools.jar");
		if (fJar == null) {
			fJar = getExistFile(targetHome, "Classes/classes.jar");
			if (fJar == null) {
				//--- java 7 対応
				fJar = getExistFile(targetHome, "Home/lib/tools.jar");
				if (fJar == null) {
					return;		// not found
				}
			}
		}
		if (!isIncludeJavaCompilerClass(fJar)) {
			return;		// not found
		}
		this.fCompilerJar = fJar;
	}
	
	private String getJavaVersion(String javaCmdPath) throws IOException
	{
//		// コマンド実行
//		CommandExecutor exec = new CommandExecutor(javaCmdPath, "-version");
//		exec.start();
//		try {
//			exec.waitFor(5000);	// wait for command, limit 5 seconds
//		} catch (InterruptedException ex) {}
//		exec.destroy();
//		if (AppLogger.isInfoEnabled()) {
//			AppLogger.info("[" + exec.getProcessIdentifierString() + "] Java command stopped! --- Exit code : " + exec.getExitCode());
//		}
//		
//		// 文字列取得
//		StringBuffer sb = new StringBuffer();
//		while (!exec.getCommandOutput().isEmpty()) {
//			OutputString ostr = exec.getCommandOutput().pop();
//			sb.append(ostr.getString());
//		}
//		String str = sb.toString();
//		exec = null;
//		sb = null;
		
		// コマンド実行
		SimpleCommandExecutor exec = new SimpleCommandExecutor(javaCmdPath, "-version");
		exec.redirectErrorStream(true);	// 標準エラー出力は標準出力へリダイレクト
		exec.exec(5000L);	// 最大5秒待つ
		
		// バージョン番号取得
		String str = exec.getOutputString();
		int sidx = str.indexOf('"');
		int eidx = str.indexOf('"', sidx+1);
		if (sidx >= 0 && eidx >= 0 && (sidx+1) < eidx) {
			str = str.substring(sidx+1, eidx);
		} else {
			str = null;
		}
		return str;
	}

	// 基準パスと相対パスから、ファイルの有無を確認する
	private File getExistFile(File fHome, String relPath) {
		File file = new File(fHome, relPath);
		
		try {
			if (file.exists() && file.isFile()) {
				file = file.getAbsoluteFile();
			} else {
				// not file
				AppLogger.debug("File not found : " + file.getAbsolutePath());
				file = null;
			}
		} catch (Throwable ex) {
			AppLogger.debug("Failed to File operation for \"" + file.getAbsolutePath() + "\"", ex);
			file = null;
		}
		
		return file;
	}

	// 指定のファイルに Javaコンパイラ・クラスが含まれているかを検証する
	// 検証対象クラス：com.sun.tools.javac.Main
	private boolean isIncludeJavaCompilerClass(File jarFile) {
		boolean flgExist;
		ZipFile fZip = null;
		try {
			// Zip ファイルとしてエントリを検証
			fZip = new ZipFile(jarFile);
			ZipEntry entry = fZip.getEntry("com/sun/tools/javac/Main.class");
			flgExist = (entry != null);
			entry = null;
		}
		catch (Throwable ex) {
			AppLogger.debug("File is not ZIP \"" + jarFile.getAbsolutePath() + "\"");
			flgExist = false;
		}
		finally {
			if (fZip != null) {
				try {
					fZip.close();
				} catch (IOException ignoreEx) {
					ignoreEx = null;	// ignore exception
				}
				fZip = null;
			}
		}
		return flgExist;
	}
}
