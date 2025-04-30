/*
 * @(#)CsvFileConfigDialog.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;

import ssac.aadl.common.CommonMessages;

/**
 * CSVファイルの詳細設定ダイアログ
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public class CsvFileConfigDialog extends AbCsvFileConfigDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CsvFileConfigDialog(Frame owner, File targetFile) {
		super(owner, targetFile, CommonMessages.getInstance().CsvConfigDlg_title, true);
	}
	
	public CsvFileConfigDialog(Dialog owner, File targetFile) {
		super(owner, targetFile, CommonMessages.getInstance().CsvConfigDlg_title, true);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
