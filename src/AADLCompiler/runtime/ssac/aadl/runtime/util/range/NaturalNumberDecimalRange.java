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
 * @(#)NaturalNumberDecimalRange.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range;

import java.math.BigDecimal;
import java.util.Iterator;

import ssac.aadl.runtime.util.range.internal.NaturalNumberRangeImpl;

/**
 * 0 以上の自然数によって指定される不連続の数値範囲を持つ数値範囲オブジェクトです。
 * この数値範囲は規定の書式によって記述された文字列から生成されます。ここで指定可能な
 * 文字列は、連続する数値範囲をハイフンで、連続ではない数値範囲をカンマで区切って記述します。
 * 例えば、1、3、5～10、12～15、という数値範囲の場合、&quot;1,3,5-10,12-15&quot; というように記述します。
 * ハイフンの前を省略した場合は 1 から指定された数値までの範囲とみなします。また、ハイフンの後を
 * 省略した場合は指定された数値から最大値までの範囲と見なします。
 * <p>この数値範囲オブジェクトでは、数値範囲終端の値を含みます。また、数値間隔は、連続、不連続に
 * 関わらず、常に 1 を返します。
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
public class NaturalNumberDecimalRange implements DecimalRange
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 数値範囲の実装 **/
	protected NaturalNumberRangeImpl	_range;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定された数値範囲定義文字列から、デフォルトの上限(最大値)までの数値範囲を生成します。
	 * 数値範囲指定の文字列に、ハイフン、カンマ、数字、空白以外の文字が含まれている
	 * 場合や、数字を空白で区切って記述した場合、ハイフンの前後どちらかに数値が指定
	 * されていない場合は例外をスローします。<br>
	 * このコンストラクタで生成された数値範囲の上限(最大値)は、{@link Long#MAX_VALUE} となります。
	 * 
	 * @param rangeString	数値範囲定義文字列
	 * @throws IllegalArgumentException	数値範囲定義文字列の書式が不正な場合
	 */
	public NaturalNumberDecimalRange(String rangeString) {
		this(rangeString, null);
	}

	/**
	 * 指定された数値範囲定義文字列から、指定された上限(最大値)までの数値範囲を生成します。
	 * 数値範囲指定の文字列に、ハイフン、カンマ、数字、空白以外の文字が含まれている
	 * 場合や、数字を空白で区切って記述した場合、ハイフンの前後どちらかに数値が指定
	 * されていない場合は例外をスローします。<br>
	 * <em>maxValue</em> に <tt>null</tt> を指定した場合の上限(最大値)は、{@link Long#MAX_VALUE} となります。
	 * 
	 * @param rangeString	数値範囲定義文字列
	 * @param maxValue		範囲の上限とする値(0 以上の整数のみ)
	 * @throws IllegalArgumentException	数値範囲定義文字列の書式が不正な場合、
	 * 										<em>maxValue</em> が負の値、もしくは整数ではない場合
	 */
	public NaturalNumberDecimalRange(String rangeString, BigDecimal maxValue) {
		this._range = new NaturalNumberRangeImpl(rangeString, maxValue);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 定義されている数値範囲が無効である場合に <tt>true</tt> を返します。
	 * @return	数値範囲が無効であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isEmpty() {
		return _range.isEmpty();
	}
	
	/**
	 * 数値範囲内の値に、範囲終端の値を含むよう指定されている場合に <tt>true</tt> を返します。
	 * 範囲の指定によっては、このメソッドが <tt>true</tt> を返す場合でも、範囲終端が含まれない場合があります。
	 * @return	常に <tt>true</tt>
	 */
	public boolean isIncludeRangeLast() {
		return true;
	}

	/**
	 * 数値範囲が増加方向か減少方向かを返します。
	 * @return	増加方向の数値範囲の場合は <tt>true</tt>、減少方向の数値範囲の場合は <tt>false</tt>
	 */
	public boolean isIncremental() {
		return _range.isIncremental();
	}

	/**
	 * 指定された値が、数値範囲内の要素と等しいかどうかを判定します。
	 * ただし、数値範囲が無効である場合、このメソッドは常に <tt>false</tt> を返します。
	 * @param value	判定する値
	 * @return	数値範囲内の要素と等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * 			数値範囲が無効の場合は <tt>false</tt> を返す。
	 */
	public boolean containsValue(Object value) {
		// 範囲が無効なら、常に false
		if (isEmpty()) {
			return false;
		}

		// 数値を取得
		BigDecimal dval = RangeUtil.toBigDecimal(value);
		
		// 判定
		return _range.containsValue(dval);
	}

	/**
	 * 範囲先頭として設定された値を返します。
	 * @return	範囲先頭の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public BigDecimal rangeFirst() {
		return _range.getDecimalFromValue();
	}

	/**
	 * 範囲終端として設定された値を返します。
	 * @return	範囲終端の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public BigDecimal rangeLast() {
		return _range.getDecimalToValue();
	}

	/**
	 * このオブジェクトの数値間隔は基本的に 1 であるため、常に 1 を返す。
	 * @return	常に 1
	 */
	public BigDecimal rangeStep() {
		return BigDecimal.ONE;
	}

	/**
	 * 数値範囲の要素の中で最小の値を返します。
	 * ただし、数値範囲が無効である場合は <tt>null</tt> を返します。
	 * @return	数値範囲の要素の最小値を返す。
	 * 			数値範囲が無効の場合は <tt>null</tt> を返す。
	 */
	public BigDecimal rangeMin() {
		if (_range.isEmpty()) {
			return null;
		} else {
			return _range.getDecimalFromValue();
		}
	}

	/**
	 * 数値範囲の要素の中で最大の値を返します。
	 * ただし、数値範囲が無効である場合は <tt>null</tt> を返します。
	 * @return	数値範囲の要素の最大値を返す。
	 * 			数値範囲が無効の場合は <tt>null</tt> を返す。
	 */
	public BigDecimal rangeMax() {
		if (_range.isEmpty()) {
			return null;
		} else {
			return _range.getDecimalToValue();
		}
	}

	/**
	 * この数値範囲の要素を、範囲先頭から終端まで繰り返し処理する反復子を返します。
	 * @return	数値範囲の要素を繰り返し処理する反復子
	 */
	public Iterator<BigDecimal> iterator() {
		return _range.getDecimalRangeIterator();
	}

	/**
	 * 数値範囲のハッシュコード値を返します。
	 * <code>Object.hashCode</code> の一般規約によって要求されるように、任意の 2 つの数値範囲 <code>r1</code> と <code>r2</code> で、
	 * <code>r1.equals(r2)</code> であれば、<code>r1.hashCode()==r2.hashCode()</code> となることが保証されます。
	 * @return	ハッシュコード値
	 */
	public int hashCode() {
		return _range.hashCode();
	}

	/**
	 * 指定されたオブジェクトが、この数値範囲と等しいかどうかを比較します。
	 * 指定されたオブジェクトも数値範囲であり、数値範囲の定義が同じ場合に <tt>true</tt> を返します。
	 * 
	 * @param obj	この数値範囲と等しいかどうかが比較される <code>Object</code> 
	 * @return	指定されたオブジェクトが、この数値範囲と等しい場合は <tt>true</tt>
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof NaturalNumberDecimalRange) {
			if (((NaturalNumberDecimalRange)obj)._range.equals(_range)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * この数値範囲オブジェクトの文字列表現を返します。
	 * この文字列表現は、不連続な数値範囲をカンマで区切り、連続した数値範囲をハイフンで
	 * 区切った文字列を &quot;[&quot; と &quot;]&quot; で囲んだ文字列となります。
	 * @return 数値範囲を文字列に変換した結果を返す。数値範囲が無効の場合は &quot;[]&quot; を返す。
	 */
	@Override
	public String toString() {
		return "[" + _range.toString() + "]";
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
