/*
 * @(#)StaticTextComponent.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * 静的なテキストを表示するコンポーネント。
 * <code>JTextField</code> と同じ概観を持つ。
 * 
 * @version 1.00 2008/03/24
 */
public class StaticTextComponent extends JTextField implements ComponentListener
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public StaticTextComponent() {
		super();
		setEditable(false);
		addComponentListener(this);
	}

	public StaticTextComponent(Document doc, String text, int columns) {
		super(doc, text, columns);
		setEditable(false);
		addComponentListener(this);
	}

	public StaticTextComponent(int columns) {
		super(columns);
		setEditable(false);
		addComponentListener(this);
	}

	public StaticTextComponent(String text, int columns) {
		super(text, columns);
		setEditable(false);
		addComponentListener(this);
	}

	public StaticTextComponent(String text) {
		super(text);
		setEditable(false);
		addComponentListener(this);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public void setText(String t) {
		String oldText = super.getText();
		super.setText(t);
		if (t == null || oldText == null || !t.equals(oldText)) {
			updateToolTipText();
		}
	}

	//------------------------------------------------------------
	// implement ComponentListener interfaces
	//------------------------------------------------------------

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent e) {}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent e) {}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	public void componentResized(ComponentEvent e) {
		updateToolTipText();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent e) {
		updateToolTipText();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	private void updateToolTipText() {
		if (isVisible()) {
			String strToolTipText = null;
			String strText = getText();
			Font fn = getFont();
			if (strText != null && fn != null) {
				FontMetrics fm = getFontMetrics(fn);
				if (fm != null) {
					Rectangle rc = getVisibleRect();
					if (rc.width <= fm.stringWidth(strText)) {
						strToolTipText = strText;
					}
				}
			}
			setToolTipText(strToolTipText);
		}
	}
}
