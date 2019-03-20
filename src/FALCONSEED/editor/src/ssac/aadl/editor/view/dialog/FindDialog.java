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
 * @(#)FindDialog.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FindDialog.java	1.14	2009/12/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FindDialog.java	1.10	2008/12/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FindDialog.java	1.04	2008/08/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)FindDialog.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.setting.AppSettings;
import ssac.util.Strings;
import ssac.util.swing.BasicDialog;

/**
 * 検索ダイアログ
 * 
 * @version 1.16	2010/09/27
 */
public class FindDialog extends BasicDialog
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(400, 210);
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private FindReplaceInterface	handler;
	
	private JTextField	fldKeyword;
	private JTextField	fldReplace;
	private JCheckBox	chkCaseInsensitive;	// 大文字と小文字を区別しない場合に Checked
	private JButton	btnFindPrev;
	private JButton	btnFindNext;
	private JButton	btnReplaceNext;
	private JButton	btnReplaceAll;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FindDialog(FindReplaceInterface handler) {
		this(null, handler);
	}
	
	public FindDialog(Frame owner, FindReplaceInterface handler) {
		super(owner, EditorMessages.getInstance().FindDlg_Title_Main, false);
		this.setAlwaysOnTop(true);
		setHandler(handler);
		
		// 設定
		this.setResizable(true);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200,300);
		}
		this.setMinimumSize(dmMin);
		setKeepMinimumSize(true);
		
		// restore settings
		loadSettings();
	}
	
	protected void setupMainPanel() {
		super.setupMainPanel();
		
		this.mainPanel.setLayout(new BorderLayout());
		
		// panel
		JPanel pnlTextField = createTextFieldPanel();
		JPanel pnlFunction = createFunctionPanel();
		Border bd = BorderFactory.createEmptyBorder(3,3,3,3);
		pnlTextField.setBorder(bd);
		pnlFunction.setBorder(bd);
		
		// Layout
		this.mainPanel.add(pnlTextField, BorderLayout.CENTER);
		this.mainPanel.add(pnlFunction, BorderLayout.SOUTH);
	}
	
	protected void setupButtonPanel() {
		int maxWidth = 0;
		int maxHeight = 0;
		Dimension dim;
		//--- not use
		/*---
		dim = this.btnApply.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		//---
		dim = this.btnOK.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		---*/
		//---
		this.btnCancel.setText(CommonMessages.getInstance().Button_Close);
		dim = this.btnCancel.getPreferredSize();
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
		//btnBox.add(this.btnApply);
		//btnBox.add(this.btnOK);
		//btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(this.btnCancel);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(Box.createRigidArea(new Dimension(0, dim.height+10)));
		
		// setup default button
		this.btnCancel.setDefaultCapable(true);
		
		this.getContentPane().add(btnBox, BorderLayout.SOUTH);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void setHandler(FindReplaceInterface handler) {
		this.handler = handler;
		updateFunctionButtons();
	}
	
	public void close() {
		if (this.isVisible()) {
			dialogClose(DialogResult_Cancel);
		}
		this.dispose();
	}
	
	static public void showNotFoundMessage(Component parentComponent, String keyword) {
		// 検索文字列が存在する場合のみ、メッセージ表示
		if (!Strings.isNullOrEmpty(keyword)) {
			AADLEditor.showMessageBox(parentComponent, EditorMessages.getInstance().FindDlg_Title_Main,
									"\"" + keyword + "\" " + EditorMessages.getInstance().msgSearchResultNotFound,
									JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	static public void showReplacedTokensMessage(Component parentComponent, int tokens) {
		// 一度に置換した文字列の個数を表示
		AADLEditor.showMessageBox(parentComponent, EditorMessages.getInstance().FindDlg_Title_Main,
								String.format(EditorMessages.getInstance().msgSearchReplacedTokens, tokens),
								JOptionPane.INFORMATION_MESSAGE);
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	protected void onButtonReplaceNext(ActionEvent ae) {
		if (handler != null) {
			storeFindReplaceSettings();
			Cursor oldCursor = getCursor();
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (!handler.replaceNext()) {
				setCursor(oldCursor);
				showNotFoundMessage(this, handler.getKeywordString());
			} else {
				setCursor(oldCursor);
			}
		}
	}
	
	protected void onButtonReplaceAll(ActionEvent ae) {
		if (handler != null) {
			storeFindReplaceSettings();
			Cursor oldCursor = getCursor();
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (!handler.replaceAll()) {
				setCursor(oldCursor);
				showNotFoundMessage(this, handler.getKeywordString());
			} else {
				setCursor(oldCursor);
				showReplacedTokensMessage(this, handler.getLastReplacedCount());
			}
		}
	}
	
	protected void onButtonFindPrev(ActionEvent ae) {
		if (handler != null) {
			storeFindReplaceSettings();
			Cursor oldCursor = getCursor();
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (!handler.findPrev()) {
				setCursor(oldCursor);
				showNotFoundMessage(this, handler.getKeywordString());
			} else {
				setCursor(oldCursor);
			}
		}
	}
	
	protected void onButtonFindNext(ActionEvent ae) {
		if (handler != null) {
			storeFindReplaceSettings();
			Cursor oldCursor = getCursor();
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (!handler.findNext()) {
				setCursor(oldCursor);
				showNotFoundMessage(this, handler.getKeywordString());
			} else {
				setCursor(oldCursor);
			}
		}
	}
	
	@Override
	protected void dialogClose(int result) {
		dialogResult = result;
		saveSettings();

		// no dispose, only hide window
		//dispose();
		this.setVisible(false);
	}
	
	@Override
	protected void onShown(ComponentEvent e) {
		restoreFindReplaceSettings();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void updateFunctionButtons() {
		if (handler != null) {
			btnFindPrev.setEnabled(handler.allowFindOperation());
			btnFindNext.setEnabled(handler.allowFindOperation());
			btnReplaceNext.setEnabled(handler.allowReplaceOperation());
			btnReplaceAll.setEnabled(handler.allowReplaceOperation());
		} else {
			btnFindPrev.setEnabled(false);
			btnFindNext.setEnabled(false);
			btnReplaceNext.setEnabled(false);
			btnReplaceAll.setEnabled(false);
		}
	}
	
	protected void restoreFindReplaceSettings() {
		if (handler != null) {
			this.fldKeyword.setText(handler.getKeywordString());
			// replace string set null
			//this.fldReplace.setText(handler.getReplaceString());
			this.fldReplace.setText(null);
			this.chkCaseInsensitive.setSelected(handler.isIgnoreCase());
		}
		else {
			// set defaults
			this.fldKeyword.setText(null);
			this.fldReplace.setText(null);
			this.chkCaseInsensitive.setSelected(true);	// default:大文字と小文字を区別しない
		}
	}
	
	protected void storeFindReplaceSettings() {
		if (handler == null)
			return;
		
		// keyword
		String strKeyword = this.fldKeyword.getText();
		if (!Strings.isNullOrEmpty(strKeyword)) {
			handler.putKeywordString(strKeyword);
		} else {
			handler.putKeywordString(null);
		}
		
		// replace
		String strReplace = this.fldReplace.getText();
		if (!Strings.isNullOrEmpty(strReplace)) {
			handler.putReplaceString(strReplace);
		} else {
			handler.putReplaceString(null);
		}
		
		// options
		boolean ignoreCase = this.chkCaseInsensitive.isSelected();
		handler.setIgnoreCase(ignoreCase);
	}
	
	@Override
	protected Point getDefaultLocation() {
		return super.getDefaultLocation();
	}

	@Override
	protected Dimension getDefaultSize() {
		//return super.getDefaultSize();
		return DM_MIN_SIZE;
	}

	@Override
	protected void loadSettings() {
		super.loadSettings(AppSettings.FIND_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	protected void saveSettings() {
		super.saveSettings(AppSettings.FIND_DLG, AppSettings.getInstance().getConfiguration());
//		// テスト
//		Point loc = this.getLocationOnScreen();
//		Dimension dm = this.getSize();
//		AppLogger.debug("Location : " + loc.toString());
//		AppLogger.debug("Size : " + dm.toString());
//		// テスト
	}
	
	private JPanel createTextFieldPanel() {
		// items
		//--- find
		JLabel lblFind = new JLabel(EditorMessages.getInstance().FindDlg_Title_FindText);
		/*--- modified : 2008.12.22 ---*/
		//this.fldKeyword = new JTextField();
		this.fldKeyword = new KeywordTextField();
		/*--- modified : 2008.12.22 ---*/
		//--- replace
		JLabel lblReplace = new JLabel(EditorMessages.getInstance().FindDlg_Title_ReplaceText);
		/*--- modified : 2008.12.22 ---*/
		//this.fldReplace = new JTextField();
		this.fldReplace = new KeywordTextField();
		/*--- modified : 2008.12.22 ---*/
		//--- options
		this.chkCaseInsensitive = new JCheckBox(EditorMessages.getInstance().FindDlg_Title_Option_CaseInsensitive);
		//--- Panel
		JPanel panel = new JPanel(new GridBagLayout());
		
		final int spacing = 5;

		// Layout
		//--- init
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.insets = new Insets(0, 0, spacing, 0);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridy = 0;
		//--- keyword
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		panel.add(lblFind, gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		panel.add(this.fldKeyword, gbc);
		gbc.gridy++;
		//--- replace
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		panel.add(lblReplace, gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		panel.add(this.fldReplace, gbc);
		gbc.gridy++;
		//--- options
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		panel.add(this.chkCaseInsensitive, gbc);
		
		// Actions
		
		// completed
		return panel;
	}
	
	private JPanel createFunctionPanel() {
		// items
		this.btnReplaceNext = new JButton(EditorMessages.getInstance().FindDlg_Button_ReplaceNext);
		this.btnReplaceAll = new JButton(EditorMessages.getInstance().FindDlg_Button_ReplaceAll);
		this.btnFindPrev = new JButton(EditorMessages.getInstance().FindDlg_Button_FindPrev);
		this.btnFindNext = new JButton(EditorMessages.getInstance().FindDlg_Button_FindNext);
		//--- Panel
		JPanel panel = new JPanel(new GridBagLayout());
		
		final int spacing = 5;

		// Layout
		//--- init
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		//--- replace
		gbc.insets = new Insets(0,0,0,0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(this.btnReplaceNext, gbc);
		gbc.gridy++;
		gbc.insets = new Insets(spacing, 0, 0, 0);
		panel.add(this.btnReplaceAll, gbc);
		//--- Find
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, spacing, 0, 0);
		panel.add(this.btnFindPrev, gbc);
		gbc.gridy++;
		gbc.insets = new Insets(spacing, spacing, 0, 0);
		panel.add(this.btnFindNext, gbc);
		
		// Actions
		//--- replace next
		this.btnReplaceNext.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonReplaceNext(ae);
			}
		});
		//--- replace all
		this.btnReplaceAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonReplaceAll(ae);
			}
		});
		//--- find prev
		this.btnFindPrev.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonFindPrev(ae);
			}
		});
		//--- find next
		this.btnFindNext.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				onButtonFindNext(ae);
			}
		});
		
		// completed
		return panel;
	}
	
	/*--- added : 2008.12.22 ---*/
	// キーワード終端のタブ、改行文字を削除する
	// これは、ペーストなどで改行文字等を終端に持つキーワードが
	// 入力されたとき、検索できなくなる問題を回避するため
	class KeywordTextField extends JTextField {
		static private final String UNPRINTABLE_CHARS = "\t\n\b\f\r";
		public KeywordTextField() {
			super();
		}
		@Override
		public void replaceSelection(String content) {
			//*****************************************************************
			// このメソッドに渡される引数には null が指定される場合がある。
			// とくに InputMethod が実行されているときは必ず null が渡される。
			// そのため、引数値 null に対応する。
			//*****************************************************************
			if (!Strings.isNullOrEmpty(content)) {
				//--- 最初の改行文字以降は取り除く
				int idx = content.indexOf('\n');
				if (idx >= 0) {
					content = content.substring(0, idx);
				}
				//--- 終端の非表示文字は取り除く
				int len = content.length();
				int startOffset = len - 1;
				if (len > 0 && UNPRINTABLE_CHARS.indexOf(content.charAt(startOffset)) >= 0) {
					//--- search
					for (int i = startOffset - 1; i >= 0; i--) {
						if (UNPRINTABLE_CHARS.indexOf(content.charAt(i)) < 0)
							break;
						else
							startOffset = i;
					}
					//--- remove
					content = content.substring(0, startOffset);
				}
			}
			//--- 標準の処理
			super.replaceSelection(content);
		}
	}
	/*--- added : 2008.12.22 ---*/
}
