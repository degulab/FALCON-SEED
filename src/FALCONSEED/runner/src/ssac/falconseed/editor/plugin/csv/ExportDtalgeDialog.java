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
 * @(#)ExportDtalgeDialog.java	1.21	2012/06/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExportDtalgeDialog.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.plugin.csv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.Strings;
import ssac.util.logging.AppLogger;
import ssac.util.nio.csv.CsvFieldAttr;
import ssac.util.nio.csv.CsvRecordCursor;
import ssac.util.nio.csv.CsvUtil;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.JStaticMultilineTextPane;
import ssac.util.swing.ProgressMonitorTask;
import dtalge.DtBase;

/**
 * データ代数形式でCSVをエクスポートする設定を行うダイアログ
 * 
 * @version 1.21	2012/06/05
 */
public class ExportDtalgeDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(480, 320);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 編集対象の情報 **/
	protected final CsvFileModel	_targetModel;
	

	/** 選択されているファイルの抽象パス **/
	private File				_selectedFile;
	/** テーブル **/
	private DtalgeHeaderTable	_table;
	/** 出力対象のファイルパス **/
	private JTextComponent		_stcFilePath;
	/** 出力先ファイルを選択するボタン **/
	private JButton			_btnChooseFile;
	/** 型の自動判別を行うボタン **/
	private JButton			_btnAutoDetect;
	/** 基底の重複を無視することを示すチェックボックス **/
	private JCheckBox			_chkIgnoreDtBaseMultiple;
	/** null出力状態全設定用チェックボックス **/
	private TriStateCheckBox	_triNullState;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ExportDtalgeDialog(Frame owner, final CsvFileModel csvModel) {
		super(owner, CsvFileMessages.getInstance().ExportDtalgeDlg_title, true);
		if (csvModel == null)
			throw new NullPointerException("The specified CsvFileModel object is null.");
		if (csvModel.getColumnCount() <= 0 || csvModel.getRowCount() <= 0)
			throw new IllegalArgumentException("The specified CsvFileModel object is empty.");
		this._targetModel = csvModel;
		setConfiguration(AppSettings.EXPORTDTALGE_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	public ExportDtalgeDialog(Dialog owner, final CsvFileModel csvModel) {
		super(owner, CsvFileMessages.getInstance().ExportDtalgeDlg_title, true);
		if (csvModel == null)
			throw new NullPointerException("The specified CsvFileModel object is null.");
		if (csvModel.getColumnCount() <= 0 || csvModel.getRowCount() <= 0)
			throw new IllegalArgumentException("The specified CsvFileModel object is empty.");
		this._targetModel = csvModel;
		setConfiguration(AppSettings.EXPORTDTALGE_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	public void initialComponent() {
		// create content components
		createContentComponents();
		
		// initial component
		super.initialComponent();
		
		// restore settings
		restoreConfiguration();
		
		// CSVデータから、初期モデルを構築
		DtalgeHeaderTableModel newModel = createTableModel(getTargetModel());
		getTable().setModel(newModel);
		//--- setup Null state controller
		NullStateManager nsm = new NullStateManager(newModel.nullStateColumnIndex());
		newModel.addTableModelListener(nsm);
		_triNullState.setHandler(nsm);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public CsvFileModel getTargetModel() {
		return _targetModel;
	}
	
	public File getSelectedFile() {
		return _selectedFile;
	}
	
	public boolean isIgnoreDtBaseMultiple() {
		if (_chkIgnoreDtBaseMultiple != null) {
			return _chkIgnoreDtBaseMultiple.isSelected();
		} else {
			return false;
		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected boolean doOkAction() {
		//--- テーブルの編集状態を解除
		commitEditForTable();
		
		// 出力先ファイルのチェック
		if (getSelectedFile() == null) {
			Application.showErrorMessage(this, getErrorEmptySelectedFile());
			return false;
		}
		
		// データチェック
		if (!verifyHeaderData()) {
			return false;
		}
		
		// 上書き保存のチェック
		int result = FileChooserManager.confirmFileOverwrite(this, getSelectedFile(), true);
		if (result != JOptionPane.YES_OPTION) {
			// user canceled
			return false;
		}
		
		// 完了
		return true;
	}

	// ファイル選択ボタン
	protected void onButtonChooseFile() {
		//--- テーブルの編集状態を解除
		commitEditForTable();
		
		// ファイル保存ダイアログを開く
		File initFile = FileChooserManager.getInitialDocumentFile(getSelectedFile());
		File selectedFile = FileChooserManager.chooseSaveFile(this,
								CsvFileMessages.getInstance().ExportDtalgeDlg_title_filechooser,
								initFile, FileChooserManager.getCsvFileFilter());
		if (selectedFile == null) {
			// user canceled
			return;
		}
		
		// 選択ファイルの保存
		FileChooserManager.setLastChooseDocumentFile(selectedFile);
		setSelectedFile(selectedFile);
	}

	// データ型自動判別ボタン
	protected void onButtonAutoDetect() {
		//--- テーブルの編集状態を解除
		commitEditForTable();
		
		// ボタンを disable
		getAutoDetectButton().setEnabled(false);
		
		// 処理開始
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				// データ型自動判別タスクの実行
				DataTypeCollectProgressTask task = new DataTypeCollectProgressTask(getTargetModel().getSearchCursor());
				boolean result = task.execute(ExportDtalgeDialog.this);
				if (result) {
					// 処理成功
					DtalgeHeaderTableModel model = getTableModel();
					List<Class<?>> typelist = task.getDetectedDataTypeList();
					int len = typelist.size();
					for (int i = 0; i < len; i++) {
						DtalgeDataTypes dttype = convertFromClass(typelist.get(i));
						model.setTypeKey(i, dttype);
					}
				} else {
					// 処理失敗
					Throwable taskex = task.getErrorCause();
					if (taskex instanceof IOException) {
						// 入出力エラー
						String errmsg = CommonMessages.formatErrorMessage(getErrorFailedToAutoDetect(), taskex,
															getTargetModel().getTargetFile().getAbsolutePath());
						AppLogger.error(errmsg, taskex);
						Application.showErrorMessage(ExportDtalgeDialog.this, errmsg);
					}
					else if (taskex instanceof OutOfMemoryError) {
						// メモリ不足
						String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
						AppLogger.error(errmsg, taskex);
						Application.showErrorMessage(ExportDtalgeDialog.this, errmsg);
					}
					else if (taskex != null) {
						// その他のエラー
						String errmsg = CommonMessages.formatErrorMessage(getErrorFailedToAutoDetect(), taskex);
						AppLogger.error(errmsg, taskex);
						Application.showErrorMessage(ExportDtalgeDialog.this, errmsg);
					}
					//--- (taskex == null) => user canceled
				}
				//--- ボタンの有効化
				getAutoDetectButton().setEnabled(true);
			}
		});
	}
	
	@Override
	protected void dialogClose(int result) {
		// テーブル編集状態を破棄
		cancelEditForTable();
		
		// ダイアログを閉じる
		super.dialogClose(result);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected void setSelectedFile(File file) {
		if (file == null) {
			_selectedFile = null;
			getFilePathLabel().setText("");
		} else {
			_selectedFile = file;
			getFilePathLabel().setText(file.getAbsolutePath());
		}
	}
	
	protected String trimString(String str) {
		if (str == null) {
			return null;
		} else {
			return str.trim();
		}
	}

	/**
	 * テーブルが編集状態なら、編集をコミットし、編集状態を完了する。
	 */
	protected void commitEditForTable() {
		if (getTable().isEditing()) {
			getTable().getCellEditor().stopCellEditing();
			getTable().removeEditor();
		}
	}

	/**
	 * テーブルが編集状態なら、編集をキャンセルする
	 */
	protected void cancelEditForTable() {
		if (getTable().isEditing()) {
			getTable().removeEditor();
		}
	}
	
	protected String getErrorEmptyNameKey(int rowIndex, String columnName) {
		return String.format(CsvFileMessages.getInstance().msgDtBaseNameEmpty, (rowIndex+1), columnName);
	}
	
	protected String getErrorEmptyDataTypeKey(int rowIndex, String columnName) {
		return String.format(CsvFileMessages.getInstance().msgDtBaseTypeEmpty, (rowIndex+1), columnName);
	}
	
	protected String getErrorIllegalBaseKey(int rowIndex, String columnName) {
		String msg = String.format(CsvFileMessages.getInstance().msgDtBaseIllegalBaseKeyChars, (rowIndex+1), columnName);
		msg = msg + "\n    " + CsvFileMessages.getInstance().ExportDtalgeDlg_illegalBaseKeyChars;
		return msg; 
	}
	
	protected String getErrorIllegalDataTypeKey(int rowIndex, String columnName) {
		return String.format(CsvFileMessages.getInstance().msgDtBaseIllegalTypeKey, (rowIndex+1), columnName);
	}
	
	protected String getErrorDtBaseMultiple(int existRowIndex, int rowIndex) {
		return String.format(CsvFileMessages.getInstance().msgDtBaseMultiple, (existRowIndex+1), (rowIndex+1));
	}
	
	protected String getErrorFailedToAutoDetect() {
		return CsvFileMessages.getInstance().msgFailedToAutoDetect;
	}
	
	protected String getErrorEmptySelectedFile() {
		return CsvFileMessages.getInstance().msgOutputFileEmpty;
	}

	/**
	 * 設定されたヘッダー情報を検証する。
	 * このメソッドではデータ型の正当性はチェックしない。
	 * @return	ヘッダー情報として正当なら <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	protected boolean verifyHeaderData() {
		DtalgeHeaderTableModel model = getTableModel();
		int indexName = model.nameColumnIndex();
		int indexType = model.typeColumnIndex();
		int indexAttr = model.attrColumnIndex();
		int indexSubject = model.subjectColumnIndex();
		String labelNameColumn = model.getColumnName(indexName);
		String labelTypeColumn = model.getColumnName(indexType);
		String labelAttrColumn = model.getColumnName(indexAttr);
		String labelSubjectColumn = model.getColumnName(indexSubject);
		
		//--- 同じ基底が存在することを警告するための集合
		Map<DtBase, Integer> map = new HashMap<DtBase, Integer>();
		
		// データ代数基底に変換
		int len = model.getRowCount();
		for (int row = 0; row < len; row++) {
			DtalgeDataTypes type = model.getTypeKey(row);
			String name = trimString(model.getNameKey(row));
			String attr = trimString(model.getAttributeKey(row));
			String subject = trimString(model.getSubjectKey(row));
			
			//--- check name key
			if (Strings.isNullOrEmpty(name)) {
				Application.showErrorMessage(this, getErrorEmptyNameKey(row, labelNameColumn));
				getTable().scrollToVisibleCell(row, indexName);
				getTable().setCellSelected(row, indexName);
				return false;
			}
			if (!DtBase.Util.isValidBaseKeyString(name, true)) {
				Application.showErrorMessage(this, getErrorIllegalBaseKey(row, labelNameColumn));
				getTable().scrollToVisibleCell(row, indexName);
				getTable().setCellSelected(row, indexName);
				return false;
			}
			//--- check type key
			if (type == null) {
				Application.showErrorMessage(this, getErrorEmptyDataTypeKey(row, labelTypeColumn));
				getTable().scrollToVisibleCell(row, indexType);
				getTable().setCellSelected(row, indexType);
				return false;
			}
			
			//--- check attr key
			if (!DtBase.Util.isValidBaseKeyString(attr, true)) {
				Application.showErrorMessage(this, getErrorIllegalBaseKey(row, labelAttrColumn));
				getTable().scrollToVisibleCell(row, indexAttr);
				getTable().setCellSelected(row, indexAttr);
				return false;
			}
			//--- check subject key
			if (!DtBase.Util.isValidBaseKeyString(subject, true)) {
				Application.showErrorMessage(this, getErrorIllegalBaseKey(row, labelSubjectColumn));
				getTable().scrollToVisibleCell(row, indexSubject);
				getTable().setCellSelected(row, indexSubject);
				return false;
			}
			
			//--- create DtBase
			DtBase base;
			try {
				base = new DtBase(name, type.toString(), attr, subject);
			} catch (Throwable ex) {
				String errmsg = CommonMessages.formatErrorMessage(getErrorIllegalDataTypeKey(row, labelTypeColumn), ex);
				AppLogger.debug(errmsg, ex);
				Application.showErrorMessage(this, errmsg);
				getTable().scrollToVisibleCell(row, indexType);
				getTable().setCellSelected(row, indexType);
				return false;
			}
			//--- check exists
			if (!isIgnoreDtBaseMultiple() && map.containsKey(base)) {
				Application.showErrorMessage(this, getErrorDtBaseMultiple(map.get(base), row));
				getTable().scrollToVisibleCell(row, indexName);
				getTable().setCellSelected(row, indexName);
				return false;
			}
			map.put(base, row);
		}
		
		// valid
		return true;
	}

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	@Override
	protected JButton createApplyButton() {
		// no apply button
		return null;
	}

	@Override
	protected void setupDialogConditions() {
		super.setupDialogConditions();
		
		this.setResizable(true);
		//this.setStoreLocation(false);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200, 300);
		}
		setMinimumSize(dmMin);
		setKeepMinimumSize(true);
	}

	@Override
	protected void setupMainContents() {
		// create labels
		JLabel lblFilePath = new JLabel(CsvFileMessages.getInstance().ExportDtalgeDlg_label_FilePath + " :");
		JLabel lblHeader   = new JLabel(CsvFileMessages.getInstance().ExportDtalgeDlg_label_Table + " :");
		
		// setup scroll for table
		JScrollPane scroll = createTableScrollPane();
		
		// create main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		Insets spaceInsets = new Insets(0, 0, 3, 0);
		Insets separateInsets = new Insets(10, 0, 3, 0);
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = spaceInsets;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- file path
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		mainPanel.add(lblFilePath, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		mainPanel.add(_btnChooseFile, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		mainPanel.add(_stcFilePath, gbc);
		gbc.gridy++;
		//--- Ignore DtBase multiple
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.insets = separateInsets;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		mainPanel.add(_chkIgnoreDtBaseMultiple, gbc);
		gbc.gridy++;
		//--- table
		gbc.insets = separateInsets;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		mainPanel.add(lblHeader, gbc);
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		mainPanel.add(_btnAutoDetect, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.insets = spaceInsets;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(scroll, gbc);
		gbc.gridy++;
		//--- All null state toggle
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = spaceInsets;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		mainPanel.add(_triNullState, gbc);
		gbc.gridy++;

		//// addl to main panel
		//this.add(mainPanel, BorderLayout.CENTER);

		// add to main panel
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
	}
	
	protected JTextComponent getFilePathLabel() {
		return _stcFilePath;
	}
	
	protected JCheckBox getIgnoreDtBaseMultipleCheckBox() {
		return _chkIgnoreDtBaseMultiple;
	}
	
	protected DtalgeHeaderTable getTable() {
		return _table;
	}
	
	protected DtalgeHeaderTableModel getTableModel() {
		return (DtalgeHeaderTableModel)_table.getModel();
	}
	
	protected JButton getChooseFileButton() {
		return _btnChooseFile;
	}
	
	protected JButton getAutoDetectButton() {
		return _btnAutoDetect;
	}

	protected void createContentComponents() {
		this._table = createTable();
		this._stcFilePath = createStaticFilePathLabel();
		this._btnChooseFile = createChooseFileButton();
		this._btnAutoDetect = createAutoDetectButton();
		this._chkIgnoreDtBaseMultiple = createIgnoreDtBaseMultipleCheckBox();
		this._triNullState = new TriStateCheckBox(CsvFileMessages.getInstance().ExportDtalgeDlg_button_AllNullStateChange);
	}
	
	protected JButton createChooseFileButton() {
		JButton btn = new JButton(CsvFileMessages.getInstance().ExportDtalgeDlg_button_ChooseFile);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonChooseFile();
			}
		});
		return btn;
	}
	
	protected JButton createAutoDetectButton() {
		JButton btn = new JButton(CsvFileMessages.getInstance().ExportDtalgeDlg_button_AutoDetect);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonAutoDetect();
			}
		});
		return btn;
	}
	
	protected JTextComponent createStaticFilePathLabel() {
		JStaticMultilineTextPane label = new JStaticMultilineTextPane();
		return label;
	}
	
	protected JCheckBox createIgnoreDtBaseMultipleCheckBox() {
		JCheckBox chk = new JCheckBox(CsvFileMessages.getInstance().ExportDtalgeDlg_button_IgnoreDtBaseMulti);
		return chk;
	}
	
	protected JScrollPane createTableScrollPane() {
		JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
											JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//--- set view
		scroll.setViewportView(_table);
		scroll.setRowHeaderView(_table.getTableRowHeader());
		//--- setup corner
		JPanel borderPanel = new JPanel();
		borderPanel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		scroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, borderPanel);
		
		return scroll;
	}
	
	protected DtalgeHeaderTable createTable() {
		DtalgeHeaderTable table = new DtalgeHeaderTable();
		table.setModel(createDefaultTableModel());
		
		//--- setup table parameters
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnSelectionAllowed(true);
		table.setRowSelectionAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		// テーブルの罫線を表示(Mac は白で書いております)
		table.setShowGrid(true);
		table.setGridColor(new Color(128,128,128));
		
		table.getTableRowHeader().setFixedCellWidth(50);
		
		return table;
	}
	
	protected DtalgeHeaderTableModel createDefaultTableModel() {
		return new DtalgeHeaderTableModel();
	}
	
	protected DtalgeDataTypes convertFromClass(Class<?> datatype) {
		if (datatype == null) {
			// String 型
			return DtalgeDataTypes.STRING;
		}
		else if (Boolean.class.isAssignableFrom(datatype)) {
			// Boolean 型
			return DtalgeDataTypes.BOOLEAN;
		}
		else if (Number.class.isAssignableFrom(datatype)) {
			// Decimal 型
			return DtalgeDataTypes.DECIMAL;
		}
		else {
			// String 型
			return DtalgeDataTypes.STRING;
		}
	}
	
	protected DtalgeHeaderTableModel createTableModel(final CsvFileModel csvModel) {
		DtalgeHeaderTableModel tableModel = new DtalgeHeaderTableModel();
		CsvFieldAttr[] attrArray = csvModel.getFileData().getFieldListArray();
		for (int i = 0; i < attrArray.length; i++) {
			CsvFieldAttr attr = attrArray[i];
			String name = attr.getFieldName();
			if (Strings.isNullOrEmpty(name)) {
				name = getTargetModel().getColumnName(i);
			}
			DtalgeDataTypes type = convertFromClass(attr.getDataType());
			//--- add to table model
			tableModel.addRow(name, type, null, null);
		}
		return tableModel;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class NullStateManager implements TableModelListener, TriStateCheckBox.TriStateHandler
	{
		private final int		_targetColumnIndex;
		
		private boolean	_ignoreTableChangeEvent = false;
		
		public NullStateManager(int targetColumnIndex) {
			_targetColumnIndex = targetColumnIndex;
		}

		public void tableChanged(TableModelEvent tme) {
	        if(!_ignoreTableChangeEvent && tme.getType()==TableModelEvent.UPDATE && tme.getColumn()==_targetColumnIndex)
	        {
	        	TriStateCheckBox.Status state = _triNullState.getState();
	        	if (!TriStateCheckBox.Status.INDETERMINATE.equals(state)) {
	        		// SELECTED or DESELECTED
	        		_triNullState.setState(TriStateCheckBox.Status.INDETERMINATE);
	        	}
	        	else {
	        		boolean selected = true;
	        		boolean deselected = true;
	        		
	        		DtalgeHeaderTableModel model = getTableModel();
	        		for (int row = 0; row < model.getRowCount(); row++) {
	        			boolean b = model.getNullState(row);
	        			selected &= b;
	        			deselected &= !b;
	        			if (selected==deselected) return;
	        		}
	        		
	        		if (selected) {
	        			_triNullState.setState(TriStateCheckBox.Status.SELECTED);
	        		}
	        		else if (deselected) {
	        			_triNullState.setState(TriStateCheckBox.Status.DESELECTED);
	        		}
	        	}
	        }
		}

//		public void stateChanged(ChangeEvent ce) {
//			System.out.println("ExportDtalgeDialog:JTriStateCheckBox:stateChanged::ChangeEvent = " + String.valueOf(ce));
//			System.out.println("ExportDtalgeDialog:JTriStateCheckBox:stateChanged::_triNullState.getState() = " + String.valueOf(_triNullState.getState()));
//		}

		public TriStateCheckBox.Status nextState(TriStateCheckBox.Status currentState) {
			boolean toSelected = TriStateCheckBox.Status.SELECTED.equals(currentState) ? false : true;
			_ignoreTableChangeEvent = true;
			DtalgeHeaderTableModel model = getTableModel();
			for (int row = 0; row < model.getRowCount(); row++) {
				model.setNullState(row, toSelected);
			}
			_ignoreTableChangeEvent = false;
			return (toSelected ? TriStateCheckBox.Status.SELECTED : TriStateCheckBox.Status.DESELECTED);
		}
	}
	
	static protected class DataTypeCollectProgressTask extends ProgressMonitorTask
	{
		protected final CsvRecordCursor			_cursor;
		protected final List<Class<?>>			_listDataType;
		
		public DataTypeCollectProgressTask(final CsvRecordCursor cursor)
		{
			super(CsvFileMessages.getInstance().progressAutoDetect_title, CsvFileMessages.getInstance().progressAutoDetect_desc, null, 0, 0, 100);
			this._cursor = cursor;
			this._listDataType = new ArrayList<Class<?>>(cursor.getFileData().getMaxFieldSize());
			setMinimum(0);
			setMaximum(100+2);
			setValue(0);
		}
		
		public List<Class<?>> getDetectedDataTypeList() {
			return _listDataType;
		}
		
		@Override
		public void processTask() throws Throwable
		{
			final CsvRecordCursor cursor = _cursor;
			final List<Class<?>> typelist = _listDataType;
			final long numRecords = _cursor.getRecordSize();
			final int maxFields = cursor.getFileData().getMaxFieldSize();
			
			// データ型格納リストの要素を初期化
			for (int col = 0; col < maxFields; col++) {
				typelist.add(null);
			}
			//--- begin progress
			incrementValue();
			//--- end progress

			// データ型の判定
			final int baseProgressValue = getValue();
			long index = 0L;
			for ( ; index < numRecords;) {
				//--- begin progress
				if (isTerminateRequested()) {
					return;
				}
				//--- end progress
				String[] fields = _cursor.getRecord(index);
				for (int col = 0; col < fields.length; col++) {
					//--- begin progress
					if (isTerminateRequested()) {
						return;
					}
					//--- end progress
					Class<?> curType = typelist.get(col);
					Class<?> detectedType = CsvUtil.detectDataType(curType, fields[col]);
					if (detectedType != null) {
						typelist.set(col, detectedType);
					}
				}
				//--- begin progress
				index++;
				setValue(baseProgressValue + (int)(((double)index / (double)numRecords) * 100.0));
				//--- end progress
			}
			
			//--- begin progress
			if (isTerminateRequested()) {
				return;
			}
			//--- end progress
			
			// 不明な型は文字列型にする
			for (int col = 0; col < maxFields; col++) {
				if (typelist.get(col) == null) {
					typelist.set(col, String.class);
				}
			}
			//--- begin progress
			incrementValue();
			//--- end progress
		}
	}
	/**
	 * Maintenance tip - There were some tricks to getting this code
	 * working:
	 *
	 * 1. You have to overwite addMouseListener() to do nothing
	 * 2. You have to add a mouse event on mousePressed by calling
	 * super.addMouseListener()
	 * 3. You have to replace the UIActionMap for the keyboard event
	 * "pressed" with your own one.
	 * 4. You have to remove the UIActionMap for the keyboard event
	 * "released".
	 * 5. You have to grab focus when the next state is entered,
	 * otherwise clicking on the component won't get the focus.
	 * 6. You have to make a TristateDecorator as a button model that
	 * wraps the original button model and does state management.
	 */
	static protected class TriStateCheckBox extends JCheckBox
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------
		
		static public enum Status { SELECTED, DESELECTED, INDETERMINATE }

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------
		
		private final TriStateDecorator	_model;
		
		private TriStateHandler	_handler = DefaultTriStateHandler;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		public TriStateCheckBox() {
			this(null);
		}
		
		public TriStateCheckBox(String text) {
			this(text, Status.DESELECTED);
		}
		
		public TriStateCheckBox(String text, Status initialState) {
			this(text, null, initialState);
		}
		
		public TriStateCheckBox(String text, Icon icon) {
			this(text, icon, Status.DESELECTED);
		}
		
		public TriStateCheckBox(String text, Icon icon, Status initialState) {
			super(text, icon);
			
			// Add a listener for when the mouse is pressed
			super.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					grabFocus();
					_model.nextState();
				}
			});
			
			// Reset the keyboard action map
			ActionMap map = new ActionMapUIResource();
			map.put("pressed", new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
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
		public void setSelected(boolean b) {
			setState(b ? Status.SELECTED : Status.DESELECTED);
		}
		
		@Override
		public synchronized void addMouseListener(MouseListener l) {
			// No one may add mouse listeners, not even Swing!
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------
		
		static protected final TriStateHandler DefaultTriStateHandler = new TriStateHandler(){
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
		
		static public interface TriStateHandler
		{
			public Status nextState(Status currentState);
		}
		
		/**
		 * 3ステートの処理を行うデコレータモデル。
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
	}
}
