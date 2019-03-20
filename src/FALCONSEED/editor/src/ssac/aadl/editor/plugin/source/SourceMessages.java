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
