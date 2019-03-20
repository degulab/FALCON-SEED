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
 * @(#)AbMExecDefArgTablePane.java	3.1.0	2014/05/12
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)AbMExecDefArgTablePane.java	2.0.0	2012/11/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonResources;
import ssac.aadl.module.ModuleArgData;
import ssac.aadl.module.ModuleArgDetail;
import ssac.aadl.module.ModuleArgType;
import ssac.aadl.module.swing.table.AbModuleArgTablePane;
import ssac.aadl.module.swing.table.IModuleArgTableModel;
import ssac.aadl.module.swing.table.ModuleArgTable;
import ssac.aadl.module.swing.table.StaticModuleArgTypeTableCellRenderer;
import ssac.falconseed.file.VirtualFilePathFormatter;
import ssac.falconseed.file.VirtualFileTransferable;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgCsvFile;
import ssac.falconseed.module.args.MExecArgDirectory;
import ssac.falconseed.module.args.MExecArgPublish;
import ssac.falconseed.module.args.MExecArgString;
import ssac.falconseed.module.args.MExecArgSubscribe;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.args.MExecArgTextFile;
import ssac.falconseed.module.args.MExecArgXmlFile;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.Objects;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.swing.Application;
import ssac.util.swing.MenuToggleButton;
import ssac.util.swing.menu.AbMenuItemAction;

/**
 * モジュール実行定義用引数を表示もしくは設定するテーブルペインの共通実装。
 * <p>
 * このクラスの利用においては、コンストラクタによるインスタンス生成後、
 * 必ず {@link #initialComponent()} を呼び出すこと。
 * 
 * @version 3.1.0	2014/05/12
 */
