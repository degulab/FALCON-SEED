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
 * @(#)MacroSubFilterArgReferDialog.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroSubFilterArgReferDialog.java	2.0.0	2012/11/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.swing.table.ModuleArgTable;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.swing.table.ReferenceableSubFilterArgTableModel;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.swing.AbBasicDialog;

/**
 * マクロフィルタを構成するサブフィルタのなかから、参照する引数を選択するためのダイアログ。
 * このダイアログでは、参照するフィルタとそのフィルタの参照可能な引数を選択可能とする。
 * 
 * @version 3.1.0	2014/05/19
 * @since 2.0.0
 */
public class MacroSubFilterArgReferDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
//	private final DefaultListModel			_emptyListModel = new DefaultListModel();
	
	private final MacroFilterEditModel		_datamodel;
	private final ModuleRuntimeData		_targetModule;
	private final ModuleArgConfig			_targetArg;

	/**
	 * モジュールの選択可能引数を表示するテーブルコンポーネント
	 * @since 3.1.0
	 */
	private ModuleArgTable	_cTable;
//	private JList		_cList;
	private JComboBox	_cCombo;

	private int			_selectedIndex = -1;
	private ModuleArgID	_selectedArg = null;
	
//	private List<ListModel>	_referArgsList;
	/**
	 * 選択可能な引数のデータモデルのリスト
	 * @since 3.1.0
	 */
	private List<ReferenceableSubFilterArgTableModel>	_referArgsList;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroSubFilterArgReferDialog(Frame owner, MacroFilterEditModel datamodel, ModuleRuntimeData targetModule, ModuleArgConfig targetArg) {
		super(owner, RunnerMessages.getInstance().MacroSubFilterArgReferDlg_Title, true);
		if (datamodel == null)
			throw new NullPointerException("'datamodel' is null.");
		if (targetModule == null)
			throw new NullPointerException("'targetModule' is null.");
		if (targetArg == null)
			throw new NullPointerException("'targetArg' is null.");
		if (ModuleArgType.IN != targetArg.getType() && ModuleArgType.SUB != targetArg.getType())
			throw new IllegalArgumentException("'targetArg' is not [IN],[SUB] argument!");
		if (targetArg.isFixedValue() || targetArg.getParameterType() == null)
			throw new IllegalArgumentException("'targetArg' is fixed value!");
		this._datamodel    = datamodel;
		this._targetModule = targetModule;
		this._targetArg    = targetArg;
		setConfiguration(AppSettings.MACROSUBFILTERARGREF_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public MacroSubFilterArgReferDialog(Dialog owner, MacroFilterEditModel datamodel, ModuleRuntimeData targetModule, ModuleArgConfig targetArg) {
		super(owner, RunnerMessages.getInstance().MacroSubFilterArgReferDlg_Title, true);
		if (datamodel == null)
			throw new NullPointerException("'datamodel' is null.");
		if (targetModule == null)
			throw new NullPointerException("'targetModule' is null.");
		if (targetArg == null)
			throw new NullPointerException("'targetArg' is null.");
		if (ModuleArgType.IN != targetArg.getType() && ModuleArgType.SUB != targetArg.getType())
			throw new IllegalArgumentException("'targetArg' is not [IN],[SUB] argument!");
		if (targetArg.isFixedValue() || targetArg.getParameterType() == null)
			throw new IllegalArgumentException("'targetArg' is fixed value!");
		this._datamodel    = datamodel;
		this._targetModule = targetModule;
		this._targetArg    = targetArg;
		setConfiguration(AppSettings.MACROSUBFILTERARGREF_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	public void initialComponent() {
		// フィルタ選択用コンポーネントの生成
		_cCombo = createFilterComboBox(createFilterComboBoxModel());
		
		// 引数選択用コンポーネントの生成
//		_selectedIndex = _cCombo.getSelectedIndex();
//		if (_selectedIndex < 0) {
//			//--- no selection
//			this._cList = new JList(_emptyListModel);
//		} else {
//			//--- selected
//			this._cList = new JList(_referArgsList.get(_selectedIndex));
//		}
//		_cList.setCellRenderer(new ArgReferListRenderer());
//		_cList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_cTable = createFilterArgumentTable();
		
		// 指定された引数の値が他フィルタ引数なら、その引数が属するモジュールを選択した状態とする
		_selectedIndex = (-1);
		ModuleArgID initArgID = null;
		if (_targetArg.getValue() instanceof ModuleArgID) {
			//--- 初期選択モジュールのインデックスを検索する
			ModuleRuntimeData initModule = ((ModuleArgID)_targetArg.getValue()).getData();
			for (int i = 0; i < _referArgsList.size(); ++i) {
				ReferenceableSubFilterArgTableModel tmodel = _referArgsList.get(i);
				assert (tmodel.getRowCount() > 0);
				if (initModule == tmodel.getRowData(0).getData()) {
					initArgID = (ModuleArgID)_targetArg.getValue();
					_selectedIndex = i;
					break;
				}
			}
		}
		if (_selectedIndex < 0) {
			// 選択可能なモジュールがあれば、先頭のモジュールを選択した状態とする
			if (!_referArgsList.isEmpty()) {
				_selectedIndex = 0;
				_cCombo.setSelectedIndex(_selectedIndex);
				_cTable.setModel(_referArgsList.get(0));
			}
		}
		else {
			// 初期選択モジュールを選択した状態とする
			_cCombo.setSelectedIndex(_selectedIndex);
			_cTable.setModel(_referArgsList.get(_selectedIndex));
		}
		
		// 指定された引数の値が他フィルタ引数で、その引数が属するモジュールが選択されていれば、その引数を選択した状態とする
		if (initArgID != null) {
			ReferenceableSubFilterArgTableModel tmodel = (ReferenceableSubFilterArgTableModel)_cTable.getModel();
			for (int rowIndex = 0; rowIndex < tmodel.getRowCount(); ++rowIndex) {
				ModuleArgID candArgID = tmodel.getRowData(rowIndex);
				if (candArgID.equals(initArgID)) {
					_cTable.setRowSelectionInterval(rowIndex, rowIndex);
					break;
				}
			}
		}
		
		super.initialComponent();
		
		restoreConfiguration();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ModuleArgID getSelectedArgument() {
		return _selectedArg;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void onComboBoxItemChanged(ItemEvent e) {
//		int selidx = _cCombo.getSelectedIndex();
//		if (selidx != _selectedIndex) {
//			_selectedIndex = selidx;
//			if (selidx < 0) {
//				//--- no selection
//				_cList.setModel(_emptyListModel);
//			} else {
//				//--- selected
//				_cList.setModel(_referArgsList.get(selidx));
//			}
//		}

		int selidx = _cCombo.getSelectedIndex();
		if (selidx != _selectedIndex) {
			_selectedIndex = selidx;
			if (selidx < 0) {
				//--- no selection
				_cTable.setModel(ReferenceableSubFilterArgTableModel.EmptyTableModel);
			} else {
				//--- selected
				_cTable.setModel(_referArgsList.get(selidx));
			}
		}
	}

	@Override
	protected JButton createApplyButton() {
		return null;	// no [apply] button
	}

	@Override
	protected boolean doOkAction() {
//		if (_cList.isSelectionEmpty()) {
		if (_cTable.getSelectedRowCount() <= 0) {
			// no selection
			ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgChooseReferArgument);
			return false;
		}
//		_selectedArg = (ModuleArgID)_cList.getSelectedValue();
		_selectedArg = ((ReferenceableSubFilterArgTableModel)_cTable.getModel()).getRowData(_cTable.getSelectedRow());
		
		return super.doOkAction();
	}

	@Override
	protected void setupActions() {
		super.setupActions();
		
		_cCombo.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				onComboBoxItemChanged(e);
			}
		});
	}

	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();
		
		setResizable(true);
		setKeepMinimumSize(true);
	}

	@Override
	protected void setupMainContents() {
		super.setupMainContents();
		
		JScrollPane scList = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		scList.setViewportView(this._cList);
		scList.setViewportView(_cTable);

		JLabel lblComboTitle = new JLabel(RunnerMessages.getInstance().MacroSubFilterArgReferDlg_Label_Combo);
		JLabel lblListTitle = new JLabel(RunnerMessages.getInstance().ArgReferDlg_Label_args);
		
		JPanel pnlMain = new JPanel(new GridBagLayout());
		pnlMain.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		pnlMain.add(lblComboTitle, gbc);
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnlMain.add(this._cCombo, gbc);

		gbc.gridy++;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 0, 0, 0);
		pnlMain.add(lblListTitle, gbc);
		gbc.gridy++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		pnlMain.add(scList, gbc);
		
		this.getContentPane().add(pnlMain, BorderLayout.CENTER);
	}

	/**
	 * 参照可能な引数を持つフィルタのみを保持するコンボボックスモデルを生成する。
	 * @return	生成されたモデル
	 */
	protected ComboBoxModel createFilterComboBoxModel() {
//		List<ListModel> list = _datamodel.getReferenceableSubFilterArguments(_targetModule, _targetArg);
//		DefaultComboBoxModel cmbmodel = new DefaultComboBoxModel();
//		
//		for (ListModel lmodel : list) {
//			assert(lmodel.getSize() > 0);
//			ModuleArgID argid = (ModuleArgID)lmodel.getElementAt(0);
//			cmbmodel.addElement(argid.getData().getRunNoAndFilterName());
//		}
//		
//		this._referArgsList = list;
//		return cmbmodel;
		List<ReferenceableSubFilterArgTableModel> list = _datamodel.getReferenceableSubFilterArgumentTableModel(_targetModule, _targetArg);
		DefaultComboBoxModel cmbmodel = new DefaultComboBoxModel();
		
		for (ReferenceableSubFilterArgTableModel lmodel : list) {
			assert(lmodel.getRowCount() > 0);
			ModuleArgID argid = (ModuleArgID)lmodel.getRowData(0);
			cmbmodel.addElement(argid.getData().getRunNoAndFilterName());
		}
		
		this._referArgsList = list;
		return cmbmodel;
	}

	/**
	 * 参照可能な引数を持つフィルタを選択するためのコンボボックスを生成する。
	 * @param model	コンボボックスに適用するモデル
	 * @return	生成されたコンボボックス
	 */
	protected JComboBox createFilterComboBox(ComboBoxModel model) {
		JComboBox cbox = new JComboBox(model);
		cbox.setEditable(false);
		//--- 初期選択位置は、編集対象サブフィルタの直前のサブフィルタとする。
		if (cbox.getItemCount() > 0) {
			cbox.setSelectedIndex(cbox.getItemCount()-1);
		}
		return cbox;
	}

	/**
	 * 参照可能な引数一覧を表示するテーブルコンポーネントを生成する。
	 * @return	生成されたテーブルコンポーネント
	 */
	protected ModuleArgTable createFilterArgumentTable() {
		ModuleArgTable table = new MacroSubFilterArgReferTable();
		table.setEditable(false);
		table.setModel(ReferenceableSubFilterArgTableModel.EmptyTableModel);
		//--- setup table parameters
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		return table;
	}

	@Override
	protected Dimension getDefaultSize() {
		return new Dimension(320,200);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class MacroSubFilterArgReferTable extends ModuleArgTable
	{
		private static final long serialVersionUID = 1L;

		public MacroSubFilterArgReferTable() {
			super();
		}

		@Override
		public void setModel(TableModel dataModel) {
			super.setModel(dataModel);
			
			// サイズ調整(属性のみ)
			if (dataModel instanceof ReferenceableSubFilterArgTableModel) {
				adjustColumnPreferredWidth(ReferenceableSubFilterArgTableModel.CI_ARGATTR);
				adjustColumnPreferredWidth(ReferenceableSubFilterArgTableModel.CI_ARGVALUE);
			}
		}

		@Override
		protected TableCellRenderer createModuleAttrCellRenderer() {
			return null;
		}

		@Override
		protected TableCellRenderer createDefaultObjectCellRenderer() {
			// TODO Auto-generated method stub
			return super.createDefaultObjectCellRenderer();
		}
	}
}
