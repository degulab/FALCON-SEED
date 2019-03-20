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
 * @(#)SpreadSheetRowHeader.java	3.2.0	2015/06/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SpreadSheetRowHeader.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SpreadSheetRowHeader.java	1.10	2009/01/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.CellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import ssac.util.Validations;

/**
 * スプレッドシート・テーブルの行ヘッダーコンポーネント。
 * 
 * @version 3.2.0
 * @since 1.10
 */
public class SpreadSheetRowHeader extends JList
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -4622057631403431257L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final SpreadSheetTable		targetTable;
	
	//private final DefaultListModel		numberList;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SpreadSheetRowHeader(SpreadSheetTable table) {
		super();
		if (table == null)
			throw new NullPointerException();
		this.targetTable = Validations.validNotNull(table);
		
		// setup JList
		//--- 選択不可
		setFocusable(false);		// フォーカス無効
		setSelectionModel(new DefaultListSelectionModel(){	// 選択状態を無効にする選択モデル
			public boolean isSelectedIndex(int index) {
				return false;
			}
		});
		//--- JList のモデル設定
		//numberList = new DefaultListModel();
		//setModel(numberList);
		table.getTableRowHeaderModel().setSize(table.getRowCount());
		setModel(table.getTableRowHeaderModel());
		//--- JList の表示設定
		//updateNumberList(table.getRowCount());
		setFixedCellHeight(table.getRowHeight());
		setCellRenderer(new RowHeaderRenderer(table));

		// setup actions
		//--- table のセル選択アクション
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				onTableCellSelectionChanged(e);
			}
		});
		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				onTableCellSelectionChanged(e);
			}
		});
		//--- JList のマウスアクション
		MouseInputListener ml = new MouseInputListener();
		addMouseListener(ml);
		addMouseMotionListener(ml);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * マウスイベントに応じたツールチップテキストを取得する。
	 */
	@Override
	public String getToolTipText(MouseEvent event) {
		String tooltip = targetTable.getRowHeaderToolTipText(this, event);
		if (tooltip == null) {
			// 標準の動作
			tooltip = super.getToolTipText(event);
		}
		return tooltip;
	}

	/*---
	public void updateNumberList(int rowCount) {
		if (rowCount <= 0) {
			numberList.removeAllElements();
		}
		else if (rowCount != numberList.getSize()) {
			if (rowCount > numberList.getSize()) {
				for (int i = numberList.getSize()+1; i <= rowCount; i++) {
					numberList.addElement(i);
				}
			} else {
				numberList.removeRange(rowCount, (numberList.getSize()-1));
			}
		}
	}
	---*/
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void onTableCellSelectionChanged(ListSelectionEvent e) {
		this.repaint();
	}

	//------------------------------------------------------------
	// internal RowHeaderRenderer class
	//------------------------------------------------------------
	
	//------------------------------------------------------------------------------
	// Mac では、JLabelの描画実装のままでは、なぜか文字が表示されない。
	// なので、JLabel派生とDefaultListCellRenderer は使えない。
	// ただ、DefaultTableCellRenderer を使用すると表示される。DefaultListCellRendererと
	// DefaultTableCellRendererはどちらもJLabelの派生だが、paintに関する実装が
	// ことなると思われるが、現時点ではそこまで調査していない。
	//--------------------------------------------------------------------------------
    //class RowHeaderRenderer extends JLabel implements ListCellRenderer {
	//class RowHeaderRenderer extends DefaultListCellRenderer {
	static class RowHeaderRenderer extends DefaultTableCellRenderer implements ListCellRenderer {
		private final SpreadSheetTable srcTable;
    	public RowHeaderRenderer(SpreadSheetTable targetTable) {
    		super();
    		this.srcTable = Validations.validNotNull(targetTable);
    		JTableHeader header = targetTable.getTableHeader();
    		this.setOpaque(true);
    		//this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    		this.setBorder(BorderFactory.createMatteBorder(0,0,1,2,Color.gray.brighter()));
    		this.setHorizontalAlignment(CENTER);
    		this.setForeground(header.getForeground());
    		this.setBackground(header.getBackground());
    		this.setFont(header.getFont());
    	}
    	public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    		setOpaque(true);
    		setBorder(UIManager.getBorder("TableHeader.cellBorder"));

    		if (srcTable.isRowSelected(index)) {
    			if (srcTable.getSelectedColumnCount()==srcTable.getColumnCount()) {
    				setForeground(srcTable.getSelectionForeground());
    				setBackground(srcTable.getSelectionBackground());

    			} else {
    				setForeground(srcTable.getFocusForeground());
    				setBackground(srcTable.getFocusBackground());
    			}
    		}
    		else {
    			setForeground(srcTable.getTableHeader().getForeground());
    			setBackground(srcTable.getTableHeader().getBackground());
    		}
    		
    		setFont(srcTable.getTableHeader().getFont());
    		String tableRowName = srcTable.getRowHeaderName(index);
    		setText(tableRowName==null ? (value==null ? "" : value.toString()) : tableRowName);
    		return this;
    	}
	}

	//------------------------------------------------------------
	// internal MouseInputListener class
	//------------------------------------------------------------
	
	class MouseInputListener extends MouseInputAdapter
	{
		// Component receiving mouse events during editing.
		// May not be editorComponent.
		protected Component dispatchComponent;
		protected boolean selectedOnPress;

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			//System.err.println("SpreadSeetRowHeader.MouseInputListener#mousePressed(" + e.toString() + ")");
			if (e.isConsumed()) {
				selectedOnPress = false;
				return;
			}
			if (isResizingMouseEvent(e)) {
				selectedOnPress = false;
				return;
			}

			// default selection action
			selectedOnPress = true;
			adjustFocusAndSelection(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			//System.err.println("SpreadSeetRowHeader.MouseInputListener#mouseReleased(" + e.toString() + ")");
			if (selectedOnPress) {
				if (shouldIgnoreMouseEvent(e)) {
					return;
				}

				repostEvent(e);
				dispatchComponent = null;
				setValueIsAdjusting(false);
			}
			else if (!isResizingMouseEvent(e)) {
				// default selection action
				adjustFocusAndSelection(e);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (shouldIgnoreMouseEvent(e)) {
				return;
			}

			if (selectedOnPress) {
				mouseDraggedImpl(e);
			}
		}

		protected void setValueIsAdjusting(boolean flag) {
			targetTable.getSelectionModel().setValueIsAdjusting(flag);
			targetTable.getColumnModel().getSelectionModel().setValueIsAdjusting(flag);
		}

		protected void setDispatchComponent(MouseEvent e) {
			Component editorComponent = targetTable.getEditorComponent();
			Point p = e.getPoint();
			Point p2 = SwingUtilities.convertPoint(targetTable, p, editorComponent);
			dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent, p2.x, p2.y);
			//--- SwingUtilities2.setSkipClickCount(dispatchComponent, e.getClickCount() - 1);
			//--- この実装は、JTextComponent の場合に有効であり、public なクラスではないため、
			//--- この呼び出しを行わない。
		}

		protected boolean repostEvent(MouseEvent e) {
			// Check for isEditing() in case another event has
			// caused the editor to be removed. See bug #4306499.
			if (dispatchComponent == null || !targetTable.isEditing()) {
				return false;
			}
			MouseEvent e2 = SwingUtilities.convertMouseEvent(targetTable, e, dispatchComponent);
			dispatchComponent.dispatchEvent(e2);
			return true;
		}

		protected boolean shouldIgnoreMouseEvent(MouseEvent e) {
			return (e.isConsumed() || !SwingUtilities.isLeftMouseButton(e) || targetTable==null || !targetTable.isEnabled());
			//--- return e.isConsumed() || SwingUtilities2.shouldIgnore(e, targetTable);
			//--- SwingUtilities2 は使用しない。
		}

		protected boolean isResizingMouseEvent(MouseEvent e) {
			//return (SwingUtilities.isLeftMouseButton(e) && canResize(getResizingColumnIndex(e.getPoint())));
			//--- 行ヘッダではサイズ変更に対応しないため、常に false を返す
			return false;
		}

		protected void adjustFocusAndSelection(MouseEvent e) {
			if (shouldIgnoreMouseEvent(e)) {
				return;
			}

			if (targetTable.isEditing()) {
				CellEditor editor = targetTable.getCellEditor();
				if (editor != null) {
					//--- セル編集停止
					editor.stopCellEditing();
				}
			}

			Point p = e.getPoint();
			int targetIndex = targetTable.rowAtPoint(p);
			if (targetIndex < 0) {
				return;
			}

			//--- SwingUtilities2.adjustFocus(table)
			if (!targetTable.hasFocus() && targetTable.isRequestFocusEnabled()) {
				targetTable.requestFocus();
			}

			CellEditor editor = targetTable.getCellEditor();
			if (editor == null || editor.shouldSelectCell(e)) {
				boolean adjusting = (e.getID() == MouseEvent.MOUSE_PRESSED) ? true : false;
				setValueIsAdjusting(adjusting);
				makeSelectionChange(targetIndex, e);
			}
		}

		protected void makeSelectionChange(int targetIndex, MouseEvent e) {
			boolean ctrl = e.isControlDown();

			if (ctrl && e.isShiftDown()) {
				ListSelectionModel rm = targetTable.getSelectionModel();
				ListSelectionModel cm = targetTable.getColumnModel().getSelectionModel();
				int anchorIndex = rm.getAnchorSelectionIndex();

				boolean anchorSelected = true;
				if (anchorIndex < 0 || anchorIndex >= targetTable.getRowCount()) {
					anchorIndex = 0;
					anchorSelected = false;
				}

				if (anchorSelected && rm.isSelectedIndex(anchorIndex)) {
					rm.addSelectionInterval(anchorIndex, targetIndex);
					cm.addSelectionInterval(targetTable.getColumnCount()-1, 0);
				} else {
					rm.removeSelectionInterval(anchorIndex, targetIndex);
					if (rm.isSelectionEmpty()) {
						cm.clearSelection();
					}
					//--- clientProperty("Table.isFileList") の状態は無視
				}
			}
			else {
				targetTable.changeRowHeaderSelection(targetIndex, ctrl, (!ctrl && e.isShiftDown()));
			}
		}

		protected void mouseDraggedImpl(MouseEvent e) {
			repostEvent(e);

			//--- clientProperty("Table.isFileList") の状態は無視
			if (targetTable.isEditing()) {
				return;
			}

			Point p = e.getPoint();
			int targetIndex = targetTable.rowAtPoint(p);
			if (targetIndex < 0) {
				return;
			}

			if (e.isControlDown()) {
				ListSelectionModel rm = targetTable.getSelectionModel();
				ListSelectionModel cm = targetTable.getColumnModel().getSelectionModel();
				int anchorIndex = rm.getAnchorSelectionIndex();

				boolean anchorSelected = true;
				if (anchorIndex < 0 || anchorIndex >= targetTable.getRowCount()) {
					anchorIndex = 0;
					anchorSelected = false;
				}

				if (anchorSelected && rm.isSelectedIndex(anchorIndex)) {
					rm.addSelectionInterval(anchorIndex, targetIndex);
					cm.addSelectionInterval(targetTable.getColumnCount()-1, 0);
				} else {
					rm.removeSelectionInterval(anchorIndex, targetIndex);
					if (rm.isSelectionEmpty()) {
						cm.clearSelection();
					}
					//--- clientProperty("Table.isFileList") の状態は無視
				}

				// From JTable.changeSelection():
				// Scroll after changing the selection as blit scrolling is immediate,
				// so that if we cause the repaint after the scroll we end up painting
				// everything!
				if (targetTable.getAutoscrolls()) {
					// 行インデックス[0]と現在の列インデックスにフォーカス
					Rectangle cellRect = targetTable.getCellRect(targetIndex, 0, false);
					if (cellRect != null) {
						targetTable.scrollRectToVisible(cellRect);
					}
				}
			}
			else {
				targetTable.changeRowHeaderSelection(targetIndex, false, true);
			}
		}
	}
}
