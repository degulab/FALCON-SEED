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
 * @(#)SimpleDecimalRange.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;

import ssac.aadl.runtime.util.range.internal.BigDecimalRangeImpl;
import ssac.aadl.runtime.util.range.internal.IntegerRangeImpl;
import ssac.aadl.runtime.util.range.internal.LongRangeImpl;
import ssac.aadl.runtime.util.range.internal.NumberRangeImpl;
import ssac.aadl.runtime.util.range.internal.ShortRangeImpl;

/**
 * 実数(<code>BigDecimal</code>)の数値範囲を持つ数値範囲オブジェクトクラスの標準実装です。
 * このオブジェクトは範囲の先頭と終端によって定義され、
 * 定義された間隔によって構成された値のコレクションとして利用できます。
 * <p>数値範囲の定義は、範囲先頭、範囲終端、数値間隔、範囲終端を含むか否かによって
 * 構成されます。数値間隔が正の場合は増加方向の数値範囲、数値間隔が負の場合は減少方向の
 * 数値範囲となります。範囲先頭の値が数値範囲の最初の要素であり、範囲先頭に数値間隔を
 * 繰り返し加算し、範囲終端を超えない値が数値範囲の要素となります。範囲終端を含まない
 * 数値範囲では、数値範囲の要素に範囲終端の値は含まれません。範囲終端を含む場合、
 * 範囲先頭に数値間隔を繰り返し加算した値が範囲終端と等しい場合のみ、範囲終端の値が
 * 要素に含まれます。<br>
 * 次の場合、数値範囲は無効となり、{@link #isEmpty()} メソッドは <tt>true</tt> を返します。
 * <ul>
 * <li>数値間隔が 0 の場合
 * <li>終端を含まない増加方向の数値範囲で (範囲先頭 &gt;= 範囲終端) の場合
 * <li>終端を含む増加方向の数値範囲で (範囲先頭 &gt; 範囲終端) の場合
 * <li>終端を含まない減少方向の数値範囲で (範囲先頭 &lt;= 範囲終端) の場合
 * <li>終端を含む減少方向の数値範囲で (範囲先頭 &lt; 範囲終端) の場合
 * </ul>
 * <p>数値範囲は反復子によって、数値範囲の要素を取得できます。
 * このインタフェースの実装では基本的に不変オブジェクトを想定しています。
 * そのため、このインタフェースが返す <code>Iterator</code> オブジェクトの
 * <code>{@link java.util.Iterator#remove()}</code> メソッドは通常、
 * <code>UnsupportedOperationException</code> をスローします。
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
public class SimpleDecimalRange implements DecimalRange
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 数値範囲の実装 **/
	protected final NumberRangeImpl	_impl;

	/** 範囲終端を含むことを示すフラグ **/
	protected final boolean	_includeLast;
	/** ユーザー定義の範囲終端 **/
	protected final BigDecimal	_last;
	/** ユーザー定義の数値範囲 **/
	protected final BigDecimal	_step;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 範囲先頭=0、範囲終端=<em>last</em> の、範囲終端を含まない数値範囲オブジェクトを生成します。
	 * (0 &lt;= <em>last</em>) の場合は、数値間隔=1 が設定されます。
	 * (0 &gt <em>last</em>) の場合は、数値間隔=(-1) が設定されます。
	 * 次の場合、オブジェクトは生成されますが、数値範囲は無効となり、{@link #isEmpty()} メソッドは <tt>true</tt> を返します。
	 * <ul>
	 * <li>終端を含まない数値範囲で (範囲終端 == 0) の場合
	 * </ul>
	 * 
	 * @param last			範囲終端の値
	 * @throws NullPointerException	<em>last</em> が <tt>null</tt> の場合
	 */
	public SimpleDecimalRange(BigDecimal last) {
		this(BigDecimal.ZERO, last, false);
	}
	
	/**
	 * 範囲先頭=0、範囲終端=<em>last</em> の、数値範囲オブジェクトを生成します。
	 * (0 &lt;= <em>last</em>) の場合は、数値間隔=1 が設定されます。
	 * (0 &gt; <em>last</em>) の場合は、数値間隔=(-1) が設定されます。
	 * <em>includeLast</em> に <tt>true</tt> が指定された場合は、範囲終端を含む数値範囲オブジェクトとなります。
	 * <em>includeLast</em> に <tt>false</tt> が指定された場合は、範囲終端を含まない数値範囲オブジェクトとなります。
	 * 次の場合、オブジェクトは生成されますが、数値範囲は無効となり、{@link #isEmpty()} メソッドは <tt>true</tt> を返します。
	 * <ul>
	 * <li>終端を含まない数値範囲で (範囲終端 == 0) の場合
	 * </ul>
	 * 
	 * @param last			範囲終端の値
	 * @param includeLast	範囲終端を含める場合は <tt>true</tt>、範囲終端を含めない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>last</em> が <tt>null</tt> の場合
	 */
	public SimpleDecimalRange(BigDecimal last, boolean includeLast) {
		this(BigDecimal.ZERO, last, includeLast);
	}
	
	/**
	 * 範囲先頭=<em>first</em>、範囲終端=<em>last</em> の、範囲終端を含まない数値範囲オブジェクトを生成します。
	 * (<em>first</em> &lt;= <em>last</em>) の場合、数値間隔=1 が設定されます。
	 * (<em>first</em> &gt; <em>last</em>) の場合、数値間隔=(-1) が設定されます。
	 * 次の場合、オブジェクトは生成されますが、数値範囲は無効となり、{@link #isEmpty()} メソッドは <tt>true</tt> を返します。
	 * <ul>
	 * <li>終端を含まない増加方向の数値範囲で (範囲先頭 &gt;= 範囲終端) の場合
	 * <li>終端を含む増加方向の数値範囲で (範囲先頭 &gt; 範囲終端) の場合
	 * <li>終端を含まない減少方向の数値範囲で (範囲先頭 &lt;= 範囲終端) の場合
	 * <li>終端を含む減少方向の数値範囲で (範囲先頭 &lt; 範囲終端) の場合
	 * </ul>
	 * 
	 * @param first			範囲先頭の値
	 * @param last			範囲終端の値
	 * @throws NullPointerException	<em>first</em>、<em>last</em> のどれかが <tt>null</tt> の場合
	 */
	public SimpleDecimalRange(BigDecimal first, BigDecimal last) {
		this(first, last, false);
	}
	
	/**
	 * 範囲先頭=<em>first</em>、範囲終端=<em>last</em> の、数値範囲オブジェクトを生成します。
	 * (<em>first</em> &lt;= <em>last</em>) の場合、数値間隔=1 が設定されます。
	 * (<em>first</em> &gt; <em>last</em>) の場合、数値間隔=(-1) が設定されます。
	 * <em>includeLast</em> に <tt>true</tt> が指定された場合は、範囲終端を含む数値範囲オブジェクトとなります。
	 * <em>includeLast</em> に <tt>false</tt> が指定された場合は、範囲終端を含まない数値範囲オブジェクトとなります。
	 * 次の場合、オブジェクトは生成されますが、数値範囲は無効となり、{@link #isEmpty()} メソッドは <tt>true</tt> を返します。
	 * <ul>
	 * <li>終端を含まない増加方向の数値範囲で (範囲先頭 &gt;= 範囲終端) の場合
	 * <li>終端を含む増加方向の数値範囲で (範囲先頭 &gt; 範囲終端) の場合
	 * <li>終端を含まない減少方向の数値範囲で (範囲先頭 &lt;= 範囲終端) の場合
	 * <li>終端を含む減少方向の数値範囲で (範囲先頭 &lt; 範囲終端) の場合
	 * </ul>
	 * 
	 * @param first			範囲先頭の値
	 * @param last			範囲終端の値
	 * @param includeLast	範囲終端を含める場合は <tt>true</tt>、範囲終端を含めない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>first</em>、<em>last</em> のどれかが <tt>null</tt> の場合
	 */
	public SimpleDecimalRange(BigDecimal first, BigDecimal last, boolean includeLast) {
		this(first, last, (first.compareTo(last) > 0 ? BigDecimal.ONE.negate() : BigDecimal.ONE), includeLast);
	}
	
	/**
	 * 範囲先頭=<em>first</em>、範囲終端=<em>last</em>、数値間隔=<em>step</em> の、範囲終端を含まない数値範囲オブジェクトを生成します。
	 * 次の場合、オブジェクトは生成されますが、数値範囲は無効となり、{@link #isEmpty()} メソッドは <tt>true</tt> を返します。
	 * <ul>
	 * <li>数値間隔が 0 の場合
	 * <li>終端を含まない増加方向の数値範囲で (範囲先頭 &gt;= 範囲終端) の場合
	 * <li>終端を含む増加方向の数値範囲で (範囲先頭 &gt; 範囲終端) の場合
	 * <li>終端を含まない減少方向の数値範囲で (範囲先頭 &lt;= 範囲終端) の場合
	 * <li>終端を含む減少方向の数値範囲で (範囲先頭 &lt; 範囲終端) の場合
	 * </ul>
	 * 
	 * @param first			範囲先頭の値
	 * @param last			範囲終端の値
	 * @param step			範囲の数値間隔
	 * @throws NullPointerException	<em>first</em>、<em>last</em>、<em>step</em> のどれかが <tt>null</tt> の場合
	 */
	public SimpleDecimalRange(BigDecimal first, BigDecimal last, BigDecimal step) {
		this(first, last, step, false);
	}

	/**
	 * 範囲先頭=<em>first</em>、範囲終端=<em>last</em>、数値間隔=<em>step</em> の、数値範囲オブジェクトを生成します。
	 * <em>includeLast</em> に <tt>true</tt> が指定された場合は、範囲終端を含む数値範囲オブジェクトとなります。
	 * <em>includeLast</em> に <tt>false</tt> が指定された場合は、範囲終端を含まない数値範囲オブジェクトとなります。
	 * 次の場合、オブジェクトは生成されますが、数値範囲は無効となり、{@link #isEmpty()} メソッドは <tt>true</tt> を返します。
	 * <ul>
	 * <li>数値間隔が 0 の場合
	 * <li>終端を含まない増加方向の数値範囲で (範囲先頭 &gt;= 範囲終端) の場合
	 * <li>終端を含む増加方向の数値範囲で (範囲先頭 &gt; 範囲終端) の場合
	 * <li>終端を含まない減少方向の数値範囲で (範囲先頭 &lt;= 範囲終端) の場合
	 * <li>終端を含む減少方向の数値範囲で (範囲先頭 &lt; 範囲終端) の場合
	 * </ul>
	 * 
	 * @param first			範囲先頭の値
	 * @param last			範囲終端の値
	 * @param step			範囲の数値間隔
	 * @param includeLast	範囲終端を含める場合は <tt>true</tt>、範囲終端を含めない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>first</em>、<em>last</em>、<em>step</em> のどれかが <tt>null</tt> の場合
	 */
	public SimpleDecimalRange(BigDecimal first, BigDecimal last, BigDecimal step, boolean includeLast) {
		if (first == null)
			throw new NullPointerException("'first' argument is null.");
		if (last == null)
			throw new NullPointerException("'last' argument is null.");
		if (step == null)
			throw new NullPointerException("'step' argument is null.");
		
		first = first.stripTrailingZeros();
		last  = last.stripTrailingZeros();
		step  = step.stripTrailingZeros();
		
		this._includeLast = includeLast;
		this._last  = last;
		this._step  = step;
		
		BigDecimal vFrom = first;
		BigDecimal vTo   = null;
		BigDecimal vStep = null;
		int stepSignum = step.signum();
		int cmp = first.compareTo(last);
		
		if (stepSignum > 0) {
			// incremental
			if (cmp == 0) {
				//--- first == last
				if (includeLast) {
					//--- one number range
					vTo = last;
				}
			}
			else if (cmp < 0) {
				//--- first < last
				BigDecimal dcnt = last.subtract(first).divide(step, 0, RoundingMode.DOWN);
				BigDecimal dmax = dcnt.multiply(step).add(first);
				if (includeLast || dmax.compareTo(last) < 0) {
					//--- (include last) or (dmax < last)
					vTo = dmax.stripTrailingZeros();
				}
				else if (dcnt.compareTo(BigDecimal.ZERO) > 0) {
					//--- (exclude last) and (dcnt > 0)
					vTo = dmax.subtract(step).stripTrailingZeros();
				}
			}
		}
		else if (stepSignum < 0) {
			// decremental
			if (cmp == 0) {
				//--- first == last
				if (includeLast) {
					//--- one number range
					vTo = last;
				}
			}
			else if (cmp > 0) {
				//--- first > last
				BigDecimal dcnt = last.subtract(first).divide(step, 0, RoundingMode.DOWN);
				BigDecimal dmin = dcnt.multiply(step).add(first);
				if (includeLast || dmin.compareTo(last) > 0) {
					//--- (include last) or (dmin > last)
					vTo = dmin.stripTrailingZeros();
				}
				else if (dcnt.compareTo(BigDecimal.ZERO) > 0) {
					//--- (exclude last) and (dcnt > 0)
					vTo = dmin.subtract(step).stripTrailingZeros();
				}
			}
		}
		
		if (vTo != null) {
			// exists range
			vTo = vTo.stripTrailingZeros();
			vStep = step;
			if (RangeUtil.exactlyIterableShortRange(vFrom, vTo, vStep)) {
				// Iterable by Short
				this._impl = new ShortRangeImpl(vFrom.shortValue(), vTo.shortValue(), vStep.shortValue());
			}
			else if (RangeUtil.exactlyIterableIntegerRange(vFrom, vTo, vStep)) {
				// Iterable by Integer
				this._impl = new IntegerRangeImpl(vFrom.intValue(), vTo.intValue(), vStep.intValue());
			}
			else if (RangeUtil.exactlyIterableLongRange(vFrom, vTo, vStep)) {
				// Iterable by Long
				this._impl = new LongRangeImpl(vFrom.longValue(), vTo.longValue(), vStep.longValue());
			}
			else {
				// Iterable by BigDecimal
				this._impl = new BigDecimalRangeImpl(vFrom, vTo, vStep);
			}
		}
		else {
			// empty range
			vStep = BigDecimal.ZERO;
			this._impl = new BigDecimalRangeImpl(first, last, vStep);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 定義されている数値範囲が無効である場合に <tt>true</tt> を返します。
	 * @return	数値範囲が無効であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isEmpty() {
		return _impl.isEmpty();
	}

	/**
	 * 数値範囲内の値に、範囲終端の値を含むよう指定されている場合に <tt>true</tt> を返します。
	 * 範囲の指定によっては、このメソッドが <tt>true</tt> を返す場合でも、範囲終端が含まれない場合があります。
	 * @return	範囲終端の値を含むよう指定されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isIncludeRangeLast() {
		return _includeLast;
	}

	/**
	 * 数値範囲が増加方向か減少方向かを返します。
	 * @return	増加方向の数値範囲の場合は <tt>true</tt>、減少方向の数値範囲の場合は <tt>false</tt>
	 */
	public boolean isIncremental() {
		int stepSignum = _step.signum();
		if (stepSignum < 0) {
			// decremental
			return false;
		} else {
			// incremental
			return true;
		}
	}

	/**
	 * 指定された値が、数値範囲内の要素と等しいかどうかを判定します。
	 * ただし、数値範囲が無効である場合、このメソッドは常に <tt>false</tt> を返します。
	 * @param value	判定する値
	 * @return	数値範囲内の要素と等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * 			数値範囲が無効の場合は <tt>false</tt> を返す。
	 */
	public boolean containsValue(Object value) {
		// 数値を取得
		BigDecimal dval = RangeUtil.toBigDecimal(value);
		if (dval == null) {
			// 比較可能な数値以外なら、false
			return false;
		}
		
		// 数値の判定
		return _impl.containsValue(dval);
	}

	/**
	 * 範囲先頭として設定された値を返します。
	 * @return	範囲先頭
	 */
	public BigDecimal rangeFirst() {
		return _impl.getDecimalFromValue();
	}

	/**
	 * 範囲終端として設定された値を返します。
	 * @return	範囲終端
	 */
	public BigDecimal rangeLast() {
		return _last;
	}

	/**
	 * 数値間隔として設定された値を返します。
	 * @return	数値間隔
	 */
	public BigDecimal rangeStep() {
		return _step;
	}

	/**
	 * 数値範囲の要素の中で最小の値を返します。
	 * ただし、数値範囲が無効である場合は <tt>null</tt> を返します。
	 * @return	数値範囲の要素の最小値を返す。
	 * 			数値範囲が無効の場合は <tt>null</tt> を返す。
	 */
	public BigDecimal rangeMin() {
		int stepSignum = _impl.stepSignum();
		if (stepSignum > 0) {
			// incremental
			return _impl.getDecimalFromValue();
		}
		else if (stepSignum < 0) {
			// decremental
			return _impl.getDecimalToValue();
		}
		else {
			// empty
			return null;
		}
	}

	/**
	 * 数値範囲の要素の中で最大の値を返します。
	 * ただし、数値範囲が無効である場合は <tt>null</tt> を返します。
	 * @return	数値範囲の要素の最大値を返す。
	 * 			数値範囲が無効の場合は <tt>null</tt> を返す。
	 */
	public BigDecimal rangeMax() {
		int stepSignum = _impl.stepSignum();
		if (stepSignum > 0) {
			// incremental
			return _impl.getDecimalToValue();
		}
		else if (stepSignum < 0) {
			// decremental
			return _impl.getDecimalFromValue();
		}
		else {
			// empty
			return null;
		}
	}

	/**
	 * この数値範囲の要素を、範囲先頭から終端まで繰り返し処理する反復子を返します。
	 * @return	数値範囲の要素を繰り返し処理する反復子
	 */
	public Iterator<BigDecimal> iterator() {
		return _impl.getDecimalRangeIterator();
	}

	/**
	 * 数値範囲のハッシュコード値を返します。
	 * <code>Object.hashCode</code> の一般規約によって要求されるように、任意の 2 つの数値範囲 <code>r1</code> と <code>r2</code> で、
	 * <code>r1.equals(r2)</code> であれば、<code>r1.hashCode()==r2.hashCode()</code> となることが保証されます。
	 * @return	ハッシュコード値
	 */
	@Override
	public int hashCode() {
		BigDecimal val;
		
		//--- isIncludeRangeLast
		int h = isIncludeRangeLast() ? 1231 : 1237;
		//--- rangeFirst
		val = rangeFirst();
		h = 31 * h + val.stripTrailingZeros().hashCode();
		//--- rangeLast
		val = rangeLast();
		h = 31 * h + val.stripTrailingZeros().hashCode();
		//--- rangeStep
		val = rangeStep();
		h = 31 * h + val.stripTrailingZeros().hashCode();
		
		return h;
	}

	/**
	 * 指定されたオブジェクトが、この数値範囲と等しいかどうかを比較します。
	 * 指定されたオブジェクトも数値範囲であり、数値範囲の定義が同じ場合に <tt>true</tt> を返します。
	 * 
	 * @param obj	この数値範囲と等しいかどうかが比較される <code>Object</code> 
	 * @return	指定されたオブジェクトが、この数値範囲と等しい場合は <tt>true</tt>
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof SimpleDecimalRange) {
			SimpleDecimalRange arange = (SimpleDecimalRange)obj;
			if (arange._includeLast == this._includeLast
				&& arange._impl.getDecimalFromValue().compareTo(this._impl.getDecimalFromValue()) == 0
				&& arange._last.compareTo(this._last) == 0
				&& arange._step.compareTo(this._step) == 0)
			{
				return true;
			}
		}
		
		return false;
	}

	/**
	 * この数値範囲の文字列表現を返します。
	 * 数値範囲の文字列表現は、次のようなものとなります。
	 * <blockquote>
	 * [<b><i>範囲先頭</i></b> -&gt; <b><i>範囲終端</i></b>, step=<b><i>数値範囲</i></b>, includeLast=<b><i>(true|false)</i></b>]
	 * </blockquote>
	 * @return	この数値範囲の文字列表現
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(valueToString(rangeFirst()));
		sb.append(" -> ");
		sb.append(valueToString(rangeLast()));
		sb.append(", step=");
		sb.append(valueToString(rangeStep()));
		sb.append(", includeLast=");
		sb.append(isIncludeRangeLast());
		sb.append(']');
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定された数値の文字列表現を返す。
	 * 指定された値が <tt>null</tt> の場合は、<tt>null</tt> を返す。
	 * @param value	文字列に変換する値
	 * @return	数値の文字列表現
	 */
	protected String valueToString(BigDecimal value) {
		return (value==null ? null : value.stripTrailingZeros().toPlainString());
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
