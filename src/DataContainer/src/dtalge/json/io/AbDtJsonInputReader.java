/*
 * @(#)AbDtJsonInputReader.java	0.1.0	2022/07/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.io;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.LinkedList;

import net.arnx.jsonic.JSONEventType;
import net.arnx.jsonic.JSONException;
import net.arnx.jsonic.JSONReader;
import net.arnx.jsonic.io.InputSource;
import net.arnx.jsonic.parse.JSONParser;
import net.arnx.jsonic.util.LocalCache;

/**
 * JSON フォーマットとしてストリームから読み込むリーダー。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
abstract public class AbDtJsonInputReader implements DtJsonInputReader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** JSON Reader **/
	protected JSONReader	_jsonReader;
	/** 最後に読み込まれた JSON のタイプ。読み込みが一度も行われていないか、ストリームの終端に到達している場合は <code>null</code> **/
	private JSONEventType	_lastType = null;
	/** 直前に取得された先読みトークン、先読みが行われていないか消化されている場合は <code>null</code> **/
	private DtJsonFetchedToken	_curFetchedToken;
	/** 次に取得する先読みトークンのリスト、先読みが行われていないか消化されていない場合は要素が空となる **/
	private LinkedList<DtJsonFetchedToken>	_nextFetchedToken = new LinkedList<>();
	/** 直前に読み込まれたトークンの、ストリーム先頭からのオフセット。ライブラリの仕様上、正確ではない。 **/
	private long	_curPosition;
	/** 直前に読み込まれたトークンの、ストリーム先頭からの行番号。ライブラリの仕様上、正確ではない。 **/
	private long	_curLineNumber;
	/** 直前に読み込まれたトークンの、行内オフセット。ライブラリの仕様上、正確ではない。 **/
	private long	_curColumnNumber;
	
	
	/** JSONIC ライブラリのパーサー **/
	private JSONParser		_jsonicParser;
	/** JSONIC ライブラリの入力ソース **/
	private InputSource		_jsonicInputSource;
	/** JSONIC ライブラリのメッセージキャッシュ **/
	private LocalCache		_jsonicMessageCache;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * ストリームがすでに閉じられているかを判定する。
	 * @return	ストリームが閉じられている場合は <code>true</code>
	 */
	public boolean isClosed() {
		return (_jsonReader == null);
	}

	/**
	 * ストリームを閉じてリソースを開放する。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException
	{
		if (_jsonReader != null) {
			_jsonReader = null;
			_curFetchedToken = null;
			_nextFetchedToken.clear();
			_lastType = null;
			clearJsonicLocatorAndMessages();
		}
	}

	/**
	 * 例外をスローせずに、ストリームを閉じてリソースを開放する。
	 */
	public void closeSilent() {
		try {
			close();
		}
		catch (IOException ignoreEx) {}
	}
	
	/**
	 * 直前に読み込まれたトークンの、ストリーム先頭からのオフセットを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * 利用するライブラリの仕様により、実際の位置と異なる場合がある。
	 * </blockquote>
	 */
	public long getPosition() {
		return _curPosition;
	}

	/**
	 * 直前に読み込まれたトークンの、行番号を返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * 利用するライブラリの仕様により、実際の位置と異なる場合がある。
	 * </blockquote>
	 */
	public long getLineNo() {
		return _curLineNumber;
	}

	/**
	 * 直前に読み込まれたトークンの、行先頭からのオフセットを返す。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * 利用するライブラリの仕様により、実際の位置と異なる場合がある。
	 * </blockquote>
	 */
	public long getColumnNo() {
		return _curColumnNumber;
	}
	
	/**
	 * ストリームの次の読み込み位置から、トークンを新たに先読みする。
	 * このメソッドの呼び出しにより、実際のストリームの読み込み位置が進められる。
	 * @return	先読みされたトークン、ストリームの終端に到達している場合は <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonFetchedToken fetchNextToken() throws IOException
	{
		JSONReader jreader = ensureReader();
		
		// 先読みが存在しない場合、直前のトークンを保存
		if (_curFetchedToken == null && _lastType != null) {
			_curFetchedToken = new DtJsonFetchedToken(_curPosition, _curLineNumber, _curColumnNumber, _lastType, jreader);
		}
		
		// ストリームを進める前の位置情報を保存
		long npos = 0, nline = 0, ncol = 0;
		if (_jsonicInputSource != null) {
			npos  = _jsonicInputSource.getOffset();
			nline = _jsonicInputSource.getLineNumber();
			ncol  = _jsonicInputSource.getColumnNumber();
		}
		
		// 次のトークンを読み込む
		JSONEventType newToken = jreader.next();
		_lastType = newToken;
		if (newToken == null) {
			// ストリーム終端
			return null;
		}
		
		// 読み込まれたトークンを保存
		DtJsonFetchedToken nextToken = new DtJsonFetchedToken(npos, nline, ncol, newToken, jreader);
		_nextFetchedToken.add(nextToken);
		return nextToken;
	}
	
	/**
	 * 先読みされたすべてのトークンを消費し、トークン読み込み位置をストリームの読み込み位置に合わせる。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void consumeAllFetchedTokens() throws IOException
	{
		if (!_nextFetchedToken.isEmpty()) {
			DtJsonFetchedToken lastToken = _nextFetchedToken.getLast();
			_nextFetchedToken.clear();
			_curFetchedToken = null;
			_curPosition = lastToken.position();
			_curLineNumber = lastToken.lineNumber();
			_curColumnNumber = lastToken.columnNumber();
		}
	}
	
	/**
	 * 次の読み込み位置のトークンを取得する。
	 * @return	次のトークン種別を表すオブジェクト
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public JSONEventType nextToken() throws IOException
	{
		JSONReader jreader = ensureReader();
		
		if (!_nextFetchedToken.isEmpty()) {
			// 先読みされたトークンを消費
			_curFetchedToken = _nextFetchedToken.poll();
			_curPosition = _curFetchedToken.position();
			_curLineNumber = _curFetchedToken.lineNumber();
			_curColumnNumber = _curFetchedToken.columnNumber();
			return _curFetchedToken.type();
		}
		else {
			// ストリームから読み出し
			if (_jsonicInputSource != null) {
				_curPosition  = _jsonicInputSource.getOffset();
				_curLineNumber = _jsonicInputSource.getLineNumber();
				_curColumnNumber  = _jsonicInputSource.getColumnNumber();
			}
			_curFetchedToken = null;
			_lastType = jreader.next();
			return _lastType;
		}
	}
	
	/**
	 * 次の読み込み位置のトークンを、Boolean の値として読み込む
	 * @return	取得した Boolean の値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは Boolean の値ではない場合
	 */
	public Boolean nextBooleanValue() throws IOException
	{
		JSONEventType jtype = nextToken();
		if (jtype == JSONEventType.BOOLEAN) {
			return getBooleanValue();
		}
		else if (jtype == JSONEventType.NULL) {
			return null;
		}
		else {
			throw createUnexpectedTypeError(jtype, JSONEventType.BOOLEAN);
		}
	}
	
	/**
	 * 次の読み込み位置のトークンを、Number の値として読み込む
	 * @return	取得した Number の値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは Number の値ではない場合
	 */
	public BigDecimal nextNumberValue() throws IOException
	{
		JSONEventType jtype = nextToken();
		if (jtype == JSONEventType.NUMBER) {
			return getNumberValue();
		}
		else if (jtype == JSONEventType.NULL) {
			return null;
		}
		else {
			throw createUnexpectedTypeError(jtype, JSONEventType.NUMBER);
		}
	}

	/**
	 * 次の読み込み位置のトークンを、String の値として読み込む
	 * @return	取得した String の値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは String の値ではない場合
	 */
	public String nextStringValue() throws IOException
	{
		JSONEventType jtype = nextToken();
		if (jtype == JSONEventType.STRING) {
			return getStringValue();
		}
		else if (jtype == JSONEventType.NULL) {
			return null;
		}
		else {
			throw createUnexpectedTypeError(jtype, JSONEventType.STRING);
		}
	}
	
	/**
	 * 次の読み込み位置のトークンを、Boolean、Number、String のいずれかの値として読み込む
	 * @return	取得した Boolean、Number、String のいずれかの値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは Boolean、Number、String のいずれかの値ではない場合
	 */
	public Object nextPrimitiveValue() throws IOException
	{
		JSONEventType jtype = nextToken();
		if (jtype == JSONEventType.STRING) {
			return getStringValue();
		}
		else if (jtype == JSONEventType.NUMBER) {
			return getNumberValue();
		}
		else if (jtype == JSONEventType.BOOLEAN) {
			return getBooleanValue();
		}
		else if (jtype == JSONEventType.NULL) {
			return null;
		}
		else {
			throw createUnexpectedPrimitiveTypeError(jtype);
		}
	}

	/**
	 * 直前に読み込まれたトークンを取得する。
	 * @return	直前に読み込まれたトークン、一度も読み込まれていないか終端に到達している場合は <code>null</code>
	 */
	public JSONEventType currentToken()
	{
		return (_curFetchedToken==null ? _lastType : _curFetchedToken.type());
	}
	
	/**
	 * 直前に読み込まれたトークンを、Boolean の値として読み込む
	 * @return	取得した Boolean の値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは Boolean の値ではない場合
	 */
	public Boolean getBooleanValue() throws IOException
	{
		JSONReader jreader = ensureReader();
		if (_curFetchedToken == null) {
			return jreader.getBoolean();
		} else {
			return _curFetchedToken.getBoolean();
		}
	}
	
	/**
	 * 直前に読み込まれたトークンを、Number の値として読み込む
	 * @return	取得した Number の値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは Number の値ではない場合
	 */
	public BigDecimal getNumberValue() throws IOException
	{
		JSONReader jreader = ensureReader();
		if (_curFetchedToken == null) {
			return jreader.getNumber();
		} else {
			return _curFetchedToken.getNumber();
		}
	}
	
	/**
	 * 直前に読み込まれたトークンを、String の値として読み込む
	 * @return	取得した String の値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは String の値ではない場合
	 */
	public String getStringValue() throws IOException
	{
		JSONReader jreader = ensureReader();
		if (_curFetchedToken == null) {
			return jreader.getString();
		} else {
			return _curFetchedToken.getString();
		}
	}
	
	/**
	 * 直前に読み込まれたトークンを、Boolean、Number、String のいずれかの値として読み込む
	 * @return	取得した Boolean、Number、String のいずれかの値、もしくは <code>null</code>
	 * @throws IOException	入出力エラーが発生した場合、もしくは Boolean、Number、String のいずれかの値ではない場合
	 */
	public Object getPrimitiveValue() throws IOException
	{
		ensureReader();
		JSONEventType etype = currentToken();
		switch (etype) {
			case STRING:
				return getStringValue();
			case NUMBER:
				return getNumberValue();
			case BOOLEAN:
				return getBooleanValue();
			case NULL:
				return null;
			case START_OBJECT:
				throw createError(MSGID_UNEXPECTED_LITERAL, "JSON object's name");
			case END_OBJECT:
				throw createError(MSGID_UNEXPECTED_CHAR, "}");
			case START_ARRAY:
				throw createError(MSGID_UNEXPECTED_LITERAL, "JSON object's name");
			case END_ARRAY:
				throw createError(MSGID_UNEXPECTED_CHAR, "]");
			default:
				throw createError(MSGID_UNEXPECTED_LITERAL, "JSON object's name");
		}
	}

	//------------------------------------------------------------
	// Exceptions
	//------------------------------------------------------------
	
	public JSONException createErrorWithMessage(String message)
	{
		if (_jsonicInputSource != null) {
			message = "" + _jsonicInputSource.getLineNumber() + ": " + message + "\n" + _jsonicInputSource.toString() + " <- ?";
			return new JSONException(message, JSONException.PARSE_ERROR, _jsonicInputSource.getLineNumber(), _jsonicInputSource.getColumnNumber(), _jsonicInputSource.getOffset());
		}
		else {
			return new JSONException(message, JSONException.PARSE_ERROR);
		}
	}
	
	public JSONException createError(String id, Object...args)
	{
		String message = (_jsonicMessageCache==null ? id : _jsonicMessageCache.getMessage(id, args));
		if (_jsonicInputSource != null) {
			message = "" + _jsonicInputSource.getLineNumber() + ": " + message + "\n" + _jsonicInputSource.toString() + " <- ?";
			return new JSONException(message, JSONException.PARSE_ERROR, _jsonicInputSource.getLineNumber(), _jsonicInputSource.getColumnNumber(), _jsonicInputSource.getOffset());
		}
		else {
			return new JSONException(message, JSONException.PARSE_ERROR);
		}
	}
	
	public JSONException createUnexpectedTypeError(JSONEventType real, JSONEventType expected)
	{
		if (real == null) {
			return createError(MSGID_UNEXPECTED_CHAR, "{End of Input}");
		}
		else if (real == JSONEventType.END_OBJECT) {
			return createError(MSGID_UNEXPECTED_CHAR, "}");
		}
		else if (real == JSONEventType.END_ARRAY) {
			return createError(MSGID_UNEXPECTED_CHAR, "]");
		}
		
		String mesasge = "Unexpected " + getJsonTypeName(expected) + " type: " + getJsonTypeName(real);
		return createErrorWithMessage(mesasge);
	}
	
	public JSONException createUnexpectedPrimitiveTypeError(JSONEventType real)
	{
		if (real == null) {
			return createError(MSGID_UNEXPECTED_CHAR, "{End of Input}");
		}
		else if (real == JSONEventType.END_OBJECT) {
			return createError(MSGID_UNEXPECTED_CHAR, "}");
		}
		else if (real == JSONEventType.END_ARRAY) {
			return createError(MSGID_UNEXPECTED_CHAR, "]");
		}
		
		String mesasge = "Unexpected String, Number or Boolean type: " + getJsonTypeName(real);
		return createErrorWithMessage(mesasge);
	}
	
	public JSONException createConversionError(JSONEventType type, Class<?> expected)
	{
		if (type == null) {
			return createError(MSGID_UNEXPECTED_CHAR, "{End of Input}");
		}
		
		switch (type) {
			case START_OBJECT:
				return createError(MSGID_CONVERSION_ERROR, "JSON object", expected.getSimpleName(), "{");
			case END_OBJECT:
				return createError(MSGID_UNEXPECTED_CHAR, "}");
			case START_ARRAY:
				return createError(MSGID_CONVERSION_ERROR, "JSON array", expected.getSimpleName(), "[");
			case END_ARRAY:
				return createError(MSGID_UNEXPECTED_CHAR, "]");
			case NAME:
				return createError(MSGID_CONVERSION_ERROR, "JSON object's name", expected.getSimpleName(), DtJsonIOHelper.encodeString(getStringIgnoreException())+":");
			case STRING:
				return createError(MSGID_CONVERSION_ERROR, "String", expected.getSimpleName(), DtJsonIOHelper.encodeString(getStringIgnoreException())+":");
			case NUMBER:
				return createError(MSGID_CONVERSION_ERROR, "Number", expected.getSimpleName(), String.valueOf(getNumberIgnoreException()));
			case BOOLEAN:
				return createError(MSGID_CONVERSION_ERROR, "Boolean", expected.getSimpleName(), String.valueOf(getBooleanIgnoreException()));
			case NULL:
				return createError(MSGID_CONVERSION_ERROR, "null", expected.getSimpleName(), "null");
			default:
				return createError(MSGID_CONVERSION_ERROR, "Unknown", expected.getSimpleName(), "Unknown");
		}
	}
	
	public JSONException createUnclosedObjectError() {
		return createError(MSGID_OBJECT_NOT_CLOSED);
	}
	
	public JSONException createUnclosedArrayError() {
		return createError(MSGID_ARRAY_NOT_CLOSED);
	}
	
	public JSONException createUnspecifiedValueError(String key)
	{
		String message = "Value is not specified at Key(" + String.valueOf(key) + ")";
		return createErrorWithMessage(message);
	}
	
	public JSONException createIllegalSpecifiedValue(String key, String expected, Object value)
	{
		String message;
		if (expected != null && !expected.isEmpty()) {
			message = "Illegal value as " + expected + " at Key(" + String.valueOf(key) + ") : " + String.valueOf(value);
		}
		else {
			message = "Illegal value at Key(" + String.valueOf(key) + ") : " + String.valueOf(value);
		}
		return createErrorWithMessage(message);
	}
	
	public JSONException createUnsupportedDataType(String typename)
	{
		String message = "Unsupported data type: " + String.valueOf(typename);
		return createErrorWithMessage(message);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * <code>JSONReader</code> オブジェクトを取得する。
	 * すでに閉じられている場合は、例外をスローする。
	 * @return	<code>JSONReader</code> オブジェクト
	 * @throws IOException	入力ストリームがすでに閉じられている場合
	 */
	protected JSONReader ensureReader() throws IOException
	{
		if (_jsonReader == null) {
			throw new IOException("JSON Reader is already closed.");
		}
		return _jsonReader;
	}
	
	protected void setupJsonicLocatorAndMessages() {
		// get parser
		try {
			Field fld = JSONReader.class.getDeclaredField("parser");
			fld.setAccessible(true);
			_jsonicParser = (JSONParser)fld.get(_jsonReader);
		}
		catch (Throwable ex) {
			_jsonicParser = null;
			_jsonicInputSource = null;
			_jsonicMessageCache = null;
		}
		
		// get input source
		if (_jsonicParser != null) {
			try {
				Field fld = JSONParser.class.getDeclaredField("in");
				fld.setAccessible(true);
				_jsonicInputSource = (InputSource)fld.get(_jsonicParser);
			}
			catch (Throwable ex) {
				_jsonicInputSource = null;
			}
		}
		
		// get message cache
		if (_jsonicParser != null) {
			try {
				Field fld = JSONParser.class.getDeclaredField("cache");
				fld.setAccessible(true);
				_jsonicMessageCache = (LocalCache)fld.get(_jsonicParser);
			}
			catch (Throwable ex) {
				_jsonicMessageCache = null;
			}
		}
	}
	
	protected void clearJsonicLocatorAndMessages() {
		_jsonicParser = null;
		_jsonicInputSource = null;
		_jsonicMessageCache = null;
	}
	
	protected String getStringIgnoreException() {
		try {
			return _jsonReader.getString();
		} catch (Throwable ignoreEx) {
			return null;
		}
	}
	
	protected BigDecimal getNumberIgnoreException() {
		try {
			return _jsonReader.getNumber();
		} catch (Throwable ignoreEx) {
			return null;
		}
	}
	
	protected Boolean getBooleanIgnoreException() {
		try {
			return _jsonReader.getBoolean();
		} catch (Throwable ignoreEx) {
			return null;
		}
	}
	
	protected String getJsonTypeName(JSONEventType type) {
		if (type == null) {
			return "{End of Input}";
		}
		
		switch (type) {
			case START_OBJECT:
			case END_OBJECT:
				return "JSON object";
			case START_ARRAY:
			case END_ARRAY:
				return "JSON array";
			case NAME:
				return "JSON object's key";
			case STRING:
				return "String";
			case NUMBER:
				return "Number";
			case BOOLEAN:
				return "Boolean";
			case NULL:
				return "NULL";
			default:
				return "Unknown";
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
