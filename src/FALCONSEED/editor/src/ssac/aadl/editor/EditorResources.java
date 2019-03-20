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
 * @(#)EditorResources.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)EditorResources.java	1.10	2009/01/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * AADLエディタのリソース。
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.10
 */
public class EditorResources
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String PATH_RESOURCE = "resource/";
	
	static public final Icon ICON_BUILD_COMPILE	= getResourceIcon("images/iconBuildCompile.png");
	static public final Icon ICON_BUILD_COMPRUN	= getResourceIcon("images/iconBuildCompRun.png");
	static public final Icon ICON_BUILD_RUN		= getResourceIcon("images/iconBuildRun.png");
	
	static public final Icon SICON_NORMAL_CLOSE	= getResourceIcon("images/siconCloseNormal.png");
	static public final Icon SICON_HOOVER_CLOSE	= getResourceIcon("images/siconCloseHoover.png");
	static public final Icon SICON_PRESSED_CLOSE	= getResourceIcon("images/siconClosePressed.png");
	
	static public final Icon ICON_TABLE_ROWS_INSERT_ABOVE	= getResourceIcon("images/iconRowsInsertAbove.png");
	static public final Icon ICON_TABLE_ROWS_INSERT_BELOW	= getResourceIcon("images/iconRowsInsertBelow.png");
	static public final Icon ICON_TABLE_ROWS_CUT			= getResourceIcon("images/iconRowsCut.png");
	static public final Icon ICON_TABLE_ROWS_DELETE		= getResourceIcon("images/iconRowsDelete.png");
	
	static public final Icon ICON_DATA_COMMENT_ADD		= getResourceIcon("images/iconCommentAdd.png");
	static public final Icon ICON_DATA_COMMENT_REMOVE		= getResourceIcon("images/iconCommentRemove.png");
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static protected ImageIcon getResourceIcon(String path) {
		URL url = AADLEditor.class.getClassLoader().getResource(path);
		ImageIcon icon;
		if (url != null) {
			icon = new ImageIcon(url);
		} else {
			icon = new ImageIcon(PATH_RESOURCE + path);
		}
		return icon;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
