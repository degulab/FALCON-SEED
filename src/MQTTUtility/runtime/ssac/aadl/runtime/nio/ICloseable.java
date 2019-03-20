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
 * @(#)ICloseable.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.runtime.nio;

import java.io.IOException;

/**
 * クローズ可能なオブジェクトのインタフェース
 * 
 * @version 1.16	2010/09/27
 * @since 1.16
 */
public interface ICloseable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * ストリームが開いている状態の場合に <tt>true</tt> を返す。
	 */
	public boolean isOpen();

	/**
	 * このストリームを閉じ、関連付けられている全てのシステムリソースを開放する。
	 * ストリームがすでに閉じている場合、このメソッドを呼び出しても何も行われない。
	 * 
	 * @throws IOException	入出力エラーが発生した場合
	 */
	public void close() throws IOException;
}
