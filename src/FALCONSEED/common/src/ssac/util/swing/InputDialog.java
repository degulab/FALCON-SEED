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
 * @(#)InputDialog.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.util.Strings;

/**
 * テキスト入力ダイアログ
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.14
 */
public class InputDialog extends BasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DEFAULT_MIN_SIZE = new Dimension(320, 120);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** テキストフィールドの入力を監視するフィルタ **/
	protected TextFieldCharacterValidator	_validator;
	/** 説明表示用ラベルコンポーネント **/
	protected JTextPane	_lblDesc;
	/** 入力フィールドのラベル **/
	protected JLabel		_lblInput;
	/** 入力用テキストフィールド **/
	protected JTextField	_field;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public InputDialog() {
		this((Frame)null, null, null, null, null, null, null);
	}
	
	public InputDialog(String label, Icon icon, String title, String description, String initialValue, TextFieldCharacterValidator validator) {
		this((Frame)null, label, icon, title, description, initialValue, validator);
	}
	
	public InputDialog(Frame owner, String label, Icon icon, String title, String description, String initialValue, TextFieldCharacterValidator validator) {
		super(owner, title, true);
		setupComponents(label, icon, description, initialValue, validator);
	}
	
	public InputDialog(Dialog owner, String label, Icon icon, String title, String description, String initialValue, TextFieldCharacterValidator validator) {
		super(owner, title, true);
		setupComponents(label, icon, description, initialValue, validator);
	}
	
	protected void setupComponents(String label, Icon icon, String description, String initialValue, TextFieldCharacterValidator validator) {
		// setup parameters
		setDescription(description);
		setInputLabel(label);
		setInputIcon(icon);
		setFieldText(initialValue);
		
		// setup validator
		this._validator = validator;
		
		// setup dialog
		setResizable(false);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = DEFAULT_MIN_SIZE;
		}
		this.setMinimumSize(dmMin);
		setKeepMinimumSize(true);
		
		// restore settings
		//--- ダイアログのサイズを更新する
		loadSettings(null, null);
		
		// setup actions
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public JTextField getTextField() {
		return _field;
	}

	public String getDescription() {
		return _lblDesc.getText();
	}
	
	public void setDescription(String description) {
		if (Strings.isNullOrEmpty(description)) {
			_lblDesc.setVisible(false);
			_lblDesc.setText("");
		} else {
			_lblDesc.setText(description);
			_lblDesc.setVisible(true);
		}
	}
	
	public String getInputLabel() {
		return _lblInput.getText();
	}
	
	public Icon getInputIcon() {
		return _lblInput.getIcon();
	}
	
	public void setInputLabel(String label) {
		if (Strings.isNullOrEmpty(label)) {
			if (_lblInput.getIcon() == null) {
				_lblInput.setVisible(false);
			}
			_lblInput.setText("");
		} else {
			_lblInput.setText(label);
			_lblInput.setVisible(true);
		}
	}
	
	public void setInputIcon(Icon icon) {
		if (icon == null) {
			if (Strings.isNullOrEmpty(_lblInput.getText())) {
				_lblInput.setVisible(false);
			}
			_lblInput.setIcon(null);
		} else {
			_lblInput.setIcon(icon);
			_lblInput.setVisible(true);
		}
	}
	
	public String getFieldText() {
		return _field.getText();
	}
	
	public void setFieldText(String text) {
		_field.setText(Strings.isNullOrEmpty(text) ? "" : text);
	}
	
	public TextFieldCharacterValidator getValidator() {
		return _validator;
	}
	
	public void setValidator(TextFieldCharacterValidator validator) {
		if (validator == _validator) {
			return;
		}
		
		TextFieldCharacterValidator oldValidator = _validator;
		_validator = validator;
		
		if (oldValidator != null) {
			oldValidator.setTargetTextField(null);
		}
		
		if (validator != null) {
			validator.setTargetTextField(_field);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected JTextPane createDescriptionLabel() {
		JTextPane pane = new JTextPane();
		pane.setEditable(false);
		pane.setFocusable(false);
		pane.setForeground(UIManager.getColor("Label.foreground"));
		pane.setOpaque(false);
		
		SimpleAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setLineSpacing(attr, -0.2f);
		pane.setParagraphAttributes(attr, true);
		
		pane.setMargin(new Insets(CommonResources.DIALOG_CONTENT_MARGIN,
									CommonResources.DIALOG_CONTENT_MARGIN,
									CommonResources.DIALOG_CONTENT_MARGIN,
									CommonResources.DIALOG_CONTENT_MARGIN));
		
		pane.setText("description");
		return pane;
	}
	
	protected JLabel createInputLabel() {
		JLabel label = new JLabel("input");
		return label;
	}
	
	protected JTextField createTextField() {
		JTextField field = new JTextField();
		field.setEditable(true);
		return field;
	}

	@Override
	protected void setupMainPanel() {
		super.setupMainPanel();
		
		// create components
		this._lblDesc  = createDescriptionLabel();
		this._lblInput = createInputLabel();
		this._field    = createTextField();
		
		// setup main panel layout
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		mainPanel.add(_lblInput, gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(_field, gbc);
		
		// setup message layout
		this.getContentPane().add(_lblDesc, BorderLayout.NORTH);
	}

	@Override
	protected void setupButtonPanel() {
		int maxWidth = 0;
		int maxHeight = 0;
		Dimension dim;
		this.btnCancel.setText(CommonMessages.getInstance().Button_Cancel);
		this.btnOK.setText(CommonMessages.getInstance().Button_OK);
		dim = this.btnCancel.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		dim = this.btnOK.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		//---
		dim = new Dimension(maxWidth, maxHeight);
		//btnApply .setMinimumSize(dim);
		this.btnApply .setPreferredSize(dim);
		//btnOK    .setMinimumSize(dim);
		this.btnOK    .setPreferredSize(dim);
		//btnCancel.setMinimumSize(dim);
		this.btnCancel.setPreferredSize(dim);
		
		Box btnBox = Box.createHorizontalBox();
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

	@Override
	protected Dimension getDefaultSize() {
		return null;
	}

	@Override
	protected void initDialog() {
		// 基本処理
		super.initDialog();
		
		// フィールドバリデータを接続
		if (_validator != null) {
			_validator.setTargetTextField(_field);
		}
	}

	@Override
	protected void dialogClose(int result) {
		// フィールドバリデータの関連を切断
		if (_validator != null) {
			_validator.setTargetTextField(null);
		}
		
		// 基本処理
		super.dialogClose(result);
	}

	@Override
	protected boolean onButtonOK() {
		// バリデータがあれば、入力文字列をチェック
		if (_validator != null) {
			if (!_validator.verify(_field, _field.getText())) {
				// エラーのため、ダイアログを閉じない
				return false;
			}
		}
		
		// ダイアログを閉じる
		return true;
	}

	@Override
	protected boolean onButtonCancel() {
		return super.onButtonCancel();
	}
}
