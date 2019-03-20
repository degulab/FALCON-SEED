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
 * @(#)MqttCachedSession.java	0.4.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MqttCachedSession.java	0.2.0	2013/05/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.mqtt;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.util.Locale;

import org.fusesource.mqtt.client.MqClient;

import ssac.aadl.runtime.mqtt.internal.MqttBufferedSessionImpl;
import ssac.aadl.runtime.mqtt.internal.MqttManager;

/**
 * MQTT ユーティリティ。
 * 
 * @version 0.4.0	2014/05/29
 * @since 0.2.0
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MqttUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * MQTT クライアントのトレースメッセージ表示フラグのプロパティ・キー
	 */
	static public final String MQTT_CLIENT_TRACE	= MqClient.MQTT_CLIENT_TRACE_PROPERTY;

	/**
	 * 接続パラメータとして指定するクライアントIDの最大長。
	 */
	static public final int MAX_CLIENTID_LENGTH = 23;

	/**
	 * 接続時のデフォルトサーバーアドレス。
	 */
    static public final String DEFAULT_IP_ADDRESS = "127.0.0.1";
    /**
     * 接続時のデフォルトポート番号
     */
    static public final int DEFAULT_PORT_NUMBER = 1883;

    static protected final int		MAX_RANDOM_DIGITS = 15;
    static protected final String	MQTT_CLIENT_JAR	= "mqtt-client-1.5-uber.jar";
    static protected final File	_MqttUtilJarDir;
    
    static {
    	_MqttUtilJarDir = getMqttUtilJarDir();
//    	loadMqttClientLibrary();
    }

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static public final PrintStream	NullPrintStream	= new NullPrintStreamImpl();
	
	static public PrintStream	out		= System.out;
	static public PrintStream	err		= System.err;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	protected MqttUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたオブジェクトが <tt>null</tt> かどうか判定する。
	 * @param obj	判定するオブジェクト
	 * @return	<tt>null</tt> なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static public boolean isNull(Object obj) {
		return (obj == null);
	}

	/**
	 * ランダムなクライアントIDを生成する。
	 * @return	生成されたクライアントID
	 */
	static public String generateClientID() {
		return generateClientID(null);
	}

	/**
	 * 指定されたプレフィックスを持つ、ランダムなクライアントIDを生成する。
	 * なお、ここで生成されるクライアントIDは 23 文字以内になるため、
	 * <em>prefix</em> の文字数によって前に生成したものと重複する
	 * 可能性が高くなる。
	 * なお、<em>prefix</em> が <tt>null</tt> もしくは空文字の場合は、
	 * {@link #generateClientID()} と同じ結果を返す。
	 * @param prefix	23 文字以下の文字列
	 * @return	生成されたクライアントID
	 * @throws IllegalArgumentException	<em>prefix</em> が 23 文字よりも大きい場合
	 */
	static public String generateClientID(String prefix) {
		if (prefix == null || prefix.length() <= 0) {
			prefix = System.getProperty("user.name");
			if (prefix.length() > MAX_CLIENTID_LENGTH) {
				prefix = prefix.substring(0, MAX_CLIENTID_LENGTH);
			}
		}
		else if (prefix.length() > MAX_CLIENTID_LENGTH) {
			throw new IllegalArgumentException("prefix is greater than " + MAX_CLIENTID_LENGTH + " characters : " + prefix);
		}
		
		int remain = MAX_CLIENTID_LENGTH - prefix.length();
		if (remain > 0) {
			//String suffix = String.valueOf(System.currentTimeMillis());
			BigDecimal bv = BigDecimal.valueOf(10L).pow(Math.min(remain, MAX_RANDOM_DIGITS)).subtract(BigDecimal.ONE);
			long rlv = Math.abs(bv.multiply(BigDecimal.valueOf(Math.random())).longValue());
			String suffix = String.valueOf(rlv);
			if (suffix.length() > remain) {
				suffix = suffix.substring(suffix.length() - remain);
			}
			return (prefix + suffix);
		}
		else {
			return prefix;
		}
	}
	
	/**
	 * 接続先サーバーの URI を取得する。
	 * @param session	対象のセッション
	 * @return	サーバー URI
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public String getServerURI(MqttSession session) {
		return session.getServerURI();
	}

	/**
	 * 現在設定されているクライアントIDを返す。
	 * @param session	対象のセッション
	 * @return	クライアントID
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public String getClientID(MqttSession session) {
		return session.getClientID();
	}

	/**
	 * 指定されたセッションが、サーバーに接続されているかを判定する。
	 * @param session	対象のセッション
	 * @return	サーバーに接続されていれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static public boolean isConnected(MqttSession session) {
		return (session==null ? false : session.isConnected());
	}

	/**
	 * このセッションが何らかの理由により切断された場合に <tt>true</tt> を返す。
	 * 接続中の場合や、正常に切断されている場合、このメソッドは <tt>false</tt> を返す。
	 * @param session	対象のセッション
	 * @return	何らかの理由により切断された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static public boolean isConnectionLost(MqttSession session) {
		return (session==null ? false : session.isConnectionLost());
	}

	/**
	 * セッションが何らかの理由に切断されたときの、要因を示す例外オブジェクトを返す。
	 * 接続中の場合や、正常に切断されている場合、このメソッドは <tt>null</tt> を返す。
	 * @param session	対象のセッション
	 * @return	何らかの理由によりセッションが切断されたときの要因である例外オブジェクトを返す。
	 * 			接続中もしくは正常に切断されている場合は <tt>null</tt> を返す。
	 */
	static public Throwable getConnectionLostCause(MqttSession session) {
		return (session==null ? null : session.getConnectionLostCause());
	}

	/**
	 * 指定されたセッションのサーバーとの接続を切断する。
	 * @param session	対象のセッション
	 */
	static public void disconnect(MqttSession session) {
		if (session != null && session.isConnected()) {
			session.disconnect();
		}
	}

	/**
	 * ローカルホスト（127.0.0.1）の 1883 番ポートのサーバーに接続し、バッファリング可能な新しいセッションを開始する。
	 * <p>クライアントID には、ランダムなクライアントIDが自動的に設定される。
	 * @return <code>MqttBufferedSession</code> オブジェクト
	 * @throws MqttRuntimeException	接続できなかった場合
	 */
	static public MqttBufferedSession connectBufferedSession() {
		return connectBufferedSession(new MqttConnectionParams());
	}

	/**
	 * 指定された接続パラメータでサーバーに接続し、バッファリング可能な新しいセッションを開始する。
	 * <em>serverURI</em> にポート番号が含まれていない場合、1883 番ポートが指定される。
	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
	 * <p>クライアントID には、ランダムなクライアントIDが自動的に設定される。
	 * @param serverURI	接続先を示す URI
	 * @return <code>MqttBufferedSession</code> オブジェクト
	 * @throws IllegalArgumentException	<em>serverURI</em> に含まれるスキーマがサポートされていない場合
	 * @throws MqttRuntimeException	接続できなかった場合
	 */
	static public MqttBufferedSession connectBufferedSession(String serverURI) {
		return connectBufferedSession(new MqttConnectionParams(serverURI, generateClientID()));
	}

	/**
	 * 指定された接続パラメータでサーバーに接続し、バッファリング可能な新しいセッションを開始する。
	 * <em>serverURI</em> にポート番号が含まれていない場合、1883 番ポートが指定される。
	 * <em>serverURI</em> にプロトコルが指定されていない場合、&quot;tcp://&quot; が指定される。
	 * <em>serverURI</em> が <tt>null</tt> もしくは空文字の場合、ローカルIPアドレスが指定される。
	 * <em>clientID</em> が <tt>null</tt> もしくは空文字の場合、ランダムなクライアントIDが設定される。
	 * @param serverURI	接続先を示す URI
	 * @param clientID	23文字以下のクライアントID
	 * @return <code>MqttBufferedSession</code> オブジェクト
	 * @throws IllegalArgumentException	<em>serverURI</em> に含まれるスキーマがサポートされていない場合、または <em>clientID</em> が 23 文字よりも大きい場合
	 * @throws MqttRuntimeException	接続できなかった場合
	 */
	static public MqttBufferedSession connectBufferedSession(String serverURI, String clientID) {
		return connectBufferedSession(new MqttConnectionParams(serverURI, clientID));
	}

	/**
	 * 指定された接続パラメータでサーバーに接続し、バッファリング可能な新しいセッションを開始する。
	 * @param params	接続パラメータ
	 * @return <code>MqttBufferedSession</code> オブジェクト
	 * @throws NullPointerException	<em>params</em> が <tt>null</tt> の場合
	 * @throws MqttRuntimeException	接続できなかった場合
	 */
	static public MqttBufferedSession connectBufferedSession(MqttConnectionParams params) {
		MqttBufferedSessionImpl session = new MqttBufferedSessionImpl(params);
		session.connect();
		return session;
	}

	/**
	 * 現在接続されているすべてのセッションを切断する。
	 * このメソッドでは、切断時に発生した例外をすべて無視する。
	 */
	static public void disconnectAllSessions() {
		MqttManager.getInstance().cleanup();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このクラスの Jar が配置されているディレクトリを取得する。
	 * @return	このクラスの Jar が配置されているディレクトリへの絶対パスを返す。
	 * 			Jar の位置が取得できない場合はカレントディレクトリへの絶対パスを返す。
	 */
	static protected File getMqttUtilJarDir() {
		File dir = getClassSource(MqttUtil.class);
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

//	/*
//	 * MQTT ライブラリがロードされていない場合にロードする。
//	 */
//	static protected void loadMqttClientLibrary() {
//		// load MQTT class from current class paths
//		boolean foundLib;
//		try {
//			Class.forName("org.fusesource.mqtt.client.MQTT");
//			foundLib = true;
//		} catch (ClassNotFoundException ex) {
//			foundLib = false;
//		}
//		if (foundLib)
//			return;		// Class found!
//		
//		// check MQTT Client Library
//		File libJar = new File(_MqttUtilJarDir, MQTT_CLIENT_JAR);
//		if (!libJar.exists()) {
//			String devRelPath = "lib" + File.separatorChar + "MQTT" + File.separatorChar + "client" + File.separatorChar  + MQTT_CLIENT_JAR;
//			libJar = new File(_MqttUtilJarDir, devRelPath);
//			if (!libJar.exists()) {
//				return;	// Jar not found!
//			}
//		}
//		
//		// load MQTT Client Library
//		try {
//			// get class loader
//			URLClassLoader loader;
//			ClassLoader cl = MqttUtil.class.getClassLoader();
//			if (cl instanceof URLClassLoader) {
//				loader = (URLClassLoader)cl;
//			} else {
//				loader = (URLClassLoader)ClassLoader.getSystemClassLoader();
//			}
//			
//			// add class path
//			URL url = libJar.toURI().toURL();
//			Method md = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
//			md.setAccessible(true);
//			md.invoke(loader, url);
//		}
//		catch (Throwable ignoreEx) {
//			ignoreEx = null;
//		}
//		
//		// check class loading
////		try {
////			Class.forName("org.fusesource.mqtt.client.MQTT");
////			foundLib = true;
////		} catch (ClassNotFoundException ex) {
////			foundLib = false;
////		}
//		new MqttConnectionParams();
//	}
	
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
		ClassLoader cl = (classLoader == null ? MqttUtil.class.getClassLoader() : classLoader); 
		
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
	
	static protected class EmptyOutputStream extends OutputStream
	{
		@Override
		public void write(int b) throws IOException {}
	}
	
	static protected class NullPrintStreamImpl extends PrintStream
	{
		public NullPrintStreamImpl() {
			super(new EmptyOutputStream());
		}

		@Override
		public void flush() {}

		@Override
		public void close() {}

		@Override
		public void write(int b) {}

		@Override
		public void write(byte[] buf, int off, int len) {}

		@Override
		public void print(boolean b) {}

		@Override
		public void print(char c) {}

		@Override
		public void print(int i) {}

		@Override
		public void print(long l) {}

		@Override
		public void print(float f) {}

		@Override
		public void print(double d) {}

		@Override
		public void print(char[] s) {}

		@Override
		public void print(String s) {}

		@Override
		public void print(Object obj) {}

		@Override
		public void println() {}

		@Override
		public void println(boolean x) {}

		@Override
		public void println(char x) {}

		@Override
		public void println(int x) {}

		@Override
		public void println(long x) {}

		@Override
		public void println(float x) {}

		@Override
		public void println(double x) {}

		@Override
		public void println(char[] x) {}

		@Override
		public void println(String x) {}

		@Override
		public void println(Object x) {}

		@Override
		public PrintStream printf(String format, Object... args) {
			return this;
		}

		@Override
		public PrintStream printf(Locale l, String format, Object... args) {
			return this;
		}

		@Override
		public PrintStream format(String format, Object... args) {
			return this;
		}

		@Override
		public PrintStream format(Locale l, String format, Object... args) {
			return this;
		}

		@Override
		public PrintStream append(CharSequence csq) {
			return this;
		}

		@Override
		public PrintStream append(CharSequence csq, int start, int end) {
			return this;
		}

		@Override
		public PrintStream append(char c) {
			return this;
		}

		@Override
		public void write(byte[] b) throws IOException {}
	}
}
