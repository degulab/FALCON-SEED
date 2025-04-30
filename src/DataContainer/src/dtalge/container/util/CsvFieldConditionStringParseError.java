package dtalge.container.util;

/**
 * CSVフィールド検索条件構文のパースエラーを示す例外。
 * 
 * @version 0.2.0
 * @since 0.2.0
 * 
 * @author H.Deguchi (Chiba University of Commerce)
 * @author Y.Ozaki (Mitzba Denyosha CO.,LTD.)
 * @author Y.Ishizuka(PieCake,Inc.)
 */
public class CsvFieldConditionStringParseError extends Exception
{
	private static final long serialVersionUID = -4143912421254085134L;
	
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** エラーの文字位置(文字先頭からのインデックス) **/
	private final int	_pos;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定されたパラメーターで、新しいインスタンスを生成する。
	 * @param pos	文字列のエラー発生位置
	 * @param message	詳細メッセージ
	 */
	public CsvFieldConditionStringParseError(int pos, String message)
	{
		super(message);
		_pos = pos;
	}

	/**
	 * 指定されたパラメーターで、新しいインスタンスを生成する。
	 * @param pos	文字列のエラー発生位置
	 * @param message	詳細メッセージ
	 * @param cause		要因を表す例外
	 */
	public CsvFieldConditionStringParseError(int pos, String message, Throwable cause) {
		super(message, cause);
		_pos = pos;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * エラー位置を示す文字列先頭からのインデックスを返す。
	 * @return エラー位置を示す文字列先頭からのインデックス
	 */
	public int getPosition() {
		return _pos;
	}

	/**
	 * メッセージの前に、文字位置(`[pos: #]`) を付加した文字列を取得する。
	 * メッセージが設定されていない場合は、エラーを示すメッセージが出力される。
	 * @return	文字位置を付加したエラーメッセージ
	 */
	public String toFormattedString() {
		String errmsg = getMessage();
		if (errmsg == null) {
			errmsg = "Invalid field condition.";
		}
		return ("[pos: " + _pos + "] " + errmsg);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
