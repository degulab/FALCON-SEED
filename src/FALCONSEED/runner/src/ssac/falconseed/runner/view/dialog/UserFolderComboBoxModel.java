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
 * @(#)UserFolderComboBoxModel.java	1.13	2011/11/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)UserFolderComboBoxModel.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

import ssac.util.Objects;
import ssac.util.Strings;

/**
 * ユーザー作業領域を選択するためのコンボボックス用モデル。
 * なお、このモデルは、語彙順に昇順ソートされる。
 * 
 * @version 1.13	2011/11/08
 */
public class UserFolderComboBoxModel extends AbstractListModel implements MutableComboBoxModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 選択候補のリスト **/
	private ArrayList<File>	_list;
	/** 選択されているファイル **/
	private File				_selected;
	/** 標準の場所を示す抽象パス **/
	private File				_defFile;
	/** 標準の場所を示す表示名 **/
	private String				_defDisplayPrefix;
	/** 旧標準の場所を示す抽象パス **/
	private File				_oldDefFile;
	/** 旧標準の場所を示す表示名 **/
	private String				_oldDefDisplayPrefix;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public UserFolderComboBoxModel() {
		this(null, null, (Collection<? extends File>)null);
	}
	
	public UserFolderComboBoxModel(File defFile, String defPrefix) {
		this(defFile, defPrefix, (Collection<? extends File>)null);
	}
	
	public UserFolderComboBoxModel(File defFile, String defPrefix, File...files) {
		this(defFile, defPrefix, (files==null ? (Collection<? extends File>)null : Arrays.asList(files)));
	}
	
	public UserFolderComboBoxModel(File defFile, String defPrefix, Collection<? extends File> files) {
		if (files == null) {
			this._list = new ArrayList<File>();
		} else {
			this._list = new ArrayList<File>(files);
		}
		Collections.sort(_list);

		_oldDefFile = null;
		_oldDefDisplayPrefix = null;
		setDefaultFile(defFile, defPrefix);
		
		if (!_list.isEmpty()) {
			_selected = _list.get(0);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public List<File> getInternalList() {
		return _list;
	}

	/**
	 * 標準ファイルとして設定されている抽象パスを返す。
	 * 設定されていない場合は <tt>null</tt> を返す。
	 */
	public File getDefaultFile() {
		return _defFile;
	}

	/**
	 * 標準ファイルの表示名を取得する。
	 * @return	標準ファイルの名前が設定されていればその文字列、
	 * 			設定されていない場合は <tt>null</tt>
	 */
	public String getDefaultDisplayPrefix() {
		return _defDisplayPrefix;
	}
	
	public File getOldDefaultFile() {
		return _oldDefFile;
	}
	
	public String getOldDefaultDisplayPrefix() {
		return _oldDefDisplayPrefix;
	}

	/**
	 * 標準ファイルの設定を除去する。
	 */
	public void removeDefaultFile() {
		if (_defFile != null) {
			_defFile = null;
			_defDisplayPrefix = null;
			fireIntervalRemoved(this, 0, 0);
		}
	}
	
	public void removeOldDefaultFile() {
		if (_oldDefFile != null) {
			_oldDefFile = null;
			_oldDefDisplayPrefix = null;
			if (_defFile != null) {
				fireIntervalRemoved(this, 1, 1);
			} else {
				fireIntervalRemoved(this, 0, 0);
			}
		}
	}

	/**
	 * 標準のファイルを設定する。
	 * @param file	標準のファイルとする抽象パスを指定する。
	 * 				<tt>null</tt> の場合は、標準のファイルを除去する。
	 * @param prefix	標準ファイルの表示名を指定する。
	 */
	public void setDefaultFile(File file, String prefix) {
		if (file != null) {
			if (Strings.isNullOrEmpty(prefix)) {
				prefix = null;
			}
			
			if (_defFile == null) {
				// 標準ファイルの新規追加
				_defFile = file;
				_defDisplayPrefix = prefix;
				fireIntervalAdded(this, 0, 0);
			} else if (!file.equals(_defFile) || Objects.isEqual(prefix, _defDisplayPrefix)) {
				// 標準ファイルの更新
				_defFile = file;
				_defDisplayPrefix = prefix;
				fireContentsChanged(this, 0, 0);
			}
		}
		else {
			// 標準ファイルの除去
			removeDefaultFile();
		}
	}
	
	public void setOldDefaultFile(File file, String prefix) {
		if (file != null) {
			if (Strings.isNullOrEmpty(prefix)) {
				prefix = null;
			}
			
			if (_oldDefFile == null) {
				// 旧標準ファイルの新規追加
				_oldDefFile = file;
				_oldDefDisplayPrefix = prefix;
				int index = indexOfOldDefaultFile();
				fireIntervalAdded(this, index, index);
			} else if (!file.equals(_defFile) || Objects.isEqual(prefix, _defDisplayPrefix)) {
				// 旧標準ファイルの更新
				_defFile = file;
				_defDisplayPrefix = prefix;
				int index = indexOfOldDefaultFile();
				fireContentsChanged(this, index, index);
			}
		}
		else {
			// 旧標準ファイルの除去
			removeOldDefaultFile();
		}
	}

	/**
	 * 指定されたオブジェクトのインデックスを取得する。
	 * @param anItem	オブジェクト
	 * @return	指定されたオブジェクトの位置を示すインデックスを返す。
	 * 			リストに含まれていない場合は (-1) を返す。
	 */
	public int getIndexOf(Object anItem) {
		if (_defFile != null && _defFile.equals(anItem)) {
			return 0;
		}
		else if (_oldDefFile != null && _oldDefFile.equals(anItem)) {
			return (_defFile==null ? 0 : 1);
		}
		else {
			int index = _list.indexOf(anItem);
			if (index >= 0) {
				index += getListIndexOffset();
			}
			return index;
		}
	}

	/**
	 * 全てのオブジェクトをリストから削除する。
	 * 標準ファイルは削除されない。
	 */
	public void removeAllElements() {
		if (!_list.isEmpty()) {
			// リストを削除
			File oldSelected = getSelectedItem();
			int firstIndex = getListIndexOffset();
			int lastIndex = _list.size() - 1 + firstIndex;
			_list.clear();
			fireIntervalRemoved(this, firstIndex, lastIndex);
			if (oldSelected != null) {
				int index = getIndexOf(oldSelected);
				if (index >= 0) {
					setSelectedItem(oldSelected);
				} else {
					setSelectedItem(_defFile==null ? _oldDefFile : _defFile);
				}
			}
		}
	}
	
//	public String getDisplayNameAt(int index, Object value) {
//		String name = null;
//		
//		if (value != null) {
//			String prefix = getDisplayPrefixAt(index);
//			if (prefix != null) {
//				name = prefix + value.toString();
//			} else {
//				name = value.toString();
//			}
//		}
//		
//		return name;
//	}

	//------------------------------------------------------------
	// Implements MutableComboBoxModel interfaces
	//------------------------------------------------------------

	public void addElement(Object anItem) {
		File newPath = (File)anItem;
		int index = getInsertionIndexOfList(newPath);
		if (index < _list.size()) {
			insertElementAt(anItem, index+getListIndexOffset());
		} else {
			_list.add(newPath);
			fireIntervalAdded(this, _list.size()-1+getListIndexOffset(), _list.size()-1+getListIndexOffset());
			if (_list.size()==1 && _selected==null && anItem != null) {
				setSelectedItem(anItem);
			}
		}
	}

	public void insertElementAt(Object anItem, int index) {
		int offset = getListIndexOffset();
		int size = _list.size() + offset;
		
		if (index > size || index < offset) {
		    throw new IndexOutOfBoundsException(
					"Index: "+index+", Offset: "+offset+", Size: "+size);
		}
		
		_list.add(index-offset, (File)anItem);
		fireIntervalAdded(this, index, index);
	}

	public void removeElement(Object anItem) {
		int index = _list.indexOf(anItem);
		if (index != -1) {
			removeElementAt(index + getListIndexOffset());
		}
	}

	public void removeElementAt(int index) {
		if (_list.isEmpty()) {
			return;	// no operation
		}

		int offset = getListIndexOffset();
		if (index >= offset) {
			int li = index - offset;
			if (li < _list.size()) {
				File anItem = _list.get(li);
				if (anItem != null && anItem == _selected) {
					if (li == 0) {
						setSelectedItem(_defFile==null ? _oldDefFile : _defFile);
					} else {
						setSelectedItem(_list.get(li-1));
					}
				}
				_list.remove(li);
				fireIntervalRemoved(this, index, index);
			}
		}
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
		int offset = getListIndexOffset();
		int size = _list.size() + offset;
		
		if (index >= 0) {
			if (index < offset) {
				if (index == 0) {
					return (_defFile==null ? _oldDefFile : _defFile);
				}
				else {
					return _oldDefFile;
				}
			}
			else if (index < size) {
				return _list.get(index-offset);
			}
		}
		
		// index out of bounds
		return null;
	}

	public int getSize() {
		return (_list.size() + getListIndexOffset());
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String getDisplayPrefixAt(int index) {
		if (_defFile != null) {
			if (index == 0) {
				return _defDisplayPrefix;
			}
			if (_oldDefFile != null && index == 1) {
				return _oldDefDisplayPrefix;
			}
		}
		
		if (_oldDefFile != null && index == 0) {
			return _oldDefDisplayPrefix;
		}
		
		return null;
	}
	
	protected int indexOfOldDefaultFile() {
		if (_oldDefFile != null) {
			return (_defFile==null ? 0 : 1);
		}
		else {
			return (-1);
		}
	}
	
	protected int getListIndexOffset() {
		int offset = 0;
		if (_defFile != null)
			offset += 1;
		if (_oldDefFile != null)
			offset += 1;
		return offset;
	}
	
	protected int getListIndexOf(Object anItem) {
		int index = _list.indexOf(anItem);
		if (index < 0) {
			return index;
		} else {
			return (index + getListIndexOffset());
		}
	}
	
	protected int getInsertionIndexOfList(File base) {
		int index = Collections.binarySearch(_list, base);
		if (index < 0) {
			return (Math.abs(index) - 1);
		} else {
			return index;
		}
	}
}
