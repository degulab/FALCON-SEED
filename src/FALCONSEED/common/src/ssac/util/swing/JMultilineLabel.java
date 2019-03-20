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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)JMultilineLabel.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * 複数行表示可能なラベルコンポーネント。
 * <p>このコンポーネントは {@link javax.swing.JTextArea} の実装であり、
 * {@link javax.swing.JLabel} と同じ背景色、前景色、無効時の前景色を適用する。
 * 標準では、ボーダーは適用されない。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public class JMultilineLabel extends JTextArea
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JMultilineLabel() {
		this(null);
	}
	
	public JMultilineLabel(String text) {
		super(text);
		setEditable(false);
		setLineWrap(true);
		setWrapStyleWord(true);
		setBorder(null);
		setBackground(UIManager.getColor("Label.background"));
		setForeground(UIManager.getColor("Label.foreground"));
		setDisabledTextColor(UIManager.getColor("Label.disabledForeground"));
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
