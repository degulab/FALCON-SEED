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
 *  Copyright 2007-2010  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
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
