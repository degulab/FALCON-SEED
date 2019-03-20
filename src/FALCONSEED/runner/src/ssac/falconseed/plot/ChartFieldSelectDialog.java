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
 * @(#)ChartFieldSelectDialog.java	2.1.0	2013/07/22
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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ssac.aadl.common.CommonResources;
import ssac.falconseed.plot.ChartConfigModel.ChartConfigDataFieldModel;
import ssac.falconseed.plot.ChartConfigModel.ChartConfigRecordNumberField;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;

/**
 * チャート描画対象データのフィールドを選択するダイアログ。
 * 
 * @version 2.1.0	2013/07/22
 * @since 2.1.0
 */
public class ChartFieldSelectDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	static private final Dimension DM_MIN_SIZE = new Dimension(480, 150);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** チャート描画設定モデル **/
	protected ChartConfigModel		_configModel;
	protected ChartConfigDataFieldModel	_field;

	private JComboBox		_cmbField;
	private JRadioButton	_rdoRecordNumber;
	private JRadioButton	_rdoChooseField;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ChartFieldSelectDialog(Frame owner, String title, final ChartConfigModel chartModel, final ChartConfigDataFieldModel initField) {
		super(owner, title, true);
		if (chartModel == null)
			throw new NullPointerException("The specified ChartConfigModel object is null.");
		_configModel = chartModel;
		_field = initField;
		setConfiguration(AppSettings.CHARTFIELDSELECT_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public ChartFieldSelectDialog(Dialog owner, String title, final ChartConfigModel chartModel, final ChartConfigDataFieldModel initField) {
		super(owner, title, true);
		if (chartModel == null)
			throw new NullPointerException("The specified ChartConfigModel object is null.");
		_configModel = chartModel;
		_field = initField;
		setConfiguration(AppSettings.CHARTFIELDSELECT_DLG, AppSettings.getInstance().getConfiguration());
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
		if (_field == null || _field instanceof ChartConfigRecordNumberField) {
			_rdoRecordNumber.setSelected(true);
			_cmbField.setSelectedIndex(0);
			_cmbField.setEnabled(false);
		} else {
			_rdoChooseField.setSelected(true);
			_cmbField.setSelectedIndex(_field.getFieldIndex());
			_cmbField.setEnabled(true);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ChartConfigModel getConfigModel() {
		return _configModel;
	}
	
	public ChartConfigDataFieldModel getSelectedField() {
		return _field;
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected boolean doOkAction() {
		// check Field
		if (_rdoChooseField.isSelected()) {
			if (_cmbField.getSelectedIndex() >= 0) {
				_field = _configModel.getFieldModel(_cmbField.getSelectedIndex());
			} else {
				// no selection error
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigDataFieldNoSelected);
				return false;
			}
		} else {
			_field = _configModel.getDefaultRecordNumberField();
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

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
		//this.setStoreSize(false);
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
		// create main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
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
		mainPanel.add(_rdoRecordNumber, gbc);
		gbc.gridy++;
		
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		mainPanel.add(_rdoChooseField, gbc);
		gbc.gridx++;
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		mainPanel.add(_cmbField, gbc);

		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	//------------------------------------------------------------
	// Create components
	//------------------------------------------------------------

	protected void createContentComponents() {
		_rdoRecordNumber = new JRadioButton(RunnerMessages.getInstance().ChartLabel_RecordNumber);
		_rdoChooseField  = new JRadioButton(RunnerMessages.getInstance().ChartLabel_Field);
		_rdoChooseField.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				_cmbField.setEnabled(_rdoChooseField.isSelected());
			}
		});
		_cmbField = new JComboBox(_configModel.createFieldComboBoxModel());
		
		ButtonGroup bgrp = new ButtonGroup();
		bgrp.add(_rdoRecordNumber);
		bgrp.add(_rdoChooseField);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
