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
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import ssac.util.logging.AppLogger;

/**
 * 実行オプション・パネルのAADL引数テーブル用行ヘッダ・コンポーネント
 * 
 * @version 1.00 2008/03/24
 */
public class TableRowHeader extends JList
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final DefaultListModel		numberList;
	
	private final ListSelectionModel	tableSelection;
	private final ListSelectionModel	rowListSelection;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public TableRowHeader(JTable table) {
		super();
		numberList = new DefaultListModel();
		setModel(numberList);
		
		tableSelection = table.getSelectionModel();
		rowListSelection = getSelectionModel();
		
		initialize(table);
	}
	
	private void initialize(JTable table) {
		updateNumberList(table.getRowCount());
		
		setFixedCellHeight(table.getRowHeight());
		setCellRenderer(new RowHeaderRenderer(table.getTableHeader()));
		//setSelectionModel(table.getSelectionModel());
		RollOverListener rol = new RollOverListener();
		addMouseListener(rol);
		addMouseMotionListener(rol);
		//setBorder(BorderFactory.createMatteBorder(1,0,0,0,Color.GRAY.brighter()));
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// internal RowHeaderRenderer class
	//------------------------------------------------------------
	
	/*------------------------------------------------------------------------------
	 * Mac では、JLabelの描画実装のままでは、なぜか文字が表示されない。
	 * なので、JLabel派生とDefaultListCellRenderer は使えない。
	 * ただ、DefaultTableCellRenderer を使用すると表示される。DefaultListCellRendererと
	 * DefaultTableCellRendererはどちらもJLabelの派生だが、paintに関する実装が
	 * ことなると思われるが、現時点ではそこまで調査していない。
	------------------------------------------------------------------------------*/
    //class RowHeaderRenderer extends JLabel implements ListCellRenderer {
	//class RowHeaderRenderer extends DefaultListCellRenderer {
	class RowHeaderRenderer extends DefaultTableCellRenderer implements ListCellRenderer {
    	private final JTableHeader header; // = table.getTableHeader();
    	public RowHeaderRenderer(JTableHeader header) {
    		super();
    		this.header = header;
    		this.setOpaque(true);
    		//this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    		this.setBorder(BorderFactory.createMatteBorder(0,0,1,2,Color.gray.brighter()));
    		this.setHorizontalAlignment(CENTER);
    		this.setForeground(header.getForeground());
    		this.setBackground(header.getBackground());
    		this.setFont(header.getFont());
    	}
    	public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    		/*
    		if(index==pressedRowIndex) {
    			setBackground(Color.gray);
    		}else if(index==rollOverRowIndex) {
    			setBackground(Color.white);
    		}else if(isSelected) {
    			setBackground(Color.gray.brighter());
    		}else{
    			this.setForeground(header.getForeground());
    			this.setBackground(header.getBackground());
    		}
    		this.setText((value == null) ? "" : value.toString());
    		*/
    		
    		setOpaque(true);
    		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    		if (header != null) {
    			Color crFore = header.getForeground();
    			Color crBack = header.getBackground();
    			Font f = header.getFont();
    			AppLogger.trace("RowHeader:Foreground=" + (crFore != null ? crFore : "null"));
    			AppLogger.trace("RowHeader:Background=" + (crBack != null ? crBack : "null"));
    			AppLogger.trace("RowHeader:Font=" + (f != null ? f : "null"));
    			setForeground(header.getForeground());
    			setBackground(header.getBackground());
    			setFont(header.getFont());
    		}
    		setText((value == null) ? "" : value.toString());
    		return this;
    	}
	}

	//------------------------------------------------------------
	// internal RollOverListener class
	//------------------------------------------------------------

	private int rollOverRowIndex = -1;
	private int pressedRowIndex = -1;
	class RollOverListener extends MouseInputAdapter {
		//@Override
		public void mouseExited(MouseEvent e) {
			pressedRowIndex  = -1;
			rollOverRowIndex = -1;
			repaint();
		}
		//@Override
		public void mouseMoved(MouseEvent e) {
			int row = locationToIndex(e.getPoint());
			if( row != rollOverRowIndex ) {
				rollOverRowIndex = row;
				repaint();
			}
		}
		//@Override
		public void mouseDragged(MouseEvent e) {
			if(pressedRowIndex>=0) {
				int row   = locationToIndex(e.getPoint());
				int start = Math.min(row,pressedRowIndex);
				int end   = Math.max(row,pressedRowIndex);
				tableSelection.clearSelection();
				rowListSelection.clearSelection();
				tableSelection.addSelectionInterval(start, end);
				rowListSelection.addSelectionInterval(start, end);
				repaint();
			}
		}
		//@Override
		public void mousePressed(MouseEvent e) {
			int row = locationToIndex(e.getPoint());
			if( row != pressedRowIndex ) {
				pressedRowIndex = row;
				repaint();
			}
			tableSelection.clearSelection();
			rowListSelection.clearSelection();
			tableSelection.addSelectionInterval(row,row);
			rowListSelection.addSelectionInterval(row,row);
		}
		//@Override
		public void mouseReleased(MouseEvent e) {
			rowListSelection.clearSelection();
			pressedRowIndex  = -1;
			rollOverRowIndex = -1;
			repaint();
		}
	}
}
