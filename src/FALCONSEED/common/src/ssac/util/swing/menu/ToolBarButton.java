/*
 * @(#)ToolBarButton.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.menu;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * ツールバーに表示するボタンを定義するクラス。
 * 
 * @version 1.00 2008/03/24
 */
public class ToolBarButton extends JButton
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private boolean ignoreMnemonic = true;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ToolBarButton() {
		super();
		this.setHorizontalTextPosition(JButton.CENTER);
		this.setVerticalTextPosition(JButton.BOTTOM);
	}

	public ToolBarButton(Action a) {
		super();
		this.setHorizontalTextPosition(JButton.CENTER);
		this.setVerticalTextPosition(JButton.BOTTOM);
		this.setAction(a);
	}

	public ToolBarButton(Icon icon) {
		super(icon);
		this.setHorizontalTextPosition(JButton.CENTER);
		this.setVerticalTextPosition(JButton.BOTTOM);
	}

	public ToolBarButton(String text, Icon icon) {
		super(text, icon);
		this.setHorizontalTextPosition(JButton.CENTER);
		this.setVerticalTextPosition(JButton.BOTTOM);
	}

	public ToolBarButton(String text) {
		super(text);
		this.setHorizontalTextPosition(JButton.CENTER);
		this.setVerticalTextPosition(JButton.BOTTOM);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isIgnoreMnemonic() {
		return ignoreMnemonic;
	}
	
	public void setIgnoreMnemonic(boolean ignore) {
		ignoreMnemonic = ignore;
	}
	
	@Override
	public void setAction(Action a) {
		// check icon
		Icon icon = (a != null ? (Icon)a.getValue(Action.SMALL_ICON) : null);
		if (icon != null) {
		    this.putClientProperty("hideActionText", Boolean.TRUE);
		}
		
		super.setAction(a);
	}

	@Override
	public void setMnemonic(int mnemonic) {
		if (ignoreMnemonic) {
			super.setMnemonic((int)'\0');
		} else {
			super.setMnemonic(mnemonic);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
