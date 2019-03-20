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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
