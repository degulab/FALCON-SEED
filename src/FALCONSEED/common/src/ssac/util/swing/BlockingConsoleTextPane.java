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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)BlockingConsoleTextPane.java	3.0.0	2014/03/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.AbstractAction;
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

import ssac.aadl.common.CommonMessages;
import ssac.util.Strings;
import ssac.util.process.IOutputWriter;
import ssac.util.process.OutputString;
import ssac.util.swing.menu.AbMenuItemAction;
import ssac.util.swing.menu.MenuItemResource;

/**
 * テキストメッセージ表示用のテキストコンポーネント。
 * 主にサブプロセスの標準出力、標準エラー出力の内容を表示するためのコンポーネントであり、
 * 可能な限り同期的に出力処理を行うための機能を実装している。
 * 
 * @version 3.0.0	2014/03/26
 * @since 3.0.0
 */
public class BlockingConsoleTextPane extends JTextPane implements IOutputWriter
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 8349062748881140193L;
	
	static public final int INVALID_LINELENGTH_LIMIT = 0;
	static public final int MIN_LINELENGTH_LIMIT = 16;
	static public final int MAX_LINELENGTH_LIMIT = Integer.MAX_VALUE;
	
	static protected final int INVALID_LINECOUNT_LIMIT = 0;

	static protected final int		BLOCKINGQUEUE_CAPACITY	= 1;
	static protected final String	CONSOLE_NEWLINE	= "\n";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** コンソール出力用のキュー **/
	protected ArrayBlockingQueue<OutputString>	_queue;
	/** コンソール出力ランナー **/
	private OutputConsoleInvoker	_invoker;
	
	/** コンソール出力が有効であることを示すフラグ **/
	private volatile boolean	_queueingEnabled;

	/** フォーカスを設定するためのハンドラ **/
	private final FocusRequester _hFocusRequester = new FocusRequester();

	/** このコンポーネントのドキュメント **/
	private final DefaultStyledDocument	_document;
	/** エラー出力内容の表示属性 **/
	private final MutableAttributeSet		_attrForError;

	/** 文字列の折り返しを示すフラグ **/
	private boolean	_lineWrapped = false;

	/** ユーザー定義のコピーアクション **/
	private Action	_userCopyAction;
	/** ユーザー定義の全選択アクション **/
	private Action	_userSelectAllAction;

	/** 標準のコピーアクション **/
	private Action		_defaultCopyAction;
	/** 標準の全選択アクション **/
	private Action		_defaultSelectAllAction;
	/** 現在のコンテキストメニュー **/
	private JPopupMenu	_contextMenu;
	
