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
 * @(#)ArgReferListRenderer.java	3.1.0	2014/05/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ArgReferListRenderer.java	2.0.0	2012/10/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.ModuleArgID;

/**
 * 参照可能引数一覧リストのレンダラー。
 * 
 * @version 3.1.0	2014/05/16
 * @since 2.0.0
 */
public class ArgReferListRenderer extends DefaultListCellRenderer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ArgReferListRenderer() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		ModuleArgType argtype = null;
		String strValue;

		if (value instanceof ModuleArgID) {
			IModuleArgConfig arg = ((ModuleArgID)value).getArgument();
			int argno = arg.getArgNo();
			argtype = arg.getType();
			String desc = arg.getDescription();
			strValue = String.format("($%d)%s %s", argno, (argtype==null ? "" : argtype.toString()), (desc==null ? "" : desc));
		}
		else if (value instanceof IModuleArgConfig) {
			IModuleArgConfig arg = (IModuleArgConfig)value;
			int argno = arg.getArgNo();
			argtype = arg.getType();
			String desc = arg.getDescription();
			strValue = String.format("($%d)%s %s", argno, (argtype==null ? "" : argtype.toString()), (desc==null ? "" : desc));
		}
		else if (value != null) {
			strValue = value.toString();
		}
		else {
			strValue = "";
		}
		
		Component comp = super.getListCellRendererComponent(list, strValue, index, isSelected, cellHasFocus);
		
		if (!isSelected) {
			if (ModuleArgType.IN == argtype) {
				comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_IN);
			}
			else if (ModuleArgType.OUT == argtype) {
				comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_OUT);
			}
			else if (ModuleArgType.STR == argtype) {
				comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_STR);
			}
			else if (ModuleArgType.PUB == argtype) {
				comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_PUB);
			}
			else if (ModuleArgType.SUB == argtype) {
				comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_SUB);
			}
			else {
				comp.setBackground(list.getBackground());
			}
		}
		
		return comp;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
