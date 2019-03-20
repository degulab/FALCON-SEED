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
 * @(#)CellRect.java	3.3.0	2016/05/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.poi;




/**
 * セルの範囲を保持するクラス。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class CellRect implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** セル範囲の左上の行インデックス **/
	private int	_firstRowIndex;
	/** セル範囲の左上の列インデックス **/
	private int	_firstColIndex;
	/** セル範囲の行数 **/
	private int	_numRows;
	/** セル範囲の列数 **/
	private int	_numCols;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CellRect() {
		_firstRowIndex = 0;
		_firstColIndex = 0;
		_numRows = EtcPoiUtil.MAX_OVER2007_ROWS;
		_numCols = EtcPoiUtil.MAX_OVER2007_COLS;
	}
	
	public CellRect(int firstRowIndex, int firstColIndex, int rowCount, int colCount) {
		_firstRowIndex = firstRowIndex;
		_firstColIndex = firstColIndex;
		_numRows = (rowCount < 0 ? 0 : rowCount);
		_numCols = (colCount < 0 ? 0 : colCount);
	}
	
	public CellRect(final CellRect src) {
		_firstRowIndex = src._firstRowIndex;
		_firstColIndex = src._firstColIndex;
		_numRows       = src._numRows;
		_numCols       = src._numCols;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return (_numRows <= 0 || _numCols <= 0);
	}
	
	public int getFirstRowIndex() {
		return _firstRowIndex;
	}
	
	public int getLastRowIndex() {
		return (_firstRowIndex + _numRows - 1);
	}
	
	public int getRowCount() {
		return _numRows;
	}
	
	public int getFirstColumnIndex() {
		return _firstColIndex;
	}
	
	public int getLastColumnIndex() {
		return (_firstColIndex + _numCols - 1);
	}
	
	public int getColumnCount() {
		return _numCols;
	}
	
	public CellIndex getFirstCellIndex() {
		return new CellIndex(_firstRowIndex, _firstColIndex);
	}
	
	public void getFirstCellPosition(CellIndex cellpos) {
		cellpos.setPosition(_firstRowIndex, _firstColIndex);
	}
	
	public CellIndex getLastCellPosition() {
		return new CellIndex(getLastRowIndex(), getLastColumnIndex());
	}
	
	public void getLastCellPosition(CellIndex cellpos) {
		cellpos.setPosition(getLastRowIndex(), getLastColumnIndex());
	}
	
	public void getRect(CellRect dst) {
		dst._firstRowIndex = _firstRowIndex;
		dst._firstColIndex = _firstColIndex;
		dst._numRows       = _numRows;
		dst._numCols       = _numCols;
	}
	
	public void setRect(final CellRect src) {
		_firstRowIndex = src._firstRowIndex;
		_firstColIndex = src._firstColIndex;
		_numRows       = src._numRows;
		_numCols       = src._numCols;
	}
	
	public void setRect(int firstRowIndex, int firstColIndex, int rowCount, int colCount) {
		_firstRowIndex = firstRowIndex;
		_firstColIndex = firstColIndex;
		_numRows = (rowCount < 0 ? 0 : rowCount);
		_numCols = (colCount < 0 ? 0 : colCount);
	}
	
	public void setCorner(int firstRowIndex, int firstColIndex, int lastRowIndex, int lastColIndex) {
		_firstRowIndex = firstRowIndex;
		_firstColIndex = firstColIndex;
		_numRows = (lastRowIndex < firstRowIndex ? 0 : lastRowIndex - firstRowIndex + 1);
		_numCols = (lastColIndex < firstColIndex ? 0 : lastColIndex - firstColIndex + 1);
	}
	
	public void setPosition(int firstRowIndex, int firstColIndex) {
		_firstRowIndex = firstRowIndex;
		_firstColIndex = firstColIndex;
	}
	
	public void setPosition(CellIndex firstCellIndex) {
		setPosition(firstCellIndex.getRowIndex(), firstCellIndex.getColumnIndex());
	}
	
	public void stretchFirstRow(int newFirstRowIndex) {
		int lastRowIndex = getLastRowIndex();
		_numRows = (lastRowIndex < newFirstRowIndex ? 0 : lastRowIndex - newFirstRowIndex + 1);
		_firstRowIndex = newFirstRowIndex;
	}
	
	public void stretchLastRow(int newLastRowIndex) {
		_numRows = (newLastRowIndex < _firstRowIndex ? 0 : newLastRowIndex - _firstRowIndex + 1);
	}
	
	public void stretchRowCount(int newRowCount) {
		_numRows = (newRowCount < 0 ? 0 : newRowCount);
	}
	
	public void stretchFirstColumn(int newFirstColIndex) {
		int lastColIndex = getLastColumnIndex();
		_numCols = (lastColIndex < newFirstColIndex ? 0 : lastColIndex - newFirstColIndex + 1);
		_firstColIndex = newFirstColIndex;
	}
	
	public void stretchLastColumn(int newLastColIndex) {
		_numCols = (newLastColIndex < _firstColIndex ? 0 : newLastColIndex - _firstColIndex + 1);
	}
	
	public void stretchColumnCount(int newColCount) {
		_numCols = (newColCount < 0 ? 0 : newColCount);
	}

	/**
	 * このオブジェクトと指定された領域とが交差するかどうかを判定する。
	 * 共通部分が空でない場合、2 つの領域は交差する。
	 * @param rect	判定する領域の一方
	 * @return	交差する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean intersects(CellRect rect) {
		int th = this._numRows;
		int tw = this._numCols;
		int rh = rect._numRows;
		int rw = rect._numCols;
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}
		
		int ty = this._firstRowIndex;
		int tx = this._firstColIndex;
		int ry = rect._firstRowIndex;
		int rx = rect._firstColIndex;
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		return ((rw < rx || rw > tx) &&
				(rh < ry || rh > ty) &&
				(tw < tx || tw > rx) &&
				(th < ty || th > ry));
	}

	/**
	 * この領域と指定された領域との共通部分を算出し、2 つの領域の共通部分を表す新しい領域を返す。
	 * 2 つの領域が交差しない場合、空の領域を返す。
	 * @param rect	領域の一方
	 * @return	指定された領域とこの領域の両方に収まる最大の領域、交差しない場合は空の領域
	 */
	public CellRect intersection(CellRect rect) {
		int ty1 = this._firstRowIndex;
		int tx1 = this._firstColIndex;
		int ry1 = rect._firstRowIndex;
		int rx1 = rect._firstColIndex;
		long ty2 = ty1; ty2 += this._numRows;
		long tx2 = tx1; tx2 += this._numCols;
		long ry2 = ry1; ry2 += rect._numRows;
		long rx2 = rx1; rx2 += rect._numCols;
		if (tx1 < rx1) tx1 = rx1;
		if (ty1 < ry1) ty1 = ry1;
		if (tx2 > rx2) tx2 = rx2;
		if (ty2 > ry2) ty2 = ry2;
		tx2 -= tx1;
		ty2 -= ty1;
		if (tx2 < Integer.MIN_VALUE) tx2 = Integer.MIN_VALUE;
		if (ty2 < Integer.MIN_VALUE) ty2 = Integer.MIN_VALUE;
		return new CellRect(ty1, tx1, (int)ty2, (int)tx2);
	}

	@Override
	public CellRect clone() {
		try {
			return (CellRect)super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new AssertionError();
		}
	}

	@Override
	public int hashCode() {
		int h = _firstRowIndex;
		h = 31 * h + _firstColIndex;
		h = 31 * h + _numRows;
		h = 31 * h + _numCols;
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj != null && obj.getClass().equals(this.getClass())) {
			CellRect aValue = (CellRect)obj;
			if (aValue._firstRowIndex==this._firstRowIndex &&
				aValue._firstColIndex==this._firstColIndex &&
				aValue._numRows==this._numRows &&
				aValue._numCols==this._numCols)
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[[");
		sb.append(_firstRowIndex);
		sb.append(',');
		sb.append(_firstColIndex);
		sb.append("], rows=");
		sb.append(_numRows);
		sb.append(", columns=");
		sb.append(_numCols);
		sb.append(']');
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
