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
 * @(#)GenericFilterSchemaEditPane.java	3.2.0	2015/07/21
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericFilterSchemaEditPane.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.fs.module.schema.type.SchemaDateTimeFormats;
import ssac.falconseed.filter.generic.gui.arg.GenericFilterArgEditModel;
import ssac.falconseed.filter.generic.gui.exp.GenericExpressionElementEditModel;
import ssac.falconseed.filter.generic.gui.exp.GenericExpressionSchemaTableModel;
import ssac.falconseed.filter.generic.gui.exp.GenericExpressionSchemaTablePane;
import ssac.falconseed.filter.generic.gui.exp.GenericExpressionSchemaTablePane.GenericExpressionSchemaPropertyPane;
import ssac.falconseed.filter.generic.gui.exp.GenericJoinCondElementEditModel;
import ssac.falconseed.filter.generic.gui.exp.GenericJoinCondSchemaTableModel;
import ssac.falconseed.filter.generic.gui.exp.GenericJoinCondSchemaTablePane;
import ssac.falconseed.filter.generic.gui.exp.GenericJoinCondSchemaTablePane.GenericJoinCondSchemaPropertyPane;
import ssac.falconseed.filter.generic.gui.exp.GenericOperationSchemaPropertyPane;
import ssac.falconseed.filter.generic.gui.table.GenericInputSchemaEditDialog;
import ssac.falconseed.filter.generic.gui.table.GenericOutputSchemaEditDialog;
import ssac.falconseed.filter.generic.gui.table.GenericTableSchemaTreeNode;
import ssac.falconseed.filter.generic.gui.table.InputCsvFieldSchemaEditModel;
import ssac.falconseed.filter.generic.gui.table.InputCsvTableSchemaEditModel;
import ssac.falconseed.filter.generic.gui.table.OutputCsvFieldSchemaEditModel;
import ssac.falconseed.filter.generic.gui.table.OutputCsvTableSchemaEditModel;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaElementTree;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaElementTreeNode;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.properties.ExConfiguration;
import ssac.util.swing.Application;
import ssac.util.swing.IDialogResult;
import ssac.util.swing.menu.ToolBarButton;

/**
 * 汎用フィルタの入力設定用ツリーモデル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public class GenericFilterSchemaEditPane extends JPanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	static protected final String	PROPKEY_DIVIDER_INPUTSCHEMA		= ".GenericFilterSchemaEditPane.input";
	static protected final String	PROPKEY_DIVIDER_OUTPUTSCHEMA	= ".GenericFilterSchemaEditPane.output";
	static protected final String	PROPKEY_DIVIDER_PROPERTY		= ".GenericFilterSchemaEditPane.property";
	
	static protected final String	PROPPANEKEY_Exp		= "PropExp";
	static protected final String	PROPPANEKEY_Join	= "PropJoin";

	static protected final String	BTNCMD_INPUT_TABLE_TO_OUTPUT	= "input.table.to.output";
	static protected final String	BTNCMD_INPUT_TABLE_NEW			= "input.table.new";
	static protected final String	BTNCMD_INPUT_TABLE_EDIT			= "input.table.edit";
	static protected final String	BTNCMD_INPUT_TABLE_DELETE		= "input.table.delete";
	
	static protected final String	BTNCMD_OUTPUT_TABLE_NEW			= "output.table.new";
	static protected final String	BTNCMD_OUTPUT_TABLE_EDIT		= "output.table.edit";
	static protected final String	BTNCMD_OUTPUT_TABLE_DELETE		= "output.table.delete";
	static protected final String	BTNCMD_OUTPUT_FIELD_DELETE		= "output.field.delete";
	static protected final String	BTNCMD_OUTPUT_FIELD_MOVEUP		= "output.field.moveup";
	static protected final String	BTNCMD_OUTPUT_FIELD_MOVEDOWN	= "output.field.movedown";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 汎用フィルタ編集データモデル **/
	private GenericFilterEditModel		_editModel;
	
	private JSplitPane	_splitInputPane;
	private JSplitPane	_splitOutputPane;
	private JSplitPane	_splitCondPane;
	
	private GenericSchemaElementTree	_treeInput;
	private GenericSchemaElementTree	_treeOutput;
	
	private JToolBar	_tbarInputTableFunctions;
	private JToolBar	_tbarOutputTableFunctions;

	private JButton	_btnInputTableToOutput;
	private JButton	_btnInputTableNew;
	private JButton	_btnInputTableEdit;
	private JButton	_btnInputTableDelete;
	
	private JButton	_btnOutputTableNew;
	private JButton	_btnOutputTableEdit;
	private JButton	_btnOutputTableDelete;
	private JButton	_btnOutputFieldDelete;
	private JButton	_btnOutputFieldMoveUp;
	private JButton	_btnOutputFieldMoveDown;
	
	private JTabbedPane	_tabConditions;
	private GenericExpressionSchemaTablePane	_tableExp;
	private GenericJoinCondSchemaTablePane		_tableJoin;
	
	private GenericExpressionSchemaPropertyPane	_panePropExp;
	private GenericJoinCondSchemaPropertyPane	_panePropJoin;
	
	private JPanel		_propPanel;
	private CardLayout	_propLayout;
	
	private JButton		_btnEditDataTimeFormats;
	private JTextField	_stcDateTimeFormats;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericFilterSchemaEditPane(GenericFilterEditModel editModel) {
		super(new BorderLayout());
		
		// set edit model
		if (editModel == null)
			throw new NullPointerException("GenericFilterEditModel object is null.");
		_editModel = editModel;
		
		// create components
		JPanel pnlDTFormats = createDateTimeFormatsPane();
		JPanel pnlInputPane  = createInputSchemaTreePane(editModel);
		JPanel pnlOutputPane = createOutputSchemaTreePane(editModel);
		_tableExp      = createExpressionSchemaTablePane(editModel.getExpressionSchemaTableModel());
		_tableJoin     = createJoinCondSchemaTablePane(editModel.getJoinConditionSchemaTableModel());
		_tabConditions = createConditionTablesTab();
		
		// property panes
		_panePropExp    = _tableExp.getLocalPropertyPane();
		_panePropJoin   = _tableJoin.getLocalPropertyPane();
		//--- create card panel;
		_propLayout = new CardLayout();
		_propPanel  = new JPanel(_propLayout);
		setupPropertyLayoutPanel();
		
		_splitInputPane   = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		_splitInputPane.setResizeWeight(0);		// left
		_splitOutputPane  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
		_splitOutputPane.setResizeWeight(1);	// Right
		_splitCondPane    = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false);
		_splitCondPane.setResizeWeight(1);		// Bottom
		
		_splitInputPane.setLeftComponent(pnlInputPane);
		_splitInputPane.setRightComponent(_splitOutputPane);
		_splitOutputPane.setRightComponent(pnlOutputPane);
		if (_editModel.isEditing()) {
			// 編集モード時のみプロパティを表示
			_splitOutputPane.setLeftComponent(_splitCondPane);
			_splitCondPane.setTopComponent(_tabConditions);
			_splitCondPane.setBottomComponent(_propPanel);
		} else {
			// 参照モードではプロパティ非表示
			_splitOutputPane.setLeftComponent(_tabConditions);
		}
		
		add(_splitInputPane, BorderLayout.CENTER);
		add(pnlDTFormats, BorderLayout.NORTH);
		
		// ツリーをすべて展開
		int numChildren;
		//--- 入力スキーマツリー
		numChildren = _editModel.getInputTableSchemaRootNode().getChildCount();
		for (int index = 0; index < numChildren; ++index) {
			GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)_editModel.getInputTableSchemaRootNode().getChildAt(index);
			_treeInput.expandPath(new TreePath(tableNode.getPath()));
		}
		//--- 出力スキーマツリー
		numChildren = _editModel.getOutputTableSchemaRootNode().getChildCount();
		for (int index = 0; index < numChildren; ++index) {
			GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)_editModel.getOutputTableSchemaRootNode().getChildAt(index);
			_treeOutput.expandPath(new TreePath(tableNode.getPath()));
		}
		
		// 参照モード対応
		if (!_editModel.isEditing()) {
			//--- DateTime formats panel
			_btnEditDataTimeFormats.setEnabled(false);
			_btnEditDataTimeFormats.setVisible(false);
			
			// Input schema tree
			_tbarInputTableFunctions.setVisible(false);
			
			// output schema tree
			_tbarOutputTableFunctions.setVisible(false);
		}
		
		// actions
		setupActions();
		
		// 最小領域サイズの設定
		Dimension dm = getMinimumSize();
		if (dm.width > 640)
			dm.width = 640;
		if (dm.height > 480)
			dm.height = 480;
		setMinimumSize(dm);
	}

	/**
	 * データモデルの変更通知をうけて、他のデータモデルを変更するためのリスナーを登録する。
	 * @since 3.2.1
	 */
	public void setupDataModelListenersForRelation() {
		// フィルタ定義引数テーブルのデータモデルリスナー
		_editModel.getMExecDefArgTableModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent tme) {
				onFilterArgsTableModelChanged(tme);
			}
		});
		
		// 入力スキーマツリーのデータモデルリスナー
		_editModel.getInputTableSchemaTreeModel().addTreeModelListener(new TreeModelListener() {
			@Override
			public void treeStructureChanged(TreeModelEvent tme) {
				onInputTableSchemaTreeModelChanged(TreeModelChangeTypes.TreeStructureChanged, tme);
			}
			
			@Override
			public void treeNodesRemoved(TreeModelEvent tme) {
				onInputTableSchemaTreeModelChanged(TreeModelChangeTypes.TreeNodesRemoved, tme);
			}
			
			@Override
			public void treeNodesInserted(TreeModelEvent tme) {
				onInputTableSchemaTreeModelChanged(TreeModelChangeTypes.TreeNodesInserted, tme);
			}
			
			@Override
			public void treeNodesChanged(TreeModelEvent tme) {
				onInputTableSchemaTreeModelChanged(TreeModelChangeTypes.TreeNodesChanged, tme);
			}
		});
		
		// 計算式テーブルのデータモデルリスナー
		_editModel.getExpressionSchemaTableModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent tme) {
				onExpressionSchemaTableModelChanged(tme);
			}
		});
		
		// 出力スキーマツリーのデータモデルリスナー
		_editModel.getOutputTableSchemaTreeModel().addTreeModelListener(new TreeModelListener() {
			@Override
			public void treeStructureChanged(TreeModelEvent tme) {
				onOutputTableSchemaTreeModelChanged(TreeModelChangeTypes.TreeStructureChanged, tme);
			}
			
			@Override
			public void treeNodesRemoved(TreeModelEvent tme) {
				onOutputTableSchemaTreeModelChanged(TreeModelChangeTypes.TreeNodesRemoved, tme);
			}
			
			@Override
			public void treeNodesInserted(TreeModelEvent tme) {
				onOutputTableSchemaTreeModelChanged(TreeModelChangeTypes.TreeNodesInserted, tme);
			}
			
			@Override
			public void treeNodesChanged(TreeModelEvent tme) {
				onOutputTableSchemaTreeModelChanged(TreeModelChangeTypes.TreeNodesChanged, tme);
			}
		});
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * スキーマ定義の正当性をチェックする。
	 * @return	スキーマ定義が正当な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean verifyData() {
		boolean valid = true;
		
		// check Input schema data
		if (!verifyInputSchemaData()) {
			valid = false;
		}
		
		// check expression schema data
		if (!verifyExpressionSchemaData()) {
			valid = false;
		}
		
		// check join condition schema data
		if (!verifyJoinCondSchemaData()) {
			valid = false;
		}
		
		// check output schema data
		if (!verifyOutputSchemaData()) {
			valid = false;
		}
		
		// completed
		return valid;
	}

	/**
	 * 入力スキーマの正当性をチェックする。
	 * @return	入力スキーマ定義が正当な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.2.1
	 */
	protected boolean verifyInputSchemaData() {
		boolean valid = true;
		int numChildren = _editModel.getInputTableSchemaRootNode().getChildCount();
		for (int index = 0; index < numChildren; ++index) {
			// table
			GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)_editModel.getInputTableSchemaRootNode().getChildAt(index);
			//--- verify table
			boolean oldError = tableNode.hasError();
			boolean newError = !tableNode.verify();
			if (newError || oldError) {
				//--- 更新
				_editModel.getInputTableSchemaTreeModel().nodeChanged(tableNode);
				if (newError) {
					valid = false;
				}
			}
			
			// fields
			int numFields = tableNode.getChildCount();
			for (int fi = 0; fi < numFields; ++fi) {
				GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)tableNode.getChildAt(fi);
				//--- verify field
				oldError = fieldNode.hasError();
				newError = !fieldNode.verify();
				if (newError || oldError) {
					//--- 更新
					_editModel.getInputTableSchemaTreeModel().nodeChanged(fieldNode);
					if (newError) {
						valid = false;
					}
				}
			}
		}
		// completed
		return valid;
	}
	
	/**
	 * 出力スキーマの正当性をチェックする。
	 * @return	出力スキーマ定義が正当な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.2.1
	 */
	protected boolean verifyOutputSchemaData() {
		boolean valid = true;
		int numChildren = _editModel.getOutputTableSchemaRootNode().getChildCount();
		for (int index = 0; index < numChildren; ++index) {
			// table
			GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)_editModel.getOutputTableSchemaRootNode().getChildAt(index);
			//--- verify table
			boolean oldError = tableNode.hasError();
			boolean newError = !tableNode.verify();
			if (newError || oldError) {
				//--- 更新
				_editModel.getOutputTableSchemaTreeModel().nodeChanged(tableNode);
				if (newError) {
					valid = false;
				}
			}
			
			// fields
			int numFields = tableNode.getChildCount();
			for (int fi = 0; fi < numFields; ++fi) {
				GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)tableNode.getChildAt(fi);
				//--- verify field
				oldError = fieldNode.hasError();
				newError = !fieldNode.verify();
				if (newError || oldError) {
					//--- 更新
					_editModel.getOutputTableSchemaTreeModel().nodeChanged(fieldNode);
					if (newError) {
						valid = false;
					}
				}
			}
		}
		// completed
		return valid;
	}

	/**
	 * 計算式スキーマの正当性をチェックする。
	 * @return	計算式スキーマ定義が正当な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.2.1
	 */
	protected boolean verifyExpressionSchemaData() {
		boolean valid = true;
		GenericExpressionSchemaTableModel tableModel = _editModel.getExpressionSchemaTableModel();
		int numElements = tableModel.getRowCount();
		for (int index = 0; index < numElements; ++index) {
			GenericExpressionElementEditModel expModel = tableModel.getElement(index);
			//--- verify
			boolean oldError = expModel.hasError();
			boolean newError = !expModel.verify();
			if (newError || oldError) {
				//--- 更新
				tableModel.fireTableRowsUpdated(index, index);
				if (newError) {
					valid = false;
				}
			}
		}
		// completed
		return valid;
	}
	
	/**
	 * 結合条件スキーマの正当性をチェックする。
	 * @return	結合条件スキーマ定義が正当な場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @since 3.2.1
	 */
	protected boolean verifyJoinCondSchemaData() {
		boolean valid = true;
		GenericJoinCondSchemaTableModel tableModel = _editModel.getJoinConditionSchemaTableModel();
		int numElements = tableModel.getRowCount();
		for (int index = 0; index < numElements; ++index) {
			GenericJoinCondElementEditModel joinModel = tableModel.getElement(index);
			//--- verify
			boolean oldError = joinModel.hasError();
			boolean newError = !joinModel.verify();
			if (newError || oldError) {
				//--- 更新
				tableModel.fireTableRowsUpdated(index, index);
				if (newError) {
					valid = false;
				}
			}
		}
		// completed
		return valid;
	}

	/**
	 * このコンポーネントの最初のエラーをメッセージボックスで表示する。
	 */
	public void showFirstError() {
		if (showFirstErrorForInputSchemaData()) {
			return;
		}
		
		if (showFirstErrorForExpressionSchemaData()) {
			return;
		}
		
		if (showFirstErrorForJoinCondSchemaData()) {
			return;
		}
		
		if (showFirstErrorForOutputSchemaData()) {
			return;
		}
	}
	
	protected String formatFirstErrorMessage(String message, String category, String objectName) {
		return String.format("%s\n(%s[%s])", message, category, objectName);
	}

	/**
	 * 入力スキーマデータの最初のエラーメッセージを表示する。
	 * @return	エラーメッセージの表示が行われた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 3.2.1
	 */
	public boolean showFirstErrorForInputSchemaData() {
		int numChildren = _editModel.getInputTableSchemaRootNode().getChildCount();
		for (int index = 0; index < numChildren; ++index) {
			// table
			GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)_editModel.getInputTableSchemaRootNode().getChildAt(index);
			if (tableNode.hasError()) {
				_treeOutput.clearSelection();
				_tableExp.getTableComponent().clearSelection();
				_tableJoin.getTableComponent().clearSelection();
				_treeInput.clearSelection();
				TreePath tp = new TreePath(tableNode.getPath());
				_treeInput.scrollPathToVisible(tp);
				_treeInput.setSelectionPath(tp);
				String strMessage = formatFirstErrorMessage(tableNode.getAvailableErrorMessage(),
										RunnerMessages.getInstance().GenericFilterEditDlg_name_InputSchema,
										tableNode.toString());
				Application.showErrorMessage(this, strMessage);
				return true;
			}
			
			// fields
			int numFields = tableNode.getChildCount();
			for (int fi = 0; fi < numFields; ++fi) {
				GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)tableNode.getChildAt(fi);
				if (fieldNode.hasError()) {
					_treeOutput.clearSelection();
					_tableExp.getTableComponent().clearSelection();
					_tableJoin.getTableComponent().clearSelection();
					_treeInput.clearSelection();
					TreePath tablePath = new TreePath(tableNode.getPath());
					if (!_treeInput.isExpanded(tablePath))
						_treeInput.expandPath(tablePath);
					TreePath tp = new TreePath(fieldNode.getPath());
					_treeInput.scrollPathToVisible(tp);
					_treeInput.setSelectionPath(tp);
					InputCsvFieldSchemaEditModel fieldModel = (InputCsvFieldSchemaEditModel)fieldNode.getUserObject();
					String strMessage = formatFirstErrorMessage(fieldNode.getAvailableErrorMessage(),
											RunnerMessages.getInstance().GenericFilterEditDlg_name_InputSchema,
											fieldModel.toString());
					Application.showErrorMessage(this, strMessage);
					return true;
				}
			}
		}
		// no error
		return false;
	}
	
	/**
	 * 出力スキーマデータの最初のエラーメッセージを表示する。
	 * @return	エラーメッセージの表示が行われた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 3.2.1
	 */
	public boolean showFirstErrorForOutputSchemaData() {
		int numChildren = _editModel.getOutputTableSchemaRootNode().getChildCount();
		for (int index = 0; index < numChildren; ++index) {
			// table
			GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)_editModel.getOutputTableSchemaRootNode().getChildAt(index);
			if (tableNode.hasError()) {
				_treeInput.clearSelection();
				_tableExp.getTableComponent().clearSelection();
				_tableJoin.getTableComponent().clearSelection();
				_treeOutput.clearSelection();
				TreePath tp = new TreePath(tableNode.getPath());
				_treeOutput.scrollPathToVisible(tp);
				_treeOutput.setSelectionPath(tp);
				String strMessage = formatFirstErrorMessage(tableNode.getAvailableErrorMessage(),
										RunnerMessages.getInstance().GenericFilterEditDlg_name_OutputSchema,
										tableNode.toString());
				Application.showErrorMessage(this, strMessage);
				return true;
			}
			
			// fields
			int numFields = tableNode.getChildCount();
			for (int fi = 0; fi < numFields; ++fi) {
				GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)tableNode.getChildAt(fi);
				if (fieldNode.hasError()) {
					_treeInput.clearSelection();
					_tableExp.getTableComponent().clearSelection();
					_tableJoin.getTableComponent().clearSelection();
					_treeOutput.clearSelection();
					TreePath tablePath = new TreePath(tableNode.getPath());
					if (!_treeOutput.isExpanded(tablePath))
						_treeOutput.expandPath(tablePath);
					TreePath tp = new TreePath(fieldNode.getPath());
					_treeOutput.scrollPathToVisible(tp);
					_treeOutput.setSelectionPath(tp);
					OutputCsvFieldSchemaEditModel fieldModel = (OutputCsvFieldSchemaEditModel)fieldNode.getUserObject();
					String strMessage = formatFirstErrorMessage(fieldNode.getAvailableErrorMessage(),
											RunnerMessages.getInstance().GenericFilterEditDlg_name_OutputSchema,
											fieldModel.toString());
					Application.showErrorMessage(this, strMessage);
					return true;
				}
			}
		}
		// no error
		return false;
	}
	
	/**
	 * 計算式スキーマデータの最初のエラーメッセージを表示する。
	 * @return	エラーメッセージの表示が行われた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 3.2.1
	 */
	public boolean showFirstErrorForExpressionSchemaData() {
		GenericExpressionSchemaTableModel tableModel = _editModel.getExpressionSchemaTableModel();
		int numElements = tableModel.getRowCount();
		for (int index = 0; index < numElements; ++index) {
			GenericExpressionElementEditModel expModel = tableModel.getElement(index);
			if (expModel.hasError()) {
				_treeInput.clearSelection();
				_treeOutput.clearSelection();
				_tableJoin.getTableComponent().clearSelection();
				_tableExp.getTableComponent().clearSelection();
				_tabConditions.setSelectedComponent(_tableExp);
				_tableExp.getTableComponent().scrollToVisibleCell(index, 0);
				_tableExp.getTableComponent().setRowSelectionInterval(index, index);
				String strMessage = formatFirstErrorMessage(expModel.getAvailableErrorMessage(),
										RunnerMessages.getInstance().GenericFilterEditDlg_name_ArithExp,
										expModel.toString());
				Application.showErrorMessage(this, strMessage);
				return true;
			}
		}
		// no error
		return false;
	}
	
	/**
	 * 結合条件スキーマデータの最初のエラーメッセージを表示する。
	 * @return	エラーメッセージの表示が行われた場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @since 3.2.1
	 */
	public boolean showFirstErrorForJoinCondSchemaData() {
		GenericJoinCondSchemaTableModel tableModel = _editModel.getJoinConditionSchemaTableModel();
		int numElements = tableModel.getRowCount();
		for (int index = 0; index < numElements; ++index) {
			GenericJoinCondElementEditModel joinModel = tableModel.getElement(index);
			if (joinModel.hasError()) {
				_treeInput.clearSelection();
				_treeOutput.clearSelection();
				_tableExp.getTableComponent().clearSelection();
				_tableJoin.getTableComponent().clearSelection();
				_tabConditions.setSelectedComponent(_tableJoin);
				_tableJoin.getTableComponent().scrollToVisibleCell(index, 0);
				_tableJoin.getTableComponent().setRowSelectionInterval(index, index);
				String strMessage = formatFirstErrorMessage(joinModel.getAvailableErrorMessage(),
										RunnerMessages.getInstance().GenericFilterEditDlg_name_JoinCond,
										joinModel.toString());
				Application.showErrorMessage(this, strMessage);
				return true;
			}
		}
		// no error
		return false;
	}

	public void restoreConfiguration(ExConfiguration config, String prefix) {
		// restore divider location
		if (config != null) {
			int dl;
			// Input schema area
			dl = config.getDividerLocation(prefix + PROPKEY_DIVIDER_INPUTSCHEMA);
			if (dl > 0)
				_splitInputPane.setDividerLocation(dl);
			
			// Output schema area
			dl = config.getDividerLocation(prefix + PROPKEY_DIVIDER_OUTPUTSCHEMA);
			if (dl > 0)
				_splitOutputPane.setDividerLocation(dl);
			
			// Property pane area
			if (_editModel.isEditing()) {
				//--- 編集モードのときのみプロパティを表示
				dl = config.getDividerLocation(prefix + PROPKEY_DIVIDER_PROPERTY);
				if (dl > 0)
					_splitCondPane.setDividerLocation(dl);
			}
		}
	}

	public void storeConfiguration(ExConfiguration config, String prefix) {
		// store divider location
		if (config != null) {
			int dl;
			// Input schema area
			dl = _splitInputPane.getDividerLocation();
			config.setDividerLocation(prefix + PROPKEY_DIVIDER_INPUTSCHEMA, dl);
			
			// Output schema area
			dl = _splitOutputPane.getDividerLocation();
			config.setDividerLocation(prefix + PROPKEY_DIVIDER_OUTPUTSCHEMA, dl);
			
			// Property pane area
			if (_editModel.isEditing()) {
				//--- 編集モード時のみプロパティを表示
				dl = _splitCondPane.getDividerLocation();
				config.setDividerLocation(prefix + PROPKEY_DIVIDER_PROPERTY, dl);
			}
		}
	}

	//------------------------------------------------------------
	// Event handlers
	//------------------------------------------------------------
	
	protected void onConditionTabSelectionChanged(ChangeEvent ce) {
		int index = _tabConditions.getSelectedIndex();
		if (index == 1) {
			// Join
			_propLayout.show(_propPanel, PROPPANEKEY_Join);
		}
		else {
			// Expression
			_propLayout.show(_propPanel, PROPPANEKEY_Exp);
		}
	}

	/**
	 * 現在の設定に応じた内容で、日付時刻書式ペインを更新する。
	 */
	protected void refreshDateTimeFormatPane() {
		if (_editModel.getDateTimeFormatEditModel().hasFilterArgumentModel()) {
			// フィルタ定義引数で指定
			_stcDateTimeFormats.setText(RunnerMessages.getInstance().SchemaDateTimeFormatEditDlg_rdo_useFilterArg);
		}
		else {
			String strPattern = _editModel.getDateTimeFormatEditModel().getPatternString();
			if (strPattern == null) {
				_stcDateTimeFormats.setText(SchemaDateTimeFormats.getDefaultSpecifier());
			} else {
				_stcDateTimeFormats.setText(strPattern);
			}
		}
	}

	/**
	 * 日付時刻書式の編集ボタンがクリックされたときに呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onClickedDateTimeFormatEditButton(ActionEvent ae) {
		SchemaDateTimeFormatEditDialog dlg;
		Window owner = SwingUtilities.getWindowAncestor(this);
		if (owner instanceof Dialog) {
			dlg = new SchemaDateTimeFormatEditDialog((Dialog)owner);
		} else {
			dlg = new SchemaDateTimeFormatEditDialog((Frame)owner);
		}
		dlg.initialComponent(_editModel);
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			return;	// user canceled
		}
		
		// modified DateTime formats
		SchemaDateTimeFormatEditModel dtfModel = _editModel.getDateTimeFormatEditModel();
		if (dlg.isUseFilterArg()) {
			// フィルタ定義引数を使用する
			if (!dtfModel.hasFilterArgumentModel()) {
				GenericFilterArgEditModel argModel = _editModel.addDateTimeFormatFilterArgument();
				dtfModel.setPattern(null);
				dtfModel.setFilterArgumentModel(argModel);
			}
		}
		else {
			// パターンを指定する
			SchemaDateTimeFormats dtf = dlg.getCustomDateTimeFormats();
			dtfModel.setFilterArgumentModel(null);
			dtfModel.setPattern(dtf);
		}
		//--- refresh display
		refreshDateTimeFormatPane();
	}

	/**
	 * コマンドボタンがクリックされたときに呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onCommandButtonClicked(ActionEvent ae) {
		String commandKey = ae.getActionCommand();
		if (BTNCMD_INPUT_TABLE_NEW.equals(commandKey)) {
			onClickedInputTableNewButton(ae);
		}
		else if (BTNCMD_INPUT_TABLE_EDIT.equals(commandKey)) {
			onClickedInputTableEditButton(ae);
		}
		else if (BTNCMD_INPUT_TABLE_DELETE.equals(commandKey)) {
			onClickedInputTableDeleteButton(ae);
		}
		else if (BTNCMD_INPUT_TABLE_TO_OUTPUT.equals(commandKey)) {
			onClickedInputTableToOutputButton(ae);
		}
		else if (BTNCMD_OUTPUT_TABLE_NEW.equals(commandKey)) {
			onClickedOutputTableNewButton(ae);
		}
		else if (BTNCMD_OUTPUT_TABLE_EDIT.equals(commandKey)) {
			onClickedOutputTableEditButton(ae);
		}
		else if (BTNCMD_OUTPUT_TABLE_DELETE.equals(commandKey)) {
			onClickedOutputTableDeleteButton(ae);
		}
		else if (BTNCMD_OUTPUT_FIELD_DELETE.equals(commandKey)) {
			onClickedOutputFieldDeleteButton(ae);
		}
		else if (BTNCMD_OUTPUT_FIELD_MOVEUP.equals(commandKey)) {
			onClickedOutputFieldMoveUpButton(ae);
		}
		else if (BTNCMD_OUTPUT_FIELD_MOVEDOWN.equals(commandKey)) {
			onClickedOutputFieldMoveDownButton(ae);
		}
	}

	/**
	 * 入力スキーマのテーブル追加ボタン押下時に呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onClickedInputTableNewButton(ActionEvent ae) {
		// 編集ダイアログ
		GenericInputSchemaEditDialog dlg;
		Window window = SwingUtilities.getWindowAncestor(this);
		if (window instanceof Dialog) {
			dlg = new GenericInputSchemaEditDialog((Dialog)window, null, _editModel);
		} else {
			dlg = new GenericInputSchemaEditDialog((Frame)window, null, _editModel);
		}
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			return;	// user canceled
		}
		
		// ツリーへ追加
		InputCsvTableSchemaEditModel newDataModel = dlg.getDataModel();
		TreePath newPath = _editModel.addInputTableSchema(newDataModel);
		//--- ツリーの更新
		_treeInput.clearSelection();
		_treeInput.expandPath(newPath);				// 展開
		_treeInput.scrollPathToVisible(newPath);	// 表示
		_treeInput.setSelectionPath(newPath);		// 選択
		//--- コンボボックスの更新は、ツリーモデルの変更通知によって更新済み
	}

	/**
	 * 入力スキーマのテーブル編集ボタン押下時に呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onClickedInputTableEditButton(ActionEvent ae) {
		// 選択位置の取得
		TreePath tp = _treeInput.getSelectionPath();
		if (tp == null)
			return;	// no selection
		
		// ノードの取得
		GenericTableSchemaTreeNode tableNode;
		GenericSchemaElementTreeNode srcNode = (GenericSchemaElementTreeNode)tp.getLastPathComponent();
		if (srcNode.isFieldNode()) {
			tableNode = (GenericTableSchemaTreeNode)srcNode.getParent();
		}
		else if (srcNode.isTableNode()) {
			tableNode = (GenericTableSchemaTreeNode)srcNode;
		}
		else {
			return;	// no table and field
		}

		// 編集ダイアログ
		GenericInputSchemaEditDialog dlg;
		Window window = SwingUtilities.getWindowAncestor(this);
		if (window instanceof Dialog) {
			dlg = new GenericInputSchemaEditDialog((Dialog)window, tableNode, _editModel);
		} else {
			dlg = new GenericInputSchemaEditDialog((Frame)window, tableNode, _editModel);
		}
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			return;	// user canceled
		}
		
		// 既存のテーブルスキーマを、新しいテーブルスキーマで更新
		TreePath tablePath = new TreePath(tableNode.getPath());
		boolean expanded = _treeInput.isExpanded(tablePath);
		_treeInput.clearSelection();
		_editModel.updateInputTableSchema(tableNode, dlg.getDataModel(), dlg.getRemovedOriginalFieldList());
		//--- ツリーの状態を更新
		if (expanded)
			_treeInput.expandPath(tablePath);
		else
			_treeInput.collapsePath(tablePath);
		_treeInput.scrollPathToVisible(tablePath);	// 表示
		_treeInput.setSelectionPath(tablePath);		// 選択
	}

	/**
	 * 入力スキーマのテーブル削除ボタン押下時に呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onClickedInputTableDeleteButton(ActionEvent ae) {
		// 選択位置の取得
		TreePath tp = _treeInput.getSelectionPath();
		if (tp == null)
			return;	// no selection
		
		// ノードの取得
		GenericTableSchemaTreeNode tableNode;
		GenericSchemaElementTreeNode srcNode = (GenericSchemaElementTreeNode)tp.getLastPathComponent();
		if (srcNode.isFieldNode()) {
			tableNode = (GenericTableSchemaTreeNode)srcNode.getParent();
		}
		else if (srcNode.isTableNode()) {
			tableNode = (GenericTableSchemaTreeNode)srcNode;
		}
		else {
			return;	// no table and field
		}
		
		// 参照の有無を判定
		boolean refered = false;
		int numFields = tableNode.getChildCount();
		for (int index = 0; index < numFields; ++index) {
			GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)tableNode.getChildAt(index);
			InputCsvFieldSchemaEditModel fieldModel = (InputCsvFieldSchemaEditModel)fieldNode.getUserObject();
			if (_editModel.isPrecedentReferenceObject(fieldModel)) {
				refered = true;
				break;
			}
		}
		
		// 問合せ
		String msgConfirm;
		if (tableNode == srcNode) {
			if (refered)
				msgConfirm = RunnerMessages.getInstance().confirmDeleteSpecifiedInputTableSchemaWithRefer;
			else
				msgConfirm = RunnerMessages.getInstance().confirmDeleteSpecifiedInputTableSchemaNoRefer;
		} else {
			if (refered)
				msgConfirm = RunnerMessages.getInstance().confirmDeleteParentInputTableSchemaWithRefer;
			else
				msgConfirm = RunnerMessages.getInstance().confirmDeleteParentInputTableSchemaNoRefer;
		}
		int ret = JOptionPane.showConfirmDialog(this, msgConfirm,
						CommonMessages.getInstance().msgboxTitleWarn,
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (ret != JOptionPane.OK_OPTION) {
			// user canceled
			return;
		}
		
		// テーブルの削除
		TreePath nextPath = _editModel.deleteInputTableSchema(tableNode);
		//--- ツリーの状態を更新
		_treeInput.clearSelection();
		if (nextPath != null) {
			_treeInput.scrollPathToVisible(nextPath);	// 表示
			_treeInput.setSelectionPath(nextPath);		// 選択
		}
	}
	
	protected void onClickedInputTableToOutputButton(ActionEvent ae) {
		// 現在の選択オブジェクトを取得
		if (_treeInput.isSelectionEmpty()) {
			return;	// no selection
		}

		// 追加するコンテナオブジェクトを収集
		TreePath[] paths = _treeInput.getSelectionPaths();
		ArrayList<InputCsvFieldSchemaEditModel> fields = new ArrayList<InputCsvFieldSchemaEditModel>();
		for (TreePath path : paths) {
			Object lastobj = path.getLastPathComponent();
			if (lastobj instanceof GenericTableSchemaTreeNode) {
				//--- テーブル選択の場合は、その全フィールドを追加
				GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)lastobj;
				int len = tableNode.getChildCount();
				for (int i = 0; i < len; ++i) {
					GenericSchemaElementTreeNode node = (GenericSchemaElementTreeNode)tableNode.getChildAt(i);
					//--- フィールドを追加
					fields.add((InputCsvFieldSchemaEditModel)node.getUserObject());
				}
			}
			else if (lastobj instanceof GenericSchemaElementTreeNode) {
				//--- フィールドならそのフィールドのみ
				GenericSchemaElementTreeNode node = (GenericSchemaElementTreeNode)lastobj;
				InputCsvFieldSchemaEditModel field = (InputCsvFieldSchemaEditModel)node.getUserObject();
				if (field != null) {
					fields.add(field);
				}
			}
		}
		
		// 出力ツリーの選択パスを取得
		TreePath insertTreePos;
		TreePath[] outSelected = _treeOutput.getSelectionPaths();
		if (outSelected != null && outSelected.length > 0) {
			//--- 最後に選択されたノードの次に追加
			insertTreePos = outSelected[outSelected.length-1];
		} else{
			//--- 終端へ追加
			insertTreePos = null;
		}
		
		// 出力ツリーに追加
		TreePath firstNodePath = _editModel.addOutputTableSchema(fields, insertTreePos);
		
		// 表示を更新
		GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)firstNodePath.getLastPathComponent();
		GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)fieldNode.getParent();
		//--- 出力ツリーを展開
		_treeOutput.expandPath(new TreePath(tableNode.getPath()));
		//--- 最初に追加されたフィールドを表示
		_treeOutput.clearSelection();
		_treeOutput.scrollPathToVisible(firstNodePath);
		_treeOutput.setSelectionPath(firstNodePath);
		//--- 入力側は次のノードを選択
	}

	/**
	 * [出力テーブルの追加]ボタンが押下されたときに呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onClickedOutputTableNewButton(ActionEvent ae) {
		// 編集ダイアログ
		GenericOutputSchemaEditDialog dlg;
		Window window = SwingUtilities.getWindowAncestor(this);
		if (window instanceof Dialog) {
			dlg = new GenericOutputSchemaEditDialog((Dialog)window, null, _editModel);
		} else {
			dlg = new GenericOutputSchemaEditDialog((Frame)window, null, _editModel);
		}
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			return;	// user canceled
		}
		
		// 編集により生成したデータモデルをそのまま利用
		OutputCsvTableSchemaEditModel newDataModel = dlg.getDataModel();
		TreePath newPath = _editModel.attachOutputTableSchema(newDataModel);
		//--- ツリーの状態を更新
		_treeOutput.clearSelection();
		_treeOutput.expandPath(newPath);			// 展開
		_treeOutput.scrollPathToVisible(newPath);	// 表示
		_treeOutput.setSelectionPath(newPath);		// 選択
	}

	/**
	 * [出力テーブルの編集]ボタンが押下されたときに呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onClickedOutputTableEditButton(ActionEvent ae) {
		// 選択位置の取得
		TreePath tp = _treeOutput.getSelectionPath();
		if (tp == null)
			return;	// no selection
		
		// ノードの取得
		GenericTableSchemaTreeNode tableNode;
		GenericSchemaElementTreeNode srcNode = (GenericSchemaElementTreeNode)tp.getLastPathComponent();
		if (srcNode.isFieldNode()) {
			tableNode = (GenericTableSchemaTreeNode)srcNode.getParent();
		}
		else if (srcNode.isTableNode()) {
			tableNode = (GenericTableSchemaTreeNode)srcNode;
		}
		else {
			return;	// no table and field
		}

		// 編集ダイアログ
		GenericOutputSchemaEditDialog dlg;
		Window window = SwingUtilities.getWindowAncestor(this);
		if (window instanceof Dialog) {
			dlg = new GenericOutputSchemaEditDialog((Dialog)window, tableNode, _editModel);
		} else {
			dlg = new GenericOutputSchemaEditDialog((Frame)window, tableNode, _editModel);
		}
		dlg.initialComponent();
		dlg.setVisible(true);
		dlg.dispose();
		if (dlg.getDialogResult() != IDialogResult.DialogResult_OK) {
			return;	// user canceled
		}
		
		// 既存のテーブルスキーマを、新しいテーブルスキーマに置き換え
		TreePath tablePath = new TreePath(tableNode.getPath());
		boolean expanded = _treeOutput.isExpanded(tablePath);
		_editModel.replaceOutputTableSchema(tableNode, dlg.getDataModel());
		//--- ツリーの状態を更新
		_treeOutput.clearSelection();
		if (expanded)
			_treeOutput.expandPath(tablePath);
		else
			_treeOutput.collapsePath(tablePath);
		_treeOutput.scrollPathToVisible(tablePath);	// 表示
		_treeOutput.setSelectionPath(tablePath);	// 選択
	}

	/**
	 * [出力テーブルの削除]ボタンが押下されたときに呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onClickedOutputTableDeleteButton(ActionEvent ae) {
		// 選択位置の取得
		TreePath tp = _treeOutput.getSelectionPath();
		if (tp == null)
			return;	// no selection
		
		// ノードの取得
		String msgConfirm;
		GenericTableSchemaTreeNode tableNode;
		GenericSchemaElementTreeNode srcNode = (GenericSchemaElementTreeNode)tp.getLastPathComponent();
		if (srcNode.isFieldNode()) {
			tableNode = (GenericTableSchemaTreeNode)srcNode.getParent();
			msgConfirm = RunnerMessages.getInstance().confirmDeleteParentOutputTableSchema;
		}
		else if (srcNode.isTableNode()) {
			tableNode = (GenericTableSchemaTreeNode)srcNode;
			msgConfirm = RunnerMessages.getInstance().confirmDeleteSpecifiedOutputTableSchema;
		}
		else {
			return;	// no table and field
		}
		
		// 問合せ
		int ret = JOptionPane.showConfirmDialog(this, msgConfirm,
						CommonMessages.getInstance().msgboxTitleWarn,
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (ret != JOptionPane.OK_OPTION) {
			// user canceled
			return;
		}
		
		// テーブルの削除
		TreePath nextPath = _editModel.deleteOutputTableSchema(tableNode);
		//--- ツリーの状態を更新
		_treeOutput.clearSelection();
		if (nextPath != null) {
			_treeOutput.scrollPathToVisible(nextPath);	// 表示
			_treeOutput.setSelectionPath(nextPath);		// 選択
		}
	}

	/**
	 * [出力フィールドの削除]ボタンが押下されたときに呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onClickedOutputFieldDeleteButton(ActionEvent ae) {
		// 選択位置の取得
		TreePath tp = _treeOutput.getSelectionPath();
		if (tp == null)
			return;	// no selection
		
		// ノードの取得
		GenericSchemaElementTreeNode srcNode = (GenericSchemaElementTreeNode)tp.getLastPathComponent();
		if (!srcNode.isFieldNode())
			return;	// no field data
		
		// 削除
		TreePath nextRemovable = _editModel.deleteOutputFieldSchema(srcNode);
		
		// 選択位置の更新
		_treeOutput.clearSelection();
		if (nextRemovable != null) {
			_treeOutput.scrollPathToVisible(nextRemovable);	// 表示
			_treeOutput.setSelectionPath(nextRemovable);	// 選択
		}
	}

	/**
	 * [出力フィールドを上へ移動]ボタンが押下されたときに呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onClickedOutputFieldMoveUpButton(ActionEvent ae) {
		// 選択位置の取得
		TreePath tp = _treeOutput.getSelectionPath();
		if (tp == null)
			return;	// no selection
		
		// ノードの取得
		GenericSchemaElementTreeNode srcNode = (GenericSchemaElementTreeNode)tp.getLastPathComponent();
		if (!srcNode.isFieldNode())
			return;	// no field data
		
		// 移動
		TreePath newPath = _editModel.shiftOutputFieldSchema(srcNode, -1);
		if (newPath == null)
			return;
		
		// 選択位置の変更
		_treeOutput.clearSelection();
		_treeOutput.scrollPathToVisible(newPath);	// 表示
		_treeOutput.setSelectionPath(newPath);		// 選択
	}

	/**
	 * [出力フィールドを下へ移動]ボタンが押下されたときに呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onClickedOutputFieldMoveDownButton(ActionEvent ae) {
		// 選択位置の取得
		TreePath tp = _treeOutput.getSelectionPath();
		if (tp == null)
			return;	// no selection
		
		// ノードの取得
		GenericSchemaElementTreeNode srcNode = (GenericSchemaElementTreeNode)tp.getLastPathComponent();
		if (!srcNode.isFieldNode())
			return;	// no field data
		
		// 移動
		TreePath newPath = _editModel.shiftOutputFieldSchema(srcNode, +1);
		if (newPath == null)
			return;
		
		// 選択位置の変更
		_treeOutput.clearSelection();
		_treeOutput.scrollPathToVisible(newPath);	// 表示
		_treeOutput.setSelectionPath(newPath);		// 選択
	}

	/**
	 * 計算式テーブルパネルの[出力へ追加]ボタンが押下されたときに呼び出されるイベントハンドラ。
	 * @param ae	イベントオブジェクト
	 */
	protected void onClickedExpressionToOutputButton(ActionEvent ae) {
		// 選択状態の確認
		int row = _tableExp.getTableComponent().getSelectedRow();
		if (row < 0)
			return;	// no selection
		
		// 出力対象を取得
		GenericExpressionElementEditModel elem = _editModel.getExpressionSchemaTableModel().getElement(row);
		
		// 出力ツリーの選択パスを取得
		TreePath insertTreePos;
		TreePath[] outSelected = _treeOutput.getSelectionPaths();
		if (outSelected != null && outSelected.length > 0) {
			//--- 最後に選択されたノードの次に追加
			insertTreePos = outSelected[outSelected.length-1];
		} else{
			//--- 終端へ追加
			insertTreePos = null;
		}
		
		// 出力ツリーに追加
		TreePath firstNodePath = _editModel.addOutputTableSchema(Collections.singletonList(elem), insertTreePos);
		
		// 表示を更新
		GenericSchemaElementTreeNode fieldNode = (GenericSchemaElementTreeNode)firstNodePath.getLastPathComponent();
		GenericTableSchemaTreeNode tableNode = (GenericTableSchemaTreeNode)fieldNode.getParent();
		//--- 出力ツリーを展開
		_treeOutput.expandPath(new TreePath(tableNode.getPath()));
		//--- 最初に追加されたフィールドを表示
		_treeOutput.clearSelection();
		_treeOutput.scrollPathToVisible(firstNodePath);
		_treeOutput.setSelectionPath(firstNodePath);
		//--- 入力側は次のノードを選択
	}

	/**
	 * 入力スキーマツリーの選択状態が変更されたときに呼び出されるイベントハンドラ。
	 * @param tse	イベントオブジェクト
	 */
	protected void onInputSchemaTreeSelectionChanged(TreeSelectionEvent tse) {
		if (!_editModel.isEditing()) {
			_btnInputTableNew.setEnabled(false);
			_btnInputTableEdit.setEnabled(false);
			_btnInputTableDelete.setEnabled(false);
			_btnInputTableToOutput.setEnabled(false);
		}
		else if (_treeInput.isSelectionEmpty()) {
			_btnInputTableToOutput.setEnabled(false);
			_btnInputTableEdit.setEnabled(false);
			_btnInputTableDelete.setEnabled(false);
		} else {
			_btnInputTableToOutput.setEnabled(true);
			_btnInputTableEdit.setEnabled(true);
			_btnInputTableDelete.setEnabled(true);
		}
	}

	/**
	 * 出力スキーマツリーの選択状態が変更されたときに呼び出されるイベントハンドラ。
	 * @param tse	イベントオブジェクト
	 */
	protected void onOutputSchemaTreeSelectionChanged(TreeSelectionEvent tse) {
		boolean enableTableEdit;
		boolean enableTableDelete;
		boolean enableFieldDelete;
		boolean enableFieldMoveUp;
		boolean enableFieldMoveDw;

		if (!_editModel.isEditing()) {
			_btnOutputTableNew.setEnabled(false);
			enableTableEdit   = false;
			enableTableDelete = false;
			enableFieldDelete = false;
			enableFieldMoveUp = false;
			enableFieldMoveDw = false;
		}
		else if (_treeOutput.isSelectionEmpty()) {
			// 非選択
			enableTableEdit   = false;
			enableTableDelete = false;
			enableFieldDelete = false;
			enableFieldMoveUp = false;
			enableFieldMoveDw = false;
		}
		else {
			// フィールドもしくはテーブル選択中
			enableTableEdit   = true;
			enableTableDelete = true;

			// フィールドデータ取得
			GenericSchemaElementTreeNode node = (GenericSchemaElementTreeNode)_treeOutput.getSelectionPath().getLastPathComponent();
			if (node.isFieldNode()) {
				// フィールドノード
				enableFieldDelete = true;
				int numChildren = node.getParent().getChildCount();
				OutputCsvFieldSchemaEditModel fieldModel = (OutputCsvFieldSchemaEditModel)node.getUserObject();
				int elemNo = fieldModel.getElementNo();
				//--- can move up
				enableFieldMoveUp = (elemNo > 1);
				//--- can move down
				enableFieldMoveDw = (elemNo < numChildren);
			}
			else {
				// テーブルノード
				enableFieldDelete = false;
				enableFieldMoveUp = false;
				enableFieldMoveDw = false;
			}
		}
		
		// 更新
		_btnOutputTableEdit.setEnabled(enableTableEdit);
		_btnOutputTableDelete.setEnabled(enableTableDelete);
		_btnOutputFieldDelete.setEnabled(enableFieldDelete);
		_btnOutputFieldMoveUp.setEnabled(enableFieldMoveUp);
		_btnOutputFieldMoveDown.setEnabled(enableFieldMoveDw);
	}

	/**
	 * フィルタ定義引数テーブルの内容が変更されたときに呼び出されるイベントハンドラ。
	 * @param tme	イベントオブジェクト
	 * @since 3.2.1
	 */
	protected void onFilterArgsTableModelChanged(TableModelEvent tme) {
		// フィルタ定義引数選択用コンボボックスの更新
		_tableExp.getFilterArgOperandComboBoxModel(GenericOperationSchemaPropertyPane.INDEX_LEFT ).refresh();
		_tableExp.getFilterArgOperandComboBoxModel(GenericOperationSchemaPropertyPane.INDEX_RIGHT).refresh();
	}

	/**
	 * 計算式テーブルの内容が変更されたときに呼び出されるイベントハンドラ。
	 * @param tme	イベントオブジェクト
	 * @since 3.2.1
	 */
	protected void onExpressionSchemaTableModelChanged(TableModelEvent tme) {
		// 計算式要素選択用コンボボックスの更新
		_tableExp.getExpressionOperandComboBoxModel(GenericOperationSchemaPropertyPane.INDEX_LEFT ).refresh();
		_tableExp.getExpressionOperandComboBoxModel(GenericOperationSchemaPropertyPane.INDEX_RIGHT).refresh();
	}

	/**
	 * 入力スキーマツリーのデータモデルの内容が変更されたときに呼び出されるイベントハンドラ。
	 * @param changeType	変更種別
	 * @param tme	イベントオブジェクト
	 * @since 3.2.1
	 */
	protected void onInputTableSchemaTreeModelChanged(TreeModelChangeTypes changeType, TreeModelEvent tme) {
		// 入力フィールド選択用コンボボックスの更新
		_tableExp.getInputFieldComboBoxModel(GenericOperationSchemaPropertyPane.INDEX_LEFT ).refresh();
		_tableExp.getInputFieldComboBoxModel(GenericOperationSchemaPropertyPane.INDEX_RIGHT).refresh();
		_tableJoin.getInputFieldComboBoxModel(GenericOperationSchemaPropertyPane.INDEX_LEFT ).refresh();
		_tableJoin.getInputFieldComboBoxModel(GenericOperationSchemaPropertyPane.INDEX_RIGHT).refresh();
	}

	/**
	 * 出力スキーマツリーのデータモデルの内容が変更されたときに呼び出されるイベントハンドラ。
	 * @param changeType	変更種別
	 * @param tme	イベントオブジェクト
	 * @since 3.2.1
	 */
	protected void onOutputTableSchemaTreeModelChanged(TreeModelChangeTypes changeType, TreeModelEvent tme) {
		// 特に何も行わない
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 日付時刻書式を表示するパネルを生成する。
	 * @return	生成されたパネル
	 */
	protected JPanel createDateTimeFormatsPane() {
		// create components
		JLabel lbl = new JLabel(RunnerMessages.getInstance().GenericFilterEditDlg_name_DateTimeFormat + ": ");
		_btnEditDataTimeFormats = CommonResources.createIconButton(CommonResources.ICON_EDIT,
									RunnerMessages.getInstance().GenericFilterEditDlg_btn_EditDateTimeFormat + "...");
		_stcDateTimeFormats = new JTextField();
		_stcDateTimeFormats.setEditable(false);
		
		refreshDateTimeFormatPane();
		
		// create panel
		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createBevelBorder(BevelBorder.LOWERED),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		
		// setup layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- label
		pnl.add(lbl, gbc);
		gbc.gridx++;
		//--- text
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(_stcDateTimeFormats, gbc);
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx++;
		//--- edit button
		gbc.insets = new Insets(0, 2, 0, 0);
		pnl.add(_btnEditDataTimeFormats, gbc);
		
		return pnl;
	}
	
	protected JButton createToolBarButton(String command, Icon icon, String tooltip) {
		ToolBarButton btn = new ToolBarButton(icon);
		btn.setActionCommand(command);
		btn.setToolTipText(tooltip);
		btn.setRequestFocusEnabled(false);
		return btn;
	}

	/**
	 * 関連するコンポーネントとともに、入力スキーマ用ツリーパネルを生成する。
	 * @param editModel	編集モデル
	 * @return	生成されたパネル
	 */
	protected JPanel createInputSchemaTreePane(GenericFilterEditModel editModel) {
		// create buttons
		_btnInputTableToOutput = createToolBarButton(BTNCMD_INPUT_TABLE_TO_OUTPUT, CommonResources.ICON_ARROW_RIGHT,
									RunnerMessages.getInstance().GenericFilterEditDlg_btn_AddToOutput);
		_btnInputTableNew    = createToolBarButton(BTNCMD_INPUT_TABLE_NEW, CommonResources.ICON_DOCUMENT_NEW,
									RunnerMessages.getInstance().GenericFilterEditDlg_btn_AddInputTable + "...");
		_btnInputTableEdit   = createToolBarButton(BTNCMD_INPUT_TABLE_EDIT, CommonResources.ICON_DOCUMENT_EDIT,
									RunnerMessages.getInstance().GenericFilterEditDlg_btn_EditInputTable + "...");
		_btnInputTableDelete = createToolBarButton(BTNCMD_INPUT_TABLE_DELETE, CommonResources.ICON_DOCUMENT_DELETE,
									RunnerMessages.getInstance().GenericFilterEditDlg_btn_DelInputTable);
		
		// create label
		JLabel lblTitle = new JLabel(RunnerMessages.getInstance().GenericFilterEditDlg_name_InputSchema);
		lblTitle.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		// create toolbar
		JToolBar tbar = new JToolBar(JToolBar.HORIZONTAL);
		tbar.setFloatable(false);
		tbar.add(Box.createGlue());
		tbar.add(_btnInputTableNew);
		tbar.add(_btnInputTableEdit);
		tbar.add(_btnInputTableDelete);
		tbar.add(_btnInputTableToOutput);
		_tbarInputTableFunctions = tbar;
		
		// ツリーコンポーネントの生成
		_treeInput = createInputSchemaTreeComponent(editModel.getInputTableSchemaTreeModel());
		JScrollPane scTree = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scTree.setViewportView(_treeInput);
		
		// パネル
		JPanel innerPanel = new JPanel(new BorderLayout());
		innerPanel.add(tbar, BorderLayout.NORTH);
		innerPanel.add(scTree, BorderLayout.CENTER);
		JPanel pnl = new JPanel(new BorderLayout());
		pnl.add(lblTitle, BorderLayout.NORTH);
		pnl.add(innerPanel, BorderLayout.CENTER);
		
		return pnl;
	}

	/**
	 * 関連するコンポーネントともに、出力スキーマ用ツリーパネルを生成する。
	 * @param editModel	編集モデル
	 * @return	生成されたパネル
	 */
	protected JPanel createOutputSchemaTreePane(GenericFilterEditModel editModel) {
		// create buttons
		_btnOutputTableNew    = createToolBarButton(BTNCMD_OUTPUT_TABLE_NEW, CommonResources.ICON_DOCUMENT_NEW,
									RunnerMessages.getInstance().GenericFilterEditDlg_btn_AddOutputTable + "...");
		_btnOutputTableEdit   = createToolBarButton(BTNCMD_OUTPUT_TABLE_EDIT, CommonResources.ICON_DOCUMENT_EDIT,
									RunnerMessages.getInstance().GenericFilterEditDlg_btn_EditOutputTable + "...");
		_btnOutputTableDelete = createToolBarButton(BTNCMD_OUTPUT_TABLE_DELETE, CommonResources.ICON_DOCUMENT_DELETE,
									RunnerMessages.getInstance().GenericFilterEditDlg_btn_DelOutputTable);
		_btnOutputFieldDelete   = createToolBarButton(BTNCMD_OUTPUT_FIELD_DELETE, CommonResources.ICON_DELETE,
									RunnerMessages.getInstance().GenericFilterEditDlg_btn_DelOutputField);
		_btnOutputFieldMoveUp   = createToolBarButton(BTNCMD_OUTPUT_FIELD_MOVEUP, CommonResources.ICON_ARROW_UP,
									RunnerMessages.getInstance().GenericFilterEditDlg_btn_MoveUpField);
		_btnOutputFieldMoveDown = createToolBarButton(BTNCMD_OUTPUT_FIELD_MOVEDOWN, CommonResources.ICON_ARROW_DOWN,
									RunnerMessages.getInstance().GenericFilterEditDlg_btn_MoveDownField);
		
		// create title label
		JLabel lblTitle = new JLabel(RunnerMessages.getInstance().GenericFilterEditDlg_name_OutputSchema);
		lblTitle.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
		// create tool bar
		JToolBar tbar = new JToolBar(JToolBar.HORIZONTAL);
		tbar.setFloatable(false);
		tbar.add(Box.createGlue());
		tbar.add(_btnOutputTableNew);
		tbar.add(_btnOutputTableEdit);
		tbar.add(_btnOutputTableDelete);
		tbar.add(_btnOutputFieldDelete);
		tbar.add(_btnOutputFieldMoveUp);
		tbar.add(_btnOutputFieldMoveDown);
		_tbarOutputTableFunctions = tbar;
		
		// ツリーコンポーネントの生成
		_treeOutput = createOutputSchemaTreeComponent(editModel.getOutputTableSchemaTreeModel());
		JScrollPane scTree = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scTree.setViewportView(_treeOutput);
		
		// パネル
		JPanel innerPanel = new JPanel(new BorderLayout());
		innerPanel.add(tbar, BorderLayout.NORTH);
		innerPanel.add(scTree, BorderLayout.CENTER);
		JPanel pnl = new JPanel(new BorderLayout());
		pnl.add(lblTitle, BorderLayout.NORTH);
		pnl.add(innerPanel, BorderLayout.CENTER);
		
		return pnl;
	}
	
	protected GenericSchemaElementTree createInputSchemaTreeComponent(TreeModel treeModel) {
		GenericSchemaElementTree tree = new GenericSchemaElementTree(treeModel);
		tree.setEditable(false);	// 編集不可
		tree.setRootVisible(false);		// ルート非表示
		tree.setShowsRootHandles(true);	// ハンドル表示
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);	// 単一選択
		return tree;
	}
	
	protected GenericSchemaElementTree createOutputSchemaTreeComponent(TreeModel treeModel) {
		GenericSchemaElementTree tree = new GenericSchemaElementTree(treeModel);
		tree.setEditable(false);	// 編集不可
		tree.setRootVisible(false);		// ルート非表示
		tree.setShowsRootHandles(true);	// ハンドル表示
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);	// 単一選択
		return tree;
	}
	
	protected GenericExpressionSchemaTablePane createExpressionSchemaTablePane(GenericExpressionSchemaTableModel model) {
		GenericExpressionSchemaTablePane pane = new GenericExpressionSchemaTablePane();
		pane.initialComponent(_editModel, model);
		return pane;
	}
	
	protected GenericJoinCondSchemaTablePane createJoinCondSchemaTablePane(GenericJoinCondSchemaTableModel model) {
		GenericJoinCondSchemaTablePane pane = new GenericJoinCondSchemaTablePane();
		pane.initialComponent(_editModel, model);
		return pane;
	}
	
	protected JTabbedPane createConditionTablesTab() {
		JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		pane.addTab(_tableExp.getTitle()  , _tableExp);
		pane.addTab(_tableJoin.getTitle() , _tableJoin);
		return pane;
	}
	
	protected void setupPropertyLayoutPanel() {
		_propPanel.add(_panePropExp    , PROPPANEKEY_Exp);
		_propPanel.add(_panePropJoin   , PROPPANEKEY_Join);
	}
	
	protected void setupActions() {
		// Tab
		_tabConditions.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				onConditionTabSelectionChanged(e);
			}
		});
		
		// DateTimeFormats Edit Button
		_btnEditDataTimeFormats.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onClickedDateTimeFormatEditButton(ae);
			}
		});
		
		// buttons
		ActionListener btnListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onCommandButtonClicked(ae);
			}
		};
		_btnInputTableToOutput.addActionListener(btnListener);
		_btnInputTableToOutput.setEnabled(false);
		_btnInputTableNew.addActionListener(btnListener);
		_btnInputTableEdit.addActionListener(btnListener);
		_btnInputTableEdit.setEnabled(false);
		_btnInputTableDelete.addActionListener(btnListener);
		_btnInputTableDelete.setEnabled(false);
		_btnOutputTableNew.addActionListener(btnListener);
		_btnOutputTableEdit.addActionListener(btnListener);
		_btnOutputTableEdit.setEnabled(false);
		_btnOutputTableDelete.addActionListener(btnListener);
		_btnOutputTableDelete.setEnabled(false);
		_btnOutputFieldDelete.addActionListener(btnListener);
		_btnOutputFieldDelete.setEnabled(false);
		_btnOutputFieldMoveUp.addActionListener(btnListener);
		_btnOutputFieldMoveUp.setEnabled(false);
		_btnOutputFieldMoveDown.addActionListener(btnListener);
		_btnOutputFieldMoveDown.setEnabled(false);
		
		// buttons on Expression table pane
		_tableExp.getAddToOutputButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onClickedExpressionToOutputButton(ae);
			}
		});
		
		// tree actions
		//--- input
		_treeInput.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent tse) {
				onInputSchemaTreeSelectionChanged(tse);
			}
		});
		//--- output
		_treeOutput.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent tse) {
				onOutputSchemaTreeSelectionChanged(tse);
			}
		});
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static protected enum TreeModelChangeTypes {
		TreeStructureChanged,
		TreeNodesInserted,
		TreeNodesRemoved,
		TreeNodesChanged;
	}
}
