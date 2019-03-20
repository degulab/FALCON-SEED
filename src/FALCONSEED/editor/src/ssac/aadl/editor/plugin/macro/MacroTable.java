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
 *  Copyright 2007-2014  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MacroTable.java	3.1.0	2014/05/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroTable.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroTable.java	1.10	2009/01/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.plugin.macro;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.plugin.macro.MacroModel.MacroCell;
import ssac.aadl.editor.plugin.macro.MacroModel.MacroElementModel;
import ssac.aadl.macro.command.MacroAction;
import ssac.aadl.macro.file.CsvMacroFiles;
import ssac.util.swing.table.SpreadSheetModel;
import ssac.util.swing.table.SpreadSheetTable;

/**
 * AADLマクロのテーブル・コンポーネント。
 * 
 * @version 3.1.0	2014/05/26
 *
 * @since 1.10
 */
public class MacroTable extends SpreadSheetTable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	/** コメント行の標準前景色 **/
	static private final Color DEF_FORECOLOR_COMMENT	= new Color(  0,128,  0);
	/** サブマクロ行の標準前景色 **/
	//static private final Color DEF_FORECOLOR_MACRO	= new Color(255, 69,  0);
	static private final Color DEF_FORECOLOR_MACRO	= new Color(160, 32,  0);
	/** シェル実行行の標準前景色 **/
	static private final Color DEF_FORECOLOR_SHELL	= new Color(255,  0,255);
	/** エコー行の標準前景色 **/
	//static private final Color DEF_FORECOLOR_ECHO		= new Color( 72, 61,139);
	static private final Color DEF_FORECOLOR_ECHO		= new Color(  0,  0,255);
	/** エラー設定行の標準前景色 **/
	//static private final Color DEF_FORECOLOR_ERRCOND	= new Color(218,165, 32);
	static private final Color DEF_FORECOLOR_ERRCOND	= new Color(198,133, 46);
	/** 終了行の標準前景色 **/
	static private final Color DEF_FORECOLOR_EXIT		= new Color(255,  0,  0);
	/** wait行の標準前景色 **/
	static private final Color DEF_FORECOLOR_WAIT	= new Color(128,0,255);
	/** データエラーの標準背景色 **/
	static private final Color DEF_BACKCOLOR_ERROR	= CommonResources.DEF_BACKCOLOR_ERROR;

	/** Javaプロセス実行行の前景色 **/
	static private Color foreColorJava		= null;
	/** Javaグループ実行行の前景色 **/
	static private Color foreColorGroup	= null;
	/** コメント行の標準前景色 **/
	static private Color foreColorComment	= DEF_FORECOLOR_COMMENT;
	/** サブマクロ行の標準前景色 **/
	static private Color foreColorMacro	= DEF_FORECOLOR_MACRO;
	/** シェル実行行の標準前景色 **/
	static private Color foreColorShell	= DEF_FORECOLOR_SHELL;
	/** エコー行の標準前景色 **/
	static private Color foreColorEcho		= DEF_FORECOLOR_ECHO;
	/** エラー設定行の標準前景色 **/
	static private Color foreColorErrCond	= DEF_FORECOLOR_ERRCOND;
	/** 終了行の標準前景色 **/
	static private Color foreColorExit		= DEF_FORECOLOR_EXIT;
	/** wait行の標準前景色 **/
	static private Color foreColorWait	= DEF_FORECOLOR_WAIT;
	/** データエラーの標準背景色 **/
	static private Color backColorError	= DEF_BACKCOLOR_ERROR;
	/** 引数属性[IN]の標準背景色 **/
	static private Color backColorArgIn	= CommonResources.DEF_BACKCOLOR_ARG_IN;
	/** 引数属性[OUT]の標準背景色 **/
	static private Color backColorArgOut	= CommonResources.DEF_BACKCOLOR_ARG_OUT;
	/** 引数属性[STR]の標準背景色 **/
	static private Color backColorArgStr	= CommonResources.DEF_BACKCOLOR_ARG_STR;
	/** 引数属性[PUB]の標準背景色 **/
	static private Color backColorArgPub	= CommonResources.DEF_BACKCOLOR_ARG_PUB;
	/** 引数属性[SUB]の標準背景色 **/
	static private Color backColorArgSub	= CommonResources.DEF_BACKCOLOR_ARG_SUB;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public MacroTable() {
		super();
	}

	public MacroTable(SpreadSheetModel model) {
		super(model);
	}
	
	@Override
	protected void initialComponents() {
		super.initialComponents();
		
		// 標準のセルレンダラーを設定
		setDefaultRenderer(MacroCell.class, new MacroCellRenderer());
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 指定された列インデックスが、マクロコマンド列であるかを判定する。
	 * ここで指定する列インデックスは、モデルの列インデックスに変換してから判定される。
	 * @param columnIndex	判定する列インデックス
	 * @return マクロコマンド列の列インデックスなら <tt>true</tt>
	 */
	public boolean isCommandColumn(int columnIndex) {
		return ((MacroModel)getModel()).isCommandColumn(convertColumnIndexToModel(columnIndex));
	}

	/**
	 * 指定された列インデックスが、プロセス名列であるかを判定する。
	 * ここで指定する列インデックスは、モデルの列インデックスに変換してから判定される。
	 * @param columnIndex	判定する列インデックス
	 * @return	プロセス名列の列インデックスなら <tt>true</tt>
	 */
	public boolean isProcessNameColumn(int columnIndex) {
		return ((MacroModel)getModel()).isProcessNameColumn(convertColumnIndexToModel(columnIndex));
	}

	/**
	 * 指定された列インデックスが、コメント列であるかを判定する。
	 * ここで指定する列インデックスは、モデルの列インデックスに変換してから判定される。
	 * @param columnIndex	判定する列インデックス
	 * @return	コメント列の列インデックスなら <tt>true</tt>
	 */
	public boolean isCommentColumn(int columnIndex) {
		return ((MacroModel)getModel()).isCommentColumn(convertColumnIndexToModel(columnIndex));
	}

	/**
	 * 指定された列インデックスが、実行モジュール名列であるかを判定する。
	 * ここで指定する列インデックスは、モデルの列インデックスに変換してから判定される。
	 * @param columnIndex	判定する列インデックス
	 * @return	実行モジュール名列の列インデックスなら <tt>true</tt>
	 */
	public boolean isModuleNameColumn(int columnIndex) {
		return ((MacroModel)getModel()).isModuleNameColumn(convertColumnIndexToModel(columnIndex));
	}

	/**
	 * 指定された列インデックスが、クラスパス列であるかを判定する。
	 * ここで指定する列インデックスは、モデルの列インデックスに変換してから判定される。
	 * @param columnIndex	判定する列インデックス
	 * @return	クラスパス列の列インデックスなら <tt>true</tt>
	 */
	public boolean isClassPathColumn(int columnIndex) {
		return ((MacroModel)getModel()).isClassPathColumn(convertColumnIndexToModel(columnIndex));
	}

	/**
	 * 指定された列インデックスが、クラス名列であるかを判定する。
	 * ここで指定する列インデックスは、モデルの列インデックスに変換してから判定される。
	 * @param columnIndex	判定する列インデックス
	 * @return	クラスパス列の列インデックスなら <tt>true</tt>
	 */
	public boolean isMainClassColumn(int columnIndex) {
		return ((MacroModel)getModel()).isMainClassColumn(convertColumnIndexToModel(columnIndex));
	}

	/**
	 * 指定された列インデックスが、Javaパラメータ列であるかを判定する。
	 * ここで指定する列インデックスは、モデルの列インデックスに変換してから判定される。
	 * @param columnIndex	判定する列インデックス
	 * @return	Javaパラメータ列の列インデックスなら <tt>true</tt>
	 */
	public boolean isJavaParametersColumn(int columnIndex) {
		return ((MacroModel)getModel()).isJavaParametersColumn(convertColumnIndexToModel(columnIndex));
	}

	/**
	 * 指定された列インデックスが、引数(属性ならびに値)列であるかを判定する。
	 * ここで指定する列インデックスは、モデルの列インデックスに変換してから判定される。
	 * @param columnIndex	判定する列インデックス
	 * @return	引数列の列インデックスなら <tt>true</tt>
	 */
	public boolean isArgumentsColumn(int columnIndex) {
		return ((MacroModel)getModel()).isArgumentsColumn(convertColumnIndexToModel(columnIndex));
	}

	/**
	 * 指定された列インデックスが、引数属性列であるかを判定する。
	 * ここで指定する列インデックスは、モデルの列インデックスに変換してから判定される。
	 * @param columnIndex	判定する列インデックス
	 * @return	引数属性列の列インデックスなら <tt>true</tt>
	 */
	public boolean isArgumentTypeColumn(int columnIndex) {
		return ((MacroModel)getModel()).isArgumentTypeColumn(convertColumnIndexToModel(columnIndex));
	}

	/**
	 * 指定された列インデックスが、引数値列であるかを判定する。
	 * ここで指定する列インデックスは、モデルの列インデックスに変換してから判定される。
	 * @param columnIndex	判定する列インデックス
	 * @return	引数値列の列インデックスなら <tt>true</tt>
	 */
	public boolean isArgumentValueColumn(int columnIndex) {
		return ((MacroModel)getModel()).isArgumentValueColumn(convertColumnIndexToModel(columnIndex));
	}

	/**
	 * 指定された列インデックスが、ファイル名を記述する列であるかを判定する。
	 * ファイル名を記述する列は、実行モジュール名列と引数値列の場合とする。
	 * ここで指定する列インデックスは、モデルの列インデックスに変換してから判定される。
	 * @param columnIndex	判定する列インデックス
	 * @return	ファイル名を記述する列の列インデックスなら <tt>true</tt>
	 */
	public boolean isFileNameColumn(int columnIndex) {
		return ((MacroModel)getModel()).isFileNameColumn(convertColumnIndexToModel(columnIndex));
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		int row = rowAtPoint(event.getPoint());
		int col = columnAtPoint(event.getPoint());
		if (row >= 0 && col >= 0) {
			MacroCell cell = (MacroCell)getModel().getValueAt(row, convertColumnIndexToModel(col));
			if (cell != null) {
				return cell.getTooltipText();
			} else {
				return null;
			}
		}
		
		return super.getToolTipText(event);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * マクロのセルレンダラー
	 */
	static class MacroCellRenderer extends DefaultTableCellRenderer
	{
	    private final String layoutCompoundLabel(JLabel label, FontMetrics fm, String text, Icon icon,
	    									Rectangle rcView, Rectangle rcIcon, Rectangle rcText)
	    {
	    	return SwingUtilities.layoutCompoundLabel(
	    				label, fm, text, icon,
	    				label.getVerticalAlignment(),
	    				label.getHorizontalAlignment(),
	    				label.getVerticalTextPosition(),
	    				label.getHorizontalTextPosition(),
	    				rcView, rcIcon, rcText, label.getIconTextGap());
	    }
	    
	    private final Rectangle getTargetStringBounds(Graphics g, FontMetrics fm, Rectangle rcText, String text, int index, int len) {
	    	int tlen = text.length();
	    	int rb = index + len;
	    	Rectangle rc;
	    	if (rb < tlen) {
	    		Rectangle2D rcL = fm.getStringBounds(text, 0, index, g);
	    		Rectangle2D rcR = fm.getStringBounds(text, rb, tlen, g);
	    		rc = new Rectangle(
	    				(int)(rcText.x + rcL.getWidth()),
	    				rcText.y,
	    				(int)(rcText.width - rcL.getWidth() - rcR.getWidth()),
	    				rcText.height);
	    	} else {
	    		Rectangle2D rcL = fm.getStringBounds(text, 0, index, g);
	    		rc = new Rectangle(
	    				(int)(rcText.x + rcL.getWidth()),
	    				rcText.y,
	    				(int)(rcText.width - rcL.getWidth()),
	    				rcText.height);
	    	}
	    	return rc;
	    }
	    
	    private final void drawWhitespace(Graphics g, FontMetrics fm, Rectangle rcArea, Color fg, Color bg) {
	    	g.setColor(fg);
	    	g.drawRect(rcArea.x, rcArea.y+1, rcArea.width-1, fm.getAscent()-1);
	    }
	    
	    private final void drawWideWhitespace(Graphics g, FontMetrics fm, Rectangle rcArea, Color fg, Color bg) {
	    	g.setColor(fg);
	    	int w = rcArea.width-1;
	    	int arcHeight = Math.max(2, (int)(w*0.2));
	    	g.drawRoundRect(rcArea.x, rcArea.y+1, w, fm.getAscent()-1, arcHeight, arcHeight);
	    }
	    
	    @Override
		protected void paintComponent(Graphics g) {
			// 標準の描画
			super.paintComponent(g);

			/* --- 今は空白表示しない ---
			String text = this.getText();
			Icon icon = (this.isEnabled()) ? this.getIcon() : this.getDisabledIcon();
			FontMetrics fm = this.getFontMetrics(g.getFont());
			Insets insets = this.getInsets();
			Rectangle rcView = new Rectangle(
									insets.left, insets.top,
									this.getWidth()-(insets.left + insets.right),
									this.getHeight()-(insets.top + insets.bottom));
			Rectangle rcIcon = new Rectangle(0,0,0,0);
			Rectangle rcText = new Rectangle(0,0,0,0);
			String clippedText = layoutCompoundLabel(this, fm, text, icon, rcView, rcIcon, rcText);
			//--- icon は再描画しない
			if (text != null) {
				View v = (View)this.getClientProperty(BasicHTML.propertyKey);
				if (v != null) {
					//--- 何もしない
				} else {
					int len = clippedText.length();
					for (int i = 0; i < len; i++) {
						char c = clippedText.charAt(i);
						if (c == ' ') {
							//--- 半角スペース
							Rectangle rc = getTargetStringBounds(g, fm, rcText, clippedText, i, 1);
							//g.setColor(Color.YELLOW);
							//g.fillRect(rc.x, rc.y, rc.width, rc.height);
							drawWhitespace(g, fm, rc, Color.GRAY, this.getBackground());
						}
						else if (c == '　') {
							//--- 全角スペース
							Rectangle rc = getTargetStringBounds(g, fm, rcText, clippedText, i, 1);
							//g.setColor(Color.YELLOW);
							//g.fillRect(rc.x, rc.y, rc.width, rc.height);
							drawWideWhitespace(g, fm, rc, Color.GRAY, this.getBackground());
						}
					}
				}
			}
			*/
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
														boolean isSelected, boolean hasFocus,
														int row, int column)
		{
			//System.err.println("MacroCellRenderer#getTableCellRendererComponent: value(" + String.valueOf(value)
			//		+ ") isSelected(" + String.valueOf(isSelected)
			//		+ ") hasFocus(" + String.valueOf(hasFocus)
			//		+ ") row(" + row + ") column(" + column + ")");

			// 標準のレンダラーで描画
			//--- (!isSelected && hasFocus)の状態では、super#getTableCellRendererComponent() 内で、
			//--- "Table.focusCellForeground" と "Table.focusCellBackground" にカラーが変更されているため、
			//--- 元に戻す。
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			// 選択時以外の色を設定
			if (!isSelected) {
				// モデルを取得
				MacroModel model = (MacroModel)table.getModel();
				
				// 前景色設定
				Color foreColor = null;
				MacroElementModel rowData = (MacroElementModel)model.getRow(row);
				MacroAction action = (rowData==null ? null : rowData.getMacroAction());
				if (action != null) {
					if (MacroAction.JAVA == action) {
						foreColor = foreColorJava;
					}
					else if (MacroAction.GROUP == action) {
						foreColor = foreColorGroup;
					}
					else if (MacroAction.COMMENT == action) {
						foreColor = foreColorComment;
					}
					else if (MacroAction.ECHO == action) {
						foreColor = foreColorEcho;
					}
					else if (MacroAction.MACRO == action) {
						foreColor = foreColorMacro;
					}
					else if (MacroAction.SHELL == action) {
						foreColor = foreColorShell;
					}
					else if (MacroAction.ERRORCOND == action) {
						foreColor = foreColorErrCond;
					}
					else if (MacroAction.WAIT == action) {
						foreColor = foreColorWait;
					}
					else if (MacroAction.EXIT == action) {
						foreColor = foreColorExit;
					}
				}
				if (foreColor != null)
					comp.setForeground(foreColor);
				else
					comp.setForeground(table.getForeground());
				
				// 背景色
				Color backColor = null;
				int columnIndex = table.convertColumnIndexToModel(column);
				if ((value instanceof MacroCell) && ((MacroCell)value).isError()) {
					backColor = backColorError;
				}
				else if (columnIndex >= CsvMacroFiles.FIELD_ARG_TYPE1) {
					int coloffset = columnIndex - CsvMacroFiles.FIELD_ARG_TYPE1;
					int typecol = columnIndex - (coloffset % 2);
					Object obj = model.getValueAt(row, typecol);
					if (obj != null) {
						String typekey = obj.toString();
						if (MacroModel.ARGATTR_IN.equalsIgnoreCase(typekey))
							backColor = backColorArgIn;
						else if (MacroModel.ARGATTR_OUT.equalsIgnoreCase(typekey))
							backColor = backColorArgOut;
						else if (MacroModel.ARGATTR_STR.equalsIgnoreCase(typekey))
							backColor = backColorArgStr;
						else if (MacroModel.ARGATTR_PUB.equalsIgnoreCase(typekey))
							backColor = backColorArgPub;
						else if (MacroModel.ARGATTR_SUB.equalsIgnoreCase(typekey))
							backColor = backColorArgSub;
					}
				}
				if (backColor != null)
					comp.setBackground(backColor);
				else
					comp.setBackground(table.getBackground());
			}
			
			return comp;
		}
	}
}
