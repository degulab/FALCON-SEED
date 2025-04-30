/*
 * @(#)CompileMonitorPane.java	4.0.0	2021/08/28 : for Java11
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CompileMonitorPane.java	1.10	2008/12/05
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)CompileMonitorPane.java	1.00	2008/03/24
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.console;

import javax.swing.JButton;
import javax.swing.JLabel;

import ssac.aadl.editor.AADLEditor;
import ssac.aadl.editor.EditorMessages;
import ssac.aadl.editor.document.IEditorDocument;
import ssac.aadl.module.setting.EditorBuildOptions;
import ssac.util.Strings;
import ssac.util.io.Files;
import ssac.util.logging.AppLogger;
import ssac.util.process.CommandExecutor;

/**
 * 情報ビューのコンパイル・メッセージ・パネル
 * 
 * @version 4.0.0
 */
public class CompileMonitorPane extends AbstractMonitorPane
{
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -8851152435012933108L;
	
	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private IEditorDocument	targetModel;
	/**
	 * AADLEditor の操作に応じたオプションを保持するオブジェクト
	 * @since 4.0.0
	 */
	private EditorBuildOptions	_buildOptions;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public CompileMonitorPane() {
		super();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean canRun() {
		return (targetModel != null && targetModel.hasTargetFile());
	}
	
	public IEditorDocument getTargetDocument() {
		return targetModel;
	}
	
	/**
	 * AADLEditor の操作によって設定されたビルドオプションを返す。
	 * @return	ビルドオプション、未設定なら <tt>null</tt>
	 * @since 4.0.0
	 */
	public EditorBuildOptions getEditorBuildOptions() {
		return _buildOptions; 
	}
	
	/**
	 * ビルド対象の AADL ソースドキュメントを設定する。
	 * @param newModel		ビルド対象の AADL ソースドキュメント
	 * @param buildoptions	AADLEditor の操作に応じたオプション
	 */
	public void setTargetDocument(IEditorDocument newModel, EditorBuildOptions buildoptions) {
		if (isRunning()) {
			throw new RuntimeException("Already running executor!");
		}
		
		this.targetModel = newModel;
		this._buildOptions = buildoptions;
		this.executor = null;
		
		// update display
		updateTarget();
	}

	// 実行開始
	public void start() {
		try {
			// check exist Source
			if (targetModel == null) {
				throw new NullPointerException("Source file is null!");
			}
			
			// create new executor
			CommandExecutor newExec = targetModel.createCompileExecutor(_buildOptions);
			//CommandExecutor newExec = ExecutorFactory.createCompileExecutor(this.srcModel);
			
			// clear status
			clear();
			String msg = "Start compile \"" + targetModel.getTargetFile().getAbsolutePath() + "\"\n\n";
			console.appendOutputString(msg);
			
			// start
			startProcess(newExec);
		}
		catch (Throwable ex) {
			String strTarget = (targetModel != null && targetModel.hasTargetFile() ? targetModel.getTargetFile().getAbsolutePath() : null);
			if (Strings.isNullOrEmpty(strTarget)) {
				strTarget = "Source file is nothing!";
			}
			String msg = EditorMessages.getErrorMessage(EditorMessages.MessageID.ERR_BUILD_COMPILE, ex, strTarget);
			AppLogger.error(msg, ex);
			AADLEditor.showErrorMessage(this, msg);
		}
	}

	// 実行停止
	public void stop() {
		// タイマー停止
		this.lblRunning.setRunning(false);
		if (this.animTimer.isRunning()) {
			this.animTimer.stop();
		}

		// 実行停止
		if (this.executor != null) {
			// 実行完了
			this.executor.destroy();
			// 表示更新
			showQueueingOutput();
			//--- 終了ステータス
			int exitCode = this.executor.getExitCode();
			//@@@ added by Y.Ishziuka : 2008.05.23
			//this.srcModel.setCompileMessages(exitCode, this.console.getText());	// メッセージ設定
			//@@@ end of added.
			if (exitCode == 0) {
				// 正常終了
				this.console.appendOutputString("\n...succeeded!\n");
			} else {
				// 失敗
				this.console.appendOutputString("\n...Compile failed(" + exitCode + ")!\n");
			}
			String strTime = getTimeText(this.executor.getProcessingTimeMillis());
			this.console.appendOutputString("Compile time : " + strTime + "\n");
			//--- event
			fireExecutorStopped(this.executor);
			//--- executor 破棄
			this.executor = null;
		}
	}

	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	// ターゲットの表示更新
	protected void updateTarget() {
		String strTarget = null;
		if (targetModel != null && targetModel.isCompilable() && targetModel.hasTargetFile()) {
			strTarget = targetModel.getTargetFile().getAbsolutePath();
		}
		if (!Strings.isNullOrEmpty(strTarget)) {
			this.lblTarget.setText(Files.getFilename(strTarget));
			this.lblTarget.setToolTipText(strTarget);
		} else {
			this.lblTarget.setText(" ");
			this.lblTarget.setToolTipText(null);
		}
	}
	
	// 時間ラベルは生成しない
	protected JLabel createTimeLabel() {
		return null;
	}
	
	// [Setting]ボタンは生成しない
	protected JButton createSettingButton() {
		return null;
	}
	
	// [Run]ボタンは生成しない
	protected JButton createRunButton() {
		return null;
	}
	
	// [Cancel]ボタンは生成しない
	protected JButton createCancelButton() {
		return null;
	}
	
}
