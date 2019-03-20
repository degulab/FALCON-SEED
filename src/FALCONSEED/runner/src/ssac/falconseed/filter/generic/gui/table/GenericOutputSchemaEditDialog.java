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
 * @(#)GenericOutputSchemaEditDialog.java	3.2.1	2015/07/22
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.falconseed.filter.generic.gui.ComboBoxOperandGroupItem;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.falconseed.filter.generic.gui.GenericSchemaValueGroups;
import ssac.falconseed.filter.generic.gui.exp.ComboBoxExpressionOperandModel;
import ssac.falconseed.filter.generic.gui.exp.GenericExpressionElementEditModel;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaComboBox;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.swing.AbBasicDialog;
import ssac.util.swing.Application;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * 汎用フィルタの出力テーブル定義編集ダイアログ。
 * 
 * @version 3.2.1
 * @since 3.2.1
 */
public class GenericOutputSchemaEditDialog extends AbBasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 4024246032964698394L;

	/** 最小処理対象外レコード数 **/
	static protected int MIN_SKIP_RECORDS	= 0;
	/** 最大処理対象外レコード数 **/
	static protected int MAX_SKIP_RECORDS	= 10;
	/** 読み込むデータレコード数 **/
	static protected int NUM_READ_RECORDS	= MAX_SKIP_RECORDS + 5;

	//	static private final int FIXED_DESC_HEIGHT = 150;
	static private final Dimension DM_MIN_SIZE = new Dimension(640, 480);

	static protected final String BTNCMD_FIELD_ADD			= "field.add";
	static protected final String BTNCMD_FIELD_UPDATE		= "field.update";
	static protected final String BTNCMD_FIELD_DELETE		= "field.delete";
	static protected final String BTNCMD_FIELD_MOVELEFT		= "field.move.left";
	static protected final String BTNCMD_FIELD_MOVERIGHT	= "field.move.right";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 編集対象の出力テーブルスキーマのツリーノード **/
	private final GenericTableSchemaTreeNode	_orgTableData;
	/** CSV 出力スキーマテーブルのデータモデル **/
	private final GenericOutputCsvSchemaTableModel	_schemaCsvModel;
	/** 汎用フィルタ編集用データモデル **/
	private final GenericFilterEditModel		_editModel;

	/** ヘッダーレコード数を設定するスピンコンポーネント **/
	private JSpinner	_spinSkipRecords;

	/** ヘッダーレコードに関するラジオボタンのグループ **/
	private ButtonGroup		_rdoGroupHeaderRecord;
	/** ヘッダーレコードをフィールド名から自動で生成することを示すラジオボタン **/
	private JRadioButton	_rdoAutoHeaderRecord;
	/** ヘッダーレコードを手動で設定することを示すラジオボタン **/
	private JRadioButton	_rdoManualHeaderRecord;

	/** コンボボックスモデル：要素が空のモデル **/
	private DefaultComboBoxModel			_cmodelEmpty;
	/** コンボボックスモデル：入力スキーマ選択用モデル **/
//	private ComboBoxInputOperandModel		_cmodelInputFields;
	private ComboBoxInputSelectionModel		_cmodelInputFields;
	/** コンボボックスモデル：計算式洗濯用モデル **/
	private ComboBoxExpressionOperandModel	_cmodelExpElements;
	/** コンボボックス要素：入力スキーマの出力対象グループアイテム **/
	private ComboBoxOperandGroupItem		_cmbgrpInputFieldsItem;
	/** コンボボックス要素：計算式の出力対象グループアイテム **/
	private ComboBoxOperandGroupItem		_cmbgrpExpElementsItem;

	/** 出力対象のグループ選択用コンボボックス **/
	private JComboBox	_cmbTargetGroup;
	/** 出力対象の値選択用コンボボックス **/
	private GenericSchemaComboBox	_cmbTargetValue;
	/** 出力対象の入力テーブル選択用コンボボックス **/
	private GenericSchemaComboBox	_cmbInputTable;
	/** 出力対象の名前用テキストボックス(表示のみ) **/
	private JTextField		_txtTargetName;

	/** フィールド追加ボタン **/
	private JButton		_btnFieldAdd;
	/** フィールド更新ボタン **/
	private JButton		_btnFieldUpdate;
	/** フィールド削除ボタン **/
	private JButton		_btnFieldDelete;
	/** フィールド左へ移動ボタン **/
	private JButton		_btnFieldMoveLeft;
	/** フィールド右へ移動ボタン **/
	private JButton		_btnFieldMoveRight;

	/** 出力スキーマテーブル **/
	private GenericOutputCsvSchemaTable	_tableSchema;

	/** 出力スキーマテーブル用スクロールペイン **/
	private JScrollPane			_scSchema;
	/** テーブル説明のテキストボックス **/
	private JTextField			_txtDesc;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public GenericOutputSchemaEditDialog(Frame owner, GenericTableSchemaTreeNode orgTableData, GenericFilterEditModel editModel) {
		super(owner, true);
		if (editModel == null)
			throw new NullPointerException("GenericFilterEditModel object is null.");
		
		// データ格納
		_editModel    = editModel;
		_orgTableData = orgTableData;
		_schemaCsvModel = new GenericOutputCsvSchemaTableModel(editModel, orgTableData);	// 編集用コピー生成
		
		// 共通の初期化
		localCommonConstructor();
	}
	
	public GenericOutputSchemaEditDialog(Dialog owner, GenericTableSchemaTreeNode orgTableData, GenericFilterEditModel editModel) {
		super(owner, true);
		if (editModel == null)
			throw new NullPointerException("GenericFilterEditModel object is null.");
		
		// データ格納
		_editModel    = editModel;
		_orgTableData = orgTableData;
		_schemaCsvModel = new GenericOutputCsvSchemaTableModel(editModel, orgTableData);	// 編集用コピー生成
		
		// 共通の初期化
		localCommonConstructor();
	}
	
	private void localCommonConstructor() {
		// タイトル設定
		if (_orgTableData == null) {
			// 新規作成
			setTitle(RunnerMessages.getInstance().GenericOutputSchemaEditDlg_title_new);
		} else {
			// 編集
			setTitle(RunnerMessages.getInstance().GenericOutputSchemaEditDlg_title_edit);
		}
		
		// コンボボックスモデル生成
		_cmodelEmpty = new DefaultComboBoxModel();
//		_cmodelInputFields = new ComboBoxInputOperandModel(_editModel);	// 元のツリーは監視しない
		_cmodelInputFields = new ComboBoxInputSelectionModel(_editModel);	// 元のツリーは監視しない
		_cmodelExpElements = new ComboBoxExpressionOperandModel(_editModel.getExpressionSchemaTableModel());	// 元のテーブルは監視しない
		_cmbgrpInputFieldsItem = new ComboBoxOperandGroupItem(GenericSchemaValueGroups.INPUT, _cmodelInputFields);
		_cmbgrpExpElementsItem = new ComboBoxOperandGroupItem(GenericSchemaValueGroups.EXP, _cmodelExpElements);
		
		// アプリケーション設定情報の登録
		setConfiguration(AppSettings.GENERICOUTPUT_EDIT_DLG, AppSettings.getInstance().getConfiguration());
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
	}

	@Override
	public void storeConfiguration() {
		super.storeConfiguration();
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
		// フィールド数チェック
		//--- フィールド数=0 でもOK
		int numFields = _schemaCsvModel.getDataModel().size();
		
		// 列定義チェック
		if (numFields > 0) {
			// 出力対象をチェック
			for (int index = 0; index < numFields; ++index) {
				OutputCsvFieldSchemaEditModel fieldModel = (OutputCsvFieldSchemaEditModel)_schemaCsvModel.getDataModel().get(index);
				if (!fieldModel.hasTargetValue()) {
					_tableSchema.clearSelection();
					_tableSchema.setCellSelected(0, index);
					Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericOutputSchemaEdit_NoTargetValue);
					return false;
				}
			}
		}
		else {
			// 列が指定されていない
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericOutputSchemaEdit_EmptyTable);
			return false;
		}
		
		// 値の保存
		_schemaCsvModel.getDataModel().setDescription(_txtDesc.getText());
		
		// 変更の反映
		return super.doOkAction();
	}

	/**
	 * 汎用フィルタの編集データモデルを取得する。
	 * @return	汎用フィルタ編集用データモデル
	 */
	public GenericFilterEditModel getEditModel() {
		return _editModel;
	}

	/**
	 * 新しい構成のデータモデルを取得する。
	 * @return	新しい構成のデータモデル
	 */
	public OutputCsvTableSchemaEditModel getDataModel() {
		return _schemaCsvModel.getDataModel();
	}

	/**
	 * 編集によって削除された、オリジナルのフィールドデータモデルのリストを取得する。
	 * @return	削除されたオリジナルフィールドデータモデルのリスト、オリジナルが削除されていない場合は要素が空のリスト
	 */
	public ArrayList<OutputCsvFieldSchemaEditModel> getRemovedOriginalFields() {
		return _schemaCsvModel.getRemovedOriginalFieldDataList();
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected void initDialog() {
		super.initDialog();
		
		// 初期ボタン状態
		_btnFieldUpdate.setEnabled(false);
		_btnFieldDelete.setEnabled(false);
		_btnFieldMoveLeft.setEnabled(false);
		_btnFieldMoveRight.setEnabled(false);
		
		// 初期値
		_txtDesc.setText(_schemaCsvModel.getDataModel().getDescription());
		_spinSkipRecords.setValue(_schemaCsvModel.getDataModel().getHeaderRecordCount());
		
		// refresh initial values
		if (_schemaCsvModel.isAutoHeaderRecordEnabled()) {
			_rdoManualHeaderRecord.setSelected(false);
			_rdoAutoHeaderRecord.setSelected(true);
			_spinSkipRecords.setEnabled(false);
		} else {
			_rdoAutoHeaderRecord.setSelected(false);
			_rdoManualHeaderRecord.setSelected(true);
			_spinSkipRecords.setEnabled(true);
		}
		
		// プロパティの初期状態
		setSelectedFieldProperty(null);
	}

	@Override
	protected void onWindowClosed(WindowEvent e) {
		super.onWindowClosed(e);
	}

	/**
	 * スキーマ定義テーブルの選択状態が変更されたときに呼び出されるイベントハンドラ。
	 */
	protected void onSchemaTableSelectionChanged() {
		if (!_tableSchema.hasSelectedCells()) {
			// no selection
			_btnFieldUpdate.setEnabled(false);
			_btnFieldDelete.setEnabled(false);
			_btnFieldMoveLeft.setEnabled(false);
			_btnFieldMoveRight.setEnabled(false);
			//--- 出力対象プロパティの内容はそのままとする
			return;
		}
		
		// 選択列の取得
		_btnFieldDelete.setEnabled(true);	// 選択されているので、削除ボタン有効
		int[] selColumns = _tableSchema.getSelectedColumns();
		
		// 選択列の出力対象プロパティの更新
		if (selColumns.length == 1) {
			// 単一選択の場合のみ、選択された列の出力対象を表示
			_btnFieldUpdate.setEnabled(true);	// 単一選択なら、更新可能
			setSelectedFieldProperty(_tableSchema.getValueAt(GenericOutputCsvSchemaTableModel.ROWIDX_TARGETVALUE, selColumns[0]));
		}
		else {
			// 複数選択なら、出力対象プロパティの内容は変更しない
			_btnFieldUpdate.setEnabled(false);	// 複数選択の場合は、更新不可
		}
		
		// 移動可能かを判定
		int canShift = _schemaCsvModel.canMoveFields(selColumns);
		_btnFieldMoveLeft .setEnabled((canShift & GenericInputCsvSchemaTableModel.CAN_SHIFT_LEFT) != 0);
		_btnFieldMoveRight.setEnabled((canShift & GenericInputCsvSchemaTableModel.CAN_SHIFT_RIGHT) != 0);
	}

	/**
	 * [ヘッダーレコード数] が変更されたときに呼び出されるイベントハンドラ。
	 * @param ce	イベントオブジェクト
	 */
	protected void onSkipRecordSpinnerChanged(ChangeEvent ce) {
		_schemaCsvModel.setHeaderRecordCount((Integer)_spinSkipRecords.getValue());
	}

	/**
	 * ヘッダーレコードの生成方法に関するラジオボタンがクリックされたときに呼び出されるイベントハンドラ。
	 * @param ce	イベントオブジェクト
	 */
	protected void onHeaderRecordRadioButtonStateChanged(ChangeEvent ce) {
		boolean enableAutoHeaderRecord = _rdoAutoHeaderRecord.isSelected();
		if (_schemaCsvModel.setAutoHeaderRecordEnabled(enableAutoHeaderRecord)) {
			// modified
			_spinSkipRecords.setEnabled(!enableAutoHeaderRecord);
		}
	}

	/**
	 * 出力対象グループコンボボックスの選択が変更されたときに呼び出されるイベントハンドラ。
	 */
	protected void onTargetGroupItemChanged() {
		ComboBoxOperandGroupItem grpItem = (ComboBoxOperandGroupItem)_cmbTargetGroup.getSelectedItem();
		if (grpItem == _cmbgrpInputFieldsItem) {
			// 入力スキーマグループ
			if (_cmbTargetValue.getModel() != _cmodelInputFields.getFieldComboBoxModel()) {
				//--- モデル変更
				_cmbInputTable.setVisible(true);
				_cmbInputTable.setModel(_cmodelInputFields);
				_cmbTargetValue.setModel(_cmodelInputFields.getFieldComboBoxModel());
				if (_cmodelInputFields.getSize() > 0) {
					_cmbInputTable.setSelectedIndex(0);
					if (_cmodelInputFields.getFieldComboBoxModel().getSize() > 0) {
						_cmbTargetValue.setSelectedIndex(0);
					}
				}
			}
		}
		else if (grpItem == _cmbgrpExpElementsItem) {
			// 計算式グループ
			if (_cmbTargetValue.getModel() != _cmodelExpElements) {
				//--- モデル変更
				_cmbInputTable.setVisible(false);
				_cmbInputTable.setModel(_cmodelEmpty);
				_cmbTargetValue.setModel(_cmodelExpElements);
				if (_cmodelExpElements.getSize() > 0) {
					_cmbTargetValue.setSelectedIndex(0);
				}
			}
		}
		else {
			// unknown
			if (_cmbTargetValue.getModel() != _cmodelEmpty) {
				//--- モデル変更
				_cmbInputTable.setVisible(false);
				_cmbInputTable.setModel(_cmodelEmpty);
				_cmbTargetValue.setModel(_cmodelEmpty);
			}
		}
		
		// update name text field
		onTargetValueItemChanged();
	}

	/**
	 * 出力対象の値コンボボックスの選択が変更されたときに呼び出されるイベントハンドラ。
	 */
	protected void onTargetValueItemChanged() {
		SchemaElementValue selected = (SchemaElementValue)_cmbTargetValue.getSelectedItem();
		if (selected == null) {
			_txtTargetName.setText("");
		} else {
			_txtTargetName.setText(selected.getName());
		}
	}

	/**
	 * 各ボタンがクリックされたときに呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onActionButtonClicked(ActionEvent ae) {
		String commandKey = ae.getActionCommand();
		if (BTNCMD_FIELD_ADD.equals(commandKey)) {
			// [列の追加]
			doAddNewField();
		}
		else if (BTNCMD_FIELD_UPDATE.equals(commandKey)) {
			// [出力対象の更新]
			doUpdateSelectedField();
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

	/**
	 * フィールドの出力対象プロパティで選択されている値を設定する。
	 * @param targetValue	選択対象として設定する値
	 */
	protected void setSelectedFieldProperty(Object targetValue) {
		if (targetValue instanceof InputCsvFieldSchemaEditModel) {
			// input field schema
			_cmbTargetGroup.setSelectedItem(_cmbgrpInputFieldsItem);
			_cmodelInputFields.setSelectedItem(targetValue);
//			_txtTargetName.setText(((InputCsvFieldSchemaEditModel)targetValue).getName());
		}
		else if (targetValue instanceof GenericExpressionElementEditModel) {
			// expression element schema
			_cmbTargetGroup.setSelectedItem(_cmbgrpExpElementsItem);
			_cmbTargetValue.setSelectedItem(targetValue);
//			_txtTargetName.setText(((GenericExpressionElementEditModel)targetValue).getName());
		}
		else {
			// unknown
			_cmbTargetGroup.setSelectedItem(null);
			//--- 値のコンボボックスモデル変更は、上記のイベントハンドラで処理
//			_txtTargetName.setText(null);
		}
	}

	/**
	 * フィールドの出力対象プロパティで選択されている値を取得する。
	 * @return	選択されている出力対象値、選択されていない場合は <tt>null</tt>
	 */
	protected SchemaElementValue getSelectedFieldProperty() {
		Object aValue = _cmbTargetValue.getSelectedItem();
		if (aValue instanceof SchemaElementValue) {
			return (SchemaElementValue)aValue;
		} else {
			return null;
		}
	}
	
	protected boolean doAddNewField() {
		SchemaElementValue selected = getSelectedFieldProperty();
		if (selected == null) {
			// 出力対象が未選択
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericOutputSchemaEdit_NoTargetValue);
			return false;
		}
		_schemaCsvModel.addNewField(selected);
		int selidx = _schemaCsvModel.getColumnCount() - 1;
		_tableSchema.setCellSelected(0, selidx);
		_tableSchema.scrollToVisibleCell(0, selidx);
		return true;
	}
	
	protected boolean doUpdateSelectedField() {
		if (_tableSchema.getSelectedColumnCount() != 1)
			return false;	// 単一列の選択ではない場合は、処理しない
		
		SchemaElementValue selected = getSelectedFieldProperty();
		if (selected == null) {
			// 出力対象が未選択
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericOutputSchemaEdit_NoTargetValue);
			return false;
		}

		int column = _tableSchema.getSelectedColumn();
		return _schemaCsvModel.updateFieldTargetValue(column, selected);
	}
	
	protected boolean doDeleteSelectedFields() {
		if (!_tableSchema.hasSelectedCells())
			return false;
		
		int[] columns = _tableSchema.getSelectedColumns();
		if (columns == null || columns.length == 0)
			return false;

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
	 * 説明入力用パネルを生成する。
	 * @return	生成されたコンポーネント
	 */
	protected JPanel createDescriptionPanel() {
		// create desc textbox
		JLabel lblDesc = new JLabel(RunnerMessages.getInstance().GenericFilterEditDlg_name_Desc + " : ");
		_txtDesc = new JTextField();

		// create layout panel
		JPanel pnl = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- label
		pnl.add(lblDesc, gbc);
		//--- text box
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(_txtDesc, gbc);
		
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

	/**
	 * ヘッダーレコードを設定するパネルを生成する。
	 * @return	生成されたコンポーネント
	 */
	protected JPanel createHeaderRecordConfigPanel() {
		// create Skip Records spinner
		SpinnerNumberModel model = new SpinnerNumberModel(0, MIN_SKIP_RECORDS, MAX_SKIP_RECORDS, 1);
		_spinSkipRecords = new JSpinner(model);
		
		// create radio buttons
		_rdoAutoHeaderRecord   = new JRadioButton(RunnerMessages.getInstance().GenericOutputSchemaEditDlg_rdo_AutoHeaderRecords);
		_rdoManualHeaderRecord = new JRadioButton(RunnerMessages.getInstance().GenericOutputSchemaEditDlg_rdo_ManualHeaderRecords);
		_rdoGroupHeaderRecord = new ButtonGroup();
		_rdoGroupHeaderRecord.add(_rdoAutoHeaderRecord);
		_rdoGroupHeaderRecord.add(_rdoManualHeaderRecord);
		
		// create panel
		JPanel pnl = new JPanel(new GridBagLayout());
		Border border = BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(RunnerMessages.getInstance().GenericOutputSchemaEditDlg_lbl_HeaderRecordTitle),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)
		);
		pnl.setBorder(border);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- radio-auotHeader
		gbc.gridwidth = 4;
		pnl.add(_rdoAutoHeaderRecord, gbc);
		//--- dummy
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx += gbc.gridwidth;
		gbc.gridwidth = 1;
		pnl.add(new JLabel(), gbc);
		//--- radio-manualHeader
		gbc.gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		pnl.add(_rdoManualHeaderRecord, gbc);
		gbc.gridx++;
		pnl.add(new JLabel("  /  "), gbc);
		gbc.gridx++;
		pnl.add(new JLabel(RunnerMessages.getInstance().GenericOutputSchemaEditDlg_lbl_HeaderRecordCount + " : "), gbc);
		gbc.gridx++;
		pnl.add(_spinSkipRecords, gbc);
		
		return pnl;
	}

	/**
	 * 出力フィールドの出力対象を設定するパネルを生成する。
	 * @return	生成されたコンポーネント
	 */
	protected JPanel createOutputFieldPropertyPanel() {
		// create text box
		_txtTargetName = new JTextField();
		_txtTargetName.setEditable(false);
		
		// create group combobox
		DefaultComboBoxModel groupModel = new DefaultComboBoxModel();
		groupModel.addElement(_cmbgrpInputFieldsItem);
		groupModel.addElement(_cmbgrpExpElementsItem);
		_cmbTargetGroup = new JComboBox(groupModel);
		_cmbTargetGroup.setEditable(false);
		final int width = 100;
		Dimension dm = _cmbTargetGroup.getPreferredSize();
		_cmbTargetGroup.setPreferredSize(new Dimension(width, dm.height));
		
		// create value combobox
		_cmbTargetValue = new GenericSchemaComboBox(_cmodelEmpty);
		_cmbTargetValue.setEditable(false);
		_cmbInputTable = new GenericSchemaComboBox(_cmodelEmpty);
		_cmbInputTable.setEditable(false);
		_cmbInputTable.setVisible(false);
		_cmbInputTable.setPreferredSize(_cmbTargetGroup.getPreferredSize());
		_cmbInputTable.setMinimumSize(_cmbTargetGroup.getMinimumSize());
		_cmbInputTable.setMaximumSize(_cmbTargetGroup.getMaximumSize());
		
		// create function buttons
		_btnFieldAdd    = CommonResources.createIconButton(CommonResources.ICON_ADD, RunnerMessages.getInstance().GenericInputSchemaEditDlg_btn_AddField);
		_btnFieldUpdate = CommonResources.createIconButton(CommonResources.ICON_REFRESH, CommonMessages.getInstance().Button_Update);
		
		// layout buttons panel
		Box btnbox = Box.createHorizontalBox();
		btnbox.add(Box.createHorizontalStrut(10));
		btnbox.add(_btnFieldAdd);
		btnbox.add(Box.createHorizontalStrut(2));
		btnbox.add(_btnFieldUpdate);
		
		// create panel
		JPanel pnl = new JPanel(new GridBagLayout());
		Border border = BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(RunnerMessages.getInstance().GenericOutputSchemaEditDlg_lbl_TargetTitle),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)
		);
		pnl.setBorder(border);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- name
		pnl.add(new JLabel(RunnerMessages.getInstance().GenericFilterEditDlg_name_Name + " : "), gbc);
		gbc.gridx++;
		gbc.gridwidth = 3;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(_txtTargetName, gbc);
		//--- dummy
		gbc.gridx += gbc.gridwidth;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		pnl.add(new JLabel(), gbc);
		//--- values
		gbc.insets = new Insets(5, 0, 0, 0);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		pnl.add(new JLabel(RunnerMessages.getInstance().GenericFilterEditDlg_name_Operand + " : "), gbc);
		//--- group
		gbc.gridx++;
		gbc.anchor = GridBagConstraints.WEST;
		pnl.add(_cmbTargetGroup, gbc);
		//--- input table
		gbc.gridx++;
		pnl.add(_cmbInputTable, gbc);
		//--- value
		gbc.gridx++;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(_cmbTargetValue, gbc);
		//--- buttons
		gbc.gridx += gbc.gridwidth;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		pnl.add(btnbox, gbc);
		
		return pnl;
	}

	/**
	 * 入力スキーマの設定テーブルコンポーネントを生成する。
	 * @return	生成されたコンポーネント
	 */
	protected GenericOutputCsvSchemaTable createSchemaTable() {
		GenericOutputCsvSchemaTable table = new GenericOutputCsvSchemaTable(_schemaCsvModel);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);	// 列入れ替え禁止
		table.getTableHeader().setResizingAllowed(true);	// 列サイズ変更可能
		table.setupVisibleGrid();	// グリッド表示(Mac対策)
		return table;
	}

	@Override
	protected void setupMainContents() {
		// create table
		_tableSchema = createSchemaTable();
		//---
		_scSchema = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_scSchema.setViewportView(_tableSchema);
		_scSchema.setRowHeaderView(_tableSchema.getTableRowHeader());
		_scSchema.setCorner(JScrollPane.UPPER_LEFT_CORNER, SpreadSheetTable.createUpperLeftCornerComponent());
		
		// create panels
		JPanel pnlDesc   = createDescriptionPanel();
		JPanel pnlHeader = createHeaderRecordConfigPanel();
		JPanel pnlTarget = createOutputFieldPropertyPanel();

		// create buttons
		_btnFieldDelete = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		_btnFieldMoveLeft  = CommonResources.createIconButton(CommonResources.ICON_ARROW_LEFT, CommonMessages.getInstance().Button_Left);
		_btnFieldMoveRight = CommonResources.createIconButton(CommonResources.ICON_ARROW_RIGHT, CommonMessages.getInstance().Button_Right);
		
		// create table label
		Box lblbox = Box.createHorizontalBox();
		lblbox.add(new JLabel(RunnerMessages.getInstance().GenericOutputSchemaEditDlg_lbl_Table + " : "));
		lblbox.add(Box.createGlue());
		lblbox.add(_btnFieldDelete);
		lblbox.add(Box.createHorizontalStrut(2));
		lblbox.add(_btnFieldMoveLeft);
		lblbox.add(Box.createHorizontalStrut(2));
		lblbox.add(_btnFieldMoveRight);
		
		// create main panel
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		
		// layout main panel
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- desc
		mainPanel.add(pnlDesc, gbc);
		//--- header record
		gbc.gridy++;
		gbc.insets = new Insets(5, 0, 0, 0);
		mainPanel.add(pnlHeader, gbc);
		//--- field property
		gbc.gridy++;
		mainPanel.add(pnlTarget, gbc);
		//--- table title and buttons
		gbc.gridy++;
		mainPanel.add(lblbox, gbc);
		//--- table
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		mainPanel.add(_scSchema, gbc);
		
		// set to main panel
		getContentPane().add(mainPanel, BorderLayout.CENTER);
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
				onSchemaTableSelectionChanged();
			}
		};
		_tableSchema.getSelectionModel().addListSelectionListener(tableSelectionListener);
		_tableSchema.getColumnModel().getSelectionModel().addListSelectionListener(tableSelectionListener);
		
		// Radio action
		ChangeListener rdoListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				onHeaderRecordRadioButtonStateChanged(ce);
			}
		};
		_rdoAutoHeaderRecord.addChangeListener(rdoListener);
		_rdoManualHeaderRecord.addChangeListener(rdoListener);
		
		// ComboBox action
		_cmbTargetGroup.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				onTargetGroupItemChanged();
			}
		});
		_cmbTargetValue.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				onTargetValueItemChanged();
			}
		});
		
		// Button action
		ActionListener btnListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onActionButtonClicked(ae);
			}
		};
		_btnFieldAdd.setActionCommand(BTNCMD_FIELD_ADD);
		_btnFieldAdd.addActionListener(btnListener);
		_btnFieldUpdate.setActionCommand(BTNCMD_FIELD_UPDATE);
		_btnFieldUpdate.addActionListener(btnListener);
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
}
