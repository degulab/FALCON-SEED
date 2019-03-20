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
 * @(#)EtcPoiUtil.java	3.3.0	2016/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.poi;

import java.io.File;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * <code>Apache POI</code> のためのユーティリティ群。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcPoiUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** Excel 2007 以上の最大行数 **/
	static public final int	MAX_OVER2007_ROWS	= 1048576;
	/** Excel 2007 以上の最大列数 **/
	static public final int	MAX_OVER2007_COLS	= 16384;
	/** Excel 2003 以下の最大行数 **/
	static public final int MAX_UNTIL2003_ROWS	= 65536;
	/** Excel 2003 以下の最大列数 **/
	static public final int	MAX_UNTIL2003_COLS	= 256;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private EtcPoiUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定されたワークブックを、例外をスローせずにクローズする。
	 * 引数に <tt>null</tt> が指定された場合、このメソッドはなにもしない。
	 * @param workbook	ワークブック
	 */
	static public void closeSilent(Workbook workbook) {
		if (workbook != null) {
			try {
				workbook.close();
			} catch (Throwable ex) {}
		}
	}

	/**
	 * 指定されたファイルが、読み取りパスワード付きワークブックか判定する。
	 * @param targetfile	対象のファイル
	 * @return	読み取りパスワード付きワークブックなら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws InvalidFormatException 	ワークブックが読み込み可能な形式ではない場合
	 * @throws IOException				入出力エラーが発生した場合 
	 */
	static public boolean checkWorkbookWithReadingPassword(File targetfile) throws InvalidFormatException, IOException
	{
		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(targetfile, null, true);
		}
		catch (EncryptedDocumentException ex) {
			return true;
		}
		finally {
			closeSilent(wb);
		}
		
		// no password, or unknown
		return false;
	}

	/**
	 * 指定された <code>Cell</code> の、取得可能な値の種類を取得する。
	 * <p>このメソッドでは、<code>Cell</code> が数式の場合、キャッシュされている値の種類を返す。
	 * @param cell	<code>Cell</code> オブジェクト
	 * @return	値の種類
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public int getCellValueType(Cell cell) {
		int ctype = cell.getCellType();
		if (ctype == Cell.CELL_TYPE_FORMULA) {
			ctype = cell.getCachedFormulaResultType();
		}
		return ctype;
	}

	/**
	 * 指定された <code>Cell</code> が <tt>null</tt> もしくはセルのタイプが <code>BLANK</code> かどうかを判定する。
	 * このメソッドでは、セルのタイプが <code>FORMULA</code> の場合は <tt>false</tt> を返す。
	 * @param cell	対象のセル
	 * @return	セルが <tt>null</tt> もしくは <code>BLANK</code> セルの場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static public boolean isNullOrBlankCell(Cell cell) {
		return (cell==null || cell.getCellType()==Cell.CELL_TYPE_BLANK);
	}

	/**
	 * 指定された <code>Cell</code> の、取得可能な値の種類が <code>BLANK</code> かどうかを判定する。
	 * <p>このメソッドでは、<code>Cell</code> が数式の場合、キャッシュされている値の種類で判定する。
	 * @param cell	<code>Cell</code> オブジェクト
	 * @return	取得可能な値の種類が <code>BLANK</code> なら <tt>true</tt>、そうでないなら <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isBlankCellValue(Cell cell) {
		return (getCellValueType(cell) == Cell.CELL_TYPE_BLANK);
	}
	
	/**
	 * 指定された <code>Cell</code> の、取得可能な値の種類が <code>STRING</code> かどうかを判定する。
	 * <p>このメソッドでは、<code>Cell</code> が数式の場合、キャッシュされている値の種類で判定する。
	 * @param cell	<code>Cell</code> オブジェクト
	 * @return	取得可能な値の種類が <code>STRING</code> なら <tt>true</tt>、そうでないなら <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isStringCellValue(Cell cell) {
		return (getCellValueType(cell) == Cell.CELL_TYPE_STRING);
	}

	/**
	 * 指定された <code>Cell</code> の、取得可能な値の種類が <code>BOOLEAN</code> かどうかを判定する。
	 * <p>このメソッドでは、<code>Cell</code> が数式の場合、キャッシュされている値の種類で判定する。
	 * @param cell	<code>Cell</code> オブジェクト
	 * @return	取得可能な値の種類が <code>BOOLEAN</code> なら <tt>true</tt>、そうでないなら <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isBooleanCellValue(Cell cell) {
		return (getCellValueType(cell) == Cell.CELL_TYPE_BOOLEAN);
	}
	
	/**
	 * 指定された <code>Cell</code> の、取得可能な値の種類が <code>NUMERIC</code> かどうかを判定する。
	 * <p>このメソッドでは、<code>Cell</code> が数式の場合、キャッシュされている値の種類で判定する。
	 * @param cell	<code>Cell</code> オブジェクト
	 * @return	取得可能な値の種類が <code>NUMERIC</code> なら <tt>true</tt>、そうでないなら <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isNumericCellValue(Cell cell) {
		return (getCellValueType(cell) == Cell.CELL_TYPE_NUMERIC);
	}
	
	/**
	 * 指定された <code>Cell</code> の、取得可能な値の種類が <code>ERROR</code> かどうかを判定する。
	 * <p>このメソッドでは、<code>Cell</code> が数式の場合、キャッシュされている値の種類で判定する。
	 * @param cell	<code>Cell</code> オブジェクト
	 * @return	取得可能な値の種類が <code>ERROR</code> なら <tt>true</tt>、そうでないなら <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isErrorCellValue(Cell cell) {
		return (getCellValueType(cell) == Cell.CELL_TYPE_ERROR);
	}

	/**
	 * 指定された <code>Cell</code> の書式スタイルのインデックス値を取得する。
	 * @param cell	対象のセル
	 * @return	書式スタイルのインデックス
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public short getCellFormatIndex(Cell cell) {
		return cell.getCellStyle().getDataFormat();
	}

	/**
	 * 指定された <code>Cell</code> の書式スタイルのパターンを表す文字列を取得する。
	 * @param cell	対象のセル
	 * @return	書式スタイルパターンの文字列、パターンが指定されていない場合は空文字
	 */
	static public String getCellFormatPattern(Cell cell) {
		final DataFormat dataFormat = cell.getSheet().getWorkbook().createDataFormat();
		final short formatIndex = cell.getCellStyle().getDataFormat();
		
		String formatPattern = dataFormat.getFormat(formatIndex);
		return (formatPattern==null ? "" : formatPattern);
	}

	/**
	 * 指定されたセルが結合領域内のセルかどうかを判定する。
	 * @param cell	判定するセル
	 * @return	結合領域内のセルなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isMergedCell(Cell cell) {
		return (getMergedCellRangeAddress(cell) != null);
	}

	/**
	 * 指定されたセルが結合セルの場合に、指定されたセルが含まれる結合領域を取得する。
	 * <p><code>Apache POI</code> の場合、結合されている領域では、左上のセル以外はブランクセルとなるため、
	 * 結合セルの場合は結合領域の左上セルを取得する。
	 * @param cell	判定するセル
	 * @return	対象セルが結合セルの場合は結合領域のアドレス範囲、結合セルではない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public CellRangeAddress getMergedCellRangeAddress(Cell cell) {
		final Sheet sheet = cell.getSheet();
		final int numMerges = sheet.getNumMergedRegions();
		if (numMerges == 0)
			return null;	// no merged regions

		for (int index = 0; index < numMerges; ++index) {
			final CellRangeAddress range = sheet.getMergedRegion(index);
			if (range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
				return range;
			}
		}
		
		// no matches
		return null;
	}

	/**
	 * 結合セル領域から、値が格納された基準セルを取得する。
	 * <p><code>Apache POI</code> の場合、通常は結合領域の左上のセルに値が格納されている。
	 * @param sheet 結合領域を保持するシート
	 * @param range	結合領域
	 * @return	基準セル、領域にセルが存在しない場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Cell getMergedBaseCellFromMergedRegion(Sheet sheet, CellRangeAddress range) {
		Cell firstFoundCell = null;
		
		// 非 BLANK または null ではないセルを取得
		for (int rowIndex = range.getFirstRow(); rowIndex <= range.getLastRow(); ++rowIndex) {
			final Row objrow = sheet.getRow(rowIndex);
			if (objrow == null)
				continue;
			for (int colIndex = range.getFirstColumn(); colIndex <= range.getLastColumn(); ++colIndex) {
				final Cell objcell = objrow.getCell(colIndex);
				if (objcell == null)
					continue;
				if (firstFoundCell == null)
					firstFoundCell = objcell;	// 最初にインスタンスが存在するセルを保存
				if (objcell.getCellType() != Cell.CELL_TYPE_BLANK) {
					// 値のあるセルを基準セルとみなす
					return objcell;
				}
			}
		}
		
		// 見つからない場合は、最初に見つかったセルインスタンスを返す
		return firstFoundCell;
	}
	
	/**
	 * 結合されているセルの場合に、値が格納されたセルを返す。
	 * <p><code>Apache POI</code> の場合、結合されている領域では、左上のセル以外はブランクセルとなるため、
	 * 結合セルの場合は結合領域の左上セルを取得する。
	 * @param cell	対象のセル
	 * @return	対象セルが結合セルの場合は基準セル、結合セルではない場合は <em>cell</em> をそのまま返す
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Cell getMergedBaseCell(Cell cell) {
		CellRangeAddress range = getMergedCellRangeAddress(cell);
		if (range == null) {
			// 結合セルではない
			return cell;
		}
		else {
			// 結合セルの基準セルを返す
			Cell ret = getMergedBaseCellFromMergedRegion(cell.getSheet(), range);
			//--- 基準セルが <tt>null</tt> の場合は結合セルとはみなさない
			return (ret==null ? cell : ret);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
