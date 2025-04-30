/*
 * @(#)ZipDecodeOperationException.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.io.File;

/**
 * ZIPファイル解凍によるファイル操作時に発生する例外
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class ZipDecodeOperationException extends ZipDecodeException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final int UNKNOWN	= 0;
	static public final int CREATE	= 1;
	static public final int MKDIR		= 2;
	static public final int WRITE		= 3;
	static public final int RENAME	= 4;
	static public final int DELETE	= 5;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** エラー発生時のソースとなるエントリ名 **/
	private final String		_srcEntryName;
	/** エラー発生時のターゲット **/
	private final File			_dstFile;
	/** エラー発生時の操作種別 **/
	private final int	_opType;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ZipDecodeOperationException(int operation, String srcEntryName, File dstFile)
	{
		super();
		this._opType     = operation;
		this._srcEntryName = srcEntryName;
		this._dstFile = dstFile;
	}
	
	public ZipDecodeOperationException(int operation, String srcEntryName, File dstFile,
									String message)
	{
		super(message);
		this._opType     = operation;
		this._srcEntryName = srcEntryName;
		this._dstFile = dstFile;
	}
	
	public ZipDecodeOperationException(int operation, String srcEntryName, File dstFile,
									Throwable cause)
	{
		super(cause);
		this._opType     = operation;
		this._srcEntryName = srcEntryName;
		this._dstFile = dstFile;
	}
	
	public ZipDecodeOperationException(int operation, String srcEntryName, File dstFile,
									String message, Throwable cause)
	{
		super(message, cause);
		this._opType     = operation;
		this._srcEntryName = srcEntryName;
		this._dstFile = dstFile;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getOperationType() {
		return _opType;
	}
	
	public String getSourceEntryName() {
		return _srcEntryName;
	}
	
	public File getDestinationFile() {
		return _dstFile;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append(" : type[");
		sb.append(_opType);
		sb.append("] : source[");
		sb.append(_srcEntryName);
		sb.append("] : target[");
		sb.append(_dstFile.getPath());
		sb.append("]");
		
		String message = getLocalizedMessage();
		if (message != null) {
			sb.append(" : ");
			sb.append(message);
		}
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
