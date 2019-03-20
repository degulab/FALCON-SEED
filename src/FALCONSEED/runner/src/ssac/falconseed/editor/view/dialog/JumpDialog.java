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
 * @(#)JumpDialog.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.swing.BasicDialog;

/**
 * 行選択ダイアログ
 * 
 * @version 1.00	2010/12/20
 */
public class JumpDialog extends BasicDialog
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(240, 120);
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final int	maxLineNo;
	
	private SpinnerNumberModel spinModel;
	private JSpinner			spin;
	
	private JLabel				lblDesc;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JumpDialog(int maxLines) {
		this(null, maxLines);
	}
	
	public JumpDialog(Frame owner, int maxLines) {
		super(owner, RunnerMessages.getInstance().JumpDlg_Title_Main, true);
		
		// setup lineNo
		if (maxLines > 1) {
			this.maxLineNo = maxLines;
		} else {
			this.maxLineNo = 1;
		}
		this.spinModel.setMaximum(this.maxLineNo);
		String strDesc = RunnerMessages.getInstance().JumpDlg_Title_Desc + "(1..." + this.maxLineNo + ")";
		this.lblDesc.setText(strDesc);
		
		// 設定
		this.setResizable(true);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200,300);
		}
		this.setMinimumSize(dmMin);
		setKeepMinimumSize(true);
		
		// restore settings
		loadSettings();
	}
	
	protected void setupMainPanel() {
		super.setupMainPanel();
		
		this.mainPanel.setLayout(new GridBagLayout());
		
		// Label
		lblDesc = new JLabel(RunnerMessages.getInstance().JumpDlg_Title_Desc);
		
		// Spin
		this.spinModel = new SpinnerNumberModel(1, 1, 999999, 1);
		this.spin = new JSpinner(spinModel);

		// Layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2,2,0,2);
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- label
		this.mainPanel.add(lblDesc, gbc);
		//--- spin
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.mainPanel.add(spin, gbc);
	}
	
	protected void setupButtonPanel() {
		int maxWidth = 0;
		int maxHeight = 0;
		Dimension dim;
		//--- not use apply
		/*---
		dim = this.btnApply.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		---*/
		//---
		dim = this.btnOK.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		//---
		dim = this.btnCancel.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		//---
		dim = new Dimension(maxWidth, maxHeight);
		//btnApply .setMinimumSize(dim);
		this.btnApply .setPreferredSize(dim);
		//btnOK    .setMinimumSize(dim);
		this.btnOK    .setPreferredSize(dim);
		//btnCancel.setMinimumSize(dim);
		this.btnCancel.setPreferredSize(dim);
		
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(Box.createHorizontalGlue());
		//btnBox.add(this.btnApply);
		btnBox.add(this.btnOK);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(this.btnCancel);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(Box.createRigidArea(new Dimension(0, dim.height+10)));
		
		// setup default button
		this.btnOK.setDefaultCapable(true);
		
		this.getContentPane().add(btnBox, BorderLayout.SOUTH);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getSelectedLineNo() {
		return ((Integer)spinModel.getValue()).intValue();
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	protected boolean onButtonOK() {
		return super.onButtonOK();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	@Override
	protected Point getDefaultLocation() {
		return super.getDefaultLocation();
	}

	@Override
	protected Dimension getDefaultSize() {
		//return super.getDefaultSize();
		return DM_MIN_SIZE;
	}

	@Override
	protected void loadSettings() {
		super.loadSettings(AppSettings.JUMP_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	protected void saveSettings() {
		super.saveSettings(AppSettings.JUMP_DLG, AppSettings.getInstance().getConfiguration());
//		// テスト
//		Point loc = this.getLocationOnScreen();
//		Dimension dm = this.getSize();
//		AppLogger.debug("Location : " + loc.toString());
//		AppLogger.debug("Size : " + dm.toString());
//		// テスト
	}
}
