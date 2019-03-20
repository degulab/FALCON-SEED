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
 * @(#)MacroFilterDefEditPane.java	3.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroFilterDefEditPane.java	2.0.0	2012/10/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.module.FilterDataError;
import ssac.falconseed.module.FilterError;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.module.MacroSubModuleRuntimeData;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.ModuleDataList;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.setting.MExecDefSettings;
import ssac.falconseed.module.swing.table.AbMacroSubFiltersTableModel;
import ssac.falconseed.module.swing.table.MacroSubFiltersTable;
import ssac.falconseed.module.swing.tree.IMExecDefFileChooserHandler;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.RunnerResources;
import ssac.falconseed.runner.view.RunnerFrame;
import ssac.util.io.VirtualFile;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.JStaticMultilineTextPane;
import ssac.util.swing.MenuToggleButton;
import ssac.util.swing.SwingTools;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.menu.ExMenuItem;
import ssac.util.swing.tree.JTreePopupMenu;

/**
 * マクロフィルタ専用のサブフィルタ構成編集パネル。
 * 
 * @version 3.1.0	2014/05/29
 * @since 2.0.0
 */
public class MacroFilterConstEditPane extends JSplitPane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	static private final String SubFilterListAction_Add			= "SubFilterList.add";
	static private final String SubFilterListAction_Duplicate	= "SubFilterList.duplicate";
	static private final String SubFilterListAction_Delete		= "SubFilterList.delete";
	static private final String SubFilterListAction_Up			= "SubFilterList.up";
	static private final String SubFilterListAction_Down		= "SubFilterList.down";
	static private final String SubFilterListAction_Edit		= "SubFilterList.edit";
	static private final String SubFilterListAction_EditWait	= SubFilterListAction_Edit + ".wait";
	static private final String SubFilterListAction_EditWait_Choose	= SubFilterListAction_EditWait + ".choose";
	static private final String SubFilterListAction_EditWait_Prev	= SubFilterListAction_EditWait + ".previous";
	static private final String SubFilterListAction_EditWait_NoWait	= SubFilterListAction_EditWait + ".nowait";
	
	static private final String PROPKEY_LASTSELECTED_SUBFILTER	= "subfilter";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** イベントリスナーのリスト **/
	protected EventListenerList	_listenerList = new EventListenerList();
	
	/** サブフィルタモデル変更イベント転送用リスナー **/
	protected IFilterValuesEditModelListener	_delegateListener;
	
	protected final SubFilterSelectionHandler _hFilterChooserHandler = new SubFilterSelectionHandler();

	/** データモデル **/
	private MacroFilterEditModel	_editModel;
	private MacroSubFilterValuesEditModel	_emptyValuesModel;
	
	/** サブフィルタ一覧テーブル用データモデル **/
	private final ImplMacroSubFiltersTableModel	_modelSubFiltersTableModel;
	/** フィルタ定義引数編集イベント用ハンドラ **/
	private final MacroFilterDefArgEditHandler		_hDefArgEditHandler;
	
	/** サブフィルタデータモデルのマップ **/
	private final Map<MacroSubModuleRuntimeData, MacroSubFilterValuesEditModel>	_mapConstFilterModel;
	
	private IFilterValuesEditModelListener	_argsModelListener;

	/** 現在選択中のフィルタを示すインデックス、未選択もしくは複数選択中は(-1) **/
	private int	_selectedItemIndex = (-1);
	
	/** フィルタ一覧テーブル **/
	private MacroSubFiltersTable		_listTable;
	/** 引数値設定パネル **/
	private MacroFilterValuesEditPane	_pnlValues;
	/** フィルタ一覧への追加ボタン **/
	private JButton	_btnListAdd;
	/** フィルタ一覧の複製ボタン **/
	private JButton	_btnListDup;
	/** フィルタ一覧からの削除ボタン **/
	private JButton	_btnListDel;
	/** フィルタ一覧の Up ボタン **/
	private JButton	_btnListUp;
	/** フィルタ一覧の Down ボタン **/
	private JButton	_btnListDown;

	/**
	 * フィルタ一覧編集用メニューアクションマップ
	 * @since 3.1.0
	 */
	private Map<String, ListEditMenuItemAction> _mapListEditMenuActions;
	/**
	 * フィルタ一覧の待機フィルタ選択ボタン
	 * @since 3.1.0
	 */
	private MenuToggleButton	_btnListEditWait;
	/**
	 * フィルタ一覧の待機フィルタ選択ボタン用ポップアップメニュー
	 * @since 3.1.0
	 */
	private JPopupMenu	_popupListEditWait;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroFilterConstEditPane() {
		super(JSplitPane.VERTICAL_SPLIT);
		this._mapConstFilterModel = new HashMap<MacroSubModuleRuntimeData, MacroSubFilterValuesEditModel>();
		this._modelSubFiltersTableModel = new ImplMacroSubFiltersTableModel();
		this._hDefArgEditHandler = new MacroFilterDefArgEditHandler();
		
		setResizeWeight(0);
		
		JPanel pnlList = createFilterListPane();
		
		_pnlValues = new MacroFilterValuesEditPane();
		_pnlValues.initialComponent();
		
		setTopComponent(pnlList);
		setBottomComponent(_pnlValues);
		
		// actions
		_listTable.setModel(getSubFiltersTableModel());
		_listTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onTableSelectionChanged(e);
			}
		});
		ActionListener btnAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSubFilterListButtonSelected(e);
			}
		};
		_btnListAdd .addActionListener(btnAction);
		_btnListDup .addActionListener(btnAction);
		_btnListDel .addActionListener(btnAction);
		_btnListUp  .addActionListener(btnAction);
		_btnListDown.addActionListener(btnAction);
		_btnListEditWait.addActionListener(btnAction);
		
		updateListButtons();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

