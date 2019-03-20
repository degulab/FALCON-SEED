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
 * @(#)MacroSubFilterArgValueTablePane.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroSubFilterArgValueTablePane.java	2.0.0	2012/11/05
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.swing.table.AbModuleArgTablePane;
import ssac.aadl.module.swing.table.DefaultModuleArgTableCellRenderer;
import ssac.aadl.module.swing.table.IModuleArgTableModel;
import ssac.aadl.module.swing.table.ModuleArgTable;
import ssac.aadl.module.swing.table.StaticModuleArgTypeTableCellRenderer;
import ssac.falconseed.file.VirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFileTransferable;
import ssac.falconseed.module.FilterError;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.ModuleRuntimeData;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgCsvFile;
import ssac.falconseed.module.args.MExecArgDirectory;
import ssac.falconseed.module.args.MExecArgPublish;
import ssac.falconseed.module.args.MExecArgString;
import ssac.falconseed.module.args.MExecArgSubscribe;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.args.MExecArgTextFile;
import ssac.falconseed.module.args.MExecArgXmlFile;
import ssac.falconseed.module.swing.FilterArgEditModel;
import ssac.falconseed.module.swing.IFilterValuesEditModel;
import ssac.falconseed.module.swing.MacroFilterDefArgReferDialog;
import ssac.falconseed.module.swing.MacroFilterEditModel;
import ssac.falconseed.module.swing.MacroSubFilterArgReferDialog;
import ssac.falconseed.module.swing.MacroSubFilterValuesEditModel;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.MenuToggleButton;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.table.SpreadSheetTable.RowHeaderModel;

/**
 * マクロフィルタを構成するサブフィルタ毎の引数値設定用テーブル。
 * 
 * @version 3.1.0	2014/05/19
 * @since 2.0.0
 */
public class MacroSubFilterArgValueTablePane extends AbModuleArgTablePane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	static protected final String MIEDIT_INFILE_MENU		= "arg.edit.infile";
	static protected final String MIEDIT_INFILE_CHOOSE		= MIEDIT_INFILE_MENU + ".choose";		// 入力ファイル選択
	static protected final String MIEDIT_INFILE_OUTFILE		= MIEDIT_INFILE_MENU + ".outfile";		// 出力ファイルの参照
	static protected final String MIEDIT_INFILE_REFER		= MIEDIT_INFILE_MENU + ".refer";		// フィルタ定義引数の参照
	static protected final String MIEDIT_INFILE_CREATEARG	= MIEDIT_INFILE_MENU + ".createarg";	// フィルタ定義引数の作成
	
	static protected final String MIEDIT_OUTFILE_MENU		= "arg.edit.outfile";
	//static protected final String MIEDIT_OUTFILE_CHOOSE	= MIEDIT_OUTFILE_MENU + ".choose";		// 出力ファイル選択
	static protected final String MIEDIT_OUTFILE_TEMP		= MIEDIT_OUTFILE_MENU + ".temp";		// テンポラリ出力指定
	static protected final String MIEDIT_OUTFILE_REFER		= MIEDIT_OUTFILE_MENU + ".refer";		// フィルタ定義引数の参照
	static protected final String MIEDIT_OUTFILE_CREATEARG	= MIEDIT_OUTFILE_MENU + ".createarg";	// フィルタ定義引数の作成

	static protected final String MIEDIT_STR_MENU			= "arg.edit.str";
	static protected final String MIEDIT_STR_DIRECT			= MIEDIT_STR_MENU + ".direct";			// 直接編集
	static protected final String MIEDIT_STR_REFER			= MIEDIT_STR_MENU + ".refer";			// フィルタ定義引数の参照
	static protected final String MIEDIT_STR_CREATEARG		= MIEDIT_STR_MENU + ".createarg";		// フィルタ定義引数の作成
	
	static protected final String MIEDIT_PUB_MENU			= "arg.edit.pub";
	static protected final String MIEDIT_PUB_DIRECT			= MIEDIT_PUB_MENU + ".direct";			// 直接編集
	static protected final String MIEDIT_PUB_REFER			= MIEDIT_PUB_MENU + ".refer";			// フィルタ定義引数の参照
	static protected final String MIEDIT_PUB_CREATEARG		= MIEDIT_PUB_MENU + ".createarg";		// フィルタ定義引数の作成
	
	static protected final String MIEDIT_SUB_MENU			= "arg.edit.sub";
	static protected final String MIEDIT_SUB_DIRECT			= MIEDIT_SUB_MENU + ".direct";			// 直接編集
	static protected final String MIEDIT_SUB_PUB			= MIEDIT_SUB_MENU + ".pub";				// [PUB]の参照
	static protected final String MIEDIT_SUB_REFER			= MIEDIT_SUB_MENU + ".refer";			// フィルタ定義引数の参照
	static protected final String MIEDIT_SUB_CREATEARG		= MIEDIT_SUB_MENU + ".createarg";		// フィルタ定義引数の作成

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final ArgDataDropTargetListener	_hDropDataToTable = new ArgDataDropTargetListener();
	
	private Map<String, EditMenuItemAction> _mapEditMenuActions;

	/** 入力ファイルを引数とする値の編集用ポップアップメニュー **/
	private JPopupMenu		_popupEditFileIn;
	/** 出力ファイルを引数とする値の編集用ポップアップメニュー **/
	private JPopupMenu		_popupEditFileOut;
	/** 文字列を引数とする値の編集用ポップアップメニュー **/
	private JPopupMenu		_popupEditString;
	/** パブリッシュ宛先を引数とする値の編集用ポップアップメニュー **/
	private JPopupMenu		_popupEditPublish;
	/** サブスクライブ宛先を引数とする値の編集用ポップアップメニュー **/
	private JPopupMenu		_popupEditSubscribe;
	
	private boolean	_dataDropEnabled = false;

	/** データモデル **/
	private MacroSubFilterValuesEditModel	_dataModel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MacroSubFilterArgValueTablePane() {
		super();
		setAdjustAllColumnWidthOnSetModel(false);	// モデル更新時に列幅の自動調整は行わない。
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isDataDropEnabled() {
		return _dataDropEnabled;
	}
	
	public void setDataDropEnabled(boolean toEnable) {
		_dataDropEnabled = toEnable;
	}

	/**
	 * このコンポーネントに設定されている、ファイルパス表示用フォーマッターを返す。
	 * @return	<code>VirtualFilePathFormatter</code> オブジェクト
	 */
	public VirtualFilePathFormatter getPathFormatter() {
		return (_dataModel==null ? null : _dataModel.getFormatter());
	}

	/**
	 * このコンポーネントに設定されている、データの基準パスを返す。
	 * @return	基準パスを示す抽象パス、もしくは <tt>null</tt>
	 */
	public VirtualFile getBasePath() {
		return (_dataModel==null ? null : _dataModel.getExecDefParentDirectory());
	}

	/**
	 * 指定された抽象パスを、表示用パス文字列に整形する。
	 * @return	整形後のパス文字列
	 */
	public String formatPathString(VirtualFile file) {
		if (_dataModel != null) {
			return _dataModel.formatPathString(file);
		}
		else {
			VirtualFilePathFormatter vfFormatter = getPathFormatter();
			if (vfFormatter != null) {
				String path = vfFormatter.formatPath(file);
				if (path != null)
					return path;
			}

			VirtualFile vfBasePath = getBasePath();
			if (vfBasePath != null && file.isDescendingFrom(vfBasePath)) {
				return file.relativePathFrom(vfBasePath, Files.CommonSeparatorChar);
			} else {
				return file.toString();
			}
		}
	}

	/**
	 * 引数設定の値から、このコンポーネントにおけるサブフィルタ引数表示用テキストを取得する。
	 * @param arg	表示対象引数
	 * @return	整形された文字列
	 */
	protected String getDisplayArgumentValueText(IModuleArgConfig arg) {
//		// modified @since 3.1.0
//		if (arg == null)
//			return "";
//		
//		if (arg.getOutToTempEnabled()) {
//			// テンポラリ出力指定
//			return RunnerMessages.getInstance().MExecDefArgTable_DispFileVarTemporary;
//		}
//		
//		Object value = arg.getValue();
//		if (value instanceof ModuleArgID) {
//			//--- 引数キー
//			return ModuleArgID.formatShortDisplayString((ModuleArgID)value);
//		}
//		else if (value instanceof ModuleArgConfig) {
//			//--- 引数定義の参照
////			return MacroFilterEditModel.formatLinkArgNo(((ModuleArgConfig)value).getArgNo());
//			return MacroFilterEditModel.formatLinkArgNo((ModuleArgConfig)value);
//		}
//		else if (value instanceof VirtualFile) {
//			return formatPathString((VirtualFile)value);
//		}
//		else if (value instanceof IMExecArgParam) {
//			if (value instanceof MExecArgTempFile) {
//				return RunnerMessages.getInstance().MExecDefArgTable_DispFileVarTemporary;
//			}
//			else if (value instanceof MExecArgDirectory) {
//				return RunnerMessages.getInstance().MExecDefArgTable_DispFileVarDirectory;
//			}
//			else if (value instanceof MExecArgCsvFile) {
//				return RunnerMessages.getInstance().MExecDefArgTable_DispFileVarCsv;
//			}
//			else if (value instanceof MExecArgXmlFile) {
//				return RunnerMessages.getInstance().MExecDefArgTable_DispFileVarXml;
//			}
//			else if (value instanceof MExecArgTextFile) {
//				return RunnerMessages.getInstance().MExecDefArgTable_DispFileVarText;
//			}
//			else if (value instanceof MExecArgString) {
//				return RunnerMessages.getInstance().MExecDefArgTable_DispStringVar;
//			}
//			else if (value instanceof MExecArgPublish) {
//				return RunnerMessages.getInstance().MExecDefArgTable_DispMqttPubAddrVar;
//			}
//			else if (value instanceof MExecArgSubscribe) {
//				return RunnerMessages.getInstance().MExecDefArgTable_DispMqttSubAddrVar;
//			}
//			else {
//				return value.getClass().getName();
//			}
//		}
//		else {
//			return (value==null ? "" : value.toString());
//		}
		
		if (_dataModel != null) {
			return getDisplayArgumentValueText(_dataModel, _dataModel.getModuleData(), arg);
		}
		else {
			VirtualFilePathFormatter vfFormatter = getPathFormatter();
			return getDisplayArgumentValueText((vfFormatter==null ? getBasePath() : vfFormatter), null, arg);
		}
	}

	/**
	 * 引数設定の値から、サブフィルタ引数表示用テキストを取得する。
	 * @param formatter	パスをフォーマットするためのオブジェクト
	 * @param module	引数が属するモジュール
	 * @param arg		表示対象引数
	 * @return	整形された文字列
	 * @since 3.1.0
	 */
	static public String getDisplayArgumentValueText(Object formatter, ModuleRuntimeData module, IModuleArgConfig arg) {
		if (arg == null)
			return "";
		
		if (arg.getOutToTempEnabled()) {
			// テンポラリ出力指定
			return RunnerMessages.getInstance().MExecDefArgTable_DispFileVarTemporary;
		}
		
		Object value = arg.getValue();
		if (value instanceof ModuleArgID) {
			//--- 引数キー
			return ModuleArgID.formatShortDisplayString((ModuleArgID)value);
		}
		else if (value instanceof IModuleArgConfig) {
			//--- 引数定義の参照
			return MacroFilterEditModel.formatLinkArgNo((IModuleArgConfig)value);
		}
		else if (value instanceof VirtualFile) {
			VirtualFile targetFile = (VirtualFile)value;
			if (formatter instanceof MacroSubFilterValuesEditModel) {
				// 編集モデルの機能に委譲
				return ((MacroSubFilterValuesEditModel)formatter).formatPathString(targetFile);
			}
			else if (formatter instanceof MacroFilterEditModel) {
				//--- 引数が属するモジュール内のファイルであれば、フィルタ名を先頭とする相対パスで表示
				if (module != null && module.isExistExecDefDirectory()) {
					VirtualFile vfBase = module.getExecDefParentDirectory();
					if (targetFile.isDescendingFrom(vfBase)) {
						return targetFile.relativePathFrom(vfBase, Files.CommonSeparatorChar);
					}
				}
				//--- 変換されなかった場合は、編集モデルの機能に委譲
				return ((MacroFilterEditModel)formatter).formatLocalFilePath(targetFile);
			}
			else if (formatter instanceof VirtualFilePathFormatter) {
				// 指定されたフォーマッターで変換
				String path = ((VirtualFilePathFormatter)formatter).formatPath(targetFile, Files.CommonSeparatorChar);
				if (path == null) {
					// 変換できなかった場合は、そのままのパスとする
					path = targetFile.getPath();
				}
				return path;
			}
			else if (formatter instanceof VirtualFile) {
				// 指定されたフォーマッターを基準パスとして相対パスに変換
				VirtualFile vfBase = (VirtualFile)formatter;
				if (targetFile.isDescendingFrom(vfBase)) {
					return targetFile.relativePathFrom(vfBase, Files.CommonSeparatorChar);
				} else {
					return targetFile.getPath();
				}
			}
			else {
				//--- 引数が属するモジュール内のファイルであれば、フィルタ名を先頭とする相対パスで表示
				if (module != null && module.isExistExecDefDirectory()) {
					VirtualFile vfBase = module.getExecDefParentDirectory();
					if (targetFile.isDescendingFrom(vfBase)) {
						return targetFile.relativePathFrom(vfBase, Files.CommonSeparatorChar);
					}
				}
				//--- 変換されなかった場合は、そのまま
				return targetFile.getPath();
			}
		}
		else if (value instanceof IMExecArgParam) {
			if (value instanceof MExecArgTempFile) {
				return RunnerMessages.getInstance().MExecDefArgTable_DispFileVarTemporary;
			}
			else if (value instanceof MExecArgDirectory) {
				return RunnerMessages.getInstance().MExecDefArgTable_DispFileVarDirectory;
			}
			else if (value instanceof MExecArgCsvFile) {
				return RunnerMessages.getInstance().MExecDefArgTable_DispFileVarCsv;
			}
			else if (value instanceof MExecArgXmlFile) {
				return RunnerMessages.getInstance().MExecDefArgTable_DispFileVarXml;
			}
			else if (value instanceof MExecArgTextFile) {
				return RunnerMessages.getInstance().MExecDefArgTable_DispFileVarText;
			}
			else if (value instanceof MExecArgString) {
				return RunnerMessages.getInstance().MExecDefArgTable_DispStringVar;
			}
			else if (value instanceof MExecArgPublish) {
				return RunnerMessages.getInstance().MExecDefArgTable_DispMqttPubAddrVar;
			}
			else if (value instanceof MExecArgSubscribe) {
				return RunnerMessages.getInstance().MExecDefArgTable_DispMqttSubAddrVar;
			}
			else {
				return value.getClass().getName();
			}
		}
		else {
			return (value==null ? "" : value.toString());
		}
	}

//	/**
//	 * 指定された引数定義を持つテーブルモデルを設定する。
//	 * このメソッドでは、既存の引数設定は破棄される。
//	 * @param details	引数定義の配列
//	 */
//	public void updateTableModelByArgDetails(ModuleArgDetail[] details) {
//		MacroFilterArgValueTableModel newModel = createDefaultTableModel();
//		if (details != null && details.length > 0) {
//			for (ModuleArgDetail det : details) {
//				newModel.addRow(det.type(), det.description(), null);
//			}
//		}
//		setTableModel(newModel);
//	}
//
//	/**
//	 * 指定された引数設定を持つテーブルモデルを設定する。
//	 * このメソッドでは、既存の引数設定は破棄される。
//	 * @param data	引数設定の配列
//	 */
//	public void updateTableModelByArgData(ModuleArgData[] data) {
//		MacroFilterArgValueTableModel newModel = createDefaultTableModel();
//		if (data != null && data.length > 0) {
//			for (ModuleArgData item : data) {
//				newModel.addRow(item.getType(), item.getDescription(), item.getValue());
//			}
//		}
//		setTableModel(newModel);
//	}

	/**
	 * このコンポーネントのデータモデルを取得する。
	 */
	public MacroSubFilterValuesEditModel getDataModel() {
		return _dataModel;
	}

	/**
	 * このコンポーネントに、新しいデータモデルを設定する。
	 * @param newModel	新しいデータモデルとする <code>MacroSubFilterValuesEditModel</code> オブジェクト、もしくは <tt>null</tt>
	 * @return	データモデルが変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws ClassCastException	引数のインスタンスが <code>MacroSubFilterValuesEditModel</code> の派生ではない場合
	 */
	public boolean setDataModel(IFilterValuesEditModel newModel) {
		if (newModel == _dataModel) {
			// no changes
			return false;
		}

		// データモデルの更新
		_dataModel = (MacroSubFilterValuesEditModel)newModel;
		
		// テーブルの列幅保存
		int[] colwidth = null;
		if (getTableComponent().getColumnCount() > 0) {
			ModuleArgTable table = getTableComponent();
			int numCols = table.getColumnCount();
			colwidth = new int[numCols];
			TableColumnModel colmodel = table.getColumnModel();
			for (int col = 0; col < numCols; col++) {
				colwidth[col] = colmodel.getColumn(col).getPreferredWidth();
			}
		}
		
		// テーブルモデルの更新
		setTableModel(_dataModel==null ? new EmptyMacroSubFilterArgValueTableModel() : _dataModel.getEditorTableModel());
		
		// テーブルの列幅復元
		if (colwidth != null) {
			TableColumnModel colmodel = getTableComponent().getColumnModel();
			int numCols = Math.min(colmodel.getColumnCount(), colwidth.length);
			for (int col = 0; col < numCols; col++) {
				TableColumn tc = colmodel.getColumn(col);
				if (tc.getResizable()) {
					// 列幅変更可能な列のみ、幅を元に戻す
					tc.setPreferredWidth(colwidth[col]);
				}
			}
		}

		// 更新完了
		return true;
	}

	@Override
	public IMacroSubFilterArgValueTableModel getTableModel() {
		return (IMacroSubFilterArgValueTableModel)super.getTableModel();
	}

	@Override
	public void setTableModel(IModuleArgTableModel newModel) {
		if (!(newModel instanceof IMacroSubFilterArgValueTableModel)) {
			throw new IllegalArgumentException("'newModel' is not IMacroSubFilterArgValueTableModel object.");
		}
		super.setTableModel(newModel);
	}

	/**
	 * 指定された引数インデックスに対応する箇所に、入力フォーカスを設定する。
	 * インデックスが適切ではない場合、このコンポーネントにフォーカスを設定する。
	 * @param argIndex	引数インデックス
	 */
	public void setFocusToArgument(int argIndex) {
		if (argIndex >= 0 && argIndex < getTableComponent().getRowCount()) {
			getTableComponent().getSelectionModel().setSelectionInterval(argIndex, argIndex);
			scrollRowToVisible(argIndex);
		}
		else {
			getTableComponent().clearSelection();
		}
		getTableComponent().setFocus();
	}

	/**
	 * テーブルのカラム幅を初期化する。
	 * この初期化では、説明列と値列を、表示可能領域内で半々になるように設定する。
	 */
	public void initialAllTableColumnWidth() {
		int viewWidth;
		Container ct = getTableComponent().getParent();
		if (ct instanceof JViewport) {
			JScrollPane sc = (JScrollPane)ct.getParent();
			viewWidth = sc.getViewportBorderBounds().width;
		} else {
			viewWidth = getTableComponent().getWidth();
		}
		if (viewWidth <= 0) {
			return;		// 表示領域なし
		}
		TableColumnModel colmodel = getTableComponent().getColumnModel();
		int totalColWidth = colmodel.getTotalColumnWidth();
		int delta = viewWidth - totalColWidth;
		int numResizableCols = 0;
		int resizableTotalColumnWidth = 0;
		for (int col = 0; col < colmodel.getColumnCount(); col++) {
			TableColumn tc = colmodel.getColumn(col);
			if (tc.getResizable()) {
				numResizableCols++;
				resizableTotalColumnWidth += tc.getPreferredWidth();
			}
		}
		if (resizableTotalColumnWidth <= 0)
			return;		// 変更可能な列なし
		
		resizableTotalColumnWidth += delta;
		if (Math.abs(resizableTotalColumnWidth) <= numResizableCols) {
			// 幅調整はできないので、そのまま
			return;
		}
		
		delta = resizableTotalColumnWidth / numResizableCols;
		for (int col = 0; col < colmodel.getColumnCount(); col++) {
			TableColumn tc = colmodel.getColumn(col);
			if (tc.getResizable()) {
				numResizableCols--;
				if (numResizableCols > 0) {
					tc.setPreferredWidth(delta);
					resizableTotalColumnWidth -= delta;
				}
				else {
					// 終端のサイズには余りを設定して、ループ終了
					tc.setPreferredWidth(resizableTotalColumnWidth);
					break;
				}
			}
		}
	}

	@Override
	public void onButtonEdit(ActionEvent ae) {
		stopTableCellEditing();
		int selected = getSelectedRow();
		if (selected < 0 || _dataModel == null) {
			// no selection
			return;
		}
		
		// 引数属性が未定義の場合は警告
		ModuleArgType argtype = _dataModel.getArgumentType(selected);
		if (argtype == null || ModuleArgType.NONE == argtype) {
			String errmsg = "($" + Integer.valueOf(selected+1) + ") " + RunnerMessages.getInstance().msgMExecDefArgNothingAttr;
			Application.showErrorMessage(this, errmsg);
			return;
		}
		
		// 引数のタイプによって設定を変更
		JPopupMenu popupMenu;
		if (ModuleArgType.IN == argtype) {
			// for input file
			popupMenu = getEditFileInPopupMenu();
		}
		else if (ModuleArgType.OUT == argtype) {
			// for output file
			popupMenu = getEditFileOutPopupMenu();
		}
		else if (ModuleArgType.PUB == argtype) {
			// for publish address
			popupMenu = getEditPublishPopupMenu();
		}
		else if (ModuleArgType.SUB == argtype) {
			// for subscribe address
			popupMenu = getEditSubscribePopupMenu();
		}
		else {
			// for string
			popupMenu = getEditStringPopupMenu();
		}
		MenuToggleButton btn = (MenuToggleButton)ae.getSource();
		popupMenu.show(btn, 0, btn.getHeight());
	}

	@Override
	public void onButtonDelete(ActionEvent ae) {
		// 現在選択されている行の値列の内容をクリアする
		stopTableCellEditing();
		int selected = getSelectedRow();
		if (selected >= 0 && _dataModel != null) {
			_dataModel.setArgumentValue(selected, null);
			updateButtons();
		}
	}

	@Override
	public void onButtonAdd(ActionEvent ae) {
		// no entry
	}

	@Override
	public void onButtonDown(ActionEvent ae) {
		// no entry
	}

	@Override
	public void onButtonUp(ActionEvent ae) {
		// no entry
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	@Override
	protected void onTableModelChanged(TableModelEvent event) {
		updateButtons();

		// 行数が変更された場合は、行ヘッダーの内容も更新する
		RowHeaderModel rowModel = getTableComponent().getTableRowHeaderModel();
		int numRows = getTableComponent().getRowCount();
		if (rowModel.getSize() != numRows) {
			rowModel.setSize(numRows);
		}
	}
	
	@Override
	protected void onTableSelectionChanged(ListSelectionEvent event) {
		super.onTableSelectionChanged(event);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected IMacroSubFilterArgValueTableModel createDefaultTableModel() {
		// 初期テーブルモデルは、要素が空のモデル
		return new EmptyMacroSubFilterArgValueTableModel();
	}

	@Override
	protected MacroSubFilterArgValueTable createTable() {
		return new MacroSubFilterArgValueTable();
	}

	/**
	 * [IN]引数値を他フィルタの[OUT]引数から選択するメニューからの呼び出し
	 * @param rowIndex	編集対象の行インデックス
	 * @param event		メニューアクションのイベント
	 */
	protected void onEditMenuReferOutArgument(int rowIndex, ActionEvent event) {
		// 引数の取得
		MacroFilterEditModel editModel = _dataModel.getEditModel();
		ModuleRuntimeData moduledata = _dataModel.getModuleData();
		ModuleArgConfig argdata = moduledata.getArgument(rowIndex);
		
		// チェック
		if (argdata.isFixedValue()) {
			// 変更不可
			ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgUneditableArgument);
		}
		if (!editModel.hasReferenceableSubFilterArguments(moduledata, argdata)) {
			// 参照可能引数なし
			ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgNoReferOutArgument);
			return;
		}
		
		// 参照可能引数の選択
		MacroSubFilterArgReferDialog dlg;
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Dialog)
			dlg = new MacroSubFilterArgReferDialog((Dialog)w, editModel, moduledata, argdata);
		else if (w instanceof Frame)
			dlg = new MacroSubFilterArgReferDialog((Frame)w, editModel, moduledata, argdata);
		else
			dlg = new MacroSubFilterArgReferDialog((Frame)null, editModel, moduledata, argdata);
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			return;		// user canceled
		}
		ModuleArgID refargid = dlg.getSelectedArgument();
		
		_dataModel.setArgumentValue(rowIndex, refargid);
	}

	/**
	 * [SUB]引数値を他フィルタの[PUB]引数から選択するメニューからの呼び出し
	 * @param rowIndex	編集対象の行インデックス
	 * @param event		メニューアクションのイベント
	 */
	protected void onEditMenuReferPubArgument(int rowIndex, ActionEvent event) {
		// 引数の取得
		MacroFilterEditModel editModel = _dataModel.getEditModel();
		ModuleRuntimeData moduledata = _dataModel.getModuleData();
		ModuleArgConfig argdata = moduledata.getArgument(rowIndex);
		
		// チェック
		if (argdata.isFixedValue()) {
			// 変更不可
			ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgUneditableArgument);
		}
		if (!editModel.hasReferenceableSubFilterArguments(moduledata, argdata)) {
			// 参照可能引数なし
			ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgNoReferPubArgument);
			return;
		}
		
		// 参照可能引数の選択
		MacroSubFilterArgReferDialog dlg;
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Dialog)
			dlg = new MacroSubFilterArgReferDialog((Dialog)w, editModel, moduledata, argdata);
		else if (w instanceof Frame)
			dlg = new MacroSubFilterArgReferDialog((Frame)w, editModel, moduledata, argdata);
		else
			dlg = new MacroSubFilterArgReferDialog((Frame)null, editModel, moduledata, argdata);
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			return;		// user canceled
		}
		ModuleArgID refargid = dlg.getSelectedArgument();
		
		_dataModel.setArgumentValue(rowIndex, refargid);
	}

	/**
	 * 引数値をフィルタ定義引数から選択するメニューからの呼び出し
	 * @param rowIndex	編集対象の行インデックス
	 * @param event		メニューアクションのイベント
	 */
	protected void onEditMenuReferDefinitionArgument(int rowIndex, ActionEvent event) {
		// 引数の取得
		MacroFilterEditModel editModel = _dataModel.getEditModel();
		ModuleArgConfig argdata = _dataModel.getArgument(rowIndex);
		
		// チェック
		if (argdata.isFixedValue()) {
			// 変更不可
			ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgUneditableArgument);
		}
		if (!editModel.hasReferenceableMExecDefArguments(argdata)) {
			// 参照可能引数なし
			int ret = JOptionPane.showConfirmDialog(this,
					RunnerMessages.getInstance().confirmAddReferArgToDefinition,
					RunnerMessages.getInstance().MacroFilterDefArgReferDlg_Title,
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (ret == JOptionPane.YES_OPTION) {
				// フィルタ定義引数を生成
				FilterArgEditModel newarg = new FilterArgEditModel(argdata.getArgNo(), argdata.getType(), argdata.getDescription(), argdata.getParameterType());
				newarg.setValue(argdata.getParameterType());
				
				// フィルタ定義引数へ追加
				editModel.addMExecDefArgument(newarg);
				FilterArgEditModel defarg = editModel.getMExecDefArgument(editModel.getMExecDefArgumentCount()-1);
				
				// 追加されたフィルタ定義引数を参照
				_dataModel.setArgumentValue(rowIndex, defarg);
			}
			return;
		}
		
		MacroFilterDefArgReferDialog dlg;
		Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Dialog)
			dlg = new MacroFilterDefArgReferDialog((Dialog)w, editModel, argdata);
		else if (w instanceof Frame)
			dlg = new MacroFilterDefArgReferDialog((Frame)w, editModel, argdata);
		else
			dlg = new MacroFilterDefArgReferDialog((Frame)null, editModel, argdata);
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			return;		// user canceled
		}
		IModuleArgConfig refarg = dlg.getSelectedArgument();
		
		_dataModel.setArgumentValue(rowIndex, refarg);
	}

	/**
	 * 引数定義をフィルタ定義引数に追加するメニューからの呼び出し
	 * @param rowIndex	編集対象の行インデックス
	 * @param event		メニューアクションのイベント
	 */
	protected void onEditMenuAddDefinitionArgument(int rowIndex, ActionEvent event) {
		// 引数の取得
		MacroFilterEditModel editModel = _dataModel.getEditModel();
		ModuleArgConfig argdata = _dataModel.getModuleData().getArgument(rowIndex);
		
		// チェック
		if (argdata.isFixedValue()) {
			// 変更不可
			ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgUneditableArgument);
		}
		
		// フィルタ定義引数を生成
		FilterArgEditModel newarg = new FilterArgEditModel(argdata.getArgNo(), argdata.getType(), argdata.getDescription(), argdata.getParameterType());
		newarg.setValue(argdata.getParameterType());
		
		// フィルタ定義引数へ追加
		editModel.addMExecDefArgument(newarg);
		FilterArgEditModel defarg = editModel.getMExecDefArgument(editModel.getMExecDefArgumentCount()-1);
		
		// 追加されたフィルタ定義引数を参照
		_dataModel.setArgumentValue(rowIndex, defarg);
	}
	
	protected void onEditMenuItemAction(ActionEvent ae) {
		String command = ae.getActionCommand();
		int rowIndex = getSelectedRow();
		
		if (MIEDIT_INFILE_CHOOSE.equals(command)) {
			// 入力ファイルの選択
			File selectedFile = FileChooserManager.chooseArgumentDirOrFile(this,
					FileChooserManager.getInitialDocumentFile(_dataModel.getArgumentFileValue(rowIndex)));
			if (selectedFile != null) {
				// check, cannot be specified a directory
				if (selectedFile.isDirectory()) {
					Application.showErrorMessage(RunnerMessages.getInstance().msgMExecDefArgCannotUseDir);
					return;
				}
				// setup argument for the specified file
				FileChooserManager.setLastChooseDocumentFile(selectedFile);
				VirtualFile file = ModuleFileManager.fromJavaFile(selectedFile).getNormalizedFile().getAbsoluteFile();
				_dataModel.setArgumentValue(rowIndex, file);
			}
		}
		else if (MIEDIT_INFILE_OUTFILE.equals(command)) {
			// 他フィルタの[OUT]引数選択
			onEditMenuReferOutArgument(rowIndex, ae);
		}
		else if (MIEDIT_OUTFILE_TEMP.equals(command)) {
			// 出力ファイルのテンポラリ出力指定
			_dataModel.setArgumentOutToTempEnabled(rowIndex, true);
		}
		else if (MIEDIT_STR_DIRECT.equals(command) || MIEDIT_PUB_DIRECT.equals(command) || MIEDIT_SUB_DIRECT.equals(command)) {
			// [STR][PUB][SUB] 文字列の直接編集
			getTableComponent().requestFocusInWindow();
			getTableComponent().editCellAt(rowIndex, getTableModel().valueColumnIndex());
	        Component editorComponent = getTableComponent().getEditorComponent();
	        if (editorComponent != null) {
	        	if (!editorComponent.hasFocus()) {
	        		if (!editorComponent.requestFocusInWindow()) {
	        			editorComponent.requestFocus();
	        		}
	        	}
	        	if (editorComponent instanceof JTextComponent) {
	        		JTextComponent textComponent = (JTextComponent)editorComponent;
	        		textComponent.selectAll();
	        	}
	        }
		}
		else if (MIEDIT_INFILE_REFER.equals(command)) {
			// [IN]フィルタ定義引数の参照
			onEditMenuReferDefinitionArgument(rowIndex, ae);
		}
		else if (MIEDIT_OUTFILE_REFER.equals(command)) {
			// [OUT]フィルタ定義引数の参照
			onEditMenuReferDefinitionArgument(rowIndex, ae);
		}
		else if (MIEDIT_STR_REFER.equals(command)) {
			// [STR]フィルタ定義引数の参照
			onEditMenuReferDefinitionArgument(rowIndex, ae);
		}
		else if (MIEDIT_PUB_REFER.equals(command)) {
			// [PUB]フィルタ定義引数の参照
			onEditMenuReferDefinitionArgument(rowIndex, ae);
		}
		else if (MIEDIT_SUB_PUB.equals(command)) {
			// 他フィルタの[PUB]引数参照
			onEditMenuReferPubArgument(rowIndex, ae);
		}
		else if (MIEDIT_SUB_REFER.equals(command)) {
			// [SUB]フィルタ定義引数の参照
			onEditMenuReferDefinitionArgument(rowIndex, ae);
		}
		else if (MIEDIT_INFILE_CREATEARG.equals(command)) {
			// [IN]フィルタ定義引数の追加
			onEditMenuAddDefinitionArgument(rowIndex, ae);
		}
		else if (MIEDIT_OUTFILE_CREATEARG.equals(command)) {
			// [OUT]フィルタ定義引数の追加
			onEditMenuAddDefinitionArgument(rowIndex, ae);
		}
		else if (MIEDIT_STR_CREATEARG.equals(command)) {
			// [STR]フィルタ定義引数の追加
			onEditMenuAddDefinitionArgument(rowIndex, ae);
		}
		else if (MIEDIT_PUB_CREATEARG.equals(command)) {
			// [PUB]フィルタ定義引数の追加
			onEditMenuAddDefinitionArgument(rowIndex, ae);
		}
		else if (MIEDIT_SUB_CREATEARG.equals(command)) {
			// [SUB]フィルタ定義引数の追加
			onEditMenuAddDefinitionArgument(rowIndex, ae);
		}
	}
	
	protected EditMenuItemAction getEditMenuItemAction(String command) {
		if (_mapEditMenuActions == null) {
			_mapEditMenuActions = new HashMap<String, EditMenuItemAction>();
			//--- choose file
			_mapEditMenuActions.put(MIEDIT_INFILE_CHOOSE,
					new EditMenuItemAction(MIEDIT_INFILE_CHOOSE,
							RunnerMessages.getInstance().MExecDefArgTable_EditMenuFileChoose));
			//--- select [OUT] argument
			_mapEditMenuActions.put(MIEDIT_INFILE_OUTFILE,
					new EditMenuItemAction(MIEDIT_INFILE_OUTFILE,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuOutArgChoose));
			//--- select [PUB] argument
			_mapEditMenuActions.put(MIEDIT_SUB_PUB,
					new EditMenuItemAction(MIEDIT_SUB_PUB,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuPubArgChoose));
			//--- output temporary
			_mapEditMenuActions.put(MIEDIT_OUTFILE_TEMP,
					new EditMenuItemAction(MIEDIT_OUTFILE_TEMP,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuOutToTemp));
			//--- String direct edit
			_mapEditMenuActions.put(MIEDIT_STR_DIRECT,
					new EditMenuItemAction(MIEDIT_STR_DIRECT,
							RunnerMessages.getInstance().MExecDefArgTable_EditMenuStringDirect));
			//--- Publish direct edit
			_mapEditMenuActions.put(MIEDIT_PUB_DIRECT,
					new EditMenuItemAction(MIEDIT_PUB_DIRECT,
							RunnerMessages.getInstance().MExecDefArgTable_EditMenuMqttAddrDirect));
			//--- Subscribe direct edit
			_mapEditMenuActions.put(MIEDIT_SUB_DIRECT,
					new EditMenuItemAction(MIEDIT_SUB_DIRECT,
							RunnerMessages.getInstance().MExecDefArgTable_EditMenuMqttAddrDirect));
			//--- Refer definition argument(IN)
			_mapEditMenuActions.put(MIEDIT_INFILE_REFER,
					new EditMenuItemAction(MIEDIT_INFILE_REFER,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuFilterArgRefer));
			//--- Refer definition argument(OUT)
			_mapEditMenuActions.put(MIEDIT_OUTFILE_REFER,
					new EditMenuItemAction(MIEDIT_OUTFILE_REFER,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuFilterArgRefer));
			//--- Refer definition argument(STR)
			_mapEditMenuActions.put(MIEDIT_STR_REFER,
					new EditMenuItemAction(MIEDIT_STR_REFER,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuFilterArgRefer));
			//--- Refer definition argument(PUB)
			_mapEditMenuActions.put(MIEDIT_PUB_REFER,
					new EditMenuItemAction(MIEDIT_PUB_REFER,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuFilterArgRefer));
			//--- Refer definition argument(SUB)
			_mapEditMenuActions.put(MIEDIT_SUB_REFER,
					new EditMenuItemAction(MIEDIT_SUB_REFER,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuFilterArgRefer));
			//--- Add to definition argument(IN)
			_mapEditMenuActions.put(MIEDIT_INFILE_CREATEARG,
					new EditMenuItemAction(MIEDIT_INFILE_CREATEARG,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuCreateReferArg));
			//--- Add to definition argument(OUT)
			_mapEditMenuActions.put(MIEDIT_OUTFILE_CREATEARG,
					new EditMenuItemAction(MIEDIT_OUTFILE_CREATEARG,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuCreateReferArg));
			//--- Add to definition argument(STR)
			_mapEditMenuActions.put(MIEDIT_STR_CREATEARG,
					new EditMenuItemAction(MIEDIT_STR_CREATEARG,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuCreateReferArg));
			//--- Add to definition argument(PUB)
			_mapEditMenuActions.put(MIEDIT_PUB_CREATEARG,
					new EditMenuItemAction(MIEDIT_PUB_CREATEARG,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuCreateReferArg));
			//--- Add to definition argument(SUB)
			_mapEditMenuActions.put(MIEDIT_SUB_CREATEARG,
					new EditMenuItemAction(MIEDIT_SUB_CREATEARG,
							RunnerMessages.getInstance().MacroSubFilterArgValueTable_EditMenuCreateReferArg));
		}
		return _mapEditMenuActions.get(command);
	}
	
	protected JMenuItem createEditMenuItem(String command) {
		Action action = getEditMenuItemAction(command);
		JMenuItem item = new JMenuItem(action);
		return item;
	}
	
	protected JPopupMenu getEditFileInPopupMenu() {
		if (_popupEditFileIn == null) {
			JPopupMenu menu = new JPopupMenu();
			//--- choose file
			menu.add(createEditMenuItem(MIEDIT_INFILE_CHOOSE));
			//---
			menu.addSeparator();
			//--- [OUT] link
			menu.add(createEditMenuItem(MIEDIT_INFILE_OUTFILE));
			//---
			menu.addSeparator();
			//--- refer
			menu.add(createEditMenuItem(MIEDIT_INFILE_REFER));
			//--- create filter definition argument
			menu.add(createEditMenuItem(MIEDIT_INFILE_CREATEARG));
			
			_popupEditFileIn = menu;
			
			menu.addPopupMenuListener(((MenuToggleButton)getEditButton()).getToggleOffPopupMenuListener());
		}
		return _popupEditFileIn;
	}
	
	protected JPopupMenu getEditFileOutPopupMenu() {
		if (_popupEditFileOut == null) {
			JPopupMenu menu = new JPopupMenu();
//			// モジュール実行定義の[OUT]引数には、固定の出力先は指定できないものとする。
//			//--- choose file
//			menu.add(createEditMenuItem(MIEDIT_FILE_CHOOSE));
//			//---
//			menu.addSeparator();
			//--- Temp
			menu.add(createEditMenuItem(MIEDIT_OUTFILE_TEMP));
			//---
			menu.addSeparator();
			//--- refer
			menu.add(createEditMenuItem(MIEDIT_OUTFILE_REFER));
			//--- create filter definition argument
			menu.add(createEditMenuItem(MIEDIT_OUTFILE_CREATEARG));
			
			_popupEditFileOut = menu;
			
			menu.addPopupMenuListener(((MenuToggleButton)getEditButton()).getToggleOffPopupMenuListener());
		}
		return _popupEditFileOut;
	}
	
	protected JPopupMenu getEditStringPopupMenu() {
		if (_popupEditString == null) {
			JPopupMenu menu = new JPopupMenu();
			//--- direct input
			menu.add(createEditMenuItem(MIEDIT_STR_DIRECT));
			//---
			menu.addSeparator();
			//--- refer
			menu.add(createEditMenuItem(MIEDIT_STR_REFER));
			//--- create filter definition argument
			menu.add(createEditMenuItem(MIEDIT_STR_CREATEARG));
			
			_popupEditString = menu;
			
			menu.addPopupMenuListener(((MenuToggleButton)getEditButton()).getToggleOffPopupMenuListener());
		}
		return _popupEditString;
	}
	
	protected JPopupMenu getEditPublishPopupMenu() {
		if (_popupEditPublish == null) {
			JPopupMenu menu = new JPopupMenu();
			//--- direct input
			menu.add(createEditMenuItem(MIEDIT_PUB_DIRECT));
			//---
			menu.addSeparator();
			//--- refer
			menu.add(createEditMenuItem(MIEDIT_PUB_REFER));
			//--- create filter definition argument
			menu.add(createEditMenuItem(MIEDIT_PUB_CREATEARG));
			
			_popupEditString = menu;
			
			menu.addPopupMenuListener(((MenuToggleButton)getEditButton()).getToggleOffPopupMenuListener());
		}
		return _popupEditString;
	}
	
	protected JPopupMenu getEditSubscribePopupMenu() {
		if (_popupEditSubscribe == null) {
			JPopupMenu menu = new JPopupMenu();
			//--- direct input
			menu.add(createEditMenuItem(MIEDIT_SUB_DIRECT));
			//---
			menu.addSeparator();
			//--- [OUT] link
			menu.add(createEditMenuItem(MIEDIT_SUB_PUB));
			//---
			menu.addSeparator();
			//--- refer
			menu.add(createEditMenuItem(MIEDIT_SUB_REFER));
			//--- create filter definition argument
			menu.add(createEditMenuItem(MIEDIT_SUB_CREATEARG));
			
			_popupEditSubscribe = menu;
			
			menu.addPopupMenuListener(((MenuToggleButton)getEditButton()).getToggleOffPopupMenuListener());
		}
		return _popupEditSubscribe;
	}
	
	@Override
	protected void setupActions() {
		super.setupActions();
		
		// テーブルにファイルドロップリスナーを設定
		new DropTarget(getTableComponent(), DnDConstants.ACTION_COPY, _hDropDataToTable, true);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	protected class EditMenuItemAction extends AbMenuItemAction
	{
		public EditMenuItemAction(String command, String caption) {
			super(caption);
			setCommandKey(command);
		}

		public void actionPerformed(final ActionEvent ae) {
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					onEditMenuItemAction(ae);
				}
			});
		}
	}

	/**
	 * 引数の値列のセルレンダラー
	 */
	protected class ArgumentValueCellRenderer extends DefaultModuleArgTableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
														boolean isSelected, boolean hasFocus,
														int row, int column)
		{
			// 基本のレンダリング
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			// エラー発生時の背景色
			if (!isSelected) {
				if (table instanceof MacroSubFilterArgValueTable) {
					Object rawValue = ((MacroSubFilterArgValueTable)table).getRawValueAt(row, column);
					if (rawValue instanceof ModuleArgConfig) {
						ModuleArgConfig arg = (ModuleArgConfig)rawValue;
						if (arg.getUserData() != null || value==null || value.toString().length() <= 0) {
							// エラーオブジェクトが存在するか、表示値が空の場合は、エラーとみなす。
							comp.setBackground(CommonResources.DEF_BACKCOLOR_ERROR);
						}
					}
				}
			}
			
			return comp;
		}
	}
	
	protected class MacroSubFilterArgValueTable extends ModuleArgTable
	{
		private static final long serialVersionUID = 1L;

		public MacroSubFilterArgValueTable() {
			super();
		}

		@Override
		public Object getValueAt(int row, int column) {
			Object value = super.getValueAt(row, column);
			if (value instanceof IModuleArgConfig) {
				value = getDisplayArgumentValueText((IModuleArgConfig)value);
			}
			return value;
		}
		
		public Object getRawValueAt(int row, int column) {
			return super.getValueAt(row, column);
		}

		@Override
		public Component prepareEditor(TableCellEditor editor, int row, int column) {
			Component ec = super.prepareEditor(editor, row, column);
			Object value = super.getValueAt(row, column);
			if (!(value instanceof String)) {
				((JTextComponent)ec).setText("");
				((JTextComponent)ec).setCaretPosition(0);
			}
			return ec;
		}

		@Override
		public void editingStopped(ChangeEvent e) {
			super.editingStopped(e);
		}

		@Override
		public void setValueAt(Object value, int row, int column) {
			if (value == null) {
				super.setValueAt(value, row, column);
				return;
			}
			
			String strValue = value.toString();
			if (strValue.length() <= 0) {
				super.setValueAt(null, row, column);
				return;
			}
			
			// AADLマクロテンポラリ引数のチェック
			String strTempID = ssac.aadl.macro.command.AbstractTokenizer.getValidTemporaryID(strValue);
			if (strTempID != null) {
				if (strTempID.length() > 0) {
					//--- 正常なテンポラリ記述子はスルー
					super.setValueAt(strTempID, row, column);
					return;
				}
				else {
					//--- エラー
					ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgMacroSubFilterArgStringIllegal + "\n\"" + strValue + "\"");
					return;
				}
			}
			
			// AADLマクロ参照引数のチェック
			String[] strRefIDs = ssac.aadl.macro.command.AbstractTokenizer.getValidMacroArgumentID(strValue);
			if (strRefIDs != null) {
				if (strRefIDs.length <= 0) {
					//--- エラー
					ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgMacroSubFilterArgStringIllegal + "\n\"" + strValue + "\"");
					return;
				}
				else if (strRefIDs.length > 1) {
					//--- エラー
					ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgMacroSubFilterArgStringMultiRefer + "\n\"" + strValue + "\"");
					return;
				}
				String strID = ssac.aadl.macro.command.AbstractTokenizer.getReferencedIdentifier(strRefIDs[0]);
				int refID;
				try {
					refID = Integer.parseInt(strID);
				} catch (Throwable ex) {
					refID = -1;
				}
				if (refID < 0) {
					//--- エラー
					ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgMacroSubFilterArgStringIllegal + "\n\"" + strValue + "\"");
					return;
				}
				else if (refID < 1 || refID > _dataModel.getEditModel().getMExecDefArgumentCount()) {
					//--- 引数参照番号のエラー
					ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgMacroSubFilterArgStringNoReferArgNo + "\n\"" + strValue + "\"");
					return;
				}
				// 参照引数の取得
				IModuleArgConfig refarg = _dataModel.getEditModel().getMExecDefArgument(refID-1);
				if (refarg.getType() != _dataModel.getArgumentType(row)) {
					ModuleRunner.showErrorMessage(this, RunnerMessages.getInstance().msgMacroSubFilterArgStringMismatchArgType + "\n\"" + strValue + "\"");
					return;
				}
				
				// 参照引数の代入
				value = refarg;
			}
			
			// 入力文字列を受付
			super.setValueAt(value, row, column);
		}

		@Override
		public String getToolTipText(MouseEvent event) {
	        Point p = event.getPoint();
	        int hitColumnIndex = columnAtPoint(p);
	        int hitRowIndex = rowAtPoint(p);
	        if (hitColumnIndex != -1 && hitRowIndex != -1) {
	        	Object value = getRawValueAt(hitRowIndex, hitColumnIndex);
	        	if (value instanceof ModuleArgConfig) {
	        		Object errValue = ((ModuleArgConfig)value).getUserData();
	        		if (errValue instanceof FilterError) {
	        			String errmsg = ((FilterError)errValue).getErrorMessage();
	        			if (errmsg != null && errmsg.length() > 0) {
	        				return errmsg;
	        			}
	        		}
	        	}
	        }
	        
	        return super.getToolTipText(event);
		}

		@Override
		public void setModel(TableModel dataModel) {
			// モデルのセット
			super.setModel(dataModel);
			
			if (dataModel instanceof IMacroSubFilterArgValueTableModel) {
				// 値列固有の設定
				TableColumn colValue = getColumnModel().getColumn( ((IMacroSubFilterArgValueTableModel)dataModel).valueColumnIndex() );
				//--- setup cell renderer
				ArgumentValueCellRenderer valCellRenderer = createArgumentValueCellRenderer();
				if (valCellRenderer != null) {
					colValue.setCellRenderer(valCellRenderer);
				}
			}
		}

		@Override
		protected TableCellRenderer createModuleAttrCellRenderer() {
			return new StaticModuleArgTypeTableCellRenderer();
		}

		protected ArgumentValueCellRenderer createArgumentValueCellRenderer() {
			ArgumentValueCellRenderer renderer = new ArgumentValueCellRenderer();
			return renderer;
		}
	}
	
	/**
	 * データのドロップリスナー
	 */
	private final class ArgDataDropTargetListener extends DropTargetAdapter
	{
		private boolean isDataFlavorSupported(final DropTargetDragEvent dtde) {
			if (dtde.isDataFlavorSupported(VirtualFileTransferable.javaFileListFlavor)) {
				return true;
			}
			
			if (dtde.isDataFlavorSupported(VirtualFileTransferable.virtualFileListFlavor)) {
				return true;
			}
			
			// not supported
			return false;
		}
		
		private int getAcceptableRowIndex(Point location, ModuleArgTable table) {
			int row = table.rowAtPoint(location);
			//--- ドロップ可能は、[IN]実行時可変の行のみ
			if (0 <= row && row < table.getRowCount()) {
				if (_dataModel.getArgumentType(row)==ModuleArgType.IN && !_dataModel.isArgumentFixedValue(row)) {
					return row;
				} else {
					return (-1);
				}
			} else {
				return (-1);
			}
		}
		
		private int getAcceptableColumnIndex(Point location, ModuleArgTable table) {
			int col = table.columnAtPoint(location);
			//--- ドロップ可能は、全カラム
			if (0 <= col && col < table.getColumnCount()) {
				return col;
			} else {
				return (-1);
			}
		}
		
		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			if (!isDataDropEnabled()) {
				dtde.rejectDrag();
				return;
			}
			
			// サポートする DataFlavor のチェック
			if (!isDataFlavorSupported(dtde)) {
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
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			if (!isDataDropEnabled()) {
				dtde.rejectDrag();
				return;
			}
			
			if (isDataFlavorSupported(dtde)) {
				// Drop 位置のカラム判定
				ModuleArgTable table = getTableComponent();
				int row = getAcceptableRowIndex(dtde.getLocation(), table);
				int col = getAcceptableColumnIndex(dtde.getLocation(), table);
				if (row >= 0 && col >= 0) {
					dtde.acceptDrag(DnDConstants.ACTION_COPY);
					return;
				}
			}
			
			dtde.rejectDrag();
		}

		public void drop(DropTargetDropEvent dtde) {
			if (!isDataDropEnabled()) {
				dtde.rejectDrop();
				return;
			}
			
			// サポートする DataFlavor のチェック
			if (!dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// サポートされないデータ形式
				dtde.rejectDrop();
				return;
			}
			
			// ソースアクションにコピーが含まれていない場合は、許可しない
			if ((dtde.getSourceActions() & DnDConstants.ACTION_COPY) == 0) {
				// サポートされないドロップアクション
				dtde.rejectDrop();
				return;
			}

			// カラム位置の取得
			ModuleArgTable table = getTableComponent();
			int row = getAcceptableRowIndex(dtde.getLocation(), table);
			int col = getAcceptableColumnIndex(dtde.getLocation(), table);
			if (row < 0 || col < 0) {
				// 許容されない位置
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
			try {
				VirtualFile[] files = VirtualFileTransferable.getFilesFromTransferData(t);
				if (files != null && files.length > 0) {
					// 先頭のファイルのみを使用
					// check, cannot be specified a directory
					if (files[0].isDirectory()) {
						Application.showErrorMessage(RunnerMessages.getInstance().msgMExecDefArgCannotUseDir);
					} else {
						// setup argument for the specified file
						_dataModel.setArgumentValue(row, files[0].getAbsoluteFile().getNormalizedFile());
					}
				}
				// ドロップ完了
				dtde.dropComplete(true);
			}
			catch (Throwable ex) {
				AppLogger.error("Failed to drop to Table.", ex);
			}
			
			// drop を受け付けない
			dtde.rejectDrop();
		}
	}
}
