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
 * @(#)EtcDestSetEditPane.java	3.3.0	2016/05/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.excel2csv.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import ssac.falconseed.file.VirtualFilePathFormatterList;
import ssac.util.excel2csv.EtcConfigDataSet;
import ssac.util.io.VirtualFile;

/**
 * <code>[Excel to CSV]</code> 変換定義の出力先設定パネル。
 * 複数の CSV ファイル出力先を設定する。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcDestSetEditPane extends JPanel implements Scrollable, EtcDestItemChangeHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 引数のコンポーネントのリスト **/
	private final List<EtcDestItemEditPane>	_listItemPane;

	/** イベントハンドラー **/
	private final EtcDestSetChangeHandler	_handler;
	/** データモデル **/
	private EtcConfigDataSet				_datamodel;
	/** パスフォーマッター **/
	private VirtualFilePathFormatterList	_vfFormatter;
	/** テンポラリ出力が有効なアイテム数 **/
	private int								_numOutToTempEnabledItems;
	/** 結果閲覧が有効なアイテム数 **/
	private int								_numShowDestEnabledItems;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public EtcDestSetEditPane(VirtualFilePathFormatterList vfFormatter, EtcDestSetChangeHandler handler) {
		super(new GridBagLayout());
		if (handler == null)
			throw new NullPointerException();
		_handler = handler;
		_vfFormatter  = vfFormatter;
		_listItemPane = new ArrayList<EtcDestItemEditPane>();
		setFocusTraversalPolicyProvider(true);	// フォーカスのダウントラバースを許可
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * すべてのアイテムで [テンポラリ出力] が選択されているかどうかを判定する。
	 * @return	すべて選択されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isOutToTempSelectedAll() {
		return (_listItemPane.isEmpty() ? false : _listItemPane.size() == _numOutToTempEnabledItems);
	}

	/**
	 * すべてのアイテムで [テンポラリ出力] が選択解除されているかどうかを判定する。
	 * @return	すべて選択解除されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isOutToTempSelectionEmpty() {
		return (_numOutToTempEnabledItems == 0);
	}

	/**
	 * すべてのアイテムの [テンポラリ出力] 設定を変更する。
	 * @param toSelect		すべて選択する場合は <tt>true</tt>、すべて解除する場合は <tt>false</tt>
	 * @return	選択状態が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean selectAllOutToTemp(boolean toSelect) {
		boolean modified = false;
		int numSelected = 0;
		for (EtcDestItemEditPane itemPane : _listItemPane) {
			if (itemPane.setOutToTempEnabled(toSelect, false)) {
				modified = true;
			}
			if (itemPane.getOutToTempEnabled()) {
				++numSelected;
			}
		}
		_numOutToTempEnabledItems = numSelected;
		return modified;
	}

	/**
	 * すべてのアイテムで [結果を閲覧] が選択されているかどうかを判定する。
	 * @return	すべて選択されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isShowDestSelectedAll() {
		return (_listItemPane.isEmpty() ? false : _listItemPane.size() == _numShowDestEnabledItems);
	}

	/**
	 * すべてのアイテムで [結果を閲覧] が選択解除されているかどうかを判定する。
	 * @return	すべて選択解除されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isShowDestSelectionEmpty() {
		return (_numShowDestEnabledItems == 0);
	}

	/**
	 * すべてのアイテムの [結果を閲覧] 設定を変更する。
	 * @param toSelect		すべて選択する場合は <tt>true</tt>、すべて解除する場合は <tt>false</tt>
	 * @return	選択状態が変更された場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean selectAllShowDest(boolean toSelect) {
		boolean modified = false;
		int numSelected = 0;
		for (EtcDestItemEditPane itemPane : _listItemPane) {
			if (itemPane.setShowDestEnabled(toSelect, false)) {
				modified = true;
			}
			if (itemPane.getShowDestEnabled()) {
				++numSelected;
			}
		}
		_numShowDestEnabledItems = numSelected;
		return modified;
	}

	/**
	 * このコンポーネントに設定されているアイテム数を取得する。
	 * @return	アイテム数
	 */
	public int getNumItems() {
		return (_datamodel==null ? 0 : _datamodel.size());
	}
	
	public EtcDestItemEditPane getItemPane(int index) {
		return _listItemPane.get(index);
	}

	/**
	 * このコンポーネントのデータモデルを取得する。
	 * モデルが設定されていない場合は <tt>null</tt> を返す。
	 * @return	<code>EtcConfigDataSet</code> オブジェクト。
	 * 			設定されていない場合は <tt>null</tt>
	 */
	public EtcConfigDataSet getDataModel() {
		return _datamodel;
	}

	/**
	 * このコンポーネントにモデルを設定する。
	 * @param newModel	新しい <code>EtcConfigDataSet</code> オブジェクト
	 */
	public void setModel(EtcConfigDataSet newModel) {
		if (newModel == _datamodel) {
			return;		// no changes
		}
		
		_datamodel = newModel;
		_numOutToTempEnabledItems = 0;
		_numShowDestEnabledItems  = 0;
		setupItemComponents();
	}

	/**
	 * 指定された位置のアイテムにエラーがあるかを判定する。
	 * @param index	アイテムの位置を示すインデックス
	 * @return	エラーがある場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public boolean hasItemError(int index) {
		checkItemIndex(index);
		return _listItemPane.get(index).hasError();
	}

	/**
	 * 指定された位置のアイテムのエラーメッセージを取得する。
	 * @param index	アイテムの位置を示すインデックス
	 * @return	エラーメッセージを返す。エラーがない場合は <tt>null</tt> を返す。
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public String getItemError(int index) {
		checkItemIndex(index);
		EtcDestItemEditPane item = _listItemPane.get(index);
		return formatItemErrorWithLocation(item);
	}

	/**
	 * 指定された位置のアイテムのエラーをクリアする。
	 * @param index	アイテムの位置を示すインデックス
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public void clearItemError(int index) {
		checkItemIndex(index);
		_listItemPane.get(index).clearError();
	}

	/**
	 * 指定された位置のアイテムにエラーメッセージを設定する。
	 * <em>errmsg</em> が <tt>null</tt> もしくは空文字の場合は、エラーがクリアされる。
	 * @param index	アイテムの位置を示すインデックス
	 * @param errmsg	設定するエラーメッセージ
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public void setItemError(int index, String errmsg) {
		checkItemIndex(index);
		_listItemPane.get(index).setError(errmsg);
	}

	/**
	 * エラーのあるアイテムが存在するかを判定する。
	 * @return	エラーのあるアイテムが存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean hasErrorItems() {
		boolean hasError = false;
		for (EtcDestItemEditPane item : _listItemPane) {
			if (item.hasError()) {
				hasError = true;
				break;
			}
		}
		return hasError;
	}

	/**
	 * 全てのアイテムのエラーをクリアする。
	 */
	public void clearAllItemErrors() {
		for (EtcDestItemEditPane item : _listItemPane) {
			item.clearError();
		}
	}

	/**
	 * 引数順において、最も先頭に近いエラーを持つアイテムのエラーメッセージを取得する。
	 * @return	取得したエラーメッセージを返す。エラーを持つアイテムが一つも存在しない
	 * 			場合は <tt>null</tt> を返す。
	 */
	public String getFirstItemError() {
		String errmsg = null;
		for (EtcDestItemEditPane item : _listItemPane) {
			if (item.hasError()) {
				errmsg = formatItemErrorWithLocation(item);
				break;
			}
		}
		return errmsg;
	}

	/**
	 * 出力先ファイルのパスを整形する。
	 * @param destPath	整形するパス
	 * @return	整形後のパス文字列
	 */
	public String formatDestPath(VirtualFile destPath) {
		if (destPath == null)
			return null;
		
		if (_vfFormatter != null) {
			String strPath = _vfFormatter.formatPath(destPath);
			if (strPath != null) {
				return strPath;
			}
		}
		
		return destPath.toString();
	}

	/**
	 * アイテムの値の正当性を検証する。
	 * このメソッドは、アイテムが表示されており編集可能な場合にのみ、
	 * そのアイテムの値の正当性を検証する。
	 * @return	検証の結果、エラーのアイテムが存在した場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean verifyItemValues() {
		EtcDestItemEditPane topErrorItem = null;
		for (EtcDestItemEditPane item : _listItemPane) {
			if (item.isVisible()) {
				if (!item.verifyValue() && topErrorItem==null) {
					topErrorItem = item;
				}
			}
		}
		
		if (topErrorItem != null) {
			Rectangle rc = topErrorItem.getBounds();
			scrollRectToVisible(rc);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ビューの最上部が表示されるようにスクロールする。
	 */
	public void scrollTopToVisible() {
		scrollRectToVisible(new Rectangle(0,0,2,2));
	}

	/**
	 * ビューの最下部が表示されるようにスクロールする。
	 */
	public void scrollBottomToVisible() {
		Dimension dm = getSize();
		scrollRectToVisible(new Rectangle(0, dm.height-2,2,2));
	}

	/**
	 * 指定された位置のアイテムが表示されるようにスクロールする。
	 * ただし、指定された位置のアイテムが非表示の場合や、インデックスが
	 * 無効な場合、このメソッドはなにもしない。
	 * @param index	アイテムの位置を示すインデックス
	 */
	public void scrollItemToVisible(int index) {
		try {
			checkItemIndex(index);
			EtcDestItemEditPane item = _listItemPane.get(index);
			if (item.isVisible()) {
				scrollRectToVisible(item.getBounds());
			}
		} catch (Throwable ignoreEx) {}
	}

	/**
	 * 指定された子コンポーネントが表示されるように、スクロールする。
	 * @param component	対象の子コンポーネント
	 */
	public void scrollClientComponentToVisible(Component component) {
		Rectangle rc = component.getBounds();
		Rectangle rcParent = SwingUtilities.convertRectangle(component, rc, this);
		//AppLogger.debug("EtcDestEditPane#scrollClientComponentToVisible() : rc=" + rc.toString() + " / rcParent=" + rcParent.toString() + " / component=" + component.toString());
		
		scrollRectToVisible(rcParent);
	}

	//------------------------------------------------------------
	// Implement javax.swing.Scrollable interfaces
	//------------------------------------------------------------

	/**
	 * このコンポーネントのビューポートの適切なサイズを返す。
	 * これを実装すると、コンポーネントの適切なサイズを返すデフォルト動作を実行できる。
	 * @return	このオブジェクトをビューとして持つ <code>JViewport</code> の <code>preferredSize</code>
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	/**
	 * この実装は、単純に可視領域の 10% を返す。
	 * 
	 * @param visibleRect	ビューポート内の可視のビュー領域
	 * @param orientation	<code>SwingConstants.VERTICAL</code> または <code>SwingConstants.HORIZONTAL</code>
	 * @param direction		上または左にスクロールする場合は 0 より小さく、下または右にスクロールする場合は 0 より大きい
	 * @return	指定された方向にスクロールするための「ユニット」増分値
	 * @throws IllegalArgumentException	方向が無効な場合
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (SwingConstants.VERTICAL == orientation) {
			return visibleRect.height / 10;
		}
		else if (SwingConstants.HORIZONTAL == orientation) {
			return visibleRect.width / 10;
		}
		else {
			throw new IllegalArgumentException("Invalid orientation: " + orientation);
		}
	}

	/**
	 * スクロール可能な論理ブロックサイズを返す。
	 * このデフォルト実装は、単純に可視領域を返す。
	 * 
	 * @param visibleRect	ビューポート内の可視のビュー領域
	 * @param orientation	<code>SwingConstants.VERTICAL</code> または <code>SwingConstants.HORIZONTAL</code>
	 * @param direction		上または左にスクロールする場合は 0 より小さく、下または右にスクロールする場合は 0 より大きい
	 * @return	指定された方向にスクロールするための「ブロック」増分値
	 * @throws IllegalArgumentException	方向が無効な場合
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (SwingConstants.VERTICAL == orientation) {
			return visibleRect.height;
		}
		else if (SwingConstants.HORIZONTAL == orientation) {
			return visibleRect.width;
		}
		else {
			throw new IllegalArgumentException("Invalid orientation: " + orientation);
		}
	}

	/**
	 * ビューポートが、常にこの <code>Scrollable</code> の幅を強制的にビューポートの幅に一致させようとする場合は <tt>true</tt> を返す。
	 * 
	 * @return	常に <tt>true</tt> を返す。
	 */
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	/**
	 * ビューポートで、この <code>Scrollable</code> の高さを常にビューポートの高さに合わせる場合に <tt>true</tt> を返す。
	 * このメソッドで <tt>true</tt> を返すと、垂直スクロールが事実上無効になる。
	 * 
	 * @return	ビューポートが <code>Scrollable</code> の高さを強制的にそれ自体に一致させる場合は <tt>true</tt>
	 */
	public boolean getScrollableTracksViewportHeight() {
		// 標準の実装
		if (getParent() instanceof JViewport) {
			return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
		}
		return false;
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	/**
	 * ユーザー操作によってテンポラリ出力設定が変更されたときに呼び出されるイベントハンドラ
	 * @param itemPane	変更された要素のコンポーネント
	 */
	@Override
	public void editOutToTempChanged(EtcDestItemEditPane itemPane) {
		if (itemPane.getOutToTempEnabled()) {
			++_numOutToTempEnabledItems;
			if (_listItemPane.size() > _numOutToTempEnabledItems) {
				updateNumOutToTempEnabledItems(true);
			}
		} else {
			--_numOutToTempEnabledItems;
			if (_numOutToTempEnabledItems < 0) {
				updateNumOutToTempEnabledItems(true);
			}
		}
		if (_handler != null) {
			_handler.editOutToTempChanged(this);
		}
	}

	/**
	 * ユーザー操作によって結果閲覧出力設定が変更されたときに呼び出されるイベントハンドラ
	 * @param itemPane	変更された要素のコンポーネント
	 */
	@Override
	public void editShowDestChanged(EtcDestItemEditPane itemPane) {
		if (itemPane.getShowDestEnabled()) {
			++_numShowDestEnabledItems;
			if (_listItemPane.size() > _numShowDestEnabledItems) {
				updateNumShowDestEnabledItems(true);
			}
		} else {
			--_numShowDestEnabledItems;
			if (_numShowDestEnabledItems < 0) {
				updateNumShowDestEnabledItems(true);
			}
		}
		if (_handler != null) {
			_handler.editShowDestChanged(this);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * テンポラリ出力が有効な要素数を更新する。
	 * @param ignoreEventHandler	要素数が更新されたときでもイベントハンドラを呼び出さない場合は <tt>true</tt>
	 * @return	要素数が更新されたときは <tt>true</tt>
	 */
	protected boolean updateNumOutToTempEnabledItems(boolean ignoreEventHandler) {
		int numSelected = 0;
		for (EtcDestItemEditPane itemPane : _listItemPane) {
			if (itemPane.getOutToTempEnabled()) {
				++numSelected;
			}
		}
		if (numSelected != _numOutToTempEnabledItems) {
			if (!ignoreEventHandler && _handler != null) {
				_handler.editOutToTempChanged(this);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 結果閲覧が有効な要素数を更新する。
	 * @param ignoreEventHandler	要素数が更新されたときでもイベントハンドラを呼び出さない場合は <tt>true</tt>
	 * @return	要素数が更新されたときは <tt>true</tt>
	 */
	protected boolean updateNumShowDestEnabledItems(boolean ignoreEventHandler) {
		int numSelected = 0;
		for (EtcDestItemEditPane itemPane : _listItemPane) {
			if (itemPane.getShowDestEnabled()) {
				++numSelected;
			}
		}
		if (numSelected != _numShowDestEnabledItems) {
			if (!ignoreEventHandler && _handler != null) {
				_handler.editShowDestChanged(this);
			}
			return true;
		} else {
			return false;
		}
	}
	
	protected void checkItemIndex(int index) {
		if (_datamodel == null)
			throw new ArrayIndexOutOfBoundsException(index);
		if (index >= _datamodel.size())
			throw new ArrayIndexOutOfBoundsException(index);
	}

	protected String formatItemErrorWithLocation(EtcDestItemEditPane item) {
		if (item == null || !item.hasError())
			return null;
		
		return String.format("%s %s", item.getDestNoLabelText(), item.getError());
	}
	
	protected void setupItemComponents() {
		// 古いコンポーネントを破棄
		this._listItemPane.clear();
		this.removeAll();

		// 新しいアイテム数を判定
		int num = (_datamodel==null ? 0 : _datamodel.size());
		if (num <= 0) {
			revalidate();
			repaint();
			return;		// no items
		}

		// 新しいアイテムコンポーネントを登録
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
		//gbc.ipadx = 30;
		//gbc.ipady = 10;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 10, 10);
		for (int i = 0; i < num; i++) {
			EtcDestItemEditPane itemPane = new EtcDestItemEditPane(i, _datamodel.get(i), this);
			this._listItemPane.add(itemPane);
			this.add(itemPane, gbc);
			if (itemPane.getOutToTempEnabled()) {
				++_numOutToTempEnabledItems;
			}
			if (itemPane.getShowDestEnabled()) {
				++_numShowDestEnabledItems;
			}
			gbc.gridy++;
		}
		// 下部余白生成のためのダミーコンポーネント
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0,0,0,0);
		gbc.weighty = 1;
		this.add(new JLabel(), gbc);
		revalidate();
		repaint();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class ItemFocusTraversalPolicy extends FocusTraversalPolicy
	{
		@Override
		public Component getComponentAfter(Container container, Component component) {
			//AppLogger.debug("EtcDestEditPane.ItemFocusTraversalPolicy#getComponentAfter()");
			int nextIndex = (-1);
			if (!_listItemPane.isEmpty()) {
				nextIndex = _listItemPane.indexOf(component);
				if (nextIndex >= 0 && nextIndex < (_listItemPane.size()-1)) {
					nextIndex = nextIndex + 1;
				} else {
					nextIndex = (-1);
				}
				//--- または
				//nextIndex = (nextIndex + 1) % _listItemPane.size();
			}
			//AppLogger.debug("EtcDestEditPane.ItemFocusTraversalPolicy#getComponentAfter() : nextIndex=" + nextIndex);
			if (nextIndex < 0) {
				return null;
			} else {
				return _listItemPane.get(nextIndex);
			}
		}

		@Override
		public Component getComponentBefore(Container container, Component component) {
			//AppLogger.debug("EtcDestEditPane.ItemFocusTraversalPolicy#getComponentBefore()");
			int prevIndex = (-1);
			if (!_listItemPane.isEmpty()) {
				prevIndex = _listItemPane.indexOf(component);
				if (prevIndex > 0) {
					prevIndex = prevIndex - 1;
				} else {
					prevIndex = (-1);
				}
				//--- または
				//prevIndex = (prevIndex - 1 + _listItemPane.size()) % _listItemPane.size();
			}
			//AppLogger.debug("EtcDestEditPane.ItemFocusTraversalPolicy#getComponentBefore() : prevIndex=" + prevIndex);
			if (prevIndex < 0) {
				return null;
			} else {
				return _listItemPane.get(prevIndex);
			}
		}

		@Override
		public Component getDefaultComponent(Container container) {
			//AppLogger.debug("EtcDestEditPane.ItemFocusTraversalPolicy#getDefaultComponent()");
			if (_listItemPane.isEmpty()) {
				return null;
			} else {
				return _listItemPane.get(0);
			}
		}

		@Override
		public Component getFirstComponent(Container container) {
			//AppLogger.debug("EtcDestEditPane.ItemFocusTraversalPolicy#getFirstComponent()");
			if (_listItemPane.isEmpty()) {
				return null;
			} else {
				return _listItemPane.get(0);
			}
		}

		@Override
		public Component getLastComponent(Container container) {
			//AppLogger.debug("EtcDestEditPane.ItemFocusTraversalPolicy#getLastComponent()");
			if (_listItemPane.isEmpty()) {
				return null;
			} else {
				return _listItemPane.get(_listItemPane.size()-1);
			}
		}
	};
}
