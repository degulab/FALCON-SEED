/*
 * @(#)IDtStringThesaurus.java	0.5.0	2019/02/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge;

/**
 * シソーラス定義に含まれる順序で文字列を比較するインタフェース。
 * 
 * @version 0.5.0
 * @since 0.5.0
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface IDtStringThesaurus
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * シソーラス定義としての順序が規定されていない場合に <tt>true</tt> を返す。
	 * @return	順序が規定されていない場合は <tt>true</tt>
	 */
	public boolean isEmpty();

	/**
	 * 指定された語句がシソーラスとしての順序を持つかどうかを判定する。
	 * 
	 * @param word	判定する語句
	 * @return	関係が定義されている語句であれば <tt>true</tt>
	 */
	public boolean contains(String word);

	/**
	 * 指定された 2 つの語句が比較可能(関係を持つ)であれば <tt>true</tt> を返す。
	 * なお、2 つの引数が同値の場合、<tt>false</tt> を返す。
	 * 
	 * @param word1		検証する語句
	 * @param word2		検証する語句のもう一方
	 * @return	比較可能であれば <tt>true</tt>
	 */
	public boolean isComparable(String word1, String word2);

	/**
	 * 指定された 2 つの語句をシソーラス定義に基づき比較する。
	 * <code>word1</code> が <code>word2</code> の子孫にあたる(<code>word1</code> &lt; <code>word2</code>)場合は負の値を返す。
	 * <code>word1</code> が <code>word2</code> の祖先にあたる(<code>word1</code> &gt; <code>word2</code>)場合は正の値を返す。
	 * 上記以外の場合は 0 を返す。<br>
	 * なお、2 つの語句のどちらかが <tt>null</tt> もしくは同値の場合も 0 を返す。
	 * 
	 * @param word1		比較する語句
	 * @param word2		比較する語句のもう一方
	 * @return	比較結果を返す。
	 */
	public int compare(String word1, String word2);
}
