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
 * @(#)ClassPathPane.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ClassPathPane.java	1.10	2008/12/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ClassPathPane.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.module.setting.ClassPathSettings;
import ssac.util.Strings;
import ssac.util.swing.list.FileListModel;

/**
 * ビルドオプションダイアログのクラス・パス・パネル
 * 
 * @version 1.14	2009/12/09
 */
public class ClassPathPane extends JPanel implements IBuildOptionPane
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ClassPathSettings	settings;
	private final boolean readOnly;
	
	private CustomFileList	lstPaths;
	private JButton		btnPathAdd;
	private JButton		btnPathEdit;
	private JButton		btnPathDel;
	private JButton		btnPathUp;
	private JButton		btnPathDown;
	
	private String			lastClassPath;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ClassPathPane(ClassPathSettings settings, boolean readOnly) {
		super(new GridBagLayout());
		if (settings == null)
			throw new NullPointerException();
		this.settings = settings;
		this.readOnly = readOnly;
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		initialComponent();
		restoreOptionSettings();
	}
	
	protected void initialComponent() {
		// Items
		//--- label
		JLabel lblTitle = new JLabel(EditorMessages.getInstance().BuildOptionDlg_Title_ClassPath_Path);
		//--- Buttons
		/*
		this.btnPathAdd = new JButton(AppMessages.getInstance().Button_Add);
		this.btnPathEdit = new JButton(AppMessages.getInstance().Button_Edit);
		this.btnPathDel = new JButton(AppMessages.getInstance().Button_Delete);
		this.btnPathUp = new JButton(AppMessages.getInstance().Button_Up);
		this.btnPathDown = new JButton(AppMessages.getInstance().Button_Down);
		*/
		this.btnPathAdd		= CommonResources.createIconButton(CommonResources.ICON_ADD, CommonMessages.getInstance().Button_Add);
		this.btnPathEdit	= CommonResources.createIconButton(CommonResources.ICON_EDIT, CommonMessages.getInstance().Button_Edit);
		this.btnPathDel		= CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		this.btnPathUp		= CommonResources.createIconButton(CommonResources.ICON_ARROW_UP, CommonMessages.getInstance().Button_Up);
		this.btnPathDown	= CommonResources.createIconButton(CommonResources.ICON_ARROW_DOWN, CommonMessages.getInstance().Button_Down);
		//--- List
		this.lstPaths = new CustomFileList();
		//this.lstPaths.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (!readOnly) {
			this.lstPaths.controller.attachAddButton(this.btnPathAdd);
			this.lstPaths.controller.attachEditButton(this.btnPathEdit);
			this.lstPaths.controller.attachDeleteButton(this.btnPathDel);
			this.lstPaths.controller.attachUpButton(this.btnPathUp);
			this.lstPaths.controller.attachDownButton(this.btnPathDown);
		}
		JScrollPane sp = new JScrollPane(this.lstPaths,
										  JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
										  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
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
		//--- label
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = (readOnly ? 1 : 2);
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(lblTitle, gbc);
		//--- list
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 6;
		gbc.gridy = 1;
		this.add(sp, gbc);
		//--- Buttons
		if (!readOnly) {
			gbc.anchor = GridBagConstraints.NORTHEAST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridheight = 1;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.gridx = 1;
			gbc.gridy = 1;
			this.add(this.btnPathAdd, gbc);
			gbc.gridy++;
			this.add(this.btnPathEdit, gbc);
			gbc.gridy++;
			this.add(this.btnPathDel, gbc);
			gbc.gridy++;
			this.add(this.btnPathUp, gbc);
			gbc.gridy++;
			this.add(this.btnPathDown, gbc);
			//--- Dummy
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weighty = 1;
			gbc.gridy++;
			this.add(new JLabel(), gbc);
		}
		
		// update status
		this.lstPaths.updateButtons();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	/**
	 * このオプションを保持する設定情報が読み込み専用の場合に <tt>true</tt> を返す。
	 * @since 1.14
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public String getTitle() {
		return EditorMessages.getInstance().BuildOptionDlg_Title_ClassPath;
	}
	
	public String getLastClassPath() {
		return this.lastClassPath;
	}
	
	public void setLastClassPath(String path) {
		this.lastClassPath = path;
	}

	//------------------------------------------------------------
	// implement IBuildOptionPane interfaces
	//------------------------------------------------------------
	
	public ClassPathSettings getOptionSettings() {
		return this.settings;
	}
	
	public void restoreOptionSettings() {
		FileListModel list = this.settings.getClassPathListModel();
		this.lstPaths.setModel(list);
	}
	
	public void storeOptionSettings() {
		if (!readOnly) {
			FileListModel list = (FileListModel)this.lstPaths.getModel();
			this.settings.setClassPathList(list);
		}
	}
	/**
	 * このコンポーネントを保持するウィンドウが最初に表示された
	 * 直後に呼び出されるイベント。
	 * @param source	このイベントを呼び出したウィンドウ
	 * @since 1.14
	 */
	public void onWindowOpened(Window source) {}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// implement CustomFileList class
	//------------------------------------------------------------
	
	private class CustomFileList extends FileList {
		@Override
		protected File chooseFile(File initFile) {
			if (initFile == null) {
				String strLastPath = getLastClassPath();
				if (Strings.isNullOrEmpty(strLastPath)) {
					initFile = new File("");
				} else {
					initFile = new File(strLastPath);
				}
			}
			
			File selected = FileChooserManager.chooseLibraryClassPath(this, initFile,
									EditorMessages.getInstance().chooserTitleClassPath);
			if (selected != null) {
				setLastClassPath(selected.getAbsolutePath());
			}
			return selected;
		}
	}
}
