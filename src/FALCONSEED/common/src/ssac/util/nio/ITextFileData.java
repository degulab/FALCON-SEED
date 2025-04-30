/*
 * @(#)ITextFileData.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.File;
import java.nio.charset.Charset;

/**
 * テキストファイルの情報を取得するインタフェース
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public interface ITextFileData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトに関連付けられているファイルを返す。
	 */
	public File getFile();

	/**
	 * このオブジェクトのテキスト・エンコーディングとなる文字セットを返す。
	 */
	public Charset getEncoding();
}
