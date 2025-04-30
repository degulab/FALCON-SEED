/*
 * @(#)DtContainerEditorResources.java	1.0.0	2022/12/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package dtalge.container.editor;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * データコンテナエディタのリソース。
 * 
 * @version 1.0.0
 */
public class DtContainerEditorResources
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final String PATH_RESOURCE = "dtcontainereditor/resource/";
	
	/** アプリケーションアイコン **/
	static public final Icon ICON_APP					= getResourceIcon("images/DtContainerEditorAppIcon.png");
	
	static public final Icon ICON_NORMAL_CLOSE			= getResourceIcon("images/siconCloseNormal.png");
	static public final Icon ICON_HOVER_CLOSE			= getResourceIcon("images/siconCloseHover.png");
	static public final Icon ICON_PRESSED_CLOSE			= getResourceIcon("images/siconClosePressed.png");
	
	static public final Icon ICON_IMPORT_INTO_NODE		= getResourceIcon("images/iconImportIntoNode.png");
	static public final Icon ICON_EXPORT_FROM_NODE		= getResourceIcon("images/iconExportFromNode.png");
	static public final Icon ICON_SELECT_PARENT_NODE	= getResourceIcon("images/iconSelectParentNode.png");
	static public final Icon ICON_SELECT_TARGET_NODE	= getResourceIcon("images/iconSelectTargetNode.png");
	
	static public final Icon ICON_TABLE_ROWS_INSERT_ABOVE	= getResourceIcon("images/iconRowsInsertAbove.png");
	static public final Icon ICON_TABLE_ROWS_INSERT_BELOW	= getResourceIcon("images/iconRowsInsertBelow.png");
	static public final Icon ICON_TABLE_ROWS_CUT			= getResourceIcon("images/iconRowsCut.png");
	static public final Icon ICON_TABLE_ROWS_DELETE			= getResourceIcon("images/iconRowsDelete.png");
	
	static public final Icon ICON_BALL_BLUE			= getResourceIcon("images/iconBlueBall.png");
	static public final Icon ICON_BALL_CYAN			= getResourceIcon("images/iconCyanBall.png");
	static public final Icon ICON_BALL_GRAY			= getResourceIcon("images/iconGrayBall.png");
	static public final Icon ICON_BALL_GREEN		= getResourceIcon("images/iconGreenBall.png");
	static public final Icon ICON_BALL_MAGENTA		= getResourceIcon("images/iconMagentaBall.png");
	static public final Icon ICON_BALL_ORANGE		= getResourceIcon("images/iconOrangeBall.png");
	static public final Icon ICON_BALL_RED			= getResourceIcon("images/iconRedBall.png");
	static public final Icon ICON_BALL_YELLOW		= getResourceIcon("images/iconYellowBall.png");
	
	
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
		URL url = DtContainerEditorResources.class.getClassLoader().getResource(path);
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

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
