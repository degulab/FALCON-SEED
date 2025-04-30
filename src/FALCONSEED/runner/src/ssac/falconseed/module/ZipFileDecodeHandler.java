/*
 * @(#)ZipFileDecodeHandler.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.io.File;
import java.util.zip.ZipEntry;

/**
 * <code>ZipFileDecoder</code> クラスの動作時に呼び出されるハンドラ。
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public interface ZipFileDecodeHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ディレクトリの解凍直前に呼び出されるハンドラ。
	 * @param dir			書き込み先の抽象パス
	 * @param entryName		ZIP内のエントリ名('/'区切りの相対パスで、終端が'/')
	 * @return	このディレクトリを解凍する場合は <tt>true</tt>、スキップする場合は <tt>false</tt>
	 */
	public boolean acceptDecodeDirectory(final File dir, final String entryName);
	/**
	 * ディレクトリの解凍完了後に呼び出されるハンドラ。
	 * @param dir			書き込み先の抽象パス
	 * @param entryName		ZIP内のエントリ名('/'区切りの相対パスで、終端が'/')
	 */
	public void completedDecodeDirectory(final File dir, final String entryName);
	
	/**
	 * ファイル(ディレクトリではない)の解凍直前に呼び出されるハンドラ。
	 * @param file			書き込み先の抽象パス
	 * @param entryName		ZIP内のエントリ名('/'区切りの相対パス)
	 * @param zipEntry		ZIPエントリオブジェクト
	 * @return	このファイルを解凍する場合は <tt>true</tt>、スキップする場合は <tt>false</tt>
	 */
	public boolean acceptDecodeFile(final File file, final String entryName, final ZipEntry zipEntry);
	/**
	 * ファイル(ディレクトリではない)の解凍完了後に呼び出されるハンドラ。
	 * @param file			書き込み先の抽象パス
	 * @param entryName		ZIP内のエントリ名('/'区切りの相対パス)
	 * @param zipEntry		ZIPエントリオブジェクト
	 */
	public void completedDecodeFile(final File file, final String entryName, final ZipEntry zipEntry);

	/**
	 * ファイル(ディレクトリではない)の解凍中に
	 * 呼び出されるハンドラ。このメソッドは、ファイル書き込み毎に呼び出される。
	 * @param file			書き込み先の抽象パス
	 * @param entryName		ZIP内のエントリ名('/'区切りの相対パス)
	 * @param zipEntry		ZIPエントリオブジェクト
	 * @param wroteLen		このメソッド呼び出しの直前に書き込まれたバイト数
	 * @param wroteTotalLen	このメソッド呼び出しまでに書き込まれた総バイト数
	 */
	public void onWritingFile(final File file, final String entryName, final ZipEntry zipEntry, final int wroteLen, final long wroteTotalLen);

	/**
	 * ファイル(ディレクトリではない)の解凍をスキップしたときに呼び出されるハンドラ。
	 * @param file			書き込み先の抽象パス
	 * @param entryName		ZIP内のエントリ名('/'区切りの相対パス)
	 * @param zipEntry		ZIPエントリオブジェクト
	 */
	public void onSkipFileEntry(final File file, final String entryName, final ZipEntry zipEntry);
}
