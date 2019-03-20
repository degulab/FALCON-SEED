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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ZipFileEncodeHandler.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.io.File;

/**
 * <code>ZipFileEncoder</code> クラスの動作時に呼び出されるハンドラ。
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public interface ZipFileEncodeHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * ディレクトリの ZIP ストリームへの書き込み直前に呼び出されるハンドラ。
	 * なお、ZIPへ登録する際のZIPエントリ名は、<em>relativePath</em> の終端に
	 * '/' を付加したものとなる。
	 * @param file		読込元の抽象パス
	 * @param relativePath	ZIP内のエントリ名とする基準パスからの相対パス('/'区切り)
	 * @return	このディレクトリをZIPに登録する場合は <tt>true</tt>、スキップする場合は <tt>false</tt>
	 */
	public boolean acceptEncodeDirectory(final File file, final String relativePath);
	/**
	 * ディレクトリの ZIP ストリームへの書き込み完了後に呼び出されるハンドラ。
	 * なお、ZIPへ登録する際のZIPエントリ名は、<em>relativePath</em> の終端に
	 * '/' を付加したものとなる。
	 * @param file		読込元の抽象パス
	 * @param relativePath	ZIP内のエントリ名とする基準パスからの相対パス('/'区切り)
	 */
	public void completedEncodeDirectory(final File file, final String relativePath);
	
	/**
	 * ファイル(ディレクトリではない)の ZIP ストリームへの書き込み直前に呼び出されるハンドラ。
	 * @param file		読込元の抽象パス
	 * @param relativePath	ZIP内のエントリ名とする基準パスからの相対パス('/'区切り)
	 * @return	このファイルをZIPに登録する場合は <tt>true</tt>、スキップする場合は <tt>false</tt>
	 */
	public boolean acceptEncodeFile(final File file, final String relativePath);
	/**
	 * ファイル(ディレクトリではない)の ZIP ストリームへの書き込み完了後に呼び出されるハンドラ。
	 * @param file		読込元の抽象パス
	 * @param relativePath	ZIP内のエントリ名とする基準パスからの相対パス('/'区切り)
	 */
	public void completedEncodeFile(final File file, final String relativePath);

	/**
	 * ファイル(ディレクトリではない)の ZIP ストリームへの書き込み中に
	 * 呼び出されるハンドラ。このメソッドは、書き込み毎に呼び出される。
	 * @param file			読込元の抽象パス
	 * @param relativePath	ZIP内のエントリ名とする基準パスからの相対パス('/'区切り)
	 * @param wroteLen		このメソッド呼び出しの直前に書き込まれたバイト数
	 * @param wroteTotalLen	このメソッド呼び出しまでに書き込まれた総バイト数
	 */
	public void onWritingFile(final File file, final String relativePath, final int wroteLen, final long wroteTotalLen);
}
