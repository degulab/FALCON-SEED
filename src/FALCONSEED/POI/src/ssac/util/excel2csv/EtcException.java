/*
 * @(#)EtcException.java	3.3.0	2016/04/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.excel2csv;

/**
 * <code>[Excel to CSV]</code> の例外。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class EtcException extends RuntimeException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 4306319683353788098L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public EtcException() {
		super();
	}

	public EtcException(String message, Throwable cause) {
		super(message, cause);
	}

	public EtcException(String message) {
		super(message);
	}

	public EtcException(Throwable cause) {
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
