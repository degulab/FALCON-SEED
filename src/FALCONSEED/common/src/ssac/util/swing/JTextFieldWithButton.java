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
 * @(#)JTextFieldWithButton.java	1.10	2009/01/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <code>JTextField</code> と <code>JButton</code> を組み合わせた
 * コンポーネント。
 * 
 * @version 1.10	2009/01/06
 *
 * @since 1.10
 */
public class JTextFieldWithButton extends JPanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final String DEFAULT_BUTTON_TEXT = "...";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	protected final JTextField	field;
	protected final JButton		button;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public JTextFieldWithButton() {
		this(null, null);
	}
	
	public JTextFieldWithButton(String fieldText) {
		this(fieldText, null);
	}
	
	public JTextFieldWithButton(String fieldText, String buttonText) {
		super(new BorderLayout());
		this.field = createDefaultTextField();
		this.button = createDefaultButton();
		//--- set field text
		if (fieldText != null)
			this.field.setText(fieldText);
		//--- set button text
		if (buttonText != null)
			this.button.setText(buttonText);
		else
			this.button.setText(DEFAULT_BUTTON_TEXT);
		//--- setup layout
		add(this.button, BorderLayout.EAST);
		add(this.field, BorderLayout.CENTER);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * フィールド・コンポーネントに格納されているテキストを返す。
	 * @return テキスト
	 */
	public String getFieldText() {
		return field.getText();
	}

	/**
	 * フィールド・コンポーネントに、指定されたテキストを設定する。
	 * @param text	設定する新しいテキスト
	 */
	public void setFieldText(String text) {
		field.setText(text);
	}
	
	/**
	 * ボタン・コンポーネントに設定されているテキストを返す。
	 * @return	テキスト
	 */
	public String getButtonText() {
		return button.getText();
	}
	
	/**
	 * ボタン・コンポーネントに、指定されたテキストを設定する。
	 * @param text	設定する新しいテキスト
	 */
	public void setButtonText(String text) {
		button.setText(text);
	}

	/**
	 * このコンポーネントに含まれる、フィールド・コンポーネントを取得する。
	 * @return <code>JTextField</code> のインスタンス
	 */
	public JTextField getFieldComponent() {
		return field;
	}

	/**
	 * このコンポーネントに含まれる、ボタン・コンポーネントを取得する。
	 * @return	<code>JButton</code> のインスタンス
	 */
	public JButton getButtonComponent() {
		return button;
	}

	/**
	 * フィールド・コンポーネントに対し、指定されたアクションリスナーを追加して、
	 * アクションイベントをフィールド・コンポーネントから受け取る。
	 * @param listener	追加されるアクションリスナー
	 */
	public void addFieldActionListener(ActionListener listener) {
		field.addActionListener(listener);
	}

	/**
	 * ボタン・コンポーネントに対し、指定されたアクションリスナーを追加して、
	 * アクションイベントをボタン・コンポーネントから受け取る。
	 * @param listener	追加されるアクションリスナー
	 */
	public void addButtonActionListener(ActionListener listener) {
		button.addActionListener(listener);
	}

	/**
	 * フィールド・コンポーネントから、指定されたアクションリスナーを削除して、
	 * アクションイベントをフィールド・コンポーネントからそれ以上受け取らないようにする。
	 * @param listener	削除されるアクションリスナー
	 */
	public void removeFieldActionListener(ActionListener listener) {
		field.removeActionListener(listener);
	}

	/**
	 * ボタン・コンポーネントから、指定されたアクションリスナーを削除して、
	 * アクションイベントをボタン・コンポーネントからそれ以上受け取らないようにする。
	 * @param listener	削除されるアクションリスナー
	 */
	public void removeButtonActionListener(ActionListener listener) {
		button.removeActionListener(listener);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * このコンポーネント標準の <code>JTextField</code> を生成する。
	 * @return <code>JTextField</code> のインスタンス
	 */
	protected JTextField createDefaultTextField() {
		JTextField tf = new JTextField();
		tf.setBorder(null);
		tf.setMargin(null);
		return tf;
	}

	/**
	 * このコンポーネント標準の <code>JButton</code> を生成する。
	 * @return	<code>JButton</code> のインスタンス
	 */
	protected JButton createDefaultButton() {
		JButton btn = new JButton();
		btn.setMargin(new Insets(2,2,2,2));
		Font font = btn.getFont();
		AffineTransform at = new AffineTransform();
		at.scale(0.80, 0.80);
		font = font.deriveFont(at);
		btn.setFont(font);
		return btn;
	}
}
