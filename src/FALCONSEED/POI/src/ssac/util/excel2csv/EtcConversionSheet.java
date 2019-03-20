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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)EtcConversionSheet.java	3.3.0	2016/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import ssac.util.excel2csv.poi.EtcPoiUtil;
import ssac.util.excel2csv.poi.SheetInfo;

import com.github.mygreen.cellformatter.POICellFormatter;

/**
 * <code>[Excel to CSV]</code> における変換対象シートを保持するクラス。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcConversionSheet
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このシートの属するワークブック **/
	private final EtcWorkbookManager	_excelbook;
	/** シート情報 **/
	private final SheetInfo	_sinfo;
	/** シートオブジェクト **/
	private final Sheet		_sheet;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメータで、新しいインスタンスを生成する。
	 * @param excelbook	対象の Excel ワークブック
	 * @param sinfo		対象シートの情報
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	引数が適切ではない場合
	 */
	public EtcConversionSheet(EtcWorkbookManager excelbook, SheetInfo sinfo)
	{
		if (excelbook == null || sinfo == null)
			throw new NullPointerException();
		if (!excelbook.isOpened())
			throw new IllegalArgumentException("Excel book was already closed.");
		if (sinfo.getSheetIndex() < 0 || sinfo.getSheetIndex() >= excelbook.getWorkbook().getNumberOfSheets())
			throw new IllegalArgumentException("Sheet index is out of range : index=" + sinfo.getSheetIndex());

		_excelbook = excelbook;
		_sinfo = sinfo;
		_sheet = excelbook.getWorkbook().getSheetAt(sinfo.getSheetIndex());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public EtcWorkbookManager getWorkbook() {
		return _excelbook;
	}

	/**
	 * <code>Excel-CellFormatter</code> ライブラリの <code>POI</code> 用フォーマッターオブジェクトを取得する。
	 * <p>このメソッドが返すフォーマッターは結合セルを考慮しないので、結合セルの基準セルから値を取得する場合は、
	 * {@link ssac.util.excel2csv.poi.EtcPoiUtil#getMergedBaseCell(org.apache.poi.ss.usermodel.Cell)} によって
	 * 結合セルの基準セルを取得すること。
	 * @return	<code>POICellFormatter</code> オブジェクト
	 */
	public POICellFormatter getCellFormatter() {
		return _excelbook.getCellFormatter();
	}

	/**
	 * <code>Apache POI</code> ライブラリのデータフォーマッターオブジェクトを取得する。
	 * <p>このメソッドが返すフォーマッターは結合セルを考慮しないので、結合セルの基準セルから値を取得する場合は、
	 * {@link ssac.util.excel2csv.poi.EtcPoiUtil#getMergedBaseCell(org.apache.poi.ss.usermodel.Cell)} によって
	 * 結合セルの基準セルを取得すること。
	 * @return	<code>DataFormatter</code> オブジェクト
	 */
	public DataFormatter getPoiFormatter() {
		return _excelbook.getPoiFormatter();
	}
	
	public SheetInfo getSheetInfo() {
		return _sinfo;
	}

	/**
	 * 指定された行インデックスに対応する行オブジェクトが存在するかどうかを判定する。
	 * @param rowIndex	行インデックス
	 * @return	行インデックスに対応する行オブジェクトが存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean rowExists(int rowIndex) {
		return (_sheet.getRow(rowIndex) != null);
	}

	/**
	 * このシートから、指定された位置にあるセルを取得する。
	 * @param rowIndex				行インデックス
	 * @param colIndex				列インデックス
	 * @param considerMergedCell	結合セルを考慮する場合は <tt>true</tt>
	 * @return	指定された位置の <code>Cell</code> オブジェクト、セルが存在しない場合は <tt>null</tt>
	 */
	public Cell getCell(int rowIndex, int colIndex, boolean considerMergedCell) {
		if (rowIndex >= _sinfo.getFirstRowIndex() && rowIndex <= _sinfo.getLastRowIndex()) {
			final Row objrow = _sheet.getRow(rowIndex);
			if (objrow == null || colIndex < objrow.getFirstCellNum() || colIndex >= objrow.getLastCellNum()) {
				// no cell
				return null;
			}
			final Cell objcell = objrow.getCell(colIndex);
			if (considerMergedCell) {
				// 結合セルを考慮
				if (objcell != null && objcell.getCellType() == Cell.CELL_TYPE_BLANK) {
					// 結合セルの可能性
					return EtcPoiUtil.getMergedBaseCell(objcell);
				} else {
					// 非結合セル
					return objcell;
				}
			}
			else {
				// 結合セルは無視
				return objcell;
			}
		}
		else {
			// no row
			return null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
