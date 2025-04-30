/*
 * @(#)CompileException.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc;


/**
 * AADL コンパイラー例外。
 * <br>
 * AADLコンパイル時に発生するエラー情報として、行番号やカラム番号と
 * ともに、エラーメッセージを保持する。
 * 
 * @version 1.00	2007/11/29
 *
 */
public class CompileException extends RuntimeException
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final int lineNo;
	private final int columnNo;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CompileException(String message) {
		this(-1, -1, message);
	}
	
	public CompileException(Throwable cause) {
		this(-1, -1, cause.getMessage(), cause);
	}
	
	public CompileException(int lineNo, String message) {
		this(lineNo, -1, message);
	}
	
	public CompileException(int lineNo, int columnNo, String message) {
		super(makeMessage(lineNo, columnNo, message));
		this.lineNo = lineNo;
		this.columnNo = columnNo;
	}
	
	public CompileException(String message, Throwable cause) {
		this(-1, -1, message, cause);
	}
	
	public CompileException(int lineNo, Throwable cause) {
		this(lineNo, -1, cause.getMessage(), cause);
	}
	
	public CompileException(int lineNo, int columnNo, Throwable cause) {
		this(lineNo, columnNo, cause.getMessage(), cause);
	}
	
	public CompileException(int lineNo, String message, Throwable cause) {
		this(lineNo, -1, message, cause);
	}
	
	public CompileException(int lineNo, int columnNo, String message, Throwable cause) {
		super(makeMessage(lineNo, columnNo, message), cause);
		this.lineNo = lineNo;
		this.columnNo = columnNo;
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	public boolean hasLineNo() {
		return (this.lineNo >= 0);
	}
	
	public boolean hasColumnNo() {
		return (this.columnNo >= 0);
	}
	
	public int getLineNo() {
		return this.lineNo;
	}
	
	public int getColumnNo() {
		return this.columnNo;
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	static protected String makeMessage(int lineNo, int columnNo, String message) {
		StringBuffer sb = new StringBuffer();
		if (lineNo >= 0 || columnNo >= 0) {
			if (lineNo >= 0) {
				sb.append(lineNo);
				sb.append(":");
			}
			if (columnNo >= 0) {
				sb.append(columnNo);
				sb.append(":");
			}
		}
		sb.append(message);
		return sb.toString();
	}
}
