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
 * @(#)GenericOperationSchemaPropertyPane.java	3.2.1	2015/07/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericOperationSchemaPropertyPane.java	3.2.0	2015/06/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.exp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.text.JTextComponent;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.fs.module.schema.SchemaElementValue;
import ssac.aadl.fs.module.schema.SchemaLiteralValue;
import ssac.aadl.fs.module.schema.type.SchemaValueType;
import ssac.falconseed.filter.generic.gui.ComboBoxOperandGroupItem;
import ssac.falconseed.filter.generic.gui.GenericFilterEditModel;
import ssac.falconseed.filter.generic.gui.GenericSchemaValueGroups;
import ssac.falconseed.filter.generic.gui.SchemaValueTypeComboBoxModel;
import ssac.falconseed.filter.generic.gui.table.ComboBoxInputSelectionModel;
import ssac.falconseed.filter.generic.gui.table.InputCsvFieldSchemaEditModel;
import ssac.falconseed.filter.generic.gui.util.GenericFilterSwingTools;
import ssac.falconseed.filter.generic.gui.util.GenericSchemaComboBox;
import ssac.falconseed.runner.RunnerMessages;

/**
 * 汎用フィルタの式や条件等の定義編集用パネル。
 * 
 * @version 3.2.1
 * @since 3.2.0
 */
public abstract class GenericOperationSchemaPropertyPane extends JPanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	static public final int	INDEX_LEFT	= 0;
	static public final int	INDEX_RIGHT	= 1; 

	static protected final String	BTNCMD_EXP_ADD		= "exp.add";
	static protected final String	BTNCMD_EXP_UPDATE	= "exp.update";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 初期化完了フラグ **/
	private boolean	_initialized;

	/** 汎用フィルタ編集モデル **/
	protected GenericFilterEditModel	_editModel;

	/** タイトル **/
	private JLabel		_lblTitle;
	/** ツールバー **/
	private JToolBar	_toolbar;
	
	protected EmptyComboBoxModel	_emptyComboModel = new EmptyComboBoxModel();

	/** 関係演算子を選択するコンボボックス **/
	private JComboBox	_cmbOperator;
	/** 値の種類 **/
	private JComboBox[]	_cmbOperandGroup;
	/** 値の値 **/
	private GenericSchemaComboBox[]	_cmbOperandValue;
	/** 値の詳細値 **/
	private GenericSchemaComboBox[]	_cmbOperandDetailValue;
	/** 即値編集テキストフィールド **/
	private JTextField[]	_txtOperandLiteral;
	/** 即値用データ型コンボボックス **/
	private JComboBox[]	_cmbLiteralValueType;
//	/** 即値用データ編集コンポーネント **/
//	private JComponent[]	_pnlOperandLiteral;
	/** フィルタ定義引数用追加ボタン **/
	private JButton[]	_btnAddFilterArg;

	private JButton	_btnExpAdd;
	private JButton	_btnExpUpdate;
	
	/** 名前編集用テキストボックス **/
	private JTextField	_txtItemName;
	
	/** メインパネル **/
	private JPanel		_mainPanel;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public GenericOperationSchemaPropertyPane() {
		super(new BorderLayout());
	}

	public void initialComponent(GenericFilterEditModel editModel) {
		if (editModel == null)
			throw new NullPointerException();
		_editModel = editModel;
		
		// create components
		_txtItemName = new JTextField();
		_lblTitle = createTitleLabel();
		_toolbar  = createToolBar();
		
		// layout components
		add(_toolbar, BorderLayout.NORTH);
		
		// create components
		_cmbOperator = new JComboBox(_emptyComboModel);
		_cmbOperandGroup = new JComboBox[]{
				new JComboBox(_emptyComboModel),
				new JComboBox(_emptyComboModel),
		};
		_cmbOperandValue = new GenericSchemaComboBox[]{
				new GenericSchemaComboBox(_emptyComboModel),
				new GenericSchemaComboBox(_emptyComboModel),
		};
		_cmbOperandDetailValue = new GenericSchemaComboBox[]{
				new GenericSchemaComboBox(_emptyComboModel),
				new GenericSchemaComboBox(_emptyComboModel),
		};
		_cmbLiteralValueType = new JComboBox[]{
				new JComboBox(new SchemaValueTypeComboBoxModel()),
				new JComboBox(new SchemaValueTypeComboBoxModel()),
		};
		_txtOperandLiteral = new JTextField[]{
				new JTextField(),
				new JTextField(),
		};
//		_pnlOperandLiteral = new JComponent[]{
//				createLiteralEditPanel(_cmbLiteralValueType[INDEX_LEFT], _txtOperandLiteral[INDEX_LEFT]),
//				createLiteralEditPanel(_cmbLiteralValueType[INDEX_RIGHT], _txtOperandLiteral[INDEX_RIGHT]),
//		};
		_btnAddFilterArg = new JButton[]{
				new JButton(RunnerMessages.getInstance().GenericFilterEditDlg_btn_AddOperandFilterArg + "..."),
				new JButton(RunnerMessages.getInstance().GenericFilterEditDlg_btn_AddOperandFilterArg + "..."),
		};
//		_pnlOperandLiteral[INDEX_LEFT ].setVisible(false);
//		_pnlOperandLiteral[INDEX_RIGHT].setVisible(false);
		_cmbLiteralValueType[INDEX_LEFT ].setSelectedIndex(0);
		_cmbLiteralValueType[INDEX_RIGHT].setSelectedIndex(0);

		ActionListener lAddArg = new AddFilterArgActionListener();
		_btnAddFilterArg[INDEX_LEFT ].addActionListener(lAddArg);
		_btnAddFilterArg[INDEX_RIGHT].addActionListener(lAddArg);
		_btnAddFilterArg[INDEX_LEFT ].setVisible(false);
		_btnAddFilterArg[INDEX_RIGHT].setVisible(false);
		
		_cmbOperator.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				JComboBox cmb = (JComboBox)ie.getSource();
				onOperatorComboBoxSelectionChanged(ie, cmb);
			}
		});
		
		OperandGroupItemListener lGroup = new OperandGroupItemListener();
		OperandValueItemListener lValue = new OperandValueItemListener();
		LiteralValueTypeItemListener lType = new LiteralValueTypeItemListener();
		_cmbOperandGroup[INDEX_LEFT ].addItemListener(lGroup);
		_cmbOperandGroup[INDEX_RIGHT].addItemListener(lGroup);
		_cmbOperandValue[INDEX_LEFT ].addItemListener(lValue);
		_cmbOperandValue[INDEX_RIGHT].addItemListener(lValue);
		_cmbLiteralValueType[INDEX_LEFT ].addItemListener(lType);
		_cmbLiteralValueType[INDEX_RIGHT].addItemListener(lType);

		JComboBox cmb = new JComboBox(new Object[]{" == "});
		int minWidth = cmb.getPreferredSize().width;
		_cmbOperator.setMinimumSize(new Dimension(minWidth, _cmbOperator.getMinimumSize().height));
		_cmbOperator.setPreferredSize(new Dimension(minWidth, _cmbOperator.getPreferredSize().height));
		
		// main panel
		_mainPanel = createMainPanel();
		add(_mainPanel, BorderLayout.CENTER);
		
		// title
		_lblTitle.setText(getPropertyTitle());
		
		// 参照モード対応
		if (!_editModel.isEditing()) {
			_btnAddFilterArg[INDEX_LEFT ].setEnabled(false);
			_btnAddFilterArg[INDEX_RIGHT].setEnabled(false);
			disableTextComponent(_txtItemName);
			disableTextComponent(_txtOperandLiteral[INDEX_LEFT ]);
			disableTextComponent(_txtOperandLiteral[INDEX_RIGHT]);
			disableComboBox(_cmbOperator);
			disableComboBox(_cmbOperandGroup[INDEX_LEFT ]);
			disableComboBox(_cmbOperandGroup[INDEX_RIGHT]);
			disableComboBox(_cmbOperandValue[INDEX_LEFT ]);
			disableComboBox(_cmbOperandValue[INDEX_RIGHT]);
			disableComboBox(_cmbOperandDetailValue[INDEX_LEFT ]);
			disableComboBox(_cmbOperandDetailValue[INDEX_RIGHT]);
			disableComboBox(_cmbLiteralValueType[INDEX_LEFT ]);
			disableComboBox(_cmbLiteralValueType[INDEX_RIGHT]);
		}
		
		// accomplished
		_initialized = true;
	}
	
	protected void disableTextComponent(JTextComponent txtcmp) {
		txtcmp.setEnabled(false);
		txtcmp.setDisabledTextColor(UIManager.getColor("Label.foreground"));
	}
	
	protected void disableComboBox(JComboBox cmb) {
		cmb.setEnabled(false);
		cmb.setEditable(true);
		JTextField editor = (JTextField)cmb.getEditor().getEditorComponent();
		editor.setDisabledTextColor(UIManager.getColor("Label.foreground"));
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getTitle() {
		ensureInitialized();
		return _lblTitle.getText();
	}
	
	public void setTitle(String newTitle) {
		ensureInitialized();
		_lblTitle.setText(newTitle);
	}
	
	public JToolBar getToolBar() {
		return _toolbar;
	}
	
	public void refreshComboBox() {
		refreshComboBoxModels(INDEX_LEFT );
		refreshComboBoxModels(INDEX_RIGHT);
	}
	
	public JPanel getMainPanel() {
		return _mainPanel;
	}
	
	public JButton getToolBarAddButton() {
		return _btnExpAdd;
	}
	
	public JButton getToolBarUpdateButton() {
		return _btnExpUpdate;
	}
	
	public JTextField getItemNametextField() {
		return _txtItemName;
	}
	
	public JComboBox getOperatorComboBox() {
		return _cmbOperator;
	}
	
	public JComboBox getOperandGroupComboBox(int index) {
		return _cmbOperandGroup[index];
	}
	
	public JComboBox getOperandValueComboBox(int index) {
		return _cmbOperandValue[index];
	}
	
	public JComboBox getOperandDetailValueComboBox(int index) {
		return _cmbOperandDetailValue[index];
	}
	
	public JComboBox getLiteralValueTypeComboBox(int index) {
		return _cmbLiteralValueType[index];
	}
	
	public JTextField getLiteralNameTextField(int index) {
		return _txtOperandLiteral[index];
	}
	
	public JButton getAddFilterArgButton(int index) {
		return _btnAddFilterArg[index];
	}
	
	public String getItemName() {
		return _txtItemName.getText();
	}
	
	public void setItemName(String text) {
		_txtItemName.setText(text);
	}
	
	public String getOperator() {
		return (String)_cmbOperator.getSelectedItem();
	}
	
	public void setOperator(String operator) {
		_cmbOperator.setSelectedItem(operator);
	}
	
	public SchemaElementValue getOperandValue(int index) {
		ComboBoxOperandGroupItem grpitem = (ComboBoxOperandGroupItem)_cmbOperandGroup[index].getSelectedItem();
		if (grpitem == null)
			return null;
		
		if (grpitem.getValueGroup() == GenericSchemaValueGroups.INPUT) {
			// 入力フィールド選択
			return (SchemaElementValue)_cmbOperandDetailValue[index].getSelectedItem();
		}
		else if (grpitem.getValueGroup() == GenericSchemaValueGroups.LITERAL) {
			// 即値
			String strText = _txtOperandLiteral[index].getText();
			SchemaValueType literalType = (SchemaValueType)_cmbLiteralValueType[index].getSelectedItem();
			SchemaLiteralValue slv = new SchemaLiteralValue(literalType, (literalType==null ? null : strText));
			slv.isValidSpecifiedValue();	// 実際の値を更新（比較のため）
			return slv;
		}
		else {
			// 選択値
			return (SchemaElementValue)_cmbOperandValue[index].getSelectedItem();
		}
	}
	
	public void setOperandValue(int index, SchemaElementValue value) {
		int groupIndex = getOperandGroupIndexByOperandValue(value);
		_cmbOperandGroup[index].setSelectedIndex(groupIndex);
		if (value instanceof SchemaLiteralValue) {
			// 即値
			SchemaLiteralValue literal = (SchemaLiteralValue)value;
			_cmbLiteralValueType[index].setSelectedItem(literal.getValueType());
			_txtOperandLiteral[index].setText(literal.getSpecifiedText());
		}
		else if (value instanceof InputCsvFieldSchemaEditModel) {
			// 入力フィールド値
			((ComboBoxInputSelectionModel)_cmbOperandValue[index].getModel()).setSelectedItem(value);
		}
		else {
			// その他の選択値
			_cmbOperandValue[index].setSelectedItem(value);
		}
	}

	//------------------------------------------------------------
	// Event handlers
	//------------------------------------------------------------
	
	protected void onOperatorComboBoxSelectionChanged(ItemEvent ie, JComboBox cmb) {
		// place holder
	}
	
	protected void onOperandGroupComboBoxSelectionChanged(ItemEvent ie, JComboBox cmb, int index) {
		refreshComboBoxModels(index);
	}
	
	protected void onOperandValueComboBoxSelectionChanged(ItemEvent ie, JComboBox cmb, int index) {
		// place holder
	}
	
	protected void onLiteralValueTypeComboBoxSelectionChanged(ItemEvent ie, JComboBox cmb, int index) {
		// place holder
	}
	
	protected void onClickedAddFilterArgButton(ActionEvent ae, JButton btn, int index) {
		// place holder
	}

	protected abstract String getPropertyTitle();
	
	protected abstract String getOperationCaption();
	
	protected abstract void onToolAddButtonClicked(ActionEvent ae);
	
	protected abstract void onToolUpdateButtonClicked(ActionEvent ae);
	
	protected void refreshComboBoxModels(int index) {
		ComboBoxOperandGroupItem item = (ComboBoxOperandGroupItem)_cmbOperandGroup[index].getSelectedItem();
		if (item == null) {
//			_pnlOperandLiteral[index].setVisible(false);
			_cmbOperandValue[index].setModel(_emptyComboModel);
			_cmbOperandValue[index].setVisible(true);
			_cmbOperandDetailValue[index].setModel(_emptyComboModel);
			_cmbOperandDetailValue[index].setVisible(false);
			_cmbLiteralValueType[index].setVisible(false);
			_txtOperandLiteral[index].setVisible(false);
			_btnAddFilterArg[index].setVisible(false);
		}
		else {
			ComboBoxModel model = item.getComboBoxModel();
			if (model == null) {
				// literal group
				_cmbOperandValue[index].setModel(_emptyComboModel);
				_cmbOperandValue[index].setVisible(false);
				_cmbOperandDetailValue[index].setModel(_emptyComboModel);
				_cmbOperandDetailValue[index].setVisible(false);
				_cmbLiteralValueType[index].setVisible(true);
				_txtOperandLiteral[index].setVisible(true);
				_txtOperandLiteral[index].setEnabled(_editModel.isEditing());
				_btnAddFilterArg[index].setVisible(false);
			}
			else if (item.getValueGroup() == GenericSchemaValueGroups.INPUT) {
				// input field group
				_cmbOperandValue[index].setModel(model);
				_cmbOperandValue[index].setVisible(true);
				_cmbOperandDetailValue[index].setModel(((ComboBoxInputSelectionModel)model).getFieldComboBoxModel());
				_cmbOperandDetailValue[index].setVisible(true);
				_cmbLiteralValueType[index].setVisible(false);
				_txtOperandLiteral[index].setVisible(false);
				_btnAddFilterArg[index].setVisible(false);
			}
			else if (item.getValueGroup() == GenericSchemaValueGroups.EXP) {
				// expression group
				_cmbOperandValue[index].setModel(model);
				_cmbOperandValue[index].setVisible(true);
				_cmbOperandDetailValue[index].setModel(_emptyComboModel);
				_cmbOperandDetailValue[index].setVisible(false);
				_cmbLiteralValueType[index].setVisible(false);
				_txtOperandLiteral[index].setVisible(false);
				_btnAddFilterArg[index].setVisible(false);
			}
			else if (item.getValueGroup() == GenericSchemaValueGroups.F_ARG) {
				// filter argument group
				_cmbOperandValue[index].setModel(model);
				_cmbOperandValue[index].setVisible(true);
				_cmbOperandDetailValue[index].setModel(_emptyComboModel);
				_cmbOperandDetailValue[index].setVisible(false);
				_cmbLiteralValueType[index].setVisible(false);
				_txtOperandLiteral[index].setVisible(false);
				_btnAddFilterArg[index].setVisible(_editModel.isEditing());
			}
			else {
				// unknown group type
				_cmbOperandValue[index].setModel(_emptyComboModel);
				_cmbOperandValue[index].setVisible(true);
				_cmbOperandDetailValue[index].setModel(_emptyComboModel);
				_cmbOperandDetailValue[index].setVisible(false);
				_cmbLiteralValueType[index].setVisible(false);
				_txtOperandLiteral[index].setVisible(false);
				_btnAddFilterArg[index].setVisible(false);
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected abstract int getOperandGroupIndexByOperandValue(SchemaElementValue value);
	
	protected JComponent createLiteralEditPanel(JComboBox cmbDataTypes, JTextField txtField) {
		JPanel pnl = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 2);
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- combobox
		pnl.add(cmbDataTypes, gbc);
		gbc.gridx++;
		//--- text field
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pnl.add(txtField, gbc);
		
		return pnl;
	}
	
	protected void ensureInitialized() {
		if (!_initialized) {
			throw new IllegalStateException("Components are not initialized.");
		}
	}
	
	protected JLabel createTitleLabel() {
		JLabel lbl = new JLabel();
		return lbl;
	}
	
	protected JToolBar createToolBar() {
		JToolBar tbar = new JToolBar();
		tbar.setFloatable(false);	// 移動禁止
		
		tbar.add(_lblTitle);
		tbar.add(Box.createGlue());
		
		tbar.setBorderPainted(true);
		tbar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		_btnExpAdd    = GenericFilterSwingTools.createToolBarButton(BTNCMD_EXP_ADD, CommonResources.ICON_ADD, RunnerMessages.getInstance().GenericInputSchemaEditDlg_btn_AddField);
		_btnExpUpdate = GenericFilterSwingTools.createToolBarButton(BTNCMD_EXP_UPDATE, CommonResources.ICON_REFRESH, CommonMessages.getInstance().Button_Update);
		_btnExpUpdate.setEnabled(false);
		
		tbar.add(_btnExpAdd);
		tbar.add(_btnExpUpdate);
		
		_btnExpAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onToolAddButtonClicked(ae);
			}
		});
		_btnExpUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				onToolUpdateButtonClicked(ae);
			}
		});
		
		return tbar;
	}
	
	protected JPanel createMainPanel() {
		// create labels
		JLabel lblName   = new JLabel(RunnerMessages.getInstance().GenericFilterEditDlg_name_Name + " : ");
		JLabel lblExp    = new JLabel(getOperationCaption() + " : ");
		JLabel lblLValue = new JLabel(RunnerMessages.getInstance().GenericFilterEditDlg_name_Operand1);
		JLabel lblRValue = new JLabel(RunnerMessages.getInstance().GenericFilterEditDlg_name_Operand2);
		
		// create main panel
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// layout on GridBag
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill   = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		//--- Name
		panel.add(lblName, gbc);
		gbc.gridx++;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		panel.add(_txtItemName, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		//--- labels
		gbc.insets = new Insets(5,0,0,0);
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.gridx = 0;
		panel.add(new JLabel(" "), gbc);	// dummy
		gbc.gridx++;
		panel.add(lblLValue, gbc);
		gbc.gridx++;
		panel.add(new JLabel(" "), gbc);	// dummy
		gbc.gridx++;
		panel.add(lblRValue, gbc);
		gbc.gridy++;
		//--- groups
		gbc.gridx = 0;
		gbc.insets = new Insets(0,0,0,0);
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(lblExp, gbc);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(_cmbOperandGroup[INDEX_LEFT], gbc);
		gbc.gridx++;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(new JLabel(" "), gbc);	// dummy
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(_cmbOperandGroup[INDEX_RIGHT], gbc);
		gbc.gridy++;
		//--- value
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(new JLabel(" "), gbc);	// dummy
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(_cmbOperandValue[INDEX_LEFT], gbc);
		panel.add(_cmbLiteralValueType[INDEX_LEFT], gbc);
		gbc.gridx++;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 3, 0, 3);
		panel.add(_cmbOperator, gbc);
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(_cmbOperandValue[INDEX_RIGHT], gbc);
		panel.add(_cmbLiteralValueType[INDEX_RIGHT], gbc);
		gbc.gridy++;
		//--- detail
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(new JLabel(" "), gbc);	// dummy
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(_cmbOperandDetailValue[INDEX_LEFT], gbc);
		panel.add(_txtOperandLiteral[INDEX_LEFT], gbc);
		panel.add(_btnAddFilterArg[INDEX_LEFT], gbc);
		gbc.gridx++;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(new JLabel(" "), gbc);	// dummy
		gbc.gridx++;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(_cmbOperandDetailValue[INDEX_RIGHT], gbc);
		panel.add(_txtOperandLiteral[INDEX_RIGHT], gbc);
		panel.add(_btnAddFilterArg[INDEX_RIGHT], gbc);
		
		
		return panel;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class OperandGroupItemListener implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent ie) {
			JComboBox cmb = (JComboBox)ie.getSource();
			int index;
			if (cmb == _cmbOperandGroup[INDEX_LEFT])
				index = INDEX_LEFT;
			else if (cmb == _cmbOperandGroup[INDEX_RIGHT])
				index = INDEX_RIGHT;
			else
				index = -1;
			onOperandGroupComboBoxSelectionChanged(ie, cmb, index);
		}
	}
	
	protected class OperandValueItemListener implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent ie) {
			JComboBox cmb = (JComboBox)ie.getSource();
			int index;
			if (cmb == _cmbOperandValue[INDEX_LEFT])
				index = INDEX_LEFT;
			else if (cmb == _cmbOperandValue[INDEX_RIGHT])
				index = INDEX_RIGHT;
			else
				index = -1;
			onOperandValueComboBoxSelectionChanged(ie, cmb, index);
		}
	}
	
	protected class LiteralValueTypeItemListener implements ItemListener
	{
		@Override
		public void itemStateChanged(ItemEvent ie) {
			JComboBox cmb = (JComboBox)ie.getSource();
			int index;
			if (cmb == _cmbLiteralValueType[INDEX_LEFT])
				index = INDEX_LEFT;
			else if (cmb == _cmbLiteralValueType[INDEX_RIGHT])
				index = INDEX_RIGHT;
			else
				index = -1;
			onLiteralValueTypeComboBoxSelectionChanged(ie, cmb, index);
		}
	}
	
	protected class AddFilterArgActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent ae) {
			JButton btn = (JButton)ae.getSource();
			int index;
			if (btn == _btnAddFilterArg[INDEX_LEFT])
				index = INDEX_LEFT;
			else if (btn == _btnAddFilterArg[INDEX_RIGHT])
				index = INDEX_RIGHT;
			else
				index = -1;
			onClickedAddFilterArgButton(ae, btn, index);
		}
	}
	
	static protected class EmptyComboBoxModel extends AbstractListModel implements ComboBoxModel
	{
		private static final long serialVersionUID = 1L;

		@Override
		public int getSize() {
			return 0;
		}

		@Override
		public Object getElementAt(int index) {
			return null;
		}

		@Override
		public void setSelectedItem(Object anItem) {
		}

		@Override
		public Object getSelectedItem() {
			return null;
		}
	}
}
