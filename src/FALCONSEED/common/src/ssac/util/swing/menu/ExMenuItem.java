/*
 * @(#)ExMenuItem.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.menu;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * <code>JMenuItem</code> クラスの機能拡張。
 * 
 * @version 1.00 2008/03/24
 */
public class ExMenuItem extends JMenuItem
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private boolean ignoreToolTip = false;
	private boolean ignoreIcon = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ExMenuItem() {
		super();
	}
	
	public ExMenuItem(Action a) {
		super(a);
	}

	public ExMenuItem(Icon icon) {
		super(icon);
	}

	public ExMenuItem(String text, Icon icon) {
		super(text, icon);
	}

	public ExMenuItem(String text, int mnemonic) {
		super(text, mnemonic);
	}

	public ExMenuItem(String text) {
		super(text);
	}
	
	public ExMenuItem(boolean ignoreToolTip, boolean ignoreIcon) {
		super();
		this.ignoreToolTip = ignoreToolTip;
		this.ignoreIcon = ignoreIcon;
	}
	
	public ExMenuItem(boolean ignoreToolTip, boolean ignoreIcon, Action a) {
		super();
		this.ignoreToolTip = ignoreToolTip;
		this.ignoreIcon = ignoreIcon;
		setAction(a);
	}
	
	public ExMenuItem(MenuItemResource res) {
		super();
		setMenuItemResource(res);
	}
	
	public ExMenuItem(boolean ignoreToolTip, boolean ignoreIcon, MenuItemResource res) {
		super();
		this.ignoreToolTip = ignoreToolTip;
		this.ignoreIcon = ignoreIcon;
		setMenuItemResource(res);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isToolTipIgnored() {
		return this.ignoreToolTip;
	}
	
	public boolean isIconIgnored() {
		return this.ignoreIcon;
	}
	
	public void setIgnoreToolTip(boolean ignore) {
		this.ignoreToolTip = ignore;
	}
	
	public void setIgnoreIcon(boolean ignore) {
		this.ignoreIcon = ignore;
	}
	
	public void setMenuItemResource(MenuItemResource res) {
		if (res != null) {
			setMnemonic(res.getMnemonic());
			setText(res.getName());
			setToolTipText(res.getToolTip());
			setIcon(res.getIcon());
			setActionCommand(res.getCommandKey());
			setAccelerator(res.getAccelerator());
		}
		else {
			// clear all
			setMnemonic('\0');
			setText(null);
			setToolTipText(null);
			setIcon(null);
			setActionCommand(null);
			setAccelerator(null);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	public void setIcon(Icon defaultIcon) {
		super.setIcon(ignoreIcon ? null : defaultIcon);
	}

	@Override
	public void setToolTipText(String text) {
		super.setToolTipText(ignoreToolTip ? null : text);
	}
}
