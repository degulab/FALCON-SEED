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
