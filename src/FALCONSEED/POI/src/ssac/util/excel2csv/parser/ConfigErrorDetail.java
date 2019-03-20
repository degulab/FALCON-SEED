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
 * @(#)ConfigErrorDetail.java	3.3.0	2016/04/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.parser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellAddress;

import ssac.util.excel2csv.poi.CellPosition;

/**
 * <code>[Excel to CSV]</code> 変換定義のエラー詳細情報。
 * <p>このオブジェクトは、不変とする。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class ConfigErrorDetail
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 警告の詳細情報であれば <tt>true</tt> **/
	private boolean			_warn;
	/** エラーのあるセル位置 **/
	private CellPosition	_cellpos;
	/** 詳細メッセージ **/
	private String			_message;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ConfigErrorDetail(CellPosition cellpos, String message) {
		this(false, cellpos, message);
	}
	
	public ConfigErrorDetail(boolean warn, CellPosition cellpos, String message) {
		_warn    = warn;
		_cellpos = cellpos;
		_message = message;
	}
	
	public ConfigErrorDetail(Cell cell, String message) {
		this(false, cell, message);
	}
	
	public ConfigErrorDetail(boolean warn, Cell cell, String message) {
		this(warn, new CellPosition(cell), message);
	}
	
	public ConfigErrorDetail(String sheetname, CellAddress celladdr, String message) {
		this(false, sheetname, celladdr, message);
	}
	
	public ConfigErrorDetail(boolean warn, String sheetname, CellAddress celladdr, String message) {
		this(warn, new CellPosition(sheetname, celladdr), message);
	}
	
	public ConfigErrorDetail(String sheetname, int rowIndex, int colIndex, String message) {
		this(false, sheetname, rowIndex, colIndex, message);
	}
	
	public ConfigErrorDetail(boolean warn, String sheetname, int rowIndex, int colIndex, String message) {
		this(warn, new CellPosition(sheetname, rowIndex, colIndex), message);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isWarn() {
		return _warn;
	}
	
	public CellPosition getCellPosition() {
		return _cellpos;
	}
	
	public String getSheetName() {
		return (_cellpos==null ? null : _cellpos.getSheetName());
	}
	
	public int getRowIndex() {
		return (_cellpos==null ? 0 : _cellpos.getRowIndex());
	}
	
	public int getColumnIndex() {
		return (_cellpos==null ? 0 : _cellpos.getColumnIndex());
	}
	
	public CellAddress getCellAddress() {
		return (_cellpos==null ? null : _cellpos.getCellAddress());
	}
	
	public String getMessage() {
		return _message;
	}

	//------------------------------------------------------------
	// Implements Object interfaces
	//------------------------------------------------------------

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		//--- level
		if (_warn)
			sb.append("Warning : ");
		else
			sb.append("Error : ");
		//--- location
		sb.append('[');
		if (_cellpos != null) {
			sb.append(_cellpos.toString());
		}
		sb.append("] ");
		//--- message
		sb.append(_message==null ? "Syntax error." : _message);
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
