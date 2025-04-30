/*
 * @(#)ITextFileRecordData.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.IOException;

/**
 * テキストファイルのレコード情報を取得するインタフェース
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public interface ITextFileRecordData extends ITextFileData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * テキストファイルのレコード総数を返す。
	 */
	public long getRecordSize();
	/**
	 * テキストファイルのレコードの最大バイト数を返す。
	 */
	public int getMaxRecordByteSize();
	/**
	 * テキストファイルのレコードの最大文字数を返す。
	 */
	public int getMaxRecordCharSize();

	/**
	 * レコード位置を表すファイルポインタを返す。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public IFileRecordPointer getRecordPointer() throws IOException;
	/**
	 * レコード単位で読み込みを行うリーダーを返す。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public ITextFileRecordReader getRecordReader() throws IOException;
	/**
	 * レコード単位で書き込みを行うライタを返す。
	 * @param appending	ファイル終端に追加する場合は <tt>true</tt> を指定する。
	 */
	public ITextFileRecordWriter getRecordWriter(boolean appending);
}
