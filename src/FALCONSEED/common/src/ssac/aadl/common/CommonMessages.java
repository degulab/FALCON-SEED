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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CommonMessages.java	3.2.0	2015/06/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommonMessages.java	2.0.0	2012/11/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommonMessages.java	1.20	2012/03/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommonMessages.java	1.17	2011/02/02
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommonMessages.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommonMessages.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommonMessages.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.common;

import java.util.ResourceBundle;

import ssac.util.Strings;
import ssac.util.io.FieldResource;
import ssac.util.logging.AppLogger;

/**
 * 共通の文字列リソース。
 * 
 * @version 3.2.0
 * @since 1.14
 */
public class CommonMessages extends FieldResource
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * 文字列リソースの唯一のインスタンス
	 */
	static private CommonMessages instance = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected String getResourceName() {
		return CommonMessages.class.getName();
	}
	
	static public CommonMessages getInstance() {
		if (instance == null) {
			instance = new CommonMessages();
			String path = getResourceName();
			try {
				ResourceBundle resource = ResourceBundle.getBundle(path);
				instance.updateFields(resource);
			} catch (RuntimeException ex) {
				AppLogger.debug(ex);
			}
		}
		return instance;
	}

	/**
	 * リソースの再読み込み
	 * @since 2.0.0
	 */
	static public void updateInstance() {
		instance = null;
		getInstance();
	}
	
	static public String getUnexpectedElement(String expected, String actual) {
		return "expected element=" + expected + " but actual=" + actual;
	}

	//------------------------------------------------------------
	// Messages
	//------------------------------------------------------------

	// Message box titles
	public String msgboxTitleInfo		= "Information";
	public String msgboxTitleWarn		= "Warning";
	public String msgboxTitleError		= "Error";
	public String msgboxTitleConfirm	= "Confirmation";
	public String msgboxTitleQuestion	= "Question";
	
	// Message strings
	public String Message_ReadOnly = "(Read Only)";

	// Button text
	public String Button_Browse	= "Browse...";
	public String Button_Add	= "Add...";
	public String Button_Edit	= "Edit...";
	public String Button_Delete	= "Delete";
	public String Button_Up		= "Up";
	public String Button_Down	= "Down";
	public String Button_Left	= "Left";
	public String Button_Right	= "Right";
	public String Button_Update = "Update";
	
	public String Button_Apply	= "Apply";
	public String Button_OK		= "OK";
	public String Button_Cancel	= "Cancel";
	public String Button_Close	= "Close";
	public String Button_RemoveHistories = "Remove histories";
	public String Button_Select	= "Select";
	public String Button_AllSelect = "Select All";
	public String Button_Undo = "Undo";
	public String Button_Redo = "Redo";
	public String Button_Cut = "Cut";
	public String Button_Copy = "Copy";
	public String Button_Paste = "Paste";
	public String Button_Duplicate = "Duplicate";
	
	public String TableHeaderPopupMenu_Column_AdjustColumnWidth = "Adjust column width";
	public String TableHeaderPopupMenu_Column_SetWidthToDefault = "Set column width to default";

	// Document title modifiers
	public String editingDocumentModifier = "* ";
	public String newDocumentTitle = "Untitled";

	// Status bar labels
	public String statusInsertMode_Insert		= "Insert";
	public String statusInsertMode_Overwrite	= "Overwrite";

	// Common labels
	public String labelDefault		= "Default";
	public String labelCustom		= "Custom";
	public String labelFile			= "File";
	public String labelMainClass	= "Main class";
	public String labelMessageNotFound	= "(Not found)";
	public String labelTextEncoding		= "Text encoding";
	
	// File progress dialog
	public String Progress_OpenAs = "Open %s";
	public String Progress_CopyTo = "Copy files";
	public String Progress_MoveTo = "Move files";
	public String Progress_Delete = "Delete files";
	public String Progress_Reading = "Reading...";
	public String Progress_Writing = "Writing...";
	
	// Filename Validator
	public String FilenameValidator_ErrorTitle		= "Input Error";
	public String FilenameValidator_ErrorMessage	= "The input name is illegal.";
	public String FilenameValidator_RequiredError	= "Please input a name.";
	public String FilenameValidator_CharsetError	= "Cannot use the following characters.";
	public String FilenameValidator_IllegalName		= "Cannot use the input name.";
	public String FilenameValidator_MultipleName	= "Already exists the input name.";
	
	// Confirm Dialog's button text
	public String confirmOverwiteTitle = "Overwrite confirmation";
	public String Overwrite_YesOne = "Yes";
	public String Overwrite_YesAll = "Yes all";
	public String Overwrite_NoOne  = "No";
	public String Overwrite_NoAll  = "No all";
	public String confirmDeleteTitle = "Deletion confirmation";
	
	// Chooser titles
	public String Choose_directory			= "Select directory";
	public String Choose_folder			= "Select folder";
	public String Choose_mainmodule		= "Select Main module";
	public String Choose_MoveTarget		= "Select move target";
	public String Choose_Package			= "Select package";
	public String PackageBaseChooser_Title = "Select package base";
	public String InputTitle_NewObject		= "New";
	public String InputTitle_NewProject	= "New Project";
	public String InputLabel_ProjectName	= "Project name:";
	public String InputTitle_NewFolder		= "New Folder";
	public String InputTitle_NewFile		= "New File";
	public String InputLabel_FolderName	= "Folder name:";
	public String InputLabel_FileName		= "File name:";
	public String InputTitle_Rename		= "Rename";
	public String InputLabel_NewName		= "New name:";
	public String TreeLabel_SelectParentFolder	= "Select parent folder:";
	public String InputLabel_OmitExtension	= "(Can omit the extension)";
	
	// Module detail items
	public String ModuleInfoTabTitle_Package		= "Package";
	public String ModuleInfoTabTitle_MainModule	= "Main module";
	public String ModuleInfoLabel_lastmodified	= "[Last modified]";
	public String ModuleInfoLabel_title		= "[Title]";
	public String ModuleInfoLabel_description	= "[Description]";
	public String ModuleInfoLabel_note			= "[Note]";
	public String ModuleInfoLabel_MainModule	= "[Main module]";
	public String ModuleInfoLabel_MainClass	= "[Main class]";
	public String ModuleInfoLabel_compiler		= "[Compiler]";
	public String ModuleInfoLabel_revision		= "[Revision]";
	public String ModuleInfoLabel_arguments	= "[Arguments]";
	public String ModuleInfoLabel_args_attr	= "Attr";
	public String ModuleInfoLabel_args_desc	= "Description";
	public String ModuleInfoLabel_args_value	= "Value";
	
	// CSV Config Dialog
	public String CsvConfigDlg_title			= "CSV File Configuration";
	public String CsvSaveConfigDlg_title		= "CSV File Save Configuration";
	public String CsvConfigDlgLabel_filepath	= "File";
	public String CsvConfigDlgLabel_encoding	= "File encoding";
	public String CsvConfigDlgLabel_config		= "CSV configuration";
	public String CsvConfigDlgLabel_delimiter	= "delimiter char";
	public String CsvConfigDlgLabel_escape		= "escape char";
	public String CsvConfigDlgLabel_denyNL		= "The line feed does not escape.";
	public String CsvConfigDlgLabel_headerline	= "Header Line";
	public String CsvConfigDlgLabel_autodetect	= "Auto detect data type";
	public String CsvConfigDlgDelim_comma		= "Comma";
	public String CsvConfigDlgDelim_tab			= "Tab";
	public String CsvConfigDlgDelim_space		= "Space";
	public String CsvConfigDlgEscape_none		= "None";
	public String CsvConfigDlgEscape_squot		= "Single quote (')";
	public String CsvConfigDlgEscape_dquot		= "Double quote (\")";
	
	// Monitor Dialog
	public String PackageRegistProgressMonitor_title	= "Register package";

	//==============================
	// Messages
	//==============================

	// Confirm messages
	public String confirmOverwriteFile = "\"%s\" is already exist.\nDo you want to replace?";
	//public String msgToAskOverwriteFile = "\"%s\" はすでに存在します。\nこのファイルを置き換えますか？";
	public String confirmSaveChanges = "Do you want to save the changes you made?";
	//public String msgToAskSaveChanges = "変更を保存しますか？";
	public String confirmSaveDocument = "Please save the changes to file.";
	//public String msgToAskSaveDocument = "変更をファイルに保存してください。";
	public String confirmExecutePrevious = "The file has been changed.\nDo you execute it in the state before?";
	//public String msgToAskExecutePrevious = "ファイルは変更されています。\n以前の状態で実行しますか？";
	public String confirmCreateFile = "\"%s\" does not exist.\nDo you want to create?";
	//public String confirmCreateFile = "\"%s\" は存在しません。\n作成しますか?";
	public String confirmDetailOverwriteFile = "\"%s\" file already exists.\n\nWould you like to replace the exiting file\n    %s bytes\n    Last modified: %s\n\nwith this one?\n    %s bytes\n    Last modified: %s\n\n";
	//public String confirmDetailOverwriteFile = "\"%s\" ファイルがすでに存在します。\n\n現在のファイル\n    %s バイト\n    更新日時: %s\n\nに次の新しいファイルを上書きしますか?\n    %s バイト\n    更新日時: %s\n\n";
	public String confirmDetailOverwriteDirectory = "\"%s\" folder already exists.\n\nDo you want to overwrite?\n\n";
	//public String confirmDetailOverwriteDirectory = "\"%s\" フォルダがすでに存在します。\n\nこのフォルダのファイルと移動またはコピーしようとしているフォルダの\nファイルが同じ名前の場合、新しいファイルで上書きされます。\n\nフォルダを移動またはコピーしますか?\n\n";
	public String confirmDetailDeleteMultiFiles = "Do you want to delete %d elements?";
	//public String confirmDetailDeleteMultiFiles = "これら %d 個の要素を削除しますか?";
	public String confirmDetailDeleteSingleFile = "Do you want to delete '%s'?";
	//public String confirmDetailDeleteSingleFile = "'%s' を削除しますか?";

	// Error messages
	public String msgSearchResultNotFound = "Not found.";
	public String msgSearchReplacedTokens = "%d tokens are replaced.";

	public String msgOutOfMemoryError = "Out of memory!";
	//public String msgOutOfMemoryError = "メモリが足りません。";
	public String msgSystemError = "System error";
	//public String msgSystemError = "システムエラーにより停止しました。";
	public String msgCouldNotCreateFile = "Failed to create file";
	//public String msgCouldNotCreateFile = "ファイルの作成に失敗しました。";
	public String msgCouldNotRenameFile = "Failed to rename file";
	//public String msgCouldNotRenameFile = "ファイル名の変更に失敗しました。";
	public String msgCouldNotReadFile = "Failed to read file";
	//public String msgCouldNotReadFile = "ファイルの読み込みに失敗しました。";
	public String msgCouldNotWriteFile = "Failed to write file";
	//public String msgCouldNotWriteFile = "ファイルの書き出しに失敗しました。";
	public String msgCouldNotSaveSetting = "Failed to write settings file";
	//public String msgCouldNotSaveSetting = "設定情報のファイルへの書き出しに失敗しました。";
	public String msgNotHaveWritePermission = "Does not have write permission";
	//public String msgNotHaveWritePermission = "書き込み権限がありません。";
	public String msgFileNotFound = "File not found";
	//public String msgFileNotFound = "ファイルが見つかりません。";
	public String msgUnsupportedEncoding = "Unsupported file encoding";
	//public String msgUnsupportedEncoding = "このファイルエンコーディングはサポートされていません。";
	public String msgNotFile = "Please select a file";
	//public String msgNotFile = "ファイルを選択してください。";
	public String msgNotDirectory = "Please select a folder";
	//public String msgNotDirectory = "フォルダを選択してください。";
	public String msgCouldNotExecute = "Failed to run!";
	//public String msgCouldNotExecute = "実行に失敗しました。";
	public String msgCouldNotCompile = "Failed to compile!";
	//public String msgCouldNotCompile = "コンパイルの実行に失敗しました。";
	public String msgCompileNotYet = "It is not compiled.\nPlease compile it previously.";
	//public String msgCompileNotYet = "コンパイルされていません。先にコンパイルを実行してください。";
	public String msgNowCompiling = "It is compiling.\nPlease do this operation after a present compiling.";
	//public String msgNowCompiling = "コンパイル中です。\nこの操作はコンパイルが終了してから行ってください。";
	public String msgCompileTerminated = "Compiling terminated.";
	//public String msgCompileTerminated = "コンパイルは中断されました。";
	public String msgNowExecuting = "Module is running.\nPlease do this operation after the module running.";
	//public String msgNowExecuting = "モジュール実行中です。\nこの操作はモジュールの実行が終了してから行ってください。";
	public String msgNotFoundJDK = "JDK not found!\nPlease correct preference from [Preference] menu after it starts.";
	//public String msgNotFoundJDK = "Java開発環境が見つかりません。\n起動後、[設定]メニューから正しい設定を行ってください。";
	public String msgUnsupportedDocument = "\"%s\" is not supported file type.";
	//public String msgUnsupportedDocument = "\"%s\" はサポートされていないファイル形式です。";
	public String msgExecutableFileNotFound = "Executable file not found.";
	//public String msgExecutableFileNotFound = "実行可能なファイルが見つかりません。";
	public String msgSpreadSheetPasteInMultiple = "Cannot paste to multiple selections.";
	//public String msgSpreadSheetPasteInMultiple = "複数の選択範囲には貼り付けできません。";
	public String msgCannotWriteCauseReadOnly = "File '%s' is read only. Cannot save it.";
	//public String msgCannotWriteCauseReadOnly = "ファイル '%s' は読み取り専用です。保存できません。";
	public String msgCouldNotProcessComplete = "Processing cannot be completed.";
	//public String msgCouldNotProcessComplete = "処理が完了できません。";
	public String msgCouldNotRenameTo = "Failed to change '%s' to '%s'";
	//public String msgCouldNotRenameTo = "'%s' を '%s' に変更できません。";
	public String msgCouldNotMoveTo   = "Failed to move \"%s\"";
	//public String msgCouldNotMoveTo   = "\"%s\" を移動できません。";
	public String msgCouldNotCopyTo   = "Failed to copy \"%s\"";
	//public String msgCouldNotCopyTo   = "\"%s\" をコピーできません。";
	public String msgCouldNotDelete   = "Failed to delete \"%s\"";
	//public String msgCouldNotDelete   = "\"%s\" を削除できません。";
	public String msgCouldNotCreateDirectory = "Failed to create folder.";
	//public String msgCouldNotCreateDirectory = "フォルダの作成に失敗しました。";
	public String msgCouldNotAccessFile = "Cannot access to file.";
	//public String msgCouldNotAccessFile = "ファイルにアクセスできません。";
	public String msgCouldNotAccessDirectory = "Cannot access to folder.";
	//public String msgCouldNotAccessDirectory = "フォルダにアクセスできません。";
	public String msgPackBaseNoSelection = "Select package base.";
	//public String msgPackBaseNoSelection = "パッケージベースを選択してください。";
	public String msgPackBaseNotFound = "Package base not found.";
	//public String msgPackBaseNotFound = "パッケージベースが見つかりません。";
	public String msgPackBaseNotDirectory = "Package base is not folder.";
	//public String msgPackBaseNotDirectory = "パッケージベースはフォルダではありません。";
	public String msgPackBaseCannotAccess = "Cannot access to Package base";
	//public String msgPackBaseCannotAccess = "パッケージベースにアクセスできません。";
	public String msgCouldNotModifyPackageStructure = "Cannot change the constitution of the package.";
	//public String msgCouldNotModifyPackageStructure = "パッケージの構成は変更できません。";
	public String msgCouldNotModifyFilesInPackage = "Cannot change a file and the folder of the package.";
	//public String msgCouldNotModifyFilesInPackage = "パッケージのファイルやフォルダは変更できません。";
	public String msgCouldNotMoveFilesInPackage = "A file and the folder included in a package cannot move.";
	//public String msgCouldNotMoveFilesInPackage = "パッケージに含まれるファイルやフォルダは移動できません。";
	public String msgCouldNotDeleteFilesInPackage = "A file and the folder included in a package cannot delete.";
	//public String msgCouldNotDeleteFilesInPackage = "パッケージに含まれるファイルやフォルダは削除できません。";
	public String msgCouldNotSelectInProject = "Cannot select a project folder or the subfolder.";
	//public String msgCouldNotSelectInProject = "プロジェクト・フォルダ、もしくはそのサブフォルダは選択できません。";
	public String msgCouldNotSelectInPackage = "Cannot select a package folder or the subfolder.";
	//public String msgCouldNotSelectInPackage = "パッケージ・フォルダ、もしくはそのサブフォルダは選択できません。";
	public String msgCouldNotCopySameDirectory = "Cannot copy to '%S'.\nCopying and an origin of copy are the same Folder.";
	//public String msgCouldNotCopySameDirectory = "'%S' にはコピーできません。\nコピー先とコピー元が同じフォルダです。";
	public String msgCouldNotCopyToSubDirectory = "Cannot copy to '%S'.\nThe copying is a subfolder of the origins of copy.";
	//public String msgCouldNotCopyToSubDirectory = "'%S' にはコピーできません。\nコピー先はコピー元のサブフォルダです。";
	public String msgCouldNotMoveSameDirectory = "Cannot move to '%s'.\nMovement and an origin of movement are the same.";
	//public String msgCouldNotMoveSameDirectory = "'%s' には移動できません。\n移動先と移動元が同じです。";
	public String msgCouldNotMoveToSubDirectory = "Cannot move to '%s'.\nThe movement is a subfolder of the origins of movement.";
	//public String msgCouldNotMoveToSubDirectory = "'%s' には移動できません。\n移動先は移動元のサブフォルダです。";
	public String msgCouldNotMoveOpenedEditingFile = "The following files will open in the editor can not be moved.";
	//public String msgCouldNotMoveOpenedEditingFile = "エディタで開いている以下のファイルは移動できません。";
	public String msgCouldNotRenameOpenedEditingFile = "The following files will open in the editor can not be renamed.";
	//public String msgCouldNotRenameOpenedEditingFile = "エディタで開いている以下のファイルは名前変更できません。";

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// implement MessageID
	//------------------------------------------------------------
	
	static public final String formatErrorMessage(String msg, Throwable cause, Object...params) {
		if (cause == null && params.length <= 0) {
			return msg;
		}
		
		StringBuffer sb = new StringBuffer();
		//--- message
		sb.append(msg);
		//--- params
		if (params.length > 0) {
			for (Object obj : params) {
				sb.append("\n[");
				if (obj != null)
					sb.append(obj.toString());
				else
					sb.append("null");
				sb.append("]");
			}
		}
		//--- cause
		if (cause != null) {
			sb.append("\n\n  Exception : ");
			String exmsg = cause.getLocalizedMessage();
			if (!Strings.isNullOrEmpty(exmsg)) {
				sb.append(exmsg);
			}
			sb.append("(");
			sb.append(cause.getClass().getName());
			sb.append(")");
		}
		
		return sb.toString();
	}
	
	static public final String getErrorMessage(int msgID, Throwable cause, Object...params) {
		CommonMessages appmsg = CommonMessages.getInstance();
		String retmsg = "";
		switch (msgID) {
			case MessageID.ERR_FILE_READ :
				retmsg = appmsg.msgCouldNotReadFile;
				break;
			case MessageID.ERR_FILE_WRITE :
				retmsg = appmsg.msgCouldNotWriteFile;
				break;
			case MessageID.ERR_FILE_NOTFILE :
				retmsg = appmsg.msgNotFile;
				break;
			case MessageID.ERR_FILE_NOTFOUND :
				retmsg = appmsg.msgFileNotFound;
				break;
			case MessageID.ERR_FILE_UNSUPPORTED_ENCODING :
				retmsg = appmsg.msgUnsupportedEncoding;
				break;
			case MessageID.ERR_FILE_SETTING_WRITE :
				retmsg = appmsg.msgCouldNotSaveSetting;
				break;
			case MessageID.ERR_FILE_CREATE :
				retmsg = appmsg.msgCouldNotCreateFile;
				break;
			case MessageID.ERR_FILE_RENAME :
				retmsg = appmsg.msgCouldNotRenameFile;
				break;
			case MessageID.ERR_BUILD_COMPILE :
				retmsg = appmsg.msgCouldNotCompile;
				break;
			case MessageID.ERR_BUILD_RUN :
				retmsg = appmsg.msgCouldNotExecute;
				break;
			case MessageID.ERR_BUILD_COMPILE_NOT_YET :
				retmsg = appmsg.msgCompileNotYet;
			//--- default error message
			default :
				retmsg = "Unknown error!";
		}
		return formatErrorMessage(retmsg, cause, params);
	}
	
	static public final class MessageID {
		static public final int ERR_FILE_READ					= 1001;
		static public final int ERR_FILE_WRITE				= 1002;
		static public final int ERR_FILE_NOTFILE				= 1003;
		static public final int ERR_FILE_NOTFOUND				= 1004;
		static public final int ERR_FILE_UNSUPPORTED_ENCODING	= 1005;
		static public final int ERR_FILE_SETTING_WRITE		= 1006;
		static public final int ERR_FILE_CREATE				= 1007;
		static public final int ERR_FILE_RENAME				= 1008;
		static public final int ERR_BUILD_COMPILE				= 2001;
		static public final int ERR_BUILD_RUN					= 2002;
		static public final int ERR_BUILD_COMPILE_NOT_YET		= 2003;
	}
}
