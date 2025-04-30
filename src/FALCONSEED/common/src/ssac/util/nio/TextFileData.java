/*
 * @(#)TextFileData.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.File;
import java.nio.charset.Charset;

/**
 * テキストファイル情報を保持する抽象クラス。
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public abstract class TextFileData
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 対象ファイルのパス **/
	private final File		_file;
	/** テキスト・ファイル・エンコーディング **/
	private final Charset	_encoding;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * <code>TextFileData</code> の新しいインスタンスを生成する。
	 * @param file		対象ファイル
	 * @param encoding	テキストエンコーディングに適用する文字セット
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>encoding</em> がエンコード不可能な文字セットの場合
	 */
	public TextFileData(File file, Charset encoding) {
		if (file == null)
			throw new NullPointerException("'File' argument is null.");
		if (encoding == null)
			throw new NullPointerException("'Charset' argument is null.");
		if (!encoding.canEncode())
			throw new IllegalArgumentException("Cannot encode by the specified Character-set : '" + encoding.displayName() + "'");
		this._file = file;
		this._encoding = encoding;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 *  処理対象ファイルのパスを返す。
	 */
	public File getFile() {
		return _file;
	}

	/**
	 * テキスト・ファイル・エンコーディングを返す。
	 */
	public Charset getEncoding() {
		return _encoding;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
