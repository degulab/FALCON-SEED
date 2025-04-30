/*
 * @(#)EtcDestItemChangeHandler.java	3.3.0	2016/05/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.excel2csv.swing;

/**
 * <code>[Excel to CSV]</code> における 1 変換出力設定での変更通知ハンドラ・インタフェース。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public interface EtcDestItemChangeHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	public void editOutToTempChanged(EtcDestItemEditPane itemPane);
	public void editShowDestChanged(EtcDestItemEditPane itemPane);
}
