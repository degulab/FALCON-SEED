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
 * @(#)GenericFilterEditModelFactory.java	3.2.0	2015/06/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.util;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;

import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.ToolBarButton;

/**
 * 汎用フィルタに関する Swing ユーティリティ群。
 * 
 * @version 3.2.0
 * @since 3.2.0
 */
public class GenericFilterSwingTools
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private GenericFilterSwingTools() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	static public JLabel createToolBarLabel(String caption) {
		return SwingTools.createToolBarLabel(caption);
	}
	
	static public JButton createToolBarButton(String command, Icon icon, String tooltip) {
		ToolBarButton btn = new ToolBarButton(icon);
		btn.setActionCommand(command);
		btn.setToolTipText(tooltip);
		btn.setRequestFocusEnabled(false);
		return btn;
	}
	
	static public JButton createToolBarButton(String command, String caption, String tooltip) {
		ToolBarButton btn = new ToolBarButton(caption);
		btn.setActionCommand(command);
		btn.setToolTipText(tooltip);
		btn.setRequestFocusEnabled(false);
		return btn;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
