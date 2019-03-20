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
 * @(#)CmdTableFormNode.java	3.3.0	2016/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command;

import java.io.IOException;
import java.util.TreeMap;

import ssac.util.excel2csv.EtcConversionSheet;
import ssac.util.excel2csv.EtcConvertProgressHandler;
import ssac.util.excel2csv.EtcCsvWriter;
import ssac.util.excel2csv.EtcException;
import ssac.util.excel2csv.EtcWorkbookManager;
import ssac.util.excel2csv.command.option.OptionValue;
import ssac.util.excel2csv.parser.OptionValueParser;
import ssac.util.excel2csv.poi.CellPosition;
import ssac.util.excel2csv.poi.CellRect;
import ssac.util.excel2csv.poi.SheetInfo;

/**
 * <code>[Excel to CSV]</code> 変換定義の '#table-form' コマンドブロック。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class CmdTableFormNode extends AbCommandSheetBlockNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	CMD_NAME	= "#table-form";
	static public final String	CMD_BEGIN	= CMD_NAME + CommandBlockNode.BLOCK_BEGIN_SUFFIX;
	static public final String	CMD_END		= CMD_NAME + CommandBlockNode.BLOCK_END_SUFFIX;
	
	static public final String[]	ALLOW_OPTIONS = {
		OptionValueParser.OPTION_VALUETYPE,
		OptionValueParser.OPTION_BLANKCELL,
		OptionValueParser.OPTION_DATEFORMAT,
		OptionValueParser.OPTION_PRECISION,
		OptionValueParser.OPTION_SHEET,
		OptionValueParser.OPTION_COLUMN_HEADER_ROWS,
		OptionValueParser.OPTION_FIRST_ROW,
		OptionValueParser.OPTION_LAST_ROW,
		OptionValueParser.OPTION_ROW_COUNT,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 列ヘッダーの行インデックスのマップ **/
	protected TreeMap<Integer, OptionValue>		_relHeaderRowIndices;
	/** Excel 1 行あたりの最大出力レコード数 **/
	private long	_outRecordsPerRow = 1L;
	/** 最初に実行する出力列を持つグループコマンド **/
	private CmdGroupColumnsNode	_firstGroup = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CmdTableFormNode(CellPosition cmdpos) {
		super(cmdpos);
		_relHeaderRowIndices = new TreeMap<Integer, OptionValue>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getName() {
		return CMD_NAME;
	}
	
	public String getBlockBeginName() {
		return CMD_BEGIN;
	}
	
	public String getBlockEndName() {
		return CMD_END;
	}
	
	public TreeMap<Integer, OptionValue> getHeaderRowOptionMap() {
		return _relHeaderRowIndices;
	}
	
	public boolean isHeaderRowEmpty() {
		return _relHeaderRowIndices.isEmpty();
	}
	
	public int getHeaderRowCount() {
		return _relHeaderRowIndices.size();
	}
	
	public boolean isHeaderRow(int rowIndex) {
		return _relHeaderRowIndices.containsKey(rowIndex);
	}
	
	public OptionValue putHeaderRow(int rowIndex, OptionValue opt) {
		return _relHeaderRowIndices.put(rowIndex, opt);
	}

	/**
	 * 実行準備を行う。ここでコマンド特有の初期化処理を実行する。
	 * @param excelbook	Excel ワークブック
	 * @param param		使用しない
	 * @return	概算の処理数(進捗状況のカウントに利用)
	 */
	@Override
	public long prepareExec(EtcWorkbookManager excelbook, Object param) {
		// 出力開始フィールド位置は、レコード先頭
		_destFieldIndex = 0;
		
		// Excel シート 1 行あたりの最大出力レコード数と出力フィールド数を集計
		long recsPerRow = 1L;
		int  numFields = 0;
		CmdGroupColumnsNode lastGroupNode = null;
		for (int index = 0; index < getChildCount(); ++index) {
			CommandNode child = getChildAt(index);
			//--- 子コマンドの実行準備
			long cnt = child.prepareExec(excelbook, numFields);
			if (cnt > 0L && child instanceof CmdGroupColumnsNode) {
				// 出力列のある '#group-columns' コマンドの初期化
				CmdGroupColumnsNode grpcmd = (CmdGroupColumnsNode)child;
				if (lastGroupNode == null) {
					// 最初のグループコマンド
					_firstGroup = grpcmd;
				} else {
					// 次のグループコマンド
					lastGroupNode.setNextGroupCommand(grpcmd);
				}
				lastGroupNode = grpcmd;
				recsPerRow *= cnt;
			}
			//--- 子コマンドの出力フィールド数
			numFields += child.getOutputFieldCount();
		}
		_numDestFields = numFields;
		_outRecordsPerRow = recsPerRow;
		
		// シートごとの処理レコード数を集計
		long numRows = 0L;
		for (int si = 0; si < _targetSheetInfos.length; ++si) {
			final SheetInfo sinfo = _targetSheetInfos[si];
			//--- 垂直方向のレコード操作のため、シートの有効領域を基準とする
			CellRect absTargetRect = getAbsoluteAvailableTargetArea(sinfo.getPhysicalSheetRect());
			//--- 単純に有効領域の行数を処理対象レコード数とする
			numRows += (absTargetRect.isEmpty() ? 0L : absTargetRect.getRowCount());
		}
		
		// 処理数 = numRows * recsPerRow
		return numRows * recsPerRow;
	}
	
	/**
	 * 変換を実行する。
	 * @param writer		CSV ファイルライター
	 * @param param			Excel ワークブック(EtcWorkbookManager)
	 * @param excelsheet	使用しない
	 * @param absTargetRect	このコマンドで処理対象となる絶対処理対象領域
	 * @param handler		進捗状況を更新するためのハンドラ、指定しない場合は <tt>null</tt>
	 * @return	出力対象のセルに何らかの値が格納されていた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws EtcException	変換エラーが発生した場合
	 */
	@Override
	public boolean exec(EtcCsvWriter writer, Object param, EtcConversionSheet excelsheet, CellRect absTargetRect, EtcConvertProgressHandler handler)
	throws IOException, EtcException
	{
		final EtcWorkbookManager excelbook = (EtcWorkbookManager)param;
		final int numChild = getChildCount();
		
		// シートごとに出力
		boolean hasRecords = false;	// CSV レコード出力の有無
		for (int si = 0; si < _targetSheetInfos.length; ++si) {
			// シートの取得
			final EtcConversionSheet sheet = excelbook.getSheet(_targetSheetInfos[si]);
			//--- 垂直方向のレコード操作のため、シートの有効領域を基準とする
			absTargetRect = getAbsoluteAvailableTargetArea(new CellRect(
								_targetSheetInfos[si].getFirstRowIndex(), 0,
								_targetSheetInfos[si].getPhysicalRowCount(),
								_targetSheetInfos[si].getLogicalColumnCount()));
			final int lastRowIndex = absTargetRect.getLastRowIndex();
			final int firstColIndex = absTargetRect.getFirstColumnIndex();
			
			// 処理対象領域の行ごとの処理
			for (int rowIndex = absTargetRect.getFirstRowIndex(); rowIndex <= lastRowIndex; ++rowIndex) {
				// ヘッダー行は処理対象から除外
				if (_relHeaderRowIndices.containsKey(rowIndex)) {
					//--- 進捗を更新
					if (handler != null) {
						handler.addCurrentValue(_outRecordsPerRow);
					}
					continue;
				}
				
				// セルのない行は除外
				if (!sheet.rowExists(rowIndex)) {
					//--- 進捗を更新
					if (handler != null) {
						handler.addCurrentValue(_outRecordsPerRow);
					}
					continue;
				}
				
				// 処理対象領域を更新
				absTargetRect.setPosition(rowIndex, firstColIndex);
				// グループ以外のフィールドを出力
				boolean hasFields = false;
				writer.clearRecordBuffer();
				for (int index = 0; index < numChild; ++index) {
					CommandNode child = getChildAt(index);
					if (!(child instanceof CmdGroupColumnsNode)) {
						if (child.exec(writer, null, sheet, absTargetRect, handler)) {
							hasFields = true;
						}
					}
				}
				// グループ出力
				if (_firstGroup != null) {
					if (_firstGroup.exec(writer, hasFields, sheet, absTargetRect, handler)) {
						// コミットされたら次の行へ
						hasRecords = true;
						continue;
					}
				}
				// 必要があればレコードコミット
				if (hasFields) {
					writer.commitRecord();
					hasRecords = true;
					//--- 進捗を更新
					if (handler != null) {
						handler.addCurrentValue(_outRecordsPerRow);
					}
				}
			}
		}

		// レコードが出力されいたら true
		return hasRecords;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
