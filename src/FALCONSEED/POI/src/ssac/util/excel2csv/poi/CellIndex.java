/*
 * @(#)CellIndex.java	3.3.0	2016/05/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.poi;

/**
 * セルの位置を保持するクラス。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class CellIndex implements Cloneable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 行インデックス **/
	private int	_rowIndex;
	/** 列インデックス **/
	private int	_colIndex;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CellIndex() {
		_rowIndex = 0;
		_colIndex = 0;
	}
	
	public CellIndex(int rowIndex, int colIndex) {
		_rowIndex = rowIndex;
		_colIndex = colIndex;
	}
	
	public CellIndex(final CellIndex src) {
		_rowIndex = src._rowIndex;
		_colIndex = src._colIndex;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getRowIndex() {
		return _rowIndex;
	}
	
	public int getColumnIndex() {
		return _colIndex;
	}
	
	public void setRowIndex(int rowIndex) {
		_rowIndex = rowIndex;
	}
	
	public void setColumnIndex(int colIndex) {
		_colIndex = colIndex;
	}
	
	public void getPosition(CellIndex dst) {
		dst._rowIndex = _rowIndex;
		dst._colIndex = _colIndex;
	}
	
	public void setPosition(final CellIndex src) {
		_rowIndex = src._rowIndex;
		_colIndex = src._colIndex;
	}
	
	public void setPosition(int rowIndex, int colIndex) {
		_rowIndex = rowIndex;
		_colIndex = colIndex;
	}

	@Override
	public CellIndex clone() {
		try {
			return (CellIndex)super.clone();
		}
		catch (CloneNotSupportedException ex) {
			throw new AssertionError();
		}
	}

	@Override
	public int hashCode() {
		int h = _rowIndex;
		h = 31 * h + _colIndex;
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj != null && obj.getClass().equals(this.getClass())) {
			CellIndex aValue = (CellIndex)obj;
			if (aValue._rowIndex==this._rowIndex && aValue._colIndex==this._colIndex) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(_rowIndex);
		sb.append(',');
		sb.append(_colIndex);
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
