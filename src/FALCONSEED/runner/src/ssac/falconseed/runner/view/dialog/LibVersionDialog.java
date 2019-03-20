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
 *  Copyright 2007-2010  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)LibVersionDialog.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.runner.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.swing.Box;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ssac.aadl.common.CommonMessages;
import ssac.falconseed.runner.ModuleRunner;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.Strings;
import ssac.util.io.JarFileInfo;
import ssac.util.swing.BasicDialog;

/**
 * 使用するライブラリのバージョンを表示するダイアログ。
 * このダイアログでは、FALCONSEEDのライブラリパスに格納されている
 * jar ファイルに限定し、jar ファイルのマニフェストからバージョン情報を
 * 取得する。
 * 
 * @version 1.00	2010/12/20
 */
public class LibVersionDialog extends BasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(600, 400);

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private JTextArea taInfo;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public LibVersionDialog() {
		this(null);
	}
	
	public LibVersionDialog(Frame owner) {
		super(owner, RunnerMessages.getInstance().LibVersionDlg_Title_Main, true);
		
		// 設定
		this.setResizable(true);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200,300);
		}
		this.setMinimumSize(dmMin);
		setKeepMinimumSize(true);
		
		// restore settings
		loadSettings();
		
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
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	protected void setupMainPanel() {
		super.setupMainPanel();
		
		this.mainPanel.setLayout(new BorderLayout());
		
		// 表示エリア
		taInfo = new JTextArea();
		taInfo.setEditable(false);
		taInfo.setWrapStyleWord(false);
		taInfo.setLineWrap(false);
		
		// スクロール領域
		JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setViewportView(taInfo);
		
		// メインパネルへ設定
		this.mainPanel.add(scroll, BorderLayout.CENTER);
	}
	
	protected void setupButtonPanel() {
		int wApply, wCancel;
		int maxHeight = 0;
		Dimension dim;
		//--- setup copy to clipboard button
		this.btnApply.setText(RunnerMessages.getInstance().LibVersionDlg_Title_CopyToClip);
		dim = this.btnApply.getPreferredSize();
		wApply = dim.width;
		maxHeight = Math.max(maxHeight, dim.height);
		//--- setup close button
		this.btnCancel.setText(CommonMessages.getInstance().Button_Close);
		dim = this.btnCancel.getPreferredSize();
		wCancel = dim.width;
		maxHeight = Math.max(maxHeight, dim.height);
		//--- setup layout
		this.btnApply.setPreferredSize(new Dimension(wApply, maxHeight));
		this.btnCancel.setPreferredSize(new Dimension(wCancel, maxHeight));
		Box btnBox = Box.createHorizontalBox();
		btnBox.add(Box.createHorizontalGlue());
		btnBox.add(this.btnApply);
		btnBox.add(Box.createHorizontalStrut(10));
		btnBox.add(this.btnCancel);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(Box.createRigidArea(new Dimension(0, maxHeight+10)));
		
		// setup default button
		this.btnCancel.setDefaultCapable(true);
		
		this.getContentPane().add(btnBox, BorderLayout.SOUTH);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	@Override
	protected Point getDefaultLocation() {
		return super.getDefaultLocation();
	}

	@Override
	protected Dimension getDefaultSize() {
		//return super.getDefaultSize();
		return DM_MIN_SIZE;
	}

	@Override
	protected void onButtonApply() {
		// クリップボードへコピー
		taInfo.selectAll();
		taInfo.copy();
		taInfo.select(0, 0);
	}

	@Override
	protected void loadSettings() {
		//--- 保存情報を使用しない
		//super.loadSettings(DLG_SETTING_KEY, AppSettings.getInstance().getConfiguration());
	}

	@Override
	protected void saveSettings() {
		//--- 保存しない
		//super.saveSettings(DLG_SETTING_KEY, AppSettings.getInstance().getConfiguration());
	}

	/**
	 * ライブラリのバージョン情報を更新する。
	 */
	protected void updateVersionInfo() {
		taInfo.removeAll();
		File baseLibPath = ModuleRunner.getLibDirFile();
		parseDirectory(baseLibPath);
		taInfo.setCaretPosition(0);
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
		appendPathParamString(RunnerMessages.getInstance().LibVersionDlg_Title_Info_Path, file.getParent());
		//--- 最終更新日時
		appendTimeParamString(RunnerMessages.getInstance().LibVersionDlg_Title_Info_LastMod, file.lastModified());
		
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
		appendParamString(RunnerMessages.getInstance().LibVersionDlg_Title_Info_JarTitle, strTitle);
		//--- Version
		appendParamString(RunnerMessages.getInstance().LibVersionDlg_Title_Info_JarVersion, strVersion);
		
		// 空行を追加
		taInfo.append("\n");
	}
	
	protected void appendTitleString(String title) {
		taInfo.append(String.format("<%s>\n", title));
	}
	
	protected void appendParamString(String label, String value) {
		taInfo.append(String.format("    %s : %s\n", label, value));
	}
	
	protected void appendPathParamString(String label, String path) {
		taInfo.append(String.format("    %s : \"%s\"\n", label, path));
	}
	
	protected void appendTimeParamString(String label, long timeValue) {
		DateFormat formatter = DateFormat.getDateTimeInstance();
		String strTime = formatter.format(new Date(timeValue));
		appendParamString(label, strTime);
	}
}
