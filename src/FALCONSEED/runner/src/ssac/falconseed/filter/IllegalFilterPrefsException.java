/*
 * @(#)IllegalFilterPrefsException.java	3.2.0	2015/06/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter;

/**
 * フィルタ定義に関するエラーを通知する例外。
 * @version 3.2.0
 * @since 3.2.0
 */
public class IllegalFilterPrefsException extends RuntimeException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 2970069403878101982L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public IllegalFilterPrefsException() {
		super();
	}

	public IllegalFilterPrefsException(String message) {
		super(message);
	}

	public IllegalFilterPrefsException(Throwable cause) {
		super(cause);
	}

	public IllegalFilterPrefsException(String message, Throwable cause) {
		super(message, cause);
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
