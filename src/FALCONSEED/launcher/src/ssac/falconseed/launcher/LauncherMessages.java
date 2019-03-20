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
 * @(#)LauncherMessages.java	2.0.0	2012/11/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.launcher;

import java.util.ResourceBundle;

/**
 * アプリケーションランチャーの文字列リソース。
 * 
 * @version 2.0.0	2012/11/06
 */
public class LauncherMessages extends LauncherFieldResource
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
	static private LauncherMessages instance = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected String getResourceName() {
		return LauncherMessages.class.getName();
	}
	
	static public LauncherMessages getInstance() {
		if (instance == null) {
			instance = new LauncherMessages();
			String path = getResourceName();
			try {
				ResourceBundle resource = ResourceBundle.getBundle(path);
				instance.updateFields(resource);
			} catch (RuntimeException ex) {
				LauncherMain.printLauncherDebug(ex, "Failed to get bundle resource : %s", String.valueOf(path));
			}
		}
		return instance;
	}
	
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
	
	public String appMainTitle	= "Application Launcher";
	
	public String ButtonLabel_Start		= "Start";
	public String ButtonLabel_About		= "about FALCON-SEED";

	//==============================
	// Library versions dialog resources
	//==============================
	
	public String LibVersionDlg_Button = "About libraries...";
	
	public String LibVersionDlg_Title_Main				= "Library versions";
	public String LibVersionDlg_Button_Close			= "Close";
	public String LibVersionDlg_Button_CopyToClip		= "Copy to clipboard";
	public String LibVersionDlg_Title_Info_Path			= "Location";
	public String LibVersionDlg_Title_Info_LastMod		= "Last modified";
	public String LibVersionDlg_Title_Info_JarTitle		= "Title";
	public String LibVersionDlg_Title_Info_JarVersion	= "Version";

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
