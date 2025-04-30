/*
 * @(#)String.java	2.2.0	2021/08/27 : for Java11 
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.util;

/**
 * Java システムユーティリティ。
 * @version 2.2.0
 * @since 2.2.0 
 */
public class SystemUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private SystemUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * Java のメジャーバージョン番号を数値として取得する。
	 * &quot;1.8.x&quot; の場合は 8、&quot;11.0.x&quot; の場合は 11 を返す。
	 * @return	Java メジャーバージョン番号を示す数値、取得できなかった場合は 0
	 * @since 2.2.0
	 */
	static public int getJavaMajorVersionNumber()
	{
		String str = System.getProperty("java.version");
		if (str != null && !str.isEmpty()) {
			int from = (str.startsWith("1.") ? 2 : 0);
			int period = str.indexOf('.', from);
			String strNum = str.substring(from, period);
			try {
				return Integer.parseInt(strNum);
			}
			catch (Throwable ex) {
				// unknown version string
				return 0;
			}
		}
		// unknown version string
		return 0;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
