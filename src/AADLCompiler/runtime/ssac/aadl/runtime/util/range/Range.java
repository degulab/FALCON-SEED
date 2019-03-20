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
 * @(#)Range.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range;


/**
 * 数値範囲のインタフェースです。このインタフェースは範囲の先頭と終端によって定義され、
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
public interface Range<E> extends Iterable<E>
{
	/**
	 * 定義されている数値範囲が無効である場合に <tt>true</tt> を返します。
	 * @return	数値範囲が無効であれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isEmpty();

	/**
	 * 数値範囲内の値に、範囲終端の値を含むよう指定されている場合に <tt>true</tt> を返します。
	 * 範囲の指定によっては、このメソッドが <tt>true</tt> を返す場合でも、範囲終端が含まれない場合があります。
	 * @return	範囲終端の値を含むよう指定されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isIncludeRangeLast();

	/**
	 * 数値範囲が増加方向か減少方向かを返します。
	 * @return	増加方向の数値範囲の場合は <tt>true</tt>、減少方向の数値範囲の場合は <tt>false</tt>
	 */
	public boolean isIncremental();

	/**
	 * 指定された値が、数値範囲内の要素と等しいかどうかを判定します。
	 * ただし、数値範囲が無効である場合、このメソッドは常に <tt>false</tt> を返します。
	 * @param value	判定する値
	 * @return	数値範囲内の要素と等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * 			数値範囲が無効の場合は <tt>false</tt> を返す。
	 */
	public boolean containsValue(Object value);

	/**
	 * 範囲先頭として設定された値を返します。
	 * @return	範囲先頭
	 */
	public E rangeFirst();

	/**
	 * 範囲終端として設定された値を返します。
	 * @return	範囲終端
	 */
	public E rangeLast();

	/**
	 * 数値間隔として設定された値を返します。
	 * @return	数値間隔
	 */
	public E rangeStep();

	/**
	 * 数値範囲の要素の中で最小の値を返します。
	 * ただし、数値範囲が無効である場合は <tt>null</tt> を返します。
	 * @return	数値範囲の要素の最小値を返す。
	 * 			数値範囲が無効の場合は <tt>null</tt> を返す。
	 */
	public E rangeMin();

	/**
	 * 数値範囲の要素の中で最大の値を返します。
	 * ただし、数値範囲が無効である場合は <tt>null</tt> を返します。
	 * @return	数値範囲の要素の最大値を返す。
	 * 			数値範囲が無効の場合は <tt>null</tt> を返す。
	 */
	public E rangeMax();

	/**
	 * 数値範囲のハッシュコード値を返します。
	 * <code>Object.hashCode</code> の一般規約によって要求されるように、任意の 2 つの数値範囲 <code>r1</code> と <code>r2</code> で、
	 * <code>r1.equals(r2)</code> であれば、<code>r1.hashCode()==r2.hashCode()</code> となることが保証されます。
	 * @return	ハッシュコード値
	 */
	public int hashCode();

	/**
	 * 指定されたオブジェクトが、この数値範囲と等しいかどうかを比較します。
	 * 指定されたオブジェクトも数値範囲であり、数値範囲の定義が同じ場合に <tt>true</tt> を返します。
	 * 
	 * @param o	この数値範囲と等しいかどうかが比較される <code>Object</code> 
	 * @return	指定されたオブジェクトが、この数値範囲と等しい場合は <tt>true</tt>
	 */
	public boolean equals(Object o);
}
