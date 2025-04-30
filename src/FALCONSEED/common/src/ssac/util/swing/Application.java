/*
 * @(#)Application.java	4.0.0	2021/08/23
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)Application.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * アプリケーションのコンテキストとなるクラス。
 * 
 * @version 4.0.0
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
	static protected String msgboxTitleConfirm = "Confirmation";
	
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
		//JOptionPane.showMessageDialog(getApplicationMainFrame(), message, title, messageType);
		showMessageBox(getApplicationMainFrame(), title, message, messageType);
	}
	
	static public void showMessageBox(Component parentComponent, String title,
										String message, int messageType)
	{
		if (parentComponent != null && !(parentComponent instanceof Window)) {
			Window w = SwingUtilities.getWindowAncestor(parentComponent);
			if (w != null) {
				parentComponent = w;
			}
		}
		JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
	}
	
	static public int showConfirmMessageBox(String title, String message, int optionType)
	{
		return showConfirmMessageBox(getApplicationMainFrame(), title, message, optionType);
	}
	
	static public int showConfirmMessageBox(Component parentComponent, String title, String message, int optionType)
	{
		if (parentComponent != null && !(parentComponent instanceof Window)) {
			Window w = SwingUtilities.getWindowAncestor(parentComponent);
			if (w != null) {
				parentComponent = w;
			}
		}
		return JOptionPane.showConfirmDialog(parentComponent, message, title, optionType);
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
