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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CorrelationCoefficient.java	0.1.0	2013/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.basicfilter.statistics;

/**
 * 相関係数を求めるフィルタ。
 * 
 * @version 0.1.0	2013/08/09
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class CorrelationCoefficient extends ssac.aadl.runtime.AADLModule
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 除算、sqrt(double) の結果変換に使用する精度 **/
	static private final java.math.MathContext MATH_DIV_PREC  = java.math.MathContext.DECIMAL64;
	
	private final exalge2.ExBase BASE_NUMRECORDS_TOTAL		= new exalge2.ExBase("numTotalRecords", exalge2.ExBase.NO_HAT);
	private final exalge2.ExBase BASE_NUMRECORDS_SKIPPED	= new exalge2.ExBase("numSkippedRecords", exalge2.ExBase.NO_HAT);
	private final exalge2.ExBase BASE_NUMRECORDS_PROCESSED	= new exalge2.ExBase("numProcessedRecords", exalge2.ExBase.NO_HAT);
	private final exalge2.ExBase BASE_NUMFIELDS_MAX			= new exalge2.ExBase("numMaxFields", exalge2.ExBase.NO_HAT);
	private final exalge2.ExBase BASE_NUMFIELDS_PROCESSED	= new exalge2.ExBase("numProcessedFields", exalge2.ExBase.NO_HAT);

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CorrelationCoefficient module = new CorrelationCoefficient();
		int ret = module.aadlRun(args);
		System.exit(ret);
	}

	//------------------------------------------------------------
	// Implement ssac.aadl.runtime.AADLModule interfaces
	//------------------------------------------------------------

	@Override
	protected int _aadl$getAADLLineNumber(int javaLineNo) {
		return 0;
	}

	/*
	 * $1[IN] 入力データソース(CSV)
	 * $2[STR] 処理対象外レコード数
	 * $3[STR] 処理対象列（カンマ区切りで複数指定可能）
	 * $4[STR] 不偏分散を使用（'true' or 'false'）
	 * $5[STR] 空白ならびに無効値は 0 とする('true' or 'false')
	 * $6[OUT] 結果の出力先(CSV)
	 */
	@Override
	protected int aadlRun(String[] args) {
		if (args.length < 6) {
			System.err.println("'CsvFileSort' module needs 6 arguments! : " + args.length);
			return (1);
		}
		
		// 引数の取得
		final String inFile             	= args[0];	// $1[IN]	入力ファイル(CSV)
		final String outFile            	= args[5];	// $6[OUT]	結果の出力先(CSV)
		final String strSkipRecords     	= args[1];	// $2[STR]	処理対象外レコード数
		final String strTargetFields		= args[2];	// $3[STR]	処理対象列
		final String strUseUnbiasedVariance	= args[3];	// $4[STR]	不偏分散を使用('true' or 'false')
		final String strInvalidValueAsZero	= args[4];	// $5[STR]	空白ならびに無効値は 0 とする('true' or 'false')
		
		// 除外レコード数
		final java.math.BigDecimal numSkipRecords = getSkipRecordCount(strSkipRecords);
		
		// 処理対象列
		if (!isNaturalNumberRangeFormat(strTargetFields)) {
			//var errmsg:String = "対象列指定の記述が不正です : \"" + strTargetFields + "\"";
			String errmsg = "対象列指定の記述が不正です : \"" + strTargetFields + "\"";
			throw new RuntimeException(errmsg);
		}
		final ssac.aadl.runtime.util.range.DecimalRange fieldRange = naturalNumberRange(strTargetFields);
		
		// 不偏分散を使用
		final boolean useUnbiasedVariance = toBoolean(strUseUnbiasedVariance);
		
		// 空白ならびに無効値は 0 とする
		final boolean invalidValueAsZero = toBoolean(strInvalidValueAsZero);
		
		// データバッファ
		// @{
		FieldStatisticsMap	mapFields = new FieldStatisticsMap();
		CoefficientMap		mapCoeffs = new CoefficientMap();
		// }@;
		
		println("Calculate averages...");
		
		// 平均値の算出と、値変換エラーチェック
		exalge2.Exalge retAlge;
		if (invalidValueAsZero) {
			retAlge = aggregateValuesForInvalidAsZero(inFile, numSkipRecords, fieldRange, mapFields);
		} else {
			retAlge = aggregateValuesForInvalidAsSkip(inFile, numSkipRecords, fieldRange, mapFields);
		}
		mapFields.calcAverages();
		java.util.List<java.math.BigDecimal> fieldNumberList = mapFields.getFieldNumberList();
		java.util.List<java.math.BigDecimal> fieldValueBuffer = createFieldValueBuffer(fieldNumberList);
		
		// 不偏分散を使用する場合、レコード数は 2 以上必要
		if (useUnbiasedVariance && retAlge.get(BASE_NUMRECORDS_PROCESSED).compareTo(toDecimal(2)) < 0) {
			String errmsg = "不偏分散を計算するには、有効なレコード数が 2 以上必要です。";
			throw new RuntimeException(errmsg);
		}
		
		println("Calculate deviations...");
		
		// 偏差の算出
		if (invalidValueAsZero) {
			sumDeviationsForInvalidAsZero(inFile, retAlge.get(BASE_NUMRECORDS_TOTAL), numSkipRecords, fieldNumberList, fieldValueBuffer, mapFields, mapCoeffs);
		} else {
			sumDeviationsForInvalidAsSkip(inFile, retAlge.get(BASE_NUMRECORDS_TOTAL), numSkipRecords, fieldNumberList, fieldValueBuffer, mapFields, mapCoeffs);
		}
		
		println("Calculate correlation coefficients...");
		
		// 統計量計算
		mapFields.calcStatistics(useUnbiasedVariance);
		mapCoeffs.calcCoefficient(useUnbiasedVariance);
		
		println("Output results...");
		writeResults(outFile, fieldNumberList, mapFields, mapCoeffs, useUnbiasedVariance);
		
		// completed
		println("  Total records   : " + toString(retAlge.get(BASE_NUMRECORDS_TOTAL)));
		println("  Skipped records : " + toString(retAlge.get(BASE_NUMRECORDS_SKIPPED)));
		println("  Data records    : " + toString(retAlge.get(BASE_NUMRECORDS_PROCESSED)));
		println("Completed!");
		
		// completed
		return 0;
	}

	//------------------------------------------------------------
	// AADL functions
	//------------------------------------------------------------

	// 処理対象外レコード数の取得
	// AADL:function getSkipRecordCount(str:String):Decimal
	private java.math.BigDecimal getSkipRecordCount(String str)
	{
		if (isNull(str))
			return java.math.BigDecimal.ZERO;
		str = str.trim();
		if (isEmpty(str))
			return java.math.BigDecimal.ZERO;

		if (!isDecimal(str)) {
			String errmsg = "除外するレコード数には数字を入力してください : \"" + str + "\"";
			throw new RuntimeException(errmsg);
		}
		java.math.BigDecimal num = toDecimal(str);
		if (num.signum() > 0) {
			println("skip " + toString(num) + " records.");
		} else {
			num = java.math.BigDecimal.ZERO;
		}
		return num;
	}

	// フィールド値を格納するバッファを生成する
	// AADL:function createFieldValueBuffer(fieldNumberList:DecimalList):DecimalList
	private java.util.List<java.math.BigDecimal> createFieldValueBuffer(java.util.List<java.math.BigDecimal> fieldNumberList) {
		java.util.List<java.math.BigDecimal> list = new java.util.ArrayList<java.math.BigDecimal>(fieldNumberList.size());
		for (int i = 0; i < fieldNumberList.size(); ++i) {
			list.add(java.math.BigDecimal.ZERO);
		}
		return list;
	}

	// 空のフィールドを fieldNumberList の要素数+1分出力する
	// (戻り値)正常に出力できた場合は true を返す
	// AADL:function writeEmptyRecord(fout:CsvFileWriter, fieldNumberList:DecimalList):Boolean
	private boolean writeEmptyRecord(ssac.aadl.runtime.io.CsvFileWriter fout, java.util.List<java.math.BigDecimal> fieldNumberList) {
		if (!fout.writeField(null)) {
			return false;	// 出力エラー
		}
		for (int i = 0; i < fieldNumberList.size(); ++i) {
			if (!fout.writeField(null)) {
				return false;	// 出力エラー
			}
		}
		//--- レコード区切り文字を出力
		return fout.newRecord();
	}

	// 第1列に指定された文字列のみを出力し、あとは空のフィールドを出力する
	// (戻り値)正常に出力できた場合は true を返す
	// AADL:function writeTitle(fout:CsvFileWriter, fieldNumberList:DecimalList, title:String):Boolean
	private boolean writeTitle(ssac.aadl.runtime.io.CsvFileWriter fout, java.util.List<java.math.BigDecimal> fieldNumberList, String title) {
		//--- タイトル
		if (!fout.writeField(title)) {
			return false;	// 出力エラー
		}
		//--- 空欄の出力
		for (int i = 0; i < fieldNumberList.size(); ++i) {
			if (!fout.writeField(null)) {
				return false;	// 出力エラー
			}
		}
		//--- レコード区切り文字を出力
		return fout.newRecord();
	}

	// ヘッダレコードを出力する
	// (戻り値)正常に出力できた場合は true を返す
	// AADL:function writeHeaderRecord(fout:CsvFileWriter, fieldNumberList:DecimalList):Boolean
	private boolean writeHeaderRecord(ssac.aadl.runtime.io.CsvFileWriter fout, java.util.List<java.math.BigDecimal> fieldNumberList) {
		//--- 空フィールド
		if (!fout.writeField(null)) {
			return false;	// 出力エラー
		}
		//--- フィールド番号出力
		for (java.math.BigDecimal fldNumber : fieldNumberList) {
			if (!fout.writeField("[" + toString(fldNumber) + "]")) {
				return false;	// 出力エラー
			}
		}
		//--- レコード区切り文字を出力
		return fout.newRecord();
	}

	// 計算結果を出力する
	// (戻り値)正常に出力できた場合は true を返す
	// AADL:function writeResults(outFilename:String, fieldNumberList:DecimalList,
	//								objMapFields:Object, objMapCoeffs:Object, useUnbiasedVariance:Boolean):Boolean
	private boolean writeResults(String outFilename, java.util.List<java.math.BigDecimal> fieldNumberList,
								Object objMapFields, Object objMapCoeffs, boolean useUnbiasedVariance)
	{
		FieldStatisticsMap mapFields = (FieldStatisticsMap)objMapFields;
		CoefficientMap     mapCoeffs = (CoefficientMap)objMapCoeffs;
		
		// 出力ファイルを開く
		ssac.aadl.runtime.io.CsvFileWriter fout = newCsvFileWriter(outFilename);
		
		try {
			// 基本統計量
			if (!writeHeaderRecord(fout, fieldNumberList)) {
				return false;	// 出力エラー
			}
			//--- 有効レコード数
			if (!fout.writeField("データ数(n)")) {
				return false;	// 出力エラー
			}
			for (java.math.BigDecimal fldNumber : fieldNumberList) {
				FieldStatisticsValue field = mapFields.get(fldNumber);
				if (!fout.writeField(toString(field.numValues()))) {
					return false;	// 出力エラー
				}
			}
			if (!fout.newRecord()) {
				return false;	// 出力エラー
			}
			//--- 平均
			if (!fout.writeField("平均")) {
				return false;	// 出力エラー
			}
			for (java.math.BigDecimal fldNumber : fieldNumberList) {
				FieldStatisticsValue field = mapFields.get(fldNumber);
				if (!fout.writeField(toString(field.average()))) {
					return false;	// 出力エラー
				}
			}
			if (!fout.newRecord()) {
				return false;	// 出力エラー
			}
			//--- 自由度
			if (useUnbiasedVariance) {
				if (!fout.writeField("自由度(n-1)")) {
					return false;	// 出力エラー
				}
			} else {
				if (!fout.writeField("自由度(n)")) {
					return false;	// 出力エラー
				}
			}
			for (java.math.BigDecimal fldNumber : fieldNumberList) {
				FieldStatisticsValue field = mapFields.get(fldNumber);
				if (!fout.writeField(toString(field.numDegreeOfFreedom()))) {
					return false;	// 出力エラー
				}
			}
			if (!fout.newRecord()) {
				return false;	// 出力エラー
			}
//			//--- 偏差平方和
//			if (!fout.writeField("偏差平方和")) {
//				return false;	// 出力エラー
//			}
//			for (java.math.BigDecimal fldNumber : fieldNumberList) {
//				FieldStatisticsValue field = mapFields.get(fldNumber);
//				if (!fout.writeField(toString(field.sumSquareDeviation()))) {
//					return false;	// 出力エラー
//				}
//			}
//			if (!fout.newRecord()) {
//				return false;	// 出力エラー
//			}
			//--- 分散
			if (!fout.writeField("分散")) {
				return false;	// 出力エラー
			}
			for (java.math.BigDecimal fldNumber : fieldNumberList) {
				FieldStatisticsValue field = mapFields.get(fldNumber);
				if (!fout.writeField(toString(field.variance()))) {
					return false;	// 出力エラー
				}
			}
			if (!fout.newRecord()) {
				return false;	// 出力エラー
			}
			//--- 標準偏差
			if (!fout.writeField("標準偏差")) {
				return false;	// 出力エラー
			}
			for (java.math.BigDecimal fldNumber : fieldNumberList) {
				FieldStatisticsValue field = mapFields.get(fldNumber);
				if (!fout.writeField(toString(field.standardDeviation()))) {
					return false;	// 出力エラー
				}
			}
			if (!fout.newRecord()) {
				return false;	// 出力エラー
			}
			
			// 共分散
			int numFields = fieldNumberList.size();
			if (!writeEmptyRecord(fout, fieldNumberList)) {
				return false;	// 出力エラー
			}
			if (!writeTitle(fout, fieldNumberList, "＜共分散＞")) {
				return false;	// 出力エラー
			}
			if (!writeHeaderRecord(fout, fieldNumberList)) {
				return false;	// 出力エラー
			}
			for (int xi = 0; xi < numFields; ++xi) {
				java.math.BigDecimal xFldNumber = fieldNumberList.get(xi);
				if (!fout.writeField("[" + toString(xFldNumber) + "]")) {
					return false;	// 出力エラー
				}
				for (int yi = 0; yi < numFields; ++yi) {
					java.math.BigDecimal yFldNumber = fieldNumberList.get(yi);
					CoefficientValue coeff = mapCoeffs.getCoeff(xFldNumber, yFldNumber);
					if (!fout.writeField(toString(coeff.covariance()))) {
						return false;	// 出力エラー
					}
				}
				if (!fout.newRecord()) {
					return false;	// 出力エラー
				}
			}
			
			// 相関係数
			if (!writeEmptyRecord(fout, fieldNumberList)) {
				return false;	// 出力エラー
			}
			if (!writeTitle(fout, fieldNumberList, "＜相関係数＞")) {
				return false;	// 出力エラー
			}
			if (!writeHeaderRecord(fout, fieldNumberList)) {
				return false;	// 出力エラー
			}
			for (int xi = 0; xi < numFields; ++xi) {
				java.math.BigDecimal xFldNumber = fieldNumberList.get(xi);
				if (!fout.writeField("[" + toString(xFldNumber) + "]")) {
					return false;	// 出力エラー
				}
				for (int yi = 0; yi < numFields; ++yi) {
					java.math.BigDecimal yFldNumber = fieldNumberList.get(yi);
					CoefficientValue coeff = mapCoeffs.getCoeff(xFldNumber, yFldNumber);
					if (!fout.writeField(toString(coeff.correlation()))) {
						return false;	// 出力エラー
					}
				}
				if (!fout.newRecord()) {
					return false;	// 出力エラー
				}
			}
		}
		finally {
			closeWriter(fout);
		}
		
		return true;	// 出力正常
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void validMaxFieldCount(java.math.BigDecimal maxNumFields) {
		if (maxNumFields.compareTo(java.math.BigDecimal.ZERO) <= 0) {
			// error
			String errmsg = "フィールド（列）が存在しません。";
			throw new RuntimeException(errmsg);
		}
	}
	
	private void validTargetFieldCount(java.math.BigDecimal numTargetFields, Object fieldRange) {
		if (numTargetFields.compareTo(java.math.BigDecimal.ZERO) <= 0) {
			// error
			String errmsg = "処理対象列は存在しません : " + toString(fieldRange);
			throw new RuntimeException(errmsg);
		}
	}
	
	private void validProcessRecordCount(java.math.BigDecimal numProcessRecords) {
		if (numProcessRecords.compareTo(java.math.BigDecimal.ZERO) <= 0) {
			// error
			String errmsg = "処理可能なレコードが存在しません。";
			throw new RuntimeException(errmsg);
		}
	}
	
	private void validMinMaxProcessedRecordCount(java.math.BigDecimal numProcessRecords, java.math.BigDecimal minProcessRecords, java.math.BigDecimal maxProcessRecords) {
		if (minProcessRecords.compareTo(maxProcessRecords) != 0) {
			// error
			String errmsg = "処理可能なレコード数が各処理対象列で一致しません : (min)" + toString(minProcessRecords) + ", (max)" + toString(maxProcessRecords);
			throw new RuntimeException(errmsg);
		}
		// 念のためのチェック
		if (numProcessRecords.compareTo(maxProcessRecords) != 0) {
			// error
			String errmsg = "処理可能なレコード数が各処理対象列で一致しません : (valid)" + toString(numProcessRecords) + ", (selected)" + toString(maxProcessRecords);
			throw new RuntimeException(errmsg);
		}
	}
	
	private java.math.BigDecimal getFieldDecimalValue(java.util.List<String> rec, java.math.BigDecimal fieldNumber) {
		int index = fieldNumber.intValue() - 1;
		if (index < rec.size()) {
			String strValue = rec.get(index);
			try {
				return new java.math.BigDecimal(strValue);	// no precision
			} catch (Throwable ex) {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 無効値を 0 として、全データレコードの平均との偏差を加算する。
	 * @param inFilename		入力ファイル名
	 * @param numTotalRecords	全レコード数
	 * @param skipRecords		処理対象外レコード数
	 * @param fieldNumberList	処理対象フィールド番号のリスト
	 * @param fieldValueBuffer	フィールド値を格納するバッファ
	 * @param mapFields			フィールドの基本統計値マップ
	 * @param mapCoeffs			フィールドの係数値マップ
	 */
	private void sumDeviationsForInvalidAsZero(String inFilename, java.math.BigDecimal numTotalRecords, java.math.BigDecimal skipRecords,
												java.util.List<java.math.BigDecimal> fieldNumberList,
												java.util.List<java.math.BigDecimal> fieldValueBuffer,
												FieldStatisticsMap mapFields, CoefficientMap mapCoeffs)
	{
		java.math.BigDecimal cntRecTotal = java.math.BigDecimal.ZERO;
		ssac.aadl.runtime.io.CsvFileReader csvReader = null;
		
		// 空欄もしくは無効値を 0 にする
		try {
			csvReader = newCsvFileReader(inFilename);
			if (skipRecords.compareTo(java.math.BigDecimal.ZERO) > 0) {
				// skip あり
				for (java.util.List<String> rec : csvReader) {
					cntRecTotal = cntRecTotal.add(java.math.BigDecimal.ONE);
					if (cntRecTotal.compareTo(skipRecords) > 0) {
						appendDeviationsInvalidAsZero(rec, fieldNumberList, fieldValueBuffer, mapFields, mapCoeffs);
					}
				}
			}
			else {
				// skip なし
				for (java.util.List<String> rec : csvReader) {
					cntRecTotal = cntRecTotal.add(java.math.BigDecimal.ONE);
					appendDeviationsInvalidAsZero(rec, fieldNumberList, fieldValueBuffer, mapFields, mapCoeffs);
				}
			}
		}
		finally {
			if (csvReader != null)
				closeReader(csvReader);
		}
	}
	
	/**
	 * 無効値を含むレコードを除外した全データレコードの平均との偏差を加算する。
	 * @param inFilename		入力ファイル名
	 * @param numTotalRecords	全レコード数
	 * @param skipRecords		処理対象外レコード数
	 * @param fieldNumberList	処理対象フィールド番号のリスト
	 * @param fieldValueBuffer	フィールド値を格納するバッファ
	 * @param mapFields			フィールドの基本統計値マップ
	 * @param mapCoeffs			フィールドの係数値マップ
	 */
	private void sumDeviationsForInvalidAsSkip(String inFilename, java.math.BigDecimal numTotalRecords, java.math.BigDecimal skipRecords,
												java.util.List<java.math.BigDecimal> fieldNumberList,
												java.util.List<java.math.BigDecimal> fieldValueBuffer,
												FieldStatisticsMap mapFields, CoefficientMap mapCoeffs)
	{
		java.math.BigDecimal cntRecTotal = java.math.BigDecimal.ZERO;
		ssac.aadl.runtime.io.CsvFileReader csvReader = null;
		
		// 空欄もしくは無効値を 0 にする
		try {
			csvReader = newCsvFileReader(inFilename);
			if (skipRecords.compareTo(java.math.BigDecimal.ZERO) > 0) {
				// skip あり
				for (java.util.List<String> rec : csvReader) {
					cntRecTotal = cntRecTotal.add(java.math.BigDecimal.ONE);
					if (!rec.isEmpty() && cntRecTotal.compareTo(skipRecords) > 0) {
						appendDeviationsInvalidAsSkip(rec, fieldNumberList, fieldValueBuffer, mapFields, mapCoeffs);
					}
				}
			}
			else {
				// skip なし
				for (java.util.List<String> rec : csvReader) {
					cntRecTotal = cntRecTotal.add(java.math.BigDecimal.ONE);
					if (!rec.isEmpty()) {
						appendDeviationsInvalidAsSkip(rec, fieldNumberList, fieldValueBuffer, mapFields, mapCoeffs);
					}
				}
			}
		}
		finally {
			if (csvReader != null)
				closeReader(csvReader);
		}
	}
	
	/**
	 * 無効値を 0 として、全データレコードの値を加算する。
	 * @param inFilename		入力ファイル名
	 * @param skipRecords		処理対象外レコード数
	 * @param fieldRange		処理対象列
	 * @param mapFields			フィールドの基本統計値マップ
	 */
	private exalge2.Exalge aggregateValuesForInvalidAsZero(String inFilename, java.math.BigDecimal skipRecords,
															ssac.aadl.runtime.util.range.DecimalRange fieldRange,
															FieldStatisticsMap	mapFields)
	{
		java.math.BigDecimal maxFields = java.math.BigDecimal.ZERO;
		java.math.BigDecimal cntRecTotal = java.math.BigDecimal.ZERO;
		java.math.BigDecimal cntRecValid = java.math.BigDecimal.ZERO;
		ssac.aadl.runtime.io.CsvFileReader csvReader = null;
		
		// 空欄もしくは無効値を 0 にする
		try {
			csvReader = newCsvFileReader(inFilename);
			if (skipRecords.compareTo(java.math.BigDecimal.ZERO) > 0) {
				// skip あり
				for (java.util.List<String> rec : csvReader) {
					cntRecTotal = cntRecTotal.add(java.math.BigDecimal.ONE);
					maxFields = maxFields.max(toDecimal(rec.size()));
					if (cntRecTotal.compareTo(skipRecords) > 0) {
						if (!rec.isEmpty()) {
							sumValuesInvalidAsZero(maxFields, cntRecTotal, rec, fieldRange, mapFields);
						}
						cntRecValid = cntRecValid.add(java.math.BigDecimal.ONE);
					}
				}
			}
			else {
				// skip なし
				for (java.util.List<String> rec : csvReader) {
					cntRecTotal = cntRecTotal.add(java.math.BigDecimal.ONE);
					maxFields = maxFields.max(toDecimal(rec.size()));
					if (!rec.isEmpty()) {
						sumValuesInvalidAsZero(maxFields, cntRecTotal, rec, fieldRange, mapFields);
					}
					cntRecValid = cntRecValid.add(java.math.BigDecimal.ONE);
				}
			}
		}
		finally {
			if (csvReader != null)
				closeReader(csvReader);
		}
		
		// 最大フィールド数のチェック
		validMaxFieldCount(maxFields);

		// 処理対象レコード数のチェック
		validProcessRecordCount(cntRecValid);
		
		// すべての有効レコード数が同じになるよう、0 を追加
		sumValuesFillZero(maxFields, cntRecValid, fieldRange, mapFields);
		
		// 処理対象列の有無をチェック
		java.math.BigDecimal numTargetFields = toDecimal(mapFields.size());
		validTargetFieldCount(numTargetFields, fieldRange);
		
		// 結果を返す
		exalge2.Exalge retAlge = new exalge2.Exalge(new Object[]{
			BASE_NUMRECORDS_TOTAL    , cntRecTotal,
			BASE_NUMRECORDS_SKIPPED  , cntRecTotal.subtract(cntRecValid),
			BASE_NUMRECORDS_PROCESSED, cntRecValid,
			BASE_NUMFIELDS_MAX       , maxFields,
			BASE_NUMFIELDS_PROCESSED , numTargetFields,
		});
		return retAlge;
	}
	
	/**
	 * 無効値を含むレコードを除外した全データレコードの値を加算する。
	 * @param inFilename		入力ファイル名
	 * @param skipRecords		処理対象外レコード数
	 * @param fieldRange		処理対象列
	 * @param mapFields			フィールドの基本統計値マップ
	 */
	private exalge2.Exalge aggregateValuesForInvalidAsSkip(String inFilename, java.math.BigDecimal skipRecords,
															ssac.aadl.runtime.util.range.DecimalRange fieldRange,
															FieldStatisticsMap	mapFields)
	{
		java.math.BigDecimal maxFields = java.math.BigDecimal.ZERO;
		java.math.BigDecimal cntRecTotal = java.math.BigDecimal.ZERO;
		java.math.BigDecimal cntRecValid = java.math.BigDecimal.ZERO;
		ssac.aadl.runtime.io.CsvFileReader csvReader = null;
		
		// 空欄もしくは無効値を含むレコードをスキップ
		try {
			csvReader = newCsvFileReader(inFilename);
			if (skipRecords.compareTo(java.math.BigDecimal.ZERO) > 0) {
				// skip あり
				for (java.util.List<String> rec : csvReader) {
					cntRecTotal = cntRecTotal.add(java.math.BigDecimal.ONE);
					maxFields = maxFields.max(toDecimal(rec.size()));
					if (!rec.isEmpty() && cntRecTotal.compareTo(skipRecords) > 0) {
						if (sumValuesInvalidAsSkip(maxFields, cntRecTotal, rec, fieldRange, mapFields)) {
							cntRecValid = cntRecValid.add(java.math.BigDecimal.ONE);
						}
					}
				}
			}
			else {
				// skip なし
				for (java.util.List<String> rec : csvReader) {
					cntRecTotal = cntRecTotal.add(java.math.BigDecimal.ONE);
					maxFields = maxFields.max(toDecimal(rec.size()));
					if (!rec.isEmpty()) {
						if (sumValuesInvalidAsSkip(maxFields, cntRecTotal, rec, fieldRange, mapFields)) {
							cntRecValid = cntRecValid.add(java.math.BigDecimal.ONE);
						}
					}
				}
			}
		}
		finally {
			if (csvReader != null)
				closeReader(csvReader);
		}
		
		// 最大フィールド数のチェック
		validMaxFieldCount(maxFields);

		// 処理対象レコード数のチェック
		validProcessRecordCount(cntRecValid);
		
		// 有効レコード数の範囲を取得
		java.math.BigDecimal maxNumDataRecords;
		java.math.BigDecimal minNumDataRecords;
		{
			java.util.List<java.math.BigDecimal> retlist = getMinMaxDataRecordCount(maxFields, cntRecValid, fieldRange, mapFields);
			minNumDataRecords = retlist.get(0);
			maxNumDataRecords = retlist.get(1);
		}
		
		// 処理対象列の有無をチェック
		java.math.BigDecimal numTargetFields = toDecimal(mapFields.size());
		validTargetFieldCount(numTargetFields, fieldRange);
		
		// 有効レコード数の最小値をチェック(0 なら、処理可能なレコードが存在しないエラー)
		validProcessRecordCount(minNumDataRecords);
		
		// 有効レコード数の最大値と最小値が異なる場合は、フィールド数の異なるレコードが存在したとみなし、再読み込み
		if (maxNumDataRecords.compareTo(minNumDataRecords) != 0) {
			mapFields.clear();
			// 最大フィールド数は取得済みのものを使用
			cntRecTotal = java.math.BigDecimal.ZERO;
			cntRecValid = java.math.BigDecimal.ZERO;
			csvReader = null;
			
			try {
				csvReader = newCsvFileReader(inFilename);
				if (skipRecords.compareTo(java.math.BigDecimal.ZERO) > 0) {
					// skip あり
					for (java.util.List<String> rec : csvReader) {
						cntRecTotal = cntRecTotal.add(java.math.BigDecimal.ONE);
						if (!rec.isEmpty() && cntRecTotal.compareTo(skipRecords) > 0) {
							if (sumValuesInvalidAsSkip(maxFields, cntRecTotal, rec, fieldRange, mapFields)) {
								cntRecValid = cntRecValid.add(java.math.BigDecimal.ONE);
							}
						}
					}
				}
				else {
					// skip なし
					for (java.util.List<String> rec : csvReader) {
						cntRecTotal = cntRecTotal.add(java.math.BigDecimal.ONE);
						if (!rec.isEmpty()) {
							if (sumValuesInvalidAsSkip(maxFields, cntRecTotal, rec, fieldRange, mapFields)) {
								cntRecValid = cntRecValid.add(java.math.BigDecimal.ONE);
							}
						}
					}
				}
			}
			finally {
				if (csvReader != null)
					closeReader(csvReader);
			}
			
			// 有効レコード数の範囲を取得
			{
				java.util.List<java.math.BigDecimal> retlist = getMinMaxDataRecordCount(maxFields, cntRecValid, fieldRange, mapFields);
				minNumDataRecords = retlist.get(0);
				maxNumDataRecords = retlist.get(1);
			}
			
			// 処理対象列の有無をチェック
			numTargetFields = toDecimal(mapFields.size());
			validTargetFieldCount(numTargetFields, fieldRange);
			
			// 有効レコード数の最小値をチェック(0 なら、処理可能なレコードが存在しないエラー)
			validProcessRecordCount(minNumDataRecords);
		}
		
		// 有効レコード数の最小値と最大値が異なる場合はエラー
		validMinMaxProcessedRecordCount(cntRecValid, minNumDataRecords, maxNumDataRecords);
		
		// 結果を返す
		exalge2.Exalge retAlge = new exalge2.Exalge(new Object[]{
			BASE_NUMRECORDS_TOTAL    , cntRecTotal,
			BASE_NUMRECORDS_SKIPPED  , cntRecTotal.subtract(cntRecValid),
			BASE_NUMRECORDS_PROCESSED, cntRecValid,
			BASE_NUMFIELDS_MAX       , maxFields,
			BASE_NUMFIELDS_PROCESSED , numTargetFields,
		});
		return retAlge;
	}

	/**
	 * 有効データレコード数の最大値と最小値を返す。
	 * 列数が不ぞろいの場合や、数値以外の値がフィールドにある場合などで、最小値と最大値が異なる場合がある。
	 * @param maxNumFields	最大フィールド数
	 * @param maxNumRecords	最大レコード数
	 * @param fieldRange	処理対象列
	 * @param map			フィールドの基本統計値マップ
	 * @return	最小レコード数と最大レコード数の順に格納された配列を返す。
	 */
	private java.util.List<java.math.BigDecimal> getMinMaxDataRecordCount(java.math.BigDecimal maxNumFields, java.math.BigDecimal maxNumRecords,
																		ssac.aadl.runtime.util.range.DecimalRange fieldRange, FieldStatisticsMap map)
	{
		java.math.BigDecimal maxNumDataRecords = java.math.BigDecimal.ZERO;
		java.math.BigDecimal minNumDataRecords = toDecimal(Long.MAX_VALUE);
		java.math.BigDecimal maxFields = maxNumFields.min(fieldRange.rangeMax());
		java.math.BigDecimal fldNumber = java.math.BigDecimal.ONE;
		for (; fldNumber.compareTo(maxFields) <= 0; fldNumber = fldNumber.add(java.math.BigDecimal.ONE)) {
			if (fieldRange.containsValue(fldNumber)) {
				// 処理対象列
				FieldStatisticsValue field = map.getField(fldNumber);
				maxNumDataRecords = maxNumDataRecords.max(field.numValues());
				minNumDataRecords = minNumDataRecords.min(field.numValues());
			}
		}
		
		java.util.ArrayList<java.math.BigDecimal> list = new java.util.ArrayList<java.math.BigDecimal>(2);
		list.add(minNumDataRecords);
		list.add(maxNumDataRecords);
		return list;
	}

	/**
	 * 処理対象列の各集計値について、最大レコード数を適用する。すべてのフィールドの有効レコード数が統一され、集計値に 0 が加算されたことを意味する。
	 * @param maxNumFields	最大フィールド数
	 * @param maxNumRecords	最大レコード数
	 * @param fieldRange	処理対象列
	 * @param map			フィールドの基本統計値マップ
	 */
	private void sumValuesFillZero(java.math.BigDecimal maxNumFields, java.math.BigDecimal maxNumRecords,
									ssac.aadl.runtime.util.range.DecimalRange fieldRange, FieldStatisticsMap map)
	{
		java.math.BigDecimal maxFields = maxNumFields.min(fieldRange.rangeMax());
		java.math.BigDecimal fldNumber = java.math.BigDecimal.ONE;
		for (; fldNumber.compareTo(maxFields) <= 0; fldNumber = fldNumber.add(java.math.BigDecimal.ONE)) {
			if (fieldRange.containsValue(fldNumber)) {
				// 処理対象列
				map.fillZeroToMaxRecords(fldNumber, maxNumRecords);
			}
		}
	}

	/**
	 * レコードのフィールド値から計算された偏差を処理対象列の統計値に加算する。
	 * このメソッドでは、無効値は 0 として計算される。
	 * @param rec				処理対象レコード
	 * @param fieldNumberList	処理対象列の列番号リスト
	 * @param fieldValueBuffer	フィールド値バッファ
	 * @param mapFields			フィールドの基本統計値マップ
	 * @param mapCoeffs			フィールドの係数値マップ
	 */
	private void appendDeviationsInvalidAsZero(java.util.List<String> rec, java.util.List<java.math.BigDecimal> fieldNumberList,
											java.util.List<java.math.BigDecimal> fieldValueBuffer,
											FieldStatisticsMap mapFields, CoefficientMap mapCoeffs)
	{
		// 値取得
		int numFields = fieldNumberList.size();
		for (int index = 0; index < numFields; ++index) {
			java.math.BigDecimal fldNumber = fieldNumberList.get(index);
			java.math.BigDecimal value = getFieldDecimalValue(rec, fldNumber);
			if (value == null) {
				value = java.math.BigDecimal.ZERO;
			}
			fieldValueBuffer.set(index, value);
			mapFields.addForSquareDeviation(fldNumber, value);
		}
		
		// 全組合せの偏差を追加
		for (int xi = 0; xi < numFields; ++xi) {
			java.math.BigDecimal xValue = fieldValueBuffer.get(xi);
			FieldStatisticsValue xField = mapFields.getField(fieldNumberList.get(xi));
			for (int yi = 0; yi < numFields; ++yi) {
				java.math.BigDecimal yValue = fieldValueBuffer.get(yi);
				FieldStatisticsValue yField = mapFields.getField(fieldNumberList.get(yi));
				mapCoeffs.addForDeviation(xField, xValue, yField, yValue);
			}
		}
	}

	/**
	 * レコードのフィールド値から計算された偏差を処理対象列の統計値に加算する。
	 * このメソッドでは、無効値が含まれていた場合にはこのレコードについての処理を行わない。
	 * @param rec				処理対象レコード
	 * @param fieldNumberList	処理対象列の列番号リスト
	 * @param fieldValueBuffer	フィールド値バッファ
	 * @param mapFields			フィールドの基本統計値マップ
	 * @param mapCoeffs			フィールドの係数値マップ
	 */
	private void appendDeviationsInvalidAsSkip(java.util.List<String> rec, java.util.List<java.math.BigDecimal> fieldNumberList,
											java.util.List<java.math.BigDecimal> fieldValueBuffer,
											FieldStatisticsMap mapFields, CoefficientMap mapCoeffs)
	{
		// 値取得
		int numFields = fieldNumberList.size();
		for (int index = 0; index < numFields; ++index) {
			java.math.BigDecimal fldNumber = fieldNumberList.get(index);
			java.math.BigDecimal value = getFieldDecimalValue(rec, fldNumber);
			if (value == null) {
				// skip record
				return;
			}
			fieldValueBuffer.set(index, value);
		}
		
		// 偏差平方和の計算
		for (int index = 0; index < numFields; ++index) {
			java.math.BigDecimal fldNumber = fieldNumberList.get(index);
			java.math.BigDecimal value = fieldValueBuffer.get(index);
			mapFields.addForSquareDeviation(fldNumber, value);
		}
		
		// 全組合せの偏差を追加
		for (int xi = 0; xi < numFields; ++xi) {
			java.math.BigDecimal xValue = fieldValueBuffer.get(xi);
			FieldStatisticsValue xField = mapFields.getField(fieldNumberList.get(xi));
			for (int yi = 0; yi < numFields; ++yi) {
				java.math.BigDecimal yValue = fieldValueBuffer.get(yi);
				FieldStatisticsValue yField = mapFields.getField(fieldNumberList.get(yi));
				mapCoeffs.addForDeviation(xField, xValue, yField, yValue);
			}
		}
	}
	
	/**
	 * 指定されたレコードの値を集計する。
	 * このメソッドは、空白ならびに無効値を 0 として集計する。
	 * ただし、
	 * @param maxNumFields	現在までの最大フィールド数
	 * @param recNumber		対象レコード番号(先頭からの通し番号)
	 * @param rec			対象レコード
	 * @param fieldRange	処理対象列
	 * @param map			フィールド番号とフィールド集計データのマップ
	 * @return	数値として集計したレコードであれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	private boolean sumValuesInvalidAsZero(java.math.BigDecimal maxNumFields, java.math.BigDecimal recNumber,
											java.util.List<String> rec, ssac.aadl.runtime.util.range.DecimalRange fieldRange, FieldStatisticsMap map)
	{
		// 値取得
		boolean hasDecimal = false;
		boolean hasBlank   = false;
		boolean hasNoDigit = false;
		java.math.BigDecimal recSize = toDecimal(rec.size());
		java.math.BigDecimal maxFields = maxNumFields.min(fieldRange.rangeMax());
		int recLimit = recSize.min(maxFields).intValue();
		for (int index = 0; index < recLimit; ++index) {
			java.math.BigDecimal fldNumber = toDecimal(index+1);
			if (fieldRange.containsValue(fldNumber)) {
				// 処理対象列
				String strValue = rec.get(fldNumber.intValue()-1);
				if (strValue != null && strValue.length() > 0) {
					// 文字列あり
					if (isDecimal(strValue)) {
						// 数値
						hasDecimal = true;
						map.addDecimalCount(fldNumber);
						//--- 数値を加算
						map.addValue(fldNumber, toDecimal(strValue));
					} else {
						// 数値ではない文字列
						hasNoDigit = true;
						map.addNoDigitCount(fldNumber);
						//--- 0 を加算
						map.addValue(fldNumber, java.math.BigDecimal.ZERO);
					}
				} else {
					// 空欄
					hasBlank = true;
					map.addBlankCount(fldNumber);
					//--- 0 を加算
					map.addValue(fldNumber, java.math.BigDecimal.ZERO);
				}
			}
		}
		if (recSize.compareTo(maxFields) < 0) {
			// 最大フィールド数が、現フィールド数より大きく、処理対象最大列以下の場合、最大フィールドまで空欄として扱う
			long fieldLimit = maxNumFields.longValue();
			for (long ln = (long)rec.size() + 1L; ln <= fieldLimit; ++ln) {
				java.math.BigDecimal fldNumber = toDecimal(ln);
				if (fieldRange.containsValue(fldNumber)) {
					// 処理対象列：空欄
					hasBlank = true;
					map.addBlankCount(fldNumber);
					//--- 0 を加算
					map.addValue(fldNumber, java.math.BigDecimal.ZERO);
				}
			}
		}

		// レコード内で処理したフィールドがあれば true を返す
		return (hasDecimal || hasNoDigit || hasBlank);
	}
	
	/**
	 * 指定されたレコードの値を集計する。
	 * このメソッドは、空白ならびに無効値を含むレコードをスキップする。
	 * @param maxNumFields	現在までの最大フィールド数
	 * @param recNumber		対象レコード番号(先頭からの通し番号)
	 * @param rec			対象レコード
	 * @param fieldRange	処理対象列
	 * @param map			フィールド番号とフィールド集計データのマップ
	 * @return	数値として集計したレコードであれば <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	private boolean sumValuesInvalidAsSkip(java.math.BigDecimal maxNumFields, java.math.BigDecimal recNumber,
											java.util.List<String> rec, ssac.aadl.runtime.util.range.DecimalRange fieldRange, FieldStatisticsMap map)
	{
		// 無効値検査
		boolean hasDecimal = false;
		boolean hasBlank   = false;
		boolean hasNoDigit = false;
		java.math.BigDecimal recSize = toDecimal(rec.size());
		java.math.BigDecimal maxFields = maxNumFields.min(fieldRange.rangeMax());
		int recLimit = recSize.min(maxFields).intValue();
		for (int index = 0; index < recLimit; ++index) {
			java.math.BigDecimal fldNumber = toDecimal(index+1);
			if (fieldRange.containsValue(fldNumber)) {
				// 処理対象列
				String strValue = rec.get(fldNumber.intValue()-1);
				if (strValue != null && strValue.length() > 0) {
					// 文字列あり
					if (isDecimal(strValue)) {
						// 数値
						hasDecimal = true;
						map.addDecimalCount(fldNumber);
					} else {
						// 数値ではない文字列
						hasNoDigit = true;
						map.addNoDigitCount(fldNumber);
					}
				} else {
					// 空欄
					hasBlank = true;
					map.addBlankCount(fldNumber);
				}
			}
		}
		if (recSize.compareTo(maxFields) < 0) {
			// 最大フィールド数が、現フィールド数より大きく、処理対象最大列以下の場合、最大フィールドまで空欄として扱う
			long fieldLimit = maxNumFields.longValue();
			for (long ln = (long)rec.size() + 1L; ln <= fieldLimit; ++ln) {
				java.math.BigDecimal fldNumber = toDecimal(ln);
				if (fieldRange.containsValue(fldNumber)) {
					// 処理対象列：空欄
					hasBlank = true;
					map.addBlankCount(fldNumber);
				}
			}
		}
		
		// 無効値が含まれていれば skip
		if (!hasDecimal || hasBlank || hasNoDigit) {
			// 数値がない、もしくは空白または無効値を含む
			return false;
		}
		
		// 数値のみを集計(数値は必ず、recLimit 以内)
		java.math.BigDecimal fldNumber = java.math.BigDecimal.ZERO;
		for (int index = 0; index < recLimit; ++index) {
			fldNumber = toDecimal(index+1);
			if (fieldRange.containsValue(fldNumber)) {
				// 処理対象列
				String strValue = rec.get(index);
				map.addValue(fldNumber, toDecimal(strValue));
			}
		}
		
		return true;	// 数値加算
	}
	
	static private String makeFieldNumberPairKey(java.math.BigDecimal xFieldNumber, java.math.BigDecimal yFieldNumber) {
		return (toString(xFieldNumber) + "_" + toString(yFieldNumber));
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * フィールドの基本統計値を格納するクラス。
	 */
	static protected class FieldStatisticsValue {
		/** フィールド番号(index + 1) **/
		private final java.math.BigDecimal	_fieldNumber;
		/** 有効データ数 **/
		private java.math.BigDecimal	_numValues;
		/** 偏差データ数 **/
		private java.math.BigDecimal	_numDeviationValues;
		/** 数値を持つレコード数 **/
		private java.math.BigDecimal	_numDecimalRec;
		/** 数値ではない文字列を持つレコード数 **/
		private java.math.BigDecimal	_numNoDigitRec;
		/** 空文字のレコード数 **/
		private java.math.BigDecimal	_numBlankRec;
		/** 計算に使用する自由度 **/
		private java.math.BigDecimal	_numDegreeOfFreedom;
		/** 数値の合計 **/
		private java.math.BigDecimal	_total;
		/** 数値の平均 **/
		private java.math.BigDecimal	_average;
		/** 偏差平方和 **/
		private java.math.BigDecimal	_sumSquareDeviation;
		/** 分散 **/
		private java.math.BigDecimal	_variance;
		/** 標準偏差 **/
		private java.math.BigDecimal	_standardDeviation;
		
		public FieldStatisticsValue(java.math.BigDecimal fieldNumber) {
			_fieldNumber        = fieldNumber;
			_numValues          = java.math.BigDecimal.ZERO;
			_numDeviationValues = java.math.BigDecimal.ZERO;
			_numDecimalRec      = java.math.BigDecimal.ZERO;
			_numNoDigitRec      = java.math.BigDecimal.ZERO;
			_numBlankRec        = java.math.BigDecimal.ZERO;
			_numDegreeOfFreedom = java.math.BigDecimal.ZERO;
			_total              = java.math.BigDecimal.ZERO;
			_average            = java.math.BigDecimal.ZERO;
			_sumSquareDeviation = java.math.BigDecimal.ZERO;
			_variance           = java.math.BigDecimal.ZERO;
			_standardDeviation  = java.math.BigDecimal.ZERO;
		}
		
		public java.math.BigDecimal fieldNumber() {
			return _fieldNumber;
		}
		
		public java.math.BigDecimal numValues() {
			return _numValues;
		}
		
		public java.math.BigDecimal numDeviationValues() {
			return _numDeviationValues;
		}
		
		public java.math.BigDecimal numDecimalRecords() {
			return _numDecimalRec;
		}
		
		public java.math.BigDecimal numNoDigitRecords() {
			return _numNoDigitRec;
		}
		
		public java.math.BigDecimal numBlankRecords() {
			return _numBlankRec;
		}
		
		public java.math.BigDecimal numDegreeOfFreedom() {
			return _numDegreeOfFreedom;
		}
		
		public java.math.BigDecimal totalValue() {
			return _total;
		}
		
		public java.math.BigDecimal average() {
			return _average;
		}
		
		public java.math.BigDecimal sumSquareDeviation() {
			return _sumSquareDeviation;
		}
		
		public java.math.BigDecimal variance() {
			return _variance;
		}
		
		public java.math.BigDecimal standardDeviation() {
			return _standardDeviation;
		}
		
		public void addDecimalCount() {
			_numDecimalRec = _numDecimalRec.add(java.math.BigDecimal.ONE);
		}
		
		public void addNoDigitCount() {
			_numNoDigitRec = _numNoDigitRec.add(java.math.BigDecimal.ONE);
		}
		
		public void addBlankCount() {
			_numBlankRec = _numBlankRec.add(java.math.BigDecimal.ONE);
		}
		
		public void addValue(java.math.BigDecimal value) {
			_numValues = _numValues.add(java.math.BigDecimal.ONE);	// 精度なし
			_total = _total.add(value);
		}
		
		public void fillZeroToMaxRecords(java.math.BigDecimal maxNumRecords) {
			java.math.BigDecimal remain = maxNumRecords.subtract(_numValues);
			if (remain.compareTo(java.math.BigDecimal.ZERO) > 0) {
				_numBlankRec = _numBlankRec.add(remain);
				_numValues = maxNumRecords;
			}
		}
		
		public void calcAverage() {
			_average = _total.divide(_numValues, MATH_DIV_PREC);
		}
		
		public void addForSquareDeviation(java.math.BigDecimal value) {
			_numDeviationValues = _numDeviationValues.add(java.math.BigDecimal.ONE);
			//--- vDeviation = (value - _average)
			java.math.BigDecimal vDeviation = value.subtract(_average);
			//--- sumSquareDeviation += ((value - _average)^2)
			_sumSquareDeviation = _sumSquareDeviation.add(vDeviation.multiply(vDeviation));
		}
		
		public void calcStatistics(boolean useUnbiasedVariance) {
			if (useUnbiasedVariance) {
				// 標本数
				_numDegreeOfFreedom = _numValues.subtract(java.math.BigDecimal.ONE);
			}
			else {
				// 標本数
				_numDegreeOfFreedom = _numValues;
			}
			
			// 分散
			_variance = _sumSquareDeviation.divide(_numDegreeOfFreedom, MATH_DIV_PREC);
			
			// 標準偏差
			double stdvar = Math.sqrt(_variance.doubleValue());
			_standardDeviation = new java.math.BigDecimal(stdvar, MATH_DIV_PREC);
		}
	}

	/**
	 * 共分散、相関係数を格納するクラス。
	 */
	static protected class CoefficientValue {
		/** フィールドの一方 **/
		private final FieldStatisticsValue	_xFieldValue;
		/** フィールドのもう一方 **/
		private final FieldStatisticsValue	_yFieldValue;
		/** 偏差積の総数 **/
		private java.math.BigDecimal	_numDeviationValues;
		/** 計算に使用する自由度 **/
		private java.math.BigDecimal	_numDegreeOfFreedom;
		/** 偏差積和 **/
		private java.math.BigDecimal	_sumProductDeviation;
		/** 共分散 **/
		private java.math.BigDecimal	_covariance;
		/** 相関係数 **/
		private java.math.BigDecimal	_correlation;
		
		public CoefficientValue(FieldStatisticsValue xField, FieldStatisticsValue yField) {
			_xFieldValue = xField;
			_yFieldValue = yField;
			_numDeviationValues  = java.math.BigDecimal.ZERO;
			_numDegreeOfFreedom  = java.math.BigDecimal.ZERO;
			_sumProductDeviation = java.math.BigDecimal.ZERO;
			_covariance  = java.math.BigDecimal.ZERO;
			_correlation = java.math.BigDecimal.ZERO;
		}
		
		public FieldStatisticsValue getXFieldValue() {
			return _xFieldValue;
		}
		
		public FieldStatisticsValue getYFieldValue() {
			return _yFieldValue;
		}
		
		public java.math.BigDecimal numDeviationValues() {
			return _numDeviationValues;
		}
		
		public java.math.BigDecimal numDegreeOfFreedom() {
			return _numDegreeOfFreedom;
		}
		
		public java.math.BigDecimal sumProductDeviation() {
			return _sumProductDeviation;
		}
		
		public java.math.BigDecimal covariance() {
			return _covariance;
		}
		
		public java.math.BigDecimal correlation() {
			return _correlation;
		}
		
		public void addForDeviation(java.math.BigDecimal xValue, java.math.BigDecimal yValue) {
			_numDeviationValues = _numDeviationValues.add(java.math.BigDecimal.ONE);
			java.math.BigDecimal xDev = xValue.subtract(_xFieldValue._average);
			java.math.BigDecimal yDev = yValue.subtract(_yFieldValue._average);
			//--- sumProductDeviation += ((xValue - average(x)) * (yValue - average(y)))
			_sumProductDeviation = _sumProductDeviation.add(xDev.multiply(yDev));
		}
		
		public void calcCoefficient(boolean useUnbiasedVariance) {
			if (useUnbiasedVariance) {
				// 標本数
				_numDegreeOfFreedom = _numDeviationValues.subtract(java.math.BigDecimal.ONE);
			}
			else {
				// 標本数
				_numDegreeOfFreedom = _numDeviationValues;
			}
			
			// 共分散
			_covariance = _sumProductDeviation.divide(_numDegreeOfFreedom, MATH_DIV_PREC);
			
			// 相関係数
			java.math.BigDecimal divisor = _xFieldValue.standardDeviation().multiply(_yFieldValue.standardDeviation());
			_correlation = _covariance.divide(divisor, MATH_DIV_PREC);
		}
	}

	/**
	 * フィールド番号とフィールド集計データのマップ
	 */
	static protected class FieldStatisticsMap extends java.util.HashMap<java.math.BigDecimal, FieldStatisticsValue> {
		private static final long serialVersionUID = 1L;

		public FieldStatisticsMap() {
			super();
		}
		
		public FieldStatisticsValue getField(java.math.BigDecimal fieldNumber) {
			FieldStatisticsValue fieldData = get(fieldNumber);
			if (fieldData == null) {
				fieldData = new FieldStatisticsValue(fieldNumber);
				put(fieldNumber, fieldData);
			}
			return fieldData;
		}
		
		public void addDecimalCount(java.math.BigDecimal fieldNumber) {
			FieldStatisticsValue fieldData = getField(fieldNumber);
			fieldData.addDecimalCount();
		}
		
		public void addNoDigitCount(java.math.BigDecimal fieldNumber) {
			FieldStatisticsValue fieldData = getField(fieldNumber);
			fieldData.addNoDigitCount();
		}
		
		public void addBlankCount(java.math.BigDecimal fieldNumber) {
			FieldStatisticsValue fieldData = getField(fieldNumber);
			fieldData.addBlankCount();
		}
		
		public void addValue(java.math.BigDecimal fieldNumber, java.math.BigDecimal value) {
			FieldStatisticsValue fieldData = getField(fieldNumber);
			fieldData.addValue(value);
		}
		
		public void fillZeroToMaxRecords(java.math.BigDecimal fieldNumber, java.math.BigDecimal maxNumRecords) {
			FieldStatisticsValue fieldData = getField(fieldNumber);
			fieldData.fillZeroToMaxRecords(maxNumRecords);
		}
		
		public void calcAverages() {
			for (java.util.Map.Entry<java.math.BigDecimal, FieldStatisticsValue> entry : entrySet()) {
				entry.getValue().calcAverage();
			}
		}
		
		public void addForSquareDeviation(java.math.BigDecimal fieldNumber, java.math.BigDecimal value) {
			FieldStatisticsValue fieldData = getField(fieldNumber);
			fieldData.addForSquareDeviation(value);
		}
		
		public void calcStatistics(boolean useUnbiasedVariance) {
			for (java.util.Map.Entry<java.math.BigDecimal, FieldStatisticsValue> entry : entrySet()) {
				entry.getValue().calcStatistics(useUnbiasedVariance);
			}
		}
		
		public java.util.List<java.math.BigDecimal> getFieldNumberList() {
			java.util.ArrayList<java.math.BigDecimal> list = new java.util.ArrayList<java.math.BigDecimal>(size());
			for (java.util.Map.Entry<java.math.BigDecimal, FieldStatisticsValue> entry : entrySet()) {
				list.add(entry.getKey());
			}
			java.util.Collections.sort(list);
			return list;
		}
	}

	/**
	 * フィールド組合せキー文字列と相関係数データのマップ
	 */
	static protected class CoefficientMap extends java.util.HashMap<String, CoefficientValue> {
		private static final long serialVersionUID = 1L;

		public CoefficientMap() {
			super();
		}
		
		public CoefficientValue getCoeff(java.math.BigDecimal xFieldNumber, java.math.BigDecimal yFieldNumber) {
			String strkey = makeFieldNumberPairKey(xFieldNumber, yFieldNumber);
			return get(strkey);
		}
		
		public void addForDeviation(FieldStatisticsValue xField, java.math.BigDecimal xValue,
									FieldStatisticsValue yField, java.math.BigDecimal yValue)
		{
			String strkey = makeFieldNumberPairKey(xField.fieldNumber(), yField.fieldNumber());
			CoefficientValue data = get(strkey);
			if (data == null) {
				data = new CoefficientValue(xField, yField);
				put(strkey, data);
			}
			
			data.addForDeviation(xValue, yValue);
		}
		
		public void calcCoefficient(boolean useUnbiasedVariance) {
			for (java.util.Map.Entry<String, CoefficientValue> entry : entrySet()) {
				entry.getValue().calcCoefficient(useUnbiasedVariance);
			}
		}
	}
}
