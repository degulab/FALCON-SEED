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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)StaticModuleArgTypeTableCellRenderer.java	3.1.0	2014/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)StaticModuleArgTypeTableCellRenderer.java	2.0.0	2012/11/02
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing.table;

import java.awt.Dimension;

import javax.swing.SwingConstants;
import javax.swing.UIManager;

import ssac.aadl.module.ModuleArgType;
import ssac.util.swing.SwingTools;

/**
 * モジュール引数テーブルの引数属性専用セルレンダラー。
 * この実装では、編集不可能な静的ラベルの表示となる。
 * 
 * @version 3.1.0	2014/05/12
 */
public class StaticModuleArgTypeTableCellRenderer extends DefaultModuleArgTableCellRenderer
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
	
	public StaticModuleArgTypeTableCellRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
		adjustFixedSize();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void adjustFixedSize() {
		setFont(UIManager.getFont("Table.font"));
		Dimension dmMax = new Dimension();
		//---
		setText(" " + ModuleArgType.IN.toString() + " ");
		SwingTools.expand(dmMax, getPreferredSize());
		setText(" " + ModuleArgType.OUT.toString() + " ");
		SwingTools.expand(dmMax, getPreferredSize());
		setText(" " + ModuleArgType.STR.toString() + " ");
		SwingTools.expand(dmMax, getPreferredSize());
		setText(" " + ModuleArgType.PUB.toString() + " ");
		SwingTools.expand(dmMax, getPreferredSize());
		setText(" " + ModuleArgType.SUB.toString() + " ");
		SwingTools.expand(dmMax, getPreferredSize());
		//--- setup Preferred size
		setPreferredSize(dmMax);
		//--- setup Minimum size
		dmMax.height = getMinimumSize().height;
		setMinimumSize(dmMax);
		//--- setup Maximum size
		dmMax.height = getMaximumSize().height;
		setMaximumSize(dmMax);
		
		setText("");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
