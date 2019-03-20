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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)SpreadSheetColumnHeader.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SpreadSheetColumnHeader.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)SpreadSheetColumnHeader.java	1.10	2009/01/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.table;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.CellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * スプレッドシート・テーブルの列ヘッダーコンポーネント。
 * 
 * @version 1.16	2010/09/27
 * 
 * @since 1.10
 */
public class SpreadSheetColumnHeader extends JTableHeader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final SpreadSheetTable		targetTable;
	
	private boolean _enableChangeSelection = true;
	private boolean _visibleSelection = true;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public SpreadSheetColumnHeader(SpreadSheetTable table, TableColumnModel cm) {
		super(cm);
		if (table == null)
			throw new NullPointerException();
		this.targetTable = table;
		setupDefaultRenderer();
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				onTableCellSelectionChanged(e);
			}
		});
		cm.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
				onTableCellSelectionChanged(e);
			}
		});
		
		initialComponents();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * マウスクリックによる列選択が有効であれば <tt>true</tt> を返す。
	 * @see #setEnableChangeSelection(boolean)
	 * @since 1.14
	 */
	public boolean getEnableChangeSelection() {
		return _enableChangeSelection;
	}

	/**
	 * マウスクリックによる列選択を有効もしくは無効にする。
	 * <em>enable</em> に <tt>true</tt> を指定した場合、
	 * 列ヘッダのクリックでその列が全選択される。
	 * <tt>false</tt> を指定した場合は、列ヘッダをクリックしても
	 * 選択状態は変更されない。
	 * @param enable	列選択を有効にする場合は <tt>true</tt>、
	 * 					無効にする場合は <tt>false</tt> を指定する。
	 * @since 1.14
	 */
	public void setEnableChangeSelection(boolean enable) {
		_enableChangeSelection = enable;
	}

	/**
	 * セルが選択されている列ヘッダの強調表示が有効であれば <tt>true</tt> を返す。
	 * @see #setVisibleSelection(boolean)
	 * @since 1.14
	 */
	public boolean getVisibleSelection() {
		return _visibleSelection;
	}

	/**
	 * セルが選択されている列ヘッダの強調表示を有効もしくは無効にする。
	 * <em>visible</em> に <tt>true</tt> を指定した場合、
	 * 選択されているセルがある列の列ヘッダが強調表示される。
	 * <tt>true</tt> を指定した場合は、列ヘッダは強調表示されない。
	 * @param visible	強調表示を有効にする場合は <tt>true</tt>、
	 * 					無効にする場合は <tt>false</tt> を指定する。
	 * @since 1.14
	 */
	public void setVisibleSelection(boolean visible) {
		if (_visibleSelection != visible) {
			_visibleSelection = visible;
			revalidate();
			repaint();
		}
	}
	
	/**
	 * 標準の列幅を返す。<br>
	 * 現在、標準の列幅は 75 に固定。
	 * @return	標準の列幅
	 * @since 1.16
	 */
	public int getDefaultColumnWidth() {
		return 75;
	}
	
	/**
	 * 列幅の変更が可能な場合に、列幅を標準の列幅に設定する。<br>
	 * <em>targetColumnIndex</em> で指定された列のセルが選択されている場合、
	 * セルが選択されている他の列も標準列幅に設定する。<br>
	 * <em>targetColumnIndex</em> で指定された列のセルが選択されていない場合は、
	 * 指定された列のみ標準列幅に設定する。
	 * @param targetColumnIndex	対象列のインデックス
	 * @since 1.16
	 */
	public void setSelectedColumnWidthToDefault(int targetColumnIndex) {
		if (targetColumnIndex < 0 || targetColumnIndex >= targetTable.getColumnCount()) {
			// invalid index
			return;
		}

		int wDefault = getDefaultColumnWidth();
		int[] columns = targetTable.getSelectedColumns();
		if (columns.length > 0) {
			Arrays.sort(columns);
			if (Arrays.binarySearch(columns, targetColumnIndex) >= 0/* && targetTable.isColumnSelectedAllRows(targetIndex)*/) {
				// 選択されている列のみ幅調整
				for (int col : columns) {
					if (canResize(col)/* && targetTable.isColumnSelectedAllRows(col)*/) {
						getColumnModel().getColumn(col).setPreferredWidth(wDefault);
					}
				}
			} else {
				// 調整列が選択されていなければ、調整列のみ
				if (canResize(targetColumnIndex)) {
					getColumnModel().getColumn(targetColumnIndex).setPreferredWidth(wDefault);
				}
			}
		} else {
			if (canResize(targetColumnIndex)) {
				getColumnModel().getColumn(targetColumnIndex).setPreferredWidth(wDefault);
			}
		}
	}

	/**
	 * 列幅の変更が可能な場合に、列幅をセルの値の幅に自動調整する。<br>
	 * <em>targetColumnIndex</em> で指定された列のセルが選択されている場合、
	 * セルが選択されている他の列も自動調整する。<br>
	 * <em>targetColumnIndex</em> で指定された列のセルが選択されていない場合は、
	 * 指定された列のみ自動調整する。
	 * @param targetColumnIndex	対象列のインデックス
	 * @since 1.16
	 */
	public void adjustSelectedColumnWidth(int targetColumnIndex) {
		if (targetColumnIndex < 0 || targetColumnIndex >= targetTable.getColumnCount()) {
			// invalid index
			return;
		}
		
		int[] columns = targetTable.getSelectedColumns();
		if (columns.length > 0) {
			Arrays.sort(columns);
			if (Arrays.binarySearch(columns, targetColumnIndex) >= 0/* && targetTable.isColumnSelectedAllRows(targetIndex)*/) {
				// 選択されている列のみ幅調整
				for (int col : columns) {
					if (canResize(col)/* && targetTable.isColumnSelectedAllRows(col)*/) {
						targetTable.adjustColumnPreferredWidth(col);
					}
				}
			} else {
				// 調整列が選択されていなければ、調整列のみ
				if (canResize(targetColumnIndex)) {
					targetTable.adjustColumnPreferredWidth(targetColumnIndex);
				}
			}
		} else {
			if (canResize(targetColumnIndex)) {
				targetTable.adjustColumnPreferredWidth(targetColumnIndex);
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void initialComponents() {
		MouseInputListener ml = new MouseInputListener();
		addMouseListener(ml);
		addMouseMotionListener(ml);
	}
	
	protected void setupDefaultRenderer() {
		DefaultTableCellRenderer label = new ColumnHeaderRenderer();
		label.setHorizontalAlignment(JLabel.CENTER);
		setDefaultRenderer(label);
	}
	
	protected boolean canResize(int columnIndex) {
		if (columnIndex < 0) {
			return false;
		}
		else {
			return canResize(getColumnModel().getColumn(columnIndex));
		}
	}

	protected boolean canResize(TableColumn column) { 
		return (column != null) && getResizingAllowed() && column.getResizable(); 
	}
	
	protected int getResizingColumnIndex(Point p) {
		return getResizingColumnIndex(p, columnAtPoint(p));
	}
	
	protected int getResizingColumnIndex(Point p, int columnIndex) {
		if (columnIndex < 0) {
			return (-1);
		}
		
		Rectangle r = getHeaderRect(columnIndex);
		r.grow(-3, 0);
		if (r.contains(p)) {
			return (-1);
		}
		int midPoint = r.x + r.width / 2;
		
		int targetIndex;
		if (getComponentOrientation().isLeftToRight()) {
			targetIndex = (p.x < midPoint) ? columnIndex - 1 : columnIndex;
		} else {
			targetIndex = (p.x < midPoint) ? columnIndex : columnIndex - 1;
		}
		if (targetIndex < 0) {
			return (-1);
		}
		
		return targetIndex;
	}

	protected TableColumn getResizingColumn(Point p) { 
		return getResizingColumn(p, columnAtPoint(p)); 
	}
	
	protected TableColumn getResizingColumn(Point p, int columnIndex) {
		int targetIndex = getResizingColumnIndex(p, columnIndex);
		if (targetIndex < 0)
			return null;
		else
			return getColumnModel().getColumn(targetIndex);
	}
	
	protected void onTableCellSelectionChanged(ListSelectionEvent e) {
		this.repaint();
	}

	//------------------------------------------------------------
	// Internal classes
	//------------------------------------------------------------
	
	class ColumnHeaderRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column)
		{
			/*---
			System.err.println(
					String.format("SpreadSheetColumnHeader.ColumnHeaderRenderer#getTableCellRendererComponent(table, value:%s, isSelected:%s, hasFocus:%s, row:%d, column:%d)",
					String.valueOf(value), String.valueOf(isSelected), String.valueOf(hasFocus), row, column));
			System.err.println("SpreadSheetColumnHeader.ColumnHeaderRenderer#getTableCellRendererComponent : ColumSelectionModel[" + table.getColumnModel().getSelectionModel().toString() + "]");
			---*/
			if (table != null) {
				if (table instanceof SpreadSheetTable) {
					SpreadSheetTable srcTable = (SpreadSheetTable)table;
					boolean rowSelected = !srcTable.getSelectionModel().isSelectionEmpty();
					if (_visibleSelection && rowSelected && srcTable.isColumnSelected(column)) {
						if (srcTable.getSelectedRowCount()==srcTable.getRowCount()) {
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
				}
				else {
					if (_visibleSelection && isSelected) {
						setForeground(table.getSelectionForeground());
						setBackground(table.getSelectionBackground());
					}
					else {
						setForeground(table.getTableHeader().getForeground());
						setBackground(table.getTableHeader().getBackground());
					}
				}
				setFont(table.getTableHeader().getFont());
			}

			setText((value == null) ? "" : value.toString());
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			return this;
		}
	}
	
	class MouseInputListener extends MouseInputAdapter
	{
		// Component receiving mouse events during editing.
		// May not be editorComponent.
		protected Component dispatchComponent;
		protected boolean selectedOnPress;

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!selectedOnPress && SwingUtilities.isLeftMouseButton(e) && e.getClickCount()==2) {
				//--- マウス左ボタンのダブルクリック
				int targetIndex = getResizingColumnIndex(e.getPoint());
				adjustSelectedColumnWidth(targetIndex);
//				//--- 選択されている列のインデックスを取得
//				int[] columns = targetTable.getSelectedColumns();
//				if (columns.length > 0) {
//					Arrays.sort(columns);
//					if (Arrays.binarySearch(columns, targetIndex) >= 0/* && targetTable.isColumnSelectedAllRows(targetIndex)*/) {
//						// 選択されている列のみ幅調整
//						for (int col : columns) {
//							if (canResize(col)/* && targetTable.isColumnSelectedAllRows(col)*/) {
//								targetTable.adjustColumnPreferredWidth(col);
//							}
//						}
//					} else {
//						// 調整列が選択されていなければ、調整列のみ
//						if (canResize(targetIndex)) {
//							targetTable.adjustColumnPreferredWidth(targetIndex);
//						}
//					}
//				} else {
//					if (canResize(targetIndex)) {
//						targetTable.adjustColumnPreferredWidth(targetIndex);
//					}
//				}
				return;
			}

			// デフォルト
			super.mouseClicked(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
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
			return (SwingUtilities.isLeftMouseButton(e) && canResize(getResizingColumnIndex(e.getPoint())));
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
			int targetIndex = targetTable.columnAtPoint(p);
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
			if (!_enableChangeSelection)
				return;
			
			boolean ctrl = e.isControlDown();
			if (ctrl && e.isShiftDown()) {
				ListSelectionModel rm = targetTable.getSelectionModel();
				ListSelectionModel cm = targetTable.getColumnModel().getSelectionModel();
				int anchorIndex = cm.getAnchorSelectionIndex();

				boolean anchorSelected = true;
				if (anchorIndex < 0 || anchorIndex >= targetTable.getColumnCount()) {
					anchorIndex = 0;
					anchorSelected = false;
				}

				if (anchorSelected && cm.isSelectedIndex(anchorIndex)) {
					rm.addSelectionInterval(targetTable.getRowCount()-1, 0);
					cm.addSelectionInterval(anchorIndex, targetIndex);
				} else {
					cm.removeSelectionInterval(anchorIndex, targetIndex);
					if (cm.isSelectionEmpty()) {
						rm.clearSelection();
					}
					//--- clientProperty("Table.isFileList") の状態は無視
				}
			}
			else {
				targetTable.changeColumnHeaderSelection(targetIndex, ctrl, (!ctrl && e.isShiftDown()));
			}
		}

		protected void mouseDraggedImpl(MouseEvent e) {
			repostEvent(e);

			//--- clientProperty("Table.isFileList") の状態は無視
			if (!_enableChangeSelection || targetTable.isEditing()) {
				return;
			}

			Point p = e.getPoint();
			int targetIndex = targetTable.columnAtPoint(p);
			if (targetIndex < 0) {
				return;
			}

			if (e.isControlDown()) {
				ListSelectionModel rm = targetTable.getSelectionModel();
				ListSelectionModel cm = targetTable.getColumnModel().getSelectionModel();
				int anchorIndex = cm.getAnchorSelectionIndex();

				boolean anchorSelected = true;
				if (anchorIndex < 0 || anchorIndex >= targetTable.getColumnCount()) {
					anchorIndex = 0;
					anchorSelected = false;
				}

				if (anchorSelected && cm.isSelectedIndex(anchorIndex)) {
					rm.addSelectionInterval(targetTable.getRowCount()-1, 0);
					cm.addSelectionInterval(anchorIndex, targetIndex);
				} else {
					cm.removeSelectionInterval(anchorIndex, targetIndex);
					if (cm.isSelectionEmpty()) {
						rm.clearSelection();
					}
					//--- clientProperty("Table.isFileList") の状態は無視
				}

				// From JTable.changeSelection():
				// Scroll after changing the selection as blit scrolling is immediate,
				// so that if we cause the repaint after the scroll we end up painting
				// everything!
				if (targetTable.getAutoscrolls()) {
					// 行インデックス[0]と現在の列インデックスにフォーカス
					Rectangle cellRect = targetTable.getCellRect(0, targetIndex, false);
					if (cellRect != null) {
						targetTable.scrollRectToVisible(cellRect);
					}
				}
			}
			else {
				targetTable.changeColumnHeaderSelection(targetIndex, false, true);
			}
		}
	}
}
