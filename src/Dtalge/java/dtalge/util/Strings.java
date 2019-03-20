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
 * @(#)Strings.java	0.10	2008/07/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.util;

/**
 * 文字列操作ユーティリティ。
 * 
 * @version 0.10	2008/07/16
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.10
 */
public final class Strings
{
	private Strings() {}
	
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 引数に指定された文字列が <tt>null</tt> もしくは、長さ 0 の
	 * 文字列なら <tt>true</tt> を返す。
	 * 
	 * @param value	検査する文字列
	 * @return		<tt>null</tt> もしくは、長さ 0 の文字列なら <tt>true</tt>
	 */
	static public boolean isNullOrEmpty(String value) {
		return (value == null || value.length() <= 0);
	}

	/**
	 * target 文字列内に、指定された文字のどれか一つでも含まれている場合に
	 * <tt>true</tt> を返す。
	 * <p>
	 * なお、target 引数が <tt>null</tt> もしくは、長さ 0 の場合は、常に <tt>false</tt> を返す。
	 * 
	 * @param target	検証する文字列
	 * @param cs		文字(群)
	 * @return	指定された文字のどれか一つが含まれている場合は <tt>true</tt>
	 */
	static public boolean contains(final String target, char...cs) {
		boolean exist = false;
		int len;
		if (target != null && (len = target.length()) > 0) {
			for (int i = 0; i < len; i++) {
				char tc = target.charAt(i);
				for (char cc : cs) {
					if (cc == tc) {
						return true;
					}
				}
			}
		}
		return exist;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
