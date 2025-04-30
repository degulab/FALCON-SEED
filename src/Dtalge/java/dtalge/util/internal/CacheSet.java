/*
 * @(#)CacheSet.java	0.10	2008/08/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.util.internal;

import java.util.Set;

/**
 * インスタンスのキャッシュを操作するインタフェース。
 * 
 * @version 0.10	2008/08/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.10
 */
public interface CacheSet<E> extends Set<E> {
	/**
	 * 指定されたオブジェクトの参照をキャッシュする。
	 * <p>
	 * 指定されたオブジェクトのインスタンスが、
	 * このオブジェクトの <code>equals(Object)</code> メソッドに
	 * よってキャッシュされたインスタンスと等しいと判断された場合は
	 * キャッシュ済みのインスタンスを返す。
	 * キャッシュに存在しないインスタンスの場合は、指定された
	 * インスタンスをキャッシュに追加し、そのインスタンスを返す。
	 * 
	 * @param o	キャッシュするオブジェクトへの参照
	 * @return	キャッシュ済みのインスタンス
	 */
	public E cache(E o);
}
