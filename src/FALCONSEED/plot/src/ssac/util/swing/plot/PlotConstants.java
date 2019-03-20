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
