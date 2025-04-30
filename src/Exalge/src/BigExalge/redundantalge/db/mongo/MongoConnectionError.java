/*
 * @(#)MongoConnectionError.java	0.992	2020/03/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.db.mongo;

/**
 * MongoDB サーバーへの接続時に発生する例外。
 * この例外の要因は、<code>getCause()</code> で取得できる。
 * 
 * @version 0.992
 * @since 0.992
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoConnectionError extends RuntimeException
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 4272315024334670061L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * パラメータが空の、新しいインスタンスを生成する。
	 */
	public MongoConnectionError() {
		super();
	}

	/**
	 * 指定されたパラメータを持つ、新しいインスタンスを生成する。
	 * @param message	メッセージ
	 * @param cause		要因
	 */
	public MongoConnectionError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 指定されたメッセージを持つ、新しいインスタンスを生成する。
	 * @param message	メッセージ
	 */
	public MongoConnectionError(String message) {
		super(message);
	}

	/**
	 * 指定された要因を持つ、新しいインスタンスを生成する。
	 * @param cause	要因
	 */
	public MongoConnectionError(Throwable cause) {
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
