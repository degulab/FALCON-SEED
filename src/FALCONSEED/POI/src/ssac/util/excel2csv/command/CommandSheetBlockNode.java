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
 * @(#)CommandSheetBlockNode.java	3.3.0	2016/05/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command;

import ssac.util.excel2csv.poi.SheetInfo;

/**
 * <code>[Excel to CSV]</code> 変換定義における処理対象シートを保持するコマンドブロックノードのインタフェース。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public interface CommandSheetBlockNode extends CommandBlockNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * 処理対象シートが存在するかどうかを判定する。
	 * @return	処理対象シートが存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isTargetSheetEmpty();

	/**
	 * 処理対象シート数を返す。
	 * @return	処理対象シート数
	 */
	public int getTargetSheetCount();

	/**
	 * 処理対象シートの配列を設定する。
	 * このメソッドは、配列のインスタンスを格納する。
	 * @param targets	このオブジェクトに設定する処理対象シート情報の配列
	 */
	public void setTargetSheets(SheetInfo[] targets);

	/**
	 * 処理対象シートの配列を取得する。
	 * @return	処理対象シート情報の配列、処理対象シートが存在しない場合は空の配列
	 */
	public SheetInfo[] getTargetSheets();
}
