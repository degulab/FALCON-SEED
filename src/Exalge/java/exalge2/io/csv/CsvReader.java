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
 *  Copyright 2007-2014  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)CsvReader.java	0.984	2014/04/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvReader.java	0.982	2009/09/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvReader.java	0.91	2007/08/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.io.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import exalge2.io.FileUtil;
import exalge2.io.csv.CsvLocator;
import exalge2.io.csv.CsvLocatorImpl;
import exalge2.io.csv.CsvRecordReader;

/**
 * CSV ファイルの読み込み機能を提供する Reader クラス。
 * <br>
 * 基本的にカンマ(<code>,</code>)区切りのテキストファイルを対象とする。
 * <p>
 * <strong>(注)</strong> このクラスは、標準 Java {@link java.io.Reader Reader} インタフェースの
 * 拡張ではない。
 * <p>
 * CSV ファイルについては、次のように規定し、この CSV ファイル読み込みに関する機能を提供する。
 * <ul>
 * <li>文字コードは、実行するプラットフォームの処理系に依存する。
 * <li>カラムの区切りは、カンマ(<code>,</code>)を区切りとして認識する。
 * <li>カラムの先頭がダブルクオート(<code>"</code>)で始まるエンコード(エンクオート)に対応する。
 * ダブルクオートエンコードは、カラム先頭がダブルクオートで開始された場合、
 * 単一のダブルクオートが出現するまで、もしくは、行の終端までを同一カラムとみなすものである。
 * また、エンクオートされたカラムには改行文字を含めることができる。
 * ダブルクオートで開始されたカラムにおいて、連続する 2 つのダブルクオート文字は
 * 単一のダブルクオート文字とみなす。
 * <li>行の先頭からエンクオートされていない改行文字(CR、LF、CRLF)までを 1 レコードとする。
 * <li>空白も有効なカラムの文字とする。
 * <li>1レコードの終端がカンマの場合、そのカンマの次のも有効なカラムが存在するものとみなす。
 * <li>行の先頭文字がシャープ(<code>'#'</code>)の行は、コメント行とみなす。
 * </ul>
 * また、先頭行の行番号を 1、行の先頭フィールド(カラム)番号を 1 とする。
 * 
 * @version 0.984	2014/04/28
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.91
 */
