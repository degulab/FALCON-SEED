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
