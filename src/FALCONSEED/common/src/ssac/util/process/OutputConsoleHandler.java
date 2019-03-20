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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)OutputConsoleHandler.java	3.0.0	2014/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

/**
 * 標準出力、標準エラー出力のコンソールへの出力要求に応答するための、ハンドラーインタフェース。
 * このインタフェースは、{@link OutputConsoleWriter} オブジェクトに対する書き込みが
 * 発生した後、出力が必要なメッセージがある場合のみ、Swing スレッドから呼び出される。
 * このハンドラを登録した場合、速やかに出力処理を行うこと。
 * 
 * @version 3.0.0	2014/03/24
 * @since 3.0.0
 */
public interface OutputConsoleHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void doOutput(OutputString ostr);
}
