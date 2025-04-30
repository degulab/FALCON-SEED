/*
 * @(#)IDialogResult.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

/**
 * ダイアログの戻り値を提供するインタフェース。
 * 
 * @version 1.00 2008/03/24
 */
public interface IDialogResult {
	public static final int DialogResult_None = 0;
	public static final int DialogResult_Cancel = (-1);
	public static final int DialogResult_OK = 1;

	public int getDialogResult();
}
