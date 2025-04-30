/*
 * @(#)DtJsonWriter.java	0.1.0	2022/07/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.json.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * JSON フォーマットでストリームに書き出すための、ライター・インターフェース。
 * 
 * @version 0.1.0
 * @since 0.1.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public interface DtJsonOutputWriter extends Closeable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

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
	 * JSON オブジェクト(マップ)の開始記号を出力する。
	 * 直前までの出力内容との整合性がとれない場合、例外をスローする。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter beginObject() throws IOException;
	
	/**
	 * JSON オブジェクト(マップ)の終端記号を出力する。
	 * 直前までの出力内容との整合性がとれない場合、例外をスローする。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter endObject() throws IOException;
	
	/**
	 * JSON 配列の開始記号を出力する。
	 * 直前までの出力内容との整合性がとれない場合、例外をスローする。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter beginArray() throws IOException;
	
	/**
	 * JSON 配列の終端記号を出力する。
	 * 直前までの出力内容との整合性がとれない場合、例外をスローする。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter endArray() throws IOException;
	
	/**
	 * JSON オブジェクト(マップ)要素のキー(名前)を出力する。
	 * 直前までの出力内容との整合性がとれない場合、例外をスローする。
	 * @param name	出力するキー(名前)
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter writeName(String name) throws IOException;
	
	/**
	 * JSON オブジェクト(マップ)要素の値、もしくは JSON 配列の値として、指定されたオブジェクトの内容を出力する。
	 * 直前までの出力内容との整合性がとれない場合、例外をスローする。
	 * @param value	出力する値
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter writeValue(Object value) throws IOException;
	
	/**
	 * <code>null</code> 値を出力する。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter writeNull() throws IOException;

	/**
	 * 指定されたテキストを、そのまま出力する。
	 * @param text	出力する文字列
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter appendRaw(String text) throws IOException;
	
	/**
	 * 現在までの出力内容をフラッシュする。
	 * @return	このオブジェクトのインスタンス
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public DtJsonOutputWriter flush() throws IOException;
}
