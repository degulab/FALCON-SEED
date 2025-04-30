/*
 * @(#)OutputLogUtil.java	3.0.0	2014/03/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.process;

/**
 * 標準出力、標準エラー出力用ログファイルのユーティリティクラス。
 * 
 * @version 3.0.0	2014/03/11
 * @since 3.0.0
 */
public class OutputLogUtil
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** ログファイルのフィールド区切り文字 **/
	static public final char	FIELD_DELIMITER_CHAR	= ',';
	/** エスケープ用クオート文字 **/
	static public final char	FIELD_ENQUOTE_CHAR		= '"';
	/** ログファイルのレコード区切り文字 **/
	static public final String	RECORD_DELIMITER	= "\r\n";

	/** ログ種別：標準出力 **/
	static public final String	LOGTYPE_STDOUT		= "1";
	/** ログ種別：標準エラー出力 **/
	static public final String	LOGTYPE_STDERR		= "2";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private OutputLogUtil() {}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