//	/**
//	 * すべてのサブフィルタの引数情報を、現在のデータモデルに従い更新する。
//	 */
//	protected void refreshDisplayAllArguments() {
//		// サブフィルタ一覧の表示更新
//		//_listTable.refreshDisplay();
//		
//		// 現在アクティブなサブフィルタの引数表示更新
//		_pnlValues.refreshDisplayAllArguments();
//	}

	/**
	 * サブフィルタのファイルツリーの選択状態を解除。
	 * このメソッドは、フィルタ定義のファイルツリーとの選択状態交換のため、
	 * 呼び出される。
	 */
	public void clearFileTreeSelection() {
		_pnlValues.getTreeComponent().clearSelection();
	}

	/**
	 * サブフィルタのファイルツリーのノードが選択されていなければ <tt>true</tt> を返す。
	 */
	public boolean isFileTreeSelectionEmpty() {
		return _pnlValues.getTreeComponent().isSelectionEmpty();
	}

	/**
	 * 設定されているダイアログハンドラを取得する。
	 * @return	<code>IFilterDialogHandler</code> のインスタンス
	 */
	public IFilterDialogHandler getFilterDialogHandler() {
		return _pnlValues.getFilterDialogHandler();
	}

	/**
	 * 新しいダイアログハンドラを設定する。
	 * <em>newHandler</em> が <tt>null</tt> の場合、{@link EmptyFilterDialogHandler#getInstance()} が返す
	 * インスタンスが設定される。
	 * @param newHandler	<code>IFilterDialogHandler</code> のインスタンス
	 */
	public void setFilterDialogHandler(IFilterDialogHandler newHandler) {
		_pnlValues.setFilterDialogHandler(newHandler);
	}
	
	public MacroFilterEditModel getEditModel() {
		return _editModel;
	}
	
	public void setEditModel(MacroFilterEditModel newModel) {
		if (_editModel != newModel) {
			MacroFilterEditModel oldModel = _editModel;
			_editModel = newModel;
			onChangedDataModel(oldModel, newModel);
		}
	}

	/**
	 * サブフィルタ一覧表示テーブル用データモデルを返す。
	 */
	public AbMacroSubFiltersTableModel getSubFiltersTableModel() {
		return _modelSubFiltersTableModel;
	}
	
	public MacroFilterDefArgEditHandler getMExecDefArgsEditHandler() {
		return _hDefArgEditHandler;
	}

	/**
	 * フィルタ引数編集テーブルのカラム幅を初期化する。
	 * この初期化では、説明列と値列を、表示可能領域内で半々になるように設定する。
	 */
	public void initialAllTableColumnWidth() {
		_pnlValues.initialAllTableColumnWidth();
	}

	/**
	 * データモデルが変更されるたびに通知されるリストにリスナーを追加する。
	 * @param l		追加するリスナーオブジェクト
	 */
	public void addSubFilterDataModelListener(IFilterValuesEditModelListener l) {
		_listenerList.add(IFilterValuesEditModelListener.class, l);
	}

	/**
	 * データモデルが変更されるたびに通知されるリストからリスナーを削除する。
	 * @param l		削除するリスナーオブジェクト
	 */
	public void removeSubFilterDataModelListener(IFilterValuesEditModelListener l) {
		_listenerList.remove(IFilterValuesEditModelListener.class, l);
	}

	/**
	 * このモデルに登録された、すべてのデータモデルリスナーから成る配列を返す。
	 * @return	このモデルの <code>IFilterValuesEditModelListener</code> 全部。データモデルリスナーが現在登録されていない場合は空の配列
	 * @see #addSubFilterDataModelListener(IFilterValuesEditModelListener)
	 * @see #removeSubFilterDataModelListener(IFilterValuesEditModelListener)
	 */
	public IFilterValuesEditModelListener[] getSubFilterDataModelListeners() {
		return (IFilterValuesEditModelListener[])_listenerList.getListeners(IFilterValuesEditModelListener.class);
	}

	/**
	 * この <code>MacroFilterConstEditPane</code> に <code><em>Foo</em>Listener</code> として現在登録されているすべてのオブジェクトの配列を返す。
	 * <code><em>Foo</em>Listener</code> は、<code>add<em>Foo</em>Listener</code> メソッドを使用して登録する。
	 * <code><em>Foo</em>Listener.class</code> といったクラスリテラルを使用して、<em>listenerType</em> 引数を指定できる。
	 * 該当するリスナーがない場合は空の配列を返す。
	 * @param listenerType	要求されるリスナーの型。<code>java.util.EventListener</code> の下位インタフェースを指定
	 * @return	コンポーネントに <code><em>Foo</em>Listener</code> として登録されているすべてのオブジェクトの配列。リスナーが登録されていない場合は空の配列を返す
	 * @throws ClassCastException	<em>listenerType</em> が <code>java.util.EventListener</code> を実装するクラスまたはインタフェースを指定しない場合
	 * @see #getSubFilterDataModelListeners()
	 */
	public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		return _listenerList.getListeners(listenerType);
	}
	
	/**
	 * サブフィルタの実行時引数設定の正当性をチェックする。
	 * @return	実行時引数設定が正当な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean verifyData() {
		if (_editModel == null || _editModel.isSubFilterEmpty()) {
			// サブフィルタが存在しない
			return false;
		}

		boolean available = true;
		for (int index = 0; index < _editModel.getSubFilterCount(); index++) {
			MacroSubFilterValuesEditModel subFilterModel = _mapConstFilterModel.get(_editModel.getSubFilter(index));
			//--- モジュール設定をチェック
			if (!subFilterModel.verifyRunOrderLink(false)) {
				available = false;
			}
			//--- サブフィルタの引数設定をチェック
			if (!subFilterModel.verifyAllArguments()) {
				available = false;
			}
		}
		return available;
	}

	/**
	 * 待機なしサブフィルタの引数妥当性をチェックし、処理を続行するかを問い合わせる。
	 * 警告表示の際、指定されたタブコンポーネントの表示を、このコンポーネントに切り替える。
	 * このメソッドは、{@link #verifyData()} が <tt>true</tt> を返した後に呼び出すことを前提とする。
	 * @param cTab	このコンポーネントを含む、タブコンポーネント
	 * @return	処理を続行する場合は <tt>true</tt>、中止する場合は <tt>false</tt>
	 * @since 3.1.0
	 */
	public boolean verifyNoWaitAsyncModuleArguments(JTabbedPane cTab) {
		//--- 待機なしサブフィルタはマクロ実行開始時に実行されるようスケジューリングするため、
		//--- 他フィルタの引数参照に関する妥当性を検証する。
		//--- 妥当でない場合、処理を続行するかを問い合わせる。
		long lastNoWaitAsyncModuleRunNo = (-1L);
		for (int index = 0; index < _editModel.getSubFilterCount(); index++) {
			MacroSubModuleRuntimeData subModule = _editModel.getSubFilter(index);
			MacroSubFilterValuesEditModel subFilterModel = _mapConstFilterModel.get(subModule);
			//--- 待機なし非同期実行以外なら、スキップ
			if (!subModule.getRunOrderLinkEnabled() || !subModule.isEmptyWaitModules()) {
				continue;
			}
			//--- 引数をチェック
			int numargs = subFilterModel.getArgumentCount();
			for (int argindex = 0; argindex < numargs; ++argindex) {
				Object argval = subFilterModel.getArgumentValue(argindex);
				//--- 他フィルタの引数参照以外なら、スキップ
				if (!(argval instanceof ModuleArgID))
					continue;
				//--- 引数参照が、他の待機なしフィルタのものなら、スキップ
				ModuleArgID argid = (ModuleArgID)argval;
				if (argid.getData().getRunNo() <= lastNoWaitAsyncModuleRunNo)
					continue;

				// 引数参照が、他の待機なしフィルタよりも後に実行されるものなら、処理続行か問い合わせる
				//--- 表示切替
				cTab.setSelectedComponent(this);	// このコンポーネントを表示
				//--- 引数のエラー
				_listTable.getSelectionModel().setSelectionInterval(index, index);
				_listTable.scrollToVisibleCell(index, 0);
				_pnlValues.setFocusToArgument(argindex);
				//--- 問い合わせ
				String title  = String.format("%s ([%d] %s)", CommonMessages.getInstance().msgboxTitleWarn, subModule.getRunNo(), subModule.getName());
				String name   = String.format("[%d] %s", argid.getData().getRunNo(), argid.getData().getName());
				String askmsg = String.format(RunnerMessages.getInstance().confirmNoWaitFilterArgReferWarning, name);
				int ret = JOptionPane.showConfirmDialog(this, askmsg, title, 
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (ret == JOptionPane.YES_OPTION) {
					return true;	// 続行
				}
				else {
					return false;	// 中止
				}
			}
			//--- 待機なしフィルタの実行番号を保存
			lastNoWaitAsyncModuleRunNo = subModule.getRunNo();
		}
		// 該当するものがなければ、処理続行
		return true;
	}

	/**
	 * このコンポーネントの最初のエラーをメッセージボックスで表示する。
	 */
	public void showFirstError() {
		if (_editModel.isSubFilterEmpty()) {
			//--- サブフィルタなし
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgMacroFilterNoSubfilter);
			return;
		}
		
		for (int index = 0; index < _editModel.getSubFilterCount(); index++) {
			MacroSubFilterValuesEditModel subFilterModel = _mapConstFilterModel.get(_editModel.getSubFilter(index));
			//--- モジュールのエラーをチェック
			FilterDataError linkError = subFilterModel.getRunOrderLinkError();
			if (linkError != null) {
				//--- 現時点では、実行順序リンクエラーに関するもののみ
				_listTable.getSelectionModel().setSelectionInterval(index, index);
				_listTable.scrollToVisibleCell(index, AbMacroSubFiltersTableModel.CI_WAITFILTERS);
				_listTable.setFocus();
				Application.showErrorMessage(this, linkError.getErrorMessage());
				return;
			}
			//--- 引数のエラーをチェック
			FilterError err;
			for (int argIndex = 0; argIndex < subFilterModel.getArgumentCount(); argIndex++) {
				err = subFilterModel.getArgumentError(argIndex);
				if (err != null) {
					//--- 引数のエラー
					_listTable.getSelectionModel().setSelectionInterval(index, index);
					_listTable.scrollToVisibleCell(index, 0);
					_pnlValues.setFocusToArgument(argIndex);
					Application.showErrorMessage(this, err.getErrorMessage());
					return;
				}
			}
		}
	}

	public void restoreConfiguration(ExConfiguration config, String prefix) {
		// restore divider location
		if (config != null) {
			// Main tree
			int dl = config.getDividerLocation(prefix + ".MacroFilterConstEdit.main");
			if (dl > 0)
				this.setDividerLocation(dl);
			// sub panels
			if (_pnlValues != null) {
				_pnlValues.restoreConfiguration(config, prefix);
			}
		}
	}

	public void storeConfiguration(ExConfiguration config, String prefix) {
		// store divider location
		if (config != null) {
			// Main tree
			int dl = this.getDividerLocation();
			config.setDividerLocation(prefix + ".MacroFilterConstEdit.main", dl);
			// sub panels
			if (_pnlValues != null) {
				_pnlValues.storeConfiguration(config, prefix);
			}
		}
	}

