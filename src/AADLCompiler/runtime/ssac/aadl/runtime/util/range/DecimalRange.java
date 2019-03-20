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
 * @(#)DecimalRange.java	1.70	2011/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.util.range;

import java.math.BigDecimal;

/**
 * <code>BigDecimal</code> オブジェクトを要素とする数値範囲のインタフェースです。
 * このインタフェースは範囲の先頭と終端によって定義され、
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
public interface DecimalRange extends Range<BigDecimal>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
}
