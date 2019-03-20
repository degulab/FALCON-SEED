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
/*
 * @(#)CompileInDirDialog.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.plugin.source.SourceModel;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.module.setting.CompileSettings;
import ssac.aadl.module.swing.FileDialogManager;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.logging.AppLogger;
import ssac.util.process.CommandOutput;
import ssac.util.process.OutputString;
import ssac.util.swing.BasicDialog;

/**
 * フォルダに含まれる全てのAADLソースファイルをコンパイルする、
 * ユーティリティ・ダイアログ。
 * 
 * @version 1.14	2009/12/09
 * 
 * @since 1.14
 */
public class CompileInDirDialog extends BasicDialog
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(640, 480);
	
	static private final AADLFileFilter AADL_FILTER = new AADLFileFilter();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	static private File lastDir = null;

	private int		fileListCount = 0;
	private boolean	showFileList = false;
	
	private boolean	isRunning = false;
	private File		targetDir = null;
	private List<File> srcFiles = null;
	private boolean	recursiveSubDir = false;
	private Timer		drawTimer = null;
	
	private CompileProcessThread	thread = null;
	
	private JLabel		lblTargetDir;
	private JButton	btnSelectFolder;
	private JButton	btnCompile;
	private JCheckBox	chkWithoutSubdirectories;
	
	private JLabel		lblNumFiles;
	private JLabel		lblNumSuccess;
	private JLabel		lblNumError;
	private JProgressBar	progressbar;
	
	//private JTextArea	taMessage;
	private JConsolePane	taMessage;
	private JButton	btnCopyMessage;
	
	private CommandOutput	procOutput;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CompileInDirDialog() {
		this(null);
	}
	
	public CompileInDirDialog(Frame owner) {
		super(owner, EditorMessages.getInstance().CompileInDirDlg_Title_Main, true);
		
		// Setup dialog
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
	}
	
	protected void setupMainPanel() {
		super.setupMainPanel();
		
		// create components
		JPanel panel1 = createOperationPanel();
		JPanel panel2 = createProgressPanel();
		JPanel panel3 = createMessagePanel();
		
		// setup main panel layout
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		panel1.setBorder(BorderFactory.createTitledBorder(EditorMessages.getInstance().CompileInDirDlg_Label_Target));
		panel2.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(panel1, gbc);
		gbc.gridy = 1;
		mainPanel.add(panel2, gbc);
		gbc.gridy = 2;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(panel3, gbc);
		
		// setup actions
		btnSelectFolder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onSelectFolderButton();
			}
		});
		btnCompile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onCompileButton();
			}
		});
		btnCopyMessage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onCopyMessageButton();
			}
		});
	}
	
	protected void setupButtonPanel() {
		int maxWidth = 0;
		int maxHeight = 0;
		Dimension dim;
		this.btnCancel.setText(CommonMessages.getInstance().Button_Close);
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
		btnBox.add(this.btnCancel);
		btnBox.add(Box.createHorizontalStrut(5));
		btnBox.add(Box.createRigidArea(new Dimension(0, dim.height+10)));
		
		// setup default button
		this.btnCancel.setDefaultCapable(true);
		
		this.getContentPane().add(btnBox, BorderLayout.SOUTH);
	}

	@Override
	protected void initDialog() {
		super.initDialog();
		
		// setup lastDir
		if (lastDir == null) {
			String strLastPath = AppSettings.getInstance().getLastFilename(AppSettings.DOCUMENT);
			lastDir = (Strings.isNullOrEmpty(strLastPath) ? null : new File(strLastPath));
		}
		
		// initialize display
		clearResults();
		updateButtons();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private JPanel createOperationPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		// create components
		//--- caption
		JLabel caption = new JLabel(EditorMessages.getInstance().CompileInDirDlg_Label_Location);
		//--- folder path
		lblTargetDir = new JLabel("Select a directory");
		lblTargetDir.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		Dimension dm = lblTargetDir.getPreferredSize();
		lblTargetDir.setMinimumSize(dm);
		lblTargetDir.setPreferredSize(dm);
		lblTargetDir.setText("");
		//--- select button
		btnSelectFolder = new JButton(EditorMessages.getInstance().CompileInDirDlg_Button_SelectDir);
		/*
		{
			btnSelectFolder.setMargin(new Insets(2,2,2,2));
			Font font = btnSelectFolder.getFont();
			AffineTransform at = new AffineTransform();
			at.scale(0.80, 0.80);
			font = font.deriveFont(at);
			btnSelectFolder.setFont(font);
		}
		*/
		//--- Check box
		chkWithoutSubdirectories = new JCheckBox(EditorMessages.getInstance().CompileInDirDlg_Button_WithoutSubDir);
		//--- compile button
		btnCompile = new JButton(EditorMessages.getInstance().CompileInDirDlg_Button_StartCompile);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		//--- caption
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(caption, gbc);
		//--- folder path
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(lblTargetDir, gbc);
		//--- select button
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(btnSelectFolder, gbc);
		//--- Check box
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(3, 0, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(chkWithoutSubdirectories, gbc);
		//--- compile button
		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(btnCompile, gbc);
		
		//
		return panel;
	}
	
	private JPanel createProgressPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		// create components
		//--- target files
		JLabel cap1 = new JLabel(EditorMessages.getInstance().CompileInDirDlg_Label_TargetFiles);
		lblNumFiles = new JLabel("0");
		//--- success
		JLabel cap2 = new JLabel(EditorMessages.getInstance().CompileInDirDlg_Label_TargetSuccess);
		lblNumSuccess = new JLabel("0");
		//--- error
		JLabel cap3 = new JLabel(EditorMessages.getInstance().CompileInDirDlg_Label_TargetError);
		lblNumError = new JLabel("0");
		//--- progress
		progressbar = new JProgressBar(JProgressBar.HORIZONTAL);
		
		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		//--- caption
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(cap1, gbc);
		gbc.gridx = 2;
		panel.add(cap2, gbc);
		gbc.gridx = 4;
		panel.add(cap3, gbc);
		//--- number
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 1;
		panel.add(lblNumFiles, gbc);
		gbc.gridx = 3;
		panel.add(lblNumSuccess, gbc);
		gbc.gridx = 5;
		panel.add(lblNumError, gbc);
		//--- progress bar
		gbc.gridwidth = 6;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(3, 0, 0, 0);
		panel.add(progressbar, gbc);
		
		//
		return panel;
	}
	
	private JPanel createMessagePanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		// create components
		JLabel caption = new JLabel(EditorMessages.getInstance().CompileInDirDlg_Label_Message);
		btnCopyMessage = new JButton(EditorMessages.getInstance().CompileInDirDlg_Button_CopyToClip);
		//taMessage = new JTextArea();
		//taMessage.setEditable(false);
		//taMessage.setWrapStyleWord(false);
		//taMessage.setLineWrap(false);
		taMessage = new JConsolePane();
		JScrollPane sc = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		sc.setViewportView(taMessage);
		
		// Layout
		GridBagConstraints gbc = new GridBagConstraints();
		//--- caption
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		panel.add(caption, gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		panel.add(btnCopyMessage, gbc);
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.BOTH;
		panel.add(sc, gbc);
		
		//
		return panel;
	}
	
	private void setupDisplayForReady() {
		btnSelectFolder.setEnabled(true);
		chkWithoutSubdirectories.setEnabled(true);
		btnCompile.setText(EditorMessages.getInstance().CompileInDirDlg_Button_StartCompile);
		btnCompile.setEnabled(acceptTargetDir());
		btnCopyMessage.setEnabled(true);
		btnCancel.setEnabled(true);
	}
	
	private void setupDisplayForRunning() {
		btnSelectFolder.setEnabled(false);
		chkWithoutSubdirectories.setEnabled(true);
		btnCompile.setText(EditorMessages.getInstance().CompileInDirDlg_Button_StopCompile);
		btnCompile.setEnabled(true);
		btnCopyMessage.setEnabled(false);
		btnCancel.setEnabled(false);
	}
	
	private void initProcessOutputPool() {
		procOutput = new CommandOutput();
	}
	
	private void destroyProcessOutputPool() {
		displayProcessOutput();
		procOutput = null;
	}
	
	private void displayProcessOutput() {
		if (procOutput != null) {
			OutputString ostr;
			while ((ostr = procOutput.pop()) != null) {
				if (ostr.isError()) {
					//--- Error string
					//taMessage.append(ostr.getString());
					taMessage.appendErrorText(ostr.getString());
				} else {
					//--- Normal string
					//taMessage.append(ostr.getString());
					taMessage.appendNormalText(ostr.getString());
				}
			}
		}
	}
	
	private boolean acceptTargetDir() {
		return (targetDir != null && targetDir.exists() && targetDir.isDirectory());
	}
	
	private void updateButtons() {
		if (isRunning) {
			setupDisplayForRunning();
		} else {
			setupDisplayForReady();
		}
	}
	
	private void clearResults() {
		fileListCount = 0;
		lblNumFiles.setText("????");
		lblNumSuccess.setText("0");
		lblNumError.setText("0");
		progressbar.setEnabled(false);
		progressbar.setIndeterminate(false);
		progressbar.setStringPainted(false);
		progressbar.setMinimum(0);
		progressbar.setMaximum(100);
		progressbar.setValue(0);
		progressbar.setEnabled(true);
		taMessage.setText("");
	}
	
	private void startCompileProcess() {
		// initialize
		clearResults();
		recursiveSubDir = !chkWithoutSubdirectories.isSelected();
		isRunning = true;
		updateButtons();
		progressbar.setIndeterminate(true);
		srcFiles = Collections.synchronizedList(new ArrayList<File>());
		
		// start thread
		assert (drawTimer==null && thread==null);
		thread = new AADLSourceCollectThread();
		thread.start();
		
		// start timer
		drawTimer = new Timer(100, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onSwingTimer(e);
			}
		});
		drawTimer.start();
	}
	
	private void stopCompileProcess() {
		// stop timer
		if (drawTimer != null) {
			drawTimer.stop();
			drawTimer = null;
		}
		
		// terminate thread
		if (thread != null) {
			boolean isInterrupted = false;
			if (thread.isAlive()) {
				thread.cancel();
				try {
					thread.join();
				} catch (InterruptedException ex) {
					AppLogger.warn("CompileAllInFolderDialog : interrupted CompileProcessThread.", ex);
					isInterrupted = true;
				}
			}
			if (isInterrupted) {
				String msg = "\n\n" + EditorMessages.getInstance().msgSystemError + "\n";
				//taMessage.append(msg);
				taMessage.appendErrorText(msg);
			}
			else if (thread.isTerminated()) {
				String msg = "\n\n" + EditorMessages.getInstance().msgCompileTerminated + "\n";
				//taMessage.append(msg);
				taMessage.appendErrorText(msg);
			}
			thread = null;
		}

		// destroy process output pool
		destroyProcessOutputPool();
		
		// update status
		if (progressbar.isIndeterminate()) {
			progressbar.setMaximum(100);
			progressbar.setValue(0);
			progressbar.setIndeterminate(false);
		}
		isRunning = false;
		updateButtons();
	}
	
	protected void onSelectFolderButton() {
		// Select folder
		File dir = FileDialogManager.chooseDirectory(this, lastDir, EditorMessages.getInstance().CompileInDirDlg_Title_DirChooser, null);
		if (dir == null) {
			// user canceled
			return;
		}
		if (!dir.exists()) {
			String strmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_NOTFOUND, null, dir.getAbsolutePath());
			AADLEditor.showErrorMessage(this, strmsg);
			return;
		}
		if (!dir.isDirectory()) {
			String strmsg = EditorMessages.getInstance().msgNotDirectory;
			AADLEditor.showErrorMessage(this, strmsg);
			return;
		}
		
		lastDir = dir;
		targetDir = dir;
		lblTargetDir.setText(dir.getName());
		lblTargetDir.setToolTipText(dir.getAbsolutePath());
		btnCompile.setEnabled(acceptTargetDir());
	}
	
	protected void onCompileButton() {
		if (isRunning) {
			// stop compile
			btnCompile.setEnabled(false);
			if (drawTimer != null && drawTimer.isRunning()) {
				if (thread != null) {
					if (thread instanceof AADLSourceCollectThread)
						thread.cancel();
					if (thread.isAlive()) {
						thread.cancel();
						return;
					}
				}
			}
			stopCompileProcess();
		}
		else {
			// check target
			if (!acceptTargetDir()) {
				AADLEditor.showErrorMessage(this, EditorMessages.getInstance().msgNotDirectory);
				return;
			}
			
			// start compile
			startCompileProcess();
		}
	}
	
	protected void onCopyMessageButton() {
		// クリップボードへコピー
		taMessage.selectAll();
		taMessage.copy();
		taMessage.select(0, 0);
	}
	
	private void redisplayFileCount() {
		int numFiles = srcFiles.size();
		if (showFileList) {
			for (; fileListCount < numFiles; fileListCount++) {
				File f = srcFiles.get(fileListCount);
				String msg = String.format("[%d] %s\n", fileListCount, f.getAbsolutePath());
				//taMessage.append(msg);
				taMessage.appendNormalText(msg);
			}
		} else {
			fileListCount = numFiles;
		}
		lblNumFiles.setText(String.valueOf(numFiles));
	}
	
	private void redisplayCompileStatus() {
		//--- コンパイルメッセージ
		displayProcessOutput();
		//--- 進行状況
		AADLSourceCompileThread ct = (AADLSourceCompileThread)thread;
		int cntFin = ct.getFinishedCount();
		int cntSuc = ct.getSuccessCount();
		int cntErr = ct.getErrorCount();
		progressbar.setValue(cntFin);
		lblNumSuccess.setText(String.valueOf(cntSuc));
		lblNumError.setText(String.valueOf(cntErr));
	}
	
	protected void onSwingTimer(ActionEvent ae) {
		// Check process
		if (drawTimer == null) {
			return;
		}
		if (thread == null) {
			stopCompileProcess();
			return;
		}
		
		// update status
		if (thread instanceof AADLSourceCompileThread) {
			// コンパイル実行中
			redisplayCompileStatus();
			if (!thread.isAlive()) {
				//--- 念のため、情報表示更新
				redisplayCompileStatus();
				//--- 処理終了
				stopCompileProcess();
			}
		}
		else {
			// ファイル収集中
			redisplayFileCount();
			if (!thread.isAlive()) {
				//--- キャンセルなら、処理中断
				if (thread.isTerminated()) {
					stopCompileProcess();
					return;
				}
				//--- ファイル数が更新されていなければ、表示を更新
				int numFiles = srcFiles.size();
				if (numFiles <= 0) {
					//--- ファイルが存在しないので、終了
					thread = null;
					stopCompileProcess();
					return;
				} else if (fileListCount < srcFiles.size()) {
					redisplayFileCount();
				}
				//--- プロセス出力プール初期化
				initProcessOutputPool();
				//--- コンパイルスレッド開始
				thread = null;
				progressbar.setMaximum(numFiles);
				progressbar.setValue(0);
				progressbar.setStringPainted(true);
				progressbar.setIndeterminate(false);
				thread = new AADLSourceCompileThread();
				thread.start();
			}
		}
	}

	@Override
	protected boolean onButtonCancel() {
		if (isRunning) {
			JOptionPane.showMessageDialog(this, EditorMessages.getInstance().msgNowCompiling);
			return false;
		} else {
			return true;
		}
	}
	
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
	protected void loadSettings() {
		super.loadSettings(AppSettings.COMPILEALLFOLDER_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	protected void saveSettings() {
		super.saveSettings(AppSettings.COMPILEALLFOLDER_DLG, AppSettings.getInstance().getConfiguration());
		// テスト
		//Point loc = this.getLocationOnScreen();
		//Dimension dm = this.getSize();
		//AppLogger.debug("Location : " + loc.toString());
		//AppLogger.debug("Size : " + dm.toString());
		// テスト
	}
	
	static class AADLFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			if (pathname.isFile()) {
				if (Strings.endsWithIgnoreCase(pathname.getName(), ".aadl")) {
					// AADL拡張子のファイルのみ許可
					return true;
				}
			}
			else if (pathname.isDirectory()) {
				return true;
			}
			// do not accept
			return false;
		}
	}
	
	abstract class CompileProcessThread extends Thread
	{
		protected volatile boolean _isTerminated = false;
		
		public void cancel() {
			_isTerminated = true;
		}
		
		public boolean isTerminated() {
			return _isTerminated;
		}
	}
	
	class AADLSourceCollectThread extends CompileProcessThread
	{
		private void appendSourceFilesRecursive(File[] files) {
			for (File f : files) {
				if (_isTerminated)
					break;
				if (f.isDirectory()) {
					File[] subfiles = f.listFiles(AADL_FILTER);
					if (subfiles != null && subfiles.length > 0) {
						appendSourceFilesRecursive(subfiles);
					}
				} else {
					srcFiles.add(f);
				}
			}
		}
		
		@Override
		public void run() {
			// collect source files
			File[] files = targetDir.listFiles(AADL_FILTER);
			if (recursiveSubDir) {
				appendSourceFilesRecursive(files);
			} else {
				for (File f : files) {
					if (f.isFile()) {
						if (_isTerminated)
							break;
						srcFiles.add(f);
					}
				}
			}
		}
	}

	/**
	 * プロセスのストリームを読み出すハンドラ。
	 */
	static class ProcessOutputHandler extends Thread
	{
		/**
		 * 標準のバッファサイズ
		 */
		static private final int DEFAULT_BUFFER_SIZE = 8192;
		/** 標準出力を読み出すリーダー **/
		private final BufferedReader inOutput;
		/** エラー出力を読み出すリーダー **/
		private final BufferedReader inError;
		/**
		 * 出力先
		 */
		private final CommandOutput out;
		/**
		 * <code>ProcessOutputHandler</code> の新しいインスタンスを生成する。
		 * @param proc	出力を読み取るプロセス
		 * @param out	出力先
		 */
		public ProcessOutputHandler(final Process proc, final CommandOutput out) {
			super();
			this.inOutput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			this.inError  = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			this.out = out;
		}
		/**
		 * スレッドのエントリメソッド
		 */
		@Override
		public void run() {
			// setup
			boolean aliveOutputStream = true;
			boolean aliveErrorStream  = true;
			final char[] cbuf = new char[DEFAULT_BUFFER_SIZE];

			do {
				boolean needWaitForOut = false;
				boolean needWaitForErr = false;
				//--- read stdout
				if (aliveOutputStream) {
					try {
						int read = inOutput.read(cbuf);
						if (read > 0) {
							out.push(false, new String(cbuf, 0, read));
						}
						else if (read < 0) {
							aliveOutputStream = false;
						}
						else {
							needWaitForOut = true;
						}
					} catch (IOException ex) {
						ex = null;
						aliveOutputStream = false;
					}
				}
				//--- read stderr
				if (aliveErrorStream) {
					try {
						int read = inError.read(cbuf);
						if (read > 0) {
							out.push(true, new String(cbuf, 0, read));
						}
						else if (read < 0) {
							aliveErrorStream = false;
						}
						else {
							needWaitForErr = true;
						}
					} catch (IOException ex) {
						ex = null;
						aliveErrorStream = false;
					}
				}
				//--- wait
				if (needWaitForOut || needWaitForErr) {
					try {
						//out.push(true, ".....waiting.....\n");
						Thread.sleep(50);
					} catch (InterruptedException ex) {ex=null;}
				}
			} while (aliveOutputStream || aliveErrorStream);
		}
	}
	
	class AADLSourceCompileThread extends CompileProcessThread
	{
		private volatile int numFinished = 0;
		private volatile int numSuccess = 0;
		private volatile int numError = 0;
		
		public int getFinishedCount() {
			return numFinished;
		}
		
		public int getSuccessCount() {
			return numSuccess;
		}
		
		public int getErrorCount() {
			return numError;
		}
		
		protected void printOut(String msg) {
			procOutput.push(false, msg);
		}
		
		protected void printErr(String msg) {
			procOutput.push(true, msg);
		}
		
		protected void printSeparator() {
			procOutput.push(false, "\n--------------------------------------------------\n");
		}
		
		@Override
		public void run() {
			// compile all AADL sources
			CompileSettings settings = new CompileSettings();
			
			// コンパイル
			int numFiles = srcFiles.size();
			{
				String msg = "Start compile AADL source files(" + numFiles + ")\n";
				printOut(msg);
				printSeparator();
			}
			for (int i = 0; i < numFiles; i++) {
				if (_isTerminated) {
					// user canceled
					return;
				}

				// ファイルの取得
				File f = srcFiles.get(i);
				settings.loadForTarget(f);

				// ソースコンパイルプロセスの生成
				Process proc = null;
				try {
					String msg = "\n@Compile \"" + f.getAbsolutePath() + "\"\n";
					printOut(msg);
					//--- create ProcessBuilder instance
					ProcessBuilder pb = SourceModel.createCompileProcessBuilder(f, settings);
					//--- start process
					proc = pb.start();
				}
				catch (Throwable ex) {
					//--- プロセス起動失敗
					String msg = "... Failed to start AADL compile : " + ex.toString() + "\n";
					printErr(msg);
					printSeparator();
					numError = numError + 1;
					numFinished = numFinished + 1;
					continue;
				}

				// プロセスのストリームハンドラ起動
				ProcessOutputHandler hOut = new ProcessOutputHandler(proc, procOutput);
				hOut.start();

				// プロセスの完了待機
				int exitCode = 255;
				try {
					exitCode = proc.waitFor();
				}
				catch (InterruptedException ex) {
					// 割り込みがあれば、終了
					ex = null;
					Files.closeStream(proc.getInputStream());
					Files.closeStream(proc.getErrorStream());
				}
				//--- Stream を閉じる
				try { hOut.join(); } catch (InterruptedException ex) {ex=null;}
				hOut = null;
				Files.closeStream(proc.getInputStream());
				Files.closeStream(proc.getErrorStream());
				Files.closeStream(proc.getOutputStream());
				//--- エラーチェック
				if (exitCode == 0) {
					//--- 正常終了
					SourceModel.updateSettingsByCompileSucceeded(f, settings);
					String msg = "... Succeeded!\n";
					printOut(msg);
					printSeparator();
					numSuccess = numSuccess + 1;
					numFinished = numFinished + 1;
				}
				else {
					//--- 異常終了
					String msg = "... Failed to compile(" + exitCode + ")\n";
					printErr(msg);
					printSeparator();
					numError = numError + 1;
					numFinished = numFinished + 1;
				}
			}
			//--- 終了メッセージ
			{
				String msg = "\nCompile finished!\n";
				printOut(msg);
			}
		}
	}
	
	static class JConsolePane extends JTextPane
	{
		private final DefaultStyledDocument document;
		private final MutableAttributeSet attrForError;
		private boolean lineWrapped = false;

		public JConsolePane() {
			super(new DefaultStyledDocument());
			this.document = (DefaultStyledDocument)getDocument();
			setEditable(false);
			this.attrForError = new SimpleAttributeSet();
			StyleConstants.setForeground(this.attrForError, Color.RED);
		}
		
		public boolean getLineWrap() {
			return lineWrapped;
		}
		
		public void setLineWrap(boolean wrap) {
			lineWrapped = wrap;
		}

		public void clear() {
			setText("");
		}
		
		public void appendNormalText(String text) {
			try {
				document.insertString(document.getLength(), text, null);
			} catch (BadLocationException ex) {ex=null;}
		}
		
		public void appendErrorText(String text) {
			try {
				document.insertString(document.getLength(), text, attrForError);
			} catch (BadLocationException ex) {ex=null;}
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			if (!lineWrapped) {
				if (getParent() instanceof JViewport) {
					JViewport port = (JViewport)getParent();
					TextUI ui = getUI();
					int w = port.getWidth();
					Dimension sz = ui.getPreferredSize(this);
					if (sz.width < w) {
						return true;	// 実際の文字列サイズが表示幅より小さい場合は、折り返す
					}
				}
				return false;	// 折り返さない
			} else {
				return true;	// 折り返す
			}
		}
	}
}
