/*
 * @(#)DbExalgeError.java	0.991	2019/02/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.db;

/**
 * データベースをバックエンドとする大容量オブジェクトに関する実行時エラー。
 * 
 * @version 0.991
 * @since 0.991
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class BigAlgeError extends RuntimeException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -670778298468890327L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * パラメータが空の、新しいインスタンスを生成する。
	 */
	public BigAlgeError() {
		super();
	}

	/**
	 * 指定されたパラメータを持つ、新しいインスタンスを生成する。
	 * @param message	メッセージ
	 * @param cause		要因
	 */
	public BigAlgeError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 指定されたメッセージを持つ、新しいインスタンスを生成する。
	 * @param message	メッセージ
	 */
	public BigAlgeError(String message) {
		super(message);
	}

	/**
	 * 指定された要因を持つ、新しいインスタンスを生成する。
	 * @param cause	要因
	 */
	public BigAlgeError(Throwable cause) {
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
