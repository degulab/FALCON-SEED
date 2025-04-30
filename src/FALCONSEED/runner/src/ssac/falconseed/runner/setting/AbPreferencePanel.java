/*
 * @(#)AbPreferencePanel.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.setting;

import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * 設定情報を編集するパネルの共通クラス。
 * 
 * @version 1.00	2010/12/20
 */
public abstract class AbPreferencePanel extends JPanel
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
	
	public AbPreferencePanel() {
		super();
	}
	public AbPreferencePanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}
	public AbPreferencePanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}
	public AbPreferencePanel(LayoutManager layout) {
		super(layout);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	abstract public void restoreSettings();
	
	abstract public void storeSettings();

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void setCommonBorder() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
