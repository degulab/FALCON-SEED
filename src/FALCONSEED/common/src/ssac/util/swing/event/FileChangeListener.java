/*
 * @(#)FileChangeListener.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing.event;

import java.util.EventListener;

/**
 * <code>File</code> オブジェクトの抽象パスが示すファイルに
 * 変更があったことを通知するイベントを受け取るリスナーインタフェース
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.14
 */
public interface FileChangeListener extends EventListener
{
	public void fileChanged(FileChangeEvent e);
}
