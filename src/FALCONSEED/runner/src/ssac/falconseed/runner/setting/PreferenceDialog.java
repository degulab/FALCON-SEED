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
 * @(#)PreferenceDialog.java	2.1.0	2013/08/13
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)PreferenceDialog.java	1.20	2012/03/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)PreferenceDialog.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.setting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.common.FSStartupSettings;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.logging.AppLogger;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;

/**
 * アプリケーション設定を行うダイアログ。
 * 
 * @version 2.1.0	2013/08/13
 */
public class PreferenceDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(580, 420);
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private JTabbedPane		_tab;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PreferenceDialog(Frame owner) {
		super(owner, RunnerMessages.getInstance().PreferenceDlg_Title_Main, true);
		setConfiguration(AppSettings.PREFERENCE_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	public void initialComponent() {
		super.initialComponent();
		
		restoreConfiguration();
		restorePreferences();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void restorePreferences() {
		if (_tab != null) {
			int cnt = _tab.getTabCount();
			for (int i = 0; i < cnt; i++) {
				AbPreferencePanel pnl = (AbPreferencePanel)_tab.getComponentAt(i);
				pnl.restoreSettings();
			}
		}
	}
	
	public void storePreferences() {
		if (_tab != null) {
			int cnt = _tab.getTabCount();
			for (int i = 0; i < cnt; i++) {
				AbPreferencePanel pnl = (AbPreferencePanel)_tab.getComponentAt(i);
				pnl.storeSettings();
			}
		}
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected void doApplyAction() {
		applySettings();
	}

	@Override
	protected boolean doOkAction() {
		return applySettings();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();

		setResizable(true);
		setStoreLocation(false);
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200,300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
	}

	@Override
	protected void setupMainContents() {
		// create panels
		_tab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		_tab.addTab(RunnerMessages.getInstance().PreferenceDlg_Title_Tab_Java, new PrefsJavaPanel());
		//_tab.addTab(RunnerMessages.getInstance().PreferenceDlg_Title_Tab_Font, new PrefsFontPanel());
		_tab.addTab(RunnerMessages.getInstance().PreferenceDlg_Title_Tab_Encoding, new PrefsTextEncodingPanel());
		_tab.addTab(RunnerMessages.getInstance().PreferenceDlg_Title_Tab_GraphViz, new PrefsGraphVizPanel());
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			_tab.addTab(RunnerMessages.getInstance().PreferenceDlg_Title_Tab_Debug, new PrefsDebugPanel());
		}
		
		// create main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		//--- setup border
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		mainPanel.add(_tab, BorderLayout.CENTER);
		
		// set to main component
		this.add(mainPanel, BorderLayout.CENTER);
	}
	
	private boolean applySettings() {
		// store properties from GUI
		storePreferences();
		
		// commit settings
		boolean succeeded;
		try {
			AppSettings.flush();
			succeeded = true;
		}
		catch (Throwable ex) {
			String strmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_SETTING_WRITE,
														ex, AppSettings.getPropertyFilePath());
			AppLogger.error(strmsg, ex);
			Application.showErrorMessage(this, strmsg);
			succeeded = false;
		}
		return succeeded;
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}
}
