/*
 * @(#)DTCContentExalgeEditView.java	1.1.0	2023/01/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor.content.common.table;

import dtalge.container.editor.content.common.swing.DtContainerContentDetailView;

/**
 * 交換代数元を編集するテーブルを配置したスクロール可能なビュー。
 * 
 * @version 1.1.0
 * @since 1.1.0
 */
public class DTCContentExalgeEditView extends AbDTCContentAlgeEditView
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	
	static protected final DTCContentExalgeEditModel	EMPTY_TABLE_MODEL = new DTCContentExalgeEditModel();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public DTCContentExalgeEditView(DtContainerContentDetailView parentContainer) {
		super(parentContainer);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected AbDTCContentAlgeEditModel getEmptyEditModel() {
		return EMPTY_TABLE_MODEL;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
