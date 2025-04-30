/*
 * @(#)MongoRuntimeError.java	0.991	2019/02/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.db.mongo;

import redundantalge.db.BigAlgeError;

/**
 * MongoDB に対するオペレーションで発生する例外。
 * 
 * @version 0.991
 * @since 0.991
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public class MongoAlgeError extends BigAlgeError
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 4813025852959764694L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * パラメータが空の、新しいインスタンスを生成する。
	 */
	public MongoAlgeError() {
		super();
	}

	/**
	 * 指定されたパラメータを持つ、新しいインスタンスを生成する。
	 * @param message	メッセージ
	 * @param cause		要因
	 */
	public MongoAlgeError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 指定されたメッセージを持つ、新しいインスタンスを生成する。
	 * @param message	メッセージ
	 */
	public MongoAlgeError(String message) {
		super(message);
	}

	/**
	 * 指定された要因を持つ、新しいインスタンスを生成する。
	 * @param cause	要因
	 */
	public MongoAlgeError(Throwable cause) {
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
