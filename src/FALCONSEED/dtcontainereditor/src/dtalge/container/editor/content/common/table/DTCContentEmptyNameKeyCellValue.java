/*
 * @(#)DTCContentEmptyNameKeyCellValue.java	1.1.0	2023/01/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.table;

import dtalge.container.editor.DtContainerEditorMessages;

/**
 * 詳細ビューの編集テーブルの名前キーセルの値が空で、それ以外の同行のセルの値が空ではない場合に、
 * その値と警告メッセージを保持するオブジェクト。
 * 
 * このオブジェクトは、不変とする。
 * 
 * @version 1.1.0
 */
public class DTCContentEmptyNameKeyCellValue
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/** 値もメッセージも可変ではないので、再利用可能な、このオブジェクトの唯一のインスタンス */
	static public final DTCContentEmptyNameKeyCellValue	instance = new DTCContentEmptyNameKeyCellValue(DtContainerEditorMessages.getInstance().msgWarnEmptyNameKeyAlthoughOtherCellHasValue);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** メッセージ **/
	private final String	_message;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたパラメータを保持する、新しいインスタンスを生成する。
	 * @param message	メッセージ
	 * @throws NullPointerException	<em>message</em> が {@code null} の場合
	 */
	public DTCContentEmptyNameKeyCellValue(String message) {
		if (message == null)
			throw new NullPointerException("Message is null");
		_message = message;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public String getMessage() {
		return _message;
	}
	
	@Override
	public String toString() {
		return "";
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
