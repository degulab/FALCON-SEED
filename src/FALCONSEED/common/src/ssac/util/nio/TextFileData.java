/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
