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
package ssac.util.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.Icon;
import javax.swing.border.MatteBorder;

/**
 * フォーカスの状態に応じた <code>MatterBorder</code>
 * 
 * @version 1.00 2008/03/24
 *
 */
public class FocusedMatteBorder extends MatteBorder implements WindowFocusListener, FocusListener
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	protected boolean	isFocused;
	protected Color	focusedColor;
	protected Icon		focusedIcon;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FocusedMatteBorder(Insets borderInsets, Color matteColor) {
		super(borderInsets, matteColor);
	}
	
	public FocusedMatteBorder(Insets borderInsets, Color matteColor, Color focusedColor) {
		super(borderInsets, matteColor);
		this.focusedColor = focusedColor;
	}
	
	public FocusedMatteBorder(int top, int left, int bottom, int right, Color matteColor) {
		super(top, left, bottom, right, matteColor);
	}
	
	public FocusedMatteBorder(int top, int left, int bottom, int right, Color matteColor, Color focusedColor) {
		super(top, left, bottom, right, matteColor);
		this.focusedColor = focusedColor;
	}
	
	public FocusedMatteBorder(Insets borderInsets, Icon tileIcon) {
		super(borderInsets, tileIcon);
	}
	
	public FocusedMatteBorder(Insets borderInsets, Icon tileIcon, Icon focusedIcon) {
		super(borderInsets, tileIcon);
		this.focusedIcon = focusedIcon;
	}
	
	public FocusedMatteBorder(int top, int left, int bottom, int right, Icon tileIcon) {
		super(top, left, bottom, right, tileIcon);
	}
	
	public FocusedMatteBorder(int top, int left, int bottom, int right, Icon tileIcon, Icon focusedIcon) {
		super(top, left, bottom, right, tileIcon);
		this.focusedIcon = focusedIcon;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public Color getFocusedColor() {
		return focusedColor;
	}
	
	public Icon getFocusedIcon() {
		return focusedIcon;
	}
	
	public void setMatteColor(Color matteColor) {
		this.color = matteColor;
	}
	
	public void setFocusedColor(Color focusedColor) {
		this.focusedColor = focusedColor;
	}
	
	public void setTileIcon(Icon tileIcon) {
		this.tileIcon = tileIcon;
	}
	
	public void setFocusedIcon(Icon focusedIcon) {
		this.focusedIcon = focusedIcon;
	}
	
	public Insets getBorderInsets(Component c) {
		return getBorderInsets();
	}
	
	public Insets getBorderInsets(Component c, Insets insets) {
		return computeInsets(insets);
	}
	
	public Insets getBorderInsets() {
		return computeInsets(new Insets(0,0,0,0));
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Insets insets = getBorderInsets(c);
		Color oldColor = g.getColor();
		g.translate(x, y);
		Color targetColor = (isFocused ? this.focusedColor : this.color);
		Icon targetIcon = (isFocused ? this.focusedIcon : this.tileIcon);
		
		// If the tileIcon failed loading, paint as gray.
		if (targetIcon != null) {
			targetColor = (targetIcon.getIconWidth() == -1) ? Color.gray : null;
		}
        
		if (targetColor != null) {
			g.setColor(targetColor);
			g.fillRect(0, 0, width - insets.right, insets.top);
			g.fillRect(0, insets.top, insets.left, height - insets.top);
			g.fillRect(insets.left, height - insets.bottom, width - insets.left, insets.bottom);
			g.fillRect(width - insets.right, 0, insets.right, height - insets.bottom);
		}
		else if (targetIcon != null) {
			int tileW = targetIcon.getIconWidth();
			int tileH = targetIcon.getIconHeight();
			int xpos, ypos, startx, starty;
			Graphics cg;
			
			// Paint top matte edge
			cg = g.create();
			cg.setClip(0, 0, width, insets.top);
			for (ypos = 0; insets.top - ypos > 0; ypos += tileH) {
				for (xpos = 0; width - xpos > 0; xpos += tileW) {
					targetIcon.paintIcon(c, cg, xpos, ypos);
				}
			}
			cg.dispose();
			
			// Paint left matte edge
			cg = g.create();
			cg.setClip(0, insets.top, insets.left, height - insets.top);
			starty = insets.top - (insets.top%tileH);
			startx = 0;
			for (ypos = starty; height - ypos > 0; ypos += tileH) {
				for (xpos = startx; insets.left - xpos > 0; xpos += tileW) {
					targetIcon.paintIcon(c, cg, xpos, ypos);
				}
			}
			cg.dispose();
			
			// Paint bottom matte edge
			cg = g.create();
			cg.setClip(insets.left, height - insets.bottom, width - insets.left, insets.bottom);
			starty = (height - insets.bottom) - ((height - insets.bottom)%tileH);
			startx = insets.left - (insets.left%tileW);
			for (ypos = starty; height - ypos > 0; ypos += tileH) {
				for (xpos = startx; width - xpos > 0; xpos += tileW) {
					targetIcon.paintIcon(c, cg, xpos, ypos);
				}
			}
			cg.dispose();
			
			// Paint right matte edge
			cg = g.create();
			cg.setClip(width - insets.right, insets.top, insets.right, height - insets.top - insets.bottom);
			starty = insets.top - (insets.top%tileH);
			startx = width - insets.right - ((width - insets.right)%tileW);
			for (ypos = starty; height - ypos > 0; ypos += tileH) {
				for (xpos = startx; width - xpos > 0; xpos += tileW) {
					targetIcon.paintIcon(c, cg, xpos, ypos);
				}
			}
			cg.dispose();
		}
		g.translate(-x, -y);
		g.setColor(oldColor);
	}

	/* should be protected once api changes area allowed */
	protected Insets computeInsets(Insets insets) {
		Icon targetIcon = (isFocused ? this.focusedIcon : this.tileIcon);
		if (targetIcon != null && top == -1 && bottom == -1 && 
				left == -1 && right == -1) {
			int w = targetIcon.getIconWidth();
			int h = targetIcon.getIconHeight();
			insets.top = h;
			insets.right = w;
			insets.bottom = h;
			insets.left = w;
		} else {
			insets.left = left;
			insets.top = top;
			insets.right = right;
			insets.bottom = bottom;
		}
		return insets;
	}
	
	protected void setFocus(Component c) {
		if (c != null) {
			boolean cFocused = c.hasFocus();
			if ((cFocused && !this.isFocused) || (!cFocused && this.isFocused)) {
				this.isFocused = cFocused;
				c.repaint();
			}
		}
	}

	//------------------------------------------------------------
	// Window focus action
	//------------------------------------------------------------

	public void windowGainedFocus(WindowEvent e) {
		setFocus(e.getComponent());
	}

	public void windowLostFocus(WindowEvent e) {
		setFocus(e.getComponent());
	}

	public void focusGained(FocusEvent e) {
		setFocus(e.getComponent());
	}

	public void focusLost(FocusEvent e) {
		setFocus(e.getComponent());
	}
}
