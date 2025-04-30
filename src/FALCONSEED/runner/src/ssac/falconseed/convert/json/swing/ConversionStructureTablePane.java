/*
 * @(#)ConversionStructureTablePane.java	3.4.0	2020/03/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.convert.json.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import ssac.falconseed.runner.RunnerMessages;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * JSON-CSV 変換における、変換構造テーブル・パネル。
 * 
 * @version 3.4.0
 * @since 3.4.0
 */
public abstract class ConversionStructureTablePane<E extends IConversionStructureItem> extends JScrollPane
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	static public final int	EMPTY_ROWHEADER_WIDTH	= 20;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected ConversionStructureTable	_table;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ConversionStructureTablePane(AbConversionStructureTableModel<E> model) {
		super(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		_table = new ConversionStructureTable(model);
	}
	
	public void initialComponent() {
		setupTableStyles();
		setupScrollPaneContent();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public AbConversionStructureTableModel<E> getTableModel() {
		return (AbConversionStructureTableModel<E>)_table.getModel();
	}
	
	public ConversionStructureTable getTableComponent() {
		return _table;
	}
	
	public int getRowCount() {
		return _table.getRowCount();
	}
	
	public void clearSelection() {
		_table.clearSelection();
	}
	
	public boolean isSelectionEmpty() {
		return (_table.getSelectedRowCount() == 0);
	}
	
	public int getSelectedRowCount() {
		return _table.getSelectedRowCount();
	}
	
	public void updateTableRowHeaderWidth() {
		int curWidth = _table.getTableRowHeader().getFixedCellWidth();
		if (_table.getRowCount() > 0) {
			// 行あり
			if (curWidth > 0) {
				// 行ヘッダーの幅が固定の場合は、自動調整に変更
				_table.getTableRowHeader().setFixedCellWidth(-1);
			}
		}
		else {
			// 行なし
			if (curWidth < 0) {
				// 行ヘッダーの幅が自動調整の場合は、固定幅に変更
				_table.getTableRowHeader().setFixedCellWidth(EMPTY_ROWHEADER_WIDTH);
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void setupTableStyles() {
		_table.setRowSelectionAllowed(true);
		_table.setColumnSelectionAllowed(true);
		_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_table.getTableHeader().setReorderingAllowed(false);	// 列入れ替え禁止
		_table.getTableHeader().setResizingAllowed(true);		// 列サイズ変更可能
		_table.setupVisibleGrid();								// グリッド表示(Mac対策)
	}
	
	protected void setupScrollPaneContent() {
		setViewportView(_table);
		setRowHeaderView(_table.getTableRowHeader());
		JPanel pnl = SpreadSheetTable.createUpperLeftCornerComponent();
		pnl.setLayout(new BorderLayout());
//		JLabel lbl = new JLabel(RunnerMessages.getInstance().ConversionStructureTableColumn_number);
//		Dimension dm = lbl.getMinimumSize();
//		dm.width = EMPTY_ROWHEADER_WIDTH;
//		pnl.add(lbl, BorderLayout.CENTER);
		setCorner(JScrollPane.UPPER_LEFT_CORNER, pnl);
		_table.getTableRowHeader().setFixedCellWidth(EMPTY_ROWHEADER_WIDTH);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
