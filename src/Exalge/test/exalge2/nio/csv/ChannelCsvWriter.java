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
 * @(#)ChannelCsvWriter.java	0.980	2009/04/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package exalge2.nio.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import exalge2.io.FileUtil;
import exalge2.util.Strings;

/**
 * CSV ファイルへの書き込み機能を提供する Writer クラス。
 * <br>
 * 基本的にカンマ(<code>,</code>)区切りのテキストファイルを出力する。
 * <p>
 * <strong>(注)</strong> このクラスは、標準 Java {@link java.io.Writer Writer} インタフェースの
 * 拡張ではない。
 * 
 * @author Hiroshi Deguchi(SOARS Project.)
 * @author Yasunari Ishizuka(PieCake.inc,)
 * 
 * @version 0.980	2009/04/09
 *
 * @since 0.980
 */
public class ChannelCsvWriter
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
	 * <code>Writer</code> インスタンス
	 */
	private Writer csvWriter;
	/**
	 * 行区切り文字列
	 */
    private String lineSeparator;

	/**
	 * 次に書き込みを行うCSVのフィールド番号。この番号は、1 から始まる。
	 */
	private int csvFieldNo = 1;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 書き込み先のファイルにデフォルト文字セットで出力する、新規 <code>CsvWriter</code> インスタンスを生成する。
	 * 
	 * @param csvFile 書き込み先ファイル
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public ChannelCsvWriter(File csvFile)
		throws FileNotFoundException, IOException
	{
		this(csvFile, null);
	}
	
	/**
	 * 書き込み先ファイルに指定された文字セットで出力する、新規 <code>CsvWriter</code> インスタンスを生成する。
	 * 指定された文字セット名が <tt>null</tt> もしくは空文字列の場合は、実行時のプラットフォームに
	 * 依存した文字セット名が使用される。
	 * 
	 * @param csvFile 書き込み先ファイル
	 * @param encoding 文字セット名
	 * 
	 * @throws FileNotFoundException ファイルは存在するが、普通のファイルではなくディレクトリである場合、
	 * ファイルは存在せず作成もできない場合、または何らかの理由で開くことができない場合
	 * @throws UnsupportedEncodingException 指定された文字セットがサポートされていない場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public ChannelCsvWriter(File csvFile, String encoding)
		throws FileNotFoundException, UnsupportedEncodingException, IOException
	{
		// 行区切り文字の取得
		lineSeparator = System.getProperty("line.separator");
		
		// Writer の生成
		if (Strings.isNullOrEmpty(encoding)) {
			// default encoding
			this.csvWriter = new BufferedWriter(new FileWriter(csvFile));
		}
		else {
			// specified encoding
			FileOutputStream foStream = new FileOutputStream(csvFile);
			OutputStreamWriter osWriter = null;
			try {
				osWriter = new OutputStreamWriter(foStream, encoding);
			}
			catch (UnsupportedEncodingException ex) {
				FileUtil.closeStream(foStream);
				throw ex;
			}
			catch (NullPointerException ex) {
				FileUtil.closeStream(foStream);
				throw ex;
			}
			this.csvWriter = new BufferedWriter(osWriter);
		}
	}

	// for Test
	public ChannelCsvWriter(File csvFile, String encoding, boolean useDirectBuffer)
		throws FileNotFoundException, UnsupportedEncodingException
	{
		// 行区切り文字の取得
		lineSeparator = System.getProperty("line.separator");
		
		// 文字セットの取得
		CharsetEncoder ce;
		try {
		if (Strings.isNullOrEmpty(encoding))
			ce = Charset.defaultCharset().newEncoder();
		else
			ce = Charset.forName(encoding).newEncoder();
		}
		catch (Throwable ex) {
			String message = ex.getLocalizedMessage();
			throw new UnsupportedEncodingException(message==null ? ex.toString() : message);
		}
		
		// ファイルチャネルを利用した Writer の生成(ヒープバッファ)
		ce.reset();
		this.csvWriter = new BufferedChannelWriter((new FileOutputStream(csvFile)).getChannel(), ce, -1, useDirectBuffer);
	}
	
	public ChannelCsvWriter(String encoding, File csvFile)
		throws FileNotFoundException, UnsupportedEncodingException
	{
		// 行区切り文字の取得
		lineSeparator = System.getProperty("line.separator");
		
		// 文字セットの取得
		CharsetEncoder ce;
		try {
		if (Strings.isNullOrEmpty(encoding))
			ce = Charset.defaultCharset().newEncoder();
		else
			ce = Charset.forName(encoding).newEncoder();
		}
		catch (Throwable ex) {
			String message = ex.getLocalizedMessage();
			throw new UnsupportedEncodingException(message==null ? ex.toString() : message);
		}
		
		// ファイルチャネルを利用した Writer の生成(ヒープバッファ)
		ce.reset();
		this.csvWriter = Channels.newWriter((new FileOutputStream(csvFile)).getChannel(), ce, -1);
	}

	//------------------------------------------------------------
	// Attributes
	//------------------------------------------------------------

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
		if (delimiter == '"')
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
		FileUtil.closeStream(this.csvWriter);
	}

	/**
	 * ストリームをフラッシュする。
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void flush() throws IOException
	{
		csvWriter.flush();
	}

	/**
	 * 現在の出力位置に改行文字を出力し、フィールド位置情報を先頭フィールドへリセットする。
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void newLine() throws IOException
	{
		csvWriter.write(lineSeparator);
		csvFieldNo = 1;
	}

	/**
	 * 現在の出力位置に空行を出力し、フィールド位置情報を先頭フィールドへリセットする。
	 * <br>
	 * 現在位置が先頭フィールドではない場合、現在位置に改行文字を出力した後に空行を出力する。
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeBlankLine() throws IOException
	{
		if (csvFieldNo > 1) {
			newLine();
		}
		newLine();
	}

	/**
	 * 現在の出力位置に、指定された文字列を１行分の文字列として出力する。
	 * フィールド位置情報は、先頭フィールドへリセットされる。また、指定の文字列を出力後、
	 * 自動的に改行文字も出力する。
	 * <br>
	 * 現在位置が先頭フィールドではない場合、現在位置に改行文字を出力した後に空行を出力する。
	 * <p><b>注:</b>このメソッドは、指定された文字列をそのまま出力するので、エンクオートされない。
	 * 
	 * @param lineValue 出力する文字列
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeLine(String lineValue) throws IOException
	{
		if (csvFieldNo > 1) {
			newLine();
		}
		csvWriter.write(lineValue);
		newLine();
	}

	/**
	 * 現在の出力位置に、指定された文字列を１行分の文字列として出力する。
	 * フィールド位置情報は、先頭フィールドへリセットされる。また、指定の文字列を出力後、
	 * 自動的に改行文字も出力する。
	 * <br>
	 * 現在位置が先頭フィールドではない場合、現在位置に改行文字を出力した後に空行を出力する。
	 * <p><b>(注)</b>このメソッドは、指定された文字列をそのまま出力するので、エンクオートされない。
	 * 
	 * @param format	書式文字列(参考：{@link java.lang.String#format(String, Object[])})
	 * @param args		書式文字列の書式指示子により参照される引数(参考：{@link java.lang.String#format(String, Object[])})
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @see java.lang.String#format(String, Object[])
	 */
	public void writeLine(String format, Object...args) throws IOException {
		writeLine(String.format(format, args));
	}
	
	/**
	 * 現在位置にフィールド文字列を出力する。
	 * <br>
	 * <code>isFinalField</code> が <tt>false</tt> の場合は、フィールド文字列出力後に
	 * フィールド区切り文字を出力し、フィールド位置情報をインクリメントする。
	 * <br>
	 * <code>isFinalField</code> が <tt>true</tt> の場合は、フィールド文字列出力後に
	 * 改行文字を出力し、フィールド位置情報を先頭フィールドへリセットする。
	 * <p>
	 * なお、このメソッドでは、必要に応じてエンクオートする。
	 * 
	 * @param isFinalField 出力したフィールドをレコード終端とする場合は <tt>true</tt>
	 * @param strValue 出力するフィールド文字列
	 * 
	 * @throws NullPointerException	<code>strValue</code> が <tt>null</tt> の場合
	 * @throws IOException 入出力エラーが発生した場合
	 */
	public void writeField(boolean isFinalField, String strValue) throws IOException {
		if (strValue == null)
			throw new NullPointerException("'strValue' argument cannot be null.");
		
		// output Field
		if (!Strings.isNullOrEmpty(strValue) && Strings.contains(strValue, csvDelimiter, '"', '\r', '\n')) {
			// enquote
			csvWriter.append('"');
			int epos = strValue.indexOf('"');
			if (epos >= 0) {
				int len = strValue.length();
				int spos = 0;
				do {
					csvWriter.write(strValue, spos, (epos + 1 - spos));
					csvWriter.append('"');
					spos = epos + 1;
					if (spos < len) {
						epos = strValue.indexOf('"', spos);
						if (epos < 0) {
							csvWriter.write(strValue, spos, (len - spos));
							spos = -1;
						}
					} else {
						spos = -1;
					}
				} while (spos >= 0);
			} else {
				csvWriter.write(strValue);
			}
			csvWriter.append('"');
		} else {
			// no editing
			csvWriter.write(strValue);
		}
		
		// フィールド終端
		if (isFinalField) {
			// レコード終端は改行
			newLine();
		} else {
			// フィールド終端は、デリミタ
			csvWriter.write(csvDelimiter);
			csvFieldNo++;
		}
	}
	
	/**
	 * 現在位置にフィールド文字列を出力する。
	 * <br>
	 * <code>isFinalField</code> が <tt>false</tt> の場合は、フィールド文字列出力後に
	 * フィールド区切り文字を出力し、フィールド位置情報をインクリメントする。
	 * <br>
	 * <code>isFinalField</code> が <tt>true</tt> の場合は、フィールド文字列出力後に
	 * 改行文字を出力し、フィールド位置情報を先頭フィールドへリセットする。
	 * <p>
	 * なお、このメソッドでは、必要に応じてエンクオートする。
	 * 
	 * @param isFinalField 出力したフィールドをレコード終端とする場合は <tt>true</tt>
	 * @param format	書式文字列(参考：{@link java.lang.String#format(String, Object[])})
	 * @param args		書式文字列の書式指示子により参照される引数(参考：{@link java.lang.String#format(String, Object[])})
	 * 
	 * @throws IOException 入出力エラーが発生した場合
	 * 
	 * @see java.lang.String#format(String, Object[])
	 */
	public void writeField(boolean isFinalField, String format, Object...args) throws IOException {
		writeField(isFinalField, String.format(format, args));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 文字列をダブルクオートでエンクオートする。
	 * このメソッドは、フィールド区切り文字、改行文字、ダブルクオートが含まれている場合のみ、
	 * エンクオートした文字列を返す。それ以外の場合は、入力文字列をそのまま返す。
	 * 
	 * @param srcString エンクオートする文字列
	 * @return エンクオートした文字列
	 */
	protected String enquote(final String srcString) {
		String retString = srcString;
		if (!Strings.isNullOrEmpty(srcString) && Strings.contains(srcString, csvDelimiter, '"', '\r', '\n')) {
			StringBuilder sb = new StringBuilder(srcString.length() * 2);
			sb.append('"');
			sb.append(srcString.replaceAll("\"", "\"\""));
			sb.append('"');
			retString = sb.toString();
		}
		return retString;
	}
	
	static protected class BufferedChannelWriter extends Writer
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------

		/** デフォルトバッファサイズ **/
		static protected final int DEFAULT_BYTE_BUFFER_SIZE = 8192;

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		/** 文字セット **/
		final private Charset cs;
		/** エンコーダ **/
		final private CharsetEncoder encoder;
		/** 書き込み可能チャネル **/
		final private WritableByteChannel channel;
		/** バッファ **/
		final private ByteBuffer buffer;
		/** エンコードが完了できなかった文字がある場合は <tt>true</tt> **/
		private boolean haveLeftChar;
		/** エンコードが完了できなかった文字 **/
		private char leftChar;
		/** エンコード未完文字を保持するバッファ **/
		private CharBuffer lcb;
		
		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		protected BufferedChannelWriter(WritableByteChannel ch, CharsetEncoder enc,
										 int bufferSize, boolean useDirectBuffer)
		{
			if (ch == null)
				throw new NullPointerException("WritableByteChannel is null!");
			haveLeftChar = false;
			lcb = null;
			channel = ch;
			cs = enc.charset();
			encoder = enc;
			if (useDirectBuffer) {
				buffer = ByteBuffer.allocateDirect(bufferSize >= 0 ? bufferSize : DEFAULT_BYTE_BUFFER_SIZE);
			} else {
				buffer = ByteBuffer.allocate(bufferSize >= 0 ? bufferSize : DEFAULT_BYTE_BUFFER_SIZE);
			}
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
		// Implement Writer interfaces
		//------------------------------------------------------------
		
		@Override
		public void close() throws IOException {
			synchronized(lock) {
				if (!isOpen())
					return;
				implClose();
			}
		}

		@Override
		public void flush() throws IOException
		{
			synchronized(lock) {
				ensureOpen();
				implFlush();
			}
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			synchronized(lock) {
				ensureOpen();
				if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) {
					throw new IndexOutOfBoundsException();
				} else if (len == 0) {
					return;
				}
				implWrite(cbuf, off, len);
			}
		}

		@Override
		public void write(int c) throws IOException {
			char[] ac = new char[1];
			ac[0] = (char)c;
			write(ac, 0, 1);
		}

		@Override
		public void write(String str, int off, int len) throws IOException {
			if (len < 0) {
				throw new IndexOutOfBoundsException();
			} else {
				char[] ac = new char[len];
				str.getChars(off, off + len, ac, 0);
				write(ac, 0, len);
				return;
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
		 * バッファの内容をチャネルへ出力する。
		 * @throws IOException	入出力エラーが発生した場合
		 */
		private void writeBytes() throws IOException {
			buffer.flip();
			int lim = buffer.limit();
			int pos = buffer.position();
			int len = (pos > lim) ? 0 : (lim - pos);
			if (len > 0) {
				channel.write(buffer);
			}
			buffer.clear();
		}

		/**
		 * 指定された文字バッファのデータとともに、エンコードが完了できなかった文字をチャネルに出力する。
		 * @param charbuf		新しい文字を格納する文字バッファ
		 * @param endOfInput	呼び出し元が指定されたバッファにこれ以上の入力文字を追加する可能性がない場合に限り true
		 * @throws IOException	入出力エラーが発生した場合
		 */
		private void flushLeftChar(CharBuffer charbuf, boolean endOfInput) throws IOException {
			if (!haveLeftChar && !endOfInput)
				return;		// フラッシュの必要がなければ、終了

			// 未完文字を格納するバッファの準備
			if (lcb == null)
				lcb = CharBuffer.allocate(2);
			else
				lcb.clear();
			if (haveLeftChar)
				lcb.put(leftChar);
			if (charbuf != null && charbuf.hasRemaining())
				lcb.put(charbuf.get());
			lcb.flip();

			// 出力
			do {
				if (!lcb.hasRemaining() && !endOfInput)
					break;
				CoderResult result = encoder.encode(lcb, buffer, endOfInput);
				if (result.isUnderflow()) {
					if (lcb.hasRemaining())
						throw new Error();
					break;
				}
				if (result.isOverflow()) {
					writeBytes();
				} else {
					result.throwException();
				}
			} while (true);
			haveLeftChar = false;
		}

		/**
		 * 指定された文字配列の一部を出力する。
		 * @param array		文字配列
		 * @param off		文字の書き込み開始オフセット
		 * @param len		書き込む文字数
		 * @throws IOException
		 */
		void implWrite(char[] array, int off, int len) throws IOException {
			CharBuffer charbuf = CharBuffer.wrap(array, off, len);
			
			if (haveLeftChar)
				flushLeftChar(charbuf, false);
			
			do {
				if (!charbuf.hasRemaining())
					break;
				CoderResult coderresult = encoder.encode(charbuf, buffer, false);
				if (coderresult.isUnderflow()) {
					if (charbuf.remaining() == 1) {
						haveLeftChar = true;
						leftChar = charbuf.get();
					}
					break;
				}
				if (coderresult.isOverflow()) {
					writeBytes();
				} else {
					coderresult.throwException();
				}
			} while (true);
		}

		void implFlush() throws IOException {
			if (buffer.position() > 0)
				writeBytes();
		}

		void implClose() throws IOException {
			flushLeftChar(null, true);
			do {
				CoderResult coderresult = encoder.flush(buffer);
				if (coderresult.isUnderflow())
					break;
				if (coderresult.isOverflow()) {
					writeBytes();
				} else {
					coderresult.throwException();
				}
			} while (true);
			if (buffer.position() > 0)
				writeBytes();
			if (channel != null) {
				channel.close();
			}
		}
	}
}
