/*
 * @(#)EmptyModuleArgValidationHandler.java	1.22	2012/08/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

/**
 * モジュール実行時引数の正当性検査における空のイベントハンドラ。
 * このクラスの実装では、イベントに対し何も処理を行わない。
 * 
 * @version 1.22	2012/08/22
 * @since 1.22
 */
public class EmptyModuleArgValidationHandler implements IModuleArgValidationHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final EmptyModuleArgValidationHandler	_instance = new EmptyModuleArgValidationHandler();
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public EmptyModuleArgValidationHandler getInstance() {
		return _instance;
	}

	public void clearError() {}

	public void setError(String errmsg) {}
}
