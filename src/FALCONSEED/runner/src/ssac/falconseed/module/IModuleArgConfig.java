/*
 * @(#)IModuleArgConfig.java	2.0.0	2012/10/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)IModuleArgConfig.java	1.22	2012/08/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;


/**
 * モジュール実行時の引数設定値のインタフェース。
 * 
 * @version 2.0.0	2012/10/16
 * @since 1.22
 */
public interface IModuleArgConfig extends IModuleArgValue
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * この引数の番号を取得する。
	 * @return	1から始まる引数番号
	 */
	public int getArgNo();
	
	/**
	 * この引数の番号を設定する。
	 * @param argno	1から始まる引数番号
	 * @since 2.0.0
	 */
	public void setArgNo(int argno);

	/**
	 * 引数説明を設定する。
	 * @param desc	新しい説明の文字列
	 */
	public void setDescription(String desc);

	/**
	 * 引数の値を設定する。
	 * @param newValue	新しい引数の値
	 */
	public void setValue(Object newValue);

	/**
	 * テンポラリファイルへの出力設定を取得する。
	 * @return	テンポラリファイルへ出力する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public boolean getOutToTempEnabled();

	/**
	 * テンポラリファイルへの出力を設定する。
	 * @param toEnable	テンポラリファイルへ出力する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 */
	public void setOutToTempEnabled(boolean toEnable);

	/**
	 * テンポラリファイルのプレフィックスが指定されているかを判定する。
	 * @return	プレフィックスが指定されている場合は <tt>true</tt>、指定されていない場合は <tt>false</tt>
	 */
	public boolean hasTempFilePrefix();

	/**
	 * 指定されたテンポラリファイル用プレフィックスを取得する。
	 * @return	プレフィックスが指定されている場合はその文字列、指定されていない場合は <tt>null</tt>
	 */
	public String getTempFilePrefix();

	/**
	 * テンポラリファイル用プレフィックスを設定する。
	 * @param prefix	テンポラリファイルのプレフィックス、指定しない場合は <tt>null</tt> もしくは長さが 0 の文字列
	 */
	public void setTempFilePrefix(String prefix);

	/**
	 * 実行完了後にファイルを表示するかどうかの設定を取得する。
	 * @return	表示する場合は <tt>true</tt>、表示しない場合は <tt>false</tt>
	 */
	public boolean getShowFileAfterRun();

	/**
	 * 実行完了後にファイルを表示するかどうかを設定する。
	 * @param toShow	表示する場合は <tt>true</tt>、表示しない場合は <tt>false</tt>
	 */
	public void setShowFileAfterRun(boolean toShow);
}
