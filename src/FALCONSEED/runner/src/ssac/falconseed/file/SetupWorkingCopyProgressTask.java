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
 * @(#)SetupWorkingCopyProgressTask.java	2.0.0	2012/10/26
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file;

import java.io.File;
import java.io.IOException;

import ssac.aadl.module.ModuleFileManager;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.runner.RunnerMessages;
import ssac.falconseed.runner.setting.AppSettings;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.io.VirtualFilenameFilter;
import ssac.util.logging.AppLogger;

/**
 * 指定されたディレクトリの作業コピーを作成する、プログレスモニタータスク。
 * 作業コピーは、オリジナルと同一のフォルダに、隠しファイル(もしくはディレクトリ)として、
 * 重複しない名称で作成される。
 * なお、オリジナルが <tt>null</tt> の場合、アプリケーションのテンポラリ領域に任意のファイル名で
 * 作業コピーとなるディレクトリが作成される。
 * 
 * @version 2.0.0	2012/10/26
 * @since 2.0.0
 */
public class SetupWorkingCopyProgressTask extends VirtualFileProgressMonitorTask
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** オリジナルの抽象パス **/
	private final VirtualFile	_vfRootDir;
	/** 作業コピーの抽象パス **/
	private VirtualFile		_vfWorkDir;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public SetupWorkingCopyProgressTask(final VirtualFile rootDir) {
		super(RunnerMessages.getInstance().MExecDefEditDlg_SetupWorkTask_title, null, null, 0, 0, 100, false);	// キャンセル無効
		if (rootDir != null && !rootDir.isDirectory())
			throw new IllegalArgumentException("Root directory path is not directory : \"" + rootDir.getAbsolutePath() + "\"");
		this._vfRootDir = rootDir;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public VirtualFile getRootDirectory() {
		return _vfRootDir;
	}
	
	public VirtualFile getWorkDirectory() {
		return _vfWorkDir;
	}
	
	@Override
	public void processTask() throws Throwable
	{
		AppLogger.info("Start " + getClass().getName() + " task...");
		
		// 作業用ディレクトリの作成
		try {
			VirtualFile vfWork = createWorkDirectoryPath(_vfRootDir);
			if (!vfWork.exists()) {
				if (!vfWork.mkdir()) {
					throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, vfWork);
				}
			}
			_vfWorkDir = vfWork;
		}
		catch (VirtualFileOperationException ex) {
			// 作業コピーを削除
			if (_vfWorkDir != null) {
				setMaximum(getMinimum());
				deleteAllFiles(_vfWorkDir, true);	// 例外を無視
				_vfWorkDir = null;
			}
			throw ex;
		}
		catch (Throwable ex) {
		}
		
		// 作業コピーの内容を削除
		deleteAllSubFiles(_vfWorkDir, true);	// 例外を無視
		
		// ローカルファイルルートディレクトリを作成
		{
			VirtualFile vfLocalFileRootDir = _vfWorkDir.getChildFile(MExecDefFileManager.MEXECDEF_FILES_ROOT_DIRNAME);
			try {
				if (!vfLocalFileRootDir.mkdirs()) {
					throw new IOException("Failed to create directory.");
				}
			} catch (Throwable ex) {
				// 作業コピーを削除
				VirtualFile vfWork = _vfWorkDir;
				if (_vfWorkDir != null) {
					setMaximum(getMinimum());
					deleteAllFiles(_vfWorkDir, true);	// 例外を無視
					_vfWorkDir = null;
				}
				throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, vfWork, ex);
			}
		}
		
		// 作業ディレクトリへコピー
		if (_vfRootDir != null) {
			// コピーするファイルの総数を取得
			int numFiles = _vfRootDir.countFiles();
			if (AppLogger.isInfoEnabled()) {
				AppLogger.info("Start " + getClass().getName() + " task for " + numFiles + " files.");
			}
			//--- 総数の更新
			setMaximum(numFiles);
			incrementValue();
			
			// コピー
			setAllowOverwriteAllFilesFlag(true);
			VirtualFile[] files = _vfRootDir.listFiles(ExclusiveWorkingFilenameFilter.instance);
			try {
				if (files != null) {
					for (VirtualFile f : files) {
						copyRecursive(f, _vfWorkDir);
					}
				}
			}
			catch (VirtualFileOperationException ex) {
				// 作業コピーを削除
				if (_vfWorkDir != null) {
					setMaximum(getMinimum());
					deleteAllFiles(_vfWorkDir, true);	// 例外を無視
					_vfWorkDir = null;
				}
				throw ex;
			}
			catch (Throwable ex) {
				// 作業コピーを削除
				VirtualFile vfWork = _vfWorkDir;
				if (_vfWorkDir != null) {
					setMaximum(getMinimum());
					deleteAllFiles(_vfWorkDir, true);	// 例外を無視
					_vfWorkDir = null;
				}
				throw new VirtualFileOperationException(VirtualFileOperationException.MKDIR, null, vfWork, ex);
			}
		}
		
		// 完了
		setValue(getMaximum());
		AppLogger.info(getClass().getName() + " task Completed!");
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	/**
	 * 作業用ディレクトリのパスを作成する。
	 * この作業用ディレクトリは、<em>vfRootDir</em> の直下に固定の名称で作成される。
	 * ただし、<em>vfRootDir</em> が <tt>null</tt> の場合は、アプリケーションのテンポラリ領域に作成される。
	 * @param vfRootDir		作業コピーのコピー元となるディレクトリ
	 */
	protected VirtualFile createWorkDirectoryPath(final VirtualFile vfRootDir) throws IOException
	{
		if (vfRootDir != null) {
			// ルートディレクトリが指定されていれば、その直下に固定名で作成
			return vfRootDir.getChildFile(MExecDefFileManager.MEXECDEF_WORKING_DIRNAME);
		}
		
		// テンポラリ領域に作成
		File tmpFile = AppSettings.createTemporaryFile(MExecDefFileManager.MEXECDEF_WORKING_DIRNAME, null, false);
		tmpFile.delete();	// テンポラリファイルは削除
		return ModuleFileManager.fromJavaFile(tmpFile);
	}
	
	static public class ExclusiveWorkingFilenameFilter implements VirtualFilenameFilter
	{
		static public final ExclusiveWorkingFilenameFilter instance = new ExclusiveWorkingFilenameFilter();
		
		@Override
		public boolean accept(VirtualFile dir, String name) {
			return (!MExecDefFileManager.MEXECDEF_WORKING_DIRNAME.equalsIgnoreCase(name));
		}
	}
}
