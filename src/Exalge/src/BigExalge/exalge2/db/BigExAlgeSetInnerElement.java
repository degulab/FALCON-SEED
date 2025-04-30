/*
 * @(#)BigExAlgeSetInnerElement.java	0.990	2018/11/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.db;

import java.math.BigDecimal;

import exalge2.ExBase;

/**
 * 大容量の交換代数要素の中の交換代数元の一要素のインタフェース。
 * <p>このオブジェクトは不変である。
 * 
 * @version 0.990
 * @since 0.990
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface BigExAlgeSetInnerElement
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * この要素が所属する交換代数元を識別するための ID を取得する。
	 * @return	交換代数元 ID
	 */
	public String getAlgeSetID();

	/**
	 * 交換代数元の要素の基底を取得する。
	 * @return	交換代数基底オブジェクト
	 */
	public ExBase getBase();

	/**
	 * 交換代数元の要素の値を取得する。
	 * @return	値
	 */
	public BigDecimal getValue();
}
