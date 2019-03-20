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
 * @(#)TableUtil.java	2.00	2012/09/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.table;

import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import ssac.util.swing.SwingTools;

/**
 * <code>JTable</code> 用のユーティリティ。
 * このクラスのメソッドは、基本的に <code>static</code> メソッドとする。
 * 
 * @version 2.00	2012/09/25
 * @since 2.00
 */
public class TableUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String DefaultCutActionName    = "cut";
	static public final String DefaultCopyActionName   = "copy";
	static public final String DefaultPasteActionName  = "paste";
	static public final String DefaultDeleteActionName = "delete";
	
	static public final String TableEditCutActionName    = "table.edit.cut";
	static public final String TableEditCopyActionName   = "table.edit.copy";
	static public final String TableEditPasteActionName  = "table.edit.paste";
	static public final String TableEditDeleteActionName = "table.edit.delete";
	
	static public final String EmptyTableActionName = "table.action.empty";
	
	static protected final Action EmptyTableAction  = new EmptyActionHandler();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private TableUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 行インデックスの有効範囲をチェックする。指定された有効範囲外の場合は、例外をスローする。
	 * チェックでは、<code>0 &lt;= index &lt; rowSize</code> を正当とする。
	 * @param table		<code>JTable</code> オブジェクト
	 * @param index		行インデックス
	 * @throws NullPointerException		<em>table</em> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	指定されたインデックスが範囲外の場合
	 */
	static public final void validRowIndexRange(final JTable table, final int index) {
		if (index >= table.getRowCount())
			throw new IndexOutOfBoundsException("Row index out of range : index(" + index + ")>=" + table.getRowCount());
		if (index < 0)
			throw new IndexOutOfBoundsException("Row index out of range : index(" + index + ")<0");
	}
	
	/**
	 * 列インデックスの有効範囲をチェックする。指定された有効範囲外の場合は、例外をスローする。
	 * チェックでは、<code>0 &lt;= index &lt; colSize</code> を正当とする。
	 * @param table		<code>JTable</code> オブジェクト
	 * @param index		列インデックス
	 * @throws NullPointerException		<em>table</em> が <tt>null</tt> の場合
	 * @throws IndexOutOfBoundsException	指定されたインデックスが範囲外の場合
	 */
	static public final void validColumnIndexRange(final JTable table, final int index) {
		if (index >= table.getColumnCount())
			throw new IndexOutOfBoundsException("Column index out of range : index(" + index + ")>=" + table.getColumnCount());
		if (index < 0)
			throw new IndexOutOfBoundsException("Column index out of range : index(" + index + ")<0");
	}

	/**
	 * テーブルの標準フォントを返す。
	 * @return このテーブルの標準フォント
	 */
	static public Font getDefaultTableFont() {
		return UIManager.getFont("Table.font");
	}

	/**
	 * キーストロークによる自動編集開始の設定状態を取得する。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	自動編集開始の場合は <tt>true</tt>、それ以外の場合は <tt>false</tt> を返す。
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isEnabledAutoStartEditOnKeyStroke(final JTable table) {
		Object value = table.getClientProperty("JTable.autoStartsEdit");
		if (value instanceof Boolean)
			return ((Boolean)value).booleanValue();
		else
			return false;
	}

	/**
	 * キーストロークによる自動編集開始を設定する。
	 * @param table		<code>JTable</code> オブジェクト
	 * @param toEnable	自動編集開始とする場合は <tt>true</tt>
	 * @return	状態が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean setAutoStartEditOnKeyStroke(final JTable table, boolean toEnable) {
		Object value = table.getClientProperty("JTable.autoStartsEdit");
		boolean oldValue = (value instanceof Boolean ? ((Boolean)value).booleanValue() : false);
		boolean modified = false;
		if (oldValue != toEnable) {
			table.putClientProperty("JTable.autoStartsEdit", Boolean.valueOf(toEnable));
			modified = true;
		}
		return modified;
	}

	/**
	 * 行が選択されていない場合に <tt>true</tt> を返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isRowSelectionEmpty(final JTable table) {
		return (table.getSelectedRowCount() <= 0);
	}

	/**
	 * 列が選択されていない場合に <tt>true</tt> を返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isColumnSelectionEmpty(final JTable table) {
		return (table.getSelectedColumnCount() <= 0);
	}

	/**
	 * 全ての行が選択されていれば <tt>true</tt> を返す。
	 * このメソッドは、行の選択状態のみを判定するものであり、列の状態については考慮しない。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	列の状態に関係なく、全ての行が選択されていれば <tt>true</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isSelectedAllRows(final JTable table) {
		int numRows = table.getRowCount();
		return (numRows>0 && numRows==table.getSelectedRowCount());
	}

	/**
	 * 全ての列が選択されていれば <tt>true</tt> を返す。
	 * このメソッドは、列の選択状態のみを判定するものであり、行の状態については考慮しない。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	行の状態に関係なく、全ての列が選択されていれば <tt>true</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isSelectedAllColumns(final JTable table) {
		int numCols = table.getColumnCount();
		return (numCols>0 && numCols==table.getSelectedColumnCount());
	}

	/**
	 * 指定された行が選択状態であり、その行に含まれる全ての列が選択されている場合に <tt>true</tt> を返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @param rowIndex	判定対象の行インデックス
	 * @return	全ての列が選択された行である場合は <tt>true</tt>
	 * @throws NullPointerException	<em>table</em> が <tt>null</tt> の場合
	 */
	static public boolean isRowSelectedAllColumns(final JTable table, int rowIndex) {
		return (table.isRowSelected(rowIndex) && isSelectedAllColumns(table));
	}

	/**
	 * 指定された列が選択状態であり、その列に含まれる全ての行が選択されている場合に <tt>true</tt> を返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @param columnIndex	判定対象の画面内での列インデックス。この値は、テーブルのデータモデル内の列インデックスと同じであるとは限らない。
	 * @return	全ての行が選択された列である場合は <tt>true</tt>
	 * @throws NullPointerException	<em>table</em> が <tt>null</tt> の場合
	 */
	static public boolean isColumnSelectedAllRows(final JTable table, int columnIndex) {
		return (table.isColumnSelected(columnIndex) && isSelectedAllRows(table));
	}

	/**
	 * テーブルが 1 つ以上セルを持っているかを判定する。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	セルが 1 つ以上存在する場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean hasCells(final JTable table) {
		return (table.getRowCount() > 0 && table.getColumnCount() > 0);
	}

	/**
	 * テーブルのセルが 1 つ以上選択されている場合に <tt>true</tt> を返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean hasSelectedCells(final JTable table) {
		return (table.getSelectedRowCount() > 0 && table.getSelectedColumnCount() > 0);
	}

	/**
	 * テーブルのセルが複数選択されている場合に <tt>true</tt> を返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean isMultipleSelectionCells(final JTable table) {
		int numRows = table.getSelectedRowCount();
		int numCols = table.getSelectedColumnCount();
		if (numRows > 1)
			return (numCols > 0);
		else if (numCols > 1)
			return (numRows > 0);
		else
			return false;
	}
	
	/**
	 * テーブルのセルが 1 つだけ選択されている場合に <tt>true</tt> を返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	public boolean isSelectionOneCell(final JTable table) {
		return (table.getSelectedRowCount()==1 && table.getSelectedColumnCount()==1);
	}

	/**
	 * 選択されている行の最小インデックスを返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	選択範囲の行インデックスの最小値、選択されていない場合は (-1)
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public int getMinSelectionRowIndex(final JTable table) {
		return table.getSelectionModel().getMinSelectionIndex();
	}

	/**
	 * 選択されている行の最大インデックスを返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	選択範囲の行インデックスの最大値、選択されていない場合は (-1)
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public int getMaxSelectionRowIndex(final JTable table) {
		return table.getSelectionModel().getMaxSelectionIndex();
	}

	/**
	 * 選択されている行のうち、アンカーとなる行のインデックスを返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	選択範囲のアンカー行のインデックス、選択されていない場合は (-1)
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public int getAnchorSelectionRowIndex(final JTable table) {
		return table.getSelectionModel().getAnchorSelectionIndex();
	}

	/**
	 * 選択されている行のうち、リードとなる行のインデックスを返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	選択範囲のリード行のインデックス、選択されていない場合は (-1)
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public int getLeadSelectionRowIndex(final JTable table) {
		return table.getSelectionModel().getLeadSelectionIndex();
	}

	/**
	 * 選択されている列の最小インデックスを返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	選択範囲の列インデックスの最小値、選択されていない場合は (-1)
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public int getMinSelectionColumnIndex(final JTable table) {
		return table.getColumnModel().getSelectionModel().getMinSelectionIndex();
	}

	/**
	 * 選択されている列の最大インデックスを返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	選択範囲の列インデックスの最大値、選択されていない場合は (-1)
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public int getMaxSelectionColumnIndex(final JTable table) {
		return table.getColumnModel().getSelectionModel().getMaxSelectionIndex();
	}

	/**
	 * 選択されている列のうち、アンカーとなる列のインデックスを返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	選択範囲のアンカー列のインデックス、選択されていない場合は (-1)
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public int getAnchorSelectionColumnIndex(final JTable table) {
		return table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();
	}

	/**
	 * 選択されている列のうち、リードとなる列のインデックスを返す。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	選択範囲のリード列のインデックス、選択されていない場合は (-1)
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public int getLeadSelectionColumnIndex(final JTable table) {
		return table.getColumnModel().getSelectionModel().getLeadSelectionIndex();
	}

	/**
	 * 現在選択されているセルを含む全ての列が選択されるよう、選択範囲を拡張する。
	 * このメソッドは、列選択モデルの全ての要素を選択状態に変更する。
	 * 行選択モデルは変更しない。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	選択状態が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean expandSelectionAllColumns(final JTable table) {
		// 編集をキャンセル
		if (table.isEditing()) {
			table.removeEditor();
		}
		
		// セルが選択されている場合のみ、列の選択範囲を拡張
		boolean modified = false;
		if (hasSelectedCells(table) && table.getSelectedColumnCount() != table.getColumnCount()) {
			int oldLead;
			int oldAnchor;
			ListSelectionModel selModel = table.getColumnModel().getSelectionModel();
			selModel.setValueIsAdjusting(true);
			oldLead = selModel.getLeadSelectionIndex();
			if (oldLead >= table.getColumnCount())
				oldLead = -1;
			oldAnchor = selModel.getAnchorSelectionIndex();
			if (oldAnchor >= table.getColumnCount())
				oldAnchor = -1;
			
			//--- 全てのカラムを選択
			table.setColumnSelectionInterval(0, table.getColumnCount()-1);
            
            //--- anchor & lead を戻す
            if (oldAnchor < 0) {
            	oldAnchor = oldLead;
            }
            if (oldLead < 0) {
            	selModel.setAnchorSelectionIndex(-1);
            	selModel.setLeadSelectionIndex(-1);
            } else {
            	selModel.addSelectionInterval(oldAnchor, oldLead);
            }
            
            //--- 完了
            selModel.setValueIsAdjusting(false);
            modified = true;
		}
		
		return modified;
	}
	
	/**
	 * 現在選択されているセルを含む全ての行が選択されるよう、選択範囲を拡張する。
	 * このメソッドは、行選択モデルの全ての要素を選択状態に変更する。
	 * 列選択モデルは変更しない。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	選択状態が変更された場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public boolean expandSelectionAllRows(final JTable table) {
		// 編集をキャンセル
		if (table.isEditing()) {
			table.removeEditor();
		}
		
		// セルが選択されている場合のみ、行の選択範囲を拡張
		boolean modified = false;
		if (hasSelectedCells(table) && table.getSelectedRowCount() != table.getRowCount()) {
			int oldLead;
			int oldAnchor;
			ListSelectionModel selModel = table.getSelectionModel();
			selModel.setValueIsAdjusting(true);
			oldLead = selModel.getLeadSelectionIndex();
			if (oldLead >= table.getRowCount())
				oldLead = -1;
			oldAnchor = selModel.getAnchorSelectionIndex();
			if (oldAnchor >= table.getRowCount())
				oldAnchor = -1;
			
			//--- 全ての行を選択
			table.setRowSelectionInterval(0, table.getRowCount()-1);
			
			//--- anchor & lead を戻す
			if (oldAnchor < 0) {
				oldAnchor = oldLead;
			}
			if (oldLead < 0) {
				selModel.setAnchorSelectionIndex(-1);
				selModel.setLeadSelectionIndex(-1);
			} else {
				selModel.addSelectionInterval(oldAnchor, oldLead);
			}
			
			//--- 完了
			selModel.setValueIsAdjusting(false);
			modified = true;
		}
		
		return modified;
	}

	/**
	 * 指定されたセルを表示可能な位置までスクロールする。
	 * @param table				<code>JTable</code> オブジェクト
	 * @param rowIndex			セルの行インデックス
	 * @param columnIndex		画面内で目的のセルが置かれている列インデックス。この値は、テーブルのデータモデル内の列インデックスと同じであるとは限らない。
	 * @param includeSpacing	<tt>false</tt> の場合、真のセルの境界を返す。この境界は、列モデルと行モデルの高さと幅からセルの間隔を引くことによって計算される 
	 * @throws NullPointerException	<em>table</em> が <tt>null</tt> の場合
	 */
	static public void scrollToVisibleCell(final JTable table, int rowIndex, int columnIndex, boolean includeSpacing) {
		Rectangle rcCell = table.getCellRect(rowIndex, columnIndex, includeSpacing);
		table.scrollRectToVisible(rcCell);
	}

	/**
	 * 指定されたセルを表示可能な位置までスクロールする。
	 * このメソッドでは、指定された行と列の全高さと全幅を含む矩形によって位置を調整する。
	 * @param table				<code>JTable</code> オブジェクト
	 * @param rowIndex			セルの行インデックス
	 * @param columnIndex		画面内で目的のセルが置かれている列インデックス。この値は、テーブルのデータモデル内の列インデックスと同じであるとは限らない。
	 * @throws NullPointerException	<em>table</em> が <tt>null</tt> の場合
	 */
	static public void scrollToVisibleCell(final JTable table, int rowIndex, int columnIndex) {
		Rectangle rcCell = table.getCellRect(rowIndex, columnIndex, true);
		table.scrollRectToVisible(rcCell);
	}

	/**
	 * 指定されたセルにフォーカスを設定する。
	 * <em>scrollToVisible</em> に <tt>true</tt> を指定した場合、指定されたセルを表示可能な位置までスクロールする。
	 * @param table				<code>JTable</code> オブジェクト
	 * @param rowIndex			セルの行インデックス
	 * @param columnIndex		画面内で目的のセルが置かれている列インデックス。この値は、テーブルのデータモデル内の列インデックスと同じであるとは限らない。
	 * @param scrollToVisible	指定されたセルを表示可能な位置までスクロールする場合は <tt>true</tt>
	 * @throws NullPointerException	<em>table</em> が <tt>null</tt> の場合
	 */
	static public void setFocusToCell(final JTable table, int rowIndex, int columnIndex, boolean scrollToVisible) {
		table.getColumnModel().getSelectionModel().setLeadSelectionIndex(columnIndex);
		table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(columnIndex);
		table.getSelectionModel().setLeadSelectionIndex(rowIndex);
		table.getSelectionModel().setAnchorSelectionIndex(rowIndex);
		if (scrollToVisible) {
			scrollToVisibleCell(table, rowIndex, columnIndex);
		}
	}

	/**
	 * 表示されているテーブルの範囲を示す行インデックスと列インデックスの
	 * 矩形を返す。<br>
	 * {@link java.awt.Rectangle#x} は表示領域左上の列インデックス、
	 * {@link java.awt.Rectangle#y} は表示領域左上の行インデックス、
	 * {@link java.awt.Rectangle#width} は表示領域内の列数、
	 * {@link java.awt.Rectangle#height} は表示領域内の行数を格納する。<br>
	 * このメソッドは、表示領域となる座標を {@link #getVisibleRect()} によって座標を表す矩形を取得する。
	 * 表示領域左上の座標から行インデックスもしくは列インデックスが取得できない場合、取得できない
	 * インデックス値を 0 とする。また、表示領域右下の座標から列インデックスが取得できない場合は最大列数、
	 * 行インデックスが取得できない場合は最大行数を上限として領域の大きさを格納する。
	 * 表示領域が空の場合、このメソッドは(すべての要素が 0 の)空の矩形を返す。
	 * <p><b>注：</b>
	 * <blockquote>
	 * このメソッドが返す 列インデックスは、テーブルのデータモデル内の列インデックスと同じであるとは限らない。
	 * </blockquote>
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	<code>Rectangle</code> オブジェクト
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public Rectangle getVisibleCellIndices(final JTable table) {
		Rectangle box = table.getVisibleRect();
		Point pt = box.getLocation();
		int rBegin = table.rowAtPoint(pt);
		int cBegin = table.columnAtPoint(pt);
		if (rBegin < 0)	rBegin=0;
		if (cBegin < 0) cBegin=0;
		if (box.isEmpty()) {
			return new Rectangle();
		}
		pt.translate(box.width, box.height);
		
		int rEnd = table.rowAtPoint(pt);
		int cEnd = table.columnAtPoint(pt);
		if (rEnd < rBegin) {
			rEnd = table.getRowCount();
		} else {
			rEnd = Math.min(rEnd + 1, table.getRowCount());
		}
		if (cEnd < cBegin) {
			cEnd = table.getColumnCount();
		} else {
			cEnd = Math.min(cEnd + 1, table.getColumnCount());
		}
		
		return new Rectangle(cBegin, rBegin, (cEnd-cBegin), (rEnd-rBegin));
	}

	/**
	 * 全カラムの推奨幅の合計を取得する。
	 * @param table		<code>JTable</code> オブジェクト
	 * @return	全カラムの推奨幅の合計値
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public int getTotalPreferredColumnWidth(final JTable table) {
		TableColumnModel model = table.getColumnModel();
		int totalWidth = 0;
		int numCols = model.getColumnCount();
		for (int col = 0; col < numCols; col++) {
			totalWidth += model.getColumn(col).getPreferredWidth();
		}
		return totalWidth;
	}

	/**
	 * 指定されたカラムの <em>beginRowIndex</em> から (<em>endRowIndex</em> - 1) までのデータがすべて表示可能な推奨サイズを取得する。
	 * 指定された行インデックスがテーブル範囲外の場合、最低限のサイズが返される。
	 * <p><em>columnHeader</em> が <tt>null</tt> ではない場合、列ヘッダの内容も表示対象のデータとして利用される。
	 * <p><em>useSelectedCells</em> が <tt>true</tt> の場合、指定された行範囲の中で選択されたセルのデータのみが対象となる。この場合、列ヘッダのデータは無視される。
	 * ただし、指定された行範囲の中で選択されたセルが 1 つも存在しない場合は、全てのセルと列ヘッダーのデータが対象となる。
	 * <p>データからサイズが取得できなかった場合、最低限のサイズが返される。
	 * @param table		<code>JTable</code> オブジェクト
	 * @param columnHeader		列ヘッダの情報を使用する場合は、列ヘッダのオブジェクトを指定する
	 * @param beginRowIndex		データを取得する範囲の先頭行インデックス
	 * @param endRowIndex		データを取得する範囲の終端行インデックス
	 * @param columnIndex		対象の画面内での列インデックス。この値は、テーブルのデータモデル内の列インデックスと同じであるとは限らない。
	 * @param useSelectedCells	選択されたセルのみを対象とする場合は <tt>true</tt> を指定する
	 * @return	計算された列の幅を返す。
	 * @throws NullPointerException	<em>table</em> が <tt>null</tt> の場合
	 */
	static public int getPreferredColumnDataWidth(final JTable table, final TableColumn columnHeader, int beginRowIndex, int endRowIndex, int columnIndex, boolean useSelectedCells)
	{
		// 列ヘッダの幅を取得
		int maxWidth = 0;
		if (columnHeader != null) {
			TableCellRenderer headerRenderer = columnHeader.getHeaderRenderer();
			if (headerRenderer == null)
				headerRenderer = table.getTableHeader().getDefaultRenderer();
			Object headerValue = columnHeader.getHeaderValue();
			//--- 表示が大きく変化しないよう、選択、フォーカス設定されているものとする
			Component headerComp = headerRenderer.getTableCellRendererComponent(table, headerValue, true, true, -1, columnIndex);
			maxWidth = headerComp.getPreferredSize().width;
		}

		// セルの最大幅を取得
		int limitRowIndex = Math.min(endRowIndex, table.getRowCount());
		if (!useSelectedCells || isColumnSelectedAllRows(table, columnIndex) || isRowSelectionEmpty(table)) {
			// 選択状態に関わらず、サイズを取得する
			int width = 0;
			for (int row = beginRowIndex; row < limitRowIndex; row++) {
				Object value = table.getValueAt(row, columnIndex);
				TableCellRenderer renderer = table.getCellRenderer(row, columnIndex);
				//--- 表示が大きく変化しないよう、選択、フォーカス設定されているものとする
				Component comp = renderer.getTableCellRendererComponent(table, value, true, true, row, columnIndex);
				width = comp.getPreferredSize().width;
				if (maxWidth < width) {
					maxWidth = width;
				}
			}
		}
		else {
			// 選択されているセルのみ使用する
			int width = 0;
			int selectedMaxWidth = -1;
			for (int row = beginRowIndex; row < limitRowIndex; row++) {
				Object value = table.getValueAt(row, columnIndex);
				TableCellRenderer renderer = table.getCellRenderer(row, columnIndex);
				//--- 表示が大きく変化しないよう、選択、フォーカス設定されているものとする
				Component comp = renderer.getTableCellRendererComponent(table, value, true, true, row, columnIndex);
				width = comp.getPreferredSize().width;
				if (maxWidth < width) {
					maxWidth = width;
				}
				if (table.isCellSelected(row, columnIndex) && selectedMaxWidth < width) {
					selectedMaxWidth = width;
				}
			}
			if (selectedMaxWidth >= 0) {
				maxWidth = selectedMaxWidth;
			}
		}
		
		// 推奨幅の調整
		if (maxWidth > 0) {
			maxWidth += table.getColumnModel().getColumnMargin();		// コンポーネントの推奨幅に追加
		}
		else {
			// マージンのみ
			maxWidth = table.getColumnModel().getColumnMargin();
		}
		
		return maxWidth;
	}

	/**
	 * すべての列幅の合計が指定された幅よりも小さい場合、<em>columnHeader</em> の列幅を拡張する。
	 * ただし、<em>columnHeader</em> の最大列幅は超えないようにサイズ調整される。
	 * @param table			<code>JTable</code> オブジェクト
	 * @param columnHeader	設定対象のカラム
	 * @param targetWidth	設定する推奨幅
	 * @return	幅が変更された場合は <tt>true</tt>、変更されなかった場合は <tt>false</tt>
	 * @throws NullPointerException	<em>table</em> もしくは <em>columnHeader</em> が <tt>null</tt> の場合
	 */
	static public boolean expandColumnWidthToFit(final JTable table, final TableColumn columnHeader, int targetWidth) {
		int pWidth = columnHeader.getPreferredWidth();
		int delta = targetWidth - getTotalPreferredColumnWidth(table);
		int newWidth = pWidth + delta;
		if (newWidth > columnHeader.getMaxWidth()) {
			newWidth = columnHeader.getMaxWidth();
		}
		if (newWidth > pWidth) {
			columnHeader.setPreferredWidth(newWidth);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * すべての列幅の合計が指定された幅になるように、<em>columnHeader</em> の列幅を調整する。
	 * ただし、<em>columnHeader</em> の最小・最大幅の範囲内で調整される。
	 * @param table			<code>JTable</code> オブジェクト
	 * @param columnHeader	設定対象のカラム
	 * @param targetWidth	設定する幅
	 * @return	幅が変更された場合は <tt>true</tt>、変更されなかった場合は <tt>false</tt>
	 * @throws NullPointerException	<em>table</em> もしくは <em>columnHeader</em> が <tt>null</tt> の場合
	 */
	static public boolean adjustColumnWidthToFit(final JTable table, final TableColumn columnHeader, int targetWidth) {
		int pWidth = columnHeader.getPreferredWidth();
		int delta = targetWidth - getTotalPreferredColumnWidth(table);
		int newWidth = pWidth + delta;
		if (newWidth > columnHeader.getMaxWidth()) {
			newWidth = columnHeader.getMaxWidth();
		}
		else if (newWidth < columnHeader.getMinWidth()) {
			newWidth = columnHeader.getMinWidth();
		}
		
		if (newWidth != pWidth) {
			columnHeader.setPreferredWidth(newWidth);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 指定された列の幅を、セルのデータが表示可能な幅に調整する。
	 * <p><em>onlyVisibleRows</em> に <tt>true</tt> を指定した場合は、現在の表示領域に含まれる行が調整対象となる。<tt>false</tt> の場合は全行が対象となる。
	 * <p><em>useSelectedCells</em> に <tt>true</tt> を指定した場合は、対象行範囲内で選択されているセルのデータのみで調整する。
	 * 選択されているセルが存在しない場合は、対象行範囲内のすべてのセルデータで調整する。
	 * <p><em>ignoreColumnHeader</em> に <tt>true</tt> を指定した場合は、列ヘッダーの内容は使用しない。
	 * @param table					<code>JTable</code> オブジェクト
	 * @param columnIndex			画面内で目的のセルが置かれている列インデックス。この値は、テーブルのデータモデル内の列インデックスと同じであるとは限らない。
	 * @param onlyVisibleRows		表示範囲内の行のみを調整範囲とする場合は <tt>true</tt>
	 * @param useSelectedCells		選択セルのデータのみを使用する場合は <tt>true</tt>
	 * @param ignoreColumnHeader	列ヘッダの内容を無視する場合は <tt>true</tt>
	 * @return	幅が変更された場合は <tt>true</tt>、変更されなかった場合は <tt>false</tt>
	 * @throws NullPointerException	<em>table</em> が <tt>null</tt> の場合
	 */
	static public boolean adjustColumnPreferredWidth(final JTable table, int columnIndex, boolean onlyVisibleRows, boolean useSelectedCells, boolean ignoreColumnHeader) {
		// check
		validColumnIndexRange(table, columnIndex);

		// 調整に利用するデータの範囲を取得
		int beginRowIndex = 0;
		int endRowIndex = table.getRowCount();
		if (onlyVisibleRows) {
			Rectangle rc = getVisibleCellIndices(table);
			if (rc.height > 0) {
				beginRowIndex = rc.y;
				endRowIndex = rc.y + rc.height;
			}
		}
		
		// 列ヘッダを取得
		TableColumn columnHeader = table.getColumnModel().getColumn(columnIndex);
		
		// サイズ調整
		int newWidth = getPreferredColumnDataWidth(table, (ignoreColumnHeader ? null : columnHeader), beginRowIndex, endRowIndex, columnIndex, useSelectedCells);
		if (newWidth != columnHeader.getPreferredWidth()) {
			// 新しいサイズを設定
			columnHeader.setPreferredWidth(newWidth);
			return true;
		} else {
			// 変更無し
			return false;
		}
	}

	/**
	 * テーブルに、メニューアクセラレータキーとなるキーストロークを
	 * 登録する。ここで登録されたキーストロークは、テーブル内で処理をせず、
	 * メニューコンポーネントのキーストロークとして処理される。
	 * 
	 * @param table		<code>JTable</code> オブジェクト
	 * @param strokes	登録するキーストローク
	 * @throws NullPointerException	<code>strokes</code> が <tt>null</tt> の場合、
	 * 									もしくは、<code>strokes</code> 内の要素に <tt>null</tt> が含まれている場合
	 */
	static public void registMenuAcceleratorKeyStroke(final JTable table, KeyStroke...strokes) {
		// check argument
		if (strokes == null)
			throw new NullPointerException("'strokes' is null.");
		
		// get Maps
		//InputMap imap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		//ActionMap amap = getActionMap();
		InputMap imap = SwingUtilities.getUIInputMap(table, JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap amap = SwingUtilities.getUIActionMap(table);

		// regist key strokes
		for (KeyStroke ks : strokes) {
			if (ks == null)
				throw new NullPointerException("'strokes' in null element.");
			SwingTools.registerActionToMaps(imap, amap, ks, EmptyTableActionName, EmptyTableAction);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * メニューアクセラレータキー用のアクションハンドラ。
	 * このクラスは、JTableのキーストロークマッピング(InputMap)を変更するために
	 * インスタンス化され、アクションマップでこのインスタンスを取得した場合は、
	 * <code>JTable#processKeyBinding()</code> を呼び出さないように処理する。
	 * これは、JTable標準のキーストロークマッピングに処理されないことと、
	 * セルの自動編集開始が有効な場合にアクセラレータが処理されてもセルエディタが
	 * 起動してしまう問題を回避するためのもの。
	 * <p>このクラスはダミーインスタンスとなり、このクラスインスタンスが
	 * 何らかのアクションを実行することはない。
	 */
	static class EmptyActionHandler extends AbstractAction {
		public EmptyActionHandler() {
			super(EmptyTableActionName);
		}
		
		public void actionPerformed(ActionEvent e) {}	// 処理しない
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
