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
/*
 * @(#)ConsolePane.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ConsolePane.java	1.10	2009/01/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ConsolePane.java	1.03	2008/08/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ConsolePane.java	1.00	2008/12/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.console;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import ssac.aadl.editor.view.menu.EditorMenuResources;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.menu.MenuItemResource;

/**
 * 情報ビューの各パネルに組み込まれる、メッセージ表示用コンポーネント。
 * 
 * @version 1.14	2009/12/09
 */
public class ConsolePane extends JTextPane
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static public final int INVALID_LINELENGTH_LIMIT = 0;
	static public final int MIN_LINELENGTH_LIMIT = 16;
	static public final int MAX_LINELENGTH_LIMIT = Integer.MAX_VALUE;
	
	static protected final int INVALID_LINECOUNT_LIMIT = 0;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final DefaultStyledDocument document;
	private final MutableAttributeSet attrForError;
	private boolean lineWrapped = false;
	
	private final FocusRequester hFocusRequester = new FocusRequester();
	
	private final ConsoleOutputWriter writerOut;
	private final ConsoleErrorWriter  writerErr;

	protected final Action contextCopyAction;
	protected final Action contextSelectAllAction;
	protected final JPopupMenu contextMenu;
	
	private volatile int limitLineLength = INVALID_LINELENGTH_LIMIT;
	private volatile int limitLineCount = INVALID_LINECOUNT_LIMIT;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ConsolePane() {
		super(new DefaultStyledDocument());
		this.document = (DefaultStyledDocument)getDocument();
		this.attrForError = new SimpleAttributeSet();
		StyleConstants.setForeground(this.attrForError, Color.RED);
		//---
		writerOut = new ConsoleOutputWriter();
		writerErr = new ConsoleErrorWriter();
		//---
		setEditable(false);
		setLineWrap(true);
		
		// コンテキストメニューの作成
		this.contextCopyAction = createContextCopyAction();
		this.contextSelectAllAction = createContextSelectAllAction();
		this.contextMenu = createContextMenu();

		// フォーカスによるキャレット制御
		addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				//--- キャレットを表示
				Caret caret = ConsolePane.this.getCaret();
				if (caret != null) {
					if (!caret.isVisible()) {
						caret.setVisible(true);
					}
				}
			}

			public void focusLost(FocusEvent e) {
				//--- キャレットを非表示
				Caret caret = ConsolePane.this.getCaret();
				if (caret != null) {
					caret.setVisible(false);
				}
			}
		});
		
		// マウスイベントによるコンテキストメニュー制御
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				evaluateContextPopupMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				evaluateContextPopupMenu(e);
			}
		});
		
		// キャレット変更通知(コンテキストメニューの更新)
		addCaretListener(new CaretListener(){
			public void caretUpdate(CaretEvent ce) {
				boolean selected = (ce.getDot() != ce.getMark());
				contextCopyAction.setEnabled(selected);
			}
		});
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean canCopy() {
		return contextCopyAction.isEnabled();
	}
	
	public boolean getLineWrap() {
		return lineWrapped;
	}
	
	public void setLineWrap(boolean wrap) {
		lineWrapped = wrap;
	}

	public void clear() {
		setText("");
	}
	
	public int getLineLengthLimit() {
		return limitLineLength;
	}
	
	public void setLineLengthLimit(int limit) {
		if (limit <= INVALID_LINELENGTH_LIMIT) {
			// ignore limit
			limitLineLength = INVALID_LINELENGTH_LIMIT;
		}
		else if (limit < MIN_LINELENGTH_LIMIT || limit > MAX_LINELENGTH_LIMIT) {
			throw new IllegalArgumentException("Illegal limit : " + limit);
		}
		else {
			limitLineLength = limit;
		}
	}
	
	public PrintWriter getOutputPrintWriter() {
		return new PrintWriter(writerOut);
	}
	
	public PrintWriter getErrorPrintWriter() {
		return new PrintWriter(writerErr);
	}
	
	public void appendOutputString(String str) {
		append(str, null);
	}
	
	public void appendErrorString(String str) {
		append(str, attrForError);
	}
	
	public void setFocus() {
		//--- このメソッドは、フォーカスの状態に関わらず、フォーカス設定を実行
		SwingUtilities.invokeLater(hFocusRequester);
		/*---
		if (!this.hasFocus()) {
			SwingUtilities.invokeLater(hFocusRequester);
		}
		---*/
	}

	@Override
	public void setText(String t) {
		// ステータスをクリアするなら、ココ(現時点では、クリアしない)
		// ドキュメント・テキストの設定
		super.setText(t);
	}

	public int getLineOfOffset(int offset) throws BadLocationException
	{
		Document doc = getDocument();
		if (offset < 0) {
			throw new BadLocationException("Can't translate offset to line", -1);
		} else if (offset > doc.getLength()) {
			throw new BadLocationException("Can't translate offset to line", doc.getLength()+1);
		} else {
			Element map = getDocument().getDefaultRootElement();
			return map.getElementIndex(offset);
		}
	}

	public int getLineCount() {
		Element map = getDocument().getDefaultRootElement();
		return map.getElementCount();
	}

	public int getLineStartOffset(int line) throws BadLocationException
	{
		int lineCount = getLineCount();
		if (line < 0) {
			throw new BadLocationException("Negative line", -1);
		} else if (line >= lineCount) {
			throw new BadLocationException("No such line", getDocument().getLength()+1);
		} else {
			Element map = getDocument().getDefaultRootElement();
			Element lineElem = map.getElement(line);
			return lineElem.getStartOffset();
		}
	}

	public int getLineEndOffset(int line) throws BadLocationException
	{
		int lineCount = getLineCount();
		if (line < 0) {
			throw new BadLocationException("Negative line", -1);
		} else if (line >= lineCount) {
			throw new BadLocationException("No such line", getDocument().getLength()+1);
		} else {
			Element map = getDocument().getDefaultRootElement();
			Element lineElem = map.getElement(line);
			int endOffset = lineElem.getEndOffset();
			// hide the implicit break at the end of the document
			return ((line == lineCount - 1) ? (endOffset - 1) : endOffset);
		}
	}

	//@@@ added by Y.Ishizuka : 2008.08.04
	protected int getFinalLineCharCount() {
		// 現在の最終行の文字数を取得する。
		int targetLine = getLineCount() - 1;
		if (targetLine < 0)
			targetLine = 0;
		int flc = 0;
		try {
			flc = getLineEndOffset(targetLine) - getLineStartOffset(targetLine);
		} catch (BadLocationException ignoreEx) {}
		return flc;
	}
	//@@@ end of added.

	//@@@ added by Y.Ishziuka : 2008.08.04
	public void append(String strText, AttributeSet attr) {
		// 制限行数へクリップ
		clipToLineCountLimit();
		// 文字列追加
		int lineLimit = limitLineLength;
		if (lineLimit > 0) {
			// １行を指定の文字数に収める。制限文字数を超える場合は、改行を埋め込む。
			int len = strText.length();
			int spos = 0;
			while (spos < len) {
				int cnt = 512 - getFinalLineCharCount();
				if (cnt <= 0) {
					appendOne("\n", null);
				}
				else {
					int epos = Math.min(spos + cnt, len);
					appendOne(strText.substring(spos, epos), attr);
					spos = epos;
				}
			}
		} else {
			// 制限文字数に関係なく、テキストをドキュメントに追加
			appendOne(strText, attr);
		}
	}
	
	protected void clipToLineCountLimit() {
		if (limitLineCount > 0) {
			int removeLines = getLineCount() - limitLineCount;
			if (removeLines > 0) {
				try {
					int spos = getLineStartOffset(0);
					int epos = getLineEndOffset(removeLines-1);
					document.remove(spos, (epos-spos));
				} catch (BadLocationException ignoreEx) {}
			}
		}
	}
	
	protected void appendOne(String strText, AttributeSet attr) {
		try {
			boolean retry = false;
			try {
				document.insertString(document.getLength(), strText, attr);
			}
			catch (OutOfMemoryError ex) {
				//AppLogger.warn("OutOfMemory at appending text to JTextArea.", ex);
				// メモリ不足の場合は、表示行数を制限する
				int removeLines = 0;
				try {
					//--- 削除する文字数の閾値
					int removeChars = Math.max(strText.length()*2, (int)(this.getDocument().getLength()*0.1f));
					//--- 削る文字数より、現在のドキュメントが小さい場合は、メモリ少なすぎ
					if (this.getDocument().getLength() < removeChars) {
						throw ex;
					}
					//--- 最終的な制限行数を設定
					removeLines = getLineOfOffset(getLineStartOffset(0) + removeChars) + 1;
				} catch (BadLocationException ignoreEx) {}
				//--- 削除する行数がない場合は、メモリ少なすぎ
				if (removeLines <= 0) {
					throw ex;
				}
				//--- 制限行数を設定
				limitLineCount = getLineCount() - removeLines;
//				AppLogger.debug("Update Console line limit = " + limitLineCount);
				//--- 文字列追加をリトライ
				retry = true;
			}

			// 再試行
			if (retry) {
				//--- 制限行数内に収める
				clipToLineCountLimit();
				//--- 文字を追加
				document.insertString(document.getLength(), strText, attr);
			}
		}
		catch (BadLocationException ex) {ex=null;}
	}
	//@@@ end of added.

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected Action createContextCopyAction() {
		MenuItemResource mr = EditorMenuResources.getMenuResource(EditorMenuResources.ID_EDIT_COPY);
		Action action = new AbMenuItemAction(mr){
			public void actionPerformed(ActionEvent e) {
				ConsolePane.this.copy();
				ConsolePane.this.setFocus();
			}
		};
		return action;
	}
	
	protected Action createContextSelectAllAction() {
		MenuItemResource mr = EditorMenuResources.getMenuResource(EditorMenuResources.ID_EDIT_SELECTALL);
		Action action = new AbMenuItemAction(mr){
			public void actionPerformed(ActionEvent e) {
				ConsolePane.this.selectAll();
				ConsolePane.this.setFocus();
			}
		};
		return action;
	}
	
	protected JPopupMenu createContextMenu() {
		JPopupMenu menu = new JPopupMenu();

		//--- copy
		menu.add(new JMenuItem(contextCopyAction));
		//---
		menu.addSeparator();
		//--- select all
		menu.add(new JMenuItem(contextSelectAllAction));
		
		return menu;
	}
	
	protected void evaluateContextPopupMenu(MouseEvent me) {
		// コンテキストメニュー表示のトリガ検証
		if (!me.isPopupTrigger())
			return;
		
		// ポップアップメニューの表示
		JPopupMenu pmenu = contextMenu;
		if (pmenu != null) {
			pmenu.show(me.getComponent(), me.getX(), me.getY());
			me.getComponent().requestFocusInWindow();
		}
	}
	
	protected void appendOutputString(char[] cbuf, int off, int len) {
		String str = new String(cbuf, off, len);
		append(str, null);
	}
	
	protected void appendErrorString(char[] cbuf, int off, int len) {
		String str = new String(cbuf, off, len);
		append(str, attrForError);
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		if (!lineWrapped) {
			if (getParent() instanceof JViewport) {
				JViewport port = (JViewport)getParent();
				TextUI ui = getUI();
				int w = port.getWidth();
				Dimension sz = ui.getPreferredSize(this);
				if (sz.width < w) {
					return true;	// 実際の文字列サイズが表示幅より小さい場合は、折り返す
				}
			}
			return false;	// 折り返さない
		} else {
			return true;	// 折り返す
		}
	}
	
	class FocusRequester implements Runnable {
		public void run() {
			if (!ConsolePane.this.requestFocusInWindow()) {
				ConsolePane.this.requestFocus();
			}
		}
	}

	//------------------------------------------------------------
	// class Output writer
	//------------------------------------------------------------
	
	protected class ConsoleOutputWriter extends Writer {
		public ConsoleOutputWriter() {
			super();
		}
		@Override
		public void close() throws IOException {
			// 閉じる必要なし
		}
		@Override
		public void flush() throws IOException {
			// flush の必要なし
		}
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			appendOutputString(cbuf, off, len);
		}
	}

	//------------------------------------------------------------
	// class Error writer
	//------------------------------------------------------------
	
	protected class ConsoleErrorWriter extends Writer {
		public ConsoleErrorWriter() {
			super();
		}
		@Override
		public void close() throws IOException {
			// 閉じる必要なし
		}
		@Override
		public void flush() throws IOException {
			// flush の必要なし
		}
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			appendErrorString(cbuf, off, len);
		}
	}
}
