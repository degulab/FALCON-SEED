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
 * @(#)TestEditorPane.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,):only moved package
 * @(#)TestEditorPane.java	1.10	2008/12/05
 *     - modified by Y.Ishizuka(PieCake.inc,):only moved package
 * @(#)TextEditorPane.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * <code>JTextArea</code> を実装とする、テキスト・エディタ・コンポーネント。
 * 
 * @version 1.14	2009/12/09
 */
public class TextEditorPane extends JTextArea
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final int defaultFontSize = 12;
	static private final int defaultTabSize = 4;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private static boolean isOverwriteMode = false;
	
	private final FocusRequester hFocusRequester = new FocusRequester();

	private Color crCaretLineBack;

	private Caret normalCaret;
	private Caret overwriteCaret;
	
	//private StatusBar	targetStatusBar;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public TextEditorPane() {
		this(null, null, 0, 0);
	}
	
	public TextEditorPane(String text) {
		this(null, text, 0, 0);
	}
	
	public TextEditorPane(int rows, int columns) {
		this(null, null, rows, columns);
	}
	
	public TextEditorPane(String text, int rows, int columns) {
		this(null, text, rows, columns);
	}
	
	public TextEditorPane(Document doc) {
		this(doc, null, 0, 0);
	}
	
	public TextEditorPane(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);

		final Caret defaultCaret = getCaret();
		normalCaret    = new NormalCaret();
		overwriteCaret = new OverwriteCaret();
		normalCaret.setBlinkRate(defaultCaret.getBlinkRate());
		overwriteCaret.setBlinkRate(defaultCaret.getBlinkRate());

		if (isOverwriteMode()) {
			setCaret(overwriteCaret);
		} else {
			setCaret(normalCaret);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public int getDefaultFontSize() {
		return defaultFontSize;
	}
	
	static public int getDefaultTabSize() {
		return defaultTabSize;
	}
	
	static public Font getDefaultFont() {
		return SwingTools.getDefaultEditorFont(defaultFontSize);
	}
	
	public Color getCaretLineBackground() {
		return this.crCaretLineBack;
	}
	
	public void setCaretLineBackground(Color cr) {
		this.crCaretLineBack = cr;
	}

	/*---
	public StatusBar getStatusBar() {
		return targetStatusBar;
	}
	
	public void setStatusBar(StatusBar bar) {
		// detach
		StatusBar oldBar = getStatusBar();
		if (oldBar != null) {
			this.removeCaretListener(oldBar);
			this.removeFocusListener(oldBar);
		}
		
		// attach
		targetStatusBar = bar;
		if (bar != null) {
			this.addFocusListener(bar);
			this.addCaretListener(bar);
		}
	}
	---*/
	
	public void setFocus() {
		if (!this.hasFocus()) {
			SwingUtilities.invokeLater(hFocusRequester);
		}
	}
	
	public void updateLocationForStatusBar(StatusBar infoBar) {
		if (infoBar != null) {
			try {
				int pos = getCaretPosition();
				int lno = getLineOfOffset(pos);
				int cno = pos - getLineStartOffset(lno);
				infoBar.setCaretPosition(lno+1, cno+1);
			}
			catch (BadLocationException ex) {
				infoBar.clearPosition();
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	public boolean isOverwriteMode() {
		return isOverwriteMode;
	}

	/*
	 *Set the caret to use depending on overtype/insert mode
	 */
	public synchronized void setOverwriteMode(StatusBar infoBar, boolean isOverwrite) {
		isOverwriteMode = isOverwrite;
		int pos = getCaretPosition();
		if(isOverwriteMode()) {
			if (infoBar != null) {
				infoBar.setInsertMode(false);
			}
			setCaret(overwriteCaret);
		}else{
			if (infoBar != null) {
				infoBar.setInsertMode(true);
			}
			setCaret(normalCaret);
		}
		setCaretPosition(pos);
	}

	/*
	 *  Override method from JComponent
	 */
	public void replaceSelection(String text) {
		//  Implement overtype mode by selecting the character at the current
		//  caret position
		if(isOverwriteMode()) {
			int pos = getCaretPosition();
			if(getSelectedText()==null &&  pos<getDocument().getLength()) {
				moveCaretPosition(pos+1);
			}
		}
		super.replaceSelection(text);
	}

	/*
	 *  Override method from JComponent
	 */
	protected void processKeyEvent(KeyEvent e) {
		super.processKeyEvent(e);
		/*--- 上書きモードは将来用
		//  Handle release of Insert key to toggle overtype/insert mode
		if(e.getID()==KeyEvent.KEY_RELEASED &&  e.getKeyCode()==KeyEvent.VK_INSERT) {
			setCaretPosition(getCaretPosition());  //追加
			moveCaretPosition(getCaretPosition()); //追加
			setOverwriteMode(!isOverwriteMode());
			repaint(); //追加
		}
		---*/
	}
	
	@Override
	public void setTabSize(int size) {
		super.setTabSize(size);
		// 強制的に再描画
		revalidate();
		repaint();
	}

	protected void paintComponent(Graphics g) {
		// has Highlight color?
		if (this.crCaretLineBack == null) {
			super.paintComponent(g);
			return;
		}
		
		// draw highlight background
		Graphics2D g2 = (Graphics2D)g;
		Insets i = getInsets();
		int h = g2.getFontMetrics().getHeight();
		int y = (SwingTools.getLineAtCaret(this)-1) * h + i.top;
		g2.setPaint(this.crCaretLineBack);
		g2.fillRect(i.left, y, getSize().width-i.left-i.right, h);
		super.paintComponent(g);
	}
	
	class NormalCaret extends DefaultCaret {
		protected synchronized void damage(Rectangle r) {
			if (r != null) {
				JTextComponent jtc = getComponent();
				x = 0;
				y = r.y;
				width = jtc.getSize().width;
				height = r.height;
				repaint();
			}
		}
	}
	
	class OverwriteCaret extends DefaultCaret {
		public void paint(Graphics g) {
			if (isVisible()) {
				try{
					JTextComponent component = getComponent();
					TextUI mapper = component.getUI();
					Rectangle r = mapper.modelToView(component, getDot());
					g.setColor(component.getCaretColor());
					int width = g.getFontMetrics().charWidth('w');
					//全角などに対応
					if(isOverwriteMode()) {
						int pos = getCaretPosition();
						if(pos<getDocument().getLength()) {
							if(getSelectedText()!=null) {
								width = 0;
							}else{
								String str = getText(pos, 1);
								width = g.getFontMetrics().stringWidth(str);
							}
						}
					} //ここまで追加
					int y = r.y + r.height - 2;
					g.drawLine(r.x, y, r.x + width - 2, y);
				} catch(BadLocationException e) {}
			}
		}
		protected synchronized void damage(Rectangle r) {
			if(r != null) {
				JTextComponent component = getComponent();
				x = r.x;
				y = r.y;
				//width = component.getFontMetrics(component.getFont()).charWidth('w');
				width = component.getFontMetrics(component.getFont()).charWidth('あ');
				height = r.height;
				repaint();
			}
		}
	}
	
	class FocusRequester implements Runnable {
		public void run() {
			if (!TextEditorPane.this.requestFocusInWindow()) {
				TextEditorPane.this.requestFocus();
			}
		}
	}
}
