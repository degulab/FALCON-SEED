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
 * @(#)CacheManager.java	0.10	2008/08/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.util.internal;

import dtalge.DtBase;
import dtalge.DtBasePattern;
import dtalge.util.DtConditions;

/**
 * データ代数のキャッシュを管理するクラス。
 * <p>
 * 
 * @version 0.10	2008/08/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 *
 * @since 0.10
 */
public final class CacheManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final boolean USE_STRING_CANONICALIZED = true;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private CacheSet<String> stringCache = null;
	static private CacheSet<DtBase> dtbaseCache = null;
	static private CacheSet<DtBasePattern>	dtpatternCache = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private CacheManager() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された文字列のキャッシュ済みインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedStringEnabled()} が <tt>true</tt> を
	 * 返すとき、 {@link java.lang.String#equals(Object)} メソッドによって
	 *  <code>targetString</code> と等しいと判断される文字列がキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * <code>targetString</code> のインスタンスが返される。
	 * {@link dtalge.util.DtConditions#isCachedStringEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に <code>targetString</code> のインスタンスを返す。
	 * 
	 * @param targetString	キャッシュする文字列
	 * @return	キャッシュ済みインスタンス
	 */
	static public synchronized String cacheString(String targetString) {
		String cachedString = targetString;
		if (targetString != null) {
			// use Cached string
			if (DtConditions.isCachedStringEnabled()) {
				if (USE_STRING_CANONICALIZED) {
					// String#intern()
					cachedString = targetString.intern();
				} else {
					// normal cache
					cachedString = getStringCacheInstance().cache(cachedString);
				}
			}
		}
		return cachedString;
	}
	
	/**
	 * 指定されたデータ代数基底のキャッシュ済みインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>true</tt> を
	 * 返すとき、 {@link dtalge.DtBase#equals(Object)} メソッドによって
	 *  <code>targetBase</code> と等しいと判断される基底がキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * <code>targetBase</code> のインスタンスが返される。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に <code>targetBase</code> のインスタンスを返す。
	 * 
	 * @param targetBase	キャッシュする基底
	 * @return	キャッシュ済みインスタンス
	 */
	static public synchronized DtBase cacheDtBase(DtBase targetBase) {
		DtBase cachedBase = targetBase;
		if (targetBase != null) {
			if (DtConditions.isCachedBaseEnabled()) {
				cachedBase = getDtBaseCacheInstance().cache(cachedBase);
			}
		}
		return cachedBase;
	}
	
	/**
	 * 指定されたデータ代数基底パターンのキャッシュ済みインスタンスを返す。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>true</tt> を
	 * 返すとき、 {@link dtalge.DtBasePattern#equals(Object)} メソッドによって
	 *  <code>targetPattern</code> と等しいと判断される基底がキャッシュにすでに
	 * あった場合、キャッシュ内の該当するインスタンスが返される。そうでない場合は、
	 * <code>targetPattern</code> のインスタンスが返される。
	 * {@link dtalge.util.DtConditions#isCachedBaseEnabled()} が <tt>false</tt> を
	 * 返すとき、このメソッドは常に <code>targetPattern</code> のインスタンスを返す。
	 * 
	 * @param targetPattern	キャッシュする基底パターン
	 * @return	キャッシュ済みインスタンス
	 */
	static public synchronized DtBasePattern cacheDtBasePattern(DtBasePattern targetPattern) {
		DtBasePattern cachedPattern = targetPattern;
		if (targetPattern != null) {
			if (DtConditions.isCachedBaseEnabled()) {
				cachedPattern = getDtBasePatternCacheInstance().cache(cachedPattern);
			}
		}
		return cachedPattern;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private CacheSet<String> getStringCacheInstance() {
		if (stringCache == null) {
			stringCache = new ReferenceCacheSet<String>(ReferenceType.WEAK);
		}
		return stringCache;
	}
	
	static private CacheSet<DtBase> getDtBaseCacheInstance() {
		if (dtbaseCache == null) {
			dtbaseCache = new ReferenceCacheSet<DtBase>(ReferenceType.WEAK);
		}
		return dtbaseCache;
	}
	
	static private CacheSet<DtBasePattern> getDtBasePatternCacheInstance() {
		if (dtpatternCache == null) {
			dtpatternCache = new ReferenceCacheSet<DtBasePattern>(ReferenceType.WEAK);
		}
		return dtpatternCache;
	}
}
