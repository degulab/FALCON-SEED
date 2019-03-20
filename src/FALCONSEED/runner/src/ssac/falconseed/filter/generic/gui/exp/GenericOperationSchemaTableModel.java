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
 * @(#)GenericConditionSchemaTableModel.java	3.2.2	2015/10/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericConditionSchemaTableModel.java	3.2.1	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericConditionSchemaTableModel.java	3.2.0	2015/06/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.exp;

import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.SchemaObjectList;
import ssac.aadl.fs.module.schema.exp.SchemaBinaryOperationData;
import ssac.aadl.fs.module.schema.exp.SchemaBinaryOperatorType;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.falconseed.filter.generic.gui.GenericSchemaElementValidation;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.swing.table.AbSpreadSheetTableModel;

/**
 * 条件や式のテーブルモデル。
 * このテーブルモデルは、基本的にセル編集は不可となっている。
 * 
 * @version 3.2.2
 * @since 3.2.0
 */
public abstract class GenericOperationSchemaTableModel<E extends SchemaBinaryOperationData> extends AbSpreadSheetTableModel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 3723082641210051508L;

	static protected int	CI_ITEMNAME		= 0;
	static protected int	CI_RESULTTYPE	= 1;
	static protected int	CI_EXPRESSION	= 2;
	static protected int	MAX_COLUMNS		= 3;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 結果のデータ型を表示するかどうかのフラグ **/
	protected final boolean		_visibleResultType;

	/** 要素のリスト **/
	protected SchemaObjectList<E>	_list;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public GenericOperationSchemaTableModel(boolean visibleResultType) {
		super();
		_visibleResultType = visibleResultType;
		_list = new SchemaObjectList<E>();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public String formatLeftOperandValueTypeErrorMessage(SchemaBinaryOperatorType optype, SchemaElementValue leftOperand) {
		return RunnerMessages.getInstance().msgGenericFilterEdit_InvalidLeftOperandType + "\n" + optype.getSupportedLeftValueTypes();
	}
	
	static public String formatRightOperandValueTypeErrorMessage(SchemaBinaryOperatorType optype, SchemaElementValue leftOperand, SchemaElementValue rightOperand) {
		return RunnerMessages.getInstance().msgGenericFilterEdit_InvalidRightOperandType + "\n" + optype.getSupportedRightValueTypes(leftOperand.getValueType());
	}
	
	abstract public SchemaBinaryOperatorType getOperatorType(String operator);
	
	abstract protected String getRowNamePrefix();
	
	abstract protected E createElementInstance();
	
	abstract protected E createElementInstance(String operator, SchemaElementValue leftValue, SchemaElementValue rightValue);
	
	protected String getDisplayExpression(E rowData) {
		String strOperator = rowData.getOperator();
		SchemaElementValue opLeft = rowData.getLeftOperand();
		SchemaElementValue opRight = rowData.getRightOperand();
		String strDispText = String.format("%s %s %s",
				(opLeft==null ? "(Unknown)" : opLeft.toString()),
				(strOperator==null ? "(?)" : strOperator),
				(opRight==null ? "(Unknown)" : opRight.toString()));
		return strDispText;
	}
	
	public E addNewElement(String name, String operator, SchemaElementValue leftValue, SchemaElementValue rightValue) {
		int curSize = getDataModel().size();
		E newModel = createElementInstance(operator, leftValue, rightValue);
		newModel.setName(name);
		newModel.setElementNo(curSize + 1);
		//--- verify
		if (newModel instanceof GenericSchemaElementValidation) {
			((GenericSchemaElementValidation)newModel).verify();
		}
		getDataModel().add(newModel);
		fireTableRowsInserted(curSize, curSize);
		return newModel;
	}
	
	public E updateElement(int index, String name, String operator, SchemaElementValue leftValue, SchemaElementValue rightValue) {
		E curModel = getDataModel().get(index);
		boolean flgName = curModel.updateName(name);
		boolean flgLop  = curModel.updateLeftOperand(leftValue);
		boolean flgRop  = curModel.updateRightOperand(rightValue);
		boolean flgOp   = curModel.updateOperator(operator);
		if (flgName || flgLop || flgRop || flgOp) {
			// 変更あり
			//--- verify
			if (curModel instanceof GenericSchemaElementValidation) {
				((GenericSchemaElementValidation)curModel).verify();
			}
			fireTableRowsUpdated(index, index);
		}
		return curModel;
	}
	
	public E deleteElement(int index) {
		if (index < 0 || index >= getDataModel().size())
			return null;
		
		E removed = getDataModel().remove(index);
		int len = getDataModel().size();
		for (int i = index; i < len; ++i) {
			getDataModel().get(i).setElementNo(i+1);
		}
		fireTableRowsDeleted(index, index);
		return removed;
	}
	
	public E getElement(int index) {
		return getDataModel().get(index);
	}
	
	public int getExpressionColumnIndex() {
		return (_visibleResultType ? CI_EXPRESSION : (CI_EXPRESSION-1));
	}
	
	public int getResultTypeColumnIndex() {
		return (_visibleResultType ? CI_RESULTTYPE : -1);
	}

	public int getItemNameColumnIndex() {
		return CI_ITEMNAME;
	}
	
	public abstract String getTitle();
	
	public SchemaObjectList<E> getDataModel() {
		return _list;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	/**
	 * 指定された位置のデータがエラーを示すデータかを判定する。
	 * @param rowIndex		行インデックス
	 * @param columnIndex	列インデックス
	 * @return	エラーを示す場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	@Override
	public boolean isError(int rowIndex, int columnIndex) {
		if (rowIndex >= 0 && rowIndex < _list.size()) {
			E elem = _list.get(rowIndex);
			if ((elem instanceof GenericSchemaElementValidation) && columnIndex == getExpressionColumnIndex()) {
				return ((GenericSchemaElementValidation)elem).hasError();
			}
		}
		
		return false;
	}

	@Override
	public String getCellToolTipText(int rowIndex, int columnIndex) {
		if (rowIndex >= 0 && rowIndex < _list.size()) {
			E elem = _list.get(rowIndex);
			if ((elem instanceof GenericSchemaElementValidation) && columnIndex == getExpressionColumnIndex()) {
				return ((GenericSchemaElementValidation)elem).getAvailableErrorMessage();
			}
		}
		return super.getCellToolTipText(rowIndex, columnIndex);
	}

	@Override
	public int getRowCount() {
		return _list.size();
	}

	@Override
	public int getColumnCount() {
		return (_visibleResultType ? MAX_COLUMNS : (MAX_COLUMNS-1));
	}
	
	@Override
	public String getColumnName(int column) {
		if (column == getExpressionColumnIndex()) {
			return getExpressionColumnName();
		}
		else if (column == getResultTypeColumnIndex()) {
			return RunnerMessages.getInstance().GenericOperationTableModel_name_ResultType;
		}
		else if (column == getItemNameColumnIndex()) {
			return RunnerMessages.getInstance().GenericFilterEditDlg_name_Name;
		}
		else {
			return super.getColumnName(column);
		}
	}

	@Override
	public String getRowName(int rowIndex) {
		return String.format("%s[%d]", getRowNamePrefix(), (rowIndex+1));
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= 0 && rowIndex < _list.size() && columnIndex >= 0) {
			E rowData = _list.get(rowIndex);
			if (columnIndex == getExpressionColumnIndex()) {
				return getDisplayExpression(rowData);
			}
			else if (columnIndex == getResultTypeColumnIndex()) {
				SchemaValueType vtype = rowData.getResultType();
				return (vtype==null ? null : vtype.getTypeName());
			}
			else if (columnIndex == getItemNameColumnIndex()) {
				return rowData.getName();
			}
		}
		
		// no value
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	protected String getExpressionColumnName() {
		return RunnerMessages.getInstance().GenericOperationTableModel_name_Expression;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
