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
 *  <author> Hideaki Yagi (MRI)
 */
/*
 * @(#)DrawImagePanel.java	1.20	2012/03/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.graph.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JList;

public class DrawImagePanel extends JComponent
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	JList hoge;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private BufferedImage	_img;
	private int	_imgWidth;
	private int	_imgHeight;
	private double	_scale;
	private double	_invScale;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DrawImagePanel() {
		this(null);
	}
	
	public DrawImagePanel(BufferedImage image) {
		super();
		this._scale = 1.0;
		this._invScale = 1.0;
		this._img = image;
		if (_img != null) {
			_imgWidth = image.getWidth();
			_imgHeight = image.getHeight();
		}
		
		Dimension dmSize = new Dimension(_imgWidth, _imgHeight);
		this.setMaximumSize(dmSize);
		this.setPreferredSize(dmSize);
		this.setSize(dmSize);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getImageWidth() {
		return _imgWidth;
	}
	
	public int getImageHeight() {
		return _imgHeight;
	}
	
	public double getScale() {
		return _scale;
	}
	
	public void setScale(double newScale) {
		if (newScale <= 0.0) {
			_scale = 0.0;
			_invScale = 0.0;
		} else if (newScale >= 1.0) {
			_scale = 1.0;
			_invScale = 1.0;
		} else {
			_scale = newScale;
			_invScale = 1.0 / newScale;
		}
		
		int sw = (int)(_imgWidth * _scale);
		int sh = (int)(_imgHeight * _scale);
		Dimension dmSize = new Dimension(sw, sh);
		setPreferredSize(dmSize);
		setSize(dmSize);
	}
	
	public BufferedImage getImage() {
		return _img;
	}
	
	public void setImage(BufferedImage newImage) {
		if (newImage == null) {
			_img = null;
			_imgWidth = 0;
			_imgHeight = 0;
		} else {
			_img = newImage;
			_imgWidth = newImage.getWidth();
			_imgHeight = newImage.getHeight();
		}
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (_img != null) {
			Rectangle rcView = getVisibleRect();
			int dx1 = rcView.x;
			int dy1 = rcView.y;
			int dx2 = rcView.x + rcView.width;
			int dy2 = rcView.y + rcView.height;
			
			int sx1 = (int)(dx1 * _invScale);
			int sy1 = (int)(dy1 * _invScale);
			int sx2 = (int)(dx2 * _invScale);
			int sy2 = (int)(dy2 * _invScale);

			g.drawImage(_img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, this);	
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
