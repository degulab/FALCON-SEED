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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)Objects.java	1.50	2010/09/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * AADL のオブジェクトに関するユーティリティ
 * 
 * @version 1.50	2010/09/29
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Yuji Onuki (Statistics Bureau)
 * @author Shungo Sakaki (Tokyo University of Technology)
 * @author Akira Sasaki (HOSEI UNIVERSITY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 * 
 * @since 1.50
 */
public class Objects
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

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * オブジェクトのハッシュコードを返します。
	 * <br>オブジェクトが <code>BigDecimal</code> インスタンスの場合、
	 * 値が 0 と等しい場合には 0、それ以外は余分な 0 を除去した後の
	 * <code>BigDecimal</code> インスタンスのハッシュコードを返します。
	 * <br>オブジェクトが <code>BigDecimal</code> 以外の場合は、
	 * <code>hashCode</code> メソッドの戻り値を返します。
	 * @param value	ハッシュコード取得対象のオブジェクト
	 * @return	ハッシュコード値 
	 */
	static public int valueHashCode(Object value) {
		if (value == null) {
			return 0;
		}
		else if (value instanceof BigDecimal) {
			//--- 値が BigDecimal の場合、BigDecimal#stripTrailingZeros() によって下位の桁の
			//--- 0(余分な0)を消去した後の BigDecimal#hashCode() の値とする。値が 0 の場合は
			//--- 強制的に 0 とする。
			BigDecimal dv = (BigDecimal)value;
			if (0 != BigDecimal.ZERO.compareTo(dv)) {
				return dv.stripTrailingZeros().hashCode();
			} else {
				return 0;
			}
		}
		else {
			return value.hashCode();
		}
	}

	/**
	 * 2 つのオブジェクトが等しいかを検証します。
	 * このメソッドは、2 つのオブジェクトがどちらも <tt>null</tt> であるか、
	 * {@link Object#equals(Object)} メソッドが <tt>true</tt> を返した場合に等しいとみなします。
	 * 
	 * @param obj1	判定する値の一方
	 * @param obj2	判定する値のもう一方
	 * @return	2 つのオブジェクトが等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean equals(Object obj1, Object obj2) {
		if (obj1 == null) {
			return (obj2 == null);
		}
		else {
			return obj1.equals(obj2);
		}
	}

	/**
	 * 2つのオブジェクトが値として等しいかを検証します。<br>
	 * このメソッドは、2 つのオブジェクトがどちらも <tt>null</tt> であるか、
	 * {@link Object#equals(Object)} メソッドが <tt>true</tt> を返した場合に等しいとみなします。<br>
	 * 2 つのオブジェクトが <code>BigDecimal</code> インスタンスの場合は、
	 * 比較結果が 0 の場合に等しいとみなします。
	 * 
	 * @param obj1	判定する値の一方
	 * @param obj2	判定する値のもう一方
	 * @return	2 つのオブジェクトが等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean valueEquals(Object obj1, Object obj2) {
		if (obj1 == null) {
			return (obj2 == null);
		}
		else if ((obj1 instanceof BigDecimal) && (obj2 instanceof BigDecimal)) {
			return (((BigDecimal)obj1).compareTo((BigDecimal)obj2) == 0);
		}
		else {
			return obj1.equals(obj2);
		}
	}

	/**
	 * 指定された値と等しい要素が指定されたコレクションに存在する場合に <tt>true</tt> を返します。
	 * このメソッドは、指定された値と指定されたコレクション要素の比較において、
	 * {@link #valueEquals(Object, Object)} メソッドが <tt>true</tt> を返した場合に等しいとみなします。<br>
	 * @param value		判定する値
	 * @param c			判定対象のコレクション
	 * @return	指定された値と等しい要素が指定されたコレクションに存在する場合は <tt>true</tt>、
	 * 			そうでない場合は <tt>false</tt>。
	 * 			指定されたコレクションの要素が空の場合は <tt>false</tt>。
	 * @throws NullPointerException	コレクションが <tt>null</tt> の場合
	 */
	static public boolean containsValue(Object value, Collection<?> c) {
		if (value == null) {
			for (Object val : c) {
				if (val == null) {
					return true;
				}
			}
		} else {
			for (Object val : c) {
				if (valueEquals(val, value)) {
					return true;
				}
			}
		}
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 2つのオブジェクトが値として等しいかを検証します。<br>
	 * このメソッドは、{@link Object#equals(Object)} メソッドが <tt>true</tt> を
	 * 返した場合に等しいとみなします。<br>
	 * 2 つのオブジェクトが <code>BigDecimal</code> インスタンスの場合は、
	 * 比較結果が 0 の場合に等しいとみなします。
	 * <br>
	 * なお、このメソッドの引数に <tt>null</tt> は指定できません。
	 * 
	 * @param obj1	判定する値の一方
	 * @param obj2	判定する値のもう一方
	 * @return	2 つのオブジェクトが等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>obj1</em> が <tt>null</tt> の場合
	 */
	static protected boolean valueEqualsWithoutNull(Object obj1, Object obj2) {
		if ((obj1 instanceof BigDecimal) && (obj2 instanceof BigDecimal)) {
			return (((BigDecimal)obj1).compareTo((BigDecimal)obj2) == 0);
		}
		else {
			return obj1.equals(obj2);
		}
	}
}
