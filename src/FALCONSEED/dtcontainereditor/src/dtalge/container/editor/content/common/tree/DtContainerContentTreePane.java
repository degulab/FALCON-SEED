/*
 * @(#)DtContainerContentTreePane.java	1.0.0	2022/12/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.tree;

import javax.swing.SwingUtilities;

import ssac.util.swing.tree.DnDTree;

/**
 * データコンテナの構造を示すツリーコンポーネント。
 * 
 * @version 1.0.0
 */
public class DtContainerContentTreePane extends DnDTree
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final FocusRequester _hFocusRequester = new FocusRequester();

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtContainerContentTreePane() {
		super();
	}
	
	public DtContainerContentTreePane(DtContainerContentTreeModel model) {
		super(model);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * ツリーコンポーネントにフォーカスを設定する。
	 */
	public void setFocus() {
		if (!hasFocus()) {
			SwingUtilities.invokeLater(_hFocusRequester);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このビューのメインコンポーネントとなるツリーコンポーネントに
	 * フォーカスを設定するアクションクラス。
	 */
	protected class FocusRequester implements Runnable {
		public void run() {
			if (!requestFocusInWindow()) {
				requestFocus();
			}
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
