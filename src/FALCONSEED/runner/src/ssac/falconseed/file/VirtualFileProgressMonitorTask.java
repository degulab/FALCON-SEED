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
 * @(#)VirtualFileProgressMonitorTask.java	2.0.0	2012/10/26
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)VirtualFileProgressMonitorTask.java	1.20	2012/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import ssac.aadl.common.CommonMessages;
import ssac.falconseed.runner.RunnerMessages;
import ssac.util.io.DefaultFile;
import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.io.VirtualFilenameFilter;
import ssac.util.logging.AppLogger;
import ssac.util.swing.Application;
import ssac.util.swing.ProgressMonitorTask;

/**
 * バックグラウンドで実行されるファイル操作に関するタスク。
 * 
 * @version 2.0.0	2012/10/26
 * @since 1.20
 */
public abstract class VirtualFileProgressMonitorTask extends ProgressMonitorTask
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
	/**
	 * パス整形オブジェクト。
	 * このフィールドが <tt>null</tt> ではない場合、ユーザーへの
	 * メッセージにおいて、ファイルの位置を示すための
	 * パスがこのオブジェクトで整形されたパスで表示される。
	 */
	private VirtualFilePathFormatter	_pathFormatter;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public VirtualFileProgressMonitorTask(String title, String description, String note,
											int min, int max, int refreshInterval)
	{
		super(title, description, note, min, max, refreshInterval);
		this._pathFormatter = null;
	}
	
	public VirtualFileProgressMonitorTask(String title, String description, String note,
											int min, int max, int refreshInterval, boolean allowCancel)
	{
		super(title, description, note, min, max, refreshInterval, allowCancel);
		this._pathFormatter = null;
	}
	
	public VirtualFileProgressMonitorTask(VirtualFilePathFormatter formatter,
											String title, String description, String note,
											int min, int max, int refreshInterval)
	{
		super(title, description, note, min, max, refreshInterval);
		this._pathFormatter = formatter;
	}
	
	public VirtualFileProgressMonitorTask(VirtualFilePathFormatter formatter,
											String title, String description, String note,
											int min, int max, int refreshInterval, boolean allowCancel)
	{
		super(title, description, note, min, max, refreshInterval, allowCancel);
		this._pathFormatter = formatter;
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	public VirtualFilePathFormatter getPathFormatter() {
		return _pathFormatter;
	}
	
	public void setPathFormatter(VirtualFilePathFormatter formatter) {
		_pathFormatter = formatter;
	}
	
	public String getFormattedPath(VirtualFile file) {
		if (_pathFormatter == null) {
			return file.getPath();
		} else {
			return _pathFormatter.getPath(file);
		}
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
								(vfSource==null ? null : getFormattedPath(vfSource)),
								(vfTarget==null ? null : getFormattedPath(vfTarget)));
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
	
	static public long getTotalFileLength(VirtualFile file) {
		long llen = 0;
		if (file.isDirectory()) {
			VirtualFile[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (VirtualFile subfile : files) {
					llen += getTotalFileLength(subfile);
				}
			}
		}
		else if (file.isFile()) {
			llen += file.length();
		}
		return llen;
	}
	
	static public long getTotalFileLength(VirtualFile file, VirtualFilenameFilter filter) {
		long llen = 0;
		if (file.isDirectory()) {
			VirtualFile[] files = file.listFiles(filter);
			if (files != null && files.length > 0) {
				for (VirtualFile subfile : files) {
					llen += getTotalFileLength(subfile);
				}
			}
		}
		else if (file.isFile()) {
			llen += file.length();
		}
		return llen;
	}
	
	static public long getTotalFileLength(VirtualFile file, VirtualFileFilter filter) {
		long llen = 0;
		if (file.isDirectory()) {
			VirtualFile[] files = file.listFiles(filter);
			if (files != null && files.length > 0) {
				for (VirtualFile subfile : files) {
					llen += getTotalFileLength(subfile);
				}
			}
		}
		else if (file.isFile()) {
			llen += file.length();
		}
		return llen;
	}

	//------------------------------------------------------------
	// Internal Handler
	//------------------------------------------------------------
	
	protected int showOverwriteFileConfirmDialog(VirtualFile source, VirtualFile dest)
	{
		String title = CommonMessages.getInstance().confirmOverwiteTitle;
		String message = createFileOverwriteConfirmMessage(source, dest);
		int optionType = JOptionPane.YES_NO_CANCEL_OPTION;
		int messageType = JOptionPane.QUESTION_MESSAGE;
		
		return showOptionDialog(message, title, optionType, messageType, null,
								OVERWRITE_OPTIONS, OVERWRITE_OPTIONS[0]);
	}
	
	protected int showOverwriteDirectoryConfirmDialog(VirtualFile source, VirtualFile dest)
	{
		String title = CommonMessages.getInstance().confirmOverwiteTitle;
		String message = createDirectoryOverwriteConfirmMessage(source, dest);
		int optionType = JOptionPane.YES_NO_CANCEL_OPTION;
		int messageType = JOptionPane.QUESTION_MESSAGE;
		
		return showOptionDialog(message, title, optionType, messageType, null,
								OVERWRITE_OPTIONS, OVERWRITE_OPTIONS[0]);
	}
	
	protected int showReplaceTargetConfirmDialog(VirtualFile source, VirtualFile dest) {
		String title = RunnerMessages.getInstance().confirmReplaceTitle;
		String message = createTargetReplaceConfirmMessage(source, dest);
		int optionType = JOptionPane.YES_NO_CANCEL_OPTION;
		int messageType = JOptionPane.QUESTION_MESSAGE;
		
		return showOptionDialog(message, title, optionType, messageType, null,
								OVERWRITE_OPTIONS, OVERWRITE_OPTIONS[0]);
	}

	protected boolean acceptCopy(VirtualFile source) {
		return true;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected String getFileOperationTypeString(int operationType) {
		switch (operationType) {
			case VirtualFileOperationException.CREATE :
				return "Create";
			case VirtualFileOperationException.MKDIR :
				return "Make Directory";
			case VirtualFileOperationException.DELETE :
				return "Delete";
			case VirtualFileOperationException.COPY :
				return "Copy";
			case VirtualFileOperationException.MOVE :
				return "Move";
			default :
				return "Error";
		}
	}
	
	protected String getDefaultErrorMessage() {
		return CommonMessages.getInstance().msgCouldNotProcessComplete;
	}
	
	protected String formatFileOperationErrorMessage(VirtualFileOperationException ex, String sourcePath, String targetPath)
	{
		String errmsg;
		int opType = ex.getOperationType();
		if (opType == VirtualFileOperationException.CREATE) {
			String strPath = (sourcePath==null ? targetPath == null ? null : targetPath : sourcePath);
			if (strPath != null)
				errmsg = String.format(CommonMessages.getInstance().msgCouldNotCreateFile + "\nFile:\"%s\"", strPath);
			else
				errmsg = CommonMessages.getInstance().msgCouldNotCreateFile;
		}
		else if (opType == VirtualFileOperationException.MKDIR) {
			String strPath = (sourcePath==null ? targetPath == null ? null : targetPath : sourcePath);
			if (strPath != null)
				errmsg = String.format(CommonMessages.getInstance().msgCouldNotCreateDirectory + "\nFile:\"%s\"", strPath);
			else
				errmsg = CommonMessages.getInstance().msgCouldNotCreateDirectory;
		}
		else if (opType == VirtualFileOperationException.MOVE) {
			errmsg = String.format(CommonMessages.getInstance().msgCouldNotMoveTo, (sourcePath==null ? "" : sourcePath));
		}
		else if (opType == VirtualFileOperationException.COPY) {
			errmsg = String.format(CommonMessages.getInstance().msgCouldNotCopyTo, (sourcePath==null ? "" : sourcePath));
		}
		else if (opType == VirtualFileOperationException.DELETE) {
			errmsg = String.format(CommonMessages.getInstance().msgCouldNotDelete, (sourcePath==null ? "" : sourcePath));
		}
		else {
			StringBuilder sb = new StringBuilder();
			sb.append(getDefaultErrorMessage());
			sb.append("\n  type:" + opType);
			sb.append("(");
			sb.append(opType);
			sb.append(")");
			sb.append(getFileOperationTypeString(ex.getOperationType()));
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
	
	protected Boolean getAllowOverwriteAllFilesFlag() {
		return this._allowOverwriteAllFiles;
	}
	
	protected void resetAllowOverwriteAllFilesFlag() {
		this._allowOverwriteAllFiles = null;
	}
	
	protected void setAllowOverwriteAllFilesFlag(Boolean allowOrDenied) {
		this._allowOverwriteAllFiles = allowOrDenied;
	}
	
	protected String createFileOverwriteConfirmMessage(VirtualFile source, VirtualFile dest)
	{
		NumberFormat frmNumber = NumberFormat.getNumberInstance();
		DateFormat   frmDate = DateFormat.getDateTimeInstance();
		Date dtSource = new Date(source.lastModified());
		Date dtDest   = new Date(dest.lastModified());
		
		return String.format(CommonMessages.getInstance().confirmDetailOverwriteFile,
				getFormattedPath(dest),
				frmNumber.format(dest.length()), frmDate.format(dtDest),
				frmNumber.format(source.length()), frmDate.format(dtSource));
	}
	
	protected String createDirectoryOverwriteConfirmMessage(VirtualFile source, VirtualFile dest)
	{
		return String.format(CommonMessages.getInstance().confirmDetailOverwriteDirectory, getFormattedPath(dest));
	}
	
	protected String createTargetReplaceConfirmMessage(VirtualFile source, VirtualFile dest)
	{
		NumberFormat frmNumber = NumberFormat.getNumberInstance();
		DateFormat   frmDate = DateFormat.getDateTimeInstance();
		Date dtSource = new Date(source.lastModified());
		Date dtDest   = new Date(dest.lastModified());
		
		StringBuilder sb = new StringBuilder();
		//--- dest
		if (dest.isDirectory()) {
			sb.append(String.format(RunnerMessages.getInstance().confirmDetailPartReplaceDestDirectory,
						getFormattedPath(dest), frmDate.format(dtDest)));
		} else {
			sb.append(String.format(RunnerMessages.getInstance().confirmDetailPartReplaceDestFile,
						getFormattedPath(dest),
						frmNumber.format(dest.length()), frmDate.format(dtDest)));
		}
		//--- source
		if (source.isDirectory()) {
			sb.append(String.format(RunnerMessages.getInstance().confirmDetailPartReplaceSrcDirectory,
						frmDate.format(dtSource)));
		} else {
			sb.append(String.format(RunnerMessages.getInstance().confirmDetailPartReplaceSrcFile,
						frmNumber.format(source.length()), frmDate.format(dtSource)));
		}
		return sb.toString();
	}

	//------------------------------------------------------------
	// File operations
	//------------------------------------------------------------

	/**
	 * 単一のファイルを削除する。
	 * 削除に一度失敗した場合、10ミリ秒の待機時間経過後に最大10回削除のリトライを行う。
	 * 削除可能なファイルが <code>delete</code> メソッドの呼び出しで <tt>false</tt> を
	 * 返す場合の対策のため、このメソッドを使用している。
	 * なお、このメソッドでは <em>file</em> が削除可能かどうか、存在しているかどうかはチェックしない。
	 * @param file	削除対象のファイルもしくはディレクトリ
	 * @return	ファイルが削除できた場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws SecurityException	セキュリティマネージャが存在し、セキュリティマネージャの
	 * 								<code>SecurityManager.checkDelete(java.lang.String)</code>
	 * 								メソッドがファイルへの削除アクセスを許可しない場合 
	 */
	static public boolean fileDeleteExactly(final VirtualFile file)
	{
		file.delete();
		boolean deleted = !file.exists();
		if (!deleted) {
			long lst = System.currentTimeMillis();
			for (int i = 0; i < 10; i++) {
				try {
					Thread.sleep(10);
				} catch (Throwable ignoreEx) {ignoreEx=null;}
				file.delete();
				deleted = !file.exists();
				if (deleted) {
					String msg = String.format("Delete files by the retry was successful. (%d ms)\nFile : \"%s\"",
												(System.currentTimeMillis() - lst),
												file.getAbsolutePath());
					AppLogger.warn(msg);
					break;
				}
			}
		}
		return deleted;
	}

	/**
	 * 指定されたディレクトリに含まれる全てのファイルやディレクトリを削除する。指定されたディレクトリは削除されない。
	 * このメソッドでは、指定された抽象パスの正当性は検証しない。
	 * @param destDir	全てのサブファイルを削除するディレクトリの抽象パス
	 * @param ignoreException	例外を無視して処理を続行する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws	NullPointerException	<em>destDir</em> が <tt>null</tt> の場合
	 * @throws VirtualFileOperationException	<em>ignoreException</em> が <tt>false</tt> のとき、ファイルが削除できなかった場合
	 * @throws SecurityException	<em>ignoreException</em> が <tt>false</tt> のとき、<code>SecurityException</code> が発生した場合
	 */
	static public void deleteAllSubFiles(final VirtualFile destDir, boolean ignoreException)
	{
		if (ignoreException) {
			//--- 例外を無視
			try {
				VirtualFile[] files = destDir.listFiles();
				if (files != null && files.length > 0) {
					for (VirtualFile child : files) {
						deleteAllFiles(child, ignoreException);
					}
				}
			}
			catch (Throwable ex) {
				// ignore exception
			}
		} else {
			//--- 例外をスロー
			VirtualFile[] files = destDir.listFiles();
			if (files != null && files.length > 0) {
				for (VirtualFile child : files) {
					deleteAllFiles(child, ignoreException);
				}
			}
		}
	}

	/**
	 * 指定された抽象パスのファイルもしくはディレクトリを削除する。
	 * 抽象パスがディレクトリの場合、そのディレクトリに含まれる全てのファイルやディレクトリが削除される。
	 * @param file	削除対象の抽象パス
	 * @throws	NullPointerException	<em>destDir</em> が <tt>null</tt> の場合
	 * @throws VirtualFileOperationException	<em>ignoreException</em> が <tt>false</tt> のとき、ファイルが削除できなかった場合
	 */
	static public void deleteAllFiles(final VirtualFile file, boolean ignoreException)
	{
		if (ignoreException) {
			try {
				if (file.isDirectory()) {
					VirtualFile[] files = file.listFiles();
					if (files != null && files.length > 0) {
						for (VirtualFile child : files) {
							deleteAllFiles(child, ignoreException);
						}
					}
				}
				
				fileDeleteExactly(file);
			} catch (Throwable ex) {
				// ignore exception
			}
		} else {
			try {
				if (file.isDirectory()) {
					VirtualFile[] files = file.listFiles();
					if (files != null && files.length > 0) {
						for (VirtualFile child : files) {
							deleteAllFiles(child, ignoreException);
						}
					}
				}
				
				if (file.exists() && fileDeleteExactly(file)) {
					throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, file, null);
				}
			} catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, file, null, ex);
			}
		}
	}
	
	static public void renameFiles(final VirtualFile source, final VirtualFile dest) {
		try {
			if (dest.exists())
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest);
			
			if (!source.renameTo(dest)) {
				if (!source.isDirectory())
					throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest);
				
				if (!source.mkdirs())
					throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest);
				
				VirtualFile[] subfiles = source.listFiles();
				if (subfiles != null && subfiles.length > 0) {
					for (VirtualFile svf : subfiles) {
						renameFiles(svf, dest.getChildFile(svf.getName()));
					}
				}
				dest.setLastModified(source.lastModified());
				if (!source.canWrite())
					dest.setReadOnly();
			}
		}
		catch (SecurityException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest, ex);
		}
	}

	/**
	 * 指定されたファイルが書き込み可能なファイルであることをチェックする。ディレクトリの場合は、ディレクトリに含まれる
	 * 全てのファイルもしくはディレクトリを再帰的にチェックする。書き込み不可能なファイルやディレクトリが含まれている
	 * 場合、このメソッドは書き込み不可能な抽象パスを返す。
	 * @param file	判定するファイルもしくはディレクトリを示す抽象パス
	 * @return	書き込む不可能なファイルもしくはディレクトリを示す抽象パス。全て書き込み可能な場合は <tt>true</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public VirtualFile checkWritableAllFiles(final VirtualFile file) {
		boolean cando;
		try {
			cando = file.canWrite();
		} catch (Throwable ignoreEx) {
			cando = false;
		}
		if (!cando) {
			return file;
		}
		
		if (file.exists() && file.isDirectory()) {
			VirtualFile[] subfiles = file.listFiles();
			if (subfiles != null && subfiles.length > 0) {
				for (VirtualFile svf : subfiles) {
					VirtualFile ret = checkWritableAllFiles(svf);
					if (ret != null) {
						return ret;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 指定されたファイルが変更可能なファイルであることをチェックする。ディレクトリの場合は、ディレクトリに含まれる
	 * 全てのファイルもしくはディレクトリを再帰的にチェックする。変更不可能なファイルやディレクトリが含まれている
	 * 場合、このメソッドは変更不可能な抽象パスを返す。なお、ファイルが存在しない抽象パスについては、変更可能と判定する。
	 * @param file	判定するファイルもしくはディレクトリを示す抽象パス
	 * @return	変更不可能なファイルもしくはディレクトリを示す抽象パス。全て変更可能な場合は <tt>null</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static public VirtualFile checkModifiableAllFiles(final VirtualFile file) {
		boolean cando = true;
		
		try {
			if (file.exists()) {
				if (file.isDirectory()) {
					cando = file.canWrite();
					if (cando) {
						VirtualFile[] subfiles = file.listFiles();
						if (subfiles != null && subfiles.length > 0) {
							for (VirtualFile svf : subfiles) {
								VirtualFile umf = checkModifiableAllFiles(svf);
								if (umf != null) {
									return umf;		// unmodifiable
								}
							}
						}
					}
				}
				else {
					cando = isModifiableByLock(file);
				}
			}
		}
		catch (SecurityException ignoreEx) {
			ignoreEx = null;
			cando = false;
		}
		
		return (cando ? null : file);
	}

	/**
	 * <em>source</em> に指定されたファイルもしくはディレクトリを、<em>targetDir</em> ディレクトリ直下に
	 * コピーする。このメソッドでは、<em>source</em> に含まれる全てのファイルもしくはディレクトリをコピーする。
	 * @param source		コピー元の抽象パス
	 * @param targetDir		コピー先の親ディレクトリを示す抽象パス
	 * @return	処理が完了した場合は <tt>true</tt>、中断された場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws Error	<em>targetDir</em> が存在しないかディレクトリではない場合
	 * @throws VirtualFileOperationException	入出力エラーが発生した場合
	 */
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
	
	/**
	 * <em>source</em> に指定されたファイルもしくはディレクトリを、<em>targetDir</em> ディレクトリ直下に
	 * 移動する。このメソッドでは、<em>source</em> に含まれる全てのファイルもしくはディレクトリを移動する。
	 * @param source		移動元の抽象パス
	 * @param targetDir		移動先の親ディレクトリを示す抽象パス
	 * @return	処理が完了した場合は <tt>true</tt>、中断された場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws Error	<em>targetDir</em> が存在しないかディレクトリではない場合
	 * @throws VirtualFileOperationException	入出力エラーが発生した場合
	 */
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

	/**
	 * <em>source</em> に指定された抽象パスが示すファイルもしくはディレクトリを、<em>dest</em> に指定された
	 * 抽象パス名に変更する。<em>dest</em> が存在するファイルもしくはディレクトリであっても、このメソッドは
	 * <em>source</em> のファイル構成に <em>dest</em> の内容を置き換える。置換の際、同じ名前のディレクトリや
	 * フォルダが存在する場合、確認メッセージを表示し、置換を行う。<em>source</em> 側に存在しないファイルや
	 * ディレクトリは基本的に削除される。
	 * @param source	変更する抽象パス名
	 * @param dest		新しい抽象パス名
	 * @return	処理が完了した場合は <tt>true</tt>、中断された場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws Error	<em>dest</em> の親抽象パスが存在しないファイル、もしくはディレクトリではない場合
	 * @throws VirtualFileOperationException	入出力エラーが発生した場合
	 */
	protected boolean forceRenameTo(VirtualFile source, VirtualFile dest) {
		VirtualFile file = dest.getParentFile();
		if (file != null) {
			if (!file.exists())
				throw new Error("Destination parent directory does not exists : \"" + file.getAbsolutePath() + "\"");
			if (!file.isDirectory())
				throw new Error("Destination parent directory is not directory : \"" + file.getAbsolutePath() + "\"");
		}
		
		byte[] buffer = new byte[BUF_SIZE];
		
		if (source.isDirectory()) {
			HashSet<VirtualFile> removeFileBuffer = new HashSet<VirtualFile>();
			return forceRenameToDirectory(null, source, dest, removeFileBuffer, buffer);
		} else {
			return forceRenameToFile(null, source, dest, buffer);
		}
	}
	
	/**
	 * <em>source</em> に指定されたファイルもしくはディレクトリを、<em>targetDir</em> ディレクトリ直下に
	 * 移動する。このメソッドでは、<em>source</em> に含まれる全てのファイルもしくはディレクトリを移動する。
	 * 同じ名前のディレクトリが存在する場合、その内容を置換する。この置換では、<em>source</em> 側に存在しない
	 * ファイルやディレクトリは削除される。
	 * @param source		移動元の抽象パス
	 * @param destParentDir		移動先の親ディレクトリを示す抽象パス
	 * @return	処理が完了した場合は <tt>true</tt>、中断された場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws Error	<em>targetDir</em> が存在しないかディレクトリではない場合
	 * @throws VirtualFileOperationException	入出力エラーが発生した場合
	 */
	protected boolean replaceMoveRecursive(VirtualFile source, VirtualFile destParentDir) {
		if (!destParentDir.exists())
			throw new Error("Destination parent directory does not exists : \"" + destParentDir.getAbsolutePath() + "\"");
		if (!destParentDir.isDirectory())
			throw new Error("Destination parent directory is not directory : \"" + destParentDir.getAbsolutePath() + "\"");
		
		byte[] buffer = new byte[BUF_SIZE];
		VirtualFile dest = destParentDir.getChildFile(source.getName());
		
		if (source.isDirectory()) {
			HashSet<VirtualFile> removeFileBuffer = new HashSet<VirtualFile>();
			return forceRenameToDirectory(null, source, dest, removeFileBuffer, buffer);
		} else {
			return forceRenameToFile(null, source, dest, buffer);
		}
		
	}
	
	/**
	 * <em>source</em> に指定されたファイルもしくはディレクトリを削除する。
	 * このメソッドでは、<em>source</em> に含まれる全てのファイルもしくはディレクトリを削除する。
	 * @param source		削除対象の抽象パス
	 * @return	処理が完了した場合は <tt>true</tt>、中断された場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws Error	<em>targetDir</em> が存在しないかディレクトリではない場合
	 * @throws VirtualFileOperationException	入出力エラーが発生した場合
	 */
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
				fileDeleteExactly(source);
				if (source.exists()) {
					throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, source, null);
				}
			}
		} catch (SecurityException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, source, null, ex);
		}
		
		// completed
		incrementValue();	// increment progress value
		return true;
	}
	
	/**
	 * <em>source</em> に指定されたファイルもしくはディレクトリを削除する。
	 * このメソッドでは、<em>source</em> に含まれる全てのファイルもしくはディレクトリを削除する。
	 * @param source		削除対象の抽象パス
	 * @param ignoreException	例外を無視して処理を続行する場合は <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @return	処理が完了した場合は <tt>true</tt>、中断された場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 * @throws Error	<em>targetDir</em> が存在しないかディレクトリではない場合
	 * @throws VirtualFileOperationException	入出力エラーが発生した場合
	 * @since 2.0.0
	 */
	protected boolean deleteRecursive(VirtualFile source, boolean ignoreException) {
		// check termination
		if (isTerminateRequested())
			return false;	// canceled!

		// delete children
		if (source.isDirectory()) {
			if (ignoreException) {
				// 例外を無視
				boolean existSubFiles = false;
				VirtualFile[] flist = source.listFiles();
				for (VirtualFile toDelete : flist) {
					if (!deleteRecursive(toDelete, ignoreException)) {
						existSubFiles = true;
					}
				}
				if (existSubFiles) {
					return false;	// cannot delete directory
				}
			} else {
				VirtualFile[] flist = source.listFiles();
				for (VirtualFile toDelete : flist) {
					if (!deleteRecursive(toDelete, ignoreException)) {
						return false;	// canceled!
					}
				}
			}
		}
		
		// check termination
		if (isTerminateRequested())
			return false;	// canceled!
		
		// delete source
		try {
			if (source.exists()) {
				fileDeleteExactly(source);
				if (source.exists()) {
					throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, source, null);
				}
			}
		} catch (SecurityException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, source, null, ex);
		}
		
		// completed
		incrementValue();	// increment progress value
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
		
		// check exists source
		if (source.exists()) {
			try {
				fileDeleteExactly(source);
			}
			catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, targetFile, ex);
			}
		}
		
		// update progress
		incrementValue();
		return true;
	}

	private boolean forceRenameToFile(Boolean allowReplace, VirtualFile source, VirtualFile dest, byte[] buffer)
	{
		// check termination
		if (isTerminateRequested())
			return false;	// canceled!
		
		// replace file
		if (dest.exists()) {
			// << exists target file >>
			//--- check replace
			if (allowReplace == null) {
				if (_allowOverwriteAllFiles != null) {
					allowReplace = _allowOverwriteAllFiles;
				} else {
					int ret = showReplaceTargetConfirmDialog(source, dest);
					switch (ret) {
						case YES_ONE_OPTION :
							allowReplace = Boolean.TRUE;
							break;
						case YES_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.TRUE;
							allowReplace = Boolean.TRUE;
							break;
						case NO_ONE_OPTION :
							allowReplace = Boolean.FALSE;
							break;
						case NO_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.FALSE;
							allowReplace = Boolean.FALSE;
							break;
						default :
							return false;
					}
				}
			}
			if (!allowReplace.booleanValue()) {
				// skip source file
				incrementValue();
				return true;
			}
			//--- copy file
			try {
				fileCopy(source, dest, buffer);
			}
			catch (IOException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest, ex);
			}
			catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest, ex);
			}
		}
		else {
			// << does not exists target file >>
			try {
				if (!source.renameTo(dest)) {
					dest.createNewFile();
					fileCopy(source, dest, buffer);
				}
			}
			catch (IOException ex) {
				try {
					dest.delete();
				} catch (Throwable ignore){}
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest, ex);
			}
			catch (SecurityException ex) {
				try {
					dest.delete();
				} catch (Throwable ignore){}
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest, ex);
			}
		}
		//--- completed copy
		
		// check exists source
		if (source.exists()) {
			try {
				fileDeleteExactly(source);
			}
			catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest, ex);
			}
		}
		
		// update progress
		incrementValue();
		return true;
	}
	
	static private String getCopyPrefix(int copyNo) {
		if (copyNo <= 1) {
			return "Copy_of_";
		} else {
			return "Copy_" + Integer.toString(copyNo) + "_of_";
		}
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
			throw new VirtualFileOperationException(VirtualFileOperationException.COPY, source, copyDir);
		}
		else {
			// check overwrite
			if (allowOverwrite == null) {
				if (_allowOverwriteAllFiles != null) {
					allowOverwrite = _allowOverwriteAllFiles;
				} else {
					int ret = showOverwriteDirectoryConfirmDialog(source, copyDir);
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
		}
		//--- completed copy
		incrementValue();
		// check termination
		if (isTerminateRequested()) {
			return false;
		}
		
		// copy children
		VirtualFile[] flist = source.listFiles();
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
		return true;
	}
	
	private boolean moveDirectory(Boolean allowOverwrite, VirtualFile source, VirtualFile targetDir, byte[] buffer)
	{
		// check termination
		if (isTerminateRequested())
			return false;	// canceled!
		
		// copy directory
		VirtualFile createdMoveDir = null;
		VirtualFile moveDir = targetDir.getChildFile(source.getName());
		if (!moveDir.exists()) {
			//--- rename
			boolean renamed = false;
			try {
				renamed = source.renameTo(moveDir);
			} catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, moveDir, ex);
			}
			if (renamed) {
				int numFiles = moveDir.countFiles();
				setValue(getValue() + numFiles);
				return true;
			}
			//--- create directory and copy
			try {
				if (!moveDir.mkdir()) {
					throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, moveDir);
				}
				createdMoveDir = moveDir;
			} catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, moveDir, ex);
			}
		}
		else if (!moveDir.isDirectory()) {
			throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, moveDir);
		}
		else {
			// check overwrite
			if (allowOverwrite == null) {
				if (_allowOverwriteAllFiles != null) {
					allowOverwrite = _allowOverwriteAllFiles;
				} else {
					int ret = showOverwriteDirectoryConfirmDialog(source, moveDir);
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
		}
		// check termination
		if (isTerminateRequested()) {
			try {
				if (createdMoveDir != null && createdMoveDir.exists()) {
					createdMoveDir.delete();
				}
			} catch (Throwable ignoreEx) {}
			return false;
		}
		//--- completed copy
		
		// move children
		VirtualFile[] flist = source.listFiles();
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
		
		// check exists source
		if (source.exists()) {
			try {
				fileDeleteExactly(source);
			}
			catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, moveDir, ex);
			}
		}
		
		// update progress
		incrementValue();
		return true;
	}
	
	private boolean forceRenameToDirectory(Boolean allowReplace, VirtualFile source, VirtualFile dest, Set<VirtualFile> removeFileBuffer, byte[] buffer)
	{
		// check termination
		if (isTerminateRequested())
			return false;	// canceled!
		
		// confirm replace directory
		boolean canRemoveDestDir = true;
		VirtualFile createdDestDir = null;
//		VirtualFile replaceDir = targetDir.getChildFile(source.getName());
		if (dest.exists()) {
			// check replace
			if (allowReplace == null) {
				if (_allowOverwriteAllFiles != null) {
					allowReplace = _allowOverwriteAllFiles;
				} else {
					int ret = showReplaceTargetConfirmDialog(source, dest);
					switch (ret) {
						case YES_ONE_OPTION :
							allowReplace = Boolean.TRUE;
							break;
						case YES_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.TRUE;
							allowReplace = Boolean.TRUE;
							break;
						case NO_ONE_OPTION :
							allowReplace = Boolean.FALSE;
							break;
						case NO_ALL_OPTION :
							_allowOverwriteAllFiles = Boolean.FALSE;
							allowReplace = Boolean.FALSE;
							break;
						default :
							return false;
					}
				}
			}
			if (!allowReplace.booleanValue()) {
				// skip source directory
				int num = source.countFiles();
				setValue(getValue() + num);
				return true;
			}
			
			// is not directory?
			if (!dest.isDirectory()) {
				//--- 移動先がディレクトリではないため、存在する単一ファイルを削除
				try {
					if (!fileDeleteExactly(dest)) {
						throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest);
					}
				}
				catch (SecurityException ex) {
					throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest, ex);
				}
				canRemoveDestDir = false;
			}
		}
		
		// 移動先が存在しない場合の処理
		if (!dest.exists()) {
			//--- rename
			boolean renamed = false;
			try {
				renamed = source.renameTo(dest);
			} catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest, ex);
			}
			if (renamed) {
				int numFiles = dest.countFiles();
				setValue(getValue() + numFiles);
				return true;
			}
			//--- create directory and copy
			try {
				if (!dest.mkdir()) {
					throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest);
				}
				createdDestDir = dest;
			} catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest, ex);
			}
		}
		// check termination
		if (isTerminateRequested()) {
			try {
				if (canRemoveDestDir && createdDestDir != null && createdDestDir.exists()) {
					createdDestDir.delete();
				}
			} catch (Throwable ignoreEx) {}
			return false;
		}
		
		// move children
		VirtualFile[] flist = source.listFiles();
		for (VirtualFile srcChild : flist) {
			VirtualFile destChild = dest.getChildFile(srcChild.getName());
			if (srcChild.isDirectory()) {
				//--- 置換が許可された場合は、ディレクトリ以下の全てのファイルやディレクトリを置換する。
				if (!forceRenameToDirectory(allowReplace, srcChild, destChild, removeFileBuffer, buffer)) {
					return false;	// canceled
				}
			} else {
				if (!forceRenameToFile(allowReplace, srcChild, destChild, buffer)) {
					return false;	// canceled
				}
			}
		}
		//--- 置換されなかったファイルを削除
		removeFileBuffer.clear();
		VirtualFile[] fdestlist = dest.listFiles();
		if (createdDestDir == null && fdestlist != null && fdestlist.length > 0) {
			removeFileBuffer.addAll(Arrays.asList(fdestlist));
			if (flist != null && flist.length > 0) {
				for (VirtualFile svf : flist) {
					VirtualFile remain = dest.getChildFile(svf.getName());
					removeFileBuffer.remove(remain);
				}
			}
			//--- ソースに存在しないファイルは削除
			for (VirtualFile rf : removeFileBuffer) {
				deleteAllFiles(rf, false);	// 例外は発生する
			}
			removeFileBuffer.clear();
		}
		
		//--- 移動元が残っている場合は、削除
		if (source.exists()) {
			try {
				fileDeleteExactly(source);
			}
			catch (SecurityException ex) {
				throw new VirtualFileOperationException(VirtualFileOperationException.MOVE, source, dest, ex);
			}
		}
		
		// update progress
		incrementValue();
		return true;
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

	/**
	 * 指定されたファイルが変更可能かを判定する。
	 * このメソッドでは、指定されたファイルが <code>java.io.File</code> を管理するものの場合のみ、
	 * 排他ロックが取得できるかどうかで変更可能かを判定する。
	 * <code>java.io.File</code> を管理するものではない場合は、<code>canWrite</code> の結果を返す。
	 * <b>注意：</b>
	 * <blockquote>
	 * このメソッドでは、指定されたファイルが存在しており、ディレクトリではないことを前提としている。
	 * この前提を満たさない場合の動作は不定とする。
	 * </blockquote>
	 * <b>注意２：</b>
	 * <blockquote>
	 * JDK 5 では、ファイルが変更可能かを判断する方法がないため、読み込み用としてファイルを開くことで
	 * 暫定的に排他ロックを判定している。
	 * </blockquote>
	 * @param file	判定するファイルの抽象パス
	 * @return	変更可能なら <tt>true</tt>、それ以外の場合は <tt>false</tt>
	 * @throws NullPointerException	引数が <tt>null</tt> の場合
	 */
	static protected boolean isModifiableByLock(VirtualFile file) {
		boolean cando = true;
		
		try {
			cando = file.canWrite();
			if (cando && (file instanceof DefaultFile)) {
				File javafile = ((DefaultFile)file).getJavaFile();
				FileInputStream fis = null;
				//FileChannel fc = null;
				//FileLock flock = null;
				try {
					// Java のファイルに対して、排他ロックが取得できるかどうかで変更可能なファイルかを判定する。
					fis = new FileInputStream(javafile);
					//fc = fis.getChannel();
					//flock = fc.tryLock();	// 排他ロック
				}
				finally {
					//if (flock != null) {
					//	try {
					//		flock.release();
					//	} catch (Throwable ignoreEx) {}
					//	flock = null;
					//}
					//if (fc != null) {
					//	try {
					//		fc.close();
					//	} catch (Throwable ignoreEx) {}
					//	fc = null;
					//}
					if (fis != null) {
						try {
							fis.close();
						} catch (Throwable ignoreEx) {}
						fis = null;
					}
				}
			}
		}
		catch (Throwable ignoreEx) {
			ignoreEx = null;
			cando = false;
		}
		
		return cando;
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static public class UnmodifiableFileException extends RuntimeException
	{
		/** エラー発生時のソース **/
		private final VirtualFile	_sourceFile;

		//------------------------------------------------------------
		// Constructions
		//------------------------------------------------------------
		
		public UnmodifiableFileException(VirtualFile source)
		{
			super();
			this._sourceFile = source;
		}
		
		public UnmodifiableFileException(VirtualFile source, String message)
		{
			super(message);
			this._sourceFile = source;
		}
		
		public UnmodifiableFileException(VirtualFile source, Throwable cause)
		{
			super(cause);
			this._sourceFile = source;
		}
		
		public UnmodifiableFileException(VirtualFile source, String message, Throwable cause)
		{
			super(message, cause);
			this._sourceFile = source;
		}

		//------------------------------------------------------------
		// Public interfaces
		//------------------------------------------------------------
		
		public VirtualFile getSourceFile() {
			return _sourceFile;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getName());
			sb.append(" : source[");
			sb.append(_sourceFile);
			sb.append("]");
			
			String message = getLocalizedMessage();
			if (message != null) {
				sb.append(" : ");
				sb.append(message);
			}
			
			return sb.toString();
		}
	}
}
