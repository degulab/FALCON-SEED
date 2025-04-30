/*
 * @(#)ConfigSheetNotFoundException.java	3.3.0	2016/04/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv.parser;

/**
 * Excel ファイルに変換定義シートが存在しないことを示す例外。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class ConfigSheetNotFoundException extends ConfigException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 3094565397279441967L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ConfigSheetNotFoundException() {
		super();
	}

	public ConfigSheetNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigSheetNotFoundException(String message) {
		super(message);
	}

	public ConfigSheetNotFoundException(Throwable cause) {
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
