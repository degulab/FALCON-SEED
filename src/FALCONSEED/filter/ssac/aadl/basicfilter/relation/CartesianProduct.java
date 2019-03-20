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
 * @(#)CartesianProduct.java	0.1.0	2013/08/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.basicfilter.relation;


/**
 * テーブルの直積フィルタ。
 * 
 * @version 0.1.0	2013/08/08
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class CartesianProduct extends ssac.aadl.runtime.AADLModule
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

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
		CartesianProduct module = new CartesianProduct();
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
	 * $1[IN]  入力データソース1(CSV)
	 * $2[STR] 処理対象外レコード数1(CSV)
	 * $3[IN]  入力データソース2(CSV)
	 * $4[STR] 処理対象外レコード数2
	 * $5[OUT] 結果の出力先
	 */
	@Override
	protected int aadlRun(String[] args) {
		if (args.length < 5) {
			System.err.println("'Cartesian product' module needs 5 arguments! : " + args.length);
			return (1);
		}
		
		// 引数の取得
		final String inFile1			= args[0];	// $1[IN]	入力ファイル1(CSV)
		final String inFile2			= args[2];	// $3[IN]   入力ファイル2(CSV)
		final String outFile			= args[4];	// $5[OUT]	結果の出力先(CSV)
		final String strSkipRecords1	= args[1];	// $2[STR]	処理対象外レコード数1
		final String strSkipRecords2	= args[3];	// $4[STR]  処理対象外レコード数2
		
		// 除外レコード数
		final java.math.BigDecimal numSkipRecords1 = getSkipRecordCount(strSkipRecords1, toDecimal(2));
		final java.math.BigDecimal numSkipRecords2 = getSkipRecordCount(strSkipRecords2, toDecimal(4));
		
		// 処理
		java.util.List<java.math.BigDecimal> csvInfo1 = null;
		java.util.List<java.math.BigDecimal> lastResult = null;
		
		// テーブル1の情報収集
		println("Check Csv file 1...");
		csvInfo1 = surveyCsvFile(inFile1, numSkipRecords1);
		println(".....done.");
		
		// 結合
		java.util.List<java.math.BigDecimal> csvInfo2 = new java.util.ArrayList<java.math.BigDecimal>(java.util.Arrays.asList(
				java.math.BigDecimal.ZERO, floor(numSkipRecords2), java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO));
		println("Cartesian production...");
		lastResult = cartetialProj(inFile1, inFile2, outFile, csvInfo1, csvInfo2);
		println(".....done.");
		
		// completed
		println("<Left table>");
		println("  Total records   : " + toString(csvInfo1.get(0)));
		println("  Skipped records : " + toString(csvInfo1.get(1)));
		println("  Data records    : " + toString(csvInfo1.get(2)));
		println("  Max field count : " + toString(csvInfo1.get(3)));
		println("<Right table>");
		println("  Total records   : " + toString(csvInfo2.get(0)));
		println("  Skipped records : " + toString(csvInfo2.get(1)));
		println("  Data records    : " + toString(csvInfo2.get(2)));
		println("  Max field count : " + toString(csvInfo2.get(3)));
		println("<Result table>");
		println("  Total records   : " + toString(lastResult.get(0)));
		println("  Skipped records : " + toString(lastResult.get(1)));
		println("  Data records    : " + toString(lastResult.get(2)));
		println("  Max field count : " + toString(lastResult.get(3)));
		println("Completed!");
		
		// completed
		return 0;
	}

	//------------------------------------------------------------
	// AADL functions
	//------------------------------------------------------------

	// 処理対象外レコード数の取得
	// AADL:function getSkipRecordCount(str:String, argno:Decimal):Decimal
	private java.math.BigDecimal getSkipRecordCount(String str, java.math.BigDecimal argno)
	{
		if (isNull(str))
			return java.math.BigDecimal.ZERO;
		str = str.trim();
		if (isEmpty(str))
			return java.math.BigDecimal.ZERO;

		if (!isDecimal(str)) {
			String errmsg = "($" + toString(argno) + ") 除外するレコード数には数字を入力してください : \"" + str + "\"";
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
	
	// CSV ファイルの情報取得
	// AADL:function surveyCsvFile(inFile:String, skipRecords:Decimal):DecimalList
	private java.util.List<java.math.BigDecimal> surveyCsvFile(String inFile, java.math.BigDecimal skipRecords)
	{
		java.math.BigDecimal totalRecords  = java.math.BigDecimal.ZERO;
		java.math.BigDecimal headerRecords = java.math.BigDecimal.ZERO;
		java.math.BigDecimal dataRecords   = java.math.BigDecimal.ZERO;
		java.math.BigDecimal maxFields     = java.math.BigDecimal.ZERO;
		
		ssac.aadl.runtime.csv.internal.BigCsvFileReader reader = null;
		try {
			int maxNumFields = 0;
			headerRecords = (skipRecords.signum() < 0 ? java.math.BigDecimal.ZERO : floor(skipRecords));
			
			reader = newBigCsvReader(inFile);
			for (; reader.hasNext(); ) {
				java.util.List<String> rec = reader.next();
				totalRecords = totalRecords.add(java.math.BigDecimal.ONE);
				maxNumFields = Math.max(maxNumFields, rec.size());
			}
			
			if (headerRecords.compareTo(totalRecords) >= 0) {
				headerRecords = totalRecords;
				dataRecords = java.math.BigDecimal.ZERO;
			} else {
				dataRecords = totalRecords.subtract(headerRecords);
			}
			maxFields = toDecimal(maxNumFields);
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
		//}@;
		
		return new java.util.ArrayList<java.math.BigDecimal>(java.util.Arrays.asList(totalRecords, headerRecords, dataRecords, maxFields));
	}

	// 左外部結合を行う。
	// (引数)inFile1		左テーブルの CSV ファイルパス
	// (引数)inFile2		右テーブルの CSV ファイルパス
	// (引数)outFile		出力先のファイルパス
	// (引数)csvInfo1	左テーブルの情報
	// (引数)csvInfo2	右テーブルの情報を格納するバッファ(入力時は、ヘッダレコード数のみ)
	// (戻り値)	成功した場合は、出力結果の最大レコード数、ヘッダレコード数、データレコード数、最大フィールド数（順序維持フィールドも含む）の順に
	//			要素を格納する数値オブジェクトの配列を返す。
	//			それ以外の場合は null を返す。
	// AADL:function cartetialProj(inFile1:String, inFile2:String, outFile:String,
	// 							sortResult1:DecimalList, sortResult2:DecimalList, keyPairs:Object):DecimalList
	protected java.util.List<java.math.BigDecimal> cartetialProj(String inFile1, String inFile2, String outFile,
														java.util.List<java.math.BigDecimal> csvInfo1,
														java.util.List<java.math.BigDecimal> csvInfo2)
	{
		java.math.BigDecimal totalRecords  = java.math.BigDecimal.ZERO;
		java.math.BigDecimal headerRecords = java.math.BigDecimal.ZERO;
		java.math.BigDecimal dataRecords   = java.math.BigDecimal.ZERO;
		java.math.BigDecimal maxFields     = java.math.BigDecimal.ZERO;
		
		//@{
		// パラメータ更新
		int maxNumFields1 = csvInfo1.get(3).intValue();
		
		// merge
		ssac.aadl.runtime.csv.internal.BigCsvFileReader reader1 = null;
		ssac.aadl.runtime.csv.internal.BigCsvFileReader reader2 = null;
		ssac.aadl.runtime.io.CsvFileWriter	fout = null;
		try {
			// create reader
			reader1 = newBigCsvReader(inFile1);
			reader2 = newBigCsvReader(inFile2);
			
			// create writer
			fout = newCsvFileWriter(outFile);
			
			// ヘッダ統合
			java.math.BigDecimal[] retHeaders = mergeHeader(maxNumFields1, csvInfo1.get(1).longValue(), csvInfo2.get(1).longValue(),
															reader1, reader2, fout);
			if (retHeaders == null) {
				return null;	// 出力エラー
			}
			headerRecords  = retHeaders[0];	// 結果のヘッダレコード数
			
			// 結合
			java.math.BigDecimal[] retRecs = mergeRecords(maxNumFields1, retHeaders[1].intValue(), reader1, reader2, fout);
			if (retRecs == null) {
				return null;	// 出力エラー
			}
			dataRecords = retRecs[0];		// 結果のデータレコード数
			csvInfo2.set(0, retRecs[1].add(csvInfo2.get(1)));	// 右テーブルの総レコード数
			csvInfo2.set(2, retRecs[1]);	// 右テーブルのデータレコード数
			csvInfo2.set(3, retRecs[2]);	// 右テーブルの最大フィールド数
			maxFields = toDecimal(maxNumFields1).add(retRecs[2]);	// 結果の最大フィールド数
			totalRecords    = headerRecords.add(dataRecords);	// 結果の総レコード数
		}
		finally {
			if (reader1 != null) {
				reader1.close();
			}
			if (reader2 != null) {
				reader2.close();
			}
			if (fout != null) {
				closeWriter(fout);
			}
		}
		//}@;
		
		return new java.util.ArrayList<java.math.BigDecimal>(java.util.Arrays.asList(totalRecords, headerRecords, dataRecords, maxFields));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected final java.util.List<String>	EMPTY_RECORD	= java.util.Collections.emptyList();

	/**
	 * ヘッダレコードを合成して出力する。
	 * @param maxNumFields1		左テーブルの最大フィールド数
	 * @param numHeaderRecords1	左テーブルのヘッダレコード数
	 * @param numHeaderRecords2	右テーブルのヘッダレコード数
	 * @param reader1			左テーブルのリーダー
	 * @param reader2			右テーブルのリーダー
	 * @param fout		出力先となる <code>CsvFileWriter</code> オブジェクト
	 * @return	成功した場合は出力したレコード数と右テーブルの最大フィールド数を格納する配列、それ以外の場合は <tt>null</tt>
	 */
	protected java.math.BigDecimal[] mergeHeader(int maxNumFields1,
												long numHeaderRecords1, long numHeaderRecords2,
												ssac.aadl.runtime.csv.internal.BigCsvFileReader reader1,
												ssac.aadl.runtime.csv.internal.BigCsvFileReader reader2,
												ssac.aadl.runtime.io.CsvFileWriter fout)
	{
		int maxNumFields2 = 0;
		java.math.BigDecimal outRecords = java.math.BigDecimal.ZERO;
		long maxHeaderRecords = Math.max(numHeaderRecords1, numHeaderRecords2);
		for (long row = 0; row < maxHeaderRecords; ++row) {
			// left header
			java.util.List<String> rec1;
			if (row < numHeaderRecords1 && reader1.hasNext()) {
				rec1 = reader1.next();
			} else {
				rec1 = EMPTY_RECORD;
			}
			
			// right header
			java.util.List<String> rec2;
			if (row < numHeaderRecords2 && reader2.hasNext()) {
				rec2 = reader2.next();
				maxNumFields2 = Math.max(maxNumFields2, rec2.size());
			} else {
				rec2 = EMPTY_RECORD;
			}
			
			// write
			if (!writeRecord(maxNumFields1, maxNumFields2, rec1, rec2, fout)) {
				return null;	// 出力エラー
			}
			
			outRecords = outRecords.add(java.math.BigDecimal.ONE);
		}
		
		// completed
		return new java.math.BigDecimal[]{outRecords, toDecimal(maxNumFields2)};
	}

	/**
	 * データレコードをキー列を照合して結合する。
	 * この出力では、右側テーブルのキー列は出力されない。
	 * @param maxNumFields1		左テーブルの最大フィールド数
	 * @param maxNumFields2		右テーブルの最大フィールド数
	 * @param reader1			左テーブルのリーダー
	 * @param reader2			右テーブルのリーダー
	 * @param fout		出力先となる <code>CsvFileWriter</code> オブジェクト
	 * @return	成功した場合は出力したレコード数、右テーブルのデータレコード数と最大フィールド数を格納する配列、それ以外の場合は <tt>null</tt>
	 */
	protected java.math.BigDecimal[] mergeRecords(int maxNumFields1, int maxNumFields2,
												ssac.aadl.runtime.csv.internal.BigCsvFileReader reader1,
												ssac.aadl.runtime.csv.internal.BigCsvFileReader reader2,
												ssac.aadl.runtime.io.CsvFileWriter fout)
	{
		java.math.BigDecimal outRecords = java.math.BigDecimal.ZERO;
		java.math.BigDecimal numRecords2 = java.math.BigDecimal.ZERO;
		java.util.List<String> rec1 = null;	// 左レコード
		java.util.List<String> rec2 = null;	// 右レコード
		
		// 左右レコードが存在しなければ、終了
		if (!reader1.hasNext() || !reader2.hasNext()) {
			return new java.math.BigDecimal[]{outRecords, toDecimal(maxNumFields2)};
		}
		
		// 右レコードの次の読み込み位置を保存
		reader2.markNextReadPosition();
		
		// 左レコードに連結 (1回目)
		rec1 = reader1.next();
		for (; reader2.hasNext(); ) {
			rec2 = reader2.next();
			numRecords2 = numRecords2.add(java.math.BigDecimal.ONE);
			maxNumFields2 = Math.max(maxNumFields2, rec2.size());
			if (!writeRecord(maxNumFields1, maxNumFields2, rec1, rec2, fout)) {
				return null;	// 出力エラー
			}
			outRecords = outRecords.add(java.math.BigDecimal.ONE);
		}
		
		// 左レコードに連結 (2回目以降)
		for (; reader1.hasNext(); ) {
			// 左レコード取得
			rec1 = reader1.next();
			
			// 右レコードを先頭位置へ移動
			reader2.seekToMarkedReadPosition();
			
			// 右レコードをすべて読み込み
			for (; reader2.hasNext(); ) {
				rec2 = reader2.next();
				if (!writeRecord(maxNumFields1, maxNumFields2, rec1, rec2, fout)) {
					return null;	// 出力エラー
				}
				outRecords = outRecords.add(java.math.BigDecimal.ONE);
			}
		}
		
		// すべて完了
		return new java.math.BigDecimal[]{outRecords, numRecords2, toDecimal(maxNumFields2)};
	}
	
	protected ssac.aadl.runtime.csv.internal.BigCsvFileReader newBigCsvReader(String filename) {
		try {
			ssac.aadl.runtime.csv.internal.BigCsvFileReader reader;
			String _defEncoding = getDefaultCsvEncoding();
			if (_defEncoding == null)
				reader = new ssac.aadl.runtime.csv.internal.BigCsvFileReader(filename==null ? null : new java.io.File(filename));
			else
				reader = new ssac.aadl.runtime.csv.internal.BigCsvFileReader(filename==null ? null : new java.io.File(filename), _defEncoding);
			return reader;
		}
		catch (Exception ex) {
			throw new ssac.aadl.runtime.AADLRuntimeException(ex);
		}
	}
	
	protected boolean writeRecord(int maxFields1, int maxFields2, java.util.List<String> rec1, java.util.List<String> rec2, ssac.aadl.runtime.io.CsvFileWriter fout) {
		// レコード1出力
		if (!fout.writeFields(rec1)) {
			return false;	// 出力エラー
		}
		
		// 空のフィールド出力
		if (!writeEmptyFields(rec1.size(), maxFields1, fout)) {
			return false;	// 出力エラー
		}
		
		// レコード2出力
		if (!fout.writeFields(rec2)) {
			return false;	// 出力エラー
		}
		
		// 空のフィールド出力
		if (!writeEmptyFields(rec2.size(), maxFields2, fout)) {
			return false;	// 出力エラー
		}
		
		// レコード区切り出力
		return fout.newRecord();
	}

	/**
	 * 指定されたフィールドインデックスから最大フィールド数まで、空のフィールドを出力する。
	 * @param beginIndex	空フィールドの出力開始位置とするフィールドインデックス
	 * @param maxCount		レコードの最大フィールド数
	 * @param fout	出力先とする <code>CsvFileWriter</code> オブジェクト
	 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	static protected boolean writeEmptyFields(int beginIndex, int maxCount, ssac.aadl.runtime.io.CsvFileWriter fout)
	{
		for (; beginIndex < maxCount; ++beginIndex) {
			if (!fout.writeField(null)) {
				return false;
			}
		}
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
