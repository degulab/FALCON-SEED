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
 * @(#)AbCommandSheetBlockNode.java	3.3.0	2016/05/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.command;

import ssac.util.excel2csv.poi.CellPosition;
import ssac.util.excel2csv.poi.CellRect;
import ssac.util.excel2csv.poi.SheetInfo;

/**
 * <code>[Excel to CSV]</code> 変換定義における処理対象シートを保持するコマンドブロックノードの共通実装。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public abstract class AbCommandSheetBlockNode extends AbCommandBlockNode
implements CommandSheetBlockNode
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 処理対象シート情報の配列、対象が存在しない場合は空の配列 **/
	protected SheetInfo[]	_targetSheetInfos	= SheetInfo.EMPTY_SHEETINFO_ARRAY;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbCommandSheetBlockNode(CellPosition cmdpos) {
		super(cmdpos);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 処理対象シートが存在するかどうかを判定する。
	 * @return	処理対象シートが存在しない場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isTargetSheetEmpty() {
		return (_targetSheetInfos.length == 0);
	}

	/**
	 * 処理対象シート数を返す。
	 * @return	処理対象シート数
	 */
	public int getTargetSheetCount() {
		return _targetSheetInfos.length;
	}

	/**
	 * 処理対象シートの配列を設定する。
	 * このメソッドは、配列のインスタンスを格納する。
	 * @param targets	このオブジェクトに設定する処理対象シート情報の配列
	 */
	public void setTargetSheets(SheetInfo[] targets) {
		_targetSheetInfos = (targets==null ? SheetInfo.EMPTY_SHEETINFO_ARRAY : targets);
	}

	/**
	 * 処理対象シートの配列を取得する。
	 * @return	処理対象シート情報の配列、処理対象シートが存在しない場合は空の配列
	 */
	public SheetInfo[] getTargetSheets() {
		return _targetSheetInfos;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このコマンドブロックの処理対象領域と、指定されたシートの有効領域との交差領域を返す。
	 * @param absSheetRect	シート左上(0,0) を含む有効領域
	 * @return	交差領域、交差しない場合はこのコマンドの処理対象領域左上を原点とする空の領域
	 */
	protected CellRect getAbsoluteAvailableTargetArea(CellRect absSheetRect) {
		// このコマンドの相対処理対象領域は絶対処理対象領域と同義
		CellRect rect = _relTargetRect.intersection(absSheetRect);
		if (rect.isEmpty())
			rect.setRect(_relTargetRect.getFirstRowIndex(), _relTargetRect.getFirstColumnIndex(), 0, 0);
		return rect;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
