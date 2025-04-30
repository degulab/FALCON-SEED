/*
 * @(#)ConfigTooManyErrorsException.java	3.3.0	2016/04/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.parser;


/**
 * 変換定義のエラー数が上限を超えたときの例外。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class ConfigTooManyErrorsException extends ConfigException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -7642165535901980930L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ConfigTooManyErrorsException() {
		super();
	}

	public ConfigTooManyErrorsException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigTooManyErrorsException(String message) {
		super(message);
	}

	public ConfigTooManyErrorsException(Throwable cause) {
		super(cause);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
