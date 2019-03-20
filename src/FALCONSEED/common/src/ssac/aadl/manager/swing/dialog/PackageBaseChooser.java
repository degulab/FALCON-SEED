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
 * @(#)PackageBaseChooser.java	3.2.2	2015/10/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)PackageBaseChooser.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.manager.swing.dialog;

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
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

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

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.common.PackageBaseSettings;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.PackageBaseLocation;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.aadl.module.swing.list.PackageBaseComboBoxModel;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.Application;
import ssac.util.swing.BasicDialog;

/**
 * パッケージ格納位置の基準となる位置を選択するダイアログ
 * 
 * @version 3.2.2
 * @since 1.14
 */
public class PackageBaseChooser extends BasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -4501548202685815034L;

	static private final Dimension DM_MIN_SIZE = new Dimension(500, 200);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final String _configPrefix;
	private final ExConfiguration _cConfig;
	
	private JComboBox _cCombo;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PackageBaseChooser(Frame owner, String prefix, ExConfiguration config) {
		super(owner, CommonMessages.getInstance().PackageBaseChooser_Title, true);
		if (config != null) {
			this._configPrefix = prefix;
			this._cConfig = config;
		} else {
			this._configPrefix = null;
			this._cConfig = null;
		}
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
		// get settings
		PackageBaseSettings settings = PackageBaseSettings.getInstance();
		settings.rollback();
		URI selectedURI = settings.getLastPackageBase();
		URI[] baseURIs = settings.getPackageBases();
		
		// create ComboBox model
		ArrayList<PackageBaseLocation> baseList = new ArrayList<PackageBaseLocation>(baseURIs.length);
		for (URI uri : baseURIs) {
			try {
				PackageBaseLocation newBase = new PackageBaseLocation(uri);
				baseList.add(newBase);
			} catch (Throwable ex) {
				AppLogger.debug("Failed to create PackageBaseLocation from URI : " + String.valueOf(uri), ex);
			}
		}
		PackageBaseComboBoxModel model = new PackageBaseComboBoxModel(baseList);
		
		// selected last URI
		if (selectedURI != null) {
			try {
				PackageBaseLocation selectedBase = new PackageBaseLocation(selectedURI);
				model.setSelectedItem(selectedBase);
			} catch (Throwable ex) {
				AppLogger.debug("Failed to create PackageBaseLocation from last URI : " + String.valueOf(selectedURI), ex);
			}
		}

		// create ComboBox
		_cCombo = new JComboBox(model);
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
	
	/**
	 * 指定されたファイルがパッケージベースとして適正であるかを検証する。
	 * このメソッドが <tt>false</tt> を返す場合、適切なエラーメッセージがメッセージボックスとして表示される。
	 * @param parentComponent		エラーメッセージを表示する際の親コンポーネント
	 * @param ignoreCheckExists		抽象パスが示すディレクトリの有無をチェックしない場合は <tt>true</tt> を指定する
	 * @param file					検証対象の抽象パス
	 * @return	適正であれば <tt>true</tt>、そうでない場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	<em>file</em> が <tt>null</tt> の場合
	 */
	static public boolean verifyPackageBase(Component parentComponent,
												boolean ignoreCheckExists,
												VirtualFile file)
	{
		try {
			// check
			if (!ignoreCheckExists && !file.exists()) {
				//--- check exists
				String errmsg = CommonMessages.formatErrorMessage(
									CommonMessages.getInstance().msgPackBaseNotFound,
									null, file.getAbsolutePath());
				AppLogger.error(errmsg);
				Application.showErrorMessage(parentComponent, errmsg);
				return false;
			}
			else if (!file.isDirectory()) {
				//--- check is directory
				String errmsg = CommonMessages.formatErrorMessage(
									CommonMessages.getInstance().msgPackBaseNotDirectory,
									null, file.getAbsolutePath());
				AppLogger.error(errmsg);
				Application.showErrorMessage(parentComponent, errmsg);
				return false;
			}
			else if (ModuleFileManager.getTopProjectPrefsFile(file, null) != null) {
				//--- check project root
				String errmsg = CommonMessages.formatErrorMessage(
									CommonMessages.getInstance().msgCouldNotSelectInProject,
									null, file.getAbsolutePath());
				AppLogger.error(errmsg);
				Application.showErrorMessage(parentComponent, errmsg);
				return false;
			}
			else if (ModuleFileManager.getTopModulePackagePrefsFile(file, null) != null) {
				//--- check package root
				String errmsg = CommonMessages.formatErrorMessage(
									CommonMessages.getInstance().msgCouldNotSelectInPackage,
									null, file.getAbsolutePath());
				AppLogger.error(errmsg);
				Application.showErrorMessage(parentComponent, errmsg);
				return false;
			}
		}
		catch (SecurityException ex) {
			String errmsg = CommonMessages.formatErrorMessage(
									CommonMessages.getInstance().msgPackBaseCannotAccess,
									ex, file.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
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
		
		PackageBaseComboBoxModel model = (PackageBaseComboBoxModel)_cCombo.getModel();
		
		// 選択されている位置を取得
		File initDir = null;
		PackageBaseLocation selectedBase = model.getSelectedItem();
		if (selectedBase != null) {
			try {
				initDir = new File(selectedBase.getURI());
			} catch (Throwable ex) {
				initDir = null;
			}
		}

		File dirPath = FileDialogManager.chooseSaveDirectory(this, initDir,
							CommonMessages.getInstance().Choose_folder,
							CommonMessages.getInstance().Button_OK);
		if (dirPath == null) {
			// user canceled
			return;
		}
		// check directory
		if (!ensureFileDirectory(ModuleFileManager.fromJavaFile(dirPath))) {
			return;
		}
		
		// ロケーションを生成
		PackageBaseLocation newBase = new PackageBaseLocation(dirPath.toURI());
		int index = model.getIndexOf(newBase);
		if (index < 0) {
			model.addElement(newBase);
			model.setSelectedItem(newBase);
		} else {
			model.setSelectedItem(model.getElementAt(index));
		}
	}
	
	@Override
	protected void onButtonApply() {
		// 履歴を消去
		_cCombo.setSelectedIndex(-1);
		_cCombo.removeAllItems();
	}

	@Override
	protected boolean onButtonOK() {
		PackageBaseComboBoxModel model = (PackageBaseComboBoxModel)_cCombo.getModel();
		
		// check selected item
		PackageBaseLocation selectedBase = model.getSelectedItem();
		if (selectedBase == null) {
			Application.showErrorMessage(this, CommonMessages.getInstance().msgPackBaseNoSelection);
			return false;
		}
		if (!verifyPackageBase(this, false, selectedBase.getFileObject())) {
			// invalid directory
			return false;
		}
		
		// save history
		URI[] baseList = new URI[model.getSize()];
		for (int i = 0; i < model.getSize(); i++) {
			PackageBaseLocation base = model.getElementAt(i);
			baseList[i] = base.getURI();
		}
		URI selectedURI = selectedBase.getURI();

		// commit settings
		PackageBaseSettings settings = PackageBaseSettings.getInstance();
		settings.setPackageBases(baseList);
		settings.setLastPackageBase(selectedURI);
		boolean acceptClose;
		try {
			settings.commit();
			acceptClose = true;
		}
		catch (IOException ex) {
			AppLogger.error("Failed to commit Package base settings!", ex);
			Application.showErrorMessage(this, CommonMessages.getInstance().msgCouldNotSaveSetting);
			acceptClose = false;
		}
		
		return acceptClose;
	}
	
	@Override
	protected void dialogClose(int result) {
		super.dialogClose(result);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean ensureFileDirectory(VirtualFile targetDir) {
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
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected void loadSettings() {
		loadSettings(_configPrefix, _cConfig);
	}

	@Override
	protected void saveSettings() {
		if (_cConfig != null) {
			saveSettings(_configPrefix, _cConfig);
		}
		/*
		if (AppLogger.isDebugEnabled()) {
			Point loc = this.getLocationOnScreen();
			Dimension dm = this.getSize();
			AppLogger.debug("PackageBaseChooser Location : " + loc.toString());
			AppLogger.debug("PackageBaseChooser Size : " + dm.toString());
		}
		*/
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	static protected class ComboCellRenderer extends DefaultListCellRenderer
	{
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel cmp = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof PackageBaseLocation) {
				PackageBaseLocation base = (PackageBaseLocation)value;
				Icon icon;
				try {
					if (base.getFileObject().exists()) {
						icon = base.getDisplayIcon();
					} else {
						icon = CommonResources.ICON_DELETE;
					}
				} catch (Throwable ex) {
					icon = CommonResources.ICON_DELETE;
				}
				//--- setup Icon
				if (icon != null) {
					if (!list.isEnabled()) {
						setDisabledIcon(base.getDisplayIcon());
					} else {
						setIcon(base.getDisplayIcon());
					}
				}
			}
			return cmp;
		}
	}
}
