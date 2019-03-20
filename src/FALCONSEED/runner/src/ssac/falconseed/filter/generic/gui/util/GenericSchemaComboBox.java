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
 * @(#)GenericSchemaComboBox.java	3.2.1	2015/07/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.filter.generic.gui.util;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import ssac.falconseed.filter.generic.gui.table.InputCsvFieldSchemaEditModel;

/**
 * 汎用フィルタのスキーマ選択用コンボボックス。
 * 
 * @version 3.2.1
 * @since 3.2.1
 */
public class GenericSchemaComboBox extends JComboBox
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private final Color		_defaultForeColor;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public GenericSchemaComboBox() {
		super();
		setEditable(false);
		setRenderer(new GenericSchemaListCellRenderer());
		_defaultForeColor = getForeground();
	}

	public GenericSchemaComboBox(ComboBoxModel aModel) {
		super(aModel);
		setEditable(false);
		setRenderer(new GenericSchemaListCellRenderer());
		_defaultForeColor = getForeground();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void resetEditorForeground() {
		setForeground(_defaultForeColor);
	}
	
	public void setEditorForeground(Color foreColor) {
		if (foreColor == null) {
			resetEditorForeground();
		} else {
			setForeground(foreColor);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

	/**
	 * スキーマ選択用コンボボックスのリストセルレンダラ―。
	 * @version 3.2.1
	 * @since 3.2.1
	 */
	static protected class GenericSchemaListCellRenderer extends DefaultListCellRenderer
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			GenericSchemaListCellRenderer renderer = (GenericSchemaListCellRenderer)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			// 表示用文字列の設定
			if (value instanceof InputCsvFieldSchemaEditModel) {
				renderer.setText(((InputCsvFieldSchemaEditModel)value).toTreeString());
			}
			
			return renderer;
		}
	}
}
