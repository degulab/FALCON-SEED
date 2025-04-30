/*
 * @(#)DtContainerContentInvalidCellValue.java	1.1.0	2023/01/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerContentInvalidCellValue.java	1.0.0	2022/12/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.table;

import java.util.Objects;

import dtalge.container.editor.DtContainerEditorMessages;

/**
 * 詳細ビューの編集テーブルのセル値が不正の場合に、その値とエラーメッセージを保持するオブジェクト。
 * 
 * このオブジェクトは、不変とする。
 * 
 * @version 1.1.0
 */
public class DTCContentInvalidCellValue
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	/**
	 * 名前キーが空で、それ以外の列の値が空ではない場合のエラーを示す、このオブジェクトの再利用可能なインスタンス
	 * @since 1.1.0
	 */
	static public final DTCContentInvalidCellValue EMPTY_NAME_CELL_VALUE	= new DTCContentInvalidCellValue(null, DtContainerEditorMessages.getInstance().msgErrorEmptyNameKeyAlthoughOtherCellHasValue);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	/** 対象の値 **/
	private final Object	_srcValue;
	/** メッセージ(テーブルセルのツールチップ) **/
	private final String	_message;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	/**
	 * 指定されたパラメータを保持する、新しいインスタンスを生成する。
	 * @param value		値
	 * @param message	メッセージ
	 * @throws NullPointerException	<em>message</em> が {@code null} の場合
	 */
	public DTCContentInvalidCellValue(Object value, String message) {
		if (message == null)
			throw new NullPointerException("Message is null");
		_srcValue = value;
		_message = message;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public Object getValue() {
		return _srcValue;
	}
	
	public String getMessage() {
		return _message;
	}

	//------------------------------------------------------------
	// Implement java.lang.Object interfaces
	//------------------------------------------------------------
	
	@Override
	public String toString() {
		return (_srcValue==null ? "" : _srcValue.toString());
	}

	@Override
	public int hashCode() {
		int h = (_srcValue==null ? 0 : _srcValue.hashCode());
		h = 31 * h + (_message==null ? 0 : _message.hashCode());
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj.getClass().equals(this.getClass())) {
			DTCContentInvalidCellValue aValue = (DTCContentInvalidCellValue)obj;
			
			if (Objects.equals(aValue._srcValue, this._srcValue) && Objects.equals(aValue._message, this._message)) {
				return true;
			}
		}
		
		// not equals
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
