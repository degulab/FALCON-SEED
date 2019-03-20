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
package ssac.aadl.editor.view.text;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;

/**
 * エディタの行番号表示用コンポーネント。
 * 
 * @version 1.00 2008/03/24
 */
public class LineNumberPane extends JComponent
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------

	static private final int MARGIN = 5;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private JTextArea ownerText;

	private final ComponentListener defaultComponentListener;
	private final DocumentListener defaultDocumentListener;

	private Color	crBorder;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public LineNumberPane() {
		super();
		this.ownerText = null;
		setBorderColor(Color.GRAY);
		setOpaque(true);
		setBackground(Color.WHITE);
		//--- actions
		this.defaultComponentListener = new ComponentAdapter(){
			public void componentResized(ComponentEvent ce) {
				revalidate();
				repaint();
			}
		};
		this.defaultDocumentListener = new DocumentListener(){
			public void insertUpdate(DocumentEvent e) {
				repaint();
			}
			public void removeUpdate(DocumentEvent e) {
				repaint();
			}
			public void changedUpdate(DocumentEvent e) {}
		};
	}

	public LineNumberPane(JTextArea owner) {
		this();
		this.attachOwnerText(owner);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	public Color getBorderColor() {
		return this.crBorder;
	}

	public void setBorderColor(Color cr) {
		Border br = BorderFactory.createMatteBorder(0, 0, 0, 1, cr);
		this.crBorder = cr;
		setBorder(br);
	}

	public int getLineCount() {
		int count = 0;
		if (ownerText != null) {
			Document doc = ownerText.getDocument();
			Element root = doc.getDefaultRootElement();
			count = root.getElementIndex(doc.getLength());
		}
		return count;
	}

	public int getLineAtPoint(int y) {
		int lineNo = -1;
		if (ownerText != null) {
			Element root = ownerText.getDocument().getDefaultRootElement();
			int pos = ownerText.viewToModel(new Point(0, y));
			lineNo = root.getElementIndex(pos);
		}
		return lineNo;
	}

	public Dimension getPreferredSize() {
		return new Dimension(getComponentWidth(), ownerText.getHeight());
	}

	public JTextArea attachOwnerText(JTextArea owner) {
		//--- Check
		if (owner == null)
			throw new NullPointerException();
		
		//--- Detach
		final JTextArea oldOwner = detachOwnerText();
		
		//--- Attach
		this.ownerText = owner;
		owner.getDocument().addDocumentListener(this.defaultDocumentListener);
		owner.addComponentListener(this.defaultComponentListener);
		
		//--- return old owner
		return oldOwner;
	}

	public JTextArea detachOwnerText() {
		//--- has Old owner?
		if (this.ownerText == null)
			return null;
		
		//--- detach
		JTextArea oldOwner = this.ownerText;
		oldOwner.removeComponentListener(this.defaultComponentListener);
		oldOwner.getDocument().removeDocumentListener(this.defaultDocumentListener);
		this.ownerText = null;
		
		return oldOwner;
	}

	public void paintComponent(Graphics g) {
		Rectangle clip = g.getClipBounds();
		g.setColor(getBackground());
		g.fillRect(clip.x, clip.y, clip.width, clip.height);
		g.setColor(getForeground());
		final Font targetFont = getCurrentFont();
		final Font oldFont = g.getFont();
		g.setFont(targetFont);
		final FontMetrics fontMetrics = getFontMetrics(targetFont);
		//final FontMetrics fontMetrics = g.getFontMetrics();
		int topInset = (ownerText != null ? ownerText.getInsets().top : 0);
		int fontHeight = fontMetrics.getHeight();
		int fontAscent = fontMetrics.getAscent();
		int base  = clip.y - topInset;
		int start = getLineAtPoint(base);
		int end   = getLineAtPoint(base + clip.height);
		int y     = topInset - fontHeight + fontAscent + start * fontHeight;
		for (int i = start; i <= end; i++) {
			String text = String.valueOf(i+1);
			int x = getComponentWidth() - MARGIN - fontMetrics.stringWidth(text);
			y = y + fontHeight;
			g.drawString(text, x, y);
		}
		g.setFont(oldFont);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	private int getComponentWidth() {
		int lineCount = getLineCount();
		int maxDigits = Math.max(3, String.valueOf(lineCount).length());
		int width = maxDigits * getFontMetrics(getCurrentFont()).stringWidth("0") + MARGIN * 2;
		return width;
	}
	
	private Font getCurrentFont() {
		if (this.ownerText != null)
			return this.ownerText.getFont();
		else
			return UIManager.getFont("Label.font");
	}
}
