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
 * @(#)ExtensionFileFilter.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExtensionFileFilter.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.io.File;
import java.util.Vector;

import javax.swing.filechooser.FileFilter;

import ssac.util.Strings;

/**
 * ファイル拡張子とファイルの説明を保持するファイルフィルタ
 * 
 * @version 1.14	2009/12/09
 */
public final class ExtensionFileFilter extends FileFilter
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final char EXT_SEPARATOR_CHAR = ';';
	static public final String EXT_SEPARATOR = ""+EXT_SEPARATOR_CHAR;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final String fileDescription;
	private final String[] fileExtensions;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ExtensionFileFilter(String desc, String separatedExtensions) {
		this(desc, splitExtensions(separatedExtensions));
	}
	
	public ExtensionFileFilter(String desc, String[] extensions) {
		this.fileDescription = (desc != null ? desc : "");
		this.fileExtensions = normalizeExtensions(extensions);
	}
	
	static private String[] splitExtensions(String separatedExtensions) {
		if (separatedExtensions != null) {
			return separatedExtensions.split(EXT_SEPARATOR);
		} else {
			return null;
		}
	}
	
	static private String[] normalizeExtensions(String[] extensions) {
		if (extensions == null)
			return new String[0];
		
		Vector<String> buf = new Vector<String>(extensions.length);
		for (String ext : extensions) {
			if (ext != null && ext.length() > 0) {
				if (ext.startsWith(".")) {
					buf.add(ext.toLowerCase());
				} else {
					buf.add("." + ext.toLowerCase());
				}
			}
		}
		
		return buf.toArray(new String[buf.size()]);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトに登録されている拡張子が <em>text</em> に存在する
	 * 場合に <tt>true</tt> を返す。
	 * @since 1.14
	 */
	public boolean existsExtension(String text) {
		if (!Strings.isNullOrEmpty(text)) {
			String str = text.toLowerCase();
			for (String ext : fileExtensions) {
				if (str.endsWith(ext)) {
					return true;
				}
			}
		}
		// no match
		return false;
	}
	
	public String[] getExtensions() {
		return this.fileExtensions;
	}
	
	public String getDefaultExtension() {
		return (this.fileExtensions.length > 0 ? this.fileExtensions[0] : null);
	}
	
	public String appendExtension(String filePath) {
		if (this.fileExtensions.length > 0) {
			//@@@ 2009/10/29 : Y.Ishizuka : modified @@@
			if (!existsExtension(filePath)) {
				//--- append default extension
				return (filePath + fileExtensions[0]);
			}
			/* --- 2009/10/29 : old codes
			if (!filePath.toLowerCase().endsWith(this.fileExtensions[0])) {
				return (filePath + this.fileExtensions[0]);
			}
			** --- 2009/10/29 : end of old codes */
			//@@@ 2009/10/29 : Y.Ishizuka : end of modified @@@
		}
		return filePath;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement javax.swing.filechooser.FileFilter interfaces
	//------------------------------------------------------------

	@Override
	public boolean accept(File f) {
		// is File directory?
		if (f.isDirectory())
			return true;
		
		// is File?
		if (f.isFile()) {
			// check extensions
			final String fname = f.getName().toLowerCase();
			for (String ext : this.fileExtensions) {
				if (fname.endsWith(ext)) {
					return true;
				}
			}
		}
		
		// cannot accept, because no match pattern
		return false;
	}

	@Override
	public String getDescription() {
		return this.fileDescription;
	}
}
