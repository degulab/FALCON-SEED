/*
 * @(#)IFilterArgEditHandler.java	2.0.0	2012/10/28
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;


/**
 * フィルタ定義引数編集用データモデルの内容が変更されたときに呼び出されるハンドラ。
 * 
 * @version 2.0.0	2012/10/28
 * @since 2.0.0
 */
public interface IFilterArgEditHandler
{
	public boolean isReferencedValue(final Object refValue);
	
	public void onRemovedArgument(FilterArgEditModel deletedArgData);
	
	public void onChangedArgument(int firstArgIndex, int lastArgIndex);
}
