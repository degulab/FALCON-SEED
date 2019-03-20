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
 * @(#)RollbackWorkingCopyProgressTask.java	2.0.0	2012/10/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file;

import ssac.falconseed.runner.RunnerMessages;
import ssac.util.io.VirtualFile;
import ssac.util.logging.AppLogger;

/**
 * 作業コピーを削除する、プログレスモニタータスク。
 * 
 * @version 2.0.0	2012/10/26
 * @since 2.0.0
 */
public class RollbackWorkingCopyProgressTask extends VirtualFileProgressMonitorTask
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 作業コピーのパス **/
	private final VirtualFile	_vfWork;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public RollbackWorkingCopyProgressTask(final VirtualFile workFile) {
		super(RunnerMessages.getInstance().MExecDefEditDlg_RollbackTask_title, null, null, 0, 0, 100, false);	// キャンセル無効
		if (workFile == null) {
			throw new IllegalArgumentException("'workFile' object is null.");
		}
		this._vfWork = workFile;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@Override
	public void processTask() throws Throwable
	{
		AppLogger.info("Start " + getClass().getName() + " task...");
		
		// 処理対象のファイル総数をカウント
		int numFiles = _vfWork.countFiles();
		if (AppLogger.isInfoEnabled()) {
			AppLogger.info("Start " + getClass().getName() + " task for " + numFiles + " files.");
		}
		//--- 総数をプログレスバーに設定
		setMaximum(numFiles + 1);
		incrementValue();
		
		deleteRecursive(_vfWork);
		
		// 完了
		setValue(getMaximum());
		AppLogger.info(getClass().getName() + " task Completed!");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
