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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CsvFileMessages.java	2.0.0	2012/10/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileMessages.java	1.21	2012/06/06
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileMessages.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.editor.plugin.csv;

import java.util.ResourceBundle;

import ssac.util.io.FieldResource;
import ssac.util.logging.AppLogger;

/**
 * CSVファイル閲覧用ビューの文字列リソース。
 * 
 * @version 2.0.0	2012/10/03
 */
public class CsvFileMessages extends FieldResource
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
	static private CsvFileMessages instance = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected String getResourceName() {
		return CsvFileMessages.class.getName();
	}
	
	static public CsvFileMessages getInstance() {
		if (instance == null) {
			instance = new CsvFileMessages();
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
	
	public String pluginName		= "CSV File";
	public String pluginDesc		= "CSV File viewer Plug-in";
	
	public String descExtCsv	= "CSV file (*.csv)";
	public String extCsv		= ".csv";
	public String descExtOpen	= "CSV file (*.csv)";
	public String extOpen		= ".csv";
	
	public String chooserTitleOpen	= "Open CSV file";
	public String chooserTitleSave = "Save as CSV file";
	public String chooserTitleRelPath = "Choose a file";
	
	public String msgNowValueReading = "skip...";
	public String msgValueReadError  = "failed!";

	public String menuFileSaveAsMenu		= "Save As";
	public String menuFileSaveAsDefault		= "Default settings...";
	public String menuFileSaveAsConfig		= "Configure settings...";
	public String menuFileExportDtalge		= "Export to Dtalge...";

	public String DtalgeNullState	= "null";
	public String DtalgeNameKey		= "name";
	public String DtalgeTypeKey		= "type";
	public String DtalgeAttrKey		= "attr";
	public String DtalgeSubjectKey	= "subject";
	
	public String progressAutoDetect_title		= "Auto-Detection of data types.";
	public String progressAutoDetect_desc		= "detecting...";
	public String progressExportDtalge_title	= "Export to Dtalge";
	public String progressExportDtalge_desc		= "exporting...";
//	public String progressAutoDetect_title		= "データ型の自動判別";
//	public String progressAutoDetect_desc		= "データ型判別中...";
//	public String progressExportDtalge_title	= "データ代数形式の出力";
//	public String progressExportDtalge_desc		= "出力中...";
	
	public String ExportDtalgeDlg_title						= "Export settings for Dtalge format";
	public String ExportDtalgeDlg_title_filechooser			= "Choose a destination file";
	public String ExportDtalgeDlg_label_FilePath			= "Destination file";
	public String ExportDtalgeDlg_label_Table				= "Header settings";
	public String ExportDtalgeDlg_button_IgnoreDtBaseMulti	= "Ignore the repetition of the DtBase";
	public String ExportDtalgeDlg_button_AutoDetect			= "Auto-Detection of data types";
	public String ExportDtalgeDlg_button_ChooseFile			= "Choose a destination...";
	public String ExportDtalgeDlg_illegalBaseKeyChars		= "White-spaces  <  >  -  ,  ^  %  &  ?  |  @  \'  \"";
	public String ExportDtalgeDlg_button_AllNullStateChange	= "select/deselect all";
//	public String ExportDtalgeDlg_title						= "データ代数形式の出力設定";
//	public String ExportDtalgeDlg_title_filechooser			= "出力先の選択";
//	public String ExportDtalgeDlg_label_FilePath			= "出力ファイル";
//	public String ExportDtalgeDlg_label_Table				= "ヘッダー設定";
//	public String ExportDtalgeDlg_button_IgnoreDtBaseMulti	= "基底の重複を無視する";
//	public String ExportDtalgeDlg_button_AutoDetect			= "データ型の自動判別";
//	public String ExportDtalgeDlg_button_ChooseFile			= "出力先の選択...";
//	public String ExportDtalgeDlg_illegalBaseKeyChars		= "空白(改行)  <  >  -  ,  ^  %  &  ?  |  @  \'  \"";
//	public String ExportDtalgeDlg_button_AllNullStateChange	= "全て選択/全て非選択";
	
	// Error messages
	public String msgOutputFileEmpty			= "Please choose a destination file.";
	//public String msgOutputFileEmpty			= "出力先を選択してください。";
	public String msgFailedToAutoDetect			= "Failed to auto-detect of data types.";
	//public String msgFailedToAutoDetect			= "データ型の自動判別に失敗しました。";
	public String msgDtBaseMultiple				= "In Line:%d and Line:%d, the definition of the base repeats.";
	//public String msgDtBaseMultiple				= "行:%d と 行:%d で、基底の定義が重複しています。";
	public String msgDtBaseIllegalBaseKeyChars	= "Line:%d [%s] Cannot include the following characters.";
	//public String msgDtBaseIllegalBaseKeyChars	= "行:%d [%s] 次の文字を含めることはできません。";
	public String msgDtBaseIllegalTypeKey		= "Line:%d [%s] Illegal data type.";
	//public String msgDtBaseIllegalTypeKey		= "行:%d [%s] データ型が不正です。";
	public String msgDtBaseNameEmpty			= "Line:%d [%s] Please input a name.";
	//public String msgDtBaseNameEmpty			= "行:%d [%s] 名前を入力してください。";
	public String msgDtBaseTypeEmpty			= "Line:%d [%s] Please choose a data type.";
	//public String msgDtBaseTypeEmpty			= "行:%d [%s] データ型を選択してください。";
	public String msgExportDtalgeNoRecords		= "There are no records can be exported.";
	//public String msgExportDtalgeNoRecords		= "エクスポート可能なレコードがありません。";
	public String msgLimitOverCsvRecords		= "Number of records in the CSV file (%s) is greater than the number of records that can be displayed.\nIt displays only the records that can be displayed.";
	//public String msgLimitOverCsvRecords		= "CSV ファイルのレコード数(%s) が表示可能なレコード数を超えています。\n表示可能なレコードのみ表示します。";

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
