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
 * @(#)GenericExpressionSchemaTablePane.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.exp;

import static ssac.falconseed.filter.generic.gui.exp.GenericOperationSchemaPropertyPane.INDEX_LEFT;
import static ssac.falconseed.filter.generic.gui.exp.GenericOperationSchemaPropertyPane.INDEX_RIGHT;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.exp.SchemaBinaryOperatorType;
import ssac.aadl.fs.module.schema.exp.SchemaJoinConditionOperators;
import ssac.falconseed.filter.generic.gui.ComboBoxOperandGroupItem;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.falconseed.filter.generic.gui.GenericSchemaValueGroups;
import ssac.falconseed.filter.generic.gui.table.ComboBoxInputSelectionModel;
import ssac.falconseed.filter.generic.gui.table.InputCsvFieldSchemaEditModel;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.swing.Application;

/**
 * 汎用フィルタの結合条件設定用テーブルパネル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericJoinCondSchemaTablePane extends GenericOperationSchemaTablePane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	static protected final String	BTNCMD_EXP_DELETE	= "exp.delete";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** このテーブルコンポーネント専用のプロパティコンポーネント **/
	private GenericJoinCondSchemaPropertyPane	_localProperty;

	/** データ選択用のコンボボックスモデル **/
//	private ComboBoxInputOperandModel[]		_cmodelInputFields;
	private ComboBoxInputSelectionModel[]	_cmodelInputFields;

	private JButton	_btnExpDelete;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericJoinCondSchemaTablePane() {
		super();
	}
	
	@Override
	public void initialComponent(GenericFilterEditModel editModel, GenericOperationSchemaTableModel<?> model) {
		// check
		if (model != null && !(model instanceof GenericJoinCondSchemaTableModel)) {
			throw new IllegalArgumentException("Model class is not GenericJoinCondSchemaTableModel : " + model.getClass().getName());
		}
		
		// basic initialize
		super.initialComponent(editModel, model);
		
		_btnExpDelete = CommonResources.createIconButton(CommonResources.ICON_DELETE, CommonMessages.getInstance().Button_Delete);
		_btnExpDelete.setEnabled(false);
		
		Box btnbox = Box.createHorizontalBox();
		btnbox.add(Box.createGlue());
		btnbox.add(_btnExpDelete);
		
		// create property pane
		_localProperty = new GenericJoinCondSchemaPropertyPane();
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
		//--- operator
		DefaultComboBoxModel cmodelOperator = new DefaultComboBoxModel();
		cmodelOperator.addElement(SchemaJoinConditionOperators.OP_INNER_JOIN);
		cmodelOperator.setSelectedItem(SchemaJoinConditionOperators.OP_INNER_JOIN);
		//--- left operand group
		DefaultComboBoxModel cmodelLeftGroup = new DefaultComboBoxModel();
		cmodelLeftGroup.addElement(new ComboBoxOperandGroupItem(GenericSchemaValueGroups.INPUT, _cmodelInputFields[INDEX_LEFT]));
		cmodelLeftGroup.setSelectedItem(cmodelLeftGroup.getElementAt(0));
		//--- right operand group
		DefaultComboBoxModel cmodelRightGroup = new DefaultComboBoxModel();
		cmodelRightGroup.addElement(new ComboBoxOperandGroupItem(GenericSchemaValueGroups.INPUT, _cmodelInputFields[INDEX_RIGHT]));
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
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public String getTitle() {
		return RunnerMessages.getInstance().GenericFilterEditDlg_name_JoinCond;
	}

	@Override
	public GenericJoinCondSchemaPropertyPane getLocalPropertyPane() {
		return _localProperty;
	}
	
	public DefaultComboBoxModel getOperandGroupComboBoxModel(int index) {
		return (DefaultComboBoxModel)_localProperty.getOperandGroupComboBox(index).getModel();
	}
	
	public ComboBoxInputSelectionModel getInputFieldComboBoxModel(int index) {
		return _cmodelInputFields[index];
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
			_btnExpDelete.setEnabled(true);
			//--- プロパティの更新
			int row = _table.getSelectedRow();
			GenericJoinCondElementEditModel data = _editModel.getJoinConditionSchemaTableModel().getDataModel().get(row);
			_localProperty.setItemName(data.getName());
			//_localProperty.setOperator(data.getOperator());
			_localProperty.setOperandValue(INDEX_LEFT, data.getLeftOperand());
			_localProperty.setOperandValue(INDEX_RIGHT, data.getRightOperand());
		} else {
			// 選択されていない
			_localProperty.getToolBarUpdateButton().setEnabled(false);
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
		GenericJoinCondElementEditModel joinModel = _editModel.getJoinConditionSchemaTableModel().addNewElement(
				_localProperty.getItemName(), _localProperty.getOperator(), lop, rop);
		//--- 参照関係を追加
		_editModel.setObjectReference(joinModel, lop);
		_editModel.setObjectReference(joinModel, rop);
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
		GenericJoinCondElementEditModel joinModel = _editModel.getJoinConditionSchemaTableModel().getElement(row);
		_editModel.removeDependentReference(joinModel);

		// 更新
		SchemaElementValue lop = _localProperty.getOperandValue(INDEX_LEFT);
		SchemaElementValue rop = _localProperty.getOperandValue(INDEX_RIGHT);
		_editModel.getJoinConditionSchemaTableModel().updateElement(row,
				_localProperty.getItemName(), _localProperty.getOperator(), lop, rop);
		
		// 新しい参照関係の登録
		_editModel.setObjectReference(joinModel, lop);
		_editModel.setObjectReference(joinModel, rop);
		
		// 選択状態の更新
		_table.setRowSelectionInterval(row, row);
	}
	
	protected void onClickedPropertyToolDeleteButton(ActionEvent ae) {
		int row = _table.getSelectedRow();
		if (row < 0)
			return;
		
		GenericJoinCondElementEditModel removed = _editModel.getJoinConditionSchemaTableModel().deleteElement(row);
		//--- 参照関係の破棄
		_editModel.removeDependentReference(removed);
		//--- 選択状態の更新
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
		SchemaBinaryOperatorType optype = SchemaJoinConditionOperators.getOperatorType(strOperator);
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
		
		// valid
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	public class GenericJoinCondSchemaPropertyPane extends GenericOperationSchemaPropertyPane
	{
		private static final long serialVersionUID = 1L;

		public GenericJoinCondSchemaPropertyPane() {
			super();
		}

		@Override
		protected String getPropertyTitle() {
			return RunnerMessages.getInstance().GenericJoinCondProperty_title;
		}

		@Override
		protected String getOperationCaption() {
			return RunnerMessages.getInstance().GenericJoinCondTableModel_name_Expression;
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
			} else {
				return (-1);
			}
		}
	}
}
