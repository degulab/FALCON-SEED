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
 * @(#)DefaultFile.java	2.0.0	2012/11/06
 *     - modified by Y.Ishizuka(PieCake.inc,)
 * @(#)DefaultFile.java	1.14	2009/12/09
 *     - created by Y.Ishizuka(PieCake.inc,)
 */
package ssac.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * {@link java.io.File} を実体とする {@link VirtualFile} の実装。
 * 
 * @version 2.0.0	2012/11/06
 * @since 1.14
 */
public class DefaultFile extends AbstractVirtualFile implements Serializable
{
	//------------------------------------------------------------
	// Constants
	//------------------------------------------------------------
	
	private static final long serialVersionUID = -4982454711509257354L;

	//------------------------------------------------------------
	// Fields
	//------------------------------------------------------------

	/** 抽象パスの実体 **/
	private final File	_file;

	//------------------------------------------------------------
	// Constructions
	//------------------------------------------------------------

	/**
	 * 指定された <code>File</code> オブジェクトを格納する
	 * <code>DefaultFile</code> の新しいインスタンスを生成する。
	 * @throws NullPointerException	<em>path</em> が <tt>null</tt> の場合
	 */
	public DefaultFile(File path) {
		if (path==null)
			throw new NullPointerException("'path' argument is null.");
		this._file = path;
	}

	/**
	 * 指定されたパス名から生成される <code>File</code> オブジェクトを格納する
	 * <code>DefaultFile</code> の新しいインスタンスを生成する。
	 * @param pathname	パス名
	 * @throws NullPointerException	<em>pathname</em> が <tt>null</tt> の場合
	 * @see java.io.File#File(String)
	 */
	public DefaultFile(String pathname) {
		this._file = new File(pathname);
	}

	/**
	 * 指定された親抽象パス名および子パス名文字列から生成される <code>File</code> オブジェクトを格納する
	 * <code>DefaultFile</code> の新しいインスタンスを生成する。
	 * @param parent	親抽象パス名
	 * @param child		子パス名文字列
	 * @throws NullPointerException	<em>child</em> が <tt>null</tt> の場合
	 * @see java.io.File#File(String, String)
	 */
	public DefaultFile(String parent, String child) {
		this._file = new File(parent, child);
	}

	/**
	 * 指定された親抽象パスおよび子パス名文字列から生成される <code>File</code> オブジェクトを格納する
	 * <code>DefaultFile</code> の新しいインスタンスを生成する。
	 * @param parent	親抽象パス
	 * @param child		子パス名文字列
	 * @throws NullPointerException	<em>child</em> が <tt>null</tt> の場合
	 * @see java.io.File#File(File, String)
	 */
	public DefaultFile(File parent, String child) {
		this._file = new File(parent, child);
	}

	/**
	 * 指定された親抽象パスおよび子パス名文字列から生成される <code>File</code> オブジェクトを格納する
	 * <code>DefaultFile</code> の新しいインスタンスを生成する。
	 * @param parent	親抽象パス
	 * @param child		子パス名文字列
	 * @throws NullPointerException	<em>child</em> が <tt>null</tt> の場合
	 * @see DefaultFile#getJavaFile()
	 * @see java.io.File#File(File, String)
	 */
	public DefaultFile(DefaultFile parent, String child) {
		File fParent = (parent==null ? null : parent.getJavaFile());
		this._file = new File(fParent, child);
	}

	/**
	 * 指定された <code>file: URI</code> を抽象パス名に変換し生成される <code>File</code> オブジェクトを
	 * 格納する <code>DefaultFile</code> の新しいインスタンスを生成する。
	 * このメソッドで指定する <code>URI</code> は、{@link java.io.File#File(URI)} が
	 * 受け付ける <code>URI</code> でなければならない。
	 * @param uri	階層型の絶対 <code>URI</code>。形式は、&quot;file&quot;、パス、
	 * 				権限、クエリー、フラグメント。パスは必ず指定する。権限、クエリー、
	 * 				およびフラグメントは定義しない。
	 * @throws NullPointerException	<em>uri</em> が <tt>null</tt> の場合
	 * @throws IllegalArgumentException	<em>uri</em> のパラメータの前提条件が満たされていない場合
	 * @see java.io.File#File(URI)
	 */
	public DefaultFile(URI uri) {
		this._file = new File(uri);
	}

	//------------------------------------------------------------
	// Public interfaces
	//------------------------------------------------------------

	/**
	 * このオブジェクトに格納されている <code>File</code> オブジェクトを返す。
	 */
	public File getJavaFile() {
		return _file;
	}

	//------------------------------------------------------------
	// Implements VirtualFile interfaces
	//------------------------------------------------------------

	public DefaultFileFactory getFactory() {
		return DefaultFileFactory.getInstance();
	}
	
	public FileInputStream getInputStream() throws FileNotFoundException
	{
		return new FileInputStream(_file);
	}
	
	public FileOutputStream getOutputStream() throws FileNotFoundException
	{
		return new FileOutputStream(_file);
	}
	
	public FileOutputStream getOutputStream(boolean append) throws FileNotFoundException
	{
		return new FileOutputStream(_file, append);
	}
	
	public boolean isDescendingFrom(VirtualFile ancestor) {
		if (!(ancestor instanceof DefaultFile))
			return false;	// different type
		
		return Files.isDescendingFrom(_file, ((DefaultFile)ancestor)._file);
	}
	
	public String relativePathFrom(VirtualFile basepath) {
		return relativePathFrom(basepath, File.separatorChar);
	}
	
	public String relativePathFrom(VirtualFile basepath, char separator) {
		if (!(basepath instanceof DefaultFile))
			throw new IllegalArgumentException("'basepath' does not DefaultFile class : " + basepath.getClass().getName());
		return Files.convertAbsoluteToRelativePath(((DefaultFile)basepath)._file, _file, separator);
	}
	
	public DefaultFile relativeFileFrom(VirtualFile basepath) {
		return relativeFileFrom(basepath, File.separatorChar);
	}
	
	public DefaultFile relativeFileFrom(VirtualFile basepath, char separator) {
		if (!(basepath instanceof DefaultFile))
			throw new IllegalArgumentException("'basepath' does not DefaultFile class : " + basepath.getClass().getName());
		String relpath = Files.convertAbsoluteToRelativePath(((DefaultFile)basepath)._file, _file, separator);
		return getFactory().newFile(relpath);
	}
	
	public String getName() {
		return _file.getName();
	}
	
	public String getPath() {
		return _file.getPath();
	}
	
	public String getParentPath() {
		return _file.getParent();
	}
	
	public String getAbsolutePath() {
		return _file.getAbsolutePath();
	}
	
	public String getNormalizedPath() {
		return getNormalizedPath(File.separatorChar);
	}
	
	public String getNormalizedPath(char separator) {
		return Files.normalizePath(_file.getPath(), separator);
	}
	
	public VirtualFile getNormalizedFile() {
		return getNormalizedFile(File.separatorChar);
	}
	
	public VirtualFile getNormalizedFile(char separator) {
		return getFactory().newFile(Files.normalizePath(_file.getPath(), separator));
	}
	
	public URL toURL() throws MalformedURLException {
		//--- 2012.11.06 : java.io.File#toURL() had been deprecated
		return _file.toURI().toURL();
	}
	
	public URI toURI() {
		return _file.toURI();
	}
	
	public boolean isAbsolute() {
		return _file.isAbsolute();
	}
	
	public boolean isNormalized() {
		return Files.isNormalized(_file);
	}
	
	public boolean canRead() {
		return _file.canRead();
	}
	
	public boolean canWrite() {
		return _file.canWrite();
	}

	public boolean isDirectory() {
		return _file.isDirectory();
	}
	
	public boolean isFile() {
		return _file.isFile();
	}
	
	public boolean isHidden() {
		return _file.isHidden();
	}
	
	public boolean exists() {
		return _file.exists();
	}
	
	public long length() {
		return _file.length();
	}
	
	public long lastModified() {
		return _file.lastModified();
	}
	
	public boolean setLastModified(long time) {
		return _file.setLastModified(time);
	}
	
	public boolean setReadOnly() {
		return _file.setReadOnly();
	}
	
	public boolean createNewFile() throws IOException {
		return _file.createNewFile();
	}
	
	public boolean mkdir() {
		return _file.mkdir();
	}
	
	public boolean mkdirs() {
		return _file.mkdirs();
	}
	
	public boolean delete() {
		return _file.delete();
	}
	
	public boolean renameTo(VirtualFile dest) {
		return _file.renameTo(((DefaultFile)dest)._file);
	}
	
	public String[] list() {
		return _file.list();
	}
	
	public DefaultFile[] listFiles() {
		String[] names = list();
		if (names == null)	return null;
		int len = names.length;
		DefaultFile[] files = new DefaultFile[len];
		for (int i = 0; i < len; i++) {
			files[i] = getFactory().newFile(this, names[i]);
		}
		return files;
	}

	public DefaultFile[] listFiles(VirtualFilenameFilter filter) {
		String[] names = list();
		if (names == null)	return null;
		int len = names.length;
		ArrayList<DefaultFile> files = new ArrayList<DefaultFile>(len);
		for (int i = 0; i < len; i++) {
			if ((filter==null) || filter.accept(this, names[i])) {
				files.add(getFactory().newFile(this, names[i]));
			}
		}
		return files.toArray(new DefaultFile[files.size()]);
	}

	public DefaultFile[] listFiles(VirtualFileFilter filter) {
		String[] names = list();
		if (names == null)	return null;
		int len = names.length;
		ArrayList<DefaultFile> files = new ArrayList<DefaultFile>(len);
		for (int i = 0; i < len; i++) {
			DefaultFile vf = getFactory().newFile(this, names[i]);
			if ((filter==null) || filter.accept(vf)) {
				files.add(vf);
			}
		}
		return files.toArray(new DefaultFile[files.size()]);
	}
	
	public int compareTo(VirtualFile pathname) {
		return _file.compareTo(((DefaultFile)pathname)._file);
	}

	public int hashCode() {
		return _file.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj instanceof DefaultFile) {
			return this._file.equals(((DefaultFile)obj)._file);
		}
		
		return false;
	}

	//------------------------------------------------------------
	// Internal methods
	//------------------------------------------------------------
}
