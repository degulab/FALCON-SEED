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
 *  Copyright 2007-2011  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)MExecDefFileProgressMonitorTask.java	1.10	2011/02/16
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module.swing;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import ssac.aadl.common.CommonMessages;
import ssac.falconseed.module.MExecDefFileManager;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.ProgressMonitorTask;

/**
 * モジュール実行定義に関する、バックグラウンドで実行されるタスクの処理状況を
 * プログレスダイアログで表示するコンポーネント。
 * 
 * @version 1.10	2011/02/16
 * @since 1.10
 */
public abstract class MExecDefFileProgressMonitorTask extends ProgressMonitorTask
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected int BUF_SIZE = 50000;
	
	static protected final int YES_ONE_OPTION = 0;
	static protected final int YES_ALL_OPTION = 1;
	static protected final int NO_ONE_OPTION  = 2;
	static protected final int NO_ALL_OPTION  = 3;
	static protected final int CANCEL_OPTION  = 4;

	static protected final String[] OVERWRITE_OPTIONS = {
		CommonMessages.getInstance().Overwrite_YesOne,
		CommonMessages.getInstance().Overwrite_YesAll,
		CommonMessages.getInstance().Overwrite_NoOne,
		CommonMessages.getInstance().Overwrite_NoAll,
		CommonMessages.getInstance().Button_Cancel
	};

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/**
	 * すべてのファイルやフォルダの上書きを許可するフラグ。
	 * このフィールドが <tt>null</tt> の場合は、未確認状態となる。
	 */
	private Boolean	_allowOverwriteAllFiles;
	/** システムルートとなるディレクトリの抽象パス **/
	private final VirtualFile	_systemRootDir;
	/** ユーザールートとなるディレクトリの抽象パス **/
	private final VirtualFile	_userRootDir;
	/** システムルートの表示名 **/
	private final String		_systemRootName;
	/** ユーザールートの表示名 **/
	private final String		_userRootName;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public MExecDefFileProgressMonitorTask(
									VirtualFile vfSystemRootDir,
									String strSystemRootName,
									VirtualFile vfUserRootDir,
									String strUserRootName,
									String title,
									String description,
									String note,
									int min,
									int max,
									int refreshInterval)
	{
		super(title, description, note, min, max, refreshInterval);
		this._systemRootDir  = vfSystemRootDir;
		this._userRootDir    = vfUserRootDir;
		this._systemRootName = strSystemRootName;
		this._userRootName   = strUserRootName;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public VirtualFile getSystemRootDir() {
		return _systemRootDir;
	}
	
	public VirtualFile getUserRootDir() {
		return _userRootDir;
	}
	
	public String getSystemRootName() {
		return _systemRootName;
	}
	
	public String getUserRootName() {
		return _userRootName;
	}
	
	/**
	 * コピー、移動、削除など、プログレスモニタータスクの処理中に発生した
	 * エラーに関するメッセージを表示する。
	 * エラーが発生していない場合は、処理しない。
	 * 
	 * @param parentComponent	メッセージボックスのオーナー
	 */
	public void showError(Component parentComponent) {
		if (getErrorCause() instanceof VirtualFileOperationException) {
			VirtualFileOperationException ex = (VirtualFileOperationException)getErrorCause();
			VirtualFile vfSource = ex.getSourceFile();
			VirtualFile vfTarget = ex.getTargetFile();
			// for message box
			String errmsg = formatFileOperationErrorMessage(ex,
								(vfSource==null ? null : formatFilePath(vfSource)),
								(vfTarget==null ? null : formatFilePath(vfTarget)));
			// for log
			String logmsg = formatFileOperationErrorMessage(ex,
					(vfSource==null ? null : vfSource.getAbsolutePath()),
					(vfTarget==null ? null : vfTarget.getAbsolutePath()));

			// show
			AppLogger.error(logmsg, ex);
			Application.showErrorMessage(parentComponent, errmsg);
		}
		else if (getErrorCause() != null) {
			String errmsg = CommonMessages.formatErrorMessage(
								CommonMessages.getInstance().msgCouldNotProcessComplete,
								getErrorCause());
			AppLogger.error(errmsg, getErrorCause());
			Application.showErrorMessage(parentComponent, errmsg);
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String formatFileOperationErrorMessage(VirtualFileOperationException ex, String sourcePath, String targetPath)
	{
		String errmsg;
		if (ex.getOperationType() == VirtualFileOperationException.MOVE) {
			errmsg = String.format(CommonMessages.getInstance().msgCouldNotMoveTo, (sourcePath==null ? "" : sourcePath));
		}
		else if (ex.getOperationType() == VirtualFileOperationException.COPY) {
			errmsg = String.format(CommonMessages.getInstance().msgCouldNotCopyTo, (sourcePath==null ? "" : sourcePath));
		}
		else if (ex.getOperationType() == VirtualFileOperationException.DELETE) {
			errmsg = String.format(CommonMessages.getInstance().msgCouldNotDelete, (sourcePath==null ? "" : sourcePath));
		}
		else {
			StringBuilder sb = new StringBuilder();
			sb.append(CommonMessages.getInstance().msgCouldNotProcessComplete);
			sb.append("\n  type:" + ex.getOperationType());
			if (sourcePath != null) {
				sb.append("\n  source:\"" + sourcePath + "\"");
			}
			if (targetPath != null) {
				sb.append("\n  target:\"" + targetPath + "\"");
			}
			if (ex.getCause() != null) {
				sb.append("\n  cause:(");
				sb.append(ex.getCause().getClass().getName());
				sb.append(")");
				String msg = ex.getCause().getLocalizedMessage();
				if (msg == null) {
					sb.append("\n        " + msg);
				}
			}
			errmsg = sb.toString();
		}
		
		return errmsg;
	}
	
	protected void deleteAllSubFiles(final VirtualFile destDir) {
		VirtualFile[] files = destDir.listFiles();
		if (files != null && files.length > 0) {
			for (VirtualFile child : files) {
				deleteAllFilesRecursive(child);
			}
		}
	}
	
	protected void deleteAllFilesRecursive(final VirtualFile file) {
		if (file.isDirectory()) {
			VirtualFile[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (VirtualFile child : files) {
					deleteAllFilesRecursive(child);
				}
			}
		}
		
		try {
			file.delete();
		} catch (Throwable ex) {
			// ignore exception
		}
	}
	
	protected String formatFilePath(VirtualFile destFile) {
		if (!destFile.isAbsolute()) {
			destFile = destFile.getAbsoluteFile();
		}
		
		if (_userRootDir != null && destFile.isDescendingFrom(_userRootDir)) {
			StringBuilder sb = new StringBuilder();
			sb.append(Files.CommonSeparatorChar);
			if (_userRootName != null && _userRootName.length() > 0) {
				sb.append(_userRootName);
			} else {
				sb.append(_userRootDir.getName());
			}
			sb.append(Files.CommonSeparatorChar);
			sb.append(destFile.relativePathFrom(_userRootDir, Files.CommonSeparatorChar));
			return sb.toString();
		}
		
		if (_systemRootDir != null && destFile.isDescendingFrom(_systemRootDir)) {
			StringBuilder sb = new StringBuilder();
			sb.append(Files.CommonSeparatorChar);
			if (_systemRootName != null && _systemRootName.length() > 0) {
				sb.append(_systemRootName);
			} else {
				sb.append(_systemRootDir.getName());
			}
			sb.append(Files.CommonSeparatorChar);
			sb.append(destFile.relativePathFrom(_systemRootDir, Files.CommonSeparatorChar));
			return sb.toString();
		}
		
		// normal path string
		return destFile.getPath();
	}
	
	protected String createMExecDefOverwriteConfirmMessage(VirtualFile srcFile, VirtualFile destFile)
	{
		DateFormat   frmDate = DateFormat.getDateTimeInstance();
		long srcLastModified = MExecDefFileManager.lastModifiedModuleExecDefData(srcFile);
		long dstLastModified = MExecDefFileManager.lastModifiedModuleExecDefData(destFile);
		String strSrcLastModified = frmDate.format(new Date(srcLastModified));
		String strDstLastModified = frmDate.format(new Date(dstLastModified));
		
		return String.format(RunnerMessages.getInstance().confirmDetailReplaceMExecDef,
				formatFilePath(destFile),
				strDstLastModified,
				strSrcLastModified);
	}
	
	protected int showReplaceMExecDefConfirmDialog(VirtualFile srcFile, VirtualFile destFile)
	{
		String title = CommonMessages.getInstance().confirmOverwiteTitle;
		String message = createMExecDefOverwriteConfirmMessage(srcFile, destFile);
		int optionType = JOptionPane.YES_NO_CANCEL_OPTION;
		int messageType = JOptionPane.QUESTION_MESSAGE;
		
		return showOptionDialog(message, title, optionType, messageType, null,
								OVERWRITE_OPTIONS, OVERWRITE_OPTIONS[0]);
	}
	
	protected String createFileOverwriteConfirmMessage(VirtualFile source, VirtualFile target)
	{
		NumberFormat frmNumber = NumberFormat.getNumberInstance();
		DateFormat   frmDate = DateFormat.getDateTimeInstance();
		Date dtSource = new Date(source.lastModified());
		Date dtTarget = new Date(target.lastModified());
		
		return String.format(CommonMessages.getInstance().confirmDetailOverwriteFile,
				formatFilePath(target),
				frmNumber.format(target.length()), frmDate.format(dtTarget),
				frmNumber.format(source.length()), frmDate.format(dtSource));
	}
	
	protected String createFolderOverwriteConfirmMessage(VirtualFile source, VirtualFile target)
	{
		return String.format(CommonMessages.getInstance().confirmDetailOverwriteDirectory, formatFilePath(target));
	}
	
	protected int showOverwriteFileConfirmDialog(VirtualFile source, VirtualFile target)
	{
		String title = CommonMessages.getInstance().confirmOverwiteTitle;
		String message = createFileOverwriteConfirmMessage(source, target);
		int optionType = JOptionPane.YES_NO_CANCEL_OPTION;
		int messageType = JOptionPane.QUESTION_MESSAGE;
		
		return showOptionDialog(message, title, optionType, messageType, null,
								OVERWRITE_OPTIONS, OVERWRITE_OPTIONS[0]);
	}
	
	protected int showOverwriteDirConfirmDialog(VirtualFile source, VirtualFile target)
	{
		String title = CommonMessages.getInstance().confirmOverwiteTitle;
		String message = createFolderOverwriteConfirmMessage(source, target);
		int optionType = JOptionPane.YES_NO_CANCEL_OPTION;
		int messageType = JOptionPane.QUESTION_MESSAGE;
		
		return showOptionDialog(message, title, optionType, messageType, null,
								OVERWRITE_OPTIONS, OVERWRITE_OPTIONS[0]);
	}
	
	protected Boolean getAllowOverwriteAllFilesFlag() {
		return this._allowOverwriteAllFiles;
	}
	
	protected void resetAllowOverwriteAllFilesFlag() {
		this._allowOverwriteAllFiles = null;
	}
	
	protected void setAllowOverwriteAllFilesFlag(Boolean allowOrDenied) {
		this._allowOverwriteAllFiles = allowOrDenied;
	}
	
	protected void onFileCreated(VirtualFile target) {
		
	}
	
	protected void onFileCopied(VirtualFile source, VirtualFile target) {
		
	}
	
	protected void onFileMoved(VirtualFile source, VirtualFile target) {
		
	}
	
	protected void onFileDeleted(VirtualFile source) {
		
	}
	
	protected boolean copyRecursive(VirtualFile source, VirtualFile targetDir)
	{
		if (!targetDir.exists())
			throw new Error("Target directory does not exists : \"" + targetDir.getAbsolutePath() + "\"");
		if (!targetDir.isDirectory())
			throw new Error("Target directory is not directory : \"" + targetDir.getAbsolutePath() + "\"");
		
		byte[] buffer = new byte[BUF_SIZE];
		
		if (source.isDirectory()) {
			return copyDirectory(null, source, targetDir, buffer);
		} else {
			return copyFile(null, source, targetDir, buffer);
		}
	}
	
	protected boolean moveRecursive(VirtualFile source, VirtualFile targetDir) {
		if (!targetDir.exists())
			throw new Error("Target directory does not exists : \"" + targetDir.getAbsolutePath() + "\"");
		if (!targetDir.isDirectory())
			throw new Error("Target directory is not directory : \"" + targetDir.getAbsolutePath() + "\"");
		
		byte[] buffer = new byte[BUF_SIZE];
		
		if (source.isDirectory()) {
			return moveDirectory(null, source, targetDir, buffer);
		} else {
			return moveFile(null, source, targetDir, buffer);
		}
	}
	
	protected boolean deleteRecursive(VirtualFile source) {
		// check termination
		if (isTerminateRequested())
			return false;	// canceled!

		// delete children
		if (source.isDirectory()) {
			VirtualFile[] flist = source.listFiles();
			for (VirtualFile toDelete : flist) {
				if (!deleteRecursive(toDelete)) {
					return false;	// canceled!
				}
			}
		}
		
		// check termination
		if (isTerminateRequested())
			return false;	// canceled!
		
		// delete source
		try {
			if (source.exists()) {
				source.delete();
				if (source.exists()) {
					throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, source, null);
				}
			}
		} catch (SecurityException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, source, null, ex);
		}
		
		// completed
		incrementValue();	// increment progress value
		onFileDeleted(source);
		return true;
	}

	protected boolean acceptCopy(VirtualFile source) {
		return true;
	}
	
	private boolean copyDirectory(Boolean allowOverwrite, VirtualFile source, VirtualFile targetDir, byte[] buffer)
	{
		// check termination
		if (isTerminateRequested())
			return false;	// canceled!
		
		// check accept
		if (!acceptCopy(source)) {
			// skip source directory
			int num = source.countFiles();
			setValue(getValue() + num);
			return true;
		}
		
		// copy directory
		boolean isMExecDefData = MExecDefFileManager.isModuleExecDefDataDirectory(source);
		VirtualFile copyDir = targetDir.getChildFile(source.getName());
		if (copyDir.equals(source)) {
			// 同じ場所へコピーする場合は、名前を変更する。
			String srcName = source.getName();
			VirtualFile newCopyDir = null;
			for (int i = 1; i < 100000; i++) {
				if (isTerminateRequested())
					return false;	// canceled!
				VirtualFile vfdir = targetDir.getChildFile(getCopyPrefix(i) + srcName);
				if (!vfdir.exists()) {
					newCopyDir = vfdir;
					break;
				}
			}
			if (newCopyDir == null) {
				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, copyDir);
			}
			copyDir = newCopyDir;
		}
		if (!copyDir.exists()) {
			try {
				if (!copyDir.mkdir()) {
					throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, copyDir);
				}
			} catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, copyDir, ex);
			}
		}
		else if (!copyDir.isDirectory()) {
			// コピー先がディレクトリでないならエラー
			throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, copyDir);
		}
		else if (isMExecDefData && !MExecDefFileManager.isModuleExecDefDataDirectory(copyDir)) {
			// コピー元がモジュール実行定義で、コピー先がモジュール実行定義でないならエラー
			throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, copyDir);
		}
		else {
			// check overwrite
			if (allowOverwrite == null) {
				if (_allowOverwriteAllFiles != null) {
					allowOverwrite = _allowOverwriteAllFiles;
				} else {
					int ret;
					if (isMExecDefData) {
						ret = showReplaceMExecDefConfirmDialog(source, copyDir);
					} else {
						ret = showOverwriteDirConfirmDialog(source, copyDir);
					}
					switch (ret) {
						case YES_ONE_OPTION :
							allowOverwrite = Boolean.TRUE;
							break;
						case YES_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.TRUE;
							allowOverwrite = Boolean.TRUE;
							break;
						case NO_ONE_OPTION :
							allowOverwrite = Boolean.FALSE;
							break;
						case NO_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.FALSE;
							allowOverwrite = Boolean.FALSE;
							break;
						default :
							return false;
					}
				}
			}
			if (!allowOverwrite.booleanValue()) {
				// skip source directory
				int num = source.countFiles();
				setValue(getValue() + num);
				return true;
			}
			
			if (isMExecDefData) {
				// コピー元がモジュール実行定義なら、コピー先ディレクトリ内のファイルを全て削除
				deleteAllSubFiles(copyDir);
			}
		}
		//--- completed copy
		incrementValue();
		onFileCopied(source, copyDir);
		// check termination
		if (isTerminateRequested()) {
			return false;
		}
		
		// copy children
		VirtualFile[] flist = source.listFiles();
		if (isMExecDefData) {
			//--- モジュール実行定義のコピー
			for (VirtualFile srcChild : flist) {
				if (srcChild.isDirectory()) {
					if (!copyDirectory(true, srcChild, copyDir, buffer)) {
						return false;	// canceled
					}
				} else {
					if (!copyFile(true, srcChild, copyDir, buffer)) {
						return false;	// canceled
					}
				}
			}
		} else {
			//--- 通常のディレクトリのコピー
			for (VirtualFile srcChild : flist) {
				if (srcChild.isDirectory()) {
					if (!copyDirectory(_allowOverwriteAllFiles, srcChild, copyDir, buffer)) {
						return false;	// canceled
					}
				} else {
					if (!copyFile(allowOverwrite, srcChild, copyDir, buffer)) {
						return false;	// canceled
					}
				}
			}
		}
		return true;
	}

	private boolean copyFile(Boolean allowOverwrite, VirtualFile source, VirtualFile targetDir, byte[] buffer)
	{
		// check termination
		if (isTerminateRequested())
			return false;	// canceled!
		
		// check accept
		if (!acceptCopy(source)) {
			// skip source file
			incrementValue();
			return true;
		}
		
		// copy file
		VirtualFile targetFile = targetDir.getChildFile(source.getName());
		if (!targetDir.isDirectory())
			throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, targetFile);
		if (targetFile.equals(source)) {
			// 同じ場所へコピーする場合は、名前を変更する。
			String srcName = source.getName();
			VirtualFile newTargetFile = null;
			for (int i = 1; i < 100000; i++) {
				if (isTerminateRequested())
					return false;	// canceled!
				VirtualFile vfdir = targetDir.getChildFile(getCopyPrefix(i) + srcName);
				if (!vfdir.exists()) {
					newTargetFile = vfdir;
					break;
				}
			}
			if (newTargetFile == null) {
				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, targetFile);
			}
			targetFile = newTargetFile;
		}
		if (targetFile.exists()) {
			// << exists target file >>
			if (targetFile.isDirectory())
				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, targetFile);
			//--- check overwrite
			if (allowOverwrite == null) {
				if (_allowOverwriteAllFiles != null) {
					allowOverwrite = _allowOverwriteAllFiles;
				} else {
					int ret = showOverwriteFileConfirmDialog(source, targetFile);
					switch (ret) {
						case YES_ONE_OPTION :
							allowOverwrite = Boolean.TRUE;
							break;
						case YES_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.TRUE;
							allowOverwrite = Boolean.TRUE;
							break;
						case NO_ONE_OPTION :
							allowOverwrite = Boolean.FALSE;
							break;
						case NO_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.FALSE;
							allowOverwrite = Boolean.FALSE;
							break;
						default :
							return false;
					}
				}
			}
			if (!allowOverwrite.booleanValue()) {
				// skip source file
				incrementValue();
				return true;
			}
			//--- copy file
			try {
				fileCopy(source, targetFile, buffer);
			}
			catch (IOException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, targetFile, ex);
			}
			catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, targetFile, ex);
			}
		} else {
			// << does not exists target file >>
			try {
				targetFile.createNewFile();
				fileCopy(source, targetFile, buffer);
			}
			catch (IOException ex) {
				try {
					targetFile.delete();
				} catch (Throwable ignore){}
				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, targetFile, ex);
			}
			catch (SecurityException ex) {
				try {
					targetFile.delete();
				} catch (Throwable ignore){}
				throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, targetFile, ex);
			}
		}
		
		// update progress
		incrementValue();
		onFileCopied(source, targetFile);
		return true;
	}
	
	private boolean moveDirectory(Boolean allowOverwrite, VirtualFile source, VirtualFile targetDir, byte[] buffer)
	{
		// check termination
		if (isTerminateRequested())
			return false;	// canceled!
		
		// copy directory
		boolean isMExecDefData = MExecDefFileManager.isModuleExecDefDataDirectory(source);
//		VirtualFile createdMoveDir = null;
		VirtualFile moveDir = targetDir.getChildFile(source.getName());
		if (!moveDir.exists()) {
			try {
				if (!moveDir.mkdir()) {
					throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, moveDir);
				}
//				createdMoveDir = moveDir;
			} catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, moveDir, ex);
			}
		}
		else if (!moveDir.isDirectory()) {
			throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, moveDir);
		}
		else if (isMExecDefData && !MExecDefFileManager.isModuleExecDefDataDirectory(moveDir)) {
			// 移動元がモジュール実行定義で、移動先がモジュール実行定義でないならエラー
			throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, moveDir);
		}
		else {
			// check overwrite
			if (allowOverwrite == null) {
				if (_allowOverwriteAllFiles != null) {
					allowOverwrite = _allowOverwriteAllFiles;
				} else {
					int ret;
					if (isMExecDefData) {
						ret = showReplaceMExecDefConfirmDialog(source, moveDir);
					} else {
						ret = showOverwriteDirConfirmDialog(source, moveDir);
					}
					switch (ret) {
						case YES_ONE_OPTION :
							allowOverwrite = Boolean.TRUE;
							break;
						case YES_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.TRUE;
							allowOverwrite = Boolean.TRUE;
							break;
						case NO_ONE_OPTION :
							allowOverwrite = Boolean.FALSE;
							break;
						case NO_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.FALSE;
							allowOverwrite = Boolean.FALSE;
							break;
						default :
							return false;
					}
				}
			}
			if (!allowOverwrite.booleanValue()) {
				// skip source directory
				int num = source.countFiles();
				setValue(getValue() + num);
				return true;
			}
			
			if (isMExecDefData) {
				// 移動元がモジュール実行定義なら、移動先ディレクトリ内のファイルを全て削除
				deleteAllSubFiles(moveDir);
			}
		}
		// check termination
		if (isTerminateRequested()) {
//			try {
//				if (createdMoveDir != null && createdMoveDir.exists()) {
//					createdMoveDir.delete();
//				}
//			} catch (Throwable ignoreEx) {}
			return false;
		}
		//--- completed copy
		onFileCopied(source, moveDir);
		
		// move children
		VirtualFile[] flist = source.listFiles();
		if (isMExecDefData) {
			//--- モジュール実行定義の移動
			for (VirtualFile srcChild : flist) {
				if (srcChild.isDirectory()) {
					if (!moveDirectory(true, srcChild, moveDir, buffer)) {
						return false;	// canceled
					}
				} else {
					if (!moveFile(true, srcChild, moveDir, buffer)) {
						return false;	// canceled
					}
				}
			}
		} else {
			//--- 通常のディレクトリの移動
			for (VirtualFile srcChild : flist) {
				if (srcChild.isDirectory()) {
					if (!moveDirectory(_allowOverwriteAllFiles, srcChild, moveDir, buffer)) {
						return false;	// canceled
					}
				} else {
					if (!moveFile(allowOverwrite, srcChild, moveDir, buffer)) {
						return false;	// canceled
					}
				}
			}
		}
		
		// check exists source
		if (source.exists()) {
			boolean deleted;
			try {
				deleted = source.delete();
			}
			catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, moveDir, ex);
			}
			if (deleted) {
				onFileDeleted(source);
			}
		} else {
			onFileDeleted(source);
		}
		
		// update progress
		incrementValue();
		onFileMoved(source, moveDir);
		return true;
	}

	private boolean moveFile(Boolean allowOverwrite, VirtualFile source, VirtualFile targetDir, byte[] buffer)
	{
		// check termination
		if (isTerminateRequested())
			return false;	// canceled!
		
		// move file
		VirtualFile targetFile = targetDir.getChildFile(source.getName());
		if (!targetDir.isDirectory())
			throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, targetFile);
		if (targetFile.exists()) {
			// << exists target file >>
			if (targetFile.isDirectory())
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, targetFile);
			//--- check overwrite
			if (allowOverwrite == null) {
				if (_allowOverwriteAllFiles != null) {
					allowOverwrite = _allowOverwriteAllFiles;
				} else {
					int ret = showOverwriteFileConfirmDialog(source, targetFile);
					switch (ret) {
						case YES_ONE_OPTION :
							allowOverwrite = Boolean.TRUE;
							break;
						case YES_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.TRUE;
							allowOverwrite = Boolean.TRUE;
							break;
						case NO_ONE_OPTION :
							allowOverwrite = Boolean.FALSE;
							break;
						case NO_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.FALSE;
							allowOverwrite = Boolean.FALSE;
							break;
						default :
							return false;
					}
				}
			}
			if (!allowOverwrite.booleanValue()) {
				// skip source file
				incrementValue();
				return true;
			}
			//--- copy file
			try {
				fileCopy(source, targetFile, buffer);
			}
			catch (IOException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, targetFile, ex);
			}
			catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, targetFile, ex);
			}
		}
		else {
			// << does not exists target file >>
			try {
				if (!source.renameTo(targetFile)) {
					targetFile.createNewFile();
					fileCopy(source, targetFile, buffer);
				}
			}
			catch (IOException ex) {
				try {
					targetFile.delete();
				} catch (Throwable ignore){}
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, targetFile, ex);
			}
			catch (SecurityException ex) {
				try {
					targetFile.delete();
				} catch (Throwable ignore){}
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, targetFile, ex);
			}
		}
		//--- completed copy
		onFileCopied(source, targetFile);
		
		// check exists source
		if (source.exists()) {
			try {
				source.delete();
			}
			catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, targetFile, ex);
			}
		}
		onFileDeleted(source);
		
		// update progress
		incrementValue();
		onFileMoved(source, targetFile);
		return true;
	}
	
	static private String getCopyPrefix(int copyNo) {
		if (copyNo <= 1) {
			return "Copy_of_";
		} else {
			return "Copy_" + Integer.toString(copyNo) + "_of_";
		}
	}
	
	static public long fileCopy(VirtualFile source, VirtualFile target, byte[] buffer) throws IOException
	{
		InputStream fis = null;
		OutputStream fos = null;
		long ret = 0L;
		try {
			fis = source.getInputStream();
			fos = target.getOutputStream();
			ret = Files.copy(fis, fos, buffer);
			//--- set attribute by java.io.File
			if (!source.canWrite())
				target.setReadOnly();
			target.setLastModified(source.lastModified());
		}
		finally {
			if (fis != null) {
				Files.closeStream(fis);
				fis = null;
			}
			if (fos != null) {
				Files.closeStream(fos);
				fos = null;
			}
		}
		
		//--- update attributes
		try {
			if (!source.canWrite()) {
				target.setReadOnly();
			}
		} catch (Throwable ignoreEx) {}
		try {
			target.setLastModified(source.lastModified());
		} catch (Throwable ignoreEx) {}
		
		return ret;
	}
}
