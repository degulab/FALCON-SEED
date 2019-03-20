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
