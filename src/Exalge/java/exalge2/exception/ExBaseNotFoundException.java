/*
 * @(#)ExBaseNotFoundException.java	0.91	2007/08/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ExBaseNotFoundException.java	0.90	2007/07/25
 *     - created by Y.Ishizuka(PieCake.inc,)
 */

package exalge2.exception;

/**
 * アクセス時に指定した交換代数基底が存在しない場合にスローされる。
 *
 * @version 0.91 2007/07/25
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 * 
 * @since 0.90
 *
 */
public class ExBaseNotFoundException extends RuntimeException
{
	/**
	 * 詳細メッセージを持たない <code>ExBaseNotFoundException</code> を構築する。
	 */
	public ExBaseNotFoundException() {
		super();
	}

	/**
	 * 指定された詳細メッセージを持つ <code>ExBaseNotFoundException</code> を構築する。
	 * 
	 * @param s 詳細メッセージ
	 */
	public ExBaseNotFoundException(String s) {
		super(s);
	}

	/**
	 * 指定された詳細メッセージおよび原因を使用して新規例外を構築する。
	 * 
	 * <p><code>cause</code> と関連付けられた詳細メッセージが、この例外の
	 * 詳細メッセージに自動的に統合されることはない。
	 * 
	 * @param message 詳細メッセージ。{@link Throwable#getMessage()} メソッドによる
	 * 				   取得用に保存される。
	 * @param cause 原因。あとで {@link Throwable#getCause()} メソッドで取得するために
	 * 				 保存される。<tt>null</tt> が許可されており、原因が存在しないか不明で
	 * 				 あることを示す。
	 */
	public ExBaseNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * <code>(cause==null ? null : cause.toString())</code> の指定された原因および
	 * 詳細メッセージを使用して新規例外を構築する。
	 * 
	 * <p>通常 <tt>(cause==null ? null : cause.toString())</tt> には、<tt>cause</tt> の
	 * クラスおよび詳細メッセージが含まれる。
	 * <br>
	 * このコンストラクタは、例外が他のスロー可能オブジェクトのラッパーである場合に有用である。
	 * 
	 * @param cause 原因。あとで {@link Throwable#getCause()} メソッドで取得するために
	 * 				 保存される。<tt>null</tt> が許可されており、原因が存在しないか不明で
	 * 				 あることを示す。
	 */
	public ExBaseNotFoundException(Throwable cause) {
		super(cause);
	}

}
