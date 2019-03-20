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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MoquetteBrokerLauncher.java	0.3.0	2013/06/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MoquetteBrokerLauncher.java	0.1.0	2013/05/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mqtt.broker;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import ssac.falconseed.mqtt.broker.util.ConsoleMonitorAppender;
import ssac.falconseed.mqtt.broker.util.MoquetteServer;

/**
 * Moquette Micro Broker を起動するユーティリティ。
 * 
 * @version 0.3.0	2013/06/30
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public final class MoquetteBrokerLauncher
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

    static protected final String	MQTT_MOQUETTE_JAR	= "moquette-broker-0.1-jar-with-dependencies.jar";
    static protected final File	_ThisClassJarDir;
    
    static {
    	_ThisClassJarDir = getClassJarDir();
    }
	
	static protected MoquetteServer	_inprocServer = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected MoquetteBrokerLauncher() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 現在のプロセス内で、<code>GUI</code> を持たない <code>Moquette Broker</code> を起動する。
	 * このメソッドでは、<code>Moquette</code> のメッセージを標準出力へ出力する。
	 * @throws IOException	サーバー起動に失敗した場合
	 */
	static public synchronized void startBrokerInprocess() throws IOException
	{
		startBrokerInprocess(true);
	}
	
	/**
	 * 現在のプロセス内で、<code>GUI</code> を持たない <code>Moquette Broker</code> を起動する。
	 * このメソッドでは、<code>Moquette</code> のメッセージを出力しない。
	 * @throws IOException	サーバー起動に失敗した場合
	 */
	static public synchronized void startBrokerInprocessSilent() throws IOException
	{
		startBrokerInprocess(false);
	}

	/**
	 * 現在のプロセス内で実行されている <code>Moquette Broker</code> を停止する。
	 */
	static public synchronized void stopBrokerInprocess() {
		if (_inprocServer != null) {
			_inprocServer.stopServer();
			_inprocServer = null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected void startBrokerInprocess(boolean verbose) throws IOException
	{
		if (_inprocServer == null) {
			loadMoquetteLibrary();
			if (verbose) {
				// verbose mode
				ConsoleMonitorAppender.setOutputQueueing(false);
				ConsoleMonitorAppender.setOutputWriteToStream(true);
			} else {
				// silent mode
				ConsoleMonitorAppender.setOutputQueueing(false);
				ConsoleMonitorAppender.setOutputWriteToStream(false);
			}
			
			MoquetteServer server = new MoquetteServer();
			server.startServer();
			_inprocServer = server;
			
			Runtime.getRuntime().addShutdownHook(new Thread(){
				@Override
				public void run() {
					stopBrokerInprocess();
				}
			});
		}
	}

	/**
	 * このクラスの Jar が配置されているディレクトリを取得する。
	 * @return	このクラスの Jar が配置されているディレクトリへの絶対パスを返す。
	 * 			Jar の位置が取得できない場合はカレントディレクトリへの絶対パスを返す。
	 */
	static protected File getClassJarDir() {
		File dir = getClassSource(MoquetteBrokerLauncher.class);
		if (dir == null || !endsWithIgnoreCase(dir.getPath(), ".jar")) {
			// get current directory
			dir = new File(".").getAbsoluteFile();
			if (dir.getPath().endsWith(File.separator + ".")) {
				dir = dir.getParentFile();
			}
		} else {
			dir = dir.getParentFile();
		}
		return dir;
	}

	/**
	 * Moquette ライブラリがロードされていない場合にロードする。
	 */
	static protected void loadMoquetteLibrary() {
		// load org.dna.mqtt.commons.Constants class from current class paths
		boolean foundLib;
		try {
			Class.forName("org.dna.mqtt.commons.Constants");
			foundLib = true;
		} catch (ClassNotFoundException ex) {
			foundLib = false;
		}
		if (foundLib)
			return;		// Class found!
		
		// check Target Library
		File libJar = new File(_ThisClassJarDir, MQTT_MOQUETTE_JAR);
		if (!libJar.exists()) {
			String devRelPath = "lib" + File.separatorChar + "MQTT" + File.separatorChar + "broker" + File.separatorChar + "moquette" + File.separatorChar + MQTT_MOQUETTE_JAR;
			libJar = new File(_ThisClassJarDir, devRelPath);
			if (!libJar.exists()) {
				return;	// Jar not found!
			}
		}
		
		// load Target Library
		try {
			// get class loader
			URLClassLoader loader;
			ClassLoader cl = MoquetteBrokerLauncher.class.getClassLoader();
			if (cl instanceof URLClassLoader) {
				loader = (URLClassLoader)cl;
			} else {
				loader = (URLClassLoader)ClassLoader.getSystemClassLoader();
			}
			
			// add class path
			URL url = libJar.toURI().toURL();
			Method md = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			md.setAccessible(true);
			md.invoke(loader, url);
		}
		catch (Throwable ignoreEx) {
			ignoreEx = null;
		}
		
		// check class loading
//		try {
//			Class.forName("org.dna.mqtt.commons.Constants");
//			foundLib = true;
//		} catch (ClassNotFoundException ex) {
//			foundLib = false;
//		}
	}
	
	static protected boolean endsWithIgnoreCase(String str, String suffix) {
		// 同じインスタンスなら true
		if (str == suffix)
			return true;
		
		// suffix の長さによる判定
		int slen = suffix.length();
		if (slen <= 0)
			return true;
		else if (slen > str.length())
			return false;	// suffix のほうが長い場合は false
		
		// 判定
		String target = str.substring(str.length() - slen);
		return suffix.equalsIgnoreCase(target);
	}

	/**
	 * 指定されたクラスのリソースが格納されているクラスパスを
	 * 取得する。このメソッドが返すパスは絶対パスとなる。
	 * 
	 * @param clazz		対象クラス
	 * @return	クラスリソースが格納されているクラスパス
	 * 
	 * @throws IllegalArgumentException	クラスが見つからない場合
	 */
	static protected final File getClassPath(String className) {
		Class<?> clazz;
		try {
			clazz = Class.forName(className);
		} catch (Throwable ex) {
			throw new IllegalArgumentException(ex);
		}
		return getClassSource(clazz);
	}

	/**
	 * 指定したクラスのロード元となるファイルを取得する
	 * 
	 * @param clazz 元ファイルを取得するクラス
	 * @return 取得したファイルを返す。取得できない場合は null を返す。
	 */
	static protected final File getClassSource(final Class<?> clazz) {
		String clazzResource = clazz.getName().replace('.','/') + ".class";
		return getResourceSource(clazz.getClassLoader(), clazzResource);
	}

	/**
	 * クラスローダーから、指定したリソースのファイルを取得する。
	 * 
	 * @param classLoader クラスローダー
	 * @param resource リソース
	 * @return 取得したファイルを返す。取得できない場合は null を返す。
	 */
	static protected final File getResourceSource(final ClassLoader classLoader, final String resource) {
		ClassLoader cl = (classLoader == null ? MoquetteBrokerLauncher.class.getClassLoader() : classLoader); 
		
		URL url = null;
		if (cl == null) {
			url = ClassLoader.getSystemResource(resource);
		} else {
			url = cl.getResource(resource);
		}
		if (url != null) {
			String strurl = url.toString();
			if (strurl.startsWith("jar:file:")) {
				int pling = strurl.indexOf("!");
				String jarName = strurl.substring(4, pling);
				return new File(getPathFromURI(jarName));
			}
			else if (strurl.startsWith("file:")) {
				int tail = strurl.indexOf(resource);
				String dirName = strurl.substring(0, tail);
				return new File(getPathFromURI(dirName));
			}
		}
		// not found!
		return null;
	}
	
	/**
	 * <code>"file:"</code> で始まる URI から、ファイルパスを生成する。
	 * ファイルパスの生成において、<code>'?'</code> で始まるURLパラメータは無視する。
	 * 
	 * @param strURI	対象のURI
	 * @return ファイルパス
	 * 
	 * @throws IllegalArgumentException	URIが正しくない場合、もしくは'file:'プロトコルではない場合
	 */
	static protected final String getPathFromURI(final String strURI) {
		if (strURI == null) {
			throw new NullPointerException("strURI is null!");
		}
		URI uri = null;
		try {
			uri = new URI(strURI);
			uri.normalize();
		}
		catch (Throwable ex) {
			throw new IllegalArgumentException("Cannot create URI instance by \"" + strURI + "\".", ex);
		}
		
		if (uri == null || !("file".equals(uri.getScheme()))) {
			throw new IllegalArgumentException("Can only handle valid file: URIs");
		}
		
		StringBuffer sb = new StringBuffer();

		// ホスト名
		String strHost = uri.getHost();
		if (strHost != null && strHost.length() > 0) {
			sb.append(File.separatorChar);
			sb.append(File.separatorChar);
			sb.append(strHost);
		}

		// URLパラメータの除外
		String strPath = uri.getPath();
		if (strPath == null) {
			// 相対パスの可能性
			strPath = uri.getSchemeSpecificPart();
			if (!strPath.startsWith("/")) {
				// 相対パスの場合は、カレントディレクトリから絶対URIを生成
				File base = new File(".").getAbsoluteFile();
				if (base.getPath().endsWith(File.separator + ".")) {
					base = base.getParentFile();
				}
				uri = base.toURI().resolve(strPath);
				uri.toString();
				strPath = uri.getPath();
			}
		}
		int pos = strPath.indexOf('?');
		sb.append((pos < 0) ? strPath : strPath.substring(0, pos));
		
		// ファイルパス
		strPath = sb.toString().replace('/', File.separatorChar);
		if (File.pathSeparatorChar == ';' && strPath.startsWith("\\") && strPath.length() > 2 &&
			Character.isLetter(strPath.charAt(1)) && strPath.charAt(2) == ':')
		{
			// 絶対パスのドライブ文字の前に File.separatorChar がある場合は、削除(Windows)
			strPath = strPath.substring(1);
		}
		
		return strPath;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
