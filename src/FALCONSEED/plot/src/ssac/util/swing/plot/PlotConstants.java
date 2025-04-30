/*
 * @(#)PlotConstants.java	2.1.0	2013/07/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

import java.math.BigDecimal;

/**
 * プロットに関する定数。
 * 
 * @version 2.1.0	2013/07/10
 * @since 2.1.0
 */
public class PlotConstants
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final PlotLineStyles[] LINE_VARIOUS = {
		PlotLineStyles.SOLID,
		PlotLineStyles.DOTTED,
		PlotLineStyles.DASHED,
		PlotLineStyles.DOT_DASHED,
		PlotLineStyles.DOT_DOT_DASHED,
	};

	/** 標準の点描画半径(Pixel) **/
	static public final int		DEFAULT_MARK_RADIUS		= 3;

	/** 標準の点描画直径(Pixel) **/
	static public final int		DEFAULT_MARK_DIAMETER	= DEFAULT_MARK_RADIUS * 2;
	
	/** 標準の描画幅(Pixel) **/
	static public final float	DEFAULT_WIDTH = 2f;

	/** 誤差範囲の端点に描画する線分の標準長(Pixel) **/
	static public final int		DEFAULT_ERRORBAR_LEG_LENGTH = 5;
	
	static public final BigDecimal	DOUBLE_MAX_VALUE = new BigDecimal(Double.MAX_VALUE);

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
