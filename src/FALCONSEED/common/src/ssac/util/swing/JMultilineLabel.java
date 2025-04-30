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
