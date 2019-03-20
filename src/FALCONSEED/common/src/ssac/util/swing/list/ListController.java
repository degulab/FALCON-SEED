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
 */
package ssac.util.swing.list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

/**
 * リスト・コンポーネント用の機能ボタン・フレームワーク。
 * 機能ボタンは、追加、編集、削除、上へ移動、下へ移動の5機能であり、
 * このクラスはこれらの機能を実装するためのフレームワークを提供する。
 * 
 * @version 2.0.0 2012/10/19
 */
public class ListController
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private AbstractButton	btnAdd;
	private AbstractButton	btnEdit;
	private AbstractButton	btnDelete;
	private AbstractButton btnClear;
	private AbstractButton	btnUp;
	private AbstractButton	btnDown;
	
	private final ActionListener alBtnAdd;
	private final ActionListener alBtnEdit;
	private final ActionListener alBtnDelete;
	private final ActionListener alBtnClear;
	private final ActionListener alBtnUp;
	private final ActionListener alBtnDown;
	
	private IListControlHandler	handler;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * ハンドラを持たない <code>ListController</code> インスタンスを生成する。
	 */
	public ListController() {
		// make Action listener
		//--- add
		alBtnAdd = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				if (handler != null) {
					handler.onButtonAdd(ae);
				}
			}
		};
		//--- edit
		alBtnEdit = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				if (handler != null) {
					handler.onButtonEdit(ae);
				}
			}
		};
		//--- delete
		alBtnDelete = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				if (handler != null) {
					handler.onButtonDelete(ae);
				}
			}
		};
		//--- clear
		alBtnClear = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				if (handler != null) {
					handler.onButtonClear(ae);
				}
			}
		};
		//--- up
		alBtnUp = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				if (handler != null) {
					handler.onButtonUp(ae);
				}
			}
		};
		//--- down
		alBtnDown = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				if (handler != null) {
					handler.onButtonDown(ae);
				}
			}
		};
	}

	/**
	 * 指定のハンドラを持つ <code>ListController</code> インスタンスを生成する。
	 * 
	 * @param newHandler ハンドラとして関連付ける <code>{@link IListControlHandler}</code>
	 */
	public ListController(IListControlHandler newHandler) {
		this();
		setHandler(newHandler);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 関連付けられているハンドラを取得する。
	 * 
	 * @return 関連付けられている <code>{@link IListControlHandler}</code> を返す。
	 * 			ハンドラが関連付けられていない場合は <code>null</code> を返す。
	 */
	public IListControlHandler getHandler() {
		return handler;
	}

	/**
	 * 指定のハンドラを関連付ける。
	 * すでにハンドラが関連付けられている場合は、指定したハンドラに置き換える。
	 * 
	 * @param newHandler 関連付ける <code>{@link IListControlHandler}</code> を指定する。
	 * 						<code>null</code> の場合は、ハンドラの関連付けが解除される。
	 */
	public void setHandler(IListControlHandler newHandler) {
		handler = newHandler;
	}

	/**
	 * [追加]ボタンがアタッチされているかを検証する。
	 * 
	 * @return アタッチされている場合は <code>true</code> を返す。
	 */
	public boolean isAddButtonAttached() {
		return (btnAdd != null);
	}
	
	/**
	 * [編集]ボタンがアタッチされているかを検証する。
	 * 
	 * @return アタッチされている場合は <code>true</code> を返す。
	 */
	public boolean isEditButtonAttached() {
		return (btnEdit != null);
	}
	
	/**
	 * [削除]ボタンがアタッチされているかを検証する。
	 * 
	 * @return アタッチされている場合は <code>true</code> を返す。
	 */
	public boolean isDeleteButtonAttached() {
		return (btnDelete != null);
	}

	/**
	 * [クリア]ボタンがアタッチされているかを検証する。
	 * @return	アタッチされている場合は <code>true</code> を返す。
	 * @since 2.0.0
	 */
	public boolean isClearButtonAttached() {
		return (btnClear != null);
	}
	
	/**
	 * [上へ移動]ボタンがアタッチされているかを検証する。
	 * 
	 * @return アタッチされている場合は <code>true</code> を返す。
	 */
	public boolean isUpButtonAttached() {
		return (btnUp != null);
	}
	
	/**
	 * [下へ移動]ボタンがアタッチされているかを検証する。
	 * 
	 * @return アタッチされている場合は <code>true</code> を返す。
	 */
	public boolean isDownButtonAttached() {
		return (btnDown != null);
	}

	/**
	 * アタッチされている[追加]ボタンを取得する。
	 * 
	 * @return アタッチされている <code>AbstractButton</code> インスタンスを返す。
	 * 			ボタンがアタッチされていない場合は <code>null</code> を返す。
	 */
	public AbstractButton getAddButton() {
		return btnAdd;
	}
	
	/**
	 * アタッチされている[編集]ボタンを取得する。
	 * 
	 * @return アタッチされている <code>AbstractButton</code> インスタンスを返す。
	 * 			ボタンがアタッチされていない場合は <code>null</code> を返す。
	 */
	public AbstractButton getEditButton() {
		return btnEdit;
	}
	
	/**
	 * アタッチされている[削除]ボタンを取得する。
	 * 
	 * @return アタッチされている <code>AbstarctButton</code> インスタンスを返す。
	 * 			ボタンがアタッチされていない場合は <code>null</code> を返す。
	 */
	public AbstractButton getDeleteButton() {
		return btnDelete;
	}

	/**
	 * アタッチされている[クリア]ボタンを取得する。
	 * @return	アタッチされている <code>AbstractButton</code> インスタンスを返す。
	 * 			ボタンがアタッチされていない場合は <tt>null</tt> を返す
	 * @since 2.0.0
	 */
	public AbstractButton getClearButton() {
		return btnClear;
	}
	
	/**
	 * アタッチされている[上へ移動]ボタンを取得する。
	 * 
	 * @return アタッチされている <code>AbstractButton</code> インスタンスを返す。
	 * 			ボタンがアタッチされていない場合は <code>null</code> を返す。
	 */
	public AbstractButton getUpButton() {
		return btnUp;
	}
	
	/**
	 * アタッチされている[下へ移動]ボタンを取得する。
	 * 
	 * @return アタッチされている <code>JButton</code> インスタンスを返す。
	 * 			ボタンがアタッチされていない場合は <code>null</code> を返す。
	 */
	public AbstractButton getDownButton() {
		return btnDown;
	}

	/**
	 * アタッチされている[追加]ボタンの有効／無効を設定する。
	 * ボタンがアタッチされていない場合、この指定は無視される。
	 * 
	 * @param toEnable 有効にする場合は <code>true</code>、無効にする場合は <code>false</code>
	 */
	public void setAddButtonEnabled(boolean toEnable) {
		if (btnAdd != null && !isEqual(btnAdd.isEnabled(), toEnable)) {
			btnAdd.setEnabled(toEnable);
		}
	}
	
	/**
	 * アタッチされている[編集]ボタンの有効／無効を設定する。
	 * ボタンがアタッチされていない場合、この指定は無視される。
	 * 
	 * @param toEnable 有効にする場合は <code>true</code>、無効にする場合は <code>false</code>
	 */
	public void setEditButtonEnabled(boolean toEnable) {
		if (btnEdit != null && !isEqual(btnEdit.isEnabled(), toEnable)) {
			btnEdit.setEnabled(toEnable);
		}
	}
	
	/**
	 * アタッチされている[削除]ボタンの有効／無効を設定する。
	 * ボタンがアタッチされていない場合、この指定は無視される。
	 * 
	 * @param toEnable 有効にする場合は <code>true</code>、無効にする場合は <code>false</code>
	 */
	public void setDeleteButtonEnabled(boolean toEnable) {
		if (btnDelete != null && !isEqual(btnDelete.isEnabled(), toEnable)) {
			btnDelete.setEnabled(toEnable);
		}
	}

	/**
	 * アタッチされている[クリア]ボタンの有効／無効を設定する。
	 * ボタンがアタッチされていない場合、この指定は無視される。
	 * 
	 * @param toEnable 有効にする場合は <code>true</code>、無効にする場合は <code>false</code>
	 * @since 2.0.0
	 */
	public void setClearButtonEnabled(boolean toEnable) {
		if (btnClear != null && !isEqual(btnClear.isEnabled(), toEnable)) {
			btnClear.setEnabled(toEnable);
		}
	}
	
	/**
	 * アタッチされている[上へ移動]ボタンの有効／無効を設定する。
	 * ボタンがアタッチされていない場合、この指定は無視される。
	 * 
	 * @param toEnable 有効にする場合は <code>true</code>、無効にする場合は <code>false</code>
	 */
	public void setUpButtonEnabled(boolean toEnable) {
		if (btnUp != null && !isEqual(btnUp.isEnabled(), toEnable)) {
			btnUp.setEnabled(toEnable);
		}
	}
	
	/**
	 * アタッチされている[下へ移動]ボタンの有効／無効を設定する。
	 * ボタンがアタッチされていない場合、この指定は無視される。
	 * 
	 * @param toEnable 有効にする場合は <code>true</code>、無効にする場合は <code>false</code>
	 */
	public void setDownButtonEnabled(boolean toEnable) {
		if (btnDown != null && !isEqual(btnDown.isEnabled(), toEnable)) {
			btnDown.setEnabled(toEnable);
		}
	}

	/**
	 * [追加]ボタンのアタッチを解除する。
	 * 
	 * @return すでにアタッチされていた <code>AbstractButton</code> インスタンス
	 */
	public AbstractButton detachAddButton() {
		AbstractButton btn = detachButtonAction(btnAdd, alBtnAdd);
		btnAdd = null;
		return btn;
	}
	
	/**
	 * [編集]ボタンのアタッチを解除する。
	 * 
	 * @return すでにアタッチされていた <code>AbstractButton</code> インスタンス
	 */
	public AbstractButton detachEditButton() {
		AbstractButton btn = detachButtonAction(btnEdit, alBtnEdit);
		btnEdit = null;
		return btn;
	}
	
	/**
	 * [削除]ボタンのアタッチを解除する。
	 * 
	 * @return すでにアタッチされていた <code>AbstractButton</code> インスタンス
	 */
	public AbstractButton detachDeleteButton() {
		AbstractButton btn = detachButtonAction(btnDelete, alBtnDelete);
		btnDelete = null;
		return btn;
	}

	/**
	 * [クリア]ボタンのアタッチを解除する。
	 * @return	すでにアタッチされていた <code>AbstractButton</code> インスタンス
	 * @since 2.0.0
	 */
	public AbstractButton detachClearButton() {
		AbstractButton btn = detachButtonAction(btnClear, alBtnClear);
		btnClear = null;
		return btn;
	}
	
	/**
	 * [上へ移動]ボタンのアタッチを解除する。
	 * 
	 * @return すでにアタッチされていた <code>AbstractButton</code> インスタンス
	 */
	public AbstractButton detachUpButton() {
		AbstractButton btn = detachButtonAction(btnUp, alBtnUp);
		btnUp = null;
		return btn;
	}
	
	/**
	 * [下へ移動]ボタンのアタッチを解除する。
	 * 
	 * @return すでにアタッチされていた <code>AbstractButton</code> インスタンス
	 */
	public AbstractButton detachDownButton() {
		AbstractButton btn = detachButtonAction(btnDown, alBtnDown);
		btnDown = null;
		return btn;
	}

	/**
	 * [追加]ボタンをアタッチする。
	 * 
	 * @param btn アタッチする <code>AbstractButton</code> インスタンス
	 * @return すでにアタッチされていた <code>AbstractButton</code> インスタンス
	 */
	public AbstractButton attachAddButton(AbstractButton btn) {
		AbstractButton old = detachAddButton();
		btnAdd = attachButtonAction(btn, alBtnAdd);
		return old;
	}
	
	/**
	 * [編集]ボタンをアタッチする。
	 * 
	 * @param btn アタッチする <code>AbstractButton</code> インスタンス
	 * @return すでにアタッチされていた <code>AbstractButton</code> インスタンス
	 */
	public AbstractButton attachEditButton(AbstractButton btn) {
		AbstractButton old = detachEditButton();
		btnEdit = attachButtonAction(btn, alBtnEdit);
		return old;
	}
	
	/**
	 * [削除]ボタンをアタッチする。
	 * 
	 * @param btn アタッチする <code>AbstractButton</code> インスタンス
	 * @return すでにアタッチされていた <code>AbstractButton</code> インスタンス
	 */
	public AbstractButton attachDeleteButton(AbstractButton btn) {
		AbstractButton old = detachDeleteButton();
		btnDelete = attachButtonAction(btn, alBtnDelete);
		return old;
	}
	
	/**
	 * [クリア]ボタンをアタッチする。
	 * 
	 * @param btn アタッチする <code>AbstractButton</code> インスタンス
	 * @return すでにアタッチされていた <code>AbstractButton</code> インスタンス
	 * @since 2.0.0
	 */
	public AbstractButton attachClearButton(AbstractButton btn) {
		AbstractButton old = detachClearButton();
		btnClear = attachButtonAction(btn, alBtnClear);
		return old;
	}
	
	/**
	 * [上へ移動]ボタンをアタッチする。
	 * 
	 * @param btn アタッチする <code>AbstractButton</code> インスタンス
	 * @return すでにアタッチされていた <code>AbstractButton</code> インスタンス
	 */
	public AbstractButton attachUpButton(AbstractButton btn) {
		AbstractButton old = detachUpButton();
		btnUp = attachButtonAction(btn, alBtnUp);
		return old;
	}
	
	/**
	 * [下へ移動]ボタンをアタッチする。
	 * 
	 * @param btn アタッチする <code>AbstractButton</code> インスタンス
	 * @return すでにアタッチされていた <code>AbstractButton</code> インスタンス
	 */
	public AbstractButton attachDownButton(AbstractButton btn) {
		AbstractButton old = detachDownButton();
		btnDown = attachButtonAction(btn, alBtnDown);
		return old;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * <code>boolean</code> の値が等しいかを検証する
	 * 
	 * @return 等しい場合は <code>true</code> を返す。
	 */
	protected boolean isEqual(boolean ba, boolean bb) {
		if ((ba && bb) || (!ba && !bb))
			return true;
		else
			return false;
	}

	/**
	 * 指定のボタンにアクションを割り当てる。
	 * 
	 * @param btn 対象の <code>AbstractButton</code> インスタンス
	 * @param action 割り当てるアクション
	 * @return <code>btn</code> の値
	 */
	protected AbstractButton attachButtonAction(AbstractButton btn, ActionListener action) {
		if (btn != null) {
			btn.addActionListener(action);
		}
		return btn;
	}

	/**
	 * 指定のボタンからアクションを削除する。
	 * 
	 * @param btn 対象の <code>AbstractButton</code> インスタンス
	 * @param action 削除するアクション
	 * @return <code>btn</code> の値
	 */
	protected AbstractButton detachButtonAction(AbstractButton btn, ActionListener action) {
		if (btn != null) {
			btn.removeActionListener(action);
		}
		return btn;
	}
}
