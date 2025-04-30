/*
 * @(#)NetUtilities.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.util;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Formatter;

/**
 * ネットワークに関するユーティリティ群。
 * 
 * @version 0.990
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Li Hou(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class NetUtilities
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String	_FMT_BYTE_HEX = "%02X";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private NetUtilities() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 区切り文字のない 16 進数(大文字)での MAC アドレスを表す文字列を取得する。
	 * @param bytes	MAC アドレスを表すバイト配列
	 * @return	MAC アドレスを表す文字列を返す。
	 * 			<em>bytes</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * 			<em>bytes</em> が空文字列の場合は空文字列を返す。
	 */
	static public String toHexNicAddr(byte[] bytes) {
		return toHexNicAddr('\0', bytes);
	}
	
	/**
	 * 指定された区切り文字で区切られた 16 進数(大文字)での MAC アドレスを表す文字列を取得する。
	 * @param delim	区切り文字とする文字列、0 の場合は区切り文字なし
	 * @param bytes	MAC アドレスを表すバイト配列
	 * @return	MAC アドレスを表す文字列を返す。
	 * 			<em>bytes</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * 			<em>bytes</em> が空文字列の場合は空文字列を返す。
	 */
	static public String toHexNicAddr(char delim, byte[] bytes) {
		if (bytes == null)	return null;
		if (bytes.length == 0)	return "";
		int delimlen = (delim=='\0' ? 0 : 1);
		StringBuilder sb = new StringBuilder(bytes.length * 2 + bytes.length * delimlen);
		@SuppressWarnings("resource")
		Formatter fmt = new Formatter(sb);
		if (delimlen > 0) {
			// 区切り文字あり
			fmt.format(_FMT_BYTE_HEX, bytes[0]);
			for (int i = 1; i < bytes.length; ++i) {
				sb.append(delim);
				fmt.format(_FMT_BYTE_HEX, bytes[i]);
			}
		}
		else {
			// 区切り文字なし
			for (byte b : bytes) {
				fmt.format(_FMT_BYTE_HEX, b);
			}
		}
		return sb.toString();
	}

	/**
	 * 指定された区切り文字で区切られた 16 進数(大文字)での MAC アドレスを表す文字列を取得する。
	 * @param delim	区切り文字とする文字列、<tt>null</tt> もしくは空文字列の場合は区切り文字なし
	 * @param bytes	MAC アドレスを表すバイト配列
	 * @return	MAC アドレスを表す文字列を返す。
	 * 			<em>bytes</em> が <tt>null</tt> の場合は <tt>null</tt>、
	 * 			<em>bytes</em> が空文字列の場合は空文字列を返す。
	 */
	static public String toHexNicAddr(String delim, byte[] bytes) {
		if (bytes == null)	return null;
		if (bytes.length == 0)	return "";
		int delimlen = (delim==null ? 0 : delim.length());
		StringBuilder sb = new StringBuilder(bytes.length * 2 + bytes.length * delimlen);
		@SuppressWarnings("resource")
		Formatter fmt = new Formatter(sb);
		if (delimlen > 0) {
			// 区切り文字あり
			fmt.format(_FMT_BYTE_HEX, bytes[0]);
			for (int i = 1; i < bytes.length; ++i) {
				sb.append(delim);
				fmt.format(_FMT_BYTE_HEX, bytes[i]);
			}
		}
		else {
			// 区切り文字なし
			for (byte b : bytes) {
				fmt.format(_FMT_BYTE_HEX, b);
			}
		}
		return sb.toString();
	}

	/**
	 * 指定されたネットワークインタフェースが、物理的ネットワークインタフェースかどうかを判定する。
	 * <p>このメソッドでの判定基準は次の通り。
	 * <ul>
	 * <li>{@link NetworkInterface#isLoopback()} が <tt>false</tt></li>
	 * <li>{@link NetworkInterface#isPointToPoint()} が <tt>false</tt></li>
	 * <li>{@link NetworkInterface#isVirtual()} が <tt>false</tt></li>
	 * <li>{@link NetworkInterface#getHardwareAddress()} が 6 バイトの配列で、すべてが 0 ではない</li>
	 * </ul>
	 * @param ni	判定対象のネットワークインタフェース
	 * @return	物理的ネットワークインタフェースとみなされた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isPhysicalNic(final NetworkInterface ni) {
		try {
			if (ni.isLoopback())
				return false;
		
			if (ni.isPointToPoint())
				return false;
			
			if (ni.isVirtual())
				return false;
		
			byte[] nicaddr = ni.getHardwareAddress();
			if (nicaddr != null && nicaddr.length == 6) {
				return (nicaddr[0]!=0 || nicaddr[1]!=0 || nicaddr[2]!=0 || nicaddr[3]!=0 || nicaddr[4]!=0 || nicaddr[5]!=0);
			} else {
				return false;
			}
		}
		catch (Throwable ignoreEx) {
			return false;
		}
	}

	/**
	 * 物理的ネットワークインタフェース数を返す。
	 * このメソッドでは、Java で認識可能な物理的ネットワークインタフェースを検索し、その総数を返す。
	 * @return	物理的ネットワークインタフェース数
	 */
	static public int getPhysicalNicCount() {
		int cnt = 0;
		try {
			Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
			for (; nics.hasMoreElements(); ) {
				NetworkInterface ni = nics.nextElement();
				if (isPhysicalNic(ni)) {
					++cnt;
				}
			}
		}
		catch (Throwable ignroeEx) {}
		return cnt;
	}

	/**
	 * 物理的ネットワークインタフェースの配列を返す。
	 * このメソッドでは、Java で認識可能な物理的ネットワークインタフェースを検索し、
	 * 該当するネットワークインタフェースの配列を返す。
	 * @return	物理的ネットワークインタフェースの配列、見つからなった場合は空の配列
	 */
	static public NetworkInterface[] getPhysicalNics() {
		try {
			Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
			ArrayList<NetworkInterface> pniclist = new ArrayList<NetworkInterface>();
			for (; nics.hasMoreElements(); ) {
				NetworkInterface ni = nics.nextElement();
				if (isPhysicalNic(ni)) {
					pniclist.add(ni);
				}
			}
			return pniclist.toArray(new NetworkInterface[pniclist.size()]);
		}
		catch (Throwable ignroeEx) {
			return new NetworkInterface[0];
		}
	}

	/**
	 * 指定されたインデックスに対応する物理的ネットワークカードの MAC アドレスを取得する。
	 * このメソッドでは、Java で認識可能な物理的ネットワークインタフェースを検索し、
	 * 指定されたインデックスに対応する物理的ネットワークカードの MAC アドレスを返す。
	 * @param index	0 以上、({@link #getPhysicalNicCount()}-1)以下の値
	 * @return	インデックスに対応する MAC アドレスを格納する 6 バイトの配列、見つからなかった場合もしくはインデックスが無効の場合は <tt>null</tt>
	 */
	static public byte[] getPhysicalNicHardwareAddr(int index) {
		if (index >= 0) {
			try {
				int nicnt = 0;
				Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
				for (; nics.hasMoreElements(); ) {
					NetworkInterface ni = nics.nextElement();
					if (isPhysicalNic(ni)) {
						if (index == nicnt) {
							return ni.getHardwareAddress();
						}
						++nicnt;
					}
				}
			}
			catch (Throwable ignroeEx) {}
		}
		return null;
	}

	/**
	 * 最初の物理的ネットワークカードの MAC アドレスを取得する。
	 * このメソッドでは、Java で認識可能な物理的ネットワークインタフェースを検索し、
	 * 最初に見つかった有効な MAC アドレスを格納する 6 バイトの配列を返す。
	 * 有効な MAC アドレスが取得できなかった場合は、<tt>null</tt> を返す。
	 * @return	有効な MAC アドレスを格納する 6 バイトの配列、見つからなかった場合は <tt>null</tt>
	 */
	static public byte[] getFirstPhysicalNicHardwareAddr() {
		return getPhysicalNicHardwareAddr(0);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
