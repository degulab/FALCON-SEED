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
