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
 * @(#)ExBalanceTable.java	0.1.0	2013/05/31
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.basicfilter.exalge;

/**
 * CSVファイルを交換代数標準形に変換するフィルタ。
 * 
 * @version 0.1.0	2013/05/31
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class CsvToExalge extends ssac.aadl.runtime.AADLModule
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String ITEM_IGNORE	= "ignore";
	static protected final String ITEM_DEBIT	= "debit";
	static protected final String ITEM_CREDIT	= "credit";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Entry method
	//------------------------------------------------------------

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CsvToExalge cte = new CsvToExalge();
		int ret = cte.aadlRun(args);
		System.exit(ret);
	}

	//------------------------------------------------------------
	// Implement ssac.aadl.runtime.AADLModule interfaces
	//------------------------------------------------------------

	@Override
	protected int _aadl$getAADLLineNumber(int javaLineNo) {
		return 0;
	}

	@Override
	protected int aadlRun(String[] args) {
		if (args.length < 8) {
			System.err.println("'CsvFileSort' module needs 8 arguments! : " + args.length);
			return (1);
		}
		
		// 引数の取得
		final String inFile             = args[0];	// $1[IN]	入力ファイル(CSV)
		final String inTable			= args[1];	// $2[IN]	貸借科目定義テーブル(CSV)
		final String outFile            = args[7];	// $8[OUT]	出力先ファイル(交換代数標準形CSV)
		final String strSkipRecords     = args[2];	// $3[STR]	処理対象外レコード数
		final String strDebitDef		= args[3];	// $4[STR]	借方列指定（値列、科目名列、単位キー列、時間キー列、主体キー列）
		final String strCreditDef		= args[4];	// $5[STR]	貸方列指定（値列、科目名列、単位キー列、時間キー列、主体キー列）
		final String strUnknownItem		= args[5];	// $6[STR]	未定義科目の処理方法('ignore' || 'debit' || or 'credit')
		final String strSplitExalge		= args[6];	// $7[STR]	交換代数集合として出力('true' or 'false')

		// 除外レコード数
		final java.math.BigDecimal numSkipRecords = getSkipRecordCount(strSkipRecords);
		
		// 貸借科目定義テーブルの読み込み
		final Object balanceTable = readBalanceTable(inTable);
		
		// 借方列指定の取得
		final Object defDebit = getDebitFieldDef(strDebitDef);
		
		// 貸方列指定の取得
		final Object defCredit = getCreditFieldDef(strCreditDef);
		
		// check 貸借指定
		//@{
			DefFieldForExalge dd = (DefFieldForExalge)defDebit;
			DefFieldForExalge dc = (DefFieldForExalge)defCredit;
			if (dd.fieldOfValue()==dc.fieldOfValue() && dd.fieldOfName()==dc.fieldOfName()) {
				throw new RuntimeException(getErrorDebitCreditValueAndNameEquals());
			}
		//}@;
		final boolean sameDebitCreditValue = (dd.fieldOfValue() == dc.fieldOfValue());
		final boolean sameDebitCreditName = (dd.fieldOfName() == dc.fieldOfName());
		
		// 未定義科目の処理方法の取得
		final String unknownItemProc = getUnknownItemProcess(strUnknownItem);
		
		// 交換代数集合として出力
		final boolean splitExalge = toBoolean(strSplitExalge);
		
		// 出力ファイルを開く
		ssac.aadl.runtime.io.CsvFileWriter fout = newCsvFileWriter(outFile);
		
		// ファイルを読み込み、指定列を変換
		boolean isSplit = false;
		java.math.BigDecimal cntrec = java.math.BigDecimal.ZERO;
		java.math.BigDecimal cnvrec = java.math.BigDecimal.ZERO;
		java.math.BigDecimal retrec = java.math.BigDecimal.ZERO;
		ssac.aadl.runtime.io.CsvFileReader csvReader = null;
		try {
			csvReader = newCsvFileReader(inFile);
			if (numSkipRecords.compareTo(java.math.BigDecimal.ZERO) > 0) {
				// skip あり
				for (java.util.List<String> rec : csvReader) {
					cntrec = cntrec.add(java.math.BigDecimal.ONE);
					if (!rec.isEmpty() && cntrec.compareTo(numSkipRecords) > 0) {
						java.math.BigDecimal writerec = writeExalgeRecord(fout, rec, cntrec, isSplit, unknownItemProc, sameDebitCreditValue, sameDebitCreditName, balanceTable, defDebit, defCredit);
						if (writerec.compareTo(java.math.BigDecimal.ZERO) > 0) {
							cnvrec = cnvrec.add(java.math.BigDecimal.ONE);
							retrec = retrec.add(writerec);
							isSplit = splitExalge;
						}
					}
				}
			}
			else {
				// skip なし
				for (java.util.List<String> rec : csvReader) {
					cntrec = cntrec.add(java.math.BigDecimal.ONE);
					if (!rec.isEmpty()) {
						java.math.BigDecimal writerec = writeExalgeRecord(fout, rec, cntrec, isSplit, unknownItemProc, sameDebitCreditValue, sameDebitCreditName, balanceTable, defDebit, defCredit);
						if (writerec.compareTo(java.math.BigDecimal.ZERO) > 0) {
							cnvrec = cnvrec.add(java.math.BigDecimal.ONE);
							retrec = retrec.add(writerec);
							isSplit = splitExalge;
						}
					}
				}
			}
		}
		finally {
			if (csvReader != null)
				closeReader(csvReader);
			closeWriter(fout);
		}
		
		// completed
		println("Converted " + toString(cnvrec) + " records, output " + toString(retrec) + " records.");
		
		// completed
		return 0;
	}

	//------------------------------------------------------------
	// AADL functions
	//------------------------------------------------------------
	
	protected java.math.BigDecimal writeExalgeRecord(ssac.aadl.runtime.io.CsvFileWriter fout, java.util.List<String> rec, java.math.BigDecimal rno,
										boolean splitExalge, String unknownItem, boolean sameDebitCreditValue, boolean sameDebitCreditName,
										Object balanceTable, Object debit, Object credit)
	{
		ExBalanceTable table = (ExBalanceTable)balanceTable;
		DefFieldForExalge defDebit  = (DefFieldForExalge)debit;
		DefFieldForExalge defCredit = (DefFieldForExalge)credit;

		java.math.BigDecimal outRecords = java.math.BigDecimal.ZERO;
		boolean writeRecord = false;

		String strHat;
		String strDebitValue   = defDebit.getFieldOfValue(rec);
		if (strDebitValue != null)
			strDebitValue = strDebitValue.trim();
		String strDebitName    = defDebit.getFieldOfName(rec);
		String strDebitUnit    = defDebit.getFieldOfUnit(rec);
		String strDebitTime    = defDebit.getFieldOfTime(rec);
		String strDebitSubject = defDebit.getFieldOfSubject(rec);
		
		String strCreditValue   = defCredit.getFieldOfValue(rec);
		if (strCreditValue != null)
			strCreditValue = strCreditValue.trim();
		String strCreditName    = defCredit.getFieldOfName(rec);
		String strCreditUnit    = defCredit.getFieldOfUnit(rec);
		String strCreditTime    = defCredit.getFieldOfTime(rec);
		String strCreditSubject = defCredit.getFieldOfSubject(rec);
		
		// check
		boolean skipDebit  = (isNullOrEmpty(strDebitName) && isNullOrEmpty(strDebitValue));
		boolean skipCredit = (isNullOrEmpty(strCreditName) && isNullOrEmpty(strCreditValue));
		// 借方列と貸方列の科目名が同列の場合
		if (sameDebitCreditName && !isNullOrEmpty(strDebitName)) {
			if (isNullOrEmpty(strDebitValue) && isNullOrEmpty(strCreditValue)) {
				// unknown balance side
				String errmsg = "警告 : 借方列と貸方列の値が指定されていません。このレコードは無視します : record=" + toString(rno);
				errorprintln(errmsg);
				return outRecords;
			}
			else if (isNullOrEmpty(strDebitValue)) {
				// no debit value
				skipDebit = true;
			}
			else if (isNullOrEmpty(strCreditValue)) {
				// no credit value
				skipCredit = true;
			}
		}
		// 借方列と貸方列の値が同列の場合
		if (sameDebitCreditValue && !isNullOrEmpty(strDebitValue)) {
			if (isNullOrEmpty(strDebitName) && isNullOrEmpty(strCreditName)) {
				// unknown balance side
				String errmsg = "借方列と貸方列の科目名が指定されていません : record=" + toString(rno);
				throw new RuntimeException(errmsg);
			}
			else if (isNullOrEmpty(strDebitName)) {
				// no debit name
				skipDebit = true;
			}
			else if (isNullOrEmpty(strCreditName)) {
				// no credit name
				skipCredit = true;
			}
		}
		
		// for Debit
		if (!skipDebit) {
			if (!isNullOrEmpty(strDebitName)) {
				// check value
				if (!isNullOrEmpty(strDebitValue)) {
					if (!isDecimal(strDebitValue)) {
						// error : not decimal value
						String errmsg = String.format("借方列の値[%s]は数値ではありません : record=%s", strDebitValue, toString(rno));
						throw new RuntimeException(errmsg);
					}
				}
				else {
					// no value
					strDebitValue = "null";
					String errmsg = "警告 : 借方列の値が指定されていません。null値として変換します : record=" + toString(rno);
					errorprintln(errmsg);
				}
				
				// judge by ExBalanceTable
				strHat = null;
				Object balanceElem = table.get(strDebitName);
				if (balanceElem != null) {
					ExBalanceElement elem = (ExBalanceElement)balanceElem;
					if (elem.isDebitSide())
						strHat = exalge2.ExBase.NO_HAT;
					else
						strHat = exalge2.ExBase.HAT;
					if (isNullOrEmpty(strDebitUnit))
						strDebitUnit = elem.getUnitKey();
					if (isNullOrEmpty(strDebitSubject))
						strDebitSubject = elem.getSubjectKey();
				}
				else if (unknownItem == ITEM_DEBIT) {
					// 借方科目(noHat)
					strHat = exalge2.ExBase.NO_HAT;
					String errmsg = "警告 : 借方列の科目[" + strDebitName + "]は貸借科目定義テーブルで未定義です。借方科目として変換します : record=" + toString(rno);
					errorprintln(errmsg);
				}
				else if (unknownItem == ITEM_CREDIT) {
					// 貸方科目(hat)
					strHat = exalge2.ExBase.HAT;
					String errmsg = "警告 : 借方列の科目[" + strDebitName + "]は貸借科目定義テーブルで未定義です。貸方科目として変換します : record=" + toString(rno);
					errorprintln(errmsg);
				}
				else {
					// skip
					String errmsg = "警告 : 借方列の科目[" + strDebitName + "]は貸借科目定義テーブルで未定義です。この科目は除外します : record=" + toString(rno);
					errorprintln(errmsg);
				}
				
				// output exalge record
				if (strHat != null) {
					if (!writeRecord && splitExalge) {
						// split exalge
						fout.newRecord();
						outRecords = outRecords.add(java.math.BigDecimal.ONE);
					}
					fout.writeField(strDebitValue);
					fout.writeField(strHat);
					fout.writeField(filterBaseKeyString(strDebitName));
					fout.writeField(filterBaseKeyString(strDebitUnit));
					fout.writeField(filterBaseKeyString(strDebitTime));
					fout.writeField(filterBaseKeyString(strDebitSubject));
					fout.newRecord();
					writeRecord = true;
					outRecords = outRecords.add(java.math.BigDecimal.ONE);
				}
			}
			else if (!isNullOrEmpty(strDebitValue)) {
				// debit value exists, but debit name not exists
				String errmsg = "借方列の値に対して科目名が指定されていません : record=" + toString(rno);
				throw new RuntimeException(errmsg);
			}
		}
		
		// for Credit
		if (!skipCredit) {
			if (!isNullOrEmpty(strCreditName)) {
				// check value
				if (!isNullOrEmpty(strCreditValue)) {
					if (!isDecimal(strCreditValue)) {
						// error : not decimal value
						String errmsg = "貸方列の値[" + strCreditValue + "]は数値ではありません : record=" + toString(rno);
						throw new RuntimeException(errmsg);
					}
				}
				else {
					// no value
					strCreditValue = "null";
					String errmsg = "警告 : 貸方列の値が指定されていません。null値として変換します : record=" + toString(rno);
					errorprintln(errmsg);
				}
				
				// judge by ExBalanceTable
				strHat = null;
				Object balanceElem = table.get(strCreditName);
				if (balanceElem != null) {
					ExBalanceElement elem = (ExBalanceElement)balanceElem;
					if (elem.isCreditSide())
						strHat = exalge2.ExBase.NO_HAT;
					else
						strHat = exalge2.ExBase.HAT;
					if (isNullOrEmpty(strCreditUnit))
						strCreditUnit = elem.getUnitKey();
					if (isNullOrEmpty(strCreditSubject))
						strCreditSubject = elem.getSubjectKey();
				}
				else if (unknownItem == ITEM_DEBIT) {
					// 借方科目(hat)
					strHat = exalge2.ExBase.HAT;
					String errmsg = "警告 : 貸方列の科目[" + strCreditName + "]は貸借科目定義テーブルで未定義です。借方科目として変換します : record=" + toString(rno);
					errorprintln(errmsg);
				}
				else if (unknownItem == ITEM_CREDIT) {
					// 貸方科目(noHat)
					strHat = exalge2.ExBase.NO_HAT;
					String errmsg = "警告 : 貸方列の科目[" + strCreditName + "]は貸借科目定義テーブルで未定義です。貸方科目として変換します : record=" + toString(rno);
					errorprintln(errmsg);
				}
				else {
					// skip
					String errmsg = "警告 : 貸方列の科目[" + strCreditName + "]は貸借科目定義テーブルで未定義です。この科目は除外します : record=" + toString(rno);
					errorprintln(errmsg);
				}
				
				// output exalge record
				if (strHat != null) {
					if (!writeRecord && splitExalge) {
						// split exalge
						fout.newRecord();
						outRecords = outRecords.add(java.math.BigDecimal.ONE);
					}
					fout.writeField(strCreditValue);
					fout.writeField(strHat);
					fout.writeField(filterBaseKeyString(strCreditName));
					fout.writeField(filterBaseKeyString(strCreditUnit));
					fout.writeField(filterBaseKeyString(strCreditTime));
					fout.writeField(filterBaseKeyString(strCreditSubject));
					fout.newRecord();
					writeRecord = true;
					outRecords = outRecords.add(java.math.BigDecimal.ONE);
				}
			}
			else if (!isNullOrEmpty(strCreditValue)) {
				// credit value exists, but credit name not exists.
				String errmsg = "貸方列の値に対して科目名が指定されていません : record=" + toString(rno);
				throw new RuntimeException(errmsg);
			}
		}
		
		return outRecords;
	}
	
	private boolean isNullOrEmpty(String str) {
		if (str != null && str.length() > 0)
			return false;
		else
			return true;
	}

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

	// 貸借科目定義テーブルの読み込み
	// AADL:function readBalanceTable(filename:String):Object
	private ExBalanceTable readBalanceTable(String filename) {
		ExBalanceTable table = new ExBalanceTable();
		ssac.aadl.runtime.io.CsvFileReader csvReader = newCsvFileReader(filename);
		//@{
		try {
			java.math.BigDecimal cntrec = java.math.BigDecimal.ZERO;
			for (java.util.List<String> rec : csvReader) {
				cntrec = cntrec.add(java.math.BigDecimal.ONE);
				readBalanceTableRecord(rec, cntrec, table);
			}
		}
		finally {
			closeReader(csvReader);
		}
		//}@;
		return table;
	}
	
	// 借方列指定の取得
	// AADL:function getDebitFieldDef(str:String):Object
	private DefFieldForExalge getDebitFieldDef(String str) {
		return DefFieldForExalge.parse(ExBalanceTable.BALANCE_DEBIT, argDebitName(), str);
	}
	
	// 貸方列指定の取得
	// AADL:function getCreditFieldDef(str:String):Object
	private DefFieldForExalge getCreditFieldDef(String str) {
		return DefFieldForExalge.parse(ExBalanceTable.BALANCE_CREDIT, argCreditName(), str);
	}
	
	// 未定義科目の扱い
	// AADL:function getUnknownItemProcess(str:String):String
	private String getUnknownItemProcess(String str) {
		String ret = ITEM_IGNORE;
		if (!isNull(str)) {
			str = str.trim().toLowerCase();
			if (!isEmpty(str)) {
				if (ITEM_IGNORE.equals(str)) {
					ret = ITEM_IGNORE;
				}
				else if (ITEM_DEBIT.equals(str)) {
					ret = ITEM_DEBIT;
				}
				else if (ITEM_CREDIT.equals(str)) {
					ret = ITEM_CREDIT;
				}
				else {
					// error
					String errmsg = "未定義科目の処理方法に指定されたパラメータが正しくありません : " + str;
					throw new RuntimeException(errmsg);
				}
			}
		}
		return ret;
	}

	// AADL:function filterBaseKeyString(str:String):String
	private String filterBaseKeyString(String str) {
		String strkey;
		
		if (isNullOrEmpty(str)) {
			strkey = "#";
		} else {
			strkey =  BaseKeyConverter.transformInvalidCharacters(str);
		}
		
		return strkey;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * 読み込まれたCSVレコードを、貸借科目定義テーブルの要素として登録する。
	 * 貸借科目定義テーブルは、交換代数標準形と任意のCSVファイルとの相互変換において参照される、科目名と貸借属性のマップである。
	 * <p>
	 * <b>《入力フォーマット》</b>
	 * <br>
	 * <code>ExBalanceTable</code> は、CSV ファイル形式の入力インタフェースを提供する。<br>
	 * CSVファイルは、カンマ区切りのテキストファイルで、次のフォーマットに従う。
	 * なお、科目名が重複している場合、より終端に近いものが優先される。
	 * <ul>
	 * <li>行の先頭から、次のようなカラム構成となる。<br>
	 * <i>科目名</i>，<i>貸借属性</i>，<i>単位キー</i>，<i>主体キー</i>
	 * <li>文字コードは、実行するプラットフォームの処理系に依存する。
	 * <li>空行は無視される。
	 * <li>行先頭文字が <code>'#'</code> で始まる行は、コメント行とみなす。
	 * <li>行先頭文字が <code>'!#'</code> の場合、<code>'#'</code> 文字とする。
	 * <li>行先頭文字が <code>'!!'</code> の場合、<code>'!'</code> 文字とする。
	 * <li>行先頭文字が <code>'#'</code> もしくは <code>'!#'</code> もしくは <code>'!!'</code> ではない場合、文字そのものとみなす。
	 * <li>貸借属性が <code>'借方'</code> もしくは <code>'debit'</code> の場合、借方属性となる。
	 * <li>貸借属性が <code>'貸方'</code> もしくは <code>'credit'</code> の場合、貸方属性となる。
	 * <li>単位キーには、この科目名で生成する交換代数基底の単位キーを指定する。省略可能。
	 * <li>主体キーには、この科目名で生成する交換代数基底の主体キーを指定する。省略可能。
	 * </ul>
	 * また、CSV ファイルの読み込みにおいて、次の場合に例外がスローされる。
	 * <ul>
	 * <li>空行ではなく、科目名が空欄の場合
	 * <li>空行ではなく、貸借属性が空欄の場合
	 * <li>貸借属性が空欄ではなく、<code>'借方'</code>, <code>'debit'</code>, <code>'貸方'</code>, <code>'credit'</code> 以外の文字列が指定された場合
	 * </ul>
	 */
	protected void readBalanceTableRecord(java.util.List<String> rec, java.math.BigDecimal rno, ExBalanceTable table) {
		String strName = (rec.size() > 0 ? rec.get(0) : "");
		String strSide = (rec.size() > 1 ? rec.get(1) : "");
		String strUnit = (rec.size() > 2 ? rec.get(2) : "");
		String strSubj = (rec.size() > 3 ? rec.get(3) : "");

		if (isNullOrEmpty(strName)) {
			if (!isNullOrEmpty(strSide) || !isNullOrEmpty(strUnit) || !isNullOrEmpty(strSubj)) {
				// error : 科目名がない
				String errmsg = "貸借科目定義テーブルに科目名がありません : record=" + toString(rno);
				throw new RuntimeException(errmsg);
			}
			return;	// skip blank record
		}

		// check comment
		char ch = strName.charAt(0);
		if (ch == '#') {
			return;	// skip comment record
		}
		else if (ch == '!' && strName.length() >= 2) {
			ch = strName.charAt(1);
			if (ch == '#' || ch == '!') {
				strName = strName.substring(1);
			}
		}

		// check balance
		strSide = (strSide==null ? "" : strSide.trim());
		if (isNullOrEmpty(strSide)) {
			// error : 貸借指定がない
			String errmsg = "貸借科目定義テーブルで貸借が指定されていません : record=" + toString(rno);
			throw new RuntimeException(errmsg);
		}
		int btype = ExBalanceTable.getBalanceTypeByName(strSide);
		if (btype != ExBalanceTable.BALANCE_DEBIT && btype != ExBalanceTable.BALANCE_CREDIT) {
			// error : 貸借指定が不正
			String errmsg = "貸借科目定義テーブルの貸借指定が不正です : record=" + toString(rno);
			throw new RuntimeException(errmsg);
		}

		// put item
		table.put(strName, btype, strUnit, strSubj);
	}
	
	static private String argDebitName() {
		return "借方列指定";
	}
	
	static private String argCreditName() {
		return "貸方列指定";
	}
	
	static private String valueFieldName() {
		return "'値'";
	}
	
	static private String nameFieldName() {
		return "'科目名'";
	}
	
	static private String unitFieldName() {
		return "'単位キー'";
	}
	
	static private String timeFieldName() {
		return "'時間キー'";
	}
	
	static private String subjectFieldName() {
		return "'主体キー'";
	}
	
	static private String getErrorNoFieldNumber(String fieldName) {
		return String.format("%sの列番号を指定してください。", fieldName);
	}
	
	static private String getErrorNoFieldValue(String fieldName) {
		return String.format("%sの列番号もしくはキー文字列を指定してください。");
	}
	
	static private String getErrorIllegalNaturalNumber(String fieldName, String tokenText) {
		return String.format("%sの列番号には 1 以上の整数を指定してください : %s", fieldName, tokenText);
	}
	
	static private String getErrorFieldNumberTooLarge(String fieldName, String tokenText) {
		return String.format("%sの列番号が大きすぎます。%d 以下の整数を指定してください : %s", fieldName, Integer.MAX_VALUE, tokenText);
	}
	
	static private String getErrorDelimiterNotFound(String tokenText) {
		return String.format("区切り文字（,）が必要です : %s" + tokenText);
	}
	
	static private String getErrorFieldNumberMultiple(String fieldName1, String fieldName2) {
		return String.format("%sの列番号と%sの列番号には異なる列番号を指定してください。", fieldName1, fieldName2);
	}
	
	static private String getErrorDebitCreditValueAndNameEquals() {
		return String.format("[借方列指定]と[借方列指定]の%s列番号、%s列番号が同じです。", valueFieldName(), nameFieldName());
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static public class DefFieldForExalge
	{
		private final int		_balanceSide;
		private final String	_argName;
		
		private int	_fieldOfValue   = -1;
		private int	_fieldOfName    = -1;
		private int	_fieldOfUnit    = -1;
		private int	_fieldOfTime    = -1;
		private int	_fieldOfSubject = -1;
		
		private String	_defUnit    = null;
		private String	_defTime    = null;
		private String	_defSubject = null;
		
		protected DefFieldForExalge(int balanceSide, String argName) {
			if (balanceSide != ExBalanceTable.BALANCE_DEBIT && balanceSide != ExBalanceTable.BALANCE_CREDIT)
				throw new IllegalArgumentException("Balance side parameter is illegal : " + balanceSide);
			_balanceSide = balanceSide;
			_argName = (argName != null && argName.length() > 0 ? argName : null);
		}
		
		public int getBalanceSide() {
			return _balanceSide;
		}
		
		public boolean isDebitSide() {
			return (_balanceSide == ExBalanceTable.BALANCE_DEBIT);
		}
		
		public boolean isCreditSide() {
			return (_balanceSide == ExBalanceTable.BALANCE_CREDIT);
		}
		
		public String getArgumentName() {
			return _argName;
		}
		
		public int fieldOfValue() {
			return _fieldOfValue;
		}
		
		public int fieldOfName() {
			return _fieldOfName;
		}
		
		public int fieldOfUnit() {
			return _fieldOfUnit;
		}
		
		public int fieldOfTime() {
			return _fieldOfTime;
		}
		
		public int fieldOfSubject() {
			return _fieldOfSubject;
		}
		
		public String defaultUnitKey() {
			return _defUnit;
		}
		
		public String defaultTimeKey() {
			return _defTime;
		}
		
		public String defaultSubjectKey() {
			return _defSubject;
		}
		
		public String getFieldOfValue(java.util.List<String> slist) {
			if (_fieldOfValue >= 0 && _fieldOfValue < slist.size()) {
				String value = slist.get(_fieldOfValue);
				if (value != null)
					value = value.trim();
				return value;
			}
			else
				return null;
		}
		
		public String getFieldOfName(java.util.List<String> slist) {
			if (_fieldOfName >= 0 && _fieldOfName < slist.size())
				return slist.get(_fieldOfName);
			else
				return null;
		}
		
		public String getFieldOfUnit(java.util.List<String> slist) {
			if (_defUnit != null)
				return _defUnit;
			else if (_fieldOfUnit >= 0 && _fieldOfUnit < slist.size())
				return slist.get(_fieldOfUnit);
			else
				return null;
		}
		
		public String getFieldOfTime(java.util.List<String> slist) {
			if (_defTime != null)
				return _defTime;
			else if (_fieldOfTime >= 0 && _fieldOfTime < slist.size())
				return slist.get(_fieldOfTime);
			else
				return null;
		}
		
		public String getFieldOfSubject(java.util.List<String> slist) {
			if (_defSubject != null)
				return _defSubject;
			else if (_fieldOfSubject >= 0 && _fieldOfSubject < slist.size())
				return slist.get(_fieldOfSubject);
			else
				return null;
		}
		
		static public DefFieldForExalge parse(final int balanceSide, final String argName, final String instr) {
			// create defaults
			DefFieldForExalge def = new DefFieldForExalge(balanceSide, argName);
			
			// parse tokens
			java.util.List<DefFieldToken> tokens = DefFieldToken.parseToken(instr);
			java.util.Iterator<DefFieldToken> it = tokens.iterator();
			Object retValue;
			
			// name field
			retValue = parseFieldValue(it, def._argName, nameFieldName(), true, false);
			def._fieldOfName = (Integer)retValue;
			
			// value field
			retValue = parseFieldValue(it, def._argName, valueFieldName(), true, false);
			def._fieldOfValue = (Integer)retValue;
			
			// unit field
			retValue = parseFieldValue(it, def._argName, unitFieldName(), false, true);
			if (retValue instanceof String)
				def._defUnit = (String)retValue;
			else if (retValue instanceof Integer)
				def._fieldOfUnit = (Integer)retValue;
			
			// time field
			retValue = parseFieldValue(it, def._argName, timeFieldName(), false, true);
			if (retValue instanceof String)
				def._defTime = (String)retValue;
			else if (retValue instanceof Integer)
				def._fieldOfTime = (Integer)retValue;
			
			// subject field
			retValue = parseFieldValue(it, def._argName, subjectFieldName(), false, true);
			if (retValue instanceof String)
				def._defSubject = (String)retValue;
			else if (retValue instanceof Integer)
				def._fieldOfSubject = (Integer)retValue;
			
			// check value and name field multiple
			if (def._fieldOfValue == def._fieldOfName) {
				throw new RuntimeException(appendArgumentName(argName, getErrorFieldNumberMultiple(nameFieldName(), valueFieldName())));
			}
			
			return def;
		}
		
		static private String appendArgumentName(String argName, String message) {
			if (argName != null && argName.length() > 0)
				return "[" + argName + "] " + message;
			else
				return message;
		}
		
		static private Object parseFieldValue(java.util.Iterator<DefFieldToken> it, String argName, String fieldName, boolean numberOnly, boolean optional) {
			// check exist
			if (!it.hasNext()) {
				if (!optional) {
					if (numberOnly)
						throw new RuntimeException(appendArgumentName(argName, getErrorNoFieldNumber(fieldName)));
					else
						throw new RuntimeException(appendArgumentName(argName, getErrorNoFieldValue(fieldName)));
				}
				return null;
			}
			DefFieldToken token = it.next();
			if (token.isDelimiterToken()) {
				if (!optional) {
					if (numberOnly)
						throw new RuntimeException(appendArgumentName(argName, getErrorNoFieldNumber(fieldName)));
					else
						throw new RuntimeException(appendArgumentName(argName, getErrorNoFieldValue(fieldName)));
				}
				return null;
			}
			
			// check value
			Object ret;
			if (token.isQuotedTextToken()) {
				if (numberOnly) {
					throw new RuntimeException(appendArgumentName(argName, getErrorIllegalNaturalNumber(fieldName, token.text)));
				}
				ret = token.normText;
			}
			else {
				java.math.BigDecimal decimal;
				try {
					decimal = new java.math.BigDecimal(token.text);
				} catch (Throwable ex) {
					throw new RuntimeException(appendArgumentName(argName, getErrorIllegalNaturalNumber(fieldName, token.text)));
				}
				try {
					decimal.toBigIntegerExact();
				} catch (Throwable ex) {
					throw new RuntimeException(appendArgumentName(argName, getErrorIllegalNaturalNumber(fieldName, token.text)));
				}
				if (decimal.compareTo(java.math.BigDecimal.ONE) < 0) {
					// error : field number less than 1.
					throw new RuntimeException(appendArgumentName(argName, getErrorIllegalNaturalNumber(fieldName, token.text)));
				}
				if (decimal.compareTo(java.math.BigDecimal.valueOf((long)Integer.MAX_VALUE)) > 0) {
					// error : field number too large
					throw new RuntimeException(appendArgumentName(argName, getErrorFieldNumberTooLarge(fieldName, token.text)));
				}
				ret = decimal.intValue() - 1;
			}
			
			// check delimiter
			if (it.hasNext()) {
				token = it.next();
				if (!token.isDelimiterToken()) {
					// error : next token must be delimiter
					throw new RuntimeException(appendArgumentName(argName, getErrorDelimiterNotFound(token.text)));
				}
			}
			
			return ret;
		}
	}
	
	static public class DefFieldToken
	{
		static public final int	TOKEN_UNKNOWN		= -1;
		static public final int	TOKEN_COMMA			= 1;
		static public final int	TOKEN_TEXT			= 10;
		static public final int TOKEN_QUOTED		= 12;
		
		public final int	posStart;
		public final int	posEnd;
		public final int	type;
		public final String	text;
		public final String	normText;
		
		protected DefFieldToken(int startPos, int endPos, int tokenType, String tokenText, String normalizedText) {
			posStart = startPos;
			posEnd   = endPos;
			type     = tokenType;
			text     = tokenText;
			normText = normalizedText;
		}
		
		public boolean isTextToken() {
			return (type == TOKEN_TEXT);
		}
		
		public boolean isQuotedTextToken() {
			return (type == TOKEN_QUOTED);
		}
		
		public boolean isDelimiterToken() {
			return (type == TOKEN_COMMA);
		}
		
		public String getAvailableText() {
			return (normText==null ? text : normText);
		}
		
		public java.math.BigDecimal toDecimal() {
			java.math.BigDecimal decimal;
			try {
				decimal = new java.math.BigDecimal(getAvailableText());
			} catch (Throwable ex) {
				decimal = null;
			}
			return decimal;
		}
		
		public boolean isDecimal() {
			return (toDecimal() != null);
		}
		
		static private DefFieldToken createToken(final String instr, int startPos, int endPos, int tokenType, final String normalizedText) {
			String text = instr.substring(startPos, endPos);
			if (normalizedText == null) {
				return new DefFieldToken(startPos, endPos, tokenType, text, text);
			} else {
				return new DefFieldToken(startPos, endPos, tokenType, text, normalizedText);
			}
		}
		
		static public java.util.List<DefFieldToken> parseToken(final String instr) {
			java.util.ArrayList<DefFieldToken> tokenlist = new java.util.ArrayList<DefFieldToken>();
			if (instr == null)
				return tokenlist;
			int strlen = instr.length();

			StringBuilder sbText = new StringBuilder();
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
				if (ch == ',') {
					// comma
					int spos = pos;
					++pos;
					tokenlist.add(createToken(instr, spos, pos, TOKEN_COMMA, null));
				}
				else if (ch == '\"') {
					// enquoted chars
					int spos = pos;
					sbText.setLength(0);
					for (++pos; pos < strlen; pos++) {
						ch = instr.charAt(pos);
						if (ch == '\"') {
							++pos;
							if (pos >= strlen) {
								// terminate input
								break;
							}
							ch = instr.charAt(pos);
							if (ch == '\"') {
								// quote escaped
								sbText.append(ch);
							}
							else {
								// terminate enquote
								break;
							}
						}
						else {
							sbText.append(ch);
						}
					}
					tokenlist.add(createToken(instr, spos, pos, TOKEN_QUOTED, sbText.toString()));
				}
				else {
					// normal text
					int spos = pos;
					for (++pos; pos < strlen; pos++) {
						ch = instr.charAt(pos);
						if (ch == ' ' || ch == ',' || ch == '\"') {
							// found delimiter
							break;
						}
					}
					tokenlist.add(createToken(instr, spos, pos, TOKEN_TEXT, null));
				}
			}
			
			return tokenlist;
		}
	}
	
	static protected class ExBalanceElement {
		private final int		_type;
		private final String	_unitKey;
		private final String	_subjectKey;
		
		public ExBalanceElement(int type) {
			this(type, null, null);
		}
		
		public ExBalanceElement(int type, String unitKey, String subjectKey) {
			_type = type;
			_unitKey = unitKey;
			_subjectKey = subjectKey;
		}
		
		public boolean isDebitSide() {
			return (_type != ExBalanceTable.BALANCE_CREDIT);
		}
		
		public boolean isCreditSide() {
			return (_type == ExBalanceTable.BALANCE_CREDIT);
		}
		
		public boolean isEmptyUnitKey() {
			return (_unitKey == null || _unitKey.length() <= 0);
		}
		
		public boolean isEmptySubjectKey() {
			return (_subjectKey == null || _subjectKey.length() <= 0);
		}
		
		public int getType() {
			return _type;
		}
		
		public String getUnitKey() {
			return _unitKey;
		}
		
		public String getSubjectKey() {
			return _subjectKey;
		}
	}
	
	static protected class ExBalanceTable
	{
		static public final int BALANCE_UNKNOWN	= 0;
		static public final int	BALANCE_DEBIT	= 1;
		static public final int BALANCE_CREDIT	= 2;
		
		static public final String[] DEBIT_NAMES = {
			"debit", "借方", "借",
		};
		
		static public final String[] CREDIT_NAMES = {
			"credit", "貸方", "貸",
		};
		
		private final java.util.HashMap<String, ExBalanceElement>	_map;
		
		public ExBalanceTable() {
			_map = new java.util.HashMap<String, ExBalanceElement>();
		}
		
		static public final int getBalanceTypeByName(String typename) {
			if (typename != null) {
				typename = typename.toLowerCase();
				
				// debit side
				for (String dname : DEBIT_NAMES) {
					if (dname.equals(typename)) {
						return BALANCE_DEBIT;
					}
				}
				
				// credit side
				for (String cname : CREDIT_NAMES) {
					if (cname.equals(typename)) {
						return BALANCE_CREDIT;
					}
				}
			}
			
			return BALANCE_UNKNOWN;
		}
		
		public boolean isEmpty() {
			return _map.isEmpty();
		}
		
		public int size() {
			return _map.size();
		}
		
		public void clear() {
			_map.clear();
		}
		
		public boolean containsName(Object name) {
			return _map.containsKey(name);
		}
		
		public ExBalanceElement get(Object name) {
			return _map.get(name);
		}
		
		public boolean isDebitSide(Object name) {
			ExBalanceElement elem = _map.get(name);
			if (elem == null)
				return false;
			else
				return elem.isDebitSide();
		}
		
		public boolean isCreditSide(Object name) {
			ExBalanceElement elem = _map.get(name);
			if (elem == null)
				return false;
			else
				return elem.isCreditSide();
		}
		
		public boolean isEmptyUnitKey(Object name) {
			ExBalanceElement elem = _map.get(name);
			if (elem == null)
				return false;
			else
				return elem.isEmptyUnitKey();
		}
		
		public boolean isEmptySubjectKey(Object name) {
			ExBalanceElement elem = _map.get(name);
			if (elem == null)
				return false;
			else
				return elem.isEmptySubjectKey();
		}
		
		public int getType(Object name) {
			ExBalanceElement elem = _map.get(name);
			if (elem == null)
				return BALANCE_UNKNOWN;
			else
				return elem.getType();
		}
		
		public String getUnitKey(Object name) {
			ExBalanceElement elem = _map.get(name);
			if (elem == null)
				return null;
			else
				return elem.getUnitKey();
		}
		
		public String getSubjectKey(Object name) {
			ExBalanceElement elem = _map.get(name);
			if (elem == null)
				return null;
			else
				return elem.getSubjectKey();
		}
		
		public ExBalanceElement put(String name, int type) {
			return put(name, type, null, null);
		}
		
		public ExBalanceElement put(String name, int type, String unitKey, String subjectKey) {
			if (name == null || name.length() <= 0)
				throw new IllegalArgumentException("'name' is null or empty.");
			if (type != BALANCE_DEBIT && type != BALANCE_CREDIT)
				throw new IllegalArgumentException("'type' is unknown type : " + type);
			
			ExBalanceElement elem = new ExBalanceElement(type, unitKey, subjectKey);
			return _map.put(name, elem);
		}
		
		public ExBalanceElement remove(Object name) {
			return _map.remove(name);
		}
	}

	static protected class BaseKeyConverter
	{
		static protected final char[] _ILLEGAL_BASEKEY_CHARS = new char[]{' ','\t','\n','\u000B','\f','\r','<','>','-',',','^','%','&','?','|','@','\'','\"'};
		static protected final String[] _TRANS_BASEKEY_STRING = new String[]{
			"__#sp#__","__#\\t#__","__#\\n#__","__#vt#__","__#\\f#__","__#\\r#__","__#lt#__","__#gt#__","__#hyp#__",
			"__#com#__","__#hat#__","__#per#__","__#amp#__","__#qm#__","__#or#__","__#at#__","__#apo#__","__#quo#__",
		};
		static protected final int TRANSPATTERN_INSIDE_MAXLEN = 3;
		static protected final int TRANSPATTERN_MINLEN = 3+3+2;
		static protected final java.util.Map<Character,String> _transmap = createTransformMap();
		static protected final java.util.Map<String,Character> _resumemap = createResumeMap();
		private BaseKeyConverter() {}

		/**
		 * 基底キーの使用禁止文字を除去する。
		 */
		static public String removeInvalidCharacters(String str) {
			if (str == null)
				return str;

			int len = str.length();
			if (len > 0) {
				int pos = 0;
				// find illegal char
				for ( ; pos < len; pos++) {
					if (_transmap.containsKey(str.charAt(pos))) {
						break;
					}
				}
				// check has illegal char
				if (pos < len) {
					char ch;
					StringBuilder sb = new StringBuilder(len);
					if (pos > 0) {
						sb.append(str.substring(0, pos));
					}
					do {
						//--- skip illegal chars
						for (++pos; pos < len; pos++) {
							ch = str.charAt(pos);
							if (!_transmap.containsKey(ch)) {
								break;
							}
						}
						//--- find next illegal chars
						if (pos < len) {
							int spos = pos;
							for (++pos; pos < len; pos++) {
								ch = str.charAt(pos);
								if (_transmap.containsKey(ch)) {
									break;
								}
							}
							sb.append(str.substring(spos, pos));
						}
					} while (pos < len);
					str = sb.toString();
				}
			}

			return str;
		}

		/**
		 * 基底キーの使用禁止文字を置き換える。
		 * @param str	変換元の文字列
		 * @return	変換後の文字列
		 */
		static public String transformInvalidCharacters(String str) {
			if (str == null)
				return str;

			int len = str.length();
			if (len > 0) {
				int pos = 0;
				// find illegal char
				for ( ; pos < len; pos++) {
					if (_transmap.containsKey(str.charAt(pos))) {
						break;
					}
				}
				// check has illegal char
				if (pos < len) {
					char ch;
					StringBuilder sb = new StringBuilder(len*2);
					if (pos > 0) {
						sb.append(str.substring(0, pos));
					}
					do {
						//--- transform illegal chars
						do {
							ch = str.charAt(pos);
							String ts = _transmap.get(ch);
							if (ts == null) {
								break;
							}
							sb.append(ts);
							++pos;
						} while (pos < len);
						//--- find next illegal chars
						if (pos < len) {
							int spos = pos;
							for (++pos; pos < len; pos++) {
								ch = str.charAt(pos);
								if (_transmap.containsKey(ch)) {
									break;
								}
							}
							sb.append(str.substring(spos, pos));
						}
					} while (pos < len);
					str = sb.toString();
				}
			}

			return str;
		}

		/**
		 * 置き換えられた使用禁止文字を、元の文字列に戻す。
		 * @param basekey	置き換え済みの文字列
		 * @return	使用禁止文字を含む文字列
		 */
		static public String resumeInvalidCharacters(String basekey) {
			if (basekey == null)
				return basekey;

			int len = basekey.length();
			if (len >= TRANSPATTERN_MINLEN) {
				String strpat = null;
				int pos = 0;
				// find pattern
				for ( ; pos < len; pos++) {
					if ('_' == basekey.charAt(pos)) {
						if ((len-pos) >= TRANSPATTERN_MINLEN) {
							strpat = getMatchedTransformedPattern(basekey, len, pos);
							if (strpat != null) {
								break;
							}
						}
					}
				}
				// check has pattern
				if (strpat != null) {
					StringBuilder sb = new StringBuilder(len);
					if (pos > 0) {
						sb.append(basekey.substring(0, pos));
					}
					sb.append(_resumemap.get(strpat));
					pos += strpat.length();
					strpat = null;
					int spos = pos;
					do {
						//--- find pattern
						for ( ; pos < len; pos++) {
							if ('_' == basekey.charAt(pos)) {
								if ((len-pos) >= TRANSPATTERN_MINLEN) {
									strpat = getMatchedTransformedPattern(basekey, len, pos);
									if (strpat != null) {
										break;
									}
								}
							}
						}
						//--- resume pattern
						if (strpat != null) {
							if (spos < pos) {
								sb.append(basekey.substring(spos, pos));
							}
							sb.append(_resumemap.get(strpat));
							pos += strpat.length();
							spos = pos;
							strpat = null;
						}
					} while (pos < len);
					if (spos < pos) {
						sb.append(basekey.substring(spos, pos));
					}
					basekey = sb.toString();
				}
			}

			return basekey;
		}

		static protected String getMatchedTransformedPattern(final String str, final int len, final int startPos) {
			int pos = startPos;
			//--- first mark : begin pattern
			if ('_' != str.charAt(pos++)) {
				return null;
			}
			//--- second mark : begin pattern
			if ('_' != str.charAt(pos++)) {
				return null;
			}
			//--- third mark : begin pattern
			if ('#' != str.charAt(pos++)) {
				return null;
			}
			//--- get inside pattern
			char ch = 0;
			int patlen = 0;
			for (; pos < len && patlen <= TRANSPATTERN_INSIDE_MAXLEN; pos++, patlen++) {
				ch = str.charAt(pos);
				if ('#' == ch) {
					break;
				}
			}
			//--- first mark : end pattern
			if (pos >= len || '#' != ch) {
				return null;
			}
			++pos;
			//--- second mark : end pattern
			if (pos >= len || '_' != str.charAt(pos++)) {
				return null;
			}
			//--- third mark : end pattern
			if (pos >= len || '_' != str.charAt(pos++)) {
				return null;
			}

			// check pattern
			String pat = str.substring(startPos, pos);
			if (_resumemap.containsKey(pat)) {
				return pat;
			} else {
				return null;
			}
		}

		static protected java.util.Map<Character,String> createTransformMap() {
			java.util.Map<Character,String> map = new java.util.HashMap<Character,String>();
			for (int i = 0; i < _ILLEGAL_BASEKEY_CHARS.length; i++) {
				map.put(_ILLEGAL_BASEKEY_CHARS[i], _TRANS_BASEKEY_STRING[i]);
			}
			return map;
		}

		static protected java.util.Map<String,Character> createResumeMap() {
			java.util.Map<String,Character> map = new java.util.HashMap<String,Character>();
			for (int i = 0; i < _ILLEGAL_BASEKEY_CHARS.length; i++) {
				map.put(_TRANS_BASEKEY_STRING[i], _ILLEGAL_BASEKEY_CHARS[i]);
			}
			return map;
		}
	}
}
