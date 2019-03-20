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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)CommonResources.java	3.2.0	2015/06/22
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommonResources.java	3.1.0	2014/05/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommonResources.java	3.0.0	2014/03/28
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommonResources.java	2.0.0	2012/10/29
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommonResources.java	1.17	2010/11/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CommonResources.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.common;

import java.awt.Color;
import java.awt.Insets;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;

import ssac.util.swing.MenuToggleButton;

/**
 * アプリケーション共通のリソース
 * 
 * @version 3.2.0
 * @since 1.14
 */
public class CommonResources
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final int DIALOG_CONTENT_PADDING = 10;
	static public final int DIALOG_CONTROL_SPACING = 5;

	static public final Insets ICON_BUTTON_MARGIN = new Insets(0,0,0,0);
	static public final int DIALOG_CONTENT_MARGIN = 10;
	static public final Border DIALOG_CONTENT_BORDER
									= BorderFactory.createEmptyBorder(
											DIALOG_CONTENT_MARGIN,
											DIALOG_CONTENT_MARGIN,
											DIALOG_CONTENT_MARGIN,
											DIALOG_CONTENT_MARGIN);
	
	static private final String LOCAL_PATH_RESOURCE = "common_res/";
	
	/** データエラーの標準背景色 **/
	static public final Color DEF_BACKCOLOR_ERROR		= new Color(255,153,153);
	/** 引数属性[IN]の標準背景色 **/
	static public final Color DEF_BACKCOLOR_ARG_IN	= new Color(204,255,255);
	/** 引数属性[OUT]の標準背景色 **/
	static public final Color DEF_BACKCOLOR_ARG_OUT	= new Color(255,222,173);
	/** 引数属性[STR]の標準背景色 **/
	static public final Color DEF_BACKCOLOR_ARG_STR	= new Color(245,245,220);
	/** 引数属性[PUB]の標準背景色 **/
	static public final Color DEF_BACKCOLOR_ARG_PUB = new Color(255,255,153);
	/** 引数属性[SUB]の標準背景色 **/
	static public final Color DEF_BACKCOLOR_ARG_SUB = new Color(204,255,204);
	
	//------------------------------------------------------------
	// Icons
	//------------------------------------------------------------

	/*
	 * Basic icons
	 */
	
	static public final Icon ICON_BLANK		= getResourceIcon("images/iconBlank.png");
	
	static public final Icon ICON_ARROW_UP	= getResourceIcon("images/iconUpArrow.png");
	static public final Icon ICON_ARROW_DOWN	= getResourceIcon("images/iconDownArrow.png");
	static public final Icon ICON_ARROW_LEFT	= getResourceIcon("images/iconLeftArrow.png");
	static public final Icon ICON_ARROW_RIGHT	= getResourceIcon("images/iconRightArrow.png");
	static public final Icon ICON_PREVIEW		= getResourceIcon("images/iconPreview.png");
	static public final Icon ICON_BROWSE		= getResourceIcon("images/iconBrowse.png");
	
	static public final Icon ICON_SIMPLE_WINDOW	= getResourceIcon("images/iconWindowSimple.png");
	static public final Icon ICON_FRAME_WINDOW	= getResourceIcon("images/iconWindowFrame.png");
	static public final Icon ICON_TEXT_WINDOW	= getResourceIcon("images/iconWindowConsole.png");
	
	static public final Icon ICON_EXEC_START		= getResourceIcon("images/iconExecStart.png");
	static public final Icon ICON_EXEC_STOP		= getResourceIcon("images/iconExecStop.png");
	static public final Icon ICON_EXEC_STOPALL	= getResourceIcon("images/iconExecStopAll.png");
	static public final Icon ICON_CONSOLE_CLEAR	= getResourceIcon("images/iconConsoleClear.png");
	static public final Icon ICON_WINDOW_CLEAR	= getResourceIcon("images/iconWindowClear.png");
	static public final Icon ICON_SETTING			= getResourceIcon("images/iconSetting.png");
	
	static public final Icon SICON_ERROR	= getResourceIcon("images/siconError.png");
	static public final Icon SICON_CLOSE	= getResourceIcon("images/siconClose.png");
	
	/*
	 * File icons
	 */
	
	static public final Icon ICON_FILE_NEW		= getResourceIcon("images/iconFileNew.png");
	static public final Icon ICON_FILE_OPEN		= getResourceIcon("images/iconFileOpen.png");
	static public final Icon ICON_FILE_CUSTOM_OPEN	= getResourceIcon("images/iconFileCustomOpen.png");
	static public final Icon ICON_FILE_SAVE		= getResourceIcon("images/iconFileSave.png");
	static public final Icon ICON_FILE_CLOSE		= getResourceIcon("images/iconFileClose.png");
	static public final Icon ICON_FILE_ALLCLOSE	= getResourceIcon("images/iconFileAllClose.png");
	
	static public final Icon ICON_IMPORT		= getResourceIcon("images/iconImport.png");
	static public final Icon ICON_EXPORT		= getResourceIcon("images/iconExport.png");
	
	static public final Icon ICON_REFRESH		= getResourceIcon("images/iconRefresh.png");
	static public final Icon ICON_NEW_DIR		= getResourceIcon("images/iconNewDir.png");
	static public final Icon ICON_UP_DIR		= getResourceIcon("images/iconUpDir.png");
	static public final Icon ICON_NEW_FILE	= getResourceIcon("images/iconNewFile.png");
	static public final Icon ICON_FILE		= getResourceIcon("images/iconFile.png");
	
	static public final Icon ICON_DOCUMENT_OPEN		= getResourceIcon("images/iconDocument.png");
	static public final Icon ICON_DOCUMENT_NEW		= getResourceIcon("images/iconDocumentNew.png");
	static public final Icon ICON_DOCUMENT_DELETE	= getResourceIcon("images/iconDocumentDelete.png");
	static public final Icon ICON_DOCUMENT_EDIT		= getResourceIcon("images/iconDocumentEdit.png");
	
	/*
	 * Edit icons
	 */
	
	static public final Icon ICON_ADD			= getResourceIcon("images/iconAdd.png");
	static public final Icon ICON_EDIT		= getResourceIcon("images/iconEdit.png");
	static public final Icon ICON_DELETE		= getResourceIcon("images/iconDelete.png");
	
	static public final Icon ICON_UNDO		= getResourceIcon("images/iconUndo.png");
	static public final Icon ICON_REDO		= getResourceIcon("images/iconRedo.png");
	static public final Icon ICON_CUT			= getResourceIcon("images/iconCut.png");
	static public final Icon ICON_COPY		= getResourceIcon("images/iconCopy.png");
	static public final Icon ICON_PASTE		= getResourceIcon("images/iconPaste.png");
	
	static public final Icon ICON_ROWS_INSERT_ABOVE	= getResourceIcon("images/iconRowsInsertAbove.png");
	static public final Icon ICON_ROWS_INSERT_BELOW	= getResourceIcon("images/iconRowsInsertBelow.png");
	static public final Icon ICON_COLS_INSERT_LEFT	= getResourceIcon("images/iconColumnsInsertLeft.png");
	static public final Icon ICON_COLS_INSERT_RIGHT	= getResourceIcon("images/iconColumnsInsertRight.png");

	/*
	 * Find icons
	 */
	
	static public final Icon	ICON_FIND			= getResourceIcon("images/iconFind.png");
	static public final Icon	ICON_FIND_NEXT		= getResourceIcon("images/iconFindNext.png");
	static public final Icon	ICON_FIND_PREV		= getResourceIcon("images/iconFindPrev.png");
	
	/*
	 * Manager icons
	 */
	
	//--- unit
	static public final Icon ICON_DISKUNIT			= getResourceIcon("images/iconDiskUnit.png");
	//--- Project
	static public final Icon ICON_MARK_PROJECT		= getResourceIcon("images/iconMarkProject.png");
	static public final Icon ICON_NEW_PROJECT			= getResourceIcon("images/iconNewProject.png");
	static public final Icon ICON_PROJECT				= getResourceIcon("images/iconProject.png");
	//@@@ added by Y.Ishizuka : 2010.10.01
	static public final Icon ICON_MARK_OPENPROJECT	= getResourceIcon("images/iconMarkProjectOpen.png");
	static public final Icon ICON_OPENPROJECT			= getResourceIcon("images/iconProjectOpen.png");
	//@@@ end of added : 2010.10.01
	static public final Icon ICON_REGISTER_RPOJECT	= getResourceIcon("images/iconRegisterProject.png");
	static public final Icon ICON_UNREGISTER_PROJECT	= getResourceIcon("images/iconUnregisterProject.png");
	//--- Package
	static public final Icon ICON_MARK_PACK_CLOSE		= getResourceIcon("images/iconMarkPackClose.png");
	static public final Icon ICON_PACK_CLOSE			= getResourceIcon("images/iconPackClose.png");
	static public final Icon ICON_MARK_PACK_OPEN		= getResourceIcon("images/iconMarkPackOpen.png");
	static public final Icon ICON_PACK_OPEN			= getResourceIcon("images/iconPackOpen.png");
	//--- Directory
	static public final Icon ICON_MARK_DIR_OPEN		= getResourceIcon("images/iconMarkDirOpen.png");
	static public final Icon ICON_DIR_OPEN			= getResourceIcon("images/iconDirOpen.png");
	static public final Icon ICON_MARK_DIR_CLOSE		= getResourceIcon("images/iconMarkDirClose.png");
	static public final Icon ICON_DIR_CLOSE			= getResourceIcon("images/iconDirClose.png");
	//@@@ added by Y.Ishizuka : 2010.10.01
	static public final Icon ICON_LOCK_DIR_OPEN		= getResourceIcon("images/iconLockDirOpen.png");
	static public final Icon ICON_LOCK_DIR_CLOSE		= getResourceIcon("images/iconLockDirClose.png");
	//@@@ end of added : 2010.10.01
	//--- Files
	static public final Icon ICON_MARK_FILE			= getResourceIcon("images/iconMarkFile.png");
	//@@@ added by Y.Ishizuka : 2010.10.01
	static public final Icon ICON_LOCK_FILE			= getResourceIcon("images/iconLockFile.png");
	//@@@ end of added : 2010.10.01
	
	static public final Icon ICON_MARK_JARMODULE		= getResourceIcon("images/iconMarkJarModule.png");
	static public final Icon ICON_JARMODULE			= getResourceIcon("images/iconJarModule.png");
	
	static public final Icon ICON_MARK_AADLSRCFILE	= getResourceIcon("images/iconMarkAADLSrcFile.png");
	static public final Icon ICON_NEW_AADLSRCFILE		= getResourceIcon("images/iconNewAADLSrcFile.png");
	static public final Icon ICON_AADLSRCFILE			= getResourceIcon("images/iconAADLSrcFile.png");
	static public final Icon ICON_MARK_RICH_AADLSRCFILE	= getResourceIcon("images/iconMarkRichAADLSrcFile.png");
	static public final Icon ICON_NEW_RICH_AADLSRCFILE	= getResourceIcon("images/iconNewRichAADLSrcFile.png");
	static public final Icon ICON_RICH_AADLSRCFILE		= getResourceIcon("images/iconRichAADLSrcFile.png");
	
	static public final Icon ICON_MARK_MACROFILE		= getResourceIcon("images/iconMarkMacroFile.png");
	static public final Icon ICON_NEW_MACROFILE		= getResourceIcon("images/iconNewMacroFile.png");
	static public final Icon ICON_MACROFILE			= getResourceIcon("images/iconMacroFile.png");
	static public final Icon ICON_MARK_RICH_MACROFILE		= getResourceIcon("images/iconMarkRichMacroFile.png");
	static public final Icon ICON_NEW_RICH_MACROFILE		= getResourceIcon("images/iconNewRichMacroFile.png");
	static public final Icon ICON_RICH_MACROFILE			= getResourceIcon("images/iconRichMacroFile.png");
	
	static public final Icon ICON_MARK_XMLFILE		= getResourceIcon("images/iconMarkXmlFile.png");
	static public final Icon ICON_NEW_XMLFILE			= getResourceIcon("images/iconNewXmlFile.png");
	static public final Icon ICON_XMLFILE				= getResourceIcon("images/iconXmlFile.png");
	
	static public final Icon ICON_MARK_CSVFILE		= getResourceIcon("images/iconMarkCsvFile.png");
	static public final Icon ICON_NEW_CSVFILE			= getResourceIcon("images/iconNewCsvFile.png");
	static public final Icon ICON_CSVFILE				= getResourceIcon("images/iconCsvFile.png");
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public JButton createBrowseButton(String tooltip) {
		return createIconButton(ICON_BROWSE, tooltip);
	}
	
	static public JButton createIconButton(Icon icon, String tooltip) {
		JButton btn = new JButton(icon);
		btn.setMargin(ICON_BUTTON_MARGIN);
		btn.setToolTipText(tooltip);
		return btn;
	}
	
	static public MenuToggleButton createMenuIconButton(Icon icon, String tooltip) {
		MenuToggleButton btn = new MenuToggleButton(icon);
		btn.setMargin(ICON_BUTTON_MARGIN);
		btn.setToolTipText(tooltip);
		return btn;
	}
	
	static protected ImageIcon getResourceIcon(String path) {
		URL url = CommonResources.class.getClassLoader().getResource(path);
		ImageIcon icon;
		if (url != null) {
			icon = new ImageIcon(url);
		} else {
			icon = new ImageIcon(LOCAL_PATH_RESOURCE + path);
		}
		return icon;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
