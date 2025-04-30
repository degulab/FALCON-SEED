/*
 * @(#)FilenameInputDialog.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.module.swing;

import java.awt.Frame;
import java.io.File;

import ssac.util.io.VirtualFile;
import ssac.util.swing.InputDialog;

/**
 * ファイル名入力ダイアログ
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class FilenameInputDialog extends InputDialog
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
	
	public FilenameInputDialog(Frame owner, File baseDir, String initialValue, String label, String title, String description)
	{
		super(owner, label, null, title, description, initialValue, null);
		FilenameValidator newValidator = new FilenameValidator();
		newValidator.setAlreadyExistsFilenames(baseDir);
		this.setValidator(newValidator);
	}
	
	public FilenameInputDialog(Frame owner, VirtualFile baseDir, String initialValue, String label, String title, String description)
	{
		super(owner, label, null, title, description, initialValue, null);
		FilenameValidator newValidator = new FilenameValidator();
		newValidator.setAlreadyExistsFilenames(baseDir);
		this.setValidator(newValidator);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
