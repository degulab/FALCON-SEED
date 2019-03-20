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
 * @(#)CmdCardFormNode.java	3.3.0	2016/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command;

import java.io.IOException;

import ssac.util.excel2csv.EtcConversionSheet;
import ssac.util.excel2csv.EtcConvertProgressHandler;
import ssac.util.excel2csv.EtcCsvWriter;
import ssac.util.excel2csv.EtcException;
import ssac.util.excel2csv.EtcWorkbookManager;
import ssac.util.excel2csv.command.option.OptionValue;
import ssac.util.excel2csv.command.option.RepeatTypes;
import ssac.util.excel2csv.parser.OptionValueParser;
import ssac.util.excel2csv.poi.CellPosition;
import ssac.util.excel2csv.poi.CellRect;
import ssac.util.excel2csv.poi.SheetInfo;

/**
 * <code>[Excel to CSV]</code> 変換定義の '#card-form' コマンドブロック。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class CmdCardFormNode extends AbCommandSheetBlockNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	CMD_NAME	= "#card-form";
	static public final String	CMD_BEGIN	= CMD_NAME + CommandBlockNode.BLOCK_BEGIN_SUFFIX;
	static public final String	CMD_END		= CMD_NAME + CommandBlockNode.BLOCK_END_SUFFIX;
	
	static public final String[]	ALLOW_OPTIONS = {
		OptionValueParser.OPTION_VALUETYPE,
		OptionValueParser.OPTION_BLANKCELL,
		OptionValueParser.OPTION_DATEFORMAT,
		OptionValueParser.OPTION_PRECISION,
		OptionValueParser.OPTION_SHEET,
		OptionValueParser.OPTION_FIRST_CELL,
		OptionValueParser.OPTION_LAST_CELL,
		OptionValueParser.OPTION_REPEAT,
		OptionValueParser.OPTION_ROW_SPACING,
		OptionValueParser.OPTION_COLUMN_SPACING,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 出力される最大カード数、未集計なら (-1) **/
	private long	_maxCards = (-1L);
	/** 1 カードあたりの最大出力レコード数 **/
	private long	_outRecordsPerCard = 1L;
	/** 最初に実行する出力列を持つカードテーブルコマンド **/
	private CmdCardTableNode _firstTable = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CmdCardFormNode(CellPosition cmdpos) {
		super(cmdpos);
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
	
	public RepeatTypes getRepeatType() {
		OptionValue opt = getLocalOption(OptionValueParser.OPTION_REPEAT);
		return (opt==null ? RepeatTypes.NONE : (RepeatTypes)opt.getValue());
	}
	
	public int getRowSpacing() {
		OptionValue opt = getLocalOption(OptionValueParser.OPTION_ROW_SPACING);
		return (opt==null ? 0 : (Integer)opt.getValue());
	}
	
	public int getColumnSpacing() {
		OptionValue opt = getLocalOption(OptionValueParser.OPTION_COLUMN_SPACING);
		return (opt==null ? 0 : (Integer)opt.getValue());
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
		
		// 1 カードあたりの最大出力レコード数と出力フィールド数を集計
		long recsPerCard = 1L;
		int  numFields = 0;
		CmdCardTableNode lastTableNode = null;
		for (int index = 0; index < getChildCount(); ++index) {
			CommandNode child = getChildAt(index);
			//--- 子コマンドの実行準備
			long cnt = child.prepareExec(excelbook, numFields);
			if (cnt > 0L && child instanceof CmdCardTableNode) {
				// 出力列のある '#card-table' コマンドの初期化
				CmdCardTableNode tablecmd = (CmdCardTableNode)child;
				if (lastTableNode == null) {
					// 最初のテーブルコマンド
					_firstTable = tablecmd;
				} else {
					// 次のテーブルコマンド
					lastTableNode.setNextTableCommand(tablecmd);
				}
				lastTableNode = tablecmd;
				recsPerCard *= cnt;
			}
			//--- 子コマンドの出力フィールド数
			numFields += child.getOutputFieldCount();
		}
		_numDestFields = numFields;
		_outRecordsPerCard = recsPerCard;
		
		// 最大カード数を集計
		long numCards = 0L;	// 有効カード数
		final RepeatTypes repeat = getRepeatType();
		final int rowOffset = _relTargetRect.getRowCount() + getRowSpacing();
		final int colOffset = _relTargetRect.getColumnCount() + getColumnSpacing();
		CellRect cardAbsRect = new CellRect();
		for (SheetInfo sinfo : _targetSheetInfos) {
			//--- check valid rect
			CellRect sheetRect = sinfo.getLogicalSheetRect();
			if (sheetRect.isEmpty() || _relTargetRect.isEmpty()) {
				// no data, or no area
				continue;
			}
			//--- check card area
			cardAbsRect.setRect(_relTargetRect);
			if (repeat == RepeatTypes.BOTH) {
				// both repeat
				int firstCardColIndex = cardAbsRect.getFirstColumnIndex();
				for (; cardAbsRect.getFirstRowIndex() <= sinfo.getLastRowIndex(); ) {
					for (; cardAbsRect.getFirstColumnIndex() <= sinfo.getLastColumnIndex(); ) {
						if (cardAbsRect.intersects(sheetRect)) {
							++numCards;
						}
						//--- next card in same row
						cardAbsRect.setPosition(cardAbsRect.getFirstRowIndex(), cardAbsRect.getFirstColumnIndex()+colOffset);
					}
					//--- next row
					cardAbsRect.setPosition(cardAbsRect.getFirstRowIndex()+rowOffset, firstCardColIndex);
				}
			}
			else if (repeat == RepeatTypes.HORZ) {
				// horizontal repeat
				final int firstCardRowIndex = cardAbsRect.getFirstRowIndex();
				for (; cardAbsRect.getFirstColumnIndex() <= sinfo.getLastColumnIndex();) {
					if (cardAbsRect.intersects(sheetRect)) {
						++numCards;
					}
					//--- next card
					cardAbsRect.setPosition(firstCardRowIndex, cardAbsRect.getFirstColumnIndex()+colOffset);
				}
			}
			else if (repeat == RepeatTypes.VERT) {
				// vertial repeat
				final int firstCardColIndex = cardAbsRect.getFirstColumnIndex();
				for (; cardAbsRect.getFirstRowIndex() <= sinfo.getLastRowIndex();) {
					if (cardAbsRect.intersects(sheetRect)) {
						++numCards;
					}
					//--- next card
					cardAbsRect.setPosition(cardAbsRect.getFirstRowIndex()+rowOffset, firstCardColIndex);
				}
			}
			else {
				// just one in the sheet
				if (cardAbsRect.intersects(sheetRect)) {
					++numCards;
				}
			}
		}
		_maxCards = numCards;
		
		// 処理数 = numCards * recPerCard
		return (numCards * recsPerCard);
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
		
		// シートごとに出力
		long procCards = 0L;		// 処理したカード数
		boolean hasRecords = false;	// CSV レコード出力の有無
		final RepeatTypes repeat = getRepeatType();
		final int rowOffset = _relTargetRect.getRowCount() + getRowSpacing();
		final int colOffset = _relTargetRect.getColumnCount() + getColumnSpacing();
		CellRect cardAbsRect = new CellRect();
		for (int si = 0; si < _targetSheetInfos.length; ++si) {
			// シートの取得
			final SheetInfo sinfo = _targetSheetInfos[si];
			excelsheet = excelbook.getSheet(sinfo);
			//--- シートの左上からの有効領域を基準とする
			absTargetRect = new CellRect(_targetSheetInfos[si].getLogicalSheetRect());
			
			// カードごとの処理
			cardAbsRect.setRect(_relTargetRect);
			if (repeat == RepeatTypes.BOTH) {
				// both repeat
				int firstCardColIndex = cardAbsRect.getFirstColumnIndex();
				for (; cardAbsRect.getFirstRowIndex() <= sinfo.getLastRowIndex(); ) {
					for (; cardAbsRect.getFirstColumnIndex() <= sinfo.getLastColumnIndex(); ) {
						if (cardAbsRect.intersects(absTargetRect)) {
							if (execCard(writer, excelsheet, cardAbsRect, handler)) {
								hasRecords = true;
							}
							++procCards;
						}
						//--- next card in same row
						cardAbsRect.setPosition(cardAbsRect.getFirstRowIndex(), cardAbsRect.getFirstColumnIndex()+colOffset);
					}
					//--- next row
					cardAbsRect.setPosition(cardAbsRect.getFirstRowIndex()+rowOffset, firstCardColIndex);
				}
			}
			else if (repeat == RepeatTypes.HORZ) {
				// horizontal repeat
				final int firstCardRowIndex = cardAbsRect.getFirstRowIndex();
				for (; cardAbsRect.getFirstColumnIndex() <= sinfo.getLastColumnIndex();) {
					if (cardAbsRect.intersects(absTargetRect)) {
						if (execCard(writer, excelsheet, cardAbsRect, handler)) {
							hasRecords = true;
						}
						++procCards;
					}
					//--- next card
					cardAbsRect.setPosition(firstCardRowIndex, cardAbsRect.getFirstColumnIndex()+colOffset);
				}
			}
			else if (repeat == RepeatTypes.VERT) {
				// vertial repeat
				final int firstCardColIndex = cardAbsRect.getFirstColumnIndex();
				for (; cardAbsRect.getFirstRowIndex() <= sinfo.getLastRowIndex();) {
					if (cardAbsRect.intersects(absTargetRect)) {
						if (execCard(writer, excelsheet, cardAbsRect, handler)) {
							hasRecords = true;
						}
						++procCards;
					}
					//--- next card
					cardAbsRect.setPosition(cardAbsRect.getFirstRowIndex()+rowOffset, firstCardColIndex);
				}
			}
			else {
				// just one in the sheet
				if (cardAbsRect.intersects(absTargetRect)) {
					if (execCard(writer, excelsheet, cardAbsRect, handler)) {
						hasRecords = true;
					}
					++procCards;
				}
			}
		}
		
		// 不足の進捗を進める
		if (handler != null && procCards < _maxCards) {
			handler.addCurrentValue((_maxCards - procCards) * _outRecordsPerCard);
		}

		// レコードが出力されいたら true
		return hasRecords;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 1 カード分の変換を実行する。
	 * @param writer		CSV ファイルライター
	 * @param excelsheet	処理対象の Excel シート
	 * @param cardAbsRect	処理対象カードの絶対処理対象領域
	 * @param handler		進捗状況を更新するためのハンドラ、指定しない場合は <tt>null</tt>
	 * @return	出力対象のセルに何らかの値が格納されていた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws EtcException	変換エラーが発生した場合
	 */
	protected boolean execCard(EtcCsvWriter writer, EtcConversionSheet excelsheet, CellRect cardAbsRect, EtcConvertProgressHandler handler)
	throws IOException, EtcException
	{
		final int numChild = getChildCount();
		boolean hasFields = false;
		writer.clearRecordBuffer();
		
		long procpos = (handler==null ? 0 : handler.getCurrentValue());
		
		// テーブル以外のフィールドを出力
		for (int index = 0; index < numChild; ++index) {
			CommandNode child = getChildAt(index);
			if (!(child instanceof CmdCardTableNode)) {
				if (child.exec(writer, null, excelsheet, cardAbsRect, handler)) {
					hasFields = true;
				}
			}
		}
		
		// テーブル出力
		if (_firstTable != null) {
			if (_firstTable.exec(writer, hasFields, excelsheet, cardAbsRect, handler)) {
				// コミットされたら次のカードへ
				return true;
			}
		}
		
		// 必要があればレコードコミット
		if (hasFields) {
			writer.commitRecord();
		}
		
		// 進捗を更新
		if (handler != null) {
			procpos = _outRecordsPerCard - (handler.getCurrentValue() - procpos);
			if (procpos > 0L) {
				handler.addCurrentValue(procpos);
			}
		}
		
		return hasFields;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
