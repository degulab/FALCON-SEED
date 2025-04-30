/*
 * @(#)DTCContentDtalgeEditView.java	1.1.0	2023/01/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import dtalge.container.editor.content.common.swing.DtContainerContentDetailView;
import ssac.aadl.data.dtalge.DtalgeDataTypes;
import ssac.util.logging.AppLogger;

/**
 * データ代数元を編集するテーブルを配置したスクロール可能なビュー。
 * 
 * @version 1.1.0
 * @since 1.1.0
 */
public class DTCContentDtalgeEditView extends AbDTCContentAlgeEditView
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	static protected final DTCContentDtalgeEditModel	EMPTY_TABLE_MODEL = new DTCContentDtalgeEditModel();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DTCContentDtalgeEditView(DtContainerContentDetailView parentContainer) {
		super(parentContainer);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected AbDTCContentAlgeEditModel getEmptyEditModel() {
		return EMPTY_TABLE_MODEL;
	}
	
	/**
	 * テーブルコンポーネントを生成し、テーブルモデル設定前の初期化を行う。
	 * @return	生成されたテーブルコンポーネント
	 */
	@Override
	protected DTCContentDtalgeEditTablePane createEditTableComponent() {
		DTCContentDtalgeEditTablePane table = new DTCContentDtalgeEditTablePane();
		//table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//table.setColumnSelectionAllowed(true);
		//table.setRowSelectionAllowed(true);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//table.getTableHeader().setReorderingAllowed(false);
		//table.getTableHeader().setResizingAllowed(true);
		//table.setShowGrid(true);
		//table.setGridColor(new Color(128,128,128));
		//table.getTableRowHeader().setFixedCellWidth(50);
		
		return table;
	}

	static protected void setupDTCContentDtalgeDataTypeComboBoxItems(JComboBox<Object> cb) {
		cb.removeAllItems();
		cb.addItem(DtalgeDataTypes.STRING);
		cb.addItem(DtalgeDataTypes.DECIMAL);
		cb.addItem(DtalgeDataTypes.BOOLEAN);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * データ代数元編集用テーブルコンポーネントの、データ型キー専用コンボボックスのためのレンダラー。
	 * 
	 * @version 1.1.0
	 * @since 1.1.0
	 */
	static protected class DTCContentDtalgeDataTypeCellRenderer extends JComboBox<Object> implements TableCellRenderer
	{
		private static final long serialVersionUID = 1L;
		
		private final JTextField comboEditor;
		
		public DTCContentDtalgeDataTypeCellRenderer() {
			super();
			setEditable(true);
			setBorder(BorderFactory.createEmptyBorder());
			comboEditor = (JTextField)getEditor().getEditorComponent();
			comboEditor.setBorder(BorderFactory.createEmptyBorder());
			comboEditor.setOpaque(true);
			adjustFixedSize();
		}
		
		public void adjustFixedSize() {
			addItem("");
			setSize(this.getPreferredSize());
			Dimension cbdm = getSize();
			Dimension tfdm = comboEditor.getPreferredSize();
			int width = cbdm.width - tfdm.width;
			setupDTCContentDtalgeDataTypeComboBoxItems(this);
			int maxWidth = 0;
			for (int i = 0; i < getItemCount(); i++) {
				Object item = getItemAt(i);
				String str = (item==null ? "" : item.toString());
				FontMetrics fm = comboEditor.getFontMetrics(comboEditor.getFont());
				int sw = fm.stringWidth(str);
				maxWidth = Math.max(sw, maxWidth);
			}
			int cbWidth = maxWidth + width + 5;
			removeAllItems();
			//--- setup minimum size
			cbdm = getMinimumSize();
			cbdm.setSize(cbWidth, cbdm.height);
			setMinimumSize(cbdm);
			//--- setup maximum size
			cbdm = getMaximumSize();
			cbdm.setSize(cbWidth, cbdm.height);
			setMaximumSize(cbdm);
			//--- setup Preferred size
			cbdm = getPreferredSize();
			cbdm.setSize(cbWidth, cbdm.height);
			setPreferredSize(cbdm);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			removeAllItems();
			if (isSelected) {
				comboEditor.setForeground(table.getSelectionForeground());
				comboEditor.setBackground(table.getSelectionBackground());
			} else {
				comboEditor.setForeground(table.getForeground());
				comboEditor.setBackground(table.getBackground());
			}
			addItem(value==null ? "" : value.toString());
			return this;
		}
	}

	/**
	 * データ代数元編集用テーブルコンポーネント。
	 * データ代数基底のデータ型キーをコンボボックスで選択可能にする。
	 * 
	 * @version 1.1.0
	 * @since 1.1.0
	 */
	protected class DTCContentDtalgeEditTablePane extends DTCContentAlgeEditTablePane
	{
		//------------------------------------------------------------
		// Constants
		//------------------------------------------------------------

		private static final long serialVersionUID = 1L;

		//------------------------------------------------------------
		// Fields
		//------------------------------------------------------------

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		public DTCContentDtalgeEditTablePane() {
			super();
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------
		
		@Override
		public boolean editCellAt(int rowIndex, int columnIndex, EventObject event) {
			// データ型以外のカラムの編集では、エディタコンポーネントの
			// サイズを表示されている領域に限定する。
			boolean result = super.editCellAt(rowIndex, columnIndex, event);
			if (columnIndex != DTCContentDtalgeEditModel.COL_TYPE && result) {
				Rectangle cellRect = getCellRect(rowIndex, columnIndex, false);
				Rectangle editRect = getVisibleRect().intersection(cellRect);
				editorComp.setBounds(editRect);
				editorComp.validate();
			}
			return result;
		}
		
		@Override
		public void setModel(TableModel dataModel) {
			//--- setup base key cell editor
			super.setModel(dataModel);
			
			//--- setup original cell editor and renderer for Dtalge
			if (dataModel instanceof DTCContentDtalgeEditModel) {
				// setup data-type column
				TableColumn col = getColumnModel().getColumn(DTCContentDtalgeEditModel.COL_TYPE);
				//--- setup cell editor
				TableCellEditor typeCellEditor = createDtalgeDataTypeCellEditor();
				if (typeCellEditor != null) {
					col.setCellEditor(typeCellEditor);
				}
				//--- setup cell renderer
				DTCContentDtalgeDataTypeCellRenderer typeCellRenderer = createDtalgeDataTypeCellRenderer();
				if (typeCellRenderer != null) {
					col.setCellRenderer(typeCellRenderer);
					
					// setup column size
					col.setMinWidth(typeCellRenderer.getMinimumSize().width);
					col.setMaxWidth(typeCellRenderer.getMaximumSize().width);
					col.setResizable(false);
				}
			}
		}

		//------------------------------------------------------------
		// Event handlers
		//------------------------------------------------------------

		@Override
		protected void onFinishedTableEditCutAction() {
			if (AppLogger.isTraceEnabled()) {
				AppLogger.trace("called DTCContentDtalgeEditTablePane#onFinishedTableEditCutAction() in " + DTCContentDtalgeEditView.this.getClass().getName());
			}
			fireUpdateMenusByCutCopy(getFrame());
		}
		
		@Override
		protected void onFinishedTableEditCopyAction() {
			if (AppLogger.isTraceEnabled()) {
				AppLogger.trace("called DTCContentDtalgeEditTablePane#onFinishedTableEditCopyAction() in " + DTCContentDtalgeEditView.this.getClass().getName());
			}
			fireUpdateMenusByCutCopy(getFrame());
		}
		
		@Override
		protected void onFinishedTableEditPasteAction() {
			if (AppLogger.isTraceEnabled()) {
				AppLogger.trace("called DTCContentDtalgeEditTablePane#onFinishedTableEditPasteAction() in " + DTCContentDtalgeEditView.this.getClass().getName());
			}
		}
		
		@Override
		protected void onFinishedTableEditDeleteAction() {
			if (AppLogger.isTraceEnabled()) {
				AppLogger.trace("called DTCContentDtalgeEditTablePane#onFinishedTableEditDeleteAction() in " + DTCContentDtalgeEditView.this.getClass().getName());
			}
		}

		//------------------------------------------------------------
		// Internal methods
		//------------------------------------------------------------

		protected TableCellEditor createDtalgeDataTypeCellEditor() {
			JComboBox<Object> cb = new JComboBox<Object>();
			cb.setBorder(BorderFactory.createEmptyBorder());
			//--- アイテム生成
			setupDTCContentDtalgeDataTypeComboBoxItems(cb);
			return new DefaultCellEditor(cb);
		}
		
		protected DTCContentDtalgeDataTypeCellRenderer createDtalgeDataTypeCellRenderer() {
			DTCContentDtalgeDataTypeCellRenderer renderer = new DTCContentDtalgeDataTypeCellRenderer();
			return renderer;
		}
	}
}
