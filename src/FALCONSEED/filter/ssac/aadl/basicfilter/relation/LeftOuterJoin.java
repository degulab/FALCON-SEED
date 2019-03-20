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
 * @(#)LeftOuterJoin.java	0.1.0	2013/08/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.basicfilter.relation;


/**
 * テーブルの左外部結合フィルタ。
 * 
 * @version 0.1.0	2013/08/08
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class LeftOuterJoin extends ssac.aadl.runtime.AADLModule
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
		LeftOuterJoin module = new LeftOuterJoin();
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
	 * $5[STR] キー列番号
	 * $6[STR] 元のレコード順序を維持する('true' or 'false')
	 * $7[OUT] 結果の出力先
	 */
	@Override
	protected int aadlRun(String[] args) {
		if (args.length < 7) {
			System.err.println("'Left join' module needs 7 arguments! : " + args.length);
			return (1);
		}
		
		// 引数の取得
		final String inFile1			= args[0];	// $1[IN]	入力ファイル1(CSV)
		final String inFile2			= args[2];	// $3[IN]   入力ファイル2(CSV)
		final String outFile			= args[6];	// $7[OUT]	結果の出力先(CSV)
		final String strSkipRecords1	= args[1];	// $2[STR]	処理対象外レコード数1
		final String strSkipRecords2	= args[3];	// $4[STR]  処理対象外レコード数2
		final String strTargetFields	= args[4];	// $5[STR]	キー列番号
		final String strKeepOrder		= args[5];	// $6[STR]	元のレコード順序を維持する('true' or 'false')
		
		// 除外レコード数
		final java.math.BigDecimal numSkipRecords1 = getSkipRecordCount(strSkipRecords1, toDecimal(2));
		final java.math.BigDecimal numSkipRecords2 = getSkipRecordCount(strSkipRecords2, toDecimal(4));
		
		// 元のレコード順序を維持する
		final boolean keepOrder = toBoolean(strKeepOrder);
		
		// キー列番号
		final DefNumberPairs pairs = DefNumberPairs.parse(keepOrder, strTargetFields);
		
		// 処理
		java.util.List<java.math.BigDecimal> firstSortResult1 = null;
		java.util.List<java.math.BigDecimal> firstSortResult2 = null;
		java.util.List<java.math.BigDecimal> lastResult = null;
		String tempFile1 = null;
		String tempFile2 = null;
		String tempFile3 = null;
		try {
			// ソート用テンポラリファイル作成
			tempFile1 = createTemporaryFile("join1_", ".csv");
			tempFile2 = createTemporaryFile("join2_", ".csv");
			if (keepOrder) {
				tempFile3 = createTemporaryFile("join3_", ".csv");
			}
			
			// 左テーブルのソート
			println("Sort Csv file 1...");
			firstSortResult1 = firstSortCsvFile(keepOrder, inFile1, tempFile1, numSkipRecords1, pairs.getLeftComparator());
			println(".....done.");
			
			// 右テーブルのソート
			println("Sort Csv file 2...");
			firstSortResult2 = firstSortCsvFile(keepOrder, inFile2, tempFile2, numSkipRecords2, pairs.getRightComparator());
			println(".....done.");
			
			// 結合
			println("Join...");
			if (tempFile3 != null) {
				lastResult = leftJoin(tempFile1, tempFile2, tempFile3, firstSortResult1, firstSortResult2, pairs);
			} else {
				lastResult = leftJoin(tempFile1, tempFile2, outFile, firstSortResult1, firstSortResult2, pairs);
			}
			println(".....done.");
			
			// 元の順序に戻す
			if (keepOrder) {
				println("Restore order...");
				lastResult = lastSortCsvFile(tempFile3, outFile, firstSortResult1, lastResult);
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
		println("<Left table>");
		println("  Total records   : " + toString(firstSortResult1.get(0)));
		println("  Skipped records : " + toString(firstSortResult1.get(1)));
		println("  Data records    : " + toString(firstSortResult1.get(2)));
		println("  Max field count : " + toString(firstSortResult1.get(4)));
		println("<Right table>");
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
	// (引数)sortResult1	左テーブルのキー列によるソート結果（順序維持フィールド含む)
	private java.util.List<java.math.BigDecimal> lastSortCsvFile(String inFile, String outFile,
																java.util.List<java.math.BigDecimal> sortResult1,
																java.util.List<java.math.BigDecimal> joinResult)
	{
		java.math.BigDecimal totalRecords    = java.math.BigDecimal.ZERO;
		java.math.BigDecimal headerRecords   = joinResult.get(1);
		java.math.BigDecimal sortedRecords   = java.math.BigDecimal.ZERO;
		java.math.BigDecimal maxFields       = java.math.BigDecimal.ZERO;
		
		//@{
		// create comparator
		StringBuilder sb = new StringBuilder();
		//--- 左テーブルの順序維持レコード番号
		sb.append(1);
		sb.append(":decimal");	// 数値型昇順ソート
		//--- 右テーブルの順序維持レコード番号
		sb.append(',');
		sb.append(toString(sortResult1.get(3).add(java.math.BigDecimal.ONE)));	// 左テーブル最大フィールド数の次(右テーブルの先頭フィールド)
		sb.append(":decimal");	// 数値型昇順ソート
		ssac.aadl.runtime.csv.internal.CsvTypedRecordComparator sortComparator =
				new ssac.aadl.runtime.csv.internal.CsvTypedRecordComparator(null, sb.toString());

		// create sort object
		ssac.aadl.runtime.csv.internal.CsvFileSorter sorter = new ssac.aadl.runtime.csv.internal.CsvFileSorter(
																		new java.io.File(inFile),
																		new java.io.File(outFile),
																		getDefaultCsvEncoding(), headerRecords,
																		sortComparator);
		//--- 除外フィールド指定
		sorter.addRemoveFieldIndexAtLast(0);
		sorter.addRemoveFieldIndexAtLast(sortResult1.get(3).intValue());	// 左テーブル最大フィールド数(右テーブルの先頭フィールドインデックス)
		
		// ソート
		sorter.sort();
		totalRecords    = toDecimal(sorter.getMaxRecordCount());
		headerRecords   = toDecimal(sorter.getHeaderRecordCount());
		sortedRecords   = toDecimal(sorter.getSortedRecordCount());
		maxFields       = toDecimal(sorter.getMaxFieldCount());
		//}@;
		
		return new java.util.ArrayList<java.math.BigDecimal>(java.util.Arrays.asList(totalRecords, headerRecords, sortedRecords, maxFields));
	}

	// 左外部結合を行う。
	// (引数)inFile1		左テーブルの CSV ファイルパス
	// (引数)inFile2		右テーブルの CSV ファイルパス
	// (引数)outFile		出力先のファイルパス
	// (引数)sortResult1	左テーブルのキー列によるソート結果
	// (引数)sortResult2	右テーブルのキー列によるソート結果
	// (引数)keyPairs	キー列の関係定義
	// (戻り値)	成功した場合は、出力結果の最大レコード数、ヘッダレコード数、データレコード数、最大フィールド数（順序維持フィールドも含む）の順に
	//			要素を格納する数値オブジェクトの配列を返す。
	//			それ以外の場合は null を返す。
	// AADL:function leftJoin(keepOrder:Boolean, inFile1:String, inFile2:String, outFile:String,
	// 							sortResult1:DecimalList, sortResult2:DecimalList, keyPairs:Object):DecimalList
	protected java.util.List<java.math.BigDecimal> leftJoin(String inFile1, String inFile2, String outFile,
														java.util.List<java.math.BigDecimal> sortResult1,
														java.util.List<java.math.BigDecimal> sortResult2,
														Object keyPairs)
	{
		java.math.BigDecimal totalRecords  = java.math.BigDecimal.ZERO;
		java.math.BigDecimal headerRecords = java.math.BigDecimal.ZERO;
		java.math.BigDecimal dataRecords   = java.math.BigDecimal.ZERO;
		java.math.BigDecimal maxFields     = java.math.BigDecimal.ZERO;
		
		//@{
		// キー列の更新
		DefNumberPairs pairs = (DefNumberPairs)keyPairs;
		int maxNumFields1 = sortResult1.get(3).intValue();
		int maxNumFields2 = sortResult2.get(3).intValue();
		pairs.restructByMaxFieldCount(maxNumFields1, maxNumFields2);
		if (pairs.isEmpty()) {
			throw new RuntimeException(getErrorOutOfRangeAllFieldNumbers());
		}
		//--- 右テーブルの最大フィールド数からキー列を除外
		maxNumFields2 -= pairs.getRightMaxFieldCountExcludeKeyFields(maxNumFields2);
		
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
			headerRecords = mergeHeader(pairs, maxNumFields1, maxNumFields2, sortResult1.get(1).longValue(), sortResult2.get(1).longValue(),
										reader1, reader2, fout);
			if (headerRecords == null) {
				return null;	// 出力エラー
			}
			
			// 結合
			dataRecords = mergeRecords(pairs, maxNumFields1, maxNumFields2, sortResult1.get(2).longValue(), sortResult2.get(2).longValue(),
										reader1, reader2, fout);
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
		maxFields       = toDecimal(maxNumFields1).add(toDecimal(maxNumFields2));
		//}@;
		
		return new java.util.ArrayList<java.math.BigDecimal>(java.util.Arrays.asList(totalRecords, headerRecords, dataRecords, maxFields));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected final java.util.List<String>	EMPTY_RECORD	= java.util.Collections.emptyList();

	/**
	 * ヘッダレコードを合成して出力する。
	 * この出力では、右側テーブルのキー列は出力されない。
	 * @param pairs				キー列の関係定義
	 * @param maxNumFields1		左テーブルの最大フィールド数（順序維持用フィールドも含む）
	 * @param maxNumFields2		右テーブルの最大フィールド数（順序維持用フィールドも含み、キー列を除く）
	 * @param numHeaderRecords1	左テーブルのヘッダレコード数
	 * @param numHeaderRecords2	右テーブルのヘッダレコード数
	 * @param reader1			左テーブルのリーダー
	 * @param reader2			右テーブルのリーダー
	 * @param fout		出力先となる <code>CsvFileWriter</code> オブジェクト
	 * @return	成功した場合は出力したレコード数、それ以外の場合は <tt>null</tt>
	 */
	protected java.math.BigDecimal mergeHeader(DefNumberPairs pairs, int maxNumFields1, int maxNumFields2,
												long numHeaderRecords1, long numHeaderRecords2,
												ssac.aadl.runtime.csv.internal.BigCsvFileReader reader1,
												ssac.aadl.runtime.csv.internal.BigCsvFileReader reader2,
												ssac.aadl.runtime.io.CsvFileWriter fout)
	{
		java.math.BigDecimal outRecords = java.math.BigDecimal.ZERO;
		long maxHeaderRecords = Math.max(numHeaderRecords1, numHeaderRecords2);
		for (long row = 0; row < maxHeaderRecords; ++row) {
			// left header
			if (row < numHeaderRecords1 && reader1.hasNext()) {
				if (!pairs.writeLeftRecord(reader1.next(), maxNumFields1, fout)) {
					return null;	// 出力エラー
				}
			} else {
				if (!pairs.writeLeftRecord(EMPTY_RECORD, maxNumFields1, fout)) {
					return null;	// 出力エラー
				}
			}
			
			// right header(左テーブルと重複するキー列は除外)
			if (row < numHeaderRecords2 && reader2.hasNext()) {
				if (!pairs.writeRightRecord(reader2.next(), maxNumFields2, fout)) {
					return null;	// 出力エラー
				}
			} else {
				if (!pairs.writeRightRecord(EMPTY_RECORD, maxNumFields2, fout)) {
					return null;	// 出力エラー
				}
			}
			
			outRecords = outRecords.add(java.math.BigDecimal.ONE);
		}
		
		// completed
		return outRecords;
	}

	/**
	 * データレコードをキー列を照合して結合する。
	 * この出力では、右側テーブルのキー列は出力されない。
	 * @param pairs				キー列の関係定義
	 * @param maxNumFields1		左テーブルの最大フィールド数（順序維持用フィールドも含む）
	 * @param maxNumFields2		右テーブルの最大フィールド数（順序維持用フィールドも含み、キー列を除く）
	 * @param numDataRecords1	左テーブルのヘッダを除くデータレコード数
	 * @param numDataRecords2	右テーブルのヘッダを除くデータレコード数
	 * @param reader1			左テーブルのリーダー
	 * @param reader2			右テーブルのリーダー
	 * @param fout		出力先となる <code>CsvFileWriter</code> オブジェクト
	 * @return	成功した場合は出力したレコード数、それ以外の場合は <tt>null</tt>
	 */
	protected java.math.BigDecimal mergeRecords(DefNumberPairs pairs, int maxNumFields1, int maxNumFields2,
												long numDataRecords1, long numDataRecords2,
												ssac.aadl.runtime.csv.internal.BigCsvFileReader reader1,
												ssac.aadl.runtime.csv.internal.BigCsvFileReader reader2,
												ssac.aadl.runtime.io.CsvFileWriter fout)
	{
		java.math.BigDecimal outRecords = java.math.BigDecimal.ZERO;
		java.util.List<String> firstMatchedRec2 = null;
		java.util.List<String> rec1 = null;	// 左レコード
		java.util.List<String> rec2 = null;	// 右レコード
		boolean invalidRec2 = false;	// 読み込み済み右レコードが無効であることを示す
		int cmp = 0;
		
		// 左レコードが存在しなければ、終了
		if (!reader1.hasNext()) {
			return outRecords;
		}
		
		// 最初の右レコードを読み込む
		if (reader2.hasNext()) {
			invalidRec2 = false;
			rec2 = reader2.next();
		}
		
		// 結合
		for (; rec2 != null && reader1.hasNext(); ) {
			// 左レコードを取得
			rec1 = reader1.next();
			
			// 前回一致したキーの先頭レコードを比較
			if (firstMatchedRec2 != null) {
				cmp = pairs.compareToKeyFields(rec1, firstMatchedRec2);
				if (cmp == 0) {
					// キーが一致した場合は、処理対象は前回一致レコード
					invalidRec2 = false;
					rec2 = firstMatchedRec2;
					reader2.seekToMarkedReadPosition();	// 次の読み込み位置を更新
				} else {
					// キーが一致しないので、新しいレコードで比較
					firstMatchedRec2 = null;
					if (invalidRec2) {
						// 新しい右レコードは存在しない
						cmp = 1;	// 右レコードなし
					} else {
						// 読み込み済みの新しい右レコードと比較
						cmp = pairs.compareToKeyFields(rec1, rec2);
					}
				}
			} else {
				// 前回一致レコードは存在しないので、新しいレコードでキー比較
				cmp = pairs.compareToKeyFields(rec1, rec2);
			}
			
			// キーを比較
			if (cmp < 0) {
				// 左レコード < 右レコード
				//--- 左レコードを出力
				if (!pairs.writeLeftRecord(rec1, maxNumFields1, fout)) {
					return null;	// 出力エラー
				}
				//--- 右レコードを空フィールドとして出力
				if (!pairs.writeRightRecord(EMPTY_RECORD, maxNumFields2, fout)) {
					return null;	// 出力エラー
				}
				outRecords = outRecords.add(java.math.BigDecimal.ONE);
				continue;	// 左レコードを進める
			}
			else if (cmp > 0) {
				// 左レコード > 右レコード
				//--- 一致するキーが見つかるまで、右レコードを進める
				rec2 = null;
				for (; reader2.hasNext(); ) {
					rec2 = reader2.next();
					cmp = pairs.compareToKeyFields(rec1, rec2);
					if (cmp <= 0) {
						// 一致もしくは左レコードを進める
						break;
					}
					rec2 = null;
				}
				if (rec2 == null || cmp < 0) {
					// 一致する右レコードは存在しないので、空フィールドを出力
					//--- 左レコードを出力
					if (!pairs.writeLeftRecord(rec1, maxNumFields1, fout)) {
						return null;	// 出力エラー
					}
					//--- 右レコードを空フィールドとして出力
					if (!pairs.writeRightRecord(EMPTY_RECORD, maxNumFields2, fout)) {
						return null;	// 出力エラー
					}
					outRecords = outRecords.add(java.math.BigDecimal.ONE);
					continue;	// 左レコードを進める
				}
			}
			
			// キーが一致したレコードを出力
			firstMatchedRec2 = rec2;
			reader2.markNextReadPosition();	// 次の読み込み位置を保存
			// キーが一致している間は、すべて出力
			do {
				//--- 左レコードを出力
				if (!pairs.writeLeftRecord(rec1, maxNumFields1, fout)) {
					return null;	// 出力エラー
				}
				//--- 右レコードを出力
				if (!pairs.writeRightRecord(rec2, maxNumFields2, fout)) {
					return null;	// 出力エラー
				}
				outRecords = outRecords.add(java.math.BigDecimal.ONE);
				
				// 次のレコードを読み込み、比較
				if (reader2.hasNext()) {
					rec2 = reader2.next();
					cmp = pairs.compareToKeyFields(rec1, rec2);
				} else {
					invalidRec2 = true;
					rec2 = EMPTY_RECORD;
					cmp = 1;
				}
			} while (cmp == 0);	// 一致している間
		}
		
		// 残りの左レコードをすべて出力（右レコードが存在しないもの）
		firstMatchedRec2 = null;
		rec2 = null;
		for (; reader1.hasNext(); ) {
			rec1 = reader1.next();
			//--- 左レコードを出力
			if (!pairs.writeLeftRecord(rec1, maxNumFields1, fout)) {
				return null;	// 出力エラー
			}
			//--- 右レコードを空フィールドとして出力
			if (!pairs.writeRightRecord(EMPTY_RECORD, maxNumFields2, fout)) {
				return null;	// 出力エラー
			}
			outRecords = outRecords.add(java.math.BigDecimal.ONE);
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
	
	static protected String getErrorFieldNumberIsEmpty() {
		return "[キー列番号] キー列番号を指定してください。";
	}
	
	static protected String getErrorOutOfRangeAllFieldNumbers() {
		return "[キー列番号] に指定された左列番号は、指定された CSV ファイルの範囲外です。";
	}
	
	static protected String getErrorInvalidFieldNumber(DefFieldToken token) {
		return String.format("[キー列番号 (pos=%d)] 列番号には 1 以上の整数を指定してください : %s", (token.posStart+1), String.valueOf(token.text));
	}
	
	static protected String getErrorFieldNumberOverflow(DefFieldToken token, java.math.BigDecimal limit) {
		return String.format("[キー列番号 (pos=%d)] 列番号には %s 以下の整数を指定してください : %s", (token.posStart+1),
				ssac.aadl.runtime.AADLFunctions.toString(limit), String.valueOf(token.text));
	}
	
	static protected String getErrorInvalidPair(DefFieldToken leftToken) {
		return String.format("[キー列番号 (pos=%d)] 左列番号 '=' 右列番号と記述してください : %s", (leftToken.posStart+1), String.valueOf(leftToken.text));
	}
	
	static protected String getErrorInvalidPair(DefFieldToken leftToken, DefFieldToken equalToken) {
		return String.format("[キー列番号 (pos=%d)] 左列番号 '=' 右列番号と記述してください : %s%s", (leftToken.posStart+1),
				String.valueOf(leftToken.text), String.valueOf(equalToken.text));
	}
	
	static protected String getErrorInvalidPair(DefFieldToken leftToken, DefFieldToken equalToken, DefFieldToken rightToken) {
		return String.format("[キー列番号 (pos=%d)] 左列番号 '=' 右列番号と記述してください : %s%s%s", (leftToken.posStart+1),
				String.valueOf(leftToken.text), String.valueOf(equalToken.text), String.valueOf(rightToken.text));
	}
	
	static protected String getErrorInvalidDelimiter(DefFieldToken token) {
		return String.format("[キー列番号 (pos=%d)] 複数のキー列番号を指定する場合は、カンマ(,)で区切ってください : %s", (token.posStart+1), String.valueOf(token.text));
	}
	
	static protected String getErrorLeftFieldNumberMultiple(DefFieldToken token) {
		return String.format("[キー列番号 (pos=%d)] 左列番号が重複しています : %s", (token.posStart+1), String.valueOf(token.text));
	}
	
	static protected String getErrorRightFieldNumberMultiple(DefFieldToken token) {
		return String.format("[キー列番号 (pos=%d)] 右列番号が重複しています : %s", (token.posStart+1), String.valueOf(token.text));
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static public class DefNumberPairs
	{
		private final java.util.TreeMap<Integer, FieldIndexPair>	_map1;
		private final java.util.TreeMap<Integer, FieldIndexPair>	_map2;

		private boolean	_keepOrder;
		private int[]	_reverseFieldIndices1;
		private int[]	_reverseFieldIndices2;
		private ssac.aadl.runtime.csv.internal.CsvRecordComparator	_keyComparator1;
		private ssac.aadl.runtime.csv.internal.CsvRecordComparator	_keyComparator2;
		
		public DefNumberPairs() {
			_map1 = new java.util.TreeMap<Integer, FieldIndexPair>();
			_map2 = new java.util.TreeMap<Integer, FieldIndexPair>();
		}
		
		public boolean isKeepOrder() {
			return _keepOrder;
		}
		
		public boolean isEmpty() {
			return _map1.isEmpty();
		}
		
		public boolean containsLeftFieldIndex(int index) {
			return _map1.containsKey(index);
		}
		
		public boolean containsRightFieldIndex(int index) {
			return _map2.containsKey(index);
		}

		/**
		 * 指定された出力先に左レコードを出力する。
		 * @param rec1			左レコード
		 * @param maxNumFields1	左テーブルの最大フィールド数（順序維持用フィールドも含む）
		 * @param fout			出力先となる <code>CsvFileWriter</code> オブジェクト
		 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 */
		public boolean writeLeftRecord(java.util.List<String> rec1, int maxNumFields1, ssac.aadl.runtime.io.CsvFileWriter fout) {
			// レコード出力
			if (!fout.writeFields(rec1)) {
				return false;	// 出力エラー
			}
			
			// 空のフィールド出力
			return writeEmptyFields(rec1.size(), maxNumFields1, fout);
		}

		/**
		 * 指定された出力先に右レコードを出力し、最後にレコード区切り文字を出力する。
		 * @param rec2			右レコード
		 * @param maxNumFields2	右テーブルの最大フィールド数（順序維持用フィールドも含み、キー列を除く）
		 * @param fout			出力先となる <code>CsvFileWriter</code> オブジェクト
		 * @return	成功した場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 */
		public boolean writeRightRecord(java.util.List<String> rec2, int maxNumFields2, ssac.aadl.runtime.io.CsvFileWriter fout) {
			// レコード出力
			int findex = 0;
			for (String field : rec2) {
				if (!_map2.containsKey(findex)) {
					// キー列ではない列を出力
					if (!fout.writeField(field)) {
						return false;	// 出力エラー
					}
				}
				++findex;
			}
			
			// 空のフィールド出力
			if (!writeEmptyFields(findex, maxNumFields2, fout)) {
				return false;	// 出力エラー
			}
			
			// レコード区切り文字を出力
			return fout.newRecord();
		}

		/**
		 * 左レコードと右レコードのキー列値を比較する。
		 * なお、この比較において、左レコードのすべてのキー列が空文字もしくは <tt>null</tt> の場合は比較不能とみなし、
		 * 常に (-1) を返す。
		 * @param rec1	左レコード
		 * @param rec2	右レコード
		 * @return	左レコードと右レコードの全キー列値が等しい場合は 0、
		 * 			左レコードが右レコードのキー列値より小さい場合は (-1)、
		 * 			左レコードが右レコードのキー列値より大きい場合は 1 を返す。
		 */
		public int compareToKeyFields(java.util.List<String> rec1, java.util.List<String> rec2) {
			int cmp = 0;
			boolean hasValueAnyLeftKey = false;
			for (FieldIndexPair aPair : _map1.values()) {
				String field1 = (aPair._findex1 < rec1.size() ? rec1.get(aPair._findex1) : null);
				String field2 = (aPair._findex2 < rec2.size() ? rec2.get(aPair._findex2) : null);
				if (field1 != null && field1.length() > 0) {
					hasValueAnyLeftKey = true;
				}
				cmp = ssac.aadl.runtime.csv.internal.CsvStringRecordComparator.compareValue(field1, field2);
				if (cmp != 0) {
					break;
				}
			}
			return (hasValueAnyLeftKey ? cmp : (-1));	// 左キーがすべて空なら (-1)
		}
		
//		public boolean isEqualsKeyFields(java.util.List<String> rec1, java.util.List<String> rec2) {
//			for (FieldIndexPair aPair : _map1.values()) {
//				String field1 = (aPair._findex1 < rec1.size() ? rec1.get(aPair._findex1) : null);
//				String field2 = (aPair._findex2 < rec2.size() ? rec2.get(aPair._findex2) : null);
//				if (!ssac.aadl.runtime.AADLFunctions.isEqual(field1, field2)) {
//					return false;
//				}
//			}
//			
//			// all matches
//			return (!_map1.isEmpty());
//		}
		
		public int[] getReverseLeftFieldIndices() {
			if (_reverseFieldIndices1 == null) {
				createFieldIndicesArray();
			}
			return _reverseFieldIndices1;
		}
		
		public int[] getReverseRightFieldIndices() {
			if (_reverseFieldIndices2 == null) {
				createFieldIndicesArray();
			}
			return _reverseFieldIndices2;
		}
		
		public ssac.aadl.runtime.csv.internal.CsvRecordComparator getLeftComparator() {
			if (_keyComparator1 == null) {
				createSortComparator();
			}
			return _keyComparator1;
		}
		
		public ssac.aadl.runtime.csv.internal.CsvRecordComparator getRightComparator() {
			if (_keyComparator2 == null) {
				createSortComparator();
			}
			return _keyComparator2;
		}
		
		protected ssac.aadl.runtime.csv.internal.CsvRecordComparator createTypedRecordComparator(String condString) {
			return new ssac.aadl.runtime.csv.internal.CsvTypedRecordComparator(null, condString);
		}

		/**
		 * 左テーブルの最大フィールド数に含まれるキー列数を返す。
		 * @param leftMaxFields	左テーブルの最大フィールド数（順序維持用フィールドも含む)
		 * @return	キー列数
		 */
		public int getLeftMaxFieldCountExcludeKeyFields(int leftMaxFields) {
			int numKeyFields = 0;
			for (int findex : _map1.keySet()) {
				if (findex >= leftMaxFields) {
					break;
				}
				++numKeyFields;
			}
			return numKeyFields;
		}

		/**
		 * 右テーブルの最大フィールド数に含まれるキー列数を返す。
		 * @param rightMaxFields	右テーブルの最大フィールド数（順序維持用フィールドも含む）
		 * @return	キー列数
		 */
		public int getRightMaxFieldCountExcludeKeyFields(int rightMaxFields) {
			int numKeyFields = 0;
			for (int findex : _map2.keySet()) {
				if (findex >= rightMaxFields) {
					break;
				}
				++numKeyFields;
			}
			return numKeyFields;
		}

		/**
		 * 指定された最大フィールド数をもとに、最大フィールド数を超えた左キー列を除外する。
		 * @param leftMaxFields		左テーブルの最大フィールド数（順序維持用フィールドも含む）
		 * @param rightMaxFields	右テーブルの最大フィールド数（順序維持用フィールドも含み、キー列も含む）
		 */
		public void restructByMaxFieldCount(int leftMaxFields, int rightMaxFields) {
			if (_map1.isEmpty())
				return;
			
			int curMaxFieldIndex1 = _map1.lastKey();
			int curMaxFieldIndex2 = _map2.lastKey();
			if (curMaxFieldIndex1 < leftMaxFields && curMaxFieldIndex2 < rightMaxFields) {
				// 変更の必要なし
				return;
			}
			
			// 現在の状態をクリア
			_reverseFieldIndices1 = null;
			_reverseFieldIndices2 = null;
			_keyComparator1 = null;
			_keyComparator2 = null;
			
			// 左キーマップの削除(右側はチェックしない)
			java.util.Iterator<Integer> it = _map1.keySet().iterator();
			while (it.hasNext()) {
				FieldIndexPair aPair = _map1.get(it.next());
				if (aPair._findex1 >= leftMaxFields) {
					// 削除
					it.remove();
					_map2.remove(aPair._findex2);
				}
			}
		}
		
		protected void createFieldIndicesArray() {
			// 対象フィールドのインデックス配列（降順）を作成
			int numPairs = _map1.size();
			_reverseFieldIndices1 = new int[numPairs];
			_reverseFieldIndices2 = new int[numPairs];

			int aindex = numPairs - 1;
			for (int findex : _map1.keySet()) {
				_reverseFieldIndices1[aindex--] = findex;
			}
			
			aindex = numPairs - 1;
			for (int findex : _map2.keySet()) {
				_reverseFieldIndices2[aindex--] = findex;
			}
		}
		
		protected void createSortComparator() {
			StringBuilder sbLeft  = new StringBuilder();
			StringBuilder sbRight = new StringBuilder();
			
			java.util.Iterator<FieldIndexPair> it = _map1.values().iterator();
			if (it.hasNext()) {
				FieldIndexPair aPair = it.next();
				sbLeft .append(ssac.aadl.runtime.AADLFunctions.toString(aPair.getLeftFieldNumber()));
				sbRight.append(ssac.aadl.runtime.AADLFunctions.toString(aPair.getRightFieldNumber()));
				while (it.hasNext()) {
					aPair = it.next();
					sbLeft .append(',');
					sbLeft .append(ssac.aadl.runtime.AADLFunctions.toString(aPair.getLeftFieldNumber()));
					sbRight.append(',');
					sbRight.append(ssac.aadl.runtime.AADLFunctions.toString(aPair.getRightFieldNumber()));
				}
			}
			
			_keyComparator1 = createTypedRecordComparator(sbLeft .toString());
			_keyComparator2 = createTypedRecordComparator(sbRight.toString());
		}
		
		static public DefNumberPairs parse(boolean keepOrder, String instr) {
			// create defaults
			DefNumberPairs pairs = new DefNumberPairs();
			pairs._keepOrder = keepOrder;
			java.math.BigDecimal maxFieldNumber = (keepOrder ? toDecimal(Integer.MAX_VALUE-1) : toDecimal(Integer.MAX_VALUE));
			
			// parse
			java.util.List<DefFieldToken> tokenlist = DefFieldToken.parseToken(instr);
			
			// check pairs
			int len = tokenlist.size();
			for (int i = 0; i < len; ) {
				// left number
				DefFieldToken leftToken = tokenlist.get(i++);
				if (!leftToken.isFieldNumber()) {
					throw new RuntimeException(getErrorInvalidFieldNumber(leftToken));
				}
				
				// equal
				if (i >= len) {
					// no more token
					throw new RuntimeException(getErrorInvalidPair(leftToken));
				}
				DefFieldToken equalToken = tokenlist.get(i++);
				
				// right number
				if (i >= len) {
					// no more token
					throw new RuntimeException(getErrorInvalidPair(leftToken, equalToken));
				}
				DefFieldToken rightToken = tokenlist.get(i++);
				
				// check
				if (!equalToken.isEqualToken()) {
					throw new RuntimeException(getErrorInvalidPair(leftToken, equalToken, rightToken));
				}
				if (!rightToken.isFieldNumber()) {
					throw new RuntimeException(getErrorInvalidFieldNumber(rightToken));
				}
				
				// delimiter
				if (i < len) {
					DefFieldToken delimToken = tokenlist.get(i++);
					if (!delimToken.isDelimiterToken()) {
						throw new RuntimeException(getErrorInvalidDelimiter(delimToken));
					}
				}
				
				// value check
				java.math.BigDecimal leftNo  = leftToken.toDecimal();
				java.math.BigDecimal rightNo = rightToken.toDecimal();
				//--- left number limit
				if (leftNo.compareTo(maxFieldNumber) > 0) {
					throw new RuntimeException(getErrorFieldNumberOverflow(leftToken, maxFieldNumber));
				}
				//--- right number limit
				if (rightNo.compareTo(maxFieldNumber) > 0) {
					throw new RuntimeException(getErrorFieldNumberOverflow(rightToken, maxFieldNumber));
				}
				//--- left number multiple
				int findex1 = (keepOrder ? leftNo.intValue() : (leftNo.intValue()-1));	// 元順序維持の場合は、インデックス+1
				if (pairs._map1.containsKey(findex1)) {
					throw new RuntimeException(getErrorLeftFieldNumberMultiple(leftToken));
				}
				//--- right number multiple
				int findex2 = (keepOrder ? rightNo.intValue() : (rightNo.intValue()-1));	// 元順序維持の場合は、インデックス+1
				if (pairs._map2.containsKey(findex2)) {
					throw new RuntimeException(getErrorRightFieldNumberMultiple(rightToken));
				}
				//--- put new pair
				FieldIndexPair aPair = new FieldIndexPair(findex1, findex2);
				pairs._map1.put(findex1, aPair);
				pairs._map2.put(findex2, aPair);
			}
			
			// is empty
			if (pairs._map1.isEmpty()) {
				throw new RuntimeException(getErrorFieldNumberIsEmpty());
			}
			
			return pairs;
		}
	}
	
	static public class FieldIndexPair
	{
		private final int	_findex1;
		private final int	_findex2;
		
		public FieldIndexPair(int leftFieldIndex, int rightFieldIndex) {
			_findex1 = leftFieldIndex;
			_findex2 = rightFieldIndex;
		}
		
		public int getLeftFieldIndex() {
			return _findex1;
		}
		
		public int getRightFieldIndex() {
			return _findex2;
		}
		
		public int getLeftFieldNumber() {
			return (_findex1 + 1);
		}
		
		public int getRightFieldNumber() {
			return (_findex2 + 1);
		}
		
		public java.math.BigDecimal getDecimalLeftFieldNumber() {
			return ssac.aadl.runtime.AADLFunctions.toDecimal(_findex1 + 1);
		}
		
		public java.math.BigDecimal getDecimalRightFieldNumber() {
			return ssac.aadl.runtime.AADLFunctions.toDecimal(_findex2 + 1);
		}

		@Override
		public int hashCode() {
			int h = _findex1;
			h = 31 * h + _findex2;
			return h;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			
			if (obj != null && obj.getClass() == FieldIndexPair.class) {
				FieldIndexPair aPair = (FieldIndexPair)obj;
				if (this._findex1==aPair._findex1 && this._findex2==aPair._findex2) {
					return true;
				}
			}
			
			return false;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(_findex1 + 1);
			sb.append("]=[");
			sb.append(_findex2 + 1);
			sb.append("]");
			return sb.toString();
		}
	}
	
	static public class NumberPair
	{
		private final java.math.BigDecimal	_leftFieldNo;
		private final java.math.BigDecimal	_rightFieldNo;
		
		public NumberPair(java.math.BigDecimal leftNo, java.math.BigDecimal rightNo) {
			_leftFieldNo  = leftNo;
			_rightFieldNo = rightNo;
		}
		
		public java.math.BigDecimal getLeftFieldNo() {
			return _leftFieldNo;
		}
		
		public java.math.BigDecimal getRightFieldNo() {
			return _rightFieldNo;
		}
		
		public boolean isEqualsLeftFieldNo(java.math.BigDecimal value) {
			return (_leftFieldNo.compareTo(value) == 0);
		}
		
		public boolean isEqualsRightFieldNo(java.math.BigDecimal value) {
			return (_rightFieldNo.compareTo(value) == 0);
		}

		@Override
		public int hashCode() {
			int h = _leftFieldNo.hashCode();
			h = 31 * h + _rightFieldNo.hashCode();
			return h;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			
			if (obj != null && obj.getClass() == NumberPair.class) {
				NumberPair aPair = (NumberPair)obj;
				if (this._leftFieldNo.compareTo(aPair._leftFieldNo) == 0 && this._rightFieldNo.compareTo(aPair._rightFieldNo) == 0) {
					return true;
				}
			}
			
			return false;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append(ssac.aadl.runtime.AADLFunctions.toString(_leftFieldNo));
			sb.append("]=[");
			sb.append(ssac.aadl.runtime.AADLFunctions.toString(_rightFieldNo));
			sb.append("]");
			return sb.toString();
		}
	}
	
	static public class DefFieldToken
	{
		static public final int	TOKEN_UNKNOWN	= -1;
		static public final int	TOKEN_COMMA		= 1;
		static public final int TOKEN_EQUAL		= 2;
		static public final int	TOKEN_TEXT		= 10;
		
		public final int	posStart;
		public final int	posEnd;
		public final int	type;
		public final String	text;
		
		protected DefFieldToken(int startPos, int endPos, int tokenType, String tokenText) {
			posStart = startPos;
			posEnd   = endPos;
			type     = tokenType;
			text     = tokenText;
		}
		
		public boolean isTextToken() {
			return (type == TOKEN_TEXT);
		}
		
		public boolean isDelimiterToken() {
			return (type == TOKEN_COMMA);
		}
		
		public boolean isEqualToken() {
			return (type == TOKEN_EQUAL);
		}
		
		public String getText() {
			return text;
		}
		
		public java.math.BigDecimal toDecimal() {
			java.math.BigDecimal decimal;
			try {
				decimal = new java.math.BigDecimal(getText());
			} catch (Throwable ex) {
				decimal = null;
			}
			return decimal;
		}
		
		public boolean isDecimal() {
			return (toDecimal() != null);
		}
		
		public boolean isFieldNumber() {
			java.math.BigDecimal decimal = toDecimal();
			if (decimal == null) {
				return false;	// not decimal
			}
			
			if (decimal.compareTo(java.math.BigDecimal.ONE) < 0) {
				return false;	// less then 1
			}
			
			try {
				decimal.toBigIntegerExact();
				return true;
			} catch (ArithmeticException ex) {
				return false;
			}
		}
		
		static private DefFieldToken createToken(final String instr, int startPos, int endPos, int tokenType) {
			String text = instr.substring(startPos, endPos);
			return new DefFieldToken(startPos, endPos, tokenType, text);
		}
		
		static public java.util.List<DefFieldToken> parseToken(final String instr) {
			java.util.ArrayList<DefFieldToken> tokenlist = new java.util.ArrayList<DefFieldToken>();
			if (instr == null)
				return tokenlist;
			int strlen = instr.length();

			int pos = 0;
			char ch = 0;
			for (; pos < strlen;) {
				ch = instr.charAt(pos);
				
				// skip white space
				if (ch == ' ') {
					for (; pos < strlen; pos++) {
						ch = instr.charAt(pos);
						if (ch != ' ') {
							break;
						}
					}
					if (pos >= strlen) {
						// terminate input
						break;
					}
				}
				
				// check string
				if (ch == '=') {
					// equal
					int spos = pos;
					++pos;
					tokenlist.add(createToken(instr, spos, pos, TOKEN_EQUAL));
				}
				else if (ch == ',') {
					// comma
					int spos = pos;
					++pos;
					tokenlist.add(createToken(instr, spos, pos, TOKEN_COMMA));
				}
				else {
					// normal text
					int spos = pos;
					for (++pos; pos < strlen; pos++) {
						ch = instr.charAt(pos);
						if (ch == ' ' || ch == '=' || ch == ',') {
							// found delimiter
							break;
						}
					}
					tokenlist.add(createToken(instr, spos, pos, TOKEN_TEXT));
				}
			}
			
			return tokenlist;
		}
	}
}
