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
 * @(#)CsvFileMessages.java	2.0.0	2012/11/07
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CsvFileMessages.java	1.16	2010/09/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.csv;

import java.util.ResourceBundle;

import ssac.util.io.FieldResource;
import ssac.util.logging.AppLogger;

/**
 * CSVファイル閲覧用ビューの文字列リソース。
 * 
 * @version 2.0.0	2012/11/07
 *
 * @since 1.16
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

	//
	// Error messages
	//
	
	public String msgLimitOverCsvRecords		= "Number of records in the CSV file (%s) is greater than the number of records that can be displayed.\nIt displays only the records that can be displayed.";
	//public String msgLimitOverCsvRecords		= "CSV ファイルのレコード数(%s) が表示可能なレコード数を超えています。\n表示可能なレコードのみ表示します。";

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
