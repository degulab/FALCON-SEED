/*
 * @(#)FileOperationException.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.io.File;

/**
 * ファイル操作に関する例外
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class FileOperationException extends RuntimeException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final int UNKNOWN	= 0;
	static public final int CREATE	= 1;
	static public final int MKDIR		= 2;
	static public final int DELETE	= 3;
	static public final int COPY		= 4;
	static public final int MOVE		= 5;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** エラー発生時のソース **/
	private final File	_sourceFile;
	/** エラー発生時のターゲット **/
	private final File	_targetFile;
	/** エラー発生時の操作種別 **/
	private final int	_opType;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public FileOperationException(int operation, File source, File target)
	{
		super();
		this._opType     = operation;
		this._sourceFile = source;
		this._targetFile = target;
	}
	
	public FileOperationException(int operation, File source, File target,
									String message)
	{
		super(message);
		this._opType     = operation;
		this._sourceFile = source;
		this._targetFile = target;
	}
	
	public FileOperationException(int operation, File source, File target,
									Throwable cause)
	{
		super(cause);
		this._opType     = operation;
		this._sourceFile = source;
		this._targetFile = target;
	}
	
	public FileOperationException(int operation, File source, File target,
									String message, Throwable cause)
	{
		super(message, cause);
		this._opType     = operation;
		this._sourceFile = source;
		this._targetFile = target;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getOperationType() {
		return _opType;
	}
	
	public File getSourceFile() {
		return _sourceFile;
	}
	
	public File getTargetFile() {
		return _targetFile;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName());
		sb.append(" : type[");
		sb.append(_opType);
		sb.append("] : source[");
		sb.append(_sourceFile);
		sb.append("] : target[");
		sb.append(_targetFile);
		sb.append("]");
		
		String message = getLocalizedMessage();
		if (message != null) {
			sb.append(" : ");
			sb.append(message);
		}
		
		return sb.toString();
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
