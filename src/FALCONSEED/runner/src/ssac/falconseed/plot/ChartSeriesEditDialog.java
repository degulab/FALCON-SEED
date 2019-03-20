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
 * @(#)ChartSeriesEditDialog.java	2.1.0	2013/07/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.plot;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.plot.ChartConfigModel.ChartConfigDataFieldModel;
import ssac.falconseed.plot.ChartConfigModel.ChartConfigRecordNumberField;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.plot.DefaultDataSeries;

/**
 * チャートのデータ系列の内容を編集するダイアログ。
 * 
 * @version 2.1.0	2013/07/19
 * @since 2.1.0
 */
public class ChartSeriesEditDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	static private final Dimension DM_MIN_SIZE = new Dimension(480, 320);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** チャート描画設定モデル **/
	protected ChartConfigModel		_configModel;
	protected DefaultDataSeries	_series;

	private JRadioButton	_rdoDefaultLegend;
	private JRadioButton	_rdoCustomLegend;
	private JLabel			_lblDefLegend;
	private JTextField		_fldLegend;
	private JComboBox		_cmbHorzField;
	private JComboBox		_cmbVertField;
	private JRadioButton	_rdoHorzRecordNumber;
	private JRadioButton	_rdoHorzChooseField;
	private JRadioButton	_rdoVertRecordNumber;
	private JRadioButton	_rdoVertChooseField;
	private ButtonGroup	_grpLegend;
	private ButtonGroup	_grpHorz;
	private ButtonGroup	_grpVert;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ChartSeriesEditDialog(Frame owner, String title, final ChartConfigModel chartModel, final DefaultDataSeries targetSeries) {
		super(owner, title, true);
		if (chartModel == null)
			throw new NullPointerException("The specified ChartConfigModel object is null.");
		_configModel = chartModel;
		_series = targetSeries;
		setConfiguration(AppSettings.CHARTSERIESEDIT_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public ChartSeriesEditDialog(Dialog owner, String title, final ChartConfigModel chartModel, final DefaultDataSeries targetSeries) {
		super(owner, title, true);
		if (chartModel == null)
			throw new NullPointerException("The specified ChartConfigModel object is null.");
		_configModel = chartModel;
		_series = targetSeries;
		setConfiguration(AppSettings.CHARTSERIESEDIT_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	public void initialComponent() {
		// create content components
		createContentComponents();
		
		// initial component
		super.initialComponent();
		
		// restore settings
		restoreConfiguration();
		
		// データを反映
		ChartConfigDataFieldModel xField;
		ChartConfigDataFieldModel yField;
		if (_series == null) {
			// create default
			xField = _configModel.getDefaultRecordNumberField();
			yField = _configModel.getDefaultRecordNumberField();
			_series = new DefaultDataSeries(xField, yField);
		}
		else {
			xField = (ChartConfigDataFieldModel)_series.getXField();
			if (xField == null)
				xField = _configModel.getDefaultRecordNumberField();
			
			yField = (ChartConfigDataFieldModel)_series.getYField();
			if (yField == null)
				yField = _configModel.getDefaultRecordNumberField();
		}

		String strLegend = _series.getLegend();
		if (strLegend == null) {
			_rdoDefaultLegend.setSelected(true);
			_fldLegend.setEnabled(false);
		} else {
			_rdoCustomLegend.setSelected(true);
			_fldLegend.setEnabled(true);
			_fldLegend.setText(_series.getLegend());
		}
		
		if (xField == null || xField instanceof ChartConfigRecordNumberField) {
			_rdoHorzRecordNumber.setSelected(true);
			_cmbHorzField.setSelectedIndex(0);
			_cmbHorzField.setEnabled(false);
		} else {
			_rdoHorzChooseField.setSelected(true);
			_cmbHorzField.setSelectedIndex(xField.getFieldIndex());
			_cmbHorzField.setEnabled(true);
		}
		
		if (yField == null || yField instanceof ChartConfigRecordNumberField) {
			_rdoVertRecordNumber.setSelected(true);
			_cmbVertField.setSelectedIndex(0);
			_cmbVertField.setEnabled(false);
		} else {
			_rdoVertChooseField.setSelected(true);
			_cmbVertField.setSelectedIndex(yField.getFieldIndex());
			_cmbVertField.setEnabled(true);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ChartConfigModel getConfigModel() {
		return _configModel;
	}
	
	public DefaultDataSeries getDataSeries() {
		return _series;
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected boolean doOkAction() {
		// check xField
		ChartConfigDataFieldModel xField = null;
		if (_rdoHorzChooseField.isSelected()) {
			if (_cmbHorzField.getSelectedIndex() >= 0) {
				xField = _configModel.getFieldModel(_cmbHorzField.getSelectedIndex());
			} else {
				// no selection error
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigHorzDataFieldNoSelected);
				return false;
			}
		} else {
			xField = _configModel.getDefaultRecordNumberField();
		}
		
		// check yField
		ChartConfigDataFieldModel yField = null;
		if (_rdoVertChooseField.isSelected() && _cmbVertField.getSelectedIndex() >= 0) {
			if (_cmbVertField.getSelectedIndex() >= 0) {
				yField = _configModel.getFieldModel(_cmbVertField.getSelectedIndex());
			} else {
				// no selection error
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigVertDataFieldNoSelected);
				return false;
			}
		} else {
			yField = _configModel.getDefaultRecordNumberField();
		}
		
		// update series
		String strLegend = null;
		if (_rdoCustomLegend.isSelected()) {
			strLegend = _fldLegend.getText();
		}
		_series.setLegend(strLegend);
		_series.setXField(xField);
		_series.setYField(yField);
		
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String trimString(String str) {
		if (str == null) {
			return null;
		} else {
			return str.trim();
		}
	}

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
		this.setStoreLocation(false);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200, 300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
	}

	@Override
	protected void setupMainContents() {
		// create sub panels
		JPanel pnlLege = createLegendEditPanel();
		JPanel pnlHorz = createDataSelectPanel(RunnerMessages.getInstance().ChartLabel_Xaxis,
							_rdoHorzRecordNumber, _rdoHorzChooseField, _cmbHorzField);
		JPanel pnlVert = createDataSelectPanel(RunnerMessages.getInstance().ChartLabel_Yaxis,
							_rdoVertRecordNumber, _rdoVertChooseField, _cmbVertField);
		
		// create main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		mainPanel.add(pnlLege, gbc);
		gbc.gridy++;
		
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 0, 0, 0);
		mainPanel.add(pnlHorz, gbc);
		gbc.gridy++;
		
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		mainPanel.add(pnlVert, gbc);

		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	//------------------------------------------------------------
	// Create components
	//------------------------------------------------------------

	protected void createContentComponents() {
		_rdoDefaultLegend = new JRadioButton(CommonMessages.getInstance().labelDefault);
		_rdoCustomLegend  = new JRadioButton(RunnerMessages.getInstance().ChartSeriesEditDlg_CustomLegend);
		_rdoCustomLegend.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				_fldLegend.setEnabled(_rdoCustomLegend.isSelected());
			}
		});
		_rdoHorzRecordNumber = new JRadioButton(RunnerMessages.getInstance().ChartLabel_RecordNumber);
		_rdoVertRecordNumber = new JRadioButton(RunnerMessages.getInstance().ChartLabel_RecordNumber);
		_rdoHorzChooseField  = new JRadioButton(RunnerMessages.getInstance().ChartLabel_Field);
		_rdoHorzChooseField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				_cmbHorzField.setEnabled(_rdoHorzChooseField.isSelected());
			}
		});
		_rdoVertChooseField  = new JRadioButton(RunnerMessages.getInstance().ChartLabel_Field);
		_rdoVertChooseField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				_cmbVertField.setEnabled(_rdoVertChooseField.isSelected());
			}
		});
		_cmbHorzField = new JComboBox(_configModel.createFieldComboBoxModel());
		_cmbVertField = new JComboBox(_configModel.createFieldComboBoxModel());

		_lblDefLegend = new JLabel();
		_fldLegend = new JTextField();
		_fldLegend.setEditable(true);
		
		_grpLegend = new ButtonGroup();
		_grpLegend.add(_rdoDefaultLegend);
		_grpLegend.add(_rdoCustomLegend);
		
		_grpHorz = new ButtonGroup();
		_grpHorz.add(_rdoHorzRecordNumber);
		_grpHorz.add(_rdoHorzChooseField);
		
		_grpVert = new ButtonGroup();
		_grpVert.add(_rdoVertRecordNumber);
		_grpVert.add(_rdoVertChooseField);
	}
	
	protected JPanel createLegendEditPanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0,0,3,0);

		pnl.add(new JLabel(RunnerMessages.getInstance().ChartLabel_Legend), gbc);
		gbc.gridx++;
		gbc.gridy = 0;

		gbc.insets = new Insets(0, 3, 3, 0);
		pnl.add(_rdoDefaultLegend, gbc);
		gbc.gridy++;
		pnl.add(_rdoCustomLegend, gbc);
		gbc.gridx++;
		gbc.gridy = 0;

		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(_lblDefLegend, gbc);
		gbc.gridy++;
		pnl.add(_fldLegend, gbc);
		
		return pnl;
	}
	
	protected JPanel createDataSelectPanel(String groupLabel, JRadioButton rdoRecNum, JRadioButton rdoField, JComboBox cmbField) {
		JPanel pnl = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(2,2,2,2);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		gbc.gridwidth = 2;
		pnl.add(rdoRecNum, gbc);
		gbc.gridy++;
		
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		pnl.add(rdoField, gbc);
		gbc.gridx++;
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnl.add(cmbField, gbc);
		
		pnl.setBorder(BorderFactory.createTitledBorder(groupLabel));
		
		return pnl;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
