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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AbVirtualFilePathFormatter.java	2.0.0	2012/10/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbVirtualFilePathFormatter.java	1.20	2012/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file;

import ssac.util.io.Files;
import ssac.util.io.VirtualFile;

/**
 * パスの表示名整形インタフェースの標準実装。
 * このクラスでは、単一のベースパスと、そのベースパスを置き換える表示名を指定する。
 * ベースパスの相対とならない場合は、絶対パスで表示される。なお、絶対パスの場合は、
 * ファイル区切り文字は変更しない。
 * 
 * @version 2.0.0	2012/10/26
 * @since 1.20
 */
public abstract class AbVirtualFilePathFormatter implements VirtualFilePathFormatter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 整形後のパス文字列の先頭にファイル区切り文字を表示する場合に <tt>true</tt> を返す。
	 * このメソッドが <tt>true</tt> を返す場合、整形後のパス文字列の先頭には、
	 * ファイル区切り文字が付加される。
	 */
	abstract public boolean isRootSeparatorVisible();

	/**
	 * 基準パスの表示名を返す。
	 * このメソッドが <tt>null</tt> を返す場合、整形後の表示名は基準パスのファイル名となる。
	 * また、空文字列を返す場合は、整形後の表示名に基準パスは含まれない。
	 * @return	基準パスの表示名
	 */
	abstract public String getDisplayName();

	/**
	 * このオブジェクトの基準パスを返す。
	 * @return	基準パスの抽象パス
	 */
	abstract public VirtualFile getBaseFile();

	/**
	 * このオブジェクトの基準パスのファイル名を返す。
	 * 表示名が設定されている場合は、その表示名を返す。
	 * @return	基準パスの整形されたファイル名
	 * @throws NullPointerException	基準パスが <tt>null</tt> の場合
	 */
	public String getBaseName() {
		String name = getDisplayName();
		return (name==null ? getBaseFile().getName() : name);
	}

	/**
	 * 指定された抽象ファイルが、このオブジェクトの基準パスと等しい、
	 * もしくは下位のファイルかを判定する。
	 * @param file	判定する抽象パス
	 * @return	基準パスと等しい、もしくは下位のファイルなら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isDescendingFromBasePath(VirtualFile file) {
		VirtualFile fbase = getBaseFile();
		if (fbase == null) {
			return false;
		} else {
			return file.isDescendingFrom(fbase);
		}
	}
	
	/**
	 * 指定された抽象パスの整形後のパス文字列を取得する。
	 * 基準パスと等しい、もしくは下位のファイルの場合のみ、パス文字列を整形する。
	 * 基準パスに含まれないパスの場合は、抽象パスのパス文字列をそのまま返す。
	 * なお、整形する際の区切り文字はスラッシュ('/')とする。
	 * @param file	整形対象の抽象パス
	 * @return	パス文字列
	 */
	public String getPath(VirtualFile file) {
		return getPath(file, Files.CommonSeparatorChar);
	}
	
	/**
	 * 指定された抽象パスの整形後のパス文字列を取得する。
	 * 基準パスと等しい、もしくは下位のファイルの場合のみ、パス文字列を整形する。
	 * 基準パスに含まれないパスの場合は、抽象パスのパス文字列をそのまま返す。
	 * なお、整形する際の区切り文字には、指定された区切り文字を使用する。
	 * @param file		整形対象の抽象パス
	 * @param separator	ファイル区切り文字
	 * @return	パス文字列
	 */
	public String getPath(VirtualFile file, char separator) {
		String path = formatPath(file, separator);
		if (path == null) {
			return file.getPath();
		} else {
			return path;
		}
	}

	/**
	 * 指定された抽象パスのパス文字列を、このオブジェクトの定義に従い整形する。
	 * 基準パスと等しい、もしくは下位のファイルの場合のみ、整形したパス文字列を返す。
	 * 基準パスに含まれないパスの場合は <tt>null</tt> を返す。
	 * なお、整形する際の区切り文字はスラッシュ('/')とする。
	 * @param file	整形対象の抽象パス
	 * @return	パス文字列
	 */
	public String formatPath(VirtualFile file) {
		return formatPath(file, Files.CommonSeparatorChar);
	}

	/**
	 * 指定された抽象パスのパス文字列を、このオブジェクトの定義に従い整形する。
	 * 基準パスと等しい、もしくは下位のファイルの場合のみ、整形したパス文字列を返す。
	 * 基準パスに含まれないパスの場合は <tt>null</tt> を返す。
	 * なお、整形する際の区切り文字には、指定された区切り文字を使用する。
	 * @param file		整形対象の抽象パス
	 * @param separator	ファイル区切り文字
	 * @return	パス文字列
	 */
	public String formatPath(VirtualFile file, char separator) {
		if (!isDescendingFromBasePath(file)) {
			return null;	// not descendant from base path
		}
		
		VirtualFile targetFile = (file.isAbsolute() ? file : file.getAbsoluteFile());
		StringBuilder sb = new StringBuilder();
		if (isRootSeparatorVisible()) {
			sb.append(separator);
		}
		String strName = getBaseName();
		if (strName.length() > 0) {
			sb.append(strName);
			sb.append(separator);
		}
		sb.append(targetFile.relativePathFrom(getBaseFile(), separator));
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
