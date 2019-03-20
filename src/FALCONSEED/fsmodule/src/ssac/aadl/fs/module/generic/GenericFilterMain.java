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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)GenericFilterMain.java	3.2.1	2015/07/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericFilterMain.java	3.2.0	2015/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.fs.module.generic;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import ssac.aadl.fs.module.FilterArgType;
import ssac.aadl.fs.module.schema.SchemaConfig;
import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.SchemaFilterArgList;
import ssac.aadl.fs.module.schema.SchemaFilterArgValue;
import ssac.aadl.fs.module.schema.SchemaUtil;
import ssac.aadl.fs.module.schema.exp.SchemaExpressionData;
import ssac.aadl.fs.module.schema.exp.SchemaExpressionEvalException;
import ssac.aadl.fs.module.schema.exp.SchemaExpressionList;
import ssac.aadl.fs.module.schema.exp.SchemaJoinConditionData;
import ssac.aadl.fs.module.schema.exp.SchemaJoinConditionList;
import ssac.aadl.fs.module.schema.table.SchemaInputCsvDataField;
import ssac.aadl.fs.module.schema.table.SchemaInputCsvDataTable;
import ssac.aadl.fs.module.schema.table.SchemaInputDataTableList;
import ssac.aadl.fs.module.schema.table.SchemaOutputCsvDataField;
import ssac.aadl.fs.module.schema.table.SchemaOutputCsvDataTable;
import ssac.aadl.fs.module.schema.table.SchemaOutputDataTableList;
import ssac.aadl.fs.module.schema.type.RuntimeSchemaValueTypeManager;
import ssac.aadl.fs.module.schema.type.SchemaDateTimeFormats;
import ssac.aadl.fs.module.schema.type.SchemaValueFormatException;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeDateTime;
import ssac.aadl.runtime.io.CsvFileReader;
import ssac.aadl.runtime.io.CsvFileWriter;
import ssac.aadl.runtime.util.Objects;

