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
 * @(#)PackageImportProgressMonitor.java	1.14	2009/12/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadl.editor.view.tree;

import ssac.aadl.editor.EditorMessages;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.setting.PackageSettings;
import ssac.aadl.module.setting.ProjectSettings;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.swing.FileProgressMonitorTask;

/**
 * パッケージインポート(プロジェクトに変換)専用のプログレスモニタ。
 * このモニタは、エディタで利用される。
 * 
 * @version 1.14	2009/12/17
 * @since 1.14
 */
public class PackageImportProgressMonitor extends FileProgressMonitorTask
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final PackageRootFileFilter _packRootFilter = new PackageRootFileFilter();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final VirtualFile	_srcPackRoot;
	private final VirtualFile	_targetDir;
	private final String		_projName;
	private final PackageSettings	_srcPackSetting;
	private final ProjectSettings _dstProjSetting;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PackageImportProgressMonitor(VirtualFile sourcePackage, PackageSettings packSetting,
										VirtualFile targetDir, String projectName)
	{
		super(EditorMessages.getInstance().PackImportDlg_Title, null, null, 0, 0, 0);
		this._srcPackRoot    = sourcePackage;
		this._srcPackSetting = packSetting;
		this._targetDir      = targetDir;
		this._projName       = projectName;
		this._dstProjSetting = new ProjectSettings();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@Override
	public void processTask()
	{
		// 処理対象のファイル総数をカウント
		int numFiles = _srcPackRoot.countFiles();
		
		// 総数をプログレスバーに設定
		setMaximum(numFiles);
		
		// check
		if (!_targetDir.exists())
			throw new Error("Target directory does not exists : \"" + _targetDir.getAbsolutePath() + "\"");
		if (!_targetDir.isDirectory())
			throw new Error("Target directory is not directory : \"" + _targetDir.getAbsolutePath() + "\"");
		if (!_targetDir.canWrite())
			throw new Error("Target directory can not write : \"" + _targetDir.getAbsolutePath() + "\"");
		if (isTerminateRequested())
			return;
		
		// プロジェクトのルート作成
		VirtualFile projRootDir = _targetDir.getChildFile(_projName);
		if (projRootDir.exists())
			throw new Error("Project root directory already exists : \"" + projRootDir.getAbsolutePath() + "\"");
		try {
			if (!projRootDir.mkdir()) {
				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, _srcPackRoot, projRootDir);
			}
		} catch (SecurityException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.COPY, _srcPackRoot, projRootDir, ex);
		}
		
		// プロジェクト設定ファイルの生成
		VirtualFile projPrefs = ModuleFileManager.getProjectPrefsFile(projRootDir);
		//--- パッケージ設定のコピー
		_dstProjSetting.setTitle(_srcPackSetting.getTitle());
		_dstProjSetting.setDescription(_srcPackSetting.getDescription());
		String relMainModule = _srcPackSetting.getRelativeMainModuleFile();
		if (relMainModule != null) {
			VirtualFile projMainModule = projRootDir.getChildFile(relMainModule).getNormalizedFile();
			_dstProjSetting.setMainModuleFile(projMainModule);
		}
		//--- プロジェクト設定ファイルの保存
		try {
			_dstProjSetting.saveForTarget(projPrefs);
		} catch (Throwable ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.COPY,
					_srcPackSetting.getVirtualPropertyFile(), projPrefs, ex);
		}
		if (isTerminateRequested())
			return;
		
		// コピーするファイルの収集
		resetAllowOverwriteAllFilesFlag();
		VirtualFile[] copyFiles = _srcPackRoot.listFiles(_packRootFilter);
		if (copyFiles != null && copyFiles.length > 0) {
			for (VirtualFile srcFile : copyFiles) {
				if (!copyRecursive(srcFile, projRootDir)) {
					// cancel
					break;
				}
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	@Override
	protected boolean acceptCopy(VirtualFile source) {
		// プロジェクト設定ファイルは除外する
		if (source.isFile() && ModuleFileManager.PROJECT_PREFS_FILENAME.equalsIgnoreCase(source.getName())) {
			return false;
		}
		
		return super.acceptCopy(source);
	}
	
	static private class PackageRootFileFilter implements VirtualFileFilter
	{
		public boolean accept(VirtualFile pathname) {
			// モジュールパッケージ設定ファイルとプロジェクト設定ファイルは除外する
			String filename = pathname.getName();
			if (ModuleFileManager.PACK_PREFS_FILENAME.equalsIgnoreCase(filename)) {
				return false;
			}
			else if (ModuleFileManager.PROJECT_PREFS_FILENAME.equalsIgnoreCase(filename)) {
				return false;
			}
			else {
				return true;
			}
		}
	}
}
