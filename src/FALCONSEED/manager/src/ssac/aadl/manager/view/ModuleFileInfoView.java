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
 * @(#)ModuleFileInfoView.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.manager.view;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.setting.AadlJarProfile;
import ssac.aadl.module.setting.AbstractSettings;
import ssac.aadl.module.setting.PackageSettings;
import ssac.aadl.module.swing.tree.ModuleFileTreeNode;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;

/**
 * モジュールファイルの情報を表示するビュー
 * 
 * @version 1.14	2009/12/09
 * @since 1.14
 */
public class ModuleFileInfoView extends JPanel
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 表示対象のモジュールアイテム **/
	private ModuleFileTreeNode	_target;
	/** 情報表示タブ **/
	private final JTabbedPane	_tabInfo;

	/** パッケージの情報を表示するコンポーネント **/
	private final JEditorPane	_cInfoPackage;
	/** メインモジュールの情報を表示するコンポーネント **/
	private final JEditorPane	_cInfoMainModule;
	/** パッケージの情報を表示するコンポーネントのスクロール **/
	private final JScrollPane	_scForPackage;
	/** メインモジュールの情報を表示するコンポーネントのスクロール **/
	private final JScrollPane	_scForMainModule;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ModuleFileInfoView() {
		super(new BorderLayout());
		this._tabInfo = createInfoTab();
		this._cInfoPackage = createInfoEditorPane();
		this._cInfoMainModule = createInfoEditorPane();
		this._scForPackage = createInfoScrollPane();
		this._scForMainModule = createInfoScrollPane();
		setupComponents();
	}
	
	private void setupComponents() {
		// setup Package Info
		_scForPackage.setViewportView(_cInfoPackage);
		
		// setup MainModule Info
		_scForMainModule.setViewportView(_cInfoMainModule);
		
		// set tab to panel
		this.add(_tabInfo, BorderLayout.CENTER);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * 情報表示対象のパスを表すツリーノードを返す。
	 * 表示対象が設定されていない場合は <tt>null</tt> を返す。
	 */
	public ModuleFileTreeNode getTargetNode() {
		return _target;
	}

	/**
	 * 情報表示対象のパスを表すツリーノードを設定する。
	 * @param newTarget	情報を表示するツリーノードを指定する。
	 * 					表示しない場合は <tt>null</tt> を指定する。
	 */
	public void setTargetNode(ModuleFileTreeNode newTarget) {
		if (newTarget != this._target) {
			this._target = newTarget;
			refreshInfo();
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected void refreshInfo() {
		// パッケージルートではないディレクトリの場合は、何も表示しない
		if (_target == null
			|| !_target.exists()
			|| !_target.isDirectory()
			|| !_target.isModulePackageRoot())
		{
			_tabInfo.removeAll();
			return;
		}
		
		// パッケージ情報を取得
		PackageSettings packSettings = _target.getModulePackageProperties();
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		ModuleFileManager.appendDisplayFilePropertyBlock(sb, _target.getFileObject(), packSettings);
		sb.append("</body></html>");
		_cInfoPackage.setText(sb.toString());
		_cInfoPackage.setCaretPosition(0);
		if (_tabInfo.indexOfComponent(_scForPackage) < 0) {
			_tabInfo.addTab(CommonMessages.getInstance().ModuleInfoTabTitle_Package, _scForPackage);
		}
		
		// メインモジュール情報を取得
		VirtualFile vfMain = packSettings.getMainModuleFile();
		if (vfMain != null && vfMain.exists() && vfMain.isFile()) {
			sb.setLength(0);
			//--- Jar ファイル専用の解析
			if (ModuleFileManager.isJarFile(vfMain)) {
				AadlJarProfile jarprop = null;
				try {
					jarprop = new AadlJarProfile(vfMain);
				}
				catch (Throwable ex) {
					AppLogger.warn("Failed to read profile from jar : \"" + vfMain.getPath() + "\"", ex);
					jarprop = null;
				}
				if (jarprop != null) {
					sb.append("<html><body>");
					ModuleFileManager.appendDisplayJarFilePropertyBlock(sb, vfMain, jarprop);
					sb.append("</body></html>");
				}
			}
			//--- それ以外の場合は、設定情報ファイルを参照
			if (sb.length() == 0) {
				// another file types
				AbstractSettings settings = ModuleFileManager.getModuleFileSettings(vfMain);
				sb.setLength(0);
				sb.append("<html><body>");
				ModuleFileManager.appendDisplayFilePropertyBlock(sb, vfMain, settings);
				sb.append("</body></html>");
			}
			_cInfoMainModule.setText(sb.toString());
			_cInfoMainModule.setCaretPosition(0);
			if (_tabInfo.indexOfComponent(_scForMainModule) < 0) {
				_tabInfo.addTab(CommonMessages.getInstance().ModuleInfoTabTitle_MainModule, _scForMainModule);
			}
		} else {
			int index = _tabInfo.indexOfComponent(_scForMainModule);
			if (index >= 0) {
				_tabInfo.removeTabAt(index);
			}
		}
		
		if (_tabInfo.getTabCount() > 0) {
			_tabInfo.setSelectedIndex(0);
		}
	}
	
	protected JTabbedPane createInfoTab() {
		JTabbedPane tab = new JTabbedPane();
		tab.setFocusable(false);
		return tab;
	}
	
	protected JEditorPane createInfoEditorPane() {
		JEditorPane pane = new JEditorPane();
		pane.setContentType("text/html");
		pane.setEditable(false);
		return pane;
	}
	
	protected JScrollPane createInfoScrollPane() {
		JScrollPane pane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
											JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return pane;
	}
}
