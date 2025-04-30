/*
 * @(#)FilterErrorType	3.1.0	2014/05/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

/**
 * フィルタに関するエラーの種類を定義する列挙型オブジェクト。
 * 
 * @version 3.1.0	2014/05/19
 * @since 3.1.0
 */
public enum FilterErrorType
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** エラー種別が不明であることを示す **/
	UNKNOWN,
	/** 待機フィルタに関するエラーであることを示す **/
	FILTER_WAITFILTERS,
}
