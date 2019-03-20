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
import java.util.Arrays;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import ssac.aadl.common.CommonResources;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;

/**
 * チャート描画対象データの複数フィールドを選択するダイアログ。
 * 
 * @version 2.1.0	2013/07/22
 * @since 2.1.0
 */
public class ChartMultiFieldSelectDialog extends AbBasicDialog
{
	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(480, 320);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** チャート描画設定モデル **/
	protected ChartConfigModel	_configModel;
	/** 選択列 **/
	protected int[]			_selectedFields;
	/** 組合せ生成の場合は <tt>true</tt> **/
	protected boolean			_genPairs;
	/** リストコンポーネント **/
	protected JList		_lstFields;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ChartMultiFieldSelectDialog(Frame owner, String title, final ChartConfigModel chartModel, boolean genPairs, int...initSelectedFields) {
		super(owner, title, true);
		if (chartModel == null)
			throw new NullPointerException("The specified ChartConfigModel object is null.");
		_configModel = chartModel;
		_selectedFields = initSelectedFields;
		_genPairs = genPairs;
		setConfiguration(AppSettings.CHARTMULTIFIELDSELECT_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public ChartMultiFieldSelectDialog(Dialog owner, String title, final ChartConfigModel chartModel, boolean genPairs, int...initSelectedFields) {
		super(owner, title, true);
		if (chartModel == null)
			throw new NullPointerException("The specified ChartConfigModel object is null.");
		_configModel = chartModel;
		_selectedFields = initSelectedFields;
		_genPairs = genPairs;
		setConfiguration(AppSettings.CHARTMULTIFIELDSELECT_DLG, AppSettings.getInstance().getConfiguration());
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
		if (_selectedFields != null && _selectedFields.length > 0) {
			_lstFields.clearSelection();
			_lstFields.setSelectedIndices(_selectedFields);
			_lstFields.scrollRectToVisible(_lstFields.getCellBounds(_selectedFields[0], _selectedFields[0]));
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ChartConfigModel getConfigModel() {
		return _configModel;
	}
	
	public int[] getSelectedIndices() {
		return _selectedFields;
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected boolean doOkAction() {
		// check selection
		int[] selrows = _lstFields.getSelectedIndices();
		if (_genPairs) {
			if (selrows == null || selrows.length < 2) {
				// not enough
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigGenPairsNumFieldsNotEnough);
				return false;
			}
		}
		else {
			if (selrows == null || selrows.length < 1) {
				// no selection
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgChartConfigMultiDataFieldsNoSelected);
				return false;
			}
		}
		
		// update selected indices
		_selectedFields = _lstFields.getSelectedIndices();
		Arrays.sort(_selectedFields);
		
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
		// create main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sc.setViewportView(_lstFields);
		
		mainPanel.add(sc, BorderLayout.CENTER);

		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	//------------------------------------------------------------
	// Create components
	//------------------------------------------------------------

	protected void createContentComponents() {
		_lstFields = new JList(new FieldListModel());
		_lstFields.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class FieldListModel extends AbstractListModel
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Object getElementAt(int index) {
			return _configModel.getFieldModel(index);
		}

		@Override
		public int getSize() {
			return _configModel.getFieldCount();
		}
	}
}
