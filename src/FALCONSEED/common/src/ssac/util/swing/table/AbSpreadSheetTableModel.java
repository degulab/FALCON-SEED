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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)AbSpreadSheetTableModel.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.table;

import java.text.NumberFormat;

import javax.swing.table.AbstractTableModel;

/**
 * スプレッドシートのテーブルモデルの共通実装。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public abstract class AbSpreadSheetTableModel extends AbstractTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -639514445549105738L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public AbSpreadSheetTableModel() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された位置のデータがエラーを示すデータかを判定する。
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return	エラーを示す場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isError(int rowIndex, int columnIndex) {
		return false;
	}

	/**
	 * 指定された位置のセルに対するツールチップ文字列を取得する。
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return	ツールチップ文字列、ツールチップを表示しない場合は <tt>null</tt>
	 */
	public String getCellToolTipText(int rowIndex, int columnIndex) {
		return null;
	}

	/**
	 * 指定された行の名前を取得する。
	 * このオブジェクトが返す文字列は、テーブルの行ヘッダーに表示される。
	 * @param rowIndex	行インデックス
	 * @return	行の名前を示す文字列
	 */
	public String getRowName(int rowIndex) {
		return NumberFormat.getNumberInstance().format(rowIndex+1);
	}

	/**
	 * 指定された行の行ヘッダーに対するツールチップ文字列を取得する。
	 * @param rowIndex	行インデックス
	 * @return	ツールチップ文字列、ツールチップを表示しない場合は <tt>null</tt>
	 */
	public String getRowHeaderToolTipText(int rowIndex) {
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
