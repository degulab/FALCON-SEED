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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CharsetNameChooserPanel.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.view.JEncodingComboBox;
import ssac.util.Strings;

/**
 * テキストエンコーディングとなる文字セット名選択パネル
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.14
 */
public class CharsetNameChooserPanel extends JPanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private String				panelCaption;
	
	private String				defEncodingName;
	
	private ButtonGroup		bgEncoding;
	private JRadioButton		rbDefaultEncoding;
	private JRadioButton		rbCustomEncoding;
	private JLabel				lblDefaultEncoding;
	private JEncodingComboBox	cmbCustomEncoding;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CharsetNameChooserPanel() {
		this(null);
	}
	
	public CharsetNameChooserPanel(String caption) {
		super(new GridBagLayout());
		setCaption(caption);
		initialComponent();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getCaption() {
		return panelCaption;
	}
	
	public void setCaption(String caption) {
		if (!Strings.isNullOrEmpty(caption)) {
			this.panelCaption = caption;
			Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			Border bro = BorderFactory.createTitledBorder(bri, caption, TitledBorder.LEFT, TitledBorder.TOP);
			setBorder(bro);
		} else {
			// no border
			this.panelCaption = "";
			setBorder(null);
		}
	}
	
	public String getDefaultEncodingName() {
		if (Strings.isNullOrEmpty(defEncodingName)) {
			return JEncodingComboBox.getDefaultEncoding();
		} else {
			return defEncodingName;
		}
	}
	
	public void setupDisplay(boolean isCustomSelected, String defaultEncodingName, String customEncodingName) {
		// setup default encoding
		if (JEncodingComboBox.isAvailableEncoding(defaultEncodingName)) {
			defEncodingName = defaultEncodingName;
		} else {
			defEncodingName = null;
		}
		String defCharsetName = getDefaultEncodingName();
		setDefaultEncodingLabel(defCharsetName);

		// setup components state
		if (isCustomSelected) {
			if (JEncodingComboBox.isAvailableEncoding(customEncodingName)) {
				cmbCustomEncoding.setSelectedEncoding(customEncodingName);
			} else {
				cmbCustomEncoding.setSelectedEncoding(defCharsetName);
			}
			rbCustomEncoding.setSelected(true);
		} else {
			cmbCustomEncoding.setSelectedEncoding(defCharsetName);
			rbDefaultEncoding.setSelected(true);
		}
		updateDisplay();
	}
	
	public boolean isDefaultSelected() {
		return rbDefaultEncoding.isSelected();
	}
	
	public boolean isCustomSelected() {
		return rbCustomEncoding.isSelected();
	}
	
	public void selectDefault() {
		rbDefaultEncoding.setSelected(true);
		updateDisplay();
	}
	
	public void selectCustom() {
		rbCustomEncoding.setSelected(true);
		updateDisplay();
	}
	
	public String getSelectedCustomEncodingName() {
		return cmbCustomEncoding.getSelectedEncoding();
	}
	
	public void setSelectedCustomEncoding(String name) {
		cmbCustomEncoding.setSelectedEncoding(name);
	}
	
	public void updateDisplay() {
		boolean flgEnable = rbCustomEncoding.isSelected();
		cmbCustomEncoding.setEnabled(flgEnable);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void setDefaultEncodingLabel(String charsetName) {
		if (!Strings.isNullOrEmpty(charsetName)) {
			lblDefaultEncoding.setText("(" + charsetName + ")");
		} else {
			lblDefaultEncoding.setText("(Default)");
		}
	}
	
	protected void initialComponent() {
		// setup Objects
		//--- Radio buttons
		rbDefaultEncoding = new JRadioButton(EditorMessages.getInstance().labelDefault);
		rbCustomEncoding = new JRadioButton(EditorMessages.getInstance().labelCustom);
		bgEncoding = new ButtonGroup();
		bgEncoding.add(this.rbDefaultEncoding);
		bgEncoding.add(this.rbCustomEncoding);
		//--- Labels
		lblDefaultEncoding = new JLabel(rbDefaultEncoding.getText());
		//--- Combo box
		cmbCustomEncoding = new JEncodingComboBox();
		
		// Layout
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
		//--- radio button
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(rbDefaultEncoding, gbc);
		gbc.gridy = 1;
		this.add(rbCustomEncoding, gbc);
		//--- label
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		this.add(lblDefaultEncoding, gbc);
		//--- combo
		gbc.gridy = 1;
		this.add(cmbCustomEncoding, gbc);
		
		// Actions
		//--- radio button
		ActionListener rbal = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onRadioButtonEncoding(ae);
			}
		};
		rbDefaultEncoding.addActionListener(rbal);
		rbCustomEncoding.addActionListener(rbal);
	}
	
	protected void onRadioButtonEncoding(ActionEvent ae) {
		updateDisplay();
	}
}
