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
/*
 * @(#)MExecArgsConfigPane.java	1.22	2012/08/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

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

import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.util.Objects;
import ssac.util.io.VirtualFile;

/**
 * モジュール実行時引数の設定ペイン。
 * 
 * @version 1.22	2012/08/21
 * @since 1.22
 */
public class MExecArgsConfigPane extends JPanel implements Scrollable, IMExecArgConfigHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** データモデル **/
	private ModuleRuntimeData	_datamodel;
	/** このコンポーネントの編集許可フラグ **/
	private boolean		_editable = true;
	/** 引数番号を表示するフラグ **/
	private boolean		_visibleArgNo = true;
	/** 相対パス表示用の基準パス **/
	private VirtualFile	_vfBasePath;
	/** 引数のコンポーネントのリスト **/
	private final List<MExecArgConfigPane>	_listItemPane;
	/** フレームのエディタビューでアクティブなファイル **/
	private VirtualFile	_vfViewingFile;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecArgsConfigPane() {
		super(new GridBagLayout());
		this._listItemPane = new ArrayList<MExecArgConfigPane>();
		setFocusTraversalPolicyProvider(true);	// フォーカスのダウントラバースを許可
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このコンポーネントに、エディタビューで表示されているファイルの抽象パスを設定する。
	 * @param newFile	設定する抽象パス。抽象パスを設定しない場合は <tt>null</tt>
	 */
	public void setViewingFileOnEditor(VirtualFile newFile) {
		if ((_vfViewingFile==null && newFile==null) || (newFile!=null && newFile.equals(_vfViewingFile))) {
			return;		// no changes
		}
		
		_vfViewingFile = newFile;
		for (MExecArgConfigPane item : _listItemPane) {
			if (item.isVisible() && item.isEditable() && item.getArgType()==ModuleArgType.IN) {
				item.updateValueComponents();
			}
		}
	}

	/**
	 * このコンポーネントの編集可能、または編集不可に設定する。
	 * @param editable	編集可能とする場合は <tt>true</tt>、編集不可とする場合は <tt>false</tt>
	 */
	public void setEditable(boolean editable) {
		boolean oldEditable = this._editable;
		this._editable = editable;
		if (oldEditable != editable) {
			for (MExecArgConfigPane item : _listItemPane) {
				item.updateValueComponents();
			}
		}
	}

	/**
	 * 引数番号の表示・非表示を設定する。
	 * @param visible	引数番号を表示する場合は <tt>true</tt>、非表示の場合は <tt>false</tt>
	 */
	public void setVisibleArgNo(boolean visible) {
		boolean oldVisible = this._visibleArgNo;
		this._visibleArgNo = visible;
		if (oldVisible != visible) {
			for (MExecArgConfigPane item : _listItemPane) {
				item.updateArgNo();
			}
		}
	}

	/**
	 * パス表示時の基準パスを設定する。
	 * ここで指定したパスを含むパスを表示する際、相対パスとして表示する。
	 * @param basePath	基準パスを指定する。基準パスを設定しない場合は <tt>null</tt> を指定する。
	 */
	public void setBasePath(VirtualFile basePath) {
		VirtualFile oldPath = this._vfBasePath;
		this._vfBasePath = basePath;
		if (!Objects.isEqual(oldPath, basePath)) {
			for (MExecArgConfigPane item : _listItemPane) {
				item.updateValueComponents();
			}
		}
	}

	/**
	 * このコンポーネントに設定されているアイテム数を取得する。
	 * @return	アイテム数
	 */
	public int getNumItems() {
		return (_datamodel==null ? 0 : _datamodel.getArgumentCount());
	}

	/**
	 * このコンポーネントのデータモデルを取得する。
	 * モデルが設定されていない場合は <tt>null</tt> を返す。
	 * @return	<code>ModuleRuntimeData</code> オブジェクト。
	 * 			設定されていない場合は <tt>null</tt>
	 */
	public ModuleRuntimeData getDataModel() {
		return _datamodel;
	}

	/**
	 * このコンポーネントにモデルを設定する。
	 * @param newModel	新しい <code>ModuleRuntimeData</code> オブジェクト
	 */
	public void setModel(ModuleRuntimeData newModel) {
		if (newModel == _datamodel) {
			return;		// no changes
		}
		
		this._datamodel = newModel;
		setupItemComponents();
	}

	/**
	 * 指定された位置のアイテムが編集可能かを判定する。
	 * このメソッドは、{@link #isEditable()} の返す値に関係なく、アイテムの状態を返す。
	 * @param index	アイテムの位置を示すインデックス
	 * @return	編集可能なら <tt>true</tt>、不可能なら <tt>false</tt>
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public boolean isItemEditable(int index) {
		checkItemIndex(index);
		IModuleArgConfig item = _datamodel.getArgument(index);
		return MExecArgConfigPane.isDataEditable(item);
	}

	/**
	 * 指定された位置のアイテムの表示状態を取得する。
	 * @param index	アイテムの位置を示すインデックス
	 * @return	表示されていれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public boolean isItemVisible(int index) {
		checkItemIndex(index);
		return _listItemPane.get(index).isVisible();
	}

	/**
	 * 指定された位置のアイテムの表示状態を設定する。
	 * @param index	アイテムの位置を示すインデックス
	 * @param visible	表示する場合は <tt>true</tt>、非表示とする場合は <tt>false</tt>
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public void setItemVisible(int index, boolean visible) {
		checkItemIndex(index);
		_listItemPane.get(index).setVisible(visible);
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
		MExecArgConfigPane item = _listItemPane.get(index);
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
		for (MExecArgConfigPane item : _listItemPane) {
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
		for (MExecArgConfigPane item : _listItemPane) {
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
		for (MExecArgConfigPane item : _listItemPane) {
			if (item.hasError()) {
				errmsg = formatItemErrorWithLocation(item);
				break;
			}
		}
		return errmsg;
	}

	/**
	 * アイテムの値の正当性を検証する。
	 * このメソッドは、アイテムが表示されており編集可能な場合にのみ、
	 * そのアイテムの値の正当性を検証する。
	 * @return	検証の結果、エラーのアイテムが存在した場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean verifyItemValues() {
		MExecArgConfigPane topErrorItem = null;
		for (MExecArgConfigPane item : _listItemPane) {
			if (item.isVisible() && item.isEditable()) {
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
			MExecArgConfigPane item = _listItemPane.get(index);
			if (item.isVisible()) {
				scrollRectToVisible(item.getBounds());
			}
		} catch (Throwable ignoreEx) {}
	}

	/**
	 * 指定された引数履歴の値を、このコンポーネントに設定する。
	 * @param history	引数履歴
	 * @return	履歴の引数値のどれか一つでも設定された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @since 1.20
	 */
	public boolean setHistory(List<ModuleArgData> history) {
		if (!isEditable())
			return false;		// cannot editing
		int len = Math.min(history.size(), getNumItems());
		boolean modified = false;
		for (int index = 0; index < len; index++) {
			ModuleArgData hdata = history.get(index);
			MExecArgConfigPane item = _listItemPane.get(index);
			if (item.setHistoryData(hdata)) {
				modified = true;
			}
		}
		return modified;
	}

	//------------------------------------------------------------
	// Implement IMExecArgConfigHandler interfaces
	//------------------------------------------------------------

	/**
	 * コンポーネントが編集可能か判定する。
	 * @return	編集可能なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isEditable() {
		return _editable;
	}

	/**
	 * 引数番号を表示・非表示の設定を取得する。
	 * @return	引数番号を表示する場合は <tt>true</tt>、非表示の場合は <tt>false</tt>
	 */
	public boolean isVisibleArgNo() {
		return _visibleArgNo;
	}

	/**
	 * 指定された引数IDから、表示名を取得する。
	 * @param argid	引数ID
	 * @return	表示名を示す文字列
	 */
	public String getDisplayModuleArgID(ModuleArgID argid) {
		return ModuleArgID.formatDisplayString(argid);
	}

	/**
	 * このコンポーネントに設定された、エディタビューで表示されているファイルの抽象パスを返す。
	 * 設定されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getViewingFileOnEditor() {
		return _vfViewingFile;
	}

	/**
	 * パス表示時の基準パスを取得する。
	 * @return	基準パスを返す。設定されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getBasePath() {
		return _vfBasePath;
	}

	/**
	 * 指定された子コンポーネントが表示されるように、スクロールする。
	 * @param component	対象の子コンポーネント
	 */
	public void scrollClientComponentToVisible(Component component) {
		Rectangle rc = component.getBounds();
		Rectangle rcParent = SwingUtilities.convertRectangle(component, rc, this);
		//AppLogger.debug("MExecArgsConfigPane#scrollClientComponentToVisible() : rc=" + rc.toString() + " / rcParent=" + rcParent.toString() + " / component=" + component.toString());
		
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void checkItemIndex(int index) {
		if (_datamodel == null)
			throw new ArrayIndexOutOfBoundsException(index);
		if (index >= _datamodel.getArgumentCount())
			throw new ArrayIndexOutOfBoundsException(index);
	}
	
	protected String formatItemErrorWithLocation(MExecArgConfigPane item) {
		if (item == null || !item.hasError())
			return null;
		
		return String.format("%s %s", item.getArgTypeLabelText(), item.getError());
	}
	
	protected void setupItemComponents() {
		// 古いコンポーネントを破棄
		this._listItemPane.clear();
		this.removeAll();

		// 新しいアイテム数を判定
		int num = (_datamodel==null ? 0 : _datamodel.getArgumentCount());
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
			MExecArgConfigPane itemPane = createItemPane();
			itemPane.setArgDataModel(_datamodel.getArgument(i));
			this._listItemPane.add(itemPane);
			this.add(itemPane, gbc);
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
	
	protected MExecArgConfigPane createItemPane() {
		return new MExecArgConfigPane(this);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class ItemFocusTraversalPolicy extends FocusTraversalPolicy
	{
		@Override
		public Component getComponentAfter(Container container, Component component) {
			//AppLogger.debug("MExecArgsConfigPane.ItemFocusTraversalPolicy#getComponentAfter()");
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
			//AppLogger.debug("MExecArgsConfigPane.ItemFocusTraversalPolicy#getComponentAfter() : nextIndex=" + nextIndex);
			if (nextIndex < 0) {
				return null;
			} else {
				return _listItemPane.get(nextIndex);
			}
		}

		@Override
		public Component getComponentBefore(Container container, Component component) {
			//AppLogger.debug("MExecArgsConfigPane.ItemFocusTraversalPolicy#getComponentBefore()");
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
			//AppLogger.debug("MExecArgsConfigPane.ItemFocusTraversalPolicy#getComponentBefore() : prevIndex=" + prevIndex);
			if (prevIndex < 0) {
				return null;
			} else {
				return _listItemPane.get(prevIndex);
			}
		}

		@Override
		public Component getDefaultComponent(Container container) {
			//AppLogger.debug("MExecArgsConfigPane.ItemFocusTraversalPolicy#getDefaultComponent()");
			if (_listItemPane.isEmpty()) {
				return null;
			} else {
				return _listItemPane.get(0);
			}
		}

		@Override
		public Component getFirstComponent(Container container) {
			//AppLogger.debug("MExecArgsConfigPane.ItemFocusTraversalPolicy#getFirstComponent()");
			if (_listItemPane.isEmpty()) {
				return null;
			} else {
				return _listItemPane.get(0);
			}
		}

		@Override
		public Component getLastComponent(Container container) {
			//AppLogger.debug("MExecArgsConfigPane.ItemFocusTraversalPolicy#getLastComponent()");
			if (_listItemPane.isEmpty()) {
				return null;
			} else {
				return _listItemPane.get(_listItemPane.size()-1);
			}
		}
	};
}
