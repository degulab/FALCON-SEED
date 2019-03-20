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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
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
