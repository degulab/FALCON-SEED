/*
 * @(#)DbServerURI.java	0.991	2019/02/23
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package redundantalge.db;

import java.util.List;

import redundantalge.net.StorageURI;

/**
 * データベースをバックエンドとする大容量代数オブジェクトのストレージの場所に関するインタフェース。
 * <p>このオブジェクトの実装は、基本的に不変オブジェクトとする。
 * 
 * @version 0.991
 * @since 0.991
 * 
 * @author H.Deguchi(SOARS Project.)
 * @author Y.Ishizuka(PieCake.inc,)
 */
public interface DbServerURI extends StorageURI
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	/**
	 * 接続先データベースのアドレスリストを返す。
	 * @return	接続先データベースアドレスのリスト
	 */
	public List<DbServerAddress> getHosts();

	/**
	 * 接続先データベースのデータベース名を返す。
	 * @return	接続先のデータベース名
	 */
	public String getDatabaseName();

	/**
	 * 接続時のユーザー名を返す。
	 * @return	接続ユーザー名、設定されていない場合は <tt>null</tt>
	 */
	public String getUsername();

	/**
	 * 接続時のパスワードを返す。
	 * @return	接続パスワード、設定されていない場合は <tt>null</tt>
	 */
	public char[] getPassword();
}
