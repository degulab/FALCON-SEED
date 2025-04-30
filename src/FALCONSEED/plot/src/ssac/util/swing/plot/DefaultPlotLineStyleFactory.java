/*
 * @(#)DefaultPlotLineStyleFactory.java	2.1.0	2013/07/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.plot;

import java.awt.BasicStroke;

/**
 * プロット時の線描画に使用するオブジェクト生成用のインタフェースの標準実装。
 * 
 * @version 2.1.0	2013/07/10
 * @since 2.1.0
 */
public class DefaultPlotLineStyleFactory implements IPlotLineStyleFactory
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private float[][] DASH_VALUES = {
		// solid : ordinal=0
		{},
		// dotted : ordinal=1
		{ 2.0f, 2.0f },
		// dashed : ordinal=2
		{ 8.0f, 4.0f },
		// dotdashed : ordinal=3
		{ 2.0f, 2.0f, 8.0f, 2.0f },
		// dotdotdashed : ordinal=4
		{ 2.0f, 2.0f, 2.0f, 2.0f, 8.0f, 2.0f },
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public BasicStroke createLineStroke(PlotLineStyles style, float width) {
		switch (style) {
			case SOLID :
				return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0);
			case DOTTED :
				return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, getDashPattern(style), 0);
			case DASHED :
				return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, getDashPattern(style), 0);
			case DOT_DASHED :
				return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, getDashPattern(style), 0);
			case DOT_DOT_DASHED :
				return new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, getDashPattern(style), 0);
			default :
				return null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected float[] getDashPattern(PlotLineStyles style) {
		return DASH_VALUES[style.ordinal()];
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
