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
 * @(#)CmdCardTableNode.java	3.3.0	2016/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.poi;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * シートの情報を保持するクラス。
 * このクラスでは、シートそのもののインスタンスは保持しない。
 * また、このぼうじぇくとは不変オブジェクトとする。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class SheetInfo
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final SheetInfo[]	EMPTY_SHEETINFO_ARRAY	= new SheetInfo[0];

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** ワークブック上のシートインデックス **/
	private final int		_sheetIndex;
	/** シート名 **/
	private final String	_sheetName;
	/** 結合セル数 **/
	private final int		_numMergedResions;

	/** シート内の先頭行インデックス、存在しない場合は (-1) **/
	private final int		_firstRowIndex;
	/** シート内の最終行インデックス(このインデックスも含む)、存在しない場合は (-1) **/
	private final int		_lastRowIndex;
	/** シート内の有効行数 **/
	private final int		_numPhysicalRows;
	/** シート内の最小列インデックス、存在しない場合は (-1) **/
	private final int		_minColIndex;
	/** シート内の最大列インデックス(このインデックスも含む)、存在しない場合は (-1) **/
	private final int		_maxColIndex;
	/** シート内の有効最大列数 **/
	private final int		_numMaxCols;
	/** シートのデータのある領域 **/
	private final CellRect	_physicalSheetRect;
	/** シート左上を起点とする、データのある最大領域 **/
	private final CellRect	_logicalSheetRect;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたシートの全行を走査し、新しいインスタンスを生成する。
	 * @param workbook		ワークブック
	 * @param sheetIndex	対象シートのインデックス
	 * @throws NullPointerException	<em>workbook</em> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	<em>sheetIndex</em> が範囲外の場合
	 */
	public SheetInfo(Workbook workbook, int sheetIndex) {
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		_sheetIndex = sheetIndex;
		_sheetName  = sheet.getSheetName();
		_numMergedResions = sheet.getNumMergedRegions();
		
		_firstRowIndex   = sheet.getFirstRowNum();
		_lastRowIndex    = sheet.getLastRowNum();
		
		int minIndex = Integer.MAX_VALUE;
		int maxIndex = -1;
		if (_lastRowIndex >= 0) {
			for (int rowIndex = _firstRowIndex; rowIndex <= _lastRowIndex; ++rowIndex) {
				Row objrow = sheet.getRow(rowIndex);
				if (objrow == null || objrow.getPhysicalNumberOfCells() <= 0)
					continue;
				minIndex = Math.min(minIndex, objrow.getFirstCellNum());
				maxIndex = Math.max(maxIndex, objrow.getLastCellNum() - 1);
			}
		}
		if (maxIndex >= 0) {
			_numPhysicalRows = _lastRowIndex - _firstRowIndex + 1;
			_minColIndex = minIndex;
			_maxColIndex = maxIndex;
			_numMaxCols  = (maxIndex + 1 - minIndex);
			_physicalSheetRect = new CellRect(_firstRowIndex, _minColIndex, _numPhysicalRows, _numMaxCols);
			_logicalSheetRect  = new CellRect(0, 0, _lastRowIndex+1, _maxColIndex+1);
		} else {
			_numPhysicalRows = 0;
			_minColIndex = -1;
			_maxColIndex = -1;
			_numMaxCols  = 0;
			_physicalSheetRect = new CellRect(_firstRowIndex, 0, 0, 0);
			_logicalSheetRect  = new CellRect(0, 0, 0, 0);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * シートのワークブック上のインデックスを返す。
	 * @return	シートのインデックス
	 */
	public int getSheetIndex() {
		return _sheetIndex;
	}

	/**
	 * シート名を返す。
	 * @return	シート名
	 */
	public String getSheetName() {
		return _sheetName;
	}

	/**
	 * このシートに結合セルが存在するかどうかを判定する。
	 * @return	結合セルが存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean hasMergedRegions() {
		return (_numMergedResions > 0);
	}

	/**
	 * このシートの結合セル数を返す。
	 * @return	結合セル数
	 */
	public int getMergedRegionCount() {
		return _numMergedResions;
	}

	/**
	 * 第1行からの行数を返す。
	 * @return	第1行からの行数
	 */
	public int getLogicalRowCount() {
		return (_lastRowIndex + 1);
	}

	/**
	 * 先頭行から最終行までの行数を返す。
	 * このメソッドが返す値は、<code>Apache POI</code> の <code>Sheet#getPhysicalRowCount()</code> が返す値とは異なる場合がある。
	 * @return	データのある範囲の行数
	 */
	public int getPhysicalRowCount() {
		return _numPhysicalRows;
	}

	/**
	 * データのある最初の行インデックスを返す。
	 * @return	最初の行インデックス、データが存在しない場合は (-1)
	 */
	public int getFirstRowIndex() {
		return _firstRowIndex;
	}

	/**
	 * データのある最後の行インデックスを返す。
	 * @return	最後の行インデックス(このインデックスを含む)、データが存在しない場合は (-1)
	 */
	public int getLastRowIndex() {
		return _lastRowIndex;
	}

	/**
	 * 第1列からの最大列数を返す。
	 * @return	第1列からの最大列数
	 */
	public int getLogicalColumnCount() {
		return (_maxColIndex + 1);
	}

	/**
	 * データのある範囲の最大列数を返す。
	 * @return	データのある範囲の最大列数
	 */
	public int getPhysicalColumnCount() {
		return _numMaxCols;
	}

	/**
	 * データのある範囲の最小列インデックスを返す。
	 * @return	データのある範囲の最小列インデックス、データが存在しない場合は (-1)
	 */
	public int getFirstColumnIndex() {
		return _minColIndex;
	}

	/**
	 * データのある範囲の最大列インデックスを返す。
	 * @return	データのある範囲の最大列インデックス(このインデックスを含む)、データが存在しない場合は (-1)
	 */
	public int getLastColumnIndex() {
		return _maxColIndex;
	}

	/**
	 * シートのデータのある最大領域を取得する。
	 * シートにデータが存在しない場合は、{@link CellRect#isEmpty()} が <tt>true</tt> を返すオブジェクトを返す。
	 * @return	{@link CellRect} オブジェクト
	 */
	public CellRect getPhysicalSheetRect() {
		return _physicalSheetRect;
	}

	/**
	 * シートの左上を起点とする、データのある最大領域を取得する。
	 * シートにデータが存在しない場合は、{@link CellRect#isEmpty()} が <tt>true</tt> を返すオブジェクトを返す。
	 * @return	{@link CellRect} オブジェクト
	 */
	public CellRect getLogicalSheetRect() {
		return _logicalSheetRect;
	}

	@Override
	public int hashCode() {
		int h = _sheetIndex;
		h = 31 * h + _sheetName.hashCode();
		h = 31 * h + _numMergedResions;
		h = 31 * h + _firstRowIndex;
		h = 31 * h + _lastRowIndex;
		h = 31 * h + _numPhysicalRows;
		h = 31 * h + _minColIndex;
		h = 31 * h + _maxColIndex;
		h = 31 * h + _numMaxCols;
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof SheetInfo) {
			SheetInfo aInfo = (SheetInfo)obj;
			if (aInfo._sheetIndex == this._sheetIndex &&
				this._sheetName.equals(aInfo._sheetName) &&
				aInfo._numMergedResions == this._numMergedResions &&
				aInfo._firstRowIndex == this._firstRowIndex &&
				aInfo._lastRowIndex == this._lastRowIndex &&
				aInfo._numPhysicalRows == this._numPhysicalRows &&
				aInfo._minColIndex == this._minColIndex &&
				aInfo._maxColIndex == this._maxColIndex &&
				aInfo._numMaxCols == this._numMaxCols)
			{
				return true;
			}
		}
		
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
