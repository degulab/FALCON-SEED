/*
 * @(#)IDtContainerDocument.java	1.0.0	2022/12/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content;

import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;

/**
 * データコンテナのドキュメントデータが変更された等のイベントを通知するためのイベントハンドラ。
 * なお、ツリーノードの変更(データの置き換え)や、ツリーノードの構造変更などの場合は、このイベントハンドラは呼び出されない。
 * 
 * @version 1.0.0
 */
public interface IDtContainerElementDataChangeHandler
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Interfaces
	//------------------------------------------------------------
	
	/**
	 * ドキュメント内のデータコンテナ要素において、その要素内のデータが変更されたときに呼び出されるイベントハンドラ
	 * @param ndTarget	変更されたデータを保持するデータコンテナ要素のツリーノード
	 */
	public void onChangedElementData(IDtContainerContentTreeNode ndTarget);
}
