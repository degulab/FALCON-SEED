/*
 * @(#)WaitCursorGlassPane.java	3.3.0	2016/05/07
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;

/**
 * 砂時計カーソルを表示するのみの、Glass pane 用コンポーネント。
 * <p>はじめに、生成したこのオブジェクトのインスタンスを {@link javax.swing.JFrame#setGlassPane(Component)}、
 * または {@link javax.swing.JDialog#setGlassPane(Component)} にて登録する。
 * 登録したコンポーネントは、{@link javax.swing.JFrame#getGlassPane()} または {@link javax.swing.JDialog#getGlassPane()} で
 * 取得した後、ウェイトカーソルを表示する場合には {@link #setVisible(boolean)} に <tt>true</tt> を指定し、
 * ウェイトカーソルを消す場合には {@link #setVisible(boolean)} に <tt>false</tt> を指定する。
 * <p>ウェイトカーソルを表示している間は、そのウィンドウに対する一切の操作が行えなくなるので、
 * 長時間の処理となる場合は {@link javax.swing.SwingWorker} を利用するなどの工夫をすること。
 * 
 * @author FALCON-SEED Project
 * @version 3.3.0
 * @since 3.3.0
 */
public class WaitCursorGlassPane extends JComponent
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	private static final long serialVersionUID = -3400150361913240283L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	public WaitCursorGlassPane() {
		setOpaque(false);
		setFocusTraversalPolicy(new DefaultFocusTraversalPolicy(){
			private static final long serialVersionUID = 1L;

			@Override
			public boolean accept(Component c) {
				return false;
			}
		});
		addKeyListener(new KeyAdapter() {});
		addMouseListener(new MouseAdapter() {});
		requestFocusInWindow();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	@Override
	public void setVisible(boolean toVisible) {
		super.setVisible(toVisible);
		setFocusTraversalPolicyProvider(toVisible);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
