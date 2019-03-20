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
 * @(#)ExecuteOptionPane.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.view.table.ModuleArgValueTableModel;
import ssac.aadl.editor.view.table.ModuleArgValueTablePane;
import ssac.aadl.module.ModuleArgDetail;
import ssac.aadl.module.setting.MacroExecSettings;
import ssac.util.swing.MenuToggleButton;

/**
 * ビルドオプションダイアログのマクロ専用実行オプション・パネル
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class MacroExecOptionPane extends JPanel implements IBuildOptionPane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final MacroExecSettings settings;
	private final boolean readOnly;
	
	//--- Program arguments
	private ModuleArgValueTablePane	tableArgs;
	private MenuToggleButton	btnArgAdd;
	private MenuToggleButton	btnArgEdit;
	private JButton			btnArgDel;
	private JButton			btnArgUp;
	private JButton			btnArgDown;
	
	//--- Java VM arguments
	private JTextArea		taVMArgs;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroExecOptionPane(MacroExecSettings settings, boolean readOnly) {
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
		// items
		JPanel p3 = createProgramArgumentsPanel();
		JPanel p4 = createVMArgumentsPanel();
		
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
		//--- Program arguments
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		this.add(p3, gbc);
		gbc.gridy++;
		//--- Java VM arguments
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0;
		this.add(p4, gbc);
		gbc.gridy++;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getTitle() {
		return EditorMessages.getInstance().BuildOptionDlg_Title_Run;
	}
	
	public void updateProgramArgumentDetails(ModuleArgDetail[] details) {
		tableArgs.updateArgumentDetails(details);
	}

	//------------------------------------------------------------
	// implement IBuildOptionPane interfaces
	//------------------------------------------------------------
	/**
	 * このオプションを保持する設定情報が読み込み専用の場合に <tt>true</tt> を返す。
	 * @since 1.14
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public String getLastArgumentPath() {
		return this.tableArgs.getLastArgumentPath();
	}
	
	public void setLastArgumentPath(String path) {
		this.tableArgs.setLastArgumentPath(path);
	}
	
	public MacroExecSettings getOptionSettings() {
		return this.settings;
	}
	
	public void restoreOptionSettings() {
		// program args
		ModuleArgValueTableModel argsModel = new ModuleArgValueTableModel(settings.getArgumentDetails(), settings.getProgramArgs());
		tableArgs.setTableModel(argsModel);
		
		// java VM args
		String strVMArgs = settings.getJavaVMArgs();
		taVMArgs.setText(strVMArgs != null ? strVMArgs : "");
		
		// update display
		tableArgs.setDefaultArgumentBaseFile(getAvailableWorkDir());
	}
	
	public void storeOptionSettings() {
		// 引数編集確定
		this.tableArgs.stopTableCellEditing();
		
		if (!readOnly) {
			// program args
			ModuleArgValueTableModel argsModel = this.tableArgs.getTableModel();
			this.settings.setProgramArgs(argsModel.getArgumentValues());

			// java VM args
			String strVMArgs = this.taVMArgs.getText();
			this.settings.setJavaVMArgs(strVMArgs);
		}
	}
	
	/**
	 * このコンポーネントを保持するウィンドウが最初に表示された
	 * 直後に呼び出されるイベント。
	 * @param source	このイベントを呼び出したウィンドウ
	 * @since 1.14
	 */
	public void onWindowOpened(Window source) {
		tableArgs.adjustTableAllColumnsPreferredWidth();
		if (readOnly) {
			tableArgs.setEditable(false);
			btnArgAdd .setEnabled(false);
			btnArgEdit.setEnabled(false);
			btnArgDel .setEnabled(false);
			btnArgUp  .setEnabled(false);
			btnArgDown.setEnabled(false);
			taVMArgs  .setEditable(false);
			JTextField dummyField = new JTextField();
			dummyField.setEditable(false);
			taVMArgs.setBackground(dummyField.getBackground());
		}
	}

	//------------------------------------------------------------
	// Actions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private File getAvailableWorkDir() {
		File fTarget = settings.getTargetFile();
		if (fTarget==null) {
			return null;
		} else {
			return fTarget.getParentFile();
		}
	}
	
	private JPanel createProgramArgumentsPanel() {
		// items
		//--- Buttons
		/*
		this.btnArgAdd = new JButton(AppMessages.getInstance().Button_Add);
		this.btnArgEdit = new JButton(AppMessages.getInstance().Button_Edit);
		this.btnArgDel = new JButton(AppMessages.getInstance().Button_Delete);
		this.btnArgUp = new JButton(AppMessages.getInstance().Button_Up);
		this.btnArgDown = new JButton(AppMessages.getInstance().Button_Down);
		*/
		this.btnArgAdd	= CommonResources.createMenuIconButton(CommonResources.ICON_ADD, CommonMessages.getInstance().Button_Add);
		this.btnArgEdit	= CommonResources.createMenuIconButton(CommonResources.ICON_EDIT, CommonMessages.getInstance().Button_Edit);
		this.btnArgDel	= CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		this.btnArgUp	= CommonResources.createIconButton(CommonResources.ICON_ARROW_UP, CommonMessages.getInstance().Button_Up);
		this.btnArgDown	= CommonResources.createIconButton(CommonResources.ICON_ARROW_DOWN, CommonMessages.getInstance().Button_Down);
		this.btnArgDel .setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		this.btnArgUp  .setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		this.btnArgDown.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		//--- table
		this.tableArgs = new ModuleArgValueTablePane();
		this.tableArgs.initialComponent();
		if (!readOnly) {
			this.tableArgs.attachAddButton(this.btnArgAdd);
			this.tableArgs.attachEditButton(this.btnArgEdit);
			this.tableArgs.attachDeleteButton(this.btnArgDel);
			this.tableArgs.attachUpButton(this.btnArgUp);
			this.tableArgs.attachDownButton(this.btnArgDown);
		}
		this.btnArgDown.setSize(30, 20);
		//--- Panel
		JPanel panel = new JPanel(new GridBagLayout());
		//--- Border
		Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border bro = BorderFactory.createTitledBorder(bri, EditorMessages.getInstance().BuildOptionDlg_Title_Run_ProgramArgs,
														TitledBorder.LEFT, TitledBorder.TOP);
		panel.setBorder(bro);

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
		//--- table
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridheight = 6;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(tableArgs, gbc);
		if (!readOnly) {
			//--- buttons
			gbc.anchor = GridBagConstraints.NORTHEAST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridheight = 1;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.gridx = 1;
			gbc.gridy = 0;
			panel.add(this.btnArgAdd, gbc);
			gbc.gridy++;
			panel.add(this.btnArgEdit, gbc);
			gbc.gridy++;
			panel.add(this.btnArgDel, gbc);
			gbc.gridy++;
			panel.add(this.btnArgUp, gbc);
			gbc.gridy++;
			panel.add(this.btnArgDown, gbc);
			//--- dummy
			JLabel lblDummy = new JLabel();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weighty = 1;
			gbc.gridy++;
			panel.add(lblDummy, gbc);
		}
		
		// Actions
		
		// completed
		return panel;
	}
	
	private JPanel createVMArgumentsPanel() {
		// items
		//--- text box
		this.taVMArgs = new JTextArea(3, 10);
		this.taVMArgs.setEditable(true);
		Dimension dm = this.taVMArgs.getPreferredSize();
		this.taVMArgs.setMinimumSize(dm);
		this.taVMArgs.setPreferredSize(dm);
		JScrollPane sp = new JScrollPane(this.taVMArgs,
										  JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
										  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setMinimumSize(dm);
		//--- Panel
		JPanel panel = new JPanel(new BorderLayout());
		//--- Border
		Border bri = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border bro = BorderFactory.createTitledBorder(bri, EditorMessages.getInstance().BuildOptionDlg_Title_Run_VMArgs,
														TitledBorder.LEFT, TitledBorder.TOP);
		Border br = BorderFactory.createCompoundBorder(bro, BorderFactory.createEmptyBorder(2, 2, 2, 2));
		panel.setBorder(br);

		// Layout
		panel.add(sp, BorderLayout.CENTER);
		
		// Actions
		
		// completed
		return panel;
	}
}
