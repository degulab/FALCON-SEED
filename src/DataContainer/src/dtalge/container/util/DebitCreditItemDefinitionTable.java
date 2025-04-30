package dtalge.container.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import dtalge.io.internal.CsvReader;
import dtalge.io.internal.CsvWriter;
import dtalge.util.Strings;

/**
 * 勘定科目の借方・貸方種別定義テーブル。
 * 科目名と貸借種別(借方 or 貸方)を保持する。
 * <p>
 * このオブジェクトは、CSV ファイルの入出力機能も提供する。
 * <p>
 * <b>《貸借科目定義 CSV フォーマット》</b>
 * <br>
 * 貸借科目定義CSVファイルは、カンマ区切りのテキストファイルで、次のフォーマットに従う。
 * なお、科目名が重複している場合、より終端に近いものが優先される。
 * <ul>
 * <li>行の先頭から、次のようなカラム構成となる。
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;<i>科目名</i>，<i>貸借属性</i>
 * <li>文字コードは、実行するプラットフォームの処理系に依存する。
 * <li>空行は無視される。
 * <li>行先頭文字が <code>'#'</code> で始まる行は、コメント行とみなす。
 * <li>行先頭文字が <code>'!#'</code> の場合、<code>'#'</code> 文字とする。
 * <li>行先頭文字が <code>'!!'</code> の場合、<code>'!'</code> 文字とする。
 * <li>行先頭文字が <code>'#'</code> もしくは <code>'!#'</code> もしくは <code>'!!'</code> ではない場合、文字そのものとみなす。
 * <li>貸借属性が以下のいずれかの場合、借方属性となる。なお、大文字小文字は区別しない。CSV ファイルへの出力時には、'debit' と出力される。
 * 	<ul>
 * 		<li>借方</li>
 * 		<li>借</li>
 * 		<li>debit</li>
 * 		<li>dr</li>
 * 	</ul>
 * </li>
 * <li>貸借属性が以下のいずれかの場合、貸方属性となる。なお、大文字小文字は区別しない。CSV ファイルへの出力時には、'credit' と出力される。
 * 	<ul>
 * 		<li>貸方</li>
 * 		<li>貸</li>
 * 		<li>credit</li>
 * 		<li>cr</li>
 * 	</ul>
 * </li>
 * </ul>
 * また、CSV ファイルの読み込みにおいて、次の場合に例外がスローされる。
 * <ul>
 * <li>空行ではなく、科目名が空欄の場合</li>
 * <li>空行ではなく、貸借属性が空欄の場合</li>
 * <li>貸借属性が空欄ではなく、許可されていない文字列の場合</li>
 * </ul>
 * 貸借科目定義CSVファイルの入出力は、次のメソッドにより行う。
 * <ul>
 * <li>{@link #fromCSV(File)}
 * <li>{@link #fromCSV(File, String)}
 * <li>{@link #toCSV(File)}
 * <li>{@link #toCSV(File, String)}
 * </ul>
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DebitCreditItemDefinitionTable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 貸借種別の借方を表す表記として許可する文字列の配列 **/
	static public final String[] DEBIT_NAMES = {
		"debit", "借方", "借", "dr", "dr.",
	};
		
	/** 貸借種別の貸方を表す表記として許可する文字列の配列 **/
	static public final String[] CREDIT_NAMES = {
		"credit", "貸方", "貸", "cr", "cr.",
	};
	
	static protected final int CSV_COLIDX_NAME = 0;
	static protected final int CSV_COLIDX_SIDE = 1;
	static protected final int CSV_COLIDX_UNIT = 2;
	static protected final int CSV_COLIDX_SUBJECT = 3;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 科目名をキーとするマップ **/
	protected HashMap<String, DebitCreditItem>	_nameMap;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 要素が空の新しいインスタンスを生成する。
	 */
	public DebitCreditItemDefinitionTable() {
		_nameMap = new HashMap<>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 指定された文字列が、借方または貸方のどちらを表す文字列かを判定し、その結果を返す。
	 * @param str	判定する文字列
	 * @return	借方を表す文字列なら {@link DebitCreditItem#SIDE_DEBIT}、
	 * 			借方を表す文字列なら {@link DebitCreditItem#SIDE_CREDIT}、
	 * 			それ以外の場合は {@link DebitCreditItem#SIDE_UNKNOWN}
	 */
	static public int getDebitCreditSideFromString(String str) {
		if (str != null) {
			str = str.toLowerCase();
			
			// debit side
			for (String dname : DEBIT_NAMES) {
				if (dname.equalsIgnoreCase(str)) {
					return DebitCreditItem.SIDE_DEBIT;
				}
			}
			
			// credit side
			for (String cname : CREDIT_NAMES) {
				if (cname.equalsIgnoreCase(str)) {
					return DebitCreditItem.SIDE_CREDIT;
				}
			}
		}

		return DebitCreditItem.SIDE_UNKNOWN;
	}

	/**
	 * このオブジェクトの要素が空かどうかを判定する。
	 * @return	要素が空なら <code>true</code>
	 */
	public boolean isEmpty() {
		return _nameMap.isEmpty();
	}

	/**
	 * 要素数を取得する。
	 * @return	要素数
	 */
	public int size() {
		return _nameMap.size();
	}

	/**
	 * このオブジェクトの要素を空にする。
	 */
	public void clear() {
		_nameMap.clear();
	}

	/**
	 * 指定された科目名が、このオブジェクトに含まれているかどうかを判定する。
	 * @param name	判定する科目名
	 * @return	含まれている場合は <code>true</code>
	 */
	public boolean containsName(Object name) {
		return _nameMap.containsKey(name);
	}

	/**
	 * 指定された科目名に対応する {@link DebitCreditItem} オブジェクトを取得する。
	 * @param name	科目名
	 * @return	対応する {@link DebitCreditItem} オブジェクト、存在しない場合は <code>null</code>
	 */
	public DebitCreditItem get(Object name) {
		return _nameMap.get(name);
	}

	/**
	 * 指定された科目名が借方科目として保持されているかどうかを判定する。
	 * @param name	判定する科目名
	 * @return	借方科目として保持されている場合は <code>true</code>、それ以外の場合は <code>false</code>
	 */
	public boolean isDebitSide(Object name) {
		DebitCreditItem elem = _nameMap.get(name);
		if (elem == null)
			return false;
		else
			return elem.isDebitSide();
	}
	
	/**
	 * 指定された科目名が貸方科目として保持されているかどうかを判定する。
	 * @param name	判定する科目名
	 * @return	貸方科目として保持されている場合は <code>true</code>、それ以外の場合は <code>false</code>
	 */
	public boolean isCreditSide(Object name) {
		DebitCreditItem elem = _nameMap.get(name);
		if (elem == null)
			return false;
		else
			return elem.isCreditSide();
	}

	/**
	 * 指定された科目名の貸借種別を取得する。
	 * @param name	科目名
	 * @return	科目名に対応する貸借種別({@link DebitCreditItem#getType()} が返す値)、科目名に対応する貸借種別が存在しない場合は {@link DebitCreditItem#SIDE_UNKNOWN}
	 */
	public int getType(Object name) {
		DebitCreditItem elem = _nameMap.get(name);
		if (elem == null)
			return DebitCreditItem.SIDE_UNKNOWN;
		else
			return elem.getType();
	}

	/**
	 * 指定された科目名と貸借種別を、このオブジェクトに登録する。
	 * <em>name</em> に指定された科目名がすでに登録されている場合は、指定されたパラメーターに置き替える。
	 * @param type	貸借種別({@link DebitCreditItem#SIDE_DEBIT} or {@link DebitCreditItem#SIDE_CREDIT})
	 * @param name	科目名
	 * @return	<em>name</em> に指定された科目名がすでに登録されている場合は以前の値、そうでない場合は <code>null</code>
	 * @throws IllegalArgumentException	<em>name</em> が <code>null</code> もしくは空文字列の場合、
	 * 									<em>type</em> が {@link DebitCreditItem#SIDE_DEBIT} でも {@link DebitCreditItem#SIDE_CREDIT} でもない場合
	 */
	public DebitCreditItem put(int type, String name) {
		if (name == null || name.length() <= 0)
			throw new IllegalArgumentException("'name' is null or empty.");
		if (type != DebitCreditItem.SIDE_DEBIT && type != DebitCreditItem.SIDE_CREDIT)
			throw new IllegalArgumentException("'type' is not Debit or Credit : " + type);
		
		DebitCreditItem elem = new DebitCreditItem(type, name);
		return _nameMap.put(name, elem);
	}

	/**
	 * 指定された科目名とそれに対応する値を、このオブジェクトから削除する。
	 * @param name	科目名
	 * @return	削除された値、存在しない科目名の場合は <code>null</code>
	 */
	public DebitCreditItem remove(Object name) {
		return _nameMap.remove(name);
	}
	
	/**
	 * このインスタンスのハッシュ値を返す。
	 * 
	 * @return	ハッシュ値
	 */
	@Override
	public int hashCode() {
		return _nameMap.hashCode();
	}

	/**
	 * 指定されたオブジェクトとこのインスタンスの内容が等しいかどうかを判定する。
	 * <p>
	 * このメソッドは、オブジェクト内のすべての要素も比較する。
	 * 
	 * @param obj	同値性を判定するオブジェクトの一方
	 * 
	 * @return 同値である場合に <code>true</code> を返す。
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj != null && obj.getClass().equals(this.getClass())) {
			DebitCreditItemDefinitionTable another = (DebitCreditItemDefinitionTable)obj;
			if (another._nameMap.equals(this._nameMap)) {
				return true;
			}
		}
		
		// not equals
		return false;
	}

	//------------------------------------------------------------
	// for I/O
	//------------------------------------------------------------

	/**
	 * 貸借科目定義の内容を、指定のファイルに CSV フォーマットで出力する。
	 * 
	 * @param csvFile 出力先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 */
	public void toCSV(File csvFile)
		throws IOException, FileNotFoundException
	{
		CsvWriter writer = new CsvWriter(csvFile);
		try {
			writeToCSV(writer);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * 貸借科目定義の内容を、指定された文字セットで指定のファイルに CSV フォーマットで出力する。
	 * 
	 * @param csvFile 出力先ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public void toCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, UnsupportedEncodingException
	{
		CsvWriter writer = new CsvWriter(csvFile, charsetName);
		try {
			writeToCSV(writer);
		}
		finally {
			writer.close();
		}
	}
	
	/**
	 * CSV フォーマットのファイルを読み込み、新しい貸借科目定義を生成する。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DebitCreditItemDefinitionTable</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws DebitCreditItemDefinitionTableCsvFormatError カラムのデータが正しくない場合にスローされる
	 */
	static public DebitCreditItemDefinitionTable fromCSV(File csvFile)
		throws IOException, FileNotFoundException, DebitCreditItemDefinitionTableCsvFormatError
	{
		CsvReader reader = new CsvReader(csvFile);
		DebitCreditItemDefinitionTable newTable = new DebitCreditItemDefinitionTable();
		
		try {
			newTable.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newTable;
	}
	
	/**
	 * 指定された文字セットで CSV フォーマットのファイルを読み込み、新しい貸借科目定義を生成する。
	 * 
	 * @param csvFile 読み込む CSV ファイル
	 * @param charsetName サポートする {@link java.nio.charset.Charset </code>charset<code>} の名前
	 * 
	 * @return ファイルの内容で生成された、新しい <code>DebitCreditItemDefinitionTable</code> インスタンスを返す。
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 * @throws DebitCreditItemDefinitionTableCsvFormatError カラムのデータが正しくない場合にスローされる
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	static public DebitCreditItemDefinitionTable fromCSV(File csvFile, String charsetName)
		throws IOException, FileNotFoundException, DebitCreditItemDefinitionTableCsvFormatError, UnsupportedEncodingException
	{
		CsvReader reader = new CsvReader(csvFile, charsetName);
		DebitCreditItemDefinitionTable newTable = new DebitCreditItemDefinitionTable();
		
		try {
			newTable.readFromCSV(reader);
		}
		finally {
			reader.close();
		}
		
		return newTable;
	}

	//------------------------------------------------------------
	// Internal methods for I/O
	//------------------------------------------------------------
	
	/**
	 * 指定された <code>writer</code> に、このオブジェクトの全ての要素を出力する。
	 * 
	 * @param writer	出力に使用する <code>CsvWriter</code> オブジェクト
	 * 
	 * @throws IOException	入出力エラーが発生した場合
	 */
	protected void writeToCSV(CsvWriter writer) throws IOException
	{
		for (Map.Entry<String, DebitCreditItem> entry : _nameMap.entrySet()) {
			DebitCreditItem item = entry.getValue();
			
			// name
			writer.writeField(item.getNameKey());
			
			// side
			if (item.isDebitSide()) {
				// 借方
				writer.writeField(DEBIT_NAMES[0]);
			}
			else if (item.isCreditSide()) {
				// 貸方
				writer.writeField(CREDIT_NAMES[0]);
			}
			else {
				// ここには到達してしまった場合は、空白
				writer.writeField("");
			}
			
			// new line
			writer.newLine();
		}
		
		writer.flush();
	}
	
	/**
	 * 指定された <code>reader</code> を使用して、CSVフォーマットのストリームを読み込み、
	 * このオブジェクトに格納する。
	 * 
	 * @param reader	読み込むストリームとなる <code>CsvReader</code> オブジェクト
	 * 
	 * @throws IOException			入出力エラーが発生した場合
	 * @throws DebitCreditItemDefinitionTableCsvFormatError	CSVフォーマットエラーが発生した場合
	 */
	protected void readFromCSV(CsvReader reader)
		throws IOException, DebitCreditItemDefinitionTableCsvFormatError
	{
		CsvReader.CsvRecord csvrec;
		
		while ((csvrec = reader.readRecord()) != null) {
			readRecordFromCSV(csvrec);
		}
	}

	/**
	 * 指定されたCSVレコードから、有効な内容を table に追加する。
	 * 
	 * @param csvrec	<code>CsvReader.CsvRecord</code> オブジェクト
	 * @return	有効な行の内容がこのオブジェクトに追加された場合は true、それ以外の場合は false
	 * @throws IOException	読み込みエラーが発生したとき
	 * @throws DebitCreditItemDefinitionTableCsvFormatError CSVフォーマットエラーが発生した場合
	 */
	protected boolean readRecordFromCSV(CsvReader.CsvRecord csvrec)
		throws IOException, DebitCreditItemDefinitionTableCsvFormatError
	{
		// skip blank
		if (!csvrec.hasValues()) {
			return false;
		}
		
		// check name exists
		String strName = csvrec.getValue(CSV_COLIDX_NAME);
		if (Strings.isNullOrEmpty(strName)) {
			// error
			throw new DebitCreditItemDefinitionTableCsvFormatError("Account item name is not specified.", csvrec.getLineNo(), CSV_COLIDX_NAME+1);
		}
		
		// skip comment
		char firstChar = strName.charAt(0);
		if (firstChar == '#') {
			return false;	// skip comment record
		}
		else if (firstChar == '!' && strName.length() >= 2) {
			char secondChar = strName.charAt(1);
			if (secondChar == '#' || secondChar == '!') {
				strName = strName.substring(1);
			}
		}
		
		// check side
		String strSide = csvrec.getTrimmedValue(CSV_COLIDX_SIDE);
		if (Strings.isNullOrEmpty(strSide)) {
			// error
			throw new DebitCreditItemDefinitionTableCsvFormatError("\"Credit\" or \"Debit\" side is not specified.", csvrec.getLineNo(), CSV_COLIDX_SIDE+1);
		}
		int side = getDebitCreditSideFromString(strSide);
		if (side != DebitCreditItem.SIDE_DEBIT && side != DebitCreditItem.SIDE_CREDIT) {
			// error
			String errmsg = "Unknown \"Credit\" or \"Debit\" side : " + strSide;
			throw new DebitCreditItemDefinitionTableCsvFormatError(errmsg, csvrec.getLineNo(), CSV_COLIDX_SIDE+1);
		}
		
		// put
		if (containsName(strName)) {
			String warnmsg = "[DebitCreditItemDefinition Warning] {Line:" + csvrec.getLineNo() + "} Specified name (\"" + strName + "\") already exists, it is overwritten by this entry : " + csvrec.getRecord();
			System.out.println(warnmsg);
		}
		//put(side, strName, csvrec.getValue(CSV_COLIDX_UNIT), csvrec.getValue(CSV_COLIDX_SUBJECT));
		put(side, strName);
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
