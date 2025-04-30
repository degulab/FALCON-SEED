/*
 * @(#)DtContainerContentReplaceDialog.java	1.0.0	2022/12/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.swing;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

import dtalge.container.editor.DtContainerEditorMessages;
import dtalge.container.editor.content.common.tree.DtContainerContentParentTreeNode;
import dtalge.container.editor.content.common.tree.IDtContainerContentTreeNode;
import dtalge.container.editor.setting.DtContainerEditorSettings;

/**
 * データコンテナ要素の変更ダイアログ。
 * 空のオブジェクトに変更するか、ファイルから読み込まれたオブジェクトに変更するかを選択できる。
 * 
 * @version 1.0.0
 */
public class DtContainerContentReplaceDialog extends AbDtContainerContentImportDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/** このダイアログの最小サイズ **/
	static private final Dimension DM_MIN_SIZE = new Dimension(480, 350);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 変更モードで、インポートダイアログを生成する。
	 * @param owner			このダイアログのオーナーコンポーネント
	 * @param forSlipObject	挿入または変更対象がスリップオブジェクトとする場合は {@code true}、データオブジェクトとする場合は {@code false} を指定する
	 * @param targetParent	オブジェクトの追加先ノード、もしくは変更対象ノードの親ノード
	 * @param replaceTarget	変更対象のノード
	 */
	public DtContainerContentReplaceDialog(Frame owner, boolean forSlipObject, DtContainerContentParentTreeNode targetParent, IDtContainerContentTreeNode replaceTarget)
	{
		super(owner, forSlipObject, DtContainerEditorSettings.CONTENT_REPLACE_DLG, DtContainerEditorMessages.getInstance().ContentImportDlg_title_ReplaceObject, targetParent, replaceTarget);
		if (replaceTarget == null) throw new NullPointerException("Replace target node is null");
	}
	
	/**
	 * 変更モードで、インポートダイアログを生成する。
	 * @param owner			このダイアログのオーナーコンポーネント
	 * @param forSlipObject	挿入または変更対象がスリップオブジェクトとする場合は {@code true}、データオブジェクトとする場合は {@code false} を指定する
	 * @param targetParent	オブジェクトの追加先ノード、もしくは変更対象ノードの親ノード
	 * @param replaceTarget	変更対象のノード
	 */
	public DtContainerContentReplaceDialog(Dialog owner, boolean forSlipObject, DtContainerContentParentTreeNode targetParent, IDtContainerContentTreeNode replaceTarget)
	{
		super(owner, forSlipObject, DtContainerEditorSettings.CONTENT_REPLACE_DLG, DtContainerEditorMessages.getInstance().ContentImportDlg_title_ReplaceObject, targetParent, replaceTarget);
		if (replaceTarget == null) throw new NullPointerException("Replace target node is null");
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
