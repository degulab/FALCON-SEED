/*
 * @(#)DtBinderEditView.java	2.0.0	2025/02/18
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtBinderEditView.java	1.0.0	2022/12/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.binder;

import javax.swing.Action;

import dtalge.container.editor.content.common.AbDtContainerEditView;
import dtalge.container.editor.menu.DtContainerEditorMenuResources;

/**
 * データコンテナの編集用GUIを提供するビューの、{@code DtBinder} 固有の実装。
 * 
 * @version 2.0.0
 */
public class DtBinderEditView extends AbDtContainerEditView<DtBinderDocument>
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DtBinderEditView(DtBinderDocument document) {
		super(document);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Event handlers
	//------------------------------------------------------------
	
	/**
	 * メニュー項目の更新要求時に呼び出されるハンドラ・メソッド。
	 * 現時点では、このメソッドでは処理を行わないため、常に {@code false} を返す。
	 * 
	 * @param command	このイベント要因のコマンド文字列
	 * @param source	このイベント要因のソースオブジェクト。
	 * 					ソースオブジェクトが未定義の場合は <tt>null</tt>。
	 * @param action	このイベント要因のソースオブジェクトに割り当てられたアクション。
	 * 					アクションが未定義の場合は <tt>null</tt>。
	 * @return	このハンドラ内で処理が完結した場合は <tt>true</tt> を返す。イベントシーケンスの
	 * 			別のハンドラに処理を委譲する場合は <tt>false</tt> を返す。
	 */
	public boolean onProcessMenuUpdate(String command, Object source, Action action) {
		if (command.equals(DtContainerEditorMenuResources.ID_FILE_EXPORT_DTBINDER_DESIMPLESLIPS_CSV)) {
			// データバインダーがアクティブのときのみ、エクスポートを許可
			action.setEnabled(true);
			return true;
		}
		
		return super.onProcessMenuUpdate(command, source, action);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
