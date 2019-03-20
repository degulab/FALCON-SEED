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
 * @(#)PackageOptionPane.java	1.17	2010/11/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)PackageOptionPane.java	1.14	2009/12/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.module.setting.PackageSettings;
import ssac.aadl.module.setting.ProjectSettings;
import ssac.aadl.module.swing.tree.ModuleFileChooser;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.swing.StaticTextComponent;

/**
 * ビルドオプションダイアログのモジュールオプション・パネル
 * 
 * @version 1.17	2010/11/22
 * @since 1.14
 */
public class PackageOptionPane extends JPanel implements IBuildOptionPane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final PackageSettings settings;
	private final boolean readOnly;

	/** タイトル入力用フィールド **/
	private JTextField	_tcTitle;
	/** 説明入力用フィールド **/
	private JTextArea	_taDesc;
	/** メインモジュールパス **/
	private JTextComponent	_stcMainPath;
	/** メインモジュール選択ボタン **/
	private JButton		_btnSelectPath;
	/** メインモジュール削除ボタン **/
	private JButton		_btnDeletePath;
	/** 編集中のメインモジュールパス **/
	private VirtualFile	_vfMainPath;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PackageOptionPane(PackageSettings settings, boolean readOnly) {
		super(new GridBagLayout());
		if (settings == null)
			throw new NullPointerException();
		this.settings = settings;
		this.readOnly = readOnly;
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		initialComponent();
		restoreOptionSettings();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getTitle() {
		if (settings instanceof ProjectSettings) {
			return EditorMessages.getInstance().BuildOptionDlg_Title_Project;
		} else {
			return EditorMessages.getInstance().BuildOptionDlg_Title_Package;
		}
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

	public PackageSettings getOptionSettings() {
		return settings;
	}

	public void restoreOptionSettings() {
		// title
		String title = settings.getTitle();
		if (Strings.isNullOrEmpty(title)) {
			_tcTitle.setText("");
		} else {
			_tcTitle.setText(title);
		}
		
		// description
		String desc = settings.getDescription();
		if (Strings.isNullOrEmpty(desc)) {
			_taDesc.setText("");
		} else {
			_taDesc.setText(desc);
			_taDesc.setCaretPosition(0);
		}
		
		// Main Module Path
		_vfMainPath = settings.getMainModuleFile();
		updateDisplayForMainPath();
	}

	public void storeOptionSettings() {
		if (!readOnly) {
			// title
			settings.setTitle(_tcTitle.getText());

			// description
			settings.setDescription(_taDesc.getText());
			
			// Main module path
			settings.setMainModuleFile(_vfMainPath);
		}
	}

	/**
	 * このコンポーネントを保持するウィンドウが最初に表示された
	 * 直後に呼び出されるイベント。
	 * @param source	このイベントを呼び出したウィンドウ
	 * @since 1.14
	 */
	public void onWindowOpened(Window source) {}
	
	protected void onButtonSelectPath(ActionEvent event) {
		VirtualFile selected = ModuleFileChooser.chooseMailModuleFile(this,
									settings.getVirtualPropertyFile().getParentFile(),
									_vfMainPath);
		if (selected != null) {
			_vfMainPath = selected;
			updateDisplayForMainPath();
		}
	}
	
	protected void onButtonDeletePath(ActionEvent event) {
		if (_vfMainPath != null) {
			_vfMainPath = null;
			updateDisplayForMainPath();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void updateDisplayForMainPath() {
		if (_vfMainPath != null) {
			if (settings != null && settings.getVirtualPropertyFile() != null) {
				String relPath = _vfMainPath.relativePathFrom(
									settings.getVirtualPropertyFile().getParentFile(),
									Files.CommonSeparatorChar);
				_stcMainPath.setText(relPath);
			} else {
				_stcMainPath.setText(_vfMainPath.getPath());
			}
		} else {
			_stcMainPath.setText("");
		}
	}
	
	private void initialComponent() {
		// items
		Insets insets = new Insets(5, 0, 0, 0);
		//--- title
		JLabel lblTitle = new JLabel(EditorMessages.getInstance().BuildOptionDlg_Title_Package_Title);
		_tcTitle = createTextField();
		//--- description
		JLabel lblDesc = new JLabel(EditorMessages.getInstance().BuildOptionDlg_Title_Package_Desc);
		_taDesc = createTextArea();
		//--- main path
		JLabel lblMainPath = new JLabel(EditorMessages.getInstance().BuildOptionDlg_Title_Package_MainModule);
		_stcMainPath = new StaticTextComponent();
		_btnSelectPath = CommonResources.createBrowseButton(CommonMessages.getInstance().Button_Browse);
		_btnDeletePath = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		
		// Layout
		//--- init
		GridBagConstraints gbc = new GridBagConstraints();
		Insets defaultInsets = gbc.insets;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//--- title
		this.add(lblTitle, gbc);
		gbc.gridy++;
		this.add(_tcTitle, gbc);
		gbc.gridy++;
		//--- description
		gbc.insets = insets;
		this.add(lblDesc, gbc);
		gbc.gridy++;
		gbc.insets = defaultInsets;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(new JScrollPane(
					_taDesc,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
				), gbc);
		gbc.gridy++;
		//--- main module path
		gbc.gridwidth = 1;
		gbc.weighty = 0;
		gbc.insets = insets;
		this.add(lblMainPath, gbc);
		gbc.gridy++;
		gbc.insets = defaultInsets;
		this.add(_stcMainPath, gbc);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 1;
		this.add(_btnSelectPath, gbc);
		gbc.gridx = 2;
		this.add(_btnDeletePath, gbc);
		gbc.gridy++;
		//--- dummy
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 3;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		this.add(new JLabel(), gbc);
		
		// setup actions
		_btnSelectPath.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonSelectPath(e);
			}
		});
		_btnDeletePath.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonDeletePath(e);
			}
		});
		
		// set readonly
		if (readOnly) {
			_tcTitle.setEditable(false);
			_taDesc.setEditable(false);
			_btnSelectPath.setEnabled(false);
			_btnDeletePath.setEnabled(false);
			_taDesc.setBackground(_tcTitle.getBackground());
		}
	}
	
	private JTextField createTextField() {
		JTextField field = new JTextField();
		field.setEditable(true);
		return field;
	}
	
	private JTextArea createTextArea() {
		JTextArea ta = new JTextArea();
		ta.setEditable(true);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		return ta;
	}
}
