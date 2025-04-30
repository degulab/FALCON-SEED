/*
 * @(#)DtJsonFetchedToken.java	0.1.0	2022/07/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.io;

import java.io.IOException;
import java.math.BigDecimal;

import net.arnx.jsonic.JSONEventType;
import net.arnx.jsonic.JSONReader;

/**
 * 先読みされた JSON トークン。
 * このオブジェクトは、不変である。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class DtJsonFetchedToken
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private long	_position;
	private long	_line_no;
	private long	_column_no;
	
	private JSONEventType	_tokentype;
	private Object			_value;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたパラメーターで、新しいインスタンスを生成する。
	 * @param pos	このトークン読み込み直前の、ストリーム先頭からのオフセット
	 * @param lno	このトークン読み込み直前の、ストリーム先頭からの行番号
	 * @param cno	このトークン読み込み直前の、行先頭からのオフセット
	 * @param tokentype	先読みしたトークンの種類
	 * @param reader	先読みしたトークンの値を取得するためのリーダー
	 * @throws NullPointerException	<em>tokentype</em> が <code>null</code> ではなく、<em>reader</em> が <code>null</code> の場合
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonFetchedToken(long pos, long lno, long cno, JSONEventType tokentype, JSONReader reader) throws IOException
	{
		_position  = pos;
		_line_no   = lno;
		_column_no = cno;
		
		_tokentype = tokentype;
		switch (tokentype) {
			case STRING:
			case NAME:
				_value = reader.getString();
				break;
			case NUMBER:
				_value = reader.getNumber();
				break;
			case BOOLEAN:
				_value = reader.getBoolean();
				break;
			default:
				_value = null;
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public long position() {
		return _position;
	}
	
	public long lineNumber() {
		return _line_no;
	}
	
	public long columnNumber() {
		return _column_no;
	}
	
	public JSONEventType type() {
		return _tokentype;
	}
	
	public String getString() {
		return (String)_value;
	}
	
	public BigDecimal getNumber() {
		return (BigDecimal)_value;
	}
	
	public Boolean getBoolean() {
		return (Boolean)_value;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
