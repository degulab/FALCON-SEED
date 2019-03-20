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
 * @(#)PrefsFontPanel.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.setting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import ssac.falconseed.runner.RunnerMessages;
import ssac.util.swing.StaticTextComponent;

/**
 * フォントを設定するパネル。
 * 
 * @version 1.00	2010/12/20
 */
public class PrefsFontPanel extends AbPreferencePanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String FONT_SAMPLE_TEXT = "ABCabcあいう亜居宇";
	
	static private final String[]	_fontSizes = {
		"8","9","10","11","12","13","14","15","16","18","20","22","24","28","32"
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	static private String[]	_fontFamilyNames;
	static private String[]	_fontTargetNames;
	static private LinkedHashMap<String,String> _fontTargetKeys = new LinkedHashMap<String,String>();
	
	private final HashMap<String,Font>	_selectedFont = new HashMap<String,Font>();
	
	private boolean			_flgIgnoreListEvent = false;
	
	private JTextComponent		_stcFontFamily;
	private JTextComponent		_stcFontSize;
	private JTextField			_stcFontSample;
	private JComboBox			_cmbFontTarget;
	private JList				_lstFontFamily;
	private JList				_lstFontSize;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PrefsFontPanel() {
		super(new GridBagLayout());
		setupContents();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void restoreSettings() {
		String[] fontTargets = getFontTargetNames();
		for (String strTarget : fontTargets) {
			String strPrefix = _fontTargetKeys.get(strTarget);
			if (strPrefix != null) {
				Font font = AppSettings.getInstance().getFont(strPrefix);
				if (font != null) {
					_selectedFont.put(strTarget, font);
				}
			}
		}
		
		updateDisplayForSelectedFont();
	}
	
	public void storeSettings() {
		String[] fontTargets = getFontTargetNames();
		for (String strTarget : fontTargets) {
			String strPrefix = _fontTargetKeys.get(strTarget);
			if (strPrefix != null) {
				Font font = _selectedFont.get(strTarget);
				AppSettings.getInstance().setFont(strPrefix, font);
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected final String[] getFontTargetNames() {
		if (_fontTargetNames == null) {
			// font target keys
			//--- Console
			_fontTargetKeys.put(RunnerMessages.getInstance().PreferenceDlg_Font_Target_Console, AppSettings.VIEW_CONSOLE);
			//--- CSV Viewer
			_fontTargetKeys.put(RunnerMessages.getInstance().PreferenceDlg_Font_Target_CSV_Viewer, AppSettings.VIEW_CSVVIEWER);
			// font target names
			_fontTargetNames = _fontTargetKeys.keySet().toArray(new String[_fontTargetKeys.size()]);
		}
		return _fontTargetNames;
	}
	
	static protected final String[] getFontFamilyNames() {
		if (_fontFamilyNames == null) {
			GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			_fontFamilyNames = genv.getAvailableFontFamilyNames();
		}
		return _fontFamilyNames;
	}
	
	static protected final String[] getFontSizes() {
		return _fontSizes;
	}
	
	private void updateDisplayForSelectedFont() {
		String strFontTarget = (String)_cmbFontTarget.getSelectedItem();
		Font targetFont = _selectedFont.get(strFontTarget);
		if (targetFont == null) {
			targetFont = _stcFontFamily.getFont();
		}
		String strFontFamily = targetFont.getFamily();
		String strFontSize = Integer.toString(targetFont.getSize());
		_stcFontFamily.setText(strFontFamily);
		_stcFontSize.setText(strFontSize);
		{
			_flgIgnoreListEvent = true;
			_lstFontFamily.setSelectedValue(strFontFamily, true);
			_lstFontSize.setSelectedValue(strFontSize, true);
			_flgIgnoreListEvent = false;
		}
		_stcFontSample.setFont(targetFont);
	}
	
	private void updateDisplayForFontSample() {
		String strFontTarget = (String)_cmbFontTarget.getSelectedItem();
		Font targetFont = createSelectedFont();
		_selectedFont.put(strFontTarget, targetFont);
		_stcFontSample.setFont(targetFont);
	}
	
	private Font createSelectedFont() {
		String strFontFamily = _stcFontFamily.getText();
		String strFontSize = _stcFontSize.getText();
		int fSize = Integer.parseInt(strFontSize);
		return new Font(strFontFamily, Font.PLAIN, fSize);
	}
	
	private final void setupContents() {
		// Color
		Color crBack = (new JTextField()).getBackground();
		
		// create components
		//--- Font target
		JLabel lblTarget = new JLabel(RunnerMessages.getInstance().PreferenceDlg_Font_Target);
		_cmbFontTarget = new JComboBox(getFontTargetNames());
		//--- Font family
		JLabel lblFamily = new JLabel(RunnerMessages.getInstance().PreferenceDlg_Font_Family);
		_stcFontFamily = new StaticTextComponent("Family");
		_stcFontFamily.setBackground(crBack);
		_lstFontFamily = new JList(getFontFamilyNames());
		JScrollPane scFontFamily = new JScrollPane(_lstFontFamily,
													JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
													JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//--- Font size
		JLabel lblSize = new JLabel(RunnerMessages.getInstance().PreferenceDlg_Font_Size);
		_stcFontSize = new StaticTextComponent("Size");
		_stcFontSize.setBackground(crBack);
		_lstFontSize = new JList(getFontSizes());
		JScrollPane scFontSize = new JScrollPane(_lstFontSize,
												JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
												JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//--- Font sample
		Border obd = BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
						RunnerMessages.getInstance().PreferenceDlg_Font_Sample,
						TitledBorder.LEFT, TitledBorder.TOP
					);
		Border bd = BorderFactory.createCompoundBorder(obd, BorderFactory.createEmptyBorder(3,3,3,3));
		_stcFontSample = new JTextField(FONT_SAMPLE_TEXT);
		_stcFontSample.setEditable(false);
		_stcFontSample.setHorizontalAlignment(JTextField.CENTER);
		Dimension dm = _stcFontSample.getPreferredSize();
		dm.height = 50;
		_stcFontSample.setPreferredSize(dm);
		//_stcFontSample.setSize(dm);
		_stcFontSample.setMinimumSize(dm);
		_stcFontSample.setBackground(crBack);
		JPanel pnlSample = new JPanel(new BorderLayout());
		pnlSample.add(_stcFontSample, BorderLayout.CENTER);
		pnlSample.setBorder(bd);
		
		// setup border
		setCommonBorder();
		
		// Layout
		//--- init
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(1,1,1,1);
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//--- target
		this.add(lblTarget, gbc);
		gbc.gridy++;
		this.add(_cmbFontTarget, gbc);
		gbc.gridy++;
		//--- fonts
		int sy = gbc.gridy;
		//--- Font family
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		this.add(lblFamily, gbc);
		gbc.gridy++;
		this.add(_stcFontFamily, gbc);
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(scFontFamily, gbc);
		//--- Font size
		gbc.gridy = sy;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		this.add(lblSize, gbc);
		gbc.gridy++;
		this.add(_stcFontSize, gbc);
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(scFontSize, gbc);
		//--- Font sample
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 2;
		this.add(pnlSample, gbc);
		
		// initialize
		_cmbFontTarget.setSelectedIndex(0);
		_lstFontFamily.setSelectedIndex(0);
		_lstFontSize.setSelectedIndex(0);
		
		// Actions
		//--- target
		_cmbFontTarget.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				updateDisplayForSelectedFont();
			}
		});
		//--- family
		_lstFontFamily.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if (!_flgIgnoreListEvent) {
					String strValue = (String)_lstFontFamily.getSelectedValue();
					_stcFontFamily.setText(strValue);
					updateDisplayForFontSample();
				}
			}
		});
		//--- size
		_lstFontSize.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				if (!_flgIgnoreListEvent) {
					String strValue = (String)_lstFontSize.getSelectedValue();
					_stcFontSize.setText(strValue);
					updateDisplayForFontSample();
				}
			}
		});
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
