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
 * @(#)CsvFileWriter.java	1.90	2013/08/07
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileWriter.java	1.81	2012/09/24
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileWriter.java	1.50	2010/09/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Collection;

import ssac.aadl.runtime.AADLMessages;
import ssac.aadl.runtime.AADLRuntimeException;
import ssac.aadl.runtime.io.internal.AbTextFileWriter;

import dtalge.util.Strings;

/**
 * テキストファイルに任意の文字列を CSV 形式のレコード、
 * もしくはフィールドとして出力するライタークラス。
 * <p>
 * このクラスのインスタンスが生成されると、指定されたファイルをオープンし、
 * 書き込み待機状態となります。<br>
 * <b>このクラスでは、{@link #close()} メソッドが呼び出されるまでファイルは
 * 閉じられません。ファイルへの書き込みが完了したときは、
 * 必ず {@link #close()} メソッドを呼び出してファイルを閉じてください。</b>
 * 
 * @version 1.90	2013/08/07
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
public class CsvFileWriter extends AbTextFileWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** カラム区切り文字 **/
	private char _csvDelimiter = ',';

	/**
	 *  現在の書き込み位置がフィールド先頭であることを示す。
	 *  このフラグが <tt>true</tt> の場合、レコードの先頭であるか、
	 *  フィールド区切り文字の直後であることを示す。
	 */
	private boolean _curposFieldTop = true;
	/**
	 * 現在の書き込み位置がレコード先頭であることを示す。
	 */
	private boolean _curposRecordTop = true;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * プラットフォーム標準のエンコーディングで指定されたファイルへ書き込む、
	 * <code>TextFileWriter</code> の新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkWrite</code> メソッドがファイルへの書き込みアクセスを拒否する場合
	 */
	public CsvFileWriter(File file) throws FileNotFoundException
	{
		super(file);
	}
	
	/**
	 * 指定されたエンコーディングで指定されたファイルへ書き込む、
	 * <code>TextFileWriter</code> の新しいインスタンスを生成します。
	 * @param file	読み込むファイル
	 * @param charsetName	エンコーディングとする文字セット名。
	 * 						<tt>null</tt> の場合は、プラットフォーム標準のエンコーディングとなる。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws SecurityException	セキュリティマネージャが存在し、
	 * <code>checkWrite</code> メソッドがファイルへの書き込みアクセスを拒否する場合
	 */
	public CsvFileWriter(File file, String charsetName) throws FileNotFoundException, UnsupportedEncodingException
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

	/**
	 * ファイルを閉じます。
	 * すでに閉じられている場合は、何もしません。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @return	正常にファイルを閉じた場合、もしくは既に閉じられている場合は <tt>true</tt> を返す。
	 * 			閉じられなかった場合は <tt>false</tt> を返す。
	 */
	@Override
	public boolean close() {
		boolean result = true;
		if (_writer != null) {
			try {
				if (!_curposRecordTop) {
					_writer.newLine();
					_curposRecordTop = true;
					_curposFieldTop = true;
				}
				_writer.close();
			} catch (Exception ex) {
				_lastException = new AADLRuntimeException(AADLMessages.formatFailedCloseMessage(_targetFile), ex);
				result = false;
			}
			_writer = null;
		}
		return result;
	}

	/**
	 * レコード区切り文字を書き込みます。
	 * 通常、このメソッドが出力するレコード区切り文字は Java 標準の改行文字であり、
	 * プラットフォーム標準の改行文字が出力されます。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	@Override
	public boolean newRecord() {
		boolean result = true;
		try {
			ensureOpen();
			_writer.newLine();
			_curposRecordTop = true;
			_curposFieldTop  = true;
		}
		catch (Exception ex) {
			_lastException = new AADLRuntimeException(AADLMessages.formatFailedWriteMessage(_targetFile), ex);
			result = false;
		}
		return result;
	}
	
	/**
	 * 現在のフィールド区切り文字を書き込みます。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean newField() {
		boolean result = true;
		try {
			ensureOpen();
			_writer.write(_csvDelimiter);
			_curposFieldTop  = true;
			_curposRecordTop = false;
		}
		catch (Exception ex) {
			_lastException = new AADLRuntimeException(AADLMessages.formatFailedWriteMessage(_targetFile), ex);
			result = false;
		}
		return result;
	}
	
	/**
	 * 指定されたオブジェクトを、{@link String#valueOf(Object)} メソッドにより文字列に変換し、
	 * その文字列を新しいフィールドとして書き込みます。
	 * 引数が <code>BigDecimal</code> オブジェクトの場合、
	 * 基本的に指数フィールドのない、極力短い文字列表現として出力します。
	 * 引数が <tt>null</tt> の場合は空のフィールドが出力されます。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @param obj	出力するオブジェクト
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean writeField(Object obj) {
		if (obj == null) {
			return writeNewField(null);
		}
		else if (obj instanceof BigDecimal) {
			BigDecimal val = (BigDecimal)obj;
			if (BigDecimal.ZERO.compareTo(val) == 0) {
				return writeNewField(BigDecimal.ZERO.toPlainString());
			} else {
				return writeNewField(val.stripTrailingZeros().toPlainString());
			}
		}
		else if (obj instanceof String) {
			return writeNewField((String)obj);
		}
		else {
			return writeNewField(String.valueOf(obj));
		}
	}

	/**
	 * 指定されたコレクションに含まれる全てのオブジェクトを、コレクションの反復子が返す
	 * 順序で、新しいフィールドとして書き込みます。
	 * このメソッドは、コレクションに含まれる各オブジェクトについて {@link #writeField(Object)} を
	 * 呼び出すのと同じように動作します。<br>
	 * なお、コレクションの要素が空の場合は、何も行わずに <tt>true</tt> を返します。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @param c		フィールドとして出力するオブジェクトのコレクション
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean writeFields(Collection<?> c) {
		if (!c.isEmpty()) {
			for (Object obj : c) {
				if (!writeField(obj)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 指定されたコレクションに含まれる全てのオブジェクトを、コレクションの反復子が返す
	 * 順序で、新しいレコードの新しいフィールドとして書き込み、レコードを終了します。
	 * このメソッドは、現在の書き込み位置がレコード先頭ではない場合にのみレコードを区切り文字を
	 * 出力します。その後、{@link #writeFields(Collection)}、{@link #newRecord()} を
	 * 呼び出すのと同じように動作します。<br>
	 * なお、コレクションの要素が空の場合は、フィールドを持たないレコードが出力されます。
	 * <p>このメソッドが <tt>false</tt> を返した場合、
	 * {@link #lastException()} で最後に発生した例外を取得できます。
	 * @param c		フィールドとして出力するオブジェクトのコレクション
	 * @return	正常に書き込めた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean writeRecord(Collection<?> c) {
		if (!_curposRecordTop) {
			if (!newRecord())
				return false;
		}
		
		if (!writeFields(c)) {
			return false;
		}
		
		return newRecord();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * 指定された文字列を新しいフィールドとして
	 * 取得されたフィールドデータは、エンクオート済みです。
	 * フィールドバッファが空の場合は、<tt>null</tt> を返します。
	 * @return	エンクオート済みのフィールドデータとなる文字列を返す。
	 * 			フィールドバッファが空の場合は、<tt>null</tt> を返す。
	 */
	protected boolean writeNewField(String str) {
		boolean result = true;
		str = enquoteString(str);
		try {
			ensureOpen();
			if (!_curposFieldTop) {
				_writer.write(_csvDelimiter);
				_curposFieldTop = true;
			}
			if (str != null && str.length() > 0) {
				_writer.write(str);
			}
			_curposFieldTop  = false;
			_curposRecordTop = false;
		}
		catch (Exception ex) {
			_lastException = new AADLRuntimeException(AADLMessages.formatFailedWriteMessage(_targetFile), ex);
			result = false;
		}
		return result;
	}
	
	/**
	 * 指定された文字列を、必要に応じてダブルクオートで囲みます。
	 * ダブルクオートで囲む必要のあるフィールドデータは、次の場合です。
	 * <ul>
	 * <li>ダブルクオートが含まれている。
	 * <li>フィールド区切り文字が含まれている。
	 * <li>改行文字が含まれている。
	 * </ul>
	 * なお、ダブルクオートが含まれている場合、そのダブルクオートを二重にします。
	 * @return	エンクオートされた文字列を返す。エンクオートの必要がない場合は、
	 * 			指定された文字列をそのまま返す。
	 */
	protected String enquoteString(String str) {
		if (!Strings.isNullOrEmpty(str) && Strings.contains(str, _csvDelimiter, '"', '\t', '\r', '\n')) {
			StringBuilder sb = new StringBuilder(str.length() * 2);
			sb.append('"');
			sb.append(str.replaceAll("\"", "\"\""));
			sb.append('"');
			str = sb.toString();
		}
		return str;
	}
}
