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
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)CsvLocator.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvLocator.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io.csv;

/**
 * @deprecated このインタフェースは使用されなくなりました。このインタフェースは将来削除されます。
 * 
 * CSVファイルとオブジェクトとの位置を関連付けるためのインターフェース。
 * <p>
 * 主に CSV ファイル読み込み時に、読み込みカラムとファイル上の位置との関連付けに使用される。
 * 
 * 
 * @version 0.91 2007/08/09
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.91
 *
 */
public interface CsvLocator {
	/**
	 * CSV ファイルの行番号を取得する。
	 * <br>
	 * CSV ファイルの行番号は 1 から始まるものとする。
	 * 
	 * @return 行番号
	 */
	public int getLineNumber();
	
	/**
	 * CSV ファイルのカラム番号を取得する。
	 * <br>
	 * CSV ファイルのカラム番号は 1 から始まるものとする。
	 * 
	 * @return カラム番号
	 */
	public int getColumnNumber();
	
	/**
	 * CSV ファイルの行番号を 1 つ進める。
	 *
	 */
	public void incrementLineNumber();
	
	/**
	 * CSV ファイルのカラム番号を 1 つ進める。
	 *
	 */
	public void incrementColumnNumber();
}
