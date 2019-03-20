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
