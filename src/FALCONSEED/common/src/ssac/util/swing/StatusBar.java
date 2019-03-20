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
 * @(#)StatusBar.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 * @(#)StatusBar.java	1.10	2008/12/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;

import ssac.util.Strings;

/**
 * ステータス・バー。
 * 行番号とカラム番号を表示する領域を持つ。
 * 
 * @version 1.10	2008/12/05
 */
public class StatusBar extends JPanel
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final String Blank = " ";
	
	static private final String defModeInsert    = "Insert";
	static private final String defModeOverwrite = "Overwrite";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private JLabel lblPosition;
	private JLabel lblInsertMode;
	private JLabel lblMessage;
	
	private String insertModeText    = defModeInsert;
	private String overwriteModeText = defModeOverwrite;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public StatusBar() {
		super(new GridBagLayout());
		initialComponent();

		clearMessage();
		clearInsertMode();
		clearPosition();
	}
	
	private final void initialComponent() {
		// create Components
		//--- Messages
		lblMessage = new JLabel();
		//--- InsertMode
		lblInsertMode = createInsertModeLabel();
		//--- Position
		lblPosition = createPositionLabel();
		
		// Layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(1,1,1,1);
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- message
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(lblMessage, gbc);
		gbc.gridx++;
		gbc.weightx = 0;
		/*--- Insert Mode は将来用
		//--- separator
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.VERTICAL;
		this.add(createSeparator(), gbc);
		gbc.gridx++;
		//--- Insert mode
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.VERTICAL;
		this.add(lblInsertMode, gbc);
		gbc.gridx++;
		---*/
		//--- separator
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.VERTICAL;
		this.add(createSeparator(), gbc);
		gbc.gridx++;
		//--- Position
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.VERTICAL;
		this.add(lblPosition, gbc);
		gbc.gridx++;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getInsertModeText() {
		return insertModeText;
	}
	
	public void setInsertModeText(String text) {
		if (Strings.isNullOrEmpty(text)) {
			this.insertModeText = defModeInsert;
		} else {
			this.insertModeText = text;
		}
	}
	
	public String getOverwriteModeText() {
		return overwriteModeText;
	}
	
	public void setOverwriteModeText(String text) {
		if (Strings.isNullOrEmpty(text)) {
			this.overwriteModeText = defModeOverwrite;
		} else {
			this.overwriteModeText = text;
		}
	}
	
	public void clearMessage() {
		lblMessage.setText(Blank);
	}
	
	public void clearInsertMode() {
		lblInsertMode.setText(Blank);
	}
	
	public void clearPosition() {
		lblPosition.setText(Blank);
	}
	
	public String getMessage() {
		return lblMessage.getText();
	}
	
	public void setMessage(String strmsg) {
		lblMessage.setText(strmsg);
	}
	
	public void setInsertMode(TextEditorPane editor) {
		if (editor != null) {
			boolean flg = editor.isOverwriteMode();
			setInsertMode(!flg);
		} else {
			setInsertMode(true);
		}
	}
	
	public void setInsertMode(boolean isInsert) {
		if (isInsert) {
			lblInsertMode.setText(getInsertModeText());
		} else {
			lblInsertMode.setText(getOverwriteModeText());
		}
	}
	
	public void setCaretPosition(TextEditorPane editor) {
		try {
			int pos = editor.getCaretPosition();
			int ln = editor.getLineOfOffset(pos);
			int cn = pos - editor.getLineStartOffset(ln);
			setCaretPosition(ln+1, cn+1);
		}
		catch (BadLocationException ex) {
			clearPosition();
		}
	}
	
	public void setCaretPosition(int lineNo, int columnNo) {
		lblPosition.setText(Blank + lineNo + " : " + columnNo);
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	/*--- deleted : 1.10 2008/12/05
	public void focusGained(FocusEvent e) {
		Component cmp = e.getComponent();
		if (cmp != null && cmp instanceof TextEditorPane) {
			TextEditorPane editor = (TextEditorPane)cmp;
			setInsertMode(editor);
			setCaretPosition(editor);
		}
	}

	public void focusLost(FocusEvent e) {
		Component cmp = e.getComponent();
		if (cmp != null && cmp instanceof JTextComponent) {
			clearMessage();
			clearInsertMode();
			clearPosition();
		}
	}
	
	public void caretUpdate(CaretEvent e) {
		Object obj = e.getSource();
		if (obj != null && obj instanceof TextEditorPane) {
			setCaretPosition((TextEditorPane)obj);
		}
	}
	--- deleted : 1.10 2008/12/05 ---*/

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	private JLabel createInsertModeLabel() {
		JLabel label = new JLabel("m");
		FontMetrics fm = label.getFontMetrics(label.getFont());
		String s1 = Blank + getInsertModeText() + Blank;
		String s2 = Blank + getOverwriteModeText() + Blank;
		int w1 = fm.stringWidth(s1);
		int w2 = fm.stringWidth(s2);
		Dimension dm = label.getSize();
		dm = new Dimension(Math.max(w1, w2), dm.height);
		label.setSize(dm);
		label.setPreferredSize(dm);
		label.setMinimumSize(dm);
		return label;
	}
	
	private JLabel createPositionLabel() {
		final String maxString = Blank + "999999 : 9999" + Blank;
		JLabel label = new JLabel(maxString);
		FontMetrics fm = label.getFontMetrics(label.getFont());
		int w = fm.stringWidth(maxString);
		Dimension dm = label.getSize();
		dm = new Dimension(w, dm.height);
		label.setSize(dm);
		label.setPreferredSize(dm);
		label.setMinimumSize(dm);
		return label;
	}

	private JLabel createSeparator() {
		JLabel label = new JLabel();
		label.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		Dimension dm = new Dimension(2,1);
		label.setSize(dm);
		label.setPreferredSize(dm);
		label.setMinimumSize(dm);
		return label;
	}
}
