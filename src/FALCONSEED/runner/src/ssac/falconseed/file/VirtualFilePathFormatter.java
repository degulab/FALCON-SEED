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
 * @(#)VirtualFilePathFormatter.java	1.20	2012/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file;

import ssac.util.io.VirtualFile;

/**
 * パスの表示名を整形するためのインタフェース。
 * 
 * @version 1.20	2012/03/08
 * @since 1.20
 */
public interface VirtualFilePathFormatter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * 指定された抽象ファイルが、このオブジェクトの基準パスと等しい、
	 * もしくは下位のファイルかを判定する。
	 * @param file	判定する抽象パス
	 * @return	基準パスと等しい、もしくは下位のファイルなら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isDescendingFromBasePath(VirtualFile file);
	
	/**
	 * 指定された抽象パスの整形後のパス文字列を取得する。
	 * 基準パスと等しい、もしくは下位のファイルの場合のみ、パス文字列を整形する。
	 * 基準パスに含まれないパスの場合は、抽象パスのパス文字列をそのまま返す。
	 * なお、整形する際の区切り文字はスラッシュ('/')とする。
	 * @param file	整形対象の抽象パス
	 * @return	パス文字列
	 */
	public String getPath(VirtualFile file);
	
	/**
	 * 指定された抽象パスの整形後のパス文字列を取得する。
	 * 基準パスと等しい、もしくは下位のファイルの場合のみ、パス文字列を整形する。
	 * 基準パスに含まれないパスの場合は、抽象パスのパス文字列をそのまま返す。
	 * なお、整形する際の区切り文字には、指定された区切り文字を使用する。
	 * @param file		整形対象の抽象パス
	 * @param separator	ファイル区切り文字
	 * @return	パス文字列
	 */
	public String getPath(VirtualFile file, char separator);

	/**
	 * 指定された抽象パスのパス文字列を、このオブジェクトの定義に従い整形する。
	 * 基準パスと等しい、もしくは下位のファイルの場合のみ、整形したパス文字列を返す。
	 * 基準パスに含まれないパスの場合は <tt>null</tt> を返す。
	 * なお、整形する際の区切り文字はスラッシュ('/')とする。
	 * @param file	整形対象の抽象パス
	 * @return	パス文字列
	 */
	public String formatPath(VirtualFile file);

	/**
	 * 指定された抽象パスのパス文字列を、このオブジェクトの定義に従い整形する。
	 * 基準パスと等しい、もしくは下位のファイルの場合のみ、整形したパス文字列を返す。
	 * 基準パスに含まれないパスの場合は <tt>null</tt> を返す。
	 * なお、整形する際の区切り文字には、指定された区切り文字を使用する。
	 * @param file		整形対象の抽象パス
	 * @param separator	ファイル区切り文字
	 * @return	パス文字列
	 */
	public String formatPath(VirtualFile file, char separator);
}
