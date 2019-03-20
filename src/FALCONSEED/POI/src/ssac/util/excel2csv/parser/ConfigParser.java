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
 * @(#)ConfigParser.java	3.3.0	2016/05/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import ssac.util.excel2csv.EtcConfigDataSet;
import ssac.util.excel2csv.EtcMessages;
import ssac.util.excel2csv.EtcWorkbookManager;
import ssac.util.excel2csv.command.CmdCardFormNode;
import ssac.util.excel2csv.command.CmdCardTableNode;
import ssac.util.excel2csv.command.CmdCsvFieldCellNode;
import ssac.util.excel2csv.command.CmdCsvFieldColumnNode;
import ssac.util.excel2csv.command.CmdCsvFieldFixedNode;
import ssac.util.excel2csv.command.CmdCsvFileNode;
import ssac.util.excel2csv.command.CmdCsvRecordFixedNode;
import ssac.util.excel2csv.command.CmdGroupColumnsNode;
import ssac.util.excel2csv.command.CmdParamValue;
import ssac.util.excel2csv.command.CmdTableFormNode;
import ssac.util.excel2csv.command.CommandBlockNode;
import ssac.util.excel2csv.command.option.CoordCell;
import ssac.util.excel2csv.command.option.CoordInteger;
import ssac.util.excel2csv.command.option.CoordValue;
import ssac.util.excel2csv.command.option.OptionValue;
import ssac.util.excel2csv.poi.CellIndex;
import ssac.util.excel2csv.poi.CellPosition;
import ssac.util.excel2csv.poi.CellRect;
import ssac.util.excel2csv.poi.EtcPoiUtil;
import ssac.util.excel2csv.poi.SheetInfo;

/**
 * <code>[Excel to CSV]</code> 変換定義のパーサー。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class ConfigParser
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 変換定義解析におけるエラー詳細の最大数、この数を超えたエラーが発生した場合は例外がスローされる **/
	static private final int	MAX_ERROR_LIMIT		= 100;
	
	static private final Set<String>	_cmdset;
	static {
		_cmdset = new TreeSet<String>();
		_cmdset.add(CmdCardFormNode.CMD_BEGIN);
		_cmdset.add(CmdCardFormNode.CMD_END);
		_cmdset.add(CmdCardTableNode.CMD_BEGIN);
		_cmdset.add(CmdCardTableNode.CMD_END);
		_cmdset.add(CmdCsvFieldCellNode.CMD_NAME);
		_cmdset.add(CmdCsvFieldColumnNode.CMD_NAME);
		_cmdset.add(CmdCsvFieldFixedNode.CMD_NAME);
		_cmdset.add(CmdCsvFileNode.CMD_BEGIN);
		_cmdset.add(CmdCsvFileNode.CMD_END);
		_cmdset.add(CmdCsvRecordFixedNode.CMD_NAME);
		_cmdset.add(CmdGroupColumnsNode.CMD_BEGIN);
		_cmdset.add(CmdGroupColumnsNode.CMD_END);
		_cmdset.add(CmdTableFormNode.CMD_BEGIN);
		_cmdset.add(CmdTableFormNode.CMD_END);
	}

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** Excel ワークブックマネージャー **/
	private EtcWorkbookManager		_workbook;
	/** 解析エラー詳細情報のリスト **/
	private ConfigErrorList			_errlist = new ConfigErrorList(MAX_ERROR_LIMIT);

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ConfigParser(EtcWorkbookManager workbook) throws ConfigSheetNotFoundException
	{
		if (!workbook.isOpened())
			throw new IllegalArgumentException("Excel workbook is not opened.");
		if (workbook.isConfigSheetsEmpty())
			throw new ConfigSheetNotFoundException(EtcMessages.getInstance().errConfigSheetNotFound);
		_workbook = workbook;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ConfigErrorList getErrorList() {
		return _errlist;
	}
	
	public EtcConfigDataSet parse() throws ConfigException
	{
		EtcConfigDataSet cmdlist = new EtcConfigDataSet();
		for (SheetInfo cinfo : _workbook.getConfigSheetInfos()) {
			// シートの取得
			ConfigSheetReader reader = new ConfigSheetReader(_workbook.getWorkbook().getSheetAt(cinfo.getSheetIndex()));
			
			// 1シート分の解析
			for (Row cmdrow = reader.nextCommandRow(); cmdrow != null; cmdrow = reader.nextCommandRow()) {
				String cmdname = reader.getCurrentCommandName();
				if (!CmdCsvFileNode.CMD_BEGIN.equals(cmdname)) {
					//--- トップレベルコマンドではないため、次のトップレベルコマンドまでスキップ
					_errlist.appendError(cmdrow.getCell(0), EtcMessages.getInstance().errConfigCmdCsvFileBeginNotFound);
					for (cmdrow = reader.nextCommandRow(); cmdrow != null; cmdrow = reader.nextCommandRow()) {
						cmdname = reader.getCurrentCommandName();
						if (CmdCsvFileNode.CMD_BEGIN.equals(cmdname)) {
							break;
						}
					}
					if (cmdrow == null) {
						break;
					}
				}
				//--- トップレベルコマンドのパース
				CmdCsvFileNode node = parseCsvFileCommandBlock(reader, cmdname, cmdrow);
				if (node != null) {
					cmdlist.add(node);
				}
			}
		}
		
		// check exist commands
		//--- ここではチェックしない

		cmdlist.trimToSize();
		return cmdlist;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

//	/**
//	 * 指定されたセルから、Excel の書式によりフォーマットされた文字列を取得する。
//	 * <p>なお、結合セルは考慮しない。
//	 * @param cell	対象のセル
//	 * @return	整形済み文字列、<em>cell</em> が <tt>null</tt> の場合は空文字列
//	 */
//	protected String getFormattedValue(Cell cell) {
//		try {
//			return _workbook.getCellFormatter().formatAsString(cell);
//		} catch (Throwable ex) {}
//		
//		// 例外が発生した場合は、Apache POI のフォーマッターを使用する。
//		try {
//			return _workbook.getPoiFormatter().formatCellValue(cell);
//		} catch (Throwable ex) {}
//		
//		// 例外が発生した場合は、Cell#toString() の結果とする
//		return (cell==null ? "" : cell.toString());
//	}

	/**
	 * 指定されたシート名もしくはシートパターンに一致するデータ用シートのワークブックインデックスを、指定されたセットに追加する。
	 * <em>sheetOption</em> の値が {@link Pattern} の場合、指定された正規表現に一致するシート名を検索する。
	 * @param indexmap		インデックスを格納するマップ
	 * @param sheetOption	シート名もしくはシートパターンを保持するオプション
	 * @return	シート名もしくはシートパターンに一致するシートが存在する場合は <tt>true</tt>、存在しない場合は <tt>false</tt>
	 * @throws NullPointerException		引数が <tt>null</tt> の場合
	 */
	protected boolean appendTargetSheetIndices(Map<Integer,SheetInfo> indexset, OptionValue sheetOption) {
		boolean found = false;
		SheetInfo[] sheets = _workbook.getDataSheetInfos();
		if (sheetOption.getValue() instanceof Pattern) {
			// Pattern
			Pattern inputpattern = (Pattern)sheetOption.getValue();
			for (SheetInfo sinfo : sheets) {
				if (inputpattern.matcher(sinfo.getSheetName()).matches()) {
					found = true;
					indexset.put(sinfo.getSheetIndex(), sinfo);
				}
			}
		}
		else {
			// String
			String inputname = sheetOption.getValue().toString();
			for (SheetInfo sinfo : sheets) {
				if (inputname.equals(sinfo.getSheetName())) {
					found = true;
					indexset.put(sinfo.getSheetIndex(), sinfo);
					break;	// 同一名は存在しない(はず)
				}
			}
		}
		
		return found;
	}

	/**
	 * コマンド行の第2列以降もしくは第3列以降が空かどうかをチェックする。
	 * 空ではない場合、警告を設定する。
	 * @param cmdrow			判定対象のコマンド行
	 * @param ignoreFromThird	第3列以降をチェックする場合は <tt>true</tt>、第2列以降をチェックする場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected void checkIgnoreCells(Row cmdrow, boolean ignoreFromThird) {
		for (int colIndex = (ignoreFromThird ? 2 : 1); colIndex < cmdrow.getLastCellNum(); ++colIndex) {
			Cell cell = cmdrow.getCell(colIndex);
			if (cell != null && !EtcPoiUtil.isBlankCellValue(cell)) {
				// no blank cell
				if (ignoreFromThird) {
					_errlist.appendWarn(cmdrow.getCell(0), EtcMessages.getInstance().warnConfigIgnoreAfterThirdColumn);
				} else {
					_errlist.appendWarn(cmdrow.getCell(0), EtcMessages.getInstance().warnConfigIgnoreAfterSecondColumn);
				}
				break;
			}
		}
	}

	/**
	 * コマンドブロック終端コマンド行でない場合に、エラー情報を出力する。
	 * @param reader			変換定義シートリーダー
	 * @param cmdrow			解析対象のコマンド行
	 * @param strExpectedName	期待する終端コマンド名
	 * @return	指定されたコマンドブロック終端コマンドなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws ConfigException	続行不可能な解析エラーが発生した場合
	 */
	protected boolean validBlockEndCommand(ConfigSheetReader reader, Row cmdrow, String strExpectedName) throws ConfigException
	{
		if (cmdrow == null) {
			// end command of command block does not found
			_errlist.appendError(reader.getCurrentSheet().getSheetName(), reader.getCurrentSheet().getLastRowNum()+1, 0,
					EtcMessages.getInstance().errConfigCommandBlockEndNotFound + " : " + strExpectedName);
			return false;
		}
		else if (!strExpectedName.equals(cmdrow.getCell(0).getStringCellValue())) {
			// unexpected command name
			_errlist.appendError(cmdrow.getCell(0),
					EtcMessages.getInstance().errConfigCommandBlockEndNotFound + " : " + strExpectedName);
			return false;
		}
		else {
			// ignore options
			//--- 第2列以降は、無視
			checkIgnoreCells(cmdrow, false);
			return true;
		}
	}

	/**
	 * 指定された 'column-header-rows' のオプション定義から、上位処理対象範囲からの相対ヘッダー行インデックスを取得し、
	 * <em>buffer</em> に格納する。ヘッダー行インデックスが重複する場合、最初に格納されたオプション値を保持する。
	 * @param buffer		行インデックスと対応するオプション値を格納するマップ
	 * @param optRowIndices	ヘッダー行インデックスのオプション値のリスト
	 * @param absParentRect	上位コマンドブロックの絶対処理対象範囲、上位コマンドブロックがない場合はシートの最大領域
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ConfigException		続行不可能な解析エラーが発生した場合
	 */
	protected void collectRelativeHeaderRowIndices(Map<Integer, OptionValue> buffer, List<OptionValue> optRowIndices, CellRect absParentRect)
	throws ConfigException
	{
		int rangeFirstRowIndex, rangeLastRowIndex;
		rangeFirstRowIndex = absParentRect.getFirstRowIndex();
		rangeLastRowIndex  = absParentRect.getLastRowIndex();
		for (OptionValue opt : optRowIndices) {
			assert (OptionValueParser.OPTION_COLUMN_HEADER_ROWS.equals(opt.getName()));
			CoordValue[] optvalue = (CoordValue[])opt.getValue();
			boolean warn = false;
			for (CoordValue coord : optvalue) {
				CoordInteger rowCoord;
				if (coord instanceof CoordCell) {
					// CoordCell
					rowCoord = ((CoordCell)coord).row();
				}
				else {
					// CoordInteger
					rowCoord = (CoordInteger)coord;
				}
				int rowIndex;
				if (rowCoord.isRelative()) {
					rowIndex = rangeFirstRowIndex + rowCoord.intValue();
				} else {
					rowIndex = rowCoord.intValue();
				}
				//--- check absolute row index
				if (rowIndex < 0 || rowIndex >= EtcPoiUtil.MAX_OVER2007_ROWS) {
					// error
					_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigParamRowPositionOutOfSheet);
					break;	// 次のオプションへ
				}
				//--- convert relative
				if (!warn && (rowIndex < rangeFirstRowIndex || rowIndex > rangeLastRowIndex)) {
					warn = true;
					_errlist.appendWarn(opt.getCellPosition(), EtcMessages.getInstance().warnConfigParamRowPositionOutOfRange);
				}
				rowIndex = rowIndex - rangeFirstRowIndex;
				//--- save row index
				if (!buffer.containsKey(rowIndex)) {
					buffer.put(rowIndex, opt);
				}
			}
		}
	}

	/**
	 * パラメータのセル位置を、相対セル位置に変換する。
	 * @param parampos		パラメータ値が格納されているセル位置
	 * @param paramvalue	パラメータ値
	 * @param absParentRect	上位コマンドブロックの絶対処理対象領域、上位コマンドブロックがない場合はシートの最大領域
	 * @return	上位処理対象領域からの相対セル位置
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ClassCastException	<em>paramvalue</em> が <code>CoordCell</code> オブジェクトではない場合
	 * @throws ConfigException		続行不可能な解析エラーが発生した場合
	 */
	protected CellIndex getRelativeCellPosition(CellPosition parampos, Object paramvalue, CellRect absParentRect)
	throws ConfigException
	{
		if (parampos == null || paramvalue == null || absParentRect == null)
			throw new NullPointerException();
		CoordCell cellpos = (CoordCell)paramvalue;
		CoordInteger rowpos = cellpos.row();
		CoordInteger colpos = cellpos.column();

		boolean rowError = false;
		long rowIndex = (rowpos.isAbsolute() ? rowpos.longValue() : rowpos.longValue() + absParentRect.getFirstRowIndex());
		if (rowIndex < 0L) {
			// error
			rowError = true;
			rowIndex = absParentRect.getFirstRowIndex();
		}
		else if (rowIndex >= EtcPoiUtil.MAX_OVER2007_ROWS) {
			// error
			rowError = true;
			rowIndex = absParentRect.getLastRowIndex();
		}
		else if (rowIndex < absParentRect.getFirstRowIndex() || rowIndex > absParentRect.getLastRowIndex()) {
			// warning
			_errlist.appendWarn(parampos, EtcMessages.getInstance().warnConfigParamRowPositionOutOfRange);
		}
		
		boolean colError = false;
		long colIndex = (colpos.isAbsolute() ? colpos.longValue() : colpos.longValue() + absParentRect.getFirstColumnIndex());
		if (colIndex < 0L) {
			// error
			colError = true;
			colIndex = absParentRect.getFirstColumnIndex();
		}
		else if (colIndex >= EtcPoiUtil.MAX_OVER2007_COLS) {
			// error
			colError = true;
			colIndex = absParentRect.getLastColumnIndex();
		}
		else if (colIndex < absParentRect.getFirstColumnIndex() || colIndex > absParentRect.getLastColumnIndex()) {
			// warning
			_errlist.appendWarn(parampos, EtcMessages.getInstance().warnConfigParamColPositionOutOfRange);
		}
		
		// error
		if (rowError && colError) {
			_errlist.appendError(parampos, EtcMessages.getInstance().errConfigParamCellPositionOutOfSheet);
		} else if (rowError) {
			_errlist.appendError(parampos, EtcMessages.getInstance().errConfigParamRowPositionOutOfSheet);
		} else if (colError) {
			_errlist.appendError(parampos, EtcMessages.getInstance().errConfigParamColPositionOutOfSheet);
		}
		
		return new CellIndex((int)(rowIndex - absParentRect.getFirstRowIndex()), (int)(colIndex - absParentRect.getFirstColumnIndex()));
	}
	
	/**
	 * パラメータの行位置を、相対行位置に変換する。
	 * @param parampos		パラメータ値が格納されているセル位置
	 * @param paramvalue	パラメータ値
	 * @param absParentRect	上位コマンドブロックの絶対処理対象領域、上位コマンドブロックがない場合はシートの最大領域
	 * @return	上位処理対象領域からの相対セル位置
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ClassCastException	<em>paramvalue</em> が <code>CoordCell</code> または <code>CoordInteger</code> オブジェクトではない場合
	 * @throws ConfigException		続行不可能な解析エラーが発生した場合
	 */
	protected int getRelativeRowPosition(CellPosition parampos, Object paramvalue, CellRect absParentRect)
	throws ConfigException
	{
		if (parampos == null || paramvalue == null || absParentRect == null)
			throw new NullPointerException();
		CoordInteger rowpos;
		if (paramvalue instanceof CoordCell) {
			rowpos = ((CoordCell)paramvalue).row();
		} else {
			rowpos = (CoordInteger)paramvalue;
		}
		
		long rowIndex;
		if (rowpos.isAbsolute()) {
			rowIndex = rowpos.longValue();
		} else {
			rowIndex = rowpos.longValue() + absParentRect.getFirstRowIndex();
		}
		if (rowIndex < 0L) {
			// error
			_errlist.appendError(parampos, EtcMessages.getInstance().errConfigParamRowPositionOutOfSheet);
			rowIndex = absParentRect.getFirstRowIndex();
		}
		else if (rowIndex >= EtcPoiUtil.MAX_OVER2007_ROWS) {
			// error
			_errlist.appendError(parampos, EtcMessages.getInstance().errConfigParamRowPositionOutOfSheet);
			rowIndex = absParentRect.getLastRowIndex();
		}
		else if (rowIndex < absParentRect.getFirstRowIndex() || rowIndex > absParentRect.getLastRowIndex()) {
			// warning
			_errlist.appendWarn(parampos, EtcMessages.getInstance().warnConfigParamRowPositionOutOfRange);
		}
		
		return (int)(rowIndex - absParentRect.getFirstRowIndex());
	}
	
	/**
	 * パラメータの列位置を、相対列位置に変換する。
	 * @param parampos		パラメータ値が格納されているセル位置
	 * @param paramvalue	パラメータ値
	 * @param absParentRect	上位コマンドブロックの絶対処理対象領域、上位コマンドブロックがない場合はシートの最大領域
	 * @return	上位処理対象領域からの相対セル位置
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws ClassCastException	<em>paramvalue</em> が <code>CoordCell</code> または <code>CoordInteger</code> オブジェクトではない場合
	 * @throws ConfigException		続行不可能な解析エラーが発生した場合
	 */
	protected int getRelativeColumnPosition(CellPosition parampos, Object paramvalue, CellRect absParentRect)
	throws ConfigException
	{
		if (parampos == null || paramvalue == null || absParentRect == null)
			throw new NullPointerException();
		CoordInteger colpos;
		if (paramvalue instanceof CoordCell) {
			colpos = ((CoordCell)paramvalue).column();
		} else {
			colpos = (CoordInteger)paramvalue;
		}
		
		long colIndex;
		if (colpos.isAbsolute()) {
			colIndex = colpos.longValue();
		} else {
			colIndex = colpos.longValue() + absParentRect.getFirstColumnIndex();
		}
		if (colIndex < 0L) {
			// error
			_errlist.appendError(parampos, EtcMessages.getInstance().errConfigParamColPositionOutOfSheet);
			colIndex = absParentRect.getFirstColumnIndex();
		}
		else if (colIndex >= EtcPoiUtil.MAX_OVER2007_COLS) {
			// error
			_errlist.appendError(parampos, EtcMessages.getInstance().errConfigParamColPositionOutOfSheet);
			colIndex = absParentRect.getLastColumnIndex();
		}
		else if (colIndex < absParentRect.getFirstColumnIndex() || colIndex > absParentRect.getLastColumnIndex()) {
			// warning
			_errlist.appendWarn(parampos, EtcMessages.getInstance().warnConfigParamColPositionOutOfRange);
		}
		
		return (int)(colIndex - absParentRect.getFirstColumnIndex());
	}

	/**
	 * <em>targetNode</em> のオプション値から、<em>targetNode</em> の相対処理対象領域を設定し、絶対処理対象領域を取得する。
	 * このメソッドは、以下のオプションから値を取得する。
	 * <ul>
	 * <li><code>first-cell</code>
	 * <li><code>last-cell</code>
	 * </ul>
	 * @param targetNode	対象のコマンドブロックノード
	 * @param parentNode	親のコマンドブロックノード
	 * @param absParentRect	親の絶対処理対象領域
	 * @return	オプション値から生成された絶対処理対象領域
	 * @throws NullPointerException	<em>targetNode</em> または <em>absParentRect</em> が <tt>null</tt> の場合
	 * @throws ConfigException		続行不可能な解析エラーが発生した場合
	 */
	protected CellRect getAndSetTargetCellAreaByOptions(CommandBlockNode targetNode, CommandBlockNode parentNode, CellRect absParentRect)
	throws ConfigException
	{
		OptionValue opt;
		CellIndex absFirstCellIndex = null;
		CellIndex relFirstCellIndex = null;
		long absLastRowIndex;
		long absLastColIndex;
		
		// first-cell
		if ((opt = targetNode.getLocalOption(OptionValueParser.OPTION_FIRST_CELL)) != null) {
			relFirstCellIndex = getRelativeCellPosition(opt.getCellPosition(), opt.getValue(), absParentRect);
			absFirstCellIndex = new CellIndex(absParentRect.getFirstRowIndex() + relFirstCellIndex.getRowIndex(),
											  absParentRect.getFirstColumnIndex() + relFirstCellIndex.getColumnIndex());
		}
		else {
			relFirstCellIndex = new CellIndex(0, 0);
			absFirstCellIndex = new CellIndex(absParentRect.getFirstRowIndex(), absParentRect.getFirstColumnIndex());
		}
		
		// last-cell
		if ((opt = targetNode.getLocalOption(OptionValueParser.OPTION_LAST_CELL)) != null) {
			CellIndex relLastCellIndex = getRelativeCellPosition(opt.getCellPosition(), opt.getValue(), absParentRect);
			absLastRowIndex = (long)absParentRect.getFirstRowIndex() + (long)relLastCellIndex.getRowIndex();
			absLastColIndex = (long)absParentRect.getFirstColumnIndex() + (long)relLastCellIndex.getColumnIndex();
			//--- check error
			if (absLastRowIndex < absFirstCellIndex.getRowIndex()) {
				if (absLastColIndex < absFirstCellIndex.getColumnIndex()) {
					// illegal cell position
					_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigParamCellPositionIllegal);
					absLastRowIndex  = absParentRect.getLastRowIndex();
					absLastColIndex  = absParentRect.getLastColumnIndex();
				} else {
					// illegal row position
					_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigParamRowPositionIllegal);
					absLastRowIndex  = absParentRect.getLastRowIndex();
				}
			}
			else if (absLastColIndex < absFirstCellIndex.getColumnIndex()) {
				// illegal column position
				_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigParamColPositionIllegal);
				absLastColIndex  = absParentRect.getLastColumnIndex();
			}
		}
		else {
			absLastRowIndex = absParentRect.getLastRowIndex();
			absLastColIndex = absParentRect.getLastColumnIndex();
		}
		
		// 相対行範囲の保存
		int rowCount = (int)(absLastRowIndex - absFirstCellIndex.getRowIndex() + 1);
		int colCount = (int)(absLastColIndex - absFirstCellIndex.getColumnIndex() + 1);
		targetNode.setRelativeTargetArea(relFirstCellIndex.getRowIndex(), relFirstCellIndex.getColumnIndex(), rowCount, colCount);
		
		// このノードの絶対処理対象領域
		CellRect nodeAbsTargetRange = new CellRect(absFirstCellIndex.getRowIndex(), absFirstCellIndex.getColumnIndex(), rowCount, colCount);
		return nodeAbsTargetRange;
	}
	
	/**
	 * <em>targetNode</em> のオプション値から、<em>targetNode</em> の相対処理対象領域を設定し、絶対処理対象領域を取得する。
	 * このメソッドは、以下のオプションから値を取得する。
	 * <ul>
	 * <li><code>first-row</code>
	 * <li><code>last-row</code>
	 * <li><code>row-count</code>
	 * </ul>
	 * @param targetNode	対象のコマンドブロックノード
	 * @param parentNode	親のコマンドブロックノード
	 * @param absParentRect	親の絶対処理対象領域
	 * @return	オプション値から生成された絶対処理対象領域
	 * @throws NullPointerException	<em>targetNode</em> または <em>absParentRect</em> が <tt>null</tt> の場合
	 * @throws ConfigException		続行不可能な解析エラーが発生した場合
	 */
	protected CellRect getAndSetTargetRowAreaByOptions(CommandBlockNode targetNode, CommandBlockNode parentNode, CellRect absParentRect)
	throws ConfigException
	{
		OptionValue opt;
		int  absFirstRowIndex;
		int  relFirstRowIndex;
		long absLastRowIndex = -1L;
		
		// first-row
		if ((opt = targetNode.getLocalOption(OptionValueParser.OPTION_FIRST_ROW)) != null) {
			relFirstRowIndex = getRelativeRowPosition(opt.getCellPosition(), opt.getValue(), absParentRect);
			absFirstRowIndex = relFirstRowIndex + absParentRect.getFirstRowIndex();
		}
		else {
			relFirstRowIndex = 0;
			absFirstRowIndex = absParentRect.getFirstRowIndex();
		}
		
		// last-row
		if ((opt = targetNode.getLocalOption(OptionValueParser.OPTION_LAST_ROW)) != null) {
			int rowIndex = getRelativeRowPosition(opt.getCellPosition(), opt.getValue(), absParentRect);
			absLastRowIndex = (long)absParentRect.getFirstRowIndex() + rowIndex;
			//--- check Error
			if (absLastRowIndex < absFirstRowIndex) {
				// illegal row position
				_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigParamRowPositionIllegal);
				absLastRowIndex = absParentRect.getLastRowIndex();
			}
		}
		
		// row-count
		if ((opt = targetNode.getLocalOption(OptionValueParser.OPTION_ROW_COUNT)) != null) {
			long rowpos = (Integer)opt.getValue();
			if (rowpos > 0L) {
				rowpos = absFirstRowIndex + rowpos - 1;
				if (rowpos >= EtcPoiUtil.MAX_OVER2007_ROWS) {
					// illegal row count
					_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigParamRowCountOutOfSheet);
				}
				else if (absLastRowIndex < 0L || absLastRowIndex < rowpos) {
					// row-count のオプション値有効
					absLastRowIndex = rowpos;
					if (absLastRowIndex > absParentRect.getLastRowIndex()) {
						// warning
						_errlist.appendWarn(opt.getCellPosition(), EtcMessages.getInstance().warnConfigParamRowCountOutOfRange);
					}
				}
				// else : row-count のオプション値無効
			}
			else {
				// illegal value
				_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigOptionIllegalValue);
			}
		}
		
		// last row index
		if (absLastRowIndex < 0L) {
			// last-row ならびに row-count の指定なし
			absLastRowIndex = absParentRect.getLastRowIndex();
		}
		int targetRowCount = (int)(absLastRowIndex - absFirstRowIndex + 1);
		
		// 相対行範囲の保存
		targetNode.setRelativeTargetArea(relFirstRowIndex, 0, targetRowCount, absParentRect.getColumnCount());
		
		// このノードの絶対処理対象領域
		CellRect nodeAbsTargetRange = new CellRect(absFirstRowIndex, absParentRect.getFirstColumnIndex(), targetRowCount, absParentRect.getColumnCount());
		return nodeAbsTargetRange;
	}
	
	/**
	 * <em>targetNode</em> のオプション値から、<em>targetNode</em> の相対処理対象領域を設定し、絶対処理対象領域を取得する。
	 * このメソッドは、以下のオプションから値を取得する。
	 * <ul>
	 * <li><code>first-column</code>
	 * <li><code>last-column</code>
	 * <li><code>column-count</code>
	 * </ul>
	 * @param targetNode	対象のコマンドブロックノード
	 * @param parentNode	親のコマンドブロックノード
	 * @param absParentRect	親の絶対処理対象領域
	 * @return	オプション値から生成された絶対処理対象領域
	 * @throws NullPointerException	<em>targetNode</em> または <em>absParentRect</em> が <tt>null</tt> の場合
	 * @throws ConfigException		続行不可能な解析エラーが発生した場合
	 */
	protected CellRect getAndSetTargetColumnAreaByOptions(CommandBlockNode targetNode, CommandBlockNode parentNode, CellRect absParentRect)
	throws ConfigException
	{
		OptionValue opt;
		int  absFirstColIndex;
		int  relFirstColIndex;
		long absLastColIndex = -1L;
		
		// first-column
		if ((opt = targetNode.getLocalOption(OptionValueParser.OPTION_FIRST_COLUMN)) != null) {
			relFirstColIndex = getRelativeColumnPosition(opt.getCellPosition(), opt.getValue(), absParentRect);
			absFirstColIndex = relFirstColIndex + absParentRect.getFirstColumnIndex();
		}
		else {
			relFirstColIndex = 0;
			absFirstColIndex = absParentRect.getFirstColumnIndex();
		}
		
		// last-column
		if ((opt = targetNode.getLocalOption(OptionValueParser.OPTION_LAST_COLUMN)) != null) {
			int colIndex = getRelativeColumnPosition(opt.getCellPosition(), opt.getValue(), absParentRect);
			absLastColIndex = (long)absParentRect.getFirstColumnIndex() + colIndex;
			//--- check Error
			if (absLastColIndex < absFirstColIndex) {
				// illegal column position
				_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigParamColPositionIllegal);
				absLastColIndex = absParentRect.getLastColumnIndex();
			}
		}
		
		// column-count
		if ((opt = targetNode.getLocalOption(OptionValueParser.OPTION_COLUMN_COUNT)) != null) {
			long colpos = (Integer)opt.getValue();
			if (colpos > 0L) {
				colpos = absFirstColIndex + colpos - 1;
				if (colpos >= EtcPoiUtil.MAX_OVER2007_COLS) {
					// illegal column count
					_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigParamColCountOutOfSheet);
				}
				else if (absLastColIndex < 0L || absLastColIndex < colpos) {
					// column-count のオプション値有効
					absLastColIndex = colpos;
					if (absLastColIndex > absParentRect.getLastColumnIndex()) {
						// warning
						_errlist.appendWarn(opt.getCellPosition(), EtcMessages.getInstance().warnConfigParamColCountOutOfRange);
					}
				}
				// else : column-count のオプション値無効
			}
			else {
				// illegal value
				_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigOptionIllegalValue);
			}
		}
		
		// last column index
		if (absLastColIndex < 0L) {
			// last-column ならびに column-count の指定なし
			absLastColIndex = absParentRect.getLastColumnIndex();
		}
		int targetColCount = (int)(absLastColIndex - absFirstColIndex + 1);
		
		// 相対列範囲の保存
		targetNode.setRelativeTargetArea(0, relFirstColIndex, absParentRect.getRowCount(), targetColCount);
		
		// このノードの絶対処理対象領域
		CellRect nodeAbsTargetRange = new CellRect(absParentRect.getFirstRowIndex(), absFirstColIndex, absParentRect.getRowCount(), targetColCount);
		return nodeAbsTargetRange;
	}

	/**
	 * '#csv-field-fixed' コマンドの解析。
	 * @param reader		変換定義シートリーダー
	 * @param cmdname		コマンド名
	 * @param cmdrow		解析対象の行
	 * @param parentNode	親コマンドブロックノード
	 * @param absParentRect	上位コマンドブロックの絶対処理対象範囲、上位コマンドブロックがない場合はシートの最大領域
	 * @return	生成されたコマンドノード、解析エラーにより生成されなかった場合は <tt>null</tt>
	 * @throws NullPointerException	<em>cmdrow</em> が <tt>null</tt> の場合
	 * @throws ConfigException	続行不可能な解析エラーが発生した場合
	 */
	protected CmdCsvFieldFixedNode parseCsvFieldFixedCommand(ConfigSheetReader reader, String cmdname, Row cmdrow,
															CommandBlockNode parentNode, CellRect absParentRect)
	throws ConfigException
	{
		assert (CmdCsvFieldFixedNode.CMD_NAME.equals(cmdname));

		CmdCsvFieldFixedNode node = new CmdCsvFieldFixedNode(new CellPosition(cmdrow.getCell(0)));
		
		//--- 第2列の値のみ取得
		node.setValue(_workbook.getFormattedExcelValue(cmdrow.getCell(1)));
		
		//--- 第3列以降は、無視
		checkIgnoreCells(cmdrow, true);
		
		//--- 完了
		return node;
	}

	/**
	 * '#csv-field-cell' コマンドの解析。
	 * @param reader		変換定義シートリーダー
	 * @param cmdname		コマンド名
	 * @param cmdrow		解析対象の行
	 * @param parentNode	親コマンドブロックノード
	 * @param absParentRect	上位コマンドブロックの絶対処理対象範囲、上位コマンドブロックがない場合はシートの最大領域
	 * @return	生成されたコマンドノード、解析エラーにより生成されなかった場合は <tt>null</tt>
	 * @throws NullPointerException	<em>cmdrow</em> が <tt>null</tt> の場合
	 * @throws ConfigException	続行不可能な解析エラーが発生した場合
	 */
	protected CmdCsvFieldCellNode parseCsvFieldCellCommand(ConfigSheetReader reader, String cmdname, Row cmdrow,
															CommandBlockNode parentNode, CellRect absParentRect)
	throws ConfigException
	{
		assert (CmdCsvFieldFixedNode.CMD_NAME.equals(cmdname));
		
		CmdCsvFieldCellNode node = new CmdCsvFieldCellNode(new CellPosition(cmdrow.getCell(0)));
		
		//--- 第2列の値を取得
		int colIndex = 1;
		CellPosition parampos = new CellPosition(cmdrow.getSheet().getSheetName(), cmdrow.getRowNum(), colIndex);
		Cell cell = cmdrow.getCell(colIndex);
		CmdParamValue paramvalue = null;
		String strValue = _workbook.getFormattedExcelValue(cell).trim();
		if (strValue.isEmpty()) {
			// blank
			paramvalue = new CmdParamValue(parampos, strValue);
		}
		//--- CellCoord
		if (paramvalue == null) {
			CoordCell cellcoord = OptionValueParser.parseCellCoordinate(strValue);
			if (cellcoord != null) {
				CellIndex index = getRelativeCellPosition(parampos, cellcoord, absParentRect);
				paramvalue = new CmdParamValue(parampos, index);
			}
		}
		//--- String literal
		if (paramvalue == null) {
			strValue = OptionValueParser.parseStringLiteral(strValue);
			if (strValue != null) {
				paramvalue = new CmdParamValue(parampos, strValue);
			}
		}
		//--- illegal value
		if (paramvalue == null) {
			_errlist.appendError(cell, EtcMessages.getInstance().errConfigCommandParamIllegalValue);
			paramvalue = new CmdParamValue(parampos, null);
		}
		node.setFieldParameter(paramvalue);
		
		//--- 第3列以降はオプション
		int maxColumns = cmdrow.getLastCellNum();
		++colIndex;
		for (; colIndex < maxColumns; ++colIndex) {
			cell = cmdrow.getCell(colIndex);
			OptionValue opt;
			try {
				opt = OptionValueParser.parseOption(cell, CmdCsvFieldCellNode.ALLOW_OPTIONS);
			} catch (ConfigRecognitionException ex) {
				opt = null;
				_errlist.appendErrorDetail(ex.getDetail());
			}
			if (opt == null)
				continue;	// no option
			
			// check multiple
			if (node.containsLocalOption(opt.getName())) {
				// options are not allowed multiple
				_errlist.appendError(cell, EtcMessages.getInstance().errConfigOptionAlreadySpecified);
			} else {
				// valid option
				node.putOption(opt);
			}
		}
		
		//--- 完了
		return node;
	}

	/**
	 * '#csv-field-column' コマンドの解析。
	 * @param reader		変換定義シートリーダー
	 * @param cmdname		コマンド名
	 * @param cmdrow		解析対象の行
	 * @param parentNode	親コマンドブロックノード
	 * @param absParentRect	上位コマンドブロックの絶対処理対象範囲、上位コマンドブロックがない場合はシートの最大領域
	 * @return	生成されたコマンドノード、解析エラーにより生成されなかった場合は <tt>null</tt>
	 * @throws NullPointerException	<em>cmdrow</em> が <tt>null</tt> の場合
	 * @throws ConfigException	続行不可能な解析エラーが発生した場合
	 */
	protected CmdCsvFieldColumnNode parseCsvFieldColumnCommand(ConfigSheetReader reader, String cmdname, Row cmdrow,
																CommandBlockNode parentNode, CellRect absParentRect)
	throws ConfigException
	{
		assert (CmdCsvFieldColumnNode.CMD_NAME.equals(cmdname));
		
		CmdCsvFieldColumnNode node = new CmdCsvFieldColumnNode(new CellPosition(cmdrow.getCell(0)));
		
		//--- 第2列の値を取得
		int colIndex = 1;
		CellPosition parampos = new CellPosition(cmdrow.getSheet().getSheetName(), cmdrow.getRowNum(), colIndex);
		Cell cell = cmdrow.getCell(colIndex);
		CmdParamValue paramvalue = null;
		String strValue = _workbook.getFormattedExcelValue(cell).trim();
		if (strValue.isEmpty()) {
			// blank
			paramvalue = new CmdParamValue(parampos, strValue);
		}
		//--- CellCoord
		if (paramvalue == null) {
			CoordCell cellcoord = OptionValueParser.parseCellCoordinate(strValue);
			if (cellcoord != null) {
				int index = getRelativeColumnPosition(parampos, cellcoord, absParentRect);
				paramvalue = new CmdParamValue(parampos, index);
			}
		}
		//--- Column index
		if (paramvalue == null) {
			CoordInteger colcoord = OptionValueParser.parseColumnCoordinate(strValue);
			if (colcoord != null) {
				int index = getRelativeColumnPosition(parampos, colcoord, absParentRect);
				paramvalue = new CmdParamValue(parampos, index);
			}
		}
		//--- String literal
		if (paramvalue == null) {
			strValue = OptionValueParser.parseStringLiteral(strValue);
			if (strValue != null) {
				paramvalue = new CmdParamValue(parampos, strValue);
			}
		}
		//--- illegal value
		if (paramvalue == null) {
			_errlist.appendError(cell, EtcMessages.getInstance().errConfigCommandParamIllegalValue);
			paramvalue = new CmdParamValue(parampos, null);
		}
		node.setFieldParameter(paramvalue);
		
		//--- 第3列以降はオプション
		int maxColumns = cmdrow.getLastCellNum();
		++colIndex;
		for (; colIndex < maxColumns; ++colIndex) {
			cell = cmdrow.getCell(colIndex);
			OptionValue opt;
			try {
				opt = OptionValueParser.parseOption(cell, CmdCsvFieldColumnNode.ALLOW_OPTIONS);
			} catch (ConfigRecognitionException ex) {
				opt = null;
				_errlist.appendErrorDetail(ex.getDetail());
			}
			if (opt == null)
				continue;	// no option
			
			// check multiple
			if (node.containsLocalOption(opt.getName())) {
				// options are not allowed multiple
				_errlist.appendError(cell, EtcMessages.getInstance().errConfigOptionAlreadySpecified);
			} else {
				// valid option
				node.putOption(opt);
			}
		}
		
		//--- 完了
		return node;
	}
	
	/**
	 * '#csv-record-fixed' コマンドの解析。
	 * @param reader		変換定義シートリーダー
	 * @param cmdname		コマンド名
	 * @param cmdrow		解析対象の行
	 * @param parentNode	親コマンドブロックノード
	 * @param absParentRect	上位コマンドブロックの絶対処理対象範囲、上位コマンドブロックがない場合はシートの最大領域
	 * @return	生成されたコマンドノード、解析エラーにより生成されなかった場合は <tt>null</tt>
	 * @throws NullPointerException	<em>cmdrow</em> が <tt>null</tt> の場合
	 * @throws ConfigException	続行不可能な解析エラーが発生した場合
	 */
	protected CmdCsvRecordFixedNode parseCsvRecordFixedCommand(ConfigSheetReader reader, String cmdname, Row cmdrow,
																CommandBlockNode parentNode, CellRect absParentRect)
	throws ConfigException
	{
		assert (CmdCsvRecordFixedNode.CMD_NAME.equals(cmdname));

		CmdCsvRecordFixedNode node = new CmdCsvRecordFixedNode(new CellPosition(cmdrow.getCell(0)));
		
		//--- 第2列以降は、Excelの表示通り(BLANK も同様)
		int maxColumns = cmdrow.getLastCellNum();
		ArrayList<String> valuelist = new ArrayList<String>();
		for (int colIndex = 1; colIndex < maxColumns; ++colIndex) {
			String strValue = _workbook.getFormattedExcelValue(cmdrow.getCell(colIndex));
			valuelist.add(strValue);
		}
		node.setValues(valuelist.toArray(new String[valuelist.size()]));
		
		return node;
	}

	/**
	 * '#csv-file' コマンドブロック(トップレベルコマンドブロック)の解析。
	 * @param reader		変換定義シートリーダー
	 * @param cmdname		コマンド名
	 * @param cmdrow		解析対象の行
	 * @return	生成されたコマンドノード
	 * @throws NullPointerException	<em>cmdrow</em> が <tt>null</tt> の場合
	 * @throws ConfigException	続行不可能な解析エラーが発生した場合
	 */
	protected CmdCsvFileNode parseCsvFileCommandBlock(ConfigSheetReader reader, String cmdname, Row cmdrow)
	{
		assert (CmdCsvFileNode.CMD_BEGIN.equals(cmdname));

		CellRect absSheetRange = new CellRect(0, 0, EtcPoiUtil.MAX_OVER2007_ROWS, EtcPoiUtil.MAX_OVER2007_COLS);
		Cell cmdcell = cmdrow.getCell(0);
		CmdCsvFileNode node = new CmdCsvFileNode(new CellPosition(cmdcell));
		int colIndex = 1;
		int maxColumns   = cmdrow.getLastCellNum();
		Cell cell;

		// タイトル・パラメータ
		cell = cmdrow.getCell(colIndex++);
		String strTitle;
		if (cell != null && !EtcPoiUtil.isBlankCellValue(cell)) {
			strTitle = _workbook.getFormattedExcelValue(cell);
		} else {
			strTitle = "";
		}
		node.setTitleParameter(new CmdParamValue(new CellPosition(cell), strTitle));
		
		// 標準ファイル名パラメータ
		cell = cmdrow.getCell(colIndex++);
		String strFilename;
		if (cell != null && !EtcPoiUtil.isBlankCellValue(cell)) {
			strFilename = _workbook.getFormattedExcelValue(cell);
		} else {
			strFilename = "";
		}
		node.setFilenameParameter(new CmdParamValue(new CellPosition(cell), strFilename));
		
		// オプション
		for (; colIndex < maxColumns; ++colIndex) {
			cell = cmdrow.getCell(colIndex);
			OptionValue opt;
			try {
				opt = OptionValueParser.parseOption(cell, CmdCsvFileNode.ALLOW_OPTIONS);
			} catch (ConfigRecognitionException ex) {
				opt = null;
				_errlist.appendErrorDetail(ex.getDetail());
			}
			if (opt == null)
				continue;	// no option
			
			// check multiple
			if (node.containsLocalOption(opt.getName())) {
				// options are not allowed multiple
				_errlist.appendError(cell, EtcMessages.getInstance().errConfigOptionAlreadySpecified);
			} else {
				// valid option
				node.putOption(opt);
			}
		}
		
		// ブロック内のコマンド解析
		boolean existTableForm = false;
		boolean existCardForm  = false;
		for (cmdrow = reader.nextCommandRow(); cmdrow != null; cmdrow = reader.nextCommandRow()) {
			cmdname = reader.getCurrentCommandName();
			if (cmdname.endsWith(CommandBlockNode.BLOCK_END_SUFFIX)) {
				// #XXXX-end
				break;
			}
			else if (CmdTableFormNode.CMD_BEGIN.equals(cmdname)) {
				// #table-form-begin
				if (existTableForm) {
					// already specified
					_errlist.appendError(cmdrow.getCell(0), EtcMessages.getInstance().errConfigCommandAlreadySpecified);
					continue;
				}
				existTableForm = true;
				CmdTableFormNode child = parseTableFormCommand(reader, cmdname, cmdrow, node, absSheetRange);
				node.addChild(child);
			}
			else if (CmdCardFormNode.CMD_BEGIN.equals(cmdname)) {
				// #card-form-begin
				if (existCardForm) {
					// already specified
					_errlist.appendError(cmdrow.getCell(0), EtcMessages.getInstance().errConfigCommandAlreadySpecified);
					continue;
				}
				existCardForm = true;
				CmdCardFormNode child = parseCardFormCommand(reader, cmdname, cmdrow, node, absSheetRange);
				node.addChild(child);
			}
			else if (CmdCsvRecordFixedNode.CMD_NAME.equals(cmdname)) {
				// #csv-record-fixed
				CmdCsvRecordFixedNode child = parseCsvRecordFixedCommand(reader, cmdname, cmdrow, node, absSheetRange);
				node.addChild(child);
			}
			else if (cmdname.endsWith(CommandBlockNode.BLOCK_BEGIN_SUFFIX)) {
				// #XXXX-begin
				break;
			}
			else {
				// Unsupported command
				_errlist.appendError(cmdrow.getCell(0), EtcMessages.getInstance().errConfigCommandUnsupported);
			}
		}
		
		// check '-end' command
		if (!validBlockEndCommand(reader, cmdrow, CmdCsvFileNode.CMD_END)) {
			// rewind for next command read
			reader.rewindToLastCommandRow();
		}
		else if (!existTableForm && !existCardForm) {
			// no required command
			_errlist.appendError(cmdcell, EtcMessages.getInstance().errConfigCommandNothingRequired
								+ "\n  : '" + CmdTableFormNode.CMD_NAME + "' or '" + CmdCardFormNode.CMD_NAME + "'");
		}
		
		// 終了
		return node;
	}
	
	/**
	 * '#table-form' コマンドの解析。
	 * @param reader		変換定義シートリーダー
	 * @param cmdname		コマンド名
	 * @param cmdrow		解析対象の行
	 * @param parentNode	親コマンドブロックノード
	 * @param absParentRect	上位コマンドブロックの絶対処理対象範囲、上位コマンドブロックがない場合はシートの最大領域
	 * @return	生成されたコマンドノード、解析エラーにより生成されなかった場合は <tt>null</tt>
	 * @throws NullPointerException	<em>cmdrow</em> が <tt>null</tt> の場合
	 * @throws ConfigException	続行不可能な解析エラーが発生した場合
	 */
	protected CmdTableFormNode parseTableFormCommand(ConfigSheetReader reader, String cmdname, Row cmdrow,
													CommandBlockNode parentNode, CellRect absParentRect)
	throws ConfigException
	{
		assert (CmdTableFormNode.CMD_BEGIN.equals(cmdname));

		CmdTableFormNode node = new CmdTableFormNode(new CellPosition(cmdrow.getCell(0)));
		int colIndex = 1;
		int maxColumns   = cmdrow.getLastCellNum();
		Cell cell;
		
		// オプション
		TreeMap<Integer, SheetInfo> targetSheetMap = new TreeMap<Integer, SheetInfo>();
		ArrayList<OptionValue> headerRowsList = new ArrayList<OptionValue>();
		for (; colIndex < maxColumns; ++colIndex) {
			cell = cmdrow.getCell(colIndex);
			OptionValue opt;
			try {
				opt = OptionValueParser.parseOption(cell, CmdTableFormNode.ALLOW_OPTIONS);
			} catch (ConfigRecognitionException ex) {
				opt = null;
				_errlist.appendErrorDetail(ex.getDetail());
			}
			if (opt == null)
				continue;	// no option
			
			// check options
			if (OptionValueParser.OPTION_SHEET.equals(opt.getName())) {
				// sheet option
				if (!appendTargetSheetIndices(targetSheetMap, opt)) {
					// no target sheet
					_errlist.appendError(cell, EtcMessages.getInstance().errConfigOptionSheetNotFound);
				}
			}
			else if (OptionValueParser.OPTION_COLUMN_HEADER_ROWS.equals(opt.getName())) {
				// column-header-rows option
				headerRowsList.add(opt);
			}
			else if (node.containsLocalOption(opt.getName())) {
				// options are not allowed multiple
				_errlist.appendError(cell, EtcMessages.getInstance().errConfigOptionAlreadySpecified);
			} else {
				// valid option
				node.putOption(opt);
			}
		}
		
		// 処理対象シートの保存
		SheetInfo[] targetSheets = targetSheetMap.values().toArray(new SheetInfo[targetSheetMap.size()]);
		if (targetSheets.length == 0) {
			// error
			_errlist.appendError(cmdrow.getCell(0), EtcMessages.getInstance().errConfigOptionNothingRequired
					+ "\n  : '" + OptionValueParser.OPTION_SHEET + "'");
		}
		node.setTargetSheets(targetSheets);
		
		// 列ヘッダー行の保存
		collectRelativeHeaderRowIndices(node.getHeaderRowOptionMap(), headerRowsList, absParentRect);
		
		// 処理対象領域の保存
		CellRect nodeAbsTargetRange = getAndSetTargetRowAreaByOptions(node, parentNode, absParentRect);
		
		// ブロック内のコマンド解析
		for (cmdrow = reader.nextCommandRow(); cmdrow != null; cmdrow = reader.nextCommandRow()) {
			cmdname = reader.getCurrentCommandName();
			if (cmdname.endsWith(CommandBlockNode.BLOCK_END_SUFFIX)) {
				// #XXXX-end
				break;
			}
			else if (CmdGroupColumnsNode.CMD_BEGIN.equals(cmdname)) {
				// #group-columns-begin
				CmdGroupColumnsNode child = parseGroupColumnsCommand(reader, cmdname, cmdrow, node, nodeAbsTargetRange);
				node.addChild(child);
			}
			else if (CmdCsvFieldFixedNode.CMD_NAME.equals(cmdname)) {
				// #csv-field-fixed
				CmdCsvFieldFixedNode child = parseCsvFieldFixedCommand(reader, cmdname, cmdrow, node, nodeAbsTargetRange);
				node.addChild(child);
			}
			else if (CmdCsvFieldColumnNode.CMD_NAME.equals(cmdname)) {
				// #csv-field-column
				CmdCsvFieldColumnNode child = parseCsvFieldColumnCommand(reader, cmdname, cmdrow, node, nodeAbsTargetRange);
				node.addChild(child);
			}
			else if (cmdname.endsWith(CommandBlockNode.BLOCK_BEGIN_SUFFIX)) {
				// #XXXX-begin
				break;
			}
			else {
				// Unsupported command
				_errlist.appendError(cmdrow.getCell(0), EtcMessages.getInstance().errConfigCommandUnsupported);
			}
		}
		
		// check '-end' command
		if (!validBlockEndCommand(reader, cmdrow, CmdTableFormNode.CMD_END)) {
			// rewind for next command read
			reader.rewindToLastCommandRow();
		}
		//--- no requires
		
		// 終了
		return node;
	}
	
	/**
	 * '#group-columns' コマンドの解析。
	 * @param reader		変換定義シートリーダー
	 * @param cmdname		コマンド名
	 * @param cmdrow		解析対象の行
	 * @param parentNode	親コマンドブロックノード
	 * @param absParentRect	上位コマンドブロックの絶対処理対象範囲、上位コマンドブロックがない場合はシートの最大領域
	 * @return	生成されたコマンドノード、解析エラーにより生成されなかった場合は <tt>null</tt>
	 * @throws NullPointerException	<em>cmdrow</em> が <tt>null</tt> の場合
	 * @throws ConfigException	続行不可能な解析エラーが発生した場合
	 */
	protected CmdGroupColumnsNode parseGroupColumnsCommand(ConfigSheetReader reader, String cmdname, Row cmdrow,
															CommandBlockNode parentNode, CellRect absParentRect)
	throws ConfigException
	{
		assert (CmdGroupColumnsNode.CMD_BEGIN.equals(cmdname));

		CmdGroupColumnsNode node = new CmdGroupColumnsNode(new CellPosition(cmdrow.getCell(0)));
		
		// オプション
		Cell cell;
		int maxColumns   = cmdrow.getLastCellNum();
		for (int colIndex = 1; colIndex < maxColumns; ++colIndex) {
			cell = cmdrow.getCell(colIndex);
			OptionValue opt;
			try {
				opt = OptionValueParser.parseOption(cell, CmdGroupColumnsNode.ALLOW_OPTIONS);
			} catch (ConfigRecognitionException ex) {
				opt = null;
				_errlist.appendErrorDetail(ex.getDetail());
			}
			if (opt == null)
				continue;	// no option
			
			// check options
			if (node.containsLocalOption(opt.getName())) {
				// options are not allowed multiple
				_errlist.appendError(cell, EtcMessages.getInstance().errConfigOptionAlreadySpecified);
			} else {
				// valid option
				node.putOption(opt);
			}
		}
		
		// 処理対象領域の保存
		CellRect nodeAbsTargetRange = getAndSetTargetColumnAreaByOptions(node, parentNode, absParentRect);
		
		// group-count の保存
		OptionValue opt;
		int groupcnt;
		if ((opt = node.getLocalOption(OptionValueParser.OPTION_GROUP_COUNT)) != null) {
			// group-count
			groupcnt = (Integer)opt.getValue();
			if (groupcnt > 0) {
				// check range
				if (groupcnt > nodeAbsTargetRange.getColumnCount()) {
					// このノードの処理対象領域外
					_errlist.appendWarn(opt.getCellPosition(), EtcMessages.getInstance().warnConfigParamColCountOutOfRange);
				}
			} else {
				// illegal value
				_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigOptionIllegalValue);
				groupcnt = 1;
			}
		} else {
			groupcnt = 1;
		}
		node.setGroupCount(groupcnt);
		
		// ブロック内のコマンド解析
		for (cmdrow = reader.nextCommandRow(); cmdrow != null; cmdrow = reader.nextCommandRow()) {
			cmdname = reader.getCurrentCommandName();
			if (cmdname.endsWith(CommandBlockNode.BLOCK_END_SUFFIX)) {
				// #XXXX-end
				break;
			}
			else if (CmdCsvFieldFixedNode.CMD_NAME.equals(cmdname)) {
				// #csv-field-fixed
				CmdCsvFieldFixedNode child = parseCsvFieldFixedCommand(reader, cmdname, cmdrow, node, nodeAbsTargetRange);
				node.addChild(child);
			}
			else if (CmdCsvFieldColumnNode.CMD_NAME.equals(cmdname)) {
				// #csv-field-column
				CmdCsvFieldColumnNode child = parseCsvFieldColumnCommand(reader, cmdname, cmdrow, node, nodeAbsTargetRange);
				node.addChild(child);
			}
			else if (cmdname.endsWith(CommandBlockNode.BLOCK_BEGIN_SUFFIX)) {
				// #XXXX-begin
				break;
			}
			else {
				// Unsupported command
				_errlist.appendError(cmdrow.getCell(0), EtcMessages.getInstance().errConfigCommandUnsupported);
			}
		}
		
		// check '-end' command
		if (!validBlockEndCommand(reader, cmdrow, CmdGroupColumnsNode.CMD_END)) {
			// rewind for next command read
			reader.rewindToLastCommandRow();
		}
		//--- no requires
		
		// 終了
		return node;
	}
	
	/**
	 * '#card-form' コマンドの解析。
	 * @param reader		変換定義シートリーダー
	 * @param cmdname		コマンド名
	 * @param cmdrow		解析対象の行
	 * @param parentNode	親コマンドブロックノード
	 * @param absParentRect	上位コマンドブロックの絶対処理対象範囲、上位コマンドブロックがない場合はシートの最大領域
	 * @return	生成されたコマンドノード、解析エラーにより生成されなかった場合は <tt>null</tt>
	 * @throws NullPointerException	<em>cmdrow</em> が <tt>null</tt> の場合
	 * @throws ConfigException	続行不可能な解析エラーが発生した場合
	 */
	protected CmdCardFormNode parseCardFormCommand(ConfigSheetReader reader, String cmdname, Row cmdrow,
													CommandBlockNode parentNode, CellRect absParentRect)
	throws ConfigException
	{
		assert (CmdCardFormNode.CMD_BEGIN.equals(cmdname));

		CmdCardFormNode node = new CmdCardFormNode(new CellPosition(cmdrow.getCell(0)));
		
		// オプション
		OptionValue opt;
		int maxColumns   = cmdrow.getLastCellNum();
		TreeMap<Integer, SheetInfo> targetSheetMap = new TreeMap<Integer, SheetInfo>();
		for (int colIndex = 1; colIndex < maxColumns; ++colIndex) {
			Cell cell = cmdrow.getCell(colIndex);
			try {
				opt = OptionValueParser.parseOption(cell, CmdCardFormNode.ALLOW_OPTIONS);
			} catch (ConfigRecognitionException ex) {
				opt = null;
				_errlist.appendErrorDetail(ex.getDetail());
			}
			if (opt == null)
				continue;	// no option
			
			// check options
			if (OptionValueParser.OPTION_SHEET.equals(opt.getName())) {
				// sheet option
				if (!appendTargetSheetIndices(targetSheetMap, opt)) {
					// no target sheet
					_errlist.appendError(cell, EtcMessages.getInstance().errConfigOptionSheetNotFound);
				}
			}
			else if (node.containsLocalOption(opt.getName())) {
				// options are not allowed multiple
				_errlist.appendError(cell, EtcMessages.getInstance().errConfigOptionAlreadySpecified);
			} else {
				// valid option
				node.putOption(opt);
			}
		}
		
		// 処理対象シートの保存
		SheetInfo[] targetSheets = targetSheetMap.values().toArray(new SheetInfo[targetSheetMap.size()]);
		if (targetSheets.length == 0) {
			// error
			_errlist.appendError(cmdrow.getCell(0), EtcMessages.getInstance().errConfigOptionNothingRequired
					+ "\n  : '" + OptionValueParser.OPTION_SHEET + "'");
		}
		node.setTargetSheets(targetSheets);
		
		// 処理対象領域の保存
		CellRect nodeAbsTargetRange = getAndSetTargetCellAreaByOptions(node, parentNode, absParentRect);
		
		// check row-spacing
		if ((opt = node.getLocalOption(OptionValueParser.OPTION_ROW_SPACING)) != null) {
			int spacing = (Integer)opt.getValue();
			if (spacing < 0) {
				_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigOptionIllegalValue);
			}
		}
		
		// check column-spacing
		if ((opt = node.getLocalOption(OptionValueParser.OPTION_COLUMN_SPACING)) != null) {
			int spacing = (Integer)opt.getValue();
			if (spacing < 0) {
				_errlist.appendError(opt.getCellPosition(), EtcMessages.getInstance().errConfigOptionIllegalValue);
			}
		}
		
		// ブロック内のコマンド解析
		for (cmdrow = reader.nextCommandRow(); cmdrow != null; cmdrow = reader.nextCommandRow()) {
			cmdname = reader.getCurrentCommandName();
			if (cmdname.endsWith(CommandBlockNode.BLOCK_END_SUFFIX)) {
				// #XXXX-end
				break;
			}
			else if (CmdCsvFieldFixedNode.CMD_NAME.equals(cmdname)) {
				// #csv-field-fixed
				CmdCsvFieldFixedNode child = parseCsvFieldFixedCommand(reader, cmdname, cmdrow, node, nodeAbsTargetRange);
				node.addChild(child);
			}
			else if (CmdCsvFieldCellNode.CMD_NAME.equals(cmdname)) {
				// #csv-field-cell
				CmdCsvFieldCellNode child = parseCsvFieldCellCommand(reader, cmdname, cmdrow, node, nodeAbsTargetRange);
				node.addChild(child);
			}
			else if (CmdCardTableNode.CMD_BEGIN.equals(cmdname)) {
				// #card-table
				CmdCardTableNode child = parseCardTableCommand(reader, cmdname, cmdrow, node, nodeAbsTargetRange);
				node.addChild(child);
			}
			else if (cmdname.endsWith(CommandBlockNode.BLOCK_BEGIN_SUFFIX)) {
				// #XXXX-begin
				break;
			}
			else {
				// Unsupported command
				_errlist.appendError(cmdrow.getCell(0), EtcMessages.getInstance().errConfigCommandUnsupported);
			}
		}
		
		// check '-end' command
		if (!validBlockEndCommand(reader, cmdrow, CmdCardFormNode.CMD_END)) {
			// rewind for next command block
			reader.rewindToLastCommandRow();
		}
		//--- no requires
		
		// 終了
		return node;
	}
	
	/**
	 * '#card-table' コマンドの解析。
	 * @param reader		変換定義シートリーダー
	 * @param cmdname		コマンド名
	 * @param cmdrow		解析対象の行
	 * @param parentNode	親コマンドブロックノード
	 * @param absParentRect	上位コマンドブロックの絶対処理対象範囲、上位コマンドブロックがない場合はシートの最大領域
	 * @return	生成されたコマンドノード、解析エラーにより生成されなかった場合は <tt>null</tt>
	 * @throws NullPointerException	<em>cmdrow</em> が <tt>null</tt> の場合
	 * @throws ConfigException	続行不可能な解析エラーが発生した場合
	 */
	protected CmdCardTableNode parseCardTableCommand(ConfigSheetReader reader, String cmdname, Row cmdrow,
													CommandBlockNode parentNode, CellRect absParentRect)
	throws ConfigException
	{
		assert (CmdCardTableNode.CMD_BEGIN.equals(cmdname));

		CmdCardTableNode node = new CmdCardTableNode(new CellPosition(cmdrow.getCell(0)));
		
		// オプション
		OptionValue opt;
		int maxColumns   = cmdrow.getLastCellNum();
		for (int colIndex = 1; colIndex < maxColumns; ++colIndex) {
			Cell cell = cmdrow.getCell(colIndex);
			try {
				opt = OptionValueParser.parseOption(cell, CmdCardTableNode.ALLOW_OPTIONS);
			} catch (ConfigRecognitionException ex) {
				opt = null;
				_errlist.appendErrorDetail(ex.getDetail());
			}
			if (opt == null)
				continue;	// no option
			
			// check options
			if (node.containsLocalOption(opt.getName())) {
				// options are not allowed multiple
				_errlist.appendError(cell, EtcMessages.getInstance().errConfigOptionAlreadySpecified);
			} else {
				// valid option
				node.putOption(opt);
			}
		}
		
		// 処理対象領域の保存
		CellRect nodeAbsTargetRange = getAndSetTargetCellAreaByOptions(node, parentNode, absParentRect);
		
		// ブロック内のコマンド解析
		for (cmdrow = reader.nextCommandRow(); cmdrow != null; cmdrow = reader.nextCommandRow()) {
			cmdname = reader.getCurrentCommandName();
			if (cmdname.endsWith(CommandBlockNode.BLOCK_END_SUFFIX)) {
				// #XXXX-end
				break;
			}
			else if (CmdCsvFieldFixedNode.CMD_NAME.equals(cmdname)) {
				// #csv-field-fixed
				CmdCsvFieldFixedNode child = parseCsvFieldFixedCommand(reader, cmdname, cmdrow, node, nodeAbsTargetRange);
				node.addChild(child);
			}
			else if (CmdCsvFieldColumnNode.CMD_NAME.equals(cmdname)) {
				// #csv-field-column
				CmdCsvFieldColumnNode child = parseCsvFieldColumnCommand(reader, cmdname, cmdrow, node, nodeAbsTargetRange);
				node.addChild(child);
			}
			else if (cmdname.endsWith(CommandBlockNode.BLOCK_BEGIN_SUFFIX)) {
				// #XXXX-begin
				break;
			}
			else {
				// Unsupported command
				_errlist.appendError(cmdrow.getCell(0), EtcMessages.getInstance().errConfigCommandUnsupported);
			}
		}
		
		// check '-end' command
		if (!validBlockEndCommand(reader, cmdrow, CmdCardTableNode.CMD_END)) {
			// rewind for next command read
			reader.rewindToLastCommandRow();
		}
		//--- no requires
		
		// 終了
		return node;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * 変換定義用シートのコマンドリーダー。
	 * @version 3.3.0
	 * @since 3.3.0
	 */
	protected class ConfigSheetReader
	{
		/** 現在の読み込み対象シートの最大行インデックス(このインデックスも含む) **/
		private int		_lastRowIndex	= -1;
		/** 次に読み込む行のシートにおけるインデックス **/
		private int		_nextRowIndex	= -1;
		/** 現在より一つ前のコマンド行インデックス **/
		private int		_prevCmdRowIndex = -1;
		/** 現在の読み込み対象シート **/
		private Sheet	_curSheet;
		/** 現在の読み込み対象行 **/
		private Row		_curRow;
		
		public ConfigSheetReader(Sheet sheet) {
			_curSheet        = sheet;
			_lastRowIndex    = sheet.getLastRowNum();
			_nextRowIndex    = sheet.getFirstRowNum();
			_prevCmdRowIndex = -1;
			_curRow          = null;
		}
		
		public Sheet getCurrentSheet() {
			return _curSheet;
		}
		
		public Row getCurrentCommandRow() {
			return _curRow;
		}
		
		public String getCurrentCommandName() {
			return (_curRow==null ? null : _curRow.getCell(0).getStringCellValue());
		}

		/**
		 * 現在の変換定義シートから、次のコマンド行を取得する。
		 * 空行およびコメント行は除外される。
		 * @return	コマンド名が記述された行、読み込む行が存在しない場合は <tt>null</tt>
		 * @throws ConfigException	解析エラーが発生し解析が停止した場合
		 */
		public Row nextCommandRow() throws ConfigException
		{
			for (; _nextRowIndex <= _lastRowIndex; ) {
				Row configrow = _curSheet.getRow(_nextRowIndex++);
				if (configrow == null)
					continue;	// nothing row
				if (configrow.getPhysicalNumberOfCells() <= 0)
					continue;	// nothing cells
				
				// check command cell
				Cell cmdcell = configrow.getCell(0);
				if (cmdcell == null || EtcPoiUtil.isBlankCellValue(cmdcell)) {
					// command unspecified
					_errlist.appendError(_curSheet.getSheetName(), configrow.getRowNum(), 0, EtcMessages.getInstance().errConfigCommandUnspecified);
				}
				else if (!EtcPoiUtil.isStringCellValue(cmdcell)) {
					// invalid command name
					_errlist.appendError(cmdcell, EtcMessages.getInstance().errConfigCommandUndefined);
				}
				else if (cmdcell.getStringCellValue().startsWith("##")) {
					// comment row, skip
				}
				else if (cmdcell.getStringCellValue().startsWith("#")) {
					// command row
					if (_cmdset.contains(cmdcell.getStringCellValue())) {
						// valid command
						if (_curRow != null) {
							_prevCmdRowIndex = _curRow.getRowNum();
						}
						_curRow = configrow;
						return configrow;
					}
					else {
						// undefined command
						_errlist.appendError(cmdcell, EtcMessages.getInstance().errConfigCommandUndefined);
					}
				}
				else {
					// invalid command name
					_errlist.appendError(cmdcell, EtcMessages.getInstance().errConfigCommandUndefined);
				}
			}
			
			// end of command rows in the sheet
			if (_curRow != null) {
				_prevCmdRowIndex = _curRow.getRowNum();
			}
			_curRow = null;
			return null;
		}

		/**
		 * 現在の行よりも前に読み込まれたコマンド行に戻す。
		 * このメソッドは、{@link #nextCommandRow()} の呼び出しの後に一度だけ実行可能。
		 * @throws IllegalStateException	このメソッド呼び出しが無効の場合
		 */
		public void rewindToLastCommandRow() {
			if (_curSheet == null || _prevCmdRowIndex < 0)
				throw new IllegalStateException();
			if (_curRow == null) {
				_nextRowIndex = _prevCmdRowIndex;
			} else {
				_nextRowIndex = _curRow.getRowNum();
				_curRow = _curSheet.getRow(_prevCmdRowIndex);
			}
			_prevCmdRowIndex = -1;
		}
	}
}
