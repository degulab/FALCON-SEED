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
 * @(#)GenericInputCsvSchemaTable.java	3.2.2	2015/10/19 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)GenericInputCsvSchemaTable.java	3.2.0	2015/06/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import ssac.falconseed.filter.generic.gui.SchemaValueTypeComboBoxModel;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * 汎用フィルタの入力スキーマ編集ダイアログで使用する、CSV 入力スキーマ編集テーブルのコンポーネント。
 * 
 * @version 3.2.2
 * @since 3.2.0
 */
public class GenericInputCsvSchemaTable extends SpreadSheetTable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private ComboSchemaValueTypeCellRenderer	_cmbCellRenderer;
	private DefaultCellEditor[]					_cmbCellEditor;
	private int		_curEditorIndex	= 1;
	private int		_curEditorRow	= (-1);
	private int		_curEditorCol	= (-1);

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public GenericInputCsvSchemaTable() {
		super();
		localConstructor();
	}

	public GenericInputCsvSchemaTable(TableModel model) {
		super(model);
		localConstructor();
	}
	
	private void localConstructor() {
		_cmbCellRenderer = new ComboSchemaValueTypeCellRenderer();
		JComboBox cmb1 = new ComboSchemaValueTypeCellEditor(new SchemaValueTypeComboBoxModel());
		JComboBox cmb2 = new ComboSchemaValueTypeCellEditor(new SchemaValueTypeComboBoxModel());
		DefaultCellEditor editor1 = new DefaultCellEditor(cmb1);
		DefaultCellEditor editor2 = new DefaultCellEditor(cmb2);
		_cmbCellEditor   = new DefaultCellEditor[]{
				// version 3.2.2
				// コンボボックスエディタをテーブル上でワンクリックで動作させる場合、
				// 一つのインスタンスで処理させると、フォーカスゲインとフォーカスロストの処理を、
				// 同一インスタンスが処理してしまうため、一瞬だけ選択候補が表示される(Mac の場合)状態が発生。
				// そこで、インスタンスを2つ用意し、別のセルがクリックされたら異なるインスタンスを返すように変更。
				editor1,
				editor2,
		};
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement SpreadSheetTable interfaces
	//------------------------------------------------------------

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (convertRowIndexToModel(row) == GenericInputCsvSchemaTableModel.ROWIDX_VALUETYPE) {
			// データ型
			return _cmbCellRenderer;
			//return super.getCellRenderer(row, column);
		}
		else {
			// 文字列型
			return super.getCellRenderer(row, column);
		}
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		TableCellEditor editor;
		if (convertRowIndexToModel(row) == GenericInputCsvSchemaTableModel.ROWIDX_VALUETYPE) {
			// データ型
			if (_curEditorRow != row || _curEditorCol != column) {
				_curEditorRow = row;
				_curEditorCol = column;
				_curEditorIndex = (_curEditorIndex + 1) % _cmbCellEditor.length;
			}
			editor = _cmbCellEditor[_curEditorIndex];
		}
		else {
			// 文字列型
			editor = super.getCellEditor(row, column);
		}
		return editor;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	static protected class ComboSchemaValueTypeCellEditor extends JComboBox
	{
		private static final long serialVersionUID = 1L;

		public ComboSchemaValueTypeCellEditor() {
			super();
		}

		public ComboSchemaValueTypeCellEditor(ComboBoxModel aModel) {
			super(aModel);
		}

		@Override
		public void updateUI() {
			super.updateUI();
			setBorder(BorderFactory.createEmptyBorder());
			setUI(new BasicComboBoxUI() {
				@Override
				protected JButton createArrowButton() {
					JButton button = super.createArrowButton();
					button.setContentAreaFilled(false);
					button.setBorder(BorderFactory.createEmptyBorder());
					return button;
				}
			});
		}
	}
	
	static protected class ComboSchemaValueTypeCellRenderer extends JComboBox implements TableCellRenderer
	{
		private static final long serialVersionUID = 1L;

		private JTextField	_editor;
		private JButton		_button;
		private Dimension	_dmButton = new Dimension();
		
		public ComboSchemaValueTypeCellRenderer() {
			super();
			setEditable(true);
		}
		
		public Dimension getArrowButtonSize() {
			if (_button == null)
				_dmButton.setSize(0, 0);
			else
				_button.getSize(_dmButton);
			return _dmButton;
		}

		@Override
		public void updateUI() {
			super.updateUI();
			
			setBorder(BorderFactory.createEmptyBorder());
			setUI( new BasicComboBoxUI() {
				@Override
				protected JButton createArrowButton() {
					_button = super.createArrowButton();
					_button.setContentAreaFilled(false);
					_button.setBorder(BorderFactory.createEmptyBorder());
					return _button;
				}
			});
			_editor = (JTextField)getEditor().getEditorComponent();
			_editor.setBorder(BorderFactory.createEmptyBorder());
			_editor.setOpaque(true);;
			_editor.setEditable(false);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			removeAllItems();
			
			if (isSelected) {
				_editor.setForeground(table.getSelectionForeground());
				_editor.setBackground(table.getSelectionBackground());
				//_button.setBackground(table.getSelectionBackground());
			}
			else {
				_editor.setForeground(table.getForeground());
				_editor.setBackground(table.getBackground());
				//_button.setBackground(table.getBackground());
			}
			
			addItem(value==null ? "" : value.toString());
			
			return this;
		}
		
		@Override
		public boolean isOpaque() {
			Color back = getBackground();
			Component p = getParent();
			if (p != null) {
				p = p.getParent();
			}
			boolean colorMatch = (back != null && p != null && back.equals(p.getBackground()) && p.isOpaque());
			return (!colorMatch && super.isOpaque());
		}
		
		@Override
		protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
			// no entry
		}
		
		@Override
		public void repaint(long tm, int x, int y, int width, int height) {
			// no entry
		}
		
		@Override
		public void repaint(Rectangle r) {
			// no entry
		}
		
		@Override
		public void repaint() {
			// no entry
		}
		
		@Override
		public void revalidate() {
			// no entry
		}
	}
}
