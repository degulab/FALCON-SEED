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
 *  Copyright 2007-2016  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)JMultilineTextField.java	3.3.0	2016/05/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * 自動折り返しのテキストフィールド。
 * <p>このオブジェクトは {@link JTextArea} をベースに、<code>[Enter]</code> キーの入力を無効化している。
 * 
 * @version 3.3.0
 * @since 3.3.0
 */
public class JMultilineTextField extends JTextArea
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = 4806797320859502673L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	JTextField a;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public JMultilineTextField() {
		super();
		setLineWrap(true);
		setWrapStyleWord(false);
	}

	public JMultilineTextField(int rows, int columns) {
		super(rows, columns);
		setLineWrap(true);
		setWrapStyleWord(false);
	}

	public JMultilineTextField(String text) {
		super(text);
		setLineWrap(true);
		setWrapStyleWord(false);
	}

	public JMultilineTextField(Document doc) {
		super(doc);
		setLineWrap(true);
		setWrapStyleWord(false);
	}

	public JMultilineTextField(String text, int rows, int columns) {
		super(text, rows, columns);
		setLineWrap(true);
		setWrapStyleWord(false);
	}

	public JMultilineTextField(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);
		setLineWrap(true);
		setWrapStyleWord(false);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@Override
	public void setDocument(Document doc) {
		if (doc != null) {
			doc.putProperty("filterNewlines", Boolean.TRUE);
		}
		super.setDocument(doc);
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
