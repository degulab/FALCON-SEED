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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CsvFileReader.java	1.81	2012/09/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileReader.java	1.50	2010/09/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import ssac.aadl.runtime.io.internal.AbTextFileReader;

/**
 * CSV 形式のテキストファイルを 1 レコード(基本的に 1 行)ずつ読み込むリーダークラス。
 * <p>このクラスは、自身がイテレータであり、その反復子を利用して
 * CSV 形式のテキストファイルから 1 レコードずつ取得します。
 * 1 レコードは、存在するフィールド(カラム)の順序で文字列が格納されている
 * 文字列リストとして返されます。
 * <p>
 * このクラスでは CSV 形式のフィールドの区切りをデフォルトでカンマとしています。
 * また、ダブルクオートでエンコードされている場合は、複数行も 1 レコードとして
 * 読み込まれます。
 * <p>
 * このクラスのインスタンスが生成されると、指定されたファイルをオープンし、
 * 読み込み待機状態となります。反復子がファイルの最後まで到達するか、
 * 読み込みエラーが発生した場合、このファイルは自動的に閉じられます。
 * ファイルが最後まで読み込まれない状態で処理を中断した場合は、
 * {@link #close()} メソッドを呼び出してファイルを閉じてください。
 * 
 * @version 1.81	2012/09/24
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Yuji Onuki (Statistics Bureau)
 * @author Shungo Sakaki (Tokyo University of Technology)
 * @author Akira Sasaki (HOSEI UNIVERSITY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 * 
 * @since 1.50
 */
