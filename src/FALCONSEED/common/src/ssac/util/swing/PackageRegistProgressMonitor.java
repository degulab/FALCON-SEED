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
 * @(#)PackageRegistProgressMonitor.java	1.14	2009/12/17
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.swing;

import ssac.aadl.common.CommonMessages;
import ssac.aadl.module.ModuleFileManager;
import ssac.aadl.module.setting.PackageSettings;
import ssac.aadl.module.setting.ProjectSettings;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.io.VirtualFileOperationException;

/**
 * パッケージ登録(プロジェクトをパッケージへ変換)専用のプログレスモニタ。
 * このモニタは、エディタおよびモジュールマネージャで利用される。
 * 
 * @version 1.14	2009/12/17
 * @since 1.14
 */
public class PackageRegistProgressMonitor extends FileProgressMonitorTask
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static private final ProjectRootFileFilter _projRootFilter = new ProjectRootFileFilter();

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------
	
	private final VirtualFile	_srcProjRoot;
	private final VirtualFile	_targetDir;
	private final String		_packName;
	private final ProjectSettings	_srcProjSetting;
	private final PackageSettings _dstPackSetting;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public PackageRegistProgressMonitor(VirtualFile sourceProject, ProjectSettings projSetting,
										VirtualFile targetDir, String packageName)
	{
		super(CommonMessages.getInstance().PackageRegistProgressMonitor_title, null, null, 0, 0, 0);
		this._srcProjRoot    = sourceProject;
		this._srcProjSetting = projSetting;
		this._targetDir      = targetDir;
		this._packName       = packageName;
		this._dstPackSetting = new PackageSettings();
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	@Override
	public void processTask()
	{
		// 処理対象のファイル総数をカウント
		int numFiles = _srcProjRoot.countFiles();
		
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
		
		// モジュールパッケージルートの作成
		VirtualFile packRootDir = _targetDir.getChildFile(_packName);
		if (packRootDir.exists())
			throw new Error("Package root directory already exists : \"" + packRootDir.getAbsolutePath() + "\"");
		try {
			if (!packRootDir.mkdir()) {
				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, _srcProjRoot, packRootDir);
			}
		} catch (SecurityException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.COPY, _srcProjRoot, packRootDir, ex);
		}
		
		// パッケージ設定ファイルの生成
		VirtualFile packPrefs = ModuleFileManager.getModulePackagePrefsFile(packRootDir);
		//--- プロジェクト設定のコピー
		_dstPackSetting.setTitle(_srcProjSetting.getTitle());
		_dstPackSetting.setDescription(_srcProjSetting.getDescription());
		String relMainModule = _srcProjSetting.getRelativeMainModuleFile();
		if (relMainModule != null) {
			VirtualFile packMainModule = packRootDir.getChildFile(relMainModule).getNormalizedFile();
			_dstPackSetting.setMainModuleFile(packMainModule);
		}
		//--- パッケージ設定ファイルの保存
		try {
			_dstPackSetting.saveForTarget(packPrefs);
		} catch (Throwable ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.COPY,
							_srcProjSetting.getVirtualPropertyFile(), packPrefs, ex);
		}
		if (isTerminateRequested())
			return;
		
		// コピーするファイルの収集
		resetAllowOverwriteAllFilesFlag();
		VirtualFile[] copyFiles = _srcProjRoot.listFiles(_projRootFilter);
		if (copyFiles != null && copyFiles.length > 0) {
			for (VirtualFile srcFile : copyFiles) {
				if (!copyRecursive(srcFile, packRootDir)) {
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
		
		// プロジェクトから除外されているファイルは、
		// コピーしない
		VirtualFile file = source;
		do {
			if (_srcProjSetting.containsExclusiveProjectFile(file)) {
				// 除外されているファイルは許可しない
				return false;
			}
			if (_srcProjRoot.equals(file)) {
				// 検査終了
				break;
			}
		} while ((file = file.getParentFile()) != null);

		// 許可
		return true;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static private class ProjectRootFileFilter implements VirtualFileFilter
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
