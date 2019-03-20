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
 * @(#)EditorMessages.java	1.14	2009/12/11
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.manager;

import java.util.ResourceBundle;

import ssac.util.io.FieldResource;
import ssac.util.logging.AppLogger;

/**
 * モジュールマネージャの文字列リソース。
 * 
 * @version 1.14	2009/12/11
 * @since 1.14
 */
public class ManagerMessages extends FieldResource
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
	static private ManagerMessages instance = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected String getResourceName() {
		return ManagerMessages.class.getName();
	}
	
	static public ManagerMessages getInstance() {
		if (instance == null) {
			instance = new ManagerMessages();
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
	
	public String appMainTitle	= "Package Manager";
	public String appPackBaseLocationLabel = "Package Base Location";
	
	// Menu text
	//--- [File] menu
	public String menuFile				= "File";
	public String menuFileNewDir		= "New Folder...";
	public String menuFileMoveTo		= "Move to...";
	public String menuFileRename		= "Rename...";
	public String menuFileRefresh		= "Refresh";
	public String menuFileSelectWS		= "Select Package base...";
	public String menuFileQuit			= "Quit";
	
	public String menuEdit				= "Edit";
	public String menuEditCut			= "Cut";
	public String menuEditCopy			= "Copy";
	public String menuEditPaste			= "Paste";
	public String menuEditDelete		= "Delete";
	
	public String menuHelp				= "Help";
	public String menuHelpAbout			= "About...";

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
