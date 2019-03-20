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
 * @(#)ActiveViewManager.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;

import ssac.util.swing.border.ActivationBorder;

/**
 * アクティブなビューを保持するマネージャクラス。
 * このクラスでは、登録されたビュー個別のアクティベーションを管理し、
 * アクティブなビューには専用のボーダーを設定する。
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.14
 */
public class ActiveViewManager
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** アクティブな状態を表す色 **/
	static public final Color ActivatedColor = new Color(255,153, 0);
	/** 非アクティブな状態を表す標準のボーダー **/
	static public final Border DefaultDeactivatedBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
	/** アクティブな状態を表す標準のボーダー **/
	static public final Border DefaultActivatedBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, ActivatedColor);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final EventListenerList listenerList = new EventListenerList();

	/** コンポーネントとボーダーのマップ **/
	private final Map<JComponent, ActivationBorder> _bordermap = new HashMap<JComponent, ActivationBorder>();

	/** 現在アクティブなコンポーネント **/
	private JComponent	_activated;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 標準のコンストラクタ
	 */
	public ActiveViewManager() {
		//--- setup Focus owner property change listener
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		.addPropertyChangeListener("focusOwner", new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				onFocusOwnerChanged(evt);
			}
		});
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isEmpty() {
		return _bordermap.isEmpty();
	}
	
	public Set<JComponent> registeredComponents() {
		return _bordermap.keySet();
	}
	
	public Collection<ActivationBorder> registeredBorders() {
		return _bordermap.values();
	}
	
	public boolean isRegistered(Component component) {
		return _bordermap.containsKey(component);
	}
	
	public ActivationBorder getBorder(Component component) {
		return _bordermap.get(component);
	}
	
	public boolean isComponentActivated(Component component) {
		if (_activated == null)
			return false;	// no activated component
		
		if (component == _activated)
			return true;	// component activated
		
		if (SwingUtilities.isDescendingFrom(component, _activated))
			return true;	// component descending activated view
		
		// not activated
		return false;
	}
	
	public JComponent getActivatedComponent() {
		return _activated;
	}
	
	public void setActivatedComponent(JComponent component) {
		JComponent oldActive = _activated;
		JComponent newActive = (_bordermap.containsKey(component) ? component : null);
		if (oldActive != newActive) {
			_activated = newActive;
			ActivationBorder oldBorder = _bordermap.get(oldActive);
			ActivationBorder newBorder = _bordermap.get(newActive);
			if (oldBorder != null) {
				oldBorder.selectDeactivatedBorder();
				oldActive.revalidate();
				oldActive.repaint();
			}
			if (newBorder != null) {
				newBorder.selectActivatedBorder();
				newActive.revalidate();
				newActive.repaint();
			}
			fireActiveViewChanged(newActive, oldActive);
		}
	}

	/**
	 * マネージャに管理対象のコンポーネントを登録する。
	 * @param component	登録するコンポーネント
	 * @throws IllegalArgumentException	引数が <tt>null</tt> の場合
	 */
	public void registerComponent(JComponent component) {
		if (component == null)
			throw new IllegalArgumentException("component argument is null!");
		if (_bordermap.containsKey(component))
			return;
		
		// create Border
		ActivationBorder newBorder;
		Border orgBorder = component.getBorder();
		if (orgBorder != null)
			newBorder = new ActivationBorder(DefaultActivatedBorder, DefaultDeactivatedBorder, orgBorder, true);
		else
			newBorder = new ActivationBorder(DefaultActivatedBorder, DefaultDeactivatedBorder);
		
		// set border
		component.setBorder(newBorder);
		
		// register
		_bordermap.put(component, newBorder);
	}

	/**
	 * マネージャから指定のコンポーネントを削除する。
	 * @param component	削除するコンポーネント
	 */
	public void unregisterComponent(JComponent component) {
		ActivationBorder removedBorder = _bordermap.remove(component);
		if (removedBorder != null) {
			component.setBorder(removedBorder.getOriginalBorder());
		}
		if (_activated == component) {
			_activated = null;
			fireActiveViewChanged(null, component);
		}
	}

	//
	// Events
	//
	
	public void addActiveViewChangeListener(ActiveViewChangeListener l) {
		listenerList.add(ActiveViewChangeListener.class, l);
	}
	
	public void removeActiveViewChangeListener(ActiveViewChangeListener l) {
		listenerList.remove(ActiveViewChangeListener.class, l);
	}
	
	public ActiveViewChangeListener[] getActiveViewChangeListeners() {
		return (ActiveViewChangeListener[])listenerList.getListeners(ActiveViewChangeListener.class);
	}
	
	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		return listenerList.getListeners(listenerType);
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void onFocusOwnerChanged(PropertyChangeEvent pce) {
		Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if (focusOwner == null) {
			// フォーカスオーナーが存在しなければ、現在のアクティベーションを変更しない
			return;
		}
		
		if (_bordermap.containsKey(focusOwner)) {
			setActivatedComponent((JComponent)focusOwner);
			return;
		}
		
		// 探す
		for (JComponent comp : _bordermap.keySet()) {
			if (SwingUtilities.isDescendingFrom(focusOwner, comp)) {
				// フォーカスオーナーのルートコンポーネントは登録済み
				setActivatedComponent(comp);
				return;
			}
		}
		
		// フォーカスオーナーが登録されていないコンポーネントのものであれば、
		// 現在のアクティベーションを変更しない
	}
	
	protected void fireActiveViewChanged(JComponent activated, JComponent deactivated) {
		Object[] listeners = listenerList.getListenerList();
		ActiveViewChangeEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==ActiveViewChangeListener.class) {
				if (e == null) {
					e = new ActiveViewChangeEvent(this, activated, deactivated);
				}
				((ActiveViewChangeListener)listeners[i+1]).activeViewChanged(e);
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * アクティブビューが変更されたときに通知されるイベントオブジェクト
	 * 
	 * @version 1.14	2009/12/09
	 * @since 1.14
	 */
	static public class ActiveViewChangeEvent extends EventObject
	{
		/** 新たにアクティブになったコンポーネント **/
		protected final JComponent _cActivated;
		/** 直前に非アクティブになったコンポーネント **/
		protected final JComponent _cDeactivated;
		
		public ActiveViewChangeEvent(Object source, JComponent activated, JComponent deactivated) {
			super(source);
			_cActivated = activated;
			_cDeactivated = deactivated;
		}
		
		public ActiveViewManager getSourceManager() {
			return (ActiveViewManager)getSource();
		}
		
		public JComponent getActivatedComponent() {
			return _cActivated;
		}
		
		public JComponent getDeactivatedComponent() {
			return _cDeactivated;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getName() + " " + Integer.toString(hashCode()));
			sb.append(" activated [");
			sb.append(_cActivated);
			sb.append("]");
			sb.append(" deactivated [");
			sb.append(_cDeactivated);
			sb.append("]");
			return sb.toString();
		}
	}

	/**
	 * アクティブビューが変更されたイベントを受け取るリスナーインタフェース
	 * 
	 * @version 1.14	2009/12/09
	 * @since 1.14
	 */
	static public interface ActiveViewChangeListener extends EventListener
	{
		public void activeViewChanged(ActiveViewChangeEvent e);
	}
}
