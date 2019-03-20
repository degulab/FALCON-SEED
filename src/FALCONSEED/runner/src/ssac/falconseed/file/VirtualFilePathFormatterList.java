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
 * @(#)VirtualFilePathFormatterList.java	1.20	2012/03/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file;

import java.util.ArrayList;
import java.util.Collection;

import ssac.util.io.Files;
import ssac.util.io.VirtualFile;

/**
 * パスの表示名整形インタフェースのリスト。
 * このリスト自身も <code>VirtualFilePathFormatter</code> インタフェースの実装である。
 * このリストでのパス表示名整形は，リストの先頭からパスを評価し，変換可能な要素で
 * 変換できた場合はその結果を返す。そのため，先頭に配置された抽象パスが，それよりも
 * 後に配置された抽象パスの上位である場合，後ろに配置されたもので変換可能な場合でも
 * その前に変換が完了する。
 * 
 * @version 1.20	2012/03/08
 * @since 1.20
 */
public class VirtualFilePathFormatterList extends ArrayList<VirtualFilePathFormatter>
implements VirtualFilePathFormatter
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
	
	public VirtualFilePathFormatterList() {
		super();
	}

	public VirtualFilePathFormatterList(int initialCapacity) {
		super(initialCapacity);
	}

	public VirtualFilePathFormatterList(Collection<? extends VirtualFilePathFormatter> c) {
		super(c);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された抽象ファイルが、このオブジェクトに含まれる基準パスのどれか一つと等しい、
	 * もしくは下位のファイルかを判定する。
	 * @param file	判定する抽象パス
	 * @return	基準パスと等しい、もしくは下位のファイルなら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isDescendingFromBasePath(VirtualFile file) {
		for (VirtualFilePathFormatter formatter : this) {
			if (formatter != null && formatter.isDescendingFromBasePath(file)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 指定された抽象パスの整形後のパス文字列を取得する。
	 * このオブジェクトに含まれる基準パスのどれか一つと等しい、もしくは下位のファイルの場合のみ、パス文字列を整形する。
	 * どの基準パスにも含まれないパスの場合は、抽象パスのパス文字列をそのまま返す。
	 * なお、整形する際の区切り文字はスラッシュ('/')とする。
	 * @param file	整形対象の抽象パス
	 * @return	パス文字列
	 */
	public String getPath(VirtualFile file) {
		return getPath(file, Files.CommonSeparatorChar);
	}
	
	/**
	 * 指定された抽象パスの整形後のパス文字列を取得する。
	 * このオブジェクトに含まれる基準パスのどれか一つと等しい、もしくは下位のファイルの場合のみ、パス文字列を整形する。
	 * どの基準パスにも含まれないパスの場合は、抽象パスのパス文字列をそのまま返す。
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
	 * このオブジェクトに含まれる基準パスのどれか一つと等しい、もしくは下位のファイルの場合のみ、パス文字列を整形する。
	 * どの基準パスにも含まれないパスの場合は <tt>null</tt> を返す。
	 * なお、整形する際の区切り文字はスラッシュ('/')とする。
	 * @param file	整形対象の抽象パス
	 * @return	パス文字列
	 */
	public String formatPath(VirtualFile file) {
		return formatPath(file, Files.CommonSeparatorChar);
	}

	/**
	 * 指定された抽象パスのパス文字列を、このオブジェクトの定義に従い整形する。
	 * このオブジェクトに含まれる基準パスのどれか一つと等しい、もしくは下位のファイルの場合のみ、パス文字列を整形する。
	 * どの基準パスにも含まれないパスの場合は <tt>null</tt> を返す。
	 * なお、整形する際の区切り文字には、指定された区切り文字を使用する。
	 * @param file		整形対象の抽象パス
	 * @param separator	ファイル区切り文字
	 * @return	パス文字列
	 */
	public String formatPath(VirtualFile file, char separator) {
		for (VirtualFilePathFormatter formatter : this) {
			if (formatter != null) {
				String path = formatter.formatPath(file, separator);
				if (path != null) {
					return path;
				}
			}
		}
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
