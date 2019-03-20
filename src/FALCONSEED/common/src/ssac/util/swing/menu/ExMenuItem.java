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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
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
