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
 * @(#)GenericExpressionSchemaTablePane.java	3.2.1	2015/07/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericExpressionSchemaTablePane.java	3.2.0	2015/06/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.exp;

import static ssac.falconseed.filter.generic.gui.exp.GenericOperationSchemaPropertyPane.INDEX_LEFT;
import static ssac.falconseed.filter.generic.gui.exp.GenericOperationSchemaPropertyPane.INDEX_RIGHT;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.SchemaLiteralValue;
import ssac.aadl.fs.module.schema.exp.SchemaBinaryOperatorType;
import ssac.aadl.fs.module.schema.exp.SchemaExpressionOperators;
import ssac.falconseed.filter.generic.gui.ComboBoxOperandGroupItem;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.falconseed.filter.generic.gui.GenericSchemaValueGroups;
import ssac.falconseed.filter.generic.gui.arg.ComboBoxFilterArgOperandModel;
import ssac.falconseed.filter.generic.gui.arg.GenericFilterArgEditModel;
import ssac.falconseed.filter.generic.gui.arg.GenericFilterDefArgEditDialog;
import ssac.falconseed.filter.generic.gui.table.ComboBoxInputSelectionModel;
import ssac.falconseed.filter.generic.gui.table.InputCsvFieldSchemaEditModel;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;

/**
 * 汎用フィルタの計算式設定用テーブルパネル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericExpressionSchemaTablePane extends GenericOperationSchemaTablePane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	static protected final String	BTNCMD_EXP_OUTPUT	= "exp.output";
	static protected final String	BTNCMD_EXP_DELETE	= "exp.delete";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このテーブルコンポーネント専用のプロパティコンポーネント **/
	private GenericExpressionSchemaPropertyPane	_localProperty;

	/** データ選択用のコンボボックスモデル **/
	private ComboBoxInputSelectionModel[]		_cmodelInputFields;
	private ComboBoxExpressionOperandModel[]	_cmodelExpElements;
	private ComboBoxFilterArgOperandModel[]		_cmodelFilterArgs;

	private JButton	_btnExpOutput;
	private JButton	_btnExpDelete;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericExpressionSchemaTablePane() {
		super();
	}
	
	@Override
	public void initialComponent(GenericFilterEditModel editModel, GenericOperationSchemaTableModel<?> model) {
		// check
		if (model != null && !(model instanceof GenericExpressionSchemaTableModel)) {
			throw new IllegalArgumentException("Model class is not GenericExpressionSchemaTableModel : " + model.getClass().getName());
		}
		
		// basic initialize
		super.initialComponent(editModel, model);

		_btnExpOutput = CommonResources.createIconButton(CommonResources.ICON_ARROW_RIGHT, RunnerMessages.getInstance().GenericFilterEditDlg_btn_AddToOutput);
		_btnExpDelete = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		_btnExpOutput.setEnabled(false);
		_btnExpDelete.setEnabled(false);
		
		Box btnbox = Box.createHorizontalBox();
		btnbox.add(Box.createGlue());
		btnbox.add(_btnExpDelete);
		btnbox.add(Box.createHorizontalStrut(2));
		btnbox.add(_btnExpOutput);
		
		// create property pane
		_localProperty = new GenericExpressionSchemaPropertyPane();
		_localProperty.initialComponent(editModel);
		
		// setup combobox model
//		_cmodelInputFields = new ComboBoxInputOperandModel[]{
//				new ComboBoxInputOperandModel(editModel),
//				new ComboBoxInputOperandModel(editModel),
//		};
		_cmodelInputFields = new ComboBoxInputSelectionModel[]{
				new ComboBoxInputSelectionModel(editModel),
				new ComboBoxInputSelectionModel(editModel),
		};
		_cmodelExpElements = new ComboBoxExpressionOperandModel[]{
				new ComboBoxExpressionOperandModel(editModel.getExpressionSchemaTableModel()),
				new ComboBoxExpressionOperandModel(editModel.getExpressionSchemaTableModel()),
		};
		_cmodelFilterArgs = new ComboBoxFilterArgOperandModel[]{
				new ComboBoxFilterArgOperandModel(editModel),
				new ComboBoxFilterArgOperandModel(editModel),
		};
		//--- operator
		DefaultComboBoxModel cmodelOperator = new DefaultComboBoxModel();
		for (SchemaBinaryOperatorType optype : SchemaExpressionOperators.OPERATORS) {
			cmodelOperator.addElement(optype.operator());
		}
		cmodelOperator.setSelectedItem(cmodelOperator.getElementAt(0));
		//--- left operand group
		DefaultComboBoxModel cmodelLeftGroup = new DefaultComboBoxModel();
		cmodelLeftGroup.addElement(new ComboBoxOperandGroupItem(GenericSchemaValueGroups.INPUT, _cmodelInputFields[INDEX_LEFT]));
		cmodelLeftGroup.addElement(new ComboBoxOperandGroupItem(GenericSchemaValueGroups.EXP, _cmodelExpElements[INDEX_LEFT]));
		cmodelLeftGroup.addElement(new ComboBoxOperandGroupItem(GenericSchemaValueGroups.F_ARG, _cmodelFilterArgs[INDEX_LEFT]));
		cmodelLeftGroup.addElement(new ComboBoxOperandGroupItem(GenericSchemaValueGroups.LITERAL, null));
		cmodelLeftGroup.setSelectedItem(cmodelLeftGroup.getElementAt(0));
		//--- right operand group
		DefaultComboBoxModel cmodelRightGroup = new DefaultComboBoxModel();
		cmodelRightGroup.addElement(new ComboBoxOperandGroupItem(GenericSchemaValueGroups.INPUT, _cmodelInputFields[INDEX_RIGHT]));
		cmodelRightGroup.addElement(new ComboBoxOperandGroupItem(GenericSchemaValueGroups.EXP, _cmodelExpElements[INDEX_RIGHT]));
		cmodelRightGroup.addElement(new ComboBoxOperandGroupItem(GenericSchemaValueGroups.F_ARG, _cmodelFilterArgs[INDEX_RIGHT]));
		cmodelRightGroup.addElement(new ComboBoxOperandGroupItem(GenericSchemaValueGroups.LITERAL, null));
		cmodelRightGroup.setSelectedItem(cmodelRightGroup.getElementAt(0));
		//--- set combobox modes
		_localProperty.getOperatorComboBox().setModel(cmodelOperator);
		_localProperty.getOperandGroupComboBox(INDEX_LEFT).setModel(cmodelLeftGroup);
		_localProperty.getOperandGroupComboBox(INDEX_RIGHT).setModel(cmodelRightGroup);
//		_localProperty.getOperandValueComboBox(INDEX_LEFT).setModel(_cmodelInputFields[INDEX_LEFT]);
//		_localProperty.getOperandValueComboBox(INDEX_RIGHT).setModel(_cmodelInputFields[INDEX_RIGHT]);
		_localProperty.refreshComboBox();
		
		// setup layout
		add(btnbox, BorderLayout.NORTH);
		
		// 参照モード対応
		if (!_editModel.isEditing()) {
			_btnExpDelete.setVisible(false);
			_btnExpDelete.setEnabled(false);
			_btnExpOutput.setVisible(false);
			_btnExpOutput.setEnabled(false);
			_localProperty.getToolBarAddButton().setVisible(false);
			_localProperty.getToolBarAddButton().setEnabled(false);
			_localProperty.getToolBarUpdateButton().setVisible(false);
			_localProperty.getToolBarUpdateButton().setEnabled(false);
		}
		
		// setup actions
		_table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				onTableRowSelectionChanged(lse);
			}
		});
		_btnExpDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onClickedPropertyToolDeleteButton(ae);
			}
		});
		_btnExpOutput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onClickedAddToOutputButton(ae);
			}
		});
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public String getTitle() {
		return RunnerMessages.getInstance().GenericFilterEditDlg_name_ArithExp;
	}

	@Override
	public GenericExpressionSchemaPropertyPane getLocalPropertyPane() {
		return _localProperty;
	}
	
	public JButton getAddToOutputButton() {
		return _btnExpOutput;
	}
	
	public DefaultComboBoxModel getOperandGroupComboBoxModel(int index) {
		return (DefaultComboBoxModel)_localProperty.getOperandGroupComboBox(index).getModel();
	}
	
	public ComboBoxInputSelectionModel getInputFieldComboBoxModel(int index) {
		return _cmodelInputFields[index];
	}
	
	public ComboBoxExpressionOperandModel getExpressionOperandComboBoxModel(int index) {
		return _cmodelExpElements[index];
	}
	
	public ComboBoxFilterArgOperandModel getFilterArgOperandComboBoxModel(int index) {
		return _cmodelFilterArgs[index];
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected GenericOperationSchemaTable createTable(GenericOperationSchemaTableModel<?> model) {
		return super.createTable(model);
	}
	
	protected void onTableRowSelectionChanged(ListSelectionEvent lse) {
		if (_table.getSelectedRowCount() > 0) {
			// 選択されている
			_localProperty.getToolBarUpdateButton().setEnabled(true);
			_btnExpOutput.setEnabled(true);
			_btnExpDelete.setEnabled(true);
			//--- プロパティの更新
			int row = _table.getSelectedRow();

			GenericExpressionElementEditModel data = _editModel.getExpressionSchemaTableModel().getDataModel().get(row);
			_localProperty.setItemName(data.getName());
			_localProperty.setOperator(data.getOperator());
			_localProperty.setOperandValue(INDEX_LEFT, data.getLeftOperand());
			_localProperty.setOperandValue(INDEX_RIGHT, data.getRightOperand());
		} else {
			// 選択されていない
			_localProperty.getToolBarUpdateButton().setEnabled(false);
			_btnExpOutput.setEnabled(false);
			_btnExpDelete.setEnabled(false);
		}
	}
	
	protected void onClickedPropertyToolAddButton(ActionEvent ae) {
		// チェック
		if (!validPropertyValues()) {
			return;
		}
		
		// 現在の内容から、新しいデータモデルを生成
		if (_table.getRowCount() == 0) {
			_table.getTableRowHeader().setFixedCellWidth(-1);
		}
		// 新しいデータモデルを追加
		SchemaElementValue lop = _localProperty.getOperandValue(INDEX_LEFT);
		SchemaElementValue rop = _localProperty.getOperandValue(INDEX_RIGHT);
		GenericExpressionElementEditModel expModel = _editModel.getExpressionSchemaTableModel().addNewElement(
				_localProperty.getItemName(), _localProperty.getOperator(), lop, rop);
		//--- 参照関係を追加
		_editModel.setObjectReference(expModel, lop);
		_editModel.setObjectReference(expModel, rop);
		//--- 選択位置を更新
		int row = _table.getRowCount() - 1;
		_table.setRowSelectionInterval(row, row);
		_table.scrollToVisibleCell(row, 0);
	}
	
	protected void onClickedPropertyToolUpdateButton(ActionEvent ae) {
		int row = _table.getSelectedRow();
		if (row < 0)
			return;
		
		// チェック
		if (!validPropertyValues()) {
			return;
		}
		
		// 既存の参照関係を破棄
		GenericExpressionElementEditModel expModel = _editModel.getExpressionSchemaTableModel().getElement(row);
		_editModel.removeDependentReference(expModel);

		// 更新
		SchemaElementValue lop = _localProperty.getOperandValue(INDEX_LEFT);
		SchemaElementValue rop = _localProperty.getOperandValue(INDEX_RIGHT);
		_editModel.getExpressionSchemaTableModel().updateElement(row,
				_localProperty.getItemName(), _localProperty.getOperator(), lop, rop);
		
		// 新しい参照関係の登録
		_editModel.setObjectReference(expModel, lop);
		_editModel.setObjectReference(expModel, rop);
		
		// 選択状態の更新
		_table.setRowSelectionInterval(row, row);
		
		// データ変更通知
		_editModel.notifyPrecedentDataChanged(expModel);
	}
	
	protected void onClickedPropertyToolDeleteButton(ActionEvent ae) {
		int row = _table.getSelectedRow();
		if (row < 0)
			return;
		
		// 参照関係のチェック
		boolean isPrecedent = false;
		GenericExpressionElementEditModel expModel = _editModel.getExpressionSchemaTableModel().getElement(row);
		if (_editModel.isPrecedentReferenceObject(expModel)) {
			isPrecedent = true;
			// 問合せ
			int ret = JOptionPane.showConfirmDialog(this,
					RunnerMessages.getInstance().confirmDeleteReferencedExpressionSchema,
					CommonMessages.getInstance().msgboxTitleWarn,
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (ret != JOptionPane.OK_OPTION) {
				// user canceled
				return;
			}
		}

		// テーブルから削除
		_editModel.getExpressionSchemaTableModel().deleteElement(row);
		
		// 参照関係の破棄
		_editModel.removeDependentReference(expModel);
		if (isPrecedent) {
			_editModel.removePrecedentDataFromAllDependents(expModel);
		}
		
		// 表示の更新
		int rowcnt = _table.getRowCount();
		if (rowcnt > 0) {
			if (row >= rowcnt)
				row = rowcnt - 1;
			_table.setRowSelectionInterval(row, row);
			_table.scrollToVisibleCell(row, 0);
		}
		else {
			_table.getTableRowHeader().setFixedCellWidth(EMPTY_ROWHEADER_WIDTH);
		}
	}
	
	protected void onClickedAddToOutputButton(ActionEvent ae) {
		// ここでは処理しない
	}

	/**
	 * プロパティの内容が正当かを判定する。正当でない場合、エラーメッセージを表示する。
	 * @return	正当の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	protected boolean validPropertyValues() {
		// オペランドのチェック
		SchemaElementValue lop = _localProperty.getOperandValue(INDEX_LEFT);
		SchemaElementValue rop = _localProperty.getOperandValue(INDEX_RIGHT);
		if (lop == null) {
			// No left operand
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericFilterEdit_NoLeftOperand);
			return false;
		}
		else if (rop == null) {
			// No right operand
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericFilterEdit_NoRightOperand);
			return false;
		}
		
		// オペレータのチェック
		String strOperator = _localProperty.getOperator();
		if (strOperator == null) {
			// No operator
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericFilterEdit_NoOperator);
			return false;
		}
		
		// データ型のチェック
		SchemaBinaryOperatorType optype = SchemaExpressionOperators.getOperatorType(strOperator);
		if (optype == null) {
			// Unsupported operator
			Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericFilterEdit_UnsupportedOperator);
			return false;
		}
		if (!optype.containsLeftOperandType(lop.getValueType())) {
			// Invalid left operand data type
			Application.showErrorMessage(this, GenericOperationSchemaTableModel.formatLeftOperandValueTypeErrorMessage(optype, lop));
			return false;
		}
		if (!optype.containsPair(lop.getValueType(), rop.getValueType())) {
			// Invalid right operand data type
			Application.showErrorMessage(this, GenericOperationSchemaTableModel.formatRightOperandValueTypeErrorMessage(optype, lop, rop));
			return false;
		}
		
		// 測値のチェック
		//--- left operand
		if (lop instanceof SchemaLiteralValue) {
			SchemaLiteralValue literal = (SchemaLiteralValue)lop;
			if (literal.getSpecifiedText() == null) {
				// No value type selection
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericFilterEdit_NoLeftLiteralValueType);
				return false;
			}
			if (literal.getSpecifiedText().isEmpty()) {
				// Empty literal value
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericFilterEdit_EmptyLeftLiteralValue);
				return false;
			}
			if (!literal.isValidSpecifiedValue()) {
				// Failed to convert literal value to specified value type
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericFilterEdit_InvalidLeftLiteralValue);
				return false;
			}
		}
		//--- right operand
		if (rop instanceof SchemaLiteralValue) {
			SchemaLiteralValue literal = (SchemaLiteralValue)rop;
			if (literal.getSpecifiedText() == null) {
				// No value type selection
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericFilterEdit_NoRightLiteralValueType);
				return false;
			}
			if (literal.getSpecifiedText().isEmpty()) {
				// Empty literal value
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericFilterEdit_EmptyRightLiteralValue);
				return false;
			}
			if (!literal.isValidSpecifiedValue()) {
				// Failed to convert literal value to specified value type
				Application.showErrorMessage(this, RunnerMessages.getInstance().msgGenericFilterEdit_InvalidRightLiteralValue);
				return false;
			}
		}
		
		// valid
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	public class GenericExpressionSchemaPropertyPane extends GenericOperationSchemaPropertyPane
	{
		private static final long serialVersionUID = 1L;

		public GenericExpressionSchemaPropertyPane() {
			super();
		}

		@Override
		protected String getPropertyTitle() {
			return RunnerMessages.getInstance().GenericExpressionProperty_title;
		}

		@Override
		protected String getOperationCaption() {
			return RunnerMessages.getInstance().GenericExpressionTableModel_name_Expression;
		}

		@Override
		protected void onToolAddButtonClicked(ActionEvent ae) {
			onClickedPropertyToolAddButton(ae);
		}

		@Override
		protected void onToolUpdateButtonClicked(ActionEvent ae) {
			onClickedPropertyToolUpdateButton(ae);
		}

		@Override
		protected int getOperandGroupIndexByOperandValue(SchemaElementValue value) {
			if (value instanceof InputCsvFieldSchemaEditModel) {
				return 0;
			}
			else if (value instanceof GenericExpressionElementEditModel) {
				return 1;
			}
			else if (value instanceof GenericFilterArgEditModel) {
				return 2;
			}
			else if (value instanceof SchemaLiteralValue) {
				return 3;
			}
			else {
				return (-1);
			}
		}

		/**
		 * フィルタ定義引数の追加ボタン押下時に呼び出されるイベントハンドラ。
		 */
		@Override
		protected void onClickedAddFilterArgButton(ActionEvent ae, JButton btn, int index) {
			// ダイアログの表示
			GenericFilterDefArgEditDialog dlg;
			Window window = SwingUtilities.getWindowAncestor(this);
			if (window instanceof Dialog) {
				dlg = new GenericFilterDefArgEditDialog((Dialog)window, RunnerMessages.getInstance().GenericFilterDefArgEditDlg_title_new);
			} else {
				dlg = new GenericFilterDefArgEditDialog((Frame)window, RunnerMessages.getInstance().GenericFilterDefArgEditDlg_title_new);
			}
			dlg.initialComponent(_editModel, null);
			dlg.setVisible(true);
			dlg.dispose();
			if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
				// user canceled
				return;
			}
			
			// 設定の反映
			_editModel.addMExecDefArgument(dlg.getTargetModel());
			GenericFilterArgEditModel argModel = _editModel.getMExecDefArgument(_editModel.getMExecDefArgumentCount()-1);
			selectFilterArgInvokeLater(index, argModel);
		}
		
		protected void selectFilterArgInvokeLater(final int index, final GenericFilterArgEditModel argModel) {
			//--- コンボボックスで選択
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					_cmodelFilterArgs[index].setSelectedItem(argModel);
				}
			});
		}
	}
}
