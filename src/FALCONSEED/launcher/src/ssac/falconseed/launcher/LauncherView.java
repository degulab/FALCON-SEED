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
 * @(#)LauncherView.java	3.3.0	2016/05/30
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherView.java	3.1.3	2015/05/25
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherView.java	3.1.2	2014/10/20
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherView.java	2.1.0	2013/08/27
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherView.java	2.0.0	2012/11/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherView.java	1.22	2012/07/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherView.java	1.13	2012/02/16
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherView.java	1.10	2011/02/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)LauncherView.java	1.00	2010/12/20
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.launcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.JavaAppExecutor;
import ssac.aadl.common.JavaAppExecutor.ProcessWatchHandler;
import ssac.aadl.common.StartupSettings;
import ssac.falconseed.common.FSEnvironment;
import ssac.falconseed.common.FSStartupSettings;
import ssac.util.MacUtilities;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.mac.MacScreenMenuHandler;
import ssac.util.swing.JMaskedNumberSpinner;

/**
 * FALCON-SEED起動用ランチャーのメイン画面
 * 
 * @version 3.3.0	2016/05/30
 */
public class LauncherView extends JFrame implements ProcessWatchHandler, MacScreenMenuHandler
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------

	private static final long serialVersionUID = 3490565232403779958L;
	
	static protected final String APPNAME_ModuleRunner	= "Module Runner";
	static protected final String APPNAME_AADLEditor		= "AADL Editor";
	static protected final String APPNAME_PackageManager	= "Package Manager";
	static protected final String APPNAME_MQTT_Moquette	= "Moquette Broker";

	static private final String BIN_JAVA_MOQUETTE_LIB	= "moquette-broker-0.1-jar-with-dependencies.jar";
	static private final String BIN_JAVA_MOQUETTE_JAR	= "moquette-launcher.jar";
	static private final String DIR_JAVA_MOQUETTE	= "moquette";
	
	static private final String DIR_MQTTLIB_BASE	= "MQTT" + File.separatorChar + "broker";
	
	static private final String SYSPROP_ENDORSED_DIRS	= "java.endorsed.dirs";
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	private ComponentListener		_listenerComponentEvents;
	private WindowListener			_listenerWindowEvents;
	private WindowStateListener	_listenerWindowStateEvents;
	private WindowFocusListener	_listenerWindowFocusEvents;
	
	private JSpinner	spinRunner;
	private JButton	btnStartRunner;
	
	private JSpinner	spinManager;
	private JButton	btnStartManager;
	
	private JSpinner	spinEditor;
	private JButton	btnStartEditor;

	private JSpinner	spinMqttMoquette;
	private JButton btnStartMqttMoquette;
	
	private File	_binMoquetteLib;
	private File	_binMoquetteJar;
	private File	_dirMoquette;
	private boolean	_existMoquette;
	
	private JButton	btnLanguage;
	private JButton	btnAbout;
	
	private boolean	_restart = false;
	private Point		_location;
	
	private final boolean _isWindows;
	
	private String	_javaEndorsedDirsOption = null;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public LauncherView() {
		super();
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		String osName = System.getProperty("os.name");
		_isWindows = osName.startsWith("Windows");
	}
	
	public void initialComponent() {
		String title = FSEnvironment.getInstance().name();
		if (Strings.isNullOrEmpty(title)) {
			title = LauncherMain.NAME;
		}
		
		// setup broker file paths
		File fBrokerBaseDir = new File(LauncherMain.getAppLibDir(), DIR_MQTTLIB_BASE);
		_dirMoquette = new File(fBrokerBaseDir, DIR_JAVA_MOQUETTE);
		_binMoquetteLib = new File(_dirMoquette, BIN_JAVA_MOQUETTE_LIB);
		_binMoquetteJar = new File(_dirMoquette, BIN_JAVA_MOQUETTE_JAR);
		_existMoquette = (_binMoquetteLib.exists() && _binMoquetteJar.exists());
		
		// setup Java Endorsed Directories option (overwrite GDAL(QGIS) Libraries)
		_javaEndorsedDirsOption = buildJavaEndorsedDirsOption();
		
		setTitle(title);
		setupCenterPanel();
		setupSouthPanel();
		setupActions();
		
		enableComponentEvents(true);
		enableWindowEvents(true);
		
		pack();
		setResizable(false);
		
		if (_location == null) {
			setLocationRelativeTo(null);
		} else {
			setLocation(_location);
		}
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void showErrorMessage(String message) {
		LauncherMain.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public void showErrorMessage(String format, Object...args) {
		showErrorMessage(String.format(format, args));
	}

	//---------------------------------------------------------------------------------
	// Implement ssac.util.mac.MacScreenMenuHandler interfaces
	//---------------------------------------------------------------------------------
	
	public void onMacMenuAbout() {
		showAboutDialog();
	}
	
	public void onMacMenuQuit() {
		doQuit();
	}

	//---------------------------------------------------------------------------------
	// Implement ssac.falconseed.common.JAppExecutor.ProcessWatcherHandler interfaces
	//---------------------------------------------------------------------------------

	/**
	 * JAVAプロセス起動の正常起動監視スレッドの終了ハンドラ。
	 * このメソッドは、イベントディスパッチスレッドで実行される。
	 */
	public void processWatchFinished(JavaAppExecutor executor) {
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			String appName = executor.getApplicationName();
			String strin   = executor.getInputMessage();
			String strerr  = executor.getErrorMessage();
			int exitcode   = executor.getProcessExitCode();
			boolean isRunning = executor.isProcessRunning();
			boolean isSucceess = executor.isProcessSucceeded();
			
			StringBuilder sb = new StringBuilder();
			sb.append("[ Process watch finished ]\n");
			sb.append("    app name : \"");
			sb.append(appName);
			sb.append("\"\n");
			sb.append("    running : ");
			sb.append(isRunning);
			sb.append("\n");
			sb.append("    succeeded : ");
			sb.append(isSucceess);
			sb.append("\n");
			sb.append("    exit code : ");
			sb.append(exitcode);
			sb.append("\n");
			sb.append("    stdout message :\n");
			sb.append(strin);
			sb.append("\n");
			sb.append("    stderr message :\n");
			sb.append(strerr);
			sb.append("\n");
			sb.append("---------- end of process watched status -----\n");
			
			if (isSucceess) {
				System.out.println(sb.toString());
			} else {
				System.err.println(sb.toString());
			}
		}

		if (!executor.isProcessSucceeded()) {
			String strin = executor.getInputMessage();
			String strerr = executor.getErrorMessage();
			StringBuilder sb = new StringBuilder();
			sb.append("Failed to start the '");
			sb.append(executor.getApplicationName());
			sb.append("' application.");
			if ((strin != null && strin.length() > 0) || (strerr != null && strerr.length() > 0)) {
				sb.append("\n[Reason]");
				if (strin != null && strin.length() > 0) {
					sb.append("\n");
					sb.append(strin);
				}
				if (strerr != null && strerr.length() > 0) {
					sb.append("\n");
					sb.append(strerr);
				}
				sb.append("\n");
			}
			String errmsg = sb.toString();
			if (FSStartupSettings.isVerboseMode()) {
				System.err.println(errmsg);
			}
			showErrorMessage(errmsg);
		}
	}

	//------------------------------------------------------------
	// Internal interfaces
	//------------------------------------------------------------
	
	protected void showAboutDialog() {
		//--- Show Version-Info message
		JButton btn = new JButton(LauncherMessages.getInstance().LibVersionDlg_Button);
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				LibVersionDialog dlg = new LibVersionDialog(LauncherView.this);
				dlg.setVisible(true);
			}
		});
		
		String version_message = FSEnvironment.getInstance().title();
