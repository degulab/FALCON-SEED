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
 * @(#)CsvLocatorImpl.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvLocatorImpl.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io.csv;

/**
 * @deprecated このクラスは使用されなくなりました。このクラスは将来削除されます。
 * 
 * CSVファイルとオブジェクトとの位置を関連付けるためのインターフェースの実装。
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
 */
public class CsvLocatorImpl implements CsvLocator
{
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 行番号
	 */
	private int lineNumber;
	/**
	 * カラム番号
	 */
	private int columnNumber;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 新規の <code>CsvLocatorImpl</code> インスタンスを生成する。
	 * <br>
	 * 新規作成後の行番号、カラム番号は、共に 0 となる。
	 */
	public CsvLocatorImpl() {
	}

	/**
	 * コピーコンストラクタ。
	 * <br>
	 * 指定された {@link CsvLocator} インスタンスの行番号とカラム番号をコピーする。
	 * 
	 * @param locator コピー対象のロケーター
	 */
	public CsvLocatorImpl(CsvLocator locator) {
		setLineNumber(locator.getLineNumber());
		setColumnNumber(locator.getColumnNumber());
	}

	//------------------------------------------------------------
	// Implement CsvLocator interfaces
	//------------------------------------------------------------

	/**
	 * 保存されている行番号を返す(開始番号は 1)。
	 * 
	 * @return 行番号
	 */
	public int getLineNumber() {
		return this.lineNumber;
	}

	/**
	 * 保存されているカラム番号を返す(開始番号は 1)。
	 * 
	 * @return カラム番号
	 */
	public int getColumnNumber() {
		return this.columnNumber;
	}

	/**
	 * 保存されている行番号を 1 進める。
	 */
	public void incrementLineNumber() {
		this.lineNumber++;
	}

	/**
	 * 保存されているカラム番号を 1 進める。
	 */
	public void incrementColumnNumber() {
		this.columnNumber++;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 保存されている行番号を指定の値で更新する。
	 * 
	 * @param lineNumber 更新する行番号の値
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * 保存されているカラム番号を指定の値で更新する。
	 * 
	 * @param columnNumber 更新するカラム番号の値
	 */
	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}
}
