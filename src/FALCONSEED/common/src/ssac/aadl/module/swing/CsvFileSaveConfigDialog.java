/*
 * @(#)CsvFileSaveConfigDialog.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;

import javax.swing.JCheckBox;

import ssac.aadl.common.CommonMessages;

/**
 * CSVファイル保存時の詳細設定ダイアログ。
 * このダイアログでは、ファイル・エンコーディング、ヘッダー行 のみを
 * 表示する。他の設定は、標準のパラメータとする。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public class CsvFileSaveConfigDialog extends AbCsvFileConfigDialog
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
	
	public CsvFileSaveConfigDialog(Frame owner, File targetFile) {
		super(owner, targetFile, CommonMessages.getInstance().CsvSaveConfigDlg_title, true);
	}
	
	public CsvFileSaveConfigDialog(Dialog owner, File targetFile) {
		super(owner, targetFile, CommonMessages.getInstance().CsvSaveConfigDlg_title, true);
	}

	@Override
	protected JCheckBox createAutoDetectDataTypeCheckBox() {
		// no component
		return null;
	}

	@Override
	protected JCheckBox createDenyMultilineCheckBox() {
		// no component
		return null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
