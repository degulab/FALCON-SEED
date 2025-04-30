/*
 * @(#)IModuleArgValue.java	1.22	2012/08/10
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import ssac.aadl.module.ModuleArgType;
import ssac.falconseed.module.args.IMExecArgParam;

/**
 * モジュール実行時の引数値のインタフェース。
 * 
 * @version 1.22	2012/08/10
 * @since 1.22
 */
public interface IModuleArgValue
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------

	/**
	 * 引数種別を返す。
	 */
	public ModuleArgType getType();

	/**
	 * 引数説明を返す。
	 */
	public String getDescription();

	/**
	 * 実行時の引数値が固定されているかを判定する。
	 * @return	実行時引数が固定されている場合は <tt>true</tt>、変更可能な場合は <tt>false</tt>
	 */
	public boolean isFixedValue();

	/**
	 * 引数定義のパラメータ種別を返す。
	 * @return	実行時可変の場合は <code>IMExecArgParam</code> オブジェクト、実行時固定の場合は <tt>null</tt>
	 */
	public IMExecArgParam getParameterType();

	/**
	 * 引数値が設定されているかを判定する。
	 * @return	引数値が <tt>null</tt> ではない場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean hasValue();

	/**
	 * 引数の値を取得する。
	 * @return	引数の値
	 */
	public Object getValue();
}