public class CsvReader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 行コメント記号文字
	 */
	private boolean lineCommentEnabled = true;
	
	/**
	 * カラム区切り文字
	 */
	private char csvDelimiter = ',';
	/**
	 * {@link java.io.Reader} インスタンス
	 */
	private Reader csvReader;
	//private BufferedReader csvReader;

	/**
	 * 次に読み込みを行う文字の位置。この番号は、ファイル先頭からの文字位置を
	 * 示す、0 から始まるインデックスとなる。
	 */
	private int charIndex = 0;
	/**
	 * 次に読み込みを行う行の行番号。この行番号は、1 から始まるテキストファイルの
	 * 実行番号となる。
	 */
	private int csvLineNo = 1;
	/**
	 * 次に読み込みを行うフィールドの行先頭からの位置。この番号は、フィールドが
	 * 存在する行先頭からの相対位置を示す番号であり、行先頭を 1 とする。
	 */
	private int csvPosInLine = 1;
	/**
	 * 次に読み込みを行うCSVのレコード番号。この番号は、1 から始まる。
	 */
	private int csvRecordNo = 1;
	/**
	 * 次に読み込みを行うCSVのフィールド番号。この番号は、1 から始まる。
	 */
	private int csvFieldNo = 1;

	/**
	 * CSVレコード専用の読み込みバッファ
	 */
	private final StringBuilder _bufRecord = new StringBuilder();
	/**
	 * CSVフィールド専用の読み込みバッファ
	 */
	private final StringBuilder _bufOneField = new StringBuilder();
	/**
	 * 読み込み中のCSVレコードにおける読み込み済みCSVフィールド
	 */
	private final ArrayList<CsvField> _bufFields = new ArrayList<CsvField>();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 読み込み元のファイルをデフォルトの文字セットで読み込む、新規 <code>CsvReader</code> インスタンスを生成する。
	 * 
	 * @param csvFile 読み込むファイル
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 */
	public CsvReader(File csvFile)
		throws FileNotFoundException
	{
		FileReader fReader = new FileReader(csvFile);
		this.csvReader = new BufferedReader(fReader);
	}

	/**
	 * 読み込み元ファイルを指定された文字セットで読み込む、新規 <code>CsvReader</code> インスタンスを生成する。
	 * 
	 * @param csvFile 読み込むファイル
	 * @param encoding 文字セット名
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public CsvReader(File csvFile, String encoding)
		throws FileNotFoundException, UnsupportedEncodingException
	{
		// create Reader
		FileInputStream fiStream = new FileInputStream(csvFile);
		InputStreamReader isReader = null;
		try {
			isReader = new InputStreamReader(fiStream, encoding);
		}
		catch (UnsupportedEncodingException ex) {
			FileUtil.closeStream(fiStream);
			throw ex;
		}
		catch (RuntimeException ex) {
			FileUtil.closeStream(fiStream);
			throw ex;
		}
		// setup BufferedReader
		this.csvReader = new BufferedReader(isReader);
	}

	/**
	 * 指定された文字型入力ストリームで、新規 <code>CsvReader</code> インスタンスを生成する。
	 * 
	 * @param reader 読み込みに使用する文字型入力ストリーム
	 * 
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public CsvReader(Reader reader) {
		if (reader==null)
			throw new NullPointerException("'reader' argument cannot be null.");
		this.csvReader = reader;
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	/**
	 * 次に読み込まれる行の行番号を返す。
	 * 
	 * @return	行番号(1～)
	 * 
	 * @since 0.982
	 */
	public int getLineNo() {
		return this.csvLineNo;
	}

	/**
	 * @deprecated このメソッドは {@link #getLineNo()} に置き換えられました。
	 * 
	 * 次に読み込まれる行番号を返す。
	 * 
	 * @return 次に読み込まれる行番号
	 */
	public int getLineNumber() {
		return getLineNo();
	}

	/**
	 * 次に読み込まれるレコードのレコード番号を返す。
	 * 
	 * @return レコード番号(1～)
	 * 
	 * @since 0.982
	 */
	public int getRecordNo() {
		return this.csvRecordNo;
	}

	/**
	 * 次に読み込まれるフィールドのフィールド番号を返す。
	 * 
	 * @return フィールド番号(1～)
	 * 
	 * @since 0.982
	 */
	public int getFieldNo() {
		return this.csvFieldNo;
	}

	/**
	 * @deprecated このメソッドは {@link #getFieldNo()} に置き換えられました。
	 * 
	 * 次に読み込まれるカラム番号を返す。
	 * 
	 * @return 次に読み込まれるカラム番号
	 */
	public int getColumnNumber() {
		return getFieldNo();
	}

	/**
	 * 現在のフィールド区切り文字を返す。
	 * 
	 * @return フィールド区切り文字
	 */
	public char getDelimiterChar() {
		return this.csvDelimiter;
	}

	/**
	 * フィールド区切り文字として認識する文字を設定する。
	 * <br>
	 * <code>CsvReader</code> は、このメソッドにより指定された文字をフィールド区切り文字として、
	 * フィールド読み込みを行う。<br>
	 * なお、この区切り文字には半角ダブルクオートは指定できない。
	 * 
	 * @param delimiter 設定する区切り文字
	 * 
	 * @throws IllegalArgumentException 区切り文字に無効な文字が指定された場合
	 */
	public void setDelimiterChar(final char delimiter) {
		if (delimiter=='"')
			throw new IllegalArgumentException("Illegal delimiter : " + String.valueOf(delimiter));
		this.csvDelimiter = delimiter;
	}

	/**
	 * 行コメントが有効であれば <tt>true</tt> を返す。
	 * 
	 * @since 0.982
	 */
	public boolean isLineCommentEnabled() {
		return lineCommentEnabled;
	}

	/**
	 * 行コメントの有効／無効を設定する。
	 * <p>
	 * <code>enable</code> に <tt>true</tt> を指定した場合、このメソッド呼び出し以降のレコード読み出しでは、
	 * 行の先頭が <code>'#'</code> であれば、その行はコメントとみなされる。<br>
	 * <code>enable</code> に <tt>false</tt> を指定した場合、このメソッド呼び出し以降のレコード読み出しでは、
	 * 行の先頭の文字に関わらず、CSVフォーマットのレコードとして読み出す。
	 * <p>
	 * デフォルトでは、行コメントは有効となっている。
	 * 
	 * @param enable	行コメントを有効にする場合は <tt>true</tt>
	 * 
	 * @since 0.982
	 */
	public void setLineCommentEnable(boolean enable) {
		lineCommentEnabled = enable;
	}

	/**
	 * @deprecated このメソッドは破棄されます。
	 * 
	 * 現在の {@link CsvLocator} を返す。
	 * <br>
	 * ロケーターに保持される位置情報は、次に読み込まれる位置を示す。
	 * 
	 * @return 現在のロケーター
	 */
	protected CsvLocator getCsvLocator() {
		CsvLocatorImpl impl = new CsvLocatorImpl();
		impl.setLineNumber(getLineNo());
		impl.setColumnNumber(getFieldNo());
		return impl;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ストリームを閉じる。
	 */
	public void close() {
		FileUtil.closeStream(csvReader);
	}

	/**
	 * @deprecated <code>CsvReader</code> は、CSVレコード単位での読み出しに動作が変更されました。新しい {@link #readCsvRecord()} と {@link CsvRecord} を使用してください。
	 * 
	 * 単一レコードを読み込む。
	 * <br>
	 * このメソッドは、単一レコードからカラムを取得するための {@link CsvRecordReader} インスタンスを
	 * 返す。レコードからカラムを取得するには、{@link CsvRecordReader} を使用する。
	 * <p>
	 * 読み込むレコードが存在しない場合は <tt>null</tt> を返す。
	 * 
	 * @return 読み込むレコードに関連付けられた {@link CsvRecordReader} インスタンスを返す。
	 * 			読み込むレコードが存在しない場合は <tt>null</tt> を返す。
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public exalge2.io.csv.CsvRecordReader readRecord()
		throws IOException
	{
		// 次に読み込まれる位置を保存
		CsvLocator locator = getCsvLocator();
		
		// レコードを読み込む
		CsvRecord record = readCsvRecord();
		if (record == null) {
			// no more records
			return null;
		}
		
		// exalge2.io.csv.CsvRecordReader インスタンス生成
		return new exalge2.io.csv.CsvRecordReader(record.getRecord(), locator, this.csvDelimiter);
	}

	/**
	 * ストリームの現在位置から 1 レコード読み込む。
	 * <p>
	 * このメソッドは、ストリームの現在位置から 1 レコード分の情報を読み込み、
	 * その情報を保持する {@link CsvRecord} オブジェクトを生成する。
	 * レコードが空行の場合、レコード情報は生成されるが、そのフィールド数は 0 となる。
	 * 
	 * @return レコード情報を保持する {@link CsvRecord} オブジェクトを返す。
	 * 			読み込むレコードが存在しない場合は null を返す。
	 * 
	 * @throws IOException	読み込みエラーが発生した場合
	 * 
	 * @since 0.982
	 */
	public CsvRecord readCsvRecord() throws IOException {
		// 初期化
		_bufRecord.setLength(0);
		_bufFields.clear();
		
		// 現在の位置情報を保存
		int curCharIndex = charIndex;
		int curPosInLine = csvPosInLine;
		int curLineNo = csvLineNo;
		int curRecordNo = csvRecordNo;
		
		// 1文字読み込む
		int iCharRead = readChar();
		if (iCharRead < 0) {
			// ストリーム終端
			return null;
		}
		
		// コメント行判定
		if (lineCommentEnabled && iCharRead == '#') {
			// 行終端まで読み込む
			_bufRecord.append((char)iCharRead);
			int iRead = readChar();
			while (iRead >= 0) {
				if (iRead == '\n') {
					// コメント行終端文字
					csvLineNo++;
					csvPosInLine = 1;
					break;
				}
				else if (iRead == '\r') {
					// レコード終端文字
					csvLineNo++;
					csvPosInLine = 1;
					// 次の文字を先読み
					csvReader.mark(2);
					int nextRead = csvReader.read();
					if (nextRead != '\n') {
						csvReader.reset();
					} else {
						charIndex++;
					}
					break;
				}
				else {
					// バッファリング
					_bufRecord.append((char)iRead);
				}
				//--- 次の文字を読み込む
				iRead = readChar();
			}
			return new CsvRecord(curLineNo, curRecordNo, _bufRecord.toString(), new CsvField[0]);
		}
		
		// 1レコード分のフィールドを読み込む
		boolean existRecord = false;
		_bufOneField.setLength(0);
		int iRead = readField1(iCharRead, _bufRecord, _bufOneField, csvDelimiter);
		for (;;) {
			if (iRead == csvDelimiter) {
				// フィールド終端
				csvFieldNo++;
				_bufFields.add(new CsvField(curCharIndex, curLineNo, curPosInLine, _bufOneField.toString()));
			}
			else if (iRead == '\r' || iRead == '\n') {
				// レコード終端
				csvRecordNo++;
				csvFieldNo = 1;
				//--- フィールド保存
				if (_bufOneField.length() > 0 || !_bufFields.isEmpty()) {
					// 最終フィールドの保存
					_bufFields.add(new CsvField(curCharIndex, curLineNo, curPosInLine, _bufOneField.toString()));
				}
				//--- レコード保存、もしくは空行として保存
				existRecord = true;
				break;
			}
			else if (iRead < 0) {
				// ストリーム終端
				if (_bufOneField.length() <= 0 && _bufFields.isEmpty()) {
					// 先頭フィールドが存在しないので、レコードなし
					existRecord = false;
				}
				else {
					// 最終フィールドの保存
					_bufFields.add(new CsvField(curCharIndex, curLineNo, curPosInLine, _bufOneField.toString()));
					existRecord = true;
				}
				break;
			}
			else {
				// Not reached!
				throw new AssertionError();
			}
			// 次のフィールドを読み込む
			curCharIndex = charIndex;
			curPosInLine = csvPosInLine;
			curLineNo    = csvLineNo;
			iRead = readField(_bufRecord, _bufOneField, csvDelimiter);
		}
		
		// レコード情報の生成
		CsvRecord recEntry = null;
		if (existRecord) {
			recEntry = new CsvRecord(curLineNo, curRecordNo, _bufRecord.toString(),
									  _bufFields.toArray(new CsvField[_bufFields.size()]));
		}
		
		// 完了
		return recEntry;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * ストリームの現在位置から、1文字分の情報を読み込む。
	 * 
	 * @return ストリームから読み込まれた1文字
	 * 
	 * @throws IOException 読み込みエラーが発生した場合
	 * 
	 * @since 0.982
	 */
	private int readChar() throws IOException
	{
		int iRead = csvReader.read();
		if (iRead >= 0) {
			charIndex++;
			csvPosInLine++;
		}
		return iRead;
	}

	/**
	 * ストリームの現在位置から、1 フィールド分の情報を読み込む。
	 * 
	 * @param bufRecord		1 レコード分の読み込み文字列を格納するバッファを指定する。
	 * @param bufField		1 フィールド分の読み込み文字列を格納するバッファを指定する。
	 * @param delimiter		区切り文字
	 * 
	 * @return	最後にストリームから読み込まれた 1 文字を返す。ストリーム終端の場合は負の値を返す。
	 * 
	 * @throws IOException	読み込みエラーが発生した場合
	 * 
	 * @since 0.982
	 */
	private int readField(final StringBuilder bufRecord, final StringBuilder bufField, final char delimiter)
		throws IOException
	{
		// フィールドバッファをクリア
		bufField.setLength(0);

		// 1文字読み込む
		int iRead = readChar();
		if (iRead < 0) {
			// ストリーム終端
			return iRead;
		}
		
		// フィールド読み込み
		return readField1(iRead, bufRecord, bufField, delimiter);
	}

	/**
	 * ストリームの現在位置から、1 フィールド分の情報を読み込む。<br>
	 * このメソッドは、フィールド先頭の一文字をパラメータとして渡す。
	 * また、フィールドバッファはクリア済みであることを前提とする。
	 * 
	 * @param iFirstChar	読み込み済みの1文字を指定する。この値は正の値であることを前提とする。
	 * @param bufRecord		1 レコード分の読み込み文字列を格納するバッファを指定する。
	 * @param bufField		1 フィールド分の読み込み文字列を格納するバッファを指定する。
	 * @param delimiter		区切り文字
	 * 
	 * @return	最後にストリームから読み込まれた 1 文字を返す。ストリーム終端の場合は負の値を返す。
	 * 
	 * @throws IOException	読み込みエラーが発生した場合
	 * 
	 * @since 0.982
	 */
	private int readField1(int iFirstChar, final StringBuilder bufRecord, final StringBuilder bufField, final char delimiter)
		throws IOException
	{
		// 最初の1文字を設定
		int iRead = iFirstChar;

		// エンクオート処理
		if (iRead == '"') {
			// エンクオート開始文字は、レコードバッファにのみ保存
			bufRecord.append((char)iRead);
			// エンクオート終端までバッファリング
			iRead = readChar();
			while (iRead >= 0) {
				if (iRead == '"') {
					// エンクオート終端
					//--- エンクオート終端文字は、レコードバッファにのみ保存
					bufRecord.append((char)iRead);
					//--- 次の文字をチェックする
					iRead = readChar();
					if (iRead < 0) {
						// ストリーム終端
						return iRead;
					}
					else if (iRead == '"') {
						// ダブルクオートのエスケープ
						bufRecord.append((char)iRead);
						bufField.append((char)iRead);
					}
					else {
						// エンクオート終了
						break;
					}
				}
				else {
					// エンクオート中
					//--- バッファリング
					bufRecord.append((char)iRead);
					bufField.append((char)iRead);
					//--- 行番号更新
					if (iRead == '\n') {
						// LF のみは改行とみなす
						csvLineNo++;
						csvPosInLine = 1;
					}
					else if (iRead == '\r') {
						// 改行文字として認識可能かチェック
						iRead = readChar();
						if (iRead == '\n') {
							// CRLF 改行
							bufRecord.append((char)iRead);
							bufField.append((char)iRead);
							csvLineNo++;
							csvPosInLine = 1;
						}
						else {
							// CR 改行
							csvLineNo++;
							csvPosInLine = 1;
							//--- 読み込み済み文字は、正規のルートで検証
							continue;
						}
					}
				}
				//--- 次の文字を読み込む
				iRead = readChar();
			}
		}

		// 1フィールド分読み込む
		while (iRead >= 0) {
			if (iRead == delimiter) {
				// フィールド終端文字
				//--- フィールド終端文字は、レコードバッファにのみ保存
				bufRecord.append((char)iRead);
				break;
			}
			else if (iRead == '\n') {
				// レコード終端文字
				csvLineNo++;
				csvPosInLine = 1;
				break;
			}
			else if (iRead == '\r') {
				// レコード終端文字
				csvLineNo++;
				csvPosInLine = 1;
				// 次の文字を先読み
				csvReader.mark(2);
				int nextRead = csvReader.read();
				if (nextRead != '\n') {
					csvReader.reset();
				} else {
					charIndex++;
				}
				break;
			}
			else {
				// バッファリング
				bufRecord.append((char)iRead);
				bufField.append((char)iRead);
			}
			//--- 次の文字を読み込む
			iRead = readChar();
		}

		// フィールド終了
		return iRead;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * CSV の 1フィールド文の情報を保持するクラス。
	 * <p>
	 * このクラスは、次の情報を格納する。
	 * <ul>
	 * <li>このフィールド先頭のファイル内インデックス
	 * <li>このフィールド先頭の位置を示す行番号
	 * <li>このフィールド先頭の行先頭からの位置(1から始まる文字位置)
	 * <li>このフィールドのデータ
	 * </ul>
	 * 
	 * @version 0.982	2009/09/13
	 * 
	 * @since 0.982
	 */
	static public class CsvField
	{
		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		/**
		 * このフィールド先頭の位置(ファイル内のインデックス：0～)
		 */
		private final int index;
		/**
		 * このフィールド先頭の行番号(1～)
		 */
		private final int lineNo;
		/**
		 * このフィールド先頭の行先頭からの位置(1～)
		 */
		private final int posInLine;
		/**
		 * フィールド・データ
		 */
		private final String value;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------

		/**
		 * <code>CsvField</code> の新しいインスタンスを生成する。
		 * 
		 * @param index		フィールド先頭のインデックス
		 * @param lineNo	フィールド先頭の行番号
		 * @param posInLine	フィールド先頭の行先頭からの位置
		 * @param value		フィールド・データ
		 */
		public CsvField(int index, int lineNo, int posInLine, String value) {
			this.index     = index;
			this.lineNo    = lineNo;
			this.posInLine = posInLine;
			this.value     = value;
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		/**
		 * このフィールドが有効なデータを格納しているかを評価する。
		 * 
		 * @return 有効データを格納している場合は <tt>true</tt>
		 */
		public boolean hasValue() {
			return (value != null && value.length() > 0);
		}

		/**
		 * このフィールド先頭の位置を取得する。この位置は、ファイル内の
		 * 文字インデックス(0～)となる。
		 * 
		 * @return このフィールド先頭の位置
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * このフィールド先頭の行番号を取得する。この番号は、ファイル先頭行を
		 * 1 とする行番号となる。
		 * 
		 * @return	このフィールド先頭の行番号
		 */
		public int getLineNo() {
			return lineNo;
		}

		/**
		 * このフィールド先頭の行先頭からの位置を取得する。この位置は、
		 * このフィールドが存在する行の先頭からの文字位置(1～)となる。
		 * 
		 * @return	このフィールド先頭の行先頭からの位置
		 */
		public int getPositionInLine() {
			return posInLine;
		}

		/**
		 * フィールド・データを取得する。
		 * 
		 * @return	フィールド・データ
		 */
		public String getValue() {
			return value;
		}

		/**
		 * フィールド・データから前後空白を除去した値を返す。
		 * 前後空白除去は、{@link String#trim()} の結果となる。
		 * 
		 * @return	前後の空白が除去されたフィールド・データ
		 * 
		 * @see java.lang.String#trim()
		 */
		public String getTrimmedValue() {
			return (value==null ? null : value.trim());
		}

		/**
		 * このインスタンスの文字列表現を返す。
		 * フィールドの値が <tt>null</tt> の場合は、空文字列を返す。
		 * 
		 * @return 文字列表現
		 */
		public String toString() {
			return (value != null ? value : "");
		}
	}

	/**
	 * CSV の 1レコード分の情報を保持するクラス。
	 * <p>
	 * このクラスは、次の情報を格納する。
	 * <ul>
	 * <li>このレコードの先頭の位置を示す行番号
	 * <li>このレコードのレコード番号
	 * <li>ストリームからこのレコードとして読み込まれた 1レコード分の文字列
	 * <li>このレコードの全てのフィールドデータ
	 * </ul>
	 * 
	 * @version 0.982	2009/09/13
	 * 
	 * @since 0.982
	 */
	static public class CsvRecord
	{
		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		/**
		 * このレコードの開始行番号(1～)
		 */
		private final int lineNo;
		/**
		 * このレコードのレコード番号(1～)
		 */
		private final int recordNo;
		/**
		 * CSVレコードデータ(エンクオートされているフィールドはオリジナルのまま)
		 */
		private final String strRecord;
		/**
		 * デクオート済みのCSVフィールドデータ
		 */
		private final CsvField[] aryFields;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		/**
		 * <code>CsvRecord</code> の新しいインスタンスを生成するコンストラクタ。
		 * 
		 * @param lineNo	このレコードの行番号
		 * @param recordNo	このレコードのレコード番号
		 * @param record	このレコードの文字列
		 * @param fields	このレコードのフィールドデータ
		 */
		public CsvRecord(int lineNo, int recordNo, String record, CsvField[] fields) {
			this.lineNo = lineNo;
			this.recordNo = recordNo;
			this.strRecord = record;
			this.aryFields = fields;
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		/**
		 * このレコードの先頭が位置する行番号を返す。
		 * 
		 * @return 行番号
		 */
		public int getLineNo() {
			return lineNo;
		}

		/**
		 * このレコードのレコード番号を返す。
		 * 
		 * @return レコード番号
		 */
		public int getRecordNo() {
			return recordNo;
		}

		/**
		 * このレコードに含まれるフィールド数を返す。
		 * このレコードが空行であった場合、0 を返す。
		 * 
		 * @return フィールド数
		 */
		public int getNumFields() {
			return aryFields.length;
		}

		/**
		 * このレコードがフィールドを持っている場合に <tt>true</tt> を返す。
		 * 
		 * @return レコードがフィールドを持っていれば <tt>true</tt>
		 */
		public boolean hasFields() {
			return (aryFields.length > 0);
		}

		/**
		 * このレコードが値を持つフィールドを保持している場合に <tt>true</tt> を返す。
		 * 値を保持していないフィールドとは、空行の場合や、レコード(行)にフィールド区切り文字しか記述されていない場合を示す。
		 * @return	レコードが値を持つフィールドを保持していれば <tt>true</tt> を返す。
		 * 			フィールドが存在しない場合や、値を保持しているフィールドが一つもない場合は <tt>false</tt> を返す。
		 */
		public boolean hasValues() {
			if (aryFields.length > 0) {
				for (CsvField field : aryFields) {
					if (field.hasValue()) {
						return true;
					}
				}
			}
			return false;	// no values
		}
		
		/**
		 * このレコードが、指定されたインデックス以降に値を持つフィールドを保持している場合に <tt>true</tt> を返す。
		 * 値を保持していないフィールドとは、空行の場合や、レコード(行)にフィールド区切り文字しか記述されていない場合を示す。
		 * <p>
		 * <code>fromIndex</code> に制約はない。負の値の場合は 0 の場合と同じ結果となる。このレコードが保持している
		 * フィールド数より大きい場合は、常に <tt>false</tt> を返す。
		 * 
		 * @return	レコードが指定されたインデックス以降に値を持つフィールドを保持していれば <tt>true</tt> を返す。
		 * 			フィールドが存在しない場合や、値を保持しているフィールドが一つもない場合は <tt>false</tt> を返す。
		 */
		public boolean hasValues(int fromIndex) {
			for (int fi = Math.max(0, fromIndex) ; fi < aryFields.length; fi++) {
				if (aryFields[fi].hasValue()) {
					return true;
				}
			}
			return false;	// no values
		}

		/**
		 * このレコードを表す文字列を返す。この文字列は、ストリームから読み込まれた
		 * このレコードの生データとなる。ただし、レコード終端を示す改行文字は含まれない。
		 * 
		 * @return レコード文字列
		 */
		public String getRecord() {
			return strRecord;
		}
		
		/**
		 * 指定の位置にあるフィールドデータを取得する。
		 * このメソッドでは、位置の指定にフィールド番号ではなく、0 から始まる
		 * インデックスを使用する。インデックスは、レコード先頭のフィールドを 0 とする。
		 * <br>
		 * このメソッドは、フィールドが存在しない位置のインデックスが指定された場合に例外をスローする。
		 * 
		 * @param index 取得するフィールドのインデックス
		 * @return	指定した位置のフィールドデータ
		 * 
		 * @throws ArrayIndexOutOfBoundsException	無効なインデックスが指定された場合
		 */
		public CsvField get(int index) {
			return aryFields[index];
		}

		/**
		 * 指定の位置にあるフィールドデータを取得する。
		 * このメソッドでは、位置の指定にフィールド番号ではなく、0 から始まる
		 * インデックスを使用する。インデックスは、レコード先頭のフィールドを 0 とする。
		 * <br>
		 * このメソッドは、フィールドが存在しない位置のインデックスが指定された場合に <tt>null</tt> を返す。
		 * 
		 * @param index 取得するフィールドのインデックス
		 * @return	指定した位置のフィールドデータ。無効なインデックスが指定された場合は <tt>null</tt> を返す。
		 */
		public CsvField getField(int index) {
			if (index >= 0 && index < aryFields.length)
				return aryFields[index];
			else
				return null;
		}
	}
	
	/**
	 * {@link CsvRecord} から、フィールドを順次読み込む機能を提供する補助クラス。
	 * 
	 * @version 0.982	2009/09/13
	 * 
	 * @since 0.982
	 */
	static public class CsvFieldReader {
		private final CsvRecord record;
		private int pos = 0;

		/**
		 * {@link CsvRecord} インスタンスに関連付けられた、<code>CsvFieldReader</code> の
		 * 新しいインスタンスを生成する。
		 * 
		 * @param record 関連付ける {@link CsvRecord} オブジェクト
		 */
		public CsvFieldReader(CsvRecord record) {
			this.record = record;
		}

		/**
		 * このレコードがフィールドを持っている場合に <tt>true</tt> を返す。
		 * 
		 * @return レコードがフィールドを持っていれば <tt>true</tt>
		 */
		public boolean hasFields() {
			return (record.getNumFields() > 0);
		}

		/**
		 * 読み込まれるフィールドが存在する場合に <tt>true</tt> を返す。
		 */
		public boolean hasNextField() {
			return (pos < record.aryFields.length);
		}

		/**
		 * 次に読み出すフィールドを値を持つフィールドの位置までスキップする。
		 * <p>
		 * このメソッドは、次に読み出すフィールドが値を持たないフィールドの場合に、
		 * 値を持つフィールドの位置まで、値を持たないフィールドをスキップする。
		 * <br>
		 * 次に読み出すフィールドが値を保持している場合は、スキップしない。
		 */
		public void skipEmptyFields() {
			for (; pos < record.aryFields.length; pos++) {
				if (record.aryFields[pos].hasValue()) {
					return;
				}
			}
		}

		/**
		 * このレコードの文字列を返す。
		 * 
		 * @return	レコード文字列
		 */
		public String getRecordString() {
			return record.getRecord();
		}

		/**
		 * 関連付けられている {@link CsvRecord} オブジェクトへの参照を返す。
		 * 
		 * @return 関連付けられている {@link CsvRecord} オブジェクト
		 */
		public CsvRecord getRecord() {
			return record;
		}

		/**
		 * このレコードの先頭が位置する行番号を返す。
		 * 
		 * @return 行番号
		 */
		public int getLineNo() {
			return record.getLineNo();
		}
		
		/**
		 * このレコードのレコード番号を返す。
		 * 
		 * @return レコード番号
		 */
		public int getRecordNo() {
			return record.getRecordNo();
		}

		/**
		 * 次に読み込まれるフィールドのインデックスを返す。
		 * 
		 * @return フィールドインデックス
		 */
		public int getNextPosition() {
			return pos;
		}

		/**
		 * 現在位置からフィールドを読み込み、フィールドのインデックスを
		 * 次に進める。
		 * <p>
		 * フィールドが存在しない場合でも、フィールドインデックスは順次進められる。
		 * したがって、このメソッドによって返されるフィールドデータのフィールド
		 * 番号は、{@link #getNextPosition()} が返す値と同一となる。
		 * 
		 * @return フィールドが存在していれば、そのフィールドのデータを返す。
		 * 			フィールドが存在しない場合は <tt>null</tt> を返す。
		 */
		public CsvField readField() {
			CsvField ret = null;
			if (pos < record.aryFields.length) {
				ret = record.aryFields[pos];
			}
			pos++;
			return ret;
		}
		
		/**
		 * 現在位置からフィールドの値を読み込み、フィールドのインデックスを
		 * 次に進める。
		 * <p>
		 * フィールドの値が存在しない場合でも、フィールドインデックスは順次進められる。
		 * したがって、このメソッドによって返されるフィールドデータのフィールド
		 * 番号は、{@link #getNextPosition()} が返す値と同一となる。
		 * 
		 * @return フィールドが存在し、そのフィールドが値を保持していれば、その値を返す。
		 * 			それ以外の場合は <tt>null</tt> を返す。
		 */
		public String readValue() {
			String ret = null;
			if (pos < record.aryFields.length) {
				ret = record.aryFields[pos].getValue();
			}
			pos++;
			return ret;
		}
		
		/**
		 * 現在位置からフィールドの値を読み込み、フィールドのインデックスを
		 * 次に進める。このメソッドが返す値は、前後の空白が除去された値となる。
		 * 前後空白が除去された値の取得には、{@link CsvField#getTrimmedValue()} メソッドを呼び出している。
		 * <p>
		 * フィールドの値が存在しない場合でも、フィールドインデックスは順次進められる。
		 * したがって、このメソッドによって返されるフィールドデータのフィールド
		 * 番号は、{@link #getNextPosition()} が返す値と同一となる。
		 * 
		 * @return フィールドが存在し、そのフィールドが値を保持していれば、
		 * 			そのフィールドの前後空白が除去された値を返す。
		 * 			それ以外の場合は <tt>null</tt> を返す。
		 */
		public String readTrimmedValue() {
			String ret = null;
			if (pos < record.aryFields.length) {
				ret = record.aryFields[pos].getTrimmedValue();
			}
			pos++;
			return ret;
		}
	}
}
