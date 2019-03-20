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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)RunnerResources.java	3.2.1	2015/07/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerResources.java	3.1.0	2014/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerResources.java	2.1.0	2013/08/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerResources.java	2.0.0	2012/09/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerResources.java	1.22	2012/08/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerResources.java	1.20	2012/03/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerResources.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * モジュールランナーのリソース。
 * 
 * @version 3.2.1
 */
public class RunnerResources
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String PATH_RESOURCE = "runner/resource/";
	
	static public final Icon ICON_RUN				= getResourceIcon("images/iconRun.png");
	static public final Icon ICON_GRAPH				= getResourceIcon("images/iconGraph.png");
	static public final Icon ICON_WINDOW			= getResourceIcon("images/iconWindow.png");
	static public final Icon ICON_EDIT_PROPS		= getResourceIcon("images/iconEditProps.png");
	static public final Icon ICON_HISTORY_NEXT		= getResourceIcon("images/iconHistoryNext.png");
	static public final Icon ICON_HISTORY_PREV		= getResourceIcon("images/iconHistoryPrev.png");
	static public final Icon ICON_FILTER			= getResourceIcon("images/iconFilter.png");
	static public final Icon ICON_FILTER_NEW		= getResourceIcon("images/iconFilterNew.png");
	static public final Icon ICON_FILTER_EDIT		= getResourceIcon("images/iconFilterEdit.png");
	static public final Icon ICON_FILTER_DUPLICATE	= getResourceIcon("images/iconFilterDuplicate.png");
	static public final Icon ICON_RERUN_FROM		= getResourceIcon("images/iconRerunFrom.png");
	static public final Icon ICON_RERUN_TO			= getResourceIcon("images/iconRerunTo.png");
	static public final Icon ICON_RERUN_STEP		= getResourceIcon("images/iconRerunStep.png");
	static public final Icon ICON_RERUN_RANGE		= getResourceIcon("images/iconRerunRange.png");
	static public final Icon ICON_RERUNAS_FROM		= getResourceIcon("images/iconModRerunFrom.png");
	static public final Icon ICON_RERUNAS_TO		= getResourceIcon("images/iconModRerunTo.png");
	static public final Icon ICON_RERUNAS_STEP		= getResourceIcon("images/iconModRerunStep.png");
	static public final Icon ICON_RERUNAS_RANGE		= getResourceIcon("images/iconModRerunRange.png");
	static public final Icon ICON_RECORD_OFF		= getResourceIcon("images/iconRecordOff.png");
	static public final Icon ICON_RECORD_ON			= getResourceIcon("images/iconRecordOn.png");
	
	static public final Icon ICON_MACROFILTER		= getResourceIcon("images/iconMacroFilter.png");
	static public final Icon ICON_MACROFILTER_NEW	= getResourceIcon("images/iconMacroFilterNew.png");
	static public final Icon ICON_FILTER_BY_HISTORY	= getResourceIcon("images/iconFilterByHistory.png");
	
	static public final Icon ICON_GENERICFILTER		= getResourceIcon("images/iconGenericFilter.png");
	static public final Icon ICON_GENERICFILTER_NEW	= getResourceIcon("images/iconGenericFilterNew.png");
	
	static public final Icon ICON_ARROW_FIRST		= getResourceIcon("images/iconArrowFirst.png");
	static public final Icon ICON_ARROW_LAST		= getResourceIcon("images/iconArrowLast.png");
	static public final Icon ICON_ARROW_PREV		= getResourceIcon("images/iconArrowPrev.png");
	static public final Icon ICON_ARROW_NEXT		= getResourceIcon("images/iconArrowNext.png");
	
	static public final Icon ICON_BALL_GRAY			= getResourceIcon("images/iconGrayBall.png");
	static public final Icon ICON_BALL_RED			= getResourceIcon("images/iconRedBall.png");
	static public final Icon ICON_BALL_BLUE			= getResourceIcon("images/iconBlueBall.png");
	static public final Icon ICON_BALL_GREEN		= getResourceIcon("images/iconGreenBall.png");
	static public final Icon ICON_BALL_YELLOW		= getResourceIcon("images/iconYellowBall.png");
	static public final Icon ICON_BALL_CYAN			= getResourceIcon("images/iconCyanBall.png");
	static public final Icon ICON_BALL_MAGENTA		= getResourceIcon("images/iconMagentaBall.png");
	static public final Icon ICON_BALL_ORANGE		= getResourceIcon("images/iconOrangeBall.png");
	
	static public final Icon ICON_USERAREA_DISK		= getResourceIcon("images/iconUserArea.png");
	static public final Icon ICON_USERAREA_FILTER	= getResourceIcon("images/iconUserFilterArea.png");
	static public final Icon ICON_USERAREA_DATA		= getResourceIcon("images/iconUserDataArea.png");
	
	static public final Icon ICON_CHART_WINDOW		= getResourceIcon("images/iconChartWindow.png");
	static public final Icon ICON_CHART_LINE		= getResourceIcon("images/iconChartLine.png");
	static public final Icon ICON_CHART_SCATTER		= getResourceIcon("images/iconChartScatter.png");
	
	static public final Icon ICON_MACROCOMMAND_IF	= getResourceIcon("images/iconMacroCmdIf.png");
	static public final Icon ICON_MACROCOMMAND_NEXT	= getResourceIcon("images/iconMacroCmdNext.png");
	static public final Icon ICON_MACROCOMMAND_WAIT	= getResourceIcon("images/iconMacroCmdWait.png");
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected ImageIcon getResourceIcon(String path) {
		URL url = RunnerResources.class.getClassLoader().getResource(path);
		ImageIcon icon;
		if (url != null) {
			icon = new ImageIcon(url);
		} else {
			icon = new ImageIcon(PATH_RESOURCE + path);
		}
		return icon;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
