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
 *  <author> Hideaki Yagi (MRI)
 */
/*
 * @(#)ExportMacroGraphTask.java	1.20	2012/03/22
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.macro.graph.swing;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.macro.graph.AADLMacroGraphUtil;
import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;
import ssac.util.process.CommandExecutor;
import ssac.util.swing.Application;
import ssac.util.swing.ProgressMonitorTask;
import dtalge.util.Strings;

/**
 * AADLマクロを <code>GraphViz</code> を使用してグラフイメージを生成するプログレスモニタータスク。
 * 
 * @version 1.20	2012/03/22
 * @since 1.20
 */
public class ExportMacroGraphTask extends ProgressMonitorTask
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected final int PHASE_UNKNOWN			= 0;
	static protected final int PHASE_SETUPTMP_DOT	= 1;
	static protected final int PHASE_SETUPTMP_IMG	= 2;
	static protected final int PHASE_CREATE_DOT		= 3;
	static protected final int PHASE_CREATE_IMG		= 4;
	static protected final int PHASE_LOAD_IMG		= 5;
	static protected final int PHASE_COMPLETED		= 100;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** AADLマクロファイルのエンコーディングとする文字セット名 **/
	private final String		_macroEncoding;
	/** GraphViz のパス **/
	private final File			_fGraphViz;
	/** AADLマクロ定義ファイル **/
	private final VirtualFile	_vfMacro;
	/** 生成されたグラフの <code>dot</code> ファイル **/
	private File				_fGraphDot;
	/** 生成されたグラフのイメージファイル **/
	private File				_fGraphImage;
	/** 作業フェース **/
	private int				_phase;
	/** プロセスの終了コード **/
	private int				_processExitCode;
	/** グラフのイメージデータ **/
	private BufferedImage		_image;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ExportMacroGraphTask(String title, File fGraphViz, VirtualFile vfMacro, String macroEncoding) {
		super(title, null, null, 0, 0, 100, false);	// キャンセル不可
		
		if (fGraphViz == null)
			throw new NullPointerException("The specified GraphViz file is null.");
		if (vfMacro == null)
			throw new NullPointerException("The specified AADL Macro file is null.");

		this._macroEncoding = (Strings.isNullOrEmpty(macroEncoding) ? "MS932" : macroEncoding);
		this._fGraphViz = fGraphViz;
		this._vfMacro = vfMacro;
		this._fGraphDot = null;
		this._fGraphImage = null;
		this._phase = PHASE_UNKNOWN;
		this._image = null;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public boolean isSucceeded() {
		return (getErrorCause()==null && _processExitCode==0);
	}
	
	public VirtualFile getAADLMacroFile() {
		return _vfMacro;
	}
	
	public File getGraphViz() {
		return _fGraphViz;
	}
	
	public File getGraphDotFile() {
		return _fGraphDot;
	}
	
	public File getGraphImageFile() {
		return _fGraphImage;
	}
	
	public BufferedImage getGraphImage() {
		return _image;
	}
	
	/**
	 * タスクの処理中に発生したエラーに関するメッセージを表示する。
	 * エラーが発生していない場合は、処理しない。
	 * 
	 * @param parentComponent	メッセージボックスのオーナー
	 */
	public void showError(Component parentComponent) {
		Throwable error = getErrorCause();
		if (error == null) {
			// 正常終了の場合は、終了コードを確認
			if (_processExitCode != 0) {
				// Error
				String errmsg = RunnerMessages.getInstance().msgGraphVizFailedToExec + " : ExitCode=" + String.valueOf(_processExitCode);
				AppLogger.error(errmsg, error);
				Application.showErrorMessage(parentComponent, errmsg);
			}
			return;
		}

		String errmsg = RunnerMessages.getInstance().msgGraphVizFailedToOutputGraph;
		
		if (error instanceof OutOfMemoryError) {
			errmsg = CommonMessages.getInstance().msgOutOfMemoryError;
			AppLogger.error(errmsg, error);
		}
		else if (_phase == PHASE_SETUPTMP_DOT || _phase == PHASE_SETUPTMP_IMG) {
			errmsg = RunnerMessages.getInstance().msgGraphVizFailedToCreateWorkFile;
			AppLogger.error(errmsg, error);
		}
		else if (_phase == PHASE_CREATE_DOT) {
			errmsg = RunnerMessages.getInstance().msgGraphVizFailedToGenerateGraph;
			AppLogger.error(errmsg, error);
		}
		else if (_phase == PHASE_CREATE_IMG) {
			errmsg = RunnerMessages.getInstance().msgGraphVizFailedToOutputImage;
			AppLogger.error(errmsg, error);
		}
		else if (_phase == PHASE_LOAD_IMG) {
			errmsg = RunnerMessages.getInstance().msgGraphVizFailedToReadImage;
			AppLogger.error(errmsg, error);
		}
		else {
			//--- 原因不明のエラー
			AppLogger.error(errmsg, error);
		}
		
		Application.showErrorMessage(parentComponent, errmsg);
	}
	
	//------------------------------------------------------------
	// Event handler
	//------------------------------------------------------------

	@Override
	public void processTask() throws Throwable
	{
		AppLogger.info("Start " + getClass().getName() + " progress task...");
		
		// dot ファイル出力先のテンポラリファイル生成
		_phase = PHASE_SETUPTMP_DOT;
		_fGraphDot = File.createTempFile("AMFGraph", ".dot");
		_fGraphDot.deleteOnExit();
		if (AppLogger.isInfoEnabled()) {
			String msg = String.format("Temporary file for a dot file was created successfully : \"%s\"", _fGraphDot.getAbsolutePath());
			AppLogger.info(msg);
		}
		
		// イメージファイル出力先のテンポラリファイル生成
		_phase = PHASE_SETUPTMP_IMG;
		_fGraphImage = File.createTempFile("AMFGraph", ".png");
		_fGraphImage.deleteOnExit();
		if (AppLogger.isInfoEnabled()) {
			String msg = String.format("Temporary file for a image file was created successfully : \"%s\"", _fGraphImage.getAbsolutePath());
			AppLogger.info(msg);
		}
		
		// AADL マクロの dot ファイル生成
		_phase = PHASE_CREATE_DOT;
		setDescription(RunnerMessages.getInstance().MExecDefEditDlg_Graph_ProgreesDesc_GenGraph);
		AADLMacroGraphUtil.exportMacroToDot(_vfMacro, _macroEncoding, ModuleFileManager.fromJavaFile(_fGraphDot));
		if (AppLogger.isInfoEnabled()) {
			String msg = String.format("The dot file was created successfully : \"%s\"", _fGraphDot.getAbsolutePath());
			AppLogger.info(msg);
		}
		
		// dot ファイルからイメージを生成
		_phase = PHASE_CREATE_IMG;
		setDescription(RunnerMessages.getInstance().MExecDefEditDlg_Graph_ProgressDesc_OutputImage);
		//--- 実行コマンドの生成
		CommandExecutor executor = new CommandExecutor(createDotToImageCommands(_fGraphViz, _fGraphDot, _fGraphImage));
		//--- 実行
		try {
			executor.start();
			do {
				try {
					executor.waitFor();
				} catch (InterruptedException ex) {}
			} while (executor.isRunning());
		}
		finally {
			_processExitCode = executor.getExitCode();
			executor.destroy();
		}
		if (_processExitCode == 0) {
			//--- success
			if (AppLogger.isInfoEnabled()) {
				String msg = String.format("The graph image file was created successfully : \"%s\"", _fGraphImage.getAbsolutePath());
				AppLogger.info(msg);
			}
		} else {
			//--- fail
			if (AppLogger.isInfoEnabled()) {
				String msg = String.format("Failed to create the graph image file : ExitCode(%d) : \"%s\"",
										executor.getExitCode(), _fGraphImage.getAbsolutePath());
				AppLogger.info(msg);
			}
		}
		
		// イメージファイルの読み込み
		_phase = PHASE_LOAD_IMG;
		setDescription(RunnerMessages.getInstance().MExecDefEditDlg_Graph_ProgressDesc_ReadImage);
		_image = ImageIO.read(_fGraphImage);
		
		// 完了
		_phase = PHASE_COMPLETED;
		AppLogger.info(getClass().getName() + " progress task completed!");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected List<String> createDotToImageCommands(File fGraphVizFile, File fDotFile, File fImageFile) {
		// #exec,,,,,,C:\Program Files (x86)\Graphviz 2.28\bin\dot.exe,[STR],-Tpng,[IN],../result/test.dot,[STR],-o,[OUT],../result/sample1.png
		ArrayList<String> commands = new ArrayList<String>();
		
		// GraphViz 実行ファイル
		commands.add(fGraphVizFile.getAbsolutePath());
		
		// 出力オプション
		commands.add("-Tpng");	// png 出力
		
		// dot ファイル
		commands.add(fDotFile.getAbsolutePath());
		
		// 出力ファイル
		commands.add("-o");
		commands.add(fImageFile.getAbsolutePath());
		
		// 完了
		return commands;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------

}
