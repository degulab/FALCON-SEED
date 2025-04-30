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
