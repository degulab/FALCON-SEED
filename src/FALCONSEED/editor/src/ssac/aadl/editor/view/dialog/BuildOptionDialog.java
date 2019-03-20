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
 * @(#)BuildOptionDialog.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)BuildOptionDialog.java	1.10	2008/12/03
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)BuildOptionDialog.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.common.CommonResources;
import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.setting.AppSettings;
import ssac.aadl.editor.view.EditorFrame;
import ssac.aadl.module.setting.AbstractSettings;
import ssac.aadl.module.setting.ClassPathSettings;
import ssac.aadl.module.setting.CompileSettings;
import ssac.aadl.module.setting.ExecSettings;
import ssac.aadl.module.setting.MacroExecSettings;
import ssac.aadl.module.setting.ModuleSettings;
import ssac.aadl.module.setting.PackageSettings;
import ssac.util.logging.AppLogger;
import ssac.util.swing.BasicDialog;

/**
 * ビルドオプションダイアログ
 * 
 * @version 1.14	2009/12/09
 */
public class BuildOptionDialog extends BasicDialog
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final Dimension DM_MIN_SIZE = new Dimension(500, 530);
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final AbstractSettings settings;
	private final boolean readOnly;
	
	private JTabbedPane		tab;
	
	private CompileOptionPane		paneCompile;
	private ClassPathPane			paneClasspath;
	private ExecuteOptionPane		paneExecute;
	private ModuleOptionPane		paneModule;
	private PackageOptionPane		panePackage;
	private MacroExecOptionPane	paneMacroExec;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public BuildOptionDialog(AbstractSettings settings, boolean readOnly) {
		this(settings, readOnly, null);
	}
	
	public BuildOptionDialog(AbstractSettings settings, boolean readOnly, Frame owner) {
		super(owner, EditorMessages.getInstance().BuildOptionDlg_Title_Main, true);
		
		// Check params
		if (settings == null)
			throw new NullPointerException("settings");
		if (!(settings instanceof ModuleSettings) && !(settings instanceof PackageSettings))
			throw new IllegalArgumentException("AbstractSettings instance is not assignable ClassPathSettings.");
		this.settings = settings;
		this.readOnly = readOnly;
		
		// initialize
		initialLocalComponent();
		
		// restore settings
		loadSettings();
	}

	protected void initialLocalComponent() {
		// タイトル
		if (readOnly) {
			setTitle(getTitle() + " " + CommonMessages.getInstance().Message_ReadOnly);
		}
		
		// タブ
		tab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		if (this.settings instanceof PackageSettings) {
			panePackage = new PackageOptionPane((PackageSettings)this.settings, readOnly);
			tab.addTab(panePackage.getTitle(), panePackage);
		}
		if (this.settings instanceof ModuleSettings) {
			paneModule = new ModuleOptionPane((ModuleSettings)this.settings, readOnly);
			tab.addTab(paneModule.getTitle(), paneModule);
		}
		if (this.settings instanceof CompileSettings) {
			paneCompile = new CompileOptionPane((CompileSettings)this.settings, readOnly);
			tab.addTab(paneCompile.getTitle(), paneCompile);
		}
		if (this.settings instanceof ClassPathSettings) {
			paneClasspath = new ClassPathPane((ClassPathSettings)this.settings, readOnly);
			tab.addTab(paneClasspath.getTitle(), paneClasspath);
		}
		if (this.settings instanceof ExecSettings) {
			paneExecute = new ExecuteOptionPane((ExecSettings)this.settings, readOnly);
			tab.addTab(paneExecute.getTitle(), paneExecute);
		}
		if (this.settings instanceof MacroExecSettings) {
			paneMacroExec = new MacroExecOptionPane((MacroExecSettings)this.settings, readOnly);
			tab.addTab(paneMacroExec.getTitle(), paneMacroExec);
		}
		
		// setup main panel layout
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(CommonResources.DIALOG_CONTENT_BORDER);
		mainPanel.add(tab, BorderLayout.CENTER);
		
		// activate tab
		Component activecmp = null;
		if (this.settings instanceof PackageSettings) {
			activecmp = panePackage;
		}
		else if (this.settings instanceof ModuleSettings) {
			activecmp = paneModule;
		}
		else if (this.settings instanceof CompileSettings) {
			activecmp = paneCompile;
		}
		else if (this.settings instanceof ExecSettings) {
			activecmp = paneExecute;
		}
		else if (this.settings instanceof ClassPathSettings) {
			activecmp = paneClasspath;
		}
		else if (this.settings instanceof MacroExecSettings) {
			activecmp = paneMacroExec;
		}
		if (activecmp != null) {
			tab.setSelectedComponent(activecmp);
		}
		
		// setup actions
		tab.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				onTabSelectionChanged(e);
			}
		});
		
		// setup Dialog style
		this.setResizable(true);
		//--- setup minimum size
		Dimension dmMin = getDefaultSize();
		if (dmMin == null) {
			dmMin = new Dimension(200,300);
		}
		this.setMinimumSize(dmMin);
		setKeepMinimumSize(true);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public void setSelectedModuleSettings() {
		if (paneModule != null) {
			tab.setSelectedComponent(paneModule);
		}
	}
	
	public void setSelectedCompileSettings() {
		if (paneCompile != null) {
			tab.setSelectedComponent(paneCompile);
		}
	}
	
	public void setSelectedExecSettings() {
		if (paneExecute != null) {
			tab.setSelectedComponent(paneExecute);
		}
	}
	
	public void setSelectedMacroExecSettings() {
		if (paneMacroExec != null) {
			tab.setSelectedComponent(paneMacroExec);
		}
	}
	
	/**
	 * このダイアログの親となるエディタフレームを取得する。
	 * @return	このダイアログの親となるエディタフレームのインスタンス。
	 * 			このダイアログの親がエディタフレームではない場合は <tt>null</tt> を返す。
	 */
	public EditorFrame getParentFrame() {
		Window parentFrame = SwingUtilities.windowForComponent(this);
		if (parentFrame instanceof EditorFrame)
			return (EditorFrame)parentFrame;
		else
			return null;
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	protected void onButtonApply() {
		if (applySettings()) {
			EditorFrame frame = getParentFrame();
			if (frame != null) {
				frame.refreshModuleProperties();
			}
		}
	}

	@Override
	protected boolean onButtonOK() {
		boolean succeeded = applySettings();
		if (succeeded) {
			EditorFrame frame = getParentFrame();
			if (frame != null) {
				frame.refreshModuleProperties();
			}
		}
		return succeeded;
	}
	
	@Override
	protected boolean onButtonCancel() {
		return true;	// Close dialog
	}
	
	@Override
	protected void dialogClose(int result) {
		super.dialogClose(result);
	}
	
	protected void onTabSelectionChanged(ChangeEvent event) {
		Component comp = tab.getSelectedComponent();
		if (comp == paneExecute) {
			paneExecute.updateProgramArgumentDetails(paneModule.getArgumentDetails());
		}
		else if (comp == paneMacroExec) {
			paneMacroExec.updateProgramArgumentDetails(paneModule.getArgumentDetails());
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	private boolean applySettings() {
		// store properties from GUI
		if (this.panePackage != null) {
			this.panePackage.storeOptionSettings();
		}
		if (this.paneModule != null) {
			this.paneModule.storeOptionSettings();
		}
		if (this.paneClasspath != null) {
			this.paneClasspath.storeOptionSettings();
		}
		if (this.paneExecute != null) {
			this.paneExecute.storeOptionSettings();
		}
		if (this.paneCompile != null) {
			this.paneCompile.storeOptionSettings();
		}
		if (this.paneMacroExec != null) {
			this.paneMacroExec.storeOptionSettings();
		}
		
		// commit settings
		boolean succeeded;
		try {
			this.settings.commit();
			succeeded = true;
		}
		catch (Throwable ex) {
			AppLogger.error(ex);
			String strpath = (this.settings.getVirtualPropertyFile() != null ? this.settings.getVirtualPropertyFile().getAbsolutePath() : null);
			String strmsg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_FILE_SETTING_WRITE, ex, strpath);
			AADLEditor.showErrorMessage(this, strmsg);
			succeeded = false;
		}
		return succeeded;
	}

	@Override
	protected void initDialog() {
		// 標準の初期化
		super.initDialog();
		
		// 各ペインの表示直後の初期化
		if (this.panePackage != null) {
			this.panePackage.onWindowOpened(this);
		}
		if (this.paneModule != null) {
			this.paneModule.onWindowOpened(this);
		}
		if (this.paneClasspath != null) {
			this.paneClasspath.onWindowOpened(this);
		}
		if (this.paneExecute != null) {
			this.paneExecute.onWindowOpened(this);
		}
		if (this.paneCompile != null) {
			this.paneCompile.onWindowOpened(this);
		}
		if (this.paneMacroExec != null) {
			this.paneMacroExec.onWindowOpened(this);
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
		super.loadSettings(AppSettings.BUILDOPTION_DLG, AppSettings.getInstance().getConfiguration());
	}

	@Override
	protected void saveSettings() {
		super.saveSettings(AppSettings.BUILDOPTION_DLG, AppSettings.getInstance().getConfiguration());
//		// テスト
//		Point loc = this.getLocationOnScreen();
//		Dimension dm = this.getSize();
//		AppLogger.debug("Location : " + loc.toString());
//		AppLogger.debug("Size : " + dm.toString());
//		// テスト
	}
}
