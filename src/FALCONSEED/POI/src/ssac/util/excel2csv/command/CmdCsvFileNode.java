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
 * @(#)CmdCsvFileNode.java	3.3.0	2016/05/10
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
 * <code>[Excel to CSV]</code> 変換定義の '#csv-file' コマンドブロック。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class CmdCsvFileNode extends AbCommandBlockNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	CMD_NAME	= "#csv-file";
	static public final String	CMD_BEGIN	= CMD_NAME + CommandBlockNode.BLOCK_BEGIN_SUFFIX;
	static public final String	CMD_END		= CMD_NAME + CommandBlockNode.BLOCK_END_SUFFIX;
	
	static public final String[]	ALLOW_OPTIONS = {
		OptionValueParser.OPTION_VALUETYPE,
		OptionValueParser.OPTION_BLANKCELL,
		OptionValueParser.OPTION_DATEFORMAT,
		OptionValueParser.OPTION_PRECISION,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** タイトルのパラメータ値 **/
	private CmdParamValue	_paramTitle;
	/** 標準ファイル名のパラメータ値 **/
	private CmdParamValue	_paramFilename;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CmdCsvFileNode(CellPosition cmdpos) {
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
	
	public CmdParamValue getTitleParameter() {
		return _paramTitle;
	}
	
	public void setTitleParameter(CmdParamValue param) {
		_paramTitle = param;
	}
	
	public CmdParamValue getFilenameParameter() {
		return _paramFilename;
	}
	
	public void setFilenameParameter(CmdParamValue param) {
		_paramFilename = param;
	}

	/**
	 * このコマンドブロックは親を持たないため、このメソッドをサポートしない。
	 * @throws UnsupportedOperationException	常にこの例外をスローする
	 */
	@Override
	public void setParent(CommandBlockNode parent) {
		throw new UnsupportedOperationException();
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
		
		// 最大出力フィールド数を集計と処理数を集計
		long proccnt = 0L;
		int maxFields = 0;
		for (int index = 0; index < getChildCount(); ++index) {
			CommandNode child = getChildAt(index);
			proccnt += child.prepareExec(excelbook, null);
			maxFields = Math.max(maxFields, child.getOutputFieldCount());
		}
		_numDestFields = maxFields;
		
		return proccnt;
	}
	
	/**
	 * 変換を実行する。
	 * @param writer		CSV ファイルライター
	 * @param param			Excel ワークブック(EtcWorkbookManager)
	 * @param excelsheet	使用しない
	 * @param absTargetRect	使用しない
	 * @param handler		進捗状況を更新するためのハンドラ、指定しない場合は <tt>null</tt>
	 * @return	出力対象のセルに何らかの値が格納されていた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws EtcException	変換エラーが発生した場合
	 */
	@Override
	public boolean exec(EtcCsvWriter writer, Object param, EtcConversionSheet excelsheet, CellRect absTargetRect, EtcConvertProgressHandler handler)
	throws IOException, EtcException
	{
		// 子コマンドを実行(進捗状況は子コマンド内で更新)
		final int numChild = getChildCount();
		boolean hasRecords = false;
		for (int index = 0; index < numChild; ++index) {
			if (getChildAt(index).exec(writer, param, excelsheet, absTargetRect, handler)) {
				hasRecords = true;
			}
		}
		
		// 処理完了
		return hasRecords;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
