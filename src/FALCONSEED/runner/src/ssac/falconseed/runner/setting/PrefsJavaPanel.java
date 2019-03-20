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
 * @(#)PrefsJavaPanel.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.setting;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.io.JavaInfo;
import ssac.util.swing.JStaticMultilineTextPane;

/**
 * JAVA 情報を表示するパネル。
 * 
 * @version 1.00	2010/12/20
 */
public class PrefsJavaPanel extends AbPreferencePanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private JTextComponent		_lblDefaultJavaHome;
	private JTextComponent		_lblDefaultJavaVersion;
	private JTextComponent		_lblDefaultJavaCommand;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PrefsJavaPanel() {
		super(new GridBagLayout());
		setupContents();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void restoreSettings() {
		// update default java info
		updateJavaInfo(AppSettings.getInstance().getCurrentJavaInfo(),
						_lblDefaultJavaHome, _lblDefaultJavaVersion, _lblDefaultJavaCommand);
	}
	
	public void storeSettings() {
		// no entry
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private final void setupContents() {
		// create components
		_lblDefaultJavaHome    = new JStaticMultilineTextPane(CommonMessages.getInstance().labelMessageNotFound);
		_lblDefaultJavaVersion = new JStaticMultilineTextPane(CommonMessages.getInstance().labelMessageNotFound);
		_lblDefaultJavaCommand = new JStaticMultilineTextPane(CommonMessages.getInstance().labelMessageNotFound);
		
		// create labels
		JLabel titleDefaultJavaHome    = new JLabel(RunnerMessages.getInstance().PreferenceDlg_Java_Home);
		JLabel titleDefaultJavaVersion = new JLabel(RunnerMessages.getInstance().PreferenceDlg_Java_Version);
		JLabel titleDefaultJavaCommand = new JLabel(RunnerMessages.getInstance().PreferenceDlg_Java_Command);
		
		// setup sizes
		Dimension dmFieldSize = _lblDefaultJavaHome.getPreferredSize();
		Dimension dmTitleSize = titleDefaultJavaHome.getPreferredSize();
		int height = dmFieldSize.height - dmTitleSize.height;
		if (height > 1) {
			int itop = height / 2;
			int ibottom = height - itop;
			Border bdMargin = BorderFactory.createEmptyBorder(itop, 0, ibottom, 0);
			titleDefaultJavaHome   .setBorder(bdMargin);
			titleDefaultJavaVersion.setBorder(bdMargin);
			titleDefaultJavaCommand.setBorder(bdMargin);
		}
		
		// setup border
		setCommonBorder();
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0,0,3,3);
		//--- default home
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		this.add(titleDefaultJavaHome, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		this.add(_lblDefaultJavaHome, gbc);
		gbc.gridy++;
		//--- default version
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		this.add(titleDefaultJavaVersion, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		this.add(_lblDefaultJavaVersion, gbc);
		gbc.gridy++;
		//--- default command
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		this.add(titleDefaultJavaCommand, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		this.add(_lblDefaultJavaCommand, gbc);
		gbc.gridy++;
		//--- dummy
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0,0,0,0);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(new JLabel(), gbc);
	}
	
	protected void updateJavaInfo(JavaInfo info,
								  JTextComponent cHome,
								  JTextComponent cVersion,
								  JTextComponent cCommand)
	{
		final String strNone = CommonMessages.getInstance().labelMessageNotFound;
		String strHome = null;
		String strVersion = null;
		String strCommand = null;
		
		if (info != null) {
			strHome = info.getHomePath();
			strVersion = info.getVersion();
			strCommand = info.getCommandPath();
		}

		if (strHome == null)
			strHome = strNone;
		if (strVersion == null)
			strVersion = strNone;
		if (strCommand == null)
			strCommand = strNone;
		
		cHome.setText(strHome);
		cVersion.setText(strVersion);
		cCommand.setText(strCommand);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