//	protected final Action contextCopyAction;
//	protected final Action contextSelectAllAction;
//	protected final JPopupMenu contextMenu;

	/** 1行分の文字数上限 **/
	private volatile int _limitLineLength = INVALID_LINELENGTH_LIMIT;
	/** 行数上限 **/
	private volatile int _limitLineCount = INVALID_LINECOUNT_LIMIT;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public BlockingConsoleTextPane() {
		super(new DefaultStyledDocument());
		
		_queue = new ArrayBlockingQueue<OutputString>(BLOCKINGQUEUE_CAPACITY, true);
		_invoker = new OutputConsoleInvoker();
		_queueingEnabled = true;
		
		this._document = (DefaultStyledDocument)getDocument();
		this._attrForError = new SimpleAttributeSet();
		StyleConstants.setForeground(this._attrForError, Color.RED);
		//---
		setEditable(false);
		//setLineWrap(true);
		
//		// コンテキストメニューの作成
//		this.contextCopyAction = createContextCopyAction();
//		this.contextSelectAllAction = createContextSelectAllAction();
//		this.contextMenu = createContextMenu();

		// フォーカスによるキャレット制御
		addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				//--- キャレットを表示
				Caret caret = BlockingConsoleTextPane.this.getCaret();
				if (caret != null) {
					if (!caret.isVisible()) {
						caret.setVisible(true);
					}
				}
			}

			public void focusLost(FocusEvent e) {
				//--- キャレットを非表示
				Caret caret = BlockingConsoleTextPane.this.getCaret();
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
				getContextCopyAction().setEnabled(selected);
			}
		});
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean canCopy() {
		return (getSelectionEnd()!=0);
	}
	
	public void setCopyContextMenuResource(MenuItemResource miResource) {
		Action newAction = null;
		if (miResource != null) {
			newAction = new AbMenuItemAction(miResource){
				public void actionPerformed(ActionEvent e) {
					BlockingConsoleTextPane.this.copy();
					BlockingConsoleTextPane.this.setFocus();
				}
			};
		}
		setContextCopyAction(newAction);
	}
	
	public void setSelectAllContextMenuResource(MenuItemResource miResource) {
		Action newAction = null;
		if (miResource != null) {
			newAction = new AbMenuItemAction(miResource){
				public void actionPerformed(ActionEvent e) {
					BlockingConsoleTextPane.this.selectAll();
					BlockingConsoleTextPane.this.setFocus();
				}
			};
		}
		setContextSelectAllAction(newAction);
	}

	/**
	 * テキスト領域の行折り返しポリシーを帰す。
	 * @return	行が折り返される場合は <tt>true</tt>、折り返されない場合は <tt>false</tt>
	 */
	public boolean getLineWrap() {
		return _lineWrapped;
	}

	/**
	 * テキスト領域の行折り返しポリシーを設定する。
	 * <tt>true</tt> に設定すると、割り当て幅に収まりきらない長さの行は折り返される。
	 * <tt>false</tt> に設定すると、行は折り返されない。
	 * ポリシーを変更すると、<code>PropertyChange</code> イベント (lineWrap) が発生する。
	 * デフォルトでは、このプロパティは <tt>false</tt> となっている。
	 * 
	 * @param wrap	行を折り返すかどうかを示す
	 */
	public void setLineWrap(boolean wrap) {
		boolean old = _lineWrapped;
		_lineWrapped = wrap;
        firePropertyChange("lineWrap", old, wrap);
	}

	/**
	 * テキスト領域のテキストをクリアする。
	 */
	public void clear() {
		super.setText("");
	}

	@Override
	public void setText(String t) {
		super.setText(t);
	}

	/**
	 * このコンポーネントで保持するテキストの１行あたりの文字数上限を取得する。
	 * @return	設定されている上限文字数を返す。無制限の場合は 0 を返す。
	 */
	public int getLineLengthLimit() {
		return _limitLineLength;
	}

	/**
	 * このコンポーネントで保持するテキストの１行あたりの文字数上限を設定する。
	 * この文字数上限は、テキストの折り返しとは異なり、入力された文字列を複数行に
	 * 分割するためのものであり、すでにこのコンポーネントに設定されているテキストの
	 * 内容を変更するものではない。
	 * @param limit	文字数上限を指定する。0 以下の値が指定された場合は無制限となる
	 */
	public void setLineLengthLimit(int limit) {
		if (limit <= INVALID_LINELENGTH_LIMIT) {
			// ignore limit
			_limitLineLength = INVALID_LINELENGTH_LIMIT;
		}
		else if (limit < MIN_LINELENGTH_LIMIT || limit > MAX_LINELENGTH_LIMIT) {
			throw new IllegalArgumentException("Illegal limit : " + limit);
		}
		else {
			_limitLineLength = limit;
		}
	}

	/**
	 * このコンポーネントで保持するテキストの行数上限を取得する。
	 * @return	設定されている上限行数を返す。無制限の場合は 0 を返す。
	 */
	public int getLineCountLimit() {
		return _limitLineCount;
	}

	/**
	 * このコンポーネントで保持するテキストの行数上限を設定する。
	 * @param limit	行数上限を指定する。0 以下の値が指定された場合は無制限となる
	 */
	public void setLineCountLimit(int limit) {
		if (limit <= INVALID_LINECOUNT_LIMIT) {
			// ignore limit
			_limitLineCount = INVALID_LINECOUNT_LIMIT;
		}
		else {
			_limitLineCount = limit;
		}
	}
	
	public void appendOutputString(String str) {
		append(str, null);
	}
	
	public void appendErrorString(String str) {
		append(str, _attrForError);
	}

	/**
	 * このコンポーネントにフォーカスを設定する。
	 */
	public void setFocus() {
		//--- このメソッドは、フォーカスの状態に関わらず、フォーカス設定を実行
		SwingUtilities.invokeLater(_hFocusRequester);
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

	/**
	 * このコンポーネントが保持するテキストの終端へ、指定されたテキストを追加する。
	 * @param strText	追加する文字列
	 * @param attr		文字列に適用する属性
	 */
	public void append(String strText, AttributeSet attr) {
		// 制限行数へクリップ
		clipToLineCountLimit();
		
		if (Strings.isNullOrEmpty(strText)) {
			return;
		}
		
		// 文字列追加
		int lengthLimit = _limitLineLength;
		if (lengthLimit > 0) {
			// １行を指定の文字数に収める。制限文字数を超える場合は、改行を埋め込む。
			int len = strText.length();
			int spos = 0;
			do {
				int remain = lengthLimit - getFinalLineCharCount();
				if (remain > 0) {
					// 残文字数分のテキストを追加
					int epos = remain + spos;
					if (epos > len) {
						epos = len;
					}
					appendOne(strText.substring(spos, epos), attr);
					spos = epos;
				} else {
					// 改行
					appendOne("\n", null);
				}
			} while (spos < len);
		} else {
			// 制限文字数に関係なく、テキストをドキュメントに追加
			appendOne(strText, attr);
		}
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		if (!_lineWrapped) {
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

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void setContextCopyAction(Action newAction) {
		_userCopyAction = newAction;
		clearConsoleContextMenuCache();
	}

	protected void setContextSelectAllAction(Action newAction) {
		_userSelectAllAction = newAction;
		clearConsoleContextMenuCache();
	}
	
	protected Action getContextCopyAction() {
		if (_userCopyAction == null) {
			return getDefaultContextCopyAction();
		}
		return _userCopyAction;
	}
	
	protected Action getContextSelectAllAction() {
		if (_userSelectAllAction == null) {
			return getDefaultContextSelectAllAction();
		}
		return _userSelectAllAction;
	}
	
	protected Action getDefaultContextCopyAction() {
		if (_defaultCopyAction == null) {
			Action action = new AbstractAction(CommonMessages.getInstance().Button_Copy) {
				public void actionPerformed(ActionEvent e) {
					BlockingConsoleTextPane.this.copy();
					BlockingConsoleTextPane.this.setFocus();
				}
			};
			_defaultCopyAction = action;
		}
		return _defaultCopyAction;
	}
	
	protected Action getDefaultContextSelectAllAction() {
		if (_defaultSelectAllAction == null) {
			Action action = new AbstractAction(CommonMessages.getInstance().Button_AllSelect) {
				public void actionPerformed(ActionEvent e) {
					BlockingConsoleTextPane.this.selectAll();
					BlockingConsoleTextPane.this.setFocus();
				}
			};
			_defaultSelectAllAction = action;
		}
		return _defaultSelectAllAction;
	}
	
	protected void clearConsoleContextMenuCache() {
		_contextMenu = null;
	}
	
	protected JPopupMenu getConsoleContextMenu() {
		if (_contextMenu == null) {
			_contextMenu = createConsoleContextMenu();
		}
		return _contextMenu;
	}
	
	protected JPopupMenu createConsoleContextMenu() {
		JPopupMenu menu = new JPopupMenu();
		
		//--- copy
		menu.add(new JMenuItem(getContextCopyAction()));
		//---
		menu.addSeparator();
		//--- select all
		menu.add(new JMenuItem(getContextSelectAllAction()));
		
		return menu;
	}

	/**
	 * 現在の最終行の文字数を取得する。
	 */
	protected int getFinalLineCharCount() {
		int targetLine = getLineCount() - 1;
		if (targetLine < 0)
			targetLine = 0;
		int flc = 0;
		try {
			flc = getLineEndOffset(targetLine) - getLineStartOffset(targetLine);
		} catch (BadLocationException ignoreEx) {}
		return flc;
	}

	/**
	 * 現在のテキストの行数を、上限内にクリップする。
	 */
	protected void clipToLineCountLimit() {
		if (_limitLineCount > 0) {
			int removeLines = getLineCount() - _limitLineCount;
			if (removeLines > 0) {
				try {
					int spos = getLineStartOffset(0);
					int epos = getLineEndOffset(removeLines-1);
					_document.remove(spos, (epos-spos));
				} catch (BadLocationException ignoreEx) {}
			}
		}
	}
	
	protected void appendText(String strText, AttributeSet attr) {
	}
	
	protected void appendOne(String strText, AttributeSet attr) {
		try {
			boolean retry = false;
			try {
				_document.insertString(_document.getLength(), strText, attr);
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
				_limitLineCount = getLineCount() - removeLines;
//				AppLogger.debug("Update Console line limit = " + _limitLineCount);
				//--- 文字列追加をリトライ
				retry = true;
			}

			// 再試行
			if (retry) {
				//--- 制限行数内に収める
				clipToLineCountLimit();
				//--- 文字を追加
				_document.insertString(_document.getLength(), strText, attr);
			}
		}
		catch (BadLocationException ex) {ex=null;}
	}
	
	protected void evaluateContextPopupMenu(MouseEvent me) {
		// コンテキストメニュー表示のトリガ検証
		if (!me.isPopupTrigger())
			return;
		
		// ポップアップメニューの表示
		JPopupMenu pmenu = getConsoleContextMenu();
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
		append(str, _attrForError);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * このコンポーネントにフォーカスを設定するための <code>Runnable</code> クラス。
	 */
	class FocusRequester implements Runnable {
		public void run() {
			if (!BlockingConsoleTextPane.this.requestFocusInWindow()) {
				BlockingConsoleTextPane.this.requestFocus();
			}
		}
	}

	/**
	 * EventDispatchThread 以外からのアクセス時に実行するタスク
	 */
	protected class OutputConsoleInvoker implements Runnable
	{
		@Override
		public void run() {
			OutputString ostr = _queue.poll();
			if (ostr != null) {
				doOutput(ostr);
			}
		}
	}
	
	public boolean getQueueingEnabled() {
		return _queueingEnabled;
	}
	
	public void setQueueingEnabled(boolean toEnable) {
		_queueingEnabled = toEnable;
		if (!_queueingEnabled) {
			_queue.clear();
		}
	}

	/**
	 * キューが空になるまでキューの内容を出力する。
	 * このメソッドの呼び出しもとが EventDispatchThread ではない場合、キューの内容を空になるまで破棄する。
	 */
	public void flushUntilQueueEmpty() {
		if (SwingUtilities.isEventDispatchThread()) {
			for (OutputString qstr=_queue.poll(); qstr != null; qstr=_queue.poll()) {
				doOutput(qstr);
			}
		}
		else {
			for (; !_queue.isEmpty(); ) {
				_queue.clear();
			}
		}
	}

	//------------------------------------------------------------
	// Implement ssac.util.process.ICommandOutputBroker interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトでは特別なロックオブジェクトは使用しないため、常に <tt>null</tt> を返す。
	 * @return	常に <tt>null</tt>
	 */
	@Override
	public Object getLockObject() {
		return null;
	}

	@Override
	public boolean checkError() {
		return false;
	}

	@Override
	public void flush() {
		flushQueue();
	}

	@Override
	public void close() {
		_queueingEnabled = false;	// close
		flushQueue();
	}

	@Override
	public void print(boolean isError, char c) {
		if (_queueingEnabled) {
			OutputString ostr = new OutputString(isError, String.valueOf(c));
			putToQueue(ostr);
		}
	}

	@Override
	public void print(boolean isError, String str) {
		if (_queueingEnabled) {
			OutputString ostr = new OutputString(isError, (str==null ? "null" : str));
			putToQueue(ostr);
		}
	}

	@Override
	public void print(boolean isError, Object obj) {
		if (_queueingEnabled) {
			OutputString ostr = new OutputString(isError, String.valueOf(obj));
			putToQueue(ostr);
		}
	}

	@Override
	public void printf(boolean isError, String format, Object... args) {
		if (_queueingEnabled) {
			OutputString ostr = new OutputString(isError, String.format(format, args));
			putToQueue(ostr);
		}
	}

	@Override
	public void printf(boolean isError, Locale l, String format, Object... args) {
		if (_queueingEnabled) {
			OutputString ostr = new OutputString(isError, String.format(l, format, args));
			putToQueue(ostr);
		}
	}

	@Override
	public void println(boolean isError, char c) {
		if (_queueingEnabled) {
			OutputString ostr = new OutputString(isError, String.valueOf(c).concat(CONSOLE_NEWLINE));
			putToQueue(ostr);
		}
	}

	@Override
	public void println(boolean isError, String str) {
		if (_queueingEnabled) {
			if (str == null) {
				str = "null".concat(CONSOLE_NEWLINE);
			} else {
				str = str.concat(CONSOLE_NEWLINE);
			}
			OutputString ostr = new OutputString(isError, str);
			putToQueue(ostr);
		}
	}

	@Override
	public void println(boolean isError, Object obj) {
		if (_queueingEnabled) {
			OutputString ostr = new OutputString(isError, String.valueOf(obj).concat(CONSOLE_NEWLINE));
			putToQueue(ostr);
		}
	}

	@Override
	public void printfln(boolean isError, String format, Object... args) {
		if (_queueingEnabled) {
			OutputString ostr = new OutputString(isError, String.format(format, args).concat(CONSOLE_NEWLINE));
			putToQueue(ostr);
		}
	}

	@Override
	public void printfln(boolean isError, Locale l, String format, Object... args) {
		if (_queueingEnabled) {
			OutputString ostr = new OutputString(isError, String.format(l, format, args).concat(CONSOLE_NEWLINE));
			putToQueue(ostr);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Implement ssac.util.process.OutputConsoleHandler interfaces
	//------------------------------------------------------------
	
	protected void doOutput(OutputString ostr) {
		if (ostr.isError())
			appendErrorString(ostr.getString());
		else
			appendOutputString(ostr.getString());
	}
	
	protected void flushQueue() {
		if (SwingUtilities.isEventDispatchThread()) {
			// 最大でキュー容量分の要素数までキューの内容を出力する
			int cnt = 0;
			for (OutputString qstr=_queue.poll(); qstr != null && cnt < BLOCKINGQUEUE_CAPACITY; qstr=_queue.poll(), ++cnt) {
				doOutput(qstr);
			}
		}
		else {
			if (!_queue.isEmpty()) {
				// invoke
				SwingUtilities.invokeLater(_invoker);
			}
		}
	}
	
	protected void putToQueue(OutputString ostr) {
		if (SwingUtilities.isEventDispatchThread()) {
			// 最大でキュー容量分の要素数までキューの内容を出力してから、メッセージを直接出力
			int cnt = 0;
			for (OutputString qstr=_queue.poll(); qstr != null && cnt < BLOCKINGQUEUE_CAPACITY; qstr=_queue.poll(), ++cnt) {
				doOutput(qstr);
			}
			doOutput(ostr);
		}
		else {
			// キューが一杯ならブロッキング
			try {
				// put and blocking
				_queue.put(ostr);
				// invoke
				SwingUtilities.invokeLater(_invoker);
			}
			catch (InterruptedException ex) {
				_queueingEnabled = false;	// stop output to console
				_queue.clear();
				Thread.currentThread().interrupt();
			}
		}
	}
}
