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
 * @(#)FileUtil.java	1.00	2007/11/29
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.aadlc.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * ファイル操作ユーティリティ
 * 
 * @version 1.00	2007/11/29
 */
public class FileUtil {
	//------------------------------------------------------------
	// Definitions
	//------------------------------------------------------------
	
	private static final String UP_DIR_PATH_WINDOWS = "..\\";
	private static final String UP_DIR_PATH_LINUX   = "../";
	private static final String UP_DIR_PATH_NATIVE  = ".." + File.separator;
	private static final String CUR_DIR_PATH_WINDOWS = ".\\";
	private static final String CUR_DIR_PATH_LINUX   = "./";
	
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------
	
	static public String removeStartsFileSeparator(String targetPath) {
		final String WindowsFileSeparator = "\\";
		final String LinuxFileSeparator = "/";
		
		if (targetPath != null) {
			if (targetPath.startsWith(WindowsFileSeparator)) {
				return targetPath.substring(WindowsFileSeparator.length());
			}
			else if (targetPath.startsWith(LinuxFileSeparator)) {
				return targetPath.substring(LinuxFileSeparator.length());
			}
			else {
				return targetPath;
			}
		}
		else {
			return targetPath;
		}
	}
	
	static public File getFileByRelativePath(File baseDir, String relativePath) {
		assert relativePath != null;

		// 基準ディレクトリが指定されていない場合は、カレントディレクトリを基準パスとする
		if (baseDir == null) {
			return new File(relativePath);
		}
		
		// 自身のディレクトリを示す文字列は消去
		String strTargetPath;
		if (relativePath.startsWith(CUR_DIR_PATH_WINDOWS)) {
			strTargetPath = relativePath.substring(CUR_DIR_PATH_WINDOWS.length());
		}
		else if (relativePath.startsWith(CUR_DIR_PATH_LINUX)) {
			strTargetPath = relativePath.substring(CUR_DIR_PATH_LINUX.length());
		}
		else {
			strTargetPath = relativePath;
		}
		
		// パスを移動
		File parentDir = baseDir;
		while (parentDir != null) {
			if (strTargetPath.startsWith(UP_DIR_PATH_WINDOWS)) {
				strTargetPath = strTargetPath.substring(UP_DIR_PATH_WINDOWS.length());
				parentDir = parentDir.getParentFile();
			}
			else if (strTargetPath.startsWith(UP_DIR_PATH_LINUX)) {
				strTargetPath = strTargetPath.substring(UP_DIR_PATH_LINUX.length());
				parentDir = parentDir.getParentFile();
			}
			else {
				// no move directory
				break;
			}
		}

		// ファイルパス決定
		File retFile;
		if (parentDir != null) {
			retFile = new File(parentDir, strTargetPath);
		} else {
			retFile = new File(relativePath);
		}
		
		return retFile;
	}
	
	static public String getRelativePath(File baseDir, File targetFile) {
		assert targetFile != null;

		// 基準ディレクトリが指定されていない場合は、相対パスは存在しない
		if (baseDir == null) {
			return null;
		}
		
		// 対象パスに親ディレクトリ指定文字列が含まれている場合は、相対パスは生成しない
		if (targetFile.getAbsolutePath().indexOf(UP_DIR_PATH_WINDOWS) >= 0) {
			return null;
		}
		else if (targetFile.getAbsolutePath().indexOf(UP_DIR_PATH_LINUX) >= 0) {
			return null;
		}

		// ディレクトリ情報を取得
		String backDir = "";
		String targetPath = targetFile.getAbsolutePath();
		File parentDir = baseDir;
		while (parentDir != null) {
			String parentPath = parentDir.getAbsolutePath();
			if (targetPath.startsWith(parentPath)) {
				String relPath = targetPath.substring(parentPath.length());
				relPath = removeStartsFileSeparator(relPath);
				relPath = backDir + relPath;
				//--- 相対パス検出
				return relPath;
			}
			//--- next
			backDir += UP_DIR_PATH_NATIVE;
			parentDir = parentDir.getParentFile();
		}
		
		// 相対パスは存在しない
		return null;
	}

	/**
	 * 指定のストリームをクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param iStream クローズする <code>InputStream</code> オブジェクト
	 */
	static public void closeStream(InputStream iStream) {
		if (iStream != null) {
			try {
				iStream.close();
			}
			catch (IOException ex) {}
		}
	}

	/**
	 * 指定の <code>Reader</code> をクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param iReader クローズする <code>Reader</code> オブジェクト
	 */
	static public void closeStream(Reader iReader) {
		if (iReader != null) {
			try {
				iReader.close();
			}
			catch (IOException ex) {}
		}
	}

	/**
	 * 指定のストリームをクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param oStream クローズする <code>OutputStream</code> オブジェクト
	 */
	static public void closeStream(OutputStream oStream) {
		if (oStream != null) {
			try {
				oStream.close();
			}
			catch (IOException ex) {}
		}
	}

	/**
	 * 指定の <code>Writer</code> をクローズする。
	 * <br>
	 * このメソッドは例外を発生させない。
	 * 
	 * @param oWriter クローズする <code>Writer</code> オブジェクト
	 */
	static public void closeStream(Writer oWriter) {
		if (oWriter != null) {
			try {
				oWriter.close();
			}
			catch (IOException ex) {}
		}
	}
	
	public static File ensureDirectory(String path)
		throws IOException
	{
		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {
				String msg = "the specified directory is a file";
				throw new IOException(msg);
			} else if (file.isDirectory()) {
				return file;
			} else {
				String msg = "the specified directory is not known type";
				throw new IOException(msg);
			}
		} else {
			if (file.mkdir()) {
				return file;
			} else {
				String msg = "failed to create the specified directory";
				throw new IOException(msg);
			}
		}
	}
	
	public static void ensureDirectory(File file)
		throws IOException
	{
		if (file.exists()) {
			if (file.isFile()) {
				String msg = "the specified directory=" + file.getAbsolutePath()
						+ " is a file";
				throw new IOException(msg);
			} else if (file.isDirectory()) {
				return;
			} else {
				String msg = "the specified directory=" + file.getAbsolutePath()
						+ " is not known type";
				throw new IOException(msg);
			}
		} else {
			if (!file.mkdirs()) {
				String msg = "failed to create the specified directory="
						+ file.getAbsolutePath();
				throw new IOException(msg);
			}
		}
	}
}
