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
