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
package ssac.aadl.module.swing;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import ssac.aadl.module.ModuleArgType;

/**
 * モジュール引数テーブルの引数属性専用セルレンダラー。
 * この実装では、編集不可能な静的ラベルの表示となる。
 * 
 * @version 3.1.0	2014/05/12
 */
public class ModuleArgTypeComboBoxModel extends AbstractListModel implements ComboBoxModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final ModuleArgType[] _argTypes = {
		ModuleArgType.NONE,
		ModuleArgType.IN,
		ModuleArgType.OUT,
		ModuleArgType.STR,
		ModuleArgType.PUB,
		ModuleArgType.SUB,
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected ModuleArgType	_selectedType;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleArgTypeComboBoxModel() {
		super();
		_selectedType = _argTypes[0];
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement javax.swing.ComboBoxModel interfaces
	//------------------------------------------------------------

	@Override
	public ModuleArgType getSelectedItem() {
		return _selectedType;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		ModuleArgType aType;
		if (anItem instanceof ModuleArgType)
			aType = (ModuleArgType)anItem;
		else
			aType = null;
		
		if (aType == _selectedType)
			return;
		
		// update
		_selectedType = aType;
		fireContentsChanged(this, -1, -1);
	}

	//------------------------------------------------------------
	// Implement javax.swing.ListModel interfaces
	//------------------------------------------------------------

	@Override
	public int getSize() {
		return _argTypes.length;
	}
	
	public int getIndexOf(Object anObject) {
		if (anObject instanceof ModuleArgType) {
			for (int index = 0; index < _argTypes.length; index++) {
				if (_argTypes[index].equals(anObject)) {
					return index;
				}
			}
		}
		return (-1);
	}

	@Override
	public Object getElementAt(int index) {
		if (index >= 0 && index < _argTypes.length) {
			return _argTypes[index];
		} else {
			return null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
