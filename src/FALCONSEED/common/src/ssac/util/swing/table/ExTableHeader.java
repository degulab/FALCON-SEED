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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)ExTableHeader.java	2.00	2012/09/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.table;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * <code>JTableHeader</code> の拡張機能の実装。
 * このクラスでは、列境界のダブルクリックで列幅を調整する機能を実装する。
 * 
 * @version 2.00	2012/09/25
 * @since 2.00
 */
public class ExTableHeader extends JTableHeader
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 列境界のダブルクリックでサイズ調整するフラグ **/
	private boolean	_enableDoubleClickResizing    = true;
	/** 列幅調整時に列ヘッダの内容を無視する **/
	private boolean	_adjustColumnIgnoreHeader     = false;
	/** 列幅調整時に表示行のみを範囲とする **/
	private boolean	_adjustColumnOnlyVisibleRows  = false;
	/** 列幅調整時に選択セルのみを対象とする **/
	private boolean	_adjustColumnUseSelectedCells = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ExTableHeader(TableColumnModel cm) {
		super(cm);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isAdjustColumnWidthEnabled() {
		return _enableDoubleClickResizing;
	}
	
	public void setAdjustColumnWidthEnabled(boolean toEnable) {
		_enableDoubleClickResizing = toEnable;
	}
	
	public boolean getAdjustColumnWidthIgnoreHeader() {
		return _adjustColumnIgnoreHeader;
	}
	
	public void setAdjustColumnWidthIgnoreHeader(boolean toSet) {
		_adjustColumnIgnoreHeader = toSet;
	}
	
	public boolean getAdjustColumnWidthOnlyVisibleRows() {
		return _adjustColumnOnlyVisibleRows;
	}
	
	public void setAdjustColumnWidthOnlyVisibleRows(boolean toSet) {
		_adjustColumnOnlyVisibleRows = toSet;
	}
	
	public boolean getAdjustColumnWidthUseSelectedCells() {
		return _adjustColumnUseSelectedCells;
	}
	
	public void setAdjustColumnWidthUseSelectedCells(boolean toSet) {
		_adjustColumnUseSelectedCells = toSet;
	}
	
	public boolean adjustColumnWidth(int columnIndex) {
		return TableUtil.adjustColumnPreferredWidth(getTable(), columnIndex, _adjustColumnOnlyVisibleRows, _adjustColumnUseSelectedCells, _adjustColumnIgnoreHeader);
	}
	
	public boolean adjustAllColumnWidth(boolean onlyResizable) {
		int numCols = getColumnModel().getColumnCount();
		boolean modified = false;
		if (onlyResizable) {
			// サイズ変更可能な列のみ
			for (int columnIndex = 0; columnIndex < numCols; columnIndex++) {
				if (getColumnModel().getColumn(columnIndex).getResizable()) {
					if (TableUtil.adjustColumnPreferredWidth(getTable(), columnIndex, _adjustColumnOnlyVisibleRows, _adjustColumnUseSelectedCells, _adjustColumnIgnoreHeader)) {
						modified = true;
					}
				}
			}
		}
		else {
			// すべての列
			for (int columnIndex = 0; columnIndex < numCols; columnIndex++) {
				if (TableUtil.adjustColumnPreferredWidth(getTable(), columnIndex, _adjustColumnOnlyVisibleRows, _adjustColumnUseSelectedCells, _adjustColumnIgnoreHeader)) {
					modified = true;
				}
			}
		}
		
		return modified;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void onDoubleClickedColumnBorder(MouseEvent e, int viewColumnIndex) {
		if (_enableDoubleClickResizing) {
			TableUtil.adjustColumnPreferredWidth(getTable(), viewColumnIndex, _adjustColumnOnlyVisibleRows, _adjustColumnUseSelectedCells, _adjustColumnIgnoreHeader);
		}
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_CLICKED && SwingUtilities.isLeftMouseButton(e)) {
			Cursor cur = this.getCursor();
			if (cur.getType() == Cursor.E_RESIZE_CURSOR) {
				int cc = e.getClickCount();
				if (cc % 2 == 1) {
					// シングルクリック
					// 無処理でリターンしない場合、ソート機能が働いてしまう
					return;
				}
				else {
					// ダブルクリック
					Point pt = new Point(e.getX() - 3, e.getY());
					int vc = super.columnAtPoint(pt);
					if (vc >= 0) {
						onDoubleClickedColumnBorder(e, vc);
						e.consume();
						return;
					}
				}
			}
		}
		
		super.processMouseEvent(e);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
