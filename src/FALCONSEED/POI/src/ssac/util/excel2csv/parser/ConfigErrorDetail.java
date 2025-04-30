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
