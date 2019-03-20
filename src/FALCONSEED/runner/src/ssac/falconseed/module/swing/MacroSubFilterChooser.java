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
 * @(#)MacroSubFilterChooser.java	3.1.0	2014/05/14
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import ssac.aadl.common.CommonResources;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.swing.MacroSubFilterChooserModel.ChooserTableModel;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.table.SpreadSheetColumnHeader;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * サブフィルタを選択するダイアログ。
 * 
 * @version 3.1.0	2014/05/14
 * @since 3.1.0
 */
public class MacroSubFilterChooser extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** フィルタ選択モデル **/
	private final MacroSubFilterChooserModel	_datamodel;
	
	private SubFilterChooserTable	_candTable;
	private SubFilterChooserTable	_selectedTable;
	
	private JButton					_btnSelect;
	private JButton					_btnDeselect;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroSubFilterChooser(Frame owner, MacroSubFilterChooserModel datamodel, String title) {
		super(owner, title, true);
		if (datamodel == null)
			throw new NullPointerException("'datamodel' is null.");
		if (datamodel.isEmpty())
			throw new IllegalArgumentException("'datamodel' is empty.");
		_datamodel = datamodel;
		setConfiguration(AppSettings.MACROSUBFILTERCHOOSER_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public MacroSubFilterChooser(Dialog owner, MacroSubFilterChooserModel datamodel, String title) {
		super(owner, title, true);
		if (datamodel == null)
			throw new NullPointerException("'datamodel' is null.");
		if (datamodel.isEmpty())
			throw new IllegalArgumentException("'datamodel' is empty.");
		_datamodel = datamodel;
		setConfiguration(AppSettings.MACROSUBFILTERCHOOSER_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	public void initialComponent() {
		// create components
		_candTable     = new SubFilterChooserTable(_datamodel.getCandidateTableModel());
		_selectedTable = new SubFilterChooserTable(_datamodel.getSelectedTableModel());
		_btnSelect   = new JButton("↓");
		_btnDeselect = new JButton("↑");
		_btnSelect  .setEnabled(false);
		_btnDeselect.setEnabled(false);
		
		// initialize
		super.initialComponent();
		
		// restore
		restoreConfiguration();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public List<ModuleRuntimeData> getSelectedModuleList() {
		return _datamodel.getSelectedModuleList();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void onClickedSelectButton(ActionEvent ae) {
		int[] selrows = _candTable.getSelectedRows();
		_datamodel.selectAllModules(selrows);
	}
	
	protected void onClickedDeselectButton(ActionEvent ae) {
		int[] selrows = _selectedTable.getSelectedRows();
		_datamodel.deselectAllModules(selrows);
	}
	
	protected void onCandidateTableSelectionChanged(ListSelectionEvent lse) {
		_btnSelect.setEnabled(_candTable.getSelectedRowCount() != 0);
	}
	
	protected void onSelectedTableSelectionChanged(ListSelectionEvent lse) {
		_btnDeselect.setEnabled(_selectedTable.getSelectedRowCount() != 0);
	}

	@Override
	protected JButton createApplyButton() {
		return null;	// no [apply] button
	}

	@Override
	protected boolean doOkAction() {
		// エラーチェック必要？
		return super.doOkAction();
	}

	@Override
	protected void setupActions() {
		super.setupActions();
		
		// [selected] button
		_btnSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onClickedSelectButton(ae);
			}
		});
		
		// [deselected] button
		_btnDeselect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onClickedDeselectButton(ae);
			}
		});
		
		// table selection
		_candTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				onCandidateTableSelectionChanged(lse);
			}
		});
		_selectedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				onSelectedTableSelectionChanged(lse);
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
		
		JScrollPane scCand = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scCand.setViewportView(_candTable);
		
		JScrollPane scSelected = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scSelected.setViewportView(_selectedTable);

		//JLabel lblComboTitle = new JLabel(RunnerMessages.getInstance().MacroSubFilterArgReferDlg_Label_Combo);
		//JLabel lblListTitle = new JLabel(RunnerMessages.getInstance().ArgReferDlg_Label_args);
		
		Box pnlButtons = Box.createHorizontalBox();
		pnlButtons.add(Box.createHorizontalGlue());
		pnlButtons.add(_btnDeselect);
		pnlButtons.add(Box.createHorizontalStrut(10));
		pnlButtons.add(_btnSelect);
		pnlButtons.add(Box.createHorizontalGlue());
		
		JPanel pnlMain = new JPanel(new GridBagLayout());
		pnlMain.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		//--- label
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 0;
		pnlMain.add(new JLabel(RunnerMessages.getInstance().MacroSubFilterChooser_Label_CandidateFilters), gbc);
		gbc.gridy++;
		//--- Candidate table
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		pnlMain.add(scCand, gbc);
		gbc.gridy++;
		//--- buttons
		gbc.insets = new Insets(10, 0, 5, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0;
		pnlMain.add(pnlButtons, gbc);
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridy++;
		//--- label
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 0;
		pnlMain.add(new JLabel(RunnerMessages.getInstance().MacroSubFilterChooser_Label_SelectedFilters), gbc);
		gbc.gridy++;
		//--- Selected table
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		pnlMain.add(scSelected, gbc);
		gbc.gridy++;
		
		this.getContentPane().add(pnlMain, BorderLayout.CENTER);
	}

	@Override
	protected Dimension getDefaultSize() {
		return new Dimension(400,400);
	}
	
	@Override
	protected void initDialog() {
		super.initDialog();
		
		// 選択解除されたフィルタがあれば、通知
		if (!_datamodel.getCanceledModuleList().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append(RunnerMessages.getInstance().msgMacroSubFilterSelectedFilterCanceled);
			sb.append("\n");
			for (ModuleRuntimeData module : _datamodel.getCanceledModuleList()) {
				sb.append("\n");
				sb.append(String.format("[%d] %s", module.getRunNo(), module.getName()));
			}
			
			ModuleRunner.showWarningMessage(this, sb.toString());
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	static protected class EmptySubFilterChooserTableModel extends ChooserTableModel
	{
		@Override
		List<ModuleRuntimeData> getModuleList() {
			return Collections.emptyList();
		}
	}
	
	static protected EmptySubFilterChooserTableModel	_emptyChooserTableModel = new EmptySubFilterChooserTableModel();
	
	static protected class SubFilterChooserTable extends SpreadSheetTable
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
		
		public SubFilterChooserTable() {
			super();
			commonConstruction();
		}
		
		public SubFilterChooserTable(ChooserTableModel model) {
			super(model);
			commonConstruction();
		}
		
		private void commonConstruction() {
			setAdjustOnlyVisibleRows(false);			// 列幅の自動調整では全行を対象
			setAdjustOnlySelectedCells(false);			// 列幅の自動調整では選択セルのみに限定しない
			getTableRowHeader().setFixedCellWidth(50);	// 列幅を 50pixel に固定
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			setRowSelectionAllowed(true);
			setColumnSelectionAllowed(false);
			//setDefaultRenderer(Object.class, new MacroSubFiltersTableCellRenderer());
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		@Override
		public ChooserTableModel getModel() {
			return (ChooserTableModel)super.getModel();
		}

		@Override
		public void setModel(TableModel dataModel) {
			if (!(dataModel instanceof ChooserTableModel))
				throw new IllegalArgumentException("Table model is not MacroSubFilterChooserModel.ChooserTableModel instance.");
			
			super.setModel(dataModel);
		}

		/**
		 * 現在のデータモデルに従い、表示内容を更新する。
		 */
		public void refreshDisplay() {
			getModel().fireTableDataChanged();
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------

		@Override
		protected TableModel createDefaultDataModel() {
			return _emptyChooserTableModel;
		}

		@Override
		protected JTableHeader createDefaultTableHeader() {
			SpreadSheetColumnHeader th = (SpreadSheetColumnHeader)super.createDefaultTableHeader();
			// このテーブルでは、行単位の選択とするため、
			// 列選択や強調表示は無効とする。
			th.setEnableChangeSelection(false);
			th.setVisibleSelection(false);
			return th;
		}

		@Override
		protected RowHeaderModel createRowHeaderModel() {
			// モジュール引数テーブル専用の行ヘッダーモデル。
			// このモデルが返す値は、'$'と数字を連結させた文字列となる。
			return new SpreadSheetTable.RowHeaderModel(){
				@Override
				public Object getElementAt(int index) {
					return ((ChooserTableModel)getModel()).getRowName(index);
				}
			};
		}
	}
}
