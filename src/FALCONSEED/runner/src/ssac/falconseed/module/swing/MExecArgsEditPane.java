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
 * @(#)MExecArgsEditPane.java	3.1.0	2014/05/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgsEditPane.java	1.22	2012/08/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgsEditPane.java	1.20	2012/03/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgsEditPane.java	1.13	2011/11/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgsEditPane.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MExecArgsEditPane.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.file.VirtualFileTransferable;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgCsvFile;
import ssac.falconseed.module.args.MExecArgDirectory;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.args.MExecArgTextFile;
import ssac.falconseed.module.args.MExecArgXmlFile;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.Objects;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.JMultilineLabel;
import ssac.util.swing.JStaticMultilineTextPane;

/**
 * モジュール実行時引数の設定ペイン。
 * 
 * @version 3.1.0	2014/05/14
 */
public class MExecArgsEditPane extends JPanel implements Scrollable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	private final MExecArgItemFocusListener	_hItemFocus = new MExecArgItemFocusListener();

	/** データモデル **/
	private MExecArgsModel	_model;
	/** このコンポーネントの編集許可フラグ **/
	private boolean		_editable = true;
	/** 引数番号を表示するフラグ **/
	private boolean		_visibleArgNo = true;
	/** 相対パス表示用の基準パス **/
	private VirtualFile	_vfBasePath;
	/** 引数のコンポーネントのリスト **/
	private final List<MExecArgItemPane>	_listItemPane;
	/** フレームのエディタビューでアクティブなファイル **/
	private VirtualFile	_vfViewingFile;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecArgsEditPane() {
		super(new GridBagLayout());
		this._listItemPane = new ArrayList<MExecArgItemPane>();
		setFocusTraversalPolicyProvider(true);	// フォーカスのダウントラバースを許可
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このコンポーネントに設定された、エディタビューで表示されているファイルの抽象パスを返す。
	 * 設定されていない場合は <tt>null</tt> を返す。
	 * @since 1.20
	 */
	public VirtualFile getViewingFileOnEditor() {
		return _vfViewingFile;
	}

	/**
	 * このコンポーネントに、エディタビューで表示されているファイルの抽象パスを設定する。
	 * @param newFile	設定する抽象パス。抽象パスを設定しない場合は <tt>null</tt>
	 * @since 1.20
	 */
	public void setViewingFileOnEditor(VirtualFile newFile) {
		if ((_vfViewingFile==null && newFile==null) || (newFile!=null && newFile.equals(_vfViewingFile))) {
			return;		// no changes
		}
		
		_vfViewingFile = newFile;
		for (MExecArgItemPane item : _listItemPane) {
			if (item.isVisible() && item.getItem().isEditable() && item.getItem().getType()==ModuleArgType.IN) {
				item.updateValueComponents();
			}
		}
	}

	/**
	 * このコンポーネントが編集可能かを判定する
	 * @return	編集可能な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean isEditable() {
		return _editable;
	}

	/**
	 * このコンポーネントの編集可能、または編集不可に設定する。
	 * @param editable	編集可能とする場合は <tt>true</tt>、編集不可とする場合は <tt>false</tt>
	 */
	public void setEditable(boolean editable) {
		boolean oldEditable = this._editable;
		this._editable = editable;
		if (oldEditable != editable) {
			for (MExecArgItemPane item : _listItemPane) {
				item.updateValueComponents();
			}
		}
	}

	/**
	 * 引数番号を表示・非表示の設定を取得する。
	 * @return	引数番号を表示する場合は <tt>true</tt>、非表示の場合は <tt>false</tt>
	 */
	public boolean isVisibleArgNo() {
		return _visibleArgNo;
	}

	/**
	 * 引数番号の表示・非表示を設定する。
	 * @param visible	引数番号を表示する場合は <tt>true</tt>、非表示の場合は <tt>false</tt>
	 */
	public void setVisibleArgNo(boolean visible) {
		boolean oldVisible = this._visibleArgNo;
		this._visibleArgNo = visible;
		if (oldVisible != visible) {
			for (MExecArgItemPane item : _listItemPane) {
				item.updateArgNo();
			}
		}
	}

	/**
	 * パス表示時の基準パスを取得する。
	 * @return	基準パスを返す。設定されていない場合は <tt>null</tt> を返す。
	 */
	public VirtualFile getBasePath() {
		return _vfBasePath;
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
			for (MExecArgItemPane item : _listItemPane) {
				item.updateValueComponents();
			}
		}
	}

	/**
	 * このコンポーネントに設定されているアイテム数を取得する。
	 * @return	アイテム数
	 */
	public int getNumItems() {
		return (_model==null ? 0 : _model.getNumItems());
	}

	/**
	 * このコンポーネントのデータモデルを取得する。
	 * モデルが設定されていない場合は <tt>null</tt> を返す。
	 * @return	<code>MExecArgsModel</code> オブジェクト。
	 * 			設定されていない場合は <tt>null</tt>
	 */
	public MExecArgsModel getModel() {
		return _model;
	}

	/**
	 * このコンポーネントにモデルを設定する。
	 * @param newModel	新しい <code>MExecArgsModel</code> オブジェクト
	 */
	public void setModel(MExecArgsModel newModel) {
		if (newModel == _model) {
			return;		// no changes
		}
		
		this._model = newModel;
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
		if (_model == null)
			throw new ArrayIndexOutOfBoundsException(index);
		return _model.isItemEditable(index);
	}

	/**
	 * 指定された位置のアイテムについて、編集の可不可を設定する。
	 * @param index	アイテムの位置を示すインデックス
	 * @param editable	編集可能とする場合は <tt>true</tt>、不可能とする場合は <tt>false</tt>
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public void setItemEditable(int index, boolean editable) {
		if (_model == null)
			throw new ArrayIndexOutOfBoundsException(index);
		boolean oldEditable = _model.isItemEditable(index);
		_model.setItemEditable(index, editable);
		if (oldEditable != editable) {
			_listItemPane.get(index).updateValueComponents();
		}
	}

	/**
	 * 指定された位置のアイテムの表示状態を取得する。
	 * @param index	アイテムの位置を示すインデックス
	 * @return	表示されていれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public boolean isItemVisible(int index) {
		if (_model == null)
			throw new ArrayIndexOutOfBoundsException(index);
		return _listItemPane.get(index).isVisible();
	}

	/**
	 * 指定された位置のアイテムの表示状態を設定する。
	 * @param index	アイテムの位置を示すインデックス
	 * @param visible	表示する場合は <tt>true</tt>、非表示とする場合は <tt>false</tt>
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public void setItemVisible(int index, boolean visible) {
		_listItemPane.get(index).setVisible(visible);
	}

	/**
	 * 指定された位置のアイテムにエラーがあるかを判定する。
	 * @param index	アイテムの位置を示すインデックス
	 * @return	エラーがある場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public boolean hasItemError(int index) {
		return _listItemPane.get(index).hasError();
	}

	/**
	 * 指定された位置のアイテムのエラーメッセージを取得する。
	 * @param index	アイテムの位置を示すインデックス
	 * @return	エラーメッセージを返す。エラーがない場合は <tt>null</tt> を返す。
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public String getItemError(int index) {
		MExecArgItemPane item = _listItemPane.get(index);
		return formatItemErrorWithLocation(item);
	}

	/**
	 * 指定された位置のアイテムのエラーをクリアする。
	 * @param index	アイテムの位置を示すインデックス
	 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
	 */
	public void clearItemError(int index) {
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
		_listItemPane.get(index).setError(errmsg);
	}

	/**
	 * エラーのあるアイテムが存在するかを判定する。
	 * @return	エラーのあるアイテムが存在する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean hasErrorItems() {
		boolean hasError = false;
		for (MExecArgItemPane item : _listItemPane) {
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
		for (MExecArgItemPane item : _listItemPane) {
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
		for (MExecArgItemPane item : _listItemPane) {
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
		MExecArgItemPane topErrorItem = null;
		for (MExecArgItemPane item : _listItemPane) {
			if (item.isVisible() && item.getItem().isEditable()) {
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
			MExecArgItemPane item = _listItemPane.get(index);
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
		int len = Math.min(history.size(), _listItemPane.size());
		boolean modified = false;
		for (int index = 0; index < len; index++) {
			ModuleArgData hdata = history.get(index);
			MExecArgItemPane item = _listItemPane.get(index);
			if (item.setHistoryData(hdata)) {
				modified = true;
			}
		}
		return modified;
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
	
//	protected void onSelectedItemOptionCheckBox(MExecArgItemPane source) {
//		
//	}
//
//	protected void onButtonItemEdit(MExecArgItemPane source) {
//		
//	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String formatItemErrorWithLocation(MExecArgItemPane item) {
		if (item == null || !item.hasError())
			return null;
		
		return String.format("%s %s", item.getArgTypeLabelText(), item.getError());
	}
	
	protected void setupItemComponents() {
		// 古いコンポーネントを破棄
		this._listItemPane.clear();
		this.removeAll();

		// 新しいアイテム数を判定
		int num = _model.getNumItems();
		if (num <= 0) {
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
			MExecArgItemPane itemPane = createItemPane();
			itemPane.setItemIndex(i);
			this._listItemPane.add(itemPane);
			this.add(itemPane, gbc);
			gbc.gridy++;
		}
		// 下部余白生成のためのダミーコンポーネント
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0,0,0,0);
		gbc.weighty = 1;
		this.add(new JLabel(), gbc);
	}
	
	protected MExecArgItemPane createItemPane() {
		return new MExecArgItemPane();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class MExecArgItemFocusListener implements FocusListener
	{
		public void focusGained(FocusEvent fe) {
			Rectangle rc = fe.getComponent().getBounds();
			Rectangle rcParent = SwingUtilities.convertRectangle(fe.getComponent(), rc, MExecArgsEditPane.this);
			//AppLogger.debug("MExecArgsEditPane.MExecArgItemFocusListener#focusGaind() : rc=" + rc.toString() + " / rcParent=" + rcParent.toString() + " / component=" + fe.getComponent().toString());
			
			scrollRectToVisible(rcParent);
		}

		public void focusLost(FocusEvent fe) {}
	}
	
	/**
	 * モジュール実行時引数の１引数設定ペイン。
	 * 
	 * @version 1.10	2011/02/14
	 */
	protected class MExecArgItemPane extends JPanel implements DocumentListener
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------

		private static final long serialVersionUID = 1L;

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		/** 表示対象ノードのインデックス **/
		private int	_itemindex;
		/** 値のエラーとして表示する文字列 **/
		private String	_errmsg;

		/** 引数属性表示ラベル **/
		private JLabel				_lblArgType;
		/** 引数説明表示ラベル **/
		private JMultilineLabel	_lblArgDesc;

		/** オプションチェックボックス **/
		private JCheckBox	_chkOption;
		/** 閲覧ファイル選択ボタン **/
		private JButton	_btnChooseViewingFile;
		/** テンポラリファイル出力 **/
		private JCheckBox	_chkOutToTemp;
		/** 編集ボタン **/
		private JButton	_btnEdit;
		/** 値編集コンポーネント **/
		private JTextArea	_edtValue;
		/** 値表示ラベル **/
		private JStaticMultilineTextPane	_stcValue;

		/** 値編集用テキストボックスのデフォルト背景色 **/
		private Color	_defStcValueBackground;
		/** 値表示用ラベルのデフォルト背景色 **/
		private Color	_defEdtValueBackground;
		
		private boolean	_ignoreChkOptionChangeEvent = false;
		private boolean	_ignoreChkOutToTempChangeEvent = false;
		private boolean	_saveOptionForOutToTempState = false;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		protected class ItemFocusTraversalPolicy extends FocusTraversalPolicy
		{
			@Override
			public Component getComponentAfter(Container container, Component component) {
				AppLogger.debug("MExecArgsEditPane.MExecArgItemPane.ItemFocusTraversalPolicy#getComponentAfter()");
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
				AppLogger.debug("MExecArgsEditPane.MExecArgItemPane.ItemFocusTraversalPolicy#getComponentAfter() : nextIndex=" + nextIndex);
				if (nextIndex < 0) {
					return null;
				} else {
					return _listItemPane.get(nextIndex);
				}
			}

			@Override
			public Component getComponentBefore(Container container, Component component) {
				AppLogger.debug("MExecArgsEditPane.MExecArgItemPane.ItemFocusTraversalPolicy#getComponentBefore()");
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
				AppLogger.debug("MExecArgsEditPane.MExecArgItemPane.ItemFocusTraversalPolicy#getComponentBefore() : prevIndex=" + prevIndex);
				if (prevIndex < 0) {
					return null;
				} else {
					return _listItemPane.get(prevIndex);
				}
			}

			@Override
			public Component getDefaultComponent(Container container) {
				AppLogger.debug("MExecArgsEditPane.MExecArgItemPane.ItemFocusTraversalPolicy#getDefaultComponent()");
				if (_listItemPane.isEmpty()) {
					return null;
				} else {
					return _listItemPane.get(0);
				}
			}

			@Override
			public Component getFirstComponent(Container container) {
				AppLogger.debug("MExecArgsEditPane.MExecArgItemPane.ItemFocusTraversalPolicy#getFirstComponent()");
				if (_listItemPane.isEmpty()) {
					return null;
				} else {
					return _listItemPane.get(0);
				}
			}

			@Override
			public Component getLastComponent(Container container) {
				AppLogger.debug("MExecArgsEditPane.MExecArgItemPane.ItemFocusTraversalPolicy#getLastComponent()");
				if (_listItemPane.isEmpty()) {
					return null;
				} else {
					return _listItemPane.get(_listItemPane.size()-1);
				}
			}
		};
		
		public MExecArgItemPane() {
			super(new GridBagLayout());
			//setFocusTraversalPolicyProvider(true);
			MExecArgItemPane.this._itemindex = -1;
			
			// create components
			MExecArgItemPane.this._lblArgType = createArgTypeLabel();
			MExecArgItemPane.this._lblArgDesc = createDescriptionLabel();
			MExecArgItemPane.this._chkOption  = createOptionCheckBox();
			MExecArgItemPane.this._chkOutToTemp = createOutToTempCheckBox();
			MExecArgItemPane.this._btnChooseViewingFile = createChooseViewingFileButton();
			MExecArgItemPane.this._btnEdit    = createEditButton();
			MExecArgItemPane.this._stcValue   = createStaticValueComponent();
			MExecArgItemPane.this._edtValue   = createEditValueComponent();
			MExecArgItemPane.this._defStcValueBackground = _stcValue.getBackground();
			MExecArgItemPane.this._defEdtValueBackground = _edtValue.getBackground();
			
			// layout
			initialLayout();

			// setup actions
			//--- document listener
			_edtValue.getDocument().addDocumentListener(this);
			//--- drop target
			DataFileDropTargetListener hDrop = new DataFileDropTargetListener();
			new DropTarget(MExecArgItemPane.this._stcValue, DnDConstants.ACTION_COPY, hDrop, true);
			new DropTarget(MExecArgItemPane.this._edtValue, DnDConstants.ACTION_COPY, hDrop, true);
			//--- focus listener
			MExecArgItemPane.this._lblArgType.setFocusable(false);
			MExecArgItemPane.this._lblArgDesc.setFocusable(false);
			MExecArgItemPane.this._stcValue.setFocusable(false);
			MExecArgItemPane.this._lblArgType.addFocusListener(_hItemFocus);
			MExecArgItemPane.this._lblArgDesc.addFocusListener(_hItemFocus);
			MExecArgItemPane.this._chkOption.addFocusListener(_hItemFocus);
			MExecArgItemPane.this._chkOutToTemp.addFocusListener(_hItemFocus);
			MExecArgItemPane.this._btnChooseViewingFile.addFocusListener(_hItemFocus);
			MExecArgItemPane.this._btnEdit.addFocusListener(_hItemFocus);
			MExecArgItemPane.this._stcValue.addFocusListener(_hItemFocus);
			MExecArgItemPane.this._edtValue.addFocusListener(_hItemFocus);
			
			//setFocusTraversalPolicy(new ItemFocusTraversalPolicy());
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------

		/**
		 * このコンポーネントに表示するデータを示すデータのインデックスを取得する。
		 * @return	<code>MExecArgsModel</code> のアイテムインデックス
		 */
		public int getItemIndex() {
			return _itemindex;
		}

		/**
		 * このコンポーネントの表示対象となるデータを取得する。
		 * @return	<code>MExecArgItemModel</code> オブジェクト
		 */
		public MExecArgItemModel getItem() {
			return getModel().getItem(_itemindex);
		}

		/**
		 * 引数種別のラベルに表示している文字列を取得する。
		 * @return	ラベルの文字列
		 */
		public String getArgTypeLabelText() {
			return _lblArgType.getText();
		}

		/**
		 * 引数説明のラベルに表示している文字列を取得する。
		 * @return	ラベルの文字列
		 */
		public String getArgDescriptionLabelText() {
			return _lblArgDesc.getText();
		}

		/**
		 * このコンポーネントの表示対象とするデータを示すインデックスを設定する。
		 * @param newIndex	アイテムのインデックス
		 * @throws ArrayIndexOutOfBoundsException	インデックスが無効な場合
		 */
		public void setItemIndex(int newIndex) {
			if (_itemindex == newIndex) {
				return;		// no changes
			}
			_itemindex = newIndex;
			MExecArgItemModel item = getModel().getItem(newIndex);
			
			// update display
			//clearErrorDisplay();
			redisplayArgType(isVisibleArgNo(), newIndex, item.getType());
			redisplayArgDescription(item.getDescription());
			redisplayArgValue(item);
			redisplayErrorState();
		}

		/**
		 * このコンポーネントにエラーメッセージが設定されているかを判定する。
		 * @return	エラーメッセージが設定されている場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
		 */
		public boolean hasError() {
			return (_errmsg != null);
		}

		/**
		 * このコンポーネントに設定されているエラーメッセージを取得する。
		 * @return	設定されているエラーメッセージを返す。設定されていない場合は <tt>null</tt> を返す。
		 */
		public String getError() {
			return _errmsg;
		}

		/**
		 * このコンポーネントからエラーメッセージを除去する。
		 * このメソッド呼び出しの直後、{@link #hasError()} は <tt>false</tt> を返す。
		 */
		public void clearError() {
			if (_errmsg != null) {
				clearErrorDisplay();
				_errmsg = null;
			}
		}

		/**
		 * このコンポーネントに新しいエラーメッセージを設定する。
		 * <em>errmsg</em> が <tt>null</tt> もしくは空文字列の場合は、
		 * {@link #clearError()} を呼び出したのと同じ結果となる。
		 * @param errmsg	設定するエラーメッセージ
		 */
		public void setError(String errmsg) {
			// 指定されたメッセージが無効なら、エラーをクリア
			if (Strings.isNullOrEmpty(errmsg)) {
				clearError();
			}
			
			// 新しいメッセージを設定
			String olderr = this._errmsg;
			this._errmsg = errmsg;
			if (!errmsg.equals(olderr)) {
				setErrorDisplay(errmsg);
			}
		}

		/**
		 * 引数番号と引数属性の表示を更新する。
		 */
		public void updateArgNo() {
			redisplayArgType(isVisibleArgNo(), getItemIndex(), getItem().getType());
		}

		/**
		 * 引数値に関するすべてのコンポーネントの表示を更新する。
		 */
		public void updateValueComponents() {
			redisplayArgValue(getItem());
		}

		/**
		 * 値の正当性を検証する。
		 * このメソッドは、編集の可不可に関わらず値を検証し、エラーステートを更新する。
		 * @return	値が正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
		 */
		public boolean verifyValue() {
			MExecArgItemModel item = getItem();
			switch (item.getType()) {
				case IN :
					return verifyInputFileValue(item);
				case OUT :
					return verifyOutputFileValue(item);
				case PUB :
					return verifyPublishValue(item);
				case SUB :
					return verifySubscribeValue(item);
				default :
					return verifyStringValue(item);
			}
		}

		/**
		 * このコンポーネントに履歴として保存された引数の値を設定する。
		 * この履歴引数がこのコンポーネントの引数属性と異なる場合や、
		 * 値の変更が許可されていない場合、値の型が合わない場合、
		 * 値が <tt>null</tt> の場合、引数の値は設定されない。
		 * @param hdata	履歴引数
		 * @return	履歴引数によりこのコンポーネントの値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 * @throws NullPointerException	引数が <tt>null</tt> の場合
		 * @since 1.20
		 */
		public boolean setHistoryData(ModuleArgData hdata) {
			Object hvalue = hdata.getValue();
			if (hvalue == null)
				return false;
			
			MExecArgItemModel item = getItem();
			if (item.isEditable() && item.getType()==hdata.getType()) {
				switch (item.getType()) {
					case IN :
						return setInputHistoryData(hdata.getValue());
					case OUT :
						return setOutputHistoryData(hdata.getValue());
					case STR :
						return setStringHistoryData(hdata.getValue());
					case PUB :
						return setPublishHistoryData(hdata.getValue());
					case SUB :
						return setSubscribeHistoryData(hdata.getValue());
				}
			}
			
			// not set
			return false;
		}

		protected boolean setInputHistoryData(Object histValue) {
			VirtualFile vfFile = null;
			if (histValue instanceof VirtualFile) {
				vfFile = ((VirtualFile)histValue).getAbsoluteFile().getNormalizedFile();
			}
			else if (histValue instanceof File) {
				vfFile = ModuleFileManager.fromJavaFile((File)histValue).getAbsoluteFile().getNormalizedFile();
			}
			
			if (vfFile != null) {
				getItem().setValue(vfFile);
				_stcValue.setText(formatValue(vfFile));
				verifyValue();
				return true;
			}
			else {
				return false;
			}
		}
		
		protected boolean setOutputHistoryData(Object histValue) {
			VirtualFile vfFile = null;
			if (histValue instanceof MExecArgTempFile) {
				// テンポラリファイル出力
				if (_chkOutToTemp.isVisible() && _chkOutToTemp.isEnabled()) {
					_saveOptionForOutToTempState = false;
					getItem().setOutToTempEnabled(true);
					getItem().setOptionEnabled(true);
					getItem().setTempFilePrefix(null);
					redisplayOutputValue(getItem());
					updateOutputControlStates(true, getItem().isOptionEnabled());
//					_chkOutToTemp.setSelected(true);
//					_stcValue.setText("");
//					getItem().setTempFilePrefix("");
					verifyOutputFileValue(getItem());
					return true;
				} else {
					return false;
				}
			}
			else if (histValue instanceof String) {
				// プレフィックス指定のテンポラリファイル出力
				if (_chkOutToTemp.isVisible() && _chkOutToTemp.isEnabled()) {
					_saveOptionForOutToTempState = false;
					getItem().setOutToTempEnabled(true);
					getItem().setOptionEnabled(true);
					getItem().setTempFilePrefix(histValue.toString());
					redisplayOutputValue(getItem());
					updateOutputControlStates(true, getItem().isOptionEnabled());
//					_chkOutToTemp.setSelected(true);
//					String strValue = histValue.toString();
//					_stcValue.setText(strValue);
//					getItem().setTempFilePrefix(strValue);
					verifyOutputFileValue(getItem());
					return true;
				} else {
					return false;
				}
			}
			else if (histValue instanceof VirtualFile) {
				vfFile = ((VirtualFile)histValue).getAbsoluteFile().getNormalizedFile();
			}
			else if (histValue instanceof File) {
				vfFile = ModuleFileManager.fromJavaFile((File)histValue).getAbsoluteFile().getNormalizedFile();
			}
			
			if (vfFile != null) {
				getItem().setValue(vfFile);
				getItem().setOutToTempEnabled(false);
				getItem().setTempFilePrefix(null);
				redisplayOutputValue(getItem());
				updateOutputControlStates(false, getItem().isOptionEnabled());
//				_stcValue.setText(formatValue(vfFile));
				verifyValue();
				return true;
			}
			else {
				return false;
			}
		}

		/**
		 * 履歴の値を文字列として入力コンポーネントにセットする。
		 * @param histValue	履歴の値
		 * @return	入力コンポーネントの値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 */
		protected boolean setStringHistoryData(Object histValue) {
			String strValue = histValue.toString();
			getItem().setValue(strValue);
			_edtValue.setText(formatValue(strValue));
			verifyStringValue(getItem());
			return true;
		}

		/**
		 * 履歴の値をパブリッシュパラメータ(文字列)として入力コンポーネントにセットする。
		 * @param histValue	履歴の値
		 * @return	入力コンポーネントの値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 * @since 3.1.0
		 */
		protected boolean setPublishHistoryData(Object histValue) {
			String strValue = histValue.toString();
			getItem().setValue(strValue);
			_edtValue.setText(formatValue(strValue));
			verifyPublishValue(getItem());
			return true;
		}
		
		/**
		 * 履歴の値をサブスクライブパラメータ(文字列)として入力コンポーネントにセットする。
		 * @param histValue	履歴の値
		 * @return	入力コンポーネントの値が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
		 * @since 3.1.0
		 */
		protected boolean setSubscribeHistoryData(Object histValue) {
			String strValue = histValue.toString();
			getItem().setValue(strValue);
			_edtValue.setText(formatValue(strValue));
			verifySubscribeValue(getItem());
			return true;
		}

		//------------------------------------------------------------
		// Implement javax.swing.event.DocumentListener interfaces
		//------------------------------------------------------------
		
		public void changedUpdate(DocumentEvent de) {
			onChangeStringValue(de);
		}

		public void insertUpdate(DocumentEvent de) {
			onChangeStringValue(de);
		}

		public void removeUpdate(DocumentEvent de) {
			onChangeStringValue(de);
		}

		//------------------------------------------------------------
		// Event handler
		//------------------------------------------------------------
		
		protected void onChangeStringValue(DocumentEvent e) {
			String strValue;
			try {
				strValue = e.getDocument().getText(0, e.getDocument().getLength());
			} catch (BadLocationException ignoreEx) {
				strValue = "";
			}
			MExecArgItemModel item = getModel().getItem(_itemindex);
			if (ModuleArgType.STR == item.getType()) {
				item.setValue(strValue);
				verifyStringValue(item);
			}
			else if (ModuleArgType.PUB == item.getType()) {
				item.setValue(strValue);
				verifyPublishValue(item);
			}
			else if (ModuleArgType.SUB == item.getType()) {
				item.setValue(strValue);
				verifySubscribeValue(item);
			}
			else if (ModuleArgType.OUT == item.getType()) {
				IMExecArgParam param = item.getParameter();
				if (param instanceof MExecArgCsvFile) {
					item.setTempFilePrefix(strValue);
					verifyOutputFileValue(item);
				}
			}
		}
		
		protected void onSelectedCheckBox() {
			if (_ignoreChkOptionChangeEvent)
				return;
			
			MExecArgItemModel item = getModel().getItem(_itemindex);
			boolean selected = _chkOption.isSelected();
			boolean oldSelected = item.isOptionEnabled();
			if (selected != oldSelected) {
				item.setOptionEnabled(selected);
				if (ModuleArgType.IN == item.getType()) {
					if (selected) {
						_stcValue.setText("");
						_btnEdit.setEnabled(false);
					} else {
						_stcValue.setText(formatValue(item.getValue()));
						_btnEdit.setEnabled(true);
					}
					verifyValue();
				}
				else if (ModuleArgType.OUT == item.getType() && item.isOutToTempEnabled()) {
					_saveOptionForOutToTempState = selected;
				}
			}
		}
		
		protected void onSelectedChooseViewingFile() {
			VirtualFile vfFile = getViewingFileOnEditor();
			if (vfFile == null)
				return;		// no viewing file
			
			MExecArgItemModel item = getModel().getItem(_itemindex);
			if (!vfFile.equals(item.getValue())) {
				item.setValue(vfFile);
				_stcValue.setText(formatValue(vfFile));
				verifyValue();
			}
		}
		
		protected void onSelectedOutToTempCheckBox() {
			if (_ignoreChkOutToTempChangeEvent)
				return;
			
			MExecArgItemModel item = getModel().getItem(_itemindex);
			if (!_chkOutToTemp.isVisible())
				return;		// ignore event
			if (ModuleArgType.OUT != item.getType())
				return;		// ignore event
			
			boolean outToTemp = _chkOutToTemp.isSelected();
			boolean oldOutToTemp = item.isOutToTempEnabled();
			if (outToTemp == oldOutToTemp)
				return;		// ignore event
			if (outToTemp) {
				item.setOutToTempEnabled(true);
				_saveOptionForOutToTempState = item.isOptionEnabled();
				item.setOptionEnabled(true);
			} else {
				item.setOutToTempEnabled(false);
				item.setOptionEnabled(_saveOptionForOutToTempState);
				_saveOptionForOutToTempState = false;
			}
			updateOutputControlStates(outToTemp, item.isOptionEnabled());
			verifyValue();
		}
		
		protected void updateOutputControlStates(boolean outToTemp, boolean itemOptionEnabled) {
			if (outToTemp) {
				_btnEdit.setEnabled(false);
				//_chkOption.setEnabled(false);	// テンポラリ出力でも閲覧設定を変更可能なように変更
				_stcValue.setVisible(false);
				_edtValue.setVisible(true);
			} else {
				_btnEdit.setEnabled(true);
				_edtValue.setVisible(false);
				_stcValue.setVisible(true);
				//_chkOption.setEnabled(true);
			}
			_ignoreChkOptionChangeEvent = true;
			_chkOption.setSelected(itemOptionEnabled);
			_ignoreChkOptionChangeEvent = false;
		}

		protected void onButtonEdit() {
			MExecArgItemModel item = getModel().getItem(_itemindex);
			IMExecArgParam param = item.getParameter();
			File selFile = null;
			RunnerFrame frame = (RunnerFrame)ModuleRunner.getInstance().getMainFrame();
			File orgRecFile = FileChooserManager.getRecommendedDirectory();
			FileChooserManager.setRecommendedDirectory(frame.getDataFileUserRootDirectory());
			if (param instanceof MExecArgCsvFile) {
				selFile = FileChooserManager.chooseArgCsvFile(this, FileChooserManager.getInitialDocumentFile(item.getValue()));
			}
			else if (param instanceof MExecArgTextFile) {
				selFile = FileChooserManager.chooseArgTextFile(this, FileChooserManager.getInitialDocumentFile(item.getValue()));
			}
			else if (param instanceof MExecArgXmlFile) {
				selFile = FileChooserManager.chooseArgXmlFile(this, FileChooserManager.getInitialDocumentFile(item.getValue()));
			}
			else if (param instanceof MExecArgDirectory) {
				selFile = FileChooserManager.chooseArgDirectory(this, FileChooserManager.getInitialDocumentFile(item.getValue()));
			}
			else {
				selFile = FileChooserManager.chooseArgumentFile(this, FileChooserManager.getInitialDocumentFile(item.getValue()));
			}
			FileChooserManager.setRecommendedDirectory(orgRecFile);
			if (selFile != null) {
				FileChooserManager.setLastChooseDocumentFile(selFile);
				VirtualFile vfFile = ModuleFileManager.fromJavaFile(selFile);
				if (!vfFile.equals(item.getValue())) {
					item.setValue(vfFile);
					_stcValue.setText(formatValue(vfFile));
					verifyValue();
				}
			}
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------

		/**
		 * このコンポーネントが値を表示するコンポーネントを取得する。
		 * @return	<code>JTextComponent</code> オブジェクト
		 */
		protected JTextComponent getCurrentValueComponent() {
			if (_edtValue.isVisible()) {
				return _edtValue;
			} else {
				return _stcValue;
			}
		}
		
		protected void redisplayErrorState() {
			if (hasError()) {
				setErrorDisplay(getError());
			} else {
				clearErrorDisplay();
			}
		}
		
		protected void clearErrorDisplay() {
			_edtValue.setToolTipText(null);
			_stcValue.setToolTipText(null);
			_edtValue.setBackground(_defEdtValueBackground);
			_stcValue.setBackground(_defStcValueBackground);
		}
		
		protected void setErrorDisplay(String errmsg) {
			JTextComponent cValue = getCurrentValueComponent();
			cValue.setToolTipText(errmsg);
			cValue.setBackground(CommonResources.DEF_BACKCOLOR_ERROR);
		}

		/**
		 * 引数属性のラベルを生成
		 * @return	生成された <code>JLabel</code> オブジェクト
		 */
		protected JLabel createArgTypeLabel() {
			JLabel label = new JLabel();
			label.setOpaque(true);
			label.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
					BorderFactory.createEmptyBorder(1,5,1,5)));
			return label;
		}

		/**
		 * 引数説明のラベルを生成
		 * @return	生成された <code>JMultilineLabel</code> オブジェクト
		 */
		protected JMultilineLabel createDescriptionLabel() {
			JMultilineLabel label = new JMultilineLabel();
			return label;
		}

		/**
		 * 引数設定オプションのチェックボックスを生成
		 * @return	生成された <code>JCheckBox</code> オブジェクト
		 */
		protected JCheckBox createOptionCheckBox() {
			JCheckBox chk = new JCheckBox();
			chk.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					onSelectedCheckBox();
				}
			});
			return chk;
		}
		
		protected JButton createChooseViewingFileButton() {
			JButton btn = new JButton(RunnerMessages.getInstance().MExecDefEditDlg_Arg_UseViewingFile);
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onSelectedChooseViewingFile();
				}
			});
			return btn;
		}
		
		protected JCheckBox createOutToTempCheckBox() {
			JCheckBox chk = new JCheckBox();
			chk.setText(RunnerMessages.getInstance().MExecDefEditDlg_Arg_OutToTemp);
			chk.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					onSelectedOutToTempCheckBox();
				}
			});
			return chk;
		}

		/**
		 * 編集ボタンを生成
		 * @return	生成された <code>JButton</code> オブジェクト
		 */
		protected JButton createEditButton() {
			JButton btn = CommonResources.createBrowseButton(CommonMessages.getInstance().Button_Browse);
			btn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					onButtonEdit();
				}
			});
			return btn;
		}

		/**
		 * 値編集用コンポーネントを生成
		 * @return	生成された <code>JTextArea</code> オブジェクト
		 */
		protected JTextArea createEditValueComponent() {
			JTextArea ta = new JTextArea();
			ta.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
			ta.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
			ta.setLineWrap(true);
			ta.setWrapStyleWord(false);
			ta.setEditable(true);
			ta.setBorder(UIManager.getBorder("TextField.border"));
			return ta;
		}

		/**
		 * 編集不可能な引数値表示コンポーネントを生成
		 * @return	生成された <code>JStaticMultilineTextPane</code> オブジェクト
		 */
		protected JStaticMultilineTextPane createStaticValueComponent() {
			return new JStaticMultilineTextPane();
		}


		/**
		 * コンポーネントを配置する。
		 */
		protected void initialLayout() {
			Insets typeInsets = new Insets(0, 0, 3, 0);
			Insets descInsets = new Insets(0, 3, 3, 0);
			Insets valueInsets = new Insets(0, 20, 0, 0);
			Insets rightInsets = new Insets(0, 3, 0, 0);
			JPanel valuePanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weighty = 0;
			//--- check box
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			valuePanel.add(_chkOption, gbc);
			valuePanel.add(_btnChooseViewingFile);
			valuePanel.add(new JLabel(" "), gbc);
			//--- out to temp
			gbc.gridy=1;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			valuePanel.add(_chkOutToTemp, gbc);
			gbc.gridy=0;
			gbc.gridheight = 2;
			gbc.weighty = 1;
			gbc.gridx++;
			gbc.insets = rightInsets;
			//--- value component
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			valuePanel.add(_stcValue, gbc);
			valuePanel.add(_edtValue, gbc);
			gbc.gridx++;
			gbc.insets = rightInsets;
			//--- button
			gbc.anchor = GridBagConstraints.NORTHEAST;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			valuePanel.add(_btnEdit, gbc);
			gbc.gridx++;
			
			// layout main panel
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weighty = 0;
			gbc.anchor = GridBagConstraints.NORTHEAST;
			//--- attr
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = typeInsets;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.VERTICAL;
			this.add(_lblArgType, gbc);
			//--- desc
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.insets = descInsets;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			this.add(_lblArgDesc, gbc);
			//--- value
			gbc.gridwidth = 2;
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.insets = valueInsets;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			this.add(valuePanel, gbc);
		}

		/**
		 * 指定されたパラメータで、引数属性のラベルの表示を更新する。
		 * 引数番号を表示する場合、<em>itemIndex</em> + 1 の値を引数番号とする。
		 * @param visibleArgNo	引数番号を表示する場合は <tt>true</tt>、表示しない場合は <tt>false</tt>
		 * @param itemIndex		引数設定のインデックス
		 * @param argtype		引数属性
		 */
		protected void redisplayArgType(boolean visibleArgNo, int itemIndex, ModuleArgType argtype) {
			// 表示文字列
			String strLabel;
			if (visibleArgNo) {
				// 引数番号を表示
				strLabel = String.format("($%d) %s", (itemIndex+1), argtype.typeName());
			} else {
				// 引数番号は非表示
				strLabel = argtype.typeName();
			}
			
			// 背景色
			JLabel label = _lblArgType;
			switch (argtype) {
				case IN :
					label.setBackground(CommonResources.DEF_BACKCOLOR_ARG_IN);
					break;
				case OUT :
					label.setBackground(CommonResources.DEF_BACKCOLOR_ARG_OUT);
					break;
				case STR :
					label.setBackground(CommonResources.DEF_BACKCOLOR_ARG_STR);
					break;
				case PUB :
					label.setBackground(CommonResources.DEF_BACKCOLOR_ARG_PUB);
					break;
				case SUB :
					label.setBackground(CommonResources.DEF_BACKCOLOR_ARG_SUB);
					break;
				default :
					// 標準の背景色
					label.setBackground(UIManager.getColor("Label.background"));
			}
			// 文字列設定
			label.setText(strLabel);
		}

		/**
		 * 指定された文字列で、引数説明の表示を更新する。
		 * @param desc	引数の説明
		 */
		protected void redisplayArgDescription(String desc) {
			_lblArgDesc.setText(desc==null ? "" : desc);
		}

		/**
		 * 指定された引数設定で、引数値の表示を更新する。
		 * @param item	引数設定
		 */
		protected void redisplayArgValue(MExecArgItemModel item) {
			switch (item.getType()) {
				case IN :
					redisplayInputValue(item);
					break;
				case OUT :
					redisplayOutputValue(item);
					break;
				case STR :	// [STR]
				case PUB :	// [PUB] もコンポーネント制御は [STR] と同じ
				case SUB :	// [SUB] もコンポーネント制御は [STR] と同じ
					redisplayStringValue(item);
					break;
				default :
					throw new AssertionError("Not reached!");
			}
		}

		/**
		 * 指定された引数設定で、[IN] 属性の引数値の表示を更新する。
		 * @param item	引数設定
		 */
		protected void redisplayInputValue(MExecArgItemModel item) {
			IMExecArgParam param = item.getParameter();
			Object value = item.getValue();
			_chkOutToTemp.setVisible(false);
			_edtValue.setVisible(false);
			_stcValue.setVisible(true);
			if (param instanceof MExecArgTempFile) {
				_chkOption.setVisible(false);
				_btnChooseViewingFile.setVisible(false);
				_btnChooseViewingFile.setEnabled(false);
				_btnEdit.setVisible(false);
				if (value == null) {
					_stcValue.setText(RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarTemporary);
				} else {
					_stcValue.setText(RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarTemporary + " (" + value.toString() + ")");
				}
			}
			else if (param instanceof MExecArgCsvFile) {
				_chkOption.setText(RunnerMessages.getInstance().MExecDefEditDlg_Arg_UseViewingFile);
				_chkOption.setVisible(false);
				_chkOption.setSelected(item.isOptionEnabled());
				if (isEditable() && item.isEditable()) {
					_btnEdit.setVisible(true);
					_chkOption.setEnabled(_model.canUseActiveDocument(param));
					_btnChooseViewingFile.setVisible(true);
					_btnChooseViewingFile.setEnabled(getViewingFileOnEditor()!=null);
					_btnEdit.setEnabled(!item.isOptionEnabled());
				} else {
					_btnChooseViewingFile.setVisible(false);
					_btnEdit.setVisible(false);
					_chkOption.setEnabled(false);
				}
				_stcValue.setText(formatValue(value));
			}
			else {
				_btnChooseViewingFile.setVisible(false);
				_btnChooseViewingFile.setEnabled(false);
				_chkOption.setVisible(false);
				if (isEditable() && item.isEditable()) {
					_btnEdit.setVisible(true);
					_btnEdit.setEnabled(true);
				} else {
					_btnEdit.setVisible(false);
				}
				_stcValue.setText(formatValue(value));
			}
		}
		
		/**
		 * 指定された引数設定で、[OUT] 属性の引数値の表示を更新する。
		 * @param item	引数設定
		 */
		protected void redisplayOutputValue(MExecArgItemModel item) {
			IMExecArgParam param = item.getParameter();
			Object value = item.getValue();
			_btnChooseViewingFile.setVisible(false);
			_btnChooseViewingFile.setEnabled(false);
			_edtValue.setVisible(false);
			_stcValue.setVisible(true);
			if (param instanceof MExecArgTempFile) {
				_chkOption.setVisible(false);
				_chkOutToTemp.setVisible(false);
				_btnEdit.setVisible(false);
				if (value == null) {
					_stcValue.setText(RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarTemporary);
				} else {
					_stcValue.setText(RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileVarTemporary + " (" + value.toString() + ")");
				}
			}
			else if (param instanceof MExecArgCsvFile) {
				_chkOption.setText(RunnerMessages.getInstance().MExecDefEditDlg_Arg_ShowResultCsv);
				_chkOption.setVisible(true);
				_chkOption.setEnabled(true);	// テンポラリ出力でも閲覧しない設定が可能となるよう変更
				_chkOutToTemp.setVisible(true);
				_ignoreChkOptionChangeEvent = true;
				_ignoreChkOutToTempChangeEvent = true;
				_chkOption.setSelected(item.isOptionEnabled());
				_chkOutToTemp.setSelected(item.isOutToTempEnabled());
				_ignoreChkOutToTempChangeEvent = false;
				_ignoreChkOptionChangeEvent = false;
				if (isEditable()) {
					if (item.isOutToTempEnabled()) {
						if (!item.isEditable()) {
							_chkOutToTemp.setEnabled(false);
							_btnEdit.setVisible(false);
							_edtValue.setEnabled(false);
						}
					} else {
						if (item.isEditable()) {
							_btnEdit.setVisible(true);
							_btnEdit.setEnabled(true);
						} else {
							_btnEdit.setVisible(false);
							_chkOutToTemp.setEnabled(false);
						}
					}
				}
				else {
					_chkOutToTemp.setEnabled(false);
					_btnEdit.setVisible(false);
				}
				_edtValue.setText(item.getTempFilePrefix());
				_stcValue.setText(formatValue(value));
			}
			else {
				_chkOption.setVisible(false);
				_chkOutToTemp.setVisible(false);
				if (isEditable() && item.isEditable()) {
					_btnEdit.setVisible(true);
					_btnEdit.setEnabled(true);
				} else {
					_btnEdit.setVisible(false);
				}
				_stcValue.setText(formatValue(value));
			}
		}
		
		/**
		 * 指定された引数設定で、[STR] 属性の引数値の表示を更新する。
		 * @param item	引数設定
		 */
		protected void redisplayStringValue(MExecArgItemModel item) {
			// CheckBox は非表示
			_chkOption.setVisible(false);
			_chkOutToTemp.setVisible(false);
			_btnChooseViewingFile.setVisible(false);
			_btnChooseViewingFile.setEnabled(false);
			
			// 編集ボタンは非表示
			_btnEdit.setVisible(false);
			
			// 表示テキストを設定
			if (isEditable() && item.isEditable()) {
				_stcValue.setVisible(false);
				_edtValue.setVisible(true);
				_edtValue.setText(formatValue(item.getValue()));
			} else {
				_edtValue.setVisible(false);
				_stcValue.setVisible(true);
				_stcValue.setText(formatValue(item.getValue()));
			}
		}

		/**
		 * 引数値として表示するテキストの整形
		 * @param value	表示する値
		 * @return	値の文字列表現
		 */
		protected String formatValue(Object value) {
			if (value instanceof VirtualFile) {
				VirtualFile vfValue = (VirtualFile)value;
				VirtualFile vfBase = getBasePath();
				if (vfBase != null && vfValue.isDescendingFrom(vfBase)) {
					return vfValue.relativePathFrom(vfBase, Files.CommonSeparatorChar);
				} else {
					return vfValue.toString();
				}
			}
			else {
				return (value==null ? "" : value.toString());
			}
		}

		/**
		 * 文字列属性の引数値が正当かを判定する。
		 * 次の条件に一致した場合に正当と判定する。
		 * <ul>
		 * <li>文字列が空ではでない。
		 * </ul>
		 * @param item	判定対象のデータ
		 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
		 */
		protected boolean verifyStringValue(MExecArgItemModel item) {
			if (item.getValue() == null) {
				setError(RunnerMessages.getInstance().msgErrorRequiredString);
				return false;
			} else {
				String strValue = item.getValue().toString();
				if (strValue.length() > 0) {
					clearError();
					return true;
				} else {
					setError(RunnerMessages.getInstance().msgErrorRequiredString);
					return false;
				}
			}
		}
		
		/**
		 * パブリッシュ属性の引数値が正当かを判定する。
		 * 次の条件に一致した場合に正当と判定する。
		 * <ul>
		 * <li>文字列が空ではでない。
		 * </ul>
		 * @param item	判定対象のデータ
		 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
		 * @since 3.1.0
		 */
		protected boolean verifyPublishValue(MExecArgItemModel item) {
			if (item.getValue() == null) {
				setError(RunnerMessages.getInstance().msgErrorRequiredMqttPubAddr);
				return false;
			} else {
				String strValue = item.getValue().toString();
				if (strValue.length() > 0) {
					// TODO:パブリッシュ宛先のフォーマットチェック
					clearError();
					return true;
				} else {
					setError(RunnerMessages.getInstance().msgErrorRequiredMqttPubAddr);
					return false;
				}
			}
		}
		
		/**
		 * サブスクライブ属性の引数値が正当かを判定する。
		 * 次の条件に一致した場合に正当と判定する。
		 * <ul>
		 * <li>文字列が空ではでない。
		 * </ul>
		 * @param item	判定対象のデータ
		 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
		 * @since 3.1.0
		 */
		protected boolean verifySubscribeValue(MExecArgItemModel item) {
			if (item.getValue() == null) {
				setError(RunnerMessages.getInstance().msgErrorRequiredMqttSubAddr);
				return false;
			} else {
				String strValue = item.getValue().toString();
				if (strValue.length() > 0) {
					// TODO:サブスクライブ宛先のフォーマットチェック
					clearError();
					return true;
				} else {
					setError(RunnerMessages.getInstance().msgErrorRequiredMqttSubAddr);
					return false;
				}
			}
		}

		/**
		 * 入力ファイル属性の引数値が正当かを判定する。
		 * 次の条件のどれかに一致した場合に正当と判定する。
		 * <ul>
		 * <li>テンポラリファイル指定である。
		 * <li>閲覧ファイルが指定されている。
		 * <li>ファイルのパスが指定されており、そのファイルが存在している。
		 * <li>
		 * </ul>
		 * @param item	判定対象のデータ
		 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
		 */
		protected boolean verifyInputFileValue(MExecArgItemModel item) {
			IMExecArgParam param = item.getParameter();
			if (param instanceof MExecArgTempFile) {
				// エラーなし
				clearError();
				return true;
			}

			if (item.isOptionEnabled()) {
				// 表示中のファイルを使用するため、エラーなし
				clearError();
				return true;
			}

			Object value = item.getValue();
			if (!(value instanceof VirtualFile) && !(value instanceof File)) {
				setError(RunnerMessages.getInstance().msgErrorRequiredInputFile);
				return false;
			}
			
			boolean exists;
			if (value instanceof VirtualFile) {
				exists = ((VirtualFile)value).exists();
			} else {
				exists = ((File)value).exists();
			}
			if (!exists) {
				setError(RunnerMessages.getInstance().msgErrorInputFileNotFound);
				return false;
			}
			
			// エラーなし
			clearError();
			return true;
		}
		
		/**
		 * 出力ファイル属性の引数値が正当かを判定する。
		 * 次の条件のどれかに一致した場合に正当と判定する。
		 * <ul>
		 * <li>テンポラリファイル指定である。
		 * <li>ファイルのパスが指定されている。
		 * <li>
		 * </ul>
		 * @param item	判定対象のデータ
		 * @return	正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
		 */
		protected boolean verifyOutputFileValue(MExecArgItemModel item) {
			IMExecArgParam param = item.getParameter();
			if (param instanceof MExecArgTempFile) {
				// エラーなし
				clearError();
				return true;
			}

			Object value = item.getValue();
			if (item.isOutToTempEnabled()) {
				String strPrefix = item.getTempFilePrefix();
				if (strPrefix != null && strPrefix.length() < 3) {
					setError(RunnerMessages.getInstance().msgErrorRequiredOver3chars);
					return false;
				}
			}
			else if (!(value instanceof VirtualFile) && !(value instanceof File)) {
				setError(RunnerMessages.getInstance().msgErrorRequiredOutputFile);
				return false;
			}
			
			// エラーなし
			clearError();
			return true;
		}

		/**
		 * アイテムへのファイルのドロップを受け付けるリスナー
		 */
		protected class DataFileDropTargetListener extends DropTargetAdapter
		{
			private boolean _canImport = false;
			
			@Override
			public void dragEnter(DropTargetDragEvent dtde) {
				_canImport = false;
				
				// 編集可能かをチェック
				if (!isEditable() || !getItem().isEditable()) {
					// 編集不可
					dtde.rejectDrag();
					return;
				}
				
				// ファイルを受け付けるアイテムかをチェック
				ModuleArgType argtype = getItem().getType();
				if (argtype!=ModuleArgType.IN && argtype!=ModuleArgType.OUT) {
					// ファイル形式の引数ではない
					dtde.rejectDrag();
					return;
				}
				
				// サポートする DataFlavor のチェック
				if (!dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					// サポートされないデータ形式
					dtde.rejectDrag();
					return;
				}
				
				// ソースアクションにコピーが含まれていない場合は、許可しない
				if ((dtde.getSourceActions() & DnDConstants.ACTION_COPY) == 0) {
					// サポートされないドロップアクション
					dtde.rejectDrag();
					return;
				}
				
				// ドロップアクションをコピー操作に限定
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
				_canImport = true;
			}
			
			public void dragExit(DropTargetEvent dte) {
				_canImport = false;
			}

			public void dragOver(DropTargetDragEvent dtde) {
				if (_canImport) {
					dtde.acceptDrag(DnDConstants.ACTION_COPY);
				} else {
					dtde.rejectDrag();
				}
			}
			
			public void drop(DropTargetDropEvent dtde) {
				// 編集可能かをチェック
				if (!isEditable() || !getItem().isEditable()) {
					// 編集不可
					dtde.rejectDrop();
					return;
				}
				
				// ファイルを受け付けるアイテムかをチェック
				ModuleArgType argtype = getItem().getType();
				if (argtype!=ModuleArgType.IN && argtype!=ModuleArgType.OUT) {
					// ファイル形式の引数ではない
					dtde.rejectDrop();
					return;
				}
				
				// ソースアクションにコピーが含まれていない場合は、許可しない
				if ((dtde.getSourceActions() & DnDConstants.ACTION_COPY) == 0) {
					// サポートされないドロップアクション
					dtde.rejectDrop();
					return;
				}
				
				// データソースの取得
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				Transferable t = dtde.getTransferable();
				if (t == null) {
					dtde.rejectDrop();
					return;
				}
				
				// ファイルを取得する
				VirtualFile vfTarget = getTargetFile(t);
				if (vfTarget != null) {
					if (!vfTarget.equals(getItem().getValue())) {
						getItem().setValue(vfTarget);
						_stcValue.setText(formatValue(vfTarget));
						verifyValue();
					}
					//--- ドロップ完了
					dtde.dropComplete(true);
				}
				
				// drop を受け付けない
				dtde.rejectDrop();
			}
			
			protected VirtualFile getTargetFile(Transferable transfer) {
				// VirtualFile
				{
					VirtualFile vf = getTargetVirtualFile(transfer);
					if (vf != null) {
						return vf;
					}
				}
				
				// java File
				{
					File f = getTargetJavaFile(transfer);
					if (f != null) {
						return ModuleFileManager.fromJavaFile(f);
					}
				}
				
				// not supported data
				return null;
			}
			
			protected VirtualFile getTargetVirtualFile(Transferable transfer) {
				if (!transfer.isDataFlavorSupported(VirtualFileTransferable.virtualFileListFlavor))
					return null;
				
				VirtualFile vfTarget = null;
				try {
					// 先頭のファイルのみ受け入れる
					//--- 設定ファイル(*.prefs) を除くパスのみを取得
					VirtualFile[] filelist = (VirtualFile[])transfer.getTransferData(VirtualFileTransferable.virtualFileListFlavor);
					if (filelist != null) {
						for (VirtualFile f : filelist) {
							if (!Strings.endsWithIgnoreCase(f.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
								vfTarget = f;
								break;
							}
						}
					}
				}
				catch (Throwable ex) {
					// ignore exception
					//AppLogger.error("MExecArgsEditPane.MExecArgItemPane.DataFileDropTarget#getTargetVirtualFile() : Failed to drop to editor.", ex);
				}
				
				return vfTarget;
			}
			
			protected File getTargetJavaFile(Transferable transfer) {
				if (!transfer.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
					return null;

				File fTarget = null;
				try {
					// 先頭のファイルのみ受け入れる
					//--- 設定ファイル(*.prefs) を除くパスのみを取得
					File f;
					List<?> filelist = (List<?>)transfer.getTransferData(DataFlavor.javaFileListFlavor);
					for (Object elem : filelist) {
						if (elem instanceof File) {
							f = (File)elem;
							if (!Strings.endsWithIgnoreCase(f.getName(), ModuleFileManager.EXT_FILE_PREFS)) {
								fTarget = f;
								break;
							}
						}
					}
				}
				catch (Throwable ex) {
					// ignore exception
					//AppLogger.error("MExecArgsEditPane.MExecArgItemPane.DataFileDropTarget#getTargetJavaFile() : Failed to drop to editor.", ex);
				}
				
				return fTarget;
			}
		}
	}
}
