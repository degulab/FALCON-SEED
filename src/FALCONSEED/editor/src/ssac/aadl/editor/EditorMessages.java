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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)EditorMessages.java	1.17	2011/02/15
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorMessages.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorMessages.java	1.10	2009/01/21
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor;

import java.util.ResourceBundle;

import ssac.util.Strings;
import ssac.util.io.FieldResource;
import ssac.util.logging.AppLogger;

/**
 * AADLエディタの文字列リソース。
 * 
 * @version 1.17	2011/02/15
 *
 * @since 1.10
 */
public class EditorMessages extends FieldResource
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
	static private EditorMessages instance = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected String getResourceName() {
		return EditorMessages.class.getName();
	}
	
	static public EditorMessages getInstance() {
		if (instance == null) {
			instance = new EditorMessages();
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
	
	static public String getUnexpectedElement(String expected, String actual) {
		return "expected element=" + expected + " but actual=" + actual;
	}

	//------------------------------------------------------------
	// Messages
	//------------------------------------------------------------
	
	public String appMainTitle	= "AADL Editor";
	
	// Common file extensions
	public String descExtJar	= "Jar file (*.jar)";
	public String extJar		= ".jar";
	public String descExtZip	= "ZIP file (*.zip)";
	public String extZip		= ".zip";
	public String descExtCSV	= "CSV file (*.csv)";
	public String extCSV		= ".csv";
	public String descExtXML	= "XML file (*.xml)";
	public String extXML		= ".xml";
	public String descExtTXT	= "Text file (*.txt)";
	public String extTXT		= ".txt";
	public String descAADLArg	= "AADL Argument file (*.csv,*.xml,*.txt)";
	public String extAADLArg	= ".csv;.xml;.txt";

	public String chooserTitleJavaHome		= "Select Java Development Kit location";
	public String chooserTitleJavaCompiler	= "Select Java compiler (tools.jar)";
	public String chooserTitleJavaCommand	= "Select Java command";
	public String chooserTitleWorkDir		= "Select working directory";
	public String chooserTitleClassPath	= "Select Class-path for Libraries";
	public String chooserTitleDestFile		= "Select destination file";
	public String chooserTitleSourceDir	= "Select source output directory";
	public String chooserTitleManifest		= "Select manifest file";
	public String chooserTitleProgArgs		= "Select argument file for AADL";
	public String chooserTitleRunAsJar		= "Select executable module";

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
	public String labelMainClass	= "Mail class";
	public String labelMessageNotFound	= "(Not found)";
	public String labelTextEncoding		= "Text encoding";

	// Console pane
	public String consoleTooltipSetting	= "Options";
	public String consoleTooltipRun		= "Start";
	public String consoleTooltipStop	= "Stop";
	public String consoleTooltipClear	= "Clear texts";
	
	public String consoleTabTitleConsole	= "Console";
	public String consoleTabTitleCompile	= "Compile";
	
	// Menu text
	//--- [File] menu
	public String menuFile				= "File";
	public String menuFileNew			= "New";
	public String menuFileNewProject	= "Project...";
	public String menuFileNewFolder		= "Folder...";
	public String menuFileOpen			= "Open...";
	public String menuFileReopen		= "Re-open";
	public String menuFileReopenDefault = "Default";
	public String menuFileRecent		= "Open Recent";
	public String menuFileRecentNone	= "None";
	public String menuFileRecentClear	= "Clear recent files";
	public String menuFileSave			= "Save";
	public String menuFileSaveAs		= "Save As...";
	public String menuFileClose			= "Close";
	public String menuFileAllClose		= "Close All";
	public String menuFileMoveTo		= "Move to...";
	public String menuFileRename		= "Rename...";
	public String menuFileRefresh		= "Refresh";
	public String menuFileProjRegister	= "Register to Project";
	public String menuFileProjUnregister	= "Unregister from Project";
	public String menuFilePackage		= "Package";
	public String menuFilePackRegist	= "Register...";
	public String menuFilePackRefer		= "Refer...";
	public String menuFilePackImport	= "Import...";
	public String menuFileSelectWS		= "Select Workspace...";
	public String menuFilePreference	= "Preference...";
	public String menuFileQuit			= "Quit";
	//--- [Edit] menu
	public String menuEdit			= "Edit";
	public String menuEditUndo		= "Undo";
	public String menuEditRedo		= "Redo";
	public String menuEditCut		= "Cut";
	public String menuEditCopy		= "Copy";
	public String menuEditPaste		= "Paste";
	public String menuEditDelete	= "Delete";
	public String menuEditSelectAll	= "Select All";
	public String menuEditJump		= "Go To Line...";
	//--- [Find] menu
	public String menuFind			= "Find";
	public String menuFindFind		= "Find/Replace...";
	public String menuFindNext		= "Find Next";
	public String menuFindPrevious	= "Find Previous";
	//--- [Build] menu
	public String menuBuild					= "Build";
	public String menuBuildCompile			= "Compile";
	public String menuBuildRun				= "Run";
	public String menuBuildCompileAndRun	= "Compile and Run";
	public String menuBuildRunAsJar			= "Run as JAR...";
	public String menuBuildCompileInDir		= "Compile in directory...";
	public String menuBuildOption			= "Options...";
	//--- [Help] menu
	public String menuHelp		= "Help";
	public String menuHelpAbout	= "About...";

	// Toolbar tooltips
	public String tipFileNew	= "New %s";
	public String tipFileOpen	= "Open";
	public String tipFileSave	= "Save";
	public String tipEditUndo	= "Undo";
	public String tipEditRedo	= "Redo";
	public String tipEditCut	= "Cut";
	public String tipEditCopy	= "Copy";
	public String tipEditPaste	= "Paste";
	public String tipFindFind	= "Find/Replace";
	public String tipFindPrev	= "Find Previouse";
	public String tipFindNext	= "Find Next";
	public String tipBuildCompile		= "Compile";
	public String tipBuildRun			= "Run";
	public String tipBuildCompileAndRun	= "Compile and Run";
	public String tipBuildOption		= "Build options";

	//==============================
	// Preference dialog resources
	//==============================
	
	public String PreferenceDlg_Title_Main = "Preference";
	public String PreferenceDlg_Title_Tab_General = "General";
	public String PreferenceDlg_Title_Tab_Font = "Font";
	public String PreferenceDlg_Title_Tab_Encoding = "File Encoding";
	public String PreferenceDlg_Title_Tab_Debug = "Debug";
	
	public String PreferenceDlg_Title_TargetJDK = "Target Java Development Kit (JDK)";

	public String PreferenceDlg_Title_DefaultJavaHome = "Default Java Home";
	public String PreferenceDlg_Title_CustomJavaHome = "Custom Java Home";
	public String PreferenceDlg_Title_JavaVersion = "Version -";
	public String PreferenceDlg_Title_JavaCommand = "Command -";
	public String PreferenceDlg_Title_JavaCompiler = "Compiler -";

	public String PreferenceDlg_Title_Font_Target = "Target:";
	public String PreferenceDlg_Title_Font_Family = "Name:";
	public String PreferenceDlg_Title_Font_Size   = "Size:";
	public String PreferenceDlg_Title_Font_Sample = "Sample";
	public String PreferenceDlg_Title_Target_Editor  = "Editor font";
	public String PreferenceDlg_Title_Target_Console = "Console font";
	public String PreferenceDlg_Title_Target_Compile = "Compile message font";
	
	public String PreferenceDlg_Title_Encoding_AADLSource = "AADL Source file";
	public String PreferenceDlg_Title_Encoding_AADLMacro  = "AADL Macro file";
	public String PreferenceDlg_Title_Encoding_AADLcsvFile = "AADL csvFile() default";
	public String PreferenceDlg_Title_Encoding_AADLtxtFile = "AADL txtFile() default";

	//==============================
	// Jump dialog resources
	//==============================
	
	public String JumpDlg_Title_Main = "Go to Line";
	public String JumpDlg_Title_Desc = "Please input line no.";

	//==============================
	// Find/Replace dialog resources
	//==============================
	
	public String FindDlg_Title_Main = "Find/Replace";
	public String FindDlg_Title_FindText = "Find:";
	public String FindDlg_Title_ReplaceText = "Replace by:";
	public String FindDlg_Title_Option_CaseInsensitive = "Case insensitive";
	public String FindDlg_Button_FindNext = "Find Next";
	public String FindDlg_Button_FindPrev = "Find Previous";
	public String FindDlg_Button_ReplaceNext = "Replace";
	public String FindDlg_Button_ReplaceAll = "Replace All";

	//==============================
	// Build option dialog resources
	//==============================
	
	public String BuildOptionDlg_Title_Main = "Options";
	public String BuildOptionDlg_TItle_Module = "Module options";
	public String BuildOptionDlg_Title_Project = "Project options";
	public String BuildOptionDlg_Title_Package = "Package options";
	public String BuildOptionDlg_Title_ClassPath = "Class Path";
	public String BuildOptionDlg_Title_ClassPath_Path = "Class Path:";
	public String BuildOptionDlg_Title_Run = "Run options";
	public String BuildOptionDlg_Title_Compile = "Compile options";
	public String BuildOptionDlg_Title_Compile_Compiler = "Java compiler (tools.jar)";
	public String BuildOptionDlg_Title_Compile_Options = "AADL Compile options";
	public String BuildOptionDlg_Title_Options_Dest = "Destination (-d)";
	public String BuildOptionDlg_Title_Options_SrcDir = "Source output (-sd)";
	public String BuildOptionDlg_Title_Options_NoMani = "Not create manifest (-nomanifest)";
	public String BuildOptionDlg_Title_Options_Mani = "Includes manifest (-manifest)";
	public String BuildOptionDlg_Title_Options_CompileOnly = "Compile only (-c)";
	public String BuildOptionDlg_Title_Options_NoWarn = "Disable warning (-nowarn)";
	public String BuildOptionDlg_Title_Options_Verbose = "Verbose output (-verbose)";
	//--- Labels
	public String BuildOptionDlg_Title_Run_JavaCmd = "Java command location";
	public String BuildOptionDlg_Title_Run_Target = "Target";
	public String BuildOptionDlg_Title_Run_ProgramArgs = "AADL arguments";
	public String BuildOptionDlg_Title_Run_VMArgs = "Java VM arguments";
	public String BuildOptionDlg_Title_Run_WorkDir = "Working directory";
	public String BuildOptionDlg_TItle_Module_Title = "Title:";
	public String BuildOptionDlg_Title_Module_Desc = "Description:";
	public String BuildOptionDlg_Title_Module_Note = "Note:";
	public String BuildOptionDlg_Title_Module_Args = "AADL arguments:";
	public String BuildOptionDlg_Title_Package_Title = "Title:";
	public String BuildOptionDlg_Title_Package_Desc = "Description:";
	public String BuildOptionDlg_Title_Package_MainModule = "Main module:";
	//--- Popup menus
	public String BuildOptionDlg_Menu_ArgsAddFile = "File...";
	public String BuildOptionDlg_Menu_ArgsAddText = "Text";
	public String BuildOptionDlg_Menu_ArgsEditFile = "File...";
	public String BuildOptionDlg_Menu_ArgsEditText = "Text";

	//==============================
	// Build option dialog resources
	//==============================
	
	public String LibVersionDlg_Button = "About libraries...";
	
	public String LibVersionDlg_Title_Main = "Library versions";
	public String LibVersionDlg_Title_CopyToClip = "Copy to clipboard";
	public String LibVersionDlg_Title_Info_Path = "Location";
	public String LibVersionDlg_Title_Info_LastMod = "Last modified";
	public String LibVersionDlg_Title_Info_JarTitle = "Title";
	public String LibVersionDlg_Title_Info_JarVersion = "Version";

	//==============================
	// Compile in directory dialog resources
	//==============================
	
	public String CompileInDirDlg_Title_Main = "Compile AADL files in directory";
	public String CompileInDirDlg_Title_DirChooser = "Select a target directory";
	public String CompileInDirDlg_Label_Target        = "Target:";
	public String CompileInDirDlg_Label_Location      = "Location:";
	public String CompileInDirDlg_Label_TargetFiles   = "Target files:";
	public String CompileInDirDlg_Label_TargetSuccess = "Success:";
	public String CompileInDirDlg_Label_TargetError   = "Error:";
	public String CompileInDirDlg_Label_Message       = "Message:";
	public String CompileInDirDlg_Button_SelectDir     = "Select...";
	public String CompileInDirDlg_Button_StartCompile  = "Start compile";
	public String CompileInDirDlg_Button_StopCompile   = "Stop";
	public String CompileInDirDlg_Button_WithoutSubDir = "Without sub-directories";
	public String CompileInDirDlg_Button_CopyToClip    = "Copy to clipboard";

	//==============================
	// Workspace or Package dialogs
	//==============================
	
	public String WSChooserDlg_Title		= "Select Workspace";
	public String PackImportDlg_Title		= "Import package";
	public String PackImportDlg_TreeLabel	= "Select package:";
	public String PackImportDlg_InputLabel	= "Project name to create:";
	public String PackRegistDlg_Title		= "Register package";
	public String PackRegistDlg_TreeLabel	= "Select parent folder:";
	public String PackRegistDlg_InputLabel	= "Package name:";

	//==============================
	// Messages
	//==============================

	// Confirm messages
	public String confirmOverwriteFile = "\"%s\" is already exist.\nDo you want to overwrite?";
	//public String msgToAskOverwriteFile = "\"%s\" はすでに存在します。\nこのファイルを置き換えますか？";
	public String confirmSaveChanges = "Do you want to save the changes you made?";
	//public String msgToAskSaveChanges = "変更を保存しますか？";
	public String confirmSaveDocument = "Please save the changes to file.";
	//public String msgToAskSaveDocument = "変更をファイルに保存してください。";
	public String confirmExecutePrevious = "The file has been changed.\nDo you execute it in the state before?";
	//public String msgToAskExecutePrevious = "ファイルは変更されています。\n以前の状態で実行しますか？";
	public String confirmReopenDocument = "Do you re-open in defiance of a change?";
	//public String confirmReopenDocument = "変更を無視して開きなおしますか?";
	public String confirmDeleteEditingResources = "The resource editing is going to be deleted.\nDo you want to delete?";
	//public String confirmDeleteEditingResources = "編集中のリソースも削除されようとしています。\n削除しますか?";
	public String confirmTitleSaveModifiedDocuments = "Save all the changed resources";
	//public String confirmTitleSaveModifiedDocuments = "変更されたリソースをすべて保存";
	public String confirmSaveDocumentBeforeOperation = "It is necessary to store all the changed resources before performing this operation.";
	//public String confirmSaveDocumentBeforeOperation = "この操作を行う前に、変更されたリソースをすべて保存する必要があります。";
	public String confirmFileChangedReplace = "The file has been changed on the file system. Do you want to\nreplace the editor contents with these changes?";
	//public String confirmFileChangedReplace = "ファイルはファイル・システム上で変更されています。\nエディターの内容をこれらの変更で置き換えますか?";
	public String confirmTitle_UpdateConflictReplace = "Update conflict";
	//public String confirmTitle_UpdateConflictReplace = "更新の競合";
	public String confirmUpdateConflictReplace = "The file has been changed on the file system. Do you want to\noverwrite the changes made on the file system?";
	//public String confirmUpdateConflictReplace = "ファイルはファイル・システム上で変更されています。\nファイル・システム上でおこなわれた変更を上書きしますか?";

	// Error messages
	public String msgSearchResultNotFound = "Not found.";
	public String msgSearchReplacedTokens = "%d tokens are replaced.";

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
	public String msgFileNotFound = "File not found";
	//public String msgFileNotFound = "ファイルが見つかりません。";
	public String msgUnsupportedEncoding = "Unsupported file encoding";
	//public String msgUnsupportedEncoding = "このファイルエンコーディングはサポートされていません。";
	public String msgNotFile = "Please select a file";
	//public String msgNotFile = "ファイルを選択してください。";
	public String msgNotDirectory = "Please select a directory";
	//public String msgNotDirectory = "ディレクトリを選択してください。";
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

	public String msgWorkspaceNoSelection = "Please select a workspace.";
	//public String msgWorkspaceNoSelection = "ワークスペースを選択してください。";
	public String msgWorkspaceNotFound = "Workspace not found.";
	//public String msgWorkspaceNotFound = "選択されたワークスペースが見つかりません。";
	public String msgWorkspaceNotDirectory = "Workspace is not folder.";
	//public String msgWorkspaceNotDirectory = "選択されたワークスペースはフォルダではありません。";
	public String msgWorkspaceCouldNotWrite = "Cannot write access to Workspace.";
	//public String msgWorkspaceCouldNotWrite = "選択されたワークスペースには書き込みできません。";
	public String msgWorkspaceCouldNotAccess = "Cannot access to Workspace";
	//public String msgWorkspaceCouldNotAccess = "選択されたワークスペースにアクセスできません。";

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
		EditorMessages appmsg = EditorMessages.getInstance();
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
