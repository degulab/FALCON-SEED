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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ComboBoxFilterArgOperandModel.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ComboBoxFilterArgOperandModel.java	3.2.0	2015/06/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.arg;

import java.util.List;

import javax.swing.AbstractListModel;

import ssac.falconseed.filter.generic.gui.ComboBoxOperandValueModel;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.falconseed.filter.generic.gui.GenericSchemaValueGroups;

/**
 * 汎用フィルタの引数定義に対応するコンボボックスモデル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class ComboBoxFilterArgOperandModel extends AbstractListModel implements ComboBoxOperandValueModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final List<GenericFilterArgEditModel>	_targetList;
	
	private GenericFilterArgEditModel	_selectedObject;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ComboBoxFilterArgOperandModel(GenericFilterEditModel editModel) {
		if (editModel == null)
			throw new NullPointerException();
		_targetList = editModel.getEditableFilterArgsList();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement ComboBoxOperandValueModel
	//------------------------------------------------------------
	
	@Override
	public GenericSchemaValueGroups getValueGroup() {
		return GenericSchemaValueGroups.F_ARG;
	}
	
	@Override
	public void refresh() {
		GenericFilterArgEditModel target = _selectedObject;
		if (target != null && !_targetList.contains(target)) {
			_selectedObject = null;
		}
		fireContentsChanged(this, -1, -1);
	}

	//------------------------------------------------------------
	// Implement AbstractListModel interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement ComboBoxModel interfaces
	//------------------------------------------------------------

	@Override
	public void setSelectedItem(Object anItem) {
		if (anItem != null && !_targetList.contains(anItem)) {
			// invalid item
			anItem = null;
		}
		
		if (anItem == _selectedObject)
			return;
		
		_selectedObject = (GenericFilterArgEditModel)anItem;
		fireContentsChanged(this, -1, -1);
	}

	@Override
	public GenericFilterArgEditModel getSelectedItem() {
		return _selectedObject;
	}

	//------------------------------------------------------------
	// Implement ListModel interfaces
	//------------------------------------------------------------

	@Override
	public int getSize() {
		return _targetList.size();
	}

	@Override
	public Object getElementAt(int index) {
		return _targetList.get(index);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
