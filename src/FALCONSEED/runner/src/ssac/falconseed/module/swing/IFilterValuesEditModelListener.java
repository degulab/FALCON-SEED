/*
 * @(#)IFilterValuesEditModelListener.java	2.0.0	2012/10/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.util.EventListener;


/**
 * フィルタ実行時引数値を保持するデータモデル用のイベントリスナー。
 * 
 * @version 2.0.0	2012/10/11
 * @since 2.0.0
 */
public interface IFilterValuesEditModelListener extends EventListener
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	public void dataChanged(FilterValuesEditModelEvent e);
}
