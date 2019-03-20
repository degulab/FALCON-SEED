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
 * @(#)ProcessMonitorPane.java	1.14	2009/12/09
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ProcessMonitorPane.java	1.10	2008/12/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)ProcessMonitorPane.java	1.03	2008/08/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.console;

import java.io.File;

import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.document.IEditorDocument;
import ssac.aadl.editor.view.EditorFrame;
import ssac.aadl.editor.view.dialog.BuildOptionDialog;
import ssac.aadl.module.setting.AbstractSettings;
import ssac.aadl.module.setting.ExecSettings;
import ssac.util.logging.AppLogger;
import ssac.util.process.CommandExecutor;

/**
 * 情報ビューのコンソール・パネル
 *
 * 
 * @version 1.14	2009/12/09
 */
public class ProcessMonitorPane extends AbstractMonitorPane
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	static private final int DefaultLineLengthLimit = 512;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private IEditorDocument	targetDocument;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ProcessMonitorPane() {
		super();
		//@@@ added by Y.Ishizuka : 2008.08.04
		//--- コンソールメッセージに出力する１行の最大文字数を設定する。
		//--- この文字数を超えた場合は、自動的に改行する。
		//---(注)
		//---  １行の文字数を無限にとると、改行しない文字列が出力される
		//---  場合、JTextArea(内部的には PlainDocument)のテキスト追加
		//---  動作において、同一行への文字列追加はものすごくパフォーマンスが
		//---  悪い！なので、改行することでパフォーマンスを改善した。
		this.console.setLineLengthLimit(DefaultLineLengthLimit);
		//@@@ end of added.
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean canSetting() {
		return (targetDocument != null && targetDocument.getSettings() != null);
	}
	
	public boolean canRun() {
		if (targetDocument != null && targetDocument.isExecutable()) {
			return targetDocument.hasExecutableFile();
		}
		
		return false;
	}
	
	public IEditorDocument getTargetDocument() {
		return targetDocument;
	}
	
	public void setTargetDocument(IEditorDocument document) {
		if (isRunning()) {
			throw new RuntimeException("Already running executor!");
		}

		// set exec info
		this.targetDocument = document;
		this.executor = null;
		
		// update display
		updateTarget();
		updateButtons();
	}
	
	public ExecSettings getExecutionSettings() {
		if (targetDocument != null) {
			AbstractSettings settings = targetDocument.getSettings();
			if (settings instanceof ExecSettings) {
				return (ExecSettings)settings;
			}
		}
		return null;
	}

	/*---
	public void setExecutionSettings(ExecSettings newSettings) {
		if (isRunning()) {
			throw new RuntimeException("Already running executor!");
		}
		
		this.settings = newSettings;
		this.executor = null;
		
		// update display
		updateTarget();
		updateButtons();
	}
	---*/

	// 実行開始
	public void start() {
		try {
			// check exist target
			if (targetDocument == null) {
				throw new NullPointerException("Execution target is null!");
			}
			
			// create new executor
			//CommandExecutor newExec = ExecutorFactory.createJarExecutor(this.settings);
			CommandExecutor newExec = targetDocument.createModuleExecutor();

			// clear status
			clear();
			
			// start
			startProcess(newExec);
		}
		catch (Throwable ex) {
			File execfile = (targetDocument==null ? null : targetDocument.getExecutableFile());
			String strTarget = (execfile==null ? "Target file is nothing!" : execfile.getAbsolutePath());
			String msg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_BUILD_RUN, ex, strTarget);
			AppLogger.error(msg, ex);
			AADLEditor.showErrorMessage(this, msg);
		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------
	
	protected void onButtonSetting() {
		if (canRun() && !isRunning()) {
			AbstractSettings settings = targetDocument.getSettings();
			if (settings != null) {
				// 設定ダイアログ表示
				EditorFrame frame = (EditorFrame)AADLEditor.getApplicationMainFrame();
				BuildOptionDialog bod = new BuildOptionDialog(settings,
												frame.isReadOnlyFile(settings.getVirtualPropertyFile()),
												frame);
				bod.setSelectedExecSettings();
				bod.setVisible(true);
				int ret = bod.getDialogResult();
				AppLogger.debug("Dialog result : " + ret);
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	// ターゲットの表示更新
	protected void updateTarget() {
		File execfile = (targetDocument==null ? null : targetDocument.getExecutableFile());
		if (execfile != null) {
			lblTarget.setText(execfile.getName());
			lblTarget.setToolTipText(execfile.getAbsolutePath());
		} else {
			lblTarget.setText(" ");
			lblTarget.setToolTipText(null);
		}
		/*
		String strTarget = (this.settings != null ? this.settings.getTargetFile() : null);
		if (!Strings.isNullOrEmpty(strTarget)) {
			this.lblTarget.setText(Files.getFilename(strTarget));
			this.lblTarget.setToolTipText(strTarget);
		} else {
			this.lblTarget.setText(" ");
			this.lblTarget.setToolTipText(null);
		}
		*/
	}
}
