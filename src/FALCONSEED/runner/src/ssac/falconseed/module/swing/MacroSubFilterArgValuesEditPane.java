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
 * @(#)MacroSubFilterArgValuesEditPane.java	2.0.0	2012/10/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.module.swing.table.MacroSubFilterArgValueTablePane;
import ssac.util.io.VirtualFile;
import ssac.util.swing.MenuToggleButton;

/**
 * マクロフィルタ専用のサブフィルタ実行時引数値編集パネル。
 * この実行時引数値は、マクロフィルタに含めるサブフィルタの引数値として設定する。
 * 
 * @version 2.0.0	2012/10/12
 * @since 2.0.0
 */
public class MacroSubFilterArgValuesEditPane extends AbFilterArgValuesEditPane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** エディタで閲覧中のファイル **/
	private VirtualFile		_vfViewingOnEditor;
	
	/** 引数の編集ボタン **/
	private MenuToggleButton	_btnArgEdit;
	/** 引数の削除ボタン **/
	private JButton			_btnArgDel;
	/** 引数値編集テーブルペイン **/
	private MacroSubFilterArgValueTablePane	_argTable;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroSubFilterArgValuesEditPane() {
		super();
	}

	@Override
	public void initialComponent() {
		super.initialComponent();
		
		_argTable.setDataDropEnabled(getDataModel().isArgsEditable());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このコンポーネントの表示を更新する。
	 */
	public void refreshDisplay() {
		TableModel tm = _argTable.getTableModel();
		if (tm instanceof AbstractTableModel) {
			((AbstractTableModel)tm).fireTableDataChanged();
		}
	}

	/**
	 * フィルタ引数編集テーブルのカラム幅を初期化する。
	 * この初期化では、説明列と値列を、表示可能領域内で半々になるように設定する。
	 */
	public void initialAllTableColumnWidth() {
		_argTable.initialAllTableColumnWidth();
	}

	/**
	 * このコンポーネントに設定された、エディタビューで表示されているファイルの抽象パスを返す。
	 * 設定されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getViewingFileOnEditor() {
		return _vfViewingOnEditor;
	}

	/**
	 * このコンポーネントに、エディタビューで表示されているファイルの抽象パスを設定する。
	 * @param newFile	設定する抽象パス。抽象パスを設定しない場合は <tt>null</tt>
	 */
	public void setViewingFileOnEditor(VirtualFile newFile) {
		_vfViewingOnEditor = newFile;
	}

	/**
	 * 主コンポーネントの先頭が表示されるようにスクロールする。
	 */
	public void scrollTopToVisible() {
		_argTable.scrollTopRowToVisible();
	}

	/**
	 * 主コンポーネントの終端が表示されるようにスクロールする。
	 */
	public void scrollBottomToVisible() {
		_argTable.scrollBottomRowToVisible();
	}
	
	/**
	 * 主コンポーネントの指定された位置のアイテムが表示されるようにスクロールする。
	 * ただし、指定された位置のアイテムが非表示の場合や、インデックスが
	 * 無効な場合、このメソッドはなにもしない。
	 * @param index	アイテムの位置を示すインデックス
	 */
	public void scrollItemToVisible(int index) {
		_argTable.scrollRowToVisible(index);
	}

	/**
	 * 指定された引数インデックスに対応する箇所に、入力フォーカスを設定する。
	 * インデックスが適切ではない場合、このコンポーネントにフォーカスを設定する。
	 * @param argIndex	引数インデックス
	 */
	public void setFocusToArgument(int argIndex) {
		_argTable.setFocusToArgument(argIndex);
	}

	//------------------------------------------------------------
	// Internal events
	//------------------------------------------------------------

	@Override
	protected void onChangedDataModel(IFilterValuesEditModel oldModel, IFilterValuesEditModel newModel) {
		super.onChangedDataModel(oldModel, newModel);
		
		_argTable.setDataDropEnabled(newModel.isArgsEditable());
		_argTable.setDataModel(newModel);
		
		if (newModel != null && newModel.isEditing()) {
			//--- フィルタ編集中
			_btnArgEdit.setVisible(true);
			_btnArgDel .setVisible(true);
		} else {
			//--- フィルタ参照中
			_btnArgEdit.setVisible(false);
			_btnArgDel .setVisible(false);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 引数値編集パネルを生成する。
	 * @return	<code>JComponent</code> オブジェクト
	 */
	protected JComponent createArgValuesEditPanel() {
		this._btnArgEdit = CommonResources.createMenuIconButton(CommonResources.ICON_EDIT, CommonMessages.getInstance().Button_Edit);
		this._btnArgDel  = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		this._argTable   = new MacroSubFilterArgValueTablePane();
		_argTable.initialComponent();
		_argTable.attachEditButton(_btnArgEdit);
		_argTable.attachDeleteButton(_btnArgDel);
		
		// create panel (no border)
		JPanel pnl = new JPanel(new GridBagLayout());
		//--- adjust button preferred size
		Dimension dm = _btnArgEdit.getPreferredSize();
		_btnArgEdit.setMinimumSize(dm);
		_btnArgDel.setPreferredSize(dm);
		_btnArgDel.setMinimumSize(dm);
		//-- layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		pnl.add(_argTable, gbc);
		
		gbc.gridheight = 1;
		gbc.gridx = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 2, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		pnl.add(_btnArgEdit, gbc);
		
		gbc.gridy = 1;
		gbc.insets = new Insets(2, 2, 0, 0);
		pnl.add(_btnArgDel, gbc);
		
		return pnl;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
