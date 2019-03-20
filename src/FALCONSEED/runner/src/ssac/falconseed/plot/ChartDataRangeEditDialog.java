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
 * @(#)ChartDataRangeEditDialog.java	2.1.0	2013/08/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ssac.aadl.common.CommonResources;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.JMaskedNumberSpinner;

/**
 * チャートのデータレコード範囲を編集するダイアログ。
 * 
 * @version 2.1.0	2013/08/14
 * @since 2.1.0
 */
public class ChartDataRangeEditDialog extends AbBasicDialog
{
	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(480, 320);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private long	_totalRecordCount;
	private long	_headerRecordCount;
	private long	_dataRecordCount;
	private long	_firstRecordIndex;	// include index
	private long	_lastRecordIndex;	// not include index

	private JLabel					_lblTotalRecordCount;
	private JLabel					_lblDataRecordCount;
	private JMaskedNumberSpinner	_spnHeaderRecordCount;
	private JMaskedNumberSpinner	_spnFirstDataRecordNumber;
	private JMaskedNumberSpinner	_spnLastDataRecordNumber;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ChartDataRangeEditDialog(Frame owner, long lRecordCount, long lHeaderRecordCount, long lFirstDataRecordIndex, long lLastDataRecordIndex) {
		super(owner, RunnerMessages.getInstance().ChartDataRangeEditDlg_title, true);
		_totalRecordCount  = lRecordCount;
		_headerRecordCount = lHeaderRecordCount;
		_firstRecordIndex  = lFirstDataRecordIndex;
		_lastRecordIndex   = lLastDataRecordIndex;
		_dataRecordCount   = lLastDataRecordIndex - lFirstDataRecordIndex;
		setConfiguration(AppSettings.CHARTDATARANGEEDIT_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public ChartDataRangeEditDialog(Dialog owner, long lRecordCount, long lHeaderRecordCount, long lFirstDataRecordIndex, long lLastDataRecordIndex) {
		super(owner, RunnerMessages.getInstance().ChartDataRangeEditDlg_title, true);
		_totalRecordCount  = lRecordCount;
		_headerRecordCount = lHeaderRecordCount;
		_firstRecordIndex  = lFirstDataRecordIndex;
		_lastRecordIndex   = lLastDataRecordIndex;
		_dataRecordCount   = lLastDataRecordIndex - lFirstDataRecordIndex;
		setConfiguration(AppSettings.CHARTDATARANGEEDIT_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	public void initialComponent() {
		// create content components
		createContentComponents();
		
		// initial component
		super.initialComponent();
		
		// restore settings
		//restoreConfiguration();
		
		// データを反映
		_lblTotalRecordCount.setText(String.valueOf(_totalRecordCount));
		_lblDataRecordCount.setText(String.valueOf(_dataRecordCount));
		_spnHeaderRecordCount.setValue(new Long(_headerRecordCount));
		_spnFirstDataRecordNumber.setValue(new Long(_firstRecordIndex+1L));
		_spnLastDataRecordNumber.setValue(new Long(_lastRecordIndex));
		
		// setup actions
		ChangeListener cl = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				long lFirst = ((Number)_spnFirstDataRecordNumber.getValue()).longValue();
				long lLast  = ((Number)_spnLastDataRecordNumber.getValue()).longValue();
				_lblDataRecordCount.setText(String.valueOf(lLast - lFirst + 1L));
			}
		};
		_spnFirstDataRecordNumber.addChangeListener(cl);
		_spnLastDataRecordNumber .addChangeListener(cl);
		
		pack();
		setLocationRelativeTo(getOwner());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public long getTotalRecordCount() {
		return _totalRecordCount;
	}
	
	public long getHeaderRecordCount() {
		return _headerRecordCount;
	}
	
	public long getDataRecordCount() {
		return _dataRecordCount;
	}
	
	public long getFirstDataRecordIndex() {
		return _firstRecordIndex;
	}
	
	public long getLastDataRecordIndex() {
		return _lastRecordIndex;
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected boolean doOkAction() {
		// check values
		long lHeaderRecordCount = ((Number)_spnHeaderRecordCount.getValue()).longValue();
		long lFirstRecordNumber = ((Number)_spnFirstDataRecordNumber.getValue()).longValue();
		long lLastRecordNumber  = ((Number)_spnLastDataRecordNumber.getValue()).longValue();
		if (lHeaderRecordCount >= lFirstRecordNumber) {
			// データレコード範囲とヘッダーレコード範囲が重複
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigFirstDataRecordIllegal);
			_spnFirstDataRecordNumber.requestFocusInWindow();
			return false;
		}
		else if (lFirstRecordNumber > lLastRecordNumber) {
			// データレコード範囲が無効(First > Last)
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigLastDataRecordIllegal);
			_spnLastDataRecordNumber.requestFocusInWindow();
			return false;
		}
		
		// update value
		_headerRecordCount = lHeaderRecordCount;
		_firstRecordIndex  = lFirstRecordNumber - 1L;
		_lastRecordIndex   = lLastRecordNumber;
		_dataRecordCount   = lLastRecordNumber - lFirstRecordNumber + 1L;
		
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
		
		this.setResizable(false);
		this.setStoreLocation(false);
		this.setStoreSize(false);
//		//--- setup minimum size
//		Dimension dmMin = getDefaultSize();
//		if (dmMin == null) {
//			dmMin = new Dimension(200, 300);
//		}
//		setMinimumSize(dmMin);
		setKeepMinimumSize(false);
	}

	@Override
	protected void setupMainContents() {
		// create main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// setup minimum size
		JMaskedNumberSpinner spn = new JMaskedNumberSpinner("#0", Long.valueOf(Long.MAX_VALUE), Long.valueOf(0L), Long.valueOf(Long.MAX_VALUE), Long.valueOf(1L));
		JLabel lbl = createStaticLabel();
		lbl.setText(String.valueOf(Long.MAX_VALUE));
		Dimension dmSpin  = spn.getPreferredSize();
		Dimension dmLabel = lbl.getPreferredSize();
		int maxWidth = Math.max(dmSpin.width, dmLabel.width);
		dmSpin .width = maxWidth;
		dmLabel.width = maxWidth;
		_lblTotalRecordCount.setMinimumSize(dmLabel);
		_lblTotalRecordCount.setPreferredSize(dmLabel);
		_lblDataRecordCount.setMinimumSize(dmLabel);
		_lblDataRecordCount.setPreferredSize(dmLabel);
		_spnHeaderRecordCount.setMinimumSize(dmSpin);
		_spnHeaderRecordCount.setPreferredSize(dmSpin);
		_spnFirstDataRecordNumber.setMinimumSize(dmSpin);
		_spnFirstDataRecordNumber.setPreferredSize(dmSpin);
		_spnLastDataRecordNumber.setMinimumSize(dmSpin);
		_spnLastDataRecordNumber.setPreferredSize(dmSpin);

		// layout
		Insets inLabelSpan = new Insets(5, 0, 5, 0);
		Insets inFieldSpan = new Insets(5, 3, 5, 0);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		
		// create labels
		gbc.insets = inLabelSpan;
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartLabel_TotalRecordCount), gbc);
		gbc.gridy++;
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartLabel_HeaderRecordCount), gbc);
		gbc.gridy++;
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartLabel_FirstDataRecord), gbc);
		gbc.gridy++;
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartLabel_LastDataRecord), gbc);
		gbc.gridy++;
		mainPanel.add(new JLabel(RunnerMessages.getInstance().ChartLabel_DataRecordCount), gbc);
		
		// add fields
		gbc.insets = inFieldSpan;
		gbc.gridy = 0;
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		mainPanel.add(_lblTotalRecordCount, gbc);
		gbc.gridy++;
		mainPanel.add(_spnHeaderRecordCount, gbc);
		gbc.gridy++;
		mainPanel.add(_spnFirstDataRecordNumber, gbc);
		gbc.gridy++;
		mainPanel.add(_spnLastDataRecordNumber, gbc);
		gbc.gridy++;
		mainPanel.add(_lblDataRecordCount, gbc);

		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	//------------------------------------------------------------
	// Create components
	//------------------------------------------------------------

	protected void createContentComponents() {
		_lblTotalRecordCount = createStaticLabel();
		_lblDataRecordCount  = createStaticLabel();
		
		_spnHeaderRecordCount = new JMaskedNumberSpinner("#0", Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(_totalRecordCount), Long.valueOf(1L));
		_spnFirstDataRecordNumber = new JMaskedNumberSpinner("#0", Long.valueOf(1L), Long.valueOf(1L), Long.valueOf(_totalRecordCount), Long.valueOf(1L));
		_spnLastDataRecordNumber  = new JMaskedNumberSpinner("#0", Long.valueOf(1L), Long.valueOf(1L), Long.valueOf(_totalRecordCount), Long.valueOf(1L));
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
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		return label;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
