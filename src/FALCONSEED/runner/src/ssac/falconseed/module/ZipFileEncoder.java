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
 * @(#)ZipFileEncoder.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ssac.util.io.Files;

/**
 * JAVA標準の機能によって、ZIP圧縮を行う。
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class ZipFileEncoder implements Closeable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 出力先となる ZIP ファイル **/
	private final File		_zipFile;
	/** 圧縮対象ファイルの基準パス **/
	private final File		_basePath;
	/** 圧縮対象ファイルのフィルタ **/
	private FileFilter		_filter;
	/** 圧縮時に呼び出されるハンドラ **/
	private ZipFileEncodeHandler	_handler;
	/** ZIPファイル出力ストリーム **/
	private ZipOutputStream	_zipout;

	/** 作業用バッファ **/
	protected byte[] _rbuffer;
	
	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ZipFileEncoder(File zipFile, File basePath, ZipFileEncodeHandler handler)
	throws FileNotFoundException
	{
		// check
		if (zipFile == null)
			throw new NullPointerException("The specified 'zipFile' argument is null.");
		if (basePath == null)
			throw new NullPointerException("The specified 'basePath' argument is null.");
		this._zipFile = zipFile;
		this._basePath = basePath.getAbsoluteFile();
		this._handler = handler;
		
		// open zip stream
		this._zipout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	public void close() throws IOException
	{
		if (_zipout != null) {
			_zipout.close();
			_zipout = null;
		}
	}
	
	public ZipFileEncodeHandler getHandler() {
		return _handler;
	}
	
	public void setHandler(ZipFileEncodeHandler newHandler) {
		this._handler = newHandler;
	}
	
	public FileFilter getFileFilter() {
		return _filter;
	}
	
	public void setFileFilter(FileFilter newFilter) {
		this._filter = newFilter;
	}

	/**
	 * このオブジェクトに設定されているファイルフィルタを適用して、
	 * 指定された抽象パスに含まれる全てのファイルサイズの合計を取得する。
	 * このメソッドは、ディレクトリに含まれるファイルも再帰的に集計する。
	 * @param targetFiles	対象の抽象パスを格納する配列
	 * @return	ファイルサイズの合計を返す。
	 */
	public long countTotalFileSize(File...targetFiles)
	{
		final FileFilter filter = getFileFilter();
		
		long len = 0L;
		if (targetFiles != null) {
			if (filter != null) {
				for (File file : targetFiles) {
					if (filter.accept(file)) {
						len += countTotalFileSizeRecursive(file, filter);
					}
				}
			} else {
				for (File file : targetFiles) {
					len += countTotalFileSizeRecursive(file, filter);
				}
			}
		}
		return len;
	}
	
	public void encode(File...targetFiles) throws IOException
	{
		final ZipFileEncodeHandler handler = getHandler();
		final FileFilter filter = getFileFilter();
		final byte[] buffer = getByteBuffer();

		if (filter != null) {
			for (File file : targetFiles) {
				if (filter.accept(file)) {
					encodeRecursive(_zipout, file, filter, handler, buffer);
				}
			}
		} else {
			for (File file : targetFiles) {
				encodeRecursive(_zipout, file, filter, handler, buffer);
			}
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected File getZipFile() {
		return _zipFile;
	}
	
	protected File getBasePath() {
		return _basePath;
	}
	
	protected byte[] createByteBuffer() {
		return new byte[8192];
	}
	
	protected byte[] getByteBuffer() {
		if (_rbuffer == null) {
			_rbuffer = createByteBuffer();
		}
		return _rbuffer;
	}
	
	protected long countTotalFileSizeRecursive(final File file, final FileFilter filter)
	{
		if (file.isDirectory()) {
			// for Directory
			long len = 0L;
			File[] files;
			if (filter != null) {
				files = file.listFiles(filter);
			} else {
				files = file.listFiles();
			}
			if (files != null && files.length > 0) {
				for (File child : files) {
					len += countTotalFileSizeRecursive(child, filter);
				}
			}
			return len;
		}
		else {
			// for File
			return file.length();
		}
	}
	
	protected void encodeRecursive(final ZipOutputStream zos, final File targetFile, final FileFilter filter,
									final ZipFileEncodeHandler handler, final byte[] buffer)
	throws IOException
	{
		String relPath = getRelativePath(targetFile);
		
		if (targetFile.isDirectory()) {
			// create directory entry
			if (handler != null) {
				// with handler
				if (!handler.acceptEncodeDirectory(targetFile, relPath))
					return;		// skip directory
				encodeDirectory(zos, targetFile, relPath, handler);
				handler.completedEncodeDirectory(targetFile, relPath);
			} else {
				// without handler
				encodeDirectory(zos, targetFile, relPath, handler);
			}
			
			// create file entries in directory
			File[] files;
			if (filter != null) {
				files = targetFile.listFiles(filter);
			} else {
				files = targetFile.listFiles();
			}
			if (files != null && files.length > 0) {
				for (File f : files) {
					encodeRecursive(zos, f, filter, handler, buffer);
				}
			}
		}
		else {
			// create file entry
			if (handler != null) {
				if (!handler.acceptEncodeFile(targetFile, relPath))
					return;		// skip file
				encodeFile(zos, targetFile, relPath, handler, buffer);
				handler.completedEncodeFile(targetFile, relPath);
			} else {
				// without handler
				encodeFile(zos, targetFile, relPath, handler, buffer);
			}
		}
	}
	
	protected void encodeDirectory(final ZipOutputStream zos, final File targetDir,
									final String relativePath, final ZipFileEncodeHandler handler)
	throws IOException
	{
		ZipEntry entry = new ZipEntry(relativePath + Files.CommonSeparator);
		entry.setSize(0L);
		entry.setTime(targetDir.lastModified());
		zos.putNextEntry(entry);
		zos.closeEntry();
	}
	
	protected void encodeFile(final ZipOutputStream zos, final File targetFile, final String relativePath,
								final ZipFileEncodeHandler handler, final byte[] buffer)
	throws IOException
	{
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(targetFile));
			
			ZipEntry entry = new ZipEntry(relativePath);
			entry.setTime(targetFile.lastModified());
			zos.putNextEntry(entry);
			
			if (handler != null) {
				// with handler
				long totalLen = 0L;
				int len;
				for(;;) {
					len = bis.read(buffer);
					if (len < 0) {
						break;	// EOF
					}
					zos.write(buffer, 0, len);
					totalLen += len;
					handler.onWritingFile(targetFile, relativePath, len, totalLen);
				}
			}
			else {
				// without handler
				int len;
				for(;;) {
					len = bis.read(buffer);
					if (len < 0) {
						break;	// EOF
					}
					zos.write(buffer, 0, len);
				}
			}
			zos.closeEntry();
		}
		finally {
			if (bis != null) {
				Files.closeStream(bis);
				bis = null;
			}
		}
	}
	
	protected String getRelativePath(File file) {
		return Files.convertAbsoluteToRelativePath(_basePath, file, Files.CommonSeparatorChar);
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
	
	static public class DefaultFileFilter implements FileFilter
	{
		static private final String[] exclusiveDirs = {".svn", "_svn"};
		static private final String[] exclusiveFiles = {".DS_Store", ".localized"};
		
		public boolean accept(File pathname) {
			String name = pathname.getName();
			if (pathname.isDirectory()) {
				// ディレクトリ名による除外
				for (String exclusive : exclusiveDirs) {
					if (exclusive.equalsIgnoreCase(name)) {
						return false;
					}
				}
			}
			else {
				// ファイル名による除外
				for (String exclusive : exclusiveFiles) {
					if (exclusive.equalsIgnoreCase(name)) {
						return false;
					}
				}
			}
			
			// allow
			return true;
		}
	}
}
