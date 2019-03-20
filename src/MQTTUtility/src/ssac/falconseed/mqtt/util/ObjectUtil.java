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
