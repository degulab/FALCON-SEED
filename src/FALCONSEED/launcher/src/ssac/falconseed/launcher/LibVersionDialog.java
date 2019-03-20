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
 *  Copyright 2007-2012  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)LibVersionDialog.java	2.0.0	2012/11/06
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.launcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import ssac.util.Strings;
import ssac.util.io.JarFileInfo;

/**
 * 使用するライブラリのバージョンを表示するダイアログ。
 * このダイアログでは、FALCONSEEDのライブラリパスに格納されている
 * jar ファイルに限定し、jar ファイルのマニフェストからバージョン情報を
 * 取得する。
 * 
 * @version 1.00	2010/12/20
 */
public class LibVersionDialog extends JDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(600, 400);
	static private final String KEY_DIALOGCLOSE_BY_ESC = "close.by.escape";

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private boolean	_flgKeepMinimumSize = false;
	
	private JTextArea	_taInfo;
	private JButton	_btnCopyToClipboard;
	private JButton	_btnClose;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public LibVersionDialog() {
		this(null);
	}
	
	public LibVersionDialog(Frame owner) {
		super(owner, LauncherMessages.getInstance().LibVersionDlg_Title_Main, true);
		
		// ダイアログ基本設定
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		this.setResizable(true);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200,300);
		}
		this.setMinimumSize(dmMin);
		setKeepMinimumSize(true);
		
		// Setup components
		setupMainPanel();
		setupButtonPanel();
		
		// size
		Dimension winSize = getDefaultSize();
		if (winSize == null) {
			winSize = this.getPreferredSize();
		}
		this.setSize(winSize);
		
		//--- 表示位置を親ウィンドウの中央に配置
		this.setLocationRelativeTo(owner);
		
		// update info
		updateVersionInfo();
		
		// setup actions
		//--- [Copy to Clipboard] button event
		_btnCopyToClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onButtonCopyToClipboard();
			}
		});
		//--- [Close] button event
		_btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onButtonClose();
			}
		});
		//--- Window event
		DialogWindowListener wl = new DialogWindowListener();
		this.addWindowListener(wl);
		//--- Component event
		DialogComponentListener cl = new DialogComponentListener();
		this.addComponentListener(cl);
		
		setCloseByEscapeKeyEnabled(true);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * <code>[ESC]</code> キーによってウィンドウが閉じられる場合は <tt>true</tt> を返す。
	 */
	public boolean isCloseByEscapeKeyEnabled() {
		InputMap imap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		Object key = imap.get(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		return (KEY_DIALOGCLOSE_BY_ESC.equals(key));
	}

	/**
	 * <code>[ESC]</code> キーによってウィンドウを閉じるかどうかを設定する。
	 * @param toEnable	キー入力でウィンドウを閉じる場合は <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 */
	public void setCloseByEscapeKeyEnabled(boolean toEnable) {
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		if (toEnable) {
			// set [ESC] key action
			InputMap imap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			imap.put(ks, KEY_DIALOGCLOSE_BY_ESC);
			getRootPane().getActionMap().put(KEY_DIALOGCLOSE_BY_ESC, new AbstractAction(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if (isDisplayable()) {
						dispose();
					}
				}
			});
		}
		else {
			// remove [ESC] key action
			InputMap imap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			Object key = imap.get(ks);
			if (KEY_DIALOGCLOSE_BY_ESC.equals(key)) {
				imap.remove(ks);
			}
			getRootPane().getActionMap().remove(KEY_DIALOGCLOSE_BY_ESC);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void setupMainPanel() {
		// 表示エリア
		_taInfo = new JTextArea();
		_taInfo.setEditable(false);
		_taInfo.setWrapStyleWord(false);
		_taInfo.setLineWrap(false);
		
		// スクロール領域
		JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setViewportView(_taInfo);
		
		// メインパネルへ設定
		this.getContentPane().add(scroll, BorderLayout.CENTER);
	}
	
	protected void setupButtonPanel() {
		_btnCopyToClipboard = new JButton(LauncherMessages.getInstance().LibVersionDlg_Button_CopyToClip);
		_btnClose = new JButton(LauncherMessages.getInstance().LibVersionDlg_Button_Close);
		
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(Box.createHorizontalGlue());
		btnBox.add(this._btnCopyToClipboard);
		btnBox.add(Box.createHorizontalStrut(10));
		btnBox.add(this._btnClose);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(Box.createRigidArea(new Dimension(0, _btnClose.getPreferredSize().height+10)));
		
		// setup default button
		_btnClose.setDefaultCapable(true);
		
		this.getContentPane().add(btnBox, BorderLayout.SOUTH);
	}
	
	protected Point getDefaultLocation() {
		return null;
	}
	
	protected Dimension getDefaultSize() {
		return DM_MIN_SIZE;
	}
	
	protected void onButtonCopyToClipboard() {
		// クリップボードへコピー
		_taInfo.selectAll();
		_taInfo.copy();
		_taInfo.select(0, 0);
	}
	
	protected void onButtonClose() {
		if (isDisplayable()) {
			dispose();
		}
	}
	
	protected boolean isKeepMinimumSize() {
		return _flgKeepMinimumSize;
	}
	
	protected void setKeepMinimumSize(boolean toAllow) {
		_flgKeepMinimumSize = toAllow;
	}

	/**
	 * ライブラリのバージョン情報を更新する。
	 */
	protected void updateVersionInfo() {
		_taInfo.removeAll();
		File baseLibPath = LauncherMain.getAppLibDir();
		parseDirectory(baseLibPath);
		_taInfo.setCaretPosition(0);
	}
	
	protected void parseDirectory(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				parseDirectory(file);
			}
			else if (file.isFile()) {
				String fname = file.getName();
				if (Strings.endsWithIgnoreCase(fname, ".jar") || Strings.endsWithIgnoreCase(fname, ".zip")) {
					setJarFileVersionInfo(file);
				}
			}
		}
	}
	
	protected void setJarFileVersionInfo(File file) {
		// ファイル情報の設定
		//--- ファイル名
		appendTitleString(file.getName());
		//--- パス
		appendPathParamString(LauncherMessages.getInstance().LibVersionDlg_Title_Info_Path, file.getParent());
		//--- 最終更新日時
		appendTimeParamString(LauncherMessages.getInstance().LibVersionDlg_Title_Info_LastMod, file.lastModified());
		
		// マニフェスト情報の取得
		String strTitle = null;
		String strVersion = null;
		try {
			JarFileInfo jarInfo = new JarFileInfo(file);
			Manifest mani = jarInfo.getManifest();
			if (mani != null) {
				Attributes attr = mani.getMainAttributes();
				if (attr != null) {
					strVersion = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
					strTitle = attr.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
				}
				
				if (Strings.isNullOrEmpty(strVersion))
					strVersion = "undefined";
				if (Strings.isNullOrEmpty(strTitle))
					strTitle = "undefined";
			}
			else {
				strVersion = "undefined (manifest not found)";
				strTitle = strVersion;
			}
		}
		catch (IOException ex) {
			String msg = ex.getLocalizedMessage();
			if (Strings.isNullOrEmpty(msg)) {
				msg = ex.getClass().getName();
			}
			strVersion = "unknown (" + msg + ")";
			strTitle = strVersion;
		}
		
		// マニフェスト情報の設定
		//--- Title
		appendParamString(LauncherMessages.getInstance().LibVersionDlg_Title_Info_JarTitle, strTitle);
		//--- Version
		appendParamString(LauncherMessages.getInstance().LibVersionDlg_Title_Info_JarVersion, strVersion);
		
		// 空行を追加
		_taInfo.append("\n");
	}
	
	protected void appendTitleString(String title) {
		_taInfo.append(String.format("<%s>\n", title));
	}
	
	protected void appendParamString(String label, String value) {
		_taInfo.append(String.format("    %s : %s\n", label, value));
	}
	
	protected void appendPathParamString(String label, String path) {
		_taInfo.append(String.format("    %s : \"%s\"\n", label, path));
	}
	
	protected void appendTimeParamString(String label, long timeValue) {
		DateFormat formatter = DateFormat.getDateTimeInstance();
		String strTime = formatter.format(new Date(timeValue));
		appendParamString(label, strTime);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	protected class DialogWindowListener implements WindowListener, WindowStateListener, WindowFocusListener
	{
	    public void windowOpened(WindowEvent e) {}

	    public void windowClosing(WindowEvent e) {
	    	if (isDisplayable()) {
	    		dispose();
	    	}
	    }

	    public void windowClosed(WindowEvent e) {
	    	
	    }

	    public void windowIconified(WindowEvent e) {}

	    public void windowDeiconified(WindowEvent e) {}

	    public void windowActivated(WindowEvent e) {}

	    public void windowDeactivated(WindowEvent e) {}

	    public void windowStateChanged(WindowEvent e) {}

	    public void windowGainedFocus(WindowEvent e) {}

	    public void windowLostFocus(WindowEvent e) {}
	}
	
	protected class DialogComponentListener implements ComponentListener {
		//--- Window moved
	    public void componentMoved(ComponentEvent e) {}
	    
	    //--- Window resized
	    public void componentResized(ComponentEvent e) {
	    	if (isKeepMinimumSize()) {
	    		Dimension dmMin = getMinimumSize();
	    		int cw = getSize().width;
	    		int ch = getSize().height;
	    		if (dmMin != null && (cw < dmMin.width || ch < dmMin.height)) {
	    			setSize((cw<dmMin.width)?dmMin.width:cw, (ch<dmMin.height)?dmMin.height:ch);
	    		}
	    	}
	    }

	    //--- Window shown
	    public void componentShown(ComponentEvent e) {}

	    //--- Window hidden
	    public void componentHidden(ComponentEvent e) {}
	}
}
