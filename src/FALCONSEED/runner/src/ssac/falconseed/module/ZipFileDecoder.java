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
 * @(#)ZipFileDecoder.java	1.10	2011/02/14
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.falconseed.module;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import ssac.util.io.Files;


/**
 * JAVA標準の機能によって、ZIP解凍を行う。
 * 
 * @version 1.10	2011/02/14
 * @since 1.10
 */
public class ZipFileDecoder implements Closeable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 読み込み元となる ZIP ファイル **/
	private final File		_zipFile;
	/** 解凍時に呼び出されるハンドラ **/
	private ZipFileDecodeHandler	_handler;
	/** ZIP ファイル入力ストリーム **/
	private ZipFile	_zipin;
	/** チェックサム判定を有効にするフラグ **/
	private boolean		_enableChecksum = true;
	/** 作成を許可されたディレクトリエントリ **/
	private Map<String, File>	_mapAllowDir;
	/** 作成を拒否されたディレクトリエントリ **/
	private Map<String, File>	_mapDenyDir;

	/** 作業用バッファ **/
	protected byte[] _rbuffer;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------
	
	public ZipFileDecoder(File zipFile, ZipFileDecodeHandler handler) throws ZipException, IOException
	{
		if (zipFile == null)
			throw new NullPointerException("The specified 'zipFile' argument is null.");
		this._zipFile = zipFile;
		this._handler = handler;
		
		// open zip stream
		this._zipin = new ZipFile(zipFile);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	public void close() throws IOException
	{
		if (_zipin != null) {
			_zipin.close();
			_zipin = null;
		}
	}
	
	public boolean isChecksumEnabled() {
		return _enableChecksum;
	}
	
	public void setChecksumEnabled(boolean toEnable) {
		this._enableChecksum = toEnable;
	}
	
	public ZipFileDecodeHandler getHandler() {
		return _handler;
	}
	
	public void setHandler(ZipFileDecodeHandler newHandler) {
		this._handler = newHandler;
	}
	
	public long countTotalCompressedFileSize(String baseEntryName) {
		long totallen = 0L;
		Enumeration<? extends ZipEntry> entries = _zipin.entries();
		if (baseEntryName == null) {
			// for all
			for (;entries.hasMoreElements();) {
				ZipEntry entry = entries.nextElement();
				long len = entry.getCompressedSize();
				if (len < 0L)	return (-1);
				totallen += len;
			}
		} else {
			// for base entry
			for (;entries.hasMoreElements();) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().startsWith(baseEntryName)) {
					// matched
					long len = entry.getCompressedSize();
					if (len < 0L)	return (-1);
					totallen += len;
				}
			}
		}
		return totallen;
	}
	
	public long countTotalDecodedFileSize(String baseEntryName) {
		long totallen = 0L;
		Enumeration<? extends ZipEntry> entries = _zipin.entries();
		if (baseEntryName == null) {
			// for all
			for (;entries.hasMoreElements();) {
				ZipEntry entry = entries.nextElement();
				long len = entry.getSize();
				if (len < 0L)	return (-1);
				totallen += len;
			}
		} else {
			// for base entry
			for (;entries.hasMoreElements();) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().startsWith(baseEntryName)) {
					// matched
					long len = entry.getSize();
					if (len < 0L)	return (-1);
					totallen += len;
				}
			}
		}
		return totallen;
	}
	
	public void decode(File targetPath)
	{
		// check target
		if (!targetPath.exists()) {
			throw new ZipDecodeOperationException(ZipDecodeOperationException.WRITE, null, targetPath);
		}
		if (!targetPath.isDirectory()) {
			throw new ZipDecodeOperationException(ZipDecodeOperationException.WRITE, null, targetPath);
		}
		if (!targetPath.canWrite()) {
			throw new ZipDecodeOperationException(ZipDecodeOperationException.WRITE, null, targetPath);
		}
		
		final ZipFileDecodeHandler handler = getHandler();
		final byte[] buffer = getByteBuffer();
		
		// decode
		Enumeration<? extends ZipEntry> zipEntries = _zipin.entries();
		for (; zipEntries.hasMoreElements(); ) {
			ZipEntry entry = zipEntries.nextElement();
			File fDest = Files.normalizeFile(new File(targetPath, entry.getName()));
			if (entry.isDirectory()) {
				ensureDirectory(entry.getName(), fDest, handler);
			} else {
				decodeFile(_zipin, entry, fDest, handler, buffer);
			}
		}
	}
	
	public File getZipFile() {
		return _zipFile;
	}
	
	public Enumeration<? extends ZipEntry> zipEntries() {
		return _zipin.entries();
	}
	
	static public String getParentEntryName(String entryName) {
		int index;
		if (entryName.endsWith(Files.CommonSeparator)) {
			index = entryName.lastIndexOf(Files.CommonSeparatorChar, entryName.length()-2);
		} else {
			index = entryName.lastIndexOf(Files.CommonSeparatorChar);
		}
		
		if (index >= 0) {
			return entryName.substring(0, index+1);
		} else {
			return null;
		}
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
	
	protected ZipFile getZipFileObject() {
		return _zipin;
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
	
	protected Map<String,File> createDirectoriesMap() {
		return new HashMap<String,File>();
	}
	
	protected Map<String,File> getAllowDirectoriesMap() {
		if (_mapAllowDir == null) {
			_mapAllowDir = createDirectoriesMap();
		}
		return _mapAllowDir;
	}
	
	protected Map<String,File> getDenyDirectoriesMap() {
		if (_mapDenyDir == null) {
			_mapDenyDir = createDirectoriesMap();
		}
		return _mapDenyDir;
	}
	
	protected boolean isAllowDirectoryEntry(String dirEntryName) {
		return getAllowDirectoriesMap().containsKey(dirEntryName);
	}
	
	protected boolean isDenyDirectoryEntry(String dirEntryName) {
		return getDenyDirectoriesMap().containsKey(dirEntryName);
	}
	
	protected boolean isDenyAncestorDirectoryEntry(String dirEntryName, File destDir) {
		Map<String,File> denymap = getDenyDirectoriesMap();
		if (denymap.containsKey(dirEntryName)) {
			return true;
		}
		
		String parentDirEntryName = getParentEntryName(dirEntryName);
		while (parentDirEntryName != null) {
			if (denymap.containsKey(parentDirEntryName)) {
				denymap.put(dirEntryName, destDir);
				return true;
			}
			parentDirEntryName = getParentEntryName(parentDirEntryName);
		}
		
		return false;
	}
	
	protected boolean ensureDirectory(final String dirEntryName, final File destDir, final ZipFileDecodeHandler handler)
	{
		if (isDenyAncestorDirectoryEntry(dirEntryName, destDir)) {
			return false;
		}
		
		if (isAllowDirectoryEntry(dirEntryName)) {
			return true;
		}

		if (handler != null) {
			// check accept
			if (!handler.acceptDecodeDirectory(destDir, dirEntryName)) {
				// deny
				getDenyDirectoriesMap().put(dirEntryName, destDir);
				return false;
			}
		}
		
		// allow ensure directory
		getAllowDirectoriesMap().put(dirEntryName, destDir);
		
		// ensure directory
		if (!destDir.exists()) {
			// create directory
			try {
				if (!destDir.mkdirs()) {
					throw new ZipDecodeOperationException(ZipDecodeOperationException.MKDIR, dirEntryName, destDir);
				}
			} catch (Throwable ex) {
				throw new ZipDecodeOperationException(ZipDecodeOperationException.MKDIR, dirEntryName, destDir, ex);
			}
		}
		else if (!destDir.isDirectory()) {
			// not directory
			throw new ZipDecodeOperationException(ZipDecodeOperationException.MKDIR, dirEntryName, destDir);
		}
		else if (!destDir.canWrite()) {
			// cannot write
			throw new ZipDecodeOperationException(ZipDecodeOperationException.WRITE, dirEntryName, destDir);
		}
		
		// ensured directory
		return true;
	}
	
	protected void decodeFile(final ZipFile zin, final ZipEntry entry, final File destFile,
								final ZipFileDecodeHandler handler, final byte[] buffer)
	{
		String entryName = entry.getName();
		File fParent = destFile.getParentFile();
		String dirEntryName = getParentEntryName(entryName);
		if (dirEntryName != null && !ensureDirectory(dirEntryName, fParent, handler)) {
			if (handler != null) {
				handler.onSkipFileEntry(destFile, entryName, entry);
			}
			return;		// skip
		}
		
		// check accept
		if (handler != null) {
			if (!handler.acceptDecodeFile(destFile, entryName, entry)) {
				handler.onSkipFileEntry(destFile, entryName, entry);
				return;	// skip
			}
		}
		
		// check state
		if (destFile.exists()) {
			if (destFile.isDirectory()) {
				throw new ZipDecodeOperationException(ZipDecodeOperationException.WRITE, entryName, destFile);
			}
			if (!destFile.canWrite()) {
				throw new ZipDecodeOperationException(ZipDecodeOperationException.WRITE, entryName, destFile);
			}
		}
		
		// create temporary
		File temp = null;
		try {
			temp = File.createTempFile(".decomp", ".tmp", fParent);
		} catch (Throwable ex) {
			throw new ZipDecodeOperationException(ZipDecodeOperationException.CREATE, entryName, destFile, ex);
		}
		
		// decompress
		try {
			InputStream is = null;
			OutputStream os = null;
			try {
				// create stream
				if (isChecksumEnabled()) {
					is = new CheckedInputStream(zin.getInputStream(entry), new CRC32());
				} else {
					is = zin.getInputStream(entry);
				}
				os = new BufferedOutputStream(new FileOutputStream(temp));
				
				// decompress by stream
				if (handler != null) {
					// with handler
					long totallen = 0L;
					int len;
					for (;;) {
						len = is.read(buffer);
						if (len < 0) {
							break;
						}
						os.write(buffer, 0, len);
						totallen += len;
						handler.onWritingFile(destFile, entryName, entry, len, totallen);
					}
				} else {
					// without handler
					int len;
					for (;;) {
						len = is.read(buffer);
						if (len < 0) {
							break;
						}
						os.write(buffer, 0, len);
					}
				}
				os.flush();
				
				// judge checksum
				if (isChecksumEnabled()) {
					long validChecksum = entry.getCrc();
					if (validChecksum != (-1)) {
						long readChecksum = ((CheckedInputStream)is).getChecksum().getValue();
						if (readChecksum != validChecksum) {
							throw new ZipIllegalChecksumException(entryName, validChecksum, readChecksum);
						}
					}
				}
			}
			catch (ZipIllegalChecksumException ex) {
				throw ex;
			}
			catch (Throwable ex) {
				throw new ZipDecodeOperationException(ZipDecodeOperationException.WRITE, entryName, destFile, ex);
			}
			finally {
				if (os != null) {
					Files.closeStream(os);
					os = null;
				}
				if (is != null) {
					Files.closeStream(is);
					is = null;
				}
			}
			
			// rename temp to dest
			if (temp.renameTo(destFile)) {
				// succeeded
				temp = null;
				long lm = entry.getTime();
				if (lm >= 0L) {
					destFile.setLastModified(lm);
				}
			} else {
				// failed
				throw new ZipDecodeOperationException(ZipDecodeOperationException.RENAME, entryName, destFile);
			}
		}
		catch (ZipIllegalChecksumException ex) {
			throw ex;
		}
		catch (ZipDecodeOperationException ex) {
			throw ex;
		}
		catch (Throwable ex) {
			throw new ZipDecodeOperationException(ZipDecodeOperationException.RENAME, entryName, destFile, ex);
		}
		finally {
			if (temp != null) {
				try {
					temp.delete();
				} catch (Throwable ex) {}
			}
		}
		
		// completed handle
		if (handler != null) {
			handler.completedDecodeFile(destFile, entryName, entry);
		}
	}

	//------------------------------------------------------------
	// Inner classes
	//------------------------------------------------------------
}
