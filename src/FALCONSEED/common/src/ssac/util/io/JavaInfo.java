/*
 * @(#)JavaInfo.java	4.0.0	2021/08/23 : for Java11
 *     - modified by Y.Ishizuka(PieCake.inc,)
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
import java.util.ArrayList;
import java.util.List;

import ssac.util.logging.AppLogger;
import ssac.util.process.SimpleCommandExecutor;

/**
 * 実行する環境にインストールされているJavaの情報を収集するクラス。
 * Javaホームディレクトリから、Javaバージョン、Javaコマンド、Javaコンパイラの
 * 位置を収集する。
 * 
 * @version 4.0.0
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
	private File	fJavacCommand;
	private File	fCompilerJar;
	
	private int		iJavaMajorVersion;
	private String	strJavaVersion;
	private String	strCompilerVersion;

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
	
	public File getJavacCommandFile() {
		return this.fJavacCommand;
	}
	
	public String getJavacCommandPath() {
		return (this.fJavacCommand != null ? this.fJavacCommand.getAbsolutePath() : null);
	}
	
	public File getCompilerJarFile() {
		return this.fCompilerJar;
	}
	
	public String getCompilerJarPath() {
		return (this.fCompilerJar != null ? this.fCompilerJar.getAbsolutePath() : null);
	}
	
	public String getVersionString() {
		return this.strJavaVersion;
	}
	
	public int getMajorVersionNumber() {
		return this.iJavaMajorVersion;
	}
	
	public String getCompilerVersionString() {
		return this.strCompilerVersion;
	}
	
	public boolean existJavaCommand() {
		return (this.fJavaCommand != null);
	}
	
	public boolean existJavaCompiler() {
		return (this.strCompilerVersion != null);
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
		if (!collectJavaVersion()) {
			// error
			return;
		}
		
		// コンパイラ検索
		this.fJavacCommand = getExistFile(targetHome, "bin\\javac.exe");
		this.fCompilerJar = getExistFile(targetHome, "lib\\tools.jar");
		this.strCompilerVersion = getJavaCompilerVersion(this.fJavaCommand.getAbsolutePath(),
				(this.fCompilerJar != null ? this.fCompilerJar.getAbsolutePath() : null));
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
			this.fJavaCommand = getExistFile(targetHome, "Home/bin/java");
		}
		if (this.fJavaCommand == null) {
			this.fJavaCommand = getExistFile(targetHome, "Commands/java");
		}
		//--- バージョン情報取得
		if (!collectJavaVersion()) {
			// error
			return;
		}
		
		// コンパイラ検索
		this.fJavacCommand = getExistFile(targetHome, "bin\\javac.exe");
		if (this.fJavacCommand == null) {
			this.fJavacCommand = getExistFile(targetHome, "Home/bin/javac");
		}
		if (this.fJavacCommand == null) {
			this.fJavacCommand = getExistFile(targetHome, "Commands/javac");
		}
		this.fCompilerJar = getExistFile(targetHome, "lib/tools.jar");
		if (this.fCompilerJar == null) {
			this.fCompilerJar = getExistFile(targetHome, "Home/lib/tools.jar");
		}
		if (this.fCompilerJar == null) {
			this.fCompilerJar = getExistFile(targetHome, "Home/lib/tools.jar");
		}
		this.strCompilerVersion = getJavaCompilerVersion(this.fJavaCommand.getAbsolutePath(),
									(this.fCompilerJar != null ? this.fCompilerJar.getAbsolutePath() : null));
	}
	
	private boolean collectJavaVersion()
	{
		if (this.fJavaCommand == null) {
			return false;	// java command does not exist
		}
		
		// コマンド実行
		try {
			this.strJavaVersion = getJavaVersion(this.fJavaCommand.getAbsolutePath());
		}
		catch (Throwable ex) {
			AppLogger.debug("Failed to exec \"" + this.fJavaCommand.getAbsolutePath() + "\" -version", ex);
		}
		this.iJavaMajorVersion = getJavaMajorVersionFromVersionString(this.strJavaVersion);
		return (this.iJavaMajorVersion > 0);
	}
	
	private String getJavaCompilerVersion(String javaCmdPath, String compilerJarPath)
	{
		// コマンド引数
		ArrayList<String> cmdlist = new ArrayList<String>();
		cmdlist.add(javaCmdPath);
		if (compilerJarPath != null) {
			cmdlist.add("-cp");
			cmdlist.add(compilerJarPath);
		}
		cmdlist.add("com.sun.tools.javac.Main");
		cmdlist.add("-version");
		
		// コマンド実行
		int exitcode = 0;
		String verstr = null;
		try {
			SimpleCommandExecutor exec = new SimpleCommandExecutor(cmdlist);
			exec.redirectErrorStream(true);	// 標準エラー出力は標準出力へリダイレクト
			exitcode = exec.exec(5000L);	// 最大5秒待つ
			if (exitcode == 0) {
				verstr = exec.getOutputString();
			}
		}
		catch (Throwable ex) {
			AppLogger.debug("Failed to exec " + formatCommandArgs(cmdlist), ex);
			return null;
		}
		if (exitcode != 0) {
			AppLogger.debug("Error(" + String.valueOf(exitcode) + ") : exec " + formatCommandArgs(cmdlist));
			return null;
		}
		if (verstr == null || verstr.isEmpty()) {
			return null;
		}
		
		// バージョン番号取得
		if (verstr.startsWith("javac ")) {
			return verstr.substring("javac ".length());
		} else {
			return null;
		}
	}
	
	private String formatCommandArgs(List<String> cmdlist)
	{
		StringBuilder sb = new StringBuilder();
		for (String arg : cmdlist) {
			if (sb.length() > 0) {
				sb.append(' ');
			}
			if (arg.indexOf(' ') >= 0) {
				sb.append('\"');
				sb.append(arg);
				sb.append('\"');
			}
			else {
				sb.append(arg);
			}
		}
		return sb.toString();
	}
	
	private String getJavaVersion(String javaCmdPath) throws IOException
	{
		// コマンド実行
		SimpleCommandExecutor exec = new SimpleCommandExecutor(javaCmdPath, "-version");
		exec.redirectErrorStream(true);	// 標準エラー出力は標準出力へリダイレクト
		int exitcode = exec.exec(5000L);	// 最大5秒待つ
		if (exitcode != 0) {
			AppLogger.debug("Error(" + String.valueOf(exitcode) + ") : exec \"" + this.fJavaCommand.getAbsolutePath() + "\" -version");
			return null;
		}
		
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
	
	private int getJavaMajorVersionFromVersionString(String verstr)
	{
		if (verstr != null && !verstr.isEmpty()) {
			if (verstr.startsWith("1.")) {
				verstr = verstr.substring(2);
			}
			try {
				return Integer.parseInt( verstr.substring(0, verstr.indexOf('.')) );
			}
			catch (Throwable ex) {
				// unexpected version string
				return 0;
			}
		}
		else {
			// unexpected version string
			return 0;
		}
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

	/*** deleted : 2021-08-23 ***
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
	/*** ***/
}
