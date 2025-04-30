/*
 * @(#)JStaticMultilineTextPane.java	1.17	2010/11/19
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import javax.swing.UIManager;

/**
 * 複数行表示可能なラベルコンポーネント。
 * <p>このコンポーネントは {@link javax.swing.JTextArea} の実装であり、
 * {@link javax.swing.JLabel} と同じ背景色、前景色、無効時の前景色を適用する。
 * このコンポーネントのボーダーには、{@link javax.swing.JTextField} のボーダーが適用される。
 * 
 * @version 1.17	2010/11/19
 * @since 1.17
 */
public class JStaticMultilineTextPane extends JMultilineLabel
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
	
	public JStaticMultilineTextPane() {
		this(null);
	}
	
	public JStaticMultilineTextPane(String text) {
		super(text);
		setBorder(UIManager.getBorder("TextField.border"));
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
