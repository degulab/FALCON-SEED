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
package ssac.util.excel2csv.command;

import java.io.IOException;

import ssac.util.excel2csv.EtcConversionSheet;
import ssac.util.excel2csv.EtcConvertProgressHandler;
import ssac.util.excel2csv.EtcCsvWriter;
import ssac.util.excel2csv.EtcException;
import ssac.util.excel2csv.EtcWorkbookManager;
import ssac.util.excel2csv.parser.OptionValueParser;
import ssac.util.excel2csv.poi.CellPosition;
import ssac.util.excel2csv.poi.CellRect;

/**
 * <code>[Excel to CSV]</code> 変換定義の '#card-table' コマンドブロック。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class CmdCardTableNode extends AbCommandBlockNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	CMD_NAME	= "#card-table";
	static public final String	CMD_BEGIN	= CMD_NAME + CommandBlockNode.BLOCK_BEGIN_SUFFIX;
	static public final String	CMD_END		= CMD_NAME + CommandBlockNode.BLOCK_END_SUFFIX;
	
	static public final String[]	ALLOW_OPTIONS = {
		OptionValueParser.OPTION_VALUETYPE,
		OptionValueParser.OPTION_BLANKCELL,
		OptionValueParser.OPTION_DATEFORMAT,
		OptionValueParser.OPTION_PRECISION,
		OptionValueParser.OPTION_FIRST_CELL,
		OptionValueParser.OPTION_LAST_CELL,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 直積計算のための次のカードテーブルコマンドブロック **/
	private CmdCardTableNode	_nextTable;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CmdCardTableNode(CellPosition cmdpos) {
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
	
	public void setNextTableCommand(CmdCardTableNode nextTable) {
		_nextTable = nextTable;
	}

	/**
	 * 実行準備を行う。ここでコマンド特有の初期化処理を実行する。
	 * @param excelbook	Excel ワークブック
	 * @param param		出力フィールドインデックス(Integer)
	 * @return	概算の処理数(進捗状況のカウントに利用)、出力がない場合は 0
	 */
	@Override
	public long prepareExec(EtcWorkbookManager excelbook, Object param) {
		// 出力開始フィールド位置
		_destFieldIndex = (Integer)param;
		
		// 出力フィールド数は子コマンド数
		_numDestFields = getChildCount();
		
		// 子コマンドの実行準備
		for (int index = 0; index < getChildCount(); ++index) {
			CommandNode child = getChildAt(index);
			//--- 子ノードはすべてフィールド出力のみ
			child.prepareExec(excelbook, (_destFieldIndex+index));
		}
		
		// 処理数は出力レコード数(出力フィールド数が 0 の場合は 0)
		return (_numDestFields > 0 ? _relTargetRect.getRowCount() : 0L);
	}
	
	/**
	 * 変換を実行する。
	 * @param writer		CSV ファイルライター
	 * @param param			前のテーブルが有効セルを出力したかどうかを示すフラグ(Boolean)
	 * @param excelsheet	処理対象の Excel シート
	 * @param absTargetRect	このコマンドで処理対象となるカードの絶対処理対象領域
	 * @param handler		進捗状況を更新するためのハンドラ、指定しない場合は <tt>null</tt>
	 * @return	出力対象のセルに何らかの値が格納されていた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws EtcException	変換エラーが発生した場合
	 */
	@Override
	public boolean exec(EtcCsvWriter writer, Object param, EtcConversionSheet excelsheet, CellRect absTargetRect, EtcConvertProgressHandler handler)
	throws IOException, EtcException
	{
		final boolean hasPrevTableValidCell = (Boolean)param;
		
		CellRect childTargetRect = new CellRect(_relTargetRect.getFirstRowIndex()+absTargetRect.getFirstRowIndex(),
												_relTargetRect.getFirstColumnIndex()+absTargetRect.getFirstColumnIndex(),
												_relTargetRect.getRowCount(), _relTargetRect.getColumnCount());
		final int lastRowIndex = childTargetRect.getLastRowIndex();
		final int colIndex = childTargetRect.getFirstColumnIndex();
		final int childcnt = getChildCount();
		assert (childcnt > 0 && _relTargetRect.getRowCount() > 0L);	// 子コマンドがあることを前提
		boolean recCommitted = false;	// レコードのコミットをしたかどうか
		int rowIndex = childTargetRect.getFirstRowIndex();
		for (; rowIndex <= lastRowIndex; ++rowIndex) {
			// 現在のテーブルの 1 行の出力
			boolean hasTableValidCell = false;
			childTargetRect.setPosition(rowIndex, colIndex);
			if (excelsheet.rowExists(rowIndex)) {
				// テーブルのフィールド出力
				for (int index = 0; index < childcnt; ++index) {
					CommandNode child = getChildAt(index);
					if (child.exec(writer, null, excelsheet, childTargetRect, handler)) {
						// データのあるセルあり
						hasTableValidCell = true;
					}
				}
			}
			
			// 次のテーブルコマンドの実行
			if (_nextTable != null) {
				if (_nextTable.exec(writer, (hasPrevTableValidCell||hasTableValidCell), excelsheet, absTargetRect, handler)) {
					// コミットされた
					recCommitted = true;
				}
			}
			else {
				// 最終テーブルの場合は、レコードをコミットするかどうかを判定
				if (hasTableValidCell) {
					// レコードコミット
					writer.commitRecord();
					recCommitted = true;
				}
				//--- 進捗を更新
				if (handler != null) {
					handler.incrementCurrentValue();
				}
			}
		}
		
		// 最終テーブルの処理
		if (_nextTable == null) {
			// レコードがコミットされていない場合、前のテーブルが有効セルを出力していればコミット
			if (!recCommitted && hasPrevTableValidCell) {
				writer.commitRecord();
				recCommitted = true;
			}
			// 最大処理回数分、進捗を進める
			if (handler != null && rowIndex <= lastRowIndex) {
				handler.addCurrentValue(lastRowIndex - rowIndex + 1);
			}
		}
		
		// コミットされたかどうかを返す
		return recCommitted;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
