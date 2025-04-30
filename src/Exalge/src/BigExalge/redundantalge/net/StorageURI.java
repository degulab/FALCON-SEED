/*
 * @(#)StorageURI.java	0.991	2019/02/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.net;

/**
 * 代数オブジェクトのストレージの場所に関するインタフェース。
 * <p>このオブジェクトの実装は、基本的に不変オブジェクトとする。
 * 
 * @version 0.991
 * @since 0.991
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface StorageURI
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	/**
	 * このオブジェクトの設定されているスキーマを返す。
	 * @return	スキーマを表す文字列
	 */
	public String getScheme();

	/**
	 * このオブジェクトのハッシュ値を返す。
	 * @return	ハッシュ値
	 */
	public int hashCode();

	/**
	 * 指定されたオブジェクトと自身の内容が等しいかを判定する。
	 * @param obj	判定するオブジェクト
	 * @return	等しい場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean equals(Object obj);

	/**
	 * このオブジェクトが表す URI 文字列を返す。
	 * @return	このオブジェクトが表す URI 文字列
	 */
	public String toString();
}
