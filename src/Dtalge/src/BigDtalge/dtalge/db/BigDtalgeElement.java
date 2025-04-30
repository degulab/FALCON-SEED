/*
 * @(#)BigDtalgeElement.java	0.5.0	2019/02/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db;

import dtalge.DtBase;

/**
 * 大容量のデータ代数元の一要素のインタフェース。
 * <p>このオブジェクトは不変である。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface BigDtalgeElement
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * データ代数元の要素の基底を取得する。
	 * @return	交換代数基底オブジェクト
	 */
	public DtBase getBase();

	/**
	 * データ代数元の要素の値を取得する。
	 * @return	値
	 */
	public Object getValue();
}
