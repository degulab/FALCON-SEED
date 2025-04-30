/*
 * @(#)AbDtJsonOutputWriter.java	0.1.0	2022/07/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.io;

import java.io.IOException;

import net.arnx.jsonic.JSONWriter;

/**
 * JSON フォーマットで出力するライター。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
abstract public class AbDtJsonOutputWriter implements DtJsonOutputWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** JSON Writer **/
	protected JSONWriter	_jsonWriter;

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
		return (_jsonWriter == null);
	}

	/**
	 * ストリームを閉じてリソースを開放する。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException
	{
		if (_jsonWriter != null) {
			_jsonWriter = null;
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
	 * JSON オブジェクト(マップ)の開始記号を出力する。
	 * 直前までの出力内容との整合性がとれない場合、例外をスローする。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter beginObject() throws IOException
	{
		ensureWriter().beginObject();
		return this;
	}
	
	/**
	 * JSON オブジェクト(マップ)の終端記号を出力する。
	 * 直前までの出力内容との整合性がとれない場合、例外をスローする。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter endObject() throws IOException
	{
		ensureWriter().endObject();
		return this;
	}
	
	/**
	 * JSON 配列の開始記号を出力する。
	 * 直前までの出力内容との整合性がとれない場合、例外をスローする。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter beginArray() throws IOException
	{
		ensureWriter().beginArray();
		return this;
	}
	
	/**
	 * JSON 配列の終端記号を出力する。
	 * 直前までの出力内容との整合性がとれない場合、例外をスローする。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter endArray() throws IOException
	{
		ensureWriter().endArray();
		return this;
	}
	
	/**
	 * JSON オブジェクト(マップ)要素のキー(名前)を出力する。
	 * 直前までの出力内容との整合性がとれない場合、例外をスローする。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter writeName(String name) throws IOException
	{
		ensureWriter().name(name);
		return this;
	}
	
	/**
	 * JSON オブジェクト(マップ)要素の値、もしくは JSON 配列の値として、指定されたオブジェクトの内容を出力する。
	 * 直前までの出力内容との整合性がとれない場合、例外をスローする。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter writeValue(Object value) throws IOException
	{
		ensureWriter().value(value);
		return this;
	}
	
	/**
	 * <code>null</code> 値を出力する。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter writeNull() throws IOException
	{
		ensureWriter().value(null);
		return this;
	}

	/**
	 * 指定されたテキストを、そのまま出力する。
	 * @param text	出力する文字列
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter appendRaw(String text) throws IOException
	{
		ensureWriter().append(text);
		return this;
	}
	
	/**
	 * 現在までの出力内容をフラッシュする。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter flush() throws IOException
	{
		ensureWriter().flush();
		return this;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * <code>JSONWriter</code> オブジェクトを取得する。
	 * すでに閉じられている場合は、例外をスローする。
	 * @return	<code>JSONWriter</code> オブジェクト
	 * @throws IOException	出力ストリームがすでに閉じられている場合
	 */
	protected JSONWriter ensureWriter() throws IOException
	{
		if (_jsonWriter == null) {
			throw new IOException("JSON Writer is already closed.");
		}
		return _jsonWriter;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
