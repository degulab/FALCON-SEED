/*
 * @(#)ReferenceType.java	0.10	2008/08/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.util.internal;

/**
 * 参照マップに格納するキーの種類を表す列挙型。
 * 
 * @version 0.10	2008/08/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.10
 */
public enum ReferenceType
{
	/**
	 * 強参照であることを表す列挙値。
	 */
	STRONG,

	/**
	 * ソフト参照であることを表す列挙値。
	 */
	SOFT,

	/**
	 * 弱参照であることを表す列挙値。
	 */
	WEAK;
}
