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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Hideaki Yagi (MRI)
 */
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
