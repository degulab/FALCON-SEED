/*
 * @(#)DtContainerContentInsertDialog.java	1.0.0	2022/12/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.swing;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.common.tree.DtContainerContentParentTreeNode;
import dtalge.container.editor.setting.DtContainerEditorSettings;

/**
 * データコンテナ要素の挿入ダイアログ。
 * 空のオブジェクトを挿入するか、ファイルから読み込まれたオブジェクトを挿入するかを選択できる。
 * 
 * @version 1.0.0
 */
public class DtContainerContentInsertDialog extends AbDtContainerContentImportDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	/** このダイアログの最小サイズ **/
	static private final Dimension DM_MIN_SIZE = new Dimension(480, 380);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 挿入モードで、インポートダイアログを生成する。
	 * @param owner			このダイアログのオーナーコンポーネント
	 * @param forSlipObject	挿入または変更対象がスリップオブジェクトとする場合は {@code true}、データオブジェクトとする場合は {@code false} を指定する
	 * @param targetParent	オブジェクトの追加先ノード、もしくは変更対象ノードの親ノード
	 */
	public DtContainerContentInsertDialog(Frame owner, boolean forSlipObject, DtContainerContentParentTreeNode targetParent)
	{
		super(owner, forSlipObject, DtContainerEditorSettings.CONTENT_INSERT_DLG, DtContainerEditorMessages.getInstance().ContentImportDlg_title_InsertObject, targetParent, null);
	}
	
	/**
	 * 挿入モードで、インポートダイアログを生成する。
	 * @param owner			このダイアログのオーナーコンポーネント
	 * @param forSlipObject	挿入または変更対象がスリップオブジェクトとする場合は {@code true}、データオブジェクトとする場合は {@code false} を指定する
	 * @param targetParent	オブジェクトの追加先ノード、もしくは変更対象ノードの親ノード
	 */
	public DtContainerContentInsertDialog(Dialog owner, boolean forSlipObject, DtContainerContentParentTreeNode targetParent)
	{
		super(owner, forSlipObject, DtContainerEditorSettings.CONTENT_INSERT_DLG, DtContainerEditorMessages.getInstance().ContentImportDlg_title_InsertObject, targetParent, null);
	}
	
	/**
	 * 挿入モードで、インポートダイアログを生成する。
	 * この場合の親ノードは、リストタイプのもののみとし、<em>insertIndex</em> が初期挿入位置となる。
	 * @param owner			このダイアログのオーナーコンポーネント
	 * @param forSlipObject	挿入または変更対象がスリップオブジェクトとする場合は {@code true}、データオブジェクトとする場合は {@code false} を指定する
	 * @param targetParent	オブジェクトの追加先ノード、もしくは変更対象ノードの親ノード
	 * @param insertIndex	初期挿入位置を示すインデックス
	 */
	public DtContainerContentInsertDialog(Frame owner, boolean forSlipObject, DtContainerContentParentTreeNode targetParent, int insertIndex)
	{
		super(owner, forSlipObject, DtContainerEditorSettings.CONTENT_INSERT_DLG, DtContainerEditorMessages.getInstance().ContentImportDlg_title_InsertObject, targetParent, insertIndex);
	}
	
	/**
	 * 挿入モードで、インポートダイアログを生成する。
	 * この場合の親ノードは、リストタイプのもののみとし、<em>insertIndex</em> が初期挿入位置となる。
	 * @param owner			このダイアログのオーナーコンポーネント
	 * @param forSlipObject	挿入または変更対象がスリップオブジェクトとする場合は {@code true}、データオブジェクトとする場合は {@code false} を指定する
	 * @param targetParent	オブジェクトの追加先ノード、もしくは変更対象ノードの親ノード
	 * @param insertIndex	初期挿入位置を示すインデックス
	 */
	public DtContainerContentInsertDialog(Dialog owner, boolean forSlipObject, DtContainerContentParentTreeNode targetParent, int insertIndex)
	{
		super(owner, forSlipObject, DtContainerEditorSettings.CONTENT_INSERT_DLG, DtContainerEditorMessages.getInstance().ContentImportDlg_title_InsertObject, targetParent, insertIndex);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
