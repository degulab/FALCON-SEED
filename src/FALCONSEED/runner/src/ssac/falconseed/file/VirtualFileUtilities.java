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
 *  Copyright 2007-2015  SSAC(Systems of Social Accounting Consortium)
 *  <author> Yasunari Ishizuka (PieCake,Inc.)
 *  <author> Hiroshi Deguchi (TOKYO INSTITUTE OF TECHNOLOGY)
 *  <author> Yuji Onuki (Statistics Bureau)
 *  <author> Shungo Sakaki (Tokyo University of Technology)
 *  <author> Akira Sasaki (HOSEI UNIVERSITY)
 *  <author> Hideki Tanuma (TOKYO INSTITUTE OF TECHNOLOGY)
 */
/*
 * @(#)VirtualFileUtilities.java	3.2.0	2015/06/14
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)VirtualFileUtilities.java	1.20	2012/03/08
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ssac.util.io.Files;
import ssac.util.io.VirtualFile;
import ssac.util.io.VirtualFileFilter;
import ssac.util.io.VirtualFileOperationException;
import ssac.util.io.VirtualFilenameFilter;

/**
 * <code>VirtualFile</code> オブジェクトを操作するためのユーティリティ。
 * 
 * @version 3.2.0
 * @since 1.20
 */
public final class VirtualFileUtilities
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	static protected int BUF_SIZE = 50000;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	private VirtualFileUtilities(){}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * <em>vfFromDir</em> 以下のパスの場合に、<em>vfToDir</em> 以下のパスに変換する。
	 * <em>vfFromDir</em> もしくは <em>vfToDir</em> のどちらかが <tt>null</tt> の場合、
	 * <em>vfToDir</em> 以下の場合、<em>vfFromDir</em> 以下ではない場合、パスは変換されない。
	 * @param target		変換対象のパス
	 * @param vfFromDir		変換元の基準パス
	 * @param vfToDir		変換先の基準パス
	 * @return	変換後のパス
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 * @since 3.2.0
	 */
	static public VirtualFile convertBasePath(VirtualFile target, VirtualFile vfFromDir, VirtualFile vfToDir) {
		VirtualFile vfResult = target;
		if (vfFromDir != null && vfToDir != null) {
			if (!target.isDescendingFrom(vfToDir) && !target.isDescendingFrom(vfFromDir)) {
				VirtualFile vf = target.relativeFileFrom(vfFromDir);
				if (!vf.isAbsolute()) {
					vfResult = vfToDir.getChildFile(vf.getPath());
				}
			}
		}
		return vfResult;
	}

	/**
	 * <em>target</em> が、<em>path1</em> もしくは <em>path2</em> のどちらかに格納されているかを判定する。
	 * @param target	判定対象のパス
	 * @param path1		基準となるパス
	 * @param path2		基準となるパス
	 * @return	どちらかのパス配下のものであれば <tt>true</tt>、そうでない場合は <tt>false</tt>
	 * @throws NullPointerException	<em>target</em> が <tt>null</tt> の場合
	 * @since 3.2.0
	 */
	static public boolean isDescendingFrom(VirtualFile target, VirtualFile path1, VirtualFile path2) {
		if (path1 != null) {
			if (target.isDescendingFrom(path1)) {
				return true;
			}
		}
		
		if (path2 != null) {
			if (target.isDescendingFrom(path2)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * ファイルコピー時の同名ファイルに付加するプレフィックスを取得する。
	 * @param copyNo	プレフィックスに付加する番号、1 以下の場合は付加されない
	 * @return	プレフィックスとなる文字列
	 */
	static public String getCopyPrefix(int copyNo) {
		if (copyNo <= 1) {
			return "Copy_of_";
		} else {
			return "Copy_" + Integer.toString(copyNo) + "_of_";
		}
	}

	/**
	 * 指定された抽象パスのバックアップ抽象パスを生成する。
	 * バックアップ抽象パスは、指定された抽象パスのファイル名の先頭に、
	 * &quot;.~$&quot; を付加したものとする。このファイル名がすでに
	 * 存在している場合は、&quot;~$&quot; を複数付加する。
	 * @param source	元の抽象パス
	 * @return	ファイルが存在しないバックアップ抽象パス
	 */
	static public VirtualFile getBackupFile(VirtualFile source) {
		return getBackupFile("~$", source);
	}

	/**
	 * 指定された抽象パスのバックアップ抽象パスを生成する。
	 * バックアップ抽象パスは、指定された抽象パスのフィアル名の先頭に、
	 * &quot;.&quot; と <em>prefix</em> に指定された文字列を付加したものとする。
	 * このファイル名がすでに存在している場合は、<em>prefix</em> を複数付加する。
	 * @param prefix	付加する文字列(2文字以上)
	 * @param source	元の抽象パス
	 * @return	ファイルが存在しないバックアプ抽象パス
	 * @throws IllegalArgumentException	<em>prefix</em> が 2文字以上ではない場合、
	 * 									<em>source</em> が親を持たない場合
	 */
	static public VirtualFile getBackupFile(String prefix, VirtualFile source) {
		if (prefix.length() < 2)
			throw new IllegalArgumentException("Illegal prefix : \"" + prefix + "\"");

		VirtualFile parent = source.getParentFile();
		StringBuilder sb = new StringBuilder();
		sb.append(source.getName());
		VirtualFile bfile = null;
		do {
			sb.insert(0, prefix);
			bfile = parent.getChildFile(".".concat(sb.toString()));
		} while (!bfile.exists());
		
		return bfile;
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

	/**
	 * 指定されたディレクトリに含まれる全てのファイルとディレクトリを削除する。
	 * このメソッドは、指定されたディレクトリは削除しない。指定されたファイルの場合、このメソッドは何もしない。
	 * なお、このメソッドの進捗状況通知においては、ファイルもしくはディレクトリ 1 つを削除したとき、値を 1 加算する。
	 * @param file		処理対象のディレクトリを示す抽象パス
	 * @param handler	進捗状況通知ハンドラ
	 * @return	処理が完了した場合は <tt>true</tt>、キャンセルされた場合は <tt>false</tt>
	 * @throws VirtualFileOperationException	ファイルの操作に異常が発生した場合
	 */
	static public boolean deleteSubFiles(VirtualFile file, FileProgressHandler handler)
	throws VirtualFileOperationException
	{
		if (handler.isTerminateRequested())
			return false;
		
		if (file.isDirectory()) {
			VirtualFile[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (VirtualFile subfile : files) {
					if (!deleteFiles(subfile, handler)) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 指定されたファイル、もしくはディレクトリを削除する。ディレクトリの場合、そのディレクトリに含まれる
	 * すべてのファイルとディレクトリを削除する。
	 * なお、このメソッドの進捗状況通知においては、ファイルもしくはディレクトリ 1 つを削除したとき、値を 1 加算する。
	 * @param file		削除対象のファイルを示す抽象パス
	 * @param handler	進捗状況通知ハンドラ
	 * @return	処理が完了した場合は <tt>true</tt>、キャンセルされた場合は <tt>false</tt>
	 * @throws NullPointerException	<em>file</em> もしくは <em>handlre</em> が <tt>null</tt> の場合
	 * @throws VirtualFileOperationException	ファイルの操作に異常が発生した場合
	 */
	static public boolean deleteFiles(VirtualFile file, FileProgressHandler handler)
	throws VirtualFileOperationException
	{
		if (!deleteSubFiles(file, handler) || handler.isTerminateRequested()) {
			return false;
		}
		
		try {
			file.delete();
			if (file.exists()) {
				throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, file, null);
			}
		} catch (SecurityException ex) {
			throw new VirtualFileOperationException(VirtualFileOperationException.DELETE, file, null, ex);
		}
		handler.addProgressValue(1L);
		return true;
	}

	/**
	 * 指定されたソースファイルを指定された位置へ移動する。ディレクトリは不可。
	 * ディレクトリかどうかや読み込み書き込みが許可されているかどうかなどはチェックしない。
	 * なお、このメソッドの進捗状況通知においては、転送されたバイト数を加算する。
	 * 進捗状況通知ハンドラの処理中断要求は受け付けない。
	 * @param source
	 * @param dest
	 * @param buffer
	 * @param handler
	 * @return
	 */
	static public long moveFileDenyCancel(VirtualFile source, VirtualFile dest, byte[] buffer, FileProgressHandler handler)
	throws IOException
	{
		boolean destExists = false;
		VirtualFile ftmp = null;
		if (dest.exists()) {
			assert !dest.isDirectory();
			destExists = true;
			ftmp = getBackupFile(dest);
			if (!dest.renameTo(ftmp)) {
				ftmp = null;
				try {
					dest.delete();
				} catch (Throwable ignoreEx) {};
			}
		}
		
		boolean moved = false;
		try {
			if (!source.renameTo(dest)) {
				//--- コピー
				copyFileDenyCancel(source, dest, buffer, handler);
				//--- ソース削除
				source.delete();
			}
			moved = true;
			return dest.length();
		}
		finally {
			try {
				if (moved) {
					// 移動成功
					if (ftmp != null) {
						ftmp.delete();
					}
				} else {
					// 移動失敗
					if (ftmp != null) {
						dest.delete();
						ftmp.renameTo(dest);
					}
					else if (!destExists) {
						dest.delete();
					}
				}
			} catch (Throwable ignoreEx) {}
		}
	}

	/**
	 * 指定されたソースファイルを指定された位置へコピーする。ディレクトリは不可。
	 * ディレクトリかどうかや読み込み書き込みが許可されているかどうかなどはチェックしない。
	 * なお、このメソッドの進捗状況通知においては、転送されたバイト数を加算する。
	 * 進捗状況通知ハンドラの処理中断要求は受け付けない。
	 * @param source	コピー元のファイル
	 * @param dest		コピー先のファイル
	 * @param buffer	転送に使用するバッファ
	 * @param handler	進捗状況通知ハンドラ
	 * @return	コピーしたバイト数
	 * @throws IOException	入出力エラーが発生した場合
	 */
	static public long copyFileDenyCancel(VirtualFile source, VirtualFile dest, byte[] buffer, FileProgressHandler handler)
	throws IOException
	{
		InputStream fis = null;
		OutputStream fos = null;
		long ret = 0L;
		try {
			fis = source.getInputStream();
			fos = dest.getOutputStream();
			ret = copyDenyCancel(fis, fos, buffer, handler);
			//--- set attribute by java.io.File
			if (!source.canWrite())
				dest.setReadOnly();
			dest.setLastModified(source.lastModified());
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
				dest.setReadOnly();
			}
		} catch (Throwable ignoreEx) {}
		try {
			dest.setLastModified(source.lastModified());
		} catch (Throwable ignoreEx) {}
		
		return ret;
		
	}

	/**
	 * 指定された入力ストリームから、指定された出力ストリームにデータをコピーする。
	 * なお、このメソッドの進捗状況通知においては、転送されたバイト数を加算する。
	 * 進捗状況通知ハンドラの処理中断要求は受け付けない。
	 * @param inStream		入力ストリーム
	 * @param outStream		出力ストリーム
	 * @param buffer		転送に使用するバッファ
	 * @param handler		<tt>null</tt> ではない進捗状況通知ハンドラ
	 * @return	転送された総バイト数
	 * @throws IOException	入出力エラーが発生した場合
	 */
	static public long copyDenyCancel(InputStream inStream, OutputStream outStream, byte[] buffer, FileProgressHandler handler)
	throws IOException
	{
		long bytesCopied = 0;
		int read = -1;
		
		while ((read = inStream.read(buffer, 0, buffer.length)) != -1) {
			outStream.write(buffer, 0, read);
			handler.addProgressValue(read);
			bytesCopied += read;
		}

		return bytesCopied;
	}
	
	/**
	 * 指定された入力ストリームから、指定された出力ストリームにデータをコピーする。
	 * なお、このメソッドの進捗状況通知においては、転送されたバイト数を加算する。
	 * キャンセルされた場合は、(-1) を返す。
	 * <b>注意：</b>
	 * <blockquote>
	 * コピー途中でキャンセルした場合、出力されたストリームは完全ではない。
	 * </blockquote>
	 * @param inStream		入力ストリーム
	 * @param outStream		出力ストリーム
	 * @param buffer		転送に使用するバッファ
	 * @param handler		<tt>null</tt> ではない進捗状況通知ハンドラ
	 * @return	転送された総バイト数、キャンセルされた場合は (-1)
	 * @throws IOException	入出力エラーが発生した場合
	 */
	static public long copyAllowCancel(InputStream inStream, OutputStream outStream, byte[] buffer, FileProgressHandler handler)
	throws IOException
	{
		long bytesCopied = 0;
		int read = -1;
		
		while ((read = inStream.read(buffer, 0, buffer.length)) != -1) {
			outStream.write(buffer, 0, read);
			handler.addProgressValue(read);
			if (handler.isTerminateRequested()) {
				bytesCopied = (-1L);
				break;
			}
			bytesCopied += read;
		}

		return bytesCopied;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
