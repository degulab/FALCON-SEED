package dtalge.container.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import dtalge.DtBase;
import dtalge.Dtalge;
import dtalge.container.DtBinder;
import dtalge.container.DtSlip;
import dtalge.container.DtSlipList;
import dtalge.container.util.internal.BaseKeyTransformer;
import dtalge.container.util.internal.csv.DtDoubleEntrySimpleSlipsCsvHeader;
import dtalge.container.util.internal.csv.DtDoubleEntrySimpleSlipsCsvRecordBuilder;
import dtalge.container.util.internal.csv.filter.DtDoubleEntrySimpleSlipsCsvNoteFilterImpl;
import dtalge.io.internal.CsvReader;
import dtalge.io.internal.CsvReader.CsvField;
import dtalge.io.internal.CsvReader.CsvRecord;
import dtalge.io.internal.CsvWriter;
import dtalge.util.DtDataTypes;
import dtalge.util.Strings;
import exalge2.ExAlgeSet;
import exalge2.ExBase;
import exalge2.Exalge;

/**
 * 複式記述簡易データ伝票CSVに関するユーティリティクラス。
 * <p>
 * 貸借科目定義CSV({@link DebitCreditItemDefinitionTable})の内容に基づき、複式記述簡易データ伝票CSVとデータバインダー({@link DtBinder})との相互変換を行うインターフェースを提供する。
 * <p>
 * <b>《複式記述簡易データ伝票 CSV フォーマット》</b>
 * <br>
 * 複式記述簡易データ伝票CSVファイルは、カンマ区切りのテキストファイルで、次のフォーマットに従う。
 * <ul>
 * <li>行の構成<ul>
 * 		<li><b>空行</b>&nbsp;:&nbsp;空白を含まず、カンマのみ（もしくは値を含まずダブルクオートで囲まれたフィールド）で記述された行。</li>
 * 		<li><b>コメント行</b>&nbsp;:&nbsp;行先頭文字が <code>'#'</code> で始まる行は、コメント行とみなす。</li>
 * 		<li><b>無効行</b>&nbsp;:&nbsp;空行、もしくは、コメント行の総称であり、読み込み時には無視される。</li>
 * 		<li><b>有効行</b>&nbsp;:&nbsp;空行およびコメント行ではないもの。</li>
 * 		<li><b>項目名定義行</b>&nbsp;:&nbsp;先頭から無効行を除いた最初の有効行は、項目名定義行となる。
 * 			主にデータスリップ（{@link DtSlip}）に格納されるノート部の項目名を記述する。
 * 			データ行のノート部（第 7 列以降）でフィールドに値を記述している場合、項目定義行の同列のフィールドにも項目名を記述する必要がある。
 * 			一方、複式記述部（第 1 ～ 6 列）の内容は特に規定されない。
 * 			データバインダー（{@link DtBinder}）から複式記述簡易データ伝票CSVに変換される際は、複式記述部には次の内容が出力される。
 * 			<br>&nbsp;&nbsp;&nbsp;&nbsp;“Dr.name”，“Dr.value”，“Dr.unit”，“Cr.name”，“Cr.value”，“Cr.unit”</li>
 * 		<li><b>データ行</b>&nbsp;:&nbsp;項目名定義行の後の有効行は、データ行となる。1 行（レコード）が 1 データスリップ（{@link DtSlip}）に相当する。</li>
 * </ul></li>
 * <li>列の構成<ul>
 * 		<li><b>複式記述部（第 1 ～ 6 列）</b>&nbsp;:&nbsp;
 * 		交換代数元オブジェクトに相当する取引記述を表す。借方記述部（科目名、値、単位の順に 3 列分）と貸方記述部（科目名、値、単位の順に 3 列分）の固定長で構成される。
 * 		借方記述部の値列（第2列）と貸方記述部の値列（第5列）に限り、文字列の前後の空白は無視される。
 * 		複式記述部の内容は、貸借科目定義CSV（{@link DebitCreditItemDefinitionTable}）の内容に基づき、交換代数元と相互に変換される。
 * 		<ul>
 * 			<li><b>貸借科目定義CSVで借方と定義された科目名</b>&nbsp;:
 * 				<br>&nbsp;&nbsp;借方記述部の科目名と単位&nbsp;&hArr;&nbsp;ハットなし交換代数基底（科目名，単位）
 * 				<br>&nbsp;&nbsp;貸方記述部の科目名と単位&nbsp;&hArr;&nbsp;ハット付き交換代数基底（科目名，単位）</li>
 * 			<li><b>貸借科目定義CSVで貸方と定義された科目名</b>&nbsp;:
 * 				<br>&nbsp;&nbsp;借方記述部の科目名と単位&nbsp;&hArr;&nbsp;ハット付き交換代数基底（科目名，単位）
 * 				<br>&nbsp;&nbsp;貸方記述部の科目名と単位&nbsp;&hArr;&nbsp;ハットなし交換代数基底（科目名，単位）</li>
 * 		</ul></li>
 * 		<li><b>ノート部（第 7 列～）</b>&nbsp;:&nbsp;
 * 		データスリップ（{@link DtSlip}）ノート（“note”：<code>Dtalge</code>）に相当する付加情報。このフィールドの値は「項目名定義行」の同列のフィールド値を項目名として、ノートに格納される。</li>
 * </ul></li>
 * </ul>
 * <p>
 * また、複式記述簡易データ伝票CSV ファイルの読み込みにおいて、次の場合に例外がスローされる。
 * <ul>
 * 	<li>借方記述部および貸方記述部の内容がすべて空欄の場合</li>
 * 	<li>借方記述部の科目名または値のいずれか一方のみが空欄の場合</li>
 * 	<li>借方記述部の単位が空欄ではなく、科目名および値が空欄の場合</li>
 * 	<li>借方記述部の科目名が、貸借科目定義CSVに含まれていない場合</li>
 * 	<li>貸方記述部の科目名または値のいずれか一方のみが空欄の場合</li>
 * 	<li>貸方記述部の単位が空欄ではなく、科目名および値が空欄の場合</li>
 * 	<li>貸方記述部の科目名が、貸借科目定義CSVに含まれていない場合</li>
 * 	<li>ノート部のフィールドに何らかの文字列が記述されているが、項目定義行の同列に対応するフィールドが空欄の場合</li>
 * 	<li>借方記述部の科目名、もしくは貸方記述部の科目名に、交換代数基底キーとして許可されていない文字（以下のいずれか）が含まれている場合
 * 		<br>&nbsp;&nbsp;&nbsp;&nbsp;&lt; &gt; - , ^ &quot; % &amp; ? | @ ' " (空白)</li>
 * </ul>
 * <p>
 * 複式記述簡易データ伝票CSVファイルの出力において、次の場合に例外がスローされる。
 * <ul>
 * <li>出力対象の交換代数要素の交換代数基底にある名前キーが、貸借科目定義CSVに含まれていない場合</li>
 * </ul>
 * <p>
 * 複式記述簡易データ伝票からデータバインダーを生成するには、次のメソッドのいずれかを使用する。
 * <ul>
 * <li>{@link #importBinderFromDoubleEntrySimpleSlipsCSV(File, File)}
 * <li>{@link #importBinderFromDoubleEntrySimpleSlipsCSV(File, File, String)}
 * <li>{@link #importBinderFromDoubleEntrySimpleSlipsCSV(File, DebitCreditItemDefinitionTable)}
 * <li>{@link #importBinderFromDoubleEntrySimpleSlipsCSV(File, String, DebitCreditItemDefinitionTable)}
 * </ul>
 * <p>
 * また、複式記述簡易データ伝票CSVから指定した条件（ノート部項目抽出条件）でデータ行を抽出し、その内容のみからデータバインダーを生成するには、次のメソッドのいずれかを使用する。
 * <ul>
 * <li>{@link #importBinderFromDoubleEntrySimpleSlipsCSV(String, File, File)}
 * <li>{@link #importBinderFromDoubleEntrySimpleSlipsCSV(String, File, File, String)}
 * <li>{@link #importBinderFromDoubleEntrySimpleSlipsCSV(String, File, DebitCreditItemDefinitionTable)}
 * <li>{@link #importBinderFromDoubleEntrySimpleSlipsCSV(String, File, String, DebitCreditItemDefinitionTable)}
 * </ul>
 * <p>
 * <b>《ノート部項目抽出条件の書式》</b>
 * <b>ノート部項目抽出条件は、次のような書式の文字列として記述する</b>
 * <ul>
 * 	<li>空白、カンマのみのノート部項目抽出は、条件なしと同義</li>
 * 	<li>最小の条件の要素は、<b><i>ノート部項目抽出条件要素</i></b>であり、<b><i>項目名</i></b>、比較記号（<b>‘=’</b>、<b>‘!=’</b>のいずれか）、<b><i>パターン</i></b> の順に記述する。<ul>
 * 		<li><b><i>項目名</i></b><br>
 * 			判定する項目値の属する列の項目名（項目定義行に記述された名前）を指定する。
 * 			基本的に次の文字は項目名に含めることはできない。<br>
 * 			&nbsp;&nbsp;&nbsp;&nbsp;(空白) &quot; , ! &amp; | = &lt; &gt; \ ( )</li>
 * 		<li><b><i>パターン</i></b><br>
 * 			Javaの {@link java.util.regex.Pattern} クラスで定義される正規表現を、ダブルクオートで囲まれた Java 文字列リテラルと同じフォーマットで記述する。
 * 			エスケープシーケンスも Java と同様の書式で記述できる。</li>
 * 		<li><b><i>項目名</i></b>&nbsp;<b>=</b>&nbsp;<b><i>パターン</i></b><br>
 * 			<i>項目名</i>に対応する項目値に、<i>パターン</i>が部分一致する場合に真となる条件</li>
 * 		<li><b><i>項目名</i></b>&nbsp;<b>!=</b>&nbsp;<b><i>パターン</i></b><br>
 * 			<i>項目名</i>に対応する項目値に、<i>パターン</i>が部分一致しない場合に真となる条件</li>
 * 		</ul></li>
 * 	<li><i>ノート部項目抽出条件要素</i>は、<b>‘,’</b>（カンマ）、<b>‘&amp;&amp;’</b>、<b>‘||’</b> のいずれかで連結することで複数記述できる。
 * 		この場合、<b>‘,’</b>（カンマ）、<b>‘&amp;&amp;’</b> は AND 条件、‘||’ は OR 条件として、左から順に評価される。
 * 		また、<b>‘(’</b> と <b>‘)’</b> で囲むことでその内容が優先して評価される。
 * 		さらに、<b>‘!(’</b> と <b>‘)’</b> で囲むことで、その内容は否定条件として評価される。
 * 	</li>
 * </ul>
 * <p>データバインダーの内容を複式記述簡易データ伝票CSVに出力するには、次のメソッドのいずれかを使用する。
 * <ul>
 * <li>{@link #exportAllSlipsToDoubleEntrySimpleSlipsCSV(DtBinder, File, File)}
 * <li>{@link #exportAllSlipsToDoubleEntrySimpleSlipsCSV(DtBinder, File, File, String)}
 * <li>{@link #exportAllSlipsToDoubleEntrySimpleSlipsCSV(DtBinder, File, DebitCreditItemDefinitionTable)}
 * <li>{@link #exportAllSlipsToDoubleEntrySimpleSlipsCSV(DtBinder, File, String, DebitCreditItemDefinitionTable)}
 * </ul>
 * 
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtDoubleEntrySimpleSlipsCsvUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** CSV 読み込み時においてデータバインダーに格納されるデータスリップリストのデフォルト名 **/
	static public final String	DefaultSlipObjectNameInBinder = "dtsliplist";
	/** CSV 読み込み時においてデータスリップに格納される交換代数元オブジェクトのデフォルト名 **/
	static public final String	DefaultExalgeObjectNameInSlip = "exalge";
	
	static protected final String CsvTypeName = "DtDoubleEntrySimpleSlipCsv";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces to import DtBinder from CSV
	//------------------------------------------------------------

	/**
	 * 貸借科目定義CSVファイルの内容に基づき、複式記述簡易データ伝票CSVの内容からデータバインダーを生成する。
	 * CSVファイルの入力においては、プラットフォーム標準の文字セットが適用される。
	 * 
	 * @param dataCsvFile	複式記述簡易データ伝票CSVファイル
	 * @param defCsvFile	貸借科目定義CSVファイル
	 * @return	生成されたデータバインダー
	 * @throws FileNotFoundException	ファイルが存在しない場合
	 * @throws IOException				入出力エラーが発生した場合
	 * @throws DtDoubleEntrySimpleSlipsCsvFormatError		複式記述簡易データ伝票CSVファイルの記述が正しくない場合
	 * @throws DebitCreditItemDefinitionTableCsvFormatError	貸借科目定義CSVファイルの記述が正しくない場合
	 */
	static public DtBinder importBinderFromDoubleEntrySimpleSlipsCSV(File dataCsvFile, File defCsvFile)
		throws FileNotFoundException, IOException, DtDoubleEntrySimpleSlipsCsvFormatError, DebitCreditItemDefinitionTableCsvFormatError
	{
		// Read DebitCreditItemDefinition from CSV file
		DebitCreditItemDefinitionTable table = DebitCreditItemDefinitionTable.fromCSV(defCsvFile);
		
		// Read DoubleEntrySimpleSlips CSV file
		return importBinderFromDoubleEntrySimpleSlipsCSV(dataCsvFile, table);
	}

	/**
	 * 貸借科目定義CSVファイルの内容に基づき、複式記述簡易データ伝票CSVの内容からデータバインダーを生成する。
	 * CSVファイルの入力においては、指定された文字セットが適用される。
	 * 
	 * @param dataCsvFile	複式記述簡易データ伝票CSVファイル
	 * @param defCsvFile	貸借科目定義CSVファイル
	 * @param charsetName 	ファイル入力時に適用する文字セット名
	 * @return	生成されたデータバインダー
	 * @throws FileNotFoundException	ファイルが存在しない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws IOException				入出力エラーが発生した場合
	 * @throws DtDoubleEntrySimpleSlipsCsvFormatError		複式記述簡易データ伝票CSVファイルの記述が正しくない場合
	 * @throws DebitCreditItemDefinitionTableCsvFormatError	貸借科目定義CSVファイルの記述が正しくない場合
	 */
	static public DtBinder importBinderFromDoubleEntrySimpleSlipsCSV(File dataCsvFile, File defCsvFile, String charsetName)
		throws FileNotFoundException, UnsupportedEncodingException, IOException, DtDoubleEntrySimpleSlipsCsvFormatError, DebitCreditItemDefinitionTableCsvFormatError
	{
		// Read DebitCreditItemDefinition from CSV file
		DebitCreditItemDefinitionTable table = DebitCreditItemDefinitionTable.fromCSV(defCsvFile, charsetName);
		
		// Read DoubleEntrySimpleSlips CSV file
		return importBinderFromDoubleEntrySimpleSlipsCSV(dataCsvFile, charsetName, table);
	}

	/**
	 * 貸借科目定義テーブルの内容に基づき、複式記述簡易データ伝票CSVの内容からデータバインダーを生成する。
	 * CSVファイルの入力においては、プラットフォーム標準の文字セットが適用される。
	 * 
	 * @param dataCsvFile	複式記述簡易データ伝票CSVファイル
	 * @param table			貸借科目定義テーブル
	 * @return	生成されたデータバインダー
	 * @throws FileNotFoundException	ファイルが存在しない場合
	 * @throws IOException				入出力エラーが発生した場合
	 * @throws DtDoubleEntrySimpleSlipsCsvFormatError		複式記述簡易データ伝票CSVファイルの記述が正しくない場合
	 */
	static public DtBinder importBinderFromDoubleEntrySimpleSlipsCSV(File dataCsvFile, DebitCreditItemDefinitionTable table)
		throws FileNotFoundException, IOException, DtDoubleEntrySimpleSlipsCsvFormatError
	{
		boolean transformInvalidBaseKeyChars = false;
		
		// Read DoubleEntrySimpleSlips CSV file
		DtBinder retBinder = null;
		CsvReader csvreader = new CsvReader(dataCsvFile);
		try {
			retBinder = readFromDoubleEntrySimpleSlipsCSV(csvreader, table, transformInvalidBaseKeyChars, null);
		}
		finally {
			csvreader.close();
		}
		return retBinder;
	}

	/**
	 * 貸借科目定義テーブルの内容に基づき、複式記述簡易データ伝票CSVの内容からデータバインダーを生成する。
	 * CSVファイルの入力においては、指定された文字セットが適用される。
	 * 
	 * @param dataCsvFile	複式記述簡易データ伝票CSVファイル
	 * @param charsetName 	ファイル入出力時に適用する文字セット名
	 * @param table			貸借科目定義テーブル
	 * @return	生成されたデータバインダー
	 * @throws FileNotFoundException	ファイルが存在しない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws IOException				入出力エラーが発生した場合
	 * @throws DtDoubleEntrySimpleSlipsCsvFormatError		複式記述簡易データ伝票CSVファイルの記述が正しくない場合
	 */
	static public DtBinder importBinderFromDoubleEntrySimpleSlipsCSV(File dataCsvFile, String charsetName, DebitCreditItemDefinitionTable table)
		throws FileNotFoundException, UnsupportedEncodingException, IOException, DtDoubleEntrySimpleSlipsCsvFormatError
	{
		boolean transformInvalidBaseKeyChars = false;
		
		// Read DoubleEntrySimpleSlips CSV file
		DtBinder retBinder = null;
		CsvReader csvreader = new CsvReader(dataCsvFile, charsetName);
		try {
			retBinder = readFromDoubleEntrySimpleSlipsCSV(csvreader, table, transformInvalidBaseKeyChars, null);
		}
		finally {
			csvreader.close();
		}
		return retBinder;
	}

	//------------------------------------------------------------
	// Public interfaces to import DtBinder from CSV with CSV record conditions
	//------------------------------------------------------------

	/**
	 * 貸借科目定義CSVファイルの内容に基づき、複式記述簡易データ伝票CSVのノート部項目抽出条件に合致する内容からデータバインダーを生成する。
	 * CSVファイルの入力においては、プラットフォーム標準の文字セットが適用される。
	 * 
	 * @param csvFieldCondition	ノート部項目抽出条件
	 * @param dataCsvFile	複式記述簡易データ伝票CSVファイル
	 * @param defCsvFile	貸借科目定義CSVファイル
	 * @return	生成されたデータバインダー
	 * @throws FileNotFoundException	ファイルが存在しない場合
	 * @throws IOException				入出力エラーが発生した場合
	 * @throws DtDoubleEntrySimpleSlipsCsvFormatError		複式記述簡易データ伝票CSVファイルの記述が正しくない場合
	 * @throws DebitCreditItemDefinitionTableCsvFormatError	貸借科目定義CSVファイルの記述が正しくない場合
	 * @throws CsvFieldConditionStringParseError			ノート部項目抽出条件の記述が正しくない場合
	 */
	static public DtBinder importBinderFromDoubleEntrySimpleSlipsCSV(String csvFieldCondition, File dataCsvFile, File defCsvFile)
		throws FileNotFoundException, IOException, DtDoubleEntrySimpleSlipsCsvFormatError, DebitCreditItemDefinitionTableCsvFormatError, CsvFieldConditionStringParseError
	{
		// Read DebitCreditItemDefinition from CSV file
		DebitCreditItemDefinitionTable table = DebitCreditItemDefinitionTable.fromCSV(defCsvFile);
		
		// Read DoubleEntrySimpleSlips CSV file
		return importBinderFromDoubleEntrySimpleSlipsCSV(csvFieldCondition, dataCsvFile, table);
	}

	/**
	 * 貸借科目定義CSVファイルの内容に基づき、複式記述簡易データ伝票CSVのノート部項目抽出条件に合致する内容からデータバインダーを生成する。
	 * CSVファイルの入力においては、指定された文字セットが適用される。
	 * 
	 * @param csvFieldCondition	ノート部項目抽出条件
	 * @param dataCsvFile	複式記述簡易データ伝票CSVファイル
	 * @param defCsvFile	貸借科目定義CSVファイル
	 * @param charsetName ファイル入出力時に適用する文字セット名
	 * @return	生成されたデータバインダー
	 * @throws FileNotFoundException	ファイルが存在しない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws IOException				入出力エラーが発生した場合
	 * @throws DtDoubleEntrySimpleSlipsCsvFormatError		複式記述簡易データ伝票CSVファイルの記述が正しくない場合
	 * @throws DebitCreditItemDefinitionTableCsvFormatError	貸借科目定義CSVファイルの記述が正しくない場合
	 * @throws CsvFieldConditionStringParseError			ノート部項目抽出条件の記述が正しくない場合
	 */
	static public DtBinder importBinderFromDoubleEntrySimpleSlipsCSV(String csvFieldCondition, File dataCsvFile, File defCsvFile, String charsetName)
		throws FileNotFoundException, UnsupportedEncodingException, IOException, DtDoubleEntrySimpleSlipsCsvFormatError, DebitCreditItemDefinitionTableCsvFormatError, CsvFieldConditionStringParseError
	{
		// Read DebitCreditItemDefinition from CSV file
		DebitCreditItemDefinitionTable table = DebitCreditItemDefinitionTable.fromCSV(defCsvFile, charsetName);
		
		// Read DoubleEntrySimpleSlips CSV file
		return importBinderFromDoubleEntrySimpleSlipsCSV(csvFieldCondition, dataCsvFile, charsetName, table);
	}

	/**
	 * 貸借科目定義テーブルの内容に基づき、複式記述簡易データ伝票CSVのノート部項目抽出条件に合致する内容からデータバインダーを生成する。
	 * CSVファイルの入力においては、プラットフォーム標準の文字セットが適用される。
	 * 
	 * @param csvFieldCondition	ノート部項目抽出条件
	 * @param dataCsvFile	複式記述簡易データ伝票CSVファイル
	 * @param table			貸借科目定義テーブル
	 * @return	生成されたデータバインダー
	 * @throws FileNotFoundException	ファイルが存在しない場合
	 * @throws IOException				入出力エラーが発生した場合
	 * @throws DtDoubleEntrySimpleSlipsCsvFormatError		複式記述簡易データ伝票CSVファイルの記述が正しくない場合
	 * @throws CsvFieldConditionStringParseError			ノート部項目抽出条件の記述が正しくない場合
	 */
	static public DtBinder importBinderFromDoubleEntrySimpleSlipsCSV(String csvFieldCondition, File dataCsvFile, DebitCreditItemDefinitionTable table)
		throws FileNotFoundException, IOException, DtDoubleEntrySimpleSlipsCsvFormatError, CsvFieldConditionStringParseError
	{
		boolean transformInvalidBaseKeyChars = false;
		
		// parse CSV field condition
		DtDoubleEntrySimpleSlipsCsvNoteFilterImpl csvrecFilter = new DtDoubleEntrySimpleSlipsCsvNoteFilterImpl();
		csvrecFilter.parseConditionString(csvFieldCondition);	// throws CsvFieldConditionStringParseError 
		
		// Read DoubleEntrySimpleSlips CSV file
		DtBinder retBinder = null;
		CsvReader csvreader = new CsvReader(dataCsvFile);
		try {
			retBinder = readFromDoubleEntrySimpleSlipsCSV(csvreader, table, transformInvalidBaseKeyChars, csvrecFilter);
		}
		finally {
			csvreader.close();
		}
		return retBinder;
	}

	/**
	 * 貸借科目定義テーブルの内容に基づき、複式記述簡易データ伝票CSVのノート部項目抽出条件に合致する内容からデータバインダーを生成する。
	 * CSVファイルの入力においては、指定された文字セットが適用される。
	 * 
	 * @param csvFieldCondition	ノート部項目抽出条件
	 * @param dataCsvFile	複式記述簡易データ伝票CSVファイル
	 * @param charsetName 	ファイル入出力時に適用する文字セット名
	 * @param table			貸借科目定義テーブル
	 * @return	生成されたデータバインダー
	 * @throws FileNotFoundException	ファイルが存在しない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws IOException				入出力エラーが発生した場合
	 * @throws DtDoubleEntrySimpleSlipsCsvFormatError		複式記述簡易データ伝票CSVファイルの記述が正しくない場合
	 * @throws CsvFieldConditionStringParseError			ノート部項目抽出条件の記述が正しくない場合
	 */
	static public DtBinder importBinderFromDoubleEntrySimpleSlipsCSV(String csvFieldCondition, File dataCsvFile, String charsetName, DebitCreditItemDefinitionTable table)
		throws FileNotFoundException, UnsupportedEncodingException, IOException, DtDoubleEntrySimpleSlipsCsvFormatError, CsvFieldConditionStringParseError
	{
		boolean transformInvalidBaseKeyChars = false;
		
		// parse CSV field condition
		DtDoubleEntrySimpleSlipsCsvNoteFilterImpl csvrecFilter = new DtDoubleEntrySimpleSlipsCsvNoteFilterImpl();
		csvrecFilter.parseConditionString(csvFieldCondition);	// throws CsvFieldConditionStringParseError 
		
		// Read DoubleEntrySimpleSlips CSV file
		DtBinder retBinder = null;
		CsvReader csvreader = new CsvReader(dataCsvFile, charsetName);
		try {
			retBinder = readFromDoubleEntrySimpleSlipsCSV(csvreader, table, transformInvalidBaseKeyChars, csvrecFilter);
		}
		finally {
			csvreader.close();
		}
		return retBinder;
	}

	//------------------------------------------------------------
	// Public interfaces to export DtBinder to CSV
	//------------------------------------------------------------

	/**
	 * 貸借科目定義CSVファイルの内容に基づき、データバインダーの内容を複式記述簡易データ伝票CSVファイルとして出力する。
	 * CSVファイルの入出力においては、プラットフォーム標準の文字セットが適用される。
	 * 
	 * @param binder		データバインダー
	 * @param outCsvFile	複式記述簡易データ伝票CSVファイルの出力先
	 * @param defCsvFile	貸借科目定義CSVファイル
	 * @throws FileNotFoundException	ファイルが存在しない場合
	 * @throws IOException				入出力エラーが発生した場合
	 * @throws DebitCreditItemDefinitionTableCsvFormatError	貸借科目定義CSVファイルの記述が正しくない場合
	 */
	static public void exportAllSlipsToDoubleEntrySimpleSlipsCSV(DtBinder binder, File outCsvFile, File defCsvFile)
		throws FileNotFoundException, IOException, DebitCreditItemDefinitionTableCsvFormatError
	{
		// Read DebitCreditItemDefinition from CSV file
		DebitCreditItemDefinitionTable table = DebitCreditItemDefinitionTable.fromCSV(defCsvFile);
		
		// Write DtBinder to DoubleEntrySimpleSlips CSV file
		exportAllSlipsToDoubleEntrySimpleSlipsCSV(binder, outCsvFile, table);
	}

	/**
	 * 貸借科目定義CSVファイルの内容に基づき、データバインダーの内容を複式記述簡易データ伝票CSVファイルとして出力する。
	 * CSVファイルの入出力においては、指定された文字セットが適用される。
	 * 
	 * @param binder		データバインダー
	 * @param outCsvFile	複式記述簡易データ伝票CSVファイルの出力先
	 * @param defCsvFile	貸借科目定義CSVファイル
	 * @param charsetName 	ファイル入出力時に適用する文字セット名
	 * @throws FileNotFoundException	ファイルが存在しない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws IOException				入出力エラーが発生した場合
	 * @throws DebitCreditItemDefinitionTableCsvFormatError	貸借科目定義CSVファイルの記述が正しくない場合
	 */
	static public void exportAllSlipsToDoubleEntrySimpleSlipsCSV(DtBinder binder, File outCsvFile, File defCsvFile, String charsetName)
		throws FileNotFoundException, UnsupportedEncodingException, IOException, DebitCreditItemDefinitionTableCsvFormatError
	{
		// Read DebitCreditItemDefinition from CSV file
		DebitCreditItemDefinitionTable table = DebitCreditItemDefinitionTable.fromCSV(defCsvFile, charsetName);
		
		// Write DtBinder to DoubleEntrySimpleSlips CSV file
		exportAllSlipsToDoubleEntrySimpleSlipsCSV(binder, outCsvFile, charsetName, table);
	}

	/**
	 * 貸借科目定義テーブルの内容に基づき、データバインダーの内容を複式記述簡易データ伝票CSVファイルとして出力する。
	 * CSVファイルの出力においては、プラットフォーム標準の文字セットが適用される。
	 * 
	 * @param binder		データバインダー
	 * @param outCsvFile	複式記述簡易データ伝票CSVファイルの出力先
	 * @param table			貸借科目定義テーブル
	 * @throws FileNotFoundException	ファイルが存在しない場合
	 * @throws IOException				入出力エラーが発生した場合
	 */
	static public void exportAllSlipsToDoubleEntrySimpleSlipsCSV(DtBinder binder, File outCsvFile, DebitCreditItemDefinitionTable table)
		throws FileNotFoundException, IOException
	{
		boolean transformInvalidBaseKeyChars = false;
		
		// Write DtBinder to DoubleEntrySimpleSlips CSV file
		CsvWriter csvwriter = new CsvWriter(outCsvFile);
		try {
			writeToDoubleEntrySimpleSlipsCSV(csvwriter, table, binder, transformInvalidBaseKeyChars);
		}
		finally {
			csvwriter.close();
		}
	}

	/**
	 * 貸借科目定義テーブルの内容に基づき、データバインダーの内容を複式記述簡易データ伝票CSVファイルとして出力する。
	 * CSVファイルの出力においては、指定された文字セットが適用される。
	 * 
	 * @param binder		データバインダー
	 * @param outCsvFile	複式記述簡易データ伝票CSVファイルの出力先
	 * @param charsetName 	ファイル入出力時に適用する文字セット名
	 * @param table			貸借科目定義テーブル
	 * @throws FileNotFoundException	ファイルが存在しない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws IOException				入出力エラーが発生した場合
	 */
	static public void exportAllSlipsToDoubleEntrySimpleSlipsCSV(DtBinder binder, File outCsvFile, String charsetName, DebitCreditItemDefinitionTable table)
		throws FileNotFoundException, UnsupportedEncodingException, IOException
	{
		boolean transformInvalidBaseKeyChars = false;
		
		// Read DoubleEntrySimpleSlips CSV file
		CsvWriter csvwriter = new CsvWriter(outCsvFile, charsetName);
		try {
			writeToDoubleEntrySimpleSlipsCSV(csvwriter, table, binder, transformInvalidBaseKeyChars);
		}
		finally {
			csvwriter.close();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected Exalge readAsExalgeFromDoubleEntrySimpleSlipsCsvRecord(CsvRecord csvrec, DebitCreditItemDefinitionTable table, boolean transformInvalidBaseKeyChars)
		throws DtDoubleEntrySimpleSlipsCsvFormatError
	{
		// 複式記述部の読み込み
		//--- すべての値を読み込んでおく(空文字列は null にする)
		String strDebitName = csvrec.getValue(DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_DEBIT_NAME);
		if (Strings.isNullOrEmpty(strDebitName))  strDebitName = null;
		String strDebitValue = csvrec.getTrimmedValue(DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_DEBIT_VALUE);
		if (Strings.isNullOrEmpty(strDebitValue)) strDebitValue = null;
		String strDebitUnit = csvrec.getValue(DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_DEBIT_UNIT);
		if (Strings.isNullOrEmpty(strDebitUnit))  strDebitUnit = null;
		String strCreditName = csvrec.getValue(DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_CREDIT_NAME);
		if (Strings.isNullOrEmpty(strCreditName))  strCreditName = null;
		String strCreditValue = csvrec.getTrimmedValue(DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_CREDIT_VALUE);
		if (Strings.isNullOrEmpty(strCreditValue)) strCreditValue = null;
		String strCreditUnit = csvrec.getValue(DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_CREDIT_UNIT);
		if (Strings.isNullOrEmpty(strCreditUnit))  strCreditUnit = null;
		//--- 記述の有無を判定
		boolean existDebitEntry = (strDebitName != null || strDebitValue != null || strDebitUnit != null);
		boolean existCreditEntry = (strCreditName != null || strCreditValue != null || strCreditUnit != null);
		if (!existDebitEntry && !existCreditEntry) {
			// 借方と貸方のどちらも空欄なら、エラー
			String errmsg = "Debit entry and Credit entry are empty.";
			throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), 1);
		}

		Exalge alge = new Exalge();
		
		// 借方記述部
		if (existDebitEntry) {
			//--- Dr.name
			int colIndex = DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_DEBIT_NAME;
			if (strDebitName == null) {
				// 名前が指定されていない場合はエラー
				String errmsg = "Debit name is not specified.";
				throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), colIndex+1);
			}
			//--- Dr.name: HAT or NO_HAT
			String strDebitHat;
			if (table.isDebitSide(strDebitName)) {
				strDebitHat = ExBase.NO_HAT;	// 借方の増加(+)
			}
			else if (table.isCreditSide(strDebitName)) {
				strDebitHat = ExBase.HAT;		// 借方の減少(-)
			}
			else {
				// error
				String errmsg = "Debit name is not defined Debit or Credit side: " + strDebitName;
				throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), colIndex+1);
			}
			//--- 名前キーの使用禁止文字
			if (transformInvalidBaseKeyChars) {
				// 使用禁止文字を変換
				strDebitName = BaseKeyTransformer.transformInvalidCharacters(strDebitName);
			}
			else if (BaseKeyTransformer.containsInvalidCharacters(strDebitName)) {
				// 使用禁止文字が含まれていれば、エラー
				String errmsg = "Debit name includes invalid character as Base key: " + strDebitName;
				throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), colIndex+1);
			}
			
			//--- Dr.value
			colIndex = DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_DEBIT_VALUE;
			if (strDebitValue == null) {
				// 値が指定されていない場合はエラー
				String errmsg = "Debit value is not specified.";
				throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), colIndex+1);
			}
			BigDecimal dDebitValue;
			try {
				dDebitValue = new BigDecimal(strDebitValue);
			}
			catch (NumberFormatException ex) {
				// 数値が指定されていない場合はエラー
				String errmsg = "Debit value is not decimal: " + strDebitValue;
				throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), colIndex+1);
			}
			
			//--- Dr.unit
			if (strDebitUnit != null) {
				//--- 単位キーの使用禁止文字
				if (transformInvalidBaseKeyChars) {
					// 使用禁止文字を変換
					strDebitUnit = BaseKeyTransformer.transformInvalidCharacters(strDebitUnit);
				}
				else if (BaseKeyTransformer.containsInvalidCharacters(strDebitUnit)) {
					// 使用禁止文字が含まれていれば、エラー
					String errmsg = "Debit unit includes invalid character as Base key: " + strDebitUnit;
					throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), colIndex+1);
				}
			}
			
			// 交換代数元へ追加
			alge.add(new ExBase(strDebitName, strDebitHat, strDebitUnit), dDebitValue);
		}
		
		// 貸方記述部
		if (existCreditEntry) {
			//--- Cr.name
			int colIndex = DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_CREDIT_NAME;
			if (strCreditName == null) {
				// 名前が指定されていない場合はエラー
				String errmsg = "Credit name is not specified.";
				throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), colIndex+1);
			}
			//--- Cr.name: HAT or NO_HAT
			String strCreditHat;
			if (table.isDebitSide(strCreditName)) {
				strCreditHat = ExBase.HAT;		// 貸方の減少(-)
			}
			else if (table.isCreditSide(strCreditName)) {
				strCreditHat = ExBase.NO_HAT;	// 貸方の増加(+)
			}
			else {
				// error
				String errmsg = "Credit name is not defined Debit or Credit side: " + strCreditName;
				throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), colIndex+1);
			}
			//--- 名前キーの使用禁止文字
			if (transformInvalidBaseKeyChars) {
				// 使用禁止文字を変換
				strCreditName = BaseKeyTransformer.transformInvalidCharacters(strCreditName);
			}
			else if (BaseKeyTransformer.containsInvalidCharacters(strCreditName)) {
				// 使用禁止文字が含まれていれば、エラー
				String errmsg = "Credit name includes invalid character as Base key: " + strCreditName;
				throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), colIndex+1);
			}
			
			//--- Cr.value
			colIndex = DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_CREDIT_VALUE;
			if (strCreditValue == null) {
				// 値が指定されていない場合はエラー
				String errmsg = "Credit value is not specified.";
				throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), colIndex+1);
			}
			BigDecimal dCreditValue;
			try {
				dCreditValue = new BigDecimal(strCreditValue);
			}
			catch (NumberFormatException ex) {
				// 数値が指定されていない場合はエラー
				String errmsg = "Credit value is not decimal: " + strCreditValue;
				throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), colIndex+1);
			}
			
			//--- Cr.unit
			if (strCreditUnit != null) {
				//--- 単位キーの使用禁止文字
				if (transformInvalidBaseKeyChars) {
					// 使用禁止文字を変換
					strCreditUnit = BaseKeyTransformer.transformInvalidCharacters(strCreditUnit);
				}
				else if (BaseKeyTransformer.containsInvalidCharacters(strCreditUnit)) {
					// 使用禁止文字が含まれていれば、エラー
					String errmsg = "Credit unit includes invalid character as Base key: " + strCreditUnit;
					throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), colIndex+1);
				}
			}
			
			// 交換代数元へ追加
			alge.add(new ExBase(strCreditName, strCreditHat, strCreditUnit), dCreditValue);
		}
		
		// 完了
		return alge;
	}
	
	static protected DtSlip readAsSlipFromDoubleEntrySimpleSlipsCsvRecord(CsvRecord csvrec, DebitCreditItemDefinitionTable table, DtDoubleEntrySimpleSlipCsvNoteItemBuffer bufNoteItems, boolean transformInvalidBaseKeyChars)
		throws DtDoubleEntrySimpleSlipsCsvFormatError
	{
		// 複式記述部の読み込み
		Exalge alge = readAsExalgeFromDoubleEntrySimpleSlipsCsvRecord(csvrec, table, transformInvalidBaseKeyChars);
		
		// ノート部の読み込み
		bufNoteItems.resetCurrentNoteItemValues();
		bufNoteItems.readNoteItemValuesFromCsvRecord(csvrec);	// エラーチェックあり
		
		// データスリップの生成
		DtSlip slip = new DtSlip();
		//--- note
		if (bufNoteItems.hasCurrentNoteItemValues()) {
			Dtalge note = new Dtalge();
			String[] aryItemValues = bufNoteItems.getCurrentNoteItemValues();
			for (int i = 0; i < aryItemValues.length; i++)
			{
				String strItemValue = aryItemValues[i];
				if (Strings.isNullOrEmpty(strItemValue)) {
					continue;	// 値なし
				}
				
				// 値変換
				String dataType  = null;
				Object dataValue = null;
				//--- すべて文字列として扱う
				////--- 数値
				//try {
				//	dataValue = new BigDecimal(strItemValue);
				//	dataType = DtDataTypes.DECIMAL;
				//}
				//catch (NumberFormatException ex) {
				//	// 数値への変換不可
				//}
				////--- 真偽値
				//if (dataType == null) {
				//	if (strItemValue.equalsIgnoreCase("true")) {
				//		dataValue = Boolean.TRUE;
				//		dataType = DtDataTypes.BOOLEAN;
				//	}
				//	else if (strItemValue.equalsIgnoreCase("false")) {
				//		dataValue = Boolean.FALSE;
				//		dataType = DtDataTypes.BOOLEAN;
				//	}
				//}
				//--- 文字列
				if (dataType == null) {
					dataValue = strItemValue;
					dataType = DtDataTypes.STRING;
				}
				
				// ノートへ値を格納
				String strNameKey = bufNoteItems.csvHeader().getNoteItemNameByNoteItemIndex(i);	// エラーチェック済み
				if (transformInvalidBaseKeyChars) {
					// 基底キーの使用禁止文字を変換する
					strNameKey = BaseKeyTransformer.transformInvalidCharacters(strNameKey);
				}
				note.add(DtBase.newBase(strNameKey, dataType), dataValue);
			}
			//--- note を slip に格納
			slip.setNote(note);
		}
		//--- exalge
		slip.putExalgeObject(DefaultExalgeObjectNameInSlip, alge);
		return slip;
	}

	static protected DtBinder readFromDoubleEntrySimpleSlipsCSV(CsvReader csvreader, DebitCreditItemDefinitionTable table,
																boolean transformInvalidBaseKeyChars,  DtDoubleEntrySimpleSlipsCsvNoteFilterImpl csvrecFilter)
		throws DtDoubleEntrySimpleSlipsCsvFormatError, IOException
	{
		CsvRecord csvrec = null;
		
		// ヘッダー行の読み込み
		DtDoubleEntrySimpleSlipsCsvHeader csvheader = null;
		for (csvrec = csvreader.readRecord(); csvrec != null; csvrec = csvreader.readRecord()) {
			// skip blank
			if (!csvrec.hasValues()) {
				continue;	// 空行はスキップ
			}
			
			// check Cr.name exists
			String strDrName = csvrec.getValue(0);
			if (Strings.isNullOrEmpty(strDrName)) {
				// error
				String errmsg = "Credit name is not specified.";
				throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), 1);
			}
			
			// skip comment line
			char firstCh = strDrName.charAt(0);
			if (firstCh == '#') {
				continue;	// コメント行はスキップ
			}
			
			// CSV ヘッダーとして読み込む
			// ノート部の項目名が重複している場合は、エラー
			// 項目名が空白の場合は、エラーとしない
			csvheader = new DtDoubleEntrySimpleSlipsCsvHeader();
			csvheader.readHeaderFromCsvRecord(csvrec, transformInvalidBaseKeyChars);
			break;	// ここで抜ける
		}
		if (csvheader == null) {
			// CSV ヘッダー行が存在しない
			String errmsg = "Does not find CSV header row.";
			throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), 1);
		}
		
		// ヘッダー行のフィールド位置を、CSV フィールド条件に反映
		if (csvrecFilter != null) {
			csvrecFilter.updateConditionByCsvHeader(csvheader);
		}
		
		// データ行の読み込み
		DtSlipList sliplist = new DtSlipList();
		DtDoubleEntrySimpleSlipCsvNoteItemBuffer bufNoteItems = new DtDoubleEntrySimpleSlipCsvNoteItemBuffer(csvheader);
		//--- csvrec には次の行を最初に読み込む
		for (csvrec = csvreader.readRecord(); csvrec != null; csvrec = csvreader.readRecord()) {
			// skip blank
			if (!csvrec.hasValues()) {
				continue;	// 空行はスキップ
			}
			
			// skip by csv record filter as needed
			if (csvrecFilter != null) {
				if (!csvrecFilter.accept(csvrec)) {
					continue;	// 許可されない行はスキップ
				}
			}
			
			//// check Cr.name exists
			//String strDrName = csvrec.getValue(0);
			//if (Strings.isNullOrEmpty(strDrName)) {
			//	// error
			//	String errmsg = "[" + CsvTypeName + " Error] {Line:" + csvrec.getLineNo() + ", pos:0} Credit name is not specified.";
			//	throw new IOException(errmsg);
			//}
			
			// skip comment line
			String firstFieldValue = csvrec.getValue(0);
			char firstCh = (Strings.isNullOrEmpty(firstFieldValue) ? 0 : firstFieldValue.charAt(0));
			if (firstCh == '#') {
				continue;	// コメント行はスキップ
			}
			
			// データ行の読み込み
			DtSlip slip = readAsSlipFromDoubleEntrySimpleSlipsCsvRecord(csvrec, table, bufNoteItems, transformInvalidBaseKeyChars);
			sliplist.add(slip);
		}
		
		//--- binder へ格納
		DtBinder binder = new DtBinder();
		binder.putDtSlipListObject(DefaultSlipObjectNameInBinder, sliplist);
		return binder;
	}
	
	static protected void writeToDoubleEntrySimpleSlipsCSV(CsvWriter csvwriter, DebitCreditItemDefinitionTable table, DtBinder binder, boolean transformInvalidBaseKeyChars)
		throws IOException
	{
		// CSV ヘッダーの生成
		DtDoubleEntrySimpleSlipsCsvHeader csvheader = new DtDoubleEntrySimpleSlipsCsvHeader();
		csvheader.appendAllNoteItemNamesFromBinder(binder);
		
		// レコードバッファの生成
		DtDoubleEntrySimpleSlipsCsvRecordBuilder recbuilder = new DtDoubleEntrySimpleSlipsCsvRecordBuilder(csvheader, table);
		
		// ヘッダー行の出力
		csvheader.writeCsvHeader(csvwriter, transformInvalidBaseKeyChars);
		
		// レコードの出力
		DataObjectPosition datapos = new DataObjectPosition();
		writeAllSipsToDoubleEntrySimpleSlipsCSV(csvwriter, table, recbuilder, binder, datapos, transformInvalidBaseKeyChars);
	}
	
	static protected void writeAllSipsToDoubleEntrySimpleSlipsCSV(CsvWriter csvwriter, DebitCreditItemDefinitionTable table, DtDoubleEntrySimpleSlipsCsvRecordBuilder recbuilder,
															DtBinder binder, DataObjectPosition datapos, boolean transformInvalidBaseKeyChars)
		throws IOException
	{
		// すべての DtSlipList と DtSlip を出力対象とする
		Map<String,Object> slipmap = binder.getUnmodifiableObjects();
		for (Map.Entry<String, Object> entry : slipmap.entrySet()) {
			datapos.resetBinder();
			datapos.slipNameInBinder = entry.getKey();
			Object objSlip = entry.getValue();
			if (objSlip instanceof DtSlipList) {
				writeAllAlipsToDoubleEntrySimpleSlipsCSV(csvwriter, table, recbuilder, (DtSlipList)objSlip, datapos, transformInvalidBaseKeyChars);
			}
			else if (objSlip instanceof DtSlip) {
				writeSlipToDoubleEntrySimpleSlipsCSV(csvwriter, table, recbuilder, (DtSlip)objSlip, datapos, transformInvalidBaseKeyChars);
			}
		}
	}

	static protected void writeAllAlipsToDoubleEntrySimpleSlipsCSV(CsvWriter csvwriter, DebitCreditItemDefinitionTable table, DtDoubleEntrySimpleSlipsCsvRecordBuilder recbuilder,
															DtSlipList sliplist, DataObjectPosition datapos, boolean transformInvalidBaseKeyChars)
		throws IOException
	{
		int index = 0;
		for (DtSlip slip : sliplist) {
			datapos.resetSlipList();
			datapos.slipIndexInSlipList = index;
			writeSlipToDoubleEntrySimpleSlipsCSV(csvwriter, table, recbuilder, slip, datapos, transformInvalidBaseKeyChars);
			++index;
		}
	}
	
	static protected void writeSlipToDoubleEntrySimpleSlipsCSV(CsvWriter csvwriter, DebitCreditItemDefinitionTable table, DtDoubleEntrySimpleSlipsCsvRecordBuilder recbuilder,
														DtSlip slip, DataObjectPosition datapos, boolean transformInvalidBaseKeyChars)
		throws IOException
	{
		// note
		recbuilder.refreshSlipNoteEntries(slip);
		
		// alges
		Map<String,Object> slipmap = slip.getUnmodifiableObjects();
		for (Map.Entry<String, Object> entry : slipmap.entrySet()) {
			datapos.resetSlipOnly();
			datapos.dataNameInSlip = entry.getKey();
			Object objData = entry.getValue();
			if (objData instanceof ExAlgeSet) {
				ExAlgeSet algeset = (ExAlgeSet)objData;
				int algeindex = 0;
				for (Exalge alge :algeset) {
					datapos.resetExalgeSet();
					datapos.exalgeIndexInExAlgeSet = algeindex;
					if (!alge.isEmpty()) {
						writeExalgeToSimpleDoubleEntriedSlipsCSV(csvwriter, table, recbuilder, alge, datapos, transformInvalidBaseKeyChars);
					}
					++algeindex;
				}
			}
			else if (objData instanceof Exalge) {
				Exalge alge = (Exalge)objData;
				if (!alge.isEmpty()) {
					writeExalgeToSimpleDoubleEntriedSlipsCSV(csvwriter, table, recbuilder, alge, datapos, transformInvalidBaseKeyChars);
				}
			}
		}
	}

	static protected void writeExalgeToSimpleDoubleEntriedSlipsCSV(CsvWriter csvwriter, DebitCreditItemDefinitionTable table, DtDoubleEntrySimpleSlipsCsvRecordBuilder recbuilder,
															Exalge alge, DataObjectPosition datapos, boolean transformInvalidBaseKeyChars)
		throws IOException
	{
		// 借方・貸方のペアを構成
		recbuilder.clearUnknownSideBases();
		recbuilder.clearDoubleEntries();
		boolean pairsOnly = recbuilder.appendDoubleEntries(alge);
		
		// エラーチェック
		if (!recbuilder.isUnknownSideBaseEmpty()) {
			// 貸借が不明な名前キー
			String errmsg = "[" + CsvTypeName + " Error] " + datapos.toString() + " Unknown Debit or Credit side of name key in Exalge: " + alge.toString();
			throw new IOException(errmsg);
		}
		
		// CSV レコード出力
		for (DtDebitCreditValuePair pair : recbuilder.getDoubleEntries()) {
			// 複式記述部
			//--- 借方
			if (pair.hasDebitItem()) {
				// name
				String strKey = pair.getDebitBase().getNameKey();
				if (transformInvalidBaseKeyChars) {
					// 基底キーの使用禁止文字から変換されたパターンを元に戻す
					strKey = BaseKeyTransformer.resumeInvalidCharacters(strKey);
				}
				csvwriter.writeField(strKey);
				// value
				BigDecimal value = pair.getDebitValue();
				if (value != null) {
					csvwriter.writeField(value.stripTrailingZeros().toPlainString());
				} else {
					csvwriter.writeField("");
				}
				// unit
				strKey = pair.getDebitBase().getUnitKey();
				if (transformInvalidBaseKeyChars) {
					// 基底キーの使用禁止文字から変換されたパターンを元に戻す
					strKey = BaseKeyTransformer.resumeInvalidCharacters(strKey);
				}
				csvwriter.writeField(strKey);
			}
			else {
				csvwriter.writeField("");
				csvwriter.writeField("");
				csvwriter.writeField("");
			}
			//--- 貸方
			if (pair.hasCreditItem()) {
				// name
				String strKey = pair.getCreditBase().getNameKey();
				if (transformInvalidBaseKeyChars) {
					// 基底キーの使用禁止文字から変換されたパターンを元に戻す
					strKey = BaseKeyTransformer.resumeInvalidCharacters(strKey);
				}
				csvwriter.writeField(strKey);
				// value
				BigDecimal value = pair.getCreditValue();
				if (value != null) {
					csvwriter.writeField(value.stripTrailingZeros().toPlainString());
				} else {
					csvwriter.writeField("");
				}
				// unit
				strKey = pair.getCreditBase().getUnitKey();
				if (transformInvalidBaseKeyChars) {
					// 基底キーの使用禁止文字から変換されたパターンを元に戻す
					strKey = BaseKeyTransformer.resumeInvalidCharacters(strKey);
				}
				csvwriter.writeField(strKey);
			}
			else {
				csvwriter.writeField("");
				csvwriter.writeField("");
				csvwriter.writeField("");
			}
			
			// ノート部
			for (String noteItemValue : recbuilder.getNoteItemValues()) {
				csvwriter.writeField(noteItemValue);
			}
			
			// レコード終端
			csvwriter.newLine();
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected class DtDoubleEntrySimpleSlipCsvNoteItemBuffer
	{
		protected final DtDoubleEntrySimpleSlipsCsvHeader	_csvheader;
		
		//protected String[]	_lastNoteItemValues;
		protected String[]  _curNoteItemValues;
		//protected boolean	_existLastNoteItemValues = false;
		protected boolean	_existCurNoteItemValues = false;
		
		public DtDoubleEntrySimpleSlipCsvNoteItemBuffer(DtDoubleEntrySimpleSlipsCsvHeader csvheader)
		{
			if (csvheader == null)
				throw new NullPointerException();
			_csvheader = csvheader;
			//_lastNoteItemValues = new String[csvheader.getNumNoteItems()];
			_curNoteItemValues  = new String[csvheader.getNumNoteItems()];
		}
		
		public DtDoubleEntrySimpleSlipsCsvHeader csvHeader() {
			return _csvheader;
		}
		
		public void resetCurrentNoteItemValues() {
			Arrays.fill(_curNoteItemValues, null);
			_existCurNoteItemValues = false;
		}
		
		//public void swapAndResetCurrentNoteItemValues() {
		//	String[] last = _lastNoteItemValues;
		//	_lastNoteItemValues = _curNoteItemValues;
		//	_curNoteItemValues  = last;
		//	_existLastNoteItemValues = _existCurNoteItemValues;
		//	resetCurrentNoteItemValues();
		//}

		///**
		// * 最終有効ノート部と現在のノート部の値が同一かを判定する。
		// * @return	最終有効ノート部と現在のノート部の内容が同一とみなされるなら true、それ以外の場合は false
		// */
		//public boolean isSameStoredNoteItemValues() {
		//	return Arrays.equals(_lastNoteItemValues, _curNoteItemValues);
		//}
		
		//public boolean hasLastNoteItemValues() {
		//	return _existLastNoteItemValues;
		//}
		
		public boolean hasCurrentNoteItemValues() {
			return _existCurNoteItemValues;
		}
		
		//public String[] getLastNoteItemValues() {
		//	return _lastNoteItemValues;
		//}
		
		public String[] getCurrentNoteItemValues() {
			return _curNoteItemValues;
		}
		
		/**
		 * 
		 * @param csvrec	CSV レコード
		 * @return	一つ以上のノート項目が記述されていた場合は true、それ以外の場合は false
		 * @throws IOException	ノート部項目の値が指定されているが項目名が指定されていない場合
		 */
		public boolean readNoteItemValuesFromCsvRecord(CsvRecord csvrec)
			throws DtDoubleEntrySimpleSlipsCsvFormatError
		{
			resetCurrentNoteItemValues();
			
			int numFields = csvrec.getNumFields();
			if (numFields <= DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_NOTEITEM_BEGIN) {
				// ノート部の記述なし
				return false;
			}
			
			// ノート部の値取得
			boolean existNoteItem = false;
			for (int i = DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_NOTEITEM_BEGIN; i < numFields; i++) {
				// 値の有無を確認
				CsvField csvfield = csvrec.getField(i);
				if (csvfield == null) {
					continue;	// 値なし
				}
				String strValue = csvfield.getValue();
				if (Strings.isNullOrEmpty(strValue)) {
					continue;	// 値なし
				}
				
				// 値を格納するときの項目名を確認
				String strItemName = _csvheader.getNoteItemNameByCsvFieldIndex(i);
				if (Strings.isNullOrEmpty(strItemName)) {
					// ノート部の項目値が存在するのに項目名が指定されていない場合は、エラー
					String errmsg = "Note item name is not specified for value: " + strValue;
					throw new DtDoubleEntrySimpleSlipsCsvFormatError(errmsg, csvrec.getLineNo(), csvfield.getIndex()+1);
				}
				
				// 値の格納
				_curNoteItemValues[i - DtDoubleEntrySimpleSlipsCsvHeader.CSV_COLIDX_NOTEITEM_BEGIN] = strValue;
				existNoteItem = true;
				_existCurNoteItemValues = true;
			}
			return existNoteItem;
		}
	}
	
	static protected class DataObjectPosition
	{
		public String slipNameInBinder = null;
		public Integer slipIndexInSlipList = null;
		public String dataNameInSlip = null;
		public Integer exalgeIndexInExAlgeSet = null;
		
		public void resetBinder() {
			slipNameInBinder = null;
			slipIndexInSlipList = null;
			dataNameInSlip = null;
			exalgeIndexInExAlgeSet = null;
		}
		
		public void resetSlipList() {
			slipIndexInSlipList = null;
			dataNameInSlip = null;
			exalgeIndexInExAlgeSet = null;
		}
		
		public void resetSlipOnly() {
			dataNameInSlip = null;
			exalgeIndexInExAlgeSet = null;
		}
		
		public void resetExalgeSet() {
			exalgeIndexInExAlgeSet = null;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			
			sb.append("{");
			
			// position in DtBinder
			if (slipNameInBinder != null) {
				sb.append("name in DtBinder: \"" + slipNameInBinder + "\"");
				first = false;
			}
			
			// position in DtSlipList
			if (slipIndexInSlipList != null) {
				if (!first)
					sb.append(", ");
				sb.append("slip index in DtSlipList: " + slipIndexInSlipList.toString());
				first = false;
			}
			
			// position in DtSlip
			if (dataNameInSlip != null) {
				if (!first)
					sb.append(", ");
				sb.append("object name in DtSlip: \"" + dataNameInSlip + "\"");
				first = false;
			}
			
			// position in ExAlgeSet
			if (exalgeIndexInExAlgeSet != null) {
				if (!first)
					sb.append(", ");
				sb.append("exalge index in ExAlgeSet: " + exalgeIndexInExAlgeSet.toString());
				first = false;
			}
			
			sb.append("}");
			return sb.toString();
		}
	}
}
