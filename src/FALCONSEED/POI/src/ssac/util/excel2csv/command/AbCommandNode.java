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
 * @(#)CmdCardTableNode.java	3.3.0	2016/05/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;

import ssac.util.excel2csv.EtcWorkbookManager;
import ssac.util.excel2csv.command.option.DateFormatTypes;
import ssac.util.excel2csv.command.option.OptionValue;
import ssac.util.excel2csv.command.option.ValueTypes;
import ssac.util.excel2csv.parser.OptionValueParser;
import ssac.util.excel2csv.poi.CellPosition;
import ssac.util.excel2csv.poi.EtcPoiUtil;

import com.github.mygreen.cellformatter.CellFormatResult;

/**
 * <code>[Excel to CSV]</code> 変換定義におけるコマンドブロックノードの共通実装。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public abstract class AbCommandNode implements CommandNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** {@link DateFormatTypes#DEFAULT} のフォーマット **/
	static protected final SimpleDateFormat	_sdfDefault	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 親コマンドブロック **/
	private CommandBlockNode	_parent;
	/** コマンドのセル位置 **/
	private CellPosition		_cmdpos;
	/** オプション値 **/
	private Map<String, OptionValue>	_optionmap = Collections.emptyMap();
	/** CSV フィールドの出力開始インデックス **/
	protected int	_destFieldIndex = 0;
	/** CSV フィールドとして出力されるフィールド数 **/
	protected int	_numDestFields = 0;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbCommandNode(CellPosition cmdpos) {
		if (cmdpos == null)
			throw new NullPointerException();
		_cmdpos = cmdpos;
		_parent = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * コマンド名が記述されたセル位置を返す。
	 * @return	コマンド名が記述されたセル位置
	 */
	@Override
	public CellPosition getCommandCellPosition() {
		return _cmdpos;
	}

	/**
	 * 単独のコマンドであれば <tt>true</tt> を返す。
	 * @return	単独コマンドなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	@Override
	public boolean isSoleNode() {
		return true;
	}

	/**
	 * コマンドブロックであれば <tt>true</tt> を返す。
	 * @return	コマンドブロックなら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	@Override
	public boolean isBlockNode() {
		return false;
	}

	/**
	 * 親のコマンドブロックを取得する。
	 * @return	親コマンドブロックが指定されている場合はそのオブジェクト、親が存在しない場合は <tt>null</tt>
	 */
	@Override
	public CommandBlockNode getParent() {
		return _parent;
	}

	/**
	 * 親のコマンドブロックを設定する。
	 * @param parent	親コマンドブロック、もしくは <tt>null</tt>
	 */
	@Override
	public void setParent(CommandBlockNode parent) {
		_parent = parent;
	}

	/**
	 * このコマンドにオプションが指定されているかどうかを判定する。
	 * @return	オプションが一つも指定されていない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean isOptionEmpty() {
		return _optionmap.isEmpty();
	}

	/**
	 * このコマンドに指定されたオプション数を取得する。
	 * @return	オプション数
	 */
	@Override
	public int getOptionCount() {
		return _optionmap.size();
	}

	/**
	 * 指定された名前のオプションが、このコマンドに設定されているかどうかを判定する。
	 * @param optionName	オプション名
	 * @return	指定のオプションがこのコマンドに設定されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	@Override
	public boolean containsLocalOption(String optionName) {
		return _optionmap.containsKey(optionName);
	}

	/**
	 * 指定された名前のオプションを、このコマンドから取得する。
	 * @param optionName	オプション名
	 * @return	指定のオプションがこのコマンドに設定されている場合はその値、それ以外の場合は <tt>null</tt>
	 */
	@Override
	public OptionValue getLocalOption(String optionName) {
		return _optionmap.get(optionName);
	}

	/**
	 * 指定された名前のオプションを、このコマンドもしくは上位コマンドブロックから取得する。
	 * @param optionName	オプション名
	 * @return	指定のオプションがこのコマンドもしくは上位コマンドブロックに設定されている場合はその値、それ以外の場合は <tt>null</tt>
	 */
	@Override
	public OptionValue getAvailableOption(String optionName) {
		OptionValue value = _optionmap.get(optionName);
		if (value == null && _parent != null) {
			// 親コマンドブロックのオプションを取得する
			value = _parent.getAvailableOption(optionName);
		}
		return value;
	}

	/**
	 * このコマンドにオプションを設定する。
	 * @param option	設定するオプション
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	@Override
	public void putOption(OptionValue option) {
		if (_optionmap.isEmpty())
			_optionmap = new TreeMap<String, OptionValue>();
		_optionmap.put(option.getName(), option);
	}

	/**
	 * このコマンドにおけるセルからの値取得方法を示すオプション値を取得する。
	 * 上位コマンドでもオプションが指定されていない場合は、{@link ValueTypes#AUTO} を返す。
	 * @return	{@link ValueTypes} の値
	 */
	@Override
	public ValueTypes getOptionValueType() {
		OptionValue opt = getAvailableOption(OptionValueParser.OPTION_VALUETYPE);
		if (opt != null && opt.getValue() != null) {
			return (ValueTypes)opt.getValue();
		} else {
			return ValueTypes.AUTO;	// default
		}
	}
	
	/**
	 * このコマンドにおける日付時刻フォーマットを示すオプション値を取得する。
	 * キーワードで指定されたオプション値の場合は、そのキーワードに相当するフォーマットを返す。
	 * 上位コマンドでもオプションが指定されていない場合は、{@link ssac.util.excel2csv.command.option.DateFormatTypes#DEFAULT} に相当するフォーマットを返す。
	 * ただし、{@link ssac.util.excel2csv.command.option.DateFormatTypes#EXCEL} が指定されていた場合は <tt>null</tt> を返す。
	 * @return	<code>SimpleDateFormat</code> オブジェクト、または <tt>null</tt>
	 */
	@Override
	public SimpleDateFormat getOptionDateFormat() {
		OptionValue opt = getAvailableOption(OptionValueParser.OPTION_DATEFORMAT);
		if (opt != null) {
			if (opt.getValue() instanceof SimpleDateFormat) {
				// パターン
				return (SimpleDateFormat)opt.getValue();
			}
			else {
				// キーワード
				DateFormatTypes dftype = (DateFormatTypes)opt.getValue();
				if (dftype == DateFormatTypes.EXCEL) {
					// Excel
					return null;
				} else {
					// Default
					return _sdfDefault;
				}
			}
		}
		else {
			// default
			return _sdfDefault;
		}
	}

	/**
	 * このコマンドにおける数値の小数点以下桁数を示すオプション値を取得する。
	 * オプション値が桁数ではない場合、もしくは上位コマンドでもオプションが指定されていない場合は、
	 * {@link ssac.util.excel2csv.command.option.PrecisionTypes#AUTO} が指定されているものとみなす。
	 * @return	オプション指定に準ずる <code>DecimalFormat</code> オブジェクト
	 */
	@Override
	public DecimalFormat getOptionPrecision() {
		OptionValue opt = getAvailableOption(OptionValueParser.OPTION_PRECISION);
		if (opt != null && opt.getValue() instanceof Integer) {
			int pc = (Integer)opt.getValue();
			DecimalFormat df = new DecimalFormat("#.#");
			df.setMinimumFractionDigits(pc);
			df.setMaximumFractionDigits(pc);
			return df;
		} else {
			return new DecimalFormat("#.#");
		}
	}

	/**
	 * このコマンドにおける BLANK セルの値を示すオプション値を取得する。
	 * 上位コマンドでもオプションが指定されていない場合は、{@link ssac.util.excel2csv.command.option.BlankCellTypes#BLANK} が指定されているものとみなし、空文字を返す。
	 * @return	BLANK セルの時に出力する文字列
	 */
	@Override
	public String getOptionBlankValue() {
		OptionValue opt = getAvailableOption(OptionValueParser.OPTION_BLANKCELL);
		if (opt != null && opt.getValue() instanceof String) {
			return (String)opt.getValue();
		} else {
			return "";
		}
	}

	/**
	 * このコマンドによって出力される CSV フィールドの先頭インデックス
	 * @return	出力フィールドの先頭インデックス
	 */
	@Override
	public int getDestinationFieldIndex() {
		return _destFieldIndex;
	}

	/**
	 * このコマンドで出力されるフィールド数を取得する。
	 * @return	出力フィールド数
	 */
	@Override
	public int getOutputFieldCount() {
		return _numDestFields;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたセルから、CSV フィールドに出力する値を取得する。
	 * <em>cell</em> が <tt>null</tt> の場合、ブランクセルとして扱う。
	 * @param excelbook	Excel ワークブック
	 * @param cell		対象のセル
	 * @return	フィールドに出力する文字列
	 */
	protected String getFieldValueFromCell(EtcWorkbookManager excelbook, Cell cell) {
		if (cell == null) {
			// BLANK として扱う
			return getOptionBlankValue();
		}
		
		// セルの値の種類を判定
		ValueTypes vtype = getOptionValueType();
		int ctype = EtcPoiUtil.getCellValueType(cell);
		switch (ctype) {
			case Cell.CELL_TYPE_STRING :
				return formatStringCellValue(excelbook, cell, cell.getStringCellValue(), vtype);
			case Cell.CELL_TYPE_NUMERIC :
				return formatNumericCellValue(excelbook, cell, cell.getNumericCellValue(), vtype);
			case Cell.CELL_TYPE_BOOLEAN :
				return formatBooleanCellValue(excelbook, cell, cell.getBooleanCellValue(), vtype);
			case Cell.CELL_TYPE_BLANK :
				return getOptionBlankValue();
			case Cell.CELL_TYPE_ERROR :
				return formatErrorCellValue(excelbook, cell, cell.getErrorCellValue(), vtype);
			default :
				//--- この形式になることはないが、文字列扱いとする
				return formatStringCellValue(excelbook, cell, cell.getStringCellValue(), vtype);
		}
	}

	/**
	 * 指定された <code>ERROR</code> セルの値を、指定された形式で整形する。
	 * @param excelbook	Excel ワークブック
	 * @param cell		対象のセル
	 * @param value		対象セルの値
	 * @param vtype		値の出力形式
	 * @return	整形された出力文字列
	 */
	protected String formatErrorCellValue(EtcWorkbookManager excelbook, Cell cell, byte value, ValueTypes vtype) {
		// エラー値は、出力形式に関係なく、規定された文字列表現とする
		return "Error(" + String.valueOf(value) + ")";
	}
	
	/**
	 * 指定された <code>BOOLEAN</code> セルの値を、指定された形式で整形する。
	 * @param excelbook	Excel ワークブック
	 * @param cell		対象のセル
	 * @param value		対象セルの値
	 * @param vtype		値の形式
	 * @return	整形された出力文字列
	 */
	protected String formatBooleanCellValue(EtcWorkbookManager excelbook, Cell cell, boolean value, ValueTypes vtype) {
		if (vtype == ValueTypes.EXCEL) {
			// Excel 書式で出力
			return excelbook.getFormattedExcelValue(cell);
		}
		else if (vtype == ValueTypes.DECIMAL) {
			// 数値として出力
			return (value ? "1" : "0");
		}
		else {
			// Boolean の文字列表現
			return Boolean.toString(value);
		}
	}

	/**
	 * 文字列を数値に変換する。
	 * @param value	変換する文字列
	 * @return	変換された値、変換できなかった場合は <tt>null</tt>
	 */
	protected BigDecimal stringToDecimal(String value) {
		try {
			return new BigDecimal(value.trim());
		} catch (NumberFormatException ex) {
			return null;
		}
	}
	
	/**
	 * 指定された <code>STRING</code> セルの値を、指定された形式で整形する。
	 * @param excelbook	Excel ワークブック
	 * @param cell		対象のセル
	 * @param value		対象セルの値
	 * @param vtype		値の形式
	 * @return	整形された出力文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected String formatStringCellValue(EtcWorkbookManager excelbook, Cell cell, String value, ValueTypes vtype) {
		if (vtype == ValueTypes.EXCEL) {
			// Excel 書式で出力
			return excelbook.getFormattedExcelValue(cell);
		}
		else if (vtype == ValueTypes.DECIMAL) {
			// 数値に変換して出力
			BigDecimal dValue = stringToDecimal(value);
			return (dValue==null ? value : getOptionPrecision().format(dValue));
		}
		else if (vtype == ValueTypes.DATE) {
			// 日付時刻に変換して出力
			BigDecimal dValue = stringToDecimal(value);
			if (dValue != null) {
				// 時刻値に変換
				Calendar cal = excelbook.getJavaCalendarValue(dValue.doubleValue());
				if (cal != null) {
					// 日付時刻文字列として出力
					SimpleDateFormat sdf = getOptionDateFormat();
					if (sdf == null) {
						// Excel 書式で出力
						return excelbook.getFormattedExcelValue(cell);
					} else {
						// 指定のフォーマットで出力
						return sdf.format(cal.getTime());
					}
				} else {
					// 数値として出力
					return getOptionPrecision().format(dValue);
				}
			}
			else {
				// 文字列のまま出力
				return value;
			}
		}
		else if (vtype == ValueTypes.BOOL) {
			// Boolean 値に変換して出力
			value = value.trim();
			if ("false".equalsIgnoreCase(value) || "0".equals(value) || "no".equalsIgnoreCase(value) || "off".equalsIgnoreCase(value)) {
				// false
				return Boolean.FALSE.toString();
			} else {
				// true
				return Boolean.TRUE.toString();
			}
		}
		else {
			// 文字列のまま出力
			return value;
		}
	}
	
	/**
	 * 指定された <code>NUMERIC</code> セルの値を、指定された形式で整形する。
	 * @param excelbook	Excel ワークブック
	 * @param cell		対象のセル
	 * @param value		対象セルの値
	 * @param vtype		値の形式
	 * @return	整形された出力文字列
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected String formatNumericCellValue(EtcWorkbookManager excelbook, Cell cell, double value, ValueTypes vtype) {
		if (vtype == ValueTypes.EXCEL || vtype == ValueTypes.AUTO) {
			// 日付時刻値か判定
			Calendar cal = excelbook.getJavaCalendarValue(value);
			if (cal != null) {
				if (vtype == ValueTypes.EXCEL) {
					// Excel 書式で出力
					return excelbook.getFormattedExcelValue(cell);
				}
				else {
					// 日付書式かどうかを判定して出力
					CellFormatResult cfr = excelbook.getCellFormatter().format(cell);
					if (cfr.isDate()) {
						// 日付時刻値として出力
						SimpleDateFormat sdf = getOptionDateFormat();
						if (sdf == null) {
							// Excel 書式で出力
							return cfr.getText();
						} else {
							// 指定のフォーマットで出力
							return sdf.format(cal.getTime());
						}
					}
					else {
						// 数値として出力
						return getOptionPrecision().format(value);
					}
				}
			}
			else {
				// 数値として出力
				return getOptionPrecision().format(value);
			}
		}
		else if (vtype == ValueTypes.DATE) {
			// 日付時刻値として出力
			Calendar cal = excelbook.getJavaCalendarValue(value);
			if (cal != null) {
				// 日付時刻文字列として出力
				SimpleDateFormat sdf = getOptionDateFormat();
				if (sdf == null) {
					// Excel 書式で出力
					return excelbook.getFormattedExcelValue(cell);
				} else {
					// 指定のフォーマットで出力
					return sdf.format(cal.getTime());
				}
			} else {
				// 数値として出力
				return getOptionPrecision().format(value);
			}
		}
		else if (vtype == ValueTypes.BOOL) {
			// Boolean 値に変換して出力
			return Boolean.toString(value == 0.0);
		}
		else {
			// 数値として出力
			return getOptionPrecision().format(value);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
