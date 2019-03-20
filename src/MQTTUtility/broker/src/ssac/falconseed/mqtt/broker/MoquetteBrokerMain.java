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
 *  Copyright 2007-2013  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MoquetteBrokerMain.java	0.3.1	2013/07/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MoquetteBrokerMain.java	0.3.0	2013/06/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)MoquetteBrokerMain.java	0.1.0	2013/05/15
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.mqtt.broker;

import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ssac.falconseed.mqtt.broker.util.MoquetteBrokerGUI;

/**
 * Moquette Micro Broker を起動する GUI アプリケーション。
 * 
 * @version 0.3.1	2013/07/05
 * 
 * @author Yasunari Ishizuka (PieCake,Inc.)
 * @author Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 * @author Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
public class MoquetteBrokerMain extends JFrame
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static public final String APP_NAME = "Moquette Micro Broker";
	
	static public final String VERSION = "0.3.1";
	static public final String BUILD = "20130705";
	
	static public final String SIMPLE_VERSION_INFO
									= APP_NAME + " " + VERSION + "(" + BUILD + ")";
	
	static public final String VERSION_MESSAGE
									= APP_NAME + "\n"
									+ " - Version " + VERSION
									+ " (" + BUILD + ")";
	
	static public final String LOCAL_VERSION = APP_NAME + " (" + BUILD + ")";
	
	static public final String DEBUG_OPTION = "-debug";
	static public final String LANGUAGE_OPTION = "-language";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static protected String		_argLocale = null;
	static protected boolean	_debug = false;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// check arguments
		for (int i = 0; i < args.length; i++) {
			if (LANGUAGE_OPTION.equals(args[i]) && ((i+1) < args.length) && !args[i+1].equals("")) {
				// has locale
				i++;
				_argLocale = args[i];
				Locale defLocale = new Locale(args[i]);
				Locale.setDefault(defLocale);
			}
			else if (DEBUG_OPTION.equals(args[i])) {
				_debug = true;
			}
		}
		
		// アプリケーション開始
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					// Look & Feel の初期化
					JFrame.setDefaultLookAndFeelDecorated(false);	// Frame概観は、ネイティブに依存
					try {
						UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
					}
					catch (Throwable ignoreEx) {}
				
					// メインフレームの初期化
					MoquetteBrokerGUI mainFrame = new MoquetteBrokerGUI();
					mainFrame.setSize(800, 600);
					mainFrame.setLocationRelativeTo(null);
					mainFrame.setVisible(true);
				}
				catch (Throwable ex) {
					StringBuilder sb = new StringBuilder();
					sb.append("Failed to start " + APP_NAME + "!\n");
					sb.append("(cause) ");
					sb.append(ex);
					JOptionPane.showMessageDialog(null, sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
					System.exit(1);
				}
			}
		});
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
