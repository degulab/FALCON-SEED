/*
 * @(#)FileAccessException.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.io.File;

/**
 * ファイルアクセスに失敗した場合の例外のラッパー。
 * この例外では、失敗した操作の対象である <code>File</code> オブジェクトを保持する。
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class FileAccessException extends RuntimeException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final File _targetFile;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FileAccessException(File targetFile) {
		super();
		this._targetFile = targetFile;
	}
	
	public FileAccessException(File targetFile, String message) {
		super(message);
		this._targetFile = targetFile;
	}
	
	public FileAccessException(File targetFile, Throwable cause) {
		super(cause);
		this._targetFile = targetFile;
	}
	
	public FileAccessException(File targetFile, String message, Throwable cause) {
		super(message, cause);
		this._targetFile = targetFile;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean hasTarget() {
		return (_targetFile != null);
	}
	
	public File getTarget() {
		return _targetFile;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String message = getLocalizedMessage();
		sb.append(getClass().getName());
		if (message != null) {
			sb.append(": " + message);
		}
		if (_targetFile != null) {
			sb.append(": \"" + _targetFile.toString() + "\"");
		}
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
