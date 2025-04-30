/*
 * @(#)DbServerAddress.java	0.991	2019/02/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DbServerAddress.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.db;

/**
 * データベースの接続先を示すホスト名とポート番号を保持するオブジェクト。
 * <p>このオブジェクトは、不変オブジェクトとする。
 * 
 * @version 0.991
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class DbServerAddress
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** ホスト名または IP アドレス **/
	private final String	_host;
	/** ポート番号 **/
	private final int		_port;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static public DbServerAddress parse(String strHost, int defaultPort) {
		String useHost = "127.0.0.1";
		int    usePort = defaultPort;
		if (strHost != null) {
			strHost = strHost.trim();
			if (!strHost.isEmpty()) {
				useHost = strHost;
				if (strHost.startsWith("[")) {
					// IPv6
					int termIndex = strHost.indexOf(']');
					if (termIndex < 0) {
						throw new IllegalArgumentException("IPv6 address must be encosed with '[' and ']' according to RFC2732 : \"" + strHost + "\"");
					}
					String strAfterTerm = strHost.substring(termIndex+1).trim();
					if (strAfterTerm.startsWith(":")) {
						try {
							usePort = Integer.parseInt(strAfterTerm.substring(1));
						} catch (NumberFormatException ex) {
							throw new IllegalArgumentException("Invalid host:port format of server address : \"" + strHost + "\"");
						}
						useHost = strHost.substring(0, termIndex+1);
					}
				} else {
					// IPv4 or host name
					int delimIndex = strHost.indexOf(':');
					int lastDelimIndex = strHost.lastIndexOf(':');
					if (delimIndex == lastDelimIndex && delimIndex > 0) {
						try {
							usePort = Integer.parseInt(strHost.substring(delimIndex+1));
						} catch (NumberFormatException ex) {
							throw new IllegalArgumentException("Invalid host:port format of server address : \"" + strHost + "\"");
						}
						useHost = strHost.substring(0, delimIndex).trim();
					}
				}
			}
		}
		return new DbServerAddress(useHost, usePort);
	}
	
	/**
	 * 指定されたパラメータを保持する、新しいインスタンスを生成する。
	 * @param host	ホスト名または IP アドレス
	 * @param port	ポート番号
	 */
	public DbServerAddress(String host, int port) {
		if (host == null)
			throw new NullPointerException("'host' is null.");
		_host = host.toLowerCase();
		_port = port;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * ホスト名を返す。
	 * @return	ホスト名または IP アドレス
	 */
	public String getHost() {
		return _host;
	}

	/**
	 * ポート番号を返す。
	 * @return	ポート番号
	 */
	public int getPort() {
		return _port;
	}

	//------------------------------------------------------------
	// Implements Object interfaces
	//------------------------------------------------------------

	@Override
	public int hashCode() {
		int h = _host.hashCode();
		h = 31 * h + _port;
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;	// same instances
		}
		
		if (obj == null || !this.getClass().equals(obj.getClass())) {
			return false;
		}
		
		DbServerAddress that = (DbServerAddress)obj;
		if (_port != that._port) {
			return false;
		}
		if (!_host.equals(that._host)) {
			return false;
		}
		
		// equals
		return true;
	}

	@Override
	public String toString() {
		return (_host + ":" + String.valueOf(_port));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
