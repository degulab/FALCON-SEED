/*
 * @(#)ConversionJsonCsvConfigHandler.java	3.4.0	2020/03/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

/**
 * JSON-CSV 変換設定パネルのイベントハンドラ・インタフェース。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public interface ConversionJsonCsvConfigHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * データモデルの選択が変更されたときに呼び出されるイベントハンドラ。
	 * @param selected	新たに選択されたデータモデルを表す文字列(コマンド名)
	 */
	public void onSelectionChangedDataModel(String selected);
}
