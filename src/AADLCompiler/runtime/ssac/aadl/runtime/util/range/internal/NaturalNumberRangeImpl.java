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
 * @(#)NaturalNumberRangeImpl.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range.internal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import ssac.aadl.runtime.util.range.EmptyRangeIterator;
import ssac.aadl.runtime.util.range.RangeUtil;

/**
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
public class NaturalNumberRangeImpl implements NumberRangeImpl
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final int		MAX_INTEGER_RANGE_TOVALUE	= Integer.MAX_VALUE - 1;
	static protected final long		MAX_LONG_RANGE_TOVALUE		= Long.MAX_VALUE - 1L;
	static protected final BigDecimal	MIN_DECIMAL_RANGE_FROMVALUE	= BigDecimal.valueOf(Long.MAX_VALUE);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 連続する数値範囲のリスト **/
	protected NumberRangeImplList	_ranges;
	/** この数値範囲の文字列表現 **/
	protected String	_rangeString = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public NaturalNumberRangeImpl(String rangeString) {
		this(rangeString, null);
	}
	
	public NaturalNumberRangeImpl(String rangeString, BigDecimal maxValue) {
		if (maxValue != null) {
			if (maxValue.signum() < 0)
				throw new IllegalArgumentException("maxValue is negative.");
			if (!RangeUtil.exactlyBigInteger(maxValue))
				throw new IllegalArgumentException("maxValue is not integer number.");
		} else {
			maxValue = BigDecimal.valueOf(Long.MAX_VALUE);
		}
		this._ranges = new NumberRangeImplList();
		initRange(rangeString, maxValue);
		rebuildRangeList();
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
	 * @return	数値範囲が表現可能な数値オブジェクトのクラス。
	 * 			数値範囲が無効の場合は <code>Short.class</code>
	 */
	public Class<?> getValueClass() {
		if (_ranges.isEmpty()) {
			return Short.class;
		} else {
			return getMaxRangeImpl().getValueClass();
		}
	}

	/**
	 * 定義されている数値範囲が無効である場合に <tt>true</tt> を返します。
	 * @return	数値範囲が無効であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isEmpty() {
		return _ranges.isEmpty();
	}
	
	/**
	 * 数値範囲が増加方向か減少方向かを返します。
	 * @return	常に増加方向のため <tt>true</tt>
	 */
	public boolean isIncremental() {
		return true;
	}

	/**
	 * 数値間隔の符号値を返します。
	 * このメソッドが 0 を返す場合、この数値範囲は無効であることを示します。
	 * @return	数値範囲が有効なら 1、そうでない場合は 0
	 */
	public int stepSignum() {
		if (_ranges.isEmpty()) {
			return 0;
		} else {
			return 1;
		}
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
		
		if (value.compareTo(getDecimalFromValue()) >= 0 && value.compareTo(getDecimalToValue()) <= 0) {
			return true;
		} else {
			return false;
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
		if (value != null && !isEmpty()) {
			for (NumberRangeImpl range : _ranges) {
				if (range.containsValue(value)) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * この数値範囲の範囲先頭の値を返します。
	 * このメソッドは、範囲先頭の値を <tt>short</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>short</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#shortValue()} が返す値と同じであり、<tt>short</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalFromValue()} が返す値と異なる場合があります。
	 * @return	<tt>short</tt> に変換された、範囲先頭の値。
	 * 			数値範囲が無効の場合は 0
     */
	public short getShortFromValue() {
		if (isEmpty()) {
			return 0;
		} else {
			return getMinRangeImpl().getShortFromValue();
		}
	}
	
	/**
	 * この数値範囲の範囲先頭の値を返します。
	 * このメソッドは、範囲先頭の値を <tt>int</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>int</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#intValue()} が返す値と同じであり、<tt>int</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalFromValue()} が返す値と異なる場合があります。
	 * @return	<tt>int</tt> に変換された、範囲先頭の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public int getIntegerFromValue() {
		if (isEmpty()) {
			return 0;
		} else {
			return getMinRangeImpl().getIntegerFromValue();
		}
	}
	
	/**
	 * この数値範囲の範囲先頭の値を返します。
	 * このメソッドは、範囲先頭の値を <tt>long</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>long</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#longValue()} が返す値と同じであり、<tt>long</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalFromValue()} が返す値と異なる場合があります。
	 * @return	<tt>long</tt> に変換された、範囲先頭の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public long getLongFromValue() {
		if (isEmpty()) {
			return 0L;
		} else {
			return getMinRangeImpl().getLongFromValue();
		}
	}
	
	/**
	 * この数値範囲の範囲先頭の値を返します。
	 * @return	範囲先頭の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public BigDecimal getDecimalFromValue() {
		if (isEmpty()) {
			return BigDecimal.ZERO;
		} else {
			return getMinRangeImpl().getDecimalFromValue();
		}
	}
	
	/**
	 * この数値範囲の範囲終端の値を返します。
	 * このメソッドは、範囲終端の値を <tt>short</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>short</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#shortValue()} が返す値と同じであり、<tt>short</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalToValue()} が返す値と異なる場合があります。
	 * @return	<tt>short</tt> に変換された、範囲終端の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public short getShortToValue() {
		if (isEmpty()) {
			return 0;
		} else {
			return getMaxRangeImpl().getShortToValue();
		}
	}
	
	/**
	 * この数値範囲の範囲終端の値を返します。
	 * このメソッドは、範囲終端の値を <tt>int</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>int</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#intValue()} が返す値と同じであり、<tt>int</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalToValue()} が返す値と異なる場合があります。
	 * @return	<tt>int</tt> に変換された、範囲終端の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public int getIntegerToValue() {
		if (isEmpty()) {
			return 0;
		} else {
			return getMaxRangeImpl().getIntegerToValue();
		}
	}
	
	/**
	 * この数値範囲の範囲終端の値を返します。
	 * このメソッドは、範囲終端の値を <tt>long</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>long</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#longValue()} が返す値と同じであり、<tt>long</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalToValue()} が返す値と異なる場合があります。
	 * @return	<tt>long</tt> に変換された、範囲終端の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public long getLongToValue() {
		if (isEmpty()) {
			return 0L;
		} else {
			return getMaxRangeImpl().getLongToValue();
		}
	}
	
	/**
	 * この数値範囲の範囲終端の値を返します。
	 * @return	範囲終端の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public BigDecimal getDecimalToValue() {
		if (isEmpty()) {
			return BigDecimal.ZERO;
		} else {
			return getMaxRangeImpl().getDecimalToValue();
		}
	}
	
	/**
	 * この数値範囲の数値間隔の値を返します。
	 * このメソッドは、数値間隔の値を <tt>short</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>short</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#shortValue()} が返す値と同じであり、<tt>short</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalStepValue()} が返す値と異なる場合があります。
	 * @return	<tt>short</tt> に変換された、数値間隔の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public short getShortStepValue() {
		if (isEmpty()) {
			return 0;
		} else {
			return 1;
		}
	}
	
	/**
	 * この数値範囲の数値間隔の値を返します。
	 * このメソッドは、数値間隔の値を <tt>int</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>int</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#intValue()} が返す値と同じであり、<tt>int</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalStepValue()} が返す値と異なる場合があります。
	 * @return	<tt>int</tt> に変換された、数値間隔の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public int getIntegerStepValue() {
		if (isEmpty()) {
			return 0;
		} else {
			return 1;
		}
	}
	
	/**
	 * この数値範囲の数値間隔の値を返します。
	 * このメソッドは、数値間隔の値を <tt>long</tt> に変換します。
	 * この変換は、『Java 言語仕様』で定義された <tt>long</tt> への 「ナロープリミティブ変換」と同様です。
	 * 基本的に、{@link java.math.BigDecimal#longValue()} が返す値と同じであり、<tt>long</tt> 内に収まらない
	 * 値の場合、{@link #getDecimalStepValue()} が返す値と異なる場合があります。
	 * @return	<tt>long</tt> に変換された、数値間隔の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public long getLongStepValue() {
		if (isEmpty()) {
			return 0L;
		} else {
			return 1L;
		}
	}
	
	/**
	 * この数値範囲の数値間隔の値を返します。
	 * @return	数値間隔の値。
	 * 			数値範囲が無効の場合は 0
	 */
	public BigDecimal getDecimalStepValue() {
		if (isEmpty()) {
			return BigDecimal.ZERO;
		} else {
			return BigDecimal.ONE;
		}
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
		if (isEmpty()) {
			return new EmptyRangeIterator<Short>();
		} else {
			return new ShortNaturalNumberIterator();
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
		if (isEmpty()) {
			return new EmptyRangeIterator<Integer>();
		} else {
			return new IntegerNaturalNumberIterator();
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
		if (isEmpty()) {
			return new EmptyRangeIterator<Long>();
		} else {
			return new LongNaturalNumberIterator();
		}
	}
	
	/**
	 * この数値範囲の要素を、範囲先頭から終端まで繰り返し処理する反復子を返します。
	 * @return	数値範囲の要素を繰り返し処理する反復子
	 */
	public Iterator<BigDecimal> getDecimalRangeIterator() {
		if (isEmpty()) {
			return new EmptyRangeIterator<BigDecimal>();
		} else {
			return new BigDecimalNaturalNumberIterator();
		}
	}

	@Override
	public int hashCode() {
		return toRangeString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof NaturalNumberRangeImpl) {
			if (toRangeString().equals(((NaturalNumberRangeImpl)obj).toRangeString())) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * この数値範囲オブジェクトの文字列表現を返します。
	 * この文字列表現は、不連続な数値範囲をカンマで区切り、連続した数値範囲をハイフンで
	 * 区切った文字列となります。
	 * <p>このメソッドが返す文字列は、そのままこのクラスのコンストラクタが解析可能な文字列を表します。
	 * @return 数値範囲を文字列に変換した結果を返す。数値範囲が無効の場合は空文字列を返す。
	 */
	@Override
	public String toString() {
		return toRangeString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * この数値範囲オブジェクトの文字列表現を返します。
	 * この文字列表現は、不連続な数値範囲をカンマで区切り、連続した数値範囲をハイフンで
	 * 区切った文字列となります。
	 * <p>このメソッドが返す文字列は、そのままこのクラスのコンストラクタが解析可能な文字列を表します。
	 * @return 数値範囲を文字列に変換した結果を返す。数値範囲が無効の場合は空文字列を返す。
	 */
	protected String toRangeString() {
		String str = _rangeString;
		if (str == null) {
			StringBuilder sb = new StringBuilder();
			formatRangesString(sb);
			str = sb.toString();
			_rangeString = str;
		}
		return str;
	}
	
	/**
	 * この数値範囲の文字列表現を、指定されたバッファに追加する。
	 * この文字列表現は、数値範囲の文字列フォーマットに準拠したものであり、
	 * 不要な空白や文字などは含めない。
	 * 数値範囲が無効の場合、空文字を返す。
	 * <b>注意：</b>
	 * <blockquote>
	 * 自然数の増加方向を示す数値範囲のみを前提としている。
	 * </blockquote>
	 * @param sb	文字列を追加するバッファ
	 */
	protected void formatRangesString(StringBuilder sb) {
		Iterator<NumberRangeImpl> it = _ranges.iterator();
		if (it.hasNext()) {
			NumberRangeImpl range = it.next();
			formatRangeString(sb, range);
			
			for (; it.hasNext(); ) {
				range = it.next();
				sb.append(NaturalNumberRangeTokenizer.DELIMITER_CHAR);
				formatRangeString(sb, range);
			}
		}
	}

	/**
	 * 指定された数値範囲の文字列表現を、指定されたバッファに追加する。
	 * 単一の数値範囲の場合は、その数値のみ、連続した数値範囲の場合は、
	 * ハイフンで数字を連結した文字列を追加する。
	 * <b>注意：</b>
	 * <blockquote>
	 * 自然数の増加方向を示す数値範囲のみを前提としている。
	 * </blockquote>
	 * @param sb		文字列を追加するバッファ
	 * @param range		文字列表現に変換する数値範囲
	 */
	protected void formatRangeString(StringBuilder sb, final NumberRangeImpl range) {
		if (range instanceof OneNumberRangeImpl) {
			// 単一の値
			sb.append(range.getDecimalFromValue().toPlainString());
		}
		else {
			BigDecimal vf = range.getDecimalFromValue();
			BigDecimal vt = range.getDecimalToValue();
			if (vf.compareTo(vt) == 0) {
				// 範囲先頭と範囲終端が同一の値
				sb.append(vf.toPlainString());
			} else {
				// 範囲先頭と範囲終端が連続的な範囲を示す
				sb.append(vf.toPlainString());
				sb.append(NaturalNumberRangeTokenizer.SERIAL_CHAR);
				sb.append(vt.toPlainString());
			}
		}
	}

	/**
	 * 最小の範囲先頭を持つ数値範囲オブジェクトを返します。
	 * @return 数値範囲オブジェクト
	 * @throws IndexOutOfBoundsException	数値範囲が無効の場合
	 */
	protected NumberRangeImpl getMinRangeImpl() {
		return _ranges.get(0);
	}

	/**
	 * 最大の範囲終端を持つ数値範囲オブジェクトを返します。
	 * @return	数値範囲オブジェクト
	 * @throws IndexOutOfBoundsException	数値範囲が無効の場合
	 */
	protected NumberRangeImpl getMaxRangeImpl() {
		return _ranges.get(_ranges.size()-1);
	}

	/**
	 * <em>value</em> が <em>range</em> に含まれる値かどうかを判定します。
	 * <em>range</em> は間隔 1 の整数数値範囲であることを前提としている。
	 * @param value	判定する数値
	 * @param range	比較する数値範囲
	 * @return	指定された値が数値範囲に含まれていれば 0、数値範囲の最小値よりも小さい場合は -1、
	 * 			数値範囲の最大値よりも大きい場合は 1 を返す。
	 */
	static protected int compareRangeValue(final BigDecimal value, final NumberRangeImpl range) {
		if (range instanceof OneNumberRangeImpl) {
			return value.compareTo(range.getDecimalFromValue());
		} else {
			int cmpFrom = value.compareTo(range.getDecimalFromValue());
			int cmpTo   = value.compareTo(range.getDecimalToValue());
			if (cmpFrom < 0) {
				// (value < range.from <= range.to)
				return (-1);
			}
			else if (cmpTo > 0) {
				// (range.from <= range.to < value)
				return 1;
			}
			else {
				// (range.from <= value <= range.to)
				return 0;
			}
		}
	}

	/**
	 * バイナリサーチアルゴリズムを使用して、指定されたリストから <em>value</em> を含む数値範囲を検索します。
	 * リストは、範囲が重ならない数値範囲オブジェクトのみが格納されており、数値範囲の値によって昇順にソートされていることが前提です。
	 * @param value			検索値
	 * @param rangeList		検索される数値範囲オブジェクトのリスト
	 * @return	リストに検索値がある場合は、その値を包含する数値範囲オブジェクトのインデックス。
	 * 			検索値がリストにない場合は (-(挿入ポイント) - 1)。
	 * 			挿入ポイントとは、リストで検索値が挿入されるポイントであり、検索値より大きな最初の要素のインデックスか、
	 * 			リストのすべての要素が検索値より小さい場合は <code>rangeList.size()</code>。
	 * 			これにより、検索値が数値範囲内に包含される場合にだけ戻り値が >= 0 になることが保証される。
	 */
	static protected int searchRange(final BigDecimal value, final List<NumberRangeImpl> rangeList) {
		int lidx = 0;
		int hidx = rangeList.size() - 1;
		
		while (lidx <= hidx) {
			int midx = (lidx + hidx) >> 1;
			NumberRangeImpl mrange = rangeList.get(midx);
			int cmp = compareRangeValue(value, mrange);

			if (cmp > 0) {
				// mrange.to < value
				lidx = midx + 1;
			}
			else if (cmp < 0) {
				// value < mrange.from
				hidx = midx - 1;
			}
			else {
				// mrange.from <= value <= mrange.to
				return midx;	// range found!
			}
		}
		
		return -(lidx + 1);		// range not found.
	}

	/**
	 * 単一の値を数値範囲オブジェクトとして追加する。
	 * 追加の際、この値を包含する範囲や連続する範囲があればマージする。
	 * @param value	追加する範囲値
	 */
	protected void putOneValue(final BigDecimal value) {
		if (_ranges.isEmpty()) {
			//--- 新規追加
			NumberRangeImpl range = new OneNumberRangeImpl(value);
			_ranges.add(range);
			return;
		}
		
		// 既存の数値範囲を検索
		int index = searchRange(value, _ranges);
		if (index >= 0) {
			// already exists the range that value included
			return;
		}

		// 数値範囲の挿入
		index = -(index + 1);
		int prevIndex, cmpPrev, cmpNext;
		NumberRangeImpl range, prevRange;
		if (index == 0) {
			// リスト先頭に挿入
			prevIndex = -1;
			prevRange = null;
			range = _ranges.get(index);
			cmpPrev = -1;
			cmpNext = range.getDecimalFromValue().compareTo(value.add(BigDecimal.ONE));
		}
		else if (index >= _ranges.size()) {
			index = _ranges.size();
			prevIndex = index - 1;
			prevRange = _ranges.get(prevIndex);
			range = null;
			cmpPrev = prevRange.getDecimalToValue().compareTo(value.subtract(BigDecimal.ONE));
			cmpNext = 1;
		}
		else {
			prevIndex = index - 1;
			prevRange = _ranges.get(prevIndex);
			range = _ranges.get(index);
			cmpPrev = prevRange.getDecimalToValue().compareTo(value.subtract(BigDecimal.ONE));
			cmpNext = range.getDecimalFromValue().compareTo(value.add(BigDecimal.ONE));
		}
		//--- 挿入 or マージ
		if (cmpPrev == 0 && cmpNext == 0) {
			//--- 前後の範囲と連続なら、数値範囲を拡張
			range = new BigDecimalRangeImpl(prevRange.getDecimalFromValue(), range.getDecimalToValue(), BigDecimal.ONE);
			_ranges.set(index, range);
			_ranges.remove(prevIndex);
		}
		else if (cmpPrev == 0) {
			//--- 前の範囲と連続なら、数値範囲を拡張
			range = new BigDecimalRangeImpl(prevRange.getDecimalFromValue(), value, BigDecimal.ONE);
			_ranges.set(prevIndex, range);
		}
		else if (cmpNext == 0) {
			//--- 次の範囲と連続なら、数値範囲を拡張
			range = new BigDecimalRangeImpl(value, range.getDecimalToValue(), BigDecimal.ONE);
			_ranges.set(index, range);
		}
		else {
			//--- 新規数値範囲を挿入
			range = new OneNumberRangeImpl(value);
			_ranges.add(index, range);
		}
	}
	
	/**
	 * 単一の数値範囲を数値範囲オブジェクトとして追加する。
	 * 追加の際、この範囲を包含する範囲や連続する範囲があればマージする。
	 * <b>注意:</b>
	 * <blockquote>
	 * <em>minValue</em> &lt; <em>maxValue</em> であることを前提としている。
	 * </blockquote>
	 * @param minValue	範囲の最小の値
	 * @param maxValue 範囲の最大の値
	 */
	protected void putRangeValue(final BigDecimal minValue, final BigDecimal maxValue) {
		if (_ranges.isEmpty()) {
			//--- 新規追加
			NumberRangeImpl range = new BigDecimalRangeImpl(minValue, maxValue, BigDecimal.ONE);
			_ranges.add(range);
			return;
		}
		
		// 既存の数値範囲を検索
		int rangesLen = _ranges.size();
		BigDecimal vf, vt;
		NumberRangeImpl fromRange = null;
		NumberRangeImpl toRange = null;
		int fromIndex = searchRange(minValue, _ranges);
		int toIndex   = searchRange(maxValue, _ranges);
		
		// 範囲先頭の挿入位置を、必要であれば拡張
		if (fromIndex >= 0) {
			fromRange = _ranges.get(fromIndex);
			vf = fromRange.getDecimalFromValue();
		} else {
			fromIndex = -(fromIndex + 1);
			vf = minValue;
			if (fromIndex > 0) {
				int prevIndex = fromIndex - 1;
				NumberRangeImpl prevRange = _ranges.get(prevIndex);
				if (prevRange.getDecimalToValue().compareTo(minValue.subtract(BigDecimal.ONE)) == 0) {
					//--- 前の範囲と連続なら、数値範囲を拡張
					fromIndex = prevIndex;
					fromRange = prevRange;
					vf = prevRange.getDecimalFromValue();
				}
			}
		}
		
		// 範囲終端の挿入位置を、必要であれば拡張
		if (toIndex >= 0) {
			toRange = _ranges.get(toIndex);
			vt = toRange.getDecimalToValue();
		} else {
			toIndex = -(toIndex + 1);
			vt = maxValue;
			if (toIndex < rangesLen) {
				NumberRangeImpl nextRange = _ranges.get(toIndex);
				if (nextRange.getDecimalFromValue().compareTo(maxValue.add(BigDecimal.ONE)) == 0) {
					//--- 次の範囲と連続なら、数値範囲を拡張
					toRange = nextRange;
					vt = nextRange.getDecimalToValue();
				}
			}
		}
		
		// 範囲設定
		if (fromIndex == toIndex) {
			// 単一範囲の挿入もしくは置換
			if (fromRange == null && toRange == null) {
				// 新規挿入
				NumberRangeImpl range = new BigDecimalRangeImpl(vf, vt, BigDecimal.ONE);
				_ranges.add(fromIndex, range);
			}
			else if (fromRange != toRange) {
				// 新しい範囲で置換
				NumberRangeImpl range = new BigDecimalRangeImpl(vf, vt, BigDecimal.ONE);
				_ranges.set(fromIndex, range);
			}
			// (fromRange == toRange) : 新しい範囲は既存の範囲に包含されているため、変更不要
		}
		else {
			// 単一範囲による複数範囲の置換
			if (toIndex >= rangesLen) {
				toIndex = rangesLen - 1;
			}
			else if (toRange == null) {
				toIndex = toIndex - 1;
			}
			//--- 余分な範囲オブジェクトを除去
			_ranges.removeRange(fromIndex, toIndex);
			//--- 新しい範囲で置換
			NumberRangeImpl range = new BigDecimalRangeImpl(vf, vt, BigDecimal.ONE);
			_ranges.set(fromIndex, range);
		}
	}

	/**
	 * 数値範囲オブジェクトリストの内容を再構成する。
	 * この再構成は、数値範囲オブジェクトを表現可能な数値オブジェクトの数値範囲オブジェクトに変換する。
	 */
	protected void rebuildRangeList() {
		for (int index = 0; index < _ranges.size(); index++) {
			NumberRangeImpl range = _ranges.get(index);
			if (range instanceof BigDecimalRangeImpl) {
				// toValue のチェック
				BigDecimal toValue = range.getDecimalToValue();
				try {
					long lvt = toValue.longValueExact();
					if (lvt <= MAX_INTEGER_RANGE_TOVALUE) {
						// int range values
						// int range values
						range = new IntegerRangeImpl(range.getIntegerFromValue(), range.getIntegerToValue(), 1);
						_ranges.set(index, range);
						continue;
					}
					else if (lvt <= MAX_LONG_RANGE_TOVALUE) {
						// long range values
						range = new LongRangeImpl(range.getLongFromValue(), range.getLongToValue(), 1L);
						_ranges.set(index, range);
						continue;
					}
				} catch (ArithmeticException ignoreEx) {}
				
				// fromValue のチェック
				BigDecimal fromValue = range.getDecimalFromValue();
				//--- check long value
				try {
					long vf = fromValue.longValueExact();
					if (vf < MAX_LONG_RANGE_TOVALUE) {
						// long と BigDecimal の 2 つの数値範囲に分割
						NumberRangeImpl lrange = new LongRangeImpl(vf, MAX_LONG_RANGE_TOVALUE, 1L);
						_ranges.add(index, lrange);
						index++;
						if (MIN_DECIMAL_RANGE_FROMVALUE.compareTo(toValue) == 0) {
							range = new OneNumberRangeImpl(MIN_DECIMAL_RANGE_FROMVALUE);
						} else {
							range = new BigDecimalRangeImpl(MIN_DECIMAL_RANGE_FROMVALUE, toValue, BigDecimal.ONE);
						}
						_ranges.set(index, range);
					}
				} catch (ArithmeticException ignoreEx) {}
				
				// リストは昇順にソートされている前提なので、
				// 以降の値はプリミティブに変換不可能な値とみなし、処理終了
				break;
			}
		}
	}
	
	protected String getIllegalNumberError(final NaturalNumberRangeTokenizer tokenizer) {
		return formatTokenError("Illegal number", tokenizer.getLastTokenPosition(), tokenizer.getLastToken());
	}
	
	protected String getInvalidCharacterError(final NaturalNumberRangeTokenizer tokenizer) {
		return formatTokenError("Invalid characters", tokenizer.getLastTokenPosition(), tokenizer.getLastToken());
	}
	
	protected String getIllegalFormatError(final NaturalNumberRangeTokenizer tokenizer) {
		return getIllegalFormatError(tokenizer.getLastTokenPosition(), tokenizer.getLastToken());
	}
	
	protected String getIllegalFormatError(final int pos, final String token) {
		return formatTokenError("Illegal number range format", pos, token);
	}
	
	protected String formatTokenError(String message, final int pos, final String token) {
		if (message == null || message.length() <= 0) {
			message = "Illegal number range format";
		}
		String errmsg = String.format("%s [pos:%d, str:\"%s\"]", message, (pos+1), String.valueOf(token));
		return errmsg;
	}
	
	protected BigDecimal convertLastTokenToNumber(final NaturalNumberRangeTokenizer tokenizer) {
		try {
			return new BigDecimal(tokenizer.getLastToken());
		}
		catch (NumberFormatException ex) {
			throw new IllegalArgumentException(getIllegalNumberError(tokenizer), ex);
		}
	}
	
	protected void initRange(final String rangeString, final BigDecimal maxValue) {
		NaturalNumberRangeTokenizer tokenizer = new NaturalNumberRangeTokenizer(rangeString);
		int tt = NaturalNumberRangeTokenizer.INVALID;
		BigDecimal lastValue = null;
		
		do {
			// トークンを取得
			tt = tokenizer.nextToken();
			
			// トークン判別
			if (NaturalNumberRangeTokenizer.SERIAL == tt) {
				// <<連続マーク(ハイフン)>>
				final int lastTokenPos = tokenizer.getLastTokenPosition();
				final String lastToken = tokenizer.getLastToken();
				//--- 次のトークンを読み込む
				tt = tokenizer.nextToken();
				if (NaturalNumberRangeTokenizer.DELIMITER == tt || NaturalNumberRangeTokenizer.EOT == tt) {
					// <<区切りマーク、またはトークン終端>>
					if (lastValue == null) {
						//--- 連続マークのみの場合はエラー
						String msg = getIllegalFormatError(lastTokenPos, lastToken);
						throw new IllegalArgumentException(msg);
					}
					int cmp = maxValue.compareTo(lastValue);
					if (cmp == 0) {
						//--- (maxValue == lastValue) put one value
						putOneValue(lastValue);
					}
					else if (cmp > 0) {
						//--- (maxValue > lastValue) put range value
						putRangeValue(lastValue, maxValue);
					}
				}
				else if (NaturalNumberRangeTokenizer.NUMBER == tt) {
					// <<数字>>
					BigDecimal vt = convertLastTokenToNumber(tokenizer);
					BigDecimal vf = lastValue;
					lastValue = vt;	// 次のトークンチェックのため、保存
					int cmp = maxValue.compareTo(vt);
					if (vf == null) {
						//--- 範囲先頭が未定義の場合、先頭は 1 とする
						vf = BigDecimal.ONE;
					}
					int cmpLast = maxValue.compareTo(vf);
					if (cmp >= 0 || cmpLast >= 0) {
						//--- (vt <= maxValue || vf <= maxValue)
						if (cmp < 0)
							vt = maxValue;
						if (cmpLast < 0)
							vf = maxValue;
						cmp = vf.compareTo(vt);
						if (cmp < 0) {
							// (vf < vt)
							putRangeValue(vf, vt);
						}
						else if (cmp > 0) {
							// (vf > vt)
							putRangeValue(vt, vf);
						}
						else {
							// (vf == vt)
							putOneValue(lastValue);
						}
					}
					//--- (vt > maxValue && vf > maxValue) の場合は、無視
				}
				else {
					throw new IllegalArgumentException(getIllegalFormatError(tokenizer));
				}
				//--- clear
				lastValue = null;
			}
			else if (NaturalNumberRangeTokenizer.DELIMITER == tt) {
				// <<区切りマーク(カンマ)>>
				if (lastValue != null) {
					if (maxValue.compareTo(lastValue) >= 0) {
						// 最大値を超える単一の数値は無視
						putOneValue(lastValue);
					}
					//--- clear
					lastValue = null;
				}
				//--- 区切りマークのみなら無視
			}
			else if (NaturalNumberRangeTokenizer.NUMBER == tt) {
				// <<数字>>
				if (lastValue != null) {
					// 2 つの数値がデリミタ以外で連続している場合は、不正書式とみなす
					throw new IllegalArgumentException(getIllegalFormatError(tokenizer));
				}
				lastValue = convertLastTokenToNumber(tokenizer);
			}
			else if (NaturalNumberRangeTokenizer.INVALID == tt) {
				// <<不正文字>>
				throw new IllegalArgumentException(getInvalidCharacterError(tokenizer));
			}
		} while (NaturalNumberRangeTokenizer.EOT != tt);
		
		// 数字のみが残っている場合は、範囲値に追加
		if (lastValue != null) {
			if (maxValue.compareTo(lastValue) >= 0) {
				// 最大値を超える単一の数値は無視
				putOneValue(lastValue);
			}
			//--- clear
			lastValue = null;
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * 数値範囲オブジェクトを格納するリストクラス。
	 * 連続複数要素を削除する機能を利用するために実装。
	 */
	static protected class NumberRangeImplList extends ArrayList<NumberRangeImpl>
	{
		public NumberRangeImplList() {
			super();
		}

		public NumberRangeImplList(Collection<? extends NumberRangeImpl> c) {
			super(c);
		}

		public NumberRangeImplList(int initialCapacity) {
			super(initialCapacity);
		}

		@Override
		public void removeRange(int fromIndex, int toIndex) {
			super.removeRange(fromIndex, toIndex);
		}
	}

	/**
	 * 専用のイテレータとなる共通実装
	 */
	protected abstract class NaturalNumberIterator<E> implements Iterator<E>
	{
		protected int			_rangesLen = _ranges.size();
		protected int			_nextIndex = 1;
		protected Iterator<E>	_curIterator;
		
		public NaturalNumberIterator(Iterator<E> initialIterator) {
			_curIterator = initialIterator;
		}
		
		public boolean hasNext() {
			return (_curIterator != null);
		}
		
		public E next() {
			if (_curIterator != null) {
				E ret = _curIterator.next();
				
				if (!_curIterator.hasNext()) {
					//--- get Iterator from the next NumberRangeImpl
					for (; _nextIndex < _rangesLen; ) {
						_curIterator = getRangeIterator(_ranges.get(_nextIndex));
						++_nextIndex;
						if (_curIterator.hasNext()) {
							return ret;
						}
					}
					//--- no more Iterator
					_curIterator = null;
				}
				
				return ret;
			}
			
			// no more Elements
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		abstract protected Iterator<E> getRangeIterator(final NumberRangeImpl rangeImpl);
	}

	/**
	 * <tt>Short</tt> オブジェクトを返すイテレータ
	 */
	protected class ShortNaturalNumberIterator extends NaturalNumberIterator<Short>
	{
		public ShortNaturalNumberIterator() {
			super(getMinRangeImpl().getShortRangeIterator());
		}

		@Override
		protected Iterator<Short> getRangeIterator(NumberRangeImpl rangeImpl) {
			return rangeImpl.getShortRangeIterator();
		}
	}
	
	/**
	 * <tt>Integer</tt> オブジェクトを返すイテレータ
	 */
	protected class IntegerNaturalNumberIterator extends NaturalNumberIterator<Integer>
	{
		public IntegerNaturalNumberIterator() {
			super(getMinRangeImpl().getIntegerRangeIterator());
		}

		@Override
		protected Iterator<Integer> getRangeIterator(NumberRangeImpl rangeImpl) {
			return rangeImpl.getIntegerRangeIterator();
		}
	}
	
	/**
	 * <tt>Long</tt> オブジェクトを返すイテレータ
	 */
	protected class LongNaturalNumberIterator extends NaturalNumberIterator<Long>
	{
		public LongNaturalNumberIterator() {
			super(getMinRangeImpl().getLongRangeIterator());
		}

		@Override
		protected Iterator<Long> getRangeIterator(NumberRangeImpl rangeImpl) {
			return rangeImpl.getLongRangeIterator();
		}
	}
	
	/**
	 * <tt>BigDecimal</tt> オブジェクトを返すイテレータ
	 */
	protected class BigDecimalNaturalNumberIterator extends NaturalNumberIterator<BigDecimal>
	{
		public BigDecimalNaturalNumberIterator() {
			super(getMinRangeImpl().getDecimalRangeIterator());
		}

		@Override
		protected Iterator<BigDecimal> getRangeIterator(NumberRangeImpl rangeImpl) {
			return rangeImpl.getDecimalRangeIterator();
		}
	}
}
