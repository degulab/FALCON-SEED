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
 * @(#)MacroSubFiltersTableCellRenderer.java	3.1.0	2014/05/19
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MacroSubFiltersTableCellRenderer.java	2.0.0	2012/10/12
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import ssac.aadl.common.CommonResources;
import ssac.falconseed.module.FilterDataError;
import ssac.falconseed.module.FilterError;
import ssac.falconseed.module.FilterErrorType;
import ssac.falconseed.module.IModuleArgConfig;
import ssac.falconseed.module.ModuleArgConfig;
import ssac.falconseed.module.ModuleArgID;
import ssac.falconseed.module.args.IMExecArgParam;
import ssac.falconseed.module.args.MExecArgTempFile;
import ssac.falconseed.module.swing.MacroFilterEditModel;
import ssac.util.io.VirtualFile;

/**
 * マクロフィルタを構成するフィルタの一覧を表示するテーブルコンポーネント専用のセルレンダラ。
 * 
 * @version 3.1.0	2014/05/19
 * @since 2.0.0
 */
public class MacroSubFiltersTableCellRenderer extends DefaultTableCellRenderer
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public MacroSubFiltersTableCellRenderer() {
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Color bgColor = null;
		Object dispValue = value;
		if (column == AbMacroSubFiltersTableModel.CI_WAITFILTERS) {
			// 待機フィルタ列のエラー判定
			TableModel tmodel = table.getModel();
			if ((tmodel instanceof AbMacroSubFiltersTableModel) && row >= 0) {
				FilterDataError moduleError = ((AbMacroSubFiltersTableModel)tmodel).getRowData(row).getModuleError();
				if (moduleError != null && moduleError.getErrorType() == FilterErrorType.FILTER_WAITFILTERS) {
					// 待機フィルタにエラーが設定されているため、背景色をエラーの背景色にする
					bgColor = CommonResources.DEF_BACKCOLOR_ERROR;
				}
			}
		}
		else if (value instanceof ModuleArgConfig) {
			// モジュール引数定義の実体（引数の値ではなく、定義そのもの）
			ModuleArgConfig arg = (ModuleArgConfig)value;
			dispValue = getArgumentMarkText(arg);
			if (arg.getUserData() instanceof FilterError) {
				// エラーデータが設定されている場合は、エラーの背景色
				bgColor = CommonResources.DEF_BACKCOLOR_ERROR;
			}
			else {
				switch (arg.getType()) {
					case IN :
						bgColor = CommonResources.DEF_BACKCOLOR_ARG_IN;
						break;
					case OUT :
						bgColor = CommonResources.DEF_BACKCOLOR_ARG_OUT;
						break;
					case STR :
						bgColor = CommonResources.DEF_BACKCOLOR_ARG_STR;
						break;
					case PUB :
						bgColor = CommonResources.DEF_BACKCOLOR_ARG_PUB;
						break;
					case SUB :
						bgColor = CommonResources.DEF_BACKCOLOR_ARG_SUB;
						break;
					default :
						bgColor = null;	// デフォルトの背景色
				}
			}
		}
		else if (column >= AbMacroSubFiltersTableModel.CI_FIRSTARG) {
			// 引数の存在しないカラムはグレー
			bgColor = new Color(128,128,128);
		}

		// コンポーネント生成
		Component comp = super.getTableCellRendererComponent(table, dispValue, isSelected, hasFocus, row, column);
		
		// 背景色の設定
		if (!isSelected) {
			if (bgColor != null) {
				comp.setBackground(bgColor);
			} else {
				comp.setBackground(table.getBackground());
			}
		}
		
		// 完了
		return comp;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public String getArgumentMarkText(final IModuleArgConfig arg) {
		if (arg != null) {
			// 引数定義が保持する値を取得
			Object value = arg.getValue();
			if (arg.getOutToTempEnabled() || value instanceof MExecArgTempFile) {
				// テンポラリファイル出力
				return "@Temp";
			}
			else if (value instanceof IMExecArgParam) {
				//--- 実行時指定
				switch (arg.getType()) {
					case IN  : return "@IN";
					case OUT : return "@OUT";
					case STR : return "@STR";
					case PUB : return "@PUB";
					case SUB : return "@SUB";
					default : return "@";
				}
			}
			else if (value instanceof IModuleArgConfig) {
				//--- 引数定義参照
				return MacroFilterEditModel.formatLinkArgNo((IModuleArgConfig)value);
			}
			else if (value instanceof ModuleArgID) {
				//--- 引数キー
				return ModuleArgID.formatShortDisplayString((ModuleArgID)value);
			}
			else if ((value instanceof VirtualFile) || (value instanceof File)) {
				return "File";
			}
			else if (value instanceof String) {
				//return "Str";
				return value.toString();
			}
			else {
				return null;
			}
		}
		
		return null;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
