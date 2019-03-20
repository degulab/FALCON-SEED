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
 * @(#)PrefsTextEncodingPanel.java	1.20	2012/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)PrefsTextEncodingPanel.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.setting;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import ssac.falconseed.runner.RunnerMessages;
import ssac.util.Strings;
import ssac.util.swing.CharsetChooserPanel;

/**
 * 標準のテキストエンコーディングを設定するパネル。
 * 
 * @version 1.20	2012/03/16
 */
public class PrefsTextEncodingPanel extends AbPreferencePanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private CharsetChooserPanel	_pnlAadlRuntimeCsv;
	private CharsetChooserPanel	_pnlAadlRuntimeTxt;
//	private CharsetChooserPanel	_pnlAadlSource;
	private CharsetChooserPanel	_pnlAadlMacro;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PrefsTextEncodingPanel() {
		super(new GridBagLayout());
		setupContents();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void restoreSettings() {
		AppSettings settings = AppSettings.getInstance();
		
		// Default AADL csvFile
		_pnlAadlRuntimeCsv.setupDisplay(settings.isSpecifiedAadlCsvEncodingName(),
				settings.getDefaultAadlCsvEncodingName(), settings.getAadlCsvEncodingName());
		
		// Default AADL txtFile
		_pnlAadlRuntimeTxt.setupDisplay(settings.isSpecifiedAadlTxtEncodingName(),
				settings.getDefaultAadlTxtEncodingName(), settings.getAadlTxtEncodingName());
		
//		// AADL Source file
//		_pnlAadlSource.setupDisplay(settings.isSpecifiedAadlSourceEncodingName(),
//				settings.getDefaultAadlSourceEncodingName(), settings.getAadlSourceEncodingName());
		
		// AADL Macro file
		_pnlAadlMacro.setupDisplay(settings.isSpecifiedAadlMacroEncodingName(),
				settings.getDefaultAadlMacroEncodingName(), settings.getAadlMacroEncodingName());
	}
	
	public void storeSettings() {
		boolean flgEncoding;
		String strEncoding;
		
		// Default AADL csvFile
		flgEncoding = _pnlAadlRuntimeCsv.isCustomSelected();
		strEncoding = _pnlAadlRuntimeCsv.getSelectedCustomCharsetName();
		if (flgEncoding && !Strings.isNullOrEmpty(strEncoding)) {
			AppSettings.getInstance().setAadlCsvEncodingName(strEncoding);
		} else {
			AppSettings.getInstance().setAadlCsvEncodingName(null);
		}
		
		// Default AADL txtFile
		flgEncoding = _pnlAadlRuntimeTxt.isCustomSelected();
		strEncoding = _pnlAadlRuntimeTxt.getSelectedCustomCharsetName();
		if (flgEncoding && !Strings.isNullOrEmpty(strEncoding)) {
			AppSettings.getInstance().setAadlTxtEncodingName(strEncoding);
		} else {
			AppSettings.getInstance().setAadlTxtEncodingName(null);
		}
		
//		// AADL Source file
//		flgEncoding = _pnlAadlSource.isCustomSelected();
//		strEncoding = _pnlAadlSource.getSelectedCustomCharsetName();
//		if (flgEncoding && !Strings.isNullOrEmpty(strEncoding)) {
//			AppSettings.getInstance().setAadlSourceEncodingName(strEncoding);
//		} else {
//			AppSettings.getInstance().setAadlSourceEncodingName(null);
//		}
		
		// AADL Macro file
		flgEncoding = _pnlAadlMacro.isCustomSelected();
		strEncoding = _pnlAadlMacro.getSelectedCustomCharsetName();
		if (flgEncoding && !Strings.isNullOrEmpty(strEncoding)) {
			AppSettings.getInstance().setAadlMacroEncodingName(strEncoding);
		} else {
			AppSettings.getInstance().setAadlMacroEncodingName(null);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private final void setupContents() {
		// create components
		_pnlAadlRuntimeCsv = new CharsetChooserPanel(RunnerMessages.getInstance().PreferenceDlg_Encoding_AADLcsvFile);
		_pnlAadlRuntimeTxt = new CharsetChooserPanel(RunnerMessages.getInstance().PreferenceDlg_Encoding_AADLtxtFile);
//		_pnlAadlSource     = new CharsetChooserPanel(RunnerMessages.getInstance().PreferenceDlg_Encoding_AADLSource);
		_pnlAadlMacro      = new CharsetChooserPanel(RunnerMessages.getInstance().PreferenceDlg_Encoding_AADLMacro);
		
		// setup border
		setCommonBorder();
		
		// Layout
		//--- init
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//--- Default AADL csvFile
		this.add(_pnlAadlRuntimeCsv, gbc);
		gbc.gridy++;
		//--- Default AADL txtFile
		this.add(_pnlAadlRuntimeTxt, gbc);
		gbc.gridy++;
//		//--- AADL Source file
//		this.add(_pnlAadlSource, gbc);
//		gbc.gridy++;
		//--- AADL Macro file
		this.add(_pnlAadlMacro, gbc);
		gbc.gridy++;
		//--- blank
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(new JLabel(), gbc);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
