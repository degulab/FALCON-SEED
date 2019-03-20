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
 * @(#)OutputLogUtil.java	3.0.0	2014/03/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

/**
 * 標準出力、標準エラー出力用ログファイルのユーティリティクラス。
 * 
 * @version 3.0.0	2014/03/11
 * @since 3.0.0
 */
public class OutputLogUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** ログファイルのフィールド区切り文字 **/
	static public final char	FIELD_DELIMITER_CHAR	= ',';
	/** エスケープ用クオート文字 **/
	static public final char	FIELD_ENQUOTE_CHAR		= '"';
	/** ログファイルのレコード区切り文字 **/
	static public final String	RECORD_DELIMITER	= "\r\n";

	/** ログ種別：標準出力 **/
	static public final String	LOGTYPE_STDOUT		= "1";
	/** ログ種別：標準エラー出力 **/
	static public final String	LOGTYPE_STDERR		= "2";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private OutputLogUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
