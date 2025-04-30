/*
 * @(#)DTCContentAlgeEditTablePane.java	1.1.0	2023/01/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import ssac.aadl.common.CommonResources;
import ssac.util.Strings;
import ssac.util.io.CsvReader;
import ssac.util.logging.AppLogger;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * 交換代数元もしくはデータ代数元の編集用テーブルコンポーネント。
 * 
 * @version 1.1.0
 * @since 1.1.0
 */
public class DTCContentAlgeEditTablePane extends SpreadSheetTable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/** 不正な値を持つセルの背景色(非選択) **/
	static protected final Color	CL_INVALID_VALUE_BG_NORMAL	= CommonResources.DEF_BACKCOLOR_ERROR;
	/** 不正な値を持つセルの背景色(選択) **/
	static protected final Color	CL_INVALID_VALUE_BG_FOCUS	= new Color(255, 0, 0);
	/** 名前キーが空の場合の背景色(非選択) **/
	static protected final Color	CL_EMPTY_NAME_BG_NORMAL		= new Color(255, 242, 0);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DTCContentAlgeEditTablePane() {
		super();
		
		// 標準のセルレンダラーを設定
		setDefaultRenderer(Object.class, new DTCContentAlgeEditTableCellRenderer());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@Override
	public boolean editCellAt(int rowIndex, int columnIndex, EventObject event) {
		// データ型以外のカラムの編集では、エディタコンポーネントの
		// サイズを表示されている領域に限定する。
		boolean result = super.editCellAt(rowIndex, columnIndex, event);
		//if (columnIndex != DtContainerContentExalgeEditModel.COL_TYPE && result) {
		//	Rectangle cellRect = getCellRect(rowIndex, columnIndex, false);
		//	Rectangle editRect = getVisibleRect().intersection(cellRect);
		//	editorComp.setBounds(editRect);
		//	editorComp.validate();
		//}
		return result;
	}
	
	@Override
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);
		
		if (dataModel instanceof AbDTCContentAlgeEditModel) {
			AbDTCContentAlgeEditModel algeEditModel = (AbDTCContentAlgeEditModel)dataModel;
			// setup base key cell editor
			for (int colIndex = 0; colIndex < algeEditModel.getColumnCount(); colIndex++) {
				if (algeEditModel.isBaseKeyStringColumnIndex(colIndex)) {
					getColumnModel().getColumn(colIndex).setCellEditor(new RedundantBaseKeyCellEditor(algeEditModel.getIllegalBaseKeyChars()));
				}
			}
		}
		
		// 先頭のセルを表示
		scrollToVisibleCell(0, 0);
	}

	@Override
	public Component prepareEditor(TableCellEditor editor, int row, int column) {
		Component c = super.prepareEditor(editor, row, column);
		if (c instanceof JCheckBox) {
			JCheckBox chk = (JCheckBox)c;
			chk.setBackground(getSelectionBackground());
			chk.setBorderPainted(true);
		}
		return c;
	}

	@Override
	public void updateUI() {
		super.updateUI();

		TableCellRenderer renderer = getDefaultRenderer(Boolean.class);
		if (renderer instanceof JComponent) {
			((JComponent)renderer).updateUI();
		}
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		int row = rowAtPoint(event.getPoint());
		int col = columnAtPoint(event.getPoint());
		if (row >= 0 && col >= 0) {
			Object cellValue = getModel().getValueAt(row, convertColumnIndexToModel(col));
			if (cellValue instanceof DTCContentInvalidCellValue) {
				return ((DTCContentInvalidCellValue)cellValue).getMessage();
			}
			else {
				return null;
			}
		}
		
		return super.getToolTipText(event);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	/**
	 * テーブルのセルに対して、クリップボードのテキストを貼り付ける。
	 * [Type] 列の値を適切にし、[Value] 列が不正の場合はエラーメッセージを埋め込む。
	 * また、代数基底で禁止された文字は除去する。
	 */
	@Override
	protected void pasteFromClipboard() {
		// 貼り付ける領域が存在しない場合は、処理しない
		if (getRowCount() <= 0 || getColumnCount() <= 0) {
			return;
		}
		
		// 転送可能なオブジェクトを取得
		Clipboard clip = getToolkit().getSystemClipboard();
		Transferable trans = clip.getContents(this);
		//--- 文字列以外は貼り付け禁止
		if (trans == null || !trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return;
		}
		
		// 貼り付け範囲を取得
		CellIndex spos;
		CellIndex epos;
		int numSelectedRows = getSelectedRowCount();
		int numSelectedCols = getSelectedColumnCount();
		if (numSelectedRows > 0 && numSelectedCols > 0) {
			// 選択あり
			spos = getMinSelectionCellIndex();
			epos = getMaxSelectionCellIndex();
			//--- 連続性を検証
			int rows = epos.row - spos.row + 1;
			int cols = epos.column - spos.column + 1;
			if (rows != numSelectedRows || cols != numSelectedCols) {
				// 複数の選択領域には貼り付けできない
				showMessageBox(ERROR_CANNOT_PASTE_MULTIPLE_AREA);
				return;
			}
			//--- 選択が単一であれば、領域拡張
			if (spos.equals(epos)) {
				epos = getUpperBoundCellIndex();
			}
		}
		else {
			// 選択なし
			spos = getLeadSelectionCellIndex();
			if (spos == null || !spos.isValid()) {
				//--- カーソル位置不明の場合は、テーブル左上を貼り付け位置とする
				spos = getLowerBoundCellIndex();
			}
			epos = getUpperBoundCellIndex();
		}
		
		// 転送
		try {
			String data = (String)trans.getTransferData(DataFlavor.stringFlavor);
			if (!Strings.isNullOrEmpty(data)) {
				//--- クリップボードの内容を貼り付け
				CsvReader reader = new CsvReader(new BufferedReader(new StringReader(data)));
				try {
					reader.setDelimiterChar('\t');
					CsvReader.CsvRecord record;
					int irow = spos.row;
					int icol = spos.column;
					while ((record = reader.readRecord()) != null) {
						int numFields = record.getNumFields();
						if (numFields > 0) {
							int limit = Math.min(numFields, (epos.column-icol+1));
							for (int i = 0; i < limit; i++) {
								CsvReader.CsvField field = record.getField(i);
								updateValueAt((field==null ? null : field.getValue()), irow, icol+i);
							}
						} else {
							updateValueAt(null, irow, icol);
						}
						irow++;
						if (irow > epos.row) {
							break;
						}
					}
				}
				finally {
					reader.close();
				}
			}
		}
		catch (IOException ex) {
			AppLogger.error("Cannot to get Transferable data from System clipboard.", ex);
		}
		catch (UnsupportedFlavorException ex) {
			AppLogger.error("Unsupported String flavor in System clipboard data.", ex);
		}
		onFinishedTableEditPasteAction();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	/**
	 * 交換代数基底もしくはデータ代数基底キーのドキュメントフィルター。
	 * コンストラクタに指定された使用禁止文字配列の入力を除外する。
	 * 
	 * @version 1.1.0
	 * @since 1.1.0
	 */
	static protected class RedundantBaseKeyFilter extends DocumentFilter
	{
		private final char[]	_invalidBaseKeyChars;
		
		public RedundantBaseKeyFilter(char[] invalidChars) {
			_invalidBaseKeyChars = invalidChars;
		}
		
		//------------------------------------------------------------
		// Implements DocumentFilter interfaces
		//------------------------------------------------------------
		
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
		throws BadLocationException
		{
			if (string == null) {
				return;
			} else {
				replace(fb, offset, 0, string, attr);
			}
		}

		@Override
		public void remove(FilterBypass fb, int offset, int length)
		throws BadLocationException
		{
			replace(fb, offset, length, "", null);
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
		throws BadLocationException
		{
			if (text != null && !text.isEmpty()) {
				int len = text.length();
				//--- 使用禁止文字の検索
				int posInvalid = (-1);
				for (int i = 0; i < len; i++) {
					char ch = text.charAt(i);
					if (!isValidBaseKeyCharacter(ch)) {
						// invalid character for base key of DtBase
						posInvalid = i;
					}
				}
				if (posInvalid >= 0) {
					StringBuilder sb = Strings.getThreadLocalStringBuilder();
					sb.setLength(0);
					sb.append(text, 0, posInvalid);
					//--- 最初の禁止文字以降の文字列から禁止文字を除外
					for (int i = posInvalid + 1; i < len; i++) {
						char ch = text.charAt(i);
						if (isValidBaseKeyCharacter(ch)) {
							sb.append(ch);
						}
					}
					text = sb.toString();
				}
			}
			
			fb.replace(offset, length, text, attrs);
		}
		
		protected boolean isValidBaseKeyCharacter(char ch) {
			for (int i = 0; i < _invalidBaseKeyChars.length; i++) {
				if (_invalidBaseKeyChars[i] == ch) {
					// invalid character for base key
					return false;
				}
			}
			// valid
			return true;
		}
	}
	
	/**
	 * 交換代数基底もしくはデータ代数基底キーの編集用テキストフィールド。
	 * コンストラクタに指定された使用禁止文字配列の入力を除外する。
	 * 
	 * @version 1.1.0
	 * @since 1.1.0
	 */
	static protected class RedundantBaseKeyTextField extends JTextField
	{
		private static final long serialVersionUID = 1L;

		public RedundantBaseKeyTextField(char[] invalidChars) {
			super();
			Document doc = getDocument();
			if (doc instanceof AbstractDocument) {
				((AbstractDocument)doc).setDocumentFilter(new RedundantBaseKeyFilter(invalidChars));
			}
		}
	}
	
	/**
	 * 交換代数基底もしくはデータ代数基底キーの編集用テキストフィールド。
	 * コンストラクタに指定された使用禁止文字配列の入力を除外する。
	 * 
	 * @version 1.1.0
	 * @since 1.1.0
	 */
	static protected class RedundantBaseKeyCellEditor extends DefaultCellEditor
	{
		private static final long serialVersionUID = 1L;
		
		//Class<?>[] _argTypes = new Class<?>[] {String.class};
		//java.lang.reflect.Constructor<?> _constructor;
		//Object _value;

		public RedundantBaseKeyCellEditor(char[] invalidChars) {
			super(new RedundantBaseKeyTextField(invalidChars));
			getComponent().setName("Table.editor");
			((JComponent)getComponent()).setBorder(new LineBorder(Color.black));
		}
	}
	
	/**
	 * このテーブル独自のセルレンダラー。
	 * 
	 * @version 1.1.0
	 * @since 1.1.0
	 */
	static protected class DTCContentAlgeEditTableCellRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;

//		private final String layoutCompoundLabel(JLabel label, FontMetrics fm, String text, Icon icon,
//				Rectangle rcView, Rectangle rcIcon, Rectangle rcText)
//		{
//			return SwingUtilities.layoutCompoundLabel(
//					label, fm, text, icon,
//					label.getVerticalAlignment(),
//					label.getHorizontalAlignment(),
//					label.getVerticalTextPosition(),
//					label.getHorizontalTextPosition(),
//					rcView, rcIcon, rcText, label.getIconTextGap());
//		}
//
//		private final Rectangle getTargetStringBounds(Graphics g, FontMetrics fm, Rectangle rcText, String text, int index, int len) {
//			int tlen = text.length();
//			int rb = index + len;
//			Rectangle rc;
//			if (rb < tlen) {
//				Rectangle2D rcL = fm.getStringBounds(text, 0, index, g);
//				Rectangle2D rcR = fm.getStringBounds(text, rb, tlen, g);
//				rc = new Rectangle(
//						(int)(rcText.x + rcL.getWidth()),
//						rcText.y,
//						(int)(rcText.width - rcL.getWidth() - rcR.getWidth()),
//						rcText.height);
//			} else {
//				Rectangle2D rcL = fm.getStringBounds(text, 0, index, g);
//				rc = new Rectangle(
//						(int)(rcText.x + rcL.getWidth()),
//						rcText.y,
//						(int)(rcText.width - rcL.getWidth()),
//						rcText.height);
//			}
//			return rc;
//		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
														boolean isSelected, boolean hasFocus,
														int row, int column)
		{
			//System.err.println("MacroCellRenderer#getTableCellRendererComponent: value(" + String.valueOf(value)
			//		+ ") isSelected(" + String.valueOf(isSelected)
			//		+ ") hasFocus(" + String.valueOf(hasFocus)
			//		+ ") row(" + row + ") column(" + column + ")");

			// 標準のレンダラーで描画
			//--- (!isSelected && hasFocus)の状態では、super#getTableCellRendererComponent() 内で、
			//--- "Table.focusCellForeground" と "Table.focusCellBackground" にカラーが変更されているため、
			//--- 元に戻す。
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			// 値がエラーの場合の背景色
			if (value instanceof DTCContentInvalidCellValue) {
				if (isSelected) {
					// 選択時の背景色
					comp.setBackground(CL_INVALID_VALUE_BG_FOCUS);
				}
				else {
					// 非選択時の背景色
					comp.setBackground(CL_INVALID_VALUE_BG_NORMAL);
				}
			}
			else {
				// 通常のセル
				if (isSelected) {
					// 選択時の背景色
					comp.setBackground(table.getSelectionBackground());
				}
				else {
					// 非選択時の背景色
					comp.setBackground(table.getBackground());
				}
			}
			
			return comp;
		}
	}
}
