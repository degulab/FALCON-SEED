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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)BasicDialog.java	3.2.2	2015/10/15 (Bug fixed)
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)BasicDialog.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)BasicDialog.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import ssac.aadl.common.CommonMessages;
import ssac.util.properties.ExConfiguration;

/**
 * 基本的なダイアログ・クラス
 * 
 * @version 3.2.2
 */
public abstract class BasicDialog extends AbBaseDialog implements IDialogResult
{
	private static final long serialVersionUID = 1L;

	static public final String TEXT_APPLY		= "Apply";
	static public final String TEXT_CANCEL	= "Cancel";
	static public final String TEXT_OK		= "OK";
	
	protected final JButton btnApply;
	protected final JButton btnOK;
	protected final JButton btnCancel;
	protected final JPanel  mainPanel;
	
//	private boolean flgSaveLocation = true;
//	private boolean flgSaveSize = true;
//	private boolean flgKeepMinimumSize = false;
	
	protected int dialogResult = DialogResult_None;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public BasicDialog()
		throws HeadlessException
	{
        this((Frame)null, false);
	}

	public BasicDialog(Frame owner)
		throws HeadlessException
	{
        this(owner, false);
	}

	public BasicDialog(Frame owner, boolean modal)
		throws HeadlessException
	{
        this(owner, null, modal);
	}

	public BasicDialog(Frame owner, String title)
		throws HeadlessException
	{
        this(owner, title, false);     
	}

	public BasicDialog(Dialog owner)
		throws HeadlessException
	{
        this(owner, false);
	}

	public BasicDialog(Dialog owner, boolean modal)
		throws HeadlessException
	{
        this(owner, null, modal);
	}

	public BasicDialog(Dialog owner, String title)
		throws HeadlessException
	{
        this(owner, title, false);     
	}

	public BasicDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc)
		throws HeadlessException
	{
		super(owner, title, modal, gc);
		btnApply  = new JButton(TEXT_APPLY);
		btnOK     = new JButton(TEXT_OK);
		btnCancel = new JButton(TEXT_CANCEL);
		mainPanel = new JPanel();
		initialComponent();
	}

	public BasicDialog(Dialog owner, String title, boolean modal)
		throws HeadlessException
	{
		super(owner, title, modal);
		btnApply  = new JButton(TEXT_APPLY);
		btnOK     = new JButton(TEXT_OK);
		btnCancel = new JButton(TEXT_CANCEL);
		mainPanel = new JPanel();
		initialComponent();
	}

	public BasicDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc)
	{
		super(owner, title, modal, gc);
		btnApply  = new JButton(TEXT_APPLY);
		btnOK     = new JButton(TEXT_OK);
		btnCancel = new JButton(TEXT_CANCEL);
		mainPanel = new JPanel();
		initialComponent();
	}

	public BasicDialog(Frame owner, String title, boolean modal)
		throws HeadlessException
	{
		super(owner, title, modal);
		btnApply  = new JButton(TEXT_APPLY);
		btnOK     = new JButton(TEXT_OK);
		btnCancel = new JButton(TEXT_CANCEL);
		mainPanel = new JPanel();
		initialComponent();
	}
	
	protected void initialComponent() {
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		
		// Button text
		btnApply .setText(CommonMessages.getInstance().Button_Apply);
		btnOK    .setText(CommonMessages.getInstance().Button_OK);
		btnCancel.setText(CommonMessages.getInstance().Button_Cancel);
		
		// setup Main panel
		setupMainPanel();
		
		// setup Buttons
		setupButtonPanel();
		
		// finalize
		//this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		
		// setup actions
		setupBaseActions();
	}
	
	protected void initDialog() {
	}
	
	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public int getDialogResult() {
		return dialogResult;
	}
	
	public JButton getApplyButton() {
		return this.btnApply;
	}
	
	public JButton getCancelButton() {
		return this.btnCancel;
	}
	
	public JButton getOkButton() {
		return this.btnOK;
	}
	
	public JPanel getMainPanel() {
		return mainPanel;
	}
	
	public void loadSettings(String prefix, ExConfiguration config) {
		restoreWindowStatus(config, prefix);
//		// size
//		Dimension winSize = (config == null ? null : config.getWindowSize(prefix));
//		if (winSize == null) {
//			winSize = getDefaultSize();
//			if (winSize == null) {
//				winSize = this.getPreferredSize();
//			}
//		}
//		this.setSize(winSize);
//
//		// location
//		Point winPos = (config == null ? null : config.getWindowLocation(prefix));
//		if (winPos == null) {
//			winPos = getDefaultLocation();
//		}
//		if (winPos != null) {
//			winPos = SwingTools.convertIntoDesktop(winPos, winSize);
//			this.setLocation(winPos);
//		} else {
//			this.setLocationRelativeTo(null);
//		}
	}
	
	public void saveSettings(String prefix, ExConfiguration config) {
		storeWindowStatus(config, prefix);
//		// location
//		if (isSaveLocation()) {
//			config.setWindowLocation(prefix, this.getLocationOnScreen());
//		}
//		
//		// size
//		if (isSaveSize()) {
//			Dimension defSize = getDefaultSize();
//			Dimension curSize = getSize();
//			// defSize == curSize なら、プロパティのエントリは null とする
//			if (defSize != null && defSize.equals(curSize)) {
//				curSize = null;
//			}
//			config.setWindowSize(prefix, curSize);
//		}
	}
	
	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
