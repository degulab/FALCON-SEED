/*
 * @(#)ZipDecodeException.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

/**
 * ZIPファイル解凍時に発生する例外
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class ZipDecodeException extends RuntimeException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public ZipDecodeException() {
		super();
	}

	public ZipDecodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZipDecodeException(String message) {
		super(message);
	}

	public ZipDecodeException(Throwable cause) {
		super(cause);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
