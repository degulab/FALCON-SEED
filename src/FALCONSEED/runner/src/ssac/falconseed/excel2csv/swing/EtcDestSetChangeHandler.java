/*
 * @(#)EtcDestSetChangeHandler.java	3.3.0	2016/05/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.excel2csv.swing;

/**
 * <code>[Excel to CSV]</code> 変換設定全体での変更通知ハンドラ・インタフェース。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public interface EtcDestSetChangeHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	public void editOutToTempChanged(EtcDestSetEditPane editPane);
	public void editShowDestChanged(EtcDestSetEditPane editPane);
}