//	protected boolean isSaveLocation() {
//		return this.flgSaveLocation;
//	}
//	
//	protected boolean isSaveSize() {
//		return this.flgSaveSize;
//	}
//	
//	protected void setSaveLocation(boolean toAllow) {
//		this.flgSaveLocation = toAllow;
//	}
//	
//	protected void setSaveSize(boolean toAllow) {
//		this.flgSaveSize = toAllow;
//	}
//	
//	protected boolean isKeepMinimumSize() {
//		return this.flgKeepMinimumSize;
//	}
//	
//	protected void setKeepMinimumSize(boolean toAllow) {
//		this.flgKeepMinimumSize = toAllow;
//	}
	
	protected Point getDefaultLocation() {
		return null;
	}
	
//	protected Dimension getDefaultSize() {
//		return null;
//	}
	
	protected void setApplyButtonText(String text) {
		this.btnApply.setText(text);
	}
	
	protected void setOKButtonText(String text) {
		this.btnOK.setText(text);
	}
	
	protected void setCancelButtonText(String text) {
		this.btnCancel.setText(text);
	}
	
	protected void loadSettings() {
		// place holder
	}
	
	protected void saveSettings() {
		// place holder
	}
	
	protected void setupMainPanel() {
		this.getContentPane().add(this.mainPanel, BorderLayout.CENTER);
	}
	
	protected void setupButtonPanel() {
		int maxWidth = 0;
		int maxHeight = 0;
		Dimension dim;
		//---
		dim = this.btnApply.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		//---
		dim = this.btnOK.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		//---
		dim = this.btnCancel.getPreferredSize();
		maxWidth = Math.max(maxWidth, dim.width);
		maxHeight = Math.max(maxHeight, dim.height);
		//---
		dim = new Dimension(maxWidth, maxHeight);
		//btnApply .setMinimumSize(dim);
		this.btnApply .setPreferredSize(dim);
		//btnOK    .setMinimumSize(dim);
		this.btnOK    .setPreferredSize(dim);
		//btnCancel.setMinimumSize(dim);
		this.btnCancel.setPreferredSize(dim);
		
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(Box.createHorizontalGlue());
		btnBox.add(this.btnApply);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(this.btnOK);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(this.btnCancel);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(Box.createRigidArea(new Dimension(0, dim.height+10)));
		
		// setup default button
		this.btnOK.setDefaultCapable(true);
		
		this.getContentPane().add(btnBox, BorderLayout.SOUTH);
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	protected void onButtonApply() {
		
	}
	
	protected boolean onButtonOK() {
		return true;	// Close dialog
	}
	
	protected boolean onButtonCancel() {
		return true;	// Close dialog
	}
	
	protected void dialogClose(int result) {
		dialogResult = result;
		saveSettings();
		dispose();
	}
	
	//------------------------------------------------------------
	// Actions
	//------------------------------------------------------------
	
	private void setupBaseActions() {
		//--- Apply button event
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onButtonApply();
			}
		});
		
		//--- OK button event
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean canClose = onButtonOK();
				if (canClose) {
					dialogClose(DialogResult_OK);
				}
			}
		});
		
		//--- Cancel button event
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean canClose = onButtonCancel();
				if (canClose) {
					dialogClose(DialogResult_Cancel);
				}
			}
		});

		//--- Window event
		DialogWindowListener wl = new DialogWindowListener();
		this.addWindowListener(wl);
		
		//--- Component event
		DialogComponentListener cl = new DialogComponentListener();
		this.addComponentListener(cl);
	}
	
	//------------------------------------------------------------
	// Window actions
	//------------------------------------------------------------
	
	protected void onWindowOpened(WindowEvent e) {
		adjustWindowBoundsWhenOpened(e);
		initDialog();
	}
	
	protected void onWindowClosing(WindowEvent e) {
		// [Cancel]ボタンと同様の動作とする
		boolean canClose = onButtonCancel();
		if (canClose) {
			dialogClose(DialogResult_Cancel);
		}
	}
	
	protected void onWindowClosed(WindowEvent e) {}
	
	protected void onWindowIconified(WindowEvent e) {}
	
	protected void onWindowDeiconified(WindowEvent e) {}
	
	protected void onWindowActivated(WindowEvent e) {}
	
	protected void onWindowDeactivated(WindowEvent e) {}
	
	protected void onWindowStateChanged(WindowEvent e) {}
	
	protected void onWindowGainedFocus(WindowEvent e) {}
	
	protected void onWindowLostFocus(WindowEvent e) {}
	
	protected class DialogWindowListener implements WindowListener, WindowStateListener, WindowFocusListener
	{
	    public void windowOpened(WindowEvent e) { onWindowOpened(e); }

	    public void windowClosing(WindowEvent e) { onWindowClosing(e); }

	    public void windowClosed(WindowEvent e) { onWindowClosed(e); }

	    public void windowIconified(WindowEvent e) { onWindowIconified(e); }

	    public void windowDeiconified(WindowEvent e) { onWindowDeiconified(e); }

	    public void windowActivated(WindowEvent e) { onWindowActivated(e); }

	    public void windowDeactivated(WindowEvent e) { onWindowDeactivated(e); }

	    public void windowStateChanged(WindowEvent e) { onWindowStateChanged(e); }

	    public void windowGainedFocus(WindowEvent e) { onWindowGainedFocus(e); }

	    public void windowLostFocus(WindowEvent e) { onWindowLostFocus(e); }
	}
	
	//------------------------------------------------------------
	// Component actions
	//------------------------------------------------------------
	
	protected void onMoved(ComponentEvent e) {}
	
	protected void onResized(ComponentEvent e) {}
	
	protected void onShown(ComponentEvent e) {}
	
	protected void onHidden(ComponentEvent e) {}
	
	protected class DialogComponentListener implements ComponentListener {
		//--- Window moved
	    public void componentMoved(ComponentEvent e) {
	    	saveWindowLocationOnMoved(e);
	    	onMoved(e);
	    }
	    
	    //--- Window resized
	    public void componentResized(ComponentEvent e) {
	    	saveWindowSizeOnResized(e);
	    	onResized(e);
//	    	if (isKeepMinimumSize()) {
//	    		Dimension dmMin = getMinimumSize();
//	    		int cw = getSize().width;
//	    		int ch = getSize().height;
//	    		if (dmMin != null && (cw < dmMin.width || ch < dmMin.height)) {
//	    			setSize((cw<dmMin.width)?dmMin.width:cw, (ch<dmMin.height)?dmMin.height:ch);
//	    		}
//	    	}
	    }

	    //--- Window shown
	    public void componentShown(ComponentEvent e) { onShown(e); }

	    //--- Window hidden
	    public void componentHidden(ComponentEvent e) { onHidden(e); }
	}
	
	//------------------------------------------------------------
	//
	//------------------------------------------------------------

}
