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
 *  Copyright 2007-2011  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)DtConditions.java	0.30	2011/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtConditions.java	0.20	2010/02/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtConditions.java	0.10	2008/08/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.util;

import static dtalge.util.Strings.isNullOrEmpty;

/**
 * データ代数の挙動を制御するクラス。
 * <p>
 * このクラスにより、データ代数の各クラスの次のような挙動を制御できる。
 * <ul>
 * <li>基底に使用する文字列のキャッシュ
 * <li>データ代数基底(<code>{@link dtalge.DtBase}</code>)のキャッシュ
 * <li>データ代数基底パターン(<code>{@link dtalge.DtBasePattern}}</code>)のキャッシュ
 * </ul>
 * <p>
 * 基底に使用する文字列のキャッシュが有効な場合、基底キーに格納する文字列を
 * キャッシュし、同じ文字列(文字列が同値である場合)であれば同じインスタンスを
 * 返すようになる。
 * また、基底のキャッシュが有効な場合、データ代数や基底集合などで使用する
 * インスタンスをキャッシュし、同値となる基底であれば同じインスタンスを返す
 * ようになる。
 * <p>
 * キャッシュに関する動作は、データ代数基底(<code>{@link dtalge.DtBase}</code>)、
 * データ代数基底パターン(<code>{@link dtalge.DtBasePattern}}</code>)、データ
 * 代数元(<code>{@link dtalge.Dtalge}</code>)で実装される。
 * <p>
 * <b>注:</b>
 * 現バージョンでは、キャッシュ機能は<b>有効</b>となっている。
 * 
 * @version 0.30	2011/03/16
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public final class DtConditions
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String ID_STRING_CACHED = "dtalge.string.cached";
	static private final String ID_BASE_CACHED = "dtalge.base.cached";
	
	static private final boolean DEFAULT_STRING_CACHED = true;
	static private final boolean DEFAULT_BASE_CACHED = true;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private DtConditions() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 全てのキャッシュを無効にする。
	 */
	static public void setDefaults() {
		clearProperty(ID_STRING_CACHED);
		clearProperty(ID_BASE_CACHED);
	}
	
	//--- string.cached

	/**
	 * 基底キー文字列のキャッシュが有効かを返す。
	 * 
	 * @return キャッシュが有効であれば <tt>true</tt> を返す。
	 */
	static public boolean isCachedStringEnabled() {
		return getBooleanProperty(ID_STRING_CACHED, DEFAULT_STRING_CACHED);
	}

	/**
	 * 基底キー文字列キャッシュの状態を設定する。
	 * 
	 * @param toEnable	キャッシュを有効にする場合は <tt>true</tt> を指定する。
	 */
	static public void setEnableCachedString(boolean toEnable) {
		setBooleanProperty(ID_STRING_CACHED, toEnable);
	}
	
	//--- base.cached

	/**
	 * データ代数基底のキャッシュが有効かを返す。
	 * @return キャッシュが有効であれば <tt>true</tt> を返す。
	 */
	static public boolean isCachedBaseEnabled() {
		return getBooleanProperty(ID_BASE_CACHED, DEFAULT_BASE_CACHED);
	}

	/**
	 * データ代数基底キャッシュの状態を設定する。
	 * 
	 * @param toEnable	キャッシュを有効にする場合は <tt>true</tt> を指定する。
	 */
	static public void setEnableCachedBase(boolean toEnable) {
		setBooleanProperty(ID_BASE_CACHED, toEnable);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 真偽値を格納するプロパティを取得する。
	 * 
	 * @param name	プロパティ名
	 * @param defaultValue デフォルトの値
	 */
	static private boolean getBooleanProperty(String name, boolean defaultValue) {
		String strValue = getProperty(name);
		boolean retValue = defaultValue;
		if (!isNullOrEmpty(strValue)) {
			retValue = Boolean.valueOf(strValue);
		}
		return retValue;
	}

	/**
	 * 真偽値を格納するプロパティを設定する。
	 * 
	 * @param name	プロパティ名
	 * @param value	設定する値
	 */
	static private void setBooleanProperty(String name, boolean value) {
		setProperty(name, String.valueOf(value));
	}

	/**
	 * プロパティを取得する。
	 * 
	 * @param name	プロパティ名
	 * @return	プロパティの値(文字列)
	 */
	static private String getProperty(String name) {
		return System.getProperty(name);
	}

	/**
	 * プロパティを設定する。
	 * 
	 * @param name	プロパティ名
	 * @param value	設定する値(文字列)
	 */
	static private void setProperty(String name, String value) {
		System.setProperty(name, value);
	}

	/**
	 * プロパティを消去する。
	 * 
	 * @param name	プロパティ名
	 */
	static private void clearProperty(String name) {
		System.clearProperty(name);
	}
}
