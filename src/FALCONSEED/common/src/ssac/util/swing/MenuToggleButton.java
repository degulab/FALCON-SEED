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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MenuToggleButton.java	3.1.0	2014/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MenuToggleButton.java	1.02	2008/05/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;


/**
 * メニューのように機能するトグルボタン。
 * 
 * @version 3.1.0 2014/05/12
 * 
 * @since 1.02
 */
public class MenuToggleButton extends JToggleButton
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	private final Icon menuArrowIcon = new MenuArrowIcon();

	/**
	 * トグル状態を解除するポップアップメニューリスナー
	 * @since 3.1.0
	 */
	private PopupMenuListener	_toggleOffListener;
	/**
	 * トグル状態を解除し、イベントソースから自身を削除するポップアップメニューリスナー
	 * @since 3.1.0
	 */
	private PopupMenuListener	_oneShotListener;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public MenuToggleButton() {
		super();
		initialize();
	}

	public MenuToggleButton(Action a) {
		super(a);
		initialize();
	}

	public MenuToggleButton(Icon icon) {
		super(icon);
		initialize();
	}

	public MenuToggleButton(String text, Icon icon) {
		super(text, icon);
		initialize();
	}

	public MenuToggleButton(String text) {
		super(text);
		initialize();
	}
	
	protected void initialize() {
		setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4+menuArrowIcon.getIconWidth()));
        //setMargin(new Insets(4, 4, 4, 4+menuArrowIcon.getIconWidth()));
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension dim = getSize();
        Insets ins = getInsets();
        int x = dim.width-ins.right;
        int y = ins.top+(dim.height-ins.top-ins.bottom-menuArrowIcon.getIconHeight())/2;
        menuArrowIcon.paintIcon(this, g, x, y);
    }

    /**
     * このオブジェクトのトグル状態を制御するための、ポップアップメニューリスナーを取得する。
     * @return	ポップアップメニューリスナー
     * @since 3.1.0
     */
    public PopupMenuListener getToggleOffPopupMenuListener() {
    	if (_toggleOffListener == null) {
    		_toggleOffListener = createToggleOffPopupMenuListener();
    	}
    	return _toggleOffListener;
    }

    /**
     * このオブジェクトのトグル状態を制御するための、一回のみ作動するポップアップメニューリスナーを取得する。
     * このリスナーは、ポップアップメニューが非表示になった時点で、イベントソースのポップアップメニューから削除される。
     * @return	ポップアップメニューリスナー
     * @since 3.1.0
     */
    public PopupMenuListener getOneShotPopupMenuListener() {
    	if (_oneShotListener == null) {
    		_oneShotListener = createOneShotPopupMenuListener();
    	}
    	return _oneShotListener;
    }

    /**
     * このオブジェクトの外側に、指定されたポップアップメニューを表示する。
     * このメソッドでは、指定されたポップアップメニューに、一時的にリスナーを追加し、
     * ポップアップメニューが非表示になった時点でリスナーを取り除く。
     * @param menu	表示するポップアップメニュー
     * @throws NullPointerException	引数が <tt>null</tt> の場合
     * @since 3.1.0
     */
    public void showPopupMenu(JPopupMenu menu) {
    	menu.addPopupMenuListener(getOneShotPopupMenuListener());
    	menu.show(this, 0, this.getHeight());
    }

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
    
    /**
     * このオブジェクトのトグル状態を制御するための、ポップアップメニューリスナーを生成する。
     * このメソッドは、このインスタンスでポップアップメニューが作成されていない場合に呼び出される。
     * @return	ポップアップメニューリスナー
     * @since 3.1.0
     */
    protected PopupMenuListener createToggleOffPopupMenuListener() {
    	return new ToggleOffPopupMenuListener();
    }
    
    /**
     * このオブジェクトのトグル状態を制御するための、一回のみ作動するポップアップメニューリスナーを取得する。
     * このメソッドは、このインスタンスでポップアップメニューが作成されていない場合に呼び出される。
     * @return	ポップアップメニューリスナー
     * @since 3.1.0
     */
    protected PopupMenuListener createOneShotPopupMenuListener() {
    	return new OneShotToggleOffPopupMenuListener();
    }

	//------------------------------------------------------------
	// Arrow icon class
	//------------------------------------------------------------

	static public class MenuArrowIcon implements Icon {
	    public void paintIcon(Component c, Graphics g, int x, int y) {
	        Graphics2D g2 = (Graphics2D)g;
	        //    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setPaint(Color.BLACK);
	        g2.translate(x,y);
	        g2.drawLine( 2, 3, 6, 3 );
	        g2.drawLine( 3, 4, 5, 4 );
	        g2.drawLine( 4, 5, 4, 5 );
	        g2.translate(-x,-y);
	        //    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
	    }
	    public int getIconWidth()  { return 9; }
	    public int getIconHeight() { return 9; }
	}

	//------------------------------------------------------------
	// PopupMenu listener class
	//------------------------------------------------------------

	/**
	 * ポップアップメニューイベントでトグル状態を解除するためのイベントリスナー
	 * @since 3.1.0
	 */
	protected class ToggleOffPopupMenuListener implements PopupMenuListener
	{
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			// no action
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			// ポップアップメニューが非表示になった時点で、ボタンのトグル状態を解除
//			System.err.println("[Debug] MenuToggleButton.ToggleOffPopupMenuListener invisibled!");
			if (isSelected()) {
				setSelected(false);
			}
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			// ポップアップメニューがキャンセルされた時点で、ボタンのトグル状態を解除
//			System.err.println("[Debug] MenuToggleButton.ToggleOffPopupMenuListener canceled!");
			if (isSelected()) {
				setSelected(false);
			}
		}
	}

	/**
	 * ポップアップメニューイベントでトグル状態を解除し、イベントソースから自身を削除するイベントリスナー
	 * @since 3.1.0
	 */
	protected class OneShotToggleOffPopupMenuListener implements PopupMenuListener
	{
		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			// no action
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			// ポップアップメニューが非表示になった時点で、ボタンのトグル状態を解除
//			System.err.println("[Debug] MenuToggleButton.OneShotToggleOffPopupMenuListener invisibled!");
			if (isSelected()) {
				setSelected(false);
			}
			((JPopupMenu)e.getSource()).removePopupMenuListener(this);
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			// ポップアップメニューがキャンセルされた時点で、ボタンのトグル状態を解除
//			System.err.println("[Debug] MenuToggleButton.OneShotToggleOffPopupMenuListener canceled!");
			if (isSelected()) {
				setSelected(false);
			}
			((JPopupMenu)e.getSource()).removePopupMenuListener(this);
		}
	}
}
