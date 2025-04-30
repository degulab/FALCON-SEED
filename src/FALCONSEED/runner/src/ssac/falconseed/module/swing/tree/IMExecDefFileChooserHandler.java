/*
 * @(#)IMExecDefFileChooserHandler.java	2.0.0	2012/11/01
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.tree;

import ssac.util.io.VirtualFile;

/**
 * ツリー形式のモジュール実行定義ファイル選択ダイアログから呼び出されるイベントハンドラ。
 * 
 * @version 2.0.0	2012/11/01
 */
public interface IMExecDefFileChooserHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ファイルが選択され[OK]ボタンが押された直後に呼び出される。
	 * @param source	呼び出し元となるオブジェクト。通常は <code>MExecDefFileChooser</code> オブジェクト。
	 * @param selectionFile		選択されたファイルを示す抽象パス
	 * @return 選択を受け入れる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public boolean acceptChooseFile(Object source, VirtualFile selectionFile);
}
