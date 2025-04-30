/*
 * @(#)ITextFileRecordReader.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.File;
import java.io.IOException;

/**
 * レコード単位でテキストファイルから読み込みを行うインタフェース
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public interface ITextFileRecordReader extends ICloseable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * 読み込み対象のファイルを返す。
	 */
	public File getFile();
	/**
	 * 読み込み対象のレコード総数を返す。
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public long getRecordSize() throws IOException;
	/**
	 * 指定された位置のレコードを読み込む。
	 * @param index		読み込むレコード位置を示すインデックス
	 * @return	読み込まれたレコードの文字列
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public String readRecord(long index) throws IOException;
	/**
	 * 指定された位置のレコードを読み込み、<em>output</em> に格納する。
	 * @param output	読み込まれたレコードを格納するバッファ
	 * @param index		読み込むレコード位置を示すインデックス
	 * @return	読み込まれたレコードの文字数
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public int readRecord(StringBuilder output, long index) throws IOException;
}
