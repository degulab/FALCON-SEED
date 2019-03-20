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
 * @(#)CommandNode.java	3.3.0	2016/05/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import ssac.util.excel2csv.EtcConversionSheet;
import ssac.util.excel2csv.EtcConvertProgressHandler;
import ssac.util.excel2csv.EtcCsvWriter;
import ssac.util.excel2csv.EtcException;
import ssac.util.excel2csv.EtcWorkbookManager;
import ssac.util.excel2csv.command.option.OptionValue;
import ssac.util.excel2csv.command.option.ValueTypes;
import ssac.util.excel2csv.poi.CellPosition;
import ssac.util.excel2csv.poi.CellRect;

/**
 * <code>[Excel to CSV]</code> 変換定義におけるコマンドノードのインタフェース。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public interface CommandNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * コマンド名を返す。
	 * @return	コマンド名
	 */
	public String getName();

	/**
	 * コマンド名が記述されたセル位置を返す。
	 * @return	コマンド名が記述されたセル位置
	 */
	public CellPosition getCommandCellPosition();

	/**
	 * 単独のコマンドであれば <tt>true</tt> を返す。
	 * @return	単独コマンドなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isSoleNode();

	/**
	 * コマンドブロックであれば <tt>true</tt> を返す。
	 * @return	コマンドブロックなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isBlockNode();

	/**
	 * 親のコマンドブロックを取得する。
	 * @return	親コマンドブロックが指定されている場合はそのオブジェクト、親が存在しない場合は <tt>null</tt>
	 */
	public CommandBlockNode getParent();

	/**
	 * 親のコマンドブロックを設定する。
	 * @param parent	親コマンドブロック、もしくは <tt>null</tt>
	 */
	public void setParent(CommandBlockNode parent);

	/**
	 * このコマンドにオプションが指定されているかどうかを判定する。
	 * @return	オプションが一つも指定されていない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean isOptionEmpty();

	/**
	 * このコマンドに指定されたオプション数を取得する。
	 * @return	オプション数
	 */
	public int getOptionCount();

	/**
	 * 指定された名前のオプションが、このコマンドに設定されているかどうかを判定する。
	 * @param optionName	オプション名
	 * @return	指定のオプションがこのコマンドに設定されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean containsLocalOption(String optionName);

	/**
	 * 指定された名前のオプションを、このコマンドから取得する。
	 * @param optionName	オプション名
	 * @return	指定のオプションがこのコマンドに設定されている場合はその値、それ以外の場合は <tt>null</tt>
	 */
	public OptionValue getLocalOption(String optionName);

	/**
	 * 指定された名前のオプションを、このコマンドもしくは上位コマンドブロックから取得する。
	 * @param optionName	オプション名
	 * @return	指定のオプションがこのコマンドもしくは上位コマンドブロックに設定されている場合はその値、それ以外の場合は <tt>null</tt>
	 */
	public OptionValue getAvailableOption(String optionName);

	/**
	 * このコマンドにオプションを設定する。
	 * @param option	設定するオプション
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public void putOption(OptionValue option);

	/**
	 * このコマンドにおけるセルからの値取得方法を示すオプション値を取得する。
	 * 上位コマンドでもオプションが指定されていない場合は、{@link ValueTypes#AUTO} を返す。
	 * @return	{@link ValueTypes} の値
	 */
	public ValueTypes getOptionValueType();
	/**
	 * このコマンドにおける日付時刻フォーマットを示すオプション値を取得する。
	 * キーワードで指定されたオプション値の場合は、そのキーワードに相当するフォーマットを返す。
	 * 上位コマンドでもオプションが指定されていない場合は、{@link ssac.util.excel2csv.command.option.DateFormatTypes#DEFAULT} に相当するフォーマットを返す。
	 * ただし、{@link ssac.util.excel2csv.command.option.DateFormatTypes#EXCEL} が指定されていた場合は <tt>null</tt> を返す。
	 * @return	<code>SimpleDateFormat</code> オブジェクト、または <tt>null</tt>
	 */
	public SimpleDateFormat getOptionDateFormat();
	/**
	 * このコマンドにおける数値の小数点以下桁数を示すオプション値を取得する。
	 * オプション値が桁数ではない場合、もしくは上位コマンドでもオプションが指定されていない場合は、
	 * {@link ssac.util.excel2csv.command.option.PrecisionTypes#AUTO} が指定されているものとみなす。
	 * @return	オプション指定に準ずる <code>DecimalFormat</code> オブジェクト
	 */
	public DecimalFormat getOptionPrecision();
	/**
	 * このコマンドにおける BLANK セルの値を示すオプション値を取得する。
	 * 上位コマンドでもオプションが指定されていない場合は、{@link ssac.util.excel2csv.command.option.BlankCellTypes#BLANK} が指定されているものとみなし、空文字を返す。
	 * @return	BLANK セルの時に出力する文字列
	 */
	public String getOptionBlankValue();

	/**
	 * このコマンドによって出力される CSV フィールドの先頭インデックス
	 * @return	出力フィールドの先頭インデックス
	 */
	public int getDestinationFieldIndex();

	/**
	 * このコマンドで出力されるフィールド数を取得する。
	 * @return	出力フィールド数
	 */
	public int getOutputFieldCount();

	/**
	 * 実行準備を行う。ここでコマンド特有の初期化処理を実行する。
	 * @param excelbook	Excel ワークブック
	 * @param param		固有のパラメータ
	 * @return	概算の処理数(進捗状況のカウントに利用)
	 */
	public long prepareExec(EtcWorkbookManager excelbook, Object param);
	
	/**
	 * 変換を実行する。
	 * @param writer		CSV ファイルライター
	 * @param param			実行時の固有パラメータ
	 * @param excelsheet	対象の Excel シート、対象が未定義の場合は <tt>null</tt>
	 * @param absTargetRect	このコマンドで処理対象となる絶対処理対象領域
	 * @param handler		進捗状況を更新するためのハンドラ、指定しない場合は <tt>null</tt>
	 * @return	出力対象のセルに何らかの値が格納されていた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws IOException	入出力エラーが発生した場合
	 * @throws EtcException	変換エラーが発生した場合
	 */
	public boolean exec(EtcCsvWriter writer, Object param, EtcConversionSheet excelsheet, CellRect absTargetRect, EtcConvertProgressHandler handler)
	throws IOException, EtcException;
}
