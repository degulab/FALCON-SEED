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
 * @(#)IRecordPointer.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.nio;

import java.io.IOException;

/**
 * インデックスに対応するレコードの開始と終了位置を表すファイルポインタ。
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public interface IFileRecordPointer extends ICloseable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * レコード数を返す。
	 */
	public long getRecordSize();

	/**
	 * 指定されたインデックスに対応するレコードの開始位置を返す。
	 * 
	 * @param index		レコードのインデックス
	 * @return	レコードの開始位置
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public long getBegin(long index) throws IOException;

	/**
	 * 指定されたインデックスに対応するレコードの終端位置を返す。
	 * このメソッドが返す終了位置はレコード終端であり、次のレコード開始位置ではない。
	 * 
	 * @param index		レコードのインデックス
	 * @return	レコードの終端位置
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public long getEnd(long index) throws IOException;
}
