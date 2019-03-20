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
 * @(#)CsvFileContext.java	1.90	2013/08/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.csv.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * 文字列を CSV フィールドに分割するクラス。
 * <p>このクラスは、デコード対象文字列を格納する文字列バッファの内容を順次読み取り、
 * CSV フィールドに分割する。文字列バッファの内容がCSVレコードとして不完全であっても、
 * それまでに読み込んだ位置と解析状態を維持する。次に読み込む文字列バッファの位置は
 * {@link #position()} メソッドで、現在の解析状態は {@link #lastState()} メソッドで
 * 取得できる。文字列バッファには {@link #put(String)} メソッドにより解析する文字列を
 * 追加することができ、文字列を追加した後にデコードを継続することができる。
 * <p>文字列バッファは、このオブジェクトが生成するバッファ以外に、ユーザーが定義した
 * バッファを利用できる。ユーザー定義の文字列バッファは、このクラスのコンストラクタで
 * 指定することが可能であり、ユーザー独自のロジックでバッファに文字列を追加し、
 * 継続的にデコードを実行できる。
 * <p>
 * デコードは {@link #decode()} メソッドの呼び出しで実行する。このメソッドは
 * {@link #position()} メソッドが返す位置から現在のステータスで文字列バッファの
 * 終端まで解析を行う。デコードは {@link #flush()} メソッドの呼び出しによって完了し、
 * 解析状態が {@link #DS_RECORD_END} に設定される。また、デコード中にレコード終端文字
 * (改行文字)に到達した場合、解析状態が {@link #DS_RECORD_END} に設定され、バッファ位置が
 * レコード終端文字(改行文字)の先頭に設定される。<br>
 * 解析完了後は、{@link #fields()} メソッドによってフィールドごとに分割された文字列の
 * 配列を取得することができる。<br>
 * なお、解析状態が {@link #DS_RECORD_END} に設定されると、それ以降の {@link #decode()} 呼び出しは
 * 例外をスローするので、新たにデコードを実行する場合は {@link #reset(boolean)} メソッドを
 * 呼び出して解析状態をリセットしておく必要がある。
 * <p>
 * デコードでは、ユーザーによって設定されたクオート文字とフィールド区切り文字を利用する。<br>
 * フィールド区切り文字は {@link #setDelimiterChar(char)} メソッドによって、
 * クオート文字は {@link #setQuoteChar(char)} メソッドによって設定できる。<br>
 * また、クオートによるエスケープを有効とするか無効とするかも設定できる。クオートによる
 * エスケープとは、クオート文字から次のクオート文字が出現するまでフィールド区切り文字や
 * レコード終端文字をフィールド内の文字として扱う。クオートによるエスケープでは、
 * クオート文字が2つ連続している場合はクオート文字そのものをフィールド文字1文字分として扱う。
 * <p>
 * このクラスインスタンス生成直後では、フィールド区切り文字はカンマ(,)、
 * クオート文字はダブルクオーテーション(&quot;)、クオートによるエスケープは有効となるように
 * 設定されている。
 * <p><b>注：</b>
 * <blockquote>
 * このクラスは同期化されていない。
 * </blockquote>
 * 
 * @version 1.90	2013/08/05
 * @since 1.90
 */
public class CsvFieldDecoder
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** ステータス：レコード終端に到達したことを示す **/
	static public final int DS_RECORD_END		= -1;
	/** ステータス：フィールド文字列の入力待ちであることを示す **/
	static public final int DS_FIELD_TEXT		= 0;
	/** ステータス：クオートによるエスケープ中であること示す **/
	static public final int DS_FIELD_ENQUOTE	= 1;
	/** ステータス：クオートそのもののエスケープが開始されたことを示す **/
	static public final int DS_QUOTE_ESCAPED 	= 2;
	
	static protected final String[] EMPTY_STRING_ARRAY = new String[]{};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** CSV フォーマット **/
	protected final CsvFormat _csvFormat = new CsvFormat();

	/** デコード対象の文字列を格納するバッファ **/
	protected final StringBuilder	_stringBuffer;
	/** CSVフィールドの文字列を格納するバッファ **/
	protected final StringBuilder	_fieldBuffer;
	
	/** 読み込み済みレコードのフィールド **/
	private List<String>			_fieldList;

	/** デコードを開始するレコードバッファの位置 **/
	protected int		_position = 0;
	/** 読み込まれたフィールド文字数(エンクオート文字も含む) **/
	protected long		_readFieldChars = 0L;
	/** デコード進行状態 **/
	protected int		_lastState = DS_FIELD_TEXT;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 新しい文字列バッファで解析を行う、<code>CsvFieldDecoder</code> の新しいインスタンスを生成する。
	 */
	public CsvFieldDecoder() {
		this(new StringBuilder());
	}

	/**
	 * 指定された文字列バッファを利用する、<code>CsvFieldDecoder</code> の新しいインスタンスを生成する。
	 * @param stringBuffer	文字列バッファとして利用する <code>StringBuilder</code> オブジェクト
	 * @throws NullPointerException	<em>stringBuffer</em> が <tt>null</tt> の場合
	 */
	public CsvFieldDecoder(final StringBuilder stringBuffer) {
		if (stringBuffer == null)
			throw new NullPointerException("The specified StringBuilder object is null.");
		this._stringBuffer = stringBuffer;
		this._fieldBuffer  = new StringBuilder();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 現在の CSV フォーマットを返す。
	 * @return <code>CsvFormat</code> オブジェクト
	 */
	public CsvFormat getCsvFormat() {
		return _csvFormat;
	}

	/**
	 * 新しい CSV フォーマットを設定する。
	 * @param newFormat	<code>CsvFormat</code> オブジェクト
	 * @return	設定が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean setCsvFormat(final CsvFormat newFormat) {
		return _csvFormat.setFormat(newFormat);
	}

	/**
	 * 文字列バッファの終端に、指定された文字列を追加する。
	 * このメソッドは、{@link java.lang.StringBuilder#append(String)} メソッドを呼び出す。
	 * @param str	追加する文字列
	 * @return	このオブジェクトそのものを返す
	 */
	public CsvFieldDecoder put(String str) {
		_stringBuffer.append(str);
		return this;
	}

	/**
	 * 現在の解析状態を返す。このメソッドは、次の値を返す。
	 * <ul>
	 * <li>{@link #DS_RECORD_END}：レコード終端に到達したことを示す
	 * <li>{@link #DS_FIELD_TEXT}：フィールド文字列の入力待ちであることを示す
	 * <li>{@link #DS_FIELD_ENQUOTE}：クオートによるエスケープ中であること示す
	 * <li>{@link #DS_QUOTE_ESCAPED}：クオートそのもののエスケープが開始されたことを示す
	 * </ul>
	 * @return	現在の解析状態を示す値を返す。
	 */
	public int lastState() {
		return _lastState;
	}

	/**
	 * 次に読み込む文字列バッファの位置を返す。
	 * @return	文字列バッファのインデックス
	 */
	public int position() {
		return _position;
	}

	/**
	 * 現在の解析結果において、フィールドが存在するかどうかを検証する。
	 * @return	フィールドが存在する場合は <tt>true</tt>、存在しない場合は <tt>false</tt> を返す。
	 */
	public boolean hasField() {
		return (_fieldList!=null && !_fieldList.isEmpty());
	}

	/**
	 * 分割済みのフィールドを格納する配列を返す。
	 * フィールドが存在しない場合は、空の配列を返す。
	 * @return	<code>String</code> オブジェクトの配列
	 */
	public String[] fields() {
		if (hasField()) {
			return _fieldList.toArray(new String[_fieldList.size()]);
		} else {
			return EMPTY_STRING_ARRAY;
		}
	}

	/**
	 * 分割済みフィールドを指定された配列に格納する。
	 * 指定された配列が分割済みフィールド数よりも小さい場合、
	 * 指定された配列の長さまでフィールドを格納する。
	 * @param output	フィールドを格納する配列
	 * @return 配列に格納されたフィールド数
	 */
	public int fields(String[] output) {
		if (hasField()) {
			int len = (output!=null ? output.length : 0);
			if (_fieldList.size() < len) {
				len = _fieldList.size();
			}
			for (int i = 0; i < len; ++i) {
				output[i] = _fieldList.get(i);
			}
			return len;
		} else {
			return 0;
		}
	}

	/**
	 * 分割済みのフィールドを格納する文字列リストを返す。
	 * フィールドが存在しない場合は、要素が空のリストを返す。
	 * @return	<code>String</code> オブジェクトのリスト
	 */
	public List<String> fieldList() {
		if (hasField()) {
			return new ArrayList<String>(_fieldList);
		} else {
			return new ArrayList<String>(0);
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getRecordString() {
		if (_position > 0) {
			if (_stringBuffer.length() > _position)
				return _stringBuffer.substring(0, _position);
			else
				return _stringBuffer.toString();
		} else {
			return "";
		}
	}

	/**
	 * 解析状態をリセットする。
	 * このメソッドは、解析状態をリセットし、文字列バッファの読み込み位置を 0 に設定する。
	 * @param clearStringBuffer	文字列バッファの内容もクリアする場合は <tt>true</tt> を指定する。
	 */
	public void reset(boolean clearStringBuffer) {
		if (_fieldList != null) {
			_fieldList.clear();
		}
		_lastState = DS_FIELD_TEXT;
		_position = 0;
		_readFieldChars = 0;
		_fieldBuffer.setLength(0);
		if (clearStringBuffer) {
			_stringBuffer.setLength(0);
		}
	}

	/**
	 * 解析を終了し、解析状態を {@link #DS_RECORD_END} に設定する。
	 */
	public void flush() {
		if (_readFieldChars > 0) {
			getFieldList().add(_fieldBuffer.toString());
			_readFieldChars = 0;
		}
		_lastState = DS_RECORD_END;
	}

	/**
	 * 文字列バッファの現在の読み込み位置から解析を再開する。
	 * このメソッドが返す値は、{@link #lastState()} と同じ。
	 * @return	解析終了時の解析状態を返す。
	 */
	public int decode() {
		if (_lastState == DS_RECORD_END)
			return _lastState;
		
		if (_csvFormat.isQuoteEscapeEnabled()) {
			return decodeWithQuoteEscape();
		} else {
			return decodeWithoutQuoteEscape();
		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * フィールドを格納するリストを生成する。
	 * @return	新しい <code>List&lt;String&gt;</code> オブジェクト
	 */
	protected List<String> createFieldList() {
		return new ArrayList<String>();
	}

	/**
	 * フィールドを格納するリストを取得する。リストが作成されていない場合は
	 * 作成する。
	 * @return	<code>List&lt;String&gt;</code> オブジェクト
	 */
	protected List<String> getFieldList() {
		if (_fieldList == null) {
			_fieldList = createFieldList();
		}
		return _fieldList;
	}

	/**
	 * クオートによるエスケープを行わない解析を実行する。
	 * @return	解析終了時の解析状態を返す。
	 */
	protected int decodeWithoutQuoteEscape() {
		List<String> fields = getFieldList();
		StringBuilder sbRead = _stringBuffer;
		StringBuilder sbField = _fieldBuffer;
		int limit = sbRead.length();
		int pos   = _position;
		long cnt   = _readFieldChars;
		char cDelim = _csvFormat.getDelimiterChar();
		char c;
		
		for (; pos < limit; ) {
			c = sbRead.charAt(pos);
			if (c == '\r' || c == '\n') {
				// record delimiter
				if (cnt > 0L) {
					// 有効なフィールドを追加
					fields.add(sbField.toString());
					sbField.setLength(0);
				}
				_position = pos;
				_readFieldChars = 0L;
				_lastState = DS_RECORD_END;
				return _lastState;
			}
			else if (c == cDelim) {
				// field delimiter
				fields.add(sbField.toString());
				sbField.setLength(0);
			}
			else {
				// field text
				sbField.append(c);
			}
			++cnt;
			++pos;
		}
		_position = pos;
		_readFieldChars = cnt;
		return _lastState;
	}

	/**
	 * クオートによるエスケープを行う解析を実行する。
	 * @return	解析終了時の解析状態を返す。
	 */
	protected int decodeWithQuoteEscape() {
		List<String> fields = getFieldList();
		StringBuilder sbRead = _stringBuffer;
		StringBuilder sbField = _fieldBuffer;
		int limit = sbRead.length();
		int pos   = _position;
		long cnt   = _readFieldChars;
		int state = _lastState;
		boolean isMultiLine = _csvFormat.isAllowMutiLineField();
		char cQuote = _csvFormat.getQuoteChar();
		char cDelim = _csvFormat.getDelimiterChar();
		char c;
		
		OUTER: for (; pos < limit; ) {
			if (state == DS_FIELD_ENQUOTE) {
				// エンクオート中の文字処理
				if (isMultiLine) {
					// エンクオート中のレコード区切り文字はフィールド内の改行文字とする
					for (;;) {
						c = sbRead.charAt(pos++);
						++cnt;
						if (c == cQuote) {
							// クオートエスケープ開始(もしくはエンクオート終端)
							state = DS_QUOTE_ESCAPED;
							continue OUTER;
						}
						sbField.append(c);
						// 文字列バッファの残りを確認
						if (pos >= limit) {
							// 文字列バッファの終端に到達
							_position = pos;
							_readFieldChars = cnt;
							_lastState = state;
							return state;
						}
					}
				}
				else {
					// エンクオート中でもレコード区切り文字をレコード終端とする
					for (;;) {
						c = sbRead.charAt(pos);
						if (c == cQuote) {
							// クオートエスケープ開始(もしくはエンクオート終端)
							state = DS_QUOTE_ESCAPED;
							++pos;
							++cnt;
							continue OUTER;
						}
						else if (c == '\r' || c == '\n') {
							// record delimiter
							if (cnt > 0) {
								// 有効なフィールドを追加
								fields.add(sbField.toString());
								sbField.setLength(0);
							}
							_position = pos;
							_readFieldChars = 0L;
							_lastState = DS_RECORD_END;
							return _lastState;
						}
						sbField.append(c);
						// 文字列バッファの残りを確認
						++pos;
						++cnt;
						if (pos >= limit) {
							// 文字列バッファの終端に到達
							_position = pos;
							_readFieldChars = cnt;
							_lastState = state;
							return state;
						}
					}
				}
			}
			
			// 次の文字を読み込む
			c = sbRead.charAt(pos);
			
			// クオートエスケープ開始(もしくはエンクオート終端)直後の文字判定
			if (state == DS_QUOTE_ESCAPED) {
				if (c == cQuote) {
					// エスケープされたクオート文字
					sbField.append(c);
					++pos;
					++cnt;
					state = DS_FIELD_ENQUOTE;
					continue OUTER;
				}
				state = DS_FIELD_TEXT;
			}
			
			// 通常のフィールド文字判定ループ
			for (;;) {
				if (c == '\r' || c == '\n') {
					// record delimiter
					if (cnt > 0) {
						// 有効なフィールドを追加
						fields.add(sbField.toString());
						sbField.setLength(0);
					}
					_position = pos;
					_readFieldChars = 0L;
					_lastState = DS_RECORD_END;
					return _lastState;
				}
				else if (c == cQuote && sbField.length() <= 0) {
					// quote char
					// エンクオート開始
					state = DS_FIELD_ENQUOTE;
					++pos;
					++cnt;
					continue OUTER;
				}
				else if (c == cDelim) {
					// field delimiter
					fields.add(sbField.toString());
					sbField.setLength(0);
				}
				else {
					// field text
					sbField.append(c);
				}
				
				// 文字列バッファの残りを確認
				++cnt;
				++pos;
				if (pos < limit) {
					// 次の文字を読み込む
					c = sbRead.charAt(pos);
				} else {
					// 文字列バッファの終端に到達
					break OUTER;
				}
			}
		}
		
		// ステータスを保存
		_position = pos;
		_readFieldChars = cnt;
		_lastState = state;
		return state;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
