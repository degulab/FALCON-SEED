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
 * @(#)CellPosision.java	3.3.0	2016/05/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;

import ssac.util.Objects;

/**
 * <code>Excel</code> における、シート名とそのセル位置を保持するクラス。
 * <p>このオブジェクトは、不変オブジェクトとする。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class CellPosition
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** シート名 **/
	private String		_sheetname;
	/** 行インデックス **/
	private int			_rowIndex;
	/** 列インデックス **/
	private int			_colIndex;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CellPosition(String sheetname, int rowIndex, int colIndex) {
		_sheetname = sheetname;
		_rowIndex  = rowIndex;
		_colIndex  = colIndex;
	}
	
	public CellPosition(String sheetname, CellAddress celladdr) {
		this(sheetname, celladdr.getRow(), celladdr.getColumn());
	}
	
	public CellPosition(Cell cell) {
		this(cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getSheetName() {
		return _sheetname;
	}
	
	public int getRowIndex() {
		return _rowIndex;
	}
	
	public int getColumnIndex() {
		return _colIndex;
	}
	
	public CellAddress getCellAddress() {
		return new CellAddress(_rowIndex, _colIndex);
	}
	
	public String formatCellAddress() {
		StringBuilder sb = new StringBuilder();
		if (_sheetname != null) {
			sb.append(_sheetname);
			sb.append('!');
		}
		if (_colIndex < 0) {
			sb.append("??");
		} else {
			sb.append(CellReference.convertNumToColString(_colIndex));
		}
		sb.append(_rowIndex+1);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		int h = (_sheetname==null ? 0 : _sheetname.hashCode());
		h = 31 * h + _rowIndex;
		h = 31 * h + _colIndex;
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj instanceof CellPosition) {
			CellPosition apos = (CellPosition)obj;
			if (Objects.isEqual(apos._sheetname, _sheetname) && apos._rowIndex==_rowIndex && apos._colIndex==_colIndex) {
				return true;
			}
		}
		
		// not equals
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (_sheetname != null) {
			sb.append(_sheetname);
			sb.append('!');
		}
		//--- excel formed cell address
		if (_colIndex < 0) {
			sb.append("??");
		} else {
			sb.append(CellReference.convertNumToColString(_colIndex));
		}
		sb.append(_rowIndex+1);
		//--- normal cell index
		sb.append('(');
		sb.append(_rowIndex);
		sb.append(',');
		sb.append(_colIndex);
		sb.append(')');
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
