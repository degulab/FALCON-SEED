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
 * @(#)BigDecimalRangeImpl.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range.internal;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.NoSuchElementException;

import ssac.aadl.runtime.util.range.EmptyRangeIterator;

/**
 * <tt>BigDecimal</tt> で表現可能な数値範囲オブジェクト。
 * この数値範囲では、範囲先頭、範囲終端、数値間隔を保持し、
 * 範囲終端を含む数値範囲を表します。
 * また、数値間隔が増加方向でも (範囲先頭 > 範囲終端) の場合や、
 * 減少方向でも (範囲先頭 < 範囲終端) の場合、数値範囲は無効となります。
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
public class BigDecimalRangeImpl implements NumberRangeImpl
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 範囲先頭 **/
	protected BigDecimal	_from;
	/** 範囲終端 **/
	protected BigDecimal	_to;
	/** 数値間隔 **/
	protected BigDecimal	_step;
	/** 数値間隔の符号値 **/
	protected int			_stepSignum;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public BigDecimalRangeImpl(BigDecimal from, BigDecimal to, BigDecimal step) {
		int stepSignum = step.signum();

		this._from = from;
		this._to   = to;
		if (stepSignum > 0) {
			// incremental
			if (from.compareTo(to) > 0) {
				// empty : from > to
				step = BigDecimal.ZERO;
			}
		}
		else if (stepSignum < 0) {
			// decremental
			if (from.compareTo(to) < 0) {
				// empty : from < to
				step = BigDecimal.ZERO;
			}
		}
		else {
			// empty
			step = BigDecimal.ZERO;
		}
		this._step = step;
		this._stepSignum = step.signum();
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
		Class<?> retClazz;
		
		try {
			long lf = _from.longValueExact();
			long lt = _to.longValueExact();
			long ls = _step.longValueExact();

			if (((short)lf == lf) && ((short)lt == lt) && ((short)ls == ls)) {
				retClazz = Short.class;
			}
			else if (((int)lf == lf) && ((int)lt == lt) && ((int)ls == ls)) {
				retClazz = Integer.class;
			}
			else {
				retClazz = Long.class;
			}
		}
		catch (ArithmeticException ex) {
			retClazz = BigDecimal.class;
		}
		
		return retClazz;
	}

	/**
	 * 定義されている数値範囲が無効である場合に <tt>true</tt> を返します。
	 * @return	数値範囲が無効であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isEmpty() {
		return (_stepSignum == 0);
	}
	
	/**
	 * 数値範囲が増加方向か減少方向かを返します。
	 * @return	増加方向の数値範囲の場合は <tt>true</tt>、減少方向の数値範囲の場合は <tt>false</tt>
	 */
	public boolean isIncremental() {
		return (_stepSignum >= 0);
	}

	/**
	 * 数値間隔の符号値を返します。
	 * このメソッドが 0 を返す場合、この数値範囲は無効であることを示します。
	 * @return	数値間隔の値が負の場合は -1、ゼロの場合は 0、正の場合は 1
	 */
	public int stepSignum() {
		return _stepSignum;
	}

	/**
	 * 指定された数値が数値範囲内の数値かどうかを判定します。
	 * 数値範囲内の数値とは、指定された数値が、{@link #getDecimalFromValue()} と {@link #getDecimalToValue()} の
	 * 範囲内かどうかで判定します。
	 * @param value	判定対象の数値
	 * @return	数値範囲内であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isIncludeValue(BigDecimal value) {
		if (value == null || isEmpty()) {
			return false;
		}
		
		if (_stepSignum > 0) {
			// incremental
			if (value.compareTo(_from) >= 0 && value.compareTo(_to) <= 0) {
				return true;
			} else {
				return false;
			}
		} else {
			// decremental
			if (value.compareTo(_to) >= 0 && value.compareTo(_from) <= 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 指定された数値が数値範囲オブジェクトの要素かどうかを判定します。
	 * この判定では、範囲先頭の値と数値間隔によって構成される
	 * 数値範囲オブジェクトの要素と等しいかどうかを判定します。
	 * @param value	判定対象の数値
	 * @return	数値範囲の要素と等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsValue(BigDecimal value) {
		if (!isIncludeValue(value)) {
			return false;
		}
		
		// 要素の数値か判定
		if (BigDecimal.ZERO.compareTo(value.subtract(_from).remainder(_step)) == 0) {
			// (value - _from) % _step == 0
			return true;
		} else {
			// (value - _from) % _step != 0
			return false;
		}
	}

	/**
	 * この数値範囲の範囲先頭の値を返します。
	 * このメソッドは、範囲先頭の値を <tt>short</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>short</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#shortValue()} が返す値と同じであり、<tt>short</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalFromValue()} が返す値と異なる場合があります。
	 * @return	<tt>short</tt> に変換された、範囲先頭の値
     */
	public short getShortFromValue() {
		return _from.shortValue();
	}
	
	/**
	 * この数値範囲の範囲先頭の値を返します。
	 * このメソッドは、範囲先頭の値を <tt>int</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>int</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#intValue()} が返す値と同じであり、<tt>int</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalFromValue()} が返す値と異なる場合があります。
	 * @return	<tt>int</tt> に変換された、範囲先頭の値
	 */
	public int getIntegerFromValue() {
		return _from.intValue();
	}
	
	/**
	 * この数値範囲の範囲先頭の値を返します。
	 * このメソッドは、範囲先頭の値を <tt>long</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>long</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#longValue()} が返す値と同じであり、<tt>long</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalFromValue()} が返す値と異なる場合があります。
	 * @return	<tt>long</tt> に変換された、範囲先頭の値
	 */
	public long getLongFromValue() {
		return _from.longValue();
	}
	
	/**
	 * この数値範囲の範囲先頭の値を返します。
	 * @return	範囲先頭の値
	 */
	public BigDecimal getDecimalFromValue() {
		return _from;
	}
	
	/**
	 * この数値範囲の範囲終端の値を返します。
	 * このメソッドは、範囲終端の値を <tt>short</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>short</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#shortValue()} が返す値と同じであり、<tt>short</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalToValue()} が返す値と異なる場合があります。
	 * @return	<tt>short</tt> に変換された、範囲終端の値
	 */
	public short getShortToValue() {
		return _to.shortValue();
	}
	
	/**
	 * この数値範囲の範囲終端の値を返します。
	 * このメソッドは、範囲終端の値を <tt>int</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>int</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#intValue()} が返す値と同じであり、<tt>int</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalToValue()} が返す値と異なる場合があります。
	 * @return	<tt>int</tt> に変換された、範囲終端の値
	 */
	public int getIntegerToValue() {
		return _to.intValue();
	}
	
	/**
	 * この数値範囲の範囲終端の値を返します。
	 * このメソッドは、範囲終端の値を <tt>long</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>long</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#longValue()} が返す値と同じであり、<tt>long</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalToValue()} が返す値と異なる場合があります。
	 * @return	<tt>long</tt> に変換された、範囲終端の値
	 */
	public long getLongToValue() {
		return _to.longValue();
	}
	
	/**
	 * この数値範囲の範囲終端の値を返します。
	 * @return	範囲終端の値
	 */
	public BigDecimal getDecimalToValue() {
		return _to;
	}
	
	/**
	 * この数値範囲の数値間隔の値を返します。
	 * このメソッドは、数値間隔の値を <tt>short</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>short</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#shortValue()} が返す値と同じであり、<tt>short</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalStepValue()} が返す値と異なる場合があります。
	 * @return	<tt>short</tt> に変換された、数値間隔の値
	 */
	public short getShortStepValue() {
		return _step.shortValue();
	}
	
	/**
	 * この数値範囲の数値間隔の値を返します。
	 * このメソッドは、数値間隔の値を <tt>int</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>int</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#intValue()} が返す値と同じであり、<tt>int</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalStepValue()} が返す値と異なる場合があります。
	 * @return	<tt>int</tt> に変換された、数値間隔の値
	 */
	public int getIntegerStepValue() {
		return _step.intValue();
	}
	
	/**
	 * この数値範囲の数値間隔の値を返します。
	 * このメソッドは、数値間隔の値を <tt>long</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>long</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#longValue()} が返す値と同じであり、<tt>long</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalStepValue()} が返す値と異なる場合があります。
	 * @return	<tt>long</tt> に変換された、数値間隔の値
	 */
	public long getLongStepValue() {
		return _step.longValue();
	}
	
	/**
	 * この数値範囲の数値間隔の値を返します。
	 * @return	数値間隔の値
	 */
	public BigDecimal getDecimalStepValue() {
		return _step;
	}
	
	/**
	 * この数値範囲の要素を、範囲先頭から終端まで繰り返し処理する反復子を返します。
	 * このメソッドは、反復子が返す値を <tt>short</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>short</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#shortValue()} が返す値と同じであり、<tt>short</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalRangeIterator()} が返す反復子の値と異なる場合があります。
	 * @return	<tt>short</tt> に変換された値を返す、数値範囲の要素を繰り返し処理する反復子
	 */
	public Iterator<Short> getShortRangeIterator() {
		if (_stepSignum > 0) {
			// incremental
			return new IncrementalRangeIteratorImpl<Short>() {
				public Short next() {
					return getNextValue().shortValue();
				}
			};
		}
		else if (_stepSignum < 0) {
			// decremental
			return new DecrementalRangeIteratorImpl<Short>() {
				public Short next() {
					return getNextValue().shortValue();
				}
			};
		}
		else {
			// empty
			return new EmptyRangeIterator<Short>();
		}
	}
	
	/**
	 * この数値範囲の要素を、範囲先頭から終端まで繰り返し処理する反復子を返します。
	 * このメソッドは、反復子が返す値を <tt>int</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>int</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#intValue()} が返す値と同じであり、<tt>int</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalRangeIterator()} が返す反復子の値と異なる場合があります。
	 * @return	<tt>int</tt> に変換された値を返す、数値範囲の要素を繰り返し処理する反復子
	 */
	public Iterator<Integer> getIntegerRangeIterator() {
		if (_stepSignum > 0) {
			// incremental
			return new IncrementalRangeIteratorImpl<Integer>() {
				public Integer next() {
					return getNextValue().intValue();
				}
			};
		}
		else if (_stepSignum < 0) {
			// decremental
			return new DecrementalRangeIteratorImpl<Integer>() {
				public Integer next() {
					return getNextValue().intValue();
				}
			};
		}
		else {
			// empty
			return new EmptyRangeIterator<Integer>();
		}
	}
	
	/**
	 * この数値範囲の要素を、範囲先頭から終端まで繰り返し処理する反復子を返します。
	 * このメソッドは、反復子が返す値を <tt>long</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>long</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#longValue()} が返す値と同じであり、<tt>long</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalRangeIterator()} が返す反復子の値と異なる場合があります。
	 * @return	<tt>long</tt> に変換された値を返す、数値範囲の要素を繰り返し処理する反復子
	 */
	public Iterator<Long> getLongRangeIterator() {
		if (_stepSignum > 0) {
			// incremental
			return new IncrementalRangeIteratorImpl<Long>() {
				public Long next() {
					return getNextValue().longValue();
				}
			};
		}
		else if (_stepSignum < 0) {
			// decremental
			return new DecrementalRangeIteratorImpl<Long>() {
				public Long next() {
					return getNextValue().longValue();
				}
			};
		}
		else {
			// empty
			return new EmptyRangeIterator<Long>();
		}
	}
	
	/**
	 * この数値範囲の要素を、範囲先頭から終端まで繰り返し処理する反復子を返します。
	 * @return	数値範囲の要素を繰り返し処理する反復子
	 */
	public Iterator<BigDecimal> getDecimalRangeIterator() {
		if (_stepSignum > 0) {
			// incremental
			return new IncrementalRangeIteratorImpl<BigDecimal>() {
				public BigDecimal next() {
					return getNextValue();
				}
			};
		}
		else if (_stepSignum < 0) {
			// decremental
			return new DecrementalRangeIteratorImpl<BigDecimal>() {
				public BigDecimal next() {
					return getNextValue();
				}
			};
		}
		else {
			// empty
			return new EmptyRangeIterator<BigDecimal>();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected abstract class RangeIteratorImpl<E> implements Iterator<E> {
		protected BigDecimal	_curValue = _from;
		protected boolean	_valid = true;

		public boolean hasNext() {
			return _valid;
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		abstract protected BigDecimal getNextValue();
	}
	
	protected abstract class IncrementalRangeIteratorImpl<E> extends RangeIteratorImpl<E> {
		protected BigDecimal getNextValue() {
			if (_valid) {
				BigDecimal retValue = _curValue;
				_curValue = _curValue.add(_step);
				if (_curValue.compareTo(_to) > 0) {
					_valid = false;
				}
				return retValue;
			} else {
				throw new NoSuchElementException();
			}
		}
	}
	
	protected abstract class DecrementalRangeIteratorImpl<E> extends RangeIteratorImpl<E> {
		protected BigDecimal getNextValue() {
			if (_valid) {
				BigDecimal retValue = _curValue;
				_curValue = _curValue.add(_step);
				if (_curValue.compareTo(_to) < 0) {
					_valid = false;
				}
				return retValue;
			} else {
				throw new NoSuchElementException();
			}
		}
	}
}