//	/**
//	 * 指定されたインデックスに対応するフィルタ定義引数が、サブフィルタから参照されている場合に <tt>true</tt> を返す。
//	 * @param defArgIndex	判定するフィルタ定義引数のインデックス
//	 * @return	サブフィルタから参照された定義引数の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 * @throws IndexOutOfBoundsException	インデックスが適切ではない場合
//	 */
//	public boolean isReferedToMExecDefArgument(int defArgIndex) {
//		ModuleArgConfig defArgData = _editModel.getMExecDefArgument(defArgIndex);
//		return isReferedToMExecDefArgument(defArgData);
//	}
//
//	/**
//	 * 指定されたフィルタ定義引数が、サブフィルタから参照されている場合に <tt>true</tt> を返す。
//	 * @param defArgData	判定するフィルタ定義引数データ
//	 * @return	サブフィルタから参照された定義引数の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
//	 */
//	public boolean isReferedToMExecDefArgument(final ModuleArgConfig defArgData) {
//		if (defArgData != null && !_mapConstFilterModel.isEmpty()) {
//			for (Map.Entry<ModuleRuntimeData, MacroSubFilterValuesEditModel> entry : _mapConstFilterModel.entrySet()) {
//				if (entry.getValue().isReferedToMExecDefArgument(defArgData)) {
//					return true;
//				}
//			}
//		}
//		return false;	// no referenced
//	}

	//------------------------------------------------------------
	// Internal events
	//------------------------------------------------------------
	
	protected void onChangedDataModel(MacroFilterEditModel oldModel, MacroFilterEditModel newModel) {
		// create table model
		_emptyValuesModel = new MacroSubFilterValuesEditModel(newModel, null);
		getSubFiltersTableModel().updateColumnCount();
		_pnlValues.setDataModel(_emptyValuesModel);	// 未選択状態
		
		// 古いデータモデルのマップを破棄
		final IFilterValuesEditModelListener delegateListener = getDelegateModelListener();
		for (Map.Entry<MacroSubModuleRuntimeData, MacroSubFilterValuesEditModel> entry : _mapConstFilterModel.entrySet()) {
			entry.getValue().removeDataModelListener(delegateListener);
			entry.getValue().clearReferencedMap();
		}
		_mapConstFilterModel.clear();
		if (oldModel != null) {
			oldModel.setMExecDefArgEditHandler(null);
		}
		
		// 新しいデータモデルのマップを生成
		if (newModel != null) {
			for (MacroSubModuleRuntimeData module : newModel.getSubFilterList()) {
				MacroSubFilterValuesEditModel subModel = new MacroSubFilterValuesEditModel(newModel, module);
				subModel.verifyAllArguments();	// エラーチェック
				subModel.addDataModelListener(delegateListener);
				_mapConstFilterModel.put(module, subModel);
			}
			newModel.setMExecDefArgEditHandler(getMExecDefArgsEditHandler());
		}
		
		//--- リストボタン更新
		if (newModel != null && newModel.isEditing()) {
			//--- 編集可
			_btnListAdd .setVisible(true);
			_btnListDup .setVisible(true);
			_btnListDel .setVisible(true);
			_btnListUp  .setVisible(true);
			_btnListDown.setVisible(true);
			_btnListEditWait.setVisible(true);
		} else {
			//--- 編集不可
			_btnListAdd .setVisible(false);
			_btnListDup .setVisible(false);
			_btnListDel .setVisible(false);
			_btnListUp  .setVisible(false);
			_btnListDown.setVisible(false);
			_btnListEditWait.setVisible(false);
		}
		updateListButtons();
	}
	
	protected void onChangedTargetFilter(int oldItemIndex, int newItemIndex) {
		if (_editModel == null || newItemIndex < 0) {
			// no target model
			_pnlValues.setDataModel(_emptyValuesModel);
		}
		else {
			ModuleRuntimeData targetData = _editModel.getSubFilter(newItemIndex);
			if (targetData == null) {
				// no selection, or multiple selection
				//_pnlValues.setVisible(false);
				_pnlValues.setDataModel(_emptyValuesModel);
			}
			else {
				// set new data model
				_pnlValues.setDataModel(_mapConstFilterModel.get(targetData));
				//_pnlValues.setVisible(true);
			}
		}
	}
	
	protected void onTableSelectionChanged(ListSelectionEvent event) {
		if (event.getValueIsAdjusting())
			return;		// changing
		
		// update filter list control buttons
		updateListButtons();

		// update selected filter info
		if (_listTable.getSelectedRowCount() == 1) {
			// just one selected
			int newItemIndex = _listTable.getSelectedRow();
			if (newItemIndex != _selectedItemIndex) {
				int oldItemIndex = _selectedItemIndex;
				_selectedItemIndex = newItemIndex;
				onChangedTargetFilter(oldItemIndex, newItemIndex);
			}
		}
		else {
			// multiple selection or no selection
			if (_selectedItemIndex >= 0) {
				// change state to no selection
				int oldItemIndex = _selectedItemIndex;
				_selectedItemIndex = (-1);
				onChangedTargetFilter(oldItemIndex, _selectedItemIndex);
			}
		}
	}

	/**
	 * サブフィルタの値が変更されたときに呼び出されるイベント
	 */
	protected void onSubFilterDataChanged(FilterValuesEditModelEvent event) {
		MacroSubFilterValuesEditModel srcModel = (MacroSubFilterValuesEditModel)event.getSource();
		ModuleRuntimeData targetModule = (srcModel==null ? null : srcModel.getModuleData());
		
		if ((event.getType() & FilterValuesEditModelEvent.UPDATE_DEFINITION) != 0) {
			// 定義情報の更新
			_listTable.getModel().fireArgumentDefinitionUpdated(targetModule);
		}
		else if ((event.getType() & FilterValuesEditModelEvent.UPDATE_RUNORDER_LINK) != 0) {
			// 待機フィルタのエラー情報更新
			_listTable.getModel().fireWaitFiltersUpdate(targetModule);
		}
		else if ((event.getType() & FilterValuesEditModelEvent.UPDATE_ARGUMENT_DATA) != 0) {
			_listTable.getModel().fireArgumentValuesUpdated(targetModule, event.getFirstArgIndex(), event.getLastArgIndex());
		}
	}

	/**
	 * サブフィルタ操作ボタンが選択されたときに呼び出されるイベント
	 */
	protected void onSubFilterListButtonSelected(ActionEvent event) {
		final String strCommand = event.getActionCommand();
		
		if (SubFilterListAction_Add.equals(strCommand)) {
			onSubFilterListAdd(event);
		}
		else if (SubFilterListAction_Duplicate.equals(strCommand)) {
			onSubFilterListDuplicate(event);
		}
		else if (SubFilterListAction_Delete.equals(strCommand)) {
			onSubFilterListDelete(event);
		}
		else if (SubFilterListAction_Up.equals(strCommand)) {
			onSubFilterListMoveUp(event);
		}
		else if (SubFilterListAction_Down.equals(strCommand)) {
			onSubFilterListMoveDown(event);
		}
		else if (SubFilterListAction_EditWait.equals(strCommand)) {
			onSubFilterListEditWait(event);
		}
	}

	/**
	 * リスト編集メニュー選択時のハンドラ
	 * @since 3.1.0
	 */
	protected void onListEditMenuItemAction(ActionEvent ae) {
		String command = ae.getActionCommand();
		
		if (SubFilterListAction_EditWait_Prev.equals(command)) {
			// 直前のフィルタのみ待機
			onListEditMenuWaitPrevious(_listTable.getSelectedRows(), ae);
		}
		else if (SubFilterListAction_EditWait_NoWait.equals(command)) {
			// 待機無し
			onListEditMenuWaitNoWait(_listTable.getSelectedRows(), ae);
		}
		else if (SubFilterListAction_EditWait_Choose.equals(command)) {
			// 待機フィルタ選択
			onListEditMenuWaitChoose(_listTable.getSelectedRow(), ae);
		}
	}

	/**
	 * サブフィルタリストの[Add]ボタンが選択されたときに呼び出されるイベント
	 */
	protected void onSubFilterListAdd(ActionEvent event) {
		// 終端に追加
		VirtualFile vfInit = ((RunnerFrame)ModuleRunner.getApplicationMainFrame()).loadLastSelectedFilterPath(PROPKEY_LASTSELECTED_SUBFILTER);
		// フィルタの選択
		VirtualFile vfFilter = ((RunnerFrame)ModuleRunner.getApplicationMainFrame()).chooseMExecDefFile(this,
											true,	// システムルートを表示する
											RunnerMessages.getInstance().MExecDefFileChooser_Title,	// タイトル
											null,	// 説明は表示しない
											vfInit,	// 初期選択位置
											_hFilterChooserHandler);	// ハンドラ
		if (vfFilter == null) {
			// user canceled
			return;
		}
		((RunnerFrame)ModuleRunner.getApplicationMainFrame()).saveLastSelectedFilterPath(vfFilter, PROPKEY_LASTSELECTED_SUBFILTER);
		
		// フィルタの読み込み
		VirtualFile vfFilterPrefs = MExecDefFileManager.getModuleExecDefDataFile(vfFilter);
		MExecDefSettings settings = new MExecDefSettings();
		settings.loadForTarget(vfFilterPrefs);
		MacroSubModuleRuntimeData newData = new MacroSubModuleRuntimeData(settings);
		newData.getEditData().setOriginalMExecDefDir(vfFilterPrefs);
		
		// フィルタの終端への追加
		ModuleDataList<MacroSubModuleRuntimeData> sublist = _editModel.getSubFilterList();
		int insertRow = _editModel.getSubFilterCount();
		newData.setRunNo(insertRow+1);
		//--- サブフィルタリストへ追加
		sublist.add(newData);
		//--- 管理マップへ追加
		addToSubFilterDataModelMap(newData);
		
		// 表示を更新
		//--- 選択状態解除
		_listTable.clearSelection();
		//--- 行の挿入
		_listTable.getModel().fireTableRowsInserted(insertRow, insertRow);
		//--- 列数の更新
		_listTable.getModel().updateColumnCount();
		//--- 挿入された列を選択
		_listTable.getSelectionModel().setSelectionInterval(insertRow, insertRow);
		_listTable.scrollToVisibleCell(insertRow, 0);
	}
	
	/**
	 * サブフィルタリストの[Duplicate]ボタンが選択されたときに呼び出されるイベント
	 */
	protected void onSubFilterListDuplicate(ActionEvent event) {
		int[] selrows = _listTable.getSelectedRows();
		if (selrows.length <= 0)
			return;		// no selection
		int insertRow = _listTable.getSelectionModel().getMaxSelectionIndex() + 1;
		
		// 挿入位置以降の実行番号を更新
		ModuleDataList<MacroSubModuleRuntimeData> sublist = _editModel.getSubFilterList();
		for (int row = insertRow; row < sublist.size(); row++) {
			sublist.get(row).setRunNo(row + 1 + selrows.length);
		}
		
		// コピーを挿入
		int updateFirstRow = insertRow;
		for (int srcrow : selrows) {
			MacroSubModuleRuntimeData newData = sublist.get(srcrow).clone();
			newData.setRunNo(updateFirstRow+1);	// 実行番号更新
			//for (ModuleArgConfig argdata : newData) {
			//	argdata.setUserData(null);	// エラー除去
			//}
			//--- サブフィルタリストへ挿入
			sublist.add(updateFirstRow++, newData);
			//--- 管理マップへ追加
			addToSubFilterDataModelMap(newData);	// 全ての引数値は、この中でチェック
		}
		
		// 表示を更新
		//--- 選択状態解除
		_listTable.clearSelection();
		//--- 行の挿入
		_listTable.getModel().fireTableRowsInserted(insertRow, updateFirstRow-1);
		//--- 挿入位置より前で表示されている行の表示更新
		Rectangle rcVisibleIndices = _listTable.getVisibleCellIndices();
		if (!rcVisibleIndices.isEmpty() && rcVisibleIndices.y < updateFirstRow) {
			// 挿入位置より前の行が表示されていれば、その表示内容を更新
			_listTable.getModel().fireTableRowsUpdated(rcVisibleIndices.y, (updateFirstRow-1));
		}
		//--- 実行番号が更新された行の表示更新
		if (updateFirstRow < sublist.size()) {
			_listTable.getModel().fireTableRowsUpdated(updateFirstRow, sublist.size()-1);
		}
		//--- 列数の更新は不要(表示しているサブフィルタの複製なので、最大列数は変わらない)
		//_listTable.getModel().updateColumnCount();
		//--- 挿入された列を選択
		_listTable.getSelectionModel().setSelectionInterval(insertRow, updateFirstRow-1);
		subFiltersTableScrollRowsToVisible(insertRow, updateFirstRow-1);
	}
	
	/**
	 * サブフィルタリストの[Delete]ボタンが選択されたときに呼び出されるイベント
	 */
	protected void onSubFilterListDelete(ActionEvent event) {
		int[] selrows = _listTable.getSelectedRows();
		if (selrows.length <= 0)
			return;		// no selection
		int updateRow = _listTable.getSelectionModel().getMinSelectionIndex();
		
		// 削除対象のフィルタデータ収集
		HashSet<MacroSubModuleRuntimeData> referedForDelete = new HashSet<MacroSubModuleRuntimeData>();
		ModuleDataList<MacroSubModuleRuntimeData> sublist = _editModel.getSubFilterList();
		HashSet<MacroSubModuleRuntimeData> willRemoveSet = new HashSet<MacroSubModuleRuntimeData>(selrows.length);
		for (int row : selrows) {
			MacroSubModuleRuntimeData subModule = sublist.get(row);
			//--- 削除対象フィルタの集合へ追加
			willRemoveSet.add(subModule);
			//--- 削除対象フィルタを待機するフィルタの取得し、参照中フィルタの集合へ追加
			referedForDelete.addAll(subModule.getNextModuleSet());
		}
		//--- 参照中フィルタから削除対象フィルタを除外
		referedForDelete.removeAll(willRemoveSet);
		
		// 削除対象フィルタを参照中のフィルタを収集
		for (Map.Entry<MacroSubModuleRuntimeData, MacroSubFilterValuesEditModel> entry : _mapConstFilterModel.entrySet()) {
			if (!willRemoveSet.contains(entry.getKey())) {
				// 削除対象外のフィルタデータ
				if (entry.getValue().isReferedSubFilterData(willRemoveSet)) {
					referedForDelete.add(entry.getKey());
				}
			}
		}
		
		// 削除対象を問い合わせ
		if (!referedForDelete.isEmpty()) {
			int ret = JOptionPane.showConfirmDialog(this,
									RunnerMessages.getInstance().confirmDeleteReferedSubFilter,
									RunnerMessages.getInstance().confirmDeleteSubFilter,
									JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (ret != JOptionPane.YES_OPTION) {
				return;		// user canceled
			}
		}
		
		// 削除対象フィルタデータへの参照を、他サブフィルタから除去
		for (MacroSubModuleRuntimeData moduledata : referedForDelete) {
			MacroSubFilterValuesEditModel smodel = _mapConstFilterModel.get(moduledata);
			smodel.removeReferedSubFilterData(willRemoveSet);
		}
		
		// 削除対象フィルタのデータモデル除去
		for (ModuleRuntimeData moduledata : willRemoveSet) {
			removeFromSubFilterDataModelMap(moduledata);
		}
		
		// 行の削除と表示の更新
		//--- 選択を解除
		_listTable.clearSelection();
		//--- 行の削除
		AbMacroSubFiltersTableModel tmodel = _listTable.getModel();
		for (int index = selrows.length - 1; index >= 0; index--) {
			tmodel.removeRow(selrows[index]);
		}
		//--- 実行番号を更新
		for (int row = updateRow; row < sublist.size(); row++) {
			sublist.get(row).setRunNo(row+1);
		}
		//--- テーブルの表示を更新
		if (!tmodel.updateColumnCount()) {
			//--- 列数が更新されなかった場合は、全体の表示を更新
			tmodel.fireTableDataChanged();
		}
		//--- 削除開始位置を選択
		if (tmodel.getRowCount() > 0) {
			if (updateRow >= tmodel.getRowCount()) {
				updateRow = tmodel.getRowCount()-1;
			}
			_listTable.getSelectionModel().setSelectionInterval(updateRow, updateRow);
			_listTable.scrollToVisibleCell(updateRow, 0);
		}
	}
	
	/**
	 * サブフィルタリストの[Up]ボタンが選択されたときに呼び出されるイベント
	 */
	protected void onSubFilterListMoveUp(ActionEvent event) {
		int[] selrows = _listTable.getSelectedRows();
		if (selrows.length <= 0)
			return;		// no selection

		// 移動
		ModuleDataList<MacroSubModuleRuntimeData> subFilterList = _editModel.getSubFilterList();
		int firstRowIndex = -1;
		int lastRowIndex = -1;
		int movableRow = 1;
		final int[] afterMovedRows = new int[selrows.length];
		for (int index = 0; index < selrows.length; index++) {
			int srow = selrows[index];
			if (srow >= movableRow) {
				// 移動可能
				movableRow = srow-1;
				if (firstRowIndex < 0)
					firstRowIndex = movableRow;
				lastRowIndex = srow;
				MacroSubModuleRuntimeData prevModule = subFilterList.get(movableRow);
				subFilterList.set(movableRow, subFilterList.get(srow));
				subFilterList.set(srow, prevModule);
				afterMovedRows[index] = movableRow;
				movableRow += 2;	// 移動先インデックスと間隔が空いている位置
			}
			else {
				// 移動不可能
				afterMovedRows[index] = srow;
				movableRow = srow + 2;	// 移動不可能インデックスと間隔が空いている位置
			}
		}
		
		// 更新
		if (firstRowIndex >= 0) {
			updateRunNoAfterMove(afterMovedRows, firstRowIndex, lastRowIndex);
		}
	}
	
	/**
	 * サブフィルタリストの[Down]ボタンが選択されたときに呼び出されるイベント
	 */
	protected void onSubFilterListMoveDown(ActionEvent event) {
		int[] selrows = _listTable.getSelectedRows();
		if (selrows.length <= 0)
			return;		// no selection

		// 移動
		ModuleDataList<MacroSubModuleRuntimeData> subFilterList = _editModel.getSubFilterList();
		int firstRowIndex = -1;
		int lastRowIndex = -1;
		int movableRow = subFilterList.size() - 2;
		final int[] afterMovedRows = new int[selrows.length];
		for (int index = selrows.length-1; index >= 0; index--) {
			int srow = selrows[index];
			if (srow <= movableRow) {
				// 移動可能
				movableRow = srow+1;
				if (lastRowIndex < 0)
					lastRowIndex = movableRow;
				firstRowIndex = srow;
				MacroSubModuleRuntimeData nextModule = subFilterList.get(movableRow);
				subFilterList.set(movableRow, subFilterList.get(srow));
				subFilterList.set(srow, nextModule);
				afterMovedRows[index] = movableRow;
				movableRow -= 2;	// 移動先インデックスと間隔が空いている位置
			}
			else {
				// 移動不可能
				afterMovedRows[index] = srow;
				movableRow = srow - 2;	// 移動不可能インデックスと間隔が空いている位置
			}
		}
		
		// 更新
		if (lastRowIndex >= 0) {
			updateRunNoAfterMove(afterMovedRows, firstRowIndex, lastRowIndex);
		}
	}

	/**
	 * 移動後の実行番号と表示の更新を行う。
	 * 実行番号と実行順序が入れ替わるため、全てのサブフィルタ引数参照の値はエラーチェックされる。
	 * @param afterMovedRows	移動後の選択位置を示す行インデックスの配列
	 * @param firstRowIndex		移動範囲の開始インデックス
	 * @param lastRowIndex		移動範囲の終了インデックス
	 */
	protected void updateRunNoAfterMove(int[] afterMovedRows, int firstRowIndex, int lastRowIndex) {
		// 一端選択状態を解除
		ListSelectionModel selmodel = _listTable.getSelectionModel();
		selmodel.setValueIsAdjusting(true);
		selmodel.clearSelection();
		
		// 実行番号を更新
		ModuleDataList<MacroSubModuleRuntimeData> sublist = _editModel.getSubFilterList();
		HashSet<MacroSubModuleRuntimeData> updatedModuleSet = new HashSet<MacroSubModuleRuntimeData>(lastRowIndex-firstRowIndex+1);
		for (int rowIndex = firstRowIndex; rowIndex <= lastRowIndex; rowIndex++) {
			MacroSubModuleRuntimeData moduledata = sublist.get(rowIndex);
			moduledata.setRunNo(rowIndex+1);
			updatedModuleSet.add(moduledata);
		}
		
		// モジュール実行番号の参照表示を更新
		for (Map.Entry<MacroSubModuleRuntimeData, MacroSubFilterValuesEditModel> entry : _mapConstFilterModel.entrySet()) {
			MacroSubFilterValuesEditModel emodel = entry.getValue();
			//--- 待機フィルタの検証および表示更新
			Set<MacroSubModuleRuntimeData> waitModules = entry.getKey().getWaitModuleSet();
			if (!waitModules.isEmpty()) {
				boolean contains = false;
				if (updatedModuleSet.size() < waitModules.size()) {
					for (MacroSubModuleRuntimeData sm : updatedModuleSet) {
						if (waitModules.contains(sm)) {
							contains = true;
							break;
						}
					}
				} else {
					for (MacroSubModuleRuntimeData sm : waitModules) {
						if (updatedModuleSet.contains(sm)) {
							contains = true;
							break;
						}
					}
				}
				if (contains) {
					// 待機フィルタに変更されたモジュールが含まれていれば、検証および表示更新
					emodel.verifyRunOrderLink(true);	// イベント発行なし
					_listTable.getModel().fireRunOrderLinkUpdated(entry.getKey());
				}
			}
			//--- 引数参照の検証および表示更新
			emodel.refreshArgumentValueReferSubFilter(updatedModuleSet, false);	// エラーチェックは行う
		}
		
		// 選択状態の更新
		for (int row : afterMovedRows) {
			selmodel.addSelectionInterval(row, row);
		}
		selmodel.setValueIsAdjusting(false);
		//--- 選択位置を表示
		subFiltersTableScrollRowsToVisible(selmodel.getMinSelectionIndex(), selmodel.getMaxSelectionIndex());
		
		// 単一選択の場合に、フィルタの内容表示を更新
		if (afterMovedRows.length == 1) {
			_pnlValues.refreshDisplayFilterName();
			_pnlValues.refreshDisplayAllArguments();
		}
	}
	
	protected void subFiltersTableScrollRowsToVisible(int firstRowIndex, int lastRowIndex) {
		Rectangle rc1 = _listTable.getCellRect(firstRowIndex, 0, true);
		Rectangle rc2 = _listTable.getCellRect(lastRowIndex, 0, true);
		_listTable.scrollRectToVisible(rc1.union(rc2));
	}

	/**
	 * サブフィルタリストの[Edit wait filters]ボタンが選択されたときに呼び出されるイベント
	 * @since 3.1.0
	 */
	protected void onSubFilterListEditWait(ActionEvent event) {
		int numSelrows = _listTable.getSelectedRowCount();
		if (numSelrows <= 0)
			return;		// no selection
		
		// ポップアップメニューを表示
		JPopupMenu popupMenu = getListEditWaitPopupMenu();
		MenuToggleButton btn = (MenuToggleButton)event.getSource();
		popupMenu.show(btn, 0, btn.getHeight());
	}

	/**
	 * [Edit wait filters]-[Previous] メニュー選択時のハンドラ
	 * @param rowIndices	選択されている行インデックスの配列
	 * @param ae	イベントオブジェクト
	 * @since 3.1.0
	 */
	protected void onListEditMenuWaitPrevious(int[] rowIndices, ActionEvent ae) {
		if (rowIndices.length <= 0)
			return;	// 念のため
		
		for (int selrow : rowIndices) {
			// 対象フィルタの取得
			MacroSubModuleRuntimeData targetModule = _editModel.getSubFilter(selrow);
			
			// 直前のフィルタ待機なら、何もしない
			if (!targetModule.getRunOrderLinkEnabled() && targetModule.isEmptyWaitModules())
				continue;
			
			// 直前のフィルタのみ待機に変更
			targetModule.setRunOrderLinkEnabled(false);
			targetModule.disconnectAllWaitModules();
			_mapConstFilterModel.get(targetModule).verifyRunOrderLink(true);
			
			// 待機フィルタ選択結果を反映
			_listTable.getModel().fireWaitFiltersUpdate(selrow);
		}
	}

	/**
	 * [Edit wait filters]-[No wait] メニュー選択時のハンドラ
	 * @param rowIndices	選択されている行インデックスの配列
	 * @param ae	イベントオブジェクト
	 * @since 3.1.0
	 */
	protected void onListEditMenuWaitNoWait(int[] rowIndices, ActionEvent ae) {
		if (rowIndices.length <= 0)
			return;	// 念のため
		
		for (int selrow : rowIndices) {
			// 対象フィルタの取得
			MacroSubModuleRuntimeData targetModule = _editModel.getSubFilter(selrow);
			
			// 待機なしなら、何もしない
			if (targetModule.getRunOrderLinkEnabled() && targetModule.isEmptyWaitModules())
				continue;
			
			// 待機無しに変更
			targetModule.setRunOrderLinkEnabled(true);
			targetModule.disconnectAllWaitModules();
			_mapConstFilterModel.get(targetModule).verifyRunOrderLink(true);
			
			// 待機フィルタ選択結果を反映
			_listTable.getModel().fireWaitFiltersUpdate(selrow);
		}
	}
	
	/**
	 * [Edit wait filters]-[Choose] メニュー選択時のハンドラ
	 * @param rowIndex	唯一選択されている行インデックス
	 * @param ae	イベントオブジェクト
	 * @since 3.1.0
	 */
	protected void onListEditMenuWaitChoose(int rowIndex, ActionEvent ae) {
		if (rowIndex < 0)
			return;	// 念のため
		
		// 対象フィルタの取得
		MacroSubModuleRuntimeData targetModule = _editModel.getSubFilter(rowIndex);
		
		// 実行番号選択ダイアログの表示
		ModuleDataList<MacroSubModuleRuntimeData> subFilterList = _editModel.getSubFilterList();
		MacroSubFilterChooserModel chooserModel = new MacroSubFilterChooserModel(subFilterList,
														targetModule.getWaitModuleSet(),
														// 対象フィルタより下にあるフィルタは選択できないようにする
														subFilterList.subList(rowIndex, subFilterList.size()));
		Window wnd = SwingUtilities.getWindowAncestor(this);
		MacroSubFilterChooser chooser;
		if (wnd instanceof Frame) {
			chooser = new MacroSubFilterChooser((Frame)wnd, chooserModel, RunnerMessages.getInstance().MacroSubFilterChooser_Title_WaitFilters);
		} else {
			chooser = new MacroSubFilterChooser((Dialog)wnd, chooserModel, RunnerMessages.getInstance().MacroSubFilterChooser_Title_WaitFilters);
		}
		chooser.initialComponent();
		chooser.setVisible(true);
		if (chooser.getDialogResult() != IDialogResult.DialogResult_OK) {
			return;	// user canceled
		}
		//--- 待機フィルタの格納
		targetModule.setRunOrderLinkEnabled(true);		// 任意のフィルタ待機指定
		targetModule.disconnectAllWaitModules();	// すべての待機フィルタをクリア
		for (ModuleRuntimeData aModule : chooserModel.getSelectedModuleList()) {
			targetModule.connectWaitModule((MacroSubModuleRuntimeData)aModule);	// 選択されたフィルタを待機フィルタに登録
		}

		// 待機フィルタのエラーチェック(エラー情報のクリア)
		_mapConstFilterModel.get(targetModule).verifyRunOrderLink(true);
		
		// 待機フィルタ選択結果を反映
		_listTable.getModel().fireWaitFiltersUpdate(rowIndex);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 指定されたフィルタを、管理マップへ登録する。
	 * 登録する際に新しいデータモデルを作成し、エラーチェックなども行う。
	 * @param newModule		新たに登録するサブフィルタデータ
	 * @return	新規に登録された場合は <tt>true</tt>、すでに存在している場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	protected boolean addToSubFilterDataModelMap(final MacroSubModuleRuntimeData newModule) {
		if (newModule == null)
			throw new NullPointerException("'newModule' is null.");
		if (_mapConstFilterModel.containsKey(newModule))
			return false;		// already exists
		
		MacroSubFilterValuesEditModel smodel = new MacroSubFilterValuesEditModel(_editModel, newModule);
		smodel.verifyRunOrderLink(true);
		smodel.verifyAllArguments();
		smodel.addDataModelListener(getDelegateModelListener());
		_mapConstFilterModel.put(newModule, smodel);
		return true;
	}

	/**
	 * 指定されたフィルタのサブフィルタデータモデルを、管理マップから削除する。
	 * このメソッドでは、削除するモデルが存在する場合、データはすべてクリアし、
	 * イベントリスナーなども除外する。
	 * @param removeModule	削除するサブフィルタデータ
	 * @return	削除された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean removeFromSubFilterDataModelMap(final ModuleRuntimeData removeModule) {
		MacroSubFilterValuesEditModel rmodel = _mapConstFilterModel.remove(removeModule);
		if (rmodel != null) {
			//--- 削除
			rmodel.removeDataModelListener(getDelegateModelListener());
			rmodel.cleanupWithoutEvents();
			return true;
		}
		else {
			//--- データなし
			return false;
		}
	}
	
	protected void updateListButtons() {
		int selRows = _listTable.getSelectedRowCount();
		if (selRows > 0) {
			// has selection rows
			_btnListDup .setEnabled(true);
			_btnListDel .setEnabled(true);
			_btnListEditWait.setEnabled(true);
			if (selRows == _listTable.getRowCount()) {
				// selected all
				_btnListUp  .setEnabled(false);
				_btnListDown.setEnabled(false);
			}
			else {
				int selMinRow = _listTable.getSelectionModel().getMinSelectionIndex();
				int selMaxRow = _listTable.getSelectionModel().getMaxSelectionIndex();
				int rangeRows = selMaxRow - selMinRow + 1;
				if (rangeRows != selRows) {
					// no selection rows in range
					_btnListUp  .setEnabled(true);
					_btnListDown.setEnabled(true);
				}
				else if (selMinRow == 0) {
					// allow down
					_btnListUp  .setEnabled(false);
					_btnListDown.setEnabled(true);
				}
				else if (selMaxRow == (_listTable.getRowCount()-1)) {
					// allow up
					_btnListUp  .setEnabled(true);
					_btnListDown.setEnabled(false);
				}
				else {
					// allow up and down
					_btnListUp  .setEnabled(true);
					_btnListDown.setEnabled(true);
				}
			}
		} else {
			// no selection
			_btnListDup .setEnabled(false);
			_btnListDel .setEnabled(false);
			_btnListUp  .setEnabled(false);
			_btnListDown.setEnabled(false);
			_btnListEditWait.setEnabled(false);
		}
	}
	
	protected JPanel createFilterListPane() {
		// create Filter list component
		_listTable = new MacroSubFiltersTable();
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sc.setViewportView(_listTable);
		sc.setRowHeaderView(_listTable.getTableRowHeader());
		//--- setup corner
		JPanel borderPanel = new JPanel();
		borderPanel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		sc.setCorner(JScrollPane.UPPER_LEFT_CORNER, borderPanel);
		
		// create buttons
		_btnListAdd      = CommonResources.createIconButton(CommonResources.ICON_ADD, CommonMessages.getInstance().Button_Add);
		_btnListDup      = CommonResources.createIconButton(RunnerResources.ICON_FILTER_DUPLICATE, CommonMessages.getInstance().Button_Duplicate);
		_btnListDel      = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		_btnListUp       = CommonResources.createIconButton(CommonResources.ICON_ARROW_UP, CommonMessages.getInstance().Button_Up);
		_btnListDown     = CommonResources.createIconButton(CommonResources.ICON_ARROW_DOWN, CommonMessages.getInstance().Button_Down);
		_btnListEditWait = CommonResources.createMenuIconButton(RunnerResources.ICON_MACROCOMMAND_WAIT, RunnerMessages.getInstance().MacroSubFiltersTable_Button_EditWait);
		_btnListAdd .setActionCommand(SubFilterListAction_Add);
		_btnListDup .setActionCommand(SubFilterListAction_Duplicate);
		_btnListDel .setActionCommand(SubFilterListAction_Delete);
		_btnListUp  .setActionCommand(SubFilterListAction_Up);
		_btnListDown.setActionCommand(SubFilterListAction_Down);
		_btnListEditWait.setActionCommand(SubFilterListAction_EditWait);
		Dimension dmMax = new Dimension();
		SwingTools.expand(dmMax, _btnListAdd .getPreferredSize());
		SwingTools.expand(dmMax, _btnListDup .getPreferredSize());
		SwingTools.expand(dmMax, _btnListDel .getPreferredSize());
		SwingTools.expand(dmMax, _btnListUp  .getPreferredSize());
		SwingTools.expand(dmMax, _btnListDown.getPreferredSize());
		SwingTools.expand(dmMax, _btnListEditWait.getPreferredSize());
		_btnListAdd .setPreferredSize(dmMax);
		_btnListAdd .setMinimumSize(dmMax);
		_btnListDup .setPreferredSize(dmMax);
		_btnListDup.setMinimumSize(dmMax);
		_btnListDel .setPreferredSize(dmMax);
		_btnListDel .setMinimumSize(dmMax);
		_btnListUp  .setPreferredSize(dmMax);
		_btnListUp  .setMinimumSize(dmMax);
		_btnListDown.setPreferredSize(dmMax);
		_btnListDown.setMinimumSize(dmMax);
		_btnListEditWait.setPreferredSize(dmMax);
		_btnListEditWait.setMinimumSize(dmMax);
		
		// Layout
		int numListButtons = 6;
		JPanel pnl = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = numListButtons;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		pnl.add(sc, gbc);
		
		gbc.gridheight = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 2, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		pnl.add(_btnListAdd, gbc);
		
		gbc.gridy++;
		gbc.insets = new Insets(2, 2, 0, 0);
		pnl.add(_btnListDup, gbc);
		
		gbc.gridy++;
		pnl.add(_btnListEditWait, gbc);
		
		gbc.gridy++;
		pnl.add(_btnListDel, gbc);
		
		gbc.gridy++;
		pnl.add(_btnListUp, gbc);
		
		gbc.gridy++;
		pnl.add(_btnListDown, gbc);
		
		Dimension dmMin = new Dimension(0, dmMax.height*numListButtons+(2*(numListButtons-1)));
		pnl.setMinimumSize(dmMin);
		
		//_btnListAdd .setVisible(false);
		//_btnListEdit.setVisible(false);
		//_btnListDel .setVisible(false);
		//_btnListUp  .setVisible(false);
		//_btnListDown.setVisible(false);
		
		return pnl;
	}

//	/**
//	 * フィルタ定義引数を参照するすべてのフィルタの表示を更新する。
//	 * <em>targetSet</em> が <tt>null</tt> ではない場合、この集合に含まれるフィルタ定義引数を
//	 * 参照している箇所の表示のみが更新される。
//	 * <em>targetSet</em> が <tt>null</tt> の場合は、フィルタ定義引数を参照しているすべての表示箇所が
//	 * 更新される。
//	 * @param targetSet	更新対象となるフィルタ定義引数の集合
//	 */
//	protected void refreshDisplayAllReferedMExecDefArguments(Collection<ModuleArgConfig> targetSet) {
//		if (_editModel == null)
//			return;
//		
//		if (targetSet != null) {
//			for (int index = 0; index < _editModel.getSubFilterCount(); index++) {
//				MacroSubFilterValuesEditModel smodel = _mapConstFilterModel.get(_editModel.getSubFilter(index));
//				for (int argIndex = 0; argIndex < smodel.getArgumentCount(); argIndex++) {
//					ModuleArgConfig argData = smodel.getArgument(argIndex);
//					if (argData.getValue() instanceof ModuleArgConfig) {
//						ModuleArgConfig defArgData = (ModuleArgConfig)argData.getValue();
//						if (targetSet.contains(defArgData)) {
//							//--- サブフィルタ一覧表示の更新
//							_modelSubFiltersTableModel.fireArgumentValueUpdated(index, argIndex);
//							//--- サブフィルタ引数表示の更新
//							smodel.refreshDisplayArgumentValue(argIndex);
//						}
//					}
//				}
//			}
//		}
//		else {
//			for (int index = 0; index < _editModel.getSubFilterCount(); index++) {
//				MacroSubFilterValuesEditModel smodel = _mapConstFilterModel.get(_editModel.getSubFilter(index));
//				for (int argIndex = 0; argIndex < smodel.getArgumentCount(); argIndex++) {
//					ModuleArgConfig argData = smodel.getArgument(argIndex);
//					if (argData.getValue() instanceof ModuleArgConfig) {
//						//--- サブフィルタ一覧表示の更新
//						_modelSubFiltersTableModel.fireArgumentValueUpdated(index, argIndex);
//						//--- サブフィルタ引数表示の更新
//						smodel.refreshDisplayArgumentValue(argIndex);
//					}
//				}
//			}
//		}
//	}

	/**
	 * リスト編集メニューアクションの取得
	 * @param command	コマンド文字列
	 * @return	コマンド文字列に対応するアクション
	 * @since 3.1.0
	 */
	protected ListEditMenuItemAction getListEditMenuItemAction(String command) {
		if (_mapListEditMenuActions == null) {
			_mapListEditMenuActions = new HashMap<String, ListEditMenuItemAction>();
			//--- Edit wait - no wait
			_mapListEditMenuActions.put(SubFilterListAction_EditWait_NoWait,
					new ListEditMenuItemAction(SubFilterListAction_EditWait_NoWait,
							RunnerMessages.getInstance().MacroSubFiltersTable_EditMenu_NoWait));
			//--- Edit wait - previous
			_mapListEditMenuActions.put(SubFilterListAction_EditWait_Prev,
					new ListEditMenuItemAction(SubFilterListAction_EditWait_Prev,
							RunnerMessages.getInstance().MacroSubFiltersTable_EditMenu_WaitPrevious));
			//--- Edit wait - choose
			_mapListEditMenuActions.put(SubFilterListAction_EditWait_Choose,
					new ListEditMenuItemAction(SubFilterListAction_EditWait_Choose,
							RunnerMessages.getInstance().MacroSubFiltersTable_EditMenu_WaitChoose));
		}
		return _mapListEditMenuActions.get(command);
	}

	/**
	 * リスト編集メニューアイテムの生成
	 * @param command	コマンド文字列
	 * @return	メニューアイテム
	 * @since 3.1.0
	 */
	protected JMenuItem createListEditMenuItem(String command) {
		Action action = getListEditMenuItemAction(command);
		JMenuItem item = new JMenuItem(action);
		return item;
	}

	/**
	 * 待機フィルタ編集ポップアップメニューの取得。
	 * @return	ポップアップメニュー
	 * @since 3.1.0
	 */
	protected JPopupMenu getListEditWaitPopupMenu() {
		// なければインスタンス生成
		if (_popupListEditWait == null) {
			JPopupMenu menu = new JPopupMenu();
			//--- previous
			menu.add(createListEditMenuItem(SubFilterListAction_EditWait_Prev));
			//--- no wait
			menu.add(createListEditMenuItem(SubFilterListAction_EditWait_NoWait));
			//--- choose
			menu.add(createListEditMenuItem(SubFilterListAction_EditWait_Choose));
			
			_popupListEditWait = menu;
			
			// popup menu action
			menu.addPopupMenuListener(_btnListEditWait.getToggleOffPopupMenuListener());
		}
		
		// 選択数に応じてメニュー項目の有効／無効を設定
		int numSelRows = _listTable.getSelectedRowCount();
		if (numSelRows > 0) {
			// 選択あり
			getListEditMenuItemAction(SubFilterListAction_EditWait_Prev)  .setEnabled(true);
			getListEditMenuItemAction(SubFilterListAction_EditWait_NoWait).setEnabled(true);
			if (numSelRows == 1) {
				// 単一選択
				getListEditMenuItemAction(SubFilterListAction_EditWait_Choose).setEnabled(_listTable.getSelectedRow() != 0);	// 先頭は選択できない
			} else {
				// 複数選択の場合は、待機フィルタは選択できない
				getListEditMenuItemAction(SubFilterListAction_EditWait_Choose).setEnabled(false);
			}
		} else {
			// 選択なし
			getListEditMenuItemAction(SubFilterListAction_EditWait_Prev)  .setEnabled(false);
			getListEditMenuItemAction(SubFilterListAction_EditWait_NoWait).setEnabled(false);
			getListEditMenuItemAction(SubFilterListAction_EditWait_Choose).setEnabled(false);
		}

		// インスタンスを返す
		return _popupListEditWait;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * ボタン押下で表示されるポップアップメニューに対応するアクション用クラス。
	 * @since 3.1.0
	 */
	protected class ListEditMenuItemAction extends AbMenuItemAction
	{
		private static final long serialVersionUID = 1L;

		public ListEditMenuItemAction(String command, String caption) {
			super(caption);
			setCommandKey(command);
		}

		public void actionPerformed(final ActionEvent ae) {
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					onListEditMenuItemAction(ae);
				}
			});
		}
	}
	
	protected IFilterValuesEditModelListener getDelegateModelListener() {
		if (_argsModelListener == null) {
			_argsModelListener = createDelegateModelListener();
		}
		return _argsModelListener;
	}
	
	protected IFilterValuesEditModelListener createDelegateModelListener() {
		return new DelegateFilterValuesEditModelListener();
	}
	
	protected class DelegateFilterValuesEditModelListener implements IFilterValuesEditModelListener
	{
		@Override
		public void dataChanged(FilterValuesEditModelEvent e) {
			onSubFilterDataChanged(e);
			Object[] listeners = _listenerList.getListenerList();
			for (int i = listeners.length-2; i>=0; i-=2) {
				if (listeners[i]==IFilterValuesEditModelListener.class) {
					((IFilterValuesEditModelListener)listeners[i+1]).dataChanged(e);
				}
			}
		}
	}

	/**
	 * サブフィルタ一覧表示テーブル用のデータモデル。
	 * このモデルは、<code>MacroFilterEditModel</code> の編集に応じて更新されるため、
	 * サブクラスとして実装している。
	 */
	protected class ImplMacroSubFiltersTableModel extends AbMacroSubFiltersTableModel
	{
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean hasEditModel() {
			return (_editModel != null);
		}
		
		@Override
		protected MacroFilterEditModel getEditModel() {
			return MacroFilterConstEditPane.this.getEditModel();
		}
		
		protected String getDisplayFilterName(MacroSubModuleRuntimeData moduledata) {
			return _mapConstFilterModel.get(moduledata).getName();
		}
	}
	
	protected class SubFilterSelectionHandler implements IMExecDefFileChooserHandler
	{
		@Override
		public boolean acceptChooseFile(Object source, VirtualFile selectionFile) {
			if (_editModel != null) {
				VirtualFile vfOrgPath = _editModel.getOriginalFilterRootDirectory();
				if (vfOrgPath != null && vfOrgPath.equals(selectionFile)) {
					//--- このフィルタと同じサブフィルタは選択できない
					Application.showErrorMessage((source instanceof Dialog ? (Dialog)source : null),
							RunnerMessages.getInstance().msgCouldNotChooseThisFilterAsSubFilter);
					return false;
				}
			}
			
			// accept
			return true;
		}
	}
	
	protected class MacroFilterDefArgEditHandler implements IFilterArgEditHandler
	{
		@Override
		public boolean isReferencedValue(Object refValue) {
			// 指定された値がサブフィルタからの参照値であるかを判定
			for (Map.Entry<MacroSubModuleRuntimeData, MacroSubFilterValuesEditModel> entry : _mapConstFilterModel.entrySet()) {
				if (entry.getValue().isReferencedValue(refValue)) {
					return true;
				}
			}
			return false;	// no reference
		}

		@Override
		public void onChangedArgument(int firstArgIndex, int lastArgIndex) {
			// フィルタ定義引数の参照値の表示更新
			List<FilterArgEditModel> argsList = _editModel.getMExecDefArgsList().subList(firstArgIndex, lastArgIndex+1);
			if (!argsList.isEmpty()) {
				for (Map.Entry<MacroSubModuleRuntimeData, MacroSubFilterValuesEditModel> entry : _mapConstFilterModel.entrySet()) {
					for (FilterArgEditModel defArgData : argsList) {
						entry.getValue().updateReferencedValue(defArgData, true);	// 値のチェックは行わない
					}
				}
			}
		}

		@Override
		public void onRemovedArgument(FilterArgEditModel deletedArgData) {
			// サブフィルタから、削除されたフィルタ定義引数の参照を除去
			for (Map.Entry<MacroSubModuleRuntimeData, MacroSubFilterValuesEditModel> entry : _mapConstFilterModel.entrySet()) {
				entry.getValue().removeReferencedValue(deletedArgData);
			}
		}
	}
	
	static protected class MacroFilterValuesEditPane extends AbFilterValuesEditPane
	{
		private static final long serialVersionUID = 1L;

		private JLabel			_lblFilterName;
		private JTextComponent	_lblFilterLastModified;
		private JTextComponent	_lblFilterLocation;
		
		public MacroFilterValuesEditPane() {
			super(true);
			setVisibleFilterName(true);
		}

		/**
		 * フィルタ引数編集テーブルのカラム幅を初期化する。
		 * この初期化では、説明列と値列を、表示可能領域内で半々になるように設定する。
		 */
		public void initialAllTableColumnWidth() {
			((MacroSubFilterArgValuesEditPane)getArgValuesEditPane()).initialAllTableColumnWidth();
		}

		@Override
		protected AbFilterArgValuesEditPane createArgValuesEditPanel() {
			AbFilterArgValuesEditPane argpnl = new MacroSubFilterArgValuesEditPane();
			argpnl.initialComponent();
			return argpnl;
		}
		
		@Override
		protected void onTreeDoubleClicked(MouseEvent e) {
			// no entry
		}

		@Override
		public void refreshDisplayFilterName() {
			if (_lblFilterName != null && _lblFilterName.isDisplayable()) {
				String name = getFormattedFilterName();
				String time = getFormattedFilterLastModified();
				String path = getFormattedFilterLocation();
				_lblFilterName.setText(name!=null && name.length()>0 ? name : " ");
				_lblFilterLastModified.setText(time!=null && time.length()>0 ? time : " ");
				_lblFilterLocation.setText(path!=null && path.length()>0 ? path : " ");
			}
		}
		
		public String getFormattedFilterLastModified() {
			ModuleRuntimeData data = getDataModel().getModuleData();
			if (data != null) {
				VirtualFile vfPrefs = data.getExecDefPrefsFile();
				if (vfPrefs != null && vfPrefs.exists()) {
					long lastmodified = vfPrefs.lastModified();
					return "(".concat(DateFormat.getDateTimeInstance().format(new Date(lastmodified))).concat(")");
				}
				else {
					return null;
				}
			}
			else {
				return null;
			}
		}

		/**
		 * フィルタの位置を整形されたパス文字列で返す。
		 * @return	フィルタの整形済みパス文字列。フィルタが指定されていない場合は <tt>null</tt> を返す。
		 */
		public String getFormattedFilterLocation() {
			ModuleRuntimeData data = getDataModel().getModuleData();
			if (data != null) {
				if (getDataModel() instanceof MacroSubFilterValuesEditModel) {
					MacroSubFilterValuesEditModel emodel = (MacroSubFilterValuesEditModel)getDataModel();
					return emodel.getEditModel().formatFilePath(data.getExecDefDirectory());
				}
				else {
					return data.getExecDefDirectory().getAbsolutePath();
				}
			}
			else {
				return null;
			}
		}
		
		protected JPanel createFilterInfoPane() {
			_lblFilterName = new JLabel("Test");
			JStaticMultilineTextPane ta = new JStaticMultilineTextPane("Test");
			ta.setBorder(null);
			ta.setLineWrap(false);
			ta.setWrapStyleWord(false);
			_lblFilterLastModified = ta;
			_lblFilterLocation = new JStaticMultilineTextPane("Test");
			_lblFilterLocation.setBorder(null);
			
			JPanel pnl = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = 2;
			gbc.gridheight = 1;
			gbc.weightx = 1;
			gbc.weighty = 0;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.fill   = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = new Insets(3,3,0,3);
			//--- filter name label
			pnl.add(_lblFilterName, gbc);
			gbc.gridy++;
			//--- filter last modified
			gbc.insets = new Insets(0, 5, 3, 3);
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			pnl.add(_lblFilterLastModified, gbc);
			gbc.gridx++;
			//--- filter location
			gbc.insets = new Insets(0, 10, 3, 3);
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			pnl.add(_lblFilterLocation, gbc);
			
			pnl.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			
			Dimension dmMin = pnl.getMinimumSize();
			dmMin.height = pnl.getPreferredSize().height;
			pnl.setMinimumSize(dmMin);

			_lblFilterName.setText(" ");
			_lblFilterLastModified.setText(" ");
			_lblFilterLocation.setText(" ");
			
			return pnl;
		}

		@Override
		protected JPanel createDescriptionPane() {
			JPanel pnlInfo = createFilterInfoPane();
			JPanel pnlDesc = super.createDescriptionPane();
			
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(pnlInfo, BorderLayout.NORTH);
			panel.add(pnlDesc, BorderLayout.CENTER);
			
			Dimension dmMin = panel.getMinimumSize();
			dmMin.height = pnlInfo.getMinimumSize().height + pnlDesc.getMinimumSize().height;
			panel.setMinimumSize(dmMin);
			
			return panel;
		}

		@Override
		protected JTreePopupMenu createTreeContextMenu() {
			// create Menu component
			JMenuItem item;
			JTreePopupMenu menu = new JTreePopupMenu();
//			//--- open
//			item = new ExMenuItem(true, false, _treeActionOpen);
//			menu.add(item);
//			//--- typed open
//			JMenu typedOpenMenu = new JMenu(_treeActionTypedOpen);
//			{
//				//--- csv
//				item = new ExMenuItem(true, false, _treeActionTypedOpenCsv);
//				typedOpenMenu.add(item);
//			}
//			menu.add(typedOpenMenu);
//			//---
//			menu.addSeparator();
			//--- copy
			item = new ExMenuItem(true, false, _treeActionCopy);
			menu.add(item);
			//---
			menu.addSeparator();
			//--- refresh
			item = new ExMenuItem(true, false, _treeActionRefresh);
			menu.add(item);
			
			return menu;
		}
	}
}
