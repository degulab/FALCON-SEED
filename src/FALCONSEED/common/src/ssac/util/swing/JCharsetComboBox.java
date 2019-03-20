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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)JCharsetComboBox.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComboBox;

import ssac.util.Strings;

/**
 * 文字セット名で文字セットを選択するコンボボックス・コンポーネント。
 * 基本的に、プラットフォームで利用可能な文字セットを選択可能とする。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public class JCharsetComboBox extends JComboBox
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------

	/** このコンボボックスで選択可能な文字セット候補 **/
	static private final String[] DefaultPresetCharsetNames = new String[]{
		"Shift_JIS",
		"EUC-JP",
		"MS932",
		"UTF-8",
		"UTF-16",
		"UTF-16BE",
		"UTF-16LE",
		"US-ASCII",
	};
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このコンポーネントのデフォルト文字セット **/
	static private Charset				_defCharset;
	/** このコンポーネントで選択可能な文字セットと文字セット名のマップ **/
	static private Map<Charset,String>	_mapCharsetNames;
	
	static {
		updateCandidateCharsetNames(DefaultPresetCharsetNames);
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JCharsetComboBox() {
		super(_mapCharsetNames.values().toArray(new String[0]));
		this.setSelectedItem(_mapCharsetNames.get(_defCharset));
	}

	//------------------------------------------------------------
	// Static Public interfaces
	//------------------------------------------------------------

	/**
	 * このコンポーネントで予め指定されている文字セット名の配列を返す。
	 * @return	プリセットされている文字セット名の新しい配列
	 */
	static public String[] getPresetCandidateCharsetNames() {
		return DefaultPresetCharsetNames.clone();
	}

	/**
	 * このコンポーネントで予め指定されている文字セット名を
	 * 選択候補として設定する。
	 */
	static public void setupPresetCandidateCharsetNames() {
		updateCandidateCharsetNames(DefaultPresetCharsetNames);
	}

	/**
	 * このコンポーネントで選択可能とする文字セット候補を設定する。
	 * ここで指定された文字セット名から、現在のプラットフォームで利用可能な
	 * 文字セットのみ選択可能となる。また、指定された複数の名前が同じ
	 * 文字セットを示す場合、最後に指定された文字セット名が優先される。
	 * @param names	選択候補とする文字セット名の配列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public void setupCandidateCharsetNames(String...names) {
		updateCandidateCharsetNames(names);
	}

	/**
	 * このコンポーネントのデフォルト文字セットを返す。
	 * @return	デフォルト文字セット
	 */
	static public Charset getDefaultCharset() {
		return _defCharset;
	}

	/**
	 * このコンポーネントのデフォルト文字セット名を返す。
	 * @return	デフォルト文字セット名
	 */
	static public String getDefaultCharsetName() {
		return _mapCharsetNames.get(_defCharset);
	}

	/**
	 * このコンポーネントで選択可能な文字セット候補の総数を返す。
	 * @return	文字セット候補の総数
	 */
	static public int getNumAvailableCharsets() {
		return _mapCharsetNames.size();
	}

	/**
	 * このコンポーネントで選択可能な文字セットの配列を返す。
	 * @return	文字セットの配列
	 */
	static public Charset[] getAvailableCharsets() {
		return _mapCharsetNames.keySet().toArray(new Charset[0]);
	}

	/**
	 * このコンポーネントで選択可能な文字セット名の配列を返す。
	 * @return	文字セット名の配列
	 */
	static public String[] getAvailableCharsetNames() {
		return _mapCharsetNames.values().toArray(new String[0]);
	}

	/**
	 * 指定された文字セット名に対応する、このコンポーネントで選択可能な文字セットを取得する。
	 * @param charsetName	文字セット名
	 * @return	指定された文字セット名に対応する、選択可能な文字セットを返す。
	 * 			選択可能な文字セットが存在しない場合は <tt>null</tt> を返す。
	 */
	static public Charset getAvailableCharset(String charsetName) {
		if (Strings.isNullOrEmpty(charsetName))
			return null;
		
		Charset cs;
		try {
			cs = Charset.forName(charsetName);
		} catch (Throwable ex) {
			cs = null;
		}
		if (!_mapCharsetNames.containsKey(cs)) {
			cs = null;
		}
		
		return cs;
	}

	/**
	 * 指定された文字セットに対応する、このコンポーネントで選択可能な文字セット名を取得する。
	 * @param charset	文字セット
	 * @return	指定された文字セットに対応する、選択可能な文字セット名を返す。
	 * 			選択可能な文字セットが存在しない場合は <tt>null</tt> を返す。
	 */
	static public String getAvailableCharsetName(Charset charset) {
		if (charset == null) {
			return null;
		} else {
			return _mapCharsetNames.get(charset);
		}
	}

	/**
	 * 指定された文字セット名がこのコンポーネントで選択可能かを判定する。
	 * @param charsetName	文字セット名
	 * @return	選択可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean isAvailableCharset(String charsetName) {
		if (Strings.isNullOrEmpty(charsetName))
			return false;
		
		boolean contains;
		try {
			contains = _mapCharsetNames.containsKey(Charset.forName(charsetName));
		} catch (Throwable ex) {
			contains = false;
		}
		
		return contains;
	}

	/**
	 * 指定された文字セットがこのコンポーネントで選択可能かを判定する。
	 * @param charset	文字セット
	 * @return	選択可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	static public boolean isAvailableCharset(Charset charset) {
		if (charset == null) {
			return false;
		} else {
			return _mapCharsetNames.containsKey(charset);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトで選択されている文字セットを返す。
	 * @return	選択されている文字セットを返す。
	 * 			選択されていない場合は <tt>null</tt> を返す。
	 */
	public Charset getSelectedCharset() {
		String csName = getSelectedCharsetName();
		if (csName != null) {
			try {
				return Charset.forName(csName);
			} catch (Throwable ex) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * このオブジェクトで選択されている文字セット名を返す。
	 * @return	選択されている文字セット名を返す。
	 * 			選択されていない場合は <tt>null</tt> を返す。
	 */
	public String getSelectedCharsetName() {
		return (String)getSelectedItem();
	}

	/**
	 * デフォルト文字セットを選択する。
	 */
	public void setSelectedDefaultCharset() {
		setSelectedItem(getDefaultCharsetName());
	}

	/**
	 * 指定された文字セットに対応する項目を選択する。
	 * 指定された文字セットが候補に存在しない場合は、選択を解除する。
	 * @param charset	選択する文字セット
	 */
	public void setSelectedCharset(Charset charset) {
		String csName = getAvailableCharsetName(charset);
		if (csName != null) {
			setSelectedItem(csName);
		} else {
			setSelectedIndex(-1);
		}
	}

	/**
	 * 指定された文字セット名に対応する項目を選択する。
	 * 指定された文字セットが候補に存在しない場合は、選択を解除する。
	 * このメソッドでは、文字セット名のエイリアスにも対応する。
	 * @param charsetName	選択する文字セット名
	 */
	public void setSelectedCharsetName(String charsetName) {
		if (!Strings.isNullOrEmpty(charsetName)) {
			setSelectedItem(charsetName);
			if (getSelectedIndex() < 0) {
				Charset cs = getAvailableCharset(charsetName);
				if (cs != null) {
					setSelectedItem(getAvailableCharsetName(cs));
				} else {
					setSelectedIndex(-1);
				}
			}
		} else {
			setSelectedIndex(-1);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定された文字セット名を、候補文字セット名として設定する。
	 * このプラットフォームでサポートされない文字セット名は除外する。
	 * また、プラットフォーム標準の文字セット名が存在しない場合は候補に追加する。
	 * @param names	候補とする文字セット名の配列
	 */
	static private void updateCandidateCharsetNames(String...names) {
		Charset defCharset = Charset.defaultCharset();
		Map<Charset,String> csmap = new TreeMap<Charset, String>();
		csmap.put(defCharset, defCharset.displayName());
		for (String csName : names) {
			try {
				Charset cs = Charset.forName(csName);
				if (cs.canEncode()) {
					csmap.put(cs, csName);
				}
			}
			catch (Throwable ignoreEx) {}
		}
		
		_defCharset = defCharset;
		_mapCharsetNames = csmap;
	}
}
