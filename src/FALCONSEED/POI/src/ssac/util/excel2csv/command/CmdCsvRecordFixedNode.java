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
 * @(#)CmdCsvRecordFixedNode.java	3.3.0	2016/05/10
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
 * <code>[Excel to CSV]</code> 変換定義の '#csv-record-fixed' コマンド。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class CmdCsvRecordFixedNode extends AbCommandNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	CMD_NAME	= "#csv-record-fixed";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** CSV 出力値のリスト **/
	private String[]	_values = new String[0];

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CmdCsvRecordFixedNode(CellPosition cmdpos) {
		super(cmdpos);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getName() {
		return CMD_NAME;
	}
	
	public boolean isValueEmpty() {
		return (_values.length == 0);
	}
	
	public int getValueCount() {
		return _values.length;
	}
	
	public String getString(int index) {
		String v = _values[index];
		return (v==null ? "" : v);
	}
	
	public String getValue(int index) {
		return _values[index];
	}
	
	public String[] getValues() {
		return _values;
	}
	
	public void setValues(String[] values) {
		_values = (values==null ? new String[0] : values);
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
		
		// 出力フィールド数は、値リストの総数
		_numDestFields = _values.length;
		
		// 処理数は、1 レコード分(出力フィールドがある場合のみ)
		return (_numDestFields > 0 ? 1L : 0L);
	}
	
	/**
	 * 変換を実行する。
	 * @param writer		CSV ファイルライター
	 * @param param			使用しない
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
		// 出力値がある場合のみ出力
		boolean hasRecord = false;
		if (_values.length > 0) {
			// レコードバッファをクリア
			writer.clearRecordBuffer();
			// 固定の値を出力
			for (int index = 0; index < _values.length; ++index) {
				writer.setFieldValue(_destFieldIndex+index, _values[index]);
			}
			// レコード完結
			writer.commitRecord();
			hasRecord = true;
		}
		
		// 進捗状況更新
		if (handler != null) {
			handler.incrementCurrentValue();
		}

		// レコードが出力されたかどうか
		return hasRecord;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
