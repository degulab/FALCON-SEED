/*
 * @(#)BigDtStringThesaurusElement.java	0.5.0	2019/02/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.db;

/**
 * 大容量のシソーラス定義の中の一つのシソーラスペアを表す要素のインタフェース。
 * <p>このオブジェクトは不変である。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface BigDtStringThesaurusElement
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	/**
	 * シソーラス定義の親子関係の親語句を返す。
	 * @return	親子関係の親語句
	 */
	public String getParentWord();
	
	/**
	 * シソーラス定義の親子関係の子語句を返す。
	 * @return	親子関係の子語句
	 */
	public String getChildWord();
}
