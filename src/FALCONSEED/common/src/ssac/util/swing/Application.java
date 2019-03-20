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
package ssac.util.swing;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * アプリケーションのコンテキストとなるクラス。
 * 
 * @version 1.00 2008/03/24
 */
public abstract class Application implements Runnable
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static protected Application instance = null;
	
	static protected String msgboxTitleInfo = "Information";
	static protected String msgboxTitleWarn = "Warning";
	static protected String msgboxTitleError = "Error";
	
	protected FrameWindow mainFrame;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public Application() {
		super();
	}

	//------------------------------------------------------------
	// Application context
	//------------------------------------------------------------
	
	public void run() {
		
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public FrameWindow getMainFrame() {
		return this.mainFrame;
	}

	//------------------------------------------------------------
	// Helper
	//------------------------------------------------------------
	
	static public Application getInstance() {
		return Application.instance;
	}
	
	static public FrameWindow getApplicationMainFrame() {
		if (getInstance() != null)
			return getInstance().getMainFrame();
		else
			return null;
	}
	
	static public void showMessageBox(String title, String message, int messageType)
	{
		JOptionPane.showMessageDialog(getApplicationMainFrame(), message, title, messageType);
	}
	
	static public void showMessageBox(Component parentComponent, String title,
										String message, int messageType)
	{
		JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
	}

	static public void showInfoMessage(String message) {
		showInfoMessage(getApplicationMainFrame(), message);
	}
	
	static public void showInfoMessage(Component parentComponent, String message) {
		showMessageBox(parentComponent, msgboxTitleInfo, message, JOptionPane.INFORMATION_MESSAGE);
	}
	
	static public void showInfoMessage(String format, Object...args) {
		showInfoMessage(getApplicationMainFrame(), format, args);
	}
	
	static public void showInfoMessage(Component parentComponent, String format, Object...args) {
		showMessageBox(parentComponent, msgboxTitleInfo,
				String.format(format, args), JOptionPane.INFORMATION_MESSAGE);
	}
	
	static public void showWarningMessage(String message) {
		showWarningMessage(getApplicationMainFrame(), message);
	}
	
	static public void showWarningMessage(Component parentComponent, String message) {
		showMessageBox(parentComponent, msgboxTitleWarn, message, JOptionPane.WARNING_MESSAGE);
	}
	
	static public void showWarningMessage(String format, Object...args) {
		showWarningMessage(getApplicationMainFrame(), format, args);
	}
	
	static public void showWarningMessage(Component parentComponent, String format, Object...args) {
		showMessageBox(parentComponent, msgboxTitleWarn,
				String.format(format, args), JOptionPane.WARNING_MESSAGE);
	}
	
	static public void showErrorMessage(String message) {
		showErrorMessage(getApplicationMainFrame(), message);
	}
	
	static public void showErrorMessage(Component parentComponent, String message) {
		showMessageBox(parentComponent, msgboxTitleError, message, JOptionPane.ERROR_MESSAGE);
	}
	
	static public void showErrorMessage(String format, Object...args) {
		showErrorMessage(getApplicationMainFrame(), format, args);
	}
	
	static public void showErrorMessage(Component parentComponent, String format, Object...args) {
		showMessageBox(parentComponent, msgboxTitleError,
				String.format(format, args), JOptionPane.ERROR_MESSAGE);
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
