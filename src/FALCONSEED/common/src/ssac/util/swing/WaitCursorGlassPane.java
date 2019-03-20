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
