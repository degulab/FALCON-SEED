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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)RunnerMessages.java	3.3.0	2016/05/31
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMessages.java	3.2.1	2015/07/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMessages.java	3.2.0	2015/06/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMessages.java	3.1.3	2015/05/22 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMessages.java	3.1.0	2014/05/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMessages.java	3.0.0	2014/03/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMessages.java	2.1.0	2013/08/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMessages.java	2.0.0	2012/11/08
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMessages.java	1.22	2012/08/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMessages.java	1.20	2012/03/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMessages.java	1.13	2012/02/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMessages.java	1.10	2011/02/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)RunnerMessages.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner;

import java.util.ResourceBundle;

import ssac.util.io.FieldResource;
import ssac.util.logging.AppLogger;

/**
 * モジュールランナーの文字列リソース。
 * 
 * @version 3.3.0
 */
public class RunnerMessages extends FieldResource
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/**
	 * 文字列リソースの唯一のインスタンス
	 */
	static private RunnerMessages instance = null;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected String getResourceName() {
		return RunnerMessages.class.getName();
	}
	
	static public RunnerMessages getInstance() {
		if (instance == null) {
			instance = new RunnerMessages();
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
	
	public String appMainTitle	= "Module Runner";
	
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
	public String descExcel		= "Excel file (*.xlsx,*.xls)";
	public String extExcel		= ".xlsx;.xls";
	public String descExtPNG	= "PNG (*.png)";
	public String extPNG		= ".png";
	public String descExtDOT	= "GraphViz dot file (*.dot)";
	public String extDOT		= ".dot";
	public String descAADLArg	= "AADL Argument file (*.csv,*.xml,*.txt)";
	public String extAADLArg	= ".csv;.xml;.txt";
	public String descMExecDefFile	= "Filter Definition file (*.med)";
	public String extMExecDefFile	= ".med";

	public String chooserTitleWorkDir		= "Select working directory";
	public String chooserTitleProgArgs		= "Select argument file for AADL";
	
	// Menu text
	//--- menu item for Tree only
	public String menuTreeFileOpen			= "Open";
	public String menuTreeFileOpenTyped		= "Open by Type";
	public String menuTreeFileOpenTypedCsv	= "CSV...";
	//--- [File] menu
	public String menuFile				= "File";
	public String menuFileNew			= "New";
	public String menuFileNewModuleExecDef	= "Filter...";
	public String menuFileNewGenericFilter	= "Generic Filter...";
	public String menuFileNewMacroFilter	= "Filter Macro...";
	public String menuFileNewFolder		= "Folder...";
	public String menuFileOpen			= "Open...";
	public String menuFileOpenConfigCsv	= "Open for CSV...";
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
	public String menuFileSelectUserRoot	= "Select User Folder";
	public String menuFileSelectUserRootMExecDef	= "Filter...";
	public String menuFileSelectUserRootDataFile	= "Data File...";
	public String menuFilePreference	= "Preference...";
	public String menuFileImport		= "Import...";
	public String menuFileExport		= "Export...";
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
	public String menuEditExecDef	= "Edit Filter...";
	//--- [Find] menu
	public String menuFind			= "Find";
	public String menuFindFind		= "Find...";	//"Find/Replace...";
	public String menuFindNext		= "Find Next";
	public String menuFindPrevious	= "Find Previous";
	//--- [Fitler] menu
	public String menuFilter						= "Filter";
	public String menuFilterRun						= "Run...";
	public String menuFilterEdit					= "Edit...";
	public String menuFilterCreateByHistory			= "New By History";
	public String menuFilterRecordHistory			= "Record History";
	public String menuFilterHistoryRun				= "Run History";
	public String menuFilterHistoryRunLatest		= "Latest";
	public String menuFilterHistoryRunFrom			= "From selection";
	public String menuFilterHistoryRunBefore		= "Before selection";
	public String menuFilterHistoryRunSelected		= "Selections";
	public String menuFilterHistoryRunAs			= "Run History As";
	public String menuFilterHistoryRunAsLatest		= "Latest...";
	public String menuFilterHistoryRunAsFrom		= "From selection...";
	public String menuFilterHistoryRunAsBefore		= "Before selection...";
	public String menuFilterHistoryRunAsSelected	= "Selections...";
	//--- [Tool] menu
	public String menuTool				= "Tool";
	public String menuToolChartMenu		= "Chart";
	public String menuToolChartScatter	= "Scatter...";
	public String menuToolChartLine		= "Line...";
	public String menuToolExcel2Csv		= "Excel to CSV...";
	//--- [Help] menu
	public String menuHelp		= "Help";
	public String menuHelpAbout	= "About...";

	// Toolbar tooltips
	public String tipFileNew	= "New %s";
	public String tipFileOpen	= "Open...";
	public String tipFileOpenConfigCsv	= "Open for CSV...";
	public String tipFileSave	= "Save";
	public String tipFileRefresh	= "Refresh";
	public String tipFileSelectUserRootMExecDef	= "[Filter] Choose a User Folder...";
	public String tipFileSelectUserRootDataFile	= "[Data File] Choose a User Folder...";
	public String tipEditUndo	= "Undo";
	public String tipEditRedo	= "Redo";
	public String tipEditCut	= "Cut";
	public String tipEditCopy	= "Copy";
	public String tipEditPaste	= "Paste";
	public String tipFindFind	= "Find...";	//"Find/Replace";
	public String tipFindPrev	= "Find Previouse";
	public String tipFindNext	= "Find Next";
	public String tipRunRun		= "Run";
	public String tipShowExecArgsDlg	= "Brings Filter arguments dialog to the front.";
	public String tipShowChartWindow	= "Brings Chart Window to the front.";
	public String tipFilterRun					= "Run filter...";
	public String tipFilterEdit					= "Edit filter...";
	public String tipFilterCreateByHistory		= "Create filter by history...";
	public String tipFilterRecordHistory		= "Toggle history recoring on/off state";
	public String tipFilterHistoryRunLatest		= "Run latest history";
	public String tipFilterHistoryRunFrom		= "Run histories from selection";
	public String tipFilterHistoryRunBefore		= "Run histories before selection";
	public String tipFilterHistoryRunSelected	= "Run selected histories";
	public String tipFilterHistoryRunAsLatest	= "Run latest history with arguments...";
	public String tipFilterHistoryRunAsFrom		= "Run histories from selection with arguments of first filter...";
	public String tipFilterHistoryRunAsBefore	= "Run histories before selection with arguments of first filter...";
	public String tipFilterHistoryRunAsSelected	= "Run selected histories with arguments of first filter...";
	public String tipFilterHistoryDelete		= "Delete selected histories";
	public String tipFilterHistorySelectAll		= "Select all histories";
	
	// MExecDef keywords
	public String MExecDef_system	= "System";
	public String MExecDef_user		= "User";
	public String DataFile_system	= "System Data";
	public String DataFile_user		= "User Data";
	
	public String TreeTabViewTitle_MExecDef	= "Filter";
	public String TreeTabViewTitle_DataFile	= "Data File";
	
	public String ToolTabViewTitle_History	= "History";
	public String ToolTabViewTitle_Running = "Running";

	public String MExecDefFolderChooser_Title					= "Choose a Folder";
	public String MExecDefFileChooser_Title						= "Choose a Filter";
	public String MExecDefFileChooser_TreeLabel_SelectFilter	= "Choose a Filter";

	//==============================
	// Jump dialog resources
	//==============================
	
	public String JumpDlg_Title_Main = "Go to Line";
	public String JumpDlg_Title_Desc = "Please input line no.";

	//==============================
	// Find/Replace dialog resources
	//==============================
	
	public String FindDlg_Title_Main_WithReplace = "Find/Replace";
	public String FindDlg_Title_Main_FindOnly = "Find";
	public String FindDlg_Title_FindText = "Find:";
	public String FindDlg_Title_ReplaceText = "Replace by:";
	public String FindDlg_Title_Option_CaseInsensitive = "Case insensitive";
	public String FindDlg_Button_FindNext = "Find Next";
	public String FindDlg_Button_FindPrev = "Find Previous";
	public String FindDlg_Button_ReplaceNext = "Replace";
	public String FindDlg_Button_ReplaceAll = "Replace All";

	//==============================
	// Preference Dialog resources
	//==============================
	
	public String PreferenceDlg_Title_Main = "Preference";
	public String PreferenceDlg_Title_Tab_General = "General";
	public String PreferenceDlg_Title_Tab_Java = "Java";
	public String PreferenceDlg_Title_Tab_Font = "Font";
	public String PreferenceDlg_Title_Tab_Encoding = "File Encoding";
	public String PreferenceDlg_Title_Tab_GraphViz = "GraphViz";
	public String PreferenceDlg_Title_Tab_Debug = "Debug";
	//--- Java info
	public String PreferenceDlg_Java_Home = "Java Home -";
	public String PreferenceDlg_Java_Version = "Version -";
	public String PreferenceDlg_Java_Command = "Command - ";
	//--- Font
	public String PreferenceDlg_Font_Target = "Target:";
	public String PreferenceDlg_Font_Family = "Name:";
	public String PreferenceDlg_Font_Size   = "Size:";
	public String PreferenceDlg_Font_Sample = "Sample";
	public String PreferenceDlg_Font_Target_Console		= "Console font";
	public String PreferenceDlg_Font_Target_CSV_Viewer	= "CSV Viewer font";
	//--- Text encoding
	public String PreferenceDlg_Encoding_AADLSource = "AADL Source file";
	public String PreferenceDlg_Encoding_AADLMacro  = "AADL Macro file";
	public String PreferenceDlg_Encoding_AADLcsvFile = "AADL csvFile() default";
	public String PreferenceDlg_Encoding_AADLtxtFile = "AADL txtFile() default";
	//--- GraphViz
	public String PreferenceDlg_GraphViz_DotFile = "GraphViz(dot):";
	public String PreferenceDlg_GraphViz_ChooseDot = "Select GraphViz(dot) application";

	//==============================
	// Library versions dialog resources
	//==============================
	
	public String LibVersionDlg_Button = "About libraries...";
	
	public String LibVersionDlg_Title_Main = "Library versions";
	public String LibVersionDlg_Title_CopyToClip = "Copy to clipboard";
	public String LibVersionDlg_Title_Info_Path = "Location";
	public String LibVersionDlg_Title_Info_LastMod = "Last modified";
	public String LibVersionDlg_Title_Info_JarTitle = "Title";
	public String LibVersionDlg_Title_Info_JarVersion = "Version";

	//==============================
	// User folder chooser dialog
	//==============================
	
	public String UserFolderChooserDlg_TitleForMExecDef	= "[Filter] Choose a User Folder";
	public String UserFolderChooserDlg_TitleForDataFile	= "[Data File] Choose a User Folder";

	//==============================
	// Common ProcessMonitor setting resources
	//==============================
	
	public String ProcMonitorSetting_AutoCloseAfterExec	= "Close console after execution";
	//public String ProcMonitorSetting_AutoCloseAfterExec	= "実行終了後コンソールを閉じる";
	public String ProcMonitorSetting_ShowConsoleAtStart	= "Show console at start";
	//public String ProcMonitorSetting_ShowConsoleAtStart	= "開始時にコンソールを表示";

	//==============================
	// ProcessMonitor Dialog resources
	//==============================
	
	public String ProcMonitorDlg_Title_Main = "Execution of [%s]";
	public String ProcMonitorDlg_Button_ShowArgs		= "Show arguments";
	public String ProcMonitorDlg_Button_Stop			= "Stop";
	public String ProcMonitorDlg_Button_StopAll			= "Stop all";
	public String ProcMonitorDlg_Button_StopSelected	= "Stop selected";
	
	public String ProcMonitorDlg_State_Running			= "Running";
	//public String ProcMonitorDlg_State_Running			= "実行中";
	public String ProcMonitorDlg_State_Terminating		= "Terminating";
	//public String ProcMonitorDlg_State_Terminating		= "停止中";
	public String ProcMonitorDlg_State_Finished		= "Finished";
	//public String ProcMonitorDlg_State_Finished		= "終了";
	public String ProcMonitorDlg_Button_Kill			= "Kill";
	//public String ProcMonitorDlg_Button_Kill			= "強制終了";
	public String ProcMonitorDlg_Button_CopyToClip		= "Copy to Clipboard";
	//public String ProcMonitorDlg_Button_CopyToClip		= "クリップボードへコピー";
	public String ProcMonitorDlg_Button_ShowConsole	= "Show console window";
	//public String ProcMonitorDlg_Button_ShowConsole	= "コンソールを表示";

	//==============================
	// MExecDefArgTablePane resources
	//==============================
	
	public String MExecDefArgTable_EditMenuFileChoose		= "Choose a file...";
	public String MExecDefArgTable_EditMenuFileVarFile		= "Input when running (File)";
	public String MExecDefArgTable_EditMenuFileVarDirectory	= "Input when running (Folder)";
	public String MExecDefArgTable_EditMenuFileVarCsv		= "Input when running (CSV)";
	public String MExecDefArgTable_EditMenuFileVarText		= "Input when running (TXT)";
	public String MExecDefArgTable_EditMenuFileVarXml		= "Input when running (XML)";
	public String MExecDefArgTable_EditMenuFileVarTemporary	= "Temporary file";
	public String MExecDefArgTable_EditMenuStringDirect		= "Edit characters";
	public String MExecDefArgTable_EditMenuStringVar		= "Input when running (STR)";
	public String MExecDefArgTable_EditMenuMqttAddrDirect	= "Edit characters";
	public String MExecDefArgTable_EditMenuMqttPubAddrVar	= "Input when running (PUB)";
	public String MExecDefArgTable_EditMenuMqttSubAddrVar	= "Input when running (SUB)";
	
	public String MExecDefArgTable_DispFileVarTemporary		= "@Temporary file";
	public String MExecDefArgTable_DispFileVarFile			= "@Input when running (File)";
	public String MExecDefArgTable_DispFileVarDirectory		= "@Input when running (Folder)";
	public String MExecDefArgTable_DispFileVarCsv			= "@Input when running (CSV)";
	public String MExecDefArgTable_DispFileVarXml			= "@Input when running (XML)";
	public String MExecDefArgTable_DispFileVarText			= "@Input when running (TXT)";
	public String MExecDefArgTable_DispStringVar			= "@Input when running (STR)";
	public String MExecDefArgTable_DispMqttPubAddrVar		= "@Input when running (PUB)";
	public String MExecDefArgTable_DispMqttSubAddrVar		= "@Input when running (SUB)";

	//==============================
	// MExecDefEditDialog resources
	//==============================
	
	public String MExecDefEditDlg_Title_NEW		= "New Filter";
	public String MExecDefEditDlg_Title_MODIFY	= "Edit Filter";
	public String MExecDefEditDlg_Title_VIEW	= "Filter (Read Only)";
	public String MExecDefEditDlg_Title_ModuleChooser	= "Choose a AADL Module";
	public String MExecDefEditDlg_SetupWorkTask_title	= "Prepares to Edit";
	public String MExecDefEditDlg_RollbackTask_title	= "Cancel editing";
	public String MExecDefEditDlg_Title_SaveMExecDef	= "Save Filter";
	public String MExecDefEditDlg_Tooltip_ChooseLocation	= "Choose a location";
	public String MExecDefEditDlg_Tooltip_ChooseModule		= "Choose a Module for Execution";
	public String MExecDefEditDlg_Button_ReadModuleInfo	= "Import from Information of Module";
	public String MExecDefEditDlg_CheckBox_ClearArgsHistory	= "Clear history of arguments for when this module was executed.";
	public String MExecDefEditDlg_Label_Name			= "Name";
	public String MExecDefEditDlg_Label_Location		= "Location";
	public String MExecDefEditDlg_Label_Module			= "Module for Execution";
	public String MExecDefEditDlg_Label_Args			= "Arguments";
	public String MExecDefEditDlg_Label_Desc			= "Description";
	public String MExecDefEditDlg_Arg_UseViewingFile	= "Use a viewing file";
	public String MExecDefEditDlg_Arg_ShowResultCsv		= "Show result (CSV)";
	public String MExecDefEditDlg_Arg_OutToTemp			= "Output to Temporary";
	public String MExecDefEditDlg_Tooltip_OutputGraph	= "Output the graph for AADL macro";
	public String MExecDefEditDlg_Graph_ProgressTitle	= "Output the graph for AADL macro";
	public String MExecDefEditDlg_Graph_ProgreesDesc_GenGraph		= "Generating graph by AADL macro...";
	public String MExecDefEditDlg_Graph_ProgressDesc_OutputImage	= "Writing graph image...";
	public String MExecDefEditDlg_Graph_ProgressDesc_ReadImage		= "Reading graph image...";

	//==============================
	// MacroFilterEditDialog resources
	//==============================
	
	public String MacroFilterEditDlg_Title_NEW		= "New Filter Macro";
	public String MacroFilterEditDlg_Title_MODIFY	= "Edit Filter Macro";
	public String MacroFilterEditDlg_Title_VIEW	= "Filter Macro (Read Only)";
	public String MacroFilterEditDlg_TabTitle_Definition	= "Definition";
	public String MacroFilterEditDlg_TabTitle_SubFilters	= "Sub-Filters";
	public String MacroFilterEditDlg_Label_MacroFilter		= "Filter Macro";

	//==============================
	// AADLMacroGraphDialog resources
	//==============================
	
	public String AADLMacroGraphDlg_Title				= "The graph for AADL macro";
	public String AADLMacroGraphDlg_Title_ImageChooser	= "Save Graph image";
	public String AADLMacroGraphDlg_Title_DotChooser	= "Save dot file";
	public String AADLMacroGraphDlg_tooltip_SaveImage	= "Save image...";
	public String AADLMacroGraphDlg_tooltip_ExportDot	= "Export dot file...";

	//==============================
	// MExecArgsEditDialog resources
	//==============================
	
	public String MExecArgsEditDlg_Title = "Edit arguments for [%s]";
	public String MExecArgsEditDlg_Label_History			= "History:";
	public String MExecArgsEditDlg_Tooltip_HistoryRecent	= "More recent history";
	public String MExecArgsEditDlg_Tooltip_HistoryOlder		= "Older history";
	public String MEXecArgsEditDlg_Tooltip_HistoryAllClear	= "Clear all history";
	
	//==============================
	// MExecArgsViewDialog resources
	//==============================
	
	public String MExecArgsViewDlg_Title = "Argument for [%s]";
	
	//==============================
	// MExecDefImportDialog resources
	//==============================
	
	public String MExecDefImportDlg_Title						= "Import Filter";
	public String MExecDefImportDlg_Label_ImportFileArea		= "Import File";
	public String MExecDefImportDlg_Label_ImportTargetFile		= "File";
	public String MExecDefImportDlg_Label_ImportTargetMExecDef	= "Filter";
	public String MExecDefImportDlg_Tooltip_ChooseImportFile	= "Choose a import file";
	public String MExecDefImport_ProgressTitle					= "Import the Filter";
	public String MExecDefImport_ProgressDesc					= "Importing...";
	
	//==============================
	// MExecDefExportDialog resources
	//==============================
	
	public String MExecDefExportDlg_Title						= "Export Filter";
	public String MExecDefExportDlg_Label_ExportFileArea		= "Export File";
	public String MExecDefExportDlg_Label_ExportTargetFile		= "File";
	public String MExecDefExportDlg_Label_ExportTree			= "Choose a Filter to export";
	public String MExecDefExportDlg_Tooltip_ChooseExportFile	= "Choose a export file";
	public String MExecDefExport_ProgressTitle					= "Export the Filter";
	public String MExecDefExport_ProgressDesc					= "Exporting...";
	
	//==============================
	// MExecHistoryToolView resources
	//==============================
	
	public String MExecHistory_Title_Execution		= "Run History";
	//public String MExecHistory_Title_Execution	= "履歴の実行";
	public String MExecHistory_Title_CreateFilter	= "Create filter by history";
	//public String MExecHistory_Title_CreateFilter	= "履歴からフィルタ作成";
	public String MExecHistoryTableColumnResult		= "Result";
	//public String MExecHistoryTableColumnResult	= "実行結果";
	public String MExecHistoryTableColumnName		= "Filter Name";
	//public String MExecHistoryTableColumnName		= "フィルタ名";
	public String MExecHistoryTableColumnStart		= "Start";
	//public String MExecHistoryTableColumnStart	= "開始日時";
	public String MExecHistoryTableColumnTime		= "Time";
	//public String MExecHistoryTableColumnTime		= "実行時間";
	
	//==============================
	// MExecRunningToolView resources
	//==============================
	
	public String MExecRunningTableColumnStatus	= "Status";
	//public String MExecRunningTableColumnStatus	= "状態";
	public String MExecRunningTableColumnControl	= "Operation";
	//public String MExecRunningTableColumnControl	= "操作";
	public String MExecRunningTableColumnName		= "Filter Name";
	//public String MExecRunningTableColumnName		= "フィルタ名";
	public String MExecRunningTableColumnStart		= "Start";
	//public String MExecRunningTableColumnStart	= "開始日時";
	public String MExecRunningTableColumnTime		= "Time";
	//public String MExecRunningTableColumnTime		= "実行時間";
	
	//==============================
	// MacroFilterDefArgReferDialog/MacroSubFilterArgReferDialog resources
	//==============================

	public String ArgReferDlg_Label_args				= "Available arguments";
	//public String ArgReferDlg_Label_args				= "参照可能引数";
	public String ArgReferDlg_Label_prefix				= "Prefix";
	//public String ArgReferDlg_Label_prefix				= "前に付加する文字";
	public String ArgReferDlg_Label_suffix				= "Suffix";
	//public String ArgReferDlg_Label_suffix				= "後に付加する文字";
	public String MacroFilterDefArgReferDlg_Title		= "Refer to the filter definition argument";
	//public String MacroFilterDefArgReferDlg_Title		= "フィルタ定義引数の参照";
	public String MacroSubFilterArgReferDlg_Title		= "Refer to the argument of sub-filter";
	//public String MacroSubFilterArgReferDlg_Title		= "サブフィルタ引数の参照";
	public String MacroSubFilterArgReferDlg_Label_Combo	= "Filter";
	//public String MacroSubFilterArgReferDlg_Label_Combo	= "フィルタ";

	//==============================
	// MacroSubFiltersTable resources
	//==============================
	
	public String MacroSubFiltersTableColumnNumber	= "No.";
	//public String MacroSubFiltersTableColumnNumber	= "No.";
	public String MacroSubFiltersTableColumnWait	= "Wait Target";
	//public String MacroSubFiltersTableColumnWait	= "待機対象";
	public String MacroSubFiltersTableColumnName	= "Filter Name";
	//public String MacroSubFiltersTableColumnName	= "フィルタ名";
	public String MacroSubFiltersTableColumnComment	= "Comment";
	//public String MacroSubFiltersTableColumnComment	= "コメント";
	public String MacroSubFiltersTable_Button_EditWait	= "Edit wait targets";
	//public String MacroSubFiltersTable_Button_EditWait	= "待機対象の編集";
	public String MacroSubFiltersTable_EditMenu_WaitChoose		= "Choose wait targets...";
	//public String MacroSubFiltersTable_EditMenu_WaitChoose	= "待機対象の選択...";
	public String MacroSubFiltersTable_EditMenu_WaitPrevious	= "Wait all of above";
	//public String MacroSubFiltersTable_EditMenu_WaitPrevious	= "前のすべてを待機";
	public String MacroSubFiltersTable_EditMenu_NoWait			= "No wait";
	//public String MacroSubFiltersTable_EditMenu_NoWait			= "待機しない";
	public String MacroSubFiltersTable_Value_WaitPrevious	= "All of above";
	//public String MacroSubFiltersTable_Value_WaitPrevious	= "前のすべて";
	public String MacroSubFiltersTable_Value_NoWait		= "No wait";
	//public String MacroSubFiltersTable_Value_NoWait		= "なし";
	
	//==============================
	// MacroSubFilterArgValueTablePane resources
	//==============================
	
	public String MacroSubFilterArgValueTable_EditMenuOutToTemp			= "Output temporary";
	//public String MacroSubFilterArgValueTable_EditMenuOutToTemp			= "テンポラリ出力";
	public String MacroSubFilterArgValueTable_EditMenuOutArgChoose		= "Refer [OUT] argument";
	//public String MacroSubFilterArgValueTable_EditMenuOutArgChoose		= "[OUT]引数の参照";
	public String MacroSubFilterArgValueTable_EditMenuPubArgChoose		= "Refer [PUB] argument";
	//public String MacroSubFilterArgValueTable_EditMenuPubArgChoose		= "[PUB]引数の参照";
	public String MacroSubFilterArgValueTable_EditMenuFilterArgRefer	= "Refer definition argument";
	//public String MacroSubFilterArgValueTable_EditMenuFilterArgRefer	= "フィルタ定義引数の参照";
	public String MacroSubFilterArgValueTable_EditMenuCreateReferArg	= "Add to definition argument";
	//public String MacroSubFilterArgValueTable_EditMenuCreateReferArg	= "フィルタ定義引数に追加";

	//==============================
	// MacroSubFilterChooser resources
	//==============================

	public String MacroSubFilterChooser_Title_WaitFilters		= "Choose wait targets";
	//public String MacroSubFilterChooser_Title_WaitFilters		= "待機対象の選択";
	public String MacroSubFilterChooser_Label_CandidateFilters	= "Candidate filters";
	//public String MacroSubFilterChooser_Label_CandidateFilters	= "フィルタ候補";
	public String MacroSubFilterChooser_Label_SelectedFilters		= "Selected filters";
	//public String MacroSubFilterChooser_Label_SelectedFilters		= "選択したフィルタ";
	
	//==============================
	// Chart Dialogs resources
	//==============================
	
	// Chart styles
	public String ChartStyle_Scatter			= "Scatter Chart";
	public String ChartStyle_Line				= "Line Chart";
	
	// Plot Policy
	public String ChartInvalidValuePolicy_Skip		= "Skip record contains invalid value";
	public String ChartInvalidValuePolicy_AsZero	= "Invalid value as Zero";
	public String ChartInvalidValuePolity_Connected	= "Connect data point with line";
	
	// Chart infos
	public String ChartFieldName_RecordNumber	= "Record#";
	public String ChartLabel_Title				= "Title";
	public String ChartLabel_DefaultLegend		= "Default legend"; 
	public String ChartLabel_Legend				= "Legend";
	public String ChartLabel_Xaxis				= "Horizontal axis";
	public String ChartLabel_Yaxis				= "Vertical axis";
	public String ChartLabel_NameOfXaxis		= "Horizontal axis title";
	public String ChartLabel_NameOfYaxis		= "Vertical axis title";
	public String ChartLabel_LabelOfXaxis		= "Horizontal axis label";
	public String ChartLabel_LabelOfYaxis		= "Vertical axis label";
	public String ChartLabel_RecordNumber		= "Record number";
	public String ChartLabel_Field				= "Data field";
	public String ChartLabel_Series				= "Data series";
	public String ChartLabel_TotalRecordCount	= "Total number of records";
	public String ChartLabel_HeaderRecordCount	= "Number of header records";
	public String ChartLabel_DataRecordCount	= "Number of data records";
	public String ChartLabel_FirstDataRecord	= "First record of data range";
	public String ChartLabel_LastDataRecord		= "Last record of data range";
	public String ChartLabel_DataType_decimal	= "Decimal";
	public String ChartLabel_DataType_datetime	= "Date time";
	public String ChartLabel_DataType_text		= "Record (Text)";
	
	// [ChartConfigDialog]
	public String ChartConfigDlg_title			= "Chart settings for CSV file";
	public String ChartConfigDlg_target_filename		= "File";
	public String ChartConfigDlg_seriescolumn_legend	= "Legend";
	public String ChartConfigDlg_seriescolumn_Xaxis		= "Horizontal axis";
	public String ChartConfigDlg_seriescolumn_Yaxis		= "Vertical axis";
	public String ChartConfigDlg_func_allpairs_repeated	= "All pairs of selected fields (Repeated)";
	public String ChartConfigDlg_func_allpairs_norepeat = "All pairs of selected fields (No repetition)";
	public String ChartConfigDlg_func_horzfieldsselect	= "Select data fields for horizontal axis";
	public String ChartConfigDlg_func_vertfieldsselect	= "Select data fields for vertical axis";
	public String ChartConfigDlg_func_horzfieldsreplace = "Replace data fields on Horizontal axis";
	public String ChartConfigDlg_func_vertfieldsreplace = "Replace data fields on Vertical axis";
	public String ChartConfigDlg_func_deleteallseries   = "Delete all data series";
	public String ChartConfigDlg_func_title				= "Batch function";
	public String ChartConfigDlg_func_exec				= "Generate";
	public String ChartConfigDlg_ChartType				= "Chart type";
	public String ChartConfigDlg_InvalidValuePolicy		= "Blank or Invalid value";
	public String ChartConfigDlg_DateTimeFormat			= "Date time format";
	public String ChartConfigDlg_CustomDateTimeFormat	= "Pattern";
	public String ChartConfigDlg_HorzDataType			= "Horizontal axis data type";
	public String ChartConfigDlg_VertDataType			= "Vertical axis data type";
	public String ChartConfigDlg_EditDataRecordRange_title	= "Edit data reocrd range";
	
	
	// [ChartSeriesEditDialog]
	public String ChartSeriesEditDlg_title_new		= "New data series";
	public String ChartSeriesEditDlg_title_edit		= "Edit data series";
	public String ChartSeriesEditDlg_CustomLegend	= "Any";
	
	// [ChartFieldSelectDialog]
//	public String ChartFieldSelectDlg_title_single   = "Select a data field (Single selection)";	// TODO: 日本語化
//	public String ChartFieldSelectDlg_title_multiple = "Select data fields (Multiple selection)";	// TODO: 日本語化
	
	// [ChartDataRangeEditDialog]
	public String ChartDataRangeEditDlg_title = "Data record range setting";
	
	// [ChartViewDialog]
	public String ChartViewDlg_tooltip_SaveImage		= "Save image...";
	public String ChartViewDlg_tooltip_Refresh			= "Repaint";
	public String ChartViewDlg_tooltip_Config			= "Configuration...";
	public String ChartViewDlg_tooltip_CsvView			= "Show target CSV file";
	public String ChartViewDlg_Title_ImageChooser		= "Save Chart image";
	
	//==============================
	// Generic Filter Editor
	// @since 3.2.0
	//==============================

	// [GenericFilterEditDialog]
	public String GenericFilterEditDlg_Title_NEW	= "New Generic Filter";
	//public String GenericFilterEditDlg_Title_NEW	= "汎用フィルタの新規作成";
	public String GenericFilterEditDlg_Title_MODIFY	= "Edit Generic Filter";
	//public String GenericFilterEditDlg_Title_MODIFY	= "汎用フィルタの編集";
	public String GenericFilterEditDlg_Title_VIEW	= "Generic Filter (Read Only)";
	//public String GenericFilterEditDlg_Title_VIEW	= "汎用フィルタ (読み込み専用)";
	public String GenericFilterEditDlg_TabTitle_Definition	= "Definition";
	//public String GenericFilterEditDlg_TabTitle_Definition	= "定義";
	public String GenericFilterEditDlg_TabTitle_Schema		= "Schema";
	//public String GenericFilterEditDlg_TabTitle_Schema		= "スキーマ";
	public String GenericFilterEditDlg_Label_GenericFilter	= "Generic Filter";
	//public String GenericFilterEditDlg_Label_GenericFilter	= "汎用フィルタ";
	public String GenericFilterEditDlg_name_DateTimeFormat	= "DateTime Format";
	//public String GenericFilterEditDlg_name_DateTimeFormat	= "日付/時刻フォーマット";
	public String GenericFilterEditDlg_name_InputSchema		= "Input Table";
	//public String GenericFilterEditDlg_name_InputSchema		= "入力テーブル";
	public String GenericFilterEditDlg_name_OutputSchema	= "Output Table";
	//public String GenericFilterEditDlg_name_OutputSchema	= "出力テーブル";
	public String GenericFilterEditDlg_name_ArithExp		= "Expression";
	//public String GenericFilterEditDlg_name_ArithExp		= "計算式";
	public String GenericFilterEditDlg_name_JoinCond		= "Join";
	//public String GenericFilterEditDlg_name_JoinCond		= "結合条件";
	public String GenericFilterEditDlg_name_WhereCond		= "Where";
	//public String GenericFilterEditDlg_name_WhereCond		= "抽出条件";
	public String GenericFilterEditDlg_name_FilterArg		= "Filter Definition Argument";
	//public String GenericFilterEditDlg_name_FilterArg		= "フィルタ定義引数";
	public String GenericFilterEditDlg_name_Literal			= "Immediate";
	//public String GenericFilterEditDlg_name_Literal			= "値";
	public String GenericFilterEditDlg_name_Name			= "Name";
	//public String GenericFilterEditDlg_name_Name			= "名前";
	public String GenericFilterEditDlg_name_Desc			= "Description";
	//public String GenericFilterEditDlg_name_Desc			= "説明";
	public String GenericFilterEditDlg_name_ValueType		= "Data type";
	//public String GenericFilterEditDlg_name_ValueType		= "データ型";
	public String GenericFilterEditDlg_name_Header			= "Header";
	//public String GenericFilterEditDlg_name_Header			= "ヘッダー";
	public String GenericFilterEditDlg_name_Operand			= "Value";
	//public String GenericFilterEditDlg_name_Operand			= "値";
	public String GenericFilterEditDlg_name_Operand1		= "(Value 1)";
	//public String GenericFilterEditDlg_name_Operand1		= "(値1)";
	public String GenericFilterEditDlg_name_Operand2		= "(Value 2)";
	//public String GenericFilterEditDlg_name_Operand2		= "(値2)";
	public String GenericFilterEditDlg_btn_EditDateTimeFormat	= "Edit DateTime Format";
	//public String GenericFilterEditDlg_btn_EditDateTimeFormat	= "日付/時刻フォーマットの編集";
	public String GenericFilterEditDlg_btn_AddToOutput		= "Add to Output Table";
	//public String GenericFilterEditDlg_btn_AddToOutput		= "出力テーブルへ追加";
	public String GenericFilterEditDlg_btn_AddInputTable	= "Add New Input Table";
	//public String GenericFilterEditDlg_btn_AddInputTable	= "入力テーブルを追加";
	public String GenericFilterEditDlg_btn_EditInputTable	= "Edit Input Table";
	//public String GenericFilterEditDlg_btn_EditInputTable	= "入力テーブルを編集";
	public String GenericFilterEditDlg_btn_DelInputTable	= "Delete Input Table";
	//public String GenericFilterEditDlg_btn_DelInputTable	= "入力テーブルを削除";
	public String GenericFilterEditDlg_btn_AddOutputTable	= "Add New Output Table";
	//public String GenericFilterEditDlg_btn_AddOutputTable	= "出力テーブルを追加";
	public String GenericFilterEditDlg_btn_EditOutputTable	= "Edit Output Table";
	//public String GenericFilterEditDlg_btn_EditOutputTable	= "出力テーブルを編集";
	public String GenericFilterEditDlg_btn_DelOutputTable	= "Delete Output Table";
	//public String GenericFilterEditDlg_btn_DelOutputTable	= "出力テーブルを削除";
	public String GenericFilterEditDlg_btn_DelOutputField	= "Delete Output Field";
	//public String GenericFilterEditDlg_btn_DelOutputField	= "出力列を削除";
	public String GenericFilterEditDlg_btn_MoveUpField		= "Move Up Output Field";
	//public String GenericFilterEditDlg_btn_MoveUpField		= "出力列を上へ移動";
	public String GenericFilterEditDlg_btn_MoveDownField	= "Move Down Output Field";
	//public String GenericFilterEditDlg_btn_MoveDownField	= "出力列を下へ移動";
	public String GenericFilterEditDlg_btn_AddOperandFilterArg	= "Add New Filter Argument";
	//public String GenericFilterEditDlg_btn_AddOperandFilterArg	= "フィルタ定義引数の追加";
	// [SchemaDateTimeFormatEditDlg]
	public String SchemaDateTimeFormatEditDlg_title				= "Edit DateTime Format";
	//public String SchemaDateTimeFormatEditDlg_title				= "日付/時刻フォーマットの編集";
	public String SchemaDateTimeFormatEditDlg_rdo_useFilterArg	= "Specify at the Filter Argument.";
	//public String SchemaDateTimeFormatEditDlg_rdo_useFilterArg	= "フィルタ定義引数で指定する";
	public String SchemaDateTimeFormatEditDlg_rdo_default		= "Use default format.";
	//public String SchemaDateTimeFormatEditDlg_rdo_default		= "標準のフォーマットを使用する";
	public String SchemaDateTimeFormatEditDlg_rdo_custom		= "Define DateTime format.";
	//public String SchemaDateTimeFormatEditDlg_rdo_custom		= "フォーマットを定義する";
	// [GenericFilterDefArgEditDialog]
	public String GenericFilterDefArgEditDlg_title_new		= "New String Argument Definition";
	//public String GenericFilterDefArgEditDlg_title_new		= "文字列引数定義の追加";
	public String GenericFilterDefArgEditDlg_title_edit		= "Edit String Argument Definition";
	//public String GenericFilterDefArgEditDlg_title_edit		= "文字列引数定義の編集";
	// [GenericInputSchemaEditDialog]
	public String GenericInputSchemaEditDlg_title_new	= "New Input Table";
	//public String GenericInputSchemaEditDlg_title_new	= "入力テーブル新規作成";
	public String GenericInputSchemaEditDlg_title_edit	= "Edit Input Table";
	//public String GenericInputSchemaEditDlg_title_edit	= "入力テーブル編集";
	public String GenericInputSchemaEditDlg_lbl_CsvSchema		= "CSV Table";
	//public String GenericInputSchemaEditDlg_lbl_CsvSchema		= "CSV テーブル";
	public String GenericInputSchemaEditDlg_lbl_SkipRecords	= "Skip record count";
	//public String GenericInputSchemaEditDlg_lbl_SkipRecords		= "処理対象外レコード数";
	public String GenericInputSchemaEditDlg_lbl_BaseCsv		= "Base CSV file";
	//public String GenericInputSchemaEditDlg_lbl_BaseCsv			= "基準 CSV ファイル";
	public String GenericInputSchemaEditDlg_lbl_BasePath		= "Path";
	//public String GenericInputSchemaEditDlg_lbl_BasePath		= "パス";
	public String GenericInputSchemaEditDlg_btn_OpenBaseCsv		= "Open Base CSV file...";
	//public String GenericInputSchemaEditDlg_btn_OpenBaseCsv		= "基準 CSV ファイルを開く...";
	public String GenericInputSchemaEditDlg_btn_ComposeByCsv	= "Componse input table from Base CSV";
	//public String GenericInputSchemaEditDlg_btn_ComposeByCsv	= "基準 CSV ファイルから入力テーブルを構成";
	public String GenericInputSchemaEditDlg_btn_AddField		= "Add";
	//public String GenericInputSchemaEditDlg_btn_AddField		= "追加";
	// [GenericOutputSchemaEditDialog]
	public String GenericOutputSchemaEditDlg_title_new	= "New Output Table";
	//public String GenericOutputSchemaEditDlg_title_new	= "出力テーブル新規作成";
	public String GenericOutputSchemaEditDlg_title_edit	= "Edit Output Table";
	//public String GenericOutputSchemaEditDlg_title_edit	= "出力テーブル編集";
	public String GenericOutputSchemaEditDlg_lbl_Table			= "Field configuration";
	//public String GenericOutputSchemaEditDlg_lbl_Table			= "列構成";
	public String GenericOutputSchemaEditDlg_lbl_TargetTitle	= "Output Target";
	//public String GenericOutputSchemaEditDlg_lbl_TargetTitle	= "出力対象";
	public String GenericOutputSchemaEditDlg_lbl_HeaderRecordTitle	= "Header record";
	//public String GenericOutputSchemaEditDlg_lbl_HeaderRecordTitle	= "ヘッダーレコード";
	public String GenericOutputSchemaEditDlg_lbl_HeaderRecordCount	= "Header record count";
	//public String GenericOutputSchemaEditDlg_lbl_HeaderRecordCount	= "ヘッダーレコード数";
	public String GenericOutputSchemaEditDlg_rdo_AutoHeaderRecords		= "Output Name of Output field as Header record";
	//public String GenericOutputSchemaEditDlg_rdo_AutoHeaderRecords		= "出力列の名前をヘッダーレコードとして出力";
	public String GenericOutputSchemaEditDlg_rdo_ManualHeaderRecords	= "Specify Header reocrd";
	//public String GenericOutputSchemaEditDlg_rdo_ManualHeaderRecords	= "ヘッダーレコードの内容を指定";
	// [GenericOperationTableModel]
	public String GenericOperationTableModel_name_Expression	= "Expression";
	//public String GenericOperationTableModel_name_Expression	= "式";
	public String GenericOperationTableModel_name_ResultType	= "Return data type";
	//public String GenericOperationTableModel_name_ResultType	= "戻り値型";
	public String GenericExpressionTableModel_name_Expression	= "Expression";
	//public String GenericExpressionTableModel_name_Expression	= "計算式";
	public String GenericExpressionProperty_title				= "Expression Property";
	//public String GenericExpressionProperty_title				= "計算式のプロパティ";
	public String GenericJoinCondTableModel_name_Expression		= "Join condition";
	//public String GenericJoinCondTableModel_name_Expression		= "結合条件";
	public String GenericJoinCondProperty_title					= "Join condition Property";
	//public String GenericJoinCondProperty_title					= "結合条件のプロパティ";

	//==============================
	// Excel 2 CSV
	// @since 3.3.0
	//==============================
	
	//public String Excel2csv_OpenFileChooser_title		= "Excel ファイルを開く";
	public String Excel2csv_OpenFileChooser_title		= "Open Excel file";
	//public String Excel2csv_InputPassword_desc			= "読み取り用パスワードを入力してください。";
	public String Excel2csv_InputPassword_desc			= "Input password to read.";
	//public String Excel2csv_OpenFileProgress_desc		= "変換定義を読み込み中...";
	public String Excel2csv_OpenFileProgress_desc		= "Reading conversion definitions...";
	//public String Excel2csv_DestEditDlg_title			= "[Excel to CSV] 出力先設定";
	public String Excel2csv_DestEditDlg_title			= "[Excel to CSV] Configure destinations";
	//public String Excel2csv_DestEditDlg_lbl_excelfile	= "Excel ファイル";
	public String Excel2csv_DestEditDlg_lbl_excelfile	= "Target Excel file";
	//public String Excel2csv_DestEditDlg_lbl_baseoutdir	= "基準出力フォルダ";
	public String Excel2csv_DestEditDlg_lbl_baseoutdir	= "Base destination folder";
	//public String Excel2csv_ExecProgress_predesc		= "CSV への変換準備中";
	public String Excel2csv_ExecProgress_predesc		= "Preparing conversion to CSV";
	//public String Excel2csv_ExecProgress_rundesc		= "CSV へ変換中";
	public String Excel2csv_ExecProgress_rundesc		= "Converting to CSV";
	
	//public String Excel2csv_confirmParseWarning			= "変換定義の記述に適切ではない値が含まれています。\n続行しますか？";
	public String Excel2csv_confirmParseWarning			= "Unsuitable values are specified in the conversion definition.\nContinue?";
	//public String Excel2csv_confirmChangeAllOutDir		= "以前の基準出力フォルダとは異なる出力先が設定されているものがあります。\nすべての出力先を変更する場合は [はい] を選択してください。\n以前の基準出力フォルダが出力先となっているもののみ変更する場合は [いいえ] を選択してください。";
	public String Excel2csv_confirmChangeAllOutDir		= "There are destination files that are different last base destination folder.\nIf you change all destination files of destination folder, choose [Yes].\nIf you change only destination files of destination folder same last base destination folder, choose [No].";
	//public String Excel2csv_confirmDestFilesMultipled	= "変換結果の出力先が重複しているものがあります。続行しますか？";
	public String Excel2csv_confirmDestFilesMultipled	= "There are duplicated destination files.\nContinue?";
	//public String Excel2csv_confirmDestFilesOverwrite	= "変換結果の出力先ファイルはすでに存在しています。\n上書きしますか？";
	public String Excel2csv_confirmDestFilesOverwrite	= "Destination files already exist.\nDo you want to overwrite?";
	
	//public String Excel2csv_msgWrongExcelPassword		= "パスワードが違います。";
	public String Excel2csv_msgWrongExcelPassword		= "Password was wrong.";
	//public String Excel2csv_msgCouldNotOpenExcel		= "Excel ファイルが開けません。";
	public String Excel2csv_msgCouldNotOpenExcel		= "Could not open Excel file.";
	//public String Excel2csv_msgOperationError			= "Excel ファイルの変換に失敗しました。";
	public String Excel2csv_msgOperationError			= "Failed to convert Excel file to CSV.";
	//public String Excel2csv_msgConfigParseError			= "Excel ファイルの変換定義の読み込みに失敗しました。";
	public String Excel2csv_msgConfigParseError			= "Failed to parse conversion definition in Excel file.";
	//public String Excel2csv_msgConfigSyntaxError		= "変換定義の記述が正しくありません。";
	public String Excel2csv_msgConfigSyntaxError		= "Conversion definitions are illegal.";
	//public String Excel2csv_msgConfigTooManyErrors		= "...さらに多くのエラーがあります。";
	public String Excel2csv_msgConfigTooManyErrors		= "...more errors.";
	//public String Excel2csv_msgConfigCommandsNothing	= "変換定義にコマンドが記述されていません。";
	public String Excel2csv_msgConfigCommandsNothing	= "There is no command in the conversion definition.";
	//public String Excel2csv_msgConfigDestFileIsNone		= "変換結果の出力先ファイルを指定してください。";
	public String Excel2csv_msgConfigDestFileIsNone		= "Please choose a destination file.";
	//public String Excel2csv_msgConfigDestFileIsDir		= "変換結果の出力先には、フォルダは指定できません。";
	public String Excel2csv_msgConfigDestFileIsDir		= "Cannot set the folder to the destination file.";
	//public String Excel2csv_msgFailedToRenameResults	= "変換結果の一時ファイルからの置き換えに失敗しました。";
	public String Excel2csv_msgFailedToRenameResults	= "Failed to replace temporary to destination file.";

	//==============================
	// Messages
	//==============================

	//
	// Confirm messages
	//
	public String confirmReplaceTitle = "Replace confirmation";
	//public String confirmReplaceTitle = "置換の確認";
	public String confirmDetailPartReplaceDestDirectory = "\"%s\" folder already exists.\n\nWould you like to replace the existing folder\n    Last modified: %s\n\n";
	//public String confirmDetailPartReplaceDestDirectory = "\"%s\" フォルダがすでに存在します。\n\nこのフォルダ\n    更新日時: %s\n\n";
	public String confirmDetailPartReplaceDestFile = "\"%s\" file already exists.\n\nWould you like to replace the existing file\n    %s bytes\n    Last modified: %s\n\n";
	//public String confirmDetailPartReplaceDestFile = "\"%s\" ファイルがすでに存在します。\n\nこのファイル\n    %s バイト\n    更新日時: %s\n\n";
	public String confirmDetailPartReplaceSrcDirectory = "by this folder?\n    Last modified: %s\n\n";
	//public String confirmDetailPartReplaceSrcDirectory = "を次のディレクトリで置き換えますか?\n    更新日時: %s\n\n";
	public String confirmDetailPartReplaceSrcFile = "by this file?\n    %s バイト\n    Last modified: %s\n\n";
	//public String confirmDetailPartReplaceSrcFile = "を次のファイルで置き換えますか?\n    %s バイト\n    更新日時: %s\n\n";
	
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
	public String confirmDetailReplaceMExecDef = "\"%s\" Filter already exists.\n\nWould you like to replace the exiting Filter\n    Last modified: %s\n\nwith this one?\n    Last modified: %s\n\n";
	//public String confirmDetailReplaceMExecDef="%s" フィルタがすでに存在します。\n\n現在のフィルタ\n    更新日時: %s\n\nを次の新しいフィルタで置き換えますか?\n    更新日時: %s\n\n

	public String confirmHistoryWarningForExec		= "Module file has been updated or module configuration has been modified.\nIn this case, the execution result is different or may fail to run.\nDo you want to continue?";
	//public String confirmHistoryWarningForExec	= "モジュール構成が変更されている、もしくはモジュールファイルが更新されています。\nこの場合、実行結果が異なるか、実行に失敗する可能性があります。\n続行しますか?";
	public String confirmHistoryWarningForCreate	= "Module file has been updated or module configuration has been modified.\nIn this case, there is a possibility that the value of the filter argument cannot be imported.\nDo you want to continue?";
	//public String confirmHistoryWarningForCreate	= "モジュール構成が変更されている、もしくはモジュールファイルが更新されています。\nこの場合、フィルタの引数値が取り込めない可能性があります。\n続行しますか?";

	public String confirmAddReferArgToDefinition = "There is no argument that can be referred.\nDo you want to add to the filter definition this argument?";
	//public String confirmAddReferArgToDefinition = "参照可能なフィルタ定義引数はありません。\nこの引数をフィルタ定義に追加しますか?";
	
	public String confirmReplaceSubFilterTitle = "Replace sub-filter";
	//public String confirmReplaceSubFilterTitle = "サブフィルタの変更";
	public String confirmReplaceReferedSubFilter = "Sub-filter to be replaced is referenced by another sub-filter.\nAll references to this filter will be deleted, if you replace this filter.\nDo you want to replace?";
	//public String confirmReplaceReferedSubFilter = "変更するサブフィルタは、他のサブフィルタから参照されています。\n変更すると、このフィルタへのすべての参照が削除されます。\n変更しますか?";
	public String confirmDeleteSubFilter = "Delete sub-filter";
	//public String confirmDeleteSubFilter = "サブフィルタの削除";
	public String confirmDeleteReferedSubFilter = "Sub-filters to be deleted are referencing by another sub-filters.\nDo you want to delete?";
	//public String confirmDeleteReferedSubFilter = "削除対象サブフィルタは、他のサブフィルタから参照されています。\n削除しますか?";
	
	public String confirmDeleteArgumentTitle = "Delete argument";
	//public String confirmDeleteArgumentTitle = "引数の削除";
	public String confirmDeleteReferedArgument = "This argument is referenced by the argument of the sub-filter.\nDo you want to delete?";
	//public String confirmDeleteReferedArgument = "この引数はサブフィルタの引数から参照されています。\n削除しますか?";
	
	public String confirmDeleteDataSeriesTitle	= "Delete data series";
	//public String confirmDeleteDataSeriesTitle	= "データ系列の削除";
	public String confirmDeleteAllDataSeries	= "Delete all data series. Are you sure?";
	//public String confirmDeleteAllDataSeries	= "すべてのデータ系列を削除します。よろしいですか?";
	public String confirmCloseChartByReopenFile	= "The specified file is being configured for chart or chart displayed.\nDo you want to continue to discard the chart?";
	//public String confirmCloseChartByReopenFile	= "指定されたファイルは、グラフ表示中もしくは設定中です。\nグラフを破棄して処理を続行しますか?";
	public String confirmFileChangedCloseChartAndReplace = "The file has been changed on the file system.\nDo you want to discard the chart and replace\nthe editor contents with these changes?";
	//public String confirmFileChangedCloseChartAndReplace = "ファイルはファイル・システム上で変更されています。\nグラフを破棄して、エディターの内容をこれらの変更で置き換えますか?";
	
	public String confirmKillAllFilters	= "Filter is running! Do you want to kill?";
	//public String confirmKillAllFilters	= "フィルタが実行中です。強制終了しますか？";
	public String confirmStopAllFilters = "Stop all the filter running. Are you sure?";
	//public String confirmStopAllFilters = "実行中のフィルタをすべて停止します。よろしいですか？";
	public String confirmKillTerminatingFilter = "Filter is stopping! Do you want to kill?";
	//public String confirmKillTerminatingFilter = "フィルタは停止処理中です。強制終了しますか？";
	public String confirmKillAllTerminatingFilters = "Kill all of the filter stop being processed. Are you sure?";
	//public String confirmKillAllTerminatingFilters = "停止処理中のフィルタをすべて強制終了します。よろしいですか？";
	public String confirmKillSelectedTerminatingFilters = "Kill all of the filter stop being processed in selected. Are you sure?";
	//public String confirmKillSelectedTerminatingFilters = "選択されている停止処理中のフィルタをすべて強制終了します。よろしいですか？";
	
	public String confirmNoWaitFilterArgReferWarning = "This filter is referencing the argument of the following filter.\n\n  %s\n\nThe referencing argument may not ready at run-time.\nBecause this filter is to be executed before the referencing filter.\nDo you want to continue?";
	//public String confirmNoWaitFilterArgReferWarning = "このフィルタは、次のフィルタの引数を参照しています。\n\n  %s\n\n待機なしフィルタは参照元のフィルタよりも前に実行されるため、\n実行時に引数が準備できていない場合があります。\n続行しますか?";
	
	//==============================
	// Generic Filter Editor
	// @since 3.2.0
	//==============================

	public String confirmClearCurrentInputCsvTableSchema 		= "Delete current fields and then reconstruct fields from CSV table.\nAre you sure";
	//public String confirmClearCurrentInputCsvTableSchema 		= "現在の列構成を破棄し、CSV テーブルから再構成します。\nよろしいですか？";
	public String confirmClearIncludedReferencedInputCsvField	= "Input table contains field that is referenced from others.\nDiscard current fields and then reconstruct fields from CSV table.\nAre you sure?";
	//public String confirmClearIncludedReferencedInputCsvField	= "入力テーブルには他の定義から参照されている列が含まれています。\n強制的に削除し、CSV テーブルから再構成します。\nよろしいですか？";
	public String confirmDeleteCurrentReferencedInputCsvField	= "Selected field is referenced from others.\nDo you want to delete?";
	//public String confirmDeleteCurrentReferencedInputCsvField	= "この列は他の定義から参照されています。\n削除しますか？";
	public String confirmDeleteIncludedReferencedInputCsvField	= "Input table contains field that is referenced from others.\nDo you want to delete?";
	//public String confirmDeleteIncludedReferencedInputCsvField	= "他の定義から参照されている列が含まれています。\n削除しますか？";
	public String confirmReferencedInputCsvTableSchemaChanged	= "Data type of field that is referenced from others was changed or it was deleted.\nDo you want to apply?";
	//public String confirmReferencedInputCsvTableSchemaChanged	= "他の定義から参照されている列のデータ型が変更、もしくは削除されています。\n変更を反映しますか？";
	public String confirmReferencedGenericFilterArgWillDelete	= "Selected filter argument is referenced from expression.\nDo you want to delete?";
	//public String confirmReferencedGenericFilterArgWillDelete	= "この引数は計算式で参照されています。削除すると参照も削除されます。\n強制的に削除しますか？";
	public String confirmReferencedGenericFilterArgWillChange	= "Selected filter argument is referenced from expression.\nIf you change data type, calculation may not satisfied.\nDo you want to change?";
	//public String confirmReferencedGenericFilterArgWillChange	= "この引数は計算式で参照されています。データ型を変更すると計算が成立しない場合があります。\nよろしいですか？";
	public String confirmDeleteSpecifiedInputTableSchemaNoRefer	= "Input table will be deleted.\nAre you sure?";
	//public String confirmDeleteSpecifiedInputTableSchemaNoRefer	= "入力テーブルを削除します。入力テーブルのすべての列も削除されます。\nよろしいですか？";
	public String confirmDeleteParentInputTableSchemaNoRefer		= "Input table that has selected field will be deleted.\nAre you sure?";
	//public String confirmDeleteParentInputTableSchemaNoRefer		= "この列が属する入力テーブルを削除します。入力テーブルのすべての列も削除されます。\nよろしいですか？";
	public String confirmDeleteSpecifiedInputTableSchemaWithRefer	= "Input table will be delete.\nIt contains field that is referenced others.\nAre you sure?";
	//public String confirmDeleteSpecifiedInputTableSchemaWithRefer	= "入力テーブルを削除します。入力テーブルのすべての列も削除されます。\n入力テーブルには他の定義から参照されている列が含まれています。\n強制的に削除しますか？";
	public String confirmDeleteParentInputTableSchemaWithRefer		= "Input table that has selected field will be deleted.\nIt contains field that is referenced others.\nAre you sure?";
	//public String confirmDeleteParentInputTableSchemaWithRefer		= "この列が属する入力テーブルを削除します。入力テーブルのすべての列も削除されます。\n入力テーブルには他の定義から参照されている列が含まれています。\n強制的に削除しますか？";
	public String confirmDeleteSpecifiedOutputTableSchema		= "Output table will be deleted.\nAre you sure?";
	//public String confirmDeleteSpecifiedOutputTableSchema		= "出力テーブルを削除します。出力テーブルのすべての列も削除されます。\nよろしいですか？";
	public String confirmDeleteParentOutputTableSchema			= "Output table that has selected field will be deleted.Are you sure?";
	//public String confirmDeleteParentOutputTableSchema			= "この列が属する出力テーブルを削除します。出力テーブルのすべての列も削除されます。\nよろしいですか？";
	public String confirmDeleteReferencedExpressionSchema		= "Selected expression that is referenced others will be deleted.\nAre you sure?";
	//public String confirmDeleteReferencedExpressionSchema		= "この計算式は他の定義から参照されています。\n強制的に削除しますか？";
	
	//
	// Error message
	//
	public String msgIllegalMExecArgsValue = "Illegal argument value for Module Execution.";
	//public String msgIllegalMExecArgsValue = "引数の値が正しく入力されていません。";
	public String msgUserFolderNoSelection = "Please select a User Folder.";
	//public String msgUserFolderNoSelection = "ユーザー領域を選択してください。";
	public String msgUserFolderNotFound = "User Folder not found.";
	//public String msgUserFolderNotFound = "選択されたユーザー領域が見つかりません。";
	public String msgUserFolderNotDirectory = "Selected path is not folder.";
	//public String msgUserFolderNotDirectory = "選択されたユーザー領域はフォルダではありません。";
	public String msgUserFolderCouldNotWrite = "Cannot write access to User Folder.";
	//public String msgUserFolderCouldNotWrite = "選択されたユーザー領域には書き込みできません。";
	public String msgUserFolderCouldNotAccess = "Cannot access to User Folder.";
	//public String msgUserFolderCouldNotAccess = "選択されたユーザー領域にアクセスできません。";
	public String msgUserFolderCouldNotUse = "Cannot use the specified folder for User Folder.";
	//public String msgUserFolderCouldNotUse = "指定されたフォルダは、ユーザー領域には設定できません。";
	public String msgUserFolderIncludesSystemRoot = "Cannot use the folder that system area.";
	//public String msgUserFolderIncludesSystemRoot = "システム領域を含むフォルダは設定できません。";
	public String msgSelectExistingFolder = "Please select an existing folder.";
	//public String msgSelectExistingFolder = "存在するフォルダを選択してください。";
	public String msgLongSelectExistingFolder = "\"%s\"\nfolder is not exists.\nPlease select an existing folder.";
	//public String msgLongSelectExistingFolder = "\"%s\"\nフォルダは存在しません。\n存在するフォルダを選択してください。";
	
	public String msgCouldNotPasteToThis		= "Cannot paste it here.";
	//public String msgCouldNotPasteToThis		= "この場所には貼り付けできません。";
	public String msgFailedToCreateTempFile		= "Cannot create the temporary file.";
	//public String msgFailedToCreateTempFile		= "テンポラリファイルが生成できません。";
	public String msgIllegalModuleArguments		= "Illegal any arguments.";
	//public String msgIllegalModuleArguments		= "引数の設定が正しくありません。";
	public String msgErrorRequiredString		= "Please input characters.";
	//public String msgErrorRequiredString		= "文字を入力してください。";
	public String msgErrorRequiredInputFile		= "Please choose a Input file.";
	//public String msgErrorRequiredInputFile		= "入力ファイルを指定してください。";
	public String msgErrorRequiredOutputFile	= "Please choose a Output file.";
	//public String msgErrorRequiredOutputFile	= "出力先ファイルを指定してください。";
	public String msgErrorInputFileNotFound		= "Input file does not exists.";
	//public String msgErrorInputFileNotFound		= "入力ファイルは存在しません。";
	public String msgErrorRequiredOver3chars	= "If you type a character, please enter at least three characters.";
	//public String msgErrorRequiredOver3chars	= "文字を入力する場合は、3文字以上入力してください。";
	public String msgErrorRequiredMqttPubAddr	= "Please input publish address.";
	//public String msgErrorRequiredMqttPubAddr	= "パブリッシュ宛先を入力してください。";
	public String msgErrorRequiredMqttSubAddr	= "Please input subscribe address.";
	//public String msgErrorRequiredMqttSubAddr	= "サブスクライブ宛先を入力してください。";
	public String msgErrorReferArgNotFound		= "Reference argument does not exist.";
	//public String msgErrorReferArgNotFound		= "参照する引数は存在しません。";
	public String msgErrorReferArgTypeIsNotSame	= "Please specify the reference argument of the same attribute.";
	//public String msgErrorReferArgTypeIsNotSame	= "参照する引数には、同じ引数属性のものを指定してください。";
	public String msgErrorLinkedOutArgNoExecuted	= "Please select [OUT] argument of the filter to be executed before this filter.";
	//public String msgErrorLinkedOutArgNoExecuted	= "このフィルタより前に実行されるフィルタの[OUT]引数を選択してください。";
	public String msgErrorLinkedPubArgNoExecuted	= "Please select [PUB] argument of the filter to be executed before this filter.";
	//public String msgErrorLinkedPubArgNoExecuted	= "このフィルタより前に実行されるフィルタの[PUB]引数を選択してください。";
	public String msgNotModifiableFile			= "The file can not be changed.\nPlease proceed to close the file, if you are opening the file.";
	//public String msgNotModifiableFile			= "ファイルが変更できません。\nファイルを開いている場合は閉じてから操作を続行してください。";
	public String msgErrorInputFileIsExternal	= "The input file Please specify the files that belong to this filter definition.";
	//public String msgErrorInputFileIsExternal	= "入力ファイルには、このフィルタ定義に属するファイルを指定してください。";
	public String msgErrorOutputFileIsExternal	= "The output file Please specify the files that belong to this filter definition.";
	//public String msgErrorOutputFileIsExternal	= "出力ファイルには、このフィルタ定義に属するファイルを指定してください。";
	public String msgErrorOutputFileIsDirectory	= "Cannot set the folder to the output file.";
	//public String msgErrorOutputFileIsDirectory	= "出力ファイルには、フォルダは指定できません。";
	
	public String msgMExecDefCouldNotOpen		= "Cannot open the file of the Filter.";
	//public String msgMExecDefCouldNotOpen=フィルタが開けません。
	public String msgMExecDefTargetNotFound		= "%s does not exists.";
	//public String msgMExecDefTargetNotFound		= "%sは存在しません。";
	public String msgMExecDefTargetNotJar		= "%s is not Jar file.";
	//public String msgMExecDefTargetNotJar		= "%sは Jar ファイルではありません。";
	public String msgMExecDefTargetNotAadlJar	= "%s is not AADL module.";
	//public String msgMExecDefTargetNotAadlJar	= "%sは AADL 実行モジュールではありません。";
	public String msgMExecDefTargetNotAadlMacro	= "%s is not AADL Macro file.";
	//public String msgMExecDefTargetNotAadlMacro	= "%sは AADL マクロ定義ファイルではありません。";
	public String msgMEXecDefTargetNotLocalMacro	= "Please select the AADL macro file that is stored in this Filter.";
	//public String msgMEXecDefTargetNotLocalMacro=このフィルタ内に配置された AADL マクロ定義ファイルを指定してください。
	public String msgMExecDefArgNothingAttr		= "The attribute of the argument is unknown.";
	//public String msgMExecDefArgNothingAttr		= "引数の属性が設定されていません。";
	public String msgMExecDefArgNothingValue	= "The value of the argument is not specified.";
	//public String msgMExecDefArgNothingValue	= "引数の値が指定されていません。";
	public String msgMExecDefArgCannotUseFixedOutput	= "Cannot use the fixed output location.";
	//public String msgMExecDefArgCannotUseFixedOutput	= "固定の出力先は指定できません。";
	public String msgMExecDefArgCannotUseString			= "Cannot use the character string.";
	//public String msgMExecDefArgCannotUseString			= "文字列は指定できません。";
	public String msgMExecDefArgCannotUseFile			= "Cannot use the file.";
	//public String msgMExecDefArgCannotUseFile			= "ファイルは指定できません。";
	public String msgMExecDefArgCannotUseDir			= "Cannot use the directory.";
	//public String msgMExecDefArgCannotUseDir			= "ディレクトリは指定できません。";
	public String msgMExecDefArgCannotUseTempFile		= "Cannot use the temporary file.";
	//public String msgMExecDefArgCannotUseTempFile		= "テンポラリファイルは指定できません。";
	public String msgMExecDefArgCannotUsePubParam		= "Cannot use the publish parameter.";
	//public String msgMExecDefArgCannotUsePubParam		= "パブリッシュパラメータは指定できません。";
	public String msgMExecDefArgCannotUseSubParam		= "Cannot use the subscribe parameter.";
	//public String msgMExecDefArgCannotUseSubParam		= "サブスクライブパラメータは指定できません。";
	public String msgMExecDefNotDirectory	= "The root of the Filter is not a directory.";
	//public String msgMExecDefNotDirectory=フィルタのルートがディレクトリではありません。
	public String msgMExecDefPrefsNotFile	= "The file of the Filter is not a file.";
	//public String msgMExecDefPrefsNotFile=フィルタ定義ファイルがファイルではありません。
	public String msgMExecDefNothingModule	= "Please choose a file for execution.";
	//public String msgMExecDefNothingModule	= "実行モジュールを指定してください。";
	public String msgMExecDefModuleNotExecutable	= "%s is not an executable file.";
	//public String msgMExecDefModuleNotExecutable	= "%s は実行可能なファイルではありません。";
	public String msgMExecDefModuleNotFound			= "The specified execution module not found.";
	//public String msgMExecDefModuleNotFound			= "指定された実行モジュールが見つかりません。";
	public String msgMExecDefOpenForReadOnly		= "Open as read-only the Filter.";
	//public String msgMExecDefOpenForReadOnly=フィルタを，読み込み専用として開きます。
	public String msgMExecDefFailedToSave			= "Failed to save the Filter.";
	//public String msgMExecDefFailedToSave=フィルタの保存に失敗しました。
	public String msgMExecDefFailedToBuildForSave	= "Failed to generate the filter stored information.";
	//public String msgMExecDefFailedToBuildForSave	= "フィルタの保存情報生成に失敗しました。";
	
	public String msgMExecDefExportNotFile	= "Please select a destination file.";
	//public String msgMExecDefExportNotFile	= "エクスポート先ファイルを選択してください。";
	public String msgMExecDefExportNoSource	= "Please select a Filter to export.";
	//public String msgMExecDefExportNoSource=エクスポートするフィルタを選択してください。
	public String msgMExecDefExportFailed	= "Failed to export the Filter.";
	//public String msgMExecDefExportFailed=フィルタのエクスポートに失敗しました。
	public String msgMExecDefImportNotFile	= "Please select a file to import.";
	//public String msgMExecDefImportNotFile	= "インポートするファイルを選択してください。";
	public String msgMExecDefImportIllegalSource	= "Cannot import from the specified file.";
	//public String msgMExecDefImportIllegalSource=指定されたファイルではフィルタをインポートできません。
	public String msgMExecDefImportNoTarget		= "Please select a destination folder.";
	//public String msgMExecDefImportNoTarget=フィルタを配置するフォルダを選択してください。
	public String msgMExecDefImportFailed	= "Failed to import the Filter.";
	//public String msgMExecDefImportFailed=フィルタのインポートに失敗しました。
	public String msgMExecDefImportChecksumError = "Invalid checksum of the Filter to import.";
	//public String msgMExecDefImportChecksumError=インポートするフィルタのチェックサムが不正です。
	public String msgMExecDefImportErrorFile = "The following file can not be imported.";
	//public String msgMExecDefImportErrorFile = "次のファイルはインポートできません。";
	
	public String msgMExecDefGraphVizDotNotSet		= "GraphViz(dot) application is not set.";
	//public String msgMExecDefGraphVizDotNotSet	= "GraphViz(dot) アプリケーションが設定されていません。";
	public String msgMExecDefGraphVizCannotAccess	= "GraphViz(dot) application does not exist or can not be accessed.";
	//public String msgMExecDefGraphVizCannotAccess	= "GraphViz(dot) アプリケーションが存在しないか、アクセスできません。";
	public String msgGraphVizFailedToExec			= "Failed to execute GraphViz.";
	//public String msgGraphVizFailedToExec			= "GraphViz の実行に失敗しました。";
	public String msgGraphVizFailedToOutputGraph	= "Failed to output the graph for AADL macro.";
	//public String msgGraphVizFailedToOutputGraph	= "AADLマクロのグラフ出力に失敗しました。";
	public String msgGraphVizFailedToCreateWorkFile	= "Failed to create work file.";
	//public String msgGraphVizFailedToCreateWorkFile	= "作業ファイルが作成できません。";
	public String msgGraphVizFailedToGenerateGraph	= "Failed to generate graph for AADL macro.";
	//public String msgGraphVizFailedToGenerateGraph	= "AADLマクロのグラフ生成に失敗しました。";
	public String msgGraphVizFailedToOutputImage	= "Failed to output graph image.";
	//public String msgGraphVizFailedToOutputImage	= "グラフイメージの出力に失敗しました。";
	public String msgGraphVizFailedToReadImage		= "Failed to read graph image.";
	//public String msgGraphVizFailedToReadImage		= "グラフイメージの読み込みに失敗しました。";

	public String msgFailedToExecuteByHistory		= "Could not run the module by the history.";
	//public String msgFailedToExecuteByHistory		= "この履歴による実行はできません。";
	public String msgFailedToCreateFilterByHistory	= "Could not create a filter by the history.";
	//public String msgFailedToCreateFilterByHistory	= "この履歴からフィルタは作成できません。";
	public String msgIllegalHistoryData			= "History's module does not exist or argument definition has been changed.";
	//public String msgIllegalHistoryData			= "履歴のモジュールが存在しないか、引数定義が変更されています。";
	public String msgHistoryErrorCauseModuleNotFound	= "Filter not found.";
	//public String msgHistoryErrorCauseModuleNotFound	= "フィルタが見つかりません。";
	public String msgHistoryErrorCauseArgDefChanged		= "Argument definition has been changed.";
	//public String msgHistoryErrorCauseArgDefChanged		= "引数定義が変更されています。";
	public String msgHistoryErrorCauseModuleFileNotFound	= "Module file not found.";
	//public String msgHistoryErrorCauseModuleFileNotFound	= "モジュールファイルが見つかりません。";
	public String msgHistoryErrorCauseUndefined			= "Error";
	//public String msgHistoryErrorCauseUndefined			= "エラー";
	public String msgHistoryWarnCauseModuleDefChanged	= "Module configuration has been changed.";
	//public String msgHistoryWarnCauseModuleDefChanged	= "モジュール構成が変更されています。";
	public String msgHistoryWarnCauseModuleFileOverwrited	= "Module file has been updated.";
	//public String msgHistoryWarnCauseModuleFileOverwrited	= "モジュールファイルが更新されています。";
	public String msgHistoryWarnCauseUndefined			= "Modified";
	//public String msgHistoryWarnCauseUndefined			= "変更";

	public String msgUneditableArgument				= "You can not change the value of this argument.";
	//public String msgUneditableArgument				= "この引数の値は変更できません。";
	public String msgChooseReferArgument			= "Please select the arguments to which it refers.";
	//public String msgChooseReferArgument			= "参照する引数を選択してください。";
	public String msgNoReferOutArgument				= "There is no [OUT] argument that can be referred.\nYou can choose an argument from the filter that be executed before this filter.\nIts argument is set the fixed value or the same as the run-time parameter type of this argument.";
	//public String msgNoReferOutArgument				= "参照可能な [OUT] 引数はありません。\n参照可能な引数は、このフィルタよりも前に実行されるフィルタであり、\n固定の引数値か、実行時指定の形式が同じ引数のみとなります。";
	public String msgNoReferPubArgument				= "There is no [PUB] argument that can be referred.\nYou can choose an argument from the filter that be executed before this filter.";
	//public String msgNoReferPubArgument				= "参照可能な [PUB] 引数はありません。\n参照可能な引数は、このフィルタよりも前に実行されるフィルタのみとなります。";

	public String msgMacroFilterNoSubfilter			= "Sub-filter is not registered.";
	//public String msgMacroFilterNoSubfilter			= "サブフィルタが登録されていません。";
	public String msgCouldNotChooseThisFilterAsSubFilter	= "You can not select the filter of currently editing.";
	//public String msgCouldNotChooseThisFilterAsSubFilter	= "サブフィルタに、現在編集中のフィルタは選択できません。";

	public String msgMacroFilterDefFailedToEdit				= "Failed to open the Filter Macro.";
	//public String msgMacroFilterDefFailedToEdit				= "フィルタマクロの定義が開けません。";
	public String msgMacroFilterDefInvalidData				= "Filter Macro is contained invalid data.\nPlease correct the Filter Macro.";
	//public String msgMacroFilterDefInvalidData				= "フィルタマクロ定義に不正なデータが含まれています。\nフィルタマクロ定義を修正してください。";
	public String msgMacroFilterDefNoArgType				= "The attribute of the Filter Macro argument does not specified.";
	//public String msgMacroFilterDefNoArgType				= "フィルタマクロ定義引数の引数属性が指定されていません。";
	public String msgMacroFilterDefInvalidArgType			= "Invalid the attribute of the Filter Macro argument.";
	//public String msgMacroFilterDefInvalidArgType			= "フィルタマクロ定義引数の引数属性が不正です。";
	public String msgMacroFilterDefInvalidArgFile			= "The value specified for the Filter Macro argument is not a File.";
	//public String msgMacroFilterDefInvalidArgFile			= "フィルタマクロ定義引数に指定された値はファイルではありません。";
	public String msgMacroFilterDefInvalidArgValue			= "Invalid the valud specified for the Filter Macro argument.";
	//public String msgMacroFilterDefInvalidArgValue			= "フィルタマクロ定義引数に指定された値が不正です。";
	public String msgMacroSubFilterNotFound					= "Sub-filter not found.";
	//public String msgMacroSubFilterNotFound					= "サブフィルタが見つかりません。";
	public String msgMacroSubFilterArgCountNotEquals		= "Sub-filter argument count is different from the sub-filter definition.";
	//public String msgMacroSubFilterArgCountNotEquals		= "サブフィルタの引数定義数と設定された引数の数が一致しません。";
	public String msgMacroSubFilterArgInvalidDefRefer		= "Invalid reference for filter macro argument.";
	//public String msgMacroSubFilterArgInvalidDefRefer		= "フィルタ定義引数の参照が正しくありません。";
	public String msgMacroSubFilterArgReferNotFound			= "Referenced sub-filter argument not found.";
	//public String msgMacroSubFilterArgReferNotFound			= "サブフィルタ引数の参照先が見つかりません。";
	public String msgMacroSubFilterArgInvalidFileValue		= "The value specified for the sub-filter argument is not a File.";
	//public String msgMacroSubFilterArgInvalidFileValue		= "サブフィルタ引数に指定された値はファイルではありません。";
	public String msgMacroSubFilterInvalidWaitNumber		= "Invalid wait target of sub-filter.";
	//public String msgMacroSubFilterInvalidWaitNumber		= "サブフィルタの待機指定が不正です。";
	public String msgMacroSubFilterOutOfBoundWaitNumber		= "Wait target of sub-filter not found.";
	//public String msgMacroSubFilterOutOfBoundWaitNumber		= "待機対象のサブフィルタが見つかりません。";
	
	public String msgMacroSubFilterArgStringIllegal			= "This description cannot accepted, because it is illegal in AADL macro.";
	//public String msgMacroSubFilterArgStringIllegal			= "この記述は AADL マクロにおいて不正な記述となるため、受け付けられません。";
	public String msgMacroSubFilterArgStringMultiRefer		= "Reference argument can not be specified more than once to the argument of sub-filter.";
	//public String msgMacroSubFilterArgStringMultiRefer		= "フィルタマクロのサブフィルタ引数に、複数の引数参照は指定できません。";
	public String msgMacroSubFilterArgStringNoReferArgNo	= "This description cannot accepted, because reference argument of the specified number does not exist in Filter macro.";
	//public String msgMacroSubFilterArgStringNoReferArgNo	= "指定された番号のフィルタ定義引数は存在しないため、受け付けられません。";
	public String msgMacroSubFilterArgStringMismatchArgType	= "This description cannot accepted, because attribute of reference argument does not match the attribute of this argument.";
	//public String msgMacroSubFilterArgStringMismatchArgType	= "指定された番号のフィルタ定義引数と引数属性が一致しないため、受け付けられません。";
	public String msgMacroSubFilterWiatTargetInvalidSelected	= "Please select wait filters to be executed before this filter.";
	//public String msgMacroSubFilterWiatTargetInvalidSelected	= "待機フィルタには、このフィルタより前に実行されるフィルタを選択してください。";
	public String msgMacroSubFilterWaitTargetIllegal		= "Filter that can not be selected as a wait filter is specified.";
	//public String msgMacroSubFilterWaitTargetIllegal		= "待機フィルタとして選択できないフィルタが指定されています。";
	public String msgMacroSubFilterWaitTargetChooseAfter	= "Filter to be executed after this filter can not be specified to wait filter.";
	//public String msgMacroSubFilterWaitTargetChooseAfter	= "このフィルタより後に実行されるフィルタは、待機フィルタに指定できません。";
	public String msgMacroSubFilterSelectedFilterCanceled	= "Following filters were excluded, because can not be selected.";
	//public String msgMacroSubFilterSelectedFilterCanceled	= "以下のフィルタは選択できないため、除外されました。";

	public String msgChartConfigNoData					= "There is no data can be plotted.";
	//public String msgChartConfigNoData					= "グラフを描画するデータが存在しません。";
	public String msgChartConfigNoSelectionChartStyle	= "Please select chart type.";
	//public String msgChartConfigNoSelectionChartStyle	= "グラフの種類を選択してください。";
	public String msgChartConfigScatterDataTypeNotDecimal	= "Please select decimal to data type of the scatter plot.";
	//public String msgChartConfigScatterDataTypeNotDecimal	= "散布図のデータ型には数値を指定してください。";
	public String msgChartConfigIllegalDateTimeFormat	= "Date time format is illegal.";
	//public String msgChartConfigIllegalDateTimeFormat	= "日付時刻書式が正しくありません。";
	public String msgChartConfigFirstDataRecordIllegal	= "Set the first record number of data record range greater than the number of header records.";
	//public String msgChartConfigFirstDataRecordIllegal	= "データレコード範囲の先頭には、ヘッダレコード数よりも大きい値を設定してください。";
	public String msgChartConfigLastDataRecordIllegal	= "Set the last record number of data record range greater than the first record number of data record range.";
	//public String msgChartConfigLastDataRecordIllegal		= "データレコード範囲の最後には、データレコード範囲の先頭より大きい値を設定してください。";
	public String msgChartConfigGenerateFuncNoSelected	= "Please select a batch function.";
	//public String msgChartConfigGenerateFuncNoSelected	= "一括設定の機能を選択してください。";
	public String msgChartConfigMultiDataFieldsNoSelected	= "Please select one or more data fields.";
	//public String msgChartConfigMultiDataFieldsNoSelected	= "データ列を一つ以上選択してください。";
	public String msgChartConfigDataFieldNoSelected			= "Please select a data field.";
	//public String msgChartConfigDataFieldNoSelected		= "データ列を選択してください。";
	public String msgChartConfigHorzDataFieldNoSelected		= "Please select a data field for Horizontal axis.";
	//public String msgChartConfigHorzDataFieldNoSelected	= "横軸のデータ列を選択してください。";
	public String msgChartConfigVertDataFieldNoSelected		= "Please select a data field for Vertical axis.";
	//public String msgChartConfigVertDataFieldNoSelected	= "縦軸のデータ系列を選択してください。";
	public String msgChartConfigGenPairsNumFieldsNotEnough	= "In order to create pairs, two or more fields is required.";
	//public String msgChartConfigGenPairsNumFieldsNotEnough	= "組合せ生成には、2 列以上必要です。";
	public String msgChartConfigNoDataSeries				= "Please set the data series.";
	//public String msgChartConfigNoDataSeries				= "データ系列を設定してください。";
	public String msgChartViewNoImage						= "Painting is not complete. Please repaint.";
	//public String msgChartViewNoImage						= "描画が完了していません。再描画してください。";
	
	public String msgFailedToCreateCommandLogFile		= "Cannot create a log file for filter runtime.";
	//public String msgFailedToCreateCommandLogFile		= "フィルタ実行時ログファイルが生成できません。";
	public String msgFailedToOpenCommandLogFile			= "Cannot open a log file for filter runtime.";
	//public String msgFailedToOpenCommandLogFile		= "フィルタ実行時ログファイルが開けません。";
	public String msgFailedToExecuteByMultiHistory		= "Could not run multiple modules by history.";
	//public String msgFailedToExecuteByMultiHistory	= "履歴から複数のモジュールを実行することはできません。";
	
	//==============================
	// Generic Filter Editor
	// @since 3.2.0
	//==============================

	public String msgGenericFilterEdit_FailedToReadSchemaFile		= "Description of schema definition file is incorrect.";
	//public String msgGenericFilterEdit_FailedToReadSchemaFile		= "スキーマ定義ファイルの記述が正しくありません。";
	public String msgGenericFilterEdit_NoSelectionValueType			= "Please select data type.";
	//public String msgGenericFilterEdit_NoSelectionValueType			= "データ型を選択してください。";
	public String msgGenericFilterEdit_NoOperator					= "Please select operator.";
	//public String msgGenericFilterEdit_NoOperator					= "演算子を選択してください。";
	public String msgGenericFilterEdit_UnsupportedOperator			= "Selected operator is not supported.";
	//public String msgGenericFilterEdit_UnsupportedOperator			= "この演算子は、サポートされていません。";
	public String msgGenericFilterEdit_NoLeftOperand				= "Please select a value in (value 1).";
	//public String msgGenericFilterEdit_NoLeftOperand				= "(値1)を選択してください。";
	public String msgGenericFilterEdit_NoRightOperand				= "Please select a value in (value 2).";
	//public String msgGenericFilterEdit_NoRightOperand				= "(値2)を選択してください。";
	public String msgGenericFilterEdit_InvalidLeftOperandType		= "In current operator, please select a value of the following data types in (value 1):";
	//public String msgGenericFilterEdit_InvalidLeftOperandType		= "この演算子では、(値1)には次のデータ型の値を選択してください : ";
	public String msgGenericFilterEdit_InvalidRightOperandType		= "In current operator, please select a value of the following data types in (value 2):";
	//public String msgGenericFilterEdit_InvalidRightOperandType		= "この演算子では、(値2)には次のデータ型の値を選択してください : ";
	public String msgGenericFilterEdit_NoLeftLiteralValueType		= "Please select a data type in (value 1).";
	//public String msgGenericFilterEdit_NoLeftLiteralValueType		= "(値1)のデータ型を選択してください。";
	public String msgGenericFilterEdit_NoRightLiteralValueType		= "Please select a data type in (value 2).";
	//public String msgGenericFilterEdit_NoRightLiteralValueType		= "(値2)のデータ型を選択してください。";
	public String msgGenericFilterEdit_EmptyLeftLiteralValue		= "Please input a immediate value in (value 1).";
	//public String msgGenericFilterEdit_EmptyLeftLiteralValue		= "(値1)の値を入力してください。";
	public String msgGenericFilterEdit_EmptyRightLiteralValue		= "Please input a immediate value in (value 2).";
	//public String msgGenericFilterEdit_EmptyRightLiteralValue		= "(値2)の値を入力してください。";
	public String msgGenericFilterEdit_InvalidLeftLiteralValue		= "Immediate value of (value 1) can not converted to the specified data type.";
	//public String msgGenericFilterEdit_InvalidLeftLiteralValue		= "(値1)に入力された値は、指定されたデータ型に変換できません。";
	public String msgGenericFilterEdit_InvalidRightLiteralValue		= "Immediate value of (value 2) can not converted to the specified data type.";
	//public String msgGenericFilterEdit_InvalidRightLiteralValue		= "(値2)に入力された値は、指定されたデータ型に変換できません。";
	public String msgGenericFilterEdit_Table_NoField				= "Table has no field.";
	//public String msgGenericFilterEdit_Table_NoField				= "列が指定されていません。";
	public String msgGenericFilterEdit_OutputField_NoTargetValue	= "Output target is not selected.";
	//public String msgGenericFilterEdit_OutputField_NoTargetValue	= "出力対象が選択されていません。";
	public String msgSchemaDateTimeFormatEdit_EmptyFormat	= "Please input DateTime format.";
	//public String msgSchemaDateTimeFormatEdit_EmptyFormat	= "日付時刻書式を入力してください。";
	public String msgSchemaDateTimeFormatEdit_IllegalFormat	= "DateTime format is illegal.";
	//public String msgSchemaDateTimeFormatEdit_IllegalFormat	= "日付時刻書式が正しくありません。";
	public String msgGenericInputSchemaEdit_EmptyTable		= "Input table has no field.";
	//public String msgGenericInputSchemaEdit_EmptyTable		= "列が構成されていません。";
	public String msgGenericOutputSchemaEdit_EmptyTable		= "Output table has no field.";
	//public String msgGenericOutputSchemaEdit_EmptyTable		= "出力列を指定してください。";
	public String msgGenericOutputSchemaEdit_NoTargetValue	= "Please select a value of Output target.";
	//public String msgGenericOutputSchemaEdit_NoTargetValue	= "出力対象を指定してください。";
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
