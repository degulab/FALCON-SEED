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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Hideaki Yagi (MRI)
 */
/*
 * @(#)PlotLineStyles.java	2.1.0	2013/07/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

/**
 * プロット時の線描画スタイル。
 * 
 * @version 2.1.0	2013/07/10
 * @since 2.1.0
 */
public enum PlotLineStyles
{
	//------------------------------------------------------------
	// Constnts
	//------------------------------------------------------------

	/** 線を描画しない **/
	NONE,
	/** データ系列ごとに異なる線種で描画する **/
	VARIOUS,
	/** 実線 **/
	SOLID,
	/** 点線 **/
	DOTTED,
	/** 破線 **/
	DASHED,
	/** 一点鎖線 **/
	DOT_DASHED,
	/** 二点鎖線 **/
	DOT_DOT_DASHED;

	//------------------------------------------------------------
	// Fields
	//-----------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
