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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ChartConfigDialog.java	2.1.0	2013/08/30
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.editor.plugin.csv.CsvFileMessages;
import ssac.falconseed.plot.ChartConfigModel.ChartConfigDataFieldModel;
import ssac.falconseed.plot.ChartConfigModel.ChartConfigRecordNumberField;
import ssac.falconseed.plot.ChartConfigModel.ChartDataTypes;
import ssac.falconseed.plot.ChartConfigModel.ChartInvalidValuePolicy;
import ssac.falconseed.plot.ChartConfigModel.ChartStyles;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.plot.DefaultDataSeries;
import ssac.util.swing.plot.IDataField;
import ssac.util.swing.plot.PlotDateTimeFormats;
import ssac.util.swing.table.SpreadSheetColumnHeader;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * チャート表示設定を行うダイアログ
 * 
 * @version 2.1.0	2013/08/30
 * @since 2.1.0
 */
public class ChartConfigDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	static private final Dimension DM_MIN_SIZE = new Dimension(800, 600);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** チャート描画設定モデル **/
	protected final ChartConfigModel	_configModel;
	
	/** 対象ファイルの総レコード数 **/
	private JLabel			_lblTotalRecordCount;
	/** データレコード数 **/
	private JLabel			_lblDataRecordCount;
	/** ヘッダーレコード数 **/
	private JLabel			_lblHeaderRecordCount;
	/** データレコード開始番号 **/
	private JLabel			_lblFirstRecordNumber;
	/** データレコード終了番号 **/
	private JLabel			_lblLastRecordNumber;
	/** データレコード範囲の設定ボタン **/
	private JButton			_btnEditRecordRange;
	
	/** 対象ファイル名 **/
	private JLabel			_lblTargetFileName;
	/** 対象ファイルを表示するボタン **/
	private JButton			_btnShowTargetFile;

	/** チャートの種類を選択するコンボボックス **/
	private JComboBox		_cmbChartTypes;
	/** 不正値もしくは空白の扱い **/
	private JComboBox		_cmbInvalidValuePolicy;
	/** チャートタイトル **/
	private JTextField		_txtChartTitle;
	/** チャート横軸のタイトル **/
	private JTextField		_txtChartHorzTitle;
	/** チャート縦軸のタイトル **/
	private JTextField		_txtChartVertTitle;
	/** チャートデータ型の日付時刻書式 **/
	private JTextField		_txtDateTimeFormat;
	/** 標準日付時刻書式を使用 **/
	private JRadioButton	_rdoTimeUseDefault;
	/** 独自の日付時刻書式を使用 **/
	private JRadioButton	_rdoTimeCustom;
	/** 横軸データ型：数値 **/
	private JRadioButton	_rdoHorzDataTypeDecimal;
	/** 縦軸データ型：数値 **/
	private JRadioButton	_rdoVertDataTypeDecimal;
	/** 横軸データ型：日付時刻 **/
	private JRadioButton	_rdoHorzDataTypeTime;
	/** 縦軸データ型：日付時刻 **/
	private JRadioButton	_rdoVertDataTypeTime;
	/** 横軸データ型：テキスト **/
	private JRadioButton	_rdoHorzDataTypeText;
	/** 縦軸データ型：テキスト **/
	private JRadioButton	_rdoVertDataTypeText;

	/** データ系列一括設定：機能選択コンボボックス **/
	private JComboBox		_cmbFunctionType;
	/** データ系列一括設定：機能実行ボタン **/
	private JButton			_btnFunctionExec;
	/** データ系列追加ボタン **/
	private JButton			_btnSeriesAdd;
	/** データ系列編集ボタン **/
	private JButton			_btnSeriesEdit;
	/** データ系列削除ボタン **/
	private JButton			_btnSeriesDelete;
	/** データ系列上へ移動ボタン **/
	private JButton			_btnSeriesMoveUp;
	/** データ系列下へ移動ボタン **/
	private JButton			_btnSeriesMoveDown;
	/** データ系列テーブル **/
	private SeriesTable		_seriesTable;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ChartConfigDialog(Frame owner, final ChartConfigModel chartModel, boolean modal) {
		super(owner, RunnerMessages.getInstance().ChartConfigDlg_title, modal);
		if (chartModel == null)
			throw new NullPointerException("The specified ChartConfigModel object is null.");
		_configModel = chartModel;
		setConfiguration(AppSettings.CHARTCONFIG_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public ChartConfigDialog(Dialog owner, final ChartConfigModel chartModel, boolean modal) {
		super(owner, RunnerMessages.getInstance().ChartConfigDlg_title, modal);
		if (chartModel == null)
			throw new NullPointerException("The specified ChartConfigModel object is null.");
		_configModel = chartModel;
		setConfiguration(AppSettings.CHARTCONFIG_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	public void initialComponent() {
		// create content components
		createContentComponents();
		
		// initial component
		super.initialComponent();
		
		// restore settings
		restoreConfiguration();
		
		// update display
		_lblTargetFileName.setText(_configModel.getTargetFile().getName());
		_lblTotalRecordCount.setText(String.valueOf(_configModel.getRecordCount()));
		refreshDataRecordRange();
		refreshChartSettings();
		_cmbFunctionType.setSelectedIndex(0);
		updateSeriesButtons();
		
		// setup actions
		//--- show target file
		_btnShowTargetFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onShowTargetFile(e);
			}
		});
		//--- datetime formats
		_rdoTimeCustom.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				_txtDateTimeFormat.setEnabled(_rdoTimeCustom.isSelected());
			}
		});
		//--- data range edit button
		_btnEditRecordRange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onEditRecordRangeButton(e);
			}
		});
		//--- generate series
		_btnFunctionExec.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onGenerateFunctionButton(e);
			}
		});
		//--- series table selection
		_seriesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onSeriesTableSelectionChanged(e);
			}
		});
		//--- series table buttons
		_btnSeriesAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSeriesAddButton(e);
			}
		});
		_btnSeriesEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSeriesEditButton(e);
			}
		});
		_btnSeriesDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSeriesDeleteButton(e);
			}
		});
		_btnSeriesMoveUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSeriesMoveUpButton(e);
			}
		});
		_btnSeriesMoveDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSeriesMoveDownButton(e);
			}
		});
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ChartConfigModel getConfigModel() {
		return _configModel;
	}
	
	/**
	 * このウィンドウリソースを破棄する。
	 */
	public void destroy() {
		if (isDisplayable()) {
			dialogClose(IDialogResult.DialogResult_Cancel);			
		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	@Override
	protected void initDialog() {
		super.initDialog();
		
		// テーブルの幅を設定
		Rectangle rc = _seriesTable.getVisibleRect();
		int maxWidth = rc.width;
		int colWidth = rc.width / 3;
		TableColumnModel cmodel = _seriesTable.getColumnModel();
		for (int i = 0; i < cmodel.getColumnCount() - 1; i++) {
			cmodel.getColumn(i).setPreferredWidth(colWidth);
			maxWidth -= colWidth;
		}
		cmodel.getColumn(cmodel.getColumnCount()-1).setPreferredWidth(maxWidth);
	}
	
	protected void onSeriesTableSelectionChanged(ListSelectionEvent lse) {
		updateSeriesButtons();
	}
	
	protected void onShowTargetFile(ActionEvent ae) {
		// place holder
	}

	protected void onEditRecordRangeButton(ActionEvent ae) {
		ChartDataRangeEditDialog dlg = new ChartDataRangeEditDialog(this,
				_configModel.getRecordCount(), _configModel.getHeaderRecordCount(),
				_configModel.getFirstDataRecordIndex(), _configModel.getLastDataRecordIndex());
		dlg.initialComponent();
		dlg.setVisible(true);
		if (IDialogResult.DialogResult_OK == dlg.getDialogResult()) {
			long lHeaderRecordCount = dlg.getHeaderRecordCount();
			long lFirstRecordIndex  = dlg.getFirstDataRecordIndex();
			long lLastRecordIndex   = dlg.getLastDataRecordIndex();
			if (lHeaderRecordCount != _configModel.getHeaderRecordCount()) {
				_configModel.setHeaderRecordCount(lHeaderRecordCount);
				_seriesTable.getModel().fireTableDataChanged();
			}
			if (lFirstRecordIndex != _configModel.getFirstDataRecordIndex() || lLastRecordIndex != _configModel.getLastDataRecordIndex()) {
				_configModel.setDataRecordRange(lFirstRecordIndex, lLastRecordIndex);
			}
			refreshDataRecordRange();
		}
		dlg.dispose();
	}
	
	protected void onGenerateFunctionButton(ActionEvent ae) {
		int selected = _cmbFunctionType.getSelectedIndex();
		if (selected == 0) {
			// [選択列の全組合せ（重複あり）]
			if (_configModel.getFieldCount() < 2) {
				// error
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigGenPairsNumFieldsNotEnough);
				return;
			}
			//--- 現在の選択列を収集
			int[] indices = getNoRepetitionFieldIndicesFromAllDataSeries();
			//--- 列選択
			ChartMultiFieldSelectDialog dlg = new ChartMultiFieldSelectDialog(this,
					RunnerMessages.getInstance().ChartConfigDlg_func_allpairs_repeated, _configModel, true, indices);
			dlg.initialComponent();
			dlg.setVisible(true);
			if (IDialogResult.DialogResult_OK == dlg.getDialogResult()) {
				//--- ペア生成
				int[][] pairs = ChartConfigModel.createRepeatedPairsDataFields(dlg.getSelectedIndices());
				//--- 全データ系列削除
				SeriesTableModel model = _seriesTable.getModel();
				model.removeAllRows();
				//--- 新しいデータ系列の生成
				for (int[] pair : pairs) {
					DefaultDataSeries series = new DefaultDataSeries(_configModel.getFieldModel(pair[0]), _configModel.getFieldModel(pair[1]));
					model.addRow(series, null, null);
				}
				//--- 先頭表示
				_seriesTable.scrollToVisibleCell(0, 0);
				//--- データ型を数値型に設定
				_rdoHorzDataTypeDecimal.setSelected(true);
				_rdoVertDataTypeDecimal.setSelected(true);
			}
			dlg.dispose();
		}
		else if (selected == 1) {
			// [選択列の全組合せ（重複なし）]
			if (_configModel.getFieldCount() < 2) {
				// error
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigGenPairsNumFieldsNotEnough);
				return;
			}
			//--- 現在の選択列を収集
			int[] indices = getNoRepetitionFieldIndicesFromAllDataSeries();
			//--- 列選択
			ChartMultiFieldSelectDialog dlg = new ChartMultiFieldSelectDialog(this,
					RunnerMessages.getInstance().ChartConfigDlg_func_allpairs_norepeat, _configModel, true, indices);
			dlg.initialComponent();
			dlg.setVisible(true);
			if (IDialogResult.DialogResult_OK == dlg.getDialogResult()) {
				//--- ペア生成
				int[][] pairs = ChartConfigModel.createNoRepetitionPairsDataFields(dlg.getSelectedIndices());
				//--- 全データ系列削除
				SeriesTableModel model = _seriesTable.getModel();
				model.removeAllRows();
				//--- 新しいデータ系列の生成
				for (int[] pair : pairs) {
					DefaultDataSeries series = new DefaultDataSeries(_configModel.getFieldModel(pair[0]), _configModel.getFieldModel(pair[1]));
					model.addRow(series, null, null);
				}
				//--- 先頭表示
				_seriesTable.scrollToVisibleCell(0, 0);
				//--- データ型を数値型に設定
				_rdoHorzDataTypeDecimal.setSelected(true);
				_rdoVertDataTypeDecimal.setSelected(true);
			}
			dlg.dispose();
		}
		else if (selected == 2) {
			// [縦軸データ列を選択]
			//--- 現在の選択列を収集
			int[] indices = getNoRepetitionFieldIndicesFromAllDataSeries();
			//--- 列選択
			ChartMultiFieldSelectDialog dlg = new ChartMultiFieldSelectDialog(this,
					RunnerMessages.getInstance().ChartConfigDlg_func_vertfieldsselect, _configModel, false, indices);
			dlg.initialComponent();
			dlg.setVisible(true);
			if (IDialogResult.DialogResult_OK == dlg.getDialogResult()) {
				indices = dlg.getSelectedIndices();
				//--- 全データ系列削除
				SeriesTableModel model = _seriesTable.getModel();
				model.removeAllRows();
				//--- 新しいデータ系列の生成
				for (int index : indices) {
					DefaultDataSeries series = new DefaultDataSeries(_configModel.getDefaultRecordNumberField(), _configModel.getFieldModel(index));
					model.addRow(series, null, null);
				}
				//--- 先頭表示
				_seriesTable.scrollToVisibleCell(0, 0);
				//--- データ型を数値型に設定
				_rdoHorzDataTypeDecimal.setSelected(true);
			}
			dlg.dispose();
		}
		else if (selected == 3) {
			// [横軸データ列を選択]
			//--- 現在の選択列を収集
			int[] indices = getNoRepetitionFieldIndicesFromAllDataSeries();
			//--- 列選択
			ChartMultiFieldSelectDialog dlg = new ChartMultiFieldSelectDialog(this,
					RunnerMessages.getInstance().ChartConfigDlg_func_horzfieldsselect, _configModel, false, indices);
			dlg.initialComponent();
			dlg.setVisible(true);
			if (IDialogResult.DialogResult_OK == dlg.getDialogResult()) {
				indices = dlg.getSelectedIndices();
				//--- 全データ系列削除
				SeriesTableModel model = _seriesTable.getModel();
				model.removeAllRows();
				//--- 新しいデータ系列の生成
				for (int index : indices) {
					DefaultDataSeries series = new DefaultDataSeries(_configModel.getFieldModel(index), _configModel.getDefaultRecordNumberField());
					model.addRow(series, null, null);
				}
				//--- 先頭表示
				_seriesTable.scrollToVisibleCell(0, 0);
				//--- データ型を数値型に設定
				_rdoVertDataTypeDecimal.setSelected(true);
			}
			dlg.dispose();
		}
		else if (selected == 4) {
			// [縦軸のデータ列置き換え]
			//--- データ系列の有無を確認
			SeriesTableModel model = _seriesTable.getModel();
			if (model.getRowCount() <= 0) {
				// no data series
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigNoDataSeries);
				return;
			}
			//--- 先頭のデータ系列を取得
			ChartConfigDataFieldModel field = (ChartConfigDataFieldModel)model.getSeries(0).getYField();
			//--- 列選択
			ChartFieldSelectDialog dlg = new ChartFieldSelectDialog(this,
					RunnerMessages.getInstance().ChartConfigDlg_func_vertfieldsreplace, _configModel, field);
			dlg.initialComponent();
			dlg.setVisible(true);
			if (IDialogResult.DialogResult_OK == dlg.getDialogResult()) {
				//--- 縦軸系列置き換え
				field = dlg.getSelectedField();
				int numRows = model.getRowCount();
				for (int row = 0; row < numRows; ++row) {
					model.setValueAt(field, row, SeriesTableModel.LOGICAL_CI_YFIELD);
				}
			}
			dlg.dispose();
		}
		else if (selected == 5) {
			// [横軸のデータ列置き換え]
			//--- データ系列の有無を確認
			SeriesTableModel model = _seriesTable.getModel();
			if (model.getRowCount() <= 0) {
				// no data series
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigNoDataSeries);
				return;
			}
			//--- 先頭のデータ系列を取得
			ChartConfigDataFieldModel field = (ChartConfigDataFieldModel)model.getSeries(0).getXField();
			//--- 列選択
			ChartFieldSelectDialog dlg = new ChartFieldSelectDialog(this,
					RunnerMessages.getInstance().ChartConfigDlg_func_horzfieldsreplace, _configModel, field);
			dlg.initialComponent();
			dlg.setVisible(true);
			if (IDialogResult.DialogResult_OK == dlg.getDialogResult()) {
				//--- 横軸系列置き換え
				field = dlg.getSelectedField();
				int numRows = model.getRowCount();
				for (int row = 0; row < numRows; ++row) {
					model.setValueAt(field, row, SeriesTableModel.LOGICAL_CI_XFIELD);
				}
			}
			dlg.dispose();
		}
		else if (selected == 6) {
			// [データ系列をすべて削除]
			if (_seriesTable.getModel().getRowCount() > 0) {
				int ret = JOptionPane.showConfirmDialog(this,
							RunnerMessages.getInstance().confirmDeleteAllDataSeries,
							RunnerMessages.getInstance().confirmDeleteDataSeriesTitle,
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (ret == JOptionPane.YES_OPTION) {
					// delete all data series
					_seriesTable.getModel().removeAllRows();
				}
			}
		}
		else {
			// no function
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigGenerateFuncNoSelected);
		}
	}

	// データ系列[追加]ボタン
	protected void onSeriesAddButton(ActionEvent ae) {
		// 新規作成
		ChartSeriesEditDialog dlg = new ChartSeriesEditDialog(this,
				RunnerMessages.getInstance().ChartSeriesEditDlg_title_new, _configModel, null);
		dlg.initialComponent();
		dlg.setVisible(true);
		if (IDialogResult.DialogResult_OK == dlg.getDialogResult()) {
			DefaultDataSeries series = dlg.getDataSeries();
			_seriesTable.getModel().addRow(series, null, null);
			_seriesTable.scrollToVisibleCell(_seriesTable.getRowCount()-1, 0);
			updateSeriesButtons();
		}
		dlg.dispose();
	}

	// データ系列[編集]ボタン
	protected void onSeriesEditButton(ActionEvent ae) {
		// 編集
		int selrow = _seriesTable.getSelectedRow();
		if (selrow < 0) {
			return;
		}
		DefaultDataSeries series = _seriesTable.getModel().getSeries(selrow);
		ChartSeriesEditDialog dlg = new ChartSeriesEditDialog(this,
				RunnerMessages.getInstance().ChartSeriesEditDlg_title_edit, _configModel, series);
		dlg.initialComponent();
		dlg.setVisible(true);
		if (IDialogResult.DialogResult_OK == dlg.getDialogResult()) {
			series = dlg.getDataSeries();
			_seriesTable.getModel().setSeries(selrow, series);
			_seriesTable.setRowSelectionInterval(selrow, selrow);
			_seriesTable.scrollToVisibleCell(selrow, 0);
			updateSeriesButtons();
		}
		dlg.dispose();
	}

	// データ系列[削除]ボタン
	protected void onSeriesDeleteButton(ActionEvent ae) {
		int[] selrows = _seriesTable.getSelectedRows();
		if (selrows.length > 0) {
			Arrays.sort(selrows);
			int firstRow = selrows[0];
			//--- remove
			for (int i = selrows.length-1; i >= 0; --i) {
				_seriesTable.getModel().removeRow(selrows[i]);
			}
			//--- select last row
			if (_seriesTable.getRowCount() > 0) {
				if (firstRow >= _seriesTable.getRowCount()) {
					firstRow = _seriesTable.getRowCount()-1;
				}
				_seriesTable.getSelectionModel().setSelectionInterval(firstRow, firstRow);
				_seriesTable.scrollToVisibleCell(firstRow, 0);
			}
		}
	}
	
	protected void onSeriesMoveUpButton(ActionEvent ae) {
		int[] selrows = _seriesTable.getSelectedRows();
		if (selrows.length <= 0)
			return;		// no selection
		
		// 移動
		SeriesTableModel model = _seriesTable.getModel();
		int firstRowIndex = -1;
		int lastRowIndex  = -1;
		int movableRow = 1;
		final int[] afterMovedRows = new int[selrows.length];
		for (int index = 0; index < selrows.length; index++) {
			int srow = selrows[index];
			if (srow >= movableRow) {
				// 移動可能
				movableRow = srow - 1;
				if (firstRowIndex < 0)
					firstRowIndex = srow;
				lastRowIndex = srow;
				afterMovedRows[index] = movableRow;
				movableRow += 2;
			}
			else {
				// 移動不可能
				if (firstRowIndex >= 0) {
					model.moveRow(firstRowIndex, lastRowIndex, firstRowIndex-1);
					firstRowIndex = -1;
					lastRowIndex = -1;
				}
				afterMovedRows[index] = srow;
				movableRow = srow + 2;	// 移動不可能インデックスと間隔が空いている位置
			}
		}
		if (firstRowIndex >= 0) {
			model.moveRow(firstRowIndex, lastRowIndex, firstRowIndex-1);
		}
		
		// 選択位置更新
		//--- 一時選択解除
		ListSelectionModel selmodel = _seriesTable.getSelectionModel();
		selmodel.setValueIsAdjusting(true);
		selmodel.clearSelection();
		//--- 選択状態更新
		for (int row : afterMovedRows) {
			selmodel.addSelectionInterval(row, row);
		}
		selmodel.setValueIsAdjusting(false);
		//--- 選択位置を表示
		//Rectangle rc1 = _seriesTable.getCellRect(selmodel.getMinSelectionIndex(), 0, true);
		//Rectangle rc2 = _seriesTable.getCellRect(selmodel.getMaxSelectionIndex(), 0, true);
		//_seriesTable.scrollRectToVisible(rc1.union(rc2));
		_seriesTable.scrollToVisibleCell(afterMovedRows[0], 0);
	}
	
	protected void onSeriesMoveDownButton(ActionEvent ae) {
		int[] selrows = _seriesTable.getSelectedRows();
		if (selrows.length <= 0)
			return;		// no selection
		
		// 移動
		SeriesTableModel model = _seriesTable.getModel();
		int firstRowIndex = -1;
		int lastRowIndex  = -1;
		int movableRow = model.getRowCount() - 2;
		final int[] afterMovedRows = new int[selrows.length];
		for (int index = selrows.length-1; index >= 0; index--) {
			int srow = selrows[index];
			if (srow <= movableRow) {
				// 移動可能
				movableRow = srow + 1;
				if (lastRowIndex < 0)
					lastRowIndex = srow;
				firstRowIndex = srow;
				afterMovedRows[index] = movableRow;
				movableRow -= 2;
			}
			else {
				// 移動不可能
				if (firstRowIndex >= 0) {
					model.moveRow(firstRowIndex, lastRowIndex, firstRowIndex+1);
					firstRowIndex = -1;
					lastRowIndex = -1;
				}
				afterMovedRows[index] = srow;
				movableRow = srow - 2;	// 移動不可能インデックスと間隔が空いている位置
			}
		}
		if (firstRowIndex >= 0) {
			model.moveRow(firstRowIndex, lastRowIndex, firstRowIndex+1);
		}
		
		// 選択位置更新
		//--- 一時選択解除
		ListSelectionModel selmodel = _seriesTable.getSelectionModel();
		selmodel.setValueIsAdjusting(true);
		selmodel.clearSelection();
		//--- 選択状態更新
		for (int row : afterMovedRows) {
			selmodel.addSelectionInterval(row, row);
		}
		selmodel.setValueIsAdjusting(false);
		//--- 選択位置を表示
		//Rectangle rc1 = _seriesTable.getCellRect(selmodel.getMinSelectionIndex(), 0, true);
		//Rectangle rc2 = _seriesTable.getCellRect(selmodel.getMaxSelectionIndex(), 0, true);
		//_seriesTable.scrollRectToVisible(rc1.union(rc2));
		_seriesTable.scrollToVisibleCell(afterMovedRows[selrows.length-1], 0);
	}

	@Override
	protected boolean doOkAction() {
		// グラフ種類が選択されているか
		ChartStyles chartType = getSelectedChartStyle();
		if (chartType == null) {
			// no selection chart type
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigNoSelectionChartStyle);
			return false;
		}
		
		// 無効値の扱い
		ChartInvalidValuePolicy policy = getSelectedInvalidValuePolicy();
		if (policy == null) {
			policy = ChartInvalidValuePolicy.SKIP_RECORD;
		}
		
		// 日付時刻フォーマット
		PlotDateTimeFormats dtFormats = null;
		if (_rdoTimeCustom.isSelected()) {
			String strFormat = _txtDateTimeFormat.getText();
			if (strFormat == null)
				strFormat = "";
			strFormat.trim();
			if (strFormat.length() > 0) {
				try {
					dtFormats = new PlotDateTimeFormats(strFormat);
				} catch (Throwable ex) {
					// format error
					Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigIllegalDateTimeFormat);
					return false;
				}
			}
		}
		
		// データ系列の有無をチェック
		if (_seriesTable.getRowCount() <= 0) {
			// no series
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigNoDataSeries);
			return false;
		}
		
		// データ型のチェック
		if (chartType == ChartStyles.SCATTER && !_rdoHorzDataTypeDecimal.isSelected() && !_rdoVertDataTypeDecimal.isSelected()) {
			// not decimal data type
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigScatterDataTypeNotDecimal);
			return false;
		}
		
		// 設定値を保存
		_configModel.setChartStyle(chartType);
		_configModel.setInvalidValuePolicy(policy);
		_configModel.setChartTitle(_txtChartTitle.getText());
		_configModel.setXaxisLabel(_txtChartHorzTitle.getText());
		_configModel.setYaxisLabel(_txtChartVertTitle.getText());
		_configModel.setCustomDateTimeFormats(dtFormats);
		if (_rdoHorzDataTypeText.isSelected()) {
			_configModel.setXDataType(ChartDataTypes.TEXT);
		}
		else if (_rdoHorzDataTypeTime.isSelected()) {
			_configModel.setXDataType(ChartDataTypes.DATETIME);
		}
		else {
			_configModel.setXDataType(ChartDataTypes.DECIMAL);
		}
		if (_rdoVertDataTypeText.isSelected()) {
			_configModel.setYDataType(ChartDataTypes.TEXT);
		}
		else if (_rdoVertDataTypeTime.isSelected()) {
			_configModel.setYDataType(ChartDataTypes.DATETIME);
		}
		else {
			_configModel.setYDataType(ChartDataTypes.DECIMAL);
		}
		//--- データ系列
		SeriesTableModel model = _seriesTable.getModel();
		ArrayList<DefaultDataSeries> seriesList = _configModel.getDataSeriesList();
		seriesList.clear();
		for (int row = 0; row < model.getRowCount(); ++row) {
			seriesList.add(model.getSeries(row));
		}
		
		// 完了
		return true;
	}
	
	@Override
	protected void dialogClose(int result) {
		// ダイアログを閉じる
		super.dialogClose(result);
	}

	//------------------------------------------------------------
	// Error messages
	//------------------------------------------------------------
	
	protected String getErrorEmptyNameKey(int rowIndex, String columnName) {
		return String.format(CsvFileMessages.getInstance().msgDtBaseNameEmpty, (rowIndex+1), columnName);
	}
	
	protected String getErrorEmptyDataTypeKey(int rowIndex, String columnName) {
		return String.format(CsvFileMessages.getInstance().msgDtBaseTypeEmpty, (rowIndex+1), columnName);
	}
	
	protected String getErrorIllegalBaseKey(int rowIndex, String columnName) {
		String msg = String.format(CsvFileMessages.getInstance().msgDtBaseIllegalBaseKeyChars, (rowIndex+1), columnName);
		msg = msg + "\n    " + CsvFileMessages.getInstance().ExportDtalgeDlg_illegalBaseKeyChars;
		return msg; 
	}
	
	protected String getErrorIllegalDataTypeKey(int rowIndex, String columnName) {
		return String.format(CsvFileMessages.getInstance().msgDtBaseIllegalTypeKey, (rowIndex+1), columnName);
	}
	
	protected String getErrorDtBaseMultiple(int existRowIndex, int rowIndex) {
		return String.format(CsvFileMessages.getInstance().msgDtBaseMultiple, (existRowIndex+1), (rowIndex+1));
	}
	
	protected String getErrorFailedToAutoDetect() {
		return CsvFileMessages.getInstance().msgFailedToAutoDetect;
	}
	
	protected String getErrorEmptySelectedFile() {
		return CsvFileMessages.getInstance().msgOutputFileEmpty;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected List<Integer> getFieldIndicesFromAllDataSeries() {
		SeriesTableModel tmodel = _seriesTable.getModel();
		int rowCount = tmodel.getRowCount();
		ArrayList<Integer> list = new ArrayList<Integer>(rowCount * 2);
		for (int i = 0; i < rowCount; ++i) {
			DefaultDataSeries series = tmodel.getSeries(i);
			IDataField field = series.getXField();
			if (field != null && !(field instanceof ChartConfigRecordNumberField)) {
				list.add(field.getFieldIndex());
			}
			field = series.getYField();
			if (field != null && !(field instanceof ChartConfigRecordNumberField)) {
				list.add(field.getFieldIndex());
			}
		}
		return list;
	}
	
	protected int[] getNoRepetitionFieldIndicesFromAllDataSeries() {
		SeriesTableModel tmodel = _seriesTable.getModel();
		int rowCount = tmodel.getRowCount();
		TreeSet<Integer> tset = new TreeSet<Integer>();
		for (int i = 0; i < rowCount; ++i) {
			DefaultDataSeries series = tmodel.getSeries(i);
			IDataField field = series.getXField();
			if (field != null && !(field instanceof ChartConfigRecordNumberField)) {
				tset.add(field.getFieldIndex());
			}
			field = series.getYField();
			if (field != null && !(field instanceof ChartConfigRecordNumberField)) {
				tset.add(field.getFieldIndex());
			}
		}
		int index = 0;
		int[] indices = new int[tset.size()];
		for (int fi : tset) {
			indices[index++] = fi;
		}
		return indices;
	}
	
	protected void refreshDataRecordRange() {
		_lblHeaderRecordCount.setText(String.valueOf(_configModel.getHeaderRecordCount()));
		_lblFirstRecordNumber.setText(String.valueOf(_configModel.getFirstDataRecordIndex() + 1L));
		_lblLastRecordNumber .setText(String.valueOf(_configModel.getLastDataRecordIndex()));
		_lblDataRecordCount  .setText(String.valueOf(_configModel.getDataRecordCount()));
	}
	
	protected void refreshDateTimeFormatSetting() {
		PlotDateTimeFormats formats = _configModel.getCustomDateTimeFormats();
		if (formats != null) {
			_rdoTimeCustom.setSelected(true);
			_txtDateTimeFormat.setEnabled(true);
			_txtDateTimeFormat.setText(formats.toString());
		} else {
			_rdoTimeUseDefault.setSelected(true);
			_txtDateTimeFormat.setEnabled(false);
		}
	}
	
	protected void refreshChartSettings() {
		setSelectedChartStyle(_configModel.getChartStyle());
		setSelectedInvalidValuePolicy(_configModel.getInvalidValuePolicy());
		_txtChartTitle.setText(_configModel.getChartTitle());
		_txtChartHorzTitle.setText(_configModel.getXaxisLabel());
		_txtChartVertTitle.setText(_configModel.getYaxisLabel());
		
		refreshDateTimeFormatSetting();
		
		ChartDataTypes xType = _configModel.getXDataType();
		if (xType == ChartDataTypes.TEXT) {
			_rdoHorzDataTypeText.setSelected(true);
		} else if (xType == ChartDataTypes.DATETIME) {
			_rdoHorzDataTypeTime.setSelected(true);
		} else {
			_rdoHorzDataTypeDecimal.setSelected(true);
		}
		
		ChartDataTypes yType = _configModel.getYDataType();
		if (yType == ChartDataTypes.TEXT) {
			_rdoVertDataTypeText.setSelected(true);
		} else if (yType == ChartDataTypes.DATETIME) {
			_rdoVertDataTypeTime.setSelected(true);
		} else {
			_rdoVertDataTypeDecimal.setSelected(true);
		}
	}
	
	protected void updateSeriesButtons() {
		int selRows = _seriesTable.getSelectedRowCount();
		if (selRows > 0) {
			// has selection rows
			_btnSeriesEdit  .setEnabled(selRows == 1);
			_btnSeriesDelete.setEnabled(true);
			if (selRows == _seriesTable.getRowCount()) {
				// selected all
				_btnSeriesMoveUp  .setEnabled(false);
				_btnSeriesMoveDown.setEnabled(false);
			}
			else {
				int selMinRow = _seriesTable.getSelectionModel().getMinSelectionIndex();
				int selMaxRow = _seriesTable.getSelectionModel().getMaxSelectionIndex();
				int rangeRows = selMaxRow - selMinRow + 1;
				if (rangeRows != selRows) {
					// no selection rows in range
					_btnSeriesMoveUp  .setEnabled(true);
					_btnSeriesMoveDown.setEnabled(true);
				}
				else if (selMinRow == 0) {
					// allow down
					_btnSeriesMoveUp  .setEnabled(false);
					_btnSeriesMoveDown.setEnabled(true);
				}
				else if (selMaxRow == (_seriesTable.getRowCount()-1)) {
					// allow up
					_btnSeriesMoveUp  .setEnabled(true);
					_btnSeriesMoveDown.setEnabled(false);
				}
				else {
					// allow up and down
					_btnSeriesMoveUp  .setEnabled(true);
					_btnSeriesMoveDown.setEnabled(true);
				}
			}
		}
		else {
			// no selection
			_btnSeriesEdit  .setEnabled(false);
			_btnSeriesDelete.setEnabled(false);
			_btnSeriesMoveUp.setEnabled(false);
			_btnSeriesMoveDown.setEnabled(false);
		}
	}

//	/**
//	 * 設定されたヘッダー情報を検証する。
//	 * このメソッドではデータ型の正当性はチェックしない。
//	 * @return	ヘッダー情報として正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
//	 */
//	protected boolean verifyHeaderData() {
//		DtalgeHeaderTableModel model = getTableModel();
//		int indexName = model.nameColumnIndex();
//		int indexType = model.typeColumnIndex();
//		int indexAttr = model.attrColumnIndex();
//		int indexSubject = model.subjectColumnIndex();
//		String labelNameColumn = model.getColumnName(indexName);
//		String labelTypeColumn = model.getColumnName(indexType);
//		String labelAttrColumn = model.getColumnName(indexAttr);
//		String labelSubjectColumn = model.getColumnName(indexSubject);
//		
//		//--- 同じ基底が存在することを警告するための集合
//		Map<DtBase, Integer> map = new HashMap<DtBase, Integer>();
//		
//		// データ代数基底に変換
//		int len = model.getRowCount();
//		for (int row = 0; row < len; row++) {
//			DtalgeDataTypes type = model.getTypeKey(row);
//			String name = trimString(model.getNameKey(row));
//			String attr = trimString(model.getAttributeKey(row));
//			String subject = trimString(model.getSubjectKey(row));
//			
//			//--- check name key
//			if (Strings.isNullOrEmpty(name)) {
//				Application.showErrorMessage(this, getErrorEmptyNameKey(row, labelNameColumn));
//				getTable().scrollToVisibleCell(row, indexName);
//				getTable().setCellSelected(row, indexName);
//				return false;
//			}
//			if (!DtBase.Util.isValidBaseKeyString(name, true)) {
//				Application.showErrorMessage(this, getErrorIllegalBaseKey(row, labelNameColumn));
//				getTable().scrollToVisibleCell(row, indexName);
//				getTable().setCellSelected(row, indexName);
//				return false;
//			}
//			//--- check type key
//			if (type == null) {
//				Application.showErrorMessage(this, getErrorEmptyDataTypeKey(row, labelTypeColumn));
//				getTable().scrollToVisibleCell(row, indexType);
//				getTable().setCellSelected(row, indexType);
//				return false;
//			}
//			
//			//--- check attr key
//			if (!DtBase.Util.isValidBaseKeyString(attr, true)) {
//				Application.showErrorMessage(this, getErrorIllegalBaseKey(row, labelAttrColumn));
//				getTable().scrollToVisibleCell(row, indexAttr);
//				getTable().setCellSelected(row, indexAttr);
//				return false;
//			}
//			//--- check subject key
//			if (!DtBase.Util.isValidBaseKeyString(subject, true)) {
//				Application.showErrorMessage(this, getErrorIllegalBaseKey(row, labelSubjectColumn));
//				getTable().scrollToVisibleCell(row, indexSubject);
//				getTable().setCellSelected(row, indexSubject);
//				return false;
//			}
//			
//			//--- create DtBase
//			DtBase base;
//			try {
//				base = new DtBase(name, type.toString(), attr, subject);
//			} catch (Throwable ex) {
//				String errmsg = CommonMessages.formatErrorMessage(getErrorIllegalDataTypeKey(row, labelTypeColumn), ex);
//				AppLogger.debug(errmsg, ex);
//				Application.showErrorMessage(this, errmsg);
//				getTable().scrollToVisibleCell(row, indexType);
//				getTable().setCellSelected(row, indexType);
//				return false;
//			}
//			//--- check exists
//			if (!isIgnoreDtBaseMultiple() && map.containsKey(base)) {
//				Application.showErrorMessage(this, getErrorDtBaseMultiple(map.get(base), row));
//				getTable().scrollToVisibleCell(row, indexName);
//				getTable().setCellSelected(row, indexName);
//				return false;
//			}
//			map.put(base, row);
//		}
//		
//		// valid
//		return true;
//	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected JButton createApplyButton() {
		// no apply button
		return null;
	}

	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();
		
		this.setResizable(true);
		this.setStoreLocation(true);
		this.setStoreSize(true);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(320, 240);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
		setupEscapeKeyBind();
	}
	
	protected JComponent createFileNamePanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		pnl.add(_lblTargetFileName, gbc);
		
		gbc.insets = new Insets(0, 2, 0, 0);
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx++;
		pnl.add(_btnShowTargetFile, gbc);
		
		return pnl;
	}
	
	protected JComponent createDataRecordRangePanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder((String)null),
				BorderFactory.createEmptyBorder(0, 3, 3, 3)));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		
		// label
		JLabel label;
		int maxLabelWidth = 0;
		gbc.insets = new Insets(3, 0, 0, 2);
		label = new JLabel(RunnerMessages.getInstance().ChartLabel_TotalRecordCount);
		maxLabelWidth = Math.max(maxLabelWidth, label.getPreferredSize().width);
		pnl.add(label, gbc);
		gbc.gridy++;
		label = new JLabel(RunnerMessages.getInstance().ChartLabel_HeaderRecordCount);
		maxLabelWidth = Math.max(maxLabelWidth, label.getPreferredSize().width);
		pnl.add(label, gbc);
		gbc.gridy++;
		label = new JLabel(RunnerMessages.getInstance().ChartLabel_FirstDataRecord);
		maxLabelWidth = Math.max(maxLabelWidth, label.getPreferredSize().width);
		pnl.add(label, gbc);
		gbc.gridy++;
		label = new JLabel(RunnerMessages.getInstance().ChartLabel_LastDataRecord);
		maxLabelWidth = Math.max(maxLabelWidth, label.getPreferredSize().width);
		pnl.add(label, gbc);
		gbc.gridy++;
		label = new JLabel(RunnerMessages.getInstance().ChartLabel_DataRecordCount);
		maxLabelWidth = Math.max(maxLabelWidth, label.getPreferredSize().width);
		pnl.add(label, gbc);
		
		// control
		gbc.gridy = 0;
		gbc.gridx = 1;
		gbc.insets = new Insets(3, 0, 0, 0);
		_lblTotalRecordCount.setText(String.valueOf(Long.MAX_VALUE)+"  ");
		Dimension dmField = _lblTotalRecordCount.getPreferredSize();
		_lblTotalRecordCount.setMinimumSize(dmField);
		_lblTotalRecordCount.setPreferredSize(dmField);
		pnl.add(_lblTotalRecordCount, gbc);
		gbc.gridy++;
		_lblHeaderRecordCount.setMinimumSize(dmField);
		_lblHeaderRecordCount.setPreferredSize(dmField);
		pnl.add(_lblHeaderRecordCount, gbc);
		gbc.gridy++;
		_lblFirstRecordNumber.setMinimumSize(dmField);
		_lblFirstRecordNumber.setPreferredSize(dmField);
		pnl.add(_lblFirstRecordNumber, gbc);
		gbc.gridy++;
		_lblLastRecordNumber.setMinimumSize(dmField);
		_lblLastRecordNumber.setPreferredSize(dmField);
		pnl.add(_lblLastRecordNumber, gbc);
		gbc.gridy++;
		_lblDataRecordCount.setMinimumSize(dmField);
		_lblDataRecordCount.setPreferredSize(dmField);
		pnl.add(_lblDataRecordCount, gbc);
		gbc.gridy++;
		
		// button
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.NORTH;
		Dimension dmButton = _btnEditRecordRange.getPreferredSize();
		dmButton.width = maxLabelWidth + dmField.width + 2;
		_btnEditRecordRange.setMinimumSize(dmButton);
		_btnEditRecordRange.setPreferredSize(dmButton);
		pnl.add(_btnEditRecordRange, gbc);
		
		return pnl;
	}
	
	protected JComponent createDateTimeConfigPanel(JRadioButton btnDefault, JRadioButton btnCustom, JTextField fldFormat) {
		JPanel pnl = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Insets defInsets = gbc.insets;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		
		pnl.add(btnDefault, gbc);
		gbc.gridx++;
		gbc.insets = new Insets(0, 3, 0, 0);
		pnl.add(btnCustom, gbc);
		gbc.gridx++;
		gbc.insets = defInsets;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(fldFormat, gbc);
		
		return pnl;
	}
	
	protected JComponent createDataTypePanel(JRadioButton...buttons) {
		Box box = Box.createHorizontalBox();
		box.add(buttons[0]);
		for (int i = 1; i < buttons.length; ++i) {
			box.add(Box.createHorizontalStrut(3));
			box.add(buttons[i]);
		}
		box.add(Box.createHorizontalGlue());
		return box;
	}
	
	protected JPanel createSeriesTablePanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		
		// function panel
		Box box = Box.createHorizontalBox();
		box.add(new JLabel(RunnerMessages.getInstance().ChartLabel_Series + ":  "));
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel(RunnerMessages.getInstance().ChartConfigDlg_func_title));
		box.add(Box.createHorizontalStrut(3));
		box.add(_cmbFunctionType);
		box.add(Box.createHorizontalStrut(3));
		box.add(_btnFunctionExec);
		box.setBorder(BorderFactory.createEmptyBorder(5, 0, 3, 0));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(box, gbc);
		
		// table panel
		gbc.gridheight = 5;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sc.setViewportView(_seriesTable);
		pnl.add(sc, gbc);
		
		// table buttons
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 2, 2, 0);
		//--- add button
		pnl.add(_btnSeriesAdd, gbc);
		gbc.gridy++;
		//--- edit button
		pnl.add(_btnSeriesEdit, gbc);
		gbc.gridy++;
		//--- delete button
		pnl.add(_btnSeriesDelete, gbc);
		gbc.gridy++;
		//--- move up button
		pnl.add(_btnSeriesMoveUp, gbc);
		gbc.gridy++;
		//--- move down button
		pnl.add(_btnSeriesMoveDown, gbc);
		
		return pnl;
	}

	@Override
	protected void setupMainContents() {
		// create main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		
		// labels
		gbc.insets = new Insets(0, 0, 0, 2);
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartConfigDlg_target_filename), gbc);
		gbc.gridy++;
		gbc.insets = new Insets(5, 0, 0, 2);
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartConfigDlg_ChartType), gbc);
		gbc.gridy++;
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartLabel_Title), gbc);
		gbc.gridy++;
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartLabel_LabelOfXaxis), gbc);
		gbc.gridy++;
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartLabel_LabelOfYaxis), gbc);
		gbc.gridy++;
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartConfigDlg_InvalidValuePolicy), gbc);
		gbc.gridy++;
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartConfigDlg_DateTimeFormat), gbc);
		gbc.gridy++;
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartConfigDlg_HorzDataType), gbc);
		gbc.gridy++;
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartConfigDlg_VertDataType), gbc);
		
		// fields-1
		gbc.insets = new Insets(0,0,0,0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridy = 0;
		gbc.gridx = 1;
		mainPanel.add(createFileNamePanel(), gbc);
		gbc.gridy++;
		gbc.insets = new Insets(5, 0, 0, 0);
		mainPanel.add(_cmbChartTypes, gbc);
		gbc.gridy++;
		mainPanel.add(_txtChartTitle, gbc);
		gbc.gridy++;
		mainPanel.add(_txtChartHorzTitle, gbc);
		gbc.gridy++;
		mainPanel.add(_txtChartVertTitle, gbc);
		gbc.gridy++;
		mainPanel.add(_cmbInvalidValuePolicy, gbc);
		
		// data record range
		gbc.insets = new Insets(0, 10, 0, 0);
		gbc.weightx = 0;
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridheight = 6;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		JComponent compRecordRange = createDataRecordRangePanel();
		mainPanel.add(compRecordRange, gbc);
		gbc.gridy = 6;
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		
		// field-2
		gbc.insets = new Insets(5,0,0,0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		ButtonGroup bgrp = new ButtonGroup();
		bgrp.add(_rdoTimeUseDefault);
		bgrp.add(_rdoTimeCustom);
		JComponent component = createDateTimeConfigPanel(_rdoTimeUseDefault, _rdoTimeCustom, _txtDateTimeFormat);
		mainPanel.add(component, gbc);
		gbc.gridy++;
		bgrp = new ButtonGroup();
		bgrp.add(_rdoHorzDataTypeDecimal);
		bgrp.add(_rdoHorzDataTypeTime);
		bgrp.add(_rdoHorzDataTypeText);
		component = createDataTypePanel(_rdoHorzDataTypeDecimal, _rdoHorzDataTypeTime, _rdoHorzDataTypeText);
		mainPanel.add(component, gbc);
		gbc.gridy++;
		bgrp = new ButtonGroup();
		bgrp.add(_rdoVertDataTypeDecimal);
		bgrp.add(_rdoVertDataTypeTime);
		bgrp.add(_rdoVertDataTypeText);
		component = createDataTypePanel(_rdoVertDataTypeDecimal, _rdoVertDataTypeTime, _rdoVertDataTypeText);
		mainPanel.add(component, gbc);
		gbc.gridy++;
		
		// table
		gbc.gridwidth = 3;
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(10, 0, 0, 0);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(createSeriesTablePanel(), gbc);

		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	//------------------------------------------------------------
	// Create components
	//------------------------------------------------------------

	protected void createContentComponents() {
		_lblTotalRecordCount  = createNumberStaticLabel();
		_lblDataRecordCount   = createNumberStaticLabel();
		_lblHeaderRecordCount = createNumberStaticLabel();
		_lblFirstRecordNumber = createNumberStaticLabel();
		_lblLastRecordNumber  = createNumberStaticLabel();
		_btnEditRecordRange = new JButton(RunnerMessages.getInstance().ChartConfigDlg_EditDataRecordRange_title);
		
		_lblTargetFileName = createStaticLabel();
		_btnShowTargetFile = CommonResources.createIconButton(CommonResources.ICON_CSVFILE, RunnerMessages.getInstance().ChartViewDlg_tooltip_CsvView);
		
		_cmbChartTypes = createChartStylesComboBox();
		_cmbInvalidValuePolicy = createInvalidValuePolicyComboBox();
		_txtChartTitle = new JTextField();
		_txtChartHorzTitle = new JTextField();
		_txtChartVertTitle = new JTextField();
		_txtDateTimeFormat = new JTextField();
		
		_rdoTimeUseDefault = new JRadioButton(CommonMessages.getInstance().labelDefault);
		_rdoTimeCustom     = new JRadioButton(RunnerMessages.getInstance().ChartConfigDlg_CustomDateTimeFormat);
		_rdoHorzDataTypeDecimal = new JRadioButton(RunnerMessages.getInstance().ChartLabel_DataType_decimal);
		_rdoVertDataTypeDecimal = new JRadioButton(RunnerMessages.getInstance().ChartLabel_DataType_decimal);
		_rdoHorzDataTypeTime    = new JRadioButton(RunnerMessages.getInstance().ChartLabel_DataType_datetime);
		_rdoVertDataTypeTime    = new JRadioButton(RunnerMessages.getInstance().ChartLabel_DataType_datetime);
		_rdoHorzDataTypeText    = new JRadioButton(RunnerMessages.getInstance().ChartLabel_DataType_text);
		_rdoVertDataTypeText    = new JRadioButton(RunnerMessages.getInstance().ChartLabel_DataType_text);
		
		_cmbFunctionType = createSeriesFunctionComboBox();
		_btnFunctionExec = new JButton(RunnerMessages.getInstance().ChartConfigDlg_func_exec);
		_btnSeriesAdd      = CommonResources.createIconButton(CommonResources.ICON_ADD, CommonMessages.getInstance().Button_Add);
		_btnSeriesEdit     = CommonResources.createIconButton(CommonResources.ICON_EDIT, CommonMessages.getInstance().Button_Edit);
		_btnSeriesDelete   = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		_btnSeriesMoveUp   = CommonResources.createIconButton(CommonResources.ICON_ARROW_UP, CommonMessages.getInstance().Button_Up);
		_btnSeriesMoveDown = CommonResources.createIconButton(CommonResources.ICON_ARROW_DOWN, CommonMessages.getInstance().Button_Down);
		SeriesTableModel tableModel = createSeriesTableModel();
		_seriesTable = createDataSeriesTable(tableModel);
	}
	
	protected JComboBox createSeriesFunctionComboBox() {
		JComboBox cmb = new JComboBox();
		cmb.addItem(RunnerMessages.getInstance().ChartConfigDlg_func_allpairs_repeated);
		cmb.addItem(RunnerMessages.getInstance().ChartConfigDlg_func_allpairs_norepeat);
		cmb.addItem(RunnerMessages.getInstance().ChartConfigDlg_func_vertfieldsselect);
		cmb.addItem(RunnerMessages.getInstance().ChartConfigDlg_func_horzfieldsselect);
		cmb.addItem(RunnerMessages.getInstance().ChartConfigDlg_func_vertfieldsreplace);
		cmb.addItem(RunnerMessages.getInstance().ChartConfigDlg_func_horzfieldsreplace);
		cmb.addItem(RunnerMessages.getInstance().ChartConfigDlg_func_deleteallseries);
		return cmb;
	}
	
	protected JLabel createStaticLabel() {
		JLabel label = new JLabel();
		Border tfBorder = UIManager.getBorder("TextField.border");
		Insets tfInsets = UIManager.getInsets("TextField.margin");
		Color  tfColor  = UIManager.getColor("TextField.background");
		label.setBackground(tfColor);
		if (tfInsets != null) {
			label.setBorder(BorderFactory.createCompoundBorder(tfBorder,
					BorderFactory.createEmptyBorder(tfInsets.top, tfInsets.left, tfInsets.bottom, tfInsets.right)));
		} else {
			label.setBorder(tfBorder);
		}
		return label;
	}
	
	protected JLabel createNumberStaticLabel() {
		JLabel label = createStaticLabel();
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		return label;
	}
	
	protected ChartStyles getSelectedChartStyle() {
		int selidx = _cmbChartTypes.getSelectedIndex();
		switch (selidx) {
			case 0 : return ChartStyles.SCATTER;
			case 1 : return ChartStyles.LINE;
			default : return null;
		}
	}
	
	protected void setSelectedChartStyle(ChartStyles style) {
		int selidx;
		if (style == null) {
			selidx = -1;
		} else {
			switch (style) {
				case SCATTER :	selidx = 0; break;
				case LINE :		selidx = 1; break;
				default :		selidx = -1;
			}
		}
		_cmbChartTypes.setSelectedIndex(selidx);
	}
	
	protected JComboBox createChartStylesComboBox() {
		JComboBox cmb = new JComboBox();
		cmb.addItem(RunnerMessages.getInstance().ChartStyle_Scatter);
		cmb.addItem(RunnerMessages.getInstance().ChartStyle_Line);
		return cmb;
	}
	
	protected ChartInvalidValuePolicy getSelectedInvalidValuePolicy() {
		int selidx = _cmbInvalidValuePolicy.getSelectedIndex();
		switch (selidx) {
			case 0 : return ChartInvalidValuePolicy.SKIP_RECORD;
			case 1 : return ChartInvalidValuePolicy.AS_ZERO;
			case 2 : return ChartInvalidValuePolicy.CONNECTED;
			default : return null;
		}
	}
	
	protected void setSelectedInvalidValuePolicy(ChartInvalidValuePolicy policy) {
		int selidx;
		if (policy == null) {
			selidx = -1;
		} else {
			switch (policy) {
				case SKIP_RECORD :	selidx = 0; break;
				case AS_ZERO :		selidx = 1; break;
				case CONNECTED :	selidx = 2; break;
				default :			selidx = -1;
			}
		}
		_cmbInvalidValuePolicy.setSelectedIndex(selidx);
	}
	
	protected JComboBox createInvalidValuePolicyComboBox() {
		JComboBox cmb = new JComboBox();
		cmb.addItem(RunnerMessages.getInstance().ChartInvalidValuePolicy_Skip);
		cmb.addItem(RunnerMessages.getInstance().ChartInvalidValuePolicy_AsZero);
		cmb.addItem(RunnerMessages.getInstance().ChartInvalidValuePolity_Connected);
		return cmb;
	}
	
	protected SeriesTableModel createSeriesTableModel() {
		SeriesTableModel model = new SeriesTableModel();
		ArrayList<DefaultDataSeries> seriesList = _configModel.getDataSeriesList();
		for (DefaultDataSeries series : seriesList) {
			model.addRow(series, null, null);
		}
		return model;
	}
	
	protected SeriesTable createDataSeriesTable(SeriesTableModel model) {
		SeriesTable table = new SeriesTable(model);
		return table;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static protected class SeriesTableModel extends DefaultTableModel
	{
		private static final long serialVersionUID = 1L;
		static private final String[] _columnNames = {
			RunnerMessages.getInstance().ChartConfigDlg_seriescolumn_legend,
			RunnerMessages.getInstance().ChartConfigDlg_seriescolumn_Xaxis,
			RunnerMessages.getInstance().ChartConfigDlg_seriescolumn_Yaxis,
		};
		
		static public final int	LOGICAL_CI_LEGEND	= 0;
		static public final int	LOGICAL_CI_XFIELD	= 1;
		static public final int	LOGICAL_CI_YFIELD	= 2;
		
		static public final int	ACTUAL_CI_SERIES	= 0;
		static public final int	ACTUAL_CI_XERROR	= 1;
		static public final int	ACTUAL_CI_YERROR	= 2;
		
		public SeriesTableModel() {
			super();
		}

		@Override
		public void setColumnCount(int columnCount) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addColumn(Object columnName) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addColumn(Object columnName, Vector columnData) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addColumn(Object columnName, Object[] columnData) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getColumnCount() {
			return _columnNames.length;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Object.class;
		}

		@Override
		public String getColumnName(int column) {
			if (column >= 0 && column < _columnNames.length) {
				return _columnNames[column];
			} else {
				return super.getColumnName(column);
			}
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
		
		public String getXFieldError(int row) {
			return (String)super.getValueAt(row, ACTUAL_CI_XERROR);
		}
		
		public String getYFieldError(int row) {
			return (String)super.getValueAt(row, ACTUAL_CI_YERROR);
		}
		
		public void setXFieldError(int row, String error) {
			if (error == null)
				error = "";
			String oldError = getXFieldError(row);
			if (!error.equals(oldError)) {
				((Vector)dataVector.elementAt(row)).setElementAt(error, ACTUAL_CI_XERROR);
				fireTableCellUpdated(row, 1);
			}
		}
		
		public void setYFieldError(int row, String error) {
			if (error == null)
				error = "";
			String oldError = getYFieldError(row);
			if (!error.equals(oldError)) {
				((Vector)dataVector.elementAt(row)).setElementAt(error, ACTUAL_CI_YERROR);
				fireTableCellUpdated(row, 2);
			}
		}
		
		public DefaultDataSeries getSeries(int row) {
			DefaultDataSeries series = (DefaultDataSeries)super.getValueAt(row, ACTUAL_CI_SERIES);
			return series;
		}
		
		public void setSeries(int row, DefaultDataSeries series) {
			((Vector)dataVector.elementAt(row)).setElementAt(series, ACTUAL_CI_SERIES);
			fireTableRowsUpdated(row, row);
		}

		@Override
		public Object getValueAt(int row, int column) {
			if (column >= _columnNames.length) {
				throw new IndexOutOfBoundsException();
			}

			DefaultDataSeries series = (DefaultDataSeries)super.getValueAt(row, ACTUAL_CI_SERIES);
			if (column == LOGICAL_CI_LEGEND) {
				return series.getAvailableLegend();
			}
			else if (column == LOGICAL_CI_XFIELD) {
				return series.getXField();
			}
			else if (column == LOGICAL_CI_YFIELD) {
				return series.getYField();
			}
			else {
				return super.getValueAt(row, column);
			}
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			if (column >= _columnNames.length) {
				throw new IndexOutOfBoundsException();
			}
			Vector rowVector = (Vector)dataVector.elementAt(row);
			DefaultDataSeries series = (DefaultDataSeries)rowVector.elementAt(ACTUAL_CI_SERIES);
			if (column == LOGICAL_CI_LEGEND) {
				// Legend
				String strLegend = (aValue==null ? null : aValue.toString());
				if (!series.getDefaultLegend().equals(strLegend)) {
					series.setLegend(strLegend);
					fireTableCellUpdated(row, column);
				}
			}
			else if (column == LOGICAL_CI_XFIELD) {
				// X-Field
				ChartConfigDataFieldModel xField = (ChartConfigDataFieldModel)aValue;
				if (series.setXField(xField)) {
					rowVector.setElementAt(null, ACTUAL_CI_XERROR);
					fireTableRowsUpdated(row, row);
				}
			}
			else if (column == LOGICAL_CI_YFIELD) {
				// Y-Field
				ChartConfigDataFieldModel yField = (ChartConfigDataFieldModel)aValue;
				if (series.setYField(yField)) {
					rowVector.setElementAt(null, ACTUAL_CI_YERROR);
					fireTableRowsUpdated(row, row);
				}
			}
			else {
				// unknown
				super.setValueAt(aValue, row, column);
			}
		}
		
		public void addRow(String legend, ChartConfigDataFieldModel xField, ChartConfigDataFieldModel yField, String xFieldError, String yFieldError) {
			addRow(new DefaultDataSeries(legend, xField, yField), xFieldError, yFieldError);
		}
		
		public void addRow(DefaultDataSeries series, String xFieldError, String yFieldError) {
			super.addRow(new Object[]{series, xFieldError, yFieldError});
		}
		
		public void insertRow(int row, String legend, ChartConfigDataFieldModel xField, ChartConfigDataFieldModel yField, String xFieldError, String yFieldError) {
			insertRow(row, new DefaultDataSeries(legend, xField, yField), xFieldError, yFieldError);
		}
		
		public void insertRow(int row, DefaultDataSeries series, String xFieldError, String yFieldError) {
			super.insertRow(row, new Object[]{series, xFieldError, yFieldError});
		}
		
		public void removeAllRows() {
			if (!dataVector.isEmpty()) {
				int frow = 0;
				int lrow = dataVector.size() - 1;
				dataVector.clear();
				fireTableRowsDeleted(frow, lrow);
			}
		}
	}
	
	static protected class SeriesTableCellRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			Color bgColor = null;
			Object dispValue = value;
			String tooltip = null;
			
			if (column == SeriesTableModel.LOGICAL_CI_XFIELD || column == SeriesTableModel.LOGICAL_CI_YFIELD) {
				// x-field or y-field
				SeriesTableModel model = (SeriesTableModel)table.getModel();
				ChartConfigDataFieldModel field = (ChartConfigDataFieldModel)value;
				dispValue = field.getFieldName();
				String error = (column==SeriesTableModel.LOGICAL_CI_XFIELD ? model.getXFieldError(row) : column==SeriesTableModel.LOGICAL_CI_YFIELD ? model.getYFieldError(row) : null);
				if (error != null && error.length() > 0) {
					// has error
					tooltip = error;
					bgColor = CommonResources.DEF_BACKCOLOR_ERROR;
				}
			}

			// コンポーネント生成
			Component comp = super.getTableCellRendererComponent(table, dispValue, isSelected, hasFocus, row, column);
			
			// 背景色の設定
			if (!isSelected) {
				if (bgColor != null) {
					comp.setBackground(bgColor);
				} else {
					comp.setBackground(table.getBackground());
				}
			}
			
			// tooltip
			setToolTipText(tooltip);
			
			// 完了
			return comp;
		}
	}
	
	static protected class SeriesTable extends SpreadSheetTable
	{
		private static final long serialVersionUID = 1L;

		public SeriesTable(SeriesTableModel model) {
			super(model);
			setAdjustOnlyVisibleRows(false);			// 列幅の自動調整では全行を対象
			setAdjustOnlySelectedCells(false);			// 列幅の自動調整では選択セルのみに限定しない
			getTableRowHeader().setFixedCellWidth(50);	// 列幅を 50pixel に固定
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			setRowSelectionAllowed(true);
			setColumnSelectionAllowed(false);
			setDefaultRenderer(Object.class, new SeriesTableCellRenderer());
			getTableHeader().setReorderingAllowed(false);
			getTableHeader().setResizingAllowed(true);
			// テーブルの罫線を表示(Mac は白で書いております)
			setShowGrid(true);
			setGridColor(new Color(128,128,128));
		}

		@Override
		public SeriesTableModel getModel() {
			return (SeriesTableModel)super.getModel();
		}

		@Override
		public void setModel(TableModel dataModel) {
			if (!(dataModel instanceof SeriesTableModel))
				throw new IllegalArgumentException("Table model is not SeriesTableModel instance.");
			
			super.setModel(dataModel);
		}

		@Override
		protected TableModel createDefaultDataModel() {
			return new SeriesTableModel();
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
	}
}
