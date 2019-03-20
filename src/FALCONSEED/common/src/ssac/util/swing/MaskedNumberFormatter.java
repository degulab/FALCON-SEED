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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MaskedNumberFormatter.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

/**
 * 数値以外の文字入力を受け付けないフォーマッタークラス。
 * このクラスは、{@link javax.swing.text.NumberFormatter} から派生し、
 * <code>NumberFormatter</code> の基本機能を使用している。<br>
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.14
 */
public class MaskedNumberFormatter extends NumberFormatter
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

	/**
	 * デフォルトの <code>MaskedNumberFormatter</code> インスタンスを生成する。
	 */
	public MaskedNumberFormatter() {
		super();
		setup();
	}

	/**
	 * 指定した <code>Format</code> インスタンスを使用して、
	 * <code>MaskedNumberFormatter</code> インスタンスを生成する。
	 * @param format	正当な値の規定に使用する書式
	 */
	public MaskedNumberFormatter(NumberFormat format) {
		super(format);
		setup();
	}

	/**
	 * 文字列を値に変換するときの、値評価の振る舞いを設定する。
	 */
	protected void setup() {
		setAllowsInvalid(false);		// 不正な文字の入力を許可しない(デフォルト=true:なんで？)
		setCommitsOnValidEdit(true);	// 文字入力の度に、commitEdit 呼び出し(デフォルト=false)
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * <code>text</code> の <code>Object</code> 表現を返す。
	 * この実装では、最小値もしくは最大値が取得可能で、入力された文字列が変換可能な場合、
	 * 最小値を下回る場合は最小値に、最大値を上回る場合は最大値にクリップされた値を返す。
	 * 
	 * @param text	変換する <code>String</code>
	 * @return	テキストの <code>Object</code> 表現
	 * @throws ParseException	変換でエラーが発生した場合
	 */
	@Override
	public Object stringToValue(String text) throws ParseException {
		try {
			return super.stringToValue(text);
		}
		catch (ParseException ex) {
			Object parsedValue = getFormat().parseObject(text);
			
			// check minimum value
			Comparable min = getMinimum();
			if (min != null) {
				try {
					if (compareMinMaxValue(min, parsedValue) > 0) {
						// parsedValue less than minimum
						return min;
					}
				} catch (ClassCastException cce) {
					throw new ParseException("Class cast exception comparing values: " + cce, 0);
				}
			}
			
			// check maximum value
			Comparable max = getMaximum();
			if (max != null) {
				try {
					if (compareMinMaxValue(max, parsedValue) < 0) {
						// parsedValue greater than maximum
						return max;
					}
				} catch (ClassCastException cce) {
					throw new ParseException("Class cast exception comparing values: " + cce, 0);
				}
			}
			
			// Another cases are ParseException
			throw ex;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたオブジェクトを、<code>BigDecimal</code> に変換する。
	 */
	protected BigDecimal toBigDecimal(Object value) {
		if (value instanceof BigDecimal) {
			return ((BigDecimal)value);
		}
		else if (value instanceof BigInteger) {
			return new BigDecimal((BigInteger)value);
		}
		else if ((value instanceof Double) || (value instanceof Float)) {
			return new BigDecimal(((Number)value).doubleValue());
		}
		else {
			return new BigDecimal(value.toString());
		}
	}

	/**
	 * 指定された制限値(最小値もしくは最大値)と、指定された値を比較し、比較結果を返す。
	 * このメソッドは、変換可能な限り、制限値と比較する値を <code>BigDecimal</code> に変換してから比較する。
	 * @param valueMinMax	比較に使用する制限値(最小値もしくは最大値)
	 * @param value			比較する値
	 * @return	<code>valueMinMax</code> の数値が <code>value</code> より小さい場合は -1、等しい場合は 0、大きい場合は 1
	 * @throws ClassCastException	値の比較においてクラスが適切ではない場合
     */
	@SuppressWarnings("unchecked")
	protected int compareMinMaxValue(Comparable valueMinMax, Object value) throws ClassCastException
	{
		if ((valueMinMax instanceof Number) && (value instanceof Number)) {
			if ((valueMinMax instanceof BigDecimal)) {
				BigDecimal compValue = (BigDecimal)valueMinMax;
				return compValue.compareTo(toBigDecimal(value));
			}
			else if (valueMinMax instanceof Double) {
				BigDecimal compValue = new BigDecimal(((Double)valueMinMax).doubleValue());
				return compValue.compareTo(toBigDecimal(value));
			}
			else if (valueMinMax instanceof Float) {
				BigDecimal compValue = new BigDecimal(((Float)valueMinMax).doubleValue());
				return compValue.compareTo(toBigDecimal(value));
			}
			else if ((valueMinMax instanceof BigInteger) || (valueMinMax instanceof Long) ||
					  (valueMinMax instanceof Integer) || (valueMinMax instanceof Short) || (valueMinMax instanceof Byte))
			{
				BigDecimal compValue = new BigDecimal(valueMinMax.toString());
				return compValue.compareTo(toBigDecimal(value));
			}
			else {
				return valueMinMax.compareTo(value);
			}
		} else {
			return valueMinMax.compareTo(value);
		}
	}
}
