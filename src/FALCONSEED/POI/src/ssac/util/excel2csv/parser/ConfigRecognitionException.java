/*
 * @(#)ConfigRecognitionException.java	3.3.0	2016/05/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.parser;

/**
 * <code>[Excel to CSV]</code> 変換定義解析時の例外。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class ConfigRecognitionException extends ConfigException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -5362776188260450883L;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 変換定義解析エラーの詳細情報 **/
	private final ConfigErrorDetail	_detail;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

//	public ConfigRecognitionException() {
//		super();
//	}
	
	public ConfigRecognitionException(ConfigErrorDetail detail) {
		super(detail.toString());
		_detail = detail;
	}
	
	public ConfigRecognitionException(ConfigErrorDetail detail, Throwable cause) {
		super(detail.toString(), cause);
		_detail = detail;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public ConfigErrorDetail getDetail() {
		return _detail;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
