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
/*
 * @(#)WorkspaceComboBoxModel.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

/**
 * ワークスペースの位置を選択するためのコンボボックス用モデル。
 * なお、このモデルは、語彙順に昇順ソートされる。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class WorkspaceComboBoxModel extends AbstractListModel implements MutableComboBoxModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private ArrayList<File>	_list;
	private File				_selected;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public WorkspaceComboBoxModel() {
		_list = new ArrayList<File>();
	}
	
	public WorkspaceComboBoxModel(File...files) {
		_list = new ArrayList<File>(Arrays.asList(files));
		Collections.sort(_list);
		
		if (_list.size() > 0) {
			_selected = _list.get(0);
		}
	}
	
	public WorkspaceComboBoxModel(Collection<? extends File> c) {
		_list = new ArrayList<File>(c);
		Collections.sort(_list);
		
		if (_list.size() > 0) {
			_selected = _list.get(0);
		}
	}

	//------------------------------------------------------------
	// Implements MutableComboBoxModel interfaces
	//------------------------------------------------------------

	public void addElement(Object anItem) {
		File newPath = (File)anItem;
		int index = getInsertionIndex(newPath);
		if (index < _list.size()) {
			insertElementAt(anItem, index);
		} else {
			_list.add(newPath);
			fireIntervalAdded(this, _list.size()-1, _list.size()-1);
			if (_list.size()==1 && _selected==null && anItem != null) {
				setSelectedItem(anItem);
			}
		}
	}

	public void insertElementAt(Object anItem, int index) {
		_list.add(index, (File)anItem);
		fireIntervalAdded(this, index, index);
	}

	public void removeElement(Object anItem) {
		int index = _list.indexOf(anItem);
		if (index != -1) {
			removeElementAt(index);
		}
	}

	public void removeElementAt(int index) {
		if (getElementAt(index) == _selected) {
			if (index == 0) {
				setSelectedItem(getSize() == 1 ? null : getElementAt(index+1));
			} else {
				setSelectedItem(getElementAt(index-1));
			}
		}
		
		_list.remove(index);
		
		fireIntervalRemoved(this, index, index);
	}

	//------------------------------------------------------------
	// Implements ComboBoxModel interfaces
	//------------------------------------------------------------

	public File getSelectedItem() {
		return _selected;
	}

	public void setSelectedItem(Object anItem) {
		if ((_selected != null && !_selected.equals(anItem)) || _selected==null && anItem != null) {
			_selected = (File)anItem;
			fireContentsChanged(this, -1, -1);
		}
	}

	//------------------------------------------------------------
	// Implements ListModel interfaces
	//------------------------------------------------------------

	public File getElementAt(int index) {
		if (index >= 0 && index < _list.size()) {
			return _list.get(index);
		} else {
			return null;
		}
	}

	public int getSize() {
		return _list.size();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getIndexOf(Object anItem) {
		return _list.indexOf(anItem);
	}
	
	public void removeAllElements() {
		if (_list.size() > 0) {
			int firstIndex = 0;
			int lastIndex = _list.size() - 1;
			_list.clear();
			_selected = null;
			fireIntervalRemoved(this, firstIndex, lastIndex);
		} else {
			_selected = null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected int getInsertionIndex(File base) {
		int index = Collections.binarySearch(_list, base);
		if (index < 0) {
			return (Math.abs(index) - 1);
		} else {
			return index;
		}
	}
}
