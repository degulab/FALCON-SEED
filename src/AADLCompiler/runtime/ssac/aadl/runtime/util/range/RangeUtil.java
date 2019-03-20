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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)RangeUtil.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * 数値範囲のためのユーティリティ。
 * 
 * @version 1.70	2011/06/29
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Yuji Onuki (Statistics Bureau)
 * @author Shungo Sakaki (Tokyo University of Technology)
 * @author Akira Sasaki (HOSEI UNIVERSITY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 * 
 * @since 1.70
 */
public class RangeUtil
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
	
	private RangeUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたオブジェクトを <code>BigDecimal</code> に変換します。
	 * 変換可能なオブジェクトは、<code>Number</code> クラスの派生オブジェクトのみです。
	 * <code>Number</code> クラスの派生オブジェクトのうち、<code>BigDecimal</code>、<code>BigInteger</code>、
	 * <code>Double</code>、<code>Float</code> オブジェクトは、最適な方法で変換されます。
	 * これら以外の場合は <code>Number</code> クラスの <code>longValue</code> メソッドによって変換されます。
	 * @param value	変換するオブジェクト
	 * @return	変換結果の <code>BigDecimal</code> オブジェクトを返す。
	 * 			変換不可能な場合は <tt>null</tt> を返す。
	 */
	static public BigDecimal toBigDecimal(Object value) {
		BigDecimal dval = null;
		
		if (value instanceof BigDecimal) {
			dval = (BigDecimal)value;
		}
		else if (value instanceof BigInteger) {
			dval = new BigDecimal((BigInteger)value);
		}
		else if (value instanceof Double) {
			try {
				dval = BigDecimal.valueOf(((Double)value).doubleValue());
			} catch (NumberFormatException ex) {
				dval = null;
			}
		}
		else if (value instanceof Float) {
			try {
				dval = BigDecimal.valueOf(((Float)value).doubleValue());
			} catch (NumberFormatException ex) {
				dval = null;
			}
		}
		else if (value instanceof Number) {
			dval = BigDecimal.valueOf(((Number)value).longValue());
		}
		
		return dval;
	}

	/**
	 * 指定された数値が、情報を失わずに <code>short</code> へ変換可能かを判定します。
	 * <em>value</em> に 0 以外の小数部がある、または <code>short</code> の範囲外である場合、
	 * このメソッドは <tt>false</tt> を返します。<em>value</em> が <tt>null</tt> の場合も <tt>false</tt> を返します。
	 * 
	 * @param value	判定する値
	 * @return 情報を失わずに <code>short</code> に変換可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean exactlyShort(BigDecimal value) {
		if (value != null) {
			try {
				value.shortValueExact();
				return true;
			} catch (ArithmeticException ex) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * 指定された数値が、情報を失わずに <code>int</code> へ変換可能かを判定します。
	 * <em>value</em> に 0 以外の小数部がある、または <code>int</code> の範囲外である場合、
	 * このメソッドは <tt>false</tt> を返します。<em>value</em> が <tt>null</tt> の場合も <tt>false</tt> を返します。
	 * 
	 * @param value	判定する値
	 * @return 情報を失わずに <code>int</code> に変換可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean exactlyInteger(BigDecimal value) {
		if (value != null) {
			try {
				value.intValueExact();
				return true;
			} catch (ArithmeticException ex) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * 指定された数値が、情報を失わずに <code>long</code> へ変換可能かを判定します。
	 * <em>value</em> に 0 以外の小数部がある、または <code>long</code> の範囲外である場合、
	 * このメソッドは <tt>false</tt> を返します。<em>value</em> が <tt>null</tt> の場合も <tt>false</tt> を返します。
	 * 
	 * @param value	判定する値
	 * @return 情報を失わずに <code>long</code> に変換可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean exactlyLong(BigDecimal value) {
		if (value != null) {
			try {
				value.longValueExact();
				return true;
			} catch (ArithmeticException ex) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * 指定された数値が、情報を失わずに <code>BigInteger</code> へ変換可能かを判定します。
	 * <em>value</em> に 0 以外の小数部がある場合、
	 * このメソッドは <tt>false</tt> を返します。<em>value</em> が <tt>null</tt> の場合も <tt>false</tt> を返します。
	 * 
	 * @param value	判定する値
	 * @return 情報を失わずに <code>BigInteger</code> に変換可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean exactlyBigInteger(BigDecimal value) {
		if (value != null) {
			try {
				value.toBigIntegerExact();
				return true;
			} catch (ArithmeticException ex) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 指定されたすべての数値が、情報を失わずに <code>short</code> へ変換可能かを判定します。
	 * 数値のどれかに 0 以外の小数部がある、または <code>short</code> の範囲外である場合、
	 * このメソッドは <tt>false</tt> を返します。数値のどれかが <tt>null</tt> の場合も <tt>false</tt> を返します。
	 * @param fromValue	判定する範囲先頭の値
	 * @param toValue	判定する範囲終端の値
	 * @param stepValue	判定する数値間隔の値
	 * @return	すべての値が情報を失わずに <code>short</code> に変換可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean exactlyShortValues(BigDecimal fromValue, BigDecimal toValue, BigDecimal stepValue) {
		if (fromValue != null && toValue != null && stepValue != null) {
			try {
				fromValue.shortValueExact();
				toValue  .shortValueExact();
				stepValue.shortValueExact();
				return true;
			} catch (ArithmeticException ex) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * 指定されたすべての数値が、情報を失わずに <code>int</code> へ変換可能かを判定します。
	 * 数値のどれかに 0 以外の小数部がある、または <code>int</code> の範囲外である場合、
	 * このメソッドは <tt>false</tt> を返します。数値のどれかが <tt>null</tt> の場合も <tt>false</tt> を返します。
	 * @param fromValue	判定する範囲先頭の値
	 * @param toValue	判定する範囲終端の値
	 * @param stepValue	判定する数値間隔の値
	 * @return	すべての値が情報を失わずに <code>int</code> に変換可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean exactlyIntegerValues(BigDecimal fromValue, BigDecimal toValue, BigDecimal stepValue) {
		if (fromValue != null && toValue != null && stepValue != null) {
			try {
				fromValue.intValueExact();
				toValue  .intValueExact();
				stepValue.intValueExact();
				return true;
			} catch (ArithmeticException ex) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * 指定されたすべての数値が、情報を失わずに <code>long</code> へ変換可能かを判定します。
	 * 数値のどれかに 0 以外の小数部がある、または <code>long</code> の範囲外である場合、
	 * このメソッドは <tt>false</tt> を返します。数値のどれかが <tt>null</tt> の場合も <tt>false</tt> を返します。
	 * @param fromValue	判定する範囲先頭の値
	 * @param toValue	判定する範囲終端の値
	 * @param stepValue	判定する数値間隔の値
	 * @return	すべての値が情報を失わずに <code>long</code> に変換可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean exactlyLongValues(BigDecimal fromValue, BigDecimal toValue, BigDecimal stepValue) {
		if (fromValue != null && toValue != null && stepValue != null) {
			try {
				fromValue.longValueExact();
				toValue  .longValueExact();
				stepValue.longValueExact();
				return true;
			} catch (ArithmeticException ex) {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * 指定されたすべての数値が、情報を失わずに <code>BigInteger</code> へ変換可能かを判定します。
	 * 数値のどれかに 0 以外の小数部がある場合、
	 * このメソッドは <tt>false</tt> を返します。数値のどれかが <tt>null</tt> の場合も <tt>false</tt> を返します。
	 * @param fromValue	判定する範囲先頭の値
	 * @param toValue	判定する範囲終端の値
	 * @param stepValue	判定する数値間隔の値
	 * @return	すべての値が情報を失わずに <code>BigInteger</code> に変換可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean exactlyBigIntegerValues(BigDecimal fromValue, BigDecimal toValue, BigDecimal stepValue) {
		if (fromValue != null && toValue != null && stepValue != null) {
			try {
				fromValue.toBigIntegerExact();
				toValue  .toBigIntegerExact();
				stepValue.toBigIntegerExact();
				return true;
			} catch (ArithmeticException ex) {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * <code>short</code> プリミティブでイテレーションが行える
	 * 数値範囲定義かどうかを判定する。
	 * イテレーションが行える数値範囲の判定では、
	 * <code>(from + step * n) <= to)</code> もしくは <code>(from + step * n) >= to)</code> の
	 * 演算が <code>short</code> プリミティブのみで行える数値かどうかで判定する。
	 * @param from	数値範囲の先頭の値
	 * @param to	数値範囲の終端の値。この値も数値範囲の要素に含まれる。
	 * @param step	数値範囲の間隔
	 * @return	対象のプリミティブで演算可能な数値範囲なら <tt>true</tt>、そうでない場合は <tt>false</tt>。
	 * 			<em>step</em> が 0 の場合は <tt>false</tt>。
	 */
	static public boolean exactlyIterableShortRange(short from, short to, short step) {
		if (step == 0) {
			// empty
			return false;
		}

		int rf = from;
		int rs = step;
		int limitover = ((int)to - rf) / rs * rs + rs + rf;
		if (Short.MIN_VALUE <= limitover && limitover <= Short.MAX_VALUE) {
			// iterable by short
			return true;
		} else {
			// overflow short
			return false;
		}
	}
	
	/**
	 * <code>int</code> プリミティブでイテレーションが行える
	 * 数値範囲定義かどうかを判定する。
	 * イテレーションが行える数値範囲の判定では、
	 * <code>(from + step * n) <= to)</code> もしくは <code>(from + step * n) >= to)</code> の
	 * 演算が <code>int</code> プリミティブのみで行える数値かどうかで判定する。
	 * @param from	数値範囲の先頭の値
	 * @param to	数値範囲の終端の値。この値も数値範囲の要素に含まれる。
	 * @param step	数値範囲の間隔
	 * @return	対象のプリミティブで演算可能な数値範囲なら <tt>true</tt>、そうでない場合は <tt>false</tt>。
	 * 			<em>step</em> が 0 の場合は <tt>false</tt>。
	 */
	static public boolean exactlyIterableIntegerRange(int from, int to, int step) {
		if (step == 0) {
			// empty
			return false;
		}

		long rf = from;
		long rs = step;
		long limitover = ((long)to - rf) / rs * rs + rs + rf;
		if (Integer.MIN_VALUE <= limitover && limitover <= Integer.MAX_VALUE) {
			// iterable by int
			return true;
		} else {
			// overflow int
			return false;
		}
	}
	
	/**
	 * <code>long</code> プリミティブでイテレーションが行える
	 * 数値範囲定義かどうかを判定する。
	 * イテレーションが行える数値範囲の判定では、
	 * <code>(from + step * n) <= to)</code> もしくは <code>(from + step * n) >= to)</code> の
	 * 演算が <code>long</code> プリミティブのみで行える数値かどうかで判定する。
	 * @param from	数値範囲の先頭の値
	 * @param to	数値範囲の終端の値。この値も数値範囲の要素に含まれる。
	 * @param step	数値範囲の間隔
	 * @return	対象のプリミティブで演算可能な数値範囲なら <tt>true</tt>、そうでない場合は <tt>false</tt>。
	 * 			<em>step</em> が 0 の場合は <tt>false</tt>。
	 */
	static public boolean exactlyIterableLongRange(long from, long to, long step) {
		if (step == 0L) {
			// empty
			return false;
		}
		
		return exactlyIterableRangeValue(BigDecimal.valueOf(from), BigDecimal.valueOf(to), BigDecimal.valueOf(step),
										BigDecimal.valueOf(Long.MIN_VALUE), BigDecimal.valueOf(Long.MAX_VALUE));
	}
	
	/**
	 * <code>short</code> プリミティブでイテレーションが行える
	 * 数値範囲定義かどうかを判定する。
	 * イテレーションが行える数値範囲の判定では、
	 * <code>(from + step * n) <= to)</code> もしくは <code>(from + step * n) >= to)</code> の
	 * 演算が <code>short</code> プリミティブのみで行える数値かどうかで判定する。
	 * @param from	数値範囲の先頭の値
	 * @param to	数値範囲の終端の値。この値も数値範囲の要素に含まれる。
	 * @param step	数値範囲の間隔
	 * @return	対象のプリミティブで演算可能な数値範囲なら <tt>true</tt>、そうでない場合は <tt>false</tt>。
	 * 			<em>step</em> が 0 の場合は <tt>false</tt>。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean exactlyIterableShortRange(BigDecimal from, BigDecimal to, BigDecimal step) {
		short shortFrom, shortTo, shortStep;
		try {
			shortFrom = from.shortValueExact();
			shortTo   = to.shortValueExact();
			shortStep = step.shortValueExact();
		} catch (ArithmeticException ex) {
			return false;
		}
		
		return exactlyIterableShortRange(shortFrom, shortTo, shortStep);
	}
	
	/**
	 * <code>int</code> プリミティブでイテレーションが行える
	 * 数値範囲定義かどうかを判定する。
	 * イテレーションが行える数値範囲の判定では、
	 * <code>(from + step * n) <= to)</code> もしくは <code>(from + step * n) >= to)</code> の
	 * 演算が <code>int</code> プリミティブのみで行える数値かどうかで判定する。
	 * @param from	数値範囲の先頭の値
	 * @param to	数値範囲の終端の値。この値も数値範囲の要素に含まれる。
	 * @param step	数値範囲の間隔
	 * @return	対象のプリミティブで演算可能な数値範囲なら <tt>true</tt>、そうでない場合は <tt>false</tt>。
	 * 			<em>step</em> が 0 の場合は <tt>false</tt>。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean exactlyIterableIntegerRange(BigDecimal from, BigDecimal to, BigDecimal step) {
		int intFrom, intTo, intStep;
		try {
			intFrom = from.intValueExact();
			intTo   = to.intValueExact();
			intStep = step.intValueExact();
		} catch (ArithmeticException ex) {
			return false;
		}
		
		return exactlyIterableIntegerRange(intFrom, intTo, intStep);
	}
	
	/**
	 * <code>long</code> プリミティブでイテレーションが行える
	 * 数値範囲定義かどうかを判定する。
	 * イテレーションが行える数値範囲の判定では、
	 * <code>(from + step * n) <= to)</code> もしくは <code>(from + step * n) >= to)</code> の
	 * 演算が <code>long</code> プリミティブのみで行える数値かどうかで判定する。
	 * @param from	数値範囲の先頭の値
	 * @param to	数値範囲の終端の値。この値も数値範囲の要素に含まれる。
	 * @param step	数値範囲の間隔
	 * @return	対象のプリミティブで演算可能な数値範囲なら <tt>true</tt>、そうでない場合は <tt>false</tt>。
	 * 			<em>step</em> が 0 の場合は <tt>false</tt>。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean exactlyIterableLongRange(BigDecimal from, BigDecimal to, BigDecimal step) {
		try {
			from.longValueExact();
			to.longValueExact();
			step.longValueExact();
		} catch (ArithmeticException ex) {
			return false;
		}
		
		if (step.signum() == 0) {
			// empty
			return false;
		}
		
		return exactlyIterableRangeValue(from, to, step,
					BigDecimal.valueOf(Long.MIN_VALUE), BigDecimal.valueOf(Long.MAX_VALUE));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected boolean exactlyIterableRangeValue(BigDecimal from, BigDecimal to, BigDecimal step, BigDecimal min, BigDecimal max) {
		BigDecimal limitover = to.subtract(from).divide(step, 0, RoundingMode.DOWN).multiply(step).add(step).add(from);
		if (min.compareTo(limitover) <= 0 && max.compareTo(limitover) >= 0) {
			// iterable by (min <= limitover <= max)
			return true;
		} else {
			// overflow min or max
			return false;
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
