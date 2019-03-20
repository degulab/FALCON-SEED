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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)GenericInputTableSchemaEditDialog.java	3.2.1	2015/07/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericInputTableSchemaEditDialog.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.table;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.fs.module.schema.table.SchemaInputCsvDataField;
import ssac.aadl.fs.module.schema.type.SchemaDateTimeFormats;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeBoolean;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeDateTime;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeDecimal;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeManager;
import ssac.aadl.fs.module.schema.type.SchemaValueTypeString;
import ssac.aadl.runtime.io.CsvFileReader;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.falconseed.filter.generic.gui.util.GenericFilterSwingTools;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.falconseed.runner.view.dialog.FileChooserManager;
import ssac.util.logging.AppLogger;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.JStaticMultilineTextPane;
import ssac.util.swing.table.AbSpreadSheetTableModel;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * 汎用フィルタの入力テーブル定義編集ダイアログ。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericInputSchemaEditDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -1139441568620123347L;

	/** 最小処理対象外レコード数 **/
	static protected int MIN_SKIP_RECORDS	= 0;
	/** 最大処理対象外レコード数 **/
	static protected int MAX_SKIP_RECORDS	= 10;
	/** 標準処理対象外レコード数 **/
	static protected int DEF_SKIP_RECORDS	= 1;
	/** 読み込むデータレコード数 **/
	static protected int NUM_READ_RECORDS	= MAX_SKIP_RECORDS + 5;

	//	static private final int FIXED_DESC_HEIGHT = 150;
	static private final Dimension DM_MIN_SIZE = new Dimension(640, 480);

	static protected final String BTNCMD_OPEN_BASECSV			= "open.basecsv";
	static protected final String BTNCMD_COMPONSE_BY_BASECSV	= "componse.by.basecsv";
	static protected final String BTNCMD_FIELD_ADD				= "field.add";
	static protected final String BTNCMD_FIELD_DELETE			= "field.delete";
	static protected final String BTNCMD_FIELD_MOVELEFT			= "field.move.left";
	static protected final String BTNCMD_FIELD_MOVERIGHT		= "field.move.right";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 編集対象の入力テーブルスキーマのツリーノード **/
	private final GenericTableSchemaTreeNode	_orgTableData;
	/** CSV 入力スキーマテーブルのデータモデル **/
	private final GenericInputCsvSchemaTableModel	_schemaCsvModel;
	/** 汎用フィルタ編集用データモデル **/
	private final GenericFilterEditModel		_editModel;
	/** 基準 CSV テーブルのデータモデル **/
	private final BaseCsvTableModel				_baseCsvModel;

	/** スキーマ用ツールバー **/
	private JToolBar	_tbarInputSchema;
	/** 基準 CSV ファイル用ツールバー **/
	private JToolBar	_tbarBaseCsv;

	/** 処理対象外レコード数を設定するスピンコンポーネント **/
	private JSpinner	_spinSkipRecords;

	/** 基準 CSV ファイルのパス **/
	private JStaticMultilineTextPane	_stcBaseCsvPath;

	/** 基準 CSV ファイルを開くボタン **/
	private JButton		_btnOpenBaseCsvFile;
	/** CSV ファイルから構成するボタン **/
	private JButton		_btnComposeByFile;
	/** フィールド追加ボタン **/
	private JButton		_btnFieldAdd;
	/** フィールド削除ボタン **/
	private JButton		_btnFieldDelete;
	/** フィールド左へ移動ボタン **/
	private JButton		_btnFieldMoveLeft;
	/** フィールド右へ移動ボタン **/
	private JButton		_btnFieldMoveRight;

	/** 入力スキーマテーブル **/
	private GenericInputCsvSchemaTable	_tableSchema;
	/** 基準 CSV テーブル **/
	private SpreadSheetTable	_tableBaseCsv;

	/** スプリッタ **/
	private JSplitPane			_splitSchema;
	/** 入力スキーマテーブル用スクロールペイン **/
	private JScrollPane			_scSchema;
	/** テーブル説明のテキストボックス **/
	private JTextField			_txtDesc;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public GenericInputSchemaEditDialog(Frame owner, GenericTableSchemaTreeNode orgTableData, GenericFilterEditModel editModel) {
		super(owner, true);
		if (editModel == null)
			throw new NullPointerException("GenericFilterEditModel object is null.");
		
		// データ格納
		_editModel    = editModel;
		_orgTableData = orgTableData;
		_schemaCsvModel = new GenericInputCsvSchemaTableModel(editModel, orgTableData);	// 編集用コピー生成
		if (orgTableData == null) {
			_schemaCsvModel.getDataModel().setHeaderRecordCount(DEF_SKIP_RECORDS);	// 新規の初期値
		}
		_baseCsvModel = new BaseCsvTableModel(_schemaCsvModel.getDataModel().getBaseCsvData());
		
		// 共通の初期化
		localCommonConstructor();
	}
	
	public GenericInputSchemaEditDialog(Dialog owner, GenericTableSchemaTreeNode orgTableData, GenericFilterEditModel editModel) {
		super(owner, true);
		if (editModel == null)
			throw new NullPointerException("GenericFilterEditModel object is null.");
		
		// データ格納
		_editModel    = editModel;
		_orgTableData = orgTableData;
		_schemaCsvModel = new GenericInputCsvSchemaTableModel(editModel, orgTableData);	// 編集用コピー生成
		if (orgTableData == null) {
			_schemaCsvModel.getDataModel().setHeaderRecordCount(DEF_SKIP_RECORDS);	// 新規の初期値
		}
		_baseCsvModel = new BaseCsvTableModel(_schemaCsvModel.getDataModel().getBaseCsvData());
		
		// 共通の初期化
		localCommonConstructor();
	}
	
	private void localCommonConstructor() {
		// タイトル設定
		if (_orgTableData == null) {
			// 新規作成
			setTitle(RunnerMessages.getInstance().GenericInputSchemaEditDlg_title_new);
		} else {
			// 編集
			setTitle(RunnerMessages.getInstance().GenericInputSchemaEditDlg_title_edit);
		}
		
		// アプリケーション設定情報の登録
		setConfiguration(AppSettings.GENERICINPUT_EDIT_DLG, AppSettings.getInstance().getConfiguration());
	}
	
	@Override
	public void initialComponent() {
		// コンポーネントの初期化
		super.initialComponent();
		
		// 設定情報の反映
		restoreConfiguration();
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public void restoreConfiguration() {
		super.restoreConfiguration();
		
		// restore divider location
		if (_splitSchema != null) {
			int dl = getConfiguration().getDividerLocation(getConfigurationPrefix());
			if (dl > 0)
				_splitSchema.setDividerLocation(dl);
		}
	}

	@Override
	public void storeConfiguration() {
		super.storeConfiguration();
		
		// store current divider location
		if (_splitSchema != null) {
			int dl = _splitSchema.getDividerLocation();
			getConfiguration().setDividerLocation(getConfigurationPrefix(), dl);
		}
	}

	/**
	 * ダイアログが表示されている場合はダイアログを閉じ、リソースを開放する。
	 * このメソッドでは、{@link java.awt.Window#dispose()} を呼び出す。
	 */
	public void destroy() {
		if (this.isDisplayable()) {
			dialogClose(DialogResult_Cancel);
		}
	}
	
	@Override
	protected boolean doCancelAction() {
		return super.doCancelAction();
	}

	@Override
	protected boolean doOkAction() {
		// チェック
		InputCsvTableSchemaEditModel tableModel = _schemaCsvModel.getDataModel();
		if (tableModel.isEmpty()) {
			// 要素が定義されていない
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericInputSchemaEdit_EmptyTable);
			return false;
		}
		
		// 列チェック
		ArrayList<InputCsvFieldSchemaEditModel> removedOrgFields = _schemaCsvModel.getRemovedOriginalFieldDataList();
		boolean removeReferenced = false;
		for (SchemaInputCsvDataField orgField : removedOrgFields) {
			if (_editModel.isPrecedentReferenceObject(orgField)) {
				removeReferenced = true;
				break;
			}
		}
		//--- オリジナルとデータ型が異なるかをチェック
		boolean referencedValueTypeChanged = false;
		int numFields = tableModel.size();
		for (int index = 0; index < numFields; ++index) {
			InputCsvFieldSchemaEditModel fieldModel = (InputCsvFieldSchemaEditModel)tableModel.get(index);
			if (fieldModel.hasOriginalSchema()) {
				SchemaInputCsvDataField orgField = fieldModel.getOriginalSchema();
				if (_editModel.isPrecedentReferenceObject(orgField) && orgField.getValueType() != fieldModel.getValueType()) {
					referencedValueTypeChanged = true;
					break;
				}
			}
		}
		if (removeReferenced || referencedValueTypeChanged) {
			// 最終確認
			int ret = JOptionPane.showConfirmDialog(this, RunnerMessages.getInstance().confirmReferencedInputCsvTableSchemaChanged,
					CommonMessages.getInstance().msgboxTitleWarn,
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (ret != JOptionPane.OK_OPTION) {
				// user canceled
				return false;
			}
		}
		
		// 値の保存
		tableModel.setDescription(_txtDesc.getText());
		tableModel.setHeaderRecordCount((Integer)_spinSkipRecords.getValue());
		tableModel.setBaseCsvData(_baseCsvModel.getDataModel());
		
		return super.doOkAction();
	}

	/**
	 * 新しい構成のデータモデルを取得する。
	 * @return	新しい構成のデータモデル
	 */
	public InputCsvTableSchemaEditModel getDataModel() {
		return _schemaCsvModel.getDataModel();
	}

	/**
	 * このダイアログの操作によって削除されたオリジナルの入力フィールドのリストを取得する。
	 * @return	削除されたフィールドデータを格納するリスト、削除されたものがない場合は要素が空のリスト
	 */
	public ArrayList<InputCsvFieldSchemaEditModel> getRemovedOriginalFieldList() {
		return _schemaCsvModel.getRemovedOriginalFieldDataList();
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected void initDialog() {
		super.initDialog();
		
		// 初期ボタン状態
		_btnFieldDelete.setEnabled(false);
		_btnFieldMoveLeft.setEnabled(false);
		_btnFieldMoveRight.setEnabled(false);
		
		// 初期値
		_txtDesc.setText(_schemaCsvModel.getDataModel().getDescription());
		_spinSkipRecords.setValue(_schemaCsvModel.getDataModel().getHeaderRecordCount());
	}

	/**
	 * スキーマ定義テーブルの選択状態が変更されたときに呼び出されるイベントハンドラ。
	 * @param lse	イベントオブジェクト
	 */
	protected void onSchemaTableSelectionChanged(ListSelectionEvent lse) {
		if (!_tableSchema.hasSelectedCells()) {
			// no selection
			_btnFieldDelete.setEnabled(false);
			_btnFieldMoveLeft.setEnabled(false);
			_btnFieldMoveRight.setEnabled(false);
			return;
		}
		
		// 選択されている
		_btnFieldDelete.setEnabled(true);
		int[] selColumns = _tableSchema.getSelectedColumns();
		int canShift = _schemaCsvModel.canMoveFields(selColumns);
		_btnFieldMoveLeft .setEnabled((canShift & GenericInputCsvSchemaTableModel.CAN_SHIFT_LEFT) != 0);
		_btnFieldMoveRight.setEnabled((canShift & GenericInputCsvSchemaTableModel.CAN_SHIFT_RIGHT) != 0);
	}

	/**
	 * [処理対象外レコード数] が変更されたときに呼び出されるイベントハンドラ。
	 * @param ce	イベントオブジェクト
	 */
	protected void onSkipRecordSpinnerChanged(ChangeEvent ce) {
		_schemaCsvModel.getDataModel().setHeaderRecordCount((Integer)_spinSkipRecords.getValue());
	}

	/**
	 * 各ボタンがクリックされたときに呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onActionButtonClicked(ActionEvent ae) {
		String commandKey = ae.getActionCommand();
		if (BTNCMD_COMPONSE_BY_BASECSV.equals(commandKey)) {
			// [基準 CSV ファイルから構成]
			doComposeByBaseCsv();
		}
		else if (BTNCMD_OPEN_BASECSV.equals(commandKey)) {
			// [基準 CSV ファイルを開く]
			doOpenBaseCsvFile();
		}
		else if (BTNCMD_FIELD_ADD.equals(commandKey)) {
			// [列の追加]
			doAddNewField();
		}
		else if (BTNCMD_FIELD_DELETE.equals(commandKey)) {
			// [列の削除]
			doDeleteSelectedFields();
		}
		else if (BTNCMD_FIELD_MOVELEFT.equals(commandKey)) {
			// [列を左へ移動]
			doMoveSelectedFields(-1);
		}
		else if (BTNCMD_FIELD_MOVERIGHT.equals(commandKey)) {
			// [列を右へ移動]
			doMoveSelectedFields(1);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean doComposeByBaseCsv() {
		// check exists
		if (!_schemaCsvModel.isEmpty()) {
			// 参照の有無をチェック
			boolean refered = false;
			int numFields = _schemaCsvModel.getDataModel().size();
			for (int index = 0; index < numFields; ++index) {
				InputCsvFieldSchemaEditModel fieldModel = (InputCsvFieldSchemaEditModel)_schemaCsvModel.getDataModel().get(index);
				if (fieldModel.hasOriginalSchema()) {
					// 参照の確認
					if (_editModel.isPrecedentReferenceObject(fieldModel.getOriginalSchema())) {
						// 参照されているオブジェクトがある
						refered = true;
						break;
					}
				}
			}
			// 確認メッセージ
			String msg;
			if (refered) {
				msg = RunnerMessages.getInstance().confirmClearIncludedReferencedInputCsvField;
			} else {
				msg = RunnerMessages.getInstance().confirmClearCurrentInputCsvTableSchema;
			}
			// すべて消去されることを確認
			int ret = JOptionPane.showConfirmDialog(this, msg,
					CommonMessages.getInstance().msgboxTitleWarn,
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (ret != JOptionPane.YES_OPTION) {
				// user canceled
				return false;
			}
		}
		
		// 基準ファイルが開いていない場合は、開く
		if (_baseCsvModel.isEmpty()) {
			if (!doOpenBaseCsvFile()) {
				// user canceled or error
				return false;
			}
		}
		
		// 全要素を削除
		_schemaCsvModel.clear();	// オリジナルデータがある場合は、保存
		
		// 基準ファイルが空なら処理終了
		if (_baseCsvModel.isEmpty()) {
			return true;
		}
		
		// 基準ファイルの内容からデータ型を自動的に判定
		SchemaDateTimeFormats dtformat = _editModel.getAvailableDateTimeFormats();
		InputCsvTableSchemaEditModel schemaModel = _schemaCsvModel.getDataModel();
		BaseCsvTableData csvData = _baseCsvModel.getDataModel();
		assert(schemaModel.isEmpty());
		int maxFields = csvData.getMaxFieldCount();
		int maxRecords = csvData.getRecordCount();
		int maxHeaders = Math.min(schemaModel.getHeaderRecordCount(), maxRecords);
		ArrayList<String> headerFields = new ArrayList<String>(maxHeaders);
		StringBuilder sb = new StringBuilder();
		for (int fieldIndex = 0; fieldIndex < maxFields; ++fieldIndex) {
			//--- initialize
			headerFields.clear();
			sb.setLength(0);
			SchemaValueType vtype = null;
			int recIndex = 0;
			//--- read header
			for (; recIndex < maxHeaders; ++recIndex) {
				String hvalue = null;
				List<String> rec = csvData.getRecord(recIndex);
				if (fieldIndex < rec.size()) {
					hvalue = rec.get(fieldIndex);
					if (hvalue != null && !hvalue.isEmpty()) {
						if (sb.length() > 0)
							sb.append('_');
						sb.append(hvalue);
					}
				}
				headerFields.add(hvalue==null ? "" : hvalue);
			}
			//--- read data
			for (; recIndex < maxRecords; ++recIndex) {
				List<String> rec = csvData.getRecord(recIndex);
				if (fieldIndex < rec.size()) {
					SchemaValueType curType = SchemaValueTypeManager.detectValueTypeByValue(dtformat, rec.get(fieldIndex));
					if (curType == SchemaValueTypeString.instance) {
						// 文字列型
						//--- 一度でも文字列型と判定された場合は、以降のデータは判定しない
						vtype = SchemaValueTypeString.instance;
						break;
					}
					else if (curType == SchemaValueTypeBoolean.instance) {
						// 真偽値型
						if (vtype != null && vtype != SchemaValueTypeBoolean.instance) {
							//--- 他のデータ型と混在のため、文字列型と判定
							vtype = SchemaValueTypeString.instance;
							break;
						}
						else {
							//--- 真偽値型を保存
							vtype = curType;
						}
					}
					else if (curType == SchemaValueTypeDecimal.instance) {
						// 値型
						if (vtype != null && vtype != SchemaValueTypeDecimal.instance) {
							//--- 他のデータ型と混在のため、文字列型と判定
							vtype = SchemaValueTypeString.instance;
							break;
						}
						else {
							//--- 数値型を保存
							vtype = curType;
						}
					}
					else if (curType == SchemaValueTypeDateTime.instance) {
						// 日付時刻型
						if (vtype != null && vtype != SchemaValueTypeDateTime.instance) {
							//--- 他のデータ型と混在のため、文字列型と判定
							vtype = SchemaValueTypeString.instance;
							break;
						}
						else {
							//--- 日付時刻型を保存
							vtype = curType;
						}
					}
					// else--- 前の判定結果を変更しない
				}
			}
			//--- データ型とフィールド名を保存
			InputCsvFieldSchemaEditModel field = new InputCsvFieldSchemaEditModel(vtype);
			if (!headerFields.isEmpty()) {
				field.setHeaderValues(headerFields.toArray(new String[headerFields.size()]));
			}
			field.setName(sb.toString());
			schemaModel.add(field);
		}
		
		// テーブルを更新
		_schemaCsvModel.fireTableStructureChanged();
		return true;
	}
	
	protected boolean doOpenBaseCsvFile() {
		// get Last file
		File file = AppSettings.getInstance().getLastFile(getConfigurationPrefix());
		
		// Chooser
		File selectedFile = FileChooserManager.chooseOpenFile(this, null, false, file, FileChooserManager.getCsvFileFilter());
		if (selectedFile == null)
			return false;	// user canceled
		//--- save path
		AppSettings.getInstance().setLastFile(getConfigurationPrefix(), selectedFile);
		
		// open CSV file
		if (AppLogger.isDebugEnabled()) {
			AppLogger.debug("Open Base CSV file for GenericFilter : \"" + selectedFile.toString() + "\"");
		}
		BaseCsvTableData csvdata = new BaseCsvTableData(selectedFile, AppSettings.getInstance().getAadlCsvEncodingName());
		CsvFileReader csvReader = null;
		try {
			// open file
			if (csvdata.getEncoding() == null) {
				csvReader = new CsvFileReader(selectedFile);
			} else {
				csvReader = new CsvFileReader(selectedFile, csvdata.getEncoding());
			}
			
			// read record
			Iterator<List<String>> it = csvReader.iterator(); 
			for (int reccnt = 0; reccnt < NUM_READ_RECORDS && it.hasNext(); ++reccnt) {
				List<String> rec = it.next();
				csvdata.addRecord(rec);
			}
		}
		catch (OutOfMemoryError ex) {
			String errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
			return false;
		}
		catch (FileNotFoundException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_NOTFOUND, ex, selectedFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
			return false;
		}
		catch (UnsupportedEncodingException ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_UNSUPPORTED_ENCODING, ex);
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
			return false;
		}
		catch (Throwable ex) {
			String errmsg = CommonMessages.getErrorMessage(CommonMessages.MessageID.ERR_FILE_READ, ex, selectedFile.getAbsolutePath());
			AppLogger.error(errmsg, ex);
			ModuleRunner.showErrorMessage(this, errmsg);
			return false;
		}
		finally {
			if (csvReader != null) {
				csvReader.close();
			}
			csvReader = null;
		}
		
		// データを表示
		updateBaseCsvTitle(csvdata.getFile());
		_baseCsvModel.setCsvTableData(csvdata);
		return true;
	}
	
	protected void updateBaseCsvTitle(File csvFilePath) {
		String text = (csvFilePath == null ? "" : csvFilePath.toString());
		_stcBaseCsvPath.setText(text);
	}
	
	protected boolean doAddNewField() {
		_schemaCsvModel.addNewField();
		int selidx = _schemaCsvModel.getColumnCount() - 1;
		_tableSchema.setCellSelected(0, selidx);
		_tableSchema.scrollToVisibleCell(0, selidx);
		return true;
	}
	
	protected boolean doDeleteSelectedFields() {
		if (!_tableSchema.hasSelectedCells())
			return false;
		
		int[] columns = _tableSchema.getSelectedColumns();
		if (columns == null || columns.length == 0)
			return false;
		
		// 参照の有無をチェック
		boolean refered = false;
		for (int i = 0; i < columns.length; ++i) {
			InputCsvFieldSchemaEditModel field = (InputCsvFieldSchemaEditModel)_schemaCsvModel.getDataModel().get(columns[i]);
			if (field.hasOriginalSchema()) {
				// 参照の確認
				if (_editModel.isPrecedentReferenceObject(field.getOriginalSchema())) {
					// 参照されているオブジェクトがある
					refered = true;
					break;
				}
			}
		}
		if (refered) {
			String msg;
			if (columns.length > 1) {
				// 複数
				msg = RunnerMessages.getInstance().confirmDeleteIncludedReferencedInputCsvField;
			} else {
				// 単一
				msg = RunnerMessages.getInstance().confirmDeleteCurrentReferencedInputCsvField;
			}
			int ret = JOptionPane.showConfirmDialog(this, msg,
					CommonMessages.getInstance().msgboxTitleWarn,
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (ret != JOptionPane.YES_OPTION) {
				// user canceled
				return false;
			}
		}

		// 削除
		int minselidx = _tableSchema.getMinSelectionColumnIndex();
		if (!_schemaCsvModel.removeFields(_tableSchema.getSelectedColumns()))
			return false;
		
		// reselect
		int numcols = _schemaCsvModel.getColumnCount();
		if (numcols > 0) {
			if (minselidx >= numcols)
				minselidx = numcols - 1;
			_tableSchema.setCellSelected(0, minselidx);
			_tableSchema.scrollToVisibleCell(0, minselidx);
		}
		return true;
	}
	
	protected boolean doMoveSelectedFields(int direction) {
		if (!_tableSchema.hasSelectedCells())
			return false;

		int[] newSelIndices = _schemaCsvModel.moveFields(direction, _tableSchema.getSelectedColumns());
		if (newSelIndices == null)
			return false;
		
		// reselect
		_tableSchema.clearSelection();
		int showColIndex;
		if (direction < 0) {
			// left
			showColIndex = newSelIndices[0];
		} else {
			// right
			showColIndex = newSelIndices[newSelIndices.length - 1];
		}
		for (int i = 0; i < newSelIndices.length; ++i) {
			_tableSchema.addColumnSelectionInterval(newSelIndices[i], newSelIndices[i]);
		}
		_tableSchema.setRowSelectionInterval(0, 0);
		_tableSchema.scrollToVisibleCell(0, showColIndex);
		return true;
	}

	/**
	 * 関連するコンポーネントとともに、スキーマ定義用ツールバーを生成する。
	 * @return	生成されたツールバー
	 */
	protected JToolBar createSchemaToolBar() {
		// create label
		JLabel lbl = GenericFilterSwingTools.createToolBarLabel(RunnerMessages.getInstance().GenericInputSchemaEditDlg_lbl_CsvSchema);
		
		// create toolbar
		JToolBar tbar = new JToolBar(JToolBar.HORIZONTAL);
		tbar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		tbar.setFloatable(false);
		tbar.add(lbl);
		tbar.add(Box.createGlue());

		return tbar;
	}

	/**
	 * 関連するコンポーネントとともに、基準 CSV データテーブル用ツールバーを生成する。
	 * @return	生成されたツールバー
	 */
	protected JToolBar createBaseCsvToolBar() {
		// create label
		JLabel lbl = GenericFilterSwingTools.createToolBarLabel(RunnerMessages.getInstance().GenericInputSchemaEditDlg_lbl_BaseCsv);
		
		// create toolbar
		JToolBar tbar = new JToolBar(JToolBar.HORIZONTAL);
		tbar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		tbar.setFloatable(false);
		tbar.add(lbl);
		tbar.add(Box.createGlue());
		
		return tbar;
	}

	/**
	 * 基準 CSV データテーブルのコンポーネントを生成する。
	 * @param tableModel	テーブルモデル
	 * @return	生成されたコンポーネント
	 */
	protected SpreadSheetTable createBaseCsvTable(BaseCsvTableModel tableModel) {
		SpreadSheetTable table = new SpreadSheetTable(tableModel);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);	// 列入れ替え禁止
		table.getTableHeader().setResizingAllowed(true);	// 列サイズ変更可能
		table.setupVisibleGrid();	// グリッド表示(Mac対策)
		return table;
	}

	/**
	 * 入力スキーマの設定テーブルコンポーネントを生成する。
	 * @return	生成されたコンポーネント
	 */
	protected GenericInputCsvSchemaTable createSchemaTable() {
		GenericInputCsvSchemaTable table = new GenericInputCsvSchemaTable(_schemaCsvModel);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);	// 列入れ替え禁止
		table.getTableHeader().setResizingAllowed(true);	// 列サイズ変更可能
		table.setupVisibleGrid();	// グリッド表示(Mac対策)
		return table;
	}

	/**
	 * 関連するコンポーネントとともに、基準 CSV ファイル情報パネルを生成する。
	 * @return	生成されたパネル
	 */
	protected JPanel createBaseCsvInfoPanel() {
		// create path label
		JLabel lblPath = new JLabel(RunnerMessages.getInstance().GenericInputSchemaEditDlg_lbl_BasePath + " : ");
		_stcBaseCsvPath = new JStaticMultilineTextPane();

		// create button
		_btnOpenBaseCsvFile = CommonResources.createBrowseButton(RunnerMessages.getInstance().GenericInputSchemaEditDlg_btn_OpenBaseCsv);
		
		// create panel
		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// setup layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill   = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- path label
		pnl.add(lblPath, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(_stcBaseCsvPath, gbc);
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx++;
		//--- browse button
		gbc.anchor = GridBagConstraints.NORTHEAST;
		pnl.add(_btnOpenBaseCsvFile, gbc);
		
		return pnl;
	}

	/**
	 * 処理対象外レコード数のコンポーネントを生成する。
	 * @return	生成されたコンポーネント
	 */
	protected JSpinner createSkipRecordsSpinner() {
		SpinnerNumberModel model = new SpinnerNumberModel(0, MIN_SKIP_RECORDS, MAX_SKIP_RECORDS, 1);
		JSpinner spin = new JSpinner(model);
		return spin;
	}
	
	protected JPanel createCsvSchemaConfigPanel() {
		// create Skip Records spinner
		JLabel lblSkip = new JLabel(RunnerMessages.getInstance().GenericInputSchemaEditDlg_lbl_SkipRecords + " : ");
		SpinnerNumberModel model = new SpinnerNumberModel(0, MIN_SKIP_RECORDS, MAX_SKIP_RECORDS, 1);
		_spinSkipRecords = new JSpinner(model);
		
		// create desc textbox
		JLabel lblDesc = new JLabel(RunnerMessages.getInstance().GenericFilterEditDlg_name_Desc + " : ");
		_txtDesc = new JTextField();
		
		// create buttons
		_btnComposeByFile = new JButton(RunnerMessages.getInstance().GenericInputSchemaEditDlg_btn_ComposeByCsv);
		_btnFieldAdd    = CommonResources.createIconButton(CommonResources.ICON_ADD, RunnerMessages.getInstance().GenericInputSchemaEditDlg_btn_AddField);
		_btnFieldDelete = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		_btnFieldMoveLeft  = CommonResources.createIconButton(CommonResources.ICON_ARROW_LEFT, CommonMessages.getInstance().Button_Left);
		_btnFieldMoveRight = CommonResources.createIconButton(CommonResources.ICON_ARROW_RIGHT, CommonMessages.getInstance().Button_Right);
		
		// create box
		Box box = Box.createHorizontalBox();
		box.add(Box.createGlue());
		box.add(Box.createHorizontalStrut(5));
		box.add(_btnComposeByFile);
		box.add(Box.createHorizontalStrut(5));
		box.add(_btnFieldAdd);
		box.add(Box.createHorizontalStrut(2));
		box.add(_btnFieldDelete);
		box.add(Box.createHorizontalStrut(2));
		box.add(_btnFieldMoveLeft);
		box.add(Box.createHorizontalStrut(2));
		box.add(_btnFieldMoveRight);
		
		// cretae panel
		JPanel pnl = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 5, 0);
		//--- txtbox
		pnl.add(lblDesc, gbc);
		gbc.gridx++;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(_txtDesc, gbc);
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		//--- spin
		pnl.add(lblSkip, gbc);
		gbc.gridx++;
		pnl.add(_spinSkipRecords, gbc);
		gbc.gridx++;
		//--- buttons
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(box, gbc);
		
		return pnl;
	}

	/**
	 * 関連するコンポーネントとともに、CSV 入力スキーマ編集パネルを生成する。
	 * @return	生成されたパネル
	 */
	protected JPanel createInputSchemaTablePane() {
		// create Tool bar
		_tbarInputSchema = createSchemaToolBar();
		
		// create table
		_tableSchema = createSchemaTable();
		//---
		_scSchema = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_scSchema.setViewportView(_tableSchema);
		_scSchema.setRowHeaderView(_tableSchema.getTableRowHeader());
		_scSchema.setCorner(JScrollPane.UPPER_LEFT_CORNER, SpreadSheetTable.createUpperLeftCornerComponent());
		
		// サイズ調整
		int minWidth = 52;
		Dimension dm = _scSchema.getPreferredSize();
		_scSchema.setPreferredSize(new Dimension(dm.width, minWidth));
		dm = _scSchema.getMinimumSize();
		_scSchema.setMinimumSize(new Dimension(dm.width, minWidth));
		
		// create config panel
		JPanel pnlConfig = createCsvSchemaConfigPanel();
		pnlConfig.setBorder(BorderFactory.createEmptyBorder(5, 5, 2, 3));

		// setup body panel
		JPanel pnlBody = new JPanel(new BorderLayout());
		pnlBody.add(pnlConfig, BorderLayout.NORTH);
		pnlBody.add(_scSchema, BorderLayout.CENTER);
		
		// setup panel
		JPanel pnl = new JPanel(new BorderLayout());
		pnl.add(_tbarInputSchema, BorderLayout.NORTH);
		pnl.add(pnlBody, BorderLayout.CENTER);
		
		return pnl;
	}

	/**
	 * 関連するコンポーネントとともに、基準 CSV ファイルパネルを生成する。
	 * @return	生成されたパネル
	 */
	protected JPanel createBaseCsvTablePane() {
		// create Tool bar
		_tbarBaseCsv = createBaseCsvToolBar();
		
		// create table
		_tableBaseCsv = createBaseCsvTable(_baseCsvModel);
		JScrollPane scTable = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scTable.setViewportView(_tableBaseCsv);
		scTable.setRowHeaderView(_tableBaseCsv.getTableRowHeader());
		scTable.setCorner(JScrollPane.UPPER_LEFT_CORNER, SpreadSheetTable.createUpperLeftCornerComponent());
		
		// create info panel
		JPanel pnlInfo = createBaseCsvInfoPanel();
		
		// setup body panel
		JPanel pnlBody = new JPanel(new BorderLayout());
		pnlBody.add(pnlInfo, BorderLayout.NORTH);
		pnlBody.add(scTable, BorderLayout.CENTER);
		
		// setup panel
		JPanel pnl = new JPanel(new BorderLayout());
		pnl.add(_tbarBaseCsv, BorderLayout.NORTH);
		pnl.add(pnlBody, BorderLayout.CENTER);
		return pnl;
	}

	@Override
	protected void setupMainContents() {
		// create components
		JPanel pnlInputSchema  = createInputSchemaTablePane();
		JPanel pnlBaseCsvTable = createBaseCsvTablePane();
		
		// layout main panel
		_splitSchema = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		_splitSchema.setResizeWeight(0);
		_splitSchema.setTopComponent(pnlInputSchema);
		_splitSchema.setBottomComponent(pnlBaseCsvTable);
		
		// setup values
		updateBaseCsvTitle(_baseCsvModel.getDataModel().getFile());
		
		// set to main content panel
		getContentPane().add(_splitSchema, BorderLayout.CENTER);
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
	protected void setupActions() {
		super.setupActions();
		
		// spinner action
		_spinSkipRecords.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				onSkipRecordSpinnerChanged(ce);
			}
		});
		
		// Schema table selection changes
		ListSelectionListener tableSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				onSchemaTableSelectionChanged(lse);
			}
		};
		_tableSchema.getSelectionModel().addListSelectionListener(tableSelectionListener);
		_tableSchema.getColumnModel().getSelectionModel().addListSelectionListener(tableSelectionListener);
		
		// Button action
		ActionListener btnListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onActionButtonClicked(ae);
			}
		};
		_btnComposeByFile.setActionCommand(BTNCMD_COMPONSE_BY_BASECSV);
		_btnComposeByFile.addActionListener(btnListener);
		_btnOpenBaseCsvFile.setActionCommand(BTNCMD_OPEN_BASECSV);
		_btnOpenBaseCsvFile.addActionListener(btnListener);
		_btnFieldAdd.setActionCommand(BTNCMD_FIELD_ADD);
		_btnFieldAdd.addActionListener(btnListener);
		_btnFieldDelete.setActionCommand(BTNCMD_FIELD_DELETE);
		_btnFieldDelete.addActionListener(btnListener);
		_btnFieldMoveLeft.setActionCommand(BTNCMD_FIELD_MOVELEFT);
		_btnFieldMoveLeft.addActionListener(btnListener);
		_btnFieldMoveRight.setActionCommand(BTNCMD_FIELD_MOVERIGHT);
		_btnFieldMoveRight.addActionListener(btnListener);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * 基準 CSV データ用のテーブルモデル。
	 * @version 3.2.0
	 * @since 3.2.0
	 */
	protected class BaseCsvTableModel extends AbSpreadSheetTableModel
	{
		private static final long serialVersionUID = 1L;
		
		private BaseCsvTableData	_csvData;
		
		public BaseCsvTableModel(BaseCsvTableData csvData) {
			super();
			if (csvData == null) {
				_csvData = new BaseCsvTableData();
			} else {
				_csvData = csvData;
			}
		}
		
		public void setCsvTableData(BaseCsvTableData csvData) {
			if (csvData == null) {
				_csvData = new BaseCsvTableData();
			} else {
				_csvData = csvData;
			}
			fireTableStructureChanged();
		}
		
		public BaseCsvTableData getDataModel() {
			return _csvData;
		}
		
		public boolean isEmpty() {
			return _csvData.isEmpty();
		}

		@Override
		public int getRowCount() {
			return _csvData.getRecordCount();
		}

		@Override
		public int getColumnCount() {
			return _csvData.getMaxFieldCount();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return _csvData.getCell(rowIndex, columnIndex);
		}

		@Override
		public String getColumnName(int columnIndex) {
			return Integer.toString(columnIndex+1);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}
}
