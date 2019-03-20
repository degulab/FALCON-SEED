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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)FileList.java	2.0.0	2012/10/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 * @(#)FileList.java	1.10	2008/12/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import ssac.aadl.editor.EditorMessages;
import ssac.util.io.ExtensionFileFilter;
import ssac.util.swing.list.FileListElement;
import ssac.util.swing.list.FileListModel;
import ssac.util.swing.list.IListControlHandler;
import ssac.util.swing.list.ListController;

/**
 * 複数のファイルパスを保持するリスト・コンポーネント。
 * 
 * @version 2.0.0	2012/10/19
 */
public class FileList extends JList implements IListControlHandler
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private JFileChooser	chooser;

	/**
	 * <code>{@link ListController}</code> インスタンス
	 */
	public final ListController controller = new ListController(this);

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	public FileList() {
		super(new FileListModel());
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setCellRenderer(new FileListCellRenderer());
		
		ListSelectionListener lsl = new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent lse) {
				onSelectionChanged(lse);
			}
		};
		this.addListSelectionListener(lsl);
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * <code>FileList</code> コンポーネントによって表示される項目の
	 * リストを保持するデータモデルを取得する。
	 * 
	 * @return 表示されるリストの項目を提供する <code>{@link FileListModel}</code>
	 */
	@Override
	public FileListModel getModel() {
		return (FileListModel)super.getModel();
	}

	/**
	 * リスト内容を表すモデルを設定する。
	 * 
	 * @param model 表示されるリストの項目を提供する <code>{@link FileListModel}</code>
	 * 
	 * @throws IllegalArgumentException <code>model</code> が <code>null</code> の場合、
	 * 			もしくは、<code>model</code> のインスタンスが <code>{@link FileListModel}</code> インスタンス
	 * 			ではない場合にスローされる。
	 */
	@Override
	public final void setModel(ListModel model) {
        if (model == null) {
            throw new IllegalArgumentException("model must be non null");
        }
		if (model instanceof FileListModel) {
			super.setModel(model);
		} else {
			throw new IllegalArgumentException("model must be non FileListModel class instance.");
		}
	}

	/**
	 * リストに表示するツールヒントとなる文字列を取得する。
	 */
	@Override
	public String getToolTipText(MouseEvent event) {
		String strText = null;
		int index = locationToIndex(event.getPoint());
		Rectangle rc = getCellBounds(index, index);
		if (rc != null && rc.contains(event.getPoint())) {
			FileListElement elem = (FileListElement)getModel().getElementAt(index);
			if (elem != null) {
				strText = elem.getFile().getAbsolutePath();
			}
		}

		return strText;
		//return super.getToolTipText(event);
	}

	/**
	 * リスト・コンポーネントに関連付けられたボタンの状態を更新する。
	 */
	public void updateButtons() {
		boolean enableAdd		= false;
		boolean enableEdit		= false;
		boolean enableDelete	= false;
		boolean enableUp		= false;
		boolean enableDown		= false;
		
		if (isEnabled()) {
			enableAdd = true;
			int selected = getSelectedIndex();
			if (selected >= 0) {
				enableEdit = true;
				enableDelete = true;
				if ((selected - 1) >= 0)
					enableUp = true;
				if ((selected + 1) < getModel().getSize())
					enableDown = true;
			}
		}
		
		controller.setAddButtonEnabled(enableAdd);
		controller.setEditButtonEnabled(enableEdit);
		controller.setDeleteButtonEnabled(enableDelete);
		controller.setUpButtonEnabled(enableUp);
		controller.setDownButtonEnabled(enableDown);
	}
	
	//------------------------------------------------------------
	// implement IListControlHandler interfaces
	//------------------------------------------------------------

	/**
	 * [追加]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonAdd(ActionEvent ae) {
		File sf = chooseFile(null);
		if (sf != null) {
			getModel().addElement(new FileListElement(sf));
			updateButtons();
		}
	}
	
	/**
	 * [編集]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonEdit(ActionEvent ae) {
		// get selected file
		int selected = getSelectedIndex();
		if (selected < 0)
			return;		// no selection
		FileListElement elem = (FileListElement)getModel().get(selected);
		
		// choose file
		File sf = chooseFile(elem.getFile());
		if (sf != null) {
			getModel().setElementAt(new FileListElement(sf), selected);
		}
	}
	
	/**
	 * [削除]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonDelete(ActionEvent ae) {
		int selected = getSelectedIndex();
		if (selected >= 0) {
			getModel().removeElementAt(selected);
			int maxidx = getModel().getSize() - 1;
			if (selected > maxidx)
				selected = maxidx;
			if (selected >= 0)
				setSelectedIndex(selected);
			updateButtons();
		}
	}
	
	/**
	 * [クリア]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonClear(ActionEvent ae) {
		// No Entry
	}
	
	/**
	 * [上へ移動]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonUp(ActionEvent ae) {
		int selected = getSelectedIndex();
		if (selected > 0) {
			int prev = selected - 1;
			FileListModel model = getModel();
			Object obj = model.getElementAt(prev);
			setValueIsAdjusting(true);
			model.setElementAt(model.getElementAt(selected), prev);
			model.setElementAt(obj, selected);
			setSelectedIndex(prev);
			setValueIsAdjusting(false);
			updateButtons();
		}
	}
	
	/**
	 * [下へ移動]ボタン押下時に呼び出されるメソッド
	 */
	public void onButtonDown(ActionEvent ae) {
		int selected = getSelectedIndex();
		int next = selected + 1;
		FileListModel model = getModel();
		if (selected >= 0 && next < model.getSize()) {
			Object obj = model.getElementAt(next);
			setValueIsAdjusting(true);
			model.setElementAt(model.getElementAt(selected), next);
			model.setElementAt(obj, selected);
			setSelectedIndex(next);
			setValueIsAdjusting(false);
			updateButtons();
		}
	}

	//------------------------------------------------------------
	// Event handlers
	//------------------------------------------------------------
	
	protected void onSelectionChanged(ListSelectionEvent lse) {
		if (!lse.getValueIsAdjusting()) {
			updateButtons();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected File chooseFile(File initFile) {
		// choose file
		JFileChooser fc = getFileChooser();
		
		// init already selected
		if (initFile != null) {
			fc.setSelectedFile(initFile);
		}
		
		// show dialog
		int ret = fc.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION) {
			return null;		// canceled
		}
		
		// return selected file
		return fc.getSelectedFile();
	}
	
	static private JFileChooser getFileChooser() {
		// exist instance?
		if (chooser != null)
			return chooser;
		
		// create File filter
		FileFilter filterJar = new ExtensionFileFilter(EditorMessages.getInstance().descExtJar, EditorMessages.getInstance().extJar);
		
		// create instance
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(false);
		fc.setFileFilter(filterJar);
		
		// initialize location
		File curDir;
		String str = System.getProperty("user.home");
		if (str != null) {
			curDir = new File(str);
		} else {
			curDir = new File("");
		}
		fc.setCurrentDirectory(curDir);
		
		// save instance
		chooser = fc;
		return chooser;
	}

	//------------------------------------------------------------
	// Internal FileListCellRenderer class
	//------------------------------------------------------------

	static class FileListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(
				JList list,
				Object value,            // value to display
				int index,               // cell index
				boolean isSelected,      // is the cell selected
				boolean cellHasFocus)    // the list and the cell have the focus
		{
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			FontMetrics fm = getFontMetrics(getFont());
			String strText = getText();
			int w = fm.stringWidth(strText);
			int mw = list.getVisibleRect().width;
			if (mw > 0 && w > mw) {
				int sw = (fm.getMaxAdvance() > 0 ? fm.getMaxAdvance() : fm.stringWidth("W")) * 3;
				int mhw = (mw - sw) / 2;
				int bi, ei, tcw;
				//--- front
				for (tcw = 0, bi = 0; bi < strText.length(); bi++) {
					tcw += fm.charWidth(strText.charAt(bi));
					if (tcw > mhw) {
						--bi;
						break;
					}
				}
				//--- end
				for (tcw = 0, ei = strText.length() - 1; ei >= 0; ei--) {
					tcw += fm.charWidth(strText.charAt(ei));
					if (tcw > mhw) {
						++ei;
						break;
					}
				}
				//--- make
				String bstr = strText.substring(0, bi);
				String estr = strText.substring(ei);
				String cstr = bstr + "..." + estr;
				setText(cstr);
			}

			return this;
		}
	}
}
