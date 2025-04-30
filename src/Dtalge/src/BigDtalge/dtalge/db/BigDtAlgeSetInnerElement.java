/*
 * @(#)BigDtAlgeSetInnerElement.java	0.5.0	2019/02/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db;

import dtalge.DtBase;

/**
 * 大容量のデータ代数要素の中のデータ代数元の一要素のインタフェース。
 * <p>このオブジェクトは不変である。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface BigDtAlgeSetInnerElement
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	/**
	 * この要素が所属するデータ代数元を識別するための ID を取得する。
	 * @return	データ代数元 ID
	 */
	public String getAlgeSetID();

	/**
	 * データ代数元の要素の基底を取得する。
	 * @return	データ代数基底オブジェクト
	 */
	public DtBase getBase();

	/**
	 * データ代数元の要素の値を取得する。
	 * @return	値
	 */
	public Object getValue();
}
