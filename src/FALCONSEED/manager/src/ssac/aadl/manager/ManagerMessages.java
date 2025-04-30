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
