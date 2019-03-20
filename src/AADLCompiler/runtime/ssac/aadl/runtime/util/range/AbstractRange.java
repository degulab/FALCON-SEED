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
 * @(#)AbstractRange.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range;

import java.math.BigDecimal;


/**
 * このクラスは、<code>Range</code> インタフェースのスケルトン実装を提供し、
 * このインタフェースを実装するのに必要な作業量を軽減します。
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
public abstract class AbstractRange<E> implements Range<E>
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
	 * 唯一のコンストラクタです。
	 * サブクラスのコンストラクタによる呼び出し用で、通常は暗黙的に呼び出されます。
	 */
	protected AbstractRange() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 数値範囲のハッシュコード値を返します。
	 * <code>Object.hashCode</code> の一般規約によって要求されるように、任意の 2 つの数値範囲 <code>r1</code> と <code>r2</code> で、
	 * <code>r1.equals(r2)</code> であれば、<code>r1.hashCode()==r2.hashCode()</code> となることが保証されます。
	 * @return	ハッシュコード値
	 */
	@Override
	public int hashCode() {
		Object val;
		
		//--- isIncludeRangeLast
		int h = isIncludeRangeLast() ? 1231 : 1237;
		//--- rangeFirst
		val = rangeFirst();
		if (val instanceof BigDecimal) {
			h = 31 * h + ((BigDecimal)val).stripTrailingZeros().hashCode();
		} else {
			h = 31 * h + (val==null ? 0 : val.hashCode());
		}
		//--- rangeLast
		val = rangeLast();
		if (val instanceof BigDecimal) {
			h = 31 * h + ((BigDecimal)val).stripTrailingZeros().hashCode();
		} else {
			h = 31 * h + (val==null ? 0 : val.hashCode());
		}
		//--- rangeStep
		val = rangeStep();
		if (val instanceof BigDecimal) {
			h = 31 * h + ((BigDecimal)val).stripTrailingZeros().hashCode();
		} else {
			h = 31 * h + (val==null ? 0 : val.hashCode());
		}
		
		return h;
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
	protected abstract String valueToString(E value);

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
