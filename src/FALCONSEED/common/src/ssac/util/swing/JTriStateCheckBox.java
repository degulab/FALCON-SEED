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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)JTriStateCheckBox.java	3.3.0	2016/05/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;

/**
 * 3 状態を保持するチェックボックス。
 * 
 * @author FALCON-SEED Project
 * @version 3.3.0
 * @since 3.3.0
 */
public class JTriStateCheckBox extends JCheckBox
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 4414071888427019597L;

	/**
	 * チェックボックスの 3 状態を示す列挙値。
	 * @author RWOS Project
 	 * @version 0.3.3
 	 * @since 0.3.3
	 */
	static public enum Status {
		/** 選択 **/
		SELECTED,
		/** 非選択 **/
		DESELECTED,
		/** 不定 **/
		INDETERMINATE
	}

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 不定状態のアイコン **/
	private final Icon				_indetIcon = new IndeterminateIcon();
	/** 3 状態の変更を受け付けるモデル **/
	private final TriStateDecorator	_model;

	/** 3 状態の次の状態を返すハンドラ **/
	private TriStateHandler	_handler = DefaultTriStateHandler;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JTriStateCheckBox() {
		this(null);
	}
	
	public JTriStateCheckBox(String text) {
		this(text, Status.DESELECTED);
	}
	
	public JTriStateCheckBox(String text, Status initialState) {
		this(text, null, initialState);
	}
	
	public JTriStateCheckBox(String text, Icon icon) {
		this(text, icon, Status.DESELECTED);
	}
	
	public JTriStateCheckBox(String text, Icon icon, Status initialState) {
		super(text, icon);
		setPressedIcon(_indetIcon);
		
		// Add a listener for when the mouse is pressed
		super.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				grabFocus();
				_model.nextState();
			}
		});
		
		// Reset the keyboard action map
		ActionMap map = new ActionMapUIResource();
		map.put("pressed", new AbstractAction(){
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent ae) {
				grabFocus();
				_model.nextState();
			}
		});
		map.put("released", null);
		SwingUtilities.replaceUIActionMap(this, map);
		
		// set the model to the adapted model
		_model = new TriStateDecorator(getModel());
		setModel(_model);
		setState(initialState);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public TriStateHandler getDefaultHandler() {
		return DefaultTriStateHandler;
	}
	
	public TriStateHandler getHandler() {
		return _handler;
	}
	
	public void setHandler(TriStateHandler newHandler) {
		if (newHandler == null) {
			newHandler = DefaultTriStateHandler;
		}
		
		_handler = newHandler;
	}
	
	public void setState(Status state) {
		_model.setState(state);
	}
	
	public Status getState() {
		return _model.getState();
	}
	
	@Override
	public void setSelected(boolean selected) {
		setState(selected ? Status.SELECTED : Status.DESELECTED);
	}
	
	@Override
	public synchronized void addMouseListener(MouseListener ml) {
		// No one may add mouse listeners, not even Swing!
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected final TriStateHandler DefaultTriStateHandler = new TriStateHandler() {
		public Status nextState(Status current) {
			switch (current) {
				case SELECTED:
					return Status.INDETERMINATE;
				case DESELECTED:
					return Status.SELECTED;
				case INDETERMINATE:
					return Status.DESELECTED;
				default:
					return Status.INDETERMINATE;
			}
		}
	};

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * 3 ステートの次の状態を返すハンドラ。
	 */
	static public interface TriStateHandler
	{
		public Status nextState(Status currentState);
	}
	
	/**
	 * 3 ステートの処理を行うデコレータモデル。
	 */
	private class TriStateDecorator implements ButtonModel
	{
		private final ButtonModel	_btnModel;
		
		private TriStateDecorator(ButtonModel buttonModel) {
			_btnModel = buttonModel;
		}
		
		private void setState(Status state) {
			switch (state) {
				case SELECTED:
					_btnModel.setArmed(false);
					setPressed(false);
					setSelected(true);
					break;
				case DESELECTED:
					_btnModel.setArmed(false);
					setPressed(false);
					setSelected(false);
					break;
				case INDETERMINATE:
					_btnModel.setArmed(true);
					setPressed(true);
					setSelected(true);
					break;
				default :
					// to DESELECTED
					_btnModel.setArmed(false);
					setPressed(false);
					setSelected(false);
			}
		}
		
		private Status getState() {
			if (isSelected() && !isArmed()) {
				return Status.SELECTED;
			}
			else if (isSelected() && isArmed()) {
				return Status.INDETERMINATE;
			}
			else {
				return Status.DESELECTED;
			}
		}
		
		private void nextState() {
			Status current = getState();
			Status next = _handler.nextState(current);
			if (next!=null && !current.equals(next)) {
				setState(next);
			}
		}
		
		public void setArmed(boolean b) {}
		
		public void setEnabled(boolean b) {
			setFocusable(b);
			_btnModel.setEnabled(b);
		}
		
		public boolean isArmed() {
			return _btnModel.isArmed();
		}
		
		public boolean isSelected() {
			return _btnModel.isSelected();
		}
		
		public boolean isEnabled() {
			return _btnModel.isEnabled();
		}
		
		public boolean isPressed() {
			return _btnModel.isPressed();
		}
		
		public boolean isRollover() {
			return _btnModel.isRollover();
		}
		
		public void setSelected(boolean b) {
			_btnModel.setSelected(b);
		}
		
		public void setPressed(boolean b) {
			_btnModel.setPressed(b);
		}
		
		public void setRollover(boolean b) {
			_btnModel.setRollover(b);
		}
		
		public void setMnemonic(int key) {
			_btnModel.setMnemonic(key);
		}
		
		public int getMnemonic() {
			return _btnModel.getMnemonic();
		}
		
		public void setActionCommand(String s) {
			_btnModel.setActionCommand(s);
		}
		
		public String getActionCommand() {
			return _btnModel.getActionCommand();
		}
		
		public void setGroup(ButtonGroup group) {
			_btnModel.setGroup(group);
		}
		
		public void addActionListener(ActionListener l) {
			_btnModel.addActionListener(l);
		}
		
		public void removeActionListener(ActionListener l) {
			_btnModel.removeActionListener(l);
		}
		
		public void addItemListener(ItemListener l) {
			_btnModel.addItemListener(l);
		}
		
		public void removeItemListener(ItemListener l) {
			_btnModel.removeItemListener(l);
		}
		
		public void addChangeListener(ChangeListener l) {
			_btnModel.addChangeListener(l);
		}
		
		public void removeChangeListener(ChangeListener l) {
			_btnModel.removeChangeListener(l);
		}
		
		public Object[] getSelectedObjects() {
			return _btnModel.getSelectedObjects();
		}
	}

	/**
	 * 不定状態のアイコン。
	 */
	static protected class IndeterminateIcon implements Icon
	{
		private final Color	_foreColor	= UIManager.getColor("CheckBox.foreground");
		private final Icon	_diselIcon	= UIManager.getIcon("CheckBox.icon");

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			_diselIcon.paintIcon(c, g, x, y);
			int w = getIconWidth();
			int h = getIconHeight();
			int t = 4;
			int b = 2;
			
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setPaint(_foreColor==null ? Color.BLACK : _foreColor);
			g2.translate(x, y);
			g2.fillRect(t, (h - b)/2, (w - t - t), b);
			g2.dispose();
		}

		@Override
		public int getIconWidth() {
			return _diselIcon.getIconWidth();
		}

		@Override
		public int getIconHeight() {
			return _diselIcon.getIconHeight();
		}
	}
}
