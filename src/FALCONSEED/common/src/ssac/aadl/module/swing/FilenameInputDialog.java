/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Copyright 2007-2009  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
