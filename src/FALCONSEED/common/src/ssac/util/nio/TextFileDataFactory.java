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
 * @(#)TextFileDataFactory.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * <code>TextFileData</code> オブジェクトを生成するファクトリクラスの抽象クラス。
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public abstract class TextFileDataFactory
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** 読み込みバッファの標準バイト数 **/
	static protected final int DEFAULT_BUFFER_SIZE = 1024*8;
	/** 読み込みバッファの最小バイト数 **/
	static protected final int MIN_BUFFER_SIZE = 1024;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** バッファのバイト数 **/
	private	int		_bufSize;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public TextFileDataFactory() {
		this._bufSize  = DEFAULT_BUFFER_SIZE;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ファイル読み込み時に使用するバッファのバイト数を返す。
	 * @return	バッファのバイト数
	 */
	public int getByteBufferSize() {
		return _bufSize;
	}

	/**
	 * ファイル読み込み時に使用するバッファのバイト数を設定する。
	 * 設定するバイト数は、最小バイト数(1024)より小さい場合は、
	 * 最小バイト数に設定される。
	 * @param bufferSize	新しいバッファのバイト数
	 */
	public void setByteBufferSize(int bufferSize) {
		this._bufSize = Math.max(MIN_BUFFER_SIZE, bufferSize);
	}

	/**
	 * 指定されたファイルのファイルデータを生成する。
	 * このメソッドが生成するファイルデータのインデックスファイルは、
	 * テンポラリ領域に自動的に作成される。
	 * @param targetFile	対象のファイル
	 * @param encoding		テキストエンコーディング
	 * @return	生成されたファイルデータ
	 * @throws NullPointerException	<em>targetFile</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>encoding</em> がエンコード可能な文字セットではない場合
	 * @throws IOException	<em>targetFile</em> に指定されたファイルが存在しない場合、
	 * 						もしくは入出力エラーが発生した場合
	 */
	public TextFileData newFileData(File targetFile, Charset encoding) throws IOException
	{
		File indexFile = File.createTempFile("tmp", ".dat");
		indexFile.deleteOnExit();
		return newFileData(indexFile, targetFile, encoding);
	}

	/**
	 * 指定されたファイルのファイルデータを生成する。
	 * @param indexFile		インデックスの保存先ファイル
	 * @param targetFile	対象のファイル
	 * @param encoding		テキストエンコーディング
	 * @return	生成されたファイルデータ
	 * @throws NullPointerException	<em>indexFile</em> もしくは <em>targetFile</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>encoding</em> がエンコード可能な文字セットではない場合
	 * @throws IOException	<em>targetFile</em> に指定されたファイルが存在しない場合、
	 * 						もしくは入出力エラーが発生した場合
	 */
	abstract public TextFileData newFileData(File indexFile, File targetFile, Charset encoding) throws IOException;

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
