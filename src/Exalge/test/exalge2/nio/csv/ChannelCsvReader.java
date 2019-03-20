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
 *  Copyright 2007-2009  SOARS Project.
 *  <author> Hiroshi Deguchi(SOARS Project.)
 *  <author> Li Hou(SOARS Project.)
 *  <author> Yasunari Ishizuka(PieCake.inc,)
 */
/*
 * @(#)ChannelCsvReader.java	0.980	2009/04/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.nio.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;

import exalge2.io.FileUtil;
import exalge2.util.Strings;


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
 * </ul>
 * また、先頭行の行番号を 1、行の先頭カラム番号を 1 とする。
 * 
 * @author Hiroshi Deguchi(SOARS Project.)
 * @author Yasunari Ishizuka(PieCake.inc,)
 * 
 * @version 0.980	2009/04/09
 *
 * @since 0.980
 */
public class ChannelCsvReader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/**
	 * カラム区切り文字
	 */
	private char csvDelimiter = ',';
	/**
	 * <code>Reader</code> インスタンス
	 */
	private Reader csvReader;

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
	 * 読み込まれた文字がLF(\n)ならスキップするフラグ
	 */
	private boolean skipLF = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 読み込み元のファイルをデフォルトの文字セットで読み込む、新規 <code>CsvReader</code> インスタンスを生成する。
	 * 
	 * @param csvFile 読み込むファイル
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public ChannelCsvReader(File csvFile)
		throws FileNotFoundException, IOException
	{
		this(csvFile, null);
	}

	/**
	 * 読み込み元ファイルを指定された文字セットで読み込む、新規 <code>CsvReader</code> インスタンスを生成する。
	 * 指定された文字セット名が <tt>null</tt> もしくは空文字列の場合は、実行時のプラットフォームに
	 * 依存した文字セット名が使用される。
	 * 
	 * @param csvFile 読み込むファイル
	 * @param encoding 文字セット名
	 * 
	 * @throws FileNotFoundException ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 */
	public ChannelCsvReader(File csvFile, String encoding)
		throws FileNotFoundException, UnsupportedEncodingException
	{
		if (Strings.isNullOrEmpty(encoding)) {
			// default encoding
			FileReader fReader = new FileReader(csvFile);
			this.csvReader = new BufferedReader(fReader);
		}
		else {
			// specified encoding
			FileInputStream fiStream = new FileInputStream(csvFile);
			InputStreamReader isReader = null;
			try {
				isReader = new InputStreamReader(fiStream, encoding);
			}
			catch (UnsupportedEncodingException ex) {
				FileUtil.closeStream(isReader);
				throw ex;
			}
			catch (RuntimeException ex) {
				FileUtil.closeStream(isReader);
				throw ex;
			}
			this.csvReader = new BufferedReader(isReader);
		}
	}
	
	public ChannelCsvReader(File csvFile, String encoding, boolean useDirectBuffer)
		throws FileNotFoundException, IllegalCharsetNameException, UnsupportedCharsetException, UnsupportedEncodingException
	{
		// 文字セットの取得
		CharsetDecoder ce;
		try {
		if (Strings.isNullOrEmpty(encoding))
			ce = Charset.defaultCharset().newDecoder();
		else
			ce = Charset.forName(encoding).newDecoder();
		}
		catch (Throwable ex) {
			String message = ex.getLocalizedMessage();
			throw new UnsupportedEncodingException(message==null ? ex.toString() : message);
		}
		ce.onMalformedInput(CodingErrorAction.REPLACE);
		ce.onUnmappableCharacter(CodingErrorAction.REPLACE);
		ce.reset();
		
		// ファイルチャネルを利用した Reader の生成
		this.csvReader = new BufferedChannelReader((new FileInputStream(csvFile)).getChannel(), ce, -1, useDirectBuffer);
	}
	
	public ChannelCsvReader(String encoding, File csvFile)
		throws FileNotFoundException, UnsupportedEncodingException
	{
		// 文字セットの取得
		CharsetDecoder ce;
		try {
		if (Strings.isNullOrEmpty(encoding))
			ce = Charset.defaultCharset().newDecoder();
		else
			ce = Charset.forName(encoding).newDecoder();
		}
		catch (Throwable ex) {
			String message = ex.getLocalizedMessage();
			throw new UnsupportedEncodingException(message==null ? ex.toString() : message);
		}
		ce.onMalformedInput(CodingErrorAction.REPLACE);
		ce.onUnmappableCharacter(CodingErrorAction.REPLACE);
		ce.reset();
		
		// ファイルチャネルを利用した Reader の生成
		this.csvReader = Channels.newReader((new FileInputStream(csvFile)).getChannel(), ce, -1);
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

	/**
	 * 次に読み込まれる行の行番号を返す。
	 * 
	 * @return	行番号(1～)
	 */
	public int getLineNo() {
		return this.csvLineNo;
	}

	/**
	 * 次に読み込まれるレコードのレコード番号を返す。
	 * 
	 * @return レコード番号(1～)
	 */
	public int getRecordNo() {
		return this.csvRecordNo;
	}

	/**
	 * 次に読み込まれるフィールドのフィールド番号を返す。
	 * 
	 * @return フィールド番号(1～)
	 */
	public int getFieldNo() {
		return this.csvFieldNo;
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
	 */
	public ChannelCsvRecord readRecord() throws IOException {
		// 初期化
		StringBuilder bufRecord = new StringBuilder();
		StringBuilder bufOneField = new StringBuilder();
		ArrayList<ChannelCsvField> bufFields = new ArrayList<ChannelCsvField>();
		
		// 現在の位置情報を保存
		int curCharIndex = charIndex;
		int curPosInLine = csvPosInLine;
		int curLineNo = csvLineNo;
		int curRecordNo = csvRecordNo;
		
		// 1レコード分のフィールドを読み込む
		boolean existRecord = false;
		int iRead = readField(bufRecord, bufOneField, csvDelimiter);
		for (;;) {
			if (iRead == csvDelimiter) {
				// フィールド終端
				csvFieldNo++;
				bufFields.add(new ChannelCsvField(curCharIndex, curLineNo, curPosInLine, bufOneField.toString()));
			}
			else if (iRead == '\r' || iRead == '\n') {
				// レコード終端
				csvRecordNo++;
				csvFieldNo = 1;
				//--- フィールド保存
				if (bufOneField.length() > 0 || !bufFields.isEmpty()) {
					// 最終フィールドの保存
					bufFields.add(new ChannelCsvField(curCharIndex, curLineNo, curPosInLine, bufOneField.toString()));
				}
				//--- レコード保存、もしくは空行として保存
				existRecord = true;
				break;
			}
			else if (iRead < 0) {
				// ストリーム終端
				if (bufOneField.length() <= 0 && bufFields.isEmpty()) {
					// 先頭フィールドが存在しないので、レコードなし
					existRecord = false;
				}
				else {
					// 最終フィールドの保存
					bufFields.add(new ChannelCsvField(curCharIndex, curLineNo, curPosInLine, bufOneField.toString()));
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
			iRead = readField(bufRecord, bufOneField, csvDelimiter);
		}
		
		// レコード情報の生成
		ChannelCsvRecord recEntry = null;
		if (existRecord) {
			recEntry = new ChannelCsvRecord(curLineNo, curRecordNo, bufRecord.toString(),
									  bufFields.toArray(new ChannelCsvField[bufFields.size()]));
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
	 */
	private int readChar() throws IOException
	{
		int iRead = csvReader.read();
		if (iRead >= 0) {
			if (skipLF) {
				skipLF = false;
				if (iRead == '\n') {
					charIndex++;
					return readChar();
				}
			}
			charIndex++;
			csvPosInLine++;
		} else {
			skipLF = false;
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
				skipLF = true;
				/*
				// 次の文字を先読み
				csvReader.mark(2);
				int nextRead = csvReader.read();
				if (nextRead != '\n') {
					csvReader.reset();
				} else {
					charIndex++;
				}
				*/
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
	 * @version 0.10	2008/11/19
	 * 
	 * @since 0.10
	 */
	static public class ChannelCsvField
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
		 * <code>ChannelCsvField</code> の新しいインスタンスを生成する。
		 * 
		 * @param index		フィールド先頭のインデックス
		 * @param lineNo	フィールド先頭の行番号
		 * @param posInLine	フィールド先頭の行先頭からの位置
		 * @param value		フィールド・データ
		 */
		public ChannelCsvField(int index, int lineNo, int posInLine, String value) {
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
	 * @version 0.10	2008/11/19
	 * 
	 * @since 0.10
	 */
	static public class ChannelCsvRecord
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
		private final ChannelCsvField[] aryFields;

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
		public ChannelCsvRecord(int lineNo, int recordNo, String record, ChannelCsvField[] fields) {
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
		 * 
		 * @param index 取得するフィールドのインデックス
		 * @return	指定した位置のフィールドデータ
		 */
		public ChannelCsvField getField(int index) {
			return aryFields[index];
		}
	}
	
	/**
	 * {@link ChannelCsvRecord} から、フィールドを順次読み込む機能を提供する補助クラス。
	 * 
	 * @version 0.10	2008/11/19
	 * 
	 * @since 0.10
	 */
	static public class ChannelCsvRecordReader {
		private final ChannelCsvRecord record;
		private int pos = 0;

		/**
		 * {@link ChannelCsvRecord} インスタンスに関連付けられた、<code>ChannelCsvRecordReader</code> の
		 * 新しいインスタンスを生成する。
		 * 
		 * @param record 関連付ける {@link ChannelCsvRecord} オブジェクト
		 */
		public ChannelCsvRecordReader(ChannelCsvRecord record) {
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
		 * このレコードの文字列を返す。
		 * 
		 * @return	レコード文字列
		 */
		public String getRecordString() {
			return record.getRecord();
		}

		/**
		 * 関連付けられている {@link ChannelCsvRecord} オブジェクトへの参照を返す。
		 * 
		 * @return 関連付けられている {@link ChannelCsvRecord} オブジェクト
		 */
		public ChannelCsvRecord getRecord() {
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
		public ChannelCsvField readField() {
			ChannelCsvField ret = null;
			if (pos < record.getNumFields()) {
				ret = record.getField(pos);
			}
			pos++;
			return ret;
		}
	}

	static protected class BufferedChannelReader extends Reader
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------

		/** 最小バッファサイズ **/
		static protected final int MIN_BYTE_BUFFER_SIZE = 32;
		/** 標準のバッファサイズ **/
		static protected final int DEFAULT_BYTE_BUFFER_SIZE = 8192;

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		/** 文字セット **/
		final private Charset cs;
		/** デコーダ **/
		final private CharsetDecoder decoder;
		/** 読み込み可能チャネル **/
		final private ReadableByteChannel channel;
		/** バッファ **/
		final private ByteBuffer buffer;
		/** サロゲートペアの上位文字が存在する場合は <tt>true</tt> **/
		private boolean haveLeftChar;
		/** サロゲートペアの上位文字 **/
		private char leftChar;
		
		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		protected BufferedChannelReader(ReadableByteChannel ch, CharsetDecoder dec,
										 int bufferSize, boolean useDirectBuffer)
		{
			if (ch == null)
				throw new NullPointerException("ReadableByteChannel is null!");
			haveLeftChar = false;
			channel = ch;
			cs = dec.charset();
			decoder = dec;
			int cap = (bufferSize >= 0 ? bufferSize >= MIN_BYTE_BUFFER_SIZE ? bufferSize : MIN_BYTE_BUFFER_SIZE : DEFAULT_BYTE_BUFFER_SIZE);
			if (useDirectBuffer)
				buffer = ByteBuffer.allocateDirect(cap);
			else
				buffer = ByteBuffer.allocate(cap);
			buffer.flip();
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		/**
		 * このオブジェクトに割り当てられたチャネルがオープンされている状態であれば <tt>true</tt> を返す。
		 */
		public boolean isOpen() {
			return channel.isOpen();
		}

		/**
		 * ダイレクトバッファを使用している場合は <tt>true</tt> を返す。
		 */
		public boolean isDirectBufferUsed() {
			return buffer.isDirect();
		}

		/**
		 * このオブジェクトのエンコーディングに指定されている文字セット名を返す。
		 */
		public String getEncoding() {
			return cs.name();
		}

		//------------------------------------------------------------
		// Implement Reader interfaces
		//------------------------------------------------------------
		
		/* (non-Javadoc)
		 * @see java.io.Reader#close()
		 */
		@Override
		public void close() throws IOException {
			synchronized (lock) {
				if (!isOpen())
					return;
				implClose();
			}
		}

		/* (non-Javadoc)
		 * @see java.io.Reader#read()
		 */
		@Override
		public int read() throws IOException {
			return readChar();
		}

		/* (non-Javadoc)
		 * @see java.io.Reader#read(char[], int, int)
		 */
		@Override
		public int read(char[] array, int offset, int length) throws IOException {
			int off = offset;
			int len = length;
			synchronized (lock) {
				ensureOpen();
				if ((off < 0) || (off > array.length) || (len < 0) ||
					((off + len) > array.length) || ((off + len) < 0))
				{
					throw new IndexOutOfBoundsException();
				}
				if (len == 0)
					return 0;
				
				int n = 0;
				
				if (haveLeftChar) {
					array[off] = leftChar;
					off++;
					len--;
					haveLeftChar = false;
					n = 1;
					if ((len == 0) || !implReady()) {
						return n;
					}
				}
				
				if (len == 1) {
					int c = readChar();
					if (c == -1)
						return (n == 0 ? -1 : n);
					array[off] = (char)c;
					return (n + 1);
				}
				
				return (n + implRead(array, off, len));
			}
		}

		/* (non-Javadoc)
		 * @see java.io.Reader#ready()
		 */
		@Override
		public boolean ready() throws IOException {
			synchronized (lock) {
				ensureOpen();
				return (haveLeftChar || implReady());
			}
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------

		/**
		 * チャネルがオープンされていることをチェックする。
		 */
		private void ensureOpen() throws IOException
		{
			if (!isOpen())
				throw new IOException("Channel closed");
			else
				return;
		}

		/**
		 * チャネルの内容をバッファへ読み込む。
		 * @throws IOException	入出力エラーが発生した場合
		 */
		private int readBytes() throws IOException {
			buffer.compact();
			try {
				int r = channel.read(buffer);
				if (r < 0) {
					return r;
				}
			}
			finally {
				buffer.flip();
			}
			
			int remain = buffer.remaining();
			assert (remain != 0) : remain;
			return remain;
		}
		
		/**
		 * 配列の一部に文字を読み込む。
		 * @param array		転送先バッファ
		 * @param off		文字の格納開始オフセット
		 * @param len		読み込む文字の最大数
		 * @return	読み込まれた文字数。終端に達した場合は -1
		 * @throws IOException	入出力エラーが発生した場合
		 */
		int implRead(char[] array, int off, int len) throws IOException {
			// 転送バッファの確保
			assert (len > 0);
			CharBuffer charbuf = CharBuffer.wrap(array, off, len);
			if (charbuf.position() != 0) {
				charbuf = charbuf.slice();
			}

			// 読み込み
			boolean eof = false;
			for (;;) {
				CoderResult result = decoder.decode(buffer, charbuf, eof);
				if (result.isUnderflow()) {
					if (eof)
						break;	// 以降の読み込み不要
					if (!charbuf.hasRemaining())
						break;	// 転送先バッファがフル
					if ((charbuf.position() > 0) && !inReady())
						break;	// 読み込み継続不可
					//--- チャネルからバッファへ転送
					int n = readBytes();
					if (n < 0) {
						//--- チャネルの終端に到達
						eof = true;
						if ((charbuf.position() == 0) && (!buffer.hasRemaining()))
							break;	// 読み込みバッファが空
						decoder.reset();
					}
				}
				else if (result.isOverflow()) {
					assert (charbuf.position() > 0);	// 転送されていない場合は assertion
					break;
				}
				else {
					result.throwException();
				}
			}

			// 終端に到達していれば、デコーダをリセット
			if (eof) {
				decoder.reset();
			}

			// 終端に到達していれば、終了
			if (charbuf.position() == 0) {
				if (eof) {
					return (-1);
				}
				assert (false);	// not reached!
			}
			
			return charbuf.position();
		}

		/**
		 * チャネルが読み込み可能なら <tt>true</tt> を返す。
		 */
		private boolean inReady() {
			return (channel instanceof FileChannel);
		}

		/**
		 * データが取得可能なら <tt>true</tt> を返す。
		 */
		boolean implReady() {
			return (buffer.hasRemaining() || inReady());
		}

		/**
		 * チャネルを閉じる。
		 * @throws IOException	入出力エラーが発生した場合
		 */
		void implClose() throws IOException {
			if (channel != null) {
				channel.close();
			}
		}

		/**
		 * バッファから１文字だけ読み込む。
		 * @return	0～65535 の範囲の整数としての、読み込まれた文字。チャネルの終端に達した場合は -1
		 * @throws IOException	入出力エラーが発生した場合
		 */
		private int readChar() throws IOException {
			synchronized (lock) {
				// サロゲートペアの上位文字が保存されている場合は、その文字を返す。
				if (haveLeftChar) {
					haveLeftChar = false;
					return leftChar;
				}

				// バッファから最大２文字読み込む
				char[] cb = new char[2];
				int n = read(cb, 0, 2);
				switch (n) {
					case -1:	// 終端
						return -1;
					case 2 :	// サロゲートペア
						leftChar = cb[1];
						haveLeftChar = true;
					case 1 :	// 単一文字
						return cb[0];
					default :	// not reached!
						assert false : n;
						return -1;
				}
			}
	    }
	}
}
