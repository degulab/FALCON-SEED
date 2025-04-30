/*
 * @(#)ObjectUtil.java	1.0.0	2013/02/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mqtt.util;

/**
 * オブジェクトに関するユーティリティ
 * 
 * @version 1.0.0	2013/02/28
 */
public class ObjectUtil
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
	
	private ObjectUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたオブジェクトの同値性を判定する。
	 * <p>
	 * このメソッドが <tt>true</tt> を返す条件は、次の通り。
	 * <ul>
	 * <ui>指定された引数のどちらも <tt>null</tt> の場合
	 * <ui>指定されたインスタンスが同じ場合
	 * <ui><code>obj1.equals(obj2)</code> もしくは <code>obj2.equals(obj1)</code> の
	 * どちらかが <tt>true</tt> を返す場合
	 * </ul>
	 * @param obj1	判定するオブジェクトの一方
	 * @param obj2 判定するオブジェクトのもう一方
	 * @return 同値であれば <tt>true</tt>
	 */
	static public final boolean isEqual(Object obj1, Object obj2) {
		if (obj1 == obj2)
			return true;	// same instance
		
		if (obj1 != null)
			return obj1.equals(obj2);
		else
			return obj2.equals(obj1);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
