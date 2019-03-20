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
 * @(#)Difference.java	0.1.0	2013/08/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.basicfilter.relation;

/**
 * テーブルの差(Difference)計算フィルタ。
 * 
 * @version 0.1.0	2013/08/08
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class Difference extends ssac.aadl.runtime.AADLModule
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
		Difference module = new Difference();
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
	 * $5[STR] 元のレコード順序を維持する('true' or 'false')
	 * $6[OUT] 結果の出力先
	 */
	@Override
	protected int aadlRun(String[] args) {
		if (args.length < 6) {
			System.err.println("'Difference' module needs 6 arguments! : " + args.length);
			return (1);
		}
		
		// 引数の取得
		final String inFile1			= args[0];	// $1[IN]	入力ファイル1(CSV)
		final String inFile2			= args[2];	// $3[IN]   入力ファイル2(CSV)
		final String outFile			= args[5];	// $6[OUT]	結果の出力先(CSV)
		final String strSkipRecords1	= args[1];	// $2[STR]	処理対象外レコード数1
		final String strSkipRecords2	= args[3];	// $4[STR]  処理対象外レコード数2
		final String strKeepOrder		= args[4];	// $5[STR]	元のレコード順序を維持する('true' or 'false')
		
		// 除外レコード数
		final java.math.BigDecimal numSkipRecords1 = getSkipRecordCount(strSkipRecords1, toDecimal(2));
		final java.math.BigDecimal numSkipRecords2 = getSkipRecordCount(strSkipRecords2, toDecimal(4));
		
		// 元のレコード順序を維持する
		final boolean keepOrder = toBoolean(strKeepOrder);
		
		// コンパレータ
		final ssac.aadl.runtime.csv.internal.CsvStringRecordComparator recComparator;
		if (keepOrder) {
			recComparator = new ssac.aadl.runtime.csv.internal.CsvStringRecordComparator(0);
		} else {
			recComparator = new ssac.aadl.runtime.csv.internal.CsvStringRecordComparator();
		}
		
		// 処理
		java.util.List<java.math.BigDecimal> firstSortResult1 = null;
		java.util.List<java.math.BigDecimal> firstSortResult2 = null;
		java.util.List<java.math.BigDecimal> lastResult = null;
		String tempFile1 = null;
		String tempFile2 = null;
		String tempFile3 = null;
		try {
			// ソート用テンポラリファイル作成
			tempFile1 = createTemporaryFile("union1_", ".csv");
			tempFile2 = createTemporaryFile("union2_", ".csv");
			if (keepOrder) {
				tempFile3 = createTemporaryFile("union3_", ".csv");
			}
			
			// 左テーブルのソート
			println("Sort Csv file 1...");
			firstSortResult1 = firstSortCsvFile(keepOrder, inFile1, tempFile1, numSkipRecords1, recComparator);
			println(".....done.");
			
			// 右テーブルのソート
			println("Sort Csv file 2...");
			firstSortResult2 = firstSortCsvFile(keepOrder, inFile2, tempFile2, numSkipRecords2, recComparator);
			println(".....done.");
			
			// 和集合
			println("Union...");
			if (tempFile3 != null) {
				lastResult = difference(tempFile1, tempFile2, tempFile3, keepOrder, recComparator, firstSortResult1, firstSortResult2);
			} else {
				lastResult = difference(tempFile1, tempFile2, outFile, keepOrder, recComparator, firstSortResult1, firstSortResult2);
			}
			println(".....done.");
			
			// 元の順序に戻す
			if (keepOrder) {
				println("Restore order...");
				lastResult = lastSortCsvFile(tempFile3, outFile, lastResult);
				println(".....done.");
			}
		}
		finally {
			// remove tempFile1
			if (tempFile1 != null) {
				deleteFile(tempFile1);
			}
			// remove tempFile2
			if (tempFile2 != null) {
				deleteFile(tempFile2);
			}
			// remove tempFile3
			if (tempFile3 != null) {
				deleteFile(tempFile3);
			}
		}
		
		// completed
		println("<Table1>");
		println("  Total records   : " + toString(firstSortResult1.get(0)));
		println("  Skipped records : " + toString(firstSortResult1.get(1)));
		println("  Data records    : " + toString(firstSortResult1.get(2)));
		println("  Max field count : " + toString(firstSortResult1.get(4)));
		println("<Table2>");
		println("  Total records   : " + toString(firstSortResult2.get(0)));
		println("  Skipped records : " + toString(firstSortResult2.get(1)));
		println("  Data records    : " + toString(firstSortResult2.get(2)));
		println("  Max field count : " + toString(firstSortResult2.get(4)));
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
	
	// CSV ファイルのソート
	// AADL:function firstSortCsvFile(keepOrder:Boolean, inFile:String, outFile:String, skipRecords:Decimal, sortComparator:Object):DecimalList
	private java.util.List<java.math.BigDecimal> firstSortCsvFile(boolean keepOrder, String inFile, String outFile,
																	java.math.BigDecimal skipRecords, Object sortComparator)
	{
		java.math.BigDecimal totalRecords    = java.math.BigDecimal.ZERO;
		java.math.BigDecimal headerRecords   = java.math.BigDecimal.ZERO;
		java.math.BigDecimal sortedRecords   = java.math.BigDecimal.ZERO;
		java.math.BigDecimal maxFields       = java.math.BigDecimal.ZERO;
		java.math.BigDecimal actualMaxFields = java.math.BigDecimal.ZERO;
		
		//@{
		ssac.aadl.runtime.csv.internal.CsvFileSorter sorter = new ssac.aadl.runtime.csv.internal.CsvFileSorter(
																		new java.io.File(inFile),
																		new java.io.File(outFile),
																		getDefaultCsvEncoding(), skipRecords,
																		(ssac.aadl.runtime.csv.internal.CsvRecordComparator)sortComparator);
		sorter.setAppendRecordIdAtFirst(keepOrder);
		sorter.sort();
		totalRecords    = toDecimal(sorter.getMaxRecordCount());
		headerRecords   = toDecimal(sorter.getHeaderRecordCount());
		sortedRecords   = toDecimal(sorter.getSortedRecordCount());
		maxFields       = toDecimal(sorter.getMaxFieldCount());
		actualMaxFields = (keepOrder ? maxFields.subtract(java.math.BigDecimal.ONE) : maxFields);
		//}@;
		
		return new java.util.ArrayList<java.math.BigDecimal>(java.util.Arrays.asList(totalRecords, headerRecords, sortedRecords, maxFields, actualMaxFields));
	}

	// 順序維持のための最終ソート。
	// (引数)inFile		入力ファイル
	// (引数)outFile		出力ファイル
	// (引数)mergeResult	結合結果（順序維持フィールド含む)
	private java.util.List<java.math.BigDecimal> lastSortCsvFile(String inFile, String outFile,
																java.util.List<java.math.BigDecimal> mergeResult)
	{
		java.math.BigDecimal totalRecords    = java.math.BigDecimal.ZERO;
		java.math.BigDecimal headerRecords   = mergeResult.get(1);
		java.math.BigDecimal sortedRecords   = java.math.BigDecimal.ZERO;
		java.math.BigDecimal maxFields       = java.math.BigDecimal.ZERO;
		
		//@{
		// create comparator
		ssac.aadl.runtime.csv.internal.CsvTypedRecordComparator sortComparator =
				new ssac.aadl.runtime.csv.internal.CsvTypedRecordComparator(null, "1:decimal");

		// create sort object
		ssac.aadl.runtime.csv.internal.CsvFileSorter sorter = new ssac.aadl.runtime.csv.internal.CsvFileSorter(
																		new java.io.File(inFile),
																		new java.io.File(outFile),
																		getDefaultCsvEncoding(), headerRecords,
																		sortComparator);
		//--- 除外フィールド指定
		sorter.addRemoveFieldIndexAtLast(0);
		
		// ソート
		sorter.sort();
		totalRecords    = toDecimal(sorter.getMaxRecordCount());
		headerRecords   = toDecimal(sorter.getHeaderRecordCount());
		sortedRecords   = toDecimal(sorter.getSortedRecordCount());
		maxFields       = toDecimal(sorter.getMaxFieldCount());
		//}@;
		
		return new java.util.ArrayList<java.math.BigDecimal>(java.util.Arrays.asList(totalRecords, headerRecords, sortedRecords, maxFields));
	}

	// 重複を許可しない差集合を出力する。
	// (引数)inFile1		CSV ファイルパス1
	// (引数)inFile2		CSV ファイルパス2
	// (引数)outFile		出力先のファイルパス
	// (引数)keepOrder		元の順序を維持する場合は true
	// (引数)recComparator	レコードコンパレータ
	// (引数)sortResult1	テーブル1のソート結果
	// (引数)sortResult2	テーブル2のソート結果
	// (戻り値)	成功した場合は、出力結果の最大レコード数、ヘッダレコード数、データレコード数、最大フィールド数（順序維持フィールドも含む）の順に
	//			要素を格納する数値オブジェクトの配列を返す。
	//			それ以外の場合は null を返す。
	// AADL:function difference(inFile1:String, inFile2:String, outFile:String, keepOrder:Boolean, recComparator:Object, 
	// 							sortResult1:DecimalList, sortResult2:DecimalList):DecimalList
	protected java.util.List<java.math.BigDecimal> difference(String inFile1, String inFile2, String outFile,
														boolean keepOrder, Object recComparator,
														java.util.List<java.math.BigDecimal> sortResult1,
														java.util.List<java.math.BigDecimal> sortResult2)
	{
		java.math.BigDecimal totalRecords  = java.math.BigDecimal.ZERO;
		java.math.BigDecimal headerRecords = java.math.BigDecimal.ZERO;
		java.math.BigDecimal dataRecords   = java.math.BigDecimal.ZERO;
		java.math.BigDecimal maxFields     = java.math.BigDecimal.ZERO;
		
		//@{
		// 最大フィールド数の計算
		//--- 重複しないレコードを出力するため、テーブル1の最大のフィールド数にあわせる
		int maxNumFields = Math.max(sortResult1.get(3).intValue(), sortResult2.get(3).intValue());
		
		// merge for union
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
			headerRecords = mergeHeader(maxNumFields, sortResult1.get(1).longValue(), sortResult2.get(1).longValue(), reader1, reader2, fout);
			if (headerRecords == null) {
				return null;	// 出力エラー
			}
			
			// 和集合
			dataRecords = differRecords(maxNumFields, sortResult1.get(2).longValue(), sortResult2.get(2).longValue(),
										(ssac.aadl.runtime.csv.internal.CsvStringRecordComparator)recComparator, reader1, reader2, fout);
			if (dataRecords == null) {
				return null;	// 出力エラー
			}
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
		totalRecords    = headerRecords.add(dataRecords);
		maxFields       = toDecimal(maxNumFields);
		//}@;
		
		return new java.util.ArrayList<java.math.BigDecimal>(java.util.Arrays.asList(totalRecords, headerRecords, dataRecords, maxFields));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * ヘッダレコードを出力する。
	 * この出力では、ファイル2のヘッダは出力されない。
	 * @param maxNumFields1		最大フィールド数（順序維持用フィールドも含む）
	 * @param numHeaderRecords1	テーブル1のヘッダレコード数
	 * @param numHeaderRecords2	テーブル2のヘッダレコード数
	 * @param reader1			テーブル1のリーダー
	 * @param reader2			テーブル2のリーダー
	 * @param fout		出力先となる <code>CsvFileWriter</code> オブジェクト
	 * @return	成功した場合は出力したレコード数、それ以外の場合は <tt>null</tt>
	 */
	protected java.math.BigDecimal mergeHeader(int maxNumFields, long numHeaderRecords1, long numHeaderRecords2,
												ssac.aadl.runtime.csv.internal.BigCsvFileReader reader1,
												ssac.aadl.runtime.csv.internal.BigCsvFileReader reader2,
												ssac.aadl.runtime.io.CsvFileWriter fout)
	{
		java.math.BigDecimal outRecords = java.math.BigDecimal.ZERO;
		
		// テーブル1のヘッダを出力
		for (long row = 0; row < numHeaderRecords1 && reader1.hasNext(); ++row) {
			if (!writeRecord(reader1.next(), maxNumFields, fout)) {
				return null;	// 出力エラー
			}
			outRecords = outRecords.add(java.math.BigDecimal.ONE);
		}
		
		// テーブル2のヘッダをスキップ
		for (long row = 0; row < numHeaderRecords2 && reader2.hasNext(); ++row) {
			reader2.next();
		}
		
		// completed
		return outRecords;
	}

	/**
	 * 重複のない差集合を出力する。
	 * @param maxNumFields		最大フィールド数（順序維持用フィールドも含む）
	 * @param numDataRecords1	テーブル1のヘッダを除くデータレコード数
	 * @param numDataRecords2	テーブル1のヘッダを除くデータレコード数
	 * @param recComparator	レコードコンパレータ
	 * @param reader1			テーブル1のリーダー
	 * @param reader2			テーブル1のリーダー
	 * @param fout		出力先となる <code>CsvFileWriter</code> オブジェクト
	 * @return	成功した場合は出力したレコード数、それ以外の場合は <tt>null</tt>
	 */
	protected java.math.BigDecimal differRecords(int maxNumFields,
												long numDataRecords1, long numDataRecords2,
												ssac.aadl.runtime.csv.internal.CsvStringRecordComparator recComparator,
												ssac.aadl.runtime.csv.internal.BigCsvFileReader reader1,
												ssac.aadl.runtime.csv.internal.BigCsvFileReader reader2,
												ssac.aadl.runtime.io.CsvFileWriter fout)
	{
		java.math.BigDecimal outRecords = java.math.BigDecimal.ZERO;
		java.util.List<String> lastOutRec = null;	// 直前の出力済みレコード
		java.util.List<String> rec1 = null;	// 左レコード
		java.util.List<String> rec2 = null;	// 右レコード
		int cmp = 0;
		
		// 最初のレコードを読み込む
		if (reader1.hasNext()) {
			rec1 = reader1.next();
		}
		if (reader2.hasNext()) {
			rec2 = reader2.next();
		}
		
		// 差集合 (テーブル1にのみ存在するレコードを出力)
		for (; rec1 != null && rec2 != null; ) {
			cmp = recComparator.compare(rec1, rec2);
			
			if (cmp > 0) {
				// rec2 優先
				lastOutRec = rec2;
				//--- 同一の右レコードはスキップ
				for (rec2 = null; reader2.hasNext(); ) {
					rec2 = reader2.next();
					cmp = recComparator.compare(lastOutRec, rec2);
					if (cmp != 0) {
						break;
					}
					rec2 = null;
				}
			}
			else if (cmp < 0) {
				// rec1 優先
				lastOutRec = rec1;
				outRecords = outRecords.add(java.math.BigDecimal.ONE);
				if (!writeRecord(rec1, maxNumFields, fout)) {
					return null;	// 出力エラー
				}
				//--- 同一の左レコードはスキップ
				for (rec1 = null; reader1.hasNext(); ) {
					rec1 = reader1.next();
					cmp = recComparator.compare(lastOutRec, rec1);
					if (cmp != 0) {
						break;
					}
					rec1 = null;
				}
			}
			else {
				// equals records
				lastOutRec = rec1;
				//--- 同一の左レコードはスキップ
				for (rec1 = null; reader1.hasNext(); ) {
					rec1 = reader1.next();
					cmp = recComparator.compare(lastOutRec, rec1);
					if (cmp != 0) {
						break;
					}
					rec1 = null;
				}
				//--- 同一の右レコードはスキップ
				for (rec2 = null; reader2.hasNext(); ) {
					rec2 = reader2.next();
					cmp = recComparator.compare(lastOutRec, rec2);
					if (cmp != 0) {
						break;
					}
					rec2 = null;
				}
			}
		}
		
		// テーブル1に残りレコードがあるなら、重複しないように出力
		for (; rec1 != null; ) {
			lastOutRec = rec1;
			outRecords = outRecords.add(java.math.BigDecimal.ONE);
			if (!writeRecord(rec1, maxNumFields, fout)) {
				return null;	// 出力エラー
			}
			//--- 同一の左レコードはスキップ
			for (rec1 = null; reader1.hasNext(); ) {
				rec1 = reader1.next();
				cmp = recComparator.compare(lastOutRec, rec1);
				if (cmp != 0) {
					break;
				}
				rec1 = null;
			}
		}
		
		// すべて完了
		return outRecords;
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
	
	protected boolean writeRecord(java.util.List<String> rec, int maxFields, ssac.aadl.runtime.io.CsvFileWriter fout) {
		// レコード出力
		if (!fout.writeFields(rec)) {
			return false;	// 出力エラー
		}
		
		// 空のフィールド出力
		if (!writeEmptyFields(rec.size(), maxFields, fout)) {
			return false;	// 出力エラー
		}
		
		// レコード区切り出力
		return fout.newRecord();
	}
	
	protected boolean writeRecordModifyOrder(java.math.BigDecimal orderOffset, java.util.List<String> rec, int maxFields, ssac.aadl.runtime.io.CsvFileWriter fout) {
		// レコード出力
		if (!rec.isEmpty()) {
			//--- 順序維持用オーダーにオフセット加算
			java.math.BigDecimal newOrder = toDecimal(rec.get(0)).add(orderOffset);
			rec.set(0, toString(newOrder));
			//--- 出力
			if (!fout.writeFields(rec)) {
				return false;	// 出力エラー
			}
		}
		
		// 空のフィールド出力
		if (!writeEmptyFields(rec.size(), maxFields, fout)) {
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
