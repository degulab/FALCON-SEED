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
 * @(#)ModuleOptionPane.java	1.17	2010/11/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ModuleOptionPane.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.view.table.ModuleArgDetailTableModel;
import ssac.aadl.editor.view.table.ModuleArgDetailTablePane;
import ssac.aadl.module.ModuleArgDetail;
import ssac.aadl.module.setting.ModuleSettings;
import ssac.util.Strings;

/**
 * ビルドオプションダイアログのモジュールオプション・パネル
 * 
 * @version 1.17	2010/11/22
 * @since 1.14
 */
public class ModuleOptionPane extends JPanel implements IBuildOptionPane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ModuleSettings settings;
	private final boolean readOnly;

	/** タイトル入力用フィールド **/
	private JTextField	_tcTitle;
	/** 説明入力用フィールド **/
	private JTextArea	_taDesc;
	/** 備考入力用フィールド **/
	private JTextArea	_taNote;
	/** 引数定義用コンポーネント **/
	private ModuleArgDetailTablePane	_table;
	/** 引数定義への行追加ボタン **/
	private JButton	_btnArgAdd;
	/** 引数定義の行削除ボタン **/
	private JButton	_btnArgDel;
	/** 引数定義行の上へ移動ボタン **/
	private JButton	_btnArgUp;
	/** 引数定義行の下へ移動ボタン **/
	private JButton	_btnArgDown;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleOptionPane(ModuleSettings settings, boolean readOnly) {
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
		return EditorMessages.getInstance().BuildOptionDlg_TItle_Module;
	}
	
	public ModuleArgDetail[] getArgumentDetails() {
		ModuleArgDetailTableModel model = _table.getTableModel();
		int numRows = model.getRowCount();
		ModuleArgDetail[] details = new ModuleArgDetail[numRows];
		for (int row = 0; row < numRows; row++) {
			details[row] = new ModuleArgDetail(model.getArgumentAttr(row), model.getArgumentDescription(row));
		}
		return details;
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

	public ModuleSettings getOptionSettings() {
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
		
		// note
		String note = settings.getNote();
		if (Strings.isNullOrEmpty(note)) {
			_taNote.setText("");
		} else {
			_taNote.setText(note);
			_taNote.setCaretPosition(0);
		}
		
		// arguments
		ModuleArgDetailTableModel newModel = new ModuleArgDetailTableModel(settings);
		_table.setTableModel(newModel);
	}

	public void storeOptionSettings() {
		// 引数編集確定
		_table.stopTableCellEditing();

		if (!readOnly) {
			// title
			settings.setTitle(_tcTitle.getText());

			// description
			settings.setDescription(_taDesc.getText());

			// note
			settings.setNote(_taNote.getText());

			// arguments
			_table.getTableModel().storeToSettin(settings);
		}
	}

	/**
	 * このコンポーネントを保持するウィンドウが最初に表示された
	 * 直後に呼び出されるイベント。
	 * @param source	このイベントを呼び出したウィンドウ
	 * @since 1.14
	 */
	public void onWindowOpened(Window source) {
		_table.adjustTableAllColumnsPreferredWidth();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private void initialComponent() {
		// items
		Insets insets = new Insets(5, 0, 0, 0);
		//--- title
		JLabel lblTitle = new JLabel(EditorMessages.getInstance().BuildOptionDlg_TItle_Module_Title);
		_tcTitle = createTextField();
		//--- description
		JLabel lblDesc = new JLabel(EditorMessages.getInstance().BuildOptionDlg_Title_Module_Desc);
		_taDesc = createTextArea();
		//--- note
		JLabel lblNote = new JLabel(EditorMessages.getInstance().BuildOptionDlg_Title_Module_Note);
		_taNote = createTextArea();
		//--- arguments
		JLabel lblArgs = new JLabel(EditorMessages.getInstance().BuildOptionDlg_Title_Module_Args);
		JPanel pnlTable = createArgumentTablePanel();
		
		// Layout
		//--- init
		GridBagConstraints gbc = new GridBagConstraints();
		Insets defaultInsets = gbc.insets;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
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
		//--- arguments
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = insets;
		this.add(lblArgs, gbc);
		gbc.gridy++;
		gbc.insets = defaultInsets;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(pnlTable, gbc);
		gbc.gridy++;
		//--- note
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = insets;
		this.add(lblNote, gbc);
		gbc.gridy++;
		gbc.insets = defaultInsets;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(new JScrollPane(
					_taNote,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
				), gbc);
		
		// set readonly
		if (readOnly) {
			_tcTitle.setEditable(false);
			_taDesc.setEditable(false);
			_taNote.setEditable(false);
			_table.setEditable(false);
			_taDesc.setBackground(_tcTitle.getBackground());
			_taNote.setBackground(_tcTitle.getBackground());
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
	
	private JPanel createArgumentTablePanel() {
		//--- Buttons
		_btnArgAdd  = CommonResources.createIconButton(CommonResources.ICON_ADD, CommonMessages.getInstance().Button_Add);
		_btnArgDel  = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		_btnArgUp   = CommonResources.createIconButton(CommonResources.ICON_ARROW_UP, CommonMessages.getInstance().Button_Up);
		_btnArgDown = CommonResources.createIconButton(CommonResources.ICON_ARROW_DOWN, CommonMessages.getInstance().Button_Down);
		//--- table
		_table = new ModuleArgDetailTablePane();
		_table.initialComponent();
		_table.attachAddButton(_btnArgAdd);
		_table.attachDeleteButton(_btnArgDel);
		_table.attachUpButton(_btnArgUp);
		_table.attachDownButton(_btnArgDown);
		//--- panel
		JPanel panel = new JPanel(new GridBagLayout());
		
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
		gbc.gridheight = 5;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(_table, gbc);
		//--- buttons
		if (!readOnly) {
			gbc.anchor = GridBagConstraints.NORTHEAST;
			gbc.fill = GridBagConstraints.NONE;
			gbc.gridheight = 1;
			gbc.weightx = 0;
			gbc.weighty = 0;
			gbc.gridx = 1;
			gbc.gridy = 0;
			panel.add(_btnArgAdd, gbc);
			gbc.gridy++;
			panel.add(_btnArgDel, gbc);
			gbc.gridy++;
			panel.add(_btnArgUp, gbc);
			gbc.gridy++;
			panel.add(_btnArgDown, gbc);
			//--- dummy
			JLabel lblDummy = new JLabel();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weighty = 1;
			gbc.gridy++;
			panel.add(lblDummy, gbc);
		}
		
		return panel;
	}
}
