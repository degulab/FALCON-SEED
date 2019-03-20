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
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)TransDivideRatios.java	0.970	2009/03/10
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransDivideRatios.java	0.960	2009/02/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransDivideRatios.java	0.95	2008/12/07
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransDivideRatios.java	0.95	2008/10/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransDivideRatios.java	0.93	2007/09/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransDivideRatios.java	0.91	2007/08/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)TransDivideRatios.java	0.90	2007/07/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 按分比率テーブル(按分先の基底と比率)を保持するクラス。
 * <p>
 * 按分先基底(<code>{@link ExBasePattern}</code>)と按分比率のマップにより構成される。
 * <p>
 * 按分比率においては、任意の実数(整数または小数)を指定できる。
 * この値は、按分変換{@link #divideTransfer(ExBase, BigDecimal, boolean) divideTransfer()}
 * において、按分比率テーブル内の「比率合計値が 1 ではない」とする計算方法と、
 * 「比率合計値が 1 である」とする計算方法を選択できる。
 * <p>
 * このクラスの Map の実装は、挿入型の {@link java.util.LinkedHashMap <code>LinkedHashMap</code>} である。
 * したがって、{@link #put(ExBasePattern, BigDecimal) <code>put</code>} メソッドにおいて、
 * クラス内でのデータの格納順序は基本的に維持される。
 * 
 * @version 0.970	2009/03/10
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * 
 * @since 0.90
 */
public class TransDivideRatios implements Cloneable
{
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 按分比率と基底のマップ
	 */
	private LinkedHashMap<ExBasePattern,BigDecimal> ratios;
	/**
	 * 全要素の按分比率の合計値
	 */
	private BigDecimal totalRetio;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 要素を持たない <code>TransDivideRatios</code> の新しいインスタンスを生成する。
	 *
	 */
	public TransDivideRatios() {
		this.ratios = new LinkedHashMap<ExBasePattern,BigDecimal>();
		this.totalRetio = BigDecimal.ZERO;
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	/**
	 * 按分比率テーブルの要素が空であることを示す。
	 * 
	 * @return 要素が空なら true を返す。
	 */
	public boolean isEmpty() {
		return this.ratios.isEmpty();
	}

	/**
	 * 按分比率テーブルの要素数を返す。
	 * 
	 * @return 要素数
	 */
	public int size() {
		return this.ratios.size();
	}

	/**
	 * 指定の基底パターンが要素に含まれているかを検証する。
	 * 
	 * @param pattern 検証対象の基底パターン
	 * @return 要素に含まれていれば true を返す。
	 */
	public boolean containsPattern(ExBasePattern pattern) {
		return this.ratios.containsKey(pattern);
	}

	/**
	 * 按分比率テーブルに含まれる基底パターンの集合を取得する。
	 * 
	 * @return 基底パターンの集合を返す。
	 */
	public Set<ExBasePattern> patterns() {
		return this.ratios.keySet();
	}

	/**
	 * 指定の基底パターンに対応する按分比率を取得する。
	 * 
	 * @param pattern 検索する基底パターン
	 * @return 基底パターンに対応する按分比率を返す。存在しない場合は <tt>null</tt> を返す。
	 */
	public BigDecimal getRatio(ExBasePattern pattern) {
		return this.ratios.get(pattern);
	}

	/**
	 * 全要素の按分比率の合計値を返す。
	 * 
	 * @return 全要素の按分比率合計値
	 */
	public BigDecimal getTotalRatio() {
		return this.totalRetio;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 按分比率テーブルの全要素を削除する。
	 * 
	 */
	public void clear() {
		this.ratios.clear();
		this.totalRetio = BigDecimal.ZERO;
	}

	/**
	 * 現在の要素で、按分比率合計値を更新する。
	 *
	 */
	public void updateTotalRatio() {
		BigDecimal total = BigDecimal.ZERO;
		Iterator<BigDecimal> it = this.ratios.values().iterator();
		while (it.hasNext()) {
			BigDecimal ratio = it.next();
			total = total.add(ratio);
		}
		this.totalRetio = total;
	}

	/**
	 * 按分比率テーブルに、基底パターンと按分比率のペアを登録する。
	 * <br>
	 * 基底パターンがすでに登録されている場合、新しい按分比率が上書きされる。
	 * <p>
	 * このメソッドでは、按分比率合計値は更新されない。
	 * 按分比率合計値の更新は、{@link #updateTotalRatio()} を呼び出すこと。
	 * 
	 * @param pattern 基底パターン
	 * @param ratio 按分比率
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	public void put(ExBasePattern pattern, BigDecimal ratio) {
		// Check
		if (pattern == null)
			throw new NullPointerException("pattern is null");
		if (ratio == null)
			throw new NullPointerException("ratio is null");
		
		// put
		this.ratios.put(pattern, ratio);
	}

	/**
	 * 按分比率テーブルから、指定の基底パターンと一致するペアを削除する。
	 * <p>
	 * このメソッドでは、按分比率合計値は更新されない。
	 * 按分比率合計値の更新は、{@link #updateTotalRatio()} を呼び出すこと。
	 * 
	 * @param pattern 削除する基底パターン
	 */
	public void remove(ExBasePattern pattern) {
		this.ratios.remove(pattern);
	}

	/**
	 * 指定された値を各要素の按分比率で分配した交換代数を算出する。
	 * <p>
	 * このメソッドでは、<code>useTotalRatio</code> が <code>true</code> の場合は、
	 * 「按分比率合計値が 1 ではない」場合の計算を実行する。つまり、
	 * 次の計算により按分後の値を算出する。
	 * <blockquote>
	 * 按分値i ＝ 指定値 ÷ 按分比率合計値 × 按分比率i
	 * </blockquote>
	 * また、<code>useTotalRatio</code> が <code> false </code> の場合は、
	 * 「按分比率合計値が 1 である」場合の計算を実行する。つまり、
	 * 次の計算により按分後の値を算出する。
	 * <blockquote>
	 * 按分値i ＝ 指定値 × 按分比率i
	 * </blockquote>
	 * <p>
	 * 除算においては、{@link MathContext#DECIMAL128} の精度で結果を保持する。
	 * そのため、1 つの値を 3 当分するような按分変換の場合、誤差が発生する。
	 * <br>
	 * また、按分比率による演算結果が 0 となった場合でも、その基底は結果に保存される。
	 * 
	 * @param srcBase 分配する元となる値の基底
	 * @param srcValue 分配する元となる値
	 * @param useTotalRatio 按分比率合計値から按分比率を算出する場合は <tt>true</tt>、
	 * 						 指定の按分比率をそのまま使用する場合は <tt>false</tt> を指定する。
	 * 
	 * @return 按分後の交換代数の元
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合にスローされる
	 * @throws IllegalStateException	按分先基底が存在しない場合にスローされる
	 * @throws ArithmeticException		按分計算において 0 除算が発生した場合にスローされる
	 */
	public Exalge divideTransfer(ExBase srcBase, BigDecimal srcValue, boolean useTotalRatio) {
		// Check
		if (srcBase == null)
			throw new NullPointerException("srcBase is null");
		if (srcValue == null)
			throw new NullPointerException("srcValue is null");
		if (ratios.isEmpty())
			throw new IllegalStateException("ExBasePattern is not exist translation ahead.");
		
		// divide transfer
		Exalge newAlge = new Exalge();
		if (useTotalRatio) {
			// 按分比率合計値から比率を算出
			
			BigDecimal unitValue;
			try {
				unitValue = srcValue.divide(this.totalRetio, MathContext.DECIMAL128);
			}
			catch (ArithmeticException ex) {
				throw new ArithmeticException(
						String.format("Failed to divide srcValue(%s%s) by divisor(%s).",
								srcValue.stripTrailingZeros().toPlainString(), srcBase.toString(), this.totalRetio.stripTrailingZeros().toPlainString()));
			}
			Iterator<ExBasePattern> it = this.ratios.keySet().iterator();
			while (it.hasNext()) {
				ExBasePattern pattern = it.next();
				BigDecimal ratio = this.ratios.get(pattern);
				BigDecimal dstValue = unitValue.multiply(ratio);
				//--- 按分結果が 0 でも、結果に含める
				ExBase dstBase = pattern.translate(srcBase);
				newAlge.putValue(dstBase, dstValue);
			}
		}
		else {
			// 比率をそのまま使用
			Iterator<ExBasePattern> it = this.ratios.keySet().iterator();
			while (it.hasNext()) {
				ExBasePattern pattern = it.next();
				BigDecimal ratio = this.ratios.get(pattern);
				BigDecimal dstValue = srcValue.multiply(ratio);
				//--- 按分結果が 0 でも、結果に含める
				ExBase dstBase = pattern.translate(srcBase);
				newAlge.putValue(dstBase, dstValue);
			}
		}
		
		return newAlge;
	}

	//------------------------------------------------------------
	// Implement Object interfaces
	//------------------------------------------------------------

	/**
	 * <code>TransDivideRatios</code> インスタンスの新しいシャローコピーを返す。
	 * このオブジェクトに格納されている <code>ExBasePattern</code> インスタンスと
	 * <code>BigDecimal</code> インスタンスについては、それ自体は複製されない。
	 * 
	 * @return このオブジェクトのコピー
	 * @since 0.970
	 */
	@Override
	@SuppressWarnings("unchecked")
	public TransDivideRatios clone() {
		try {
			TransDivideRatios dup = (TransDivideRatios)super.clone();
			dup.ratios = (LinkedHashMap<ExBasePattern,BigDecimal>)this.ratios.clone();
			return dup;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * このオブジェクトのハッシュコード値を返す。
	 * このハッシュコード値は、任意の 2 つのオブジェクト <code>t1</code> と <code>t2</code> に
	 * ついて、<code>t1.equals(t2)</code> の場合 <code>t1.hashCode()==t2.hashCode()</code> になる。
	 * 
	 * @return ハッシュコード値
	 * @since 0.970
	 */
	@Override
	public int hashCode() {
		return ((totalRetio==null ? 0 : totalRetio.hashCode()) + ratios.hashCode());
	}

	/**
	 * 指定されたオブジェクトとこのオブジェクトが等しいかを検証する。
	 * <code>TransDivideRatios</code> では、2 つの検証するオブジェクトにおいて、
	 * 全ての按分先基底パターンが同値であり、同一按分先基底パターンに対応する
	 * 按分比率が数値的に等く、按分比率合計値も数値的に等しい場合に同値とみなす。
	 * 
	 * @param obj	このオブジェクトと比較するオブジェクト
	 * @return	指定されたオブジェクトがこのオブジェクトと等しい場合に <tt>true</tt>
	 * 
	 * @since 0.970
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			// same instance
			return true;
		}
		
		if (!(obj instanceof TransDivideRatios)) {
			// not equals class
			return false;
		}
		
		TransDivideRatios aRatios = (TransDivideRatios)obj;
		if (aRatios.totalRetio==this.totalRetio ||
			(aRatios.totalRetio!=null && this.totalRetio!=null &&
			aRatios.totalRetio.compareTo(this.totalRetio)==0))
		{
			if (aRatios.ratios.size() != this.ratios.size()) {
				// not equals number of ratios elements
				return false;
			}
			
			try {
				for (Map.Entry<ExBasePattern, BigDecimal> entry : aRatios.ratios.entrySet()) {
					ExBasePattern key = entry.getKey();
					BigDecimal value = entry.getValue();
					if (value == null) {
						if (!(ratios.get(key)==null && ratios.containsKey(key))) {
							// not exist same key & value
							return false;
						}
					} else {
						if (value.compareTo(ratios.get(key)) != 0) {
							// not equals value
							return false;
						}
					}
				}
			}
			catch (ClassCastException ignoreEx) {
				return false;
			}
			catch (NullPointerException ignoreEx) {
				return false;
			}
		}
		else {
			// not equal totalRetio
			return false;
		}
		
		// equals objects
		return true;
	}

	/**
	 * このオブジェクトの文字列表現を返す。
	 * <br>文字列表現は、次のような形式となる。
	 * <blockquote>
	 * ( <i>按分比率合計値</i> )[ ( <i>按分比率</i> ) <i>振替先基底パターン</i> , ( <i>按分比率</i> ) <i>振替先基底パターン</i> , ... ]
	 * </blockquote>
	 * 
	 * @return このオブジェクトの文字列表現
	 * @since 0.970
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//--- total ratio
		sb.append('(');
		sb.append(totalRetio==null ? "null" : totalRetio.stripTrailingZeros().toPlainString());
		sb.append(')');
		//--- ratios
		sb.append('[');
		if (!ratios.isEmpty()) {
			Iterator<Map.Entry<ExBasePattern, BigDecimal>> it = ratios.entrySet().iterator();
			Map.Entry<ExBasePattern, BigDecimal> entry = it.next();
			sb.append('(');
			sb.append(entry.getValue().stripTrailingZeros().toPlainString());
			sb.append(')');
			sb.append(entry.getKey());
			while (it.hasNext()) {
				entry = it.next();
				sb.append(",(");
				sb.append(entry.getValue().stripTrailingZeros().toPlainString());
				sb.append(')');
				sb.append(entry.getKey());
			}
		}
		sb.append(']');
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods for I/O
	//------------------------------------------------------------

	/**
	 * 任意の按分比率合計値を設定する。
	 * このメソッドでは、数値に対するエラーチェックは行わない。
	 * 
	 * @throws NullPointerException 引数が <tt>null</tt> の場合
	 */
	protected void setTotalRatio(BigDecimal newTotalRatio) {
		if (newTotalRatio == null)
			throw new NullPointerException();
		this.totalRetio = newTotalRatio;
	}
}
