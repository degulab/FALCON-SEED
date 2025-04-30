/*
 * @(#)IPlotLineStyleFactory.java	2.1.0	2013/07/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

import java.awt.BasicStroke;

/**
 * プロット時の線描画に使用するオブジェクト生成用のインタフェース。
 * 
 * @version 2.1.0	2013/07/08
 * @since 2.1.0
 */
public interface IPlotLineStyleFactory
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public BasicStroke createLineStroke(PlotLineStyles style, float width);
}
