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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)TextEncodingSettings.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.setting;

import ssac.util.Strings;
import ssac.util.properties.ExProperties;

/**
 * テキストファイルのエンコーディング設定を保持するクラス。
 * エンコーディング設定の他、テキストファイル保存時のエンコーディングも
 * 保持する。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class TextEncodingSettings
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String DEFAULT_PREFIX = "aadl.file.encoding";
	static protected final String SUFFIX_LAST_ENCODING    = ".last";
	static protected final String SUFFIX_ENCODING_SPECIFY = ".specify";
	static protected final String SUFFIX_ENCODING_NAME    = ".name";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このエンコーディング設定を保持するプロパティ **/
	private final ExProperties		_props;
	private final String	prefixForKeys;
	private final String	keyLastEncoding;
	private final String	keyEncodingSpecify;
	private final String	keyEncodingName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public TextEncodingSettings(ExProperties properties) {
		this(properties, null);
	}
	
	public TextEncodingSettings(ExProperties properties, String prefix) {
		if (properties==null)
			throw new IllegalArgumentException("'properties' argument is null.");
		this._props = properties;
		this.prefixForKeys = (Strings.isNullOrEmpty(prefix) ? DEFAULT_PREFIX : prefix);
		this.keyLastEncoding    = this.prefixForKeys + SUFFIX_LAST_ENCODING;
		this.keyEncodingSpecify = this.prefixForKeys + SUFFIX_ENCODING_SPECIFY;
		this.keyEncodingName    = this.prefixForKeys + SUFFIX_ENCODING_NAME;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 現在設定されているエンコーディング名を返す。
	 * このメソッドは、次のような手順でエンコーディング名を返す。
	 * <ol>
	 * <li>エンコーディング名が設定されていれば、その名前を返す。
	 * <li><em>ignoreLastEncoding</em> が <tt>false</tt> で、
	 * 最後にテキストファイル保存を行ったときのエンコーディング名があれば
	 * その名前を返す。
	 * <li><em>defaultName</em> を返す。
	 * </ol>
	 * @param defaultName	設定が存在しない場合のデフォルトエンコーディング名を指定する。
	 * @param ignoreLastEncoding	最後にテキストファイル保存を行ったときのエンコーディング名を
	 * 								無視して <em>defaultName</em> を返す場合は <tt>true</tt> を指定する。
	 * @return	取得したエンコーディング名を返す。
	 */
	public String getTargetEncodingName(String defaultName, boolean ignoreLastEncoding) {
		String strName = getEncodingName();
		if (isSpecifiedEncoding() && !Strings.isNullOrEmpty(strName)) {
			// 設定が存在する
			return strName;
		}
		else if (!ignoreLastEncoding) {
			strName = getLastEncodingName();
			if (!Strings.isNullOrEmpty(strName)) {
				// 最後の保存時のエンコーディング名が存在する
				return strName;
			}
		}
		
		// 設定が存在しないので、指定された標準エンコーディング名を返す
		return defaultName;
	}
	
	//--- KEY_LAST_ENCODING
	
	public String getLastEncodingName() {
		return _props.getString(keyLastEncoding, null);
	}
	
	public void setLastEncodingName(String name) {
		if (Strings.isNullOrEmpty(name)) {
			_props.clearProperty(keyLastEncoding);
		} else {
			_props.setString(keyLastEncoding, name);
		}
	}
	
	//--- KEY_ENCODING_SPECIFY
	
	public boolean isSpecifiedEncoding() {
		return _props.getBooleanValue(keyEncodingSpecify);
	}
	
	public void setEncodingSpecified(boolean toSpecify) {
		_props.setBooleanValue(keyEncodingSpecify, toSpecify);
	}
	
	//--- KEY_ENCODING_NAME
	
	public String getEncodingName() {
		return _props.getString(keyEncodingName, null);
	}
	
	public void setEncodingName(String name) {
		if (Strings.isNullOrEmpty(name)) {
			_props.clearProperty(keyEncodingName);
		} else {
			_props.setString(keyEncodingName, name);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
