/*
 * @(#)SourceMessages.java	1.10	2008/12/03
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.source;

import java.util.ResourceBundle;

import ssac.util.io.FieldResource;
import ssac.util.logging.AppLogger;

/**
 * AADLマクロ用ビューの文字列リソース。
 * 
 * @version 1.10	2008/12/03
 *
 * @since 1.10
 */
public class SourceMessages extends FieldResource
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
	static private SourceMessages instance = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected String getResourceName() {
		return SourceMessages.class.getName();
	}
	
	static public SourceMessages getInstance() {
		if (instance == null) {
			instance = new SourceMessages();
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
	
	public String pluginName		= "AADL Source";
	public String pluginDesc		= "AADL Source Plug-in";
	
	public String descExtSource	= "AADL Source file (*.aadl)";
	public String extSource		= ".aadl";
	
	public String chooserTitleOpen	= "Open AADL Source file";
	public String chooserTitleSave = "Save as AADL Source file";
	
	// Error messages
}
