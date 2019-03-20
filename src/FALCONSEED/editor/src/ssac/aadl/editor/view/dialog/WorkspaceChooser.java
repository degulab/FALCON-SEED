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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)WorkspaceChooser.java	3.2.2	2015/10/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)WorkspaceChooser.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileSystemView;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.BasicDialog;

/**
 * 作業領域(フォルダ)の基準となる位置を選択するダイアログ
 * 
 * @version 3.2.2
 * @since 1.14
 */
public class WorkspaceChooser extends BasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	static private final Dimension DM_MIN_SIZE = new Dimension(500, 200);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private JComboBox	_cCombo;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public WorkspaceChooser(Frame owner) {
		super(owner, EditorMessages.getInstance().WSChooserDlg_Title, true);
//		setSaveLocation(false);
//		setSaveSize(true);
		setStoreLocation(false);
		setStoreSize(true);
		
		// initialize
		initialLocalComponent();
		
		// restore settings
		loadSettings();
	}
	
	protected void initialLocalComponent() {
		// restore values from settings
		File lastDir = AppSettings.getInstance().getLastWorkspace();
		File[] wsList = AppSettings.getInstance().getWorkspaceList();
		
		// モデルの生成
		WorkspaceComboBoxModel newModel;
		if (wsList != null) {
			newModel = new WorkspaceComboBoxModel(wsList);
		} else {
			newModel = new WorkspaceComboBoxModel();
		}
		if (lastDir != null) {
			int index = newModel.getIndexOf(lastDir);
			if (index >= 0) {
				newModel.setSelectedItem(lastDir);
			}
		}

		// create ComboBox
		_cCombo = new JComboBox(newModel);
		_cCombo.setEditable(false);
		_cCombo.setRenderer(new ComboCellRenderer());
		
		// create other Components
		JButton btnBrowse = new JButton(CommonMessages.getInstance().Button_Browse);
		
		// Layout
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 5;
		gbc.ipady = 5;
		mainPanel.add(_cCombo, gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		mainPanel.add(btnBrowse, gbc);
		
		// setup Actions
		btnBrowse.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonBrowse(e);
			}
		});
		
		// setup Dialog style
		setResizable(true);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200, 300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
	}
	
	protected void setupButtonPanel() {
		int maxWidth = 0;
		int maxHeight = 0;
		Dimension dim;
		this.btnApply.setText(CommonMessages.getInstance().Button_RemoveHistories);
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
		//btnOK    .setMinimumSize(dim);
		this.btnOK    .setPreferredSize(dim);
		//btnCancel.setMinimumSize(dim);
		this.btnCancel.setPreferredSize(dim);
		
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(this.btnApply);
		btnBox.add(Box.createHorizontalGlue());
		btnBox.add(this.btnOK);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(this.btnCancel);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(Box.createRigidArea(new Dimension(0, dim.height+10)));
		
		// setup default button
		this.btnOK.setDefaultCapable(true);
		this.getContentPane().add(btnBox, BorderLayout.SOUTH);
		this.getRootPane().setDefaultButton(btnOK);
		
		// setup close action for [ESC] key
		AbstractAction act = new AbstractAction("Cancel"){
			public void actionPerformed(ActionEvent ae) {
				ActionListener[] listeners = btnCancel.getActionListeners();
				for (ActionListener l : listeners) {
					l.actionPerformed(ae);
				}
			}
		};
		InputMap imap = this.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel-by-esc");
		this.getRootPane().getActionMap().put("cancel-by-esc", act);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public File getSelectedFile() {
		return (File)_cCombo.getSelectedItem();
	}
	
	/**
	 * 指定されたファイルがワークスペースのルートとして適正であるかを検証する。
	 * このメソッドが <tt>false</tt> を返す場合、適切なエラーメッセージがメッセージボックスとして表示される。
	 * @param parentComponent		エラーメッセージを表示する際の親コンポーネント
	 * @param ignoreCheckExists		抽象パスが示すディレクトリの有無をチェックしない場合は <tt>true</tt> を指定する
	 * @param file					検証対象の抽象パス
	 * @return	適正であれば <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	<em>file</em> が <tt>null</tt> の場合
	 */
	static public boolean verifyWorkspaceRoot(Component parentComponent,
												boolean ignoreCheckExists,
												File file)
	{
		try {
			// check
			VirtualFile vfDir = ModuleFileManager.fromJavaFile(file);
			if (!ignoreCheckExists && !file.exists()) {
				//--- check exists
				String errmsg = CommonMessages.formatErrorMessage(
									EditorMessages.getInstance().msgWorkspaceNotFound,
									null, file.getAbsolutePath());
				AppLogger.error(errmsg);
				AADLEditor.showErrorMessage(parentComponent, errmsg);
				return false;
			}
			else if (!file.isDirectory()) {
				//--- check is directory
				String errmsg = CommonMessages.formatErrorMessage(
									EditorMessages.getInstance().msgWorkspaceNotDirectory,
									null, file.getAbsolutePath());
				AppLogger.error(errmsg);
				AADLEditor.showErrorMessage(parentComponent, errmsg);
				return false;
			}
			else if (!file.canWrite()) {
				//--- check can write
				String errmsg = CommonMessages.formatErrorMessage(
						EditorMessages.getInstance().msgWorkspaceCouldNotWrite,
						null, file.getAbsolutePath());
				AppLogger.error(errmsg);
				AADLEditor.showErrorMessage(parentComponent, errmsg);
				return false;
			}
			else if (ModuleFileManager.getTopProjectPrefsFile(vfDir, null) != null) {
				//--- check project root
				String errmsg = CommonMessages.formatErrorMessage(
						CommonMessages.getInstance().msgCouldNotSelectInProject,
						null, file.getAbsolutePath());
				AppLogger.error(errmsg);
				AADLEditor.showErrorMessage(parentComponent, errmsg);
				return false;
			}
			else if (ModuleFileManager.getTopModulePackagePrefsFile(vfDir, null) != null) {
				//--- check package root
				String errmsg = CommonMessages.formatErrorMessage(
						CommonMessages.getInstance().msgCouldNotSelectInPackage,
						null, file.getAbsolutePath());
				AppLogger.error(errmsg);
				AADLEditor.showErrorMessage(parentComponent, errmsg);
				return false;
			}
		}
		catch (SecurityException ex) {
			String errmsg = CommonMessages.formatErrorMessage(
								EditorMessages.getInstance().msgWorkspaceCouldNotAccess,
								null, file.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			AADLEditor.showErrorMessage(parentComponent, errmsg);
			return false;
		}
		
		// valid
		return true;
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	protected void onButtonBrowse(ActionEvent ae) {
		/*
		 * 現在は、ローカルファイルシステム上のファイルのみを参照するため、
		 * JFileChooser によるファイル選択のみとする。
		 * 新しいファイルシステム(DBなど)に対応する場合は、ここの処理を
		 * 書き換える必要がある。
		 */
		
		WorkspaceComboBoxModel model = (WorkspaceComboBoxModel)_cCombo.getModel();
		
		// 選択されている位置を取得
		File selectedFile = model.getSelectedItem();
		File dirPath = FileDialogManager.chooseSaveDirectory(this, selectedFile,
											CommonMessages.getInstance().Choose_folder,
											CommonMessages.getInstance().Button_OK);
		if (dirPath == null) {
			// user canceled
			return;
		}
		// check directory
		if (!ensureDirectory(dirPath)) {
			return;
		}
		
		// 選択された位置を保存
		int index = model.getIndexOf(dirPath);
		if (index < 0) {
			model.addElement(dirPath);
			model.setSelectedItem(dirPath);
		} else {
			model.setSelectedItem(dirPath);
		}
	}

	@Override
	protected void onButtonApply() {
		// ワークスペースの履歴を消去
		_cCombo.setSelectedIndex(-1);
		_cCombo.removeAllItems();
	}

	@Override
	protected boolean onButtonOK() {
		WorkspaceComboBoxModel model = (WorkspaceComboBoxModel)_cCombo.getModel();
		
		// check selected item
		File selectedFile = model.getSelectedItem();
		if (selectedFile == null) {
			AADLEditor.showWarningMessage(this, EditorMessages.getInstance().msgWorkspaceNoSelection);
			return false;
		}
		if (!verifyWorkspaceRoot(this, false, selectedFile)) {
			// invalid directory
			return false;
		}
		
		// save history
		int len = model.getSize();
		File[] wsList = new File[len];
		for (int i = 0; i < len; i++) {
			wsList[i] = model.getElementAt(i);
		}
		
		// store settings
		AppSettings.getInstance().setWorkspaceList(wsList);
		AppSettings.getInstance().setLastWorkspace(selectedFile);
		
		return true;
	}
	
	@Override
	protected void dialogClose(int result) {
		super.dialogClose(result);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean ensureDirectory(File targetDir) {
		try {
			if (!targetDir.exists()) {
				// confirm create directory
				String msg = String.format(CommonMessages.getInstance().confirmCreateFile, targetDir.getAbsolutePath());
				int retAsk = JOptionPane.showConfirmDialog(this, msg, null, JOptionPane.OK_CANCEL_OPTION);
				if (retAsk != JOptionPane.OK_OPTION) {
					// user canceled
					return false;
				}
				
				// create directory
				try {
					if (!targetDir.mkdirs()) {
						String errmsg = CommonMessages.formatErrorMessage(
											CommonMessages.getInstance().msgCouldNotCreateDirectory,
											null, targetDir.getAbsolutePath());
						AppLogger.error(errmsg);
						AADLEditor.showErrorMessage(this, errmsg);
						return false;
					}
				} catch (SecurityException ex) {
					String errmsg = CommonMessages.formatErrorMessage(
										CommonMessages.getInstance().msgCouldNotCreateDirectory,
										ex, targetDir.getAbsolutePath());
					AppLogger.error(errmsg, ex);
					AADLEditor.showErrorMessage(this, errmsg);
					return false;
				}
			}
			else if (!targetDir.isDirectory()) {
				String errmsg = CommonMessages.formatErrorMessage(
									CommonMessages.getInstance().msgNotDirectory,
									null, targetDir.getAbsolutePath());
				AppLogger.error(errmsg);
				AADLEditor.showErrorMessage(this, errmsg);
				return false;
			}
			else if (!targetDir.canWrite()) {
				String errmsg = CommonMessages.formatErrorMessage(
						CommonMessages.getInstance().msgNotHaveWritePermission,
						null, targetDir.getAbsolutePath());
				AppLogger.error(errmsg);
				AADLEditor.showErrorMessage(this, errmsg);
				return false;
			}
		}
		catch (SecurityException ex) {
			String errmsg = CommonMessages.formatErrorMessage(
								CommonMessages.getInstance().msgCouldNotAccessDirectory,
								ex, targetDir.getAbsolutePath());
			AppLogger.error(errmsg);
			AADLEditor.showErrorMessage(this, errmsg);
			return false;
		}
		
		return true;
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected void loadSettings() {
		// 位置情報の更新
		loadSettings(AppSettings.WORKSPACE_CHOOSER, AppSettings.getInstance().getConfiguration());
	}

	@Override
	protected void saveSettings() {
		saveSettings(AppSettings.WORKSPACE_CHOOSER, AppSettings.getInstance().getConfiguration());
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	static protected class ComboCellRenderer extends DefaultListCellRenderer
	{
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel cmp = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof File) {
				Icon icon;
				try {
					icon = FileSystemView.getFileSystemView().getSystemIcon((File)value);
				} catch (Throwable ex) {
					icon = CommonResources.ICON_DELETE;
				}
				if (icon != null) {
					if (!list.isEnabled()) {
						setDisabledIcon(icon);
					} else {
						setIcon(icon);
					}
				}
			}
			return cmp;
		}
	}
}
