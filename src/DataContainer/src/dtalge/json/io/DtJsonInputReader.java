/*
 * @(#)DtJsonReader.java	0.1.0	2022/07/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.io;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;

import net.arnx.jsonic.JSONEventType;
import net.arnx.jsonic.JSONException;

/**
 * JSON フォーマットでストリームを読み込むための、リーダー・インターフェース。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public interface DtJsonInputReader extends Closeable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String	MSGID_TOO_SMALL_ARGUMENT		= "json.TooSmallArgumentError";			// {0}は{1}以上である必要があります。
	static public final String	MSGID_EMPTY_INPUT_ERROR			= "json.parse.EmptyInputError";			// 入力が空です。
	static public final String  MSGID_CONVERSION_ERROR			= "json.parse.ConversionError";			// {0} は {1} に変換できませんでした: {2}
	static public final String	MSGID_ARRAY_NOT_CLOSED			= "json.parse.ArrayNotClosedError";		// 配列が閉じていません。
	static public final String	MSGID_ILLEGAL_UNICODE_ESCAPE	= "json.parse.IllegalUnicodeEscape";	// 不正なUnicodeエスケープ文字''{0}''が見つかりました。
	static public final String	MSGID_OBJECT_NOT_CLOSED			= "json.parse.ObjectNotClosedError";	// オブジェクトが閉じていません。
	static public final String	MSGID_STRING_NOT_CLOSED			= "json.parse.StringNotClosedError";	// 文字列が閉じていません。
	static public final String	MSGID_UNEXPECTED_CHAR			= "json.parse.UnexpectedChar";			// 予期しない文字''{0}''が見つかりました。
	static public final String	MSGID_UNEXPECTED_LITERAL		= "json.parse.UnrecognizedLiteral";		// 予期しないリテラル''{0}''が見つかりました。

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * ストリームがすでに閉じられているかを判定する。
	 * @return	ストリームが閉じられている場合は <code>true</code>
	 */
	public boolean isClosed();

	/**
	 * ストリームを閉じてリソースを開放する。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException;

	/**
	 * 例外をスローせずに、ストリームを閉じてリソースを開放する。
	 */
	public void closeSilent();
	
	/**
	 * 直前に読み込まれたトークンの、ストリーム先頭からのオフセットを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * 利用するライブラリの仕様により、実際の位置と異なる場合がある。
	 * </blockquote>
	 * @return	ストリーム先頭からのオフセット
	 */
	public long getPosition();

	/**
	 * 直前に読み込まれたトークンの、行番号を返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * 利用するライブラリの仕様により、実際の位置と異なる場合がある。
	 * </blockquote>
	 * @return	行番号
	 */
	public long getLineNo();

	/**
	 * 直前に読み込まれたトークンの、行先頭からのオフセットを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * 利用するライブラリの仕様により、実際の位置と異なる場合がある。
	 * </blockquote>
	 * @return	行先頭からのオフセット
	 */
	public long getColumnNo();
	
	/**
	 * ストリームの次の読み込み位置から、トークンを新たに先読みする。
	 * このメソッドの呼び出しにより、実際のストリームの読み込み位置が進められる。
	 * @return	先読みされたトークン、ストリームの終端に到達している場合は <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonFetchedToken fetchNextToken() throws IOException;
	
	/**
	 * 先読みされたすべてのトークンを消費し、トークン読み込み位置をストリームの読み込み位置に合わせる。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void consumeAllFetchedTokens() throws IOException;
	
	/**
	 * 次の読み込み位置のトークンを取得する。
	 * @return	次のトークン種別を表すオブジェクト
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public JSONEventType nextToken() throws IOException;
	
	/**
	 * 次の読み込み位置のトークンを、Boolean の値として読み込む
	 * @return	取得した Boolean の値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは Boolean の値ではない場合
	 */
	public Boolean nextBooleanValue() throws IOException;
	
	/**
	 * 次の読み込み位置のトークンを、Number の値として読み込む
	 * @return	取得した Number の値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは Number の値ではない場合
	 */
	public BigDecimal nextNumberValue() throws IOException;
		
	/**
	 * 次の読み込み位置のトークンを、String の値として読み込む
	 * @return	取得した String の値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは String の値ではない場合
	 */
	public String nextStringValue() throws IOException;
	
	/**
	 * 次の読み込み位置のトークンを、Boolean、Number、String のいずれかの値として読み込む
	 * @return	取得した Boolean、Number、String のいずれかの値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは Boolean、Number、String のいずれかの値ではない場合
	 */
	public Object nextPrimitiveValue() throws IOException;

	/**
	 * 直前に読み込まれたトークンを取得する。
	 * @return	直前に読み込まれたトークン、一度も読み込まれていないか終端に到達している場合は <code>null</code>
	 */
	public JSONEventType currentToken();
	
	/**
	 * 直前に読み込まれたトークンを、Boolean の値として読み込む
	 * @return	取得した Boolean の値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは Boolean の値ではない場合
	 */
	public Boolean getBooleanValue() throws IOException;
	
	/**
	 * 直前に読み込まれたトークンを、Number の値として読み込む
	 * @return	取得した Number の値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは Number の値ではない場合
	 */
	public BigDecimal getNumberValue() throws IOException;
	
	/**
	 * 直前に読み込まれたトークンを、String の値として読み込む
	 * @return	取得した String の値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは String の値ではない場合
	 */
	public String getStringValue() throws IOException;
	
	/**
	 * 直前に読み込まれたトークンを、Boolean、Number、String のいずれかの値として読み込む
	 * @return	取得した Boolean、Number、String のいずれかの値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは Boolean、Number、String のいずれかの値ではない場合
	 */
	public Object getPrimitiveValue() throws IOException;

	//------------------------------------------------------------
	// For exceptions
	//------------------------------------------------------------
	
	public JSONException createErrorWithMessage(String message);
	
	public JSONException createError(String id, Object...args);
	
	public JSONException createUnexpectedTypeError(JSONEventType real, JSONEventType expected);
	
	public JSONException createUnexpectedPrimitiveTypeError(JSONEventType real);
	
	public JSONException createConversionError(JSONEventType type, Class<?> expected);
	
	public JSONException createUnspecifiedValueError(String key);
	
	public JSONException createIllegalSpecifiedValue(String key, String expected, Object value);
	
	public JSONException createUnclosedObjectError();
	
	public JSONException createUnclosedArrayError();
	
	public JSONException createUnsupportedDataType(String typename);
}
