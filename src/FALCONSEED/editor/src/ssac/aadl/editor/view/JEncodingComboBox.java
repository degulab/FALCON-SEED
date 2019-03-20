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
 * @(#)JEncodingComboBox.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)JEncodingComboBox.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)JEncodingComboBox.java	1.10	2008/12/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)JEncodingComboBox.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view;

import java.nio.charset.Charset;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComboBox;

import ssac.util.Strings;

/**
 * AADLエディタで利用可能な文字セット(テキスト・ファイル・エンコーディング用)を
 * 保持するコンボボックス・コンポーネント。
 * 
 * @version 1.16	2010/09/27
 */
public class JEncodingComboBox extends JComboBox
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	/*--- old codes ---
	static private final String[] PresetCharsetNames = new String[]{
		"Big5", "Big5-HKSCS", "EUC-JP", "EUC-KR", "GB18030", "GB2312",
		"GBK", "ISO-8859-1", "MS932", "Shift_JIS", "US-ASCII",
		"UTF-16", "UTF-16BE", "UTF-16LE", "UTF-8",
		"Windows-1252", "Windows-31J"
	};
	**--- end of old codes ---*/
	
	static private final String[] PresetCharsetNames = new String[]{
		"UTF-8",
		"MS932",
		"EUC-JP",
		"UTF-16",
		"UTF-16BE",
		"UTF-16LE", 
		"US-ASCII",
		"Shift_JIS",
		"EUC-KR",   
	};
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	static private String		DefaultCharsetName;
	static private String[]	CharsetNames;
	
	static {
		refreshCharsetNames();
	}

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JEncodingComboBox() {
		this(null);
	}
	
	public JEncodingComboBox(String selectedName) {
		super(CharsetNames);
		if (selectedName != null) {
			this.setSelectedItem(selectedName);
		} else {
			this.setSelectedItem(DefaultCharsetName);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public int getAvailableCount() {
		return CharsetNames.length;
	}
	
	static public String[] getAvailableEncodings() {
		return CharsetNames;
	}
	
	static public String getDefaultEncoding() {
		return DefaultCharsetName;
	}
	
	static public String getAvailableCharsetName(Charset charset) {
		// check argument
		if (charset == null)
			return null;
		
		// find name
		Set<String> nameset = charset.aliases();
		String sname = charset.name();
		String[] presetnames = getAvailableEncodings();
		for (String prename : presetnames) {
			if (nameset.contains(prename)) {
				return prename;
			}
			else if (sname.equals(prename)) {
				return prename;
			}
		}
		
		// not found
		return null;
	}
	
	static public boolean isAvailableEncoding(String charsetName) {
		// check empty
		if (Strings.isNullOrEmpty(charsetName))
			return false;
		
		// can encoding?
		Charset target;
		try {
			target = Charset.forName(charsetName);
			if (!target.canEncode()) {
				target = null;
			}
		}
		catch (Throwable ex) {
			// Through
			target = null;
		}
		if (target == null)
			return false;
		
		// check for available encoding list
		for (String availableName : CharsetNames) {
			boolean found = false;
			try {
				Charset aset = Charset.forName(availableName);
				if (target.equals(aset)) {
					found = true;
				}
			}
			catch (Throwable ex) {
				// Through
			}
			if (found) {
				return true;
			}
		}
		
		// not available charset
		return false;
	}
	
	public String getSelectedEncoding() {
		return (String)this.getSelectedItem();
	}
	
	public void setSelectedEncoding(String encoding) {
		this.setSelectedItem(encoding);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static private void refreshCharsetNames() {
		String defName = null;
		Charset defCharset = Charset.defaultCharset();
		TreeSet<String> csNames = new TreeSet<String>();
		for (String s : PresetCharsetNames) {
			try {
				Charset target = Charset.forName(s);
				if (target.canEncode()) {
					if (defName == null && target.equals(defCharset)) {
						defName = s;
					}
					csNames.add(s);
				}
			}
			catch (Throwable ex) {
				// Through
			}
		}
		
		if (defName == null) {
			defName = defCharset.displayName();
			csNames.add(defName);
		}
		
		CharsetNames = csNames.toArray(new String[csNames.size()]);
		DefaultCharsetName = defName;
	}
}
