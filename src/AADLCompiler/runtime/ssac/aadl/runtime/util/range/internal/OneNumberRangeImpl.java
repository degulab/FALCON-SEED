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
 * @(#)OneNumberRangeImpl.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range.internal;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ssac.aadl.runtime.util.range.RangeUtil;

/**
 * 唯一の値を保持する数値範囲オブジェクト。
 * この数値範囲では、数値間隔=1 で、範囲先頭と範囲終端の値が等しい数値範囲となります。
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
public class OneNumberRangeImpl implements NumberRangeImpl
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 数値範囲唯一の値 **/
	protected final BigDecimal	_value;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public OneNumberRangeImpl(final long value) {
		this._value = BigDecimal.valueOf(value);
	}
	
	public OneNumberRangeImpl(final double value) {
		this._value = BigDecimal.valueOf(value);
	}
	
	public OneNumberRangeImpl(final BigDecimal value) {
		if (value == null)
			throw new NullPointerException();
		this._value = value;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトが表現可能な数値範囲の数値オブジェクトのクラスを返します。
	 * <p>このメソッドが <code>Short.class</code> を返す場合、
	 * {@link #getShortFromValue()}、{@link #getShortToValue()}、{@link #getShortStepValue()}、{@link #getShortRangeIterator()} の
	 * 返す数値が、<code>short</code> の範囲内の数値であることを保証します。
	 * <p>このメソッドが <code>Integer.class</code> を返す場合、
	 * {@link #getIntegerFromValue()}、{@link #getIntegerToValue()}、{@link #getIntegerStepValue()}、{@link #getIntegerRangeIterator()} の
	 * 返す数値が、<code>int</code> の範囲内の数値であることを保証します。
	 * <p>このメソッドが <code>Long.class</code> を返す場合、
	 * {@link #getLongFromValue()}、{@link #getLongToValue()}、{@link #getLongStepValue()}、{@link #getLongRangeIterator()} の
	 * 返す数値が、<code>long</code> の範囲内の数値であることを保証します。
	 * @return	数値範囲が表現可能な数値オブジェクトのクラス
	 */
	public Class<?> getValueClass() {
		if (RangeUtil.exactlyLong(_value)) {
			if (RangeUtil.exactlyShort(_value)) {
				return Short.class;
			}
			else if (RangeUtil.exactlyInteger(_value)) {
				return Integer.class;
			}
			return Long.class;
		}
		return _value.getClass();
	}

	/**
	 * 定義されている数値範囲が無効である場合に <tt>true</tt> を返します。
	 * @return	常に <tt>true</tt>
	 */
	public boolean isEmpty() {
		return false;
	}
	
	/**
	 * 数値範囲が増加方向か減少方向かを返します。
	 * @return	常に <tt>true</tt>
	 */
	public boolean isIncremental() {
		return true;
	}

	/**
	 * 数値間隔の符号値を返します。
	 * このメソッドが 0 を返す場合、この数値範囲は無効であることを示します。
	 * @return	常に 1
	 */
	public int stepSignum() {
		return 1;
	}

	/**
	 * 指定された数値が数値範囲内の数値かどうかを判定します。
	 * 数値範囲内の数値とは、指定された数値が、{@link #getDecimalFromValue()} と {@link #getDecimalToValue()} の
	 * 範囲内かどうかで判定します。
	 * @param value	判定対象の数値
	 * @return	数値範囲内であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isIncludeValue(BigDecimal value) {
		return containsValue(value);
	}

	/**
	 * 指定された数値が数値範囲オブジェクトの要素かどうかを判定します。
	 * この判定では、範囲先頭の値と数値間隔によって構成される
	 * 数値範囲オブジェクトの要素と等しいかどうかを判定します。
	 * @param value	判定対象の数値
	 * @return	数値範囲の要素と等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsValue(BigDecimal value) {
		if (value != null) {
			return (value.compareTo(getDecimalFromValue()) == 0);
		} else {
			return false;
		}
	}

	/**
	 * この数値範囲の範囲先頭の値を返します。
	 * このメソッドは、範囲先頭の値を <tt>short</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>short</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.lang.Number#shortValue()} が返す値と同じであり、<tt>short</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalFromValue()} が返す値と異なる場合があります。
	 * @return	<tt>short</tt> に変換された、範囲先頭の値
     */
	public short getShortFromValue() {
		return _value.shortValue();
	}
	
	/**
	 * この数値範囲の範囲先頭の値を返します。
	 * このメソッドは、範囲先頭の値を <tt>int</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>int</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.lang.Number#intValue()} が返す値と同じであり、<tt>int</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalFromValue()} が返す値と異なる場合があります。
	 * @return	<tt>int</tt> に変換された、範囲先頭の値
	 */
	public int getIntegerFromValue() {
		return _value.intValue();
	}
	
	/**
	 * この数値範囲の範囲先頭の値を返します。
	 * このメソッドは、範囲先頭の値を <tt>long</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>long</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.lang.Number#longValue()} が返す値と同じであり、<tt>long</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalFromValue()} が返す値と異なる場合があります。
	 * @return	<tt>long</tt> に変換された、範囲先頭の値
	 */
	public long getLongFromValue() {
		return _value.longValue();
	}
	
	/**
	 * この数値範囲の範囲先頭の値を返します。
	 * @return	範囲先頭の値
	 */
	public BigDecimal getDecimalFromValue() {
		return _value;
	}
	
	/**
	 * この数値範囲の範囲終端の値を返します。
	 * このメソッドは、範囲終端の値を <tt>short</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>short</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.lang.Number#shortValue()} が返す値と同じであり、<tt>short</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalToValue()} が返す値と異なる場合があります。
	 * @return	<tt>short</tt> に変換された、範囲終端の値
	 */
	public short getShortToValue() {
		return _value.shortValue();
	}
	
	/**
	 * この数値範囲の範囲終端の値を返します。
	 * このメソッドは、範囲終端の値を <tt>int</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>int</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.lang.Number#intValue()} が返す値と同じであり、<tt>int</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalToValue()} が返す値と異なる場合があります。
	 * @return	<tt>int</tt> に変換された、範囲終端の値
	 */
	public int getIntegerToValue() {
		return _value.intValue();
	}
	
	/**
	 * この数値範囲の範囲終端の値を返します。
	 * このメソッドは、範囲終端の値を <tt>long</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>long</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.lang.Number#longValue()} が返す値と同じであり、<tt>long</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalToValue()} が返す値と異なる場合があります。
	 * @return	<tt>long</tt> に変換された、範囲終端の値
	 */
	public long getLongToValue() {
		return _value.longValue();
	}
	
	/**
	 * この数値範囲の範囲終端の値を返します。
	 * @return	範囲終端の値
	 */
	public BigDecimal getDecimalToValue() {
		return _value;
	}
	
	/**
	 * この数値範囲の数値間隔の値を返します。
	 * このメソッドは、数値間隔の値を <tt>short</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>short</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.lang.Number#shortValue()} が返す値と同じであり、<tt>short</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalStepValue()} が返す値と異なる場合があります。
	 * @return	常に 1
	 */
	public short getShortStepValue() {
		return 1;
	}
	
	/**
	 * この数値範囲の数値間隔の値を返します。
	 * このメソッドは、数値間隔の値を <tt>int</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>int</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.lang.Number#intValue()} が返す値と同じであり、<tt>int</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalStepValue()} が返す値と異なる場合があります。
	 * @return	常に 1
	 */
	public int getIntegerStepValue() {
		return 1;
	}
	
	/**
	 * この数値範囲の数値間隔の値を返します。
	 * このメソッドは、数値間隔の値を <tt>long</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>long</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.lang.Number#longValue()} が返す値と同じであり、<tt>long</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalStepValue()} が返す値と異なる場合があります。
	 * @return	常に 1
	 */
	public long getLongStepValue() {
		return 1L;
	}
	
	/**
	 * この数値範囲の数値間隔の値を返します。
	 * @return	常に 1
	 */
	public BigDecimal getDecimalStepValue() {
		return BigDecimal.ONE;
	}
	
	/**
	 * この数値範囲の要素を、範囲先頭から終端まで繰り返し処理する反復子を返します。
	 * このメソッドは、反復子が返す値を <tt>short</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>short</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.lang.Number#shortValue()} が返す値と同じであり、<tt>short</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalRangeIterator()} が返す反復子の値と異なる場合があります。
	 * @return	<tt>short</tt> に変換された値を返す、数値範囲の要素を繰り返し処理する反復子
	 */
	public Iterator<Short> getShortRangeIterator() {
		return new OneNumberRangeIteratorImpl<Short>() {
			public Short next() {
				if (_hasNext) {
					_hasNext = false;
					return _value.shortValue();
				} else {
					throw new NoSuchElementException();
				}
			}
		};
	}
	
	/**
	 * この数値範囲の要素を、範囲先頭から終端まで繰り返し処理する反復子を返します。
	 * このメソッドは、反復子が返す値を <tt>int</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>int</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.lang.Number#intValue()} が返す値と同じであり、<tt>int</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalRangeIterator()} が返す反復子の値と異なる場合があります。
	 * @return	<tt>int</tt> に変換された値を返す、数値範囲の要素を繰り返し処理する反復子
	 */
	public Iterator<Integer> getIntegerRangeIterator() {
		return new OneNumberRangeIteratorImpl<Integer>() {
			public Integer next() {
				if (_hasNext) {
					_hasNext = false;
					return _value.intValue();
				} else {
					throw new NoSuchElementException();
				}
			}
		};
	}
	
	/**
	 * この数値範囲の要素を、範囲先頭から終端まで繰り返し処理する反復子を返します。
	 * このメソッドは、反復子が返す値を <tt>long</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>long</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.lang.Number#longValue()} が返す値と同じであり、<tt>long</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalRangeIterator()} が返す反復子の値と異なる場合があります。
	 * @return	<tt>long</tt> に変換された値を返す、数値範囲の要素を繰り返し処理する反復子
	 */
	public Iterator<Long> getLongRangeIterator() {
		return new OneNumberRangeIteratorImpl<Long>() {
			public Long next() {
				if (_hasNext) {
					_hasNext = false;
					return _value.longValue();
				} else {
					throw new NoSuchElementException();
				}
			}
		};
	}
	
	/**
	 * この数値範囲の要素を、範囲先頭から終端まで繰り返し処理する反復子を返します。
	 * @return	数値範囲の要素を繰り返し処理する反復子
	 */
	public Iterator<BigDecimal> getDecimalRangeIterator() {
		return new OneNumberRangeIteratorImpl<BigDecimal>() {
			public BigDecimal next() {
				if (_hasNext) {
					_hasNext = false;
					return _value;
				} else {
					throw new NoSuchElementException();
				}
			}
		};
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected abstract class OneNumberRangeIteratorImpl<E> implements Iterator<E>
	{
		protected boolean	_hasNext = true;

		public boolean hasNext() {
			return _hasNext;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