public abstract class AbMExecDefArgTablePane extends AbModuleArgTablePane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** テーブルコンポーネントのドロップリスナー **/
	private AbModuleArgTableDropListener	_hDropDataToTable;

	/** 引数属性ごとのポップアップメニュー用のアクションマップ **/
	private Map<String, EditMenuItemAction> _mapEditMenuActions;

	/** ファイルパス表示用のフォーマッター **/
	private VirtualFilePathFormatter	_vfFormatter;
	/** ファイルパス表示の基準となるディレクトリ **/
	private VirtualFile	_vfBasePath;
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

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public AbMExecDefArgTablePane() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isValueDropEnabled() {
		return (_hDropDataToTable==null ? false : _hDropDataToTable.isDataDropEnabled());
	}
	
	public void setValueDropEnabled(boolean toEnable) {
		if (_hDropDataToTable != null) {
			_hDropDataToTable.setDataDropEnabled(toEnable);
		}
	}

	/**
	 * このコンポーネントに設定されている、ファイルパス表示用フォーマッターを返す。
	 * @return	<code>VirtualFilePathFormatter</code> オブジェクト
	 * @since 1.20
	 */
	public VirtualFilePathFormatter getPathFormatter() {
		return _vfFormatter;
	}

	/**
	 * このコンポーネントに、新しいファイルパス表示用フォーマッターを設定する。
	 * @param newFormatter	新しい <code>VirtualFilePathFormatter</code> オブジェクト
	 * @since 1.20
	 */
	public void setPathFormatter(VirtualFilePathFormatter newFormatter) {
		this._vfFormatter = newFormatter;
	}
	
	public VirtualFile getBasePath() {
		return _vfBasePath;
	}
	
	public void setBasePath(VirtualFile basePath) {
		if (!Objects.isEqual(_vfBasePath, basePath)) {
			this._vfBasePath = basePath;
		}
	}

	/**
	 * 指定された抽象パスを、表示用パス文字列に整形する。
	 * @return	整形後のパス文字列
	 * @since 1.20
	 */
	public String formatPathString(VirtualFile file) {
		if (_vfFormatter != null) {
			String path = _vfFormatter.formatPath(file);
			if (path != null)
				return path;
		}
		
		if (_vfBasePath != null && file.isDescendingFrom(_vfBasePath)) {
			return file.relativePathFrom(_vfBasePath, Files.CommonSeparatorChar);
		} else {
			return file.toString();
		}
	}

	/**
	 * 指定された引数定義を持つテーブルモデルを設定する。
	 * このメソッドでは、既存の引数設定は破棄される。
	 * @param details	引数定義の配列
	 */
	public void updateTableModelByArgDetails(ModuleArgDetail[] details) {
		IMExecDefArgTableModel newModel = createDefaultTableModel();
		if (details != null && details.length > 0) {
			for (ModuleArgDetail det : details) {
				newModel.addRow(det.type(), det.description(), null);
			}
		}
		setTableModel(newModel);
	}

	/**
	 * 指定された引数設定を持つテーブルモデルを設定する。
	 * このメソッドでは、既存の引数設定は破棄される。
	 * @param data	引数設定の配列
	 */
	public void updateTableModelByArgData(ModuleArgData[] data) {
		IMExecDefArgTableModel newModel = createDefaultTableModel();
		if (data != null && data.length > 0) {
			for (ModuleArgData item : data) {
				newModel.addRow(item.getType(), item.getDescription(), item.getValue());
			}
		}
		setTableModel(newModel);
	}

	@Override
	public IMExecDefArgTableModel getTableModel() {
		return (IMExecDefArgTableModel)super.getTableModel();
	}

	@Override
	public void setTableModel(IModuleArgTableModel newModel) {
		if (newModel != null && !(newModel instanceof IMExecDefArgTableModel)) {
			throw new IllegalArgumentException("'newModel' object is not IMExecDefArgTableModel instance : " + newModel.getClass().getName());
		}
		super.setTableModel(newModel);
	}

	@Override
	public void onButtonEdit(ActionEvent ae) {
		stopTableCellEditing();
		int selected = getSelectedRow();
		if (selected < 0) {
			// no selection
			return;
		}
		
		// 引数属性が未定義の場合は警告
		IMExecDefArgTableModel model = getTableModel();
		ModuleArgType argtype = model.getArgumentAttr(selected);
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected IMExecDefArgTableModel createDefaultTableModel() {
		return new MExecDefArgTableModel();
	}

	@Override
	protected ModuleArgTable createTable() {
		return new MExecDefArgTable();
	}
	
	abstract protected void onEditMenuItemAction(ActionEvent ae);
	
	protected EditMenuItemAction getEditMenuItemAction(String command) {
		if (_mapEditMenuActions == null) {
			_mapEditMenuActions = createEditMenuActionMap();
		}
		return _mapEditMenuActions.get(command);
	}
	
	protected JMenuItem createEditMenuItem(String command) {
		Action action = getEditMenuItemAction(command);
		JMenuItem item = new JMenuItem(action);
		return item;
	}
	
	abstract protected Map<String, EditMenuItemAction> createEditMenuActionMap();
	
	abstract protected JPopupMenu createEditFileInPopupMenu();
	
	abstract protected JPopupMenu createEditFileOutPopupMenu();
	
	abstract protected JPopupMenu createEditStringPopupMenu();
	
	abstract protected JPopupMenu createEditPublishPopupMenu();
	
	abstract protected JPopupMenu createEditSubscribePopupMenu();
	
	protected JPopupMenu getEditFileInPopupMenu() {
		if (_popupEditFileIn == null) {
			_popupEditFileIn = createEditFileInPopupMenu();
			_popupEditFileIn.addPopupMenuListener(((MenuToggleButton)getEditButton()).getToggleOffPopupMenuListener());
		}
		return _popupEditFileIn;
	}
	
	protected JPopupMenu getEditFileOutPopupMenu() {
		if (_popupEditFileOut == null) {
			_popupEditFileOut = createEditFileOutPopupMenu();
			_popupEditFileOut.addPopupMenuListener(((MenuToggleButton)getEditButton()).getToggleOffPopupMenuListener());
		}
		return _popupEditFileOut;
	}
	
	protected JPopupMenu getEditStringPopupMenu() {
		if (_popupEditString == null) {
			_popupEditString = createEditStringPopupMenu();
			_popupEditString.addPopupMenuListener(((MenuToggleButton)getEditButton()).getToggleOffPopupMenuListener());
		}
		return _popupEditString;
	}
	
	protected JPopupMenu getEditPublishPopupMenu() {
		if (_popupEditPublish == null) {
			_popupEditPublish = createEditPublishPopupMenu();
			_popupEditPublish.addPopupMenuListener(((MenuToggleButton)getEditButton()).getToggleOffPopupMenuListener());
		}
		return _popupEditPublish;
	}
	
	protected JPopupMenu getEditSubscribePopupMenu() {
		if (_popupEditSubscribe == null) {
			_popupEditSubscribe = createEditSubscribePopupMenu();
			_popupEditSubscribe.addPopupMenuListener(((MenuToggleButton)getEditButton()).getToggleOffPopupMenuListener());
		}
		return _popupEditSubscribe;
	}

	/**
	 * 引数設定の値から、表示用テキストを取得する。
	 */
	protected String getDisplayArgumentValueText(Object value) {
		if (value instanceof VirtualFile) {
			return formatPathString((VirtualFile)value);
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
	
	@Override
	protected void setupActions() {
		super.setupActions();
		
		// テーブルにファイルドロップリスナーを設定
		_hDropDataToTable = createModuleArgTableDropListener();
		new DropTarget(getTableComponent(), DnDConstants.ACTION_COPY, _hDropDataToTable, true);
	}
	
	protected AbModuleArgTableDropListener createModuleArgTableDropListener() {
		return new ArgFileDropTargetListener(getTableComponent());
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
	protected class ArgumentValueCellRenderer extends DefaultTableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
														boolean isSelected, boolean hasFocus,
														int row, int column)
		{
			// 表示用テキストを生成
			String strText = getDisplayArgumentValueText(value);
			
			//--- (!isSelected && hasFocus)の状態では、super#getTableCellRendererComponent() 内で、
			//--- "Table.focusCellForeground" と "Table.focusCellBackground" にカラーが変更されているため、
			//--- 元に戻す。
			Component comp = super.getTableCellRendererComponent(table, strText, isSelected, hasFocus, row, column);

			// 選択時以外の色を設定
			if (!isSelected) {
				// 引数属性の取得
				Object objAttr = table.getValueAt(row, table.convertColumnIndexToView(0));
				if (objAttr instanceof ModuleArgType) {
					ModuleArgType attr = (ModuleArgType)objAttr;
					if (attr == ModuleArgType.IN) {
						comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_IN);
					}
					else if (attr == ModuleArgType.OUT) {
						comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_OUT);
					}
					else if (attr == ModuleArgType.STR) {
						comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_STR);
					}
					else if (attr == ModuleArgType.PUB) {
						comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_PUB);
					}
					else if (attr == ModuleArgType.SUB) {
						comp.setBackground(CommonResources.DEF_BACKCOLOR_ARG_SUB);
					}
					else {
						comp.setBackground(table.getBackground());
					}
				}
				else {
					comp.setBackground(table.getBackground());
				}
			}
			
			return comp;
		}
	}
	
	protected class MExecDefArgTable extends ModuleArgTable
	{
		public MExecDefArgTable() {
			super();
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			return super.getToolTipText(event);
		}

		@Override
		public void setModel(TableModel dataModel) {
			// モデルのセット
			super.setModel(dataModel);
			
			if (dataModel instanceof IMExecDefArgTableModel) {
				// 値列固有の設定
				TableColumn colValue = getColumnModel().getColumn( ((IMExecDefArgTableModel)dataModel).valueColumnIndex() );
				//--- setup cell renderer
				ArgumentValueCellRenderer valCellRenderer = createArgumentValueCellRenderer();
				if (valCellRenderer != null) {
					colValue.setCellRenderer(valCellRenderer);
				}
			}
		}
		
		public void setArgumentValue(int rowIndex, Object newValue) {
			if (dataModel instanceof IMExecDefArgTableModel) {
				((IMExecDefArgTableModel)dataModel).setArgumentValue(rowIndex, newValue);
			}
		}
		
		protected ArgumentValueCellRenderer createArgumentValueCellRenderer() {
			ArgumentValueCellRenderer renderer = new ArgumentValueCellRenderer();
			return renderer;
		}

		@Override
		protected TableCellRenderer createModuleAttrCellRenderer() {
			return new StaticModuleArgTypeTableCellRenderer();
		}

		@Override
		public Component prepareEditor(TableCellEditor editor, int row, int column) {
			Component ec = super.prepareEditor(editor, row, column);
			Object value = super.getValueAt(row, column);
			if (value instanceof IMExecArgParam) {
				((JTextComponent)ec).setText("");
				((JTextComponent)ec).setCaretPosition(0);
			}
			return ec;
		}
	}
	
	/**
	 * ファイル名のドロップリスナー
	 */
	static protected class ArgFileDropTargetListener extends AbModuleArgTableDropListener
	{
		public ArgFileDropTargetListener() {
			super();
		}
		
		public ArgFileDropTargetListener(ModuleArgTable table) {
			super(table);
		}

		@Override
		protected boolean acceptDataFlavor(DropTargetDragEvent dtde) {
			if (dtde.isDataFlavorSupported(VirtualFileTransferable.javaFileListFlavor)) {
				return true;
			}
			
			if (dtde.isDataFlavorSupported(VirtualFileTransferable.virtualFileListFlavor)) {
				return true;
			}
			
			// not accept
			return false;
		}

		@Override
		protected boolean acceptDataFlavor(DropTargetDropEvent dtde) {
			if (dtde.isDataFlavorSupported(VirtualFileTransferable.javaFileListFlavor)) {
				return true;
			}
			
			if (dtde.isDataFlavorSupported(VirtualFileTransferable.virtualFileListFlavor)) {
				return true;
			}
			
			// not accept
			return false;
		}

		@Override
		protected boolean acceptDropPosition(int rowIndex, int columnIndex, ModuleArgTable table) {
			// インデックスが適切ではない場合は許容しない
			if (!super.acceptDropPosition(rowIndex, columnIndex, table))
				return false;

			// ドロップ可能な行は [IN] 属性の引数行のみ
			return (ModuleArgType.IN == table.getArgumentAttr(rowIndex));
		}

		@Override
		protected void onDropData(DropTargetDropEvent dtde, ModuleArgTable table, int rowIndex, int columnIndex, Transferable t)
		{
			VirtualFile[] files = VirtualFileTransferable.getFilesFromTransferData(t);
			if (files != null && files.length > 0) {
				// 先頭のファイルのみを使用
				// check, cannot be specified a directory
				if (files[0].isDirectory()) {
					Application.showErrorMessage(RunnerMessages.getInstance().msgMExecDefArgCannotUseDir);
				} else if (table instanceof MExecDefArgTable) {
					// setup argument for the specified file
					((MExecDefArgTable)table).setArgumentValue(rowIndex, files[0].getAbsoluteFile().getNormalizedFile());
				}
			}
			// ドロップ完了
			dtde.dropComplete(true);
		}
	}
}
