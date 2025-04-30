/*
 * @(#)DtContainerEditorMessages.java	2.0.0	2025/02/17
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerEditorMessages.java	1.1.0	2023/01/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DtContainerEditorMessages.java	1.0.0	2022/11/18
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor;

import java.util.ResourceBundle;

import ssac.util.Strings;
import ssac.util.io.FieldResource;
import ssac.util.logging.AppLogger;

/**
 * データコンテナエディタの文字列リソース。
 * 
 * @version 2.0.0
 */
public class DtContainerEditorMessages extends FieldResource
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
	static private DtContainerEditorMessages instance = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected String getResourceName() {
		return DtContainerEditorMessages.class.getName();
	}
	
	static public DtContainerEditorMessages getInstance() {
		if (instance == null) {
			instance = new DtContainerEditorMessages();
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
		DtContainerEditorMessages appmsg = DtContainerEditorMessages.getInstance();
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
			case MessageID.ERR_FILE_UNSUPPORTED_FORMAT :
				retmsg = appmsg.msgUnsupportedFileFormat;
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
			case MessageID.ERR_SYSTEM :
				retmsg = appmsg.msgSystemError;
				break;
			//--- default error message
			default :
				retmsg = appmsg.msgUnexpectedError;
		}
		return formatErrorMessage(retmsg, cause, params);
	}

	//------------------------------------------------------------
	// Error message IDs
	//------------------------------------------------------------
	
	static public final class MessageID {
		static public final int ERR_UNEXPECTED					= 9999;
		static public final int ERR_SYSTEM						= 1001;
		static public final int ERR_FILE_READ					= 1002;
		static public final int ERR_FILE_WRITE					= 1003;
		static public final int ERR_FILE_NOTFILE				= 1004;
		static public final int ERR_FILE_NOTFOUND				= 1005;
		static public final int ERR_FILE_UNSUPPORTED_ENCODING	= 1006;
		static public final int ERR_FILE_UNSUPPORTED_FORMAT		= 1007;
		static public final int ERR_FILE_SETTING_WRITE			= 1008;
		static public final int ERR_FILE_CREATE					= 1009;
		static public final int ERR_FILE_RENAME					= 1010;
	}

	//------------------------------------------------------------
	// Messages
	//------------------------------------------------------------
	
	public String appMainTitle	= "Data-Container Editor";
	
	// Root content names
	public String rootContentName_DtBinder	= "Data binder";
	//public String rootContentName_DtBinder	= "データバインダー";
	public String rootContentName_DtSlip	= "Data slip";
	//public String rootContentName_DtSlip	= "データスリップ";
	
	// Content types
	public String contentName_DtBinderRoot	= "Data binder (DtBinder)";
	//public String contentName_DtBinderRoot	= "データバインダー (DtBinder)";
	public String contentName_DtBinderNote	= "Note (DtBinder)";
	//public String contentName_DtBinderNode	= "Note (DtBinder)";
	public String contentName_DtBinderSlips	= "Slips (DtBinder)";
	//public String contentName_DtBinderSlips	= "Slips (DtBinder)";
	public String contentName_DtSlipList	= "Data slip list (DtSlipList)";
	//public String contentName_DtSlipList	= "データスリップ集合 (DtSlipList)";
	public String contentName_DtSlipRoot	= "Data slip (DtSlip)";
	//public String contentName_DtSlipRoot	= "データスリップ (DtSlip)";
	public String contentName_DtSlipNote	= "Note (DtSlip)";
	//public String contentName_DtSlipNode	= "Note (DtSlip)";
	public String contentName_DtSlipObjects	= "Objects (DtSlip)";
	//public String contentName_DtSlipObjects	= "Objects (DtSlip)";
	public String contentName_DtAlgeSet		= "Data algebra set (DtAlgeSet)";
	//public String contentName_DtAlgeSet		= "データ代数集合 (DtAlgeSet)";
	public String contentName_Dtalge		= "Data algebra element (Dtalge)";
	//public String contentName_Dtalge		= "データ代数元 (Dtalge)";
	public String contentName_ExAlgeSet		= "Exchange algebra set (ExAlgeSet)";
	//public String contentName_ExAlgeSet		= "交換代数集合 (ExAlgeSet)";
	public String contentName_Exalge		= "Exchange algebra element (Exalge)";
	//public String contentName_Exalge		= "交換代数元 (Exalge)";
	
	// Fixed content names
	public String contentNodeName_note		= "note";
	public String contentNodeName_objects	= "objects";
	public String contentNodeName_slips		= "slips";
	
	public String DtSlip_docTypeName	= "DtSlip (JSON) File";
	public String DtSlip_docTypeDesc	= "DtSlip (JSON) File Editor";
	public String DtBinder_docTypeName	= "DtBinder (JSON) File";
	public String DtBinder_docTypeDesc	= "DtBinder (JSON) File Editor";
	
	// File extentions
	public String descExtCSV			= "CSV file (*.csv)";
	//public String descExtCSV			= "CSV ファイル (*.csv)";
	public String extCSV				= ".csv";
	public String descExtXML			= "XML file (*.xml)";
	//public String descExtXML			= "XML ファイル (*.xml)";
	public String extXML				= ".xml";
	public String descExtJSON			= "JSON file (*.json)";
	//public String descExtJSON			= "JSON ファイル (*.json)";
	public String extJSON				= ".json";
	//public String descExtDocument		= "Data container (DtBinder, DtSlip) file (*.dtbi,*.dtsp)";
	//public String descExtDocument		= "データコンテナ (DtBinder, DtSlip) ファイル (*.dtbi,*.dtsp)";
	//public String extDocument			= ".dtbi;.dtsp";
	//public String descExtDtBinder		= "Data binder (DtBinder) file (*.dtbi)";
	//public String descExtDtBinder		= "データバインダー (DtBinder) ファイル (*.dtbi)";
	//public String extDtBinder			= ".dtbi";
	//public String descExtSlipObject		= "Data slip object file (*.dtsp,*.dtsl)";
	//public String descExtSlipObject		= "データスリップオブジェクトファイル (*.dtsp,*.dtsl)";
	//public String extSlipObject			= ".dtsp;.dtsl";
	//public String descExtDtSlipList		= "Data slips (DtSlipList) file (*.dtsl)";
	//public String descExtDtSlipList		= "データスリップ集合 (DtSlipList) file (*.dtsl)";
	//public String extDtSlipList			= ".dtsl";
	//public String descExtDtSlipSingle	= "DtSlip file (*.dtsp)";
	//public String descExtDtSlipSingle	= "データスリップ (DtSlip) ファイル (*.dtsp)";
	//public String extDtSlipSingle		= ".dtsp";
	
	// Menu text
	//--- [File] menu
	public String menuFile				= "File";
	//public String menuFile				= "ファイル(F)";
	public String menuFileNew			= "New";
	//public String menuFileNew			= "新規作成(N)";
	public String menuFileNewSlipSingle	= "Data Slip (DtSlip)";
	//public String menuFileNewSlipSingle	= "データスリップ (DtSlip)";
	public String menuFileNewBinder		= "Data Binder (DtBinder)";
	//public String menuFileNewBinder		= "データバインダー (DtBinder)";
	public String menuFileOpen			= "Open...";
	//public String menuFileOpen			= "開く(O)...";
	public String menuFileOpenAs		= "Open As";
	//public String menuFileOpenAs		= "形式を指定して開く(P)";
	public String menuFileSave			= "Save";
	//public String menuFileSave			= "保存(S)";
	public String menuFileSaveAs		= "Save As...";
	//public String menuFileSaveAs		= "名前を付けて保存(A)...";
	public String menuFileClose			= "Close";
	//public String menuFileClose			= "閉じる(C)";
	public String menuFileAllClose		= "Close All";
	//public String menuFileAllClose		= "全て閉じる(L)";
	public String menuFileImport		= "Import";
	//public String menuFileImport		= "インポート(I)";
	public String menuFileImportBinderFromDoubleEntrySimpleSlipsCsv = "Double-Entry Simple Slips CSV as Data Binder (DtBinder)...";
	//public String menuFileImportBinderFromDoubleEntrySimpleSlipsCsv = "複式記述簡易データ伝票CSV => データバインダー (DtBinder)";
	public String menuFileExport		= "Export";
	//public String menuFileExport		= "エクスポート(E)";
	public String menuFileExportBinderToDoubleEntrySimpleSlipsCsv = "Data Binder (DtBinder) as Double-Entry Simple Slips CSV...";
	//public String menuFileExportBinderToDoubleEntrySimpleSlipsCsv = "データバインダー (DtBinder) => Double-Entry Simple Slips CSV";
	public String menuFilePreference	= "Preferences...";
	//public String menuFilePreference	= "設定(R)...";
	public String menuFileQuit			= "Quit";
	//public String menuFileQuit			= "終了(X)";
	//--- [Edit] menu
	public String menuEdit			= "Edit";
	//public String menuEdit			= "編集(E)";
	public String menuEditUndo		= "Undo";
	//public String menuEditUndo		= "元に戻す(U)";
	public String menuEditRedo		= "Redo";
	//public String menuEditRedo		= "やり直し(R)";
	public String menuEditCut		= "Cut";
	//public String menuEditCut		= "切り取り(T)";
	public String menuEditCopy		= "Copy";
	//public String menuEditCopy		= "コピー(C)";
	public String menuEditPaste		= "Paste";
	//public String menuEditPaste		= "貼り付け(P)";
	public String menuEditDelete	= "Delete";
	//public String menuEditDelete	= "削除(L)";
	//--- [Tree] menu
	public String menuTree			= "Tree";
	//public String menuTree			= "ツリー(R)";
	public String menuTreeAdd		= "Add...";
	//public String menuTreeAdd		= "追加(A)...";
	public String menuTreeReplace	= "Replace...";
	//public String menuTreeReplace	= "変更(R)...";
	public String menuTreeCut		= "Cut";
	//public String menuTreeCut		= "切り取り(T)";
	public String menuTreeCopy		= "Copy";
	//public String menuTreeCopy		= "コピー(C)";
	public String menuTreePaste		= "Paste";
	//public String menuTreePaste		= "貼り付け(P)";
	public String menuTreeDelete	= "Delete";
	//public String menuTreeDelete	= "削除(L)";
	public String menuTreeExport	= "Export...";
	//public String menuTreeExport	= "エクスポート(E)...";
	public String menuTreeRename	= "Rename...";
	//public String menuTreeRename	= "名前変更(M)...";
	public String menuTreeMoveUp	= "Move Up";
	//public String menuTreeMoveUp	= "上へ移動(U)";
	public String menuTreeMoveDown	= "Move Down";
	//public String menuTreeMoveDown	= "下へ移動(D)";
	//--- [Table] menu
	public String menuTable					= "Table";
	//public String menuTable					= "テーブル(T)";
	public String menuTableCut		= "Cut";
	//public String menuTableCut		= "切り取り(T)";
	public String menuTableCopy		= "Copy";
	//public String menuTableCopy		= "コピー(C)";
	public String menuTablePaste		= "Paste";
	//public String menuTablePaste		= "貼り付け(P)";
	public String menuTableDelete	= "Delete";
	//public String menuTableDelete	= "削除(L)";
	public String menuTableRowsInsertAbove	= "Insert rows above";
	//public String menuTableRowsInsertAbove	= "行の挿入（上）(A)";
	public String menuTableRowsInsertBelow	= "Insert rows below";
	//public String menuTableRowsInsertBelow	= "行の挿入（下）(B)";
	public String menuTableRowsInsertCopied	= "Insert copied cells";
	//public String menuTableRowsInsertCopied	= "コピーしたセルを挿入(E)";
	public String menuTableRowsCut			= "Cut rows";
	//public String menuTableRowsCut			= "行の切り取り(T)";
	public String menuTableRowsDelete		= "Delete rows";
	//public String menuTableRowsDelete		= "行の削除(D)";
	public String menuTableRowsSelect		= "Select rows";
	//public String menuTableRowsSelect		= "行の選択(S)";
	//--- [Help] menu
	public String menuHelp		= "Help";
	//public String menuHelp		= "ヘルプ(H)";
	public String menuHelpAbout	= "About...";
	//public String menuHelpAbout	= "バージョン情報(A)...";
	
	// Toolbar tooltips
	public String tipFileOpen		= "Open...";
	//public String tipFileOpen		= "開く...";
	public String tipFileSave		= "Save...";
	//public String tipFileSave		= "保存...";
	public String tipEditUndo	= "Undo";
	//public String tipEditUndo	= "元に戻す";
	public String tipEditRedo	= "Redo";
	//public String tipEditRedo	= "やり直し";
	public String tipEditCut		= "Cut";
	//public String tipEditCut		= "切り取り";
	public String tipEditCopy		= "Copy";
	//public String tipEditCopy		= "コピー";
	public String tipEditPaste		= "Paste";
	//public String tipEditPaste		= "貼り付け";
	public String tipEditDelete		= "Delete";
	//public String tipEditDelete		= "削除";
	public String tipTreeAdd		= "Add...";
	//public String tipTreeAdd		= "追加...";
	public String tipTreeReplace	= "Replace...";
	//public String tipTreeReplace	= "変更...";
	public String tipTreeExport		= "Export...";
	//public String tipTreeExport		= "エクスポート...";
	public String tipTreeMoveUp		= "Move Up";
	//public String tipTreeMoveUp		= "上へ移動";
	public String tipTreeMoveDown	= "Move Down";
	//public String tipTreeMoveDown	= "下へ移動";
	public String tipTreeRename		= "Rename...";
	//public String tipTreeRename		= "名前変更...";
	public String tipTableRowInsertAbove	= "Insert rows above";
	//public String tipTableRowInsertAbove	= "行の挿入（上）";
	public String tipTableRowInsertBelow	= "Insert rows below";
	//public String tipTableRowInsertBelow	= "行の挿入（下）";
	public String tipTableRowCut			= "Cut rows";
	//public String tipTableRowCut			= "行の切り取り";
	public String tipTableRowDelete			= "Delete rows";
	//public String tipTableRowDelete			= "行の削除";
	
	//
	// Messages
	//
	
	public String msgUnexpectedError = "Unexpected error";
	//public String msgUnexpectedError = "予期せぬエラーが発生しました。";
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
	public String msgUnsupportedFileFormat = "Unsupported file format";
	//public String msgUnsupportedFileFormat = "このファイルは、サポートされていない形式です。";
	public String msgNotFile = "Please select a file";
	//public String msgNotFile = "ファイルを選択してください。";
	public String msgNotDirectory = "Please select a directory";
	//public String msgNotDirectory = "ディレクトリを選択してください。";
	public String msgCannotWriteCauseReadOnly = "File '%s' is read only. Cannot save it.";
	//public String msgCannotWriteCauseReadOnly = "ファイル '%s' は読み取り専用です。保存できません。";
	public String msgNoDestinationFile	= "Please specify destination file.";
	//public String msgNoDestinationFile	= "出力先ファイルを指定してください。";
	public String msgUnexpectedContentFormat	= "The specified file is not format as %s (%s).";
	//public String msgUnexpectedContentFormat	= "指定されたファイルは、%s (%s) の形式ではありません。";
	public String msgNotSelectedContentType		= "Please select object type.";
	//public String msgNotSelectedContentType		= "オブジェクトの種類を選択してください。";
	public String msgInvalidDtalgeDecimalValue	= "Invalid value as decimal type";
	//public String msgInvalidDtalgeDecimalValue	= "数値(decimal)型の値ではありません";
	public String msgInvalidDtalgeBooleanValue	= "Please input \"true\" or \"false\" for boolean type value";
	//public String msgInvalidDtalgeBooleanValue	= "真偽値(boolean)型の値には、\"true\" もしくは \"false\" を指定してください";
	public String msgCouldNotConvertToDtalgeFromEdited	= "It contains value that could not convert to Dtalge value.";
	//public String msgCouldNotConvertToDtalgeFromEdited	= "データ代数に変換できない値が含まれています。";
	public String msgErrorEmptyNameKeyAlthoughOtherCellHasValue	= "[name] column is empty although other column has value.";
	//public String msgErrorEmptyNameKeyAlthoughOtherCellHasValue	= "[name] 列は空欄ですが、他の列には値が指定されています。";
	public String msgWarnEmptyNameKeyAlthoughOtherCellHasValue	= "[name] column is empty although other column has value.\nSo this row will be excluded when saving.";
	//public String msgWarnEmptyNameKeyAlthoughOtherCellHasValue	= "[name] 列は空欄ですが、他の列には値が指定されています。\nそのため、この行は保存時に除外されます。";

	public String msgDataBinderEmpty = "Data Binder is empty.";
	//public String msgDataBinderEmpty = "データバインダーの要素がありません。";
	public String msgDataBinderNotIncludeExalgeElements = "There is no Data Slip includes Exalge element in the Data Binder.";
	//public String msgDataBinderNotIncludeExalgeElements = "データバインダーに交換代数要素を持つデータスリップが存在しません。";
	public String msgCouldNotExportPresentBinder = "The present Data Binder cannot export, it is incomplete.";
	//public String msgCouldNotExportPresentBinder = "現在のデータバインダーの内容では、エクスポートできません";
	public String msgUnselectedDebitCreditDefTableCsvFile = "Please specify Debit-Credit Definition CSV file.";
	//public String msgUnselectedDebitCreditDefTableCsvFile = "貸借科目定義 CSV ファイルを指定してください。";
	public String msgUnselectedDoubleEntrySimpleSlipsCsvFile = "Please select Double-Entry Simple Slips CSV file.";
	//public String msgUnselectedDoubleEntrySimpleSlipsCsvFile = "複式記述簡易データ伝票 CSV ファイルを指定してください。";
	public String msgFailedToReadDebitCreditDefTableCsvFile = "Failed to read Debit-Credit Definition CSV file.";
	//public String msgFailedToReadDebitCreditDefTableCsvFile = "貸借科目定義 CSV ファイルの読み込みに失敗しました。";
	public String msgFailedToImportDoubleEntrySimpleSlipsCsvFile = "Failed to import Double-Entry Simple Slips CSV file.";
	//public String msgFailedToImportDoubleEntrySimpleSlipsCsvFile = "複式記述簡易データ伝票 CSV ファイルのインポートに失敗しました。";
	public String msgFailedToExportDoubleEntrySimpleSlipsCsvFile = "Failed to export Double-Entry Simple Slips CSV file.";
	//public String msgFailedToExportDoubleEntrySimpleSlipsCsvFile = "複式記述簡易データ伝票 CSV ファイルのエクスポートに失敗しました。";
	
	//
	// Confirmation messages
	//
	public String confirmOverwriteFileParam1 = "\"%s\" is already exist.\nDo you want to overwrite?";
	//public String confirmOverwriteFileParam1 = "\"%s\" はすでに存在します。\nこのファイルを置き換えますか？";
	public String confirmDestFilesOverwrite	= "Destination files already exist.\nDo you want to overwrite?";
	//public String confirmDestFilesOverwrite	= "出力先ファイルはすでに存在しています。\n上書きしますか？";
	public String confirmDeleteTreeNode	= "Do you want to delete the selected object?";
	//public String confirmDeleteTreeNode	= "選択されたオブジェクトを削除してよろしいですか？";
	public String confirmSaveChanges = "Do you want to save the changes you made?";
	//public String confirmSaveChanges = "変更を保存しますか？";
	public String confirmSaveDocument = "Please save the changes to file.";
	//public String confirmSaveDocument = "変更をファイルに保存してください。";
	public String confirmExecutePrevious = "The file has been changed.\nDo you execute it in the state before?";
	//public String confirmExecutePrevious = "ファイルは変更されています。\n以前の状態で実行しますか？";
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
	public String confirmReplaceObjectAlreadyExistName	= "The specified object name already exists.\nDo you want to replace?";
	//public String confirmReplaceObjectAlreadyExistName	= "入力されたオブジェクト名はすでに存在しています。\n置き換えますか？";
	public String titleConfirmPasteWithPath = "Paste to [%s]";
	//public String titleConfirmPasteWithPath = "[%s] への貼り付け";
	public String confirmPasteNoteAndReplace = "Do you want to replace the selected 'note' object?";
	//public String confirmPasteNoteAndReplace = "選択されたノートオブジェクトを置き換えてよろしいですか？";
	public String confirmPasteSameNameObjectAndReplace = "\"%s\" object already exists.\n Do you want to replace it?";
	//public String confirmPasteSameNameObjectAndReplace = "\"%s\" オブジェクトはすでに存在しています。\n置き換えますか？";
	public String confirmExportEditingTarget = "Do you want to export the document that is editing?";
	//public String confirmExportEditingTarget = "編集中の内容でエクスポートしてよろしいですか？";
	
	//
	// view
	//
	public String editingDocumentModifier = "* ";
	public String newDocumentTitle = "Untitled";
	
	public String Overwrite_option_Rename	= "Rename";
	//public String Overwrite_option_Rename	= "名前変更";

	public String editor_lbl_ObjectName			= "Object name";
	//public String editor_lbl_ObjectName			= "オブジェクト名";
	public String editor_lbl_ObjectPath			= "Object path";
	//public String editor_lbl_ObjectPath			= "オブジェクトパス";
	public String RenameDlg_title				= "Rename object";
	//public String RenameDlg_title				= "オブジェクト名の変更";
	public String RenameDlg_label				= "New name";
	//public String RenameDlg_label				= "新しい名前";
	public String ObjectNameValidator_ErrorTitle	= "Input error in object name";
	//public String ObjectNameValidator_ErrorTitle	= "オブジェクト名の入力エラー";
	public String ObjectNameValidator_DefaultError	= "Illegal object name.";
	//public String ObjectNameValidator_DefaultError	= "オブジェクト名が適切ではありません。";
	public String ObjectNameValidator_Empty			= "Please input object name.";
	//public String ObjectNameValidator_Empty			= "オブジェクト名を入力してください。";
	public String ObjectNameValidator_AlreadyExists	= "The specified object name already exists.";
	//public String ObjectNameValidator_AlreadyExists	= "入力されたオブジェクト名はすでに存在しています。";
	
	//
	// Exalge edit table
	//
	
	public String ExalgeEditor_colname_value	= "value";
	public String ExalgeEditor_colname_hat		= "^";
	public String ExalgeEditor_colname_name		= "name";
	public String ExalgeEditor_colname_unit		= "unit";
	public String ExalgeEditor_colname_time		= "time";
	public String ExalgeEditor_colname_subject	= "subject";
	
	//
	// Dtalge edit table
	//
	
	public String DtalgeEditor_colname_value	= "value";
	public String DtalgeEditor_colname_name		= "name";
	public String DtalgeEditor_colname_type		= "type";
	public String DtalgeEditor_colname_attr		= "attr";
	public String DtalgeEditor_colname_subject	= "subject";
	
	//
	// Content Import/Export Dialog
	//
	
	public String ContentImportDlg_title_InsertObject		= "Insert new object";
	//public String ContentImportDlg_title_InsertObject			= "オブジェクトの新規追加";
	public String ContentImportDlg_title_ReplaceObject		= "Replace object";
	//public String ContentImportDlg_title_ReplaceObject		= "オブジェクトの変更";
	public String ContentImportDlg_lbl_ContentType			= "Object type";
	//public String ContentImportDlg_lbl_ContentType			= "オブジェクトの種類";
	public String ContentImportDlg_lbl_ParentPath			= "Parent path";
	//public String ContentImportDlg_lbl_ParentPath			= "親オブジェクトのパス";
	public String ContentImportDlg_lbl_ObjectPath			= "Object path";
	//public String ContentImportDlg_lbl_ObjectPath			= "オブジェクトパス";
	public String ContentImportDlg_lbl_ObjectName			= "Object name";
	//public String ContentImportDlg_lbl_ObjectName			= "オブジェクト名";
	public String ContentImportDlg_lbl_InsertPosition		= "Insert position";
	//public String ContentImportDlg_lbl_InsertPosition		= "挿入位置";
	public String ContentImportDlg_lbl_ObjectEmpty			= "Empty object";
	//public String ContentImportDlg_lbl_ObjectEmpty			= "空のオブジェクト";
	public String ContentImportDlg_lbl_ObjectFromFile		= "Import from file";
	//public String ContentImportDlg_lbl_ObjectFromFile		= "ファイルからインポート";
	public String ContentImportDlg_lbl_FileDir				= "File location";
	//public String ContentImportDlg_lbl_FileDir				= "ファイルの場所";
	public String ContentImportDlg_lbl_FileName				= "File name";
	//public String ContentImportDlg_lbl_FileName				= "ファイル名";
	public String ContentImportDlg_lbl_FileFormat			= "File format";
	//public String ContentImportDlg_lbl_FileFormat			= "ファイル形式";
	public String ContentImportDlg_btn_ChooseFile			= "Choose file...";
	//public String ContentImportDlg_btn_ChooseFile			= "ファイルを選択...";
	public String ContentImportDlg_title_FileChooser		= "Choose file";
	//public String ContentImportDlg_title_FileChooser		= "ファイルを選択";
	
	
	public String ContentExportDlg_title				= "Export object";
	//public String ContentExportDlg_title				= "オブジェクトのエクスポート";
	public String ContentExportDlg_lbl_ContentType		= "Object type";
	//public String ContentExportDlg_lbl_ContentType		= "オブジェクトの種類";
	public String ContentExportDlg_lbl_ObjectPath		= "Object path";
	//public String ContentExportDlg_lbl_ObjectPath		= "オブジェクトパス";
	public String ContentExportDlg_lbl_FileDir			= "File location";
	//public String ContentExportDlg_lbl_FileDir			= "ファイルの場所";
	public String ContentExportDlg_lbl_FileName			= "File name";
	//public String ContentExportDlg_lbl_FileName		= "ファイル名";
	public String ContentExportDlg_lbl_FileFormat		= "File format";
	//public String ContentExportDlg_lbl_FileFormat		= "ファイル形式";
	public String ContentExportDlg_btn_ChooseFile		= "Choose destination file...";
	//public String ContentExportDlg_btn_ChooseFile		= "出力先ファイルを選択...";
	public String ContentExportDlg_title_FileChooser	= "Destination file";
	//public String ContentExportDlg_title_FileChooser	= "出力先ファイル";
	
	//===============================================================
	// 複式記述簡易データ伝票CSV
	//===============================================================
	
	public String DoubleEntrySimpleSlipsCsvFile_caption		= "Double-Entry Simple Slips CSV file";
	//public String DoubleEntrySimpleSlipsCsvFile_caption		= "複式記述簡易データ伝票CSVファイル";
	public String DebitCreditDefinitionCsvFile_caption		= "Debit-Credit Definition CSV file";
	//public String DebitCreditDefinitionCsvFile_caption		= "貸借科目定義CSVファイル";
	public String DebitCreditDefinitionCsvFIle_title_FileChooser	= "Choose Debit-Credit Definition CSV file";
	//public String DebitCreditDefinitionCsvFIle_title_FileChooser	= "貸借科目定義 CSV ファイルの選択";
	
	public String DoubleEntrySimpleSlipsCsvDlg_title_import = "[Data Binder] Import from Double-Entry Simple Slips CSV";
	//public String DoubleEntrySimpleSlipsCsvDlg_title_import = "[データバインダー] 複式記述簡易データ伝票 CSV からのインポート";
	public String DoubleEntrySimpleSlipsCsvDlg_title_export = "[Data Binder] Export to Double-Entry Simple Slips CSV";
	//public String DoubleEntrySimpleSlipsCsvDlg_title_export = "[データバインダー] 複式記述簡易データ伝票 CSV へのエクスポート";

	public String DoubleEntrySimpleSlipsCsvDlg_lbl_FileDir	= "File location";
	//public String DoubleEntrySimpleSlipsCsvDlg_lbl_FileDir	= "ファイルの場所";
	public String DoubleEntrySimpleSlipsCsvDlg_lbl_FileName	= "File name";
	//public String DoubleEntrySimpleSlipsCsvDlg_lbl_FileName	= "ファイル名";
	public String DoubleEntrySimpleSlipsCsvDlg_btn_NowReading = "Now loading...";
	//public String DoubleEntrySimpleSlipsCsvDlg_btn_NowReading = "読み込み中...";
	public String DoubleEntrySimpleSlipsCsvDlg_btn_OpenFileChooser = "Choose file...";
	//public String DoubleEntrySimpleSlipsCsvDlg_btn_OpenFileChooser = "ファイルを選択...";
	public String DoubleEntrySimpleSlipsCsvDlg_btn_SaveFileChooser = "Choose destination file...";
	//public String DoubleEntrySimpleSlipsCsvDlg_btn_SaveFileChooser = "出力先ファイルを選択...";
	public String DoubleEntrySimpleSlipsCsvDlg_lbl_charset = "Character-set";
	//public String DoubleEntrySimpleSlipsCsvDlg_lbl_charset = "文字コード";
	
	public String DoubleEntrySimpleSlipsCsvDlg_title_OpenFileChooser = "Choose Double-Entry Simple Slips CSV file";
	//public String DoubleEntrySimpleSlipsCsvDlg_title_OpenFileChooser = "複式記述簡易データ伝票CSVファイルの選択";
	public String DoubleEntrySimpleSlipsCsvDlg_title_SaveFileChooser = "Choose destination file";
	//public String DoubleEntrySimpleSlipsCsvDlg_title_SaveFileChooser = "複式記述簡易データ伝票CSVファイルの出力先";

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