//		if (!Strings.isNullOrEmpty(version_message)) {
//			version_message = version_message + "\n - " + LauncherMain.LOCAL_VERSION;
//		} else {
//			version_message = LauncherMain.LOCAL_VERSION;
//		}
		StringBuilder sb = new StringBuilder();
		if (!Strings.isNullOrEmpty(version_message)) {
			sb.append(version_message);
			sb.append("\n - ");
			String build_message   = FSEnvironment.getInstance().buildNumber();
			if (!Strings.isNullOrEmpty(build_message)) {
				sb.append("Build ");
				sb.append(build_message);
			} else {
				sb.append(LauncherMain.LOCAL_VERSION);
			}
		} else {
			sb.append(LauncherMain.LOCAL_VERSION);
		}
		
		Object[] options = new Object[]{btn, CommonMessages.getInstance().Button_Close};
		LauncherMain.showOptionDialog(this,
				sb.toString(),
				LauncherMain.NAME,
				JOptionPane.OK_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null,
				options,
				options[1]);
		
//		//--- Show Version-Info message(simple)
//		String version_message = FSEnvironment.getInstance().title();
//		if (!Strings.isNullOrEmpty(version_message)) {
//			version_message = version_message + "\n - " + LauncherMain.LOCAL_VERSION;
//		} else {
//			version_message = LauncherMain.LOCAL_VERSION;
//		}
//
//		JOptionPane.showMessageDialog(this,
//				version_message,
//				LauncherMain.NAME,
//				JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void layoutLauncherItem(JPanel panel, int gridy, String strAppName, JSpinner cMemorySpinner, JButton cStartButton)
	{
		final String strUnit = "MB";
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridy = gridy;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		panel.add(new JLabel(strAppName), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		if (cMemorySpinner != null) {
			panel.add(cMemorySpinner, gbc);
		}
		gbc.gridx = 2;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		if (cMemorySpinner != null) {
			panel.add(new JLabel(strUnit), gbc);
		}
		gbc.insets = new Insets(5, 10, 5, 5);
		gbc.gridx = 3;
		panel.add(cStartButton, gbc);
	}
	
	private void setupCenterPanel() {
		// create panel
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
		
		final String strStart = LauncherMessages.getInstance().ButtonLabel_Start;
		
		// label
		
		// Spin
		spinRunner = createMemorySizeSpinner();
		if (FSStartupSettings.isSpecifiedAdvancedOption()) {
			spinManager = createMemorySizeSpinner();
			spinEditor = createMemorySizeSpinner();
		}
		if (_existMoquette) {
			spinMqttMoquette = createMemorySizeSpinner();
			spinMqttMoquette.setEnabled(_existMoquette);
		}
		
		// Start button
		btnStartRunner = new JButton(strStart);
		if (FSStartupSettings.isSpecifiedAdvancedOption()) {
			btnStartManager = new JButton(strStart);
			btnStartEditor = new JButton(strStart);
		}
		if (_existMoquette) {
			btnStartMqttMoquette = new JButton(strStart);
			btnStartMqttMoquette.setEnabled(_existMoquette);
		}
		
		// Layout
		int pnlIndex = 0;
		layoutLauncherItem(panel, pnlIndex++, "Module Runner", spinRunner, btnStartRunner);
		if (FSStartupSettings.isSpecifiedAdvancedOption()) {
			layoutLauncherItem(panel, pnlIndex++, "AADL Editor", spinEditor, btnStartEditor);
			layoutLauncherItem(panel, pnlIndex++, "Package Manager", spinManager, btnStartManager);
		}
		if (btnStartMqttMoquette != null) {
			layoutLauncherItem(panel, pnlIndex++, APPNAME_MQTT_Moquette, spinMqttMoquette, btnStartMqttMoquette);
		}
		
		// create button panel
		btnAbout = new JButton(LauncherMessages.getInstance().ButtonLabel_About);
		btnLanguage = new JButton("Language");	//--- 言語切り替え用のため、このリソースは固定
		Box btnpanel = new Box(BoxLayout.X_AXIS);
		btnpanel.add(Box.createHorizontalGlue());
		btnpanel.add(btnAbout);
		btnpanel.add(Box.createHorizontalStrut(10));
		btnpanel.add(btnLanguage);
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(5,5,5,5);
			gbc.gridwidth = 4;
			gbc.gridheight = 1;
			gbc.weightx = 1;
			gbc.weighty = 0;
			gbc.gridy = pnlIndex++;
			gbc.anchor = GridBagConstraints.NORTHWEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(10, 5, 5, 5);
			panel.add(btnpanel, gbc);
		}
		
		// add Main panel
		this.getContentPane().add(panel, BorderLayout.CENTER);
	}
	
	private void setupSouthPanel() {
	}

	private void setupActions() {
		//--- for Runner
		btnStartRunner.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonStartRunner(e);
			}
		});
		
		if (FSStartupSettings.isSpecifiedAdvancedOption()) {
			//--- for Editor
			btnStartEditor.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					onButtonStartEditor(e);
				}
			});
			//--- for Manager
			btnStartManager.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					onButtonStartManager(e);
				}
			});
		}
		
		//--- for MQTT Moquette
		if (btnStartMqttMoquette != null) {
			btnStartMqttMoquette.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onButtonStartMqttMoquette(e);
				}
			});
		}
		
		//--- [About] button
		btnAbout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonAbout(e);
			}
		});
		
		//--- [Language] button
		btnLanguage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onButtonLanguage(e);
			}
		});
	}
	
	private JSpinner createMemorySizeSpinner() {
		// create Spinner
		JMaskedNumberSpinner spin = new JMaskedNumberSpinner("#0", FSStartupSettings.DEF_MEMORY_SIZE, 0, FSStartupSettings.MAX_MEMORY_SIZE, 1);
		//--- setup minimum size
		Dimension dim = spin.getPreferredSize();
		if (dim.width < 80) {
			dim.width = 80;
		}
		spin.setPreferredSize(dim);
		spin.setMinimumSize(dim);
		
		return spin;
	}
	
	protected boolean canCloseWindow() {
		return true;
	}

	/*--- old codes ---
	protected boolean startProcess(List<String> commandList) {
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			System.out.println("Launch process...");
			for(int i = 0; i < commandList.size(); i++) {
				System.out.println("  [" + i + "]=" + String.valueOf(commandList.get(i)));
			}
		}
		
		// start Process
		ProcessBuilder pb = new ProcessBuilder(commandList);
		Process proc = null;
		try {
			proc = pb.start();
		}
		catch (IOException ex) {
			showErrorMessage("Failed to start new Process :\n" + ex.toString());
			//ex.printStackTrace();
			return false;
		}
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			System.out.println("Launch succeeded!");
		}
		
		// start Process stream handler
		new StreamHandler(proc.getInputStream(), System.out).start();
		new StreamHandler(proc.getErrorStream(), System.err).start();
		
		// OK
		return true;
	}
	/*--- end of old codes ---*/
	
	protected boolean executeResponsableProcess(String appName, List<String> commandList) {
		// check encoding
		String charsetName = null;
		for (String cmd : commandList) {
			if (cmd.startsWith("-Dfile.encoding=")) {
				charsetName = cmd.substring("-Dfile.encoding=".length()).trim();
				break;
			}
		}
		
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			System.out.println("Launch process \"" + appName + "\"...");
			if (charsetName != null && charsetName.length() > 0) {
				System.out.println("  charset name : " + charsetName);
			} else {
				System.out.println("  charset name : none");
			}
			for(int i = 0; i < commandList.size(); i++) {
				System.out.println("  [" + i + "]=" + String.valueOf(commandList.get(i)));
			}
		}
		
		// start Process
		ProcessBuilder pb = new ProcessBuilder(commandList);
		JavaAppExecutor executor = new JavaAppExecutor(appName, pb, charsetName, this);
		try {
			if (FSStartupSettings.isVerboseMode()) {
				executor.startProcess(System.out, System.err);
			} else {
				executor.startProcess(null, null);
			}
		}
		catch (IOException ex) {
			showErrorMessage("Failed to start new Process :\n" + ex.toString());
			return false;
		}
		
		// OK
		return true;
	}
	
	protected boolean executeSimpleProcess(String appName, List<String> commandList, boolean withProcessWatcher) {
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			System.out.println("Launch process \"" + appName + "\"...");
			for(int i = 0; i < commandList.size(); i++) {
				System.out.println("  [" + i + "]=" + String.valueOf(commandList.get(i)));
			}
		}
		
		// start Process
		ProcessBuilder pb = new ProcessBuilder(commandList);
		Process proc = null;
		try {
			proc = pb.start();
		}
		catch (IOException ex) {
			showErrorMessage("Failed to start new Process \"" + appName + "\" :\n" + ex.toString());
			//ex.printStackTrace();
			return false;
		}
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			System.out.println("Launch \"" + appName + "\" succeeded!");
		}
		
		// start Process stream handler
		new StreamHandler(proc.getInputStream(), System.out).start();
		new StreamHandler(proc.getErrorStream(), System.err).start();
		if (withProcessWatcher) {
			new SimpleProcessWatcher(appName, proc);
		}
		
		// OK
		return true;
	}
	
	static class StreamHandler extends Thread {
		private final InputStream inStream;
		private final PrintStream outStream;
		
		StreamHandler(InputStream inStream) {
			this.inStream = inStream;
			this.outStream = null;
		}
		
		StreamHandler(InputStream inStream, PrintStream outStream) {
			this.inStream = inStream;
			this.outStream = outStream;
		}
		
		public void run() {
			int c;
			if (outStream != null) {
				try {
					while ((c = inStream.read()) >= 0) {
						outStream.write(c);
					}
				}
				catch (IOException ignoreEx) {}
			} else {
				try {
					while ((c = inStream.read()) >= 0) {}
				}
				catch (IOException ignoreEx) {}
			}
		}
	}
	
	static class SimpleProcessWatcher extends Thread {
		private final Process	_proc;
		
		SimpleProcessWatcher(Process proc) {
			this(null, proc);
		}
		
		SimpleProcessWatcher(String name, Process proc) {
			super();
			if (name != null && name.length() > 0) {
				setName(name + " process watcher");
			} else {
				setName("FALCON-SEED Launched Process watcher");
			}
			_proc = proc;
		}
		
		public void run() {
			try {
				_proc.waitFor();
			} catch (InterruptedException ignoreEx) { ignoreEx=null; }
			boolean isRunning;
			try {
				_proc.exitValue();
				isRunning = false;
			}
			catch (IllegalThreadStateException ignoreEx) {
				isRunning = true;
			}
			if (isRunning) {
				// retry waiting
				try {
					_proc.waitFor();
				} catch (InterruptedException ignoreEx) { ignoreEx=null; }
			}
			
			// close stream
			Files.closeStream(_proc.getOutputStream());
			Files.closeStream(_proc.getInputStream());
			Files.closeStream(_proc.getErrorStream());
			_proc.destroy();
		}
	}

	//------------------------------------------------------------
	// Events
	//------------------------------------------------------------
	
	protected boolean changeLocale(Locale newLocale) {
		Locale orgLocale = Locale.getDefault();
		if (newLocale.equals(orgLocale)) {
			return false;		// no changes
		}
		
		// change locale
		Locale.setDefault(newLocale);
		JComponent.setDefaultLocale(newLocale);
		//--- update locale for Current components
		UIManager.getDefaults().setDefaultLocale(newLocale);
		this.setLocale(newLocale);
		
		//--- debug message
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Locale changed : \"");
			sb.append(orgLocale);
			sb.append("\" -> \"");
			sb.append(newLocale);
			sb.append("\n");
			sb.append("user.language=");
			sb.append(System.getProperty("user.language"));
			sb.append("\n");
			sb.append("Locale.getDefault()=");
			sb.append(Locale.getDefault());
			sb.append("\n");
			sb.append("UIManager.getDefaults().getDefaultLocale()=");
			sb.append(UIManager.getDefaults().getDefaultLocale());
			LauncherMain.showMessageDialog(this, sb.toString());
		}
		
		//--- restart main frame
		_restart = true;
		doQuit();
		
		return true;
	}
	
	protected void onButtonAbout(ActionEvent ae) {
		showAboutDialog();
	}
	
	protected void onButtonLanguage(ActionEvent ae) {
		String orgLang = FSStartupSettings.getInstance().getAppLanguage();
		LanguageSelectorDlg dlg = new LanguageSelectorDlg(this);
		if (!dlg.doModal(this, Locale.getDefault().getLanguage())) {
			return;
		}
		String newLang = dlg.getSelectedLanguage();
		if (Strings.isNullOrEmpty(newLang)) {
			return;
		}
		if (!newLang.equals(orgLang)) {
			FSStartupSettings.getInstance().setAppLanguage(newLang);
			FSStartupSettings.flush();
		}
		
		changeLocale(new Locale(newLang));
	}
	
	protected void onButtonStartRunner(ActionEvent ae) {
		// memory size
		int memSize = ((Integer)spinRunner.getValue()).intValue();
		
		// create Command list
		List<String> cmdList = buildCommandForRunner(memSize);
		
		// start process
		/*--- old codes ---
		if (!startProcess(cmdList)) {
			return;
		}
		/*--- end of old codes ---*/
		if (!executeResponsableProcess(APPNAME_ModuleRunner, cmdList)) {
			return;
		}
		
		// save settings
		if (memSize != FSStartupSettings.DEF_MEMORY_SIZE) {
			FSStartupSettings.getInstance().setRunnerMemorySize(memSize);
		} else {
			FSStartupSettings.getInstance().setRunnerMemorySize(-1);
		}
		FSStartupSettings.flush();
	}
	
	protected void onButtonStartEditor(ActionEvent ae) {
		// memory size
		int memSize = ((Integer)spinEditor.getValue()).intValue();
		
		// create Command list
		List<String> cmdList = buildCommandForEditor(memSize);
		
		// start process
		/*--- old codes ---
		if (!startProcess(cmdList)) {
			return;
		}
		/*--- end of old codes ---*/
		if (!executeResponsableProcess(APPNAME_AADLEditor, cmdList)) {
			return;
		}
		
		// save settings
		if (memSize != StartupSettings.DEF_MEMORY_SIZE) {
			StartupSettings.getInstance().setEditorMemorySize(memSize);
		} else {
			StartupSettings.getInstance().setEditorMemorySize(-1);
		}
		StartupSettings.flush();
	}
	
	protected void onButtonStartManager(ActionEvent ae) {
		// memory size
		int memSize = ((Integer)spinManager.getValue()).intValue();
		
		// create Command list
		List<String> cmdList = buildCommandForManager(memSize);
		
		// start process
		/*--- old codes ---
		if (!startProcess(cmdList)) {
			return;
		}
		/*--- end of old codes ---*/
		if (!executeResponsableProcess(APPNAME_PackageManager, cmdList)) {
			return;
		}
		
		// save settings
		if (memSize != StartupSettings.DEF_MEMORY_SIZE) {
			StartupSettings.getInstance().setManagerMemorySize(memSize);
		} else {
			StartupSettings.getInstance().setManagerMemorySize(-1);
		}
		StartupSettings.flush();
	}

	/**
	 * Launch broker : Moquette
	 * @since	2.1.0
	 */
	protected void onButtonStartMqttMoquette(ActionEvent ae) {
		if (!_existMoquette)
			return;	// no action
		
		// memory size
		int memSize = ((Integer)spinMqttMoquette.getValue()).intValue();
		
		// create Command list
		List<String> cmdList = buildCommandForMoquette(memSize);
		
		// save settings
		if (memSize != FSStartupSettings.DEF_MEMORY_SIZE) {
			FSStartupSettings.getInstance().setMoquetteMemorySize(memSize);
		} else {
			FSStartupSettings.getInstance().setMoquetteMemorySize(-1);
		}
		FSStartupSettings.flush();
		
		// start process
		if (!executeSimpleProcess(APPNAME_MQTT_Moquette, cmdList, true)) {
			return;
		}
	}
	
	List<String> buildCommandForRunner(int memSize) {
		ArrayList<String> cmdlist = new ArrayList<String>();
		
		// Java command
		cmdlist.add(LauncherMain.getCurrentJavaCommandPath());
		
		// class paths
		File fHome = LauncherMain.getAppHome();
		
		// MemorySize
		if (memSize > 0) {
			cmdlist.add("-Xmx" + String.valueOf(memSize) + "m");
			cmdlist.add("-D" + FSStartupSettings.SYSPROP_MEMORY_SIZE + "=" + memSize);
		}
		//--- config dir
		String confDir = FSStartupSettings.getConfigDirProperty();
		if (!Strings.isNullOrEmpty(confDir)) {
			cmdlist.add("-D" + FSStartupSettings.SYSPROP_CONFIG_DIR + "=" + confDir);
		}
		//--- logging dir
		String logDir = FSStartupSettings.getLoggingDirProperty();
		if (!Strings.isNullOrEmpty(logDir)) {
			cmdlist.add("-D" + FSStartupSettings.SYSPROP_LOGGING_DIR + "=" + logDir);
		}
		//--- java endorsed dirs
		if (_javaEndorsedDirsOption != null && _javaEndorsedDirsOption.length() > 0) {
			cmdlist.add(_javaEndorsedDirsOption);
		}
//		//--- JTree DnD option
//		cmdlist.add("-Dsun.swing.enableImprovedDragGesture");
		//--- for Apple Mac
		if (MacUtilities.isMac()) {
			//--- Set file encoding for UTF-8
			cmdlist.add("-Dsun.jnu.encoding=UTF-8");
			cmdlist.add("-Dfile.encoding=UTF-8");
			//--- Set use screen menu
			cmdlist.add(MacUtilities.vmOptionUseScreenMenuBar());
			//--- Set Dock name
			cmdlist.add(MacUtilities.vmOptionSetDockAppName(APPNAME_ModuleRunner));
			//--- Set Dock Icon
			try {
				File fIcon = new File(fHome, "lib/resources/icon/ModuleRunner.icns");
				if (fIcon.exists() && fIcon.isFile()) {
					cmdlist.add(MacUtilities.vmOptionSetDockIcon(fIcon.getAbsolutePath()));
				}
			} catch (Throwable ignoreEx) {}
		}
		
		// ModuleRunner.jar
		cmdlist.add("-jar");
		File coreJar = new File(new File(fHome, "lib"), "ModuleRunner.jar");
		cmdlist.add(coreJar.getAbsolutePath());
		
		// Language option
		if (!FSStartupSettings.isDefaultLocaleWhenStartup(Locale.getDefault())) {
			cmdlist.add(FSStartupSettings.LANGUAGE_OPTION);
			cmdlist.add(Locale.getDefault().getLanguage());
		}
		
		// Debug option
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			cmdlist.add(FSStartupSettings.DEBUG_OPTION);
		}
		
		// Verbose option
		if (FSStartupSettings.isSpecifiedVerboseOption()) {
			cmdlist.add(FSStartupSettings.VERBOSE_OPTION);
		}
		
		return cmdlist;
	}
	
	List<String> buildCommandForManager(int memSize) {
		ArrayList<String> cmdlist = new ArrayList<String>();
		
		// Java command
		cmdlist.add(LauncherMain.getCurrentJavaCommandPath());
		
		// class paths
		File fHome = LauncherMain.getAppHome();
		
		// MemorySize
		if (memSize > 0) {
			cmdlist.add("-Xmx" + String.valueOf(memSize) + "m");
			cmdlist.add("-D" + StartupSettings.SYSPROP_MEMORY_SIZE + "=" + memSize);
		}
		//--- config dir
		String confDir = StartupSettings.getConfigDirProperty();
		if (!Strings.isNullOrEmpty(confDir)) {
			cmdlist.add("-D" + StartupSettings.SYSPROP_CONFIG_DIR + "=" + confDir);
		}
		//--- logging dir
		String logDir = StartupSettings.getLoggingDirProperty();
		if (!Strings.isNullOrEmpty(logDir)) {
			cmdlist.add("-D" + StartupSettings.SYSPROP_LOGGING_DIR + "=" + logDir);
		}
		//--- java endorsed dirs
		if (_javaEndorsedDirsOption != null && _javaEndorsedDirsOption.length() > 0) {
			cmdlist.add(_javaEndorsedDirsOption);
		}
//		//--- JTree DnD option
//		cmdlist.add("-Dsun.swing.enableImprovedDragGesture");
		//--- for Apple Mac
		if (MacUtilities.isMac()) {
			//--- Set file encoding for UTF-8
			cmdlist.add("-Dsun.jnu.encoding=UTF-8");
			cmdlist.add("-Dfile.encoding=UTF-8");
			//--- Set use screen menu
			cmdlist.add(MacUtilities.vmOptionUseScreenMenuBar());
			//--- Set Dock name
			cmdlist.add(MacUtilities.vmOptionSetDockAppName(APPNAME_PackageManager));
			//--- Set Dock Icon
			try {
				File fIcon = new File(fHome, "lib/resources/icon/AE_Manager.icns");
				if (fIcon.exists() && fIcon.isFile()) {
					cmdlist.add(MacUtilities.vmOptionSetDockIcon(fIcon.getAbsolutePath()));
				}
			} catch (Throwable ignoreEx) {}
		}
		
		// PackageManager.jar
		cmdlist.add("-jar");
		File coreJar = new File(new File(fHome, "lib"), "PackageManager.jar");
		cmdlist.add(coreJar.getAbsolutePath());
		
		// Language option
		if (!FSStartupSettings.isDefaultLocaleWhenStartup(Locale.getDefault())) {
			cmdlist.add(StartupSettings.LANGUAGE_OPTION);
			cmdlist.add(Locale.getDefault().getLanguage());
		}
		
		// Debug option
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			cmdlist.add(StartupSettings.DEBUG_OPTION);
		}
		
		// Verbose option
		if (FSStartupSettings.isSpecifiedVerboseOption()) {
			cmdlist.add(StartupSettings.VERBOSE_OPTION);
		}
		
		return cmdlist;
	}
	
	List<String> buildCommandForEditor(int memSize) {
		ArrayList<String> cmdlist = new ArrayList<String>();
		
		// Java command
		cmdlist.add(LauncherMain.getCurrentJavaCommandPath());
		
		// class paths
		File fHome = LauncherMain.getAppHome();
		
		// MemorySize
		if (memSize > 0) {
			cmdlist.add("-Xmx" + String.valueOf(memSize) + "m");
			cmdlist.add("-D" + StartupSettings.SYSPROP_MEMORY_SIZE + "=" + memSize);
		}
		//--- config dir
		String confDir = StartupSettings.getConfigDirProperty();
		if (!Strings.isNullOrEmpty(confDir)) {
			cmdlist.add("-D" + StartupSettings.SYSPROP_CONFIG_DIR + "=" + confDir);
		}
		//--- logging dir
		String logDir = StartupSettings.getLoggingDirProperty();
		if (!Strings.isNullOrEmpty(logDir)) {
			cmdlist.add("-D" + StartupSettings.SYSPROP_LOGGING_DIR + "=" + logDir);
		}
		//--- java endorsed dirs
		if (_javaEndorsedDirsOption != null && _javaEndorsedDirsOption.length() > 0) {
			cmdlist.add(_javaEndorsedDirsOption);
		}
//		//--- JTree DnD option
//		cmdlist.add("-Dsun.swing.enableImprovedDragGesture");
		//--- for Apple Mac
		if (MacUtilities.isMac()) {
			//--- Set file encoding for UTF-8
			cmdlist.add("-Dsun.jnu.encoding=UTF-8");
			cmdlist.add("-Dfile.encoding=UTF-8");
			//--- Set use screen menu
			cmdlist.add(MacUtilities.vmOptionUseScreenMenuBar());
			//--- Set Dock name
			cmdlist.add(MacUtilities.vmOptionSetDockAppName(APPNAME_AADLEditor));
			//--- Set Dock Icon
			try {
				File fIcon = new File(fHome, "lib/resources/icon/AE_Editor.icns");
				if (fIcon.exists() && fIcon.isFile()) {
					cmdlist.add(MacUtilities.vmOptionSetDockIcon(fIcon.getAbsolutePath()));
				}
			} catch (Throwable ignoreEx) {}
		}
		
		// AADLEditor core jar
		cmdlist.add("-jar");
		File coreJar = new File(new File(fHome, "lib"), "EditorCore.jar");
		cmdlist.add(coreJar.getAbsolutePath());
		
		// Language option
		if (!FSStartupSettings.isDefaultLocaleWhenStartup(Locale.getDefault())) {
			cmdlist.add(StartupSettings.LANGUAGE_OPTION);
			cmdlist.add(Locale.getDefault().getLanguage());
		}
		
		// Debug option
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			cmdlist.add(StartupSettings.DEBUG_OPTION);
		}
		
		// Verbose option
		if (FSStartupSettings.isSpecifiedVerboseOption()) {
			cmdlist.add(StartupSettings.VERBOSE_OPTION);
		}
		
		return cmdlist;
	}

	/**
	 * Launch Moquette Broker GUI
	 * @since 2.1.0
	 */
	List<String> buildCommandForMoquette(int memSize) {
		ArrayList<String> cmdlist = new ArrayList<String>();
		
		// Java command
		cmdlist.add(LauncherMain.getCurrentJavaCommandPath());
		
		// class paths
		
		// MemorySize
		if (memSize > 0) {
			cmdlist.add("-Xmx" + String.valueOf(memSize) + "m");
			cmdlist.add("-D" + FSStartupSettings.SYSPROP_MEMORY_SIZE + "=" + memSize);
		}
		//--- config dir
		String confDir = FSStartupSettings.getConfigDirProperty();
		if (!Strings.isNullOrEmpty(confDir)) {
			cmdlist.add("-D" + FSStartupSettings.SYSPROP_CONFIG_DIR + "=" + confDir);
		}
		//--- logging dir
		String logDir = FSStartupSettings.getLoggingDirProperty();
		if (!Strings.isNullOrEmpty(logDir)) {
			cmdlist.add("-D" + FSStartupSettings.SYSPROP_LOGGING_DIR + "=" + logDir);
		}
		//--- java endorsed dirs
		if (_javaEndorsedDirsOption != null && _javaEndorsedDirsOption.length() > 0) {
			cmdlist.add(_javaEndorsedDirsOption);
		}
//		//--- JTree DnD option
//		cmdlist.add("-Dsun.swing.enableImprovedDragGesture");
		//--- for Apple Mac
		if (MacUtilities.isMac()) {
			//--- Set file encoding for UTF-8
			cmdlist.add("-Dsun.jnu.encoding=UTF-8");
			cmdlist.add("-Dfile.encoding=UTF-8");
			//--- Set use screen menu
			cmdlist.add(MacUtilities.vmOptionUseScreenMenuBar());
			//--- Set Dock name
			cmdlist.add(MacUtilities.vmOptionSetDockAppName(APPNAME_MQTT_Moquette));
//			//--- Set Dock Icon
//			try {
//				File fIcon = new File(fHome, "lib/resources/icon/ModuleRunner.icns");
//				if (fIcon.exists() && fIcon.isFile()) {
//					cmdlist.add(MacUtilities.vmOptionSetDockIcon(fIcon.getAbsolutePath()));
//				}
//			} catch (Throwable ignoreEx) {}
		}
		
		// MoquetteLauncher.jar
		cmdlist.add("-jar");
		cmdlist.add(_binMoquetteJar.getAbsolutePath());
		
		// Language option
		if (!FSStartupSettings.isDefaultLocaleWhenStartup(Locale.getDefault())) {
			cmdlist.add(FSStartupSettings.LANGUAGE_OPTION);
			cmdlist.add(Locale.getDefault().getLanguage());
		}
		
		// Debug option
		if (FSStartupSettings.isSpecifiedDebugOption()) {
			cmdlist.add(FSStartupSettings.DEBUG_OPTION);
		}
		
		// Verbose option
		if (FSStartupSettings.isSpecifiedVerboseOption()) {
			cmdlist.add(FSStartupSettings.VERBOSE_OPTION);
		}
		
		return cmdlist;
	}

	/**
	 * &quot;java.endorsed.dirs&quot; に、&quot;lib/modules&quot; ディレクトリを
	 * 追加した、新しいプロパティを指定する文字列を生成する。
	 * <p><b>注意：</b>
	 * <blockquote>
	 * これは、QGIS(正確には、QGIS の必須ライブラリである GDAL)がインストール時に、
	 * <code>commons-logging-1.1.3.jar</code> を、Java Extensions ディレクトリに
	 * 格納してしまい、この Extensions ディレクトリにライブラリが配置されると、Log4J 用 Logger
	 * 実装クラスがロードできない問題に対処するためのものである。
	 * </blockquote>
	 * @return	生成されたプロパティ指定を示す文字列
	 * @since 2014/10/17(modified:2016/05/30)
	 */
	static private String buildJavaEndorsedDirsOption() {
		StringBuilder sb = new StringBuilder();
		sb.append("-D");
		sb.append(SYSPROP_ENDORSED_DIRS);
		sb.append("=");
		
		File fModuleLibDir = new File(LauncherMain.getAppLibDir(), "modules");
		LinkedHashSet<File> pathset = new LinkedHashSet<File>();
		pathset.add(fModuleLibDir);
		//--- append old endorsed dirs
		String oldEndorsedDirs = System.getProperty(SYSPROP_ENDORSED_DIRS);
		if (oldEndorsedDirs != null && oldEndorsedDirs.length() > 0) {
			String[] paths = oldEndorsedDirs.split("\\" + File.pathSeparatorChar);
			if (paths != null) {
				for (String path : paths) {
					if (path != null && path.length() > 0) {
						pathset.add(new File(path));
					}
				}
			}
		}
		//--- make property string
		Iterator<File> it = pathset.iterator();
		if (it.hasNext()) {
			sb.append(it.next().getPath());
			for (; it.hasNext(); ) {
				sb.append(File.pathSeparatorChar);
				sb.append(it.next().getPath());
			}
		}
		
		return sb.toString();
	}
	
	//************************************************************
	// Component events
	//************************************************************
	
	protected void onWindowResized(ComponentEvent e) {}
	
	protected void onWindowMoved(ComponentEvent e) {
		int state = getExtendedState();
		if (state == JFrame.NORMAL) {
			if (_location == null) {
				_location = getLocation();
			} else {
				getLocation(_location);
			}
		}
	}
	
	protected void onWindowShown(ComponentEvent e) {}
	
	protected void onWindowHidden(ComponentEvent e) {}
	
	//************************************************************
	// Window events
	//************************************************************
	
	protected void doQuit() {
		if (canCloseWindow()) {
			this.dispose();
		}
	}
	
	protected void onWindowOpened(WindowEvent e) {
		// 設定値を反映
		//--- for Runner
		if (FSStartupSettings.getInstance().isSpecifiedRunnerMemorySize()) {
			spinRunner.setValue(FSStartupSettings.getInstance().getRunnerMemorySize());
		} else {
			spinRunner.setValue(FSStartupSettings.DEF_MEMORY_SIZE);
		}
		if (FSStartupSettings.isSpecifiedAdvancedOption()) {
			//--- for Manager
			if (StartupSettings.getInstance().isSpecifiedManagerMemorySize()) {
				spinManager.setValue(StartupSettings.getInstance().getManagerMemorySize());
			} else {
				spinManager.setValue(StartupSettings.DEF_MEMORY_SIZE);
			}
			//--- for Editor
			if (StartupSettings.getInstance().isSpecifiedEditorMemorySize()) {
				spinEditor.setValue(StartupSettings.getInstance().getEditorMemorySize());
			} else {
				spinEditor.setValue(StartupSettings.DEF_MEMORY_SIZE);
			}
		}
		//--- for Moquette
		if (spinMqttMoquette != null) {
			if (FSStartupSettings.getInstance().isSpecifiedMoquetteMemorySize()) {
				spinMqttMoquette.setValue(FSStartupSettings.getInstance().getMoquetteMemorySize());
			} else {
				spinMqttMoquette.setValue(FSStartupSettings.DEF_MEMORY_SIZE);
			}
		}
	}
	
	protected void onWindowIconified(WindowEvent e) {}
	
	protected void onWindowDeiconified(WindowEvent e) {}
	
	protected void onWindowActivated(WindowEvent e) {}
	
	protected void onWindowDeactivated(WindowEvent e) {}
	
	//************************************************************
	// Window state events
	//************************************************************
	
	protected void onWindowStateChanged(WindowEvent e) {}
	
	//************************************************************
	// Window focus events
	//************************************************************
	
	protected void onWindowGainedFocus(WindowEvent e) {}
	
	protected void onWindowLostFocus(WindowEvent e) {}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected boolean isComponentEventsEnabled() {
		return (_listenerComponentEvents != null);
	}
	
	protected void enableComponentEvents(boolean toEnable) {
		if (!isComponentEventsEnabled() && toEnable) {
			_listenerComponentEvents = new ComponentListener() {
				public void componentResized(ComponentEvent e) {
					onWindowResized(e);
				}
				public void componentMoved(ComponentEvent e) {
					onWindowMoved(e);
				}
				public void componentShown(ComponentEvent e) {
					onWindowShown(e);
				}
				public void componentHidden(ComponentEvent e) {
					onWindowHidden(e);
				}
			};
			addComponentListener(_listenerComponentEvents);
		}
		else if (isComponentEventsEnabled() && !toEnable) {
			removeComponentListener(_listenerComponentEvents);
			_listenerComponentEvents = null;
		}
	}
	
	protected boolean isWindowEventsEnabled() {
		return (_listenerWindowEvents != null);
	}

	protected void enableWindowEvents(boolean toEnable) {
		if (!isWindowEventsEnabled() && toEnable) {
			_listenerWindowEvents = new WindowListener() {
				public void windowOpened(WindowEvent e) {
					onWindowOpened(e);
				}
				public void windowClosing(WindowEvent e) {
					doQuit();
				}
				public void windowClosed(WindowEvent e) {
					if (_restart) {
						SwingUtilities.invokeLater(new Runnable(){
							public void run() {
								// メッセージリソースの再読み込み
								CommonMessages.updateInstance();
								LauncherMessages.updateInstance();
								
								try {
									// メインフレームの初期化
									Image imgIcon = LauncherMain.getAppIconImage();
									LauncherView mainFrame = new LauncherView();
									mainFrame._location = LauncherView.this._location;
									if (imgIcon != null) {
										mainFrame.setIconImage(imgIcon);
									}
									mainFrame.initialComponent();
									if (MacUtilities.isMac()) {
										MacUtilities.setupScreenMenuHandler(mainFrame);
									}
								
									// メインフレームの表示
									mainFrame.setVisible(true);
								}
								catch (Throwable ex) {
									String errmsg = LauncherMain.NAME + " application restarting error!";
									LauncherMain.printLauncherError(ex, errmsg);
									LauncherMain.showLauncherError(ex, errmsg);
									System.exit(4);
								}
							}
						});
					} else {
						System.exit(0);
					}
				}
				public void windowIconified(WindowEvent e) {
					onWindowIconified(e);
				}
				public void windowDeiconified(WindowEvent e) {
					onWindowDeiconified(e);
				}
				public void windowActivated(WindowEvent e) {
					onWindowActivated(e);
				}
				public void windowDeactivated(WindowEvent e) {
					onWindowDeactivated(e);
				}
			};
			addWindowListener(_listenerWindowEvents);
		}
		else if (isWindowEventsEnabled() && !toEnable) {
			removeWindowListener(_listenerWindowEvents);
			_listenerWindowEvents = null;
		}
	}

	protected boolean isWindowStateEventsEnabled() {
		return (_listenerWindowStateEvents != null);
	}

	protected void enableWindowStateEvents(boolean toEnable) {
		if (!isWindowStateEventsEnabled() && toEnable) {
			_listenerWindowStateEvents = new WindowStateListener() {
				public void windowStateChanged(WindowEvent e) {
			    	onWindowStateChanged(e);
			    }
			};
			addWindowStateListener(_listenerWindowStateEvents);
		}
		else if (isWindowStateEventsEnabled() && !toEnable) {
			removeWindowStateListener(_listenerWindowStateEvents);
			_listenerWindowStateEvents = null;
		}
	}

	protected boolean isWindowFocusEventsEnabled() {
		return (_listenerWindowFocusEvents != null);
	}

	protected void enableWindowFocusEvents(boolean toEnable) {
		if (!isWindowFocusEventsEnabled() && toEnable) {
			_listenerWindowFocusEvents = new WindowFocusListener() {
				public void windowGainedFocus(WindowEvent e) {
					onWindowGainedFocus(e);
				}
				public void windowLostFocus(WindowEvent e) {
					onWindowLostFocus(e);
				}
			};
			addWindowFocusListener(_listenerWindowFocusEvents);
		}
		else if (isWindowFocusEventsEnabled() && !toEnable) {
			removeWindowFocusListener(_listenerWindowFocusEvents);
			_listenerWindowFocusEvents = null;
		}
	}
}
