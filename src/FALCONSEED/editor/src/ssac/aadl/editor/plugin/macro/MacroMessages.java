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
 * @(#)PluginMessages.java	1.16	2010/09/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)PluginMessages.java	1.10	2009/01/27
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.macro;

import java.util.ResourceBundle;

import ssac.util.io.FieldResource;
import ssac.util.logging.AppLogger;

/**
 * AADLマクロ用ビューの文字列リソース。
 * 
 * @version 1.16	2010/09/27
 *
 * @since 1.10
 */
public class MacroMessages extends FieldResource
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
	static private MacroMessages instance = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected String getResourceName() {
		return MacroMessages.class.getName();
	}
	
	static public MacroMessages getInstance() {
		if (instance == null) {
			instance = new MacroMessages();
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
	
	public String pluginName		= "AADL Macro";
	public String pluginDesc		= "AADL Macro Plug-in";
	
	public String colNameCommand	= "Command";
	public String colNameProcName	= "Process Name";
	public String colNameComment	= "Comment";
	public String colNameJarName	= "Module name";
	public String colNameClassPath	= "Classpath";
	public String colNameMainClass	= "Main class";
	public String colNameJavaParams	= "Java parameters";
	public String colNameArgAttr	= "Attr";
	public String colNameArgValue	= "Value";
	
	public String descExtMacro	= "AADL Macro file (*.amf)";
	public String extMacro		= ".amf";
	public String descExtOpen	= "AADL Macro file (*.amf)";
	public String extOpen		= ".amf";
	
	public String chooserTitleOpen	= "Open AADL Macro file";
	public String chooserTitleSave = "Save as AADL Macro file";
	public String chooserTitleRelPath = "Choose a file";
	
	// Menu text
	//--- [Table] menu
	public String menuTable					= "Table";
	public String menuTableRowsInsertAbove	= "Insert rows above";
	public String menuTableRowsInsertBelow	= "Insert rows below";
	public String menuTableRowsInsertCopied	= "Insert copied cells";
	public String menuTableRowsCut			= "Cut rows";
	public String menuTableRowsDelete		= "Delete rows";
	public String menuTableRowsSelect		= "Select rows";
	//--- [Data] menu
	public String menuData					= "Data";
	public String menuDataCommentAdd		= "Add comment mark";
	public String menuDataCommentRemove		= "Remove comment mark";
	public String menuDataInputRelPath		= "Input relative path...";
	
	// Error messages
	public String msgCannotInsertToFirstRow		= "Cannot insert rows above the first row.";
	// public String msgCannotInsertToFirstRow		= "第１行目には行を挿入できません。";
	public String msgCannotDeleteFirstRow		= "Cannot delete the first row.";
	// public String msgCannotDeleteFirstRow		= "第１行目は削除できません。";
	public String msgCannotInsertFurtherRows	= "Cannot insert further rows.";
	// public String msgCannotInsertFurtherRows	= "これ以上は行を挿入できません。";
}