public class CsvFileReader extends AbTextFileReader<List<String>>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 読み込み済みの次の CSV レコード **/
	private CsvRecord	_nextCsvRecord;
	
	/**
	 * カラム区切り文字
	 */
	private char _csvDelimiter = ',';

	/**
	 * 次に読み込みを行う文字の位置。この番号は、ファイル先頭からの文字位置を
	 * 示す、0 から始まるインデックスとなる。
	 */
	private int _charIndex = 0;
	/**
	 * 次に読み込みを行う行の行番号。この行番号は、1 から始まるテキストファイルの
	 * 実行番号となる。
	 */
	private int _csvLineNo = 1;
	/**
	 * 次に読み込みを行うフィールドの行先頭からの位置。この番号は、フィールドが
	 * 存在する行先頭からの相対位置を示す番号であり、行先頭を 1 とする。
	 */
	private int _csvPosInLine = 1;
	/**
	 * 次に読み込みを行うCSVのレコード番号。この番号は、1 から始まる。
	 */
	private int _csvRecordNo = 1;
	/**
	 * 次に読み込みを行うCSVのフィールド番号。この番号は、1 から始まる。
	 */
	private int _csvFieldNo = 1;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * プラットフォーム標準のエンコーディングで指定されたファイルを読み込む、
	 * <code>CsvFileReader</code> の新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public CsvFileReader(File file) throws FileNotFoundException
	{
		super(file);
	}
	
	/**
	 * 指定されたエンコーディングで指定されたファイルを読み込む、
	 * <code>CsvFileReader</code> の新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @param charsetName	エンコーディングとする文字セット名
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException	ファイルが存在しないか、何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException	指定された文字セットがサポートされていない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkRead</code> メソッドがファイルへの読み込みアクセスを拒否する場合
	 */
	public CsvFileReader(File file, String charsetName) throws FileNotFoundException, UnsupportedEncodingException
	{
		super(file, charsetName);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 現在のフィールド区切り文字を返す。
	 * 
	 * @return フィールド区切り文字
	 */
	public char getDelimiterChar() {
		return _csvDelimiter;
	}

	/**
	 * フィールド区切り文字として認識する文字を設定します。
	 * <br>
	 * このメソッドにより指定された文字をフィールド区切り文字として、
	 * CSV 形式のテキストファイルからフィールド読み込みます。<br>
	 * なお、この区切り文字には半角ダブルクオートは指定できません。
	 * 
	 * @param delimiter 設定する区切り文字
	 * 
	 * @throws IllegalArgumentException 区切り文字に無効な文字が指定された場合
	 */
	public void setDelimiterChar(final char delimiter) {
		if (delimiter == '"') {
			throw new IllegalArgumentException("Illegal delimiter : " + String.valueOf(delimiter));
		}
		this._csvDelimiter = delimiter;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 次に読み込まれる行の行番号を返す。
	 * 
	 * @return	行番号(1～)
	 */
	protected int getLineNo() {
		return _csvLineNo;
	}

	/**
	 * 次に読み込まれるレコードのレコード番号を返す。
	 * 
	 * @return レコード番号(1～)
	 */
	protected int getRecordNo() {
		return _csvRecordNo;
	}

	/**
	 * 読み込み済みの次のレコードを取得し、読み込み済みレコードをクリアします。
	 * @return	読み込み済みレコードが存在する場合はそのオブジェクトを返す。
	 * 			存在しない場合は <tt>null</tt> を返す。
	 */
	@Override
	protected List<String> popNextRecord() {
		_nextCsvRecord = null;
		return super.popNextRecord();
	}

	/**
	 * 新しいレコードを読み込み、読み込み済みレコードを更新します。
	 * このメソッドは、読み込み済みレコードの有無に関わらず、
	 * 新しいレコードを読み込みます。
	 * @return	新しいレコードが読み込まれた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws IOException	読み込みエラーが発生した場合
	 */
	@Override
	protected boolean readNextRecord() throws IOException {
		_nextCsvRecord = readRecord();
		if (_nextCsvRecord != null) {
			_nextRecord = _nextCsvRecord.toStringList();
			return true;
		} else {
			_nextRecord = null;
			return false;
		}
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
	protected CsvRecord readRecord() throws IOException {
		// 初期化
		StringBuilder bufRecord = new StringBuilder();
		StringBuilder bufOneField = new StringBuilder();
		ArrayList<CsvField> bufFields = new ArrayList<CsvField>();
		
		// 現在の位置情報を保存
		int curCharIndex = _charIndex;
		int curPosInLine = _csvPosInLine;
		int curLineNo = _csvLineNo;
		int curRecordNo = _csvRecordNo;
		
		// 1レコード分のフィールドを読み込む
		boolean existRecord = false;
		int iRead = readField(bufRecord, bufOneField, _csvDelimiter);
		for (;;) {
			if (iRead == _csvDelimiter) {
				// フィールド終端
				_csvFieldNo++;
				bufFields.add(new CsvField(curCharIndex, curLineNo, curPosInLine, bufOneField.toString()));
			}
			else if (iRead == '\r' || iRead == '\n') {
				// レコード終端
				_csvRecordNo++;
				_csvFieldNo = 1;
				//--- フィールド保存
				if (bufOneField.length() > 0 || !bufFields.isEmpty()) {
					// 最終フィールドの保存
					bufFields.add(new CsvField(curCharIndex, curLineNo, curPosInLine, bufOneField.toString()));
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
					bufFields.add(new CsvField(curCharIndex, curLineNo, curPosInLine, bufOneField.toString()));
					existRecord = true;
				}
				break;
			}
			else {
				// Not reached!
				throw new AssertionError();
			}
			// 次のフィールドを読み込む
			curCharIndex = _charIndex;
			curPosInLine = _csvPosInLine;
			curLineNo    = _csvLineNo;
			iRead = readField(bufRecord, bufOneField, _csvDelimiter);
		}
		
		// レコード情報の生成
		CsvRecord recEntry = null;
		if (existRecord) {
			recEntry = new CsvRecord(curLineNo, curRecordNo, bufRecord.toString(),
									  bufFields.toArray(new CsvField[bufFields.size()]));
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
		int iRead = _reader.read();
		if (iRead >= 0) {
			_charIndex++;
			_csvPosInLine++;
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
						_csvLineNo++;
						_csvPosInLine = 1;
					}
					else if (iRead == '\r') {
						// 改行文字として認識可能かチェック
						iRead = readChar();
						if (iRead == '\n') {
							// CRLF 改行
							bufRecord.append((char)iRead);
							bufField.append((char)iRead);
							_csvLineNo++;
							_csvPosInLine = 1;
						}
						else {
							// CR 改行
							_csvLineNo++;
							_csvPosInLine = 1;
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
				_csvLineNo++;
				_csvPosInLine = 1;
				break;
			}
			else if (iRead == '\r') {
				// レコード終端文字
				_csvLineNo++;
				_csvPosInLine = 1;
				// 次の文字を先読み
				_reader.mark(2);
				int nextRead = _reader.read();
				if (nextRead != '\n') {
					_reader.reset();
				} else {
					_charIndex++;
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
	 * @version 1.50	2010/09/29
	 * 
	 * @author Yasunari Ishizuka (PieCake,Inc.)
	 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
	 * @author Yuji Onuki (Statistics Bureau)
	 * @author Shungo Sakaki (Tokyo University of Technology)
	 * @author Akira Sasaki (HOSEI UNIVERSITY)
	 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
	 * 
	 * @since 1.50
	 */
	static protected class CsvField // implements Comparable<CsvField>
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
	 * @version 1.50	2010/09/29
	 * 
	 * @author Yasunari Ishizuka (PieCake,Inc.)
	 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
	 * @author Yuji Onuki (Statistics Bureau)
	 * @author Shungo Sakaki (Tokyo University of Technology)
	 * @author Akira Sasaki (HOSEI UNIVERSITY)
	 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
	 * 
	 * @since 1.50
	 */
	static protected class CsvRecord
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
		
		public List<String> toStringList() {
			ArrayList<String> list = new ArrayList<String>(aryFields.length);
			for (CsvField field : aryFields) {
				if (field != null && field.hasValue()) {
					list.add(field.getValue());
				}
				else {
					list.add(null);
				}
			}
			return list;
		}

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

		/**
		 * 指定の位置にあるフィールドの値を取得する。
		 * このメソッドでは、位置の指定にフィールド番号ではなく、0 から始まる
		 * インデックスを使用する。インデックスは、レコード先頭のフィールドを 0 とする。
		 * <br>
		 * このメソッドは、フィールドが存在しない位置のインデックスが指定された場合にも <tt>null</tt> を返す。
		 * 
		 * @param index	取得するフィールドのインデックス
		 * @return	指定した位置のフィールドの値。無効なインデックスが指定された場合は <tt>null</tt> を返す。
		 * 
		 * @since 0.20
		 */
		public String getValue(int index) {
			CsvField field = getField(index);
			return (field==null ? null : field.getValue());
		}
	}
}
