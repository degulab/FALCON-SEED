/*
 * @(#)MongoSessionHandler.java	0.992	2020/03/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.db.mongo;

/**
 * MongoDB とのセッションに関するイベントハンドラ。
 * <b>注意：</b>
 * <blockquote>
 * このインタフェースは、異なるスレッドから呼び出される場合もあるため、インタフェース実装時は注意。
 * </blockquote>
 * 
 * @version 0.992
 * @since 0.992
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface MongoSessionHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	/**
	 * MongoDB サーバーとの接続が開始されたときに呼び出される。
	 * このメソッドが呼び出されても、MongoDB とのセッションが確立しているとは限らない。
	 * @param session	対象の MongoDB セッションオブジェクト
	 */
	public void serverOpened(MongoSession session);
	
	/**
	 * MongoDB サーバーとの接続が確立したときに呼び出される。
	 * @param session	対象の MongoDB セッションオブジェクト
	 */
	public void serverConnected(MongoSession session);
	
	/**
	 * MongoDB サーバーとのセッションが、予期せず切断されたときに呼び出される。
	 * @param session	対象の MongoDB セッションオブジェクト
	 * @param cause		切断要因となる例外が存在する場合はそのインスタンス、そうでない場合は <tt>null</tt>
	 */
	public void serverLostConnection(MongoSession session, Throwable cause);
	
	/**
	 * MongoDB サーバーとの接続が閉じられたときに呼び出される。
	 * 主に、意図的にセッションを閉じたときに、このメソッドが呼び出される。
	 * @param session	対象の MongoDB セッションオブジェクト
	 */
	public void serverClosed(MongoSession session);
}
