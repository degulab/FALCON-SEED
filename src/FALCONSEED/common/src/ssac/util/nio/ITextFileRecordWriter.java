/*
 * @(#)ITextFileRecordWriter.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.File;
import java.io.IOException;

/**
 * レコード単位でテキストファイルへ書き込みを行うインタフェース
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public interface ITextFileRecordWriter extends ICloseable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * 書き込み対象のファイルを返す。
	 */
	public File getFile();
	/**
	 * ストリームの終端に指定された文字列をレコードとして出力する。
	 * @param record	出力するレコードを表す文字列
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void writeRecord(String record) throws IOException;
}
