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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)OptionValueTypes.java	3.3.0	2016/05/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command.option;

/**
 * 変換定義における、キーワードを除くオプション値の種類を示す列挙値。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public enum OptionValueTypes
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** キーワード **/
	Keyword,
	/** 整数値 **/
	Integer,
	/** 実数値 **/
	Decimal,
	/** 行位置 **/
	RowCoord,
	/** 列位置 **/
	ColumnCoord,
	/** セル位置 **/
	CellCoord,
	/** 複数の行位置 **/
	MultiRowCoords,
	/** 複数の列位置 **/
	MultiColumnCoords,
	/** 文字列リテラル **/
	String,
	/** 正規表現リテラル **/
	Pattern,
	/** 日付/時刻フォーマット **/
	DateTimePattern,
	/** シート情報 **/
	SheetInfo;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
