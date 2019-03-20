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
 * @(#)CmdCsvFieldFixedNode.java	3.3.0	2016/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command;

import java.io.IOException;

import ssac.util.excel2csv.EtcConversionSheet;
import ssac.util.excel2csv.EtcConvertProgressHandler;
import ssac.util.excel2csv.EtcCsvWriter;
import ssac.util.excel2csv.EtcException;
import ssac.util.excel2csv.EtcWorkbookManager;
import ssac.util.excel2csv.poi.CellPosition;
import ssac.util.excel2csv.poi.CellRect;

/**
 * <code>[Excel to CSV]</code> 変換定義の '#csv-field-fixed' コマンド。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class CmdCsvFieldFixedNode extends AbCommandNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	CMD_NAME	= "#csv-field-fixed";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** CSV 出力値 **/
	private String	_value;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CmdCsvFieldFixedNode(CellPosition cmdpos) {
		super(cmdpos);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getName() {
		return CMD_NAME;
	}
	
	public String getCsvString() {
		return (_value==null ? "" : _value);
	}
	
	public String getValue() {
		return _value;
	}
	
	public void setValue(String value) {
		_value = value;
	}

	/**
	 * 実行準備を行う。ここでコマンド特有の初期化処理を実行する。
	 * @param excelbook	Excel ワークブック
	 * @param param		出力フィールドインデックス(Integer)
	 * @return	概算の処理数(進捗状況のカウントに利用)
	 */
	@Override
	public long prepareExec(EtcWorkbookManager excelbook, Object param) {
		// 出力フィールドインデックス
		_destFieldIndex = (Integer)param;
		
		// 出力フィールド数
		_numDestFields = 1;
		
		// フィールドのみの出力では、処理数をカウントしない
		return 0L;
	}
	
	/**
	 * 変換を実行する。
	 * @param writer		CSV ファイルライター
	 * @param param			使用しない
	 * @param excelsheet	対象の Excel シート、対象が未定義の場合は <tt>null</tt>
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
		// 固定の値を出力
		writer.setFieldValue(_destFieldIndex, _value);
		
		//--- 進捗はカウントしない
		
		// 有効な値が出力されていれば true
		return (_value != null && !_value.isEmpty());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
