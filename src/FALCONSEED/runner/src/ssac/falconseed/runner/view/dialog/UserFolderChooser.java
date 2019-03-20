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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)UserFolderChooser.java	1.13	2011/11/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)UserFolderChooser.java	1.10	2011/02/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)UserFolderChooser.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.io.Files;
import ssac.util.logging.AppLogger;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;

/**
 * ユーザー作業領域を選択するダイアログ
 * 
 * @version 1.13	2011/11/08
 */
public class UserFolderChooser extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(500, 200);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final String	_keyPrefix;
	private JComboBox	_cCombo;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static public File chooseMExecDefUserFolder(Frame owner) {
		return chooseUserFolder(owner,
					RunnerMessages.getInstance().UserFolderChooserDlg_TitleForMExecDef,
					AppSettings.KEY_PREFIX_MEXECDEF_USERFOLDER);
	}
	
	static public File chooseDataFileUserFolder(Frame owner) {
		return chooseUserFolder(owner,
					RunnerMessages.getInstance().UserFolderChooserDlg_TitleForDataFile,
					AppSettings.KEY_PREFIX_DATAFILE_USERFOLDER);
	}
	
	static protected File chooseUserFolder(Frame owner, String title, String keyPrefix) {
		UserFolderChooser chooser = new UserFolderChooser(owner, title, keyPrefix);
		chooser.initialComponent();
		chooser.setVisible(true);
		chooser.dispose();
		
		if (chooser.getDialogResult() != IDialogResult.DialogResult_OK) {
			// user canceled
			return null;
		}
		
		File newUserDir = AppSettings.getInstance().getAvailableUserRootDirectory(keyPrefix);
		return newUserDir;
	}
	
	protected UserFolderChooser(Frame owner, String title, String keyPrefix) {
		super(owner, title, true);
		setConfiguration(AppSettings.USERFOLDERCHOOSER_DLG, AppSettings.getInstance().getConfiguration());
		if (keyPrefix==null || keyPrefix.length() <= 0)
			throw new IllegalArgumentException("The specified key prefix is null or empty.");
		this._keyPrefix = keyPrefix;
	}
	
	@Override
	public void initialComponent() {
		super.initialComponent();
		
		restoreConfiguration();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public File getSelectedFile() {
		return (File)_cCombo.getSelectedItem();
	}
	
	/**
	 * 指定されたファイルがユーザーフォルダのルートとして適正であるかを検証する。
	 * このメソッドが <tt>false</tt> を返す場合、適切なエラーメッセージがメッセージボックスとして表示される。
	 * @param parentComponent		エラーメッセージを表示する際の親コンポーネント
	 * @param beSilent				エラーメッセージを表示しない場合は <tt>true</tt>
	 * @param ignoreCheckExists		抽象パスが示すディレクトリの有無をチェックしない場合は <tt>true</tt> を指定する
	 * @param file					検証対象の抽象パス
	 * @return	適正であれば <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	<em>file</em> が <tt>null</tt> の場合
	 */
	static public boolean verifyUserFolderRoot(Component parentComponent,
												boolean beSilent,
												boolean ignoreCheckExists,
												File file)
	{
		try {
			// check path
			{
				File fSystemRootBase = AppSettings.getInstance().getDefaultSystemRootBaseDirectory();
				if (file.equals(fSystemRootBase) || Files.isDescendingFrom(file, fSystemRootBase)) {
					// システムルート配下は選択不可
					String errmsg = CommonMessages.formatErrorMessage(
										RunnerMessages.getInstance().msgUserFolderCouldNotUse,
										null, file.getAbsolutePath());
					AppLogger.error(errmsg);
					if (!beSilent) {
						Application.showErrorMessage(parentComponent, errmsg);
					}
					return false;
				}
				else if (Files.isDescendingFrom(fSystemRootBase, file)) {
					// システムルートを含むフォルダは選択不可
					String errmsg = CommonMessages.formatErrorMessage(
										RunnerMessages.getInstance().msgUserFolderIncludesSystemRoot,
										null, file.getAbsolutePath());
					AppLogger.error(errmsg);
					if (!beSilent) {
						Application.showErrorMessage(parentComponent, errmsg);
					}
					return false;
				}
			}
			
			// check attributes
			if (!ignoreCheckExists) {
				if (!file.exists()) {
					//--- check exists
					String errmsg = CommonMessages.formatErrorMessage(
										RunnerMessages.getInstance().msgUserFolderNotFound,
										null, file.getAbsolutePath());
					AppLogger.error(errmsg);
					if (!beSilent) {
						Application.showErrorMessage(parentComponent, errmsg);
					}
					return false;
				}
				else if (!file.isDirectory()) {
					//--- check is directory
					String errmsg = CommonMessages.formatErrorMessage(
										RunnerMessages.getInstance().msgUserFolderNotDirectory,
										null, file.getAbsolutePath());
					AppLogger.error(errmsg);
					if (!beSilent) {
						Application.showErrorMessage(parentComponent, errmsg);
					}
					return false;
				}
				else if (!file.canWrite()) {
					//--- check can write
					String errmsg = CommonMessages.formatErrorMessage(
										RunnerMessages.getInstance().msgUserFolderCouldNotWrite,
										null, file.getAbsolutePath());
					AppLogger.error(errmsg);
					if (!beSilent) {
						Application.showErrorMessage(parentComponent, errmsg);
					}
					return false;
				}
			}
		}
		catch (SecurityException ex) {
			String errmsg = CommonMessages.formatErrorMessage(
								RunnerMessages.getInstance().msgUserFolderCouldNotAccess,
								null, file.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			if (!beSilent) {
				Application.showErrorMessage(parentComponent, errmsg);
			}
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
		 */
		
		UserFolderComboBoxModel model = (UserFolderComboBoxModel)_cCombo.getModel();
		
		// 選択されている位置を取得
		File selectedFile = model.getSelectedItem();
		File dirPath = FileChooserManager.chooseSaveDirectory(this, selectedFile,
													CommonMessages.getInstance().Choose_folder,
													CommonMessages.getInstance().Button_OK);
		/*
		File dirPath = null;
		if (0 <= System.getProperty("os.name").indexOf("Mac")) {
			//--- for Mac OS X
			JFileChooser chooser = new JFileChooser(){
				@Override
				public void approveSelection() {
					File f = getSelectedFile();
					
					if (f == null) {
						return;		// no selection
					}
					
					if (!f.isDirectory()) {
						// can select an existing directory
						JOptionPane.showMessageDialog(this,
								String.format(RunnerMessages.getInstance().msgLongSelectExistingFolder, f.getAbsolutePath()),
								CommonMessages.getInstance().msgboxTitleError,
								JOptionPane.ERROR_MESSAGE);
						return;		// cancel to approve
					}

					// approve selection
					super.approveSelection();
				}
			};
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setMultiSelectionEnabled(false);
			
			// setup initial file
			File initDir = null;
			if (selectedFile != null) {
				try {
					if (selectedFile.isDirectory()) {
						initDir = selectedFile;
					}
					else {
						File parentDir = selectedFile.getParentFile();
						if (parentDir != null && parentDir.isDirectory()) {
							initDir = parentDir;
						}
					}
				}
				catch (Throwable ex) {
					// no implement
				}
			}
			if (initDir == null) {
				initDir = new File("");
			}
			chooser.setCurrentDirectory(initDir);
			
			chooser.setDialogTitle(CommonMessages.getInstance().Choose_folder);
			chooser.setApproveButtonText(CommonMessages.getInstance().Button_OK);
			
			int ret = chooser.showSaveDialog(this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				// selected
				dirPath = chooser.getSelectedFile();
			}
		} else {
			//--- Other OS
			dirPath = FileDialogManager.chooseSaveDirectory(this, selectedFile,
													CommonMessages.getInstance().Choose_folder,
													CommonMessages.getInstance().Button_OK);
		}
		*/
		
		
		if (dirPath == null) {
			// user canceled
			return;
		}
		
		// check directory first
		if (!verifyUserFolderRoot(this, false, true, dirPath)) {
			return;
		}
		
		// すでにリストにある？
		if (model.getIndexOf(dirPath) >= 0) {
			model.setSelectedItem(dirPath);
			return;
		}
		
		// check directory
		if (!ensureDirectory(dirPath)) {
			return;
		}
		
		// 正当性検証
		if (!verifyUserFolderRoot(this, false, false, dirPath)) {
			return;
		}
		
		// 新規保存
		model.addElement(dirPath);
		model.setSelectedItem(dirPath);
	}

	@Override
	protected void doApplyAction() {
		// 履歴を消去
		((UserFolderComboBoxModel)_cCombo.getModel()).removeAllElements();
		//_cCombo.setSelectedIndex(0);
	}

	@Override
	protected boolean doOkAction() {
		UserFolderComboBoxModel model = (UserFolderComboBoxModel)_cCombo.getModel();
		
		// check selected item
		File selectedFile = model.getSelectedItem();
		if (selectedFile == null) {
			Application.showWarningMessage(this, RunnerMessages.getInstance().msgUserFolderNoSelection);
			return false;
		}
		if (!verifyUserFolderRoot(this, false, false, selectedFile)) {
			// invalid directory
			return false;
		}
		
		// save history
		AppSettings.getInstance().setUserFolderArray(getKeyPrefix(), model.getInternalList());
		AppSettings.getInstance().setLatestUserFolder(getKeyPrefix(), selectedFile);
		
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String getKeyPrefix() {
		return _keyPrefix;
	}
	
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
						Application.showErrorMessage(this, errmsg);
						return false;
					}
				} catch (SecurityException ex) {
					String errmsg = CommonMessages.formatErrorMessage(
										CommonMessages.getInstance().msgCouldNotCreateDirectory,
										ex, targetDir.getAbsolutePath());
					AppLogger.error(errmsg, ex);
					Application.showErrorMessage(this, errmsg);
					return false;
				}
			}
			else if (!targetDir.isDirectory()) {
				String errmsg = CommonMessages.formatErrorMessage(
									CommonMessages.getInstance().msgNotDirectory,
									null, targetDir.getAbsolutePath());
				AppLogger.error(errmsg);
				Application.showErrorMessage(this, errmsg);
				return false;
			}
			else if (!targetDir.canWrite()) {
				String errmsg = CommonMessages.formatErrorMessage(
						CommonMessages.getInstance().msgNotHaveWritePermission,
						null, targetDir.getAbsolutePath());
				AppLogger.error(errmsg);
				Application.showErrorMessage(this, errmsg);
				return false;
			}
		}
		catch (SecurityException ex) {
			String errmsg = CommonMessages.formatErrorMessage(
								CommonMessages.getInstance().msgCouldNotAccessDirectory,
								ex, targetDir.getAbsolutePath());
			AppLogger.error(errmsg);
			Application.showErrorMessage(this, errmsg);
			return false;
		}
		
		return true;
	}

	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();
		
		setResizable(true);
		setStoreLocation(false);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200, 300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
	}

	@Override
	protected void setupMainContents() {
		// restore values from settings
		File lastPath = AppSettings.getInstance().getAvailableUserRootDirectory(getKeyPrefix());
		File[] paths = AppSettings.getInstance().getUserFolderArray(getKeyPrefix());
		File defPath = AppSettings.getInstance().getDefaultUserRootDirectory(getKeyPrefix(), true);
		
		// モデル生成
		UserFolderComboBoxModel newModel = new UserFolderComboBoxModel(defPath, "(Default) ", paths);
		if (AppSettings.getInstance().hasFilesInOldDefaultUserRootDirectory(getKeyPrefix())) {
			File oldDefPath = AppSettings.getInstance().getOldDefaultUserRootDirectory(getKeyPrefix(), false);
			// TODO: 固定文字列
			newModel.setOldDefaultFile(oldDefPath, "(Old default) ");
		}
		if (lastPath == null) {
			newModel.setSelectedItem(defPath);
		} else {
			int index = newModel.getIndexOf(lastPath);
			if (index < 0) {
				newModel.setSelectedItem(defPath);
			} else {
				newModel.setSelectedItem(lastPath);
			}
		}
		
		// create components
		_cCombo = new JComboBox(newModel);
		_cCombo.setEditable(false);
		_cCombo.setRenderer(new ComboCellRenderer());
		JButton btnBrowse = new JButton(CommonMessages.getInstance().Button_Browse);
		
		// create main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// Layout
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
		
		// add to root
		this.add(mainPanel, BorderLayout.CENTER);
		
		// setup Actions
		btnBrowse.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonBrowse(e);
			}
		});
	}
	
	@Override
	protected String getDefaultApplyButtonCaption() {
		return CommonMessages.getInstance().Button_RemoveHistories;
	}
	
	@Override
	protected JComponent createButtonsPanel() {
		// [履歴の消去]ボタンは左寄せ
		JButton[] buttons = createButtons();
		if (buttons == null || buttons.length < 1) {
			// no buttons
			return null;
		}
		
		int maxHeight = adjustButtonSize(buttons);
		
		// Layout
		int index = 0;
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(buttons[index++]);
		btnBox.add(Box.createHorizontalGlue());
		for (; index < buttons.length; index++) {
			btnBox.add(buttons[index]);
			btnBox.add(Box.createHorizontalStrut(5));
		}
		btnBox.add(Box.createRigidArea(new Dimension(0, maxHeight+10)));
		return btnBox;
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
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
