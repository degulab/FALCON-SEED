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
 *  <author> Hideaki Yagi (MRI)
 */
/*
 * @(#)PrefsGraphVizPanel.java	1.20	2012/03/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.setting;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.Strings;
import ssac.util.logging.AppLogger;
import ssac.util.swing.JStaticMultilineTextPane;

/**
 * GraphViz の実行ファイルを設定するパネル。
 * 
 * @version 1.20	2012/03/19
 * @since 1.20
 */
public class PrefsGraphVizPanel extends AbPreferencePanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private File				_fGraphViz;
	private JTextComponent		_lblGraphVizPath;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PrefsGraphVizPanel() {
		super(new GridBagLayout());
		setupContents();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void restoreSettings() {
		String strPath = AppSettings.getInstance().getGraphVizPath();
		if (!Strings.isNullOrEmpty(strPath)) {
			_fGraphViz = new File(strPath);
			_lblGraphVizPath.setText(_fGraphViz.getAbsolutePath());
		} else {
			_fGraphViz = null;
			_lblGraphVizPath.setText("");
		}
	}
	
	public void storeSettings() {
		if (_fGraphViz != null) {
			AppSettings.getInstance().setGraphVizPath(_fGraphViz.getAbsolutePath());
		} else {
			AppSettings.getInstance().setGraphVizPath(null);
		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	protected void onChooseGraphVizPath(ActionEvent ae) {
		AppLogger.debug("on [Browse...] for GraphViz path.");

		File newPath = FileChooserManager.chooseAllFile(this, _fGraphViz, RunnerMessages.getInstance().PreferenceDlg_GraphViz_ChooseDot);
		if (newPath == null) {
			AppLogger.debug("...canceled!");
			return;
		}

		_fGraphViz = newPath;
		_lblGraphVizPath.setText(newPath==null ? "" : newPath.getAbsolutePath());
	}
	
	protected void onRemoveGraphVizPath(ActionEvent ae) {
		_fGraphViz = null;
		_lblGraphVizPath.setText("");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private final void setupContents() {
		// create components
		_lblGraphVizPath = new JStaticMultilineTextPane();
		JButton btnChoosePath = CommonResources.createBrowseButton(CommonMessages.getInstance().Button_Browse);
		JButton btnRemovePath = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		
		// create labels
		JLabel titleGraphVizPath = new JLabel(RunnerMessages.getInstance().PreferenceDlg_GraphViz_DotFile);
		
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
		//--- path
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		this.add(titleGraphVizPath, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		this.add(_lblGraphVizPath, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		this.add(btnChoosePath, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		this.add(btnRemovePath, gbc);
		gbc.gridx++;
		//--- dummy
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 4;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0,0,0,0);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(new JLabel(), gbc);
		
		// setup actions
		btnChoosePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				onChooseGraphVizPath(ae);
			}
		});
		//---
		btnRemovePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				onRemoveGraphVizPath(ae);
			}
		});
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
