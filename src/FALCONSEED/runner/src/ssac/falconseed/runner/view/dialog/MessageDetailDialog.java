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
 */
/*
 * @(#)MessageDetailDialog.java	1.22	2012/08/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import ssac.falconseed.runner.RunnerMessages;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.JMultilineLabel;
import ssac.util.swing.SwingTools;

/**
 * モジュール実行履歴のエラーダイアログ。
 * 
 * @version 1.22	2012/08/19
 * @since 1.22
 */
public class MessageDetailDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	//static private final Dimension DM_MIN_SIZE = new Dimension(600, 400);
	
	static public int YES_NO_OPTION			= JOptionPane.YES_NO_OPTION;
	static public int YES_NO_CANCEL_OPTION	= JOptionPane.YES_NO_CANCEL_OPTION;
	static public int OK_CANCEL_OPTION		= JOptionPane.OK_CANCEL_OPTION;
	
	static public int YES_OPTION		= JOptionPane.YES_OPTION;
	static public int NO_OPTION		= JOptionPane.NO_OPTION;
	static public int OK_OPTION		= JOptionPane.OK_OPTION;
	static public int CANCEL_OPTION	= JOptionPane.CANCEL_OPTION;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final int			_optionType;
	private JMultilineLabel	_message;
	private JTextArea			_detail;
	
	private int				_optionResult;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	static public void showErrorDetailMessage(Component parentComponent, String title, String message, String detail) {
		MessageDetailDialog dlg;
		Window window = SwingTools.getWindowForComponent(parentComponent);
		if (window instanceof Frame) {
			dlg = new MessageDetailDialog((Frame)window, title, JOptionPane.DEFAULT_OPTION);
		} else {
			dlg = new MessageDetailDialog((Dialog)window, title, JOptionPane.DEFAULT_OPTION);
		}
		dlg.initialComponent();
		dlg.setMessage(message);
		dlg.setDetail(detail);
		dlg.pack();
		dlg.setLocationRelativeTo(window);
		
		dlg.setVisible(true);
		dlg.dispose();
	}
	
	static public int showConfirmDetailMessage(Component parentComponent, String title, String message, String detail, int optionType) {
		MessageDetailDialog dlg;
		Window window = SwingTools.getWindowForComponent(parentComponent);
		if (window instanceof Frame) {
			dlg = new MessageDetailDialog((Frame)window, title, optionType);
		} else {
			dlg = new MessageDetailDialog((Dialog)window, title, optionType);
		}
		dlg.initialComponent();
		dlg.setMessage(message);
		dlg.setDetail(detail);
		dlg.pack();
		dlg.setLocationRelativeTo(window);
		
		dlg.setVisible(true);
		dlg.dispose();
		return dlg._optionResult;
	}
	
	protected MessageDetailDialog(Frame owner, String title, int optionType) {
		super(owner, title, true);
		_optionType = optionType;
	}
	
	protected MessageDetailDialog(Dialog owner, String title, int optionType) {
		super(owner, title, true);
		_optionType = optionType;
	}
	
	@Override
	public void initialComponent() {
		super.initialComponent();
		setResizable(true);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getMessage() {
		return _message.getText();
	}
	
	public void setMessage(String message) {
		_message.setText(message==null ? "" : message);
	}
	
	public String getDetail() {
		return _detail.getText();
	}
	
	public void setDetail(String detail) {
		_detail.setText(detail==null ? "" : detail);
		_detail.setCaretPosition(0);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected void setupActions() {
		super.setupActions();
	}

	@Override
	protected void setupButtons() {
		super.setupButtons();
	}
	
	protected Action createCopyToClipAction() {
		Action action = new AbstractAction(RunnerMessages.getInstance().LibVersionDlg_Title_CopyToClip) {
			public void actionPerformed(ActionEvent ae) {
				onClickCopyToClipButton();
			}
		};
		return action;
	}
	
	protected Action createYesButtonAction() {
		Action action = new AbstractAction(UIManager.getString("OptionPane.yesButtonText")) {
			public void actionPerformed(ActionEvent ae) {
				onClickYesButton();
			}
		};
		action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Y);
		return action;
	}
	
	protected Action createNoButtonAction() {
		Action action = new AbstractAction(UIManager.getString("OptionPane.noButtonText")) {
			public void actionPerformed(ActionEvent ae) {
				onClickNoButton();
			}
		};
		action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		return action;
	}

	@Override
	protected Action createOkButtonAction() {
		Action action = new AbstractAction(UIManager.getString("OptionPane.okButtonText")) {
			public void actionPerformed(ActionEvent ae) {
				onClickOkButton();
			}
		};
		return action;
	}

	@Override
	protected Action createCancelButtonAction() {
		Action action = new AbstractAction(UIManager.getString("OptionPane.cancelButtonText")) {
			public void actionPerformed(ActionEvent ae) {
				onClickCancelButton();
			}
		};
		return action;
	}

	@Override
	protected JButton createApplyButton() {
		if (_optionType == YES_NO_CANCEL_OPTION) {
			return createYesButton();
		}
		else {
			return null;
		}
	}
	
	protected JButton createCopyToClipButton() {
		JButton btn = new JButton(createCopyToClipAction());
		return btn;
	}
	
	protected JButton createYesButton() {
		JButton btn = new JButton(createYesButtonAction());
		return btn;
	}
	
	protected JButton createNoButton() {
		JButton btn = new JButton(createNoButtonAction());
		return btn;
	}

	@Override
	protected JButton createOkButton() {
		if (_optionType == YES_NO_CANCEL_OPTION) {
			return createNoButton();
		}
		else if (_optionType == YES_NO_OPTION) {
			return createYesButton();
		}
		else {
			return super.createOkButton();
		}
	}

	@Override
	protected JButton createCancelButton() {
		if (_optionType == YES_NO_CANCEL_OPTION) {
			return super.createCancelButton();
		}
		else if (_optionType == YES_NO_OPTION) {
			return createNoButton();
		}
		else if (_optionType == OK_CANCEL_OPTION) {
			return super.createCancelButton();
		}
		else {
			return null;
		}
	}

	@Override
	protected JComponent createButtonsPanel() {
		JButton btnCopyToClip = createCopyToClipButton();
		
		JButton[] buttons = createButtons();
		if (buttons == null || buttons.length < 1) {
			// no buttons
			return null;
		}
		
		int maxHeight = adjustButtonSize(buttons);
		
		// Layout
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(btnCopyToClip);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(Box.createHorizontalGlue());
		for (JButton btn : buttons) {
			btnBox.add(btn);
			btnBox.add(Box.createHorizontalStrut(5));
		}
		btnBox.add(Box.createRigidArea(new Dimension(0, maxHeight+10)));
		return btnBox;
	}

	@Override
	protected void setupMainContents() {
		// create components
		//--- [Copy to Clipboard] button
		//--- Message text area
		_message = new JMultilineLabel();
		_message.setEditable(false);
		_message.setWrapStyleWord(false);
		_message.setLineWrap(false);
		_message.setTabSize(4);
		_message.setBorder(BorderFactory.createEmptyBorder());
		//--- Detail text area
		_detail = new JTextArea();
		_detail.setEditable(false);
		_detail.setWrapStyleWord(false);
		_detail.setLineWrap(false);
		_detail.setTabSize(4);
		_detail.setBackground(UIManager.getColor("Label.background"));
		_detail.setForeground(UIManager.getColor("Label.foreground"));
		_detail.setDisabledTextColor(UIManager.getColor("Label.disabledForeground"));
		
		// create Main Panel
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(3, 3, 3, 3);
		//--- message
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(_message, gbc);
		gbc.gridy++;
		//--- detail
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sc.setViewportView(_detail);
		panel.add(sc, gbc);
		gbc.gridy++;
		
		this.getContentPane().add(panel, BorderLayout.CENTER);
	}
	
	protected void onClickCopyToClipButton() {
		StringBuilder sb = new StringBuilder();
		
		String msg = getMessage();
		if (msg != null && msg.length() > 0) {
			sb.append(msg);
			sb.append("\n\n");
		}
		
		msg = getDetail();
		if (msg != null && msg.length() > 0) {
			sb.append(msg);
			if (msg.charAt(msg.length()-1) != '\n') {
				sb.append("\n");
			}
		}
		
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection ss = new StringSelection(sb.toString());
		clip.setContents(ss, ss);
	}
	
	protected void onClickYesButton() {
		_optionResult = YES_OPTION;
		dialogClose(IDialogResult.DialogResult_Cancel);
	}

	protected void onClickNoButton() {
		_optionResult = NO_OPTION;
		dialogClose(IDialogResult.DialogResult_Cancel);
	}

	@Override
	protected void onClickOkButton() {
		_optionResult = OK_OPTION;
		dialogClose(IDialogResult.DialogResult_Cancel);
	}

	@Override
	protected void onClickCancelButton() {
		if (_optionType == YES_NO_OPTION) {
			_optionResult = NO_OPTION;
		} else {
			_optionResult = CANCEL_OPTION;
		}
		dialogClose(IDialogResult.DialogResult_Cancel);
	}
}