/**
 * 汎用フィルタのメインプログラム。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericFilterMain extends ssac.aadl.runtime.AADLModule
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final SchemaConfig	_genericConfig;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericFilterMain() {
		super();
		_genericConfig = new SchemaConfig();
	}
	
	public GenericFilterMain(String genericFilePath) {
		super();
		try {
			_genericConfig = GenericFilterUtil.loadGenericSchema(genericFilePath);
		} catch (Throwable ex) {
			throw new RuntimeException("Failed to load Generic filter configuration : " + String.valueOf(genericFilePath), ex);
		}
	}

	//------------------------------------------------------------
	// AADL Entry method
	//------------------------------------------------------------
	
	public int aadlRun(String[] _args) {
		int _aadl$result = 0;
		try {
			println("[" + String.valueOf(_genericConfig.getName()) + "] Generic Filter started...");
			
			// check arguments
			if (_args.length < _genericConfig.getLeastFilterArgumentCount()) {
				throw new IllegalArgumentException("Program argument is insufficient.\nNeed " + _genericConfig.getLeastFilterArgumentCount() + " arguments.");
			}
			// check input table
			if (_genericConfig.getInputTableList().isEmpty()) {
				throw new RuntimeException("No input table!");
			}
			
			try {
				// setup filter arguments
				RuntimeSchemaValueTypeManager valueTypeManager = new RuntimeSchemaValueTypeManager();
				SchemaInputDataTableList validInputTables = setupSchemaFilterArguments(valueTypeManager, _args);
				if (validInputTables.isEmpty()) {
					println("All tables have no data records.");
					return 0;	// 入力テーブルにデータレコードが存在しない場合は、処理終了
				}
				
				// 計算式に設定された即値の変換は、編集時に標準のデータ型で変換可能な値の為、実行時データ型では検証しない

				// output header records
				outputHeaders();
				
				// 内容によるテーブル処理
				SchemaInputCsvDataTable firstTable = validInputTables.get(0);
				CsvFileReader firstReader = (CsvFileReader)firstTable.getFilterArgument().getValue();
				//--- already skipped
				Iterator<List<String>> it = firstReader.iterator();
				for (; it.hasNext();) {
					setFieldValuesFromCsvRecord(valueTypeManager, firstTable, it.next());
					//--- 次のテーブルの処理
					getNextTableAndDoExpressions(valueTypeManager, validInputTables, 1);
				}
				
				// 処理完了
				println("[" + String.valueOf(_genericConfig.getName()) + "] Generic Filter succeeded.");
			}
			finally {
				cleanupSchemaFilterArguments();
			}
		}
		catch (Throwable ex) {
			_aadl$result = (1);
			aadlErrorReport(ex);
		}
		finally {
			dispose();
		}
		return _aadl$result;
	}
	
	//------------------------------------------------------------
	// Java main method
	//------------------------------------------------------------
	
	static public void main(String[] args) {
	    if (args.length < 1) {
	        System.err.println("Program argument is insufficient.");
	        System.err.println("Need 1 arguments.");
	        System.exit(1);
	    }
	    //--- transform args array
	    String schemaFile = args[0];
	    if (args.length > 1) {
	    	String[] newArgs = new String[args.length-1];
	    	System.arraycopy(args, 1, newArgs, 0, newArgs.length);
	    	args = newArgs;
	    }
    	GenericFilterMain aadlpg = new GenericFilterMain(schemaFile);
    	int retCode = aadlpg.aadlRun(args);
    	System.exit(retCode);
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void cleanupSchemaFilterArguments() {
		SchemaFilterArgList list = _genericConfig.getFilterArgList();
		for (int argIndex = 0; argIndex < list.size(); ++argIndex) {
			SchemaFilterArgValue argConfig = list.get(argIndex);
			if (argConfig.getArgumentType() == FilterArgType.IN) {
				CsvFileReader reader = (CsvFileReader)argConfig.getValue();
				if (reader != null) {
					closeReader(reader);
				}
			}
			else if (argConfig.getArgumentType() == FilterArgType.OUT) {
				CsvFileWriter writer = (CsvFileWriter)argConfig.getValue();
				if (writer != null) {
					closeWriter(writer);
				}
			}
			argConfig.setValue(null);
		}
	}
	
	protected SchemaInputDataTableList setupSchemaFilterArguments(final RuntimeSchemaValueTypeManager valueTypeManager, String[] args) {
		// 引数定義の取得
		SchemaFilterArgList list = _genericConfig.getFilterArgList();
		//--- 日付時刻書式の変換
		SchemaFilterArgValue dtfArgConfig = _genericConfig.getDateTimeFormatConfig().getFilterArgument();
		if (dtfArgConfig != null) {
			dtfArgConfig.setValue(args[dtfArgConfig.getArgumentIndex()]);
		}
		SchemaDateTimeFormats dtf;
		try {
			dtf = _genericConfig.getDateTimeFormatConfig().getDateTimeFormats();
		}
		catch (Throwable ex) {
			// format error
			if (dtfArgConfig == null) {
				// invalid user specified patterns
				String msg = String.format("Invalid preset DateTime format patterns : %s",
						_genericConfig.getDateTimeFormatConfig().toString());
				throw new RuntimeException(msg, ex);
			}
			else {
				// invalid patterns in filter argument
				String msg = String.format("Invalid DateTime format patterns in argument($%d) : %s",
						(dtfArgConfig.getArgumentIndex()+1), dtfArgConfig.getValue());
				throw new RuntimeException(msg, ex);
			}
		}
		valueTypeManager.registValueType(new SchemaValueTypeDateTime(dtf));	// この実行における日付時刻書式フォーマット
		//--- 引数値の変換
		for (int argIndex = 0; argIndex < list.size(); ++argIndex) {
			SchemaFilterArgValue argConfig = list.get(argIndex);
			FilterArgType argType = argConfig.getArgumentType();
			if (argType == FilterArgType.IN) {
				// 入力ファイル
				CsvFileReader csvReader = newCsvFileReader(args[argIndex]);
				argConfig.setValue(csvReader);
			}
			else if (argType == FilterArgType.OUT) {
				// 出力ファイル
				CsvFileWriter csvWriter = newCsvFileWriter(args[argIndex]);
				argConfig.setValue(csvWriter);
			}
			else if (dtfArgConfig == null || argIndex != dtfArgConfig.getArgumentIndex()) {
				// 文字列 : 既定のデータ型に変換
				//--- データ型変換
				String strValue = SchemaUtil.emptyStringToNull(args[argIndex]);	// 空文字は null にする
				try {
					argConfig.setValue(valueTypeManager.convertFromString(argConfig.getValueType(), strValue));
				} catch (SchemaValueFormatException ex) {
					String msg = String.format("Failed to convert argument($%d) to %s type : %s",
							(argIndex+1), argConfig.getValueType(), args[argIndex]);
					throw new RuntimeException(msg, ex);
				}
			}
		}
		
		// 処理対象外レコード数のスキップ
		SchemaInputDataTableList tables = _genericConfig.getInputTableList();
		SchemaInputDataTableList newList = new SchemaInputDataTableList(tables.size());
		for (SchemaInputCsvDataTable table : tables) {
			//--- check exists data record
			SchemaFilterArgValue argConfig = table.getFilterArgument();
			CsvFileReader reader = (CsvFileReader)argConfig.getValue();
			Iterator<List<String>> it = reader.iterator();
			skipInputRecords(table.getHeaderRecordCount(), it);
			if (it.hasNext()) {
				// exists data records
				newList.add(table);
			}
			else {
				// no data records
				closeReader(reader);
				argConfig.setValue(null);
				//--- cleanup fields
				for (SchemaInputCsvDataField field : table) {
					field.setValue(null);
				}
			}
		}
		return newList;
	}
	
	protected void outputHeaders() {
		// 引数定義から、ヘッダーレコードを出力する。
		SchemaOutputDataTableList list = _genericConfig.getOutputTableList();
		for (SchemaOutputCsvDataTable table : list) {
			CsvFileWriter writer = (CsvFileWriter)table.getFilterArgument().getValue();
			if (table.isAutoHeaderRecordEnabled()) {
				// ヘッダーレコード自動生成(1行のみ)
				for (SchemaOutputCsvDataField field : table) {
					String strValue;
					if (field.hasTargetValue()) {
						strValue = field.getTargetValue().getName();
						if (strValue == null)
							strValue = "";
					} else {
						strValue = "";
					}
					writer.writeField(strValue);
				}
				writer.newRecord();
			}
			else {
				// ユーザー指定のヘッダーレコード出力
				int headerCount = table.getHeaderRecordCount();
				for (int row = 0; row < headerCount; ++row) {
					for (SchemaOutputCsvDataField field : table) {
						String strValue = (row < field.getHeaderCount() ? field.getHeaderValue(row) : "");
						writer.writeField(strValue);
					}
					writer.newRecord();
				}
			}
		}
	}
	
	protected void skipInputRecords(int skipRecords, Iterator<List<String>> it) {
		if (skipRecords > 0) {
			for (int cnt = 0; cnt < skipRecords && it.hasNext(); ++cnt, it.next());
		}
	}
	
	protected CsvFileReader reopenCsvFileReader(CsvFileReader currentReader) {
		File openedFile = currentReader.getFile();
		closeReader(currentReader);
		return newCsvFileReader(openedFile.toString());
	}

	/**
	 * スキーマのフィールド値を、指定されたレコードの値で更新する。
	 * なお、ここでの値代入は文字列のままとする。
	 */
	protected void setFieldValuesFromCsvRecord(final RuntimeSchemaValueTypeManager valueTypeManager, SchemaInputCsvDataTable table, List<String> rec) {
		int minFields = Math.min(table.size(), rec.size());
		
		// 有効フィールドのコピー
		int col;
		for (col = 0; col < minFields; ++col) {
			SchemaInputCsvDataField field = table.get(col);
			String strValue = SchemaUtil.emptyStringToNull(rec.get(col));	// 空文字は null にする
			//--- 参照されているフィールドなら、データ型に対応する値に変換
			if (_genericConfig.isReferencedInputCsvDataField(field)) {
				// データ型に変換
				try {
					field.setValue(valueTypeManager.convertFromString(field.getValueType(), strValue));
				} catch (SchemaValueFormatException ex) {
					// 変換エラー
					String msg = String.format("Failed to convert input field string to %s type : %s <- %s",
							field.getValueType(), field.toVariableNameString(), strValue);
					throw new RuntimeException(msg, ex);
				}
			} else {
				// 参照されていないので、文字列をそのまま代入
				field.setValue(strValue);
			}
		}
		
		// 実際のフィールド数より大きいフィールドはクリア
		if (table.size() > minFields) {
			for (; col < table.size(); ++col) {
				table.get(col).setValue(null);;
			}
		}
	}
	
	protected long getNextTableAndDoExpressions(final RuntimeSchemaValueTypeManager valueTypeManager, SchemaInputDataTableList tablelist, int nextTableIndex) {
		if (nextTableIndex < tablelist.size()) {
			// exist next input table
			long outputRecCount = 0L;
			SchemaInputCsvDataTable nextTable = tablelist.get(nextTableIndex);
			CsvFileReader nextReader = reopenCsvFileReader((CsvFileReader)nextTable.getFilterArgument().getValue());
			nextTable.getFilterArgument().setValue(nextReader);
			Iterator<List<String>> it = nextReader.iterator();
			skipInputRecords(nextTable.getHeaderRecordCount(), it);
			for (; it.hasNext(); ) {
				setFieldValuesFromCsvRecord(valueTypeManager, nextTable, it.next());
				//--- 次のテーブルの処理
				outputRecCount += getNextTableAndDoExpressions(valueTypeManager, tablelist, nextTableIndex+1);
			}
			return outputRecCount;
		}
		else {
			// no more input table, do calculation
			return doCalc(valueTypeManager);
		}
	}
	
	protected long doCalc(final RuntimeSchemaValueTypeManager valueTypeManager) {
		// check join condition
		SchemaJoinConditionList joinList = _genericConfig.getJoinConditionList();
		for (SchemaJoinConditionData join : joinList) {
			SchemaElementValue val1 = join.getLeftOperand();
			SchemaElementValue val2 = join.getRightOperand();
			if (val1.getValueType().equals(val2.getValueType())) {
				// 比較可能
				if (!Objects.equals(val1.getValue(), val2.getValue())) {
					return 0L;	// 不一致
				}
			}
			else {
				// 文字列として比較
				String str1 = val1.hasValue() ? toString(val1.getValue()) : null;
				String str2 = val2.hasValue() ? toString(val2.getValue()) : null;
				if (!Objects.equals(str1, str2)) {
					return 0L;	// 不一致
				}
			}
		}
		
		// expression
		SchemaExpressionList expList = _genericConfig.getExpressionList();
		for (SchemaExpressionData exp : expList) {
			try {
				exp.eval();
			} catch (SchemaExpressionEvalException ex) {
				throw new RuntimeException(ex);
			}
		}
		
		// output fields
		SchemaOutputDataTableList outlist = _genericConfig.getOutputTableList();
		for (SchemaOutputCsvDataTable table : outlist) {
			CsvFileWriter writer = (CsvFileWriter)table.getFilterArgument().getValue();
			for (SchemaOutputCsvDataField field : table) {
				String strValue;
				if (field.hasTargetValue()) {
					Object value = field.getTargetValue().getValue();
					strValue = valueTypeManager.convertToString(field.getTargetValue().getValueType(), value);
				} else {
					strValue = null;
				}
				writer.writeField(strValue==null ? "" : strValue);
			}
			writer.newRecord();
		}
		return 1L;
	}

	static final int[][] _aadl$lno = new int[0][0];
	static final void _aadl$lnoInitializer1() {}
	static {
	    _aadl$lnoInitializer1();
	}

	protected final int _aadl$getAADLLineNumber(int javaLineNo) {
//	    int lno = java.util.Arrays.binarySearch(_aadl$lno[0], javaLineNo);
//	    if (lno >= 0) {
//	        lno = _aadl$lno[1][lno];
//	    }
//	    return lno;
		return javaLineNo;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
